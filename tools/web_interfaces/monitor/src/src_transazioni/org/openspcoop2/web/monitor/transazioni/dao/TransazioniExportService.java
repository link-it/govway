package org.openspcoop2.web.monitor.transazioni.dao;

import it.link.pdd.core.DAO;
import org.openspcoop2.core.commons.dao.DAOFactory;
import it.link.pdd.core.transazioni.TransazioneExport;
import it.link.pdd.core.transazioni.constants.DeleteState;
import it.link.pdd.core.transazioni.constants.ExportState;
import it.link.pdd.core.transazioni.dao.IDBTransazioneExportServiceSearch;
import it.link.pdd.core.transazioni.dao.IServiceManager;
import it.link.pdd.core.transazioni.dao.ITransazioneExportService;
import it.link.pdd.core.transazioni.dao.ITransazioneExportServiceSearch;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.openspcoop2.generic_project.beans.NonNegativeNumber;
import org.openspcoop2.generic_project.exception.ExpressionException;
import org.openspcoop2.generic_project.exception.ExpressionNotImplementedException;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.generic_project.exception.NotImplementedException;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.generic_project.expression.IExpression;
import org.openspcoop2.generic_project.expression.IPaginatedExpression;
import org.openspcoop2.generic_project.expression.SortOrder;

public class TransazioniExportService implements ITransazioniExportService {

	private transient Logger log = null;

	private ITransazioneExportServiceSearch transazioneExportSearchDAO;
	private ITransazioneExportService transazioneExportDAO;
	private IServiceManager transazioniServiceManager;

	public TransazioniExportService() {
		this.log =  LoggerManager.getPddMonitorSqlLogger();
		try {
			// init Service Manager utenti
			this.transazioniServiceManager = (it.link.pdd.core.transazioni.dao.IServiceManager) DAOFactory
					.getInstance(this.log).getServiceManager(DAO.TRANSAZIONI,this.log);
			this.transazioneExportDAO = this.transazioniServiceManager
					.getTransazioneExportService();
			this.transazioneExportSearchDAO = this.transazioniServiceManager
					.getTransazioneExportServiceSearch();
		} catch (Exception e) {
			this.log.error(e.getMessage(), e);
		}
	}
	
	
	public TransazioniExportService(Connection con, boolean autoCommit, Logger log) {
		this.log = log;
		try {
			// init Service Manager utenti
			this.transazioniServiceManager = (it.link.pdd.core.transazioni.dao.IServiceManager) DAOFactory
					.getInstance(this.log).getServiceManager(DAO.TRANSAZIONI,con,autoCommit,this.log);
			this.transazioneExportDAO = this.transazioniServiceManager
					.getTransazioneExportService();
			this.transazioneExportSearchDAO = this.transazioniServiceManager
					.getTransazioneExportServiceSearch();
		} catch (Exception e) {
			this.log.error(e.getMessage(), e);
		}
	}
	
	public TransazioniExportService(Logger log) {
		this.log = log;
		try {
			// init Service Manager utenti
			this.transazioniServiceManager = (it.link.pdd.core.transazioni.dao.IServiceManager) DAOFactory
					.getInstance(this.log).getServiceManager(DAO.TRANSAZIONI,this.log);
			this.transazioneExportDAO = this.transazioniServiceManager
					.getTransazioneExportService();
			this.transazioneExportSearchDAO = this.transazioniServiceManager
					.getTransazioneExportServiceSearch();
		} catch (Exception e) {
			this.log.error(e.getMessage(), e);
		}
	}


	@Override
	public List<TransazioneExport> findAll(int start, int limit) {
		try {
			this.log.info("Find All");
			IPaginatedExpression pagExpr = this.transazioneExportSearchDAO
					.newPaginatedExpression();
			pagExpr.offset(start).limit(limit);

			return this.transazioneExportSearchDAO.findAll(pagExpr);
		} catch (Exception e) {
			this.log.error(e.getMessage(), e);
		}
		return new ArrayList<TransazioneExport>();
	}

	@Override
	public int totalCount() {
		try {
			this.log.info("Count");
			IExpression expr = this.transazioneExportSearchDAO.newExpression();

			NonNegativeNumber nnn = this.transazioneExportSearchDAO.count(expr);

			return nnn != null ? new Long(nnn.longValue()).intValue() : 0;
		} catch (Exception e) {
			this.log.error(e.getMessage(), e);
		}
		return 0;
	}

	@Override
	public void store(TransazioneExport obj) throws Exception {
		try {
			this.log.info("Store");
			if (obj.getId() == -1)
				this.transazioneExportDAO.create(obj);
			else
				this.transazioneExportDAO.update(obj);
		} catch (Exception e) {
			this.log.error(e.getMessage(), e);
			throw e;
		}
	}

