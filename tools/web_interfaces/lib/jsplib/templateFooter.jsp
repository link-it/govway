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



<%@ page session="true" import="java.util.List, org.openspcoop2.web.lib.mvc.*" %>

<%
String iddati = "";
String ct = request.getContentType();
if (ct != null && (ct.indexOf(Costanti.MULTIPART) != -1)) {
  iddati = ServletUtils.getObjectFromSession(request, session, String.class, Costanti.SESSION_ATTRIBUTE_ID_DATI);
} else {
  iddati = request.getParameter(Costanti.PARAMETER_NAME_ID_DATI);
}
String gdString = Costanti.SESSION_ATTRIBUTE_GENERAL_DATA;
if (iddati != null && !iddati.equals("notdefined"))
  gdString += iddati;
else
  iddati = "notdefined";
GeneralData gd = ServletUtils.getObjectFromSession(request, session, GeneralData.class, gdString);
String randomNonce = (String) request.getAttribute(Costanti.REQUEST_ATTRIBUTE_CSP_RANDOM_NONCE);
%>
<!-- TR4: Footer -->
<tr class="trPageFooter">
	<td colspan="2" class="tdPageFooter">
		<div>
			<a href="<%= gd.getLinkFoot() %>" target="_blank" rel="noopener">
				<img src="images/tema_link/logo_link_footer.png" alt="link.it" />
			</a>
		</div>
	</td>
</tr>
<script type="text/javascript" nonce="<%= randomNonce %>">
$(document).ready(function(){
	$("a").mousedown(function(e) {
		addTabIdParamToHref($(this),e);
	});
});
</script>
<script type="text/javascript" nonce="<%= randomNonce %>">
function onPageLoad() {
    <%=Costanti.JS_FUNCTION_NASCONDI_AJAX_STATUS %>
}
window.onload = onPageLoad;
</script>


