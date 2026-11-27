/*
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
 *
 */
package org.openspcoop2.testsuite.zap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.utils.UtilsException;
import org.zaproxy.clientapi.core.ClientApiException;

/**
 * GovWayConsole
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class GovWayMonitor extends AbstractConsoleScan {
	
	public static void main(String[] args) throws UtilsException, ClientApiException, IOException {
				
		GovWayMonitor console = new GovWayMonitor();
		console.scan(args);
		
	}
	
	@Override
	protected String getLoginUrl(String baseUrl) {
		return baseUrl+"public/login.jsf";
	}
	@Override
	protected String getLogoutUrl(String baseUrl) {
		return baseUrl+"public/logout.jsf";
	}
	
	@Override
	protected String getLoginRequestData() {
		return "AJAXREQUEST=_viewRoot&loginForm=loginForm&username={%username%}&password={%password%}&javax.faces.ViewState=j_id1&loginBtn=loginBtn&";
	}
	
	/* Indicatori di sessione per ZAP (verificati su ogni risposta, header compresi). Una risposta è autenticata
	   se contiene l'indicatore di login, oppure se non contiene quello di logout.
	   - login: 'menuUtente', il menu utente dell'header, presente solo nelle pagine autenticate (anche nella
	     risposta alla POST di login riuscita). 'welcome.jsf' non va bene: è il link del titolo e compare anche
	     nella pagina di login, di sessione scaduta e di errore, oltre che dopo un login fallito.
	   - logout: il form della pagina di login ('loginForm') o il link di quella di sessione scaduta ('tornaAlLoginLink'),
	     oppure il redirect verso la pagina di login, con cui il monitor risponde quando la sessione esiste ma non è autenticata.
	     Senza indicatore di logout ZAP rifaceva il login per ogni risposta priva dell'indicatore di login
	     (risorse statiche, risposte AJAX). Non va usato il Location '/govwayMonitor/public/timeoutPage.jsf':
	     il monitor lo restituisce anche, a sessione valida, alla POST di un form con un ViewState non più valido;
	     ZAP rifaceva il login, la nuova sessione invalidava i ViewState dei form già raccolti e i login si susseguivano a cascata. */
	@Override
	protected String getLoggedInIndicator() {
		return "menuUtente";
	}
	@Override
	protected String getLoggedOutIndicator() {
		return "id=\"(loginForm|tornaAlLoginLink)\"|Location: [^\\r\\n]*/govwayMonitor/public/login\\.jsf";
	}
	@Override
	protected boolean isLoggedOutIndicatorRegExp() {
		return true;
	}

	/* Lo stato delle viste JSF è salvato sul server: ZAP deve rigenerare il ViewState (oltre al token '_csrf',
	   già noto a ZAP) prima di ripetere un form, altrimenti la scansione attiva riceve solo la pagina di sessione scaduta. */
	@Override
	protected List<String> getAntiCsrfTokens() {
		List<String> l = new ArrayList<>();
		l.add("javax.faces.ViewState");
		l.add("_csrf");
		return l;
	}
	
	@Override
	protected boolean isDebug() {
		return false;
	}
	
	@Override
	protected boolean isUniqueClientApi() {
		return false;
	}
	
}
