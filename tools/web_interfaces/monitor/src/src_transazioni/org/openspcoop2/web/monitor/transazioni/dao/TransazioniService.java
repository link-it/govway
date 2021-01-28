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
package org.openspcoop2.web.monitor.transazioni.dao;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.commons.dao.DAOFactory;
import org.openspcoop2.core.config.constants.TipoAutenticazione;
import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.transazioni.CredenzialeMittente;
import org.openspcoop2.core.transazioni.DumpAllegato;
import org.openspcoop2.core.transazioni.DumpContenuto;
import org.openspcoop2.core.transazioni.DumpHeaderTrasporto;
import org.openspcoop2.core.transazioni.DumpMessaggio;
import org.openspcoop2.core.transazioni.Transazione;
import org.openspcoop2.core.transazioni.constants.PddRuolo;
import org.openspcoop2.core.transazioni.constants.TipoAPI;
import org.openspcoop2.core.transazioni.constants.TipoMessaggio;
import org.openspcoop2.core.transazioni.dao.ICredenzialeMittenteService;
import org.openspcoop2.core.transazioni.dao.IDBDumpMessaggioServiceSearch;
import org.openspcoop2.core.transazioni.dao.ITransazioneServiceSearch;
import org.openspcoop2.core.transazioni.model.TransazioneModel;
import org.openspcoop2.core.transazioni.utils.TipoCredenzialeMittente;
import org.openspcoop2.core.transazioni.utils.TransazioniIndexUtils;
import org.openspcoop2.core.transazioni.utils.credenziali.AbstractCredenzialeList;
import org.openspcoop2.core.transazioni.utils.credenziali.CredenzialeClientAddress;
import org.openspcoop2.core.transazioni.utils.credenziali.CredenzialeSearchApi;
import org.openspcoop2.core.transazioni.utils.credenziali.CredenzialeSearchClientAddress;
import org.openspcoop2.core.transazioni.utils.credenziali.CredenzialeSearchEvento;
import org.openspcoop2.core.transazioni.utils.credenziali.CredenzialeSearchGruppo;
import org.openspcoop2.core.transazioni.utils.credenziali.CredenzialeSearchToken;
import org.openspcoop2.core.transazioni.utils.credenziali.CredenzialeSearchTrasporto;
import org.openspcoop2.generic_project.beans.Function;
import org.openspcoop2.generic_project.beans.FunctionField;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.NonNegativeNumber;
import org.openspcoop2.generic_project.beans.UnixTimestampIntervalField;
import org.openspcoop2.generic_project.dao.IDBServiceUtilities;
import org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities;
import org.openspcoop2.generic_project.exception.ExpressionException;
import org.openspcoop2.generic_project.exception.ExpressionNotImplementedException;
import org.openspcoop2.generic_project.exception.MultipleResultException;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.generic_project.exception.NotImplementedException;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.generic_project.expression.IExpression;
import org.openspcoop2.generic_project.expression.IPaginatedExpression;
import org.openspcoop2.generic_project.expression.Index;
import org.openspcoop2.generic_project.expression.LikeMode;
import org.openspcoop2.generic_project.expression.SortOrder;
import org.openspcoop2.generic_project.expression.impl.sql.ISQLFieldConverter;
import org.openspcoop2.generic_project.utils.ServiceManagerProperties;
import org.openspcoop2.monitor.engine.condition.EsitoUtils;
import org.openspcoop2.monitor.engine.condition.FilterImpl;
import org.openspcoop2.monitor.engine.config.BasicServiceLibrary;
import org.openspcoop2.monitor.engine.config.BasicServiceLibraryReader;
import org.openspcoop2.monitor.engine.config.SearchServiceLibrary;
import org.openspcoop2.monitor.engine.config.SearchServiceLibraryReader;
import org.openspcoop2.monitor.engine.config.TransactionServiceLibrary;
import org.openspcoop2.monitor.engine.config.TransactionServiceLibraryReader;
import org.openspcoop2.core.plugins.ConfigurazioneServizioAzione;
import org.openspcoop2.core.plugins.dao.IConfigurazioneServizioAzioneServiceSearch;
import org.openspcoop2.monitor.engine.config.ricerche.ConfigurazioneRicerca;
import org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazione;
import org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazioneRisorsaContenuto;
import org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazioneStato;
import org.openspcoop2.monitor.engine.config.transazioni.dao.IConfigurazioneTransazioneServiceSearch;
import org.openspcoop2.monitor.engine.dynamic.DynamicFactory;
import org.openspcoop2.monitor.engine.dynamic.IDynamicLoader;
import org.openspcoop2.monitor.sdk.condition.Context;
import org.openspcoop2.monitor.sdk.condition.IFilter;
import org.openspcoop2.monitor.sdk.exceptions.SearchException;
import org.openspcoop2.monitor.sdk.parameters.Parameter;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.web.monitor.core.bean.BaseSearchForm;
import org.openspcoop2.web.monitor.core.constants.CaseSensitiveMatch;
import org.openspcoop2.web.monitor.core.constants.Costanti;
import org.openspcoop2.web.monitor.core.constants.ModalitaRicercaTransazioni;
import org.openspcoop2.web.monitor.core.constants.TipoMatch;
import org.openspcoop2.web.monitor.core.constants.TipologiaRicerca;
import org.openspcoop2.web.monitor.core.core.PddMonitorProperties;
import org.openspcoop2.web.monitor.core.core.PermessiUtenteOperatore;
import org.openspcoop2.web.monitor.core.core.Utility;
import org.openspcoop2.web.monitor.core.dao.MBeanUtilsService;
import org.openspcoop2.web.monitor.core.datamodel.ResLive;
import org.openspcoop2.web.monitor.core.dynamic.DynamicComponentUtils;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;
import org.openspcoop2.web.monitor.core.utils.ParseUtility;
import org.openspcoop2.web.monitor.transazioni.bean.DumpMessaggioBean;
import org.openspcoop2.web.monitor.transazioni.bean.TransazioneBean;
import org.openspcoop2.web.monitor.transazioni.bean.TransazioniSearchForm;
import org.openspcoop2.web.monitor.transazioni.datamodel.TransazioniDM;
import org.slf4j.Logger;

