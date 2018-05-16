package org.openspcoop2.web.monitor.transazioni.mbean;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.slf4j.Logger;

import org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazioneRisorsaContenuto;
import org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazioneStato;
import org.openspcoop2.core.commons.search.AccordoServizioParteSpecifica;
import org.openspcoop2.core.commons.search.IdAccordoServizioParteComune;
import org.openspcoop2.web.monitor.core.dao.ISearchFormService;
import org.openspcoop2.web.lib.users.dao.Stato;
import org.openspcoop2.web.monitor.core.bean.ApplicationBean;
import org.openspcoop2.web.monitor.core.bean.BaseSearchForm;
import org.openspcoop2.web.monitor.core.constants.NomiTabelle;
import org.openspcoop2.web.monitor.core.core.PddMonitorProperties;
import org.openspcoop2.web.monitor.core.core.Utility;
import org.openspcoop2.web.monitor.core.dao.IUserService;
import org.openspcoop2.web.monitor.core.dao.UserService;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;
import org.openspcoop2.web.monitor.core.mbean.DynamicPdDBean;
import org.openspcoop2.web.monitor.core.utils.MessageUtils;
import org.openspcoop2.web.monitor.transazioni.bean.TransazioneBean;
import org.openspcoop2.web.monitor.transazioni.bean.TransazioniSearchForm;
import org.openspcoop2.web.monitor.transazioni.dao.ITransazioniService;
import org.openspcoop2.web.monitor.transazioni.exporter.ColonnaExportManager;
import org.openspcoop2.web.monitor.transazioni.exporter.CostantiExport;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


public class TrasazioniBean extends DynamicPdDBean<TransazioneBean, String, ISearchFormService<TransazioneBean, String, TransazioniSearchForm>> {

	public static final String COLUMNS_VISIBILITY_STATO_TABELLE_KEY = "columnsVisibility";
	public static final String COLUMNS_ORDER_STATO_TABELLE_KEY = "columnsOrder";
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L; 
	private static Logger log =  LoggerManager.getPddMonitorCoreLogger();
	private List<SelectItem> stati = new ArrayList<SelectItem>();
	private List<SelectItem> risorse = new ArrayList<SelectItem>();

	private List<String> esportazioniSelezionate = new ArrayList<String>();

	private String tableState = "";

	private transient IUserService userService = null;

	private boolean visualizzaIdCluster = false;
	private boolean exportCsvAbilitato = false;
	private boolean exportCsvCompletato = false;
	private boolean visualizzaDataAccettazione = false;
	

	private ApplicationBean applicationBean = null;

	public TrasazioniBean(){
		super();

		try{
			PddMonitorProperties pddMonitorProperties = PddMonitorProperties.getInstance(TrasazioniBean.log);
			List<String> pddMonitorare = pddMonitorProperties.getListaPdDMonitorate_StatusPdD();
			this.setVisualizzaIdCluster(pddMonitorare!=null && pddMonitorare.size()>1);
			this.visualizzaDataAccettazione = pddMonitorProperties.isAttivoTransazioniDataAccettazione();
			
			this.applicationBean = new ApplicationBean();
			this.applicationBean.setLoginBean(Utility.getLoginBean()); 
			
		}catch(Exception e){
			log.error(e.getMessage(), e);
		}

		try{
			this.userService = new UserService();
		}catch(Exception e){
			log.error(e.getMessage(), e);
		}
	}

	@Override
	public void setSearch(BaseSearchForm searc) {
		this.search = searc;

		if (this.search instanceof TransazioniSearchForm)
			((TransazioniSearchForm) this.search)
			.setTransazioniService(((ITransazioniService)this.service));
	}

