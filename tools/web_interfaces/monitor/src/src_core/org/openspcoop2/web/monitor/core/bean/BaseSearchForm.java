package org.openspcoop2.web.monitor.core.bean;

import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.generic_project.expression.SortOrder;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.EsitoTransazioneName;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.resources.MapReader;
import org.richfaces.model.Ordering;

import org.openspcoop2.monitor.engine.config.ricerche.ConfigurazioneRicerca;
import org.openspcoop2.monitor.engine.config.statistiche.ConfigurazioneStatistica;
import org.openspcoop2.core.commons.search.IdSoggetto;
import org.openspcoop2.core.commons.search.Soggetto;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.monitor.engine.condition.EsitoUtils;
import org.openspcoop2.monitor.sdk.condition.IFilter;
import org.openspcoop2.web.lib.users.dao.User;
import org.openspcoop2.web.monitor.core.bean.UserDetailsBean;
import org.openspcoop2.web.monitor.core.constants.ModalitaRicercaTransazioni;
import org.openspcoop2.web.monitor.core.constants.TipoMessaggio;
import org.openspcoop2.web.monitor.core.dynamic.Ricerche;
import org.openspcoop2.web.monitor.core.dynamic.Statistiche;
import org.openspcoop2.web.monitor.core.core.PddMonitorProperties;
import org.openspcoop2.web.monitor.core.core.PermessiUtenteOperatore;
import org.openspcoop2.web.monitor.core.core.Utility;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;
import org.openspcoop2.web.monitor.core.utils.DynamicPdDBeanUtils;
import org.openspcoop2.web.monitor.core.utils.MessageUtils;

public abstract class BaseSearchForm extends AbstractDateSearchForm {

	// private String nomeMittente;
	private String nomeAzione;
	private String nomeServizio;
	// private String nomeDestinatario;
	private String tipologiaRicerca;
	// private String trafficoPerSoggetto;
	// private String soggettoLocale;

	private Integer esitoGruppo;
	private Integer esitoDettaglio;
	private Integer[] esitoDettaglioPersonalizzato;
	private String esitoContesto;

	private String servizioApplicativo;
	private String idCorrelazioneApplicativa;
	private String idEgov;
	private String tipoIdMessaggio = TipoMessaggio.Richiesta.name();
	private String idTransazione;

	private String tipoNomeMittente;
	private String tipoNomeDestinatario;
	private String tipoNomeTrafficoPerSoggetto;
	private String tipoNomeSoggettoLocale;

	// ricerche
	private String nomeRicercaPersonalizzata;
	private Ricerche ricerchePersonalizzate;
	private ConfigurazioneRicerca ricercaSelezionata;
	// statistiche
	private String nomeStatisticaPersonalizzata;
	private Statistiche statistichePersonalizzate;
	private ConfigurazioneStatistica statisticaSelezionata;

	private boolean ricerchePersonalizzateAttive = false;
	private boolean statistichePersonalizzateAttive = false;

	private String protocollo;
	private List<SelectItem> protocolli= null;

	private IFilter filtro;

	// indica il tipo di ricerca che si vuole fare
	// true (default) = tipo di ricerca spcoop
	// false = tipo di ricerca IM
	// null = entrambe
	private String tipoRicercaSPCoop;

	private static String default_modalitaRicercaStorico = ModalitaRicercaTransazioni.ANDAMENTO_TEMPORALE.getValue();
	private String modalitaRicercaStorico = default_modalitaRicercaStorico;

	private String intervalloRefresh = null;
	private int tempoMassimoRefreshLive = 0;

	private User user;

	private SortOrder sortOrder = SortOrder.DESC;
	private String sortField = null;

	private Map<String, Ordering> sortOrders =null;

	private static Logger log =  LoggerManager.getPddMonitorCoreLogger();

	private TipiDatabase tipoDatabase = null;



	public TipiDatabase getDatabaseType() {
		return _getTipoDatabase(org.openspcoop2.core.transazioni.utils.ProjectInfo.getInstance());
	}

	public TipiDatabase _getTipoDatabase(org.openspcoop2.generic_project.beans.IProjectInfo pfInfo) {
		if(this.tipoDatabase == null){
			try {
				PddMonitorProperties pddMonitorProperties = PddMonitorProperties.getInstance(BaseSearchForm.log);
				this.tipoDatabase = pddMonitorProperties.tipoDatabase(pfInfo);
			} catch (Exception e) {
				log.error("Errore la get Tipo Database: " + e.getMessage(),e);
			}
		}
		return this.tipoDatabase;
	}

	@Override
	public String getPrintPeriodo(){
		return super.getDefaultPrintPeriodoBehaviour();
	}

	public BaseSearchForm() {

		this.tipologiaRicerca = "all";
		this.esitoGruppo = EsitoUtils.ALL_VALUE;
		this.esitoDettaglio = EsitoUtils.ALL_VALUE;
		try {
			EsitiProperties esitiProperties = EsitiProperties.getInstance(log);
			if(esitiProperties.getEsitoTransactionContextDefault()!=null){
				this.esitoContesto = esitiProperties.getEsitoTransactionContextDefault();
			}
			else{
				this.esitoContesto = EsitoUtils.ALL_VALUE_AS_STRING;
			}
		} catch (Exception e) {
			log.error("Errore durante l'impostazione del default per il contesto: " + e.getMessage(),e);
			this.esitoContesto = EsitoUtils.ALL_VALUE_AS_STRING;
		}
		this.tipoRicercaSPCoop = "spcoop";
		this.setPeriodo(this.periodoDefault != null ? this.periodoDefault
				: "Ultimo mese");
		this.sortOrder = SortOrder.DESC;
		this.sortField = null;
		this.sortOrders = new HashMap<String, Ordering>();

		try {
			PddMonitorProperties pddMonitorProperties = PddMonitorProperties.getInstance(BaseSearchForm.log);

			this.intervalloRefresh = pddMonitorProperties.getIntervalloRefreshTransazioniLive();
			this.tempoMassimoRefreshLive = pddMonitorProperties.getTempoMassimoRefreshLive();
			this.setRicerchePersonalizzateAttive(pddMonitorProperties.isAttivoModuloRicerchePersonalizzate());
			this.setStatistichePersonalizzateAttive(pddMonitorProperties.isAttivoModuloTransazioniStatistichePersonalizzate());			
		} catch (Exception e) {
			log.error("Errore durante la creazione del form: " + e.getMessage(),e);
		}

		this.modalitaRicercaStorico = default_modalitaRicercaStorico;
	}


