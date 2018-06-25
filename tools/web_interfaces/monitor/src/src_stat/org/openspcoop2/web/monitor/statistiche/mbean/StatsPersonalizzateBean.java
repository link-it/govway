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
package org.openspcoop2.web.monitor.statistiche.mbean;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.faces.application.Application;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.utils.transport.http.HttpUtilities;

import org.openspcoop2.monitor.engine.config.statistiche.ConfigurazioneStatistica;
import org.openspcoop2.core.statistiche.constants.TipoBanda;
import org.openspcoop2.core.statistiche.constants.TipoLatenza;
import org.openspcoop2.core.statistiche.constants.TipoReport;
import org.openspcoop2.core.statistiche.constants.TipoVisualizzazione;
import org.openspcoop2.monitor.sdk.constants.StatisticType;
import org.openspcoop2.web.monitor.core.bean.BaseSearchForm;
import org.openspcoop2.web.monitor.core.datamodel.Res;
import org.openspcoop2.web.monitor.core.datamodel.ResDistribuzione;
import org.openspcoop2.web.monitor.core.mbean.DynamicPdDBean;
import org.openspcoop2.web.monitor.core.utils.MessageUtils;
import org.openspcoop2.web.monitor.statistiche.bean.StatistichePersonalizzateSearchForm;
import org.openspcoop2.web.monitor.statistiche.bean.StatsSearchForm;
import org.openspcoop2.web.monitor.statistiche.constants.CostantiGrafici;
import org.openspcoop2.web.monitor.statistiche.dao.IStatisticaPersonalizzataService;
import org.openspcoop2.web.monitor.statistiche.dao.IStatisticheGiornaliere;
import org.openspcoop2.web.monitor.statistiche.utils.ExportUtils;
import org.openspcoop2.web.monitor.statistiche.utils.JsonStatsUtils;
import org.openspcoop2.web.monitor.statistiche.utils.StatsUtils;
import net.sf.dynamicreports.jasper.builder.JasperReportBuilder;
import net.sf.json.JSONObject;

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
	private IStatisticheGiornaliere statisticheGiornaliereService;

	public StatsPersonalizzateBean() {
		super();
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

		if(this.search instanceof StatistichePersonalizzateSearchForm){
			((StatistichePersonalizzateSearchForm) this.search).setService((IStatisticaPersonalizzataService)this.service);
			((StatistichePersonalizzateSearchForm) this.search).setmBean(this);
		}
	}

	@Override
	public StatisticType getTempo() {
		return ((StatistichePersonalizzateSearchForm) this.search).getModalitaTemporale() == null ? StatisticType.GIORNALIERA
				: ((StatistichePersonalizzateSearchForm) this.search).getModalitaTemporale();
	}

	public String getXml() {
		StringBuffer sb = new StringBuffer();
		try {
			SimpleDateFormat sdf;
			SimpleDateFormat sdf_last_hour = new SimpleDateFormat(CostantiGrafici.PATTERN_HH,
					Locale.ITALIAN);
			StatisticType tempo = this.getTempo();

			if (StatisticType.MENSILE.equals(tempo)) {
				sdf = new SimpleDateFormat(CostantiGrafici.PATTERN_MM_YY, Locale.ITALIAN);
			} else if (StatisticType.ORARIA.equals(tempo)) {
				sdf = new SimpleDateFormat(CostantiGrafici.PATTERN_DD_MM_YY_HH, Locale.ITALIAN);
			} else {
				sdf = new SimpleDateFormat(CostantiGrafici.PATTERN_DD_MM_YY, Locale.ITALIAN);
			}


			// ANDAMENTO TEMPORALE 	<f:selectItem itemLabel="Andamento Temporale" itemValue="andamentoTemporale"/>
			TipoReport tipoReport = ((StatistichePersonalizzateSearchForm) this.search).getTipoReport();
			if (tipoReport.equals(TipoReport.ANDAMENTO_TEMPORALE)) {

				Map<String, List<Res>> results = null;

				try {
					results = this.statisticheGiornaliereService.findAllAndamentoTemporalePersonalizzato();
				} catch (ServiceException e) {
					MessageUtils.addErrorMsg("Si e' verificato un errore durante il recupero dei dati:"	+ e.getMessage());
					DynamicPdDBean.log.error(e.getMessage(), e);
					return null;
				}

				sb.append(StatsUtils.getXmlAndamentoTemporaleStatPersonalizzate( sdf, sdf_last_hour, tempo, results,(StatsSearchForm) this.search, this.getCaption(), this.getSubCaption()));

				if(results != null && results.size() > 0)
					this.setVisualizzaComandiExport(true);

			} else {
				
				// GRAFICO A TORTA o BARRE
				List<ResDistribuzione> list = null;
				try {
					list =  this.statisticheGiornaliereService.findAllDistribuzionePersonalizzata();
				} catch (ServiceException e) {
					MessageUtils.addErrorMsg("Si e' verificato un errore durante il recupero dei dati:"	+ e.getMessage());
					DynamicPdDBean.log.error(e.getMessage(), e);
					return null;
				}
				
				if(tipoReport.equals(TipoReport.PIE_CHART))
					sb.append(StatsUtils.getXmlPieChartStatistichePersonalizzate(list,((StatsSearchForm) this.search), this.getSlice(), this.getCaption(), this.getSubCaption()));
				else if (tipoReport.equals(TipoReport.BAR_CHART))
					sb.append(StatsUtils.getXmlBarChartStatistichePersonalizzate(list,((StatsSearchForm) this.search), this.getSlice(), this.getCaption(), this.getSubCaption()));

				if(list != null && list.size() > 0)
					this.setVisualizzaComandiExport(true);
			}

			return sb.toString();
		} catch (Exception e) {
			DynamicPdDBean.log.error(e.getMessage(), e);
		}
		return null;
	}

	
	public String getJson(){
		JSONObject grafico = null;
		try {
			SimpleDateFormat sdf;
			SimpleDateFormat sdf_last_hour = new SimpleDateFormat(CostantiGrafici.PATTERN_HH,
					Locale.ITALIAN);
			StatisticType tempo = this.getTempo();

			if (StatisticType.MENSILE.equals(tempo)) {
				sdf = new SimpleDateFormat(CostantiGrafici.PATTERN_MM_YY, Locale.ITALIAN);
			} else if (StatisticType.ORARIA.equals(tempo)) {
				sdf = new SimpleDateFormat(CostantiGrafici.PATTERN_DD_MM_YY_HH, Locale.ITALIAN);
			} else {
				sdf = new SimpleDateFormat(CostantiGrafici.PATTERN_DD_MM_YY, Locale.ITALIAN);
			}


			// ANDAMENTO TEMPORALE 	<f:selectItem itemLabel="Andamento Temporale" itemValue="andamentoTemporale"/>
			TipoReport tipoReport = ((StatistichePersonalizzateSearchForm) this.search).getTipoReport();
			if (tipoReport.equals(TipoReport.ANDAMENTO_TEMPORALE)) {

				Map<String, List<Res>> results = null;

				try {
					results = this.statisticheGiornaliereService.findAllAndamentoTemporalePersonalizzato();
				} catch (ServiceException e) {
					MessageUtils.addErrorMsg("Si e' verificato un errore durante il recupero dei dati:"	+ e.getMessage());
					DynamicPdDBean.log.error(e.getMessage(), e);
					return null;
				}

				grafico = JsonStatsUtils.getJsonAndamentoTemporaleStatPersonalizzate( sdf, sdf_last_hour, tempo, results,(StatsSearchForm) this.search, this.getCaption(), this.getSubCaption(), this.getDirezioneLabel());

				if(results != null && results.size() > 0)
					this.setVisualizzaComandiExport(true);

			} else {
				
				// GRAFICO A TORTA o BARRE
				List<ResDistribuzione> list = null;
				try {
					list =  this.statisticheGiornaliereService.findAllDistribuzionePersonalizzata();
				} catch (ServiceException e) {
					MessageUtils.addErrorMsg("Si e' verificato un errore durante il recupero dei dati:"	+ e.getMessage());
					DynamicPdDBean.log.error(e.getMessage(), e);
					return null;
				}
				
				if(tipoReport.equals(TipoReport.PIE_CHART))
					grafico = JsonStatsUtils.getJsonPieChartStatistichePersonalizzate(list,((StatsSearchForm) this.search), this.getSlice(), this.getCaption(), this.getSubCaption());
				else if (tipoReport.equals(TipoReport.BAR_CHART))
					grafico = JsonStatsUtils.getJsonBarChartStatistichePersonalizzate(list,((StatsSearchForm) this.search), this.getDirezioneLabel(), this.getSlice(), this.getCaption(), this.getSubCaption());

				if(list != null && list.size() > 0)
					this.setVisualizzaComandiExport(true);
			}
		} catch (Exception e) {
			DynamicPdDBean.log.error(e.getMessage(), e);
		}
		
		String json = grafico != null ?  grafico.toString() : "";
		log.debug(json); 
		return json ;
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
//		res += CostantiGrafici.WHITE_SPACE;
//		if (StatisticType.GIORNALIERA.equals(this.getTempo())) {
//				res += CostantiGrafici.GIORNALIERA_LABEL + CostantiGrafici.WHITE_SPACE;
//		} else if (StatisticType.ORARIA.equals(this.getTempo())) {
//				res += CostantiGrafici.ORARIA_LABEL + CostantiGrafici.WHITE_SPACE;
//		} else if (StatisticType.MENSILE.equals(this.getTempo())) {
//			res += CostantiGrafici.MENSILE_LABEL + CostantiGrafici.WHITE_SPACE;
//		} else if (StatisticType.SETTIMANALE.equals(this.getTempo())) {
//			res += CostantiGrafici.SETTIMANALE_LABEL + CostantiGrafici.WHITE_SPACE;
//		} else {
//				res += CostantiGrafici.GIORNALIERA_LABEL + CostantiGrafici.WHITE_SPACE;
//		}

		return StringEscapeUtils.escapeXml(res);
	}

	public String getSubCaption() {
		String captionText = StatsUtils.getSubCaption((StatsSearchForm)this.search);
		StringBuffer caption = new StringBuffer(
				captionText);
//		StatisticType tempo = this.getTempo();
//
//		SimpleDateFormat sdf;
//		if (StatisticType.MENSILE.equals(tempo)) {
//			sdf = new SimpleDateFormat("MM/yy", Locale.ITALIAN);
//		} else if (StatisticType.ORARIA.equals(tempo)) {
//			sdf = new SimpleDateFormat("dd/MM/yy HH", Locale.ITALIAN);
//		} else {
//			sdf = new SimpleDateFormat("dd/MM/yy", Locale.ITALIAN);
//		}
//		if(this.search.getDataInizio() != null && this.search.getDataFine() != null)
//			caption.append(" (dal " + sdf.format(this.search.getDataInizio())	+ " al " + sdf.format(this.search.getDataFine()) + " )");

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

		FacesContext facesContext = FacesContext.getCurrentInstance();
		Application app = facesContext.getApplication();
		ExpressionFactory elFactory = app.getExpressionFactory();
		ELContext elContext = facesContext.getELContext();
		ValueExpression valueExp = elFactory.createValueExpression(elContext,
				"#{statistichePersonalizzateBean}",
				StatsPersonalizzateBean.class);
		StatsPersonalizzateBean ab = new StatsPersonalizzateBean();
		// ab.setSoggettiDao(this.soggettiDao);
		// ab.setAzioniDao(this.azioniDao);
		// ab.setConfigDao(this.configDao);
		// ab.setServiziDao(this.serviziDao);
		// ab.setGiornalieroDao(this.giornalieroDao);
		// ab.setOrarioDao(this.orarioDao);

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
		// if(Utility.getSoggettoInGestione()==null){
		// MessageUtils.addErrorMsg("E' necessario selezionare il Soggetto.");
		// return null;
		// }
		return "statsPersonalizzate";
	}

	public String getSommaColumnHeader(){
		return StatsUtils.sommaColumnHeader((StatsSearchForm)this.search, "Messaggi");
	}

	public List<SelectItem> getValoriRisorse() {

		List<SelectItem> res = new ArrayList<SelectItem>();

		try{
			List<String> valRes = this.getValoriRisorseAsString();

			if(valRes != null && valRes.size() > 0)
				for (String valore : valRes) {
					res.add(new SelectItem(valore, valore));
				}

			//			res.add(0, new SelectItem("*"));  
		}catch (Exception e){
			log.error("Si e' verificato un errore durante la lettura dei valori risorse: " + e.getMessage(),e);
		}

		return res;
	}

	public List<String> getValoriRisorseAsString() throws ServiceException { 
		if(this.search.getStatisticaSelezionata()!=null){
			List<String> valRes = this.statisticheGiornaliereService.getValoriRisorse();
			return valRes;
		}
		return null;
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
			List<TipoBanda> tipiBanda = new ArrayList<TipoBanda>();
			tipiBanda.add(((StatsSearchForm)this.search).getTipoBanda());
			List<TipoLatenza> tipiLatenza = new ArrayList<TipoLatenza>();
			tipiLatenza.add(((StatsSearchForm)this.search).getTipoLatenza());


			JasperReportBuilder report = null;
			String headerLabel = null;

			TipoReport tipoReport = ((StatistichePersonalizzateSearchForm) this.search).getTipoReport();
			if (tipoReport.equals(TipoReport.ANDAMENTO_TEMPORALE)) {
				headerLabel = CostantiGrafici.DATA_LABEL;
				Map<String, List<Res>> results = null;

				try {
					results = this.statisticheGiornaliereService.findAllAndamentoTemporalePersonalizzato();
					if(results==null || results.size()<=0){
						// passando dalla console, questo caso non succede mai, mentre tramite http get nel servizio di exporter può succedere
						throw new NotFoundException("Dati non trovati");
					}
				} catch (Exception e) {
					DynamicPdDBean.log.error(e.getMessage(), e);
					if(useFaceContext){
						MessageUtils.addErrorMsg("Si e' verificato un errore durante il recupero dei dati:"	+ e.getMessage());
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
					if(list==null || list.size()<=0){
						// passando dalla console, questo caso non succede mai, mentre tramite http get nel servizio di exporter può succedere
						throw new NotFoundException("Dati non trovati");
					}
				} catch (Exception e) {
					DynamicPdDBean.log.error(e.getMessage(), e);
					if(useFaceContext){
						MessageUtils.addErrorMsg("Si e' verificato un errore durante il recupero dei dati:"	+ e.getMessage());
						return null;
					}
					else{
						throw e;
					}
				}
				headerLabel = CostantiGrafici.NOME_LABEL;
				// creazione del report con Dynamic Report
				report = ExportUtils.creaReportDistribuzione(list, titoloReport, log, tipoVisualizzazione, tipiBanda, tipiLatenza,((StatsSearchForm)this.search).getTipoStatistica(), false);
				
				// scrittura del report sullo stream
				ExportUtils.esportaCsv(response.getOutputStream(),report,titoloReport,headerLabel,tipoVisualizzazione,tipiBanda, tipiLatenza,((StatsSearchForm)this.search).getTipoStatistica());
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
			List<TipoBanda> tipiBanda = new ArrayList<TipoBanda>();
			tipiBanda.add(((StatsSearchForm)this.search).getTipoBanda());
			List<TipoLatenza> tipiLatenza = new ArrayList<TipoLatenza>();
			tipiLatenza.add(((StatsSearchForm)this.search).getTipoLatenza());

			JasperReportBuilder report = null;
			String headerLabel = null;

			TipoReport tipoReport = ((StatistichePersonalizzateSearchForm) this.search).getTipoReport();
			if (tipoReport.equals(TipoReport.ANDAMENTO_TEMPORALE)) {
				headerLabel = CostantiGrafici.DATA_LABEL;
				Map<String, List<Res>> results = null;

				try {
					results = this.statisticheGiornaliereService.findAllAndamentoTemporalePersonalizzato();
					if(results==null || results.size()<=0){
						// passando dalla console, questo caso non succede mai, mentre tramite http get nel servizio di exporter può succedere
						throw new NotFoundException("Dati non trovati");
					}
				} catch (Exception e) {
					DynamicPdDBean.log.error(e.getMessage(), e);
					if(useFaceContext){
						MessageUtils.addErrorMsg("Si e' verificato un errore durante il recupero dei dati:"	+ e.getMessage());
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
					if(list==null || list.size()<=0){
						// passando dalla console, questo caso non succede mai, mentre tramite http get nel servizio di exporter può succedere
						throw new NotFoundException("Dati non trovati");
					}
				} catch (Exception e) {
					DynamicPdDBean.log.error(e.getMessage(), e);
					if(useFaceContext){
						MessageUtils.addErrorMsg("Si e' verificato un errore durante il recupero dei dati:"	+ e.getMessage());
						return null;
					}
					else{
						throw e;
					}
				}
				headerLabel = CostantiGrafici.NOME_LABEL;
				// creazione del report con Dynamic Report
				report = ExportUtils.creaReportDistribuzione(list, titoloReport, log, tipoVisualizzazione, tipiBanda, tipiLatenza,((StatsSearchForm)this.search).getTipoStatistica(), false);
				
				// scrittura del report sullo stream
				ExportUtils.esportaXls(response.getOutputStream(),report,titoloReport,headerLabel,tipoVisualizzazione,tipiBanda, tipiLatenza,((StatsSearchForm)this.search).getTipoStatistica());
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
			List<TipoBanda> tipiBanda = new ArrayList<TipoBanda>();
			tipiBanda.add(((StatsSearchForm)this.search).getTipoBanda());
			List<TipoLatenza> tipiLatenza = new ArrayList<TipoLatenza>();
			tipiLatenza.add(((StatsSearchForm)this.search).getTipoLatenza());

			JasperReportBuilder report = null;
			String headerLabel = null;

			TipoReport tipoReport = ((StatistichePersonalizzateSearchForm) this.search).getTipoReport();
			if (tipoReport.equals(TipoReport.ANDAMENTO_TEMPORALE)) {
				headerLabel = CostantiGrafici.DATA_LABEL;
				Map<String, List<Res>> results = null;

				try {
					results = this.statisticheGiornaliereService.findAllAndamentoTemporalePersonalizzato();
					if(results==null || results.size()<=0){
						// passando dalla console, questo caso non succede mai, mentre tramite http get nel servizio di exporter può succedere
						throw new NotFoundException("Dati non trovati");
					}
				} catch (Exception e) {
					DynamicPdDBean.log.error(e.getMessage(), e);
					if(useFaceContext){
						MessageUtils.addErrorMsg("Si e' verificato un errore durante il recupero dei dati:"	+ e.getMessage());
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
					if(list==null || list.size()<=0){
						// passando dalla console, questo caso non succede mai, mentre tramite http get nel servizio di exporter può succedere
						throw new NotFoundException("Dati non trovati");
					}
				} catch (Exception e) {
					DynamicPdDBean.log.error(e.getMessage(), e);
					if(useFaceContext){
						MessageUtils.addErrorMsg("Si e' verificato un errore durante il recupero dei dati:"	+ e.getMessage());
						return null;
					}
					else{
						throw e;
					}
				}
				headerLabel = CostantiGrafici.NOME_LABEL;
				// creazione del report con Dynamic Report
				report = ExportUtils.creaReportDistribuzione(list, titoloReport, log, tipoVisualizzazione, tipiBanda, tipiLatenza,((StatsSearchForm)this.search).getTipoStatistica(), true);
				
				// scrittura del report sullo stream
				ExportUtils.esportaPdf(response.getOutputStream(),report,titoloReport,headerLabel,tipoVisualizzazione,tipiBanda, tipiLatenza,((StatsSearchForm)this.search).getTipoStatistica());
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
				throw e;
			}
		}


		return null;
	}
	
	@Override
	public String getExportFilename() {
		return CostantiGrafici.DISTRIBUZIONE_PERSONALIZZATA_FILE_NAME;
	}
}
