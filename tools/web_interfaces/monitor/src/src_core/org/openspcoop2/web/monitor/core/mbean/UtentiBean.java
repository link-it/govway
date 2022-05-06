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
package org.openspcoop2.web.monitor.core.mbean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.commons.search.Soggetto;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.engine.utils.NamingUtils;
import org.openspcoop2.utils.crypt.CryptConfig;
import org.openspcoop2.utils.crypt.CryptFactory;
import org.openspcoop2.utils.crypt.ICrypt;
import org.openspcoop2.utils.crypt.PasswordVerifier;
import org.openspcoop2.web.lib.users.dao.User;
import org.openspcoop2.web.lib.users.dao.UserPassword;
import org.openspcoop2.web.monitor.core.bean.LoginBean;
import org.openspcoop2.web.monitor.core.bean.UserDetailsBean;
import org.openspcoop2.web.monitor.core.constants.Costanti;
import org.openspcoop2.web.monitor.core.core.PddMonitorProperties;
import org.openspcoop2.web.monitor.core.core.Utility;
import org.openspcoop2.web.monitor.core.dao.IService;
import org.openspcoop2.web.monitor.core.dao.UserService;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;
import org.openspcoop2.web.monitor.core.utils.MessageUtils;
import org.slf4j.Logger;

/**
 * UtentiBean
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class UtentiBean extends PdDBaseBean<UtentiBean, String, IService<User, String>> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L; 
	private static Logger log =  LoggerManager.getPddMonitorCoreLogger();

	private User user;
	private String confermaPassword;
	private String vecchiaPassword;
	private boolean showFormCambiaPassword = false;
	
	private String nuovaPassword;
	private String userToUpdate;

	private Boolean showServizi = false;

	private String id;

	private boolean gestionePassword = true;
	private ICrypt passwordManager;
	private ICrypt passwordManager_backwardCompatibility;

	private PasswordVerifier passwordVerifier = null;
	
	private boolean salvaModificheProfiloInSessione = false;
	
	public UtentiBean() {
		this.service = new UserService();

		try {
			PddMonitorProperties pddMonitorProperties = PddMonitorProperties.getInstance(log);
			
			String utentiPasswordConfig = pddMonitorProperties.getUtentiPassword();
			
			this.gestionePassword = pddMonitorProperties.isGestionePasswordUtentiAttiva();
			if(utentiPasswordConfig!=null){
				this.passwordVerifier = new PasswordVerifier(utentiPasswordConfig);
				if(this.passwordVerifier.existsRestrictionUpdate()==false){
					this.passwordVerifier = null;
				}
			}
			CryptConfig config = new CryptConfig(utentiPasswordConfig);
			this.passwordManager = CryptFactory.getCrypt(log, config);
			if(config.isBackwardCompatibility()) {
				this.passwordManager_backwardCompatibility = CryptFactory.getOldMD5Crypt(log);
			}
			
			this.salvaModificheProfiloInSessione = pddMonitorProperties.isModificaProfiloUtenteDaFormAggiornaSessione();
			
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	public String getNotePassword(){
		if(this.passwordVerifier!=null){
			return this.passwordVerifier.helpUpdate("<BR/>");
		}
		return null;
	}

	public void setId(String id) {
		this.id = id;
		this.user = null;

		if (this.id != null){
			this.user = this.service.findById(this.id);
		}
	}

	public String getId() {
		return this.id;
	}

	public User getUser() {
		if (this.user == null && this.id == null){
			this.user = new User();

			if(!this.gestionePassword) {
				this.user.setPassword("*");
				this.setConfermaPassword("*");
			} else {
				this.user.setPassword(null);
				this.setConfermaPassword(null);	
			}
		}else if (this.user == null && this.id != null)
			this.user = this.service.findById(this.id);

		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getConfermaPassword() {
		return this.confermaPassword;
	}

	public void setConfermaPassword(String confermaPassword) {
		this.confermaPassword = confermaPassword;
	}

	public void editListener(ActionEvent ae) {
		this.user = this.service.findById(this.user.getLogin());
	}

	public String salvaProfilo() {
		
		User userToUpdate = this.service.findById(this.user.getLogin());
		
		String modalitaDefaultUtente = this.user.getProtocolloSelezionatoPddMonitor();
		userToUpdate.setProtocolloSelezionatoPddMonitor((modalitaDefaultUtente != null && !modalitaDefaultUtente.equals(Costanti.VALUE_PARAMETRO_MODALITA_ALL)) ? modalitaDefaultUtente : null);
		String soggettoDefaultUtente = this.user.getSoggettoSelezionatoPddMonitor();
		userToUpdate.setSoggettoSelezionatoPddMonitor((soggettoDefaultUtente != null && !soggettoDefaultUtente.equals(Costanti.VALUE_PARAMETRO_MODALITA_ALL)) ? soggettoDefaultUtente : null);
		
		try {
			((UserService)this.service).salvaModalita(userToUpdate.getLogin(), userToUpdate.getProtocolloSelezionatoPddMonitor());
			((UserService)this.service).salvaSoggettoPddMonitor(userToUpdate.getLogin(), userToUpdate.getSoggettoSelezionatoPddMonitor());
			
			if(this.salvaModificheProfiloInSessione) {
				Utility.getLoggedUser().getUtente().setProtocolloSelezionatoPddMonitor(userToUpdate.getProtocolloSelezionatoPddMonitor());
				Utility.getLoggedUser().getUtente().setSoggettoSelezionatoPddMonitor(userToUpdate.getSoggettoSelezionatoPddMonitor());
			}
			
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Profilo Utente modificato correttamente"));
		} catch (Exception e) {
			log.error("Salvataggio Profilo Utente non riuscito",e);
			MessageUtils.addErrorMsg("Salvataggio Profilo Utente non riuscito");
			return null;
		}
		
		// salvataggio della password solo se e' spuntata la check box nella pagina
		if(this.showFormCambiaPassword) {
			return this.cambioPassword();
		}
		
		return null;
	}
	
	public String cambioPassword() {

		try {
			if(StringUtils.isEmpty(this.user.getPassword())){
				MessageUtils.addErrorMsg("Il campo Nuova non pu\u00F2 essere vuoto");
				return null;
			}

			if(StringUtils.isEmpty(this.confermaPassword)){
				MessageUtils.addErrorMsg("Il campo Conferma Nuova non pu\u00F2 essere vuoto");
				return null;
			}

			if(this.isShowVecchiaPassword()) {
				if(StringUtils.isEmpty(this.vecchiaPassword)){
					MessageUtils.addErrorMsg("Il campo Vecchia non pu\u00F2 essere vuoto");
					return null;
				}

				// check vecchia password dal db [TODO]
				User findById = this.service.findById(this.user.getLogin());
				boolean trovato = this.passwordManager.check(this.vecchiaPassword, findById.getPassword());
				if(!trovato && this.passwordManager_backwardCompatibility!=null) {
					trovato = this.passwordManager_backwardCompatibility.check(this.vecchiaPassword, findById.getPassword());
				}
				if (!trovato) {
					MessageUtils.addErrorMsg("La vecchia password indicata non \u00E8 corretta");
					return null;
				}

				// controlla che la nuova password non coincida con la vecchia
				if (StringUtils.equals(this.vecchiaPassword, this.user.getPassword())) {
					// errore
					MessageUtils.addErrorMsg("La nuova password non pu\u00F2 essere uguale alla vecchia");
					return null;
				}
			}

			// controlla pwd
			if (!StringUtils.equals(this.confermaPassword, this.user.getPassword())) {
				// errore
				MessageUtils.addErrorMsg("Le password inserite nei campi Nuova e Conferma Nuova non corrispondono");
				return null;
			}

			boolean updateStoricoPassword = false;
			if(this.passwordVerifier!=null){
				StringBuilder motivazioneErrore = new StringBuilder();
				if(this.passwordVerifier.validate(this.user.getLogin(), this.user.getPassword(), motivazioneErrore)==false){
					// errore
					MessageUtils.addErrorMsg(motivazioneErrore.toString());
					return null;
				}
				
				if(this.passwordVerifier.isHistory()) {
					List<UserPassword> precedentiPassword = this.user.getPrecedentiPassword();
					User findById = this.service.findById(this.user.getLogin());
					
					// se la lista storico e' vuota controllo la password precedente salvata nel bean
					if(precedentiPassword == null || precedentiPassword.isEmpty()) {
						boolean trovato = this.passwordManager.check(this.user.getPassword(), findById.getPassword());
						if(!trovato && this.passwordManager_backwardCompatibility!=null) {
							trovato = this.passwordManager_backwardCompatibility.check(this.user.getPassword(), findById.getPassword());
						}
						if (trovato) {
							MessageUtils.addErrorMsg("La password scelta non deve corrispondere ad una precedente password");
							return null;
						}
					}
					
					for (UserPassword userPassword : precedentiPassword) {
						boolean trovato = this.passwordManager.check(this.user.getPassword(), userPassword.getPassword());
						if(!trovato && this.passwordManager_backwardCompatibility!=null) {
							trovato = this.passwordManager_backwardCompatibility.check(this.user.getPassword(), userPassword.getPassword());
						}
						if (trovato) {
							MessageUtils.addErrorMsg("La password scelta non deve corrispondere ad una precedente password");
							return null;
						}
					}
					
					UserPassword userPassword = new UserPassword();
					userPassword.setDatePassword(findById.getLastUpdatePassword());
					userPassword.setPassword(findById.getPassword());
					this.user.getPrecedentiPassword().add(userPassword );
					updateStoricoPassword = true;
				}
			}
			
			// aggiorno data ultima modifica
			this.user.setLastUpdatePassword(new Date());

			// cript pwd
			String newPassword = this.passwordManager.crypt(this.user.getPassword());
			
			if(updateStoricoPassword) {
				((UserService)this.service).savePasswordStorico(this.user.getId(), this.user.getLogin(), newPassword, this.user.getLastUpdatePassword(), this.user.getPrecedentiPassword());
			} else {
				((UserService)this.service).savePassword(this.user.getId(), this.user.getLogin(), newPassword, this.user.getLastUpdatePassword());
			}

			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage("Password modificata correttamente"));

			this.showFormCambiaPassword = false;
		} catch (Exception e) {
			log.error("Cambio Password non riuscito",e);
			MessageUtils.addErrorMsg("Cambio Password non riuscito");
		}

		return null;
	}
	
	public String cambioPasswordScaduta() {

		try {
			User findById = this.service.findById(this.userToUpdate);
			
			if(StringUtils.isEmpty(this.nuovaPassword)){
				MessageUtils.addErrorMsg("Il campo Nuova non pu\u00F2 essere vuoto");
				return null;
			}

			if(StringUtils.isEmpty(this.confermaPassword)){
				MessageUtils.addErrorMsg("Il campo Conferma Nuova non pu\u00F2 essere vuoto");
				return null;
			}
			
			// Controllo che non ci siano spazi nei campi di testo
			if ((this.nuovaPassword.indexOf(" ") != -1) || (this.confermaPassword.indexOf(" ") != -1)) {
				MessageUtils.addErrorMsg("Non inserire spazi nei campi di testo");
				return null;
			}

			if(StringUtils.isEmpty(this.vecchiaPassword)){
				MessageUtils.addErrorMsg("La vecchia password indicata non \u00E8 corretta");
				return null;
			}
			
			// Controllo che non ci siano spazi nei campi di testo
			if ((this.vecchiaPassword.indexOf(" ") != -1)) {
				MessageUtils.addErrorMsg("Non inserire spazi nei campi di testo");
				return null;
			}

			// check vecchia password dal db [TODO]
			boolean trovato = this.passwordManager.check(this.vecchiaPassword, findById.getPassword());
			if(!trovato && this.passwordManager_backwardCompatibility!=null) {
				trovato = this.passwordManager_backwardCompatibility.check(this.vecchiaPassword, findById.getPassword());
			}
			if (!trovato) {
				MessageUtils.addErrorMsg("La vecchia password indicata non \u00E8 corretta");
				return null;
			}
			
			// controlla che la nuova password non coincida con la vecchia
			if (StringUtils.equals(this.vecchiaPassword, this.nuovaPassword)) {
				// errore
				MessageUtils.addErrorMsg("La nuova password deve essere differente dalla vecchia");
				return null;
			}

			// controlla pwd
			if (!StringUtils.equals(this.confermaPassword, this.nuovaPassword)) {
				// errore
				MessageUtils.addErrorMsg("Le password inserite nei campi Nuova e Conferma Nuova non corrispondono");
				return null;
			}

			boolean updateStoricoPassword = false;
			List<UserPassword> precedentiPassword = null;
			if(this.passwordVerifier!=null){
				StringBuilder motivazioneErrore = new StringBuilder();
				if(this.passwordVerifier.validate(this.userToUpdate, this.nuovaPassword, motivazioneErrore)==false){
					// errore
					MessageUtils.addErrorMsg(motivazioneErrore.toString());
					return null;
				}
				
				if(this.passwordVerifier.isHistory()) {
					precedentiPassword = findById.getPrecedentiPassword();
					
					// se la lista storico e' vuota controllo la password precedente salvata nel bean
					if(precedentiPassword == null || precedentiPassword.isEmpty()) {
						trovato = this.passwordManager.check(this.nuovaPassword, findById.getPassword());
						if(!trovato && this.passwordManager_backwardCompatibility!=null) {
							trovato = this.passwordManager_backwardCompatibility.check(this.nuovaPassword, findById.getPassword());
						}
						if (trovato) {
							MessageUtils.addErrorMsg("La password scelta non deve corrispondere ad una precedente password");
							return null;
						}
					}
					
					for (UserPassword userPassword : precedentiPassword) {
						trovato = this.passwordManager.check(this.nuovaPassword, userPassword.getPassword());
						if(!trovato && this.passwordManager_backwardCompatibility!=null) {
							trovato = this.passwordManager_backwardCompatibility.check(this.nuovaPassword, userPassword.getPassword());
						}
						if (trovato) {
							MessageUtils.addErrorMsg("La password scelta non deve corrispondere ad una precedente password");
							return null;
						}
					}
					
					UserPassword userPassword = new UserPassword();
					userPassword.setDatePassword(findById.getLastUpdatePassword());
					userPassword.setPassword(findById.getPassword());
					precedentiPassword.add(userPassword );
					updateStoricoPassword = true;
				}
			}
			
			// aggiorno data ultima modifica
//			this.user.setLastUpdatePassword(new Date());

			// cript pwd
			String newPassword = this.passwordManager.crypt(this.nuovaPassword);
			
			if(updateStoricoPassword) {
				((UserService)this.service).savePasswordStorico(findById.getId(), this.userToUpdate, newPassword, new Date(), precedentiPassword);
			} else {
				((UserService)this.service).savePassword(findById.getId(), this.userToUpdate, newPassword, new Date());
			}

			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Password aggiornata con successo"));
			
			// caricamento delle impostazioni utente e redirect alla home
			return Utility.getLoginBean().ricaricaUtenteDopoCambioPasswordScaduta();
		} catch (Exception e) {
			log.error("Cambio Password non riuscito",e);
			MessageUtils.addErrorMsg("Cambio Password non riuscito");
			return null;
		}
	}

	@Override
	public String delete() {
		// operazione non piu' permessa nel monitor
		return null;
	}

	public int getSizeSoggetti() {
		return this.user.getSoggetti() != null ? this.user
				.getSoggetti().size() : 0;
	}

	public Boolean isShowServizi() { return this.showServizi; }

	public void setShowServizi(Boolean showServizi) { this.showServizi = showServizi; }

	public void setListaSoggettiAssociatiUtente(List<String> lst){}

	public List<org.openspcoop2.utils.NameValue> getListaSoggettiAssociatiUtente() throws Exception{
		List<org.openspcoop2.utils.NameValue> lst = new ArrayList<org.openspcoop2.utils.NameValue>();
		List<IDSoggetto> utenteSoggettoList = this.user.getSoggetti();

		if(utenteSoggettoList != null && utenteSoggettoList.size() > 0) {
			for (IDSoggetto idSog : utenteSoggettoList) {
				org.openspcoop2.utils.NameValue val = new org.openspcoop2.utils.NameValue();
				val.setName(NamingUtils.getLabelSoggetto(idSog));
				val.setValue(NamingUtils.getLabelProtocollo(ProtocolFactoryManager.getInstance().getProtocolByOrganizationType(idSog.getTipo())));
				lst.add(val);
			}
		}

		return lst;
	}

	public void setListaServiziAssociatiUtente(List<String> lst){}

	public List<org.openspcoop2.utils.NameValue> getListaServiziAssociatiUtente() throws Exception{ 
		List<org.openspcoop2.utils.NameValue> lst = new ArrayList<org.openspcoop2.utils.NameValue>();
		List<IDServizio> utenteServizioList = this.user.getServizi();

		if(utenteServizioList != null && utenteServizioList.size() > 0) {
			for (IDServizio serv : utenteServizioList) {
				org.openspcoop2.utils.NameValue val = new org.openspcoop2.utils.NameValue();
				val.setName(NamingUtils.getLabelAccordoServizioParteSpecifica(serv));
				val.setValue(NamingUtils.getLabelProtocollo(ProtocolFactoryManager.getInstance().getProtocolByServiceType(serv.getTipo())));
				
				lst.add(val);
			}
		}
		return lst;
	}

	public String getProfilo() {
		return Costanti.LABEL_PROFILO;
	}

	public void setProfilo(String profilo) {
	}


	public boolean isGestionePassword() {
		return this.gestionePassword;
	}

	public void setGestionePassword(boolean gestionePassword) {
		this.gestionePassword = gestionePassword;
	}

	public String getVecchiaPassword() {
		return this.vecchiaPassword;
	}

	public void setVecchiaPassword(String vecchiaPassword) {
		this.vecchiaPassword = vecchiaPassword;
	}

	public boolean isShowFormCambiaPassword() {
		return this.showFormCambiaPassword;
	}

	public void setShowFormCambiaPassword(boolean showFormCambiaPassword) {
		this.showFormCambiaPassword = showFormCambiaPassword;
	}

	public boolean isShowVecchiaPassword() {
		if(this.user.getId() != null && this.user.getId() > 0){
			UserDetailsBean loggedUser = Utility.getLoggedUser();
			if(this.user.getLogin().equals(loggedUser.getUsername())) // && !loggedUser.isAdmin())
				return true;
		}

		return false;
	}

	public void setShowVecchiaPassword(boolean showVecchiaPassword) {
	}

	public String getModalitaDisponibiliUser() throws Exception {
		List<String> protocolliDisponibli = Utility.getLoginBean().listaProtocolliDisponibilePerUtentePddMonitor();
		StringBuilder sb= new StringBuilder();
		for (String protocollo : protocolliDisponibli) {
			if(sb.length() > 0)
				sb.append(", ");
			
			sb.append(NamingUtils.getLabelProtocollo(protocollo));
		}
		
		return sb.toString();
	}

	public String getNuovaPassword() {
		return this.nuovaPassword;
	}

	public void setNuovaPassword(String nuovaPassword) {
		this.nuovaPassword = nuovaPassword;
	}

	public String getUserToUpdate() {
		return this.userToUpdate;
	}

	public void setUserToUpdate(String userToUpdate) {
		this.userToUpdate = userToUpdate;
	}
	
	public List<SelectItem> getModalitaDisponibiliItems() throws Exception {
		List<String> protocolliDisponibli = Utility.getLoginBean().listaProtocolliDisponibilePerUtentePddMonitor();
		List<SelectItem> modalita = new ArrayList<>();
		for (String protocollo : protocolliDisponibli) {
			modalita.add(new SelectItem(protocollo, NamingUtils.getLabelProtocollo(protocollo)));
		}

		modalita.add(0, new SelectItem(Costanti.VALUE_PARAMETRO_MODALITA_ALL, Costanti.LABEL_PARAMETRO_MODALITA_ALL));
		
		return modalita;   
	}
	
	public String getModalitaDefault() throws Exception {
		if(this.getModalitaDefaultUtente().equals(Costanti.VALUE_PARAMETRO_MODALITA_ALL))
			return Costanti.LABEL_PARAMETRO_MODALITA_ALL;
				
		return NamingUtils.getLabelProtocollo(Utility.getLoginBean().listaProtocolliDisponibilePerUtentePddMonitor().get(0));
	}

	public boolean isVisualizzaSelectModalitaDisponibili() throws Exception {
		return Utility.getLoginBean().listaProtocolliDisponibilePerUtentePddMonitor().size() > 1;
	}

	public void setVisualizzaSelectModalitaDisponibili(boolean visualizzaSelectModalitaDisponibili) {
		
	}
	
	public String getModalitaDefaultUtente() {
		if(this.user.getProtocolloSelezionatoPddMonitor() == null)
			return Costanti.VALUE_PARAMETRO_MODALITA_ALL;
		
		return this.user.getProtocolloSelezionatoPddMonitor();
	}

	public void setModalitaDefaultUtente(String modalita) {
		this.user.setProtocolloSelezionatoPddMonitor(modalita);
	}

	public void modalitaDefaultUtenteSelected(ActionEvent ae) {
		
	}
	
	public String getSoggettoDefaultUtente() {
		if(this.user.getSoggettoSelezionatoPddMonitor() == null)
			return Costanti.VALUE_PARAMETRO_MODALITA_ALL;
		
		return this.user.getSoggettoSelezionatoPddMonitor();
	}
	
	public void setSoggettoDefaultUtente(String idSoggettoDefaultUtente) {
		this.user.setSoggettoSelezionatoPddMonitor(idSoggettoDefaultUtente);
	}
	
	public void idSoggettoDefaultUtenteSelected(ActionEvent ae) {
		
	}
	
	public boolean isVisualizzaSezioneSelezioneSoggetto() {
		log.debug("AAAAAAAAAAA: " + this.toString() + " isVisualizzaSezioneSelezioneSoggetto: " + (!this.getModalitaDefaultUtente().equals(Costanti.VALUE_PARAMETRO_MODALITA_ALL)));
		return !this.getModalitaDefaultUtente().equals(Costanti.VALUE_PARAMETRO_MODALITA_ALL);
	}

	public void setVisualizzaSezioneSelezioneSoggetto(boolean visualizzaSezioneSelezioneSoggetto) {
	}

	public boolean isVisualizzaSelectSoggettiDisponibili() throws Exception {
		if(!this.getModalitaDefaultUtente().equals(Costanti.VALUE_PARAMETRO_MODALITA_ALL))
			return LoginBean.listaSoggettiDisponibiliPerProtocollo(Utility.getLoggedUser(), this.getModalitaDefaultUtente()).size() > 1;
			
		return false;
	}
	
	public String getSoggettoDefault() throws Exception {
		if(LoginBean.listaSoggettiDisponibiliPerProtocollo(Utility.getLoggedUser(), this.getModalitaDefaultUtente()).size() > 1) {
			return Costanti.LABEL_PARAMETRO_MODALITA_ALL;
		}
		
		Soggetto soggetto = LoginBean.listaSoggettiDisponibiliPerProtocollo(Utility.getLoggedUser(), this.getModalitaDefaultUtente()).get(0);
		IDSoggetto idSoggetto = new IDSoggetto(soggetto.getTipoSoggetto(), soggetto.getNomeSoggetto()); 
		
		return NamingUtils.getLabelSoggetto(idSoggetto);
	}
	
	public List<SelectItem> getSoggettiDisponibiliItems() throws Exception {
		List<Soggetto> soggettiDisponibli = LoginBean.listaSoggettiDisponibiliPerProtocollo(Utility.getLoggedUser(), this.getModalitaDefaultUtente());
		List<SelectItem> modalita = new ArrayList<>();
		
		List<String> listaLabel = new ArrayList<>();
		Map<String, IDSoggetto> mapLabelIds = new HashMap<>();
		for (Soggetto soggetto : soggettiDisponibli) {
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
		
		for (String label : listaLabel) {
			modalita.add(new SelectItem(NamingUtils.getSoggettoFromLabel(this.getModalitaDefaultUtente(), label).toString(), label));
		}

		modalita.add(0, new SelectItem(Costanti.VALUE_PARAMETRO_MODALITA_ALL, Costanti.LABEL_PARAMETRO_MODALITA_ALL));
		
		return modalita;   
	}
}
