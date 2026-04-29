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
package org.openspcoop2.web.monitor.core.mbean;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import org.apache.commons.lang3.StringUtils;
import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.commons.search.Soggetto;
import org.openspcoop2.core.commons.search.constants.TipoPdD;
import org.openspcoop2.core.config.driver.db.DriverConfigurazioneDB;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.registry.driver.db.DriverRegistroServiziDB;
import org.openspcoop2.protocol.engine.utils.NamingUtils;
import org.openspcoop2.web.monitor.core.bean.BaseSearchForm;
import org.openspcoop2.web.monitor.core.bean.UserDetailsBean;
import org.openspcoop2.web.monitor.core.constants.Costanti;
import org.openspcoop2.web.monitor.core.constants.TipologiaRicerca;
import org.openspcoop2.web.monitor.core.core.ConfigurazioneSoggettiVisualizzatiSearchForm;
import org.openspcoop2.web.monitor.core.core.Utility;
import org.openspcoop2.web.monitor.core.dao.IService;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;
import org.openspcoop2.web.monitor.core.utils.DynamicPdDBeanUtils;
import org.slf4j.Logger;

/**
 * DynamicPdDBean
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
@SuppressWarnings("rawtypes")
public class DynamicPdDBean<T,K,S extends IService> extends PdDBaseBean<T, K, S>{


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L; 

	protected static Logger log =  LoggerManager.getPddMonitorCoreLogger();

	private List<SelectItem> soggettiAssociati = null;
	private List<SelectItem> soggettiLocale = null;
	private List<SelectItem> soggetti = null;
	private List<SelectItem> gruppi = null;
	private List<SelectItem> api = null;
	private List<SelectItem> servizi = null;

	private List<SelectItem> azioni = null;
	private List<SelectItem> serviziApplicativi = null;

	protected Integer soggettiAssociatiSelectItemsWidth = 0;
	protected Integer soggettiLocaleSelectItemsWidth = 0;
	protected Integer soggettiSelectItemsWidth = 0;
	protected Integer gruppiSelectItemsWidth= 0;
	protected Integer apiSelectItemsWidth= 0;
	protected Integer serviziSelectItemsWidth= 0;
	protected Integer serviziApplicativiSelectItemsWidth = 0;
	protected Integer azioniSelectItemsWidth = 0;

	protected boolean soggettiAssociatiSelectItemsWidthCheck = false;
	protected boolean soggettiLocaleSelectItemsWidthCheck = false;
	protected boolean soggettiSelectItemsWidthCheck = false;
	protected boolean gruppiSelectItemsWidthCheck = false;
	protected boolean apiSelectItemsWidthCheck = false;
	protected boolean serviziSelectItemsWidthCheck = false;
	protected boolean serviziApplicativiSelectItemsWidthCheck = false;
	protected boolean azioniSelectItemsWidthCheck = false;

	protected transient BaseSearchForm search;

	protected Integer maxSelectItemsWidth = 900;

	protected Integer defaultSelectItemsWidth = 412;

	protected DynamicPdDBeanUtils dynamicUtils = null;

	public DynamicPdDBean(){
		super();
		try {
			this.dynamicUtils = new DynamicPdDBeanUtils(log);
		} catch (Exception e) {
			DynamicPdDBean.log.error("lettura delle properties fallita.....",
					e);
		}
	}
	public DynamicPdDBean(org.openspcoop2.core.commons.search.dao.IServiceManager serviceManager, org.openspcoop2.core.plugins.dao.IServiceManager pluginsServiceManager,
			DriverRegistroServiziDB driverRegistroServiziDB, DriverConfigurazioneDB driverConfigurazioneDB){
		super(serviceManager, pluginsServiceManager,
				driverRegistroServiziDB, driverConfigurazioneDB);
		try {
			this.dynamicUtils = new DynamicPdDBeanUtils(serviceManager, pluginsServiceManager,
					driverRegistroServiziDB, driverConfigurazioneDB,
					log);
		} catch (Exception e) {
			DynamicPdDBean.log.error("lettura delle properties fallita.....",
					e);
		}
	}

	public void setSearch(BaseSearchForm searc) {
		this.search = searc;
	}

	public BaseSearchForm getSearch() {
		return this.search;
	}
	
	
	public List<org.openspcoop2.web.monitor.core.bean.SelectItem> gruppiAutoComplete(Object val){
		List<org.openspcoop2.web.monitor.core.bean.SelectItem> listaGruppi = new ArrayList<>();
		List<SelectItem> listaGruppiTmp = new ArrayList<>();
		if(val!=null && !StringUtils.isEmpty((String)val)) {
			listaGruppiTmp = this.getGruppiEngine((String)val);
		}
		
		listaGruppiTmp.add(0, new SelectItem("--", "--"));
		
		for (SelectItem selectItem : listaGruppiTmp) {
			String label = selectItem.getLabel();
			String value = (String) selectItem.getValue();
			
			org.openspcoop2.web.monitor.core.bean.SelectItem newItem = new org.openspcoop2.web.monitor.core.bean.SelectItem(value, label);
			listaGruppi.add(newItem);
		}

		return listaGruppi;
	}

	public List<SelectItem> getGruppi() {
		return getGruppiEngine(null);		

	}

	protected List<SelectItem> getGruppiEngine(String input) {
		if(this.search==null){
			return new ArrayList<>();
		}
		if(!this.gruppiSelectItemsWidthCheck){
			this.gruppi = new ArrayList<>();
			
			String tipoProtocollo = this.search.getProtocollo();
			this.gruppi = this.dynamicUtils.getListaGruppi(tipoProtocollo, input);
			
			Integer lunghezzaSelectList = this.dynamicUtils.getLunghezzaSelectList(this.gruppi);
			this.gruppiSelectItemsWidth = Math.max(this.gruppiSelectItemsWidth,  lunghezzaSelectList);
		}
		return this.gruppi;
	}
	
	
	public List<org.openspcoop2.web.monitor.core.bean.SelectItem> azioniAutoComplete(Object val) {
		List<org.openspcoop2.web.monitor.core.bean.SelectItem> listaAzioni = new ArrayList<>();
		List<SelectItem> listaAzioniTmp = new ArrayList<>();
		if(val!=null && !StringUtils.isEmpty((String)val)) {
			listaAzioniTmp = this.getAzioniEngine((String)val);
		}
		
		listaAzioniTmp.add(0, new SelectItem("--", "--"));
		
		for (SelectItem selectItem : listaAzioniTmp) {
			String label = selectItem.getLabel();
			String value = (String) selectItem.getValue();
			
			org.openspcoop2.web.monitor.core.bean.SelectItem newItem = new org.openspcoop2.web.monitor.core.bean.SelectItem(value, label);
			listaAzioni.add(newItem);
		}

		return listaAzioni;
	}

	public List<SelectItem> getAzioni() {
		return getAzioniEngine(null);
	}

	protected List<SelectItem> getAzioniEngine(String input) {
		if(this.search==null){
			return new ArrayList<>();
		}
		if(this.search.getApi() == null && this.search.getNomeServizio()==null){
			return new ArrayList<>();
		}
		if (StringUtils.isBlank(this.search.getNomeServizio()) && StringUtils.isBlank(this.search.getApi())) {
			return new ArrayList<>();
		}

		if(!this.azioniSelectItemsWidthCheck){
			this.azioni = new ArrayList<>();
			try {
				String tipoProtocollo = this.search.getProtocollo();

				//ricerca per API
				if(StringUtils.isNotBlank(this.search.getApi())) {
					String apiSearch = this.search.getApi();
					IDAccordo idAccordo = IDAccordoFactory.getInstance().getIDAccordoFromUri(apiSearch);
					
					String nome = idAccordo.getNome();
					Integer versione = idAccordo.getVersione();
					String tipoReferente = idAccordo.getSoggettoReferente().getTipo();
					String nomeReferente = idAccordo.getSoggettoReferente().getNome();
	
					this.azioni = this.dynamicUtils.getListaSelectItemsAzioniFromAPI(tipoProtocollo, nome, tipoReferente, nomeReferente, versione,input);
				} else if(StringUtils.isNotBlank(this.search.getNomeServizio())) { 				// ricerca per servizio
					IDServizio idServizio = Utility.parseServizioSoggetto(this.search.getNomeServizio());
	
					String nomeServizio = idServizio.getNome();
					String tipoServizio = idServizio.getTipo();
					String nomeErogatore = idServizio.getSoggettoErogatore().getNome();
					String tipoErogatore = idServizio.getSoggettoErogatore().getTipo();
					Integer versioneServizio = idServizio.getVersione();
	
					this.azioni = this.dynamicUtils.getListaSelectItemsAzioniFromServizio(tipoProtocollo, tipoServizio, nomeServizio, tipoErogatore, nomeErogatore, versioneServizio,input);
				}

			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}

			// Aggiorno le dimensioni della selectlist
			Integer lunghezzaSelectList = this.dynamicUtils.getLunghezzaSelectList(this.azioni);
			this.azioniSelectItemsWidth = Math.max(this.azioniSelectItemsWidth,  lunghezzaSelectList);
		}
		return this.azioni;
	}
	
	public List<org.openspcoop2.web.monitor.core.bean.SelectItem> apiAutoComplete(Object val) {
		List<org.openspcoop2.web.monitor.core.bean.SelectItem> listaApi = new ArrayList<>();
		List<SelectItem> listaApiTmp = new ArrayList<>();
		if(val!=null && !StringUtils.isEmpty((String)val)) {
			listaApiTmp = this.getApiListEngine((String)val);
		}
		
		listaApiTmp.add(0, new SelectItem("--", "--"));
		
		for (SelectItem selectItem : listaApiTmp) {
			String label = selectItem.getLabel();
			String value = (String) selectItem.getValue();
			
			org.openspcoop2.web.monitor.core.bean.SelectItem newItem = new org.openspcoop2.web.monitor.core.bean.SelectItem(value, label);
			listaApi.add(newItem);
		}

		return listaApi;
	}
	
	public List<SelectItem> getApiList() {
		return getApiListEngine(null);		

	}

	protected List<SelectItem> getApiListEngine(String input) {
		if(this.search==null){
			return new ArrayList<>();
		}
		if(!this.apiSelectItemsWidthCheck){
			this.api = new ArrayList<>();

			// per adesso non lo aggancio
			String tipoSoggettoReferente = null;
			String nomeSoggettoReferente = null;
			boolean isReferente = false;
			
			String tipoProtocollo = this.search.getProtocollo();
			
			String gruppo = this.search.getGruppo();
			
			this.api = this.dynamicUtils.getListaSelectItemsAccordiServizio(tipoProtocollo, tipoSoggettoReferente, nomeSoggettoReferente, 
					isReferente, false, gruppo,
					input);
			
			Integer lunghezzaSelectList = this.dynamicUtils.getLunghezzaSelectList(this.api);
			this.apiSelectItemsWidth = Math.max(this.apiSelectItemsWidth,  lunghezzaSelectList);
		}
		return this.api;
	}
	
	
	public List<org.openspcoop2.web.monitor.core.bean.SelectItem> serviziAutoComplete(Object val) throws CoreException {
		List<org.openspcoop2.web.monitor.core.bean.SelectItem> listaServizi = new ArrayList<>();
		List<SelectItem> listaServiziTmp = new ArrayList<>();
		if(val!=null && !StringUtils.isEmpty((String)val)) {
			listaServiziTmp = this.getServiziEngine((String)val);
		}
		
		listaServiziTmp.add(0, new SelectItem("--", "--"));
		
		for (SelectItem selectItem : listaServiziTmp) {
			String label = selectItem.getLabel();
			String value = (String) selectItem.getValue();
			
			org.openspcoop2.web.monitor.core.bean.SelectItem newItem = new org.openspcoop2.web.monitor.core.bean.SelectItem(value, label);
			listaServizi.add(newItem);
		}

		return listaServizi;
	}

	public void setServizi(List<SelectItem> servizi) {
		this.servizi = servizi;
	}
	public List<SelectItem> getServiziRawField() {
		return this.servizi;
	}
	public List<SelectItem> getServizi() throws CoreException {
		return getServiziEngine(null);		
	}

	protected List<SelectItem> getServiziEngine(String input) throws CoreException {
		if(this.search==null){
			return new ArrayList<>();
		}
		if(!this.serviziSelectItemsWidthCheck){
			this.servizi = new ArrayList<>();
			Soggetto erogatore = getSoggettoErogatoreEngine( );

			String tipoSoggetto =  null;
			String nomeSoggetto = null;

			if(erogatore != null){
				tipoSoggetto = erogatore.getTipoSoggetto();
				nomeSoggetto = erogatore.getNomeSoggetto();
			}

			String tipoProtocollo = this.search.getProtocollo();
			
			String gruppo = this.search.getGruppo();
			
			IDAccordo idAccordo = null;
			String searchApi = this.search.getApi();
			if((searchApi!=null && !"".equals(searchApi)) ) {
				try {
					idAccordo = IDAccordoFactory.getInstance().getIDAccordoFromUri(searchApi);
				}catch(Exception e) {
					throw new CoreException(e.getMessage(),e);
				}
			}
			
			if (TipologiaRicerca.uscita.equals(this.search.getTipologiaRicercaEnum())) {
				this.servizi = this.dynamicUtils.getListaSelectItemsElencoServiziFruizione(tipoProtocollo, gruppo, idAccordo, tipoSoggetto, nomeSoggetto,input, true);
			} else if (TipologiaRicerca.ingresso.equals(this.search.getTipologiaRicercaEnum())) {
				this.servizi = this.dynamicUtils.getListaSelectItemsElencoServiziErogazione(tipoProtocollo, gruppo, idAccordo, tipoSoggetto, nomeSoggetto,input, true);
			} else {
				this.servizi = this.dynamicUtils.getListaSelectItemsElencoServiziFromAccordoAndSoggettoErogatore(tipoProtocollo,gruppo, idAccordo, null, tipoSoggetto, nomeSoggetto, input);
			}
			
			
			Integer lunghezzaSelectList = this.dynamicUtils.getLunghezzaSelectList(this.servizi);
			this.serviziSelectItemsWidth = Math.max(this.serviziSelectItemsWidth,  lunghezzaSelectList);
		}
		return this.servizi;
	}

	public List<SelectItem> getSoggetti(boolean includiOperativi,boolean includiEsterni, String input) throws CoreException {
		return this.getSoggetti(includiOperativi, includiEsterni, false, input);
	}
	public List<SelectItem> getSoggetti(boolean includiOperativi,boolean includiEsterni, boolean escludiSoggettoSelezionato, String input) throws CoreException {
		if(this.search==null){
			return new ArrayList<>();
		}
		
		if(!this.soggettiSelectItemsWidthCheck){
			this.soggetti = new ArrayList<>();

			String tipoProtocollo = this.search.getProtocollo();
			String idPorta = null;
			List<Soggetto> list = this.dynamicUtilsService.findElencoSoggetti(tipoProtocollo ,idPorta, input);

			for (Soggetto soggetto : list) {
				boolean add = true;
				if(!(includiOperativi && includiEsterni)) { 
					if(includiOperativi) {
						String nomePddFromSoggetto = this.dynamicUtils.getServerFromSoggetto(soggetto.getTipoSoggetto(), soggetto.getNomeSoggetto());
						add = this.dynamicUtils.checkTipoPdd(nomePddFromSoggetto, TipoPdD.OPERATIVO);
					} 
					if(includiEsterni) {
						String nomePddFromSoggetto = this.dynamicUtils.getServerFromSoggetto(soggetto.getTipoSoggetto(), soggetto.getNomeSoggetto());
						add = this.dynamicUtils.checkTipoPdd(nomePddFromSoggetto, TipoPdD.ESTERNO);
					}
				}
				
				if(escludiSoggettoSelezionato &&
					this.search!=null && this.search.getUser()!=null) {
					String soggettoSelezionato = this.search.getUser().getSoggettoSelezionatoPddMonitor();
					if(soggettoSelezionato==null || "".equals(soggettoSelezionato)) {
						soggettoSelezionato = this.search.getTipoNomeSoggettoLocale();
					}
					if(soggettoSelezionato!=null && !"".equals(soggettoSelezionato) && soggettoSelezionato.contains("/")) {
						String tipo = soggettoSelezionato.split("/")[0];
						String nome = soggettoSelezionato.split("/")[1];
						if(tipo.equals(soggetto.getTipoSoggetto()) && nome.equals(soggetto.getNomeSoggetto())) {
							add = false;
						}
					}
				}
				
				if(add) {				
					String value = soggetto.getTipoSoggetto() + "/" + soggetto.getNomeSoggetto();
					IDSoggetto idSoggetto = new IDSoggetto(soggetto.getTipoSoggetto(), soggetto.getNomeSoggetto());
					try {
						String label = tipoProtocollo != null ? NamingUtils.getLabelSoggetto(tipoProtocollo,idSoggetto) : NamingUtils.getLabelSoggetto(idSoggetto);
						this.soggetti.add(new SelectItem(value,label));
					}catch(Exception e) {
						throw new CoreException(e.getMessage(),e);
					}
				}
			}
			Integer lunghezzaSelectList = this.dynamicUtils.getLunghezzaSelectList(this.soggetti);
			this.soggettiSelectItemsWidth = Math.max(this.soggettiSelectItemsWidth,  lunghezzaSelectList);
		}
		return this.soggetti;
	}
	
	public List<org.openspcoop2.web.monitor.core.bean.SelectItem> soggettiLocaleAutoComplete(Object val) throws CoreException {
		List<SelectItem> listaSoggettiTmp = new ArrayList<>();
		if(val!=null && !StringUtils.isEmpty((String)val)) {
			listaSoggettiTmp = this.getSoggettiLocale(true, false, (String)val);
		}
		listaSoggettiTmp.add(0, new SelectItem("--", "--"));
		
		List<org.openspcoop2.web.monitor.core.bean.SelectItem> listaSoggetti = new ArrayList<>();
		
		for (SelectItem selectItem : listaSoggettiTmp) {
			String label = selectItem.getLabel();
			String value = (String) selectItem.getValue();
			
			org.openspcoop2.web.monitor.core.bean.SelectItem newItem = new org.openspcoop2.web.monitor.core.bean.SelectItem(value, label);
			listaSoggetti.add(newItem);
		}

		return listaSoggetti;
	}

	public List<SelectItem> getSoggettiLocale() throws CoreException {
		return getSoggettiLocale(true,false,null);
	}

	public List<SelectItem> getSoggettiLocale(boolean includiOperativi,boolean includiEsterni, String input) throws CoreException {
		if(this.search==null){
			return new ArrayList<>();
		}
		
		if(!this.soggettiLocaleSelectItemsWidthCheck){
			this.soggettiLocale = new ArrayList<>();

			String tipoProtocollo = this.search.getProtocollo();
			String idPorta = null;
			List<Soggetto> list = this.dynamicUtilsService.findElencoSoggetti(tipoProtocollo ,idPorta, input);

			for (Soggetto soggetto : list) {
				boolean add = true;
				if(!(includiOperativi && includiEsterni)) { 
					if(includiOperativi) {
						String nomePddFromSoggetto = this.dynamicUtils.getServerFromSoggetto(soggetto.getTipoSoggetto(), soggetto.getNomeSoggetto());
						add = this.dynamicUtils.checkTipoPdd(nomePddFromSoggetto, TipoPdD.OPERATIVO);
					} 
					if(includiEsterni) {
						String nomePddFromSoggetto = this.dynamicUtils.getServerFromSoggetto(soggetto.getTipoSoggetto(), soggetto.getNomeSoggetto());
						add = this.dynamicUtils.checkTipoPdd(nomePddFromSoggetto, TipoPdD.ESTERNO);
					}
				}
				
				if(add) {				
					String value = soggetto.getTipoSoggetto() + "/" + soggetto.getNomeSoggetto();
					IDSoggetto idSoggetto = new IDSoggetto(soggetto.getTipoSoggetto(), soggetto.getNomeSoggetto());
					try {
						String label = tipoProtocollo != null ? NamingUtils.getLabelSoggetto(tipoProtocollo,idSoggetto) : NamingUtils.getLabelSoggetto(idSoggetto);
						this.soggettiLocale.add(new SelectItem(value,label));
					}catch(Exception e) {
						throw new CoreException(e.getMessage(),e);
					}
				}
			}
			Integer lunghezzaSelectList = this.dynamicUtils.getLunghezzaSelectList(this.soggettiLocale);
			this.soggettiLocaleSelectItemsWidth = Math.max(this.soggettiLocaleSelectItemsWidth,  lunghezzaSelectList);
		}
		return this.soggettiLocale;
	}

	/***
	 * 
	 * Implementazione base, ripresa dalla vecchia versione imposta il soggetto erogatore in base alla tipologia di ricerca effettuata
	 * 
	 * 	- ingresso : erogatore viene preso dal fiel soggettoLocale;
	 *  - uscita: erogatore preso dal soggettoDestinatario
	 *  - All non c'e' filtro sui servizi  
	 * 
	 * @return Soggetto erogatore
	 */
	protected Soggetto getSoggettoErogatoreEngine() {
		Soggetto erogatore = null;
		// ingresso
		if (TipologiaRicerca.ingresso.equals(this.search.getTipologiaRicercaEnum())) {
			if (StringUtils.isNotBlank(this.search.getSoggettoLocale())) {
				// recuper soggetto erogatore
				erogatore = this.dynamicUtilsService.findSoggettoByTipoNome(
						this.search.getTipoSoggettoLocale(),
						this.search.getSoggettoLocale());
			}
		} else if (TipologiaRicerca.uscita.equals(this.search.getTipologiaRicercaEnum()) &&
			// uscita
			StringUtils.isNotBlank(this.search.getNomeDestinatario())) {
			// recuper soggetto erogatore
			erogatore = this.dynamicUtilsService.findSoggettoByTipoNome(
					this.search.getTipoDestinatario(),
					this.search.getNomeDestinatario());
		}
		return erogatore;
	}

	public List<SelectItem> getServiziApplicativi() {
		if(this.search==null){
			return new ArrayList<>();
		}
		if(!this.serviziApplicativiSelectItemsWidthCheck){
			this.serviziApplicativi = new ArrayList<>();
			
			String tipoProtocollo =   this.search.getProtocollo();
			
			String nomeSoggetto = null;
			String tipoSoggetto = null;
			
			if( // StringUtils.isNotBlank(this.search.getTipoNomeMittente()) && 
					TipologiaRicerca.ingresso.equals(this.search.getTipologiaRicercaEnum()) &&
					StringUtils.isNotEmpty(this.search.getRiconoscimento()) &&
					this.search.getRiconoscimento().equals(org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_APPLICATIVO) 
				) {
				if( StringUtils.isNotBlank(this.search.getTipoNomeMittente()) ) {
					tipoSoggetto = this.search.getTipoMittente();
					nomeSoggetto = this.search.getNomeMittente();
				}
			}
			else if (StringUtils.isNotBlank(this.search.getSoggettoLocale()) ) {
				tipoSoggetto = this.search.getTipoSoggettoLocale();
				nomeSoggetto = this.search.getSoggettoLocale();
			}
			else {
				boolean multiTenant = Utility.isMultitenantAbilitato();
				if(!multiTenant) {
					List<Soggetto> lista = this.dynamicUtils.getListaSoggetti(tipoProtocollo, TipoPdD.OPERATIVO);
					if(lista!=null && lista.size()==1) { // se maggiore di 1 e' saltato il multitenat
						nomeSoggetto = lista.get(0).getNomeSoggetto();
						tipoSoggetto = lista.get(0).getTipoSoggetto();
					}
				}
			}

			if(tipoSoggetto!=null && nomeSoggetto!=null && StringUtils.isNotEmpty(this.search.getIdentificazione())) {
				
				boolean trasporto = Costanti.IDENTIFICAZIONE_TRASPORTO_KEY.equals(this.search.getIdentificazione());
				boolean token = Costanti.IDENTIFICAZIONE_TOKEN_KEY.equals(this.search.getIdentificazione());
				
				this.serviziApplicativi = this.dynamicUtils.getListaSelectItemsServiziApplicativiFromSoggettoLocale(tipoProtocollo,tipoSoggetto, nomeSoggetto, trasporto, token);
			}
			Integer lunghezzaSelectList = this.dynamicUtils.getLunghezzaSelectList(this.serviziApplicativi);
			this.serviziApplicativiSelectItemsWidth = Math.max(this.serviziApplicativiSelectItemsWidth,  lunghezzaSelectList);
		}

		return this.serviziApplicativi;
	}
	
	
	public List<org.openspcoop2.web.monitor.core.bean.SelectItem> soggettiAutoComplete(Object val) throws CoreException {
		List<org.openspcoop2.web.monitor.core.bean.SelectItem> listaSoggetti = new ArrayList<>();
		List<SelectItem> listaSoggettiTmp = new ArrayList<>();
		if(val!=null && !StringUtils.isEmpty((String)val)) {
			if(this.search!=null &&
					TipologiaRicerca.ingresso.equals(this.search.getTipologiaRicercaEnum()) && 
					StringUtils.isNotEmpty(this.search.getRiconoscimento()) && 
					this.search.getRiconoscimento().equals(org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_APPLICATIVO)) {
					listaSoggettiTmp = getSoggetti(true, false, false, (String)val);
			}
			else {
				ConfigurazioneSoggettiVisualizzatiSearchForm config = Utility.getMultitenantAbilitatoSoggettiConfig(this.search!=null ? this.search.getTipologiaRicercaEnum() : null);
				listaSoggettiTmp = this.getSoggetti(config.isIncludiSoloOperativi(), config.isIncludiSoloEsterni(), config.isEscludiSoggettoSelezionato(), (String)val);
			}
		}
		
		listaSoggettiTmp.add(0, new SelectItem("--", "--"));
		
		for (SelectItem selectItem : listaSoggettiTmp) {
			String label = selectItem.getLabel();
			String value = (String) selectItem.getValue();
			
			org.openspcoop2.web.monitor.core.bean.SelectItem newItem = new org.openspcoop2.web.monitor.core.bean.SelectItem(value, label);
			listaSoggetti.add(newItem);
		}

		return listaSoggetti;
	}

	public List<SelectItem> getSoggetti() throws CoreException{
		if(this.search!=null &&
				TipologiaRicerca.ingresso.equals(this.search.getTipologiaRicercaEnum()) && 
				StringUtils.isNotEmpty(this.search.getRiconoscimento()) && 
				this.search.getRiconoscimento().equals(org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_APPLICATIVO)) {
			
			try {
				boolean isSupportataAutenticazioneApplicativiEsterni = this.dynamicUtils.isSupportataAutenticazioneApplicativiEsterniErogazione(this.search.getProtocollo());
				return getSoggetti(true, isSupportataAutenticazioneApplicativiEsterni, false, null);
			}catch(Exception e) {
				throw new CoreException(e.getMessage(),e);
			}
		}
		
		ConfigurazioneSoggettiVisualizzatiSearchForm config = Utility.getMultitenantAbilitatoSoggettiConfig(this.search!=null ? this.search.getTipologiaRicercaEnum() : null);
		return getSoggetti(config.isIncludiSoloOperativi(), config.isIncludiSoloEsterni(), config.isEscludiSoggettoSelezionato(),null);
	}

	public List<SelectItem> getTipiNomiSoggettiAssociati() throws CoreException {
		return getTipiNomiSoggettiAssociati(false);
	}

	public List<SelectItem> getTipiNomiSoggettiAssociati(boolean soloOperativi) throws CoreException {
		if(this.search==null){
			return new ArrayList<>();
		}

		if(!this.soggettiAssociatiSelectItemsWidthCheck){
			this.soggettiAssociati = new ArrayList<>();

			UserDetailsBean loggedUser = Utility.getLoggedUser();
			if(loggedUser!=null){
				List<IDSoggetto> lst = new ArrayList<>();
				String tipoProtocollo = this.search.getProtocollo();
				if(tipoProtocollo == null) {
					lst = loggedUser.getUtenteSoggettoList();
				} else {
					// se ho selezionato un protocollo devo filtrare per protocollo
					List<IDSoggetto> tipiNomiSoggettiAssociati = loggedUser.getUtenteSoggettoProtocolliMap().containsKey(tipoProtocollo) ? loggedUser.getUtenteSoggettoProtocolliMap().get(tipoProtocollo) : new ArrayList<>();
					List<String> lstTmp = new ArrayList<>();
					if(tipiNomiSoggettiAssociati !=null && !tipiNomiSoggettiAssociati.isEmpty())
						for (IDSoggetto utenteSoggetto : tipiNomiSoggettiAssociati) {
							String tipoNome = utenteSoggetto.getTipo() + "/" + utenteSoggetto.getNome();
							boolean add = true;
							if(soloOperativi) {
								String nomePddFromSoggetto = this.dynamicUtils.getServerFromSoggetto(utenteSoggetto.getTipo(), utenteSoggetto.getNome());
								add = this.dynamicUtils.checkTipoPdd(nomePddFromSoggetto, TipoPdD.OPERATIVO);
							}
							if(!lstTmp.contains(tipoNome) && add){
								lstTmp.add(tipoNome);
								lst.add(utenteSoggetto);
							}
						}
				}

				for (IDSoggetto idSoggetto : lst) {
					String value = idSoggetto.getTipo() + "/" + idSoggetto.getNome();
					try {
						String label = tipoProtocollo != null ? NamingUtils.getLabelSoggetto(tipoProtocollo,idSoggetto) : NamingUtils.getLabelSoggetto(idSoggetto);
						this.soggettiAssociati.add(new SelectItem(value,label));
					}catch(Exception e) {
						throw new CoreException(e.getMessage(),e);
					}
				}
			}
		}

		return this.soggettiAssociati;
	}


	public String getSoggettiAssociatiSelectItemsWidth() throws CoreException {
		this.soggettiAssociatiSelectItemsWidthCheck = false;
		getTipiNomiSoggettiAssociati();
		this.soggettiAssociatiSelectItemsWidthCheck = true;
		return checkWidthLimits(this.soggettiAssociatiSelectItemsWidth).toString();
	}

	public String getSoggettiLocaleSelectItemsWidth() throws CoreException{
		this.soggettiLocaleSelectItemsWidthCheck = false;
		getSoggettiLocale();
		this.soggettiLocaleSelectItemsWidthCheck = true;
		return checkWidthLimits(this.soggettiLocaleSelectItemsWidth).toString();
	}

	public String getSoggettiSelectItemsWidth() throws CoreException {
		this.soggettiSelectItemsWidthCheck = false;
		getSoggetti();
		this.soggettiSelectItemsWidthCheck = true;
		return checkWidthLimits(this.soggettiSelectItemsWidth).toString();
	}

	public String getGruppiSelectItemsWidth() {
		this.gruppiSelectItemsWidthCheck = false;
		getGruppi();
		this.gruppiSelectItemsWidthCheck = true;
		return checkWidthLimits(this.gruppiSelectItemsWidth).toString();
	}
	
	public String getApiSelectItemsWidth() {
		this.apiSelectItemsWidthCheck = false;
		getApiList();
		this.apiSelectItemsWidthCheck = true;
		return checkWidthLimits(this.apiSelectItemsWidth).toString();
	}
	
	public String getServiziSelectItemsWidth() throws CoreException {
		this.serviziSelectItemsWidthCheck = false;
		getServizi();
		this.serviziSelectItemsWidthCheck = true;
		return checkWidthLimits(this.serviziSelectItemsWidth).toString();
	}

	public String getServiziApplicativiSelectItemsWidth() {
		this.serviziApplicativiSelectItemsWidthCheck = false;
		getServiziApplicativi();
		this.serviziApplicativiSelectItemsWidthCheck = true;
		return checkWidthLimits(this.serviziApplicativiSelectItemsWidth).toString();
	}

	public String getAzioniSelectItemsWidth(){
		this.azioniSelectItemsWidthCheck = false;
		getAzioni();
		this.azioniSelectItemsWidthCheck = true;
		return checkWidthLimits(this.azioniSelectItemsWidth).toString();
	}

	public Integer checkWidthLimits(Integer value){
		// valore deve essere compreso minore del max ma almeno quanto la default
		Integer toRet = Math.max(this.defaultSelectItemsWidth, value);

		toRet = Math.min(toRet, this.maxSelectItemsWidth);

		return toRet;
	}

	public boolean isSoggettiLocaleSelectItemsWidthCheck() {
		return this.soggettiLocaleSelectItemsWidthCheck;
	}

	public void setSoggettiLocaleSelectItemsWidthCheck(boolean soggettiLocaleSelectItemsWidthCheck) {
		this.soggettiLocaleSelectItemsWidthCheck = soggettiLocaleSelectItemsWidthCheck;
	}

	public boolean isSoggettiAssociatiSelectItemsWidthCheck() {
		return this.soggettiAssociatiSelectItemsWidthCheck;
	}

	public void setSoggettiAssociatiSelectItemsWidthCheck(boolean soggettiAssociatiSelectItemsWidthCheck) {
		this.soggettiAssociatiSelectItemsWidthCheck = soggettiAssociatiSelectItemsWidthCheck;
	}

	public boolean isSoggettiSelectItemsWidthCheck() {
		return this.soggettiSelectItemsWidthCheck;
	}

	public void setSoggettiSelectItemsWidthCheck(boolean soggettiSelectItemsWidthCheck) {
		this.soggettiSelectItemsWidthCheck = soggettiSelectItemsWidthCheck;
	}

	public boolean isGruppiSelectItemsWidthCheck() {
		return this.gruppiSelectItemsWidthCheck;
	}

	public void setGruppiSelectItemsWidthCheck(boolean gruppiSelectItemsWidthCheck) {
		this.gruppiSelectItemsWidthCheck = gruppiSelectItemsWidthCheck;
	}
	
	public boolean isApiSelectItemsWidthCheck() {
		return this.apiSelectItemsWidthCheck;
	}

	public void setApiSelectItemsWidthCheck(boolean apiSelectItemsWidthCheck) {
		this.apiSelectItemsWidthCheck = apiSelectItemsWidthCheck;
	}
	
	public boolean isServiziSelectItemsWidthCheck() {
		return this.serviziSelectItemsWidthCheck;
	}

	public void setServiziSelectItemsWidthCheck(boolean serviziSelectItemsWidthCheck) {
		this.serviziSelectItemsWidthCheck = serviziSelectItemsWidthCheck;
	}

	public boolean isServiziApplicativiSelectItemsWidthCheck() {
		return this.serviziApplicativiSelectItemsWidthCheck;
	}

	public void setServiziApplicativiSelectItemsWidthCheck(
			boolean serviziApplicativiSelectItemsWidthCheck) {
		this.serviziApplicativiSelectItemsWidthCheck = serviziApplicativiSelectItemsWidthCheck;
	}

	public boolean isAzioniSelectItemsWidthCheck() {
		return this.azioniSelectItemsWidthCheck;
	}

	public void setAzioniSelectItemsWidthCheck(boolean azioniSelectItemsWidthCheck) {
		this.azioniSelectItemsWidthCheck = azioniSelectItemsWidthCheck;
	}

	public Integer getDefaultSelectItemsWidth() {
		return this.defaultSelectItemsWidth;
	}

	public void setDefaultSelectItemsWidth(Integer defaultSelectItemsWidth) {
		this.defaultSelectItemsWidth = defaultSelectItemsWidth;
	}

	public void setGruppiSelectItemsWidth(Integer gruppiSelectItemsWidth) {
		this.gruppiSelectItemsWidth = gruppiSelectItemsWidth;
	}
	
	public void setApiSelectItemsWidth(Integer apiSelectItemsWidth) {
		this.apiSelectItemsWidth = apiSelectItemsWidth;
	}
	
	public void setServiziSelectItemsWidth(Integer serviziSelectItemsWidth) {
		this.serviziSelectItemsWidth = serviziSelectItemsWidth;
	}

	public void setServiziApplicativiSelectItemsWidth(
			Integer serviziApplicativiSelectItemsWidth) {
		this.serviziApplicativiSelectItemsWidth = serviziApplicativiSelectItemsWidth;
	}

	public void setAzioniSelectItemsWidth(Integer azioniSelectItemsWidth) {
		this.azioniSelectItemsWidth = azioniSelectItemsWidth;
	}

	public Integer getSoggettiLocaleSelectItemsWidthAsInteger() {
		return checkWidthLimits(this.soggettiLocaleSelectItemsWidth);
	}

	public Integer getSoggettiAssociatiSelectItemsWidthAsInteger() {
		return checkWidthLimits(this.soggettiAssociatiSelectItemsWidth);
	}

	public Integer getSoggettiSelectItemsWidthAsInteger() {
		return checkWidthLimits(this.soggettiSelectItemsWidth);
	}

	public Integer getGruppiSelectItemsWidthAsInteger() {
		return checkWidthLimits(this.gruppiSelectItemsWidth);
	}
	
	public Integer getApiSelectItemsWidthAsInteger() {
		return checkWidthLimits(this.apiSelectItemsWidth);
	}
	
	public Integer getServiziSelectItemsWidthAsInteger() {
		return checkWidthLimits(this.serviziSelectItemsWidth);
	}

	public Integer getServiziApplicativiSelectItemsWidthAsInteger() {
		return checkWidthLimits(this.serviziApplicativiSelectItemsWidth);
	}

	public Integer getAzioniSelectItemsWidthAsInteger(){
		return checkWidthLimits(this.azioniSelectItemsWidth);
	}

	@Override
	public String getProtocollo() {
		if(this.search == null)
			return super.getProtocollo();
		else 
			return this.search.getProtocollo();
	}
}

