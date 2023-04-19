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



<%@ page session="true" import="java.util.List, java.util.ArrayList, org.apache.commons.lang.StringEscapeUtils ,org.openspcoop2.web.lib.mvc.*" %>

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
else
  iddati = "notdefined";
GeneralData gd = ServletUtils.getObjectFromSession(request, session, GeneralData.class, gdString);
PageData pd = ServletUtils.getObjectFromSession(request, session, PageData.class, pdString);
String randomNonce = (String) request.getAttribute(Costanti.REQUEST_ATTRIBUTE_CSP_RANDOM_NONCE);
String customListViewName = pd.getCustomListViewName();

boolean mime = false;

List<?> datiConGruppi = pd.getDati();
List<?> dati = datiConGruppi.size() >0 ? (List<?>) datiConGruppi.get(0) : new ArrayList<Object>();

if(dati.size() > 0){
	for (int i = 0; i < dati.size(); i++) {
	  DataElement de = (DataElement) dati.get(i);
	  if (de.getType().equals("file") || de.getType().equals("multi-file")) {
	    mime = true;
	  }
	}
}
String encTypeS = "";
if (mime) {
	encTypeS = "ENCTYPE=\"multipart/form-data\"";
}

String csrfTokenFromSession = ServletUtils.leggiTokenCSRF(request, session);
if(csrfTokenFromSession == null)
	csrfTokenFromSession = "";
%>


<td valign="top" class="td2PageBody">
	<form name="form" <%=encTypeS %> action="<%= gd.getUrl() %>" method="post">
		<!-- Breadcrumbs -->
		<jsp:include page="/jsplib/titlelist.jsp" flush="true" />
		
		<%
		if(!csrfTokenFromSession.equals("")){
			%>
			<input type="hidden" name="<%=Costanti.PARAMETRO_CSRF_TOKEN%>" id="<%=Costanti.PARAMETRO_CSRF_TOKEN%>"  value="<%= csrfTokenFromSession %>"/>
			<%			
		}
		%>

		<table class="tabella-ext">
			<% if(customListViewName.equals("erogazione")){ %>
				<jsp:include page="/jsp/form/erogazione.jsp" flush="true"/>
			<% } else if(customListViewName.equals("fruizione")){ %>
				<jsp:include page="/jsp/form/fruizione.jsp" flush="true"/>
			<% } else if(customListViewName.equals("api")){ %>
				<jsp:include page="/jsp/form/api.jsp" flush="true"/>
			<% } else if(customListViewName.equals("configurazione")){ %>
				<jsp:include page="/jsp/form/configurazione.jsp" flush="true"/>
			<% }  else if(customListViewName.equals("connettoriMultipli")){ %>
				<jsp:include page="/jsp/form/connettoriMultipli.jsp" flush="true"/>
			<% }  %>
		</table>
	</form>
</td>

