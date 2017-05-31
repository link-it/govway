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
else
  iddati = "notdefined";
GeneralData gd = (GeneralData) session.getAttribute(gdString);
PageData pd = (PageData) session.getAttribute(pdString);
String showListInfos = request.getParameter("showListInfos");
Vector<?> v = pd.getDati();

String message = pd.getMessage();
String messageType = pd.getMessageType();
String pageDescription = pd.getPageDescription();
%>
<table style="width:100%;">
	<tbody>
		<tr>
			<td>
				<div id="breadcrumb-ct">
					<%
						Vector<GeneralLink> titlelist = pd.getTitleList();
						if (titlelist != null && titlelist.size() > 0) {
						%>
					<div id="crumbs">
						<ul>
						<%
						GeneralLink l;
					  	for (int i = 0; i < titlelist.size(); i++) {
					    	l = titlelist.elementAt(i);
						    if (!l.getLabel().equals("")) {
						    	if (i != titlelist.size()-1) {
					        		if (!l.getUrl().equals("")) {
							          //non ultimo con url
							          %>
							         	<li><a href="<%= l.getUrl() %>"><span><%= l.getLabel() %></span></a></li>
							         <%
							        } else {
							          //non ultimo ma senza url
							          %>
							          <li><span><%= l.getLabel() %></span></li>
							          <%
							        }
					        		// per ogni elemento non ultimo aggiugo la freccia >
					        		%>
					        			<li><img alt="" src="images/tema_link/next_white.png"/></li>
					        		<%
					     		 } else {
								    //ultimo
								    String labelUltimo = l.getLabel();
								    
// 								    if (showListInfos != null && showListInfos.equals("true")) {
// 										int index = pd.getIndex();
// 									  	if (pd.getNumEntries() > 0){
// 									    	index++;
// 										}
									  	
// 									  	labelUltimo += " ["+index+"-"+(v.size()+pd.getIndex())+"] su " + pd.getNumEntries();
// 								    }
							        %>
								    <li class="ultimo-path"><span><%=labelUltimo %></span></li>
								    <%
					      		}
					    	}
						}
						%>
						</ul>
					</div>
				<%
				}
				%>
				</div>
			</td>
		</tr>
		<%
		if (!pageDescription.equals("")) {
		  %>
			<tr>
				<td>
		  			<div class="pageDescription">
		  				<%= pageDescription %>
		  			</div>
		  		</td>
			</tr>
		  <%
		}
		%>
		<%
		if (!message.equals("")) {
		  %>
		  	<tr>
				<td>
		  			<div class="messages-<%=messageType %>">
		  				<%= message %>
		  			</div>
		  		</td>
			</tr>
		  <%
		}
		%>
	</tbody>
</table>
