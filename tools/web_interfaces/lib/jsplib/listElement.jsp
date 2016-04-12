<%--
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
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
<%@ page session="true" import="java.util.*, org.openspcoop2.web.lib.mvc.*" %>
<html>
<%
String iddati = request.getParameter("iddati");
String params = (String) request.getAttribute("params");

if (params == null)
	 params="";

ListElement listElement = 
	  (org.openspcoop2.web.lib.mvc.ListElement) session.getValue("ListElement");

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

GeneralData gd = (GeneralData) session.getValue(gdString);
PageData pd = (PageData) session.getAttribute(pdString);

Vector v = pd.getDati();
int n = v.size();
String search = request.getParameter("search");
if (search == null)
  search = "";
%>

<head>
<jsp:include page="/jsplib/browserUtils.jsp" flush="true" />
<title><%= gd.getTitle() %></title>
<link rel=stylesheet href=images/<%= gd.getCss() %> type=text/css>
<script type="text/javascript" src="js/webapps.js"></script>


<SCRIPT>
var nomeServletAdd_Custom = '<%= nomeServletAdd %>';
var nomeServletDel_Custom = '<%= nomeServletDel %>';
var nomeServletList_Custom = '<%= nomeServletList %>';
</SCRIPT>


<jsp:include page="/jsp/listElementCustom.jsp" flush="true" />



<SCRIPT>
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
</SCRIPT>

<!-- JQuery lib-->
<script type="text/javascript" src="js/jquery-latest.js"></script>
<!--Funzioni di utilita -->
<script type="text/javascript" src="js/jquery.confirm-1.2.js"></script>
<script type="text/javascript" src="js/utils.js"></script>
<link rel="shortcut icon" href="images/favicon.ico" type="image/x-icon" />
</head>
<body marginwidth=0 marginheight=0 onLoad="focusText(document.form);">
<table border=0 cellspacing=0 cellpadding=0 width=100%>

<jsp:include page="/jsplib/templateHeader.jsp" flush="true" />

<tr>

<jsp:include page="/jsplib/menu.jsp" flush="true" />
<jsp:include page="/jsplib/full-list.jsp" flush="true" />
<jsp:include page="/jsplib/templateFooter.jsp" flush="true" />

</tr>
</table>
</body>
</html>
