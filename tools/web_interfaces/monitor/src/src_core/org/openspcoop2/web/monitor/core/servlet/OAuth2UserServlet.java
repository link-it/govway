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
import org.openspcoop2.utils.oauth2.OAuth2Costanti;
import org.openspcoop2.utils.oauth2.OAuth2Utilities;
import org.openspcoop2.utils.transport.http.credential.IPrincipalReader;
import org.openspcoop2.utils.transport.http.credential.PrincipalReaderException;
import org.openspcoop2.utils.transport.http.credential.PrincipalReaderFactory;
import org.openspcoop2.utils.transport.http.credential.PrincipalReaderType;
import org.openspcoop2.web.monitor.core.bean.LoginBean;
import org.openspcoop2.web.monitor.core.core.PddMonitorProperties;
import org.openspcoop2.web.monitor.core.core.Utility;
import org.openspcoop2.web.monitor.core.filters.PrincipalFilter;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;
import org.openspcoop2.web.monitor.core.utils.SessionUtils;
import org.slf4j.Logger;

/**
 * OAuth2UserServlet
 *
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class OAuth2UserServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static Logger log = LoggerManager.getPddMonitorCoreLogger();

	public OAuth2UserServlet() {
		super();
	}

	@Override
	protected void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
		engineDoGet(httpServletRequest, httpServletResponse); 
	}

	private void engineDoGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
		// login utenza  
		IPrincipalReader principalReader = null;
		String loginUtenteNonAutorizzatoRedirectUrl = null;
		String loginSessioneScadutaRedirectUrl = null;
		String oauth2LogoutUrl = null;
		try {
			String loginTipo = PddMonitorProperties.getInstance(OAuth2UserServlet.log).getLoginTipo();

			if(StringUtils.isEmpty(loginTipo))
				loginTipo = PrincipalReaderType.PRINCIPAL.getValue();

			Properties prop = PddMonitorProperties.getInstance(OAuth2UserServlet.log).getLoginProperties();

			principalReader = caricaPrincipalReader(loginTipo, prop); 

			loginUtenteNonAutorizzatoRedirectUrl = PddMonitorProperties.getInstance(OAuth2UserServlet.log).getLoginUtenteNonAutorizzatoRedirectUrl();
			loginSessioneScadutaRedirectUrl = PddMonitorProperties.getInstance(OAuth2UserServlet.log).getLoginSessioneScadutaRedirectUrl();

			oauth2LogoutUrl = prop.getProperty(OAuth2Costanti.PROP_OAUTH2_LOGOUT_ENDPOINT);

			HttpSession sessione = httpServletRequest.getSession();

			// Cerco il login bean nella sessione, se non c'e' provo a cercarlo nella sessione di JSF
			LoginBean lb = (LoginBean) sessione.getAttribute(org.openspcoop2.web.monitor.core.bean.AbstractLoginBean.LOGIN_BEAN_SESSION_ATTRIBUTE_NAME);

			OAuth2UserServlet.log.debug("LoginBean trovato in sessione [{}]", (lb!= null)); 

			lb = creaLoginBeanSeNonPresente(httpServletRequest, lb);

			OAuth2UserServlet.log.debug("Login Bean Utente Loggato: [{}]", lb.isLoggedIn()); 

			// se non e' loggato lo loggo
			if(!lb.isLoggedIn()){
				// Controllo principal
				String username = getPrincipal(httpServletRequest, principalReader);

				OAuth2UserServlet.log.debug("Username trovato: [{}]", username);

				// Se l'username che mi arriva e' settato vuol dire che sono autorizzato dal Container
				if(username != null){
					controllaEsistenzaUtente(httpServletRequest, httpServletResponse, loginUtenteNonAutorizzatoRedirectUrl,
							oauth2LogoutUrl, sessione, lb,
							username);
				}else{
					utenteNonTrovatoInSessione(httpServletRequest, httpServletResponse, loginUtenteNonAutorizzatoRedirectUrl, sessione);
				}
			} else {
				verificaSessioneUtenteLoggato(httpServletRequest, httpServletResponse, loginSessioneScadutaRedirectUrl, lb); 
			}
		} catch (PrincipalReaderException e) {
			httpServletResponse.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
			try {
				httpServletResponse.getWriter().write(OAuth2Costanti.ERROR_MSG_AUTENTICAZIONE_OAUTH2_NON_DISPONIBILE_SI_E_VERIFICATO_UN_ERRORE + e.getMessage());
			} catch (IOException e1) {
				log.error("Errore durante la lettura del principal: " + e1.getMessage(), e1);
			}
		} catch (IOException e) {
			OAuth2UserServlet.log.error("Errore durante esecuzione redirect: " + e.getMessage(), e);
			httpServletResponse.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
			try {
				httpServletResponse.getWriter().write(OAuth2Costanti.ERROR_MSG_AUTENTICAZIONE_OAUTH2_NON_DISPONIBILE_SI_E_VERIFICATO_UN_ERRORE + e.getMessage());
			} catch (IOException e1) {
				log.error("Errore durante esecuzione redirect: " + e1.getMessage(), e1);
			}
		} catch (UtilsException e) {
			OAuth2Utilities.logError(log, OAuth2Costanti.ERROR_MSG_ERRORE_DURANTE_LA_LETTURA_DELLE_PROPERTIES + e.getMessage(),e);
			httpServletResponse.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
			try {
				httpServletResponse.getWriter().write(OAuth2Costanti.ERROR_MSG_AUTENTICAZIONE_OAUTH2_NON_DISPONIBILE_SI_E_VERIFICATO_UN_ERRORE + e.getMessage());
			} catch (IOException e1) {
				log.error(OAuth2Costanti.ERROR_MSG_ERRORE_DURANTE_LA_LETTURA_DELLE_PROPERTIES + e1.getMessage(), e1);
			}
		}
	}

	private String getPrincipal(HttpServletRequest httpServletRequest, IPrincipalReader principalReader) throws PrincipalReaderException {
		try {
			return principalReader.getPrincipal(httpServletRequest);
		}catch (PrincipalReaderException e) {
			OAuth2Utilities.logError(log, "Errore durante la lettura del principal: " + e.getMessage(),e);
			throw e;
		}
	}

	private IPrincipalReader caricaPrincipalReader(String loginTipo, Properties prop) throws PrincipalReaderException {
		try {
			IPrincipalReader principalReader = PrincipalReaderFactory.getReader(OAuth2UserServlet.log, loginTipo);
			principalReader.init(prop);
			return principalReader;
		}catch (PrincipalReaderException e) {
			OAuth2Utilities.logError(log, "Impossibile caricare il principal reader: "+e.getMessage(), e);
			throw e;
		}
	}

	private LoginBean creaLoginBeanSeNonPresente(HttpServletRequest httpServletRequest, LoginBean lb) {
		if(lb == null){
			lb = leggiLoginBeanDallaSessioneJsf(lb);
		}

		// check lingua
		OAuth2UserServlet.log.debug("Controllo Locale in corso ...");
		Locale loc = leggiLocale();
		OAuth2UserServlet.log.debug("Locale trovato Valore[{}]", loc);

		// Se login bean == null lo creo
		if(lb == null){
			lb = creaLoginBeanNonLoggato(httpServletRequest);
		}

		return lb;
	}

	private void utenteNonTrovatoInSessione(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
			String loginUtenteNonAutorizzatoRedirectUrl, HttpSession sessione) throws IOException {
		// ERRORE
		sessione.setAttribute(org.openspcoop2.web.monitor.core.bean.AbstractLoginBean.LOGIN_BEAN_SESSION_ATTRIBUTE_NAME, null);
		String redirPageUrl =
				StringUtils.isNotEmpty(loginUtenteNonAutorizzatoRedirectUrl) ? 
						loginUtenteNonAutorizzatoRedirectUrl : httpServletRequest.getContextPath() + "/" + "pages/welcome.jsf";
		OAuth2UserServlet.log.debug("Username NULL redirect [{}]", redirPageUrl);

		// Messaggio di errore
		sessione.setAttribute(PrincipalFilter.PRINCIPAL_ERROR_MSG, "Impossibile autenticare l'utente"); 

		httpServletResponse.sendRedirect(redirPageUrl);
	}

	private void controllaEsistenzaUtente(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
			String loginUtenteNonAutorizzatoRedirectUrl, String oauth2LogoutUrl, HttpSession sessione, LoginBean lb,
			String username) throws IOException, UtilsException {

		String loginUtenteNonValidoRedirectUrl = PddMonitorProperties.getInstance(OAuth2UserServlet.log).getLoginUtenteNonValidoRedirectUrl();
		String loginErroreInternoRedirectUrl = PddMonitorProperties.getInstance(OAuth2UserServlet.log).getLoginErroreInternoRedirectUrl();

		// Creo il login bean ed effettuo il login
		lb.setApplicationLogin(false);
		lb.setUsername(username); 
		String loginResult = lb.login();
		if(loginResult.equals("login")){
			OAuth2UserServlet.log.debug("Utente non autorizzato: {}", lb.getLoginErrorMessage());
			String redirPageUrl =
					StringUtils.isNotEmpty(loginUtenteNonAutorizzatoRedirectUrl) ? 
							loginUtenteNonAutorizzatoRedirectUrl : Utility.buildInternalRedirectUrl(httpServletRequest, PrincipalFilter.REDIRECT_ERRORE_DEFAULT);

			String idToken = (String) sessione.getAttribute(OAuth2Costanti.ATTRIBUTE_NAME_ID_TOKEN);
			String logoutUrl = OAuth2Utilities.creaUrlLogout(idToken, oauth2LogoutUrl, redirPageUrl);

			// Messaggio di errore
			sessione.setAttribute(PrincipalFilter.PRINCIPAL_ERROR_MSG, lb.getLoginErrorMessage()); 

			httpServletResponse.sendRedirect(logoutUrl);
		}else if(loginResult.equals("loginError")){
			OAuth2UserServlet.log.debug("Errore durante il login: {}", lb.getLoginErrorMessage());
			String redirPageUrl = StringUtils.isNotEmpty(loginErroreInternoRedirectUrl) ? 
					loginErroreInternoRedirectUrl : Utility.buildInternalRedirectUrl(httpServletRequest, PrincipalFilter.REDIRECT_ERRORE_DEFAULT);

			String idToken = (String) sessione.getAttribute(OAuth2Costanti.ATTRIBUTE_NAME_ID_TOKEN);
			String logoutUrl = OAuth2Utilities.creaUrlLogout(idToken, oauth2LogoutUrl, redirPageUrl);

			// Messaggio di errore
			sessione.setAttribute(PrincipalFilter.PRINCIPAL_ERROR_MSG, lb.getLoginErrorMessage()); 

			httpServletResponse.sendRedirect(logoutUrl);
		}else if(loginResult.equals("loginUserInvalid")){
			OAuth2UserServlet.log.debug("Errore durante il caricamento informazioni utente: {}", lb.getLoginErrorMessage());
			String redirPageUrl = StringUtils.isNotEmpty(loginUtenteNonValidoRedirectUrl) ? 
					loginUtenteNonValidoRedirectUrl : Utility.buildInternalRedirectUrl(httpServletRequest, PrincipalFilter.REDIRECT_ERRORE_DEFAULT);

			String idToken = (String) sessione.getAttribute(OAuth2Costanti.ATTRIBUTE_NAME_ID_TOKEN);
			String logoutUrl = OAuth2Utilities.creaUrlLogout(idToken, oauth2LogoutUrl, redirPageUrl);

			// Messaggio di errore
			sessione.setAttribute(PrincipalFilter.PRINCIPAL_ERROR_MSG, lb.getLoginErrorMessage()); 

			httpServletResponse.sendRedirect(logoutUrl);
		}else{
			OAuth2UserServlet.log.debug("Utente autorizzato");
			sessione.setAttribute(org.openspcoop2.web.monitor.core.bean.AbstractLoginBean.LOGIN_BEAN_SESSION_ATTRIBUTE_NAME, lb);
			String redirPageUrl = httpServletRequest.getContextPath() + "/"+"index.jsp" ;
			httpServletResponse.sendRedirect(redirPageUrl);
		}
	}

	private void verificaSessioneUtenteLoggato(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, String loginSessioneScadutaRedirectUrl, LoginBean lb) throws IOException {
		OAuth2UserServlet.log.debug("Login Bean Utente Loggato controllo validita' sessione..."); 
		// controllo se la sessione e' valida
		boolean isSessionInvalid = SessionUtils.isSessionInvalid(httpServletRequest);

		// Se non sono loggato mi autentico e poi faccio redirect verso la pagina di welcome
		if(isSessionInvalid){
			OAuth2UserServlet.log.debug("Login Bean Utente Loggato controllo validita' sessione [invalida]");
			lb.logout();
			String redirPageUrl =  StringUtils.isNotEmpty(loginSessioneScadutaRedirectUrl) ? loginSessioneScadutaRedirectUrl : httpServletRequest.getContextPath() + "/"+"index.jsp" ;
			httpServletResponse.sendRedirect(redirPageUrl);
		} else {
			OAuth2UserServlet.log.debug("Utente autorizzato");
			String redirPageUrl = httpServletRequest.getContextPath() + "/"+"index.jsp" ;
			httpServletResponse.sendRedirect(redirPageUrl);
		}
	}

	private LoginBean creaLoginBeanNonLoggato(HttpServletRequest httpServletRequest) {
		LoginBean lb;
		// prelevo la lingua della richiesta http
		Locale localeRequest = httpServletRequest.getLocale();
		OAuth2UserServlet.log.debug("Locale trovato nella Request[{}]", localeRequest);

		lb = new LoginBean(true); 
		lb.setLoggedIn(false);
		// supporto alla localizzazione
		//lb.impostaLocale(localeRequest)
		return lb;
	}

	private Locale leggiLocale() {
		Locale loc = null;
		try{
			loc = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		}catch(Exception e){
			OAuth2UserServlet.log.debug("Errore durante controllo Locale: "+ e.getMessage(), e);
			loc = Locale.getDefault();
		}
		return loc;
	}

	private LoginBean leggiLoginBeanDallaSessioneJsf(LoginBean lb) {
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
		return lb;
	}
}
