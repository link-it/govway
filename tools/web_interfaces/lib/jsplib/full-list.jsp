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



<%@ page session="true" import="java.util.*, org.openspcoop2.web.lib.mvc.*" %>

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

<td valign="top" class="td2PageBody">
<form name='form' method='post' onSubmit='return false;' id="form">

<%
Hashtable<String,String> hidden = pd.getHidden();
if (hidden!=null) {
    for (Enumeration<String> e = hidden.keys() ; e.hasMoreElements() ;) {
	String key = e.nextElement();
	String value = (String) hidden.get(key);
	%><input type="hidden" name=<%= key %> value=<%= value %>><%
    }
}

Vector<GeneralLink> titlelist = pd.getTitleList();
String titoloSezione = Costanti.LABEL_TITOLO_SEZIONE_DEFAULT;
if (titlelist != null && titlelist.size() > 0) {
	GeneralLink l = titlelist.elementAt(titlelist.size() -1);
	titoloSezione = l.getLabel();
} 
%>


<jsp:include page="/jsplib/titlelist.jsp" flush="true">
	<jsp:param name="showListInfos" value="true"/>
</jsp:include>

<table class="tabella-ext">

<%
boolean mostraFormHeader = (
		pd.getSearch().equals("on") || 
		(pd.getSearch().equals("auto") && (pd.getNumEntries() > pd.getSearchNumEntries()))
	) || 
	(
		pd.getFilterNames() != null &&
		pd.getFilterValues().size()>0
	);

int colFormHeader = (mostraFormHeader ? 2 : 1);
String classPanelTitolo = mostraFormHeader ? "panelListaRicerca" : "panelListaRicercaNoForm";

