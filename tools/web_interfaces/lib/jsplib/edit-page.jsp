<%--
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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
<%@page import="java.util.List"%>
<%@page import="org.openspcoop2.web.lib.mvc.DataElement.STATO_APERTURA_SEZIONI"%>
<%@page import="org.openspcoop2.utils.crypt.PasswordGenerator"%>
<%@ page session="true" import="java.util.List, org.apache.commons.lang.StringEscapeUtils ,org.openspcoop2.web.lib.mvc.*" %>

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
%>

<%
String vBL = request.getParameter("visualizzaBottoneLogin");
boolean visualizzaBottoneLogin = false;

if(vBL != null){
	visualizzaBottoneLogin = Boolean.parseBoolean(vBL);
}


boolean mime = false;

List<?> dati = pd.getDati();
for (int i = 0; i < dati.size(); i++) {
  DataElement de = (DataElement) dati.get(i);
  if (de.getType().equals("file") || de.getType().equals("multi-file")) {
    mime = true;
  }
}
String encTypeS = "";
if (mime) {
	encTypeS = "ENCTYPE=\"multipart/form-data\"";
}

String csrfTokenFromSession = ServletUtils.leggiTokenCSRF(request, session);
if(csrfTokenFromSession == null)
	csrfTokenFromSession = "";

%>


<td valign="top" class="td2PageBody">
	<form name="form" <%=encTypeS %> action="<%= gd.getUrl() %>" method="post">
		<!-- Breadcrumbs -->
		<jsp:include page="/jsplib/titlelist.jsp" flush="true" />

		<%
		if(!csrfTokenFromSession.equals("")){
			%>
			<input type="hidden" name="<%=Costanti.PARAMETRO_CSRF_TOKEN%>" id="<%=Costanti.PARAMETRO_CSRF_TOKEN%>"  value="<%= csrfTokenFromSession %>"/>
			<%			
		}
		%>
		<input type="hidden" name="__i_hidden_lockurl_" id="__i_hidden_lockurl_"  value=""/>
		<input type="hidden" name="__i_hidden_lockvalue_" id="__i_hidden_lockvalue_"  value=""/>
<%
boolean elementsRequired = false;
boolean elementsRequiredEnabled = true;
if (pd.getMode().equals(Costanti.DATA_ELEMENT_EDIT_MODE_DISABLE_NAME)) {
	elementsRequiredEnabled = false;
}

boolean visualizzaPanelLista = visualizzaBottoneLogin;

if(!visualizzaPanelLista)
	visualizzaPanelLista = !pd.isPageBodyEmpty();
String classDivPanelLista = visualizzaPanelLista  ? "panelLista" : "";
String classTabellaPanelLista = visualizzaPanelLista  ? "tabella" : "";

List<GeneralLink> titlelist = pd.getTitleList();
String titoloSezione = null;
if (titlelist != null && titlelist.size() > 0) {
	GeneralLink l = titlelist.get(titlelist.size() -1);
	titoloSezione = l.getLabel();
	
	if(titoloSezione != null && titoloSezione.equals(Costanti.PAGE_DATA_TITLE_LABEL_AGGIUNGI))
		titoloSezione = null;
} 
List<DataElement> listaComandi = pd.getComandiAzioneBarraTitoloDettaglioElemento();
boolean mostraComandiHeader = listaComandi != null && listaComandi.size() > 0;
int colFormHeader = (mostraComandiHeader ? 2 : 1);
String classPanelTitolo = "panelDettaglioNoForm";
String numeroEntry = "dettaglio";
String tabSessionKey = ServletUtils.getTabIdFromRequestAttribute(request);
%>


<table class="tabella-ext">
	<% if(titoloSezione != null) { %>
	<tr>
		<td valign=top colspan="2">
			<div class="<%= classPanelTitolo %>" >
				<table class="tabella" id="panelDettaglioHeader">
					<tbody>
						<tr>	
							<td class="titoloSezione" id="dettaglioFormHeader" colspan="<%= colFormHeader %>">
								<span class="history"><%= titoloSezione %></span>
							</td>
							<% if(mostraComandiHeader) { %>
								<jsp:include page="/jsplib/comandi-header.jsp" flush="true" />
							<% } %>
						</tr>
					</tbody>
				</table>
			</div>
		</td>
	</tr>
	<% }%>
	<!-- Riga tabella -->
		<tr> 
			<td valign=top colspan="2">		
			<div class="<%=classDivPanelLista %>">
				<table class="<%=classTabellaPanelLista %>">

<%



if(elementsRequiredEnabled){
	for (int i = 0; i < dati.size(); i++) {
	  DataElement de = (DataElement) dati.get(i);
	  if(de.isRequired()){
	     elementsRequired=true;
	     break;
	  }
	}
	if(elementsRequired){
		// inserisco la riga con le note
		%>
		<tr class="even" name="row_CampiObbligatori">
			<td class="campiObbligatori" colspan="2">
				<p class="legend">
					<strong>Note: </strong>(<em>*</em>) Campi obbligatori
				</p>
			</td>
			<td class="tdInput">&nbsp;</td>
		</tr>
		<%
	}
}

%><tr class="even">
	<td colspan="2">
<%


