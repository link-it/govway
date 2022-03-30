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
package org.openspcoop2.web.monitor.eventi.dao;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.commons.dao.DAOFactory;
import org.openspcoop2.core.eventi.Evento;
import org.openspcoop2.core.eventi.constants.TipoSeverita;
import org.openspcoop2.core.eventi.dao.IDBEventoServiceSearch;
import org.openspcoop2.core.eventi.dao.IEventoServiceSearch;
import org.openspcoop2.core.eventi.dao.IServiceManager;
import org.openspcoop2.core.eventi.utils.SeveritaConverter;
import org.openspcoop2.generic_project.beans.NonNegativeNumber;
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
import org.openspcoop2.generic_project.utils.ServiceManagerProperties;
import org.openspcoop2.web.monitor.core.constants.CaseSensitiveMatch;
import org.openspcoop2.web.monitor.core.constants.TipoMatch;
import org.openspcoop2.web.monitor.core.core.PddMonitorProperties;
import org.openspcoop2.web.monitor.core.core.Utility;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;
import org.openspcoop2.web.monitor.core.thread.ThreadExecutorManager;
import org.openspcoop2.web.monitor.eventi.bean.EventiSearchForm;
import org.openspcoop2.web.monitor.eventi.bean.EventoBean;
import org.slf4j.Logger;