	public BaseSearchForm(boolean useInBatch) {

		this.tipologiaRicerca = "all";
		this.esitoGruppo = EsitoUtils.ALL_VALUE;
		this.esitoDettaglio = EsitoUtils.ALL_VALUE;
		try {
			EsitiProperties esitiProperties = EsitiProperties.getInstance(log);
			if(esitiProperties.getEsitoTransactionContextDefault()!=null){
				this.esitoContesto = esitiProperties.getEsitoTransactionContextDefault();
			}
			else{
				this.esitoContesto = EsitoUtils.ALL_VALUE_AS_STRING;
			}
		} catch (Exception e) {
			log.error("Errore durante l'impostazione del default per il contesto: " + e.getMessage(),e);
			this.esitoContesto = EsitoUtils.ALL_VALUE_AS_STRING;
		}
		this.tipoRicercaSPCoop = "spcoop";
		this.setPeriodo(this.periodoDefault != null ? this.periodoDefault
				: "Ultimo mese");

		this.intervalloRefresh = "";
		this.tempoMassimoRefreshLive = 0;
		this.ricerchePersonalizzateAttive = false;
		this.statistichePersonalizzateAttive = false;
		this.sortOrder = SortOrder.DESC;
		this.sortField = null;
		this.sortOrders = new HashMap<String, Ordering>();

		this.modalitaRicercaStorico = default_modalitaRicercaStorico;
	}

	@Override
	protected String ripulisciValori(){
		this.initSearchListener(null);
		return null;
	}

	@Override
	public void initSearchListener(ActionEvent ae) {
		try {
			super.initSearchListener(ae);
			this.tipologiaRicerca = "all";
			this.esitoGruppo = EsitoUtils.ALL_VALUE;
			this.esitoDettaglio = EsitoUtils.ALL_VALUE;
			try {
				EsitiProperties esitiProperties = EsitiProperties.getInstance(log);
				if(esitiProperties.getEsitoTransactionContextDefault()!=null){
					this.esitoContesto = esitiProperties.getEsitoTransactionContextDefault();
				}
				else{
					this.esitoContesto = EsitoUtils.ALL_VALUE_AS_STRING;
				}
			} catch (Exception e) {
				log.error("Errore durante l'impostazione del default per il contesto: " + e.getMessage(),e);
				this.esitoContesto = EsitoUtils.ALL_VALUE_AS_STRING;
			}
			this.tipoRicercaSPCoop = "spcoop";
			this.setPeriodo(this.periodoDefault != null ? this.periodoDefault
					: "Ultimo mese");

			_setPeriodo();

			this.aggiornaNuovaDataRicerca();

			this.nomeAzione = null;
			// this.nomeDestinatario=null;
			// this.nomeMittente=null;
			this.nomeRicercaPersonalizzata = null;
			this.nomeServizio = null;
			// this.soggettoLocale=null;
			this.nomeStatisticaPersonalizzata = null;
			this.filtro = null;
			this.servizioApplicativo = null;
			this.ricercaSelezionata = null;
			// this.trafficoPerSoggetto=null;
			this.statisticaSelezionata = null;
			this.statistichePersonalizzate = null;
			this.ricerchePersonalizzate = null;

			this.tipoNomeDestinatario = null;
			this.tipoNomeMittente = null;
			this.tipoNomeSoggettoLocale = null;
			this.tipoNomeTrafficoPerSoggetto = null;

			this.idCorrelazioneApplicativa = null;
			this.idEgov = null;
			this.tipoIdMessaggio = TipoMessaggio.Richiesta.name();
			this.idTransazione = null;

			this.sortOrder = SortOrder.DESC;
			this.sortField = null;
			this.protocollo = null;

			this.modalitaRicercaStorico = default_modalitaRicercaStorico;

			// gia' eseguito nella chiamata del parent
//			this.executeQuery = false;
		} catch (Throwable e) {
			log.error("Errore durante l'inizializzazione: " + e.getMessage(),e);
		}
	}

	public void modalitaRicercaListener(ActionEvent ae) {

		//		String mod = this.modalitaRicercaStorico;
		//		
		//		initSearchListener(ae);
		//		
		//		this.modalitaRicercaStorico = mod;
		this.sortOrders = new HashMap<String, Ordering>();
	}

	public void soggettoLocaleSelected(ActionEvent ae){
		this.setServizioApplicativo(null);
	}

	public void destinatarioSelected(ActionEvent ae) {
		this.nomeServizio = null;
		this.nomeAzione = null;
		this.ricerchePersonalizzate = null;
		this.statistichePersonalizzate = null;
	}

	public void servizioSelected(ActionEvent ae) {
		this.nomeAzione = null;
		this.ricercaSelezionata = null;
		this.nomeRicercaPersonalizzata = null;
		this.nomeStatisticaPersonalizzata = null;
	}

	public void azioneSelected(ActionEvent ae) {
		this.ricercaSelezionata = null;
		this.nomeRicercaPersonalizzata = null;
		this.nomeStatisticaPersonalizzata = null;
	}

	public void tipologiaRicercaListener(ActionEvent ae) {
		// se cambia la tipologia di ricerca devo azzerare le scelte precedenti
		// this.nomeDestinatario = null;
		this.nomeServizio = null;
		this.nomeAzione = null;
		// this.nomeMittente = null;
		// this.trafficoPerSoggetto = null;
		this.ricerchePersonalizzate = null;
		this.ricercaSelezionata = null;
		// this.soggettoGestione = null;

		// User loggedUser = Utility.getLoggedUser();
		// this.identificativoPorta = loggedUser.getIdentificativoPorta();

		// if(loggedUser.isAdmin()==false){
		//
		// if("ingresso".equals(this.tipologiaRicerca)){
		// this.nomeDestinatario = loggedUser.getSoggetto().getNome();
		// }
		// if("uscita".equals(this.tipologiaRicerca)){
		// this.nomeMittente = loggedUser.getSoggetto().getNome();
		// }
		// }

		this.tipoNomeDestinatario = null;
		this.tipoNomeMittente = null;
		this.tipoNomeSoggettoLocale = null;
		this.tipoNomeTrafficoPerSoggetto = null;
		
		this.servizioApplicativo=null;

	}

