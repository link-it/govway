package org.openspcoop2.web.monitor.core.mbean;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities;
import org.openspcoop2.utils.crypt.Password;
import org.openspcoop2.utils.crypt.PasswordVerifier;
import org.slf4j.Logger;

import it.link.pdd.core.utenti.Ruolo;
import it.link.pdd.core.utenti.Utente;
import it.link.pdd.core.utenti.UtenteSoggetto;
import org.openspcoop2.core.commons.search.AccordoServizioParteSpecifica;
import org.openspcoop2.core.commons.search.IdSoggetto;
import org.openspcoop2.core.commons.search.Soggetto;
import org.openspcoop2.core.commons.search.constants.TipoPdD;
import org.openspcoop2.web.monitor.core.bean.UserDetailsBean;
import org.openspcoop2.web.monitor.core.dao.IService;
import org.openspcoop2.web.monitor.core.bean.SelectItem;
import org.openspcoop2.web.monitor.core.bean.UtentiSearchForm;
import org.openspcoop2.web.monitor.core.core.PddMonitorProperties;
import org.openspcoop2.web.monitor.core.core.PermessiUtenteOperatore;
import org.openspcoop2.web.monitor.core.core.Utility;
import org.openspcoop2.web.monitor.core.costants.Costanti;
import org.openspcoop2.web.monitor.core.costants.RuoliUtente;
import org.openspcoop2.web.monitor.core.dao.IUserService;
import org.openspcoop2.web.monitor.core.dao.UserService;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;
import org.openspcoop2.web.monitor.core.utils.DynamicPdDBeanUtils;
import org.openspcoop2.web.monitor.core.utils.MessageUtils;

