/*
 * Copyright (c) 2023 Airbyte, Inc., all rights reserved.
 */

package io.airbyte.integrations.destination.bigquery;

import static io.airbyte.integrations.base.JavaBaseConstants.AIRBYTE_NAMESPACE_SCHEMA;
import static io.airbyte.integrations.destination.bigquery.BigQueryRecordConsumer.OVERWRITE_TABLE_SUFFIX;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.cloud.bigquery.StandardSQLTypeName;
import com.google.cloud.bigquery.TableDefinition;
import com.google.cloud.bigquery.TableId;
import com.google.common.base.Functions;
import com.google.common.base.Preconditions;
import io.airbyte.commons.json.Jsons;
import io.airbyte.commons.text.Names;
import io.airbyte.integrations.base.AirbyteMessageConsumer;
import io.airbyte.integrations.destination.bigquery.formatter.BigQueryRecordFormatter;
import io.airbyte.integrations.destination.bigquery.typing_deduping.BigQueryDestinationHandler;
import io.airbyte.integrations.destination.bigquery.typing_deduping.BigQuerySqlGenerator;
import io.airbyte.integrations.destination.bigquery.typing_deduping.CatalogParser;
import io.airbyte.integrations.destination.bigquery.typing_deduping.SqlGenerator;
import io.airbyte.integrations.destination.bigquery.typing_deduping.TypingAndDedupingFlag;
import io.airbyte.integrations.destination.buffered_stream_consumer.BufferedStreamConsumer;
import io.airbyte.integrations.destination.buffered_stream_consumer.OnCloseFunction;
import io.airbyte.integrations.destination.buffered_stream_consumer.OnStartFunction;
import io.airbyte.integrations.destination.record_buffer.BufferCreateFunction;
import io.airbyte.integrations.destination.record_buffer.FlushBufferFunction;
import io.airbyte.integrations.destination.record_buffer.SerializedBufferingStrategy;
import io.airbyte.protocol.models.v0.AirbyteMessage;
import io.airbyte.protocol.models.v0.AirbyteStream;
import io.airbyte.protocol.models.v0.AirbyteStreamNameNamespacePair;
import io.airbyte.protocol.models.v0.ConfiguredAirbyteCatalog;
import io.airbyte.protocol.models.v0.DestinationSyncMode;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class mimics the same functionality as
 * {@link io.airbyte.integrations.destination.staging.StagingConsumerFactory} which likely should be
 * placed into a commons package to be utilized across all ConsumerFactories
 */
public class BigQueryStagingConsumerFactory {

  private static final Logger LOGGER = LoggerFactory.getLogger(BigQueryStagingConsumerFactory.class);

  public AirbyteMessageConsumer create(final JsonNode config,
                                       final ConfiguredAirbyteCatalog catalog,
                                       final Consumer<AirbyteMessage> outputRecordCollector,
                                       final BigQueryStagingOperations bigQueryGcsOperations,
                                       final BufferCreateFunction onCreateBuffer,
                                       final Function<JsonNode, BigQueryRecordFormatter> recordFormatterCreator,
                                       final Function<String, String> tmpTableNameTransformer,
                                       final Function<String, String> targetTableNameTransformer,
                                       final BigQuerySqlGenerator sqlGenerator,
                                       final BigQueryDestinationHandler destinationHandler,
                                       final CatalogParser.ParsedCatalog<StandardSQLTypeName> parsedCatalog,
                                       final boolean use1s1t,
                                       final String rawNamespaceOverride) throws InterruptedException {
    final Map<AirbyteStreamNameNamespacePair, BigQueryWriteConfig> writeConfigs = createWriteConfigs(
        config,
        catalog,
        recordFormatterCreator,
        tmpTableNameTransformer,
        targetTableNameTransformer);

    final var overwriteStreamsWithTmpTable = createFinalTables(use1s1t, parsedCatalog, destinationHandler, sqlGenerator);
    final String finalDatasetId = config.get("dataset_id").asText();
    ThrowingConsumer<String, InterruptedException> typeAndDedupeStreamFunction = typingAndDedupingStreamConsumer(finalDatasetId,
            rawNamespaceOverride,
            sqlGenerator,
            destinationHandler,
            parsedCatalog,
            use1s1t,
            overwriteStreamsWithTmpTable
    );

    ThrowingConsumer<BigQueryWriteConfig, InterruptedException> replaceFinalTableConsumer =
            getReplaceFinalTableConsumer(use1s1t,
                    finalDatasetId,
                    rawNamespaceOverride,
                    sqlGenerator,
                    destinationHandler,
                    overwriteStreamsWithTmpTable,
                    parsedCatalog);

    return new BufferedStreamConsumer(
            outputRecordCollector,
            onStartFunction(bigQueryGcsOperations, writeConfigs),
            new SerializedBufferingStrategy(
                    onCreateBuffer,
                    catalog,
                    flushBufferFunction(bigQueryGcsOperations, writeConfigs, catalog, typeAndDedupeStreamFunction)),
            onCloseFunction(bigQueryGcsOperations, writeConfigs, replaceFinalTableConsumer),
            catalog,
            json -> true);
  }