	/**
	 * Se l'utente non e' impostato, allora lo prendo dal contesto di spring
	 * security Qualora l'utente fosse stato gia' impostato tramite la setUser
	 * ad esempio non sono in un contesto j2ee e quindi spring-security non lo
	 * utilizzo, allora viene ritornato l'utente gia' impostato
	 * 
	 * @return Utente collegato
	 */
	public User getUser() {

		if (this.user != null) {
			// e' stato impostato manualmente l'utente
			return this.user;
		}

		return Utility.getLoggedUtente();
	}

	public void setUser(User user) {
		this.user = user;
	}


	public String getTipoMittente() {
		return Utility.parseTipoSoggetto(this.tipoNomeMittente);
	}

	public String getNomeMittente() {
		return Utility.parseNomeSoggetto(this.tipoNomeMittente);
	}

	/**
	 * La stringa di input sara' del tipo tipoSoggetto/nomeSoggetto
	 * 
	 * @param tipoNomeMittente
	 */
	public void setTipoNomeMittente(String tipoNomeMittente) {
		this.tipoNomeMittente = tipoNomeMittente;

		if (StringUtils.isEmpty(this.tipoNomeMittente)
				|| "--".equals(this.tipoNomeMittente))
			this.tipoNomeMittente = null;
	}

	public String getNomeAzione() {
		return this.nomeAzione;
	}

	public void setNomeAzione(String nomeAzione) {
		this.nomeAzione = nomeAzione;

		if (StringUtils.isEmpty(nomeAzione) || "--".equals(nomeAzione))
			this.nomeAzione = null;
	}

	public String estraiNomeServizioDalServizio() {
		if(this.nomeServizio!=null){
			if(this.nomeServizio.contains(" ")){
				String [] tmp = this.nomeServizio.split(" ");
				if(tmp!=null && tmp.length>0){
					String tipoNomeServizio = tmp[0].trim();
					if(tipoNomeServizio.contains("/")){
						tmp = tipoNomeServizio.split("/");
						if(tmp!=null && tmp.length>1){
							return tmp[1].trim();
						}
					}
				}
			}
		}
		return null;
	}
	public String estraiTipoServizioDalServizio() {
		if(this.nomeServizio!=null){
			if(this.nomeServizio.contains(" ")){
				String [] tmp = this.nomeServizio.split(" ");
				if(tmp!=null && tmp.length>0){
					String tipoNomeServizio = tmp[0].trim();
					if(tipoNomeServizio.contains("/")){
						tmp = tipoNomeServizio.split("/");
						if(tmp!=null && tmp.length>1){
							return tmp[0].trim();
						}
					}
				}
			}
		}
		return null;
	}

	public String estraiNomeSoggettoDalServizio() {
		if(this.nomeServizio!=null){
			if(this.nomeServizio.contains(" ")){
				String [] tmp = this.nomeServizio.split(" ");
				if(tmp!=null && tmp.length>0){
					String tipoNomeSoggetto = tmp[1].trim();
					if(tipoNomeSoggetto.startsWith("(")){
						tipoNomeSoggetto = tipoNomeSoggetto.substring(1);
					}
					if(tipoNomeSoggetto.endsWith(")")){
						tipoNomeSoggetto = tipoNomeSoggetto.substring(0,(tipoNomeSoggetto.length()-1));
					}
					if(tipoNomeSoggetto.contains("/")){
						tmp = tipoNomeSoggetto.split("/");
						if(tmp!=null && tmp.length>1){
							return tmp[1].trim();
						}
					}
				}
			}
		}
		return null;
	}

	public String estraiTipoSoggettoDalServizio() {
		if(this.nomeServizio!=null){
			if(this.nomeServizio.contains(" ")){
				String [] tmp = this.nomeServizio.split(" ");
				if(tmp!=null && tmp.length>0){
					String tipoNomeSoggetto = tmp[1].trim();
					if(tipoNomeSoggetto.startsWith("(")){
						tipoNomeSoggetto = tipoNomeSoggetto.substring(1);
					}
					if(tipoNomeSoggetto.endsWith(")")){
						tipoNomeSoggetto = tipoNomeSoggetto.substring(0,(tipoNomeSoggetto.length()-1));
					}
					if(tipoNomeSoggetto.contains("/")){
						tmp = tipoNomeSoggetto.split("/");
						if(tmp!=null && tmp.length>1){
							return tmp[0].trim();
						}
					}
				}
			}
		}
		return null;
	}


	public String getNomeServizio() {
		return this.nomeServizio;
	}

	public void setNomeServizio(String nomeServizio) {
		this.nomeServizio = nomeServizio;

		if (StringUtils.isEmpty(nomeServizio) || "--".equals(nomeServizio))
			this.nomeServizio = null;
	}

	public String getTipoDestinatario() {
		if(this.tipoNomeDestinatario!=null && !"".equals(this.tipoNomeDestinatario)){
			return Utility.parseTipoSoggetto(this.tipoNomeDestinatario);
		}
		else{
			return this.estraiTipoSoggettoDalServizio();
		}
	}

	public String getNomeDestinatario() {
		if(this.tipoNomeDestinatario!=null && !"".equals(this.tipoNomeDestinatario)){
			return Utility.parseNomeSoggetto(this.tipoNomeDestinatario);
		}
		else{
			return this.estraiNomeSoggettoDalServizio();
		}
	}

	public void setTipoNomeDestinatario(String nomeDestinatario) {
		this.tipoNomeDestinatario = nomeDestinatario;

		if (StringUtils.isEmpty(nomeDestinatario)
				|| "--".equals(nomeDestinatario))
			this.tipoNomeDestinatario = null;

	}

	public String getTipologiaRicerca() {
		return this.tipologiaRicerca;
	}

	public void setTipologiaRicerca(String tipologiaRicerca) {
		this.tipologiaRicerca = tipologiaRicerca;

		if (StringUtils.isEmpty(tipologiaRicerca)
				|| "--".equals(tipologiaRicerca))
			this.tipologiaRicerca = null;
	}

