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
package org.openspcoop2.generic_project.web.impl.jsf1.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
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
import org.openspcoop2.generic_project.web.impl.jsf1.mbean.LoginBean;
import org.openspcoop2.utils.Identity;

/****
* PrincipalFilter Filtro base per il controllo della login via Container basata sulla presenza del principal.
* 
* @author Pintori Giuliano (pintori@link.it)
* @author $Author$
* @version $Rev$, $Date$
 *
 */
public class PrincipalFilter implements Filter {

	private String loginPage = "public/login.jsf";
	private String timeoutPage = "public/timeoutPage.jsf";
	
	public static final String PRINCIPAL_ERROR_MSG = "principalErrorMsg";
	public static final String PRINCIPAL_SHOW_FORM = "principalShowForm";

	private List<String> excludedPages = null;

	public static final String USE_PRINCIPAL = "usePrincipal";

	private boolean usePrincipal =false;

	@Override
	public void destroy() {
	}

	@Override
	public void init(FilterConfig config) throws ServletException {
		this.excludedPages = new ArrayList<String>();
		this.excludedPages.add("public");

		String usePrincipalProp = config.getInitParameter(USE_PRINCIPAL);

		if(usePrincipalProp != null){
			try{
				this.usePrincipal = Boolean.parseBoolean(usePrincipalProp);
			}catch(Exception e){
				this.usePrincipal = false;
			}
		}

		// popolo la white list degli oggetti che possono essere visti anche se non authenticati, in particolare css, immagini, js, ecc...
		if(this.usePrincipal){
			this.excludedPages.add("a4j");
			this.excludedPages.add("images");
			this.excludedPages.add("css");
			this.excludedPages.add("scripts");
		}
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain filterChain) throws IOException, ServletException {


		if ((request instanceof HttpServletRequest) && (response instanceof HttpServletResponse)) {
			HttpServletRequest httpServletRequest = (HttpServletRequest) request;
			HttpServletResponse httpServletResponse = (HttpServletResponse) response;
			// Autenticazione gestita dall'applicazione 
			if(!this.usePrincipal){
				// is session expire control required for this request?
				if (isSessionControlRequiredForThisResource(httpServletRequest)) {

					// is session invalid?
					if (isSessionInvalid(httpServletRequest)) {					
						String redirPageUrl = httpServletRequest.getContextPath() + "/";
						//se la pagina richiesta e' quella di login allora redirigo direttamente a quella, altrimenti a quella di timeout
						//redirPageUrl += StringUtils.contains(httpServletRequest.getRequestURI(), getLoginPage()) ? getLoginPage() : getTimeoutPage();
						redirPageUrl += getRedirPage(httpServletRequest);
						//					log.info("session is invalid! redirecting to page : " + redirPageUrl);
						httpServletResponse.sendRedirect(redirPageUrl);
						return;
					}
				}
			}else {
				if (isSessionControlRequiredForThisResource(httpServletRequest)) {
					HttpSession sessione = httpServletRequest.getSession();

					// Ho richiesto una risorsa protetta cerco il login bean
					// Cerco il login bean nella sessione, se non c'e' provo a cercarlo nella sessione di JSF
					LoginBean lb = (LoginBean) sessione.getAttribute("loginBean");

					if(lb == null){
						try{
							FacesContext currentInstance = FacesContext.getCurrentInstance();
							if(currentInstance != null){
								ExternalContext ec = currentInstance.getExternalContext();
								if(ec != null){
									lb = (LoginBean) ec.getSessionMap().get("loginBean");
								}
							}
						}catch(Exception e){
							lb = null;
						}
					}

					// Se login bean == null lo creo
					if(lb == null){
						lb = new LoginBean(true); 
						lb.setIsLoggedIn(false);
						
						// prelevo la lingua da utilizzare per prima dalla richiesta http
						Locale localeRequest = request.getLocale();
						lb.impostaLocale(localeRequest);
					}

					// se non e' loggato lo loggo
					if(!lb.getIsLoggedIn()){
						// Controllo principal
						Identity identity = new Identity(httpServletRequest);
						String username = identity.getPrincipal();

						// Se l'username che mi arriva e' settato vuol dire che sono autorizzato dal Container
						if(username != null){
							// Creo il login bean ed effettuo il login
							lb.setNoPasswordLogin(true);
							lb.setUsername(username); 
							String loginResult = lb.login();
							if(loginResult.equals("login")){
								lb.logout();
								String redirPageUrl = httpServletRequest.getContextPath() + "/"+"index.jsp" ;
								httpServletResponse.sendRedirect(redirPageUrl);
								return;
							}else{
								sessione.setAttribute("loginBean", lb);
								String redirPageUrl = httpServletRequest.getContextPath() + "/"+"index.jsp" ;
								httpServletResponse.sendRedirect(redirPageUrl);
								return;
							}
						}else{
							// ERRORE
							sessione.setAttribute("loginBean", null);
							String redirPageUrl = httpServletRequest.getContextPath() + "/";
							//se la pagina richiesta e' quella di login allora redirigo direttamente a quella, altrimenti a quella di timeout
							//redirPageUrl += StringUtils.contains(httpServletRequest.getRequestURI(), getLoginPage()) ? getLoginPage() : getTimeoutPage();
							redirPageUrl += getRedirPage(httpServletRequest);
							//					log.info("session is invalid! redirecting to page : " + redirPageUrl);
							httpServletResponse.sendRedirect(redirPageUrl);
							return;
						}
					}	else {
						// controllo se la sessione e' valida
						boolean isSessionInvalid = isSessionInvalid(httpServletRequest);

						// Se non sono loggato mi autentico e poi faccio redirect verso la pagina di welcome
						if(isSessionInvalid){
							lb.logout();
							String redirPageUrl = httpServletRequest.getContextPath() + "/"+"index.jsp" ;
							httpServletResponse.sendRedirect(redirPageUrl);
							return;
						} 
					}
				}
			}
		}

		filterChain.doFilter(request, response);

	}

