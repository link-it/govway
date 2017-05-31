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



<%@page import="org.apache.commons.lang.StringUtils"%>
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
if (iddati != null && !iddati.equals("notdefined"))
  gdString += iddati;
else
  iddati = "notdefined";
GeneralData gd = (GeneralData) session.getAttribute(gdString);

String logoImage = gd.getLogoHeaderImage();
String logoLink = gd.getLogoHeaderLink();
String logoTitolo = gd.getLogoHeaderTitolo();
%>
<% if(StringUtils.isNotEmpty(logoImage)){ %>
<!-- TR Logo -->
<!-- TR1: Header1 -->
<tr class="trPageHeaderLogo">
 	<td colspan="2" class="tdPageHeaderLogo">
		<table style="width:100%;">
			<tbody>
				 <tr>
				 	<td colspan="2" align="left">
				 		<% if(StringUtils.isNotEmpty(logoLink)){%>
				 			<a href="<%=logoLink %>" target="_blank">
				 				<img src="<%=logoImage %>" alt="<%=logoTitolo %>" title="<%=logoTitolo %>">
				 			</a>
				 		<% } else {%>
				 			<img src="<%=logoImage %>" alt="<%=logoTitolo %>" title="<%=logoTitolo %>">
				 		<% } %>
				 	</td>
			 	</tr>
		 	</tbody>
	 	</table>
 	</td>
</tr>
<% } %>
<!-- TR1: Header1 -->
<tr class="trPageHeader">
 	<td colspan="2" class="tdPageHeader">
		<table style="width:100%;">
			<tbody>
				 <tr>
				  	<td class="td1PageHeader">
				    	<span class="consoleTitle"><%= gd.getTitle() %></span>
				    </td>
				 	<td class="td2PageHeader" align="right">
				 		<table>
				 			<tbody>
				 				<tr>
				 					<td>
								 		<%
										Vector<GeneralLink> v = gd.getHeaderLinks();
							 			if(v!= null && v.size() > 1) {
							 				GeneralLink userNameLink = v.get(0);
									 		%>
											<div id="menuUtente" class="ddmenu-label">
												<div class="text-decor"> 
													<span class="nomeUtente"><%=userNameLink.getLabel() %></span>
													<span class="nomeUtenteImg"></span>
												</div>
												<div style="margin: 0px; padding: 0px; border: 0px; position: absolute; z-index: 100;">
													<div id="menuUtente_menu">
												  		<% 
												  		GeneralLink l;
												  		for (int i = 1; i < v.size(); i++) {
															l = (GeneralLink) v.elementAt(i);
															String icon = l.getIcon();
															String spanLabelClass= "item-label";
															String itemClass= "menu-item";
															if(icon!= null && icon.length() > 0){
																icon = "icon-" + icon;	
																spanLabelClass = "item-label-with-icon";
																itemClass ="menu-item-with-icon";
															}
															
															if (!l.getLabel().equals("")) {							
												  				%>
												    			<div class="<%=itemClass %>">
												    				<span class="item-icon <%=icon %>"></span>
												    				<span class="<%=spanLabelClass %>">
															    		<% 
															    		if (!l.getUrl().equals("")) {
															      			if (!l.getTarget().equals("")) {
															        		//url+target
																				if (l.getTarget().equals("_blank")) {
															          			%>
															          				<a class="td2PageHeader" onClick="var win = window.open('<%= l.getUrl() %>', '<%= l.getLabel().replace(' ', '_') %>', 'width=900,height=700,resizable=yes,scrollbars=yes');win.focus();return false;" 
															          					target="<%= l.getTarget() %>" href="<%= l.getUrl() %>"><%= l.getLabel() %></a>
															          			<%
																				}else if("new".equals(l.getTarget())){
																				%><a class="td2PageHeader" target="_blank" href="<%= l.getUrl() %>"><%= l.getLabel() %></a><%
																				}else {
															          			%><a class="td2PageHeader" target="<%= l.getTarget() %>" href="<%= l.getUrl() %>"><%= l.getLabel() %></a><%
																				}
															      			} else {
															        		//solo url
															        		%><a class="td2PageHeader" href="<%= l.getUrl() %>"><%= l.getLabel() %></a><%
															      			}
																		} else {
															      			if (!l.getOnClick().equals("")) {
															        		//onClick
															        		%><a class="td2PageHeader" href="" onClick="<%= l.getOnClick() %>; return false;"><%= l.getLabel() %></a><%
															      			} else {
																			//solo stringa
															        		%><span class="td2PageHeader"><%= l.getLabel() %></span><%
															      			}
															    		}
															    		%>
														    		</span>					    					    
													    		</div>
														    	<%
															}
												  		}
												    	%>
												  	</div>
											  	</div>
											</div>
								 		<%
							 			}
										%>
									</td>
				 				</tr>
				 			</tbody>
				 		</table>
					</td>
				 </tr>		
			</tbody>
		</table>
	</td>
</tr>
