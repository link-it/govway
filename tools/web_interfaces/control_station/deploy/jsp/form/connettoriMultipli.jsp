<%--
 * GovWay - A customizable API Gateway
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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
	
	String params = (String) request.getAttribute(Costanti.PARAMETER_NAME_PARAMS);

	if (params == null)
		 params="";

	ListElement listElement = ServletUtils.getObjectFromSession(request, session, ListElement.class, Costanti.SESSION_ATTRIBUTE_LIST_ELEMENT);

	String nomeServlet = listElement.getOggetto();
	String nomeServletAdd = nomeServlet+"Add.do";
	String nomeServletDel = nomeServlet+"Del.do";
	String nomeServletList = nomeServlet+"List.do";
	
	String formatPar = listElement.formatParametersURL();
	
	Vector<?> datiConGruppi = pd.getDati();
	int n = datiConGruppi.size();
	
	String classSpanNoEdit="spanNoEdit";
	String classDivNoEdit="divNoEdit";
	
	int colSpanLength = 1;
	String tabSessionKey = ServletUtils.getTabIdFromRequestAttribute(request);
%>
<SCRIPT type="text/javascript" nonce="<%= randomNonce %>">
var nomeServletAdd_Custom = '<%= nomeServletAdd %>';
var nomeServletDel_Custom = '<%= nomeServletDel %>';
var nomeServletList_Custom = '<%= nomeServletList %>';
</SCRIPT>
<jsp:include page="/jsp/listElementCustom.jsp" flush="true" />
<SCRIPT type="text/javascript" nonce="<%= randomNonce %>">
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
  if(idTabSelezionato) {
		elemToRemove = $("#hiddenIdRemove-" + idTabSelezionato).val();
	} else {
		elemToRemove = $("#hiddenIdRemove-" + tabSelezionato).val();
	}
  
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
    var destinazione;
    if (formatPar != null && formatPar != "")
        destinazione='<%= request.getContextPath() %>/'+nomeServletDel+'?'+formatPar+'&obj='+elemToRemove+'&iddati='+iddati+params+parametroIdConnTab;
      else
        destinazione='<%= request.getContextPath() %>/'+nomeServletDel+'?obj='+elemToRemove+'&iddati='+iddati+params+parametroIdConnTab;

	//addTabID
	destinazione = addTabIdParam(destinazione,true);
	
	destinazione = addParamToURL(destinazione, '<%=Costanti.PARAMETRO_AZIONE %>' , 'removeEntries');
	
	//aggiungo parametro csfr
	if(csrfToken != ''){
	  destinazione = addParamToURL(destinazione, csrfTokenKey , csrfToken);
	}
	
	document.location = destinazione;
  }
};

function AddEntry() {
  if (nr != 0) {
    return false;
  }
  nr = 1;
  var destinazione;
  if (formatPar != null && formatPar != "")
	  destinazione='<%= request.getContextPath() %>/'+nomeServletAdd+'?'+formatPar+'&iddati='+iddati+params;
  else
	  destinazione='<%= request.getContextPath() %>/'+nomeServletAdd+'?iddati='+iddati+params;
	  
	//addTabID
	destinazione = addTabIdParam(destinazione,true);
	document.location = destinazione;
};

function CambiaVisualizzazione(newPageSize) {
  if (nr != 0) {
    return false;
  }
  nr = 1;
  if (newPageSize == '0') {
    index = 0; 
  }
  var destinazione;
  if (formatPar != null && formatPar != "")
	  destinazione='<%= request.getContextPath() %>/'+nomeServletList+'?'+formatPar+'&pageSize='+newPageSize+'&index='+index+'&iddati='+iddati+params+'&_searchDone=true';
  else
	  destinazione='<%= request.getContextPath() %>/'+nomeServletList+'?pageSize='+newPageSize+'&index='+index+'&iddati='+iddati+params+'&_searchDone=true';
    
//addTabID
	destinazione = addTabIdParam(destinazione,true);
	document.location = destinazione;
};

