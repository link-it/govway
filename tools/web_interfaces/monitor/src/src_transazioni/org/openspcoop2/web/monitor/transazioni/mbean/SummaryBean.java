package org.openspcoop2.web.monitor.transazioni.mbean;

import java.io.Serializable;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.commons.search.AccordoServizioParteSpecifica;
import org.openspcoop2.core.commons.search.Soggetto;
import org.openspcoop2.core.commons.search.constants.TipoPdD;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.transazioni.constants.Colors;
import org.openspcoop2.monitor.engine.condition.EsitoUtils;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.engine.utils.NamingUtils;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.openspcoop2.utils.resources.MapReader;
import org.openspcoop2.web.lib.users.dao.Stato;
import org.openspcoop2.web.lib.users.dao.User;
import org.openspcoop2.web.monitor.core.bean.AbstractDateSearchForm;
import org.openspcoop2.web.monitor.core.bean.ApplicationBean;
import org.openspcoop2.web.monitor.core.bean.UserDetailsBean;
import org.openspcoop2.web.monitor.core.constants.Costanti;
import org.openspcoop2.web.monitor.core.constants.CostantiGrafici;
import org.openspcoop2.web.monitor.core.constants.NomiTabelle;
import org.openspcoop2.web.monitor.core.core.PddMonitorProperties;
import org.openspcoop2.web.monitor.core.core.PermessiUtenteOperatore;
import org.openspcoop2.web.monitor.core.core.Utility;
import org.openspcoop2.web.monitor.core.dao.DynamicUtilsService;
import org.openspcoop2.web.monitor.core.dao.IDynamicUtilsService;
import org.openspcoop2.web.monitor.core.dao.IUserService;
import org.openspcoop2.web.monitor.core.dao.UserService;
import org.openspcoop2.web.monitor.core.datamodel.ResLive;
import org.openspcoop2.web.monitor.core.filters.BrowserFilter;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;
import org.openspcoop2.web.monitor.core.report.CostantiReport;
import org.openspcoop2.web.monitor.core.report.ILiveReport;
import org.openspcoop2.web.monitor.core.report.ReportFactory;
import org.openspcoop2.web.monitor.core.utils.BrowserInfo;
import org.openspcoop2.web.monitor.core.utils.DynamicPdDBeanUtils;
import org.openspcoop2.web.monitor.core.utils.MessageUtils;
import org.openspcoop2.web.monitor.core.utils.ParseUtility;
import org.openspcoop2.web.monitor.transazioni.dao.ITransazioniService;
import org.slf4j.Logger;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class SummaryBean implements Serializable{

	private static final String STATO_PERIODO = NomiTabelle.WELCOME_SCREEN.toString();

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private List<ResLive> dataLive = new ArrayList<ResLive>();
	private int maxDataStored = 15;

	private Date lastRequest;
	private Date minDate;
	private Date maxDate;
	private String periodo;
	private String periodoDefault = CostantiReport.PERIODO_NOT_SET;
	private int offset;
	private String soggettoLocale;

	private String intervalloRefresh = null;

	private transient ILiveReport report = null;

	private static Logger log =  LoggerManager.getPddMonitorCoreLogger();

	private String esitoContesto;

	private transient EsitiProperties esitiProperties;

	private List<SelectItem> soggettiAssociati = null;
	
	private Integer soggettiAssociatiSelectItemsWidth = 0;
	private Integer soggettiSelectItemsWidth = 0;
	
	private boolean soggettiAssociatiSelectItemsWidthCheck = false;
	private boolean soggettiSelectItemsWidthCheck = false;
	
	private Integer maxSelectItemsWidth = 900;

	private Integer defaultSelectItemsWidth = 412;
	/*
	 * chart info
	 */
	private transient ITransazioniService transazioniService;
	private transient IDynamicUtilsService dynamicUtilsService;
	private transient IUserService userService;
	private boolean funzionalitaStatisticaAbilitata;

	private boolean useGraficiSVG = false;
	
	private DynamicPdDBeanUtils dynamicUtils = null;
	
	private String protocollo;
	private List<SelectItem> protocolli= null;


	public SummaryBean() {		
		this.lastRequest = new Date();

		try {
			PddMonitorProperties pddMonitorProperties = PddMonitorProperties.getInstance(SummaryBean.log);

			this.intervalloRefresh = pddMonitorProperties.getIntervalloRefreshEsitiLive();

			this.intervalloRefresh = (Integer.parseInt(this.intervalloRefresh) ) + ""; //* 1000

			this.dynamicUtils = new DynamicPdDBeanUtils(SummaryBean.log);
			
			this.dynamicUtilsService = new DynamicUtilsService();
			
			this.userService = new UserService();

			// controllo se e' abilitata la funzionalita' delle statistiche
			this.funzionalitaStatisticaAbilitata = ApplicationBean.getInstance().isFunzionalitaAbilitata(ApplicationBean.FUNZIONALITA_STATISTICHE_BASE);

			// se la funzionalita' delle statistiche e' abilitata allora visualizzo l'andamento temporale salvato nel bean dell'utente.
			if(this.funzionalitaStatisticaAbilitata){
				this.periodoDefault = this.leggiStatoPeriodo();
				// Comportamento di default, se l'utente non ha uno stato salvato mostro l'ultimo anno.
				if(this.periodoDefault == null)
					this.periodoDefault = CostantiReport.ULTIMO_ANNO;
				// scelgo il report
				this.report = ReportFactory.getInstance().getStatisticaReportManager();
			}else {
				// comportamento originale.
				this.periodoDefault = CostantiReport.PERIODO_NOT_SET;
				this.report = ReportFactory.getInstance().getTransazioniReportManager();
			}
			
			IProtocolFactory<?> protocolFactory = null;
			String loggedUtenteModalita = Utility.getLoggedUtenteModalita();
			
			if(Costanti.VALUE_PARAMETRO_MODALITA_ALL.equals(loggedUtenteModalita)) {
				try {
					protocolFactory = ProtocolFactoryManager.getInstance().getDefaultProtocolFactory();
				}catch(Exception e) {
					User user = Utility.getLoggedUtente();
					protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(user.getProtocolliSupportati().get(0));
				}
			} else {
				protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(loggedUtenteModalita);
			}
			
			this.protocollo = protocolFactory.getProtocol();
		} catch (Exception e) {
			SummaryBean.log.error("Errore durante la init del SummaryBean: "+e.getMessage(),e); 
		}

		try {
			this.esitiProperties = EsitiProperties.getInstance(SummaryBean.log);
		} catch (Exception e) {
			SummaryBean.log.error("Errore durante la creazione del form: " + e.getMessage(),e);
		}

		try {
			if(this.esitiProperties.getEsitoTransactionContextDefault()!=null){
				this.esitoContesto = this.esitiProperties.getEsitoTransactionContextDefault();
			}
			else{
				this.esitoContesto = EsitoUtils.ALL_VALUE_AS_STRING;
			}
		} catch (Exception e) {
			SummaryBean.log.error("Errore durante l'impostazione del default per il contesto: " + e.getMessage(),e);
			this.esitoContesto = EsitoUtils.ALL_VALUE_AS_STRING;
		}

		this.setPeriodo(this.periodoDefault);
	}


	public void setTransazioniService(ITransazioniService transazioniService) {
		this.transazioniService = transazioniService;
	}

	/**
	 * recupera i dati per il riepilogo messaggi negli ultimo 12 mesi
	 * @return esitoContenuto
	 */

	public String getEsitoContesto() {
		return this.esitoContesto;
	}

	public void setEsitoContesto(String esitoContesto) {
		this.esitoContesto = esitoContesto;
	}
	
	private Date startDateForLabel;
	private Date endDateForLabel;

	public String getPrintPeriodo(){
		return AbstractDateSearchForm.printPeriodo(this.startDateForLabel, this.endDateForLabel);
	}
	
	public String getXml(){
		if(CostantiReport.PERIODO_NOT_SET.equals(this.periodo)){
			return "<chart></chart>";
		}	

		int[] error_array = new int[this.offset];
		int[] fault_array = new int[this.offset];
		int[] ok_array = new int[this.offset];
		String[] labelArray = new String[this.offset];

		SimpleDateFormat sdf = null;
		SimpleDateFormat sdfend = null;
		Calendar w_end = null;
		Calendar w_start = null;
		Calendar actualStart = null;

		int field = Calendar.DATE;
		int increment = 0;

		w_end=Calendar.getInstance();
		w_end.setTime(this.maxDate);


		w_start = (Calendar)w_end.clone();
		w_start.setTime(this.minDate);

		boolean clearEndMs = false;

		if(CostantiReport.ULTIMO_ANNO.equals(this.periodo)){
			sdf = new SimpleDateFormat(CostantiGrafici.PATTERN_MMM_YY, Locale.ITALIAN);

			field = Calendar.MONTH;
			increment = 1;
			//siccome incremento i mesi devo impostare l'actual come 00:00 del primo del mese
			actualStart = (Calendar) w_start.clone();
			actualStart.set(Calendar.DAY_OF_MONTH, actualStart.getActualMinimum(Calendar.DAY_OF_MONTH));
			actualStart.set(Calendar.HOUR_OF_DAY,actualStart.getActualMinimum(Calendar.HOUR_OF_DAY));
			actualStart.set(Calendar.MINUTE,actualStart.getActualMinimum(Calendar.MINUTE));
			actualStart.clear(Calendar.SECOND);
			actualStart.clear(Calendar.MILLISECOND);


		}else if(CostantiReport.ULTIMI_30_GIORNI.equals(this.periodo)){
			sdf = new SimpleDateFormat(CostantiGrafici.PATTERN_DD_MM, Locale.ITALIAN);
			sdfend = new SimpleDateFormat(CostantiGrafici.PATTERN_DD_MM, Locale.ITALIAN);
			field = Calendar.WEEK_OF_YEAR;
			increment = 1;

			actualStart = (Calendar) w_start.clone();
			actualStart.set(Calendar.HOUR_OF_DAY,actualStart.getActualMinimum(Calendar.HOUR_OF_DAY));
			actualStart.set(Calendar.MINUTE,actualStart.getActualMinimum(Calendar.MINUTE));
			actualStart.clear(Calendar.SECOND);
			actualStart.clear(Calendar.MILLISECOND);

		}else if(CostantiReport.ULTIMI_7_GIORNI.equals(this.periodo)){
			sdf = new SimpleDateFormat(CostantiGrafici.PATTERN_EEE_DD_MM, Locale.ITALIAN);
			field = Calendar.DATE;
			increment = 1;

			actualStart = (Calendar) w_start.clone();
			actualStart.set(Calendar.HOUR_OF_DAY,actualStart.getActualMinimum(Calendar.HOUR_OF_DAY));
			actualStart.set(Calendar.MINUTE,actualStart.getActualMinimum(Calendar.MINUTE));
			actualStart.clear(Calendar.SECOND);
			actualStart.clear(Calendar.MILLISECOND);

		}else if(CostantiReport.ULTIME_24_ORE.equals(this.periodo)){
			sdf = new SimpleDateFormat(CostantiGrafici.PATTERN_HH, Locale.ITALIAN);
			sdfend = new SimpleDateFormat(CostantiGrafici.PATTERN_HH, Locale.ITALIAN);
			field = Calendar.HOUR_OF_DAY;
			increment = 1;

			actualStart = (Calendar) w_start.clone();
			actualStart.set(Calendar.MINUTE,actualStart.getActualMinimum(Calendar.MINUTE));
			actualStart.clear(Calendar.SECOND);
			actualStart.clear(Calendar.MILLISECOND);
			clearEndMs = true;
		}

		for (int i=0;i<this.offset;i++){

			//				Calendar fineMeseAttuale = (Calendar)w_start.clone();
			//				int max=fineMeseAttuale.getActualMaximum(field);
			//				int min=fineMeseAttuale.getActualMinimum(field);
			//				fineMeseAttuale.add(field, increment);//start+1
			//				//fineMeseAttuale.set(field, fineMeseAttuale.getActualMaximum(field));

			Calendar actualEnd = (Calendar)actualStart.clone();
			actualEnd.add(field, increment);

			actualEnd.add(Calendar.MILLISECOND, -1);

			Date s = actualStart.getTime();
			Date e = actualEnd.getTime();
			
			if(i==0){
				this.startDateForLabel = (Date) s.clone();
			}
			if(i==(this.offset-1)){
				this.endDateForLabel = (Date) e.clone();
			}
			
			// controllo che l'estremo superiore non sia nel futuro...
//			if(e.after(new Date()) && (CostantiReport.ULTIMI_30_GIORNI.equals(this.periodo))){
//				actualEnd = Calendar.getInstance();
//				e = actualEnd.getTime();
//			}

			ResLive r = null;
			try{
				// L'xml del Summary Bean viene generato dal report selezionato all'avvio del bean.
				r = this.report.getEsiti(this.getPermessiUtenteOperatore(), s, e, this.periodo, this.esitoContesto, this.protocollo);
			} catch (CoreException er) {
				MessageUtils.addErrorMsg("Si e' verificato un errore durante il recupero degli esiti");
				SummaryBean.log.error(er.getMessage(), er);
				return null;
			}

			error_array[i] = r.getEsitoKo().intValue();
			fault_array[i] = r.getEsitoFault().intValue();
			ok_array[i] = r.getEsitoOk().intValue();
			if(sdfend!=null){
				if(clearEndMs){
					actualEnd.add(Calendar.MILLISECOND, 1);
					e = actualEnd.getTime();
				}
				labelArray[i] = MessageFormat.format("[{0}-{1})", sdf.format(s), sdfend.format(e));
			}
			else
				labelArray[i] = sdf.format(s);

			//incremento actual
			actualStart.add(field, increment);
		}

		String color = Colors.CODE_OK+","+Colors.CODE_FAULT_APPLICATIVO+","+Colors.CODE_ERROR;
		
		sdf = new SimpleDateFormat("dd MMMMM yyyy HH:mm", Locale.ITALIAN);
		StringBuilder sb = new StringBuilder();
		sb.append("<chart pallete='3' caption='");
		sb.append(this.getCaption());
		sb.append("' subCaption='");
		sb.append(this.getSubCaption());
		sb.append("' ");
		sb.append("decimals='0' decimalSeparator=',' thousandSeparator='.' useRoundEdges='1' showSum='1' formatNumber='1' formatNumberScale='0' paletteColors='"+color+"' labelDisplay='Rotate' slantLabels='1'>");

		sb.append("<categories>");
		for (int i=0;i<this.offset;i++){
			sb.append("<category label='");
			sb.append(labelArray[i]);
			sb.append("' />");
		}

		sb.append("</categories>");

		int showValues = 0; // non potendo estendere il grafico, non si fanno vedere i numeri, altrimenti si sovrappongono.

		sb.append("<dataset seriesName='" + CostantiGrafici.OK_LABEL + "' showValues='"+showValues+"' showLabels='1'>");
		for (int i=0;i<this.offset;i++){
			sb.append("<set value='");
			sb.append(ok_array[i]);
			sb.append("' />");
		}
		sb.append("</dataset>");

		sb.append("<dataset seriesName='" + CostantiGrafici.FAULT_LABEL + "' showValues='"+showValues+"' showLabels='1'>");
		for (int i=0;i<this.offset;i++){			 
			sb.append("	<set value='");
			sb.append(fault_array[i]);
			sb.append("' />");
		}
		sb.append("</dataset>");

		sb.append("<dataset seriesName='" + CostantiGrafici.ERRORE_LABEL + "' showValues='"+showValues+"' showLabels='1'>");
		for (int i=0;i<this.offset;i++){			 
			sb.append("	<set value='");
			sb.append(error_array[i]);
			sb.append("' />");
		}
		sb.append("</dataset>");
		
		sb.append("</chart> ");
		return sb.toString();
	}

	public String getJson(){
		JSONObject grafico = new JSONObject();

		if(!CostantiReport.PERIODO_NOT_SET.equals(this.periodo)){

			grafico.put(CostantiGrafici.USA_COLORI_AUTOMATICI_KEY, false);
			grafico.put(CostantiGrafici.TITOLO_KEY, this.getCaption());
			grafico.put(CostantiGrafici.SOTTOTITOLO_KEY, this.getSubCaption());
			grafico.put(CostantiGrafici.Y_AXIS_LABEL_KEY, CostantiGrafici.NUMERO_ESITI_LABEL);
			grafico.put(CostantiGrafici.MOSTRA_LEGENDA_KEY, true);

			JSONArray categorie = new JSONArray();
			JSONObject categoria = new JSONObject();
			categoria.put(CostantiGrafici.KEY_KEY , CostantiGrafici.OK_KEY);
			categoria.put(CostantiGrafici.LABEL_KEY , CostantiGrafici.OK_LABEL);
			categoria.put(CostantiGrafici.COLORE_KEY , Colors.CSS_COLOR_OK);
			categorie.add(categoria);

			JSONObject categoria2 = new JSONObject();
			categoria2.put(CostantiGrafici.KEY_KEY , CostantiGrafici.FAULT_KEY);
			categoria2.put(CostantiGrafici.LABEL_KEY , CostantiGrafici.FAULT_LABEL);
			categoria2.put(CostantiGrafici.COLORE_KEY , Colors.CSS_COLOR_FAULT_APPLICATIVO);
			categorie.add(categoria2);

			JSONObject categoria3 = new JSONObject();
			categoria3.put(CostantiGrafici.KEY_KEY , CostantiGrafici.ERRORE_KEY);
			categoria3.put(CostantiGrafici.LABEL_KEY , CostantiGrafici.ERRORE_LABEL);
			categoria3.put(CostantiGrafici.COLORE_KEY , Colors.CSS_COLOR_ERROR);
			categorie.add(categoria3);

			// Inserisco le catergorie del grafico
			grafico.put(CostantiGrafici.CATEGORIE_KEY, categorie);

			try{
				JSONArray dati = new JSONArray();

				int[] error_array = new int[this.offset];
				int[] fault_array = new int[this.offset];
				int[] ok_array = new int[this.offset];
				String[] labelArray = new String[this.offset];

				SimpleDateFormat sdf = null;
				SimpleDateFormat sdfend = null;
				Calendar w_end = null;
				Calendar w_start = null;
				Calendar actualStart = null;

				int field = Calendar.DATE;
				int increment = 0;

				w_end=Calendar.getInstance();
				w_end.setTime(this.maxDate);


				w_start = (Calendar)w_end.clone();
				w_start.setTime(this.minDate);

				boolean clearEndMs = false;

				if(CostantiReport.ULTIMO_ANNO.equals(this.periodo)){
					sdf = new SimpleDateFormat(CostantiGrafici.PATTERN_MMM_YY, Locale.ITALIAN);

					field = Calendar.MONTH;
					increment = 1;
					//siccome incremento i mesi devo impostare l'actual come 00:00 del primo del mese
					actualStart = (Calendar) w_start.clone();
					actualStart.set(Calendar.DAY_OF_MONTH, actualStart.getActualMinimum(Calendar.DAY_OF_MONTH));
					actualStart.set(Calendar.HOUR_OF_DAY,actualStart.getActualMinimum(Calendar.HOUR_OF_DAY));
					actualStart.set(Calendar.MINUTE,actualStart.getActualMinimum(Calendar.MINUTE));
					actualStart.clear(Calendar.SECOND);
					actualStart.clear(Calendar.MILLISECOND);


				}else if(CostantiReport.ULTIMI_30_GIORNI.equals(this.periodo)){
					sdf = new SimpleDateFormat(CostantiGrafici.PATTERN_DD_MM, Locale.ITALIAN);
					sdfend = new SimpleDateFormat(CostantiGrafici.PATTERN_DD_MM, Locale.ITALIAN);
					field = Calendar.WEEK_OF_YEAR;
					increment = 1;

					actualStart = (Calendar) w_start.clone();
					actualStart.set(Calendar.HOUR_OF_DAY,actualStart.getActualMinimum(Calendar.HOUR_OF_DAY));
					actualStart.set(Calendar.MINUTE,actualStart.getActualMinimum(Calendar.MINUTE));
					actualStart.clear(Calendar.SECOND);
					actualStart.clear(Calendar.MILLISECOND);

				}else if(CostantiReport.ULTIMI_7_GIORNI.equals(this.periodo)){
					sdf = new SimpleDateFormat(CostantiGrafici.PATTERN_EEE_DD_MM, Locale.ITALIAN);
					field = Calendar.DATE;
					increment = 1;

					actualStart = (Calendar) w_start.clone();
					actualStart.set(Calendar.HOUR_OF_DAY,actualStart.getActualMinimum(Calendar.HOUR_OF_DAY));
					actualStart.set(Calendar.MINUTE,actualStart.getActualMinimum(Calendar.MINUTE));
					actualStart.clear(Calendar.SECOND);
					actualStart.clear(Calendar.MILLISECOND);

				}else if(CostantiReport.ULTIME_24_ORE.equals(this.periodo)){
					sdf = new SimpleDateFormat(CostantiGrafici.PATTERN_HH, Locale.ITALIAN);
					sdfend = new SimpleDateFormat(CostantiGrafici.PATTERN_HH, Locale.ITALIAN);
					field = Calendar.HOUR_OF_DAY;
					increment = 1;

					actualStart = (Calendar) w_start.clone();
					actualStart.set(Calendar.MINUTE,actualStart.getActualMinimum(Calendar.MINUTE));
					actualStart.clear(Calendar.SECOND);
					actualStart.clear(Calendar.MILLISECOND);
					clearEndMs = true;
				}

				for (int i=0;i<this.offset;i++){
					Calendar actualEnd = (Calendar)actualStart.clone();
					actualEnd.add(field, increment);

					actualEnd.add(Calendar.MILLISECOND, -1);

					Date s = actualStart.getTime();
					Date e = actualEnd.getTime();

					ResLive r = null;
					try{
						r = this.report.getEsiti(this.getPermessiUtenteOperatore(), s, e, this.periodo, this.esitoContesto, this.protocollo);
					} catch (CoreException er) {
						MessageUtils.addErrorMsg("Si e' verificato un errore durante il recupero degli esiti");
						SummaryBean.log.error(er.getMessage(),er);
						return null;
					}

					error_array[i] = r.getEsitoKo().intValue();
					fault_array[i] = r.getEsitoFault().intValue();
					ok_array[i] = r.getEsitoOk().intValue();
					if(sdfend!=null){
						if(clearEndMs){
							actualEnd.add(Calendar.MILLISECOND, 1);
							e = actualEnd.getTime();
						}
						labelArray[i] = MessageFormat.format("[{0}-{1})", sdf.format(s), sdfend.format(e));
					}
					else
						labelArray[i] = sdf.format(s);

					//incremento actual
					actualStart.add(field, increment);

					JSONObject bar = new JSONObject();

					bar.put(CostantiGrafici.DATA_KEY, labelArray[i]);
					bar.put(CostantiGrafici.OK_KEY, ok_array[i]);
					bar.put(CostantiGrafici.FAULT_KEY, fault_array[i]);
					bar.put(CostantiGrafici.ERRORE_KEY, error_array[i]);
					bar.put(CostantiGrafici.OK_KEY + CostantiGrafici.TOOLTIP_SUFFIX, CostantiGrafici.OK_LABEL + ", "+ labelArray[i] + ", " + ok_array[i]);
					bar.put(CostantiGrafici.FAULT_KEY + CostantiGrafici.TOOLTIP_SUFFIX, CostantiGrafici.FAULT_LABEL + ", "+ labelArray[i] + ", " + fault_array[i]);
					bar.put(CostantiGrafici.ERRORE_KEY + CostantiGrafici.TOOLTIP_SUFFIX, CostantiGrafici.ERRORE_LABEL + ", "+ labelArray[i] + ", " + error_array[i]);

					dati.add(bar);
				}

				grafico.put(CostantiGrafici.DATI_KEY, dati);
				grafico.put(CostantiGrafici.X_AXIS_LABEL_DIREZIONE_KEY, dati.size() > 12 ? CostantiGrafici.DIREZIONE_LABEL_OBLIQUO : CostantiGrafici.DIREZIONE_LABEL_ORIZZONTALE);
				grafico.put(CostantiGrafici.X_AXIS_GRID_LINES_KEY,true);
			}catch(Exception e){
				SummaryBean.log.error("Errore durante la generazione del json: " + e.getMessage(),e);
				return "";
			}
			//String json = grafico != null ?  grafico.toString() : "";
			//log.debug(json); 
			
			return grafico.toString();	
		}

		return ""; 
	}

	public String getData(){
		if(isUseGraficiSVG())
			return this.getJson();

		return this.getXml();
	}


	@SuppressWarnings("deprecation")
	private PermessiUtenteOperatore getPermessiUtenteOperatore() throws CoreException{
		UserDetailsBean loggedUser = Utility.getLoggedUser();
		User u =  Utility.getLoggedUtente();
		
		int foundSoggetti = u.getSoggetti() != null ? u.getSoggetti().size() : 0;
		int foundServizi = u.getServizi() != null ? u.getServizi().size() : 0;
		
		if((foundServizi + foundSoggetti) == 1) {
			IDServizio idServizio =  null;
			if(foundServizi == 1) {
				idServizio = u.getServizi().get(0);
			} else {
				idServizio = new IDServizio();
				idServizio.setSoggettoErogatore(u.getSoggetti().get(0));
			}
			this.soggettoLocale = Utility.convertToSoggettoServizio(idServizio);
		}

		String tipoSoggettoLocale = null;
		String nomeSoggettoLocale = null;
		String tipoServizio = null;
		String nomeServizio = null;
		Integer versioneServizio = null;
		if(this.soggettoLocale!=null && !StringUtils.isEmpty(this.soggettoLocale) && !"--".equals(this.soggettoLocale)){
			IDServizio idServizio = Utility.parseSoggettoServizio(this.soggettoLocale);
			tipoSoggettoLocale = idServizio.getSoggettoErogatore().getTipo();
			nomeSoggettoLocale = idServizio.getSoggettoErogatore().getNome();
			versioneServizio = idServizio.getVersione();
			tipoServizio = idServizio.getTipo(); // possono essere null
			nomeServizio = idServizio.getNome(); // possono essere null
		}
		
		return PermessiUtenteOperatore.getPermessiUtenteOperatore(loggedUser, 
				tipoSoggettoLocale,nomeSoggettoLocale, 
				tipoServizio, nomeServizio,versioneServizio);
		
	}


	private String getSubCaption() {
		SimpleDateFormat sdf = null;
		if(!CostantiReport.ULTIME_24_ORE.equals(this.periodo)){
			sdf = new SimpleDateFormat(CostantiGrafici.PATTERN_DD_MMMMM_YYYY, Locale.ITALIAN);
		}else{
			sdf = new SimpleDateFormat(CostantiGrafici.PATTERN_DD_MMMMM_YYYY_ORE_HH_MM, Locale.ITALIAN);
		}
		String res = MessageFormat.format(CostantiGrafici.DAL_AL_PATTERN, sdf.format(this.getMinDate()), sdf.format(this.getMaxDate())); 

		return StringEscapeUtils.escapeXml(res);
	}

	private Date getMinDate() {
		return this.minDate;
	}


	private Date getMaxDate() {
		return this.maxDate;
	}

	
	private String getCaption() {
		return StringEscapeUtils.escapeXml(CostantiGrafici.DISTRIBUZIONE_ESITO_DEI_MESSAGGI_NELL_ULTIMO_PERIODO); 
	}


	public String getDataLive(){
		if(isUseGraficiSVG())
			return this.getJsonLive();

		return this.getXmlLive();
	}

	/**
	 * Live graph
	 * @return Xml del grafico degli esiti live.
	 */
	public String getXmlLive(){

		//		if(SummaryBean.PERIODO_NOT_SET.equals(this.periodo))
		//			return "<chart></chart>";

		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");

		//se idPorta e' null allora recupera tutti gli esiti 
		ResLive esiti=null;
		try {
			esiti = this.transazioniService.getEsitiInfoLive(getPermessiUtenteOperatore(),this.lastRequest);
		} catch (CoreException e) {
			MessageUtils.addErrorMsg("Si e' verificato un errore durante il recupero degli esiti");
			SummaryBean.log.error(e.getMessage(), e);
			return null;
		}

		//aggiorno lastRequest
		this.lastRequest = esiti.getRisultato(); 

		synchronized (this.dataLive) {
			//aggiungo elemento
			this.dataLive.add(esiti);
			//se ho superato il massimo numero di elementi ammessi
			//rimuovo il primo
			if(this.dataLive.size() > this.maxDataStored)
				this.dataLive.remove(0);
		}

		String url = FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath()+"/pages/list/transazioni.jsf";

		String color = Colors.CODE_OK+","+Colors.CODE_FAULT_APPLICATIVO+","+Colors.CODE_ERROR;
		
		StringBuilder sb = new StringBuilder();
		sb.append( "<chart clickURL='");
		sb.append(url);
		sb.append("' caption='Esito Transazioni' animation='0' rotateLabels='1' slantLabels='1' xAxisName='Tempo' yAxisName='Totale Transazioni' showValues='0' numberPrefix='#' paletteColors='"+color+"' >");

		StringBuffer categories = new StringBuffer();
		StringBuffer ok = new StringBuffer();
		StringBuffer fault = new StringBuffer();
		StringBuffer ko = new StringBuffer();

		for (ResLive res : this.dataLive) {
			categories.append("<category label='"+formatter.format(res.getRisultato())+"' />");
			ok.append("<set value='"+res.getEsitoOk()+"' />");
			fault.append("<set value='"+res.getEsitoFault()+"' />");
			ko.append("<set value='"+res.getEsitoKo()+"' />");
		}

		//categories
		sb.append("<categories>");
		sb.append(categories.toString());
		sb.append("</categories>");
		//dataset ok
		sb.append("<dataset seriesName='Ok'>");
		sb.append(ok.toString());
		sb.append("</dataset>");
		//dataset fault
		sb.append("<dataset seriesName='Fault Applicativo'>");
		sb.append(fault.toString());
		sb.append("</dataset>");
		//dataset error
		sb.append("<dataset seriesName='Fallite'>");
		sb.append(ko.toString());
		sb.append("</dataset>");

		//fine chart
		sb.append("</chart>");

		return sb.toString();
	}

	public String getJsonLive(){
		JSONObject grafico = new JSONObject();

		try{
			grafico.put(CostantiGrafici.USA_COLORI_AUTOMATICI_KEY, false);
			grafico.put(CostantiGrafici.TITOLO_KEY, CostantiGrafici.ESITO_TRANSAZIONI_LABEL);
			grafico.put(CostantiGrafici.SOTTOTITOLO_KEY, "");
			grafico.put(CostantiGrafici.Y_AXIS_LABEL_KEY, CostantiGrafici.TOTALE_TRANSAZIONI_LABEL);
			grafico.put(CostantiGrafici.MOSTRA_LEGENDA_KEY, true);

			JSONArray categorie = new JSONArray();
			JSONObject categoria = new JSONObject();
			categoria.put(CostantiGrafici.KEY_KEY , CostantiGrafici.OK_KEY);
			categoria.put(CostantiGrafici.LABEL_KEY , CostantiGrafici.OK_LABEL);
			categoria.put(CostantiGrafici.COLORE_KEY , Colors.CSS_COLOR_OK);
			categorie.add(categoria);

			JSONObject categoria2 = new JSONObject();
			categoria2.put(CostantiGrafici.KEY_KEY , CostantiGrafici.FAULT_KEY);
			categoria2.put(CostantiGrafici.LABEL_KEY , CostantiGrafici.FAULT_LABEL);
			categoria2.put(CostantiGrafici.COLORE_KEY , Colors.CSS_COLOR_FAULT_APPLICATIVO);
			categorie.add(categoria2);

			JSONObject categoria3 = new JSONObject();
			categoria3.put(CostantiGrafici.KEY_KEY , CostantiGrafici.ERRORE_KEY);
			categoria3.put(CostantiGrafici.LABEL_KEY , CostantiGrafici.ERRORE_LABEL);
			categoria3.put(CostantiGrafici.COLORE_KEY , Colors.CSS_COLOR_ERROR);
			categorie.add(categoria3);

			// Inserisco le catergorie del grafico
			grafico.put(CostantiGrafici.CATEGORIE_KEY, categorie);
			
			SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");

			//se idPorta e' null allora recupera tutti gli esiti 
			ResLive esiti=null;
			try {
				esiti = this.transazioniService.getEsitiInfoLive(this.getPermessiUtenteOperatore(),this.lastRequest);
			} catch (CoreException e) {
				MessageUtils.addErrorMsg("Si e' verificato un errore durante il recupero degli esiti");
				SummaryBean.log.error(e.getMessage(),e);
				return null;
			}

			//aggiorno lastRequest
			this.lastRequest = esiti.getRisultato(); 

			synchronized (this.dataLive) {
				//aggiungo elemento
				this.dataLive.add(esiti);
				//se ho superato il massimo numero di elementi ammessi
				//rimuovo il primo
				if(this.dataLive.size() > this.maxDataStored)
					this.dataLive.remove(0);
			}

			JSONArray dati = new JSONArray();
			
			for (ResLive res : this.dataLive) {
				
				String data = formatter.format(res.getRisultato());
				String nOk = res.getEsitoOk()+"";
				String nFault = res.getEsitoFault()+"";
				String nKo = res.getEsitoKo()+"";
				JSONObject bar = new JSONObject();

				bar.put(CostantiGrafici.DATA_KEY, data);
				bar.put(CostantiGrafici.OK_KEY, nOk);
				bar.put(CostantiGrafici.FAULT_KEY, nFault);
				bar.put(CostantiGrafici.ERRORE_KEY, nKo);
				bar.put(CostantiGrafici.OK_KEY + CostantiGrafici.TOOLTIP_SUFFIX, CostantiGrafici.OK_LABEL + ", "+ data + ", " + nOk);
				bar.put(CostantiGrafici.FAULT_KEY + CostantiGrafici.TOOLTIP_SUFFIX, CostantiGrafici.FAULT_LABEL + ", "+ data + ", " + nFault);
				bar.put(CostantiGrafici.ERRORE_KEY + CostantiGrafici.TOOLTIP_SUFFIX, CostantiGrafici.ERRORE_LABEL + ", "+ data + ", " + nKo);

				dati.add(bar);
			}

			grafico.put(CostantiGrafici.DATI_KEY, dati);
			grafico.put(CostantiGrafici.X_AXIS_LABEL_DIREZIONE_KEY, dati.size() > 12 ? CostantiGrafici.DIREZIONE_LABEL_OBLIQUO : CostantiGrafici.DIREZIONE_LABEL_ORIZZONTALE);
			grafico.put(CostantiGrafici.X_AXIS_GRID_LINES_KEY,true);
		}catch(Exception e){
			SummaryBean.log.error("Errore durante la generazione del json live: " + e.getMessage(),e);
			return "";
		}
		return grafico.toString();	
	}

	public String getPeriodo() {
		return this.periodo;
	}

	public void setPeriodo(String periodo) {

		this.periodo = periodo;

		if(CostantiReport.ULTIMO_ANNO.equals(periodo)){
			//calcolo 12 mesi dal mese attuale
			Calendar max = Calendar.getInstance();
			this.maxDate=max.getTime();


			max.add(Calendar.MONTH, -11);

			Calendar min = (Calendar) max.clone();
			min.set(Calendar.DAY_OF_MONTH, min.getActualMinimum(Calendar.DAY_OF_MONTH));
			min.set(Calendar.HOUR_OF_DAY,min.getActualMinimum(Calendar.HOUR_OF_DAY));
			min.set(Calendar.MINUTE,min.getActualMinimum(Calendar.MINUTE));
			min.clear(Calendar.SECOND);
			min.clear(Calendar.MILLISECOND);


			this.minDate=min.getTime();

			this.offset=12;
		}
		else if(CostantiReport.ULTIMI_30_GIORNI.equals(periodo)){
			Calendar max = Calendar.getInstance();
			this.maxDate=max.getTime();

			max.add(Calendar.DATE, -30);
			this.minDate=max.getTime();
			this.offset=5;
		}
		else if(CostantiReport.ULTIMI_7_GIORNI.equals(periodo)){
			Calendar max = Calendar.getInstance();
			this.maxDate=max.getTime();

			max.add(Calendar.DATE, -6);
			this.minDate=max.getTime();
			this.offset=7;
		}else if(CostantiReport.ULTIME_24_ORE.equals(periodo)){
			Calendar max = Calendar.getInstance();
			max.add(Calendar.HOUR_OF_DAY, 1);
			this.maxDate=max.getTime();

			max.add(Calendar.HOUR_OF_DAY, -24);
			this.minDate=max.getTime();
			this.offset=24;
		}

		this.salvaStatoPeriodo(this.periodo);
	}
	public String getSoggettoLocale() {
		return this.soggettoLocale;
	}
	public void setSoggettoLocale(String soggettoLocale) {
		this.soggettoLocale = soggettoLocale;

		if(StringUtils.isEmpty(soggettoLocale) || "--".equals(soggettoLocale)){
			this.soggettoLocale = null;
		}
	}

	public List<Soggetto> soggettiAutoComplete(Object val){

		List<Soggetto> list = null;
		Soggetto s = new Soggetto();
		s.setNomeSoggetto("--");
		String tipoProtocollo =  this.getProtocollo();

		if(val==null || StringUtils.isEmpty((String)val))
			list = new ArrayList<Soggetto>();
		else{

			list = this.dynamicUtilsService.soggettiAutoComplete(tipoProtocollo,(String)val);
		}

		list.add(0,s);
		return list;

	}
	
	public List<SelectItem> getTipiNomiSoggettiAssociati() throws Exception {
		return _getTipiNomiSoggettiAssociati(false);
	}

	public List<SelectItem> _getTipiNomiSoggettiAssociati(boolean soloOperativi) throws Exception {
		if(this.soggettiAssociati == null)
			this.soggettiAssociati = new ArrayList<SelectItem>();
		
		if(!this.soggettiAssociatiSelectItemsWidthCheck){
			this.soggettiAssociati = new ArrayList<SelectItem>();

			UserDetailsBean loggedUser = Utility.getLoggedUser();
			if(loggedUser!=null){
				List<IDSoggetto> lst = new ArrayList<IDSoggetto>();
				String tipoProtocollo = this.getProtocollo();
				
				if(tipoProtocollo == null) {
					lst = loggedUser.getUtenteSoggettoList();
				} else {
					// se ho selezionato un protocollo devo filtrare per protocollo
					List<IDSoggetto> tipiNomiSoggettiAssociati = loggedUser.getUtenteSoggettoList();
					List<String> lstTmp = new ArrayList<String>();
					if(tipiNomiSoggettiAssociati !=null && tipiNomiSoggettiAssociati.size() > 0)

						for (IDSoggetto utenteSoggetto : tipiNomiSoggettiAssociati) {
							if(this.dynamicUtils.isTipoSoggettoCompatibileConProtocollo(utenteSoggetto.getTipo(), tipoProtocollo)){
								String tipoNome = utenteSoggetto.getTipo() + "/" + utenteSoggetto.getNome();
								boolean add = true;
								if(soloOperativi) {
									String nomePddFromSoggetto = this.dynamicUtils.getServerFromSoggetto(utenteSoggetto.getTipo(), utenteSoggetto.getNome());
									add = this.dynamicUtils.checkTipoPdd(nomePddFromSoggetto, TipoPdD.OPERATIVO);
								}
								if(lstTmp.contains(tipoNome)==false && add){
									lstTmp.add(tipoNome);
									lst.add(utenteSoggetto);
								}
							}
						}
				}

				for (IDSoggetto idSoggetto : lst) {
					String value = idSoggetto.getTipo() + "/" + idSoggetto.getNome();
					String label = tipoProtocollo != null ? NamingUtils.getLabelSoggetto(tipoProtocollo,idSoggetto) : NamingUtils.getLabelSoggetto(idSoggetto);
					this.soggettiAssociati.add(new SelectItem(value,label));
				}
				Integer lunghezzaSelectList = this.dynamicUtils.getLunghezzaSelectList(this.soggettiAssociati);
				this.soggettiAssociatiSelectItemsWidth = Math.max(this.soggettiAssociatiSelectItemsWidth,  lunghezzaSelectList);
			}
		}

		return this.soggettiAssociati;
	}
	
	@SuppressWarnings("deprecation")
	public List<SelectItem> soggettiServiziAutoComplete(Object val) throws Exception {
		this.soggettiSelectItemsWidth = 0;
		List<SelectItem> list = new ArrayList<SelectItem>();
		list.add(new SelectItem("--","--"));
		
		String tipoProtocollo = this.getProtocollo();
		
		if(val!=null && !StringUtils.isEmpty((String)val)){
			
			
			Map<String,String> mapInternal = new HashMap<String,String>();
			
			List<Soggetto> listSoggetti = this.dynamicUtilsService.soggettiAutoComplete(tipoProtocollo,(String)val);
			if(listSoggetti!=null && listSoggetti.size()>0){
				for (Soggetto soggetto : listSoggetti) {
					IDServizio idServizio = new IDServizio();
					IDSoggetto idSoggetto = new IDSoggetto(soggetto.getTipoSoggetto(), soggetto.getNomeSoggetto());
					idServizio.setSoggettoErogatore(idSoggetto);
					String label = tipoProtocollo != null ? NamingUtils.getLabelSoggetto(tipoProtocollo, idSoggetto) : NamingUtils.getLabelSoggetto(idSoggetto);
					mapInternal.put(ParseUtility.convertToSoggettoServizio(idServizio), label);
				}
			}
			
			List<AccordoServizioParteSpecifica> listServizi = this.dynamicUtilsService.getServizi(tipoProtocollo, null, null, null,(String)val,false);
			if(listServizi!=null && listServizi.size()>0){
				for (AccordoServizioParteSpecifica asps : listServizi) {
					IDServizio idServizio = new IDServizio();
					idServizio.setSoggettoErogatore(new IDSoggetto(asps.getIdErogatore().getTipo(), asps.getIdErogatore().getNome()));
					idServizio.setTipo(asps.getTipo());
					idServizio.setNome(asps.getNome());
					idServizio.setVersione(asps.getVersione());
					String label = tipoProtocollo != null ?  NamingUtils.getLabelAccordoServizioParteSpecifica(tipoProtocollo, idServizio) : NamingUtils.getLabelAccordoServizioParteSpecifica(idServizio);
					mapInternal.put(ParseUtility.convertToSoggettoServizio(idServizio), label);
				}
			}
			
			if(mapInternal.size()>0){
				//convert map to a List
				List<Entry<String, String>> tmpList = new LinkedList<Map.Entry<String, String>>(mapInternal.entrySet());

				//sorting the list with a comparator
				Collections.sort(tmpList, new Comparator<Entry<String, String>>() {
					@Override
					public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {
						return (o1.getValue()).compareTo(o2.getValue());
					}
				});

				//convert sortedMap back to Map
				Map<String, String> sortedMap = new LinkedHashMap<String, String>();
				for (Entry<String, String> entry : tmpList) {
					sortedMap.put(entry.getKey(), entry.getValue());
				}
				
				for (String key : sortedMap.keySet()) {
					list.add(new SelectItem(key, sortedMap.get(key)));
				}
			}
		}
		
		Integer lunghezzaSelectList = this.dynamicUtils.getLunghezzaSelectList(list);
		this.soggettiSelectItemsWidth = Math.max(this.soggettiSelectItemsWidth,  lunghezzaSelectList);
		
		return list;

	}

	public String getIntervalloRefresh() {
		return this.intervalloRefresh;
	}

	public void setIntervalloRefresh(String intervalloRefresh) {
		this.intervalloRefresh = intervalloRefresh;
	}

	public void salvaStatoPeriodo(String statoPeriodo){
		if(this.funzionalitaStatisticaAbilitata){
			String p = statoPeriodo;
// FIX OPPT-638 per poter salvare anche lo stato in cui non si ottiene nessuna informazione dopo il login
//			if(p != null && p.equals(CostantiReport.PERIODO_NOT_SET))
//				p = null;

			if(p!=null){
				p = "{" + p + "}"; // trasformo in json
			}

			Stato state = this.userService.getTableState(SummaryBean.STATO_PERIODO,Utility.getLoggedUtente());
			state.setStato(p);
			this.userService.saveTableState(SummaryBean.STATO_PERIODO,Utility.getLoggedUtente(), state);
		}
	}

	public String leggiStatoPeriodo(){
		Stato state = this.userService.getTableState(SummaryBean.STATO_PERIODO,Utility.getLoggedUtente());
		String statoPeriodo = state.getStato();
		if(statoPeriodo!=null){
			if(statoPeriodo.startsWith("{")){
				statoPeriodo = statoPeriodo.substring(1);
			}
			if(statoPeriodo.endsWith("}")){
				statoPeriodo = statoPeriodo.substring(0, (statoPeriodo.length()-1) );
			}
		}
		return  statoPeriodo;
	}

	public boolean isShowEsitiContesto(){
		return this.getEsitiContesto().size()>2;
	}

	public List<SelectItem> getEsitiContesto() {
		ArrayList<SelectItem> list = new ArrayList<SelectItem>();

		list.add(new SelectItem(EsitoUtils.ALL_VALUE_AS_STRING));

		try{

			List<String> esiti = this.esitiProperties.getEsitiTransactionContextCodeOrderLabel();
			for (String esito : esiti) {

				SelectItem si = new SelectItem(esito);

				list.add(si);
			}

		}catch(Exception e){
			throw new RuntimeException(e.getMessage(),e);
		}

		return list;
	}

	public boolean isUseGraficiSVG() {
		BrowserInfo browserInfo = ApplicationBean.getInstance().getBrowserInfo();
		this.useGraficiSVG =ApplicationBean.getInstance().isGraficiSvgEnabled() && !BrowserFilter.disabilitaGraficiSVG(browserInfo);

		LoggerManager.getPddMonitorCoreLogger().trace("Usa grafici SVG ["+this.useGraficiSVG+"]");


		return this.useGraficiSVG;
	}

	public void setUseGraficiSVG(boolean useGraficiSVG) {
		this.useGraficiSVG = useGraficiSVG;
	}
	
	public String getSoggettiAssociatiSelectItemsWidth() throws Exception{
		this.soggettiAssociatiSelectItemsWidthCheck = false;
		getTipiNomiSoggettiAssociati();
		this.soggettiAssociatiSelectItemsWidthCheck = true;
		return checkWidthLimits(this.soggettiAssociatiSelectItemsWidth).toString();
	}
	
	public Integer getSoggettiAssociatiSelectItemsWidthAsInteger() throws Exception{
		return checkWidthLimits(this.soggettiAssociatiSelectItemsWidth);
	}
	
	public boolean isSoggettiAssociatiSelectItemsWidthCheck() {
		return this.soggettiAssociatiSelectItemsWidthCheck;
	}

	public void setSoggettiAssociatiSelectItemsWidthCheck(boolean soggettiAssociatiSelectItemsWidthCheck) {
		this.soggettiAssociatiSelectItemsWidthCheck = soggettiAssociatiSelectItemsWidthCheck;
	}
	
	public String getSoggettiSelectItemsWidth() throws Exception{
		return checkWidthLimits(this.soggettiSelectItemsWidth).toString();
	}
	
	public Integer getSoggettiSelectItemsWidthAsInteger() throws Exception{
		return checkWidthLimits(this.soggettiSelectItemsWidth);
	}
	
	public boolean isSoggettiSelectItemsWidthCheck() {
		return this.soggettiSelectItemsWidthCheck;
	}

	public void setSoggettiSelectItemsWidthCheck(boolean soggettiSelectItemsWidthCheck) {
		this.soggettiSelectItemsWidthCheck = soggettiSelectItemsWidthCheck;
	}
	
	public Integer checkWidthLimits(Integer value){
		// valore deve essere compreso minore del max ma almeno quanto la default
		Integer toRet = Math.max(this.defaultSelectItemsWidth, value);

		toRet = Math.min(toRet, this.maxSelectItemsWidth);

		return toRet;
	}
	
	/**
	 * I nomi spcoop dei soggetti associati all'utente loggato
	 */
	public List<Soggetto> getSoggettiGestione() {
		User u = Utility.getLoggedUtente();
		return Utility.getSoggettiGestione(u,this.soggettoLocale);
	}
	
	public String getProtocollo() {
		if(!Utility.getLoginBean().getModalita().equals(Costanti.VALUE_PARAMETRO_MODALITA_ALL)) {
			this.setProtocollo(Utility.getLoginBean().getModalita()); 
		}
		
		return this.protocollo;
	}
	
	public void setProtocollo(String protocollo) {
		this.protocollo = protocollo;

		if (StringUtils.isEmpty(protocollo)
				|| "*".equals(protocollo))
			this.protocollo = null;
	}

	public List<SelectItem> getProtocolli() throws Exception {
		//		if(this.protocolli == null)
		this.protocolli = new ArrayList<SelectItem>();
//		this.protocolli.add(new SelectItem("*",AllConverter.ALL_STRING));
		try {
			List<Soggetto> listaSoggettiGestione = this.getSoggettiGestione();
			ProtocolFactoryManager pfManager = org.openspcoop2.protocol.engine.ProtocolFactoryManager.getInstance();
			MapReader<String,IProtocolFactory<?>> protocolFactories = pfManager.getProtocolFactories();	

			List<String> listaNomiProtocolli = Utility.getListaProtocolli(listaSoggettiGestione, pfManager, protocolFactories);

			for (String protocolKey : listaNomiProtocolli) {
				IProtocolFactory<?> protocollo = protocolFactories.get(protocolKey);
				this.protocolli.add(new SelectItem(protocollo.getProtocol(),NamingUtils.getLabelProtocollo(protocollo.getProtocol())));
			}

		} catch (ProtocolException e) {
			SummaryBean.log.error("Si e' verificato un errore durante il caricamento della lista protocolli: " + e.getMessage(), e);
		}  


		return this.protocolli;
	}


	public boolean isShowListaProtocolli(){
		try {
			ProtocolFactoryManager pfManager = org.openspcoop2.protocol.engine.ProtocolFactoryManager.getInstance();
			MapReader<String, IProtocolFactory<?>> protocolFactories = pfManager.getProtocolFactories();	
			int numeroProtocolli = protocolFactories.size();

			// se c'e' installato un solo protocollo non visualizzo la select List.
			if(numeroProtocolli == 1)
				return false;
			
			
			User utente = Utility.getLoggedUtente();
			
			// utente ha selezionato una modalita'
			if(utente.getProtocolloSelezionatoPddMonitor()!=null) {
				return false;
			}
			
			
			if(utente.getProtocolliSupportati() ==null ||  utente.getProtocolliSupportati().size() <= 1) {
				return false;
			}

			List<Soggetto> listaSoggettiGestione = this.getSoggettiGestione();
			List<String> listaNomiProtocolli = Utility.getListaProtocolli(listaSoggettiGestione, pfManager, protocolFactories);

			numeroProtocolli = listaNomiProtocolli.size();

			// se c'e' installato un solo protocollo non visualizzo la select List.
			if(numeroProtocolli == 1)
				return false;

		} catch (ProtocolException e) {
			SummaryBean.log.error("Si e' verificato un errore durante il caricamento della lista protocolli: " + e.getMessage(), e);
		}  

		return true;
	}
	
	public boolean isSetFiltroProtocollo() {
		boolean setFilter = StringUtils.isNotEmpty(this.getProtocollo()) &&
				(this.isShowListaProtocolli() || !Utility.getLoginBean().getModalita().equals(Costanti.VALUE_PARAMETRO_MODALITA_ALL));
		
		return setFilter;
	}
	
	public void protocolloSelected(ActionEvent ae) {}
}
