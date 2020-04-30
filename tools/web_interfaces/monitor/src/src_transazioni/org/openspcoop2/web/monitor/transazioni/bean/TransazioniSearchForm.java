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
package org.openspcoop2.web.monitor.transazioni.bean;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.openspcoop2.core.commons.dao.DAOFactory;
import org.openspcoop2.core.commons.search.AccordoServizioParteSpecifica;
import org.openspcoop2.core.commons.search.IdAccordoServizioParteComune;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.monitor.engine.condition.EsitoUtils;
import org.openspcoop2.monitor.engine.config.ricerche.ConfigurazioneRicerca;
import org.openspcoop2.monitor.engine.dynamic.DynamicFactory;
import org.openspcoop2.monitor.engine.dynamic.IDynamicValidator;
import org.openspcoop2.monitor.sdk.condition.Context;
import org.openspcoop2.monitor.sdk.condition.IFilter;
import org.openspcoop2.monitor.sdk.constants.CRUDType;
import org.openspcoop2.monitor.sdk.constants.SearchType;
import org.openspcoop2.monitor.sdk.exceptions.SearchException;
import org.openspcoop2.monitor.sdk.exceptions.ValidationException;
import org.openspcoop2.monitor.sdk.parameters.Parameter;
import org.openspcoop2.protocol.sdk.builder.EsitoTransazione;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.web.monitor.core.bean.AbstractDateSearchForm;
import org.openspcoop2.web.monitor.core.bean.ApplicationBean;
import org.openspcoop2.web.monitor.core.bean.BaseSearchForm;
import org.openspcoop2.web.monitor.core.constants.CaseSensitiveMatch;
import org.openspcoop2.web.monitor.core.constants.Costanti;
import org.openspcoop2.web.monitor.core.constants.ModalitaRicercaTransazioni;
import org.openspcoop2.web.monitor.core.constants.TipoMatch;
import org.openspcoop2.web.monitor.core.constants.TipologiaRicerca;
import org.openspcoop2.web.monitor.core.core.PddMonitorProperties;
import org.openspcoop2.web.monitor.core.core.Utility;
import org.openspcoop2.web.monitor.core.dynamic.Ricerche;
import org.openspcoop2.web.monitor.core.dynamic.components.BaseComponent;
import org.openspcoop2.web.monitor.core.filters.BrowserFilter;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;
import org.openspcoop2.web.monitor.core.utils.BrowserInfo;
import org.openspcoop2.web.monitor.core.utils.MessageManager;
import org.openspcoop2.web.monitor.core.utils.MessageUtils;
import org.openspcoop2.web.monitor.transazioni.constants.TransazioniCostanti;
import org.openspcoop2.web.monitor.transazioni.dao.ITransazioniService;
import org.openspcoop2.web.monitor.transazioni.datamodel.TransazioniDM;
import org.richfaces.model.Ordering;
import org.slf4j.Logger;

