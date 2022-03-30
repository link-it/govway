/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it).
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


package org.openspcoop2.web.monitor.allarmi.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.allarmi.Allarme;
import org.openspcoop2.core.allarmi.AllarmeHistory;
import org.openspcoop2.core.allarmi.AllarmeParametro;
import org.openspcoop2.core.allarmi.IdAllarme;
import org.openspcoop2.core.allarmi.constants.RuoloPorta;
import org.openspcoop2.core.allarmi.constants.StatoAllarme;
import org.openspcoop2.core.allarmi.dao.IAllarmeHistoryService;
import org.openspcoop2.core.allarmi.dao.IAllarmeHistoryServiceSearch;
import org.openspcoop2.core.allarmi.dao.IAllarmeService;
import org.openspcoop2.core.allarmi.dao.IAllarmeServiceSearch;
import org.openspcoop2.core.allarmi.dao.IDBAllarmeServiceSearch;
import org.openspcoop2.core.allarmi.dao.IServiceManager;
import org.openspcoop2.core.allarmi.dao.jdbc.JDBCAllarmeHistoryServiceSearch;
import org.openspcoop2.core.allarmi.utils.ProjectInfo;
import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.commons.dao.DAOFactory;
import org.openspcoop2.core.commons.dao.DAOFactoryProperties;
import org.openspcoop2.core.commons.search.PortaApplicativa;
import org.openspcoop2.core.commons.search.PortaDelegata;
import org.openspcoop2.core.commons.search.dao.IPortaApplicativaServiceSearch;
import org.openspcoop2.core.commons.search.dao.IPortaDelegataServiceSearch;
import org.openspcoop2.core.commons.search.model.PortaApplicativaModel;
import org.openspcoop2.core.commons.search.model.PortaDelegataModel;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.driver.db.DriverConfigurazioneDB;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.mapping.MappingErogazionePortaApplicativa;
import org.openspcoop2.core.mapping.MappingFruizionePortaDelegata;
import org.openspcoop2.core.plugins.IdPlugin;
import org.openspcoop2.core.plugins.Plugin;
import org.openspcoop2.core.plugins.constants.TipoPlugin;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.core.registry.driver.db.DriverRegistroServiziDB;
import org.openspcoop2.core.transazioni.constants.PddRuolo;
import org.openspcoop2.generic_project.beans.CustomField;
import org.openspcoop2.generic_project.beans.NonNegativeNumber;
import org.openspcoop2.generic_project.dao.IServiceSearchWithId;
import org.openspcoop2.generic_project.exception.ExpressionException;
import org.openspcoop2.generic_project.exception.ExpressionNotImplementedException;
import org.openspcoop2.generic_project.exception.MultipleResultException;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.generic_project.exception.NotImplementedException;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.generic_project.expression.IExpression;
import org.openspcoop2.generic_project.expression.IPaginatedExpression;
import org.openspcoop2.generic_project.expression.LikeMode;
import org.openspcoop2.generic_project.expression.SortOrder;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.monitor.engine.alarm.AlarmManager;
import org.openspcoop2.monitor.engine.alarm.wrapper.ConfigurazioneAllarmeBean;
import org.openspcoop2.monitor.engine.alarm.wrapper.ConfigurazioneAllarmeHistoryBean;
import org.openspcoop2.monitor.engine.dynamic.DynamicFactory;
import org.openspcoop2.monitor.engine.dynamic.IDynamicLoader;
import org.openspcoop2.monitor.sdk.alarm.IAlarm;
import org.openspcoop2.monitor.sdk.condition.Context;
import org.openspcoop2.monitor.sdk.exceptions.AlarmException;
import org.openspcoop2.monitor.sdk.parameters.Parameter;
import org.openspcoop2.monitor.sdk.plugins.DialogInfo;
import org.openspcoop2.monitor.sdk.plugins.IAlarmProcessing;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.engine.utils.NamingUtils;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.config.IProtocolConfiguration;
import org.openspcoop2.web.monitor.allarmi.bean.AllarmiSearchForm;
import org.openspcoop2.web.monitor.core.constants.Costanti;
import org.openspcoop2.web.monitor.core.core.PddMonitorProperties;
import org.openspcoop2.web.monitor.core.core.Utility;
import org.openspcoop2.web.monitor.core.dao.DynamicUtilsServiceEngine;
import org.openspcoop2.web.monitor.core.dynamic.DynamicComponentUtils;
import org.openspcoop2.web.monitor.core.dynamic.components.BaseComponent;
import org.openspcoop2.web.monitor.core.exception.UserInvalidException;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;
import org.openspcoop2.web.monitor.core.utils.DynamicPdDBeanUtils;
import org.openspcoop2.web.monitor.core.utils.ParseUtility;
import org.slf4j.Logger;

