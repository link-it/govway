/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 * 
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it).
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
import org.openspcoop2.core.transazioni.constants.TipoMessaggio;
import org.openspcoop2.core.transazioni.dao.ICredenzialeMittenteService;
import org.openspcoop2.core.transazioni.dao.IDBDumpMessaggioServiceSearch;
import org.openspcoop2.core.transazioni.dao.ITransazioneServiceSearch;
import org.openspcoop2.core.transazioni.dao.jdbc.JDBCCredenzialeMittenteServiceSearch;
import org.openspcoop2.core.transazioni.model.TransazioneModel;
import org.openspcoop2.core.transazioni.utils.CredenzialiMittenteUtils;
import org.openspcoop2.core.transazioni.utils.TipoCredenzialeMittente;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.NonNegativeNumber;
import org.openspcoop2.generic_project.beans.UnixTimestampIntervalField;
import org.openspcoop2.generic_project.dao.IDBServiceUtilities;
import org.openspcoop2.generic_project.exception.ExpressionException;
import org.openspcoop2.generic_project.exception.ExpressionNotImplementedException;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.generic_project.exception.NotImplementedException;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.generic_project.expression.IExpression;
import org.openspcoop2.generic_project.expression.IPaginatedExpression;
import org.openspcoop2.generic_project.expression.Index;
import org.openspcoop2.generic_project.expression.LikeMode;
import org.openspcoop2.generic_project.expression.SortOrder;
import org.openspcoop2.generic_project.expression.impl.sql.ISQLFieldConverter;
import org.openspcoop2.monitor.engine.condition.EsitoUtils;
import org.openspcoop2.monitor.engine.condition.FilterImpl;
import org.openspcoop2.monitor.engine.config.BasicServiceLibrary;
import org.openspcoop2.monitor.engine.config.BasicServiceLibraryReader;
import org.openspcoop2.monitor.engine.config.SearchServiceLibrary;
import org.openspcoop2.monitor.engine.config.SearchServiceLibraryReader;
import org.openspcoop2.monitor.engine.config.TransactionServiceLibrary;
import org.openspcoop2.monitor.engine.config.TransactionServiceLibraryReader;
import org.openspcoop2.monitor.engine.config.base.ConfigurazioneServizioAzione;
import org.openspcoop2.monitor.engine.config.base.dao.IConfigurazioneServizioAzioneServiceSearch;
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
import org.openspcoop2.web.monitor.core.datamodel.ResLive;
import org.openspcoop2.web.monitor.core.dynamic.DynamicComponentUtils;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;
import org.openspcoop2.web.monitor.core.utils.ParseUtility;
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
	private org.openspcoop2.monitor.engine.config.base.dao.IServiceManager basePluginsServiceManager;

	private org.openspcoop2.core.transazioni.dao.IServiceManager transazioniServiceManager;
	private org.openspcoop2.core.transazioni.dao.ITransazioneService transazioniDAO;
	private org.openspcoop2.core.transazioni.dao.ITransazioneServiceSearch transazioniSearchDAO;
	private org.openspcoop2.core.transazioni.dao.ICredenzialeMittenteService credenzialiMittenteDAO;

	private org.openspcoop2.core.transazioni.dao.IDumpMessaggioServiceSearch dumpMessaggioSearchDAO;
	
	private org.openspcoop2.core.commons.search.dao.IServiceManager utilsServiceManager;

	private ISQLFieldConverter transazioniFieldConverter = null;
	
	private BasicServiceLibraryReader basicServiceLibraryReader = null;
	private TransactionServiceLibraryReader transactionServiceLibraryReader = null;
	private SearchServiceLibraryReader searchServiceLibraryReader = null;
	
	private Integer liveUltimiGiorni = null;
	
	private List<Index> forceIndexAndamentoTemporaleFindAll;
	private List<Index> forceIndexAndamentoTemporaleCount;
	private List<Index> forceIndexIdApplicativoFindAll;
	private List<Index> forceIndexIdApplicativoCount;
	private List<Index> forceIndexIdMessaggioRichiestaFindAll;
	private List<Index> forceIndexIdMessaggioRichiestaCount;
	private List<Index> forceIndexIdMessaggioRispostaFindAll;
	private List<Index> forceIndexIdMessaggioRispostaCount;
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
					(org.openspcoop2.monitor.engine.config.base.dao.IServiceManager) this.daoFactory.getServiceManager(org.openspcoop2.monitor.engine.config.base.utils.ProjectInfo.getInstance(),this.log);
			this.confSerAzSearchDAO = this.basePluginsServiceManager
					.getConfigurazioneServizioAzioneServiceSearch();

			this.transazioniServiceManager = 
					(org.openspcoop2.core.transazioni.dao.IServiceManager) this.daoFactory.getServiceManager(org.openspcoop2.core.transazioni.utils.ProjectInfo.getInstance(),this.log);

			this.transazioniSearchDAO = this.transazioniServiceManager.getTransazioneServiceSearch();
			this.transazioniDAO = this.transazioniServiceManager.getTransazioneService();
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
			
			this.initForceIndex(monitorProperties);
			
		} catch (Exception e) {
			this.log.error(e.getMessage(), e);
		}

		this.liveMaxResults = 50;
	}

	public TransazioniService(Connection con, boolean autoCommit, Logger log) {
		this.log =  log;
		try {

			this.daoFactory = DAOFactory.getInstance(this.log);
			
			// init Service Manager (Transazioni.plugins)
			this.transazioniPluginsServiceManager = 
					(org.openspcoop2.monitor.engine.config.transazioni.dao.IServiceManager) this.daoFactory.getServiceManager(org.openspcoop2.monitor.engine.config.transazioni.utils.ProjectInfo.getInstance(),con,autoCommit,this.log);
			this.confTransazioneSearchDAO = this.transazioniPluginsServiceManager
					.getConfigurazioneTransazioneServiceSearch();

			// init Service Manager (ricerche.plugins)
			this.ricerchePluginsServiceManager = 
					(org.openspcoop2.monitor.engine.config.ricerche.dao.IServiceManager) this.daoFactory.getServiceManager(org.openspcoop2.monitor.engine.config.ricerche.utils.ProjectInfo.getInstance(),con,autoCommit,this.log);

			// init Service Manager (base.plugins)
			this.basePluginsServiceManager = 
					(org.openspcoop2.monitor.engine.config.base.dao.IServiceManager) this.daoFactory.getServiceManager(org.openspcoop2.monitor.engine.config.base.utils.ProjectInfo.getInstance(),con,autoCommit,this.log);
			this.confSerAzSearchDAO = this.basePluginsServiceManager
					.getConfigurazioneServizioAzioneServiceSearch();

			this.transazioniServiceManager = 
					(org.openspcoop2.core.transazioni.dao.IServiceManager) this.daoFactory.getServiceManager(org.openspcoop2.core.transazioni.utils.ProjectInfo.getInstance(),con,autoCommit,this.log);

			this.transazioniSearchDAO = this.transazioniServiceManager.getTransazioneServiceSearch();
			this.transazioniDAO = this.transazioniServiceManager.getTransazioneService();
			this.credenzialiMittenteDAO = this.transazioniServiceManager.getCredenzialeMittenteService();
			this.dumpMessaggioSearchDAO = this.transazioniServiceManager.getDumpMessaggioServiceSearch();

			this.transazioniFieldConverter = ((IDBServiceUtilities<?>)this.transazioniSearchDAO).getFieldConverter(); 

			this.utilsServiceManager = 
					(org.openspcoop2.core.commons.search.dao.IServiceManager) this.daoFactory.getServiceManager(org.openspcoop2.core.commons.search.utils.ProjectInfo.getInstance(), con,autoCommit,this.log);
			
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
			
			this.initForceIndex(monitorProperties);
			
		} catch (Exception e) {
			this.log.error(e.getMessage(), e);
		}

		this.liveMaxResults = 50;
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
			
			List<Transazione> list = this.transazioniSearchDAO.findAll(pagExpr);
			if(list!= null && list.size() > 0)
				for (Transazione transazione : list) {
					TransazioneBean bean = new TransazioneBean(transazione);
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
			
			List<Transazione> list = this.transazioniSearchDAO.findAll(pagExpr);
			if(list!= null && list.size() > 0)
				for (Transazione transazione : list) {
					TransazioneBean bean = new TransazioneBean(transazione);
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
			
			List<Transazione> list = this.transazioniSearchDAO.findAll(pagExpr);
			if(list!= null && list.size() > 0)
				for (Transazione transazione : list) {
					TransazioneBean bean = new TransazioneBean(transazione);
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
			
			List<Transazione> list = this.transazioniSearchDAO.findAll(pagExpr);

			if(list!= null && list.size() > 0)
				for (Transazione transazione : list) {
					TransazioneBean bean = new TransazioneBean(transazione);
					listaBean.add(bean);
				}

		} catch (Exception e) {
			this.log.error(e.getMessage(), e);
		}

		return listaBean;
	}

	@Override
	public ResLive getEsitiInfoLive(PermessiUtenteOperatore permessiUtente, Date lastDatePick, String protocollo) {

		this.log.debug("Get Esiti Info Live[idPorta: " + permessiUtente+ "], [ LastDatePick: " + lastDatePick + "]");

		try {
			
			
			EsitiProperties esitiProperties = EsitiProperties.getInstance(this.log, protocollo);
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

		return new ResLive(new Long("0"), new Long("0"), new Long("0"));
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
			TransazioneBean transazioneBean = new TransazioneBean(t);
			
			// Integrazione dei dati delle credenziali
			// Trasporto Mittente
			String trasportoMittente = t.getTrasportoMittente();
			if(StringUtils.isNotEmpty(trasportoMittente)) {
				try {
					CredenzialeMittente credenzialeMittente = ((JDBCCredenzialeMittenteServiceSearch)this.credenzialiMittenteDAO).get(Long.parseLong(trasportoMittente));
					transazioneBean.setTrasportoMittenteLabel(credenzialeMittente.getCredenziale()); 
				} catch(NumberFormatException e) {
					// informazione non valida
					transazioneBean.setTrasportoMittenteLabel(Costanti.LABEL_INFORMAZIONE_NON_DISPONIBILE); 
				} catch(NotFoundException e) {
					// informazione non piu disponibile
					transazioneBean.setTrasportoMittenteLabel(Costanti.LABEL_INFORMAZIONE_NON_PIU_PRESENTE); 
				}
			}
			
			// Token Issuer
			String tokenIssuer = t.getTokenIssuer();
			if(StringUtils.isNotEmpty(tokenIssuer)) {
				try {
					CredenzialeMittente credenzialeMittente = ((JDBCCredenzialeMittenteServiceSearch)this.credenzialiMittenteDAO).get(Long.parseLong(tokenIssuer));
					transazioneBean.setTokenIssuerLabel(credenzialeMittente.getCredenziale()); 
				} catch(NumberFormatException e) {
					// informazione non valida
					transazioneBean.setTokenIssuerLabel(Costanti.LABEL_INFORMAZIONE_NON_DISPONIBILE); 
				} catch(NotFoundException e) {
					// informazione non piu disponibile
					transazioneBean.setTokenIssuerLabel(Costanti.LABEL_INFORMAZIONE_NON_PIU_PRESENTE); 
				}
			}
			
			// Token Client ID
			String tokenClientID = t.getTokenClientId();
			if(StringUtils.isNotEmpty(tokenClientID)) {
				try {
					CredenzialeMittente credenzialeMittente = ((JDBCCredenzialeMittenteServiceSearch)this.credenzialiMittenteDAO).get(Long.parseLong(tokenClientID));
					transazioneBean.setTokenClientIdLabel(credenzialeMittente.getCredenziale()); 
				} catch(NumberFormatException e) {
					// informazione non valida
					transazioneBean.setTokenClientIdLabel(Costanti.LABEL_INFORMAZIONE_NON_DISPONIBILE); 
				} catch(NotFoundException e) {
					// informazione non piu disponibile
					transazioneBean.setTokenClientIdLabel(Costanti.LABEL_INFORMAZIONE_NON_PIU_PRESENTE); 
				}
			}
			
			// Token Subject
			String tokenSubject = t.getTokenSubject();
			if(StringUtils.isNotEmpty(tokenSubject)) {
				try {
					CredenzialeMittente credenzialeMittente = ((JDBCCredenzialeMittenteServiceSearch)this.credenzialiMittenteDAO).get(Long.parseLong(tokenSubject));
					transazioneBean.setTokenSubjectLabel(credenzialeMittente.getCredenziale()); 
				} catch(NumberFormatException e) {
					// informazione non valida
					transazioneBean.setTokenSubjectLabel(Costanti.LABEL_INFORMAZIONE_NON_DISPONIBILE); 
				} catch(NotFoundException e) {
					// informazione non piu disponibile
					transazioneBean.setTokenSubjectLabel(Costanti.LABEL_INFORMAZIONE_NON_PIU_PRESENTE); 
				}
			}
			
			// Token Username
			String tokenUsername = t.getTokenUsername();
			if(StringUtils.isNotEmpty(tokenUsername)) {
				try {
					CredenzialeMittente credenzialeMittente = ((JDBCCredenzialeMittenteServiceSearch)this.credenzialiMittenteDAO).get(Long.parseLong(tokenUsername));
					transazioneBean.setTokenUsernameLabel(credenzialeMittente.getCredenziale()); 
				} catch(NumberFormatException e) {
					// informazione non valida
					transazioneBean.setTokenUsernameLabel(Costanti.LABEL_INFORMAZIONE_NON_DISPONIBILE); 
				} catch(NotFoundException e) {
					// informazione non piu disponibile
					transazioneBean.setTokenUsernameLabel(Costanti.LABEL_INFORMAZIONE_NON_PIU_PRESENTE); 
				}
			}
			
			// Token Mail
			String tokenMail = t.getTokenMail();
			if(StringUtils.isNotEmpty(tokenMail)) {
				try {
					CredenzialeMittente credenzialeMittente = ((JDBCCredenzialeMittenteServiceSearch)this.credenzialiMittenteDAO).get(Long.parseLong(tokenMail));
					transazioneBean.setTokenMailLabel(credenzialeMittente.getCredenziale()); 
				} catch(NumberFormatException e) {
					// informazione non valida
					transazioneBean.setTokenMailLabel(Costanti.LABEL_INFORMAZIONE_NON_DISPONIBILE); 
				} catch(NotFoundException e) {
					// informazione non piu disponibile
					transazioneBean.setTokenMailLabel(Costanti.LABEL_INFORMAZIONE_NON_PIU_PRESENTE); 
				}
			}
			
			return transazioneBean; 
		} catch (NotFoundException nre) {
			// ignore
		} catch (Exception e) {
			this.log.error("Impossibile recuperare La Transazione con idTransazione: "	+ idTransazione, e);
			throw new Exception("Impossibile recuperare La Transazione con idTransazione: "	+ idTransazione, e);
		}
		return null;
	}

	private static long virtualIdRequest = -999l;
	private static long virtualIdResponse = -888l;
	private DumpMessaggio createVirtualMessageWithSdk(String idTransazione, TipoMessaggio tipoMessaggio) throws Exception{
		DumpMessaggio msg = new DumpMessaggio();
		msg.setTipoMessaggio(tipoMessaggio);
		msg.setIdTransazione(idTransazione);
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
	public DumpMessaggio getDumpMessaggio(String idTransazione, TipoMessaggio tipoMessaggio) throws Exception {

		try {
			this.log.debug("Get Dump Messaggio [id transazione: " + idTransazione + "],[ tipomessaggio: "	+ tipoMessaggio.toString() + "]");

			IExpression expr = this.dumpMessaggioSearchDAO.newExpression();
			expr.equals(DumpMessaggio.model().TIPO_MESSAGGIO, TipoMessaggio.toEnumConstant(tipoMessaggio.toString()));
			expr.and().equals(DumpMessaggio.model().ID_TRANSAZIONE, idTransazione);

			DumpMessaggio mes = this.dumpMessaggioSearchDAO.find(expr);
			this.updateMessageWithSdk(mes);
			return mes;

		} catch (NotFoundException nre) {
			
			// provo a vedere se esiste virtualmente grazie all'SDK
			DumpMessaggio mes = createVirtualMessageWithSdk(idTransazione, tipoMessaggio);
			if(mes!=null){
				return mes;
			}
			
			// ignore
		} catch (Exception e) {
			this.log.error("Impossibile recuperare DumpMessaggio con idTransazione: "+ idTransazione + " e tipo: "	+ tipoMessaggio.toString(), e);
			throw new Exception("Impossibile recuperare DumpMessaggio con idTransazione: "	+ idTransazione + " e tipo: " + tipoMessaggio.toString(), e);
		}
		return null;
	}

	@Override
	public List<DumpAllegato> getAllegatiMessaggio(String idTransazione, TipoMessaggio tipoMessaggio, Long idDump) {

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
				DumpMessaggio mes = createVirtualMessageWithSdk(idTransazione, tipoMessaggio);
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
	public List<DumpContenuto> getContenutiSpecifici(String idTransazione,	TipoMessaggio tipoMessaggio, Long idDump) {
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
				DumpMessaggio mes = createVirtualMessageWithSdk(idTransazione, tipoMessaggio);
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
	public boolean hasInfoDumpAvailable(String idTransazione,
			TipoMessaggio tipoMessaggio) {

		try {

			this.log
			.info("Has Info Dump Available [id transazione: "
					+ idTransazione + "],[ tipomessaggio: "
					+ tipoMessaggio.toString() + "]");
			// Long l =
			// (Long)this.em.createQuery("select count(d) from DumpMessaggio d where d.idTransazione=:idTransazione and d.tipoMessaggio=:tipoMessaggio
			//AND ( (d.envelope is not null) OR (EXISTS (select id FROM DumpAllegato da WHERE da.dumpMessaggio.id=d.id)) OR (EXISTS (select id FROM DumpContenuto dc WHERE dc.dumpMessaggio.id=d.id)) ) ")
			// .setParameter("idTransazione", idTransazione)
			// .setParameter("tipoMessaggio", tipoMessaggio)
			// .getSingleResult();

			IExpression expr = this.dumpMessaggioSearchDAO.newExpression();
			expr.equals(DumpMessaggio.model().TIPO_MESSAGGIO,
					TipoMessaggio.toEnumConstant(tipoMessaggio.toString()));
			expr.and().equals(DumpMessaggio.model().ID_TRANSAZIONE,
					idTransazione);
			//			IExpression orExpr = this.dumpMessaggioSearchDAO.newExpression();

			//			orExpr.isNotNull(DumpMessaggio.model().ENVELOPE);
			//			
			//					
			//			orExpr.or().isNotEmpty(DumpMessaggio.model().ALLEGATO.ID_ALLEGATO);
			//			orExpr.or().isNotEmpty(DumpMessaggio.model().CONTENUTO.NOME);
			//
			//			expr.and(orExpr);

			DumpMessaggio msg = this.dumpMessaggioSearchDAO.find(expr);

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
			this.log.debug("non sono state trovate informazioni Dump per [id transazione: "+ idTransazione + "],[ tipomessaggio: "+ tipoMessaggio.toString() + "]");
			
			try{
				// provo a vedere se esiste virtualmente grazie all'SDK
				DumpMessaggio mes = createVirtualMessageWithSdk(idTransazione, tipoMessaggio);
				if(mes!=null){
					return mes.getBody() != null || mes.sizeAllegatoList() > 0 || mes.sizeContenutoList() > 0;
				}
			}catch (Exception eVirtual) {
				this.log.error(
						"Errore durante la costruzione virtuale del messaggio (hasInfoDumpAvailable) [id transazione: "+ idTransazione + "],[ tipomessaggio: "+ tipoMessaggio.toString() + "]", eVirtual);
			}
			
		}catch (Exception e) {
			this.log.error(e.getMessage(), e);
		}

		return false;
	}

	@Override
	public boolean hasInfoHeaderTrasportoAvailable(String idTransazione, TipoMessaggio tipoMessaggio) {

		this.log
		.info("Has Info Header Trasporto Available [id transazione: "	+ idTransazione + "],[ tipomessaggio: "	+ tipoMessaggio.toString() + "]");
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

			DumpMessaggio msg = this.dumpMessaggioSearchDAO.find(expr);

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
			this.log.debug("non sono state trovate informazioni sull'Header Trasporto per [id transazione: "+ idTransazione + "],[ tipomessaggio: "+ tipoMessaggio.toString() + "]");
			
			try{
				// provo a vedere se esiste virtualmente grazie all'SDK
				DumpMessaggio mes = createVirtualMessageWithSdk(idTransazione, tipoMessaggio);
				if(mes!=null){
					return mes.sizeHeaderTrasportoList() > 0;
				}
			}catch (Exception eVirtual) {
				this.log.error(
						"Errore durante la costruzione virtuale del messaggio (hasInfoHeaderTrasportoAvailable) [id transazione: "+ idTransazione + "],[ tipomessaggio: "+ tipoMessaggio.toString() + "]", eVirtual);
			}
			
		}catch (Exception e) {
			this.log.error(e.getMessage(), e);
		}

		return false;
	}

	@Override
	public List<DumpHeaderTrasporto> getHeaderTrasporto(String idTransazione, TipoMessaggio tipoMessaggio, Long idDump) {

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
				DumpMessaggio mes = createVirtualMessageWithSdk(idTransazione, tipoMessaggio);
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
			

			//			StringBuffer sb = new StringBuffer(
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
					TransazioneBean bean = new TransazioneBean(transazione);
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
			
			return new TransazioneBean(this.transazioniSearchDAO.find(expr));

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
	public ResLive getEsiti(PermessiUtenteOperatore permessiUtente, Date min, Date max, String esitoContesto, String protocollo, TipologiaRicerca tipologiaRicerca) {
		// StringBuffer pezzoIdPorta = new StringBuffer();

		this.log.debug("Get Esiti [permessiUtenti: " + permessiUtente + "],[ Date Min: " + min + "], [Date Max: " + max + "]");
		try {
			
			EsitoUtils esitoUtils = new EsitoUtils(this.log, protocollo);
			EsitiProperties esitiProperties = EsitiProperties.getInstance(this.log, protocollo);
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
			
			exprOk.and().equals(Transazione.model().PROTOCOLLO,	protocollo);
			exprFault.and().equals(Transazione.model().PROTOCOLLO,	protocollo);
			exprKo.and().equals(Transazione.model().PROTOCOLLO,	protocollo);
			
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

		return new ResLive(new Long("0"), new Long("0"), new Long("0"));

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

			IDynamicLoader bl = DynamicFactory.getInstance().newDynamicLoader(configurazioneRicerca.getPlugin().getClassName(),this.log);
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

			// StringBuffer sb = new StringBuffer(
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

			// StringBuffer sb = new StringBuffer(
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
		EsitoUtils esitoUtils = new EsitoUtils(this.log, this.searchForm.getProtocollo());
		esitoUtils.setExpression(filter, this.searchForm.getEsitoGruppo(), 
				this.searchForm.getEsitoDettaglio(),
				this.searchForm.getEsitoDettaglioPersonalizzato(),
				this.searchForm.getEsitoContesto(),
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

				filter.notEquals(Transazione.model().PDD_RUOLO,	PddRuolo.INTEGRATION_MANAGER);
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
				}

			} else if (TipologiaRicerca.ingresso.equals(this.searchForm.getTipologiaRicercaEnum())) {
				// EROGAZIONE

				// il mittente puo non essere specifica
				if (StringUtils.isNotBlank(this.searchForm.getNomeMittente())) {
					// sb.append("AND t.soggettoFruitore = :nome_mittente ");
					filter.and().equals(Transazione.model().NOME_SOGGETTO_FRUITORE,	this.searchForm.getNomeMittente());
				}

				if (StringUtils.isNotBlank(this.searchForm.getTipoMittente())) {
					// sb.append("AND t.soggettoFruitore = :nome_mittente ");
					filter.and().equals(Transazione.model().TIPO_SOGGETTO_FRUITORE,	this.searchForm.getTipoMittente());
				}

			} else {
				// FRUIZIONE

				// il destinatario puo nn essere specificato
				if (StringUtils.isNotBlank(this.searchForm.getNomeDestinatario())) {
					// sb.append("AND t.soggettoErogatore = :nome_destinatario ");
					filter.and().equals(Transazione.model().NOME_SOGGETTO_EROGATORE,this.searchForm.getNomeDestinatario());
				}

				if (StringUtils.isNotBlank(this.searchForm.getTipoDestinatario())) {
					// sb.append("AND t.soggettoErogatore = :nome_destinatario ");
					filter.and().equals(Transazione.model().TIPO_SOGGETTO_EROGATORE,this.searchForm.getTipoDestinatario());
				}
			}

		}

		impostaFiltroDatiMittente(filter, this.transazioniSearchDAO, this.searchForm, Transazione.model(), isCount, isLiveSearch);
		
		
		
		// aggiungo la condizione sul protocollo se e' impostato e se e' presente piu' di un protocollo
		// protocollo e' impostato anche scegliendo la modalita'
//		if (StringUtils.isNotEmpty(this.searchForm.getProtocollo()) && this.searchForm.isShowListaProtocolli()) {
		if (this.searchForm.isSetFiltroProtocollo()) {
			filter.and().equals(Transazione.model().PROTOCOLLO,	this.searchForm.getProtocollo());
		}

		if (StringUtils.isNotEmpty(this.searchForm.getEvento())) {
			filter.and().ilike(Transazione.model().EVENTI_GESTIONE,	this.searchForm.getEvento().trim(),LikeMode.ANYWHERE);
		}
		
		if (StringUtils.isNotEmpty(this.searchForm.getClusterId())) {
			
			filter.and().equals(Transazione.model().CLUSTER_ID,	this.searchForm.getClusterId().trim());
			
		}
		
		//Ricerche personalizzate
		IFilter filtro = this.searchForm.getFiltro();		
		if(filtro != null){
			FilterImpl f = (FilterImpl) filtro;
			filter.and(f.getExpression());
		}

	}
	private void impostaFiltroDatiMittente(IExpression filter, ITransazioneServiceSearch transazioniSearchDAO , BaseSearchForm searchForm, TransazioneModel model, boolean isCount, boolean isLiveSearch)
			throws ServiceException, NotImplementedException, ExpressionNotImplementedException, ExpressionException {
		// credenziali mittente
		if(StringUtils.isNotEmpty(searchForm.getRiconoscimento())) {
			if(searchForm.getRiconoscimento().equals(org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_APPLICATIVO)) {
				if (StringUtils.isNotBlank(searchForm.getServizioApplicativo())) {
					// sb.append("AND t.servizioApplicativo = :servizio_applicativo ");
					IExpression saOr = transazioniSearchDAO.newExpression();
					saOr.equals(model.SERVIZIO_APPLICATIVO_FRUITORE,	searchForm.getServizioApplicativo());
//					saOr.or();
//					saOr.equals(model.SERVIZIO_APPLICATIVO_EROGATORE,	searchForm.getServizioApplicativo());
					filter.and(saOr);
				}
			} else {
				List<CredenzialeMittente> listaCredenzialiMittente = getIdCredenzialiFromFilter(searchForm, this.credenzialiMittenteDAO, isCount, isLiveSearch);
				
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
			case trasporto:
			default:
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
			if(searchForm.getRiconoscimento().equals(org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_IDENTIFICATIVO_AUTENTICATO)) {
				pagExpr = CredenzialiMittenteUtils.createCredenzialeMittentePaginatedExpression(credenzialeMittentiService,
						TipoCredenzialeMittente.trasporto, searchForm.getAutenticazione(), searchForm.getValoreRiconoscimento(), ricercaEsatta, caseSensitive);
			} 
			
			if(searchForm.getRiconoscimento().equals(org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_TOKEN_INFO)) {
				TipoCredenzialeMittente tcm = TipoCredenzialeMittente.valueOf(searchForm.getTokenClaim());
				pagExpr = CredenzialiMittenteUtils.createCredenzialeMittentePaginatedExpression(credenzialeMittentiService,
						tcm, null, searchForm.getValoreRiconoscimento(), ricercaEsatta, caseSensitive);
			}
			
			findAll = credenzialeMittentiService.findAll(pagExpr);
		}catch(ServiceException e) {
			this.log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			this.log.error(e.getMessage(), e);
		} catch (ExpressionNotImplementedException e) { 
			this.log.error(e.getMessage(), e);
		} catch (ExpressionException e) {
			this.log.error(e.getMessage(), e);
		} catch (UtilsException e) {
			this.log.error(e.getMessage(), e);
		}
		return findAll;
	}
}