	@Override
	public void deleteById(Long key) {
		try {
			this.log.info("Delete By Id [" + key + "]");
			TransazioneExport obj = this.findById(key);
			this.transazioneExportDAO.delete(obj);
		} catch (Exception e) {
			this.log.error(e.getMessage(), e);
		}
	}

	@Override
	public void delete(TransazioneExport obj) throws Exception {
		try {
			this.log.info("Delete: " + obj.getId());
			this.transazioneExportDAO.delete(obj);
		} catch (Exception e) {
			this.log.error(e.getMessage(), e);
			throw e;
		}
	}

	@Override
	public void deleteAll() throws Exception {
		throw new Exception("Metodo non implementato.");
	}

	@Override
	public TransazioneExport findById(Long key) {
		try {
			this.log.info("Find By Id: [" + key + "]");
			return ((IDBTransazioneExportServiceSearch) this.transazioneExportSearchDAO)
					.get(key);
		} catch (Exception e) {
			this.log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public List<TransazioneExport> findAll() {
		try {
			this.log.info("Find All");
			IPaginatedExpression pagExpr = this.transazioneExportSearchDAO
					.newPaginatedExpression();
			return this.transazioneExportSearchDAO.findAll(pagExpr);
		} catch (Exception e) {
			this.log.error(e.getMessage(), e);
		}
		return new ArrayList<TransazioneExport>();
	}

	//
	// private Query createQuery(boolean isCount) {
	//
	// StringBuffer sb = new StringBuffer();
	//
	// if (isCount)
	// sb.append("SELECT count(t) FROM TransazioniExport t ");
	// else
	// sb.append("SELECT t FROM TransazioniExport t ");
	//
	// Query q = this.em.createQuery(sb.toString());
	//
	// return q;
	// }

	@Override
	public TransazioneExport getByIntervallo(Date dataInizio, Date dataFine) {
		TransazioneExport t = null;
		try {
			this.log
					.info("Find By Intervallo Date [inizio: " + dataInizio
							+ " fine: " + dataFine + " ]");
			IExpression expr = this.transazioneExportSearchDAO.newExpression();
			expr.equals(TransazioneExport.model().INTERVALLO_INIZIO, dataInizio);
			expr.and().equals(TransazioneExport.model().INTERVALLO_FINE,
					dataFine);

			t = this.transazioneExportSearchDAO.find(expr);

 			// "select t from TransazioniExport t where t.intervalloInizio=:dataInizio and t.intervalloFine=:dataFine")
			// .setParameter("dataInizio", dataInizio)
			// .setParameter("dataFine", dataFine).getSingleResult();
		} catch (NotFoundException nre) {
			t = new TransazioneExport();
			t.setIntervalloInizio(dataInizio);
			t.setIntervalloFine(dataFine);
		} catch (Exception e) {
			this.log.error(e.getMessage(), e);
		}

		return t;
	}

	@Override
	public TransazioneExport getOldestForDelete() throws Exception {

		this.log.info("Find By Oldest For Delete");

		IExpression expr;
		try {
			expr = this.transazioneExportSearchDAO.newExpression();

			expr.equals(TransazioneExport.model().EXPORT_STATE,
					ExportState.COMPLETED);
			expr.and().notEquals(TransazioneExport.model().DELETE_STATE,
					DeleteState.COMPLETED);
			expr.sortOrder(SortOrder.ASC).addOrder(TransazioneExport.model().EXPORT_TIME_START)
					;

			IPaginatedExpression pagExpr = this.transazioneExportSearchDAO
					.toPaginatedExpression(expr);
			pagExpr.limit(1);

			List<TransazioneExport> lst = this.transazioneExportSearchDAO
					.findAll(pagExpr);

 
			// "SELECT t from TransazioniExport t WHERE t.exportState=:export_state AND"
			// +
			// " t.deleteState<>:delete_state ORDER BY t.exportTimeStart ASC ")
			// .setParameter("export_state", ExportState.completed)
			// .setParameter("delete_state", DeleteState.completed)
			// .setMaxResults(1).getSingleResult();

			if (lst != null && lst.size() > 0) {
				return lst.get(0);
			}

		} catch (ServiceException e) {
			this.log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			this.log.error(e.getMessage(), e);
		} catch (ExpressionNotImplementedException e) {
			this.log.error(e.getMessage(), e);
		} catch (ExpressionException e) {
			this.log.error(e.getMessage(), e);
		}
		return null;

	}

}
