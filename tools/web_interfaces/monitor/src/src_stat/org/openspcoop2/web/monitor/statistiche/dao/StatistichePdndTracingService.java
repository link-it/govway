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
package org.openspcoop2.web.monitor.statistiche.dao;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.lang3.StringUtils;
import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.commons.dao.DAOFactory;
import org.openspcoop2.core.commons.dao.DAOFactoryException;
import org.openspcoop2.core.commons.search.Soggetto;
import org.openspcoop2.core.commons.search.dao.ISoggettoServiceSearch;
import org.openspcoop2.core.commons.search.dao.jdbc.JDBCSoggettoServiceSearch;
import org.openspcoop2.core.constants.CostantiLabel;
import org.openspcoop2.core.statistiche.StatistichePdndTracing;
import org.openspcoop2.core.statistiche.constants.TipoIntervalloStatistico;
import org.openspcoop2.core.statistiche.dao.IDBStatistichePdndTracingServiceSearch;
import org.openspcoop2.core.statistiche.dao.IStatistichePdndTracingServiceSearch;
import org.openspcoop2.core.statistiche.dao.jdbc.JDBCStream;
import org.openspcoop2.core.statistiche.model.StatistichePdndTracingModel;
import org.openspcoop2.generic_project.beans.NonNegativeNumber;
import org.openspcoop2.generic_project.beans.UpdateField;
import org.openspcoop2.generic_project.dao.IServiceSearchWithoutId;
import org.openspcoop2.generic_project.exception.ExpressionException;
import org.openspcoop2.generic_project.exception.ExpressionNotImplementedException;
import org.openspcoop2.generic_project.exception.MultipleResultException;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.generic_project.exception.NotImplementedException;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.generic_project.expression.IExpression;
import org.openspcoop2.generic_project.expression.IPaginatedExpression;
import org.openspcoop2.generic_project.expression.SortOrder;
import org.openspcoop2.generic_project.utils.ServiceManagerProperties;
import org.openspcoop2.monitor.engine.statistic.PdndTracciamentoInfo;
import org.openspcoop2.monitor.engine.statistic.PdndTracciamentoSoggetto;
import org.openspcoop2.monitor.engine.statistic.PdndTracciamentoUtils;
import org.openspcoop2.monitor.engine.statistic.StatisticsInfoUtils;
import org.openspcoop2.protocol.engine.utils.NamingUtils;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.web.monitor.core.core.PddMonitorProperties;
import org.openspcoop2.web.monitor.core.core.Utility;
import org.openspcoop2.web.monitor.core.dao.DynamicUtilsService;
import org.openspcoop2.web.monitor.core.dao.IDynamicUtilsService;
import org.openspcoop2.web.monitor.core.exception.UserInvalidException;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;
import org.openspcoop2.web.monitor.core.thread.ThreadExecutorManager;
import org.openspcoop2.web.monitor.statistiche.bean.StatistichePdndTracingBean;
import org.openspcoop2.web.monitor.statistiche.bean.StatistichePdndTracingSearchForm;
import org.openspcoop2.web.monitor.statistiche.constants.ModalitaRicercaStatistichePdnd;
import org.openspcoop2.web.monitor.statistiche.constants.StatisticheCostanti;
import org.slf4j.Logger;