%>
	<tr>
		<td valign=top>
			<div class="<%= classPanelTitolo %>" >
				<table class="tabella" id="panelListaRicercaHeader">
					<tbody>
						<tr>
							<td class="titoloSezione" id="searchFormHeader" colspan="<%= colFormHeader %>">
								<span class="history"><%=titoloSezione %></span>
							</td>
							<% if(mostraFormHeader) { %>
								<td class="titoloSezione titoloSezione-right">
									<span class="icon-box" id="iconaPanelListaSpan">
										<i class="material-icons md-24" id="iconaPanelLista">&#xE152;</i>
									</span>
								</td>
							<% }%>
						</tr>
						</tbody>
				</table>
				<% 
					if ( mostraFormHeader ) {
				%>
				<table class="tabella" id="searchForm">
					<tbody>
						<tr>
							<td class="spazioSottoTitolo">
								<span>&nbsp;</span>
							</td>
						</tr>
	
						<%
						if (pd.getFilterValues() != null) {
							for(int iPD=0; iPD<pd.getFilterValues().size(); iPD++){

								DataElement filtroName = pd.getFilterNames().get(iPD);

								DataElement filtro = pd.getFilterValues().get(iPD);
								String filterName = filtro.getName();
							  	String [] values = filtro.getValues();
							  	String [] labels = filtro.getLabels();
							  	String selezionato = filtro.getSelected();
								String selEvtOnChange = !filtro.getOnChange().equals("") ? (" onChange=\"Change(document.form,'"+filterName+"')\" " ) : " ";
								String classInput = filtro.getStyleClass();
							  	%>
										<tr>
											<td>
												<div class="prop">

													<input type="hidden" name="<%= filtroName.getName() %>" value="<%= filtroName.getValue() %>"/>

													<label><%= filtro.getLabel() %></label>
												  	<select name="<%= filterName %>" <%= selEvtOnChange %> class="<%= classInput %>">
												  	<%
												  	for (int i = 0; i < values.length; i++) {
												  		String optionSel = values[i].equals(selezionato) ? " selected " : " ";
												  		%><option value="<%= values[i]  %>" <%=optionSel %> ><%= labels[i] %></option><%
												  	}
												  	%></select>
												</div>
											</td>
										</tr>	
						<%	}
						} %>


						<%
						if (pd.getSearch().equals("on") || (pd.getSearch().equals("auto") && (pd.getNumEntries() > pd.getSearchNumEntries()) )) {
							String searchDescription = pd.getSearchDescription();
							String searchLabelName = pd.getSearchLabel();
							boolean searchNote = pd.isSearchNote();
							%>
									<tr>
										<td>
											<div class="prop">
												<label><%=searchLabelName %></label>
												<input type="text" name="search" class="inputLinkLong" value="<%=searchDescription %>"/>
												<% if(searchNote && !searchDescription.equals("")){ %>
								      				<p class="note-ricerca">Attenzione! &Egrave; attualmente impostato il filtro di ricerca con la stringa '<%=searchDescription %>'</p>
								      			<% } %>
											</div>
										</td>
									</tr>	
						
						<% } %>
						
								<tr>
									<td class="buttonrow">
										<div class="buttonrowricerca">
											<input type="button" onClick="Search(document.form)" value='Filtra' />
											<input type="button" onClick="Reset(document.form);" value='Ripulisci' />
										</div>								
									
									</td>
								</tr>
					</tbody>
				</table>
				<% } %>
			</div>
		</td>
	</tr>
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
						int labelLength = labels != null ? labels.length : 0;
						
						Vector<?> v = pd.getDati();
						String stile= "";
						
						int index = pd.getIndex();
					  	if (pd.getNumEntries() > 0){
					    	index++;
						}
						String dataScrollerTopLabel = "Visualizzati record ["+index+"-"+(v.size()+pd.getIndex())+"] su " + pd.getNumEntries();
						%>
					
						<!-- data scroller top -->
						<tr>
							<td colspan="<%= labelLength + 1%>">
								<div class="buttonrowdatascroller dsTop" align="center">
									<table>
										<tbody>
											<tr>
												<td>
													<%
													//Bottone Previous
													if (pd.getIndex() != 0) {
														%>							
														<img id="ds_prev_top" src="images/tema_link/go_prev.png" onclick="PrevPage(document.form.limit.options[document.form.limit.selectedIndex].value)" title="Precedente"  class="dsImg" />
														<%
													} else{
														%>
														<img id="ds_prev_disabled_top" src="images/tema_link/go_prev_disabilitato.png">
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
													   			<img id="ds_next_top" src="images/tema_link/go_next.png" onClick="NextPage()" title="Successiva" class="dsImg"/>
													   			<%
													  }
													}
													
													if (nextTopDisabled) {
														%>
															<img id="ds_next_disabled_top" src="images/tema_link/go_next_disabilitato.png" />
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
					
						<tr class="tableHeader">
							<%
							if (v.size()> 0 && pd.getSelect()) {
							  %>
							  <td style="width:30px;">
							  	<div align="center">
							  		<input id="chkAll" type="checkbox" name="chkAll" onclick="checkAll();"/> 
							  	</div>
							  </td>
							  <%
							}
							%>
					
							<%
							
							if(labels!=null){
								for (int i = 0; i < labelLength ;i++) {
								  %><td><span><%= labels[i] %></span></td><%
								}
							}
							%>
					
						</tr>
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
						  	Vector<?> e = (Vector<?>) v.elementAt(i);	  
						
						  	for (int j = 0; j < e.size(); j++) {
						  	    DataElement de = (DataElement) e.elementAt(j);
								if(de.getType().equals("text")) {
									// setto l'id per la rimozione in caso non sia settato, N.B. spostato qui perche ora le checkbox statnno a sx.
									if(idToRemove==null) idToRemove = de.getIdToRemove();
								}
						  	}
						  	
							if (pd.getSelect()) {
							   %>
								<td class="tdText">
							   		<div align="center">
							   			<input id='_<% if(idToRemove!=null) out.write(idToRemove);else out.write(""+i); %>' type="checkbox" name="selectcheckbox" value='<% if(idToRemove!=null) out.write(idToRemove);else out.write(""+i); %>'/>
							   		</div>
							   	</td><%
							 }
								  
							for (int j = 0; j < e.size(); j++) {
							    DataElement de = (DataElement) e.elementAt(j);
							    String deName = !de.getName().equals("") ? de.getName() : "de_name_"+j;
							    String classLink = "";
							    String classSpan = de.getLabelStyleClass();
							    String tdStyle = " "; 
							    if (!de.getStyle().equals("")) {
							    	tdStyle = " style=\""+ de.getStyle() +"\"";
						  		}
							    
							    // se e' un elemento visualizzabile inserisco una cella
							    if (!de.getType().equals("hidden")) {
							      %><td class="tdText" <%=tdStyle %>><%
							    }
					
								if (de.getType().equals("text")) {
						    		// tipo link
					      			if (!de.getUrl().equals("")) {
							    		//tooltip
								  		String tip = "";
								  		boolean showTip=false;
								  		if(de.getToolTip()!=null && !"".equals(de.getToolTip())){
								  			tip=de.getToolTip();
								  			showTip=true;
								  		}
								  		//se la lunghezza del dato impostato come value e' > della larghezza della
								  		//colonna allora accorcio il dato ed imposto il tooltip
								  		//15 e' il valore di default impostato nel dataelement
								  		// [TODO] questa dimensione e' da rivedere
								  		int size=de.getSize();
								  		String res=de.getValue();
								  		if(size>15 && de.getValue().trim().length()>size){
								  			res=de.getValue().trim().substring(0,size)+" ...";
								  			//se nn e' stato specificato un tip precedentemente
								  			//metto come tip il valore completo del campo
								  			if(!showTip) tip=de.getValue();
								  			
								  			showTip=true;
								  		}
								  		
								  		String deTarget = " ";
								  		if (!de.getTarget().equals("")) {
								  			deTarget = " target=\""+ de.getTarget() +"\"";
								  		}
								  		
								  		String deTip = " ";
								  		if(showTip){
								  			deTip = " title=\"" + tip + "\"";
								  		}
								  		
								  		%><a class="<%= classLink %>" <%= deTip %> <%=deTarget %> href="<%= de.getUrl() %>"><%= res %></a><%
								  		
							      	} else {
										//no url
										if (!de.getOnClick().equals("")) {
										  //onclick
										  %><a class="<%= classLink %>" href='' onClick="<%= de.getOnClick() %>; return false;"><%= de.getValue() %></a><%
										} else {
										  //string only
										  %><span class="<%= classSpan %>"><%= de.getValue() %></span><%
										}
							      	}
								} else { 
								      // Tipo hidden
								      if (de.getType().equals("hidden")) {
										%><input type="hidden" name="<%= deName %>" value="<%= de.getValue() %>" /><%
								      } else {
										// Tipo image
										if (de.getType().equals("image")) {
									
										  String[] stValue = de.getValue().split("\\s");
										  String[] stUrl = de.getUrl().split("\\s");
										  String[] stOnClick = de.getOnClick().split("\\s");
									
										  // Ciclo sulla lista di immagini
										  for (int x=0; x<stValue.length; x++) {
									
										    // Se e' definito 'Url'
										    if (stUrl.length > x && stUrl[x] != "") {
										      %><a class="<%= stile %>" href="<%= stUrl[x] %>"><img src="images/<%= stValue[x] %>"></a>&nbsp;<%
										    } else {
									
										      // Se e' definito 'OnClick'
										      if (stOnClick.length > x && stOnClick[x] != "") {
											%><a class="<%= stile %>" href='' onClick="<%= stOnClick[x] %>; return false;"><img src="images/<%= stValue[x] %>"></a>&nbsp;<%
										      } else {
									
											// Solo immagine
											%><img src="images/<%= stValue[x] %>">&nbsp;<%
										      }
										    }
										  }
										} else {
								        	  if (de.getType().equals("radio")) {
										   		String[] stValues = de.getValues();
									    		String[] stLabels = de.getLabels();
									
											    // Ciclo sulla lista di valori 
											    for (int r = 0; r < stValues.length; r++) {
											      if (stValues[r].equals(de.getSelected())) {
										                 %><input type="radio" checked name='<%= deName %>' value='<%= stValues[r] %>'>&nbsp;&nbsp;<%= stLabels[r] %><%
											      } else {
										                 %><input type="radio" name='<%= deName %>' value='<%= stValues[r] %>'>&nbsp;&nbsp;<%= stLabels[r] %><%
										              }
										    	  if (r<stValues.length-1) {
													%><br/><%
									              }
										    	}
									  		} else { 
									  			 if (de.getType().equals("checkbox")) {
												 	String image = "disabled_red.png";
												 	if("yes".equals(de.getSelected())){
														image = "check_green.png";
													}
													else if("warn".equals(de.getSelected())){
														image = "check_yellow.png";
													}
													else if("off".equals(de.getSelected())){
														image = "disconnected_grey.png";
													}
									  				String tooltip = !de.getToolTip().equals("") ? " title=\"" + de.getToolTip() + "\"" : ""; 
									  				 
									  				// tipo link
										      		if (!de.getUrl().equals("")) { 
										      			String deTarget = " ";
												  		if (!de.getTarget().equals("")) {
												  			deTarget = " target=\""+ de.getTarget() +"\"";
												  		} 
												  		%><div style="text-align: center;">
												  			<a class="<%= classLink %>" <%=deTarget %> href="<%= de.getUrl() %>">
												  				<img src="images/tema_link/<%= image %>" <%= tooltip %>/>
															</a>
														</div><%
										      			
										      		}else {
									  				 	%><div style="text-align: center;"><img src="images/tema_link/<%= image %>" <%= tooltip %>/>&nbsp;</div><%
										      		}
									  			 } // enc checkbox
									  		} // end else radio
										} // end else image
						      		} // end else hidden
						    	} // end else text
					
							    if (!de.getType().equals("hidden")) {
							      %></td><%
							    }
					 		 }
					 	 %></tr><%
					}
					
					//fine entries
					
					//navigazione tra la pagine visualizzata se il numero di entries > 20
					if (pd.getNumEntries() > 20){
						%>
						<tr>
							<td colspan="<%= labelLength + 1 %>">
								<div class="buttonrowdatascroller dsBottom" align="center" >
									<table>
										<tbody>
											<tr>
											<td>
										<%
										//Bottone Previous
										if (pd.getIndex() != 0) {
											%>							
											<img id="ds_prev_bottom" src="images/tema_link/go_prev.png" onclick="PrevPage(document.form.limit.options[document.form.limit.selectedIndex].value)" title="Precedente"  class="dsImg" />
											<%
										} else{
											%>
											<img id="ds_prev_disabled_bottom" src="images/tema_link/go_prev_disabilitato.png">
											<%
										}
	
										//Scelta numero di entries da visualizzare
										if ((pd.getNumEntries() > 20) || (pd.getIndex() != 0)) {
										  %></td>
											<td><select name="limit" onChange="CambiaVisualizzazione(document.form.limit.options[selectedIndex].value)"><%
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
										  %></select></td>
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
										   			<img id="ds_next_bottom" src="images/tema_link/go_next.png" onClick="NextPage()" title="Successiva" class="dsImg"/>
										   			<%
										  }
										}
										
										if (nextBottomDisabled) {
											%>
												<img id="ds_next_disabled_bottom" src="images/tema_link/go_next_disabilitato.png" />
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
							<td colspan=<%= labelLength + 1 %> class="buttonrow">
								<div class="buttonrowlista">
									<%
									Vector<?> areaBottoni = pd.getAreaBottoni();
									if (areaBottoni != null) {
										for (int i = 0; i < areaBottoni.size(); i++) {
									  		AreaBottoni area = (AreaBottoni) areaBottoni.elementAt(i);
									  		Vector<?> bottoni = area.getBottoni();
									 		for (int b = 0; b < bottoni.size(); b++) {
									   			DataElement bottone = (DataElement) bottoni.elementAt(b);
									   			%>
									   			<input type="button" onClick="<%= bottone.getOnClick() %>" value='<%= bottone.getValue() %>'/>
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
									  %><input type="button" onClick="AddEntry()" value='Aggiungi' /><%
									}
									
									%>
								</div>
							</td>
						</tr>
						<% 
					} else if(pd.getAddButton()) {
						%>
						<tr>
							<td colspan=<%= labelLength + 1 %> class="buttonrow">
								<div class="buttonrowlista">
									<input type="button" onClick="AddEntry()" value='Aggiungi' />
								</div>
							</td>
						</tr>
						<%
					} else{
						%>
						 <tr class="buttonrownobuttons">
						  <td colspan="<%= labelLength + 1 %>">&nbsp;</td>
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
