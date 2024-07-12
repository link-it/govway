<%--
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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
<%@ page session="true" import="org.openspcoop2.web.lib.mvc.*" %>

<html lang="it">
<%
String iddati = request.getParameter(Costanti.PARAMETER_NAME_ID_DATI);
String params = (String) request.getAttribute(Costanti.PARAMETER_NAME_PARAMS);
String gdString = Costanti.SESSION_ATTRIBUTE_GENERAL_DATA;
String pdString = Costanti.SESSION_ATTRIBUTE_PAGE_DATA;
if (iddati != null && !iddati.equals("notdefined")) {
  gdString += iddati;
  pdString += iddati;
}
else {
  iddati = "notdefined";
}

if(params == null) params="";

GeneralData gd = ServletUtils.getObjectFromSession(request, session, GeneralData.class, gdString);
PageData pd = ServletUtils.getObjectFromSession(request, session, PageData.class, pdString);
String customListViewName = pd.getCustomListViewName();
boolean includiMenuLateraleSx = pd.isIncludiMenuLateraleSx();
String tabSessionKey = ServletUtils.getTabIdFromRequestAttribute(request);
String randomNonce = (String) request.getAttribute(Costanti.REQUEST_ATTRIBUTE_CSP_RANDOM_NONCE);
%>

<head>
<meta charset="UTF-8">
<title><%= gd.getTitle() %></title>
<link href="css/roboto/roboto-fontface.css" rel="stylesheet" type="text/css">
<link rel="stylesheet" href="css/materialIcons/material-icons-fontface.css" type="text/css">
<link rel="stylesheet" href="css/materialSymbols/material-symbols-fontface.css" type="text/css">
<link rel="stylesheet" href="css/<%= gd.getCss() %>" type="text/css">
<link rel="stylesheet" href="css/materialIcons.css" type="text/css">
<link rel="stylesheet" href="css/materialSymbols.css" type="text/css">
<link rel="stylesheet" href="css/ui.core.css" type="text/css">
<link rel="stylesheet" href="css/ui.theme.css" type="text/css">
<link rel="stylesheet" href="css/ui.dialog.css" type="text/css">
<link rel="stylesheet" href="css/ui.resizable.css" type="text/css">
<link rel="stylesheet" href="css/ui.slider.css" type="text/css">
<link rel="stylesheet" href="css/ui.datepicker.css" type="text/css">
<link rel="stylesheet" href="css/bootstrap-tagsinput.css" type="text/css">
<!-- JQuery lib-->
<script type="text/javascript" src="webjars/jquery/3.6.4/jquery.min.js" nonce="<%= randomNonce %>"></script>
<script type="text/javascript" src="webjars/jquery-ui/1.13.2/jquery-ui.min.js" nonce="<%= randomNonce %>"></script>
<script type="text/javascript" src="js/HtmlSanitizer.js" nonce="<%= randomNonce %>"></script>
<script type="text/javascript" src="js/ui.datepicker-it.js" nonce="<%= randomNonce %>"></script>
<jsp:include page="/jsplib/browserUtils.jsp" flush="true" />
<script type="text/javascript" src="js/webapps.js" nonce="<%= randomNonce %>"></script>
<!--Funzioni di utilita -->
<script type="text/javascript" nonce="<%= randomNonce %>">
var iddati = '<%= iddati %>';
var params = '<%= params %>';
var path = '<%= request.getContextPath()%>';
</script>
<script type="text/javascript" src="js/PostBack.js" nonce="<%= randomNonce %>"></script>
<script type="text/javascript" src="js/utils.js" nonce="<%= randomNonce %>"></script>
<script type="text/javascript" src="js/array-utils.js" nonce="<%= randomNonce %>"></script>
<% if(
// TODO verifica se serve questa gestione
		(customListViewName == null || "".equals(customListViewName))
	|| (!"configurazione".equals(customListViewName) && !"connettoriMultipli".equals(customListViewName))
	){ %>
<script type="text/javascript" src="js/ui.resizable.js" nonce="<%= randomNonce %>"></script>
<% }%>
<script type="text/javascript" src="js/typeahead.bundle.js" nonce="<%= randomNonce %>"></script>
<script type="text/javascript" src="js/bootstrap-tagsinput.js" nonce="<%= randomNonce %>"></script>
<script type="text/javascript" src="js/jquery.searchabledropdown-1.0.8.min.js" nonce="<%= randomNonce %>"></script>
<script type="text/javascript" src="js/jquery.context-menu.min.js" nonce="<%= randomNonce %>"></script>
<script type="text/javascript" nonce="<%= randomNonce %>">
var nr = 0;
function CheckDati() {
  if (nr != 0) {
    return false;
  }

  //I controlli si fanno direttamente nei .java
  nr = 1;

  var theForm = document.form;
  
  //evito di mandare indietro al server il valore degli elementi hidden che si utilizzano per la creazione delle finestre DialogInfo.
  eliminaElementiHidden(theForm);
  
  //elimino codice html dall'input testuale
  for (var k=0; k<theForm.elements.length; k++) {
		var tipo = theForm.elements[k].type;
		if (tipo == "text" || tipo == "textarea" || tipo == "number"){
			var valore = theForm.elements[k].value;
			theForm.elements[k].value = HtmlSanitizer.SanitizeHtml(valore);
		}
  }
  
  // aggiungo parametro idTab
  if(tabValue != ''){
  	addHidden(theForm, tabSessionKey , tabValue);
  	addHidden(theForm, prevTabSessionKey , tabValue);
  }

  addHidden(theForm, '<%=Costanti.PARAMETRO_AZIONE %>' , '<%=Costanti.VALUE_PARAMETRO_AZIONE_SALVA %>');

  theForm.submit();
};

</script>
<script type="text/javascript" src="js/add-element.js" nonce="<%= randomNonce %>"></script>
<jsp:include page="/jsp/addElementCustom.jsp" flush="true" />
<jsp:include page="/jsplib/menuUtente.jsp" flush="true" />
<link rel="shortcut icon" href="images/favicon.ico" type="image/x-icon" />
</head>
<body marginwidth=0 marginheight=0>
<table class="bodyWrapper">
	<tbody>
		<jsp:include page="/jsplib/templateHeader.jsp" flush="true" />
	
		<!-- TR3: Body -->
		<tr class="trPageBody">
			<% if(includiMenuLateraleSx){ %>
				<jsp:include page="/jsplib/menu.jsp" flush="true" />
			<% } %>
			<% if(customListViewName == null || "".equals(customListViewName)){ %>
				<jsp:include page="/jsplib/edit-page.jsp" flush="true" />
			<% } else {%>	
				<jsp:include page="/jsplib/edit-page-custom.jsp" flush="true" />
			<% } %>
		</tr>
	
		<jsp:include page="/jsplib/templateFooter.jsp" flush="true" />
	</tbody>
</table>
<jsp:include page="/jsplib/conferma.jsp" flush="true" />
</body>
</html>
