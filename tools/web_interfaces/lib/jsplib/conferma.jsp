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
<%@page import="java.util.*,org.openspcoop2.web.lib.mvc.*" session="true"%>



<%
String params = (String) request.getAttribute(Costanti.PARAMETER_NAME_PARAMS);
if(params == null) params="";
GeneralData gd = ServletUtils.getObjectFromSession(request, session, GeneralData.class, Costanti.SESSION_ATTRIBUTE_GENERAL_DATA);
PageData pd = ServletUtils.getObjectFromSession(request, session, PageData.class, Costanti.SESSION_ATTRIBUTE_PAGE_DATA);

String message = pd.getMessage();
String messageType = pd.getMessageType();
String [][] bottoni = pd.getBottoni();
String messageTitle = pd.getMessageTitle();
String tabSessionKey = ServletUtils.getTabIdFromRequestAttribute(request);
String randomNonce = (String) request.getAttribute(Costanti.REQUEST_ATTRIBUTE_CSP_RANDOM_NONCE);
// messaggio in cima alla pagina solo se non e' un messaggio di conferma o una copykey
if (!message.equals("") && messageType.equals(MessageType.CONFIRM.toString())) {
%>
  	<div id="confermaModal" title="<%=messageTitle%>">
  		<div id="confermaModalBody" class="contenutoModal">
  			<%=message%>
 		</div>
	</div>
	<script type="text/javascript" nonce="<%= randomNonce %>">
	 $(document).ready(function(){
		 	if($( "#confermaModal" ).length > 0){
		 		$( "#confermaModal" ).dialog({
		 	      resizable: false,
		 	     dialogClass: "no-close",
		 	     autoOpen: true,
		 	     height: "auto",
		 	     width: "auto",
		 	     modal: true,
		 	     buttons: {
		 	    	<%if ((bottoni != null) && (bottoni.length > 0)) {
						  for (int i = 0; i < bottoni.length; i++) {
							if(i > 0) {%>, <%}%>'<%=bottoni[i][0]%>' : function() {
						    	<%=Costanti.JS_FUNCTION_VISUALIZZA_AJAX_STATUS%>
						    	<%=bottoni[i][1]%>;
						    	 $( this ).dialog( "close" );
			 	 	        }
						    <%}
						}%>
		 	      }
		 	    });
		 	}
	 });
	</script>
	<SCRIPT type="text/javascript" nonce="<%= randomNonce %>">
		var nomeServlet_Custom_Ok = '';
		var nomeServlet_Custom_No = '';
		
		function generaUrl() {
		    var params = '';
		    
		    for (var k=0; k<document.form.elements.length; k++) {
				var nome = document.form.elements[k].name;
				if (nome && nome.length > 0 && nome != "idhid") {
				    var tipo = document.form.elements[k].type;
				    var valore = "";
				    if ( tipo == "hidden"){
						valore = document.form.elements[k].value;
						params += "&" + nome + "=" + valore;
			    	}
			    }
		    }
		    
			return params;   
		}
		    
	</SCRIPT>
	<jsp:include page="/jsp/confermaCustom.jsp" flush="true" />
	<SCRIPT type="text/javascript" nonce="<%= randomNonce %>">
		var params;
		
		function EseguiOp() {
			console.log('Esegui ' + document.form.elements['actionConfirm'].value);
			params = generaUrl();
			
			if(document.form.elements['actionConfirm']) {
				// evito di mandare indietro al server il valore degli elementi hidden che si utilizzano per la creazione delle finestre DialogInfo.
				  for (var k=0; k<document.form.elements.length; k++) {
						var nome = document.form.elements[k].name;
						var hiddenInfo = nome!=null ? nome.indexOf("__i_hidden") : -1;

						if(hiddenInfo > -1) {
							document.form.elements[k].value = '';
						}
				  }
				
				// aggiungo parametro idTab
			  if(tabValue != ''){
			  	addHidden(document.form, tabSessionKey , tabValue);
			  	addHidden(document.form, prevTabSessionKey , tabValue);
			  }
				
			  addHidden(document.form, '<%=Costanti.PARAMETRO_AZIONE %>' , 'conferma');
			  
			  //aggiungo parametro csfr
			  //if(csrfToken != ''){
			  //	addHidden(document.form, csrfTokenKey , csrfToken);
			  //}
				
				document.form.submit();
			} else {
				var destinazione = '<%=request.getContextPath()%>/'+nomeServlet_Custom_Ok +params;
				//addTabID
				destinazione = addTabIdParam(destinazione,true);
				
				destinazione = addParamToURL(destinazione, '<%=Costanti.PARAMETRO_AZIONE %>' , 'conferma');
				//aggiungo parametro csfr
				if(csrfToken != ''){
				  destinazione = addParamToURL(destinazione, csrfTokenKey , csrfToken);
				}
				
				document.location = destinazione;
			}
		};
		
		function Annulla() {
			console.log('Annulla ' + document.form.elements['actionConfirm'].value);
			params = generaUrl();
			
			if(document.form.elements['actionConfirm']) {
				// evito di mandare indietro al server il valore degli elementi hidden che si utilizzano per la creazione delle finestre DialogInfo.
				  for (var k=0; k<document.form.elements.length; k++) {
						var nome = document.form.elements[k].name;
						var hiddenInfo = nome!=null ? nome.indexOf("__i_hidden") : -1;

						if(hiddenInfo > -1) {
							document.form.elements[k].value = '';
						}
				  }
				
				// aggiungo parametro idTab
				  if(tabValue != ''){
				  	addHidden(document.form, tabSessionKey , tabValue);
				  	addHidden(document.form, prevTabSessionKey , tabValue);
				  }
				
				  addHidden(document.form, '<%=Costanti.PARAMETRO_AZIONE %>' , 'annulla');
				  
				  //aggiungo parametro csfr
				  //if(csrfToken != ''){
				  //	addHidden(document.form, csrfTokenKey , csrfToken);
				  //}
				
				document.form.submit();
			} else {
				var destinazione = '<%=request.getContextPath()%>/'+nomeServlet_Custom_No +params;
				//addTabID
				destinazione = addTabIdParam(destinazione,true);
				
				destinazione = addParamToURL(destinazione, '<%=Costanti.PARAMETRO_AZIONE %>' , 'annulla');
				//aggiungo parametro csfr
				if(csrfToken != ''){
				  destinazione = addParamToURL(destinazione, csrfTokenKey , csrfToken);
				}
				
				document.location = destinazione;
			}
		};
		
	</SCRIPT>
<%
	}
