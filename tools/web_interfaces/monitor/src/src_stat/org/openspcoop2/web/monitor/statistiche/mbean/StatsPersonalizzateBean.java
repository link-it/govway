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
package org.openspcoop2.web.monitor.statistiche.mbean;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.application.Application;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringEscapeUtils;
import org.openspcoop2.core.config.driver.db.DriverConfigurazioneDB;
import org.openspcoop2.core.registry.driver.db.DriverRegistroServiziDB;
import org.openspcoop2.core.statistiche.constants.TipoBanda;
import org.openspcoop2.core.statistiche.constants.TipoLatenza;
import org.openspcoop2.core.statistiche.constants.TipoReport;
import org.openspcoop2.core.statistiche.constants.TipoVisualizzazione;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.monitor.engine.config.statistiche.ConfigurazioneStatistica;
import org.openspcoop2.monitor.sdk.constants.StatisticType;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.json.JSONUtils;
import org.openspcoop2.utils.transport.http.HttpUtilities;
import org.openspcoop2.web.monitor.core.bean.ApplicationBean;
import org.openspcoop2.web.monitor.core.bean.BaseSearchForm;
import org.openspcoop2.web.monitor.core.datamodel.Res;
import org.openspcoop2.web.monitor.core.datamodel.ResDistribuzione;
import org.openspcoop2.web.monitor.core.mbean.DynamicPdDBean;
import org.openspcoop2.web.monitor.core.report.ReportDataSource;
import org.openspcoop2.web.monitor.core.utils.MessageUtils;
import org.openspcoop2.web.monitor.statistiche.bean.StatistichePersonalizzateSearchForm;
import org.openspcoop2.web.monitor.statistiche.bean.StatsSearchForm;
import org.openspcoop2.web.monitor.statistiche.constants.CostantiGrafici;
import org.openspcoop2.web.monitor.statistiche.dao.IStatisticaPersonalizzataService;
import org.openspcoop2.web.monitor.statistiche.dao.IStatisticheGiornaliere;
import org.openspcoop2.web.monitor.statistiche.utils.ExportUtils;
import org.openspcoop2.web.monitor.statistiche.utils.JsonStatsUtils;
import org.openspcoop2.web.monitor.statistiche.utils.StatsUtils;

import com.fasterxml.jackson.databind.node.ObjectNode;

import jakarta.el.ELContext;
import jakarta.el.ExpressionFactory;
import jakarta.el.ValueExpression;
import jakarta.servlet.http.HttpServletResponse;

