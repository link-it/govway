package org.openspcoop2.web.monitor.core.bean;

import java.util.ArrayList;
import java.util.List;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.slf4j.Logger;

import it.link.pdd.core.utenti.Utente;
import it.link.pdd.core.utenti.UtenteSoggetto;
import org.openspcoop2.core.commons.search.IdSoggetto;
import org.openspcoop2.core.commons.search.Soggetto;
import org.openspcoop2.web.monitor.core.exception.UserInvalidException;
import org.openspcoop2.web.monitor.core.utils.MessageUtils;
import org.openspcoop2.web.monitor.core.core.PddMonitorProperties;
import org.openspcoop2.web.monitor.core.core.Utility;
import org.openspcoop2.web.monitor.core.dao.DBLoginDAO;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;

public class LoginBean extends AbstractLoginBean {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Logger log = LoggerManager.getPddMonitorCoreLogger();
	
	private String loginErrorMessage = null;
	
	private boolean showLogout = true;
	
	private String logoutDestinazione = null;
	
	private String logoHeaderImage = null;
	private String logoHeaderTitolo = null;
	private String logoHeaderLink = null;
	
	public LoginBean(boolean initDao){
		super(initDao);
		this.caricaProperties();
	}
	
	public LoginBean(){
		super();
		this.caricaProperties();
	}
	
	private void caricaProperties(){
		try {
			this.showLogout = PddMonitorProperties.getInstance(this.log).isMostraButtonLogout();
			this.logoutDestinazione = PddMonitorProperties.getInstance(this.log).getLogoutUrlDestinazione();
			
			this.setLogoHeaderImage(PddMonitorProperties.getInstance(this.log).getLogoHeaderImage());
			this.setLogoHeaderLink(PddMonitorProperties.getInstance(this.log).getLogoHeaderLink());
			this.setLogoHeaderTitolo(PddMonitorProperties.getInstance(this.log).getLogoHeaderTitolo()); 

		} catch (Exception e) {
			this.log.error("Errore durante la configurazione del logout: " + e.getMessage(),e);
		}
	}
	
	@Override
	protected void init() {
		if(this.isInitDao()){
			this.setLoginDao(new DBLoginDAO());
		}
	}
	
	@Override
	public String login() {
		if(this.isApplicationLogin()){

			if(null == this.getUsername() && this.getPwd() == null){		
				return "login";
			}

			try{
				this.log.info("Verifico le credenziali per l'utente ["+this.getUsername()+"]");

				if(this.getLoginDao().login(this.getUsername(),this.getPwd())){
					//			ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
					//			HttpSession session = (HttpSession) ec.getSession(true);
					//			session.setAttribute("logged", true);
					this.setLoggedIn(true);
					this.setLoggedUser(this.getLoginDao().loadUserByUsername(this.getUsername()));
					this.setDettaglioUtente(this.getLoginDao().getUtente(this.getLoggedUser()));
					this.log.info("Utente ["+this.getUsername()+"] autenticato con successo");
					return "loginSuccess";
				}else{
					MessageUtils.addErrorMsg("Il sistema non riesce ad autenticare l'utente "+this.getUsername()+": Username o password non validi.");
				}
			} catch (ServiceException e) {
				MessageUtils.addErrorMsg("Si e' verificato un errore durante il login, impossibile autenticare l'utente "+this.getUsername()+".");
			} catch (NotFoundException e) {
				MessageUtils.addErrorMsg("Il sistema non riesce ad autenticare l'utente "+this.getUsername()+": Username o password non validi.");
			} catch (UserInvalidException e) {
				MessageUtils.addErrorMsg("Si e' verificato un errore durante il login, impossibile autenticare l'utente "+this.getUsername()+": " + e.getMessage());
			}
		}else{
			this.log.info("Verifico il ticket per l'utente ["+this.getUsername()+"]");
			this.loginErrorMessage = null;
			try{
				this.setLoggedUser(this.getLoginDao().loadUserByUsername(this.getUsername()));
				this.setDettaglioUtente(this.getLoginDao().getUtente(this.getLoggedUser()));
				
				if(this.getLoggedUser() != null){
				this.setLoggedIn(true);
				this.log.info("Utente ["+this.getUsername()+"] autenticato con successo");
				return "loginSuccess";
			}
			} catch (ServiceException e) {
				this.loginErrorMessage = "Si e' verificato un errore durante il login, impossibile autenticare l'utente "+this.getUsername()+"."; 
				this.log.error(this.loginErrorMessage);
				return "loginError";
			} catch (NotFoundException e) {
				this.loginErrorMessage = "Il sistema non riesce ad autenticare l'utente "+this.getUsername()+": Utente non registrato.";
				this.log.debug(this.loginErrorMessage);
				return "login";
			} catch (UserInvalidException e) {
				this.loginErrorMessage = "Si e' verificato un errore durante il login, impossibile autenticare l'utente "+this.getUsername()+": " + e.getMessage();
				this.log.debug(this.loginErrorMessage);
				return "loginUserInvalid";
			}
		}
		return "login"; 
	}
	
