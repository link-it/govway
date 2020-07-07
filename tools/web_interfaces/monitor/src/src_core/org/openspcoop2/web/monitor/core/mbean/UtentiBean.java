/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.engine.utils.NamingUtils;
import org.openspcoop2.utils.crypt.CryptConfig;
import org.openspcoop2.utils.crypt.CryptFactory;
import org.openspcoop2.utils.crypt.ICrypt;
import org.openspcoop2.utils.crypt.PasswordVerifier;
import org.openspcoop2.web.lib.users.dao.User;
import org.openspcoop2.web.monitor.core.bean.UserDetailsBean;
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

	private Boolean showServizi = false;

	private String id;

	private boolean gestionePassword = true;
	private ICrypt passwordManager;
	private ICrypt passwordManager_backwardCompatibility;

	private PasswordVerifier passwordVerifier = null;

	public UtentiBean() {
		this.service = new UserService();

		try {
			PddMonitorProperties pddMonitorProperties = PddMonitorProperties.getInstance(log);
			
			String utentiPasswordConfig = pddMonitorProperties.getUtentiPassword();
			
			this.gestionePassword = pddMonitorProperties.isGestionePasswordUtentiAttiva();
			if(utentiPasswordConfig!=null){
				this.passwordVerifier = new PasswordVerifier(utentiPasswordConfig);
				if(this.passwordVerifier.existsRestriction()==false){
					this.passwordVerifier = null;
				}
			}
			CryptConfig config = new CryptConfig(utentiPasswordConfig);
			this.passwordManager = CryptFactory.getCrypt(log, config);
			if(config.isBackwardCompatibility()) {
				this.passwordManager_backwardCompatibility = CryptFactory.getOldMD5Crypt(log);
			}
			
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	public String getNotePassword(){
		if(this.passwordVerifier!=null){
			return this.passwordVerifier.help("<BR/>");
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

			if(this.passwordVerifier!=null){
				StringBuilder motivazioneErrore = new StringBuilder();
				if(this.passwordVerifier.validate(this.user.getLogin(), this.user.getPassword(), motivazioneErrore)==false){
					// errore
					MessageUtils.addErrorMsg(motivazioneErrore.toString());
					return null;
				}
			}

			// cript pwd
			String newPassword = this.passwordManager.crypt(this.user.getPassword());
			((UserService)this.service).savePassword(this.user.getLogin(), newPassword);

			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage("Password modificata correttamente"));

			this.showFormCambiaPassword = false;
		} catch (Exception e) {
			log.error("Cambio Password non riuscito",e);
			MessageUtils.addErrorMsg("Cambio Password non riuscito");
		}

		return null;
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
		return "Profilo Utente";
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
}
