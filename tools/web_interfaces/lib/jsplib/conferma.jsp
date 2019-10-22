<%--
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it). 
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
<%@page import="java.util.*, org.openspcoop2.web.lib.mvc.*" session="true"%>



<%
String params = (String) request.getAttribute("params");
if(params == null) params="";
GeneralData gd = (GeneralData) session.getAttribute("GeneralData");
PageData pd = (PageData) session.getAttribute("PageData");

String message = pd.getMessage();
String messageType = pd.getMessageType();
String [][] bottoni = pd.getBottoni();

// messaggio in cima alla pagina solo se non e' un messaggio di conferma
if (!message.equals("") && messageType.equals(MessageType.CONFIRM.toString())) {
  %>
  	<div id="confermaModal" title="Conferma Operazione">
  		<div id="confermaModalBody" class="contenutoModal">
  			<%= message %>
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
		 	    	<% 
		 	    	if ((bottoni != null) && (bottoni.length > 0)) {
						  for (int i = 0; i < bottoni.length; i++) {
							if(i > 0) {
								%>, <%	
							}
						    %>'<%= bottoni[i][0] %>' : function() {
						    	visualizzaAjaxStatus();
						    	<%= bottoni[i][1] %>;
						    	 $( this ).dialog( "close" );
			 	 	        }
						    <%
						  }
						}
		 	    	 %>
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
				if (nome.length > 0 && nome != "idhid") {
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
		    	document.location='<%= request.getContextPath() %>/'+nomeServlet_Custom_Ok +params;
		};
		
		function Annulla() {
			console.log('Annulla ' + document.form.elements['actionConfirm'].value);
			params = generaUrl();
			
			if(document.form.elements['actionConfirm'])
				document.form.submit();
			else
		    	document.location='<%= request.getContextPath() %>/'+nomeServlet_Custom_No +params;
		};
		
	</SCRIPT>
<%
}
%>