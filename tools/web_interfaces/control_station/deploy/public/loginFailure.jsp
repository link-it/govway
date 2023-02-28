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
<!DOCTYPE html>
<%@page import="org.openspcoop2.utils.transport.http.HttpConstants"%>
<%@page import="java.text.MessageFormat"%>
<%@page import="java.util.UUID"%>
<%@page import="org.openspcoop2.web.ctrlstat.servlet.login.LoginCostanti"%>
<%@page import="org.openspcoop2.web.ctrlstat.servlet.GeneralHelper"%>
<%@page import="org.apache.commons.lang3.StringUtils"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page session="true" import="java.util.Vector, org.openspcoop2.web.lib.mvc.*" %>
<%
String iddati = "";
String ct = request.getContentType();
if (ct != null && (ct.indexOf(Costanti.MULTIPART) != -1)) {
  iddati = ServletUtils.getObjectFromSession(request, session, String.class, Costanti.SESSION_ATTRIBUTE_ID_DATI);
} else {
  iddati = request.getParameter(Costanti.PARAMETER_NAME_ID_DATI);
}
String gdString = Costanti.SESSION_ATTRIBUTE_GENERAL_DATA;
if (iddati != null && !iddati.equals("notdefined")) {
  gdString += iddati;
}
else
  iddati = "notdefined";
GeneralData gd = ServletUtils.getObjectFromSession(request, session, GeneralData.class, gdString);

GeneralHelper generalHelper = new GeneralHelper(session);
if(gd == null) {
	PageData pd = generalHelper.initPageData();
	gd = generalHelper.initGeneralData(request,LoginCostanti.SERVLET_NAME_LOGIN);
	ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd, true);
}
// int idx =gd.getUrl().indexOf("/govwayConsole");
// int l = "/govwayConsole".length();

//   if(idx > -1){
//   	String ap1 = gd.getUrl().substring(0,idx);
// 	String ap2 = gd.getUrl().substring(idx+l);
// 	gd.setUrl(ap1 + request.getContextPath() + ap2);
//   }

String csrfTokenFromSession = ServletUtils.leggiTokenCSRF(request, session);
if(csrfTokenFromSession == null)
	csrfTokenFromSession = "";

String contextPath = request.getContextPath(); // '/govwayConsole'

String logoImage = gd.getLogoHeaderImage();
String logoLink = gd.getLogoHeaderLink();
String logoTitolo = gd.getLogoHeaderTitolo();
boolean visualizzaLinkHome = gd.isVisualizzaLinkHome();
String homeLink = request.getContextPath() + "/messagePage"+".do?dest=home";
String tabSessionKey = ServletUtils.getTabIdFromRequestAttribute(request);

if(tabSessionKey == null)
	tabSessionKey = "";

if(!tabSessionKey.equals("")){
	homeLink = homeLink + "&" + Costanti.PARAMETER_TAB_KEY + "=" + tabSessionKey;
}

//Header CSP
String randomNonce = UUID.randomUUID().toString().replace("-", "");
request.setAttribute(Costanti.REQUEST_ATTRIBUTE_CSP_RANDOM_NONCE, randomNonce);
response.setHeader(HttpConstants.HEADER_NAME_CONTENT_SECURITY_POLICY, MessageFormat.format(generalHelper.getCore().getCspHeaderValue(), randomNonce, randomNonce));
%>
<html lang="it">
<head>
<meta charset="UTF-8">
<jsp:include page="/jsplib/browserUtils.jsp" flush="true" />
<title><%= gd.getTitle() %></title>
<script type="text/javascript" nonce="<%= randomNonce %>">

var ok = true;
function white(str){
  for(var n=0; n<str.length; n++){
    if (str.charAt(n) == " "){
      ok = false;
    }
  }
};

function CheckDati() {
  white(document.form.login.value);
  white(document.form.password.value);
  if (ok == false) {
    ok = true;
    var win = window.open("?op=alert&msg=NoSpace", "winAlert", "width=200,height=130");
    win.focus();
    return false;
  } else { document.form.submit(); }
};

</script>
<link href="css/roboto/roboto-fontface.css" rel="stylesheet" type="text/css">
<link rel="stylesheet" href="css/<%= gd.getCss() %>" type="text/css">
<script type="text/javascript" src="js/webapps.js" nonce="<%= randomNonce %>"></script>
<!-- JQuery lib-->
<script type="text/javascript" src="js/jquery-latest.js" nonce="<%= randomNonce %>"></script>
<jsp:include page="/jsplib/menuUtente.jsp" flush="true" />
<link rel="shortcut icon" href="images/favicon.ico" type="image/x-icon" />
<script type="text/javascript" nonce="<%= randomNonce %>">

