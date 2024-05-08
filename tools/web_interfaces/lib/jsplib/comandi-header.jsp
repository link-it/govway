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



<%@page import="java.util.ArrayList"%>
<%@page import="org.openspcoop2.web.lib.mvc.Dialog.BodyElement"%>
<%@page import="java.util.List"%>
<%@page import="org.openspcoop2.web.lib.mvc.DataElement.STATO_APERTURA_SEZIONI"%>
<%@ page session="true" import="java.util.List, org.apache.commons.lang.StringEscapeUtils ,org.openspcoop2.web.lib.mvc.*" %>

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

List<DataElement> listaComandi = pd.getComandiAzioneBarraTitoloDettaglioElemento();

List<DataElement> listaComandiDaVisualizzareNellaBarra = new ArrayList<DataElement>();
List<DataElement> listaComandiDaVisualizzareNelMenu = new ArrayList<DataElement>();

for (DataElement de : listaComandi) {
	if (de.isContextMenu()) {
		listaComandiDaVisualizzareNelMenu.add(de);
	} else {
		listaComandiDaVisualizzareNellaBarra.add(de);
	}
}

String tabSessionKey = ServletUtils.getTabIdFromRequestAttribute(request);

String iconaComandiMenu = Costanti.ICONA_MENU_AZIONI_BUTTON;
String tipComandiMenu = " title=\"" + Costanti.ICONA_MENU_AZIONI_BUTTON_TOOLTIP + "\"" ;

String numeroEntry = "barraTitolo";
String idDivIconMenu = "divIconMenu_"+numeroEntry;
String idIconMenu = "iconMenu_"+numeroEntry; 
String idSpanMenu = "spanIconMenu_"+numeroEntry;
String idContextMenu = "contextMenu_"+numeroEntry;

String vCEFR = request.getParameter("visualizzaComandoEspandiFormRicerca");
boolean mostraComandoEspandiFormRicerca = false;

if(vCEFR != null){
	mostraComandoEspandiFormRicerca = Boolean.parseBoolean(vCEFR);
}

// il div per visualizzare i comandi affiancati si deve inserire se e' valida almeno una delle due condizioni:
	// 1. c'e' il comando per espandare il filtro di ricerca e almento una delle due liste di comandi non e' vuota
	// 2. entrambe le liste di comandi non sono vuote
boolean inserisciDivComandiAffiancati = 
	(mostraComandoEspandiFormRicerca && (!listaComandiDaVisualizzareNellaBarra.isEmpty() || !listaComandiDaVisualizzareNelMenu.isEmpty())) 
		|| (!listaComandiDaVisualizzareNellaBarra.isEmpty() && !listaComandiDaVisualizzareNelMenu.isEmpty());
%>

