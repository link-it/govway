<%--
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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
<!DOCTYPE html>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page session="true" import="org.openspcoop2.web.lib.mvc.*" %>

<%
String iddati = "";
String ct = request.getContentType();
if (ct != null && (ct.indexOf(Costanti.MULTIPART) != -1)) {
  iddati = ServletUtils.getObjectFromSession(request, session, String.class, Costanti.SESSION_ATTRIBUTE_ID_DATI);
} else {
  iddati = request.getParameter(Costanti.PARAMETER_NAME_ID_DATI);
}
String gdString = Costanti.SESSION_ATTRIBUTE_GENERAL_DATA;
if (iddati != null && !iddati.equals("notdefined"))
  gdString += iddati;
else
  iddati = "notdefined";
GeneralData gd = ServletUtils.getObjectFromSession(request, session, GeneralData.class, gdString);
String randomNonce = (String) request.getAttribute(Costanti.REQUEST_ATTRIBUTE_CSP_RANDOM_NONCE);

// int idx =gd.getUrl().indexOf("/govwayConsole");
// int l = "/govwayConsole".length();

//   if(idx > -1){
//   	String ap1 = gd.getUrl().substring(0,idx);
// 	String ap2 = gd.getUrl().substring(idx+l);
// 	gd.setUrl(ap1 + request.getContextPath() + ap2);
//   }

%>

<html lang="it">
<head>
	<meta charset="UTF-8">
	<jsp:include page="/jsplib/browserUtils.jsp" flush="true" />
	<title><%= gd.getTitle() %></title>
	<SCRIPT type="text/javascript" nonce="<%= randomNonce %>">
	
	var ok = true;
	function white(str) {
	  for (var n=0; n<str.length; n++){
	    if (str.charAt(n) == " "){
	      ok = false;
	    }
	  }
	}
	
	function CheckDati() {
	  white(document.form.login.value);
	  white(document.form.password.value);
	  if (ok == false) {
	    ok = true;
	    var win = window.open("?op=alert&msg=NoSpace", "winAlert", "width=200,height=130");
	    win.focus();
	    return false;
	  } else { document.form.submit(); }
	};
	
	</SCRIPT>
	<link href="css/roboto/roboto-fontface.css" rel="stylesheet" type="text/css">
	<link rel="stylesheet" href="css/<%= gd.getCss() %>" type="text/css">
	<script type="text/javascript" src="js/webapps.js" nonce="<%= randomNonce %>"></script>
	<!-- JQuery lib-->
	<script type="text/javascript" src="js/jquery-latest.js" nonce="<%= randomNonce %>"></script>
	<jsp:include page="/jsplib/menuUtente.jsp" flush="true" />
	<link rel="shortcut icon" href="images/favicon.ico" type="image/x-icon" />
	<script type="text/javascript" nonce="<%= randomNonce %>">
$(document).ready(function(){
	focusText(document.form);
});
</script>
</head>
<body marginwidth=0 marginheight=0>
	<table class="bodyWrapper">
		<tbody>
			<jsp:include page="/jsplib/templateHeader.jsp" flush="true" />
			<!-- TR3: Body -->
			<tr class="trPageBody">
				<jsp:include page="/jsplib/edit-page.jsp" flush="true" >
					<jsp:param value="true" name="visualizzaBottoneLogin"/>
				</jsp:include>
			</tr>
			<jsp:include page="/jsplib/templateFooter.jsp" flush="true" />
		</tbody>
	</table>
</body>

<% session.invalidate(); %>
