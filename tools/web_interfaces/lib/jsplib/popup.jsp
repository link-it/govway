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
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page session="true" import="org.openspcoop2.web.lib.mvc.*" %>

<%
String iddati = "";
String ct = request.getContentType();
if (ct != null && (ct.indexOf("multipart/form-data") != -1)) {
  iddati = (String) session.getValue("iddati");
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
GeneralData gd = (GeneralData) session.getValue(gdString);
PageData pd = (PageData) session.getAttribute(pdString);
%>
<html>
<head>
<jsp:include page="/jsplib/browserUtils.jsp" flush="true" />
<link rel=stylesheet href=images/<%= gd.getCss() %> type=text/css>
<script type="text/javascript" src="js/webapps.js"></script>

<script LANGUAGE = JavaScript1.2>
function exit() {
  window.close();
}
</SCRIPT>
<link rel="shortcut icon" href="images/favicon.ico" type="image/x-icon" />
</head>

<body marginwidth=0 marginheight=0 onLoad="focusText(document.form);">
<table border=0 cellspacing=0 cellpadding=0 width=100% >
<tr>
<form name=form>
<td valign=top background=images/plugsx.gif class=corpoTesto>
<%= pd.getMessage() %>
<br><br>
<input type=button onClick=exit() value='OK'>&nbsp;</td>
</form>
</tr>
</table>
</body>
</html>