public class UtentiBean extends PdDBaseBean<UtentiBean, String, IService<Utente, String>> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L; 
	private static Logger log =  LoggerManager.getPddMonitorCoreLogger();

	private Utente user;
	private String nomeSoggetto;
	private String confermaPassword;
	private String vecchiaPassword;
	private boolean showFormCambiaPassword = false;

	private transient UtentiSearchForm search;

	private DynamicPdDBeanUtils dynamicUtils = null;

	private Boolean showServizi = false;
	
	private String id;
	private List<SelectItem> tipiNomiSoggettiServiziSelezionati;
	private List<String> roles = null;
	private List<String> criteri = null;
	
	private String profilo =null;

	//	private Boolean visualizzaFiltroSoggetto = true;
	//	private Boolean visualizzaFiltroServizio = false;

	private String soggettoSuggestionValue= null;
	private String servizioSuggestionValue= null;

	private boolean checkDimensions = false;
	// lunghezza delle liste dei dati
	private Integer soggettiServiziSourceListWidth = null;
	private Integer soggettiServiziTargetListWidth = null;
	private Integer defaultListWidth = null;
	private Integer maxListWidth = null; 
	private Integer suggestionBoxWidth = null;
	private String suggestionBoxStyle;

	private List<SelectItem> soggettiServizi;

	private boolean gestionePassword = true;
	
	private PasswordVerifier passwordVerifier = null;

	public UtentiBean() {
		this.service = new UserService();

		try {
			this.gestionePassword = PddMonitorProperties.getInstance(log).isGestionePasswordUtentiAttiva();

			this.dynamicUtils = DynamicPdDBeanUtils.getInstance(log);
			
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

		this.soggettiServiziSourceListWidth = 186;
		this.soggettiServiziTargetListWidth = 186;
		this.defaultListWidth = 186;
		this.maxListWidth = 400;
		// Lunghezza di default della suggestion box e lista dei risultati
		this.suggestionBoxWidth = 186;
		this.suggestionBoxStyle = "width:" + this.suggestionBoxWidth + "px;";  //margin-left: 8px; 
	}

	public String getNotePassword(){
		if(this.passwordVerifier!=null){
			return this.passwordVerifier.help("<BR/>");
		}
		return null;
	}
	
	public List<String> getCriteri() {
		if(this.criteri == null){
			this.criteri = new ArrayList<String>();
			this.criteri.add("soggetti");
		}

		return this.criteri;
	}
	public void setCriteri(List<String> criteri) {
		this.criteri = criteri;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
	}

	public void setCorruptedMessage(String msg){}
	public String getCorruptedMessage(){
		if(this.user!=null){
			// check consistenza
			if(this.user.getRuoloList()==null || this.user.getRuoloList().size()<=0){
				return "ERROR: Utente non dispone di alcun ruolo ??";
			}
			boolean amministratore = false;
			boolean operatore = false;
			for (Ruolo ruolo : this.user.getRuoloList()) {
				if("ROLE_OPERATORE".equals(ruolo.getRuolo())){
					operatore = true;
				}
				else if("ROLE_ADMIN".equals(ruolo.getRuolo())){
					amministratore = true;
				}
			}
			if(operatore && !amministratore){
				if(this.user.getUtenteSoggettoList()==null || this.user.getUtenteSoggettoList().size()<=0){
					return "!!Attenzione!! Nessun soggetto associato all'operatore (I soggetti associati sono stati eliminati)";
				}
			}
		}
		return null;
	}

	public void setSearch(UtentiSearchForm search) {
		this.search = search;
	}

	public UtentiSearchForm getSearch() {
		return this.search;
	}

	public List<String> getRoles() {
		if (this.roles != null)
			return this.roles;

		if (this.user != null && this.user.getId() != null) {
			this.roles = new ArrayList<String>();
			List<Ruolo> auths = ((IUserService)this.service).getRoles(this.user);

			for (Ruolo authority : auths) {
				this.roles.add(authority.getRuolo());
			}
		}
		return this.roles;
	}

	public void setId(String id) {
		this.id = id;
		this.user = null;
		this.roles = null;
		
//this.user == null && 
		if (this.id != null){
			this.user = this.service.findById(this.id);

			this.tipiNomiSoggettiServiziSelezionati = new ArrayList<SelectItem>();

			for (UtenteSoggetto soggServ : this.user.getUtenteSoggettoList()) {
				long idSoggetto = soggServ.getIdSoggetto();
				Soggetto soggetto = this.dynamicUtilsService.findSoggettoById(idSoggetto);
				IDServizio idServizio = new IDServizio(soggetto.getTipoSoggetto(), soggetto.getNomeSoggetto());

				AccordoServizioParteSpecifica asps =null;
				Long idServizioLong = soggServ.getIdServizio();
				if(idServizioLong != null){
					asps = this.dynamicUtilsService.getAspsFromId(idServizioLong);
					idServizio.setTipoServizio(asps.getTipo());
					idServizio.setServizio(asps.getNome());
				}

				String label = Utility.convertToSoggettoServizio(idServizio);
				SelectItem item = new SelectItem(label, label);
				this.tipiNomiSoggettiServiziSelezionati.add(item);
			}

			this.soggettiServizi = _getSoggettiServizi(null,null,this.tipiNomiSoggettiServiziSelezionati);
			this.checkDimensions = true;
			this.checkDimensions();
			this.checkDimensions = false;
		}
	}

	public String getId() {
		return this.id;
	}

	public String getNomeSoggetto() {
		// if(this.user!=null && this.user.getSoggetto()!=null)
		// this.nomeSoggetto=this.user.getSoggetto().getNome();

		return this.nomeSoggetto;
	}

	public void setNomeSoggetto(String nomeSoggetto) {
		this.nomeSoggetto = nomeSoggetto;
	}

	public Utente getUser() {

		if (this.user == null && this.id == null){
			this.user = new Utente();

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

	public void setUser(Utente user) {
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

	public boolean getShowSoggettiAssociati() {
		/*
		 * Nella form di gestione degli utenti: -se viene selezionato il ruolo
		 * OPERATORE deve essere possibile associare i soggetti in gestione -se
		 * viene selezionato il ruolo AMMINISTRATORE NON va visualizzato il box
		 * dei soggetti in gestione -se e' selezionato SOLO il ruolo
		 * CONFIGURATORE NON va visualizzato il box dei soggetti in gestione
		 * 
		 * La visualizzazione dei soggetti in gestione avviene solo nel caso
		 * delle seguenti selezioni: -Operatore -Operatore + Configuratore
		 */

		// questa e' la lista dei ruoli selezionati da interfaccia
		List<String> ruoli = this.getRoles();

		if (ruoli == null)
			return true;

		// rimuovo eventuale ROLE_USER che non mi serve per il controllo
		Iterator<String> it = ruoli.iterator();
		while (it.hasNext()) {
			String ruolo = it.next();
			if ("ROLE_USER".equals(ruolo)) {
				it.remove();
			}
		}

		boolean hasConfiguratore = false;
		boolean hasAdmin = false;
		boolean hasOperatore = false;

		// cerco se ha il ruolo configuratore
		for (String ruolo : ruoli) {
			if (RuoliUtente.ROLE_CONFIG.equals(RuoliUtente.valueOf(ruolo))) {
				hasConfiguratore = true;
			}
			if (RuoliUtente.ROLE_ADMIN.equals(RuoliUtente.valueOf(ruolo))) {
				hasAdmin = true;
			}
			if (RuoliUtente.ROLE_OPERATORE.equals(RuoliUtente.valueOf(ruolo))) {
				hasOperatore = true;
			}
		}

		/*
		 * La visualizzazione dei soggetti in gestione avviene solo nel caso
		 * delle seguenti selezioni: -Operatore -Operatore + Configuratore
		 */
		if (hasOperatore && !hasAdmin && !hasConfiguratore)
			return true;

		if (hasOperatore && hasConfiguratore && !hasAdmin)
			return true;

		return false;

		// //se non ha configuratore come ruolo allora mostro il campo soggetti
		// associati
		// if(!hasConfiguratore)
		// return true;
		//
		// //se ha SOLO configuratore come ruolo NON visualizzo il pannello
		// if(hasConfiguratore && ruoli.size()==1)
		// return false;

		// return true;
	}

	public List<javax.faces.model.SelectItem> getSoggetti() {
		List<Soggetto> list = this.dynamicUtils.getListaSoggetti(null, TipoPdD.OPERATIVO);  
		List<javax.faces.model.SelectItem> soggetti = new ArrayList<javax.faces.model.SelectItem>();
		for (Soggetto soggetto : list) {
			soggetti.add(new javax.faces.model.SelectItem(soggetto.getTipoSoggetto() + Costanti.SEPARATORE_TIPO_NOME
					+ soggetto.getNomeSoggetto()));
		}
		return soggetti;
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
				Utente findById = this.service.findById(this.user.getLogin());
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

	private String valida(boolean isAdd, boolean isCambioPassword){
		String msg = null;

		if(StringUtils.isEmpty(this.user.getLogin())){
			return "Il campo Username non pu\u00F2 essere vuoto";
		}

		if(StringUtils.isEmpty(this.user.getPassword())){
			return "Il campo Password non pu\u00F2 essere vuoto";
		}

		if(isCambioPassword || isAdd){
			if(StringUtils.isEmpty(this.confermaPassword)){
				return "Il campo Conferma Password non pu\u00F2 essere vuoto";
			}
		}
		
		if(isCambioPassword || isAdd){
			if(this.passwordVerifier!=null){
				StringBuffer motivazioneErrore = new StringBuffer();
				if(this.passwordVerifier.validate(this.user.getLogin(), this.user.getPassword(), motivazioneErrore)==false){
					return motivazioneErrore.toString();
				}
			}
		}

		if(!isCambioPassword){ // controllo valido solo in add/edit utente da parte dell'admin
			if(this.roles == null || this.roles.size() == 0){
				return "Il campo Ruoli non pu\u00F2 essere vuoto, selezionare almeno un ruolo per l'utente";
			}

			boolean hasConfiguratore = false;
			boolean hasAdmin = false;
			boolean hasOperatore = false;

			for (String ruolo : this.roles) {
				if (RuoliUtente.ROLE_CONFIG.equals(RuoliUtente.valueOf(ruolo))) {
					hasConfiguratore = true;
				}
				if (RuoliUtente.ROLE_ADMIN.equals(RuoliUtente.valueOf(ruolo))) {
					hasAdmin = true;
				}
				if (RuoliUtente.ROLE_OPERATORE.equals(RuoliUtente.valueOf(ruolo))) {
					hasOperatore = true;
				}
			}

			if(this.getTipiNomiSoggettiServiziSelezionati().size() == 0)  {
				// controllare che l'utente operatore abbia anche ruolo admin per avere la lista dei soggetti vuota altrimenti errore.
				// oppure operatore/configuratore
				if ((hasOperatore && !hasAdmin && !hasConfiguratore) || (hasOperatore && hasConfiguratore && !hasAdmin)){
					return "Attenzione, l'utente che non ha ruolo Amministratore deve avere associato almeno un Soggetto/Servizio.";
				}
			}else {
				// controllo univocita' delle selezioni
				List<String> listaSoggettiAggiunti = new ArrayList<String>();
				List<String> listaSoggettiServiziAggiunti = new ArrayList<String>();

				if(this.getTipiNomiSoggettiServiziSelezionati().size() > 0){
					for (SelectItem selezione : this.getTipiNomiSoggettiServiziSelezionati()) {
						String value = selezione.getValue();

						if(listaSoggettiServiziAggiunti.contains(value)){
							return "Attenzione, hai gia' inserito il soggetto/servizio ["+value+"], eliminare il duplicato per proseguire.";
						}

						// servizio
						IDServizio idServizio = Utility.parseSoggettoServizio(value);
						if(idServizio.getServizio()!=null){
							if(PermessiUtenteOperatore.CHECK_UNIQUE_SOGGETTO_SERVIZIO_UTENTE){
								if(listaSoggettiAggiunti.contains(idServizio.getSoggettoErogatore().toString())){
									return "Attenzione, hai già inserito tutti i servizi per il soggetto ["+idServizio.getSoggettoErogatore().toString()+"], se si vuole inserire un servizio specifico eliminare la selezione del soggetto.";
								}
							}

							listaSoggettiServiziAggiunti.add(value);
						} else { // soggetto
							listaSoggettiAggiunti.add(value);

							// check che non aggiunga un soggetto per un servizio che ho gia' aggiunto
							if(PermessiUtenteOperatore.CHECK_UNIQUE_SOGGETTO_SERVIZIO_UTENTE){
								for (String tipoNomeSoggetto : listaSoggettiServiziAggiunti) {
									// ho trovato un servizio per il soggetto che sto aggiungendo e lo elimino.
									if(tipoNomeSoggetto.startsWith(value)){
										return "Attenzione, hai già inserito almeno un servizio per il soggetto ["+value+"], se si vuole inserire tutti i servizi del soggetto eliminare i singoli servizi selezionati.";
									}
								}
							}

							listaSoggettiServiziAggiunti.add(value);
						}
					}
				}
			}
		}

		return msg;
	}

	public String salva() throws Exception {

		boolean isAdd = this.user.getId() != null && this.user.getId() < 1;

		String msgErr = valida(isAdd, false);

		if (msgErr != null) {
			MessageUtils.addErrorMsg(msgErr);
			return null;
		}

		if (isAdd) {
			// nuovo utente

			// controllo se username disponibile
			Utente exiting = null;
			try {
				exiting = ((IUserService)this.service).find(this.user);
				// getEntityManager().createQuery("from User u where u.username=:nome").setParameter("nome",
				// this.user.getLogin()).getSingleResult();
			} catch (Exception e) {
				exiting = null;
			}
			if (exiting != null) {
				// errore
				MessageUtils
				.addErrorMsg("Esiste già un utente con l'username indicato");
				return null;
			}

			// controlla pwd
			if (!StringUtils.equals(this.confermaPassword,
					this.user.getPassword())) {
				// errore
				MessageUtils
				.addErrorMsg("Le password fornite non coincidono");
				return null;
			}

			if(this.gestionePassword){ // cripto la password solo se devo usarla altrimenti lascio "*"
				// cript pwd
				Password passwordManager = new Password();
				this.user.setPassword(passwordManager.cryptPw(this.user
						.getPassword()));

			}
			// salvo il nuovo utente
			this.service.store(this.user);

			// carico il valore dell'id dell'utente creato, necessario per
			// inserire le altre informazioni ad esso collegato
			this.user = ((IUserService)this.service).find(this.user);
		}

		try {

			// imposto ruoli
			ArrayList<Ruolo> auths = new ArrayList<Ruolo>();
			Ruolo r = new Ruolo();
			r.setRuolo("ROLE_USER");
			auths.add(r);

			for (String ruolo : this.roles) {
				Ruolo r1 = new Ruolo();
				r1.setRuolo(ruolo);
				auths.add(r1);
			}

			this.user.setRuoloList(auths);

			List<UtenteSoggetto> soggetti = new ArrayList<UtenteSoggetto>();
			if(this.tipiNomiSoggettiServiziSelezionati != null && getShowSoggettiAssociati()){
				if(this.tipiNomiSoggettiServiziSelezionati.size() > 0){
					for (SelectItem selezione : this.tipiNomiSoggettiServiziSelezionati) {
						String value = selezione.getValue();

						IDServizio idServizio = Utility.parseSoggettoServizio(value);						

						String tipoSoggetto = idServizio.getSoggettoErogatore().getTipo();
						String nomeSoggetto =  idServizio.getSoggettoErogatore().getNome();
						String tipoServizio = idServizio.getTipoServizio(); 
						String nomeServizio =  idServizio.getServizio();

						Soggetto s = this.dynamicUtilsService.findSoggettoByTipoNome(tipoSoggetto, nomeSoggetto);
						AccordoServizioParteSpecifica asps = null;

						if(StringUtils.isNotEmpty(tipoServizio) && StringUtils.isNotEmpty(nomeServizio)){
							asps = this.dynamicUtilsService.getAspsFromValues(tipoServizio, nomeServizio, tipoSoggetto, nomeSoggetto);
						}

						UtenteSoggetto us = new UtenteSoggetto();
						us.setIdSoggetto(s.getId());
						if(asps!= null)
							us.setIdServizio(asps.getId());
						soggetti.add(us);
					}
				} 
			}
			this.user.setUtenteSoggettoList(soggetti);

			// salva
			this.service.store(this.user);

			MessageUtils.addInfoMsg("Operazione effettuata correttamente.");

		} catch (Exception e) {
			UtentiBean.log.error(e.getMessage(), e);
			MessageUtils
			.addErrorMsg("Si e' verificato un errore durante il salvataggio. Riprovare.");
			return null;
		}

		// salva
		// this.service.store(this.user);

		return "user";
	}

	@Override
	public String delete() {
		this.toRemove = new ArrayList<UtentiBean>();
		Iterator<UtentiBean> it = this.selectedIds.keySet().iterator();
		while (it.hasNext()) {
			UtentiBean elem = it.next();
			if (this.selectedIds.get(elem).booleanValue()) {
				this.toRemove.add(elem);
				it.remove();
			}
		}

		for (UtentiBean elem : this.toRemove) {
			try {
				if(elem.getUser().getLogin().equals(Utility.getLoggedUser().getUsername())){
					MessageUtils
					.addWarnMsg("Non e' possibile eliminare l'utenza '"+elem.getUser().getLogin()+"' poichè risulta essere l'utenza attualmente collegata alla console.");
					continue;
				}
				this.service.delete(elem.getUser());
			} catch (Exception e) {
				MessageUtils.addErrorMsg("Errore durante l'eliminazione degli utenti");
			}

		}
		return null;
	}

	public int getSizeSoggetti() {
		return this.user.getUtenteSoggettoList() != null ? this.user
				.getUtenteSoggettoList().size() : 0;
	}

	public List<String> getTipiNomiSoggettiServiziAssociati() {
		List<String> tooltip = new ArrayList<String>();
		for (UtenteSoggetto soggServ : this.user.getUtenteSoggettoList()) {
			long idSoggetto = soggServ.getIdSoggetto();
			Soggetto soggetto = this.dynamicUtilsService.findSoggettoById(idSoggetto);
			IDServizio idServizio = new IDServizio(soggetto.getTipoSoggetto(), soggetto.getNomeSoggetto());

			AccordoServizioParteSpecifica asps =null;
			Long idServizioLong = soggServ.getIdServizio();
			if(idServizioLong != null){
				asps = this.dynamicUtilsService.getAspsFromId(idServizioLong);
				idServizio.setTipoServizio(asps.getTipo());
				idServizio.setServizio(asps.getNome());
			}

			String label = Utility.convertToSoggettoServizio(idServizio);
			tooltip.add(label);
		}

		return tooltip;
	}

	/***
	 * 
	 * Restituisce la rappresentazione dei ruoli di un utente, per visualizzarli nella tabella di gestione degli utenti
	 * 
	 * @return
	 */
	public String getRuoliUtente(){
		if(this.user == null)
			return "";

		if(this.user.getRuoloList() == null)
			return "";

		if(this.user.getRuoloList() != null && this.user.getRuoloList().size() == 0)
			return "";

		StringBuilder sb = new StringBuilder();

		for (Ruolo ruolo : this.user.getRuoloList()) {
			RuoliUtente rU = RuoliUtente.valueOf(ruolo.getRuolo());

			if(!rU.equals(RuoliUtente.ROLE_USER)){
				sb.append(RuoliUtente.getValue(rU)).append(", ");
			}
		}
		if(sb.length() > 0){
			int idx = sb.lastIndexOf(", ");

			if(idx> 0)
				return sb.substring(0, idx);
		}

		return sb.toString();
	}

	public List<String> nomeUtenteAutoComplete(Object val){
		List<String> list = null;
		if(val==null || StringUtils.isEmpty((String)val))
			list = new ArrayList<String>();
		else{
			list = ((IUserService)this.service).nomeUtenteAutoComplete((String) val);
		}		

		list.add(0,"--");
		return list;
	}





	public Boolean getVisualizzaFiltroSoggetto() {
		//		this.visualizzaFiltroSoggetto = false;
		if(this.getCriteri() != null && this.getCriteri().size() > 0){
			if(this.getCriteri().contains("soggetti")){
				//				this.visualizzaFiltroSoggetto = true;
				return true;
			}
		}

		//		return this.visualizzaFiltroSoggetto;
		return false;
	}
	public void setVisualizzaFiltroSoggetto(Boolean visualizzaFiltroSoggetto) {
		//		this.visualizzaFiltroSoggetto = visualizzaFiltroSoggetto;
	}

	public Boolean getVisualizzaFiltroServizio() {
		//		this.visualizzaFiltroServizio = false;
		if(this.getCriteri() != null && this.getCriteri().size() > 0){
			if(this.getCriteri().contains("servizi")){
				//				this.visualizzaFiltroServizio = true;
				return true;
			}
		}

		//		return this.visualizzaFiltroServizio;
		return false;
	}
	public void setVisualizzaFiltroServizio(Boolean visualizzaFiltroServizio) {
		//		this.visualizzaFiltroServizio = visualizzaFiltroServizio;
	}



	public String getSoggettoSuggestionValue() {
		return this.soggettoSuggestionValue;
	}
	public void setSoggettoSuggestionValue(String soggettoSuggestionValue) {
		this.soggettoSuggestionValue = soggettoSuggestionValue;
	}
	public String getServizioSuggestionValue() {
		return this.servizioSuggestionValue;
	}
	public void setServizioSuggestionValue(String servizioSuggestionValue) {
		this.servizioSuggestionValue = servizioSuggestionValue;
	}
	public void filtroSoggettiSelectListener(ActionEvent ae){
		String selSog = this.getSoggettoSuggestionValue();
		String selServ = this.getServizioSuggestionValue();

		this.soggettiServizi = this._getSoggettiServizi(selSog,selServ,this.tipiNomiSoggettiServiziSelezionati);
	}

	public void filtroServiziSelectListener(ActionEvent ae){
		String selSog = this.getSoggettoSuggestionValue();
		String selServ = this.getServizioSuggestionValue();

		this.soggettiServizi = this._getSoggettiServizi(selSog,selServ,this.tipiNomiSoggettiServiziSelezionati);
	}

	public List<SelectItem> getTipiNomiSoggettiServiziSelezionati() {
		if (this.tipiNomiSoggettiServiziSelezionati == null)
			this.tipiNomiSoggettiServiziSelezionati = new ArrayList<SelectItem>();

		return this.tipiNomiSoggettiServiziSelezionati;
	}

	public void setTipiNomiSoggettiServiziSelezionati(List<SelectItem> nomiSoggettiSelezionati) {
		this.tipiNomiSoggettiServiziSelezionati = nomiSoggettiSelezionati;
	}

	public void soggettiServiziSelectListener(ActionEvent ae){

	}

	public void criteriSelectListener(ActionEvent ae){
		String selSog = this.getSoggettoSuggestionValue();
		String selServ = this.getServizioSuggestionValue();

		this.soggettiServizi = this._getSoggettiServizi(selSog,selServ,this.tipiNomiSoggettiServiziSelezionati);
		this.checkDimensions = true;
		this.checkDimensions();
		this.checkDimensions = false;
	}



	public List<SelectItem> getSoggettiServizi() {
		return this.soggettiServizi;
	}

	public void setSoggettiServizi(List<SelectItem> soggettiServizi) {
		this.soggettiServizi = soggettiServizi;
	}

	private boolean addElementoSourceList(String elemento, List<SelectItem> lista){
		if(lista == null || lista.size() == 0)
			return true;

		for (SelectItem selectItem : lista) {
			if(selectItem.getValue().equals(elemento))
				return false;
		}

		return true;
	}

	private List<SelectItem> _getSoggettiServizi(String valSoggetto, String valServizio, List<SelectItem> soggettiServiziSelezionati) {
		String tipoProtocollo = null;
		List<SelectItem> soggetti = new ArrayList<SelectItem>();

		// quando viene selezionata la visualizzazione per soggetti
		if(this.getVisualizzaFiltroSoggetto()){
			List<Soggetto> list = this.dynamicUtilsService.soggettiAutoComplete(tipoProtocollo ,valSoggetto,true);

			try{
				for (Soggetto soggetto : list) {
					// per ogni soggetto aggiungo prima il soggetto
					IDServizio idSoggettoNoServizio = new IDServizio(soggetto.getTipoSoggetto(), soggetto.getNomeSoggetto());

					if(addElementoSourceList(idSoggettoNoServizio.getSoggettoErogatore().toString(), soggettiServiziSelezionati))
						soggetti.add(new SelectItem(idSoggettoNoServizio.getSoggettoErogatore().toString(),idSoggettoNoServizio.getSoggettoErogatore().toString()));

					if(this.getVisualizzaFiltroServizio()) {
						// ora tutti i servizi di quel soggetto
						List<Map<String,Object>> findElencoServizi = this.dynamicUtilsService.findElencoServizi(tipoProtocollo, soggetto,valServizio,true);

						if(findElencoServizi != null && findElencoServizi.size() > 0){
							for (Map<String, Object> res : findElencoServizi) {
								IDServizio idServizio = new IDServizio(soggetto.getTipoSoggetto(), soggetto.getNomeSoggetto());

								Object obj = res.get(JDBCUtilities.getAlias(AccordoServizioParteSpecifica.model().NOME));

								String nomeAsps = (obj instanceof String) ? (String) obj : null;
								idServizio.setServizio(nomeAsps);

								String tipoAsps = null;
								obj = res.get(JDBCUtilities.getAlias(AccordoServizioParteSpecifica.model().TIPO));
								tipoAsps = (obj instanceof String) ? (String) obj : null;
								idServizio.setTipoServizio(tipoAsps);
								String serv = Utility.convertToSoggettoServizio(idServizio);

								if(addElementoSourceList(serv, soggettiServiziSelezionati))
									soggetti.add(new SelectItem(serv,serv));
							}
						}
					}
				}
			}catch(Exception e){
				log.error("Si e' verificato un errore durante la ricerca dei soggetti e servizi]: " + e.getMessage(),e);
			}
		} else {
			try{
				if(this.getVisualizzaFiltroServizio()) {

					List<AccordoServizioParteSpecifica> findElencoServizi = this.dynamicUtilsService.getServizi(tipoProtocollo, null, null, null,valServizio,true);

					if(findElencoServizi != null && findElencoServizi.size() > 0){
						for (AccordoServizioParteSpecifica asps : findElencoServizi) {
							IdSoggetto soggetto = asps.getIdErogatore();
							IDServizio idServizio = new IDServizio(soggetto.getTipo(), soggetto.getNome());

							idServizio.setServizio(asps.getNome());
							idServizio.setTipoServizio(asps.getTipo());
							String serv = Utility.convertToSoggettoServizio(idServizio);

							if(addElementoSourceList(serv, soggettiServiziSelezionati))
								soggetti.add(new SelectItem(serv,serv));
						}
					}
				}
			}catch(Exception e){
				log.error("Si e' verificato un errore durante la ricerca dei soggetti e servizi]: " + e.getMessage(),e);
			}
		}

		return soggetti;
	}

	public List<javax.faces.model.SelectItem> getImmagineSoggetti(){
		List<javax.faces.model.SelectItem> selectItems = new ArrayList<javax.faces.model.SelectItem>();

		List<SelectItem> _getSoggettiServizi = this._getSoggettiServizi(null, null, new ArrayList<SelectItem>());

		//		log.debug("---- Soggetti Trovati ----"); 

		if(_getSoggettiServizi != null && _getSoggettiServizi.size() > 0)
			for (SelectItem selectItem : _getSoggettiServizi) {
				//				log.debug(selectItem.getValue() + "|" + selectItem.getLabel());
				selectItems.add(new javax.faces.model.SelectItem(selectItem)); 
			}

		//		log.debug("---- ----- ----- ----");

		return selectItems;
	}

	public void setImmagineSoggetti(List<javax.faces.model.SelectItem> img){
	}

	public void checkDimensions() {
		try{
			if(this.checkDimensions){

				// elementi di sx
				this.soggettiServiziSourceListWidth = 186;
				if(this.soggettiServizi != null && this.soggettiServizi.size() > 0){
					for (SelectItem item : this.soggettiServizi) {
						String label = item.getLabel();

						int lunghezza = this.dynamicUtils.getFontWidth(label);
						this.soggettiServiziSourceListWidth = Math.max(this.soggettiServiziSourceListWidth,  lunghezza);
					}
				} 

				//elementi di dx
				this.soggettiServiziTargetListWidth = 186;
				if(this.tipiNomiSoggettiServiziSelezionati != null && this.tipiNomiSoggettiServiziSelezionati.size() > 0){
					for (SelectItem item : this.tipiNomiSoggettiServiziSelezionati) {
						String label = item.getLabel();

						int lunghezza = this.dynamicUtils.getFontWidth(label);
						this.soggettiServiziTargetListWidth = Math.max(this.soggettiServiziTargetListWidth,  lunghezza);
					}
				}

				// imposto la dimensione massima trovata per entrambi

				Integer mxc = Math.max(this.soggettiServiziTargetListWidth, this.soggettiServiziSourceListWidth) + 25;
				this.soggettiServiziTargetListWidth = mxc;
				this.soggettiServiziSourceListWidth = mxc;
				this.suggestionBoxWidth = mxc;
				this.suggestionBoxStyle = "width:" + this.suggestionBoxWidth + "px;"; //margin-left: 8px;  
			}
		}catch(Throwable e){
			log.error("Si e' verificato un errore durante la verifica delle dimensioni della picklist]: " + e.getMessage(),e);
		}
	}

	public Integer getSoggettiServiziSourceListWidth() {
		return DynamicPdDBeanUtils.checkLimits(this.defaultListWidth, this.maxListWidth, this.soggettiServiziSourceListWidth);
	}

	public void setSoggettiServiziSourceListWidth(Integer soggettiServiziSourceListWidth) {
		this.soggettiServiziSourceListWidth = soggettiServiziSourceListWidth;
	}

	public Integer getSoggettiServiziTargetListWidth() {
		return DynamicPdDBeanUtils.checkLimits(this.defaultListWidth, this.maxListWidth, this.soggettiServiziTargetListWidth);
	}

	public void setSoggettiServiziTargetListWidth(Integer soggettiServiziTargetListWidth) {
		this.soggettiServiziTargetListWidth = soggettiServiziTargetListWidth;
	}

	@Override
	public void addNewListener(ActionEvent ae) {
		super.addNewListener(ae);

		//		String selit = this.getSoggettoServizioSuggestionValue();
		this.tipiNomiSoggettiServiziSelezionati = new ArrayList<SelectItem>();
		this.soggettiServizi = _getSoggettiServizi(null,null,this.tipiNomiSoggettiServiziSelezionati);
		this.checkDimensions = true;
		this.checkDimensions();
		this.checkDimensions = false;
	}

	public String getSuggestionBoxStyle() {return this.suggestionBoxStyle;}

	public void setSuggestionBoxStyle(String suggestionBoxStyle) { this.suggestionBoxStyle = suggestionBoxStyle;}



	public Boolean isShowServizi() { return this.showServizi; }

	public void setShowServizi(Boolean showServizi) { this.showServizi = showServizi; }

	public void setListaSoggettiAssociatiUtente(List<String> lst){}
	
	public List<String> getListaSoggettiAssociatiUtente(){
		List<String> lst = this._getListaSoggettiServiziAssociatiUtente(false);
		return lst;
	}
	
	public void setListaServiziAssociatiUtente(List<String> lst){}
	
	public List<String> getListaServiziAssociatiUtente(){
		List<String> lst = this._getListaSoggettiServiziAssociatiUtente(true);
		return lst;
	}
	
	private List<String> _getListaSoggettiServiziAssociatiUtente(boolean servizi){
		List<String> lst = new ArrayList<>();
		
		List<UtenteSoggetto> utenteSoggettoList = this.user.getUtenteSoggettoList();
		
		if(utenteSoggettoList != null && utenteSoggettoList.size() > 0) {
			for (UtenteSoggetto utenteSoggetto : utenteSoggettoList) {
				
				IDServizio serv = new IDServizio(utenteSoggetto.getSoggetto().getTipo(),utenteSoggetto.getSoggetto().getNome());
				
				if(!servizi) {
					if(utenteSoggetto.getIdServizio() == null)
						lst.add(Utility.convertToSoggettoServizio(serv));
				} else {
					// servizio
					if(utenteSoggetto.getIdServizio() != null){
						serv.setServizio(utenteSoggetto.getServizio().getNome());
						serv.setTipoServizio(utenteSoggetto.getServizio().getTipo());
						lst.add(Utility.convertToSoggettoServizio(serv));
					}
				}
			}
		}
		
		return lst;
	}

	public String getProfilo() {
		if(Utility.getLoggedUser().isAdmin()){
			if(this.user.getId() != null && this.user.getId() > 0){
				if(this.user.getLogin().equals(Utility.getLoggedUser().getUsername()))
					this.profilo = "Profilo Utente";
				else 
					this.profilo = "Modifica Utente";
						
			}else {
				this.profilo = "Inserisci Utente";
			}
		}
		else {
			//not empty utentiBean.user.id and utentiBean.user.id gt 0
			this.profilo = this.user.getId() != null && this.user.getId() > 0 ? "Profilo Utente" : "Inserisci Utente";
		}
		
		return this.profilo;
	}

	public void setProfilo(String profilo) {
		this.profilo = profilo;
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
			if(this.user.getLogin().equals(loggedUser.getUsername()) && !loggedUser.isAdmin())
				return true;
		}
		
		return false;
	}

	public void setShowVecchiaPassword(boolean showVecchiaPassword) {
	}
	
	
}