  @FunctionalInterface
  public interface ThrowingConsumer<T, E extends Exception> {
    void accept(T t) throws E;
  }

  private ThrowingConsumer<String, InterruptedException> typingAndDedupingStreamConsumer(final String finalNamespace,
                                                           final String rawNamespace,
                                                           final BigQuerySqlGenerator sqlGenerator,
                                                           final BigQueryDestinationHandler destinationHandler,
                                                           final CatalogParser.ParsedCatalog<StandardSQLTypeName> parsedCatalog,
                                                           final boolean use1s1t,
                                                           final Map<SqlGenerator.StreamId, String> overwriteStreamsWithTmpTable
                                                           ) {
    return (streamName) -> {
      if (!use1s1t) {
        return;
      } else {
        final var streamConfig = getStreamConfig(streamName, finalNamespace, rawNamespace, parsedCatalog);
        String suffix;
        suffix = overwriteStreamsWithTmpTable.getOrDefault(streamConfig.id(), "");
        final String sql = sqlGenerator.updateTable(suffix, streamConfig);
        destinationHandler.execute(sql);
      }
    };
  }

  private CatalogParser.StreamConfig<StandardSQLTypeName> getStreamConfig(String streamName,
                                                                          String finalNamespace,
                                                                          String rawNamespace,
                                                                          final CatalogParser.ParsedCatalog<StandardSQLTypeName> parsedCatalog) {
    BigQueryRecordConsumer.StreamWriteTargets streamWriteTargets = new BigQueryRecordConsumer.StreamWriteTargets(finalNamespace, rawNamespace, streamName);
    return parsedCatalog.streams()
            .stream()
            .filter(s -> s.id().originalName().equals(streamWriteTargets.name()) && s.id()
                    .originalNamespace()
                    .equals(streamWriteTargets.finalNamespace()))
            .findFirst()
            // Assume that if we're trying to do T+D on a stream, that stream exists in the catalog.
            .get();
  }


  private Map<AirbyteStreamNameNamespacePair, BigQueryWriteConfig> createWriteConfigs(final JsonNode config,
                                                                                      final ConfiguredAirbyteCatalog catalog,
                                                                                      final Function<JsonNode, BigQueryRecordFormatter> recordFormatterCreator,
                                                                                      final Function<String, String> tmpTableNameTransformer,
                                                                                      final Function<String, String> targetTableNameTransformer) {
    return catalog.getStreams().stream()
        .map(configuredStream -> {
          Preconditions.checkNotNull(configuredStream.getDestinationSyncMode(), "Undefined destination sync mode");

          final AirbyteStream stream = configuredStream.getStream();
          final String streamName = stream.getName();
          final BigQueryRecordFormatter recordFormatter = recordFormatterCreator.apply(stream.getJsonSchema());

          final var internalTableNamespace = TypingAndDedupingFlag.isDestinationV2() ? AIRBYTE_NAMESPACE_SCHEMA : stream.getNamespace();
          final var targetTableName = resolveTargetTableName(targetTableNameTransformer, config.get("dataset_id").asText(), streamName);

          final BigQueryWriteConfig writeConfig = new BigQueryWriteConfig(
              streamName,
              internalTableNamespace,
              internalTableNamespace,
              BigQueryUtils.getDatasetLocation(config),
              tmpTableNameTransformer.apply(streamName),
              targetTableName,
              recordFormatter.getBigQuerySchema(),
              configuredStream.getDestinationSyncMode());

          LOGGER.info("BigQuery write config: {}", writeConfig);

          return writeConfig;
        })
        .collect(Collectors.toMap(
            c -> new AirbyteStreamNameNamespacePair(c.streamName(), c.namespace()),
            Functions.identity()));
  }

  private String resolveTargetTableName(final Function<String, String> targetTableNameResolver, String datasetId, String streamName) {
    final String v2name = String.join("_",
            Arrays.asList(datasetId, streamName)
                    .stream().filter(Objects::nonNull)
                    .collect(Collectors.toList()));
    return TypingAndDedupingFlag.isDestinationV2() ?
            Names.toAlphanumericAndUnderscore(v2name) : targetTableNameResolver.apply(streamName);
  }