	public PermessiUtenteOperatore getPermessiUtenteOperatore() throws CoreException {

		User u = getUser();
		UserDetailsBean user = new UserDetailsBean();
		user.setUtente(u);

		String tipoSoggettoLocale = null;
		String nomeSoggettoLocale = null;

		if(this.tipoNomeSoggettoLocale!=null && !StringUtils.isEmpty(this.tipoNomeSoggettoLocale) && !"--".equals(this.tipoNomeSoggettoLocale)){
			tipoSoggettoLocale = Utility.parseTipoSoggetto(this.tipoNomeSoggettoLocale);
			nomeSoggettoLocale = Utility.parseNomeSoggetto(this.tipoNomeSoggettoLocale);
		}

		return PermessiUtenteOperatore.getPermessiUtenteOperatore(user, tipoSoggettoLocale, nomeSoggettoLocale);

	}

	public String getTipoTrafficoPerSoggetto() {
		return Utility.parseTipoSoggetto(this.tipoNomeTrafficoPerSoggetto);
	}

	public String getTrafficoPerSoggetto() {
		return Utility.parseNomeSoggetto(this.tipoNomeTrafficoPerSoggetto);
	}

	public void setTipoNomeTrafficoPerSoggetto(String trafficoPerSoggetto) {
		this.tipoNomeTrafficoPerSoggetto = trafficoPerSoggetto;
		if (StringUtils.isEmpty(trafficoPerSoggetto)
				|| "--".equals(trafficoPerSoggetto))
			this.tipoNomeTrafficoPerSoggetto = null;
	}

	public String getTipoSoggettoLocale() {
		User u = getUser();
		if (u.getSoggetti().size() == 1) {
			IDSoggetto s = u.getSoggetti().get(0);
			this.tipoNomeSoggettoLocale = s.getTipo() + "/" + s.getNome();
		}

		return Utility.parseTipoSoggetto(this.tipoNomeSoggettoLocale);
	}

	/**
	 * ritorna il nome del soggetto locale
	 * 
	 * @return Soggetto Locale
	 */
	public String getSoggettoLocale() {

		User u = getUser();
		if (u.getSoggetti().size() == 1) {
			IDSoggetto s = u.getSoggetti().get(0);
			this.tipoNomeSoggettoLocale = s.getTipo() + "/" + s.getNome();
		}

		return Utility.parseNomeSoggetto(this.tipoNomeSoggettoLocale);
	}

	public void setTipoNomeSoggettoLocale(String soggettoLocale) {
		this.tipoNomeSoggettoLocale = soggettoLocale;

		if (StringUtils.isEmpty(soggettoLocale) || "--".equals(soggettoLocale)) {
			this.tipoNomeSoggettoLocale = null;
		}
	}

	/**
	 * I nomi spcoop dei soggetti associati all'utente loggato
	 */
	public List<Soggetto> getSoggettiGestione() {
		ArrayList<Soggetto> soggetti = new ArrayList<Soggetto>();

		User u = getUser();

		// se il soggetto locale e' specificato allora ritorno solo quello
		if (StringUtils.isNotEmpty(this.tipoNomeSoggettoLocale)) {

			// nomi.add(this.soggettoLocale);
			String tipo = Utility
					.parseTipoSoggetto(this.tipoNomeSoggettoLocale);
			String nome = Utility
					.parseNomeSoggetto(this.tipoNomeSoggettoLocale);

			for (IDSoggetto idSog : u.getSoggetti()) {
				if (idSog.getTipo().equals(tipo)
						&& idSog.getNome().equals(nome)) {
					IdSoggetto idsog2 = new IdSoggetto();
//					idsog2.setId(idSog.getId());
					idsog2.setNome(idSog.getNome());
					idsog2.setTipo(idSog.getTipo());
					Soggetto soggetto = Utility.getSoggetto(idsog2);
					soggetti.add(soggetto);
					break;
				}
			}

			return soggetti;
		} else {
			List<String> checkUnique = new ArrayList<String>();
			for (IDSoggetto idSog : u.getSoggetti()) {

				String tipoNome = idSog.getTipo()+"/"+idSog.getNome();
				if(checkUnique.contains(tipoNome)==false){
					IdSoggetto idsog2 = new IdSoggetto();
//					idsog2.setId(idSog.getId());
					idsog2.setNome(idSog.getNome());
					idsog2.setTipo(idSog.getTipo());

					Soggetto s = Utility.getSoggetto(idsog2);
					soggetti.add(s);	

					checkUnique.add(tipoNome);
				}

			}
			return soggetti;
		}

	}

	// public void setSoggettoGestione(String soggettoGestione) {
	//
	// this.soggettoGestione = soggettoGestione;
	//
	// //l'identificativo porta dipende dal soggetto selezionato
	// this.identificativoPorta = this.soggettoGestione+"SPCoopIT";
	//
	// if(StringUtils.isEmpty(soggettoGestione) ||
	// "--".equals(soggettoGestione)){
	// this.soggettoGestione = null;
	// this.identificativoPorta = null;
	// }
	// }

	public Integer getEsitoGruppo() {
		return this.esitoGruppo;
	}

	public void setEsitoGruppo(Integer esito) {
		this.esitoGruppo = esito;
		this.checkDettaglio();
		this.checkDettaglioPersonalizzato();
	}

	public Integer getEsitoDettaglio() {
		return this.esitoDettaglio;
	}

	public void setEsitoDettaglio(Integer esito) {
		this.esitoDettaglio = esito;
		this.checkDettaglio();
	}

	public Integer[] getEsitoDettaglioPersonalizzato() {
		return this.esitoDettaglioPersonalizzato;
	}

	public void setEsitoDettaglioPersonalizzato(Integer[] esitoDettaglioPersonalizzato) {
		this.esitoDettaglioPersonalizzato = esitoDettaglioPersonalizzato;
	}

