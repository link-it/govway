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
<!-- JQuery lib-->
<script type="text/javascript" src="webjars/jquery/3.6.4/jquery.min.js" nonce="<%= randomNonce %>"></script>
<script type="text/javascript" src="webjars/jquery-ui/1.13.2/jquery-ui.min.js" nonce="<%= randomNonce %>"></script>
<script type="text/javascript" src="js/HtmlSanitizer.js" nonce="<%= randomNonce %>"></script>
<jsp:include page="/jsplib/browserUtils.jsp" flush="true" />
<script type="text/javascript" src="js/webapps.js" nonce="<%= randomNonce %>"></script>
<script type="text/javascript" nonce="<%= randomNonce %>">
var nomeServletAdd_Custom = '<%= nomeServletAdd %>';
var nomeServletDel_Custom = '<%= nomeServletDel %>';
var nomeServletList_Custom = '<%= nomeServletList %>';
</script>
<jsp:include page="/jsp/listElementCustom.jsp" flush="true" />
<script type="text/javascript" nonce="<%= randomNonce %>">
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
	 	
		destinazione = addParamToURL(destinazione, '<%=Costanti.PARAMETRO_AZIONE %>' , '<%=Costanti.VALUE_PARAMETRO_AZIONE_REMOVE_ENTRIES %>');
		
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
   	  
   	  addHidden(deleteForm, '<%=Costanti.PARAMETRO_AZIONE %>' , '<%=Costanti.VALUE_PARAMETRO_AZIONE_REMOVE_ENTRIES %>');
   	  
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


function Esporta(tipo) {
	EsportaImpl(tipo,eseguiOperazioniConGET);
}

</script>
<jsp:include page="/jsplib/listUtils.jsp" flush="true" />
<script type="text/javascript" nonce="<%= randomNonce %>">
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
<script type="text/javascript" src="js/jquery.searchabledropdown-1.0.8.min.js" nonce="<%= randomNonce %>"></script>
<script type="text/javascript" src="js/jquery.context-menu.min.js" nonce="<%= randomNonce %>"></script>
<jsp:include page="/jsplib/menuUtente.jsp" flush="true" />
<link rel="shortcut icon" href="images/favicon.ico" type="image/x-icon" />
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
