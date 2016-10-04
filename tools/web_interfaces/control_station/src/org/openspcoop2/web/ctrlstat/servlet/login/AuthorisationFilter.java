/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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

import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.archivi.ArchiviCostanti;
import org.openspcoop2.web.ctrlstat.servlet.config.ConfigurazioneCostanti;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.ServletUtils;

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

		try {
//			System.out.println("SERVLET PATH ["+request.getServletPath()+"]");
//			System.out.println("SERVLET URI ["+request.getRequestURI()+"]");
//			System.out.println("SERVLET CONTEXT PATH ["+request.getContextPath()+"]");
//			System.out.println("SERVLET PATH INFO ["+request.getPathInfo()+"]");
//			System.out.println("SERVLET QUERY STRING ["+request.getQueryString()+"]");
//			System.out.println("SERVLET URL ["+request.getRequestURL()+"]");
			String contextPath = request.getContextPath(); // '/pddConsole'
			
			// Non faccio il filtro sulla pagina di login e sulle immagini
			String urlRichiesta = request.getRequestURI();
			if ((urlRichiesta.indexOf("/"+LoginCostanti.SERVLET_NAME_LOGIN) == -1) && (urlRichiesta.indexOf("/"+CostantiControlStation.IMAGES_DIR) == -1)) {
				
				String userLogin = ServletUtils.getUserLoginFromSession(session);
				if (userLogin == null) {
					
					if((contextPath+"/").equals(urlRichiesta)){
						this.setErrorMsg(generalHelper, session, request, response, LoginCostanti.LOGIN_JSP,null);
					}
					else{
						this.setErrorMsg(generalHelper, session, request, response, LoginCostanti.LOGIN_JSP, 
						LoginCostanti.LABEL_LOGIN_SESSIONE_SCADUTA);
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
							
							// Non faccio verificare login/logout
							if (!LoginCostanti.SERVLET_NAME_LOGIN.equals(servletRichiesta) && !LoginCostanti.SERVLET_NAME_LOGOUT.equals(servletRichiesta)) {
								if(GestoreAutorizzazioni.autorizzazioneUtente(singlePdDBooleanValue,ControlStationCore.getLog(), servletRichiesta,request, session)==false){
									ControlStationCore.logError("Autorizzazione negata all'utente "+userLogin+" per la servlet ["+servletRichiesta+"]");
									setErrorMsg(generalHelper, session, request, response, LoginCostanti.INFO_JSP,
											LoginCostanti.LABEL_LOGIN_AUTORIZZAZIONE_NEGATA);
									// return so that we do not chain to other filters
									return;
								}
							}
							
							ControlStationCore.logDebug("Autorizzazione permessa all'utente "+userLogin+" per la servlet ["+servletRichiesta+"]");
							
						} catch (Exception e) {
							ControlStationCore.logError("Errore durante il processo di autorizzazione della servlet ["+urlRichiesta
									+"] per l'utente ["+userLogin+"] : " + e.getMessage(),e);
							setErrorMsg(generalHelper, session, request, response, LoginCostanti.INFO_JSP,
								LoginCostanti.LABEL_LOGIN_ERRORE);
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
							&& urlRichiesta.indexOf("/"+ArchiviCostanti.SERVLET_NAME_RESOCONTO_EXPORT) == -1) {

						this.setErrorMsg(generalHelper, session, request, response, "/jsplib/login.jsp", 
							LoginCostanti.LABEL_LOGIN_SESSIONE_SCADUTA);
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
				this.setErrorMsg(generalHelper, session, request, response, LoginCostanti.INFO_JSP, 
					LoginCostanti.LABEL_LOGIN_ERRORE);
				// return so that we do not chain to other filters
				return;
			}catch(Exception eClose){
				ControlStationCore.logError("Errore rilevato durante l'authorizationFilter (segnalazione errore)",e);
			}
		}

	}

	@Override
	public void destroy() {
		this.filterConfig = null;
	}
	
	public void setErrorMsg(GeneralHelper gh,HttpSession session,
			HttpServletRequest request,HttpServletResponse response,String servletDispatcher,String msgErrore) throws IOException,ServletException {
		
		// Inizializzo PageData
		PageData pd = gh.initPageData();

		// Inizializzo GeneralData
		GeneralData gd = gh.initGeneralData(request,LoginCostanti.SERVLET_NAME_LOGIN);
		
		if(msgErrore!=null)
			pd.setMessage(msgErrore);
		
		ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd, true);

		this.filterConfig.getServletContext().getRequestDispatcher(servletDispatcher).forward(request, response);
		
	}
}
