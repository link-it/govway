<%--
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
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


Vector<?> v = pd.getDati();

String numeroEntryS = request.getParameter("numeroEntry");
int numeroEntry = Integer.parseInt(numeroEntryS);

Vector<?> riga = (Vector<?>) v.elementAt(numeroEntry);

Vector<DataElement> vectorRiepilogo = new Vector<DataElement>();
Vector<DataElement> vectorImmagini = new Vector<DataElement>();

for (int j = 0; j < riga.size(); j++) {
    DataElement de = (DataElement) riga.elementAt(j);
    
    if (de.getType().equals("image")) {
    	vectorImmagini.add(de);
    } else {
    	vectorRiepilogo.add(de);
    }
}
%>
<td>
	<div id="entry_<%=numeroEntryS %>" class="entryErogazione">
			<% 
				DataElement deTitolo = (DataElement) vectorRiepilogo.elementAt(0);
				String deTitoloName = "url_entry_"+numeroEntry;
				String deTitoloValue = !deTitolo.getValue().equals("") ? deTitolo.getValue() : "&nbsp;";
				%><input id="<%= deTitoloName  %>" type="hidden" name="<%= deTitoloName  %>" value="<%= deTitolo.getUrl()  %>"/>
				
					<script type="text/javascript">
					   $('[id=entry_<%=numeroEntryS %>]')
					   .click(function() {
								var val = $(this).children('input[id=url_entry_<%=numeroEntryS %>]').val();
								window.location = val;
					       });
				   </script>
				<%
			%>
		<div id="titolo_<%=numeroEntryS %>" class="titoloEntry">
			<span class="titoloEntry"><%=deTitoloValue %>&nbsp;&nbsp;&nbsp;&nbsp;</span>		
		</div>
		<% 
			DataElement deMetadati = (DataElement) vectorRiepilogo.elementAt(1);
			String deMetadatiValue = !deMetadati.getValue().equals("") ? deMetadati.getValue() : "&nbsp;";
			%>
		<div id="metadati_<%=numeroEntryS %>" class="metadatiEntry">
			<span class="metadatiEntry"><%=deMetadatiValue %>&nbsp;&nbsp;&nbsp;&nbsp;</span>
		</div>
	</div>
</td>
<% if(vectorImmagini.size() > 0){ %>
	<td>
		<div id="funzionalita_<%=numeroEntryS %>" class="funzionalitaErogazione">
			<% 
			
			for (int j = 0; j < vectorImmagini.size(); j++) {
			    DataElement de = (DataElement) vectorImmagini.elementAt(j);
			 	String deValue = de.getValue();
			    String deName = !de.getName().equals("") ? de.getName() : "de_name_"+j;
				int wCount = deValue.split(" ") != null ? deValue.split(" ").length : 1;
				String spanClass= wCount == 1 ? "configurazioneErogazioneSpanOneLine" : "configurazioneErogazioneSpan";			
			  	%>
			  		<div id="configurazione_<%=j %>" class="configurazioneErogazione" title="<%=deValue %>">
			  			<div class="configurazioneErogazioneIcon">
				  			<span class="configurazioneErogazioneIcon" id="iconConfigurazione_<%=j %>">
								<i class="material-icons md-18 md-light">&#xE5CA;</i>
							</span>
						</div>
						<div class="<%=spanClass %>">
			  				<span class="<%=spanClass %>" ><%=deValue %></span>
		  				</div>
			  		</div>
		  		<%
			  	
			} // for
			%>
		</div>
	</td>
<% } %>

