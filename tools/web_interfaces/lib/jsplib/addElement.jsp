<%--
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
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

<!DOCTYPE html>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page session="true" import="org.openspcoop2.web.lib.mvc.*" %>

<html>
<%
String iddati = request.getParameter("iddati");
String params = (String) request.getAttribute("params");
String gdString = "GeneralData";
String pdString = "PageData";
if (iddati != null && !iddati.equals("notdefined")) {
  gdString += iddati;
  pdString += iddati;
}
else {
  iddati = "notdefined";
}

if(params == null) params="";

GeneralData gd = (GeneralData) session.getAttribute(gdString);
PageData pd = (PageData) session.getAttribute(pdString);

%>

<head>
<meta charset="UTF-8">
<title><%= gd.getTitle() %></title>
<link href="css/roboto/roboto-fontface.css" rel="stylesheet" type="text/css">
<link rel="stylesheet" href="css/<%= gd.getCss() %>" type="text/css">
<jsp:include page="/jsplib/browserUtils.jsp" flush="true" />
<script type="text/javascript" src="js/webapps.js"></script>
<!-- JQuery lib-->
<script type="text/javascript" src="js/jquery-latest.js"></script>
<!--Funzioni di utilita -->
<script type="text/javascript">
var iddati = '<%= iddati %>';
var params = '<%= params %>';
var path = '<%= request.getContextPath()%>';
</script>
<script type="text/javascript" src="js/PostBack.js"></script>
<script type="text/javascript" src="js/utils.js"></script>
<style type="text/css">@import url(css/ui.datepicker.css);</style>
<script type="text/javascript" src="js/ui.datepicker.js"></script>
<script type="text/javascript" src="js/ui.datepicker-it.js"></script>
<script type="text/javascript" src="js/jquery.placement.below.js"></script>
<style type="text/css">@import url(css/time-picker.css);</style>
<script type="text/javascript" src="js/jquery.timepicker-table.js"></script>
<style type="text/css">@import url(css/ui.core.css);</style>
<style type="text/css">@import url(css/ui.theme.css);</style>
<style type="text/css">@import url(css/ui.slider.css);</style>
<script type="text/javascript" src="js/ui.core.js"></script>
<script type="text/javascript" src="js/ui.slider.js"></script>
<script>
var nr = 0;
function CheckDati() {
  if (nr != 0) {
    return false;
  }

  //I controlli si fanno direttamente nei .java
  nr = 1;
  document.form.submit();
};

</script>
<script type="text/javascript">
        $(document).ready(function(){
                //date time tracciamento
                //date time diagnostica
                $(":input[name='datainizio']").datepicker({dateFormat: 'yy-mm-dd'});
                $(":input[name='datafine']").datepicker({dateFormat: 'yy-mm-dd'});

                showSlider($("select[name^='percentuale']:not([type=hidden])"));
        });
</script>

<jsp:include page="/jsp/addElementCustom.jsp" flush="true" />
<jsp:include page="/jsplib/menuUtente.jsp" flush="true" />
<link rel="shortcut icon" href="images/favicon.ico" type="image/x-icon" />
</head>
<body marginwidth=0 marginheight=0 onLoad="focusText(document.form);">
<table class="bodyWrapper">
	<tbody>
		<jsp:include page="/jsplib/templateHeader.jsp" flush="true" />
	
		<!-- TR3: Body -->
		<tr class="trPageBody">
			<jsp:include page="/jsplib/menu.jsp" flush="true" />
			<jsp:include page="/jsplib/edit-page.jsp" flush="true" />
		</tr>
	
		<jsp:include page="/jsplib/templateFooter.jsp" flush="true" />
	</tbody>
</table>
</body>
</html>