function NextPage() {
  if (nr != 0) {
    return false;
  }
  nr = 1;
  index += pageSize;
  var destinazione;
  if (formatPar != null && formatPar != "")
	  destinazione='<%= request.getContextPath() %>/'+nomeServletList+'?'+formatPar+'&pageSize='+pageSize+'&index='+index+'&iddati='+iddati+params+'&_searchDone=true';
  else
	  destinazione='<%= request.getContextPath() %>/'+nomeServletList+'?pageSize='+pageSize+'&index='+index+'&iddati='+iddati+params+'&_searchDone=true';
    
//addTabID
	destinazione = addTabIdParam(destinazione,true);
	document.location = destinazione;
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
  var destinazione;
  if (formatPar != null && formatPar != "")
	  destinazione='<%= request.getContextPath() %>/'+nomeServletList+'?'+formatPar+'&pageSize='+pageSize+'&index='+index+'&iddati='+iddati+params+'&_searchDone=true';
  else
	  destinazione='<%= request.getContextPath() %>/'+nomeServletList+'?pageSize='+pageSize+'&index='+index+'&iddati='+iddati+params+'&_searchDone=true';
    
//addTabID
	destinazione = addTabIdParam(destinazione,true);
	document.location = destinazione;
};

function Search(form) {
  if (nr != 0) {
    return false;
  }
  nr = 1;

  addHidden(form, 'index' , 0);
  addHidden(form, 'iddati' , iddati);
  addHidden(form, 'pageSize' , pageSize);
  addHidden(form, '_searchDone' , true);

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
  
  //evito di mandare indietro al server il valore degli elementi hidden che si utilizzano per la creazione delle finestre DialogInfo.
  for (var k=0; k<document.form.elements.length; k++) {
		var nome = document.form.elements[k].name;
		var hiddenInfo = nome!=null ? nome.indexOf("__i_hidden") : -1;

		if(hiddenInfo > -1) {
			document.form.elements[k].value = '';
		}
  }
      
  // aggiungo parametro idTab
  if(tabValue != ''){
  	addHidden(document.form, tabSessionKey , tabValue);
  	addHidden(document.form, prevTabSessionKey , tabValue);
  }
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
		} else {
			var tipo = document.form.elements[k].type;
			if (tipo == "select-one" || tipo == "select-multiple") {
				document.form.elements[k].selectedIndex = 0;
			} else if (tipo == "text" || tipo == "textarea"|| tipo == "number") {
				document.form.elements[k].value="";
			} else if (tipo == "checkbox") {
				document.form.elements[k].checked=false;
			}
		}
	  }

	  addHidden(form, 'index' , 0);
	  addHidden(form, 'iddati' , iddati);
	  addHidden(form, 'pageSize' , pageSize);
	  addHidden(form, '_searchDone' , true);

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
	   
	   // evito di mandare indietro al server il valore degli elementi hidden che si utilizzano per la creazione delle finestre DialogInfo.
	   for (var k=0; k<document.form.elements.length; k++) {
	 		var nome = document.form.elements[k].name;
	 		var hiddenInfo = nome!=null ? nome.indexOf("__i_hidden") : -1;

	 		if(hiddenInfo > -1) {
	 			document.form.elements[k].value = '';
	 		}
	   }
	   
	   // aggiungo parametro idTab
	   if(tabValue != ''){
	   	addHidden(document.form, tabSessionKey , tabValue);
	   	addHidden(document.form, prevTabSessionKey , tabValue);
	   }
	  // form submit
	  document.form.submit();
	 
	};

function Export(url){
	var destinazione='<%= request.getContextPath() %>/'+url;
	//addTabID
	destinazione = addTabIdParam(destinazione,true);
	document.location = destinazione;
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
		var destinazione = "<%= request.getContextPath() %>/export.do?tipoExport="+tipo+"&obj="+elemToExport; 
			
			//addTabID
			destinazione = addTabIdParam(destinazione,true);
			document.location = destinazione;
	} else {
		$( "#selezioneRichiestaModal" ).dialog( "open" );
	}
		  
};

