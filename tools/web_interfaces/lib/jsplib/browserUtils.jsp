<%--
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it). 
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
<script type="text/javascript">
var destElement;
console.log("Windows HASH:");
console.log(window.location.hash);

if(window.location.hash){
	destElement = window.location.hash.substr(1);
}  
</script>
<%
boolean debug = false;
String userAgent = request.getHeader("user-agent");
String info[] = getBrowserInfo(userAgent);
String browsername = getBrowserName(info);
String browserversion = getBrowserVersion(info);

if(browsername != null){
	// Microsoft IE (Trident e' il browsername che viene impostato da IE11)
	// <meta http-equiv="X-UA-Compatible" content="IE=8">
	if(browsername.equalsIgnoreCase("MSIE") ||  browsername.equalsIgnoreCase("Trident")){
		// fix ie10
		%>
		<meta http-equiv="X-UA-Compatible" content="IE=edge">
		<script type="text/javascript">
			window.location.hash="no-back-button";
			window.location.hash="Again-No-back-button";//again because google chrome don't insert first hash into history
			window.onhashchange=function(){window.location.hash="no-back-button";}
		</script> 
		<%
	
	} else if(browsername.equalsIgnoreCase("Firefox")){ // Firefox
			%>
			<script type="text/javascript">
				window.location.hash="no-back-button";
				window.location.hash="Again-No-back-button";//again because google chrome don't insert first hash into history
				window.onhashchange=function(){window.location.hash="no-back-button";}
			</script> 
			<%
	} else if(browsername.equalsIgnoreCase("Chrome")){ // Chrome
		%>
		<script type="text/javascript">
	  	  window.history.forward();
	   	 function noBack() { window.history.forward(); }
		</script>
		<%
	} else{
		%>
		<script type="text/javascript">
			window.location.hash="no-back-button";
			window.location.hash="Again-No-back-button";//again because google chrome don't insert first hash into history
			window.onhashchange=function(){window.location.hash="no-back-button";}
		</script> 
		<%
	}
}
%>

<% if(debug){ %>
<script>
var browserName = '<%=browsername%>';
var browserVersione = '<%=browserversion %>';
</script>
<% } %>
