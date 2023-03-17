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
<link rel="stylesheet" href="css/<%= gd.getCss() %>" type="text/css">
<link rel="stylesheet" href="css/materialIcons.css" type="text/css">
<link rel="stylesheet" href="css/ui.core.css" type="text/css">
<link rel="stylesheet" href="css/ui.theme.css" type="text/css">
<link rel="stylesheet" href="css/ui.dialog.css" type="text/css">
<link rel="stylesheet" href="css/ui.slider.css" type="text/css">
<link rel="stylesheet" href="css/ui.datepicker.css" type="text/css">
<link rel="stylesheet" href="css/bootstrap-tagsinput.css" type="text/css">
<!-- JQuery lib-->
<script type="text/javascript" src="js/jquery-3.6.4.js" nonce="<%= randomNonce %>"></script>
<script type="text/javascript" src="js/jquery-ui-1.13.2.js" nonce="<%= randomNonce %>"></script>
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
<script type="text/javascript" src="js/array-from.js" nonce="<%= randomNonce %>"></script>
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

  addHidden(document.form, '<%=Costanti.PARAMETRO_AZIONE %>' , 'salva');

  document.form.submit();
};

$(document).ready(function(){

  	// info
  	if($(".spanIconInfoBox").length>0){
  		$(".spanIconInfoBox").click(function(){
  			var iconInfoBoxId = $(this).parent().prop('id');
  			var idx = iconInfoBoxId.substring(iconInfoBoxId.indexOf("_")+1);
  			console.log(idx);
  			if(idx) {
				var label = $("#hidden_title_iconInfo_"+ idx).val();
				var body = $("#hidden_body_iconInfo_"+ idx).val();
				mostraDataElementInfoModal(label,body);
  			}
		});
  	}
  	
  	if($(".iconInfoBox-cb-info").length>0){
  		$(".iconInfoBox-cb-info").click(function(){
  			var iconInfoBoxId = $(this).prop('id');
  			var idx = iconInfoBoxId.substring(iconInfoBoxId.indexOf("_")+1);
  			console.log(idx);
  			if(idx) {
				var label = $("#hidden_title_iconInfo_"+ idx).val();
				var body = $("#hidden_body_iconInfo_"+ idx).val();
				mostraDataElementInfoModal(label,body);
  			}
		});
  	}
  	
    //date time tracciamento
    //date time diagnostica
    $(":input[name='datainizio']").datepicker({dateFormat: 'yy-mm-dd', showOtherMonths: true, selectOtherMonths: false, showWeek: true, firstDay: 1, changeMonth: true, changeYear: true});
    $(":input[name='datafine']").datepicker({dateFormat: 'yy-mm-dd', showOtherMonths: true, selectOtherMonths: false, showWeek: true, firstDay: 1, changeMonth: true, changeYear: true});

    showSlider($("select[name*='percentuale']:not([type=hidden])"));
    
    var numInputs = Array.from(document.querySelectorAll('input[type=number]'));

   	for(var i = 0 ; i < numInputs.length; i++){
   		if(numInputs[i].hasAttribute('gw-function')){
   			$(numInputs[i]).on('change', window[numInputs[i].getAttribute('gw-function')]);
   		} else {
   			$(numInputs[i]).on('change', inputNumberChangeEventHandler	);
   		}
   	}
    
    scrollToPostBackElement(destElement);
});
        
</script>

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
<div id="dataElementInfoModal" title="Info">
	<div id="dataElementInfoModalBody" class="contenutoModal"></div>
</div>
<jsp:include page="/jsplib/conferma.jsp" flush="true" />
</body>
</html>
