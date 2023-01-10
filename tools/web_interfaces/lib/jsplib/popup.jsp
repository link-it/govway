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
String pdString = Costanti.SESSION_ATTRIBUTE_PAGE_DATA;
if (iddati != null && !iddati.equals("notdefined")) {
  gdString += iddati;
  pdString += iddati;
}
else {
  iddati = "notdefined";
}
GeneralData gd = ServletUtils.getObjectFromSession(request, session, GeneralData.class, gdString);
PageData pd = ServletUtils.getObjectFromSession(request, session, PageData.class, pdString);
String randomNonce = (String) request.getAttribute(Costanti.REQUEST_ATTRIBUTE_CSP_RANDOM_NONCE);
%>
<html>
<head>
	<jsp:include page="/jsplib/browserUtils.jsp" flush="true" />
	<link href="css/roboto/roboto-fontface.css" rel="stylesheet" type="text/css">
	<link rel="stylesheet" href="css/<%= gd.getCss() %>" type="text/css">
	<script type="text/javascript" src="js/webapps.js" nonce="<%= randomNonce %>"></script>
	<script type="text/javascript" nonce="<%= randomNonce %>">
		function exit() {
		  window.close();
		}
	</script>
	<link rel="shortcut icon" href="images/favicon.ico" type="image/x-icon" />
	<!-- JQuery lib-->
	<script type="text/javascript" src="js/jquery-latest.js" nonce="<%= randomNonce %>"></script>
	<script type="text/javascript" nonce="<%= randomNonce %>">
$(document).ready(function(){
	focusText(document.form);
});
</script>
</head>

<body marginwidth=0 marginheight=0>
	<form name="form">
		<table class="bodyWrapper">
			<tbody>
				<tr class="trPageBody">
					<td valign="top" class="td2PageBody">
						<%= pd.getMessage() %>
						<br/><br/>
						<input id="exitBtn" type="button" value="OK">&nbsp;
						<script type="text/javascript" nonce="<%= randomNonce %>">
						      $(document).ready(function(){
									$('#exitBtn').click(function() {
										exit();
									});
								});
						  </script>
					</td>
				</tr>
			</tbody>
		</table>
	</form>
</body>
</html>