%>

<%
	// messaggio in cima alla pagina solo se non e' un messaggio di conferma
if (!message.equals("") && messageType.equals(MessageType.DIALOG.toString())) {
	Dialog finestraDialog = pd.getDialog();
	String titolo = finestraDialog.getTitolo();
	String icona = finestraDialog.getIcona();
	String header1 = finestraDialog.getHeaderRiga1();
	String header2 = finestraDialog.getHeaderRiga2();
	String nota = finestraDialog.getNotaFinale();
	List<BodyElement> body = finestraDialog.getBody();
	
	String classSpanNoEdit="spanNoEdit";
	String classDivNoEdit="divNoEdit";
%>
  	<div id="finestraDialogModal" title="<%= titolo %>">
  		<% if(!"".equals(icona) || !"".equals(header1) || !"".equals(header2)) { %>
	  		<div id="finestraDialogModalHeader" class="finestraDialogModalHeader">
	  			<% if(!"".equals(icona)) { %>
		  		<div id="finestraDialogModalHeaderSx" class="finestraDialogModalHeaderSx">
			  		<span class="icon-box">
						<i class="material-icons md-48"><%= icona %></i>
					</span>
				</div>
				<% }%>
				<% if(!"".equals(header1) || !"".equals(header2)) { %>
				<div id="finestraDialogModalHeaderDx" class="finestraDialogModalHeaderDx">
					<% if(!"".equals(header1)) { %>
		  			<div id="finestraDialogModalHeaderDxRiga1" class="finestraDialogModalHeaderDxRiga1">
			  			<span class="finestraDialogModalHeaderDxRiga1"><%=header1 %></span>	
					</div>
					<%  }%>
					<% if(!"".equals(header2)) {%>
					<div id="finestraDialogModalHeaderDxRiga2" class="finestraDialogModalHeaderDxRiga2">
			  			<span class="finestraDialogModalHeaderDxRiga2"><%=header2 %></span>
					</div>
					<% } %>
				</div>
				<% } %>	
			</div>
		<% } %>	
  		<div id="finestraDialogModalBody" class="finestraDialogModal">
			<%
				for(int i = 0; i < body.size(); i++){
					BodyElement de = body.get(i);
					String deLabel = de.getLabel();
					String type = de.getType();
					String deValue = de.getValue();
					String deName =  "be_name_"+i;
					String deLabelId = "be_label_"+i;
					String labelStyleClass= de.getLabelStyleClass();
					String classInput= de.getStyleClass();
					String iconaCtrlC = Costanti.ICON_COPY;
					String iconaCtrlCTitle = (de.getTooltipCopyAction()!=null && !de.getTooltipCopyAction().equals("")) ? ("title='"+de.getTooltipCopyAction()+"'") : " ";
					
					boolean visualizzaIconCopia = de.isVisualizzaCopyAction();
					if (type.equals("text")){
           				String textValNoEdit = de.getValue() != null && !de.getValue().equals("") ? de.getValue() : (pd.getMode().equals("view-noeditbutton") ? "&nbsp;" : "not defined");
           				%>
               			<div class="propDialog">
               				<label class="<%= labelStyleClass %>" id="<%=deLabelId %>" ><%=deLabel %></label>
               				<div class="<%=classDivNoEdit %>"> 
                				<span class="<%=classSpanNoEdit %>"><%= textValNoEdit %></span>
                				<input type="hidden" name="<%= deName %>" value="<%= de.getValue() %>"/>
                			</div>
                			<% 
					      		if(visualizzaIconCopia){
					      			String idDivIconInfo = "divIconInfo_"+i;
					      			String idIconCopy = "iconCopy_"+i; 
					      			String titleIconaCopia = "";
					      	%> 	<div class="iconCopyBox" id="<%=idDivIconInfo %>">
					      			<input type="hidden" name="__i_hidden_value_<%= idIconCopy %>" id="hidden_value_<%= idIconCopy %>"  value="<%= de.getValue() %>"/>
							      	<span class="spanIconCopyBox" <%= iconaCtrlCTitle %> >
										<i class="material-icons md-18" id="<%=idIconCopy %>"><%= iconaCtrlC %></i>
									</span>
								</div>
					      	<% } %>
               			</div>
               			<%
               		} else { // else text
               			if (type.equals("textedit")){
               				%>
                   			<div class="propDialog">
                   				<label class="<%= labelStyleClass %>" id="<%=deLabelId %>"><%=deLabel %></label>
                   				<%
                   					String textNoEdit = " disabled ";
						      		%><input type="text" name="<%= deName %>" value="<%= de.getValue() %>" class="<%= classInput %>" <%=textNoEdit %> >
						      	<% 
						      		if(visualizzaIconCopia){
						      			String idDivIconInfo = "divIconInfo_"+i;
						      			String idIconCopy = "iconCopy_"+i; 
						      	%> 	<div class="iconCopyBox" id="<%=idDivIconInfo %>">
						      			<input type="hidden" name="__i_hidden_value_<%= idIconCopy %>" id="hidden_value_<%= idIconCopy %>"  value="<%= de.getValue() %>"/>
								      	<span class="spanIconCopyBox" <%= iconaCtrlCTitle %>>
											<i class="material-icons md-18" id="<%=idIconCopy %>"><%= iconaCtrlC %></i>
										</span>
									</div>
						      	<% } 
						      	%>
                   			</div>
                   			<%
                   		} else { // else textedit
                   			if (type.equals("textarea") || type.equals("textarea-noedit")){
                   				String inputId = "txtA" + i;
       	     					if (type.equals("textarea-noedit")){
       								inputId = "txtA_ne" + i; 
       	     					}
                   				%>
                       			<div class="propDialog">
                       				<label class="<%= labelStyleClass %>" id="<%=deLabelId %>"><%=deLabel %></label>
                       				<%
		     						String taNoEdit = " readonly ";
		     						%><div class="txtA_div_propDialog">
		     							<textarea id="<%=inputId %>" <%=taNoEdit %> rows='<%= de.getRows() %>' cols='' name="<%= deName  %>" class="<%= classInput %> textAreaNoResize"><%= de.getValue() %></textarea>
		     							<% 
								      		if(visualizzaIconCopia){
								      			String idDivIconInfo = "divIconInfo_"+i;
								      			String idIconCopy = "iconCopy_"+i; 
								      	%> 	<div class="iconCopyBox" id="<%=idDivIconInfo %>">
								      			<input type="hidden" name="__i_hidden_value_<%= idIconCopy %>" id="hidden_value_<%= idIconCopy %>"  value="<%= de.getValue() %>"/>
										      	<span class="spanIconCopyBox" <%= iconaCtrlCTitle %>>
													<i class="material-icons md-18" id="<%=idIconCopy %>"><%= iconaCtrlC %></i>
												</span>
											</div>
								      	<% } %>
	     							</div><%
								%>
                       			</div>
                       			<%
                       		} else { // else textarea || textarea-noedit	
                       			//fineelementi
                       		} // end else textarea || textarea-noedit
                   		} // end else textedit
               		} // end else text
				} // end for
			%>
  		</div>
  		<% if(!"".equals(nota)) { %>
			<div id="finestraDialogModalBodyNota" class="finestraDialogModalNota">
				<span class="finestraDialogModalBodyNota"><%=nota %></span>
			</div>
		<% } %>	
	</div>
	<script type="text/javascript" nonce="<%= randomNonce %>">
	 $(document).ready(function(){
		 	if($( "#finestraDialogModal" ).length > 0){
		 		$( "#finestraDialogModal" ).dialog({
		 	      resizable: false,
		 	     dialogClass: "no-close",
		 	     autoOpen: true,
		 	     height: "auto",
		 	     width: "660px",
		 	     modal: true,
		 	     buttons: {
		 	    	<% 
		 	    	if ((bottoni != null) && (bottoni.length > 0)) {
						  for (int i = 0; i < bottoni.length; i++) {
							  String funzione = bottoni[i][1] != null ? bottoni[i][1] : "";
							if(i > 0) {
								%>, <%	
							}
						    %>'<%= bottoni[i][0] %>' : function() {
						    	<% if(!"".equals(funzione)) { %>
						    		<%= Costanti.JS_FUNCTION_VISUALIZZA_AJAX_STATUS %>
						    		<%= bottoni[i][1] %>;
						    	<% }%>
						    	 $( this ).dialog( "close" );
			 	 	        }
						    <%
						  }
						}
		 	    	 %>
		 	      }
		 	    });
		 	}
		 	
		 	if($(".spanIconCopyBox").length>0){

		 		$(".spanIconCopyBox").click(function(evt){
        			var iconCopyBoxId = $(this).parent().attr('id');
        			var idx = iconCopyBoxId.substring(iconCopyBoxId.indexOf("_")+1);
        			// console.log(idx);
        			if(idx) {
        				var valueToCopy = $("#hidden_value_iconCopy_"+ idx).val();
        				var copiatoOK = copyTextToClipboard(valueToCopy);
        				
        				if(copiatoOK) {
        					showTooltip(evt);
        				}
        			}
    			});
        		
		 		$(".spanIconCopyBox").mouseout(function(){
        			$('div.copyTooltip').remove();  
        		});
        	}
	 });
	 
	</script>
<%
}
%>