function Change(form,dataElementName) {
	Change(form,dataElementName,false);
}
function Change(form,dataElementName,fromFilters) {
    
	if( fromFilters ){
		var formAction = form.action;
		
		// hack actionvuota
		if(formAction == ''){
			formAction = document.location.href;
		}
		
		if(isModificaUrlRicerca(formAction,'Add.do')){
			form.action=formAction.replace('Add.do','List.do');
		}
		if(isModificaUrlRicerca(formAction,'Change.do')){
			form.action=formAction.replace('Change.do','List.do');
		}
		if(isModificaUrlRicerca(formAction,'Del.do')){
			form.action=formAction.replace('Del.do','List.do');
		}
	}
	
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
     
     // evito di mandare indietro al server il valore degli elementi hidden che si utilizzano per la creazione delle finestre DialogInfo.
     for (var k=0; k<document.form.elements.length; k++) {
   		var nome = document.form.elements[k].name;
   		var hiddenInfo = nome!=null ? nome.indexOf("__i_hidden") : -1;

   		if(hiddenInfo > -1) {
   			document.form.elements[k].value = '';
   		}
     }
        
  // aggiungo parametro idTab
  	  if(tabValue != ''){
  	  	addHidden(document.form, tabSessionKey , tabValue);
  	    addHidden(document.form, prevTabSessionKey , tabValue);
  	  }
    // form submit
    document.form.submit();
}

function isModificaUrlRicerca(formAction, urlToCheck){
	// hack hash documento impostato, la parte di url che contiene la # bisogna eliminarla dal check
	if(formAction.indexOf('#') > 0) {
		formAction = formAction.substring(0, formAction.indexOf('#'));
	}
	if(formAction.indexOf('?') > 0) {
		formAction = formAction.substring(0, formAction.indexOf('?'));
	}
	
	return ieEndsWith(formAction, urlToCheck);
}

function ieEndsWith(str, suffix){
	return str.indexOf(suffix, str.length - suffix.length) !== -1;
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
	if (!searchDescription.equals("") || (pd.getFilterNames() != null && pd.hasAlmostOneFilterDefined()) || (pd.isPostBackResult())){
	%>	panelListaRicercaOpen = true; <% 
	} 
}%>

</SCRIPT>
<!--Funzioni di utilita -->
<script type="text/javascript" nonce="<%= randomNonce %>">
function togglePanelListaRicerca(panelListaRicercaOpen){
	if(panelListaRicercaOpen) {
    	$("#searchForm").removeClass('searchFormOff');
    	$("#searchForm").addClass('searchFormOn');
    	
    	if($( "#iconaPanelListaSpan" ).length > 0){
    		$('#iconaPanelListaSpan').prop('title', '<%=Costanti.TOOLTIP_NASCONDI_FILTRI_RICERCA %>');
    	}
    	
    	// reinit select del filtro
    	inizializzaSelectFiltro();
    } else {
    	$("#searchForm").removeClass('searchFormOn');
    	$("#searchForm").addClass('searchFormOff');
    	
    	if($( "#iconaPanelListaSpan" ).length > 0){
    		$('#iconaPanelListaSpan').prop('title', '<%=Costanti.TOOLTIP_VISUALIZZA_FILTRI_RICERCA %>');
    	}
    }
}

