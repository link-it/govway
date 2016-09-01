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
package org.openspcoop2.core.registry.ws.server.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.id.IDAccordoCooperazione;
import org.openspcoop2.core.id.IDAccordoCooperazioneWithSoggetto;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoCooperazione;
import org.openspcoop2.core.registry.IdAccordoCooperazione;
import org.openspcoop2.core.registry.IdSoggetto;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.core.registry.driver.FiltroRicercaAccordi;
import org.openspcoop2.core.registry.driver.IDAccordoCooperazioneFactory;
import org.openspcoop2.core.registry.driver.IDriverRegistroServiziCRUD;
import org.openspcoop2.core.registry.driver.IDriverRegistroServiziGet;
import org.openspcoop2.core.registry.driver.db.DriverRegistroServiziDB;
import org.openspcoop2.core.registry.ws.server.AccordoCooperazioneCRUD;
import org.openspcoop2.core.registry.ws.server.AccordoCooperazioneSearch;
import org.openspcoop2.core.registry.ws.server.beans.UseInfo;
import org.openspcoop2.core.registry.ws.server.config.Constants;
import org.openspcoop2.core.registry.ws.server.config.DriverRegistroServizi;
import org.openspcoop2.core.registry.ws.server.config.LoggerProperties;
import org.openspcoop2.core.registry.ws.server.config.ServerProperties;
import org.openspcoop2.core.registry.ws.server.exception.RegistryMultipleResultException_Exception;
import org.openspcoop2.core.registry.ws.server.exception.RegistryNotAuthorizedException_Exception;
import org.openspcoop2.core.registry.ws.server.exception.RegistryNotFoundException_Exception;
import org.openspcoop2.core.registry.ws.server.exception.RegistryNotImplementedException_Exception;
import org.openspcoop2.core.registry.ws.server.exception.RegistryServiceException_Exception;
import org.openspcoop2.core.registry.ws.server.filter.SearchFilterAccordoCooperazione;
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
 * AccordoCooperazioneImpl
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class AccordoCooperazioneImpl extends BaseImpl  implements AccordoCooperazioneSearch, AccordoCooperazioneCRUD {

	private DriverRegistroServizi accordoCooperazioneService = null; 

	public AccordoCooperazioneImpl() {
		super();
		try {
			this.accordoCooperazioneService = DriverRegistroServizi.getInstance();
			LoggerProperties.getLoggerWS().info("Inizializzazione AccordoCooperazione Service effettuata con successo");
		} catch (Exception e) {
			LoggerProperties.getLoggerWS().error("Errore durante l'inizializzazione del AccordoCooperazione Service",  e);
		} 
	}
	
	private IDAccordoCooperazione convertToIdAccordoCooperazione(IdAccordoCooperazione id) throws DriverRegistroServiziException{
		IDSoggetto idSoggettoReferente = null;
		if(id.getSoggettoReferente()!=null){
			idSoggettoReferente = new IDSoggetto(id.getSoggettoReferente().getTipo(), id.getSoggettoReferente().getNome());
		}
		return this.buildIDAccordoCooperazione(id.getNome(), id.getVersione(), idSoggettoReferente);
	}
	private IDAccordoCooperazione buildIDAccordoCooperazione(String nome, String versione, IDSoggetto idSoggetto) throws DriverRegistroServiziException{
		IDAccordoCooperazione idAccordo = IDAccordoCooperazioneFactory.getInstance().getIDAccordoFromValues(nome, versione);
		IDAccordoCooperazione idSearch = idAccordo;
		if(idSoggetto!=null){
			IDAccordoCooperazioneWithSoggetto idAccordoWithSoggetto = new IDAccordoCooperazioneWithSoggetto(idAccordo);
			idAccordoWithSoggetto.setSoggettoReferente(idSoggetto);
			idSearch = idAccordoWithSoggetto;
		}
		return idSearch;
	}

	private IdAccordoCooperazione convertToIdAccordoCooperazioneWS(IDAccordoCooperazione id) throws DriverRegistroServiziException{
		IdAccordoCooperazione idAccordo = new IdAccordoCooperazione();
		idAccordo.setNome(id.getNome());
		idAccordo.setVersione(id.getVersione());
		if(id instanceof IDAccordoCooperazioneWithSoggetto){
			IDAccordoCooperazioneWithSoggetto idWithSoggetto = (IDAccordoCooperazioneWithSoggetto) id;
			IdSoggetto soggettoReferente = new IdSoggetto();
			soggettoReferente.setTipo(idWithSoggetto.getSoggettoReferente().getTipo());
			soggettoReferente.setNome(idWithSoggetto.getSoggettoReferente().getNome());
			idAccordo.setSoggettoReferente(soggettoReferente);
		}
		return idAccordo;
	}
	
	private List<IdAccordoCooperazione> readAccordiIds(SearchFilterAccordoCooperazione filter, boolean paginated) throws ServiceException, NotImplementedException, Exception{
		List<IDAccordoCooperazione> listIds = this.readIds(filter, true);
		List<org.openspcoop2.core.registry.IdAccordoCooperazione> listIdsWS = new ArrayList<IdAccordoCooperazione>();
		for (int i = 0; i < listIds.size(); i++) {
			listIdsWS.add(this.convertToIdAccordoCooperazioneWS(listIds.get(i)));
		}
		return listIdsWS;
	}
	private List<AccordoCooperazione> readAccordi(SearchFilterAccordoCooperazione filter, boolean paginated) throws ServiceException, NotImplementedException, Exception{
		List<Long> listIds = this.readLongIds(filter, true);
		List<AccordoCooperazione> listAccordi = new ArrayList<AccordoCooperazione>();
		DriverRegistroServiziDB driverDB = ((DriverRegistroServiziDB)this.accordoCooperazioneService.getDriver());
		for (int i = 0; i < listIds.size(); i++) {
			listAccordi.add(driverDB.getAccordoCooperazione(listIds.get(0)));
		}
		return listAccordi;
	}
	
	@SuppressWarnings("unchecked")
	private List<Long> readLongIds(SearchFilterAccordoCooperazione filter, boolean paginated) throws ServiceException, NotImplementedException, Exception{
		return (List<Long>) this.toList(filter, true, paginated);
	}
	@SuppressWarnings("unchecked")
	private List<IDAccordoCooperazione> readIds(SearchFilterAccordoCooperazione filter, boolean paginated) throws ServiceException, NotImplementedException, Exception{
		return (List<IDAccordoCooperazione>) this.toList(filter, false, paginated);
	}
	private List<?> toList(SearchFilterAccordoCooperazione filter, boolean idLong, boolean paginated) throws ServiceException, NotImplementedException, Exception{
		
		DriverRegistroServiziDB driverDB = ((DriverRegistroServiziDB)this.accordoCooperazioneService.getDriver());
		
		ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(driverDB.getTipoDB());
		List<Class<?>> returnTypes = new ArrayList<Class<?>>();
		List<JDBCObject> paramTypes = new ArrayList<JDBCObject>();
		
		sqlQueryObject.addFromTable(CostantiDB.ACCORDI_COOPERAZIONE);
		
		if(idLong){
			sqlQueryObject.addSelectField(CostantiDB.ACCORDI_COOPERAZIONE, "id");
			returnTypes.add(Long.class);
		}
		
		if(!idLong || paginated){
			sqlQueryObject.addSelectField(CostantiDB.ACCORDI_COOPERAZIONE, "id_referente");
			returnTypes.add(Long.class);
			
			sqlQueryObject.addSelectField(CostantiDB.ACCORDI_COOPERAZIONE, "nome");
			returnTypes.add(String.class);
			
			sqlQueryObject.addSelectField(CostantiDB.ACCORDI_COOPERAZIONE, "versione");
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
			List<IDAccordoCooperazione> listIds = new ArrayList<IDAccordoCooperazione>();
			for (List<Object> list : listaRisultati) {
				Long idReferente = (Long)list.get(0);
				String name = (String)list.get(1);
				String versione = (String)list.get(2);
				IDSoggetto idSoggettoReferente = null;
				if(idReferente!=null && idReferente>0){
					idSoggettoReferente = driverDB.getIdSoggetto(idReferente);
				}
				listIds.add(this.buildIDAccordoCooperazione(name, versione, idSoggettoReferente));
			}
			return listIds;
		}
		
	}
	
	private long toCount(SearchFilterAccordoCooperazione filter) throws ServiceException, NotImplementedException, Exception{
		
		DriverRegistroServiziDB driverDB = ((DriverRegistroServiziDB)this.accordoCooperazioneService.getDriver());
		
		ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(driverDB.getTipoDB());
		List<Class<?>> returnTypes = new ArrayList<Class<?>>();
		List<JDBCObject> paramTypes = new ArrayList<JDBCObject>();
		
		sqlQueryObject.addFromTable(CostantiDB.ACCORDI_COOPERAZIONE,CostantiDB.ACCORDI_COOPERAZIONE);
		
		sqlQueryObject.addSelectCountField(CostantiDB.ACCORDI_COOPERAZIONE, "id", "cont", true);
		returnTypes.add(Long.class);
		
		this.setFilter(sqlQueryObject, paramTypes, filter, false);
		
		List<List<Object>> listaRisultati = driverDB.readCustom(sqlQueryObject, returnTypes, paramTypes);
		
		return (Long) listaRisultati.get(0).get(0);
		
	}
	
	private void setFilter(ISQLQueryObject sqlQueryObjectParam, List<JDBCObject> paramTypes,
			SearchFilterAccordoCooperazione filter, boolean paginated) throws ServiceException, NotImplementedException, Exception{
				
		if(filter.getSoggettoReferente()!= null) {
			sqlQueryObjectParam.addFromTable(CostantiDB.SOGGETTI);
			sqlQueryObjectParam.addWhereCondition(CostantiDB.ACCORDI_COOPERAZIONE+".id_referente="+CostantiDB.SOGGETTI+".id");
		}
		
		sqlQueryObjectParam.setANDLogicOperator(true);
		
		
		ISQLQueryObject sqlQueryObjectCondition = sqlQueryObjectParam.newSQLQueryObject();
		
		if(filter.getOrCondition().booleanValue()) {
			sqlQueryObjectCondition.setANDLogicOperator(false);
		} else {
			sqlQueryObjectCondition.setANDLogicOperator(true);
		}

		if(filter.getStatoPackage()!= null) {
			sqlQueryObjectCondition.addWhereCondition(CostantiDB.ACCORDI_COOPERAZIONE+".stato=?");
			paramTypes.add(new JDBCObject(filter.getStatoPackage(),String.class));
		}
		if(filter.getPrivato()!= null) {
			sqlQueryObjectCondition.addWhereCondition(CostantiDB.ACCORDI_COOPERAZIONE+".privato=?");
			paramTypes.add(new JDBCObject(filter.getPrivato()?1:0,Integer.class));
		}
		if(filter.getSoggettoReferente()!= null) {
			if(filter.getSoggettoReferente().getTipo()!= null) {
				sqlQueryObjectCondition.addWhereCondition(CostantiDB.SOGGETTI+".tipo_soggetto=?");
				paramTypes.add(new JDBCObject(filter.getSoggettoReferente().getTipo(),String.class));
			}
			if(filter.getSoggettoReferente().getNome()!= null) {
				sqlQueryObjectCondition.addWhereCondition(CostantiDB.SOGGETTI+".nome_soggetto=?");
				paramTypes.add(new JDBCObject(filter.getSoggettoReferente().getNome(),String.class));
			}
		}
		if(filter.getNome()!= null) {
			sqlQueryObjectCondition.addWhereCondition(CostantiDB.ACCORDI_COOPERAZIONE+".nome=?");
			paramTypes.add(new JDBCObject(filter.getNome(),String.class));
		}
		if(filter.getDescrizione()!= null) {
			sqlQueryObjectCondition.addWhereLikeCondition(CostantiDB.ACCORDI_COOPERAZIONE+".descrizione=?",filter.getDescrizione(),true,true);
		}
		if(filter.getVersione()!= null) {
			sqlQueryObjectCondition.addWhereCondition(CostantiDB.ACCORDI_COOPERAZIONE+".versione=?");
			paramTypes.add(new JDBCObject(filter.getVersione(),String.class));
		}
		if(filter.getOraRegistrazioneMin()!= null) {
			sqlQueryObjectCondition.addWhereCondition(CostantiDB.ACCORDI_COOPERAZIONE+".ora_registrazione>=?");
			paramTypes.add(new JDBCObject(filter.getOraRegistrazioneMin(),Date.class));
		}
		if(filter.getOraRegistrazioneMax()!= null) {
			sqlQueryObjectCondition.addWhereCondition(CostantiDB.ACCORDI_COOPERAZIONE+".ora_registrazione<=?");
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
			sqlQueryObjectParam.addOrderBy(CostantiDB.ACCORDI_COOPERAZIONE+".id_referente");
			sqlQueryObjectParam.addOrderBy(CostantiDB.ACCORDI_COOPERAZIONE+".nome");
			sqlQueryObjectParam.addOrderBy(CostantiDB.ACCORDI_COOPERAZIONE+".versione");
			sqlQueryObjectParam.setSortType(true);
		}
		
	}



	@Override
	public List<AccordoCooperazione> findAll(SearchFilterAccordoCooperazione filter) throws RegistryServiceException_Exception,RegistryNotImplementedException_Exception,RegistryNotAuthorizedException_Exception {
		try{
		
			checkInitDriverRegistroServiziDB(this.accordoCooperazioneService);
			this.logStartMethod("findAll", filter);
			this.authorize(true);	
			List<AccordoCooperazione> result = this.readAccordi(filter, true);
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
	public AccordoCooperazione find(SearchFilterAccordoCooperazione filter) throws RegistryServiceException_Exception,RegistryNotFoundException_Exception,RegistryMultipleResultException_Exception,RegistryNotImplementedException_Exception,RegistryNotAuthorizedException_Exception {
		try{
		
			checkInitDriverRegistroServiziDB(this.accordoCooperazioneService);
			this.logStartMethod("find", filter);
			this.authorize(true);
			List<AccordoCooperazione> resultList = this.readAccordi(filter, false);
			if(resultList==null || resultList.size()<=0){
				throw new DriverRegistroServiziNotFound("NotFound");
			}
			if(resultList.size()>1){
				throw new MultipleResultException("Found "+resultList.size()+" accordi");
			}
			AccordoCooperazione result = resultList.get(0);
			this.logEndMethod("find", result);
			return result;
			
		} catch(NotAuthorizedException e){
			throw throwNotAuthorizedException("find", e, Constants.CODE_NOT_AUTHORIZED_EXCEPTION, filter);
		} catch (NotImplementedException e) {
			throw throwNotImplementedException("find", e, Constants.CODE_NOT_IMPLEMENTED_EXCEPTION, filter);
		} catch (ServiceException e) {
			throw throwServiceException("find", e, Constants.CODE_SERVICE_EXCEPTION, filter);
		} catch (DriverRegistroServiziNotFound e) {
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
	public long count(SearchFilterAccordoCooperazione filter) throws RegistryServiceException_Exception,RegistryNotImplementedException_Exception,RegistryNotAuthorizedException_Exception {
		try{
		
			checkInitDriverRegistroServiziDB(this.accordoCooperazioneService);
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
	public AccordoCooperazione get(org.openspcoop2.core.registry.IdAccordoCooperazione id) throws RegistryServiceException_Exception,RegistryNotFoundException_Exception,RegistryMultipleResultException_Exception,RegistryNotImplementedException_Exception,RegistryNotAuthorizedException_Exception {
		try{
		
			checkInitDriverRegistroServiziGet(this.accordoCooperazioneService);
			this.logStartMethod("get", id);
			this.authorize(true);
			AccordoCooperazione result = ((IDriverRegistroServiziGet)this.accordoCooperazioneService.getDriver()).getAccordoCooperazione(this.convertToIdAccordoCooperazione(id));
			this.logEndMethod("get", result);
			return result;
			
		} catch(NotAuthorizedException e){
			throw throwNotAuthorizedException("get", e, Constants.CODE_NOT_AUTHORIZED_EXCEPTION, id);
		} catch (NotImplementedException e) {
			throw throwNotImplementedException("exists", e, Constants.CODE_NOT_IMPLEMENTED_EXCEPTION, id);
		} catch (ServiceException e) {
			throw throwServiceException("get", e, Constants.CODE_SERVICE_EXCEPTION, id);
		} catch (DriverRegistroServiziNotFound e) {
			throw throwNotFoundException("get", e, Constants.CODE_NOT_FOUND_EXCEPTION, id);
		}catch (Exception e){
			throw throwServiceException("get", e, Constants.CODE_ERROR_GENERAL_EXCEPTION, id);
		}
	}
	
	@Override
	public boolean exists(org.openspcoop2.core.registry.IdAccordoCooperazione id) throws RegistryServiceException_Exception,RegistryMultipleResultException_Exception,RegistryNotImplementedException_Exception,RegistryNotAuthorizedException_Exception {
		try{
		
			checkInitDriverRegistroServiziCRUD(this.accordoCooperazioneService);
			this.logStartMethod("exists", id);
			this.authorize(true);
			boolean result = ((IDriverRegistroServiziCRUD)this.accordoCooperazioneService.getDriver()).existsAccordoCooperazione(this.convertToIdAccordoCooperazione(id));
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
	public List<org.openspcoop2.core.registry.IdAccordoCooperazione> findAllIds(SearchFilterAccordoCooperazione filter) throws RegistryServiceException_Exception,RegistryNotImplementedException_Exception,RegistryNotAuthorizedException_Exception {
		try{
		
			checkInitDriverRegistroServiziDB(this.accordoCooperazioneService);
			this.logStartMethod("findAllIds", filter);
			this.authorize(true);
			List<org.openspcoop2.core.registry.IdAccordoCooperazione> result = this.readAccordiIds(filter, true);
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
	public UseInfo inUse(org.openspcoop2.core.registry.IdAccordoCooperazione id) throws RegistryServiceException_Exception,RegistryNotFoundException_Exception,RegistryNotImplementedException_Exception,RegistryNotAuthorizedException_Exception {
		try{
		
			this.logStartMethod("inUse", id);
			this.authorize(true);
//			
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
//						if("AccordoCooperazione".equals(inUseCondition.getObjectName())){
//							idEntity = new IdEntity();
//							org.openspcoop2.core.registry.ws.server.beans.WrapperIdAccordoCooperazione wrapperId = new 
//								org.openspcoop2.core.registry.ws.server.beans.WrapperIdAccordoCooperazione();
//							wrapperId.setId((org.openspcoop2.core.registry.IdAccordoCooperazione)object);
//							idEntity.setId(wrapperId);
//						}
//						if("AccordoServizioParteComune".equals(inUseCondition.getObjectName())){
//							idEntity = new IdEntity();
//							org.openspcoop2.core.registry.ws.server.beans.WrapperIdAccordoServizioParteComune wrapperId = new 
//								org.openspcoop2.core.registry.ws.server.beans.WrapperIdAccordoServizioParteComune();
//							wrapperId.setId((org.openspcoop2.core.registry.IdAccordoServizioParteComune)object);
//							idEntity.setId(wrapperId);
//						}
//						if("PortaDominio".equals(inUseCondition.getObjectName())){
//							idEntity = new IdEntity();
//							org.openspcoop2.core.registry.ws.server.beans.WrapperIdPortaDominio wrapperId = new 
//								org.openspcoop2.core.registry.ws.server.beans.WrapperIdPortaDominio();
//							wrapperId.setId((org.openspcoop2.core.registry.IdPortaDominio)object);
//							idEntity.setId(wrapperId);
//						}
//						if("Soggetto".equals(inUseCondition.getObjectName())){
//							idEntity = new IdEntity();
//							org.openspcoop2.core.registry.ws.server.beans.WrapperIdSoggetto wrapperId = new 
//								org.openspcoop2.core.registry.ws.server.beans.WrapperIdSoggetto();
//							wrapperId.setId((org.openspcoop2.core.registry.IdSoggetto)object);
//							idEntity.setId(wrapperId);
//						}
//						if("AccordoServizioParteSpecifica".equals(inUseCondition.getObjectName())){
//							idEntity = new IdEntity();
//							org.openspcoop2.core.registry.ws.server.beans.WrapperIdAccordoServizioParteSpecifica wrapperId = new 
//								org.openspcoop2.core.registry.ws.server.beans.WrapperIdAccordoServizioParteSpecifica();
//							wrapperId.setId((org.openspcoop2.core.registry.IdAccordoServizioParteSpecifica)object);
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
//		} catch (DriverRegistroServiziNotFound e) {
//			throw throwNotFoundException("inUse", e, Constants.CODE_NOT_FOUND_EXCEPTION, id);
		} catch (Exception e){
			throw throwServiceException("inUse", e, Constants.CODE_ERROR_GENERAL_EXCEPTION, id);
		}
	}

	@Override
	public void create(AccordoCooperazione obj) throws RegistryServiceException_Exception,RegistryNotImplementedException_Exception,RegistryNotAuthorizedException_Exception {
		try{
		
			checkInitDriverRegistroServiziCRUD(this.accordoCooperazioneService);
			this.logStartMethod("create", obj);
			this.authorize(false);
			obj.setSuperUser(ServerProperties.getInstance().getUser());
			((IDriverRegistroServiziCRUD)this.accordoCooperazioneService.getDriver()).createAccordoCooperazione(obj);
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
    public void update(org.openspcoop2.core.registry.IdAccordoCooperazione oldId, AccordoCooperazione obj) throws RegistryServiceException_Exception,RegistryNotFoundException_Exception,RegistryNotImplementedException_Exception,RegistryNotAuthorizedException_Exception {
		try{
		
			checkInitDriverRegistroServiziCRUD(this.accordoCooperazioneService);
			this.logStartMethod("update", oldId, obj);
			this.authorize(false);
			if(this.exists(oldId)==false){
				throw new DriverRegistroServiziNotFound("Accordo Cooperazione non esistente");
			}
			obj.setSuperUser(ServerProperties.getInstance().getUser());
			obj.setOldIDAccordoForUpdate(this.convertToIdAccordoCooperazione(oldId));
			((IDriverRegistroServiziCRUD)this.accordoCooperazioneService.getDriver()).updateAccordoCooperazione(obj);
			this.logEndMethod("update");
			
		} catch(NotAuthorizedException e){
			throw throwNotAuthorizedException("update", e, Constants.CODE_NOT_AUTHORIZED_EXCEPTION, oldId);
		} catch (NotImplementedException e) {
			throw throwNotImplementedException("update", e, Constants.CODE_NOT_IMPLEMENTED_EXCEPTION, oldId);
		} catch (ServiceException e) {
			throw throwServiceException("update", e, Constants.CODE_SERVICE_EXCEPTION, oldId);
		} catch (DriverRegistroServiziNotFound e) {
			throw throwNotFoundException("update", e, Constants.CODE_NOT_FOUND_EXCEPTION, oldId);
		} catch (Exception e){
			throw throwServiceException("update", e, Constants.CODE_ERROR_GENERAL_EXCEPTION, oldId);
		}
    }
	
	@Override
	public void updateOrCreate(org.openspcoop2.core.registry.IdAccordoCooperazione oldId, AccordoCooperazione obj) throws RegistryServiceException_Exception,RegistryNotImplementedException_Exception,RegistryNotAuthorizedException_Exception {
		try{
		
			checkInitDriverRegistroServiziCRUD(this.accordoCooperazioneService);
			this.logStartMethod("updateOrCreate", oldId, obj);
			this.authorize(false);
			if(this.exists(oldId)==false){
				obj.setSuperUser(ServerProperties.getInstance().getUser());
				((IDriverRegistroServiziCRUD)this.accordoCooperazioneService.getDriver()).createAccordoCooperazione(obj);	
			}else{
				obj.setSuperUser(ServerProperties.getInstance().getUser());
				obj.setOldIDAccordoForUpdate(this.convertToIdAccordoCooperazione(oldId));
				((IDriverRegistroServiziCRUD)this.accordoCooperazioneService.getDriver()).updateAccordoCooperazione(obj);
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
	public void deleteById(org.openspcoop2.core.registry.IdAccordoCooperazione id) throws RegistryServiceException_Exception,RegistryNotImplementedException_Exception,RegistryNotAuthorizedException_Exception {
		try{
		
			checkInitDriverRegistroServiziCRUD(this.accordoCooperazioneService);
			this.logStartMethod("deleteById", id);
			this.authorize(false);
			AccordoCooperazione ac = null;
			try{
				ac = this.get(id);
			}catch(RegistryNotFoundException_Exception notFound){}
			if(ac!=null)
				((IDriverRegistroServiziCRUD)this.accordoCooperazioneService.getDriver()).deleteAccordoCooperazione(ac);
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
	public long deleteAll() throws RegistryServiceException_Exception,RegistryNotImplementedException_Exception,RegistryNotAuthorizedException_Exception {
		try{
		
			checkInitDriverRegistroServiziCRUD(this.accordoCooperazioneService);
			this.logStartMethod("deleteAll");
			this.authorize(false);
			List<IDAccordoCooperazione> list = null; 
			try{
				list = ((IDriverRegistroServiziGet)this.accordoCooperazioneService.getDriver()).getAllIdAccordiCooperazione(new FiltroRicercaAccordi());
			}catch(DriverRegistroServiziNotFound notFound){}
			long result = 0;
			if(list!=null && list.size()>0){
				result = list.size();
				for (IDAccordoCooperazione idAccordoCooperazione : list) {
					try{
						((IDriverRegistroServiziCRUD)this.accordoCooperazioneService.getDriver()).
							deleteAccordoCooperazione(((IDriverRegistroServiziGet)this.accordoCooperazioneService.getDriver()).getAccordoCooperazione(idAccordoCooperazione));
					}catch(DriverRegistroServiziNotFound notFound){}
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
	public long deleteAllByFilter(SearchFilterAccordoCooperazione filter) throws RegistryServiceException_Exception,RegistryNotImplementedException_Exception,RegistryNotAuthorizedException_Exception {
		try{
		
			checkInitDriverRegistroServiziCRUD(this.accordoCooperazioneService);
			this.logStartMethod("deleteAllByFilter", filter);
			this.authorize(false);
			List<IDAccordoCooperazione> list = this.readIds(filter, true);
			long result = 0;
			if(list!=null && list.size()>0){
				result = list.size();
				for (IDAccordoCooperazione idAccordoCooperazione : list) {
					try{
						((IDriverRegistroServiziCRUD)this.accordoCooperazioneService.getDriver()).
							deleteAccordoCooperazione(((IDriverRegistroServiziGet)this.accordoCooperazioneService.getDriver()).getAccordoCooperazione(idAccordoCooperazione));
					}catch(DriverRegistroServiziNotFound notFound){}
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
	public void delete(AccordoCooperazione obj) throws RegistryServiceException_Exception,RegistryNotImplementedException_Exception,RegistryNotAuthorizedException_Exception {
		try{
		
			checkInitDriverRegistroServiziCRUD(this.accordoCooperazioneService);
			this.logStartMethod("delete", obj);
			this.authorize(false);
			((IDriverRegistroServiziCRUD)this.accordoCooperazioneService.getDriver()).deleteAccordoCooperazione(obj);
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