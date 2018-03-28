package org.openspcoop2.core.transazioni.ws.server.impl;

import org.openspcoop2.core.transazioni.ws.server.filter.SearchFilterDumpMessaggio;
import org.openspcoop2.generic_project.expression.IExpression;
import org.openspcoop2.generic_project.expression.IPaginatedExpression;
import org.openspcoop2.generic_project.expression.SortOrder;
import org.openspcoop2.generic_project.exception.ExpressionNotImplementedException;
import org.openspcoop2.generic_project.exception.ExpressionException;


import java.util.List;
import org.openspcoop2.core.transazioni.ws.server.config.Constants;
import org.openspcoop2.core.transazioni.ws.server.config.DriverTransazioni;
import org.openspcoop2.core.transazioni.ws.server.config.LoggerProperties;
import org.openspcoop2.core.transazioni.DumpMessaggio;

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

import org.openspcoop2.core.transazioni.ws.server.DumpMessaggioSearch;

/**     
 * DumpMessaggioImpl
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class DumpMessaggioImpl extends BaseImpl  implements DumpMessaggioSearch {

	private DriverTransazioni transazioniService;

	public DumpMessaggioImpl() {
		super();
		try {
			this.transazioniService = DriverTransazioni.getInstance();
			LoggerProperties.getLoggerWS().info("Inizializzazione Transazioni Service effettuata con successo");
		} catch (Exception e) {
			LoggerProperties.getLoggerWS().error("Errore durante l'inizializzazione del Transazioni Service",  e);
		}
	}
	
	private org.openspcoop2.core.transazioni.dao.IDumpMessaggioService getService() throws Exception{
		if(this.transazioniService==null){
			throw new Exception("Service isn't correct initialized");
		}
		return this.transazioniService.getDriver().getDumpMessaggioService();
	}


	private IExpression toExpression(SearchFilterDumpMessaggio filter) throws ServiceException, NotImplementedException, Exception{
		IExpression exp = this.getService().newExpression();
		exp.and();

		if(filter.getIdTransazione()!= null) {
			exp.equals(DumpMessaggio.model().ID_TRANSAZIONE, filter.getIdTransazione());
		}
		if(filter.getTipoMessaggio()!= null) {
			exp.equals(DumpMessaggio.model().TIPO_MESSAGGIO, filter.getTipoMessaggio());
		}
		if(filter.getContentType()!= null) {
			exp.equals(DumpMessaggio.model().CONTENT_TYPE, filter.getContentType());
		}
		
		if(filter.getMultipartContentId()!= null) {
			exp.equals(DumpMessaggio.model().MULTIPART_CONTENT_ID, filter.getMultipartContentId());
		}
		if(filter.getMultipartContentLocation()!= null) {
			exp.equals(DumpMessaggio.model().MULTIPART_CONTENT_LOCATION, filter.getMultipartContentLocation());
		}
		if(filter.getMultipartContentType()!= null) {
			exp.equals(DumpMessaggio.model().MULTIPART_CONTENT_TYPE, filter.getMultipartContentType());
		}

		return exp;
	}

	private IPaginatedExpression toPaginatedExpression(SearchFilterDumpMessaggio filter) throws ServiceException, NotImplementedException, Exception{
		IPaginatedExpression pagExp = this.getService().toPaginatedExpression(this.toExpression(filter));
		if(filter.getLimit()!=null) {
			pagExp.limit(filter.getLimit());
		}
		if(filter.getOffset()!=null) {
			pagExp.offset(filter.getOffset());
		}
		if(filter.getDescOrder()!=null && filter.getDescOrder()) {
			pagExp.sortOrder(SortOrder.DESC);
		}
		else {
			pagExp.sortOrder(SortOrder.ASC);
		}
		pagExp.addOrder(DumpMessaggio.model().ID_TRANSAZIONE);
		pagExp.addOrder(DumpMessaggio.model().TIPO_MESSAGGIO);
		
		return pagExp;
	}



	@Override
	public List<DumpMessaggio> findAll(SearchFilterDumpMessaggio filter) throws TransazioniServiceException_Exception,TransazioniNotImplementedException_Exception,TransazioniNotAuthorizedException_Exception {
		try{
		
			checkInitDriverTransazioni(this.transazioniService);
			this.logStartMethod("findAll", filter);
			this.authorize(true);
			IPaginatedExpression pagExp = this.toPaginatedExpression(filter);	
			List<DumpMessaggio> result = this.getService().findAll(pagExp);
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
	public DumpMessaggio find(SearchFilterDumpMessaggio filter) throws TransazioniServiceException_Exception,TransazioniNotFoundException_Exception,TransazioniMultipleResultException_Exception,TransazioniNotImplementedException_Exception,TransazioniNotAuthorizedException_Exception {
		try{
		
			checkInitDriverTransazioni(this.transazioniService);
			this.logStartMethod("find", filter);
			this.authorize(true);
			IExpression exp = this.toExpression(filter);
			DumpMessaggio result = this.getService().find(exp);
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
	public long count(SearchFilterDumpMessaggio filter) throws TransazioniServiceException_Exception,TransazioniNotImplementedException_Exception,TransazioniNotAuthorizedException_Exception {
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
	public DumpMessaggio get(org.openspcoop2.core.transazioni.IdDumpMessaggio id) throws TransazioniServiceException_Exception,TransazioniNotFoundException_Exception,TransazioniMultipleResultException_Exception,TransazioniNotImplementedException_Exception,TransazioniNotAuthorizedException_Exception {
		try{
		
			checkInitDriverTransazioni(this.transazioniService);
			this.logStartMethod("get", id);
			this.authorize(true);
			DumpMessaggio result = this.getService().get(id);
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
	public boolean exists(org.openspcoop2.core.transazioni.IdDumpMessaggio id) throws TransazioniServiceException_Exception,TransazioniMultipleResultException_Exception,TransazioniNotImplementedException_Exception,TransazioniNotAuthorizedException_Exception {
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
	public List<org.openspcoop2.core.transazioni.IdDumpMessaggio> findAllIds(SearchFilterDumpMessaggio filter) throws TransazioniServiceException_Exception,TransazioniNotImplementedException_Exception,TransazioniNotAuthorizedException_Exception {
		try{
		
			checkInitDriverTransazioni(this.transazioniService);
			this.logStartMethod("findAllIds", filter);
			this.authorize(true);
			IPaginatedExpression pagExp = this.toPaginatedExpression(filter);	
			List<org.openspcoop2.core.transazioni.IdDumpMessaggio> result = this.getService().findAllIds(pagExp);
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
