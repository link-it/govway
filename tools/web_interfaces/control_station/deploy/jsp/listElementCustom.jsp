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