String classSpanNoEdit="spanNoEdit";
String classDivNoEdit="divNoEdit";
boolean fieldsetOpen = false;
boolean subtitleOpen = false;
for (int i = 0; i < dati.size(); i++) {
	DataElement de = (DataElement) dati.get(i);
  
	String deName = !de.getName().equals("") ? de.getName() : "de_name_"+i;
  	String type = de.getType();
  	String rowName="row_"+deName;
  	String deLabel = !de.getLabel(elementsRequiredEnabled).equals("") ? de.getLabel(elementsRequiredEnabled) : "&nbsp;";
  	String deLabelId = "de_label_"+i;
  	String deNote = de.getNote();
  	String classInput= de.getStyleClass();
  	String labelStyleClass= de.getLabelStyleClass();
  	DataElementInfo deInfo = de.getInfo();
	
    	if (type.equals("hidden")) {
    		%><input type="hidden" name="<%= deName  %>" value="<%= de.getValue()  %>"/><%
    	} else { // else hidden
    		if (type.equals("title")){
    			// gestion apertura e chiusura.
    			STATO_APERTURA_SEZIONI statoAperturaSezione = de.getStatoSottosezione();
    			boolean gestioneAperturaFieldset = !STATO_APERTURA_SEZIONI.DISABILITATO.equals(statoAperturaSezione);
    			String titleDivId = deName + "__id";
    			
    			String cssClassTitle = "navigatorAnchor";
    			String titoloComandoApertura = "";
    			String cssClassLegend = "";
    			String cssClassFieldset = "";
    			if(gestioneAperturaFieldset) {
    				titoloComandoApertura = " title=\""+ Costanti.TOOLTIP_VISUALIZZA_FIELDSET +"\"";
    				cssClassTitle = "navigatorAnchorClosable";
    				cssClassLegend = "navigatorAnchorClosable";
    				
    				cssClassFieldset = "fieldsetCollapsed";
    				if(de.isVisualizzaSezioneAperta()){
    					cssClassFieldset = "";
    				}
    			}
    			
    			if(subtitleOpen){
    				%>
    				</div>
        			<%
        			subtitleOpen = false;
    			}
    			
    			// se c'e' un altro field set aperto viene chiuso
    			if(fieldsetOpen){
    				%>
    					</div>
    				</fieldset>
        			<%
        			fieldsetOpen = false;
    			}
    			if(!fieldsetOpen){
	    			%>
	    				<fieldset id="<%= deName  %>__fieldset" class="<%=cssClassFieldset %>">
	    					<legend class="<%=cssClassLegend %>">
	    						<%
	    							if(gestioneAperturaFieldset){
	    						%>
	    							<span class="<%=cssClassTitle %>">
	    								<i class="material-icons md-16" id="<%= deName  %>__icon" title="<%= Costanti.TOOLTIP_VISUALIZZA_SEZIONE_FILTRI_RICERCA%>"><%= Costanti.ICON_VISUALIZZA_SEZIONE_FILTRI_RICERCA%></i>
	    							</span>
	    						<%
	    							}
	    						%>
	    						<a id="<%= deName  %>__anchor" name="<%=rowName %>" class="<%=cssClassTitle %>" <%=titoloComandoApertura %>><%=deLabel %></a>
	    					</legend>
	    						<%
	    							if(gestioneAperturaFieldset){
	    						%>
	    							<script type="text/javascript" nonce="<%= randomNonce %>">
			        					$(document).ready(function() {
       									<%
       										boolean sub = de.isVisualizzaSezioneAperta();
							      		%>
							      		var subtitle_<%= deName  %>_aperto = <%=sub %>; 
							      		
							      		if(subtitle_<%= deName  %>_aperto){
						      				$("#<%= titleDivId  %>").show();
						      				$("#<%= deName  %>__anchor").attr('title', '<%= Costanti.TOOLTIP_NASCONDI_FIELDSET%>');
						      				$("#<%= deName  %>__icon").html('<%= Costanti.ICON_NASCONDI_FIELDSET%>');
						      				$("#<%= deName  %>__icon").attr('title', '<%= Costanti.TOOLTIP_NASCONDI_FIELDSET%>');
						      				$("#<%= deName  %>__fieldset").removeClass('fieldsetCollapsed');
						      			} else {
						      				$("#<%= titleDivId  %>").hide();
						      				$("#<%= deName  %>__anchor").attr('title', '<%= Costanti.TOOLTIP_VISUALIZZA_FIELDSET%>');
						      				$("#<%= deName  %>__icon").html('<%= Costanti.ICON_VISUALIZZA_FIELDSET%>');
						      				$("#<%= deName  %>__icon").attr('title', '<%= Costanti.TOOLTIP_VISUALIZZA_FIELDSET%>');
						      				$("#<%= deName  %>__fieldset").addClass('fieldsetCollapsed');
						      			}
							      		
							      		$("#<%= deName  %>__anchor").click(function(){
							      			subtitle_<%= deName  %>_aperto = !subtitle_<%= deName  %>_aperto;
							      			
							      			if(subtitle_<%= deName  %>_aperto){
							      				$("#<%= titleDivId  %>").show();
							      				$("#<%= deName  %>__anchor").attr('title', '<%= Costanti.TOOLTIP_NASCONDI_FIELDSET%>');
							      				$("#<%= deName  %>__icon").html('<%= Costanti.ICON_NASCONDI_FIELDSET%>');
							      				$("#<%= deName  %>__icon").attr('title', '<%= Costanti.TOOLTIP_NASCONDI_FIELDSET%>');
							      				inizializzaSelectSezione('<%= titleDivId  %>');
							      				$("#<%= deName  %>__fieldset").removeClass('fieldsetCollapsed');
							      			} else {
							      				$("#<%= titleDivId  %>").hide();
							      				$("#<%= deName  %>__anchor").attr('title', '<%= Costanti.TOOLTIP_VISUALIZZA_FIELDSET%>');
							      				$("#<%= deName  %>__icon").html('<%= Costanti.ICON_VISUALIZZA_FIELDSET%>');
							      				$("#<%= deName  %>__icon").attr('title', '<%= Costanti.TOOLTIP_VISUALIZZA_FIELDSET%>');
							      				$("#<%= deName  %>__fieldset").addClass('fieldsetCollapsed');
							      			}
							      		});
							      		
							      		$("#<%= deName  %>__icon").click(function(){
							      			subtitle_<%= deName  %>_aperto = !subtitle_<%= deName  %>_aperto;
							      			
							      			if(subtitle_<%= deName  %>_aperto){
							      				$("#<%= titleDivId  %>").show();
							      				$("#<%= deName  %>__anchor").attr('title', '<%= Costanti.TOOLTIP_NASCONDI_FIELDSET%>');
							      				$("#<%= deName  %>__icon").html('<%= Costanti.ICON_NASCONDI_FIELDSET%>');
							      				$("#<%= deName  %>__icon").attr('title', '<%= Costanti.TOOLTIP_NASCONDI_FIELDSET%>');
							      				inizializzaSelectSezione('<%= titleDivId  %>');
							      				$("#<%= deName  %>__fieldset").removeClass('fieldsetCollapsed');
							      			} else {
							      				$("#<%= titleDivId  %>").hide();
							      				$("#<%= deName  %>__anchor").attr('title', '<%= Costanti.TOOLTIP_VISUALIZZA_FIELDSET%>');
							      				$("#<%= deName  %>__icon").html('<%= Costanti.ICON_VISUALIZZA_FIELDSET%>');
							      				$("#<%= deName  %>__icon").attr('title', '<%= Costanti.TOOLTIP_VISUALIZZA_FIELDSET%>');
							      				$("#<%= deName  %>__fieldset").addClass('fieldsetCollapsed');
							      			}
							      		});
        										});
			        				</script>
	    						<%
	    							}
	    						%>
	    					<div id="<%= titleDivId  %>">
	    			<%
	    			fieldsetOpen = true;
    			}
    		} else { // else title
    			if (type.equals("subtitle")){
    				
    				// gestion apertura e chiusura.
        			STATO_APERTURA_SEZIONI statoAperturaSezione = de.getStatoSottosezione();
        			boolean gestioneAperturaSubTitle = !STATO_APERTURA_SEZIONI.DISABILITATO.equals(statoAperturaSezione);
    				
    				String subtitleDeId = deName + "__id";
    				
    				String cssClassDivElementi = "subtitleBox";
    				String cssClassTitle = "navigatorAnchor";
        			String titoloComandoApertura = "";
        			String cssClassSubtitle = "";
        			if(gestioneAperturaSubTitle) {
        				titoloComandoApertura = " title=\""+ Costanti.TOOLTIP_VISUALIZZA_SUBTITLE +"\"";
        				cssClassTitle = "navigatorAnchorClosable";
        				
        				cssClassSubtitle = "subtitle " + labelStyleClass + " subtitleCollapsed";
        				if(de.isVisualizzaSezioneAperta()){
        					cssClassSubtitle = "subtitle " + labelStyleClass + " subtitleOpen";
        				}
        			}
        			
    				// se c'e' un altro field set aperto viene chiuso
        			if(subtitleOpen){
        				%>
        				</div>
            			<%
            			subtitleOpen = false;
        			}
					
    				%>
    				<div class="<%= cssClassSubtitle %>" id="<%= deName  %>__divEsterno">
    				
	    				<%
						if(gestioneAperturaSubTitle){
	  						%>
	    				<span class="subtitleGroup">
	       					<span class="subtitleAnchor">
	       						<i class="material-icons md-16" id="<%= deName  %>__icon" title="<%= Costanti.TOOLTIP_VISUALIZZA_SUBTITLE%>"><%= Costanti.ICON_VISUALIZZA_SUBTITLE%></i>
	       					</span>
	       					<a id="<%= deName  %>__anchor" name="<%=rowName %>" class="<%=cssClassTitle %>" <%=titoloComandoApertura %>"><%=deLabel %></a>
	       				</span>
	       				<%
							} else {
						%>
	        			<div class="subtitle <%= labelStyleClass %>">
	        				<span class="subtitle"><a name="<%=rowName %>"  class="<%=cssClassTitle %>" ><%=deLabel %>&nbsp;&nbsp;&nbsp;&nbsp;</a></span>
	        			</div>
	        			<%
							}
						%>
						<%
							if(gestioneAperturaSubTitle){
						%>
	        			<script type="text/javascript" nonce="<%= randomNonce %>">
	       					$(document).ready(function() {
							<%
								boolean sub = de.isVisualizzaSezioneAperta();
				      		%>
				      		var subtitle_<%= deName  %>_aperto = <%=sub %>; 
				      		
				      		if(subtitle_<%= deName  %>_aperto){
			      				$("#<%= subtitleDeId  %>").show();
			      				$("#<%= deName  %>__anchor").attr('title', '<%= Costanti.TOOLTIP_NASCONDI_SUBTITLE%>');
			      				$("#<%= deName  %>__icon").html('<%= Costanti.ICON_NASCONDI_SUBTITLE%>');
			      				$("#<%= deName  %>__icon").attr('title', '<%= Costanti.TOOLTIP_NASCONDI_SUBTITLE%>');
			      				$("#<%= deName  %>__divEsterno").removeClass('subtitleCollapsed');
			      				$("#<%= deName  %>__divEsterno").addClass('subtitleOpen');
			      			} else {
			      				$("#<%= subtitleDeId  %>").hide();
			      				$("#<%= deName  %>__anchor").attr('title', '<%= Costanti.TOOLTIP_VISUALIZZA_SUBTITLE%>');
			      				$("#<%= deName  %>__icon").html('<%= Costanti.ICON_VISUALIZZA_SUBTITLE%>');
			      				$("#<%= deName  %>__icon").attr('title', '<%= Costanti.TOOLTIP_VISUALIZZA_SUBTITLE%>');
			      				$("#<%= deName  %>__divEsterno").removeClass('subtitleOpen');
			      				$("#<%= deName  %>__divEsterno").addClass('subtitleCollapsed');
			      			}
				      		
				      		$("#<%= deName  %>__anchor").click(function(){
				      			subtitle_<%= deName  %>_aperto = !subtitle_<%= deName  %>_aperto;
				      			
				      			if(subtitle_<%= deName  %>_aperto){
				      				$("#<%= subtitleDeId  %>").show();
				      				$("#<%= deName  %>__anchor").attr('title', '<%= Costanti.TOOLTIP_NASCONDI_SUBTITLE%>');
				      				$("#<%= deName  %>__icon").html('<%= Costanti.ICON_NASCONDI_SUBTITLE%>');
				      				$("#<%= deName  %>__icon").attr('title', '<%= Costanti.TOOLTIP_NASCONDI_SUBTITLE%>');
				      				$("#<%= deName  %>__divEsterno").removeClass('subtitleCollapsed');
				      				$("#<%= deName  %>__divEsterno").addClass('subtitleOpen');
				      				inizializzaSelectSezione('<%= subtitleDeId  %>');
				      			} else {
				      				$("#<%= subtitleDeId  %>").hide();
				      				$("#<%= deName  %>__anchor").attr('title', '<%= Costanti.TOOLTIP_VISUALIZZA_SUBTITLE%>');
				      				$("#<%= deName  %>__icon").html('<%= Costanti.ICON_VISUALIZZA_SUBTITLE%>');
				      				$("#<%= deName  %>__icon").attr('title', '<%= Costanti.TOOLTIP_VISUALIZZA_SUBTITLE%>');
				      				$("#<%= deName  %>__divEsterno").removeClass('subtitleOpen');
				      				$("#<%= deName  %>__divEsterno").addClass('subtitleCollapsed');
				      			}
				      		});
				      		
				      		$("#<%= deName  %>__icon").click(function(){
				      			subtitle_<%= deName  %>_aperto = !subtitle_<%= deName  %>_aperto;
				      			
				      			if(subtitle_<%= deName  %>_aperto){
				      				$("#<%= subtitleDeId  %>").show();
				      				$("#<%= deName  %>__anchor").attr('title', '<%= Costanti.TOOLTIP_NASCONDI_SUBTITLE%>');
				      				$("#<%= deName  %>__icon").html('<%= Costanti.ICON_NASCONDI_SUBTITLE%>');
				      				$("#<%= deName  %>__icon").attr('title', '<%= Costanti.TOOLTIP_NASCONDI_SUBTITLE%>');
				      				$("#<%= deName  %>__divEsterno").removeClass('subtitleCollapsed');
				      				$("#<%= deName  %>__divEsterno").addClass('subtitleOpen');
				      				inizializzaSelectSezione('<%= subtitleDeId  %>');
				      			} else {
				      				$("#<%= subtitleDeId  %>").hide();
				      				$("#<%= deName  %>__anchor").attr('title', '<%= Costanti.TOOLTIP_VISUALIZZA_SUBTITLE%>');
				      				$("#<%= deName  %>__icon").html('<%= Costanti.ICON_VISUALIZZA_SUBTITLE%>');
				      				$("#<%= deName  %>__icon").attr('title', '<%= Costanti.TOOLTIP_VISUALIZZA_SUBTITLE%>');
				      				$("#<%= deName  %>__divEsterno").removeClass('subtitleOpen');
				      				$("#<%= deName  %>__divEsterno").addClass('subtitleCollapsed');
				      			}
				      		});
	    										});
	       				</script>
       				<%
						}
					%>
       			</div>
       			<%
       			
	       			if(!subtitleOpen){
	   	    			%>
	   	    				<div id="<%= subtitleDeId  %>" class="<%=cssClassDivElementi %>">
	   	    			<%
	   	    			subtitleOpen = true;
	       			}
        		} else { // else subtitle
	    			if (type.equals("note")){
	    				String noteValue = !de.getValue().equals("") ? de.getValue() : "&nbsp;";
	    				
	    				%>
	        			<div class="prop">
						<label class="<%= labelStyleClass %>" id="<%=deLabelId %>" ><%=deLabel %></label>
        					<div class="<%=classDivNoEdit %>">
							<span class="<%=classSpanNoEdit %>"><%=noteValue %></span>
						</div>
	        			</div>
	        			<%
	        		} else { // else note
	        			if (type.equals("link")){
	        				String visualizzaAjaxStatus = de.isShowAjaxStatus() ? Costanti.JS_FUNCTION_VISUALIZZA_AJAX_STATUS : "";
	        				String deLabelLink = !de.getLabelLink().equals("") ? de.getLabelLink() : "&nbsp;";
	        				classSpanNoEdit = de.getStyleClass(); // gestione override classe di default 24/03/2021
	        				
	        				String deTarget = " ";
							if (!de.getTarget().equals("")) {
					  			deTarget = " target=\""+ de.getTarget() +"\"";
					  		}
							
							if (!de.getUrl().equals("")) {
								de.addParameter(new Parameter(Costanti.PARAMETER_PREV_TAB_KEY, tabSessionKey));
							}
							String id = "form-link_" + i ;
							
							DataElementConfirm deConfirm = de.getConfirm();
							boolean visualizzaConferma = deConfirm != null;
	        				%>
	            			<div class="prop prop-link">
	            				<label class="<%= labelStyleClass %>" id="<%=deLabelId %>"><%=deLabelLink %></label>
	            				<div class="<%=classDivNoEdit %>"> 
	            					<% if (!visualizzaConferma) { %>
		            					<span class="<%=classSpanNoEdit %>"><a id="<%=id %>" href="<%= de.getUrl() %>" <%=deTarget %> ><%= de.getValue() %></a></span>
		            					<% if (!de.getOnClick().equals("")) { %>
			            					<script type="text/javascript" nonce="<%= randomNonce %>">
										      	 $(document).ready(function(){
														$('#<%=id %>').click(function() {
															<%= visualizzaAjaxStatus %><%= de.getOnClick() %>;
														});
													});
											</script>
										<%  } %>
									<%  } else {%>
										<span class="<%=classSpanNoEdit %>"><a id="<%=id %>" href="<%= de.getUrl() %>"><%= de.getValue() %></a></span>
										<input type="hidden" name="__i_hidden_title_<%= id %>" id="hidden_title_<%= id %>"  value="<%= deConfirm.getTitolo() %>"/>
						      			<input type="hidden" name="__i_hidden_body_<%= id %>" id="hidden_body_<%= id %>"  value="<%= deConfirm.getBody() %>"/>
						      			<input type="hidden" name="__i_hidden_azione_<%= id %>" id="hidden_azione_<%= id %>"  value="<%= deConfirm.getAzione() %>"/>
						      			<input type="hidden" name="__i_hidden_url_<%= id %>" id="hidden_url_<%= id %>"  value="<%= de.getUrl() %>"/>
										<script type="text/javascript" nonce="<%= randomNonce %>">
									      	 $(document).ready(function(){
													$('#<%=id %>').click(function(event) {
														
														// Evita l'azione predefinita dell'evento click (apertura del link)
													    event.preventDefault();

													    // Mostra la modale o finestra di conferma
													    //$('#myModal').modal('show');

													    // Aggiungi un listener per l'evento di conferma dalla modale
													    //document.getElementById('confirmButton').addEventListener('click', function() {
													        // Ripristina l'azione originale dell'evento click
													   //     window.location.href = event.target.href;
													    //});
														
														
														// visualizza modale di conferma
														var label = $("#hidden_title_<%=id %>").val();
														var body = $("#hidden_body_<%=id %>").val();
														var url = $("#hidden_url_<%=id %>").val();
														var azione = $("#hidden_azione_<%=id %>").val();
														mostraDownloadInformazioniCifrateModal(label,body,url,azione);
													});
												});
										</script>
									
									<%  } %>
	            				</div>
	            				<% 
						      		if(deInfo != null){
						      			String idDivIconInfo = "divIconInfo_"+i;
						      			String idIconInfo = "iconInfo_"+i; 
						      	%> 	<div class="iconInfoBox" id="<%=idDivIconInfo %>">
						      			<input type="hidden" name="__i_hidden_title_<%= idIconInfo %>" id="hidden_title_<%= idIconInfo %>"  value="<%= deInfo.getHeaderFinestraModale() %>"/>
						      			<input type="hidden" name="__i_hidden_body_<%= idIconInfo %>" id="hidden_body_<%= idIconInfo %>"  value="<%= deInfo.getBody() %>"/>
								      	<span class="spanIconInfoBox">
											<i class="material-icons md-24" id="<%=idIconInfo %>"><%= deInfo.getButtonIcon() %></i>
										</span>
									</div>
						      	<% } %>
						      	<% if(!deNote.equals("")){ %>
						      		<p class="note <%= labelStyleClass %>"><%=deNote %></p>
						      	<% } %>
	            			</div>
	            			<%
	            		} else { // else link
	            			if (type.equals("text")){
	            				String textValNoEdit = de.getValue() != null && !de.getValue().equals("") ? de.getValue() : (pd.getMode().equals("view-noeditbutton") ? "&nbsp;" : "not defined");
	            				classSpanNoEdit = de.getStyleClass(); // gestione override classe di default 24/03/2021
	            				%>
	                			<div class="prop">
	                				<label class="<%= labelStyleClass %>" id="<%=deLabelId %>" ><%=deLabel %></label>
	                				<div class="<%=classDivNoEdit %>"> 
		                				<span class="<%=classSpanNoEdit %>"><%= textValNoEdit %></span>
		                				<input type="hidden" name="<%= deName %>" value="<%= de.getValue() %>"/>
		                				
		                				<% 
		                					// tasti azione sulla text
											if(!de.getImage().isEmpty()){
												for(int idxLink =0; idxLink < de.getImage().size() ; idxLink ++ ){
													DataElementImage image = de.getImage().get(idxLink);
													String classLink = image.getStyleClass();
													String deIconName = image.getImage(); 
		                					
													String deTip = !image.getToolTip().equals("") ? " title=\"" + image.getToolTip() + "\"" : "";
		                							
		                							String deTarget = " ";
											  		if (!image.getTarget().equals("")) {
											  			deTarget = " target=\""+ image.getTarget() +"\"";
											  		}
										  			
											  		String visualizzaAjaxStatus = image.isShowAjaxStatus() ? Costanti.JS_FUNCTION_VISUALIZZA_AJAX_STATUS : "";
											  		
											  		if (!image.getUrl().equals("")) {
											  			image.addParameter(new Parameter(Costanti.PARAMETER_PREV_TAB_KEY, tabSessionKey));
													}
											  		String id = "form-image-link_" + i + "_" + idxLink;
			                					%>
			                					<a id="<%=id %>" class="text-action-link <%= classLink %>" <%= deTip %> <%=deTarget %> href="<%= image.getUrl() %>" type="button" >
			                						<span class="icon-box">
														<i class="material-icons md-16"><%= deIconName %></i>
													</span>
													<% if (!image.getOnClick().equals("")) { %>
						            					<script type="text/javascript" nonce="<%= randomNonce %>">
													      	 $(document).ready(function(){
																	$('#<%=id %>').click(function() {
																		<%= visualizzaAjaxStatus %>"postVersion_"<%= image.getOnClick() %>;
																	});
																});
														</script>
													<%  } %>
			                					</a>
			                				<%
												}// end for-edit-link
											} // end edit-link
										%>
		                			</div>
	                				<% if(!deNote.equals("")){ %>
							      		<p class="note <%= labelStyleClass %>"><%=deNote %></p>
							      	<% } %>
	                			</div>
	                			<%
	                		} else { // else text
	                			if (type.equals("textedit")){
	                				%>
	                    			<div class="prop">
	                    				<label class="<%= labelStyleClass %>" id="<%=deLabelId %>"><%=deLabel %></label>
	                    				<%
								    	if (pd.getMode().equals("view") || pd.getMode().equals("view-noeditbutton")) {
								    		String taeditValNoEdit = de.getValue() != null && !de.getValue().equals("") ? de.getValue() : (pd.getMode().equals("view-noeditbutton") ? "&nbsp;" : "not defined");
								    		%><div class="<%=classDivNoEdit %>"> 
								    			<span class="<%=classSpanNoEdit %>"><%= taeditValNoEdit %></span>
								    			</div><%
								    	} else {
								    		String selDataAttributes = !de.getDataAttributesAsString().equals("") ? de.getDataAttributesAsString() : " ";
								      		%><input type="text" name="<%= deName %>" value="<%= de.getValue() %>" class="<%= classInput %>" <%= selDataAttributes %> >
								      		<%
								      			if(!de.getDataAttributesAsString().equals("")){
								      				
								      				String [] values = de.getValues();
								      				
								      				boolean multiColors = de.getDataAttributes().containsKey("colors");
								      				
								      				%>
								      					<script type="text/javascript" nonce="<%= randomNonce %>">
								      					
								      						<% 
									      						if (values != null) {
									      							%>
									      								var valori_<%= deName %> = [];
	                            									<%
	                            									
	                            									String [] labels = de.getLabels();
	                            									/*
	                            									for (int v = 0; v < values.length; v++) {
	                            										if (labels != null) {
	                            											%>valori_<%= deName %>.push({'label': '<%= labels[v] %>', 'value': '<%= values[v]  %>', 'index' : <%= v  %>});<%
	                            										} else {
	                            											%>valori_<%= deName %>.push({'label': '<%= values[v] %>', 'value': '<%= values[v]  %>', 'index' : <%= v  %>});<%
	                            										}
	                            									} //end for values
	                            									*/
	                            									
	                            									for (int v = 0; v < values.length; v++) {
                            											%> valori_<%= deName %>.push('<%= values[v]  %>'); <%
	                            									} //end for values
	                            									%>
	                            									// constructs the suggestion engine
	                            									var valori_<%= deName %>_bh = new Bloodhound({
	                            									  datumTokenizer: Bloodhound.tokenizers.whitespace,
	                            									  queryTokenizer: Bloodhound.tokenizers.whitespace,
	                            									  // `states` is an array of state names defined in "The Basics"
	                            									  local: valori_<%= deName %>
	                            									});
	                            									//valori_<%= deName %>_bh.initialize();
	                            									
	                            									<%
	                                        					}
								      						%>
								      					
								      					 /**
									      				   * Initialize tagsinput behaviour on inputs and selects which have
									      				   * data-role=tagsinput
									      				   */
									      				  $(function() {
									      				    $("input[data-role=tagsinput][name='<%= deName %>']").tagsinput(
									      				    		<% if(multiColors || values != null) { %>
										      				    		{ 	
										      				    	<% 	} %>
									      				    		
									      				    		<%
									      				    		if(multiColors) { 
									      				    			String [] supportoColori = de.getStatusValues();
									      				    		%>
										      				    		tagClass: function(item, index) {
										      				    			
										      				    			var supportoColori_<%= deName %> = [];	
										      				    			<% 
												      						if (supportoColori != null) {
												      							for (int v = 0; v < supportoColori.length; v++) {
			                            											%> supportoColori_<%= deName %>.push('<%= supportoColori[v]  %>'); <%
				                            									} //end for values
												      							%>
										      				    			<%
					                                        					}
												      						%>
										      				    			if(index !== undefined && index < supportoColori_<%= deName %>.length){
// 										      				    				return 'label label-info label-info-' + (index % Costanti.NUMERO_GRUPPI_CSS);
										      				    				return 'label label-info ' + supportoColori_<%= deName %>[index];
										      				    			}
										      				    			
										      				    			return 'label label-info label-info-default';
										      				    		  }
									      				    		<% if(multiColors && values != null) { %>
									      				    		, 	
									      				    		<% 	} %>
									      				    		 
									      				    			<%
									      				    			}
									      				    		%>
									      				    		
									      				    		<%
									      				    			if(values != null) {
								      				    					// source: valori_<%= deName % >_bh.ttAdapter()
								      				    					%>
								      				    					// ,itemValue: 'value', itemText: 'label'
								      				    					  typeaheadjs: {
								      				    						highlight: true,
								      				    					    name: 'valori_<%= deName %>',
								      				    					    // displayKey: 'label',
								      				    					    // valueKey: 'value',
								      				    					    source: valori_<%= deName %>_bh
								      				    					  }
								      				    					<%
								      				    				}
								      				    			%>
							      				    			<% if(multiColors || values != null) { %>		
							      				    			}
							      				    			<% } %>
									      				    );
									      				  });
								      					</script>
								      				<%
								      			}
								      		%>
								      		
								      	<% 
								      		if(deInfo != null){
								      			String idDivIconInfo = "divIconInfo_"+i;
								      			String idIconInfo = "iconInfo_"+i; 
								      	%> 	<div class="iconInfoBox" id="<%=idDivIconInfo %>">
								      			<input type="hidden" name="__i_hidden_title_<%= idIconInfo %>" id="hidden_title_<%= idIconInfo %>"  value="<%= deInfo.getHeaderFinestraModale() %>"/>
								      			<input type="hidden" name="__i_hidden_body_<%= idIconInfo %>" id="hidden_body_<%= idIconInfo %>"  value="<%= deInfo.getBody() %>"/>
										      	<span class="spanIconInfoBox">
													<i class="material-icons md-24" id="<%=idIconInfo %>"><%= deInfo.getButtonIcon() %></i>
												</span>
											</div>
								      	<% } 
								    	}
								      	%>
								      	<% if(!deNote.equals("")){ %>
								      		<p class="note <%= labelStyleClass %>"><%=deNote %></p>
								      	<% } %>
	                    			</div>
	                    			<%
	                    		} else { // else textedit
	                    			if (type.equals("number")){
		                				%>
		                    			<div class="prop">
		                    				<label class="<%= labelStyleClass %>" id="<%=deLabelId %>"><%=deLabel %></label>
		                    				<%
									    	if (pd.getMode().equals("view") || pd.getMode().equals("view-noeditbutton")) {
									    		String taeditValNoEdit = de.getValue() != null && !de.getValue().equals("") ? de.getValue() : (pd.getMode().equals("view-noeditbutton") ? "&nbsp;" : "not defined");
									    		%><div class="<%=classDivNoEdit %>"> 
									    			<span class="<%=classSpanNoEdit %>"><%= taeditValNoEdit %></span>
									    			</div><%
									    	} else {
									    		String minvalue = de.getMinValue() != null ? " min=\"" + de.getMinValue() + "\"" : "";
									    		String maxValue = de.getMaxValue() != null ? " max=\"" + de.getMaxValue() + "\"" : "";
									    		String customJsFunction = de.getCustomJsFunction() != null && !de.getCustomJsFunction().equals("")  ? " gw-function=\"" + de.getCustomJsFunction() + "\"" : "";
									    		
									      		%><input type="number" name="<%= deName %>" value="<%= de.getValue() %>" class="<%= classInput %>" <%=minvalue %> <%=maxValue %> <%=customJsFunction %> >
									      	<% 
								      		if(deInfo != null){
								      			String idDivIconInfo = "divIconInfo_"+i;
								      			String idIconInfo = "iconInfo_"+i; 
									      	%> 	<div class="iconInfoBox" id="<%=idDivIconInfo %>">
									      			<input type="hidden" name="__i_hidden_title_<%= idIconInfo %>" id="hidden_title_<%= idIconInfo %>"  value="<%= deInfo.getHeaderFinestraModale() %>"/>
									      			<input type="hidden" name="__i_hidden_body_<%= idIconInfo %>" id="hidden_body_<%= idIconInfo %>"  value="<%= deInfo.getBody() %>"/>
											      	<span class="spanIconInfoBox">
														<i class="material-icons md-24" id="<%=idIconInfo %>"><%= deInfo.getButtonIcon() %></i>
													</span>
												</div>
									      	<% } 
									    	}
									      	%>
									      	<% if(!deNote.equals("")){ %>
									      		<p class="note <%= labelStyleClass %>"><%=deNote %></p>
									      	<% } %>
		                    			</div>
		                    			<%
		                    		} else { // else number
		                    			if (type.equals("crypt")){
		                    				DataElementPassword dePwd = de.getPassword();
		                    				boolean visualizzaPasswordChiaro = dePwd.isVisualizzaPasswordChiaro();
		                    				boolean bottoneGeneraPassword = dePwd.isVisualizzaBottoneGeneraPassword();
		                    				boolean visualizzaIconaMostraPassword = dePwd.isVisualizzaIconaMostraPassword();
		                    				
		                    				String dePwdType = visualizzaPasswordChiaro ? "text" : "password";
		                    				String dePwdNoEdit = visualizzaPasswordChiaro ? de.getValue() : Costanti.PARAMETER_LOCK_DEFAULT_VALUE;
		                    				if(bottoneGeneraPassword){
		                    					classInput = Costanti.INPUT_PWD_CHIARO_CSS_CLASS;
		                    				}
		                    				String idPwd = "pwd_" + i;
		                    				%>
		                        			<div class="prop">
		                        				<label class="<%= labelStyleClass %>" id="<%=deLabelId %>" ><%=deLabel %></label>
		                        				<%
							          			if (pd.getMode().equals("view") || pd.getMode().equals("view-noeditbutton")) {
													%><div class="<%=classDivNoEdit %>"> <span class="<%=classSpanNoEdit %>"><%=dePwdNoEdit %></span></div><%
							   					} else {
							   						String idPwdEye = "pwd_" + i + "_eye";
							   						String idPwdEyeSpan = "pwd_" + i + "_eye_span";
													%><input class="<%= classInput %>" type="<%=dePwdType %>" name="<%= deName  %>" id="<%=idPwd %>" value="<%= de.getValue()  %>">
													<%
							          				if (!bottoneGeneraPassword && visualizzaIconaMostraPassword) {
								          				%>
								          					<span id="<%=idPwdEyeSpan %>" class="span-password-eye">
														  		<i id="<%=idPwdEye %>" class="material-icons md-24"><%= Costanti.ICON_VISIBILITY %></i>
														  	</span>
															<script type="text/javascript" nonce="<%= randomNonce %>">
																$(document).ready(function(){
																	$('#<%=idPwdEye %>').click(function() {
																		
																		// toggle the type attribute
																		var x = document.getElementById("<%=idPwd %>");
																		  if (x.type === "password") {
																		    x.type = "text";
																		  } else {
																		    x.type = "password";
																		  }
		
																		  // toggle the eye slash icon
																	    var eyeIcon = $('#<%=idPwdEye %>');
																	    if (x.type === 'password') {
																	        eyeIcon.html('<%= Costanti.ICON_VISIBILITY %>');
																	    } else {
																	        eyeIcon.html('<%= Costanti.ICON_VISIBILITY_OFF %>');
																	    }
																	    
																	});
																});
															</script>
							     					<% 
								   					}
									      		if(deInfo != null || bottoneGeneraPassword){
									      			String idDivIconInfo = "divIconInfo_"+i;
									      			String idIconInfo = "iconInfo_"+i; 
									      			
											      	%> 	<div class="iconInfoBox" id="<%=idDivIconInfo %>">
											      		<% 
									      				if(bottoneGeneraPassword){
									      					PasswordGenerator pwdGen = dePwd.getPasswordGenerator();
									      					String id = "form-gen-pass-link_" + i;
									      				%>
									      					<script type="text/javascript" nonce="<%= randomNonce %>">
									      						var pwdGenerate_<%= deName %>_idx = 0;
									      						var pwdGenerate_<%= deName %> = [];
									      					
									      						<% 
									      							for(int iPwd = 0; iPwd < dePwd.getNumeroSample(); iPwd ++){
									      								String pwTmp = pwdGen.generate();
									      								%> pwdGenerate_<%= deName %>.push('<%= pwTmp  %>'); <%
									      							}
									      						%>
									      						
									      						function generaPwd(inputElement){
									      							pwdGenerate_<%= deName %>_idx = pwdGenerate_<%= deName %>_idx % pwdGenerate_<%= deName %>.length;
									      							
									      							var newValue = pwdGenerate_<%= deName %> [pwdGenerate_<%= deName %>_idx];
									      							$('input[name="'+ inputElement+'"]').val(newValue);
									      							
									      							pwdGenerate_<%= deName %>_idx ++;
									      						}
									      					</script>
									      					<span class="spanButtonGeneraBox">
								      							<input id="<%=id %>" class="buttonGeneraPassword" type="button" title="<%=dePwd.getTooltipButtonGeneraPassword() %>" value="<%=dePwd.getLabelButtonGeneraPassword() %>">
								      							<script type="text/javascript" nonce="<%= randomNonce %>">
															      	 $(document).ready(function(){
																			$('#<%=id %>').click(function() {
																				<%= Costanti.JS_FUNCTION_VISUALIZZA_AJAX_STATUS %>generaPwd('<%= deName  %>');<%= Costanti.JS_FUNCTION_NASCONDI_AJAX_STATUS %>;
																			});
																		});
																</script>
									      					</span>
									      				<% } %>	
											      		<% 
									      				if(deInfo != null){
									      				%>
											      			<input type="hidden" name="__i_hidden_title_<%= idIconInfo %>" id="hidden_title_<%= idIconInfo %>"  value="<%= deInfo.getHeaderFinestraModale() %>"/>
											      			<input type="hidden" name="__i_hidden_body_<%= idIconInfo %>" id="hidden_body_<%= idIconInfo %>"  value="<%= deInfo.getBody() %>"/>
													      	<span class="spanIconInfoBox">
																<i class="material-icons md-24" id="<%=idIconInfo %>"><%= deInfo.getButtonIcon() %></i>
															</span>
															
														<% } %>	
														</div>
											      	<% 
							   					}
						     					%>
						     					<% if(!deNote.equals("")){ %>
									      			<p class="note <%= labelStyleClass %>"><%=deNote %></p>
									      		<% } %>
		                        			</div>
		                        			<%
						   					}
		                        		} else { // else crypt
		                        			if (type.equals("textarea") || type.equals("textarea-noedit")){
		                        				String inputId = "txtA" + i;
		            	     					if (type.equals("textarea-noedit")){
		            								inputId = "txtA_ne" + i; 
		            	     					}
		                        				%>
		                            			<div class="prop">
		                            				<label class="<%= labelStyleClass %>" id="<%=deLabelId %>"><%=deLabel %></label>
		                            				<%
							     					if ((pd.getMode().equals("view") || pd.getMode().equals("view-noeditbutton")) && de.isLabelAffiancata()) {
							     						String taValNoEdit = de.getValue() != null && !de.getValue().equals("") ? de.getValue() : (pd.getMode().equals("view-noeditbutton") ? "&nbsp;" : "not defined");
							     						%><div class="<%=classDivNoEdit %>"> <span class="<%=classSpanNoEdit %>"><%= taValNoEdit %></span></div><%
							     					} else {
							     						String taNoEdit = type.equals("textarea") ? " " : " readonly ";
							     						%><div class="txtA_div">
							     							<textarea id="<%=inputId %>" <%=taNoEdit %> rows='<%= de.getRows() %>' cols='<%= de.getCols() %>' name="<%= deName  %>" class="<%= classInput %>"><%= de.getValue() %></textarea>
							     							<% 
													      		if(deInfo != null){
													      			String idDivIconInfo = "divIconInfo_"+i;
													      			String idIconInfo = "iconInfo_"+i; 
													      	%> 	<div class="iconInfoBox" id="<%=idDivIconInfo %>">
													      			<input type="hidden" name="__i_hidden_title_<%= idIconInfo %>" id="hidden_title_<%= idIconInfo %>"  value="<%= deInfo.getHeaderFinestraModale() %>"/>
													      			<input type="hidden" name="__i_hidden_body_<%= idIconInfo %>" id="hidden_body_<%= idIconInfo %>"  value="<%= deInfo.getBody() %>"/>
															      	<span class="spanIconInfoBox">
																		<i class="material-icons md-24" id="<%=idIconInfo %>"><%= deInfo.getButtonIcon() %></i>
																	</span>
																</div>
													      	<% } %>
						     							</div><%
							     					}
													%>
							     					<% if(!deNote.equals("")){ %>
									      				<p class="note <%= labelStyleClass %>"><%=deNote %></p>
									      			<% } %>
		                            			</div>
		                            			<%
		                            		} else { // else textarea || textarea-noedit
		                            			if (type.equals("button")){
		                            				String visualizzaAjaxStatus = de.isShowAjaxStatus() ? Costanti.JS_FUNCTION_VISUALIZZA_AJAX_STATUS : "";
		                            				String id = "form-btn-link_" + i;
		                            				%>
		                                			<div class="prop">
		                                				<label class="<%= labelStyleClass %>" id="<%=deLabelId %>"><%=deLabel %></label>
		                                				<input id="<%=id %>" type="button" value="<%= de.getValue() %>">
		                                				<% if(!deNote.equals("")){ %>
									      					<p class="note <%= labelStyleClass %>"><%=deNote %></p>
									      				<% } %>
									      				<script type="text/javascript" nonce="<%= randomNonce %>">
													      	 $(document).ready(function(){
																	$('#<%=id %>').click(function() {
																		<%= visualizzaAjaxStatus %><%= de.getOnClick() %>;
																	});
																});
														</script>
		                                			</div>
		                                			<%
		                                		} else { // else button
		                                			if (type.equals("file") || type.equals("multi-file")){
		                                				String multipleFiles = "";
		                                				if(type.equals("multi-file")){
		                                					multipleFiles = " multiple ";
		                                				}
		                                				
		                                				String id = "form-file_" + i;
		                                				%>
		                                    			<div class="prop">
		                                    				<label class="<%= labelStyleClass %>" id="<%=deLabelId %>"><%=deLabel %></label>
		                                    				<%
		                                    				if (pd.getMode().equals("view") || pd.getMode().equals("view-noeditbutton")) {
		                                    	     			String fileValue = (de.getValue() != null && !de.getValue().equals("")) ? de.getValue() : "not defined";
		                                    	            	%> 
		                                    	            	<div class="<%=classDivNoEdit %>"> <span class="<%=classSpanNoEdit %>"><%=fileValue %></span></div><%
		                                    	      		} else {
		                                    	          		%><input id="<%=id %>" size='<%= de.getSize() %>' type=file name="<%= deName  %>" class="<%= classInput %>"  <%= multipleFiles  %> />
													  		<% if(!de.getOnChange().equals("")){ 
													  			String visualizzaAjaxStatus = de.isShowAjaxStatus() ? Costanti.JS_FUNCTION_VISUALIZZA_AJAX_STATUS : "";
													  			String changeHandler = visualizzaAjaxStatus + "postVersion_" + de.getOnChange();
													  		%>
												      		<script type="text/javascript" nonce="<%= randomNonce %>">
																$(document).ready(function(){
																	$('#<%= id  %>').change(function() {
																		<%=changeHandler%>
																	});
																});
															</script>
															<% } %>
													  <%
													      		if(deInfo != null){
													      			String idDivIconInfo = "divIconInfo_"+i;
													      			String idIconInfo = "iconInfo_"+i; 
													      	%> 	<div class="iconInfoBox" id="<%=idDivIconInfo %>">
													      			<input type="hidden" name="__i_hidden_title_<%= idIconInfo %>" id="hidden_title_<%= idIconInfo %>"  value="<%= deInfo.getHeaderFinestraModale() %>"/>
													      			<input type="hidden" name="__i_hidden_body_<%= idIconInfo %>" id="hidden_body_<%= idIconInfo %>"  value="<%= deInfo.getBody() %>"/>
															      	<span class="spanIconInfoBox">
																		<i class="material-icons md-24" id="<%=idIconInfo %>"><%= deInfo.getButtonIcon() %></i>
																	</span>
																</div>
													      	<% }
		                                    	      		}
		                                    				%>
		                                    				<% if(!deNote.equals("")){ %>
									      						<p class="note <%= labelStyleClass %>"><%=deNote %></p>
									      					<% } %>
		                                    			</div>
		                                    			<%
		                                    		} else { // else file
		                                    			if (type.equals("select")){
		                                    				%>
		                                        			<div class="prop">
		                                        				<label class="<%= labelStyleClass %>" id="<%=deLabelId %>"><%=deLabel %></label>
		                                        				<%
		                                        				if (pd.getMode().equals("view") || pd.getMode().equals("view-noeditbutton")) {
		                                      						String selValNoEdit = (de.getSelected() != "") ? de.getSelected() : (pd.getMode().equals("view-noeditbutton") ? "&nbsp;" : "not defined");
		                                      						%><div class="<%=classDivNoEdit %>"> <span class="<%=classSpanNoEdit %>"><%= selValNoEdit %></span></div><%
		                               							} else {
		                               								String visualizzaAjaxStatus = de.isShowAjaxStatus() ? Costanti.JS_FUNCTION_VISUALIZZA_AJAX_STATUS : "";
																	String selTitle = (de.getToolTip()!=null && !de.getToolTip().equals("")) ? ("title='"+de.getToolTip()+"'") : " ";
		                               								String selId = "select_" + i;
		                          									%><select id="<%= selId  %>" name="<%= deName  %>" <%= selTitle %> class="<%= classInput %>"><%
		                          									String [] values = de.getValues();
		                                        					if (values != null) {
		                            									String [] labels = de.getLabels();
		                            									for (int v = 0; v < values.length; v++) {
		                            										String optionSel = values[v].equals(de.getSelected()) ? " selected " : " ";
		                            										
		                            										if (labels != null) {
		                            											%><option value="<%= values[v]  %>" <%=optionSel %> ><%= labels[v] %></option><%
		                            										} else {
		                            											%><option value="<%= values[v]  %>" <%=optionSel %> ><%= values[v] %></option><%
		                            										}
		                            									} //end for values
		                                        					}
		                          									%></select>
		                          									<% if(!de.getOnChange().equals("")){ 
																  			String changeHandler = visualizzaAjaxStatus + de.getOnChange();
																  		%>
															      		<script type="text/javascript" nonce="<%= randomNonce %>">
																			$(document).ready(function(){
																				$('#<%= selId  %>').change(function() {
																					<%=changeHandler%>
																				});
																			});
																		</script>
																		<% } %>
		                          									
		                          									<script type="text/javascript" nonce="<%= randomNonce %>">
			                          									$(document).ready(function() {
			                          									<%
			                          										String abilitaSearch = "false";
			                          										String disabilitaSearch= "{disableInput : false}";
			                          										
			                          										if(de.isAbilitaFiltroOpzioniSelect()){
																      			abilitaSearch = "true";
																      			disabilitaSearch= "{disableInput : false}";
																      		} else {
																      			abilitaSearch = "false";
																      			disabilitaSearch = "{disableInput : true}";
																      		}
															      		%> 
															      		$("#<%= selId %>").searchable(<%= disabilitaSearch %>);
		                          										});
		                          									</script>
		                          									<input type="hidden" id="<%= selId  %>_hidden_chk" value="<%= abilitaSearch  %>"/>
		                          									<%
														      		if(deInfo != null){
														      			String idDivIconInfo = "divIconInfo_"+i;
												      					String idIconInfo = "iconInfo_"+i; 
														      	%> 	<div class="iconInfoBox" id="<%=idDivIconInfo %>">
														      			<input type="hidden" name="__i_hidden_title_<%= idIconInfo %>" id="hidden_title_<%= idIconInfo %>"  value="<%= deInfo.getHeaderFinestraModale() %>"/>
														      			<input type="hidden" name="__i_hidden_body_<%= idIconInfo %>" id="hidden_body_<%= idIconInfo %>"  value="<%= deInfo.getBody() %>"/>
																      	<span class="spanIconInfoBox">
																			<i class="material-icons md-24" id="<%=idIconInfo %>"><%= deInfo.getButtonIcon() %></i>
																		</span>
																	</div>
														      	<% }
		                               							}
		                                        				%>
		                                        				<% if(!deNote.equals("")){ %>
									      							<p class="note <%= labelStyleClass %>"><%=deNote %></p>
									      						<% } %>
		                                        			</div>
		                                        			<%
		                                        		} else { // else select
		                                        			if(type.equals("multi-select")){
		                                        				%>
	                                        					<div class="prop">
			                                        				<label class="<%= labelStyleClass %>" id="<%=deLabelId %>"><%=deLabel %></label>
			                                        				<%
			                                        				if (pd.getMode().equals("view") || pd.getMode().equals("view-noeditbutton")) {
			                                      						String selValNoEdit = (de.getSelezionatiAsString() != "") ? de.getSelezionatiAsString() : (pd.getMode().equals("view-noeditbutton") ? "&nbsp;" : "not defined");
			                                      						%><div class="<%=classDivNoEdit %>"> <span class="<%=classSpanNoEdit %>"><%= selValNoEdit %></span></div><%
			                               							} else {
			                               							 	String visualizzaAjaxStatus = de.isShowAjaxStatus() ? Costanti.JS_FUNCTION_VISUALIZZA_AJAX_STATUS : "";
			                               								String selSize = " size='"+de.getRows()+"' ";
			                               								String selDataAttributes = !de.getDataAttributesAsString().equals("") ? de.getDataAttributesAsString() : " ";
			                               								String selId = "select_" + i;
			                          									%><select id="<%= selId  %>" name="<%= deName  %>" <%= selSize %> class="<%= classInput %>" multiple <%= selDataAttributes %> ><%
			                          									String [] values = de.getValues();
			                                        					if (values != null) {
			                            									String [] labels = de.getLabels();
			                            									for (int v = 0; v < values.length; v++) {
			                            										String optionSel = de.isSelected(values[v]) ? " selected " : " ";
			                            										
			                            										if (labels != null) {
			                            											%><option value="<%= values[v]  %>" <%=optionSel %> ><%= labels[v] %></option><%
			                            										} else {
			                            											%><option value="<%= values[v]  %>" <%=optionSel %> ><%= values[v] %></option><%
			                            										}
			                            									} //end for values
			                                        					}
			                          									%></select>
			                          									<% if(!de.getOnChange().equals("")){ 
																  			String changeHandler = visualizzaAjaxStatus + de.getOnChange();
																  		%>
															      		<script type="text/javascript" nonce="<%= randomNonce %>">
																			$(document).ready(function(){
																				$('#<%= selId  %>').change(function() {
																					<%=changeHandler%>
																				});
																			});
																		</script>
																		<% } %>
			                          									<%
															      			if(!de.getDataAttributesAsString().equals("")){
															      				boolean multiColors = de.getDataAttributes().containsKey("colors");
															      				%>
															      					<script type="text/javascript" nonce="<%= randomNonce %>">
															      					 /**
																      				   * Initialize tagsinput behaviour on inputs and selects which have
																      				   * data-role=tagsinput
																      				   */
																      				  $(function() {
																      				    $("select[multiple][data-role=tagsinput][name='<%= deName %>']").tagsinput(
															      				    		<% 
															      				    		if(multiColors) { 
															      				    		%>
																      				    		{ tagClass: function(item, index) {
																      				    			
																      				    			if(Number.isInteger(index)){
																      				    				return 'label label-info label-info-' + (index % Costanti.NUMERO_GRUPPI_CSS);
																      				    			}
																      				    			
																      				    			return 'label label-info label-info-default';
																      				    		  } 
															      				    			}<%
															      				    		}
																      				    		%>
																      				    );
																      				  });
															      					</script>
															      				<%
															      			}
															      		%>
			                          									<%
															      		if(deInfo != null){
															      			String idDivIconInfo = "divIconInfo_"+i;
															      			String idIconInfo = "iconInfo_"+i; 
															      	%> 	<div class="iconInfoBox" id="<%=idDivIconInfo %>">
															      			<input type="hidden" name="__i_hidden_title_<%= idIconInfo %>" id="hidden_title_<%= idIconInfo %>"  value="<%= deInfo.getHeaderFinestraModale() %>"/>
															      			<input type="hidden" name="__i_hidden_body_<%= idIconInfo %>" id="hidden_body_<%= idIconInfo %>"  value="<%= deInfo.getBody() %>"/>
																	      	<span class="spanIconInfoBox">
																				<i class="material-icons md-24" id="<%=idIconInfo %>"><%= deInfo.getButtonIcon() %></i>
																			</span>
																		</div>
															      	<% } 
			                               							}
															      	%>
			                                        				<% if(!deNote.equals("")){ %>
										      							<p class="note <%= labelStyleClass %>"><%=deNote %></p>
										      						<% } %>
			                                        			</div>
		                                        				<%
		                                        			} else { // else multi-select
		                                        				if (type.equals("checkbox")){
			                                        				%>
			                                            			<div class="prop">
			                                            				<label class="<%= labelStyleClass %>" id="<%=deLabelId %>"><%=deLabel %></label>
			                                            				<%
			                                            				String id = "form-checkbox-link_" + i;
			                                            				String visualizzaAjaxStatus = de.isShowAjaxStatus() ? Costanti.JS_FUNCTION_VISUALIZZA_AJAX_STATUS : "";
								    									String chkVal = de.getSelected().equals("yes") ? " checked='true' " : " ";
								    									String disVal = pd.getMode().equals("view") || pd.getMode().equals("view-noeditbutton") ? "disabled=\"disabled\"" : "";
								    									String controlSetClass = deInfo != null ? "controlset-cb-info" : "controlset";
								    									String styleClass = de.getStyleClass();
								    									if(deInfo == null) {
								    										if(!deNote.equals("") && de.isLabelAffiancata()){
								    											controlSetClass = "controlset-cb-note-affiancate";
								    										}
								    									}
								    									%>	<table class="<%=controlSetClass %>">
						    													<tr> 
						    														<td>
								   														<input id="<%=id %>" type="checkbox" name="<%= deName  %>" value="yes" <%=chkVal %> <%=disVal %> >
								   														<% if (!de.getOnClick().equals("")) { %>
															            					<script type="text/javascript" nonce="<%= randomNonce %>">
																						      	 $(document).ready(function(){
																										$('#<%=id %>').click(function() {
																											<%= visualizzaAjaxStatus %><%= de.getOnClick() %>;
																										});
																									});
																							</script>
																						<%  } %>
								   													</td>
								   													<% if(!de.getLabelRight().equals("")){ %>
								   													<td>
								   														<span class="controlset"><%=de.getLabelRight() %></span>
								   													</td>
								   													<% } %>
								   													<%
																			      		if(!deNote.equals("") && de.isLabelAffiancata()){
																			      			String idDivIconInfo = "divIconInfo_"+i;
																			      			String idIconInfo = "iconInfo_"+i; 
																			      	%> <td>	
																			      			<p class="note-checkbox-affiancata <%= styleClass %>"><%=deNote %></p>
																						</td>
																			      	<% } 
																			      	%>
								   													<%
																			      		if(deInfo != null){
																			      			String idDivIconInfo = "divIconInfo_"+i;
																			      			String idIconInfo = "iconInfo_"+i; 
																			      	%> <td>	
																			      			<div class="iconInfoBox-cb-info" id="<%=idDivIconInfo %>">
																				      			<input type="hidden" name="__i_hidden_title_<%= idIconInfo %>" id="hidden_title_<%= idIconInfo %>"  value="<%= deInfo.getHeaderFinestraModale() %>"/>
																				      			<input type="hidden" name="__i_hidden_body_<%= idIconInfo %>" id="hidden_body_<%= idIconInfo %>"  value="<%= deInfo.getBody() %>"/>
																						      	<span class="spanIconInfoBox-cb-info">
																									<i class="material-icons md-24" id="<%=idIconInfo %>"><%= deInfo.getButtonIcon() %></i>
																								</span>
																							</div>
																						</td>
																			      	<% } 
																			      	%>
							   													</tr>
							   												</table>
								  										<% if(!deNote.equals("") && !de.isLabelAffiancata()){ %>
										      								<p class="note <%= labelStyleClass %>"><%=deNote %></p>
										      							<% } %>
			                                            			</div>
			                                            			<%
			                                            		} else { // else checkbox
			                                            			if (type.equals("radio")){
			                                            				%>
			                                                			<div class="prop">
			                                                				<label class="<%= labelStyleClass %>" id="<%=deLabelId %>"><%=deLabel %></label>
			                                                				<%
						   	        										if (pd.getMode().equals("view") || pd.getMode().equals("view-noeditbutton")) {
						   	        											String radioValNoEdit = !de.getSelected().equals("") ? de.getSelected() : "not defined";
						    													%><div class="<%=classDivNoEdit %>"> <span class="<%=classSpanNoEdit %>"><%=radioValNoEdit %></span></div><%
						    												} else {
						    													%>
						    													<table class="controlset">
						    														<tr>
						    													<%
							  													String [] values = de.getValues();
							  													String [] labels = de.getLabels();
							  													for (int v = 0; v < values.length; v++) {
							  														String chkVal = values[v].equals(de.getSelected()) ? " checked " : " ";
							  														String id = deName + "_" + v;
							  														
							  														if (labels != null) {
				                            											%><td><input type="radio" <%=chkVal %> name="<%= deName  %>" value="<%= values[v]  %>" id="<%=id %>" />
				                            											<label for="<%=id %>"><%= labels[v] %></label></td><%
				                            										} else {
				                            											%><td><input type="radio" <%=chkVal %> name="<%= deName  %>" value="<%= values[v]  %>" id="<%=id %>" />
																						<label for="<%=id %>"><%= values[v] %></label></td><%
				                            										}
							  														%><%
						          												} // end for values
							  													%></tr>
						    													</table><%
																			}
																	    	%>
																	    	<% if(!deNote.equals("")){ %>
										      									<p class="note <%= labelStyleClass %>"><%=deNote %></p>
										      								<% } %>
			                                                			</div>
			                                                			<%
			                                                		} else { // else radio
			                                                			if(type.equals("interval-number")){
			                                                				%>
			                                                					<div class="prop">
												                    				<label class="<%= labelStyleClass %>" id="<%=deLabelId %>"><%=deLabel %></label>
												                    				<%
																			    	if (pd.getMode().equals("view") || pd.getMode().equals("view-noeditbutton")) {
																			    		String taeditValNoEdit = de.getValuesNoEdit(pd.getMode());
																			    		%><div class="<%=classDivNoEdit %>"> 
																			    			<span class="<%=classSpanNoEdit %>"><%= taeditValNoEdit %></span>
																			    			</div><%
																			    	} else {
																			    		String minvalue = de.getMinValue() != null ? " min=\"" + de.getMinValue() + "\"" : "";
																			    		String maxValue = de.getMaxValue() != null ? " max=\"" + de.getMaxValue() + "\"" : "";
																			    		String customJsFunction = de.getCustomJsFunction() != null && !de.getCustomJsFunction().equals("")  ? " gw-function=\"" + de.getCustomJsFunction() + "\"" : "";
																			    		
																			      		%>
																			      		<div class="intervalExternalDiv">
																			      		<%
																			      			for(int z=0; z < de.getNames().length ; z++){
																			      				String nameI = de.getNames()[z] == null ? "" : de.getNames()[z];
																			      				String valueI = de.getValues()[z] == null ? "" : de.getValues()[z];
																			      		%>
																			      			<div class="intervalInnerDiv">
																			      				<input type="number" name="<%= nameI %>" value="<%= valueI %>" class="<%= classInput %> intervalInnerInput" <%=minvalue %> <%=maxValue %> <%=customJsFunction %> >
																			      			</div>
																			      		<%
																				      		} // end for
																				      	%>
																			      		</div>
																			      	<% 
																			      		if(deInfo != null){
																			      			String idDivIconInfo = "divIconInfo_"+i;
																			      			String idIconInfo = "iconInfo_"+i; 
																			      	%> 	<div class="iconInfoBox" id="<%=idDivIconInfo %>">
																			      			<input type="hidden" name="__i_hidden_title_<%= idIconInfo %>" id="hidden_title_<%= idIconInfo %>"  value="<%= deInfo.getHeaderFinestraModale() %>"/>
																			      			<input type="hidden" name="__i_hidden_body_<%= idIconInfo %>" id="hidden_body_<%= idIconInfo %>"  value="<%= deInfo.getBody() %>"/>
																					      	<span class="spanIconInfoBox">
																								<i class="material-icons md-24" id="<%=idIconInfo %>"><%= deInfo.getButtonIcon() %></i>
																							</span>
																						</div>
																			      	<% } 
																			    	} // end else
																			      	%>
																			      	<% if(!deNote.equals("")){ %>
																			      		<p class="note <%= labelStyleClass %>"><%=deNote %></p>
																			      	<% } %>
												                    			</div>
			                                                				<%
			                                                			} else{ // else interval number			   
			                                                				if (type.equals("lock")){
			                        		                    				String idPwd = "pwd_" + i;
			                        		                    				%>
			                        		                        			<div class="prop">
			                        		                        				<label class="<%= labelStyleClass %>" id="<%=deLabelId %>" ><%=deLabel %></label>
			                        		                        				<%
			                        							          			if (pd.getMode().equals("view") || pd.getMode().equals("view-noeditbutton")) {
			                        													%><div class="<%=classDivNoEdit %>"> <span class="<%=classSpanNoEdit %>"><%= Costanti.PARAMETER_LOCK_DEFAULT_VALUE  %></span></div><%
			                        							   					} else {
			                        							   						String idPwdEdit = idPwd + "_edit";
			                        							   						String idPwdEditSpan = idPwd + "_edit_span";
			                        							   						String idPwdLock = idPwd + "_lock";
			                        							   						String idPwdLockOpen = idPwd + "_lock_open";
			                        							   						String idPwdViewLock = idPwd + "_lock_view";
			                        							   						String idPwdCopyLock = idPwd + "_lock_copy";
			                        							   						String idPwdViewInnerLock = idPwd + "_lock_view_inner";
			                        							   						String hiddenLockName = Costanti.PARAMETER_LOCK_PREFIX + deName;
			                        							   						String hiddenLockId = Costanti.PARAMETER_LOCK_PREFIX + idPwd;
			                        							   						String visualizzaAjaxStatus = de.isShowAjaxStatus() ? Costanti.JS_FUNCTION_VISUALIZZA_AJAX_STATUS : "";
			                        							   						DataElementPassword dePwd = de.getPassword();
			                        							   						boolean lockReadOnly = dePwd.isLockReadOnly();
			                        							   						boolean forzaVisualizzazioneInputUtente = dePwd.isLockForzaVisualizzazioneInputUtente();
			                        							   						boolean utilizzaInputPassword = dePwd.isLockUtilizzaInputPassword();
			                        							   						boolean visualizzaIconLucchetto = dePwd.isLockVisualizzaIconaLucchetto();
			                        							   						boolean lockValuePresent = !de.getValue().equals(""); // e' gia' presente un valore
			                        							   						boolean visualizzaInformazioniCifrate = lockValuePresent && dePwd.isLockVisualizzaInformazioniCifrate();
			                        							   						
			                        							   						String lockValue = lockValuePresent ? Costanti.PARAMETER_LOCK_DEFAULT_VALUE : de.getValue();
			                        							   						String lockDisabled = lockValuePresent ? " disabled=\"disabled\"" : "";
			                        							   						// 1. decido il valore da inserire nell'input e la visualizzazione readonly del campo
			                        							   						// se devo visualizzare l'input come password o forzare l'input dell'utente  visualizzo gli asterischi
			                        							   						if(forzaVisualizzazioneInputUtente || (utilizzaInputPassword && !dePwd.isLockVisualizzaInformazioniCifrate())){
			                        							   							lockValue = de.getValue();
			                        							   						}
			                        							   						
			                        							   						// 2. Tipo del campo, text quando e' vuoto o password quando e' valorizzato
			                        							   						String dePwdType = !lockValuePresent ? "text" : "password";
			                        							   						if(utilizzaInputPassword) { // in questa modalita' l'input e' sempre di tipo password
			                        							   							dePwdType = "password";
			                        							   						}
			                        							   						// 3. se il lock e' readonly il campo input sara' disabilitato
			                        							   						if(lockReadOnly) {
			                        							   							lockDisabled = " disabled=\"disabled\"";
			                        							   						}
			                        				                    						     
			                        							   						// 4. Comando Edit visualizzato se presente un valore e il campo e' modificabile
			                        							   						boolean visualizzaComandoEdit = lockValuePresent && !lockReadOnly;
			                        							   						
			                        							   						// 5. Comando Eye visualizzato quando si utilizza il campo password e c'e' un valore o non si devono utilizzare i servizi remoti di decodifica
			                        							   						boolean visualizzaComandoEye = utilizzaInputPassword && (!lockValuePresent || !dePwd.isLockVisualizzaInformazioniCifrate());
			                        							   						
			                        							   						// 6. Comando all'interno dell'input visualizzato se almeno uno dei due comandi qui su e' abilitato
			                        				                    				boolean visualizzaComandiInternoInput = visualizzaComandoEdit || visualizzaComandoEye;

			                        							   						// 7. Comando lock aperto visualizzato se non c'e un valore oppure si forza la visualizzazione dell'input utente.
			                        				                    				boolean visualizzaOpenLock = !lockValuePresent || forzaVisualizzazioneInputUtente;
			                        							   						
			                        							   						String spanComaniInternoInputClass = (visualizzaComandoEdit && visualizzaComandoEye) ? "span-password-eye-2" : "span-password-eye";
			                        				                    				
			                        													%><input class="<%= classInput %>" type="<%=dePwdType %>" name="<%= deName  %>" id="<%=idPwd %>" value="<%= lockValue %>" <%=lockDisabled %>>
			                        													  <input type="hidden" name="<%= hiddenLockName  %>" id="<%=hiddenLockId %>" value="<%= de.getValue()  %>">
			                        													<%
			                        							          				if (visualizzaComandiInternoInput) {
			                        								          				%>
			                        								          					<span id="<%=idPwdEditSpan %>" class="<%= spanComaniInternoInputClass %>">
			                        														  		<i id="<%=idPwdEdit %>" class="material-icons md-24" title="<%= Costanti.ICONA_EDIT_TOOLTIP %>"><%= Costanti.ICONA_EDIT %></i>
			                        														  		<i id="<%=idPwdViewInnerLock %>" class="material-icons md-24"><%= Costanti.ICON_VISIBILITY %></i>
			                        														  	</span>
			                        															<script type="text/javascript" nonce="<%= randomNonce %>">
			                        																$(document).ready(function(){
			                        																	
			                        																	<% 
		    			                        									      				if(visualizzaComandoEdit){
		    			                        									      				%>
		                        																			// nascondi icona modifica contentuto
			                        																        $('#<%=idPwdEdit%>').show();
																									    <% } else {
	                        																				   %>
				                        																		// nascondi icona modifica contentuto
				                        																        $('#<%=idPwdEdit%>').hide();
		                        																			   <%
																									    }
	                        																			%>
	                        																			
	                        																			<% 
		    			                        									      				if(visualizzaComandoEye){
		    			                        									      				%>
			    			                        									      				// icona visualizza contenuto visibile	
		                        																			$('#<%=idPwdViewInnerLock%>').show();
																									    <% } else {
	                        																				   %>
	                        																					// icona visualizza contenuto nascosta
				                        																		$('#<%=idPwdViewInnerLock%>').hide();
		                        																			   <%
																									    }
	                        																			%>
			                        																	
			                        																	$('#<%=idPwdEdit %>').click(function() {
			                        																		
			                        																		// Abilita l'input di tipo password
			                        																        $('#<%=idPwd %>').attr('disabled', false);
			                        																        <% 
			    			                        									      				if(!(visualizzaComandoEdit && visualizzaComandoEye)){
			    			                        									      				%>
				                        																        // Svuota l'input di tipo password
				                        																        $('#<%=idPwd %>').val('');
			                        																        <% } %>
			                        																        
			                        																        // convertire l'elemento in un text
			                        																        // toggle the type attribute
																											var x = document.getElementById("<%=idPwd %>");
			                        																        
			                        																        // eliminare il comando di edit
			                        																        $('#<%=idPwdEdit %>').remove();
			                        																        $('#<%=idPwdEditSpan%>').removeClass('span-password-eye-2').addClass('span-password-eye');
			                        																        
			                        																    	<% 
			    			                        									      				if(visualizzaIconLucchetto){
			    			                        									      				%>
				                        																        // sostituire l'icona lock con unlock
				                        																        $('#<%=idPwdLock%>').hide();
				                        																        $('#<%=idPwdLockOpen%>').show();
				                        																        // ripristino cursore con puntatore
				                        																        $('#<%=idPwdLockOpen%>').parent().removeClass('spanIconInfoBox-lock').addClass('spanIconInfoBox');
																											    
																											    // nascondere comandi copia
																											    $('#<%=idPwdCopyLock%>').hide();
																											    // nascondere comando visualizza
																											    $('#<%=idPwdViewLock%>').hide();
																										    <% } %>
																										    
																										    <% 
			    			                        									      				if(utilizzaInputPassword){
			    			                        									      				%>
			    			                        									      				 	x.type = "password";
				                        																        // visualizza icona visualizza contentuto
				                        																        $('#<%=idPwdViewInnerLock%>').show();
																										    <% } else { %>
																										    	x.type = "text";
																										    <% } %>
			                        																	});
			                        																	
			                        																	<% 
		    			                        									      				if(utilizzaInputPassword){
		    			                        									      				%>
			                        																		$('#<%=idPwdViewInnerLock %>').click(function() {
			                        																			
			                        																			// toggle the type attribute
			                        																			var x = document.getElementById("<%=idPwd %>");
			                        																			  if (x.type === "password") {
			                        																			    x.type = "text";
			                        																			  } else {
			                        																			    x.type = "password";
			                        																			  }
			                        			
			                        																			  // toggle the eye slash icon
			                        																		    var eyeIcon = $('#<%=idPwdViewInnerLock %>');
			                        																		    if (x.type === 'password') {
			                        																		        eyeIcon.html('<%= Costanti.ICON_VISIBILITY %>');
			                        																		    } else {
			                        																		        eyeIcon.html('<%= Costanti.ICON_VISIBILITY_OFF %>');
			                        																		    }
			                        																		    
			                        																		});
		                        																		 <% } %>
			                        																	
			                        																});
			                        															</script>
			                        							     					<% 
			                        								   					}
			                        									      		
			                        									      			String idDivIconInfo = "divIconInfo_"+i;
			                        									      			String idIconInfo = "iconInfo_"+i; 
			                        									      			
			                        									      			if(visualizzaIconLucchetto || visualizzaInformazioniCifrate || deInfo != null){
			                        											      	%> 	<div class="iconInfoBox" id="<%=idDivIconInfo %>">
			                        											      	
			                        											      	<% 
			                        									      				if(visualizzaIconLucchetto){
			                        									      					String chiamataEventoPostback = Costanti.POSTBACK_VIA_POST_FUNCTION_PREFIX;
			                        									      					chiamataEventoPostback+=Costanti.POSTBACK_FUNCTION_WITH_PARAMETER_START;
			                        									      					chiamataEventoPostback+=de.getName();
			                        									      					chiamataEventoPostback+=Costanti.POSTBACK_FUNCTION_WITH_PARAMETER_END;
			                        									      				%>
			                        											      			<span class="spanIconInfoBox-lock">
			                        																<i class="material-icons md-24" id="<%=idPwdLockOpen %>" title="<%= Costanti.ICON_LOCK_OPEN_TOOLTIP %>" ><%=Costanti.ICON_LOCK_OPEN %></i>
			                        																<i class="material-icons md-24 md-nohover" id="<%=idPwdLock %>"><%=Costanti.ICON_LOCK %></i>
			                        															</span>
			                        															<script type="text/javascript" nonce="<%= randomNonce %>">
			                        																$(document).ready(function(){
			                        																	<% 
						                        									      				if(!visualizzaOpenLock){
						                        									      				%>
						                        									      				 	$('#<%=idPwdLock%>').show();
			                        																        $('#<%=idPwdLockOpen%>').hide();
			                        																     	// ripristino cursore senza puntatore
			                        																        $('#<%=idPwdLockOpen%>').parent().removeClass('spanIconInfoBox').addClass('spanIconInfoBox-lock');
						                        									      				<% } else { %>
						                        									      				 	$('#<%=idPwdLock%>').hide();
			                        																        $('#<%=idPwdLockOpen%>').show();
			                        																     	// ripristino cursore con puntatore
			                        																        $('#<%=idPwdLockOpen%>').parent().removeClass('spanIconInfoBox-lock').addClass('spanIconInfoBox');
						                        									      				<% } %>
						                        									      				
						                        									      				// lancio il postback
				                        																$('#<%=idPwdLockOpen %>').click(function() {
				                        																	<%= visualizzaAjaxStatus %><%= chiamataEventoPostback %>;
				                        																});
			                        																});
			                        															</script>
		                        															<% } %>
			                        															<% 
			                        									      				if(visualizzaInformazioniCifrate){
			                        									      				%>
			                        															<input type="hidden" name="__i_hidden_title_<%= idPwdCopyLock %>" id="hidden_title_<%= idPwdCopyLock %>"  value="<%= Costanti.TITOLO_FINESTRA_MODALE_COPIA_MESSAGE_WARNING %>"/>
			                        											      			<input type="hidden" name="__i_hidden_body_<%= idPwdCopyLock %>" id="hidden_body_<%= idPwdCopyLock %>"  value="<%= dePwd.getLockWarningMessage() %>"/>
			                        											      			<input type="hidden" name="__i_hidden_url_<%= idPwdCopyLock %>" id="hidden_url_<%= idPwdCopyLock %>"  value="<%= de.getUrl() %>"/>
			                        															<span class="spanIconInfoBox-copyLock">
			                        																<i class="material-icons md-24" id="<%=idPwdCopyLock %>" title="<%= Costanti.ICONA_COPY_LOCK_TOOLTIP %>"><%= Costanti.ICON_COPY %></i>
			                        															</span>
			                        															
																								<input type="hidden" name="__i_hidden_title_<%= idPwdViewLock %>" id="hidden_title_<%= idPwdViewLock %>"  value="<%= Costanti.TITOLO_FINESTRA_MODALE_VISUALIZZA_MESSAGE_WARNING %>"/>
			                        											      			<input type="hidden" name="__i_hidden_body_<%= idPwdViewLock %>" id="hidden_body_<%= idPwdViewLock %>"  value="<%= dePwd.getLockWarningMessage() %>"/>
			                        											      			<input type="hidden" name="__i_hidden_url_<%= idPwdViewLock %>" id="hidden_url_<%= idPwdViewLock %>"  value="<%= de.getUrl() %>"/>
			                        													      	<span class="spanIconInfoBox-viewLock">
			                        																<i class="material-symbols-outlined md-24" id="<%=idPwdViewLock %>" title="<%= Costanti.ICONA_VISIBILITY_LOCK_TOOLTIP %>"><%= Costanti.ICON_VISIBILITY_LOCK %></i>
			                        															</span>			                        															
			                        														<% } %>	
			                        											      			                        											      	
			                        											      		<% 
			                        									      				if(deInfo != null){
			                        									      				%>
			                        											      			<input type="hidden" name="__i_hidden_title_<%= idIconInfo %>" id="hidden_title_<%= idIconInfo %>"  value="<%= deInfo.getHeaderFinestraModale() %>"/>
			                        											      			<input type="hidden" name="__i_hidden_body_<%= idIconInfo %>" id="hidden_body_<%= idIconInfo %>"  value="<%= deInfo.getBody() %>"/>
			                        													      	<span class="spanIconInfoBox">
			                        																<i class="material-icons md-24" id="<%=idIconInfo %>"><%= deInfo.getButtonIcon() %></i>
			                        															</span>
			                        															
			                        														<% } %>	
			                        														</div>
			                        											      	<% 
			                        							   					}
			                        						     					%>
			                        						     					<% if(!deNote.equals("")){ %>
			                        									      			<p class="note <%= labelStyleClass %>"><%=deNote %></p>
			                        									      		<% } %>
			                        		                        			</div>
			                        		                        			<%
			                        						   					}
			                        		                        		} else { // else lock
			                                                					//fineelementi
			                        		                        		} // end else lock
			                                                			} // end else interval number
			                                                		} // end else radio
			                                            		} // end else checkbox
		                                        			} // end else multi-select
		                                        		} // end else select
		                                    		} // end else file
		                                		} // end else button
		                            		} // end else textarea || textarea-noedit
		                        		} // end else crypt
		                    		} // end else number
	                    		} // end else textedit
	                		} // end else text
	            		} // end else link
	        		} // end else note
    		} // end else subtitle
    		} // end else title
    	} // end else hidden
	} // for

	if(subtitleOpen){ // se c'e' un subtitle aperto ed ho finito gli elementi devo chiudere
		%>
		</div>
		<%
	}
	if(fieldsetOpen){ // se c'e' un fieldset aperto ed ho finito gli elementi devo chiudere
		%>
			</div>
		</fieldset>
		<%
	}
	%>
	</td>
