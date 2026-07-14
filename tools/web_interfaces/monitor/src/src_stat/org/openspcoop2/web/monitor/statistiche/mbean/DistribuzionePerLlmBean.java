/*
 * GovWay - A customizable API Gateway
 * https://govway.org
 *
 * Copyright (c) 2005-2026 Link.it srl (https://link.it).
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

import javax.faces.application.Application;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.openspcoop2.core.config.driver.db.DriverConfigurazioneDB;
import org.openspcoop2.core.registry.driver.db.DriverRegistroServiziDB;
import org.openspcoop2.core.statistiche.constants.TipoBanda;
import org.openspcoop2.core.statistiche.constants.TipoLatenza;
import org.openspcoop2.core.statistiche.constants.TipoReport;
import org.openspcoop2.core.statistiche.constants.TipoVisualizzazione;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.monitor.sdk.constants.StatisticType;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.json.JSONUtils;
import org.openspcoop2.utils.transport.http.HttpUtilities;
import org.openspcoop2.web.monitor.core.dao.IService;
import org.openspcoop2.web.monitor.core.datamodel.ResBase;
import org.openspcoop2.web.monitor.core.datamodel.ResDistribuzione;
import org.openspcoop2.web.monitor.core.mbean.DynamicPdDBean;
import org.openspcoop2.web.monitor.core.report.ReportDataSource;
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

import com.fasterxml.jackson.databind.node.ObjectNode;

import jakarta.el.ELContext;
import jakarta.el.ExpressionFactory;
import jakarta.el.ValueExpression;
import jakarta.servlet.http.HttpServletResponse;

/**
 * DistribuzionePerLlmBean
 *
 * Distribuzione dei dati LLM (raggruppamento per provider / model / provider-binding)
 * con metrica selezionabile (richieste, banda, tempo medio, token, costo).
 *
 * @author Poli Andrea (apoli@link.it)
 *
 */
