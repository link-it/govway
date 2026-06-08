<%--
 * GovWay - A customizable API Gateway
 * https://govway.org
 *
 * Copyright (c) 2005-2026 Link.it srl (https://link.it).
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
<%@page import="org.openspcoop2.utils.VersionInfoPopup"%>
<%@ page session="true" import="java.util.List, org.openspcoop2.web.lib.mvc.*" %>
<%
// Popup informativo opzionale (valori = frammenti HTML fidati forniti dal plugin IVersionInfo).
// Mostrato una sola volta dopo il login: la comparsa e' pilotata dall'attributo di sessione 'showVersionPopup',
// che viene consumato qui sotto.
GeneralData gdVersionPopup = ServletUtils.getObjectFromSession(request, session, GeneralData.class, Costanti.SESSION_ATTRIBUTE_GENERAL_DATA);
String randomNonceVersionPopup = (String) request.getAttribute(Costanti.REQUEST_ATTRIBUTE_CSP_RANDOM_NONCE);
Boolean showVersionPopup = (Boolean) session.getAttribute("showVersionPopup");
VersionInfoPopup vPopup = (gdVersionPopup!=null) ? gdVersionPopup.getPopup() : null;
if(showVersionPopup!=null && showVersionPopup.booleanValue() && vPopup!=null) {
	session.removeAttribute("showVersionPopup"); // una sola comparsa
	String vpSeverity = (vPopup.getSeverity()!=null && !vPopup.getSeverity().trim().isEmpty()) ? vPopup.getSeverity() : "warning";
	String vpIcon = (vPopup.getIcon()!=null && !vPopup.getIcon().trim().isEmpty()) ? vPopup.getIcon() : ("error".equals(vpSeverity) ? "error" : "warning");
	String vpButton = (vPopup.getButtonLabel()!=null && !vPopup.getButtonLabel().trim().isEmpty()) ? vPopup.getButtonLabel() : "Ho capito";
%>
<div id="versionPopupOverlay" class="version-popup-overlay">
	<div class="version-popup-dialog" role="dialog" aria-modal="true">
		<div class="version-popup-header version-popup-header-<%= vpSeverity %>">
			<i class="material-icons md-24 version-popup-header-icon"><%= vpIcon %></i>
			<span class="version-popup-title"><%= (vPopup.getTitle()!=null) ? vPopup.getTitle() : "" %></span>
			<% if(vPopup.isClosable()){ %><span class="version-popup-close" id="versionPopupClose" title="Chiudi">&times;</span><% } %>
		</div>
		<div class="version-popup-body">
			<%
			if(vPopup.getParagraphs()!=null) {
				for(String par : vPopup.getParagraphs()) {
					if(par!=null && !par.trim().isEmpty()) {
			%>
			<p class="version-popup-paragraph"><%= par %></p>
			<%
					}
				}
			}
			if(vPopup.getListHead()!=null && !vPopup.getListHead().trim().isEmpty()) {
			%>
			<p class="version-popup-listhead"><%= vPopup.getListHead() %></p>
			<%
			}
			if(vPopup.getListItems()!=null && !vPopup.getListItems().isEmpty()) {
			%>
			<ul class="version-popup-list">
				<%
				for(String voce : vPopup.getListItems()) {
					if(voce!=null && !voce.trim().isEmpty()) {
				%>
				<li><%= voce %></li>
				<%
					}
				}
				%>
			</ul>
			<%
			}
			if(vPopup.getNote()!=null && !vPopup.getNote().trim().isEmpty()) {
				String vpNoteStyle = (vPopup.getNoteStyle()!=null && !vPopup.getNoteStyle().trim().isEmpty()) ? vPopup.getNoteStyle() : "warning";
			%>
			<div class="version-popup-note version-popup-note-<%= vpNoteStyle %>"><%= vPopup.getNote() %></div>
			<%
			}
			%>
		</div>
		<div class="version-popup-footer">
			<button type="button" id="versionPopupButton" class="version-popup-button"><%= vpButton %></button>
		</div>
	</div>
</div>
<script type="text/javascript" nonce="<%= randomNonceVersionPopup %>">
(function(){
	var overlay = document.getElementById("versionPopupOverlay");
	if(!overlay){ return; }
	function closeVersionPopup(){ overlay.style.display = "none"; }
	var btnOk = document.getElementById("versionPopupButton");
	if(btnOk){ btnOk.onclick = closeVersionPopup; }
	var btnClose = document.getElementById("versionPopupClose");
	if(btnClose){ btnClose.onclick = closeVersionPopup; }
	overlay.style.display = "flex";
})();
</script>
<% } %>
