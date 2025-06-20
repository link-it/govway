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
<!DOCTYPE html>
<%@page import="java.text.MessageFormat"%>
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

String styleClassColonnaSX = ""; // "td1PageBodyNoMenu"

String jQueryVersion = (String) request.getAttribute(Costanti.REQUEST_ATTRIBUTE_JQUERY_VERSION);
%>

<html lang="it">
<head>
	<meta charset="UTF-8">
	<title><%= gd.getTitle() %></title>
	<link href="css/roboto/roboto-fontface.css" rel="stylesheet" type="text/css">
	<link rel="stylesheet" href="css/<%= gd.getCss() %>" type="text/css">
	<!-- JQuery lib-->
	<script type="text/javascript" src="<%=MessageFormat.format(Costanti.LIB_JQUERY_PATH, jQueryVersion) %>" nonce="<%= randomNonce %>"></script>
	<script type="text/javascript" src="js/HtmlSanitizer.js" nonce="<%= randomNonce %>"></script>
	<jsp:include page="/jsplib/browserUtils.jsp" flush="true" />
	<script type="text/javascript" src="js/webapps.js" nonce="<%= randomNonce %>"></script>
	<jsp:include page="/jsplib/menuUtente.jsp" flush="true" />
	<link rel="shortcut icon" href="images/favicon.ico" type="image/x-icon" />
</head>
<body marginwidth=0 marginheight=0>
	<table class="bodyWrapper">
		<tbody>
			<jsp:include page="/jsplib/templateHeader.jsp" flush="true" />
			<!-- TR3: Body -->
			<tr class="trPageBody">
				<td valign=top class="<%= styleClassColonnaSX %>">&nbsp;</td>
				<jsp:include page="/jsplib/info-page.jsp" flush="true" />
			</tr>	
			<jsp:include page="/jsplib/templateFooter.jsp" flush="true" />
		</tbody>
	</table>
</body>
</html>