/**
 * EventiService
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class EventiService implements IEventiService{

	private static Logger log =  LoggerManager.getPddMonitorSqlLogger();

	private IServiceManager pluginsServiceManager = null;
	private IEventoServiceSearch eventiDao = null;

	private EventiSearchForm searchForm = null;

	private boolean timeoutEvent = false;
	private Integer timeoutRicerche = null;

	private List<Index> forceIndexFindAll;
	private List<Index> forceIndexCount;
	private void initForceIndex(PddMonitorProperties govwayMonitorProperties) throws Exception{
		Properties repositoryExternal = govwayMonitorProperties.getExternalForceIndexRepository();
		this.forceIndexFindAll = convertForceIndexList(govwayMonitorProperties.getEventiForceIndexFindAll(repositoryExternal));
		this.forceIndexCount = convertForceIndexList(govwayMonitorProperties.getEventiForceIndexCount(repositoryExternal));
	}
	private List<Index> convertForceIndexList(List<String> l){
		if(l!=null && l.size()>0){
			List<Index> li = new ArrayList<Index>();
			for (String index : l) {
				li.add(new Index(Evento.model(),index));
			}
			return li;
		}
		return null;
	}
	
	public EventiService(){
		try {
			// init Service Manager plugins
			this.pluginsServiceManager = (IServiceManager) DAOFactory
					.getInstance(EventiService.log).getServiceManager(
							org.openspcoop2.core.eventi.utils.ProjectInfo.getInstance(),EventiService.log);
			this.eventiDao = this.pluginsServiceManager
					.getEventoServiceSearch();
			
			this.initForceIndex(PddMonitorProperties.getInstance(EventiService.log));
			
			this.timeoutRicerche = PddMonitorProperties.getInstance(EventiService.log).getIntervalloTimeoutRicercaEventi();
			
		} catch (Exception e) {
			EventiService.log.error(e.getMessage(), e);
		}
	}
	
	public EventiService(Connection con, boolean autoCommit){
		this(con, autoCommit, null, EventiService.log);
	}
	public EventiService(Connection con, boolean autoCommit, Logger log){
		this(con, autoCommit, null, log);
	}
	public EventiService(Connection con, boolean autoCommit, ServiceManagerProperties serviceManagerProperties){
		this(con, autoCommit, serviceManagerProperties, EventiService.log);
	}
	public EventiService(Connection con, boolean autoCommit, ServiceManagerProperties serviceManagerProperties, Logger log){
		try {
			// init Service Manager plugins
			this.pluginsServiceManager = (IServiceManager) DAOFactory
					.getInstance(log).getServiceManager(
							org.openspcoop2.core.eventi.utils.ProjectInfo.getInstance(),con,autoCommit,serviceManagerProperties,log);
			this.eventiDao = this.pluginsServiceManager
					.getEventoServiceSearch();
			
			this.initForceIndex(PddMonitorProperties.getInstance(EventiService.log));
			
			this.timeoutRicerche = PddMonitorProperties.getInstance(EventiService.log).getIntervalloTimeoutRicercaEventi();
			
		} catch (Exception e) {
			EventiService.log.error(e.getMessage(), e);
		}
	}

	@Override
	public void setSearch(EventiSearchForm searchForm) {
		this.searchForm = searchForm;
	}
	
	@Override
	public EventiSearchForm getSearch() {
		return this.searchForm;
	}

	@Override
	public List<EventoBean> findAll(int start, int limit) {
		EventiService.log.debug("Metodo FindAll: start[" + start + "], limit: [" + limit + "]");

		try {
			IExpression expr = this.getExpressionFromFilter(this.eventiDao);

			IPaginatedExpression pagExpr = this.eventiDao.toPaginatedExpression(expr);

			pagExpr.offset(start).limit(limit);

			pagExpr.sortOrder(SortOrder.DESC).addOrder(Evento.model().ORA_REGISTRAZIONE);

			List<Index> forceIndexFindAll = this.forceIndexFindAll;
			if(forceIndexFindAll!=null && forceIndexFindAll.size()>0){
				for (Index index : forceIndexFindAll) {
					pagExpr.addForceIndex(index);	
				}
			}
			
			this.timeoutEvent = false;
			
			List<Evento> list = null;
			if(this.timeoutRicerche == null) {
				list = this.eventiDao.findAll(pagExpr);
			} else {
				try {
					list = ThreadExecutorManager.getClientPoolExecutorRicerche().submit(() -> this.eventiDao.findAll(pagExpr)).get(this.timeoutRicerche.longValue(), TimeUnit.SECONDS);
				} catch (InterruptedException e) {
					EventiService.log.error(e.getMessage(), e);
				} catch (ExecutionException e) {
					if(e.getCause() instanceof ServiceException) {
						throw (ServiceException) e.getCause();
					}
					if(e.getCause() instanceof NotImplementedException) {
						throw (NotImplementedException) e.getCause();
					}
					EventiService.log.error(e.getMessage(), e);
				} catch (TimeoutException e) {
					this.timeoutEvent = true;
					EventiService.log.error(e.getMessage(), e);
				}
			}
			if(list!=null && list.size()>0){
				List<EventoBean> wrappedList = new ArrayList<EventoBean>();
				for (Evento evento : list) {
					wrappedList.add(new EventoBean(evento));
				}
				return wrappedList;
			}

		} catch (ExpressionNotImplementedException  e) {
			EventiService.log.error(e.getMessage(), e);
		} catch (ExpressionException e) {
			EventiService.log.error(e.getMessage(), e);
		} catch (ServiceException e) {
			EventiService.log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			EventiService.log.error(e.getMessage(), e);
		}

		return new ArrayList<EventoBean>();
	}

	@Override
	public int totalCount() {
		EventiService.log.debug("Metodo TotalCount");

		try {
			IExpression expr = this.getExpressionFromFilter(this.eventiDao);

			List<Index> forceIndexFindAll = this.forceIndexCount;
			if(forceIndexFindAll!=null && forceIndexFindAll.size()>0){
				for (Index index : forceIndexFindAll) {
					expr.addForceIndex(index);	
				}
			}

			NonNegativeNumber nnn = this.eventiDao.count(expr);

			if(nnn != null)
				return (int) nnn.longValue();
		} catch (ExpressionNotImplementedException  e) {
			EventiService.log.error(e.getMessage(), e);
		} catch (ExpressionException e) {
			EventiService.log.error(e.getMessage(), e);
		} catch (ServiceException e) {
			EventiService.log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			EventiService.log.error(e.getMessage(), e);
		}

		return 0;
	}

	@Override
	public void store(EventoBean evento)
			throws Exception {

	}

	

	@Override
	public void deleteById(Long key) {
		

	}

	@Override
	public void delete(EventoBean obj)  throws Exception {
		
	}

	@Override
	public void deleteAll() throws Exception {
		

	}

	@Override
	public EventoBean findById(Long key) {
		EventoBean bean = null;
		try {
			bean = this.findById(key, false);
		}catch(NotFoundException notFound){
			// il parametro false assicura che l'eccezione non sia mai lanciata
		}
		return bean;
	}
	public EventoBean findById(Long key, boolean throwNotFound) throws NotFoundException {
		EventiService.log.debug("Metodo FindById: [" + key + "]");

		try {
			IDBEventoServiceSearch search = (IDBEventoServiceSearch) this.eventiDao;
			Evento evento = search.get(key.longValue());
			if(evento!=null){
				return new EventoBean(evento);
			}
		} catch (ServiceException e) {
			EventiService.log.error(e.getMessage(), e);
		} catch (NotFoundException e) {
			if(throwNotFound) {
				throw e;
			}
			else {
				EventiService.log.error(e.getMessage(), e);
			}
		} catch (MultipleResultException e) {
			EventiService.log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			EventiService.log.error(e.getMessage(), e);
		}

		return null;
	}

	@Override
	public List<EventoBean> findAll() {
		EventiService.log.debug("Metodo FindAll");

		try {
			IExpression expr = this.getExpressionFromFilter(this.eventiDao);

			IPaginatedExpression pagExpr = this.eventiDao.toPaginatedExpression(expr);

			pagExpr.sortOrder(SortOrder.DESC).addOrder(Evento.model().ORA_REGISTRAZIONE);

			List<Index> forceIndexFindAll = this.forceIndexFindAll;
			if(forceIndexFindAll!=null && forceIndexFindAll.size()>0){
				for (Index index : forceIndexFindAll) {
					pagExpr.addForceIndex(index);	
				}
			}
			
			this.timeoutEvent = false;
			
			List<Evento> list = null;
			if(this.timeoutRicerche == null) {
				list = this.eventiDao.findAll(pagExpr);
			} else {
				try {
					list = ThreadExecutorManager.getClientPoolExecutorRicerche().submit(() -> this.eventiDao.findAll(pagExpr)).get(this.timeoutRicerche.longValue(), TimeUnit.SECONDS);
				} catch (InterruptedException e) {
					EventiService.log.error(e.getMessage(), e);
				} catch (ExecutionException e) {
					if(e.getCause() instanceof ServiceException) {
						throw (ServiceException) e.getCause();
					}
					if(e.getCause() instanceof NotImplementedException) {
						throw (NotImplementedException) e.getCause();
					}
					EventiService.log.error(e.getMessage(), e);
				} catch (TimeoutException e) {
					this.timeoutEvent = true;
					EventiService.log.error(e.getMessage(), e);
				}
			}
			if(list!=null && list.size()>0){
				List<EventoBean> wrappedList = new ArrayList<EventoBean>();
				for (Evento evento : list) {
					wrappedList.add(new EventoBean(evento));
				}
				return wrappedList;
			}

		} catch (ExpressionNotImplementedException  e) {
			EventiService.log.error(e.getMessage(), e);
		} catch (ExpressionException e) {
			EventiService.log.error(e.getMessage(), e);
		} catch (ServiceException e) {
			EventiService.log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			EventiService.log.error(e.getMessage(), e);
		}

		return new ArrayList<EventoBean>();
	}


	/***
	 * 
	 * converto i dati inseriti dall'utente nel form nella query sql
	 * 
	 * @param dao
	 * @return Condizioni di ricerca da applicare
	 * @throws ExpressionException 
	 * @throws ExpressionNotImplementedException 
	 * @throws NotImplementedException 
	 * @throws ServiceException 
	 * @throws Exception
	 */
	public IExpression getExpressionFromFilter(IEventoServiceSearch dao) throws ExpressionNotImplementedException, ExpressionException, ServiceException, NotImplementedException {
		IExpression expr = dao.newExpression();
		
		if(this.searchForm == null)
			return expr;

		expr.and();
		
		if (this.searchForm.getDataInizio() != null) {
			// sb.append("AND t.dataIngressoRichiesta >= :data_inizio ");
			expr.and().greaterEquals(
					Evento.model().ORA_REGISTRAZIONE,
					this.searchForm.getDataInizio());
		}

		if (this.searchForm.getDataFine() != null) {
			// sb.append("AND t.dataIngressoRichiesta <= :data_fine ");
			expr.and().lessEquals(
					Evento.model().ORA_REGISTRAZIONE,
					this.searchForm.getDataFine());
		}
		
		if (this.searchForm.getDataRicerca() != null) {
			// sb.append(" AND t.dataIngressoRichiesta<= :data_richiesta_ricerca");
			expr.lessEquals(
					Evento.model().ORA_REGISTRAZIONE,
					this.searchForm.getDataRicerca());
		}
		
		if(this.searchForm.getSeverita()!=null){
			TipoSeverita tipoSev = TipoSeverita.toEnumConstant(this.searchForm.getSeverita());
			if(tipoSev!=null){
				try{
					expr.lessEquals(Evento.model().SEVERITA, SeveritaConverter.toIntValue(tipoSev));
				}catch(Exception e){
					throw new ExpressionException(e.getMessage(),e);
				}
			}
		}
		
		if(StringUtils.isNotEmpty(this.searchForm.getTipo())){
			expr.equals(Evento.model().TIPO, this.searchForm.getTipo().trim());
		}
		
		if(StringUtils.isNotEmpty(this.searchForm.getCodice())){
			expr.equals(Evento.model().CODICE, this.searchForm.getCodice().trim());
		}
		
		if (StringUtils.isNotEmpty(this.searchForm.getIdConfigurazione())) {
			
			CaseSensitiveMatch caseSensitiveMatch = CaseSensitiveMatch.valueOf(this.searchForm.getCaseSensitiveType());
			TipoMatch match = TipoMatch.valueOf(this.searchForm.getMatchingType());
			if (TipoMatch.LIKE.equals(match)) {
				if(CaseSensitiveMatch.SENSITIVE.equals(caseSensitiveMatch)){
					expr.like(Evento.model().ID_CONFIGURAZIONE, this.searchForm.getIdConfigurazione().trim(),LikeMode.ANYWHERE);
				}
				else{
					expr.ilike(Evento.model().ID_CONFIGURAZIONE, this.searchForm.getIdConfigurazione().trim(),LikeMode.ANYWHERE);
				}
			} else {				
				if(CaseSensitiveMatch.SENSITIVE.equals(caseSensitiveMatch)){
					expr.equals(Evento.model().ID_CONFIGURAZIONE,	this.searchForm.getIdConfigurazione().trim());
				}
				else{
					expr.ilike(Evento.model().ID_CONFIGURAZIONE,	this.searchForm.getIdConfigurazione().trim(),LikeMode.EXACT);
				}
			}
			
		}

		if (StringUtils.isNotEmpty(this.searchForm.getIdCluster())) {
			
			CaseSensitiveMatch caseSensitiveMatch = CaseSensitiveMatch.valueOf(this.searchForm.getCaseSensitiveType());
			TipoMatch match = TipoMatch.valueOf(this.searchForm.getMatchingType());
			if (TipoMatch.LIKE.equals(match)) {
				if(CaseSensitiveMatch.SENSITIVE.equals(caseSensitiveMatch)){
					expr.like(Evento.model().CLUSTER_ID, this.searchForm.getIdCluster().trim(),LikeMode.ANYWHERE);
				}
				else{
					expr.ilike(Evento.model().CLUSTER_ID, this.searchForm.getIdCluster().trim(),LikeMode.ANYWHERE);
				}
			} else {				
				if(CaseSensitiveMatch.SENSITIVE.equals(caseSensitiveMatch)){
					expr.equals(Evento.model().CLUSTER_ID,	this.searchForm.getIdCluster().trim());
				}
				else{
					expr.ilike(Evento.model().CLUSTER_ID,	this.searchForm.getIdCluster().trim(),LikeMode.EXACT);
				}
			}
			
		}
		else if (StringUtils.isNotEmpty(this.searchForm.getCanale())) {
			
			List<String> listId = Utility.getNodi(this.searchForm.getCanale());
			if(listId!=null && !listId.isEmpty()) {
				expr.and().in(Evento.model().CLUSTER_ID, listId);
			}
			else {
				expr.and().equals(Evento.model().CLUSTER_ID, "--"); // non esistente volutamente
			}
		}


		return expr;
	}

	@Override
	public boolean isTimeoutEvent() {
		return this.timeoutEvent;
	}
}
