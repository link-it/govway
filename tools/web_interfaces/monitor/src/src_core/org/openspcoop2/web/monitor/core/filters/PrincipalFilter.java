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
package org.openspcoop2.web.monitor.core.filters;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import javax.faces.application.ViewExpiredException;
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
import org.openspcoop2.utils.transport.http.credential.IPrincipalReader;
import org.openspcoop2.utils.transport.http.credential.PrincipalReaderException;
import org.openspcoop2.utils.transport.http.credential.PrincipalReaderFactory;
import org.openspcoop2.utils.transport.http.credential.PrincipalReaderType;
import org.slf4j.Logger;

import org.openspcoop2.web.monitor.core.listener.LoginPhaseListener;
import org.openspcoop2.web.monitor.core.bean.LoginBean;
import org.openspcoop2.web.monitor.core.core.PddMonitorProperties;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;

/****
 * PrincipalFilter Filtro base per il controllo della login via Container basata sulla presenza del principal.
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class PrincipalFilter implements Filter {

	public static final String PRINCIPAL_ERROR_MSG = LoginPhaseListener.PRINCIPAL_ERROR_MSG; 

	private Logger log = LoggerManager.getPddMonitorCoreLogger();

	private String loginPage = "public/login.jsf";
	private String timeoutPage = "public/timeoutPage.jsf";

	private List<String> excludedPages = null;

	public static final String LOGIN_APPLICATION = "login.application";
	public static final String LOGIN_TIPO = "login.tipo";
	public static final String LOGIN_PROPR_PREFIX = "login.props.";

	private boolean loginApplication = true;
	private String loginTipo = null;
	private IPrincipalReader principalReader = null;
	private String loginUtenteNonAutorizzatoRedirectUrl = null;
	private String loginUtenteNonValidoRedirectUrl = null;
	private String loginErroreInternoRedirectUrl = null;
	private String loginSessioneScadutaRedirectUrl = null;

	@Override
	public void destroy() {
	}

	@Override
	public void init(FilterConfig config) throws ServletException {
		this.excludedPages = new ArrayList<String>();
		this.excludedPages.add("public");

		// configurazione del filtro dalle properties
		
			try{
				this.loginApplication = PddMonitorProperties.getInstance(this.log).isLoginApplication();
			}catch(Exception e){
				this.loginApplication = true;
			}
		
		try{
			if(!this.loginApplication){
				this.loginTipo = PddMonitorProperties.getInstance(this.log).getLoginTipo();

				if(StringUtils.isEmpty(this.loginTipo))
					this.loginTipo = PrincipalReaderType.PRINCIPAL.getValue();
				
				this.principalReader = PrincipalReaderFactory.getReader(this.log, this.loginTipo);
				Properties prop = PddMonitorProperties.getInstance(this.log).getLoginProperties();
				this.principalReader.init(prop); 
				
				this.loginUtenteNonAutorizzatoRedirectUrl = PddMonitorProperties.getInstance(this.log).getLoginUtenteNonAutorizzatoRedirectUrl();
				this.loginUtenteNonValidoRedirectUrl = PddMonitorProperties.getInstance(this.log).getLoginUtenteNonValidoRedirectUrl();
				this.loginErroreInternoRedirectUrl = PddMonitorProperties.getInstance(this.log).getLoginErroreInternoRedirectUrl();
				this.loginSessioneScadutaRedirectUrl = PddMonitorProperties.getInstance(this.log).getLoginSessioneScadutaRedirectUrl();
			}
		}catch(PrincipalReaderException e){
			this.log.error("Impossibile caricare il principal reader: "+e.getMessage());
			throw new ServletException(e);
		} catch (Exception e) {
			this.log.error("Impossibile leggere la configurazione della console: "+e.getMessage());
			throw new ServletException(e);
		}

		this.log.debug("Usa il principal per il controllo autorizzazione utente ["+!this.loginApplication+"]"); 

		// popolo la white list degli oggetti che possono essere visti anche se non authenticati, in particolare css, immagini, js, ecc...
//		if(this.loginApplication){
			this.excludedPages.add("a4j");
			this.excludedPages.add("images");
			this.excludedPages.add("css");
			this.excludedPages.add("scripts");
			this.excludedPages.add("fonts");
			this.excludedPages.add("/report/statistica");
			this.excludedPages.add("/report/configurazione");
//		}

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain filterChain) throws IOException, ServletException {

		if ((request instanceof HttpServletRequest) && (response instanceof HttpServletResponse)) {
			HttpServletRequest httpServletRequest = (HttpServletRequest) request;
			HttpServletResponse httpServletResponse = (HttpServletResponse) response;
			// Autenticazione gestita dall'applicazione 
			if(this.loginApplication){
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

					//					this.log.debug("Richiesta risorsa privata ["+httpServletRequest.getRequestURI()+"]"); 
					// Ho richiesto una risorsa protetta cerco il login bean
					// Cerco il login bean nella sessione, se non c'e' provo a cercarlo nella sessione di JSF
					LoginBean lb = (LoginBean) sessione.getAttribute(org.openspcoop2.web.monitor.core.bean.AbstractLoginBean.LOGIN_BEAN_SESSION_ATTRIBUTE_NAME);

					this.log.debug("LoginBean trovato in sessione ["+(lb!= null)+"]"); 

					if(lb == null){
						try{
							FacesContext currentInstance = FacesContext.getCurrentInstance();
							this.log.debug("FacesContext not null ["+(currentInstance!= null)+"]"); 
							if(currentInstance != null){
								ExternalContext ec = currentInstance.getExternalContext();
								this.log.debug("ExternalContext not null ["+(ec!= null)+"]");
								if(ec != null){
									lb = (LoginBean) ec.getSessionMap().get(org.openspcoop2.web.monitor.core.bean.AbstractLoginBean.LOGIN_BEAN_SESSION_ATTRIBUTE_NAME);
									this.log.debug("LoginBean trovato in nella SessionMap JSF ["+(lb!= null)+"]"); 
								}
							}
						}catch(Exception e){
							lb = null;
						}
					}

					// check lingua

					this.log.debug("Controllo Locale in corso ...");
					Locale loc = null;
					try{
						loc = FacesContext.getCurrentInstance().getViewRoot().getLocale();
					}catch(Exception e){
						this.log.debug("Errore durante controllo Locale: "+ e.getMessage());
						loc = Locale.getDefault();
					}

					this.log.debug("Locale trovato Valore["+loc.toString()+"]");


					//this.log.debug("Login Bean trovato in sessione ["+(lb != null)+"]");

					// Se login bean == null lo creo
					if(lb == null){
						// prelevo la lingua della richiesta http
						Locale localeRequest = request.getLocale();
						this.log.debug("Locale trovato nella Request["+localeRequest.toString()+"]");

						lb = new LoginBean(true); 
						lb.setLoggedIn(false);
						// supporto alla localizzazione
						//lb.impostaLocale(localeRequest);
					}

				//	this.log.debug("Login Bean Lingua: ["+lb.getCurrentLang()+"]");

					//					this.log.debug("Login Bean Tipo: ["+lb.getClass().getName()+"]");
					this.log.debug("Login Bean Utente Loggato: ["+lb.isLoggedIn()+"]"); 
					// se non e' loggato lo loggo
					if(!lb.isLoggedIn()){
						// Controllo principal
//						Identity identity = new Identity(httpServletRequest);
//						String username = identity.getPrincipal();
						
						String username = null;
						try {
							username = this.principalReader.getPrincipal(httpServletRequest);
						} catch (PrincipalReaderException e) {
							this.log.error("Errore durante la lettura del principal: " + e.getMessage(),e);
						}
						
						this.log.debug("Username trovato: ["+username+"]");

						// Se l'username che mi arriva e' settato vuol dire che sono autorizzato dal Container
						if(username != null){
							// Creo il login bean ed effettuo il login
							lb.setApplicationLogin(false);
							lb.setUsername(username); 
							String loginResult = lb.login();
							if(loginResult.equals("login")){
								this.log.debug("Utente non autorizzato: " + lb.getLoginErrorMessage());
								lb.logout();
								String redirPageUrl =
										StringUtils.isNotEmpty(this.loginUtenteNonAutorizzatoRedirectUrl) ? 
												this.loginUtenteNonAutorizzatoRedirectUrl : httpServletRequest.getContextPath() + "/public/error.jsf?principalShowForm=true";//+"index.jsp" ;

								// Messaggio di errore
								sessione.setAttribute(PrincipalFilter.PRINCIPAL_ERROR_MSG, lb.getLoginErrorMessage()); 

								httpServletResponse.sendRedirect(redirPageUrl);
								return;
							}else if(loginResult.equals("loginError")){
								this.log.debug("Errore durante il login: " + lb.getLoginErrorMessage());
								lb.logout();
								String redirPageUrl = StringUtils.isNotEmpty(this.loginErroreInternoRedirectUrl) ? this.loginErroreInternoRedirectUrl : httpServletRequest.getContextPath() + "/public/error.jsf?principalShowForm=true" ;

								// Messaggio di errore
								sessione.setAttribute(PrincipalFilter.PRINCIPAL_ERROR_MSG, lb.getLoginErrorMessage()); 

								httpServletResponse.sendRedirect(redirPageUrl);
								return;
							}else if(loginResult.equals("loginUserInvalid")){
								this.log.debug("Errore durante il caricamento informazioni utente: " + lb.getLoginErrorMessage());
								lb.logout();
								String redirPageUrl = StringUtils.isNotEmpty(this.loginUtenteNonValidoRedirectUrl) ? 
										this.loginUtenteNonValidoRedirectUrl : httpServletRequest.getContextPath() + "/public/error.jsf?principalShowForm=true" ;

								// Messaggio di errore
								sessione.setAttribute(PrincipalFilter.PRINCIPAL_ERROR_MSG, lb.getLoginErrorMessage()); 

								httpServletResponse.sendRedirect(redirPageUrl);
								return;
							}else{
								this.log.debug("Utente autorizzato");
								sessione.setAttribute(org.openspcoop2.web.monitor.core.bean.AbstractLoginBean.LOGIN_BEAN_SESSION_ATTRIBUTE_NAME, lb);
								String redirPageUrl = httpServletRequest.getContextPath() + "/"+"index.jsp" ;
								httpServletResponse.sendRedirect(redirPageUrl);
								return;
							}
						}else{

							// ERRORE
							sessione.setAttribute(org.openspcoop2.web.monitor.core.bean.AbstractLoginBean.LOGIN_BEAN_SESSION_ATTRIBUTE_NAME, null);
							String redirPageUrl =
									StringUtils.isNotEmpty(this.loginUtenteNonAutorizzatoRedirectUrl) ? 
									this.loginUtenteNonAutorizzatoRedirectUrl : httpServletRequest.getContextPath() + "/" + "pages/welcome.jsf";
							//se la pagina richiesta e' quella di login allora redirigo direttamente a quella, altrimenti a quella di timeout
							//redirPageUrl += StringUtils.contains(httpServletRequest.getRequestURI(), getLoginPage()) ? getLoginPage() : getTimeoutPage();
							//							redirPageUrl += getRedirPage(httpServletRequest);
							this.log.debug("Username NULL reidrect ["+redirPageUrl+"]");
							//					log.info("session is invalid! redirecting to page : " + redirPageUrl);
							httpServletResponse.sendRedirect(redirPageUrl);
							return;
						}
					}	else {
						this.log.debug("Login Bean Utente Loggato controllo validita' sessione..."); 
						// controllo se la sessione e' valida
						boolean isSessionInvalid = isSessionInvalid(httpServletRequest);

						// Se non sono loggato mi autentico e poi faccio redirect verso la pagina di welcome
						if(isSessionInvalid){
							this.log.debug("Login Bean Utente Loggato controllo validita' sessione [invalida]");
							lb.logout();
							String redirPageUrl =  StringUtils.isNotEmpty(this.loginSessioneScadutaRedirectUrl) ? this.loginSessioneScadutaRedirectUrl : httpServletRequest.getContextPath() + "/"+"index.jsp" ;
							httpServletResponse.sendRedirect(redirPageUrl);
							return;
						} 
					}
				}
			}
		}  

		//		filterChain.doFilter(request, response);

		try {
			//	this.log.debug("filterChain.dofilter..."); 
			filterChain.doFilter(request, response);
		} catch (ServletException e) {
			//			this.log.debug(" ServletException ["+e.getRootCause()+"]"); 
			Throwable rootCause = e.getRootCause();
			if(rootCause != null){
				if (rootCause instanceof ViewExpiredException) { // This is true for any FacesException.

					this.log.debug("Rilevata ViewExpiredException: ["+rootCause.getMessage()+"]"); 
					if ((request instanceof HttpServletRequest) && (response instanceof HttpServletResponse)) {
						HttpServletRequest httpServletRequest = (HttpServletRequest) request;
						HttpServletResponse httpServletResponse = (HttpServletResponse) response;

						String redirPageUrl = httpServletRequest.getContextPath() + "/"+ "public/timeoutPage.jsf";
						httpServletResponse.sendRedirect(redirPageUrl);
						return;
					}

					throw (ViewExpiredException) rootCause; // Throw wrapped ViewExpiredException instead of ServletException.
				} else if (rootCause instanceof RuntimeException) { // This is true for any FacesException.
					throw (RuntimeException) rootCause; // Throw wrapped RuntimeException instead of ServletException.
				} else {
					throw e;
				}
			} else {
				throw e;
			}
		} 
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


	//	private boolean isSessionInvalid(HttpServletRequest httpServletRequest) {
	//		boolean sessionInValid = (httpServletRequest.getRequestedSessionId() == null) || !httpServletRequest.isRequestedSessionIdValid();
	//		return sessionInValid;
	//	}

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