	public List<SelectItem> getStati() {

		try {
			this.stati = new ArrayList<SelectItem>();
			if(this.search==null || this.search.getNomeServizio()==null){
				return this.stati;
			}

			//			String uri = this.search.getNomeServizio().split(" \\(")[1]
			//					.replace(")", "");
			//			IDAccordo idAccordo = IDAccordoFactory.getInstance()
			//					.getIDAccordoFromUri(uri);
			String nomeServizio = this.search.getNomeServizio().split(" \\(")[0];

			//			String nomeServizio = this.getNomeServizio().split(" \\(")[0];
			String tipoServizio = null;
			// showTipoSoggetto e' true allora la label e' di tipo TIPO/NOME
			tipoServizio = Utility.parseTipoSoggetto(nomeServizio);
			nomeServizio = Utility.parseNomeSoggetto(nomeServizio);

			String nomeErogatore = this.search.getNomeServizio().split(" \\(")[1]
					.replace(")", "");
			String tipoErogatore  = Utility.parseTipoSoggetto(nomeErogatore);
			nomeErogatore = Utility.parseNomeSoggetto(nomeErogatore); 

			AccordoServizioParteSpecifica aspsFromValues = this.dynamicUtils.getAspsFromValues(tipoServizio, nomeServizio, tipoErogatore, nomeErogatore);

			IdAccordoServizioParteComune idAccordoServizioParteComune = aspsFromValues.getIdAccordoServizioParteComune();
			Integer ver = idAccordoServizioParteComune.getVersione();
			String nomeSoggettoReferente = null;
			String tipoSoggettoReferente = null;

			if(idAccordoServizioParteComune.getIdSoggetto() != null){
				nomeSoggettoReferente=	idAccordoServizioParteComune.getIdSoggetto().getNome();
				tipoSoggettoReferente=	idAccordoServizioParteComune.getIdSoggetto().getTipo();
			}

			String nomeAS = idAccordoServizioParteComune.getNome(); 

			IDAccordo idAccordo = IDAccordoFactory.getInstance().getIDAccordoFromValues(nomeAS, tipoSoggettoReferente, nomeSoggettoReferente, ver);
			String nomeServizioKey = aspsFromValues.getPortType() != null ? aspsFromValues.getPortType() : nomeServizio;

			List<ConfigurazioneTransazioneStato> l = (((ITransazioniService)this.service))
					.getStatiByValues(idAccordo, nomeServizioKey,
							this.search.getNomeAzione());

			if (l != null) {
				for (ConfigurazioneTransazioneStato s : l) {
					this.stati.add(new SelectItem(s.getNome()));
				}
			}

		} catch (Exception e) {
			TrasazioniBean.log.error(e.getMessage(), e);
		}

		return this.stati;
	}

	public List<SelectItem> getRisorse() {

		try {

			this.risorse = new ArrayList<SelectItem>();
			if(this.search==null || this.search.getNomeServizio()==null){
				return this.risorse;
			}
			//
			//			String uri = this.search.getNomeServizio().split(" \\(")[1]
			//					.replace(")", "");
			//			IDAccordo idAccordo = IDAccordoFactory.getInstance()
			//					.getIDAccordoFromUri(uri);
			String nomeServizio = this.search.getNomeServizio().split(" \\(")[0];
			String tipoServizio = null;
			// showTipoSoggetto e' true allora la label e' di tipo TIPO/NOME
			tipoServizio = Utility.parseTipoSoggetto(nomeServizio);
			nomeServizio = Utility.parseNomeSoggetto(nomeServizio);

			String nomeErogatore = this.search.getNomeServizio().split(" \\(")[1]
					.replace(")", "");
			String tipoErogatore  = Utility.parseTipoSoggetto(nomeErogatore);
			nomeErogatore = Utility.parseNomeSoggetto(nomeErogatore); 

			AccordoServizioParteSpecifica aspsFromValues = this.dynamicUtils.getAspsFromValues(tipoServizio, nomeServizio, tipoErogatore, nomeErogatore);

			IdAccordoServizioParteComune idAccordoServizioParteComune = aspsFromValues.getIdAccordoServizioParteComune();
			Integer ver = idAccordoServizioParteComune.getVersione();
			String nomeSoggettoReferente = null;
			String tipoSoggettoReferente = null;

			if(idAccordoServizioParteComune.getIdSoggetto() != null){
				nomeSoggettoReferente=	idAccordoServizioParteComune.getIdSoggetto().getNome();
				tipoSoggettoReferente=	idAccordoServizioParteComune.getIdSoggetto().getTipo();
			}

			String nomeAS = idAccordoServizioParteComune.getNome(); 

			IDAccordo idAccordo = IDAccordoFactory.getInstance().getIDAccordoFromValues(nomeAS, tipoSoggettoReferente, nomeSoggettoReferente, ver);
			String nomeServizioKey = aspsFromValues.getPortType() != null ? aspsFromValues.getPortType() : nomeServizio;
			List<ConfigurazioneTransazioneRisorsaContenuto> l = (((ITransazioniService)this.service))
					.getRisorseContenutoByValues(idAccordo, nomeServizioKey,
							this.search.getNomeAzione(),
							((TransazioniSearchForm) this.search)
							.getNomeStato());

			if (l != null) {
				for (ConfigurazioneTransazioneRisorsaContenuto r : l) {
					this.risorse.add(new SelectItem(r.getNome()));
				}
			}

		} catch (Exception e) {
			TrasazioniBean.log.error(e.getMessage(), e);
		}

		return this.risorse;
	}

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

