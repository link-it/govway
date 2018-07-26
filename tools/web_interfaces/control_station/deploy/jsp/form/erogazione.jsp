<%--
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
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



<%@page import="org.openspcoop2.web.ctrlstat.servlet.aps.erogazioni.ErogazioniCostanti"%>
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
	
	Vector<?> datiConGruppi = pd.getDati();
	Vector<?> dati = (Vector<?>) datiConGruppi.elementAt(0);
	
	boolean visualizzaPanelLista = !pd.isPageBodyEmpty();
	
	String classDivPanelLista = visualizzaPanelLista  ? "panelLista" : "";
	String classTabellaPanelLista = visualizzaPanelLista  ? "tabella" : "";
	
	Vector<GeneralLink> titlelist = pd.getTitleList();
	String titoloSezione = null;
	if (titlelist != null && titlelist.size() > 0) {
		GeneralLink l = titlelist.elementAt(titlelist.size() -1);
		titoloSezione = l.getLabel();
		
		if(titoloSezione != null && titoloSezione.equals(Costanti.PAGE_DATA_TITLE_LABEL_AGGIUNGI))
			titoloSezione = null;
	} 
	boolean mostraFormHeader = false;
	int colFormHeader = (mostraFormHeader ? 2 : 1);
	String classPanelTitolo = mostraFormHeader ? "panelDettaglioForm" : "panelDettaglioNoForm";
	
	Vector<DataElement> vectorRiepilogo = new Vector<DataElement>();
	Vector<DataElement> vectorLink = new Vector<DataElement>();
	Vector<DataElement> vectorCheckBox = new Vector<DataElement>();

	for (int j = 0; j < dati.size(); j++) {
	    DataElement de = (DataElement) dati.elementAt(j);
	    
	    if (de.getType().equals("link")) {
	    	vectorLink.add(de);
	    } else if (de.getType().equals("checkbox")) {
	    	vectorCheckBox.add(de);
	    } else {
	    	vectorRiepilogo.add(de);
	    }
	}
	
	String classSpanNoEdit="spanNoEdit";
	String classDivNoEdit="divNoEdit";
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
								<% if(mostraFormHeader) { %>
								<td class="titoloSezione titoloSezione-right">
									<span class="icon-box" id="iconaPanelDettaglioSpan">
										<i class="material-icons md-18" id="iconaPanelDettaglio">&#xE5CF;</i>
									</span>
								</td>
								<% }%>
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
							
							for (int i = 0; i < vectorRiepilogo.size(); i++) {
								DataElement de = (DataElement) vectorRiepilogo.elementAt(i);
							  
								String deName = !de.getName().equals("") ? de.getName() : "de_name_"+i;
							  	String type = de.getType();
							  	String rowName="row_"+deName;
							  	String deLabel = !de.getLabel().equals("") ? de.getLabel() : "&nbsp;";
							  	String deNote = de.getNote();
							  	String classInput= de.getStyleClass();
							  	String labelStyleClass= de.getLabelStyleClass();
							  	
							  	String stile=null;
							  	//per ogni entry:
								if ((i % 2) != 0) {
							    	stile = "odd";
							  	} else {
								    stile = "even";
							  	}
								
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
				            				%>
				                			<tr class="">
												<td class="tdTextRiepilogo labelRiepilogo">
													<label class="<%= labelStyleClass %>"><%=deLabel %></label>
												</td>
												<td class="tdTextRiepilogo <%= stile %>">
													<div class="<%=classDivNoEdit %>"> 
						                				<span class="<%=classSpanNoEdit %>"><%= textValNoEdit %></span>
						                				<input type="hidden" name="<%= deName %>" value="<%= de.getValue() %>"/>
					                				
													<% if(!de.getUrl().equals("")){
					                					String deTip =  de.getToolTip() != null && !de.getToolTip().equals("") ? " title=\"" + de.getToolTip() + "\"" : "";
					                					String classLink = "";
					                					%>
					                					<a class="edit-link <%= classLink %>" <%= deTip %> href="<%= de.getUrl() %>" type="button">
					                					<span class="icon-box">
															<i class="material-icons md-18">&#xE3C9;</i>
														</span>
					                				</a>
					                				<%
							                		} // end edit-link
													%>
													</div>
												</td>
											</tr>
				                			<%
					                		} else { // else text
					                		} // end else text
											%>
											
										<% 
					        			} // end else subtitle
						    		} // end else hidden
				        		} %>	
								
								<tr class="riepilogo-spacer">
									<td colspan="2" class="tdTextRiepilogo">&nbsp;</td>
								</tr>
								
								<tr>
									<td colspan="2" class="tdTextRiepilogo" style="padding-left: 0px;">
										<div class="riepilogo-links">
											
											<%	
											for (int j = 0; j < vectorCheckBox.size(); j++) {
											    DataElement de = (DataElement) vectorCheckBox.elementAt(j);
											    String image = de.getValue();
								  				String tooltip = !de.getToolTip().equals("") ? " title=\"" + de.getToolTip() + "\"" : ""; 
											  	%>
										  			<span class="riepilogo-links-pipe" id="iconConfigurazione_<%=j %>">
														<img src="images/tema_link/<%= image %>" <%= tooltip %>/>
													</span>
										  		<%
											  	
											} // for
											
											for (int i = 0; i < vectorLink.size(); i++) {
												DataElement de = (DataElement) vectorLink.elementAt(i);
											  
												String deName = !de.getName().equals("") ? de.getName() : "de_name_"+i;
											  	String type = de.getType();
											  	String deTip =  de.getToolTip() != null && !de.getToolTip().equals("") ? " title=\"" + de.getToolTip() + "\"" : "";
											  	String classInput= de.getStyleClass();
											  	String labelStyleClass= de.getLabelStyleClass();
											  	
											  	if (type.equals("link")){
											  		if(i > 0){
											  			%>
								            				<span class="riepilogo-links-pipe">&nbsp;&nbsp;|&nbsp;&nbsp;</span>
								            			<%
												  	}
							        				%>
							            				<span><a href="<%= de.getUrl() %>" <%= deTip %> ><%= de.getValue() %></a></span>
							            			<%
							            		}
											}
											%>
										</div>
									</td>
								</tr>
							</table>
								
								<%
								if(datiConGruppi.size() > 1) {
									boolean fieldsetGroupOpen = false;
									for (int z = 1; z < datiConGruppi.size(); z++) {
										boolean groupOpen = false;
										boolean buttonGroupOpen = false;
										Vector<?> gruppo = (Vector<?>) datiConGruppi.elementAt(z);	
										DataElement deAbilitazione =  (DataElement) gruppo.elementAt(gruppo.size() - 2);
										String deAbilitazioneLabel = !deAbilitazione.getLabel().equals("") ? deAbilitazione.getLabel() : "&nbsp;";
					  					String deAbilitazioneType = deAbilitazione.getType();
					  					String deAbilitazioneName = !deAbilitazione.getName().equals("") ? deAbilitazione.getName() : "de_name_"+ (gruppo.size() - 2);
					  					String deAbilitazioneNote = deAbilitazione.getNote();
					  					String deAbilitazioneLabelStyleClass= deAbilitazione.getLabelStyleClass();
					  					String deAbilitazioneTip =  " title=\"" + deAbilitazione.getToolTip() + "\"";
					  					
					  					DataElement deNuovaConfigurazione =  (DataElement) gruppo.elementAt(gruppo.size() - 1);
										String deNuovaConfigurazioneLabel = !deNuovaConfigurazione.getLabel().equals("") ? deNuovaConfigurazione.getLabel() : "&nbsp;";
					  					String deNuovaConfigurazioneType = deNuovaConfigurazione.getType();
									  	String deNuovaConfigurazioneNote = deNuovaConfigurazione.getNote();
									  	String deNuovaConfigurazioneClassInput= deNuovaConfigurazione.getStyleClass();
									  	String deNuovaConfigurazioneLabelStyleClass= deNuovaConfigurazione.getLabelStyleClass();
									  	String deNuovaConfigurazioneName = !deNuovaConfigurazione.getName().equals("") ? deNuovaConfigurazione.getName() : "de_name_"+ (gruppo.size() - 1);
					  					
					  					int numeroConfigurazioniDisponibili = 1;
					  					int numeroConfigurazioniAttive = 0;
					  					
										for (int i = 0; i < (gruppo.size() -2); i++) {
											DataElement de = (DataElement) gruppo.elementAt(i);
										  
											String deName = !de.getName().equals("") ? de.getName() : "de_name_"+i;
										  	String type = de.getType();
										  	String rowName="row_"+deName;
										  	String deLabel = !de.getLabel().equals("") ? de.getLabel() : "&nbsp;";
										  	String deNote = de.getNote();
										  	String classInput= de.getStyleClass();
										  	String labelStyleClass= de.getLabelStyleClass();
										  	String deTip =  de.getToolTip() != null && !de.getToolTip().equals("") ? " title=\"" + de.getToolTip() + "\"" : "";
										  	
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
								                				<label class="<%= labelStyleClass %>"><%=deLabel %></label>
								                				<div class="<%=classDivNoEdit %>"> 
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
														  		
								                				%>
								                				<a class="<%= classLink %>" <%= deTip %> href="<%= de.getUrl() %>" type="button">
								                					<span class="icon-box">
																		<i class="material-icons md-18"><%= de.getValue() %></i>
																	</span>
								                				</a>
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
			                                        				<label class="<%= deNuovaConfigurazioneLabelStyleClass %>"><%=deNuovaConfigurazioneLabel %></label>
			                                        				<%
		                               								String selEvtOnChange = !deNuovaConfigurazione.getOnChange().equals("") ? (" onChange=\"" + deNuovaConfigurazione.getOnChange() + "\" " ) : " ";
																	String selTitle = (deNuovaConfigurazione.getToolTip()!=null && !deNuovaConfigurazione.getToolTip().equals("")) ? ("title='"+deNuovaConfigurazione.getToolTip()+"'") : " ";
		                               								
		                          									%><select name="<%= deNuovaConfigurazioneName  %>" <%= selEvtOnChange %> <%= selTitle %> class="<%= deNuovaConfigurazioneClassInput %>"><%
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
		                          									%></select><%
			                                        				%>
			                                        				<% if(!deNuovaConfigurazioneNote.equals("")){ %>
										      							<p class="note <%= deNuovaConfigurazioneLabelStyleClass %>"><%=deNuovaConfigurazioneNote %></p>
										      						<% } %>
			                                        			</div>
													 		</div>
														</div>
														<script>
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
						    									String chkEvtOnClick = !deAbilitazione.getOnClick().equals("") ? (" onClick=\"" + deAbilitazione.getOnClick() + "\" ") :" ";
						    									String chkVal = deAbilitazione.getSelected().equals("yes") ? " checked='true' " : " ";
						    									String disVal = "" ; // pd.getMode().equals("view") || pd.getMode().equals("view-noeditbutton") ? "disabled=\"disabled\"" : "";
						    									
						    									%>	<table class="controlset">
				    													<tr> 
				    														<td>
						   														<input type="checkbox" name="<%= deAbilitazioneName  %>" value="yes" <%=chkVal %> <%=chkEvtOnClick %> <%=disVal %> <%=deAbilitazioneTip %> >
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
								      %><input type=button onClick="<%= bottoni[i][1] %>" value="<%= bottoni[i][0] %>"/>&nbsp;<%
								    }
								  } else {
								    %><input type=button onClick="EditPage()" value="Edit" /><%
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
						    		<input type=submit onClick='CheckDati();return false;' value="Invia" />
						    		<input type=button onClick='document.form.reset();' value="Cancella" />
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