</tr>

<%

if (visualizzaBottoneLogin) {
	  %><tr class="buttonrow">
		  <td colspan="2">
		  	<div class="buttonrowform">
				<input id="loginBtn" type="submit" value="Login"/>
				<script type="text/javascript" nonce="<%= randomNonce %>">
					$(document).ready(function(){
						$('#loginBtn').click(function() {
							<%=Costanti.JS_FUNCTION_VISUALIZZA_AJAX_STATUS%>CheckDati();return false;
						});
					});
				</script>
			</div>
		  </td>
	  </tr>
	  <%
	}
else
if (pd.getMode().equals("view")) {
  %><tr class="buttonrow">
	  <td colspan="2">
	  	<div class="buttonrowform"><%
			  String [][] bottoni = pd.getBottoni();
			  if ((bottoni != null) && (bottoni.length > 0)) {
			    for (int i = 0; i < bottoni.length; i++) {
			    	String id = "azioneBtn_" + i;
			      %><input id="<%=id %>" type="button" value="<%= bottoni[i][0] %>"/>&nbsp;
			      	<script type="text/javascript" nonce="<%= randomNonce %>">
				      	 $(document).ready(function(){
								$('#<%=id %>').click(function() {
									<%=Costanti.JS_FUNCTION_VISUALIZZA_AJAX_STATUS%><%= bottoni[i][1] %>
								});
							});
					</script>
			      <%
			    }
			  } else {
			    %><input id="azioneBtn" type="button" value="Modifica" />
			      <script type="text/javascript" nonce="<%= randomNonce %>">
				      $(document).ready(function(){
							$('#azioneBtn').click(function() {
								<%=Costanti.JS_FUNCTION_VISUALIZZA_AJAX_STATUS%>EditPage();
							});
						});
				  </script>
			    <%
			  }
		  %></div>
	  </td>
  </tr>
  <%
} else {
  if (pd.getMode().equals("view-noeditbutton") || pd.getMode().equals("view-nobutton") ) {
    %><tr class="buttonrownobuttons">
    	<td colspan="2" >&nbsp;</td>
    </tr><%
  } else {
    %><tr class="buttonrow">
	    <td colspan="2" >
	    	<div class="buttonrowform"><%
			  String [][] bottoni = pd.getBottoni();
			  if ((bottoni != null) && (bottoni.length > 0)) {
			    for (int i = 0; i < bottoni.length; i++) {
			    	String id = "azioneBtn_" + i;
			      %><input id="<%=id %>" type="submit" value="<%= bottoni[i][0] %>"/>&nbsp;
			      <script type="text/javascript" nonce="<%= randomNonce %>">
				      $(document).ready(function(){
							$('#<%=id %>').click(function() {
								<%=Costanti.JS_FUNCTION_VISUALIZZA_AJAX_STATUS%><%= bottoni[i][1] %>
							});
						});
				  </script>
				  <%
			    }
			  } else {
				  String visualizzaAjax = pd.isShowAjaxStatusBottoneInvia() ? Costanti.JS_FUNCTION_VISUALIZZA_AJAX_STATUS : "";
				  %><input id="azioneBtn" type="submit" value="<%=pd.getLabelBottoneInvia() %>" />
				  	<script type="text/javascript" nonce="<%= randomNonce %>">
						  	$(document).ready(function(){
								$('#azioneBtn').click(function() {
									<%=visualizzaAjax%>CheckDati();return false;
								});
							});
					</script>
				  <%
			  }
		  %></div>
	    </td>
    </tr><%
  }
}
%>

</table>
</div>
</td>
</tr>
</table>
</form>
</td>

