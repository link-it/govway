/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.MessageType;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.users.DriverUsersDBException;
import org.openspcoop2.web.monitor.core.core.Utility;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;

/**
 * OAuth2UserServlet
 *
 * @author Pintori Giuliano (giuliano.pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class OAuth2UserServlet extends HttpServlet {

	private static final String ERROR_MSG_ERRORE_DURANTE_LA_LETTURA_DELLE_PROPERTIES = "Errore durante la lettura delle properties: ";

	private static final String ERROR_MSG_AUTENTICAZIONE_OAUTH2_NON_DISPONIBILE_SI_E_VERIFICATO_UN_ERRORE = "Autenticazione Oauth2 non disponibile: si e' verificato un'errore: ";

	private static final long serialVersionUID = 1L;

	private static Logger log = ControlStationLogger.getPddConsoleCoreLogger();

	public OAuth2UserServlet() {
		super();
	}

	@Override
	protected void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
		// login utenza  
		IPrincipalReader principalReader = null;
		String loginUtenteNonAutorizzatoRedirectUrl = null;
		String loginUtenteNonValidoRedirectUrl = null;
		String loginErroreInternoRedirectUrl = null;
		String loginSessioneScadutaRedirectUrl = null;
		String oauth2LogoutUrl = null;
		GeneralHelper generalHelper = null;

		try {
			String loginTipo = ConsoleProperties.getInstance().getLoginTipo();

			if(StringUtils.isEmpty(loginTipo))
				loginTipo = PrincipalReaderType.PRINCIPAL.getValue();

			Properties prop = ConsoleProperties.getInstance().getLoginProperties();

			principalReader = caricaPrincipalReader(loginTipo, prop); 

			loginUtenteNonAutorizzatoRedirectUrl = ConsoleProperties.getInstance().getLoginUtenteNonAutorizzatoRedirectUrl();
			loginUtenteNonValidoRedirectUrl = ConsoleProperties.getInstance().getLoginUtenteNonValidoRedirectUrl();
			loginErroreInternoRedirectUrl = ConsoleProperties.getInstance().getLoginErroreInternoRedirectUrl();
			loginSessioneScadutaRedirectUrl = ConsoleProperties.getInstance().getLoginSessioneScadutaRedirectUrl();
			oauth2LogoutUrl = prop.getProperty(OAuth2Costanti.PROP_OAUTH2_LOGOUT_ENDPOINT);


			HttpSession session = httpServletRequest.getSession();

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
						AuthorizationFilter.setErrorMsg(generalHelper, session, httpServletRequest, httpServletResponse, LoginCostanti.SERVLET_NAME_LOGIN_MESSAGE_PAGE, LoginCostanti.MESSAGGIO_INFO_CONTROLLO_CONSISTENZA_DATI_IN_CORSO, MessageType.INFO, httpServletRequest.getServletContext(), null);
						// return so that we do not chain to other filters
						return;
					}

					// effettuo il login sul db
					try {
						boolean isOk = loginHelper.loginCheckData(LoginTipologia.WITHOUT_PASSWORD, username, null);

						if(isOk) {
							// utente loggato
							ServletUtils.setObjectIntoSession(httpServletRequest, session, core.getTipoDatabase(), CostantiControlStation.SESSION_PARAMETRO_TIPO_DB);

							LoginCore loginCore = new LoginCore(core);

							LoginSessionUtilities.setLoginParametersSession(httpServletRequest, session, loginCore, username);

							loginCore.performAuditLogin(username);

							log.debug("Utente autorizzato, effettuo il redirect verso l'applicazione...");

							String redirPageUrl = new Parameter("", httpServletRequest.getContextPath() + "/" + LoginCostanti.SERVLET_NAME_MESSAGE_PAGE,
									new Parameter(Costanti.PARAMETER_MESSAGE_TEXT,LoginCostanti.LABEL_LOGIN_EFFETTUATO_CON_SUCCESSO),
									new Parameter(Costanti.PARAMETER_MESSAGE_TYPE,MessageType.INFO_SINTETICO.toString())
									).getValue();

							// redirect dopo il login
							httpServletResponse.sendRedirect(redirPageUrl);
						} else {
							// utenza non valida
							if(loginHelper.getPd().getMessage().equals(LoginCostanti.MESSAGGIO_ERRORE_UTENTE_NON_ABILITATO_UTILIZZO_CONSOLE) 
									|| loginHelper.getPd().getMessage().equals(LoginCostanti.MESSAGGIO_ERRORE_UTENTE_NON_ABILITATO_UTILIZZO_CONSOLE_CONFIGURAZIONE_NON_CORRETTO)) {

								log.debug("Utente non valido: {}", loginHelper.getPd().getMessage());
								ServletUtils.setObjectIntoSession(httpServletRequest, session, MessageFormat.format(Costanti.MESSAGGIO_ERRORE_LOGIN_CON_PRINCIPAL_UTENTE_NON_VALIDO, username,	loginHelper.getPd().getMessage()), Costanti.PRINCIPAL_ERROR_MSG);

								ServletUtils.removeUserLoginFromSession(session);
								String redirPageUrl = StringUtils.isNotEmpty(loginUtenteNonValidoRedirectUrl) ? loginUtenteNonValidoRedirectUrl : 
									Utility.buildInternalRedirectUrl(httpServletRequest, "/" + LoginCostanti.SERVLET_NAME_LOGIN_MESSAGE_PAGE);

								String idToken = (String) session.getAttribute(OAuth2Costanti.ATTRIBUTE_NAME_ID_TOKEN);
								String logoutUrl = OAuth2Utilities.creaUrlLogout(idToken, oauth2LogoutUrl, redirPageUrl);

								// Messaggio di errore
								httpServletResponse.sendRedirect(logoutUrl);
								return;
							}

							log.debug("Utente non autorizzato: {}", loginHelper.getPd().getMessage());
							ServletUtils.setObjectIntoSession(httpServletRequest, session, MessageFormat.format(Costanti.MESSAGGIO_ERRORE_LOGIN_CON_PRINCIPAL_UTENTE_NON_AUTORIZZATO, username, loginHelper.getPd().getMessage()), Costanti.PRINCIPAL_ERROR_MSG);

							ServletUtils.removeUserLoginFromSession(session);
							String redirPageUrl = StringUtils.isNotEmpty(loginUtenteNonAutorizzatoRedirectUrl) ? loginUtenteNonAutorizzatoRedirectUrl : 
								Utility.buildInternalRedirectUrl(httpServletRequest, "/" + LoginCostanti.SERVLET_NAME_LOGIN_MESSAGE_PAGE);

							String idToken = (String) session.getAttribute(OAuth2Costanti.ATTRIBUTE_NAME_ID_TOKEN);
							String logoutUrl = OAuth2Utilities.creaUrlLogout(idToken, oauth2LogoutUrl, redirPageUrl);

							// Messaggio di errore
							httpServletResponse.sendRedirect(logoutUrl);
						}
					} catch(IOException | DriverUsersDBException | DriverControlStationException e) {
						// errore interno
						log.debug("Errore durante il login: {}", e.getMessage());
						ServletUtils.removeUserLoginFromSession(session);
						String redirPageUrl = StringUtils.isNotEmpty(loginErroreInternoRedirectUrl) ? loginErroreInternoRedirectUrl : httpServletRequest.getContextPath() +  "/" + LoginCostanti.SERVLET_NAME_LOGIN_MESSAGE_PAGE ;

						// Messaggio di errore
						ServletUtils.setObjectIntoSession(httpServletRequest, session, Costanti.MESSAGGIO_ERRORE_LOGIN_CON_PRINCIPAL_ERRORE_INTERNO, Costanti.PRINCIPAL_ERROR_MSG);

						String idToken = (String) session.getAttribute(OAuth2Costanti.ATTRIBUTE_NAME_ID_TOKEN);
						String logoutUrl = OAuth2Utilities.creaUrlLogout(idToken, oauth2LogoutUrl, redirPageUrl);
						
						httpServletResponse.sendRedirect(logoutUrl);
						e.printStackTrace();
					}
				} else {

					// ERRORE
					ServletUtils.removeUserLoginFromSession(session);

					// Messaggio di errore
					ServletUtils.setObjectIntoSession(httpServletRequest, session, Costanti.MESSAGGIO_ERRORE_LOGIN_CON_PRINCIPAL_PRINCIPAL_ASSENTE, Costanti.PRINCIPAL_ERROR_MSG);

					String redirPageUrl =
							StringUtils.isNotEmpty(loginUtenteNonAutorizzatoRedirectUrl) ? loginUtenteNonAutorizzatoRedirectUrl : httpServletRequest.getContextPath() + "/" + LoginCostanti.SERVLET_NAME_LOGIN_MESSAGE_PAGE;
				
					String idToken = (String) session.getAttribute(OAuth2Costanti.ATTRIBUTE_NAME_ID_TOKEN);
					String logoutUrl = OAuth2Utilities.creaUrlLogout(idToken, oauth2LogoutUrl, redirPageUrl);
					
					httpServletResponse.sendRedirect(logoutUrl);
				}
			} else {

				log.debug("Utente Loggato controllo validita' sessione..."); 
				// controllo se la sessione e' valida
				boolean isSessionInvalid = AuthorizationFilter.isSessionInvalid(httpServletRequest);

				// Se non sono loggato mi autentico e poi faccio redirect verso la pagina di welcome
				if(isSessionInvalid){
					log.debug("Utente Loggato controllo validita' sessione: [invalida]");
					ServletUtils.removeUserLoginFromSession(session);

					// Messaggio di errore
					ServletUtils.setObjectIntoSession(httpServletRequest, session, Costanti.MESSAGGIO_ERRORE_LOGIN_CON_PRINCIPAL_SESSIONE_SCADUTA, Costanti.PRINCIPAL_ERROR_MSG);

					String redirPageUrl =  StringUtils.isNotEmpty(loginSessioneScadutaRedirectUrl)
							? loginSessioneScadutaRedirectUrl : httpServletRequest.getContextPath() + "/" +  LoginCostanti.SERVLET_NAME_LOGIN_MESSAGE_PAGE;
					
					
					String idToken = (String) session.getAttribute(OAuth2Costanti.ATTRIBUTE_NAME_ID_TOKEN);
					String logoutUrl = OAuth2Utilities.creaUrlLogout(idToken, oauth2LogoutUrl, redirPageUrl);
					
					httpServletResponse.sendRedirect(logoutUrl);
				} 

				log.debug("Utente Loggato sessione valida.");
				ServletUtils.setObjectIntoSession(httpServletRequest, session, core.isSinglePdD(), CostantiControlStation.SESSION_PARAMETRO_SINGLE_PDD);

				// utente loggato
				ServletUtils.setObjectIntoSession(httpServletRequest, session, core.getTipoDatabase(), CostantiControlStation.SESSION_PARAMETRO_TIPO_DB);

				boolean singlePdDBooleanValue = core.isSinglePdD();

				// Controllo autorizzazione sulla funzionalita' richiesta, in base ai permessi dell'utente
				String servletRichiesta = null;
				try {
					ControlStationCore.logDebug("Check autorizzazione dell'utente "+userLogin+" per servlet ["+servletRichiesta+"] ...");

					// Se arrivo in questo punto sto richiedendo una pagina che riguarda una funzionalite' della console
					// Imposto il CharacterEncoding UTF-8 per risolvere i problemi di encoding evidenziati in OP-407 e OP-571
					httpServletRequest.setCharacterEncoding("UTF-8");

					// Non faccio verificare login/logout
					if (!"".equals(servletRichiesta) && !LoginCostanti.SERVLET_NAME_MESSAGE_PAGE.equals(servletRichiesta) && !LoginCostanti.SERVLET_NAME_LOGIN.equals(servletRichiesta) && !LoginCostanti.SERVLET_NAME_LOGOUT.equals(servletRichiesta)) {
						StringBuilder bfError = new StringBuilder();
						if(!GestoreAutorizzazioni.autorizzazioneUtente(singlePdDBooleanValue,ControlStationCore.getLog(), servletRichiesta, loginHelper, bfError)){
							ControlStationCore.logError("Autorizzazione negata all'utente "+userLogin+" per la servlet ["+servletRichiesta+"]: "+bfError.toString());
							AuthorizationFilter.setErrorMsg(generalHelper, session, httpServletRequest, httpServletResponse, LoginCostanti.INFO_JSP, LoginCostanti.LABEL_LOGIN_AUTORIZZAZIONE_NEGATA,MessageType.ERROR_SINTETICO, httpServletRequest.getServletContext(), HttpStatus.FORBIDDEN);
							// return so that we do not chain to other filters
							return;
						}
					}
					ControlStationCore.logDebug("Autorizzazione permessa all'utente "+userLogin+" per la servlet ["+servletRichiesta+"]");

					// Controllo se ho cliccato su un tab dove non e' stata rinnovata la sessione
					String refreshTabId = ServletUtils.getObjectFromSession(httpServletRequest, session, String.class, Costanti.SESSION_ATTRIBUTE_TAB_MAP_REFRESH_TAB_ID);
					if(refreshTabId != null) {
						ControlStationCore.logDebug("Rilevato click su tab dove e' scaduta la sessione, refresh in corso");
						// elimino il token per evitare loop
						ServletUtils.removeObjectFromSession(httpServletRequest, session, Costanti.SESSION_ATTRIBUTE_TAB_MAP_REFRESH_TAB_ID);

						httpServletResponse.sendRedirect(AuthorizationFilter.getRedirectToMessageServletRefreshSessione(httpServletRequest.getContextPath(), HttpStatus.FORBIDDEN));
						return;
					}

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

					if("".equals(servletRichiesta) || "/".equals(servletRichiesta))
						httpServletResponse.sendRedirect(AuthorizationFilter.getRedirectToMessageServlet());

				} catch (Exception e) {
					ControlStationCore.logError("Errore durante il processo di autorizzazione per l'utente ["+userLogin+"] : " + e.getMessage(),e);
					AuthorizationFilter.setErrorMsg(generalHelper, session, httpServletRequest, httpServletResponse, LoginCostanti.INFO_JSP, LoginCostanti.LABEL_LOGIN_ERRORE, httpServletRequest.getServletContext(), HttpStatus.SERVICE_UNAVAILABLE);
				}

			}

		} catch (UtilsException | OpenSPCoop2ConfigurationException e) {
			ControlStationCore.logError(ERROR_MSG_ERRORE_DURANTE_LA_LETTURA_DELLE_PROPERTIES + e.getMessage(),e);
			httpServletResponse.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
			try {
				httpServletResponse.getWriter().write(ERROR_MSG_AUTENTICAZIONE_OAUTH2_NON_DISPONIBILE_SI_E_VERIFICATO_UN_ERRORE + e.getMessage());
			} catch (IOException e1) {
				ControlStationCore.logError(ERROR_MSG_ERRORE_DURANTE_LA_LETTURA_DELLE_PROPERTIES + e1.getMessage(), e1);
			}
		} catch (PrincipalReaderException e) {
			httpServletResponse.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
			try {
				httpServletResponse.getWriter().write(ERROR_MSG_AUTENTICAZIONE_OAUTH2_NON_DISPONIBILE_SI_E_VERIFICATO_UN_ERRORE + e.getMessage());
			} catch (IOException e1) {
				ControlStationCore.logError("Errore durante la lettura del principal: " + e1.getMessage(), e1);
			}
		}
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
