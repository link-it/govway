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



<%@ page session="true" import="java.util.List, org.openspcoop2.web.lib.mvc.*" %>

<%
String iddati = "";
String ct = request.getContentType();
if (ct != null && (ct.indexOf(Costanti.MULTIPART) != -1)) {
  iddati = ServletUtils.getObjectFromSession(request, session, String.class, Costanti.SESSION_ATTRIBUTE_ID_DATI);
} else {
  iddati = request.getParameter(Costanti.PARAMETER_NAME_ID_DATI);
}
String gdString = Costanti.SESSION_ATTRIBUTE_GENERAL_DATA;
String pdString = Costanti.SESSION_ATTRIBUTE_PAGE_DATA;
if (iddati != null && !iddati.equals("notdefined")) {
  gdString += iddati;
  pdString += iddati;
}
else {
  iddati = "notdefined";
}
GeneralData gd = ServletUtils.getObjectFromSession(request, session, GeneralData.class, gdString);
PageData pd = ServletUtils.getObjectFromSession(request, session, PageData.class, pdString);

%>

<td class="td1PageBody" valign='top'>
	<div id="menuct">
		<% 
			List<?> v = pd.getMenu(); 
			MenuEntry m = null;
	
			for (int i = 0; i <  v.size(); i++) {
				  m = (MenuEntry) v.get(i);
		%>
			<div class="sezioneMenu">
				<div class="titoloSezione">
					<span><%= m.getTitle() %></span>
				</div>		
			
				<%
			  		String [][] entries = m.getEntries();
			  		for (int j = 0; j < entries.length; j++) {
			  			String deVoceMenuName = "url_entry_"+j;
			  			String cssClass = "voceMenuRC";
			  			
			    		if (entries[j].length == 2) {
			    			
			      		%><div class="voceMenuRC" id="voceMenu_<%=j %>">
		      				<a class='<%= cssClass %>' title='<%= entries[j][0] %>' href='<%= entries[j][1] %>'>
		      					<p class='<%= cssClass %>'><%= entries[j][0] %></p>
		      					</a>
			      			</div>
			      		<%
			    		} else if (entries[j].length == 3) {
			      		%><div class="voceMenuRC" id="voceMenu_<%=j %>">
			      			<a class='<%= cssClass %>' target='<%= entries[j][2] %>' title='<%= entries[j][0] %>' href='<%= entries[j][1] %>'>
			      				<p class='<%= cssClass %>'><%= entries[j][0] %></p>
			      			</a>
			      			</div>
			      		<%
			    		}
			 	 }
	  		 %>
				
			</div>
		<% } %>
	</div>
</td>

