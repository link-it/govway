/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it).
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

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.faces.application.Application;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpServletResponse;

import org.openspcoop2.core.statistiche.constants.TipoBanda;
import org.openspcoop2.core.statistiche.constants.TipoLatenza;
import org.openspcoop2.core.statistiche.constants.TipoReport;
import org.openspcoop2.core.statistiche.constants.TipoVisualizzazione;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.utils.transport.http.HttpUtilities;

import org.openspcoop2.monitor.sdk.constants.StatisticType;
import org.openspcoop2.web.monitor.core.dao.IService;
import org.openspcoop2.web.monitor.core.datamodel.Res;
import org.openspcoop2.web.monitor.core.datamodel.ResBase;
import org.openspcoop2.web.monitor.core.mbean.DynamicPdDBean;
import org.openspcoop2.web.monitor.core.utils.MessageManager;
import org.openspcoop2.web.monitor.core.utils.MessageUtils;
import org.openspcoop2.web.monitor.statistiche.bean.StatsSearchForm;
import org.openspcoop2.web.monitor.statistiche.constants.CostantiGrafici;
import org.openspcoop2.web.monitor.statistiche.constants.StatisticheCostanti;
import org.openspcoop2.web.monitor.statistiche.dao.IStatisticheGiornaliere;
import org.openspcoop2.web.monitor.statistiche.utils.ExportUtils;
import org.openspcoop2.web.monitor.statistiche.utils.JsonStatsUtils;
import org.openspcoop2.web.monitor.statistiche.utils.StatsUtils;
import net.sf.dynamicreports.jasper.builder.JasperReportBuilder;
import net.sf.json.JSONObject;

