/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it). 
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

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.utils.transport.http.credential.IPrincipalReader;
import org.openspcoop2.utils.transport.http.credential.PrincipalReaderException;
import org.openspcoop2.utils.transport.http.credential.PrincipalReaderFactory;
import org.openspcoop2.utils.transport.http.credential.PrincipalReaderType;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.ControlStationLogger;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.gestori.GestoreConsistenzaDati;
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
	
	@Override
	public void init(FilterConfig filterConfig) {
		
		this.filterConfig = filterConfig;
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
				session.setAttribute(CostantiControlStation.SESSION_PARAMETRO_SINGLE_PDD, this.core.isSinglePdD());
				
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
							this.setErrorMsg(generalHelper, session, request, response, LoginCostanti.LOGIN_JSP,null);
						}
						else{
							this.setErrorMsg(generalHelper, session, request, response, LoginCostanti.LOGIN_JSP, LoginCostanti.LABEL_LOGIN_SESSIONE_SCADUTA,MessageType.ERROR_SINTETICO);
						}
						
						// return so that we do not chain to other filters
						return;
						
					} else {
						
						// utente loggato
						session.setAttribute(CostantiControlStation.SESSION_PARAMETRO_TIPO_DB, this.core.getTipoDatabase());
						
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
									if(GestoreAutorizzazioni.autorizzazioneUtente(singlePdDBooleanValue,ControlStationCore.getLog(), servletRichiesta, loginHelper)==false){
										ControlStationCore.logError("Autorizzazione negata all'utente "+userLogin+" per la servlet ["+servletRichiesta+"]");
										setErrorMsg(generalHelper, session, request, response, LoginCostanti.INFO_JSP, LoginCostanti.LABEL_LOGIN_AUTORIZZAZIONE_NEGATA,MessageType.ERROR_SINTETICO);
										// return so that we do not chain to other filters
										return;
									}
								}
								ControlStationCore.logDebug("Autorizzazione permessa all'utente "+userLogin+" per la servlet ["+servletRichiesta+"]");
								
								// Check Reset delle ricerche
								String resetSearch = request.getParameter(CostantiControlStation.PARAMETRO_RESET_SEARCH);
								String postBackElement = request.getParameter(Costanti.POSTBACK_ELEMENT_NAME);
								if(ServletUtils.isCheckBoxEnabled(resetSearch) && (postBackElement==null || "".equals(postBackElement))) {
									
									for (int i = 0; i < Liste.getTotaleListe(); i++) {
										ServletUtils.removeRisultatiRicercaFromSession(session, i);
									}
									
									boolean existsRicerca = ServletUtils.existsSearchObjectFromSession(session);
									Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);
									if(ricerca!=null) {
										ricerca.reset();
										for (int i = 0; i < Liste.getTotaleListe(); i++) {
											loginHelper.initializeFilter(ricerca, i);
										}
										if(!existsRicerca) {
											// salvo in sessione le inizializzazioni
											ServletUtils.setSearchObjectIntoSession(session, ricerca);
										}
										ControlStationCore.logDebug("Effettuato reset della ricerca");					
									}
								}					
								
								if("".equals(servletRichiesta) || "/".equals(servletRichiesta))
									response.sendRedirect(getRedirectToMessageServlet());
								
							} catch (Exception e) {
								ControlStationCore.logError("Errore durante il processo di autorizzazione della servlet ["+urlRichiesta
										+"] per l'utente ["+userLogin+"] : " + e.getMessage(),e);
								setErrorMsg(generalHelper, session, request, response, LoginCostanti.INFO_JSP, LoginCostanti.LABEL_LOGIN_ERRORE);
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
	
							this.setErrorMsg(generalHelper, session, request, response, LoginCostanti.LOGIN_JSP, LoginCostanti.LABEL_LOGIN_SESSIONE_SCADUTA,MessageType.ERROR_SINTETICO);
							// return so that we do not chain to other filters
							return;
						}
					}
				}
				//else {
				//	System.out.println("NON ENTRO ["+request.getRequestURI()+"]");
				//}
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
								this.setErrorMsg(generalHelper, session, request, response, LoginCostanti.SERVLET_NAME_LOGIN_MESSAGE_PAGE, LoginCostanti.MESSAGGIO_INFO_CONTROLLO_CONSISTENZA_DATI_IN_CORSO, MessageType.INFO);
								// return so that we do not chain to other filters
								return;
							}
							
							// effettuo il login sul db
							try {
								boolean isOk = loginHelper.loginCheckData(LoginTipologia.WITHOUT_PASSWORD, username, null);
								
								if(isOk) {
									// utente loggato
									session.setAttribute(CostantiControlStation.SESSION_PARAMETRO_TIPO_DB, this.core.getTipoDatabase());
									
									LoginCore loginCore = new LoginCore(this.core);
									
									LoginSessionUtilities.setLoginParametersSession(session, loginCore, username);
									
									loginHelper.updateTipoInterfaccia();
									
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
										session.setAttribute(Costanti.PRINCIPAL_ERROR_MSG, MessageFormat.format(Costanti.MESSAGGIO_ERRORE_LOGIN_CON_PRINCIPAL_UTENTE_NON_VALIDO, username,	loginHelper.getPd().getMessage())); 
										
										ServletUtils.removeUserLoginFromSession(session);
										String redirPageUrl = StringUtils.isNotEmpty(this.loginUtenteNonValidoRedirectUrl) ? this.loginUtenteNonValidoRedirectUrl : request.getContextPath() +  "/" + LoginCostanti.SERVLET_NAME_LOGIN_MESSAGE_PAGE;

										// Messaggio di errore
										response.sendRedirect(redirPageUrl);
										return;
									}
									
									log.debug("Utente non autorizzato: " + loginHelper.getPd().getMessage());
									session.setAttribute(Costanti.PRINCIPAL_ERROR_MSG, MessageFormat.format(Costanti.MESSAGGIO_ERRORE_LOGIN_CON_PRINCIPAL_UTENTE_NON_AUTORIZZATO, username,	loginHelper.getPd().getMessage())); 
									
									ServletUtils.removeUserLoginFromSession(session);
									String redirPageUrl =
											StringUtils.isNotEmpty(this.loginUtenteNonAutorizzatoRedirectUrl) ? this.loginUtenteNonAutorizzatoRedirectUrl : request.getContextPath() +  "/" + LoginCostanti.SERVLET_NAME_LOGIN_MESSAGE_PAGE;

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
								session.setAttribute(Costanti.PRINCIPAL_ERROR_MSG, Costanti.MESSAGGIO_ERRORE_LOGIN_CON_PRINCIPAL_ERRORE_INTERNO); 

								response.sendRedirect(redirPageUrl);
								return;
							}
						} else {
						
							// ERRORE
							ServletUtils.removeUserLoginFromSession(session);
							
							// Messaggio di errore
							session.setAttribute(Costanti.PRINCIPAL_ERROR_MSG, Costanti.MESSAGGIO_ERRORE_LOGIN_CON_PRINCIPAL_PRINCIPAL_ASSENTE); 
							
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
							session.setAttribute(Costanti.PRINCIPAL_ERROR_MSG, Costanti.MESSAGGIO_ERRORE_LOGIN_CON_PRINCIPAL_SESSIONE_SCADUTA); 
							
							String redirPageUrl =  StringUtils.isNotEmpty(this.loginSessioneScadutaRedirectUrl)
									? this.loginSessioneScadutaRedirectUrl : request.getContextPath() + "/" +  LoginCostanti.SERVLET_NAME_LOGIN_MESSAGE_PAGE;
							response.sendRedirect(redirPageUrl);
							return;
						} 
						
						log.debug("Utente Loggato sessione valida.");
						session.setAttribute(CostantiControlStation.SESSION_PARAMETRO_SINGLE_PDD, this.core.isSinglePdD());
						
						// utente loggato
						session.setAttribute(CostantiControlStation.SESSION_PARAMETRO_TIPO_DB, this.core.getTipoDatabase());
						
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
									if(GestoreAutorizzazioni.autorizzazioneUtente(singlePdDBooleanValue,ControlStationCore.getLog(), servletRichiesta, loginHelper)==false){
										ControlStationCore.logError("Autorizzazione negata all'utente "+userLogin+" per la servlet ["+servletRichiesta+"]");
										setErrorMsg(generalHelper, session, request, response, LoginCostanti.INFO_JSP, LoginCostanti.LABEL_LOGIN_AUTORIZZAZIONE_NEGATA,MessageType.ERROR_SINTETICO);
										// return so that we do not chain to other filters
										return;
									}
								}
								ControlStationCore.logDebug("Autorizzazione permessa all'utente "+userLogin+" per la servlet ["+servletRichiesta+"]");
								
								// Check Reset delle ricerche
								String resetSearch = request.getParameter(CostantiControlStation.PARAMETRO_RESET_SEARCH);
								String postBackElement = request.getParameter(Costanti.POSTBACK_ELEMENT_NAME);
								if(ServletUtils.isCheckBoxEnabled(resetSearch) && (postBackElement==null || "".equals(postBackElement))) {
									
									for (int i = 0; i < Liste.getTotaleListe(); i++) {
										ServletUtils.removeRisultatiRicercaFromSession(session, i);
									}
									
									boolean existsRicerca = ServletUtils.existsSearchObjectFromSession(session);
									Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);
									if(ricerca!=null) {
										ricerca.reset();
										for (int i = 0; i < Liste.getTotaleListe(); i++) {
											loginHelper.initializeFilter(ricerca, i);
										}
										if(!existsRicerca) {
											// salvo in sessione le inizializzazioni
											ServletUtils.setSearchObjectIntoSession(session, ricerca);
										}
										ControlStationCore.logDebug("Effettuato reset della ricerca");					
									}
								}					
								
								if("".equals(servletRichiesta) || "/".equals(servletRichiesta))
									response.sendRedirect(getRedirectToMessageServlet());
								
							} catch (Exception e) {
								ControlStationCore.logError("Errore durante il processo di autorizzazione della servlet ["+urlRichiesta
										+"] per l'utente ["+userLogin+"] : " + e.getMessage(),e);
								setErrorMsg(generalHelper, session, request, response, LoginCostanti.INFO_JSP, LoginCostanti.LABEL_LOGIN_ERRORE);
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
	
							this.setErrorMsg(generalHelper, session, request, response, LoginCostanti.LOGIN_JSP, LoginCostanti.LABEL_LOGIN_SESSIONE_SCADUTA,MessageType.ERROR_SINTETICO);
							// return so that we do not chain to other filters
							return;
						}
					}
				} // end isRisorsaProtetta 
				//else {
				//	System.out.println("NON ENTRO ["+request.getRequestURI()+"]");
				//}
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
				this.setErrorMsg(generalHelper, session, request, response, LoginCostanti.INFO_JSP, LoginCostanti.LABEL_LOGIN_ERRORE);
				// return so that we do not chain to other filters
				return;
			}catch(Exception eClose){
				ControlStationCore.logError("Errore rilevato durante l'authorizationFilter (segnalazione errore)",e);
			}
		}

	}
	
	private boolean isSessionInvalid(HttpServletRequest httpServletRequest) {
		boolean sessionInValid = (httpServletRequest.getRequestedSessionId() != null)
				&& !httpServletRequest.isRequestedSessionIdValid();
		return sessionInValid;
	}
	
	private boolean isRisorsaProtetta(HttpServletRequest request){
		String urlRichiesta = request.getRequestURI();
		HttpSession session = request.getSession(true); 
		String changePwd = ServletUtils.getObjectFromSession(session, String.class, LoginCostanti.ATTRIBUTO_MODALITA_CAMBIA_PWD_SCADUTA);
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
				
				LoginSessionUtilities.setLoginParametersSession(session, loginCore, userLogin);
				lH.updateTipoInterfaccia();
				
				// Inizializzo parametri di ricerca
				Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);
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
		
		ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

		this.filterConfig.getServletContext().getRequestDispatcher(servletDispatcher).forward(request, response);
	}
	
	
	public void setErrorMsg(GeneralHelper gh, HttpSession session,
			HttpServletRequest request,HttpServletResponse response,String servletDispatcher,String msgErrore) throws IOException,ServletException {
		setErrorMsg(gh, session, request, response, servletDispatcher, msgErrore, null, MessageType.ERROR); 
	}
	
	public void setErrorMsg(GeneralHelper gh, HttpSession session,
			HttpServletRequest request,HttpServletResponse response,String servletDispatcher,String msgErrore, MessageType messageType) throws IOException,ServletException {
		setErrorMsg(gh, session, request, response, servletDispatcher, msgErrore, null, messageType); 
	}
	
	public void setErrorMsg(GeneralHelper gh,HttpSession session,
			HttpServletRequest request,HttpServletResponse response,String servletDispatcher,String msgErrore, String msgErroreTitle, MessageType messageType) throws IOException,ServletException {
		
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
		
		ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd, true);

		this.filterConfig.getServletContext().getRequestDispatcher(servletDispatcher).forward(request, response);
		
	}
	
	private String getRedirectToMessageServlet() {
		return new Parameter("", LoginCostanti.SERVLET_NAME_MESSAGE_PAGE,
				new Parameter(Costanti.PARAMETER_MESSAGE_TEXT,LoginCostanti.LABEL_CONSOLE_RIPRISTINATA),
				new Parameter(Costanti.PARAMETER_MESSAGE_TYPE,MessageType.INFO_SINTETICO.toString())
				).getValue();
	}
}
