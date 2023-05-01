"use strict";(self.webpackChunkdocu=self.webpackChunkdocu||[]).push([[9725],{59145:(e,t,a)=>{a.r(t),a.d(t,{assets:()=>p,contentTitle:()=>o,default:()=>d,frontMatter:()=>i,metadata:()=>l,toc:()=>m});var n=a(87462),r=(a(67294),a(3905));const i={},o="Yandex Metrica",l={unversionedId:"integrations/sources/yandex-metrica",id:"integrations/sources/yandex-metrica",title:"Yandex Metrica",description:"This page contains the setup guide and reference information for the Yandex Metrica source connector.",source:"@site/../docs/integrations/sources/yandex-metrica.md",sourceDirName:"integrations/sources",slug:"/integrations/sources/yandex-metrica",permalink:"/integrations/sources/yandex-metrica",draft:!1,editUrl:"https://github.com/airbytehq/airbyte/blob/master/docs/../docs/integrations/sources/yandex-metrica.md",tags:[],version:"current",frontMatter:{},sidebar:"mySidebar",previous:{title:"Yahoo Finance Price",permalink:"/integrations/sources/yahoo-finance-price"},next:{title:"Yotpo",permalink:"/integrations/sources/yotpo"}},p={},m=[{value:"Prerequisites",id:"prerequisites",level:2},{value:"Setup guide",id:"setup-guide",level:2},{value:"Step 1: Set up Yandex Metrica",id:"step-1-set-up-yandex-metrica",level:3},{value:"Step 2: Set up the Yandex Metrica connector in Airbyte",id:"step-2-set-up-the-yandex-metrica-connector-in-airbyte",level:3},{value:"For Airbyte Open Source:",id:"for-airbyte-open-source",level:4},{value:"Supported sync modes",id:"supported-sync-modes",level:2},{value:"Supported Streams",id:"supported-streams",level:2},{value:"Performance considerations",id:"performance-considerations",level:2},{value:"Data type mapping",id:"data-type-mapping",level:2},{value:"Changelog",id:"changelog",level:2}],c={toc:m},s="wrapper";function d(e){let{components:t,...a}=e;return(0,r.kt)(s,(0,n.Z)({},c,a,{components:t,mdxType:"MDXLayout"}),(0,r.kt)("h1",{id:"yandex-metrica"},"Yandex Metrica"),(0,r.kt)("p",null,"This page contains the setup guide and reference information for the Yandex Metrica source connector."),(0,r.kt)("h2",{id:"prerequisites"},"Prerequisites"),(0,r.kt)("ul",null,(0,r.kt)("li",{parentName:"ul"},"Counter ID"),(0,r.kt)("li",{parentName:"ul"},"OAuth2 Token")),(0,r.kt)("h2",{id:"setup-guide"},"Setup guide"),(0,r.kt)("h3",{id:"step-1-set-up-yandex-metrica"},"Step 1: Set up Yandex Metrica"),(0,r.kt)("ol",null,(0,r.kt)("li",{parentName:"ol"},(0,r.kt)("a",{parentName:"li",href:"https://metrica.yandex.com/"},"Create Yandex Metrica account")," if you don't already have one."),(0,r.kt)("li",{parentName:"ol"},"Head to ",(0,r.kt)("a",{parentName:"li",href:"https://metrica.yandex.com/list"},"Management page")," and add new tag or choose an existing one."),(0,r.kt)("li",{parentName:"ol"},"At the top of the dashboard you will see 8 digit number to the right of your website name. This is your ",(0,r.kt)("strong",{parentName:"li"},"Counter ID"),"."),(0,r.kt)("li",{parentName:"ol"},"Create a new app or choose an existing one from ",(0,r.kt)("a",{parentName:"li",href:"https://oauth.yandex.com/"},"My apps page"),".",(0,r.kt)("ul",{parentName:"li"},(0,r.kt)("li",{parentName:"ul"},"Which platform is the app required for?: ",(0,r.kt)("strong",{parentName:"li"},"Web services")),(0,r.kt)("li",{parentName:"ul"},"Callback URL: ",(0,r.kt)("a",{parentName:"li",href:"https://oauth.yandex.com/verification_code"},"https://oauth.yandex.com/verification_code")),(0,r.kt)("li",{parentName:"ul"},"What data do you need?: ",(0,r.kt)("strong",{parentName:"li"},"Yandex.Metrica"),". Read permission will suffice."))),(0,r.kt)("li",{parentName:"ol"},"Choose your app from ",(0,r.kt)("a",{parentName:"li",href:"https://oauth.yandex.com/"},"the list"),".",(0,r.kt)("ul",{parentName:"li"},(0,r.kt)("li",{parentName:"ul"},"To create your API key you will need to grab your ",(0,r.kt)("strong",{parentName:"li"},"ClientID"),","),(0,r.kt)("li",{parentName:"ul"},"Now to get the API key craft a GET request to an endpoint ",(0,r.kt)("em",{parentName:"li"},(0,r.kt)("a",{parentName:"em",href:"https://oauth.yandex.com/authorizE?response_type=token&client_id=%5C"},"https://oauth.yandex.com/authorizE?response_type=token&client_id=\\"),"<Your Client ID",">")),(0,r.kt)("li",{parentName:"ul"},"You will receive a response with your ",(0,r.kt)("strong",{parentName:"li"},"API key"),". Save it.")))),(0,r.kt)("h3",{id:"step-2-set-up-the-yandex-metrica-connector-in-airbyte"},"Step 2: Set up the Yandex Metrica connector in Airbyte"),(0,r.kt)("ol",null,(0,r.kt)("li",{parentName:"ol"},(0,r.kt)("a",{parentName:"li",href:"https://cloud.airbyte.io/workspaces"},"Log into your Airbyte Cloud")," account."),(0,r.kt)("li",{parentName:"ol"},"Click ",(0,r.kt)("strong",{parentName:"li"},"Sources")," and then click ",(0,r.kt)("strong",{parentName:"li"},"+ New source"),"."),(0,r.kt)("li",{parentName:"ol"},"On the Set up the source page, select ",(0,r.kt)("strong",{parentName:"li"},"Yandex Metrica")," from the ",(0,r.kt)("strong",{parentName:"li"},"Source type")," dropdown."),(0,r.kt)("li",{parentName:"ol"},"Enter a name for the Yandex Metrica connector."),(0,r.kt)("li",{parentName:"ol"},"Enter Authentication Token from step 1."),(0,r.kt)("li",{parentName:"ol"},"Enter Counter ID."),(0,r.kt)("li",{parentName:"ol"},"Enter the Start Date in format ",(0,r.kt)("inlineCode",{parentName:"li"},"YYYY-MM-DD"),"."),(0,r.kt)("li",{parentName:"ol"},"Enter the End Date in format ",(0,r.kt)("inlineCode",{parentName:"li"},"YYYY-MM-DD")," (Optional).")),(0,r.kt)("h4",{id:"for-airbyte-open-source"},"For Airbyte Open Source:"),(0,r.kt)("ol",null,(0,r.kt)("li",{parentName:"ol"},"Navigate to the Airbyte Open Source dashboard."),(0,r.kt)("li",{parentName:"ol"},"Click ",(0,r.kt)("strong",{parentName:"li"},"Sources")," and then click ",(0,r.kt)("strong",{parentName:"li"},"+ New source"),"."),(0,r.kt)("li",{parentName:"ol"},"On the Set up the source page, select ",(0,r.kt)("strong",{parentName:"li"},"Yandex Metrica")," from the Source type dropdown."),(0,r.kt)("li",{parentName:"ol"},"Enter the name for the Yandex Metrica connector."),(0,r.kt)("li",{parentName:"ol"},"Enter Authentication Token from step 1."),(0,r.kt)("li",{parentName:"ol"},"Enter Counter ID."),(0,r.kt)("li",{parentName:"ol"},"Enter the Start Date in format ",(0,r.kt)("inlineCode",{parentName:"li"},"YYYY-MM-DD"),"."),(0,r.kt)("li",{parentName:"ol"},"Enter the End Date in format ",(0,r.kt)("inlineCode",{parentName:"li"},"YYYY-MM-DD")," (Optional).")),(0,r.kt)("h2",{id:"supported-sync-modes"},"Supported sync modes"),(0,r.kt)("p",null,"The Yandex Metrica source connector supports the following ",(0,r.kt)("a",{parentName:"p",href:"https://docs.airbyte.com/cloud/core-concepts#connection-sync-modes"},"sync modes"),":"),(0,r.kt)("ul",null,(0,r.kt)("li",{parentName:"ul"},(0,r.kt)("a",{parentName:"li",href:"https://docs.airbyte.com/understanding-airbyte/connections/full-refresh-overwrite/"},"Full Refresh - Overwrite")),(0,r.kt)("li",{parentName:"ul"},(0,r.kt)("a",{parentName:"li",href:"https://docs.airbyte.com/understanding-airbyte/connections/full-refresh-append"},"Full Refresh - Append")),(0,r.kt)("li",{parentName:"ul"},(0,r.kt)("a",{parentName:"li",href:"https://docs.airbyte.com/understanding-airbyte/connections/incremental-append"},"Incremental - Append")),(0,r.kt)("li",{parentName:"ul"},(0,r.kt)("a",{parentName:"li",href:"https://docs.airbyte.com/understanding-airbyte/connections/incremental-deduped-history"},"Incremental - Deduped History"))),(0,r.kt)("h2",{id:"supported-streams"},"Supported Streams"),(0,r.kt)("ul",null,(0,r.kt)("li",{parentName:"ul"},(0,r.kt)("a",{parentName:"li",href:"https://yandex.com/dev/metrika/doc/api2/logs/fields/hits.html"},"Views")," ","(","Incremental",")","."),(0,r.kt)("li",{parentName:"ul"},(0,r.kt)("a",{parentName:"li",href:"https://yandex.com/dev/metrika/doc/api2/logs/fields/visits.html"},"Sessions")," ","(","Incremental",")",".")),(0,r.kt)("h2",{id:"performance-considerations"},"Performance considerations"),(0,r.kt)("p",null,"Yandex Metrica has some ",(0,r.kt)("a",{parentName:"p",href:"https://yandex.ru/dev/metrika/doc/api2/intro/quotas.html"},"rate limits")),(0,r.kt)("admonition",{type:"tip"},(0,r.kt)("p",{parentName:"admonition"},"It is recommended to sync data once a day.")),(0,r.kt)("admonition",{type:"note"},(0,r.kt)("p",{parentName:"admonition"},"Because of the way API works some syncs may take a long time to finish. Timeout period is 2 hours.")),(0,r.kt)("h2",{id:"data-type-mapping"},"Data type mapping"),(0,r.kt)("table",null,(0,r.kt)("thead",{parentName:"table"},(0,r.kt)("tr",{parentName:"thead"},(0,r.kt)("th",{parentName:"tr",align:"left"},"Integration Type"),(0,r.kt)("th",{parentName:"tr",align:"left"},"Airbyte Type"),(0,r.kt)("th",{parentName:"tr",align:"left"},"Notes"))),(0,r.kt)("tbody",{parentName:"table"},(0,r.kt)("tr",{parentName:"tbody"},(0,r.kt)("td",{parentName:"tr",align:"left"},(0,r.kt)("inlineCode",{parentName:"td"},"string")),(0,r.kt)("td",{parentName:"tr",align:"left"},(0,r.kt)("inlineCode",{parentName:"td"},"string")),(0,r.kt)("td",{parentName:"tr",align:"left"})),(0,r.kt)("tr",{parentName:"tbody"},(0,r.kt)("td",{parentName:"tr",align:"left"},(0,r.kt)("inlineCode",{parentName:"td"},"integer")),(0,r.kt)("td",{parentName:"tr",align:"left"},(0,r.kt)("inlineCode",{parentName:"td"},"integer")),(0,r.kt)("td",{parentName:"tr",align:"left"})),(0,r.kt)("tr",{parentName:"tbody"},(0,r.kt)("td",{parentName:"tr",align:"left"},(0,r.kt)("inlineCode",{parentName:"td"},"number")),(0,r.kt)("td",{parentName:"tr",align:"left"},(0,r.kt)("inlineCode",{parentName:"td"},"number")),(0,r.kt)("td",{parentName:"tr",align:"left"})),(0,r.kt)("tr",{parentName:"tbody"},(0,r.kt)("td",{parentName:"tr",align:"left"},(0,r.kt)("inlineCode",{parentName:"td"},"array")),(0,r.kt)("td",{parentName:"tr",align:"left"},(0,r.kt)("inlineCode",{parentName:"td"},"array")),(0,r.kt)("td",{parentName:"tr",align:"left"})),(0,r.kt)("tr",{parentName:"tbody"},(0,r.kt)("td",{parentName:"tr",align:"left"},(0,r.kt)("inlineCode",{parentName:"td"},"object")),(0,r.kt)("td",{parentName:"tr",align:"left"},(0,r.kt)("inlineCode",{parentName:"td"},"object")),(0,r.kt)("td",{parentName:"tr",align:"left"})))),(0,r.kt)("h2",{id:"changelog"},"Changelog"),(0,r.kt)("table",null,(0,r.kt)("thead",{parentName:"table"},(0,r.kt)("tr",{parentName:"thead"},(0,r.kt)("th",{parentName:"tr",align:"left"},"Version"),(0,r.kt)("th",{parentName:"tr",align:"left"},"Date"),(0,r.kt)("th",{parentName:"tr",align:"left"},"Pull Request"),(0,r.kt)("th",{parentName:"tr",align:"left"},"Subject"))),(0,r.kt)("tbody",{parentName:"table"},(0,r.kt)("tr",{parentName:"tbody"},(0,r.kt)("td",{parentName:"tr",align:"left"},"1.0.0"),(0,r.kt)("td",{parentName:"tr",align:"left"},"2023-03-20"),(0,r.kt)("td",{parentName:"tr",align:"left"},(0,r.kt)("a",{parentName:"td",href:"https://github.com/airbytehq/airbyte/pull/24188"},"24188")),(0,r.kt)("td",{parentName:"tr",align:"left"},"Migrate to Beta; Change state structure")),(0,r.kt)("tr",{parentName:"tbody"},(0,r.kt)("td",{parentName:"tr",align:"left"},"0.1.0"),(0,r.kt)("td",{parentName:"tr",align:"left"},"2022-09-09"),(0,r.kt)("td",{parentName:"tr",align:"left"},(0,r.kt)("a",{parentName:"td",href:"https://github.com/airbytehq/airbyte/pull/15061"},"15061")),(0,r.kt)("td",{parentName:"tr",align:"left"},"\ud83c\udf89 New Source: Yandex metrica")))))}d.isMDXComponent=!0},3905:(e,t,a)=>{a.d(t,{Zo:()=>c,kt:()=>k});var n=a(67294);function r(e,t,a){return t in e?Object.defineProperty(e,t,{value:a,enumerable:!0,configurable:!0,writable:!0}):e[t]=a,e}function i(e,t){var a=Object.keys(e);if(Object.getOwnPropertySymbols){var n=Object.getOwnPropertySymbols(e);t&&(n=n.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),a.push.apply(a,n)}return a}function o(e){for(var t=1;t<arguments.length;t++){var a=null!=arguments[t]?arguments[t]:{};t%2?i(Object(a),!0).forEach((function(t){r(e,t,a[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(a)):i(Object(a)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(a,t))}))}return e}function l(e,t){if(null==e)return{};var a,n,r=function(e,t){if(null==e)return{};var a,n,r={},i=Object.keys(e);for(n=0;n<i.length;n++)a=i[n],t.indexOf(a)>=0||(r[a]=e[a]);return r}(e,t);if(Object.getOwnPropertySymbols){var i=Object.getOwnPropertySymbols(e);for(n=0;n<i.length;n++)a=i[n],t.indexOf(a)>=0||Object.prototype.propertyIsEnumerable.call(e,a)&&(r[a]=e[a])}return r}var p=n.createContext({}),m=function(e){var t=n.useContext(p),a=t;return e&&(a="function"==typeof e?e(t):o(o({},t),e)),a},c=function(e){var t=m(e.components);return n.createElement(p.Provider,{value:t},e.children)},s="mdxType",d={inlineCode:"code",wrapper:function(e){var t=e.children;return n.createElement(n.Fragment,{},t)}},u=n.forwardRef((function(e,t){var a=e.components,r=e.mdxType,i=e.originalType,p=e.parentName,c=l(e,["components","mdxType","originalType","parentName"]),s=m(a),u=r,k=s["".concat(p,".").concat(u)]||s[u]||d[u]||i;return a?n.createElement(k,o(o({ref:t},c),{},{components:a})):n.createElement(k,o({ref:t},c))}));function k(e,t){var a=arguments,r=t&&t.mdxType;if("string"==typeof e||r){var i=a.length,o=new Array(i);o[0]=u;var l={};for(var p in t)hasOwnProperty.call(t,p)&&(l[p]=t[p]);l.originalType=e,l[s]="string"==typeof e?e:r,o[1]=l;for(var m=2;m<i;m++)o[m]=a[m];return n.createElement.apply(null,o)}return n.createElement.apply(null,a)}u.displayName="MDXCreateElement"}}]);