/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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


package org.openspcoop2.web.loader.servlet.login;

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

import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.MessageType;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.loader.core.Costanti;
import org.openspcoop2.web.loader.servlet.GeneralHelper;
import org.openspcoop2.web.loader.servlet.LoaderHelper;

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

	@Override
	public void init(FilterConfig filterConfig) {
		this.filterConfig = filterConfig;
		try {
		} catch (Exception e) {
		}
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		HttpSession session = request.getSession(true);

		GeneralHelper generalHelper = new GeneralHelper(session);

		try {
			String contextPath = request.getContextPath(); 
			
			// Non faccio il filtro sulla pagina di login e sulle immagini
			String urlRichiesta = request.getRequestURI();
			if (isRisorsaProtetta(request)) { 
			
				String userLogin = ServletUtils.getUserLoginFromSession(session);
				if (userLogin == null) {
					
					if((contextPath+"/").equals(urlRichiesta)){
						this.setErrorMsg(generalHelper, session, request, response, Costanti.LOGIN_JSP,null);
					}
					else{
						this.setErrorMsg(generalHelper, session, request, response, Costanti.LOGIN_JSP, Costanti.LABEL_LOGIN_SESSIONE_SCADUTA,MessageType.ERROR_SINTETICO);
					}
					
					// return so that we do not chain to other filters
					return;
				} else {
					if (urlRichiesta.indexOf(".js") == -1) {
						String servletRichiesta = urlRichiesta.substring((contextPath+"/").length());
						if("".equals(servletRichiesta) || "/".equals(servletRichiesta))
							response.sendRedirect(getRedirectToMessageServlet());
					}
				}
			}

			// allow others filter to be chained
			chain.doFilter(request, response);
		} 
		catch(Exception e){
//			generalHelper.log.error("Errore durante il dispatcher",e);
			generalHelper.log.error("Errore rilevato durante l'authorizationFilter",e);
			try{
				this.setErrorMsg(generalHelper, session, request, response, Costanti.INFO_JSP, Costanti.LABEL_LOGIN_ERRORE);
				// return so that we do not chain to other filters
				return;
			}catch(Exception eClose){
				generalHelper.log.error("Errore rilevato durante l'authorizationFilter (segnalazione errore)",e);
			}
		}
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
		GeneralData gd = gh.initGeneralData(request,Costanti.SERVLET_NAME_LOGIN);
		
		if(msgErrore!=null)
			pd.setMessage(msgErrore,msgErroreTitle,messageType);
		
		// se l'utente e' loggato faccio vedere il menu'
		String userLogin = ServletUtils.getUserLoginFromSession(session);
		if(userLogin != null){
			try{
			LoaderHelper loaderHelper = new LoaderHelper(request,pd,session);
			loaderHelper.makeMenu();
			}catch(Exception e){
				throw new ServletException(e);
			}
		}
		
		ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

		this.filterConfig.getServletContext().getRequestDispatcher(servletDispatcher).forward(request, response);
		
	}
	
	private boolean isRisorsaProtetta(HttpServletRequest request){
		String urlRichiesta = request.getRequestURI();
		if ((urlRichiesta.indexOf("/"+Costanti.SERVLET_NAME_LOGIN) == -1) 
				&& (urlRichiesta.indexOf("/"+Costanti.IMAGES_DIR) == -1) 
				&& (urlRichiesta.indexOf("/"+Costanti.CSS_DIR) == -1)
				&& (urlRichiesta.indexOf("/"+Costanti.FONTS_DIR) == -1)
				&& (urlRichiesta.indexOf("/"+Costanti.JS_DIR) == -1)) {
			return true;
		}
		
		return false;
	}
	
	private String getRedirectToMessageServlet() {
		return new Parameter("", Costanti.SERVLET_NAME_MESSAGE_PAGE,
				new Parameter(Costanti.PARAMETER_MESSAGE_TEXT,Costanti.LABEL_CONSOLE_RIPRISTINATA),
				new Parameter(Costanti.PARAMETER_MESSAGE_TYPE,MessageType.INFO_SINTETICO.toString())
				).getValue();
	}
}
