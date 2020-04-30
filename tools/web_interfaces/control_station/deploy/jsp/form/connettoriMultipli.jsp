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
<%@page import="org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="org.openspcoop2.web.ctrlstat.servlet.aps.erogazioni.ErogazioniCostanti"%>
<%@ page session="true" import="java.util.*, org.openspcoop2.web.lib.mvc.*" %>
<link rel="stylesheet" href="css/ui.tabs.css" type="text/css">
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
	
	String params = (String) request.getAttribute("params");

	if (params == null)
		 params="";

	ListElement listElement = 
		  (org.openspcoop2.web.lib.mvc.ListElement) session.getAttribute("ListElement");

	String nomeServlet = listElement.getOggetto();
	String nomeServletAdd = nomeServlet+"Add.do";
	String nomeServletDel = nomeServlet+"Del.do";
	String nomeServletList = nomeServlet+"List.do";
	
	String formatPar = listElement.formatParametersURL();
	
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
	
	
	Vector<?> datiConGruppi = pd.getDati();
	int n = datiConGruppi.size();
	
	Vector<GeneralLink> titlelist = pd.getTitleList();
	String titoloSezione = Costanti.LABEL_TITOLO_SEZIONE_DEFAULT;
	if (titlelist != null && titlelist.size() > 0) {
		GeneralLink l = titlelist.elementAt(titlelist.size() -1);
		titoloSezione = l.getLabel();
	} 
	
	String classSpanNoEdit="spanNoEdit";
	String classDivNoEdit="divNoEdit";
	
	int colSpanLength = 1;
%>
<SCRIPT>
var nomeServletAdd_Custom = '<%= nomeServletAdd %>';
var nomeServletDel_Custom = '<%= nomeServletDel %>';
var nomeServletList_Custom = '<%= nomeServletList %>';
</SCRIPT>
<jsp:include page="/jsp/listElementCustom.jsp" flush="true" />
<SCRIPT type="text/javascript">
var iddati = '<%= iddati %>';
var params = '<%= params %>';
var nomeServletAdd = nomeServletAdd_Custom;
var nomeServletDel = nomeServletDel_Custom;
var nomeServletList = nomeServletList_Custom;
var formatPar = '<%= formatPar %>';
var n = <%= n %>;
var index = <%= pd.getIndex() %>;
var pageSize = <%= pd.getPageSize() %>;
var nr = 0;

function checkAll(){
	if(n > 0){
		var chkAll = $("#chkAll:checked").length;
		
		if(chkAll > 0) {
			SelectAll();
		} else {
			DeselectAll();
		}
	}
}

function SelectAll() {
  if (n > 1) {
    for (var c = 0; c < document.form.selectcheckbox.length; c++)
      document.form.selectcheckbox[c].checked = true;
  } else {
    document.form.selectcheckbox.checked = true;
  }
};

function DeselectAll() {
  if (n > 1) {
    for (var c = 0; c < document.form.selectcheckbox.length; c++)
      document.form.selectcheckbox[c].checked = false;
  } else {
    document.form.selectcheckbox.checked = false;
  }
};

function RemoveEntries() {
  if (nr != 0) {
    return false;
  }
  var elemToRemove = '';
  
  var idTabSelezionato = $("#tabs").tabs('option', 'selected');
  
  // prendo l'id da rimuovere
  elemToRemove = $("#hiddenIdRemove-" + idTabSelezionato).val();
  
  // controllo se il tasto rimuovi e' visibile
  var isHidden = $('#rem_btn_2').is(':hidden');
  
  var parametroIdConnTab = '&<%=CostantiControlStation.PARAMETRO_ID_CONN_TAB %>='+idTabSelezionato;
 
  
//   if (n > 1) {
//     for (var j = 0; j < n; j++) {
//       if (document.form.selectcheckbox[j].checked) {
//         if (elemToRemove != '') {
//           elemToRemove += ',';
//         }
//         //elemToRemove += j;
//         elemToRemove +=document.form.selectcheckbox[j].value;
//       }
//     }
//   } else {
//     if (document.form.selectcheckbox.checked)
//       elemToRemove += document.form.selectcheckbox.value;
//   }
  if (!isHidden && elemToRemove != '') {
    nr = 1;
    if (formatPar != null && formatPar != "")
        document.location='<%= request.getContextPath() %>/'+nomeServletDel+'?'+formatPar+'&obj='+elemToRemove+'&iddati='+iddati+params+parametroIdConnTab;
      else
        document.location='<%= request.getContextPath() %>/'+nomeServletDel+'?obj='+elemToRemove+'&iddati='+iddati+params+parametroIdConnTab;
  }
};

