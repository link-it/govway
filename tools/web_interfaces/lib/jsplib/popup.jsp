<%--
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it). 
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
if (ct != null && (ct.indexOf("multipart/form-data") != -1)) {
  iddati = (String) session.getAttribute("iddati");
} else {
  iddati = request.getParameter("iddati");
}
String gdString = "GeneralData";
String pdString = "PageData";
if (iddati != null && !iddati.equals("notdefined")) {
  gdString += iddati;
  pdString += iddati;
}
else {
  iddati = "notdefined";
}
GeneralData gd = (GeneralData) session.getAttribute(gdString);
PageData pd = (PageData) session.getAttribute(pdString);
%>
<html>
<head>
	<jsp:include page="/jsplib/browserUtils.jsp" flush="true" />
	<link href="css/roboto/roboto-fontface.css" rel="stylesheet" type="text/css">
	<link rel="stylesheet" href="css/<%= gd.getCss() %>" type="text/css">
	<script type="text/javascript" src="js/webapps.js"></script>
	<script type="text/javascript">
		function exit() {
		  window.close();
		}
	</script>
	<link rel="shortcut icon" href="images/favicon.ico" type="image/x-icon" />
</head>

<body marginwidth=0 marginheight=0 onLoad="focusText(document.form);">
	<form name="form">
		<table class="bodyWrapper">
			<tbody>
				<tr class="trPageBody">
					<td valign="top" class="td2PageBody">
						<%= pd.getMessage() %>
						<br/><br/>
					<input type=button onClick=exit() value='OK'>&nbsp;</td>
				</tr>
			</tbody>
		</table>
	</form>
</body>
</html>
