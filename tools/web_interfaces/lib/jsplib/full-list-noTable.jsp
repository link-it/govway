<%--
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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



<%@ page session="true" import="java.util.*, org.openspcoop2.web.lib.mvc.*" %>

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
String tabSessionKey = ServletUtils.getTabIdFromRequestAttribute(request);

String csrfTokenFromSession = ServletUtils.leggiTokenCSRF(request, session);
if(csrfTokenFromSession == null)
	csrfTokenFromSession = "";
%>

<td valign="top" class="td2PageBody">
<form name='form' method='post' onSubmit='return false;' id="form">

		<%
		if(!csrfTokenFromSession.equals("")){
			%>
			<input type="hidden" name="<%=Costanti.PARAMETRO_CSRF_TOKEN%>" id="<%=Costanti.PARAMETRO_CSRF_TOKEN%>"  value="<%= csrfTokenFromSession %>"/>
			<%			
		}
		%>
<%
Map<String,String> hidden = pd.getHidden();
if (hidden!=null && !hidden.isEmpty()) {
	for (String key : hidden.keySet()) {
	String value = (String) hidden.get(key);
	%><input type="hidden" name=<%= key %> value=<%= value %>><%
    }
}
%>


<jsp:include page="/jsplib/titlelist.jsp" flush="true">
	<jsp:param name="showListInfos" value="true"/>
</jsp:include>