<script type="text/javascript" nonce="<%= randomNonce %>">
	$(document).ready(function(){
	 	
	// copia contenuto della modale secret
  	if($("#iconCopy_dec").length>0){
		$("#iconCopy_dec").click(function(evt){
 				var valueToCopy = $("#txtA_ne_dec").val();
 				var copiatoOK = copyTextToClipboard(valueToCopy);
 				
 				if(copiatoOK) {
 					showTooltip(evt);
 				}
			});
  		
		$("#iconCopy_dec").mouseout(function(){
  			$('div.copyTooltip').remove();  
  		});
  	}
  	
  	// visualizza secrets
  	if($(".spanIconInfoBox-viewLock").length>0){
  		$(".spanIconInfoBox-viewLock").click(function(){
  			var iconInfoBoxId = $(this).parent().attr('id');
  			var idx = iconInfoBoxId.substring(iconInfoBoxId.indexOf("_")+1);
  			console.log(idx);
  			if(idx) {
				var label = $("#hidden_title_pwd_"+ idx + "_lock_view").val();
				var body = $("#hidden_body_pwd_"+ idx+ "_lock_view").val();
				var url = $("#hidden_url_pwd_"+ idx+ "_lock_view").val();
				var valore = $("#__lk__pwd_"+ idx+ "").val();
				mostraInformazioniCifrateModal(label,body,url,valore);
  			}
			});
  	}
  	
  	// copia secrets
  	if($(".spanIconInfoBox-copyLock").length>0){
  		$(".spanIconInfoBox-copyLock").click(function(){
  			var iconInfoBoxId = $(this).parent().attr('id');
  			var idx = iconInfoBoxId.substring(iconInfoBoxId.indexOf("_")+1);
  			console.log(idx);
  			if(idx) {
  				var label = $("#hidden_title_pwd_"+ idx + "_lock_copy").val();
				var body = $("#hidden_body_pwd_"+ idx+ "_lock_copy").val();
				var url = $("#hidden_url_pwd_"+ idx+ "_lock_copy").val();
				var valore = $("#__lk__pwd_"+ idx+ "").val();
				mostraAlertInformazioniCifrateModal(label,body,url,valore);
  			}
			});
  	}
  	
  	// resize text area
  	var txtA_ne_dec_width = 0;
  	var txtA_ne_dec_height = 0;
  	if($("#txtA_ne_dec").length>0){
  		$("#txtA_ne_dec").mousedown(function(evt){
  			var ta = $(this);
  			
  			txtA_ne_dec_width = ta.width();
  				txtA_ne_dec_height= ta.height();
  		});
  		
		$("#txtA_ne_dec").mouseup(function(evt){
			var ta = $(this);
			
			if(ta.width() != txtA_ne_dec_width || ta.height() != txtA_ne_dec_height){
			    //do Something
			    console.log('resized');
			 // ripristino ombreggiatura
// 				$("#visualizzaInformazioniCifrateModal").dialog("close");
// 				$("#visualizzaInformazioniCifrateModal").dialog("open");
			  }
		});
  	}
});
	
	function mostraDataElementInfoModal(title,body){
		$("#dataElementInfoModal").prev().children('span').text(title);
		$("#dataElementInfoModalBody").html(body);
		$("#dataElementInfoModal").dialog("open");
	}
	
	function mostraInformazioniCifrateModal(title,body,url,valore){
		setValoriLock(url,valore);
		
		$("#txtA_ne_dec").val('');
		$("#txtA_ne_dec").attr('style','');		
		$("#iconCopy_dec").hide();
		$("#txtA_ne_dec").hide();
		$("#visualizzaInformazioniCifrateModalPropNota").show();
		$("#visualizzaInformazioniCifrateModal").next().show();
		
		$("#visualizzaInformazioniCifrateModal").prev().children('span').text(title);
		$("#visualizzaInformazioniCifrateModalBodyNotaSpan").html(body);
		$("#visualizzaInformazioniCifrateModal").dialog("open");
	}
		
	function mostraAlertInformazioniCifrateModal(title,body,url,valore){
		setValoriLock(url,valore);
		
		$("#alertInformazioniCifrateModalHeader").hide();
		$("#alertInformazioniCifrateModal").prev().children('span').text(title);
		$("#alertInformazioniCifrateModalBodyNotaSpan").html(body);
		$("#alertInformazioniCifrateModal").dialog("open");
	}
	
	function mostraDownloadInformazioniCifrateModal(title,body,url,azione){
		setValoriLock(url,'');
		
		$("#downloadInformazioniCifrateModal").prev().children('span').text(title);
		$("#downloadInformazioniCifrateModalBodyNotaSpan").html(body);
		$("#downloadInformazioniCifrateModal").next().children('button').text(azione);
		$("#downloadInformazioniCifrateModal").dialog("open");
	}
	
	function mostraErroreInformazioniCifrateModal(body){
		// imposto il messaggio ricevuto dal server
		$("#erroreInformazioniCifrateModalHeaderDxRiga1Span").html(body);
		$("#erroreInformazioniCifrateModal").dialog("open");
	}
	
	function mostraEsitoOperazioneAjaxModal(data){
		var esito = data.<%=Costanti.KEY_ESITO_JSON %>;
		var dettaglio = data.<%=Costanti.KEY_DETTAGLIO_ESITO_JSON %>;
		
		if(esito == '<%= MessageType.ERROR.toString() %>'){
			$("#operazioneAjaxModalHeaderSxIconOk").hide();
			$("#operazioneAjaxModalHeaderSxIconKo").show();
			$("#operazioneAjaxModal").prev().children('span').text('<%= Costanti.TITOLO_FINESTRA_MODALE_COPIA_MESSAGE_WARNING %>');
		} else {
			$("#operazioneAjaxModalHeaderSxIconOk").show();
			$("#operazioneAjaxModalHeaderSxIconKo").hide();
			$("#operazioneAjaxModal").prev().children('span').text('<%= Costanti.MESSAGE_TYPE_INFO_TITLE %>');
		}
		
		// imposto il messaggio ricevuto dal server
		$("#operazioneAjaxModalHeaderDxRiga1Span").html(dettaglio);
		$("#operazioneAjaxModal").dialog("open");
	}
	
