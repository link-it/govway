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

import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.monitor.sdk.constants.StatisticType;
import org.openspcoop2.protocol.engine.utils.NamingUtils;
import org.openspcoop2.utils.transport.http.HttpUtilities;

import org.openspcoop2.core.statistiche.constants.TipoBanda;
import org.openspcoop2.core.statistiche.constants.TipoLatenza;
import org.openspcoop2.core.statistiche.constants.TipoReport;
import org.openspcoop2.core.statistiche.constants.TipoVisualizzazione;
import org.openspcoop2.web.monitor.core.core.Utility;
import org.openspcoop2.web.monitor.core.dao.IService;
import org.openspcoop2.web.monitor.core.datamodel.ResBase;
import org.openspcoop2.web.monitor.core.datamodel.ResDistribuzione;
import org.openspcoop2.web.monitor.core.mbean.DynamicPdDBean;
import org.openspcoop2.web.monitor.core.utils.MessageUtils;
import org.openspcoop2.web.monitor.statistiche.bean.StatsSearchForm;
import org.openspcoop2.web.monitor.statistiche.constants.CostantiGrafici;
import org.openspcoop2.web.monitor.statistiche.dao.IStatisticheGiornaliere;
import org.openspcoop2.web.monitor.statistiche.utils.ExportUtils;
import org.openspcoop2.web.monitor.statistiche.utils.JsonStatsUtils;
import org.openspcoop2.web.monitor.statistiche.utils.StatsUtils;
import net.sf.dynamicreports.jasper.builder.JasperReportBuilder;
import net.sf.json.JSONObject;