  /**
   * Sets up {@link BufferedStreamConsumer} with creation of the destination's raw tables
   *
   * <p>
   * Note: targetTableId is synonymous with airbyte_raw table
   * </p>
   *
   * @param bigQueryGcsOperations collection of Google Cloud Storage Operations
   * @param writeConfigs configuration settings used to describe how to write data and where it exists
   * @return
   */
  private OnStartFunction onStartFunction(final BigQueryStagingOperations bigQueryGcsOperations,
                                          final Map<AirbyteStreamNameNamespacePair, BigQueryWriteConfig> writeConfigs) {
    return () -> {
      LOGGER.info("Preparing airbyte_raw tables in destination started for {} streams", writeConfigs.size());
      for (final BigQueryWriteConfig writeConfig : writeConfigs.values()) {
        LOGGER.info("Preparing staging are in destination for schema: {}, stream: {}, target table: {}, stage: {}",
            writeConfig.tableSchema(), writeConfig.streamName(), writeConfig.targetTableId(), writeConfig.streamName());
        // In Destinations V2, we will always use the 'airbyte' schema/namespace for raw tables
        final String rawDatasetId = TypingAndDedupingFlag.isDestinationV2() ? AIRBYTE_NAMESPACE_SCHEMA : writeConfig.datasetId();
        // Regardless, ensure the schema the customer wants to write to exists
        bigQueryGcsOperations.createSchemaIfNotExists(writeConfig.datasetId(), writeConfig.datasetLocation());
        // Schema used for raw and airbyte internal tables
        bigQueryGcsOperations.createSchemaIfNotExists(rawDatasetId, writeConfig.datasetLocation());
        // Customer's destination schema
        // With checkpointing, we will be creating the target table earlier in the setup such that
        // the data can be immediately loaded from the staging area
        bigQueryGcsOperations.createTableIfNotExists(writeConfig.targetTableId(), writeConfig.tableSchema());
        bigQueryGcsOperations.createStageIfNotExists(rawDatasetId, writeConfig.streamName());
        // When OVERWRITE mode, truncate the destination's raw table prior to syncing data
        if (writeConfig.syncMode() == DestinationSyncMode.OVERWRITE) {
          // TODO: this might need special handling during the migration
          bigQueryGcsOperations.truncateTableIfExists(rawDatasetId, writeConfig.targetTableId(), writeConfig.tableSchema());
        }
      }
      LOGGER.info("Preparing airbyte_raw tables in destination completed.");
    };
  }

  private Map<SqlGenerator.StreamId, String> createFinalTables(boolean use1s1t,
                                                               final CatalogParser.ParsedCatalog<StandardSQLTypeName> parsedCatalog,
                                                               final BigQueryDestinationHandler destinationHandler,
                                                               final BigQuerySqlGenerator sqlGenerator) throws InterruptedException {
    // TODO: share this code from BigQueryRecordConsumer
    Map<SqlGenerator.StreamId, String> overwriteStreamsWithTmpTable = new HashMap<>();
    if (use1s1t) {
      // For each stream, make sure that its corresponding final table exists.
      for (CatalogParser.StreamConfig<StandardSQLTypeName> stream : parsedCatalog.streams()) {
        final Optional<TableDefinition> existingTable = destinationHandler.findExistingTable(stream.id());
        if (existingTable.isEmpty()) {
          destinationHandler.execute(sqlGenerator.createTable(stream, ""));
          if (stream.destinationSyncMode() == DestinationSyncMode.OVERWRITE) {
            // We're creating this table for the first time. Write directly into it.
            overwriteStreamsWithTmpTable.put(stream.id(), "");
          }
        } else {
          destinationHandler.execute(sqlGenerator.alterTable(stream, existingTable.get()));
          if (stream.destinationSyncMode() == DestinationSyncMode.OVERWRITE) {
            final BigInteger rowsInFinalTable = destinationHandler.getFinalTable(stream.id()).getNumRows();
            if (new BigInteger("0").equals(rowsInFinalTable)) {
              // The table already exists but is empty. We'll load data incrementally.
              // (this might be because the user ran a reset, which creates an empty table)
              overwriteStreamsWithTmpTable.put(stream.id(), "");
            } else {
              // We're working with an existing table. Write into a tmp table. We'll overwrite the table at the end of the sync.
              overwriteStreamsWithTmpTable.put(stream.id(), OVERWRITE_TABLE_SUFFIX);
            }
          }
        }
      }
    }
    return overwriteStreamsWithTmpTable;
  }