	private void checkDettaglio(){
		if(EsitoUtils.ALL_VALUE != this.esitoDettaglio){
			// devo verificare il dettaglio che sia compatibile con il nuovo esito
			if(EsitoUtils.ALL_VALUE != this.esitoGruppo){
				try{
					EsitiProperties esitiProperties = EsitiProperties.getInstance(log);
					List<Integer> codes = null;
					if(EsitoUtils.ALL_ERROR_VALUE == this.esitoGruppo){
						codes = esitiProperties.getEsitiCodeKo();
					}
					else if(EsitoUtils.ALL_FAULT_APPLICATIVO_VALUE == this.esitoGruppo){
						codes = esitiProperties.getEsitiCodeFaultApplicativo();
					}
					else if(EsitoUtils.ALL_OK_VALUE == this.esitoGruppo){
						codes = esitiProperties.getEsitiCodeOk();
					}
					else if(EsitoUtils.ALL_PERSONALIZZATO_VALUE == this.esitoGruppo){
						this.esitoDettaglio = EsitoUtils.ALL_VALUE;
					}
					boolean found = false;
					for (Integer code : codes) {
						if(code == this.esitoDettaglio){
							found = true;
							break;
						}
					}
					if(!found){
						this.esitoDettaglio = EsitoUtils.ALL_VALUE;
					}
				}catch(Exception e){
					this.esitoDettaglio = EsitoUtils.ALL_VALUE;
					log.error("Errore durante il controllo della compatibilità del dettaglio esito "+e.getMessage(),e);
				}
			}
		}
	}
	private void checkDettaglioPersonalizzato(){
		// Rendere null l'oggetto rompe il componente
		/*if(this.esitoDettaglioPersonalizzato!=null && this.esitoDettaglioPersonalizzato.length>0){
			if(EsitoUtils.ALL_PERSONALIZZATO_VALUE != this.esitoGruppo){
				this.esitoDettaglioPersonalizzato=null;
			}
		}*/
	}

	public String getEsitoContesto() {
		return this.esitoContesto;
	}

	public void setEsitoContesto(String esitoContesto) {
		this.esitoContesto = esitoContesto;
	}

	public String getTipoRicercaSPCoop() {
		return this.tipoRicercaSPCoop;
	}

	public void setTipoRicercaSPCoop(String tipoRicercaSPCoop) {
		this.tipoRicercaSPCoop = tipoRicercaSPCoop;
	}

	public Boolean getTipologiaTransazioneSPCoop() {
		if ("spcoop".equals(this.tipoRicercaSPCoop))
			return true;
		if ("im".equals(this.tipoRicercaSPCoop))
			return false;
		return null;// entrambe
	}

	public String getServizioApplicativo() {
		return this.servizioApplicativo;
	}

	public void setServizioApplicativo(String servizioApplicativo) {
		this.servizioApplicativo = servizioApplicativo;

		if (StringUtils.isEmpty(servizioApplicativo)
				|| "--".equals(servizioApplicativo))
			this.servizioApplicativo = null;
	}

	public String getNomeRicercaPersonalizzata() {
		return this.nomeRicercaPersonalizzata;
	}

	public void setNomeRicercaPersonalizzata(String nomeRicercaPersonalizzata) {
		if ("--".equals(nomeRicercaPersonalizzata))
			nomeRicercaPersonalizzata = null;

		this.nomeRicercaPersonalizzata = nomeRicercaPersonalizzata;
	}

	public Ricerche getRicerchePersonalizzate() {
		return this.ricerchePersonalizzate;
	}

	public void setRicerchePersonalizzate(Ricerche ricerchePersonalizzate) {
		this.ricerchePersonalizzate = ricerchePersonalizzate;
	}

	public ConfigurazioneRicerca getRicercaSelezionata() {
		return this.ricercaSelezionata;
	}

	public void ricercaSelezionataListener(ActionEvent ae) {

		this.ricercaSelezionata = this.ricerchePersonalizzate
				.getRicercaByLabel(this.nomeRicercaPersonalizzata);
	}

	protected boolean validateForm() {
		// Tipo di periodo selezionato 'Personalizzato'
		if(this.getPeriodo().equals("Personalizzato")){
			if(this.getDataInizio() == null){
				MessageUtils.addErrorMsg("Selezionare Data Inizio");
				return false;
			}

			if(this.getDataFine() == null){
				MessageUtils.addErrorMsg("Selezionare Data Fine");
				return false;
			}
		}

		if(this.esitoGruppo!=null && (EsitoUtils.ALL_PERSONALIZZATO_VALUE == this.esitoGruppo)){
			if(this.esitoDettaglioPersonalizzato==null || this.esitoDettaglioPersonalizzato.length<=0){
				MessageUtils.addErrorMsg("Selezionare almeno un esito di dettaglio");
				return false;
			}
		}
		
		return true;
	}

	@Override
	protected String eseguiFiltra() {
		if(validateForm()==false) {
			return null;
		}

		return null;
	}

	public void setFiltro(IFilter filtro) {
		this.filtro = filtro;
	}

	public IFilter getFiltro() {
		return this.filtro;
	}

	public Statistiche getStatistichePersonalizzate() {
		return this.statistichePersonalizzate;
	}

	public ConfigurazioneStatistica getStatisticaSelezionata() {
		return this.statisticaSelezionata;
	}

	public void setStatistichePersonalizzate(
			Statistiche statistichePersonalizzate) {
		this.statistichePersonalizzate = statistichePersonalizzate;
	}

	public void setStatisticaSelezionata(
			ConfigurazioneStatistica statisticaSelezionata) {
		this.statisticaSelezionata = statisticaSelezionata;
	}

	public String getNomeStatisticaPersonalizzata() {
		return this.nomeStatisticaPersonalizzata;
	}

	public void setNomeStatisticaPersonalizzata(
			String nomeStatisticaPersonalizzata) {

		if ("--".equals(nomeStatisticaPersonalizzata))
			nomeStatisticaPersonalizzata = null;

		this.nomeStatisticaPersonalizzata = nomeStatisticaPersonalizzata;
	}

	public void statisticaSelezionataListener(ActionEvent ae) {


		this.statisticaSelezionata = this.statistichePersonalizzate
				.getStatisticaByLabel(this.nomeStatisticaPersonalizzata);
	}

	public void setRicercaSelezionata(ConfigurazioneRicerca ricercaSelezionata) {
		this.ricercaSelezionata = ricercaSelezionata;
	}

	public String getIdCorrelazioneApplicativa() {
		return this.idCorrelazioneApplicativa;
	}

	public void setIdCorrelazioneApplicativa(String idCorrelazioneApplicativa) {
		this.idCorrelazioneApplicativa = idCorrelazioneApplicativa;
		if (StringUtils.isEmpty(this.idCorrelazioneApplicativa)
				|| "--".equals(this.idCorrelazioneApplicativa))
			this.idCorrelazioneApplicativa = null;
	}

