package org.openspcoop2.web.monitor.core.mbean;

import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.protocol.engine.utils.NamingUtils;
import org.openspcoop2.utils.crypt.Password;
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

	private PasswordVerifier passwordVerifier = null;

	public UtentiBean() {
		this.service = new UserService();

		try {
			this.gestionePassword = PddMonitorProperties.getInstance(log).isGestionePasswordUtentiAttiva();
			String passwordVerifierConfig = PddMonitorProperties.getInstance(log).getUtentiPasswordVerifier();
			if(passwordVerifierConfig!=null){
				this.passwordVerifier = new PasswordVerifier(passwordVerifierConfig);
				if(this.passwordVerifier.existsRestriction()==false){
					this.passwordVerifier = null;
				}
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
				Password passwordManager = new Password();
				if (!passwordManager.checkPw(this.vecchiaPassword, findById.getPassword())) {
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
				StringBuffer motivazioneErrore = new StringBuffer();
				if(this.passwordVerifier.validate(this.user.getLogin(), this.user.getPassword(), motivazioneErrore)==false){
					// errore
					MessageUtils.addErrorMsg(motivazioneErrore.toString());
					return null;
				}
			}

			// cript pwd
			Password passwordManager = new Password();
			this.getUser().setPassword(
					passwordManager.cryptPw(this.user.getPassword()));

			this.service.store(this.getUser());

			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage("Password modificata correttamente"));

			this.showFormCambiaPassword = false;
		} catch (Exception e) {
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

	@SuppressWarnings("deprecation")
	public List<String> getListaSoggettiAssociatiUtente(){
		List<String> lst = new ArrayList<>();
		List<IDSoggetto> utenteSoggettoList = this.user.getSoggetti();

		if(utenteSoggettoList != null && utenteSoggettoList.size() > 0) {
			for (IDSoggetto idSog : utenteSoggettoList) {
				IDServizio serv = new IDServizio();
				serv.setSoggettoErogatore(idSog);
				lst.add(Utility.convertToSoggettoServizio(serv));
			}
		}

		return lst;
	}

	public void setListaServiziAssociatiUtente(List<String> lst){}

	public List<String> getListaServiziAssociatiUtente(){
		List<String> lst = new ArrayList<>();
		List<IDServizio> utenteServizioList = this.user.getServizi();

		if(utenteServizioList != null && utenteServizioList.size() > 0) {
			for (IDServizio serv : utenteServizioList) {
				lst.add(Utility.convertToSoggettoServizio(serv));
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
