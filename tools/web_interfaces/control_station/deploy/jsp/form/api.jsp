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
						                				<input type="hidden" name="<%= deName %>" value="<%= de.getValue() %>" />
					                				
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
															%>
						                					<tr class="">
																<td class="tdTextRiepilogo labelRiepilogo">
																	<label class="<%= labelStyleClass %>" for="<%= deHiddenId%>"><%=deLabel %></label>
																</td>
																<td class="tdTextRiepilogo <%= stile %>">
																	<div class="<%=classDivNoEdit %>"> 		
																	<input type="hidden" name="<%= deHiddenId %>" value="" id="<%= deHiddenId%>"/>															
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
									<td colspan="2" class="tdTextRiepilogo padding-left-0 padding-right-0">
										<div class="riepilogo-links">
											
											<%	
											for (int i = 0; i < listLink.size(); i++) {
												DataElement de = (DataElement) listLink.get(i);
											  
												String deName = !de.getName().equals("") ? de.getName() : "de_name_"+i;
											  	String type = de.getType();
											  	String deTip =  de.getToolTip() != null && !de.getToolTip().equals("") ? " title=\"" + de.getToolTip() + "\"" : "";
											  	String classInput = de.getStyleClass();
											  	String labelStyleClass= de.getLabelStyleClass();
											  	String iconLink =  de.getIcon();
											  	String visualizzaAjaxStatus = de.isShowAjaxStatus() ? Costanti.JS_FUNCTION_VISUALIZZA_AJAX_STATUS : "";
											  	String id = "form-riepilogo-link_" + i; 
											  	String cssClassDivStyle = !de.getStyle().equals("") ? "riepilogo-links-button-divStyle-"+i : ""; 
											  	if (type.equals("link")){
											  		if (!de.getUrl().equals("")) {
														de.addParameter(new Parameter(Costanti.PARAMETER_PREV_TAB_KEY, tabSessionKey));
													}
							        				%>
							        					<div class="riepilogo-links-button-div <%= cssClassDivStyle %>">
							        						<% if(!de.getStyle().equals("")){ %>
																<style type="text/css" nonce="<%= randomNonce %>">
																	.<%=cssClassDivStyle %> {
																		<%= de.getStyle() %>
																	}
																</style>
															<% }%>
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
								</tr>
							</table>
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
