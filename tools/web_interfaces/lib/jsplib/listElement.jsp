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
<!DOCTYPE html>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page session="true" import="java.util.*, org.openspcoop2.web.lib.mvc.*" %>
<html lang="it">
<%
String iddati = request.getParameter(Costanti.PARAMETER_NAME_ID_DATI);
String params = (String) request.getAttribute(Costanti.PARAMETER_NAME_PARAMS);

if (params == null)
	 params="";

ListElement listElement = ServletUtils.getObjectFromSession(request, session, ListElement.class, Costanti.SESSION_ATTRIBUTE_LIST_ELEMENT);

String nomeServlet = listElement.getOggetto();
String nomeServletAdd = nomeServlet+"Add.do";
String nomeServletDel = nomeServlet+"Del.do";
String nomeServletList = nomeServlet+"List.do";
	  
String formatPar = listElement.formatParametersURL();

String gdString = Costanti.SESSION_ATTRIBUTE_GENERAL_DATA;
String pdString = Costanti.SESSION_ATTRIBUTE_PAGE_DATA;
if (iddati != null && !iddati.equals("notdefined")) {
  gdString += iddati;
  pdString += iddati;
}
else {
  iddati = "notdefined";
}

GeneralData gd = ServletUtils.getObjectFromSession(request, session, GeneralData.class, gdString);
PageData pd = ServletUtils.getObjectFromSession(request, session, PageData.class, pdString);
String randomNonce = (String) request.getAttribute(Costanti.REQUEST_ATTRIBUTE_CSP_RANDOM_NONCE);

List<?> v = pd.getDati();
int n = v.size();
String search = request.getParameter(Costanti.SEARCH_PARAMETER_NAME);
if (search == null)
  search = "";

String customListViewName = pd.getCustomListViewName();
String tabSessionKey = ServletUtils.getTabIdFromRequestAttribute(request);
%>

<head>
<meta charset="UTF-8">
<jsp:include page="/jsplib/browserUtils.jsp" flush="true" />
<title><%= gd.getTitle() %></title>
<link rel="stylesheet" href="css/roboto/roboto-fontface.css" type="text/css">
<link rel="stylesheet" href="css/materialIcons/material-icons-fontface.css" type="text/css">
<link rel="stylesheet" href="css/<%= gd.getCss() %>" type="text/css">
<link rel="stylesheet" href="css/materialIcons.css" type="text/css">
<link rel="stylesheet" href="css/ui.core.css" type="text/css">
<link rel="stylesheet" href="css/ui.theme.css" type="text/css">
<link rel="stylesheet" href="css/ui.dialog.css" type="text/css">
<link rel="stylesheet" href="css/ui.resizable.css" type="text/css">
<link rel="stylesheet" href="css/bootstrap-tagsinput.css" type="text/css">
<script type="text/javascript" src="js/webapps.js" nonce="<%= randomNonce %>"></script>


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
var eseguiOperazioniConGET = false;
var nomeServletExport = 'export.do';

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

