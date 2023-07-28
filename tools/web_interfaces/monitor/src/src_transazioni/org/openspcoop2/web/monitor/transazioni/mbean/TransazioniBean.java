/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it).
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
package org.openspcoop2.web.monitor.transazioni.mbean;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.commons.search.AccordoServizioParteSpecifica;
import org.openspcoop2.core.commons.search.IdAccordoServizioParteComune;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazioneRisorsaContenuto;
import org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazioneStato;
import org.openspcoop2.web.lib.users.dao.Stato;
import org.openspcoop2.web.monitor.core.bean.ApplicationBean;
import org.openspcoop2.web.monitor.core.bean.BaseSearchForm;
import org.openspcoop2.web.monitor.core.constants.Costanti;
import org.openspcoop2.web.monitor.core.constants.ModalitaRicercaTransazioni;
import org.openspcoop2.web.monitor.core.constants.NomiTabelle;
import org.openspcoop2.web.monitor.core.constants.TipologiaRicerca;
import org.openspcoop2.web.monitor.core.core.PddMonitorProperties;
import org.openspcoop2.web.monitor.core.core.Utility;
import org.openspcoop2.web.monitor.core.dao.ISearchFormService;
import org.openspcoop2.web.monitor.core.dao.IUserService;
import org.openspcoop2.web.monitor.core.dao.UserService;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;
import org.openspcoop2.web.monitor.core.mbean.DynamicPdDBean;
import org.openspcoop2.web.monitor.core.utils.MessageManager;
import org.openspcoop2.web.monitor.core.utils.MessageUtils;
import org.openspcoop2.web.monitor.transazioni.bean.GruppoStorico;
import org.openspcoop2.web.monitor.transazioni.bean.Storico;
import org.openspcoop2.web.monitor.transazioni.bean.TransazioneBean;
import org.openspcoop2.web.monitor.transazioni.bean.TransazioniSearchForm;
import org.openspcoop2.web.monitor.transazioni.constants.TransazioniCostanti;
import org.openspcoop2.web.monitor.transazioni.dao.ITransazioniService;
import org.openspcoop2.web.monitor.transazioni.exporter.ColonnaExportManager;
import org.openspcoop2.web.monitor.transazioni.exporter.CostantiExport;
import org.slf4j.Logger;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * TransazioniBean
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class TransazioniBean extends DynamicPdDBean<TransazioneBean, String, ISearchFormService<TransazioneBean, String, TransazioniSearchForm>> {

	public static final String COLUMNS_VISIBILITY_STATO_TABELLE_KEY = "columnsVisibility";
	public static final String COLUMNS_ORDER_STATO_TABELLE_KEY = "columnsOrder";
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L; 
	private static Logger log =  LoggerManager.getPddMonitorCoreLogger();
	private List<SelectItem> stati = new ArrayList<SelectItem>();
	private List<SelectItem> risorse = new ArrayList<SelectItem>();

	private List<String> esportazioniSelezionate = new ArrayList<>();

	private String tableState = "";

	private transient IUserService userService = null;

	private boolean visualizzaIdCluster = false;
	private boolean exportCsvCompletato = false;
	private boolean visualizzaDataAccettazione = false;
	private boolean updateTipoStorico = false;

	private ApplicationBean applicationBean = null;
	
	private List<GruppoStorico> tipiStorico;
	private String tipoStorico;
	private List<GruppoStorico> tipiStoricoLivello2;
	
	private boolean visualizzazioneStoricoTabellare = false;
	private boolean exportTransazioniCsvVisualizzaCheckBoxSelezioneContenuti = false;
	
	private boolean transazioniLatenzaPortaEnabled = false;

	public TransazioniBean(){
		super();

		try{
			PddMonitorProperties govwayMonitorProperties = PddMonitorProperties.getInstance(TransazioniBean.log);
			List<String> govwayMonitorare = govwayMonitorProperties.getListaPdDMonitorate_StatusPdD();
			this.setVisualizzaIdCluster(govwayMonitorare!=null && govwayMonitorare.size()>1);
			this.visualizzaDataAccettazione = govwayMonitorProperties.isAttivoTransazioniDataAccettazione();
			
			this.applicationBean = new ApplicationBean();
			this.applicationBean.setLoginBean(Utility.getLoginBean()); 
			
			this.visualizzazioneStoricoTabellare = !govwayMonitorProperties.isAttivoUtilizzaVisualizzazioneCustomTransazioni();
			this.exportTransazioniCsvVisualizzaCheckBoxSelezioneContenuti = govwayMonitorProperties.isExportTransazioniCsvVisualizzaCheckBoxSelezioneContenuti();
			
			this.transazioniLatenzaPortaEnabled = govwayMonitorProperties.isTransazioniLatenzaPortaEnabled();
			
		}catch(Exception e){
			TransazioniBean.log.error(e.getMessage(), e);
		}

		try{
			this.userService = new UserService();
		}catch(Exception e){
			TransazioniBean.log.error(e.getMessage(), e);
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
			
			IDServizio idServizio = Utility.parseServizioSoggetto(this.search.getNomeServizio());
			
			String nomeServizio = idServizio.getNome();
			String tipoServizio = idServizio.getTipo();
			String nomeErogatore = idServizio.getSoggettoErogatore().getNome();
			String tipoErogatore = idServizio.getSoggettoErogatore().getTipo();
			Integer versioneServizio = idServizio.getVersione();

			AccordoServizioParteSpecifica aspsFromValues = this.dynamicUtils.getAspsFromValues(tipoServizio, nomeServizio, tipoErogatore, nomeErogatore,versioneServizio);

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
			TransazioniBean.log.error(e.getMessage(), e);
		}

		return this.stati;
	}

	public List<SelectItem> getRisorse() {

		try {

			this.risorse = new ArrayList<SelectItem>();
			if(this.search==null || this.search.getNomeServizio()==null){
				return this.risorse;
			}
			
			IDServizio idServizio = Utility.parseServizioSoggetto(this.search.getNomeServizio());
			
			String nomeServizio = idServizio.getNome();
			String tipoServizio = idServizio.getTipo();
			String nomeErogatore = idServizio.getSoggettoErogatore().getNome();
			String tipoErogatore = idServizio.getSoggettoErogatore().getTipo();
			Integer versioneServizio = idServizio.getVersione();

			AccordoServizioParteSpecifica aspsFromValues = this.dynamicUtils.getAspsFromValues(tipoServizio, nomeServizio, tipoErogatore, nomeErogatore,versioneServizio);

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
			TransazioniBean.log.error(e.getMessage(), e);
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
			List<String> idTransazioni = new ArrayList<>();

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
			TransazioniBean.log.error(e.getMessage(), e);
			MessageUtils
			.addErrorMsg("Si e' verificato un errore durante l'esportazione dei diagnostici.");
		}

		return null;
	}

	public String saveTracce() {

		try {

			// recupero lista diagnostici
			List<String> idTransazioni = new ArrayList<>();

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
			TransazioniBean.log.error(e.getMessage(), e);
			MessageUtils
			.addErrorMsg("Si e' verificato un errore durante l'esportazione delle tracce.");
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
			List<String> idTransazioni = new ArrayList<>();

//			// se nn sono in select all allore prendo solo quelle selezionate
//			if (!this.isSelectedAll()) {
//				
//				// NOTA: Al massimo sono selezionate 25 transazioni
//				// NOTA2: Le transazioni esportate sono sempre ordinate per data
//				List<String> orderFix = new ArrayList<>();
//				Map<String, String> orderMap = new HashMap<>();
//				SimpleDateFormat format = Utilities.getSimpleDateFormatMs();
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
				List<String> orderFix = new ArrayList<>();
				
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
			
			if(CostantiExport.FORMATO_ZIP_VALUE.equals(formatoExport)) {
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
			} else {
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
			}

			

			context.responseComplete();

			// End of the method
		} catch (Exception e) {
			FacesContext.getCurrentInstance().responseComplete();
			TransazioniBean.log.error(e.getMessage(), e);
			MessageUtils
			.addErrorMsg("Si e' verificato un errore durante l'esportazione dei diagnostici.");
		}
		return null;
	}
	
	@Override
	public void initExportListener(ActionEvent ae) {
		super.initExportListener(ae);
		
		this.tipoExport = CostantiExport.FORMATO_CSV_VALUE;
		this.showSelezioneContenuti = this.exportTransazioniCsvVisualizzaCheckBoxSelezioneContenuti;
		this.showSelezioneTipoColonne = true;
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
		List<String> colonneSelezionate = new ArrayList<>();

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
			if(this.visualizzazioneStoricoTabellare) {
				try{
					JSONObject json = JSONObject.fromObject(this.getTableState());
					if(json != null){
						// prelevo l'array dell'ordine delle colonne
						JSONArray jsonArrayColumnsOrder = json.getJSONArray(TransazioniBean.COLUMNS_ORDER_STATO_TABELLE_KEY);
						JSONObject jsonObjectColumnsVisibility = json.getJSONObject(TransazioniBean.COLUMNS_VISIBILITY_STATO_TABELLE_KEY); 
	
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
					TransazioniBean.log.error("Errore durante la lettura dei nomi colonne: " + e.getMessage(), e); 
				}
			}
			else {
				// inserisco tutte le chiavi della custom view
				List<String> colonneSelezionateTmp = ColonnaExportManager.getInstance().getKeysColonneCustomView();
				
				for (String key : colonneSelezionateTmp) {
					
					if(isCsvColumnEnabled(key)==false){
						continue;
					}
									
					colonneSelezionate.add(key);
				}
			}
		}else  { // personalizzato
			if(this.getElencoColonneSelezionate() != null)
				for (org.openspcoop2.web.monitor.core.bean.SelectItem selItem : this.getElencoColonneSelezionate()) {
					if(!colonneSelezionate.contains(selItem.getValue())) {
						colonneSelezionate.add(selItem.getValue());
					}
				}
		}

		// gestione di tracce e diagnostici
		if(this.exportTransazioniCsvVisualizzaCheckBoxSelezioneContenuti && this.esportazioniSelezionate.contains(CostantiExport.ESPORTAZIONI_VALUE_TRACCE)){
			colonneSelezionate.addAll(ColonnaExportManager.getInstance().getKeysColonneTracce());
		} else {
			colonneSelezionate.removeAll(ColonnaExportManager.getInstance().getKeysColonneTracce());
		}

		if(this.exportTransazioniCsvVisualizzaCheckBoxSelezioneContenuti && this.esportazioniSelezionate.contains(CostantiExport.ESPORTAZIONI_VALUE_DIAGNOSTICI)){
			colonneSelezionate.addAll(ColonnaExportManager.getInstance().getKeysColonneDiagnostici());
		} else {
			colonneSelezionate.removeAll(ColonnaExportManager.getInstance().getKeysColonneDiagnostici());
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
		try {
			if (this.esportazioniSelezionate.size() == 0) {
				if(CostantiExport.FORMATO_ZIP_VALUE.equals(this.tipoExport)) {
					if(PddMonitorProperties.getInstance(log).isExportTransazioniZipTracceDefaultValue())
						this.esportazioniSelezionate.add(CostantiExport.ESPORTAZIONI_VALUE_TRACCE);
					if(PddMonitorProperties.getInstance(log).isExportTransazioniZipDiagnosticiDefaultValue())
						this.esportazioniSelezionate.add(CostantiExport.ESPORTAZIONI_VALUE_DIAGNOSTICI);
					if(PddMonitorProperties.getInstance(log).isExportTransazioniZipContenutiDefaultValue())
						this.esportazioniSelezionate.add(CostantiExport.ESPORTAZIONI_VALUE_CONTENUTI);
				} else { // CSV/XLS
					if(this.showSelezioneContenuti) {
						if(PddMonitorProperties.getInstance(log).isExportTransazioniCsvTracceDefaultValue())
							this.esportazioniSelezionate.add(CostantiExport.ESPORTAZIONI_VALUE_TRACCE);
						if(PddMonitorProperties.getInstance(log).isExportTransazioniCsvDiagnosticiDefaultValue())
							this.esportazioniSelezionate.add(CostantiExport.ESPORTAZIONI_VALUE_DIAGNOSTICI);
					} else {
						this.esportazioniSelezionate.clear();
					}
				}
			}
		}catch(Exception e) {
			
		}

		return this.esportazioniSelezionate;
	}

	public void setEsportazioniSelezionate(List<String> esportazioniSelezionate) {
		this.esportazioniSelezionate = esportazioniSelezionate;
	}
	
	private List<SelectItem> esportazioniSelezionateDisponibili = null;

	public List<SelectItem> getEsportazioniSelezionateDisponibili() {
		this.esportazioniSelezionateDisponibili = new ArrayList<SelectItem>();
		
		if(CostantiExport.FORMATO_ZIP_VALUE.equals(this.tipoExport)) {
			this.esportazioniSelezionateDisponibili.add(new SelectItem(CostantiExport.ESPORTAZIONI_VALUE_TRACCE, CostantiExport.ESPORTAZIONI_LABEL_TRACCE));
			this.esportazioniSelezionateDisponibili.add(new SelectItem(CostantiExport.ESPORTAZIONI_VALUE_DIAGNOSTICI, CostantiExport.ESPORTAZIONI_LABEL_DIAGNOSTICI));
			this.esportazioniSelezionateDisponibili.add(new SelectItem(CostantiExport.ESPORTAZIONI_VALUE_CONTENUTI, CostantiExport.ESPORTAZIONI_LABEL_CONTENUTI));
		} else { // CSV/XLS
			this.esportazioniSelezionateDisponibili.add(new SelectItem(CostantiExport.ESPORTAZIONI_VALUE_TRACCE, CostantiExport.ESPORTAZIONI_LABEL_TRACCE));
			this.esportazioniSelezionateDisponibili.add(new SelectItem(CostantiExport.ESPORTAZIONI_VALUE_DIAGNOSTICI, CostantiExport.ESPORTAZIONI_LABEL_DIAGNOSTICI));
		}

		return this.esportazioniSelezionateDisponibili;
	}

	public void setEsportazioniSelezionateDisponibili(List<SelectItem> esportazioniSelezionateDisponibili) {
		this.esportazioniSelezionateDisponibili = esportazioniSelezionateDisponibili;
	}
	
	private Boolean showSelezioneContenuti = false;
	
	public Boolean getShowSelezioneContenuti() {
		if(CostantiExport.FORMATO_ZIP_VALUE.equals(this.tipoExport)) {
			this.showSelezioneContenuti = true;
		} else { // CSV/XLS
			this.showSelezioneContenuti = this.exportTransazioniCsvVisualizzaCheckBoxSelezioneContenuti;
		}
		return this.showSelezioneContenuti;
	}

	public void setShowSelezioneContenuti(Boolean showSelezioneContenuti) {
		this.showSelezioneContenuti = showSelezioneContenuti;
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

	public boolean isVisualizzaProfiloTransazione() {
		if(this.search!=null &&
				ModalitaRicercaTransazioni.RICERCA_LIBERA.getValue().equals(((TransazioniSearchForm)this.search).getModalitaRicercaStorico()) &&
				Costanti.VALUE_PARAMETRO_MODALITA_ALL.equals(this.search.getModalita()) &&
				(this.search.getProtocollo()==null || Costanti.VALUE_PARAMETRO_MODALITA_ALL.equals(this.search.getProtocollo()))) { 
			return true;
		}
		return false;
	}
	
	public boolean isVisualizzaDataAccettazione() {
		return this.visualizzaDataAccettazione;
	}

	public void setVisualizzaDataAccettazione(boolean visualizzaDataAccettazione) {
		this.visualizzaDataAccettazione = visualizzaDataAccettazione;
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
			this.exportDisponibili.add(new SelectItem(CostantiExport.FORMATO_ZIP_VALUE));
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
	
	public void tipoExportSelected(ActionEvent ae){
		this.esportazioniSelezionate.clear();
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
		TransazioniBean.log.debug("Set colonne selezionate ["+elencoColonneSelezionate+"]"); 
		this.elencoColonneSelezionate = elencoColonneSelezionate;
	}

	private List<org.openspcoop2.web.monitor.core.bean.SelectItem> _getColonneDisponibili(String tableStateUtente){
		List<org.openspcoop2.web.monitor.core.bean.SelectItem> colonne = new ArrayList<org.openspcoop2.web.monitor.core.bean.SelectItem>();
		List<org.openspcoop2.web.monitor.core.bean.SelectItem> colonneTmp = ColonnaExportManager.getInstance().getColonne();
		for (org.openspcoop2.web.monitor.core.bean.SelectItem selectItem : colonneTmp) {
			if(isCsvColumnEnabled(selectItem.getValue())==false){
				continue;
			}
			colonne.add(selectItem);
		}
		return colonne;
	}

	public void colonnaSelectListener(ActionEvent ae){
		TransazioniBean.log.debug("Evento selezione ["+ae+"]"); 
	}

	public Boolean getShowSelezioneColonne() {
		this.showSelezioneColonne = this.getColonneEsportate().equals(CostantiExport.COLONNE_VALUE_PERSONALIZZA);

		return this.showSelezioneColonne;
	}

	public void setShowSelezioneColonne(Boolean showSelezioneColonne) {
		this.showSelezioneColonne = showSelezioneColonne;
	}


	private Boolean showSelezioneColonne = false;
	private Boolean showSelezioneTipoColonne = false;
	
	public Boolean getShowSelezioneTipoColonne() {
		this.showSelezioneTipoColonne = !this.getTipoExport().equals(CostantiExport.FORMATO_ZIP_VALUE);

		return this.showSelezioneTipoColonne;
	}

	public void setShowSelezioneTipoColonne(Boolean showSelezioneTipoColonne) {
		this.showSelezioneTipoColonne = showSelezioneTipoColonne;
	}


	public List<GruppoStorico> getTipiStorico() {
		if(this.tipiStorico == null){
			this.tipiStorico = new ArrayList<GruppoStorico>();

			GruppoStorico gruppoTemporale = new GruppoStorico();
			gruppoTemporale.setLabel(MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_SEARCH_TIPO_RICERCA_TEMPORALE_LABEL_KEY));
			List<Storico> listaGruppoTermporale = new ArrayList<>();
			listaGruppoTermporale.add(new Storico(ModalitaRicercaTransazioni.ANDAMENTO_TEMPORALE.getValue(), 
					MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_SEARCH_TIPO_RICERCA_TEMPORALE_RICERCA_BASE_LABEL_KEY), 
					ModalitaRicercaTransazioni.ANDAMENTO_TEMPORALE,
					MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_SEARCH_TIPO_RICERCA_TEMPORALE_RICERCA_BASE_ICON_KEY)));
			listaGruppoTermporale.add(new Storico(ModalitaRicercaTransazioni.RICERCA_LIBERA.getValue(), 
					MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_SEARCH_TIPO_RICERCA_TEMPORALE_RICERCA_LIBERA_LABEL_KEY), 
					ModalitaRicercaTransazioni.RICERCA_LIBERA,
					MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_SEARCH_TIPO_RICERCA_TEMPORALE_RICERCA_LIBERA_ICON_KEY)));
			gruppoTemporale.setListaStorico(listaGruppoTermporale);
			this.tipiStorico.add(gruppoTemporale);
			
			GruppoStorico gruppoMittente = new GruppoStorico();
			gruppoMittente.setLabel(MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_SEARCH_TIPO_RICERCA_MITTENTE_LABEL_KEY));
			List<Storico> listaGruppoMittente = new ArrayList<>();
			listaGruppoMittente.add(new Storico(ModalitaRicercaTransazioni.MITTENTE_TOKEN_INFO.getValue(), 
					MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_SEARCH_TIPO_RICERCA_MITENTE_RICERCA_TOKEN_INFO_LABEL_KEY), 
					ModalitaRicercaTransazioni.MITTENTE_TOKEN_INFO,
					MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_SEARCH_TIPO_RICERCA_MITENTE_RICERCA_TOKEN_INFO_ICON_KEY)));
			listaGruppoMittente.add(new Storico(ModalitaRicercaTransazioni.MITTENTE_SOGGETTO.getValue(), 
					MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_SEARCH_TIPO_RICERCA_MITENTE_RICERCA_SOGGETTO_LABEL_KEY), 
					ModalitaRicercaTransazioni.MITTENTE_SOGGETTO,
					MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_SEARCH_TIPO_RICERCA_MITENTE_RICERCA_SOGGETTO_ICON_KEY)));
			listaGruppoMittente.add(new Storico(ModalitaRicercaTransazioni.MITTENTE_APPLICATIVO.getValue(), 
					MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_SEARCH_TIPO_RICERCA_MITENTE_RICERCA_APPLICATIVO_LABEL_KEY), 
					ModalitaRicercaTransazioni.MITTENTE_APPLICATIVO,
					MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_SEARCH_TIPO_RICERCA_MITENTE_RICERCA_APPLICATIVO_ICON_KEY)));
			listaGruppoMittente.add(new Storico(ModalitaRicercaTransazioni.MITTENTE_IDENTIFICATIVO_AUTENTICATO.getValue(), 
					MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_SEARCH_TIPO_RICERCA_MITENTE_RICERCA_IDENTIFICATIVO_AUTENTICATO_LABEL_KEY), 
					ModalitaRicercaTransazioni.MITTENTE_IDENTIFICATIVO_AUTENTICATO,
					MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_SEARCH_TIPO_RICERCA_MITENTE_RICERCA_IDENTIFICATIVO_AUTENTICATO_ICON_KEY)));
			listaGruppoMittente.add(new Storico(ModalitaRicercaTransazioni.MITTENTE_INDIRIZZO_IP.getValue(), 
					MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_SEARCH_TIPO_RICERCA_MITENTE_RICERCA_INDIRIZZO_IP_LABEL_KEY), 
					ModalitaRicercaTransazioni.MITTENTE_INDIRIZZO_IP,
					MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_SEARCH_TIPO_RICERCA_MITENTE_RICERCA_INDIRIZZO_IP_ICON_KEY)));
			gruppoMittente.setListaStorico(listaGruppoMittente);
			this.tipiStorico.add(gruppoMittente);

			GruppoStorico gruppoId = new GruppoStorico();
			gruppoId.setLabel(MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_SEARCH_TIPO_RICERCA_ID_LABEL_KEY));
			List<Storico> listaGruppoId = new ArrayList<>();
			listaGruppoId.add(new Storico(ModalitaRicercaTransazioni.ID_TRANSAZIONE.getValue(), 
					MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_SEARCH_TIPO_RICERCA_ID_RICERCA_ID_TRANSAZIONE_LABEL_KEY), 
					ModalitaRicercaTransazioni.ID_TRANSAZIONE,
					MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_SEARCH_TIPO_RICERCA_ID_RICERCA_ID_TRANSAZIONE_ICON_KEY)));
			Storico storicoIdApplicativo = new Storico(ModalitaRicercaTransazioni.ID_APPLICATIVO_AVANZATA.getValue(), 
					MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_SEARCH_TIPO_RICERCA_ID_RICERCA_ID_APPLICATIVO_LABEL_KEY), 
					ModalitaRicercaTransazioni.ID_APPLICATIVO_AVANZATA,
					MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_SEARCH_TIPO_RICERCA_ID_RICERCA_ID_APPLICATIVO_ICON_KEY));
			storicoIdApplicativo.setAction(TransazioniCostanti.NOME_ACTION_RICERCA_LVL2);
			listaGruppoId.add(storicoIdApplicativo);
			listaGruppoId.add(new Storico(ModalitaRicercaTransazioni.ID_MESSAGGIO.getValue(), 
					MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_SEARCH_TIPO_RICERCA_ID_RICERCA_ID_MESSAGGIO_LABEL_KEY), 
					ModalitaRicercaTransazioni.ID_MESSAGGIO,
					MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_SEARCH_TIPO_RICERCA_ID_RICERCA_ID_MESSAGGIO_ICON_KEY)));
			gruppoId.setListaStorico(listaGruppoId);
			this.tipiStorico.add(gruppoId);
		}
		
		return this.tipiStorico;
	}

	public void setTipiStorico(List<GruppoStorico> tipiStorico) {
		this.tipiStorico = tipiStorico;
	}

	public String getTipoStorico() {
		return this.tipoStorico;
	}

	public void setTipoStorico(String tipoStorico) {
		this.tipoStorico = tipoStorico;
		
		if(this.updateTipoStorico) {
			this.search.initSearchListener(null);
			((TransazioniSearchForm)this.search).setModalitaRicercaStorico(this.tipoStorico);
			
			if(!TipologiaRicerca.all.equals(this.search.getDefaultTipologiaRicercaEnum())) {
				((TransazioniSearchForm)this.search).setTipologiaRicerca(this.search.getDefaultTipologiaRicercaEnum());
			}
			else {
				((TransazioniSearchForm)this.search).setTipologiaRicerca("--"); // in modo da far comparire la lista con il suggerimento di selezione come per gli altri
			}
			
			((TransazioniSearchForm)this.search).updateRiconoscimentoByModalitaRicercaStorico();
			
			this.updateTipoStorico = false;
		}
		
	}

	public boolean isUpdateTipoStorico() {
		return this.updateTipoStorico;
	}

	public void setUpdateTipoStorico(boolean updateTipoStorico) {
		this.updateTipoStorico = updateTipoStorico;
	}
	
	public void setTipiStoricoLivello2(List<GruppoStorico> tipiStoricoLivello2) {
		this.tipiStoricoLivello2 = tipiStoricoLivello2;
	}
	
	public List<GruppoStorico> getTipiStoricoLivello2() {
		if(this.tipoStorico == null)
			return this.tipiStoricoLivello2;
		
		this.tipiStoricoLivello2 = new ArrayList<GruppoStorico>();
		
		GruppoStorico gruppoId = new GruppoStorico();
		gruppoId.setLabel(MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_SEARCH_TIPO_RICERCA_ID_LABEL_KEY));
		List<Storico> listaGruppoId = new ArrayList<>();
		
		listaGruppoId.add(new Storico(ModalitaRicercaTransazioni.ID_APPLICATIVO_BASE.getValue(), 
				MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_SEARCH_TIPO_RICERCA_ID_RICERCA_ID_APPLICATIVO_LVL2_RICERCA_BASE_LABEL_KEY),
				ModalitaRicercaTransazioni.ID_APPLICATIVO_BASE,
				MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_SEARCH_TIPO_RICERCA_ID_RICERCA_ID_APPLICATIVO_LVL2_RICERCA_BASE_ICON_KEY), 2));
		listaGruppoId.add(new Storico(ModalitaRicercaTransazioni.ID_APPLICATIVO_AVANZATA.getValue(), 
				MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_SEARCH_TIPO_RICERCA_ID_RICERCA_ID_APPLICATIVO_LVL2_RICERCA_AVANZATA_LABEL_KEY), 
				ModalitaRicercaTransazioni.ID_APPLICATIVO_AVANZATA,
				MessageManager.getInstance().getMessage(TransazioniCostanti.TRANSAZIONI_SEARCH_TIPO_RICERCA_ID_RICERCA_ID_APPLICATIVO_LVL2_RICERCA_AVANZATA_ICON_KEY), 2));
		gruppoId.setListaStorico(listaGruppoId);
		this.tipiStoricoLivello2.add(gruppoId);
		
		return this.tipiStoricoLivello2;
	}
	
	public boolean isTransazioniLatenzaPortaEnabled() {
		return this.transazioniLatenzaPortaEnabled;
	}

	public void setTransazioniLatenzaPortaEnabled(boolean transazioniLatenzaPortaEnabled) {
		this.transazioniLatenzaPortaEnabled = transazioniLatenzaPortaEnabled;
	}
}