		return "transazioni";
	}

	public String saveDiagnostici() {
		try {

			// recupero lista diagnostici
			List<String> idTransazioni = new ArrayList<String>();

			// se nn sono in select all allore prendo solo quelle selezionate
			if (!this.isSelectedAll()) {
				Iterator<TransazioneBean> it = this.selectedIds.keySet().iterator();
				while (it.hasNext()) {
					TransazioneBean t = it.next();
					if (this.selectedIds.get(t).booleanValue()) {
						idTransazioni.add(t.getIdTransazione());
						it.remove();
					}
				}
			}

			// We must get first our context
			FacesContext context = FacesContext.getCurrentInstance();

			// Then we have to get the Response where to write our file
			HttpServletResponse response = (HttpServletResponse) context
					.getExternalContext().getResponse();

			// Salvo i parametri di export in sessione
			HttpSession sessione = (HttpSession) context.getExternalContext().getSession(false);

			sessione.setAttribute(CostantiExport.PARAMETER_ID_TRANSAZIONI_ORIGINALI, StringUtils.join(idTransazioni, ","));
			sessione.setAttribute(CostantiExport.PARAMETER_IS_ALL_ORIGINALE, this.isSelectedAll());

			response.sendRedirect(context.getExternalContext()
					.getRequestContextPath()
					+ "/diagnosticiexporter?isAll="
					+ this.isSelectedAll()
					+ "&ids="
					+ StringUtils.join(idTransazioni, ","));

			context.responseComplete();

			// End of the method
		} catch (Exception e) {
			FacesContext.getCurrentInstance().responseComplete();
			TrasazioniBean.log.error(e.getMessage(), e);
			MessageUtils
			.addErrorMsg("Si e' verificato un errore durante l'esportazione dei diagnostici.");
		}

		return null;
	}

	public String saveTracce() {

		try {

			// recupero lista diagnostici
			List<String> idTransazioni = new ArrayList<String>();

			// se nn sono in select all allore prendo solo quelle selezionate
			if (!this.isSelectedAll()) {
				Iterator<TransazioneBean> it = this.selectedIds.keySet().iterator();
				while (it.hasNext()) {
					TransazioneBean t = it.next();
					if (this.selectedIds.get(t).booleanValue()) {
						idTransazioni.add(t.getIdTransazione());
						it.remove();
					}
				}
			}

			// We must get first our context
			FacesContext context = FacesContext.getCurrentInstance();

			// Then we have to get the Response where to write our file
			HttpServletResponse response = (HttpServletResponse) context
					.getExternalContext().getResponse();

			// Salvo i parametri di export in sessione
			HttpSession sessione = (HttpSession) context.getExternalContext().getSession(false);

			sessione.setAttribute(CostantiExport.PARAMETER_ID_TRANSAZIONI_ORIGINALI, StringUtils.join(idTransazioni, ","));
			sessione.setAttribute(CostantiExport.PARAMETER_IS_ALL_ORIGINALE, this.isSelectedAll());

			response.sendRedirect(context.getExternalContext()
					.getRequestContextPath()
					+ "/tracceexporter?isAll="
					+ this.isSelectedAll()
					+ "&ids="
					+ StringUtils.join(idTransazioni, ","));

			context.responseComplete();

			// End of the method
		} catch (Exception e) {
			FacesContext.getCurrentInstance().responseComplete();
			TrasazioniBean.log.error(e.getMessage(), e);
			MessageUtils
			.addErrorMsg("Si e' verificato un errore durante l'esportazione delle tracce.");
		}

		return null;
	}

	public String exportSelected() {
		try {

			// recupero lista diagnostici
			List<String> idTransazioni = new ArrayList<String>();

			// se nn sono in select all allore prendo solo quelle selezionate
			if (!this.isSelectedAll()) {
				
				// NOTA: Al massimo sono selezionate 25 transazioni
				// NOTA2: Le transazioni esportate sono sempre ordinate per data
				List<String> orderFix = new ArrayList<String>();
				Hashtable<String, String> orderMap = new Hashtable<String, String>();
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss.SSS");
				
				Iterator<TransazioneBean> it = this.selectedIds.keySet().iterator();
				while (it.hasNext()) {
					TransazioneBean t = it.next();
					if (this.selectedIds.get(t).booleanValue()) {
						
						String d = format.format(t.getDataIngressoRichiesta());
						orderFix.add(d);
						orderMap.put(d, t.getIdTransazione());
						
						it.remove();
					}
				}
				
				Collections.sort(orderFix, Collections.reverseOrder());
				for (String data : orderFix) {
					idTransazioni.add(orderMap.get(data));
				}

			}

			// We must get first our context
			FacesContext context = FacesContext.getCurrentInstance();

			// Then we have to get the Response where to write our file
			HttpServletResponse response = (HttpServletResponse) context
					.getExternalContext().getResponse();

			// Salvo i parametri di export in sessione
			HttpSession sessione = (HttpSession) context.getExternalContext().getSession(false);

			sessione.setAttribute(CostantiExport.PARAMETER_ID_TRANSAZIONI_ORIGINALI, StringUtils.join(idTransazioni, ","));
			sessione.setAttribute(CostantiExport.PARAMETER_IS_ALL_ORIGINALE, this.isSelectedAll());
			sessione.setAttribute(CostantiExport.PARAMETER_TIPI_EXPORT_ORIGINALI, StringUtils.join(this.esportazioniSelezionate, ","));

			response.sendRedirect(context.getExternalContext()
					.getRequestContextPath()
					+ "/" + CostantiExport.TRANSAZIONI_EXPORTER_SERVLET_NAME + "?" + CostantiExport.PARAMETER_IS_ALL + "="
					+ this.isSelectedAll()
					+ "&" + CostantiExport.PARAMETER_IDS + "="
					+ StringUtils.join(idTransazioni, ",")
					+ "&" + CostantiExport.PARAMETER_EXPORTER + "="
					+ StringUtils.join(this.esportazioniSelezionate, ","));

			context.responseComplete();

			// End of the method
		} catch (Exception e) {
			FacesContext.getCurrentInstance().responseComplete();
			TrasazioniBean.log.error(e.getMessage(), e);
			MessageUtils
			.addErrorMsg("Si e' verificato un errore durante l'esportazione dei diagnostici.");
		}

		return null;
	}

	public String exportCsvSelected() {
		try {
			String formatoExport = this.getTipoExport();
			String colonneEsportate = this.getColonneEsportate();
			List<String> colonneSelezionate = this.getIdentificativiColonneSelezionate(colonneEsportate); 

			// genero un id per il check dei parametri passati dall'utente
			String idColonneSelezionate = UUID.randomUUID().toString().replace("-", ""); 

			// recupero lista diagnostici
			List<String> idTransazioni = new ArrayList<String>();

//			// se nn sono in select all allore prendo solo quelle selezionate
//			if (!this.isSelectedAll()) {
//				
//				// NOTA: Al massimo sono selezionate 25 transazioni
//				// NOTA2: Le transazioni esportate sono sempre ordinate per data
//				List<String> orderFix = new ArrayList<String>();
//				Hashtable<String, String> orderMap = new Hashtable<String, String>();
//				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss.SSS");
//				
//				Iterator<TransazioneBean> it = this.selectedIds.keySet().iterator();
//				while (it.hasNext()) {
//					TransazioneBean t = it.next();
//					if (this.selectedIds.get(t).booleanValue()) {
//						
//						String d = format.format(t.getDataIngressoRichiesta());
//						orderFix.add(d);
//						orderMap.put(d, t.getIdTransazione());
//						
//						it.remove();
//					}
//				}
//				
//				Collections.sort(orderFix, Collections.reverseOrder());
//				for (String data : orderFix) {
//					idTransazioni.add(orderMap.get(data));
//				}
//			}
			
			// se nn sono in select all allore prendo solo quelle selezionate
			if (this.elencoID != null && this.elencoID.length() > 0) {
				String [] split = this.elencoID.split(",");
				
				// NOTA: Al massimo sono selezionate 25 report
				// NOTA2: i report esportate sono sempre ordinate per data
				List<String> orderFix = new ArrayList<String>();
				
				// j_id170:tableReport_tbl:19:tableReport_column_ckb
				for (String idString : split) {
					String tmpId = idString.substring(0, idString.lastIndexOf(":"));
					tmpId = tmpId.substring(tmpId.lastIndexOf(":")+1);
					orderFix.add(tmpId); 
				}
				
				idTransazioni.addAll(orderFix);
			}

			// We must get first our context
			FacesContext context = FacesContext.getCurrentInstance();

			// Then we have to get the Response where to write our file
			HttpServletResponse response = (HttpServletResponse) context
					.getExternalContext().getResponse();

			// Salvo i parametri di export in sessione
			HttpSession sessione = (HttpSession) context.getExternalContext().getSession(false);

			sessione.setAttribute(CostantiExport.PARAMETER_ID_TRANSAZIONI_ORIGINALI, StringUtils.join(idTransazioni, ","));
			sessione.setAttribute(CostantiExport.PARAMETER_IS_ALL_ORIGINALE, this.isSelectedAll());
			sessione.setAttribute(CostantiExport.PARAMETER_TIPI_EXPORT_ORIGINALI, StringUtils.join(this.esportazioniSelezionate, ","));
			sessione.setAttribute(CostantiExport.PARAMETER_FORMATO_EXPORT_ORIGINALE, formatoExport);
			sessione.setAttribute(CostantiExport.PARAMETER_ID_SELEZIONI_ORIGINALI, idColonneSelezionate);
			sessione.setAttribute(CostantiExport.PARAMETER_LISTA_SELEZIONI_ORIGINALI, colonneSelezionate);

			response.sendRedirect(context.getExternalContext()
					.getRequestContextPath()
					+ "/" + CostantiExport.TRANSAZIONI_CSV_EXPORTER_SERVLET_NAME + "?" + CostantiExport.PARAMETER_IS_ALL + "="
					+ this.isSelectedAll()
					+ "&" + CostantiExport.PARAMETER_IDS + "="
					+ StringUtils.join(idTransazioni, ",")
					+ "&" + CostantiExport.PARAMETER_EXPORTER + "="
					+ StringUtils.join(this.esportazioniSelezionate, ",")
					+ "&" + CostantiExport.PARAMETER_FORMATO_EXPORT + "="
					+ formatoExport
					+ "&" + CostantiExport.PARAMETER_ID_SELEZIONI + "="
					+ idColonneSelezionate
					);

			context.responseComplete();

			// End of the method
		} catch (Exception e) {
			FacesContext.getCurrentInstance().responseComplete();
			TrasazioniBean.log.error(e.getMessage(), e);
			MessageUtils
			.addErrorMsg("Si e' verificato un errore durante l'esportazione dei diagnostici.");
		}
		return null;
	}
	
	@Override
	public void initExportListener(ActionEvent ae) {
		super.initExportListener(ae);
		
		this.tipoExport = CostantiExport.FORMATO_CSV_VALUE;
		this.colonneEsportate = null;
		this.tipiColonneEsportate = null;
	}

	public boolean isExportCsvCompletato() {
		return this.exportCsvCompletato;
	}

	public void setExportCsvCompletato(boolean exportCsvCompletato) {
		this.exportCsvCompletato = exportCsvCompletato;
	}

