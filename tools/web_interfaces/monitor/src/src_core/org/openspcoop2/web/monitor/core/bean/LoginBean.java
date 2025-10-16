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
package org.openspcoop2.web.monitor.core.bean;

import java.io.IOException;
import java.sql.Connection;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.commons.search.IdSoggetto;
import org.openspcoop2.core.commons.search.Soggetto;
import org.openspcoop2.core.commons.search.constants.TipoPdD;
import org.openspcoop2.core.config.Configurazione;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.generic_project.utils.ServiceManagerProperties;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.engine.utils.NamingUtils;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.utils.ProtocolUtils;
import org.openspcoop2.utils.IVersionInfo;
import org.openspcoop2.utils.Semaphore;
import org.openspcoop2.utils.SemaphoreLock;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.crypt.PasswordVerifier;
import org.openspcoop2.utils.oauth2.OAuth2Costanti;
import org.openspcoop2.utils.oauth2.OAuth2Utilities;
import org.openspcoop2.utils.resources.MapReader;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.login.LoginException;
import org.openspcoop2.web.lib.users.dao.Stato;
import org.openspcoop2.web.lib.users.dao.User;
import org.openspcoop2.web.monitor.core.constants.Costanti;
import org.openspcoop2.web.monitor.core.core.PddMonitorProperties;
import org.openspcoop2.web.monitor.core.core.Utility;
import org.openspcoop2.web.monitor.core.core.Utils;
import org.openspcoop2.web.monitor.core.dao.DBLoginDAO;
import org.openspcoop2.web.monitor.core.exception.UserInvalidException;
import org.openspcoop2.web.monitor.core.filters.CsrfFilter;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;
import org.openspcoop2.web.monitor.core.utils.DynamicPdDBeanUtils;
import org.openspcoop2.web.monitor.core.utils.MessageUtils;
import org.slf4j.Logger;

