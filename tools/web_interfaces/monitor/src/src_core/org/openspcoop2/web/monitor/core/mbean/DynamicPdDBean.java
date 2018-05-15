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
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.web.monitor.core.bean.BaseSearchForm;
import org.openspcoop2.web.monitor.core.bean.UserDetailsBean;
import org.openspcoop2.web.monitor.core.core.Utility;
import org.openspcoop2.web.monitor.core.dao.IService;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;
import org.openspcoop2.web.monitor.core.utils.DynamicPdDBeanUtils;
import org.slf4j.Logger;

@SuppressWarnings("rawtypes")
public class DynamicPdDBean<T,K,ServiceType extends IService> extends PdDBaseBean<T, K, ServiceType>{


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L; 

	protected static Logger log =  LoggerManager.getPddMonitorCoreLogger();

	protected List<SelectItem> soggetti = null;
	protected List<SelectItem> servizi = null;
	protected List<SelectItem> azioni = null;
	protected List<SelectItem> serviziApplicativi = null;

	protected Integer serviziSelectItemsWidth= 0;
	protected Integer serviziApplicativiSelectItemsWidth = 0;
	protected Integer azioniSelectItemsWidth = 0;

	protected boolean serviziSelectItemsWidthCheck = false;
	protected boolean serviziApplicativiSelectItemsWidthCheck = false;
	protected boolean azioniSelectItemsWidthCheck = false;

	protected transient BaseSearchForm search;

	protected Integer maxSelectItemsWidth = 700;

	protected Integer defaultSelectItemsWidth = 412;

//	private static FontMetrics fm = null;

	protected DynamicPdDBeanUtils dynamicUtils = null;

