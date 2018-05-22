package org.openspcoop2.web.monitor.core.bean;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpSession;

import org.openspcoop2.core.commons.search.Soggetto;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.generic_project.exception.ServiceException;

import org.openspcoop2.web.lib.users.dao.User;
import org.openspcoop2.web.monitor.core.dao.DBLoginDAO;
import org.openspcoop2.web.monitor.core.dao.ILoginDAO;
import org.openspcoop2.web.monitor.core.exception.UserInvalidException;
import org.openspcoop2.web.monitor.core.utils.MessageUtils;

public abstract class AbstractLoginBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String username;
	private String pwd;

	private UserDetailsBean utenteLoggato = null;
	private User dettaglioUtente = null;
	private boolean loggedIn = false;

	// Serve per indicare se il Bean deve inizializzare da solo il LoginDAO, se
	// false viene inizializzato da Faces...
	private boolean initDao = false;
	private boolean applicationLogin = true;
	protected transient ILoginDAO loginDao;
	private transient Map<String, Soggetto> mapSoggetti = null;

//	public static final String LOGIN_BEAN_SESSION_ATTRIBUTE_NAME = "scopedTarget.loginBean"; // nome del login bean in sessione quando e' gestito dal roxy aspectj
	public static final String LOGIN_BEAN_SESSION_ATTRIBUTE_NAME = "loginBean";

	public AbstractLoginBean() {
		this(false);
	}

	public AbstractLoginBean(boolean initDao) {
		this.initDao = initDao;

		init();
	}

	protected void init() {
		if (this.initDao) {
			this.loginDao = new DBLoginDAO();
		}
		
		this.mapSoggetti = new HashMap<String, Soggetto>();
	}

	public void setLoginDao(ILoginDAO loginDao) {
		this.loginDao = loginDao;
	}

	public ILoginDAO getLoginDao() {
		return this.loginDao;
	}

	public String logout() {
		try {
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getExternalContext().getSessionMap().put(AbstractLoginBean.LOGIN_BEAN_SESSION_ATTRIBUTE_NAME, null);
			HttpSession session = (HttpSession) fc.getExternalContext().getSession(false);
			session.invalidate();
			this.loggedIn = false;
		} catch (Exception e) {
		}
		return "login";
	}

	public String login() {
		if (null == this.username || null == this.pwd) {
			return "login";
		}

		this.loggedIn = false;
		try {
			if (this.loginDao.login(this.username, this.pwd)) {
				this.utenteLoggato = this.loginDao.loadUserByUsername(this.username);
				this.dettaglioUtente = this.loginDao.getUtente(this.utenteLoggato);
				this.loggedIn = true;
				return "loginSuccess";
			} else {
				MessageUtils.addErrorMsg("Errore: Username o password non validi.");
			}

		} catch (ServiceException e) {
			MessageUtils.addErrorMsg("Si e' verificato un errore, impossibile autenticare l'utente.");
		} catch (NotFoundException e) {
			MessageUtils.addErrorMsg("Errore: Username o password non validi.");
		} catch (UserInvalidException e) {
			MessageUtils.addErrorMsg("Si e' verificato un errore, impossibile autenticare l'utente: " + e.getMessage());
		}

		return "login";
	}

	public UserDetailsBean getLoggedUser() {
		return this.utenteLoggato;
	}
	
	public void setLoggedUser(UserDetailsBean utenteLoggato) {
		this.utenteLoggato = utenteLoggato;
	}

	public String getUsername() {
		return this.username;
//		UserDetailsBean u = this.getLoggedUser();
//		return u != null ? u.getUsername() : null;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPwd() {
		return this.pwd;
//		UserDetailsBean u = this.getLoggedUser();
//		return u != null ? u.getPassword() : null;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public void logoutListener(ActionEvent ae) {

	}

	public boolean isLoggedIn() {
		return this.loggedIn;
	}

	public void setLoggedIn(boolean isLoggedIn) {
		this.loggedIn = isLoggedIn;
	}

	public boolean isInitDao() {
		return this.initDao;
	}

	public void setInitDao(boolean initDao) {
		this.initDao = initDao;
	}

	public boolean isApplicationLogin() {
		return this.applicationLogin;
	}

	public void setApplicationLogin(boolean applicationLogin) {
		this.applicationLogin = applicationLogin;
	}

	public User getDettaglioUtente() {
		return this.dettaglioUtente;
	}

	public void setDettaglioUtente(User dettaglioUtente) {
		this.dettaglioUtente = dettaglioUtente;
	}

	public Map<String, Soggetto> getMapSoggetti() {
		return this.mapSoggetti;
	}

	public void setMapSoggetti(Map<String, Soggetto> mapSoggetti) {
		this.mapSoggetti = mapSoggetti;
	}
	
}