</script>



<div id="dataElementInfoModal" title="Info">
	<div id="dataElementInfoModalBody" class="contenutoModal"></div>
</div>
<div id="visualizzaInformazioniCifrateModal" title="Visualizza Informazioni Cifrate">
	<div id="visualizzaInformazioniCifrateModalBody" class="contenutoModal">
		<div class="propDialog">
			<div class="txtA_div_propDialog_dec">
				<textarea id="txtA_ne_dec" readonly rows="3" cols="62" name="txtA_ne_dec" class="inputLinkLong"></textarea>
			 	<div class="iconCopyBox" id="divIconInfo_dec">
	      			<input type="hidden" name="__i_hidden_value_iconCopy_dec" id="hidden_value_iconCopy_dec"  value=""/>
			      	<span class="spanIconCopyBox" title="Copia">
						<i class="material-icons md-18" id="iconCopy_dec"><%= Costanti.ICON_COPY %></i>
					</span>
				</div>
			</div>
		</div>
	</div>
	<div class="propDialog" id="visualizzaInformazioniCifrateModalPropNota">
		<div id="visualizzaInformazioniCifrateModalBodyNota" class="finestraDialogModalNota">
			<span class="finestraDialogModalBodyNota" id="visualizzaInformazioniCifrateModalBodyNotaSpan"></span>
		</div>
	</div>
