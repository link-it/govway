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
package org.openspcoop2.web.monitor.core.mbean;

//import java.awt.Canvas;
//import java.awt.Font;
//import java.awt.FontMetrics;
import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.commons.search.Soggetto;
import org.openspcoop2.core.commons.search.constants.TipoPdD;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.protocol.engine.utils.NamingUtils;
import org.openspcoop2.web.monitor.core.bean.BaseSearchForm;
import org.openspcoop2.web.monitor.core.bean.UserDetailsBean;
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
public class DynamicPdDBean<T,K,ServiceType extends IService> extends PdDBaseBean<T, K, ServiceType>{


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L; 

	protected static Logger log =  LoggerManager.getPddMonitorCoreLogger();

	protected List<SelectItem> soggettiAssociati = null;
	protected List<SelectItem> soggettiLocale = null;
	protected List<SelectItem> soggetti = null;
	protected List<SelectItem> gruppi = null;
	protected List<SelectItem> api = null;
	protected List<SelectItem> servizi = null;
	protected List<SelectItem> azioni = null;
	protected List<SelectItem> serviziApplicativi = null;

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

	//	private static FontMetrics fm = null;

	protected DynamicPdDBeanUtils dynamicUtils = null;

//	protected boolean showAccordoOnServizioLabel = false;
//	protected boolean showTipoServizioOnServizioLabel =true;
//	protected boolean showErogatoreOnServizioLabel = true;

	//	public static Integer getFontWidth(String label){
	//		if(fm == null){
	//			Canvas c = new Canvas();
	//			Font verdanaFont = new Font("Verdana", Font.PLAIN , 11);
	//			fm = c.getFontMetrics(verdanaFont);
	//		}
	//
	//		return fm.stringWidth(label);
	//	}

