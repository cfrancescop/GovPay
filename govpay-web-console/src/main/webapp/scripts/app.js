function defaultFormatter(e){var o="<div>";return o+=e.titolo,o+="</div>",o+="<div secondary>",o+=e.sottotitolo,o+="</div>"}function versamenti1(e){var o="<div>";return o+=e.sottotitolo,o+="</div>",o+="<div secondary>",o+=e.titolo,o+="</div>"}var REST_SERVER=location.protocol+'//'+location.hostname+(location.port ? ':'+location.port: ''),BASE_URL="/govpayConsoleDev/rs/dars/",LOGIN_URL="/govpayConsoleDev",REST_URL=REST_SERVER+BASE_URL;!function(e){"use strict";var o=e.querySelector("#app");o.USER_LOGGED=null,o.ADVANCED_MODE=!1,o.numSpinner=0,o.baseUrl="/",""===window.location.port,o.openDrawer=function(e){this.$.drawerPanel.openDrawer()},o.closeDrawer=function(e){this.$.drawerPanel.closeDrawer()},o.scrollPageToTop=function(){},o.showSpinner=function(){o.numSpinner++,this.$.spinner.setSpinner(!0)},o.hideSpinner=function(){o.numSpinner--,0==o.numSpinner&&this.$.spinner.setSpinner(!1)},o.showPaperToast=function(e,o){this.$.toast.toggleClass("fit-bottom",!1),this.$.toast.toggleClass("success",!1),this.$.toast.toggleClass("error",!1),this.$.toast.toggleClass("warning",!1),o&&(this.$.toast.toggleClass(o,!0),this.$.toast.toggleClass("fit-bottom",!0)),Polymer.updateStyles(),this.$.toast.text=e,this.$.toast.show()},o.displayInstalledToast=function(){Polymer.dom(e).querySelector("platinum-sw-cache").disabled||Polymer.dom(e).querySelector("#caching-complete").show()},o.addEventListener("dom-change",function(){console.log("Our app is ready to rock!"),o.getUserInfo()}),window.addEventListener("WebComponentsReady",function(){}),o.getUserInfo=function(){this.$.getUserInfo.url=REST_URL+"operatori/user",this.$.getUserInfo.generateRequest()},o.getUserInfo_responseHandler=function(e){var n=e.detail.response;switch(console.log(e),n.esitoOperazione){case"ESEGUITA":o.USER_LOGGED=e.detail.response.response,o.getConsoleInfo();break;case"NONESEGUITA":window.location=o.getDoLogoutURL();break;case"ERRORE":window.location=o.getDoLogoutURL()}},o.getUserInfo_errorHandler=function(e){this.dars_errorHandler(e)},o.getConsoleInfo=function(){this.$.getConsoleInfo.url=REST_URL,this.$.getConsoleInfo.generateRequest()},o.getConsoleURL=function(){return REST_URL},o.getLoginURL=function(){return REST_SERVER+"/govpayConsoleDev/"},o.getLogoutOkURL=function(){return REST_SERVER+"/govpayConsoleDev/?msg=lo"},o.getNonAutorizzatoURL=function(){return REST_SERVER+"/govpayConsoleDev/?msg=na"},o.getSessioneScadutaURL=function(){return REST_SERVER+"/govpayConsoleDev/?msg=se"},o.getDoLogoutURL=function(){return REST_SERVER+"/govpayConsoleDev/logout?msg=er"},o.getConsoleInfo_responseHandler=function(e){var n="";e.detail.response.response.menu.home?(this.$.mainModule.title=e.detail.response.response.menu.home.label,this.$.mainModule.resource=e.detail.response.response.menu.home.uri,n=e.detail.response.response.menu.home.uri):this.$.mainModule.title=e.detail.response.response.titolo;for(var t=0;t<e.detail.response.response.menu.sezioni.length;t++){var s=e.detail.response.response.menu.sezioni[t];s.vociMenu&&s.vociMenu.length>0?(s.haVoci=!0,s.opened=!1):(s.haVoci=!1,s.opened=!1);for(var r=0;r<s.vociMenu.length;r++){var i=s.vociMenu[r];i.uri==n&&(s.opened=!0,s.selected=r)}}o.consoleInfo=e.detail.response.response},o.getConsoleInfo_errorHandler=function(e){this.dars_errorHandler(e)},o.toggleMenu=function(e){for(var n=e.model.index,t=0;t<o.consoleInfo.menu.sezioni.length;t++){o.consoleInfo.menu.sezioni[t];t==n?o.set("consoleInfo.menu.sezioni."+t+".opened",!e.model.item.opened):o.set("consoleInfo.menu.sezioni."+t+".opened",!1)}},o.loadModule=function(e){this.$.mainModule.title=e.target.dataItem.label,this.$.mainModule.resource=e.target.dataItem.uri,o.closeDrawer()},o.dars_errorHandler=function(e){o.showPaperToast("Errore","error"),console.log(e);var n=e.detail.request.status;401==n?window.location=o.getNonAutorizzatoURL():403==n?window.location=o.getSessioneScadutaURL():500==n&&(window.location=o.getDoLogoutURL())}}(document);