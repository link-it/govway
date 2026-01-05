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
package org.openspcoop2.web.ctrlstat.servlet.login;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.pdd.config.OpenSPCoop2ConfigurationException;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.oauth2.OAuth2Costanti;
import org.openspcoop2.utils.oauth2.OAuth2Utilities;
import org.openspcoop2.utils.transport.http.credential.IPrincipalReader;
import org.openspcoop2.utils.transport.http.credential.PrincipalReaderException;
import org.openspcoop2.utils.transport.http.credential.PrincipalReaderFactory;
import org.openspcoop2.utils.transport.http.credential.PrincipalReaderType;
import org.openspcoop2.web.ctrlstat.config.ConsoleProperties;
import org.openspcoop2.web.ctrlstat.core.ConsoleSearch;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.ControlStationLogger;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.driver.DriverControlStationException;
import org.openspcoop2.web.ctrlstat.gestori.GestoreConsistenzaDati;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.utenti.UtentiCore;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.MessageType;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.users.DriverUsersDBException;
import org.openspcoop2.web.lib.users.dao.User;
import org.slf4j.Logger;

/**
 * OAuth2UserServlet
 *
 * @author Pintori Giuliano (giuliano.pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class OAuth2UserServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static Logger log = ControlStationLogger.getPddConsoleCoreLogger();

	public OAuth2UserServlet() {
		super();
	}

	@Override
	protected void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
		engineDoGet(httpServletRequest, httpServletResponse);
	}

	private void engineDoGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
		// login utenza  
		IPrincipalReader principalReader = null;
		String loginUtenteNonAutorizzatoRedirectUrl = null;
		String loginUtenteNonValidoRedirectUrl = null;
		String loginErroreInternoRedirectUrl = null;
		String oauth2LogoutUrl = null;
		GeneralHelper generalHelper = null;

		HttpSession session = httpServletRequest.getSession();

		try {
			String loginTipo = ConsoleProperties.getInstance().getLoginTipo();

			loginTipo = getTipoLogin(loginTipo);

			Properties prop = ConsoleProperties.getInstance().getLoginProperties();

			principalReader = caricaPrincipalReader(loginTipo, prop); 

			loginUtenteNonAutorizzatoRedirectUrl = ConsoleProperties.getInstance().getLoginUtenteNonAutorizzatoRedirectUrl();
			loginUtenteNonValidoRedirectUrl = ConsoleProperties.getInstance().getLoginUtenteNonValidoRedirectUrl();
			loginErroreInternoRedirectUrl = ConsoleProperties.getInstance().getLoginErroreInternoRedirectUrl();
			oauth2LogoutUrl = prop.getProperty(OAuth2Costanti.PROP_OAUTH2_LOGOUT_ENDPOINT);

			generalHelper = new GeneralHelper(session);
			ControlStationCore core = generalHelper.getCore();

			LoginHelper loginHelper = new LoginHelper(generalHelper.getCore(), httpServletRequest, new PageData(), session);

			// ricerca utenza in sessione
			String userLogin = ServletUtils.getUserLoginFromSession(session);

			// utente non loggato
			if (userLogin == null) {

				// se l'utente non e' in sessione provo a loggarlo leggendo il principal da metodo di autorizzazione impostato
				String username = getPrincipal(httpServletRequest, principalReader);

				// Se l'username che mi arriva e' settato vuol dire che sono autorizzato dal Container
				if(username != null){

					// Annullo quanto letto sull'auditing
					ControlStationCore.clearAuditManager();

					if(GestoreConsistenzaDati.gestoreConsistenzaDatiInEsecuzione){
						log.debug("Controllo di valida non valido: {}", loginHelper.getPd().getMessage());
						ServletUtils.setObjectIntoSession(httpServletRequest, session, LoginCostanti.MESSAGGIO_INFO_CONTROLLO_CONSISTENZA_DATI_IN_CORSO, Costanti.PRINCIPAL_ERROR_MSG);

						ServletUtils.removeUserLoginFromSession(session);

						// Redirect verso pagina di errore con logout oauth
						redirectToPagina(log, httpServletRequest, httpServletResponse, session,
								null, LoginCostanti.SERVLET_NAME_LOGIN, oauth2LogoutUrl);
						// return so that we do not chain to other filters
						return;
					}
					
					UtentiCore utentiCore = new UtentiCore(core);
					User user = utentiCore.getUser(username);

					gestioneUtenteTrovato(httpServletRequest, httpServletResponse, loginUtenteNonAutorizzatoRedirectUrl, loginUtenteNonValidoRedirectUrl,
							oauth2LogoutUrl, loginHelper, user);

				} else {

					// ERRORE
					ServletUtils.removeUserLoginFromSession(session);

					// Messaggio di errore
					ServletUtils.setObjectIntoSession(httpServletRequest, session, Costanti.MESSAGGIO_ERRORE_LOGIN_CON_PRINCIPAL_PRINCIPAL_ASSENTE, Costanti.PRINCIPAL_ERROR_MSG);

					redirectToPagina(log, httpServletRequest, httpServletResponse, session,
							loginUtenteNonAutorizzatoRedirectUrl, LoginCostanti.SERVLET_NAME_LOGIN,
							oauth2LogoutUrl);
				}
			} else {
				UtentiCore utentiCore = new UtentiCore(core);
				User user = utentiCore.getUser(userLogin);

				log.debug("Utente Loggato sessione valida.");
				ServletUtils.setObjectIntoSession(httpServletRequest, session, core.isSinglePdD(), CostantiControlStation.SESSION_PARAMETRO_SINGLE_PDD);

				// utente loggato
				ServletUtils.setObjectIntoSession(httpServletRequest, session, core.getTipoDatabase(), CostantiControlStation.SESSION_PARAMETRO_TIPO_DB);

				resetRicerche(httpServletRequest, session, loginHelper);					

				utenteLoggatoCorrettamente(httpServletRequest, httpServletResponse, userLogin, session, core, user);
			}

		} catch (UtilsException | OpenSPCoop2ConfigurationException | DriverUsersDBException | DriverControlStationException 
				| PrincipalReaderException | IOException  
				| InstantiationException | IllegalAccessException | ClassNotFoundException 
				| IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			ServletUtils.setObjectIntoSession(httpServletRequest, session, OAuth2Costanti.ERROR_MSG_LOGIN_ERRORE_INTERNO, Costanti.PRINCIPAL_ERROR_MSG);
			// tutti gli errori anche interni provocano il logout federato
			try {
				redirectToPagina(log, httpServletRequest, httpServletResponse, session,
						loginErroreInternoRedirectUrl, LoginCostanti.SERVLET_NAME_LOGIN, oauth2LogoutUrl);
			} catch (IOException e2) {
				OAuth2UserServlet.log.error("Errore durante esecuzione redirect: " + e2.getMessage(), e2);
				httpServletResponse.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
				try {
					httpServletResponse.getWriter().write(OAuth2Costanti.ERROR_MSG_AUTENTICAZIONE_OAUTH2_NON_DISPONIBILE_SI_E_VERIFICATO_UN_ERRORE + e.getMessage());
				} catch (IOException e1) {
					log.error("Errore durante esecuzione redirect: " + e1.getMessage(), e1);
				}
			}
		}
	}

	private String getTipoLogin(String loginTipo) {
		if(StringUtils.isEmpty(loginTipo))
			loginTipo = PrincipalReaderType.OAUTH2.getValue();
		return loginTipo;
	}

	private void resetRicerche(HttpServletRequest httpServletRequest, HttpSession session, LoginHelper loginHelper)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, InvocationTargetException,
			NoSuchMethodException, DriverControlStationException {
		// Check Reset delle ricerche
		String resetSearch = httpServletRequest.getParameter(CostantiControlStation.PARAMETRO_RESET_SEARCH);
		String postBackElement = httpServletRequest.getParameter(Costanti.POSTBACK_ELEMENT_NAME);
		if(ServletUtils.isCheckBoxEnabled(resetSearch) && (postBackElement==null || "".equals(postBackElement))) {

			for (int i = 0; i < Liste.getTotaleListe(); i++) {
				ServletUtils.removeRisultatiRicercaFromSession(httpServletRequest, session, i);
			}

			boolean existsRicerca = ServletUtils.existsSearchObjectFromSession(httpServletRequest, session);
			ConsoleSearch ricerca = (ConsoleSearch) ServletUtils.getSearchObjectFromSession(httpServletRequest, session, ConsoleSearch.class);
			if(ricerca!=null) {
				ricerca.reset();
				for (int i = 0; i < Liste.getTotaleListe(); i++) {
					loginHelper.initializeFilter(ricerca, i);
				}
				if(!existsRicerca) {
					// salvo in sessione le inizializzazioni
					ServletUtils.setSearchObjectIntoSession(httpServletRequest, session, ricerca);
				}
				ControlStationCore.logDebug("Effettuato reset della ricerca");					
			}
		}
	}

	private void gestioneUtenteTrovato(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
			String loginUtenteNonAutorizzatoRedirectUrl, String loginUtenteNonValidoRedirectUrl, String oauth2LogoutUrl,
			LoginHelper loginHelper, User user) throws IOException, DriverUsersDBException {

		String username = user.getLogin();
		HttpSession session = httpServletRequest.getSession();
		GeneralHelper generalHelper = new GeneralHelper(session);
		ControlStationCore core = generalHelper.getCore();

		// effettuo il login sul db
		boolean isOk = loginHelper.loginCheckData(LoginTipologia.WITHOUT_PASSWORD, username, null);

		if(isOk) {
			// utente loggato
			utenteLoggatoCorrettamente(httpServletRequest, httpServletResponse, username, session, core, user);
		} else {
			// utente non autorizzato
			ServletUtils.setObjectIntoSession(httpServletRequest, session, MessageFormat.format(Costanti.MESSAGGIO_ERRORE_LOGIN_CON_PRINCIPAL_UTENTE_NON_AUTORIZZATO, username, loginHelper.getPd().getMessage()), Costanti.PRINCIPAL_ERROR_MSG);
			String debugMsg = "Utente non autorizzato: {}";
			String customRedirectUrl = loginUtenteNonAutorizzatoRedirectUrl;

			// utenza non valida
			if(loginHelper.getPd().getMessage().equals(LoginCostanti.MESSAGGIO_ERRORE_UTENTE_NON_ABILITATO_UTILIZZO_CONSOLE) 
					|| loginHelper.getPd().getMessage().equals(LoginCostanti.MESSAGGIO_ERRORE_UTENTE_NON_ABILITATO_UTILIZZO_CONSOLE_CONFIGURAZIONE_NON_CORRETTO)) {

				debugMsg = "Utente non valido: {}";
				customRedirectUrl = loginUtenteNonValidoRedirectUrl;
				ServletUtils.setObjectIntoSession(httpServletRequest, session, MessageFormat.format(Costanti.MESSAGGIO_ERRORE_LOGIN_CON_PRINCIPAL_UTENTE_NON_VALIDO, username,	loginHelper.getPd().getMessage()), Costanti.PRINCIPAL_ERROR_MSG);
			}

			log.debug(debugMsg, loginHelper.getPd().getMessage());

			ServletUtils.removeUserLoginFromSession(session);

			// Redirect verso pagina di errore con logout oauth
			redirectToPagina(log, httpServletRequest, httpServletResponse, session,
					customRedirectUrl, LoginCostanti.SERVLET_NAME_LOGIN,
					oauth2LogoutUrl);
		}
	}

	private void utenteLoggatoCorrettamente(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
			String username, HttpSession session, ControlStationCore core, User user) throws IOException, DriverUsersDBException {
		ServletUtils.setObjectIntoSession(httpServletRequest, session, core.getTipoDatabase(), CostantiControlStation.SESSION_PARAMETRO_TIPO_DB);

		LoginCore loginCore = new LoginCore(core);

		LoginSessionUtilities.setLoginParametersSession(httpServletRequest, session, loginCore, username, user);

		loginCore.performAuditLogin(username);

		log.debug("Utente autorizzato, effettuo il redirect verso l'applicazione...");

		// Redirect interno
		String redirPageUrl = new Parameter("", httpServletRequest.getContextPath() + "/" + LoginCostanti.SERVLET_NAME_MESSAGE_PAGE,
				new Parameter(Costanti.PARAMETER_MESSAGE_TEXT,LoginCostanti.LABEL_LOGIN_EFFETTUATO_CON_SUCCESSO),
				new Parameter(Costanti.PARAMETER_MESSAGE_TYPE,MessageType.INFO_SINTETICO.toString())
				).getValue();

		// redirect dopo il login
		httpServletResponse.sendRedirect(redirPageUrl);
	}

	private void redirectToPagina(Logger log, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, HttpSession session, String customRedirectUrl, String defaultRedirectUrl,
			String oauth2LogoutUrl) throws IOException {

		String redirPageUrl =  StringUtils.isNotEmpty(customRedirectUrl)
				? customRedirectUrl : 
					ServletUtils.buildInternalRedirectUrl(httpServletRequest, "/" + defaultRedirectUrl);

		String idToken = (String) session.getAttribute(OAuth2Costanti.ATTRIBUTE_NAME_ID_TOKEN);
		String logoutUrl = OAuth2Utilities.creaUrlLogout(idToken, oauth2LogoutUrl, redirPageUrl);

		log.debug("Effettuo redirect logout verso pagina: {}", logoutUrl);

		httpServletResponse.sendRedirect(logoutUrl);

	}

	private String getPrincipal(HttpServletRequest httpServletRequest, IPrincipalReader principalReader) throws PrincipalReaderException {
		try {
			return principalReader.getPrincipal(httpServletRequest);
		}catch (PrincipalReaderException e) {
			ControlStationCore.logError("Errore durante la lettura del principal: " + e.getMessage(),e);
			throw e;
		}
	}

	private IPrincipalReader caricaPrincipalReader(String loginTipo, Properties prop) throws PrincipalReaderException {
		try {
			IPrincipalReader principalReader = PrincipalReaderFactory.getReader(OAuth2UserServlet.log, loginTipo);
			principalReader.init(prop);
			return principalReader;
		}catch (PrincipalReaderException e) {
			ControlStationCore.logError("Impossibile caricare il principal reader: "+e.getMessage(), e);
			throw e;
		}
	}


}
