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
String pdString = Costanti.SESSION_ATTRIBUTE_PAGE_DATA;
if (iddati != null && !iddati.equals("notdefined")) {
  gdString += iddati;
  pdString += iddati;
}
else {
  iddati = "notdefined";
}
GeneralData gd = ServletUtils.getObjectFromSession(request, session, GeneralData.class, gdString);
PageData pd = ServletUtils.getObjectFromSession(request, session, PageData.class, pdString);

String randomNonce = (String) request.getAttribute(Costanti.REQUEST_ATTRIBUTE_CSP_RANDOM_NONCE);
%>

<td class="td1PageBody" valign='top' role="navigation" aria-label="<%= Costanti.LABEL_ARIA_MENU_PRINCIPALE %>">
	<%-- Reflow (WCAG 1.4.10): sotto i 71em (soglia uguale a quella di linkit-base.css) il menu va sopra il contenuto e parte chiuso; questo
	     pulsante lo apre e lo chiude. Sopra la soglia e' nascosto dal CSS e il menu resta sempre
	     visibile (la classe gw-menu-chiuso ha effetto solo dentro la media query). --%>
	<button type="button" id="gw-menu-toggle" class="gw-menu-toggle" aria-expanded="true" aria-controls="menuct">
		<span class="material-icons" aria-hidden="true">menu</span><span>Menu</span>
	</button>
	<script type="text/javascript" nonce="<%= randomNonce %>">
		(function() {
			var stretto = window.matchMedia ? window.matchMedia('(max-width: 71em)') : null;
			var imposta = function(aperto) {
				var bottone = document.getElementById('gw-menu-toggle');
				var menu = document.getElementById('menuct');
				if (!bottone || !menu) return;
				bottone.setAttribute('aria-expanded', aperto ? 'true' : 'false');
				if (aperto) menu.classList.remove('gw-menu-chiuso'); else menu.classList.add('gw-menu-chiuso');
			};
			var avvia = function() {
				var bottone = document.getElementById('gw-menu-toggle');
				if (!bottone) return;
				bottone.addEventListener('click', function() {
					imposta(bottone.getAttribute('aria-expanded') !== 'true');
				});
				// chiuso quando la vista e' stretta; aperto (e il pulsante nascosto) quando torna larga.
				// Lo stato cambia solo quando si attraversa la soglia, cosi' non annulla la scelta dell'utente;
				// oltre all'evento della media query si controllano 'resize' e la larghezza della pagina, perche' non tutti i browser
				// (WebKit/Safari) segnalano il cambio di media query dovuto allo zoom.
				var ultimo = !!(stretto && stretto.matches);
				imposta(!ultimo);
				if (stretto) {
					var verifica = function() { if (stretto.matches !== ultimo) { ultimo = stretto.matches; imposta(!ultimo); } };
					if (stretto.addEventListener) stretto.addEventListener('change', verifica); else if (stretto.addListener) stretto.addListener(verifica);
					window.addEventListener('resize', verifica);
					// e la larghezza della pagina stessa: cambia anche con lo zoom, quando gli eventi sopra mancano
					if (typeof ResizeObserver !== 'undefined') new ResizeObserver(verifica).observe(document.documentElement);
				}
			};
			if (document.readyState === 'loading') document.addEventListener('DOMContentLoaded', avvia); else avvia();
		})();
	</script>
	<div id="menuct" class="<%= (gd!=null && gd.isMenuDisabled()) ? "menu-disabled" : "" %>">
		<% 
			List<?> v = pd.getMenu(); 
			MenuEntry m = null;
	
			for (int i = 0; i <  v.size(); i++) {
				  m = (MenuEntry) v.get(i);
		%>
			<div class="sezioneMenu">
				<div class="titoloSezione">
					<span><%= m.getTitle() %></span>
				</div>		
			
				<%
			  		String [][] entries = m.getEntries();
			  		for (int j = 0; j < entries.length; j++) {
			  			String cssClass = "voceMenuRC";
			  			String divId = "voceMenu_"+i+"_"+j;
			  			String hrefId = "voceMenuAnchor_"+i+"_"+j;
			  			
			  			%><div class="voceMenuRC" id="<%=divId %>"><%
		  					if (entries[j].length == 2) {
		  						%>
		  						<a class='<%= cssClass %>' title='<%= entries[j][0] %>' href='<%= entries[j][1] %>' id="<%=hrefId %>">
		      						<p class='<%= cssClass %>'><%= entries[j][0] %></p>
		      					</a>
		  						<%
		  					} else if (entries[j].length == 3) {
								%>
		  						<a class='<%= cssClass %>' target='<%= entries[j][2] %>' title='<%= entries[j][0] %>' href='<%= entries[j][1] %>' id="<%=hrefId %>">
				      				<p class='<%= cssClass %>'><%= entries[j][0] %></p>
				      			</a>
		  						<%
		  					}
	  					%>
			  				<script type="text/javascript" nonce="<%= randomNonce %>">
						      	 $(document).ready(function(){
										$('#<%=hrefId %>').click(function() {
											<%= Costanti.JS_FUNCTION_VISUALIZZA_AJAX_STATUS %>return true;
										});
									});
							</script>
			      			</div>
			      		<%
			 	 }
	  		 %>
			</div>
		<% } %>
	</div>
</td>