<td class="titoloSezione titoloSezione-right">
	<%
	// supporto alla visualizzazione delle icone dei comandi affiancati sulla barra
	if(inserisciDivComandiAffiancati) { 
	%>
		<div class="titoloSezioneDiv">
	<% } %>
		<% 
			// inseririsco tutti i comandi da visualizzare nella barra 
			for(int idxLink =0; idxLink < listaComandiDaVisualizzareNellaBarra.size() ; idxLink ++ ){
				DataElement de = (DataElement) listaComandiDaVisualizzareNellaBarra.get(idxLink);
				String deTip = !de.getToolTip().equals("") ? " title=\"" + de.getToolTip() + "\"" : "";
				String classLink = "";
				String deTarget = " ";
		  		if (!de.getTarget().equals("")) {
		  			deTarget = " target=\""+ de.getTarget() +"\"";
		  		}
		  		
		  		if (!de.getUrl().equals("")) {
					de.addParameter(new Parameter(Costanti.PARAMETER_PREV_TAB_KEY, tabSessionKey));
				}
		  		
		  		String deVisualizzaAjaxStatus = de.isShowAjaxStatus() ? Costanti.JS_FUNCTION_VISUALIZZA_AJAX_STATUS : "";
				
		  		String deIconName = de.getIcon(); 
		  		String id = "pnlRicercaHeader_link_" + idxLink;
		  		%>
  					<a id="<%=id %>" class="titoloSezioneAzioneLink <%= classLink %>" <%= deTip %> <%=deTarget %> href="<%= de.getUrl() %>" type="button">
  						<span class="icon-box">
						<i class="material-icons md-24"><%= deIconName %></i>
					</span>
  					</a>
  					<script type="text/javascript" nonce="<%= randomNonce %>">
			      	 $(document).ready(function(){
							$('#<%=id %>').click(function() {
								<%= deVisualizzaAjaxStatus %>return true;
							});
						});
				</script>
			<% 
			}
		%>
		<%
			// inserisco il comando per espandere i filtri di ricerca
			if(mostraComandoEspandiFormRicerca) { %>
			<span class="icon-box" id="iconaPanelListaSpan">
				<i class="material-icons md-24" id="iconaPanelLista"><%= Costanti.ICON_SEARCH %></i>
			</span>
		<% }%>
	
		<% 
			// inserisco i comandi da visualizzare nel menu' contestuale
			if(!listaComandiDaVisualizzareNelMenu.isEmpty()) { 
		%>
				<div class="iconInfoBoxList" id="<%=idDivIconMenu %>" <%=tipComandiMenu %> >
  						<span class="icon-box" id="<%=idSpanMenu %>">
						<i class="material-icons md-18" id="<%=idIconMenu %>"><%= iconaComandiMenu %></i>
					</span>
  					</div>
  					
  					<% 
				// creazione elementi hidden necessari per visualizzare le modali
				for(int idxLink =0; idxLink < listaComandiDaVisualizzareNelMenu.size() ; idxLink ++ ){
					DataElement de = (DataElement) listaComandiDaVisualizzareNelMenu.get(idxLink);
					String deTip = !de.getToolTip().equals("") ? " title=\"" + de.getToolTip() + "\"" : "";
					String classLink = "";
					String numeroLink = numeroEntry + "_" + idxLink;
					
					// gestione link che visualizzano la finestra modale
					if(de.getInfo() != null || de.getDialog() != null || de.getConfirm() != null){
						if(de.getInfo() != null) {
							DataElementInfo deInfo = de.getInfo();
							String idDivIconInfo = "divIconInfo_"+numeroLink;
							String idIconInfo = "iconInfo_"+numeroLink; 
							String idSpanInfo = "spanIconInfoBoxList_"+numeroLink;
					%>
					<input type="hidden" name="__i_hidden_title_<%= idIconInfo %>" id="hidden_title_<%= idIconInfo %>"  value="<%= deInfo.getHeaderFinestraModale() %>"/>
   					<input type="hidden" name="__i_hidden_body_<%= idIconInfo %>" id="hidden_body_<%= idIconInfo %>"  value="<%= deInfo.getBody() %>"/>
               
	                <%						
						}
						
						if(de.getDialog() != null) {
							Dialog dialog = de.getDialog();
							String idDivIconUso = "divIconUso_"+numeroLink;
							String idIconUso = "iconUso_"+numeroLink; 
							String idSpanUso = "spanIconUsoBoxList_"+numeroLink;
							
							BodyElement urlElement = dialog.getUrlElement();
							
							request.setAttribute("idFinestraModale_"+numeroLink, de.getDialog());
							
							String identificativoFinestraModale = "idFinestraModale_" + numeroLink;
						%>
						<input type="hidden" name="__i_hidden_title_<%= idIconUso %>" id="hidden_title_<%= idIconUso %>"  value="<%= urlElement.getUrl() %>"/>
						<jsp:include page="/jsplib/info-uso-modal.jsp" flush="true">
							<jsp:param name="idFinestraModale" value="<%=identificativoFinestraModale %>"/>
						</jsp:include>
						
						<%	
						}
						
						if(de.getConfirm() != null) {
							DataElementConfirm deConfirm = de.getConfirm();
							String idIconInfo = "iconAjax_"+numeroLink; 
						%>
						<input type="hidden" name="__i_hidden_title_<%= idIconInfo %>" id="hidden_title_<%= idIconInfo %>"  value="<%= deConfirm.getTitolo() %>"/>
	   					<input type="hidden" name="__i_hidden_body_<%= idIconInfo %>" id="hidden_body_<%= idIconInfo %>"  value="<%= deConfirm.getBody() %>"/>
	   					<input type="hidden" name="__i_hidden_url_<%= idIconInfo %>" id="hidden_url_<%= idIconInfo %>"  value="<%= de.getUrl() %>"/>
	               
		                <%						
							}
					} else { // link classico non sono necessarie risorse
					}
				}
                %>
  					<script type="text/javascript" nonce="<%= randomNonce %>">
					if($("#<%=idSpanMenu %>").length>0){
						// create context menu
			            var contextMenu_<%=numeroEntry %> = $('#<%=idSpanMenu %>').contextMenu();
						
			         	// set context menu button
			            contextMenu_<%=numeroEntry %>.button = mouseButton.LEFT;
						<% 
							for(int idxLink =0; idxLink < listaComandiDaVisualizzareNelMenu.size() ; idxLink ++ ){
								DataElement de = (DataElement) listaComandiDaVisualizzareNelMenu.get(idxLink);
								String deTip = !de.getToolTip().equals("") ? " title=\"" + de.getToolTip() + "\"" : "";
								String classLink = "";
								String numeroLink = numeroEntry + "_" + idxLink;
								
								
								if(de.getInfo() != null || de.getDialog() != null || de.getConfirm() != null){
									
									%>
									// add third item with function
						            contextMenu_<%=numeroEntry %>.menu().addItem('<%=de.getToolTip()%>', function () {
							            <%
										
										if(de.getInfo() != null) {
											%>
						    				var labelM_<%=numeroLink %> = $("#hidden_title_iconInfo_<%= numeroLink %>").val();
											var bodyM_<%=numeroLink %> = $("#hidden_body_iconInfo_<%= numeroLink %>").val();
											mostraDataElementInfoModal(labelM_<%=numeroLink %>,bodyM_<%=numeroLink %>);
							    			<%
										}
										
										if(de.getDialog() != null) {
											Dialog dialog = de.getDialog();
											request.setAttribute("idFinestraModale_"+numeroLink, de.getDialog());
											
											%>
											var urlD_<%= numeroLink %> = $("#hidden_title_iconUso_<%= numeroLink %>").val();
											
											// addTabID
											urlD_<%= numeroLink %> = addTabIdParam(urlD_<%= numeroLink %>,true);

						    				// chiamata al servizio
						    				<%=Costanti.JS_FUNCTION_VISUALIZZA_AJAX_STATUS %>
						    				
						    				$.ajax({
					    							url : urlD_<%= numeroLink %>,
					    							method: 'GET',
					    							async : false,
					    							success: function(data, textStatus, jqXHR){
					    								// inserimento del valore nella text area
									    				$("textarea[id^='idFinestraModale_<%=numeroLink %>_txtA']").val(data);
									    				
									    				<%=Costanti.JS_FUNCTION_NASCONDI_AJAX_STATUS %>
									    				// apertura modale
									    				var idToOpen = '#' + 'idFinestraModale_<%= numeroLink %>';
									    				$(idToOpen).dialog("open");
					    							},
					    							error: function(data, textStatus, jqXHR){
					    								<%=Costanti.JS_FUNCTION_NASCONDI_AJAX_STATUS %>
					    							}
					    						}
					    					);
							    			<%
						                }
										
										if(de.getConfirm() != null) {
											DataElementConfirm deConfirm = de.getConfirm();
											String idIconInfo = "iconAjax_"+numeroLink; 
																						
											%>
											var urlD_<%= numeroLink %> = $("#hidden_url_iconAjax_<%= numeroLink %>").val();
											
											// addTabID
											urlD_<%= numeroLink %> = addTabIdParam(urlD_<%= numeroLink %>,true);

						    				// chiamata al servizio
						    				<%=Costanti.JS_FUNCTION_VISUALIZZA_AJAX_STATUS %>
						    				
						    				$.ajax({
					    							url : urlD_<%= numeroLink %>,
					    							method: 'GET',
					    							async : false,
					    							dataType: 'json', 	
					    							success: function(data, textStatus, jqXHR){
									    				<%=Costanti.JS_FUNCTION_NASCONDI_AJAX_STATUS %>

									    				// visualizza modale di risultato ok
									    				mostraEsitoOperazioneAjaxModal(data);
					    							},
					    							error: function(data, textStatus, jqXHR){
					    								<%=Costanti.JS_FUNCTION_NASCONDI_AJAX_STATUS %>
					    								// visualizza messaggio di errore ricevuto dal server
					    								mostraEsitoOperazioneAjaxModal(data);
					    							}
					    						}
					    					);
							    			<%
						                }
										
										%>
						            });
									<%
									
								} else {
									
									String deVisualizzaAjaxStatus = de.isShowAjaxStatus() ? Costanti.JS_FUNCTION_VISUALIZZA_AJAX_STATUS : "";
									%>
									contextMenu_<%=numeroEntry %>.menu().addItem('<%=de.getToolTip()%>', function () {
										
										<%= deVisualizzaAjaxStatus %>
										
							             var val = '<%= de.getUrl() %>';
							             // addTabID
					                     val = addTabIdParam(val,true);
					                     document.location = val;

											
									 });	
									<%
								}
								
							}
						%>				                
			             // generate context menu
		             	contextMenu_<%=numeroEntry %>.init();	
				                
		                $("#<%=idSpanMenu %>").click(function(e){       
			            	e.stopPropagation();
						});
					}
				</script>
		<% 
		
		}%>
	

	<% if(inserisciDivComandiAffiancati) { %>
		</div>
	<% } %>
</td>


