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



<%@page import="org.openspcoop2.web.lib.mvc.Dialog.BodyElement"%>
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

List<?> v = pd.getDati();

String numeroEntryS = request.getParameter("numeroEntry");
int numeroEntry = Integer.parseInt(numeroEntryS);

List<?> riga = (List<?>) v.get(numeroEntry);

List<DataElement> listRiepilogo = new ArrayList<DataElement>();
List<DataElement> listImmagini = new ArrayList<DataElement>();
List<DataElement> listCheckBox = new ArrayList<DataElement>();
List<DataElement> listTags = new ArrayList<DataElement>();
boolean visualizzaMetadati = false;

for (int j = 0; j < riga.size(); j++) {
    DataElement de = (DataElement) riga.get(j);
    
    if (de.getType().equals("image")) {
    	listImmagini.add(de);
   	} else  if (de.getType().equals("checkbox")) {
   		listCheckBox.add(de);
    } else if (de.getType().equals("button")) {
    	listTags.add(de);
    } else{
    	listRiepilogo.add(de);
    }
}

visualizzaMetadati = listRiepilogo.size() > 1;
%>
<td>
	<div id="entry_<%=numeroEntryS %>" class="entryApplicativi">
			<% 
				DataElement deTitolo = (DataElement) listRiepilogo.get(0);
				String deTitoloName = "url_entry_"+numeroEntry;
				String deTitoloValue = !deTitolo.getValue().equals("") ? deTitolo.getValue() : "&nbsp;";
				String visualizzaAjaxStatus = deTitolo.isShowAjaxStatus() ? Costanti.JS_FUNCTION_VISUALIZZA_AJAX_STATUS : "";
				%><input id="<%= deTitoloName  %>" type="hidden" name="<%= deTitoloName  %>" value="<%= deTitolo.getUrl()  %>"/>
				
					<script type="text/javascript" nonce="<%= randomNonce %>">
					   $('[id=entry_<%=numeroEntryS %>]')
					   .click(function() {
						   		<%= visualizzaAjaxStatus %>
								var val = $(this).children('input[id=url_entry_<%=numeroEntryS %>]').val();
								// addTabID
								val = addTabIdParam(val,true);
								window.location = val;
					       });
				   </script>
				<%
			%>
		<div id="titolo_<%=numeroEntryS %>" class="titoloEntry">
			<span class="titoloEntry"><%=deTitoloValue %>&nbsp;&nbsp;&nbsp;&nbsp;</span>	
			
			<% if(listTags.size() > 0){
				DataElement tag0 = listTags.get(0);
				String ruoliTooltip = !tag0.getToolTip().equals("") ? " title=\"" + tag0.getToolTip() + "\"" : "";
				String ruoliUrl = tag0.getUrl();
				
				%>
				<div id="titolo_<%=numeroEntryS %>_ruoli" class="titoloRuoli">
					<span class="titoloRuoli-image-sx" id="titolo_<%=numeroEntryS %>_ruoli_icona" <%=ruoliTooltip %> >
						<i class="material-icons md-18" id="iconInfo_titolo_<%=numeroEntryS %>_ruoli_icona"><%= Costanti.ICON_SUPERVISOR_ACCOUNT %></i>
					</span>
					<% for(int z = 0; z < listTags.size(); z ++){ 
						DataElement tag = listTags.get(z);
					%>
						<span class="ruolo label ruolo-label-info <%=tag.getStyleClass() %>" <%=ruoliTooltip %> 
							id="titolo_<%=numeroEntryS %>_ruoli_<%=z %>" ><%= tag.getLabel() %></span>
					<% } %>
					<script type="text/javascript" nonce="<%= randomNonce %>">
						// info
				    	if($("span[id^='titolo_<%=numeroEntryS %>_ruoli_']").length>0){
				    		$("span[id^='titolo_<%=numeroEntryS %>_ruoli_']").click(function(e){
				    			
				    			<%=Costanti.JS_FUNCTION_VISUALIZZA_AJAX_STATUS %>
				    			var val = '<%=ruoliUrl %>';
				    			// addTabID
								val = addTabIdParam(val,true);
								window.location = val;
				    			e.stopPropagation();
							});
				    	}
						</script>
				</div>
			<% } %>	
			
			<% if(listImmagini.size() > 0){
				String iconaComandiMenu = Costanti.ICONA_MENU_AZIONI_BUTTON;
				String tipComandiMenu = " title=\"" + Costanti.ICONA_MENU_AZIONI_BUTTON_TOOLTIP + "\"" ;
				String idDivIconMenu = "divIconMenu_"+numeroEntry;
				String idIconMenu = "iconMenu_"+numeroEntry; 
				String idSpanMenu = "spanIconMenu_"+numeroEntry;
				String idContextMenu = "contextMenu_"+numeroEntry;
				
				%>
				<div id="titolo_<%=numeroEntryS %>_info" class="titoloInfo">
				
					<div class="iconInfoBoxList" id="<%=idDivIconMenu %>" <%=tipComandiMenu %> >
    						<span class="icon-box" id="<%=idSpanMenu %>">
								<i class="material-icons md-18" id="<%=idIconMenu %>"><%= iconaComandiMenu %></i>
							</span>
   					</div>
   					
   					<% 
					// creazione elementi hidden necessari per visualizzare le modali
					for(int idxLink =0; idxLink < listImmagini.size() ; idxLink ++ ){
						DataElement de = (DataElement) listImmagini.get(idxLink);
						String deTip = !de.getToolTip().equals("") ? " title=\"" + de.getToolTip() + "\"" : "";
						String classLink = "";
						String numeroLink = numeroEntryS + "_" + idxLink;
						
						// gestione link che visualizzano la finestra modale
						if(de.getInfo() != null || de.getDialog() != null){
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
							for(int idxLink =0; idxLink < listImmagini.size() ; idxLink ++ ){
								DataElement de = (DataElement) listImmagini.get(idxLink);
								String deTip = !de.getToolTip().equals("") ? " title=\"" + de.getToolTip() + "\"" : "";
								String classLink = "";
								String numeroLink = numeroEntryS + "_" + idxLink;
								
								
								if(de.getInfo() != null || de.getDialog() != null){
									
									%>
									// add third item with function
						            contextMenu_<%=numeroEntry %>.menu().addItem('<%=de.getToolTip()%>', function () {
							            <%
										
										if(de.getInfo() != null) {
											%>
						    				var labelM_<%=numeroLink %> = $("#hidden_title_iconInfo_"+ '<%= numeroLink %>').val();
											var bodyM_<%=numeroLink %> = $("#hidden_body_iconInfo_"+ '<%= numeroLink %>').val();
											mostraDataElementInfoModal(labelM_<%=numeroLink %>,bodyM_<%=numeroLink %>);
							    			<%
										}
										
										if(de.getDialog() != null) {
											Dialog dialog = de.getDialog();
											request.setAttribute("idFinestraModale_"+numeroLink, de.getDialog());
											
											%>
											var urlD_<%= numeroLink %> = $("#hidden_title_iconUso_"+ '<%= numeroLink %>').val();
											
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
					    								var val = data.responseURL;
					    								document.location = val;
					    								<%=Costanti.JS_FUNCTION_NASCONDI_AJAX_STATUS %>
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
			</div> <% 
			}%>
		</div>
		<% 
		if(visualizzaMetadati){
			DataElement deMetadati = (DataElement) listRiepilogo.get(1);
			String deMetadatiValue = !deMetadati.getValue().equals("") ? deMetadati.getValue() : "&nbsp;";
			%>
			<div id="metadati_<%=numeroEntryS %>" class="metadatiEntry">
				<span class="metadatiEntry"><%=deMetadatiValue %>&nbsp;&nbsp;&nbsp;&nbsp;</span>
			</div>
		<%	
			}
		%>
	</div>
</td>