	public DynamicPdDBean(){
		super();
		try {
			this.dynamicUtils = new DynamicPdDBeanUtils(log);
		} catch (Exception e) {
			DynamicPdDBean.log.error("lettura delle properties fallita.....",
					e);
		}
	}
	public DynamicPdDBean(org.openspcoop2.core.commons.search.dao.IServiceManager serviceManager){
		super(serviceManager);
		try {
			this.dynamicUtils = new DynamicPdDBeanUtils(serviceManager,log);
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
	
	
	public List<org.openspcoop2.web.monitor.core.bean.SelectItem> gruppiAutoComplete(Object val) throws Exception{
		List<org.openspcoop2.web.monitor.core.bean.SelectItem> listaGruppi = new ArrayList<org.openspcoop2.web.monitor.core.bean.SelectItem>();
		List<SelectItem> listaGruppiTmp = new ArrayList<>();
		if(val==null || StringUtils.isEmpty((String)val)) {
		}else{
			listaGruppiTmp = this._getGruppi((String)val);
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

	public List<SelectItem> getGruppi() throws Exception {
		return _getGruppi(null);		

	}

	protected List<SelectItem> _getGruppi(String input) throws Exception {
		if(this.search==null){
			return new ArrayList<SelectItem>();
		}
		if(!this.gruppiSelectItemsWidthCheck){
			this.gruppi = new ArrayList<SelectItem>();
			
			String tipoProtocollo = this.search.getProtocollo();
			this.gruppi = this.dynamicUtils.getListaGruppi(tipoProtocollo);
			
			Integer lunghezzaSelectList = this.dynamicUtils.getLunghezzaSelectList(this.gruppi);
			this.gruppiSelectItemsWidth = Math.max(this.gruppiSelectItemsWidth,  lunghezzaSelectList);
		}
		return this.gruppi;
	}
	
	
	public List<org.openspcoop2.web.monitor.core.bean.SelectItem> azioniAutoComplete(Object val) throws Exception{
		List<org.openspcoop2.web.monitor.core.bean.SelectItem> listaAzioni = new ArrayList<org.openspcoop2.web.monitor.core.bean.SelectItem>();
		List<SelectItem> listaAzioniTmp = new ArrayList<>();
		if(val==null || StringUtils.isEmpty((String)val)) {
		}else{
			listaAzioniTmp = this._getAzioni((String)val);
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
		return _getAzioni(null);
	}

	protected List<SelectItem> _getAzioni(String input) {
		if(this.search==null){
			return new ArrayList<SelectItem>();
		}
		if(this.search.getNomeServizio()==null){
			return new ArrayList<SelectItem>();
		}
		if (this.search != null
				&& StringUtils.isBlank(this.search.getNomeServizio()))
			return new ArrayList<SelectItem>();

		if(!this.azioniSelectItemsWidthCheck){
			this.azioni = new ArrayList<SelectItem>();
			try {
				String tipoProtocollo = this.search.getProtocollo();
				IDServizio idServizio = Utility.parseServizioSoggetto(this.search.getNomeServizio());

				String nomeServizio = idServizio.getNome();
				String tipoServizio = idServizio.getTipo();
				String nomeErogatore = idServizio.getSoggettoErogatore().getNome();
				String tipoErogatore = idServizio.getSoggettoErogatore().getTipo();
				Integer versioneServizio = idServizio.getVersione();

				this.azioni = this.dynamicUtils.getListaSelectItemsAzioniFromServizio(tipoProtocollo, tipoServizio, nomeServizio, tipoErogatore, nomeErogatore, versioneServizio,input);

			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}

			// Aggiorno le dimensioni della selectlist
			Integer lunghezzaSelectList = this.dynamicUtils.getLunghezzaSelectList(this.azioni);
			this.azioniSelectItemsWidth = Math.max(this.azioniSelectItemsWidth,  lunghezzaSelectList);
		}
		return this.azioni;
	}
	
	public List<org.openspcoop2.web.monitor.core.bean.SelectItem> apiAutoComplete(Object val) throws Exception{
		List<org.openspcoop2.web.monitor.core.bean.SelectItem> listaApi = new ArrayList<org.openspcoop2.web.monitor.core.bean.SelectItem>();
		List<SelectItem> listaApiTmp = new ArrayList<>();
		if(val==null || StringUtils.isEmpty((String)val)) {
		}else{
			listaApiTmp = this._getApiList((String)val);
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
	
	public List<SelectItem> getApiList() throws Exception {
		return _getApiList(null);		

	}

	protected List<SelectItem> _getApiList(String input) throws Exception {
		if(this.search==null){
			return new ArrayList<SelectItem>();
		}
		if(!this.apiSelectItemsWidthCheck){
			this.api = new ArrayList<SelectItem>();

			// per adesso non lo aggancio
			String tipoSoggettoReferente = null;
			String nomeSoggettoReferente = null;
			boolean isReferente = false;
			
			String tipoProtocollo = this.search.getProtocollo();
			
			String gruppo = this.search.getGruppo();
			
			this.api = this.dynamicUtils.getListaSelectItemsAccordiServizio(tipoProtocollo, tipoSoggettoReferente, nomeSoggettoReferente, isReferente, false, gruppo);
			
			Integer lunghezzaSelectList = this.dynamicUtils.getLunghezzaSelectList(this.api);
			this.apiSelectItemsWidth = Math.max(this.apiSelectItemsWidth,  lunghezzaSelectList);
		}
		return this.api;
	}
	
	
	public List<org.openspcoop2.web.monitor.core.bean.SelectItem> serviziAutoComplete(Object val) throws Exception{
		List<org.openspcoop2.web.monitor.core.bean.SelectItem> listaServizi = new ArrayList<org.openspcoop2.web.monitor.core.bean.SelectItem>();
		List<SelectItem> listaServiziTmp = new ArrayList<>();
		if(val==null || StringUtils.isEmpty((String)val)) {
		}else{
			listaServiziTmp = this._getServizi((String)val);
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

	public List<SelectItem> getServizi() throws Exception {
		return _getServizi(null);		

	}

	protected List<SelectItem> _getServizi(String input) throws Exception {
		if(this.search==null){
			return new ArrayList<SelectItem>();
		}
		if(!this.serviziSelectItemsWidthCheck){
			this.servizi = new ArrayList<SelectItem>();
			Soggetto erogatore = _getSoggettoErogatore( );

			String tipoSoggetto =  null;
			String nomeSoggetto = null;

			if(erogatore != null){
				tipoSoggetto = erogatore.getTipoSoggetto();
				nomeSoggetto = erogatore.getNomeSoggetto();
			}

			String tipoProtocollo = this.search.getProtocollo();
			
			String gruppo = this.search.getGruppo();
			
			IDAccordo idAccordo = null;
			String api = this.search.getApi();
			if((api!=null && !"".equals(api)) ) {
				idAccordo = IDAccordoFactory.getInstance().getIDAccordoFromUri(api);
			}
			
			if (TipologiaRicerca.uscita.equals(this.search.getTipologiaRicercaEnum())) {
				this.servizi = this.dynamicUtils.getListaSelectItemsElencoServiziFruizione(tipoProtocollo, gruppo, idAccordo, tipoSoggetto, nomeSoggetto,input, false);
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

	public List<SelectItem> _getSoggetti(boolean includiOperativi,boolean includiEsterni, String input) throws Exception {
		return this._getSoggetti(includiOperativi, includiEsterni, false, input);
	}
	public List<SelectItem> _getSoggetti(boolean includiOperativi,boolean includiEsterni, boolean escludiSoggettoSelezionato, String input) throws Exception {
		if(this.search==null){
			return new ArrayList<SelectItem>();
		}
		
		if(!this.soggettiSelectItemsWidthCheck){
			this.soggetti = new ArrayList<SelectItem>();

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
				
				if(escludiSoggettoSelezionato) {
					if(this.search!=null && this.search.getUser()!=null) {
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
				}
				
				if(add) {				
					String value = soggetto.getTipoSoggetto() + "/" + soggetto.getNomeSoggetto();
					IDSoggetto idSoggetto = new IDSoggetto(soggetto.getTipoSoggetto(), soggetto.getNomeSoggetto());
					String label = tipoProtocollo != null ? NamingUtils.getLabelSoggetto(tipoProtocollo,idSoggetto) : NamingUtils.getLabelSoggetto(idSoggetto);
					this.soggetti.add(new SelectItem(value,label));
				}
			}
			Integer lunghezzaSelectList = this.dynamicUtils.getLunghezzaSelectList(this.soggetti);
			this.soggettiSelectItemsWidth = Math.max(this.soggettiSelectItemsWidth,  lunghezzaSelectList);
		}
		return this.soggetti;
	}
	
	public List<org.openspcoop2.web.monitor.core.bean.SelectItem> soggettiLocaleAutoComplete(Object val) throws Exception{
		List<SelectItem> listaSoggettiTmp = new ArrayList<SelectItem>();
		if(val==null || StringUtils.isEmpty((String)val)) {
		}else{
			listaSoggettiTmp = this._getSoggettiLocale(true, false, (String)val);
		}
		listaSoggettiTmp.add(0, new SelectItem("--", "--"));
		
		List<org.openspcoop2.web.monitor.core.bean.SelectItem> listaSoggetti = new ArrayList<org.openspcoop2.web.monitor.core.bean.SelectItem>();
		
		for (SelectItem selectItem : listaSoggettiTmp) {
			String label = selectItem.getLabel();
			String value = (String) selectItem.getValue();
			
			org.openspcoop2.web.monitor.core.bean.SelectItem newItem = new org.openspcoop2.web.monitor.core.bean.SelectItem(value, label);
			listaSoggetti.add(newItem);
		}

		return listaSoggetti;
	}

	public List<SelectItem> getSoggettiLocale() throws Exception{
		return _getSoggettiLocale(true,false,null);
	}

	public List<SelectItem> _getSoggettiLocale(boolean includiOperativi,boolean includiEsterni, String input) throws Exception {
		if(this.search==null){
			return new ArrayList<SelectItem>();
		}
		
		if(!this.soggettiLocaleSelectItemsWidthCheck){
			this.soggettiLocale = new ArrayList<SelectItem>();

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
					String label = tipoProtocollo != null ? NamingUtils.getLabelSoggetto(tipoProtocollo,idSoggetto) : NamingUtils.getLabelSoggetto(idSoggetto);
					this.soggettiLocale.add(new SelectItem(value,label));
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
	protected Soggetto _getSoggettoErogatore() {
		Soggetto erogatore = null;
		// ingresso
		if (TipologiaRicerca.ingresso.equals(this.search.getTipologiaRicercaEnum())) {
			if (StringUtils.isNotBlank(this.search.getSoggettoLocale())) {
				// recuper soggetto erogatore
				erogatore = this.dynamicUtilsService.findSoggettoByTipoNome(
						this.search.getTipoSoggettoLocale(),
						this.search.getSoggettoLocale());
			}
		} else if (TipologiaRicerca.uscita.equals(this.search.getTipologiaRicercaEnum())) {
			// uscita
			// String sq = "select DISTINCT s.nome, s.accordo from Servizio s ";
			if (StringUtils.isNotBlank(this.search.getNomeDestinatario())) {
				// recuper soggetto erogatore
				erogatore = this.dynamicUtilsService.findSoggettoByTipoNome(
						this.search.getTipoDestinatario(),
						this.search.getNomeDestinatario());
				//
				// // aggiungo condizione alla query
				// sq += "where s.erogatore.id = :id_erogatore";
			}
		}
		return erogatore;
	}

	public List<SelectItem> getServiziApplicativi() throws Exception {
		if(this.search==null){
			return new ArrayList<SelectItem>();
		}
		if(!this.serviziApplicativiSelectItemsWidthCheck){
			this.serviziApplicativi = new ArrayList<SelectItem>();
			
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

			if(tipoSoggetto!=null && nomeSoggetto!=null) {
				this.serviziApplicativi = this.dynamicUtils.getListaSelectItemsServiziApplicativiFromSoggettoLocale(tipoProtocollo,tipoSoggetto, nomeSoggetto);
			}
			Integer lunghezzaSelectList = this.dynamicUtils.getLunghezzaSelectList(this.serviziApplicativi);
			this.serviziApplicativiSelectItemsWidth = Math.max(this.serviziApplicativiSelectItemsWidth,  lunghezzaSelectList);
		}

		return this.serviziApplicativi;
	}
	
	
	public List<org.openspcoop2.web.monitor.core.bean.SelectItem> soggettiAutoComplete(Object val) throws Exception{
		List<org.openspcoop2.web.monitor.core.bean.SelectItem> listaSoggetti = new ArrayList<org.openspcoop2.web.monitor.core.bean.SelectItem>();
		List<SelectItem> listaSoggettiTmp = new ArrayList<>();
		if(val==null || StringUtils.isEmpty((String)val)) {
		}else{
			if(TipologiaRicerca.ingresso.equals(this.search.getTipologiaRicercaEnum()) && 
					StringUtils.isNotEmpty(this.search.getRiconoscimento()) && 
					this.search.getRiconoscimento().equals(org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_APPLICATIVO)) {
					listaSoggettiTmp = _getSoggetti(true, false, false, (String)val);
			}
			else {
				ConfigurazioneSoggettiVisualizzatiSearchForm config = Utility.getMultitenantAbilitato_soggettiConfig(this.search!=null ? this.search.getTipologiaRicercaEnum() : null);
				listaSoggettiTmp = this._getSoggetti(config.isIncludiSoloOperativi(), config.isIncludiSoloEsterni(), config.isEscludiSoggettoSelezionato(), (String)val);
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

	public List<SelectItem> getSoggetti()  throws Exception{
		if(TipologiaRicerca.ingresso.equals(this.search.getTipologiaRicercaEnum()) && 
				StringUtils.isNotEmpty(this.search.getRiconoscimento()) && 
				this.search.getRiconoscimento().equals(org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_APPLICATIVO)) {
				return _getSoggetti(true, false, false, null);
		}
		
		ConfigurazioneSoggettiVisualizzatiSearchForm config = Utility.getMultitenantAbilitato_soggettiConfig(this.search!=null ? this.search.getTipologiaRicercaEnum() : null);
		return _getSoggetti(config.isIncludiSoloOperativi(), config.isIncludiSoloEsterni(), config.isEscludiSoggettoSelezionato(),null);
	}

	public List<SelectItem> getTipiNomiSoggettiAssociati() throws Exception {
		return _getTipiNomiSoggettiAssociati(false);
	}

	public List<SelectItem> _getTipiNomiSoggettiAssociati(boolean soloOperativi) throws Exception {
		if(this.search==null){
			return new ArrayList<SelectItem>();
		}

		if(!this.soggettiAssociatiSelectItemsWidthCheck){
			this.soggettiAssociati = new ArrayList<SelectItem>();

			UserDetailsBean loggedUser = Utility.getLoggedUser();
			if(loggedUser!=null){
				List<IDSoggetto> lst = new ArrayList<IDSoggetto>();
				String tipoProtocollo = this.search.getProtocollo();
				if(tipoProtocollo == null) {
					lst = loggedUser.getUtenteSoggettoList();
				} else {
					// se ho selezionato un protocollo devo filtrare per protocollo
					List<IDSoggetto> tipiNomiSoggettiAssociati = loggedUser.getUtenteSoggettoProtocolliMap().containsKey(tipoProtocollo) ? loggedUser.getUtenteSoggettoProtocolliMap().get(tipoProtocollo) : new ArrayList<>();
					List<String> lstTmp = new ArrayList<String>();
					if(tipiNomiSoggettiAssociati !=null && tipiNomiSoggettiAssociati.size() > 0)
						for (IDSoggetto utenteSoggetto : tipiNomiSoggettiAssociati) {
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

				for (IDSoggetto idSoggetto : lst) {
					String value = idSoggetto.getTipo() + "/" + idSoggetto.getNome();
					String label = tipoProtocollo != null ? NamingUtils.getLabelSoggetto(tipoProtocollo,idSoggetto) : NamingUtils.getLabelSoggetto(idSoggetto);
					this.soggettiAssociati.add(new SelectItem(value,label));
				}
			}
		}

		return this.soggettiAssociati;
	}

//	@Override
//	protected List<Soggetto> _getListaSoggetti(Object val, String tipoProtocollo) {
//		if(this.search==null){
//			return new ArrayList<Soggetto>();
//		}
//		List<Soggetto> list = null;
//		Soggetto s = new Soggetto();
//		s.setNomeSoggetto("--");
//
//		// ricerca soggetti
//		if(val==null || StringUtils.isEmpty((String)val))
//			list = new ArrayList<Soggetto>();
//		else{
//			list = this.dynamicUtilsService.soggettiAutoComplete(tipoProtocollo,(String)val);
//		}
//
//		UserDetailsBean loggedUser = Utility.getLoggedUser();
//		//se non e' admin allora devo controllare i tipi dei soggetti associati
//		if(!loggedUser.isAdmin()){
//			List<IDSoggetto> tipiNomiSoggettiAssociati = loggedUser.getUtenteSoggettoList();
//			if(tipiNomiSoggettiAssociati !=null && tipiNomiSoggettiAssociati.size() > 0){
//				List<Soggetto> listaFiltrata = new ArrayList<Soggetto>();
//				List<String> listaTipi = new ArrayList<String>();
//
//				// controllo soggetto locale
//				String tipoSoggettoLocale = Utility.parseTipoSoggetto(this.search.getTipoNomeSoggettoLocale());
//				String nomeSoggettoLocale = Utility.parseNomeSoggetto(this.search.getTipoNomeSoggettoLocale());
//				// se il tipo soggetto locale e' impostato allora filtro per il tipo compatibile con quello scelto
//				if(nomeSoggettoLocale != null){
//					listaTipi.add(tipoSoggettoLocale);
//				} else {
//
//					// prelevo il tipo dei soggetti compatibili
//					for (IDSoggetto utenteSoggetto : tipiNomiSoggettiAssociati) {
//						if(!listaTipi.contains(utenteSoggetto.getTipo()))
//							listaTipi.add(utenteSoggetto.getTipo());
//					}
//
//				}
//				for (Soggetto soggetto : list) {
//					for (String tipo : listaTipi) {
//						try {
//							if(this.dynamicUtils.isTipoSoggettoCompatibile(tipo, soggetto.getTipoSoggetto()))
//								listaFiltrata.add(soggetto);
//						} catch (Exception e) {
//
//						}
//					}
//				}
//
//				listaFiltrata.add(0,s);
//				return listaFiltrata;
//			}
//		}  
//
//		list.add(0,s);
//		return list;	
//	}

	public String getSoggettiAssociatiSelectItemsWidth() throws Exception{
		this.soggettiAssociatiSelectItemsWidthCheck = false;
		getTipiNomiSoggettiAssociati();
		this.soggettiAssociatiSelectItemsWidthCheck = true;
		return checkWidthLimits(this.soggettiAssociatiSelectItemsWidth).toString();
	}

	public String getSoggettiLocaleSelectItemsWidth() throws Exception{
		this.soggettiLocaleSelectItemsWidthCheck = false;
		getSoggettiLocale();
		this.soggettiLocaleSelectItemsWidthCheck = true;
		return checkWidthLimits(this.soggettiLocaleSelectItemsWidth).toString();
	}

	public String getSoggettiSelectItemsWidth() throws Exception{
		this.soggettiSelectItemsWidthCheck = false;
		getSoggetti();
		this.soggettiSelectItemsWidthCheck = true;
		return checkWidthLimits(this.soggettiSelectItemsWidth).toString();
	}

	public String getGruppiSelectItemsWidth() throws Exception{
		this.gruppiSelectItemsWidthCheck = false;
		getGruppi();
		this.gruppiSelectItemsWidthCheck = true;
		return checkWidthLimits(this.gruppiSelectItemsWidth).toString();
	}
	
	public String getApiSelectItemsWidth() throws Exception{
		this.apiSelectItemsWidthCheck = false;
		getApiList();
		this.apiSelectItemsWidthCheck = true;
		return checkWidthLimits(this.apiSelectItemsWidth).toString();
	}
	
	public String getServiziSelectItemsWidth() throws Exception{
		this.serviziSelectItemsWidthCheck = false;
		getServizi();
		this.serviziSelectItemsWidthCheck = true;
		return checkWidthLimits(this.serviziSelectItemsWidth).toString();
	}

	public String getServiziApplicativiSelectItemsWidth() throws Exception{
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

	public Integer getSoggettiLocaleSelectItemsWidthAsInteger() throws Exception{
		return checkWidthLimits(this.soggettiLocaleSelectItemsWidth);
	}

	public Integer getSoggettiAssociatiSelectItemsWidthAsInteger() throws Exception{
		return checkWidthLimits(this.soggettiAssociatiSelectItemsWidth);
	}

	public Integer getSoggettiSelectItemsWidthAsInteger() throws Exception{
		return checkWidthLimits(this.soggettiSelectItemsWidth);
	}

	public Integer getGruppiSelectItemsWidthAsInteger() throws Exception{
		return checkWidthLimits(this.gruppiSelectItemsWidth);
	}
	
	public Integer getApiSelectItemsWidthAsInteger() throws Exception{
		return checkWidthLimits(this.apiSelectItemsWidth);
	}
	
	public Integer getServiziSelectItemsWidthAsInteger() throws Exception{
		return checkWidthLimits(this.serviziSelectItemsWidth);
	}

	public Integer getServiziApplicativiSelectItemsWidthAsInteger() throws Exception{
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

