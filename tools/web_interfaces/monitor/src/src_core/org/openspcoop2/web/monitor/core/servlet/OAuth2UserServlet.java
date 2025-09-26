package org.openspcoop2.web.monitor.core.servlet;

import java.io.IOException;
import java.util.Locale;
import java.util.Properties;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.transport.http.credential.IPrincipalReader;
import org.openspcoop2.utils.transport.http.credential.PrincipalReaderException;
import org.openspcoop2.utils.transport.http.credential.PrincipalReaderFactory;
import org.openspcoop2.utils.transport.http.credential.PrincipalReaderType;
import org.openspcoop2.web.monitor.core.bean.LoginBean;
import org.openspcoop2.web.monitor.core.core.PddMonitorProperties;
import org.openspcoop2.web.monitor.core.filters.PrincipalFilter;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;
import org.openspcoop2.web.monitor.core.utils.SessionUtils;
import org.slf4j.Logger;

public class OAuth2UserServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	
	private static Logger log = LoggerManager.getPddMonitorCoreLogger();
	
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
		
		try {
			String loginTipo = PddMonitorProperties.getInstance(OAuth2UserServlet.log).getLoginTipo();

			if(StringUtils.isEmpty(loginTipo))
				loginTipo = PrincipalReaderType.PRINCIPAL.getValue();
			
			principalReader = PrincipalReaderFactory.getReader(OAuth2UserServlet.log, loginTipo);
			Properties prop = PddMonitorProperties.getInstance(OAuth2UserServlet.log).getLoginProperties();
			principalReader.init(prop); 
			
			loginUtenteNonAutorizzatoRedirectUrl = PddMonitorProperties.getInstance(OAuth2UserServlet.log).getLoginUtenteNonAutorizzatoRedirectUrl();
			loginUtenteNonValidoRedirectUrl = PddMonitorProperties.getInstance(OAuth2UserServlet.log).getLoginUtenteNonValidoRedirectUrl();
			loginErroreInternoRedirectUrl = PddMonitorProperties.getInstance(OAuth2UserServlet.log).getLoginErroreInternoRedirectUrl();
			loginSessioneScadutaRedirectUrl = PddMonitorProperties.getInstance(OAuth2UserServlet.log).getLoginSessioneScadutaRedirectUrl();
		} catch (UtilsException e) {
			log.error("Errore durante la lettura delle properties: " + e.getMessage(),e);
			throw new ServletException(e);
		} catch (PrincipalReaderException e) {
			log.error("Impossibile caricare il principal reader: "+e.getMessage(), e);
			throw new ServletException(e);
		} 

		HttpSession sessione = httpServletRequest.getSession();

		//					OAuth2UserInfoServlet.log.debug("Richiesta risorsa privata ["+httpServletRequest.getRequestURI()+"]")
		// Ho richiesto una risorsa protetta cerco il login bean
		// Cerco il login bean nella sessione, se non c'e' provo a cercarlo nella sessione di JSF
		LoginBean lb = (LoginBean) sessione.getAttribute(org.openspcoop2.web.monitor.core.bean.AbstractLoginBean.LOGIN_BEAN_SESSION_ATTRIBUTE_NAME);

		OAuth2UserServlet.log.debug("LoginBean trovato in sessione [{}]", (lb!= null)); 

		if(lb == null){
			try{
				FacesContext currentInstance = FacesContext.getCurrentInstance();
				OAuth2UserServlet.log.debug("FacesContext not null [{}]", (currentInstance!= null)); 
				if(currentInstance != null){
					ExternalContext ec = currentInstance.getExternalContext();
					OAuth2UserServlet.log.debug("ExternalContext not null [{}]", (ec!= null));
					if(ec != null){
						lb = (LoginBean) ec.getSessionMap().get(org.openspcoop2.web.monitor.core.bean.AbstractLoginBean.LOGIN_BEAN_SESSION_ATTRIBUTE_NAME);
						OAuth2UserServlet.log.debug("LoginBean trovato in nella SessionMap JSF [{}]", (lb!= null)); 
					}
				}
			}catch(Exception e){
				lb = null;
			}
		}

		// check lingua

		OAuth2UserServlet.log.debug("Controllo Locale in corso ...");
		Locale loc = null;
		try{
			loc = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		}catch(Exception e){
			OAuth2UserServlet.log.debug("Errore durante controllo Locale: "+ e.getMessage(), e);
			loc = Locale.getDefault();
		}

		OAuth2UserServlet.log.debug("Locale trovato Valore[{}]", loc);

		// Se login bean == null lo creo
		if(lb == null){
			// prelevo la lingua della richiesta http
			Locale localeRequest = httpServletRequest.getLocale();
			OAuth2UserServlet.log.debug("Locale trovato nella Request[{}]", localeRequest);

			lb = new LoginBean(true); 
			lb.setLoggedIn(false);
			// supporto alla localizzazione
			//lb.impostaLocale(localeRequest)
		}

		OAuth2UserServlet.log.debug("Login Bean Utente Loggato: [{}]", lb.isLoggedIn()); 
		// se non e' loggato lo loggo
		if(!lb.isLoggedIn()){
			// Controllo principal
			
			String username = null;
			try {
				username = principalReader.getPrincipal(httpServletRequest);
			} catch (PrincipalReaderException e) {
				OAuth2UserServlet.log.error("Errore durante la lettura del principal: " + e.getMessage(),e);
			}
			
			OAuth2UserServlet.log.debug("Username trovato: [{}]", username);

			// Se l'username che mi arriva e' settato vuol dire che sono autorizzato dal Container
			if(username != null){
				// Creo il login bean ed effettuo il login
				lb.setApplicationLogin(false);
				lb.setUsername(username); 
				String loginResult = lb.login();
				if(loginResult.equals("login")){
					OAuth2UserServlet.log.debug("Utente non autorizzato: {}", lb.getLoginErrorMessage());
					lb.logout();
					String redirPageUrl =
							StringUtils.isNotEmpty(loginUtenteNonAutorizzatoRedirectUrl) ? 
									loginUtenteNonAutorizzatoRedirectUrl : httpServletRequest.getContextPath() + PrincipalFilter.REDIRECT_ERRORE_DEFAULT;

					// Messaggio di errore
					sessione.setAttribute(PrincipalFilter.PRINCIPAL_ERROR_MSG, lb.getLoginErrorMessage()); 

					httpServletResponse.sendRedirect(redirPageUrl);
				}else if(loginResult.equals("loginError")){
					OAuth2UserServlet.log.debug("Errore durante il login: {}", lb.getLoginErrorMessage());
					lb.logout();
					String redirPageUrl = StringUtils.isNotEmpty(loginErroreInternoRedirectUrl) ? loginErroreInternoRedirectUrl : httpServletRequest.getContextPath() + PrincipalFilter.REDIRECT_ERRORE_DEFAULT ;

					// Messaggio di errore
					sessione.setAttribute(PrincipalFilter.PRINCIPAL_ERROR_MSG, lb.getLoginErrorMessage()); 

					httpServletResponse.sendRedirect(redirPageUrl);
				}else if(loginResult.equals("loginUserInvalid")){
					OAuth2UserServlet.log.debug("Errore durante il caricamento informazioni utente: {}", lb.getLoginErrorMessage());
					lb.logout();
					String redirPageUrl = StringUtils.isNotEmpty(loginUtenteNonValidoRedirectUrl) ? 
							loginUtenteNonValidoRedirectUrl : httpServletRequest.getContextPath() + PrincipalFilter.REDIRECT_ERRORE_DEFAULT ;

					// Messaggio di errore
					sessione.setAttribute(PrincipalFilter.PRINCIPAL_ERROR_MSG, lb.getLoginErrorMessage()); 

					httpServletResponse.sendRedirect(redirPageUrl);
				}else{
					OAuth2UserServlet.log.debug("Utente autorizzato");
					sessione.setAttribute(org.openspcoop2.web.monitor.core.bean.AbstractLoginBean.LOGIN_BEAN_SESSION_ATTRIBUTE_NAME, lb);
					String redirPageUrl = httpServletRequest.getContextPath() + "/"+"index.jsp" ;
					httpServletResponse.sendRedirect(redirPageUrl);
				}
			}else{

				// ERRORE
				sessione.setAttribute(org.openspcoop2.web.monitor.core.bean.AbstractLoginBean.LOGIN_BEAN_SESSION_ATTRIBUTE_NAME, null);
				String redirPageUrl =
						StringUtils.isNotEmpty(loginUtenteNonAutorizzatoRedirectUrl) ? 
						loginUtenteNonAutorizzatoRedirectUrl : httpServletRequest.getContextPath() + "/" + "pages/welcome.jsf";
				//se la pagina richiesta e' quella di login allora redirigo direttamente a quella, altrimenti a quella di timeout
				//redirPageUrl += StringUtils.contains(httpServletRequest.getRequestURI(), getLoginPage()) ? getLoginPage() : getTimeoutPage()
				//							redirPageUrl += getRedirPage(httpServletRequest)
				OAuth2UserServlet.log.debug("Username NULL redirect [{}]", redirPageUrl);
				//					log.info("session is invalid! redirecting to page : " + redirPageUrl)
				
				// Messaggio di errore
				sessione.setAttribute(PrincipalFilter.PRINCIPAL_ERROR_MSG, "Impossibile autenticare l'utente"); 
				
				httpServletResponse.sendRedirect(redirPageUrl);
				return;
			}
		}	else {
			OAuth2UserServlet.log.debug("Login Bean Utente Loggato controllo validita' sessione..."); 
			// controllo se la sessione e' valida
			boolean isSessionInvalid = SessionUtils.isSessionInvalid(httpServletRequest);

			// Se non sono loggato mi autentico e poi faccio redirect verso la pagina di welcome
			if(isSessionInvalid){
				OAuth2UserServlet.log.debug("Login Bean Utente Loggato controllo validita' sessione [invalida]");
				lb.logout();
				String redirPageUrl =  StringUtils.isNotEmpty(loginSessioneScadutaRedirectUrl) ? loginSessioneScadutaRedirectUrl : httpServletRequest.getContextPath() + "/"+"index.jsp" ;
				httpServletResponse.sendRedirect(redirPageUrl);
			} 
		}
	}
}
