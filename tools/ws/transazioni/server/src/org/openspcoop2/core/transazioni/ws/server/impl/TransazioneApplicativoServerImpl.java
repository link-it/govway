/*
 * GovWay - A customizable API Gateway
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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

package org.openspcoop2.core.transazioni.ws.server.impl;

import org.openspcoop2.core.transazioni.ws.server.filter.SearchFilterTransazioneApplicativoServer;
import org.openspcoop2.generic_project.expression.IExpression;
import org.openspcoop2.generic_project.expression.IPaginatedExpression;
import org.openspcoop2.generic_project.expression.SortOrder;
import org.openspcoop2.generic_project.exception.ExpressionNotImplementedException;
import org.openspcoop2.generic_project.exception.ExpressionException;


import java.util.List;
import org.openspcoop2.core.transazioni.ws.server.config.Constants;
import org.openspcoop2.core.transazioni.ws.server.config.DriverTransazioni;
import org.openspcoop2.core.transazioni.ws.server.config.LoggerProperties;
import org.openspcoop2.core.transazioni.TransazioneApplicativoServer;

import org.openspcoop2.core.transazioni.ws.server.exception.TransazioniServiceException_Exception;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.core.transazioni.ws.server.exception.TransazioniNotFoundException_Exception;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.core.transazioni.ws.server.exception.TransazioniMultipleResultException_Exception;
import org.openspcoop2.generic_project.exception.MultipleResultException;
import org.openspcoop2.core.transazioni.ws.server.exception.TransazioniNotImplementedException_Exception;
import org.openspcoop2.generic_project.exception.NotImplementedException;
import org.openspcoop2.core.transazioni.ws.server.exception.TransazioniNotAuthorizedException_Exception;
import org.openspcoop2.generic_project.exception.NotAuthorizedException;

import org.openspcoop2.core.transazioni.ws.server.TransazioneApplicativoServerSearch;

/**     
 * TransazioneApplicativoServerImpl
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class TransazioneApplicativoServerImpl extends BaseImpl  implements TransazioneApplicativoServerSearch {

	private DriverTransazioni transazioniService;

	public TransazioneApplicativoServerImpl() {
		super();
		try {
			this.transazioniService = DriverTransazioni.getInstance();
			LoggerProperties.getLoggerWS().info("Inizializzazione TransazioneApplicativo Service effettuata con successo");
		} catch (Exception e) {
			LoggerProperties.getLoggerWS().error("Errore durante l'inizializzazione del TransazioneApplicativo Service",  e);
		}
	}
	
	private org.openspcoop2.core.transazioni.dao.ITransazioneApplicativoServerService getService() throws Exception{
		if(this.transazioniService==null){
			throw new Exception("Service isn't correct initialized");
		}
		return this.transazioniService.getDriver().getTransazioneApplicativoServerService();
	}



	private IExpression toExpression(SearchFilterTransazioneApplicativoServer filter) throws ServiceException, NotImplementedException, Exception{
		IExpression exp = this.getService().newExpression();
		exp.and();

		if(filter.getIdTransazione()!= null) {
			exp.equals(TransazioneApplicativoServer.model().ID_TRANSAZIONE, filter.getIdTransazione());
		}
		if(filter.getServizioApplicativoErogatore()!= null) {
			exp.equals(TransazioneApplicativoServer.model().SERVIZIO_APPLICATIVO_EROGATORE, filter.getServizioApplicativoErogatore());
		}
		if(filter.getDataUscitaRichiestaMin()!= null) {
			exp.greaterEquals(TransazioneApplicativoServer.model().DATA_USCITA_RICHIESTA, filter.getDataUscitaRichiestaMin());
		}
		if(filter.getDataUscitaRichiestaMax()!= null) {
			exp.lessEquals(TransazioneApplicativoServer.model().DATA_USCITA_RICHIESTA, filter.getDataUscitaRichiestaMax());
		}
		if(filter.getDataAccettazioneRispostaMin()!= null) {
			exp.greaterEquals(TransazioneApplicativoServer.model().DATA_ACCETTAZIONE_RISPOSTA, filter.getDataAccettazioneRispostaMin());
		}
		if(filter.getDataAccettazioneRispostaMax()!= null) {
			exp.lessEquals(TransazioneApplicativoServer.model().DATA_ACCETTAZIONE_RISPOSTA, filter.getDataAccettazioneRispostaMax());
		}
		if(filter.getDataIngressoRispostaMin()!= null) {
			exp.greaterEquals(TransazioneApplicativoServer.model().DATA_INGRESSO_RISPOSTA, filter.getDataIngressoRispostaMin());
		}
		if(filter.getDataIngressoRispostaMax()!= null) {
			exp.lessEquals(TransazioneApplicativoServer.model().DATA_INGRESSO_RISPOSTA, filter.getDataIngressoRispostaMax());
		}
		if(filter.getRichiestaUscitaBytesMin()!= null) {
			exp.greaterEquals(TransazioneApplicativoServer.model().RICHIESTA_USCITA_BYTES, filter.getRichiestaUscitaBytesMin());
		}
		if(filter.getRichiestaUscitaBytesMax()!= null) {
			exp.lessEquals(TransazioneApplicativoServer.model().RICHIESTA_USCITA_BYTES, filter.getRichiestaUscitaBytesMax());
		}
		if(filter.getRispostaIngressoBytesMin()!= null) {
			exp.greaterEquals(TransazioneApplicativoServer.model().RISPOSTA_INGRESSO_BYTES, filter.getRispostaIngressoBytesMin());
		}
		if(filter.getRispostaIngressoBytesMax()!= null) {
			exp.lessEquals(TransazioneApplicativoServer.model().RISPOSTA_INGRESSO_BYTES, filter.getRispostaIngressoBytesMax());
		}
		if(filter.getCodiceRisposta()!= null) {
			exp.equals(TransazioneApplicativoServer.model().CODICE_RISPOSTA, filter.getCodiceRisposta());
		}
		if(filter.getDataPrimoTentativoMin()!= null) {
			exp.greaterEquals(TransazioneApplicativoServer.model().DATA_PRIMO_TENTATIVO, filter.getDataPrimoTentativoMin());
		}
		if(filter.getDataPrimoTentativoMax()!= null) {
			exp.lessEquals(TransazioneApplicativoServer.model().DATA_PRIMO_TENTATIVO, filter.getDataPrimoTentativoMax());
		}
		if(filter.getDataUltimoErroreMin()!= null) {
			exp.greaterEquals(TransazioneApplicativoServer.model().DATA_ULTIMO_ERRORE, filter.getDataUltimoErroreMin());
		}
		if(filter.getDataUltimoErroreMax()!= null) {
			exp.lessEquals(TransazioneApplicativoServer.model().DATA_ULTIMO_ERRORE, filter.getDataUltimoErroreMax());
		}
		if(filter.getCodiceRispostaUltimoErrore()!= null) {
			exp.equals(TransazioneApplicativoServer.model().CODICE_RISPOSTA_ULTIMO_ERRORE, filter.getCodiceRispostaUltimoErrore());
		}

		return exp;
	}

	private IPaginatedExpression toPaginatedExpression(SearchFilterTransazioneApplicativoServer filter) throws ServiceException, NotImplementedException, Exception{
		IPaginatedExpression pagExp = this.getService().toPaginatedExpression(this.toExpression(filter));
		if(filter.getLimit()!=null) {
			pagExp.limit(filter.getLimit());
		}
		if(filter.getOffset()!=null) {
			pagExp.offset(filter.getOffset());
		}
		pagExp.sortOrder(SortOrder.ASC);
		pagExp.addOrder(TransazioneApplicativoServer.model().ID_TRANSAZIONE);
		pagExp.addOrder(TransazioneApplicativoServer.model().SERVIZIO_APPLICATIVO_EROGATORE);
		return pagExp;
	}



	@Override
	public List<TransazioneApplicativoServer> findAll(SearchFilterTransazioneApplicativoServer filter) throws TransazioniServiceException_Exception,TransazioniNotImplementedException_Exception,TransazioniNotAuthorizedException_Exception {
		try{
		
			checkInitDriverTransazioni(this.transazioniService);
			this.logStartMethod("findAll", filter);
			this.authorize(true);
			IPaginatedExpression pagExp = this.toPaginatedExpression(filter);	
			List<TransazioneApplicativoServer> result = this.getService().findAll(pagExp);
			this.logEndMethod("findAll", result);
			return result;
			
		} catch(NotAuthorizedException e){
			throw throwNotAuthorizedException("findAll", e, Constants.CODE_NOT_AUTHORIZED_EXCEPTION, filter);
		} catch (NotImplementedException e) {
			throw throwNotImplementedException("findAll", e, Constants.CODE_NOT_IMPLEMENTED_EXCEPTION, filter);
		} catch (ServiceException e) {
			throw throwServiceException("findAll", e, Constants.CODE_SERVICE_EXCEPTION, filter);
		} catch (ExpressionNotImplementedException e) {
			throw throwServiceException("findAll", e, Constants.CODE_EXPRESSION_NOT_IMPLEMENTED_EXCEPTION, filter);
		} catch (ExpressionException e) {
			throw throwServiceException("findAll", e, Constants.CODE_EXPRESSION_EXCEPTION, filter);
		} catch (Exception e){
			throw throwServiceException("findAll", e, Constants.CODE_ERROR_GENERAL_EXCEPTION, filter);
		}
		
	}

	@Override
	public TransazioneApplicativoServer find(SearchFilterTransazioneApplicativoServer filter) throws TransazioniServiceException_Exception,TransazioniNotFoundException_Exception,TransazioniMultipleResultException_Exception,TransazioniNotImplementedException_Exception,TransazioniNotAuthorizedException_Exception {
		try{
		
			checkInitDriverTransazioni(this.transazioniService);
			this.logStartMethod("find", filter);
			this.authorize(true);
			IExpression exp = this.toExpression(filter);
			TransazioneApplicativoServer result = this.getService().find(exp);
			this.logEndMethod("find", result);
			return result;
			
		} catch(NotAuthorizedException e){
			throw throwNotAuthorizedException("find", e, Constants.CODE_NOT_AUTHORIZED_EXCEPTION, filter);
		} catch (NotImplementedException e) {
			throw throwNotImplementedException("find", e, Constants.CODE_NOT_IMPLEMENTED_EXCEPTION, filter);
		} catch (ServiceException e) {
			throw throwServiceException("find", e, Constants.CODE_SERVICE_EXCEPTION, filter);
		} catch (NotFoundException e) {
			throw throwNotFoundException("find", e, Constants.CODE_NOT_FOUND_EXCEPTION, filter);
		} catch (MultipleResultException e) {
			throw throwMultipleResultException("find", e, Constants.CODE_MULTIPLE_RESULT_EXCEPTION, filter);
		} catch (ExpressionNotImplementedException e) {
			throw throwServiceException("find", e, Constants.CODE_EXPRESSION_NOT_IMPLEMENTED_EXCEPTION, filter);
		} catch (ExpressionException e) {
			throw throwServiceException("find", e, Constants.CODE_EXPRESSION_EXCEPTION, filter);
		} catch (Exception e){
			throw throwServiceException("find", e, Constants.CODE_ERROR_GENERAL_EXCEPTION, filter);
		}
	}

	@Override
	public long count(SearchFilterTransazioneApplicativoServer filter) throws TransazioniServiceException_Exception,TransazioniNotImplementedException_Exception,TransazioniNotAuthorizedException_Exception {
		try{
		
			checkInitDriverTransazioni(this.transazioniService);
			this.logStartMethod("count", filter);
			this.authorize(true);
			IExpression exp = this.toExpression(filter);
			long result = this.getService().count(exp).longValue();
			this.logEndMethod("count", result);
			return result;
			
		} catch(NotAuthorizedException e){
			throw throwNotAuthorizedException("count", e, Constants.CODE_NOT_AUTHORIZED_EXCEPTION, filter);
		} catch (NotImplementedException e) {
			throw throwNotImplementedException("count", e, Constants.CODE_NOT_IMPLEMENTED_EXCEPTION, filter);
		} catch (ServiceException e) {
			throw throwServiceException("count", e, Constants.CODE_SERVICE_EXCEPTION, filter);
		} catch (ExpressionNotImplementedException e) {
			throw throwServiceException("count", e, Constants.CODE_EXPRESSION_NOT_IMPLEMENTED_EXCEPTION, filter);
		} catch (ExpressionException e) {
			throw throwServiceException("count", e, Constants.CODE_EXPRESSION_EXCEPTION, filter);
		} catch (Exception e){
			throw throwServiceException("count", e, Constants.CODE_ERROR_GENERAL_EXCEPTION, filter);
		}
	}

	@Override
	public TransazioneApplicativoServer get(org.openspcoop2.core.transazioni.IdTransazioneApplicativoServer id) throws TransazioniServiceException_Exception,TransazioniNotFoundException_Exception,TransazioniMultipleResultException_Exception,TransazioniNotImplementedException_Exception,TransazioniNotAuthorizedException_Exception {
		try{
		
			checkInitDriverTransazioni(this.transazioniService);
			this.logStartMethod("get", id);
			this.authorize(true);
			TransazioneApplicativoServer result = this.getService().get(id);
			this.logEndMethod("get", result);
			return result;
			
		} catch(NotAuthorizedException e){
			throw throwNotAuthorizedException("get", e, Constants.CODE_NOT_AUTHORIZED_EXCEPTION, id);
		} catch (NotImplementedException e) {
			throw throwNotImplementedException("get", e, Constants.CODE_NOT_IMPLEMENTED_EXCEPTION, id);
		} catch (ServiceException e) {
			throw throwServiceException("get", e, Constants.CODE_SERVICE_EXCEPTION, id);
		} catch (NotFoundException e) {
			throw throwNotFoundException("get", e, Constants.CODE_NOT_FOUND_EXCEPTION, id);
		} catch (MultipleResultException e) {
			throw throwMultipleResultException("get", e, Constants.CODE_MULTIPLE_RESULT_EXCEPTION, id);
		} catch (Exception e){
			throw throwServiceException("get", e, Constants.CODE_ERROR_GENERAL_EXCEPTION, id);
		}
	}
	
	@Override
	public boolean exists(org.openspcoop2.core.transazioni.IdTransazioneApplicativoServer id) throws TransazioniServiceException_Exception,TransazioniMultipleResultException_Exception,TransazioniNotImplementedException_Exception,TransazioniNotAuthorizedException_Exception {
		try{
		
			checkInitDriverTransazioni(this.transazioniService);
			this.logStartMethod("exists", id);
			this.authorize(true);
			boolean result = this.getService().exists(id);
			this.logEndMethod("exists", result);
			return result;
			
		} catch(NotAuthorizedException e){
			throw throwNotAuthorizedException("exists", e, Constants.CODE_NOT_AUTHORIZED_EXCEPTION, id);
		} catch (NotImplementedException e) {
			throw throwNotImplementedException("exists", e, Constants.CODE_NOT_IMPLEMENTED_EXCEPTION, id);
		} catch (ServiceException e) {
			throw throwServiceException("exists", e, Constants.CODE_SERVICE_EXCEPTION, id);
		} catch (MultipleResultException e) {
			throw throwMultipleResultException("exists", e, Constants.CODE_MULTIPLE_RESULT_EXCEPTION, id);
		} catch (Exception e){
			throw throwServiceException("exists", e, Constants.CODE_ERROR_GENERAL_EXCEPTION, id);
		}
	}

	@Override
	public List<org.openspcoop2.core.transazioni.IdTransazioneApplicativoServer> findAllIds(SearchFilterTransazioneApplicativoServer filter) throws TransazioniServiceException_Exception,TransazioniNotImplementedException_Exception,TransazioniNotAuthorizedException_Exception {
		try{
		
			checkInitDriverTransazioni(this.transazioniService);
			this.logStartMethod("findAllIds", filter);
			this.authorize(true);
			IPaginatedExpression pagExp = this.toPaginatedExpression(filter);	
			List<org.openspcoop2.core.transazioni.IdTransazioneApplicativoServer> result = this.getService().findAllIds(pagExp);
			this.logEndMethod("findAllIds", result);
			return result;
			
		} catch(NotAuthorizedException e){
			throw throwNotAuthorizedException("findAllIds", e, Constants.CODE_NOT_AUTHORIZED_EXCEPTION, filter);
		} catch (NotImplementedException e) {
			throw throwNotImplementedException("findAllIds", e, Constants.CODE_NOT_IMPLEMENTED_EXCEPTION, filter);
		} catch (ServiceException e) {
			throw throwServiceException("findAllIds", e, Constants.CODE_SERVICE_EXCEPTION, filter);
		} catch (ExpressionNotImplementedException e) {
			throw throwServiceException("findAllIds", e, Constants.CODE_EXPRESSION_NOT_IMPLEMENTED_EXCEPTION, filter);
		} catch (ExpressionException e) {
			throw throwServiceException("findAllIds", e, Constants.CODE_EXPRESSION_EXCEPTION, filter);
		} catch (Exception e){
			throw throwServiceException("findAllIds", e, Constants.CODE_ERROR_GENERAL_EXCEPTION, filter);
		}
	}

	



}
