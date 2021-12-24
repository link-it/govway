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

package org.openspcoop2.web.monitor.transazioni.dao;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.commons.dao.DAOFactory;
import org.openspcoop2.core.transazioni.TransazioneApplicativoServer;
import org.openspcoop2.core.transazioni.dao.IDBTransazioneApplicativoServerServiceSearch;
import org.openspcoop2.generic_project.beans.NonNegativeNumber;
import org.openspcoop2.generic_project.exception.ExpressionException;
import org.openspcoop2.generic_project.exception.ExpressionNotImplementedException;
import org.openspcoop2.generic_project.exception.NotImplementedException;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.generic_project.expression.IExpression;
import org.openspcoop2.generic_project.expression.IPaginatedExpression;
import org.openspcoop2.generic_project.expression.SortOrder;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;
import org.openspcoop2.web.monitor.transazioni.bean.TransazioneApplicativoServerBean;
import org.slf4j.Logger;

/**
 * TransazioniApplicativoServerService
 * 
 *  
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TransazioniApplicativoServerService implements ITransazioniApplicativoServerService{ 
	
	
	private transient Logger log = null;
	private String idTransazione = null;
	private String protocollo = null;
	
	private DAOFactory daoFactory;
	private org.openspcoop2.core.transazioni.dao.IServiceManager transazioniServiceManager;
	private org.openspcoop2.core.transazioni.dao.ITransazioneApplicativoServerServiceSearch transazioniSASearch;
	
	
	public TransazioniApplicativoServerService() {
		this.log =  LoggerManager.getPddMonitorSqlLogger();
		try {

			this.daoFactory = DAOFactory.getInstance(this.log);
			
			this.transazioniServiceManager = (org.openspcoop2.core.transazioni.dao.IServiceManager) this.daoFactory.getServiceManager(org.openspcoop2.core.transazioni.utils.ProjectInfo.getInstance(),this.log);
			this.transazioniSASearch = this.transazioniServiceManager.getTransazioneApplicativoServerServiceSearch();
			
		} catch (Exception e) {
			this.log.error(e.getMessage(), e);
		}
	}
	protected TransazioniApplicativoServerService(DAOFactory daoFactory, org.openspcoop2.core.transazioni.dao.IServiceManager transazioniServiceManager, 
			org.openspcoop2.core.transazioni.dao.ITransazioneApplicativoServerServiceSearch transazioniSASearch) {
		this.log =  LoggerManager.getPddMonitorSqlLogger();
		try {
			this.daoFactory = daoFactory;
			this.transazioniServiceManager = transazioniServiceManager;
			this.transazioniSASearch = transazioniSASearch;
		} catch (Exception e) {
			this.log.error(e.getMessage(), e);
		}
	}
	

	@Override
	public List<TransazioneApplicativoServerBean> findAll(int start, int limit) {
		return this._findAll(start, limit);
	}

	@Override
	public int totalCount() {
		try {

			this.log.debug("Count Transazioni Applicativo Server per la Transazione ["+this.idTransazione+"]");

			IExpression expr = this.createFilter();

//			List<Index> forceIndexCount = this.getIndexCount();
//			if(forceIndexCount!=null && forceIndexCount.size()>0){
//				for (Index index : forceIndexCount) {
//					expr.addForceIndex(index);	
//				}
//			}
			
			NonNegativeNumber res = this.transazioniSASearch.count(expr);
			return (int) res.longValue();

		} catch (Exception e) {
			this.log.error(e.getMessage(), e);
		}
		return 0;
		
	}

	@Override
	public TransazioneApplicativoServerBean findById(Long key) {
		try {
			this.log.info("Find By Id: [" + key + "]");
			TransazioneApplicativoServer transazione = ((IDBTransazioneApplicativoServerServiceSearch) this.transazioniSASearch).get(key);
			transazione.setProtocollo(this.protocollo); 
			return new TransazioneApplicativoServerBean(transazione);
		} catch (Exception e) {
			this.log.error(e.getMessage(), e);
		}
		return null;
	}
	
	@Override
	public TransazioneApplicativoServerBean findByServizioApplicativoErogatore(String nomeServizioApplicativoErogatore) throws Exception {
		try {
			this.log.info("Find By Servizio Applicativo Erogatore: [" + nomeServizioApplicativoErogatore + "]");
			
			IExpression expr = this.createFilter();
			
			if(nomeServizioApplicativoErogatore == null) {
				expr.isNull(TransazioneApplicativoServer.model().SERVIZIO_APPLICATIVO_EROGATORE);
			} else {
				expr.equals(TransazioneApplicativoServer.model().SERVIZIO_APPLICATIVO_EROGATORE, nomeServizioApplicativoErogatore);
			}
			
			TransazioneApplicativoServer transazione = this.transazioniSASearch.find(expr);
			transazione.setProtocollo(this.protocollo); 
			return new TransazioneApplicativoServerBean(transazione);
		} catch (Exception e) {
			this.log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public List<TransazioneApplicativoServerBean> findAll() {
		return this._findAll(null, null);
	}
	
	private List<TransazioneApplicativoServerBean> _findAll(Integer start, Integer limit){
		List<TransazioneApplicativoServerBean> listaBean = new ArrayList<TransazioneApplicativoServerBean>();
		try {
			this.log.debug("Find All + Limit Applicativo Server per la Transazione ["+this.idTransazione+"]");

			IExpression expr = this.createFilter();
			
			expr.sortOrder(SortOrder.ASC).addOrder(TransazioneApplicativoServer.model().DATA_REGISTRAZIONE);

			IPaginatedExpression pagExpr = this.transazioniSASearch.toPaginatedExpression(expr);

			if(start != null)
				pagExpr.offset(start);
			
			if(limit != null)
				pagExpr.limit(limit);

//			List<Index> forceIndexFindAll = this.getIndexFindAll();
//			if(forceIndexFindAll!=null && forceIndexFindAll.size()>0){
//				for (Index index : forceIndexFindAll) {
//					pagExpr.addForceIndex(index);	
//				}
//			}
			
			List<TransazioneApplicativoServer> list = this.transazioniSASearch.findAll(pagExpr);
			if(list!= null && list.size() > 0)
				for (TransazioneApplicativoServer transazione : list) {
					
					transazione.setProtocollo(this.protocollo);
					
					TransazioneApplicativoServerBean bean = new TransazioneApplicativoServerBean(transazione);

					// Integrazione dei dati delle credenziali
//					bean.normalizeRichiedenteInfo(transazione, bean, this);
//					bean.normalizeOperazioneInfo(this.utilsServiceManager, this.log);
//					
//					this.normalizeInfoTransazioniFromCredenzialiMittenteGruppi(bean, transazione);
					
					listaBean.add(bean);
				}
		} catch (Exception e) {
			this.log.error(e.getMessage(), e);
		}
		return listaBean;
	}
	
	private IExpression createFilter() throws ServiceException, NotImplementedException, ExpressionNotImplementedException, ExpressionException {
		IExpression newExpression = this.transazioniSASearch.newExpression();
		
		if(this.idTransazione != null)
			newExpression.equals(TransazioneApplicativoServer.model().ID_TRANSAZIONE, this.idTransazione);
		
		return newExpression;
	}
	
	@Override
	public void store(TransazioneApplicativoServerBean obj) throws Exception {}

	@Override
	public void deleteById(Long key) {}

	@Override
	public void delete(TransazioneApplicativoServerBean obj) throws Exception {}

	@Override
	public void deleteAll() throws Exception {}

	public String getIdTransazione() {
		return this.idTransazione;
	}

	@Override
	public void setIdTransazione(String idTransazione) {
		this.idTransazione = idTransazione;
	}

	@Override
	public void setProtocollo(String protocollo) {
		this.protocollo = protocollo;		
	}

	public String getProtocollo() {
		return this.protocollo;
	}
}
