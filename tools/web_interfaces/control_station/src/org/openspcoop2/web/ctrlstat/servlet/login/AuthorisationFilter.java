/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it). 
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

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.ControlStationLogger;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.about.AboutCostanti;
import org.openspcoop2.web.ctrlstat.servlet.archivi.ArchiviCostanti;
import org.openspcoop2.web.ctrlstat.servlet.config.ConfigurazioneCostanti;
import org.openspcoop2.web.ctrlstat.servlet.utils.UtilsCostanti;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.MessageType;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.slf4j.Logger;

/**
 * AuthorisationFilter
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class AuthorisationFilter implements Filter {

	private FilterConfig filterConfig = null;
	private ControlStationCore core = null;
	private static Logger log = ControlStationLogger.getPddConsoleCoreLogger();
	
	
	@Override
	public void init(FilterConfig filterConfig) {
		
		this.filterConfig = filterConfig;
		try {
			this.core = new ControlStationCore();
		} catch (Exception e) {
			System.err.println("Errore durante il caricamento iniziale: " + e.toString());
			e.printStackTrace(System.err);
		}

	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		HttpSession session = request.getSession(true);

		session.setAttribute(CostantiControlStation.SESSION_PARAMETRO_SINGLE_PDD, this.core.isSinglePdD());
		GeneralHelper generalHelper = new GeneralHelper(session);

		LoginHelper loginHelper = null;
		try {
			loginHelper = new LoginHelper(request, new PageData(), session);
		} catch (Exception e) {
			ControlStationCore.logError("Errore rilevato durante l'authorizationFilter",e);
			throw new RuntimeException(e.getMessage(),e);
		}
		
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
			if (isRisorsaProtetta(request)) { 
				
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

			// allow others filter to be chained
			chain.doFilter(request, response);
		} catch (Exception e) {
			ControlStationCore.logError("Errore rilevato durante l'authorizationFilter",e);
			try{
				this.setErrorMsg(generalHelper, session, request, response, LoginCostanti.INFO_JSP, LoginCostanti.LABEL_LOGIN_ERRORE);
				// return so that we do not chain to other filters
				return;
			}catch(Exception eClose){
				ControlStationCore.logError("Errore rilevato durante l'authorizationFilter (segnalazione errore)",e);
			}
		}

	}
	
	private boolean isRisorsaProtetta(HttpServletRequest request){
		String urlRichiesta = request.getRequestURI();
		if ((urlRichiesta.indexOf("/"+LoginCostanti.SERVLET_NAME_LOGIN) == -1) 
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
	
	public void setErrorMsg(GeneralHelper gh,HttpSession session,
			HttpServletRequest request,HttpServletResponse response,String servletDispatcher,String msgErrore) throws IOException,ServletException {
		setErrorMsg(gh, session, request, response, servletDispatcher, msgErrore, null, MessageType.ERROR); 
	}
	
	public void setErrorMsg(GeneralHelper gh,HttpSession session,
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
			LoginHelper lH = new LoginHelper(request, pd, session);
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