function RemoveEntries(tipo) {
  if (nr != 0) {
    return false;
  }
  var elemToRemove = '';
  if (n > 1) {
    for (var j = 0; j < n; j++) {
      if (document.form.selectcheckbox[j].checked) {
        if (elemToRemove != '') {
          elemToRemove += ',';
        }
        //elemToRemove += j;
        elemToRemove +=document.form.selectcheckbox[j].value;
      }
    }
  } else {
    if (document.form.selectcheckbox.checked)
      elemToRemove += document.form.selectcheckbox.value;
  }
  if (elemToRemove != '') {
    nr = 1;
    
    if(eseguiOperazioniConGET) {
	    var paramsString = '';
	    if (formatPar != null && formatPar != "") {
	    	paramsString = '?'+formatPar+'&obj='+elemToRemove+'&iddati='+iddati+params;
	    }else {
	   	    paramsString = '?obj='+elemToRemove+'&iddati='+iddati+params;
	    }
	    if(tipo) {
		paramsString+='&obj_t='+tipo;
	    }
	    var destinazione='<%= request.getContextPath() %>/'+nomeServletDel+paramsString;
	    
	 	// addTabID
		destinazione = addTabIdParam(destinazione,true);
	 	
		destinazione = addParamToURL(destinazione, '<%=Costanti.PARAMETRO_AZIONE %>' , 'removeEntries');
		
		//aggiungo parametro csfr
		if(csrfToken != ''){
		  destinazione = addParamToURL(destinazione, csrfTokenKey , csrfToken);
		}
	 	
		document.location = destinazione;
    } else {
    	
    	var deleteForm = document.createElement('FORM');
    	deleteForm.name='deleteForm';
    	deleteForm.method='POST';
    	
    	addHidden(deleteForm, 'obj' , elemToRemove);
    	addHidden(deleteForm, 'iddati' , iddati);
		if(tipo) {
			addHidden(deleteForm, 'obj_t' , tipo);
		}
    	
    	// formatParams
    	  
   	   if (formatPar != null && formatPar != ""){
   	  	var pairs = ((formatPar[0] === '?' || formatPar[0] === '&') ? formatPar.substr(1) : formatPar).split('&');
   	  	for (var i = 0; i < pairs.length; i++) {
   	      	var pair = pairs[i].split('=');
   	      	addHidden(deleteForm, pair[0] , pair[1]);
   	  	}
   	   }
   	   if (params != null && params != ""){
   		   var pairs = ((params[0] === '?' || params[0] === '&') ? params.substr(1) : params).split('&');
   		   for (var i = 0; i < pairs.length; i++) {
   		       var pair = pairs[i].split('=');
   		       addHidden(deleteForm, pair[0] , pair[1]);
   		   }
   	   }
   	   
   		// imposto la destinazione
   	  deleteForm.action = nomeServletDel;
   	      
   	  document.body.appendChild(deleteForm);
   	  
   	  
   	  // aggiungo parametro idTab
   	  if(tabValue != ''){
   	  	addHidden(deleteForm, tabSessionKey , tabValue);
   	 	addHidden(deleteForm, prevTabSessionKey , tabValue);
   	  }
   	  
   	  addHidden(deleteForm, '<%=Costanti.PARAMETRO_AZIONE %>' , 'removeEntries');
   	  
   	  //aggiungo parametro csfr
	  if(csrfToken != ''){
	  	addHidden(deleteForm, csrfTokenKey , csrfToken);
	  }
   	  
   	  // form submit
   	  deleteForm.submit();
    }
    
  }else {
	if(tipo){
		$( "#selezioneRichiestaModal" ).dialog( "open" );
	}
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
		if(eseguiOperazioniConGET) {
			var destinazione = "<%= request.getContextPath() %>/export.do?tipoExport="+tipo+"&obj="+elemToExport;
			
			//addTabID
			destinazione = addTabIdParam(destinazione,true);
			document.location = destinazione;
		} else {
			
			var exportForm = document.createElement('FORM');
			exportForm.name='exportForm';
			exportForm.method='POST';
	    	
	    	addHidden(exportForm, 'obj' , elemToExport);
	    	addHidden(exportForm, 'tipoExport' , tipo);
	    	
	   		// imposto la destinazione
	   	 	 exportForm.action = nomeServletExport;
	   	      
	   	 	 document.body.appendChild(exportForm);
	   	 	 
		   	  // aggiungo parametro idTab
		   	  if(tabValue != ''){
		   	  	addHidden(exportForm, tabSessionKey , tabValue);
		   		addHidden(exportForm, prevTabSessionKey , tabValue);
		   	  }
	   	 	 // form submit
	   	 	 exportForm.submit();
		}
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

<!-- JQuery lib-->
<script type="text/javascript" src="js/jquery-latest.js" nonce="<%= randomNonce %>"></script>
<!--Funzioni di utilita -->
<script type="text/javascript" src="js/ui.core.js" nonce="<%= randomNonce %>"></script>
<script type="text/javascript" src="js/ui.dialog.js" nonce="<%= randomNonce %>"></script>
<script type="text/javascript" src="js/ui.resizable.js" nonce="<%= randomNonce %>"></script>
<script type="text/javascript" src="js/ui.draggable.js" nonce="<%= randomNonce %>"></script>
<script type="text/javascript" nonce="<%= randomNonce %>">
function togglePanelListaRicerca(panelListaRicercaOpen){
	if(panelListaRicercaOpen) {
    	$("#searchForm").removeClass('searchFormOff');
    	$("#searchForm").addClass('searchFormOn');
    	
    	if($( "#iconaPanelListaSpan" ).length > 0){
    		$('#iconaPanelListaSpan').attr('title', '<%=Costanti.TOOLTIP_NASCONDI_FILTRI_RICERCA %>');
    	}
    	
    	// reinit select del filtro
    	inizializzaSelectFiltro();
    	
//     	$("#iconaPanelLista").removeClass('icon-down-white');
//     	$("#iconaPanelLista").addClass('icon-up-white');
    } else {
    	$("#searchForm").removeClass('searchFormOn');
    	$("#searchForm").addClass('searchFormOff');
    	
    	if($( "#iconaPanelListaSpan" ).length > 0){
    		$('#iconaPanelListaSpan').attr('title', '<%=Costanti.TOOLTIP_VISUALIZZA_FILTRI_RICERCA %>');
    	}
//     	$("#iconaPanelLista").removeClass('icon-up-white');
//     	$("#iconaPanelLista").addClass('icon-down-white');
    }
}

function inizializzaSelectFiltro(){
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
}

function mostraDataElementInfoModal(title,body){
	$("#dataElementInfoModal").prev().children('span').text(title);
	$("#dataElementInfoModalBody").html(body);
	$("#dataElementInfoModal").dialog("open");
}

	$(document).ready(function(){
		// pannello ricerca che si apre e chiude iconaPanelLista
		$("#panelListaRicercaHeader").click(function(){
			panelListaRicercaOpen = !panelListaRicercaOpen;
			togglePanelListaRicerca(panelListaRicercaOpen);
		});
		
		togglePanelListaRicerca(panelListaRicercaOpen);
		
		$("tr[class='even']").hover(function() {
		    $(this).addClass('active');
		}, function() {
		    $(this).removeClass('active');
		});
		
		$("tr[class='odd']").hover(function() {
		    $(this).addClass('active');
		}, function() {
		    $(this).removeClass('active');
		});
	 });
</script>
<script type="text/javascript" src="js/utils.js" nonce="<%= randomNonce %>"></script>
<script type="text/javascript" src="js/jquery-on.js" nonce="<%= randomNonce %>"></script>
<script type="text/javascript" src="js/jquery.searchabledropdown-1.0.8.min.js" nonce="<%= randomNonce %>"></script>
<script type="text/javascript" src="js/jquery.context-menu.min.js" nonce="<%= randomNonce %>"></script>
<jsp:include page="/jsplib/menuUtente.jsp" flush="true" />
<link rel="shortcut icon" href="images/favicon.ico" type="image/x-icon" />
<script type="text/javascript" nonce="<%= randomNonce %>">
$(document).ready(function(){
	focusText(document.form);
});
</script>
</head>
<body marginwidth=0 marginheight=0>
	<table class="bodyWrapper">
		<tbody>
			<jsp:include page="/jsplib/templateHeader.jsp" flush="true" />
			<tr class="trPageBody">
				<jsp:include page="/jsplib/menu.jsp" flush="true" />
				<% if(customListViewName == null || "".equals(customListViewName)){ %>
					<jsp:include page="/jsplib/full-list.jsp" flush="true" />
				<% } else {%>	
					<jsp:include page="/jsplib/full-list-noTable.jsp" flush="true" />
				<% } %>
			</tr>
			<jsp:include page="/jsplib/templateFooter.jsp" flush="true" />
		</tbody>
	</table>
	<div id="confermaEliminazioneModal" title="Conferma Operazione">
 		<p class="contenutoEliminazioneModal">
 			<img src="images/tema_link/alert_orange.png" alt="Attenzione"/>
 			<span>Eliminare gli elementi selezionati?</span>
 		</p>
	</div>
	<div id="selezioneRichiestaModal" title="Selezione richiesta">
 		<p class="contenutoSelezioneRichiestaModal">
 			<img src="images/tema_link/alert_orange.png" alt="Attenzione"/>
 			<span>&Egrave; necessario selezionare almeno 1 elemento.</span>
 		</p>
	</div>
	<div id="dataElementInfoModal" title="Info">
		<div id="dataElementInfoModalBody" class="contenutoModal"></div>
	</div>
<jsp:include page="/jsplib/conferma.jsp" flush="true" />
</body>
</html>
