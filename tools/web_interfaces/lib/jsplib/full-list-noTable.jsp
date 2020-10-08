<%--
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it). 
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

String customListViewName = pd.getCustomListViewName();
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
	
	int indexLabel = titlelist.size() -1;
	if(titlelist.size()==2 && Costanti.PAGE_DATA_TITLE_LABEL_RISULTATI_RICERCA.equals(titlelist.get(1).getLabel())){
		indexLabel = 0;
	}
	
	GeneralLink l = titlelist.elementAt(indexLabel);
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
										<i class="material-icons md-24" id="iconaPanelLista">&#xE8B6;</i>
									</span>
								</td>
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
	
						<%
						if (pd.getFilterValues() != null) {
							for(int iPD=0; iPD<pd.getFilterValues().size(); iPD++){

								DataElement filtroName = pd.getFilterNames().get(iPD);

								DataElement filtro = pd.getFilterValues().get(iPD);
								String filterName = filtro.getName();
							  	String [] values = filtro.getValues();
							  	String [] labels = filtro.getLabels();
							  	String selezionato = filtro.getSelected();
								String selEvtOnChange = !filtro.getOnChange().equals("") ? (" onChange=\""+ Costanti.JS_FUNCTION_VISUALIZZA_AJAX_STATUS +"Change(document.form,'"+filterName+"')\" " ) : " ";
								String classInput = filtro.getStyleClass();
								String filterId = filterName + "__id";
							  	%>
										<tr>
											<td>
												<div class="prop">

													<input type="hidden" name="<%= filtroName.getName() %>" value="<%= filtroName.getValue() %>"/>

													<label><%= filtro.getLabel() %></label>
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
											<input type="button" onClick="<%=visualizzaAjaxStatusFiltra %>Search(document.form)" value='<%=pd.getLabelBottoneFiltra() %>' />
											<input type="button" onClick="<%=visualizzaAjaxStatusRipulisci %>Reset(document.form);" value='<%=pd.getLabelBottoneRipulsci() %>' />
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
						int baseLength = pd.getSelect() ? 1 : 0;
						int labelLength = (labels != null ? labels.length : 1);
						int colSpanLength = labelLength + baseLength;
						
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
						
						<% if(labels!=null){ %>
						<tr class="tableHeader">
							<%
							if (v.size()> 0 && pd.getSelect()) {
							  %>
							  <td style="width:16px;">
							  	<div align="center">
							  		<input id="chkAll" type="checkbox" name="chkAll" onclick="checkAll();"/> 
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
						  	Vector<?> e = (Vector<?>) v.elementAt(i);	  
						
						  	for (int j = 0; j < e.size(); j++) {
						  	    DataElement de = (DataElement) e.elementAt(j);
								//if(de.getType().equals("text")) {
									// setto l'id per la rimozione in caso non sia settato, N.B. spostato qui perche ora le checkbox statnno a sx.
									if(idToRemove==null) idToRemove = de.getIdToRemove();
								//}
						  	}
						  	
							if (pd.getSelect()) {
								String checkBoxStyle = labels!=null ? "" : "style=\"width:16px;\"" ;
							   %>
								<td <%=checkBoxStyle %> class="tdText">
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
											<img id="ds_prev_bottom" src="images/tema_link/go_prev.png" onclick="<%= Costanti.JS_FUNCTION_VISUALIZZA_AJAX_STATUS %>PrevPage(document.form.limit.options[document.form.limit.selectedIndex].value)" title="Precedente"  class="dsImg" />
											<%
										} else{
											%>
											<img id="ds_prev_disabled_bottom" src="images/tema_link/go_prev_disabilitato.png">
											<%
										}
	
										//Scelta numero di entries da visualizzare
										if ((pd.getNumEntries() > 20) || (pd.getIndex() != 0)) {
										  %></td>
											<td><select name="limit" onChange="<%= Costanti.JS_FUNCTION_VISUALIZZA_AJAX_STATUS %>CambiaVisualizzazione(document.form.limit.options[selectedIndex].value)"><%
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
										   			<img id="ds_next_bottom" src="images/tema_link/go_next.png" onClick="<%= Costanti.JS_FUNCTION_VISUALIZZA_AJAX_STATUS %>NextPage()" title="Successiva" class="dsImg"/>
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
							<td colspan=<%= colSpanLength %> class="buttonrow">
								<div class="buttonrowlista">
									<%
									Vector<?> areaBottoni = pd.getAreaBottoni();
									if (areaBottoni != null) {
										for (int i = 0; i < areaBottoni.size(); i++) {
									  		AreaBottoni area = (AreaBottoni) areaBottoni.elementAt(i);
									  		Vector<?> bottoni = area.getBottoni();
									 		for (int b = 0; b < bottoni.size(); b++) {
									   			DataElement bottone = (DataElement) bottoni.elementAt(b);
									   			String visualizzaAjaxStatus = bottone.isShowAjaxStatus() ? Costanti.JS_FUNCTION_VISUALIZZA_AJAX_STATUS : "";
									   			%>
									   			<input type="button" onClick="<%= visualizzaAjaxStatus %><%= bottone.getOnClick() %>" value='<%= bottone.getValue() %>'/>
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
									  %><input type="button" onClick="<%= Costanti.JS_FUNCTION_VISUALIZZA_AJAX_STATUS %>AddEntry()" value='Aggiungi' /><%
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