/**
 * AndamentoTemporaleBean
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class AndamentoTemporaleBean extends
BaseStatsMBean<ResBase, Integer, IService<ResBase, Integer>> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private boolean visualizzaComandiSelezioneNumeroLabel; 

	public AndamentoTemporaleBean() {
		super();
		this.init();
	}
	public AndamentoTemporaleBean(org.openspcoop2.core.commons.search.dao.IServiceManager serviceManager) {
		super(serviceManager);
		this.init();
	}
	private void init() {
		this.setSlice(this.getNumeroLabelAsseXDistribuzioneTemporale());
		this.setMaxCategorie(Integer.MAX_VALUE);
		this.setMinCategorie(2);
		this.visualizzaComandiSelezioneNumeroLabel = true;
	}
	
	public void setStatisticheGiornaliereService(
			IStatisticheGiornaliere statisticheGiornaliereService) {
		this.service =  (IService<ResBase, Integer>) statisticheGiornaliereService;
	}

	public void initSearchListenerAndamentoTemporale(ActionEvent ae){
		((StatsSearchForm)this.search).setAndamentoTemporalePerEsiti(false);
		this.search.initSearchListener(ae);
	}
	public void initSearchListenerDistribuzionePerEsiti(ActionEvent ae){
		((StatsSearchForm)this.search).setAndamentoTemporalePerEsiti(true);
		this.search.initSearchListener(ae);
	}
	
	public String getXml() {
		List<Res> list = null;
		String xml = "";
		this.setVisualizzaComandiExport(false);
		try {
			list = ((IStatisticheGiornaliere)this.service).findAllAndamentoTemporale();
		} catch (ServiceException e) {
			MessageUtils.addErrorMsg("Si e' verificato un errore durante il recupero dei dati:"	+ e.getMessage());
			DynamicPdDBean.log.error(e.getMessage(), e);
			return null;
		}

		StatisticType tempo = this.getTempo();
		
		TipoReport tipoReport = ((StatsSearchForm)this.search).getTipoReport();
		 xml = "";
		switch (tipoReport) {
		case BAR_CHART:
			 xml = StatsUtils.getXmlBarChartDistribuzione(list,(StatsSearchForm) this.search, this.getCaption(), this.getSubCaption(), this.getSlice(), tempo);
			break;
		case LINE_CHART:
			 xml = StatsUtils.getXmlAndamentoTemporale(list, ((StatsSearchForm)this.search), this.getCaption(), this.getSubCaption() , tempo);
			break;
		default:
			break;
		}
		
		
		if(list != null && list.size() > 0)
			this.setVisualizzaComandiExport(true);

		return  xml;
	}
	public String getJson(){
		return this._getJson(true);
	}
	
	private String _getJson(boolean nuovaRicerca){
		JSONObject grafico = null;
		List<Res> list = null;
		this.setVisualizzaComandiExport(false);
		try {
			list = ((IStatisticheGiornaliere)this.service).findAllAndamentoTemporale();
		} catch (ServiceException e) {
			MessageUtils.addErrorMsg("Si e' verificato un errore durante il recupero dei dati:"	+ e.getMessage());
			DynamicPdDBean.log.error(e.getMessage(), e);
			return null;
		}
		
		StatisticType tempo = this.getTempo();
		TipoReport tipoReport = ((StatsSearchForm)this.search).getTipoReport();
		
		
		
		switch (tipoReport) {
		case BAR_CHART:
			if(nuovaRicerca && list != null && list.size() > 0) {
				// reset valore numero label
				this.setSlice(this.getNumeroLabelAsseXDistribuzioneTemporale());
				this.setMaxCategorie(list.size());
				if(this.isNascondiComandoSelezioneNumeroLabelSeInferioreANumeroRisultati()) {
					if(this.getSlice() >= list.size())
						this.visualizzaComandiSelezioneNumeroLabel = false;
				}
			}
			
			grafico = JsonStatsUtils.getJsonBarChartDistribuzione(list,(StatsSearchForm) this.search, this.getCaption(), this.getSubCaption(), this.getDirezioneLabel(), Integer.MAX_VALUE, this.getSlice(), tempo);
			break;
		case LINE_CHART:
			if(nuovaRicerca && list != null && list.size() > 0) {
				// reset valore numero label
				this.setSlice(this.getNumeroLabelAsseXDistribuzioneTemporale());
				this.setMaxCategorie(list.size());
				
				SimpleDateFormat sdf;
				if (StatisticType.ORARIA.equals(tempo)) {
					sdf = new SimpleDateFormat(CostantiGrafici.PATTERN_DD_MM_YY_HH, Locale.ITALIAN);
				} else {
					sdf = new SimpleDateFormat(CostantiGrafici.PATTERN_DD_MM_YY, Locale.ITALIAN);
				}
				if(StatsUtils.addEstremoSX(list, ((StatsSearchForm)this.search), tempo, sdf)) {
					this.setMaxCategorie(this.getMaxCategorie() + 1); 
				}
				if(StatsUtils.addEstremoDX(list, ((StatsSearchForm)this.search), tempo, sdf)) {
					this.setMaxCategorie(this.getMaxCategorie() + 1);
				}
				
				if(this.isNascondiComandoSelezioneNumeroLabelSeInferioreANumeroRisultati()) {
					if(this.getSlice() >= list.size())
						this.visualizzaComandiSelezioneNumeroLabel = false;
				}
			}
			
			
			grafico = JsonStatsUtils.getJsonAndamentoTemporale(list, ((StatsSearchForm)this.search), this.getCaption(), this.getSubCaption() , tempo, this.getDirezioneLabel(), this.getSlice());
			
			break;
		default:
			break;
		}
		
		if(list != null && list.size() > 0)
			this.setVisualizzaComandiExport(true);
		
		String json = grafico != null ?  grafico.toString() : "";
		//log.debug(json); 
		return json ;
	}
	
	
	@Override
	public String getData(){
		if(((StatsSearchForm)this.search).isUseGraficiSVG())
			return this.getJson();
		
		return this.getXml();
	}
	
	public String getDataNumeroLabel(){
		if(((StatsSearchForm)this.search).isUseGraficiSVG())
			return this._getJson(false);
		
		return this.getXml();
	}

	public String getCaption() {
		StringBuilder sb = new StringBuilder();
		if(((StatsSearchForm)this.search).isAndamentoTemporalePerEsiti()){
			sb.append(MessageManager.getInstance().getMessage(StatisticheCostanti.STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_ESITI_LABEL_KEY)).append(CostantiGrafici.WHITE_SPACE);
		}
		else{
			sb.append(MessageManager.getInstance().getMessage(StatisticheCostanti.STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_TEMPORALE_LABEL_KEY)).append(CostantiGrafici.WHITE_SPACE);
		}
		if (StatisticType.GIORNALIERA.equals(this.getTempo())) {
			sb.append(CostantiGrafici.GIORNALIERA_LABEL).append(CostantiGrafici.WHITE_SPACE);
		} else if (StatisticType.ORARIA.equals(this.getTempo())) {
			sb.append(CostantiGrafici.ORARIA_LABEL).append(CostantiGrafici.WHITE_SPACE);
		} else if (StatisticType.MENSILE.equals(this.getTempo())) {
			sb.append(CostantiGrafici.MENSILE_LABEL).append(CostantiGrafici.WHITE_SPACE);
		} else if (StatisticType.SETTIMANALE.equals(this.getTempo())) {
			sb.append(CostantiGrafici.SETTIMANALE_LABEL).append(CostantiGrafici.WHITE_SPACE);
		} else {
			sb.append(CostantiGrafici.GIORNALIERA_LABEL).append(CostantiGrafici.WHITE_SPACE);
		}
		return sb.toString();
	}

	public String getSubCaption() {
		String captionText = StatsUtils.getSubCaption((StatsSearchForm)this.search);

		StringBuilder caption = new StringBuilder(captionText);

		if(this.search.getDataInizio() != null && this.search.getDataFine() != null){
			if (StatisticType.ORARIA.equals(this.getTempo()) || this.btnLblPrefix(this.search).toLowerCase().contains(CostantiGrafici.ORA_KEY)) {
				caption.append(MessageFormat.format(CostantiGrafici.DAL_AL_PATTERN, this.formatDate(this.search.getDataInizio(),true), this.formatDate(this.search.getDataFine(),true))); 
			} else {
				caption.append(MessageFormat.format(CostantiGrafici.DAL_AL_PATTERN, this.formatDate(this.search.getDataInizio(),false), this.formatDate(this.search.getDataFine(),false))); 
			}
		}
		return caption.toString();
	}

	public void newSearch(ActionEvent ae) {

		FacesContext facesContext = FacesContext.getCurrentInstance();
		Application app = facesContext.getApplication();
		ExpressionFactory elFactory = app.getExpressionFactory();
		ELContext elContext = facesContext.getELContext();
		ValueExpression valueExp = elFactory.createValueExpression(elContext,
				"#{andamentoTemporaleBean}", AndamentoTemporaleBean.class);
		AndamentoTemporaleBean ab = new AndamentoTemporaleBean();
		valueExp.setValue(elContext, ab);
	}

	@Override
	public String submit() {
		// Non e' piu necessario selezionare il soggetto in gestione, con
		// l'introduzione
		// di poter associare piu soggetti spcoop ad un customer_user
		// il soggetto in gestione potra' essere selezionato come filtro nel
		// form di ricerca
		// in caso non fosse selezionato allora vengono presi in considerazione
		// tutti i soggetti associati al soggetto loggato
		// if(Utility.getSoggettoInGestione()==null){
		// MessageUtils.addErrorMsg("E' necessario selezionare il Soggetto.");
		// return null;
		// }
		return "andamentoTemporale";
	}

	public String getSommaColumnHeader(){
		return StatsUtils.sommaColumnHeader((StatsSearchForm)this.search, CostantiGrafici.MESSAGGI_LABEL);
	}

	public Map<String, String> getColumnHeadersMap() {
		if(((StatsSearchForm) this.search).isAndamentoTemporalePerEsiti()){
			return ((StatsSearchForm) this.search).getHeaderColonneEsiti();
		}
		else{
			if(TipoVisualizzazione.DIMENSIONE_TRANSAZIONI.equals(((StatsSearchForm)this.search).getTipoVisualizzazione())){
				return ((StatsSearchForm) this.search).getHeaderColonneTipiBandaImpostati();
			}
			else{
				return ((StatsSearchForm) this.search).getHeaderColonneTipiLatenzaImpostati();
			}
		}
	}

	@Override
	public String esportaCsv() {
		try{
			return this._esportaCsv(null, true);
		}catch(Exception e){
			// in questo caso l'eccezione non viene mai lanciata dal metodo (useFaceContext==true)
			// Il codice sottostante e' solo per sicurezza
			DynamicPdDBean.log.error(e.getMessage(), e);
			MessageUtils.addErrorMsg("Si e' verificato un errore inatteso:"	+ e.getMessage());
			return null;
		}
	}
	@Override
	public void esportaCsv(HttpServletResponse response) throws Exception {
		this._esportaCsv(response, false);
	}
	private String _esportaCsv(HttpServletResponse responseParam, boolean useFaceContext) throws Exception {
		log.debug("Export in formato CSV in corso...."); 
		String fileExt = CostantiGrafici.CSV_EXTENSION;
		String filename = this.getExportFilename()+fileExt;

		List<Res> list = null;
		try {
			list = ((IStatisticheGiornaliere)this.service).findAllAndamentoTemporale();
			if(list==null || list.size()<=0){
				// passando dalla console, questo caso non succede mai, mentre tramite http get nel servizio di exporter può succedere
				throw new NotFoundException("Dati non trovati");
			}
		} catch (Exception e) {
			if(useFaceContext){
				DynamicPdDBean.log.error(e.getMessage(), e);
				MessageUtils.addErrorMsg("Si e' verificato un errore durante il recupero dei dati:"	+ e.getMessage());
				return null;
			}
			else{
				DynamicPdDBean.log.debug(e.getMessage(), e);
				throw e;
			}
		}

		try {	

			HttpServletResponse response = null;
			FacesContext context = null;
			if(useFaceContext){
				// We must get first our context
				context = FacesContext.getCurrentInstance();
	
				// Then we have to get the Response where to write our file
				response = (HttpServletResponse) context
						.getExternalContext().getResponse();
			}
			else{
				response = responseParam;
			}

			response.reset();
			
			// Setto Proprietà Export File
			HttpUtilities.setOutputFile(response, true, filename);
			
			response.setStatus(200);

			String titoloReport = this.getCaption() + CostantiGrafici.WHITE_SPACE + this.getSubCaption();
			String headerLabel = CostantiGrafici.DATA_LABEL;
			
			TipoVisualizzazione tipoVisualizzazione = ((StatsSearchForm)this.search).getTipoVisualizzazione();
			List<TipoBanda> tipiBanda =  ((StatsSearchForm)this.search).getTipiBandaImpostati();
			List<TipoLatenza> tipiLatenza =  ((StatsSearchForm)this.search).getTipiLatenzaImpostati();
			StatisticType modalitaTemporale = ((StatsSearchForm)this.search).getModalitaTemporale();
			// creazione del report con Dynamic Report
			JasperReportBuilder report = ExportUtils.creaReportAndamentoTemporale(list, titoloReport, log, tipoVisualizzazione, tipiBanda, tipiLatenza, modalitaTemporale,
					((StatsSearchForm)this.search).isAndamentoTemporalePerEsiti(), false);
			
			// scrittura del report sullo stream
			ExportUtils.esportaCsv(response.getOutputStream(),report,titoloReport,headerLabel,tipoVisualizzazione,tipiBanda,tipiLatenza,((StatsSearchForm)this.search).getTipoStatistica(),null,
					((StatsSearchForm)this.search).isAndamentoTemporalePerEsiti());

			if(useFaceContext){
				context.responseComplete();
			}

			// End of the method
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			if(useFaceContext){
				FacesContext.getCurrentInstance().responseComplete();
				MessageUtils.addErrorMsg(CostantiGrafici.CSV_EXPORT_MESSAGGIO_ERRORE);
			}
			else{
				throw e;
			}
		}

		return null;
	}

	@Override
	public String esportaXls() {
		try{
			return this._esportaXls(null, true);
		}catch(Exception e){
			// in questo caso l'eccezione non viene mai lanciata dal metodo (useFaceContext==true)
			// Il codice sottostante e' solo per sicurezza
			DynamicPdDBean.log.error(e.getMessage(), e);
			MessageUtils.addErrorMsg("Si e' verificato un errore inatteso:"	+ e.getMessage());
			return null;
		}
	}
	@Override
	public void esportaXls(HttpServletResponse response) throws Exception {
		this._esportaXls(response, false);
	}
	private String _esportaXls(HttpServletResponse responseParam, boolean useFaceContext) throws Exception {
		log.debug("Export in formato XLS in corso...."); 
		String fileExt = CostantiGrafici.XLS_EXTENSION;
		String filename = this.getExportFilename()+fileExt;

		List<Res> list = null;
		try {
			list = ((IStatisticheGiornaliere)this.service).findAllAndamentoTemporale();
			if(list==null || list.size()<=0){
				// passando dalla console, questo caso non succede mai, mentre tramite http get nel servizio di exporter può succedere
				throw new NotFoundException("Dati non trovati");
			}
		} catch (Exception e) {
			if(useFaceContext){
				DynamicPdDBean.log.error(e.getMessage(), e);
				MessageUtils.addErrorMsg("Si e' verificato un errore durante il recupero dei dati:"	+ e.getMessage());
				return null;
			}
			else{
				DynamicPdDBean.log.debug(e.getMessage(), e);
				throw e;
			}
		}

		try {	

			HttpServletResponse response = null;
			FacesContext context = null;
			if(useFaceContext){
				// We must get first our context
				context = FacesContext.getCurrentInstance();
	
				// Then we have to get the Response where to write our file
				response = (HttpServletResponse) context
						.getExternalContext().getResponse();
			}
			else{
				response = responseParam;
			}

			response.reset();
			
			// Setto Proprietà Export File
			HttpUtilities.setOutputFile(response, true, filename);
			
			response.setStatus(200);

			String titoloReport = this.getCaption() + CostantiGrafici.WHITE_SPACE + this.getSubCaption();
			String headerLabel = CostantiGrafici.DATA_LABEL;
			
			TipoVisualizzazione tipoVisualizzazione = ((StatsSearchForm)this.search).getTipoVisualizzazione();
			List<TipoBanda> tipiBanda =  ((StatsSearchForm)this.search).getTipiBandaImpostati();
			List<TipoLatenza> tipiLatenza =  ((StatsSearchForm)this.search).getTipiLatenzaImpostati();
			StatisticType modalitaTemporale = ((StatsSearchForm)this.search).getModalitaTemporale();
			// creazione del report con Dynamic Report
			JasperReportBuilder report = ExportUtils.creaReportAndamentoTemporale(list, titoloReport, log, tipoVisualizzazione, tipiBanda,tipiLatenza, modalitaTemporale,
					((StatsSearchForm)this.search).isAndamentoTemporalePerEsiti(), false); 

			// scrittura del report sullo stream
			ExportUtils.esportaXls(response.getOutputStream(),report,titoloReport,headerLabel,tipoVisualizzazione,tipiBanda,tipiLatenza,((StatsSearchForm)this.search).getTipoStatistica(),null,
					((StatsSearchForm)this.search).isAndamentoTemporalePerEsiti());
			response.flushBuffer();

			if(useFaceContext){
				context.responseComplete();
			}

			// End of the method
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			if(useFaceContext){
				FacesContext.getCurrentInstance().responseComplete();
				MessageUtils.addErrorMsg(CostantiGrafici.XLS_EXPORT_MESSAGGIO_ERRORE);
			}
			else{
				throw e;
			}
		}

		return null;
	}

	@Override
	public String esportaPdf() {
		try{
			return this._esportaPdf(null, true);
		}catch(Exception e){
			// in questo caso l'eccezione non viene mai lanciata dal metodo (useFaceContext==true)
			// Il codice sottostante e' solo per sicurezza
			DynamicPdDBean.log.error(e.getMessage(), e);
			MessageUtils.addErrorMsg("Si e' verificato un errore inatteso:"	+ e.getMessage());
			return null;
		}
	}
	@Override
	public void esportaPdf(HttpServletResponse response) throws Exception {
		this._esportaPdf(response, false);
	}
	private String _esportaPdf(HttpServletResponse responseParam, boolean useFaceContext) throws Exception {
		log.debug("Export in formato PDF in corso...."); 
		String fileExt = CostantiGrafici.PDF_EXTENSION;
		String filename = this.getExportFilename()+fileExt;

		List<Res> list = null;
		try {
			list = ((IStatisticheGiornaliere)this.service).findAllAndamentoTemporale();
			if(list==null || list.size()<=0){
				// passando dalla console, questo caso non succede mai, mentre tramite http get nel servizio di exporter può succedere
				throw new NotFoundException("Dati non trovati");
			}
		} catch (Exception e) {
			if(useFaceContext){
				DynamicPdDBean.log.error(e.getMessage(), e);
				MessageUtils.addErrorMsg("Si e' verificato un errore durante il recupero dei dati:"	+ e.getMessage());
				return null;
			}
			else{
				DynamicPdDBean.log.debug(e.getMessage(), e);
				throw e;
			}
		}

		try {	

			HttpServletResponse response = null;
			FacesContext context = null;
			if(useFaceContext){
				// We must get first our context
				context = FacesContext.getCurrentInstance();
	
				// Then we have to get the Response where to write our file
				response = (HttpServletResponse) context
						.getExternalContext().getResponse();
			}
			else{
				response = responseParam;
			}

			response.reset();
			
			// Setto Proprietà Export File
			HttpUtilities.setOutputFile(response, true, filename);
			
			response.setStatus(200);

			String titoloReport = this.getCaption() + CostantiGrafici.WHITE_SPACE + this.getSubCaption();
			String headerLabel = CostantiGrafici.DATA_LABEL;
			
			TipoVisualizzazione tipoVisualizzazione = ((StatsSearchForm)this.search).getTipoVisualizzazione();
			List<TipoBanda> tipiBanda =  ((StatsSearchForm)this.search).getTipiBandaImpostati();
			List<TipoLatenza> tipiLatenza =  ((StatsSearchForm)this.search).getTipiLatenzaImpostati();
			StatisticType modalitaTemporale = ((StatsSearchForm)this.search).getModalitaTemporale();
			// creazione del report con Dynamic Report
			JasperReportBuilder report = ExportUtils.creaReportAndamentoTemporale(list, titoloReport, log, tipoVisualizzazione, tipiBanda, tipiLatenza, modalitaTemporale,
					((StatsSearchForm)this.search).isAndamentoTemporalePerEsiti(), true);

			// scrittura del report sullo stream
			ExportUtils.esportaPdf(response.getOutputStream(),report,titoloReport,headerLabel,tipoVisualizzazione,tipiBanda,tipiLatenza,((StatsSearchForm)this.search).getTipoStatistica(),null,
					((StatsSearchForm)this.search).isAndamentoTemporalePerEsiti());

			if(useFaceContext){
				context.responseComplete();
			}

			// End of the method
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			if(useFaceContext){
				FacesContext.getCurrentInstance().responseComplete();
				MessageUtils.addErrorMsg(CostantiGrafici.PDF_EXPORT_MESSAGGIO_ERRORE);
			}
			else{
				throw e;
			}
		}

		return null;
	}
	
	@Override
	public String esportaXml() {
		try{
			return this._esportaXml(null, true);
		}catch(Exception e){
			// in questo caso l'eccezione non viene mai lanciata dal metodo (useFaceContext==true)
			// Il codice sottostante e' solo per sicurezza
			DynamicPdDBean.log.error(e.getMessage(), e);
			MessageUtils.addErrorMsg("Si e' verificato un errore inatteso:"	+ e.getMessage());
			return null;
		}
	}
	@Override
	public void esportaXml(HttpServletResponse response) throws Exception {
		this._esportaXml(response, false);
	}
	private String _esportaXml(HttpServletResponse responseParam, boolean useFaceContext) throws Exception {
		log.debug("Export in formato XML in corso...."); 
		String fileExt = CostantiGrafici.XML_EXTENSION;
		String filename = this.getExportFilename()+fileExt;

		try {	

			HttpServletResponse response = null;
			FacesContext context = null;
			if(useFaceContext){
				// We must get first our context
				context = FacesContext.getCurrentInstance();

				// Then we have to get the Response where to write our file
				response = (HttpServletResponse) context
						.getExternalContext().getResponse();
			}
			else{
				response = responseParam;
			}

			response.reset();

			// Setto Proprietà Export File
			HttpUtilities.setOutputFile(response, true, filename);

			response.setStatus(200);

			response.getOutputStream().write(this.getXml().getBytes());
			response.getOutputStream().flush();
			
			if(useFaceContext){
				context.responseComplete();
			}

			// End of the method
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			if(useFaceContext){
				FacesContext.getCurrentInstance().responseComplete();
				MessageUtils.addErrorMsg(CostantiGrafici.XML_EXPORT_MESSAGGIO_ERRORE);
			}
			else{
				throw e;
			}
		}

		return null;
	}
	
	@Override
	public String esportaJson() {
		try{
			return this._esportaJson(null, true);
		}catch(Exception e){
			// in questo caso l'eccezione non viene mai lanciata dal metodo (useFaceContext==true)
			// Il codice sottostante e' solo per sicurezza
			DynamicPdDBean.log.error(e.getMessage(), e);
			MessageUtils.addErrorMsg("Si e' verificato un errore inatteso:"	+ e.getMessage());
			return null;
		}
	}
	@Override
	public void esportaJson(HttpServletResponse response) throws Exception {
		this._esportaJson(response, false);
	}
	private String _esportaJson(HttpServletResponse responseParam, boolean useFaceContext) throws Exception {
		log.debug("Export in formato JSON in corso...."); 
		String fileExt = CostantiGrafici.JSON_EXTENSION;
		String filename = this.getExportFilename()+fileExt;

		try {	

			HttpServletResponse response = null;
			FacesContext context = null;
			if(useFaceContext){
				// We must get first our context
				context = FacesContext.getCurrentInstance();

				// Then we have to get the Response where to write our file
				response = (HttpServletResponse) context
						.getExternalContext().getResponse();
			}
			else{
				response = responseParam;
			}

			response.reset();

			// Setto Proprietà Export File
			HttpUtilities.setOutputFile(response, true, filename);

			response.setStatus(200);

			response.getOutputStream().write(this.getJson().getBytes());
			response.getOutputStream().flush();
			
			if(useFaceContext){
				context.responseComplete();
			}

			// End of the method
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			if(useFaceContext){
				FacesContext.getCurrentInstance().responseComplete();
				MessageUtils.addErrorMsg(CostantiGrafici.JSON_EXPORT_MESSAGGIO_ERRORE);
			}
			else{
				throw e;
			}
		}

		return null;
	}
	
	@Override
	public String getExportFilename() {
		if(((StatsSearchForm)this.search).isAndamentoTemporalePerEsiti())
			return CostantiGrafici.DISTRIBUZIONE_ESITI_FILE_NAME;
		else 
			return CostantiGrafici.DISTRIBUZIONE_TEMPORALE_FILE_NAME;
	}
	
	public boolean isVisualizzaComandiSelezioneNumeroLabel() {
		return this.visualizzaComandiSelezioneNumeroLabel;
	}

	public void setVisualizzaComandiSelezioneNumeroLabel(boolean visualizzaComandiSelezioneNumeroLabel) {
		this.visualizzaComandiSelezioneNumeroLabel = visualizzaComandiSelezioneNumeroLabel;
	}
}
