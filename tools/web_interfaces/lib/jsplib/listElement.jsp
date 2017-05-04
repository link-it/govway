<%--
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2017 Link.it srl (http://link.it). 
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
<html>
<%
String iddati = request.getParameter("iddati");
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

String gdString = "GeneralData";
String pdString = "PageData";
if (iddati != null && !iddati.equals("notdefined")) {
  gdString += iddati;
  pdString += iddati;
}
else {
  iddati = "notdefined";
}

GeneralData gd = (GeneralData) session.getAttribute(gdString);
PageData pd = (PageData) session.getAttribute(pdString);

Vector v = pd.getDati();
int n = v.size();
String search = request.getParameter("search");
if (search == null)
  search = "";
%>

<head>
<meta charset="UTF-8">
<jsp:include page="/jsplib/browserUtils.jsp" flush="true" />
<title><%= gd.getTitle() %></title>
<link href="css/roboto/roboto-fontface.css" rel="stylesheet" type="text/css">
<link rel="stylesheet" href="css/<%= gd.getCss() %>" type="text/css">
<link rel="stylesheet" href="css/ui.core.css" type="text/css">
<link rel="stylesheet" href="css/ui.theme.css" type="text/css">
<link rel="stylesheet" href="css/ui.dialog.css" type="text/css">
<script type="text/javascript" src="js/webapps.js"></script>


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
	if(n > 1){
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
    if (formatPar != null && formatPar != "")
        document.location='<%= request.getContextPath() %>/'+nomeServletDel+'?'+formatPar+'&obj='+elemToRemove+'&iddati='+iddati+params;
      else
        document.location='<%= request.getContextPath() %>/'+nomeServletDel+'?obj='+elemToRemove+'&iddati='+iddati+params;
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

function Search(string) {
  if (nr != 0) {
    return false;
  }
  nr = 1;
  string = URLEncode(string);
  if (formatPar != null && formatPar != "")
    document.location='<%= request.getContextPath() %>/'+nomeServletList+'?'+formatPar+'&search='+string+'&index=0&iddati='+iddati+params;
  else
    document.location='<%= request.getContextPath() %>/'+nomeServletList+'?search='+string+'&index=0&iddati='+iddati+params;
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


	if(elemToExport !== '')
		document.location = "<%= request.getContextPath() %>/export.do?tipoExport="+tipo+"&obj="+elemToExport;  
		  
};

var panelListaRicercaOpen = false; // controlla l'aperture del pannello di ricerca.
<%
if ((pd.getSearch().equals("on") || (pd.getSearch().equals("auto") && pd.getNumEntries() > 10)) || pd.getFilter() != null){
	String searchDescription = pd.getSearchDescription();
	if (!searchDescription.equals("")){
	%>	panelListaRicercaOpen = true; <% 
	} 
}%>

</SCRIPT>

<!-- JQuery lib-->
<script type="text/javascript" src="js/jquery-latest.js"></script>
<!--Funzioni di utilita -->
<script type="text/javascript" src="js/ui.core.js"></script>
<script type="text/javascript" src="js/ui.dialog.js"></script>
<!-- <script type="text/javascript" src="js/jquery.confirm-1.2.js"></script> -->
<script type="text/javascript">
function togglePanelListaRicerca(panelListaRicercaOpen){
	if(panelListaRicercaOpen) {
    	$("#searchForm").removeClass('searchFormOff');
    	$("#searchForm").addClass('searchFormOn');
    	
    	$("#iconaPanelLista").removeClass('icon-down-white');
    	$("#iconaPanelLista").addClass('icon-up-white');
    } else {
    	$("#searchForm").removeClass('searchFormOn');
    	$("#searchForm").addClass('searchFormOff');
    	
    	$("#iconaPanelLista").removeClass('icon-up-white');
    	$("#iconaPanelLista").addClass('icon-down-white');
    }
}

	$(document).ready(function(){
		// pannello ricerca che si apre e chiude iconaPanelLista
		$("#panelListaRicercaHeader").click(function(){
			panelListaRicercaOpen = !panelListaRicercaOpen;
			togglePanelListaRicerca(panelListaRicercaOpen);
		});
		
		togglePanelListaRicerca(panelListaRicercaOpen);
	 });
</script>
<script type="text/javascript" src="js/utils.js"></script>
<jsp:include page="/jsplib/menuUtente.jsp" flush="true" />
<link rel="shortcut icon" href="images/favicon.ico" type="image/x-icon" />
</head>
<body marginwidth=0 marginheight=0 onLoad="focusText(document.form);">
	<table class="bodyWrapper">
		<tbody>
			<jsp:include page="/jsplib/templateHeader.jsp" flush="true" />
			<tr class="trPageBody">
				<jsp:include page="/jsplib/menu.jsp" flush="true" />
				<jsp:include page="/jsplib/full-list.jsp" flush="true" />
			</tr>
			<jsp:include page="/jsplib/templateFooter.jsp" flush="true" />
		</tbody>
	</table>
	<div id="confermaModal" title="Conferma Operazione">
 		<p class="contenutoModal">
 			<img src="images/tema_link/alert_orange.png"/>
 			<span>Eliminare gli elementi selezionati?</span>
 		</p>
	</div>
</body>
</html>