/**
 * StatistichePdndTracingService
 * 
 * @author Pintori Giuliano (giuliano.pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class StatistichePdndTracingService implements IStatistichePdndTracingService {

	private static Logger log = LoggerManager.getPddMonitorSqlLogger(); 
	private static void logError(String msg, Throwable e) {
		if(log!=null) {
			log.error(msg,e);
		}
	}
	
	private transient IDynamicUtilsService dynamicService = null;

	private StatistichePdndTracingSearchForm search;

	private org.openspcoop2.core.statistiche.dao.IServiceManager statisticheServiceManager;
	
	private IStatistichePdndTracingServiceSearch statistichePdndTracingServiceSearchDAO;
	
	private org.openspcoop2.core.commons.search.dao.IServiceManager utilsServiceManager;
	
	private ISoggettoServiceSearch soggettiServiceSearchDAO;

	private PddMonitorProperties govwayMonitorProperties;

	private boolean timeoutEvent = false;
	private Integer timeoutRicerche = null;

	public StatistichePdndTracingService() {

		try {
			this.statisticheServiceManager = (org.openspcoop2.core.statistiche.dao.IServiceManager) DAOFactory
					.getInstance(StatistichePdndTracingService.log).getServiceManager(org.openspcoop2.core.statistiche.utils.ProjectInfo.getInstance(),StatistichePdndTracingService.log);

			this.statistichePdndTracingServiceSearchDAO = this.statisticheServiceManager.getStatistichePdndTracingServiceSearch();

			this.utilsServiceManager = (org.openspcoop2.core.commons.search.dao.IServiceManager) DAOFactory
					.getInstance(StatistichePdndTracingService.log).getServiceManager(org.openspcoop2.core.commons.search.utils.ProjectInfo.getInstance(), StatistichePdndTracingService.log);
			
			this.soggettiServiceSearchDAO = this.utilsServiceManager.getSoggettoServiceSearch();
			
			this.govwayMonitorProperties = PddMonitorProperties.getInstance(StatistichePdndTracingService.log);

			this.timeoutRicerche = this.govwayMonitorProperties.getIntervalloTimeoutRicercaStatistiche();

			this.dynamicService = new DynamicUtilsService();
		} catch (DAOFactoryException | ServiceException | NotImplementedException | UtilsException e) {
			StatistichePdndTracingService.logError(e.getMessage(), e);
		}
	}

	public StatistichePdndTracingService(Connection con, boolean autoCommit){
		this(con, autoCommit, null, StatistichePdndTracingService.log);
	}
	public StatistichePdndTracingService(Connection con, boolean autoCommit, Logger log){
		this(con, autoCommit, null, log);
	}
	public StatistichePdndTracingService(Connection con, boolean autoCommit, ServiceManagerProperties serviceManagerProperties){
		this(con, autoCommit, serviceManagerProperties, StatistichePdndTracingService.log);
	}
	public StatistichePdndTracingService(Connection con, boolean autoCommit, ServiceManagerProperties serviceManagerProperties, Logger log) {
		try {
			this.statisticheServiceManager = (org.openspcoop2.core.statistiche.dao.IServiceManager) DAOFactory
					.getInstance(log).getServiceManager(org.openspcoop2.core.statistiche.utils.ProjectInfo.getInstance(),con,autoCommit,serviceManagerProperties,log);

			this.statistichePdndTracingServiceSearchDAO = this.statisticheServiceManager.getStatistichePdndTracingServiceSearch();

			this.govwayMonitorProperties = PddMonitorProperties.getInstance(StatistichePdndTracingService.log);

			this.timeoutRicerche = this.govwayMonitorProperties.getIntervalloTimeoutRicercaStatistiche();
			
			this.dynamicService = new DynamicUtilsService(con,autoCommit,serviceManagerProperties,log);

		} catch (DAOFactoryException | ServiceException | NotImplementedException | UtilsException e) {
			StatistichePdndTracingService.logError(e.getMessage(), e);
		}
	}

	@Override
	public void setSearch(StatistichePdndTracingSearchForm search) {
		this.search = search;
	}

	@Override
	public StatistichePdndTracingSearchForm getSearch() {
		return this.search;
	}

	@Override
	public List<StatistichePdndTracingBean> findAll(int start, int limit) {
		return this.findAllEngine(start, limit);
	}

	@Override
	public int totalCount() {
		if (this.search == null)
			return 0;

		try {

			IExpression expr = createQuery(true, this.statistichePdndTracingServiceSearchDAO, StatistichePdndTracing.model());
			NonNegativeNumber nnn = this.statistichePdndTracingServiceSearchDAO.count(expr);

			return nnn != null ? ((int) nnn.longValue()) : 0;
		} catch (ServiceException | NotImplementedException e) {
			StatistichePdndTracingService.logError("Errore durante il calcolo del numero dei record: " + e.getMessage(), e);
		}

		return 0;
	}

	@Override
	public void store(StatistichePdndTracingBean obj) throws Exception {
		// unimplemented	
	}

	@Override
	public void deleteById(Long key) {
		// unimplemented	
	}

	@Override
	public void delete(StatistichePdndTracingBean obj) throws Exception {
		// unimplemented
	}

	@Override
	public void deleteAll() throws Exception {
		// unimplemented
	}

	public void updateFields(StatistichePdndTracingBean bean, UpdateField field) throws ServiceException, NotFoundException, NotImplementedException {
		this.statisticheServiceManager.getStatistichePdndTracingService().updateFields(bean, field);
	}
	
	public void forcePublish() throws Exception {
		IExpression expr = createQuery(false, this.statistichePdndTracingServiceSearchDAO, StatistichePdndTracing.model());
		this.statisticheServiceManager.getStatistichePdndTracingService().forcePublish(expr);
	}
	
	public void forcePublish(List<Long> ids) throws  Exception {
		this.statisticheServiceManager.getStatistichePdndTracingService().forcePublish(ids);
	}
	
	
	public boolean isForcePublishEnabled(StatistichePdndTracingBean tracing) {
		if (tracing.getFailed() && !tracing.isForcePublish()) {
			
			
			try {
				Date date = StatisticsInfoUtils.readDataUltimaGenerazioneStatistiche(
						this.statisticheServiceManager.getStatisticaInfoServiceSearch(), 
						TipoIntervalloStatistico.PDND_PUBBLICAZIONE_TRACCIAMENTO,
						log);
				
				return date.after(tracing.getDataPubblicazione());
			} catch (Exception e) {
				return false;
			}
		}
		return false;
	}
	
	public void forcePublish(StatistichePdndTracingBean tracing) throws  Exception {
		if (!isForcePublishEnabled(tracing))
			throw new ServiceException("Il tracciato non risulta essere in uno stato in cui può essere abilitata la pubblicazione forzata: non ha ancora superato il numero massimo di tentativi o non è nello stato FAILED");
		forcePublish(List.of(tracing.getId()));
	}
	
	public JDBCStream getCsvInputStream(long id) {
		IDBStatistichePdndTracingServiceSearch idSearch = (IDBStatistichePdndTracingServiceSearch) this.statistichePdndTracingServiceSearchDAO;
		try {
			return idSearch.getCsvInputStream(id);
		} catch (NotFoundException e) {
			StatistichePdndTracingService.logError("CSV non trovato per tracing PDND con id:" + id, e);
			return null;
		} catch (ServiceException e) {
			StatistichePdndTracingService.logError("Errore durante il recupero del CSV per tracing PDND con id:" + id, e);
			return null;
		}
	}
	
	public boolean existsCsv(long id) {
		IDBStatistichePdndTracingServiceSearch idSearch = (IDBStatistichePdndTracingServiceSearch) this.statistichePdndTracingServiceSearchDAO;
		try {
			String sql = "SELECT id from statistiche_pdnd_tracing where csv is not null AND id=?";
			List<Class<?>> returnClassTypes = new ArrayList<>();
			returnClassTypes.add(Long.class);
			Object[] parameters = new Object[] { id };
			List<List<Object>> l = idSearch.nativeQuery(sql, returnClassTypes, parameters);
			return l!=null && !l.isEmpty() && l.get(0)!=null;
		} catch (NotFoundException e) {
			StatistichePdndTracingService.logError("CSV non trovato per tracing PDND con id:" + id, e);
			return false;
		} catch (Exception e) {
			StatistichePdndTracingService.logError("Errore durante il recupero del CSV per tracing PDND con id:" + id, e);
			return false;
		}
	}

	@Override
	public StatistichePdndTracingBean findById(Long key) {
		IDBStatistichePdndTracingServiceSearch idSearch = (IDBStatistichePdndTracingServiceSearch) this.statistichePdndTracingServiceSearchDAO;

		try {
			StatistichePdndTracing statistichePdndTracing = idSearch.get(key);

			if(statistichePdndTracing != null) {
				StatistichePdndTracingBean bean = new StatistichePdndTracingBean(statistichePdndTracing);
				List<Soggetto> list = this.dynamicService.findElencoSoggetti(CostantiLabel.MODIPA_PROTOCOL_NAME ,statistichePdndTracing.getPddCodice());
				if (list != null && !list.isEmpty()) {
					Soggetto soggetto = list.get(0);
					bean.setSoggettoReadable(NamingUtils.getLabelSoggetto(CostantiLabel.MODIPA_PROTOCOL_NAME, soggetto.getTipoSoggetto(), soggetto.getNomeSoggetto()));
				}
				
				return bean;
			}
		} catch (ServiceException | NotFoundException | MultipleResultException | NotImplementedException | ProtocolException e) {
			StatistichePdndTracingService.logError("Errore durante la ricerca dei Tracing PDND con id:" + key, e);
		} 
		return null;
	}

	@Override
	public List<StatistichePdndTracingBean> findAll() {
		return this.findAllEngine(null, null);
	}

	private List<StatistichePdndTracingBean> findAllEngine(Integer start, Integer limit) {
		if (this.search == null) {
			return new ArrayList<>();
		}

		try {
			IExpression expr = createQuery(false, this.statistichePdndTracingServiceSearchDAO, StatistichePdndTracing.model());

			IPaginatedExpression pagExpr = this.statistichePdndTracingServiceSearchDAO.toPaginatedExpression(expr).offset(start).limit(limit);

			this.timeoutEvent = false;

			List<StatistichePdndTracing> findAll = null;

			if(this.timeoutRicerche == null) {
				findAll = this.statistichePdndTracingServiceSearchDAO.findAll(pagExpr);
			} else {
				try {
					findAll = ThreadExecutorManager.getClientPoolExecutorRicerche().submit(() -> this.statistichePdndTracingServiceSearchDAO.findAll(pagExpr)).get(this.timeoutRicerche.longValue(), TimeUnit.SECONDS);
				} catch (InterruptedException e) {
					StatistichePdndTracingService.logError(e.getMessage(), e);
					Thread.currentThread().interrupt();
				} catch (ExecutionException e) {
					if(e.getCause() instanceof ServiceException) {
						throw (ServiceException) e.getCause();
					}
					if(e.getCause() instanceof NotImplementedException) {
						throw (NotImplementedException) e.getCause();
					}
					StatistichePdndTracingService.logError(e.getMessage(), e);
				} catch (TimeoutException e) {
					List<StatistichePdndTracingBean> lNull = null;
					this.timeoutEvent = true;
					StatistichePdndTracingService.logError(e.getMessage(), e);
					return lNull;
				}
			}

			if(findAll != null && !findAll.isEmpty()){
				List<StatistichePdndTracingBean> toRet = new ArrayList<>();

				for (StatistichePdndTracing statistichePdndTracing : findAll) {
					StatistichePdndTracingBean bean = new StatistichePdndTracingBean(statistichePdndTracing);
					
					String nomeSoggettoPrincipale = null;
					List<Soggetto> list = this.dynamicService.findElencoSoggetti(CostantiLabel.MODIPA_PROTOCOL_NAME ,statistichePdndTracing.getPddCodice());
					if (list != null && !list.isEmpty()) {
						Soggetto soggetto = list.get(0);
						nomeSoggettoPrincipale = soggetto.getNomeSoggetto();
						bean.setSoggettoReadable(NamingUtils.getLabelSoggetto(CostantiLabel.MODIPA_PROTOCOL_NAME, soggetto.getTipoSoggetto(), nomeSoggettoPrincipale));
					}
					
					if(this.search!=null && this.search.getSoggettoLocale()!=null && !this.search.getSoggettoLocale().equals(nomeSoggettoPrincipale)) {
						// ricerca su soggetto differente da quello principale
						bean.setSoggettoReadable(bean.getSoggettoReadable()+" (aggregatore per "+ this.search.getSoggettoLocale()+")");
					}
					
					toRet.add(bean);
				}

				return toRet;
			}
		} catch (ServiceException | NotImplementedException | ExpressionNotImplementedException | ExpressionException | ProtocolException e) {
			StatistichePdndTracingService.logError("Errore durante la ricerca dei Tracing PDND: " + e.getMessage(), e);
		}


		return new ArrayList<>();
	}
	

	private IExpression createQuery(boolean isCount, IServiceSearchWithoutId<?> dao, StatistichePdndTracingModel model) throws ServiceException {
		IExpression expr = null;

		try {
			expr = dao.newExpression();

			ModalitaRicercaStatistichePdnd ricerca = ModalitaRicercaStatistichePdnd.getFromString(this.search.getModalitaRicerca());

			if (ricerca.equals(ModalitaRicercaStatistichePdnd.ANDAMENTO_TEMPORALE)) {

				// Data
				expr.between(model.DATA_TRACCIAMENTO, this.search.getDataInizio(),	this.search.getDataFine());

				// permessi utente operatore
				if(this.search.getPermessiUtenteOperatore()!=null){
					IExpression permessi = this.search.getPermessiUtenteOperatore().toExpressionStatistichePdndTracing(dao, model.PDD_CODICE);
					expr.and(permessi);
				}

				// Soggetto
				if(Utility.isFiltroDominioAbilitato() && this.search.getSoggettoLocale()!=null && !StringUtils.isEmpty(this.search.getSoggettoLocale()) && 
						!StatisticheCostanti.NON_SELEZIONATO.equals(this.search.getSoggettoLocale())){
					
					String tipoSoggettoLocale = this.search.getTipoSoggettoLocale();
					String nomeSoggettoLocale = this.search.getSoggettoLocale();
					
					// gestione dati aggregati
					nomeSoggettoLocale = traduciSoggettoAggregato(nomeSoggettoLocale) ;
					
					String idPorta = Utility.getIdentificativoPorta(tipoSoggettoLocale, nomeSoggettoLocale);
					expr.and().equals(model.PDD_CODICE, idPorta);
				}

				// Stato
				if (this.search.getStato() != null  && StringUtils.isNotEmpty(this.search.getStato())
						&& !StatisticheCostanti.NON_SELEZIONATO.equals(this.search.getStato())) {
					if(StatisticheCostanti.STATS_PDND_TRACING_STATO_IN_ATTESA_VALUE.equals(this.search.getStato())) {
						expr.isNull(model.STATO);
					}
					else {
						expr.equals(model.STATO, this.search.getStato());
					}
				}

				// Stato PDND
				if (this.search.getStatoPdnd() != null && StringUtils.isNotEmpty(this.search.getStatoPdnd()) 
						&& !StatisticheCostanti.NON_SELEZIONATO.equals(this.search.getStatoPdnd())) {
					expr.equals(model.STATO_PDND, this.search.getStatoPdnd());
				}

				// numero tentativi pubblicazione
				if (this.search.getTentativiPubblicazione() != null) {
					expr.equals(model.TENTATIVI_PUBBLICAZIONE, this.search.getTentativiPubblicazione());
				}

			} else if (ricerca.equals(ModalitaRicercaStatistichePdnd.TRACING_ID)) {
				// tracingId
				if (this.search.getTracingId() != null && StringUtils.isNotEmpty(this.search.getTracingId())) {
					expr.equals(model.TRACING_ID, this.search.getTracingId());
				} else {
					throw new ServiceException("Tracing ID non fornito.");
				}

			} else {
				throw new ServiceException("Modalità di ricerca non supportata: " + this.search.getModalitaRicerca());
			}

			// filtro built-in eliminazione duplicati -> history = 0
			expr.equals(model.HISTORY, 0);
			
			// filtro built-in csv is not null
			expr.isNotNull(model.CSV);
			
			
			if(!isCount) {
				expr.addOrder(model.DATA_TRACCIAMENTO, SortOrder.DESC);
				expr.addOrder(model.PDD_CODICE, SortOrder.DESC);
			}

		} catch (NotImplementedException | ExpressionNotImplementedException | ExpressionException | CoreException | UserInvalidException e) {
			StatistichePdndTracingService.logError("Errore durante la creazione dell'espressione: " + e.getMessage(), e);
			throw new ServiceException(e);
		} catch (ServiceException e) {
			StatistichePdndTracingService.logError("Errore durante la creazione dell'espressione: " + e.getMessage(), e);
			throw e;
		}

		return expr;
	}
	private String traduciSoggettoAggregato(String nomeSoggettoLocale) {
		try {
			PdndTracciamentoInfo info = PdndTracciamentoUtils.getInfoTracciamento((JDBCSoggettoServiceSearch) this.soggettiServiceSearchDAO, StatistichePdndTracingService.log);
			// cerco aggregato
			PdndTracciamentoSoggetto s = info.getInfoByNomeSoggetto(nomeSoggettoLocale, false, true);
			if(s!=null) {
				// sovrascrivo il soggetto
				return s.getIdSoggetto().getNome();
			}
		}catch(Exception e) {
			StatistichePdndTracingService.log.error("Raccolta info soggetti per tracing fallita: "+e.getMessage(),e);
		}
		return nomeSoggettoLocale;
	} 

	@Override
	public boolean isTimeoutEvent() {
		return this.timeoutEvent;
	}
}
