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
%>

<%

String vBL = request.getParameter("visualizzaBottoneLogin");
boolean visualizzaBottoneLogin = false;

if(vBL != null){
	visualizzaBottoneLogin = Boolean.parseBoolean(vBL);
}


boolean mime = false;

Vector<?> dati = pd.getDati();
for (int i = 0; i < dati.size(); i++) {
  DataElement de = (DataElement) dati.elementAt(i);
  if (de.getType().equals("file")) {
    mime = true;
  }
}
String encTypeS = "";
if (mime) {
	encTypeS = "ENCTYPE=\"multipart/form-data\"";
}
%>


<td valign="top" class="td2PageBody">
	<form name="form" <%=encTypeS %> action="<%= gd.getUrl() %>" method="post">
		<!-- Breadcrumbs -->
		<jsp:include page="/jsplib/titlelist.jsp" flush="true" />

<%
boolean elementsRequired = false;
boolean elementsRequiredEnabled = true;
if (pd.getMode().equals("view-noeditbutton")) {
	elementsRequiredEnabled = false;
}

boolean visualizzaPanelLista = visualizzaBottoneLogin;

if(!visualizzaPanelLista)
	visualizzaPanelLista = !pd.isPageBodyEmpty();
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
%>


<table class="tabella-ext">
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
									<i class="material-icons md-24" id="iconaPanelDettaglio">&#xE5CF;</i>
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



if(elementsRequiredEnabled){
	for (int i = 0; i < dati.size(); i++) {
	  DataElement de = (DataElement) dati.elementAt(i);
	  if(de.isRequired()){
	     elementsRequired=true;
	     break;
	  }
	}
	if(elementsRequired){
		// inserisco la riga con le note
		%>
		<tr class="even" name="row_CampiObbligatori">
			<td class="campiObbligatori" colspan="2">
				<p class="legend">
					<strong>Note: </strong>(<em>*</em>) Campi obbligatori
				</p>
			</td>
			<td class="tdInput">&nbsp;</td>
		</tr>
		<%
	}
}

%><tr class="even">
	<td colspan="2">
<%