function AddEntry() {
  if (nr != 0) {
    return false;
  }
  nr = 1;
  if (formatPar != null && formatPar != "")
 	  document.location='<%= request.getContextPath() %>/'+nomeServletAdd+'?'+formatPar+'&iddati='+iddati+params;
  else
	  document.location='<%= request.getContextPath() %>/'+nomeServletAdd+'?iddati='+iddati+params;
};

function CambiaVisualizzazione(newPageSize) {
  if (nr != 0) {
    return false;
  }
  nr = 1;
  if (newPageSize == '0') {
    index = 0; 
  }
  if (formatPar != null && formatPar != "")
    document.location='<%= request.getContextPath() %>/'+nomeServletList+'?'+formatPar+'&pageSize='+newPageSize+'&index='+index+'&iddati='+iddati+params;
  else
    document.location='<%= request.getContextPath() %>/'+nomeServletList+'?pageSize='+newPageSize+'&index='+index+'&iddati='+iddati+params;
};

function NextPage() {
  if (nr != 0) {
    return false;
  }
  nr = 1;
  index += pageSize;
  if (formatPar != null && formatPar != "")
    document.location='<%= request.getContextPath() %>/'+nomeServletList+'?'+formatPar+'&pageSize='+pageSize+'&index='+index+'&iddati='+iddati+params;
  else
    document.location='<%= request.getContextPath() %>/'+nomeServletList+'?pageSize='+pageSize+'&index='+index+'&iddati='+iddati+params;
};

function PrevPage(pageSize) {
  if (nr != 0) {
    return false;
  }
  nr = 1;
  index -= pageSize;
  if (index < 0) {
    index = 0;
  }
  if (formatPar != null && formatPar != "")
    document.location='<%= request.getContextPath() %>/'+nomeServletList+'?'+formatPar+'&pageSize='+pageSize+'&index='+index+'&iddati='+iddati+params;
  else
    document.location='<%= request.getContextPath() %>/'+nomeServletList+'?pageSize='+pageSize+'&index='+index+'&iddati='+iddati+params;
};

function Search(form) {
  if (nr != 0) {
    return false;
  }
  nr = 1;

  addHidden(form, 'index' , 0);
  addHidden(form, 'iddati' , iddati);
  addHidden(form, 'pageSize' , pageSize);

  // formatParams
  
   if (formatPar != null && formatPar != ""){
  	var pairs = ((formatPar[0] === '?' || formatPar[0] === '&') ? formatPar.substr(1) : formatPar).split('&');
  	for (var i = 0; i < pairs.length; i++) {
      	var pair = pairs[i].split('=');
      	addHidden(form, pair[0] , pair[1]);
  	}
   }
   if (params != null && params != ""){
	   var pairs = ((params[0] === '?' || params[0] === '&') ? params.substr(1) : params).split('&');
	   for (var i = 0; i < pairs.length; i++) {
	       var pair = pairs[i].split('=');
	       addHidden(form, pair[0] , pair[1]);
	   }
   }

  // imposto la destinazione
  document.form.action = nomeServletList;
      
  // form submit
  document.form.submit();
 
};

function Reset(form) {
	  if (nr != 0) {
	    return false;
	  }
	  nr = 1;
	  
	  document.form.reset();
 	  for (var k=0; k<document.form.elements.length; k++) {
		var name = document.form.elements[k].name;
		if (name == "search"){
			document.form.elements[k].value="";
		}
		var tipo = document.form.elements[k].type;
		if (tipo == "select-one" || tipo == "select-multiple") {
			document.form.elements[k].selectedIndex = 0;
		}
	  }

	  addHidden(form, 'index' , 0);
	  addHidden(form, 'iddati' , iddati);
	  addHidden(form, 'pageSize' , pageSize);

	  // formatParams
	  
	   if (formatPar != null && formatPar != ""){
	  	var pairs = ((formatPar[0] === '?' || formatPar[0] === '&') ? formatPar.substr(1) : formatPar).split('&');
	  	for (var i = 0; i < pairs.length; i++) {
	      	var pair = pairs[i].split('=');
	      	addHidden(form, pair[0] , pair[1]);
	  	}
	   }
	   if (params != null && params != ""){
		   var pairs = ((params[0] === '?' || params[0] === '&') ? params.substr(1) : params).split('&');
		   for (var i = 0; i < pairs.length; i++) {
		       var pair = pairs[i].split('=');
		       addHidden(form, pair[0] , pair[1]);
		   }
	  }
	  
	   // imposto la destinazione
	   document.form.action = nomeServletList;
	   
	  // form submit
	  document.form.submit();
	 
	};

function Export(url){
	document.location='<%= request.getContextPath() %>/'+url
};

