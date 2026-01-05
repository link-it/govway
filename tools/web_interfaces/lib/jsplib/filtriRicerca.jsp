<%--
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2026 Link.it srl (https://link.it). 
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

List<GeneralLink> titlelist = pd.getTitleList();
String titoloSezione = Costanti.LABEL_TITOLO_SEZIONE_DEFAULT;
if (titlelist != null && titlelist.size() > 0) {
	
	int indexLabel = titlelist.size() -1;
	if(titlelist.size()==2 && Costanti.PAGE_DATA_TITLE_LABEL_RISULTATI_RICERCA.equals(titlelist.get(1).getLabel())){
		indexLabel = 0;
	}
	
	GeneralLink l = titlelist.get(indexLabel);
	titoloSezione = l.getLabel();
} 


boolean mostraFormHeader = (
		pd.getSearch().equals("on") || 
		(pd.getSearch().equals("auto") && (pd.getNumEntries() > pd.getSearchNumEntries()))
	) || 
	(
		pd.getFilterNames() != null &&
		pd.getFilterValues().size()>0
	);

boolean inserisciSearch = pd.isInserisciSearch();

List<DataElement> listaComandi = pd.getComandiAzioneBarraTitoloDettaglioElemento();

boolean mostraComandiHeader = mostraFormHeader || (listaComandi != null && listaComandi.size() > 0);

int colFormHeader = (mostraComandiHeader ? 2 : 1);
String classPanelTitolo = mostraFormHeader ? "panelListaRicerca" : "panelListaRicercaNoForm";
String tabSessionKey = ServletUtils.getTabIdFromRequestAttribute(request);
%>

<tr>
	<td valign=top>
		<div class="<%= classPanelTitolo %>" >
		<script type="text/javascript" nonce="<%= randomNonce %>">

$(document).ready(function () {

    $(".accordionSubtitleSearchSection").each(function () {
        const divEsternoId = $(this).attr("id"); // es: subtitle_de_name_1__divEsterno
        const baseId = divEsternoId.replace("__divEsterno", "");
        const sectionId = baseId + "__id";
        const anchorId = baseId + "__anchor";
        const iconId = baseId + "__icon";
        const accordionId = baseId + "__accordion";
        const tooltipNascondiSezione = '<%= Costanti.TOOLTIP_NASCONDI_SUBTITLE%>';
        const iconNascondiSezione = '<%= Costanti.ICON_NASCONDI_SUBTITLE%>';
        const tooltipVisualizzaSezione = '<%= Costanti.TOOLTIP_VISUALIZZA_SUBTITLE%>';
        const iconVisualizzaSezione = '<%= Costanti.ICON_VISUALIZZA_SUBTITLE%>';

        var aperto = $(this).hasClass("subtitleOpen");

        // Inizializza visibilità al load
        if (aperto) {
            $("#" + sectionId).show();
            $("#" + iconId).html(iconNascondiSezione);
            $("#" + iconId).attr("title", tooltipNascondiSezione);
            $("#" + anchorId).attr("title", tooltipNascondiSezione);
        } else {
            $("#" + sectionId).hide();
            $("#" + iconId).html(iconVisualizzaSezione);
            $("#" + iconId).attr("title", tooltipVisualizzaSezione);
            $("#" + anchorId).attr("title", tooltipVisualizzaSezione);
        }

     	// Toggle (indipendente per ogni sezione)
        $("#" + anchorId + ", #" + iconId).click(function () {
        	toggleSezione(baseId, iconVisualizzaSezione, iconNascondiSezione, tooltipVisualizzaSezione, tooltipNascondiSezione);
        });
    });
    
    function apriSezione(baseId, iconNascondiSezione, tooltipNascondiSezione) {
        const sectionId = baseId + "__id";
        const anchorId  = baseId + "__anchor";
        const iconId    = baseId + "__icon";
        const divId     = baseId + "__divEsterno";

        $("#" + sectionId).show();
        $("#" + iconId).html(iconNascondiSezione).attr("title", tooltipNascondiSezione);
        $("#" + anchorId).attr("title", tooltipNascondiSezione);
        $("#" + divId).removeClass("subtitleCollapsed").addClass("subtitleOpen");
        inizializzaSelectFiltro();
    }

    function chiudiSezione(baseId, iconVisualizzaSezione, tooltipVisualizzaSezione) {
        const sectionId = baseId + "__id";
        const anchorId  = baseId + "__anchor";
        const iconId    = baseId + "__icon";
        const divId     = baseId + "__divEsterno";

        $("#" + sectionId).hide();
        $("#" + iconId).html(iconVisualizzaSezione).attr("title", tooltipVisualizzaSezione);
        $("#" + anchorId).attr("title", tooltipVisualizzaSezione);
        $("#" + divId).removeClass("subtitleOpen").addClass("subtitleCollapsed");
    }

    function chiudiTutteLeSezioni(exceptBaseId, iconVisualizzaSezione, tooltipVisualizzaSezione) {
        $(".accordionSubtitleSearchSection").each(function () {
            const b = this.id.replace("__divEsterno", "");
            if (b === exceptBaseId) return;
            chiudiSezione(b, iconVisualizzaSezione, tooltipVisualizzaSezione);
        });
    }

    function toggleSezione(baseId, iconVisualizzaSezione, iconNascondiSezione, tooltipVisualizzaSezione, tooltipNascondiSezione) {
        const sectionId   = baseId + "__id";
        const accordionId = baseId + "__accordion";

        const isOpen       = $("#" + sectionId).is(":visible");
        const useAccordion = ($("#" + accordionId).val() === "true");

        if (useAccordion) {
        	// Accordion: se è già aperta non fare nulla (non si chiude)
            if (isOpen) {
                return; // keep-open behavior
            }
            // Se era chiusa, chiudi le altre e apri questa
            chiudiTutteLeSezioni(baseId, iconVisualizzaSezione, tooltipVisualizzaSezione);
            apriSezione(baseId, iconNascondiSezione, tooltipNascondiSezione);
        } else {
            // Modalità indipendente: toggle puro sulla singola sezione
            if (isOpen) {
                chiudiSezione(baseId, iconVisualizzaSezione, tooltipVisualizzaSezione);
            } else {
                apriSezione(baseId, iconNascondiSezione, tooltipNascondiSezione);
            }
        }
    }
    
});


</script>
		
			<table class="tabella" id="panelListaRicercaHeader">
				<tbody>
					<tr>
						<td class="titoloSezione" id="searchFormHeader" colspan="<%= colFormHeader %>">
							<span class="history"><%=titoloSezione %></span>
						</td>
						<% if(mostraComandiHeader) { %>
							<jsp:include page="/jsplib/comandi-header.jsp" flush="true" >
								<jsp:param value="<%=mostraFormHeader %>" name="visualizzaComandoEspandiFormRicerca"/>
							</jsp:include>
						<% }%>
					</tr>
					</tbody>
			</table>
			<% 
				if ( mostraFormHeader ) {
					String visualizzaAjaxStatusFiltra = pd.isShowAjaxStatusBottoneFiltra() ? Costanti.JS_FUNCTION_VISUALIZZA_AJAX_STATUS : "";
					String visualizzaAjaxStatusRipulisci = pd.isShowAjaxStatusBottoneRipulisci() ? Costanti.JS_FUNCTION_VISUALIZZA_AJAX_STATUS : "";
			%>
			<table class="tabella" id="searchForm">
				<tbody>
					<tr>
						<td class="spazioSottoTitolo">
							<span>&nbsp;</span>
						</td>
					</tr>
					<tr>
						<td>
						<%
						if (pd.getFilterValues() != null) {
							
							boolean searchPresente = false;
							if (pd.getSearch().equals("on") || (pd.getSearch().equals("auto") && (pd.getNumEntries() > pd.getSearchNumEntries()) )) {
								searchPresente = true;
							}
							
							// cerco il primo subtitle e conservo la posizione perche' deve essere preceduto da eventuali search;
							int idxSubtitle =-1;
							for(int iPD=0; iPD<pd.getFilterValues().size(); iPD++){
								DataElement filtro = pd.getFilterValues().get(iPD);
								String type = filtro.getType();
								if (type.equals("subtitle")){
									idxSubtitle = iPD;
									break;
								}
							}
							
							// se ho trovato un subtitle e la search allora la inserisco prima dal subtitle
							if(searchPresente && idxSubtitle != -1) {
								inserisciSearch = false;
								
								// creare id due elementi filtro fake da inserire nella lista dei filtri
								DataElement searchName = pd.convertiSearchInTextFilterMetadata();
								DataElement search = pd.convertiSearchInTextFilter();
								
								pd.getFilterNames().add(idxSubtitle, searchName);
								pd.getFilterValues().add(idxSubtitle, search);
								
							}
							
							boolean subtitleOpen = false;
							for(int iPD=0; iPD<pd.getFilterValues().size(); iPD++){
	
								DataElement filtroName = pd.getFilterNames().get(iPD);
								DataElement filtro = pd.getFilterValues().get(iPD);
								String filterName = filtro.getName();
								String filterId = filterName + "__id";
								String classInput = filtro.getStyleClass();
								String type = filtro.getType();
								String deLabel = !filtro.getLabel().equals("") ? filtro.getLabel() : "&nbsp;";
								String labelStyleClass= filtro.getLabelStyleClass();
								String rowName="row_"+filterName;
								String deLabelId = "de_label_"+iPD;
								String deNote = filtro.getNote();
								DataElementInfo deInfo = filtro.getInfo();
																
								if (type.equals("hidden")) {
						    		%>
						    		<input type="hidden" name="<%= filtroName.getName() %>" value="<%= filtroName.getValue() %>"/>
						    		<input type="hidden" id="<%= filterId  %>" name="<%= filterName  %>" value="<%= filtro.getValue()  %>"/>
				        			<%
				        		} else { // else subtitle
									if (type.equals("subtitle")){
										// se c'e' un altro field set aperto viene chiuso
					        			if(subtitleOpen){
					        				%>
					        				</div>
					            			<%
					            			subtitleOpen = false;
					        			}
										
					        			String cssClassSubtitle = "subtitle accordionSubtitleSearchSection " + labelStyleClass + " subtitleCollapsed";
					        			
					    				if(filtro.isVisualizzaSezioneAperta()){
					    					cssClassSubtitle = "subtitle accordionSubtitleSearchSection " + labelStyleClass + " subtitleOpen";
					    				}
					    				 boolean accordion = filtro.isModalitaAccordion();
					    				%>
					        			<div class="<%= cssClassSubtitle %>" id="<%= filterName  %>__divEsterno">
					        				<input type="hidden" name="<%= filtroName.getName() %>" value="<%= filtroName.getValue() %>"/>
					        				<input type="hidden" id="<%= filterName  %>__accordion" value="<%=accordion %>" />
					        				<span class="subtitleGroup">
					        					<span class="subtitleAnchor">
					        						<i class="material-icons md-16" id="<%= filterName  %>__icon" title="<%= Costanti.TOOLTIP_VISUALIZZA_SEZIONE_FILTRI_RICERCA%>"><%= Costanti.ICON_VISUALIZZA_SEZIONE_FILTRI_RICERCA%></i>
					        					</span>
					        					<a id="<%= filterName  %>__anchor" name="<%=rowName %>" class="subtitleAnchor" title="<%= Costanti.TOOLTIP_VISUALIZZA_SEZIONE_FILTRI_RICERCA%>"><%=deLabel %></a>
					        				</span>
					        			</div>
					        			<%
					        			
					        			if(!subtitleOpen){
					    	    			%>
					    	    				<div id="<%= filterId  %>">
					    	    			<%
					    	    			subtitleOpen = true;
					        			}
					        		} else { // else subtitle
					        			if (type.equals("textedit")){
			                				%>
			                    			<div class="prop">
			                    				<input type="hidden" name="<%= filtroName.getName() %>" value="<%= filtroName.getValue() %>"/>
			                    				<label class="<%= labelStyleClass %>" id="<%=deLabelId %>" for="<%= filterId  %>"><%=deLabel %></label>
			                    				<input id="<%= filterId  %>" type="text" name="<%= filterName %>" value="<%= filtro.getValue() %>" class="<%= classInput %>">
			                    				<% if(!deNote.equals("")){ %>
										      		<p class="note-ricerca <%= labelStyleClass %>"><%=deNote %></p>
										      	<% } %>
			                    			</div>
		                    			<% 
						        		} else { // else textedit
						        			if (type.equals("number")){
				                				%>
				                    			<div class="prop">
				                    				<input type="hidden" name="<%= filtroName.getName() %>" value="<%= filtroName.getValue() %>"/>
				                    				<label class="<%= labelStyleClass %>" id="<%=deLabelId %>" for="<%= filterId  %>"><%=deLabel %></label>
				                    				<%
				                    				String minvalue = filtro.getMinValue() != null ? " min=\"" + filtro.getMinValue() + "\"" : "";
										    		String maxValue = filtro.getMaxValue() != null ? " max=\"" + filtro.getMaxValue() + "\"" : "";
										    		String customJsFunction = filtro.getCustomJsFunction() != null && !filtro.getCustomJsFunction().equals("")  ? " gw-function=\"" + filtro.getCustomJsFunction() + "\"" : "";
										    		
										      		%><input id="<%= filterId  %>" type="number" name="<%= filterName %>" value="<%= filtro.getValue() %>" class="<%= classInput %>" <%=minvalue %> <%=maxValue %> <%=customJsFunction %> >
									      		</div>
										      	<%
						        			} else { 
						        				if (type.equals("textarea")){
			                        				%>
			                            			<div class="prop">
			                            				<input type="hidden" name="<%= filtroName.getName() %>" value="<%= filtroName.getValue() %>"/>
			                            				<label class="<%= labelStyleClass %>" id="<%=deLabelId %>" for="<%= filterId  %>"><%=deLabel %></label>
							     						<div class="txtA_div">
							     							<textarea id="<%=filterId %>" rows='<%= filtro.getRows() %>' cols='<%= filtro.getCols() %>' name="<%= filterName  %>" class="<%= classInput %>"><%= filtro.getValue() %></textarea>
						     							</div>
			                            			</div>
			                            			<%
			                            		} else { // else textarea || textarea-noedit
			                            			if (type.equals("select")){
			                            				String [] values = filtro.getValues();
			            							  	String [] labels = filtro.getLabels();
			            							  	String selezionato = filtro.getSelected();
			            								String selEvtOnChange = !filtro.getOnChange().equals("") ? ( Costanti.JS_FUNCTION_VISUALIZZA_AJAX_STATUS +"Change(document.form,'"+filterName+"',true);") : " ";
			            								
			            								%>
			    										
														<div class="prop">
															<input type="hidden" name="<%= filtroName.getName() %>" value="<%= filtroName.getValue() %>"/>
															<label for="<%= filterId  %>"><%= filtro.getLabel() %></label>
														  	<select id="<%= filterId  %>" name="<%= filterName %>" <%= selEvtOnChange %> class="<%= classInput %>">
															  	<%
															  	for (int i = 0; i < values.length; i++) {
															  		String optionSel = values[i].equals(selezionato) ? " selected " : " ";
															  		%><option value="<%= values[i]  %>" <%=optionSel %> ><%= labels[i] %></option><%
															  	}
															  	%></select>
															  	<%
															  	String abilitaSearch = "false";
													      		if(filtro.isAbilitaFiltroOpzioniSelect()){
													      			abilitaSearch = "true";
													      		} else {
													      			abilitaSearch = "false";
													      		}
													      		%>
												      		<input type="hidden" id="<%= filterId  %>_hidden_chk" value="<%= abilitaSearch  %>"/>
												      		<% if(!filtro.getOnChange().equals("")){ %>
												      		<script type="text/javascript" nonce="<%= randomNonce %>">
																$(document).ready(function(){
																	$('#<%= filterId  %>').change(function() {
																		<%=selEvtOnChange%>
																	});
																});
															</script>
															<% } %>
														</div>
														
														<%	
			                            			} else {// else select		
			                            				if (type.equals("checkbox")){
	                                        				%>
	                                            			<div class="prop">
	                                            				<input type="hidden" name="<%= filtroName.getName() %>" value="<%= filtroName.getValue() %>"/>
	                                            				
	                                            				<%
	                                            				String visualizzaAjaxStatus = filtro.isShowAjaxStatus() ? Costanti.JS_FUNCTION_VISUALIZZA_AJAX_STATUS : "";
						    									String chkVal = filtro.getSelected().equals("yes") ? " checked='true' " : " ";
						    									String disVal = pd.getMode().equals("view") || pd.getMode().equals("view-noeditbutton") ? "disabled=\"disabled\"" : "";
						    									String controlSetClass = deInfo != null ? "controlset-cb-info" : "controlset";
						    									String styleClass = filtro.getStyleClass();
						    									if(deInfo == null) {
						    										if(!deNote.equals("") && filtro.isLabelAffiancata()){
						    											controlSetClass = "controlset-cb-note-affiancate";
						    										}
						    									}
						    									String id = "checkbox_filtri_" + iPD;
						    									%>
						    									<label class="<%= labelStyleClass %>" id="<%=deLabelId %>" for="<%=id %>"><%=deLabel %></label>
					    										<table class="<%=controlSetClass %>">
				    													<tr> 
				    														<td>
						   														<input id="<%=id %>" type="checkbox" name="<%= filterName  %>" value="yes" <%=chkVal %> <%=disVal %> >
						   														<% if(!filtro.getOnClick().equals("")){ %>
						   															<script type="text/javascript" nonce="<%= randomNonce %>">
																				      	 $(document).ready(function(){
																								$('#<%=id %>').click(function() {
																									visualizzaAjaxStatus + filtro.getOnClick();
																								});
																							});
																					</script>
						   														<% } %>	
						   													</td>
						   													<% if(!filtro.getLabelRight().equals("")){ %>
						   													<td>
						   														<span class="controlset"><%=filtro.getLabelRight() %></span>
						   													</td>
						   													<% } %>
						   													<%
																	      		if(!deNote.equals("") && filtro.isLabelAffiancata()){
																	      			String idDivIconInfo = "divIconInfo_"+iPD;
																	      			String idIconInfo = "iconInfo_"+iPD; 
																	      	%> <td>	
																	      			<p class="note-checkbox-affiancata <%= styleClass %>"><%=deNote %></p>
																				</td>
																	      	<% } 
																	      	%>
					   													</tr>
					   												</table>
	                                            			</div>
	                                            			<%
	                                            		} else { // else checkbox
	                                            			if(type.equals("interval-number")){
	                                            				%>
	                                            					<div class="prop">
	                                            						<input type="hidden" name="<%= filtroName.getName() %>" value="<%= filtroName.getValue() %>"/>
									                    				<label class="<%= labelStyleClass %>" id="<%=deLabelId %>" for="<%= filterId  %>"><%=deLabel %></label>
									                    				<%
																    		String minvalue = filtro.getMinValue() != null ? " min=\"" + filtro.getMinValue() + "\"" : "";
																    		String maxValue = filtro.getMaxValue() != null ? " max=\"" + filtro.getMaxValue() + "\"" : "";
																    		String customJsFunction = filtro.getCustomJsFunction() != null && !filtro.getCustomJsFunction().equals("")  ? " gw-function=\"" + filtro.getCustomJsFunction() + "\"" : "";
																    		
																      		%>
																      		<div class="intervalExternalDiv">
																      		<%
																      			for(int z=0; z < filtro.getNames().length ; z++){
																      				String nameI = filtro.getNames()[z] == null ? "" : filtro.getNames()[z];
																      				String valueI = filtro.getValues()[z] == null ? "" : filtro.getValues()[z];
																      		%>
																      			<div class="intervalInnerDiv">
																      				<input id="<%=filterId %>" type="number"  name="<%= nameI %>" value="<%= valueI %>" class="<%= classInput %> intervalInnerInput" <%=minvalue %> <%=maxValue %> <%=customJsFunction %> >
																      			</div>
																      		<%
																	      		} // end for
																	      	%>
																      		</div>
									                    			</div>
	                                            				<%
	                                            			} else{ // else interval number			                                                			
	                                            				//fineelementi
	                                            			} // end else interval number
	                                            		} // end else checkbox
			                            			}
			                            		} // end else textarea
						        			}// end else number 
						        		} // end else textedit
					        		} // end else subtitle
				        		} // end else hidden
			        		} // end for
							
							if(subtitleOpen){ // se c'e' un fieldset aperto ed ho finito gli elementi devo chiudere
								%>
								</div>
								<%
							}
							// valutare se dopo aver disegnato tutto in pagina convenga eliminare le search dai filters
							
						} // if filtri
						
						%>
						</td>
					</tr>

					<%
					if (pd.getSearch().equals("on") || (pd.getSearch().equals("auto") && (pd.getNumEntries() > pd.getSearchNumEntries()) )) {
						if(inserisciSearch){
						DataElement filtro = pd.convertiSearchInTextFilter();
						String filterName = filtro.getName();
						String filterId = filterName + "__id";
						String classInput = filtro.getStyleClass();
						String type = filtro.getType();
						String deLabel = !filtro.getLabel().equals("") ? filtro.getLabel() : "&nbsp;";
						String labelStyleClass= filtro.getLabelStyleClass();
						String rowName="row_"+filterName;
						String deLabelId = "de_label_search";
						String deNote = filtro.getNote();
						
						%>
								<tr>
									<td>
										<div class="prop">
		                    				<label class="<%= labelStyleClass %>" id="<%=deLabelId %>" for="<%= filterId %>"><%=deLabel %></label>
		                    				<input id="<%=filterId %>" type="text" name="<%= filterName %>" value="<%= filtro.getValue() %>" class="<%= classInput %>">
		                    				<% if(!deNote.equals("")){ %>
									      		<p class="note-ricerca <%= labelStyleClass %>"><%=deNote %></p>
									      	<% } %>
		                    			</div>
									</td>
								</tr>	
					
					<% }  // end if attivazione search se non presente subtitle
					} // end if attivazione search da pagedata
					%>
					
							<tr>
								<td class="buttonrow">
									<div class="buttonrowricerca">
										<input id="eseguiRicercaBtn" type="button" value='<%=pd.getLabelBottoneFiltra() %>' />
										<input id="resetRicercaBtn" type="button" value='<%=pd.getLabelBottoneRipulsci() %>' />
										<script type="text/javascript" nonce="<%= randomNonce %>">
											$(document).ready(function(){
												$('#eseguiRicercaBtn').click(function() {
													<%=visualizzaAjaxStatusFiltra %>Search(document.form);
												});
												$('#resetRicercaBtn').click(function() {
													<%=visualizzaAjaxStatusRipulisci %>Reset(document.form);
												});
											});
										</script>
									</div>								
								
								</td>
							</tr>
				</tbody>
			</table>
			<% } %>
		</div>
	</td>
</tr>


