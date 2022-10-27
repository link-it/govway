<%--
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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
if (ct != null && (ct.indexOf(Costanti.MULTIPART) != -1)) {
  iddati = ServletUtils.getObjectFromSession(request, session, String.class, Costanti.SESSION_ATTRIBUTE_ID_DATI);
} else {
  iddati = request.getParameter(Costanti.PARAMETER_NAME_ID_DATI);
}
String gdString = Costanti.SESSION_ATTRIBUTE_GENERAL_DATA;
if (iddati != null && !iddati.equals("notdefined"))
  gdString += iddati;
else
  iddati = "notdefined";
GeneralData gd = ServletUtils.getObjectFromSession(request, session, GeneralData.class, gdString);
String randomNonce = (String) request.getAttribute(Costanti.REQUEST_ATTRIBUTE_CSP_RANDOM_NONCE);

String logoImage = gd.getLogoHeaderImage();
String logoLink = gd.getLogoHeaderLink();
String logoTitolo = gd.getLogoHeaderTitolo();
boolean visualizzaLinkHome = gd.isVisualizzaLinkHome();
String homeLink = request.getContextPath() + "/messagePage"+".do?dest=home";
String tabSessionKey = ServletUtils.getTabIdFromRequestAttribute(request);

if(tabSessionKey == null)
	tabSessionKey = "";

if(!tabSessionKey.equals("")){
	homeLink = homeLink + "&" + Costanti.PARAMETER_TAB_KEY + "=" + tabSessionKey;
}

