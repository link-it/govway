<%--
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it). 
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
	String params = (String) request.getAttribute("params");
if(params == null) params="";
GeneralData gd = (GeneralData) session.getAttribute("GeneralData");
PageData pd = (PageData) session.getAttribute("PageData");

String message = pd.getMessage();
String messageType = pd.getMessageType();
String [][] bottoni = pd.getBottoni();
String messageTitle = pd.getMessageTitle();

// messaggio in cima alla pagina solo se non e' un messaggio di conferma o una copykey
if (!message.equals("") && messageType.equals(MessageType.CONFIRM.toString())) {
%>
  	<div id="confermaModal" title="<%=messageTitle%>">
  		<div id="confermaModalBody" class="contenutoModal">
  			<%=message%>
 		</div>
	</div>
	<script>
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
	<SCRIPT>
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
	<SCRIPT type="text/javascript">
		var params;
		
		function EseguiOp() {
			console.log('Esegui ' + document.form.elements['actionConfirm'].value);
			params = generaUrl();
			
			if(document.form.elements['actionConfirm'])
				document.form.submit();
			else
		    	document.location='<%=request.getContextPath()%>/'+nomeServlet_Custom_Ok +params;
		};
		
		function Annulla() {
			console.log('Annulla ' + document.form.elements['actionConfirm'].value);
			params = generaUrl();
			
			if(document.form.elements['actionConfirm'])
				document.form.submit();
			else
		    	document.location='<%=request.getContextPath()%>/'+nomeServlet_Custom_No +params;
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
	<script>
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
		 		
		 		function changeTooltipPosition(event) {
		 			var tooltipX = event.pageX - 8;
		 			var tooltipY = event.pageY + 8;
		 			$('div.copyTooltip').css({top: tooltipY, left: tooltipX});
		 		};
		 		
		 		function showTooltip(event) {
		 			$('div.copyTooltip').remove();
		 			$('<div class="copyTooltip">Copiato</div>').appendTo('body');
		 			changeTooltipPosition(event);
		 		};
		 		
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
	 
	 
	 
	 function copyTextToClipboard(text) {
		  var textArea = document.createElement("textarea");

		  //
		  // *** This styling is an extra step which is likely not required. ***
		  //
		  // Why is it here? To ensure:
		  // 1. the element is able to have focus and selection.
		  // 2. if element was to flash render it has minimal visual impact.
		  // 3. less flakyness with selection and copying which **might** occur if
		  //    the textarea element is not visible.
		  //
		  // The likelihood is the element won't even render, not even a
		  // flash, so some of these are just precautions. However in
		  // Internet Explorer the element is visible whilst the popup
		  // box asking the user for permission for the web page to
		  // copy to the clipboard.
		  //

		  // Place in top-left corner of screen regardless of scroll position.
		  textArea.style.position = 'fixed';
		  textArea.style.top = 0;
		  textArea.style.left = 0;

		  // Ensure it has a small width and height. Setting to 1px / 1em
		  // doesn't work as this gives a negative w/h on some browsers.
		  textArea.style.width = '2em';
		  textArea.style.height = '2em';

		  // We don't need padding, reducing the size if it does flash render.
		  textArea.style.padding = 0;

		  // Clean up any borders.
		  textArea.style.border = 'none';
		  textArea.style.outline = 'none';
		  textArea.style.boxShadow = 'none';

		  // Avoid flash of white box if rendered for any reason.
		  textArea.style.background = 'transparent';


		  textArea.value = text;

		  document.body.appendChild(textArea);
		  textArea.focus();
		  textArea.select();

		  var successful = false;
		  try {
		    successful = document.execCommand('copy');
		    
		    if(successful) {
		    	console.log('Valore Copiato ' + text);
		    } else {
		    	console.log('Copia non effettuata');
		    }
		  } catch (err) {
			var successful = false;
// 		    console.log('Oops, unable to copy');
		  }

		  document.body.removeChild(textArea);
		  return successful;
		}
	</script>
<%
}
%>

