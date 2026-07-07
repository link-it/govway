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
<%@ page session="true" import="java.util.*, org.apache.commons.text.StringEscapeUtils, org.openspcoop2.web.lib.mvc.*" %>
<%
	String iddati = request.getParameter(Costanti.PARAMETER_NAME_ID_DATI);
	String pdString = Costanti.SESSION_ATTRIBUTE_PAGE_DATA;
	if (iddati != null && !iddati.equals("notdefined")) {
		pdString += iddati;
	}
	PageData pdLLM = ServletUtils.getObjectFromSession(request, session, PageData.class, pdString);
	String nonceLLM = (String) request.getAttribute(Costanti.REQUEST_ATTRIBUTE_CSP_RANDOM_NONCE);

	List<?> datiConGruppiLLM = pdLLM.getDati();
	List<?> datiLLM = (datiConGruppiLLM != null && datiConGruppiLLM.size() > 0) ? (List<?>) datiConGruppiLLM.get(0) : new ArrayList<Object>();
%>
<style nonce="<%= nonceLLM %>">
.llm-hub-card { background:#ffffff; margin:0 16px 16px 16px; border-radius:4px; overflow:hidden; }
.llm-hub-titlebar { background:#454754; color:#ffffff; font-weight:600; font-size:16px; padding:14px 20px; margin:0; }
.llm-hub-body { background:#ffffff; padding:8px 16px 20px; }
.llm-hub-fieldset { border:1px solid #d5d5da; border-radius:4px; margin:16px 0 8px; padding:8px 20px 20px; }
.llm-hub-legend { font-weight:700; color:#333333; font-size:14px; padding:0 8px; margin-left:8px; }
.llm-hub-grid { display:flex; flex-wrap:wrap; gap:0 8px; padding-top:8px; }
.llm-hub-tile { display:flex; flex-direction:column; align-items:center; width:132px; padding:18px 8px; text-decoration:none; color:#2b2b2b; border-radius:6px; }
.llm-hub-tile:hover { background:#f4f4f6; text-decoration:none; color:#2b2b2b; }
.llm-hub-tile .llm-hub-icon { font-size:34px; line-height:1; color:#2b2b2b; font-variation-settings:'FILL' 1, 'wght' 400, 'GRAD' 0, 'opsz' 40; }
.llm-hub-tile .llm-hub-label { margin-top:14px; text-align:center; font-size:13px; line-height:1.3; color:#2b2b2b; }
.llm-hub-tile:hover .llm-hub-label, .llm-hub-tile:hover .llm-hub-icon { color:#2b2b2b; }
</style>
<tr>
	<td valign="top">
		<div class="llm-hub-card">
			<div class="llm-hub-titlebar">LLM</div>
			<div class="llm-hub-body">
<%
	boolean sezioneApertaLLM = false;
	boolean gridApertaLLM = false;
	for (int i = 0; i < datiLLM.size(); i++) {
		DataElement deLLM = (DataElement) datiLLM.get(i);
		if ("title".equals(deLLM.getType())) {
			if (gridApertaLLM) { %></div><% }
			if (sezioneApertaLLM) { %></fieldset><% }
			%><fieldset class="llm-hub-fieldset"><legend class="llm-hub-legend"><%= StringEscapeUtils.escapeHtml4(deLLM.getLabel()) %></legend><div class="llm-hub-grid"><%
			sezioneApertaLLM = true;
			gridApertaLLM = true;
		} else if ("link".equals(deLLM.getType()) && gridApertaLLM) {
			String hrefLLM = deLLM.getUrl();
			String iconLLM = deLLM.getIcon();
			String labelLLM = deLLM.getValue();
			%><a class="llm-hub-tile" href="<%= hrefLLM %>"><span class="material-symbols-outlined llm-hub-icon"><%= iconLLM %></span><span class="llm-hub-label"><%= StringEscapeUtils.escapeHtml4(labelLLM) %></span></a><%
		}
	}
	if (gridApertaLLM) { %></div><% }
	if (sezioneApertaLLM) { %></fieldset><% }
%>
			</div>
		</div>
	</td>
</tr>