function Esporta(tipo) {

	 var elemToExport = '';
	  if (n > 1) {
	    for (var j = 0; j < n; j++) {
	      if (document.form.selectcheckbox[j].checked) {
	        if (elemToExport != '') {
	        	elemToExport += ',';
	        }
	        //elemToRemove += j;
	        elemToExport +=document.form.selectcheckbox[j].value;
	      }
	    }
	  } else {
	    if (document.form.selectcheckbox.checked)
	    	elemToExport +=document.form.selectcheckbox.value;
	  }


	if(elemToExport !== '') {
		<%= Costanti.JS_FUNCTION_VISUALIZZA_AJAX_STATUS %>
		document.location = "<%= request.getContextPath() %>/export.do?tipoExport="+tipo+"&obj="+elemToExport; 
	} else {
		$( "#selezioneRichiestaModal" ).dialog( "open" );
	} 
		  
};

function Change(form,dataElementName) {
    
    //aggiungo parametro per indicare che si tratta di postback e azzero idhid
    addHidden(form, 'isPostBack' , true);
    if(dataElementName!=null)
    	addHidden(document.form, 'postBackElementName' , dataElementName);
    addHidden(form, 'index' , 0);
    addHidden(form, 'iddati' , iddati);
  
    // formatParams
    
     if (formatPar != null && formatPar != ""){
    	var pairs = ((formatPar[0] === '?' || formatPar[0] === '&') ? formatPar.substr(1) : formatPar).split('&');
    	for (var i = 0; i < pairs.length; i++) {
        	var pair = pairs[i].split('=');
        	addHidden(form, pair[0] , pair[1]);
    	}
     }
     if (params != null && params != ""){
	   var pairs = ((params[0] === '?' || params[0] === '&') ? params.substr(1) : params).split('&');
	   for (var i = 0; i < pairs.length; i++) {
	       var pair = pairs[i].split('=');
	       addHidden(form, pair[0] , pair[1]);
	   }
     }
        
    // form submit
    document.form.submit();
}

function addHidden(theForm, name, value) {
    // Create a hidden input element, and append it to the form:
    var input = document.createElement('input');
    input.type = 'hidden';
    input.name = name;
    input.value = value;
    theForm.appendChild(input);
}

var panelListaRicercaOpen = false; // controlla l'aperture del pannello di ricerca.
<%
if ( 
	(
		pd.getSearch().equals("on") || 
		(pd.getSearch().equals("auto") && (pd.getNumEntries() > pd.getSearchNumEntries()))
	) || 
	(
		pd.getFilterNames() != null &&
		pd.getFilterValues().size()>0
	)
) {

	String searchDescription = pd.getSearchDescription();
	if (!searchDescription.equals("") || (pd.getFilterNames() != null && pd.hasAlmostOneFilterDefined())){
	%>	panelListaRicercaOpen = true; <% 
	} 
}%>

</SCRIPT>
<!--Funzioni di utilita -->
<script type="text/javascript" src="js/ui.core.js"></script>
<script type="text/javascript" src="js/ui.dialog.js"></script>
<script type="text/javascript" src="js/ui.tabs.js"></script>
<script type="text/javascript">
function togglePanelListaRicerca(panelListaRicercaOpen){
	if(panelListaRicercaOpen) {
    	$("#searchForm").removeClass('searchFormOff');
    	$("#searchForm").addClass('searchFormOn');
    	
    	if($( "#iconaPanelListaSpan" ).length > 0){
    		$('#iconaPanelListaSpan').attr('title', '<%=Costanti.TOOLTIP_NASCONDI_FILTRI_RICERCA %>');
    	}
    	
    	// reinit select del filtro
    	if($('select[id^=filterValue_]').length > 0){
    		// elimino eventuali plugin gia' applicati
    		$('select[id^=filterValue_]').each(function() {
    			var wrapper = $( this ).parent();
    			if(wrapper.attr('id').indexOf('_wrapper') > -1) {
    				$( this ).appendTo($( this ).parent().parent());
    				wrapper.remove();
    				$( this ).css('width','');
    				$( this ).css('height','');
    			}
    			
    			var checkID = $( this ).attr('id') + '_hidden_chk';
    			if($( '#' + checkID ).length > 0) {
    				var val = $( '#' + checkID ).attr('value');
    				if(val && val == 'true'){
    					$( this ).searchable({disableInput : false});	
    				} else {
    					$( this ).searchable({disableInput : true});	
    				}
    			} else {
    				$( this ).searchable({disableInput : true});
    			}
			});
    	}
    } else {
    	$("#searchForm").removeClass('searchFormOn');
    	$("#searchForm").addClass('searchFormOff');
    	
    	if($( "#iconaPanelListaSpan" ).length > 0){
    		$('#iconaPanelListaSpan').attr('title', '<%=Costanti.TOOLTIP_VISUALIZZA_FILTRI_RICERCA %>');
    	}
    }
}

	$(document).ready(function(){
		// pannello ricerca che si apre e chiude iconaPanelLista
		$("#panelListaRicercaHeader").click(function(){
			panelListaRicercaOpen = !panelListaRicercaOpen;
			togglePanelListaRicerca(panelListaRicercaOpen);
		});
		
		togglePanelListaRicerca(panelListaRicercaOpen);
		
		document.form.action = '';
	 });