/****
 * LoginBean
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class LoginBean extends AbstractLoginBean {

	private static final String ERROR_MSG_IL_SISTEMA_NON_RIESCE_AD_AUTENTICARE_L_UTENTE_0_UTENTE_NON_REGISTRATO = "Il sistema non riesce ad autenticare l''utente {0}: Utente non registrato.";
	private static final String ERROR_MSG_SI_E_VERIFICATO_UN_ERRORE_DURANTE_IL_LOGIN_IMPOSSIBILE_AUTENTICARE_L_UTENTE_0_1 = "Si e'' verificato un errore durante il login, impossibile autenticare l''utente {0}: {1}";
	private static final String ERROR_MSG_SI_E_VERIFICATO_UN_ERRORE_DURANTE_IL_LOGIN_IMPOSSIBILE_AUTENTICARE_L_UTENTE_0 = "Si e'' verificato un errore durante il login, impossibile autenticare l''utente {0}.";
	private static final String ERROR_MSG_IL_SISTEMA_NON_RIESCE_AD_AUTENTICARE_L_UTENTE_0_USERNAME_O_PASSWORD_NON_VALIDI = "Il sistema non riesce ad autenticare l''utente {0}: Username o password non validi.";
	public static final String SEMAPHORE_GET_WIDTH_VOCI_MENU_SOGGETTO = "getWidthVociMenuSoggetto";
	public static final String SEMAPHORE_GET_VOCI_MENU_SOGGETTO = "getVociMenuSoggetto";
	public static final String SEMAPHORE_GET_VOCI_MENU_MODALITA = "getVociMenuModalita";

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
	private String title = null;
	private boolean showExtendedInfo = false;

	private String modalita = null;
	private Boolean visualizzaMenuModalita = null;
	private Boolean visualizzaSezioneModalita = null;
	private List<MenuModalitaItem> vociMenuModalita = null;
	private Semaphore vociMenuModalitaSemaphore = new Semaphore("LoginBean.vociMenuModalita");

	private String soggettoPddMonitor = null;
	private Boolean visualizzaMenuSoggetto = null;
	private Boolean visualizzaSezioneSoggetto = null;
	private List<MenuModalitaItem> vociMenuSoggetto = null;
	private Semaphore vociMenuSoggettoSemaphore = new Semaphore("LoginBean.vociMenuSoggetto");
	private Boolean visualizzaLinkSelezioneSoggetto = null;

	private Configurazione configurazioneGenerale = null;

	private IVersionInfo vInfo;
	private List<String> listaNomiGruppi = null;
	private List<Soggetto> listaSoggettiDisponibiliUtente = null;
	private Boolean showFiltroSoggettoLocale = null;

	private PasswordVerifier passwordVerifier = null;
	private String userToUpdate = null;

	private boolean checkPasswordExpire = false;

	private boolean salvaModificheProfiloSuDB = false;

	private Properties loginProperties = null;
	private boolean loginApplication = false;
	private boolean loginOAuth2Enabled = false;

	public LoginBean(boolean initDao){
		super(initDao);
		this.caricaProperties();
	}

	public LoginBean(){
		super();
		this.caricaProperties();
	}

	public LoginBean(Connection con, boolean autoCommit, ServiceManagerProperties serviceManagerProperties, Logger log) {
		super(con,autoCommit,serviceManagerProperties,log);
		this.caricaProperties();
	}

	private void caricaProperties(){
		try {
			PddMonitorProperties pddMonitorProperties = PddMonitorProperties.getInstance(this.log);
			
			this.showLogout = pddMonitorProperties.isMostraButtonLogout();
			this.logoutDestinazione = pddMonitorProperties.getLogoutUrlDestinazione();

			this.setLogoHeaderImage(pddMonitorProperties.getLogoHeaderImage());
			this.setLogoHeaderLink(pddMonitorProperties.getLogoHeaderLink());
			this.setLogoHeaderTitolo(pddMonitorProperties.getLogoHeaderTitolo()); 
			this.setTitle(pddMonitorProperties.getPddMonitorTitle());
			this.setShowExtendedInfo(pddMonitorProperties.visualizzaPaginaAboutExtendedInfo());

			String utentiPasswordConfig = pddMonitorProperties.getUtentiPassword();

			if(utentiPasswordConfig!=null){
				this.passwordVerifier = new PasswordVerifier(utentiPasswordConfig);
				if(!this.passwordVerifier.existsRestrictionUpdate()){
					this.passwordVerifier = null;
				}
			}

			this.checkPasswordExpire = pddMonitorProperties.isCheckPasswordExpire(this.passwordVerifier);

			this.salvaModificheProfiloSuDB = pddMonitorProperties.isModificaProfiloUtenteDaLinkAggiornaDB();

			this.loginProperties = pddMonitorProperties.getLoginProperties();

			this.loginApplication = pddMonitorProperties.isLoginApplication(); 
			
			this.loginOAuth2Enabled = pddMonitorProperties.isLoginOAuth2Enabled();

		} catch (Exception e) {
			this.log.error("Errore durante la configurazione del logout: " + e.getMessage(),e);
		}
	}

	@Override
	protected void init() {
		super.init();

		if(this.isInitDao()){
			this.setLoginDao(new DBLoginDAO());
		}
	}

	@Override
	public String login() {
		if(this.isApplicationLogin()){
			return loginApplicationEngine();
		}else{
			this.log.info("Verifico il ticket per l'utente [{}]", this.getUsername());
			this.loginErrorMessage = null;
			try{
				this.setLoggedUser(this.getLoginDao().loadUserByUsername(this.getUsername()));
				if(this.getLoggedUser() != null){
					this.setDettaglioUtente(this.getLoggedUser().getUtente());
					this.setModalita(this.getLoggedUser().getUtente().getProtocolloSelezionatoPddMonitor()); 
					this.setSoggettoPddMonitor(this.getLoggedUser().getUtente().getSoggettoSelezionatoPddMonitor());
					this.setLoggedIn(true);
					this.setvInfo(this.getLoginDao().readVersionInfo());
					this.log.info("Utente [{}] autenticato con successo", this.getUsername());
					return LoginBean.getOutcomeLoginSuccess(this.getLoggedUser().getUtente());
				}
			} catch (ServiceException | UtilsException e) {
				this.loginErrorMessage = MessageFormat.format(ERROR_MSG_SI_E_VERIFICATO_UN_ERRORE_DURANTE_IL_LOGIN_IMPOSSIBILE_AUTENTICARE_L_UTENTE_0, this.getUsername()); 
				this.log.error(this.loginErrorMessage);
				return Costanti.OUTCOME_LOGIN_ERROR;
			} catch (NotFoundException e) {
				this.loginErrorMessage = MessageFormat.format(ERROR_MSG_IL_SISTEMA_NON_RIESCE_AD_AUTENTICARE_L_UTENTE_0_UTENTE_NON_REGISTRATO, this.getUsername());
				this.log.debug(this.loginErrorMessage);
				return Costanti.OUTCOME_LOGIN;
			} catch (UserInvalidException e) {
				this.loginErrorMessage = MessageFormat.format(ERROR_MSG_SI_E_VERIFICATO_UN_ERRORE_DURANTE_IL_LOGIN_IMPOSSIBILE_AUTENTICARE_L_UTENTE_0_1, this.getUsername(), e.getMessage());
				this.log.debug(this.loginErrorMessage);
				return Costanti.OUTCOME_LOGIN_USER_INVALID;
			}
		}
		return Costanti.OUTCOME_LOGIN; 
	}

	public String loginApplicationEngine() {
		this.userToUpdate = null;

		if(null == this.getUsername() && this.getPwd() == null){		
			return Costanti.OUTCOME_LOGIN;
		}

		try{
			this.log.info("Verifico le credenziali per l'utente [{}]", this.getUsername());

			if(this.getLoginDao().login(this.getUsername(),this.getPwd())){

				// controllo validita' password
				UserDetailsBean loadUserByUsername = this.getLoginDao().loadUserByUsername(this.getUsername());

				// session fixation
				ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
				HttpSession session = (HttpSession) ec.getSession(true);
				HttpServletRequest request = (HttpServletRequest) ec.getRequest();
				ServletUtils.sessionFixation(this.log, request, session);

				if(this.passwordVerifier != null && this.checkPasswordExpire) {
					User user = loadUserByUsername.getUtente();
					StringBuilder bfMotivazioneErrore = new StringBuilder();
					if(user.isCheckLastUpdatePassword() && this.passwordVerifier.isPasswordExpire(user.getLastUpdatePassword(), bfMotivazioneErrore)) {
						MessageUtils.addErrorMsg(bfMotivazioneErrore.toString());
						this.userToUpdate = this.getUsername();
						this.nuovoTokenCsrfListener(null); // genero un token csrf per l'operazione
						return "utentePasswordChange";
					}
				}

				this.setLoggedIn(true);
				this.setLoggedUser(loadUserByUsername);
				this.setDettaglioUtente(this.getLoggedUser().getUtente());
				this.setModalita(this.getLoggedUser().getUtente().getProtocolloSelezionatoPddMonitor());
				this.setSoggettoPddMonitor(this.getLoggedUser().getUtente().getSoggettoSelezionatoPddMonitor());
				this.setvInfo(this.getLoginDao().readVersionInfo());
				this.log.info("Utente [{}] autenticato con successo", this.getUsername());
				return LoginBean.getOutcomeLoginSuccess(this.getLoggedUser().getUtente());
			}else{
				MessageUtils.addErrorMsg(MessageFormat.format(ERROR_MSG_IL_SISTEMA_NON_RIESCE_AD_AUTENTICARE_L_UTENTE_0_USERNAME_O_PASSWORD_NON_VALIDI, this.getUsername()));
			}
		} catch (ServiceException | UtilsException e) {
			MessageUtils.addErrorMsg(MessageFormat.format(ERROR_MSG_SI_E_VERIFICATO_UN_ERRORE_DURANTE_IL_LOGIN_IMPOSSIBILE_AUTENTICARE_L_UTENTE_0, this.getUsername()));
		} catch (NotFoundException e) {
			MessageUtils.addErrorMsg(MessageFormat.format(ERROR_MSG_IL_SISTEMA_NON_RIESCE_AD_AUTENTICARE_L_UTENTE_0_USERNAME_O_PASSWORD_NON_VALIDI, this.getUsername()));
		} catch (UserInvalidException | LoginException e) {
			MessageUtils.addErrorMsg(MessageFormat.format(ERROR_MSG_SI_E_VERIFICATO_UN_ERRORE_DURANTE_IL_LOGIN_IMPOSSIBILE_AUTENTICARE_L_UTENTE_0_1, this.getUsername(), e.getMessage()));
		}
		return Costanti.OUTCOME_LOGIN; 
	}

	@Override
	public String logout() {

		try {
			ApplicationBean.getInstance().resetAllCache();
		}catch(Throwable t) {/* donothing */}

		String idToken = null;
		String oauth2LogoutUrl = this.loginProperties.getProperty(OAuth2Costanti.PROP_OAUTH2_LOGOUT_ENDPOINT);
		HttpServletRequest httpServletRequest = null;
		try{
			FacesContext fc = FacesContext.getCurrentInstance();
			httpServletRequest = (HttpServletRequest) fc.getExternalContext().getRequest();
			fc.getExternalContext().getSessionMap().put(org.openspcoop2.web.monitor.core.bean.AbstractLoginBean.LOGIN_BEAN_SESSION_ATTRIBUTE_NAME, null);
			HttpSession session = (HttpSession)fc.getExternalContext().getSession(false);
			idToken = (String) session.getAttribute(OAuth2Costanti.ATTRIBUTE_NAME_ID_TOKEN);
			session.setAttribute(org.openspcoop2.web.monitor.core.bean.AbstractLoginBean.LOGIN_BEAN_SESSION_ATTRIBUTE_NAME, null); 
			session.invalidate();
			HttpServletRequest request = (HttpServletRequest) fc.getExternalContext().getRequest();
			ServletUtils.sessionFixation(this.log, request, session);
		}catch(Exception e){
			this.log.error("Si e' verificato un errore durante il logout: "+ e.getMessage(), e);
		}

		try{
			if(idToken != null && oauth2LogoutUrl != null) {
				// preparazione dei parametri
				String redirPageUrl = StringUtils.isNotEmpty(this.logoutDestinazione) ? 
						this.logoutDestinazione : Utility.buildInternalRedirectUrl(httpServletRequest, "/public/login.jsf"); // equivalente della chiamata normale


				String logoutUrl = OAuth2Utilities.creaUrlLogout(idToken, oauth2LogoutUrl, redirPageUrl);

				// se mi sono loggato con oauth2 e la configurazione oauth2 prevede un logoutUrl
				FacesContext fc = FacesContext.getCurrentInstance();
				ExternalContext externalContext = fc.getExternalContext();
				externalContext.redirect(logoutUrl);
				fc.responseComplete();
				return null;
			}
		} catch (IOException e) {
			this.log.error("Si e' verificato un errore durante il logout verso l'oauth2 logoutUrl: " + e.getMessage(), e);
		}

		if(StringUtils.isEmpty(this.logoutDestinazione) 
				||
				(this.loginApplication && (idToken == null))
		){
			if(this.isApplicationLogin()) {
				return Costanti.OUTCOME_LOGIN;
			} else 
				return Costanti.OUTCOME_LOGOUT_AS;
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

	public static String getOutcomeLoginSuccess(User user) {
		String homePage = null;
		for (Stato stato : user.getStati()) {
			if(stato.getOggetto().equals(Costanti.OGGETTO_STATO_UTENTE_HOME_PAGE)) {		
				homePage = Utils.extractValoreStato(stato.getStato());
				break;
			}
		}

		if(homePage != null) {
			if(homePage.equals(Costanti.VALUE_PARAMETRO_UTENTI_HOME_PAGE_MONITORAGGIO_STATISTICHE)) {
				return Costanti.OUTCOME_LOGIN_SUCCESS;
			}
			else if(homePage.equals(Costanti.VALUE_PARAMETRO_UTENTI_HOME_PAGE_MONITORAGGIO_TRANSAZIONI)) {
				return Costanti.OUTCOME_TRANSAZIONI_START;
			}
		}

		return Costanti.OUTCOME_TRANSAZIONI_START;

	}


	public Soggetto getSoggetto(IdSoggetto idSog){
		String key = idSog.getTipo() + "/" + idSog.getNome();
		if(!this.getMapSoggetti().containsKey(key)) {
			this.getMapSoggetti().put(key, this.loginDao.getSoggetto(idSog));
		}

		return this.getMapSoggetti().get(key);
	}

	public List<String> getIdentificativiPorta(User user){
		List<String> lst = new ArrayList<>();

		for (IDSoggetto idSog : user.getSoggetti()) {
			IdSoggetto idsoggetto = new IdSoggetto();
			idsoggetto.setNome(idSog.getNome());
			idsoggetto.setTipo(idSog.getTipo());
			Soggetto s = this.getSoggetto(idsoggetto);

			lst.add(s.getIdentificativoPorta());
		}

		return lst;
	}

	public User getUtente(){
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
		/* donothing */
	}

	public boolean isShowLogout() {
		return this.showLogout;
	}

	public void setShowLogout(boolean showLogout) {
		this.showLogout = showLogout;
	}

	public int getColonneUserInfo() {
		if(this.colonneUserInfo == null) {
			// visualizzazione icona stato (spostata a sx)
			int v1 = 0; //(admin || operatore) ? 1 : 0

			//2 visualizzazione modalita'
			int v2 = this.isVisualizzaSezioneModalita() ? 1 : 0;

			//3 visualizzazione tendina selezione soggetto'
			int v3 = this.isVisualizzaSezioneSoggetto() ? 1 : 0;

			// numero colonne = profiloutente+modalita+statopdd+soggetto
			this.colonneUserInfo = 1 + v1 + v2 + v3;
		}
		return this.colonneUserInfo;
	}

	public void setColonneUserInfo(int colonneUserInfo) {
		this.colonneUserInfo = colonneUserInfo;
	}

	private Integer colonneUserInfo = null;

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

	public String getTitle(){
		return this.title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public boolean isShowExtendedInfo() {
		return this.showExtendedInfo;
	}

	public void setShowExtendedInfo(boolean showExtendedInfo) {
		this.showExtendedInfo = showExtendedInfo;
	}

	public String getModalita() {
		if(this.modalita == null) {
			try {
				List<String> listaNomiProtocolli = this.listaProtocolliDisponibilePerUtentePddMonitor();

				if(listaNomiProtocolli.size() == 1) {
					return listaNomiProtocolli.get(0); 
				}
			}catch(ProtocolException e) {
				return Costanti.VALUE_PARAMETRO_MODALITA_ALL;
			}

			return Costanti.VALUE_PARAMETRO_MODALITA_ALL;
		}

		return this.modalita;
	}

	public void setModalita(String modalita) {
		this.modalita = modalita;

		if(Costanti.VALUE_PARAMETRO_MODALITA_ALL.equals(this.modalita))
			this.modalita = null;
	}

	public boolean isVisualizzaSezioneModalita()  {
		if(this.visualizzaSezioneModalita == null) {
			try {
				List<String> listaNomiProtocolli = this.listaProtocolliDisponibilePerUtentePddMonitor();

				this.visualizzaSezioneModalita = !listaNomiProtocolli.isEmpty();

			}catch(ProtocolException e) {
				this.visualizzaSezioneModalita = false;
			}
		}
		return this.visualizzaSezioneModalita;
	}

	public boolean isVisualizzaMenuModalita()  {
		if(this.visualizzaMenuModalita == null) {
			try {
				List<String> listaNomiProtocolli = this.listaProtocolliDisponibilePerUtentePddMonitor();

				this.visualizzaMenuModalita = listaNomiProtocolli.size() > 1;

			}catch(ProtocolException e) {
				this.visualizzaMenuModalita = false;
			}
		}
		return this.visualizzaMenuModalita;
	}

	public List<String> listaProtocolliDisponibilePerUtentePddMonitor() throws ProtocolException {
		ProtocolFactoryManager pfManager = org.openspcoop2.protocol.engine.ProtocolFactoryManager.getInstance();
		MapReader<String,IProtocolFactory<?>> protocolFactories = pfManager.getProtocolFactories();	
		return Utility.getProtocolli(this.getUtente(), pfManager, protocolFactories, true);
	}

	public void setVisualizzaMenuModalita(boolean visualizzaMenuModalita) {
		this.visualizzaMenuModalita = visualizzaMenuModalita;
	}

	public String cambiaModalita() {
		this.getLoggedUser().getUtente().setProtocolloSelezionatoPddMonitor(this.modalita);
		try {
			if(this.salvaModificheProfiloSuDB) {
				this.loginDao.salvaModalita(this.getLoggedUser().getUtente());
			}
		} catch (Exception e) {
			String errorMessage = "Si e' verificato un errore durante il cambio della modalita', si prega di riprovare piu' tardi.";
			this.log.error(e.getMessage(),e);
			MessageUtils.addErrorMsg(errorMessage);
		}

		// cambio della modalita' provoca il reset del soggetto
		this.colonneUserInfo = null;
		this.listaSoggettiDisponibiliUtente = null;
		this.showFiltroSoggettoLocale = null;
		this.setSoggettoPddMonitor(null);
		this.cambiaSoggetto();


		return Costanti.OUTCOME_MODALITA;
	}

	public String getLabelModalita() throws ProtocolException {
		// prelevo l'eventuale protocollo selezionato
		String labelSelezionato = "";
		try {
			if(this.modalita == null) {
				try {
					List<String> listaNomiProtocolli = this.listaProtocolliDisponibilePerUtentePddMonitor();

					if(listaNomiProtocolli.size() == 1) {
						labelSelezionato = NamingUtils.getLabelProtocollo(listaNomiProtocolli.get(0));  
					} else {
						labelSelezionato = Costanti.LABEL_PARAMETRO_MODALITA_ALL;
					}
				}catch(Exception e) {
					labelSelezionato = Costanti.LABEL_PARAMETRO_MODALITA_ALL;
				}

			} else {
				labelSelezionato = NamingUtils.getLabelProtocollo(this.modalita);
			}
		} catch (Exception e) {
			this.log.error(e.getMessage(), e);
		}
		return MessageFormat.format(Costanti.LABEL_MENU_MODALITA_CORRENTE_WITH_PARAM, labelSelezionato);
	}

	public void setLabelModalita(String labelModalita) { /* donothing */
	}

	public List<MenuModalitaItem> getVociMenuModalita() {

		SemaphoreLock lock = this.vociMenuModalitaSemaphore.acquireThrowRuntime(SEMAPHORE_GET_VOCI_MENU_MODALITA);
		try {

			this.vociMenuModalita = new ArrayList<>();
			try {
				List<String> listaNomiProtocolli = this.listaProtocolliDisponibilePerUtentePddMonitor();

				if(listaNomiProtocolli != null && listaNomiProtocolli.size() > 1) {
					// prelevo l'eventuale protocollo selezionato
					String protocolloSelezionato = this.getUtente().getProtocolloSelezionatoPddMonitor();
					if(listaNomiProtocolli.size()==1) {
						protocolloSelezionato = listaNomiProtocolli.get(0); // forzo
					}

					// prelevo l'eventuale protocollo selezionato
					// popolo la tendina con i protocolli disponibili
					for (String protocolloDisponibile : ProtocolUtils.orderProtocolli(listaNomiProtocolli) ) {
						String labelProtocollo = NamingUtils.getLabelProtocollo(protocolloDisponibile); 
						Integer labelProtocolloWidth = DynamicPdDBeanUtils.getInstance(this.log).getFontWidth(labelProtocollo); 
						MenuModalitaItem menuItem = new MenuModalitaItem(protocolloDisponibile, labelProtocollo, null); 
						menuItem.setLabelWidth(labelProtocolloWidth); 
						if(protocolloSelezionato != null && protocolloSelezionato.equals(protocolloDisponibile))
							menuItem.setDisabled(true); 
						this.vociMenuModalita.add(menuItem);
					}

					// seleziona tutti
					// (this.modalita == null) ? Costanti.ICONA_MENU_UTENTE_CHECKED : Costanti.ICONA_MENU_UTENTE_UNCHECKED
					String labelTutti = Costanti.LABEL_PARAMETRO_MODALITA_ALL;
					Integer labelTuttiWidth = DynamicPdDBeanUtils.getInstance(this.log).getFontWidth(labelTutti); 
					MenuModalitaItem menuItem = new MenuModalitaItem(Costanti.VALUE_PARAMETRO_MODALITA_ALL, labelTutti, null);
					menuItem.setLabelWidth(labelTuttiWidth); 
					if((protocolloSelezionato == null)) 
						menuItem.setDisabled(true);

					this.vociMenuModalita.add(menuItem);
				}

			}catch(ProtocolException e) {
				this.vociMenuModalita = new ArrayList<>();
			}

			return this.vociMenuModalita;
		}finally {
			this.vociMenuModalitaSemaphore.release(lock, SEMAPHORE_GET_VOCI_MENU_MODALITA);
		}
	}

	public int getWidthVociMenuModalita() {

		synchronized (this.vociMenuModalitaSemaphore) {

			if(this.vociMenuModalita.isEmpty())
				return 0;

			int max = 0;
			for (MenuModalitaItem menuModalitaItem : this.vociMenuModalita) {
				if(menuModalitaItem.getLabelWidth() > max)
					max = menuModalitaItem.getLabelWidth();

			}

			return 44 + max;

		}
	}

	public void setVociMenuModalita(List<MenuModalitaItem> vociMenuModalita) { /* donothing */
	}	

	public List<String> getProtocolliSelezionati() {
		List<String> protocolliList = new ArrayList<>();
		try{
			User utente = this.getUtente();

			if(utente.getProtocolloSelezionatoPddMonitor()!=null) {
				protocolliList.add(utente.getProtocolloSelezionatoPddMonitor());
				return protocolliList;
			}

			if(utente.getProtocolliSupportati()!=null && !utente.getProtocolliSupportati().isEmpty()) {
				return utente.getProtocolliSupportati();
			}


			return this.listaProtocolliDisponibilePerUtentePddMonitor();

		}catch (Exception e) {
			this.log.error(e.getMessage(),e);
			protocolliList = new ArrayList<>();
			return protocolliList;
		}
	}

	public List<InformazioniProtocollo> getListaInformazioniProtocollo() {
		List<InformazioniProtocollo> listaInformazioniProtocollo = new ArrayList<>();

		List<String> protocolli = this.getProtocolliSelezionati();

		for (String protocollo : protocolli) {
			try{
				InformazioniProtocollo informazioniProtocollo = new InformazioniProtocollo();
				String descrizioneProtocollo = NamingUtils.getDescrizioneProtocollo(protocollo);
				String webSiteProtocollo = NamingUtils.getWebSiteProtocollo(protocollo);
				String labelProtocollo = NamingUtils.getLabelProtocollo(protocollo);

				informazioniProtocollo.setDescrizioneProtocollo(descrizioneProtocollo);
				informazioniProtocollo.setLabelProtocollo(labelProtocollo);
				informazioniProtocollo.setWebSiteProtocollo(webSiteProtocollo);

				listaInformazioniProtocollo.add(informazioniProtocollo);
			}catch (Exception e) {
				this.log.error("Impossibile caricare le informazioni del protocollo ["+protocollo+"]: " + e.getMessage(),e);
			}
		}

		return listaInformazioniProtocollo;
	}

	public void setListaInformazioniProtocollo(List<InformazioniProtocollo> listaInformazioniProtocollo) { /* donothing */
	}

	public Configurazione getConfigurazioneGenerale(){
		if(this.configurazioneGenerale == null) {
			try {
				this.configurazioneGenerale = this.loginDao.readConfigurazioneGenerale();
			} catch (ServiceException e) {
				this.log.error("Impossibile caricare la configurazione generale: " + e.getMessage(),e);
			}
		}
		return this.configurazioneGenerale;
	}

	public String getSoggettoPddMonitor() {
		if(this.soggettoPddMonitor == null) {
			return Costanti.VALUE_PARAMETRO_MODALITA_ALL;
		}

		return this.soggettoPddMonitor;
	}

	public void setSoggettoPddMonitor(String modalita) {
		this.soggettoPddMonitor = modalita;

		if(Costanti.VALUE_PARAMETRO_MODALITA_ALL.equals(this.soggettoPddMonitor))
			this.soggettoPddMonitor = null;
	}

	public boolean isVisualizzaSezioneSoggetto()  {
		if(this.visualizzaSezioneSoggetto == null) {
			try {
				List<Soggetto> listaSoggetti = this.listaSoggettiDisponibilePerUtentePddMonitor();

				this.visualizzaSezioneSoggetto = !listaSoggetti.isEmpty();

			}catch(Exception e) {
				this.visualizzaSezioneSoggetto = false;
			}
		}
		return this.visualizzaSezioneSoggetto;
	}

	public boolean isVisualizzaMenuSoggetto()  {
		if(this.visualizzaMenuSoggetto == null) {
			try {
				List<Soggetto> listaNomiProtocolli = this.listaSoggettiDisponibilePerUtentePddMonitor();

				this.visualizzaMenuSoggetto = listaNomiProtocolli.size() > 1;

			}catch(ProtocolException e) {
				this.visualizzaMenuSoggetto = false;
			}
		}
		return this.visualizzaMenuSoggetto;
	}

	public void setVisualizzaMenuSoggetto(boolean visualizzaMenuSoggetto) {
		this.visualizzaMenuSoggetto = visualizzaMenuSoggetto;
	}

	public Boolean getVisualizzaLinkSelezioneSoggetto() {
		if(this.visualizzaLinkSelezioneSoggetto == null) {
			try {
				List<Soggetto> listaNomiProtocolli = this.listaSoggettiDisponibilePerUtentePddMonitor();

				Integer numeroMassimoSoggettiSelectListSoggettiOperatiti = PddMonitorProperties.getInstance(this.log).getNumeroMassimoSoggettiOperativiMenuUtente();

				this.visualizzaLinkSelezioneSoggetto = listaNomiProtocolli.size() > numeroMassimoSoggettiSelectListSoggettiOperatiti;

			}catch(ProtocolException | UtilsException e) {
				this.visualizzaLinkSelezioneSoggetto = false;
			}
		}
		return this.visualizzaLinkSelezioneSoggetto;
	}

	public void setVisualizzaLinkSelezioneSoggetto(Boolean visualizzaLinkSelezioneSoggetto) {
		this.visualizzaLinkSelezioneSoggetto = visualizzaLinkSelezioneSoggetto;
	}

	public String cambiaSoggetto() {
		this.getLoggedUser().getUtente().setSoggettoSelezionatoPddMonitor(this.soggettoPddMonitor);

		try {
			if(this.salvaModificheProfiloSuDB) {
				this.loginDao.salvaSoggettoPddMonitor(this.getLoggedUser().getUtente());
			}
			
			this.setvInfo(this.getLoginDao().readVersionInfo());
			
		} catch (Exception e) {
			String errorMessage = "Si e' verificato un errore durante il cambio del soggetto, si prega di riprovare piu' tardi.";
			this.log.error(e.getMessage(),e);
			MessageUtils.addErrorMsg(errorMessage);
		}

		this.visualizzaSezioneSoggetto = null;
		this.visualizzaMenuSoggetto = null;	
		this.visualizzaLinkSelezioneSoggetto = null;

		return Costanti.OUTCOME_SOGGETTO_PDD_MONITOR;
	}

	public void soggettoAutocompleteSelected(ActionEvent ae) { /* donothing */ }

	public void setSoggettoPddMonitorAutocomplete(String modalita) {
		this.soggettoPddMonitor = modalita;

		if(Costanti.VALUE_PARAMETRO_MODALITA_ALL.equals(this.soggettoPddMonitor))
			this.soggettoPddMonitor = null;

		this.getLoggedUser().getUtente().setSoggettoSelezionatoPddMonitor(this.soggettoPddMonitor);

		try {
			if(this.salvaModificheProfiloSuDB) {
				this.loginDao.salvaSoggettoPddMonitor(this.getLoggedUser().getUtente());
			}
		} catch (Exception e) {
			String errorMessage = "Si e' verificato un errore durante il cambio del soggetto, si prega di riprovare piu' tardi.";
			this.log.error(e.getMessage(),e);
			MessageUtils.addErrorMsg(errorMessage);
		}

		this.visualizzaSezioneSoggetto = null;
		this.visualizzaMenuSoggetto = null;	
		this.visualizzaLinkSelezioneSoggetto = null;
	}

	public String getLabelSoggettoNormalized() throws UtilsException {
		return getLabelSoggettoNormalizedEngine(true);
	}

	public String getLabelSoggettoNormalizedSenzaPrefisso() throws UtilsException {
		return getLabelSoggettoNormalizedEngine(false);
	}

	public String getLabelSoggettoNormalizedEngine(boolean addPrefix) throws UtilsException {
		String label = getLabelSoggettoEngine(addPrefix);

		if(label.length() > PddMonitorProperties.getInstance(this.log).getLunghezzaMassimaLabelSelectListSoggettiOperativiMenuUtente()) {
			return Utility.normalizeLabel(label, PddMonitorProperties.getInstance(this.log).getLunghezzaMassimaLabelSelectListSoggettiOperativiMenuUtente());
		}

		return null;
	}

	public String getLabelSoggetto() {
		return getLabelSoggettoEngine(true);
	}

	public String getLabelSoggettoSenzaPrefisso() {
		return getLabelSoggettoEngine(false);
	}

	private String getLabelSoggettoEngine(boolean addPrefix) {
		// prelevo l'eventuale protocollo selezionato
		String labelSelezionato = "";
		try {
			if(this.soggettoPddMonitor == null) {
				labelSelezionato = getLabelSoggettoEngineInner();

			} else {
				String tipoSoggettoOperativoSelezionato = Utility.parseTipoSoggetto(this.soggettoPddMonitor);
				String nomeSoggettoOperativoSelezionato = Utility.parseNomeSoggetto(this.soggettoPddMonitor);
				IDSoggetto idSoggetto = new IDSoggetto(tipoSoggettoOperativoSelezionato, nomeSoggettoOperativoSelezionato);
				labelSelezionato = NamingUtils.getLabelSoggetto(idSoggetto);
			}
		} catch (ProtocolException e) {
			this.log.error(e.getMessage(), e);
		}
		return addPrefix ? MessageFormat.format(Costanti.LABEL_MENU_SOGGETTO_CORRENTE_WITH_PARAM, labelSelezionato) : labelSelezionato;
	}

	private String getLabelSoggettoEngineInner() {
		String labelSelezionato = "";
		try {
			List<Soggetto> listaNomiSoggetti = this.listaSoggettiDisponibilePerUtentePddMonitor();

			if(listaNomiSoggetti.size() == 1) {
				IDSoggetto idSoggetto = new IDSoggetto(listaNomiSoggetti.get(0).getTipoSoggetto(), listaNomiSoggetti.get(0).getNomeSoggetto()); 
				labelSelezionato = NamingUtils.getLabelSoggetto(idSoggetto);  
			} else {
				labelSelezionato = Costanti.LABEL_PARAMETRO_MODALITA_ALL;
			}
		}catch(ProtocolException e) {
			labelSelezionato = Costanti.LABEL_PARAMETRO_MODALITA_ALL;
		}
		return labelSelezionato;
	}

	public void setLabelSoggetto(String labelSoggetto) { /* donothing */
	}

	public void setLabelSoggettoSenzaPrefisso(String labelSoggetto) { /* donothing */
	}

	public List<Soggetto> listaSoggettiDisponibilePerUtentePddMonitor() throws ProtocolException {
		if(this.listaSoggettiDisponibiliUtente == null) {
			this.listaSoggettiDisponibiliUtente = listaSoggettiDisponibilePerUtentePddMonitorEngine();
		}

		return this.listaSoggettiDisponibiliUtente;
	}

	private List<Soggetto> listaSoggettiDisponibilePerUtentePddMonitorEngine() throws ProtocolException {
		User utente = this.getUtente();
		List<String> protocolliDispondibili = this.listaProtocolliDisponibilePerUtentePddMonitor();
		String protocolloSelezionato = utente.getProtocolloSelezionatoPddMonitor();
		if(protocolliDispondibili.size()==1) {
			protocolloSelezionato = protocolliDispondibili.get(0); // forzo
		}
		return listaSoggettiDisponibiliPerProtocollo(this.getLoggedUser(), protocolloSelezionato);
	}

	public static List<Soggetto> listaSoggettiDisponibiliPerProtocollo(UserDetailsBean userDetailsBean, String protocolloSelezionato) {
		List<Soggetto> soggettiOperativiDisponibiliUtente = new ArrayList<>();
		List<Soggetto> soggettiOperativi = DynamicPdDBeanUtils.getInstance(LoggerManager.getPddMonitorCoreLogger()).getListaSoggetti(protocolloSelezionato, TipoPdD.OPERATIVO);

		if(protocolloSelezionato!=null && !"".equals(protocolloSelezionato) && soggettiOperativi != null && !soggettiOperativi.isEmpty()) {
			List<Soggetto> soggettiAssociatiUtente = Utility.getSoggettiOperativiAssociatiAlProfilo(userDetailsBean, protocolloSelezionato);  

			if(soggettiAssociatiUtente.isEmpty())
				return soggettiOperativi;
			else 
				return soggettiAssociatiUtente;
		}
		return soggettiOperativiDisponibiliUtente;
	}

	public List<org.openspcoop2.web.monitor.core.bean.SelectItem> soggettoPddMonitorAutoComplete(Object val) {
		List<org.openspcoop2.web.monitor.core.bean.SelectItem> listaGruppi = new ArrayList<>();
		if(val==null || StringUtils.isEmpty((String)val)) {
			//donothing
		}else{
			List<MenuModalitaItem>  vociMenuSoggettoList  = this.getVociMenuSoggetto();

			for (MenuModalitaItem menuModalitaItem : vociMenuSoggettoList) {
				if(menuModalitaItem.getLabel().toUpperCase().contains(((String)val).toUpperCase())) {
					listaGruppi.add(new org.openspcoop2.web.monitor.core.bean.SelectItem(menuModalitaItem.getValue(), menuModalitaItem.getLabel()));
				}
			}
		}
		return listaGruppi;
	}

	public List<MenuModalitaItem> getVociMenuSoggetto() {

		SemaphoreLock lock = this.vociMenuSoggettoSemaphore.acquireThrowRuntime(SEMAPHORE_GET_VOCI_MENU_SOGGETTO); 
		try{

			this.vociMenuSoggetto = new ArrayList<>();
			try {
				User utente = this.getUtente();
				List<String> protocolliDispondibili = this.listaProtocolliDisponibilePerUtentePddMonitor();
				String protocolloSelezionato = utente.getProtocolloSelezionatoPddMonitor();
				if(protocolliDispondibili.size()==1) {
					protocolloSelezionato = protocolliDispondibili.get(0); // forzo
				}

				// prelevo il soggetto selezionato
				String soggettoOperativoSelezionato = utente.getSoggettoSelezionatoPddMonitor();
				IDSoggetto idSoggettoOperativo = null;
				if(soggettoOperativoSelezionato!=null) {
					String tipoSoggettoOperativoSelezionato = Utility.parseTipoSoggetto(soggettoOperativoSelezionato);
					String nomeSoggettoOperativoSelezionato = Utility.parseNomeSoggetto(soggettoOperativoSelezionato);
					idSoggettoOperativo = new IDSoggetto(tipoSoggettoOperativoSelezionato, nomeSoggettoOperativoSelezionato);
				}

				List<Soggetto> soggettiOperativi = listaSoggettiDisponibilePerUtentePddMonitor();

				// visualizzo il menu' soggetti solo se e' stato selezionato un protocollo 
				if(protocolloSelezionato!=null && !"".equals(protocolloSelezionato) &&
						soggettiOperativi != null && !soggettiOperativi.isEmpty()) {

					if(soggettoOperativoSelezionato==null && soggettiOperativi.size()==1) {
						Soggetto soggetto = soggettiOperativi.get(0);
						IDSoggetto idSoggetto = new IDSoggetto(soggetto.getTipoSoggetto(), soggetto.getNomeSoggetto()); 
						soggettoOperativoSelezionato = idSoggetto.toString(); // forzo
					}

					if(soggettiOperativi.size()>1) {
						List<String> listaLabel = new ArrayList<>();
						Map<String, IDSoggetto> mapLabelIds = new HashMap<>();
						for (Soggetto soggetto : soggettiOperativi) {
							IDSoggetto idSoggetto = new IDSoggetto(soggetto.getTipoSoggetto(), soggetto.getNomeSoggetto()); 
							String labelSoggetto = NamingUtils.getLabelSoggetto(idSoggetto);
							if(!listaLabel.contains(labelSoggetto)) {
								listaLabel.add(labelSoggetto);
								mapLabelIds.put(labelSoggetto, idSoggetto);
							}
						}

						// Per ordinare in maniera case insensistive
						Collections.sort(listaLabel, new Comparator<>() {
							@Override
							public int compare(String o1, String o2) {
								return o1.toLowerCase().compareTo(o2.toLowerCase());
							}
						});

						int i = 1;
						for (String label : listaLabel) {
							String labelSoggetto = NamingUtils.getLabelSoggetto(mapLabelIds.get(label)); 
							MenuModalitaItem menuItem = new MenuModalitaItem(mapLabelIds.get(label).toString(), labelSoggetto, null); 

							if(soggettoOperativoSelezionato != null && mapLabelIds.get(label).toString().equals(idSoggettoOperativo.toString()))
								menuItem.setDisabled(true);

							Integer labelSoggettoWidth = DynamicPdDBeanUtils.getInstance(this.log).getFontWidth(menuItem.getLabel()); 
							if(labelSoggetto.length() > PddMonitorProperties.getInstance(this.log).getLunghezzaMassimaLabelSelectListSoggettiOperativiMenuUtente()) {
								menuItem.setTooltip(labelSoggetto);
								menuItem.setLabel(Utility.normalizeLabel(labelSoggetto, PddMonitorProperties.getInstance(this.log).getLunghezzaMassimaLabelSelectListSoggettiOperativiMenuUtente()));
								// per misurare la dimensione utilizzo solo la prima linea
								labelSoggettoWidth = DynamicPdDBeanUtils.getInstance(this.log).getFontWidth(Utility.normalizeLabel(labelSoggetto, 
										PddMonitorProperties.getInstance(this.log).getLunghezzaMassimaLabelSelectListSoggettiOperativiMenuUtente())); 
							}

							menuItem.setLabelWidth(labelSoggettoWidth); 

							menuItem.setId("voceSoggetto_"+ (i++));

							this.vociMenuSoggetto.add(menuItem);
						}

						// seleziona tutti
						// (this.modalita == null) ? Costanti.ICONA_MENU_UTENTE_CHECKED : Costanti.ICONA_MENU_UTENTE_UNCHECKED
						String labelTutti = Costanti.LABEL_PARAMETRO_MODALITA_ALL;
						Integer labelTuttiWidth = DynamicPdDBeanUtils.getInstance(this.log).getFontWidth(labelTutti); 
						MenuModalitaItem menuItem = new MenuModalitaItem(Costanti.VALUE_PARAMETRO_MODALITA_ALL, labelTutti, null);
						menuItem.setLabelWidth(labelTuttiWidth); 
						if((soggettoOperativoSelezionato == null)) 
							menuItem.setDisabled(true);

						menuItem.setId("voceSoggetto_"+ (i++));

						this.vociMenuSoggetto.add(menuItem);
					}
				}

			}catch(ProtocolException | UtilsException e) {
				this.vociMenuSoggetto = new ArrayList<>();
			}

			return this.vociMenuSoggetto;

		}finally {
			this.vociMenuSoggettoSemaphore.release(lock, SEMAPHORE_GET_VOCI_MENU_SOGGETTO); 
		}
	}

	public int getWidthVociMenuSoggetto() {

		SemaphoreLock lock = this.vociMenuSoggettoSemaphore.acquireThrowRuntime(SEMAPHORE_GET_WIDTH_VOCI_MENU_SOGGETTO); 
		try{

			if(this.vociMenuSoggetto.isEmpty())
				return 0;

			int max = 0;
			for (MenuModalitaItem menuModalitaItem : this.vociMenuSoggetto) {
				if(menuModalitaItem.getLabelWidth() > max)
					max = menuModalitaItem.getLabelWidth();

			}

			return 44 + max;

		}finally {
			this.vociMenuSoggettoSemaphore.release(lock, SEMAPHORE_GET_WIDTH_VOCI_MENU_SOGGETTO); 
		}
	}

	public void setVociMenuSoggetto(List<MenuModalitaItem> vociMenuModalita) { /* donothing */ }

	public boolean isShowFiltroSoggettoLocale(){
		if(this.showFiltroSoggettoLocale == null) {
			this.showFiltroSoggettoLocale =  isShowFiltroSoggettoLocaleEngine();
		}

		return this.showFiltroSoggettoLocale;
	}

	private boolean isShowFiltroSoggettoLocaleEngine() {
		try {
			User utente = Utility.getLoggedUtente();

			String soggettoOperativoSelezionato = utente.getSoggettoSelezionatoPddMonitor();
			// utente ha selezionato un soggetto
			if(soggettoOperativoSelezionato != null) {
				return false;
			}

			List<String> protocolliDispondibili = this.listaProtocolliDisponibilePerUtentePddMonitor();
			String protocolloSelezionato = utente.getProtocolloSelezionatoPddMonitor();
			if(protocolliDispondibili.size()==1) {
				protocolloSelezionato = protocolliDispondibili.get(0); // forzo
			}

			int numeroSoggettiDisponibili = Utility.getLoggedUser().getUtenteSoggettoProtocolliMap().containsKey(protocolloSelezionato) ? Utility.getLoggedUser().getUtenteSoggettoProtocolliMap().get(protocolloSelezionato).size() : 0;

			if(numeroSoggettiDisponibili == 1)
				return false;

			List<Soggetto> soggettiOperativi = DynamicPdDBeanUtils.getInstance(this.log).getListaSoggetti(protocolloSelezionato, TipoPdD.OPERATIVO);
			numeroSoggettiDisponibili = soggettiOperativi != null ? soggettiOperativi.size() : 0;

			if(numeroSoggettiDisponibili == 1)
				return false;
		} catch (ProtocolException e) {
			this.log.error("Si e' verificato un errore durante il caricamento della lista protocolli: " + e.getMessage(), e);
		}
		return true;
	}


	public IVersionInfo getvInfo() {
		return this.vInfo;
	}

	public void setvInfo(IVersionInfo vInfo) throws UtilsException {
		this.vInfo = vInfo;
		if(this.vInfo!=null) {
			String soggettoSelezionato = Utility.getSoggettoSelezionatoPerVersionInfo();
			String titolo = PddMonitorProperties.getInstance(this.log).getPddMonitorTitle();
			if(!StringUtils.isEmpty(this.vInfo.getErrorTitleSuffix(soggettoSelezionato))) {
				if(!this.vInfo.getErrorTitleSuffix(soggettoSelezionato).startsWith(" ")) {
					titolo = titolo + " ";
				}
				titolo = titolo + this.vInfo.getErrorTitleSuffix(soggettoSelezionato);
			}
			else if(!StringUtils.isEmpty(this.vInfo.getWarningTitleSuffix(soggettoSelezionato))) {
				if(!this.vInfo.getWarningTitleSuffix(soggettoSelezionato).startsWith(" ")) {
					titolo = titolo + " ";
				}
				titolo = titolo + this.vInfo.getWarningTitleSuffix(soggettoSelezionato);
			}
			this.setTitle(titolo);
		}
	}
	public List<String> getListaNomiGruppi(){
		if(this.listaNomiGruppi == null) {
			try {
				this.listaNomiGruppi = DynamicPdDBeanUtils.getInstance(LoggerManager.getPddMonitorCoreLogger()).getListaNomiGruppi();
			} catch (Exception e) {
				this.listaNomiGruppi =new ArrayList<>();
			}
		} 
		return this.listaNomiGruppi;
	}

	public boolean isAmministratore() {
		return this.getLoggedUser().isAdmin();
	}

	public String ricaricaUtenteDopoCambioPasswordScaduta() {

		this.log.info("Ricarico Profilo per l'utente [{}]", this.getUsername());
		this.loginErrorMessage = null;
		this.userToUpdate = null;
		try{
			this.setLoggedUser(this.getLoginDao().loadUserByUsername(this.getUsername()));
			if(this.getLoggedUser() != null){
				this.setDettaglioUtente(this.getLoggedUser().getUtente());
				this.setModalita(this.getLoggedUser().getUtente().getProtocolloSelezionatoPddMonitor()); 
				this.setSoggettoPddMonitor(this.getLoggedUser().getUtente().getSoggettoSelezionatoPddMonitor());
				this.setLoggedIn(true);
				this.setvInfo(this.getLoginDao().readVersionInfo());
				this.log.info("Profilo Utente [{}] caricato con successo", this.getUsername());
				return LoginBean.getOutcomeLoginSuccess(this.getLoggedUser().getUtente());
			}
			return Costanti.OUTCOME_LOGIN;
		} catch (ServiceException | UtilsException e) {
			this.loginErrorMessage = "Si e' verificato un errore durante il caricamento del profilo utente, impossibile autenticare l'utente "+this.getUsername()+"."; 
			this.log.error(this.loginErrorMessage);
			return Costanti.OUTCOME_LOGIN_ERROR;
		} catch (NotFoundException e) {
			this.loginErrorMessage = "Il sistema non riesce a caricare il profilo utente "+this.getUsername()+": Utente non registrato.";
			this.log.debug(this.loginErrorMessage);
			return Costanti.OUTCOME_LOGIN;
		} catch (UserInvalidException e) {
			this.loginErrorMessage = "Si e' verificato un errore durante il caricamento del profilo utente, impossibile autenticare l'utente "+this.getUsername()+": " + e.getMessage();
			this.log.debug(this.loginErrorMessage);
			return Costanti.OUTCOME_LOGIN_USER_INVALID;
		}
	}

	public String getUserToUpdate() {
		return this.userToUpdate;
	}

	public void resetUserToUpdate() {
		this.userToUpdate = null;
	}

	public String getCsrf() {
		FacesContext fc = FacesContext.getCurrentInstance();
		ExternalContext extCtx = fc.getExternalContext();
		HttpSession session = (HttpSession)extCtx.getSession(false);
		String tokenCSRF = CsrfFilter.leggiTokenCSRF(session);
		this.log.debug("Letto Token CSRF: [{}]", tokenCSRF); 
		return tokenCSRF;
	}

	public void nuovoTokenCsrfListener(ActionEvent ae) {
		FacesContext fc = FacesContext.getCurrentInstance();
		ExternalContext extCtx = fc.getExternalContext();
		HttpSession session = (HttpSession)extCtx.getSession(false);
		String nuovoTokenCSRF = CsrfFilter.generaESalvaTokenCSRF(session);
		this.log.debug("Generato Nuovo Token CSRF: [{}]", nuovoTokenCSRF);
	}

	public void setCsrf(String csrf) { /* donothing*/}

	public String avviaLoginOAuth2() {

		String state = UUID.randomUUID().toString();
		// 1) Costruisci l'URL di autorizzazione Keycloak
		String authorizationUrl = null;
		try {
			authorizationUrl = OAuth2Utilities.getURLLoginOAuth2(this.loginProperties, state);
		} catch (Exception e) {
			this.loginErrorMessage = "Si e' verificato un errore il login OAuth2";
			this.log.error(this.loginErrorMessage);
			return Costanti.OUTCOME_LOGIN_ERROR;
		}

		this.log.debug("Invio richiesta di autorizzazione alla URL: [{}]", authorizationUrl);

		ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
		try {
			// salvataggio dello scope per fare il check nella callback
			ec.getSessionMap().put(OAuth2Costanti.ATTRIBUTE_NAME_OAUTH2_STATE, state);

			ec.redirect(authorizationUrl);
		} catch (IOException e) {
			this.loginErrorMessage = "Si e' verificato un errore il login OAuth2, impossibile autenticare l'utente.";
			this.log.error(this.loginErrorMessage);
			return Costanti.OUTCOME_LOGIN_ERROR;
		}
		return null;
	}

	public boolean isLoginApplication() {
		return this.loginApplication;
	}
	
	public boolean isLoginOAuth2Enabled() {
		return this.loginOAuth2Enabled;
	}

	public boolean isMultiLoginEnabled() {
		return this.isLoginApplication() && this.isLoginOAuth2Enabled();
	}

	public boolean isUtenteLoggatoOAuth2() {
		FacesContext fc = FacesContext.getCurrentInstance();
		ExternalContext extCtx = fc.getExternalContext();
		HttpSession session = (HttpSession)extCtx.getSession(false);
		return ServletUtils.isUtenteLoggatoConOAuth2(session);
	}
}
