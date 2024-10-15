/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
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
package org.openspcoop2.web.monitor.statistiche.bean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.statistiche.constants.TipoBanda;
import org.openspcoop2.core.statistiche.constants.TipoLatenza;
import org.openspcoop2.core.statistiche.constants.TipoReport;
import org.openspcoop2.core.statistiche.constants.TipoStatistica;
import org.openspcoop2.core.statistiche.constants.TipoVisualizzazione;
import org.openspcoop2.core.transazioni.utils.TipoCredenzialeMittente;
import org.openspcoop2.generic_project.expression.SortOrder;
import org.openspcoop2.monitor.engine.condition.EsitoUtils;
import org.openspcoop2.monitor.sdk.constants.StatisticType;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.openspcoop2.utils.SortedMap;
import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.web.monitor.core.bean.AbstractDateSearchForm;
import org.openspcoop2.web.monitor.core.bean.ApplicationBean;
import org.openspcoop2.web.monitor.core.bean.BaseSearchForm;
import org.openspcoop2.web.monitor.core.constants.Costanti;
import org.openspcoop2.web.monitor.core.constants.TipologiaRicerca;
import org.openspcoop2.web.monitor.core.core.PddMonitorProperties;
import org.openspcoop2.web.monitor.core.filters.BrowserFilter;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;
import org.openspcoop2.web.monitor.core.ricerche.ModuloRicerca;
import org.openspcoop2.web.monitor.core.utils.BrowserInfo;
import org.openspcoop2.web.monitor.core.utils.MessageManager;
import org.openspcoop2.web.monitor.core.utils.MessageUtils;
import org.openspcoop2.web.monitor.statistiche.constants.CostantiGrafici;
import org.openspcoop2.web.monitor.statistiche.constants.StatisticheCostanti;
import org.openspcoop2.web.monitor.statistiche.utils.StatsUtils;
import org.slf4j.Logger;

