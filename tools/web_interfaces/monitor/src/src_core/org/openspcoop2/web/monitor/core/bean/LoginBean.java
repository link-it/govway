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
package org.openspcoop2.web.monitor.core.bean;

import java.sql.Connection;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
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
import org.openspcoop2.utils.crypt.PasswordVerifier;
import org.openspcoop2.utils.resources.MapReader;
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
	private Semaphore vociMenuModalita_semaphore = new Semaphore("LoginBean.vociMenuModalita");
	
	private String soggettoPddMonitor = null;
	private Boolean visualizzaMenuSoggetto = null;
	private Boolean visualizzaSezioneSoggetto = null;
	private List<MenuModalitaItem> vociMenuSoggetto = null;
	private Semaphore vociMenuSoggetto_semaphore = new Semaphore("LoginBean.vociMenuSoggetto");
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
			this.showLogout = PddMonitorProperties.getInstance(this.log).isMostraButtonLogout();
			this.logoutDestinazione = PddMonitorProperties.getInstance(this.log).getLogoutUrlDestinazione();

			this.setLogoHeaderImage(PddMonitorProperties.getInstance(this.log).getLogoHeaderImage());
			this.setLogoHeaderLink(PddMonitorProperties.getInstance(this.log).getLogoHeaderLink());
			this.setLogoHeaderTitolo(PddMonitorProperties.getInstance(this.log).getLogoHeaderTitolo()); 
			this.setTitle(PddMonitorProperties.getInstance(this.log).getPddMonitorTitle());
			this.setShowExtendedInfo(PddMonitorProperties.getInstance(this.log).visualizzaPaginaAboutExtendedInfo());
			
			String utentiPasswordConfig = PddMonitorProperties.getInstance(this.log).getUtentiPassword();
			
			if(utentiPasswordConfig!=null){
				this.passwordVerifier = new PasswordVerifier(utentiPasswordConfig);
				if(this.passwordVerifier.existsRestrictionUpdate()==false){
					this.passwordVerifier = null;
				}
			}
			
			this.checkPasswordExpire = PddMonitorProperties.getInstance(this.log).isCheckPasswordExpire(this.passwordVerifier);
			
			this.salvaModificheProfiloSuDB = PddMonitorProperties.getInstance(this.log).isModificaProfiloUtenteDaLinkAggiornaDB();

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
			this.userToUpdate = null;

			if(null == this.getUsername() && this.getPwd() == null){		
				return "login";
			}

			try{
				this.log.info("Verifico le credenziali per l'utente ["+this.getUsername()+"]");

				if(this.getLoginDao().login(this.getUsername(),this.getPwd())){
					//			ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
					//			HttpSession session = (HttpSession) ec.getSession(true);
					//			session.setAttribute("logged", true);
					
					// controllo validita' password
					UserDetailsBean loadUserByUsername = this.getLoginDao().loadUserByUsername(this.getUsername());
					
					if(this.passwordVerifier != null && this.checkPasswordExpire) {
						User user = loadUserByUsername.getUtente();
						if(user.isCheckLastUpdatePassword()) {
							StringBuilder bfMotivazioneErrore = new StringBuilder(); 
							if(this.passwordVerifier.isPasswordExpire(user.getLastUpdatePassword(), bfMotivazioneErrore)) {
								MessageUtils.addErrorMsg(bfMotivazioneErrore.toString());
								this.userToUpdate = this.getUsername();
								this.nuovoTokenCsrfListener(null); // genero un token csrf per l'operazione
								return "utentePasswordChange";
							}
						}
					}
					
					this.setLoggedIn(true);
					this.setLoggedUser(loadUserByUsername);
					this.setDettaglioUtente(this.getLoggedUser().getUtente());
					this.setModalita(this.getLoggedUser().getUtente().getProtocolloSelezionatoPddMonitor());
					this.setSoggettoPddMonitor(this.getLoggedUser().getUtente().getSoggettoSelezionatoPddMonitor());
					this.setvInfo(this.getLoginDao().readVersionInfo());
					this.log.info("Utente ["+this.getUsername()+"] autenticato con successo");
					return LoginBean.getOutcomeLoginSuccess(this.getLoggedUser().getUtente());
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
				if(this.getLoggedUser() != null){
					this.setDettaglioUtente(this.getLoggedUser().getUtente());
					this.setModalita(this.getLoggedUser().getUtente().getProtocolloSelezionatoPddMonitor()); 
					this.setSoggettoPddMonitor(this.getLoggedUser().getUtente().getSoggettoSelezionatoPddMonitor());
					this.setLoggedIn(true);
					this.setvInfo(this.getLoginDao().readVersionInfo());
					this.log.info("Utente ["+this.getUsername()+"] autenticato con successo");
					return LoginBean.getOutcomeLoginSuccess(this.getLoggedUser().getUtente());
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
		
		try {
			ApplicationBean.getInstance().resetAllCache();
		}catch(Throwable t) {}
		
		try{
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getExternalContext().getSessionMap().put(org.openspcoop2.web.monitor.core.bean.AbstractLoginBean.LOGIN_BEAN_SESSION_ATTRIBUTE_NAME, null);
			HttpSession session = (HttpSession)fc.getExternalContext().getSession(false);
			session.setAttribute(org.openspcoop2.web.monitor.core.bean.AbstractLoginBean.LOGIN_BEAN_SESSION_ATTRIBUTE_NAME, null); 
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
				return "loginSuccess";
			}
			else if(homePage.equals(Costanti.VALUE_PARAMETRO_UTENTI_HOME_PAGE_MONITORAGGIO_TRANSAZIONI)) {
				return "transazioniStart";
			}
		}
		
		return "transazioniStart";
		
	}


	public Soggetto getSoggetto(IdSoggetto idSog){
		String key = idSog.getTipo() + "/" + idSog.getNome();
		if(!this.getMapSoggetti().containsKey(key)) {
			this.getMapSoggetti().put(key, this.loginDao.getSoggetto(idSog));
		}

		return this.getMapSoggetti().get(key);
	}

	public List<String> getIdentificativiPorta(User user){
		List<String> lst = new ArrayList<String>();

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
			int v1 = 0; //(admin || operatore) ? 1 : 0;

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
			}catch(Exception e) {
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

				this.visualizzaSezioneModalita = listaNomiProtocolli.size() > 0;

			}catch(Exception e) {
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

			}catch(Exception e) {
				this.visualizzaMenuModalita = false;
			}
		}
		return this.visualizzaMenuModalita;
	}

	public List<String> listaProtocolliDisponibilePerUtentePddMonitor() throws Exception {
		ProtocolFactoryManager pfManager = org.openspcoop2.protocol.engine.ProtocolFactoryManager.getInstance();
		MapReader<String,IProtocolFactory<?>> protocolFactories = pfManager.getProtocolFactories();	
		List<String> listaNomiProtocolli = Utility.getProtocolli(this.getUtente(), pfManager, protocolFactories, true);
		return listaNomiProtocolli;
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
		
		
		return "modalita";
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

	public void setLabelModalita(String labelModalita) {
	}

	public List<MenuModalitaItem> getVociMenuModalita() {
		
		this.vociMenuModalita_semaphore.acquireThrowRuntime("getVociMenuModalita");
		try {
			
			this.vociMenuModalita = new ArrayList<MenuModalitaItem>();
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
						// String iconProt = this.modalita == null ? Costanti.ICONA_MENU_UTENTE_UNCHECKED : (protocolloDisponibile.equals(this.modalita) ? Costanti.ICONA_MENU_UTENTE_CHECKED : Costanti.ICONA_MENU_UTENTE_UNCHECKED);
						
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
	
			}catch(Throwable e) {
				this.vociMenuModalita = new ArrayList<MenuModalitaItem>();
			}
		
			return this.vociMenuModalita;
		}finally {
			this.vociMenuModalita_semaphore.release("getVociMenuModalita");
		}
	}
	
	public int getWidthVociMenuModalita() {
		
		synchronized (this.vociMenuModalita_semaphore) {
		
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

	public void setVociMenuModalita(List<MenuModalitaItem> vociMenuModalita) {
	}	

	public List<String> getProtocolliSelezionati() {
		List<String> protocolliList = new ArrayList<String>();
		try{
			User utente = this.getUtente();
			
			if(utente.getProtocolloSelezionatoPddMonitor()!=null) {
				protocolliList.add(utente.getProtocolloSelezionatoPddMonitor());
				return protocolliList;
			}
			
			if(utente.getProtocolliSupportati()!=null && utente.getProtocolliSupportati().size()>0) {
				return utente.getProtocolliSupportati();
			}
			
			
			return this.listaProtocolliDisponibilePerUtentePddMonitor();

		}catch (Exception e) {
			this.log.error(e.getMessage(),e);
			protocolliList = new ArrayList<String>();
			return protocolliList;
		}
	}
	
	public List<InformazioniProtocollo> getListaInformazioniProtocollo() {
		List<InformazioniProtocollo> listaInformazioniProtocollo = new ArrayList<InformazioniProtocollo>();
		
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

	public void setListaInformazioniProtocollo(List<InformazioniProtocollo> listaInformazioniProtocollo) {
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
			// Provocava un bug sul filtro per ruolo operatore, poichè risultava sempre assegnato il soggetto locale, e quindi il filtro per API di un soggetto diverso non comparivano nelle tendine
//			try {
//				List<Soggetto> listaSoggetti = this.listaSoggettiDisponibilePerUtentePddMonitor();
//
//				if(listaSoggetti.size() == 1) {
//					IDSoggetto idSoggetto = new IDSoggetto(listaSoggetti.get(0).getTipoSoggetto(), listaSoggetti.get(0).getNomeSoggetto()); 
//					return  idSoggetto.toString();
//				}
//			}catch(Exception e) {
//				return Costanti.VALUE_PARAMETRO_MODALITA_ALL;
//			}
			
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

				this.visualizzaSezioneSoggetto = listaSoggetti.size() > 0;

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

			}catch(Exception e) {
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

			}catch(Exception e) {
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
		} catch (Exception e) {
			String errorMessage = "Si e' verificato un errore durante il cambio del soggetto, si prega di riprovare piu' tardi.";
			this.log.error(e.getMessage(),e);
			MessageUtils.addErrorMsg(errorMessage);
		}
		
		this.visualizzaSezioneSoggetto = null;
		this.visualizzaMenuSoggetto = null;	
		this.visualizzaLinkSelezioneSoggetto = null;
		
		return "soggettoPddMonitor";
	}
	
	public void soggettoAutocompleteSelected(ActionEvent ae) {}
	
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
	
	public String getLabelSoggettoNormalized() throws Exception {
		return _getLabelSoggettoNormalized(true);
	}
	
	public String getLabelSoggettoNormalizedSenzaPrefisso() throws Exception {
		return _getLabelSoggettoNormalized(false);
	}
	
	public String _getLabelSoggettoNormalized(boolean addPrefix) throws Exception {
		String label = _getLabelSoggetto(addPrefix);
		
//		if(label.length() > PddMonitorProperties.getInstance(this.log).getLunghezzaMassimaLabelButtonSoggettiOperativiMenuUtente()) {
//			return Utility.normalizeLabel(label, PddMonitorProperties.getInstance(this.log).getLunghezzaMassimaLabelButtonSoggettiOperativiMenuUtente());
//		}
		
		if(label.length() > PddMonitorProperties.getInstance(this.log).getLunghezzaMassimaLabelSelectListSoggettiOperativiMenuUtente()) {
			return Utility.normalizeLabel(label, PddMonitorProperties.getInstance(this.log).getLunghezzaMassimaLabelSelectListSoggettiOperativiMenuUtente());
		}
		
		return null;
	}

	public String getLabelSoggetto() throws ProtocolException {
		return _getLabelSoggetto(true);
	}
	
	public String getLabelSoggettoSenzaPrefisso() throws ProtocolException {
		return _getLabelSoggetto(false);
	}

	private String _getLabelSoggetto(boolean addPrefix) {
		// prelevo l'eventuale protocollo selezionato
		String labelSelezionato = "";
		try {
			if(this.soggettoPddMonitor == null) {
				try {
					List<Soggetto> listaNomiSoggetti = this.listaSoggettiDisponibilePerUtentePddMonitor();

					if(listaNomiSoggetti.size() == 1) {
						IDSoggetto idSoggetto = new IDSoggetto(listaNomiSoggetti.get(0).getTipoSoggetto(), listaNomiSoggetti.get(0).getNomeSoggetto()); 
						labelSelezionato = NamingUtils.getLabelSoggetto(idSoggetto);  
					} else {
						labelSelezionato = Costanti.LABEL_PARAMETRO_MODALITA_ALL;
					}
				}catch(Exception e) {
					labelSelezionato = Costanti.LABEL_PARAMETRO_MODALITA_ALL;
				}
				
			} else {
				String tipoSoggettoOperativoSelezionato = Utility.parseTipoSoggetto(this.soggettoPddMonitor);
				String nomeSoggettoOperativoSelezionato = Utility.parseNomeSoggetto(this.soggettoPddMonitor);
				IDSoggetto idSoggetto = new IDSoggetto(tipoSoggettoOperativoSelezionato, nomeSoggettoOperativoSelezionato);
				labelSelezionato = NamingUtils.getLabelSoggetto(idSoggetto);
			}
		} catch (Exception e) {
			this.log.error(e.getMessage(), e);
		}
		return addPrefix ? MessageFormat.format(Costanti.LABEL_MENU_SOGGETTO_CORRENTE_WITH_PARAM, labelSelezionato) : labelSelezionato;
	}

	public void setLabelSoggetto(String labelSoggetto) {
	}
	
	public void setLabelSoggettoSenzaPrefisso(String labelSoggetto) {
	}
	
	public List<Soggetto> listaSoggettiDisponibilePerUtentePddMonitor() throws Exception {
		if(this.listaSoggettiDisponibiliUtente == null) {
			this.listaSoggettiDisponibiliUtente = _listaSoggettiDisponibilePerUtentePddMonitor();
		}
		
		return this.listaSoggettiDisponibiliUtente;
	}
	
	private List<Soggetto> _listaSoggettiDisponibilePerUtentePddMonitor() throws Exception {
		User utente = this.getUtente();
		List<String> protocolliDispondibili = this.listaProtocolliDisponibilePerUtentePddMonitor();
		String protocolloSelezionato = utente.getProtocolloSelezionatoPddMonitor();
		if(protocolliDispondibili.size()==1) {
			protocolloSelezionato = protocolliDispondibili.get(0); // forzo
		}
		return listaSoggettiDisponibiliPerProtocollo(this.getLoggedUser(), protocolloSelezionato);
	}

	public static List<Soggetto> listaSoggettiDisponibiliPerProtocollo(UserDetailsBean userDetailsBean, String protocolloSelezionato) throws Exception {
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
	
	public List<org.openspcoop2.web.monitor.core.bean.SelectItem> soggettoPddMonitorAutoComplete(Object val) throws Exception{
		List<org.openspcoop2.web.monitor.core.bean.SelectItem> listaGruppi = new ArrayList<org.openspcoop2.web.monitor.core.bean.SelectItem>();
//		List<SelectItem> listaGruppiTmp = new ArrayList<>();
		if(val==null || StringUtils.isEmpty((String)val)) {
			
		}else{
			List<MenuModalitaItem>  vociMenuSoggetto  = this.getVociMenuSoggetto();
			
			for (MenuModalitaItem menuModalitaItem : vociMenuSoggetto) {
				if(menuModalitaItem.getLabel().toUpperCase().contains(((String)val).toUpperCase())) {
					listaGruppi.add(new org.openspcoop2.web.monitor.core.bean.SelectItem(menuModalitaItem.getValue(), menuModalitaItem.getLabel()));
				}
			}
			
//			listaGruppi.add(0, new org.openspcoop2.web.monitor.core.bean.SelectItem("--", "--"));
			
//			for (SelectItem selectItem : listaGruppiTmp) {
//				String label = selectItem.getLabel();
//				String value = (String) selectItem.getValue();
//				
//				org.openspcoop2.web.monitor.core.bean.SelectItem newItem = new org.openspcoop2.web.monitor.core.bean.SelectItem(value, label);
//				listaGruppi.add(newItem);
//			}
		}
		return listaGruppi;
	}

	public List<MenuModalitaItem> getVociMenuSoggetto() {
		
		this.vociMenuSoggetto_semaphore.acquireThrowRuntime("getVociMenuSoggetto"); 
		try{
		
			this.vociMenuSoggetto = new ArrayList<MenuModalitaItem>();
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
	
					//Integer numeroMassimoSoggettiSelectListSoggettiOperatiti = PddMonitorProperties.getInstance(this.log).getNumeroMassimoSoggettiOperativiMenuUtente();
					
	//				if(soggettiOperativi.size() < numeroMassimoSoggettiSelectListSoggettiOperatiti) {
						
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
						Collections.sort(listaLabel, new Comparator<String>() {
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
	//				} 		
				}
	
			}catch(Throwable e) {
				this.vociMenuSoggetto = new ArrayList<MenuModalitaItem>();
			}
			
			return this.vociMenuSoggetto;
			
		}finally {
			this.vociMenuSoggetto_semaphore.release("getVociMenuSoggetto"); 
		}
	}
	
	public int getWidthVociMenuSoggetto() {
		
		this.vociMenuSoggetto_semaphore.acquireThrowRuntime("getWidthVociMenuSoggetto"); 
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
			this.vociMenuSoggetto_semaphore.release("getWidthVociMenuSoggetto"); 
		}
	}

	public void setVociMenuSoggetto(List<MenuModalitaItem> vociMenuModalita) {
	}
	
	public boolean isShowFiltroSoggettoLocale(){
		if(this.showFiltroSoggettoLocale == null) {
			this.showFiltroSoggettoLocale =  _isShowFiltroSoggettoLocale();
		}
		
		return this.showFiltroSoggettoLocale;
	}

	private boolean _isShowFiltroSoggettoLocale() {
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
		} catch (Exception e) {
			this.log.error("Si e' verificato un errore durante il caricamento della lista protocolli: " + e.getMessage(), e);
		}
		return true;
	}
	
	
	public IVersionInfo getvInfo() {
		return this.vInfo;
	}

	public void setvInfo(IVersionInfo vInfo) {
		this.vInfo = vInfo;
		if(this.vInfo!=null) {
			if(!StringUtils.isEmpty(this.vInfo.getErrorTitleSuffix())) {
				String titolo = this.getTitle();
				if(!this.vInfo.getErrorTitleSuffix().startsWith(" ")) {
					titolo = titolo + " ";
				}
				titolo = titolo + this.vInfo.getErrorTitleSuffix();
				this.setTitle(titolo);
			}
			else if(!StringUtils.isEmpty(this.vInfo.getWarningTitleSuffix())) {
				String titolo = this.getTitle();
				if(!this.vInfo.getWarningTitleSuffix().startsWith(" ")) {
					titolo = titolo + " ";
				}
				titolo = titolo + this.vInfo.getWarningTitleSuffix();
				this.setTitle(titolo);
			}
		}
	}
	public List<String> getListaNomiGruppi(){
		if(this.listaNomiGruppi == null) {
		try {
			this.listaNomiGruppi = DynamicPdDBeanUtils.getInstance(LoggerManager.getPddMonitorCoreLogger()).getListaNomiGruppi();
		} catch (Exception e) {
			this.listaNomiGruppi =new ArrayList<String>();
		}
		} 
		return this.listaNomiGruppi;
	}
	
	public boolean isAmministratore() {
		return this.getLoggedUser().isAdmin();
	}
	
	public String ricaricaUtenteDopoCambioPasswordScaduta() {
	
		this.log.info("Ricarico Profilo per l'utente ["+this.getUsername()+"]");
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
				this.log.info("Profilo Utente ["+this.getUsername()+"] caricato con successo");
				return LoginBean.getOutcomeLoginSuccess(this.getLoggedUser().getUtente());
			}
			return "login";
		} catch (ServiceException e) {
			this.loginErrorMessage = "Si e' verificato un errore durante il caricamento del profilo utente, impossibile autenticare l'utente "+this.getUsername()+"."; 
			this.log.error(this.loginErrorMessage);
			return "loginError";
		} catch (NotFoundException e) {
			this.loginErrorMessage = "Il sistema non riesce a caricare il profilo utente "+this.getUsername()+": Utente non registrato.";
			this.log.debug(this.loginErrorMessage);
			return "login";
		} catch (UserInvalidException e) {
			this.loginErrorMessage = "Si e' verificato un errore durante il caricamento del profilo utente, impossibile autenticare l'utente "+this.getUsername()+": " + e.getMessage();
			this.log.debug(this.loginErrorMessage);
			return "loginUserInvalid";
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
		this.log.debug("Letto Token CSRF: ["+tokenCSRF+"]"); 
		return tokenCSRF;
	}
	
	public void nuovoTokenCsrfListener(ActionEvent ae) {
		FacesContext fc = FacesContext.getCurrentInstance();
		ExternalContext extCtx = fc.getExternalContext();
		HttpSession session = (HttpSession)extCtx.getSession(false);
		String nuovoTokenCSRF = CsrfFilter.generaESalvaTokenCSRF(session);
		this.log.debug("Generato Nuovo Token CSRF: ["+nuovoTokenCSRF+"]");
	}

	public void setCsrf(String csrf) {}
}