String classSpanNoEdit="spanNoEdit";
String classDivNoEdit="divNoEdit";
boolean fieldsetOpen = false;
for (int i = 0; i < dati.size(); i++) {
	DataElement de = (DataElement) dati.elementAt(i);
  
	String deName = !de.getName().equals("") ? de.getName() : "de_name_"+i;
  	String type = de.getType();
  	String rowName="row_"+deName;
  	String deLabel = !de.getLabel(elementsRequiredEnabled).equals("") ? de.getLabel(elementsRequiredEnabled) : "&nbsp;";
  	String deNote = de.getNote();
  	String classInput= de.getStyleClass();
  	String labelStyleClass= de.getLabelStyleClass();
	
    	if (type.equals("hidden")) {
    		%><input type="hidden" name="<%= deName  %>" value="<%= de.getValue()  %>"/><%
    	} else { // else hidden
    		if (type.equals("title")){
    			// se c'e' un altro field set aperto viene chiuso
    			if(fieldsetOpen){
    				%>
    				</fieldset>
        			<%
        			fieldsetOpen = false;
    			}
    			if(!fieldsetOpen){
	    			%>
	    				<fieldset>
	    					<legend><a name="<%=rowName %>" class="navigatorAnchor"><%=deLabel %></a></legend>
	    			<%
	    			fieldsetOpen = true;
    			}
    		} else { // else title
    			if (type.equals("subtitle")){
    				%>
        			<div class="subtitle <%= labelStyleClass %>">
        				<span class="subtitle"><a name="<%=rowName %>" class="navigatorAnchor"><%=deLabel %>&nbsp;&nbsp;&nbsp;&nbsp;</a></span>
        			</div>
        			<%
        		} else { // else subtitle
	    			if (type.equals("note")){
	    				String noteValue = !de.getValue().equals("") ? de.getValue() : "&nbsp;";
	    				
	    				%>
	        			<div class="prop">
						<label class="<%= labelStyleClass %>"><%=deLabel %></label>
        					<div class="<%=classDivNoEdit %>">
							<span class="<%=classSpanNoEdit %>"><%=noteValue %></span>
						</div>
	        			</div>
	        			<%
	        		} else { // else note
	        			if (type.equals("link")){
	        				%>
	            			<div class="prop prop-link">
	            				<label class="<%= labelStyleClass %>">&nbsp;</label>
	            				<span><a href="<%= de.getUrl() %>"><%= de.getValue() %></a></span>
	            			</div>
	            			<%
	            		} else { // else link
	            			if (type.equals("text")){
	            				String textValNoEdit = de.getValue() != null && !de.getValue().equals("") ? de.getValue() : (pd.getMode().equals("view-noeditbutton") ? "&nbsp;" : "not defined");
	            				%>
	                			<div class="prop">
	                				<label class="<%= labelStyleClass %>"><%=deLabel %></label>
	                				<div class="<%=classDivNoEdit %>"> 
		                				<span class="<%=classSpanNoEdit %>"><%= textValNoEdit %></span>
		                				<input type="hidden" name="<%= deName %>" value="<%= de.getValue() %>"/>
		                			</div>
	                				<% if(!deNote.equals("")){ %>
							      		<p class="note <%= labelStyleClass %>"><%=deNote %></p>
							      	<% } %>
	                			</div>
	                			<%
	                		} else { // else text
	                			if (type.equals("textedit")){
	                				%>
	                    			<div class="prop">
	                    				<label class="<%= labelStyleClass %>"><%=deLabel %></label>
	                    				<%
								    	if (pd.getMode().equals("view") || pd.getMode().equals("view-noeditbutton")) {
								    		String taeditValNoEdit = de.getValue() != null && !de.getValue().equals("") ? de.getValue() : (pd.getMode().equals("view-noeditbutton") ? "&nbsp;" : "not defined");
								    		%><div class="<%=classDivNoEdit %>"> 
								    			<span class="<%=classSpanNoEdit %>"><%= taeditValNoEdit %></span>
								    			</div><%
								    	} else {
								    		String selDataAttributes = !de.getDataAttributes().equals("") ? de.getDataAttributes() : " ";
								      		%><input type="text" name="<%= deName %>" value="<%= de.getValue() %>" class="<%= classInput %>" <%= selDataAttributes %> >
								      	<%
								    	}
								      	%>
								      	<% if(!deNote.equals("")){ %>
								      		<p class="note <%= labelStyleClass %>"><%=deNote %></p>
								      	<% } %>
	                    			</div>
	                    			<%
	                    		} else { // else textedit
	                    			if (type.equals("number")){
		                				%>
		                    			<div class="prop">
		                    				<label class="<%= labelStyleClass %>"><%=deLabel %></label>
		                    				<%
									    	if (pd.getMode().equals("view") || pd.getMode().equals("view-noeditbutton")) {
									    		String taeditValNoEdit = de.getValue() != null && !de.getValue().equals("") ? de.getValue() : (pd.getMode().equals("view-noeditbutton") ? "&nbsp;" : "not defined");
									    		%><div class="<%=classDivNoEdit %>"> 
									    			<span class="<%=classSpanNoEdit %>"><%= taeditValNoEdit %></span>
									    			</div><%
									    	} else {
									    		String minvalue = de.getMinValue() != null ? " min=\"" + de.getMinValue() + "\"" : "";
									    		String maxValue = de.getMaxValue() != null ? " max=\"" + de.getMaxValue() + "\"" : "";
									    		
									      		%><input type="number" name="<%= deName %>" value="<%= de.getValue() %>" class="<%= classInput %>" <%=minvalue %> <%=maxValue %> >
									      	<%
									    	}
									      	%>
									      	<% if(!deNote.equals("")){ %>
									      		<p class="note <%= labelStyleClass %>"><%=deNote %></p>
									      	<% } %>
		                    			</div>
		                    			<%
		                    		} else { // else number
		                    			if (type.equals("crypt")){
		                    				%>
		                        			<div class="prop">
		                        				<label class="<%= labelStyleClass %>"><%=deLabel %></label>
		                        				<%
							          			if (pd.getMode().equals("view") || pd.getMode().equals("view-noeditbutton")) {
													%><div class="<%=classDivNoEdit %>"> <span class="<%=classSpanNoEdit %>">********</span></div><%
							   					} else {
													%><input class="<%= classInput %>" type="password" name="<%= deName  %>" value="<%= de.getValue()  %>"><%
							   					}
						     					%>
						     					<% if(!deNote.equals("")){ %>
									      			<p class="note <%= labelStyleClass %>"><%=deNote %></p>
									      		<% } %>
		                        			</div>
		                        			<%
		                        		} else { // else crypt
		                        			if (type.equals("textarea") || type.equals("textarea-noedit")){
		                        				String inputId = "txtA" + i;
		            	     					if (type.equals("textarea-noedit")){
		            								inputId = "txtA_ne" + i; 
		            	     					}
		                        				%>
		                            			<div class="prop">
		                            				<label class="<%= labelStyleClass %>"><%=deLabel %></label>
		                            				<%
							     					if ((pd.getMode().equals("view") || pd.getMode().equals("view-noeditbutton")) && de.isLabelAffiancata()) {
							     						String taValNoEdit = de.getValue() != null && !de.getValue().equals("") ? de.getValue() : (pd.getMode().equals("view-noeditbutton") ? "&nbsp;" : "not defined");
							     						%><div class="<%=classDivNoEdit %>"> <span class="<%=classSpanNoEdit %>"><%= taValNoEdit %></span></div><%
							     					} else {
							     						String taNoEdit = type.equals("textarea") ? " " : " readonly ";
							     						%><textarea id="<%=inputId %>" <%=taNoEdit %> rows='<%= de.getRows() %>' cols='<%= de.getCols() %>' name="<%= deName  %>" class="<%= classInput %>"><%= de.getValue() %></textarea><%
							     					}
													%>
							     					<% if(!deNote.equals("")){ %>
									      				<p class="note <%= labelStyleClass %>"><%=deNote %></p>
									      			<% } %>
		                            			</div>
		                            			<%
		                            		} else { // else textarea || textarea-noedit
		                            			if (type.equals("button")){
		                            				%>
		                                			<div class="prop">
		                                				<label class="<%= labelStyleClass %>"><%=deLabel %></label>
		                                				<input type="button" onClick="<%= de.getOnClick() %>" value="<%= de.getValue() %>">
		                                				<% if(!deNote.equals("")){ %>
									      					<p class="note <%= labelStyleClass %>"><%=deNote %></p>
									      				<% } %>
		                                			</div>
		                                			<%
		                                		} else { // else button
		                                			if (type.equals("file")){
		                                				%>
		                                    			<div class="prop">
		                                    				<label class="<%= labelStyleClass %>"><%=deLabel %></label>
		                                    				<%
		                                    				if (pd.getMode().equals("view") || pd.getMode().equals("view-noeditbutton")) {
		                                    	     			String fileValue = (de.getValue() != null && !de.getValue().equals("")) ? de.getValue() : "not defined";
		                                    	            	%> 
		                                    	            	<div class="<%=classDivNoEdit %>"> <span class="<%=classSpanNoEdit %>"><%=fileValue %></span></div><%
		                                    	      		} else {
		                                    	          		%><input size='<%= de.getSize() %>' type=file name="<%= deName  %>" class="<%= classInput %>"  
											  	<%
												  if (!de.getOnChange().equals("")) {
													    %> onChange="postVersion_<%= de.getOnChange() %>"<%
													  }
													  %>  	/><%
		                                    	      		}
		                                    				%>
		                                    				<% if(!deNote.equals("")){ %>
									      						<p class="note <%= labelStyleClass %>"><%=deNote %></p>
									      					<% } %>
		                                    			</div>
		                                    			<%
		                                    		} else { // else file
		                                    			if (type.equals("select")){
		                                    				%>
		                                        			<div class="prop">
		                                        				<label class="<%= labelStyleClass %>"><%=deLabel %></label>
		                                        				<%
		                                        				if (pd.getMode().equals("view") || pd.getMode().equals("view-noeditbutton")) {
		                                      						String selValNoEdit = (de.getSelected() != "") ? de.getSelected() : (pd.getMode().equals("view-noeditbutton") ? "&nbsp;" : "not defined");
		                                      						%><div class="<%=classDivNoEdit %>"> <span class="<%=classSpanNoEdit %>"><%= selValNoEdit %></span></div><%
		                               							} else {
		                               								String selEvtOnChange = !de.getOnChange().equals("") ? (" onChange=\"" + de.getOnChange() + "\" " ) : " ";
																	String selTitle = (de.getToolTip()!=null && !de.getToolTip().equals("")) ? ("title='"+de.getToolTip()+"'") : " ";
		                               								
		                          									%><select name="<%= deName  %>" <%= selEvtOnChange %> <%= selTitle %> class="<%= classInput %>"><%
		                          									String [] values = de.getValues();
		                                        					if (values != null) {
		                            									String [] labels = de.getLabels();
		                            									for (int v = 0; v < values.length; v++) {
		                            										String optionSel = values[v].equals(de.getSelected()) ? " selected " : " ";
		                            										
		                            										if (labels != null) {
		                            											%><option value="<%= values[v]  %>" <%=optionSel %> ><%= labels[v] %></option><%
		                            										} else {
		                            											%><option value="<%= values[v]  %>" <%=optionSel %> ><%= values[v] %></option><%
		                            										}
		                            									} //end for values
		                                        					}
		                          									%></select><%
		                               							}
		                                        				%>
		                                        				<% if(!deNote.equals("")){ %>
									      							<p class="note <%= labelStyleClass %>"><%=deNote %></p>
									      						<% } %>
		                                        			</div>
		                                        			<%
		                                        		} else { // else select
		                                        			if(type.equals("multi-select")){
		                                        				%>
	                                        					<div class="prop">
			                                        				<label class="<%= labelStyleClass %>"><%=deLabel %></label>
			                                        				<%
			                                        				if (pd.getMode().equals("view") || pd.getMode().equals("view-noeditbutton")) {
			                                      						String selValNoEdit = (de.getSelezionatiAsString() != "") ? de.getSelezionatiAsString() : (pd.getMode().equals("view-noeditbutton") ? "&nbsp;" : "not defined");
			                                      						%><div class="<%=classDivNoEdit %>"> <span class="<%=classSpanNoEdit %>"><%= selValNoEdit %></span></div><%
			                               							} else {
			                               								String selSize = " size='"+de.getRows()+"' ";
			                               								String selEvtOnChange = !de.getOnChange().equals("") ? (" onChange=\"" + de.getOnChange() + "\" " ) : " ";
			                               								String selDataAttributes = !de.getDataAttributes().equals("") ? de.getDataAttributes() : " ";
			                               								
			                          									%><select name="<%= deName  %>" <%= selSize %> <%= selEvtOnChange %> class="<%= classInput %>" multiple <%= selDataAttributes %> ><%
			                          									String [] values = de.getValues();
			                                        					if (values != null) {
			                            									String [] labels = de.getLabels();
			                            									for (int v = 0; v < values.length; v++) {
			                            										String optionSel = de.isSelected(values[v]) ? " selected " : " ";
			                            										
			                            										if (labels != null) {
			                            											%><option value="<%= values[v]  %>" <%=optionSel %> ><%= labels[v] %></option><%
			                            										} else {
			                            											%><option value="<%= values[v]  %>" <%=optionSel %> ><%= values[v] %></option><%
			                            										}
			                            									} //end for values
			                                        					}
			                          									%></select><%
			                               							}
			                                        				%>
			                                        				<% if(!deNote.equals("")){ %>
										      							<p class="note <%= labelStyleClass %>"><%=deNote %></p>
										      						<% } %>
			                                        			</div>
		                                        				<%
		                                        			} else { // else multi-select
		                                        				if (type.equals("checkbox")){
			                                        				%>
			                                            			<div class="prop">
			                                            				<label class="<%= labelStyleClass %>"><%=deLabel %></label>
			                                            				<%
								    									String chkEvtOnClick = !de.getOnClick().equals("") ? (" onClick=\"" + de.getOnClick() + "\" ") :" ";
								    									String chkVal = de.getSelected().equals("yes") ? " checked='true' " : " ";
								    									String disVal = pd.getMode().equals("view") || pd.getMode().equals("view-noeditbutton") ? "disabled=\"disabled\"" : "";
								    									
								    									%>	<table class="controlset">
						    													<tr> 
						    														<td>
								   														<input type="checkbox" name="<%= deName  %>" value="yes" <%=chkVal %> <%=chkEvtOnClick %> <%=disVal %> >
								   													</td>
								   													<% if(!de.getLabelRight().equals("")){ %>
								   													<td>
								   														<span class="controlset"><%=de.getLabelRight() %></span>
								   													</td>
								   													<% } %>
							   													</tr>
							   												</table>
								  										<% if(!deNote.equals("")){ %>
										      								<p class="note <%= labelStyleClass %>"><%=deNote %></p>
										      							<% } %>
			                                            			</div>
			                                            			<%
			                                            		} else { // else checkbox
			                                            			if (type.equals("radio")){
			                                            				%>
			                                                			<div class="prop">
			                                                				<label class="<%= labelStyleClass %>"><%=deLabel %></label>
			                                                				<%
						   	        										if (pd.getMode().equals("view") || pd.getMode().equals("view-noeditbutton")) {
						   	        											String radioValNoEdit = !de.getSelected().equals("") ? de.getSelected() : "not defined";
						    													%><div class="<%=classDivNoEdit %>"> <span class="<%=classSpanNoEdit %>"><%=radioValNoEdit %></span></div><%
						    												} else {
						    													%>
						    													<table class="controlset">
						    														<tr>
						    													<%
							  													String [] values = de.getValues();
							  													String [] labels = de.getLabels();
							  													for (int v = 0; v < values.length; v++) {
							  														String chkVal = values[v].equals(de.getSelected()) ? " checked " : " ";
							  														String id = deName + "_" + v;
							  														
							  														if (labels != null) {
				                            											%><td><input type="radio" <%=chkVal %> name="<%= deName  %>" value="<%= values[v]  %>" id="<%=id %>" />
				                            											<label for="<%=id %>"><%= labels[v] %></label></td><%
				                            										} else {
				                            											%><td><input type="radio" <%=chkVal %> name="<%= deName  %>" value="<%= values[v]  %>" id="<%=id %>" />
																						<label for="<%=id %>"><%= values[v] %></label></td><%
				                            										}
							  														%><%
						          												} // end for values
							  													%></tr>
						    													</table><%
																			}
																	    	%>
																	    	<% if(!deNote.equals("")){ %>
										      									<p class="note <%= labelStyleClass %>"><%=deNote %></p>
										      								<% } %>
			                                                			</div>
			                                                			<%
			                                                		} else { // else radio
			                                                			//fineelementi
			                                                		} // end else radio
			                                            		} // end else checkbox
		                                        			} // end else multi-select
		                                        		} // end else select
		                                    		} // end else file
		                                		} // end else button
		                            		} // end else textarea || textarea-noedit
		                        		} // end else crypt
		                    		} // end else number
	                    		} // end else textedit
	                		} // end else text
	            		} // end else link
	        		} // end else note
    		} // end else subtitle
    		} // end else title
    	} // end else hidden
	} // for

	if(fieldsetOpen){ // se c'e' un fieldset aperto ed ho finito gli elementi devo chiudere
		%>
		</fieldset>
		<%
	}
	%>
	</td>
</tr>

<%

if (visualizzaBottoneLogin) {
	  %><tr class="buttonrow">
		  <td colspan="2">
		  	<div class="buttonrowform">
				<input type=submit onClick='CheckDati();return false;' value="Login" />
			</div>
		  </td>
	  </tr>
	  <%
	}
else
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
			    %><input type=button onClick="EditPage()" value="Modifica" /><%
			  }
		  %></div>
	  </td>
  </tr>
  <%
} else {
  if (pd.getMode().equals("view-noeditbutton") || pd.getMode().equals("view-nobutton") ) {
    %><tr class="buttonrownobuttons">
    	<td colspan="2" >&nbsp;</td>
    </tr><%
  } else {
	  // 	    		<input type=button onClick='document.form.reset();' value="Cancella" />
    %><tr class="buttonrow">
	    <td colspan="2" >
	    	<div class="buttonrowform">
	    		<input type=submit onClick='CheckDati();return false;' value="<%=pd.getLabelBottoneInvia() %>" />
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
</table>
</form>
</td>