public class DistribuzionePerLlmBean<T extends ResBase> extends
BaseStatsMBean<T, Integer, IService<ResBase, Integer>> {

	private static final long serialVersionUID = 1L;

	public DistribuzionePerLlmBean() {
		super();
		this.initDpLB();
	}
	public DistribuzionePerLlmBean(org.openspcoop2.core.commons.search.dao.IServiceManager serviceManager, org.openspcoop2.core.plugins.dao.IServiceManager pluginsServiceManager,
			DriverRegistroServiziDB driverRegistroServiziDB, DriverConfigurazioneDB driverConfigurazioneDB) {
		super(serviceManager, pluginsServiceManager,
				driverRegistroServiziDB, driverConfigurazioneDB);
		this.initDpLB();
	}
	private void initDpLB() {
		this.setChartId("distribuzioneLlm");
	}

	@SuppressWarnings("unchecked")
	public void setStatisticheGiornaliereService(
			IStatisticheGiornaliere statisticheGiornaliereService) {
		this.service =  (IService<T, Integer>) statisticheGiornaliereService;
	}

	public String getXml() {
		List<ResDistribuzione> list;
		this.setVisualizzaComandiExport(false);
		((StatsSearchForm)this.search).syncTipoVisualizzazioneFromLlm();
		try {
			list = ((IStatisticheGiornaliere)this.service).findAllDistribuzioneLlm();
		} catch (ServiceException e) {
			this.addErroreDuranteRecuperoDati(e);
			DynamicPdDBean.log.error(e.getMessage(), e);
			return null;
		}

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

		if(list != null && !list.isEmpty())
			this.setVisualizzaComandiExport(true);

		return xml;
	}

	public String getJson(){
		ObjectNode grafico = null;
		List<ResDistribuzione> list;
		this.setVisualizzaComandiExport(false);
		((StatsSearchForm)this.search).syncTipoVisualizzazioneFromLlm();
		try {
			list = ((IStatisticheGiornaliere)this.service).findAllDistribuzioneLlm();
		} catch (ServiceException e) {
			this.addErroreDuranteRecuperoDati(e);
			DynamicPdDBean.log.error(e.getMessage(), e);
			return null;
		}

		TipoReport tipoReport = ((StatsSearchForm)this.search).getTipoReport();

		try {
			switch (tipoReport) {
			case BAR_CHART: {
				NumeroDimensioni numeroDimensioni = ((StatsSearchForm)this.search).getNumeroDimensioni();
				if(NumeroDimensioni.DIMENSIONI_3.equals(numeroDimensioni) || NumeroDimensioni.DIMENSIONI_3_CUSTOM.equals(numeroDimensioni)) {
					StatisticType statisticType = StatsUtils.checkStatisticType((StatsSearchForm) this.search, false);
					grafico = JsonStatsUtils.getJsonHeatmapChartDistribuzione(list,(StatsSearchForm) this.search, this.getCaption(), this.getSubCaption(), this.getDirezioneLabel(), this.getSlice(), statisticType, this.isVisualizzaTotaleNelleCelleGraficoHeatmap(), DynamicPdDBean.log);
				} else {
					grafico = JsonStatsUtils.getJsonBarChartDistribuzione(list,(StatsSearchForm) this.search, this.getCaption(), this.getSubCaption(), this.getDirezioneLabel(), this.getSlice());
				}
				break;
			}
			case PIE_CHART:
				grafico = JsonStatsUtils.getJsonPieChartDistribuzione(list, ((StatsSearchForm)this.search), this.getCaption(), this.getSubCaption() , this.getSlice());
				break;
			default:
				break;
			}
		} catch (UtilsException e) {
			this.addErroreDuranteRecuperoDati(e);
			DynamicPdDBean.log.error(e.getMessage(), e);
			return null;
		}

		if(list != null && !list.isEmpty())
			this.setVisualizzaComandiExport(true);

		try {
			JSONUtils jsonUtils = JSONUtils.getInstance();
			return grafico != null ?  jsonUtils.toString(grafico) : "";
		} catch (UtilsException e) {
			MessageUtils.addErrorMsg("Si e' verificato un errore durante la serializzazione json:"	+ e.getMessage());
			DynamicPdDBean.log.error(e.getMessage(), e);
			return null;
		}
	}

	@Override
	public String getData(){
		return this.getJson();
	}

	public String getRaggruppamentoColumnHeader() {
		return this.getRaggruppamentoLabel();
	}

	private String getRaggruppamentoLabel() {
		String raggruppa = ((StatsSearchForm)this.search).getLlmRaggruppaPer();
		MessageManager mm = MessageManager.getInstance();
		if(StatsSearchForm.LLM_RAGGRUPPA_MODEL.equals(raggruppa)) {
			return mm.getMessage("stats.analisiStatistica.tipoDistribuzione.llm.raggruppa.model");
		}
		if(StatsSearchForm.LLM_RAGGRUPPA_BINDING.equals(raggruppa)) {
			return mm.getMessage("stats.analisiStatistica.tipoDistribuzione.llm.raggruppa.binding");
		}
		return mm.getMessage("stats.analisiStatistica.tipoDistribuzione.llm.raggruppa.provider");
	}

	public String getCaption() {
		StringBuilder sb = new StringBuilder();
		sb.append(MessageManager.getInstance().getMessage(StatisticheCostanti.STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_DISTRIBUZIONE_LABEL_KEY)).append(CostantiGrafici.WHITE_SPACE);

		if(((StatsSearchForm)this.search).isShowUnitaTempo()) {
			if (StatisticType.ORARIA.equals(this.getTempo())) {
				sb.append(CostantiGrafici.ORARIA_LABEL).append(CostantiGrafici.WHITE_SPACE);
			} else {
				sb.append(CostantiGrafici.GIORNALIERA_LABEL).append(CostantiGrafici.WHITE_SPACE);
			}
		}

		sb.append(MessageManager.getInstance().getMessage(StatisticheCostanti.STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_LLM_LABEL_SUFFIX_KEY)).append(CostantiGrafici.WHITE_SPACE);
		sb.append(this.getRaggruppamentoLabel());
		return sb.toString();
	}

	public String getSubCaption() {
		String captionText = StatsUtils.getSubCaption((StatsSearchForm)this.search);
		StringBuilder caption = new StringBuilder(captionText);
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
		if(ae!=null) {
			// nop
		}
		FacesContext facesContext = FacesContext.getCurrentInstance();
		Application app = facesContext.getApplication();
		ExpressionFactory elFactory = app.getExpressionFactory();
		ELContext elContext = facesContext.getELContext();
		ValueExpression valueExp = elFactory.createValueExpression(elContext,
				"#{distribuzionePerLlmBean}",
				DistribuzionePerLlmBean.class);
		DistribuzionePerLlmBean<ResDistribuzione> ab = new DistribuzionePerLlmBean<>();
		valueExp.setValue(elContext, ab);
	}

	@Override
	public String submit() {
		return "distribLlm";
	}

	public String getSommaColumnHeader(){
		return ((StatsSearchForm)this.search).getLlmVisualizzaPerLabel();
	}

	@Override
	public String getExportFilename() {
		return "distribuzione_llm";
	}

	@Override
	public String esportaCsv() {
		try{
			return this.esportaTabellaEngine(null, true, CostantiGrafici.CSV_EXTENSION);
		}catch(Exception e){
			DynamicPdDBean.log.error(e.getMessage(), e);
			this.addErroroInatteso(e);
			return null;
		}
	}
	@Override
	public void esportaCsv(HttpServletResponse response) throws Exception {
		this.esportaTabellaEngine(response, false, CostantiGrafici.CSV_EXTENSION);
	}
	@Override
	public String esportaXls() {
		try{
			return this.esportaTabellaEngine(null, true, CostantiGrafici.XLS_EXTENSION);
		}catch(Exception e){
			DynamicPdDBean.log.error(e.getMessage(), e);
			this.addErroroInatteso(e);
			return null;
		}
	}
	@Override
	public void esportaXls(HttpServletResponse response) throws Exception {
		this.esportaTabellaEngine(response, false, CostantiGrafici.XLS_EXTENSION);
	}
	@Override
	public String esportaPdf() {
		try{
			return this.esportaTabellaEngine(null, true, CostantiGrafici.PDF_EXTENSION);
		}catch(Exception e){
			DynamicPdDBean.log.error(e.getMessage(), e);
			this.addErroroInatteso(e);
			return null;
		}
	}
	@Override
	public void esportaPdf(HttpServletResponse response) throws Exception {
		this.esportaTabellaEngine(response, false, CostantiGrafici.PDF_EXTENSION);
	}
	@Override
	public String esportaXml() {
		return this.getXml();
	}
	@Override
	public void esportaXml(HttpServletResponse response) throws Exception {
		this.esportaStream(response, this.getXml(), CostantiGrafici.XML_EXTENSION);
	}
	@Override
	public String esportaJson() {
		return this.getJson();
	}
	@Override
	public void esportaJson(HttpServletResponse response) throws Exception {
		this.esportaStream(response, this.getJson(), CostantiGrafici.JSON_EXTENSION);
	}

	private String esportaTabellaEngine(HttpServletResponse responseParam, boolean useFaceContext, String fileExt) throws ServiceException, NotFoundException {
		String filename = this.getExportFilename()+fileExt;

		((StatsSearchForm)this.search).syncTipoVisualizzazioneFromLlm();

		List<ResDistribuzione> list = null;
		try {
			list = ((IStatisticheGiornaliere)this.service).findAllDistribuzioneLlm();
			if(list==null || list.isEmpty()){
				throw this.newDatiNonTrovatiException();
			}
		} catch (Exception e) {
			return gestioneErrore(useFaceContext, e);
		}

		if(((StatsSearchForm)this.search).isLlmVisualizzaCosto()) {
			ExportUtils.setFormatoValoreColonna(new ExportUtils.FormatoValoreColonna(((StatsSearchForm)this.search).getLlmVisualizzaPerLabel(), 6, " $"));
		}

		try {
			HttpServletResponse response = null;
			FacesContext context = null;
			if(useFaceContext){
				context = FacesContext.getCurrentInstance();
				response = (HttpServletResponse) context.getExternalContext().getResponse();
			}
			else{
				response = responseParam;
			}

			response.reset();
			HttpUtilities.setOutputFile(response, true, filename);
			response.setStatus(200);

			String titoloReport = this.getCaption() + CostantiGrafici.WHITE_SPACE + this.getSubCaption();
			String headerLabel = this.getRaggruppamentoColumnHeader();

			TipoVisualizzazione tipoVisualizzazione = ((StatsSearchForm)this.search).getTipoVisualizzazione();
			List<TipoBanda> tipiBanda = new ArrayList<>();
			tipiBanda.add(((StatsSearchForm)this.search).getTipoBanda());
			List<TipoLatenza> tipiLatenza = new ArrayList<>();
			tipiLatenza.add(((StatsSearchForm)this.search).getTipoLatenza());

			boolean pdf = CostantiGrafici.PDF_EXTENSION.equals(fileExt);
			ReportDataSource report = ExportUtils.creaReportDistribuzione(list, titoloReport, DynamicPdDBean.log, tipoVisualizzazione,
					((StatsSearchForm)this.search).getNumeroDimensioni(), ((StatsSearchForm)this.search).getNumeroDimensioniCustom(),
					tipiBanda, tipiLatenza, ((StatsSearchForm)this.search).getTipoStatistica(), pdf);

			if(CostantiGrafici.CSV_EXTENSION.equals(fileExt)) {
				ExportUtils.esportaCsv(response.getOutputStream(),report,titoloReport,headerLabel,tipoVisualizzazione,
						((StatsSearchForm)this.search).getNumeroDimensioni(), ((StatsSearchForm)this.search).getNumeroDimensioniCustom(),
						tipiBanda, tipiLatenza,((StatsSearchForm)this.search).getTipoStatistica());
			} else if(CostantiGrafici.XLS_EXTENSION.equals(fileExt)) {
				ExportUtils.esportaXls(response.getOutputStream(),report,titoloReport,headerLabel,tipoVisualizzazione,
						((StatsSearchForm)this.search).getNumeroDimensioni(), ((StatsSearchForm)this.search).getNumeroDimensioniCustom(),
						tipiBanda, tipiLatenza,((StatsSearchForm)this.search).getTipoStatistica());
			} else {
				ExportUtils.esportaPdf(response.getOutputStream(),report,titoloReport,headerLabel,tipoVisualizzazione,
						((StatsSearchForm)this.search).getNumeroDimensioni(), ((StatsSearchForm)this.search).getNumeroDimensioniCustom(),
						tipiBanda, tipiLatenza,((StatsSearchForm)this.search).getTipoStatistica());
			}

			if(useFaceContext){
				context.responseComplete();
			}
		} catch (Exception e) {
			DynamicPdDBean.log.error(e.getMessage(), e);
			if(useFaceContext){
				FacesContext.getCurrentInstance().responseComplete();
				MessageUtils.addErrorMsg(CostantiGrafici.CSV_EXPORT_MESSAGGIO_ERRORE);
			}
			else{
				throw new ServiceException(e.getMessage(),e);
			}
		} finally {
			ExportUtils.clearFormatoValoreColonna();
		}
		return null;
	}

	private void esportaStream(HttpServletResponse response, String content, String fileExt) throws Exception {
		String filename = this.getExportFilename()+fileExt;
		response.reset();
		HttpUtilities.setOutputFile(response, true, filename);
		response.setStatus(200);
		if(content!=null) {
			response.getOutputStream().write(content.getBytes());
		}
		response.getOutputStream().flush();
	}

	public boolean isTimeoutEvent(){
		return ((IStatisticheGiornaliere)this.service).isTimeoutEvent();
	}
}