</script>
<script type="text/javascript" src="js/utils.js"></script>
<script type="text/javascript" src="js/jquery-on.js"></script>
<script type="text/javascript" src="js/jquery.searchabledropdown-1.0.8.min.js"></script>
<tbody>
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
			<div class="spacer">
				<%
					
					String tabSelezionatoS = ServletUtils.getObjectFromSession(session, String.class, CostantiControlStation.PARAMETRO_ID_CONN_TAB);
					int tabSelezionato = 0;
					
					if(StringUtils.isNotEmpty(tabSelezionatoS)) {
						try{
							tabSelezionato = Integer.parseInt(tabSelezionatoS);
						}catch(Exception e){ tabSelezionato = 0; }
					}
					
					Hashtable<String,String> hidden = pd.getHidden();
					if (hidden!=null) {
					    for (Enumeration<String> e = hidden.keys() ; e.hasMoreElements() ;) {
						String key = e.nextElement();
						String value = (String) hidden.get(key);
						%><input type="hidden" name=<%= key %> value=<%= value %>><%
					    }
					}
				%>
			</div>
		</td>
	</tr>
		<!-- Riga tabella -->
		<tr> 
			<td valign=top colspan="2">		
				<div class="panelLista">
					<table id="tabellaInterna" class="tabella">
					<%
						Vector<?> v = pd.getDati();
						int index = pd.getIndex();
					  	if (pd.getNumEntries() > 0){
					    	index++;
						}
					  	
					  	boolean mostraTab = v.size() > 1;
						
						%>
							<tr class="even">
								<td colspan="2">
								<div id="tabs">
									<% 
										if(mostraTab) { // etichette dei tab
											
									%>	
										<ul id="tabsNav">
											<%
												for (int i = 0; i < v.size(); i++) {
													String idTab = "#tabs-"+i;
													String idLi = "li-tabs-"+i;
													Vector<?> dati = (Vector<?>) v.elementAt(i);
													
													String titoloTab = "Tab" + (i+1);
													for (int j = 0; j < dati.size(); j++) {
													    DataElement de = (DataElement) dati.elementAt(j);
													    // if (de.getType().equals("text")) {
													    titoloTab = de.getValue();
													    break;
													    //} 
													}
													
													String tooltipTab = "";
													
													if(titoloTab.length() > Costanti.LUNGHEZZA_LABEL_TABS) {
														tooltipTab = " title=\"" + titoloTab + "\"";
														titoloTab = titoloTab.substring(0,(Costanti.LUNGHEZZA_LABEL_TABS -3)) + "...";
														
													}
											%>
										    	<li id="<%=idLi %>" ><a href="<%=idTab %>" ><span class="ui-tabs-anchor" <%= tooltipTab %> ><%=titoloTab %></span></a></li>
										    <%
												}
										    %>
										 </ul>
									<% } %>
									
									<%
										for (int z = 0; z < v.size(); z++) {
											String idTab = "tabs-"+z;
											Vector<?> dati = (Vector<?>) v.elementAt(z);
											Vector<DataElement> vectorRiepilogo = new Vector<DataElement>();
											Vector<DataElement> vectorLink = new Vector<DataElement>();
											
											for (int j = 0; j < dati.size(); j++) {
											    DataElement de = (DataElement) dati.elementAt(j);
											    
											    if (de.getType().equals("link")) {
											    	vectorLink.add(de);
											    } else {
											    	vectorRiepilogo.add(de);
											    }
											}
										%>
									<div id="<%=idTab %>">
										<table class="tabella">
											<%
												boolean firstText = true;
												for (int i = 0; i < vectorRiepilogo.size(); i++) {
													DataElement de = (DataElement) vectorRiepilogo.elementAt(i);
												  
													String hiddenIdRemove = "hiddenIdRemove-"+z;
													String deName = !de.getName().equals("") ? de.getName() : "de_name_"+z+"_"+i;
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
									            				
									            				String tooltipTextValNoEdit = "";
																
																if(textValNoEdit.length() > Costanti.LUNGHEZZA_RIGA_TESTO_TABELLA) {
																	tooltipTextValNoEdit = " title=\"" + textValNoEdit + "\"";
																	textValNoEdit = textValNoEdit.substring(0,(Costanti.LUNGHEZZA_RIGA_TESTO_TABELLA -3)) + "...";
																}
																else if(de.getToolTip()!=null && !de.getToolTip().equals("")){
																	tooltipTextValNoEdit = " title=\"" + de.getToolTip() + "\"";	
																}
									            				
									            				%>
									                			<tr class="">
																	<td class="tdTextRiepilogo labelRiepilogo">
																		<label class="<%= labelStyleClass %>"><%=deLabel %></label>
																	</td>
																	<td class="tdTextRiepilogo <%= stile %>">
																		<div class="<%=classDivNoEdit %>"> 
																			<%
																		   		String idToRemoveTab = de.getIdToRemove() != null ? de.getIdToRemove() : "";
																		   		if(firstText && StringUtils.isNotEmpty(idToRemoveTab)){
																		   			%>
																		   				<input id="<%=hiddenIdRemove %>" type="hidden" name="selectcheckbox" value="<%=idToRemoveTab %>"/>
																		   			<%
																				}%>
											                				<span class="<%=classSpanNoEdit %>" <%= tooltipTextValNoEdit %> ><%= textValNoEdit %></span>
											                				<% if(firstText){%>
												                				<input type="hidden" name="<%= deName %>" id="<%= deName %>"  value="<%= de.getValue() %>"/>
										                					<% } %>
																		<% 
																			if(!de.getListaImages().isEmpty()){
																				for(int idxLink =0; idxLink < de.getListaImages().size() ; idxLink ++ ){
																					DataElementImage image = de.getListaImages().get(idxLink);
																					String classLink = "";
																					String deIconName = image.getImage(); 
										                					
																					String deTip = !image.getToolTip().equals("") ? " title=\"" + image.getToolTip() + "\"" : "";
										                							
										                							String deTarget = " ";
																			  		if (!image.getTarget().equals("")) {
																			  			deTarget = " target=\""+ image.getTarget() +"\"";
																			  		}
																			  		
																			  		String visualizzaAjaxStatus = image.isShowAjaxStatus() ? Costanti.JS_FUNCTION_VISUALIZZA_AJAX_STATUS : "";
																		  			
											                					%>
											                					<a class="edit-link <%= classLink %>" <%= deTip %> <%=deTarget %> href="<%= image.getUrl() %>" type="button" onClick="<%= visualizzaAjaxStatus %>return true;">
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
										                			if (type.equals("checkbox")){
										                				String statusValue = de.getStatusValues() != null && de.getStatusValues().length>0 ? de.getStatusValues()[0] : "";
										                				String statusValueText = statusValue != null && !statusValue.equals("") ? statusValue : (pd.getMode().equals("view-noeditbutton") ? "&nbsp;" : "&nbsp;");
																						
										                				String statusTooltip = de.getStatusToolTips() != null && de.getStatusToolTips().length>0 ? de.getStatusToolTips()[0] : "";		
										                				String statusTooltipTitleAttribute = statusTooltip != null && !statusTooltip.equals("") ? " title=\"" + statusTooltip + "\"" : "";
										                				
																		String statusType = de.getStatusTypes() != null && de.getStatusTypes().length>0 ? de.getStatusTypes()[0] : "";			
										                					
																		%>
										                					<tr class="">
																				<td class="tdTextRiepilogo labelRiepilogo">
																					<label class="<%= labelStyleClass %>"><%=deLabel %></label>
																				</td>
																				<td class="tdTextRiepilogo <%= stile %>">
																				<div class="<%=classDivNoEdit %>"> 
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
																							<img src="images/tema_link/<%= imageCheckBox %>"/>
																						</span>
																						<%
																					   		String idToRemoveTab = de.getIdToRemove() != null ? de.getIdToRemove() : "";
																					   		if(firstText && StringUtils.isNotEmpty(idToRemoveTab)){
																					   			%>
																					   				<input id="<%=hiddenIdRemove %>" type="hidden" name="selectcheckbox" value="<%=idToRemoveTab %>"/>
																					   			<%
																							}%>
														                				<span class="<%=classSpanNoEdit %>" <%= statusTooltipTitleAttribute %> ><%= statusValueText %></span>
														                				<% if(firstText){%>
															                				<input type="hidden" name="<%= deName %>" id="<%= deName %>"  value="<%= de.getValue() %>"/>
													                					<% } %>
																						 <% 
																						if(!de.getListaImages().isEmpty()){
																							for(int idxLink =0; idxLink < de.getListaImages().size() ; idxLink ++ ){
																								DataElementImage image = de.getListaImages().get(idxLink);
																								String classLink = "";
																								String deIconName = image.getImage(); 
													                					
																								String deTip = !image.getToolTip().equals("") ? " title=\"" + image.getToolTip() + "\"" : "";
													                							
													                							String deTarget = " ";
																						  		if (!image.getTarget().equals("")) {
																						  			deTarget = " target=\""+ image.getTarget() +"\"";
																						  		}
																						  		
																						  		String visualizzaAjaxStatus = image.isShowAjaxStatus() ? Costanti.JS_FUNCTION_VISUALIZZA_AJAX_STATUS : "";
																					  			
														                					%>
														                					<a class="edit-link <%= classLink %>" <%= deTip %> <%=deTarget %> href="<%= image.getUrl() %>" type="button" onClick="<%= visualizzaAjaxStatus %>return true;">
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
										                			} else { // else checkbox
										                				if (type.equals("multi-select")){
										                					%>
										                					<tr class="">
																				<td class="tdTextRiepilogo labelRiepilogo">
																					<label class="<%= labelStyleClass %>"><%=deLabel %></label>
																				</td>
																				<td class="tdTextRiepilogo <%= stile %>">
																					<div class="<%=classDivNoEdit %>"> 
																						<%
																					   		String idToRemoveTab = de.getIdToRemove() != null ? de.getIdToRemove() : "";
																					   		if(firstText && StringUtils.isNotEmpty(idToRemoveTab)){
																					   			%>
																					   				<input id="<%=hiddenIdRemove %>" type="hidden" name="selectcheckbox" value="<%=idToRemoveTab %>"/>
																					   			<%
																							}
																						%>
																						
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
																												<img src="images/tema_link/<%= imageCheckBox %>"/>
																											</span>
																											<span class="<%=classSpanNoEdit %>-msval" <%= statusTooltipTitleAttribute %> ><%= lab %></span>
																										<%
								                            									} //end for values
								                                        					}
							                          									%>
																				
																						<% 
																							if(!de.getListaImages().isEmpty()){
																								for(int idxLink =0; idxLink < de.getListaImages().size() ; idxLink ++ ){
																									DataElementImage image = de.getListaImages().get(idxLink);
																									String classLink = "";
																									String deIconName = image.getImage(); 
														                					
																									String deTip = !image.getToolTip().equals("") ? " title=\"" + image.getToolTip() + "\"" : "";
														                							
														                							String deTarget = " ";
																							  		if (!image.getTarget().equals("")) {
																							  			deTarget = " target=\""+ image.getTarget() +"\"";
																							  		}
																							  		
																							  		String visualizzaAjaxStatus = image.isShowAjaxStatus() ? Costanti.JS_FUNCTION_VISUALIZZA_AJAX_STATUS : "";
															                					%>
															                					<a class="edit-link <%= classLink %>" <%= deTip %> <%=deTarget %> href="<%= image.getUrl() %>" type="button" onClick="<%= visualizzaAjaxStatus %>return true;">
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
																		} else { // else multi-select
																			if (type.equals("button")){
																				%>
											                					<tr class="">
																					<td class="tdTextRiepilogo labelRiepilogo">
																						<label class="<%= labelStyleClass %>"><%=deLabel %></label>
																					</td>
																					<td class="tdTextRiepilogo <%= stile %>">
																						<div class="<%=classDivNoEdit %>"> 																	
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
																								if(!de.getListaImages().isEmpty()){
																									for(int idxLink =0; idxLink < de.getListaImages().size() ; idxLink ++ ){
																										DataElementImage image = de.getListaImages().get(idxLink);
																										String classLink = "";
																										String deIconName = image.getImage(); 
															                					
																										String deTip = !image.getToolTip().equals("") ? " title=\"" + image.getToolTip() + "\"" : "";
															                							
															                							String deTarget = " ";
																								  		if (!image.getTarget().equals("")) {
																								  			deTarget = " target=\""+ image.getTarget() +"\"";
																								  		}
																								  		
																								  		String visualizzaAjaxStatus = image.isShowAjaxStatus() ? Costanti.JS_FUNCTION_VISUALIZZA_AJAX_STATUS : "";
																                					%>
																                					<a class="edit-link <%= classLink %>" <%= deTip %> <%=deTarget %> href="<%= image.getUrl() %>" type="button" onClick="<%= visualizzaAjaxStatus %>return true;">
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
																			} else { // else button
																				
																			} // end else button
											                			} // end else multi-select
											                		} // end else checkbox
										                		} // end else text
																%>
															<% 
										        			} // end else subtitle
											    		} // end else hidden
									        		} %>	
										</table>
									</div>
									<% 
									} // end tabs
									%>
								</div>				
						</td>
					</tr>	
					<%
					//Bottoni 
					if (pd.getSelect()) {
						%>
						<tr>
							<td colspan=<%= colSpanLength %> class="buttonrow">
								<div class="buttonrowlista">
									<%
									//Bottone di Remove
									if (v.size() > 1 && pd.getRemoveButton()) {
									  %><input id='rem_btn_2' type="button" value='Elimina' class="negative" /><%
									}
									
									//Bottone di Add
									if (pd.getAddButton()) {
									  %><input type="button" onClick="<%= Costanti.JS_FUNCTION_VISUALIZZA_AJAX_STATUS %>AddEntry()" value='Crea Nuovo' /><%
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
					
					
					if(mostraTab) {
						%> 
							<tr>
						  		<td colspan="<%= colSpanLength %>">&nbsp;
									<script type="text/javascript">
										function checkRemoveButton(idTabSelezionato){
											var elemToRemove = $("#hiddenIdRemove-" + idTabSelezionato).val();
											
												if(elemToRemove) {	
												  if(elemToRemove == '') {
													  $('#rem_btn_2').hide();
												  } else {
													  $('#rem_btn_2').show();
												  }
												} else {
													$('#rem_btn_2').hide();
												}
										  }
										
										function setUlWidth(idTabSelezionato){
											$('#prevTab').hide();
											$('#nextTab').hide();
											
											$('#tabsNav').width('');
											
											var larghezzaUlOriginale = $('#tabsNav').width();
											
											$('#tabsNav').width('300px');
											
											var larghezzaTabellaInterna = $('#tabellaInterna').width();
											
											if(larghezzaUlOriginale > larghezzaTabellaInterna ) {
												var larghezzaPrevTab = 0;
												if(idTabSelezionato == 0){
													$('#prevTab').hide();
												} else {
													$('#prevTab').show();
													larghezzaPrevTab = $('#prevTab').width();
												}
												
												var larghezzaNextTab = 0;
												if(idTabSelezionato == $('#tabs').tabs('length') -1){
													$('#nextTab').hide();
												} else {
													$('#nextTab').show();
													larghezzaNextTab = $('#nextTab').width();
												}
												
												var nuovaDimensione = larghezzaTabellaInterna - (larghezzaPrevTab + larghezzaNextTab);
												var d = nuovaDimensione +'px';
												$('#tabsNav').width(d);
											} else {
												var d = larghezzaUlOriginale +'px';
												$('#tabsNav').width(d);
											}
										}
										
										function spostaTabsVersoSx(idTabSelezionato){
											// controllo che il tab selezionato sia all'interno del viewport
											var p1 = getPositions('#li-tabs-' + idTabSelezionato);
											var p2 = getPositions('#tabsNav');
											
// 											console.log('Active: ' + p1[0]);
// 											console.log('NavBar: ' + p2[0]);
// 											console.log('Active inside NavBar : ' + insideX(p1[0],p2[0]));
											
											if(insideX(p1[0],p2[0]) == false){
												var shiftSX = shiftElementoSX(p1[0],p2[0]);
// 												console.log('ShiftSX: ' + shiftSX);
												
												if(shiftSX > 0){ // il tab attivo e' oltre il viewport, faccio lo scroll della nav bar
													$( "#tabsNav" ).scrollLeft(shiftSX);
												}
											}
										}
										
										function spostaTabsVersoDx(idTabSelezionato){
											// controllo che il tab selezionato sia all'interno del viewport
											var p1 = getPositions('#li-tabs-' + idTabSelezionato);
											var p2 = getPositions('#tabsNav');
											
// 											console.log('Active: ' + p1[0]);
// 											console.log('NavBar: ' + p2[0]);
// 											console.log('Active inside NavBar : ' + insideX(p1[0],p2[0]));
											
											if(insideX(p1[0],p2[0]) == false){
												var shiftDX = shiftElementoDX(p1[0],p2[0]);
// 												console.log('ShiftDX: ' + shiftDX);
												var shiftSX = shiftElementoSX(p1[0],p2[0]);
// 												console.log('ShiftSX: ' + shiftSX);
												
												if(shiftDX > 0){ // il tab attivo e' oltre il viewport, faccio lo scroll della nav bar
													$( "#tabsNav" ).scrollLeft(-shiftDX);
												}
												if(shiftSX > 0){ // il tab attivo e' oltre il viewport, faccio lo scroll della nav bar
													$( "#tabsNav" ).scrollLeft(shiftSX);
												}
											}
										}
										
										function getPositions( elem ) {
									        var pos, width, height;
									        pos = $( elem ).offset();
									        width = $( elem ).width();
									        height = $( elem ).height();
									        return [ [ pos.left, pos.left + width ], [ pos.top, pos.top + height ] ];
									    }
										
										function insideX( p1, p2 ) { // p1 dentro p2
									        var r1, r2;
									        r1 = p1[0] >= p2[0];
									        r2 = p1[1] <= p2[1];
									        return r1 && r2;
									    }
										
										function shiftElementoSX( p1, p2 ) { // quanto spostare p1 per arrivare p2
											if(p1[0] >= p2[1]) {  // elemento totalmente fuori a dx
												return p1[1] - p2[1];
											}
										
											if(p1[0] >= p2[0] && p1[1] >= p2[1]) { // l'elemento e' parzialmente fuori dal viewport a dx
												return p1[1] - p2[1];
											}
											
									       	return 0; 
									    }
									    
									    function shiftElementoDX( p1, p2 ) { // quanto spostare p1 per arrivare p2
									    	if(p1[1] <= p2[0]) {  // elemento totalmente fuori a sx
												return p2[0] - p1[0];
											}
										
											if(p1[0] < p2[0] && p1[1] >= p2[0]) { // l'elemento e' parzialmente fuori dal viewport a sx
												return p2[0] - p1[0];
											}
											
									       	return 0; 
									    }
									    										
										var tabSelezionato = <%=tabSelezionato %>;
									
										$(document).ready(function(){
											$( "#tabs" ).tabs(
												{
													active: tabSelezionato,
													selected: tabSelezionato,
												  	select: function( event, ui ) {
												  		checkRemoveButton(ui.index);
												  		setUlWidth(ui.index);
												  		
// 												  		console.log('Tab Selezionato: ' + ui.index);
// 												  		console.log('OldTab Selezionato: ' + tabSelezionato);
												  		
												  		var movimento = tabSelezionato - ui.index;
												  		
// 												  		console.log('Movimento: ' + (movimento > 0 ? 'SX' : movimento < 0 ? 'DX' : 'Nessuno'));
												  		
												  		if(movimento > 0) { // >>SX
												  			spostaTabsVersoDx(ui.index);
												  		} else if(movimento < 0) { // <<DX
												  			spostaTabsVersoSx(ui.index);
												  		} else {
												  			// stesso tab
												  		} 
												  		tabSelezionato = ui.index;
													  }
													}		
											);
											$( ".ui-state-default.ui-corner-top.ui-tabs-selected.ui-state-active" ).removeClass('ui-state-default');
											
											
											$("#rem_btn_2").click(function(){
												var idTabSelezionato = $("#tabs").tabs('option', 'selected');
												var elemToRemove = $("#hiddenIdRemove-" + idTabSelezionato).val();
												  
												  if(elemToRemove) {	
													  if(elemToRemove == '') {
													  } else {
														  var nomeGruppo = $("#de_name_" + idTabSelezionato + "_0").val();
														$( "#confermaEliminazioneSpan" ).html(nomeGruppo);
											    		$( "#confermaEliminazioneModal" ).dialog( "open" );
													  }
													}
											});
											
											$(function() {

												var $tabs = $('#tabs').tabs();
												
												var htmlDiv = '<div id="tabsNavDiv" class="tabsNavDiv"></div>';
												
												var htmlNext = "<a id='nextTab' href='#' class='next-tab mover' rel='next'><i class=\"material-icons md-40\" style=\"line-height: 0.6;\">chevron_right</i></a>";
												var htmlPrev = "<a id='prevTab' href='#' class='prev-tab mover' rel='prev'><i class=\"material-icons md-40\" style=\"line-height: 0.6;\">chevron_left</i></a>";
												
												// 1. attacco il div contentitore
												$tabs.prepend(htmlDiv);
												
												// 2. sposto ul
												$("#tabsNav").detach().appendTo("#tabsNavDiv");
												
												// 3. aggiungo la freccia di sx
												$('#tabsNavDiv').prepend(htmlPrev);
												
												// 4. aggiungo la freccia di dx
												$('#tabsNavDiv').append(htmlNext);
										
												$('.next-tab, .prev-tab').click(function() { 
														var idTabSelezionato = $("#tabs").tabs('option', 'selected');
														var destinazione = $(this).attr("rel");
														
														if(destinazione == 'prev') {
															$tabs.tabs('select', idTabSelezionato - 1);
														} else { 
															$tabs.tabs('select', idTabSelezionato + 1);
														}
											           
											           return false;
											       });

											});
											
											checkRemoveButton(tabSelezionato);
											setUlWidth(tabSelezionato);
										});
									</script>
								</td>
					  		</tr>
						<%
					}
					
					%>
				</table>
			</div>
			
			
			<div id="confermaEliminazioneModal" title="Conferma Operazione">
				<div id="confermaModalBody" class="contenutoModal">
					<p class="contenutoModal">
			 			<span>Si sta rimuovendo il gruppo:</span>
			 		</p>
			 		<ul class="contenutoModal">
			 			<li>
			 				<span id="confermaEliminazioneSpan">gruppo</span>
			 			</li>
		 			</ul>
			 		<p class="contenutoModal">
			 			<span>Procedere?</span>
			 		</p>
				</div>
			</div>
		</td>
	</tr>
</tbody>