	public String getIdEgov() {
		return this.idEgov;
	}

	public void setIdEgov(String idEgov) {
		this.idEgov = idEgov;
	}

	public String getTipoIdMessaggio() {
		if(this.tipoIdMessaggio==null){
			this.tipoIdMessaggio = TipoMessaggio.Richiesta.name();
		}
		return this.tipoIdMessaggio;
	}

	public void setTipoIdMessaggio(String tipoIdMessaggio) {
		this.tipoIdMessaggio = tipoIdMessaggio;
	}

	public String getIdTransazione() {
		return this.idTransazione;
	}

	public void setIdTransazione(String idTransazione) {
		this.idTransazione = idTransazione;
	}

	public List<SelectItem> getEsitiGruppo() {
		ArrayList<SelectItem> list = new ArrayList<SelectItem>();

		list.add(new SelectItem(EsitoUtils.ALL_VALUE));
		list.add(new SelectItem(EsitoUtils.ALL_ERROR_VALUE));
		list.add(new SelectItem(EsitoUtils.ALL_FAULT_APPLICATIVO_VALUE));
		list.add(new SelectItem(EsitoUtils.ALL_OK_VALUE));
		list.add(new SelectItem(EsitoUtils.ALL_PERSONALIZZATO_VALUE));

		return list;
	}

	public boolean isShowDettaglioPersonalizzato(){
		return this.esitoGruppo!=null &&  (EsitoUtils.ALL_PERSONALIZZATO_VALUE == this.esitoGruppo);
	}
	public boolean isShowDettaglio(){
		return this.esitoGruppo!=null &&  (EsitoUtils.ALL_FAULT_APPLICATIVO_VALUE != this.esitoGruppo) && !this.isShowDettaglioPersonalizzato();
	}

	public List<SelectItem> getEsitiDettaglio() {
		ArrayList<SelectItem> list = new ArrayList<SelectItem>();

		list.add(new SelectItem(EsitoUtils.ALL_VALUE));

		try{

			EsitiProperties esitiProperties = EsitiProperties.getInstance(log);

			List<Integer> esiti = esitiProperties.getEsitiCodeOrderLabel();

			List<Integer> esitiFiltro = null;
			if(EsitoUtils.ALL_OK_VALUE == this.esitoGruppo){
				esitiFiltro = esitiProperties.getEsitiCodeOk_senzaFaultApplicativo();
			}
			else if(EsitoUtils.ALL_ERROR_VALUE == this.esitoGruppo){
				esitiFiltro = esitiProperties.getEsitiCodeKo_senzaFaultApplicativo();
			}

			for (Integer esito : esiti) {

				if(esitiFiltro!=null){
					boolean found = false;
					for (Integer esitoFiltro : esitiFiltro) {
						if(esitoFiltro == esito){
							found = true;
							break;
						}
					}
					if(!found){
						continue;
					}
				}

				String name = esitiProperties.getEsitoName(esito);
				EsitoTransazioneName esitoTransactionName = EsitoTransazioneName.convertoTo(name);

				SelectItem si = new SelectItem(esito);

				boolean pddSpecific = this.isPddSpecific(esitoTransactionName);
				boolean integrationManagerSpecific = this.isIntegrationManagerSpecific(esitoTransactionName);				
				if ("spcoop".equals(this.tipoRicercaSPCoop)) {
					if(pddSpecific || !integrationManagerSpecific){
						list.add(si);
					}
				} else if ("im".equals(this.tipoRicercaSPCoop)) {
					if(integrationManagerSpecific || !pddSpecific){
						list.add(si);
					}
				} else{
					list.add(si);
				}
			}

		}catch(Exception e){
			log.error("Errore durante il recupero della lista del dettaglio esito "+e.getMessage(),e);
			throw new RuntimeException(e.getMessage(),e);
		}

		return list;
	}

	public List<SelectItem> getEsitiDettagliPersonalizzati() {
		try{
			ArrayList<SelectItem> list = new ArrayList<SelectItem>();

			EsitiProperties esitiProperties = EsitiProperties.getInstance(log);

			List<Integer> esiti = esitiProperties.getEsitiCodeOrderLabel();

			for (Integer esito : esiti) {

				String name = esitiProperties.getEsitoName(esito);
				EsitoTransazioneName esitoTransactionName = EsitoTransazioneName.convertoTo(name);

				SelectItem si = new SelectItem(esito.intValue(),esitiProperties.getEsitoLabel(esito));

				boolean pddSpecific = this.isPddSpecific(esitoTransactionName);
				boolean integrationManagerSpecific = this.isIntegrationManagerSpecific(esitoTransactionName);				
				if ("spcoop".equals(this.tipoRicercaSPCoop)) {
					if(pddSpecific || !integrationManagerSpecific){
						list.add(si);
					}
				} else if ("im".equals(this.tipoRicercaSPCoop)) {
					if(integrationManagerSpecific || !pddSpecific){
						list.add(si);
					}
				} else{
					list.add(si);
				}
			}

			return list;
		}catch(Exception e){
			log.error("Errore durante il recupero della lista del dettaglio esito personalizzato "+e.getMessage(),e);
			throw new RuntimeException(e.getMessage(),e);
		}
	}

	private boolean isPddSpecific(EsitoTransazioneName esitoTransactionName){
		if(EsitoTransazioneName.ERRORE_APPLICATIVO.equals(esitoTransactionName) || 
				EsitoTransazioneName.ERRORE_PROTOCOLLO.equals(esitoTransactionName) ||
				EsitoTransazioneName.ERRORE_INVOCAZIONE.equals(esitoTransactionName) ||
				EsitoTransazioneName.ERRORE_SERVER.equals(esitoTransactionName) ||
				EsitoTransazioneName.ERRORE_PROCESSAMENTO_PDD_4XX.equals(esitoTransactionName) ||
				EsitoTransazioneName.ERRORE_PROCESSAMENTO_PDD_5XX.equals(esitoTransactionName) ||
				EsitoTransazioneName.CUSTOM.equals(esitoTransactionName)
				){
			return true;
		}
		return false;
	}
	private boolean isIntegrationManagerSpecific(EsitoTransazioneName esitoTransactionName){
		if(EsitoTransazioneName.MESSAGGI_NON_PRESENTI.equals(esitoTransactionName) || 
				EsitoTransazioneName.MESSAGGIO_NON_TROVATO.equals(esitoTransactionName) ||
				EsitoTransazioneName.AUTENTICAZIONE_FALLITA.equals(esitoTransactionName) ||
				EsitoTransazioneName.AUTORIZZAZIONE_FALLITA.equals(esitoTransactionName) 
				){
			return true;
		}
		return false;
	}

