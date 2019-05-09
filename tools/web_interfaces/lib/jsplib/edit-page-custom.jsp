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



<%@ page session="true" import="java.util.Vector, org.apache.commons.lang.StringEscapeUtils ,org.openspcoop2.web.lib.mvc.*" %>

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
else
  iddati = "notdefined";
GeneralData gd = (GeneralData) session.getAttribute(gdString);
PageData pd = (PageData) session.getAttribute(pdString);
String customListViewName = pd.getCustomListViewName();

boolean mime = false;

Vector<?> datiConGruppi = pd.getDati();
Vector<?> dati = (Vector<?>) datiConGruppi.elementAt(0);

for (int i = 0; i < dati.size(); i++) {
  DataElement de = (DataElement) dati.elementAt(i);
  if (de.getType().equals("file")) {
    mime = true;
  }
}
String encTypeS = "";
if (mime) {
	encTypeS = "ENCTYPE=\"multipart/form-data\"";
}
%>


<td valign="top" class="td2PageBody">
	<form name="form" <%=encTypeS %> action="<%= gd.getUrl() %>" method="post">
		<!-- Breadcrumbs -->
		<jsp:include page="/jsplib/titlelist.jsp" flush="true" />

		<table class="tabella-ext">
			<% if(customListViewName.equals("erogazione")){ %>
				<jsp:include page="/jsp/form/erogazione.jsp" flush="true"/>
			<% } else if(customListViewName.equals("fruizione")){ %>
				<jsp:include page="/jsp/form/fruizione.jsp" flush="true"/>
			<% } else if(customListViewName.equals("api")){ %>
				<jsp:include page="/jsp/form/api.jsp" flush="true"/>
			<% } else if(customListViewName.equals("configurazione")){ %>
				<jsp:include page="/jsp/form/configurazione.jsp" flush="true"/>
			<% }  %>
		</table>
	</form>
</td>

