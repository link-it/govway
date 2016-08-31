/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it).
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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
package org.openspcoop2.core.config.ws.server.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.openspcoop2.core.config.IdSoggetto;
import org.openspcoop2.core.config.Soggetto;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.config.driver.FiltroRicercaSoggetti;
import org.openspcoop2.core.config.driver.IDriverConfigurazioneCRUD;
import org.openspcoop2.core.config.driver.IDriverConfigurazioneGet;
import org.openspcoop2.core.config.driver.db.DriverConfigurazioneDB;
import org.openspcoop2.core.config.ws.server.SoggettoCRUD;
import org.openspcoop2.core.config.ws.server.SoggettoSearch;
import org.openspcoop2.core.config.ws.server.beans.UseInfo;
import org.openspcoop2.core.config.ws.server.config.Constants;
import org.openspcoop2.core.config.ws.server.config.DriverConfigurazione;
import org.openspcoop2.core.config.ws.server.config.LoggerProperties;
import org.openspcoop2.core.config.ws.server.config.ServerProperties;
import org.openspcoop2.core.config.ws.server.exception.ConfigMultipleResultException_Exception;
import org.openspcoop2.core.config.ws.server.exception.ConfigNotAuthorizedException_Exception;
import org.openspcoop2.core.config.ws.server.exception.ConfigNotFoundException_Exception;
import org.openspcoop2.core.config.ws.server.exception.ConfigNotImplementedException_Exception;
import org.openspcoop2.core.config.ws.server.exception.ConfigServiceException_Exception;
import org.openspcoop2.core.config.ws.server.filter.SearchFilterSoggetto;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject;
import org.openspcoop2.generic_project.exception.ExpressionException;
import org.openspcoop2.generic_project.exception.ExpressionNotImplementedException;
import org.openspcoop2.generic_project.exception.MultipleResultException;
import org.openspcoop2.generic_project.exception.NotAuthorizedException;
import org.openspcoop2.generic_project.exception.NotImplementedException;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;
import org.openspcoop2.utils.sql.SQLQueryObjectCore;