	public boolean isShowEsitiContesto(){
		return this.getEsitiContesto().size()>2;
	}

	public List<SelectItem> getEsitiContesto() {
		ArrayList<SelectItem> list = new ArrayList<SelectItem>();

		list.add(new SelectItem(EsitoUtils.ALL_VALUE_AS_STRING));

		try{

			EsitiProperties esitiProperties = EsitiProperties.getInstance(log);

			List<String> esiti = esitiProperties.getEsitiTransactionContextCodeOrderLabel();
			for (String esito : esiti) {

				SelectItem si = new SelectItem(esito);

				list.add(si);
			}

		}catch(Exception e){
			throw new RuntimeException(e.getMessage(),e);
		}

		return list;
	}

	public List<SelectItem> getTipiIdMessaggio() {
		ArrayList<SelectItem> list = new ArrayList<SelectItem>();
		list.add(new SelectItem(TipoMessaggio.Richiesta.name()));
		list.add(new SelectItem(TipoMessaggio.Risposta.name()));
		return list;
	}

	//	public OrderBy getOrderBy() {
	//		return this.orderBy;
	//	}
	//
	//	public void setOrderBy(OrderBy orderBy) {
	//		this.orderBy = orderBy;
	//	}

	public String getTipoNomeMittente() {
		return this.tipoNomeMittente;
	}

	public String getTipoNomeDestinatario() {
		return this.tipoNomeDestinatario;
	}

	public String getTipoNomeTrafficoPerSoggetto() {
		return this.tipoNomeTrafficoPerSoggetto;
	}

	public String getTipoNomeSoggettoLocale() {
		return this.tipoNomeSoggettoLocale;
	}

	public String getIntervalloRefresh() {
		return this.intervalloRefresh;
	}

	public void setIntervalloRefresh(String intervalloRefresh) {
		this.intervalloRefresh = intervalloRefresh;
	}

	public boolean isRicerchePersonalizzateAttive() {
		return this.ricerchePersonalizzateAttive;
	}

	public void setRicerchePersonalizzateAttive(boolean ricerchePersonalizzateAttive) {
		this.ricerchePersonalizzateAttive = ricerchePersonalizzateAttive;
	}

	public boolean isStatistichePersonalizzateAttive() {
		return this.statistichePersonalizzateAttive;
	}

	public void setStatistichePersonalizzateAttive(
			boolean statistichePersonalizzateAttive) {
		this.statistichePersonalizzateAttive = statistichePersonalizzateAttive;
	}

	public SortOrder getSortOrder() {
		return this.sortOrder;
	}

	public void setSortOrder(SortOrder sortOrder) {
		this.sortOrder = sortOrder;
	}

	public String getSortField() {
		return this.sortField;
	}

	public void setSortField(String sortField) {
		this.sortField = sortField;
	}

	public String getModalitaRicercaStorico() {
		if(this.modalitaRicercaStorico==null || "".equals(this.modalitaRicercaStorico)){
			this.modalitaRicercaStorico = default_modalitaRicercaStorico;
		}
		return this.modalitaRicercaStorico;
	}

	public void setModalitaRicercaStorico(String modalitaRicercaStorico) {
		this.modalitaRicercaStorico = modalitaRicercaStorico;
	}

	public String getProtocollo() {
		return this.protocollo;
	}

	public void setProtocollo(String protocollo) {
		this.protocollo = protocollo;

		if (StringUtils.isEmpty(protocollo)
				|| "*".equals(protocollo))
			this.protocollo = null;
	}

	public List<SelectItem> getProtocolli() {
		//		if(this.protocolli == null)
		this.protocolli = new ArrayList<SelectItem>();

		this.protocolli.add(new SelectItem("*"));



		try {
			List<Soggetto> listaSoggettiGestione = this.getSoggettiGestione();
			ProtocolFactoryManager pfManager = org.openspcoop2.protocol.engine.ProtocolFactoryManager.getInstance();
			MapReader<String,IProtocolFactory<?>> protocolFactories = pfManager.getProtocolFactories();	

			List<String> listaNomiProtocolli = new  ArrayList<String>();

			if(listaSoggettiGestione != null && listaSoggettiGestione.size() > 0){
				List<String> tipiSoggetti = new ArrayList<String>();
				for (Soggetto soggetto : listaSoggettiGestione) {
					String tipoSoggetto = soggetto.getTipoSoggetto();

					if(!tipiSoggetti.contains(tipoSoggetto))
						tipiSoggetti.add(tipoSoggetto); 
				}

				for (String tipo : tipiSoggetti) {
					String protocolBySubjectType = pfManager.getProtocolByOrganizationType(tipo);
					if(!listaNomiProtocolli.contains(protocolBySubjectType))
						listaNomiProtocolli.add(protocolBySubjectType);
				}

			} else {
				// Tutti i protocolli
				Enumeration<String> keys = protocolFactories.keys();
				while (keys.hasMoreElements()) {
					String protocolKey = (String) keys.nextElement();
					if(!listaNomiProtocolli.contains(protocolKey))
						listaNomiProtocolli.add(protocolKey);
				}
			}

			for (String protocolKey : listaNomiProtocolli) {
				IProtocolFactory<?> protocollo = protocolFactories.get(protocolKey);
				this.protocolli.add(new SelectItem(protocollo.getProtocol()));
			}

		} catch (ProtocolException e) {
			log.error("Si e' verificato un errore durante il caricamento della lista protocolli: " + e.getMessage(), e);
		}  


		return this.protocolli;
	}