/**
 * TransazioniSearchForm
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class TransazioniSearchForm extends BaseSearchForm implements
Context, Cloneable {

	private static Logger log =  LoggerManager.getPddMonitorCoreLogger();

	private boolean ricercaPerIdApplicativo = false;
	private ITransazioniService transazioniService;
	private String nomeStato;
	private String nomeRisorsa;
	private String valoreRisorsa;
	private String evento;
	
	private TipoMatch ricercaLiberaMatchingType = TipoMatch.LIKE;
	private CaseSensitiveMatch ricercaLiberaCaseSensitiveType = CaseSensitiveMatch.INSENSITIVE;
	
	private String ricercaLiberaSoggettoRemoto;
	private String ricercaLiberaGruppo;
	private String ricercaLiberaServizio;
	private String ricercaLiberaAzione;
	
	private String ricercaLiberaApplicativo;
	private String ricercaLiberaIdentificativoAutenticato;
	private String ricercaLiberaIndirizzoIP;
	
	private String ricercaLiberaTokenIssuer;
	private String ricercaLiberaTokenSubject;
	private String ricercaLiberaTokenClientID;
	private String ricercaLiberaTokenUsername;
	private String ricercaLiberaTokenEmail;
	
	private String ricercaLiberaIdApplicativo;
	

	private Hashtable<String, Ricerche> tabellaRicerchePersonalizzate = new Hashtable<String, Ricerche>();

	private List<Parameter<?>> ricercaSelezionataParameters = new ArrayList<Parameter<?>>();

	private boolean visualizzaIdCluster = false;
	private boolean visualizzaIdClusterAsSelectList = false;
	private List<String> listIdCluster;
	private String clusterId;

	private static String default_modalitaRicercaStorico = ModalitaRicercaTransazioni.ANDAMENTO_TEMPORALE.getValue();
	private String modalitaRicercaStorico = TransazioniSearchForm.default_modalitaRicercaStorico;
	
	private boolean integrationManagerEnabled = false;
	
	private boolean visualizzaStoricoCustomEnabled = false;
	private boolean visualizzaStoricoCustomColonnaRuoloTransazioneEnabled = false;
	private boolean visualizzaLiveCustomEnabled = false;
	private boolean visualizzaLiveCustomColonnaRuoloTransazioneEnabled = false;
	private boolean visualizzaConsegneMultipleCustomEnabled = false;
	
	public TransazioniSearchForm(){
		super();
		
		try{
			PddMonitorProperties pddMonitorProperties = PddMonitorProperties.getInstance(TransazioniSearchForm.log);
			List<String> pddMonitorare = pddMonitorProperties.getListaPdDMonitorate_StatusPdD();
			this.setVisualizzaIdCluster(pddMonitorare!=null && pddMonitorare.size()>1);
			this.visualizzaIdClusterAsSelectList = pddMonitorProperties.isAttivoTransazioniUtilizzoSondaPdDListAsClusterId();
			if(pddMonitorare!=null && pddMonitorare.size()>1){
				this.listIdCluster = new ArrayList<String>();
				this.listIdCluster.add("--");
				this.listIdCluster.addAll(pddMonitorare);
			}
			
			this.getSortOrders().put(TransazioniDM.COL_DATA_INGRESSO_RICHIESTA, Ordering.DESCENDING);
			this.getSortOrders().put(TransazioniDM.COL_DATA_LATENZA_TOTALE, Ordering.UNSORTED);
			this.getSortOrders().put(TransazioniDM.COL_DATA_LATENZA_SERVIZIO, Ordering.UNSORTED);

			this.setUseCount(pddMonitorProperties.isAttivoUtilizzaCountStoricoTransazioni()); 
			
			this.modalitaRicercaStorico = TransazioniSearchForm.default_modalitaRicercaStorico;
			
			this.integrationManagerEnabled = pddMonitorProperties.isAttivoTransazioniIntegrationManager();
			
			BrowserInfo browserInfo = ApplicationBean.getInstance().getBrowserInfo();
			
			this.visualizzaStoricoCustomEnabled = pddMonitorProperties.isAttivoUtilizzaVisualizzazioneCustomTransazioni() && BrowserFilter.abilitaVisualizzazioneTransazioniCustom(browserInfo);
			this.visualizzaStoricoCustomColonnaRuoloTransazioneEnabled = this.visualizzaStoricoCustomEnabled && pddMonitorProperties.isAttivoVisualizzaColonnaRuoloTransazioneVisualizzazioneCustomTransazioni();
			this.visualizzaLiveCustomEnabled = pddMonitorProperties.isAttivoUtilizzaVisualizzazioneCustomLive() && BrowserFilter.abilitaVisualizzazioneTransazioniCustom(browserInfo);
			this.visualizzaLiveCustomColonnaRuoloTransazioneEnabled = this.visualizzaLiveCustomEnabled && pddMonitorProperties.isAttivoVisualizzaColonnaRuoloTransazioneVisualizzazioneCustomLive();
			this.visualizzaConsegneMultipleCustomEnabled = pddMonitorProperties.isAttivoUtilizzaVisualizzazioneCustomConsegneMultiple() && BrowserFilter.abilitaVisualizzazioneTransazioniCustom(browserInfo);
			
		}catch(Exception e){
			TransazioniSearchForm.log.error(e.getMessage(), e);
		}
	}

	public TransazioniSearchForm(boolean useInBatch){
		super(useInBatch);
		
		try{
			PddMonitorProperties pddMonitorProperties = PddMonitorProperties.getInstance(TransazioniSearchForm.log);
			List<String> pddMonitorare = pddMonitorProperties.getListaPdDMonitorate_StatusPdD();
			this.setVisualizzaIdCluster(pddMonitorare!=null && pddMonitorare.size()>1);
			
			this.getSortOrders().put(TransazioniDM.COL_DATA_INGRESSO_RICHIESTA, Ordering.DESCENDING);
			this.getSortOrders().put(TransazioniDM.COL_DATA_LATENZA_TOTALE, Ordering.UNSORTED);
			this.getSortOrders().put(TransazioniDM.COL_DATA_LATENZA_SERVIZIO, Ordering.UNSORTED);
			
			this.setUseCount(pddMonitorProperties.isAttivoUtilizzaCountStoricoTransazioni()); 
			
			this.modalitaRicercaStorico = TransazioniSearchForm.default_modalitaRicercaStorico;
			
			this.integrationManagerEnabled = pddMonitorProperties.isAttivoTransazioniIntegrationManager();
			
			BrowserInfo browserInfo = ApplicationBean.getInstance().getBrowserInfo();
			
			this.visualizzaStoricoCustomEnabled = pddMonitorProperties.isAttivoUtilizzaVisualizzazioneCustomTransazioni() && BrowserFilter.abilitaVisualizzazioneTransazioniCustom(browserInfo);
			this.visualizzaStoricoCustomColonnaRuoloTransazioneEnabled = this.visualizzaStoricoCustomEnabled && pddMonitorProperties.isAttivoVisualizzaColonnaRuoloTransazioneVisualizzazioneCustomTransazioni();
			this.visualizzaLiveCustomEnabled = pddMonitorProperties.isAttivoUtilizzaVisualizzazioneCustomLive() && BrowserFilter.abilitaVisualizzazioneTransazioniCustom(browserInfo);
			this.visualizzaLiveCustomColonnaRuoloTransazioneEnabled = this.visualizzaLiveCustomEnabled && pddMonitorProperties.isAttivoVisualizzaColonnaRuoloTransazioneVisualizzazioneCustomLive();
			this.visualizzaConsegneMultipleCustomEnabled = pddMonitorProperties.isAttivoUtilizzaVisualizzazioneCustomConsegneMultiple() && BrowserFilter.abilitaVisualizzazioneTransazioniCustom(browserInfo);
			
		}catch(Exception e){
			TransazioniSearchForm.log.error(e.getMessage(), e);
		}
	}

	public List<SelectItem> getEsitiDettaglio() {
		return super.getEsitiDettaglio(false);
	}
	
	public List<SelectItem> getEsitiDettagliPersonalizzati() {
		return super.getEsitiDettagliPersonalizzati(false);
	}
	
	public boolean isIntegrationManagerEnabled() {
		return this.integrationManagerEnabled;
	}
	
	public void setTransazioniService(ITransazioniService transazioniService) {
		this.transazioniService = transazioniService;
	}

	private TipoMatch correlazioneApplicativaMatchingType = TipoMatch.EQUALS;
	private CaseSensitiveMatch correlazioneApplicativaCaseSensitiveType = CaseSensitiveMatch.SENSITIVE;

	public boolean isLive() {
		return "Live".equals(this.periodo);
	}
	
	@Override
	public String getPrintPeriodo(){
		if(this.isLive()){
			try{
				PddMonitorProperties monitorProperties = PddMonitorProperties.getInstance(TransazioniSearchForm.log);
				Integer liveUltimiGiorni = monitorProperties.getTransazioniLiveUltimiGiorni();
				Date inizio = null;
				if(liveUltimiGiorni!=null && liveUltimiGiorni!=0){
					
					int numeroGiorni = liveUltimiGiorni.intValue();
					if(numeroGiorni>0){
						// deve essere un numero negativo
						numeroGiorni = numeroGiorni * -1;
					}
					
					Calendar c = Calendar.getInstance();
					c.set(Calendar.HOUR_OF_DAY, 0);
					c.set(Calendar.MINUTE, 0);
					c.set(Calendar.SECOND, 0);
					c.set(Calendar.MILLISECOND, 0);
					c.add(Calendar.DATE, numeroGiorni);
					inizio = c.getTime();
				}
				
				Date fine = null;
				return AbstractDateSearchForm.printPeriodo(inizio, fine);
				
			}catch(Exception e){
				TransazioniSearchForm.log.error("Errore durante il calcolo dell'intervallo: "+e.getMessage(),e);
				return "";
			}
		}
		else{
			return super.getPrintPeriodo();
		}
		
	}
	
	@Override
	protected String ripulisciValori(){
		String oldModalita = this.getModalitaRicercaStorico();
		String oldRiconoscimento = this.getRiconoscimento();
		boolean oldIsLive = this.isLive();
		this.initSearchListener(null);
		if(oldModalita!=null && !"".equals(oldModalita)) {
			this.setModalitaRicercaStorico(oldModalita);
		}
		if(oldModalita!=null && !"".equals(oldModalita) && !oldIsLive) {
			this.setRiconoscimento(oldRiconoscimento);
		}
		
		if(!oldIsLive && TipologiaRicerca.all.equals(this.getDefaultTipologiaRicercaEnum())) {
			this.setTipologiaRicerca("--"); // in modo da far comparire la lista con il suggerimento di selezione come per gli altri
		}
		
		return null;
	}

	public void ripulisciLive(){
		this.ripulisci();	
		this.periodo = "Live";
		this.setTipologiaRicerca("--"); // in modo da far comparire la lista con il suggerimento di selezione come per gli altri
	}

	@Override
	public void setPeriodo(String periodo) {
		super.setPeriodo(periodo);
		if(this.isLive()) {
			this.setTipologiaRicerca("--"); // in modo da far comparire la lista con il suggerimento di selezione come per gli altri
		}
	}
	
	@Override
	public void initSearchListener(ActionEvent ae) {
		
		super.initSearchListener(ae);
				
		this.modalitaRicercaStorico = TransazioniSearchForm.default_modalitaRicercaStorico;
		
		this.getSortOrders().put(TransazioniDM.COL_DATA_INGRESSO_RICHIESTA, Ordering.DESCENDING);
		this.getSortOrders().put(TransazioniDM.COL_DATA_LATENZA_TOTALE, Ordering.UNSORTED);
		this.getSortOrders().put(TransazioniDM.COL_DATA_LATENZA_SERVIZIO, Ordering.UNSORTED);

//		this.setPeriodoDefault("Ultime 24 ore");
//		this.setPeriodo("Ultime 24 ore");
		this.setPeriodoDefault("Ultima ora");
		this.setPeriodo("Ultima ora");
		_setPeriodo();
		
		this.initSearchForm();
				
		this.correlazioneApplicativaMatchingType = TipoMatch.EQUALS;
		this.correlazioneApplicativaCaseSensitiveType = CaseSensitiveMatch.SENSITIVE;
		
		this.ricercaLiberaMatchingType = TipoMatch.LIKE;
		this.ricercaLiberaCaseSensitiveType = CaseSensitiveMatch.INSENSITIVE;
		
		this.ricercaLiberaSoggettoRemoto = null;
		this.ricercaLiberaGruppo = null;
		this.ricercaLiberaServizio = null;
		this.ricercaLiberaAzione = null;
		
		this.ricercaLiberaApplicativo = null;
		this.ricercaLiberaIdentificativoAutenticato = null;
		this.ricercaLiberaIndirizzoIP = null;
		
		this.ricercaLiberaTokenIssuer = null;
		this.ricercaLiberaTokenSubject = null;
		this.ricercaLiberaTokenClientID = null;
		this.ricercaLiberaTokenUsername = null;
		this.ricercaLiberaTokenEmail = null;
		
		this.ricercaLiberaIdApplicativo = null;
		
		
		this.executeQuery = false;
		
	}


	public String getModalitaRicercaStorico() {
		if(this.modalitaRicercaStorico==null || "".equals(this.modalitaRicercaStorico)){
			this.modalitaRicercaStorico = TransazioniSearchForm.default_modalitaRicercaStorico;
		}
		return this.modalitaRicercaStorico;
	}

	public void setModalitaRicercaStorico(String modalitaRicercaStorico) {
		this.modalitaRicercaStorico = modalitaRicercaStorico;
	}
	
	public String getTipoStoricoLabel() {
		if(this.getModalitaRicercaStorico() != null) {
			ModalitaRicercaTransazioni t = ModalitaRicercaTransazioni.getFromString(this.getModalitaRicercaStorico());
			switch (t) { 
			case ANDAMENTO_TEMPORALE:
				return MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_SEARCH_TIPO_RICERCA_TEMPORALE_RICERCA_BASE_BREADCUMP_KEY);
			case RICERCA_LIBERA:
				return MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_SEARCH_TIPO_RICERCA_TEMPORALE_RICERCA_LIBERA_BREADCUMP_KEY);
				
			case MITTENTE_TOKEN_INFO:
				return MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_SEARCH_TIPO_RICERCA_MITENTE_RICERCA_TOKEN_INFO_BREADCUMP_KEY);
			case MITTENTE_SOGGETTO:
				return MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_SEARCH_TIPO_RICERCA_MITENTE_RICERCA_SOGGETTO_BREADCUMP_KEY);
			case MITTENTE_APPLICATIVO:
				return MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_SEARCH_TIPO_RICERCA_MITENTE_RICERCA_APPLICATIVO_BREADCUMP_KEY);
			case MITTENTE_IDENTIFICATIVO_AUTENTICATO:
				return MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_SEARCH_TIPO_RICERCA_MITENTE_RICERCA_IDENTIFICATIVO_AUTENTICATO_BREADCUMP_KEY);
			case MITTENTE_INDIRIZZO_IP:
				return MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_SEARCH_TIPO_RICERCA_MITENTE_RICERCA_INDIRIZZO_IP_BREADCUMP_KEY);
				
			case ID_APPLICATIVO:
				return MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_SEARCH_TIPO_RICERCA_ID_RICERCA_ID_APPLICATIVO_BREADCUMP_KEY);
			case ID_MESSAGGIO:
				return MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_SEARCH_TIPO_RICERCA_ID_RICERCA_ID_MESSAGGIO_BREADCUMP_KEY);
			case ID_TRANSAZIONE:
			default:
				return MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_SEARCH_TIPO_RICERCA_ID_RICERCA_ID_TRANSAZIONE_BREADCUMP_KEY);
			}
		}
		
		return "Visualizza Transazioni";
	}
	
	public boolean isShowRicercaPanel() {
		if(this.getModalitaRicercaStorico() != null) {
			ModalitaRicercaTransazioni t = ModalitaRicercaTransazioni.getFromString(this.getModalitaRicercaStorico());
			switch (t) { 
			case ANDAMENTO_TEMPORALE:
			case RICERCA_LIBERA:
			case MITTENTE_TOKEN_INFO:
			case MITTENTE_SOGGETTO:
			case MITTENTE_APPLICATIVO:
			case MITTENTE_IDENTIFICATIVO_AUTENTICATO:
			case MITTENTE_INDIRIZZO_IP:
			case ID_APPLICATIVO:
				return true;
				
			case ID_MESSAGGIO:
			case ID_TRANSAZIONE:
			default:
				return false;
			}
		}
		return false;
	}
	
	public boolean isShowButtonNuovaRicerca() {
		if(!this.isLive() && this.getModalitaRicercaStorico() != null) {
			ModalitaRicercaTransazioni t = ModalitaRicercaTransazioni.getFromString(this.getModalitaRicercaStorico());
			switch (t) { 
			case ANDAMENTO_TEMPORALE:
			case RICERCA_LIBERA:
			case MITTENTE_TOKEN_INFO:
			case MITTENTE_SOGGETTO:
			case MITTENTE_APPLICATIVO:
			case MITTENTE_IDENTIFICATIVO_AUTENTICATO:
			case MITTENTE_INDIRIZZO_IP:
			case ID_APPLICATIVO:
				return true;
				
			case ID_MESSAGGIO:
			case ID_TRANSAZIONE:
			default:
				return false;
			}
		}
		return false;
	}
	
	public boolean isShowMittentePanel() {
		if(this.getModalitaRicercaStorico() != null) {
			ModalitaRicercaTransazioni t = ModalitaRicercaTransazioni.getFromString(this.getModalitaRicercaStorico());
			switch (t) { 
			case ANDAMENTO_TEMPORALE:
			case RICERCA_LIBERA:
				return false;
				
			case MITTENTE_TOKEN_INFO:
			case MITTENTE_SOGGETTO:
			case MITTENTE_APPLICATIVO:
			case MITTENTE_IDENTIFICATIVO_AUTENTICATO:
			case MITTENTE_INDIRIZZO_IP:
				return true;
				
			case ID_APPLICATIVO:
			case ID_MESSAGGIO:
			case ID_TRANSAZIONE:
			default:
				return false;
			}
		}
		return false;
	}
	
	@Override
	protected boolean isTipologiaRicercaEntrambiEnabled() {
		if(this.isLive()) {
			//return super.isTipologiaRicercaEntrambiEnabled();
			return true; // sul live si fa vedere anche la possibilità di avere erogazioni/fruizioni
		}
		else {
			if(this.getModalitaRicercaStorico() != null) {
				ModalitaRicercaTransazioni t = ModalitaRicercaTransazioni.getFromString(this.getModalitaRicercaStorico());
				switch (t) { 
				case ANDAMENTO_TEMPORALE:
				case RICERCA_LIBERA:
				case MITTENTE_IDENTIFICATIVO_AUTENTICATO:
				case MITTENTE_INDIRIZZO_IP:
				case ID_APPLICATIVO:
					return true; // non c'e' motivo per non farli vedere
				
				case MITTENTE_TOKEN_INFO: // nelle erogazioni vi è anche il soggetto mittente
				case MITTENTE_SOGGETTO: // solo per erogazioni
				case MITTENTE_APPLICATIVO:
					return super.isTipologiaRicercaEntrambiEnabled();

				case ID_MESSAGGIO:
				case ID_TRANSAZIONE:
					return super.isTipologiaRicercaEntrambiEnabled(); // non viene visualizzata l'informazione
				}
			}
			return super.isTipologiaRicercaEntrambiEnabled();
		}
	}
	
	@Override
	public List<SelectItem> getTipologieRicerca() throws Exception {
		
		if(this.isLive()) {
			return super.getTipologieRicerca();
		}
		if(this.getModalitaRicercaStorico() == null) {
			return super.getTipologieRicerca();
		}
		ModalitaRicercaTransazioni t = ModalitaRicercaTransazioni.getFromString(this.getModalitaRicercaStorico());
		if(ModalitaRicercaTransazioni.MITTENTE_SOGGETTO.equals(t)==false) {
			return super.getTipologieRicerca();
		}
		
		List<SelectItem> listaTipologie = new ArrayList<SelectItem>();
		listaTipologie.add(new SelectItem(TipologiaRicerca.ingresso.toString(),"Erogazione"));
		return listaTipologie;
	}
	
	
	public void setRicercaPerIdApplicativo(boolean ricercaPerIdApplicativo) {
		this.ricercaPerIdApplicativo = ricercaPerIdApplicativo;
	}

	public boolean getRicercaPerIdApplicativo() {
		return this.ricercaPerIdApplicativo;
	}

	public void ricercaPerIDApplicativoSelected(ActionEvent ae) {
		super.setIdCorrelazioneApplicativa(null);
	}

	private Hashtable<String, Ricerche> leggiRicerche() {

		try {
			if (this.tabellaRicerchePersonalizzate != null && this.tabellaRicerchePersonalizzate.size() > 0)
				return this.tabellaRicerchePersonalizzate;
			
			IDServizio idServizio = Utility.parseServizioSoggetto(this.getNomeServizio());
			String nomeServizio = idServizio.getNome();
			AccordoServizioParteSpecifica aspsFromValues = this.getAspsFromNomeServizio(idServizio);

			IdAccordoServizioParteComune idAccordoServizioParteComune = aspsFromValues.getIdAccordoServizioParteComune();
			Integer ver = idAccordoServizioParteComune.getVersione();
			String nomeSoggettoReferente = null;
			String tipoSoggettoReferente = null;

			if(idAccordoServizioParteComune.getIdSoggetto() != null){
				nomeSoggettoReferente=	idAccordoServizioParteComune.getIdSoggetto().getNome();
				tipoSoggettoReferente=	idAccordoServizioParteComune.getIdSoggetto().getTipo();
			}

			String nomeAS = idAccordoServizioParteComune.getNome(); 

			IDAccordo idAccordo = IDAccordoFactory.getInstance().getIDAccordoFromValues(nomeAS, tipoSoggettoReferente, nomeSoggettoReferente, ver);

			// recupero le statistiche personalizzate dal db
			//			String p = this.getNomeServizio().split("\\(")[1];
			//			String uri = p.replace(")", "");
			//			IDAccordo idAccordo = IDAccordoFactory.getInstance()
			//					.getIDAccordoFromUri(uri);
			//
			//			String nomeServizio = this.getNomeServizio().split("\\(")[0];

			String nomeServizioKey = aspsFromValues.getPortType() != null ? aspsFromValues.getPortType() : nomeServizio;

			List<ConfigurazioneRicerca> l = this.transazioniService.getRicercheByValues(idAccordo, nomeServizioKey, this.getNomeAzione());

			Ricerche ricerche = new Ricerche();
			if (l != null && l.size() > 0) {
				for (ConfigurazioneRicerca r : l) {
					ricerche.addRicerca(r);
				}
				this.tabellaRicerchePersonalizzate.put(nomeServizioKey, ricerche);
			}

		} catch (Exception e) {
			TransazioniSearchForm.log.error(e.getMessage(), e);
		}
		return this.tabellaRicerchePersonalizzate;
	}

	public void validateSelectedPlugin(FacesContext context,
			UIComponent component, Object value) {

		if (value != null) {
			String nomeRicerca = (String) value;
			ConfigurazioneRicerca r = this.getRicerchePersonalizzate()
					.getRicercaByLabel(nomeRicerca);

			try {
				if (r != null){
					DynamicFactory.getInstance().newDynamicLoader(r.getPlugin().getClassName(), TransazioniSearchForm.log);
					//Class.forName(r.getPlugin().getClassName());
				}
			//} catch (ClassNotFoundException e) {
			} catch (SearchException e) {
				// throw new ValidatorException(new
				// FacesMessage("Impossibile caricare il plugin. La classe indicata ["+r.getClassName()+"] non esiste."));
				String msg = "Impossibile selezionare la ricerca ("
						+ nomeRicerca + "). La classe indicata ["
						+ r.getPlugin().getClassName() + "] non esiste.";
				((UIInput) component).setValid(false);
				context.addMessage(
						component.getClientId(context),
						new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, null));
			}
		}

	}

	public List<Parameter<?>> getRicercaSelezionataParameters() {
		if (this.ricercaSelezionataParameters != null
				&& this.ricercaSelezionataParameters.size() > 0)
			return this.ricercaSelezionataParameters;

		try {
			if (this.getRicercaSelezionata() != null) {
				ConfigurazioneRicerca r = this.getRicercaSelezionata();

				this.ricercaSelezionataParameters = this.transazioniService.instanceParameters(r,this);
				if (this.ricercaSelezionataParameters != null) {
					for (Parameter<?> searchParam : this.ricercaSelezionataParameters) {
						((BaseComponent<?>) searchParam).setContext(this);
						//						r.getConfigurazioneRicercaParametroList().add(param);
					}
				}
				// aggiorno il riferimento ai parametri nella ricerca
				// r.setParameters(this.ricercaSelezionataParameters);
			}

		} catch (SearchException e) {
			MessageUtils.addErrorMsg(e.getMessage());
			TransazioniSearchForm.log.error(e.getMessage(), e);
		} catch (Exception e) {
			TransazioniSearchForm.log.error(e.getMessage(), e);
		}
		return this.ricercaSelezionataParameters;
	}
	
	public boolean isRicercaSelezionataParametersRequired(){
		if (this.ricercaSelezionataParameters != null
				&& this.ricercaSelezionataParameters.size() > 0){
			try {
				for (Parameter<?> searchParam : this.ricercaSelezionataParameters) {
					if(searchParam.getRendering().isRequired()){
						return true;
					}
				}
			} catch (Exception e) {
				TransazioniSearchForm.log.error(e.getMessage(), e);
			}
		}
		return false;
	}

	public void initSearchForm() {
		this.tabellaRicerchePersonalizzate = new Hashtable<String, Ricerche>();
		this.ricercaSelezionataParameters = new ArrayList<Parameter<?>>();
		this.setRicerchePersonalizzate(null);
		this.setRicercaSelezionata(null);
		this.setFiltro(null);
		this.setNomeRicercaPersonalizzata(null);
		this.nomeRisorsa = null;
		this.valoreRisorsa = null;
		this.nomeStato = null;
		this.evento = null;
		this.clusterId = null;
	}

	@Override
	public void tipologiaRicercaListener(ActionEvent ae) {

		super.tipologiaRicercaListener(ae);

		this.setRicerchePersonalizzate(null);
		this.setNomeRicercaPersonalizzata(null);
		this.setFiltro(null);
		this.nomeStato = null;
		this.nomeRisorsa = null;
		this.valoreRisorsa = null;
		this.evento = null;
		
		if(!this.isLive()){
			this.updateRiconoscimentoByModalitaRicercaStorico();
		}
	}

	public void updateRiconoscimentoByModalitaRicercaStorico(){
		ModalitaRicercaTransazioni t = ModalitaRicercaTransazioni.getFromString(this.getModalitaRicercaStorico());
		switch (t) { 
		case MITTENTE_TOKEN_INFO:
			this.setRiconoscimento(Costanti.VALUE_TIPO_RICONOSCIMENTO_TOKEN_INFO);
			break;
		case MITTENTE_SOGGETTO:
			this.setRiconoscimento(Costanti.VALUE_TIPO_RICONOSCIMENTO_SOGGETTO);
			break;
		case MITTENTE_APPLICATIVO:
			this.setRiconoscimento(Costanti.VALUE_TIPO_RICONOSCIMENTO_APPLICATIVO);
			break;
		case MITTENTE_IDENTIFICATIVO_AUTENTICATO:
			this.setRiconoscimento(Costanti.VALUE_TIPO_RICONOSCIMENTO_IDENTIFICATIVO_AUTENTICATO);
			break;
		case MITTENTE_INDIRIZZO_IP:
			this.setRiconoscimento(Costanti.VALUE_TIPO_RICONOSCIMENTO_INDIRIZZO_IP);
			break;
		default:
			break;
		}
	}
	
	@Override
	public void periodoListener(ActionEvent ae) {
		super.periodoListener(ae);
	}

	@Override
	public void servizioSelected(ActionEvent ae) {
		try {
			super.servizioSelected(ae);
			this.nomeRisorsa = null;
			this.valoreRisorsa = null;
			this.nomeStato = null;
			this.tabellaRicerchePersonalizzate = new Hashtable<String, Ricerche>();

			if(this.isRicerchePersonalizzateAttive()){
				this.ricercaSelezionataParameters = new ArrayList<Parameter<?>>();
				this.setRicercaSelezionata(null);
				this.setRicerchePersonalizzate(null);

				this.setFiltro(null);
				
				IDServizio idServizio = Utility.parseServizioSoggetto(this.getNomeServizio());
				String nomeServizio = idServizio.getNome();
				AccordoServizioParteSpecifica aspsFromValues = this.getAspsFromNomeServizio(idServizio);

				String nomeServizioKey = aspsFromValues.getPortType() != null ? aspsFromValues.getPortType() : nomeServizio;

				Ricerche r = leggiRicerche().get(nomeServizioKey);

				// setto il contesto per le ricerche
				if (r != null) {
					List<ConfigurazioneRicerca> ricerche = r.getRicerche();
					for (ConfigurazioneRicerca ricerca : ricerche) {
						List<Parameter<?>> params = null;
						try {
							params =this.transazioniService
									.instanceParameters(ricerca,this);
							if(params != null && params.size() > 0)
								for (Parameter<?> searchParam : params) {
									((BaseComponent<?>) searchParam).setContext(this);
								}
						} catch (SearchException e) {
							TransazioniSearchForm.log.error(e.getMessage(), e);
						}
					}
				}

				this.setRicerchePersonalizzate(r);
			}
		} catch (Exception e) {
			TransazioniSearchForm.log.error(e.getMessage(), e);
		}

	}

	@Override
	public void azioneSelected(ActionEvent ae) {
		super.azioneSelected(ae);
		this.nomeRisorsa = null;
		this.valoreRisorsa = null;
		this.nomeStato = null;
		this.tabellaRicerchePersonalizzate = new Hashtable<String, Ricerche>();
		try{
			// String uri =
			// this.getNomeServizio().split(" \\(")[1].replace("\\)","");
			// IDAccordo idAccordo = IDAccordo.getIDAccordoFromUri(uri);
			// String nomeAccordo = idAccordo.getNome();
			if(this.isRicerchePersonalizzateAttive()){
				
				IDServizio idServizio = Utility.parseServizioSoggetto(this.getNomeServizio());
				String nomeServizio = idServizio.getNome();
				AccordoServizioParteSpecifica aspsFromValues = this.getAspsFromNomeServizio(idServizio);
				
				String nomeServizioKey = aspsFromValues.getPortType() != null ? aspsFromValues.getPortType() : nomeServizio;

				Ricerche r = leggiRicerche().get(nomeServizioKey);
				// setto il contesto per le ricerche
				if (r != null) {
					List<ConfigurazioneRicerca> ricerche = r.getRicerche();
					for (ConfigurazioneRicerca ricerca : ricerche) {
						List<Parameter<?>> params = null;
						try {
							params = this.transazioniService.instanceParameters(
									ricerca,this);
							if(params != null && params.size() > 0)
								for (Parameter<?> searchParam : params) {
									((BaseComponent<?>) searchParam).setContext(this);
								}
						} catch (SearchException e) {
							TransazioniSearchForm.log.error(e.getMessage(), e);
						}

					}
				}
				this.setRicerchePersonalizzate(r);
			}
		} catch (Exception e) {
			TransazioniSearchForm.log.error(e.getMessage(), e);
		}
	}

	private String oldNomeStato;
	public void statoSelected(ActionEvent ae) {
		if(this.nomeStato!=null){
			if(this.oldNomeStato!=null){
				if(this.nomeStato.equals(this.oldNomeStato)==false){
					this.nomeRisorsa = null;
					this.valoreRisorsa = null;
					this.oldNomeStato = this.nomeStato;
				}
			}
			else{
				this.nomeRisorsa = null;
				this.valoreRisorsa = null;
				this.oldNomeStato = this.nomeStato;
			}
		}
		else{
			if(this.oldNomeStato!=null){
				this.nomeRisorsa = null;
				this.valoreRisorsa = null;
				this.oldNomeStato = this.nomeStato;
			}
		}
	}

	private String oldNomeRisorsa;
	public void risorsaSelected(ActionEvent ae) {
		if(this.nomeRisorsa!=null){
			if(this.oldNomeRisorsa!=null){
				if(this.nomeRisorsa.equals(this.oldNomeRisorsa)==false){
					this.valoreRisorsa=null;
					this.oldNomeRisorsa = this.nomeRisorsa;
				}
			}
			else{
				this.oldNomeRisorsa = this.nomeRisorsa;
			}
		}
	}

	@Override
	protected String eseguiAggiorna() {
		this.setAggiornamentoDatiAbilitato(true); // abilito aggiornamento
		return this._filtra(true); 
	}
	
	
	@Override
	protected String eseguiFiltra() {
		this.setAggiornamentoDatiAbilitato(true); // abilito aggiornamento
		return this._filtra(false);
	}
	
	private String _filtra(boolean mantieniDataRicerca) {

		try {
						
			// reset ordinamenti
			this.getSortOrders().put(TransazioniDM.COL_DATA_INGRESSO_RICHIESTA, Ordering.DESCENDING);
			this.getSortOrders().put(TransazioniDM.COL_DATA_LATENZA_TOTALE, Ordering.UNSORTED);
			this.getSortOrders().put(TransazioniDM.COL_DATA_LATENZA_SERVIZIO, Ordering.UNSORTED);

			// Tipo di periodo selezionato 'Personalizzato'
			if(this.getPeriodo().equals("Personalizzato")){
				if(this.getDataInizio() == null){
					MessageUtils.addErrorMsg("Selezionare Data Inizio");
					return null;
				}

				if(this.getDataFine() == null){
					MessageUtils.addErrorMsg("Selezionare Data Fine");
					return null;
				}
			}

			if(!this.isLive()){
				ModalitaRicercaTransazioni ricerca = ModalitaRicercaTransazioni.getFromString(this.getModalitaRicercaStorico());
				if(ricerca!=null){
					switch (ricerca) {
					case ID_APPLICATIVO:
						if(org.apache.commons.lang.StringUtils.isEmpty(this.getIdCorrelazioneApplicativa())){
							MessageUtils.addErrorMsg("Indicare un identificativo applicativo");
							return null;
						}
						break;
					case ID_MESSAGGIO:
						if(org.apache.commons.lang.StringUtils.isEmpty(this.getIdEgov())){
							MessageUtils.addErrorMsg("Indicare un identificativo messaggio");
							return null;
						}
						break;
					case ID_TRANSAZIONE:
						if(org.apache.commons.lang.StringUtils.isEmpty(this.getIdTransazione())){
							MessageUtils.addErrorMsg("Indicare un identificativo transazione");
							return null;
						}
						break;
					default:
						break;
					}
				}
			}
			
			boolean sezioneDatiMittente = this.validaSezioneDatiMittente();
			
			if(!sezioneDatiMittente)
				return null;
			
			
			if(this.getEsitoGruppo()!=null && (EsitoUtils.ALL_PERSONALIZZATO_VALUE == this.getEsitoGruppo())){
				if(this.getEsitoDettaglioPersonalizzato()==null || this.getEsitoDettaglioPersonalizzato().length<=0){
					MessageUtils.addErrorMsg("Selezionare almeno un esito di dettaglio");
					return null;
				}
			}
			
			
			// Date
			if(mantieniDataRicerca == false){
				//System.out.println("Reset dataRicerca");
				this.aggiornaNuovaDataRicerca();
				this._setPeriodo();
			}
			else{
				// La funzionalità di non vedere nuove transazioni e' garantita dalla data ricerca, nel caso si utilizzi il pulsante 'aggiorna'.
				// In questo caso sicuramente non si vedranno 'nuove transazioni'.
				// In più il calcolo dell'intervallo delle due date data inizio e fine viene ricalcolato solamente se viene cambiato l'intervallo
				// dove in tal caso viene scaturito il cambio di periodo tramite l'invocazione _setPeriodo
				this.congelaDataRicerca();
			}
				
			
			if(org.apache.commons.lang.StringUtils.isNotEmpty(this.nomeRisorsa)){
				if(org.apache.commons.lang.StringUtils.isEmpty(this.valoreRisorsa)){
					MessageUtils.addErrorMsg("Indicare un valore per la risorsa '"+this.nomeRisorsa+"'");
					return null;
				}
			}
			
			
			// Controlli su ricerca personalizzata
			
			if (this.getRicercaSelezionata() == null)
				return null;

			IDynamicValidator bv = DynamicFactory.getInstance().newDynamicValidator(this.getRicercaSelezionata().getPlugin().getClassName(),TransazioniSearchForm.log);

			bv.validate(this);

			// procedi al filtro
			// .... il filtro viene utilizzato dal datamodel per filtrare la
			// query
			// imposteri il filtro nel baseform in modo che il service con una
			// get
			// lo recuperi e lo utilizzi
			org.openspcoop2.monitor.engine.dynamic.IDynamicFilter bf = 
					DynamicFactory.getInstance().newDynamicFilter(this.getRicercaSelezionata().getPlugin().getClassName(),TransazioniSearchForm.log);
			IFilter f = bf.createConditionFilter(this);
			if (f != null) {
				this.setFiltro((f));
			}

			
		} catch (SearchException re) {
			MessageUtils.addErrorMsg(re.getMessage());
			return null;
		} catch (ValidationException e) {

			MessageUtils.addErrorMsg(e.getMessage());

			Map<String, String> errors = e.getErrors();
			if (errors != null) {
				Set<String> keys = errors.keySet();

				for (String key : keys) {

					Parameter<?> sp = this.getParameter(key);

					String errorMsg = errors.get(key);

					// recupero il label del parametro
					String label = sp != null ? sp.getRendering().getLabel() : key;

					MessageUtils.addErrorMsg(label + ": " + errorMsg);

				}
			}

			return null;
		}

		return null;
	}

	@Override
	public String getAzione() {

		return this.getNomeAzione();
	}

	@Override
	public EsitoTransazione getEsitoTransazione() {
		if(EsitoUtils.ALL_VALUE != this.getEsitoDettaglio()){
			try{
				return EsitiProperties.getInstance(this.getLogger(),this.getProtocollo()).convertToEsitoTransazione( this.getEsitoDettaglio(), this.getEsitoContesto());
			}catch(Exception e){
				throw new RuntimeException(e.getMessage(),e);
			}
		}
		return null;
	}

	@Override
	public Date getIntervalloInferiore() {

		return this.getDataInizio();
	}

	@Override
	public Date getIntervalloSuperiore() {

		return this.getDataFine();
	}

	@Override
	public Parameter<?> getParameter(String paramID) {
		return this.getParameters().get(paramID);
	}

	@Override
	public Map<String, Parameter<?>> getParameters() {
		Map<String, Parameter<?>> map = new TreeMap<String, Parameter<?>>();

		if(this.getRicercaSelezionataParameters() != null){
			for (Parameter<?> param : this.getRicercaSelezionataParameters()) {
				map.put(param.getId(), param);
			}
		}

		//		if(this.getRicercaSelezionata() != null){
		//			for (ConfigurazioneParametro param : this.getRicercaSelezionata()
		//					.getConfigurazioneRicercaParametroList()) {
		//				try {
		//					map.put(param.getIdParametro(), RicercheParametersUtils.getComponent(
		//							param, this.getRicercaSelezionata().getClassName()));
		//				} catch (Exception e) {
		//					TransazioniSearchForm.log.error(e.getMessage(), e);
		//				}
		//
		//			}
		//		}
		return map;

	}

	@Override
	public String getTipoServizio() {
		return this.estraiTipoServizioDalServizio();
	}
	@Override
	public String getServizio() {
		return this.estraiNomeServizioDalServizio();
	}
	
	@Override
	public Integer getVersioneServizio() {
		return this.estraiVersioneServizioDalServizio();
	}

	@Override
	public String getTipoSoggettoDestinatario() {
		return this.getTipoDestinatario();
	}
	@Override
	public String getSoggettoDestinatario() {
		return this.getNomeDestinatario();
	}

	@Override
	public String getTipoSoggettoMittente() {
		return this.getTipoMittente();
	}
	@Override
	public String getSoggettoMittente() {
		return this.getNomeMittente();
	}

	@Override
	public SearchType getTipoRicerca() {
		if(this.getTipologiaRicercaEnum() != null)
			switch (this.getTipologiaRicercaEnum()) {
			case ingresso:
				return SearchType.EROGAZIONE;
			case uscita:
				return SearchType.FRUIZIONE;
			case all:
			default:
				return SearchType.ALL;
			}
		return SearchType.ALL;
	}

	public String getNomeStato() {
		return this.nomeStato;
	}

	public String getNomeRisorsa() {
		return this.nomeRisorsa;
	}

	public void setNomeStato(String nomeStato) {
		if (nomeStato != null && "--".equals(nomeStato))
			nomeStato = null;

		this.nomeStato = nomeStato;
	}

	public void setNomeRisorsa(String nomeRisorsa) {
		if (nomeRisorsa != null && "--".equals(nomeRisorsa))
			nomeRisorsa = null;

		this.nomeRisorsa = nomeRisorsa;
	}

	public String getValoreRisorsa() {
		return this.valoreRisorsa;
	}

	public void setValoreRisorsa(String valoreRisorsa) {
		this.valoreRisorsa = valoreRisorsa;
	}

	@Override
	public void ricercaSelezionataListener(ActionEvent ae) {
		super.ricercaSelezionataListener(ae);
		this.ricercaSelezionataParameters = new ArrayList<Parameter<?>>();
		// devo resettare anche il filtro
		this.setFiltro(null); 
	}

	public String getCorrelazioneApplicativaMatchingType() {
		if(this.correlazioneApplicativaMatchingType!=null)
			return this.correlazioneApplicativaMatchingType.name();
		return null;
	}

	public void setCorrelazioneApplicativaMatchingType(
			String correlazioneApplicativaMatchingType) {
		if(correlazioneApplicativaMatchingType!=null){
			this.correlazioneApplicativaMatchingType = TipoMatch.valueOf(correlazioneApplicativaMatchingType);
		}
	}
	
	public String getCorrelazioneApplicativaCaseSensitiveType() {
		if(this.correlazioneApplicativaCaseSensitiveType!=null)
			return this.correlazioneApplicativaCaseSensitiveType.name();
		return null;
	}

	public void setCorrelazioneApplicativaCaseSensitiveType(
			String correlazioneApplicativaCaseSensitiveType) {
		if(correlazioneApplicativaCaseSensitiveType!=null){
			this.correlazioneApplicativaCaseSensitiveType = CaseSensitiveMatch.valueOf(correlazioneApplicativaCaseSensitiveType);
		}
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	public String getEvento() {
		return this.evento;
	}

	public void setEvento(String evento) {
		this.evento = evento;
	}

	@Override
	public TipiDatabase getDatabaseType() {
		return _getTipoDatabase(org.openspcoop2.core.transazioni.utils.ProjectInfo.getInstance());
	}

	@Override
	public Logger getLogger() {
		return LoggerManager.getPddMonitorCoreLogger();
	}

	@Override
	public DAOFactory getDAOFactory() {
		try{
			return DAOFactory.getInstance(LoggerManager.getPddMonitorSqlLogger());
		}catch(Exception e){
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	
	@Override
	public CRUDType getTipoOperazione() {
		return CRUDType.SEARCH;
	}
	
	public boolean isVisualizzaIdCluster() {
		return this.visualizzaIdCluster;
	}

	public void setVisualizzaIdCluster(boolean visualizzaIdCluster) {
		this.visualizzaIdCluster = visualizzaIdCluster;
	}
	
	public boolean isVisualizzaIdClusterAsSelectList() {
		return this.visualizzaIdClusterAsSelectList;
	}

	public void setVisualizzaIdClusterAsSelectList(boolean visualizzaIdClusterAsSelectList) {
		this.visualizzaIdClusterAsSelectList = visualizzaIdClusterAsSelectList;
	}
	
	public List<SelectItem> getListIdCluster() {
		ArrayList<SelectItem> list = new ArrayList<SelectItem>();
		if(this.listIdCluster!=null && this.listIdCluster.size()>0){
			for (String id : this.listIdCluster) {
				list.add(new SelectItem(id));
			}
		}
		return list;
	}


	public String getClusterId() {
		if("--".equals(this.clusterId)){
			return null;
		}
		return this.clusterId;
	}

	public void setClusterId(String clusterId) {
		if("--".equals(clusterId)){
			this.clusterId = null;	
		}
		else{
			this.clusterId = clusterId;		
		}
	}
	
	public String getRicercaLiberaMatchingType() {
		if(this.ricercaLiberaMatchingType!=null)
			return this.ricercaLiberaMatchingType.name();
		return null;
	}

	public void setRicercaLiberaMatchingType(
			String ricercaLiberaMatchingType) {
		if(ricercaLiberaMatchingType!=null){
			this.ricercaLiberaMatchingType = TipoMatch.valueOf(ricercaLiberaMatchingType);
		}
	}
	
	public String getRicercaLiberaCaseSensitiveType() {
		if(this.ricercaLiberaCaseSensitiveType!=null)
			return this.ricercaLiberaCaseSensitiveType.name();
		return null;
	}

	public void setRicercaLiberaCaseSensitiveType(
			String ricercaLiberaCaseSensitiveType) {
		if(ricercaLiberaCaseSensitiveType!=null){
			this.ricercaLiberaCaseSensitiveType = CaseSensitiveMatch.valueOf(ricercaLiberaCaseSensitiveType);
		}
	}
	
	public String getRicercaLiberaSoggettoRemoto() {
		return this.ricercaLiberaSoggettoRemoto;
	}

	public void setRicercaLiberaSoggettoRemoto(String ricercaLiberaSoggettoRemoto) {
		this.ricercaLiberaSoggettoRemoto = ricercaLiberaSoggettoRemoto;
	}

	public String getRicercaLiberaGruppo() {
		return this.ricercaLiberaGruppo;
	}

	public void setRicercaLiberaGruppo(String ricercaLiberaGruppo) {
		this.ricercaLiberaGruppo = ricercaLiberaGruppo;
	}

	public String getRicercaLiberaServizio() {
		return this.ricercaLiberaServizio;
	}

	public void setRicercaLiberaServizio(String ricercaLiberaServizio) {
		this.ricercaLiberaServizio = ricercaLiberaServizio;
	}

	public String getRicercaLiberaAzione() {
		return this.ricercaLiberaAzione;
	}

	public void setRicercaLiberaAzione(String ricercaLiberaAzione) {
		this.ricercaLiberaAzione = ricercaLiberaAzione;
	}
	
	public String getRicercaLiberaApplicativo() {
		return this.ricercaLiberaApplicativo;
	}

	public void setRicercaLiberaApplicativo(String ricercaLiberaApplicativo) {
		this.ricercaLiberaApplicativo = ricercaLiberaApplicativo;
	}

	public String getRicercaLiberaIdentificativoAutenticato() {
		return this.ricercaLiberaIdentificativoAutenticato;
	}

	public void setRicercaLiberaIdentificativoAutenticato(String ricercaLiberaIdentificativoAutenticato) {
		this.ricercaLiberaIdentificativoAutenticato = ricercaLiberaIdentificativoAutenticato;
	}

	public String getRicercaLiberaIndirizzoIP() {
		return this.ricercaLiberaIndirizzoIP;
	}

	public void setRicercaLiberaIndirizzoIP(String ricercaLiberaIndirizzoIP) {
		this.ricercaLiberaIndirizzoIP = ricercaLiberaIndirizzoIP;
	}

	public String getRicercaLiberaTokenIssuer() {
		return this.ricercaLiberaTokenIssuer;
	}

	public void setRicercaLiberaTokenIssuer(String ricercaLiberaTokenIssuer) {
		this.ricercaLiberaTokenIssuer = ricercaLiberaTokenIssuer;
	}

	public String getRicercaLiberaTokenSubject() {
		return this.ricercaLiberaTokenSubject;
	}

	public void setRicercaLiberaTokenSubject(String ricercaLiberaTokenSubject) {
		this.ricercaLiberaTokenSubject = ricercaLiberaTokenSubject;
	}

	public String getRicercaLiberaTokenClientID() {
		return this.ricercaLiberaTokenClientID;
	}

	public void setRicercaLiberaTokenClientID(String ricercaLiberaTokenClientID) {
		this.ricercaLiberaTokenClientID = ricercaLiberaTokenClientID;
	}

	public String getRicercaLiberaTokenUsername() {
		return this.ricercaLiberaTokenUsername;
	}

	public void setRicercaLiberaTokenUsername(String ricercaLiberaTokenUsername) {
		this.ricercaLiberaTokenUsername = ricercaLiberaTokenUsername;
	}

	public String getRicercaLiberaTokenEmail() {
		return this.ricercaLiberaTokenEmail;
	}

	public void setRicercaLiberaTokenEmail(String ricercaLiberaTokenEmail) {
		this.ricercaLiberaTokenEmail = ricercaLiberaTokenEmail;
	}

	public String getRicercaLiberaIdApplicativo() {
		return this.ricercaLiberaIdApplicativo;
	}

	public void setRicercaLiberaIdApplicativo(String ricercaLiberaIdApplicativo) {
		this.ricercaLiberaIdApplicativo = ricercaLiberaIdApplicativo;
	}

	public boolean isVisualizzaStoricoCustomEnabled() {
		return this.visualizzaStoricoCustomEnabled;
	}

	public void setVisualizzaStoricoCustomEnabled(boolean visualizzaStoricoCustomEnabled) {
		this.visualizzaStoricoCustomEnabled = visualizzaStoricoCustomEnabled;
	}

	public boolean isVisualizzaLiveCustomEnabled() {
		return this.visualizzaLiveCustomEnabled;
	}

	public void setVisualizzaLiveCustomEnabled(boolean visualizzaLiveCustomEnabled) {
		this.visualizzaLiveCustomEnabled = visualizzaLiveCustomEnabled;
	}

	public boolean isVisualizzaConsegneMultipleCustomEnabled() {
		return this.visualizzaConsegneMultipleCustomEnabled;
	}

	public void setVisualizzaConsegneMultipleCustomEnabled(boolean visualizzaConsegneMultipleCustomEnabled) {
		this.visualizzaConsegneMultipleCustomEnabled = visualizzaConsegneMultipleCustomEnabled;
	}

	public boolean isVisualizzaStoricoCustomColonnaRuoloTransazioneEnabled() {
		return this.visualizzaStoricoCustomColonnaRuoloTransazioneEnabled;
	}

	public void setVisualizzaStoricoCustomColonnaRuoloTransazioneEnabled(boolean visualizzaStoricoCustomColonnaRuoloTransazioneEnabled) {
		this.visualizzaStoricoCustomColonnaRuoloTransazioneEnabled = visualizzaStoricoCustomColonnaRuoloTransazioneEnabled;
	}

	public boolean isVisualizzaLiveCustomColonnaRuoloTransazioneEnabled() {
		return this.visualizzaLiveCustomColonnaRuoloTransazioneEnabled;
	}

	public void setVisualizzaLiveCustomColonnaRuoloTransazioneEnabled(boolean visualizzaLiveCustomColonnaRuoloTransazioneEnabled) {
		this.visualizzaLiveCustomColonnaRuoloTransazioneEnabled = visualizzaLiveCustomColonnaRuoloTransazioneEnabled;
	}
	
	@Override
	protected void initStatoFiltroRicerca() {
		if(this.isLive()) {
			this.setVisualizzaFiltroAperto(false);
		} else {
			super.initStatoFiltroRicerca();
		}
	}
	
}
