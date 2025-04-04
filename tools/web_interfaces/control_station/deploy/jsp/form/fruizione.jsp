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



<%@page import="java.text.MessageFormat"%>
<%@page import="org.apache.commons.lang3.StringUtils"%>
<%@page import="org.openspcoop2.web.lib.mvc.Dialog.BodyElement"%>
<%@page import="java.util.List"%>
<%@page import="org.openspcoop2.web.ctrlstat.servlet.aps.erogazioni.ErogazioniCostanti"%>
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
	
	List<?> datiConGruppi = pd.getDati();
	List<?> dati = (List<?>) datiConGruppi.get(0);
	
	boolean visualizzaPanelLista = !pd.isPageBodyEmpty();
	
	String classDivPanelLista = visualizzaPanelLista  ? "panelLista" : "";
	String classTabellaPanelLista = visualizzaPanelLista  ? "tabella" : "";
	
	List<GeneralLink> titlelist = pd.getTitleList();
	String titoloSezione = null;
	if (titlelist != null && titlelist.size() > 0) {
		GeneralLink l = titlelist.get(titlelist.size() -1);
		titoloSezione = l.getLabel();
		
		if(titoloSezione != null && titoloSezione.equals(Costanti.PAGE_DATA_TITLE_LABEL_AGGIUNGI))
			titoloSezione = null;
	} 
	List<DataElement> listaComandi = pd.getComandiAzioneBarraTitoloDettaglioElemento();
	boolean mostraComandiHeader = listaComandi != null && listaComandi.size() > 0;
	int colFormHeader = (mostraComandiHeader ? 2 : 1);
	String classPanelTitolo = "panelDettaglioNoForm";
	
	List<DataElement> listRiepilogo = new ArrayList<DataElement>();
	List<DataElement> listLink = new ArrayList<DataElement>();
	
	for (int j = 0; j < dati.size(); j++) {
	    DataElement de = (DataElement) dati.get(j);
	    
	    if (de.getType().equals("link")) {
	    	listLink.add(de);
	    } else {
	    	listRiepilogo.add(de);
	    }
	}
	
	String classSpanNoEdit="spanNoEdit";
	String classDivNoEdit="divNoEdit";
	
    boolean visualizzaIconeListLink = false;
    String numeroEntry = "dettaglio";
    String tabSessionKey = ServletUtils.getTabIdFromRequestAttribute(request);
