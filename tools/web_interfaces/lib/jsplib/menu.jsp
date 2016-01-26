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



<%@ page session="true" import="java.util.Vector, org.openspcoop2.web.lib.mvc.*" %>

<%
String iddati = "";
String ct = request.getContentType();
if (ct != null && (ct.indexOf("multipart/form-data") != -1)) {
  iddati = (String) session.getValue("iddati");
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
GeneralData gd = (GeneralData) session.getValue(gdString);
PageData pd = (PageData) session.getAttribute(pdString);
%>

<td width='130' height='425' align='right' valign='top' class='menuLeft'>
	<span>
		<ul>
			<% Vector v = pd.getMenu();
				MenuEntry m;
				
				for (int i = 0; i <  v.size(); i++) {
					  m = (MenuEntry) v.elementAt(i);
			 %><li>
			  		<strong><%= m.getTitle() %></strong>
			 		<img src=images/dothdx.gif>
			 		<ul>
			  <%
			  		String [][] entries = m.getEntries();
			  		for (int j = 0; j < entries.length; j++) {
			  			
			  			String cssClass = "menuLeft";
			  			
			    		if (entries[j].length == 2) {
			      		%><li><a class='<%= cssClass %>' title='<%= entries[j][0] %>' href='<%= entries[j][1] %>'><%= entries[j][0] %></a></li><%
			    		} else if (entries[j].length == 3) {
			      		%><li><a class='<%= cssClass %>' target='<%= entries[j][2] %>' title='<%= entries[j][0] %>' href='<%= entries[j][1] %>'><%= entries[j][0] %></a></li><%
			    		}
			 	 }
			 %>	
			 		</ul>
			 </li>
			 <br/>
			 <% } %>
		</ul>
	</span>
</td>

