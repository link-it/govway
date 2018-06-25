<%--
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
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



<%@ page session="true" import="java.util.Vector, org.openspcoop2.web.lib.mvc.*" %>

<%
String iddati = "";
String ct = request.getContentType();
if (ct != null && (ct.indexOf("multipart/form-data") != -1)) {
  iddati = (String) session.getAttribute("iddati");
} else {
  iddati = request.getParameter("iddati");
}
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
%>

<td class="td1PageBody" valign='top'>
	<div id="menuct">
		<% 
			Vector<?> v = pd.getMenu(); 
			MenuEntry m = null;
	
			for (int i = 0; i <  v.size(); i++) {
				  m = (MenuEntry) v.elementAt(i);
		%>
			<div class="sezioneMenu">
				<div class="titoloSezione">
					<span><%= m.getTitle() %></span>
				</div>		
			
				<%
			  		String [][] entries = m.getEntries();
			  		for (int j = 0; j < entries.length; j++) {
			  			
			  			String cssClass = "voceMenu";
			  			
			    		if (entries[j].length == 2) {
			      		%><div class="voceMenu">
			      				<a class='<%= cssClass %>' title='<%= entries[j][0] %>' href='<%= entries[j][1] %>'><%= entries[j][0] %></a>
			      			</div>
			      		<%
			    		} else if (entries[j].length == 3) {
			      		%><div class="voceMenu">
			      			<a class='<%= cssClass %>' target='<%= entries[j][2] %>' title='<%= entries[j][0] %>' href='<%= entries[j][1] %>'><%= entries[j][0] %></a>
			      			</div>
			      		<%
			    		}
			 	 }
			 %>
				
			</div>
		<% } %>
	</div>
</td>