  /**
   * Flushes buffer data, writes to staging environment then proceeds to upload those same records to
   * destination table
   *
   * @param bigQueryGcsOperations collection of utility SQL operations
   * @param writeConfigs book keeping configurations for writing and storing state to write records
   * @param catalog configured Airbyte catalog
   */
  private FlushBufferFunction flushBufferFunction(
                                                  final BigQueryStagingOperations bigQueryGcsOperations,
                                                  final Map<AirbyteStreamNameNamespacePair, BigQueryWriteConfig> writeConfigs,
                                                  final ConfiguredAirbyteCatalog catalog,
                                                  final ThrowingConsumer<String, InterruptedException> typerDeduper) {
    return (pair, writer) -> {
      LOGGER.info("Flushing buffer for stream {} ({}) to staging", pair.getName(), FileUtils.byteCountToDisplaySize(writer.getByteCount()));
      if (!writeConfigs.containsKey(pair)) {
        throw new IllegalArgumentException(
            String.format("Message contained record from a stream that was not in the catalog. \ncatalog: %s", Jsons.serialize(catalog)));
      }

      final BigQueryWriteConfig writeConfig = writeConfigs.get(pair);
      final String datasetId = writeConfig.datasetId();
      final String stream = writeConfig.streamName();
      try (writer) {
        writer.flush();
        final String stagedFile = bigQueryGcsOperations.uploadRecordsToStage(datasetId, stream, writer);
        /*
         * The primary reason for still adding staged files despite immediately uploading the staged file to
         * the destination's raw table is because the cleanup for the staged files will occur at the end of
         * the sync
         */
        writeConfig.addStagedFile(stagedFile);
        bigQueryGcsOperations.copyIntoTableFromStage(datasetId, stream, writeConfig.targetTableId(), writeConfig.tableSchema(),
            List.of(stagedFile));
        final BigQueryRecordConsumer.StreamWriteTargets writeTargets = null;
        typerDeduper.accept(stream);
      } catch (final Exception e) {
        LOGGER.error("Failed to flush and commit buffer data into destination's raw table:", e);
        throw new RuntimeException("Failed to upload buffer to stage and commit to destination", e);
      }
    };
  }

  /**
   * Tear down process, will attempt to clean out any staging area
   *
   * @param bigQueryGcsOperations collection of staging operations
   * @param writeConfigs configuration settings used to describe how to write data and where it exists
   * @return
   */
  private OnCloseFunction onCloseFunction(final BigQueryStagingOperations bigQueryGcsOperations,
                                          final Map<AirbyteStreamNameNamespacePair, BigQueryWriteConfig> writeConfigs,
                                          final ThrowingConsumer<BigQueryWriteConfig, InterruptedException> replaceFinalTableConsumer) {
    return (hasFailed) -> {
      /*
       * Previously the hasFailed value was used to commit any remaining staged files into destination,
       * however, with the changes to checkpointing this will no longer be necessary since despite partial
       * successes, we'll be committing the target table (aka airbyte_raw) table throughout the sync
       */


      LOGGER.info("Cleaning up destination started for {} streams", writeConfigs.size());
      for (final BigQueryWriteConfig writeConfig : writeConfigs.values()) {
        bigQueryGcsOperations.dropStageIfExists(writeConfig.datasetId(), writeConfig.streamName());
        // replace final table
        replaceFinalTableConsumer.accept(writeConfig);
      }
      LOGGER.info("Cleaning up destination completed.");
    };
  }

  private ThrowingConsumer<BigQueryWriteConfig, InterruptedException> getReplaceFinalTableConsumer(boolean use1s1t,
                                                                  final String finalNamespace,
                                                                  final String rawNamespace,
                                                                  final BigQuerySqlGenerator sqlGenerator,
                                                                  final BigQueryDestinationHandler destinationHandler,
                                                                  final Map<SqlGenerator.StreamId, String> overwriteStreamsWithTmpTable,
                                                                  final CatalogParser.ParsedCatalog<StandardSQLTypeName> parsedCatalog
                                                                  ) {
    return (writeConfig) -> {
      final var streamConfig = getStreamConfig(writeConfig.streamName(), finalNamespace, rawNamespace, parsedCatalog);
      if (use1s1t && DestinationSyncMode.OVERWRITE.equals(writeConfig.syncMode())) {
        LOGGER.info("Overwriting final table with tmp table");
        final Optional<String> overwriteFinalTable = sqlGenerator.overwriteFinalTable(overwriteStreamsWithTmpTable.get(streamConfig.id()), streamConfig);
        if (overwriteFinalTable.isPresent()) {
          destinationHandler.execute(overwriteFinalTable.get());
        }
      }
    };
  }

}