/**
 * TransazioniService
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class TransazioniService implements ITransazioniService {

	private transient Logger log = null;

	private TransazioniSearchForm searchForm;
	private Integer liveMaxResults;

	// private IAdvancedTransazioneService service;
	// private IDumpMessaggioServiceSearch dumpService;

	private DAOFactory daoFactory;
	
	private IConfigurazioneServizioAzioneServiceSearch confSerAzSearchDAO;
	private IConfigurazioneTransazioneServiceSearch confTransazioneSearchDAO;

	private org.openspcoop2.monitor.engine.config.transazioni.dao.IServiceManager transazioniPluginsServiceManager;
	private org.openspcoop2.monitor.engine.config.ricerche.dao.IServiceManager ricerchePluginsServiceManager;
	private org.openspcoop2.core.plugins.dao.IServiceManager basePluginsServiceManager;

	private org.openspcoop2.core.transazioni.dao.IServiceManager transazioniServiceManager;
	private org.openspcoop2.core.transazioni.dao.ITransazioneService transazioniDAO;
	private org.openspcoop2.core.transazioni.dao.ITransazioneApplicativoServerServiceSearch transazioniApplicativoServerSearchDAO;
	private org.openspcoop2.core.transazioni.dao.ITransazioneServiceSearch transazioniSearchDAO;
	private org.openspcoop2.core.transazioni.dao.ICredenzialeMittenteService credenzialiMittenteDAO;

	private org.openspcoop2.core.transazioni.dao.IDumpMessaggioServiceSearch dumpMessaggioSearchDAO;
	
	private org.openspcoop2.core.commons.search.dao.IServiceManager utilsServiceManager;

	private ISQLFieldConverter transazioniFieldConverter = null;
	
	private BasicServiceLibraryReader basicServiceLibraryReader = null;
	private TransactionServiceLibraryReader transactionServiceLibraryReader = null;
	private SearchServiceLibraryReader searchServiceLibraryReader = null;
	
	private Integer liveUltimiGiorni = null;
	
	private boolean isAttivoSqlFilterTransazioniIntegrationManager = true;
	
	private List<Index> forceIndexAndamentoTemporaleFindAll;
	private List<Index> forceIndexAndamentoTemporaleCount;
	private List<Index> forceIndexIdApplicativoFindAll;
	private List<Index> forceIndexIdApplicativoCount;
	private List<Index> forceIndexIdMessaggioRichiestaFindAll;
	private List<Index> forceIndexIdMessaggioRichiestaCount;
	private List<Index> forceIndexIdMessaggioRispostaFindAll;
	private List<Index> forceIndexIdMessaggioRispostaCount;
	private List<Index> forceIndexIdCollaborazioneFindAll;
	private List<Index> forceIndexIdCollaborazioneCount;
	private List<Index> forceIndexRiferimentoIdRichiestaFindAll;
	private List<Index> forceIndexRiferimentoIdRichiestaCount;
	private List<Index> forceIndexIdTransazioneFindAll;
	private List<Index> forceIndexIdTransazioneCount;
	private List<Index> forceIndexGetByIdTransazione;
	private void initForceIndex(PddMonitorProperties govwayMonitorProperties) throws Exception{
		Properties repositoryExternal = govwayMonitorProperties.getExternalForceIndexRepository();
		this.forceIndexAndamentoTemporaleFindAll = convertForceIndexList(govwayMonitorProperties.getTransazioniForceIndexAndamentoTemporaleFindAll(repositoryExternal));
		this.forceIndexAndamentoTemporaleCount = convertForceIndexList(govwayMonitorProperties.getTransazioniForceIndexAndamentoTemporaleCount(repositoryExternal));
		this.forceIndexIdApplicativoFindAll = convertForceIndexList(govwayMonitorProperties.getTransazioniForceIndexIdApplicativoFindAll(repositoryExternal));
		this.forceIndexIdApplicativoCount = convertForceIndexList(govwayMonitorProperties.getTransazioniForceIndexIdApplicativoCount(repositoryExternal));
		this.forceIndexIdMessaggioRichiestaFindAll = convertForceIndexList(govwayMonitorProperties.getTransazioniForceIndexIdMessaggioRichiestaFindAll(repositoryExternal));
		this.forceIndexIdMessaggioRichiestaCount = convertForceIndexList(govwayMonitorProperties.getTransazioniForceIndexIdMessaggioRichiestaCount(repositoryExternal));
		this.forceIndexIdMessaggioRispostaFindAll = convertForceIndexList(govwayMonitorProperties.getTransazioniForceIndexIdMessaggioRispostaFindAll(repositoryExternal));
		this.forceIndexIdMessaggioRispostaCount = convertForceIndexList(govwayMonitorProperties.getTransazioniForceIndexIdMessaggioRispostaCount(repositoryExternal));
		this.forceIndexIdCollaborazioneFindAll = convertForceIndexList(govwayMonitorProperties.getTransazioniForceIndexIdCollaborazioneFindAll(repositoryExternal));
		this.forceIndexIdCollaborazioneCount = convertForceIndexList(govwayMonitorProperties.getTransazioniForceIndexIdCollaborazioneCount(repositoryExternal));
		this.forceIndexRiferimentoIdRichiestaFindAll = convertForceIndexList(govwayMonitorProperties.getTransazioniForceIndexRiferimentoIdRichiestaFindAll(repositoryExternal));
		this.forceIndexRiferimentoIdRichiestaCount = convertForceIndexList(govwayMonitorProperties.getTransazioniForceIndexRiferimentoIdRichiestaCount(repositoryExternal));
		this.forceIndexIdTransazioneFindAll = convertForceIndexList(govwayMonitorProperties.getTransazioniForceIndexIdTransazioneFindAll(repositoryExternal));
		this.forceIndexIdTransazioneCount = convertForceIndexList(govwayMonitorProperties.getTransazioniForceIndexIdTransazioneCount(repositoryExternal));
		this.forceIndexGetByIdTransazione = convertForceIndexList(govwayMonitorProperties.getTransazioniForceIndexGetByIdTransazione(repositoryExternal));
	}
	private List<Index> convertForceIndexList(List<String> l){
		if(l!=null && l.size()>0){
			List<Index> li = new ArrayList<Index>();
			for (String index : l) {
				li.add(new Index(Transazione.model(),index));
			}
			return li;
		}
		return null;
	}
	private List<Index> getIndexFindAll(){
		ModalitaRicercaTransazioni ricerca = ModalitaRicercaTransazioni.getFromString(this.searchForm.getModalitaRicercaStorico());
		switch (ricerca) {
		case ANDAMENTO_TEMPORALE:
		case RICERCA_LIBERA:
		case MITTENTE_TOKEN_INFO:
		case MITTENTE_SOGGETTO:
		case MITTENTE_APPLICATIVO:
		case MITTENTE_IDENTIFICATIVO_AUTENTICATO:
		case MITTENTE_INDIRIZZO_IP:
			return this.forceIndexAndamentoTemporaleFindAll;
		case ID_APPLICATIVO:
			return this.forceIndexIdApplicativoFindAll;
		case ID_MESSAGGIO:
			org.openspcoop2.web.monitor.core.constants.TipoMessaggio tipoMessaggio = 
				org.openspcoop2.web.monitor.core.constants.TipoMessaggio.valueOf(this.searchForm.getTipoIdMessaggio());
			switch (tipoMessaggio) {
			case Richiesta:
				return this.forceIndexIdMessaggioRichiestaFindAll;
			case Risposta:
				return this.forceIndexIdMessaggioRispostaFindAll;
			case Collaborazione:
				return this.forceIndexIdCollaborazioneFindAll;
			case RiferimentoRichiesta:
				return this.forceIndexRiferimentoIdRichiestaFindAll;
			}
			break;
		case ID_TRANSAZIONE:
			return this.forceIndexIdTransazioneFindAll;
		}
		return null;
	}
	private List<Index> getIndexCount(){
		ModalitaRicercaTransazioni ricerca = ModalitaRicercaTransazioni.getFromString(this.searchForm.getModalitaRicercaStorico());
		switch (ricerca) {
		case ANDAMENTO_TEMPORALE:
		case RICERCA_LIBERA:
		case MITTENTE_TOKEN_INFO:
		case MITTENTE_SOGGETTO:
		case MITTENTE_APPLICATIVO:
		case MITTENTE_IDENTIFICATIVO_AUTENTICATO:
		case MITTENTE_INDIRIZZO_IP:
			return this.forceIndexAndamentoTemporaleCount;
		case ID_APPLICATIVO:
			return this.forceIndexIdApplicativoCount;
		case ID_MESSAGGIO:
			org.openspcoop2.web.monitor.core.constants.TipoMessaggio tipoMessaggio = 
			org.openspcoop2.web.monitor.core.constants.TipoMessaggio.valueOf(this.searchForm.getTipoIdMessaggio());
			switch (tipoMessaggio) {
			case Richiesta:
				return this.forceIndexIdMessaggioRichiestaCount;
			case Risposta:
				return this.forceIndexIdMessaggioRispostaCount;
			case Collaborazione:
				return this.forceIndexIdCollaborazioneCount;
			case RiferimentoRichiesta:
				return this.forceIndexRiferimentoIdRichiestaCount;
			}
			break;
		case ID_TRANSAZIONE:
			return this.forceIndexIdTransazioneCount;
		}
		return null;
	}

	public TransazioniService() {
		this.log =  LoggerManager.getPddMonitorSqlLogger();
		try {

			this.daoFactory = DAOFactory.getInstance(this.log);
			
			// init Service Manager (Transazioni.plugins)
			this.transazioniPluginsServiceManager = 
					(org.openspcoop2.monitor.engine.config.transazioni.dao.IServiceManager) this.daoFactory.getServiceManager(org.openspcoop2.monitor.engine.config.transazioni.utils.ProjectInfo.getInstance(),this.log);
			this.confTransazioneSearchDAO = this.transazioniPluginsServiceManager
					.getConfigurazioneTransazioneServiceSearch();

			// init Service Manager (ricerche.plugins)
			this.ricerchePluginsServiceManager = 
					(org.openspcoop2.monitor.engine.config.ricerche.dao.IServiceManager) this.daoFactory.getServiceManager(org.openspcoop2.monitor.engine.config.ricerche.utils.ProjectInfo.getInstance(),this.log);

			// init Service Manager (base.plugins)
			this.basePluginsServiceManager = 
					(org.openspcoop2.core.plugins.dao.IServiceManager) this.daoFactory.getServiceManager(org.openspcoop2.core.plugins.utils.ProjectInfo.getInstance(),this.log);
			this.confSerAzSearchDAO = this.basePluginsServiceManager
					.getConfigurazioneServizioAzioneServiceSearch();

			this.transazioniServiceManager = 
					(org.openspcoop2.core.transazioni.dao.IServiceManager) this.daoFactory.getServiceManager(org.openspcoop2.core.transazioni.utils.ProjectInfo.getInstance(),this.log);

			this.transazioniSearchDAO = this.transazioniServiceManager.getTransazioneServiceSearch();
			this.transazioniDAO = this.transazioniServiceManager.getTransazioneService();
			this.transazioniApplicativoServerSearchDAO = this.transazioniServiceManager.getTransazioneApplicativoServerServiceSearch();
			this.credenzialiMittenteDAO = this.transazioniServiceManager.getCredenzialeMittenteService();
			this.dumpMessaggioSearchDAO = this.transazioniServiceManager.getDumpMessaggioServiceSearch();

			this.transazioniFieldConverter = ((IDBServiceUtilities<?>)this.transazioniSearchDAO).getFieldConverter(); 

			this.utilsServiceManager = 
					(org.openspcoop2.core.commons.search.dao.IServiceManager) this.daoFactory.getServiceManager(org.openspcoop2.core.commons.search.utils.ProjectInfo.getInstance(),this.log);
				
			PddMonitorProperties monitorProperties = PddMonitorProperties.getInstance(this.log);
			
			this.liveUltimiGiorni = monitorProperties.getTransazioniLiveUltimiGiorni();
			
			if(monitorProperties.isAttivoModuloTransazioniPersonalizzate() || monitorProperties.isAttivoModuloRicerchePersonalizzate()){
				
				this.basicServiceLibraryReader = new BasicServiceLibraryReader( (this.basePluginsServiceManager), 
						(this.utilsServiceManager), true);
			
				if(monitorProperties.isAttivoModuloTransazioniPersonalizzate() ){
					this.transactionServiceLibraryReader = 
							new TransactionServiceLibraryReader( (this.transazioniPluginsServiceManager), true);
				}
				if(monitorProperties.isAttivoModuloRicerchePersonalizzate() ){
					this.searchServiceLibraryReader = 
							new SearchServiceLibraryReader( (this.ricerchePluginsServiceManager), true);
				}
			}
			
			this.isAttivoSqlFilterTransazioniIntegrationManager = monitorProperties.isAttivoSqlFilterTransazioniIntegrationManager();
			
			this.initForceIndex(monitorProperties);
			
		} catch (Exception e) {
			this.log.error(e.getMessage(), e);
		}

		this.liveMaxResults = 50;
	}

	public TransazioniService(Connection con, boolean autoCommit, Logger log) {
		this(con,autoCommit, null, log);
	}
	public TransazioniService(Connection con, boolean autoCommit, ServiceManagerProperties serviceManagerProperties, Logger log) {
		this.log =  log;
		try {

			this.daoFactory = DAOFactory.getInstance(this.log);
			
			// init Service Manager (Transazioni.plugins)
			this.transazioniPluginsServiceManager = 
					(org.openspcoop2.monitor.engine.config.transazioni.dao.IServiceManager) this.daoFactory.getServiceManager(org.openspcoop2.monitor.engine.config.transazioni.utils.ProjectInfo.getInstance(),con,autoCommit,serviceManagerProperties,this.log);
			this.confTransazioneSearchDAO = this.transazioniPluginsServiceManager
					.getConfigurazioneTransazioneServiceSearch();

			// init Service Manager (ricerche.plugins)
			this.ricerchePluginsServiceManager = 
					(org.openspcoop2.monitor.engine.config.ricerche.dao.IServiceManager) this.daoFactory.getServiceManager(org.openspcoop2.monitor.engine.config.ricerche.utils.ProjectInfo.getInstance(),con,autoCommit,serviceManagerProperties,this.log);

			// init Service Manager (base.plugins)
			this.basePluginsServiceManager = 
					(org.openspcoop2.core.plugins.dao.IServiceManager) this.daoFactory.getServiceManager(org.openspcoop2.core.plugins.utils.ProjectInfo.getInstance(),con,autoCommit,serviceManagerProperties, this.log);
			this.confSerAzSearchDAO = this.basePluginsServiceManager
					.getConfigurazioneServizioAzioneServiceSearch();

			this.transazioniServiceManager = 
					(org.openspcoop2.core.transazioni.dao.IServiceManager) this.daoFactory.getServiceManager(org.openspcoop2.core.transazioni.utils.ProjectInfo.getInstance(),con,autoCommit,serviceManagerProperties,this.log);

			this.transazioniSearchDAO = this.transazioniServiceManager.getTransazioneServiceSearch();
			this.transazioniDAO = this.transazioniServiceManager.getTransazioneService();
			this.transazioniApplicativoServerSearchDAO = this.transazioniServiceManager.getTransazioneApplicativoServerServiceSearch();
			this.credenzialiMittenteDAO = this.transazioniServiceManager.getCredenzialeMittenteService();
			this.dumpMessaggioSearchDAO = this.transazioniServiceManager.getDumpMessaggioServiceSearch();

			this.transazioniFieldConverter = ((IDBServiceUtilities<?>)this.transazioniSearchDAO).getFieldConverter(); 

			this.utilsServiceManager = 
					(org.openspcoop2.core.commons.search.dao.IServiceManager) this.daoFactory.getServiceManager(org.openspcoop2.core.commons.search.utils.ProjectInfo.getInstance(), con,autoCommit,serviceManagerProperties,this.log);
			
			PddMonitorProperties monitorProperties = PddMonitorProperties.getInstance(this.log);
			
			this.liveUltimiGiorni = monitorProperties.getTransazioniLiveUltimiGiorni();
			
			if(monitorProperties.isAttivoModuloTransazioniPersonalizzate() || monitorProperties.isAttivoModuloRicerchePersonalizzate()){
			
				this.basicServiceLibraryReader = new BasicServiceLibraryReader( (this.basePluginsServiceManager), 
						(this.utilsServiceManager), true);
				
				if(monitorProperties.isAttivoModuloTransazioniPersonalizzate() ){
					this.transactionServiceLibraryReader = 
						new TransactionServiceLibraryReader( (this.transazioniPluginsServiceManager), true);
				}
				if(monitorProperties.isAttivoModuloRicerchePersonalizzate() ){
					this.searchServiceLibraryReader = 
						new SearchServiceLibraryReader( (this.ricerchePluginsServiceManager), true);
				}
				
			}
			
			this.isAttivoSqlFilterTransazioniIntegrationManager = monitorProperties.isAttivoSqlFilterTransazioniIntegrationManager();
			
			this.initForceIndex(monitorProperties);
			
		} catch (Exception e) {
			this.log.error(e.getMessage(), e);
		}

		this.liveMaxResults = 50;
	}

	@Override
	public ITransazioniApplicativoServerService getTransazioniApplicativoServerService() {
		return new TransazioniApplicativoServerService(this.daoFactory, this.transazioniServiceManager, this.transazioniApplicativoServerSearchDAO);
	}
	
	@Override
	public void setLiveMaxResults(Integer limit) {
		this.liveMaxResults = limit;
	}
	
	@Override
	public void setSearch(TransazioniSearchForm search) {
		this.searchForm  = search;
	}

	 @Override
	public TransazioniSearchForm getSearch() {
		return this.searchForm;
	}

	private void setOrderField(IExpression expr , SortOrder sortOrder, String sortField, boolean isCount) throws Exception{
		if(sortField != null){
			if(sortField.equals(TransazioniDM.COL_DATA_LATENZA_TOTALE)){
				if(!isCount){
					UnixTimestampIntervalField latenza = new UnixTimestampIntervalField("tempoRisposta", this.transazioniFieldConverter, true,
							Transazione.model().DATA_USCITA_RISPOSTA, Transazione.model().DATA_INGRESSO_RICHIESTA);

					expr.sortOrder(sortOrder);
					expr.addOrder(latenza);
				}
				expr.isNotNull(Transazione.model().DATA_USCITA_RISPOSTA);
				expr.isNotNull(Transazione.model().DATA_INGRESSO_RICHIESTA);

			} else if(sortField.equals(TransazioniDM.COL_DATA_LATENZA_SERVIZIO)){
				if(!isCount){
					UnixTimestampIntervalField latenza = new UnixTimestampIntervalField("tempoRisposta", this.transazioniFieldConverter, true,
							Transazione.model().DATA_INGRESSO_RISPOSTA, Transazione.model().DATA_USCITA_RICHIESTA);
					expr.sortOrder(sortOrder);
					expr.addOrder(latenza);
				}
				expr.isNotNull(Transazione.model().DATA_INGRESSO_RISPOSTA);
				expr.isNotNull(Transazione.model().DATA_USCITA_RICHIESTA);

			} else if(sortField.equals(TransazioniDM.COL_DATA_INGRESSO_RICHIESTA) && !isCount){
				expr.sortOrder(sortOrder);
				expr.addOrder(Transazione.model().DATA_INGRESSO_RICHIESTA);	
			} 

			return;
		}

		if(!isCount){
			// ordinamento di default
			expr.sortOrder(sortOrder);
			expr.addOrder(Transazione.model().DATA_INGRESSO_RICHIESTA);
		}

	}

	@Override
	public List<TransazioneBean> findAll(int start, int limit, SortOrder sortOrder) {
		return findAll(start, limit, sortOrder, null); 
	}
	@Override
	public List<TransazioneBean> findAll(int start, int limit, SortOrder sortOrder, String sortField) {

		List<TransazioneBean> listaBean = new ArrayList<TransazioneBean>();
		try {
			this.log.debug("Find All + Limit + Sorting: SortOrder["+sortOrder.toString()+"], SortField["+sortField+"]");

			IExpression expr = this.transazioniSearchDAO.newExpression();

			parseExpressionFilter(expr, false, false);

			setOrderField(expr, sortOrder, sortField,false); 

			IPaginatedExpression pagExpr = this.transazioniSearchDAO.toPaginatedExpression(expr);

			pagExpr.offset(start).limit(limit);

			List<Index> forceIndexFindAll = this.getIndexFindAll();
			if(forceIndexFindAll!=null && forceIndexFindAll.size()>0){
				for (Index index : forceIndexFindAll) {
					pagExpr.addForceIndex(index);	
				}
			}
			
			TransazioniIndexUtils.enableSoloColonneIndicizzateFullIndexSearch(pagExpr);
			
			List<Transazione> list = this.transazioniSearchDAO.findAll(pagExpr);
			if(list!= null && list.size() > 0)
				for (Transazione transazione : list) {
					TransazioneBean bean = new TransazioneBean(transazione, this.searchForm!=null ? this.searchForm.getSoggettoPddMonitor() : null);
					
					// Integrazione dei dati delle credenziali
					bean.normalizeRichiedenteInfo(transazione, bean, this);
					
					this.normalizeInfoTransazioniFromCredenzialiMittenteUriApi(bean, transazione);
					bean.normalizeTipoApiInfo(this.utilsServiceManager, this.log);
					bean.normalizeOperazioneInfo(this.utilsServiceManager, this.log);
					
					this.normalizeInfoTransazioniFromCredenzialiMittenteGruppi(bean, transazione);
					
					boolean normalizeHttpReturnCode = true;
					this.normalizeInfoTransazioniFromCredenzialiMittenteEventi(bean, transazione, normalizeHttpReturnCode);
					
					listaBean.add(bean);
				}
		} catch (Exception e) {
			this.log.error(e.getMessage(), e);
		}
		return listaBean;
	}

	@Override
	public List<TransazioneBean> findAll(int start, int limit) {
		List<TransazioneBean> listaBean = new ArrayList<TransazioneBean>();
		try {
			this.log.debug("Find All + Limit");

			IExpression expr = this.transazioniSearchDAO.newExpression();

			parseExpressionFilter(expr, false, false);

			SortOrder sortOrder = this.searchForm.getSortOrder();
			String sortField = this.searchForm.getSortField();
			// ordinamento di default
			setOrderField(expr, sortOrder, sortField,false); 
			//			setOrderField(expr, SortOrder.DESC, null,false);

			IPaginatedExpression pagExpr = this.transazioniSearchDAO
					.toPaginatedExpression(expr);

			pagExpr.offset(start).limit(limit);

			List<Index> forceIndexFindAll = this.getIndexFindAll();
			if(forceIndexFindAll!=null && forceIndexFindAll.size()>0){
				for (Index index : forceIndexFindAll) {
					pagExpr.addForceIndex(index);	
				}
			}
			
			TransazioniIndexUtils.enableSoloColonneIndicizzateFullIndexSearch(pagExpr);
			
			List<Transazione> list = this.transazioniSearchDAO.findAll(pagExpr);
			if(list!= null && list.size() > 0)
				for (Transazione transazione : list) {
					TransazioneBean bean = new TransazioneBean(transazione, this.searchForm!=null ? this.searchForm.getSoggettoPddMonitor() : null);
					
					// Integrazione dei dati delle credenziali
					bean.normalizeRichiedenteInfo(transazione, bean, this);
					
					this.normalizeInfoTransazioniFromCredenzialiMittenteUriApi(bean, transazione);
					bean.normalizeTipoApiInfo(this.utilsServiceManager, this.log);
					bean.normalizeOperazioneInfo(this.utilsServiceManager, this.log);
					
					this.normalizeInfoTransazioniFromCredenzialiMittenteGruppi(bean, transazione);
					
					boolean normalizeHttpReturnCode = true;
					this.normalizeInfoTransazioniFromCredenzialiMittenteEventi(bean, transazione, normalizeHttpReturnCode);
					
					listaBean.add(bean);
				}
		} catch (Exception e) {
			this.log.error(e.getMessage(), e);
		}
		return listaBean;
	}

	@Override
	public int totalCount(SortOrder sortOrder, String sortField) {
		try {

			this.log.debug("Count + Sorting: SortOrder["+sortOrder.toString()+"], SortField["+sortField+"]");

			IExpression expr = this.transazioniSearchDAO.newExpression();

			parseExpressionFilter(expr, true, false);
			// ordinamento di default
			setOrderField(expr, sortOrder,sortField,true);

			List<Index> forceIndexCount = this.getIndexCount();
			if(forceIndexCount!=null && forceIndexCount.size()>0){
				for (Index index : forceIndexCount) {
					expr.addForceIndex(index);	
				}
			}
			
			NonNegativeNumber res = this.transazioniSearchDAO.count(expr);
			return (int) res.longValue();

		} catch (Exception e) {
			this.log.error(e.getMessage(), e);
		}
		return 0;
	}

	@Override
	public int totalCount() {
		try {

			this.log.debug("Count");

			IExpression expr = this.transazioniSearchDAO.newExpression();

			parseExpressionFilter(expr, true, false);
			SortOrder sortOrder = this.searchForm.getSortOrder();
			String sortField = this.searchForm.getSortField();
			setOrderField(expr, sortOrder,sortField,true);

			List<Index> forceIndexCount = this.getIndexCount();
			if(forceIndexCount!=null && forceIndexCount.size()>0){
				for (Index index : forceIndexCount) {
					expr.addForceIndex(index);	
				}
			}
			
			NonNegativeNumber res = this.transazioniSearchDAO.count(expr);
			return (int) res.longValue();

		} catch (Exception e) {
			this.log.error(e.getMessage(), e);
		}
		return 0;
	}

	@Override
	public void delete(TransazioneBean obj) throws Exception {

	}

	@Override
	public void deleteById(String key) {

	}

	@Override
	public List<TransazioneBean> findAll() {
		List<TransazioneBean> listaBean = new ArrayList<TransazioneBean>();
		try {
			this.log.debug("Find All");
			IExpression expr = this.transazioniSearchDAO.newExpression();

			parseExpressionFilter(expr, false, false);

			SortOrder sortOrder = this.searchForm.getSortOrder();
			String sortField = this.searchForm.getSortField();
			setOrderField(expr, sortOrder,sortField,true);
			IPaginatedExpression pagExpr = this.transazioniSearchDAO.toPaginatedExpression(expr);

			List<Index> forceIndexFindAll = this.getIndexFindAll();
			if(forceIndexFindAll!=null && forceIndexFindAll.size()>0){
				for (Index index : forceIndexFindAll) {
					pagExpr.addForceIndex(index);	
				}
			}
			
			TransazioniIndexUtils.enableSoloColonneIndicizzateFullIndexSearch(pagExpr);
			
			List<Transazione> list = this.transazioniSearchDAO.findAll(pagExpr);
			if(list!= null && list.size() > 0)
				for (Transazione transazione : list) {
					TransazioneBean bean = new TransazioneBean(transazione, this.searchForm!=null ? this.searchForm.getSoggettoPddMonitor() : null);
					
					// Integrazione dei dati delle credenziali
					bean.normalizeRichiedenteInfo(transazione, bean, this);
					
					this.normalizeInfoTransazioniFromCredenzialiMittenteUriApi(bean, transazione);
					bean.normalizeTipoApiInfo(this.utilsServiceManager, this.log);
					bean.normalizeOperazioneInfo(this.utilsServiceManager, this.log);
					
					this.normalizeInfoTransazioniFromCredenzialiMittenteGruppi(bean, transazione);
					
					boolean normalizeHttpReturnCode = true;
					this.normalizeInfoTransazioniFromCredenzialiMittenteEventi(bean, transazione, normalizeHttpReturnCode);
					
					listaBean.add(bean);
				}
		} catch (Exception e) {
			this.log.error(e.getMessage(), e);
		}
		return listaBean;
	}

	@Override
	public TransazioneBean findById(String key) {
		this.log.debug("Find by id: " + key);
		try {
			throw new NotImplementedException("Metodo Eliminato");

		} catch (Exception e) {
			this.log.error(e.getMessage(), e);
		}
		return null;

	}

	@Override
	public void store(TransazioneBean obj) throws Exception {

	}

	@Override
	public List<TransazioneBean> findAllLive() {

		this.log.debug("Find All Live");
		List<TransazioneBean> listaBean = new ArrayList<TransazioneBean>();
		try {
			IExpression expr = this.transazioniSearchDAO.newExpression();

			parseExpressionFilter(expr, false, true);

			// ordinamento di default
			setOrderField(expr, SortOrder.DESC, null,false);

			IPaginatedExpression pagExpr = this.transazioniSearchDAO.toPaginatedExpression(expr);

			pagExpr.limit(this.liveMaxResults);

			// Devo rileggere il valore ogni volta, il service del live viene istanziato solamente una volta
			PddMonitorProperties govwayMonitorProperties = PddMonitorProperties.getInstance(this.log);
			Properties repositoryExternal = govwayMonitorProperties.getExternalForceIndexRepository();
			List<Index> forceIndexFindAll = convertForceIndexList(govwayMonitorProperties.getTransazioniForceIndexLiveFindAll(repositoryExternal));
			if(forceIndexFindAll!=null && forceIndexFindAll.size()>0){
				for (Index index : forceIndexFindAll) {
					pagExpr.addForceIndex(index);	
				}
			}
			
			TransazioniIndexUtils.enableSoloColonneIndicizzateFullIndexSearch(pagExpr);
			
			List<Transazione> list = this.transazioniSearchDAO.findAll(pagExpr);

			if(list!= null && list.size() > 0)
				for (Transazione transazione : list) {
					TransazioneBean bean = new TransazioneBean(transazione, this.searchForm!=null ? this.searchForm.getSoggettoPddMonitor() : null);
					
					// Integrazione dei dati delle credenziali
					bean.normalizeRichiedenteInfo(transazione, bean, this);
					
					this.normalizeInfoTransazioniFromCredenzialiMittenteUriApi(bean, transazione);
					bean.normalizeTipoApiInfo(this.utilsServiceManager, this.log);
					bean.normalizeOperazioneInfo(this.utilsServiceManager, this.log);
					
					this.normalizeInfoTransazioniFromCredenzialiMittenteGruppi(bean, transazione);
					
					boolean normalizeHttpReturnCode = true;
					this.normalizeInfoTransazioniFromCredenzialiMittenteEventi(bean, transazione, normalizeHttpReturnCode);
					
					listaBean.add(bean);
				}

		} catch (Exception e) {
			this.log.error(e.getMessage(), e);
		}

		return listaBean;
	}

	@Override
	public ResLive getEsitiInfoLive(PermessiUtenteOperatore permessiUtente, Date lastDatePick, String protocolloSelected, String protocolloDefault) {

		this.log.debug("Get Esiti Info Live[idPorta: " + permessiUtente+ "], [ LastDatePick: " + lastDatePick + "]");

		try {
			
			String protocolloP = protocolloDefault;
			if(protocolloSelected!=null) {
				protocolloP = protocolloSelected;
			}
			
			EsitiProperties esitiProperties = EsitiProperties.getInstance(this.log, protocolloP);
			List<Integer> esitiOk = esitiProperties.getEsitiCodeOk_senzaFaultApplicativo();
			List<Integer> esitiKo = esitiProperties.getEsitiCodeKo_senzaFaultApplicativo();
			List<Integer> esitiFault = esitiProperties.getEsitiCodeFaultApplicativo();
			
			// OK
			IExpression exprOk = this.transazioniSearchDAO.newExpression();
			exprOk.greaterThan(Transazione.model().DATA_INGRESSO_RICHIESTA,	lastDatePick);
			exprOk.and().in(Transazione.model().ESITO, esitiOk);
			if (permessiUtente != null) {
				IExpression permessi = permessiUtente.toExpression(this.transazioniSearchDAO, Transazione.model().PDD_CODICE, 
						Transazione.model().TIPO_SOGGETTO_EROGATORE, Transazione.model().NOME_SOGGETTO_EROGATORE, 
						Transazione.model().TIPO_SERVIZIO, Transazione.model().NOME_SERVIZIO, Transazione.model().VERSIONE_SERVIZIO);
				exprOk.and(permessi);
			}
			
			// Fault
			IExpression exprFaultApplicativo = this.transazioniSearchDAO.newExpression();
			exprFaultApplicativo.greaterThan(Transazione.model().DATA_INGRESSO_RICHIESTA,	lastDatePick);
			exprFaultApplicativo.and().in(Transazione.model().ESITO, esitiFault);
			if (permessiUtente != null) {
				IExpression permessi = permessiUtente.toExpression(this.transazioniSearchDAO, Transazione.model().PDD_CODICE, 
						Transazione.model().TIPO_SOGGETTO_EROGATORE, Transazione.model().NOME_SOGGETTO_EROGATORE, 
						Transazione.model().TIPO_SERVIZIO, Transazione.model().NOME_SERVIZIO, Transazione.model().VERSIONE_SERVIZIO);
				exprFaultApplicativo.and(permessi);
			}

			// KO
			IExpression exprKo = this.transazioniSearchDAO.newExpression();
			exprKo.greaterThan(Transazione.model().DATA_INGRESSO_RICHIESTA,	lastDatePick);
			exprKo.and().in(Transazione.model().ESITO, esitiKo);		
			if (permessiUtente != null) {
				IExpression permessi = permessiUtente.toExpression(this.transazioniSearchDAO, Transazione.model().PDD_CODICE, 
						Transazione.model().TIPO_SOGGETTO_EROGATORE, Transazione.model().NOME_SOGGETTO_EROGATORE, 
						Transazione.model().TIPO_SERVIZIO, Transazione.model().NOME_SERVIZIO, Transazione.model().VERSIONE_SERVIZIO);
				exprKo.and(permessi);
			}

			// Devo rileggere il valore ogni volta, il service del live viene istanziato solamente una volta
			PddMonitorProperties govwayMonitorProperties = PddMonitorProperties.getInstance(this.log);
			Properties repositoryExternal = govwayMonitorProperties.getExternalForceIndexRepository();
			List<Index> forceIndexEsitiCount = convertForceIndexList(govwayMonitorProperties.getTransazioniForceIndexEsitiCount(repositoryExternal));
			if(forceIndexEsitiCount!=null && forceIndexEsitiCount.size()>0){
				for (Index index : forceIndexEsitiCount) {
					exprOk.addForceIndex(index);
					exprFaultApplicativo.addForceIndex(index);
					exprKo.addForceIndex(index);
				}
			}
			
			String modalita = Utility.getLoggedUtenteModalita();
			if(StringUtils.isNotEmpty(modalita) && !modalita.equals(org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_PARAMETRO_MODALITA_ALL)) {
				exprOk.and().equals(Transazione.model().PROTOCOLLO,	modalita);
				exprFaultApplicativo.and().equals(Transazione.model().PROTOCOLLO,	modalita);
				exprKo.and().equals(Transazione.model().PROTOCOLLO,	modalita);
			}
			
			NonNegativeNumber nnnOk = this.transazioniSearchDAO.count(exprOk);
			NonNegativeNumber nnnFaultApplicativo = this.transazioniSearchDAO.count(exprFaultApplicativo);
			NonNegativeNumber nnnKo = this.transazioniSearchDAO.count(exprKo);

			if (nnnKo != null && nnnOk != null) {
				return new ResLive(nnnOk.longValue(), nnnFaultApplicativo.longValue(), nnnKo.longValue(), new Date());
			}

		} catch (ServiceException e) {
			this.log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			this.log.error(e.getMessage(), e);
		} catch (ExpressionNotImplementedException e) {
			this.log.error(e.getMessage(), e);
		} catch (ExpressionException e) {
			this.log.error(e.getMessage(), e);
		} catch (Exception e) {
			this.log.error(e.getMessage(), e);
		}

		return new ResLive(Long.valueOf("0"), Long.valueOf("0"), Long.valueOf("0"));
	}

	@Override
	public TransazioneBean findByIdTransazione(String idTransazione)
			throws Exception {
		try {

			this.log.debug("Find By Id Transazione: "+ idTransazione);

			IExpression expr = this.transazioniSearchDAO.newExpression();
			expr.equals(Transazione.model().ID_TRANSAZIONE, idTransazione);

			if(this.forceIndexGetByIdTransazione!=null && this.forceIndexGetByIdTransazione.size()>0){
				for (Index index : this.forceIndexGetByIdTransazione) {
					expr.addForceIndex(index);	
				}
			}
			
			Transazione t = this.transazioniSearchDAO.find(expr);
			TransazioneBean transazioneBean = new TransazioneBean(t, this.searchForm!=null ? this.searchForm.getSoggettoPddMonitor(false) : null);
			
			// Integrazione dei dati delle credenziali
			this.normalizeInfoTransazioniFromCredenzialiMittente(transazioneBean, t);
			
			this.normalizeInfoTransazioniFromCredenzialiMittenteUriApi(transazioneBean, t);
			transazioneBean.normalizeTipoApiInfo(this.utilsServiceManager, this.log);
			transazioneBean.normalizeOperazioneInfo(this.utilsServiceManager, this.log);
						
			return transazioneBean; 
		} catch (NotFoundException nre) {
			// ignore
		} catch (Exception e) {
			this.log.error("Impossibile recuperare La Transazione con idTransazione: "	+ idTransazione, e);
			throw new Exception("Impossibile recuperare La Transazione con idTransazione: "	+ idTransazione, e);
		}
		return null;
	}
	
	public void normalizeInfoTransazioniFromCredenzialiMittente(TransazioneBean transazioneBean, Transazione t) throws ServiceException, MultipleResultException, NotImplementedException {
		// Integrazione dei dati delle credenziali
		
		normalizeInfoTransazioniFromCredenzialiMittenteTrasporto(transazioneBean, t);
		
		normalizeInfoTransazioniFromCredenzialiMittenteToken(transazioneBean, t);
		
		boolean normalizeHttpReturnCode = true;
		normalizeInfoTransazioniFromCredenzialiMittenteEventi(transazioneBean, t, normalizeHttpReturnCode);
		
		normalizeInfoTransazioniFromCredenzialiMittenteGruppi(transazioneBean, t);
	}
	
	public void normalizeInfoTransazioniFromCredenzialiMittenteClientAddress(TransazioneBean transazioneBean, Transazione t) throws ServiceException, MultipleResultException, NotImplementedException {
		
		// Trasporto Mittente
		String clientAddress = t.getClientAddress();
		if(StringUtils.isNotEmpty(clientAddress)) {
			try {
				MBeanUtilsService mBeanUtilsService = new MBeanUtilsService(this.credenzialiMittenteDAO, this.log);
				CredenzialeMittente credenzialeMittente = mBeanUtilsService.getCredenzialeMittenteFromCache(Long.parseLong(clientAddress));
				String credenziale = credenzialeMittente.getCredenziale();
				String socket = CredenzialeClientAddress.convertSocketDBValueToOriginal(credenziale);
				String transport = CredenzialeClientAddress.convertTransportDBValueToOriginal(credenziale);
				transazioneBean.setSocketClientAddressLabel(socket);
				transazioneBean.setTransportClientAddressLabel(transport);
			} catch(NumberFormatException e) {
				// informazione non valida
				transazioneBean.setTrasportoMittenteLabel(Costanti.LABEL_INFORMAZIONE_NON_DISPONIBILE); 
			} catch(NotFoundException e) {
				// informazione non piu disponibile
				transazioneBean.setTrasportoMittenteLabel(Costanti.LABEL_INFORMAZIONE_NON_PIU_PRESENTE); 
			}
		}
		
	}
	
	public void normalizeInfoTransazioniFromCredenzialiMittenteTrasporto(TransazioneBean transazioneBean, Transazione t) throws ServiceException, MultipleResultException, NotImplementedException {
			
		// Trasporto Mittente
		String trasportoMittente = t.getTrasportoMittente();
		if(StringUtils.isNotEmpty(trasportoMittente)) {
			try {
				MBeanUtilsService mBeanUtilsService = new MBeanUtilsService(this.credenzialiMittenteDAO, this.log);
				CredenzialeMittente credenzialeMittente = mBeanUtilsService.getCredenzialeMittenteFromCache(Long.parseLong(trasportoMittente));
				transazioneBean.setTrasportoMittenteLabel(credenzialeMittente.getCredenziale()); 
				transazioneBean.setTipoTrasportoMittenteLabel(credenzialeMittente.getTipo());
			} catch(NumberFormatException e) {
				// informazione non valida
				transazioneBean.setTrasportoMittenteLabel(Costanti.LABEL_INFORMAZIONE_NON_DISPONIBILE); 
			} catch(NotFoundException e) {
				// informazione non piu disponibile
				transazioneBean.setTrasportoMittenteLabel(Costanti.LABEL_INFORMAZIONE_NON_PIU_PRESENTE); 
			}
		}
		
	}
	
	public void normalizeInfoTransazioniFromCredenzialiMittenteToken(TransazioneBean transazioneBean, Transazione t) throws ServiceException, MultipleResultException, NotImplementedException {
	
		normalizeInfoTransazioniFromCredenzialiMittenteTokenIssuer(transazioneBean, t);
		
		normalizeInfoTransazioniFromCredenzialiMittenteTokenClientID(transazioneBean, t);
		
		normalizeInfoTransazioniFromCredenzialiMittenteTokenSubject(transazioneBean, t);
	
		normalizeInfoTransazioniFromCredenzialiMittenteTokenUsername(transazioneBean, t);
		
		normalizeInfoTransazioniFromCredenzialiMittenteTokenMail(transazioneBean, t);
		
	}
	
	public void normalizeInfoTransazioniFromCredenzialiMittenteTokenIssuer(TransazioneBean transazioneBean, Transazione t) throws ServiceException, MultipleResultException, NotImplementedException {
		
		// Token Issuer
		String tokenIssuer = t.getTokenIssuer();
		if(StringUtils.isNotEmpty(tokenIssuer)) {
			try {
				MBeanUtilsService mBeanUtilsService = new MBeanUtilsService(this.credenzialiMittenteDAO, this.log);
				CredenzialeMittente credenzialeMittente = mBeanUtilsService.getCredenzialeMittenteFromCache(Long.parseLong(tokenIssuer));
				transazioneBean.setTokenIssuerLabel(credenzialeMittente.getCredenziale()); 
			} catch(NumberFormatException e) {
				// informazione non valida
				transazioneBean.setTokenIssuerLabel(Costanti.LABEL_INFORMAZIONE_NON_DISPONIBILE); 
			} catch(NotFoundException e) {
				// informazione non piu disponibile
				transazioneBean.setTokenIssuerLabel(Costanti.LABEL_INFORMAZIONE_NON_PIU_PRESENTE); 
			}
		}
		
	}
	
	public void normalizeInfoTransazioniFromCredenzialiMittenteTokenClientID(TransazioneBean transazioneBean, Transazione t) throws ServiceException, MultipleResultException, NotImplementedException {
				
		// Token Client ID
		String tokenClientID = t.getTokenClientId();
		if(StringUtils.isNotEmpty(tokenClientID)) {
			try {
				MBeanUtilsService mBeanUtilsService = new MBeanUtilsService(this.credenzialiMittenteDAO, this.log);
				CredenzialeMittente credenzialeMittente = mBeanUtilsService.getCredenzialeMittenteFromCache(Long.parseLong(tokenClientID));
				transazioneBean.setTokenClientIdLabel(credenzialeMittente.getCredenziale()); 
			} catch(NumberFormatException e) {
				// informazione non valida
				transazioneBean.setTokenClientIdLabel(Costanti.LABEL_INFORMAZIONE_NON_DISPONIBILE); 
			} catch(NotFoundException e) {
				// informazione non piu disponibile
				transazioneBean.setTokenClientIdLabel(Costanti.LABEL_INFORMAZIONE_NON_PIU_PRESENTE); 
			}
		}
		
	}
	
	public void normalizeInfoTransazioniFromCredenzialiMittenteTokenSubject(TransazioneBean transazioneBean, Transazione t) throws ServiceException, MultipleResultException, NotImplementedException {
				
		// Token Subject
		String tokenSubject = t.getTokenSubject();
		if(StringUtils.isNotEmpty(tokenSubject)) {
			try {
				MBeanUtilsService mBeanUtilsService = new MBeanUtilsService(this.credenzialiMittenteDAO, this.log);
				CredenzialeMittente credenzialeMittente = mBeanUtilsService.getCredenzialeMittenteFromCache(Long.parseLong(tokenSubject));
				transazioneBean.setTokenSubjectLabel(credenzialeMittente.getCredenziale()); 
			} catch(NumberFormatException e) {
				// informazione non valida
				transazioneBean.setTokenSubjectLabel(Costanti.LABEL_INFORMAZIONE_NON_DISPONIBILE); 
			} catch(NotFoundException e) {
				// informazione non piu disponibile
				transazioneBean.setTokenSubjectLabel(Costanti.LABEL_INFORMAZIONE_NON_PIU_PRESENTE); 
			}
		}
		
	}
	
	public void normalizeInfoTransazioniFromCredenzialiMittenteTokenUsername(TransazioneBean transazioneBean, Transazione t) throws ServiceException, MultipleResultException, NotImplementedException {
				
		// Token Username
		String tokenUsername = t.getTokenUsername();
		if(StringUtils.isNotEmpty(tokenUsername)) {
			try {
				MBeanUtilsService mBeanUtilsService = new MBeanUtilsService(this.credenzialiMittenteDAO, this.log);
				CredenzialeMittente credenzialeMittente = mBeanUtilsService.getCredenzialeMittenteFromCache(Long.parseLong(tokenUsername));
				transazioneBean.setTokenUsernameLabel(credenzialeMittente.getCredenziale()); 
			} catch(NumberFormatException e) {
				// informazione non valida
				transazioneBean.setTokenUsernameLabel(Costanti.LABEL_INFORMAZIONE_NON_DISPONIBILE); 
			} catch(NotFoundException e) {
				// informazione non piu disponibile
				transazioneBean.setTokenUsernameLabel(Costanti.LABEL_INFORMAZIONE_NON_PIU_PRESENTE); 
			}
		}
		
	}
	
	public void normalizeInfoTransazioniFromCredenzialiMittenteTokenMail(TransazioneBean transazioneBean, Transazione t) throws ServiceException, MultipleResultException, NotImplementedException {
				
		// Token Mail
		String tokenMail = t.getTokenMail();
		if(StringUtils.isNotEmpty(tokenMail)) {
			try {
				MBeanUtilsService mBeanUtilsService = new MBeanUtilsService(this.credenzialiMittenteDAO, this.log);
				CredenzialeMittente credenzialeMittente = mBeanUtilsService.getCredenzialeMittenteFromCache(Long.parseLong(tokenMail));
				transazioneBean.setTokenMailLabel(credenzialeMittente.getCredenziale()); 
			} catch(NumberFormatException e) {
				// informazione non valida
				transazioneBean.setTokenMailLabel(Costanti.LABEL_INFORMAZIONE_NON_DISPONIBILE); 
			} catch(NotFoundException e) {
				// informazione non piu disponibile
				transazioneBean.setTokenMailLabel(Costanti.LABEL_INFORMAZIONE_NON_PIU_PRESENTE); 
			}
		}
		
	}
	
	public void normalizeInfoTransazioniFromCredenzialiMittenteEventi(TransazioneBean transazioneBean, Transazione t, boolean normalizeHttpReturnCode) throws ServiceException, MultipleResultException, NotImplementedException {
				
		// Eventi
		String eventi = t instanceof TransazioneBean ? ((TransazioneBean)t).getEventiGestioneRawValue() : t.getEventiGestione();
		if(StringUtils.isNotEmpty(eventi)) {
			try {
				MBeanUtilsService mBeanUtilsService = new MBeanUtilsService(this.credenzialiMittenteDAO, this.log);
				CredenzialeMittente credenzialeMittente = mBeanUtilsService.getCredenzialeMittenteFromCache(Long.parseLong(eventi));
				String valore = credenzialeMittente.getCredenziale();
				transazioneBean.setEventiLabel(AbstractCredenzialeList.normalize(valore));
				
				if(normalizeHttpReturnCode) {
					if(transazioneBean.getCodiceRispostaIngresso()==null || "".equals(transazioneBean.getCodiceRispostaIngresso())) {
						Integer code = transazioneBean.getInResponseCodeFromEventiGestione();
						if(code!=null) {
							transazioneBean.setCodiceRispostaIngresso(code.intValue()+"");
						}
					}
					if(transazioneBean.getCodiceRispostaUscita()==null || "".equals(transazioneBean.getCodiceRispostaUscita())) {
						Integer code = transazioneBean.getOutResponseCodeFromEventiGestione();
						if(code!=null) {
							transazioneBean.setCodiceRispostaUscita(code.intValue()+"");
						}
					}
					if(transazioneBean.getTipoApi()<=0) {
						Integer code = transazioneBean.getTipoApiFromEventiGestione();
						if(code!=null) {
							transazioneBean.setTipoApi(code.intValue());
						}
					}
				}
				
			} catch(NumberFormatException e) {
				// informazione non valida
				transazioneBean.setEventiLabel(Costanti.LABEL_INFORMAZIONE_NON_DISPONIBILE); 
			} catch(NotFoundException e) {
				// informazione non piu disponibile
				transazioneBean.setEventiLabel(Costanti.LABEL_INFORMAZIONE_NON_PIU_PRESENTE); 
			}
		}
		
	}
	
	public void normalizeInfoTransazioniFromCredenzialiMittenteGruppi(TransazioneBean transazioneBean, Transazione t) throws ServiceException, MultipleResultException, NotImplementedException {
				
		// Gruppi
		String gruppi = t instanceof TransazioneBean ? ((TransazioneBean)t).getGruppiRawValue() : t.getGruppi();
		if(StringUtils.isNotEmpty(gruppi)) {
			try {
				MBeanUtilsService mBeanUtilsService = new MBeanUtilsService(this.credenzialiMittenteDAO, this.log);
				CredenzialeMittente credenzialeMittente = mBeanUtilsService.getCredenzialeMittenteFromCache(Long.parseLong(gruppi));
				String valore = credenzialeMittente.getCredenziale();
				transazioneBean.setGruppiLabel(AbstractCredenzialeList.normalize(valore)); 
			} catch(NumberFormatException e) {
				// informazione non valida
				transazioneBean.setGruppiLabel(Costanti.LABEL_INFORMAZIONE_NON_DISPONIBILE); 
			} catch(NotFoundException e) {
				// informazione non piu disponibile
				transazioneBean.setGruppiLabel(Costanti.LABEL_INFORMAZIONE_NON_PIU_PRESENTE); 
			}
		}
	}
	
	public void normalizeInfoTransazioniFromCredenzialiMittenteUriApi(TransazioneBean transazioneBean, Transazione t) throws ServiceException, MultipleResultException, NotImplementedException {
		
		if(transazioneBean.getUriAccordoServizio()==null || "".equals(transazioneBean.getUriAccordoServizio())) {
			// UriApi
			String uriApi = t instanceof TransazioneBean ? ((TransazioneBean)t).getUriApi() : t.getUriApi();
			if(StringUtils.isNotEmpty(uriApi)) {
				try {
					MBeanUtilsService mBeanUtilsService = new MBeanUtilsService(this.credenzialiMittenteDAO, this.log);
					CredenzialeMittente credenzialeMittente = mBeanUtilsService.getCredenzialeMittenteFromCache(Long.parseLong(uriApi));
					String valore = credenzialeMittente.getCredenziale();
					transazioneBean.setUriAccordoServizio(valore);				
				} catch(NumberFormatException e) {
					// informazione non valida
					transazioneBean.setUriAccordoServizio(Costanti.LABEL_INFORMAZIONE_NON_DISPONIBILE); 
				} catch(NotFoundException e) {
					// informazione non piu disponibile
					transazioneBean.setUriAccordoServizio(Costanti.LABEL_INFORMAZIONE_NON_PIU_PRESENTE); 
				} catch(Exception e) {
					this.log.error("Parsing uri api failed: "+e.getMessage(),e);
					// informazione non valida ?
					transazioneBean.setUriAccordoServizio(Costanti.LABEL_INFORMAZIONE_NON_DISPONIBILE); 
				}
			}
		}
	}

	private static long virtualIdRequest = -999l;
	private static long virtualIdResponse = -888l;
	private DumpMessaggio createVirtualMessageWithSdk(String idTransazione, String saErogatore, Date dataConsegnaErogatore, TipoMessaggio tipoMessaggio) throws Exception{
		DumpMessaggio msg = new DumpMessaggio();
		msg.setTipoMessaggio(tipoMessaggio);
		msg.setIdTransazione(idTransazione);
		msg.setServizioApplicativoErogatore(saErogatore);
		msg.setDataConsegnaErogatore(dataConsegnaErogatore);
		if(TipoMessaggio.RICHIESTA_INGRESSO.equals(tipoMessaggio) || TipoMessaggio.RICHIESTA_USCITA.equals(tipoMessaggio)){
			msg.setId(TransazioniService.virtualIdRequest); // id virtuale
		}
		else{
			msg.setId(TransazioniService.virtualIdResponse); // id virtuale
		}
		this.updateMessageWithSdk(msg);
		if(msg.getBody() != null || msg.sizeAllegatoList() > 0 || msg.sizeContenutoList() > 0 || msg.sizeHeaderTrasportoList() > 0){
			return msg;
		}
		return null;
	}
	
	@SuppressWarnings("deprecation")
	private void updateMessageWithSdk(DumpMessaggio mes) throws Exception{
		if(this.transactionServiceLibraryReader!=null && mes!=null){
			
			IDServizio idServizio = null;
			String statoTransazione = null;
			try{
				IPaginatedExpression expr = this.transazioniSearchDAO.newPaginatedExpression();
				expr.equals(Transazione.model().ID_TRANSAZIONE, mes.getIdTransazione());
				if(this.forceIndexGetByIdTransazione!=null && this.forceIndexGetByIdTransazione.size()>0){
					for (Index index : this.forceIndexGetByIdTransazione) {
						expr.addForceIndex(index);	
					}
				}
				List<Map<String, Object>> list = this.transazioniSearchDAO.select(expr, Transazione.model().TIPO_SOGGETTO_EROGATORE,Transazione.model().NOME_SOGGETTO_EROGATORE,
					Transazione.model().TIPO_SERVIZIO,Transazione.model().NOME_SERVIZIO,Transazione.model().VERSIONE_SERVIZIO,Transazione.model().AZIONE,Transazione.model().STATO);
				if(list!=null && list.size()>0){
					Map<String, Object> map = list.get(0);
					String tipoSoggettoErogatore = this.readObjectInMap(Transazione.model().TIPO_SOGGETTO_EROGATORE, map);
					String nomeSoggettoErogatore = this.readObjectInMap(Transazione.model().NOME_SOGGETTO_EROGATORE, map);
					String tipoServizio = this.readObjectInMap(Transazione.model().TIPO_SERVIZIO, map);
					String nomeServizio = this.readObjectInMap(Transazione.model().NOME_SERVIZIO, map);
					String azione = this.readObjectInMap(Transazione.model().AZIONE, map);
					statoTransazione = this.readObjectInMap(Transazione.model().STATO, map);
					String v = this.readObjectInMap(Transazione.model().VERSIONE_SERVIZIO, map);
					if(tipoSoggettoErogatore!=null && nomeSoggettoErogatore!=null && tipoServizio!=null && nomeServizio!=null){
						idServizio = new IDServizio();
						idServizio.setTipo(tipoServizio);
						idServizio.setNome(nomeServizio);
						idServizio.setSoggettoErogatore(new IDSoggetto(tipoSoggettoErogatore, nomeSoggettoErogatore));
						idServizio.setAzione(azione);
						idServizio.setVersione(Integer.parseInt(v)); 
					}
				}
			} catch (NotFoundException e) {
				this.log.debug("Non è stata trovata la transazione con id ["+mes.getIdTransazione()+"]");
			}
			
			if(idServizio!=null){
				BasicServiceLibrary basicServiceLibrary = this.basicServiceLibraryReader.read(idServizio, this.log);
				if(basicServiceLibrary!=null){
					TransactionServiceLibrary transactionServiceLibrary = this.transactionServiceLibraryReader.readConfigurazioneTransazione(basicServiceLibrary, this.log);
					if(transactionServiceLibrary!=null){
						//Transazione trReadFromDB = this.transazioniSearchDAO.get(mes.getIdTransazione());
						// Uso find per poter forzare l'indice
						IExpression expr = this.transazioniSearchDAO.newExpression();
						expr.equals(Transazione.model().ID_TRANSAZIONE, mes.getIdTransazione());
						if(this.forceIndexGetByIdTransazione!=null && this.forceIndexGetByIdTransazione.size()>0){
							for (Index index : this.forceIndexGetByIdTransazione) {
								expr.addForceIndex(index);	
							}
						}
						Transazione trReadFromDB = this.transazioniSearchDAO.find(expr);
						transactionServiceLibrary.processResourcesAfterReadFromDatabase(trReadFromDB, mes, statoTransazione,
								this.log,this.daoFactory);
					}
				}
			}
		}
	}
	
	@Override
	public int countDumpMessaggiGByDataConsegnaErogatore(String idTransazione, String saErogatore){
		try {
			this.log.debug("Count numero consegne [id transazione: " + idTransazione + "],[SA Erogatore: " + saErogatore + "]");

			IExpression expr = this.dumpMessaggioSearchDAO.newExpression();
			expr.and().equals(DumpMessaggio.model().ID_TRANSAZIONE, idTransazione);
			
			if(saErogatore == null) {
				expr.isNull(DumpMessaggio.model().SERVIZIO_APPLICATIVO_EROGATORE);
			} else {
				expr.equals(DumpMessaggio.model().SERVIZIO_APPLICATIVO_EROGATORE, saErogatore);
			}
			
			expr.addGroupBy(DumpMessaggio.model().DATA_CONSEGNA_EROGATORE);

			NonNegativeNumber nnn = this.dumpMessaggioSearchDAO.count(expr);
			if(nnn != null)
			return (int) nnn.longValue();

		} catch (Exception e) {
			this.log.error("Impossibile Count numero consegne con idTransazione: "+ idTransazione + "e SA Erogatore: " + saErogatore , e);
//			throw new Exception("Impossibile Count numero consegne con idTransazione: "	+ idTransazione + "e SA Erogatore: " + saErogatore, e);
		}
		
		return 0;
	}
	
	@Override
	public List<DumpMessaggioBean> listDumpMessaggiGByDataConsegnaErogatore(String idTransazione, String saErogatore, int start, int limit) {
		List<DumpMessaggioBean> lista = new ArrayList<DumpMessaggioBean>();
		try {
			this.log.debug("Find All + Limit numero consegne [id transazione: " + idTransazione + "],[SA Erogatore: " + saErogatore + "]");

			IExpression expr = this.dumpMessaggioSearchDAO.newExpression();
			expr.and().equals(DumpMessaggio.model().ID_TRANSAZIONE, idTransazione);
			
			if(saErogatore == null) {
				expr.isNull(DumpMessaggio.model().SERVIZIO_APPLICATIVO_EROGATORE);
			} else {
				expr.equals(DumpMessaggio.model().SERVIZIO_APPLICATIVO_EROGATORE, saErogatore);
			}
			
			expr.addGroupBy(DumpMessaggio.model().DATA_CONSEGNA_EROGATORE);
			
			IPaginatedExpression pagExpr = this.dumpMessaggioSearchDAO
					.toPaginatedExpression(expr);

			pagExpr.offset(start).limit(limit);
			
			pagExpr.addOrder(DumpMessaggio.model().DATA_CONSEGNA_EROGATORE, SortOrder.DESC);

			FunctionField fCount = new FunctionField(DumpMessaggio.model().DATA_CONSEGNA_EROGATORE, Function.COUNT, "numeroConsegnePerData");
			List<Map<String,Object>> groupBy = this.dumpMessaggioSearchDAO.groupBy(pagExpr, fCount);
			
			this.log.debug("Trovate ["+groupBy.size()+"] consegne [id transazione: " + idTransazione + "],[SA Erogatore: " + saErogatore + "]");
			for (Map<String, Object> row : groupBy) {
				Date data = (Date) row.get(JDBCUtilities.getAlias(DumpMessaggio.model().DATA_CONSEGNA_EROGATORE));
				
				DumpMessaggioBean bean = new DumpMessaggioBean();
				bean.setDataConsegnaErogatore(data);
				bean.setIdTransazione(idTransazione);
				bean.setServizioApplicativoErogatore(saErogatore);
				
				lista.add(bean);
			}
			
			for (DumpMessaggioBean dumpMessaggio : lista) {
				expr = this.dumpMessaggioSearchDAO.newExpression();
				expr.and().equals(DumpMessaggio.model().ID_TRANSAZIONE, dumpMessaggio.getIdTransazione());
				if(dumpMessaggio.getServizioApplicativoErogatore() == null) {
					expr.isNull(DumpMessaggio.model().SERVIZIO_APPLICATIVO_EROGATORE);
				} else {
					expr.equals(DumpMessaggio.model().SERVIZIO_APPLICATIVO_EROGATORE, dumpMessaggio.getServizioApplicativoErogatore());
				}
				
				expr.equals(DumpMessaggio.model().DATA_CONSEGNA_EROGATORE, dumpMessaggio.getDataConsegnaErogatore());
				
				pagExpr = this.dumpMessaggioSearchDAO.toPaginatedExpression(expr);
				
				pagExpr.addOrder(DumpMessaggio.model().TIPO_MESSAGGIO, SortOrder.ASC);
				
				List<Object> select = this.dumpMessaggioSearchDAO.select(pagExpr, DumpMessaggio.model().TIPO_MESSAGGIO);
				
				for (Object object : select) {
					dumpMessaggio.getTipiMessaggio().add(TipoMessaggio.toEnumConstant((String) object));
				}
				this.log.debug("Trovati ["+dumpMessaggio.getTipiMessaggio().size()+"] Tipi Dump ["+dumpMessaggio.getTipiMessaggio()+"] per la consegna [id transazione: " + idTransazione + "],[SA Erogatore: " + saErogatore + "],[ DATA_CONSEGNA_EROGATORE: "	+ dumpMessaggio.getDataConsegnaErogatore() + "] ");
				
			}
		} catch (Exception e) {
			this.log.error(e.getMessage(), e);
		}		
		return lista;
	}
	
	@Override
	public DumpMessaggio getDumpMessaggio(String idTransazione, String saErogatore, Date dataConsegnaErogatore, TipoMessaggio tipoMessaggio) throws Exception {

		try {
			this.log.debug("Get Dump Messaggio [id transazione: " + idTransazione + "],[SA Erogatore: " + saErogatore + "],[ tipomessaggio: "	+ tipoMessaggio.toString() + "]");

			IExpression expr = this.dumpMessaggioSearchDAO.newExpression();
			expr.equals(DumpMessaggio.model().TIPO_MESSAGGIO, TipoMessaggio.toEnumConstant(tipoMessaggio.toString()));
			expr.and().equals(DumpMessaggio.model().ID_TRANSAZIONE, idTransazione);
			
			if(saErogatore == null) {
				expr.isNull(DumpMessaggio.model().SERVIZIO_APPLICATIVO_EROGATORE);
			} else {
				expr.equals(DumpMessaggio.model().SERVIZIO_APPLICATIVO_EROGATORE, saErogatore);
			}
			
			if(dataConsegnaErogatore != null) {
				expr.equals(DumpMessaggio.model().DATA_CONSEGNA_EROGATORE, dataConsegnaErogatore);
			}
			
			// piu' recenti in cima?
			expr.addOrder(DumpMessaggio.model().DUMP_TIMESTAMP, SortOrder.DESC);

			DumpMessaggio mes = this.dumpMessaggioSearchDAO.find(expr);
			this.updateMessageWithSdk(mes);
			return mes;

		} catch (NotFoundException nre) {
			
			// provo a vedere se esiste virtualmente grazie all'SDK
			DumpMessaggio mes = createVirtualMessageWithSdk(idTransazione, saErogatore, dataConsegnaErogatore, tipoMessaggio);
			if(mes!=null){
				return mes;
			}
			
			// ignore
		} catch (Exception e) {
			this.log.error("Impossibile recuperare DumpMessaggio con idTransazione: "+ idTransazione + ", SA Erogatore: " + saErogatore + " e tipo: "	+ tipoMessaggio.toString(), e);
			throw new Exception("Impossibile recuperare DumpMessaggio con idTransazione: "	+ idTransazione + ", SA Erogatore: " + saErogatore + " e tipo: " + tipoMessaggio.toString(), e);
		}
		return null;
	}

	@Override
	public List<DumpAllegato> getAllegatiMessaggio(String idTransazione, String saErogatore, Date dataConsegnaErogatore, TipoMessaggio tipoMessaggio, Long idDump) {

		try {

			this.log.debug("Get allegati Messaggio [idDump: " + idDump + "]");
			
			if(idDump==TransazioniService.virtualIdRequest || idDump==TransazioniService.virtualIdResponse){
				throw new NotFoundException("Id Dump negative, possibile virtual message");
			}
			
			// return
			// this.em.createQuery("select d from DumpAllegato d where d.dumpMessaggio.id=:id_dump")
			// .setParameter("id_dump", idDump)
			// .getResultList();
			DumpMessaggio mes = ((IDBDumpMessaggioServiceSearch) this.dumpMessaggioSearchDAO).get(idDump);

			this.updateMessageWithSdk(mes);
			
			if (mes != null) {
				return mes.getAllegatoList();
			}

			// return this.em
			// .createQuery(
			// "select da from DumpMessaggio d JOIN d.allegatoList da where d.id=:id_dump")
			// .setParameter("id_dump", idDump).getResultList();
		}  catch (NotFoundException e) {
			this.log.debug("non sono state trovate Allegati per il messaggio con [id_messaggio: "	+ idDump + "]");
			
			try{
				// provo a vedere se esiste virtualmente grazie all'SDK
				DumpMessaggio mes = createVirtualMessageWithSdk(idTransazione, saErogatore, dataConsegnaErogatore, tipoMessaggio);
				if(mes!=null){
					return mes.getAllegatoList();
				}
			}catch (Exception eVirtual) {
				this.log.error(
						"Impossibile recuperare DumpAllegato con id_messaggio virtuale: "
								+ idDump, eVirtual);
			}
			
		}catch (Exception e) {
			this.log.error(
					"Impossibile recuperare DumpAllegato con id_messaggio: "
							+ idDump, e);
		}

		return new ArrayList<DumpAllegato>();
	}

	private String readObjectInMap(IField field,Map<String, Object> map){
		Object o = map.get(field.getFieldName());
		if(o!=null && (o instanceof String)){
			return (String) o;
		}
		else{
			return null;
		}
	}
		
	@Override
	public List<DumpContenuto> getContenutiSpecifici(String idTransazione, String saErogatore, Date dataConsegnaErogatore, TipoMessaggio tipoMessaggio, Long idDump) {
		try {

			this.log.debug("Get Contenuti specifici [idDump: "	+ idDump + "]");
			
			if(idDump==TransazioniService.virtualIdRequest || idDump==TransazioniService.virtualIdResponse){
				throw new NotFoundException("Id Dump negative, possibile virtual message");
			}
			
			// return
			// this.em.createQuery("select d from DumpContenuto d where d.dumpMessaggio.id=:id_dump")
			// .setParameter("id_dump", idDump)
			// .getResultList();

			DumpMessaggio mes = ((IDBDumpMessaggioServiceSearch) this.dumpMessaggioSearchDAO)
					.get(idDump);

			if (mes != null) {
				
				this.updateMessageWithSdk(mes);
				
				return mes.getContenutoList();
			}
			//
			// return this.em
			// .createQuery(
			// "select dc from DumpMessaggio d JOIN d.contenutoList dc where d.id=:id_dump")
			// .setParameter("id_dump", idDump).getResultList();

		} catch (NotFoundException e) {
			this.log.debug("non sono state trovate informazioni per DumpContenuto con [id_messaggio: "
					+ idDump + "]");
			
			try{
				// provo a vedere se esiste virtualmente grazie all'SDK
				DumpMessaggio mes = createVirtualMessageWithSdk(idTransazione, saErogatore, dataConsegnaErogatore, tipoMessaggio);
				if(mes!=null){
					return mes.getContenutoList();
				}
			}catch (Exception eVirtual) {
				this.log.error(
						"Impossibile recuperare DumpContenuto con id_messaggio virtuale: "
								+ idDump, eVirtual);
			}
			
		} catch (Exception e) {
			this.log.error(
					"Impossibile recuperare DumpContenuto con id_messaggio: "
							+ idDump, e);
		}

		return new ArrayList<DumpContenuto>();
	}
	
	@Override
	public Date getDataConsegnaErogatore(String idTransazione, String saErogatore, Date dataAccettazione) {
		try {

			this.log.info("Get data ultima consegna [id transazione: " + idTransazione + "],[SA Erogatore: " + saErogatore + "],[ dataAccettazione: " + dataAccettazione + "]");
			// Long l =
			// (Long)this.em.createQuery("select count(d) from DumpMessaggio d where d.idTransazione=:idTransazione and d.tipoMessaggio=:tipoMessaggio
			//AND ( (d.envelope is not null) OR (EXISTS (select id FROM DumpAllegato da WHERE da.dumpMessaggio.id=d.id)) OR (EXISTS (select id FROM DumpContenuto dc WHERE dc.dumpMessaggio.id=d.id)) ) ")
			// .setParameter("idTransazione", idTransazione)
			// .setParameter("tipoMessaggio", tipoMessaggio)
			// .getSingleResult();

			IExpression expr = this.dumpMessaggioSearchDAO.newExpression();
			expr.and().equals(DumpMessaggio.model().ID_TRANSAZIONE, idTransazione);
			
			if(saErogatore == null) {
				expr.isNull(DumpMessaggio.model().SERVIZIO_APPLICATIVO_EROGATORE);
			} else {
				expr.equals(DumpMessaggio.model().SERVIZIO_APPLICATIVO_EROGATORE, saErogatore);
			}
			
			if(dataAccettazione != null) {
				expr.greaterThan(DumpMessaggio.model().DUMP_TIMESTAMP, dataAccettazione);
			}
			
			// piu' recenti in cima?
			expr.addOrder(DumpMessaggio.model().DUMP_TIMESTAMP, SortOrder.DESC);
			
			//			IExpression orExpr = this.dumpMessaggioSearchDAO.newExpression();

			//			orExpr.isNotNull(DumpMessaggio.model().ENVELOPE);
			//			
			//					
			//			orExpr.or().isNotEmpty(DumpMessaggio.model().ALLEGATO.ID_ALLEGATO);
			//			orExpr.or().isNotEmpty(DumpMessaggio.model().CONTENUTO.NOME);
			//
			//			expr.and(orExpr);

			
			
			IPaginatedExpression pagExpr = this.dumpMessaggioSearchDAO.toPaginatedExpression(expr);
			
			// cerco solo un risultato
			pagExpr.offset(0).limit(1);
			
			List<Object> select = this.dumpMessaggioSearchDAO.select(pagExpr, DumpMessaggio.model().DATA_CONSEGNA_EROGATORE);
			
			if(select == null || select.isEmpty())
				return null;
						
			Object obj = select.get(0);
			
			if(obj instanceof Date)
				return (Date) obj;
		}  catch (NotFoundException e) {
			this.log.debug("non sono state trovate informazioni Dump per [id transazione: "+ idTransazione + "],[SA Erogatore: " + saErogatore + "],[ dataAccettazione: "+ dataAccettazione + "]");
		}catch (Exception e) {
			this.log.error(e.getMessage(), e);
		}

		return null;
	}
	
	@Override
	public boolean hasInfoDumpAvailable(String idTransazione, String saErogatore, Date dataConsegnaErogatore, TipoMessaggio tipoMessaggio) {

		try {

			this.log.info("Has Info Dump Available [id transazione: " + idTransazione + "],[SA Erogatore: " + saErogatore + "],[ dataAccettazione: " + tipoMessaggio.toString() + "]");
			// Long l =
			// (Long)this.em.createQuery("select count(d) from DumpMessaggio d where d.idTransazione=:idTransazione and d.tipoMessaggio=:tipoMessaggio
			//AND ( (d.envelope is not null) OR (EXISTS (select id FROM DumpAllegato da WHERE da.dumpMessaggio.id=d.id)) OR (EXISTS (select id FROM DumpContenuto dc WHERE dc.dumpMessaggio.id=d.id)) ) ")
			// .setParameter("idTransazione", idTransazione)
			// .setParameter("tipoMessaggio", tipoMessaggio)
			// .getSingleResult();

			IExpression expr = this.dumpMessaggioSearchDAO.newExpression();
			expr.equals(DumpMessaggio.model().TIPO_MESSAGGIO, TipoMessaggio.toEnumConstant(tipoMessaggio.toString()));
			expr.and().equals(DumpMessaggio.model().ID_TRANSAZIONE, idTransazione);
			
			if(saErogatore == null) {
				expr.isNull(DumpMessaggio.model().SERVIZIO_APPLICATIVO_EROGATORE);
			} else {
				expr.equals(DumpMessaggio.model().SERVIZIO_APPLICATIVO_EROGATORE, saErogatore);
			}
			
			if(dataConsegnaErogatore != null) {
				expr.equals(DumpMessaggio.model().DATA_CONSEGNA_EROGATORE, dataConsegnaErogatore);
			}
			
			// piu' recenti in cima?
			expr.addOrder(DumpMessaggio.model().DUMP_TIMESTAMP, SortOrder.DESC);
			
			//			IExpression orExpr = this.dumpMessaggioSearchDAO.newExpression();

			//			orExpr.isNotNull(DumpMessaggio.model().ENVELOPE);
			//			
			//					
			//			orExpr.or().isNotEmpty(DumpMessaggio.model().ALLEGATO.ID_ALLEGATO);
			//			orExpr.or().isNotEmpty(DumpMessaggio.model().CONTENUTO.NOME);
			//
			//			expr.and(orExpr);

			IPaginatedExpression pagExpr = this.dumpMessaggioSearchDAO.toPaginatedExpression(expr);
			
			// cerco solo un risultato
			pagExpr.offset(0).limit(1);

			List<DumpMessaggio> findAll = this.dumpMessaggioSearchDAO.findAll(pagExpr);
			
			if(findAll == null || findAll.isEmpty())
				throw new NotFoundException("Nessun messaggio trovato");
						
			DumpMessaggio msg = findAll.get(0);

			this.updateMessageWithSdk(msg);
			
			if(msg != null){
				return msg.getBody() != null || msg.sizeAllegatoList() > 0 || msg.sizeContenutoList() > 0;
			}

			//			NonNegativeNumber nnn = this.dumpMessaggioSearchDAO.count(expr);

			// (Long) this.em
			// .createQuery(
			// "select count(d) from DumpMessaggio d where
			// d.idTransazione=:idTransazione
			// and d._value_tipoMessaggio=:tipoMessaggio
			// AND ( (d.envelope IS NOT NULL) OR ( d.allegatoList IS NOT EMPTY )
			// OR ( d.contenutoList IS NOT EMPTY) ) ")
			// .setParameter("idTransazione", idTransazione)
			// .setParameter("tipoMessaggio", tipoMessaggio.toString())
			// .getSingleResult();

			//			return (nnn != null && nnn.longValue() > 0);

		}  catch (NotFoundException e) {
			this.log.debug("non sono state trovate informazioni Dump per [id transazione: "+ idTransazione + "],[SA Erogatore: " + saErogatore + "],[ tipomessaggio: "+ tipoMessaggio.toString() + "]");
			
			try{
				// provo a vedere se esiste virtualmente grazie all'SDK
				DumpMessaggio mes = createVirtualMessageWithSdk(idTransazione, saErogatore, dataConsegnaErogatore, tipoMessaggio);
				if(mes!=null){
					return mes.getBody() != null || mes.sizeAllegatoList() > 0 || mes.sizeContenutoList() > 0;
				}
			}catch (Exception eVirtual) {
				this.log.error(
						"Errore durante la costruzione virtuale del messaggio (hasInfoDumpAvailable) [id transazione: "+ idTransazione + "],[SA Erogatore: " + saErogatore + "],[ tipomessaggio: "+ tipoMessaggio.toString() + "]", eVirtual);
			}
			
		}catch (Exception e) {
			this.log.error(e.getMessage(), e);
		}

		return false;
	}
	
	@Override
	public boolean hasInfoHeaderTrasportoAvailable(String idTransazione, String saErogatore, Date dataConsegnaErogatore, TipoMessaggio tipoMessaggio) {

		this.log
		.info("Has Info Header Trasporto Available [id transazione: "	+ idTransazione + "],[SA Erogatore: " + saErogatore + "],[ tipomessaggio: "	+ tipoMessaggio.toString() + "]");
		try {

			// Long l =
			// (Long)this.em.createQuery("select count(d) from DumpMessaggio d,
			// DumpHeaderTrasporto dh where
			// d.id=dh.dumpMessaggio.id and d.idTransazione=:idTransazione and
			// d.tipoMessaggio=:tipoMessaggio")
			// .setParameter("idTransazione", idTransazione)
			// .setParameter("tipoMessaggio", tipoMessaggio)
			// .getSingleResult();

			IExpression expr = this.dumpMessaggioSearchDAO.newExpression();
			expr.equals(DumpMessaggio.model().TIPO_MESSAGGIO,
					TipoMessaggio.toEnumConstant(tipoMessaggio.toString()));
			expr.and().equals(DumpMessaggio.model().ID_TRANSAZIONE,
					idTransazione);
			
			if(saErogatore == null) {
				expr.isNull(DumpMessaggio.model().SERVIZIO_APPLICATIVO_EROGATORE);
			} else {
				expr.equals(DumpMessaggio.model().SERVIZIO_APPLICATIVO_EROGATORE, saErogatore);
			}
			
			if(dataConsegnaErogatore != null) {
				expr.equals(DumpMessaggio.model().DATA_CONSEGNA_EROGATORE, dataConsegnaErogatore);
			}
			
			// piu' recenti in cima?
			expr.addOrder(DumpMessaggio.model().DUMP_TIMESTAMP, SortOrder.DESC);
			
			IPaginatedExpression pagExpr = this.dumpMessaggioSearchDAO.toPaginatedExpression(expr);
			
			// cerco solo un risultato
			pagExpr.offset(0).limit(1);

			List<DumpMessaggio> findAll = this.dumpMessaggioSearchDAO.findAll(pagExpr);
			
			if(findAll == null || findAll.isEmpty())
				throw new NotFoundException("Nessun messaggio trovato");
						
			DumpMessaggio msg = findAll.get(0);

			this.updateMessageWithSdk(msg);
			
			if(msg != null){
				return msg.sizeHeaderTrasportoList() > 0;
			}

			//			IExpression expr = this.dumpMessaggioSearchDAO.newExpression();
			//			expr.equals(DumpMessaggio.model().TIPO_MESSAGGIO,
			//					TipoMessaggio.toEnumConstant(tipoMessaggio.toString()));
			//			expr.and().equals(DumpMessaggio.model().ID_TRANSAZIONE,
			//					idTransazione);
			//			expr.and().isNotEmpty(DumpMessaggio.model().HEADER_TRASPORTO.NOME);
			//
			//			NonNegativeNumber nnn = this.dumpMessaggioSearchDAO.count(expr);
			//
			//			return (nnn != null && nnn.longValue() > 0);

			// Long l = (Long) this.em
			// .createQuery(
			// "select count(d) from DumpMessaggio d where d.idTransazione=:idTransazione and"
			// +
			// " d._value_tipoMessaggio=:tipoMessaggio and d.headerTrasportoList IS NOT EMPTY")
			// .setParameter("idTransazione", idTransazione)
			// .setParameter("tipoMessaggio", tipoMessaggio.toString())
			// .getSingleResult();
			//
			// return (l != null && l > 0);

		}  catch (NotFoundException e) {
			this.log.debug("non sono state trovate informazioni sull'Header Trasporto per [id transazione: "+ idTransazione + "],[SA Erogatore: " + saErogatore + "],[ tipomessaggio: "+ tipoMessaggio.toString() + "]");
			
			try{
				// provo a vedere se esiste virtualmente grazie all'SDK
				DumpMessaggio mes = createVirtualMessageWithSdk(idTransazione, saErogatore, dataConsegnaErogatore, tipoMessaggio);
				if(mes!=null){
					return mes.sizeHeaderTrasportoList() > 0;
				}
			}catch (Exception eVirtual) {
				this.log.error(
						"Errore durante la costruzione virtuale del messaggio (hasInfoHeaderTrasportoAvailable) [id transazione: "+ idTransazione + "],[SA Erogatore: " + saErogatore + "],[ tipomessaggio: "+ tipoMessaggio.toString() + "]", eVirtual);
			}
			
		}catch (Exception e) {
			this.log.error(e.getMessage(), e);
		}

		return false;
	}

	@Override
	public List<DumpHeaderTrasporto> getHeaderTrasporto(String idTransazione, String saErogatore, Date dataConsegnaErogatore, TipoMessaggio tipoMessaggio, Long idDump) {

		try {

			this.log.debug("Get Header Trasporto [idDump: "	+ idDump + "]");
			// return
			// this.em.createQuery("select d from DumpHeaderTrasporto d where d.dumpMessaggio.id=:id_dump")
			// .setParameter("id_dump", idDump)
			// .getResultList();

			if(idDump==TransazioniService.virtualIdRequest || idDump==TransazioniService.virtualIdResponse){
				throw new NotFoundException("Id Dump negative, possibile virtual message");
			}
			
			DumpMessaggio m = ((IDBDumpMessaggioServiceSearch) this.dumpMessaggioSearchDAO)
					.get(idDump.longValue());

			this.updateMessageWithSdk(m);
			
			if (m != null) {
				return m.getHeaderTrasportoList();
			}

			// return this.em
			// .createQuery(
			// "select dh from DumpMessaggio d JOIN d.headerTrasportoList dh where d.id=:id_dump")
			// .setParameter("id_dump", idDump).getResultList();

		} catch (NotFoundException e) {
			this.log.debug("non sono state trovate informazioni dell'Header Trasporto con [id_messaggio: "	+ idDump + "]");
			
			try{
				// provo a vedere se esiste virtualmente grazie all'SDK
				DumpMessaggio mes = createVirtualMessageWithSdk(idTransazione, saErogatore, dataConsegnaErogatore, tipoMessaggio);
				if(mes!=null){
					return mes.getHeaderTrasportoList();
				}
			}catch (Exception eVirtual) {
				this.log.error(
						"Impossibile recuperare HeaderTrasporto con id_messaggio virtuale: "
								+ idDump, eVirtual);
			}
			
		} catch (Exception e) {
			this.log.error(
					"Impossibile recuperare DumpHeaderTrasporto con id_messaggio: "
							+ idDump, e);
		}

		return new ArrayList<DumpHeaderTrasporto>();
	}

	@Override
	public List<TransazioneBean> findAllDuplicati(String idTransazione, String idEgov, boolean isRisposta, int start, int limit) {
		List<TransazioneBean> listaBean = new ArrayList<TransazioneBean>();
		try {

			this.log.debug("Find All Duplicati [id transazione: "
					+ idTransazione + "],[ idEgov: " + idEgov
					+ "], [isRisposta: " + isRisposta + "]");

			IExpression expr = this.transazioniSearchDAO.newExpression();
			
			if (!isRisposta) {
				expr.equals(Transazione.model().ID_MESSAGGIO_RICHIESTA, idEgov);
			}
			else{
				expr.equals(Transazione.model().ID_MESSAGGIO_RISPOSTA, idEgov);
			}
			
			// INEFFICENTE: non vengono usati a modo gli indici. Si assume che i duplicati siano comunque sempre nel solito flusso
//			IExpression orExpr = this.transazioniSearchDAO.newExpression();
//			orExpr.equals(Transazione.model().ID_MESSAGGIO_RICHIESTA, idEgov);
//			orExpr.or().equals(Transazione.model().ID_MESSAGGIO_RISPOSTA,
//					idEgov);
//			expr.and(orExpr);
			expr.and();
			expr.notEquals(Transazione.model().ID_TRANSAZIONE, idTransazione);
			
			if(!isRisposta) {
				// Solo una porta applicativa puo' ricevere una busta di richiesta (serve per evitare i problemi in caso di loopback)
				expr.equals(Transazione.model().PDD_RUOLO, TipoPdD.APPLICATIVA.getTipo());
			}
			else {
				// Solo una porta delegata puo' ricevere una busta di risposta (serve per evitare i problemi in caso di loopback)
				expr.equals(Transazione.model().PDD_RUOLO, TipoPdD.DELEGATA.getTipo());
			}
			

			//			StringBuilder sb = new StringBuilder(
			//					"SELECT t FROM Transazione t where t.id<>:id_transazione AND ( t.idEgovRichiesta=:id_egov OR t.idEgovRisposta=:id_egov )");

			if (!isRisposta) {
				expr
				.sortOrder(SortOrder.ASC).addOrder(Transazione.model().DATA_INGRESSO_RICHIESTA);
				//				sb.append(" ORDER BY t.dataIngressoRichiesta");
			} else {
				expr
				.sortOrder(SortOrder.ASC).addOrder(Transazione.model().DATA_INGRESSO_RISPOSTA);
				//				sb.append(" ORDER BY t.dataIngressoRisposta");
			}

			IPaginatedExpression pagExpr = this.transazioniSearchDAO
					.toPaginatedExpression(expr);

			pagExpr.offset(start).limit(limit);

			List<Index> forceIndexDuplicatiFindAll = this.forceIndexIdMessaggioRichiestaFindAll;
			if (isRisposta) {
				forceIndexDuplicatiFindAll = this.forceIndexIdMessaggioRispostaFindAll;
			}
			if(forceIndexDuplicatiFindAll!=null && forceIndexDuplicatiFindAll.size()>0){
				for (Index index : forceIndexDuplicatiFindAll) {
					pagExpr.addForceIndex(index);	
				}
			}
			
			List<Transazione> list = this.transazioniSearchDAO.findAll(pagExpr); 

			if(list!= null && list.size() > 0)
				for (Transazione transazione : list) {
					TransazioneBean bean = new TransazioneBean(transazione, this.searchForm!=null ? this.searchForm.getSoggettoPddMonitor() : null);
					listaBean.add(bean);
				}


		} catch (Exception e) {
			this.log.error(e.getMessage(), e);
		}

		return listaBean;
	}

	@Override
	public TransazioneBean findTransazioneOriginale(String idTransazioneDuplicata,	String idEgov, boolean isRisposta) {

		this.log.info("Find Transazione Originale [id transazione duplicata: "	+ idTransazioneDuplicata + "],[ idEgov: " + idEgov + "], [isRisposta: " + isRisposta + "]");
		try {

			// String query =
			// "SELECT t FROM Transazione t where t.id<>:id_transazione AND ( t.idEgovRichiesta=:id_egov OR t.idEgovRisposta=:id_egov )";
			//
			// if(!isRisposta){
			// query += " AND t.duplicatiRichiesta>0";
			// }else{
			// query += " AND t.duplicatiRisposta>0";
			// }
			//
			// Query q = this.em.createQuery(query);
			//
			// q.setParameter("id_transazione", idTransazioneDuplicata)
			// .setParameter("id_egov", idEgov);
			//
			// return (Transazione) q.getSingleResult();

			IExpression expr = this.transazioniSearchDAO.newExpression();
			
			if (!isRisposta) {
				expr.equals(Transazione.model().ID_MESSAGGIO_RICHIESTA, idEgov);
			}
			else{
				expr.equals(Transazione.model().ID_MESSAGGIO_RISPOSTA, idEgov);
			}
			
			// INEFFICENTE: non vengono usati a modo gli indici. Si assume che i duplicati siano comunque sempre nel solito flusso
//			IExpression idEgovExpr = this.transazioniSearchDAO.newExpression();
//			idEgovExpr.or()
//			.equals(Transazione.model().ID_MESSAGGIO_RICHIESTA, idEgov)
//			.equals(Transazione.model().ID_MESSAGGIO_RISPOSTA, idEgov);
			expr.and();
			expr.notEquals(Transazione.model().ID_TRANSAZIONE,idTransazioneDuplicata);
//			.and(idEgovExpr);

			if (!isRisposta) {
				expr.greaterThan(Transazione.model().DUPLICATI_RICHIESTA, 0);
			} else {
				expr.greaterThan(Transazione.model().DUPLICATI_RISPOSTA, 0);
			}

			List<Index> forceIndexDuplicatiFindAll = this.forceIndexIdMessaggioRichiestaFindAll;
			if (isRisposta) {
				forceIndexDuplicatiFindAll = this.forceIndexIdMessaggioRispostaFindAll;
			}
			if(forceIndexDuplicatiFindAll!=null && forceIndexDuplicatiFindAll.size()>0){
				for (Index index : forceIndexDuplicatiFindAll) {
					expr.addForceIndex(index);	
				}
			}
			
			return new TransazioneBean(this.transazioniSearchDAO.find(expr), this.searchForm!=null ? this.searchForm.getSoggettoPddMonitor() : null);

		} catch (Exception e) {
			this.log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public int countAllDuplicati(String idTransazione, String idEgov,	boolean isRisposta) {

		this.log.debug("Count All Duplicati [id transazione: "	+ idTransazione + "],[ idEgov: " + idEgov + "], [isRisposta: "	+ isRisposta + "]");
		try {

			// Query q =
			// this.em.createQuery("SELECT count(t) FROM Transazione t where t.id<>:id_transazione AND ( t.idEgovRichiesta=:id_egov OR t.idEgovRisposta=:id_egov )");
			//
			// q.setParameter("id_transazione", idTransazione)
			// .setParameter("id_egov", idEgov);
			//
			// Long l = (Long) q.getSingleResult();
			//
			// return l!=null ? l.intValue() : 0;

			IExpression expr = this.transazioniSearchDAO.newExpression();
			
			if (!isRisposta) {
				expr.equals(Transazione.model().ID_MESSAGGIO_RICHIESTA, idEgov);
			}
			else{
				expr.equals(Transazione.model().ID_MESSAGGIO_RISPOSTA, idEgov);
			}
			
			// INEFFICENTE: non vengono usati a modo gli indici. Si assume che i duplicati siano comunque sempre nel solito flusso
//			IExpression idEgovExpr = this.transazioniSearchDAO.newExpression();
//			idEgovExpr.or()
//			.equals(Transazione.model().ID_MESSAGGIO_RICHIESTA, idEgov)
//			.equals(Transazione.model().ID_MESSAGGIO_RISPOSTA, idEgov);
			expr.and();
			expr.notEquals(Transazione.model().ID_TRANSAZIONE, idTransazione);
//			.and(idEgovExpr);

			List<Index> forceIndexDuplicatiCount = this.forceIndexIdMessaggioRichiestaCount;
			if (isRisposta) {
				forceIndexDuplicatiCount = this.forceIndexIdMessaggioRispostaCount;
			}
			if(forceIndexDuplicatiCount!=null && forceIndexDuplicatiCount.size()>0){
				for (Index index : forceIndexDuplicatiCount) {
					expr.addForceIndex(index);	
				}
			}
			
			NonNegativeNumber res = this.transazioniSearchDAO.count(expr);

			if (res == null)
				return 0;

			return (int) res.longValue();

		} catch (Exception e) {
			this.log.error(e.getMessage(), e);
		}
		return 0;
	}

	@Override
	public ResLive getEsiti(PermessiUtenteOperatore permessiUtente, Date min, Date max, String esitoContesto, 
			String protocolloSelected, String protocolloDefault, TipologiaRicerca tipologiaRicerca) {
		// StringBuilder pezzoIdPorta = new StringBuilder();

		this.log.debug("Get Esiti [permessiUtenti: " + permessiUtente + "],[ Date Min: " + min + "], [Date Max: " + max + "]");
		try {
			String protocolloP = protocolloDefault;
			if(protocolloSelected!=null) {
				protocolloP = protocolloSelected;
			}
			
			EsitoUtils esitoUtils = new EsitoUtils(this.log, protocolloP);
			EsitiProperties esitiProperties = EsitiProperties.getInstance(this.log, protocolloP);
			List<Integer> esitiOk = esitiProperties.getEsitiCodeOk_senzaFaultApplicativo();
			List<Integer> esitiKo = esitiProperties.getEsitiCodeKo_senzaFaultApplicativo();
			List<Integer> esitiFault = esitiProperties.getEsitiCodeFaultApplicativo();
			
			// Ok
			IExpression exprOk = this.transazioniSearchDAO.newExpression();
			exprOk.between(Transazione.model().DATA_INGRESSO_RICHIESTA, min,max);
			exprOk.and().in(Transazione.model().ESITO, esitiOk);
			if (permessiUtente != null) {
				IExpression permessi = permessiUtente.toExpression(this.transazioniSearchDAO, Transazione.model().PDD_CODICE, 
						Transazione.model().TIPO_SOGGETTO_EROGATORE, Transazione.model().NOME_SOGGETTO_EROGATORE, 
						Transazione.model().TIPO_SERVIZIO, Transazione.model().NOME_SERVIZIO, Transazione.model().VERSIONE_SERVIZIO);
				exprOk.and(permessi);
			}
			esitoUtils.setExpressionContesto(exprOk, Transazione.model().ESITO_CONTESTO, esitoContesto);
			if(tipologiaRicerca!=null) {
				if (TipologiaRicerca.ingresso.equals(tipologiaRicerca)) {
					exprOk.and().equals(Transazione.model().PDD_RUOLO, PddRuolo.APPLICATIVA);
				}
				else if (TipologiaRicerca.uscita.equals(tipologiaRicerca)) {
					exprOk.and().equals(Transazione.model().PDD_RUOLO, PddRuolo.DELEGATA);
				}
			}
			
			// Fault
			IExpression exprFault = this.transazioniSearchDAO.newExpression();
			exprFault.between(Transazione.model().DATA_INGRESSO_RICHIESTA, min,max);
			exprFault.and().in(Transazione.model().ESITO, esitiFault);
			if (permessiUtente != null) {
				IExpression permessi = permessiUtente.toExpression(this.transazioniSearchDAO, Transazione.model().PDD_CODICE, 
						Transazione.model().TIPO_SOGGETTO_EROGATORE, Transazione.model().NOME_SOGGETTO_EROGATORE, 
						Transazione.model().TIPO_SERVIZIO, Transazione.model().NOME_SERVIZIO, Transazione.model().VERSIONE_SERVIZIO);
				exprFault.and(permessi);
			}
			esitoUtils.setExpressionContesto(exprFault, Transazione.model().ESITO_CONTESTO, esitoContesto);
			if(tipologiaRicerca!=null) {
				if (TipologiaRicerca.ingresso.equals(tipologiaRicerca)) {
					exprFault.and().equals(Transazione.model().PDD_RUOLO, PddRuolo.APPLICATIVA);
				}
				else if (TipologiaRicerca.uscita.equals(tipologiaRicerca)) {
					exprFault.and().equals(Transazione.model().PDD_RUOLO, PddRuolo.DELEGATA);
				}
			}
			
			// Ko
			IExpression exprKo = this.transazioniSearchDAO.newExpression();
			exprKo.between(Transazione.model().DATA_INGRESSO_RICHIESTA, min,max);
			exprKo.and().in(Transazione.model().ESITO, esitiKo);
			if (permessiUtente != null) {
				IExpression permessi = permessiUtente.toExpression(this.transazioniSearchDAO, Transazione.model().PDD_CODICE, 
						Transazione.model().TIPO_SOGGETTO_EROGATORE, Transazione.model().NOME_SOGGETTO_EROGATORE, 
						Transazione.model().TIPO_SERVIZIO, Transazione.model().NOME_SERVIZIO, Transazione.model().VERSIONE_SERVIZIO);
				exprKo.and(permessi);
			}
			esitoUtils.setExpressionContesto(exprKo, Transazione.model().ESITO_CONTESTO, esitoContesto);
			if(tipologiaRicerca!=null) {
				if (TipologiaRicerca.ingresso.equals(tipologiaRicerca)) {
					exprKo.and().equals(Transazione.model().PDD_RUOLO, PddRuolo.APPLICATIVA);
				}
				else if (TipologiaRicerca.uscita.equals(tipologiaRicerca)) {
					exprKo.and().equals(Transazione.model().PDD_RUOLO, PddRuolo.DELEGATA);
				}
			}
			
			if(protocolloSelected!=null) {
				exprOk.and().equals(Transazione.model().PROTOCOLLO,	protocolloSelected);
				exprFault.and().equals(Transazione.model().PROTOCOLLO,	protocolloSelected);
				exprKo.and().equals(Transazione.model().PROTOCOLLO,	protocolloSelected);
			}
			
			// Devo rileggere il valore ogni volta, il service del live viene istanziato solamente una volta
			PddMonitorProperties govwayMonitorProperties = PddMonitorProperties.getInstance(this.log);
			Properties repositoryExternal = govwayMonitorProperties.getExternalForceIndexRepository();
			List<Index> forceIndexEsitiCount = convertForceIndexList(govwayMonitorProperties.getTransazioniForceIndexEsitiCount(repositoryExternal));
			if(forceIndexEsitiCount!=null && forceIndexEsitiCount.size()>0){
				for (Index index : forceIndexEsitiCount) {
					exprOk.addForceIndex(index);
					exprFault.addForceIndex(index);
					exprKo.addForceIndex(index);
				}
			}
			
			NonNegativeNumber nnnOk = this.transazioniSearchDAO.count(exprOk);
			NonNegativeNumber nnnFault = this.transazioniSearchDAO.count(exprFault);
			NonNegativeNumber nnnKo = this.transazioniSearchDAO.count(exprKo);

			if (nnnKo != null && nnnOk != null) {
				return new ResLive(nnnOk.longValue(), nnnFault.longValue(),nnnKo.longValue(),new Date());
			}

		} catch (ServiceException e) {
			this.log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			this.log.error(e.getMessage(), e);
		} catch (ExpressionNotImplementedException e) {
			this.log.error(e.getMessage(), e);
		} catch (ExpressionException e) {
			this.log.error(e.getMessage(), e);
		} catch (Exception e) {
			this.log.error(e.getMessage(), e);
		}

		return new ResLive(Long.valueOf("0"), Long.valueOf("0"), Long.valueOf("0"));

		// if (idPorta != null && idPorta.size() > 0) {
		// pezzoIdPorta
		// .append(" AND (t.identificativoPorta IN (:idPorta) OR t.identificativoPorta is null)");
		// }
		//
		// String esitiOK =
		// "(select count(t.esito) from Transazione t where t.dataIngressoRichiesta BETWEEN :minDate AND :maxDate AND t.esito in (0,8) "
		// + (idPorta != null ? pezzoIdPorta : "") + ") as esitiOK";
		// String esitiKO =
		// "(select count(t.esito) from Transazione t where t.dataIngressoRichiesta BETWEEN :minDate AND :maxDate AND t.esito>0 and t.esito <> 8"
		// + (idPorta != null ? pezzoIdPorta : "") + ") as esitiKO";
		//
		// String res = "select " + esitiOK + ", " + esitiKO
		// + " FROM Transazione t";
		//
		// Query q = this.em.createQuery(res);
		// q.setMaxResults(1);
		// q.setParameter("minDate", min);
		// q.setParameter("maxDate", max);
		//
		// if (idPorta != null && idPorta.size() > 0) {
		// q.setParameter("idPorta", idPorta);
		// }
		//
		// try {
		// Object[] esiti = (Object[]) q.getSingleResult();
		//
		// return new ResLive((Long) esiti[0], (Long) esiti[1]);
		// } catch (NoResultException e) {
		//
		// }
		// return new ResLive(new Long("0"), new Long("0"));
	}

	@Override
	public void deleteAll() throws Exception {
		try {

			this.log.debug("Delete All");

			IExpression expr = this.transazioniDAO.newExpression();

			parseExpressionFilter(expr, false, false);
			// ordinamento di default
			setOrderField(expr, SortOrder.DESC, null,false);

			this.transazioniDAO.deleteAll(expr);

			// Query q = this.createQuery(false, false, true);
			// q.executeUpdate();

		} catch (Exception e) {
			this.log.error(e.getMessage(), e);
			throw e;
		}
	}

	@Override
	public List<ConfigurazioneRicerca> getRicercheByValues(IDAccordo idAccordo,	String nomeServizio, String nomeAzione) {

		this.log.debug("Find ricerche by Values[id Accordo: " + idAccordo + "],[ nomeAzione: " + nomeAzione + "], [nomeServizio: " + nomeServizio + "]"); 
		try {

			if (idAccordo == null || nomeServizio == null) {
				this.log.warn("Impossibile recuperare lista ricerche: idAccordo e/o nomeServizio non forniti.");
				return new ArrayList<ConfigurazioneRicerca>();
			}

			List<ConfigurazioneRicerca> lista = new ArrayList<ConfigurazioneRicerca>();
			
			BasicServiceLibrary basicServiceLibrary = this.basicServiceLibraryReader.read(idAccordo,nomeServizio,nomeAzione, this.log);
			if(basicServiceLibrary!=null){
				SearchServiceLibrary searchServiceLibrary = this.searchServiceLibraryReader.readConfigurazioneRicerche(basicServiceLibrary, this.log);
				if(searchServiceLibrary!=null){
					lista = searchServiceLibrary.mergeServiceActionSearchLibrary(true,true);
				}
			}
			return lista;

		} catch (Exception e) {
			this.log.error(e.getMessage(), e);
		}

		return new ArrayList<ConfigurazioneRicerca>();
	}

	@Override
	public List<Parameter<?>> instanceParameters(ConfigurazioneRicerca configurazioneRicerca, Context context)	throws SearchException {

		this.log.info("Find Parametri By Ricerca ID [id Ricerca: " + configurazioneRicerca.getIdConfigurazioneRicerca()	+ "],[ className: " + configurazioneRicerca.getPlugin().getClassName() + "]");
		try {
			
			List<Parameter<?>> res = null;

			IDynamicLoader bl = DynamicFactory.getInstance().newDynamicLoader(configurazioneRicerca.getPlugin().getTipoPlugin(), configurazioneRicerca.getPlugin().getTipo(),
					configurazioneRicerca.getPlugin().getClassName(),this.log);
			List<Parameter<?>> sdkParameters = bl.getParameters(context);
			
			if(sdkParameters!=null && sdkParameters.size()>0){
				
				res = new ArrayList<Parameter<?>>();
				
				for (Parameter<?> sdkParameter : sdkParameters) {
					res.add(DynamicComponentUtils.createDynamicComponentParameter(sdkParameter, bl));
				}
			}

			return res;

		} catch (SearchException e) {
			this.log.error(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			this.log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public List<ConfigurazioneTransazioneStato> getStatiByValues(IDAccordo idAccordo, String nomeServizio, String azione) {

		this.log.debug("get Stati By Values [idAccord: " + idAccordo + "],[ nomeAzione: " + azione	+ "], [nomeServizio: " + nomeServizio + "]");
		try {
			if (idAccordo == null || nomeServizio == null) {
				this.log
				.error("Impossibile recuperare gli stati: idAccordo e/o nomeServizio non forniti.");

				return new ArrayList<ConfigurazioneTransazioneStato>();
			}

			IExpression espr = this.confSerAzSearchDAO.newExpression();

			espr.equals(
					ConfigurazioneServizioAzione.model().ID_CONFIGURAZIONE_SERVIZIO.SERVIZIO,
					nomeServizio);
			espr.and();
			espr.equals(
					ConfigurazioneServizioAzione.model().ID_CONFIGURAZIONE_SERVIZIO.ACCORDO,
					idAccordo.getNome());

			if (azione == null)
				espr.and().equals(ConfigurazioneServizioAzione.model().AZIONE,
						"*");
			else {
				IExpression azExpr = this.confSerAzSearchDAO.newExpression();
				azExpr.equals(ConfigurazioneServizioAzione.model().AZIONE, "*")
				.or()
				.equals(ConfigurazioneServizioAzione.model().AZIONE,
						azione);
				espr.and(azExpr);
			}

			if (idAccordo.getSoggettoReferente() != null) {
				if (idAccordo.getSoggettoReferente().getTipo() != null)
					espr.and()
					.equals(ConfigurazioneServizioAzione.model().ID_CONFIGURAZIONE_SERVIZIO.TIPO_SOGGETTO_REFERENTE,
							idAccordo.getSoggettoReferente().getTipo());
				if (idAccordo.getSoggettoReferente().getNome() != null)
					espr.and()
					.equals(ConfigurazioneServizioAzione.model().ID_CONFIGURAZIONE_SERVIZIO.NOME_SOGGETTO_REFERENTE,
							idAccordo.getSoggettoReferente().getNome());
			}

			if (idAccordo.getVersione() != null)
				espr.and()
				.equals(ConfigurazioneServizioAzione.model().ID_CONFIGURAZIONE_SERVIZIO.VERSIONE,
						idAccordo.getVersione());

			IPaginatedExpression pagExpr = this.confSerAzSearchDAO
					.toPaginatedExpression(espr);

			List<ConfigurazioneServizioAzione> lst = this.confSerAzSearchDAO
					.findAll(pagExpr);

			TreeMap<Long, ConfigurazioneTransazione> mappa = new TreeMap<Long, ConfigurazioneTransazione>();
			if (lst != null && lst.size() > 0) {
				IExpression expr = null;

				for (ConfigurazioneServizioAzione conf : lst) {

					expr = this.confTransazioneSearchDAO.newExpression();

					expr.equals(
							ConfigurazioneTransazione.model().ID_CONFIGURAZIONE_SERVIZIO_AZIONE.AZIONE,
							conf.getAzione());

					expr.and()
					.equals(ConfigurazioneTransazione.model().ID_CONFIGURAZIONE_SERVIZIO_AZIONE.ID_CONFIGURAZIONE_SERVIZIO.ACCORDO,
							conf.getIdConfigurazioneServizio()
							.getAccordo());
					expr.and()
					.equals(ConfigurazioneTransazione.model().ID_CONFIGURAZIONE_SERVIZIO_AZIONE.ID_CONFIGURAZIONE_SERVIZIO.NOME_SOGGETTO_REFERENTE,
							conf.getIdConfigurazioneServizio()
							.getNomeSoggettoReferente());
					expr.and()
					.equals(ConfigurazioneTransazione.model().ID_CONFIGURAZIONE_SERVIZIO_AZIONE.ID_CONFIGURAZIONE_SERVIZIO.TIPO_SOGGETTO_REFERENTE,
							conf.getIdConfigurazioneServizio()
							.getTipoSoggettoReferente());
					expr.and()
					.equals(ConfigurazioneTransazione.model().ID_CONFIGURAZIONE_SERVIZIO_AZIONE.ID_CONFIGURAZIONE_SERVIZIO.SERVIZIO,
							conf.getIdConfigurazioneServizio()
							.getServizio());
					expr.and()
					.equals(ConfigurazioneTransazione.model().ID_CONFIGURAZIONE_SERVIZIO_AZIONE.ID_CONFIGURAZIONE_SERVIZIO.VERSIONE,
							conf.getIdConfigurazioneServizio()
							.getVersione());

					ConfigurazioneTransazione t = null;

					try{
						t = this.confTransazioneSearchDAO.find(expr);
					}catch(NotFoundException e){
						// non aggiungo la risorsa
						t = null;
					}

					if (t != null) {
						if (!mappa.containsKey(t.getId())) {
							mappa.put(t.getId(), t);
						}
					}
				}

				if (mappa != null && mappa.size() > 0) {
					ArrayList<ConfigurazioneTransazioneStato> ts = new ArrayList<ConfigurazioneTransazioneStato>();
					for (ConfigurazioneTransazione tr : mappa.values()) {
						ts.addAll(tr.getConfigurazioneTransazioneStatoList());
					}

					return ts;
				}

			}

			// StringBuilder sb = new StringBuilder(
			// "SELECT st FROM StatoConfig st JOIN st.transazione t JOIN t.azioneConfig a JOIN a.configurazioneServizio s WHERE s.servizio=:nome_servizio AND s.accordo=:nome_accordo ");
			//
			// if (idAccordo.getSoggettoReferente() != null) {
			// if (idAccordo.getSoggettoReferente().getTipo() != null)
			// sb.append(" AND s.tipoSoggettoReferente=:tipo_referente");
			// if (idAccordo.getSoggettoReferente().getNome() != null)
			// sb.append(" AND s.nomeSoggettoReferente=:nome_referente");
			// }
			//
			// if (idAccordo.getVersione() != null)
			// sb.append(" AND s.versione=:versione ");
			//
			// if (azione != null)
			// sb.append(" AND a.azione=:nome_azione");
			//
			// Query q1 = this.em.createQuery(sb.toString());
			//
			// q1.setParameter("nome_servizio", nomeServizio);
			// q1.setParameter("nome_accordo", idAccordo.getNome());
			//
			// if (azione != null)
			// q1.setParameter("nome_azione", azione);
			//
			// if (idAccordo.getSoggettoReferente() != null) {
			// if (idAccordo.getSoggettoReferente().getTipo() != null)
			// q1.setParameter("tipo_referente", idAccordo
			// .getSoggettoReferente().getTipo());
			// if (idAccordo.getSoggettoReferente().getNome() != null)
			// q1.setParameter("nome_referente", idAccordo
			// .getSoggettoReferente().getNome());
			// }
			//
			// if (idAccordo.getVersione() != null)
			// q1.setParameter("versione", idAccordo.getVersione());
			//
			// return q1.getResultList();
		} catch (Exception e) {
			this.log.error(e.getMessage(), e);
		}

		return new ArrayList<ConfigurazioneTransazioneStato>();
	}

	@Override
	public List<ConfigurazioneTransazioneRisorsaContenuto> getRisorseContenutoByValues(IDAccordo idAccordo, String nomeServizio, String nomeAzione,	String nomeStato) {
		if (idAccordo == null || nomeServizio == null) {
			this.log.error("Impossibile recuperare gli stati: idAccordo e/o nomeServizio non forniti.");
			return null;
		}

		this.log
		.info("Get risorse contenuto by values[idAccord: " + idAccordo
				+ "],[ nomeServizio: " + nomeServizio
				+ "], [nomeAzione: " + nomeAzione + "], [nomeStato: "
				+ nomeStato + "]");

		try {

			IExpression espr = this.confSerAzSearchDAO.newExpression();

			espr.equals(
					ConfigurazioneServizioAzione.model().ID_CONFIGURAZIONE_SERVIZIO.SERVIZIO,
					nomeServizio);
			espr.and();
			espr.equals(
					ConfigurazioneServizioAzione.model().ID_CONFIGURAZIONE_SERVIZIO.ACCORDO,
					idAccordo.getNome());
			if (nomeAzione == null)
				espr.and().equals(ConfigurazioneServizioAzione.model().AZIONE,
						"*");
			else {
				IExpression azExpr = this.confSerAzSearchDAO.newExpression();
				azExpr.equals(ConfigurazioneServizioAzione.model().AZIONE, "*")
				.or()
				.equals(ConfigurazioneServizioAzione.model().AZIONE,
						nomeAzione);
				espr.and(azExpr);
			}
			if (idAccordo.getSoggettoReferente() != null) {
				if (idAccordo.getSoggettoReferente().getTipo() != null)
					espr.and()
					.equals(ConfigurazioneServizioAzione.model().ID_CONFIGURAZIONE_SERVIZIO.TIPO_SOGGETTO_REFERENTE,
							idAccordo.getSoggettoReferente().getTipo());
				if (idAccordo.getSoggettoReferente().getNome() != null)
					espr.and()
					.equals(ConfigurazioneServizioAzione.model().ID_CONFIGURAZIONE_SERVIZIO.NOME_SOGGETTO_REFERENTE,
							idAccordo.getSoggettoReferente().getNome());
			}

			if (idAccordo.getVersione() != null)
				espr.and()
				.equals(ConfigurazioneServizioAzione.model().ID_CONFIGURAZIONE_SERVIZIO.VERSIONE,
						idAccordo.getVersione());

			IPaginatedExpression pagExpr = this.confSerAzSearchDAO
					.toPaginatedExpression(espr);

			List<ConfigurazioneServizioAzione> lst = this.confSerAzSearchDAO
					.findAll(pagExpr);

			TreeMap<Long, ConfigurazioneTransazione> mappa = new TreeMap<Long, ConfigurazioneTransazione>();
			if (lst != null && lst.size() > 0) {
				IExpression expr = null;

				for (ConfigurazioneServizioAzione conf : lst) {

					expr = this.confTransazioneSearchDAO.newExpression();

					expr.equals(
							ConfigurazioneTransazione.model().ID_CONFIGURAZIONE_SERVIZIO_AZIONE.AZIONE,
							conf.getAzione());

					expr.and()
					.equals(ConfigurazioneTransazione.model().ID_CONFIGURAZIONE_SERVIZIO_AZIONE.ID_CONFIGURAZIONE_SERVIZIO.ACCORDO,
							conf.getIdConfigurazioneServizio()
							.getAccordo());
					expr.and()
					.equals(ConfigurazioneTransazione.model().ID_CONFIGURAZIONE_SERVIZIO_AZIONE.ID_CONFIGURAZIONE_SERVIZIO.NOME_SOGGETTO_REFERENTE,
							conf.getIdConfigurazioneServizio()
							.getNomeSoggettoReferente());
					expr.and()
					.equals(ConfigurazioneTransazione.model().ID_CONFIGURAZIONE_SERVIZIO_AZIONE.ID_CONFIGURAZIONE_SERVIZIO.TIPO_SOGGETTO_REFERENTE,
							conf.getIdConfigurazioneServizio()
							.getTipoSoggettoReferente());
					expr.and()
					.equals(ConfigurazioneTransazione.model().ID_CONFIGURAZIONE_SERVIZIO_AZIONE.ID_CONFIGURAZIONE_SERVIZIO.SERVIZIO,
							conf.getIdConfigurazioneServizio()
							.getServizio());
					expr.and()
					.equals(ConfigurazioneTransazione.model().ID_CONFIGURAZIONE_SERVIZIO_AZIONE.ID_CONFIGURAZIONE_SERVIZIO.VERSIONE,
							conf.getIdConfigurazioneServizio()
							.getVersione());

					if (StringUtils.isNotBlank(nomeStato) && !"*".equals(nomeStato))
						expr.and()
						.equals(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_STATO.NOME,
								nomeStato);

					ConfigurazioneTransazione t = null;

					try{
						t = this.confTransazioneSearchDAO.find(expr);
					}catch(NotFoundException e){
						// non aggiungo la risorsa
						t = null;
					}

					if (t != null) {
						if (!mappa.containsKey(t.getId())) {
							mappa.put(t.getId(), t);
						}
					}

				}

				if (mappa != null && mappa.size() > 0) {
					ArrayList<ConfigurazioneTransazioneRisorsaContenuto> toRet = new ArrayList<ConfigurazioneTransazioneRisorsaContenuto>();
					for (org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazione tr : mappa.values()) {
						toRet.addAll(tr.getConfigurazioneTransazioneRisorsaContenutoList());
					}

					return toRet;
				}

			}

			// StringBuilder sb = new StringBuilder(
			// "SELECT r FROM RisorsaContenutoConfig r ");
			//
			// if (nomeStato != null && !"*".equals(nomeStato))
			// sb.append(" JOIN r.stato st JOIN st.transazione t ");
			// else
			// sb.append(" JOIN r.transazione t ");
			//
			// sb.append(" JOIN t.azioneConfig a JOIN a.configurazioneServizio s WHERE s.servizio=:nome_servizio AND s.accordo=:nome_accordo ");
			//
			// if (idAccordo.getSoggettoReferente() != null) {
			// if (idAccordo.getSoggettoReferente().getTipo() != null)
			// sb.append(" AND s.tipoSoggettoReferente=:tipo_referente");
			// if (idAccordo.getSoggettoReferente().getNome() != null)
			// sb.append(" AND s.nomeSoggettoReferente=:nome_referente");
			// }
			//
			// if (idAccordo.getVersione() != null)
			// sb.append(" AND s.versione=:versione");
			//
			// if (nomeAzione != null)
			// sb.append(" AND a.azione=:nome_azione");
			//
			// if (nomeStato != null && !"*".equals(nomeStato))
			// sb.append(" AND st.nome=:nome_stato");
			//
			// Query q1 = this.em.createQuery(sb.toString());
			//
			// q1.setParameter("nome_servizio", nomeServizio);
			// q1.setParameter("nome_accordo", idAccordo.getNome());
			//
			// if (nomeAzione != null)
			// q1.setParameter("nome_azione", nomeAzione);
			//
			// if (nomeStato != null && !"*".equals(nomeStato))
			// q1.setParameter("nome_stato", nomeStato);
			//
			// if (idAccordo.getSoggettoReferente() != null) {
			// if (idAccordo.getSoggettoReferente().getTipo() != null)
			// q1.setParameter("tipo_referente", idAccordo
			// .getSoggettoReferente().getTipo());
			// if (idAccordo.getSoggettoReferente().getNome() != null)
			// q1.setParameter("nome_referente", idAccordo
			// .getSoggettoReferente().getNome());
			// }
			//
			// if (idAccordo.getVersione() != null)
			// q1.setParameter("versione", idAccordo.getVersione());
			//
			// return q1.getResultList();
		} catch (Exception e) {
			this.log.error(e.getMessage(), e);
		}
		return new ArrayList<ConfigurazioneTransazioneRisorsaContenuto>();
	}

	private void parseExpressionFilter(IExpression filter, boolean isCount,	boolean isLiveSearch) throws Exception {

		ModalitaRicercaTransazioni ricerca = null;
		if(!isLiveSearch){
			ricerca = ModalitaRicercaTransazioni.getFromString(this.searchForm.getModalitaRicercaStorico());
		}
		
		// ricerca is null in modalità live
		if(ricerca!=null && ModalitaRicercaTransazioni.ID_MESSAGGIO.equals(ricerca) ){
			if (StringUtils.isNotEmpty(this.searchForm.getIdEgov())) {
				
				org.openspcoop2.web.monitor.core.constants.TipoMessaggio tipoMessaggio = 
						org.openspcoop2.web.monitor.core.constants.TipoMessaggio.valueOf(this.searchForm.getTipoIdMessaggio());
				
				String value = this.searchForm.getIdEgov().trim();
				switch (tipoMessaggio) {
				case Richiesta:
					filter.equals(Transazione.model().ID_MESSAGGIO_RICHIESTA, value);
					break;
				case Risposta:
					filter.equals(Transazione.model().ID_MESSAGGIO_RISPOSTA, value);
					break;
				case Collaborazione:
					filter.equals(Transazione.model().ID_COLLABORAZIONE, value);
					break;
				case RiferimentoRichiesta:
					filter.equals(Transazione.model().ID_ASINCRONO, value);
					break;
				}
				// Inefficente altrimenti fare la OR
//				IExpression idegov = this.transazioniSearchDAO.newExpression();
//				idegov.or();
//				idegov.equals(Transazione.model().ID_MESSAGGIO_RICHIESTA,	value);
//				idegov.equals(Transazione.model().ID_MESSAGGIO_RISPOSTA,	value);
//				filter.and(idegov);
				return;
			}
			else{
				throw new Exception("ID Messaggio non fornito");
			}
		}
		
		// ricerca is null in modalità live
		if(ricerca!=null && ModalitaRicercaTransazioni.ID_TRANSAZIONE.equals(ricerca) ){
			if (StringUtils.isNotEmpty(this.searchForm.getIdTransazione())) {
				String value = this.searchForm.getIdTransazione().trim();
				filter.equals(Transazione.model().ID_TRANSAZIONE,	value);
				return;
			}
			else{
				throw new Exception("ID Transazione non fornito");
			}
		}
		
		// check ricerca libera
		boolean ricercaLibera = isRicercaLibera();
		boolean ricercaLiberaCaseSensitive = false;
		boolean ricercaLiberaEsatta = false;
		LikeMode ricercaLiberaLikeMode = null;
		if(ricercaLibera) {
			ricercaLiberaLikeMode = getRicercaLiberaEsattaLikeMode();
			ricercaLiberaEsatta = isRicercaLiberaEsatta();
			ricercaLiberaCaseSensitive = isRicercaLiberaCaseSensitive();
		}
		
		// condizioni che hanno senso su tutte le query
		if (this.searchForm.getDataInizio() != null && !isLiveSearch) {
			// non mi serve in caso di liveSearch
			// sb.append("AND t.dataIngressoRichiesta >= :data_inizio ");
			filter.and().greaterEquals(	Transazione.model().DATA_INGRESSO_RICHIESTA,this.searchForm.getDataInizio());
		}

		if (this.searchForm.getDataFine() != null && !isLiveSearch) {
			// sb.append("AND t.dataIngressoRichiesta <= :data_fine ");
			filter.and().lessEquals(Transazione.model().DATA_INGRESSO_RICHIESTA,this.searchForm.getDataFine());
		}

		if (this.searchForm.getDataRicerca() != null && !isLiveSearch) {
			// sb.append(" AND t.dataIngressoRichiesta<= :data_richiesta_ricerca");
			filter.and().lessEquals(Transazione.model().DATA_INGRESSO_RICHIESTA,this.searchForm.getDataRicerca());
		}

		if(isLiveSearch){
			// BugFix: per evitare che il live non utilizzi un filtro temporale.
			//	Senza questa condizione il live su una base dati con milioni di record è inutilizzabile.
			
			if(this.liveUltimiGiorni!=null && this.liveUltimiGiorni!=0){
			
				int numeroGiorni = this.liveUltimiGiorni.intValue();
				if(numeroGiorni>0){
					// deve essere un numero negativo
					numeroGiorni = numeroGiorni * -1;
				}
				
				Calendar c = Calendar.getInstance();
				c.set(Calendar.HOUR_OF_DAY, 0);
				c.set(Calendar.MINUTE, 0);
				c.set(Calendar.SECOND, 0);
				c.set(Calendar.MILLISECOND, 0);
				c.add(Calendar.DATE, numeroGiorni);
	
				filter.and().greaterThan(
						Transazione.model().DATA_INGRESSO_RICHIESTA,
						c.getTime());
				
			}
		}
		
		
		// esito
		EsitoUtils esitoUtils = new EsitoUtils(this.log, this.searchForm.getSafeProtocol());
		esitoUtils.setExpression(filter, this.searchForm.getEsitoGruppo(), 
				this.searchForm.getEsitoDettaglio(),
				this.searchForm.getEsitoDettaglioPersonalizzato(),
				this.searchForm.getEsitoContesto(),
				this.searchForm.isEscludiRichiesteScartate(),
				Transazione.model().ESITO, Transazione.model().ESITO_CONTESTO,
				this.transazioniSearchDAO.newExpression());
		
		
		if (this.searchForm.getTipologiaTransazioneSPCoop() == null) {

			// ENTRAMBE
			// sb.append(" WHERE  ( 0=0 ");
			// filter.and();

			boolean addAnd = false;
			
			// permessi utente operatore
			if(this.searchForm.getPermessiUtenteOperatore()!=null){
				IExpression permessi = this.searchForm.getPermessiUtenteOperatore().toExpression(this.transazioniSearchDAO, Transazione.model().PDD_CODICE, 
						Transazione.model().TIPO_SOGGETTO_EROGATORE, Transazione.model().NOME_SOGGETTO_EROGATORE, 
						Transazione.model().TIPO_SERVIZIO, Transazione.model().NOME_SERVIZIO, Transazione.model().VERSIONE_SERVIZIO);
				filter.and(permessi);
				addAnd = true;
			}
			
			// TODO Decommentare appena risolto bug delle enumeration
			if (this.searchForm.getTipologiaRicercaEnum() == null || TipologiaRicerca.all.equals(this.searchForm.getTipologiaRicercaEnum())) {
				// devo prendere tutte le transazioni
			} else if (TipologiaRicerca.ingresso.equals(this.searchForm.getTipologiaRicercaEnum())) {
				// EROGAZIONE
				// sb.append(" AND t.tipoPorta <> 'delegata' ");
				if (addAnd)
					filter.and();

				filter.notEquals(Transazione.model().PDD_RUOLO,	PddRuolo.DELEGATA);

				addAnd = true;
			} else {
				// FRUIZIONE
				// sb.append(" AND t.tipoPorta <> 'applicativa' ");
				if (addAnd)
					filter.and();
				filter.notEquals(Transazione.model().PDD_RUOLO,	PddRuolo.APPLICATIVA);

				addAnd = true;
			}
			// sb.append(" ) ");

		} else if (this.searchForm.getTipologiaTransazioneSPCoop()) {

			// //CASO SPCOOP
			// sb.append(" WHERE ( 0=0 ");
			//
			boolean addAnd = false;

			// permessi utente operatore
			if(this.searchForm.getPermessiUtenteOperatore()!=null){
				IExpression permessi = this.searchForm.getPermessiUtenteOperatore().toExpression(this.transazioniSearchDAO, Transazione.model().PDD_CODICE, 
						Transazione.model().TIPO_SOGGETTO_EROGATORE, Transazione.model().NOME_SOGGETTO_EROGATORE, 
						Transazione.model().TIPO_SERVIZIO, Transazione.model().NOME_SERVIZIO, Transazione.model().VERSIONE_SERVIZIO);
				filter.and(permessi);
				addAnd = true;
			}

			// TODO Decommentare appena risolto bug delle enumeration
			if (this.searchForm.getTipologiaRicercaEnum() == null || TipologiaRicerca.all.equals(this.searchForm.getTipologiaRicercaEnum())) {
				// devo prendere tutte le transazioni che sono diverse da
				// integration manager
				// sb.append(" AND t.tipoPorta <> 'integration_manager' ");
				if(this.isAttivoSqlFilterTransazioniIntegrationManager) {
					filter.notEquals(Transazione.model().PDD_RUOLO,	PddRuolo.INTEGRATION_MANAGER);
				}
			} else if (TipologiaRicerca.ingresso.equals(this.searchForm.getTipologiaRicercaEnum())) {
				// EROGAZIONE
				// sb.append(" AND t.tipoPorta = 'applicativa' ");
				if (addAnd)
					filter.and();

				filter.equals(Transazione.model().PDD_RUOLO,PddRuolo.APPLICATIVA);
				addAnd = true;
			} else {
				// FRUIZIONE
				// sb.append(" AND t.tipoPorta = 'delegata' ");

				if (addAnd)
					filter.and();
				filter.equals(Transazione.model().PDD_RUOLO,PddRuolo.DELEGATA);
				addAnd = true;
			}
			// sb.append(" ) ");

		} else {
			
			// permessi utente operatore
			if(this.searchForm.getPermessiUtenteOperatore()!=null){
				IExpression permessi = this.searchForm.getPermessiUtenteOperatore().toExpression(this.transazioniSearchDAO, Transazione.model().PDD_CODICE, 
						Transazione.model().TIPO_SOGGETTO_EROGATORE, Transazione.model().NOME_SOGGETTO_EROGATORE, 
						Transazione.model().TIPO_SERVIZIO, Transazione.model().NOME_SERVIZIO, Transazione.model().VERSIONE_SERVIZIO);
				filter.and(permessi);
			}
			
			// CASO IM
			// sb.append("WHERE (t.identificativoPorta is NULL AND t.tipoPorta='integration_manager') ");
			//filter.isNull(Transazione.model().PDD_CODICE)
			filter.and().equals(Transazione.model().PDD_RUOLO,	PddRuolo.INTEGRATION_MANAGER);
		}

		// condizioni presenti solo in tipoRicercaSPCoop==null oppure
		// tipoRicercaSPCoop=true
		if (this.searchForm.getTipologiaTransazioneSPCoop() == null	|| this.searchForm.getTipologiaTransazioneSPCoop()) {

			if(Utility.isFiltroDominioAbilitato() && this.searchForm.getSoggettoLocale()!=null && !StringUtils.isEmpty(this.searchForm.getSoggettoLocale()) && !"--".equals(this.searchForm.getSoggettoLocale())){
				String tipoSoggettoLocale = this.searchForm.getTipoSoggettoLocale();
				String nomeSoggettoLocale = this.searchForm.getSoggettoLocale();
				String idPorta = Utility.getIdentificativoPorta(tipoSoggettoLocale, nomeSoggettoLocale);
				filter.and().equals(Transazione.model().PDD_CODICE,	idPorta);
			}
			
			if (StringUtils.isNotBlank(this.searchForm.getNomeAzione())) {
				// sb.append("AND t.azione = :nome_azione ");
				filter.and().equals(Transazione.model().AZIONE,	this.searchForm.getNomeAzione());
			}
			else {
				if(ricercaLibera && StringUtils.isNotBlank(this.searchForm.getRicercaLiberaAzione())) {
					if(ricercaLiberaCaseSensitive) {
						filter.and().like(Transazione.model().AZIONE, this.searchForm.getRicercaLiberaAzione(), ricercaLiberaLikeMode);
					}
					else {
						filter.and().ilike(Transazione.model().AZIONE, this.searchForm.getRicercaLiberaAzione(), ricercaLiberaLikeMode);
					}
				}
			}

			if (StringUtils.isNotBlank(this.searchForm.getNomeServizio())) {
				// sb.append("AND t.servizio = :nome_servizio ");
				// il servizio e' nella forma
				// ServizioSincrono (SPC/ErogatoreEsterno2:ASTest1:1)
				String servizioString = this.searchForm.getNomeServizio();

				IDServizio idServizio = ParseUtility.parseServizioSoggetto(servizioString);
				
				filter.and().
					equals(Transazione.model().TIPO_SOGGETTO_EROGATORE,	idServizio.getSoggettoErogatore().getTipo()).
					equals(Transazione.model().NOME_SOGGETTO_EROGATORE,	idServizio.getSoggettoErogatore().getNome()).
					equals(Transazione.model().TIPO_SERVIZIO,	idServizio.getTipo()).
					equals(Transazione.model().NOME_SERVIZIO,	idServizio.getNome()).
					equals(Transazione.model().VERSIONE_SERVIZIO,	idServizio.getVersione());
			}
			else {
				if(ricercaLibera && StringUtils.isNotBlank(this.searchForm.getRicercaLiberaServizio())) {
					if(ricercaLiberaCaseSensitive) {
						filter.and().like(Transazione.model().NOME_SERVIZIO, this.searchForm.getRicercaLiberaServizio(), ricercaLiberaLikeMode);
					}
					else {
						filter.and().ilike(Transazione.model().NOME_SERVIZIO, this.searchForm.getRicercaLiberaServizio(), ricercaLiberaLikeMode);
					}
				}
			}

			if(ricerca!=null && ModalitaRicercaTransazioni.ID_APPLICATIVO.equals(ricerca) ){
				if (StringUtils.isNotEmpty(this.searchForm
						.getIdCorrelazioneApplicativa())) {
					filter.and();
	
					// sb.append(" AND t.idCorrelazioneApplicativa ");
					// TipoMatch match =
					// ((TransazioniSearchForm)this.searchForm).getCorrelazioneApplicativaMatchingType();
					// sb.append(TipoMatch.LIKE.compareTo(match)==0 ? "LIKE" : "=");
					// sb.append("  :id_correlazione_applicativa ");
					IExpression idcorr = this.transazioniSearchDAO.newExpression();
					IExpression idcorrRisp = this.transazioniSearchDAO.newExpression();
	
					String value = this.searchForm.getIdCorrelazioneApplicativa().trim();
					
					CaseSensitiveMatch caseSensitiveMatch = CaseSensitiveMatch.valueOf(this.searchForm.getCorrelazioneApplicativaCaseSensitiveType());
					TipoMatch match = TipoMatch.valueOf(this.searchForm.getCorrelazioneApplicativaMatchingType());
					if (TipoMatch.LIKE.equals(match)) {
						if(CaseSensitiveMatch.SENSITIVE.equals(caseSensitiveMatch)){
							idcorr.like(Transazione.model().ID_CORRELAZIONE_APPLICATIVA,value,LikeMode.ANYWHERE);
							idcorrRisp.like(Transazione.model().ID_CORRELAZIONE_APPLICATIVA_RISPOSTA,value,LikeMode.ANYWHERE);
						}
						else{
							idcorr.ilike(Transazione.model().ID_CORRELAZIONE_APPLICATIVA,value,LikeMode.ANYWHERE);
							idcorrRisp.ilike(Transazione.model().ID_CORRELAZIONE_APPLICATIVA_RISPOSTA,value,LikeMode.ANYWHERE);
						}
					} else {				
						if(CaseSensitiveMatch.SENSITIVE.equals(caseSensitiveMatch)){
							idcorr.equals(Transazione.model().ID_CORRELAZIONE_APPLICATIVA,	value);
							idcorrRisp.equals(Transazione.model().ID_CORRELAZIONE_APPLICATIVA_RISPOSTA, value);
						}
						else{
							idcorr.ilike(Transazione.model().ID_CORRELAZIONE_APPLICATIVA,	value,LikeMode.EXACT);
							idcorrRisp.ilike(Transazione.model().ID_CORRELAZIONE_APPLICATIVA_RISPOSTA, value,LikeMode.EXACT);
						}
					}
					filter.or(idcorr, idcorrRisp);
				}
				else{
					throw new Exception("ID Applicativo non fornito");
				}
			}
			else {
				if(ricercaLibera && StringUtils.isNotBlank(this.searchForm.getRicercaLiberaIdApplicativo())) {
					IExpression idcorr = this.transazioniSearchDAO.newExpression();
					IExpression idcorrRisp = this.transazioniSearchDAO.newExpression();
					if(ricercaLiberaCaseSensitive) {
						idcorr.and().like(Transazione.model().ID_CORRELAZIONE_APPLICATIVA, this.searchForm.getRicercaLiberaIdApplicativo(), ricercaLiberaLikeMode);
						idcorrRisp.and().like(Transazione.model().ID_CORRELAZIONE_APPLICATIVA_RISPOSTA, this.searchForm.getRicercaLiberaIdApplicativo(), ricercaLiberaLikeMode);
					}
					else {
						idcorr.and().ilike(Transazione.model().ID_CORRELAZIONE_APPLICATIVA, this.searchForm.getRicercaLiberaIdApplicativo(), ricercaLiberaLikeMode);
						idcorrRisp.and().ilike(Transazione.model().ID_CORRELAZIONE_APPLICATIVA_RISPOSTA, this.searchForm.getRicercaLiberaIdApplicativo(), ricercaLiberaLikeMode);
					}
					filter.or(idcorr, idcorrRisp);
				}
			}

			if (StringUtils.isNotBlank(this.searchForm.getNomeStato())) {
				// sb.append(" AND t.stato=:nome_stato ");
				filter.and().equals(Transazione.model().STATO,	this.searchForm.getNomeStato());
			}

			if (StringUtils.isNotBlank(this.searchForm.getNomeRisorsa()) ) {
				
				if(StringUtils.isBlank(this.searchForm.getValoreRisorsa())) {
					throw new Exception("Valore della Risorsa '"+this.searchForm.getNomeRisorsa()+"' non fornito");
				}
				
				// sb.append(" AND dm.idTransazione=t.id AND dc.nome=:nome_risorsa AND dc.valore=:valore_risorsa ");
				filter.and()
				.equals(Transazione.model().DUMP_MESSAGGIO.CONTENUTO.NOME,this.searchForm.getNomeRisorsa())
				.equals(Transazione.model().DUMP_MESSAGGIO.CONTENUTO.VALORE,this.searchForm.getValoreRisorsa());
			}

			// imposto il soggetto (loggato) come mittente o destinatario in
			// base alla tipologia di ricerca selezionata
			if (this.searchForm.getTipologiaRicercaEnum() == null || TipologiaRicerca.all.equals(this.searchForm.getTipologiaRicercaEnum())) {
				// il soggetto loggato puo essere mittente o destinatario
				// se e' selezionato "trafficoPerSoggetto" allora il nome del
				// soggetto selezionato va messo come complementare

				boolean trafficoSoggetto = StringUtils.isNotBlank(this.searchForm.getTrafficoPerSoggetto());

				if (trafficoSoggetto) {
					// il mio soggetto non e' stato impostato (soggetto in gestione, puo succedero solo in caso admin)
					// sb.append("AND ( (t.tipoSoggettoErogatore=:tipo_traffico_soggetto AND t.soggettoErogatore=:traffico_soggetto) OR (t.tipoSoggettoFruitore=:tipo_traffico_soggetto AND t.soggettoFruitore=:traffico_soggetto) ) ");
					// tipo e nome soggetto erogatore
					IExpression erogatore = this.transazioniSearchDAO.newExpression();

					erogatore.and()
					.equals(Transazione.model().NOME_SOGGETTO_EROGATORE, this.searchForm.getTrafficoPerSoggetto())
							.equals(Transazione.model().TIPO_SOGGETTO_EROGATORE, this.searchForm.getTipoTrafficoPerSoggetto());

					// tipo e nome fruitore
					IExpression fruitore = this.transazioniSearchDAO.newExpression();

					fruitore.and()
					.equals(Transazione.model().NOME_SOGGETTO_FRUITORE,	this.searchForm.getTrafficoPerSoggetto())
							.equals(Transazione.model().TIPO_SOGGETTO_FRUITORE, this.searchForm.getTipoTrafficoPerSoggetto());

					IExpression soggetti = this.transazioniSearchDAO.newExpression();

					soggetti.or(erogatore, fruitore);

					filter.and(soggetti);
				} else {
					// nessun filtro da impostare
					
					if(ricercaLibera) {
						if(StringUtils.isNotBlank(this.searchForm.getRicercaLiberaSoggettoRemoto()) && StringUtils.isNotBlank(this.searchForm.getRicercaLiberaSoggettoLocale()) ){
							
							String nomeSoggettoLocale = this.searchForm.getRicercaLiberaSoggettoLocale();
							String nomeSoggettoRemoto = this.searchForm.getRicercaLiberaSoggettoRemoto();
							
							IExpression erogazione = this.transazioniSearchDAO.newExpression();
							IExpression fruizione = this.transazioniSearchDAO.newExpression();
							if(ricercaLiberaCaseSensitive) {
								erogazione.and().
									like(Transazione.model().NOME_SOGGETTO_EROGATORE, nomeSoggettoLocale, ricercaLiberaLikeMode).
									like(Transazione.model().NOME_SOGGETTO_FRUITORE, nomeSoggettoRemoto, ricercaLiberaLikeMode);
								fruizione.and().
									like(Transazione.model().NOME_SOGGETTO_FRUITORE, nomeSoggettoLocale, ricercaLiberaLikeMode).
									like(Transazione.model().NOME_SOGGETTO_EROGATORE, nomeSoggettoRemoto, ricercaLiberaLikeMode);
							}
							else {
								erogazione.and().
									ilike(Transazione.model().NOME_SOGGETTO_EROGATORE, nomeSoggettoLocale, ricercaLiberaLikeMode).
									ilike(Transazione.model().NOME_SOGGETTO_FRUITORE, nomeSoggettoRemoto, ricercaLiberaLikeMode);
								fruizione.and().
									ilike(Transazione.model().NOME_SOGGETTO_FRUITORE, nomeSoggettoLocale, ricercaLiberaLikeMode).
									ilike(Transazione.model().NOME_SOGGETTO_EROGATORE, nomeSoggettoRemoto, ricercaLiberaLikeMode);
							}
							IExpression soggetti = this.transazioniSearchDAO.newExpression();
							soggetti.or(erogazione, fruizione);
							filter.and(soggetti);
							
						}
						else if(StringUtils.isNotBlank(this.searchForm.getRicercaLiberaSoggettoRemoto()) || StringUtils.isNotBlank(this.searchForm.getRicercaLiberaSoggettoLocale())){
							
							String nomeSoggetto = null;
							if(StringUtils.isNotBlank(this.searchForm.getRicercaLiberaSoggettoRemoto())) {
								nomeSoggetto = this.searchForm.getRicercaLiberaSoggettoRemoto();
							}
							else {
								nomeSoggetto = this.searchForm.getRicercaLiberaSoggettoLocale();
							}
							
							IExpression erogatore = this.transazioniSearchDAO.newExpression();
							IExpression fruitore = this.transazioniSearchDAO.newExpression();
							if(ricercaLiberaCaseSensitive) {
								erogatore.and().like(Transazione.model().NOME_SOGGETTO_EROGATORE, nomeSoggetto, ricercaLiberaLikeMode);
								fruitore.and().like(Transazione.model().NOME_SOGGETTO_FRUITORE, nomeSoggetto, ricercaLiberaLikeMode);
							}
							else {
								erogatore.and().ilike(Transazione.model().NOME_SOGGETTO_EROGATORE, nomeSoggetto, ricercaLiberaLikeMode);
								fruitore.and().ilike(Transazione.model().NOME_SOGGETTO_FRUITORE, nomeSoggetto, ricercaLiberaLikeMode);
							}
							IExpression soggetti = this.transazioniSearchDAO.newExpression();
							soggetti.or(erogatore, fruitore);
							filter.and(soggetti);
						}
					}
					
				}

			} else if (TipologiaRicerca.ingresso.equals(this.searchForm.getTipologiaRicercaEnum())) {
				// EROGAZIONE

				boolean setMittente = false;
				
				// il mittente puo non essere specifica
				if (StringUtils.isNotBlank(this.searchForm.getNomeMittente())) {
					// sb.append("AND t.soggettoFruitore = :nome_mittente ");
					filter.and().equals(Transazione.model().NOME_SOGGETTO_FRUITORE,	this.searchForm.getNomeMittente());
					setMittente = true;
				}

				if (StringUtils.isNotBlank(this.searchForm.getTipoMittente())) {
					// sb.append("AND t.soggettoFruitore = :nome_mittente ");
					filter.and().equals(Transazione.model().TIPO_SOGGETTO_FRUITORE,	this.searchForm.getTipoMittente());
					setMittente = true;
				}
				
				if(!setMittente && ricercaLibera && StringUtils.isNotBlank(this.searchForm.getRicercaLiberaSoggettoRemoto())) {
					if(ricercaLiberaCaseSensitive) {
						filter.and().like(Transazione.model().NOME_SOGGETTO_FRUITORE, this.searchForm.getRicercaLiberaSoggettoRemoto(), ricercaLiberaLikeMode);
					}
					else {
						filter.and().ilike(Transazione.model().NOME_SOGGETTO_FRUITORE, this.searchForm.getRicercaLiberaSoggettoRemoto(), ricercaLiberaLikeMode);
					}
				}
				
				if(ricercaLibera && StringUtils.isNotBlank(this.searchForm.getRicercaLiberaSoggettoLocale())) {
					if(ricercaLiberaCaseSensitive) {
						filter.and().like(Transazione.model().NOME_SOGGETTO_EROGATORE, this.searchForm.getRicercaLiberaSoggettoLocale(), ricercaLiberaLikeMode);
					}
					else {
						filter.and().ilike(Transazione.model().NOME_SOGGETTO_EROGATORE, this.searchForm.getRicercaLiberaSoggettoLocale(), ricercaLiberaLikeMode);
					}
				}

			} else {
				// FRUIZIONE

				boolean setDestinatario = false;
				
				// il destinatario puo nn essere specificato
				if (StringUtils.isNotBlank(this.searchForm.getNomeDestinatario())) {
					// sb.append("AND t.soggettoErogatore = :nome_destinatario ");
					filter.and().equals(Transazione.model().NOME_SOGGETTO_EROGATORE,this.searchForm.getNomeDestinatario());
					setDestinatario=true;
				}

				if (StringUtils.isNotBlank(this.searchForm.getTipoDestinatario())) {
					// sb.append("AND t.soggettoErogatore = :nome_destinatario ");
					filter.and().equals(Transazione.model().TIPO_SOGGETTO_EROGATORE,this.searchForm.getTipoDestinatario());
					setDestinatario=true;
				}
				
				if(!setDestinatario && ricercaLibera && StringUtils.isNotBlank(this.searchForm.getRicercaLiberaSoggettoRemoto())) {
					if(ricercaLiberaCaseSensitive) {
						filter.and().like(Transazione.model().NOME_SOGGETTO_EROGATORE, this.searchForm.getRicercaLiberaSoggettoRemoto(), ricercaLiberaLikeMode);
					}
					else {
						filter.and().ilike(Transazione.model().NOME_SOGGETTO_EROGATORE, this.searchForm.getRicercaLiberaSoggettoRemoto(), ricercaLiberaLikeMode);
					}
				}
				
				if(ricercaLibera && StringUtils.isNotBlank(this.searchForm.getRicercaLiberaSoggettoLocale())) {
					if(ricercaLiberaCaseSensitive) {
						filter.and().like(Transazione.model().NOME_SOGGETTO_FRUITORE, this.searchForm.getRicercaLiberaSoggettoLocale(), ricercaLiberaLikeMode);
					}
					else {
						filter.and().ilike(Transazione.model().NOME_SOGGETTO_FRUITORE, this.searchForm.getRicercaLiberaSoggettoLocale(), ricercaLiberaLikeMode);
					}
				}
				
			}

		}

		impostaFiltroDatiMittente(filter, this.transazioniSearchDAO, this.searchForm, Transazione.model(), isCount, isLiveSearch);
		
		
		
		// aggiungo la condizione sul protocollo se e' impostato e se e' presente piu' di un protocollo
		// protocollo e' impostato anche scegliendo la modalita'
		if (this.searchForm.isSetFiltroProtocollo()) {
			filter.and().equals(Transazione.model().PROTOCOLLO,	this.searchForm.getProtocollo());
		}

		if (StringUtils.isNotEmpty(this.searchForm.getEvento()) || StringUtils.isNotEmpty(this.searchForm.getCodiceRisposta())) {
			
			CredenzialeSearchEvento searchEventi = new CredenzialeSearchEvento();
			List<CredenzialeMittente> listaCredenzialiMittente = new ArrayList<CredenzialeMittente>();
			
			if (StringUtils.isNotEmpty(this.searchForm.getEvento())) {
				
				String evento = this.searchForm.getEvento();
				if(evento!=null) {
					// permetto di usare api=rest invece di api=1
					evento = evento.trim();
					if(evento.toLowerCase().startsWith(CostantiPdD.PREFIX_API.toLowerCase()) && evento.length()>CostantiPdD.PREFIX_API.length()) {
						try {
							String sub = evento.substring(CostantiPdD.PREFIX_API.length());
							if("rest".equalsIgnoreCase(sub)) {
								evento = CostantiPdD.PREFIX_API+TipoAPI.REST.getValoreAsInt();
							}
							else if("soap".equalsIgnoreCase(sub)) {
								evento = CostantiPdD.PREFIX_API+TipoAPI.SOAP.getValoreAsInt();
							}
						}catch(Throwable t) {}
					}
				}
				
				IPaginatedExpression pagExpr = searchEventi.createExpression(this.credenzialiMittenteDAO, evento, false, false);
				List<CredenzialeMittente> listaCredenzialiMittenteEvento = this.credenzialiMittenteDAO.findAll(pagExpr);
				if(listaCredenzialiMittenteEvento!=null && !listaCredenzialiMittenteEvento.isEmpty()) {
					listaCredenzialiMittente.addAll(listaCredenzialiMittenteEvento);
				}
			}
			
			if (StringUtils.isNotEmpty(this.searchForm.getCodiceRisposta())) {
				
				List<String> l = new ArrayList<String>();
				if(this.searchForm.getCodiceRisposta().contains(",")) {
					String [] tmp = this.searchForm.getCodiceRisposta().split(",");
					if(tmp!=null && tmp.length>0) {
						for (String v : tmp) {
							l.add(v.trim());
						}
					}
					else {
						l.add(this.searchForm.getCodiceRisposta());
					}
				}
				else {
					l.add(this.searchForm.getCodiceRisposta());
				}
				if(!l.isEmpty()) {
					for (String codice : l) {
						for (int i = 0; i < 2; i++) {
						
							String prefix = (i==0) ? CostantiPdD.PREFIX_HTTP_STATUS_CODE_OUT : CostantiPdD.PREFIX_HTTP_STATUS_CODE_IN;
							
							String searchCodice = prefix+codice;
							IPaginatedExpression pagExpr = searchEventi.createExpression(this.credenzialiMittenteDAO, searchCodice, false, false);
							List<CredenzialeMittente> listaCredenzialiMittenteCodice = this.credenzialiMittenteDAO.findAll(pagExpr);
							if(listaCredenzialiMittenteCodice!=null && !listaCredenzialiMittenteCodice.isEmpty()) {
								listaCredenzialiMittente.addAll(listaCredenzialiMittenteCodice);
							}
							
						}
					}
				}
				

			}
			
			addListaCredenzialiMittente(filter, listaCredenzialiMittente, Transazione.model());

		}
		
		if (StringUtils.isNotEmpty(this.searchForm.getGruppo())) {
			
			CredenzialeSearchGruppo searchGruppi = new CredenzialeSearchGruppo();
			IPaginatedExpression pagExpr = searchGruppi.createExpression(this.credenzialiMittenteDAO, this.searchForm.getGruppo(), true, true);
			List<CredenzialeMittente> listaCredenzialiMittente = this.credenzialiMittenteDAO.findAll(pagExpr);
			addListaCredenzialiMittente(filter, listaCredenzialiMittente, Transazione.model());

		}
		else {
			
			if(ricercaLibera && StringUtils.isNotBlank(this.searchForm.getRicercaLiberaGruppo())) {
				CredenzialeSearchGruppo searchGruppi = new CredenzialeSearchGruppo();
				IPaginatedExpression pagExpr = searchGruppi.createExpression(this.credenzialiMittenteDAO, this.searchForm.getRicercaLiberaGruppo(), ricercaLiberaEsatta, ricercaLiberaCaseSensitive);
				List<CredenzialeMittente> listaCredenzialiMittente = this.credenzialiMittenteDAO.findAll(pagExpr);
				addListaCredenzialiMittente(filter, listaCredenzialiMittente, Transazione.model());
			}
			
		}
		
		if (StringUtils.isNotEmpty(this.searchForm.getApi())) {
			
			CredenzialeSearchApi searchApi = new CredenzialeSearchApi();
			IPaginatedExpression pagExpr = searchApi.createExpression(this.credenzialiMittenteDAO, this.searchForm.getApi(), true, true);
			List<CredenzialeMittente> listaCredenzialiMittente = this.credenzialiMittenteDAO.findAll(pagExpr);
			addListaCredenzialiMittente(filter, listaCredenzialiMittente, Transazione.model());

		}
		else {
			
			if(ricercaLibera && StringUtils.isNotBlank(this.searchForm.getRicercaLiberaApi())) {
				CredenzialeSearchApi searchApi = new CredenzialeSearchApi();
				IPaginatedExpression pagExpr = searchApi.createExpression(this.credenzialiMittenteDAO, this.searchForm.getRicercaLiberaApi(), ricercaLiberaEsatta, ricercaLiberaCaseSensitive);
				List<CredenzialeMittente> listaCredenzialiMittente = this.credenzialiMittenteDAO.findAll(pagExpr);
				addListaCredenzialiMittente(filter, listaCredenzialiMittente, Transazione.model());
			}
			
		}
		
		if (StringUtils.isNotEmpty(this.searchForm.getClusterId())) {
			
			filter.and().equals(Transazione.model().CLUSTER_ID,	this.searchForm.getClusterId().trim());
			
		}
		else if (StringUtils.isNotEmpty(this.searchForm.getCanale())) {
			
			List<String> listId = this.searchForm.getIdClusterByCanale(this.searchForm.getCanale());
			if(listId!=null && !listId.isEmpty()) {
				filter.and().in(Transazione.model().CLUSTER_ID, listId);
			}
			else {
				filter.and().equals(Transazione.model().CLUSTER_ID,	"--"); // non esistente volutamente
			}
		}
		
		//Ricerche personalizzate
		IFilter filtro = this.searchForm.getFiltro();		
		if(filtro != null){
			FilterImpl f = (FilterImpl) filtro;
			filter.and(f.getExpression());
		}

	}
	private boolean isRicercaLibera() {
		if(!this.searchForm.isLive() && StringUtils.isNotEmpty(this.searchForm.getModalitaRicercaStorico())) {
			ModalitaRicercaTransazioni t = ModalitaRicercaTransazioni.getFromString(this.searchForm.getModalitaRicercaStorico());
			return ModalitaRicercaTransazioni.RICERCA_LIBERA.equals(t);	
		}
		return false;
	}
	private boolean isRicercaLiberaEsatta() {
		String r = this.searchForm.getRicercaLiberaMatchingType();
		if(r!=null && !"".equals(r)) {
			TipoMatch tipo = TipoMatch.valueOf(r);
			return TipoMatch.EQUALS.equals(tipo);
		}
		return false;
	}
	private boolean isRicercaLiberaCaseSensitive() {
		String r = this.searchForm.getRicercaLiberaCaseSensitiveType();
		if(r!=null && !"".equals(r)) {
			CaseSensitiveMatch tipo = CaseSensitiveMatch.valueOf(r);
			return CaseSensitiveMatch.SENSITIVE.equals(tipo);
		}
		return false;
	}
	private LikeMode getRicercaLiberaEsattaLikeMode() {
		return this.isRicercaLiberaEsatta() ? LikeMode.EXACT : LikeMode.ANYWHERE;
	}
	private void impostaFiltroDatiMittente(IExpression filter, ITransazioneServiceSearch transazioniSearchDAO , BaseSearchForm searchForm, TransazioneModel model, boolean isCount, boolean isLiveSearch)
			throws ServiceException, NotImplementedException, ExpressionNotImplementedException, ExpressionException {
		// credenziali mittente
		if(StringUtils.isNotEmpty(searchForm.getRiconoscimento())) {
			if(searchForm.getRiconoscimento().equals(org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_SOGGETTO)) {
				// nop; e' gia' stato impostato il soggetto tramite il filtro apposito
				
				boolean soggettoDefined = StringUtils.isNotBlank(this.searchForm.getNomeMittente()) && StringUtils.isNotBlank(this.searchForm.getTipoMittente());
				if(!soggettoDefined) {
					filter.and().equals(model.ID_TRANSAZIONE, "-1"); // Serve a non far tornare transazioni, visto che viene segnalato l'errore
				}
				
			}
			else if(searchForm.getRiconoscimento().equals(org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_APPLICATIVO)) {
				if (StringUtils.isNotBlank(searchForm.getServizioApplicativo())) {
					// sb.append("AND t.servizioApplicativo = :servizio_applicativo ");
					IExpression saOr = transazioniSearchDAO.newExpression();
					saOr.equals(model.SERVIZIO_APPLICATIVO_FRUITORE,	searchForm.getServizioApplicativo());
//					saOr.or();
//					saOr.equals(model.SERVIZIO_APPLICATIVO_EROGATORE,	searchForm.getServizioApplicativo());
					filter.and(saOr);
				}
				else {
					filter.and().equals(model.ID_TRANSAZIONE, "-1"); // Serve a non far tornare transazioni, visto che viene segnalato l'errore
				}
			} else {
				List<CredenzialeMittente> listaCredenzialiMittente = getIdCredenzialiFromFilter(searchForm, this.credenzialiMittenteDAO, isCount, isLiveSearch);
				addListaCredenzialiMittente(filter, listaCredenzialiMittente, model);
			}
		}
		else {
			if(this.isRicercaLibera()) {
				
				boolean ricercaLiberaCaseSensitive = isRicercaLiberaCaseSensitive();
				boolean ricercaLiberaEsatta = isRicercaLiberaEsatta();
				LikeMode ricercaLiberaLikeMode = getRicercaLiberaEsattaLikeMode();
				
				if(StringUtils.isNotBlank(this.searchForm.getRicercaLiberaApplicativo())) {
					if(ricercaLiberaCaseSensitive) {
						filter.and().like(Transazione.model().SERVIZIO_APPLICATIVO_FRUITORE, this.searchForm.getRicercaLiberaApplicativo(), ricercaLiberaLikeMode);
					}
					else {
						filter.and().ilike(Transazione.model().SERVIZIO_APPLICATIVO_FRUITORE, this.searchForm.getRicercaLiberaApplicativo(), ricercaLiberaLikeMode);
					}
				}
				
				if(StringUtils.isNotBlank(this.searchForm.getRicercaLiberaIdentificativoAutenticato())) {
					
					try {
					
						List<CredenzialeMittente> allList = new ArrayList<>();
						
						CredenzialeSearchTrasporto searchCredenzialiBasic = new CredenzialeSearchTrasporto(TipoAutenticazione.BASIC.getValue());
						IPaginatedExpression pagExprBasic = searchCredenzialiBasic.createExpression(this.credenzialiMittenteDAO, this.searchForm.getRicercaLiberaIdentificativoAutenticato(), ricercaLiberaEsatta, ricercaLiberaCaseSensitive);
						List<CredenzialeMittente> listaCredenzialiMittenteBasic = this.credenzialiMittenteDAO.findAll(pagExprBasic);
						if(listaCredenzialiMittenteBasic!=null && listaCredenzialiMittenteBasic.size()>0) {
							allList.addAll(listaCredenzialiMittenteBasic);
						}
						
						CredenzialeSearchTrasporto searchCredenzialiSsl = new CredenzialeSearchTrasporto(TipoAutenticazione.SSL.getValue());
						IPaginatedExpression pagExprSsl = searchCredenzialiSsl.createExpression(this.credenzialiMittenteDAO, this.searchForm.getRicercaLiberaIdentificativoAutenticato(), ricercaLiberaEsatta, ricercaLiberaCaseSensitive);
						List<CredenzialeMittente> listaCredenzialiMittenteSsl = this.credenzialiMittenteDAO.findAll(pagExprSsl);
						if(listaCredenzialiMittenteSsl!=null && listaCredenzialiMittenteSsl.size()>0) {
							allList.addAll(listaCredenzialiMittenteSsl);
						}
						
						CredenzialeSearchTrasporto searchCredenzialiPrincipal = new CredenzialeSearchTrasporto(TipoAutenticazione.PRINCIPAL.getValue());
						IPaginatedExpression pagExprPrincipal = searchCredenzialiPrincipal.createExpression(this.credenzialiMittenteDAO, this.searchForm.getRicercaLiberaIdentificativoAutenticato(), ricercaLiberaEsatta, ricercaLiberaCaseSensitive);
						List<CredenzialeMittente> listaCredenzialiMittentePrincipal = this.credenzialiMittenteDAO.findAll(pagExprPrincipal);
						if(listaCredenzialiMittentePrincipal!=null && listaCredenzialiMittentePrincipal.size()>0) {
							allList.addAll(listaCredenzialiMittentePrincipal);
						}
						
						//if(allList!=null && allList.size()>0) { fix: va chiamato per generare l'id_transazione=-1
						addListaCredenzialiMittente(filter, allList, Transazione.model());
						//}
						
					}catch(Exception e) {
						throw new ServiceException(e.getMessage(),e);
					}
					
				}
				
				if(StringUtils.isNotBlank(this.searchForm.getRicercaLiberaIndirizzoIP())) {
					try {
						CredenzialeSearchClientAddress searchClientAddress = new CredenzialeSearchClientAddress(true, true, false);
						IPaginatedExpression pagExpr = searchClientAddress.createExpression(this.credenzialiMittenteDAO, this.searchForm.getRicercaLiberaIndirizzoIP(), ricercaLiberaEsatta, ricercaLiberaCaseSensitive);
						List<CredenzialeMittente> listaCredenzialiMittente = this.credenzialiMittenteDAO.findAll(pagExpr);
						addListaCredenzialiMittente(filter, listaCredenzialiMittente, Transazione.model());
					}catch(Exception e) {
						throw new ServiceException(e.getMessage(),e);
					}
				}
				
				if(StringUtils.isNotBlank(this.searchForm.getRicercaLiberaTokenIssuer())) {
					try {
						CredenzialeSearchToken searchToken = new CredenzialeSearchToken(TipoCredenzialeMittente.token_issuer);
						IPaginatedExpression pagExpr = searchToken.createExpression(this.credenzialiMittenteDAO, this.searchForm.getRicercaLiberaTokenIssuer(), ricercaLiberaEsatta, ricercaLiberaCaseSensitive);
						List<CredenzialeMittente> listaCredenzialiMittente = this.credenzialiMittenteDAO.findAll(pagExpr);
						addListaCredenzialiMittente(filter, listaCredenzialiMittente, Transazione.model());
					}catch(Exception e) {
						throw new ServiceException(e.getMessage(),e);
					}
				}
				if(StringUtils.isNotBlank(this.searchForm.getRicercaLiberaTokenSubject())) {
					try {
						CredenzialeSearchToken searchToken = new CredenzialeSearchToken(TipoCredenzialeMittente.token_subject);
						IPaginatedExpression pagExpr = searchToken.createExpression(this.credenzialiMittenteDAO, this.searchForm.getRicercaLiberaTokenSubject(), ricercaLiberaEsatta, ricercaLiberaCaseSensitive);
						List<CredenzialeMittente> listaCredenzialiMittente = this.credenzialiMittenteDAO.findAll(pagExpr);
						addListaCredenzialiMittente(filter, listaCredenzialiMittente, Transazione.model());
					}catch(Exception e) {
						throw new ServiceException(e.getMessage(),e);
					}
				}
				if(StringUtils.isNotBlank(this.searchForm.getRicercaLiberaTokenClientID())) {
					try {
						CredenzialeSearchToken searchToken = new CredenzialeSearchToken(TipoCredenzialeMittente.token_clientId);
						IPaginatedExpression pagExpr = searchToken.createExpression(this.credenzialiMittenteDAO, this.searchForm.getRicercaLiberaTokenClientID(), ricercaLiberaEsatta, ricercaLiberaCaseSensitive);
						List<CredenzialeMittente> listaCredenzialiMittente = this.credenzialiMittenteDAO.findAll(pagExpr);
						addListaCredenzialiMittente(filter, listaCredenzialiMittente, Transazione.model());
					}catch(Exception e) {
						throw new ServiceException(e.getMessage(),e);
					}
				}
				if(StringUtils.isNotBlank(this.searchForm.getRicercaLiberaTokenUsername())) {
					try {
						CredenzialeSearchToken searchToken = new CredenzialeSearchToken(TipoCredenzialeMittente.token_username);
						IPaginatedExpression pagExpr = searchToken.createExpression(this.credenzialiMittenteDAO, this.searchForm.getRicercaLiberaTokenUsername(), ricercaLiberaEsatta, ricercaLiberaCaseSensitive);
						List<CredenzialeMittente> listaCredenzialiMittente = this.credenzialiMittenteDAO.findAll(pagExpr);
						addListaCredenzialiMittente(filter, listaCredenzialiMittente, Transazione.model());
					}catch(Exception e) {
						throw new ServiceException(e.getMessage(),e);
					}
				}
				if(StringUtils.isNotBlank(this.searchForm.getRicercaLiberaTokenEmail())) {
					try {
						CredenzialeSearchToken searchToken = new CredenzialeSearchToken(TipoCredenzialeMittente.token_eMail);
						IPaginatedExpression pagExpr = searchToken.createExpression(this.credenzialiMittenteDAO, this.searchForm.getRicercaLiberaTokenEmail(), ricercaLiberaEsatta, ricercaLiberaCaseSensitive);
						List<CredenzialeMittente> listaCredenzialiMittente = this.credenzialiMittenteDAO.findAll(pagExpr);
						addListaCredenzialiMittente(filter, listaCredenzialiMittente, Transazione.model());
					}catch(Exception e) {
						throw new ServiceException(e.getMessage(),e);
					}
				}
			}
		}
	}
	private IField getCredenzialeField(CredenzialeMittente credenzialeMittente, TransazioneModel model) {
		IField fieldCredenziale = null;
		String credenzialeTipo = credenzialeMittente.getTipo();
		if(credenzialeTipo.startsWith(org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_CREDENZIALE_TRASPORTO_PREFIX)) {
			fieldCredenziale = model.TRASPORTO_MITTENTE;
		} else {
			TipoCredenzialeMittente tcm = TipoCredenzialeMittente.valueOf(credenzialeTipo);
			
			switch (tcm) {
			case token_clientId:
				fieldCredenziale = model.TOKEN_CLIENT_ID;
				break;
			case token_eMail:
				fieldCredenziale = model.TOKEN_MAIL;
				break;
			case token_issuer:
				fieldCredenziale = model.TOKEN_ISSUER;
				break;
			case token_subject:
				fieldCredenziale = model.TOKEN_SUBJECT;
				break;
			case token_username:
				fieldCredenziale = model.TOKEN_USERNAME;
				break;
				
			case client_address:
				fieldCredenziale = model.CLIENT_ADDRESS;
				break;
				
			case eventi:
				fieldCredenziale = model.EVENTI_GESTIONE;
				break;
			case gruppi:
				fieldCredenziale = model.GRUPPI;
				break;
			case api:
				fieldCredenziale = model.URI_API;
				break;
				
			case trasporto:
				// caso impossibile
				break; 
			}
		}
		
		return fieldCredenziale;
	}
	
	
	private List<CredenzialeMittente> getIdCredenzialiFromFilter(BaseSearchForm searchForm, ICredenzialeMittenteService credenzialeMittentiService, boolean isCount, boolean isLiveSearch) {
		List<CredenzialeMittente> findAll = new ArrayList<>();
		
		try {
			CaseSensitiveMatch caseSensitiveMatch = CaseSensitiveMatch.valueOf(searchForm.getMittenteCaseSensitiveType());
			TipoMatch match = TipoMatch.valueOf(searchForm.getMittenteMatchingType());
			boolean ricercaEsatta = TipoMatch.EQUALS.equals(match);
			boolean caseSensitive = CaseSensitiveMatch.SENSITIVE.equals(caseSensitiveMatch);
			 
			IPaginatedExpression pagExpr = null;
			boolean ricercaIdentificatoAutenticatoSsl = false;
			if(searchForm.getRiconoscimento().equals(org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_IDENTIFICATIVO_AUTENTICATO)) {
				CredenzialeSearchTrasporto searchTrasporto = new CredenzialeSearchTrasporto(searchForm.getAutenticazione());
				pagExpr = searchTrasporto.createExpression(credenzialeMittentiService, searchForm.getValoreRiconoscimento(), ricercaEsatta, caseSensitive);
				ricercaIdentificatoAutenticatoSsl = searchTrasporto.isSsl();
			} 
			
			if(searchForm.getRiconoscimento().equals(org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_INDIRIZZO_IP)) {
				
				caseSensitive = true;
				boolean socketAddress = false;
				boolean trasportAddress = false;
				boolean and = false;
				if(ricercaEsatta) {
					if(Costanti.VALUE_CLIENT_ADDRESS_SOCKET.equals(searchForm.getClientAddressMode())) {
						socketAddress = true;
					}
					else if(Costanti.VALUE_CLIENT_ADDRESS_TRASPORTO.equals(searchForm.getClientAddressMode())) {
						trasportAddress = true;
					}
					else {
						socketAddress = true;
						trasportAddress = true;
					}
				}
				
				CredenzialeSearchClientAddress searchClientAddress = new CredenzialeSearchClientAddress(socketAddress, trasportAddress, and);
				pagExpr = searchClientAddress.createExpression(credenzialeMittentiService, searchForm.getValoreRiconoscimento(), ricercaEsatta, caseSensitive);
			} 
			
			if(searchForm.getRiconoscimento().equals(org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_TOKEN_INFO)) {
				TipoCredenzialeMittente tcm = TipoCredenzialeMittente.valueOf(searchForm.getTokenClaim());
				CredenzialeSearchToken searchToken = new CredenzialeSearchToken(tcm);
				pagExpr = searchToken.createExpression(credenzialeMittentiService, searchForm.getValoreRiconoscimento(), ricercaEsatta, caseSensitive);
			}
			
			findAll = credenzialeMittentiService.findAll(pagExpr);
			
			if(ricercaIdentificatoAutenticatoSsl && ricercaEsatta && findAll!=null && !findAll.isEmpty()) {
				findAll = CredenzialeSearchTrasporto.filterList(findAll, searchForm.getValoreRiconoscimento(), this.log);
			}
			
		}catch(ServiceException e) {
			this.log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			this.log.error(e.getMessage(), e);
		} catch (UtilsException e) {
			this.log.error(e.getMessage(), e);
		}
		return findAll;
	}
	
	private void addListaCredenzialiMittente(IExpression filter, List<CredenzialeMittente> listaCredenzialiMittente, TransazioneModel model) throws ExpressionNotImplementedException, ExpressionException {
		// se non ho trovato credenziali che corrispondono a quelle inserite allora restituisco un elenco di transazioni vuoto forzando l'id transazione
		if(listaCredenzialiMittente.size() ==0) {
			filter.and().equals(model.ID_TRANSAZIONE, "-1");
		} else {
			CredenzialeMittente credenzialeMittente = listaCredenzialiMittente.get(0);
			IField fieldCredenziale = getCredenzialeField(credenzialeMittente, model);
			
			if(listaCredenzialiMittente.size() ==1) {
				filter.and().equals(fieldCredenziale, credenzialeMittente.getId().toString());
				
			} else {
				List<String> ids = new ArrayList<>();
				for (CredenzialeMittente cMittente : listaCredenzialiMittente) {
					ids.add(cMittente.getId().toString());
				}
				
				filter.and().in(fieldCredenziale, ids); 
			}
		}
	}
}
