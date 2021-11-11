<%--
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it). 
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
%>
<td valign="top" class="td2PageBody">
	<form name="form" onSubmit ='return false;'>
	
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
							  	//no url
								if (!de.getOnClick().equals("")) {
							    	//onclick
									%><a class="<%= stile %>" href="" onClick="<%= de.getOnClick() %>"><%=de.getValue() %></a><%
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
				  			<input type="button" onClick="SelectAll()" value='Seleziona Tutti'/>
				  			<input type="button" onClick="DeselectAll()" value='Deseleziona Tutti'/>
				  			<%
				  			if (pd.getAddButton()) {
				    		%><input type="button" onClick="AddEntry()" value='Aggiungi'/><%
				  			}
				  			%><input type="button" onClick="RemoveEntries()" value='Rimuovi Selezionati'/>
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
			      			%>
			      			<input type="button" onClick="<%= bottone.getOnClick() %>" value='&gt;'/>
			      			<em><%= bottone.getValue() %></em><br/>
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