function inizializzaSelectFiltro(){
	if($('select[id^=filterValue_]').length > 0){
		// elimino eventuali plugin gia' applicati
		$('select[id^=filterValue_]').each(function() {
			var wrapper = $( this ).parent();
			if(wrapper.prop('id').indexOf('_wrapper') > -1) {
				$( this ).appendTo($( this ).parent().parent());
				wrapper.remove();
				$( this ).css('width','');
				$( this ).css('height','');
			}
			
			var checkID = $( this ).prop('id') + '_hidden_chk';
			if($( '#' + checkID ).length > 0) {
				var val = $( '#' + checkID ).prop('value');
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
<script type="text/javascript" src="js/utils.js" nonce="<%= randomNonce %>"></script>
<script type="text/javascript" src="js/jquery.searchabledropdown-1.0.8.min.js" nonce="<%= randomNonce %>"></script>
<tbody>
	<!-- filtri di ricerca -->
	<jsp:include page="/jsplib/filtriRicerca.jsp" flush="true"/>

	<!-- spazio -->
	<tr> 
		<td valign=top>
			<div class="spacer">
				<%
					
					String tabSelezionatoS = ServletUtils.getObjectFromSession(request, session, String.class, CostantiControlStation.PARAMETRO_ID_CONN_TAB);
					int tabSelezionato = 0;
					
					if(StringUtils.isNotEmpty(tabSelezionatoS)) {
						try{
							tabSelezionato = Integer.parseInt(tabSelezionatoS);
						}catch(Exception e){ tabSelezionato = 0; }
					}
					
					Map<String,String> hidden = pd.getHidden();
					if (hidden!=null && !hidden.isEmpty()) {
					    for (String key : hidden.keySet()) {
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
						boolean mostraTab = v.size() > 1;
						
						if(v.size() > 0){
							int index = pd.getIndex();
						  	if (pd.getNumEntries() > 0){
						    	index++;
							}
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
																	
										            				if(de.getToolTip()!=null && !de.getToolTip().equals("")){
																		tooltipTextValNoEdit = " title=\"" + de.getToolTip() + "\"";	
																	}
										            				
																	if(textValNoEdit.length() > Costanti.LUNGHEZZA_RIGA_TESTO_TABELLA) {
																		if(tooltipTextValNoEdit==null || "".equals(tooltipTextValNoEdit)){
																			tooltipTextValNoEdit = " title=\"" + textValNoEdit + "\"";
																		}
																		textValNoEdit = textValNoEdit.substring(0,(Costanti.LUNGHEZZA_RIGA_TESTO_TABELLA -3)) + "...";
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
																								<img src="images/tema_link/<%= imageCheckBox %>" alt="Stato"/>
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
																                					<a id="<%=id %>"  class="edit-link <%= classLink %>" <%= deTip %> <%=deTarget %> href="<%= image.getUrl() %>" type="button">
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
						} // if e' presente almento un risultato
						else { // visualizzo messaggio nessun risultato 
							%>
								<tr class="even">
									<td colspan="2">
										<div class="tableNoData">
											<div class="buttonrow">
												<span class="tableNoData">Trovati 0 Risultati</span>
											</div>
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
									//Bottone di Remove
									if (pd.getRemoveButton()) {
									  %><input id='rem_btn_2' type="button" value='Elimina' class="negative" /><%
									}
									
									//Bottone di Add
									if (pd.getAddButton()) {
									  %><input id="aggiungiBtn" type="button" value='Crea Nuovo' />
									  	<script type="text/javascript" nonce="<%= randomNonce %>">
									      	 $(document).ready(function(){
									      		$('#aggiungiBtn').click(function() {
														<%= Costanti.JS_FUNCTION_VISUALIZZA_AJAX_STATUS %>AddEntry();
													});
												});
										</script>
									  <%
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
							<tr>
						  		<td colspan="<%= colSpanLength %>">&nbsp;
									<script type="text/javascript" nonce="<%= randomNonce %>">
										function assegnaTabSelezionatoAlFiltroRicerca(idTabSelezionato){
											var nomeGruppo = $("#de_name_" + idTabSelezionato + "_0").val();
											  
											  if(nomeGruppo) {	
												  if(nomeGruppo == '') {
												  } else {
													  // assegno il valore al filtro hidden
													  var hiddenFiltroIdTab = $("#filterValue_0__id");
													  if(hiddenFiltroIdTab && hiddenFiltroIdTab.prop('type') == 'hidden') {
														  hiddenFiltroIdTab.val(nomeGruppo);
													  }
												  }
												}
										}
									
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
											
											var larghezzaUlOriginale = $('#tabsNav').width() + 1;
											
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
												if(idTabSelezionato == $('#tabs').tabs('instance').tabs.length -1){
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
										
										function initTabButtons (tabSelezionato) {
											var $tabs = $('#tabs').tabs();
											
											var htmlDiv = '<div id="tabsNavDiv" class="tabsNavDiv"></div>';
											
											var htmlNext = "<a id='nextTab' href='#' class='next-tab mover' rel='next'><i class=\"material-icons md-40 line-height-06\">chevron_right</i></a>";
											var htmlPrev = "<a id='prevTab' href='#' class='prev-tab mover' rel='prev'><i class=\"material-icons md-40 line-height-06\">chevron_left</i></a>";
											
											// 1. attacco il div contentitore
											$tabs.prepend(htmlDiv);
											
											// 2. sposto ul
											$("#tabsNav").detach().appendTo("#tabsNavDiv");
											
											// 3. aggiungo la freccia di sx
											$('#tabsNavDiv').prepend(htmlPrev);
											
											// 4. aggiungo la freccia di dx
											$('#tabsNavDiv').append(htmlNext);
									
											$('.next-tab, .prev-tab').click(function() { 
													var idTabSelezionato = $("#tabs").tabs('option', 'active');
													var destinazione = $(this).prop("rel");
													
													if(destinazione == 'prev') {
														$tabs.tabs('option', 'active', idTabSelezionato - 1);
													} else { 
														$tabs.tabs('option', 'active', idTabSelezionato + 1);
													}
										           
										           return false;
										       });

											$("li[id^='li-tabs']").click(function() { 
												$(this).children('a').click();
											});
										}
									
										$(document).ready(function(){
											
											<%
											if(mostraTab) {
											%>
												$( "#tabs" ).tabs(
													{
														active: tabSelezionato,
														activate: function( event, ui ) {
															var newIndex = ui.newTab.index();
															
															checkRemoveButton(newIndex);
													  		setUlWidth(newIndex);
													  		assegnaTabSelezionatoAlFiltroRicerca(newIndex);
													  		
	// 												  		console.log('Tab Selezionato: ' + ui.index);
	// 												  		console.log('OldTab Selezionato: ' + tabSelezionato);
													  		
													  		var movimento = tabSelezionato - newIndex;
													  		
	// 												  		console.log('Movimento: ' + (movimento > 0 ? 'SX' : movimento < 0 ? 'DX' : 'Nessuno'));
													  		
													  		if(movimento > 0) { // >>SX
													  			spostaTabsVersoDx(newIndex);
													  		} else if(movimento < 0) { // <<DX
													  			spostaTabsVersoSx(newIndex);
													  		} else {
													  			// stesso tab
													  		} 
													  		tabSelezionato = newIndex;
														  }
														}		
												);
// 												$( ".ui-state-default.ui-corner-top.ui-tabs-selected.ui-state-active" ).removeClass('ui-state-default');
											<%
											}
											%>
											
											$("#rem_btn_2").click(function(){
												var idTabSelezionato = $("#tabs").tabs('option', 'active');
												
												var elemToRemove;
												if(idTabSelezionato) {
													elemToRemove = $("#hiddenIdRemove-" + idTabSelezionato).val();
												} else {
													elemToRemove = $("#hiddenIdRemove-" + tabSelezionato).val();
												}
												  
												  if(elemToRemove) {	
													  if(elemToRemove == '') {
													  } else {
														  var nomeGruppo;
														  
														  if(idTabSelezionato) {
														  	nomeGruppo = $("#de_name_" + idTabSelezionato + "_0").val();
														  } else {
															nomeGruppo = $("#de_name_" + tabSelezionato + "_0").val();
														  }
														  
														$( "#confermaEliminazioneSpan" ).html(nomeGruppo);
											    		$( "#confermaEliminazioneModal" ).dialog( "open" );
													  }
													}
											});
											
											checkRemoveButton(tabSelezionato);
											
											<%
											if(mostraTab) {
											%>
												initTabButtons(tabSelezionato);
												setUlWidth(tabSelezionato);
												assegnaTabSelezionatoAlFiltroRicerca(tabSelezionato);
											<%
											}
											%>
										});
									</script>
								</td>
					  		</tr>
				</table>
			</div>
			
			
			<div id="confermaEliminazioneModal" title="Conferma Operazione">
				<div id="confermaModalBody" class="contenutoModal">
					<p class="contenutoModal">
			 			<span>Si sta rimuovendo il connettore:</span>
			 		</p>
			 		<ul class="contenutoModal">
			 			<li>
			 				<span id="confermaEliminazioneSpan">connettore</span>
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