$(document).ready(function(){
				
	$("a").mousedown(function(e) {
		
		var urlDest = $(this).attr('href');
		
	    if(e.which == 3) {
	    	// console.log("right click: " + urlDest);
	    } else 
	    if(e.which == 2) {
	    	// console.log("center click: " + urlDest);
	    } else 
	    if(e.which == 1) {
	    	// aggiungi tab id a tutti i link cliccati col tasto sinistro
	    	if(urlDest) {
		    	// console.log("left click: " + urlDest);
		    	
		    	var targetDest = $(this).attr('target');
		    	
		    	if(targetDest && targetDest == '_blank') {
		    		return;
		    	}
		    	
		    	var newUrlDest = addTabIdParam(urlDest);
		    	$(this).attr('href',newUrlDest);
	    	} else {
	    		// console.log("href non trovato per l'elemento di tipo: " + $(this));
	    	}
	    } else {
	    	console.log("click non riconosciuto: " + urlDest);
	    }
	});
	
	
});
</script> 
</head>
<body marginwidth=0 marginheight=0 onLoad="focusText(document.form);">
	<table class="bodyWrapper">
		<tbody>
			<% if(StringUtils.isNotEmpty(logoImage)){ %>
			<!-- TR Logo -->
			<!-- TR1: Header1 -->
			<tr class="trPageHeaderLogo">
			 	<td colspan="2" class="tdPageHeaderLogo">
					<table class="tablePageHeader">
						<tbody>
							 <tr>
							 	<td colspan="2" align="left">
							 		<% if(StringUtils.isNotEmpty(logoLink)){%>
							 			<a href="<%=logoLink %>" target="_blank">
							 				<img src="<%=logoImage %>" alt="<%=logoTitolo %>" title="<%=logoTitolo %>">
							 			</a>
							 		<% } else {%>
							 			<img src="<%=logoImage %>" alt="<%=logoTitolo %>" title="<%=logoTitolo %>">
							 		<% } %>
							 	</td>
						 	</tr>
					 	</tbody>
				 	</table>
			 	</td>
			</tr>
			<% } %>
			<!-- TR1: Header1 -->
			<tr class="trPageHeader">
			 	<td colspan="2" class="tdPageHeader">
					<table class="tablePageHeader">
						<tbody>
							 <tr>
							  	<td class="td1PageHeader">
							  		<% if(visualizzaLinkHome){%>
								  		<a href="<%=homeLink %>" class="titleIconLinkLeft">
								    		<span class="consoleTitle"><%= gd.getTitle() %></span>
							    		</a>
						    		<% } else {%>
							 			<span class="consoleTitle"><%= gd.getTitle() %></span>
							 		<% } %>
							    </td>
							 	<td class="td2PageHeader" align="right">
							 		<table>
							 			<tbody>
							 				<tr>
							 					<td>
												</td>
							 				</tr>
							 			</tbody>
							 		</table>
								</td>
							 </tr>		
						</tbody>
					</table>
				</td>
			</tr>
			<!-- TR3: Body -->
			<tr class="trPageBody">
				<td valign="top" class="td2PageBody">
					<form name="form" action="<%= contextPath %>/j_security_check" method="post">
						<!-- Breadcrumbs -->
						<table id="crumbs-table">
							<tbody>
								<tr>
									<td colspan="2">
										<div id="breadcrumb-ct">
										</div>
									</td>
								</tr>
							  	<tr>
									<td class="messages-td1">
							  			<div class="messages-errors">
								  			<div class="messages-title">
								  				<span class="messages-errors-title-icon">&nbsp;&nbsp;</span>
								  				<span class="messages-title-text">Username o password non validi.</span>
								  			</div>
							  			</div>
							  		</td>
							  		<td class="messages-td2">
							  			&nbsp;&nbsp;
							  		</td>
								</tr>
							</tbody>
						</table>
						<%
						if(!csrfTokenFromSession.equals("")){
							%>
							<input type="hidden" name="<%=Costanti.PARAMETRO_CSRF_TOKEN%>" id="<%=Costanti.PARAMETRO_CSRF_TOKEN%>"  value="<%= csrfTokenFromSession %>"/>
							<%			
						}
						%>
						<table class="tabella-ext">
							<!-- Riga tabella -->
							<tbody>
								<tr> 
									<td valign="top" colspan="2">		
										<div class="panelLista">
											<table class="tabella">
												<tbody>
													<tr class="even">
														<td colspan="2">
										    				<fieldset id="de_name_0__fieldset" class="">
										    					<legend class="">
										    						<a id="de_name_0__anchor" class="navigatorAnchor">Login</a>
										    					</legend>
										    					<div id="de_name_0__id">
									                    			<div class="prop">
									                    				<label class="" id="de_label_1">Username</label>
									                    				<input type="text" name="j_username" value="" class="inputLink">
									                    			</div>
								                        			<div class="prop">
								                        				<label class="" id="de_label_2">Password</label>
								                        				<input class="inputLink" type="password" name="j_password" value="">
								                        			</div>
																</div>
															</fieldset>
														</td>
													</tr>
													<tr class="buttonrow">
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
												</tbody>
											</table>
										</div>
									</td>
								</tr>
							</tbody>
						</table>
					</form>
				</td>
			</tr>
			<!-- TR4: Footer -->
			<tr class="trPageFooter">
				<td colspan="2" class="tdPageFooter">
					<div>
						<a href="<%= gd.getLinkFoot() %>" target="_blank" rel="noopener">
							<img src="images/tema_link/logo_link_footer.png" alt="link.it" />
						</a>
					</div>
					<div id="ajax_status_div">
						<span class="rich-mpnl-mask-div mainStatusStartStyle"></span>
						<span class="mainStatusStartStyleInner">
							<img src="images/tema_link/ajax_status.gif" alt="loading"/>
						</span>
					</div>
				</td>
			</tr>
		</tbody>
	</table>
</body>
</html>
 
 