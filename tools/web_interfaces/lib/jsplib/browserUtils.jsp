<%--
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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
	
       String browsername = "";
       String browserversion = "";
       String browser = Information  ;
       if(browser.contains("MSIE")){ //IE <= 10
           String subsString = browser.substring( browser.indexOf("MSIE"));
           info = (subsString.split(";")[0]).split(" ");
        }
      else if(browser.contains("msie")){ //IE <= 10
           String subsString = browser.substring( browser.indexOf("msie"));
           info = (subsString.split(";")[0]).split(" ");
        }
      else if(browser.contains("Trident")){ //IE 11
			String subsString = browser.substring( browser.indexOf("Trident"));
			info = new String[2];
			
			info[0] = (subsString.split(";")[0]).split("/")[0];
			
			int idx = (subsString.split(";")[1]).indexOf(")");
			if(idx > -1)
				info[1] = ((String)(subsString.split(";")[1]).subSequence(0, idx)).split(":")[1];
			else
			info[1] = "";
			
		}
      else if(browser.contains("Firefox")){
           String subsString = browser.substring( browser.indexOf("Firefox"));
           info = (subsString.split(" ")[0]).split("/");
      }
      else if(browser.contains("Chrome")){
           String subsString = browser.substring( browser.indexOf("Chrome"));
           info = (subsString.split(" ")[0]).split("/");
      }
      else if(browser.contains("Opera")){
           String subsString = browser.substring( browser.indexOf("Opera"));
           info = (subsString.split(" ")[0]).split("/");
      }
      else if(browser.contains("Safari")){
           String subsString = browser.substring( browser.indexOf("Safari"));
           info = (subsString.split(" ")[0]).split("/");
      }         
 return info;
}
%>

<%
boolean debug = false;
String userAgent = request.getHeader("user-agent");
String info[] = getBrowserInfo(userAgent);
String browsername = info[0];
String browserversion = info[1];

// Microsoft IE (Trident e' il browsername che viene impostato da IE11)
if(browsername.equalsIgnoreCase("MSIE") ||  browsername.equalsIgnoreCase("Trident")){
	%>
	<meta http-equiv="X-UA-Compatible" content="IE=8">
	<script type="text/javascript">
		window.location.hash="no-back-button";
		window.location.hash="Again-No-back-button";//again because google chrome don't insert first hash into history
		window.onhashchange=function(){window.location.hash="no-back-button";}
	</script> 
	<%

} else
	// Firefox
	if(browsername.equalsIgnoreCase("Firefox")){
		%>
		<script type="text/javascript">
			window.location.hash="no-back-button";
			window.location.hash="Again-No-back-button";//again because google chrome don't insert first hash into history
			window.onhashchange=function(){window.location.hash="no-back-button";}
		</script> 
		<%
} else 
	// Chrome
	if(browsername.equalsIgnoreCase("Chrome")){
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

%>

<% if(debug){ %>
<script>
var browserName = '<%=browsername%>';
var browserVersione = '<%=browserversion %>';
</script>
<% } %>