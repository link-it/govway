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



<%@page import="org.openspcoop2.web.lib.mvc.Dialog.BodyElement"%>
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
Vector<DataElement> vectorCheckBox = new Vector<DataElement>();
Vector<DataElement> vectorTags = new Vector<DataElement>();

for (int j = 0; j < riga.size(); j++) {
    DataElement de = (DataElement) riga.elementAt(j);
    
    if (de.getType().equals("image")) {
    	vectorImmagini.add(de);
   	} else  if (de.getType().equals("checkbox")) {
   		vectorCheckBox.add(de);
    } else if (de.getType().equals("button")) {
    	vectorTags.add(de);
    } else{
    	vectorRiepilogo.add(de);
    }
}
%>
<% if(vectorCheckBox.size() > 0){
		DataElement de = (DataElement) vectorCheckBox.elementAt(0);
		
		String statusType = de.getStatusTypes() != null && de.getStatusTypes().length>0 ? de.getStatusTypes()[0] : "";	
		String image = "status_red.png";
	 	if("yes".equals(statusType)){
	 		image = "status_green.png";
		}
		else if("warn".equals(statusType)){
			image = "status_yellow.png";
		}
		else if("off".equals(statusType)){
			image = "disconnected_grey.png";
		}
		else if("config_enable".equals(statusType)){
			image = "verified_green.png";
		}
		else if("config_warning".equals(statusType)){
			image = "verified_yellow.png";
		}
		else if("config_error".equals(statusType)){
			image = "verified_red.png";
		}
	 	else if("config_disable".equals(statusType)){
	 		image = "verified_grey.png";
		}

	 	String statusTooltip = de.getStatusToolTips() != null && de.getStatusToolTips().length>0 ? de.getStatusToolTips()[0] : "";		
		String statusTooltipTitleAttribute = statusTooltip != null && !statusTooltip.equals("") ? " title=\"" + statusTooltip + "\"" : "";
		 
	%>
	<td class="tdText" style="<%= de.getWidth() %>">
		<div id="stato_<%=numeroEntryS %>">
 			<span class="statoErogazioneIcon" id="iconConfigurazione_<%=numeroEntryS %>">
				<img src="images/tema_link/<%= image %>" <%= statusTooltipTitleAttribute %>/>
			</span>
		</div>
	</td>
<% } %>
<td>
	<div id="entry_<%=numeroEntryS %>" class="entryErogazione">
			<% 
				DataElement deTitolo = (DataElement) vectorRiepilogo.elementAt(0);
				String deTitoloName = "url_entry_"+numeroEntry;
				String deTitoloValue = !deTitolo.getValue().equals("") ? deTitolo.getValue() : "&nbsp;";
				String visualizzaAjaxStatus = deTitolo.isShowAjaxStatus() ? Costanti.JS_FUNCTION_VISUALIZZA_AJAX_STATUS : "";
				%><input id="<%= deTitoloName  %>" type="hidden" name="<%= deTitoloName  %>" value="<%= deTitolo.getUrl()  %>"/>
				
					<script type="text/javascript">
					   $('[id=entry_<%=numeroEntryS %>]')
					   .click(function() {
						   		<%= visualizzaAjaxStatus %>
								var val = $(this).children('input[id=url_entry_<%=numeroEntryS %>]').val();
								window.location = val;
					       });
				   </script>
				<%
			%>
		<div id="titolo_<%=numeroEntryS %>" class="titoloEntry">
			<span class="titoloEntry"><%=deTitoloValue %>&nbsp;&nbsp;&nbsp;&nbsp;</span>	
			
			<% if(vectorTags.size() > 0){ %>
				<div id="titolo_<%=numeroEntryS %>_tags" class="titoloTags">
					<% for(int z = 0; z < vectorTags.size(); z ++){ 
						DataElement tag = vectorTags.get(z);
					%>
						<span class="tag label label-info <%=tag.getStyleClass() %>"><%= tag.getLabel() %></span>
					<% } %>
				</div>
			<% } %>	
			
			<% if(vectorImmagini.size() > 0){
				%>
				<div id="titolo_<%=numeroEntryS %>_info" class="titoloInfo">
			<%
				for(int idxLink =0; idxLink < vectorImmagini.size() ; idxLink ++ ){
					DataElement de = (DataElement) vectorImmagini.elementAt(idxLink);
					String deTip = !de.getToolTip().equals("") ? " title=\"" + de.getToolTip() + "\"" : "";
					String classLink = "";
					
					if(de.getInfo() != null) {
						DataElementInfo deInfo = de.getInfo();
						String idDivIconInfo = "divIconInfo_"+numeroEntry;
						String idIconInfo = "iconInfo_"+numeroEntry; 
						String idSpanInfo = "spanIconInfoBoxList_"+numeroEntry;
						
						%>
						<div class="iconInfoBoxList" id="<%=idDivIconInfo %>" <%=deTip %> >
							<input type="hidden" name="__i_hidden_title_<%= idIconInfo %>" id="hidden_title_<%= idIconInfo %>"  value="<%= deInfo.getHeaderFinestraModale() %>"/>
		   					<input type="hidden" name="__i_hidden_body_<%= idIconInfo %>" id="hidden_body_<%= idIconInfo %>"  value="<%= deInfo.getBody() %>"/>
       						<span class="spanIconInfoBoxList" id="<%=idSpanInfo %>">
								<i class="material-icons md-18" id="<%=idIconInfo %>"><%= deInfo.getButtonIcon() %></i>
							</span>
       					</div>
						<script type="text/javascript">
						// info
				    	if($("#<%=idSpanInfo %>").length>0){
				    		$("#<%=idSpanInfo %>").click(function(e){
				    			var iconInfoBoxId = $(this).parent().attr('id');
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
						String idDivIconUso = "divIconUso_"+numeroEntry;
						String idIconUso = "iconUso_"+numeroEntry; 
						String idSpanUso = "spanIconUsoBoxList_"+numeroEntry;
						
						BodyElement urlElement = dialog.getBody().remove(0);
						
						request.setAttribute("idFinestraModale_"+numeroEntry, de.getDialog());
						
						String identificativoFinestraModale = "idFinestraModale_" + numeroEntry;
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
						<script type="text/javascript">
						// info
				    	if($("#<%=idSpanUso %>").length>0){
				    		$("#<%=idSpanUso %>").click(function(e){
				    			var iconInfoBoxId = $(this).parent().attr('id');
				    			var idx = iconInfoBoxId.substring(iconInfoBoxId.indexOf("_")+1);
				    			console.log(idx);
				    			if(idx) {
				    				var url = $("#hidden_title_iconUso_"+ idx).val();
				    				// chiamata al servizio
				    				<%=Costanti.JS_FUNCTION_VISUALIZZA_AJAX_STATUS %>
				    				
				    				$.ajax({
				    							url : url,
				    							method: 'GET',
				    							async : false,
				    							success: function(data, textStatus, jqXHR){
				    								// inserimento del valore nella text area
								    				$("textarea[id^='idFinestraModale_<%=numeroEntryS %>_txtA']").val(data);
								    				
								    				<%=Costanti.JS_FUNCTION_NASCONDI_AJAX_STATUS %>
								    				// apertura modale
								    				var idToOpen = '#' + 'idFinestraModale_<%= numeroEntry %>';
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
					%>
				<% 		
				}
			%></div> <% 
			}%>
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

