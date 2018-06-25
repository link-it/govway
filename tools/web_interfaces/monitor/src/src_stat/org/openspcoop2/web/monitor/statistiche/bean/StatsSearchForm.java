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
package org.openspcoop2.web.monitor.statistiche.bean;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.event.ActionEvent;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.statistiche.constants.TipoBanda;
import org.openspcoop2.core.statistiche.constants.TipoLatenza;
import org.openspcoop2.core.statistiche.constants.TipoReport;
import org.openspcoop2.core.statistiche.constants.TipoStatistica;
import org.openspcoop2.core.statistiche.constants.TipoVisualizzazione;
import org.openspcoop2.generic_project.expression.SortOrder;
import org.openspcoop2.utils.TipiDatabase;

import org.openspcoop2.monitor.sdk.constants.StatisticType;
import org.openspcoop2.web.monitor.core.utils.BrowserInfo;
import org.openspcoop2.web.monitor.core.bean.AbstractDateSearchForm;
import org.openspcoop2.web.monitor.core.bean.ApplicationBean;
import org.openspcoop2.web.monitor.core.bean.BaseSearchForm;
import org.openspcoop2.web.monitor.core.filters.BrowserFilter;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;

/**
 * StatsSearchForm
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class StatsSearchForm extends BaseSearchForm{

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

	// Indicazione se il la distribuzione per soggetto è locale o remota. Vale solo per la distribuzione per soggetto.
	// Negli altri casi è true per default per visualizzare la select list dei soggetti locali
	private boolean distribuzionePerSoggettoRemota = true;

	// Indicazione se l'andamento temporale è per esiti
	// Vale solo nell'andamento temporale e nella distribuzione per esiti
	private boolean andamentoTemporalePerEsiti = false; 

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
		this.setSortOrder(SortOrder.ASC);  
		this.tipoVisualizzazione = TipoVisualizzazione.NUMERO_TRANSAZIONI;
		this.tipoLatenza = TipoLatenza.LATENZA_TOTALE;
		this.tipoBanda = TipoBanda.COMPLESSIVA;
		this.modalitaTemporale =StatisticType.GIORNALIERA;
		this.setTipiLatenza(new String[3]);
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
	protected String eseguiFiltra() {
		
		if(validateForm()==false) {
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
	}

	@Override
	protected String ripulisciValori(){
		this.initSearchListener(null);
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

		Map<String,String>  map = new HashMap<String, String>();

		if(this.getTipiLatenza()!=null){
			int i = 0;
			for (String tipoLat : this.getTipiLatenza()) {
				if(tipoLat != null){
					if(tipoLat.equals("0"))
						map.put(""+i,TipoLatenza.LATENZA_TOTALE.getValue());
					if(tipoLat.equals("1"))
						map.put(""+i,TipoLatenza.LATENZA_SERVIZIO.getValue());
					if(tipoLat.equals("2"))
						map.put(""+i,TipoLatenza.LATENZA_PORTA.getValue());

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
		List<TipoBanda> lst = new ArrayList<TipoBanda>();

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

		Map<String,String>  map = new HashMap<String, String>();

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

		Map<String,String>  map = new HashMap<String, String>();
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
			this.modalitaTemporale = (StatisticType) StatisticType.valueOf(_value_modalitaTemporale.toUpperCase());
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

		if(this.tipoReport != null){
			if(this.tipoReport.equals(TipoReport.TABELLA))
				this.setSortOrder(SortOrder.DESC);
		}
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
	}

	public TipoStatistica getTipoStatistica() {
		return this.tipoStatistica;
	}

	public void setTipoStatistica(TipoStatistica tipoStatistica) {
		this.tipoStatistica = tipoStatistica;

		if(tipoStatistica != null)
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
			inizio.clear(Calendar.MINUTE);
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
			fine.set(Calendar.MINUTE,59);
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
		BrowserInfo browserInfo = ApplicationBean.getInstance().getBrowserInfo();
		this.useGraficiSVG =ApplicationBean.getInstance().isGraficiSvgEnabled() && !BrowserFilter.disabilitaGraficiSVG(browserInfo);

		LoggerManager.getPddMonitorCoreLogger().debug("Usa grafici SVG ["+this.useGraficiSVG+"]");


		return this.useGraficiSVG;
	}

	public void setUseGraficiSVG(boolean useGraficiSVG) {
		this.useGraficiSVG = useGraficiSVG;
	}

	private String action = null;


	private boolean useGraficiSVG = false;


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
				return "yyyy-MM-dd HH:mm";
			default:
				return "yyyy-MM-dd";
			}
		}
		return "yyyy-MM-dd";
	}
}
