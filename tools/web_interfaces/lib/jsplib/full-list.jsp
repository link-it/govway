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



<%@page import="org.openspcoop2.web.lib.mvc.Dialog.BodyElement"%>
<%@ page session="true" import="java.util.*, org.openspcoop2.web.lib.mvc.*" %>

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
else
  iddati = "notdefined";
GeneralData gd = ServletUtils.getObjectFromSession(request, session, GeneralData.class, gdString);
PageData pd = ServletUtils.getObjectFromSession(request, session, PageData.class, pdString);
String randomNonce = (String) request.getAttribute(Costanti.REQUEST_ATTRIBUTE_CSP_RANDOM_NONCE);
String tabSessionKey = ServletUtils.getTabIdFromRequestAttribute(request);

String csrfTokenFromSession = ServletUtils.leggiTokenCSRF(request, session);
if(csrfTokenFromSession == null)
	csrfTokenFromSession = "";
%>

<td valign="top" class="td2PageBody">
<form name="form" method="post" id="form">
	<script type="text/javascript" nonce="<%= randomNonce %>">
	$(document).ready(function(){
		$("#form").submit(formSubmit);
	 });
	</script>
		<%
		if(!csrfTokenFromSession.equals("")){
			%>
			<input type="hidden" name="<%=Costanti.PARAMETRO_CSRF_TOKEN%>" id="<%=Costanti.PARAMETRO_CSRF_TOKEN%>"  value="<%= csrfTokenFromSession %>"/>
			<%			
		}
		%>
<%
Map<String,String> hidden = pd.getHidden();
if (hidden!=null && !hidden.isEmpty()) {
	for (String key : hidden.keySet()) {
	String value = (String) hidden.get(key);
	%><input type="hidden" name=<%= key %> value=<%= value %>><%
    }
}

%>


<jsp:include page="/jsplib/titlelist.jsp" flush="true">
	<jsp:param name="showListInfos" value="true"/>
</jsp:include>