public class DistribuzionePerSoggettoBean<T extends ResBase> extends BaseStatsMBean<T,Integer,IService<ResBase, Integer>>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L; 

	public DistribuzionePerSoggettoBean() {
		super();
		this.setChartId("distribuzioneSoggetto");
		this.setFilename("../FusionCharts/ScrollCombi2D.swf");
	}

	@SuppressWarnings("unchecked")
	public void setStatisticheGiornaliereService(
			IStatisticheGiornaliere statisticheGiornaliereService) {
		this.service =  (IService<T, Integer>) statisticheGiornaliereService;
	}

	public void initSearchListenerRemoto(ActionEvent ae){
		this.search.initSearchListener(ae);
		((StatsSearchForm)this.search).setDistribuzionePerSoggettoRemota(true);
	}
	public void initSearchListenerLocale(ActionEvent ae){
		this.search.initSearchListener(ae);
		((StatsSearchForm)this.search).setDistribuzionePerSoggettoRemota(false);
	}
	
	public static List<ResDistribuzione>  calcolaLabels (List<ResDistribuzione> list, String protocollo){
		if(list!=null  && list.size()>0){
			for (ResDistribuzione res : list) {
				String tipoNomeSoggetto = res.getRisultato();
				
				String tipoSoggetto = Utility.parseTipoSoggetto(tipoNomeSoggetto);
				String nomeSoggetto = Utility.parseNomeSoggetto(tipoNomeSoggetto);
				
				try {
					res.setRisultato(NamingUtils.getLabelSoggetto(protocollo, tipoSoggetto, nomeSoggetto));
				} catch (Exception e) {				
				}
			}
		}
		return list;
	}

	public String getXml() {	
		List<ResDistribuzione> list;
		this.setVisualizzaComandiExport(false);
		try {
			list = ((IStatisticheGiornaliere)this.service).findAllDistribuzioneSoggetto();
		} catch (ServiceException e) {
			MessageUtils.addErrorMsg("Si e' verificato un errore durante il recupero dei dati:"	+ e.getMessage());
			DynamicPdDBean.log.error(e.getMessage(), e);
			return null;
		}
		
		list = calcolaLabels(list, this.search.getProtocollo());

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
			list = ((IStatisticheGiornaliere)this.service).findAllDistribuzioneSoggetto();
		} catch (ServiceException e) {
			MessageUtils.addErrorMsg("Si e' verificato un errore durante il recupero dei dati:"	+ e.getMessage());
			DynamicPdDBean.log.error(e.getMessage(), e);
			return null;
		}
		
		list = calcolaLabels(list, this.search.getProtocollo());

		TipoReport tipoReport = ((StatsSearchForm)this.search).getTipoReport();

		switch (tipoReport) {
		case BAR_CHART:
			grafico = JsonStatsUtils.getJsonBarChartDistribuzione(list,(StatsSearchForm) this.search, this.getCaption(), this.getSubCaption(), this.getDirezioneLabel() , this.getSlice());
			break;
		case PIE_CHART:
			grafico = JsonStatsUtils.getJsonPieChartDistribuzione(list, ((StatsSearchForm)this.search), this.getCaption(), this.getSubCaption() , this.getSlice());
			break;
		default:
			break;
		}

		if(list != null && list.size() > 0)
			this.setVisualizzaComandiExport(true);

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

	public String getCaption(){
		String res = CostantiGrafici.DISTRIBUZIONE_PREFIX + CostantiGrafici.WHITE_SPACE;
		
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
		}
		if(((StatsSearchForm)this.search).isDistribuzionePerSoggettoRemota()){
			res = CostantiGrafici.DISTRIBUZIONE_PER_SOGGETTO_REMOTO_LABEL_SUFFIX + CostantiGrafici.WHITE_SPACE;
		}
		else{
			res = CostantiGrafici.DISTRIBUZIONE_PER_SOGGETTO_LOCALE_LABEL_SUFFIX + CostantiGrafici.WHITE_SPACE;
		}
		return res;
	}

	public String getSubCaption(){
		String captionText = StatsUtils.getSubCaption((StatsSearchForm)this.search);
		StringBuffer caption = new StringBuffer(
				captionText);
		//		if(StringUtils.isNotBlank(this.search.getNomeServizio())){
		//			caption.append("per il Servizio "+this.search.getNomeServizio());
		//		}
		//		if(StringUtils.isNotBlank(this.search.getNomeAzione())){
		//			caption.append(", azione "+this.search.getNomeAzione());
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

	public void newSearch(ActionEvent ae){

		FacesContext facesContext = FacesContext.getCurrentInstance();
		Application app = facesContext.getApplication();
		ExpressionFactory elFactory = app.getExpressionFactory();
		ELContext elContext = facesContext.getELContext();
		ValueExpression valueExp = elFactory.createValueExpression(elContext, "#{distribuzionePerSoggettoBean}", DistribuzionePerSoggettoBean.class);
		DistribuzionePerSoggettoBean<ResDistribuzione> ab = new DistribuzionePerSoggettoBean<ResDistribuzione>();

		valueExp.setValue(elContext, ab);
	}

	@Override
	public String submit(){
		//Non e' piu necessario selezionare il soggetto in gestione, con l'introduzione
		//di poter associare piu soggetti spcoop ad un customer_user
		//il soggetto in gestione potra' essere selezionato come filtro nel form di ricerca
		//in caso non fosse selezionato allora vengono presi in considerazione
		//tutti i soggetti associati al soggetto loggato
		//		if(Utility.getSoggettoInGestione()==null){
		//			MessageUtils.addErrorMsg("E' necessario selezionare il Soggetto.");
		//			return null;
		//		}
		return "distribSoggetto";
	}

	public String getSommaColumnHeader(){
		return StatsUtils.sommaColumnHeader((StatsSearchForm)this.search, "Transazioni");
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
			list = ((IStatisticheGiornaliere)this.service).findAllDistribuzioneSoggetto();
			if(list==null || list.size()<=0){
				// passando dalla console, questo caso non succede mai, mentre tramite http get nel servizio di exporter può succedere
				throw new NotFoundException("Dati non trovati");
			}
			
			list = calcolaLabels(list, this.search.getProtocollo());
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
			String headerLabel = CostantiGrafici.SOGGETTO_LABEL;

			TipoVisualizzazione tipoVisualizzazione = ((StatsSearchForm)this.search).getTipoVisualizzazione();
			List<TipoBanda> tipiBanda = new ArrayList<TipoBanda>();
			tipiBanda.add(((StatsSearchForm)this.search).getTipoBanda());
			List<TipoLatenza> tipiLatenza = new ArrayList<TipoLatenza>();
			tipiLatenza.add(((StatsSearchForm)this.search).getTipoLatenza());
			// creazione del report con Dynamic Report
			JasperReportBuilder report = ExportUtils.creaReportDistribuzione(list, titoloReport, log, tipoVisualizzazione, tipiBanda, tipiLatenza,((StatsSearchForm)this.search).getTipoStatistica(), false); 

			// scrittura del report sullo stream
			ExportUtils.esportaCsv(response.getOutputStream(),report,titoloReport,headerLabel,tipoVisualizzazione,tipiBanda, tipiLatenza,((StatsSearchForm)this.search).getTipoStatistica());

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
			list = ((IStatisticheGiornaliere)this.service).findAllDistribuzioneSoggetto();
			if(list==null || list.size()<=0){
				// passando dalla console, questo caso non succede mai, mentre tramite http get nel servizio di exporter può succedere
				throw new NotFoundException("Dati non trovati");
			}
			list = calcolaLabels(list, this.search.getProtocollo());
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
			String headerLabel = CostantiGrafici.SOGGETTO_LABEL;

			TipoVisualizzazione tipoVisualizzazione = ((StatsSearchForm)this.search).getTipoVisualizzazione();
			List<TipoBanda> tipiBanda = new ArrayList<TipoBanda>();
			tipiBanda.add(((StatsSearchForm)this.search).getTipoBanda());
			List<TipoLatenza> tipiLatenza = new ArrayList<TipoLatenza>();
			tipiLatenza.add(((StatsSearchForm)this.search).getTipoLatenza());
			// creazione del report con Dynamic Report
			JasperReportBuilder report = ExportUtils.creaReportDistribuzione(list, titoloReport, log, tipoVisualizzazione, tipiBanda, tipiLatenza,((StatsSearchForm)this.search).getTipoStatistica(), false); 

			// scrittura del report sullo stream
			ExportUtils.esportaXls(response.getOutputStream(),report,titoloReport,headerLabel,tipoVisualizzazione,tipiBanda, tipiLatenza,((StatsSearchForm)this.search).getTipoStatistica());

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
			list = ((IStatisticheGiornaliere)this.service).findAllDistribuzioneSoggetto();
			if(list==null || list.size()<=0){
				// passando dalla console, questo caso non succede mai, mentre tramite http get nel servizio di exporter può succedere
				throw new NotFoundException("Dati non trovati");
			}
			list = calcolaLabels(list, this.search.getProtocollo());
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
			String headerLabel = CostantiGrafici.SOGGETTO_LABEL;

			TipoVisualizzazione tipoVisualizzazione = ((StatsSearchForm)this.search).getTipoVisualizzazione();
			List<TipoBanda> tipiBanda = new ArrayList<TipoBanda>();
			tipiBanda.add(((StatsSearchForm)this.search).getTipoBanda());
			List<TipoLatenza> tipiLatenza = new ArrayList<TipoLatenza>();
			tipiLatenza.add(((StatsSearchForm)this.search).getTipoLatenza());
			// creazione del report con Dynamic Report
			JasperReportBuilder report = ExportUtils.creaReportDistribuzione(list, titoloReport, log, tipoVisualizzazione, tipiBanda, tipiLatenza,((StatsSearchForm)this.search).getTipoStatistica(), true); 

			// scrittura del report sullo stream
			ExportUtils.esportaPdf(response.getOutputStream(),report,titoloReport,headerLabel,tipoVisualizzazione,tipiBanda, tipiLatenza,((StatsSearchForm)this.search).getTipoStatistica());

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
		if(((StatsSearchForm)this.search).isDistribuzionePerSoggettoRemota())
			return CostantiGrafici.DISTRIBUZIONE_SOGGETTO_REMOTO_FILE_NAME;
		else 
			return CostantiGrafici.DISTRIBUZIONE_SOGGETTO_LOCALE_FILE_NAME;
	}
}