/**     
 * AllarmiService
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AllarmiService implements IAllarmiService {

	private static Logger log = LoggerManager.getPddMonitorSqlLogger();

	private int LIMIT_SEARCH = 10000;
	private static String TIPOLOGIA_SOLO_ASSOCIATE = CostantiConfigurazione.ALLARMI_TIPOLOGIA_SOLO_ASSOCIATE;
	
	private transient IServiceManager pluginsServiceManager;
	private transient DriverRegistroServiziDB driverRegistroDB = null;
	private transient DriverConfigurazioneDB driverConfigDB = null;
	private IAllarmeService allarmeDAO;
	private IAllarmeServiceSearch allarmeSearchDAO;
	private IAllarmeHistoryServiceSearch allarmeHistorySearchDAO;
	private IAllarmeHistoryService allarmeHistoryDAO;
	private transient org.openspcoop2.core.plugins.dao.IServiceManager pluginsBaseServiceManager;
//	private IPluginServiceSearch pluginsServiceSearchDAO;
	private transient org.openspcoop2.core.commons.search.dao.IServiceManager utilsServiceManager;
	protected DynamicPdDBeanUtils dynamicUtils = null;
	private IPortaDelegataServiceSearch portaDelegataDAO = null;
	private IPortaApplicativaServiceSearch portaApplicativaDAO  = null;

	private AllarmiSearchForm searchForm;

	private static final boolean useDriverConfig = true; 
	
	public AllarmiService() {
		try {
			PddMonitorProperties pddMonitorProperties = PddMonitorProperties.getInstance(log);

			// init Service Manager allarmi
			this.pluginsServiceManager = (IServiceManager) DAOFactory
					.getInstance(AllarmiService.log).getServiceManager(ProjectInfo.getInstance(),AllarmiService.log);
			this.allarmeDAO = this.pluginsServiceManager.getAllarmeService();
			this.allarmeSearchDAO = this.pluginsServiceManager.getAllarmeServiceSearch();
			this.allarmeHistorySearchDAO = 
					this.pluginsServiceManager.getAllarmeHistoryServiceSearch();
			this.allarmeHistoryDAO = 
					this.pluginsServiceManager.getAllarmeHistoryService();

			// init Service Manager plugins
			this.pluginsBaseServiceManager = (org.openspcoop2.core.plugins.dao.IServiceManager) DAOFactory
					.getInstance(AllarmiService.log).getServiceManager(org.openspcoop2.core.plugins.utils.ProjectInfo.getInstance(),AllarmiService.log);
//			this.pluginsServiceSearchDAO = this.pluginsBaseServiceManager.getPluginServiceSearch();

			this.LIMIT_SEARCH = pddMonitorProperties.getSearchFormLimit();
			
			this.utilsServiceManager = (org.openspcoop2.core.commons.search.dao.IServiceManager) DAOFactory
					.getInstance(log).getServiceManager(org.openspcoop2.core.commons.search.utils.ProjectInfo.getInstance(), AllarmiService.log);
			
			this.portaApplicativaDAO = this.utilsServiceManager.getPortaApplicativaServiceSearch();
			this.portaDelegataDAO = this.utilsServiceManager.getPortaDelegataServiceSearch();
			
			String tipoDatabase = DAOFactoryProperties.getInstance(log).getTipoDatabase(org.openspcoop2.core.commons.search.utils.ProjectInfo.getInstance());
			String datasourceJNDIName = DAOFactoryProperties.getInstance(log).getDatasourceJNDIName(org.openspcoop2.core.commons.search.utils.ProjectInfo.getInstance());
			Properties datasourceJNDIContext = DAOFactoryProperties.getInstance(log).getDatasourceJNDIContext(org.openspcoop2.core.commons.search.utils.ProjectInfo.getInstance());

			this.driverRegistroDB = new DriverRegistroServiziDB(datasourceJNDIName,datasourceJNDIContext, log, tipoDatabase);
			this.driverConfigDB = new DriverConfigurazioneDB(datasourceJNDIName,datasourceJNDIContext, log, tipoDatabase);
			
			this.dynamicUtils = new DynamicPdDBeanUtils(this.utilsServiceManager, this.pluginsBaseServiceManager, 
					this.driverRegistroDB, this.driverConfigDB,
					log);
			
		} catch (Exception e) {
			AllarmiService.log.error(e.getMessage(), e);
		}
	}
	
	@Override
	public AllarmiSearchForm getSearch() {
		return this.searchForm;
	}
	
	@Override
	public void setSearch(AllarmiSearchForm search) {
		this.searchForm = search;
	}

	private AllarmiDriverParams buildDriverParams(AllarmiSearchForm search) throws Exception {
		
		AllarmiDriverParams driverParams = new AllarmiDriverParams();
		
		String tipologiaRicerca = search.getTipologiaAllarme();
		driverParams.setTipologiaRicerca(tipologiaRicerca);
		
		Boolean enabledParam = null;
		StatoAllarme statoParam = null;
		Boolean acknowledgedParam = null;				
		if (StringUtils.isNotEmpty(search.getStatoSelezionato())
				&& !StringUtils.equals(
						search.getStatoSelezionato(), "Qualsiasi")) {
			if (StringUtils.equals(search.getStatoSelezionato(),
					"Disabilitato")) {
				enabledParam=false;
			} else {
				enabledParam=true;
				if (StringUtils.equals(search.getStatoSelezionato(),
						"Non Disabilitato")) {
					// nop
				} else {		
					// voglio quelli abilitati con un determinato stato
					String statoCheck = search.getStatoSelezionato();
					if ("Ok".equals(statoCheck)){
						statoParam = StatoAllarme.OK;
					}
					
					if ("Warn".equals(statoCheck)){
						statoParam = StatoAllarme.WARNING;
					}
					if ("Warn (Acknowledged)".equals(statoCheck)){
						statoParam = StatoAllarme.WARNING;
						acknowledgedParam=true;
					}
					if ("Warn (Unacknowledged)".equals(statoCheck)){
						statoParam = StatoAllarme.WARNING;
						acknowledgedParam=false;
					}
					
					if ("Error".equals(statoCheck)){
						statoParam = StatoAllarme.ERROR;
					}
					if ("Error (Acknowledged)".equals(statoCheck)){
						statoParam = StatoAllarme.ERROR;
						acknowledgedParam=true;
					}
					if ("Error (Unacknowledged)".equals(statoCheck)){
						statoParam = StatoAllarme.ERROR;
						acknowledgedParam=false;
					}
				
				}
				
			}
		}
		driverParams.setEnabled(enabledParam);
		driverParams.setStato(statoParam);
		driverParams.setAcknowledged(acknowledgedParam);
		
		String nomeAllarme = null;
		if (StringUtils.isNotEmpty(search.getNomeAllarme())) {
			nomeAllarme = search.getNomeAllarme();
		}
		driverParams.setNomeAllarme(nomeAllarme);
		
		
		// Utenze
		
		List<IDSoggetto> listSoggettiProprietariAbilitati = null;
		List<IDServizio> listIDServizioAbilitati = null;
		if(search.getPermessiUtenteOperatore()!=null) {
			if(search.getPermessiUtenteOperatore().getListIDSoggetti()!=null && !search.getPermessiUtenteOperatore().getListIDSoggetti().isEmpty()) {
				listSoggettiProprietariAbilitati = search.getPermessiUtenteOperatore().getListIDSoggetti();
			}
			if(search.getPermessiUtenteOperatore().getListIDServizi()!=null && !search.getPermessiUtenteOperatore().getListIDServizi().isEmpty()) {
				listIDServizioAbilitati = search.getPermessiUtenteOperatore().getListIDServizi();
			}
		}
		driverParams.setListSoggettiProprietariAbilitati(listSoggettiProprietariAbilitati);
		driverParams.setListIDServizioAbilitati(listIDServizioAbilitati);
		
		
		// Protocollo
		
		List<String> tipoSoggettiByProtocollo = null;
		List<String> tipoServiziByProtocollo = null;
		String protocollo = null;
		// aggiungo la condizione sul protocollo se e' impostato e se e' presente piu' di un protocollo
		// protocollo e' impostato anche scegliendo la modalita'
		//		if (StringUtils.isNotEmpty(searchForm.getProtocollo()) && searchForm.isShowListaProtocolli()) {
		if (search.isSetFiltroProtocollo()) {
			protocollo = search.getProtocollo();
			if(StringUtils.isNotEmpty(protocollo)) {
				
				IProtocolFactory<?> protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(protocollo);
				IProtocolConfiguration pConfig = protocolFactory.createProtocolConfiguration();
				
				tipoSoggettiByProtocollo = pConfig.getTipiSoggetti();
				
				tipoServiziByProtocollo = new ArrayList<String>();
				tipoServiziByProtocollo.addAll(pConfig.getTipiServizi(ServiceBinding.SOAP));
				tipoServiziByProtocollo.addAll(pConfig.getTipiServizi(ServiceBinding.REST));
			}
		}
		driverParams.setTipoSoggettiByProtocollo(tipoSoggettiByProtocollo);
		driverParams.setTipoServiziByProtocollo(tipoServiziByProtocollo);
		
		
		// ID Soggetto Propriatario
		
		IDSoggetto idSoggettoProprietario= null;
		if(search.getTipoNomeSoggettoLocale()!=null && 
				!StringUtils.isEmpty(search.getTipoNomeSoggettoLocale()) && !"--".equals(search.getTipoNomeSoggettoLocale())){
			String tipoSoggetto = search.getTipoSoggettoLocale();
			String nomeSoggetto = search.getSoggettoLocale();
			if(tipoSoggetto!=null && !Costanti.VALUE_PARAMETRO_MODALITA_ALL.equals(tipoSoggetto) &&
					nomeSoggetto!=null && !Costanti.VALUE_PARAMETRO_MODALITA_ALL.equals(nomeSoggetto)) {
				idSoggettoProprietario = new IDSoggetto(tipoSoggetto, nomeSoggetto);
			}
		}
		driverParams.setIdSoggettoProprietario(idSoggettoProprietario);
		
		
		
		// list IDServizi
		
		String gruppo = search.getGruppo();
		String tipoProtocollo = search.getProtocollo();
		
		String tipoSoggetto = search.getTipoSoggettoLocale();
		String nomeSoggetto = search.getSoggettoLocale();
		
		String input = null;
		
		boolean apiImplSelected = StringUtils.isNotBlank(search.getNomeServizio());
		
		IDAccordo idAccordo = null;
		IDAccordoFactory idAccordoFactory = null;
		String api = search.getApi();
		if( !apiImplSelected && (api!=null && !"".equals(api)) ) {
			idAccordoFactory = IDAccordoFactory.getInstance();
			idAccordo = idAccordoFactory.getIDAccordoFromUri(api);
		}
		
		List<IDServizio> listIDServizio = null;
		if(!apiImplSelected && ( (gruppo!=null && !"".equals(gruppo)) || idAccordo!=null ) ) {
			listIDServizio = new ArrayList<IDServizio>();
			List<SelectItem> list = null;
			boolean distinct = true;
			
			if(PddRuolo.DELEGATA.equals(search.getTipologiaAllarme())) {
				list = this.dynamicUtils.getListaSelectItemsElencoConfigurazioneServiziFruizione(tipoProtocollo, gruppo, idAccordo, tipoSoggetto, nomeSoggetto,null,null,input, false, search.getPermessiUtenteOperatore(), distinct);
			}else {
				// bisogna filtrare per soggetti operativi
				list = this.dynamicUtils.getListaSelectItemsElencoConfigurazioneServiziErogazione(tipoProtocollo, gruppo, idAccordo, tipoSoggetto, nomeSoggetto,input, true, search.getPermessiUtenteOperatore(), distinct);
			}
			if(list!=null && list.size()>0) {
				for (SelectItem selectItem : list) {
					String servizioString = (String) selectItem.getValue();
					IDServizio idServizio = ParseUtility.parseServizioSoggetto(servizioString);
					listIDServizio.add(idServizio);
				}
			}
			else {
				// creo un servizio non esistente per fornire 0 dati
				IDServizio idServizio = IDServizioFactory.getInstance().getIDServizioFromValues("-", "-", "-", "-", -1);
				listIDServizio.add(idServizio);
			}
		}
		
		if (apiImplSelected) {
			listIDServizio = new ArrayList<IDServizio>();
			String servizioString = search.getNomeServizio();
			IDServizio idServizio = ParseUtility.parseServizioSoggetto(servizioString);
			listIDServizio.add(idServizio);
		}
		
		driverParams.setListIDServizio(listIDServizio);
		
		return driverParams;
	}
	
	@Override
	public Long getCountAllarmiByStato(String stato, Integer acknowledged) {
		Long retValue = 0L;
		try {

			AllarmiSearchForm search = (AllarmiSearchForm) this.searchForm.clone();

			if(Utility.isAmministratore()) { // amministratore vede tutti gli allarmi, indipendentemente dai filtri di ricerca
				search.ripulisci(); 
				search.setTipologiaAllarme("*"); // i risultati devono mostrare tutti gli allarmi
			} else {
				search.ripulisci(); 
				search.setTipologiaAllarme(TIPOLOGIA_SOLO_ASSOCIATE); // i risultati devono mostrare tutti gli allarmi
			}
			
			search.setStatoSelezionato(stato);

			if(useDriverConfig) {
				
				AllarmiDriverParams params = this.buildDriverParams(search);
				
				String tipologiaRicerca = params.getTipologiaRicerca();
				Boolean enabledParam = params.getEnabled();
				StatoAllarme statoParam = params.getStato();
				Boolean acknowledgedParam = params.getAcknowledged();
				String nomeAllarme = params.getNomeAllarme();
				List<IDSoggetto> listSoggettiProprietariAbilitati = params.getListSoggettiProprietariAbilitati();
				List<IDServizio> listIDServizioAbilitati = params.getListIDServizioAbilitati();
				List<String> tipoSoggettiByProtocollo = params.getTipoSoggettiByProtocollo();
				List<String> tipoServiziByProtocollo = params.getTipoServiziByProtocollo(); 
				IDSoggetto idSoggettoProprietario = params.getIdSoggettoProprietario();
				List<IDServizio> listIDServizio = params.getListIDServizio();
				
				// Solo per questo metodo
				if(acknowledged!=null){
					acknowledgedParam = acknowledged.intValue() == CostantiDB.TRUE;
				}
				
				retValue = this.driverConfigDB.countAllarmi(tipologiaRicerca, enabledParam, statoParam, acknowledgedParam, nomeAllarme, 
						listSoggettiProprietariAbilitati, listIDServizioAbilitati, tipoSoggettiByProtocollo, tipoServiziByProtocollo, idSoggettoProprietario, listIDServizio);
				
			}
			else {
			
				IExpression e = createQuery(true, search);
				
				if(acknowledged!=null){
					e.equals(Allarme.model().ACKNOWLEDGED,acknowledged);
				}
	
				NonNegativeNumber nnn = this.allarmeSearchDAO.count(e);
	
				if (nnn != null) {
					retValue = nnn.longValue();
				}
				
			}
		} catch (ServiceException e) {
			AllarmiService.log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			AllarmiService.log.error(e.getMessage(), e);
		} catch (ExpressionNotImplementedException e) {
			AllarmiService.log.error(e.getMessage(), e);
		} catch (ExpressionException e) {
			AllarmiService.log.error(e.getMessage(), e);
		} catch (CloneNotSupportedException e) {
			AllarmiService.log.error(e.getMessage(), e);
		} catch (Exception e) {
			AllarmiService.log.error(e.getMessage(), e);
		}

		return retValue;
	}

	@Override
	public List<ConfigurazioneAllarmeBean> findAll(int start, int limit) {
		// this.searchForm.getConfigurazione() puo essere null, in caso in cui
		// sia nella visualizzazione
		// dello stato degli allarmi dell'amministratore, in futuro si puo'
		// prevedere di
		// inserire come filtro per l'amministratore il servizio di
		// configurazione
		if (this.searchForm == null)
			return new ArrayList<ConfigurazioneAllarmeBean>();

		try {
			
			List<Allarme> findAll = null;
			if(useDriverConfig) {
				
				AllarmiDriverParams params = this.buildDriverParams(this.searchForm);
				
				String tipologiaRicerca = params.getTipologiaRicerca();
				Boolean enabledParam = params.getEnabled();
				StatoAllarme statoParam = params.getStato();
				Boolean acknowledgedParam = params.getAcknowledged();
				String nomeAllarme = params.getNomeAllarme();
				List<IDSoggetto> listSoggettiProprietariAbilitati = params.getListSoggettiProprietariAbilitati();
				List<IDServizio> listIDServizioAbilitati = params.getListIDServizioAbilitati();
				List<String> tipoSoggettiByProtocollo = params.getTipoSoggettiByProtocollo();
				List<String> tipoServiziByProtocollo = params.getTipoServiziByProtocollo(); 
				IDSoggetto idSoggettoProprietario = params.getIdSoggettoProprietario();
				List<IDServizio> listIDServizio = params.getListIDServizio();
				
				findAll = this.driverConfigDB.findAllAllarmi(tipologiaRicerca, enabledParam, statoParam, acknowledgedParam, nomeAllarme, 
						listSoggettiProprietariAbilitati, listIDServizioAbilitati, tipoSoggettiByProtocollo, tipoServiziByProtocollo, idSoggettoProprietario, listIDServizio,
						start,limit);
				
			}
			else {
			
				IExpression e = createQuery(false,this.searchForm);
				IPaginatedExpression pagExpr = this.allarmeSearchDAO
						.toPaginatedExpression(e);
				
				findAll = this.allarmeSearchDAO.findAll(pagExpr.offset(start)
						.limit(limit));
			}

			if(findAll != null && findAll.size() > 0){
				List<ConfigurazioneAllarmeBean> toRet = new ArrayList<ConfigurazioneAllarmeBean>();

				boolean existsAlmostOneManuallyUpdateState = false;
				boolean existsAlmostOneManuallyAckCriteria = false;
				
				for (Allarme al : findAll) {
					IdPlugin idPlugin = new IdPlugin();
					idPlugin.setTipoPlugin(TipoPlugin.ALLARME.getValue());
					idPlugin.setTipo(al.getTipo());
					Plugin plugin = this.dynamicUtils.getPlugin(idPlugin);
					ConfigurazioneAllarmeBean allarmeBean = new ConfigurazioneAllarmeBean(al, plugin);
					this.valorizzaDettaglioAPI(allarmeBean);
					if(allarmeBean.isManuallyUpdateState()) {
						existsAlmostOneManuallyUpdateState = true;
					}
					if(allarmeBean.isManuallyAckCriteria()) {
						existsAlmostOneManuallyAckCriteria = true;
					}
					toRet.add(allarmeBean);
				}
				
				for (ConfigurazioneAllarmeBean allarmeBean : toRet) {
					allarmeBean.setExistsAlmostOneManuallyUpdateState(existsAlmostOneManuallyUpdateState);
					allarmeBean.setExistsAlmostOneManuallyAckCriteria(existsAlmostOneManuallyAckCriteria);
				}

				return toRet;
			}
		} catch (ServiceException e1) {
			AllarmiService.log.error(
					"Errore durante la ricerca degli Allarmi", e1);
		} catch (NotImplementedException e1) {
			AllarmiService.log.error(
					"Errore durante la ricerca degli Allarmi", e1);
		} catch (ExpressionNotImplementedException e1) {
			AllarmiService.log.error(
					"Errore durante la ricerca degli Allarmi", e1);
		} catch (ExpressionException e1) {
			AllarmiService.log.error(
					"Errore durante la ricerca degli Allarmi", e1);
		} catch (Exception e1) {
			AllarmiService.log.error(
					"Errore durante la ricerca degli Allarmi", e1);
		}

		return new ArrayList<ConfigurazioneAllarmeBean>();
	}

	private void valorizzaDettaglioAPI(ConfigurazioneAllarmeBean allarmeBean) throws DriverRegistroServiziException, Exception {
		if(!allarmeBean.isAllarmeConfigurazione()) { // se non e' un allarme configurazione risolvo l'api collegata alla porta
			String nomePorta = allarmeBean.getFiltro().getNomePorta();
			String labelServizio =  null;
			String erogatore = null, fruitore = null;
			String tipoProtocollo = allarmeBean.getFiltro().getProtocollo();
			if(allarmeBean.isRuoloPortaDelegata()) {
				PortaDelegata portaDelegata = this.dynamicUtils.getPortaDelegata(nomePorta);
				labelServizio = NamingUtils.getLabelAccordoServizioParteSpecificaSenzaErogatore(tipoProtocollo, portaDelegata.getTipoServizio(), portaDelegata.getNomeServizio(), portaDelegata.getVersioneServizio());
				erogatore = NamingUtils.getLabelSoggetto(tipoProtocollo, portaDelegata.getTipoSoggettoErogatore(),portaDelegata.getNomeSoggettoErogatore());
				fruitore = NamingUtils.getLabelSoggetto(tipoProtocollo, portaDelegata.getIdSoggetto().getTipo(),portaDelegata.getIdSoggetto().getNome());
				
				IDServizio idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(portaDelegata.getTipoServizio(), portaDelegata.getNomeServizio(), 
						portaDelegata.getTipoSoggettoErogatore(), portaDelegata.getNomeSoggettoErogatore(),
						portaDelegata.getVersioneServizio());
				IDSoggetto idFruitore = new IDSoggetto(portaDelegata.getIdSoggetto().getTipo(), portaDelegata.getIdSoggetto().getNome());
				IDPortaDelegata idPortaDelegata = new IDPortaDelegata();
				idPortaDelegata.setNome(portaDelegata.getNome());
				MappingFruizionePortaDelegata mapping = this.dynamicUtils.getMappingFruizione(idServizio, idFruitore, idPortaDelegata);
				if(mapping!=null && !mapping.isDefault()) {
					labelServizio = labelServizio + " (gruppo: "+mapping.getDescrizione()+")";
				}
				
			} else if(allarmeBean.isRuoloPortaApplicativa()) {
				PortaApplicativa portaApplicativa = this.dynamicUtils.getPortaApplicativa(nomePorta);
				labelServizio = NamingUtils.getLabelAccordoServizioParteSpecificaSenzaErogatore(allarmeBean.getFiltro().getProtocollo(), portaApplicativa.getTipoServizio(), portaApplicativa.getNomeServizio(), portaApplicativa.getVersioneServizio());
				erogatore = NamingUtils.getLabelSoggetto(tipoProtocollo, portaApplicativa.getIdSoggetto().getTipo(), portaApplicativa.getIdSoggetto().getNome());
				
				IDServizio idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(portaApplicativa.getTipoServizio(), portaApplicativa.getNomeServizio(), 
						portaApplicativa.getIdSoggetto().getTipo(), portaApplicativa.getIdSoggetto().getNome(), 
						portaApplicativa.getVersioneServizio());
				IDPortaApplicativa idPortaApplicativa = new IDPortaApplicativa();
				idPortaApplicativa.setNome(portaApplicativa.getNome());
				MappingErogazionePortaApplicativa mapping = this.dynamicUtils.getMappingErogazione(idServizio, idPortaApplicativa);
				if(mapping!=null && !mapping.isDefault()) {
					labelServizio = labelServizio + " (gruppo: "+mapping.getDescrizione()+")";
				}
			}
			allarmeBean.setDettaglioAPI(labelServizio);
			allarmeBean.setDettaglioFruitore(fruitore);
			allarmeBean.setDettaglioErogatore(erogatore);
		}
	}

	@Override
	public int totalCount() {
		if (this.searchForm == null)
			return 0;

		try {
			if(useDriverConfig) {
				
				AllarmiDriverParams params = this.buildDriverParams(this.searchForm);
				
				String tipologiaRicerca = params.getTipologiaRicerca();
				Boolean enabledParam = params.getEnabled();
				StatoAllarme statoParam = params.getStato();
				Boolean acknowledgedParam = params.getAcknowledged();
				String nomeAllarme = params.getNomeAllarme();
				List<IDSoggetto> listSoggettiProprietariAbilitati = params.getListSoggettiProprietariAbilitati();
				List<IDServizio> listIDServizioAbilitati = params.getListIDServizioAbilitati();
				List<String> tipoSoggettiByProtocollo = params.getTipoSoggettiByProtocollo();
				List<String> tipoServiziByProtocollo = params.getTipoServiziByProtocollo(); 
				IDSoggetto idSoggettoProprietario = params.getIdSoggettoProprietario();
				List<IDServizio> listIDServizio = params.getListIDServizio();
				
				long retValue = this.driverConfigDB.countAllarmi(tipologiaRicerca, enabledParam, statoParam, acknowledgedParam, nomeAllarme, 
						listSoggettiProprietariAbilitati, listIDServizioAbilitati, tipoSoggettiByProtocollo, tipoServiziByProtocollo, idSoggettoProprietario, listIDServizio);
				
				return  Long.valueOf(retValue).intValue();
			}
			else {
			
				IExpression e = createQuery(true,this.searchForm);
				NonNegativeNumber nnn = this.allarmeSearchDAO.count(e);
			
				// Long res = (Long) this.createQuery(true).getSingleResult();
				return nnn != null ? Long.valueOf(nnn.longValue()).intValue() : 0;
			}
		} catch (ServiceException e1) {
			AllarmiService.log.error("Errore durante il calcolo del numero dei record", e1);
		} catch (NotImplementedException e1) {
			AllarmiService.log.error("Errore durante il calcolo del numero dei record", e1);
		} catch (Exception e1) {
			AllarmiService.log.error("Errore durante il calcolo del numero dei record", e1);
		}

		return 0;
	}

	@Override
	public void delete(ConfigurazioneAllarmeBean obj) {

		try {
			
			//1. elimino l'allarme
			this.allarmeDAO.delete(obj);

		} catch (ServiceException e) {
			AllarmiService.log.error( "Errore durante la rimozione dell'Allarme con id:" + obj.getId(), e);
		} catch (NotImplementedException e) {
			AllarmiService.log.error( "Errore durante la rimozione dell'Allarme con id:" + obj.getId(), e);
		} 
	}

	@Override
	public void deleteById(Long key) {
		IdAllarme idConfigurazioneAllarme = new IdAllarme();
		idConfigurazioneAllarme.setId(key);

		try {
			this.allarmeDAO.deleteById(idConfigurazioneAllarme);
		} catch (ServiceException e) {
			AllarmiService.log.error("Errore durante la rimozione dell'Allarme con id:" + key,
					e);
		} catch (NotImplementedException e) {
			AllarmiService.log.error("Errore durante la rimozione dell'Allarme con id:" + key,
					e);
		}
	}

	@Override
	public List<ConfigurazioneAllarmeBean> findAll() {
		if (this.searchForm == null)
			return new ArrayList<ConfigurazioneAllarmeBean>();

		try {
			List<Allarme> findAll = null;
			if(useDriverConfig) {
				
				AllarmiDriverParams params = this.buildDriverParams(this.searchForm);
				
				String tipologiaRicerca = params.getTipologiaRicerca();
				Boolean enabledParam = params.getEnabled();
				StatoAllarme statoParam = params.getStato();
				Boolean acknowledgedParam = params.getAcknowledged();
				String nomeAllarme = params.getNomeAllarme();
				List<IDSoggetto> listSoggettiProprietariAbilitati = params.getListSoggettiProprietariAbilitati();
				List<IDServizio> listIDServizioAbilitati = params.getListIDServizioAbilitati();
				List<String> tipoSoggettiByProtocollo = params.getTipoSoggettiByProtocollo();
				List<String> tipoServiziByProtocollo = params.getTipoServiziByProtocollo(); 
				IDSoggetto idSoggettoProprietario = params.getIdSoggettoProprietario();
				List<IDServizio> listIDServizio = params.getListIDServizio();
				
				findAll = this.driverConfigDB.findAllAllarmi(tipologiaRicerca, enabledParam, statoParam, acknowledgedParam, nomeAllarme, 
						listSoggettiProprietariAbilitati, listIDServizioAbilitati, tipoSoggettiByProtocollo, tipoServiziByProtocollo, idSoggettoProprietario, listIDServizio,
						null,null);
				
			}
			else {
			
				IExpression e = createQuery(false,this.searchForm);
	
				IPaginatedExpression pagExpr = this.allarmeSearchDAO
						.toPaginatedExpression(e);
				findAll =  this.allarmeSearchDAO.findAll(pagExpr);
				
			}

			if(findAll != null && findAll.size() > 0){
				List<ConfigurazioneAllarmeBean> toRet = new ArrayList<ConfigurazioneAllarmeBean>();

				for (Allarme al : findAll) {
					IdPlugin idPlugin = new IdPlugin();
					idPlugin.setTipoPlugin(TipoPlugin.ALLARME.getValue());
					idPlugin.setTipo(al.getTipo());
					Plugin plugin = this.dynamicUtils.getPlugin(idPlugin);
					ConfigurazioneAllarmeBean allarmeBean = new ConfigurazioneAllarmeBean(al, plugin);
					this.valorizzaDettaglioAPI(allarmeBean);
					toRet.add(allarmeBean);
				}

				return toRet;
			}
		} catch (ServiceException e1) {
			AllarmiService.log.error("Errore durante la ricerca degli Allarmi", e1);
		} catch (NotImplementedException e1) {
			AllarmiService.log.error("Errore durante la ricerca degli Allarmi", e1);
		} catch (Exception e1) {
			AllarmiService.log.error("Errore durante la ricerca degli Allarmi", e1);
		}

		return new ArrayList<ConfigurazioneAllarmeBean>();
	}

	@Override
	public ConfigurazioneAllarmeBean findById(Long key) {
		IdAllarme idAllarme = new IdAllarme();
		idAllarme.setId(key);

		IDBAllarmeServiceSearch idSearch = (IDBAllarmeServiceSearch) this.allarmeSearchDAO;

		try {
			Allarme configurazioneAllarme = idSearch.get(idAllarme);

			if(configurazioneAllarme != null) {
				IdPlugin idPlugin = new IdPlugin();
				idPlugin.setTipoPlugin(TipoPlugin.ALLARME.getValue());
				idPlugin.setTipo(configurazioneAllarme.getTipo());
				Plugin plugin = this.dynamicUtils.getPlugin(idPlugin);
				ConfigurazioneAllarmeBean allarmeBean = new ConfigurazioneAllarmeBean(configurazioneAllarme, plugin);
				this.valorizzaDettaglioAPI(allarmeBean);
				return allarmeBean;
			}
		} catch (ServiceException e) {
			AllarmiService.log.error("Errore durante la ricerca dell'Allarme con id:" + key, e);
		} catch (NotFoundException e) {
			AllarmiService.log.debug("Errore durante la ricerca dell'Allarme con id:" + key, e);
		} catch (MultipleResultException e) {
			AllarmiService.log.error("Errore durante la ricerca dell'Allarme con id:" + key, e);
		} catch (NotImplementedException e) {
			AllarmiService.log.error("Errore durante la ricerca dell'Allarme con id:" + key, e);
		} catch (Exception e) {
			AllarmiService.log.error("Errore durante la ricerca dell'Allarme con id:" + key, e);
		} 

		return null;
	}

	@Override
	public void store(ConfigurazioneAllarmeBean allarme) throws Exception {

		IdAllarme idAll = new IdAllarme();
		idAll.setNome(allarme.getNome());
		
		if (this.allarmeDAO.exists(idAll)) {
			this.allarmeDAO.update(idAll, allarme);
		} else {
			throw new Exception("Allarme con id '"+allarme.getNome()+"' non esistente");
			// imposto lo stato di default per l'allarme:
//			allarme.setStato(AllarmiConverterUtils.toIntegerValue(StatoAllarme.OK));
//			allarme.setStatoPrecedente(AllarmiConverterUtils.toIntegerValue(StatoAllarme.OK));
//			allarme.setLasttimestampCreate(new Date());
//			allarme.setAcknowledged(Integer.valueOf(0));
//			this.allarmeDAO.create(allarme);
		}
	}

	private IExpression createQuery(boolean isCount,AllarmiSearchForm formRicerca) throws Exception{

		IExpression expr = null;
		try {
			expr = this.allarmeSearchDAO.newExpression();
			
			String gruppo = formRicerca.getGruppo();
			String tipoProtocollo = formRicerca.getProtocollo();
			
			String tipoSoggetto = formRicerca.getTipoSoggettoLocale();
			String nomeSoggetto = formRicerca.getSoggettoLocale();
			
			String input = null;
			
			boolean apiImplSelected = StringUtils.isNotBlank(formRicerca.getNomeServizio());
			
			IDAccordo idAccordo = null;
			IDAccordoFactory idAccordoFactory = null;
			String api = formRicerca.getApi();
			if( !apiImplSelected && (api!=null && !"".equals(api)) ) {
				idAccordoFactory = IDAccordoFactory.getInstance();
				idAccordo = idAccordoFactory.getIDAccordoFromUri(api);
			}
			
			List<IDServizio> listIDServizio = null;
			if(!apiImplSelected && ( (gruppo!=null && !"".equals(gruppo)) || idAccordo!=null ) ) {
				listIDServizio = new ArrayList<IDServizio>();
				List<SelectItem> list = null;
				boolean distinct = true;
				
				if(PddRuolo.DELEGATA.equals(formRicerca.getTipologiaAllarme())) {
					list = this.dynamicUtils.getListaSelectItemsElencoConfigurazioneServiziFruizione(tipoProtocollo, gruppo, idAccordo, tipoSoggetto, nomeSoggetto,null,null,input, false, formRicerca.getPermessiUtenteOperatore(), distinct);
				}else {
					// bisogna filtrare per soggetti operativi
					list = this.dynamicUtils.getListaSelectItemsElencoConfigurazioneServiziErogazione(tipoProtocollo, gruppo, idAccordo, tipoSoggetto, nomeSoggetto,input, true, formRicerca.getPermessiUtenteOperatore(), distinct);
				}
				if(list!=null && list.size()>0) {
					for (SelectItem selectItem : list) {
						String servizioString = (String) selectItem.getValue();
						IDServizio idServizio = ParseUtility.parseServizioSoggetto(servizioString);
						listIDServizio.add(idServizio);
					}
				}
				else {
					// creo un servizio non esistente per fornire 0 dati
					IDServizio idServizio = IDServizioFactory.getInstance().getIDServizioFromValues("-", "-", "-", "-", -1);
					listIDServizio.add(idServizio);
				}
			}

			boolean addAnd = false;
			
			if(formRicerca.getTipologiaAllarme().equals(AllarmiSearchForm.TIPOLOGIA_CONFIGURAZIONE)) {
				expr.isNull(Allarme.model().FILTRO.NOME_PORTA);
				addAnd = true;
			} else if(formRicerca.getTipologiaAllarme().equals(AllarmiSearchForm.TIPOLOGIA_APPLICATIVA)) {
				List<PortaApplicativa> findAll = estraiPAAssociate(formRicerca, apiImplSelected, listIDServizio);
				
				
				List<IExpression> exprOrList = new ArrayList<IExpression>();
				for (PortaApplicativa portaApplicativa : findAll) {
					IExpression exprNomePorta = this.allarmeSearchDAO.newExpression();
					exprNomePorta.equals(Allarme.model().FILTRO.NOME_PORTA, portaApplicativa.getNome());
					exprOrList.add(exprNomePorta);
				}
				
				expr.or(exprOrList.toArray(new IExpression[1]));
				expr.isNotNull(Allarme.model().FILTRO.NOME_PORTA).and().equals(Allarme.model().FILTRO.RUOLO_PORTA,RuoloPorta.APPLICATIVA.getValue());
				
				addAnd = true;
			} else if(formRicerca.getTipologiaAllarme().equals(AllarmiSearchForm.TIPOLOGIA_DELEGATA)) {
				List<PortaDelegata> findAll = estraiPDAssociate(formRicerca, apiImplSelected, listIDServizio);
				
				List<IExpression> exprOrList = new ArrayList<IExpression>();
				for (PortaDelegata portaDelegata : findAll) {
					IExpression exprNomePorta = this.allarmeSearchDAO.newExpression();
					exprNomePorta.equals(Allarme.model().FILTRO.NOME_PORTA, portaDelegata.getNome());
					exprOrList.add(exprNomePorta);
				}
				
				expr.or(exprOrList.toArray(new IExpression[1]));
				expr.isNotNull(Allarme.model().FILTRO.NOME_PORTA).and().equals(Allarme.model().FILTRO.RUOLO_PORTA,RuoloPorta.DELEGATA.getValue());
				addAnd = true;
			} else if(formRicerca.getTipologiaAllarme().equals(TIPOLOGIA_SOLO_ASSOCIATE)) {
				List<PortaApplicativa> findAllPA = estraiPAAssociate(formRicerca, apiImplSelected, listIDServizio);
				List<PortaDelegata> findAllPD = estraiPDAssociate(formRicerca, apiImplSelected, listIDServizio);
				
				List<IExpression> exprOrListPA = new ArrayList<IExpression>();
				for (PortaApplicativa portaApplicativa : findAllPA) {
					IExpression exprNomePorta = this.allarmeSearchDAO.newExpression();
					exprNomePorta.equals(Allarme.model().FILTRO.NOME_PORTA, portaApplicativa.getNome());
					exprOrListPA.add(exprNomePorta);
				}
				
				IExpression exprRuoloPortaPA = this.allarmeSearchDAO.newExpression();
				exprRuoloPortaPA.equals(Allarme.model().FILTRO.RUOLO_PORTA,RuoloPorta.APPLICATIVA.getValue());
				exprRuoloPortaPA.and().or(exprOrListPA.toArray(new IExpression[1]));
				
				List<IExpression> exprOrListPD = new ArrayList<IExpression>();
				for (PortaDelegata portaDelegata : findAllPD) {
					IExpression exprNomePorta = this.allarmeSearchDAO.newExpression();
					exprNomePorta.equals(Allarme.model().FILTRO.NOME_PORTA, portaDelegata.getNome());
					exprOrListPD.add(exprNomePorta);
				}
				
				IExpression exprRuoloPortaPD = this.allarmeSearchDAO.newExpression();
				exprRuoloPortaPD.equals(Allarme.model().FILTRO.RUOLO_PORTA,RuoloPorta.DELEGATA.getValue());
				exprRuoloPortaPD.and().or(exprOrListPD.toArray(new IExpression[1]));
				
				IExpression exprPortOr = this.allarmeSearchDAO.newExpression();
				exprPortOr.or();
				exprPortOr.or(exprRuoloPortaPA,exprRuoloPortaPD);
				
				expr.and(exprPortOr);
				expr.and().isNotNull(Allarme.model().FILTRO.NOME_PORTA).and();
			}

			if (StringUtils.isNotEmpty(formRicerca.getNomeAllarme())) {
				if (addAnd) {
					expr.and();
				}

				expr.ilike(Allarme.model().ALIAS, formRicerca.getNomeAllarme(), LikeMode.ANYWHERE);
				addAnd = true;
			}

			// se e' stato selezionato uno stato != all allora imposto il filtro
			if (StringUtils.isNotEmpty(formRicerca.getStatoSelezionato())
					&& !StringUtils.equals(
							formRicerca.getStatoSelezionato(), "Qualsiasi")) {
				if (addAnd) {
					expr.and();
				}

				if (StringUtils.equals(formRicerca.getStatoSelezionato(),
						"Disabilitato")) {
					expr.equals(Allarme.model().ENABLED, 0);
				} else {
					
					expr.equals(Allarme.model().ENABLED, 1).and();

					if (StringUtils.equals(formRicerca.getStatoSelezionato(),
							"Non Disabilitato")) {
						expr.in(Allarme.model().STATO, 0,1,2);
					} else {
					
						// voglio quelli abilitati con un determinato stato
						String stato = formRicerca.getStatoSelezionato();
						if ("Ok".equals(stato)){
							expr.equals(Allarme.model().STATO, 0);
						}
						
						if ("Warn".equals(stato)){
							expr.equals(Allarme.model().STATO, 1);
						}
						if ("Warn (Acknowledged)".equals(stato)){
							expr.equals(Allarme.model().STATO, 1);
							expr.equals(Allarme.model().ACKNOWLEDGED, 1);
						}
						if ("Warn (Unacknowledged)".equals(stato)){
							expr.equals(Allarme.model().STATO, 1);
							expr.equals(Allarme.model().ACKNOWLEDGED, 0);
						}
						
						if ("Error".equals(stato)){
							expr.equals(Allarme.model().STATO, 2);
						}
						if ("Error (Acknowledged)".equals(stato)){
							expr.equals(Allarme.model().STATO, 2);
							expr.equals(Allarme.model().ACKNOWLEDGED, 1);
						}
						if ("Error (Unacknowledged)".equals(stato)){
							expr.equals(Allarme.model().STATO, 2);
							expr.equals(Allarme.model().ACKNOWLEDGED, 0);
						}
					
					}
					
				}

				addAnd = true;
			}

			// se non e' una count inserisco l'ordinamento
			if (!isCount) {
				expr.sortOrder(SortOrder.ASC).addOrder(Allarme.model().ALIAS);

			}

		} catch (ServiceException e) {
			AllarmiService.log.error("Errore durante la creazione della query di ricerca", e);
			throw e;
		} catch (NotImplementedException e) {
			AllarmiService.log.error("Errore durante la creazione della query di ricerca", e);
			throw e;
		} catch (ExpressionNotImplementedException e) {
			AllarmiService.log.error("Errore durante la creazione della query di ricerca", e);
			throw e;
		} catch (ExpressionException e) {
			AllarmiService.log.error("Errore durante la creazione della query di ricerca", e);
			throw e;
		} catch (CoreException e) {
			AllarmiService.log.error("Errore durante la creazione della query di ricerca", e);
			throw e;
		}

		return expr;
	}

	private List<PortaDelegata> estraiPDAssociate(AllarmiSearchForm formRicerca, boolean apiImplSelected,
			List<IDServizio> listIDServizio) throws ExpressionNotImplementedException, ExpressionException,
			ServiceException, NotImplementedException, CoreException, UserInvalidException {
		IExpression exprPD = this.createPDExpression(this.portaDelegataDAO, formRicerca, false);
		IPaginatedExpression pagExpr = this.portaDelegataDAO.toPaginatedExpression(exprPD);
		pagExpr.offset(0).limit(this.LIMIT_SEARCH); 

		if(!apiImplSelected && listIDServizio!=null && !listIDServizio.isEmpty()) {

			List<IExpression> exprOrList = new ArrayList<IExpression>();
			for (IDServizio idServizio : listIDServizio) {
				
				IExpression exprIdServizio = this.portaDelegataDAO.newExpression();
				exprIdServizio.equals(PortaDelegata.model().TIPO_SOGGETTO_EROGATORE,idServizio.getSoggettoErogatore().getTipo());
				exprIdServizio.equals(PortaDelegata.model().NOME_SOGGETTO_EROGATORE,idServizio.getSoggettoErogatore().getNome());
				exprIdServizio.equals(PortaDelegata.model().TIPO_SERVIZIO,idServizio.getTipo());
				exprIdServizio.equals(PortaDelegata.model().NOME_SERVIZIO,idServizio.getNome());
				exprIdServizio.equals(PortaDelegata.model().VERSIONE_SERVIZIO,idServizio.getVersione());
				exprOrList.add(exprIdServizio);
				
			}
			pagExpr.or(exprOrList.toArray(new IExpression[1]));

		}
		List<PortaDelegata> findAll = this.portaDelegataDAO.findAll(pagExpr);
		return findAll;
	}

	private List<PortaApplicativa> estraiPAAssociate(AllarmiSearchForm formRicerca, boolean apiImplSelected,
			List<IDServizio> listIDServizio) throws ExpressionNotImplementedException, ExpressionException,
			ServiceException, NotImplementedException, CoreException, UserInvalidException {
		IExpression exprPA = this.createPAExpression(this.portaApplicativaDAO, formRicerca, false);
		IPaginatedExpression pagExpr = this.portaApplicativaDAO.toPaginatedExpression(exprPA);
		pagExpr.offset(0).limit(this.LIMIT_SEARCH);  

		if(!apiImplSelected && listIDServizio!=null && !listIDServizio.isEmpty()) {

			List<IExpression> exprOrList = new ArrayList<IExpression>();
			for (IDServizio idServizio : listIDServizio) {
				IExpression exprIdServizio = this.portaApplicativaDAO.newExpression();
				exprIdServizio.equals(PortaApplicativa.model().TIPO_SERVIZIO,idServizio.getTipo());
				exprIdServizio.equals(PortaApplicativa.model().NOME_SERVIZIO,idServizio.getNome());
				exprIdServizio.equals(PortaApplicativa.model().VERSIONE_SERVIZIO,idServizio.getVersione());
				boolean setSoggettoProprietario = false;
				if(formRicerca.getTipoNomeSoggettoLocale()!=null && 
						!StringUtils.isEmpty(formRicerca.getTipoNomeSoggettoLocale()) && !"--".equals(formRicerca.getTipoNomeSoggettoLocale())){
					setSoggettoProprietario = true; // impostato dentro createPAExpression
				}
				if(setSoggettoProprietario==false){
					exprIdServizio.equals(PortaApplicativa.model().ID_SOGGETTO.TIPO,idServizio.getSoggettoErogatore().getTipo());
					exprIdServizio.equals(PortaApplicativa.model().ID_SOGGETTO.NOME,idServizio.getSoggettoErogatore().getNome());
				}
				exprOrList.add(exprIdServizio);
				
			}
			pagExpr.or(exprOrList.toArray(new IExpression[1]));

		}
		
		List<PortaApplicativa> findAll = this.portaApplicativaDAO.findAll(pagExpr);
		return findAll;
	}
	
	private IExpression createPDExpression(IServiceSearchWithId<?, ?> dao, AllarmiSearchForm searchForm, boolean count)
			throws ExpressionNotImplementedException, ExpressionException, ServiceException, NotImplementedException, CoreException, UserInvalidException {
		IExpression expr = dao.newExpression();

		if(searchForm == null)
			return expr;

		expr.and();
		PortaDelegataModel model = PortaDelegata.model();
		expr.isNotNull(model.TIPO_SOGGETTO_EROGATORE);
		expr.isNotNull(model.NOME_SOGGETTO_EROGATORE);
		expr.isNotNull(model.TIPO_SERVIZIO);
		expr.isNotNull(model.NOME_SERVIZIO);
		expr.isNotNull(model.VERSIONE_SERVIZIO);

		if(searchForm.getPermessiUtenteOperatore()!=null){
			IExpression permessi = searchForm.getPermessiUtenteOperatore().toExpressionConfigurazioneServizi(dao, 
					model.ID_SOGGETTO.TIPO, model.ID_SOGGETTO.NOME, 
					model.TIPO_SOGGETTO_EROGATORE, model.NOME_SOGGETTO_EROGATORE, 
					model.TIPO_SERVIZIO, model.NOME_SERVIZIO, model.VERSIONE_SERVIZIO,
					false);
			expr.and(permessi);
		}

		// Protocollo
		String protocollo = null;
		// aggiungo la condizione sul protocollo se e' impostato e se e' presente piu' di un protocollo
		// protocollo e' impostato anche scegliendo la modalita'
		//		if (StringUtils.isNotEmpty(searchForm.getProtocollo()) && searchForm.isShowListaProtocolli()) {
		if (searchForm.isSetFiltroProtocollo()) {
			protocollo = searchForm.getProtocollo();
			impostaTipiCompatibiliConProtocollo(dao, model, expr, protocollo);
		}


		if(searchForm.getTipoNomeSoggettoLocale()!=null && 
				!StringUtils.isEmpty(searchForm.getTipoNomeSoggettoLocale()) && !"--".equals(searchForm.getTipoNomeSoggettoLocale())){

			String tipoSoggettoDestinatario = searchForm.getTipoSoggettoLocale();
			String nomeSoggettoDestinatario = searchForm.getSoggettoLocale();

			if(tipoSoggettoDestinatario!=null && !Costanti.VALUE_PARAMETRO_MODALITA_ALL.equals(tipoSoggettoDestinatario)) {
				expr.equals(model.ID_SOGGETTO.TIPO,tipoSoggettoDestinatario);
			}
			if(nomeSoggettoDestinatario!=null && !Costanti.VALUE_PARAMETRO_MODALITA_ALL.equals(nomeSoggettoDestinatario)) {
				expr.equals(model.ID_SOGGETTO.NOME,nomeSoggettoDestinatario);
			}
		}
		if (StringUtils.isNotBlank(searchForm.getNomeServizio())) {

			String servizioString = searchForm.getNomeServizio();
			IDServizio idServizio = ParseUtility.parseServizioSoggetto(servizioString);

			expr.equals(model.TIPO_SOGGETTO_EROGATORE,idServizio.getSoggettoErogatore().getTipo());
			expr.equals(model.NOME_SOGGETTO_EROGATORE,idServizio.getSoggettoErogatore().getNome());
			expr.equals(model.TIPO_SERVIZIO,idServizio.getTipo());
			expr.equals(model.NOME_SERVIZIO,idServizio.getNome());
			expr.equals(model.VERSIONE_SERVIZIO,idServizio.getVersione());
		}

		if(!count) {
			expr.sortOrder(SortOrder.ASC);
			expr.addOrder(model.ID_SOGGETTO.TIPO);
			expr.addOrder(model.ID_SOGGETTO.NOME);
			expr.addOrder(model.TIPO_SOGGETTO_EROGATORE);
			expr.addOrder(model.NOME_SOGGETTO_EROGATORE);
			expr.addOrder(model.TIPO_SERVIZIO);
			expr.addOrder(model.NOME_SERVIZIO);
			expr.addOrder(model.VERSIONE_SERVIZIO);
			expr.addOrder(model.NOME_AZIONE);
		}
		return expr;
	}

	private IExpression createPAExpression(IServiceSearchWithId<?, ?> dao, AllarmiSearchForm searchForm, boolean count) 
			throws ExpressionNotImplementedException, ExpressionException, ServiceException, NotImplementedException, CoreException, UserInvalidException{
		IExpression expr = dao.newExpression();

		if(searchForm == null)
			return expr;

		PortaApplicativaModel model = PortaApplicativa.model();
		expr.isNotNull(model.TIPO_SERVIZIO);
		expr.isNotNull(model.NOME_SERVIZIO);
		expr.isNotNull(model.VERSIONE_SERVIZIO);

		if(searchForm.getPermessiUtenteOperatore()!=null){
			IExpression permessi = searchForm.getPermessiUtenteOperatore().toExpressionConfigurazioneServizi(dao, 
					model.ID_SOGGETTO.TIPO, model.ID_SOGGETTO.NOME, 
					model.ID_SOGGETTO.TIPO, model.ID_SOGGETTO.NOME, 
					model.TIPO_SERVIZIO, model.NOME_SERVIZIO,model.VERSIONE_SERVIZIO,
					false);
			expr.and(permessi);
		}

		// Protocollo
		String protocollo = null;
		// aggiungo la condizione sul protocollo se e' impostato e se e' presente piu' di un protocollo
		// protocollo e' impostato anche scegliendo la modalita'
		//		if (StringUtils.isNotEmpty(searchForm.getProtocollo()) && searchForm.isShowListaProtocolli()) {
		if (searchForm.isSetFiltroProtocollo()) {
			protocollo = searchForm.getProtocollo();
			impostaTipiCompatibiliConProtocollo(dao, model, expr, protocollo);
		}

		boolean setSoggettoProprietario = false;
		if(searchForm.getTipoNomeSoggettoLocale()!=null && 
				!StringUtils.isEmpty(searchForm.getTipoNomeSoggettoLocale()) && !"--".equals(searchForm.getTipoNomeSoggettoLocale())){

			String tipoSoggettoDestinatario = searchForm.getTipoSoggettoLocale();
			String nomeSoggettoDestinatario = searchForm.getSoggettoLocale();

			if(tipoSoggettoDestinatario!=null && !Costanti.VALUE_PARAMETRO_MODALITA_ALL.equals(tipoSoggettoDestinatario)) {
				expr.equals(model.ID_SOGGETTO.TIPO,tipoSoggettoDestinatario);
			}
			if(nomeSoggettoDestinatario!=null && !Costanti.VALUE_PARAMETRO_MODALITA_ALL.equals(nomeSoggettoDestinatario)) {
				expr.equals(model.ID_SOGGETTO.NOME,nomeSoggettoDestinatario);
			}
			setSoggettoProprietario = true;
		}

		if (StringUtils.isNotBlank(searchForm.getNomeServizio())) {

			String servizioString = searchForm.getNomeServizio();
			IDServizio idServizio = ParseUtility.parseServizioSoggetto(servizioString);

			expr.equals(model.TIPO_SERVIZIO,idServizio.getTipo());
			expr.equals(model.NOME_SERVIZIO,idServizio.getNome());
			expr.equals(model.VERSIONE_SERVIZIO,idServizio.getVersione());
			if(setSoggettoProprietario==false){
				expr.equals(model.ID_SOGGETTO.TIPO,idServizio.getSoggettoErogatore().getTipo());
				expr.equals(model.ID_SOGGETTO.NOME,idServizio.getSoggettoErogatore().getNome());
			}
		}

		if(!count){
			expr.sortOrder(SortOrder.ASC);
			expr.addOrder(model.ID_SOGGETTO.TIPO);
			expr.addOrder(model.ID_SOGGETTO.NOME);
			expr.addOrder(model.TIPO_SERVIZIO);
			expr.addOrder(model.NOME_SERVIZIO);
			expr.addOrder(model.VERSIONE_SERVIZIO);
			expr.addOrder(model.NOME_AZIONE);
		}

		return expr;		

	}
	
	private void impostaTipiCompatibiliConProtocollo(IServiceSearchWithId<?, ?> dao, PortaDelegataModel model,	IExpression expr, String protocollo) throws ExpressionNotImplementedException, ExpressionException {
		// Se ho selezionato il protocollo il tipo dei servizi da includere nei risultati deve essere compatibile col protocollo scelto.
		IExpression expressionTipoServiziCompatibili = null;
		try {
			if(protocollo != null) {
				expressionTipoServiziCompatibili = DynamicUtilsServiceEngine.getExpressionTipiServiziCompatibiliConProtocollo(dao, model.TIPO_SERVIZIO, protocollo);
			}
		} catch (Exception e) {
			log.error("Si e' verificato un errore durante il calcolo dei tipi servizio compatibili con il protocollo scelto: "+ e.getMessage(), e);
		}

		if(expressionTipoServiziCompatibili != null)
			expr.and(expressionTipoServiziCompatibili);

		// Se ho selezionato il protocollo il tipo dei servizi da includere nei risultati deve essere compatibile col protocollo scelto.
		IExpression expressionTipoSoggettiMittenteCompatibili = null;
		try {
			if(protocollo != null) {
				expressionTipoSoggettiMittenteCompatibili = DynamicUtilsServiceEngine.getExpressionTipiSoggettiCompatibiliConProtocollo(dao, model.ID_SOGGETTO.TIPO, protocollo);
			}
		} catch (Exception e) {
			log.error("Si e' verificato un errore durante il calcolo dei tipi soggetto mittente compatibili con il protocollo scelto: "+ e.getMessage(), e);
		}

		if(expressionTipoSoggettiMittenteCompatibili != null)
			expr.and(expressionTipoSoggettiMittenteCompatibili);


		// Se ho selezionato il protocollo il tipo dei servizi da includere nei risultati deve essere compatibile col protocollo scelto.
		IExpression expressionTipoSoggettiDestinatarioCompatibili = null;
		try {
			if(protocollo != null) {
				expressionTipoSoggettiDestinatarioCompatibili = DynamicUtilsServiceEngine.getExpressionTipiSoggettiCompatibiliConProtocollo(dao, model.TIPO_SOGGETTO_EROGATORE, protocollo);
			}
		} catch (Exception e) {
			log.error("Si e' verificato un errore durante il calcolo dei tipi soggetto destinatario compatibili con il protocollo scelto: "+ e.getMessage(), e);
		}

		if(expressionTipoSoggettiDestinatarioCompatibili != null)
			expr.and(expressionTipoSoggettiDestinatarioCompatibili);
	}

	private void impostaTipiCompatibiliConProtocollo(IServiceSearchWithId<?, ?> dao, PortaApplicativaModel model,	IExpression expr, String protocollo) throws ExpressionNotImplementedException, ExpressionException {
		// Se ho selezionato il protocollo il tipo dei servizi da includere nei risultati deve essere compatibile col protocollo scelto.
		IExpression expressionTipoServiziCompatibili = null;
		try {
			if(protocollo != null) {
				expressionTipoServiziCompatibili = DynamicUtilsServiceEngine.getExpressionTipiServiziCompatibiliConProtocollo(dao, model.TIPO_SERVIZIO, protocollo);
			}
		} catch (Exception e) {
			log.error("Si e' verificato un errore durante il calcolo dei tipi servizio compatibili con il protocollo scelto: "+ e.getMessage(), e);
		}

		if(expressionTipoServiziCompatibili != null)
			expr.and(expressionTipoServiziCompatibili);

		// Se ho selezionato il protocollo il tipo dei servizi da includere nei risultati deve essere compatibile col protocollo scelto.
		IExpression expressionTipoSoggettiDestinatarioCompatibili = null;
		try {
			if(protocollo != null) {
				expressionTipoSoggettiDestinatarioCompatibili = DynamicUtilsServiceEngine.getExpressionTipiSoggettiCompatibiliConProtocollo(dao, model.ID_SOGGETTO.TIPO, protocollo);
			}
		} catch (Exception e) {
			log.error("Si e' verificato un errore durante il calcolo dei tipi soggetto destinatario compatibili con il protocollo scelto: "+ e.getMessage(), e);
		}

		if(expressionTipoSoggettiDestinatarioCompatibili != null)
			expr.and(expressionTipoSoggettiDestinatarioCompatibili);
	}

	@Override
	public boolean isShowParameters(Allarme configurazioneAllarme, Context context) throws Exception{
		try {

			IdPlugin idPlugin = new IdPlugin();
			idPlugin.setTipoPlugin(TipoPlugin.ALLARME.getValue());
			idPlugin.setTipo(configurazioneAllarme.getTipo());
			
			Plugin plugin = this.dynamicUtils.getPlugin(idPlugin);
			
			IDynamicLoader bl = DynamicFactory.getInstance().newDynamicLoader(TipoPlugin.ALLARME, configurazioneAllarme.getTipo(), plugin.getClassName(), AllarmiService.log);
			List<Parameter<?>> sdkParameters = bl.getParameters(context);
			
			return sdkParameters!=null && sdkParameters.size()>0;

		} catch (Exception e) {
			AllarmiService.log.error(e.getMessage(), e);
			return false;
		}
	}
	
	@Override
	public List<Parameter<?>> instanceParameters(Allarme configurazioneAllarme, Context context) throws Exception {

		try {
			List<Parameter<?>> res = null;

			IdPlugin idPlugin = new IdPlugin();
			idPlugin.setTipoPlugin(TipoPlugin.ALLARME.getValue());
			idPlugin.setTipo(configurazioneAllarme.getTipo());
			
			Plugin plugin = this.dynamicUtils.getPlugin(idPlugin);
			
			IDynamicLoader bl = DynamicFactory.getInstance().newDynamicLoader(TipoPlugin.ALLARME, configurazioneAllarme.getTipo(), plugin.getClassName(), AllarmiService.log);
			List<Parameter<?>> sdkParameters = bl.getParameters(context);
			
			if(sdkParameters!=null && sdkParameters.size()>0){
				
				res = new ArrayList<Parameter<?>>();
				
				for (Parameter<?> sdkParameter : sdkParameters) {
					Parameter<?> par = DynamicComponentUtils.createDynamicComponentParameter(sdkParameter, bl);
					((BaseComponent<?>)par).setContext(context);
					res.add(par);
				}
			}

			return res;

		} catch (Exception e) {
			AllarmiService.log.error(e.getMessage(), e);
			throw e;
		}
		//		return null;
	}

	@Override
	public boolean isUsableFilter(Allarme configurazioneAllarme, Context context) throws Exception{
		return _isUsable(configurazioneAllarme, context, true);
	}
	@Override
	public boolean isUsableGroupBy(Allarme configurazioneAllarme, Context context) throws Exception{
		return _isUsable(configurazioneAllarme, context, false);
	}
	public boolean _isUsable(Allarme configurazioneAllarme, Context context, boolean filter) throws Exception{
		
		try {
			IdPlugin idPlugin = new IdPlugin();
			idPlugin.setTipoPlugin(TipoPlugin.ALLARME.getValue());
			idPlugin.setTipo(configurazioneAllarme.getTipo());
			
			Plugin plugin = this.dynamicUtils.getPlugin(idPlugin);
			
			IDynamicLoader bl = DynamicFactory.getInstance().newDynamicLoader(TipoPlugin.ALLARME, configurazioneAllarme.getTipo(), plugin.getClassName(), AllarmiService.log);
			IAlarmProcessing alarmProcessing = (IAlarmProcessing) bl.newInstance();
			return filter ? alarmProcessing.isUsableFilter(context) : alarmProcessing.isUsableGroupBy(context);

		} catch (Exception e) {
			AllarmiService.log.error(e.getMessage(), e);
			throw e;
		}
	}
	
	@Override
	public String getParameterSectionTitle(Allarme configurazioneAllarme, Context context) throws Exception{
		try {
			IdPlugin idPlugin = new IdPlugin();
			idPlugin.setTipoPlugin(TipoPlugin.ALLARME.getValue());
			idPlugin.setTipo(configurazioneAllarme.getTipo());
			
			Plugin plugin = this.dynamicUtils.getPlugin(idPlugin);
			
			IDynamicLoader bl = DynamicFactory.getInstance().newDynamicLoader(TipoPlugin.ALLARME, configurazioneAllarme.getTipo(), plugin.getClassName(), AllarmiService.log);
			IAlarmProcessing alarmProcessing = (IAlarmProcessing) bl.newInstance();
			
			boolean groupBy = alarmProcessing.isUsableGroupBy(context);
			
			String s = alarmProcessing.getParameterSectionTitle();
			if(s==null || StringUtils.isEmpty(s)) {
				if(groupBy) {
					s = org.openspcoop2.monitor.engine.constants.Costanti.LABEL_ALLARMI_VALORI_DI_SOGLIA;
				}
				else {
					s = org.openspcoop2.monitor.engine.constants.Costanti.LABEL_ALLARMI_PARAMETRI;
				}
			}
			return s;

		} catch (Exception e) {
			AllarmiService.log.error(e.getMessage(), e);
			throw e;
		}
	}

	
	@Override
	public DialogInfo getCriteriAckDialogInfo(Allarme configurazioneAllarme, Context context) throws Exception{
		try {
			IdPlugin idPlugin = new IdPlugin();
			idPlugin.setTipoPlugin(TipoPlugin.ALLARME.getValue());
			idPlugin.setTipo(configurazioneAllarme.getTipo());
			
			Plugin plugin = this.dynamicUtils.getPlugin(idPlugin);
			
			IDynamicLoader bl = DynamicFactory.getInstance().newDynamicLoader(TipoPlugin.ALLARME, configurazioneAllarme.getTipo(), plugin.getClassName(), AllarmiService.log);
			IAlarmProcessing alarmProcessing = (IAlarmProcessing) bl.newInstance();
			return alarmProcessing.getDialogInfoAckCriteria(context);

		} catch (Exception e) {
			AllarmiService.log.error(e.getMessage(), e);
			throw e;
		}
	}
	

	@Override
	public void deleteAll() throws Exception {
	}

	
	@Override
	public ConfigurazioneAllarmeBean getAllarme(String nome)
			throws NotFoundException, ServiceException {
		try {
			Allarme find = this.allarmeSearchDAO.find(this.allarmeSearchDAO
					.newExpression().equals(Allarme.model().NOME,
							nome));

			if(find != null){
				IdPlugin idPlugin = new IdPlugin();
				idPlugin.setTipoPlugin(TipoPlugin.ALLARME.getValue());
				idPlugin.setTipo(find.getTipo());
				Plugin plugin = this.dynamicUtils.getPlugin(idPlugin);
				return new ConfigurazioneAllarmeBean(find, plugin);
			}
		} catch (ServiceException e) {
			AllarmiService.log.error(e.getMessage(), e);
			throw e;
		} catch (NotFoundException e) {
			AllarmiService.log.debug(e.getMessage(), e);
			throw e;
		} catch (MultipleResultException e) {
			AllarmiService.log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			AllarmiService.log.error(e.getMessage(), e);
		} catch (ExpressionNotImplementedException e) {
			AllarmiService.log.error(e.getMessage(), e);
		} catch (ExpressionException e) {
			AllarmiService.log.error(e.getMessage(), e);
		}

		return null;
	}

	@Override
	public AllarmeParametro getParametroByIdParametro(
			ConfigurazioneAllarmeBean allarme, String idParametro)
					throws NotFoundException, ServiceException {

		IExpression expr;
		try {
			expr = this.allarmeSearchDAO.newExpression();

			expr.equals(
					Allarme.model().ALLARME_PARAMETRO.ID_PARAMETRO,
					idParametro);
			expr.and().equals(Allarme.model().NOME,
					allarme.getNome());

			Allarme c = this.allarmeSearchDAO.find(expr);

			if (c != null
					&& c.getAllarmeParametroList().size() > 0) {

				for (AllarmeParametro par : allarme
						.getAllarmeParametroList()) {

					if (par.getIdParametro().equals(idParametro)) {
						return par;
					}
				}
			}

			return null;
		} catch (ServiceException e) {
			AllarmiService.log.error(e.getMessage(), e);
			throw e;
		} catch (NotImplementedException e) {
			AllarmiService.log.error(e.getMessage(), e);
		} catch (ExpressionNotImplementedException e) {
			AllarmiService.log.error(e.getMessage(), e);
		} catch (ExpressionException e) {
			AllarmiService.log.error(e.getMessage(), e);
		} catch (NotFoundException e) {
			AllarmiService.log.debug(e.getMessage(), e);
			throw e;
		} catch (MultipleResultException e) {
			AllarmiService.log.error(e.getMessage(), e);
		}

		return null;
	}


	@Override
	public List<String> nomeAllarmeAutoComplete(String input) {
		List<String> list = new ArrayList<String>();


		IExpression expr;
		try {
			//			expr = this.allarmeSearchDAO.newExpression();

			// utilizzo la stessa condizione dei ricerca che mi restituisce solo gli allarmi consentiti per utente
			expr  = this.createQuery(false, this.searchForm);

			if(!StringUtils.isEmpty(input)){
				int idx= input.indexOf("/");
				if(idx != -1){
					input = input.substring(idx + 1, input.length());
				}

				expr.ilike(Allarme.model().ALIAS, input.toLowerCase() , LikeMode.ANYWHERE);
			}

			// Ordinamento gia' presente nella query standard
			//			expr.sortOrder(SortOrder.ASC).addOrder(ConfigurazioneAllarme.model().NOME);

			IPaginatedExpression pagExpr = this.allarmeSearchDAO
					.toPaginatedExpression(expr);
			pagExpr.offset(0).limit(this.LIMIT_SEARCH);

			List<Object> select = this.allarmeSearchDAO.select(pagExpr, true, Allarme.model().ALIAS);

			if(select != null && select.size() > 0)
				for (Object object : select) {
					list.add((String) object);
				}

		} catch (ServiceException e) {
			log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			log.error(e.getMessage(), e);
		}catch (NotFoundException e) {
			log.debug(e.getMessage());
		} catch (ExpressionNotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionException e) {
			log.error(e.getMessage(), e);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		return list;
	}
	

	@Override
	public List<AllarmeHistory> findAllHistory(long idAllarme,int start,int limit) throws ServiceException, NotImplementedException, ExpressionNotImplementedException, ExpressionException{
		IPaginatedExpression pagExpr = this.allarmeHistorySearchDAO.newPaginatedExpression();
		JDBCAllarmeHistoryServiceSearch jdbcInterfaceAudit = (JDBCAllarmeHistoryServiceSearch) this.allarmeHistorySearchDAO;
		CustomField cf = new CustomField("id_allarme", Long.class, "id_allarme", jdbcInterfaceAudit.getFieldConverter().toTable(AllarmeHistory.model()));
		pagExpr.equals(cf, idAllarme);
		pagExpr.offset(start);
		pagExpr.limit(limit);
		pagExpr.sortOrder(SortOrder.DESC);
		pagExpr.addOrder(AllarmeHistory.model().TIMESTAMP_UPDATE);
		
		List<AllarmeHistory> findAll = this.allarmeHistorySearchDAO.findAll(pagExpr);

		if(findAll != null && findAll.size() > 0){
			List<AllarmeHistory> toRet = new ArrayList<AllarmeHistory>();

			for (AllarmeHistory al : findAll) {
				toRet.add(new ConfigurazioneAllarmeHistoryBean(al));
			}

			return toRet;
		}
		
		return new ArrayList<AllarmeHistory>();
		
	}
	
	@Override
	public long countAllHistory(long idAllarme) throws ServiceException, NotImplementedException, ExpressionNotImplementedException, ExpressionException{
		
		IExpression expr = this.allarmeHistorySearchDAO.newExpression();
		JDBCAllarmeHistoryServiceSearch jdbcInterfaceAudit = (JDBCAllarmeHistoryServiceSearch) this.allarmeHistorySearchDAO;
		CustomField cf = new CustomField("id_allarme", Long.class, "id_allarme", jdbcInterfaceAudit.getFieldConverter().toTable(AllarmeHistory.model()));
		expr.equals(cf, idAllarme);
		return this.allarmeHistorySearchDAO.count(expr).longValue();
		
	}
	
	@Override
	public void addHistory(AllarmeHistory history) throws ServiceException, NotImplementedException{
		this.allarmeHistoryDAO.create(history);
	}
	
	@Override
	public IAlarm getAlarm(String name) throws AlarmException{
		try{
			return AlarmManager.getAlarm(name, LoggerManager.getPddMonitorCoreLogger(), DAOFactory.getInstance(AllarmiService.log));
		}catch(Exception e){
			throw new AlarmException(e.getMessage(),e);
		}
	}
}
