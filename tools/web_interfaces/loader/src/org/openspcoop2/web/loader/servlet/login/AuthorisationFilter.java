/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
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
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.loader.core.Costanti;
import org.openspcoop2.web.loader.servlet.GeneralHelper;

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
			if ((urlRichiesta.indexOf("/"+Costanti.SERVLET_NAME_LOGIN) == -1) && (urlRichiesta.indexOf("/"+Costanti.IMAGES_DIR) == -1)) {
			
				String userLogin = ServletUtils.getUserLoginFromSession(session);
				if (userLogin == null) {
					
					if((contextPath+"/").equals(urlRichiesta)){
						this.setErrorMsg(generalHelper, session, request, response, Costanti.LOGIN_JSP,null);
					}
					else{
						this.setErrorMsg(generalHelper, session, request, response, Costanti.LOGIN_JSP, 
								Costanti.LABEL_LOGIN_SESSIONE_SCADUTA);
					}
					
					// return so that we do not chain to other filters
					return;
				} 
			}

			// allow others filter to be chained
			chain.doFilter(request, response);
		} 
		catch(Exception e){
			generalHelper.log.error("Errore durante il dispatcher",e);
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
		GeneralData gd = gh.initGeneralData(request,Costanti.SERVLET_NAME_LOGIN);
		
		if(msgErrore!=null)
			pd.setMessage(msgErrore);
		
		ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

		this.filterConfig.getServletContext().getRequestDispatcher(servletDispatcher).forward(request, response);
		
	}
}
