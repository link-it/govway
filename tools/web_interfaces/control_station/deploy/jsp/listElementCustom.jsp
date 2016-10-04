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



<%@ page session="true" import="java.util.*, org.openspcoop2.web.lib.mvc.*" %>

<%
ListElement listElement = 
	  (org.openspcoop2.web.lib.mvc.ListElement) session.getValue("ListElement");

String nomeServlet = listElement.getOggetto();
String nomeServletAdd = nomeServlet+"Add.do";
String nomeServletDel = nomeServlet+"Del.do";
String nomeServletList = nomeServlet+"List.do";
if (nomeServlet == "monitor" || nomeServlet == "monitorSinglePdD") {
	nomeServletAdd = nomeServlet+".do";
	nomeServletDel = nomeServlet+".do";
	nomeServletList = nomeServlet+".do";
}
	  
%>

<SCRIPT>
nomeServletAdd_Custom = '<%= nomeServletAdd %>';
nomeServletDel_Custom = '<%= nomeServletDel %>';
nomeServletList_Custom = '<%= nomeServletList %>';
</SCRIPT>