	@Override
	public String logout() {
		try{
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getExternalContext().getSessionMap().put(org.openspcoop2.web.monitor.core.bean.LoginBean.LOGIN_BEAN_SESSION_ATTRIBUTE_NAME, null);
			HttpSession session = (HttpSession)fc.getExternalContext().getSession(false);
			session.setAttribute(org.openspcoop2.web.monitor.core.bean.LoginBean.LOGIN_BEAN_SESSION_ATTRIBUTE_NAME, null); 
			session.invalidate();
		}catch(Exception e){}

		if(StringUtils.isEmpty(this.logoutDestinazione)){
		if(this.isApplicationLogin())
			return "login";
		else 
			return "logoutAS";
		}else {
			try{
				FacesContext fc = FacesContext.getCurrentInstance();
				ExternalContext externalContext = fc.getExternalContext();
				externalContext.redirect(this.logoutDestinazione);
				fc.responseComplete();
			}catch(Exception e ){
				this.log.error("Si e' verificato un errore durante il logout verso un url custom: "+ e.getMessage(), e);
			}
			return null;
		}
	}
	
	
	public Soggetto getSoggetto(IdSoggetto idSog){
		String key = idSog.getTipo() + "/" + idSog.getNome();
		if(!this.getMapSoggetti().containsKey(key)) {
			this.getMapSoggetti().put(key, this.loginDao.getSoggetto(idSog));
		}

		return this.getMapSoggetti().get(key);
	}
	
	public List<String> getIdentificativiPorta(Utente user){
		List<String> lst = new ArrayList<String>();

		for (UtenteSoggetto us : user.getUtenteSoggettoList()) {
			IdSoggetto idsoggetto = new IdSoggetto();
			idsoggetto.setNome(us.getSoggetto().getNome());
			idsoggetto.setTipo(us.getSoggetto().getTipo());
			Soggetto s = this.getSoggetto(idsoggetto);
			
			lst.add(s.getIdentificativoPorta());
		}

		return lst;
	}
	
	public Utente getUtente(){
		return this.getDettaglioUtente();
	}
	
	public String getLoginErrorMessage() {
		return this.loginErrorMessage;
	}

	public void setLoginErrorMessage(String loginErrorMessage) {
		this.loginErrorMessage = loginErrorMessage;
	}

	@Override
	public void logoutListener(ActionEvent ae) {
		
	}
	
	public boolean isShowLogout() {
		return this.showLogout;
	}

	public void setShowLogout(boolean showLogout) {
		this.showLogout = showLogout;
	}
	
	public int getColonneUserInfo() {
		boolean admin = this.getLoggedUser().isAdmin();
		boolean operatore = Utility.getLoggedUser().isOperatore();
		
		//1 !applicationBean.amministratore && loginBean.loggedUser.sizeSoggetti==1
		int v1 = (!admin && this.getLoggedUser().getSizeSoggetti() == 1) ? 1 : 0;
		
		//2 applicationBean.amministratore or applicationBean.operatore
		int v2 = (admin || operatore) ? 1 : 0;
		
		this.colonneUserInfo = 1 + v1 + v2;
		
		return this.colonneUserInfo;
	}

	public void setColonneUserInfo(int colonneUserInfo) {
		this.colonneUserInfo = colonneUserInfo;
	}

	private int colonneUserInfo = 0;
	
	public String getLogoHeaderImage() {
		return this.logoHeaderImage;
	}

	public void setLogoHeaderImage(String logoHeaderImage) {
		this.logoHeaderImage = logoHeaderImage;
	}

	public String getLogoHeaderTitolo() {
		return this.logoHeaderTitolo;
	}

	public void setLogoHeaderTitolo(String logoHeaderTitolo) {
		this.logoHeaderTitolo = logoHeaderTitolo;
	}

	public String getLogoHeaderLink() {
		return this.logoHeaderLink;
	}

	public void setLogoHeaderLink(String logoHeaderLink) {
		this.logoHeaderLink = logoHeaderLink;
	}
	
}