/**     
 * SoggettoImpl
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class SoggettoImpl extends BaseImpl  implements SoggettoSearch, SoggettoCRUD {

	private DriverConfigurazione soggettoService = null; 

	public SoggettoImpl() {
		super();
		try {
			this.soggettoService = DriverConfigurazione.getInstance();
			LoggerProperties.getLoggerWS().info("Inizializzazione Soggetto Service effettuata con successo");
		} catch (Exception e) {
			LoggerProperties.getLoggerWS().error("Errore durante l'inizializzazione del Soggetto Service: Service non implementato",  e);
		}
	}
	

	
	
	private IDSoggetto convertToIdSoggetto(IdSoggetto id) throws DriverConfigurazioneException{
		return this.buildIdSoggetto(id.getTipo(), id.getNome());
	}
	private IDSoggetto buildIdSoggetto(String tipo, String nome) throws DriverConfigurazioneException{
		return new IDSoggetto(tipo, nome);
	}

	private IdSoggetto convertToIdSoggettoWS(IDSoggetto id) throws DriverConfigurazioneException{
		IdSoggetto idSoggetto = new IdSoggetto();
		idSoggetto.setTipo(id.getTipo());
		idSoggetto.setNome(id.getNome());
		return idSoggetto;
	}
	
	private List<IdSoggetto> readSoggettiIds(SearchFilterSoggetto filter, boolean paginated) throws ServiceException, NotImplementedException, Exception{
		List<IDSoggetto> listIds = this.readIds(filter, true);
		List<org.openspcoop2.core.config.IdSoggetto> listIdsWS = new ArrayList<IdSoggetto>();
		for (int i = 0; i < listIds.size(); i++) {
			listIdsWS.add(this.convertToIdSoggettoWS(listIds.get(i)));
		}
		return listIdsWS;
	}
	private List<Soggetto> readSoggetti(SearchFilterSoggetto filter, boolean paginated) throws ServiceException, NotImplementedException, Exception{
		List<Long> listIds = this.readLongIds(filter, true);
		List<Soggetto> listSoggetti = new ArrayList<Soggetto>();
		DriverConfigurazioneDB driverDB = ((DriverConfigurazioneDB)this.soggettoService.getDriver());
		for (int i = 0; i < listIds.size(); i++) {
			listSoggetti.add(driverDB.getSoggetto(listIds.get(0)));
		}
		return listSoggetti;
	}
	
	@SuppressWarnings("unchecked")
	private List<Long> readLongIds(SearchFilterSoggetto filter, boolean paginated) throws ServiceException, NotImplementedException, Exception{
		return (List<Long>) this.toList(filter, true, paginated);
	}
	@SuppressWarnings("unchecked")
	private List<IDSoggetto> readIds(SearchFilterSoggetto filter, boolean paginated) throws ServiceException, NotImplementedException, Exception{
		return (List<IDSoggetto>) this.toList(filter, false, paginated);
	}
	private List<?> toList(SearchFilterSoggetto filter, boolean idLong, boolean paginated) throws ServiceException, NotImplementedException, Exception{
		
		DriverConfigurazioneDB driverDB = ((DriverConfigurazioneDB)this.soggettoService.getDriver());
		
		ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(driverDB.getTipoDB());
		List<Class<?>> returnTypes = new ArrayList<Class<?>>();
		List<JDBCObject> paramTypes = new ArrayList<JDBCObject>();
		
		sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
		
		if(idLong){
			sqlQueryObject.addSelectField(CostantiDB.SOGGETTI, "id");
			returnTypes.add(Long.class);
		}
		
		if(!idLong || paginated){
			sqlQueryObject.addSelectField(CostantiDB.SOGGETTI, "tipo_soggetto");
			returnTypes.add(String.class);
			
			sqlQueryObject.addSelectField(CostantiDB.SOGGETTI, "nome_soggetto");
			returnTypes.add(String.class);
			
		}
		
		
		this.setFilter(sqlQueryObject, paramTypes, filter, paginated);
		
		
		List<List<Object>> listaRisultati = driverDB.readCustom(sqlQueryObject, returnTypes, paramTypes);
		
		if(idLong){
			List<Long> listIds = new ArrayList<Long>();
			for (List<Object> list : listaRisultati) {
				listIds.add((Long)list.get(0));
			}
			return listIds;
		}
		else{
			List<IDSoggetto> listIds = new ArrayList<IDSoggetto>();
			for (List<Object> list : listaRisultati) {
				String tipo = (String)list.get(0);
				String nome = (String)list.get(1);
				listIds.add(this.buildIdSoggetto(tipo, nome));
			}
			return listIds;
		}
		
	}
	
	private long toCount(SearchFilterSoggetto filter) throws ServiceException, NotImplementedException, Exception{
		
		DriverConfigurazioneDB driverDB = ((DriverConfigurazioneDB)this.soggettoService.getDriver());
		
		ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(driverDB.getTipoDB());
		List<Class<?>> returnTypes = new ArrayList<Class<?>>();
		List<JDBCObject> paramTypes = new ArrayList<JDBCObject>();
		
		sqlQueryObject.addFromTable(CostantiDB.SOGGETTI,CostantiDB.SOGGETTI);
		
		sqlQueryObject.addSelectCountField(CostantiDB.SOGGETTI, "id", "cont", true);
		returnTypes.add(Long.class);
		
		this.setFilter(sqlQueryObject, paramTypes, filter, false);
		
		List<List<Object>> listaRisultati = driverDB.readCustom(sqlQueryObject, returnTypes, paramTypes);
		
		return (Long) listaRisultati.get(0).get(0);
		
	}
	
	private void setFilter(ISQLQueryObject sqlQueryObjectParam, List<JDBCObject> paramTypes,
			SearchFilterSoggetto filter, boolean paginated) throws ServiceException, NotImplementedException, Exception{
				
		sqlQueryObjectParam.setANDLogicOperator(true);
		
		
		ISQLQueryObject sqlQueryObjectCondition = sqlQueryObjectParam.newSQLQueryObject();
		
		if(filter.getOrCondition().booleanValue()) {
			sqlQueryObjectCondition.setANDLogicOperator(false);
		} else {
			sqlQueryObjectCondition.setANDLogicOperator(true);
		}

		
		if(filter.getTipo()!= null) {
			sqlQueryObjectCondition.addWhereCondition(CostantiDB.SOGGETTI+".tipo_soggetto=?");
			paramTypes.add(new JDBCObject(filter.getTipo(),String.class));
		}
		if(filter.getNome()!= null) {
			sqlQueryObjectCondition.addWhereCondition(CostantiDB.SOGGETTI+".nome_soggetto=?");
			paramTypes.add(new JDBCObject(filter.getNome(),String.class));
		}

		if(filter.getDescrizione()!= null) {
			sqlQueryObjectCondition.addWhereLikeCondition(CostantiDB.SOGGETTI+".descrizione=?",filter.getDescrizione(),true,true);
		}

		if(filter.getIdentificativoPorta()!= null) {
			sqlQueryObjectCondition.addWhereCondition(CostantiDB.SOGGETTI+".identificativo_porta=?");
			paramTypes.add(new JDBCObject(filter.getIdentificativoPorta(),String.class));
		}
		
		if(filter.getRouter()!= null) {
			sqlQueryObjectCondition.addWhereCondition(CostantiDB.SOGGETTI+".is_router=?");
			paramTypes.add(new JDBCObject(filter.getRouter()?1:0,Integer.class));
		}
		
		if(filter.getPdUrlPrefixRewriter()!= null) {
			sqlQueryObjectCondition.addWhereCondition(CostantiDB.SOGGETTI+".pd_url_prefix_rewriter=?");
			paramTypes.add(new JDBCObject(filter.getPdUrlPrefixRewriter(),String.class));
		}
		if(filter.getPaUrlPrefixRewriter()!= null) {
			sqlQueryObjectCondition.addWhereCondition(CostantiDB.SOGGETTI+".pa_url_prefix_rewriter=?");
			paramTypes.add(new JDBCObject(filter.getPaUrlPrefixRewriter(),String.class));
		}
		
		if(filter.getOraRegistrazioneMin()!= null) {
			sqlQueryObjectCondition.addWhereCondition(CostantiDB.SOGGETTI+".ora_registrazione>=?");
			paramTypes.add(new JDBCObject(filter.getOraRegistrazioneMin(),Date.class));
		}
		if(filter.getOraRegistrazioneMax()!= null) {
			sqlQueryObjectCondition.addWhereCondition(CostantiDB.SOGGETTI+".ora_registrazione<=?");
			paramTypes.add(new JDBCObject(filter.getOraRegistrazioneMax(),Date.class));
		}
		
		
		if(((SQLQueryObjectCore)sqlQueryObjectCondition).sizeConditions()>0)
			sqlQueryObjectParam.addWhereCondition(true, sqlQueryObjectCondition.createSQLConditions());
		
		if(paginated){
			if(filter.getLimit()!=null) {
				sqlQueryObjectParam.setLimit(filter.getLimit());
			}
			if(filter.getOffset()!=null) {
				sqlQueryObjectParam.setOffset(filter.getOffset());
			}
			sqlQueryObjectParam.addOrderBy(CostantiDB.SOGGETTI+".tipo_soggetto");
			sqlQueryObjectParam.addOrderBy(CostantiDB.SOGGETTI+".nome_soggetto");
			sqlQueryObjectParam.setSortType(true);
		}
		
	}

	
	
	


	@Override
	public List<Soggetto> findAll(SearchFilterSoggetto filter) throws ConfigServiceException_Exception,ConfigNotImplementedException_Exception,ConfigNotAuthorizedException_Exception {
		try{
		
			checkInitDriverConfigurazioneDB(this.soggettoService);
			this.logStartMethod("findAll", filter);
			this.authorize(true);
			List<Soggetto> result = this.readSoggetti(filter, true);
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
	public Soggetto find(SearchFilterSoggetto filter) throws ConfigServiceException_Exception,ConfigNotFoundException_Exception,ConfigMultipleResultException_Exception,ConfigNotImplementedException_Exception,ConfigNotAuthorizedException_Exception {
		try{
		
			checkInitDriverConfigurazioneDB(this.soggettoService);
			this.logStartMethod("find", filter);
			this.authorize(true);
			List<Soggetto> resultList = this.readSoggetti(filter, false);
			if(resultList==null || resultList.size()<=0){
				throw new DriverConfigurazioneNotFound("NotFound");
			}
			if(resultList.size()>1){
				throw new MultipleResultException("Found "+resultList.size()+" soggetti");
			}
			Soggetto result = resultList.get(0);
			this.logEndMethod("find", result);
			return result;
			
		} catch(NotAuthorizedException e){
			throw throwNotAuthorizedException("find", e, Constants.CODE_NOT_AUTHORIZED_EXCEPTION, filter);
		} catch (NotImplementedException e) {
			throw throwNotImplementedException("find", e, Constants.CODE_NOT_IMPLEMENTED_EXCEPTION, filter);
		} catch (ServiceException e) {
			throw throwServiceException("find", e, Constants.CODE_SERVICE_EXCEPTION, filter);
		} catch (DriverConfigurazioneNotFound e) {
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
	public long count(SearchFilterSoggetto filter) throws ConfigServiceException_Exception,ConfigNotImplementedException_Exception,ConfigNotAuthorizedException_Exception {
		try{
		
			checkInitDriverConfigurazioneDB(this.soggettoService);
			this.logStartMethod("count", filter);
			this.authorize(true);
			long result = this.toCount(filter);
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
	public Soggetto get(org.openspcoop2.core.config.IdSoggetto id) throws ConfigServiceException_Exception,ConfigNotFoundException_Exception,ConfigMultipleResultException_Exception,ConfigNotImplementedException_Exception,ConfigNotAuthorizedException_Exception {
		try{
		
			checkInitDriverConfigurazioneGet(this.soggettoService);
			this.logStartMethod("get", id);
			this.authorize(true);
			Soggetto result = ((IDriverConfigurazioneGet)this.soggettoService.getDriver()).getSoggetto(this.convertToIdSoggetto(id));
			this.logEndMethod("get", result);
			return result;
			
		} catch(NotAuthorizedException e){
			throw throwNotAuthorizedException("get", e, Constants.CODE_NOT_AUTHORIZED_EXCEPTION, id);
		} catch (NotImplementedException e) {
			throw throwNotImplementedException("get", e, Constants.CODE_NOT_IMPLEMENTED_EXCEPTION, id);
		} catch (ServiceException e) {
			throw throwServiceException("get", e, Constants.CODE_SERVICE_EXCEPTION, id);
		} catch (DriverConfigurazioneNotFound e) {
			throw throwNotFoundException("get", e, Constants.CODE_NOT_FOUND_EXCEPTION, id);
		} catch (Exception e){
			throw throwServiceException("get", e, Constants.CODE_ERROR_GENERAL_EXCEPTION, id);
		}
	}
	
	@Override
	public boolean exists(org.openspcoop2.core.config.IdSoggetto id) throws ConfigServiceException_Exception,ConfigMultipleResultException_Exception,ConfigNotImplementedException_Exception,ConfigNotAuthorizedException_Exception {
		try{
		
			checkInitDriverConfigurazioneCRUD(this.soggettoService);
			this.logStartMethod("exists", id);
			this.authorize(true);
			boolean result = ((IDriverConfigurazioneCRUD)this.soggettoService.getDriver()).existsSoggetto(this.convertToIdSoggetto(id));
			this.logEndMethod("exists", result);
			return result;
			
		} catch(NotAuthorizedException e){
			throw throwNotAuthorizedException("exists", e, Constants.CODE_NOT_AUTHORIZED_EXCEPTION, id);
		} catch (NotImplementedException e) {
			throw throwNotImplementedException("exists", e, Constants.CODE_NOT_IMPLEMENTED_EXCEPTION, id);
		} catch (ServiceException e) {
			throw throwServiceException("exists", e, Constants.CODE_SERVICE_EXCEPTION, id);
		} catch (Exception e){
			throw throwServiceException("exists", e, Constants.CODE_ERROR_GENERAL_EXCEPTION, id);
		}
	}

	@Override
	public List<org.openspcoop2.core.config.IdSoggetto> findAllIds(SearchFilterSoggetto filter) throws ConfigServiceException_Exception,ConfigNotImplementedException_Exception,ConfigNotAuthorizedException_Exception {
		try{
		
			checkInitDriverConfigurazioneDB(this.soggettoService);
			this.logStartMethod("findAllIds", filter);
			this.authorize(true);
			List<org.openspcoop2.core.config.IdSoggetto> result = this.readSoggettiIds(filter, true);
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

	@Override
	public UseInfo inUse(org.openspcoop2.core.config.IdSoggetto id) throws ConfigServiceException_Exception,ConfigNotFoundException_Exception,ConfigNotImplementedException_Exception,ConfigNotAuthorizedException_Exception {
		try{
		
			this.logStartMethod("inUse", id);
			this.authorize(true);
			
//			UseInfo useInfo = new UseInfo();
//			InUse inUse = this.getService().inUse(id);
//			useInfo.setUsed(inUse.isInUse());
//			
//			for(@SuppressWarnings("rawtypes") org.openspcoop2.generic_project.beans.InUseCondition inUseCondition: inUse.getInUseConditions()) {
//				InUseCondition inUseCond = new InUseCondition();
//				inUseCond.setCause(inUseCondition.getCause());
//				inUseCond.setName(inUseCondition.getObjectName());
//				if(inUseCondition.getObject()!=null){
//					inUseCond.setType(Identified.toEnumConstant(inUseCondition.getObject().getSimpleName()));
//				}
//				@SuppressWarnings("unchecked")
//				List<Object> list = inUseCondition.getIds();
//				if(list!=null && list.size()>0){
//					for (Object object : list) {
//						IdEntity idEntity = null;
//						if("Soggetto".equals(inUseCondition.getObjectName())){
//							idEntity = new IdEntity();
//							org.openspcoop2.core.config.ws.server.beans.WrapperIdSoggetto wrapperId = new 
//								org.openspcoop2.core.config.ws.server.beans.WrapperIdSoggetto();
//							wrapperId.setId((org.openspcoop2.core.config.IdSoggetto)object);
//							idEntity.setId(wrapperId);
//						}
//						if("PortaDelegata".equals(inUseCondition.getObjectName())){
//							idEntity = new IdEntity();
//							org.openspcoop2.core.config.ws.server.beans.WrapperIdPortaDelegata wrapperId = new 
//								org.openspcoop2.core.config.ws.server.beans.WrapperIdPortaDelegata();
//							wrapperId.setId((org.openspcoop2.core.config.IdPortaDelegata)object);
//							idEntity.setId(wrapperId);
//						}
//						if("PortaApplicativa".equals(inUseCondition.getObjectName())){
//							idEntity = new IdEntity();
//							org.openspcoop2.core.config.ws.server.beans.WrapperIdPortaApplicativa wrapperId = new 
//								org.openspcoop2.core.config.ws.server.beans.WrapperIdPortaApplicativa();
//							wrapperId.setId((org.openspcoop2.core.config.IdPortaApplicativa)object);
//							idEntity.setId(wrapperId);
//						}
//						if("ServizioApplicativo".equals(inUseCondition.getObjectName())){
//							idEntity = new IdEntity();
//							org.openspcoop2.core.config.ws.server.beans.WrapperIdServizioApplicativo wrapperId = new 
//								org.openspcoop2.core.config.ws.server.beans.WrapperIdServizioApplicativo();
//							wrapperId.setId((org.openspcoop2.core.config.IdServizioApplicativo)object);
//							idEntity.setId(wrapperId);
//						}
//						if(idEntity==null){
//							throw new Exception("Object id unknown. ClassType["+object.getClass().getName()+"] Object["+inUseCondition.getObject()+"] ObjectName["+inUseCondition.getObjectName()+"]");
//						}
//						inUseCond.addId(idEntity);
//					}
//				}
//				useInfo.addInUseCondition(inUseCond);
//			}
//			
//			this.logEndMethod("inUse", useInfo);
//			return useInfo;
			
			throw new NotImplementedException("Not Implemented");
			
		} catch(NotAuthorizedException e){
			throw throwNotAuthorizedException("inUse", e, Constants.CODE_NOT_AUTHORIZED_EXCEPTION, id);
		} catch (NotImplementedException e) {
			throw throwNotImplementedException("inUse", e, Constants.CODE_NOT_IMPLEMENTED_EXCEPTION, id);
		} catch (ServiceException e) {
			throw throwServiceException("inUse", e, Constants.CODE_SERVICE_EXCEPTION, id);
//		} catch (NotFoundException e) {
//			throw throwNotFoundException("inUse", e, Constants.CODE_NOT_FOUND_EXCEPTION, id);
		} catch (Exception e){
			throw throwServiceException("inUse", e, Constants.CODE_ERROR_GENERAL_EXCEPTION, id);
		}
	}

	@Override
	public void create(Soggetto obj) throws ConfigServiceException_Exception,ConfigNotImplementedException_Exception,ConfigNotAuthorizedException_Exception {
		try{
		
			checkInitDriverConfigurazioneCRUD(this.soggettoService);
			this.logStartMethod("create", obj);
			this.authorize(false);
			obj.setSuperUser(ServerProperties.getInstance().getUser());
			((IDriverConfigurazioneCRUD)this.soggettoService.getDriver()).createSoggetto(obj);
			this.logEndMethod("create");
			
		} catch(NotAuthorizedException e){
			throw throwNotAuthorizedException("create", e, Constants.CODE_NOT_AUTHORIZED_EXCEPTION, null);
		} catch (NotImplementedException e) {
			throw throwNotImplementedException("create", e, Constants.CODE_NOT_IMPLEMENTED_EXCEPTION, null);
		} catch (ServiceException e) {
			throw throwServiceException("create", e, Constants.CODE_SERVICE_EXCEPTION, null);
		} catch (Exception e){
			throw throwServiceException("create", e, Constants.CODE_ERROR_GENERAL_EXCEPTION, null);
		}
	}
	
	@Override
    public void update(org.openspcoop2.core.config.IdSoggetto oldId, Soggetto obj) throws ConfigServiceException_Exception,ConfigNotFoundException_Exception,ConfigNotImplementedException_Exception,ConfigNotAuthorizedException_Exception {
		try{
		
			checkInitDriverConfigurazioneCRUD(this.soggettoService);
			this.logStartMethod("update", oldId, obj);
			this.authorize(false);
			if(this.exists(oldId)==false){
				throw new DriverConfigurazioneNotFound("Soggetto non esistente");
			}
			obj.setSuperUser(ServerProperties.getInstance().getUser());
			obj.setOldTipoForUpdate(oldId.getTipo());
			obj.setOldNomeForUpdate(oldId.getNome());
			((IDriverConfigurazioneCRUD)this.soggettoService.getDriver()).updateSoggetto(obj);
			this.logEndMethod("update");
			
		} catch(NotAuthorizedException e){
			throw throwNotAuthorizedException("update", e, Constants.CODE_NOT_AUTHORIZED_EXCEPTION, oldId);
		} catch (NotImplementedException e) {
			throw throwNotImplementedException("update", e, Constants.CODE_NOT_IMPLEMENTED_EXCEPTION, oldId);
		} catch (ServiceException e) {
			throw throwServiceException("update", e, Constants.CODE_SERVICE_EXCEPTION, oldId);
		} catch (DriverConfigurazioneNotFound e) {
			throw throwNotFoundException("update", e, Constants.CODE_NOT_FOUND_EXCEPTION, oldId);
		} catch (Exception e){
			throw throwServiceException("update", e, Constants.CODE_ERROR_GENERAL_EXCEPTION, oldId);
		}
    }
	
	@Override
	public void updateOrCreate(org.openspcoop2.core.config.IdSoggetto oldId, Soggetto obj) throws ConfigServiceException_Exception,ConfigNotImplementedException_Exception,ConfigNotAuthorizedException_Exception {
		try{
		
			checkInitDriverConfigurazioneCRUD(this.soggettoService);
			this.logStartMethod("updateOrCreate", oldId, obj);
			this.authorize(false);
			if(this.exists(oldId)==false){
				obj.setSuperUser(ServerProperties.getInstance().getUser());
				((IDriverConfigurazioneCRUD)this.soggettoService.getDriver()).createSoggetto(obj);	
			}else{
				obj.setSuperUser(ServerProperties.getInstance().getUser());
				obj.setOldTipoForUpdate(oldId.getTipo());
				obj.setOldNomeForUpdate(oldId.getNome());
				((IDriverConfigurazioneCRUD)this.soggettoService.getDriver()).updateSoggetto(obj);
			}
			this.logEndMethod("updateOrCreate");
			
		} catch(NotAuthorizedException e){
			throw throwNotAuthorizedException("updateOrCreate", e, Constants.CODE_NOT_AUTHORIZED_EXCEPTION, oldId);
		} catch (NotImplementedException e) {
			throw throwNotImplementedException("updateOrCreate", e, Constants.CODE_NOT_IMPLEMENTED_EXCEPTION, oldId);
		} catch (ServiceException e) {
			throw throwServiceException("updateOrCreate", e, Constants.CODE_SERVICE_EXCEPTION, oldId);
		} catch (Exception e){
			throw throwServiceException("updateOrCreate", e, Constants.CODE_ERROR_GENERAL_EXCEPTION, oldId);
		}
    }
	
	@Override
	public void deleteById(org.openspcoop2.core.config.IdSoggetto id) throws ConfigServiceException_Exception,ConfigNotImplementedException_Exception,ConfigNotAuthorizedException_Exception {
		try{
		
			checkInitDriverConfigurazioneCRUD(this.soggettoService);
			this.logStartMethod("deleteById", id);
			this.authorize(false);
			Soggetto soggetto = null;
			try{
				soggetto = this.get(id);
			}catch(ConfigNotFoundException_Exception notFound){}
			if(soggetto!=null)
				((IDriverConfigurazioneCRUD)this.soggettoService.getDriver()).deleteSoggetto(soggetto);
			this.logEndMethod("deleteById");
			
		} catch(NotAuthorizedException e){
			throw throwNotAuthorizedException("deleteById", e, Constants.CODE_NOT_AUTHORIZED_EXCEPTION, id);
		} catch (NotImplementedException e) {
			throw throwNotImplementedException("deleteById", e, Constants.CODE_NOT_IMPLEMENTED_EXCEPTION, id);
		} catch (ServiceException e) {
			throw throwServiceException("deleteById", e, Constants.CODE_SERVICE_EXCEPTION, id);
		} catch (Exception e){
			throw throwServiceException("deleteById", e, Constants.CODE_ERROR_GENERAL_EXCEPTION, id);
		}
    }
	
	
	@Override
	public long deleteAll() throws ConfigServiceException_Exception,ConfigNotImplementedException_Exception,ConfigNotAuthorizedException_Exception {
		try{
		
			checkInitDriverConfigurazioneCRUD(this.soggettoService);
			this.logStartMethod("deleteAll");
			this.authorize(false);
			List<IDSoggetto> list = null; 
			try{
				list = ((IDriverConfigurazioneGet)this.soggettoService.getDriver()).getAllIdSoggetti(new FiltroRicercaSoggetti());
			}catch(DriverConfigurazioneNotFound notFound){}
			long result = 0;
			if(list!=null && list.size()>0){
				result = list.size();
				for (IDSoggetto idSoggetto : list) {
					try{
						((IDriverConfigurazioneCRUD)this.soggettoService.getDriver()).
							deleteSoggetto(((IDriverConfigurazioneGet)this.soggettoService.getDriver()).getSoggetto(idSoggetto));
					}catch(DriverConfigurazioneNotFound notFound){}
				}
			}
			this.logEndMethod("deleteAll", result);
			return result;
			
		} catch(NotAuthorizedException e){
			throw throwNotAuthorizedException("deleteAll", e, Constants.CODE_NOT_AUTHORIZED_EXCEPTION, null);
		} catch (NotImplementedException e) {
			throw throwNotImplementedException("deleteAll", e, Constants.CODE_NOT_IMPLEMENTED_EXCEPTION, null);
		} catch (ServiceException e) {
			throw throwServiceException("deleteAll", e, Constants.CODE_SERVICE_EXCEPTION, null);
		} catch (Exception e){
			throw throwServiceException("deleteAll", e, Constants.CODE_ERROR_GENERAL_EXCEPTION, null);
		}
    }
	
	
	@Override
	public long deleteAllByFilter(SearchFilterSoggetto filter) throws ConfigServiceException_Exception,ConfigNotImplementedException_Exception,ConfigNotAuthorizedException_Exception {
		try{
		
			checkInitDriverConfigurazioneCRUD(this.soggettoService);
			this.logStartMethod("deleteAllByFilter", filter);
			this.authorize(false);
			List<IDSoggetto> list = this.readIds(filter, true);
			long result = 0;
			if(list!=null && list.size()>0){
				result = list.size();
				for (IDSoggetto idSoggetto : list) {
					try{
						((IDriverConfigurazioneCRUD)this.soggettoService.getDriver()).
							deleteSoggetto(((IDriverConfigurazioneGet)this.soggettoService.getDriver()).getSoggetto(idSoggetto));
					}catch(DriverConfigurazioneNotFound notFound){}
				}
			}
			this.logEndMethod("deleteAllByFilter", result);
			return result;
			
		} catch(NotAuthorizedException e){
			throw throwNotAuthorizedException("deleteAllByFilter", e, Constants.CODE_NOT_AUTHORIZED_EXCEPTION, filter);
		} catch (NotImplementedException e) {
			throw throwNotImplementedException("deleteAllByFilter", e, Constants.CODE_NOT_IMPLEMENTED_EXCEPTION, filter);
		} catch (ServiceException e) {
			throw throwServiceException("deleteAllByFilter", e, Constants.CODE_SERVICE_EXCEPTION, filter);
		} catch (ExpressionNotImplementedException e) {
			throw throwServiceException("deleteAllByFilter", e, Constants.CODE_EXPRESSION_NOT_IMPLEMENTED_EXCEPTION, filter);
		} catch (ExpressionException e) {
			throw throwServiceException("deleteAllByFilter", e, Constants.CODE_EXPRESSION_EXCEPTION, filter);
		} catch (Exception e){
			throw throwServiceException("deleteAllByFilter", e, Constants.CODE_ERROR_GENERAL_EXCEPTION, filter);
		}
    }
	
	
	@Override
	public void delete(Soggetto obj) throws ConfigServiceException_Exception,ConfigNotImplementedException_Exception,ConfigNotAuthorizedException_Exception {
		try{
		
			checkInitDriverConfigurazioneCRUD(this.soggettoService);
			this.logStartMethod("delete", obj);
			this.authorize(false);
			((IDriverConfigurazioneCRUD)this.soggettoService.getDriver()).deleteSoggetto(obj);
			this.logEndMethod("delete");
			
		} catch(NotAuthorizedException e){
			throw throwNotAuthorizedException("delete", e, Constants.CODE_NOT_AUTHORIZED_EXCEPTION, null);
		} catch (NotImplementedException e) {
			throw throwNotImplementedException("delete", e, Constants.CODE_NOT_IMPLEMENTED_EXCEPTION, null);
		} catch (ServiceException e) {
			throw throwServiceException("delete", e, Constants.CODE_EXPRESSION_NOT_IMPLEMENTED_EXCEPTION, null);
		} catch (Exception e){
			throw throwServiceException("delete", e, Constants.CODE_ERROR_GENERAL_EXCEPTION, null);
		}
    }



}