	protected boolean showAccordoOnServizioLabel = false;
	protected boolean showTipoServizioOnServizioLabel =true;
	protected boolean showErogatoreOnServizioLabel = true;

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
			DynamicPdDBean.log.warn("lettura delle properties fallita.....",
					e);
		}
	}

	public void setSearch(BaseSearchForm searc) {
		this.search = searc;
	}

	public BaseSearchForm getSearch() {
		return this.search;
	}

	public List<SelectItem> getAzioni() {
		return _getAzioni(this.showAccordoOnServizioLabel,this.showTipoServizioOnServizioLabel, this.showErogatoreOnServizioLabel);
	}

	protected List<SelectItem> _getAzioni(boolean showAccordo,boolean showTipoServizio, boolean showErogatore) {
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
				IDAccordo idAccordo = null;
				//showAccordo ha la label nel formato AAAAA (URIACCORDO)
				if(showAccordo){
					// nome servizio nella forma nomeAccordo@nomeServizio
					// nome servizio nella forma "nomeServizio (nomeAccordo)"
					String uri = this.search.getNomeServizio().split(" \\(")[1]
							.replace(")", "");
					idAccordo = IDAccordoFactory.getInstance()
							.getIDAccordoFromUri(uri);
				}
				String nomeServizio = this.search.getNomeServizio().split(" \\(")[0];
				String tipoServizio = null;
				// showTipoSoggetto e' true allora la label e' di tipo TIPO/NOME
				if(showTipoServizio){
					tipoServizio = Utility.parseTipoSoggetto(nomeServizio);
					nomeServizio = Utility.parseNomeSoggetto(nomeServizio);
				}
				String tipoProtocollo = this.search.getProtocollo();
				String nomeAzione = null;
				String nomeErogatore = null;
				String tipoErogatore = null;

				if(showErogatore){
					nomeErogatore = this.search.getNomeServizio().split(" \\(")[1]
							.replace(")", "");
					tipoErogatore  = Utility.parseTipoSoggetto(nomeErogatore);
					nomeErogatore = Utility.parseNomeSoggetto(nomeErogatore); 
				}


				if(showAccordo)
					this.azioni = this.dynamicUtils.getListaSelectItemsAzioniFromAccordoServizio(tipoProtocollo,idAccordo,tipoServizio, nomeServizio,tipoErogatore ,nomeErogatore ,nomeAzione);
				else
					this.azioni = this.dynamicUtils.getListaSelectItemsAzioniFromServizio(tipoProtocollo,tipoServizio, nomeServizio,nomeAzione);


			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}

			// Aggiorno le dimensioni della selectlist
			Integer lunghezzaSelectList = this.dynamicUtils.getLunghezzaSelectList(this.azioni);
			this.azioniSelectItemsWidth = Math.max(this.azioniSelectItemsWidth,  lunghezzaSelectList);
		}
		return this.azioni;
	}

	public List<SelectItem> getServizi() throws Exception {
		return _getServizi(this.showAccordoOnServizioLabel, this.showTipoServizioOnServizioLabel);		

	}

	protected List<SelectItem> _getServizi( boolean showAccordo,boolean showTipoServizio) {
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

			String uriAccordo =null;

			String tipoProtocollo = this.search.getProtocollo();
			this.servizi = this.dynamicUtils.getListaSelectItemsElencoServiziFromAccordoAndSoggettoErogatore(tipoProtocollo,uriAccordo, tipoSoggetto, nomeSoggetto,this.showErogatoreOnServizioLabel);
			Integer lunghezzaSelectList = this.dynamicUtils.getLunghezzaSelectList(this.servizi);
			this.serviziSelectItemsWidth = Math.max(this.serviziSelectItemsWidth,  lunghezzaSelectList);
		}
		return this.servizi;
	}

	/***
	 * 
	 * Implementazione base, ripresa dalla vecchia versione imposta il soggetto erogatore in base alla tipologia di ricerca effettuata
	 * 
	 * 	- ingresso : erogatore viene preso dal fiel soggettoLocale;
	 *  - uscita: erogatore preso dal soggettoDestinatario
	 *  - All non c'e' filtro sui servizi  
	 * 
	 * @return
	 */
	protected Soggetto _getSoggettoErogatore() {
		Soggetto erogatore = null;
		// ingresso
		if ("ingresso".equals(this.search.getTipologiaRicerca())) {
			if (StringUtils.isNotBlank(this.search.getSoggettoLocale())) {
				// recuper soggetto erogatore
				erogatore = this.dynamicUtilsService.findSoggettoByTipoNome(
						this.search.getTipoSoggettoLocale(),
						this.search.getSoggettoLocale());
			}
		} else if ("uscita".equals(this.search.getTipologiaRicerca())) {
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
			String nomeSoggetto = null;
			String tipoSoggetto = null;
			if (StringUtils.isNotBlank(this.search.getSoggettoLocale()) ) {
				tipoSoggetto = this.search.getTipoSoggettoLocale();
				nomeSoggetto = this.search.getSoggettoLocale();
			}

			String tipoProtocollo =   this.search.getProtocollo();

			this.serviziApplicativi = this.dynamicUtils.getListaSelectItemsServiziApplicativiFromSoggettoErogatore(tipoProtocollo,tipoSoggetto, nomeSoggetto);
			Integer lunghezzaSelectList = this.dynamicUtils.getLunghezzaSelectList(this.serviziApplicativi);
			this.serviziApplicativiSelectItemsWidth = Math.max(this.serviziApplicativiSelectItemsWidth,  lunghezzaSelectList);
		}

		return this.serviziApplicativi;
	}

	public List<SelectItem> getSoggetti(){
		if(this.search==null){
			return new ArrayList<SelectItem>();
		}
		if(this.soggetti!=null)
			return this.soggetti;

		String tipoProtocollo = this.search.getProtocollo();
		String idPorta = null;
		List<Soggetto> list = this.dynamicUtilsService.findElencoSoggetti(tipoProtocollo ,idPorta);
		this.soggetti = new ArrayList<SelectItem>();
		for (Soggetto soggetto : list) {
			this.soggetti.add(new SelectItem(soggetto.getNomeSoggetto(),soggetto.getNomeSoggetto(),soggetto.getNomeSoggetto()));
		}
		return this.soggetti;
	}

	public List<String> getTipiNomiSoggettiAssociati() throws Exception {
		return _getTipiNomiSoggettiAssociati(false);
	}
	
	public List<String> _getTipiNomiSoggettiAssociati(boolean soloOperativi) throws Exception {
		if(this.search==null){
			return new ArrayList<String>();
		}
		UserDetailsBean loggedUser = Utility.getLoggedUser();
		if(loggedUser!=null){
			String tipoProtocollo = this.search.getProtocollo();
			if(tipoProtocollo == null)
				return loggedUser.getTipiNomiSoggettiAssociati();

			// se ho selezionato un protocollo devo filtrare per protocollo
			List<IDSoggetto> tipiNomiSoggettiAssociati = loggedUser.getUtenteSoggettoList();
			List<String> lst = new ArrayList<String>();

			if(tipiNomiSoggettiAssociati !=null && tipiNomiSoggettiAssociati.size() > 0)
				for (IDSoggetto utenteSoggetto : tipiNomiSoggettiAssociati) {
					if(this.dynamicUtils.isTipoSoggettoCompatibileConProtocollo(utenteSoggetto.getTipo(), tipoProtocollo)){
						String tipoNome = utenteSoggetto.getTipo() + "/" + utenteSoggetto.getNome();
						boolean add = true;
						if(soloOperativi) {
							String nomePddFromSoggetto = this.dynamicUtils.getServerFromSoggetto(utenteSoggetto.getTipo(), utenteSoggetto.getNome());
							add = this.dynamicUtils.checkTipoPdd(nomePddFromSoggetto, TipoPdD.OPERATIVO);
						}
						if(lst.contains(tipoNome)==false && add){
							lst.add(tipoNome);
						}
					}
				}

			return lst;
		}

		return new ArrayList<String>();
	}

	@Override
	protected List<Soggetto> _getListaSoggetti(Object val, String tipoProtocollo) {
		if(this.search==null){
			return new ArrayList<Soggetto>();
		}
		List<Soggetto> list = null;
		Soggetto s = new Soggetto();
		s.setNomeSoggetto("--");

		// ricerca soggetti
		if(val==null || StringUtils.isEmpty((String)val))
			list = new ArrayList<Soggetto>();
		else{
			list = this.dynamicUtilsService.soggettiAutoComplete(tipoProtocollo,(String)val);
		}

		UserDetailsBean loggedUser = Utility.getLoggedUser();
		//se non e' admin allora devo controllare i tipi dei soggetti associati
		if(!loggedUser.isAdmin()){
			List<IDSoggetto> tipiNomiSoggettiAssociati = loggedUser.getUtenteSoggettoList();
			if(tipiNomiSoggettiAssociati !=null && tipiNomiSoggettiAssociati.size() > 0){
				List<Soggetto> listaFiltrata = new ArrayList<Soggetto>();
				List<String> listaTipi = new ArrayList<String>();
				
				// controllo soggetto locale
				String tipoSoggettoLocale = Utility.parseTipoSoggetto(this.search.getTipoNomeSoggettoLocale());
				String nomeSoggettoLocale = Utility.parseNomeSoggetto(this.search.getTipoNomeSoggettoLocale());
				// se il tipo soggetto locale e' impostato allora filtro per il tipo compatibile con quello scelto
				if(nomeSoggettoLocale != null){
					listaTipi.add(tipoSoggettoLocale);
				} else {
				
				// prelevo il tipo dei soggetti compatibili
				for (IDSoggetto utenteSoggetto : tipiNomiSoggettiAssociati) {
					if(!listaTipi.contains(utenteSoggetto.getTipo()))
						listaTipi.add(utenteSoggetto.getTipo());
				}

				}
				for (Soggetto soggetto : list) {
					for (String tipo : listaTipi) {
						try {
							if(this.dynamicUtils.isTipoSoggettoCompatibile(tipo, soggetto.getTipoSoggetto()))
								listaFiltrata.add(soggetto);
						} catch (Exception e) {
					 
						}
					}
				}

				listaFiltrata.add(0,s);
				return listaFiltrata;
			}
		}  
		
		list.add(0,s);
		return list;	
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