	/**
	 * 
	 * session shouldn't be checked for some pages. For example: for timeout page..
	 * Since we're redirecting to timeout page from this filter,
	 * if we don't disable session control for it, filter will again redirect to it
	 * and this will be result with an infinite loop... 
	 */
	private boolean isSessionControlRequiredForThisResource(HttpServletRequest httpServletRequest) {
		String requestPath = httpServletRequest.getRequestURI();

		boolean controlRequired = false;
		//		if(StringUtils.contains(requestPath, this.timeoutPage) || 
		//				StringUtils.contains(requestPath, this.loginPage)){
		//			controlRequired = false;
		//		}else{
		controlRequired = true;
		if(this.excludedPages.size() > 0)
			for (String page : this.excludedPages) {
				if(StringUtils.contains(requestPath, page)){
					controlRequired = false;
					break;
				}
			}
		else
			controlRequired = true;
		//		}

		return controlRequired;
	}

	private boolean isSessionInvalid(HttpServletRequest httpServletRequest) {
		boolean sessionInValid = (httpServletRequest.getRequestedSessionId() != null)
				&& !httpServletRequest.isRequestedSessionIdValid();
		return sessionInValid;
	}

	private String getRedirPage(HttpServletRequest req){
		String ctx = req.getContextPath();
		String reqUri = req.getRequestURI();

		String reqPage = StringUtils.remove(reqUri, ctx);

		String res = "";
		if("".equals(reqPage) || "/".equals(reqPage) || StringUtils.contains(reqPage, this.loginPage))
			res = this.loginPage;
		else
			res = this.timeoutPage;

		return res;
	}

}

/*
 * 
 	Identity identity = new Identity(httpServletRequest);
				String username = identity.getUsername();
				HttpSession sessione = httpServletRequest.getSession();

				// Se l'username che mi arriva e' settato vuol dire che sono autorizzato dal Container
				if(username != null){
					// Cerco il login bean nella sessione, se non c'e' provo a cercarlo nella sessione di JSF
					LoginBean lb = (LoginBean) sessione.getAttribute("loginBean");

					if(lb == null){
						try{
							FacesContext currentInstance = FacesContext.getCurrentInstance();
							if(currentInstance != null){
								ExternalContext ec = currentInstance.getExternalContext();
								if(ec != null){
									lb = (LoginBean) ec.getSessionMap().get("loginBean");
								}
							}
						}catch(Exception e){}
					} 

					if(lb != null){
						// Controllo se sono ancora loggato
						boolean isLogged = lb == null ? false : lb.getIsLoggedIn();

						// Se non sono loggato mi autentico e poi faccio redirect verso la pagina di welcome
						if(!isLogged){
							lb.setIsLoggedIn(true);
							String redirPageUrl = httpServletRequest.getContextPath() + "/"+"index.jsp" ;
							httpServletResponse.sendRedirect(redirPageUrl);
							return;
						} 
						// se sono loggato non faccio nulla...

						// Se il loginBean ancora non esiste (primo accesso) allora lo creo
					}else {
						lb = new LoginBean(true); 
						lb.setUsername(username); 
						lb.setIsLoggedIn(true);
						String redirPageUrl = httpServletRequest.getContextPath() + "/"+"index.jsp" ;
						httpServletResponse.sendRedirect(redirPageUrl);
						return;
					}

				}else {
					//Se non trovo l'username invalido tutto.
					if (isSessionControlRequiredForThisResource(httpServletRequest)) {

						// is session invalid?
						if (isSessionInvalid(httpServletRequest)) {		
							sessione.setAttribute("loginBean", null);
							String redirPageUrl = httpServletRequest.getContextPath() + "/";
							//se la pagina richiesta e' quella di login allora redirigo direttamente a quella, altrimenti a quella di timeout
							//redirPageUrl += StringUtils.contains(httpServletRequest.getRequestURI(), getLoginPage()) ? getLoginPage() : getTimeoutPage();
							redirPageUrl += getRedirPage(httpServletRequest);
							//					log.info("session is invalid! redirecting to page : " + redirPageUrl);
							httpServletResponse.sendRedirect(redirPageUrl);
							return;
						}
					}
				}
 */
