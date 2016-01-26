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



<%@page import="java.util.Vector"%>
<%@ page session="true" import="org.openspcoop2.web.lib.mvc.*" %>

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
else
  iddati = "notdefined";

GeneralData gd = (GeneralData) session.getValue(gdString);
PageData pd = (PageData) session.getAttribute(pdString);

String ghf = request.getParameter("generateHiddenForm");
Boolean generateHiddenForm = false;
String hFormMethod = "";
if(ghf != null){
	generateHiddenForm = Boolean.parseBoolean(ghf);
}

if(generateHiddenForm)
	hFormMethod = "method='POST'" + "  action='" + gd.getUrl() +"'";

%>

<form name=form  <%=hFormMethod  %> >
	<td valign=top background=images/plugsx.gif class=corpoTesto>
		<p><span class=history>

			<jsp:include page="/jsplib/titlelist.jsp" flush="true" />

			<%= pd.getPageDescription() %>
		</p>

			<%= pd.getMessage() %><br><br>
			
			<%
			if(generateHiddenForm){
				Vector<?> dati = pd.getDati();

				for(int i = 0; i < dati.size() ; i++){
					DataElement de = (DataElement) dati.get(i);
					
					String type = de.getType();
					
					// tutti gli elementi che rappresentano lo stato sono stati convertiti in elementi hidden
					if(type.equals("hidden")){
						%><input type=hidden name="<%= de.getName()  %>" value="<%= de.getValue()  %>"/><%
					}
				}
			}
			
			String [][] bottoni = pd.getBottoni();
			if ((bottoni != null) && (bottoni.length > 0)) {
			  for (int i = 0; i < bottoni.length; i++) {
			    %><input type=button onClick="<%= bottoni[i][1] %>" value="<%= bottoni[i][0] %>">&nbsp;<%
			  }
			}
  		%>
  	</td>
</form>
