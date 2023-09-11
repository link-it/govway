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

<%@ page session="true" import="org.openspcoop2.web.lib.mvc.*" %>
<%
String iddati = request.getParameter(Costanti.PARAMETER_NAME_ID_DATI);

String pdString = Costanti.SESSION_ATTRIBUTE_PAGE_DATA;
if (iddati != null && !iddati.equals("notdefined")) {
//  gdString += iddati;
  pdString += iddati;
}
else {
  iddati = "notdefined";
}

PageData pd = ServletUtils.getObjectFromSession(request, session, PageData.class, pdString);
String randomNonce = (String) request.getAttribute(Costanti.REQUEST_ATTRIBUTE_CSP_RANDOM_NONCE);
%>
<script type="text/javascript" nonce="<%= randomNonce %>">

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

function Export(url){
	var destinazione='<%= request.getContextPath() %>/'+url;
	//addTabID
	destinazione = addTabIdParam(destinazione,true);
	document.location = destinazione;
};

function EsportaImpl(tipo,eseguiExportConGET) {

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
		if(eseguiExportConGET) {
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

</script>

<script type="text/javascript" nonce="<%= randomNonce %>">

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

function togglePanelListaRicerca(panelListaRicercaOpen){
	if(panelListaRicercaOpen) {
    	$("#searchForm").removeClass('searchFormOff');
    	$("#searchForm").addClass('searchFormOn');
    	
    	if($( "#iconaPanelListaSpan" ).length > 0){
    		$('#iconaPanelListaSpan').prop('title', '<%=Costanti.TOOLTIP_NASCONDI_FILTRI_RICERCA %>');
    	}
    	
    	// reinit del filtro
    	inizializzaSelectFiltro();
    } else {
    	$("#searchForm").removeClass('searchFormOn');
    	$("#searchForm").addClass('searchFormOff');
    	
    	if($( "#iconaPanelListaSpan" ).length > 0){
    		$('#iconaPanelListaSpan').prop('title', '<%=Costanti.TOOLTIP_VISUALIZZA_FILTRI_RICERCA %>');
    	}
    }
}

</script>


