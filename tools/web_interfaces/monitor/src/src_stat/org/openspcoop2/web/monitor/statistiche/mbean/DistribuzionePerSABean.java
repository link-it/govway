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
import java.util.ArrayList;
import java.util.List;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.faces.application.Application;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.config.driver.db.DriverConfigurazioneDB;
import org.openspcoop2.core.registry.driver.db.DriverRegistroServiziDB;
import org.openspcoop2.core.statistiche.constants.TipoBanda;
import org.openspcoop2.core.statistiche.constants.TipoLatenza;
import org.openspcoop2.core.statistiche.constants.TipoReport;
import org.openspcoop2.core.statistiche.constants.TipoStatistica;
import org.openspcoop2.core.statistiche.constants.TipoVisualizzazione;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.monitor.sdk.constants.StatisticType;
import org.openspcoop2.protocol.engine.utils.NamingUtils;
import org.openspcoop2.utils.transport.http.HttpUtilities;
import org.openspcoop2.web.monitor.core.core.Utility;
import org.openspcoop2.web.monitor.core.dao.IService;
import org.openspcoop2.web.monitor.core.datamodel.ResBase;
import org.openspcoop2.web.monitor.core.datamodel.ResDistribuzione;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;
import org.openspcoop2.web.monitor.core.mbean.DynamicPdDBean;
import org.openspcoop2.web.monitor.core.utils.MessageManager;
import org.openspcoop2.web.monitor.core.utils.MessageUtils;
import org.openspcoop2.web.monitor.statistiche.bean.NumeroDimensioni;
import org.openspcoop2.web.monitor.statistiche.bean.StatsSearchForm;
import org.openspcoop2.web.monitor.statistiche.constants.CostantiGrafici;
import org.openspcoop2.web.monitor.statistiche.constants.StatisticheCostanti;
import org.openspcoop2.web.monitor.statistiche.dao.IStatisticheGiornaliere;
import org.openspcoop2.web.monitor.statistiche.utils.ExportUtils;
import org.openspcoop2.web.monitor.statistiche.utils.JsonStatsUtils;
import org.openspcoop2.web.monitor.statistiche.utils.StatsUtils;
import org.slf4j.Logger;

import net.sf.dynamicreports.jasper.builder.JasperReportBuilder;
import net.sf.json.JSONObject;

