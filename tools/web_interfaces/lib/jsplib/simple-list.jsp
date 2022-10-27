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
	<form name="form" onSubmit ='return false;'>
	
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
		%><input type=hidden name=<%= key %> value=<%= value %>><%
	    }
	}
	%>

	<jsp:include page="/jsplib/titlelist.jsp" flush="true" />
	
	<table>
				
		<tr> 
			<td background="images/dothsx.gif" style="height:7px;" colspan="3" valign="top" class="bgbottomsx"></td>
		</tr>
		
		<tr> 
			<td valign=top>
				<table class="tabella">
				<tr>
				
				<%
				String [] labels = pd.getLabels();
				for (int i = 0; i < labels.length;i++) {
				  %><td class="table01header"><%= labels[i] %></td><%
				}
				if (pd.getInserisciBottoni()) {
				  %><td class="table01header" width="30px">Selected</td><%
				}
				
				%></tr><%
				
				//inizio entries
				Vector v = pd.getDati();
				String stile;
				for (int i = 0; i < v.size(); i++) {
					//per ogni entry:
					%><tr><%
				
					if ((i % 2) == 0) {
					  stile = "table01pari";
					} else {
					  stile = "table01dispari";
					}
				
					Vector e = (Vector) v.elementAt(i);
					for (int j = 0; j < labels.length; j++) {
						DataElement de = (DataElement) e.elementAt(j);
						
						if (!de.getType().equals("hidden")) {
						  %><td class="<%= stile %>"><%
						}
						
						if (de.getType().equals("text")) {
							if (!de.getUrl().equals("")) {
								if (!de.getTarget().equals("")) {
							    	//url+target
							    	%><a class="<%= stile %>" target="<%= de.getTarget() %>" href="<%=de.getUrl() %>"><%= de.getValue() %></a><%
							 	} else {
							   		//url only
							   		%><a class="<%= stile %>" href="<%= de.getUrl() %>"><%= de.getValue()	%></a><%
							  	}
							} else {
								String id = "table_link_" + i + "_" + j;
							  	//no url
								if (!de.getOnClick().equals("")) {
							    	//getOnClick
									%><a class="<%= stile %>" href=""><%=de.getValue() %></a>
										<script type="text/javascript" nonce="<%= randomNonce %>">
										      	 $(document).ready(function(){
														$('#<%=id %>').click(function() {
															<%= de.getOnClick() %>
														});
													});
											</script>
									<%
							 	} else {
							   		//string only
							   		%><%= de.getValue() %><%
							  	}
							}
						// fine de.gettype = 'text'	
						} else {
							// Tipo hidden
						  	if (de.getType().equals("hidden")) {
							    %><input type="hidden" name="<%= de.getName() %>" value="<%= de.getValue() %>"><%
				    		//  fine de.gettype = 'hidden'	
							} else {
								// tipo radio
						   		if (de.getType().equals("radio")) {
						
						       		String[] stValues = de.getValues();
						       		String[] stLabels = de.getLabels();
						
							       // Ciclo sulla lista di valori
									for (int r = 0; r < stValues.length; r++) {
						         		if (stValues[r].equals(de.getSelected())) {
						            		%><input type="radio" checked name='<%= de.getName() %>' value='<%= stValues[r] %>'>&nbsp;&nbsp;<%= stLabels[r] %><%
						         		} else {
						            		%><input type="radio" name='<%= de.getName() %>' value='<%= stValues[r] %>'>&nbsp;&nbsp;<%= stLabels[r] %><%
						         		}
						         		
						         		if (r<stValues.length-1) {
						           			%><br/><%
						             	}
						           } // fine form livsta valori radio
								} // fine if de.gettype = 'radio'	
							} // fine else de.gettype = 'hidden'	
					 	} // fine else de.gettype = 'text'	
						
						if (!de.getType().equals("hidden")) {
						    %></td><%
						}
					} // fine singola entry
					
					if (pd.getInserisciBottoni()) {
					  //checkbox remove
					  %><td class="<%= stile %>">
					  	<div align="center">
					  		<input type="checkbox" name="selectcheckbox" value="<%= i %>"/>
					  	</div>
					  </td><%
					}
					
					%></tr><%
				
				}//fine entries
				
				if (pd.getInserisciBottoni()) {
				%><tr>
					<td colspan="<%= labels.length+1 %>" class="table01footer">
						<div align="right">
				  			<input id="selTuttiBtn" type="button" value='Seleziona Tutti'/>
				  			<input id="deselTuttiBtn" type="button" value='Deseleziona Tutti'/>
				  			<%
				  			if (pd.getAddButton()) {
				    		%><input id="aggiungiBtn" type="button" value='Aggiungi'/><%
				  			}
				  			%><input id="rimuoviSelezionatiBtn" type="button" value='Rimuovi Selezionati'/>
				  			
				  			<script type="text/javascript" nonce="<%= randomNonce %>">
						      	 $(document).ready(function(){
						      			$('#selTuttiBtn').click(function() {
											SelectAll();
										});
										$('#deselTuttiBtn').click(function() {
											DeselectAll();
										});
										<%
							  			if (pd.getAddButton()) {
							    		%>
							    		$('#aggiungiBtn').click(function() {
							    			AddEntry();
										});
										<%
							  			}
							  			%>
							  			$('#rimuoviSelezionatiBtn').click(function() {
							  				RemoveEntries();
										});
									});
							</script>
				  			
				  		</div>
				  	</td>
				 </tr><%
				}
				
				%>
				
				</table>
			</td>
			<%
			Vector areaBottoni = pd.getAreaBottoni();
			if (areaBottoni != null) {
				%>
					<td valign="top"><img src="images/spacer.gif" style="width:10px; height:1px;"></td>
			  		<td class="table01dispari" valign="top" nowrap><%
					for (int i = 0; i < areaBottoni.size(); i++) {
			    		AreaBottoni area = (AreaBottoni) areaBottoni.elementAt(i);
			    		String title = area.getTitle();
			    		Vector bottoni = area.getBottoni();
			    		%><p><%
			    		if (!title.equals("")) {
			      			%><strong><%= title %></strong><br>
			      			<img src="images/dothdx.gif" style="width:80px; height:9px;"/><br/><%
			    		}
				
			    		for (int b = 0; b < bottoni.size(); b++) {
			      			DataElement bottone = (DataElement) bottoni.elementAt(b);
			      			String id = "areaBottonBtn_" + i + "_" + b;
			      			%>
			      			<input id="<%=id %>" type="button" value='&gt;'/>
			      			<em><%= bottone.getValue() %></em><br/>
			      			<script type="text/javascript" nonce="<%= randomNonce %>">
						      	 $(document).ready(function(){
										$('#<%=id %>').click(function() {
											<%= bottone.getOnClick() %>
										});
									});
							</script>
			      			<%
			    		}
			    		%></p><%
			  		}
			  	%></td><%
			}
			
			%>
		</tr>
	</table>
	<br>
</form>
</td>