/**
 * StatsSearchForm
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class StatsSearchForm extends BaseSearchForm{

	private static Logger log =  LoggerManager.getPddMonitorCoreLogger();
	
	// Tipo di report da visualizzare
	private TipoReport tipoReport;
	protected String _value_tipoReport;

	private Boolean opened;

	// Modalita temporanea di visualizzazione statistiche
	private StatisticType modalitaTemporale;
	protected String _value_modalitaTemporale;

	// Tipo di dato da visualizzare (numero richiesta/ dimensione/ latenza)
	private TipoVisualizzazione tipoVisualizzazione;
	protected String _value_tipoVisualizzazione;

	// Tipo di banda da visualizzare
	private TipoBanda tipoBanda;
	protected String _value_tipoBanda;
	private String[] tipiBanda;

	// Tipo di latenza da visualizzare
	private TipoLatenza tipoLatenza;
	protected String _value_tipoLatenza;
	private String[] tipiLatenza;

	//Tipo Statistica
	private TipoStatistica tipoStatistica;
	protected String _value_tipoStatistica;
	
	// Numero dimensioni 
	private NumeroDimensioni numeroDimensioni;
	protected String _value_numeroDimensioni;
	
	// Dimensioni custom 
	private DimensioneCustom numeroDimensioniCustom;
	protected String _value_numeroDimensioniCustom;

	// Indicazione se il la distribuzione per soggetto è locale o remota. Vale solo per la distribuzione per soggetto.
	// Negli altri casi è true per default per visualizzare la select list dei soggetti locali
	private boolean distribuzionePerSoggettoRemota = true;

	// Indicazione se l'andamento temporale è per esiti
	// Vale solo nell'andamento temporale e nella distribuzione per esiti
	private boolean andamentoTemporalePerEsiti = false; 

	private boolean isMostraUnitaTempoDistribuzioneNonTemporale = false;
	private boolean isMostraUnitaTempoDistribuzioneNonTemporale_periodoPersonalizzato = false;
	
	private boolean distribuzionePerImplementazioneApi = true;
	
	private boolean statisticheLatenzaPortaEnabled = false;
	
	private String tipoDistribuzione = CostantiGrafici.TIPO_DISTRIBUZIONE_TEMPORALE;
	
	private static List<String> elencoFieldsRicercaDaIgnorare = new ArrayList<>();	
	
	static {
		// aggiungo tutti i fields delle super classi
		elencoFieldsRicercaDaIgnorare.addAll(Arrays.asList(Costanti.SEARCH_FORM_FIELDS_DA_NON_SALVARE));
		// aggiungo i field di questa classe
		elencoFieldsRicercaDaIgnorare.addAll(Arrays.asList(StatisticheCostanti.SEARCH_FORM_FIELDS_DA_NON_SALVARE));
	}
	
	public boolean isStatisticheLatenzaPortaEnabled() {
		return this.statisticheLatenzaPortaEnabled;
	}

	public void setStatisticheLatenzaPortaEnabled(boolean statisticheLatenzaPortaEnabled) {
		this.statisticheLatenzaPortaEnabled = statisticheLatenzaPortaEnabled;
	}

	public boolean isDistribuzionePerImplementazioneApi() {
		return this.distribuzionePerImplementazioneApi;
	}

	public void setDistribuzionePerImplementazioneApi(boolean distribuzionePerImplementazioneApi) {
		this.distribuzionePerImplementazioneApi = distribuzionePerImplementazioneApi;
	}

	public boolean isAndamentoTemporalePerEsiti() {
		return this.andamentoTemporalePerEsiti;
	}

	public void setAndamentoTemporalePerEsiti(boolean andamentoTemporalePerEsiti) {
		this.andamentoTemporalePerEsiti = andamentoTemporalePerEsiti;
	}

	public boolean isDistribuzionePerSoggettoRemota() {
		return this.distribuzionePerSoggettoRemota;
	}

	public void setDistribuzionePerSoggettoRemota(boolean distribuzionePerSoggettoRemota) {
		this.distribuzionePerSoggettoRemota = distribuzionePerSoggettoRemota;
	}

	public StatsSearchForm() {
		//imposto defaults	
		this.setPeriodo(this.getPeriodoDefault()!=null ? this.getPeriodoDefault() : "Ultimo mese");
		this.tipoReport=TipoReport.BAR_CHART;
		this.numeroDimensioni = NumeroDimensioni.DIMENSIONI_2;
		this.numeroDimensioniCustom = null;
		this.setSortOrder(SortOrder.ASC);  
		this.tipoVisualizzazione = TipoVisualizzazione.NUMERO_TRANSAZIONI;
		this.tipoLatenza = TipoLatenza.LATENZA_TOTALE;
		this.tipoBanda = TipoBanda.COMPLESSIVA;
		this.modalitaTemporale =StatisticType.GIORNALIERA;
		this.setTipiLatenza(new String[3]);
		
		try {
			PddMonitorProperties govwayMonitorProperties = PddMonitorProperties.getInstance(StatsSearchForm.log);
			
			initIdClusterAndCanali(govwayMonitorProperties);
			
			this.isMostraUnitaTempoDistribuzioneNonTemporale = govwayMonitorProperties.isMostraUnitaTempoDistribuzioneNonTemporale();
			if(!this.isMostraUnitaTempoDistribuzioneNonTemporale) {
				this.isMostraUnitaTempoDistribuzioneNonTemporale_periodoPersonalizzato = govwayMonitorProperties.isMostraUnitaTempoDistribuzioneNonTemporale_periodoPersonalizzato();
			}
			
			this.statisticheLatenzaPortaEnabled = govwayMonitorProperties.isStatisticheLatenzaPortaEnabled();
			
		} catch (Exception e) {
			StatsSearchForm.log.error("Errore il calcolo della proprieta' 'useDistribuzioneStatisticaGiornalieraPerElaborazioneSettimanaleMensile': " + e.getMessage(),e);
		}
	}

	protected Date dataInizioDellaRicerca = null;
	protected Date dataFineDellaRicerca = null;
	protected String periodoDellaRicerca = null;
	public String getPeriodoDellaRicerca() {
		return this.periodoDellaRicerca;
	}

	public Date getDataInizioDellaRicerca() {
		return this.dataInizioDellaRicerca;
	}

	public Date getDataFineDellaRicerca() {
		return this.dataFineDellaRicerca;
	}

	
	@Override
	public List<SelectItem> getEsitiGruppo() {
		if(this.tipoStatistica!=null && this.tipoStatistica.equals(TipoStatistica.DISTRIBUZIONE_ERRORI)) {
		
			ArrayList<SelectItem> list = new ArrayList<SelectItem>();
			try{
				EsitoUtils esitoUtils = new EsitoUtils(StatsSearchForm.log, getSafeProtocol());
				//list.add(new SelectItem(EsitoUtils.ALL_VALUE,esitoUtils.getEsitoLabelFromValue(EsitoUtils.ALL_VALUE,false)));
				list.add(new SelectItem(EsitoUtils.ALL_ERROR_FAULT_APPLICATIVO_VALUE,esitoUtils.getEsitoLabelFromValue(EsitoUtils.ALL_ERROR_FAULT_APPLICATIVO_VALUE,false)));
				list.add(new SelectItem(EsitoUtils.ALL_ERROR_VALUE,esitoUtils.getEsitoLabelFromValue(EsitoUtils.ALL_ERROR_VALUE,false)));
				list.add(new SelectItem(EsitoUtils.ALL_FAULT_APPLICATIVO_VALUE,esitoUtils.getEsitoLabelFromValue(EsitoUtils.ALL_FAULT_APPLICATIVO_VALUE,false)));
				list.add(new SelectItem(EsitoUtils.ALL_ERROR_CONSEGNA_VALUE,esitoUtils.getEsitoLabelFromValue(EsitoUtils.ALL_ERROR_CONSEGNA_VALUE,false)));
				list.add(new SelectItem(EsitoUtils.ALL_ERROR_RICHIESTE_SCARTATE_VALUE,esitoUtils.getEsitoLabelFromValue(EsitoUtils.ALL_ERROR_RICHIESTE_SCARTATE_VALUE,false)));
				//list.add(new SelectItem(EsitoUtils.ALL_OK_VALUE,esitoUtils.getEsitoLabelFromValue(EsitoUtils.ALL_OK_VALUE,false)));
				list.add(new SelectItem(EsitoUtils.ALL_PERSONALIZZATO_VALUE,esitoUtils.getEsitoLabelFromValue(EsitoUtils.ALL_PERSONALIZZATO_VALUE,false)));
		
				return list;
			}catch(Exception e){
				StatsSearchForm.log.error("Errore durante il recupero della lista dei gruppi di esito "+e.getMessage(),e);
				throw new RuntimeException(e.getMessage(),e);
			}
			
		}
		else {
			return super.getEsitiGruppo();
		}
	}
	
	public List<SelectItem> getEsitiDettaglio() {
		if(this.tipoStatistica!=null && this.tipoStatistica.equals(TipoStatistica.DISTRIBUZIONE_ERRORI)) {
			Integer esitoGruppo = this.getEsitoGruppo();
			if(esitoGruppo!=null) {
				if((EsitoUtils.ALL_VALUE.intValue() == this.getEsitoGruppo().intValue()) || (EsitoUtils.ALL_OK_VALUE.intValue() == this.getEsitoGruppo().intValue())){
					this.setEsitoGruppo(EsitoUtils.ALL_ERROR_FAULT_APPLICATIVO_VALUE);
				}
			}
		}
		
		return super.getEsitiDettaglio(true);
	}
	
	@Override
	protected List<Integer> getEsitiOrderLabel() throws Exception {
		if(this.tipoStatistica!=null && this.tipoStatistica.equals(TipoStatistica.DISTRIBUZIONE_ERRORI)) {
			EsitiProperties esitiProperties = EsitiProperties.getInstanceFromProtocolName(StatsSearchForm.log, getSafeProtocol());
			List<Integer> esiti = esitiProperties.getEsitiCodeOrderLabel(); // mantengo l'ordine
			List<Integer> esitiOk = esitiProperties.getEsitiCodeOk_senzaFaultApplicativo();
			if(esiti!=null && !esiti.isEmpty()) {
				List<Integer> esitiErrori = new ArrayList<>();
				for (Integer esito : esiti) {
					boolean found = false;
					for (Integer esitoOk : esitiOk) {
						if(esito.intValue() == esitoOk.intValue()) {
							found = true;
							break;
						}
					}
					if(!found) {
						esitiErrori.add(esito);
					}
				}
				return esitiErrori;
			}
			return esiti;
		}
		else {
			return super.getEsitiOrderLabel();
		}
	}
	
	public List<SelectItem> getEsitiDettagliPersonalizzati() {
		return super.getEsitiDettagliPersonalizzati(true);
	}
	
	public List<SelectItem> getListaDistribuzioneTokenClaim(){
		List<SelectItem> lst = new ArrayList<>();
		
		MessageManager mm = MessageManager.getInstance();
		
		boolean showPDNDFilters = isShowPDNDFilters();
		
		lst.add(new SelectItem("--", "--"));
		lst.add(new SelectItem(TipoCredenzialeMittente.TOKEN_ISSUER.getRawValue(), mm.getMessage(Costanti.SEARCH_TOKEN_ISSUER)));  
		lst.add(new SelectItem(TipoCredenzialeMittente.TOKEN_CLIENT_ID.getRawValue(), mm.getMessage(Costanti.SEARCH_TOKEN_CLIENT_ID)));  
		if(showPDNDFilters) {
			lst.add(new SelectItem(TipoCredenzialeMittente.PDND_ORGANIZATION_NAME.getRawValue(), mm.getMessage(StatisticheCostanti.STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_TOKEN_INFO_CLIENTID_PDNDINFO)));
		}
		lst.add(new SelectItem(TipoCredenzialeMittente.TOKEN_SUBJECT.getRawValue(), mm.getMessage(Costanti.SEARCH_TOKEN_SUBJECT)));  
		lst.add(new SelectItem(TipoCredenzialeMittente.TOKEN_USERNAME.getRawValue(), mm.getMessage(Costanti.SEARCH_TOKEN_USERNAME)));  
		lst.add(new SelectItem(TipoCredenzialeMittente.TOKEN_EMAIL.getRawValue(), mm.getMessage(Costanti.SEARCH_TOKEN_EMAIL)));  
		
		return lst;
	}
	
	@Override
	protected String eseguiFiltra() {
		
		if(!validateForm(this.tipoStatistica.equals(TipoStatistica.DISTRIBUZIONE_SERVIZIO_APPLICATIVO))) {
			return null;
		}
		
		if(this.numeroDimensioni!=null && NumeroDimensioni.DIMENSIONI_3_CUSTOM.equals(this.numeroDimensioni.getValue()) &&
			this.numeroDimensioniCustom==null) {
			MessageUtils.addErrorMsg(MessageManager.getInstance().getMessage(Costanti.SEARCH_MISSING_PARAMETERS_3D_INFO_LABEL_KEY));
			return null;
		} 
		
		this.dataInizioDellaRicerca = this.getDataInizio();
		this.dataFineDellaRicerca = this.getDataFine();
		this.periodoDellaRicerca = this.getPeriodo();

		if(this.action != null)
			return this.action;

		return null;
	}
	
	@Override
	public boolean validaSezioneDatiMittenteCustom() {
		if(StringUtils.isNotEmpty(this.getRiconoscimento())) {
			if(this.getRiconoscimento().equals(Costanti.VALUE_TIPO_RICONOSCIMENTO_SOGGETTO)) {
			} else if(this.getRiconoscimento().equals(Costanti.VALUE_TIPO_RICONOSCIMENTO_APPLICATIVO)) {
				if (StringUtils.isEmpty(this.getIdentificazione())) {
					MessageUtils.addErrorMsg(MessageManager.getInstance().getMessage(Costanti.SEARCH_MISSING_PARAMETERS_IDENTIFICAZIONE_LABEL_KEY));
					return false;
				}
			} else if(this.getRiconoscimento().equals(Costanti.VALUE_TIPO_RICONOSCIMENTO_IDENTIFICATIVO_AUTENTICATO)) {
			} else if(this.getRiconoscimento().equals(Costanti.VALUE_TIPO_RICONOSCIMENTO_INDIRIZZO_IP)) {
			} else { // token_info
				if (StringUtils.isEmpty(this.getTokenClaim())) {
					MessageUtils.addErrorMsg(MessageManager.getInstance().getMessage(Costanti.SEARCH_MISSING_PARAMETERS_CLAIM_LABEL_KEY));
					return false;
				}
			}
		} else {
			MessageUtils.addErrorMsg(MessageManager.getInstance().getMessage(Costanti.SEARCH_MISSING_PARAMETERS_TIPO_LABEL_KEY));
			return false;
		}
		
		return true;
	}
	
	@Override
	public void tipologiaRicercaListener(ActionEvent ae) {
		
		String oldRiconoscimento = this.getRiconoscimento();
		
		super.tipologiaRicercaListener(ae);
		
		if(this.tipoStatistica.equals(TipoStatistica.DISTRIBUZIONE_SERVIZIO_APPLICATIVO)){
			this.setRiconoscimento(oldRiconoscimento);
		}
		
	}
	
	@Override
	protected boolean isTipologiaRicercaEntrambiEnabled() {
		// Per le statistiche lo faccio sempre vedere.
		return true;
	}
	
	@Override
	public List<SelectItem> getListaTipiRiconoscimento(){
		List<SelectItem> lst = new ArrayList<>();
		
		lst.add(new SelectItem("--", "--"));

		boolean searchModeBySoggetto = TipologiaRicerca.ingresso.equals(this.getTipologiaRicercaEnum());
		boolean searchModeByApplicativo = !TipologiaRicerca.ingresso.equals(this.getTipologiaRicercaEnum()) || isProtocolloSupportaApplicativoInErogazione();
		
		// comunque sia per soggetto e applicativo DEVE essere selezionata una tipooogia di ricerca
		if( !TipologiaRicerca.ingresso.equals(this.getTipologiaRicercaEnum()) && !TipologiaRicerca.uscita.equals(this.getTipologiaRicercaEnum()) ) {
			searchModeBySoggetto = false;
			searchModeByApplicativo = false;
		}
		
		if(searchModeBySoggetto) {
			if(this.tipoStatistica!=null && 
					(
							this.tipoStatistica.equals(TipoStatistica.DISTRIBUZIONE_SOGGETTO)
							||
							this.tipoStatistica.equals(TipoStatistica.DISTRIBUZIONE_SERVIZIO_APPLICATIVO)
					)
				) {
				if(this.distribuzionePerSoggettoRemota) {
					searchModeBySoggetto = false;
					searchModeByApplicativo = false; // per scegliere un applicativo bisogna selezionare il soggetto operativo mittente (remoto in questo caso)
				}
			}
		}
		
		lst.add(new SelectItem(Costanti.VALUE_TIPO_RICONOSCIMENTO_TOKEN_INFO, MessageManager.getInstance().getMessage(Costanti.TOKEN_INFO_KEY)));  
		if(searchModeBySoggetto) {
			lst.add(new SelectItem(Costanti.VALUE_TIPO_RICONOSCIMENTO_SOGGETTO, MessageManager.getInstance().getMessage(Costanti.SOGGETTO_LABEL_KEY))); 
		}
		if(searchModeByApplicativo) {
			lst.add(new SelectItem(Costanti.VALUE_TIPO_RICONOSCIMENTO_APPLICATIVO, MessageManager.getInstance().getMessage(Costanti.SERVIZIO_APPLICATIVO_LABEL_KEY)));  
		}
		lst.add(new SelectItem(Costanti.VALUE_TIPO_RICONOSCIMENTO_IDENTIFICATIVO_AUTENTICATO, MessageManager.getInstance().getMessage(Costanti.IDENTIFICATIVO_AUTENTICATO_KEY)));  
		lst.add(new SelectItem(Costanti.VALUE_TIPO_RICONOSCIMENTO_INDIRIZZO_IP, MessageManager.getInstance().getMessage(Costanti.INDIRIZZO_IP_KEY)));  
		
		
		return lst;
	}

	private boolean isProtocolloSupportaApplicativoInErogazione() {
		String protocolloSelezionato = this.getProtocollo(); 
		boolean protocolloSupportaApplicativoinErogazione = false;
		try{
			protocolloSupportaApplicativoinErogazione = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(protocolloSelezionato).createProtocolConfiguration().isSupportoAutenticazioneApplicativiErogazioni();
		}catch(Exception e) {}
		return protocolloSupportaApplicativoinErogazione;
	}
	
	@Override
	public boolean isShowTipologia() {
		if(this.tipoStatistica.equals(TipoStatistica.DISTRIBUZIONE_SERVIZIO_APPLICATIVO)){
			if(this.getRiconoscimento() != null && this.getRiconoscimento().equals(org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_APPLICATIVO)) {
				return isProtocolloSupportaApplicativoInErogazione();
			}
		}
		
		return true;
	}
	
	
	@Override
	public void setRiconoscimento(String riconoscimento) {
		super.setRiconoscimento(riconoscimento);
		
		if(this.tipoStatistica.equals(TipoStatistica.DISTRIBUZIONE_SERVIZIO_APPLICATIVO)){
			if(this.getRiconoscimento() != null && this.getRiconoscimento().equals(org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_APPLICATIVO)) {
				if(!isProtocolloSupportaApplicativoInErogazione()) {
					this.setTipologiaRicerca(TipologiaRicerca.uscita);
				}
			}
		}
	}

	@Override
	public String getPrintPeriodo(){

		Date inizio = null;
		if(this.dataInizio!=null){
			inizio = (Date) this.dataInizio.clone();
		}

		Date fine = null;
		if(this.dataFine!=null){
			fine = (Date) this.dataFine.clone();
		}

		return AbstractDateSearchForm.printPeriodo(inizio, fine);
	}

	@Override
	public TipiDatabase getDatabaseType() {
		return _getTipoDatabase(org.openspcoop2.core.statistiche.utils.ProjectInfo.getInstance());
	}

	@Override
	public void periodoListener(ActionEvent ae){
		
		if(!this.isShowUnitaTempo()) {
			if(!this.isPeriodoPersonalizzato()) {
				if(this.isPeriodoUltime12ore()) {
					this.modalitaTemporale = StatisticType.ORARIA;
				}
				else {
					this.modalitaTemporale  = StatisticType.GIORNALIERA;
				}
				//System.out.println("PERIODO LISTENER: "+this.modalitaTemporale);
			}
			else {
				this.modalitaTemporale = StatisticType.ORARIA;
			}
		}
		
		
		_setPeriodo();

		this.dataInizioDellaRicerca = this.getDataInizio();
		this.dataFineDellaRicerca = this.getDataFine();
		this.periodoDellaRicerca = this.getPeriodo();
	}

	public void modalitaTemporaleListener(ActionEvent ae){
		switch(this.modalitaTemporale){
		case GIORNALIERA: this.setPeriodo("Ultima settimana"); break;
		case MENSILE: this.setPeriodo("Ultimo anno"); break;
		case SETTIMANALE: this.setPeriodo("Ultimo mese"); break;
		case ORARIA: this.setPeriodo("Ultime 12 ore"); break;
		}
		this.periodoListener(ae);

		this.dataInizioDellaRicerca = this.getDataInizio();
		this.dataFineDellaRicerca = this.getDataFine();
		this.periodoDellaRicerca = this.getPeriodo();
	}
	
	@SuppressWarnings("incomplete-switch")
	public void modalitaTemporaleListenerPersonalizzato(ActionEvent ae){

		this.periodoListener(ae);

		switch(this.modalitaTemporale){
		case ORARIA:  
			if(this.dataFine!=null) {
				Calendar fine = Calendar.getInstance();
				fine.setTime(this.dataFine);
				fine.set(Calendar.HOUR_OF_DAY,23);
				fine.set(Calendar.MINUTE,59);
				this.dataFine = fine.getTime();
			}
			break;
		}
		
		this.dataInizioDellaRicerca = this.getDataInizio();
		this.dataFineDellaRicerca = this.getDataFine();
		this.periodoDellaRicerca = this.getPeriodo();
	}
	

	@Override
	public void initSearchListener(ActionEvent ae) {
		super.initSearchListener(ae);

		if(!this.tipoStatistica.equals(TipoStatistica.ANDAMENTO_TEMPORALE)){
			this.tipoReport=TipoReport.BAR_CHART;
		}else{
			if(this.andamentoTemporalePerEsiti){
				this.tipoReport=TipoReport.BAR_CHART;
			}
			else{
				this.tipoReport=TipoReport.LINE_CHART;
			}
		}

		this.numeroDimensioni = NumeroDimensioni.DIMENSIONI_2;
		this.numeroDimensioniCustom = null;
		this.setSortOrder(SortOrder.ASC); 
		this.tipoVisualizzazione = TipoVisualizzazione.NUMERO_TRANSAZIONI;
		this.tipoLatenza = TipoLatenza.LATENZA_TOTALE;
		this.tipoBanda = TipoBanda.COMPLESSIVA;
		this.modalitaTemporale =StatisticType.GIORNALIERA;
		this.setPeriodo(this.getPeriodoDefault()!=null ? this.getPeriodoDefault() : "Ultimo mese");

		this.setTipiLatenza(new String[3]);
		this.getTipiLatenza()[0] = "0";
		this.getTipiLatenza()[1] = getTipiLatenza()[2] = null;

		this.periodoListener(ae);

		this.dataInizioDellaRicerca = this.getDataInizio();
		this.dataFineDellaRicerca = this.getDataFine();
		this.periodoDellaRicerca = this.getPeriodo();
		
		if(this.tipoStatistica.equals(TipoStatistica.DISTRIBUZIONE_SERVIZIO_APPLICATIVO)){
			if(this.getRiconoscimento() != null && this.getRiconoscimento().equals(org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_APPLICATIVO)) {
				if(!isProtocolloSupportaApplicativoInErogazione()) {
					this.setTipologiaRicerca(TipologiaRicerca.uscita);
				}
			}
		}
		
		this.setTipologiaRicerca("--"); // in modo da far comparire la lista con il suggerimento di selezione come per gli altri
		
		this.clusterId = null;
		this.canale = null;
	}

	@Override
	protected String ripulisciValori(){
		this.initSearchListener(null);
		
		this.setTipologiaRicerca("--"); // in modo da far comparire la lista con il suggerimento di selezione come per gli altri
		
		return null;
	}
	
	@Override
	protected String eseguiAggiorna() {
		return null;
	}

	public void tipoVisualizzazioneListener(ActionEvent ae){

		// Latenza
		this.tipoLatenza = TipoLatenza.LATENZA_TOTALE;

		this.setTipiLatenza(new String[3]);
		this.getTipiLatenza()[0] = "0";
		this.getTipiLatenza()[1] = getTipiLatenza()[2] = null;

		// Banda
		this.tipoBanda = TipoBanda.COMPLESSIVA;

		this.setTipiBanda(new String[3]);
		this.getTipiBanda()[0] = "0";
		this.getTipiBanda()[1] = getTipiBanda()[2] = null;
		
		// numero dimensioni
		this.numeroDimensioni = NumeroDimensioni.DIMENSIONI_2;
		this.numeroDimensioniCustom = null;
	}
	
	public void numeroDimensioniListener(ActionEvent ae){

		// numero dimensioni
		this.numeroDimensioniCustom = null;
		
	}
	
	@Override
	public void claimSelected(ActionEvent ae){
		// numero dimensioni
		this.numeroDimensioniCustom = null;
	}
	
	@Override
	public void identificazioneSelected(ActionEvent ae) {
		super.identificazioneSelected(ae);
		// numero dimensioni
		this.numeroDimensioniCustom = null;
	}

	public void tipoLatenzaListener(ActionEvent ae){
	}

	public void tipoLatenzaCBListener(ActionEvent ae){
		//controllo che sia selezionato almeno un valore della checkbox
		boolean ok = false;
		for (String tipoLat : this.getTipiLatenza()) {
			if(StringUtils.isNotEmpty(tipoLat)){
				ok = true; 
				break;
			}
		}

		// almeno una latenza deve rimanere settata
		if(!ok){
			this.setTipiLatenza(new String[3]);
			this.getTipiLatenza()[0] = "0";
			this.getTipiLatenza()[1] = getTipiLatenza()[2] = null;
		}

	}

	public void tipoBandaListener(ActionEvent ae){
	}

	public void tipoBandaCBListener(ActionEvent ae){
		//controllo che sia selezionato almeno un valore della checkbox
		boolean ok = false;
		for (String tipoLat : this.getTipiBanda()) {
			if(StringUtils.isNotEmpty(tipoLat)){
				ok = true; 
				break;
			}
		}

		// almeno una banda deve rimanere settata
		if(!ok){
			this.setTipiBanda(new String[3]);
			this.getTipiBanda()[0] = "0";
			this.getTipiBanda()[1] = getTipiBanda()[2] = null;
		}

	}

	public TipoVisualizzazione getTipoVisualizzazione() {
		return this.tipoVisualizzazione;
	}

	public void setTipoVisualizzazione(TipoVisualizzazione tipoVisualizzazione) {
		this.tipoVisualizzazione = tipoVisualizzazione;
	}

	public String get_value_tipoVisualizzazione() {
		if(this.tipoVisualizzazione == null){
			return null;
		}else{
			return this.tipoVisualizzazione.toString();
		}
	}

	public void set_value_tipoVisualizzazione(String _value_tipoVisualizzazione) {
		this.tipoVisualizzazione =(TipoVisualizzazione) TipoVisualizzazione.toEnumConstantFromString(_value_tipoVisualizzazione);
	}

	public TipoLatenza getTipoLatenza() {
		return this.tipoLatenza;
	}

	public void setTipoLatenza(TipoLatenza tipoLatenza) {
		this.tipoLatenza = tipoLatenza;
	}

	public TipoBanda getTipoBanda() {
		return this.tipoBanda;
	}

	public void setTipoBanda(TipoBanda tipoBanda) {
		this.tipoBanda = tipoBanda;
	}

	public Boolean getOpened() {
		//		if(StringUtils.isEmpty(this.getSoggettoGestione()))
		//			return true;

		return this.opened;
	}

	public void setOpened(Boolean opened) {
		this.opened = opened;
	}

	public String get_value_tipoLatenza() {
		if(this.tipoLatenza == null){
			return null;
		}else{
			return this.tipoLatenza.toString();
		}
	}

	public void set_value_tipoLatenza(String _value_tipoLatenza) {
		this.tipoLatenza = (TipoLatenza) TipoLatenza.toEnumConstantFromString(_value_tipoLatenza);
	}

	public String[] getTipiLatenza() {
		return this.tipiLatenza;
	}

	public void setTipiLatenza(String[] tipiLatenza) {
		this.tipiLatenza = tipiLatenza;
	}	

	public List<TipoLatenza> getTipiLatenzaImpostati(){
		List<TipoLatenza> lst = new ArrayList<TipoLatenza>();

		if(this.getTipiLatenza()!=null){
			for (String tipoLat : this.getTipiLatenza()) {
				if(tipoLat != null){
					if(tipoLat.equals("0"))
						lst.add(TipoLatenza.LATENZA_TOTALE);
					if(tipoLat.equals("1"))
						lst.add(TipoLatenza.LATENZA_SERVIZIO);
					if(tipoLat.equals("2"))
						lst.add(TipoLatenza.LATENZA_PORTA);
				}
			}
		}

		return lst;
	}

	public Map<String,String> getHeaderColonneTipiLatenzaImpostati(){

		Map<String,String>  map = new HashMap<>();

		if(this.getTipiLatenza()!=null){
			int i = 0;
			for (String tipoLat : this.getTipiLatenza()) {
				if(tipoLat != null){
					if(tipoLat.equals("0"))
						map.put(""+i, CostantiGrafici.LABEL_TIPO_LATENZA_LATENZA_MEDIA_TOTALE ); //TipoLatenza.LATENZA_TOTALE.getValue());
					if(tipoLat.equals("1"))
						map.put(""+i, CostantiGrafici.LABEL_TIPO_LATENZA_LATENZA_MEDIA_SERVIZIO ); // TipoLatenza.LATENZA_SERVIZIO.getValue());
					if(tipoLat.equals("2"))
						map.put(""+i, CostantiGrafici.LABEL_TIPO_LATENZA_LATENZA_MEDIA_PORTA );//TipoLatenza.LATENZA_PORTA.getValue());

					i++;
				}
			}
		}

		return map;
	}

	public String get_value_tipoBanda() {
		if(this.tipoBanda == null){
			return null;
		}else{
			return this.tipoBanda.toString();
		}
	}

	public void set_value_tipoBanda(String _value_tipoBanda) {
		this.tipoBanda = (TipoBanda) TipoBanda.toEnumConstantFromString(_value_tipoBanda);
	}

	public String[] getTipiBanda() {
		return this.tipiBanda;
	}

	public void setTipiBanda(String[] tipiBanda) {
		this.tipiBanda = tipiBanda;
	}

	public List<TipoBanda> getTipiBandaImpostati(){
		List<TipoBanda> lst = new ArrayList<>();

		if(this.getTipiBanda()!=null){
			for (String tipoBanda : this.getTipiBanda()) {
				if(tipoBanda != null){
					if(tipoBanda.equals("0"))
						lst.add(TipoBanda.COMPLESSIVA);
					if(tipoBanda.equals("1"))
						lst.add(TipoBanda.INTERNA);
					if(tipoBanda.equals("2"))
						lst.add(TipoBanda.ESTERNA);
				}
			}
		}

		return lst;
	}

	public Map<String,String> getHeaderColonneTipiBandaImpostati(){

		Map<String,String>  map = new HashMap<>();

		if(this.getTipiBanda()!=null){
			int i = 0;
			for (String tipoBanda : this.getTipiBanda()) {
				if(tipoBanda != null){
					if(tipoBanda.equals("0"))
						map.put(""+i,TipoBanda.COMPLESSIVA.getValue());
					if(tipoBanda.equals("1"))
						map.put(""+i,TipoBanda.INTERNA.getValue());
					if(tipoBanda.equals("2"))
						map.put(""+i,TipoBanda.ESTERNA.getValue());

					i++;
				}
			}
		}

		return map;
	}

	public Map<String,String> getHeaderColonneEsiti(){

		Map<String,String>  map = new HashMap<>();
		map.put(""+0,"Ok");
		map.put(""+1,"Fault Applicativo");
		map.put(""+2,"Fallite");
		return map;
	}



	public StatisticType getModalitaTemporale() {
		return this.modalitaTemporale;
	}

	public void setModalitaTemporale(StatisticType modalitaTemporale) {
		this.modalitaTemporale = modalitaTemporale;
	}

	public String get_value_modalitaTemporale() {
		if(this.modalitaTemporale == null){
			return null;
		}else{
			return this.modalitaTemporale.toString().toLowerCase();
		}
	}

	public void set_value_modalitaTemporale(String _value_modalitaTemporale) {
		if(StringUtils.isNotEmpty(_value_modalitaTemporale))
			this.modalitaTemporale = StatisticType.valueOf(_value_modalitaTemporale.toUpperCase());
		else 
			this.modalitaTemporale = null;
	}

	public StatisticType getStatisticType() {
		return this.getModalitaTemporale();
	}

	public TipoReport getTipoReport() {
		return this.tipoReport;
	}

	public void setTipoReport(TipoReport tipoReport) {
		this.tipoReport = tipoReport;

		this.setSortOrder(SortOrder.ASC);

		boolean useCount = true;
		
		if(this.tipoReport != null &&
			this.tipoReport.equals(TipoReport.TABELLA)) {
			
			this.setSortOrder(SortOrder.DESC);
			
			/**
			 * Nota: se si gestisce altri tipi di distribuzione con useCount=false aggiungere attributo useCount in <link:dataTable  come fatto in distribSAGrafico.xhtml
			 * */
			
			if(TipoStatistica.DISTRIBUZIONE_SERVIZIO_APPLICATIVO.equals(this.tipoStatistica)) {
				if(org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_APPLICATIVO.equals(this.getRiconoscimento())
						&&
						org.openspcoop2.web.monitor.core.constants.Costanti.IDENTIFICAZIONE_TOKEN_KEY.equals(this.getIdentificazione())) {
					useCount = false; // ci sono dei continue nel metodo 'executeDistribuzioneServizioApplicativo', quando non vi è l'applicativo identificato tramite count, che fanno saltare il count
				}
				else if(org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_TOKEN_INFO.equals(this.getRiconoscimento())){
					org.openspcoop2.core.transazioni.utils.TipoCredenzialeMittente tcm = org.openspcoop2.core.transazioni.utils.TipoCredenzialeMittente.toEnumConstant(this.getTokenClaim());
					if(tcm!=null && org.openspcoop2.core.transazioni.utils.TipoCredenzialeMittente.PDND_ORGANIZATION_NAME.equals(tcm)) {
						useCount = false; // ci sono dei continue nel metodo 'executeDistribuzioneServizioApplicativo', per i record per cui la risoluzione PDND non è stata attuata, che fanno saltare il count
					}
				}
			}
		}
		
		this.setUseCount(useCount);
	}

	public String get_value_tipoReport() {
		if(this.tipoReport == null){
			return null;
		}else{
			return this.tipoReport.toString();
		}
	}

	public void set_value_tipoReport(String _value_tipoReport) {
		TipoReport tr = (TipoReport) TipoReport.toEnumConstantFromString(_value_tipoReport);
		this.setTipoReport(tr); 
	}

	public void tipoReportSelected(ActionEvent ae){
		if(ae!=null) {
			// nop
		}
		this.numeroDimensioni = NumeroDimensioni.DIMENSIONI_2;
		this.numeroDimensioniCustom = null;
	}

	public TipoStatistica getTipoStatistica() {
		return this.tipoStatistica;
	}

	public void setTipoStatistica(TipoStatistica tipoStatistica) {
		this.tipoStatistica = tipoStatistica;

		if(tipoStatistica != null) {
			if(!this.tipoStatistica.equals(TipoStatistica.ANDAMENTO_TEMPORALE)){
				this.tipoReport=TipoReport.BAR_CHART;
			}else {
				if(this.andamentoTemporalePerEsiti){
					this.tipoReport=TipoReport.BAR_CHART;
				}
				else{
					this.tipoReport=TipoReport.LINE_CHART;
				}
			}
		}
	}

	public String get_value_tipoStatistica() {
		if(this.tipoStatistica == null){
			return null;
		}else{
			return this.tipoStatistica.toString();
		}
	}

	public void set_value_tipoStatistica(String _value_tipoStatistica) {
		TipoStatistica tr = (TipoStatistica) TipoStatistica.toEnumConstantFromString(_value_tipoStatistica);
		this.setTipoStatistica(tr); 
	}

	@Override
	protected void _setPeriodo() {

		super._setPeriodo();

		// Effettuo l'override per poter troncare gli intervalli di fine per le statistiche.
		// A differenza delle transazioni non vogliamo ricordarci dell'ultima ricerca effettuata.
		// Inoltre a differenza delle transazioni l'intervallo destro non deve essere preciso ma deve essere arrotondato rispetto al tipo di informazione

		this._normalizeDataInizio();
		this._normalizeDataFine();

	}
	private void _normalizeDataInizio() {
		if(this.dataInizio!=null) {
			Calendar inizio = Calendar.getInstance();
			inizio.setTime(this.dataInizio);
			// Fix: l'impostazione dei minuti non ha senso nelle statistiche poichè non esiste un campionamento sui minuti. La soluzione è di impostare l'intervallo più esterno '00' in data inizio e '59' in data fine.
			boolean esisteCampionamentoStatisticoMinuti = false;
			if(!esisteCampionamentoStatisticoMinuti || !StatisticType.ORARIA.equals(this.modalitaTemporale) || (!this.isPeriodoPersonalizzato()) || (!this.isLastPeriodoPersonalizzato())) {
				inizio.clear(Calendar.MINUTE);
			}
			inizio.clear(Calendar.SECOND);
			inizio.clear(Calendar.MILLISECOND);
			
			if(this.modalitaTemporale!=null){
				switch(this.modalitaTemporale){
				case ORARIA: 
					break;
				case GIORNALIERA: 
				case SETTIMANALE: 
				case MENSILE: 
					inizio.clear(Calendar.HOUR_OF_DAY);
					inizio.set(Calendar.HOUR_OF_DAY,0); // il clear non sempre funziona
					break;
				}
			}
			
			this.dataInizio = inizio.getTime();
		}
	}
	private void _normalizeDataFine() {
		if(this.dataFine!=null) {
			Calendar fine = Calendar.getInstance();
			fine.setTime(this.dataFine);
			// Fix: l'impostazione dei minuti non ha senso nelle statistiche poichè non esiste un campionamento sui minuti. La soluzione è di impostare l'intervallo più esterno '00' in data inizio e '59' in data fine.
			boolean esisteCampionamentoStatisticoMinuti = false;
			if(!esisteCampionamentoStatisticoMinuti || !StatisticType.ORARIA.equals(this.modalitaTemporale) || (!this.isPeriodoPersonalizzato()) || (!this.isLastPeriodoPersonalizzato())) {
				fine.set(Calendar.MINUTE,59);
			}
			fine.set(Calendar.SECOND,59);
			fine.set(Calendar.MILLISECOND,999);
			
			if(this.modalitaTemporale!=null){
				switch(this.modalitaTemporale){
				case ORARIA: 
					break;
				case GIORNALIERA: 
				case SETTIMANALE: 
				case MENSILE: 
					fine.set(Calendar.HOUR_OF_DAY,23);
					break;
				}
			}
			
			this.dataFine = fine.getTime();
		}
	}


	public String getAction() {
		return this.action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public boolean isUseGraficiSVG() {
		if(this.useGraficiSVG==null) {
			BrowserInfo browserInfo = ApplicationBean.getInstance().getBrowserInfo();
			this.useGraficiSVG =ApplicationBean.getInstance().isGraficiSvgEnabled() && !BrowserFilter.disabilitaGraficiSVG(browserInfo);
	
			LoggerManager.getPddMonitorCoreLogger().debug("Usa grafici SVG ["+this.useGraficiSVG+"]");
		}
	
		return this.useGraficiSVG;
	}

	public void setUseGraficiSVG(boolean useGraficiSVG) {
		this.useGraficiSVG = useGraficiSVG;
	}

	private String action = null;


	private Boolean useGraficiSVG = null;


	private boolean usaSVG = true;

	public boolean isUsaSVG() {
		return this.usaSVG;
	}
	public void setUsaSVG(boolean usaSVG) {
		this.usaSVG = usaSVG;
	}

	public void cleanSVG(){
		ApplicationBean.getInstance().cleanSVG();
	}
	
	public void tornaAiFiltri(ActionEvent ae){
		Date dataInizioDellaRicerca = this.getDataInizioDellaRicerca();
		Date dataFineDellaRicerca = this.getDataFineDellaRicerca();
		String periodoDellaRicerca = this.getPeriodoDellaRicerca();
		
		this.setDataInizio(dataInizioDellaRicerca);
		this.setDataFine(dataFineDellaRicerca);
		this.setPeriodo(periodoDellaRicerca); 
		
	}
	
	public String getDateFormatPeriodoPersonalizzato() {
		if(this.modalitaTemporale!=null) {
			switch (this.modalitaTemporale) {
			case ORARIA:
				// Fix: l'impostazione dei minuti non ha senso nelle statistiche poichè non esiste un campionamento sui minuti. La soluzione è di impostare l'intervallo più esterno '00' in data inizio e '59' in data fine.
				return "yyyy-MM-dd HH:mm";
			default:
				if(!TipoStatistica.ANDAMENTO_TEMPORALE.equals(this.tipoStatistica) &&
						!this.isMostraUnitaTempoDistribuzioneNonTemporale &&
						!this.isMostraUnitaTempoDistribuzioneNonTemporale_periodoPersonalizzato) {
					return "yyyy-MM-dd HH:mm"; // i minuti servono sempre per le distribuzioni statistiche se non visualizzo l'unita di tempo nel periodo personalizzato
				}
				return "yyyy-MM-dd";
			}
		}
		return "yyyy-MM-dd";
	}
	
	public boolean isShowGroupByDatiMittente() {
		return this.tipoStatistica.equals(TipoStatistica.DISTRIBUZIONE_SERVIZIO_APPLICATIVO);
	}
	
	
	public boolean isShowUnitaTempo() {
		return TipoStatistica.ANDAMENTO_TEMPORALE.equals(this.tipoStatistica) || this.isMostraUnitaTempoDistribuzioneNonTemporale;
	}
	public boolean isShowUnitaTempoPersonalizzato() {
		return !this.isShowUnitaTempo() && this.isPeriodoPersonalizzato() && this.isMostraUnitaTempoDistribuzioneNonTemporale_periodoPersonalizzato;
	}
	public boolean isShowUnitaTempoPersonalizzato_periodoPersonalizzato() {
		return this.isMostraUnitaTempoDistribuzioneNonTemporale_periodoPersonalizzato;
	}
	
	public List<SelectItem> getDimensioniDisponibili() {
		List<SelectItem> dimensioniDisponibili = new ArrayList<>();
		
		dimensioniDisponibili.add(new SelectItem(NumeroDimensioni.DIMENSIONI_2.toString(), MessageManager.getInstance().getMessage(CostantiGrafici.SEARCH_NUMERO_DIMENSIONI_2D_LABEL_KEY)));
		dimensioniDisponibili.add(new SelectItem(NumeroDimensioni.DIMENSIONI_3.toString(), MessageManager.getInstance().getMessage(CostantiGrafici.SEARCH_NUMERO_DIMENSIONI_3D_LABEL_KEY)));
		dimensioniDisponibili.add(new SelectItem(NumeroDimensioni.DIMENSIONI_3_CUSTOM.toString(), MessageManager.getInstance().getMessage(CostantiGrafici.SEARCH_NUMERO_DIMENSIONI_3D_CUSTOM_LABEL_KEY)));
		
		return dimensioniDisponibili;
	}

	public String get_value_numeroDimensioni() {
		if(this.numeroDimensioni == null){
			return null;
		}else{
			return this.numeroDimensioni.toString();
		}
	}

	public void set_value_numeroDimensioni(String valueNumeroDimensioni) {
		NumeroDimensioni nd = (NumeroDimensioni) NumeroDimensioni.toEnumConstantFromString(valueNumeroDimensioni);
		this.setNumeroDimensioni(nd); 
	}

	public NumeroDimensioni getNumeroDimensioni() {
		return this.numeroDimensioni;
	}

	public void setNumeroDimensioni(NumeroDimensioni numeroDimensioni) {
		this.numeroDimensioni = numeroDimensioni;
	}
	
	public boolean isVisualizzaNumeroDimensioni() {
		TipoReport tipoReportToCheck = this.getTipoReport();
		
		// si visualizza se e' stato scelto  il report tabella o barchart con grafici svg attivi
		return tipoReportToCheck != null && (tipoReportToCheck.equals(TipoReport.TABELLA) || (tipoReportToCheck.equals(TipoReport.BAR_CHART) && this.isUseGraficiSVG()));
	}
	
	public List<SelectItem> getDimensioniCustomDisponibili() throws UtilsException {
		
		SortedMap<String> map = getDimensioniCustomDisponibiliAsMap();
		
		List<SelectItem> dimensioniDisponibili = new ArrayList<>();
		
		dimensioniDisponibili.add(new SelectItem("--", "--"));
		
		for (String key : map.keys()) {
			String value = map.get(key);
			dimensioniDisponibili.add(new SelectItem(key, value));
		}
		
		return dimensioniDisponibili;
	}
	public SortedMap<String> getDimensioniCustomDisponibiliAsMap() throws UtilsException {
		SortedMap<String> dimensioniDisponibili = new SortedMap<>();
		
		if(!this.tipoStatistica.equals(TipoStatistica.DISTRIBUZIONE_SERVIZIO)) {
			dimensioniDisponibili.put(DimensioneCustom.TAG.toString(), MessageManager.getInstance().getMessage(CostantiGrafici.SEARCH_NUMERO_DIMENSIONI_3D_CUSTOM_LABEL_TAG));
			dimensioniDisponibili.put(DimensioneCustom.API.toString(), MessageManager.getInstance().getMessage(CostantiGrafici.SEARCH_NUMERO_DIMENSIONI_3D_CUSTOM_LABEL_API));
			dimensioniDisponibili.put(DimensioneCustom.IMPLEMENTAZIONE_API.toString(), MessageManager.getInstance().getMessage(CostantiGrafici.SEARCH_NUMERO_DIMENSIONI_3D_CUSTOM_LABEL_IMPLEMENTAZIONE_API));
		}
		if(!this.tipoStatistica.equals(TipoStatistica.DISTRIBUZIONE_AZIONE)) {
			dimensioniDisponibili.put(DimensioneCustom.OPERAZIONE.toString(), MessageManager.getInstance().getMessage(CostantiGrafici.SEARCH_NUMERO_DIMENSIONI_3D_CUSTOM_LABEL_OPERAZIONE));
		}
		
		if(!this.tipoStatistica.equals(TipoStatistica.DISTRIBUZIONE_SOGGETTO)) {
			addDimensioniCustomSoggetto(dimensioniDisponibili);
		}
		
		addDimensioniCustomApplicativo(dimensioniDisponibili);
		
		addDimensioniCustomToken(dimensioniDisponibili);
		
		if(!this.tipoStatistica.equals(TipoStatistica.DISTRIBUZIONE_SERVIZIO_APPLICATIVO) ||
				(StringUtils.isEmpty(getRiconoscimento())) ||
				!org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_IDENTIFICATIVO_AUTENTICATO.equals(getRiconoscimento())) {
			dimensioniDisponibili.put(DimensioneCustom.PRINCIPAL.toString(), MessageManager.getInstance().getMessage(CostantiGrafici.SEARCH_NUMERO_DIMENSIONI_3D_CUSTOM_LABEL_PRINCIPAL));
		}
		
		if(!this.tipoStatistica.equals(TipoStatistica.DISTRIBUZIONE_SERVIZIO_APPLICATIVO) ||
				(StringUtils.isEmpty(getRiconoscimento())) ||
				!org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_INDIRIZZO_IP.equals(getRiconoscimento())) {
			dimensioniDisponibili.put(DimensioneCustom.INDIRIZZO_IP.toString(), MessageManager.getInstance().getMessage(CostantiGrafici.SEARCH_NUMERO_DIMENSIONI_3D_CUSTOM_LABEL_INDIRIZZO_IP));
		}
		
		if(!this.tipoStatistica.equals(TipoStatistica.DISTRIBUZIONE_ERRORI)) {
			dimensioniDisponibili.put(DimensioneCustom.ESITO.toString(), MessageManager.getInstance().getMessage(CostantiGrafici.SEARCH_NUMERO_DIMENSIONI_3D_CUSTOM_LABEL_ESITO));
		}
		
		return dimensioniDisponibili;
	}
	private void addDimensioniCustomSoggetto(SortedMap<String> dimensioniDisponibili) throws UtilsException {
		if(TipologiaRicerca.ingresso.equals(this.getTipologiaRicercaEnum()) || TipologiaRicerca.uscita.equals(this.getTipologiaRicercaEnum())){
			if (this.getSoggettoLocale()==null || StringUtils.isBlank(this.getSoggettoLocale())) {
				dimensioniDisponibili.put(DimensioneCustom.SOGGETTO_LOCALE.toString(), MessageManager.getInstance().getMessage(CostantiGrafici.SEARCH_NUMERO_DIMENSIONI_3D_CUSTOM_LABEL_SOGGETTO_LOCALE));
			}
			if (this.getTipoNomeTrafficoPerSoggetto()==null || StringUtils.isBlank(this.getTipoNomeTrafficoPerSoggetto())) {
				dimensioniDisponibili.put(DimensioneCustom.SOGGETTO_REMOTO.toString(), MessageManager.getInstance().getMessage(CostantiGrafici.SEARCH_NUMERO_DIMENSIONI_3D_CUSTOM_LABEL_SOGGETTO_REMOTO));
			}
		}
		else {
			if (this.getTipoNomeMittente()==null || StringUtils.isBlank(this.getTipoNomeMittente())) {
				dimensioniDisponibili.put(DimensioneCustom.SOGGETTO_FRUITORE.toString(), MessageManager.getInstance().getMessage(CostantiGrafici.SEARCH_NUMERO_DIMENSIONI_3D_CUSTOM_LABEL_SOGGETTO_FRUITORE));
			}
			if (this.getTipoNomeDestinatario()==null || StringUtils.isBlank(this.getTipoNomeDestinatario())) {
				dimensioniDisponibili.put(DimensioneCustom.SOGGETTO_EROGATORE.toString(), MessageManager.getInstance().getMessage(CostantiGrafici.SEARCH_NUMERO_DIMENSIONI_3D_CUSTOM_LABEL_SOGGETTO_EROGATORE));
			}
		}
	}
	private void addDimensioniCustomApplicativo(SortedMap<String> dimensioniDisponibili) throws UtilsException {
		if(!this.tipoStatistica.equals(TipoStatistica.DISTRIBUZIONE_SERVIZIO_APPLICATIVO) ||
				(StringUtils.isEmpty(this.getRiconoscimento())) ||
				!org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_APPLICATIVO.equals(this.getRiconoscimento()) ||
				!org.openspcoop2.web.monitor.core.constants.Costanti.IDENTIFICAZIONE_TRASPORTO_KEY.equals(this.getIdentificazione())
				) {
			dimensioniDisponibili.put(DimensioneCustom.APPLICATIVO_TRASPORTO.toString(), MessageManager.getInstance().getMessage(CostantiGrafici.SEARCH_NUMERO_DIMENSIONI_3D_CUSTOM_LABEL_APPLICATIVO_TRASPORTO));
		}
		if(!this.tipoStatistica.equals(TipoStatistica.DISTRIBUZIONE_SERVIZIO_APPLICATIVO) ||
				(StringUtils.isEmpty(this.getRiconoscimento())) ||
				!org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_APPLICATIVO.equals(this.getRiconoscimento()) ||
				!org.openspcoop2.web.monitor.core.constants.Costanti.IDENTIFICAZIONE_TOKEN_KEY.equals(this.getIdentificazione())
				) {
			dimensioniDisponibili.put(DimensioneCustom.APPLICATIVO_TOKEN.toString(), MessageManager.getInstance().getMessage(CostantiGrafici.SEARCH_NUMERO_DIMENSIONI_3D_CUSTOM_LABEL_APPLICATIVO_TOKEN));
		}
	}
	private void addDimensioniCustomToken(SortedMap<String> dimensioniDisponibili) throws UtilsException {
		org.openspcoop2.core.transazioni.utils.TipoCredenzialeMittente tcm = null;
		if(this.tipoStatistica.equals(TipoStatistica.DISTRIBUZIONE_SERVIZIO_APPLICATIVO) &&
				org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_TOKEN_INFO.equals(this.getRiconoscimento())){
			tcm = org.openspcoop2.core.transazioni.utils.TipoCredenzialeMittente.toEnumConstant(this.getTokenClaim());
		}
		if(!this.tipoStatistica.equals(TipoStatistica.DISTRIBUZIONE_SERVIZIO_APPLICATIVO) ||
				(StringUtils.isEmpty(this.getRiconoscimento())) ||
				!org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_TOKEN_INFO.equals(this.getRiconoscimento()) ||
				!org.openspcoop2.core.transazioni.utils.TipoCredenzialeMittente.TOKEN_CLIENT_ID.equals(tcm)
				) {
			dimensioniDisponibili.put(DimensioneCustom.TOKEN_CLIENT_ID.toString(), MessageManager.getInstance().getMessage(CostantiGrafici.SEARCH_NUMERO_DIMENSIONI_3D_CUSTOM_LABEL_TOKEN_CLIENT_ID));
		}
		if(!this.tipoStatistica.equals(TipoStatistica.DISTRIBUZIONE_SERVIZIO_APPLICATIVO) ||
				(StringUtils.isEmpty(this.getRiconoscimento())) ||
				!org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_TOKEN_INFO.equals(this.getRiconoscimento()) ||
				!org.openspcoop2.core.transazioni.utils.TipoCredenzialeMittente.TOKEN_ISSUER.equals(tcm)
				) {
			dimensioniDisponibili.put(DimensioneCustom.TOKEN_ISSUER.toString(), MessageManager.getInstance().getMessage(CostantiGrafici.SEARCH_NUMERO_DIMENSIONI_3D_CUSTOM_LABEL_TOKEN_ISSUER));
		}
		if(!this.tipoStatistica.equals(TipoStatistica.DISTRIBUZIONE_SERVIZIO_APPLICATIVO) ||
				(StringUtils.isEmpty(this.getRiconoscimento())) ||
				!org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_TOKEN_INFO.equals(this.getRiconoscimento()) ||
				!org.openspcoop2.core.transazioni.utils.TipoCredenzialeMittente.TOKEN_SUBJECT.equals(tcm)
				) {
			dimensioniDisponibili.put(DimensioneCustom.TOKEN_SUBJECT.toString(), MessageManager.getInstance().getMessage(CostantiGrafici.SEARCH_NUMERO_DIMENSIONI_3D_CUSTOM_LABEL_TOKEN_SUBJECT));
		}
		if(!this.tipoStatistica.equals(TipoStatistica.DISTRIBUZIONE_SERVIZIO_APPLICATIVO) ||
				(StringUtils.isEmpty(this.getRiconoscimento())) ||
				!org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_TOKEN_INFO.equals(this.getRiconoscimento()) ||
				!org.openspcoop2.core.transazioni.utils.TipoCredenzialeMittente.TOKEN_USERNAME.equals(tcm)
				) {
			dimensioniDisponibili.put(DimensioneCustom.TOKEN_USERNAME.toString(), MessageManager.getInstance().getMessage(CostantiGrafici.SEARCH_NUMERO_DIMENSIONI_3D_CUSTOM_LABEL_TOKEN_USERNAME));
		}
		if(!this.tipoStatistica.equals(TipoStatistica.DISTRIBUZIONE_SERVIZIO_APPLICATIVO) ||
				(StringUtils.isEmpty(this.getRiconoscimento())) ||
				!org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_TOKEN_INFO.equals(this.getRiconoscimento()) ||
				!org.openspcoop2.core.transazioni.utils.TipoCredenzialeMittente.TOKEN_EMAIL.equals(tcm)
				) {
			dimensioniDisponibili.put(DimensioneCustom.TOKEN_EMAIL.toString(), MessageManager.getInstance().getMessage(CostantiGrafici.SEARCH_NUMERO_DIMENSIONI_3D_CUSTOM_LABEL_TOKEN_EMAIL));
		}
		if(!this.tipoStatistica.equals(TipoStatistica.DISTRIBUZIONE_SERVIZIO_APPLICATIVO) ||
				(StringUtils.isEmpty(this.getRiconoscimento())) ||
				!org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_TOKEN_INFO.equals(this.getRiconoscimento()) ||
				!org.openspcoop2.core.transazioni.utils.TipoCredenzialeMittente.PDND_ORGANIZATION_NAME.equals(tcm)
				) {
			boolean showPDNDFilters = isShowPDNDFilters();
			if(showPDNDFilters) {
				dimensioniDisponibili.put(DimensioneCustom.TOKEN_PDND_ORGANIZATION.toString(), MessageManager.getInstance().getMessage(CostantiGrafici.SEARCH_NUMERO_DIMENSIONI_3D_CUSTOM_LABEL_TOKEN_PDND_ORGANIZATION));
			}
		}
	}
	
	public String get_value_numeroDimensioniCustom() {
		if(this.numeroDimensioniCustom == null){
			return null;
		}else{
			return this.numeroDimensioniCustom.toString();
		}
	}

	public void set_value_numeroDimensioniCustom(String valueNumeroDimensioniCustom) {
		DimensioneCustom dc = (DimensioneCustom) DimensioneCustom.toEnumConstantFromString(valueNumeroDimensioniCustom);
		this.setNumeroDimensioniCustom(dc); 
	}

	public DimensioneCustom getNumeroDimensioniCustom() {
		return this.numeroDimensioniCustom;
	}

	public void setNumeroDimensioniCustom(DimensioneCustom numeroDimensioniCustom) {
		this.numeroDimensioniCustom = numeroDimensioniCustom;
	}
	
	public boolean isVisualizzaNumeroDimensioniCustom() {
		
		NumeroDimensioni numeroDimensioniToCheck = this.getNumeroDimensioni();
		
		return numeroDimensioniToCheck != null && numeroDimensioniToCheck.equals(NumeroDimensioni.DIMENSIONI_3_CUSTOM);
	}
	
	public String getLabelNumeroDimensioniCustom() {
		if(this.numeroDimensioniCustom!=null) {
			return StatsUtils.getLabel(this.numeroDimensioniCustom);
		}
		return "N.D.";
	}
	
	public boolean isVisualizzaGraficoBars() {
		TipoReport tipoReportToCheck = this.getTipoReport();
		NumeroDimensioni numeroDimensioniToCheck = this.getNumeroDimensioni();
		return (tipoReportToCheck != null && tipoReportToCheck.equals(TipoReport.BAR_CHART)) &&
				(numeroDimensioniToCheck != null && numeroDimensioniToCheck.equals(NumeroDimensioni.DIMENSIONI_2)) 
				;
	}
	
	public boolean isVisualizzaGraficoHeatmap() {
		TipoReport tipoReportToCheck = this.getTipoReport();
		NumeroDimensioni numeroDimensioniToCheck = this.getNumeroDimensioni();
		return (tipoReportToCheck != null && (tipoReportToCheck.equals(TipoReport.BAR_CHART) && this.isUseGraficiSVG())) &&
				(numeroDimensioniToCheck != null && 
					(numeroDimensioniToCheck.equals(NumeroDimensioni.DIMENSIONI_3) || numeroDimensioniToCheck.equals(NumeroDimensioni.DIMENSIONI_3_CUSTOM)
				)) 
				;
	}
	
	public void setTipoDistribuzione(String tipoDistribuzione) {
		this.tipoDistribuzione = tipoDistribuzione;
	}
	
	public String getTipoDistribuzione() {
		return this.tipoDistribuzione;
	}
	
	@Override
	public ModuloRicerca getModulo() {
		return ModuloRicerca.STATISTICHE;
	}
	
	@Override
	public String getModalitaRicerca() {
		return this.getTipoDistribuzione();
	}
	
	@Override
	public boolean isVisualizzaComandoSalvaRicerca() {
		return true;
	}
	
	@Override
	public List<String> getElencoFieldRicercaDaIgnorare() {
		return elencoFieldsRicercaDaIgnorare;
	}
}
