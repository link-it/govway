/*
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
 *
 */


package org.openspcoop2.web.ctrlstat.servlet.login;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Properties;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.utils.transport.http.credential.IPrincipalReader;
import org.openspcoop2.utils.transport.http.credential.PrincipalReaderException;
import org.openspcoop2.utils.transport.http.credential.PrincipalReaderFactory;
import org.openspcoop2.utils.transport.http.credential.PrincipalReaderType;
import org.openspcoop2.web.ctrlstat.core.ConsoleSearch;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.ControlStationLogger;
import org.openspcoop2.web.ctrlstat.core.GestoreConsistenzaDati;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.servlet.ConsoleHelper;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.about.AboutCostanti;
import org.openspcoop2.web.ctrlstat.servlet.archivi.ArchiviCostanti;
import org.openspcoop2.web.ctrlstat.servlet.config.ConfigurazioneCostanti;
import org.openspcoop2.web.ctrlstat.servlet.utenti.UtentiCostanti;
import org.openspcoop2.web.ctrlstat.servlet.utils.UtilsCostanti;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.MessageType;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.security.exception.ValidationException;
import org.slf4j.Logger;

/**
 * AuthorizationFilter
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class AuthorizationFilter implements Filter {

	private FilterConfig filterConfig = null;
	private ControlStationCore core = null;
	private static Logger log = ControlStationLogger.getPddConsoleCoreLogger();
	
	private boolean loginApplication = true;
	private String loginTipo = null;
	private IPrincipalReader principalReader = null;
	private String loginUtenteNonAutorizzatoRedirectUrl = null;
	private String loginUtenteNonValidoRedirectUrl = null;
	private String loginErroreInternoRedirectUrl = null;
	private String loginSessioneScadutaRedirectUrl = null;
	
	private String jQueryVersion = null;
	private String jQueryUiVersion = null;
	
	@Override
	public void init(FilterConfig filterConfig) {
		
		this.filterConfig = filterConfig;
		this.jQueryVersion = this.filterConfig.getInitParameter(Costanti.FILTER_INIT_PARAMETER_JQUERY_VERSION);
		this.jQueryUiVersion = this.filterConfig.getInitParameter(Costanti.FILTER_INIT_PARAMETER_JQUERY_UI_VERSION);
		try {
			this.core = new ControlStationCore();
		} catch (Exception e) {
			System.err.println("Errore durante il caricamento iniziale: " + e.toString());
			e.printStackTrace(System.err);
		}
		
		// configurazione del filtro dalle properties
		
		try{
			this.loginApplication = this.core.isLoginApplication();
		}catch(Exception e){
			this.loginApplication = true;
		}
		
		try{
			if(!this.loginApplication){
				this.loginTipo = this.core.getLoginTipo();

				if(StringUtils.isEmpty(this.loginTipo))
					this.loginTipo = PrincipalReaderType.PRINCIPAL.getValue();
				
				this.principalReader = PrincipalReaderFactory.getReader(log, this.loginTipo);
				Properties prop = this.core.getLoginProperties();
				this.principalReader.init(prop); 
				
				this.loginUtenteNonAutorizzatoRedirectUrl = this.core.getLoginUtenteNonAutorizzatoRedirectUrl();
				this.loginUtenteNonValidoRedirectUrl = this.core.getLoginUtenteNonValidoRedirectUrl();
				this.loginErroreInternoRedirectUrl = this.core.getLoginErroreInternoRedirectUrl();
				this.loginSessioneScadutaRedirectUrl = this.core.getLoginSessioneScadutaRedirectUrl();
			}
		}catch(PrincipalReaderException e){
			log.error("Impossibile caricare il principal reader: "+e.getMessage());
			System.err.println("Errore durante il caricamento iniziale: " + e.toString());
			e.printStackTrace(System.err);
		} catch (Exception e) {
			log.error("Impossibile leggere la configurazione della console: "+e.getMessage());
			System.err.println("Errore durante il caricamento iniziale: " + e.toString());
			e.printStackTrace(System.err);
		}

		log.debug("Usa il principal per il controllo autorizzazione utente ["+!this.loginApplication+"]"); 

	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		
		GeneralHelper generalHelper = null;
		LoginHelper loginHelper = null;
		try {
//			System.out.println("SERVLET PATH ["+request.getServletPath()+"]");
//			System.out.println("SERVLET URI ["+request.getRequestURI()+"]");
//			System.out.println("SERVLET CONTEXT PATH ["+request.getContextPath()+"]");
//			System.out.println("SERVLET PATH INFO ["+request.getPathInfo()+"]");
//			System.out.println("SERVLET QUERY STRING ["+request.getQueryString()+"]");
//			System.out.println("SERVLET URL ["+request.getRequestURL()+"]");
			String contextPath = request.getContextPath(); // '/govwayConsole'
			
			// Non faccio il filtro sulla pagina di login e sulle immagini
			String urlRichiesta = request.getRequestURI();
			log.info("Richiesta Risorsa ["+urlRichiesta+"]"); 
			
			// Autenticazione gestita dall'applicazione 
			if(this.loginApplication){
				HttpSession session = request.getSession(true);
				ServletUtils.setObjectIntoSession(request, session, this.core.isSinglePdD(), CostantiControlStation.SESSION_PARAMETRO_SINGLE_PDD);
				
				if (isRisorsaProtetta(request)) { 
					
					generalHelper = new GeneralHelper(session);

					try {
						loginHelper = new LoginHelper(generalHelper.getCore(), request, new PageData(), session);
					} catch (Exception e) {
						ControlStationCore.logError("Errore rilevato durante l'authorizationFilter",e);
						throw new RuntimeException(e.getMessage(),e);
					}

					//System.out.println("ENTRO ["+request.getRequestURI()+"]");
					
					String userLogin = ServletUtils.getUserLoginFromSession(session);
					if (userLogin == null) {
						
						if((contextPath+"/").equals(urlRichiesta)){
							AuthorizationFilter.setErrorMsg(generalHelper, session, request, response, LoginCostanti.LOGIN_JSP,null, this.filterConfig);
						}
						else{
							AuthorizationFilter.setErrorMsg(generalHelper, session, request, response, LoginCostanti.LOGIN_JSP, LoginCostanti.LABEL_LOGIN_SESSIONE_SCADUTA,MessageType.ERROR_SINTETICO, this.filterConfig);
						}
						
						// return so that we do not chain to other filters
						return;
						
					} else {
						
						// utente loggato
						ServletUtils.setObjectIntoSession(request, session, this.core.getTipoDatabase(), CostantiControlStation.SESSION_PARAMETRO_TIPO_DB);
						
						boolean singlePdDBooleanValue = this.core.isSinglePdD();
						
						// Controllo autorizzazione sulla funzionalita' richiesta, in base ai permessi dell'utente
						String servletRichiesta = null;
						if (urlRichiesta.indexOf(".js") == -1) {
							try {
								//System.out.println("URL PRE PROCESSING ["+urlRichiesta+"]");
								servletRichiesta = urlRichiesta.substring((contextPath+"/").length());
								//if(servletRichiesta.endsWith(".do")){
								//	servletRichiesta = servletRichiesta.substring(0, servletRichiesta.length()-3);
								//}
								//System.out.println("URL POST PROCESSING ["+servletRichiesta+"]");
								ControlStationCore.logDebug("Check autorizzazione dell'utente "+userLogin+" per servlet ["+servletRichiesta+"] ...");
								
								// Se arrivo in questo punto sto richiedendo una pagina che riguarda una funzionalite' della console
								// Imposto il CharacterEncoding UTF-8 per risolvere i problemi di encoding evidenziati in OP-407 e OP-571
								//System.out.println("SET ENCODING RISOLVERE BUG");
								request.setCharacterEncoding("UTF-8");
								
								// Non faccio verificare login/logout
								if (!"".equals(servletRichiesta) && !LoginCostanti.SERVLET_NAME_MESSAGE_PAGE.equals(servletRichiesta) && !LoginCostanti.SERVLET_NAME_LOGIN.equals(servletRichiesta) && !LoginCostanti.SERVLET_NAME_LOGOUT.equals(servletRichiesta)) {
									StringBuilder bfError = new StringBuilder();
									if(GestoreAutorizzazioni.autorizzazioneUtente(singlePdDBooleanValue,ControlStationCore.getLog(), servletRichiesta, loginHelper, bfError)==false){
										ControlStationCore.logError("Autorizzazione negata all'utente "+userLogin+" per la servlet ["+servletRichiesta+"]: "+bfError.toString());
										setErrorMsg(generalHelper, session, request, response, LoginCostanti.INFO_JSP, LoginCostanti.LABEL_LOGIN_AUTORIZZAZIONE_NEGATA,MessageType.ERROR_SINTETICO, this.filterConfig);
										// return so that we do not chain to other filters
										return;
									}
								}
								ControlStationCore.logDebug("Autorizzazione permessa all'utente "+userLogin+" per la servlet ["+servletRichiesta+"]");
								
								// Check Reset delle ricerche
								String resetSearch = request.getParameter(CostantiControlStation.PARAMETRO_RESET_SEARCH);
								// validazione del parametro resetSearch
								if(!ServletUtils.checkParametroResetSearch(request, CostantiControlStation.PARAMETRO_RESET_SEARCH)) {
									ControlStationCore.logError("Autorizzazione negata all'utente "+userLogin+" per la servlet ["+servletRichiesta+"]: parametro resetSearch non valido.");
									setErrorMsg(generalHelper, session, request, response, LoginCostanti.INFO_JSP, LoginCostanti.LABEL_LOGIN_AUTORIZZAZIONE_NEGATA,MessageType.ERROR_SINTETICO, this.filterConfig);
									// return so that we do not chain to other filters
									return;
								}
								
								String postBackElement = request.getParameter(Costanti.POSTBACK_ELEMENT_NAME);
								if(ServletUtils.isCheckBoxEnabled(resetSearch) && (postBackElement==null || "".equals(postBackElement))) {
									
									for (int i = 0; i < Liste.getTotaleListe(); i++) {
										ServletUtils.removeRisultatiRicercaFromSession(request, session, i);
									}
									
									boolean existsRicerca = ServletUtils.existsSearchObjectFromSession(request, session);
									ConsoleSearch ricerca = (ConsoleSearch) ServletUtils.getSearchObjectFromSession(request, session, ConsoleSearch.class);
									if(ricerca!=null) {
										ricerca.reset();
										for (int i = 0; i < Liste.getTotaleListe(); i++) {
											loginHelper.initializeFilter(ricerca, i);
										}
										if(!existsRicerca) {
											// salvo in sessione le inizializzazioni
											ServletUtils.setSearchObjectIntoSession(request, session, ricerca);
										}
										ControlStationCore.logDebug("Effettuato reset della ricerca");					
									}
								}	
								
								// Validazione identificativi tab 
								if(!ServletUtils.checkParametro(request, Costanti.PARAMETER_PREV_TAB_KEY)) {
									ControlStationCore.logError("Autorizzazione negata all'utente "+userLogin+" per la servlet ["+servletRichiesta+"]: identificativo tab sorgente non valido.");
									setErrorMsg(generalHelper, session, request, response, LoginCostanti.INFO_JSP, LoginCostanti.LABEL_LOGIN_AUTORIZZAZIONE_NEGATA,MessageType.ERROR_SINTETICO, this.filterConfig);
									// return so that we do not chain to other filters
									return;
								}
								
								if(!ServletUtils.checkParametro(request, Costanti.PARAMETER_TAB_KEY)) {
									ControlStationCore.logError("Autorizzazione negata all'utente "+userLogin+" per la servlet ["+servletRichiesta+"]: identificativo tab corrente non valido.");
									setErrorMsg(generalHelper, session, request, response, LoginCostanti.INFO_JSP, LoginCostanti.LABEL_LOGIN_AUTORIZZAZIONE_NEGATA,MessageType.ERROR_SINTETICO, this.filterConfig);
									// return so that we do not chain to other filters
									return;
								}
								
								if("".equals(servletRichiesta) || "/".equals(servletRichiesta))
									response.sendRedirect(getRedirectToMessageServlet());
								
							} catch (Exception e) {
								ControlStationCore.logError("Errore durante il processo di autorizzazione della servlet ["+urlRichiesta
										+"] per l'utente ["+userLogin+"] : " + e.getMessage(),e);
								setErrorMsg(generalHelper, session, request, response, LoginCostanti.INFO_JSP, LoginCostanti.LABEL_LOGIN_ERRORE, this.filterConfig);
								// return so that we do not chain to other filters
								return;
							}
						}
						
						
						if (urlRichiesta.indexOf(".do") == -1 && urlRichiesta.indexOf(".js") == -1 
								&& urlRichiesta.indexOf("/"+ArchiviCostanti.SERVLET_NAME_PACKAGE_EXPORT) == -1
								&& urlRichiesta.indexOf("/"+ArchiviCostanti.SERVLET_NAME_MESSAGGI_DIAGNOSTICI_EXPORT) == -1
								&& urlRichiesta.indexOf("/"+ArchiviCostanti.SERVLET_NAME_TRACCE_EXPORT) == -1
								&& urlRichiesta.indexOf("/"+ArchiviCostanti.SERVLET_NAME_DOCUMENTI_EXPORT) == -1
								&& urlRichiesta.indexOf("/"+ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_SISTEMA_EXPORTER) == -1
								&& urlRichiesta.indexOf("/"+ArchiviCostanti.SERVLET_NAME_RESOCONTO_EXPORT) == -1 
								&& urlRichiesta.indexOf("/"+UtilsCostanti.SERVLET_NAME_INFORMAZIONI_UTILIZZO_OGGETTO) == -1) {
	
							AuthorizationFilter.setErrorMsg(generalHelper, session, request, response, LoginCostanti.LOGIN_JSP, LoginCostanti.LABEL_LOGIN_SESSIONE_SCADUTA,MessageType.ERROR_SINTETICO, this.filterConfig);
							// return so that we do not chain to other filters
							return;
						}
						
						// Controllo CSFR
						try {
							String msgErroreCSFR = verificaCSRF(generalHelper, session, request, loginHelper);
							if(msgErroreCSFR != null) {
								AuthorizationFilter.setErrorCSRFMsg(generalHelper, session, request, response, msgErroreCSFR, this.filterConfig);
								// return so that we do not chain to other filters
								return;
							}
						} catch(ValidationException e) {
							ControlStationCore.logError("Autorizzazione negata all'utente "+userLogin+" per la servlet ["+servletRichiesta+"]: " + e.getMessage(), e);
							setErrorMsg(generalHelper, session, request, response, LoginCostanti.INFO_JSP, LoginCostanti.LABEL_LOGIN_AUTORIZZAZIONE_NEGATA,MessageType.ERROR_SINTETICO, this.filterConfig);
							// return so that we do not chain to other filters
							return;
						}
					}
				}
				else {
					//	System.out.println("NON ENTRO ["+request.getRequestURI()+"]");
					// genero l'idTab
					ConsoleHelper.getTabId(log, null, request);
					
					// richiesta la pagina di login ma l'utente e' loggato visualizzo messaggio di errore
					if ( (urlRichiesta.indexOf(LoginCostanti.SERVLET_NAME_LOGIN) != -1) ){
						// se l'utente e' loggato faccio vedere il menu' e il messaggio di errore
						String userLogin = ServletUtils.getUserLoginFromSession(session);
						if(userLogin != null){
							if(generalHelper==null) {
								try{
									generalHelper = new GeneralHelper(session);
								}catch(Exception eClose){
									ControlStationCore.logError("Errore rilevato durante l'authorizationFilter (reInit General Helper)",eClose);
								}
							}
							
							String servletRichiesta = urlRichiesta.substring((contextPath+"/").length());
							ControlStationCore.logError("Autorizzazione negata all'utente "+userLogin+" per la servlet ["+servletRichiesta+"]: l'utenza autenticata non puo' visualizzare la schermata di login.");
							setErrorMsg(generalHelper, session, request, response, LoginCostanti.INFO_JSP, LoginCostanti.LABEL_LOGIN_AUTORIZZAZIONE_NEGATA,MessageType.ERROR_SINTETICO, this.filterConfig);
							// return so that we do not chain to other filters
							return;
						}
					}
					
					// Controllo risorse jQuery
					if ( (urlRichiesta.indexOf(CostantiControlStation.WEBJARS_DIR) != -1) ){
						if(!isValidJQueryResource(request)) {
							if(generalHelper==null) {
								try{
									generalHelper = new GeneralHelper(session);
								}catch(Exception eClose){
									ControlStationCore.logError("Errore rilevato durante l'authorizationFilter (reInit General Helper)",eClose);
								}
							}
							
							String userLogin = ServletUtils.getUserLoginFromSession(session);
							ControlStationCore.logError("Autorizzazione negata all'utente "+userLogin+" per la risorsa jQuery ["+urlRichiesta+"]");
							response.sendRedirect(getRedirectToMessageServletAutorizzazioneNegata(request.getContextPath()));
//							setErrorMsg(generalHelper, session, request, response, LoginCostanti.INFO_JSP, LoginCostanti.LABEL_LOGIN_AUTORIZZAZIONE_NEGATA,MessageType.ERROR_SINTETICO, this.filterConfig);
							// return so that we do not chain to other filters
							return;
						}
					}
				}
			} else {
				HttpSession session = request.getSession();
				
				// lettura delle informazioni di login dal principal 
				if (isRisorsaProtetta(request)) { 
					
					generalHelper = new GeneralHelper(session);

					try {
						loginHelper = new LoginHelper(generalHelper.getCore(), request, new PageData(), session);
					} catch (Exception e) {
						ControlStationCore.logError("Errore rilevato durante l'authorizationFilter",e);
						throw new RuntimeException(e.getMessage(),e);
					}
					
					//System.out.println("ENTRO ["+request.getRequestURI()+"]");
					
					// ricerca utenza in sessione
					String userLogin = ServletUtils.getUserLoginFromSession(session);
					
					// utente non loggato
					if (userLogin == null) {
						
						// se l'utente non e' in sessione provo a loggarlo leggendo il principal da metodo di autorizzazione impostato
						String username = null;
						try {
							username = this.principalReader.getPrincipal(request);
						} catch (PrincipalReaderException e) {
							log.error("Errore durante la lettura del principal: " + e.getMessage(),e);
						}
						
						// Se l'username che mi arriva e' settato vuol dire che sono autorizzato dal Container
						if(username != null){
							
							// Annullo quanto letto sull'auditing
							ControlStationCore.clearAuditManager();
							
							if(GestoreConsistenzaDati.gestoreConsistenzaDatiInEsecuzione){
								AuthorizationFilter.setErrorMsg(generalHelper, session, request, response, LoginCostanti.SERVLET_NAME_LOGIN_MESSAGE_PAGE, LoginCostanti.MESSAGGIO_INFO_CONTROLLO_CONSISTENZA_DATI_IN_CORSO, MessageType.INFO, this.filterConfig);
								// return so that we do not chain to other filters
								return;
							}
							
							// effettuo il login sul db
							try {
								boolean isOk = loginHelper.loginCheckData(LoginTipologia.WITHOUT_PASSWORD, username, null);
								
								if(isOk) {
									// utente loggato
									ServletUtils.setObjectIntoSession(request, session, this.core.getTipoDatabase(), CostantiControlStation.SESSION_PARAMETRO_TIPO_DB);
									
									LoginCore loginCore = new LoginCore(this.core);
									
									LoginSessionUtilities.setLoginParametersSession(request, session, loginCore, username);
									
									// loginHelper.updateTipoInterfaccia();
									
									loginCore.performAuditLogin(username);
									
									log.debug("Utente autorizzato, effettuo il redirect verso l'applicazione...");
									
									// redirect dopo il login
									this.setLoginWelcomeMsg(generalHelper, session, request, response, LoginCostanti.INFO_JSP, loginCore);
									return;
								} else {
									// utenza non valida
									if(loginHelper.getPd().getMessage().equals(LoginCostanti.MESSAGGIO_ERRORE_UTENTE_NON_ABILITATO_UTILIZZO_CONSOLE) 
											|| loginHelper.getPd().getMessage().equals(LoginCostanti.MESSAGGIO_ERRORE_UTENTE_NON_ABILITATO_UTILIZZO_CONSOLE_CONFIGURAZIONE_NON_CORRETTO)) {
										
										log.debug("Utente non valido: " + loginHelper.getPd().getMessage());
										ServletUtils.setObjectIntoSession(request, session, MessageFormat.format(Costanti.MESSAGGIO_ERRORE_LOGIN_CON_PRINCIPAL_UTENTE_NON_VALIDO, username,	loginHelper.getPd().getMessage()), Costanti.PRINCIPAL_ERROR_MSG);
										
										ServletUtils.removeUserLoginFromSession(session);
										String redirPageUrl = StringUtils.isNotEmpty(this.loginUtenteNonValidoRedirectUrl) ? this.loginUtenteNonValidoRedirectUrl : request.getContextPath() +  "/" + LoginCostanti.SERVLET_NAME_LOGIN_MESSAGE_PAGE;

										// Messaggio di errore
										response.sendRedirect(redirPageUrl);
										return;
									}
									
									log.debug("Utente non autorizzato: " + loginHelper.getPd().getMessage());
									ServletUtils.setObjectIntoSession(request, session, MessageFormat.format(Costanti.MESSAGGIO_ERRORE_LOGIN_CON_PRINCIPAL_UTENTE_NON_AUTORIZZATO, username, loginHelper.getPd().getMessage()), Costanti.PRINCIPAL_ERROR_MSG);
									
									ServletUtils.removeUserLoginFromSession(session);
									String redirPageUrl = StringUtils.isNotEmpty(this.loginUtenteNonAutorizzatoRedirectUrl) ? this.loginUtenteNonAutorizzatoRedirectUrl : request.getContextPath() +  "/" + LoginCostanti.SERVLET_NAME_LOGIN_MESSAGE_PAGE;

									// Messaggio di errore
									response.sendRedirect(redirPageUrl);
									return;
								}
							} catch(Exception e) {
								// errore interno
								log.debug("Errore durante il login: " + e.getMessage());
								ServletUtils.removeUserLoginFromSession(session);
								String redirPageUrl = StringUtils.isNotEmpty(this.loginErroreInternoRedirectUrl) ? this.loginErroreInternoRedirectUrl : request.getContextPath() +  "/" + LoginCostanti.SERVLET_NAME_LOGIN_MESSAGE_PAGE ;

								// Messaggio di errore
								ServletUtils.setObjectIntoSession(request, session, Costanti.MESSAGGIO_ERRORE_LOGIN_CON_PRINCIPAL_ERRORE_INTERNO, Costanti.PRINCIPAL_ERROR_MSG);

								response.sendRedirect(redirPageUrl);
								return;
							}
						} else {
						
							// ERRORE
							ServletUtils.removeUserLoginFromSession(session);
							
							// Messaggio di errore
							ServletUtils.setObjectIntoSession(request, session, Costanti.MESSAGGIO_ERRORE_LOGIN_CON_PRINCIPAL_PRINCIPAL_ASSENTE, Costanti.PRINCIPAL_ERROR_MSG);
							
							String redirPageUrl =
									StringUtils.isNotEmpty(this.loginUtenteNonAutorizzatoRedirectUrl) ? this.loginUtenteNonAutorizzatoRedirectUrl : request.getContextPath() + "/" + LoginCostanti.SERVLET_NAME_LOGIN_MESSAGE_PAGE;
							response.sendRedirect(redirPageUrl);
							return;
						}
					} else {
					
						log.debug("Utente Loggato controllo validita' sessione..."); 
						// controllo se la sessione e' valida
						boolean isSessionInvalid = isSessionInvalid(request);

						// Se non sono loggato mi autentico e poi faccio redirect verso la pagina di welcome
						if(isSessionInvalid){
							log.debug("Utente Loggato controllo validita' sessione: [invalida]");
							ServletUtils.removeUserLoginFromSession(session);
							
							// Messaggio di errore
							ServletUtils.setObjectIntoSession(request, session, Costanti.MESSAGGIO_ERRORE_LOGIN_CON_PRINCIPAL_SESSIONE_SCADUTA, Costanti.PRINCIPAL_ERROR_MSG);
							
							String redirPageUrl =  StringUtils.isNotEmpty(this.loginSessioneScadutaRedirectUrl)
									? this.loginSessioneScadutaRedirectUrl : request.getContextPath() + "/" +  LoginCostanti.SERVLET_NAME_LOGIN_MESSAGE_PAGE;
							response.sendRedirect(redirPageUrl);
							return;
						} 
						
						log.debug("Utente Loggato sessione valida.");
						ServletUtils.setObjectIntoSession(request, session, this.core.isSinglePdD(), CostantiControlStation.SESSION_PARAMETRO_SINGLE_PDD);
						
						// utente loggato
						ServletUtils.setObjectIntoSession(request, session, this.core.getTipoDatabase(), CostantiControlStation.SESSION_PARAMETRO_TIPO_DB);
						
						boolean singlePdDBooleanValue = this.core.isSinglePdD();
						
						// Controllo autorizzazione sulla funzionalita' richiesta, in base ai permessi dell'utente
						String servletRichiesta = null;
						if (urlRichiesta.indexOf(".js") == -1) {
							try {
								//System.out.println("URL PRE PROCESSING ["+urlRichiesta+"]");
								servletRichiesta = urlRichiesta.substring((contextPath+"/").length());
								//if(servletRichiesta.endsWith(".do")){
								//	servletRichiesta = servletRichiesta.substring(0, servletRichiesta.length()-3);
								//}
								//System.out.println("URL POST PROCESSING ["+servletRichiesta+"]");
								ControlStationCore.logDebug("Check autorizzazione dell'utente "+userLogin+" per servlet ["+servletRichiesta+"] ...");
								
								// Se arrivo in questo punto sto richiedendo una pagina che riguarda una funzionalite' della console
								// Imposto il CharacterEncoding UTF-8 per risolvere i problemi di encoding evidenziati in OP-407 e OP-571
								//System.out.println("SET ENCODING RISOLVERE BUG");
								request.setCharacterEncoding("UTF-8");
								
								// Non faccio verificare login/logout
								if (!"".equals(servletRichiesta) && !LoginCostanti.SERVLET_NAME_MESSAGE_PAGE.equals(servletRichiesta) && !LoginCostanti.SERVLET_NAME_LOGIN.equals(servletRichiesta) && !LoginCostanti.SERVLET_NAME_LOGOUT.equals(servletRichiesta)) {
									StringBuilder bfError = new StringBuilder();
									if(GestoreAutorizzazioni.autorizzazioneUtente(singlePdDBooleanValue,ControlStationCore.getLog(), servletRichiesta, loginHelper, bfError)==false){
										ControlStationCore.logError("Autorizzazione negata all'utente "+userLogin+" per la servlet ["+servletRichiesta+"]: "+bfError.toString());
										setErrorMsg(generalHelper, session, request, response, LoginCostanti.INFO_JSP, LoginCostanti.LABEL_LOGIN_AUTORIZZAZIONE_NEGATA,MessageType.ERROR_SINTETICO, this.filterConfig);
										// return so that we do not chain to other filters
										return;
									}
								}
								ControlStationCore.logDebug("Autorizzazione permessa all'utente "+userLogin+" per la servlet ["+servletRichiesta+"]");
								
								// Check Reset delle ricerche
								String resetSearch = request.getParameter(CostantiControlStation.PARAMETRO_RESET_SEARCH);
								// validazione del parametro resetSearch
								if(!ServletUtils.checkParametroResetSearch(request, CostantiControlStation.PARAMETRO_RESET_SEARCH)) {
									ControlStationCore.logError("Autorizzazione negata all'utente "+userLogin+" per la servlet ["+servletRichiesta+"]: parametro resetSearch non valido.");
									setErrorMsg(generalHelper, session, request, response, LoginCostanti.INFO_JSP, LoginCostanti.LABEL_LOGIN_AUTORIZZAZIONE_NEGATA,MessageType.ERROR_SINTETICO, this.filterConfig);
									// return so that we do not chain to other filters
									return;
								}
								
								String postBackElement = request.getParameter(Costanti.POSTBACK_ELEMENT_NAME);
								if(ServletUtils.isCheckBoxEnabled(resetSearch) && (postBackElement==null || "".equals(postBackElement))) {
									
									for (int i = 0; i < Liste.getTotaleListe(); i++) {
										ServletUtils.removeRisultatiRicercaFromSession(request, session, i);
									}
									
									boolean existsRicerca = ServletUtils.existsSearchObjectFromSession(request, session);
									ConsoleSearch ricerca = (ConsoleSearch) ServletUtils.getSearchObjectFromSession(request, session, ConsoleSearch.class);
									if(ricerca!=null) {
										ricerca.reset();
										for (int i = 0; i < Liste.getTotaleListe(); i++) {
											loginHelper.initializeFilter(ricerca, i);
										}
										if(!existsRicerca) {
											// salvo in sessione le inizializzazioni
											ServletUtils.setSearchObjectIntoSession(request, session, ricerca);
										}
										ControlStationCore.logDebug("Effettuato reset della ricerca");					
									}
								}		
								
								// Validazione identificativi tab 
								if(!ServletUtils.checkParametro(request, Costanti.PARAMETER_PREV_TAB_KEY)) {
									ControlStationCore.logError("Autorizzazione negata all'utente "+userLogin+" per la servlet ["+servletRichiesta+"]: identificativo tab sorgente non valido.");
									setErrorMsg(generalHelper, session, request, response, LoginCostanti.INFO_JSP, LoginCostanti.LABEL_LOGIN_AUTORIZZAZIONE_NEGATA,MessageType.ERROR_SINTETICO, this.filterConfig);
									// return so that we do not chain to other filters
									return;
								}
								
								if(!ServletUtils.checkParametro(request, Costanti.PARAMETER_TAB_KEY)) {
									ControlStationCore.logError("Autorizzazione negata all'utente "+userLogin+" per la servlet ["+servletRichiesta+"]: identificativo tab corrente non valido.");
									setErrorMsg(generalHelper, session, request, response, LoginCostanti.INFO_JSP, LoginCostanti.LABEL_LOGIN_AUTORIZZAZIONE_NEGATA,MessageType.ERROR_SINTETICO, this.filterConfig);
									// return so that we do not chain to other filters
									return;
								}
								
								if("".equals(servletRichiesta) || "/".equals(servletRichiesta))
									response.sendRedirect(getRedirectToMessageServlet());
								
							} catch (Exception e) {
								ControlStationCore.logError("Errore durante il processo di autorizzazione della servlet ["+urlRichiesta
										+"] per l'utente ["+userLogin+"] : " + e.getMessage(),e);
								setErrorMsg(generalHelper, session, request, response, LoginCostanti.INFO_JSP, LoginCostanti.LABEL_LOGIN_ERRORE, this.filterConfig);
								// return so that we do not chain to other filters
								return;
							}
						}
						
						
						if (urlRichiesta.indexOf(".do") == -1 && urlRichiesta.indexOf(".js") == -1 
								&& urlRichiesta.indexOf("/"+ArchiviCostanti.SERVLET_NAME_PACKAGE_EXPORT) == -1
								&& urlRichiesta.indexOf("/"+ArchiviCostanti.SERVLET_NAME_MESSAGGI_DIAGNOSTICI_EXPORT) == -1
								&& urlRichiesta.indexOf("/"+ArchiviCostanti.SERVLET_NAME_TRACCE_EXPORT) == -1
								&& urlRichiesta.indexOf("/"+ArchiviCostanti.SERVLET_NAME_DOCUMENTI_EXPORT) == -1
								&& urlRichiesta.indexOf("/"+ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_SISTEMA_EXPORTER) == -1
								&& urlRichiesta.indexOf("/"+ArchiviCostanti.SERVLET_NAME_RESOCONTO_EXPORT) == -1 
								&& urlRichiesta.indexOf("/"+UtilsCostanti.SERVLET_NAME_INFORMAZIONI_UTILIZZO_OGGETTO) == -1) {
	
							AuthorizationFilter.setErrorMsg(generalHelper, session, request, response, LoginCostanti.LOGIN_JSP, LoginCostanti.LABEL_LOGIN_SESSIONE_SCADUTA,MessageType.ERROR_SINTETICO, this.filterConfig);
							// return so that we do not chain to other filters
							return;
						}
						
						// Controllo CSFR
						try {
							String msgErroreCSFR = verificaCSRF(generalHelper, session, request, loginHelper);
							if(msgErroreCSFR != null) {
								AuthorizationFilter.setErrorCSRFMsg(generalHelper, session, request, response, msgErroreCSFR, this.filterConfig);
								// return so that we do not chain to other filters
								return;
							}
						} catch(ValidationException e) {
							ControlStationCore.logError("Autorizzazione negata all'utente "+userLogin+" per la servlet ["+servletRichiesta+"]: " + e.getMessage(), e);
							setErrorMsg(generalHelper, session, request, response, LoginCostanti.INFO_JSP, LoginCostanti.LABEL_LOGIN_AUTORIZZAZIONE_NEGATA,MessageType.ERROR_SINTETICO, this.filterConfig);
							// return so that we do not chain to other filters
							return;
						}
					}
				} // end isRisorsaProtetta 
				else {
					//	System.out.println("NON ENTRO ["+request.getRequestURI()+"]");
					// genero l'idTab
					ConsoleHelper.getTabId(log, null, request);
					
					// richiesta una pagina interna libera
					if ( (urlRichiesta.indexOf(LoginCostanti.LOGIN_AS_JSP) != -1) // pagina di login AS
							|| (urlRichiesta.indexOf(LoginCostanti.LOGOUT_AS_JSP) != -1) // pagina di logout AS 
							|| (urlRichiesta.indexOf(LoginCostanti.LOGIN_FAILURE_JSP) != -1) // pagina errori login AS 
							) {
						if(generalHelper==null) {
							try{
								generalHelper = new GeneralHelper(session);
							}catch(Exception eClose){
								ControlStationCore.logError("Errore rilevato durante l'authorizationFilter (reInit General Helper)",eClose);
							}
						}
						
						// Inizializzo PageData
						PageData pd = generalHelper.initPageData();

						// Inizializzo GeneralData
						GeneralData gd = generalHelper.initGeneralData(request,LoginCostanti.SERVLET_NAME_LOGIN);
						
						// se l'utente e' loggato faccio vedere il menu'
						String userLogin = ServletUtils.getUserLoginFromSession(session);
						if(userLogin != null){
							try{
								LoginHelper lH = new LoginHelper(generalHelper.getCore(), request, pd, session);
								lH.makeMenu();
							}catch(Exception e){
								throw new ServletException(e);
							}
						}
						ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd, true);
					}
					
					// Controllo risorse jQuery
					if ( (urlRichiesta.indexOf(CostantiControlStation.WEBJARS_DIR) != -1) ){
						if(!isValidJQueryResource(request)) {
							if(generalHelper==null) {
								try{
									generalHelper = new GeneralHelper(session);
								}catch(Exception eClose){
									ControlStationCore.logError("Errore rilevato durante l'authorizationFilter (reInit General Helper)",eClose);
								}
							}
							
							String userLogin = ServletUtils.getUserLoginFromSession(session);
							ControlStationCore.logError("Autorizzazione negata all'utente "+userLogin+" per la risorsa jQuery ["+urlRichiesta+"]");
							// faccio un redirect esplicito alla servlet dei messaggi
							response.sendRedirect(getRedirectToMessageServletAutorizzazioneNegata(request.getContextPath()));
//							setErrorMsg(generalHelper, session, request, response, LoginCostanti.INFO_JSP, LoginCostanti.LABEL_LOGIN_AUTORIZZAZIONE_NEGATA,MessageType.ERROR_SINTETICO, this.filterConfig);
							// return so that we do not chain to other filters
							return;
						}
					}
				}
			}
			// allow others filter to be chained
			chain.doFilter(request, response);
		} catch (Exception e) {
			ControlStationCore.logError("Errore rilevato durante l'authorizationFilter",e);
			try{
				HttpSession session = request.getSession();
				
				if(generalHelper==null) {
					try{
						generalHelper = new GeneralHelper(session);
					}catch(Exception eClose){
						ControlStationCore.logError("Errore rilevato durante l'authorizationFilter (reInit General Helper)",e);
					}
				}
				AuthorizationFilter.setErrorMsg(generalHelper, session, request, response, LoginCostanti.INFO_JSP, LoginCostanti.LABEL_LOGIN_ERRORE, this.filterConfig);
				// return so that we do not chain to other filters
				return;
			}catch(Exception eClose){
				ControlStationCore.logError("Errore rilevato durante l'authorizationFilter (segnalazione errore)",e);
			}
		}

	}
	
	private boolean isSessionInvalid(HttpServletRequest httpServletRequest) {
		boolean sessionInValid = 
				//(httpServletRequest.getRequestedSessionId() != null)
				(httpServletRequest.getSession(false)!=null)
				&& !httpServletRequest.isRequestedSessionIdValid();
		return sessionInValid;
	}
	
	private boolean isRisorsaProtetta(HttpServletRequest request){
		String urlRichiesta = request.getRequestURI();
		HttpSession session = request.getSession(true); 
		String changePwd = ServletUtils.getObjectFromSession(request, session, String.class, LoginCostanti.ATTRIBUTO_MODALITA_CAMBIA_PWD_SCADUTA);
		if(changePwd != null && (urlRichiesta.indexOf("/"+UtentiCostanti.SERVLET_NAME_UTENTE_PASSWORD_CHANGE) > -1)) {
			return false;
		}
		
		if ((urlRichiesta.indexOf("/"+LoginCostanti.SERVLET_NAME_LOGIN) == -1) 
				&& (urlRichiesta.indexOf("/"+LoginCostanti.SERVLET_NAME_LOGIN_MESSAGE_PAGE) == -1) 
				&& (urlRichiesta.indexOf("/"+CostantiControlStation.IMAGES_DIR) == -1) 
				&& (urlRichiesta.indexOf("/"+CostantiControlStation.CSS_DIR) == -1)
				&& (urlRichiesta.indexOf("/"+CostantiControlStation.FONTS_DIR) == -1)
				&& (urlRichiesta.indexOf("/"+CostantiControlStation.JS_DIR) == -1)
				&& (urlRichiesta.indexOf("/"+AboutCostanti.SERVLET_NAME_ABOUT) == -1) 
				&& (urlRichiesta.indexOf("/"+CostantiControlStation.PUBLIC_DIR) == -1) // risorse public
				&& (urlRichiesta.indexOf("/"+CostantiControlStation.WEBJARS_DIR) == -1) // risorse jquery
				) {
			return true;
		}
		
		return false;
	}

	@Override
	public void destroy() {
		this.filterConfig = null;
	}
	
	
	public void setLoginWelcomeMsg(GeneralHelper gh, HttpSession session,
			HttpServletRequest request,HttpServletResponse response,String servletDispatcher, LoginCore loginCore) throws IOException,ServletException {
		
		// Inizializzo PageData
		PageData pd = gh.initPageData();

		// Inizializzo GeneralData
		GeneralData gd = gh.initGeneralData(request,LoginCostanti.SERVLET_NAME_LOGIN);
		
		// se l'utente e' loggato faccio vedere il menu'
		String userLogin = ServletUtils.getUserLoginFromSession(session);
		if(userLogin != null){
			try{
				LoginHelper lH = new LoginHelper(gh.getCore(), request, pd, session);
				// Preparo il menu
				lH.makeMenu();
				
				LoginSessionUtilities.setLoginParametersSession(request, session, loginCore, userLogin);
				//lH.updateTipoInterfaccia();
				
				// Inizializzo parametri di ricerca
				ConsoleSearch ricerca = (ConsoleSearch) ServletUtils.getSearchObjectFromSession(request, session, ConsoleSearch.class);
				lH.initializeFilter(ricerca);
				
				// Boolean verifico configurazione
				Login.impostaMessaggioEsitoLogin(pd, loginCore);
				
				// Inizializzo di nuovo GeneralData, dopo aver messo
				// in sessione la login dell'utente
				gd = gh.initGeneralData(request);
			
			}catch(Exception e){
				throw new ServletException(e);
			}
		}
		
		ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

		this.filterConfig.getServletContext().getRequestDispatcher(servletDispatcher).forward(request, response);
	}
	
	
	public static void setErrorMsg(GeneralHelper gh, HttpSession session,
			HttpServletRequest request,HttpServletResponse response,String servletDispatcher,String msgErrore, FilterConfig filterConfig) throws IOException,ServletException {
		AuthorizationFilter.setErrorMsg(gh, session, request, response, servletDispatcher, msgErrore, null, MessageType.ERROR, filterConfig); 
	}
	
	public static void setErrorMsg(GeneralHelper gh, HttpSession session,
			HttpServletRequest request,HttpServletResponse response,String servletDispatcher,String msgErrore, MessageType messageType, FilterConfig filterConfig) throws IOException,ServletException {
		AuthorizationFilter.setErrorMsg(gh, session, request, response, servletDispatcher, msgErrore, null, messageType, filterConfig); 
	}
	
	public static void setErrorMsg(GeneralHelper gh,HttpSession session,
			HttpServletRequest request,HttpServletResponse response,String servletDispatcher,String msgErrore, String msgErroreTitle, MessageType messageType, FilterConfig filterConfig) throws IOException,ServletException {
		
		// Inizializzo PageData
		PageData pd = gh.initPageData();

		// Inizializzo GeneralData
		GeneralData gd = gh.initGeneralData(request,LoginCostanti.SERVLET_NAME_LOGIN);
		
		// se l'utente e' loggato faccio vedere il menu'
		String userLogin = ServletUtils.getUserLoginFromSession(session);
		if(userLogin != null){
			try{
				LoginHelper lH = new LoginHelper(gh.getCore(), request, pd, session);
				lH.makeMenu();
			}catch(Exception e){
				throw new ServletException(e);
			}
		}
		
		if(msgErrore!=null)
			pd.setMessage(msgErrore,msgErroreTitle,messageType);
		
		ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd, true);

		filterConfig.getServletContext().getRequestDispatcher(servletDispatcher).forward(request, response);
	}
	
	private String getRedirectToMessageServlet() {
		return new Parameter("", LoginCostanti.SERVLET_NAME_MESSAGE_PAGE,
				new Parameter(Costanti.PARAMETER_MESSAGE_TEXT,LoginCostanti.LABEL_CONSOLE_RIPRISTINATA),
				new Parameter(Costanti.PARAMETER_MESSAGE_TYPE,MessageType.INFO_SINTETICO.toString())
				).getValue();
	}
	
	private String getRedirectToMessageServletAutorizzazioneNegata(String contextPath) {
		return new Parameter("", contextPath + "/" + LoginCostanti.SERVLET_NAME_MESSAGE_PAGE,
				new Parameter(Costanti.PARAMETER_MESSAGE_TEXT,LoginCostanti.LABEL_LOGIN_AUTORIZZAZIONE_NEGATA),
				new Parameter(Costanti.PARAMETER_MESSAGE_TYPE,MessageType.ERROR_SINTETICO.toString())
				).getValue();
	}
		
	private String verificaCSRF(GeneralHelper gh, HttpSession session, HttpServletRequest request, LoginHelper loginHelper) throws Exception {
		
		String msg = null;
		// Riconoscere se si tratta di una scrittura
		if(isRichiestaScrittura(request, loginHelper)) {
			// lettura parametro csfr dalla request
			String csfrTokenFromRequest = loginHelper.getParameter(Costanti.PARAMETRO_CSRF_TOKEN);
		
			// check validita'
			if(!ServletUtils.verificaTokenCSRF(csfrTokenFromRequest, request, session, this.core.getValiditaTokenCsrf())) {
				msg = Costanti.MESSAGGIO_ERRORE_CSRF_TOKEN_NON_VALIDO;
			}
		}
		
		// Controllo se devo generare un nuovo token CSRF
		if(isGeneraNuovoTokenCSRF(request, loginHelper)) {
			ServletUtils.generaESalvaTokenCSRF(request, session);
		}
		
		return msg;
	}
	
	public static void setErrorCSRFMsg(GeneralHelper gh, HttpSession session, HttpServletRequest request,HttpServletResponse response,String msgErrore, FilterConfig filterConfig) throws IOException,ServletException {
		String servletDispatcher = LoginCostanti.INFO_JSP;
		MessageType messageType = MessageType.ERROR;
		String msgErroreTitle = null;
		AuthorizationFilter.setErrorMsg(gh, session, request, response, servletDispatcher , msgErrore, msgErroreTitle, messageType, filterConfig); 
	}
	
	private boolean isRichiestaScrittura(HttpServletRequest request, LoginHelper loginHelper) throws Exception{
		// Per le operazioni di scrittura si cerca il parametro azione
		// scarto le chiamate in postback
		
		if(!loginHelper.isPostBack())  {
			// validazione del parametro azione
			if(!ServletUtils.checkParametroAzione(loginHelper.getRequest(), Costanti.PARAMETRO_AZIONE)) {
				throw new ValidationException("Il parametro " + Costanti.PARAMETRO_AZIONE + " contiene un valore non valido.");
			}
			
			String azione = loginHelper.getParameter(Costanti.PARAMETRO_AZIONE);
			if(azione != null) {
				return true;
			}
		}
		
		return false;
	}
	
	private boolean isGeneraNuovoTokenCSRF(HttpServletRequest request, LoginHelper loginHelper) {
		// token attuale viene invalidato e ne viene generato uno nuovo
		// tranne che per le richieste verso la servlet informazioniUtilizzoOggettoRegistro
		String urlRichiesta = request.getRequestURI();
		if ((urlRichiesta.indexOf("/"+UtilsCostanti.SERVLET_NAME_INFORMAZIONI_UTILIZZO_OGGETTO) != -1))  {
			ControlStationCore.logDebug("Richiesta Risorsa ["+urlRichiesta+"], Token CSRF non verra' aggiornato.");
			return false;
		}
		
		return true;
	}
	
	private boolean isValidJQueryResource(HttpServletRequest request) {
		String urlRichiesta = request.getRequestURI();
		
		// regola speciale per le risorse jquery, sono ammesse solo le due librerie jquery-min e jquery-ui-min
		if((urlRichiesta.indexOf("/"+MessageFormat.format(Costanti.LIB_JQUERY_PATH, this.jQueryVersion)) == -1) // jquery da jar
		&& (urlRichiesta.indexOf("/"+MessageFormat.format(Costanti.LIB_JQUERY_UI_PATH, this.jQueryUiVersion)) == -1) // jquery-ui da jar
		) {
			return false;
		}
		
		return true;
	}
}