// <span class="item-icon \<\%=icon \%\>"></span>
%>
<script type="text/javascript" src="js/autocomplete.js" nonce="<%= randomNonce %>"></script>
<% if(StringUtils.isNotEmpty(logoImage)){ %>
<!-- TR Logo -->
<!-- TR1: Header1 -->
<tr class="trPageHeaderLogo">
 	<td colspan="2" class="tdPageHeaderLogo">
		<table class="tablePageHeader">
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
		<table class="tablePageHeader">
			<tbody>
				 <tr>
				  	<td class="td1PageHeader">
				  		<% if(visualizzaLinkHome){%>
					  		<a href="<%=homeLink %>" class="titleIconLinkLeft">
					    		<span class="consoleTitle"><%= gd.getTitle() %></span>
				    		</a>
			    		<% } else {%>
				 			<span class="consoleTitle"><%= gd.getTitle() %></span>
				 		<% } %>
				    </td>
				 	<td class="td2PageHeader" align="right">
				 		<table>
				 			<tbody>
				 				<tr>
				 					<%
										Vector<GeneralLink> soggetti = gd.getSoggettiLinks();
							 			if(soggetti!= null && soggetti.size() > 0) {
							 				GeneralLink soggettoTitoloLink = soggetti.get(0);
									 		%>
									 		<td>
									 			<% if(soggettoTitoloLink.getUrl().equals("")){ %>
										 			<div id="menuSoggetto" class="ddmenu-label">
														<div class="text-decor"> 
															<span class="soggetto"><%=soggettoTitoloLink.getLabel() %></span>
															<% if(soggetti.size() > 1){%>
																<span class="soggettoImg"></span>
															<%
																}
															%>
														</div>
														<% 
														if(soggetti.size() > 1){
														%>
														<div class="container-menu-display-none">
															<div id="menuSoggetto_menu">
											 				<% 
													  		GeneralLink l;
													  		for (int i = 1; i < soggetti.size(); i++) {
																l = (GeneralLink) soggetti.elementAt(i);
	// 															String icon = l.getIcon();
																String spanLabelClass= "item-label";
																String itemClass= "menu-item-no-icon";
																String id = "soggetto_link_" + i;
	// 															if(icon!= null && icon.length() > 0){
	// 																icon = "icon-" + icon;	
	// 																spanLabelClass = "item-label-with-icon";
	// 																itemClass ="menu-item-with-icon";
	// 															}
	
																String toolTip = " ";
																if(!l.getTooltip().equals("")){
																	toolTip = " title=\"" + l.getTooltip() + "\"";
														  		}
																
																if(l.getUrl().equals("")){
																	itemClass += " menu-no-pointer";
																} else {
																	l.addParameter(new Parameter(Costanti.PARAMETER_PREV_TAB_KEY, tabSessionKey));
																}
																
																if (!l.getLabel().equals("")) {							
													  				%>
													    			<div class="<%=itemClass %>">
													    				<span class="<%=spanLabelClass %>">
																    		<% 
																    		if (!l.getUrl().equals("")) {
																      			if (!l.getTarget().equals("")) {
																        		//url+target
																					if (l.getTarget().equals("_blank")) {
																          			%>
																          				<a id="<%=id %>" class="td2PageHeader" target="<%= l.getTarget() %>" href="<%= l.getUrl() %>" <%= toolTip %> ><%= l.getLabel() %></a>
																          				<script type="text/javascript" nonce="<%= randomNonce %>">
																					      	 $(document).ready(function(){
																									$('#<%=id %>').click(function() {
																										var win = window.open('<%= l.getUrl() %>', '<%= l.getLabel().replace(' ', '_') %>', 'width=900,height=700,resizable=yes,scrollbars=yes');win.focus();return false;
																									});
																								});
																						</script>
																          			<%
																					}else if("new".equals(l.getTarget())){
																					%><a id="<%=id %>" class="td2PageHeader" target="_blank" href="<%= l.getUrl() %>" <%= toolTip %> ><%= l.getLabel() %></a><%
																					}else {
																          			%><a id="<%=id %>" class="td2PageHeader" target="<%= l.getTarget() %>" href="<%= l.getUrl() %>" <%= toolTip %> ><%= l.getLabel() %></a>
																          				<script type="text/javascript" nonce="<%= randomNonce %>">
																					      	 $(document).ready(function(){
																									$('#<%=id %>').click(function() {
																										<%= Costanti.JS_FUNCTION_VISUALIZZA_AJAX_STATUS %>return true;
																									});
																								});
																						</script>
																          			<%
																					}
																      			} else {
																        		//solo url
																        		%><a id="<%=id %>" class="td2PageHeader" href="<%= l.getUrl() %>" <%= toolTip %> ><%= l.getLabel() %></a>
																        			<script type="text/javascript" nonce="<%= randomNonce %>">
																					      	 $(document).ready(function(){
																									$('#<%=id %>').click(function() {
																										<%= Costanti.JS_FUNCTION_VISUALIZZA_AJAX_STATUS %>return true;
																									});
																								});
																						</script>
																        		<%
																      			}
																			} else {
																      			if (!l.getOnClick().equals("")) {
																        		//l.getOnClick()
																        		%><a id="<%=id %>" class="td2PageHeader" href="" <%= toolTip %> ><%= l.getLabel() %></a>
																        			<script type="text/javascript" nonce="<%= randomNonce %>">
																					      	 $(document).ready(function(){
																									$('#<%=id %>').click(function() {
																										<%= Costanti.JS_FUNCTION_VISUALIZZA_AJAX_STATUS %><%= l.getOnClick() %>;return true;
																									});
																								});
																						</script>
																        		<%
																      			} else {
																				//solo stringa
																        		%><span class="td2PageHeader" <%= toolTip %> ><%= l.getLabel() %></span><%
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
													  	<% } %>
													</div>
												<%
									 			} else {
													// Autocomplete
													%>
													<div id="menuSoggettoAutocomplete" class=""> 
														<script type="text/javascript" nonce="<%= randomNonce %>">
													  
													 		 /*An array containing all the country names in the world:*/
													  		var soggettiSuggestionList = [];
													  		
														  	
													  		<% 
													  		String labelSelezionato = "";
										  					GeneralLink l;
										  					for (int i = 1; i < soggetti.size(); i++) {
																l = (GeneralLink) soggetti.elementAt(i);
																
																String label = l.getLabel();
																String value = l.getUrl();
																String selected = "false";
																if(l.getUrl().equals("")){
																	selected = "true";
																	labelSelezionato = l.getLabel();
														  		} 
																
																%>
																	var soggettoItem_<%=i %> = {};
																	soggettoItem_<%=i %>.value = '<%= value %>';
																	soggettoItem_<%=i %>.label = '<%= label %>';
																	soggettoItem_<%=i %>.selected = <%= selected %>;
																
																	soggettiSuggestionList.push(soggettoItem_<%=i %>);
																<%
										  					}
													    	%>
												  		</script>
													
														<div class="autocomplete">
															<span class="soggettoAutocomplete">Soggetto:</span>
														    <input id="menuSoggetto_menuAutocomplete" type="text" name="soggettoAutoComplete" value="<%=labelSelezionato %>">
														  </div>
													
													  <script type="text/javascript" nonce="<%= randomNonce %>">
														  autocomplete(document.getElementById("menuSoggetto_menuAutocomplete"), soggettiSuggestionList);
													  </script>
													</div>
												<% } %>
									 		</td>
				 						<% } %>
				 					<%
										Vector<GeneralLink> modalita = gd.getModalitaLinks();
							 			if(modalita!= null && modalita.size() > 0) {
							 				GeneralLink modalitaTitoloLink = modalita.get(0);
									 		%>
									 		<td>
										 		<div id="menuModalita" class="ddmenu-label">
													<div class="text-decor">
														<% if(modalitaTitoloLink.getUrl().equals("")){%>
															<span class="modalita"><%=modalitaTitoloLink.getLabel() %></span>
															<% if(modalita.size() > 1){%>
																<span class="modalitaImg"></span>
															<%
																}
															}else {
																String id = "modalita_link_0";
															%>
															<span class="modalita">
																<a id="<%=id %>" class="td2PageHeader" href="<%= modalitaTitoloLink.getUrl() %>"><%= modalitaTitoloLink.getLabel() %></a>
															 	<script type="text/javascript" nonce="<%= randomNonce %>">
															      	 $(document).ready(function(){
																			$('#<%=id %>').click(function() {
																				<%= Costanti.JS_FUNCTION_VISUALIZZA_AJAX_STATUS %>return true;
																			});
																		});
																</script>
															 </span>
														<% }%>
													</div>
													<% if(modalita.size() > 1){%>
														<div class="container-menu-display-none">
															<div id="menuModalita_menu">
											 				<% 
													  		GeneralLink l;
													  		for (int i = 1; i < modalita.size(); i++) {
																l = (GeneralLink) modalita.elementAt(i);
	// 															String icon = l.getIcon();
																String spanLabelClass= "item-label";
																String itemClass= "menu-item-no-icon";
																String id = "modalita_link_" + i;
	// 															if(icon!= null && icon.length() > 0){
	// 																icon = "icon-" + icon;	
	// 																spanLabelClass = "item-label-with-icon";
	// 																itemClass ="menu-item-with-icon";
	// 															}
																
																if(l.getUrl().equals("")){
																	itemClass += " menu-no-pointer";
																} else {
																	l.addParameter(new Parameter(Costanti.PARAMETER_PREV_TAB_KEY, tabSessionKey));
																}
																
																if (!l.getLabel().equals("")) {							
													  				%>
													    			<div class="<%=itemClass %>">
													    				<span class="<%=spanLabelClass %>">
																    		<% 
																    		if (!l.getUrl().equals("")) {
																      			if (!l.getTarget().equals("")) {
																        		//url+target
																					if (l.getTarget().equals("_blank")) {
																          			%>
																          				<a id="<%=id %>" class="td2PageHeader" target="<%= l.getTarget() %>" href="<%= l.getUrl() %>"><%= l.getLabel() %></a>
																          					<script type="text/javascript" nonce="<%= randomNonce %>">
																						      	 $(document).ready(function(){
																										$('#<%=id %>').click(function() {
																											var win = window.open('<%= l.getUrl() %>', '<%= l.getLabel().replace(' ', '_') %>', 'width=900,height=700,resizable=yes,scrollbars=yes');win.focus();return false;
																										});
																									});
																							</script>
																          			<%
																					}else if("new".equals(l.getTarget())){
																					%><a id="<%=id %>" class="td2PageHeader" target="_blank" href="<%= l.getUrl() %>"><%= l.getLabel() %></a><%
																					}else {
																          			%><a id="<%=id %>" class="td2PageHeader" target="<%= l.getTarget() %>" href="<%= l.getUrl() %>"><%= l.getLabel() %></a>
																          				<script type="text/javascript" nonce="<%= randomNonce %>">
																					      	 $(document).ready(function(){
																									$('#<%=id %>').click(function() {
																										<%= Costanti.JS_FUNCTION_VISUALIZZA_AJAX_STATUS %>return true;
																									});
																								});
																						</script>
																          			<%
																					}
																      			} else {
																        		//solo url
																        		%><a id="<%=id %>" class="td2PageHeader" href="<%= l.getUrl() %>"><%= l.getLabel() %></a>
																        			<script type="text/javascript" nonce="<%= randomNonce %>">
																				      	 $(document).ready(function(){
																								$('#<%=id %>').click(function() {
																									<%= Costanti.JS_FUNCTION_VISUALIZZA_AJAX_STATUS %>return true;
																								});
																							});
																					</script>
																        		<%
																      			}
																			} else {
																      			if (!l.getOnClick().equals("")) {
																        		//l.getOnClick()
																        		%><a id="<%=id %>" class="td2PageHeader" href=""><%= l.getLabel() %></a>
																        			<script type="text/javascript" nonce="<%= randomNonce %>">
																				      	 $(document).ready(function(){
																								$('#<%=id %>').click(function() {
																									<%= Costanti.JS_FUNCTION_VISUALIZZA_AJAX_STATUS %><%= l.getOnClick() %>;return true;
																								});
																							});
																					</script>
																        		<%
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
												  	<% }%>
												</div>
									 		</td>
				 						<% }%>
				 					<td>
								 		<%
										Vector<GeneralLink> v = gd.getHeaderLinks();
							 			if(v!= null && v.size() > 1) {
							 				GeneralLink userNameLink = v.get(0);
									 		%>
											<div id="menuUtente" class="ddmenu-label">
												<div class="text-decor"> 
													<span class="nomeUtenteImg">
													
													</span>
												</div>
												<div class="container-menu-display-none">
													<div id="menuUtente_menu">
												  		<% 
												  		GeneralLink l;
												  		for (int i = 1; i < v.size(); i++) {
															l = (GeneralLink) v.elementAt(i);
															String icon = l.getIcon();
															String spanLabelClass= "item-label";
															String itemClass= "menu-item";
															String id = "menuUtente_link_" + i;
															if(icon!= null && icon.length() > 0){
																icon = "icon-" + icon;	
																spanLabelClass = "item-label-with-icon";
																itemClass ="menu-item-with-icon";
															}
															
															if(l.getUrl().equals("")){
																itemClass += " menu-no-pointer";
															} else {
																l.addParameter(new Parameter(Costanti.PARAMETER_PREV_TAB_KEY, tabSessionKey));
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
															          				<a id="<%=id %>" class="td2PageHeader" target="<%= l.getTarget() %>" href="<%= l.getUrl() %>"><%= l.getLabel() %></a>
														          					<script type="text/javascript" nonce="<%= randomNonce %>">
																				      	 $(document).ready(function(){
																								$('#<%=id %>').click(function() {
																									var win = window.open('<%= l.getUrl() %>', '<%= l.getLabel().replace(' ', '_') %>', 'width=900,height=700,resizable=yes,scrollbars=yes');win.focus();return false;
																								});
																							});
																					</script>
															          			<%
																				}else if("new".equals(l.getTarget())){
																				%><a id="<%=id %>" class="td2PageHeader" target="_blank" href="<%= l.getUrl() %>"><%= l.getLabel() %></a><%
																				}else {
															          			%><a id="<%=id %>" class="td2PageHeader" target="<%= l.getTarget() %>" href="<%= l.getUrl() %>"><%= l.getLabel() %></a>
															          				<script type="text/javascript" nonce="<%= randomNonce %>">
																					      	 $(document).ready(function(){
																									$('#<%=id %>').click(function() {
																										<%= Costanti.JS_FUNCTION_VISUALIZZA_AJAX_STATUS %>return true;
																									});
																								});
																						</script>
															          			<%
																				}
															      			} else {
															        		//solo url
															        		%><a id="<%=id %>" class="td2PageHeader" href="<%= l.getUrl() %>"><%= l.getLabel() %></a>
															        			<script type="text/javascript" nonce="<%= randomNonce %>">
																					      	 $(document).ready(function(){
																									$('#<%=id %>').click(function() {
																										<%= Costanti.JS_FUNCTION_VISUALIZZA_AJAX_STATUS %>return true;
																									});
																								});
																						</script>
																			<%
															      			}
																		} else {
															      			if (!l.getOnClick().equals("")) {
															        		//l.getOnClick()
															        		%><a id="<%=id %>" class="td2PageHeader" href=""><%= l.getLabel() %></a>
															        			<script type="text/javascript" nonce="<%= randomNonce %>">
																					      	 $(document).ready(function(){
																									$('#<%=id %>').click(function() {
																										<%= Costanti.JS_FUNCTION_VISUALIZZA_AJAX_STATUS %><%= l.getOnClick() %>;return true;
																									});
																								});
																						</script>
															        		<%
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