	public boolean isShowListaProtocolli(){
		try {
			ProtocolFactoryManager pfManager = org.openspcoop2.protocol.engine.ProtocolFactoryManager.getInstance();
			MapReader<String, IProtocolFactory<?>> protocolFactories = pfManager.getProtocolFactories();	
			int numeroProtocolli = protocolFactories.size();

			// se c'e' installato un solo protocollo non visualizzo la select List.
			if(numeroProtocolli == 1)
				return false;

			List<Soggetto> listaSoggettiGestione = this.getSoggettiGestione();
			List<String> listaNomiProtocolli = new  ArrayList<String>();

			if(listaSoggettiGestione != null && listaSoggettiGestione.size() > 0){
				List<String> tipiSoggetti = new ArrayList<String>();
				for (Soggetto soggetto : listaSoggettiGestione) {
					String tipoSoggetto = soggetto.getTipoSoggetto();

					if(!tipiSoggetti.contains(tipoSoggetto))
						tipiSoggetti.add(tipoSoggetto); 
				}

				for (String tipo : tipiSoggetti) {
					String protocolBySubjectType = pfManager.getProtocolByOrganizationType(tipo);
					if(!listaNomiProtocolli.contains(protocolBySubjectType))
						listaNomiProtocolli.add(protocolBySubjectType);
				}

			} else {
				// Tutti i protocolli
				Enumeration<String> keys = protocolFactories.keys();
				while (keys.hasMoreElements()) {
					String protocolKey = (String) keys.nextElement();
					if(!listaNomiProtocolli.contains(protocolKey))
						listaNomiProtocolli.add(protocolKey);
				}
			}

			numeroProtocolli = listaNomiProtocolli.size();

			// se c'e' installato un solo protocollo non visualizzo la select List.
			if(numeroProtocolli == 1)
				return false;

		} catch (ProtocolException e) {
			log.error("Si e' verificato un errore durante il caricamento della lista protocolli: " + e.getMessage(), e);
		}  

		return true;
	}

	public void protocolloSelected(ActionEvent ae) {
		String tipoProt = this.getProtocollo();

		try{
			if(tipoProt!= null){
				// Controllo Destinatario
				String tipoErogatore = Utility.parseTipoSoggetto(this.getTipoNomeDestinatario());
				String nomeErogatore = Utility.parseNomeSoggetto(this.getTipoNomeDestinatario());

				if(nomeErogatore != null){
					if(!DynamicPdDBeanUtils.getInstance(log).isTipoSoggettoCompatibileConProtocollo(tipoErogatore, tipoProt)){ 
						this.setTipoNomeDestinatario(null);
						this.destinatarioSelected(ae);
					}
				}

				// controllo soggetto locale
				String tipoSoggettoLocale = Utility.parseTipoSoggetto(this.getTipoNomeSoggettoLocale());
				String nomeSoggettoLocale = Utility.parseNomeSoggetto(this.getTipoNomeSoggettoLocale());

				if(nomeSoggettoLocale != null){
					if(!DynamicPdDBeanUtils.getInstance(log).isTipoSoggettoCompatibileConProtocollo(tipoSoggettoLocale, tipoProt)){ 
						this.setTipoNomeSoggettoLocale(null);
						this.soggettoLocaleSelected(ae);
					}
				}

				// controllo getTipoNomeTrafficoPerSoggetto
				String tipoTrafficoPerSoggetto = Utility.parseTipoSoggetto(this.getTipoNomeTrafficoPerSoggetto());
				String nomeTrafficoPerSoggetto = Utility.parseNomeSoggetto(this.getTipoNomeTrafficoPerSoggetto());

				if(nomeTrafficoPerSoggetto != null){
					if(!DynamicPdDBeanUtils.getInstance(log).isTipoSoggettoCompatibileConProtocollo(tipoTrafficoPerSoggetto, tipoProt)){ 
						this.setTipoNomeTrafficoPerSoggetto(null);
						this.destinatarioSelected(ae);
					}
				}

				// Controllo mittente

				String nomeFruitore= Utility.parseTipoSoggetto(this.getTipoNomeMittente());
				String tipoFruitore = Utility.parseNomeSoggetto(this.getTipoNomeMittente());
				if(nomeFruitore!= null)
					if(!DynamicPdDBeanUtils.getInstance(log).isTipoSoggettoCompatibileConProtocollo(tipoFruitore, tipoProt)){
						this.setTipoNomeMittente(null);
						this.destinatarioSelected(ae);
					}

				// controllo Servizio
				String nomeServizio = null, tipoServizio = null;
				if(StringUtils.isNotEmpty(this.getNomeServizio())){
					nomeServizio = this.getNomeServizio().split(" \\(")[0];
					tipoServizio = Utility.parseTipoSoggetto(nomeServizio);
					nomeServizio = Utility.parseNomeSoggetto(nomeServizio);
				}

				if(tipoServizio != null)
					if(!DynamicPdDBeanUtils.getInstance(log).isTipoServizioCompatibileConProtocollo(tipoServizio, tipoProt)){
						this.setNomeServizio(null); 
						this.servizioSelected(ae);
					}

			}

		}catch(Exception e){
			log.error("Si e' verificato un errore durante la selezione del protocollo:" +e.getMessage(),e);
		}
		//		this.accordoServizio = null;
		//		this.setTipoNomeDestinatario(null);
		//		this.setTipoNomeMittente(null);
		//		accordoServizioSelected(ae);
	}

	public Map<String, Ordering> getSortOrders() {
		return this.sortOrders;
	}

	public void setSortOrders(Map<String, Ordering> sortOrders) {
		this.sortOrders = sortOrders;
	}

	public int getTempoMassimoRefreshLive() {
		return this.tempoMassimoRefreshLive;
	}

	public void setTempoMassimoRefreshLive(int tempoMassimoRefreshLive) {
		this.tempoMassimoRefreshLive = tempoMassimoRefreshLive;
	}

	public boolean isSessioneLiveValida() {
		if(this.getDataRicercaRaw() != null){
			Date d = new Date();
			log.debug("Controllo sessione live Attiva Da: ["+this.getDataRicercaRaw()+"] Tempo Residuo: ["+((this.tempoMassimoRefreshLive * 60000 ) - (d.getTime() - this.getDataRicercaRaw().getTime()))+"] ms"); 

			return this.getDataRicercaRaw().getTime() > (d.getTime() - (this.tempoMassimoRefreshLive * 60000 ));
		}
		return false;
	}

	public void setSessioneLiveValida(boolean sessioneLiveValida) {
		log.debug("setSessioneLiveValida"+sessioneLiveValida+"]");
		if(sessioneLiveValida == true)
			this.aggiornaNuovaDataRicerca();
	}

}