</div>
<div id="alertInformazioniCifrateModal" title="Attenzione">
	<div id="alertInformazioniCifrateModalBody" class="contenutoModal">
		<div class="propDialog">
			<div id="alertInformazioniCifrateModalBodyNota" class="finestraDialogModalNota">
				<span class="finestraDialogModalBodyNota" id="alertInformazioniCifrateModalBodyNotaSpan"></span>
			</div>
		</div>
	</div>
</div>
<div id="downloadInformazioniCifrateModal" title="Attenzione">
	<div id="downloadInformazioniCifrateModalBody" class="contenutoModal">
		<div class="propDialog">
			<div id="downloadInformazioniCifrateModalBodyNota" class="finestraDialogModalNota">
				<span class="finestraDialogModalBodyNota" id="downloadInformazioniCifrateModalBodyNotaSpan"></span>
			</div>
		</div>
	</div>
</div>
<div id="erroreInformazioniCifrateModal" title="Attenzione">
	<div id="erroreInformazioniCifrateModalHeader" class="finestraDialogModalHeader">
  		<div id="erroreInformazioniCifrateModalHeaderSx" class="finestraDialogModalHeaderSx">
	  		<span class="icon-box">
				<i class="material-icons md-48"><%= Costanti.ICON_DIALOG_HEADER %></i>
			</span>
		</div>
		<div id="erroreInformazioniCifrateModalHeaderDx" class="finestraDialogModalHeaderDx">
  			<div id="erroreInformazioniCifrateModalHeaderDxRiga1" class="finestraDialogModalHeaderDxRiga1">
	  			<span class="" id="erroreInformazioniCifrateModalHeaderDxRiga1Span"></span>	
			</div>
		</div>
	</div>
</div>
<div id="operazioneAjaxModal" title="Attenzione">
	<div id="operazioneAjaxModalHeader" class="finestraDialogModalHeader">
  		<div id="operazioneAjaxModalHeaderSx" class="finestraDialogModalHeaderSx">
	  		<span class="icon-box">
				<i id="operazioneAjaxModalHeaderSxIconKo" class="material-icons md-48"><%= Costanti.ICON_DIALOG_HEADER %></i>
				<i id="operazioneAjaxModalHeaderSxIconOk" class="material-icons md-48"><%= Costanti.INFO_BUTTON_ICON_WHITE %></i>
			</span>
		</div>
		<div id="operazioneAjaxModalHeaderDx" class="finestraDialogModalHeaderDx">
  			<div id="operazioneAjaxModalHeaderDxRiga1" class="finestraDialogModalHeaderDxRiga1">
	  			<span class="" id="operazioneAjaxModalHeaderDxRiga1Span"></span>	
			</div>
		</div>
	</div>
</div>