<table class="tabella-ext">
	<!-- filtri di ricerca -->
	<jsp:include page="/jsplib/filtriRicerca.jsp" flush="true"/>
	
	<!-- spazio -->
	<tr> 
		<td valign=top>
			<div class="spacer"></div>
		</td>
	</tr>
	<!-- Riga tabella -->
		<tr> 
			<td valign=top>
				<div class="panelLista">
					<table class="tabella">
						<%
						String [] labels = pd.getLabels();
						int labelLength = labels != null ? labels.length : 0;
						
						Vector<?> v = pd.getDati();
						String stile= "";
						
						int index = pd.getIndex();
					  	if (pd.getNumEntries() > 0){
					    	index++;
						}
						String dataScrollerTopLabel = "Visualizzati record ["+index+"-"+(v.size()+pd.getIndex())+"] su " + pd.getNumEntries();
						
						if (pd.isPaginazione()){
						%>
					
						<!-- data scroller top -->
						<tr>
							<td colspan="<%= labelLength + 1%>">
								<div class="buttonrowdatascroller dsTop" align="center">
									<table>
										<tbody>
											<tr>
												<td>
													<%
													//Bottone Previous
													if (pd.getIndex() != 0) {
														%>							
														<img id="ds_prev_top" src="images/tema_link/go_prev.png" title="Precedente" alt="Precedente" class="dsImg" />
														<script type="text/javascript" nonce="<%= randomNonce %>">
															$(document).ready(function(){
																$('#ds_prev_top').click(function() {
																	<%= Costanti.JS_FUNCTION_VISUALIZZA_AJAX_STATUS %>PrevPage(document.form.limit.options[document.form.limit.selectedIndex].value);
																});
															});
														</script>
														<%
													} else{
														%>
														<img id="ds_prev_disabled_top" src="images/tema_link/go_prev_disabilitato.png" alt="Precedente">
														<%
													}
													%> 
												</td>
												<td>
													<span><%=dataScrollerTopLabel %></span>
												</td>
												<td>
													 <%
													
													//Bottone Next
													boolean nextTopDisabled = true;
													if (pd.getPageSize() != 0) {
													  if (pd.getIndex()+pd.getPageSize() < pd.getNumEntries()) {
														  nextTopDisabled = false;
													   			%>
													   			<img id="ds_next_top" src="images/tema_link/go_next.png" title="Successiva" alt="Successiva" class="dsImg"/>
													   			<script type="text/javascript" nonce="<%= randomNonce %>">
																	$(document).ready(function(){
																		$('#ds_next_top').click(function() {
																			<%= Costanti.JS_FUNCTION_VISUALIZZA_AJAX_STATUS %>NextPage();
																		});
																	});
																</script>
													   			<%
													  }
													}
													
													if (nextTopDisabled) {
														%>
															<img id="ds_next_disabled_top" src="images/tema_link/go_next_disabilitato.png" alt="Successiva" />
														<%
													}
													 %>
										 		</td>
											</tr>									
										</tbody>
									</table>
								</div>
							</td>
						</tr>
						<%
						}
						%>
					
						<tr class="tableHeader">
							<%
							if (v.size()> 0 && pd.getSelect()) {
							  %>
							  <td class="tableHeaderChkAll">
							  	<div align="center">
							  		<input id="chkAll" type="checkbox" name="chkAll"/>
							  		<script type="text/javascript" nonce="<%= randomNonce %>">
										$(document).ready(function(){
											$('#chkAll').click(function() {
												checkAll();
											});
										});
									</script> 
							  	</div>
							  </td>
							  <%
							}
							%>
					
							<%
							
							if(labels!=null){
								for (int i = 0; i < labelLength ;i++) {
								  %><td><span><%= labels[i] %></span></td><%
								}
							}
							%>
					
						</tr>
					<%
					//inizio entries
					
	
					for (int i = 0; i < v.size(); i++) {
						//per ogni entry:
						if ((i % 2) != 0) {
					    	stile = "odd";
					  	} else {
						    stile = "even";
					  	}
						%><tr class="<%= stile %>"><%
							//stringa contenente l identificativo dell'elemento che verra associato al checkbox per l eliminazione
							String idToRemove = null;	  
						  	// prelevo la riga 
						  	Vector<?> e = (Vector<?>) v.elementAt(i);	  
						
						  	for (int j = 0; j < e.size(); j++) {
						  	    DataElement de = (DataElement) e.elementAt(j);
								if(de.getType().equals("text")) {
									// setto l'id per la rimozione in caso non sia settato, N.B. spostato qui perche ora le checkbox statnno a sx.
									if(idToRemove==null) idToRemove = de.getIdToRemove();
								}
						  	}
						  	
							if (pd.getSelect()) {
							   %>
								<td class="tdText">
							   		<div align="center">
							   			<input id='_<% if(idToRemove!=null) out.write(idToRemove);else out.write(""+i); %>' type="checkbox" name="selectcheckbox" value='<% if(idToRemove!=null) out.write(idToRemove);else out.write(""+i); %>'/>
							   		</div>
							   	</td><%
							 }
								  
							for (int j = 0; j < e.size(); j++) {
							    DataElement de = (DataElement) e.elementAt(j);
							    String deName = !de.getName().equals("") ? de.getName() : "de_name_"+j;
							    String classLink = "";
							    String classSpan = de.getLabelStyleClass();
							    String cssClassTdStyle = !de.getStyle().equals("") ? "tdStyle-"+i+"-"+j : ""; 
							    // se e' un elemento visualizzabile inserisco una cella
							    if (!de.getType().equals("hidden")) {
							      %><td class="tdText <%=cssClassTdStyle %>">
							      	<% if(!de.getStyle().equals("")){ %>
										<style type="text/css" nonce="<%= randomNonce %>">
											.<%=cssClassTdStyle %> {
												<%= de.getStyle() %>
											}
										</style>
									<% }%>
							      
							      <%
							    }
					
								if (de.getType().equals("text")) {
									String visualizzaAjaxStatus = de.isShowAjaxStatus() ? Costanti.JS_FUNCTION_VISUALIZZA_AJAX_STATUS : "";
									String id = "tabella-text-link_" + i;
						    		// tipo link
					      			if (!de.getUrl().equals("")) {
							    		//tooltip
								  		String tip = "";
								  		boolean showTip=false;
								  		if(de.getToolTip()!=null && !"".equals(de.getToolTip())){
								  			tip=de.getToolTip();
								  			showTip=true;
								  		}
								  		//se la lunghezza del dato impostato come value e' > della larghezza della
								  		//colonna allora accorcio il dato ed imposto il tooltip
								  		//15 e' il valore di default impostato nel dataelement
								  		// [TODO] questa dimensione e' da rivedere
								  		int size=de.getSize();
								  		String res=de.getValue();
								  		if(size>15 && de.getValue().trim().length()>size){
								  			res=de.getValue().trim().substring(0,size)+" ...";
								  			//se nn e' stato specificato un tip precedentemente
								  			//metto come tip il valore completo del campo
								  			if(!showTip) tip=de.getValue();
								  			
								  			showTip=true;
								  		}
								  		
								  		String deTarget = " ";
								  		if (!de.getTarget().equals("")) {
								  			deTarget = " target=\""+ de.getTarget() +"\"";
								  		}
								  		
								  		String deTip = " ";
								  		if(showTip){
								  			deTip = " title=\"" + tip + "\"";
								  		}
								  		
								  		if (!de.getUrl().equals("")) {
											de.addParameter(new Parameter(Costanti.PARAMETER_PREV_TAB_KEY, tabSessionKey));
										}
								  		
								  		if(de.isVisualizzaLinkApriNuovaFinestra()) { // stringa senza link e icona sulla dx
								  			String deIconName = de.getIcon(); 
								  			if(de.getToolTip()!=null && !"".equals(de.getToolTip())){
								  				tip=de.getToolTip();
								  				deTip = " title=\"" + tip + "\"";
								  			}
								  			%><span class="<%= classSpan %>" ><%= de.getValue() %></span>
								  				
								  				<a id="<%=id %>" class="edit-link <%= classLink %>" <%= deTip %> <%=deTarget %> href="<%= de.getUrl() %>" type="button">
			                						<span class="icon-box">
														<i class="material-icons md-18"><%= deIconName %></i>
													</span>
			                					</a>
			                					<script type="text/javascript" nonce="<%= randomNonce %>">
											      $(document).ready(function(){
														$('#<%=id %>').click(function() {
															<%= visualizzaAjaxStatus %>return true;
														});
													});
											  </script>
								  			<%
								  		} else { // visualizzazione normale
								  			%><a id="<%=id %>" class="<%= classLink %>" <%= deTip %> <%=deTarget %> href="<%= de.getUrl() %>"><%= res %></a>
								  			<script type="text/javascript" nonce="<%= randomNonce %>">
											      $(document).ready(function(){
														$('#<%=id %>').click(function() {
															<%= visualizzaAjaxStatus %>return true;
														});
													});
											  </script>
											<%
								  		}
							      	} else {
										//no url
										if (!de.getOnClick().equals("")) {
											String tip = "";
											String deTip = " ";
									  		if(de.getToolTip()!=null && !"".equals(de.getToolTip())){
								  				tip=de.getToolTip();
								  				deTip = " title=\"" + tip + "\"";
								  			}
										  //getOnClick
										  %><span id="<%=id %>" class="<%= classSpan %>" <%= deTip %>><%= de.getValue() %></span>
										  	<script type="text/javascript" nonce="<%= randomNonce %>">
											      $(document).ready(function(){
														$('#<%=id %>').click(function() {
															<%= visualizzaAjaxStatus %><%= de.getOnClick() %>; return false;
														});
													});
											  </script>
											<%
										} else {
											String tip = "";
											String deTip = " ";
									  		if(de.getToolTip()!=null && !"".equals(de.getToolTip())){
								  				tip=de.getToolTip();
								  				deTip = " title=\"" + tip + "\"";
								  			}
										  //string only
										  %><span class="<%= classSpan %>" <%= deTip %> ><%= de.getValue() %></span><%
										}
							      	}
								} else { 
								      // Tipo hidden
								      if (de.getType().equals("hidden")) {
										%><input type="hidden" name="<%= deName %>" value="<%= de.getValue() %>" /><%
								      } else {
										// Tipo image
										if (de.getType().equals("image")) {
											
											if(!de.getImage().isEmpty()){
												for(int idxLink =0; idxLink < de.getImage().size() ; idxLink ++ ){
													DataElementImage image = de.getImage().get(idxLink);
													String deIconName = image.getImage(); 
		                					
													String deTip = !image.getToolTip().equals("") ? " title=\"" + image.getToolTip() + "\"" : "";
		                							
		                							String deTarget = " ";
											  		if (!image.getTarget().equals("")) {
											  			deTarget = " target=\""+ image.getTarget() +"\"";
											  		}
										  			
											  		if (!image.getUrl().equals("")) {
											  			image.addParameter(new Parameter(Costanti.PARAMETER_PREV_TAB_KEY, tabSessionKey));
													}
											  		String deUrl = !image.getUrl().equals("") ? image.getUrl() : "";
											  		String deOnClick = !image.getOnClick().equals("") ? image.getOnClick() : "";
											  		
											  		String visualizzaAjaxStatus = image.isShowAjaxStatus() ? Costanti.JS_FUNCTION_VISUALIZZA_AJAX_STATUS : "";
											  		
											  		String id = "tabella-image-link_" + i;
											  		if(!deUrl.equals("")){ // Url definita
				                					%>
					                					<a id="<%=id %>" class="image-link <%= classLink %>" <%= deTip %> <%=deTarget %> href="<%= image.getUrl() %>" type="button">
					                						<span class="icon-box">
																<i class="material-icons md-18"><%= deIconName %></i>
															</span>
					                					</a>
					                					<script type="text/javascript" nonce="<%= randomNonce %>">
														      $(document).ready(function(){
																	$('#<%=id %>').click(function() {
																		<%= visualizzaAjaxStatus %>return true;
																	});
																});
														  </script>
					                				<%
											  		} else if (!deOnClick.equals("")){ // Se e' definito 'OnClick' 
											  			%>
					                					<a id="<%=id %>" class="image-link <%= classLink %>" <%= deTip %> <%=deTarget %> href="" type="button">
					                						<span class="icon-box">
																<i class="material-icons md-18"><%= deIconName %></i>
															</span>
					                					</a>
					                					<script type="text/javascript" nonce="<%= randomNonce %>">
														      $(document).ready(function(){
																	$('#<%=id %>').click(function() {
																		<%= visualizzaAjaxStatus %><%= deOnClick %>; return false;
																	});
																});
														  </script>
					                				<%
											  		} else { // Solo immagine
											  			%>
				                						<span class="icon-box" <%= deTip %> >
															<i class="material-icons md-18"><%= deIconName %></i>
														</span>
					                				<%
											  		}
												}// end for-edit-link
											} // end edit-link
										} else {
								        	  if (de.getType().equals("radio")) {
										   		String[] stValues = de.getValues();
									    		String[] stLabels = de.getLabels();
									
											    // Ciclo sulla lista di valori 
											    for (int r = 0; r < stValues.length; r++) {
											      if (stValues[r].equals(de.getSelected())) {
										                 %><input type="radio" checked name='<%= deName %>' value='<%= stValues[r] %>'>&nbsp;&nbsp;<%= stLabels[r] %><%
											      } else {
										                 %><input type="radio" name='<%= deName %>' value='<%= stValues[r] %>'>&nbsp;&nbsp;<%= stLabels[r] %><%
										              }
										    	  if (r<stValues.length-1) {
													%><br/><%
									              }
										    	}
									  		} else { 
									  			 if (de.getType().equals("checkbox")) {
												 	String image = "status_red.png";
												 	if("yes".equals(de.getSelected())){
														image = "status_green.png";
													}
													else if("warn".equals(de.getSelected())){
														image = "status_yellow.png";
													}
													else if("off".equals(de.getSelected())){
														image = "disconnected_grey.png";
													}
													else if("config_enable".equals(de.getSelected())){
														image = "verified_green.png";
													}
													else if("config_warning".equals(de.getSelected())){
														image = "verified_yellow.png";
													}
													else if("config_error".equals(de.getSelected())){
														image = "verified_red.png";
													}
												 	else if("config_disable".equals(de.getSelected())){
												 		image = "verified_grey.png";
													}
									  				String tooltip = !de.getToolTip().equals("") ? " title=\"" + de.getToolTip() + "\"" : ""; 
									  				String altImg = !de.getToolTip().equals("") ? de.getToolTip() : "-"; 
									  				 
									  				String visualizzaAjaxStatus = de.isShowAjaxStatus() ? Costanti.JS_FUNCTION_VISUALIZZA_AJAX_STATUS : "";
									  				String id = "tabella-checkbox-link_" + i;
									  				// tipo link
										      		if (!de.getUrl().equals("")) { 
									      				de.addParameter(new Parameter(Costanti.PARAMETER_PREV_TAB_KEY, tabSessionKey));
										      			
										      			String deTarget = " ";
												  		if (!de.getTarget().equals("")) {
												  			deTarget = " target=\""+ de.getTarget() +"\"";
												  		} 
												  		%><div class="text-align-center">
												  			<a id="<%=id %>" class="<%= classLink %>" <%=deTarget %> href="<%= de.getUrl() %>">
												  				<img src="images/tema_link/<%= image %>" <%= tooltip %> alt="<%= altImg %>"/>
															</a>
															<script type="text/javascript" nonce="<%= randomNonce %>">
															      $(document).ready(function(){
																		$('#<%=id %>').click(function() {
																			<%= visualizzaAjaxStatus %>return true;
																		});
																	});
															  </script>
														</div><%
										      			
										      		}else {
									  				 	%><div class="text-align-center"><img src="images/tema_link/<%= image %>" <%= tooltip %> alt="<%= altImg %>"/>&nbsp;</div><%
										      		}
									  			 } else {
									  				 if (de.getType().equals("button")) {
									  					String deTip = !de.getToolTip().equals("") ? " title=\"" + de.getToolTip() + "\"" : "";
														
									  					if(de.getInfo() != null) {
															DataElementInfo deInfo = de.getInfo();
															String idDivIconInfo = "divIconInfo_"+i;
															String idIconInfo = "iconInfo_"+i; 
															String idSpanInfo = "spanIconInfoBoxList_"+i;
															
															%>
															<div class="iconInfoBoxList" id="<%=idDivIconInfo %>" <%=deTip %> >
																<input type="hidden" name="__i_hidden_title_<%= idIconInfo %>" id="hidden_title_<%= idIconInfo %>"  value="<%= deInfo.getHeaderFinestraModale() %>"/>
											   					<input type="hidden" name="__i_hidden_body_<%= idIconInfo %>" id="hidden_body_<%= idIconInfo %>"  value="<%= deInfo.getBody() %>"/>
									       						<span class="spanIconInfoBoxList" id="<%=idSpanInfo %>">
																	<i class="material-icons md-18" id="<%=idIconInfo %>"><%= deInfo.getButtonIcon() %></i>
																</span>
									       					</div>
															<script type="text/javascript" nonce="<%= randomNonce %>">
															// info
													    	if($("#<%=idSpanInfo %>").length>0){
													    		$("#<%=idSpanInfo %>").click(function(e){
													    			var iconInfoBoxId = $(this).parent().prop('id');
													    			var idx = iconInfoBoxId.substring(iconInfoBoxId.indexOf("_")+1);
													    			console.log(idx);
													    			if(idx) {
													    				var label = $("#hidden_title_iconInfo_"+ idx).val();
																		var body = $("#hidden_body_iconInfo_"+ idx).val();
																		mostraDataElementInfoModal(label,body);
													    			}
													    			e.stopPropagation();
																});
													    	}
															</script>
															
															<%						
														}
									  					
									  					if(de.getDialog() != null) {
															Dialog dialog = de.getDialog();
															String idDivIconUso = "divIconUso_"+i;
															String idIconUso = "iconUso_"+i; 
															String idSpanUso = "spanIconUsoBoxList_"+i;
															
															BodyElement urlElement = dialog.getUrlElement();
															
															request.setAttribute("idFinestraModale_"+i, de.getDialog());
															
															String identificativoFinestraModale = "idFinestraModale_" + i;
															%>
															<div class="iconUsoBoxList" id="<%=idDivIconUso %>" <%=deTip %> >
																<input type="hidden" name="__i_hidden_title_<%= idIconUso %>" id="hidden_title_<%= idIconUso %>"  value="<%= urlElement.getUrl() %>"/>
																<span class="spanIconUsoBoxList" id="<%=idSpanUso %>">
																	<i class="material-icons md-18" id="<%=idIconUso %>"><%= dialog.getIcona() %></i>
																</span>
									       					</div>
															<jsp:include page="/jsplib/info-uso-modal.jsp" flush="true">
																<jsp:param name="idFinestraModale" value="<%=identificativoFinestraModale %>"/>
															</jsp:include>
															<script type="text/javascript" nonce="<%= randomNonce %>">
															// info
													    	if($("#<%=idSpanUso %>").length>0){
													    		$("#<%=idSpanUso %>").click(function(e){
													    			var iconInfoBoxId = $(this).parent().prop('id');
													    			var idx = iconInfoBoxId.substring(iconInfoBoxId.indexOf("_")+1);
													    			console.log(idx);
													    			if(idx) {
													    				var url = $("#hidden_title_iconUso_"+ idx).val();
													    				
													    				//addTabID
													    				url = addTabIdParam(url,true);
													    				// chiamata al servizio
													    				<%=Costanti.JS_FUNCTION_VISUALIZZA_AJAX_STATUS %>
													    				
													    				$.ajax({
													    							url : url,
													    							method: 'GET',
													    							async : false,
													    							success: function(data, textStatus, jqXHR){
													    								// inserimento del valore nella text area
																	    				$("textarea[id^='idFinestraModale_<%=i %>_txtA']").val(data);
																	    				
																	    				<%=Costanti.JS_FUNCTION_NASCONDI_AJAX_STATUS %>
																	    				// apertura modale
																	    				var idToOpen = '#' + 'idFinestraModale_<%= i %>';
																	    				$(idToOpen).dialog("open");
													    							},
													    							error: function(data, textStatus, jqXHR){
													    								<%=Costanti.JS_FUNCTION_NASCONDI_AJAX_STATUS %>
													    							}
													    						}
													    					);
													    			}
													    			e.stopPropagation();
																});
													    	}
															</script>
															<%	
														}
									  				 } else {
									  					 
									  				 } // end button
									  			 } // end checkbox
									  		} // end else radio
										} // end else image
						      		} // end else hidden
						    	} // end else text
					
							    if (!de.getType().equals("hidden")) {
							      %></td><%
							    }
					 		 }
					 	 %></tr><%
					}
					
					//fine entries
					
					//navigazione tra la pagine visualizzata se il numero di entries > 20
					if (pd.isPaginazione() && pd.getNumEntries() > 20){
						%>
						<tr>
							<td colspan="<%= labelLength + 1 %>">
								<div class="buttonrowdatascroller dsBottom" align="center" >
									<table>
										<tbody>
											<tr>
											<td>
										<%
										//Bottone Previous
										if (pd.getIndex() != 0) {
											%>							
											<img id="ds_prev_bottom" src="images/tema_link/go_prev.png" title="Precedente" alt="Precedente" class="dsImg" />
											<script type="text/javascript" nonce="<%= randomNonce %>">
												$(document).ready(function(){
													$('#ds_prev_bottom').click(function() {
														<%= Costanti.JS_FUNCTION_VISUALIZZA_AJAX_STATUS %>PrevPage(document.form.limit.options[document.form.limit.selectedIndex].value);
													});
												});
											</script>
											<%
										} else{
											%>
											<img id="ds_prev_disabled_bottom" src="images/tema_link/go_prev_disabilitato.png" alt="Precedente">
											<%
										}
	
										//Scelta numero di entries da visualizzare
										if ((pd.getNumEntries() > 20) || (pd.getIndex() != 0)) {
										  %></td>
											<td><select id="ds_limit_bottom" name="limit"><%
										  switch (pd.getPageSize()) {
										    case 20 :
											%>
											<option value="20" selected="selected">20 Entries</option>
											<option value="75">75 Entries</option>
											<option value="125">125 Entries</option>
											<option value="250">250 Entries</option>
											<option value="500">500 Entries</option>
											<option value="1000">1000 Entries</option>
											<%
											break;
										    case 75 :
											%>
											<option value="20">20 Entries</option>
											<option value="75" selected="selected">75 Entries</option>
											<option value="125">125 Entries</option>
											<option value="250">250 Entries</option>
											<option value="500">500 Entries</option>
											<option value="1000">1000 Entries</option>
											<%
											break;
										    case 125 :
											%><option value="20">20 Entries</option>
											<option value="75">75 Entries</option>
											<option value="125" selected="selected">125 Entries</option>
											<option value="250">250 Entries</option>
											<option value="500">500 Entries</option>
											<option value="1000">1000 Entries</option><%
											break;
										    case 250 :
											%>
											<option value="20">20 Entries</option>
											<option value="75">75 Entries</option>
											<option value="125">125 Entries</option>
											<option value="250" selected="selected">250 Entries</option>
											<option value="500">500 Entries</option>
											<option value="1000">1000 Entries</option>
											<%
											break;
										    case 500 :
										    	%>
										    	<option value="20">20 Entries</option>
										    	<option value="75">75 Entries</option>
										    	<option value="125">125 Entries</option>
										    	<option value="250">250 Entries</option>
										    	<option value="500" selected="selected">500 Entries</option>
										    	<option value="1000">1000 Entries</option>
										    	<%
										    	break;
										    case 1000 :
										    	%>
										    	<option value="20">20 Entries</option>
										    	<option value="75">75 Entries</option>
										    	<option value="125">125 Entries</option>
										    	<option value="250">250 Entries</option>
										    	<option value="500">500 Entries</option>
										    	<option value="1000" selected="selected">1000 Entries</option>
										    	<%
										    	break;
										  }
										  %></select>
										  	<script type="text/javascript" nonce="<%= randomNonce %>">
													$(document).ready(function(){
														$('#ds_limit_bottom').change(function() {
															<%= Costanti.JS_FUNCTION_VISUALIZZA_AJAX_STATUS %>CambiaVisualizzazione(document.form.limit.options[this.selectedIndex].value);
														});
													});
												</script>
										  </td>
											<td><%
										} else {
											%></td>
											<td>&nbsp;</td>
											<td><%
										}
										//Bottone Next
										boolean nextBottomDisabled = true;
										if (pd.getPageSize() != 0) {
										  if (pd.getIndex()+pd.getPageSize() < pd.getNumEntries()) {
											  nextBottomDisabled = false;
										   			%>
										   			<img id="ds_next_bottom" src="images/tema_link/go_next.png" title="Successiva" alt="Successiva" class="dsImg"/>
										   			<script type="text/javascript" nonce="<%= randomNonce %>">
														$(document).ready(function(){
															$('#ds_next_bottom').click(function() {
																<%= Costanti.JS_FUNCTION_VISUALIZZA_AJAX_STATUS %>NextPage();
															});
														});
													</script>
										   			<%
										  }
										}
										
										if (nextBottomDisabled) {
											%>
												<img id="ds_next_disabled_bottom" src="images/tema_link/go_next_disabilitato.png" alt="Successiva" />
											<%
										}
										 %>
										 </td>
											</tr>									
										</tbody>
									</table>
										</div>
								</td>
							</tr>
						
						<%
					
					}
					
					//Bottoni 
					if (pd.getSelect()) {
						%>
						<tr>
							<td colspan=<%= labelLength + 1 %> class="buttonrow">
								<div class="buttonrowlista">
									<%
									Vector<?> areaBottoni = pd.getAreaBottoni();
									if (areaBottoni != null) {
										for (int i = 0; i < areaBottoni.size(); i++) {
									  		AreaBottoni area = (AreaBottoni) areaBottoni.elementAt(i);
									  		Vector<?> bottoni = area.getBottoni();
									 		for (int b = 0; b < bottoni.size(); b++) {
									 			String id = "areaBottonBtn_" + i + "_" + b;
									   			DataElement bottone = (DataElement) bottoni.elementAt(b);
									   			String visualizzaAjaxStatus = bottone.isShowAjaxStatus() ? Costanti.JS_FUNCTION_VISUALIZZA_AJAX_STATUS : "";
									   			%>
									   			<input id="<%=id %>" type="button" value='<%= bottone.getValue() %>'/>
									   			<script type="text/javascript" nonce="<%= randomNonce %>">
											      	 $(document).ready(function(){
															$('#<%=id %>').click(function() {
																<%= visualizzaAjaxStatus %><%= bottone.getOnClick() %>;
															});
														});
												</script>
									   			<%
									 		}
										}
									}
					
									//Bottone di Remove
									if (v.size() > 0 && pd.getRemoveButton()) {
									  %><input id='rem_btn' type="button" value='Elimina' class="negative" /><%
									}
									
									//Bottone di Add
									if (pd.getAddButton()) {
									  %><input id="aggiungiBtn" type="button" value='Aggiungi' />
									  	<script type="text/javascript" nonce="<%= randomNonce %>">
									      	 $(document).ready(function(){
									      		$('#aggiungiBtn').click(function() {
														<%= Costanti.JS_FUNCTION_VISUALIZZA_AJAX_STATUS %>AddEntry();
													});
												});
										</script>
									  <%
									}
									
									%>
								</div>
							</td>
						</tr>
						<% 
					} else if(pd.getAddButton()) {
						%>
						<tr>
							<td colspan=<%= labelLength + 1 %> class="buttonrow">
								<div class="buttonrowlista">
									<input id="aggiungiBtn" type="button" value='Aggiungi' />
									<script type="text/javascript" nonce="<%= randomNonce %>">
								      	 $(document).ready(function(){
								      		$('#aggiungiBtn').click(function() {
													<%= Costanti.JS_FUNCTION_VISUALIZZA_AJAX_STATUS %>AddEntry();
												});
											});
									</script>
								</div>
							</td>
						</tr>
						<%
					} else{
						%>
						 <tr class="buttonrownobuttons">
						  <td colspan="<%= labelLength + 1 %>">&nbsp;</td>
					  	</tr>
					  	<%
					}
					%>
					</table>
				</div>
			</td>
		</tr>
		</table>
	</form>
</td>
