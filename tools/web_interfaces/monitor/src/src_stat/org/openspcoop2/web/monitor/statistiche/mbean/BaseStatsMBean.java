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
package org.openspcoop2.web.monitor.statistiche.mbean;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpServletResponse;

import org.openspcoop2.monitor.sdk.constants.StatisticType;
import org.openspcoop2.web.monitor.core.bean.BaseSearchForm;
import org.openspcoop2.web.monitor.core.core.PddMonitorProperties;
import org.openspcoop2.web.monitor.core.mbean.DynamicPdDBean;
import org.openspcoop2.web.monitor.statistiche.bean.StatsSearchForm;
import org.openspcoop2.web.monitor.statistiche.constants.CostantiGrafici;

/**
 * BaseStatsMBean
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public abstract class BaseStatsMBean<T, K, IService> extends DynamicPdDBean<T, K, org.openspcoop2.web.monitor.core.dao.IService<T, K>>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	// visualizzazione per dimensione
	private Boolean isVisualizzaPerDimensioneEnabled;
	// tipo messaggio
	private String tipoMessaggio = "Richiesta";
	// numero di categorie da visualizzare
	private Integer slice = 12;
	private Integer minCategorie = 1;
	private Integer maxCategorie = 50;
	// controllo della larghezza del grafico
	private Integer larghezzaGrafico = 700;
	private Integer minLarghezzaGrafico = 500;
	private Integer maxLarghezzaGrafico = 2000;
	// orientamento delle label delle categorie del grafico
	private String direzioneLabel = CostantiGrafici.DIREZIONE_LABEL_OBLIQUO_LABEL;
	
	private Integer numeroLabelAsseXDistribuzioneTemporale = 12;
	private boolean nascondiComandoSelezioneNumeroLabelSeInferioreANumeroRisultati = true;
	/*
	 * chart info  
	 */
	private String data;
	private String chartId =null;
	private String filename = null;
	
	/*
	 * prev/actual/next
	 */
	private String dir;
	// comandi export 
	private boolean visualizzaComandiExport = false;
	
	// private boolean enableSpecialFilters = false;
	// private SelectItem[] specialFilters;

	public BaseStatsMBean() {
		init();
	}
	public BaseStatsMBean(org.openspcoop2.core.commons.search.dao.IServiceManager serviceManager) {
		super(serviceManager);
		init();
	}
	private void init() {
		try {
			PddMonitorProperties govwayMonitorProperties = PddMonitorProperties.getInstance(DynamicPdDBean.log);
			this.isVisualizzaPerDimensioneEnabled = govwayMonitorProperties.isAttivoStatisticheVisualizzazioneDimensione();
			this.direzioneLabel = govwayMonitorProperties.getOrientamentoDefaultLabelGrafici();
			this.numeroLabelAsseXDistribuzioneTemporale = govwayMonitorProperties.getNumeroLabelDefaultDistribuzioneTemporale();
			this.nascondiComandoSelezioneNumeroLabelSeInferioreANumeroRisultati = govwayMonitorProperties.isNascondiComandoNumeroLabelSeInferioreAlNumeroRisultati();
		} catch (Exception e) {
			DynamicPdDBean.log.error(e.getMessage(), e);
		}
	}
	
	public abstract String getExportFilename();
	public String getExportFilenamePng(){
		return this.getExportFilename() + ".png";
	}

	public abstract String esportaCsv();
	public abstract void esportaCsv(HttpServletResponse response) throws Exception;
	
	public abstract String esportaXls();
	public abstract void esportaXls(HttpServletResponse response) throws Exception;
	
	public abstract String esportaPdf();
	public abstract void esportaPdf(HttpServletResponse response) throws Exception;
	
	public abstract String esportaJson();
	public abstract void esportaJson(HttpServletResponse response) throws Exception;
	
	public abstract String esportaXml();
	public abstract void esportaXml(HttpServletResponse response) throws Exception;
	
	
	public boolean isVisualizzaComandiExport() {
		return this.visualizzaComandiExport;
	}

	public void setVisualizzaComandiExport(boolean visualizzaComandiExport) {
		this.visualizzaComandiExport = visualizzaComandiExport;
	}

	public Integer getSlice() {
		return this.slice;
	}

	public void setSlice(Integer slice) {
		this.slice = slice;
	}
	
	public Integer getMinCategorie() {
		return this.minCategorie;
	}

	public void setMinCategorie(Integer minCategorie) {
		this.minCategorie = minCategorie;
	}

	public Integer getMaxCategorie() {
		return this.maxCategorie;
	}

	public void setMaxCategorie(Integer maxCategorie) {
		this.maxCategorie = maxCategorie;
	}

	public Integer getLarghezzaGrafico() {
		return this.larghezzaGrafico;
	}

	public void setLarghezzaGrafico(Integer larghezzaGrafico) {
		this.larghezzaGrafico = larghezzaGrafico;
	}
	
	public Integer getInitLarghezzaGrafico() {
		return this.larghezzaGrafico;
	}

	public void setInitLarghezzaGrafico(Integer larghezzaGrafico) {
		this.larghezzaGrafico = larghezzaGrafico;
		//ricalcolare il max e min
		this.maxLarghezzaGrafico = 4 * this.larghezzaGrafico;
		this.minLarghezzaGrafico = (int) (this.maxLarghezzaGrafico / 100);
	}
	
	public Integer getMinLarghezzaGrafico() {
		return this.minLarghezzaGrafico;
	}
	public void setMinLarghezzaGrafico(Integer minLarghezzaGrafico) {
		this.minLarghezzaGrafico = minLarghezzaGrafico;
	}
	public Integer getMaxLarghezzaGrafico() {
		return this.maxLarghezzaGrafico;
	}
	public void setMaxLarghezzaGrafico(Integer maxLarghezzaGrafico) {
		this.maxLarghezzaGrafico = maxLarghezzaGrafico;
	}
	public String getDirezioneLabel() {
		return this.direzioneLabel;
	}

	public void setDirezioneLabel(String direzioneLabel) {
		this.direzioneLabel = direzioneLabel;
	}
	
	public String getDir() {
		return this.dir;
	}

	public void setDir(String dir) {
		this.dir = dir;
	}
	
	public String getTipoMessaggio() {
		return this.tipoMessaggio;
	}

	public void setTipoMessaggio(String tipoMessaggio) {
		this.tipoMessaggio = tipoMessaggio;
	}
	
	public Boolean getIsVisualizzaPerDimensioneEnabled() {
		return this.isVisualizzaPerDimensioneEnabled;
	}

	public String getData() {
		return this.data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getChartId() {
		return this.chartId;
	}

	public void setChartId(String chartId) {
		this.chartId = chartId;
	}

	public String getFilename() {
		return this.filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public void updateChartDateOffset(ActionEvent e) {
		this.spostaIntervalloTemporale(e,this.getDir(),this.search);
	}

	public void spostaIntervalloTemporale(ActionEvent e, String dir, BaseSearchForm search) {
		Calendar c = Calendar.getInstance();
		int amount = 0;
		int field = 0;

		boolean spostamentoPerOra = false;
		
		if (CostantiGrafici.PREV.equals(dir)) {
			amount = -1;
		} else if (CostantiGrafici.NEXT.equals(dir)) {
			amount = +1;
		} else {
			search.setPeriodo(search.getPeriodo());
		}

		if (CostantiGrafici.IERI_LABEL.equals(search.getPeriodo())) {
			field = Calendar.DATE;
		} 
		else if (CostantiGrafici.ULTIME_12_ORE_LABEL.equals(search.getPeriodo())) {
			field = Calendar.HOUR_OF_DAY;
			spostamentoPerOra = true;
		} 
		else if (CostantiGrafici.ULTIME_24_ORE_LABEL.equals(search.getPeriodo())) {
			field = Calendar.DATE;
		} 
		else if (CostantiGrafici.ULTIMA_SETTIMANA_LABEL.equals(search.getPeriodo())) {
			//field = Calendar.WEEK_OF_YEAR;
			field = Calendar.DAY_OF_YEAR;
			if(amount!=0){
				if(amount > 0){
					amount = +7;
				}
				else{
					amount = -7;
				}
			}
		} else if (CostantiGrafici.ULTIMO_MESE_LABEL.equals(search.getPeriodo())) {
			//field = Calendar.MONTH;
			field = Calendar.DAY_OF_YEAR;
			if(amount!=0){
				if(amount > 0){
					amount = +30;
				}
				else{
					amount = -30;
				}
			}
		} 
		else if (CostantiGrafici.ULTIMO_ANNO_LABEL.equals(search.getPeriodo())) {
			field = Calendar.YEAR;
		} 
		else if (CostantiGrafici.PERSONALIZZATO_LABEL.equals(search.getPeriodo())) {
			// controllo distanza
			long dist = search.getDataFine().getTime()
					- search.getDataInizio().getTime() +1;

			long hour = Math.round(dist / 3600000);
			long day = Math.round(hour / 24);
			// se distanza e' < 24h mi sposto di ora in ora
			if (hour < 24) {
				field = Calendar.HOUR_OF_DAY;
				spostamentoPerOra = true;
			}
			// se distanza < 7 giorni mi sposto di giorno in giorno
			if (hour >= 24 && day < 7) {
				field = Calendar.DATE;
			}
			// se distanza < 30 giorni mi sposto di settimana in settimana
			if (day >= 7 && day < 30) {
				//field = Calendar.WEEK_OF_YEAR;
				field = Calendar.DAY_OF_YEAR;
				if(amount!=0){
					if(amount > 0){
						amount = +7;
					}
					else{
						amount = -7;
					}
				}
			}
			// se distanza >=30 giorni mi sposto di mese in mese
			if (day >= 30) {
				//field = Calendar.MONTH;
				field = Calendar.DAY_OF_YEAR;
				if(amount!=0){
					if(amount > 0){
						amount = +30;
					}
					else{
						amount = -30;
					}
				}
			}
		}

		if(amount!=0){
			
			if(spostamentoPerOra){
				c.setTime(search.getDataInizio());
				c.add(field, amount);
				search.setDataInizio(c.getTime());
		
				c.setTime(search.getDataFine());
				c.add(field, amount);
				search.setDataFine(c.getTime());
			}
			else{

				if(amount>0){
					// dataInizio
					c.setTime((Date)search.getDataFine().clone());
					c.add(Calendar.MILLISECOND, +1);
					search.setDataInizio(c.getTime());
					
					// dataFine
					c.setTime(search.getDataFine());
					c.add(field, amount);
					search.setDataFine(c.getTime());
				}
				else{
					// dataFine
					c.setTime((Date)search.getDataInizio().clone());
					c.add(Calendar.MILLISECOND, -1);
					search.setDataFine(c.getTime());
					
					// dataInizio
					c.setTime(search.getDataInizio());
					c.add(field, amount);
					search.setDataInizio(c.getTime());
				}
				
			}
			
		}
		else{
			search.setDataInizio(((StatsSearchForm)search).getDataInizioDellaRicerca());
			search.setDataFine(((StatsSearchForm)search).getDataFineDellaRicerca());
			search.setPeriodo(((StatsSearchForm)search).getPeriodoDellaRicerca());
		}
	}
	
	public String btnLblPrefix(BaseSearchForm search) {
		String lbl = "";
		if (CostantiGrafici.IERI_LABEL.equals( search.getPeriodo())) {
			lbl = CostantiGrafici.GIORNO_LABEL + CostantiGrafici.WHITE_SPACE;
		} 
		else if (CostantiGrafici.ULTIME_12_ORE_LABEL.equals(search.getPeriodo())) {
			lbl = CostantiGrafici.ORA_LABEL + CostantiGrafici.WHITE_SPACE;
		}
		else if (CostantiGrafici.ULTIME_24_ORE_LABEL.equals(search.getPeriodo())) {
			lbl = CostantiGrafici.GIORNO_LABEL + CostantiGrafici.WHITE_SPACE;
		}
		else if (CostantiGrafici.ULTIMA_SETTIMANA_LABEL.equals( search.getPeriodo())) {
			lbl = CostantiGrafici.SETTIMANA_LABEL + CostantiGrafici.WHITE_SPACE;
		} 
		else if (CostantiGrafici.ULTIMO_MESE_LABEL.equals( search.getPeriodo())) {
			lbl = CostantiGrafici.MESE_LABEL + CostantiGrafici.WHITE_SPACE;
		} 
		else if (CostantiGrafici.ULTIMO_ANNO_LABEL.equals( search.getPeriodo())) {
			lbl = CostantiGrafici.ANNO_LABEL + CostantiGrafici.WHITE_SPACE;
		} 
		else {
			if( search.getDataInizio() != null && search.getDataFine() != null){
				// controllo distanza
				long dist =  search.getDataFine().getTime()
						- search.getDataInizio().getTime() +1;

				long hour = Math.round(dist / 3600000);
				long day = Math.round(hour / 24);
				// se distanza e' < 24h mi sposto di ora in ora
				if (hour < 24) {
					lbl = CostantiGrafici.ORA_LABEL + CostantiGrafici.WHITE_SPACE;
				}
				// se distanza < 7 giorni mi sposto di giorno in giorno
				if (hour >= 24 && day < 7) {
					lbl = CostantiGrafici.GIORNO_LABEL + CostantiGrafici.WHITE_SPACE;
				}
				// se distanza < 30 giorni mi sposto di settimana in settimana
				if (day >= 7 && day < 30) {
					lbl = CostantiGrafici.SETTIMANA_LABEL + CostantiGrafici.WHITE_SPACE;
				}
				// se distanza >=30 giorni mi sposto di mese in mese
				if (day >= 30) {
					lbl = CostantiGrafici.MESE_LABEL + CostantiGrafici.WHITE_SPACE;
				}
			}else{
				lbl = CostantiGrafici.INTERVALLO_LABEL + CostantiGrafici.WHITE_SPACE;
			}
		}
		return lbl;
	}
	
	public String getPrevBtnLabel() {
		return this.btnLblPrefix(this.search) + CostantiGrafici.WHITE_SPACE + CostantiGrafici.LABEL_PREC + CostantiGrafici.PUNTO;
	}

	public String getActualBtnLabel() {
		return this.btnLblPrefix(this.search) + CostantiGrafici.WHITE_SPACE + CostantiGrafici.LABEL_ATTUALE + CostantiGrafici.PUNTO;
	}

	public String getNextBtnLabel() {
		return this.btnLblPrefix(this.search) + CostantiGrafici.WHITE_SPACE + CostantiGrafici.LABEL_SUCC + CostantiGrafici.PUNTO; 
	}
	
	public String formatDate(Date date, boolean oraMinutiObbligatori) {
		Calendar c = null;
		try{
			c = org.openspcoop2.utils.date.DateManager.getCalendar();
		}catch(Exception e){
			c = Calendar.getInstance();
		}
		c.setTime(date);
//		int hour = c.get(Calendar.HOUR_OF_DAY);
//		int minute = c.get(Calendar.MINUTE);
//		int second = c.get(Calendar.SECOND);
//		int millisecond = c.get(Calendar.MILLISECOND);
//				
		StringBuilder bf = new StringBuilder("");
//		if(millisecond>0){
//			bf.append(" HH:mm:ss.SSS");
//		}
//		else if(second>0){
//			bf.append(" HH:mm:ss");
//		}
//		else if(minute>0){
//			bf.append(" HH:mm");
//		}
//		else if(hour>0){
//			bf.append(" HH:mm"); // metto comunque i minuti
//		}
//		else if(oraMinutiObbligatori){
		if(oraMinutiObbligatori){
			bf.append(CostantiGrafici.WHITE_SPACE + CostantiGrafici.PATTERN_HH_MM);
		}
		SimpleDateFormat sdf = new SimpleDateFormat(CostantiGrafici.PATTERN_DD_MMMMM_YYYY+bf.toString(), Locale.ITALIAN);
		
		//SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS", Locale.ITALIAN);
		return sdf.format(date);
		
	}
	
	public String submit() {
		return null;
	}
	
	public StatisticType getTempo() {
		StatisticType modalita = ((StatsSearchForm)this.search).getModalitaTemporale();

		if (modalita == null)
			return StatisticType.GIORNALIERA;
		return modalita;
	}

	public String get_value_tempo() {
		StatisticType modalita = ((StatsSearchForm)this.search).getModalitaTemporale();

		if (modalita == null)
			return StatisticType.GIORNALIERA.toString().toLowerCase();


		return modalita.toString().toLowerCase();
	}
	
	public String getLabelPaginaReport() {
		return CostantiGrafici.PAGINA_REPORT_LABEL;
	}
	public Integer getNumeroLabelAsseXDistribuzioneTemporale() {
		return this.numeroLabelAsseXDistribuzioneTemporale;
	}
	public void setNumeroLabelAsseXDistribuzioneTemporale(Integer numeroLabelAsseXDistribuzioneTemporale) {
		this.numeroLabelAsseXDistribuzioneTemporale = numeroLabelAsseXDistribuzioneTemporale;
	}
	public boolean isNascondiComandoSelezioneNumeroLabelSeInferioreANumeroRisultati() {
		return this.nascondiComandoSelezioneNumeroLabelSeInferioreANumeroRisultati;
	}
	public void setNascondiComandoSelezioneNumeroLabelSeInferioreANumeroRisultati(
			boolean nascondiComandoSelezioneNumeroLabelSeInferioreANumeroRisultati) {
		this.nascondiComandoSelezioneNumeroLabelSeInferioreANumeroRisultati = nascondiComandoSelezioneNumeroLabelSeInferioreANumeroRisultati;
	}
}
