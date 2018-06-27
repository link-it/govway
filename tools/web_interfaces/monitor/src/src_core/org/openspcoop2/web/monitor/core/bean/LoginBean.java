/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 * 
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it).
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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.commons.search.IdSoggetto;
import org.openspcoop2.core.commons.search.Soggetto;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.engine.utils.NamingUtils;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.utils.resources.MapReader;
import org.openspcoop2.web.lib.users.dao.User;
import org.openspcoop2.web.monitor.core.constants.Costanti;
import org.openspcoop2.web.monitor.core.core.PddMonitorProperties;
import org.openspcoop2.web.monitor.core.core.Utility;
import org.openspcoop2.web.monitor.core.dao.DBLoginDAO;
import org.openspcoop2.web.monitor.core.exception.UserInvalidException;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;
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
	private List<MenuModalitaItem> vociMenuModalita = null;

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
			this.setTitle(PddMonitorProperties.getInstance(this.log).getPddMonitorTitle());
			this.setShowExtendedInfo(PddMonitorProperties.getInstance(this.log).visualizzaPaginaAboutExtendedInfo());

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
					this.setDettaglioUtente(this.getLoggedUser().getUtente());
					this.setModalita(this.getLoggedUser().getUtente().getProtocolloSelezionatoPddMonitor());
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
				if(this.getLoggedUser() != null){
					this.setDettaglioUtente(this.getLoggedUser().getUtente());
					this.setModalita(this.getLoggedUser().getUtente().getProtocolloSelezionatoPddMonitor()); 
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
			boolean admin = this.getLoggedUser().isAdmin();
			boolean operatore = this.getLoggedUser().isOperatore();

			// visualizzazione icona stato
			int v1 = (admin || operatore) ? 1 : 0;

			//2 visualizzazione modalita'
			int v2 = this.isVisualizzaMenuModalita() ? 1 : 0;

			// numero colonne = profiloutente+modalita+statopdd
			this.colonneUserInfo = 1 + v1 + v2;
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
		if(this.modalita == null)
			return Costanti.VALUE_PARAMETRO_MODALITA_ALL;
		
		return this.modalita;
	}

	public void setModalita(String modalita) {
		this.modalita = modalita;
		
		if(Costanti.VALUE_PARAMETRO_MODALITA_ALL.equals(this.modalita))
			this.modalita = null;
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

	public List<String> listaProtocolliDisponibilePerUtentePddMonitor() throws ProtocolException {
		String tipoNomeSoggettoLocale = null;
		if (this.getUtente().getSoggetti().size() == 1) {
			IDSoggetto s = this.getUtente().getSoggetti().get(0);
			tipoNomeSoggettoLocale = s.getTipo() + "/" + s.getNome();
		}
		List<Soggetto> listaSoggettiGestione = Utility.getSoggettiGestione(this.getUtente(),tipoNomeSoggettoLocale);

		ProtocolFactoryManager pfManager = org.openspcoop2.protocol.engine.ProtocolFactoryManager.getInstance();
		MapReader<String,IProtocolFactory<?>> protocolFactories = pfManager.getProtocolFactories();	

		List<String> listaNomiProtocolli = Utility.getListaProtocolli(this.getUtente(),listaSoggettiGestione, pfManager, protocolFactories);
		return listaNomiProtocolli;
	}

	public void setVisualizzaMenuModalita(boolean visualizzaMenuModalita) {
		this.visualizzaMenuModalita = visualizzaMenuModalita;
	}

	public String cambiaModalita() {
		this.getLoggedUser().getUtente().setProtocolloSelezionatoPddMonitor(this.modalita);
		
		try {
			this.loginDao.salvaModalita(this.getLoggedUser().getUtente());
		} catch (NotFoundException | ServiceException e) {
			String errorMessage = "Si e' verificato un errore durante il cambio della modalita', si prega di riprovare piu' tardi.";
			this.log.error(e.getMessage(),e);
			MessageUtils.addErrorMsg(errorMessage);
		}
		
		return "modalita";
	}

	public String getLabelModalita() throws ProtocolException {
		// prelevo l'eventuale protocollo selezionato
		String labelSelezionato = "";
		try {
			labelSelezionato = this.modalita == null ? Costanti.LABEL_PARAMETRO_MODALITA_ALL : NamingUtils.getLabelProtocollo(this.modalita);
		} catch (Exception e) {
			this.log.error(e.getMessage(), e);
		}
		return MessageFormat.format(Costanti.LABEL_MENU_MODALITA_CORRENTE_WITH_PARAM, labelSelezionato); 
	}

	public void setLabelModalita(String labelModalita) {
	}

	public List<MenuModalitaItem> getVociMenuModalita() {
		this.vociMenuModalita = new ArrayList<MenuModalitaItem>();
		try {
			String tipoNomeSoggettoLocale = null;
			if (this.getUtente().getSoggetti().size() == 1) {
				IDSoggetto s = this.getUtente().getSoggetti().get(0);
				tipoNomeSoggettoLocale = s.getTipo() + "/" + s.getNome();
			}
			List<Soggetto> listaSoggettiGestione = Utility.getSoggettiGestione(this.getUtente(),tipoNomeSoggettoLocale);

			ProtocolFactoryManager pfManager = ProtocolFactoryManager.getInstance();
			MapReader<String,IProtocolFactory<?>> protocolFactories = pfManager.getProtocolFactories();	

			List<String> listaNomiProtocolli = Utility.getListaProtocolli(this.getUtente(),listaSoggettiGestione, pfManager, protocolFactories);

			if(listaNomiProtocolli != null && listaNomiProtocolli.size() > 1) {
				// prelevo l'eventuale protocollo selezionato
				// popolo la tendina con i protocolli disponibili
				for (String protocolloDisponibile : listaNomiProtocolli) {
					String iconProt = this.modalita == null ? Costanti.ICONA_MENU_UTENTE_UNCHECKED : (protocolloDisponibile.equals(this.modalita) ? Costanti.ICONA_MENU_UTENTE_CHECKED : Costanti.ICONA_MENU_UTENTE_UNCHECKED);
					
					MenuModalitaItem menuItem = new MenuModalitaItem(protocolloDisponibile, NamingUtils.getLabelProtocollo(protocolloDisponibile), iconProt); 
					this.vociMenuModalita.add(menuItem);
				}

				// seleziona tutti
				MenuModalitaItem menuItem = new MenuModalitaItem(Costanti.VALUE_PARAMETRO_MODALITA_ALL, Costanti.LABEL_PARAMETRO_MODALITA_ALL, (this.modalita == null) ? Costanti.ICONA_MENU_UTENTE_CHECKED : Costanti.ICONA_MENU_UTENTE_UNCHECKED);
				this.vociMenuModalita.add(menuItem);
			}

		}catch(Exception e) {
			this.vociMenuModalita = new ArrayList<MenuModalitaItem>();
		}
		
		return this.vociMenuModalita;
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
				this.log.error("Impossibilie caricare le informazioni del protocollo ["+protocollo+"]: " + e.getMessage(),e);
			}
		}
				
		return listaInformazioniProtocollo;
	}

	public void setListaInformazioniProtocollo(List<InformazioniProtocollo> listaInformazioniProtocollo) {
	}

}