//	@SuppressWarnings("rawtypes")
	public List<String> getIdentificativiColonneSelezionate(String colonneEsportate){
		List<String> colonneSelezionate = new ArrayList<String>();

		if(colonneEsportate.equals(CostantiExport.COLONNE_VALUE_TUTTE)){
			// inserisco tutte le chiavi
			List<String> colonneSelezionateTmp = ColonnaExportManager.getInstance().getKeysColonne();
			
			for (String key : colonneSelezionateTmp) {
				
				if(isCsvColumnEnabled(key)==false){
					continue;
				}
								
				colonneSelezionate.add(key);
			}
			
		}else if(colonneEsportate.equals(CostantiExport.COLONNE_VALUE_VISUALIZZATE_NELLO_STORICO)){
			try{
				JSONObject json = JSONObject.fromObject(this.getTableState());
				if(json != null){
					// prelevo l'array dell'ordine delle colonne
					JSONArray jsonArrayColumnsOrder = json.getJSONArray(COLUMNS_ORDER_STATO_TABELLE_KEY);
					JSONObject jsonObjectColumnsVisibility = json.getJSONObject(COLUMNS_VISIBILITY_STATO_TABELLE_KEY); 

					for (int i = 0; i < jsonArrayColumnsOrder.size(); i++) {
						String key = jsonArrayColumnsOrder.getString(i);
						int visibility = jsonObjectColumnsVisibility.getInt(key);
						
						if(isCsvColumnEnabled(key)==false){
							visibility = -1;
						}

						// controllo che la colonna sia tra quelle previste e tra quelle visibili
						if(ColonnaExportManager.getInstance().containsColonna(key) && visibility > 0){
							colonneSelezionate.add(key);
						}
					}
				}
			}catch(Exception e){
				log.error("Errore durante la lettura dei nomi colonne: " + e.getMessage(), e); 
			}
		}else  { // personalizzato
			if(this.getElencoColonneSelezionate() != null)
				for (org.openspcoop2.web.monitor.core.bean.SelectItem selItem : this.getElencoColonneSelezionate()) {
					if(!colonneSelezionate.contains(selItem.getValue()))
						colonneSelezionate.add(selItem.getValue());
				}
		}

		// gestione di tracce e diagnostici
		if(this.esportazioniSelezionate.contains(CostantiExport.ESPORTAZIONI_VALUE_TRACCE)){
			colonneSelezionate.addAll(ColonnaExportManager.getInstance().getKeysColonneTracce());
		}

		if(this.esportazioniSelezionate.contains(CostantiExport.ESPORTAZIONI_VALUE_DIAGNOSTICI)){
			colonneSelezionate.addAll(ColonnaExportManager.getInstance().getKeysColonneDiagnostici());
		}


		return colonneSelezionate;
	}

	public boolean isCsvColumnEnabled(String key){
		// cluster id controllato anche da properties
		if(key.equals(CostantiExport.KEY_COL_CLUSTER_ID)){
			if(!this.isVisualizzaIdCluster())
				return false;
		}
		
		// colonna stato controllata da properties
		if(key.equals(CostantiExport.KEY_COL_STATO)){
			if(!this.applicationBean.getShowInformazioniContenutiTransazioniGrid())
				return false;
		}
		
		// colonna eventi controllata da properties
		if(key.equals(CostantiExport.KEY_COL_EVENTI_GESTIONE)){
			if(!this.applicationBean.getShowInformazioniEventiTransazioniGrid())
				return false;
		}
		
		// colonna pdd Codice controllata da numero soggetti associati all'utente
		if(key.equals(CostantiExport.KEY_COL_PDD_CODICE)){
			if(Utility.getLoggedUser().getSizeSoggetti() == 1)
				return false;
		}
		
		// colonna data accettazione richiesta controllata da proprieta in monitor.properties
		if(key.equals(CostantiExport.KEY_COL_DATA_ACCETTAZIONE_RICHIESTA)){
			if(!this.isVisualizzaDataAccettazione())
				return false;
		}
		
		// colonna data accettazione risposta controllata da proprieta in monitor.properties
		if(key.equals(CostantiExport.KEY_COL_DATA_ACCETTAZIONE_RISPOSTA)){
			if(!this.isVisualizzaDataAccettazione())
				return false;
		}
		
		return true;
	}
	
	public List<String> getEsportazioniSelezionate() {

		if (this.esportazioniSelezionate.size() == 0) {
			this.esportazioniSelezionate.add(CostantiExport.ESPORTAZIONI_VALUE_TRACCE);
			this.esportazioniSelezionate.add(CostantiExport.ESPORTAZIONI_VALUE_DIAGNOSTICI);
		}

		return this.esportazioniSelezionate;
	}

	public void setEsportazioniSelezionate(List<String> esportazioniSelezionate) {
		this.esportazioniSelezionate = esportazioniSelezionate;
	}
	
	public void setElencoEsportazioni(String esportazioniSelezionate) {
		String [] split = esportazioniSelezionate.split(",");
		if(split != null){
			this.esportazioniSelezionate.clear();
			for (String sel : split) {
				if(sel.equals("0"))
					this.esportazioniSelezionate.add(CostantiExport.ESPORTAZIONI_VALUE_TRACCE);
				if(sel.equals("1"))
					this.esportazioniSelezionate.add(CostantiExport.ESPORTAZIONI_VALUE_DIAGNOSTICI);
				if(sel.equals("2"))
					this.esportazioniSelezionate.add(CostantiExport.ESPORTAZIONI_VALUE_CONTENUTI);	
			}
		}
	}

	public String getTableState() {

		if (StringUtils.isNotEmpty(this.tableState))
			return this.tableState;

		Stato state = this.userService.getTableState(NomiTabelle.TRANSAZIONI.toString(),Utility.getLoggedUtente());

		this.tableState = state.getStato();

		return this.tableState;
	}

	public void setTableState(String tableState) {

		this.tableState = tableState;
		Stato state = this.userService.getTableState(NomiTabelle.TRANSAZIONI.toString(),Utility.getLoggedUtente());
		state.setStato(this.tableState);
		this.userService.saveTableState(NomiTabelle.TRANSAZIONI.toString(),Utility.getLoggedUtente(), state);

	}

	public boolean isVisualizzaIdCluster() {
		return this.visualizzaIdCluster;
	}

	public void setVisualizzaIdCluster(boolean visualizzaIdCluster) {
		this.visualizzaIdCluster = visualizzaIdCluster;
	}

	
	public boolean isVisualizzaDataAccettazione() {
		return this.visualizzaDataAccettazione;
	}

	public void setVisualizzaDataAccettazione(boolean visualizzaDataAccettazione) {
		this.visualizzaDataAccettazione = visualizzaDataAccettazione;
	}


	public boolean isExportCsvAbilitato() {
		this.exportCsvAbilitato = true;

		if(this.getEsportazioniSelezionate().contains(CostantiExport.ESPORTAZIONI_VALUE_CONTENUTI))
			this.exportCsvAbilitato = false;

		return this.exportCsvAbilitato;
	}

	public void setExportCsvAbilitato(boolean exportCsvAbilitato) {
		this.exportCsvAbilitato = exportCsvAbilitato;
	}

	/**
	 * <!-- <a4j:support event="onclick" reRender="exportCsv-iconLink" actionListener="#{mBean.esportazioneSelectListener}" /> -->
	 * */
	public void esportazioneSelectListener(ActionEvent ae){

	}

	public String visualizzaExportCsv(){
		return null;
	}

	public String getExportCsvErrorMessage() {
		return this.exportCsvErrorMessage;
	}

	public void setExportCsvErrorMessage(String exportCsvErrorMessage) {
		this.exportCsvErrorMessage = exportCsvErrorMessage;
	}


	private String exportCsvErrorMessage = null;

	private List<SelectItem> exportDisponibili = null;
	private String tipoExport = CostantiExport.FORMATO_CSV_VALUE;

	public List<SelectItem> getExportDisponibili() {
		if(this.exportDisponibili == null) {
			this.exportDisponibili = new ArrayList<SelectItem>();
			this.exportDisponibili.add(new SelectItem(CostantiExport.FORMATO_CSV_VALUE));
			this.exportDisponibili.add(new SelectItem(CostantiExport.FORMATO_XLS_VALUE));
		}

		return this.exportDisponibili;
	}

	public void setExportDisponibili(List<SelectItem> exportDisponibili) {
		this.exportDisponibili = exportDisponibili;
	}
	
	public String getTipoExport() {
		return this.tipoExport;
	}

	public void setTipoExport(String tipoExport) {
		this.tipoExport = tipoExport;
	}

	public void tipoColonneSelected(ActionEvent ae){
	}

	private List<SelectItem> tipiColonneEsportate = null;
	private String colonneEsportate = null;

	public List<SelectItem> getTipiColonneEsportate() {
		if(this.tipiColonneEsportate == null) {
			this.tipiColonneEsportate = new ArrayList<SelectItem>();
			this.tipiColonneEsportate.add(new SelectItem(CostantiExport.COLONNE_VALUE_VISUALIZZATE_NELLO_STORICO));
			this.tipiColonneEsportate.add(new SelectItem(CostantiExport.COLONNE_VALUE_TUTTE));
			this.tipiColonneEsportate.add(new SelectItem(CostantiExport.COLONNE_VALUE_PERSONALIZZA));
		}

		return this.tipiColonneEsportate;
	}

	public void setTipiColonneEsportate(List<SelectItem> tipiColonneEsportate) {
		this.tipiColonneEsportate = tipiColonneEsportate;
	}

	public String getColonneEsportate() {
		if(this.colonneEsportate == null)
			this.colonneEsportate = CostantiExport.COLONNE_VALUE_VISUALIZZATE_NELLO_STORICO;

		return this.colonneEsportate;
	}

	public void setColonneEsportate(String colonneEsportate) {
		this.colonneEsportate = colonneEsportate;
	}

	private List<org.openspcoop2.web.monitor.core.bean.SelectItem> elencoColonneDisponibili = null;
	private List<javax.faces.model.SelectItem> elencoImmagineColonne = null;
	private List<org.openspcoop2.web.monitor.core.bean.SelectItem> elencoColonneSelezionate = null;

	public List<org.openspcoop2.web.monitor.core.bean.SelectItem> getElencoColonneDisponibili() {
		if(this.elencoColonneDisponibili == null)
			this.elencoColonneDisponibili = this._getColonneDisponibili(this.getTableState());

		return this.elencoColonneDisponibili;
	}

	public void setElencoColonneDisponibili(List<org.openspcoop2.web.monitor.core.bean.SelectItem> elencoColonneDisponibili) {
		this.elencoColonneDisponibili = elencoColonneDisponibili;
	}

	public List<javax.faces.model.SelectItem> getElencoImmagineColonne() {
		if(this.elencoImmagineColonne == null){
			this.elencoImmagineColonne = new ArrayList<javax.faces.model.SelectItem>();
			List<org.openspcoop2.web.monitor.core.bean.SelectItem> _getSoggettiServizi = this._getColonneDisponibili(this.getTableState());

			//			log.debug("---- Soggetti Trovati ----"); 

			if(_getSoggettiServizi != null && _getSoggettiServizi.size() > 0)
				for (org.openspcoop2.web.monitor.core.bean.SelectItem selectItem : _getSoggettiServizi) {
					//					log.debug(selectItem.getValue() + "|" + selectItem.getLabel());
					this.elencoImmagineColonne.add(new javax.faces.model.SelectItem(selectItem)); 
				}
			//			log.debug("---- ----- ----- ----");
		}

		return this.elencoImmagineColonne;
	}

	public void setElencoImmagineColonne(List<javax.faces.model.SelectItem> elencoImmagineColonne) {
		this.elencoImmagineColonne = elencoImmagineColonne;
	}

	public List<org.openspcoop2.web.monitor.core.bean.SelectItem> getElencoColonneSelezionate() {
		return this.elencoColonneSelezionate;
	}

	public void setElencoColonneSelezionate(List<org.openspcoop2.web.monitor.core.bean.SelectItem> elencoColonneSelezionate) {
		log.debug("Set colonne selezionate ["+elencoColonneSelezionate+"]"); 
		this.elencoColonneSelezionate = elencoColonneSelezionate;
	}

	private List<org.openspcoop2.web.monitor.core.bean.SelectItem> _getColonneDisponibili(String tableStateUtente){
		List<org.openspcoop2.web.monitor.core.bean.SelectItem> colonne = new ArrayList<org.openspcoop2.web.monitor.core.bean.SelectItem>();
		List<org.openspcoop2.web.monitor.core.bean.SelectItem> colonneTmp = ColonnaExportManager.getInstance().getColonne();
		for (org.openspcoop2.web.monitor.core.bean.SelectItem selectItem : colonneTmp) {
			if(isCsvColumnEnabled((String)selectItem.getValue())==false){
				continue;
			}
			colonne.add(selectItem);
		}
		return colonne;
	}

	public void colonnaSelectListener(ActionEvent ae){
		log.debug("Evento selezione ["+ae+"]"); 
	}

	public Boolean getShowSelezioneColonne() {
		this.showSelezioneColonne = this.getColonneEsportate().equals(CostantiExport.COLONNE_VALUE_PERSONALIZZA);

		return this.showSelezioneColonne;
	}

	public void setShowSelezioneColonne(Boolean showSelezioneColonne) {
		this.showSelezioneColonne = showSelezioneColonne;
	}


	private Boolean showSelezioneColonne = false;

}
