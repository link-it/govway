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



<%@page import="java.util.List"%>
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
else
  iddati = "notdefined";

GeneralData gd = ServletUtils.getObjectFromSession(request, session, GeneralData.class, gdString);
PageData pd = ServletUtils.getObjectFromSession(request, session, PageData.class, pdString);
String randomNonce = (String) request.getAttribute(Costanti.REQUEST_ATTRIBUTE_CSP_RANDOM_NONCE);

String ghf = request.getParameter("generateHiddenForm");
Boolean generateHiddenForm = false;
String hFormMethod = "";
if(ghf != null){
	generateHiddenForm = Boolean.parseBoolean(ghf);
}

if(generateHiddenForm)
	hFormMethod = "method='POST'" + "  action='" + gd.getUrl() +"'";

String [][] bottoni = pd.getBottoni();
boolean visualizzaPanelLista =((bottoni != null) && (bottoni.length > 0));

String classDivPanelLista = visualizzaPanelLista  ? "panelLista" : "";
String classTabellaPanelLista = visualizzaPanelLista  ? "tabella" : "";
String tabSessionKey = ServletUtils.getTabIdFromRequestAttribute(request);

String csrfTokenFromSession = ServletUtils.leggiTokenCSRF(request, session);
if(csrfTokenFromSession == null)
	csrfTokenFromSession = "";
%>
<td valign="top" class="td2PageBody">
	<form name="form"  <%=hFormMethod  %> >
		<%
		if(!csrfTokenFromSession.equals("")){
			%>
			<input type="hidden" name="<%=Costanti.PARAMETRO_CSRF_TOKEN%>" id="<%=Costanti.PARAMETRO_CSRF_TOKEN%>"  value="<%= csrfTokenFromSession %>"/>
			<%			
		}
		%>
	
		<jsp:include page="/jsplib/titlelist.jsp" flush="true" />
		<table class="tabella-ext">
		<!-- Riga tabella -->
			<tr> 
				<td valign=top>		
					<div class="<%=classDivPanelLista %>" >
						<table class="<%=classTabellaPanelLista %>">
							<tr class="buttonrow">
	  							<td colspan="2">
	  								<div class="buttonrowform">
										<%
										if(generateHiddenForm){
											List<?> dati = pd.getDati();
							
											for(int i = 0; i < dati.size() ; i++){
												DataElement de = (DataElement) dati.get(i);
												
												String type = de.getType();
												
												// tutti gli elementi che rappresentano lo stato sono stati convertiti in elementi hidden
												if(type.equals("hidden")){
													%><input type="hidden" name="<%= de.getName()  %>" value="<%= de.getValue()  %>"/><%
												}
											}
											
											if(!tabSessionKey.equals("")) {
												%><input type="hidden" name="<%=Costanti.PARAMETER_TAB_KEY %>" value="<%= tabSessionKey  %>"/><%
												%><input type="hidden" name="<%=Costanti.PARAMETER_PREV_TAB_KEY %>" value="<%= tabSessionKey %>"/><%
											}
										}
										
										
										if ((bottoni != null) && (bottoni.length > 0)) {
										  for (int i = 0; i < bottoni.length; i++) {
											  String id = "azioneBtn_" + i;
										    %><input id="<%=id %>" type="button" value="<%= bottoni[i][0] %>"/>&nbsp;
										    	<script type="text/javascript" nonce="<%= randomNonce %>">
											      $(document).ready(function(){
														$('#<%=id %>').click(function() {
															<%=Costanti.JS_FUNCTION_VISUALIZZA_AJAX_STATUS%><%= bottoni[i][1] %>
														});
													});
											  </script>
										    <%
										  }
										}
							  			%>
						  			</div>
							  	</td>
  							</tr>
  						</table>
					</div>
				</td>
			</tr>
		</table>
	</form>
</td>

