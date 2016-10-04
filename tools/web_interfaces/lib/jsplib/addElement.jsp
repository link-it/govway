<%--
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
--%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
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

GeneralData gd = (GeneralData) session.getValue(gdString);
PageData pd = (PageData) session.getAttribute(pdString);

%>

<head>
<title><%= gd.getTitle() %></title>
<link rel=stylesheet href="images/<%= gd.getCss() %>" type=text/css>
<jsp:include page="/jsplib/browserUtils.jsp" flush="true" />
<script type="text/javascript" src="js/webapps.js"></script>
<!-- JQuery lib-->
<script type="text/javascript" src="js/jquery-latest.js"></script>
<!--Funzioni di utilita -->
<script type="text/javascript">
var iddati = '<%= iddati %>';
var params = '<%= params %>';
var version= '<%= (String)session.getAttribute("version")%>';
var path = '<%= request.getContextPath()%>';
</script>
<script type="text/javascript" src="js/PostBack.js"></script>
<script type="text/javascript" src="js/utils.js"></script>
<style type="text/css">@import url(images/ui.datepicker.css);</style>
<script type="text/javascript" src="js/ui.datepicker.js"></script>
<script type="text/javascript" src="js/ui.datepicker-it.js"></script>
<script type="text/javascript" src="js/jquery.placement.below.js"></script>
<style type="text/css">@import url(images/time-picker.css);</style>
<script type="text/javascript" src="js/jquery.timepicker-table.js"></script>
<style type="text/css">@import url(images/ui.core.css);</style>
<style type="text/css">@import url(images/ui.theme.css);</style>
<style type="text/css">@import url(images/ui.spinner.css);</style>
<style type="text/css">@import url(images/ui.slider.css);</style>
<script type="text/javascript" src="js/ui.core.js"></script>
<script type="text/javascript" src="js/ui.spinner.min.js"></script>
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
				var minvalue = 1;
                if(version=="optional"){
                    minvalue = 0;
                }	
                
                $("input[name=versione]:not([type=hidden])").spinner({min:minvalue,max:999});

				showSlider($("select[name^='percentuale']:not([type=hidden])"));
        });
</script>

<jsp:include page="/jsp/addElementCustom.jsp" flush="true" />
<link rel="shortcut icon" href="images/favicon.ico" type="image/x-icon" />
</head>
<body marginwidth=0 marginheight=0 onLoad="focusText(document.form);">
<table border=0 cellspacing=0 cellpadding=0 width=100%>

<jsp:include page="/jsplib/templateHeader.jsp" flush="true" />

<tr>

<jsp:include page="/jsplib/menu.jsp" flush="true" />
<jsp:include page="/jsplib/edit-page.jsp" flush="true" />
<jsp:include page="/jsplib/templateFooter.jsp" flush="true" />

</tr>
</table>
</body>
</html>
