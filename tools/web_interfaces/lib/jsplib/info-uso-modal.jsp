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

String idFinestraModale = request.getParameter("idFinestraModale");

Dialog finestraDialog = (Dialog) request.getAttribute(idFinestraModale);

	String titolo = finestraDialog.getTitolo();
	String icona = finestraDialog.getIcona();
	String header1 = finestraDialog.getHeaderRiga1();
	String header2 = finestraDialog.getHeaderRiga2();
	String nota = finestraDialog.getNotaFinale();
	List<BodyElement> body = finestraDialog.getBody();
	
	String classSpanNoEdit="spanNoEdit";
	String classDivNoEdit="divNoEdit";
%>
<div id="<%= idFinestraModale %>" title="<%= titolo %>">
	<% if(!"".equals(icona) || !"".equals(header1) || !"".equals(header2)) { %>
  		<div id="finestraDialogInfoUsoModalHeader" class="finestraDialogInfoUsoModalHeader">
  			<% if(!"".equals(icona)) { %>
	  		<div id="finestraDialogInfoUsoModalHeaderSx" class="finestraDialogInfoUsoModalHeaderSx">
		  		<span class="icon-box">
					<i class="material-icons md-36"><%= icona %></i>
				</span>
			</div>
			<% }%>
			<% if(!"".equals(header1) || !"".equals(header2)) { %>
			<div id="finestraDialogInfoUsoModalHeaderDx" class="finestraDialogInfoUsoModalHeaderDx">
				<% if(!"".equals(header1)) { %>
	  			<div id="finestraDialogInfoUsoModalHeaderDxRiga1" class="finestraDialogInfoUsoModalHeaderDxRiga1">
		  			<span class="finestraDialogInfoUsoModalHeaderDxRiga1"><%=header1 %></span>	
				</div>
				<%  }%>
				<% if(!"".equals(header2)) {%>
				<div id="finestraDialogInfoUsoModalHeaderDxRiga2" class="finestraDialogInfoUsoModalHeaderDxRiga2">
		  			<span class="finestraDialogInfoUsoModalHeaderDxRiga2"><%=header2 %></span>
				</div>
				<% } %>
			</div>
			<% } %>	
		</div>
	<% } %>	
 		<div id="finestraDialogInfoUsoModalBody" class="finestraDialogInfoUsoModal">
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
              			<div class="propDialogInfoUso">
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
                  			<div class="propDialogInfoUso">
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
                      			<div class="propDialogInfoUso">
                      				<label class="<%= labelStyleClass %>" id="<%=deLabelId %>"><%=deLabel %></label>
                      				<%
	     						String taNoEdit = " readonly ";
	     						%><div class="txtA_div_propDialogInfoUso">
	     							<textarea id="<%= idFinestraModale %>_<%=inputId %>" <%=taNoEdit %> rows='<%= de.getRows() %>' cols='' name="<%= deName  %>" class="<%= classInput %> txtA_propDialogInfoUsoNoResize"><%= de.getValue() %></textarea>
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
		<div id="finestraDialogInfoUsoModalBodyNota" class="finestraDialogInfoUsoModalNota">
			<span class="finestraDialogInfoUsoModalBodyNota"><%=nota %></span>
		</div>
	<% } %>	
</div>
<script>
var idModal = '#'+ '<%= idFinestraModale %>';
if($( idModal ).length > 0){
		$( idModal ).dialog({
	      resizable: false,
// 	     dialogClass: "no-close",
	     autoOpen: false,
	     height: "auto",
	     width: "660px",
	     modal: true
 	    });
 	}
</script>

