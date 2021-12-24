<%--
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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



<%@page import="java.util.Vector"%>
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
else
  iddati = "notdefined";

GeneralData gd = (GeneralData) session.getAttribute(gdString);
PageData pd = (PageData) session.getAttribute(pdString);

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
%>
<td valign="top" class="td2PageBody">
	<form name="form"  <%=hFormMethod  %> >
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
											Vector<?> dati = pd.getDati();
							
											for(int i = 0; i < dati.size() ; i++){
												DataElement de = (DataElement) dati.get(i);
												
												String type = de.getType();
												
												// tutti gli elementi che rappresentano lo stato sono stati convertiti in elementi hidden
												if(type.equals("hidden")){
													%><input type="hidden" name="<%= de.getName()  %>" value="<%= de.getValue()  %>"/><%
												}
											}
										}
										
										
										if ((bottoni != null) && (bottoni.length > 0)) {
										  for (int i = 0; i < bottoni.length; i++) {
										    %><input type="button" onClick="<%= bottoni[i][1] %>" value="<%= bottoni[i][0] %>"/>&nbsp;<%
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

