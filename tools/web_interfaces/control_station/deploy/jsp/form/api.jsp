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
	
    DataElement deCheckBox = !vectorCheckBox.isEmpty() ? (DataElement) vectorCheckBox.elementAt(0) : null;
    String imageCheckBox = deCheckBox != null ?  deCheckBox.getValue() : null ;
	String tooltipCheckBox = deCheckBox != null ? (!deCheckBox.getToolTip().equals("") ? " title=\"" + deCheckBox.getToolTip() + "\"" : "") : "";
	
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
									boolean firstText = true;
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
														<% if(firstText && deCheckBox != null){%>
															<span class="<%=classSpanNoEdit %>-image" id="iconTitoloLeft">
																<img src="images/tema_link/<%= imageCheckBox %>" <%= tooltipCheckBox %>/>
															</span>
														<% } %>
						                				<span class="<%=classSpanNoEdit %>"><%= textValNoEdit %></span>
						                				<input type="hidden" name="<%= deName %>" value="<%= de.getValue() %>"/>
					                				
													<% 
														if(!de.getListaUrl().isEmpty()){
															boolean addTooltip = !de.getListaToolTip().isEmpty();
															boolean addTarget = !de.getListaTarget().isEmpty();
															boolean addIcon = !de.getListaIcon().isEmpty();
															
															for(int idxLink =0; idxLink < de.getListaUrl().size() ; idxLink ++ ){
																String classLink = "";
																String deIconName = addIcon ? de.getListaIcon().get(idxLink) : ""; 
					                					
																String deTip = (addTooltip && !de.getListaToolTip().get(idxLink).equals("")) ? " title=\"" + de.getListaToolTip().get(idxLink) + "\"" : "";
					                							
					                							String deTarget = " ";
														  		if (addTarget && !de.getListaTarget().get(idxLink).equals("")) {
														  			deTarget = " target=\""+ de.getListaTarget().get(idxLink) +"\"";
														  		}
													  			
						                					%>
						                					<a class="edit-link <%= classLink %>" <%= deTip %> <%=deTarget %> href="<%= de.getListaUrl().get(idxLink) %>" type="button">
						                						<span class="icon-box">
																	<i class="material-icons md-18"><%= deIconName %></i>
																</span>
						                					</a>
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
					                		} // end else text
											%>
											
										<% 
					        			} // end else subtitle
						    		} // end else hidden
				        		} %>	
								
								<tr>
									<td colspan="2" class="tdTextRiepilogo" style="padding-left: 0px;">
										<div class="riepilogo-links">
											
											<%	
											for (int i = 0; i < vectorLink.size(); i++) {
												DataElement de = (DataElement) vectorLink.elementAt(i);
											  
												String deName = !de.getName().equals("") ? de.getName() : "de_name_"+i;
											  	String type = de.getType();
											  	String deTip =  de.getToolTip() != null && !de.getToolTip().equals("") ? " title=\"" + de.getToolTip() + "\"" : "";
											  	String classInput= de.getStyleClass();
											  	String labelStyleClass= de.getLabelStyleClass();
											  	String iconLink =  de.getIcon();
											  	
											  	if (type.equals("link")){
							        				%>
							        					<div class="riepilogo-links-button-div">
							        						<a href="<%= de.getUrl() %>" <%= deTip %> class="riepilogo-links-button">
									            				<i class="material-icons md-36"><%=iconLink %></i>							            				
									            				<span class="riepilogo-links-button-text"><%= de.getValue() %></span>
								            				</a>
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