/**
 * DistribuzionePerSABean
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class DistribuzionePerSABean<T extends ResBase> extends BaseStatsMBean<T, Integer, IService<ResBase, Integer>> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L; 

	public DistribuzionePerSABean() {
		super();
		this.initDpSAB();
	}
	public DistribuzionePerSABean(org.openspcoop2.core.commons.search.dao.IServiceManager serviceManager, org.openspcoop2.core.plugins.dao.IServiceManager pluginsServiceManager,
			DriverRegistroServiziDB driverRegistroServiziDB, DriverConfigurazioneDB driverConfigurazioneDB) {
		super(serviceManager, pluginsServiceManager,
				driverRegistroServiziDB, driverConfigurazioneDB);
		this.initDpSAB();
	}
	private void initDpSAB() {
		this.setChartId("distribuzioneSA");
		this.setFilename("../FusionCharts/ScrollCombi2D.swf");
	}

	@SuppressWarnings("unchecked")
	public void setStatisticheGiornaliereService(
			IStatisticheGiornaliere statisticheGiornaliereService) {
		this.service =  (IService<T, Integer>) statisticheGiornaliereService;
	}

	public String getXml() {
		List<ResDistribuzione> list;
		this.setVisualizzaComandiExport(false);
		try {
			list = ((IStatisticheGiornaliere)this.service).findAllDistribuzioneServizioApplicativo();
		} catch (ServiceException e) {
			MessageUtils.addErrorMsg("Si e' verificato un errore durante il recupero dei dati:"	+ e.getMessage());
			DynamicPdDBean.log.error(e.getMessage(), e);
			return null;
		}

		list = calcolaLabels(list, this.search.getProtocollo(),(StatsSearchForm)this.search);
		TipoReport tipoReport = ((StatsSearchForm)this.search).getTipoReport();
		String xml = "";
		switch (tipoReport) {
		case BAR_CHART:
			xml = StatsUtils.getXmlBarChartDistribuzione(list,(StatsSearchForm) this.search, this.getCaption(), this.getSubCaption(), this.getSlice());
			break;
		case PIE_CHART:
			xml = StatsUtils.getXmlPieChartDistribuzione(list,(StatsSearchForm) this.search, this.getCaption(), this.getSubCaption(), this.getSlice());
			break;
		default:
			break;
		}

		if(list != null && list.size() > 0)
			this.setVisualizzaComandiExport(true);

		return xml;
	}

	public String getJson(){
		JSONObject grafico = null;
		List<ResDistribuzione> list;
		this.setVisualizzaComandiExport(false);
		try {
			list = ((IStatisticheGiornaliere)this.service).findAllDistribuzioneServizioApplicativo();
		} catch (ServiceException e) {
			MessageUtils.addErrorMsg("Si e' verificato un errore durante il recupero dei dati:"	+ e.getMessage());
			DynamicPdDBean.log.error(e.getMessage(), e);
			return null;
		}

		list = calcolaLabels(list, this.search.getProtocollo(),(StatsSearchForm)this.search);
		TipoReport tipoReport = ((StatsSearchForm)this.search).getTipoReport();

		switch (tipoReport) {
		case BAR_CHART:{
			NumeroDimensioni numeroDimensioni = ((StatsSearchForm)this.search).getNumeroDimensioni();
			
			switch (numeroDimensioni) {
				case DIMENSIONI_3:
					StatisticType statisticType = StatsUtils.checkStatisticType((StatsSearchForm) this.search, false);
					grafico = JsonStatsUtils.getJsonHeatmapChartDistribuzione(list,(StatsSearchForm) this.search, this.getCaption(), this.getSubCaption(), this.getDirezioneLabel(), this.getSlice(), statisticType, DynamicPdDBean.log);
					break;
				case DIMENSIONI_2:
				default:
					grafico = JsonStatsUtils.getJsonBarChartDistribuzione(list,(StatsSearchForm) this.search, this.getCaption(), this.getSubCaption(), this.getDirezioneLabel(), this.getSlice());
					break;
				}
			}
			break;
		case PIE_CHART:
			grafico = JsonStatsUtils.getJsonPieChartDistribuzione(list, ((StatsSearchForm)this.search), this.getCaption(), this.getSubCaption() , this.getSlice());
			break;
		default:
			break;
		}

		if(list != null && !list.isEmpty())
			this.setVisualizzaComandiExport(true);

		String json = grafico != null ?  grafico.toString() : "";
		log.debug(json); 
		return json ;
	}

	public static List<ResDistribuzione>  calcolaLabels (List<ResDistribuzione> list, String protocollo, StatsSearchForm form){
		if(form.getTipoStatistica().equals(TipoStatistica.DISTRIBUZIONE_SERVIZIO_APPLICATIVO)){
			if(form.getRiconoscimento() != null && form.getRiconoscimento().equals(org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_APPLICATIVO)) {
				if(list!=null  && !list.isEmpty()){
					for (ResDistribuzione res : list) {
						String tipoNomeSoggetto = res.getParentMap().get("0");

						String tipoSoggetto = Utility.parseTipoSoggetto(tipoNomeSoggetto);
						String nomeSoggetto = Utility.parseNomeSoggetto(tipoNomeSoggetto);

						try {
							res.getParentMap().put("0", NamingUtils.getLabelSoggetto(protocollo, tipoSoggetto, nomeSoggetto));
						} catch (Exception e) {				
						}

					}
				}
			}
		}
		return list;
	}

	@Override
	public String getData(){
		if(((StatsSearchForm)this.search).isUseGraficiSVG())
			return this.getJson();

		return this.getXml();
	}

	public String getCaption() {
		StringBuilder sb = new StringBuilder();
		sb.append(MessageManager.getInstance().getMessage(StatisticheCostanti.STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_DISTRIBUZIONE_LABEL_KEY)).append(CostantiGrafici.WHITE_SPACE);
		
		if(((StatsSearchForm)this.search).isShowUnitaTempo()) {
			if (StatisticType.GIORNALIERA.equals(this.getTempo())) {
				sb.append(CostantiGrafici.GIORNALIERA_LABEL).append(CostantiGrafici.WHITE_SPACE);
			} else if (StatisticType.ORARIA.equals(this.getTempo())) {
				sb.append(CostantiGrafici.ORARIA_LABEL).append(CostantiGrafici.WHITE_SPACE);
			} else if (StatisticType.MENSILE.equals(this.getTempo())) {
				sb.append( CostantiGrafici.MENSILE_LABEL).append(CostantiGrafici.WHITE_SPACE);
			} else if (StatisticType.SETTIMANALE.equals(this.getTempo())) {
				sb.append(CostantiGrafici.SETTIMANALE_LABEL).append(CostantiGrafici.WHITE_SPACE);
			} else {
				sb.append(CostantiGrafici.GIORNALIERA_LABEL).append(CostantiGrafici.WHITE_SPACE);
			}
		}
		
		/**sb.append(MessageManager.getInstance().getMessage(StatisticheCostanti.STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_MITTENTE_LABEL_SUFFIX_KEY)).append(CostantiGrafici.WHITE_SPACE);
		sb.append("(").append(getTipoFiltroDatiMittente()).append(")").append(CostantiGrafici.WHITE_SPACE);*/
		
		sb.append(MessageManager.getInstance().getMessage(StatisticheCostanti.STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_MITTENTE_LABEL_SUFFIX_AND_KEY)).append(CostantiGrafici.WHITE_SPACE);
		sb.append(getTipoFiltroDatiMittente()).append(CostantiGrafici.WHITE_SPACE);
		
		return sb.toString();
	}

	public String getTipoFiltroDatiMittente() {
		if(StringUtils.isNotEmpty(this.search.getRiconoscimento())) {
			if(this.search.getRiconoscimento().equals(org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_APPLICATIVO)) {
				return MessageManager.getInstance().getMessage(StatisticheCostanti.STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_MITTENTE_SERVIZIO_APPLICATIVO_LABEL_SUFFIX_KEY);
			} else if(this.search.getRiconoscimento().equals(org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_IDENTIFICATIVO_AUTENTICATO)) {
				return MessageManager.getInstance().getMessage(StatisticheCostanti.STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_MITTENTE_IDENTIFICATIVO_AUTENTICATO_SUFFIX_KEY);
			} else if(this.search.getRiconoscimento().equals(org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_INDIRIZZO_IP)) {
				return MessageManager.getInstance().getMessage(StatisticheCostanti.STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_MITTENTE_INDIRIZZO_IP_SUFFIX_KEY);
			} else { // token
				if (StringUtils.isNotEmpty(this.search.getTokenClaim())) {
					org.openspcoop2.core.transazioni.utils.TipoCredenzialeMittente tcm = null;
					try {
						tcm = org.openspcoop2.core.transazioni.utils.TipoCredenzialeMittente.toEnumConstant(this.search.getTokenClaim(), true);
					}catch(Exception e) {
						Logger log = LoggerManager.getPddMonitorCoreLogger();
						log.error(e.getMessage(),e);
					}

					if(tcm!=null) {
						switch (tcm) {
						case TOKEN_CLIENT_ID:
							return MessageManager.getInstance().getMessage(StatisticheCostanti.STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_TOKEN_INFO_CLIENT_ID_LABEL_KEY);
						case TOKEN_EMAIL:
							return MessageManager.getInstance().getMessage(StatisticheCostanti.STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_TOKEN_INFO_EMAIL_LABEL_KEY);
						case TOKEN_ISSUER:
							return MessageManager.getInstance().getMessage(StatisticheCostanti.STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_TOKEN_INFO_ISSUER_LABEL_KEY);
						case TOKEN_SUBJECT:
							return MessageManager.getInstance().getMessage(StatisticheCostanti.STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_TOKEN_INFO_SUBJECT_LABEL_KEY);
						case TOKEN_USERNAME:
							return MessageManager.getInstance().getMessage(StatisticheCostanti.STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_TOKEN_INFO_USERNAME_LABEL_KEY);
						case PDND_ORGANIZATION_NAME:
							return MessageManager.getInstance().getMessage(StatisticheCostanti.STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_TOKEN_INFO_PDND_ORGANIZATION_NAME_LABEL_KEY);
						case TRASPORTO:
						default:
							// caso impossibile
							break; 
						}
					}
				} 
			}
		}


		return MessageManager.getInstance().getMessage(StatisticheCostanti.STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_MITTENTE_LABEL_SUFFIX_KEY);
	}
	
	public boolean isShowColumnSoggetto() {
		if(StringUtils.isNotEmpty(this.search.getRiconoscimento())) {
			return this.search.getRiconoscimento().equals(org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_APPLICATIVO);
		}
		return false;
	}
	
	public boolean isShowColumnClientIdApplicativoSoggetto() {
		if(StringUtils.isNotEmpty(this.search.getRiconoscimento())) {
			if(this.search.getRiconoscimento().equals(org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_TOKEN_INFO)) {
				if(StringUtils.isNotEmpty(this.search.getTokenClaim())) {
					try {
						org.openspcoop2.core.transazioni.utils.TipoCredenzialeMittente tcm = org.openspcoop2.core.transazioni.utils.TipoCredenzialeMittente.toEnumConstant(this.search.getTokenClaim(), true);
						return org.openspcoop2.core.transazioni.utils.TipoCredenzialeMittente.TOKEN_CLIENT_ID.equals(tcm);
					}catch(Exception t) {
						// ignore
					}
				}
			}
		}
		return false;
	}
	
	public boolean isShowColumnClientIdPDNDInfo() {
		if(StringUtils.isNotEmpty(this.search.getRiconoscimento()) &&
			this.search.getRiconoscimento().equals(org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_TOKEN_INFO)  &&
			StringUtils.isNotEmpty(this.search.getTokenClaim())) {
			try {
				org.openspcoop2.core.transazioni.utils.TipoCredenzialeMittente tcm = org.openspcoop2.core.transazioni.utils.TipoCredenzialeMittente.toEnumConstant(this.search.getTokenClaim(), true);
				return org.openspcoop2.core.transazioni.utils.TipoCredenzialeMittente.PDND_ORGANIZATION_NAME.equals(tcm);
			}catch(Exception t) {
				// ignore
			}
		}
		return false;
	}
	
	public boolean isShowColumnClientIdApplicativo() {
		if(StringUtils.isNotEmpty(this.search.getRiconoscimento())) {
			if(this.search.getRiconoscimento().equals(org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_APPLICATIVO)) {
				if (StringUtils.isNotBlank(this.search.getIdentificazione()) && org.openspcoop2.web.monitor.core.constants.Costanti.IDENTIFICAZIONE_TOKEN_KEY.equals(this.search.getIdentificazione())) {
					return true;
				}
			}
		}
		return false;
	}

	public String getSubCaption() {
		String captionText = StatsUtils.getSubCaption((StatsSearchForm)this.search);
		StringBuilder caption = new StringBuilder(
				captionText);
		//		if (StringUtils.isNotBlank(this.search.getServizioApplicativo())) {
		//			caption.append("per l'Applicativo"
		//					+ this.search.getServizioApplicativo());
		//		}

		if(this.search.getDataInizio() != null && this.search.getDataFine() != null){
			if ( this.btnLblPrefix(this.search).toLowerCase().contains(CostantiGrafici.ORA_KEY)) {
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
		ValueExpression valueExp = elFactory
				.createValueExpression(elContext,
						"#{distribuzionePerSoggettoBean}",
						DistribuzionePerSABean.class);

		DistribuzionePerSABean<ResDistribuzione> ab = new DistribuzionePerSABean<ResDistribuzione>();

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
		return "distribSA";
	}

	public String getSommaColumnHeader(){
		return StatsUtils.sommaColumnHeader((StatsSearchForm)this.search, CostantiGrafici.TRANSAZIONI_LABEL);
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

		List<ResDistribuzione> list = null;
		try {
			list = ((IStatisticheGiornaliere)this.service).findAllDistribuzioneServizioApplicativo();
			if(list==null || list.size()<=0){
				// passando dalla console, questo caso non succede mai, mentre tramite http get nel servizio di exporter può succedere
				throw new NotFoundException("Dati non trovati");
			}
			list = calcolaLabels(list, this.search.getProtocollo(),(StatsSearchForm)this.search);
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
			String headerLabel = this.getTipoFiltroDatiMittente(); 

			TipoVisualizzazione tipoVisualizzazione = ((StatsSearchForm)this.search).getTipoVisualizzazione();
			List<TipoBanda> tipiBanda = new ArrayList<TipoBanda>();
			tipiBanda.add(((StatsSearchForm)this.search).getTipoBanda());
			List<TipoLatenza> tipiLatenza = new ArrayList<TipoLatenza>();
			tipiLatenza.add(((StatsSearchForm)this.search).getTipoLatenza());
			String tipoRiconoscimento = this.search.getRiconoscimento();
			String identificazione = this.search.getIdentificazione();
			String tokenClaim = this.search.getTokenClaim();
			// creazione del report con Dynamic Report
			JasperReportBuilder report = ExportUtils.creaReportDistribuzione(list, titoloReport, log, tipoVisualizzazione, ((StatsSearchForm)this.search).getNumeroDimensioni(),  
					tipiBanda, tipiLatenza,
					((StatsSearchForm)this.search).getTipoStatistica(), tipoRiconoscimento, identificazione, tokenClaim, false); 

			// scrittura del report sullo stream
			ExportUtils.esportaCsv(response.getOutputStream(),report,titoloReport,headerLabel,tipoVisualizzazione, ((StatsSearchForm)this.search).getNumeroDimensioni(), 
					tipiBanda, tipiLatenza,
					((StatsSearchForm)this.search).getTipoStatistica(),tipoRiconoscimento, identificazione, tokenClaim);

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

		List<ResDistribuzione> list = null;
		try {
			list = ((IStatisticheGiornaliere)this.service).findAllDistribuzioneServizioApplicativo();
			if(list==null || list.size()<=0){
				// passando dalla console, questo caso non succede mai, mentre tramite http get nel servizio di exporter può succedere
				throw new NotFoundException("Dati non trovati");
			}
			list = calcolaLabels(list, this.search.getProtocollo(),(StatsSearchForm)this.search);
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
			String headerLabel = this.getTipoFiltroDatiMittente();

			TipoVisualizzazione tipoVisualizzazione = ((StatsSearchForm)this.search).getTipoVisualizzazione();
			List<TipoBanda> tipiBanda = new ArrayList<TipoBanda>();
			tipiBanda.add(((StatsSearchForm)this.search).getTipoBanda());
			List<TipoLatenza> tipiLatenza = new ArrayList<TipoLatenza>();
			tipiLatenza.add(((StatsSearchForm)this.search).getTipoLatenza());
			String tipoRiconoscimento = this.search.getRiconoscimento(); 
			String identificazione = this.search.getIdentificazione();
			String tokenClaim = this.search.getTokenClaim();
			// creazione del report con Dynamic Report
			JasperReportBuilder report = ExportUtils.creaReportDistribuzione(list, titoloReport, log, tipoVisualizzazione, ((StatsSearchForm)this.search).getNumeroDimensioni(),  
					tipiBanda, tipiLatenza,
					((StatsSearchForm)this.search).getTipoStatistica(), tipoRiconoscimento, identificazione, tokenClaim, false); 

			// scrittura del report sullo stream
			ExportUtils.esportaXls(response.getOutputStream(),report,titoloReport,headerLabel,tipoVisualizzazione, ((StatsSearchForm)this.search).getNumeroDimensioni(), 
					tipiBanda, tipiLatenza,
					((StatsSearchForm)this.search).getTipoStatistica(),tipoRiconoscimento, identificazione, tokenClaim);

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

		List<ResDistribuzione> list = null;
		try {
			list = ((IStatisticheGiornaliere)this.service).findAllDistribuzioneServizioApplicativo();
			if(list==null || list.size()<=0){
				// passando dalla console, questo caso non succede mai, mentre tramite http get nel servizio di exporter può succedere
				throw new NotFoundException("Dati non trovati");
			}
			list = calcolaLabels(list, this.search.getProtocollo(),(StatsSearchForm)this.search);
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
			String headerLabel = this.getTipoFiltroDatiMittente();

			TipoVisualizzazione tipoVisualizzazione = ((StatsSearchForm)this.search).getTipoVisualizzazione();
			List<TipoBanda> tipiBanda = new ArrayList<TipoBanda>();
			tipiBanda.add(((StatsSearchForm)this.search).getTipoBanda());
			List<TipoLatenza> tipiLatenza = new ArrayList<TipoLatenza>();
			tipiLatenza.add(((StatsSearchForm)this.search).getTipoLatenza());
			String tipoRiconoscimento = this.search.getRiconoscimento();
			String identificazione = this.search.getIdentificazione();
			String tokenClaim = this.search.getTokenClaim();
			// creazione del report con Dynamic Report
			JasperReportBuilder report = ExportUtils.creaReportDistribuzione(list, titoloReport, log, tipoVisualizzazione, ((StatsSearchForm)this.search).getNumeroDimensioni(),  
					tipiBanda, tipiLatenza,
					((StatsSearchForm)this.search).getTipoStatistica(), tipoRiconoscimento, identificazione, tokenClaim, true); 

			// scrittura del report sullo stream
			ExportUtils.esportaPdf(response.getOutputStream(),report,titoloReport,headerLabel,tipoVisualizzazione, ((StatsSearchForm)this.search).getNumeroDimensioni(), 
					tipiBanda, tipiLatenza,
					((StatsSearchForm)this.search).getTipoStatistica(),tipoRiconoscimento, identificazione, tokenClaim);

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
		if(StringUtils.isNotEmpty(this.search.getRiconoscimento())) {
			if(this.search.getRiconoscimento().equals(org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_APPLICATIVO)) {
				return CostantiGrafici.DISTRIBUZIONE_SA_APPLICATIVO_FILE_NAME;
			}  else if(this.search.getRiconoscimento().equals(org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_IDENTIFICATIVO_AUTENTICATO)) {
				return CostantiGrafici.DISTRIBUZIONE_SA_IDENTIFICATIVO_AUTENTICATO_FILE_NAME;
			} else if(this.search.getRiconoscimento().equals(org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_INDIRIZZO_IP)) {
				return CostantiGrafici.DISTRIBUZIONE_SA_INDIRIZZO_IP_FILE_NAME;
			} else { // token
				if (StringUtils.isNotEmpty(this.search.getTokenClaim())) {
					org.openspcoop2.core.transazioni.utils.TipoCredenzialeMittente tcm = null;
					try {
						tcm = org.openspcoop2.core.transazioni.utils.TipoCredenzialeMittente.toEnumConstant(this.search.getTokenClaim(), true);
					}catch(Exception e) {
						Logger log = LoggerManager.getPddMonitorCoreLogger();
						log.error(e.getMessage(),e);
					}
					
					if(tcm!=null) {
						switch (tcm) {
						case TOKEN_CLIENT_ID:
							return CostantiGrafici.DISTRIBUZIONE_SA_TOKEN_CLIENTID_FILE_NAME;
						case TOKEN_EMAIL:
							return CostantiGrafici.DISTRIBUZIONE_SA_TOKEN_EMAIL_FILE_NAME;
						case TOKEN_ISSUER:
							return CostantiGrafici.DISTRIBUZIONE_SA_TOKEN_ISSUER_FILE_NAME;
						case TOKEN_SUBJECT:
							return CostantiGrafici.DISTRIBUZIONE_SA_TOKEN_SUBJECT_FILE_NAME;
						case TOKEN_USERNAME:
							return CostantiGrafici.DISTRIBUZIONE_SA_TOKEN_USERNAME_FILE_NAME;
						case TRASPORTO:
						default:
							// caso impossibile
							break; 
						}
					}
				} 
			}
		}

		return CostantiGrafici.DISTRIBUZIONE_SERVIZIO_APPLICATIVO_FILE_NAME;
	}
	
	public boolean isTimeoutEvent(){
		return ((IStatisticheGiornaliere)this.service).isTimeoutEvent();
	}
}
