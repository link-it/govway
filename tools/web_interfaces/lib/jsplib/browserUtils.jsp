<%--
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3, as published by
 * the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
--%>

<%@ page session="true" import="org.openspcoop2.web.lib.mvc.*" %>
<%!

public String[] getBrowserInfo(String Information) {
	String info[] = null;
	
	try{
		String browsername = "";
     	String browserversion = "";
       	String browser = Information  ;
		if(browser.contains("MSIE")){ //IE <= 10
           String subsString = browser.substring( browser.indexOf("MSIE"));
           info = (subsString.split(";")[0]).split(" ");
       	} else if(browser.contains("msie")){ //IE <= 10
           String subsString = browser.substring( browser.indexOf("msie"));
           info = (subsString.split(";")[0]).split(" ");
       	} else if(browser.contains("Trident")){ //IE 11
			String subsString = browser.substring( browser.indexOf("Trident"));
			info = new String[2];
			
			info[0] = (subsString.split(";")[0]).split("/")[0];
			
			int idx = (subsString.split(";")[1]).indexOf(")");
			if(idx > -1)
				info[1] = ((String)(subsString.split(";")[1]).subSequence(0, idx)).split(":")[1];
			else
			info[1] = "";
			
		} else if(browser.contains("Edge")){ // Edge (IE 12+)
           String subsString = browser.substring( browser.indexOf("Edge"));
           info = (subsString.split(" ")[0]).split("/");
       	} else if(browser.contains("Edg")){ // Edge (IE 12+)
           String subsString = browser.substring( browser.indexOf("Edg"));
           info = (subsString.split(" ")[0]).split("/");
    	} else if(browser.contains("Firefox")){
           String subsString = browser.substring( browser.indexOf("Firefox"));
           info = (subsString.split(" ")[0]).split("/");
      	} else if(browser.contains("Chrome")){
           String subsString = browser.substring( browser.indexOf("Chrome"));
           info = (subsString.split(" ")[0]).split("/");
      	} else if(browser.contains("Opera")){
           String subsString = browser.substring( browser.indexOf("Opera"));
           info = (subsString.split(" ")[0]).split("/");
      	} else if(browser.contains("Safari")){
           String subsString = browser.substring( browser.indexOf("Safari"));
           info = (subsString.split(" ")[0]).split("/");
      	}  
	}catch(Throwable t){}
	
 return info;
}

public String getBrowserName(String []info){
	if(info != null && info.length > 0)
		return info[0];
	
	return null;
}

public String getBrowserVersion(String []info){
	if(info != null && info.length > 1)
		return info[1];
	
	return null;
}
%>
<%
String iddati = request.getParameter(Costanti.PARAMETER_NAME_ID_DATI);
String params = (String) request.getAttribute(Costanti.PARAMETER_NAME_PARAMS);
String gdString = Costanti.SESSION_ATTRIBUTE_GENERAL_DATA;
String pdString = Costanti.SESSION_ATTRIBUTE_PAGE_DATA;
if (iddati != null && !iddati.equals("notdefined")) {
  gdString += iddati;
  pdString += iddati;
}
else {
  iddati = "notdefined";
}

if(params == null) params="";

GeneralData gd = ServletUtils.getObjectFromSession(request, session, GeneralData.class, gdString);
PageData pd = ServletUtils.getObjectFromSession(request, session, PageData.class, pdString);
String randomNonce = (String) request.getAttribute(Costanti.REQUEST_ATTRIBUTE_CSP_RANDOM_NONCE);

String tabSessionKey = ServletUtils.getTabIdFromRequestAttribute(request);
if(tabSessionKey == null)
	tabSessionKey = "";

String csrfTokenFromSession = ServletUtils.leggiTokenCSRF(request, session);
if(csrfTokenFromSession == null)
	csrfTokenFromSession = "";

// boolean debug = true;
String userAgent = request.getHeader("user-agent");
String info[] = getBrowserInfo(userAgent);
String browsername = getBrowserName(info);
String browserversion = getBrowserVersion(info);

if(browsername != null){
	// window.location.hash="Again-No-back-button"; viene inserito due volte: again because google chrome don't insert first hash into history
	
	
	// Microsoft IE (Trident e' il browsername che viene impostato da IE11)
	// <meta http-equiv="X-UA-Compatible" content="IE=8">
	if(browsername.equalsIgnoreCase("MSIE") ||  browsername.equalsIgnoreCase("Trident") ||  browsername.equalsIgnoreCase("Edg") ||  browsername.equalsIgnoreCase("Edge")){
		// fix ie10
		%>
		<meta http-equiv="X-UA-Compatible" content="IE=edge">
		<script type="text/javascript" nonce="<%= randomNonce %>">
			window.location.hash="no-back-button";
			window.location.hash="Again-No-back-button";
			window.onhashchange=function(){window.location.hash="no-back-button";}
		</script> 
		<%
	
	} else if(browsername.equalsIgnoreCase("Firefox")){ // Firefox
			%>
			<script type="text/javascript" nonce="<%= randomNonce %>">
				window.location.hash="no-back-button";
				window.location.hash="Again-No-back-button";
				window.onhashchange=function(){window.location.hash="no-back-button";}
			</script> 
			<%
	} else if(browsername.equalsIgnoreCase("Chrome")){ // Chrome
		%>
		<script type="text/javascript" nonce="<%= randomNonce %>">
	  	  window.history.forward();
	   	 function noBack() { window.history.forward(); }
		</script>
		<%
	} else{
		%>
		<script type="text/javascript" nonce="<%= randomNonce %>">
			window.location.hash="no-back-button";
			window.location.hash="Again-No-back-button";
			window.onhashchange=function(){window.location.hash="no-back-button";}
		</script> 
		<%
	}
}
// console.log("Windows HASH:"); console.log(window.location.hash);
%>

<script type="text/javascript" nonce="<%= randomNonce %>">
var destElement;

if(window.location.hash){
	destElement = window.location.hash.substr(1);
	
//	console.log(window.location.hash.substr(1));
}  

var browserName = '<%=browsername%>';
var browserVersione = '<%=browserversion %>';
var browserVersion = parseInt(browserVersione, 10);

function isIE(){
    return browserName == 'MSIE' || browserName == 'msie'
    	 || browserName == 'Trident'  || browserName == 'Edge'
    		 || browserName == 'Edg';
}

function isChrome(){
    return browserName == 'Chrome';
}

function isFirefox(){
    return browserName == 'Firefox';
}

function isOpera(){
    return browserName == 'Opera';
}

function isSafari(){
    return browserName == 'Safari';
}

function IEVersione(){
	return browserVersion;
}

var tabSessionKey = '<%=Costanti.PARAMETER_TAB_KEY %>';
var prevTabSessionKey = '<%=Costanti.PARAMETER_PREV_TAB_KEY %>';
var tabValue = '<%=tabSessionKey %>';
var csrfTokenKey = '<%=Costanti.PARAMETRO_CSRF_TOKEN%>';
var csrfToken = '<%=csrfTokenFromSession %>';

</script>