<table class="tabella-ext">
	<!-- filtri di ricerca -->
	<jsp:include page="/jsplib/filtriRicerca.jsp" flush="true"/>

	<!-- spazio -->
	<tr> 
		<td valign=top>
			<div class="spacer"></div>
		</td>
	</tr>
	<!-- Riga tabella -->
		<tr> 
			<td valign=top>
				<div class="panelLista">
					<table class="tabella">
						<%
						String [] labels = pd.getLabels();
						int baseLength = pd.getSelect() ? 1 : 0;
						int labelLength = (labels != null ? labels.length : 1);
						int colSpanLength = labelLength + baseLength;
						
						List<?> v = pd.getDati();
						String stile= "";
						
						int index = pd.getIndex();
					  	if (pd.getNumEntries() > 0){
					    	index++;
						}
						String dataScrollerTopLabel = "Visualizzati record ["+index+"-"+(v.size()+pd.getIndex())+"] su " + pd.getNumEntries();
						%>
					
						<!-- data scroller top -->
						<tr>
							<td colspan="<%= colSpanLength %>">
								<div class="buttonrowdatascroller dsTop" align="center">
									<table>
										<tbody>
											<tr>
												<td>
													<%
													//Bottone Previous
													if (pd.getIndex() != 0) {
														%>							
														<img id="ds_prev_top" src="images/tema_link/go_prev.png" title="Precedente" alt="Precedente" class="dsImg" />
														<script type="text/javascript" nonce="<%= randomNonce %>">
															$(document).ready(function(){
																$('#ds_prev_top').click(function() {
																	<%= Costanti.JS_FUNCTION_VISUALIZZA_AJAX_STATUS %>PrevPage(document.form.limit.options[document.form.limit.selectedIndex].value);
																});
															});
														</script>
														<%
													} else{
														%>
														<img id="ds_prev_disabled_top" src="images/tema_link/go_prev_disabilitato.png" alt="Precedente">
														<%
													}
													%> 
												</td>
												<td>
													<span><%=dataScrollerTopLabel %></span>
												</td>
												<td>
													 <%
													
													//Bottone Next
													boolean nextTopDisabled = true;
													if (pd.getPageSize() != 0) {
													  if (pd.getIndex()+pd.getPageSize() < pd.getNumEntries()) {
														  nextTopDisabled = false;
													   			%>
													   			<img id="ds_next_top" src="images/tema_link/go_next.png" title="Successiva" alt="Successiva" class="dsImg"/>
													   			<script type="text/javascript" nonce="<%= randomNonce %>">
																	$(document).ready(function(){
																		$('#ds_next_top').click(function() {
																			<%= Costanti.JS_FUNCTION_VISUALIZZA_AJAX_STATUS %>NextPage();
																		});
																	});
																</script>
													   			<%
													  }
													}
													
													if (nextTopDisabled) {
														%>
															<img id="ds_next_disabled_top" src="images/tema_link/go_next_disabilitato.png" alt="Successiva" />
														<%
													}
													 %>
										 		</td>
											</tr>									
										</tbody>
									</table>
								</div>
							</td>
						</tr>
						
						<% if(labels!=null){ %>
						<tr class="tableHeader">
							<%
							if (v.size()> 0 && pd.getSelect()) {
							  %>
							  <td class="tableHeaderChkAll-noList">
							  	<div align="center">
							  		<input id="chkAll" type="checkbox" name="chkAll"/> 
							  		<script type="text/javascript" nonce="<%= randomNonce %>">
										$(document).ready(function(){
											$('#chkAll').click(function() {
												checkAll();
											});
										});
									</script> 
							  	</div>
							  </td>
							  <%
							}
							%>
					
							<%
								for (int i = 0; i < labelLength ;i++) {
								  %><td><span><%= labels[i] %></span></td><%
								}
							%>
					
						</tr>
					  <% }	%>
					<%
					//inizio entries
					
	
					for (int i = 0; i < v.size(); i++) {
						//per ogni entry:
						if ((i % 2) != 0) {
					    	stile = "odd";
					  	} else {
						    stile = "even";
					  	}
						%><tr class="<%= stile %>"><%
							//stringa contenente l identificativo dell'elemento che verra associato al checkbox per l eliminazione
							String idToRemove = null;	  
						  	// prelevo la riga 
						  	List<?> e = (List<?>) v.get(i);	  
						
						  	for (int j = 0; j < e.size(); j++) {
						  	    DataElement de = (DataElement) e.get(j);
								//if(de.getType().equals("text")) {
									// setto l'id per la rimozione in caso non sia settato, N.B. spostato qui perche ora le checkbox statnno a sx.
									if(idToRemove==null) idToRemove = de.getIdToRemove();
								//}
						  	}
						  	
							if (pd.getSelect()) {
								String checkBoxStyle = labels!=null ? "" : " tdText-16" ;
							   %>
								<td class="tdText<%=checkBoxStyle %>">
							   		<div align="center">
							   			<input id='_<% if(idToRemove!=null) out.write(idToRemove);else out.write(""+i); %>' type="checkbox" name="selectcheckbox" value='<% if(idToRemove!=null) out.write(idToRemove);else out.write(""+i); %>'/>
							   		</div>
							   	</td><%
							 }
					 	 %>
							<% if(customListViewName.equals("erogazioni")){ %>
								<jsp:include page="/jsp/list/erogazioni.jsp" flush="true">
									<jsp:param name="numeroEntry" value="<%=i %>"/>
								</jsp:include>
							<% } else if(customListViewName.equals("fruizioni")){ %>
								<jsp:include page="/jsp/list/fruizioni.jsp" flush="true">
									<jsp:param name="numeroEntry" value="<%=i %>"/>
								</jsp:include>
							<% } else if(customListViewName.equals("api")){ %>
								<jsp:include page="/jsp/list/api.jsp" flush="true">
									<jsp:param name="numeroEntry" value="<%=i %>"/>
								</jsp:include>
							<% } else if(customListViewName.equals("applicativi")){ %>
								<jsp:include page="/jsp/list/applicativi.jsp" flush="true">
									<jsp:param name="numeroEntry" value="<%=i %>"/>
								</jsp:include>
							<% } else if(customListViewName.equals("soggetti")){ %>
								<jsp:include page="/jsp/list/soggetti.jsp" flush="true">
									<jsp:param name="numeroEntry" value="<%=i %>"/>
								</jsp:include>
							<% } else if(customListViewName.equals("ruoli")){ %>
								<jsp:include page="/jsp/list/ruoli.jsp" flush="true">
									<jsp:param name="numeroEntry" value="<%=i %>"/>
								</jsp:include>
							<% } else if(customListViewName.equals("scope")){ %>
								<jsp:include page="/jsp/list/scope.jsp" flush="true">
									<jsp:param name="numeroEntry" value="<%=i %>"/>
								</jsp:include>
							<% } else if(customListViewName.equals("tokenPolicy")){ %>
								<jsp:include page="/jsp/list/tokenPolicy.jsp" flush="true">
									<jsp:param name="numeroEntry" value="<%=i %>"/>
								</jsp:include>
							<% } else if(customListViewName.equals("attributeAuthority")){ %>
								<jsp:include page="/jsp/list/attributeAuthority.jsp" flush="true">
									<jsp:param name="numeroEntry" value="<%=i %>"/>
								</jsp:include>
							<% } else if(customListViewName.equals("allarmi")){ %>
								<jsp:include page="/jsp/list/allarmi.jsp" flush="true">
								<jsp:param name="numeroEntry" value="<%=i %>"/>
							</jsp:include>
							<% } else if(customListViewName.equals("gruppi")){ %>
								<jsp:include page="/jsp/list/gruppi.jsp" flush="true">
								<jsp:param name="numeroEntry" value="<%=i %>"/>
							</jsp:include>
							<% } %>							
					 	 </tr><%
					}
					
					//fine entries
					
					//navigazione tra la pagine visualizzata se il numero di entries > 20
					if (pd.getNumEntries() > 20){
						%>
						<tr>
							<td colspan="<%= colSpanLength %>">
								<div class="buttonrowdatascroller dsBottom" align="center" >
									<table>
										<tbody>
											<tr>
											<td>
										<%
										//Bottone Previous
										if (pd.getIndex() != 0) {
											%>							
											<img id="ds_prev_bottom" src="images/tema_link/go_prev.png" title="Precedente" alt="Precedente" class="dsImg" />
											<script type="text/javascript" nonce="<%= randomNonce %>">
												$(document).ready(function(){
													$('#ds_prev_bottom').click(function() {
														<%= Costanti.JS_FUNCTION_VISUALIZZA_AJAX_STATUS %>PrevPage(document.form.limit.options[document.form.limit.selectedIndex].value);
													});
												});
											</script>
											<%
										} else{
											%>
											<img id="ds_prev_disabled_bottom" src="images/tema_link/go_prev_disabilitato.png" alt="Precedente">
											<%
										}
	
										//Scelta numero di entries da visualizzare
										if ((pd.getNumEntries() > 20) || (pd.getIndex() != 0)) {
										  %></td>
											<td><select id="ds_limit_bottom" name="limit"><%
										  switch (pd.getPageSize()) {
										    case 20 :
											%>
											<option value="20" selected="selected">20 Entries</option>
											<option value="75">75 Entries</option>
											<option value="125">125 Entries</option>
											<option value="250">250 Entries</option>
											<option value="500">500 Entries</option>
											<option value="1000">1000 Entries</option>
											<%
											break;
										    case 75 :
											%>
											<option value="20">20 Entries</option>
											<option value="75" selected="selected">75 Entries</option>
											<option value="125">125 Entries</option>
											<option value="250">250 Entries</option>
											<option value="500">500 Entries</option>
											<option value="1000">1000 Entries</option>
											<%
											break;
										    case 125 :
											%><option value="20">20 Entries</option>
											<option value="75">75 Entries</option>
											<option value="125" selected="selected">125 Entries</option>
											<option value="250">250 Entries</option>
											<option value="500">500 Entries</option>
											<option value="1000">1000 Entries</option><%
											break;
										    case 250 :
											%>
											<option value="20">20 Entries</option>
											<option value="75">75 Entries</option>
											<option value="125">125 Entries</option>
											<option value="250" selected="selected">250 Entries</option>
											<option value="500">500 Entries</option>
											<option value="1000">1000 Entries</option>
											<%
											break;
										    case 500 :
										    	%>
										    	<option value="20">20 Entries</option>
										    	<option value="75">75 Entries</option>
										    	<option value="125">125 Entries</option>
										    	<option value="250">250 Entries</option>
										    	<option value="500" selected="selected">500 Entries</option>
										    	<option value="1000">1000 Entries</option>
										    	<%
										    	break;
										    case 1000 :
										    	%>
										    	<option value="20">20 Entries</option>
										    	<option value="75">75 Entries</option>
										    	<option value="125">125 Entries</option>
										    	<option value="250">250 Entries</option>
										    	<option value="500">500 Entries</option>
										    	<option value="1000" selected="selected">1000 Entries</option>
										    	<%
										    	break;
										  }
										  %></select>
										  	<script type="text/javascript" nonce="<%= randomNonce %>">
													$(document).ready(function(){
														$('#ds_limit_bottom').change(function() {
															<%= Costanti.JS_FUNCTION_VISUALIZZA_AJAX_STATUS %>CambiaVisualizzazione(document.form.limit.options[this.selectedIndex].value);
														});
													});
												</script>
										  </td>
											<td><%
										} else {
											%></td>
											<td>&nbsp;</td>
											<td><%
										}
										//Bottone Next
										boolean nextBottomDisabled = true;
										if (pd.getPageSize() != 0) {
										  if (pd.getIndex()+pd.getPageSize() < pd.getNumEntries()) {
											  nextBottomDisabled = false;
										   			%>
										   			<img id="ds_next_bottom" src="images/tema_link/go_next.png" title="Successiva" alt="Successiva" class="dsImg"/>
										   			<script type="text/javascript" nonce="<%= randomNonce %>">
														$(document).ready(function(){
															$('#ds_next_bottom').click(function() {
																<%= Costanti.JS_FUNCTION_VISUALIZZA_AJAX_STATUS %>NextPage();
															});
														});
													</script>
										   			<%
										  }
										}
										
										if (nextBottomDisabled) {
											%>
												<img id="ds_next_disabled_bottom" src="images/tema_link/go_next_disabilitato.png" alt="Successiva" />
											<%
										}
										 %>
										 </td>
											</tr>									
										</tbody>
									</table>
										</div>
								</td>
							</tr>
						
						<%
					
					}
					
					//Bottoni 
					if (pd.getSelect()) {
						%>
						<tr>
							<td colspan=<%= colSpanLength %> class="buttonrow">
								<div class="buttonrowlista">
									<%
									List<?> areaBottoni = pd.getAreaBottoni();
									if (areaBottoni != null) {
										for (int i = 0; i < areaBottoni.size(); i++) {
									  		AreaBottoni area = (AreaBottoni) areaBottoni.get(i);
									  		List<?> bottoni = area.getBottoni();
									 		for (int b = 0; b < bottoni.size(); b++) {
									 			String id = "areaBottonBtn_" + i + "_" + b;
									   			DataElement bottone = (DataElement) bottoni.get(b);
									   			String visualizzaAjaxStatus = bottone.isShowAjaxStatus() ? Costanti.JS_FUNCTION_VISUALIZZA_AJAX_STATUS : "";
									   			%>
									   			<input id="<%=id %>" type="button" value='<%= bottone.getValue() %>'/>
									   			<script type="text/javascript" nonce="<%= randomNonce %>">
											      	 $(document).ready(function(){
															$('#<%=id %>').click(function() {
																<%= visualizzaAjaxStatus %><%= bottone.getOnClick() %>;
															});
														});
												</script>
									   			<%
									 		}
										}
									}
					
									//Bottone di Remove
									if (v.size() > 0 && pd.getRemoveButton()) {
									  %><input id='rem_btn' type="button" value='Elimina' class="negative" /><%
									}
									
									//Bottone di Add
									if (pd.getAddButton()) {
									  %><input id="aggiungiBtn" type="button" value='Aggiungi' />
									  	<script type="text/javascript" nonce="<%= randomNonce %>">
									      	 $(document).ready(function(){
									      		$('#aggiungiBtn').click(function() {
														<%= Costanti.JS_FUNCTION_VISUALIZZA_AJAX_STATUS %>AddEntry();
													});
												});
										</script>
									  <%
									}
									
									%>
								</div>
							</td>
						</tr>
						<% 
					}
					else{
						%>
						 <tr class="buttonrownobuttons">
						  <td colspan="<%= colSpanLength %>">&nbsp;</td>
					  	</tr>
					  	<%
					}
					%>
					</table>
				</div>
			</td>
		</tr>
		</table>
	</form>
</td>