/**
 * StatsPersonalizzateBean
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class StatsPersonalizzateBean extends BaseStatsMBean<ConfigurazioneStatistica, Long, org.openspcoop2.web.monitor.core.dao.IService<ConfigurazioneStatistica, Long>> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L; 
	private transient IStatisticheGiornaliere statisticheGiornaliereService;

	public StatsPersonalizzateBean() {
		super();
		this.initSPB();
	}
	public StatsPersonalizzateBean(org.openspcoop2.core.commons.search.dao.IServiceManager serviceManager, org.openspcoop2.core.plugins.dao.IServiceManager pluginsServiceManager,
			DriverRegistroServiziDB driverRegistroServiziDB, DriverConfigurazioneDB driverConfigurazioneDB) {
		super(serviceManager, pluginsServiceManager,
				driverRegistroServiziDB, driverConfigurazioneDB);
		this.initSPB();
	}
	private void initSPB() {
		this.setChartId("statistichePersonalizzate");
		this.setFilename("../FusionCharts/ScrollCombi2D.swf");
	}
	
	public void setStatisticheGiornaliereService(
			IStatisticheGiornaliere statisticheGiornaliereService) {
		this.statisticheGiornaliereService = statisticheGiornaliereService;
	}

	@Override
	public void setSearch(BaseSearchForm search) {
		this.search = search;

		if(this.search instanceof StatistichePersonalizzateSearchForm statistichepersonalizzatesearchform){
			statistichepersonalizzatesearchform.setService((IStatisticaPersonalizzataService)this.service);
			statistichepersonalizzatesearchform.setmBean(this);
		}
	}

	@Override
	public StatisticType getTempo() {
		return ((StatistichePersonalizzateSearchForm) this.search).getModalitaTemporale() == null ? StatisticType.GIORNALIERA
				: ((StatistichePersonalizzateSearchForm) this.search).getModalitaTemporale();
	}

	public String getXml() {
		StringBuilder sb = new StringBuilder();
		try {
			SimpleDateFormat sdf;
			SimpleDateFormat sdfLastHour = new SimpleDateFormat(CostantiGrafici.PATTERN_HH, ApplicationBean.getInstance().getLocale());
			StatisticType tempo = this.getTempo();

			if (StatisticType.MENSILE.equals(tempo)) {
				sdf = new SimpleDateFormat(CostantiGrafici.PATTERN_MM_YY, ApplicationBean.getInstance().getLocale());
			} else if (StatisticType.ORARIA.equals(tempo)) {
				sdf = new SimpleDateFormat(CostantiGrafici.PATTERN_DD_MM_YY_HH, ApplicationBean.getInstance().getLocale());
			} else {
				sdf = new SimpleDateFormat(CostantiGrafici.PATTERN_DD_MM_YY, ApplicationBean.getInstance().getLocale());
			}


			// ANDAMENTO TEMPORALE 	<f:selectItem itemLabel="Andamento Temporale" itemValue="andamentoTemporale"/>
			TipoReport tipoReport = ((StatistichePersonalizzateSearchForm) this.search).getTipoReport();
			if (tipoReport.equals(TipoReport.ANDAMENTO_TEMPORALE)) {

				Map<String, List<Res>> results = null;

				try {
					results = this.statisticheGiornaliereService.findAllAndamentoTemporalePersonalizzato();
				} catch (ServiceException e) {
					this.addErroreDuranteRecuperoDati(e);
					DynamicPdDBean.log.error(e.getMessage(), e);
					return null;
				}

				sb.append(StatsUtils.getXmlAndamentoTemporaleStatPersonalizzate( sdf, sdfLastHour, tempo, results,(StatsSearchForm) this.search, this.getCaption(), this.getSubCaption()));

				if(results != null && results.size() > 0)
					this.setVisualizzaComandiExport(true);

			} else {
				
				// GRAFICO A TORTA o BARRE
				List<ResDistribuzione> list = null;
				try {
					list =  this.statisticheGiornaliereService.findAllDistribuzionePersonalizzata();
				} catch (ServiceException e) {
					this.addErroreDuranteRecuperoDati(e);
					DynamicPdDBean.log.error(e.getMessage(), e);
					return null;
				}
				
				if(tipoReport.equals(TipoReport.PIE_CHART))
					sb.append(StatsUtils.getXmlPieChartStatistichePersonalizzate(list,((StatsSearchForm) this.search), this.getSlice(), this.getCaption(), this.getSubCaption()));
				else if (tipoReport.equals(TipoReport.BAR_CHART))
					sb.append(StatsUtils.getXmlBarChartStatistichePersonalizzate(list,((StatsSearchForm) this.search), this.getSlice(), this.getCaption(), this.getSubCaption()));

				if(list != null && !list.isEmpty())
					this.setVisualizzaComandiExport(true);
			}

			return sb.toString();
		} catch (Exception e) {
			DynamicPdDBean.log.error(e.getMessage(), e);
		}
		return null;
	}

	
	public String getJson(){
		ObjectNode grafico = null;
		try {
			SimpleDateFormat sdf;
			SimpleDateFormat sdfLastHour = new SimpleDateFormat(CostantiGrafici.PATTERN_HH, ApplicationBean.getInstance().getLocale());
			StatisticType tempo = this.getTempo();

			if (StatisticType.MENSILE.equals(tempo)) {
				sdf = new SimpleDateFormat(CostantiGrafici.PATTERN_MM_YY, ApplicationBean.getInstance().getLocale());
			} else if (StatisticType.ORARIA.equals(tempo)) {
				sdf = new SimpleDateFormat(CostantiGrafici.PATTERN_DD_MM_YY_HH, ApplicationBean.getInstance().getLocale());
			} else {
				sdf = new SimpleDateFormat(CostantiGrafici.PATTERN_DD_MM_YY, ApplicationBean.getInstance().getLocale());
			}


			// ANDAMENTO TEMPORALE 	<f:selectItem itemLabel="Andamento Temporale" itemValue="andamentoTemporale"/>
			TipoReport tipoReport = ((StatistichePersonalizzateSearchForm) this.search).getTipoReport();
			if (tipoReport.equals(TipoReport.ANDAMENTO_TEMPORALE)) {

				Map<String, List<Res>> results = null;

				try {
					results = this.statisticheGiornaliereService.findAllAndamentoTemporalePersonalizzato();
				} catch (ServiceException e) {
					this.addErroreDuranteRecuperoDati(e);
					DynamicPdDBean.log.error(e.getMessage(), e);
					return null;
				}

				grafico = JsonStatsUtils.getJsonAndamentoTemporaleStatPersonalizzate( sdf, sdfLastHour, tempo, results,(StatsSearchForm) this.search, this.getCaption(), this.getSubCaption(), this.getDirezioneLabel());

				if(results != null && results.size() > 0)
					this.setVisualizzaComandiExport(true);

			} else {
				
				// GRAFICO A TORTA o BARRE
				List<ResDistribuzione> list = null;
				try {
					list =  this.statisticheGiornaliereService.findAllDistribuzionePersonalizzata();
				} catch (ServiceException e) {
					this.addErroreDuranteRecuperoDati(e);
					DynamicPdDBean.log.error(e.getMessage(), e);
					return null;
				}
				
				if(tipoReport.equals(TipoReport.PIE_CHART))
					grafico = JsonStatsUtils.getJsonPieChartStatistichePersonalizzate(list,((StatsSearchForm) this.search), this.getSlice(), this.getCaption(), this.getSubCaption());
				else if (tipoReport.equals(TipoReport.BAR_CHART))
					grafico = JsonStatsUtils.getJsonBarChartStatistichePersonalizzate(list,((StatsSearchForm) this.search), this.getDirezioneLabel(), this.getSlice(), this.getCaption(), this.getSubCaption());

				if(list != null && !list.isEmpty())
					this.setVisualizzaComandiExport(true);
			}
		} catch (Exception e) {
			DynamicPdDBean.log.error(e.getMessage(), e);
		}
		
		try {
			JSONUtils jsonUtils = JSONUtils.getInstance();
			String json = grafico != null ?  jsonUtils.toString(grafico) : "";
			log.debug(json); 
			return json ;
		} catch (UtilsException e) {
			MessageUtils.addErrorMsg("Si e' verificato un errore durante la serializzazione json:"	+ e.getMessage());
			DynamicPdDBean.log.error(e.getMessage(), e);
			return null;
		}
	}
	
	@Override
	public String getData(){
		if(((StatsSearchForm)this.search).isUseGraficiSVG())
			return this.getJson();
		
		return this.getXml();
	}
	

	public String getCaption() {
		String res = "";
		if (this.search != null && this.search.getStatisticaSelezionata() != null) {
			res = this.search.getStatisticaSelezionata().getLabel();
		}
		/**res += CostantiGrafici.WHITE_SPACE;
		if (StatisticType.GIORNALIERA.equals(this.getTempo())) {
				res += CostantiGrafici.GIORNALIERA_LABEL + CostantiGrafici.WHITE_SPACE;
		} else if (StatisticType.ORARIA.equals(this.getTempo())) {
				res += CostantiGrafici.ORARIA_LABEL + CostantiGrafici.WHITE_SPACE;
		} else if (StatisticType.MENSILE.equals(this.getTempo())) {
			res += CostantiGrafici.MENSILE_LABEL + CostantiGrafici.WHITE_SPACE;
		} else if (StatisticType.SETTIMANALE.equals(this.getTempo())) {
			res += CostantiGrafici.SETTIMANALE_LABEL + CostantiGrafici.WHITE_SPACE;
		} else {
				res += CostantiGrafici.GIORNALIERA_LABEL + CostantiGrafici.WHITE_SPACE;
		}*/

		return StringEscapeUtils.escapeXml(res);
	}

	public String getSubCaption() {
		String captionText = StatsUtils.getSubCaption((StatsSearchForm)this.search);
		StringBuilder caption = new StringBuilder(
				captionText);
		/**StatisticType tempo = this.getTempo();

		SimpleDateFormat sdf;
		if (StatisticType.MENSILE.equals(tempo)) {
			sdf = new SimpleDateFormat("MM/yy", ApplicationBean.getInstance().getLocale());
		} else if (StatisticType.ORARIA.equals(tempo)) {
			sdf = new SimpleDateFormat("dd/MM/yy HH", ApplicationBean.getInstance().getLocale());
		} else {
			sdf = new SimpleDateFormat("dd/MM/yy", ApplicationBean.getInstance().getLocale());
		}
		if(this.search.getDataInizio() != null && this.search.getDataFine() != null)
			caption.append(" (dal " + sdf.format(this.search.getDataInizio())	+ " al " + sdf.format(this.search.getDataFine()) + " )");*/

		if(this.search.getDataInizio() != null && this.search.getDataFine() != null){
			if ( this.btnLblPrefix(this.search).toLowerCase().contains(CostantiGrafici.ORA_KEY)) {
				caption.append(MessageFormat.format(CostantiGrafici.DAL_AL_PATTERN, this.formatDate(this.search.getDataInizio(),true), this.formatDate(this.search.getDataFine(),true)));
			} else {
				caption.append(MessageFormat.format(CostantiGrafici.DAL_AL_PATTERN, this.formatDate(this.search.getDataInizio(),false), this.formatDate(this.search.getDataFine(),false)));
			}
		}
		
		
		return StringEscapeUtils.escapeXml(caption.toString());
	}

	public void newSearch(ActionEvent ae) {

		if(ae!=null) {
			// nop
		}
		
		FacesContext facesContext = FacesContext.getCurrentInstance();
		Application app = facesContext.getApplication();
		ExpressionFactory elFactory = app.getExpressionFactory();
		ELContext elContext = facesContext.getELContext();
		ValueExpression valueExp = elFactory.createValueExpression(elContext,
				"#{statistichePersonalizzateBean}",
				StatsPersonalizzateBean.class);
		StatsPersonalizzateBean ab = new StatsPersonalizzateBean();
		 /**ab.setSoggettiDao(this.soggettiDao);
		 ab.setAzioniDao(this.azioniDao);
		 ab.setConfigDao(this.configDao);
		 ab.setServiziDao(this.serviziDao);
		 ab.setGiornalieroDao(this.giornalieroDao);
		 ab.setOrarioDao(this.orarioDao);*/

		valueExp.setValue(elContext, ab);

		((StatistichePersonalizzateSearchForm) this.search).initSearchForm();
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
		 /**if(Utility.getSoggettoInGestione()==null){
		 MessageUtils.addErrorMsg("E' necessario selezionare il Soggetto.");
		 return null;
		 }*/
		return "statsPersonalizzate";
	}

	public String getSommaColumnHeader(){
		return StatsUtils.sommaColumnHeader((StatsSearchForm)this.search, "Messaggi");
	}

	public List<SelectItem> getValoriRisorse() {

		List<SelectItem> res = new ArrayList<>();

		try{
			List<String> valRes = this.getValoriRisorseAsString();

			if(valRes != null && !valRes.isEmpty())
				for (String valore : valRes) {
					res.add(new SelectItem(valore, valore));
				}

		}catch (Exception e){
			log.error("Si e' verificato un errore durante la lettura dei valori risorse: " + e.getMessage(),e);
		}

		return res;
	}

	public List<String> getValoriRisorseAsString() throws ServiceException { 
		if(this.search.getStatisticaSelezionata()!=null){
			return this.statisticheGiornaliereService.getValoriRisorse();
		}
		return null;
	}

	@Override
	public String esportaCsv() {
		try{
			return this.esportaCsvEngine(null, true);
		}catch(Exception e){
			// in questo caso l'eccezione non viene mai lanciata dal metodo (useFaceContext==true)
			// Il codice sottostante e' solo per sicurezza
			DynamicPdDBean.log.error(e.getMessage(), e);
			this.addErroroInatteso(e);
			return null;
		}
	}
	@Override
	public void esportaCsv(HttpServletResponse response) throws Exception {
		this.esportaCsvEngine(response, false);
	}
	private String esportaCsvEngine(HttpServletResponse responseParam, boolean useFaceContext) throws ServiceException {
		log.debug("Export in formato CSV in corso...."); 
		String fileExt = CostantiGrafici.CSV_EXTENSION;
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

			String titoloReport = this.getCaption() + " " + this.getSubCaption();

			TipoVisualizzazione tipoVisualizzazione = ((StatsSearchForm)this.search).getTipoVisualizzazione();
			List<TipoBanda> tipiBanda = new ArrayList<>();
			tipiBanda.add(((StatsSearchForm)this.search).getTipoBanda());
			List<TipoLatenza> tipiLatenza = new ArrayList<>();
			tipiLatenza.add(((StatsSearchForm)this.search).getTipoLatenza());


			ReportDataSource report = null;
			String headerLabel = null;

			TipoReport tipoReport = ((StatistichePersonalizzateSearchForm) this.search).getTipoReport();
			if (tipoReport.equals(TipoReport.ANDAMENTO_TEMPORALE)) {
				headerLabel = CostantiGrafici.DATA_LABEL;
				Map<String, List<Res>> results = null;

				try {
					results = this.statisticheGiornaliereService.findAllAndamentoTemporalePersonalizzato();
					if(results==null || results.size()<=0){
						// passando dalla console, questo caso non succede mai, mentre tramite http get nel servizio di exporter può succedere
						throw this.newDatiNonTrovatiException();
					}
				} catch (Exception e) {
					DynamicPdDBean.log.error(e.getMessage(), e);
					if(useFaceContext){
						this.addErroreDuranteRecuperoDati(e);
						return null;
					}
					else{
						throw e;
					}
				}

				StatisticType modalitaTemporale = ((StatsSearchForm)this.search).getModalitaTemporale();
				report = ExportUtils.creaReportAndamentoTemporalePersonalizzato(results, titoloReport, log, tipoVisualizzazione, tipiBanda, tipiLatenza, modalitaTemporale, false);
				
				ExportUtils.esportaCsvAndamentoTemporalePersonalizzato(response.getOutputStream(), report, results, titoloReport, headerLabel, tipoVisualizzazione, tipiBanda, tipiLatenza); 

			} else {

				List<ResDistribuzione> list = null;
				try {
					list = this.statisticheGiornaliereService.findAllDistribuzionePersonalizzata();
					if(list==null || list.isEmpty()){
						// passando dalla console, questo caso non succede mai, mentre tramite http get nel servizio di exporter può succedere
						throw this.newDatiNonTrovatiException();
					}
				} catch (Exception e) {
					DynamicPdDBean.log.error(e.getMessage(), e);
					if(useFaceContext){
						this.addErroreDuranteRecuperoDati(e);
						return null;
					}
					else{
						throw e;
					}
				}
				headerLabel = CostantiGrafici.NOME_LABEL;
				// creazione del report con Dynamic Report
				report = ExportUtils.creaReportDistribuzione(list, titoloReport, log, tipoVisualizzazione, ((StatsSearchForm)this.search).getNumeroDimensioni(),  
						tipiBanda, tipiLatenza,((StatsSearchForm)this.search).getTipoStatistica(), false);
				
				// scrittura del report sullo stream
				ExportUtils.esportaCsv(response.getOutputStream(),report,titoloReport,headerLabel,tipoVisualizzazione, ((StatsSearchForm)this.search).getNumeroDimensioni(), 
						tipiBanda, tipiLatenza,((StatsSearchForm)this.search).getTipoStatistica());
			}
			
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
				throw new ServiceException(e.getMessage(),e);
			}
		}


		return null;
	}

	@Override
	public String esportaXls() {
		try{
			return this.esportaXlsEngine(null, true);
		}catch(Exception e){
			// in questo caso l'eccezione non viene mai lanciata dal metodo (useFaceContext==true)
			// Il codice sottostante e' solo per sicurezza
			DynamicPdDBean.log.error(e.getMessage(), e);
			this.addErroroInatteso(e);
			return null;
		}
	}
	@Override
	public void esportaXls(HttpServletResponse response) throws Exception {
		this.esportaXlsEngine(response, false);
	}
	private String esportaXlsEngine(HttpServletResponse responseParam, boolean useFaceContext) throws ServiceException {
		log.debug("Export in formato XLS in corso...."); 
		String fileExt = CostantiGrafici.XLS_EXTENSION;
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

			String titoloReport = this.getCaption() + " " + this.getSubCaption();
			TipoVisualizzazione tipoVisualizzazione = ((StatsSearchForm)this.search).getTipoVisualizzazione();
			List<TipoBanda> tipiBanda = new ArrayList<>();
			tipiBanda.add(((StatsSearchForm)this.search).getTipoBanda());
			List<TipoLatenza> tipiLatenza = new ArrayList<>();
			tipiLatenza.add(((StatsSearchForm)this.search).getTipoLatenza());

			ReportDataSource report = null;
			String headerLabel = null;

			TipoReport tipoReport = ((StatistichePersonalizzateSearchForm) this.search).getTipoReport();
			if (tipoReport.equals(TipoReport.ANDAMENTO_TEMPORALE)) {
				headerLabel = CostantiGrafici.DATA_LABEL;
				Map<String, List<Res>> results = null;

				try {
					results = this.statisticheGiornaliereService.findAllAndamentoTemporalePersonalizzato();
					if(results==null || results.size()<=0){
						// passando dalla console, questo caso non succede mai, mentre tramite http get nel servizio di exporter può succedere
						throw this.newDatiNonTrovatiException();
					}
				} catch (Exception e) {
					DynamicPdDBean.log.error(e.getMessage(), e);
					if(useFaceContext){
						this.addErroreDuranteRecuperoDati(e);
						return null;
					}
					else{
						throw e;
					}
				}

				StatisticType modalitaTemporale = ((StatsSearchForm)this.search).getModalitaTemporale();
				report = ExportUtils.creaReportAndamentoTemporalePersonalizzato(results, titoloReport, log, tipoVisualizzazione, tipiBanda, tipiLatenza, modalitaTemporale, false);
				
				ExportUtils.esportaXlsAndamentoTemporalePersonalizzato(response.getOutputStream(), report, results, titoloReport, headerLabel, tipoVisualizzazione, tipiBanda, tipiLatenza); 

			} else {

				List<ResDistribuzione> list = null;
				try {
					list = this.statisticheGiornaliereService.findAllDistribuzionePersonalizzata();
					if(list==null || list.isEmpty()){
						// passando dalla console, questo caso non succede mai, mentre tramite http get nel servizio di exporter può succedere
						throw this.newDatiNonTrovatiException();
					}
				} catch (Exception e) {
					DynamicPdDBean.log.error(e.getMessage(), e);
					if(useFaceContext){
						this.addErroreDuranteRecuperoDati(e);
						return null;
					}
					else{
						throw e;
					}
				}
				headerLabel = CostantiGrafici.NOME_LABEL;
				// creazione del report con Dynamic Report
				report = ExportUtils.creaReportDistribuzione(list, titoloReport, log, tipoVisualizzazione, ((StatsSearchForm)this.search).getNumeroDimensioni(),  
						tipiBanda, tipiLatenza,((StatsSearchForm)this.search).getTipoStatistica(), false);
				
				// scrittura del report sullo stream
				ExportUtils.esportaXls(response.getOutputStream(),report,titoloReport,headerLabel,tipoVisualizzazione, ((StatsSearchForm)this.search).getNumeroDimensioni(), 
						tipiBanda, tipiLatenza,((StatsSearchForm)this.search).getTipoStatistica());
			}

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
				throw new ServiceException(e.getMessage(),e);
			}
		}

		return null;
	}

	@Override
	public String esportaPdf() {
		try{
			return this.esportaPdfEngine(null, true);
		}catch(Exception e){
			// in questo caso l'eccezione non viene mai lanciata dal metodo (useFaceContext==true)
			// Il codice sottostante e' solo per sicurezza
			DynamicPdDBean.log.error(e.getMessage(), e);
			this.addErroroInatteso(e);
			return null;
		}
	}
	@Override
	public void esportaPdf(HttpServletResponse response) throws Exception {
		this.esportaPdfEngine(response, false);
	}
	private String esportaPdfEngine(HttpServletResponse responseParam, boolean useFaceContext) throws ServiceException {
		log.debug("Export in formato PDF in corso...."); 
		String fileExt = CostantiGrafici.PDF_EXTENSION;
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

			String titoloReport = this.getCaption() + " " + this.getSubCaption();


			TipoVisualizzazione tipoVisualizzazione = ((StatsSearchForm)this.search).getTipoVisualizzazione();
			List<TipoBanda> tipiBanda = new ArrayList<>();
			tipiBanda.add(((StatsSearchForm)this.search).getTipoBanda());
			List<TipoLatenza> tipiLatenza = new ArrayList<>();
			tipiLatenza.add(((StatsSearchForm)this.search).getTipoLatenza());

			ReportDataSource report = null;
			String headerLabel = null;

			TipoReport tipoReport = ((StatistichePersonalizzateSearchForm) this.search).getTipoReport();
			if (tipoReport.equals(TipoReport.ANDAMENTO_TEMPORALE)) {
				headerLabel = CostantiGrafici.DATA_LABEL;
				Map<String, List<Res>> results = null;

				try {
					results = this.statisticheGiornaliereService.findAllAndamentoTemporalePersonalizzato();
					if(results==null || results.size()<=0){
						// passando dalla console, questo caso non succede mai, mentre tramite http get nel servizio di exporter può succedere
						throw this.newDatiNonTrovatiException();
					}
				} catch (Exception e) {
					DynamicPdDBean.log.error(e.getMessage(), e);
					if(useFaceContext){
						this.addErroreDuranteRecuperoDati(e);
						return null;
					}
					else{
						throw e;
					}
				}

				StatisticType modalitaTemporale = ((StatsSearchForm)this.search).getModalitaTemporale();
				report = ExportUtils.creaReportAndamentoTemporalePersonalizzato(results, titoloReport, log, tipoVisualizzazione, tipiBanda, tipiLatenza, modalitaTemporale, true);
				
				ExportUtils.esportaPdfAndamentoTemporalePersonalizzato(response.getOutputStream(), report, results, titoloReport, headerLabel, tipoVisualizzazione, tipiBanda, tipiLatenza); 

			} else {

				List<ResDistribuzione> list = null;
				try {
					list = this.statisticheGiornaliereService.findAllDistribuzionePersonalizzata();
					if(list==null || list.isEmpty()){
						// passando dalla console, questo caso non succede mai, mentre tramite http get nel servizio di exporter può succedere
						throw this.newDatiNonTrovatiException();
					}
				} catch (Exception e) {
					DynamicPdDBean.log.error(e.getMessage(), e);
					if(useFaceContext){
						this.addErroreDuranteRecuperoDati(e);
						return null;
					}
					else{
						throw e;
					}
				}
				headerLabel = CostantiGrafici.NOME_LABEL;
				// creazione del report con Dynamic Report
				report = ExportUtils.creaReportDistribuzione(list, titoloReport, log, tipoVisualizzazione, ((StatsSearchForm)this.search).getNumeroDimensioni(),  
						tipiBanda, tipiLatenza,((StatsSearchForm)this.search).getTipoStatistica(), true);
				
				// scrittura del report sullo stream
				ExportUtils.esportaPdf(response.getOutputStream(),report,titoloReport,headerLabel,tipoVisualizzazione, ((StatsSearchForm)this.search).getNumeroDimensioni(), 
						tipiBanda, tipiLatenza,((StatsSearchForm)this.search).getTipoStatistica());
			}

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
				throw new ServiceException(e.getMessage(),e);
			}
		}


		return null;
	}
	
	@Override
	public String esportaXml() {
		try{
			return this.esportaXmlEngine(null, true);
		}catch(Exception e){
			// in questo caso l'eccezione non viene mai lanciata dal metodo (useFaceContext==true)
			// Il codice sottostante e' solo per sicurezza
			DynamicPdDBean.log.error(e.getMessage(), e);
			this.addErroroInatteso(e);
			return null;
		}
	}
	@Override
	public void esportaXml(HttpServletResponse response) throws Exception {
		this.esportaXmlEngine(response, false);
	}
	private String esportaXmlEngine(HttpServletResponse responseParam, boolean useFaceContext) throws ServiceException {
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
				throw new ServiceException(e.getMessage(),e);
			}
		}

		return null;
	}
	
	@Override
	public String esportaJson() {
		try{
			return this.esportaJsonEngine(null, true);
		}catch(Exception e){
			// in questo caso l'eccezione non viene mai lanciata dal metodo (useFaceContext==true)
			// Il codice sottostante e' solo per sicurezza
			DynamicPdDBean.log.error(e.getMessage(), e);
			this.addErroroInatteso(e);
			return null;
		}
	}
	@Override
	public void esportaJson(HttpServletResponse response) throws Exception {
		this.esportaJsonEngine(response, false);
	}
	private String esportaJsonEngine(HttpServletResponse responseParam, boolean useFaceContext) throws ServiceException {
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
				throw new ServiceException(e.getMessage(),e);
			}
		}

		return null;
	}
	
	@Override
	public String getExportFilename() {
		return CostantiGrafici.DISTRIBUZIONE_PERSONALIZZATA_FILE_NAME;
	}
	
	public boolean isTimeoutEvent(){
		return ((IStatisticaPersonalizzataService)this.service).isTimeoutEvent();
	}
}