%>
<tbody>
	<% if(titoloSezione != null) { %>
		<tr>
			<td valign=top colspan="2">
				<div class="<%= classPanelTitolo %>" >
					<table class="tabella" id="panelDettaglioHeader">
						<tbody>
							<tr>	
								<td class="titoloSezione" id="dettaglioFormHeader" colspan="<%= colFormHeader %>">
									<span class="history"><%= titoloSezione %></span>
								</td>
								<% if(mostraComandiHeader) { %>
									<jsp:include page="/jsplib/comandi-header.jsp" flush="true" />
								<% } %>
							</tr>
						</tbody>
					</table>
				</div>
			</td>
		</tr>
		<% }%>
		<!-- Riga tabella -->
		<tr> 
			<td valign=top colspan="2">		
				<div class="<%=classDivPanelLista %>">
					<table class="<%=classTabellaPanelLista %>">
					<%
						%><tr class="even">
							<td colspan="2">
							
							
							<table class="<%=classTabellaPanelLista %>">
								<%
									boolean firstText = true;
							for (int i = 0; i < listRiepilogo.size(); i++) {
								DataElement de = (DataElement) listRiepilogo.get(i);
							  
								String deName = !de.getName().equals("") ? de.getName() : "de_name_"+i;
							  	String type = de.getType();
							  	String rowName="row_"+deName;
							  	String deLabel = !de.getLabel().equals("") ? de.getLabel() : "&nbsp;";
							  	String deNote = de.getNote();
							  	String classInput= de.getStyleClass();
							  	String labelStyleClass= de.getLabelStyleClass();
							  	String deHiddenId = "__i_hidden_lbl_de_"+i;
							  	String deCopyId = "__i_hidden_copy_de_"+i;
							  	
							  	String stile=null;
							  	//per ogni entry:
								if ((i % 2) != 0) {
							    	stile = "odd";
							  	} else {
								    stile = "even";
							  	}
								
							  	String copyToClipboard = de.getCopyToClipboard();
							  	
							  	if (type.equals("hidden")) {
						    		%>
						    			<tr>
											<td colspan="2">
						    					<input type="hidden" name="<%= deName  %>" value="<%= de.getValue()  %>"/>
					    					</td>
				    					</tr>
			    					<%
						    	} else { // else hidden
						    		if (type.equals("subtitle")){
					    				%>
					    				<tr>
											<td colspan="2">
							        			<div class="subtitle <%= labelStyleClass %>">
							        				<span class="subtitle"><%=deLabel %>&nbsp;&nbsp;&nbsp;&nbsp;</span>
							        			</div>
						        			</td>
										</tr>
		        					<%
					        		} else { // else subtitle		
					        			if (type.equals("text")){
				            				String textValNoEdit = de.getValue() != null && !de.getValue().equals("") ? de.getValue() : (pd.getMode().equals("view-noeditbutton") ? "&nbsp;" : "not defined");
				            				
				            				String tooltipTextValNoEdit = "";
											
				            				if(de.getToolTip()!=null && !de.getToolTip().equals("")){
												tooltipTextValNoEdit = " title=\"" + de.getToolTip() + "\"";	
											}
				            				
											if(textValNoEdit.length() > Costanti.LUNGHEZZA_RIGA_TESTO_TABELLA) {
												if(tooltipTextValNoEdit==null || "".equals(tooltipTextValNoEdit)){
													tooltipTextValNoEdit = " title=\"" + textValNoEdit + "\"";
												}
												textValNoEdit = textValNoEdit.substring(0,(Costanti.LUNGHEZZA_RIGA_TESTO_TABELLA -3)) + "...";
											}	
											
											String deTextId = rowName+"_txt";
											String dataCopy = "";
											
											// valore da copiare negli appunti
											if(StringUtils.isNotEmpty(copyToClipboard)){
												dataCopy = " data-copy=\"" + copyToClipboard + "\"";		
// 												classSpanNoEdit = "	 spanNoEdit-copy-box";
											}
											
				            				%>
				                			<tr class="">
												<td class="tdTextRiepilogo labelRiepilogo">
													<label class="<%= labelStyleClass %>" for="<%= deHiddenId%>"><%=deLabel %></label>
												</td>
												<td class="tdTextRiepilogo <%= stile %>">
													<div class="<%=classDivNoEdit %>"> 
														<input type="hidden" name="<%= deHiddenId %>" value="" id="<%= deHiddenId%>"/>
						                				<span class="<%=classSpanNoEdit %>" <%= tooltipTextValNoEdit %>  <%= dataCopy %> id="<%= deTextId%>"><%= textValNoEdit %></span>
						                				<input type="hidden" name="<%= deName %>" value="<%= de.getValue() %>"/>
						                				
						                			<% if(StringUtils.isNotEmpty(copyToClipboard)){ %>
						                				<span class="copy-box" id="<%= deCopyId%>" title="<%=MessageFormat.format(Costanti.ICON_COPY_TOOLTIP_CON_PARAMETRO, deLabel) %>">
															<i class="material-icons md-18"><%= Costanti.ICON_COPY %></i>
														</span>
														
        												<div id="<%= deCopyId%>_message" class="copy-message"><%=Costanti.ICON_COPY_ESITO_OPERAZIONE %></div>
														<script type="text/javascript" nonce="<%= randomNonce %>">
													      	 $(document).ready(function(){
													      		setupCopyButtonEvents('<%= deTextId%>', '<%= deCopyId%>', '<%= deCopyId%>_message'); // imposta gestione eventi per visualizzazione tasto copia
															});
														</script>
													<% } %>
													<% 
														if(!de.getImage().isEmpty()){
															for(int idxLink =0; idxLink < de.getImage().size() ; idxLink ++ ){
																DataElementImage image = de.getImage().get(idxLink);
																String classLink = "";
																String deIconName = image.getImage(); 
					                					
																String deTip = !image.getToolTip().equals("") ? " title=\"" + image.getToolTip() + "\"" : "";
					                							
					                							String deTarget = " ";
														  		if (!image.getTarget().equals("")) {
														  			deTarget = " target=\""+ image.getTarget() +"\"";
														  		}
														  		
														  		if (!image.getUrl().equals("")) {
																	image.addParameter(new Parameter(Costanti.PARAMETER_PREV_TAB_KEY, tabSessionKey));
																}
													  			
														  		String visualizzaAjaxStatus = image.isShowAjaxStatus() ? Costanti.JS_FUNCTION_VISUALIZZA_AJAX_STATUS : "";
														  		String id = "form-image-link_" + i + "_" + idxLink;
						                					%>
						                					<a id="<%=id %>" class="edit-link <%= classLink %>" <%= deTip %> <%=deTarget %> href="<%= image.getUrl() %>" type="button">
						                						<span class="icon-box">
																	<i class="material-icons md-18"><%= deIconName %></i>
																</span>
						                					</a>
						                					<script type="text/javascript" nonce="<%= randomNonce %>">
															      	 $(document).ready(function(){
																			$('#<%=id %>').click(function() {
																				<%= visualizzaAjaxStatus %>return true;
																			});
																		});
																</script>
						                				<%
															}// end for-edit-link
														} // end edit-link
													%>
													</div>
												</td>
											</tr>
				                			<%
				                				firstText = false;
					                		} else { // else text
					                			if (type.equals("checkbox")){
					                				String statusValue = de.getStatusValues() != null && de.getStatusValues().length>0 ? de.getStatusValues()[0] : "";
					                				String statusValueText = statusValue != null && !statusValue.equals("") ? statusValue : (pd.getMode().equals("view-noeditbutton") ? "&nbsp;" : "not defined");
																	
					                				String statusTooltip = de.getStatusToolTips() != null && de.getStatusToolTips().length>0 ? de.getStatusToolTips()[0] : "";		
					                				String statusTooltipTitleAttribute = statusTooltip != null && !statusTooltip.equals("") ? " title=\"" + statusTooltip + "\"" : "";
					                				
													String statusType = de.getStatusTypes() != null && de.getStatusTypes().length>0 ? de.getStatusTypes()[0] : "";			
					                					
													%>
					                					<tr class="">
															<td class="tdTextRiepilogo labelRiepilogo">
																<label class="<%= labelStyleClass %>" for="<%= deHiddenId%>"><%=deLabel %></label>
															</td>
															<td class="tdTextRiepilogo <%= stile %>">
															<div class="<%=classDivNoEdit %>"> 
															<input type="hidden" name="<%= deHiddenId %>" value="" id="<%= deHiddenId%>"/>
																<%  
																	String imageCheckBox = "status_red.png";
																 	if("yes".equals(statusType)){
																 		imageCheckBox = "status_green.png";
																	}
																	else if("warn".equals(statusType)){
																		imageCheckBox = "status_yellow.png";
																	}
																	else if("off".equals(statusType)){
																		imageCheckBox = "disconnected_grey.png";
																	}
																	else if("config_enable".equals(statusType)){
																		imageCheckBox = "verified_green.png";
																	}
																	else if("config_warning".equals(statusType)){
																		imageCheckBox = "verified_yellow.png";
																	}
																	else if("config_error".equals(statusType)){
																		imageCheckBox = "verified_red.png";
																	}
																 	else if("config_disable".equals(statusType)){
																		imageCheckBox = "verified_grey.png";
																	}
																	%>
																	<span class="<%=classSpanNoEdit %>-image" <%= statusTooltipTitleAttribute %> id="iconTitoloLeft-<%=i%>">
																		<img src="images/tema_link/<%= imageCheckBox %>" alt="Stato"/>
																	</span>
									                				<span class="<%=classSpanNoEdit %>" <%= statusTooltipTitleAttribute %> ><%= statusValueText %></span>
									                				<% if(firstText){%>
										                				<input type="hidden" name="<%= deName %>" id="<%= deName %>"  value="<%= de.getValue() %>"/>
								                					<% } %>
																	 <% 
																	if(!de.getImage().isEmpty()){
																		for(int idxLink =0; idxLink < de.getImage().size() ; idxLink ++ ){
																			DataElementImage image = de.getImage().get(idxLink);
																			String classLink = "";
																			String deIconName = image.getImage(); 
								                					
																			String deTip = !image.getToolTip().equals("") ? " title=\"" + image.getToolTip() + "\"" : "";
								                							
								                							String deTarget = " ";
																	  		if (!image.getTarget().equals("")) {
																	  			deTarget = " target=\""+ image.getTarget() +"\"";
																	  		}
																	  		
																	  		if (!image.getUrl().equals("")) {
																				image.addParameter(new Parameter(Costanti.PARAMETER_PREV_TAB_KEY, tabSessionKey));
																			}
																  			
																	  		String visualizzaAjaxStatus = image.isShowAjaxStatus() ? Costanti.JS_FUNCTION_VISUALIZZA_AJAX_STATUS : "";
																	  		String id = "form-image-link_" + i + "_" + idxLink;
									                					%>
									                					<a id="<%=id %>" class="edit-link <%= classLink %>" <%= deTip %> <%=deTarget %> href="<%= image.getUrl() %>" type="button">
									                						<span class="icon-box">
																				<i class="material-icons md-18"><%= deIconName %></i>
																			</span>
									                					</a>
									                					<script type="text/javascript" nonce="<%= randomNonce %>">
																	      	 $(document).ready(function(){
																					$('#<%=id %>').click(function() {
																						<%= visualizzaAjaxStatus %>return true;
																					});
																				});
																		</script>
									                				<%
																		}// end for-edit-link
																	} // end edit-link
																%>
																</div>
															</td>
														</tr>
													<% 
					                			} else { // else checkbox
					                				if (type.equals("multi-select")){
					                					%>
					                					<tr class="">
															<td class="tdTextRiepilogo labelRiepilogo">
																<label class="<%= labelStyleClass %>" for="<%= deHiddenId%>"><%=deLabel %></label>
															</td>
															<td class="tdTextRiepilogo <%= stile %>">
																<div class="<%=classDivNoEdit %>"> 	
																<input type="hidden" name="<%= deHiddenId %>" value="" id="<%= deHiddenId%>"/>																
																	<%
			                          									String [] values = de.getStatusValues();
			                                        					if (values != null) {
			                            									String [] labels = de.getLabels();
			                            									for (int y = 0; y < values.length; y++) {
			                            										String statusType = de.getStatusTypes()!=null && de.getStatusTypes().length>0 ? de.getStatusTypes()[y] : null; // valore icona
			                            										
			                            										String statusTooltip = de.getStatusToolTips()!=null && de.getStatusToolTips().length>0 ?  de.getStatusToolTips()[y] : null; // tooltip
			                            										String statusTooltipTitleAttribute = statusTooltip != null && !statusTooltip.equals("") ? " title=\"" + statusTooltip + "\"" : "";
												                				
			                            										String lab = values[y]; // testo configurazione
			                            										
			                            										String imageCheckBox = "status_red.png";
																			 	if("yes".equals(statusType)){
																			 		imageCheckBox = "status_green.png";
																				}
																				else if("warn".equals(statusType)){
																					imageCheckBox = "status_yellow.png";
																				}
																				else if("off".equals(statusType)){
																					imageCheckBox = "disconnected_grey.png";
																				}
																				else if("config_enable".equals(statusType)){
																					imageCheckBox = "verified_green.png";
																				}
																				else if("config_warning".equals(statusType)){
																					imageCheckBox = "verified_yellow.png";
																				}
																				else if("config_error".equals(statusType)){
																					imageCheckBox = "verified_red.png";
																				}
																			 	else if("config_disable".equals(statusType)){
																					imageCheckBox = "verified_grey.png";
																				}
			                            											%>
			                            												<span class="<%=classSpanNoEdit %>-image-msval" <%= statusTooltipTitleAttribute %> id="iconTitoloLeft-<%=i%>_<%=y%>">
																							<img src="images/tema_link/<%= imageCheckBox %>" alt="Stato"/>
																						</span>
																						<span class="<%=classSpanNoEdit %>-msval" <%= statusTooltipTitleAttribute %> ><%= lab %></span>
																					<%
			                            									} //end for values
			                                        					}
		                          									%>
															
																	<% 
																		if(!de.getImage().isEmpty()){
																			for(int idxLink =0; idxLink < de.getImage().size() ; idxLink ++ ){
																				DataElementImage image = de.getImage().get(idxLink);
																				String classLink = "";
																				String deIconName = image.getImage(); 
									                					
																				String deTip = !image.getToolTip().equals("") ? " title=\"" + image.getToolTip() + "\"" : "";
									                							
									                							String deTarget = " ";
																		  		if (!image.getTarget().equals("")) {
																		  			deTarget = " target=\""+ image.getTarget() +"\"";
																		  		}
																		  		
																		  		if (!image.getUrl().equals("")) {
																					image.addParameter(new Parameter(Costanti.PARAMETER_PREV_TAB_KEY, tabSessionKey));
																				}
																		  		
																		  		String visualizzaAjaxStatus = image.isShowAjaxStatus() ? Costanti.JS_FUNCTION_VISUALIZZA_AJAX_STATUS : "";
																		  		String id = "form-image-link_" + i + "_" + idxLink;
										                					%>
										                					<a id="<%=id %>" class="edit-link <%= classLink %>" <%= deTip %> <%=deTarget %> href="<%= image.getUrl() %>" type="button">
										                						<span class="icon-box">
																					<i class="material-icons md-18"><%= deIconName %></i>
																				</span>
										                					</a>
										                					<script type="text/javascript" nonce="<%= randomNonce %>">
																		      	 $(document).ready(function(){
																						$('#<%=id %>').click(function() {
																							<%= visualizzaAjaxStatus %>return true;
																						});
																					});
																			</script>
											                				<%
																			}// end for-edit-link
																		} // end edit-link
																	%>
					                							</div>
															</td>
														</tr>
														<% 
														firstText = false;
													} else { // else multi-select
														if (type.equals("button")){
															String textValNoEdit = de.getValue() != null && !de.getValue().equals("") ? de.getValue() : (pd.getMode().equals("view-noeditbutton") ? "&nbsp;" : "not defined");
								            				
								            				String tooltipTextValNoEdit = "";
															
															if(textValNoEdit.length() > Costanti.LUNGHEZZA_RIGA_TESTO_TABELLA) {
																tooltipTextValNoEdit = " title=\"" + textValNoEdit + "\"";
																textValNoEdit = textValNoEdit.substring(0,(Costanti.LUNGHEZZA_RIGA_TESTO_TABELLA -3)) + "...";
																
															}
															%>
						                					<tr class="">
																<td class="tdTextRiepilogo labelRiepilogo">
																	<label class="<%= labelStyleClass %>" for="<%= deHiddenId%>"><%=deLabel %></label>
																</td>
																<td class="tdTextRiepilogo <%= stile %>">
																	<div class="<%=classDivNoEdit %>"> 	
																		<input type="hidden" name="<%= deHiddenId %>" value="" id="<%= deHiddenId%>"/>																																																		
																		<span class="<%=classSpanNoEdit %>" <%= tooltipTextValNoEdit %> ><%= textValNoEdit %>&nbsp;&nbsp;&nbsp;&nbsp;</span>
						                								<input type="hidden" name="<%= deName %>" value="<%= de.getValue() %>"/>																
																		<%
																			String [] values = de.getValues();
				                                        					if (values != null) {
				                            									String [] labels = de.getLabels();
				                            									if(values.length > 0){
				                            										%>
																					<div class="<%=classDivNoEdit %> titoloTags"> 	
																						<% 
					                            										
						                            									for (int y = 0; y < values.length; y++) {
						                            										%>
						                            										<span class="tag label label-info <%=values[y] %>"><%= labels[y] %></span>
						                            										<%
						                            									} //end for values
						                            									%>
																					</div>
																					<% 
				                            									}
				                                        					}
																		%>
																		
																		<% 
																			if(!de.getImage().isEmpty()){
																				for(int idxLink =0; idxLink < de.getImage().size() ; idxLink ++ ){
																					DataElementImage image = de.getImage().get(idxLink);
																					String classLink = "";
																					String deIconName = image.getImage(); 
										                					
																					String deTip = !image.getToolTip().equals("") ? " title=\"" + image.getToolTip() + "\"" : "";
										                							
										                							String deTarget = " ";
																			  		if (!image.getTarget().equals("")) {
																			  			deTarget = " target=\""+ image.getTarget() +"\"";
																			  		}
																			  		
																			  		if (!image.getUrl().equals("")) {
																						image.addParameter(new Parameter(Costanti.PARAMETER_PREV_TAB_KEY, tabSessionKey));
																					}
																			  		
																			  		String visualizzaAjaxStatus = image.isShowAjaxStatus() ? Costanti.JS_FUNCTION_VISUALIZZA_AJAX_STATUS : "";
																			  		String id = "form-image-link_" + i + "_" + idxLink;
											                					%>
											                					<a id="<%=id %>" class="edit-link <%= classLink %>" <%= deTip %> <%=deTarget %> href="<%= image.getUrl() %>" type="button">
											                						<span class="icon-box">
																						<i class="material-icons md-18"><%= deIconName %></i>
																					</span>
											                					</a>
											                					<script type="text/javascript" nonce="<%= randomNonce %>">
																			      	 $(document).ready(function(){
																							$('#<%=id %>').click(function() {
																								<%= visualizzaAjaxStatus %>return true;
																							});
																						});
																				</script>
												                				<%
																				}// end for-edit-link
																			} // end edit-link
																		%>
						                							</div>
																</td>
															</tr>
															<% 
															firstText = false;
														} else { // else button
															if (type.equals("image")){
							                					%>
							                					<tr class="">
																	<td class="tdTextRiepilogo labelRiepilogo">
																		<label class="<%= labelStyleClass %>" for="<%= deHiddenId%>"><%=deLabel %></label>
																	</td>
																	<td class="tdTextRiepilogo <%= stile %>">
																		<div class="<%=classDivNoEdit %>"> 	
																		<input type="hidden" name="<%= deHiddenId %>" value="" id="<%= deHiddenId%>"/>																
																			<%
					                          									String [] values = de.getStatusValues();
					                                        					if (values != null) {
					                            									String [] labels = de.getLabels();
					                            									for (int y = 0; y < values.length; y++) {
					                            										String statusType = de.getStatusTypes()!=null && de.getStatusTypes().length>0 ? de.getStatusTypes()[y] : null; // valore icona
					                            										
					                            										String statusTooltip = de.getStatusToolTips()!=null && de.getStatusToolTips().length>0 ?  de.getStatusToolTips()[y] : null; // tooltip
					                            										String statusTooltipTitleAttribute = statusTooltip != null && !statusTooltip.equals("") ? " title=\"" + statusTooltip + "\"" : "";
														                				
					                            										String lab = values[y]; // testo configurazione
					                            										
					                            											%>
					                            												<span class="<%=classSpanNoEdit %>-image-msval" <%= statusTooltipTitleAttribute %> id="iconTitoloLeft-<%=i%>_<%=y%>">
					                            													<span class="icon-box">
																										<i class="material-icons md-18"><%= statusType %></i>
																									</span>
																								</span>
																								<span class="<%=classSpanNoEdit %>-msval" <%= statusTooltipTitleAttribute %> ><%= lab %></span>
																							<%
					                            									} //end for values
					                                        					}
				                          									%>
																	
																			<% 
																				if(!de.getImage().isEmpty()){
																					for(int idxLink =0; idxLink < de.getImage().size() ; idxLink ++ ){
																						DataElementImage image = de.getImage().get(idxLink);
																						String classLink = "";
																						String deIconName = image.getImage(); 
											                					
																						String deTip = !image.getToolTip().equals("") ? " title=\"" + image.getToolTip() + "\"" : "";
											                							
											                							String deTarget = " ";
																				  		if (!image.getTarget().equals("")) {
																				  			deTarget = " target=\""+ image.getTarget() +"\"";
																				  		}
																				  		
																				  		if (!image.getUrl().equals("")) {
																							image.addParameter(new Parameter(Costanti.PARAMETER_PREV_TAB_KEY, tabSessionKey));
																						}
																				  		
																				  		String visualizzaAjaxStatus = image.isShowAjaxStatus() ? Costanti.JS_FUNCTION_VISUALIZZA_AJAX_STATUS : "";
																				  		String id = "form-image-link_" + i + "_" + idxLink;
												                					%>
												                					<a id="<%=id %>" class="edit-link <%= classLink %>" <%= deTip %> <%=deTarget %> href="<%= image.getUrl() %>" type="button">
												                						<span class="icon-box">
																							<i class="material-icons md-18"><%= deIconName %></i>
																						</span>
												                					</a>
												                					<script type="text/javascript" nonce="<%= randomNonce %>">
																				      	 $(document).ready(function(){
																								$('#<%=id %>').click(function() {
																									<%= visualizzaAjaxStatus %>return true;
																								});
																							});
																					</script>
													                				<%
																					}// end for-edit-link
																				} // end edit-link
																			%>
							                							</div>
																	</td>
																</tr>
																<% 
																firstText = false;
															} else { // else image
															
															} // end else image
														} // end else button
						                			} // end else multi-select
						                		} // end else checkbox					                			
					                		} // end else text
											%>
											
										<% 
					        			} // end else subtitle
						    		} // end else hidden
				        		} %>	
								<tr>
								<% if(!visualizzaIconeListLink){ %>
	                                <td colspan="2" class="buttonrow">
	                                     <div class="buttonrowlista">
	                                             <%
                                                    for (int i = 0; i < listLink.size(); i++) {
                                       	            	DataElement de = (DataElement) listLink.get(i);
	                                                       
	                                                    String deName = !de.getName().equals("") ? de.getName() : "de_name_"+i;
	                                                    String type = de.getType();
	                                                    String deTip =  de.getToolTip() != null && !de.getToolTip().equals("") ? " title=\"" + de.getToolTip() + "\"" : "";
	                                                    String classInput= de.getStyleClass();
	                                                    String labelStyleClass= de.getLabelStyleClass();
	                                                    String iconLink =  de.getIcon();
	                                                    
	                                                    String visualizzaAjaxStatus = de.isShowAjaxStatus() ? Costanti.JS_FUNCTION_VISUALIZZA_AJAX_STATUS : "";
	                                                    String id = "form-add-tab-link_" + i;   
	                                                    if (type.equals("link")){
	                                                    	if (!de.getUrl().equals("")) {
																de.addParameter(new Parameter(Costanti.PARAMETER_PREV_TAB_KEY, tabSessionKey));
															}
	                                                    %>
															<input id="<%=id %>" type=button <%= deTip %> value="<%= de.getValue() %>"/>
															<script type="text/javascript" nonce="<%= randomNonce %>">
														      	 $(document).ready(function(){
																		$('#<%=id %>').click(function() {
																			<%= visualizzaAjaxStatus %>window.location.href=addTabIdParam('<%= de.getUrl() %>',true);
																		});
																	});
															</script>
								                        <%
                                           				} // if
                                       				} // for
                                 				%> 
                         				</div>
                 					</td>
								<% } else { %>
									<td colspan="2" class="tdTextRiepilogo padding-left-0">
										<div class="riepilogo-links">
											
											<%	
											for (int i = 0; i < listLink.size(); i++) {
												DataElement de = (DataElement) listLink.get(i);
											  
												String deName = !de.getName().equals("") ? de.getName() : "de_name_"+i;
											  	String type = de.getType();
											  	String deTip =  de.getToolTip() != null && !de.getToolTip().equals("") ? " title=\"" + de.getToolTip() + "\"" : "";
											  	String classInput= de.getStyleClass();
											  	String labelStyleClass= de.getLabelStyleClass();
											  	String iconLink =  de.getIcon();
											  	
											  	String visualizzaAjaxStatus = de.isShowAjaxStatus() ? Costanti.JS_FUNCTION_VISUALIZZA_AJAX_STATUS : "";
											  	String id = "form-riepilogo-link_" + i;   
											  	if (type.equals("link")){
											  		if (!de.getUrl().equals("")) {
														de.addParameter(new Parameter(Costanti.PARAMETER_PREV_TAB_KEY, tabSessionKey));
													}
							        				%>
							        					<div class="riepilogo-links-button-div">
							        						<a id="<%=id %>" href="<%= de.getUrl() %>" <%= deTip %> class="riepilogo-links-button">
									            				<i class="material-icons md-36"><%=iconLink %></i>							            				
									            				<span class="riepilogo-links-button-text"><%= de.getValue() %></span>
								            				</a>
								            				<script type="text/javascript" nonce="<%= randomNonce %>">
														      	 $(document).ready(function(){
																		$('#<%=id %>').click(function() {
																			<%= visualizzaAjaxStatus %>return true;
																		});
																	});
															</script>
							            				</div>
							            			<%
							            		}
											} // for
											%>
										</div>
									</td>
									<% } %>
								</tr>
							</table>
								
								<%
								if(datiConGruppi.size() > 1) {
									boolean fieldsetGroupOpen = false;
									for (int z = 1; z < datiConGruppi.size(); z++) {
										boolean groupOpen = false;
										boolean buttonGroupOpen = false;
										List<?> gruppo = (List<?>) datiConGruppi.get(z);	
										DataElement deAbilitazione =  (DataElement) gruppo.get(gruppo.size() - 2);
										String deAbilitazioneLabel = !deAbilitazione.getLabel().equals("") ? deAbilitazione.getLabel() : "&nbsp;";
					  					String deAbilitazioneType = deAbilitazione.getType();
					  					String deAbilitazioneName = !deAbilitazione.getName().equals("") ? deAbilitazione.getName() : "de_name_"+ (gruppo.size() - 2);
					  					String deAbilitazioneNote = deAbilitazione.getNote();
					  					String deAbilitazioneLabelStyleClass= deAbilitazione.getLabelStyleClass();
					  					String deAbilitazioneTip =  " title=\"" + deAbilitazione.getToolTip() + "\"";
					  					
					  					DataElement deNuovaConfigurazione =  (DataElement) gruppo.get(gruppo.size() - 1);
										String deNuovaConfigurazioneLabel = !deNuovaConfigurazione.getLabel().equals("") ? deNuovaConfigurazione.getLabel() : "&nbsp;";
					  					String deNuovaConfigurazioneType = deNuovaConfigurazione.getType();
									  	String deNuovaConfigurazioneNote = deNuovaConfigurazione.getNote();
									  	String deNuovaConfigurazioneClassInput= deNuovaConfigurazione.getStyleClass();
									  	String deNuovaConfigurazioneLabelStyleClass= deNuovaConfigurazione.getLabelStyleClass();
									  	String deNuovaConfigurazioneName = !deNuovaConfigurazione.getName().equals("") ? deNuovaConfigurazione.getName() : "de_name_"+ (gruppo.size() - 1);
					  					
					  					int numeroConfigurazioniDisponibili = 1;
					  					int numeroConfigurazioniAttive = 0;
					  					
										for (int i = 0; i < (gruppo.size() -2); i++) {
											DataElement de = (DataElement) gruppo.get(i);
										  
											String deName = !de.getName().equals("") ? de.getName() : "de_name_"+i;
										  	String type = de.getType();
										  	String rowName="row_"+deName;
										  	String deLabel = !de.getLabel().equals("") ? de.getLabel() : "&nbsp;";
										  	String deNote = de.getNote();
										  	String classInput= de.getStyleClass();
										  	String labelStyleClass= de.getLabelStyleClass();
										  	String deTip =  de.getToolTip() != null && !de.getToolTip().equals("") ? " title=\"" + de.getToolTip() + "\"" : "";
										  	String deHiddenId = "__i_hidden_lbl_de_"+z+"_"+i;
										  	
										  	if (type.equals("hidden")) {
										  		if(deName.equals(ErogazioniCostanti.ASPS_EROGAZIONI_PARAMETRO_NUMERO_CONFIGURAZIONI_ATTIVE))
										  			numeroConfigurazioniAttive = Integer.parseInt(de.getValue());
										  		else if(deName.equals(ErogazioniCostanti.ASPS_EROGAZIONI_PARAMETRO_NUMERO_CONFIGURAZIONI_DISPONIBILI))
										  			numeroConfigurazioniDisponibili = Integer.parseInt(de.getValue());
										  		else {
									    			%><input type="hidden" name="<%= deName  %>" value="<%= de.getValue()  %>"/><%
												}
									    	} else { // else hidden
									    		if (type.equals("title")){
									    			// se c'e' un altro field set aperto viene chiuso
									    			if(fieldsetGroupOpen){
									    				%>
									    				</fieldset>
									        			<%
									        			fieldsetGroupOpen = false;
									    			}
									    			if(!fieldsetGroupOpen){
										    			%>
										    				<fieldset>
										    					<legend><%=deLabel %></legend>
										    					<div class="fieldsetBody">
										    			<%
										    			fieldsetGroupOpen = true;
									    			}
									    		} else { // else title
									    			if (type.equals("subtitle")){
									    				if(buttonGroupOpen){ // se c'e' un group aperto ed ho finito gli elementi devo chiudere
									    					%>
									    						</div>
									    					</div>
									    					<%
									    					buttonGroupOpen = false;
									    				}
									    				// se c'e' un altro box aperto viene chiuso
										    			if(groupOpen){
										    				%>
										    					</div>
										    				</div>
										        			<%
										        			groupOpen = false;
										    			}
										    			if(!groupOpen){
											    			%>
											    				<div class="box <%= labelStyleClass %>">
											    					<div class="boxTitle">	
									        							<span class="boxTitleSpan"><%=deLabel %>&nbsp;&nbsp;&nbsp;&nbsp;</span>
								        							</div>
									        						<div class="contenutoBox">
											    			<%
											    			groupOpen = true;
										    			}
									        		} else { // else subtitle
									        			if (type.equals("text")){
								            				String textValNoEdit = de.getValue() != null && !de.getValue().equals("") ? de.getValue() : (pd.getMode().equals("view-noeditbutton") ? "&nbsp;" : "not defined");
								            				%>
								                			<div class="propBox">
								                				<label class="<%= labelStyleClass %>" for="<%= deHiddenId%>"><%=deLabel %></label>
								                				<div class="<%=classDivNoEdit %>"> 
								                					<input type="hidden" name="<%= deHiddenId %>" value="" id="<%= deHiddenId%>"/>	
									                				<span class="<%=classSpanNoEdit %>" <%= deTip %> ><%= textValNoEdit %></span>
									                				<input type="hidden" name="<%= deName %>" value="<%= de.getValue() %>"/>
									                			</div>
								                				<% if(!deNote.equals("")){ %>
														      		<p class="note <%= labelStyleClass %>"><%=deNote %></p>
														      	<% } %>
								                			</div>
								                			<%
								                		} else { // else text
								                			if (type.equals("button")){
								        		    			if(!buttonGroupOpen){
								        			    			%>
								        			    				<div class="boxButtonrow">
								        	        						<div class="boxButtonrowform">
								        			    			<%
								        			    			buttonGroupOpen = true;
								        		    			}
								        		    			String classLink = "";
														  		
								        		    			String visualizzaAjaxStatus = de.isShowAjaxStatus() ? Costanti.JS_FUNCTION_VISUALIZZA_AJAX_STATUS : "";
								        		    			if (!de.getUrl().equals("")) {
																	de.addParameter(new Parameter(Costanti.PARAMETER_PREV_TAB_KEY, tabSessionKey));
																}
								        		    			String id = "gruppo-link_" + z + "_" + i;   
								                				%>
								                				<a id="<%=id %>" class="<%= classLink %>" <%= deTip %> href="<%= de.getUrl() %>" type="button">
								                					<span class="icon-box">
																		<i class="material-icons md-18"><%= de.getValue() %></i>
																	</span>
								                				</a>
								                				<script type="text/javascript" nonce="<%= randomNonce %>">
															      	 $(document).ready(function(){
																			$('#<%=id %>').click(function() {
																				<%= visualizzaAjaxStatus %>return true;
																			});
																		});
																</script>
								                    			<%
								                    		} else { // else button
								                    			//fineelementi
								                    		} // end else button
								                		} // end else text
									        		} // end else subtitle
									    		} // end else title
									    	} // end else hidden
										} // end gruppo
											if(buttonGroupOpen){ // se c'e' un group aperto ed ho finito gli elementi devo chiudere
												%>
													</div>
												</div>
												<%
											}
										
								  			if(groupOpen){ // se c'e' un group aperto ed ho finito gli elementi devo chiudere e inserisco check box abilitazione
								  				
								  				String addBoxStyleClass = numeroConfigurazioniAttive == 0 ? "addBoxSoloAggiungi" : "addBox";
								  				%>
								  					</div>
								  				</div>
								  				<% if(numeroConfigurazioniDisponibili > 0){ %>
									  				<div class="<%=addBoxStyleClass %>">
									  					<a id="aggiungiConfigurazioneButton_<%= z %>" class="" title="Aggiungi" href="#" type="button">&nbsp;+&nbsp;</a>
									  				</div>
									  				<% if(deNuovaConfigurazioneType.equals("select")){ %>
									  					<div id="aggiungiConfigurazioneModal_<%= z %>" title="Aggiungi Configurazione">
													  		<div id="aggiungiConfigurazioneModalBody_<%= z %>" class="contenutoModal">
			                                        			<div class="prop">
			                                        				<%
																	String selTitle = (deNuovaConfigurazione.getToolTip()!=null && !deNuovaConfigurazione.getToolTip().equals("")) ? ("title='"+deNuovaConfigurazione.getToolTip()+"'") : " ";
																	String selId = "select_" + z;
		                          									%>
		                          									<label class="<%= deNuovaConfigurazioneLabelStyleClass %>" for="<%= selId  %>"><%=deNuovaConfigurazioneLabel %></label>
		                          									<select id="<%= selId  %>" name="<%= deNuovaConfigurazioneName  %>" <%= selTitle %> class="<%= deNuovaConfigurazioneClassInput %>"><%
		                          									String [] values = deNuovaConfigurazione.getValues();
		                                        					if (values != null) {
		                            									String [] labels = deNuovaConfigurazione.getLabels();
		                            									for (int v = 0; v < values.length; v++) {
		                            										String optionSel = values[v].equals(deNuovaConfigurazione.getSelected()) ? " selected " : " ";
		                            										
		                            										if (labels != null) {
		                            											%><option value="<%= values[v]  %>" <%=optionSel %> ><%= labels[v] %></option><%
		                            										} else {
		                            											%><option value="<%= values[v]  %>" <%=optionSel %> ><%= values[v] %></option><%
		                            										}
		                            									} //end for values
		                                        					}
		                          									%></select>
		                          									<% if(!deNuovaConfigurazione.getOnChange().equals("")){ 
		                          											String visualizzaAjaxStatus = deNuovaConfigurazione.isShowAjaxStatus() ? Costanti.JS_FUNCTION_VISUALIZZA_AJAX_STATUS : "";
																  			String changeHandler = visualizzaAjaxStatus + deNuovaConfigurazione.getOnChange();
																  		%>
															      		<script type="text/javascript" nonce="<%= randomNonce %>">
																			$(document).ready(function(){
																				$('#<%= selId  %>').change(function() {
																					<%=changeHandler%>
																				});
																			});
																		</script>
																		<% } %>
			                                        				<% if(!deNuovaConfigurazioneNote.equals("")){ %>
										      							<p class="note <%= deNuovaConfigurazioneLabelStyleClass %>"><%=deNuovaConfigurazioneNote %></p>
										      						<% } %>
			                                        			</div>
													 		</div>
														</div>
														<script type="text/javascript" nonce="<%= randomNonce %>">
														 $(document).ready(function(){
														 		$('#aggiungiConfigurazioneButton_<%= z %>').click(function () {
														            $('#aggiungiConfigurazioneModal_<%= z %>').dialog('open');
														        });	
														 		
														 		$( "#aggiungiConfigurazioneModal_<%= z %>" ).dialog({
														 	     resizable: false,
														 	     dialogClass: "no-close",
														 	     autoOpen: false,
														 	     height: "auto",
														 	     width: "auto",
														 	     modal: true,
														 	     buttons: {
														 	    	
														 	      }
														 	    });
														 });
														</script>
									  				<% } %>
									  				
									  				<%
									  			}
								  			}
									
											if(fieldsetGroupOpen){ // se c'e' un fieldset aperto ed ho finito gli elementi devo chiudere 
												%>
													</div>
									  				<% 
													if (deAbilitazioneType.equals("checkbox")){
	                                    				%>
	                                    				<div class="fieldsetSecondRowBody">
		                                        			<div class="propBox">
		                                        				<%
						    									String chkVal = deAbilitazione.getSelected().equals("yes") ? " checked='true' " : " ";
						    									String disVal = "" ; // pd.getMode().equals("view") || pd.getMode().equals("view-noeditbutton") ? "disabled=\"disabled\"" : "";
						    									String id = "checkbox-abilitazione-link_" + z; 
						    									%>	<table class="controlset">
				    													<tr> 
				    														<td>
						   														<input id="<%=id %>" type="checkbox" name="<%= deAbilitazioneName  %>" value="yes" <%=chkVal %> <%=disVal %> <%=deAbilitazioneTip %> >
						   														<% if(!deAbilitazione.getOnClick().equals("")){ %>
						   														<script type="text/javascript" nonce="<%= randomNonce %>">
																			      	 $(document).ready(function(){
																							$('#<%=id %>').click(function() {
																								<%= deAbilitazione.getOnClick() %>
																							});
																						});
																				</script>
						   														<% } %>
						   													</td>
						   													<% if(!deAbilitazione.getLabelRight().equals("")){ %>
						   													<td>
						   														<span class="controlset"><%=deAbilitazione.getLabelRight() %></span>
						   													</td>
						   													<% } %>
					   													</tr>
					   												</table>
						  										<% if(!deAbilitazioneNote.equals("")){ %>
								      								<p class="note <%= deAbilitazioneLabelStyleClass %>"><%=deAbilitazioneNote %></p>
								      							<% } %>
		                                        			</div>
	                                        			</div>
	                                        			<%
	                                        		}
													%>
												</fieldset>
												<%
											}
									
									} // end for gruppi
								} // end visualizza gruppi
								%>
						</td>
					</tr>	
					<%
					if (pd.getMode().equals("view")) {
					  %><tr class="buttonrow">
						  <td colspan="2">
						  	<div class="buttonrowform"><%
								  String [][] bottoni = pd.getBottoni();
								  if ((bottoni != null) && (bottoni.length > 0)) {
								    for (int i = 0; i < bottoni.length; i++) {
								      String id = "areaBottonBtn_" + i;
								      %><input id="<%=id %>" type=button value="<%= bottoni[i][0] %>"/>&nbsp;
								      <script type="text/javascript" nonce="<%= randomNonce %>">
									      	 $(document).ready(function(){
													$('#<%=id %>').click(function() {
														<%= Costanti.JS_FUNCTION_VISUALIZZA_AJAX_STATUS %><%= bottoni[i][1] %>;
													});
												});
										</script>
								      <%
								    }
								  } else {
								    %><input id="modificaBtn" type=button value="Edit" />
								    <script type="text/javascript" nonce="<%= randomNonce %>">
								      	 $(document).ready(function(){
												$('#modificaBtn').click(function() {
													<%= Costanti.JS_FUNCTION_VISUALIZZA_AJAX_STATUS %>EditPage();
												});
											});
									</script>
								    <%
								  }
							  %></div>
						  </td>
					  </tr>
					  <%
					} else {
					  if (pd.getMode().equals("view-noeditbutton") || pd.getMode().equals("view-nobutton") ) {
					  } else {  
					    %><tr class="buttonrow">
						    <td colspan="2" >
						    	<div class="buttonrowform">
						    		<input id="inviaModificheBtn" type=submit value="Invia" />
						    		<input id="svuotaFormBtn" type=button value="Cancella" />
						    		<script type="text/javascript" nonce="<%= randomNonce %>">
								      	 $(document).ready(function(){
												$('#inviaModificheBtn').click(function() {
													<%= Costanti.JS_FUNCTION_VISUALIZZA_AJAX_STATUS %>CheckDati();return false;
												});
												$('#svuotaFormBtn').click(function() {
													<%= Costanti.JS_FUNCTION_VISUALIZZA_AJAX_STATUS %>document.form.reset();
												});
											});
									</script>
						    	</div>
						    </td>
					    </tr><%
					  }
					}
					%>
				</table>
			</div>
		</td>
	</tr>
</tbody>
