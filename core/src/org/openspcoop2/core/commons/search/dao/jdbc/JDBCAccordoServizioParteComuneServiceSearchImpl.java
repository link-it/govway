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
package org.openspcoop2.core.commons.search.dao.jdbc;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import java.sql.Connection;

import org.slf4j.Logger;

import org.openspcoop2.utils.sql.ISQLQueryObject;

import org.openspcoop2.generic_project.expression.impl.sql.ISQLFieldConverter;
import org.openspcoop2.generic_project.dao.jdbc.utils.IJDBCFetch;
import org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject;
import org.openspcoop2.generic_project.dao.jdbc.IJDBCServiceSearchWithId;
import org.openspcoop2.core.commons.search.IdAccordoServizioParteComune;
import org.openspcoop2.core.commons.search.IdSoggetto;
import org.openspcoop2.generic_project.utils.UtilsTemplate;
import org.openspcoop2.generic_project.beans.CustomField;
import org.openspcoop2.generic_project.beans.InUse;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.NonNegativeNumber;
import org.openspcoop2.generic_project.beans.UnionExpression;
import org.openspcoop2.generic_project.beans.Union;
import org.openspcoop2.generic_project.beans.FunctionField;
import org.openspcoop2.generic_project.exception.MultipleResultException;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.generic_project.exception.NotImplementedException;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.generic_project.expression.IExpression;
import org.openspcoop2.generic_project.dao.jdbc.JDBCExpression;
import org.openspcoop2.generic_project.dao.jdbc.JDBCPaginatedExpression;

import org.openspcoop2.generic_project.dao.jdbc.JDBCServiceManagerProperties;
import org.openspcoop2.core.commons.search.dao.jdbc.converter.AccordoServizioParteComuneFieldConverter;
import org.openspcoop2.core.commons.search.dao.jdbc.fetch.AccordoServizioParteComuneFetch;
import org.openspcoop2.core.commons.search.dao.IDBSoggettoServiceSearch;
import org.openspcoop2.core.commons.search.AccordoServizioParteComune;
import org.openspcoop2.core.commons.search.AccordoServizioParteComuneAzione;
import org.openspcoop2.core.commons.search.AccordoServizioParteComuneGruppo;
import org.openspcoop2.core.commons.search.PortType;
import org.openspcoop2.core.commons.search.Soggetto;
import org.openspcoop2.core.commons.search.Resource;
import org.openspcoop2.core.commons.search.Operation;

/**     
 * JDBCAccordoServizioParteComuneServiceSearchImpl
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JDBCAccordoServizioParteComuneServiceSearchImpl implements IJDBCServiceSearchWithId<AccordoServizioParteComune, IdAccordoServizioParteComune, JDBCServiceManager> {

	private AccordoServizioParteComuneFieldConverter _accordoServizioParteComuneFieldConverter = null;
	public AccordoServizioParteComuneFieldConverter getAccordoServizioParteComuneFieldConverter() {
		if(this._accordoServizioParteComuneFieldConverter==null){
			this._accordoServizioParteComuneFieldConverter = new AccordoServizioParteComuneFieldConverter(this.jdbcServiceManager.getJdbcProperties().getDatabaseType());
		}		
		return this._accordoServizioParteComuneFieldConverter;
	}
	@Override
	public ISQLFieldConverter getFieldConverter() {
		return this.getAccordoServizioParteComuneFieldConverter();
	}
	
	private AccordoServizioParteComuneFetch accordoServizioParteComuneFetch = new AccordoServizioParteComuneFetch();
	public AccordoServizioParteComuneFetch getAccordoServizioParteComuneFetch() {
		return this.accordoServizioParteComuneFetch;
	}
	@Override
	public IJDBCFetch getFetch() {
		return getAccordoServizioParteComuneFetch();
	}
	
	
	private JDBCServiceManager jdbcServiceManager = null;

	@Override
	public void setServiceManager(JDBCServiceManager serviceManager) throws ServiceException{
		this.jdbcServiceManager = serviceManager;
	}
	
	@Override
	public JDBCServiceManager getServiceManager() throws ServiceException{
		return this.jdbcServiceManager;
	}
	public JDBCServiceManager getServiceManager(Connection connection, JDBCServiceManagerProperties jdbcProperties, Logger log) throws ServiceException{
		return new JDBCServiceManager(connection, jdbcProperties, log);
	}
	

	@Override
	public IdAccordoServizioParteComune convertToId(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, AccordoServizioParteComune accordoServizioParteComune) throws NotImplementedException, ServiceException, Exception{
	
		IdAccordoServizioParteComune idAccordoServizioParteComune = new IdAccordoServizioParteComune();
		idAccordoServizioParteComune.setIdSoggetto(accordoServizioParteComune.getIdReferente());
        idAccordoServizioParteComune.setNome(accordoServizioParteComune.getNome());
        idAccordoServizioParteComune.setVersione(accordoServizioParteComune.getVersione());
       
        idAccordoServizioParteComune.setServiceBinding(accordoServizioParteComune.getServiceBinding());
        
        return idAccordoServizioParteComune;

	}
	
	@Override
	public AccordoServizioParteComune get(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdAccordoServizioParteComune id, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException, MultipleResultException, NotImplementedException, ServiceException,Exception {
		Long id_accordoServizioParteComune = ( (id!=null && id.getId()!=null && id.getId()>0) ? id.getId() : this.findIdAccordoServizioParteComune(jdbcProperties, log, connection, sqlQueryObject, id, true));
		return this._get(jdbcProperties, log, connection, sqlQueryObject, id_accordoServizioParteComune,idMappingResolutionBehaviour);
		
		
	}
	
	@Override
	public boolean exists(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdAccordoServizioParteComune id) throws MultipleResultException, NotImplementedException, ServiceException,Exception {

		Long id_accordoServizioParteComune = this.findIdAccordoServizioParteComune(jdbcProperties, log, connection, sqlQueryObject, id, false);
		return id_accordoServizioParteComune != null && id_accordoServizioParteComune > 0;
		
	}
	
	@Override
	public List<IdAccordoServizioParteComune> findAllIds(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCPaginatedExpression expression, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotImplementedException, ServiceException,Exception {

		List<IdAccordoServizioParteComune> list = new ArrayList<IdAccordoServizioParteComune>();

		// TODO: implementazione non efficiente. 
		// Per ottenere una implementazione efficiente:
		// 1. Usare metodo select di questa classe indirizzando esattamente i field necessari a create l'ID logico
		// 2. Usare metodo getAccordoServizioParteComuneFetch() sul risultato della select per ottenere un oggetto AccordoServizioParteComune
		//	  La fetch con la map inserirà nell'oggetto solo i valori estratti 
		// 3. Usare metodo convertToId per ottenere l'id

        List<Long> ids = this.findAllTableIds(jdbcProperties, log, connection, sqlQueryObject, expression);
        
        for(Long id: ids) {
        	AccordoServizioParteComune accordoServizioParteComune = this.get(jdbcProperties, log, connection, sqlQueryObject, id, idMappingResolutionBehaviour);
			IdAccordoServizioParteComune idAccordoServizioParteComune = this.convertToId(jdbcProperties,log,connection,sqlQueryObject,accordoServizioParteComune);
        	list.add(idAccordoServizioParteComune);
        }

        return list;
		
	}
	
	@Override
	public List<AccordoServizioParteComune> findAll(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCPaginatedExpression expression, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotImplementedException, ServiceException,Exception {

        List<AccordoServizioParteComune> list = new ArrayList<AccordoServizioParteComune>();
        
        // TODO: implementazione non efficiente. 
		// Per ottenere una implementazione efficiente:
		// 1. Usare metodo select di questa classe indirizzando esattamente i field necessari
		// 2. Usare metodo getAccordoServizioParteComuneFetch() sul risultato della select per ottenere un oggetto AccordoServizioParteComune
		//	  La fetch con la map inserirà nell'oggetto solo i valori estratti 

        List<Long> ids = this.findAllTableIds(jdbcProperties, log, connection, sqlQueryObject, expression);
        
        for(Long id: ids) {
        	list.add(this.get(jdbcProperties, log, connection, sqlQueryObject, id, idMappingResolutionBehaviour));
        }

        return list;      
		
	}
	
	@Override
	public AccordoServizioParteComune find(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCExpression expression, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) 
		throws NotFoundException, MultipleResultException, NotImplementedException, ServiceException,Exception {

        long id = this.findTableId(jdbcProperties, log, connection, sqlQueryObject, expression);
        if(id>0){
        	return this.get(jdbcProperties, log, connection, sqlQueryObject, id, idMappingResolutionBehaviour);
        }else{
        	throw new NotFoundException("Entry with id["+id+"] not found");
        }
		
	}
	
	@Override
	public NonNegativeNumber count(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCExpression expression) throws NotImplementedException, ServiceException,Exception {
		
		List<Object> listaQuery = org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.prepareCount(jdbcProperties, log, connection, sqlQueryObject, expression,
												this.getAccordoServizioParteComuneFieldConverter(), AccordoServizioParteComune.model());
		
		sqlQueryObject.addSelectCountField(this.getAccordoServizioParteComuneFieldConverter().toTable(AccordoServizioParteComune.model())+".id","tot",true);
		
		_join(expression,sqlQueryObject);
		
		return org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.count(jdbcProperties, log, connection, sqlQueryObject, expression,
																			this.getAccordoServizioParteComuneFieldConverter(), AccordoServizioParteComune.model(),listaQuery);
	}

	@Override
	public InUse inUse(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdAccordoServizioParteComune id) throws NotFoundException, NotImplementedException, ServiceException,Exception {
		
		Long id_accordoServizioParteComune = this.findIdAccordoServizioParteComune(jdbcProperties, log, connection, sqlQueryObject, id, true);
        return this._inUse(jdbcProperties, log, connection, sqlQueryObject, id_accordoServizioParteComune);
		
	}

	@Override
	public List<Object> select(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, 
													JDBCPaginatedExpression paginatedExpression, IField field) throws ServiceException,NotFoundException,NotImplementedException,Exception {
		return this.select(jdbcProperties, log, connection, sqlQueryObject,
								paginatedExpression, false, field);
	}
	
	@Override
	public List<Object> select(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, 
													JDBCPaginatedExpression paginatedExpression, boolean distinct, IField field) throws ServiceException,NotFoundException,NotImplementedException,Exception {
		List<Map<String,Object>> map = 
			this.select(jdbcProperties, log, connection, sqlQueryObject, paginatedExpression, distinct, new IField[]{field});
		return org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.selectSingleObject(map);
	}
	
	@Override
	public List<Map<String,Object>> select(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, 
													JDBCPaginatedExpression paginatedExpression, IField ... field) throws ServiceException,NotFoundException,NotImplementedException,Exception {
		return this.select(jdbcProperties, log, connection, sqlQueryObject,
								paginatedExpression, false, field);
	}
	
	@Override
	public List<Map<String,Object>> select(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, 
													JDBCPaginatedExpression paginatedExpression, boolean distinct, IField ... field) throws ServiceException,NotFoundException,NotImplementedException,Exception {
		
		org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.setFields(sqlQueryObject,paginatedExpression,field);
		try{
		
			ISQLQueryObject sqlQueryObjectDistinct = 
						org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.prepareSqlQueryObjectForSelectDistinct(distinct,sqlQueryObject, paginatedExpression, log,
												this.getAccordoServizioParteComuneFieldConverter(), field);

			return _select(jdbcProperties, log, connection, sqlQueryObject, paginatedExpression, sqlQueryObjectDistinct);
			
		}finally{
			org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.removeFields(sqlQueryObject,paginatedExpression,field);
		}
	}

	@Override
	public Object aggregate(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, 
													JDBCExpression expression, FunctionField functionField) throws ServiceException,NotFoundException,NotImplementedException,Exception {
		Map<String,Object> map = 
			this.aggregate(jdbcProperties, log, connection, sqlQueryObject, expression, new FunctionField[]{functionField});
		return org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.selectAggregateObject(map,functionField);
	}
	
	@Override
	public Map<String,Object> aggregate(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, 
													JDBCExpression expression, FunctionField ... functionField) throws ServiceException,NotFoundException,NotImplementedException,Exception {													
		
		org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.setFields(sqlQueryObject,expression,functionField);
		try{
			List<Map<String,Object>> list = _select(jdbcProperties, log, connection, sqlQueryObject, expression);
			return list.get(0);
		}finally{
			org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.removeFields(sqlQueryObject,expression,functionField);
		}
	}

	@Override
	public List<Map<String,Object>> groupBy(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, 
													JDBCExpression expression, FunctionField ... functionField) throws ServiceException,NotFoundException,NotImplementedException,Exception {
		
		if(expression.getGroupByFields().size()<=0){
			throw new ServiceException("GroupBy conditions not found in expression");
		}
		
		org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.setFields(sqlQueryObject,expression,functionField);
		try{
			return _select(jdbcProperties, log, connection, sqlQueryObject, expression);
		}finally{
			org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.removeFields(sqlQueryObject,expression,functionField);
		}
	}
	

	@Override
	public List<Map<String,Object>> groupBy(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, 
													JDBCPaginatedExpression paginatedExpression, FunctionField ... functionField) throws ServiceException,NotFoundException,NotImplementedException,Exception {
		
		if(paginatedExpression.getGroupByFields().size()<=0){
			throw new ServiceException("GroupBy conditions not found in expression");
		}
		
		org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.setFields(sqlQueryObject,paginatedExpression,functionField);
		try{
			return _select(jdbcProperties, log, connection, sqlQueryObject, paginatedExpression);
		}finally{
			org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.removeFields(sqlQueryObject,paginatedExpression,functionField);
		}
	}
	
	protected List<Map<String,Object>> _select(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, 
												IExpression expression) throws ServiceException,NotFoundException,NotImplementedException,Exception {
		return _select(jdbcProperties, log, connection, sqlQueryObject, expression, null);
	}
	protected List<Map<String,Object>> _select(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, 
												IExpression expression, ISQLQueryObject sqlQueryObjectDistinct) throws ServiceException,NotFoundException,NotImplementedException,Exception {
		
		List<Object> listaQuery = new ArrayList<Object>();
		List<JDBCObject> listaParams = new ArrayList<JDBCObject>();
		List<Object> returnField = org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.prepareSelect(jdbcProperties, log, connection, sqlQueryObject, 
        						expression, this.getAccordoServizioParteComuneFieldConverter(), AccordoServizioParteComune.model(), 
        						listaQuery,listaParams);
		
		_join(expression,sqlQueryObject);
        
        List<Map<String,Object>> list = org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.select(jdbcProperties, log, connection,
        								org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.prepareSqlQueryObjectForSelectDistinct(sqlQueryObject,sqlQueryObjectDistinct), 
        								expression, this.getAccordoServizioParteComuneFieldConverter(), AccordoServizioParteComune.model(),
        								listaQuery,listaParams,returnField);
		if(list!=null && list.size()>0){
			return list;
		}
		else{
			throw new NotFoundException("Not Found");
		}
	}
	
	@Override
	public List<Map<String,Object>> union(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, 
												Union union, UnionExpression ... unionExpression) throws ServiceException,NotFoundException,NotImplementedException,Exception {		
		
		List<ISQLQueryObject> sqlQueryObjectInnerList = new ArrayList<ISQLQueryObject>();
		List<JDBCObject> jdbcObjects = new ArrayList<JDBCObject>();
		List<Class<?>> returnClassTypes = org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.prepareUnion(jdbcProperties, log, connection, sqlQueryObject, 
        						this.getAccordoServizioParteComuneFieldConverter(), AccordoServizioParteComune.model(), 
        						sqlQueryObjectInnerList, jdbcObjects, union, unionExpression);
		
		if(unionExpression!=null){
			for (int i = 0; i < unionExpression.length; i++) {
				UnionExpression ue = unionExpression[i];
				IExpression expression = ue.getExpression();
				_join(expression,sqlQueryObjectInnerList.get(i));
			}
		}
        
        List<Map<String,Object>> list = org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.union(jdbcProperties, log, connection, sqlQueryObject, 
        								this.getAccordoServizioParteComuneFieldConverter(), AccordoServizioParteComune.model(), 
        								sqlQueryObjectInnerList, jdbcObjects, returnClassTypes, union, unionExpression);
        if(list!=null && list.size()>0){
			return list;
		}
		else{
			throw new NotFoundException("Not Found");
		}								
	}
	
	@Override
	public NonNegativeNumber unionCount(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, 
												Union union, UnionExpression ... unionExpression) throws ServiceException,NotFoundException,NotImplementedException,Exception {		
		
		List<ISQLQueryObject> sqlQueryObjectInnerList = new ArrayList<ISQLQueryObject>();
		List<JDBCObject> jdbcObjects = new ArrayList<JDBCObject>();
		List<Class<?>> returnClassTypes = org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.prepareUnionCount(jdbcProperties, log, connection, sqlQueryObject, 
        						this.getAccordoServizioParteComuneFieldConverter(), AccordoServizioParteComune.model(), 
        						sqlQueryObjectInnerList, jdbcObjects, union, unionExpression);
		
		if(unionExpression!=null){
			for (int i = 0; i < unionExpression.length; i++) {
				UnionExpression ue = unionExpression[i];
				IExpression expression = ue.getExpression();
				_join(expression,sqlQueryObjectInnerList.get(i));
			}
		}
        
        NonNegativeNumber number = org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.unionCount(jdbcProperties, log, connection, sqlQueryObject, 
        								this.getAccordoServizioParteComuneFieldConverter(), AccordoServizioParteComune.model(), 
        								sqlQueryObjectInnerList, jdbcObjects, returnClassTypes, union, unionExpression);
        if(number!=null && number.longValue()>=0){
			return number;
		}
		else{
			throw new NotFoundException("Not Found");
		}
	}



	// -- ConstructorExpression	

	@Override
	public JDBCExpression newExpression(Logger log) throws NotImplementedException, ServiceException {
		try{
			return new JDBCExpression(this.getAccordoServizioParteComuneFieldConverter());
		}catch(Exception e){
			throw new ServiceException(e);
		}
	}


	@Override
	public JDBCPaginatedExpression newPaginatedExpression(Logger log) throws NotImplementedException, ServiceException {
		try{
			return new JDBCPaginatedExpression(this.getAccordoServizioParteComuneFieldConverter());
		}catch(Exception e){
			throw new ServiceException(e);
		}
	}
	
	@Override
	public JDBCExpression toExpression(JDBCPaginatedExpression paginatedExpression, Logger log) throws NotImplementedException, ServiceException {
		try{
			return new JDBCExpression(paginatedExpression);
		}catch(Exception e){
			throw new ServiceException(e);
		}
	}

	@Override
	public JDBCPaginatedExpression toPaginatedExpression(JDBCExpression expression, Logger log) throws NotImplementedException, ServiceException {
		try{
			return new JDBCPaginatedExpression(expression);
		}catch(Exception e){
			throw new ServiceException(e);
		}
	}
	
	
	
	// -- DB

	@Override
	public void mappingTableIds(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdAccordoServizioParteComune id, AccordoServizioParteComune obj) throws NotFoundException,NotImplementedException,ServiceException,Exception{
		_mappingTableIds(jdbcProperties,log,connection,sqlQueryObject,obj,
				this.get(jdbcProperties,log,connection,sqlQueryObject,id,null));
	}
	
	@Override
	public void mappingTableIds(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, AccordoServizioParteComune obj) throws NotFoundException,NotImplementedException,ServiceException,Exception{
		_mappingTableIds(jdbcProperties,log,connection,sqlQueryObject,obj,
				this.get(jdbcProperties,log,connection,sqlQueryObject,tableId,null));
	}
	private void _mappingTableIds(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, AccordoServizioParteComune obj, AccordoServizioParteComune imgSaved) throws NotFoundException,NotImplementedException,ServiceException,Exception{
		if(imgSaved==null){
			return;
		}
		obj.setId(imgSaved.getId());
		if(obj.getIdReferente()!=null && 
				imgSaved.getIdReferente()!=null){
			obj.getIdReferente().setId(imgSaved.getIdReferente().getId());
		}
		if(obj.getAccordoServizioParteComuneAzioneList()!=null){
			List<org.openspcoop2.core.commons.search.AccordoServizioParteComuneAzione> listObj_ = obj.getAccordoServizioParteComuneAzioneList();
			for(org.openspcoop2.core.commons.search.AccordoServizioParteComuneAzione itemObj_ : listObj_){
				org.openspcoop2.core.commons.search.AccordoServizioParteComuneAzione itemAlreadySaved_ = null;
				if(imgSaved.getAccordoServizioParteComuneAzioneList()!=null){
					List<org.openspcoop2.core.commons.search.AccordoServizioParteComuneAzione> listImgSaved_ = imgSaved.getAccordoServizioParteComuneAzioneList();
					for(org.openspcoop2.core.commons.search.AccordoServizioParteComuneAzione itemImgSaved_ : listImgSaved_){
						boolean objEqualsToImgSaved_ = false;
						objEqualsToImgSaved_ = org.openspcoop2.generic_project.utils.Utilities.equals(itemObj_.getNome(),itemImgSaved_.getNome());
						if(objEqualsToImgSaved_){
							itemAlreadySaved_=itemImgSaved_;
							break;
						}
					}
				}
				if(itemAlreadySaved_!=null){
					itemObj_.setId(itemAlreadySaved_.getId());
					if(itemObj_.getIdAccordoServizioParteComune()!=null && 
							itemAlreadySaved_.getIdAccordoServizioParteComune()!=null){
						itemObj_.getIdAccordoServizioParteComune().setId(itemAlreadySaved_.getIdAccordoServizioParteComune().getId());
						if(itemObj_.getIdAccordoServizioParteComune().getIdSoggetto()!=null && 
								itemAlreadySaved_.getIdAccordoServizioParteComune().getIdSoggetto()!=null){
							itemObj_.getIdAccordoServizioParteComune().getIdSoggetto().setId(itemAlreadySaved_.getIdAccordoServizioParteComune().getIdSoggetto().getId());
						}
					}
				}
			}
		}
		if(obj.getPortTypeList()!=null){
			List<org.openspcoop2.core.commons.search.PortType> listObj_ = obj.getPortTypeList();
			for(org.openspcoop2.core.commons.search.PortType itemObj_ : listObj_){
				org.openspcoop2.core.commons.search.PortType itemAlreadySaved_ = null;
				if(imgSaved.getPortTypeList()!=null){
					List<org.openspcoop2.core.commons.search.PortType> listImgSaved_ = imgSaved.getPortTypeList();
					for(org.openspcoop2.core.commons.search.PortType itemImgSaved_ : listImgSaved_){
						boolean objEqualsToImgSaved_ = false;
						objEqualsToImgSaved_ = org.openspcoop2.generic_project.utils.Utilities.equals(itemObj_.getNome(),itemImgSaved_.getNome());
						if(objEqualsToImgSaved_){
							itemAlreadySaved_=itemImgSaved_;
							break;
						}
					}
				}
				if(itemAlreadySaved_!=null){
					itemObj_.setId(itemAlreadySaved_.getId());
					if(itemObj_.getOperationList()!=null){
						List<org.openspcoop2.core.commons.search.Operation> listObj_portType = itemObj_.getOperationList();
						for(org.openspcoop2.core.commons.search.Operation itemObj_portType : listObj_portType){
							org.openspcoop2.core.commons.search.Operation itemAlreadySaved_portType = null;
							if(itemAlreadySaved_.getOperationList()!=null){
								List<org.openspcoop2.core.commons.search.Operation> listImgSaved_portType = itemAlreadySaved_.getOperationList();
								for(org.openspcoop2.core.commons.search.Operation itemImgSaved_portType : listImgSaved_portType){
									boolean objEqualsToImgSaved_portType = false;
									objEqualsToImgSaved_portType = org.openspcoop2.generic_project.utils.Utilities.equals(itemObj_portType.getNome(),itemImgSaved_portType.getNome());
									if(objEqualsToImgSaved_portType){
										itemAlreadySaved_portType=itemImgSaved_portType;
										break;
									}
								}
							}
							if(itemAlreadySaved_portType!=null){
								itemObj_portType.setId(itemAlreadySaved_portType.getId());
								if(itemObj_portType.getIdPortType()!=null && 
										itemAlreadySaved_portType.getIdPortType()!=null){
									itemObj_portType.getIdPortType().setId(itemAlreadySaved_portType.getIdPortType().getId());
									if(itemObj_portType.getIdPortType().getIdAccordoServizioParteComune()!=null && 
											itemAlreadySaved_portType.getIdPortType().getIdAccordoServizioParteComune()!=null){
										itemObj_portType.getIdPortType().getIdAccordoServizioParteComune().setId(itemAlreadySaved_portType.getIdPortType().getIdAccordoServizioParteComune().getId());
										if(itemObj_portType.getIdPortType().getIdAccordoServizioParteComune().getIdSoggetto()!=null && 
												itemAlreadySaved_portType.getIdPortType().getIdAccordoServizioParteComune().getIdSoggetto()!=null){
											itemObj_portType.getIdPortType().getIdAccordoServizioParteComune().getIdSoggetto().setId(itemAlreadySaved_portType.getIdPortType().getIdAccordoServizioParteComune().getIdSoggetto().getId());
										}
									}
								}
							}
						}
					}
					if(itemObj_.getIdAccordoServizioParteComune()!=null && 
							itemAlreadySaved_.getIdAccordoServizioParteComune()!=null){
						itemObj_.getIdAccordoServizioParteComune().setId(itemAlreadySaved_.getIdAccordoServizioParteComune().getId());
						if(itemObj_.getIdAccordoServizioParteComune().getIdSoggetto()!=null && 
								itemAlreadySaved_.getIdAccordoServizioParteComune().getIdSoggetto()!=null){
							itemObj_.getIdAccordoServizioParteComune().getIdSoggetto().setId(itemAlreadySaved_.getIdAccordoServizioParteComune().getIdSoggetto().getId());
						}
					}
				}
			}
		}
		if(obj.getResourceList()!=null){
			List<org.openspcoop2.core.commons.search.Resource> listObj_ = obj.getResourceList();
			for(org.openspcoop2.core.commons.search.Resource itemObj_ : listObj_){
				org.openspcoop2.core.commons.search.Resource itemAlreadySaved_ = null;
				if(imgSaved.getResourceList()!=null){
					List<org.openspcoop2.core.commons.search.Resource> listImgSaved_ = imgSaved.getResourceList();
					for(org.openspcoop2.core.commons.search.Resource itemImgSaved_ : listImgSaved_){
						boolean objEqualsToImgSaved_ = false;
						objEqualsToImgSaved_ = org.openspcoop2.generic_project.utils.Utilities.equals(itemObj_.getNome(),itemImgSaved_.getNome());
						if(objEqualsToImgSaved_){
							itemAlreadySaved_=itemImgSaved_;
							break;
						}
					}
				}
				if(itemAlreadySaved_!=null){
					itemObj_.setId(itemAlreadySaved_.getId());
					if(itemObj_.getIdAccordoServizioParteComune()!=null && 
							itemAlreadySaved_.getIdAccordoServizioParteComune()!=null){
						itemObj_.getIdAccordoServizioParteComune().setId(itemAlreadySaved_.getIdAccordoServizioParteComune().getId());
						if(itemObj_.getIdAccordoServizioParteComune().getIdSoggetto()!=null && 
								itemAlreadySaved_.getIdAccordoServizioParteComune().getIdSoggetto()!=null){
							itemObj_.getIdAccordoServizioParteComune().getIdSoggetto().setId(itemAlreadySaved_.getIdAccordoServizioParteComune().getIdSoggetto().getId());
						}
					}
				}
			}
		}
		if(obj.getAccordoServizioParteComuneGruppoList()!=null){
			List<org.openspcoop2.core.commons.search.AccordoServizioParteComuneGruppo> listObj_ = obj.getAccordoServizioParteComuneGruppoList();
			for(org.openspcoop2.core.commons.search.AccordoServizioParteComuneGruppo itemObj_ : listObj_){
				org.openspcoop2.core.commons.search.AccordoServizioParteComuneGruppo itemAlreadySaved_ = null;
				if(imgSaved.getAccordoServizioParteComuneGruppoList()!=null){
					List<org.openspcoop2.core.commons.search.AccordoServizioParteComuneGruppo> listImgSaved_ = imgSaved.getAccordoServizioParteComuneGruppoList();
					for(org.openspcoop2.core.commons.search.AccordoServizioParteComuneGruppo itemImgSaved_ : listImgSaved_){
						boolean objEqualsToImgSaved_ = false;
						objEqualsToImgSaved_ = org.openspcoop2.generic_project.utils.Utilities.equals(itemObj_.getIdGruppo().getNome(),itemImgSaved_.getIdGruppo().getNome()) &&
						 						 org.openspcoop2.generic_project.utils.Utilities.equals(itemObj_.getIdAccordoServizioParteComune().getNome(),itemImgSaved_.getIdAccordoServizioParteComune().getNome())  &&
						 						 org.openspcoop2.generic_project.utils.Utilities.equals(itemObj_.getIdAccordoServizioParteComune().getIdSoggetto().getTipo(),itemImgSaved_.getIdAccordoServizioParteComune().getIdSoggetto().getTipo())  &&
						 						 org.openspcoop2.generic_project.utils.Utilities.equals(itemObj_.getIdAccordoServizioParteComune().getIdSoggetto().getNome(),itemImgSaved_.getIdAccordoServizioParteComune().getIdSoggetto().getNome())  &&
						 						 org.openspcoop2.generic_project.utils.Utilities.equals(itemObj_.getIdAccordoServizioParteComune().getVersione(),itemImgSaved_.getIdAccordoServizioParteComune().getVersione());
						if(objEqualsToImgSaved_){
							itemAlreadySaved_=itemImgSaved_;
							break;
						}
					}
				}
				if(itemAlreadySaved_!=null){
					itemObj_.setId(itemAlreadySaved_.getId());
					if(itemObj_.getIdGruppo()!=null && 
							itemAlreadySaved_.getIdGruppo()!=null){
						itemObj_.getIdGruppo().setId(itemAlreadySaved_.getIdGruppo().getId());
					}
					if(itemObj_.getIdAccordoServizioParteComune()!=null && 
							itemAlreadySaved_.getIdAccordoServizioParteComune()!=null){
						itemObj_.getIdAccordoServizioParteComune().setId(itemAlreadySaved_.getIdAccordoServizioParteComune().getId());
						if(itemObj_.getIdAccordoServizioParteComune().getIdSoggetto()!=null && 
								itemAlreadySaved_.getIdAccordoServizioParteComune().getIdSoggetto()!=null){
							itemObj_.getIdAccordoServizioParteComune().getIdSoggetto().setId(itemAlreadySaved_.getIdAccordoServizioParteComune().getIdSoggetto().getId());
						}
					}
				}
			}
		}
              
	}
	
	@Override
	public AccordoServizioParteComune get(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException, MultipleResultException, NotImplementedException, ServiceException, Exception {
		return this._get(jdbcProperties, log, connection, sqlQueryObject, Long.valueOf(tableId), idMappingResolutionBehaviour);
	}
	
	private AccordoServizioParteComune _get(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, Long tableId, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException, MultipleResultException, NotImplementedException, ServiceException, Exception {
	
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);
		
		ISQLQueryObject sqlQueryObjectGet = sqlQueryObject.newSQLQueryObject();
				
		AccordoServizioParteComune accordoServizioParteComune = new AccordoServizioParteComune();
		

		// Object accordoServizioParteComune
		ISQLQueryObject sqlQueryObjectGet_accordoServizioParteComune = sqlQueryObjectGet.newSQLQueryObject();
		sqlQueryObjectGet_accordoServizioParteComune.setANDLogicOperator(true);
		sqlQueryObjectGet_accordoServizioParteComune.addFromTable(this.getAccordoServizioParteComuneFieldConverter().toTable(AccordoServizioParteComune.model()));
		sqlQueryObjectGet_accordoServizioParteComune.addSelectField("id");
		sqlQueryObjectGet_accordoServizioParteComune.addSelectField(this.getAccordoServizioParteComuneFieldConverter().toColumn(AccordoServizioParteComune.model().NOME,true));
		sqlQueryObjectGet_accordoServizioParteComune.addSelectField(this.getAccordoServizioParteComuneFieldConverter().toColumn(AccordoServizioParteComune.model().VERSIONE,true));
		sqlQueryObjectGet_accordoServizioParteComune.addSelectField(this.getAccordoServizioParteComuneFieldConverter().toColumn(AccordoServizioParteComune.model().SERVICE_BINDING,true));
		sqlQueryObjectGet_accordoServizioParteComune.addSelectField(this.getAccordoServizioParteComuneFieldConverter().toColumn(AccordoServizioParteComune.model().CANALE,true));
		sqlQueryObjectGet_accordoServizioParteComune.addWhereCondition("id=?");

		// Get accordoServizioParteComune
		accordoServizioParteComune = (AccordoServizioParteComune) jdbcUtilities.executeQuerySingleResult(sqlQueryObjectGet_accordoServizioParteComune.createSQLQuery(), jdbcProperties.isShowSql(), AccordoServizioParteComune.model(), this.getAccordoServizioParteComuneFetch(),
			new JDBCObject(tableId,Long.class));

		
		// Recupero idSoggetto
		ISQLQueryObject sqlQueryObjectGet_accordoServizioParteComune_soggetto = sqlQueryObjectGet.newSQLQueryObject();
		sqlQueryObjectGet_accordoServizioParteComune_soggetto.addFromTable(this.getAccordoServizioParteComuneFieldConverter().toTable(AccordoServizioParteComune.model()));
		sqlQueryObjectGet_accordoServizioParteComune_soggetto.addSelectField("id_referente");
		sqlQueryObjectGet_accordoServizioParteComune_soggetto.setANDLogicOperator(true);
		sqlQueryObjectGet_accordoServizioParteComune_soggetto.addWhereCondition("id=?");
		
		// Recupero _accordoServizioParteComune_soggetto
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject [] searchParams_accordoServizioParteComune_soggetto = new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject [] { 
				new JDBCObject(accordoServizioParteComune.getId(), Long.class)
		};
		Long id_accordoServizioParteComune_soggetto = 
			(Long) jdbcUtilities.executeQuerySingleResult(sqlQueryObjectGet_accordoServizioParteComune_soggetto.createSQLQuery(), jdbcProperties.isShowSql(),
			Long.class, searchParams_accordoServizioParteComune_soggetto);
		
		if(id_accordoServizioParteComune_soggetto!=null && id_accordoServizioParteComune_soggetto>0){
			Soggetto soggetto = ((IDBSoggettoServiceSearch)this.getServiceManager(connection, jdbcProperties, log).getSoggettoServiceSearch()).get(id_accordoServizioParteComune_soggetto);
			IdSoggetto idReferente = new IdSoggetto();
			idReferente.setTipo(soggetto.getTipoSoggetto());
			idReferente.setNome(soggetto.getNomeSoggetto());
			accordoServizioParteComune.setIdReferente(idReferente);
		}


		// Object accordoServizioParteComune_accordoServizioParteComuneAzione
		ISQLQueryObject sqlQueryObjectGet_accordoServizioParteComune_accordoServizioParteComuneAzione = sqlQueryObjectGet.newSQLQueryObject();
		sqlQueryObjectGet_accordoServizioParteComune_accordoServizioParteComuneAzione.setANDLogicOperator(true);
		sqlQueryObjectGet_accordoServizioParteComune_accordoServizioParteComuneAzione.addFromTable(this.getAccordoServizioParteComuneFieldConverter().toTable(AccordoServizioParteComune.model().ACCORDO_SERVIZIO_PARTE_COMUNE_AZIONE));
		sqlQueryObjectGet_accordoServizioParteComune_accordoServizioParteComuneAzione.addSelectField("id");
		sqlQueryObjectGet_accordoServizioParteComune_accordoServizioParteComuneAzione.addSelectField(this.getAccordoServizioParteComuneFieldConverter().toColumn(AccordoServizioParteComune.model().ACCORDO_SERVIZIO_PARTE_COMUNE_AZIONE.NOME,true));
		sqlQueryObjectGet_accordoServizioParteComune_accordoServizioParteComuneAzione.addWhereCondition("id_accordo=?");

		// Get accordoServizioParteComune_accordoServizioParteComuneAzione
		java.util.List<Object> accordoServizioParteComune_accordoServizioParteComuneAzione_list = jdbcUtilities.executeQuery(sqlQueryObjectGet_accordoServizioParteComune_accordoServizioParteComuneAzione.createSQLQuery(), jdbcProperties.isShowSql(), AccordoServizioParteComune.model().ACCORDO_SERVIZIO_PARTE_COMUNE_AZIONE, this.getAccordoServizioParteComuneFetch(),
			new JDBCObject(accordoServizioParteComune.getId(),Long.class));

		if(accordoServizioParteComune_accordoServizioParteComuneAzione_list != null) {
			for (Object accordoServizioParteComune_accordoServizioParteComuneAzione_object: accordoServizioParteComune_accordoServizioParteComuneAzione_list) {
				AccordoServizioParteComuneAzione accordoServizioParteComune_accordoServizioParteComuneAzione = (AccordoServizioParteComuneAzione) accordoServizioParteComune_accordoServizioParteComuneAzione_object;


				accordoServizioParteComune.addAccordoServizioParteComuneAzione(accordoServizioParteComune_accordoServizioParteComuneAzione);
			}
		}

		// Object accordoServizioParteComune_portType
		ISQLQueryObject sqlQueryObjectGet_accordoServizioParteComune_portType = sqlQueryObjectGet.newSQLQueryObject();
		sqlQueryObjectGet_accordoServizioParteComune_portType.setANDLogicOperator(true);
		sqlQueryObjectGet_accordoServizioParteComune_portType.addFromTable(this.getAccordoServizioParteComuneFieldConverter().toTable(AccordoServizioParteComune.model().PORT_TYPE));
		sqlQueryObjectGet_accordoServizioParteComune_portType.addSelectField("id");
		sqlQueryObjectGet_accordoServizioParteComune_portType.addSelectField(this.getAccordoServizioParteComuneFieldConverter().toColumn(AccordoServizioParteComune.model().PORT_TYPE.NOME,true));
		sqlQueryObjectGet_accordoServizioParteComune_portType.addWhereCondition("id_accordo=?");

		// Get accordoServizioParteComune_portType
		java.util.List<Object> accordoServizioParteComune_portType_list = jdbcUtilities.executeQuery(sqlQueryObjectGet_accordoServizioParteComune_portType.createSQLQuery(), jdbcProperties.isShowSql(), AccordoServizioParteComune.model().PORT_TYPE, this.getAccordoServizioParteComuneFetch(),
			new JDBCObject(accordoServizioParteComune.getId(),Long.class));

		if(accordoServizioParteComune_portType_list != null) {
			for (Object accordoServizioParteComune_portType_object: accordoServizioParteComune_portType_list) {
				PortType accordoServizioParteComune_portType = (PortType) accordoServizioParteComune_portType_object;



				// Object accordoServizioParteComune_portType_operation
				ISQLQueryObject sqlQueryObjectGet_accordoServizioParteComune_portType_operation = sqlQueryObjectGet.newSQLQueryObject();
				sqlQueryObjectGet_accordoServizioParteComune_portType_operation.setANDLogicOperator(true);
				sqlQueryObjectGet_accordoServizioParteComune_portType_operation.addFromTable(this.getAccordoServizioParteComuneFieldConverter().toTable(AccordoServizioParteComune.model().PORT_TYPE.OPERATION));
				sqlQueryObjectGet_accordoServizioParteComune_portType_operation.addSelectField("id");
				sqlQueryObjectGet_accordoServizioParteComune_portType_operation.addSelectField(this.getAccordoServizioParteComuneFieldConverter().toColumn(AccordoServizioParteComune.model().PORT_TYPE.OPERATION.NOME,true));
				sqlQueryObjectGet_accordoServizioParteComune_portType_operation.addWhereCondition("id_port_type=?");

				// Get accordoServizioParteComune_portType_operation
				java.util.List<Object> accordoServizioParteComune_portType_operation_list = jdbcUtilities.executeQuery(sqlQueryObjectGet_accordoServizioParteComune_portType_operation.createSQLQuery(), jdbcProperties.isShowSql(), AccordoServizioParteComune.model().PORT_TYPE.OPERATION, this.getAccordoServizioParteComuneFetch(),
					new JDBCObject(accordoServizioParteComune_portType.getId(),Long.class));

				if(accordoServizioParteComune_portType_operation_list != null) {
					for (Object accordoServizioParteComune_portType_operation_object: accordoServizioParteComune_portType_operation_list) {
						Operation accordoServizioParteComune_portType_operation = (Operation) accordoServizioParteComune_portType_operation_object;


						accordoServizioParteComune_portType.addOperation(accordoServizioParteComune_portType_operation);
					}
				}
				accordoServizioParteComune.addPortType(accordoServizioParteComune_portType);
			}
		}

		// Object accordoServizioParteComune_resource
		ISQLQueryObject sqlQueryObjectGet_accordoServizioParteComune_resource = sqlQueryObjectGet.newSQLQueryObject();
		sqlQueryObjectGet_accordoServizioParteComune_resource.setANDLogicOperator(true);
		sqlQueryObjectGet_accordoServizioParteComune_resource.addFromTable(this.getAccordoServizioParteComuneFieldConverter().toTable(AccordoServizioParteComune.model().RESOURCE));
		sqlQueryObjectGet_accordoServizioParteComune_resource.addSelectField("id");
		sqlQueryObjectGet_accordoServizioParteComune_resource.addSelectField(this.getAccordoServizioParteComuneFieldConverter().toColumn(AccordoServizioParteComune.model().RESOURCE.NOME,true));
		sqlQueryObjectGet_accordoServizioParteComune_resource.addSelectField(this.getAccordoServizioParteComuneFieldConverter().toColumn(AccordoServizioParteComune.model().RESOURCE.HTTP_METHOD,true));
		sqlQueryObjectGet_accordoServizioParteComune_resource.addSelectField(this.getAccordoServizioParteComuneFieldConverter().toColumn(AccordoServizioParteComune.model().RESOURCE.PATH,true));
		sqlQueryObjectGet_accordoServizioParteComune_resource.addWhereCondition("id_accordo=?");

		// Get accordoServizioParteComune_resource
		java.util.List<Object> accordoServizioParteComune_resource_list = jdbcUtilities.executeQuery(sqlQueryObjectGet_accordoServizioParteComune_resource.createSQLQuery(), jdbcProperties.isShowSql(), AccordoServizioParteComune.model().RESOURCE, this.getAccordoServizioParteComuneFetch(),
			new JDBCObject(accordoServizioParteComune.getId(),Long.class));

		if(accordoServizioParteComune_resource_list != null) {
			for (Object accordoServizioParteComune_resource_object: accordoServizioParteComune_resource_list) {
				Resource accordoServizioParteComune_resource = (Resource) accordoServizioParteComune_resource_object;


				accordoServizioParteComune.addResource(accordoServizioParteComune_resource);
			}
		}

		// Object accordoServizioParteComune_accordoServizioParteComuneGruppo
		ISQLQueryObject sqlQueryObjectGet_accordoServizioParteComune_accordoServizioParteComuneGruppo = sqlQueryObjectGet.newSQLQueryObject();
		sqlQueryObjectGet_accordoServizioParteComune_accordoServizioParteComuneGruppo.setANDLogicOperator(true);
		sqlQueryObjectGet_accordoServizioParteComune_accordoServizioParteComuneGruppo.addFromTable(this.getAccordoServizioParteComuneFieldConverter().toTable(AccordoServizioParteComune.model().ACCORDO_SERVIZIO_PARTE_COMUNE_GRUPPO));
		sqlQueryObjectGet_accordoServizioParteComune_accordoServizioParteComuneGruppo.addSelectField("id");
		sqlQueryObjectGet_accordoServizioParteComune_accordoServizioParteComuneGruppo.addWhereCondition("id_accordo=?");

		// Get accordoServizioParteComune_accordoServizioParteComuneGruppo
		java.util.List<Object> accordoServizioParteComune_accordoServizioParteComuneGruppo_list = (java.util.List<Object>) jdbcUtilities.executeQuery(sqlQueryObjectGet_accordoServizioParteComune_accordoServizioParteComuneGruppo.createSQLQuery(), jdbcProperties.isShowSql(), AccordoServizioParteComune.model().ACCORDO_SERVIZIO_PARTE_COMUNE_GRUPPO, this.getAccordoServizioParteComuneFetch(),
			new JDBCObject(accordoServizioParteComune.getId(),Long.class));

		if(accordoServizioParteComune_accordoServizioParteComuneGruppo_list != null) {
			for (Object accordoServizioParteComune_accordoServizioParteComuneGruppo_object: accordoServizioParteComune_accordoServizioParteComuneGruppo_list) {
				AccordoServizioParteComuneGruppo accordoServizioParteComune_accordoServizioParteComuneGruppo = (AccordoServizioParteComuneGruppo) accordoServizioParteComune_accordoServizioParteComuneGruppo_object;


				if(idMappingResolutionBehaviour==null ||
					(org.openspcoop2.generic_project.beans.IDMappingBehaviour.ENABLED.equals(idMappingResolutionBehaviour) || org.openspcoop2.generic_project.beans.IDMappingBehaviour.USE_TABLE_ID.equals(idMappingResolutionBehaviour))
				){
					// Object _accordoServizioParteComune_accordoServizioParteComuneGruppo_gruppo (recupero id)
					ISQLQueryObject sqlQueryObjectGet_accordoServizioParteComune_accordoServizioParteComuneGruppo_gruppo_readFkId = sqlQueryObjectGet.newSQLQueryObject();
					sqlQueryObjectGet_accordoServizioParteComune_accordoServizioParteComuneGruppo_gruppo_readFkId.addFromTable(this.getAccordoServizioParteComuneFieldConverter().toTable(org.openspcoop2.core.commons.search.AccordoServizioParteComune.model().ACCORDO_SERVIZIO_PARTE_COMUNE_GRUPPO));
					sqlQueryObjectGet_accordoServizioParteComune_accordoServizioParteComuneGruppo_gruppo_readFkId.addSelectField("id_gruppo");
					sqlQueryObjectGet_accordoServizioParteComune_accordoServizioParteComuneGruppo_gruppo_readFkId.addWhereCondition("id=?");
					sqlQueryObjectGet_accordoServizioParteComune_accordoServizioParteComuneGruppo_gruppo_readFkId.setANDLogicOperator(true);
					Long idFK_accordoServizioParteComune_accordoServizioParteComuneGruppo_gruppo = (Long) jdbcUtilities.executeQuerySingleResult(sqlQueryObjectGet_accordoServizioParteComune_accordoServizioParteComuneGruppo_gruppo_readFkId.createSQLQuery(), jdbcProperties.isShowSql(),Long.class,
							new JDBCObject(accordoServizioParteComune_accordoServizioParteComuneGruppo.getId(),Long.class));
					
					org.openspcoop2.core.commons.search.IdGruppo id_accordoServizioParteComune_accordoServizioParteComuneGruppo_gruppo = null;
					if(idMappingResolutionBehaviour==null || org.openspcoop2.generic_project.beans.IDMappingBehaviour.ENABLED.equals(idMappingResolutionBehaviour)){
						id_accordoServizioParteComune_accordoServizioParteComuneGruppo_gruppo = ((JDBCGruppoServiceSearch)(this.getServiceManager(connection, jdbcProperties, log).getGruppoServiceSearch())).findId(idFK_accordoServizioParteComune_accordoServizioParteComuneGruppo_gruppo, false);
					}else{
						id_accordoServizioParteComune_accordoServizioParteComuneGruppo_gruppo = new org.openspcoop2.core.commons.search.IdGruppo();
					}
					id_accordoServizioParteComune_accordoServizioParteComuneGruppo_gruppo.setId(idFK_accordoServizioParteComune_accordoServizioParteComuneGruppo_gruppo);
					accordoServizioParteComune_accordoServizioParteComuneGruppo.setIdGruppo(id_accordoServizioParteComune_accordoServizioParteComuneGruppo_gruppo);
				}

				/*
				if(idMappingResolutionBehaviour==null ||
					(org.openspcoop2.generic_project.beans.IDMappingBehaviour.ENABLED.equals(idMappingResolutionBehaviour) || org.openspcoop2.generic_project.beans.IDMappingBehaviour.USE_TABLE_ID.equals(idMappingResolutionBehaviour))
				){
					// Object _accordoServizioParteComune_accordoServizioParteComuneGruppo_accordoServizioParteComune (recupero id)
					ISQLQueryObject sqlQueryObjectGet_accordoServizioParteComune_accordoServizioParteComuneGruppo_accordoServizioParteComune_readFkId = sqlQueryObjectGet.newSQLQueryObject();
					sqlQueryObjectGet_accordoServizioParteComune_accordoServizioParteComuneGruppo_accordoServizioParteComune_readFkId.addFromTable(this.getAccordoServizioParteComuneFieldConverter().toTable(org.openspcoop2.core.commons.search.AccordoServizioParteComune.model().ACCORDO_SERVIZIO_PARTE_COMUNE_GRUPPO));
					sqlQueryObjectGet_accordoServizioParteComune_accordoServizioParteComuneGruppo_accordoServizioParteComune_readFkId.addSelectField("id_accordo");
					sqlQueryObjectGet_accordoServizioParteComune_accordoServizioParteComuneGruppo_accordoServizioParteComune_readFkId.addWhereCondition("id=?");
					sqlQueryObjectGet_accordoServizioParteComune_accordoServizioParteComuneGruppo_accordoServizioParteComune_readFkId.setANDLogicOperator(true);
					Long idFK_accordoServizioParteComune_accordoServizioParteComuneGruppo_accordoServizioParteComune = (Long) jdbcUtilities.executeQuerySingleResult(sqlQueryObjectGet_accordoServizioParteComune_accordoServizioParteComuneGruppo_accordoServizioParteComune_readFkId.createSQLQuery(), jdbcProperties.isShowSql(),Long.class,
							new JDBCObject(accordoServizioParteComune_accordoServizioParteComuneGruppo.getId(),Long.class));
					
					org.openspcoop2.core.commons.search.IdAccordoServizioParteComune id_accordoServizioParteComune_accordoServizioParteComuneGruppo_accordoServizioParteComune = null;
					if(idMappingResolutionBehaviour==null || org.openspcoop2.generic_project.beans.IDMappingBehaviour.ENABLED.equals(idMappingResolutionBehaviour)){
						id_accordoServizioParteComune_accordoServizioParteComuneGruppo_accordoServizioParteComune = ((JDBCAccordoServizioParteComuneServiceSearch)(this.getServiceManager(connection, jdbcProperties, log).getAccordoServizioParteComuneServiceSearch())).findId(idFK_accordoServizioParteComune_accordoServizioParteComuneGruppo_accordoServizioParteComune, false);
					}else{
						id_accordoServizioParteComune_accordoServizioParteComuneGruppo_accordoServizioParteComune = new org.openspcoop2.core.commons.search.IdAccordoServizioParteComune();
					}
					id_accordoServizioParteComune_accordoServizioParteComuneGruppo_accordoServizioParteComune.setId(idFK_accordoServizioParteComune_accordoServizioParteComuneGruppo_accordoServizioParteComune);
					accordoServizioParteComune_accordoServizioParteComuneGruppo.setIdAccordoServizioParteComune(id_accordoServizioParteComune_accordoServizioParteComuneGruppo_accordoServizioParteComune);
				}*/
				//  me stesso
				org.openspcoop2.core.commons.search.IdAccordoServizioParteComune id_accordoServizioParteComune_accordoServizioParteComuneGruppo_accordoServizioParteComune = new org.openspcoop2.core.commons.search.IdAccordoServizioParteComune();
				id_accordoServizioParteComune_accordoServizioParteComuneGruppo_accordoServizioParteComune.setId(accordoServizioParteComune.getId());
				id_accordoServizioParteComune_accordoServizioParteComuneGruppo_accordoServizioParteComune.setNome(accordoServizioParteComune.getNome());
				id_accordoServizioParteComune_accordoServizioParteComuneGruppo_accordoServizioParteComune.setIdSoggetto(accordoServizioParteComune.getIdReferente());
				id_accordoServizioParteComune_accordoServizioParteComuneGruppo_accordoServizioParteComune.setVersione(accordoServizioParteComune.getVersione());
				accordoServizioParteComune_accordoServizioParteComuneGruppo.setIdAccordoServizioParteComune(id_accordoServizioParteComune_accordoServizioParteComuneGruppo_accordoServizioParteComune);
					

				accordoServizioParteComune.addAccordoServizioParteComuneGruppo(accordoServizioParteComune_accordoServizioParteComuneGruppo);
			}
		}              
		
        return accordoServizioParteComune;  
	
	} 
	
	@Override
	public boolean exists(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId) throws MultipleResultException, NotImplementedException, ServiceException, Exception {
		return this._exists(jdbcProperties, log, connection, sqlQueryObject, Long.valueOf(tableId));
	}
	
	private boolean _exists(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, Long tableId) throws MultipleResultException, NotImplementedException, ServiceException, Exception {
	
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);
				
		boolean existsAccordoServizioParteComune = false;

		sqlQueryObject = sqlQueryObject.newSQLQueryObject();
		sqlQueryObject.setANDLogicOperator(true);

		sqlQueryObject.addFromTable(this.getAccordoServizioParteComuneFieldConverter().toTable(AccordoServizioParteComune.model()));
		sqlQueryObject.addSelectField(this.getAccordoServizioParteComuneFieldConverter().toColumn(AccordoServizioParteComune.model().NOME,true));
		sqlQueryObject.addWhereCondition("id=?");


		// Exists accordoServizioParteComune
		existsAccordoServizioParteComune = jdbcUtilities.exists(sqlQueryObject.createSQLQuery(), jdbcProperties.isShowSql(),
			new JDBCObject(tableId,Long.class));

		
        return existsAccordoServizioParteComune;
	
	}
	
	private void _join(IExpression expression, ISQLQueryObject sqlQueryObject) throws NotImplementedException, ServiceException, Exception{
	
		boolean addReferente = false;
		if(expression.inUseModel(AccordoServizioParteComune.model().ID_REFERENTE,false)){
			String tableName1 = this.getAccordoServizioParteComuneFieldConverter().toTable(AccordoServizioParteComune.model());
			String tableName2 = this.getAccordoServizioParteComuneFieldConverter().toTable(AccordoServizioParteComune.model().ID_REFERENTE);
			sqlQueryObject.addWhereCondition(tableName1+".id_referente="+tableName2+".id");
			addReferente = true;
		}
		if(expression.inUseModel(AccordoServizioParteComune.model().PORT_TYPE.OPERATION.ID_PORT_TYPE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO,false) &&
				!addReferente){
			String tableName1 = this.getAccordoServizioParteComuneFieldConverter().toTable(AccordoServizioParteComune.model());
			String tableName2 = this.getAccordoServizioParteComuneFieldConverter().toTable(AccordoServizioParteComune.model().PORT_TYPE.OPERATION.ID_PORT_TYPE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO);
			sqlQueryObject.addWhereCondition(tableName1+".id_referente="+tableName2+".id");
			addReferente = true;
		}
		if(expression.inUseModel(AccordoServizioParteComune.model().PORT_TYPE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO,false) &&
				!addReferente){
			String tableName1 = this.getAccordoServizioParteComuneFieldConverter().toTable(AccordoServizioParteComune.model());
			String tableName2 = this.getAccordoServizioParteComuneFieldConverter().toTable(AccordoServizioParteComune.model().PORT_TYPE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO);
			sqlQueryObject.addWhereCondition(tableName1+".id_referente="+tableName2+".id");
			addReferente = true;
		}
		if(expression.inUseModel(AccordoServizioParteComune.model().ACCORDO_SERVIZIO_PARTE_COMUNE_AZIONE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO,false) &&
				!addReferente){
			String tableName1 = this.getAccordoServizioParteComuneFieldConverter().toTable(AccordoServizioParteComune.model());
			String tableName2 = this.getAccordoServizioParteComuneFieldConverter().toTable(AccordoServizioParteComune.model().ACCORDO_SERVIZIO_PARTE_COMUNE_AZIONE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO);
			sqlQueryObject.addWhereCondition(tableName1+".id_referente="+tableName2+".id");
			addReferente = true;
		}
		if(expression.inUseModel(AccordoServizioParteComune.model().RESOURCE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO,false) &&
				!addReferente){
			String tableName1 = this.getAccordoServizioParteComuneFieldConverter().toTable(AccordoServizioParteComune.model());
			String tableName2 = this.getAccordoServizioParteComuneFieldConverter().toTable(AccordoServizioParteComune.model().RESOURCE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO);
			sqlQueryObject.addWhereCondition(tableName1+".id_referente="+tableName2+".id");
			addReferente = true;
		}
		if(expression.inUseModel(AccordoServizioParteComune.model().ACCORDO_SERVIZIO_PARTE_COMUNE_GRUPPO.ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO,false) &&
				!addReferente){
			String tableName1 = this.getAccordoServizioParteComuneFieldConverter().toTable(AccordoServizioParteComune.model());
			String tableName2 = this.getAccordoServizioParteComuneFieldConverter().toTable(AccordoServizioParteComune.model().ACCORDO_SERVIZIO_PARTE_COMUNE_GRUPPO.ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO);
			sqlQueryObject.addWhereCondition(tableName1+".id_referente="+tableName2+".id");
			addReferente = true;
		}
		
		
		if(expression.inUseModel(AccordoServizioParteComune.model().ACCORDO_SERVIZIO_PARTE_COMUNE_AZIONE,false)){
			String tableName1 = this.getAccordoServizioParteComuneFieldConverter().toTable(AccordoServizioParteComune.model().ACCORDO_SERVIZIO_PARTE_COMUNE_AZIONE);
			String tableName2 = this.getAccordoServizioParteComuneFieldConverter().toTable(AccordoServizioParteComune.model());
			sqlQueryObject.addWhereCondition(tableName1+".id_accordo="+tableName2+".id");
		}
		
		if(expression.inUseModel(AccordoServizioParteComune.model().PORT_TYPE,false)){
			String tableName1 = this.getAccordoServizioParteComuneFieldConverter().toTable(AccordoServizioParteComune.model().PORT_TYPE);
			String tableName2 = this.getAccordoServizioParteComuneFieldConverter().toTable(AccordoServizioParteComune.model());
			sqlQueryObject.addWhereCondition(tableName1+".id_accordo="+tableName2+".id");
		}
		if(expression.inUseModel(AccordoServizioParteComune.model().PORT_TYPE.OPERATION.ID_PORT_TYPE,false) &&
				!expression.inUseModel(AccordoServizioParteComune.model().PORT_TYPE,false)){
			String tableName1 = this.getAccordoServizioParteComuneFieldConverter().toTable(AccordoServizioParteComune.model().PORT_TYPE.OPERATION.ID_PORT_TYPE);
			String tableName2 = this.getAccordoServizioParteComuneFieldConverter().toTable(AccordoServizioParteComune.model());
			sqlQueryObject.addWhereCondition(tableName1+".id_accordo="+tableName2+".id");
		}
		
		if(expression.inUseModel(AccordoServizioParteComune.model().PORT_TYPE.OPERATION,false)){
			String tableName1 = this.getAccordoServizioParteComuneFieldConverter().toTable(AccordoServizioParteComune.model().PORT_TYPE.OPERATION);
			String tableName2 = this.getAccordoServizioParteComuneFieldConverter().toTable(AccordoServizioParteComune.model().PORT_TYPE);
			sqlQueryObject.addWhereCondition(tableName1+".id_port_type="+tableName2+".id");
		}
		
		if(expression.inUseModel(AccordoServizioParteComune.model().RESOURCE,false)){
			String tableName1 = this.getAccordoServizioParteComuneFieldConverter().toTable(AccordoServizioParteComune.model().RESOURCE);
			String tableName2 = this.getAccordoServizioParteComuneFieldConverter().toTable(AccordoServizioParteComune.model());
			sqlQueryObject.addWhereCondition(tableName1+".id_accordo="+tableName2+".id");
		}
		
		boolean addJoinAccordiToAccordiGruppi = false;
		if(expression.inUseModel(AccordoServizioParteComune.model().ACCORDO_SERVIZIO_PARTE_COMUNE_GRUPPO,false)){
			String tableName1 = this.getAccordoServizioParteComuneFieldConverter().toTable(AccordoServizioParteComune.model().ACCORDO_SERVIZIO_PARTE_COMUNE_GRUPPO);
			String tableName2 = this.getAccordoServizioParteComuneFieldConverter().toTable(AccordoServizioParteComune.model());
			sqlQueryObject.addWhereCondition(tableName1+".id_accordo="+tableName2+".id");
			addJoinAccordiToAccordiGruppi = true;
		}
		if(expression.inUseModel(AccordoServizioParteComune.model().ACCORDO_SERVIZIO_PARTE_COMUNE_GRUPPO.ID_GRUPPO,false)){
			String tableName1 = this.getAccordoServizioParteComuneFieldConverter().toTable(AccordoServizioParteComune.model().ACCORDO_SERVIZIO_PARTE_COMUNE_GRUPPO.ID_GRUPPO);
			String tableName2 = this.getAccordoServizioParteComuneFieldConverter().toTable(AccordoServizioParteComune.model().ACCORDO_SERVIZIO_PARTE_COMUNE_GRUPPO);
			sqlQueryObject.addWhereCondition(tableName1+".id="+tableName2+".id_gruppo");
			if(!addJoinAccordiToAccordiGruppi) {
				tableName1 = this.getAccordoServizioParteComuneFieldConverter().toTable(AccordoServizioParteComune.model().ACCORDO_SERVIZIO_PARTE_COMUNE_GRUPPO);
				tableName2 = this.getAccordoServizioParteComuneFieldConverter().toTable(AccordoServizioParteComune.model());
				sqlQueryObject.addWhereCondition(tableName1+".id_accordo="+tableName2+".id");
			}
		}

		
		// Check FROM Table necessarie per le join di oggetti annidati dal secondo livello in poi dove pero' non viene poi utilizzato l'oggetto del livello precedente nella espressione
		if(expression.inUseModel(AccordoServizioParteComune.model().PORT_TYPE.OPERATION,false)){
			if(expression.inUseModel(AccordoServizioParteComune.model().PORT_TYPE,false)==false){
				sqlQueryObject.addFromTable(this.getAccordoServizioParteComuneFieldConverter().toTable(AccordoServizioParteComune.model().PORT_TYPE));
			}
		}
		
		if(expression.inUseModel(AccordoServizioParteComune.model().ACCORDO_SERVIZIO_PARTE_COMUNE_GRUPPO.ID_GRUPPO,false)){
			if(expression.inUseModel(AccordoServizioParteComune.model().ACCORDO_SERVIZIO_PARTE_COMUNE_GRUPPO,false)==false){
				sqlQueryObject.addFromTable(this.getAccordoServizioParteComuneFieldConverter().toTable(AccordoServizioParteComune.model().ACCORDO_SERVIZIO_PARTE_COMUNE_GRUPPO));
			}
		}
        
	}
	
	protected java.util.List<Object> _getRootTablePrimaryKeyValues(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdAccordoServizioParteComune id) throws NotFoundException, ServiceException, NotImplementedException, Exception{
	    // Identificativi
        java.util.List<Object> rootTableIdValues = new java.util.ArrayList<Object>();
        Long longId = this.findIdAccordoServizioParteComune(jdbcProperties, log, connection, sqlQueryObject.newSQLQueryObject(), id, true);
		rootTableIdValues.add(longId);
        return rootTableIdValues;
	}
	
	protected Map<String, List<IField>> _getMapTableToPKColumn() throws NotImplementedException, Exception{
	
		AccordoServizioParteComuneFieldConverter converter = this.getAccordoServizioParteComuneFieldConverter();
		Map<String, List<IField>> mapTableToPKColumn = new java.util.HashMap<String, List<IField>>();
		UtilsTemplate<IField> utilities = new UtilsTemplate<IField>();

		// AccordoServizioParteComune.model()
		mapTableToPKColumn.put(converter.toTable(AccordoServizioParteComune.model()),
			utilities.newList(
				new CustomField("id", Long.class, "id", converter.toTable(AccordoServizioParteComune.model()))
			));

		// AccordoServizioParteComune.model().ID_REFERENTE
		mapTableToPKColumn.put(converter.toTable(AccordoServizioParteComune.model().ID_REFERENTE),
			utilities.newList(
				new CustomField("id", Long.class, "id", converter.toTable(AccordoServizioParteComune.model().ID_REFERENTE))
			));

		// AccordoServizioParteComune.model().ACCORDO_SERVIZIO_PARTE_COMUNE_AZIONE
		mapTableToPKColumn.put(converter.toTable(AccordoServizioParteComune.model().ACCORDO_SERVIZIO_PARTE_COMUNE_AZIONE),
			utilities.newList(
				new CustomField("id", Long.class, "id", converter.toTable(AccordoServizioParteComune.model().ACCORDO_SERVIZIO_PARTE_COMUNE_AZIONE))
			));

		// AccordoServizioParteComune.model().ACCORDO_SERVIZIO_PARTE_COMUNE_AZIONE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE
		mapTableToPKColumn.put(converter.toTable(AccordoServizioParteComune.model().ACCORDO_SERVIZIO_PARTE_COMUNE_AZIONE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE),
			utilities.newList(
				new CustomField("id", Long.class, "id", converter.toTable(AccordoServizioParteComune.model().ACCORDO_SERVIZIO_PARTE_COMUNE_AZIONE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE))
			));

		// AccordoServizioParteComune.model().ACCORDO_SERVIZIO_PARTE_COMUNE_AZIONE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO
		mapTableToPKColumn.put(converter.toTable(AccordoServizioParteComune.model().ACCORDO_SERVIZIO_PARTE_COMUNE_AZIONE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO),
			utilities.newList(
				new CustomField("id", Long.class, "id", converter.toTable(AccordoServizioParteComune.model().ACCORDO_SERVIZIO_PARTE_COMUNE_AZIONE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO))
			));

		// AccordoServizioParteComune.model().PORT_TYPE
		mapTableToPKColumn.put(converter.toTable(AccordoServizioParteComune.model().PORT_TYPE),
			utilities.newList(
				new CustomField("id", Long.class, "id", converter.toTable(AccordoServizioParteComune.model().PORT_TYPE))
			));

		// AccordoServizioParteComune.model().PORT_TYPE.OPERATION
		mapTableToPKColumn.put(converter.toTable(AccordoServizioParteComune.model().PORT_TYPE.OPERATION),
			utilities.newList(
				new CustomField("id", Long.class, "id", converter.toTable(AccordoServizioParteComune.model().PORT_TYPE.OPERATION))
			));

		// AccordoServizioParteComune.model().PORT_TYPE.OPERATION.ID_PORT_TYPE
		mapTableToPKColumn.put(converter.toTable(AccordoServizioParteComune.model().PORT_TYPE.OPERATION.ID_PORT_TYPE),
			utilities.newList(
				new CustomField("id", Long.class, "id", converter.toTable(AccordoServizioParteComune.model().PORT_TYPE.OPERATION.ID_PORT_TYPE))
			));

		// AccordoServizioParteComune.model().PORT_TYPE.OPERATION.ID_PORT_TYPE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE
		mapTableToPKColumn.put(converter.toTable(AccordoServizioParteComune.model().PORT_TYPE.OPERATION.ID_PORT_TYPE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE),
			utilities.newList(
				new CustomField("id", Long.class, "id", converter.toTable(AccordoServizioParteComune.model().PORT_TYPE.OPERATION.ID_PORT_TYPE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE))
			));

		// AccordoServizioParteComune.model().PORT_TYPE.OPERATION.ID_PORT_TYPE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO
		mapTableToPKColumn.put(converter.toTable(AccordoServizioParteComune.model().PORT_TYPE.OPERATION.ID_PORT_TYPE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO),
			utilities.newList(
				new CustomField("id", Long.class, "id", converter.toTable(AccordoServizioParteComune.model().PORT_TYPE.OPERATION.ID_PORT_TYPE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO))
			));

		// AccordoServizioParteComune.model().PORT_TYPE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE
		mapTableToPKColumn.put(converter.toTable(AccordoServizioParteComune.model().PORT_TYPE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE),
			utilities.newList(
				new CustomField("id", Long.class, "id", converter.toTable(AccordoServizioParteComune.model().PORT_TYPE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE))
			));

		// AccordoServizioParteComune.model().PORT_TYPE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO
		mapTableToPKColumn.put(converter.toTable(AccordoServizioParteComune.model().PORT_TYPE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO),
			utilities.newList(
				new CustomField("id", Long.class, "id", converter.toTable(AccordoServizioParteComune.model().PORT_TYPE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO))
			));

		// AccordoServizioParteComune.model().RESOURCE
		mapTableToPKColumn.put(converter.toTable(AccordoServizioParteComune.model().RESOURCE),
			utilities.newList(
				new CustomField("id", Long.class, "id", converter.toTable(AccordoServizioParteComune.model().RESOURCE))
			));

		// AccordoServizioParteComune.model().RESOURCE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE
		mapTableToPKColumn.put(converter.toTable(AccordoServizioParteComune.model().RESOURCE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE),
			utilities.newList(
				new CustomField("id", Long.class, "id", converter.toTable(AccordoServizioParteComune.model().RESOURCE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE))
			));

		// AccordoServizioParteComune.model().RESOURCE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO
		mapTableToPKColumn.put(converter.toTable(AccordoServizioParteComune.model().RESOURCE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO),
			utilities.newList(
				new CustomField("id", Long.class, "id", converter.toTable(AccordoServizioParteComune.model().RESOURCE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO))
			));

		// AccordoServizioParteComune.model().ACCORDO_SERVIZIO_PARTE_COMUNE_GRUPPO
		mapTableToPKColumn.put(converter.toTable(AccordoServizioParteComune.model().ACCORDO_SERVIZIO_PARTE_COMUNE_GRUPPO),
			utilities.newList(
				new CustomField("id", Long.class, "id", converter.toTable(AccordoServizioParteComune.model().ACCORDO_SERVIZIO_PARTE_COMUNE_GRUPPO))
			));

		// AccordoServizioParteComune.model().ACCORDO_SERVIZIO_PARTE_COMUNE_GRUPPO.ID_GRUPPO
		mapTableToPKColumn.put(converter.toTable(AccordoServizioParteComune.model().ACCORDO_SERVIZIO_PARTE_COMUNE_GRUPPO.ID_GRUPPO),
			utilities.newList(
				new CustomField("id", Long.class, "id", converter.toTable(AccordoServizioParteComune.model().ACCORDO_SERVIZIO_PARTE_COMUNE_GRUPPO.ID_GRUPPO))
			));

        
        return mapTableToPKColumn;		
	}
	
	@Override
	public List<Long> findAllTableIds(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCPaginatedExpression paginatedExpression) throws ServiceException, NotImplementedException, Exception {
		
		List<Long> list = new ArrayList<Long>();

		sqlQueryObject.setSelectDistinct(true);
		sqlQueryObject.setANDLogicOperator(true);
		sqlQueryObject.addSelectField(this.getAccordoServizioParteComuneFieldConverter().toTable(AccordoServizioParteComune.model())+".id");
		Class<?> objectIdClass = Long.class;
		
		List<Object> listaQuery = org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.prepareFindAll(jdbcProperties, log, connection, sqlQueryObject, paginatedExpression,
												this.getAccordoServizioParteComuneFieldConverter(), AccordoServizioParteComune.model());
		
		_join(paginatedExpression,sqlQueryObject);
		
		List<Object> listObjects = org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.findAll(jdbcProperties, log, connection, sqlQueryObject, paginatedExpression,
																			this.getAccordoServizioParteComuneFieldConverter(), AccordoServizioParteComune.model(), objectIdClass, listaQuery);
		for(Object object: listObjects) {
			list.add((Long)object);
		}

        return list;
		
	}
	
	@Override
	public long findTableId(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCExpression expression) throws ServiceException, NotFoundException, MultipleResultException, NotImplementedException, Exception {
	
		sqlQueryObject.setSelectDistinct(true);
		sqlQueryObject.setANDLogicOperator(true);
		sqlQueryObject.addSelectField(this.getAccordoServizioParteComuneFieldConverter().toTable(AccordoServizioParteComune.model())+".id");
		Class<?> objectIdClass = Long.class;
		
		List<Object> listaQuery = org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.prepareFind(jdbcProperties, log, connection, sqlQueryObject, expression,
												this.getAccordoServizioParteComuneFieldConverter(), AccordoServizioParteComune.model());
		
		_join(expression,sqlQueryObject);

		Object res = org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.find(jdbcProperties, log, connection, sqlQueryObject, expression,
														this.getAccordoServizioParteComuneFieldConverter(), AccordoServizioParteComune.model(), objectIdClass, listaQuery);
		if(res!=null && (((Long) res).longValue()>0) ){
			return ((Long) res).longValue();
		}
		else{
			throw new NotFoundException("Not Found");
		}
		
	}

	@Override
	public InUse inUse(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId) throws ServiceException, NotFoundException, NotImplementedException, Exception {
		return this._inUse(jdbcProperties, log, connection, sqlQueryObject, Long.valueOf(tableId));
	}

	private InUse _inUse(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, Long tableId) throws ServiceException, NotFoundException, NotImplementedException, Exception {

		InUse inUse = new InUse();
		inUse.setInUse(false);
		
		/* 
		 * TODO: implement code that checks whether the object identified by the id parameter is used by other objects
		*/
		
		// Delete this line when you have implemented the method
		int throwNotImplemented = 1;
		if(throwNotImplemented==1){
		        throw new NotImplementedException("NotImplemented");
		}
		// Delete this line when you have implemented the method

        return inUse;

	}
	
	@Override
	public IdAccordoServizioParteComune findId(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, boolean throwNotFound)
			throws NotFoundException, ServiceException, NotImplementedException, Exception {
		
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);

		ISQLQueryObject sqlQueryObjectGet = sqlQueryObject.newSQLQueryObject();

		// Object _accordoServizioParteComune
		sqlQueryObjectGet.addFromTable(this.getAccordoServizioParteComuneFieldConverter().toTable(AccordoServizioParteComune.model()));
		sqlQueryObjectGet.addSelectField(this.getAccordoServizioParteComuneFieldConverter().toColumn(AccordoServizioParteComune.model().NOME,true));
		sqlQueryObjectGet.addSelectField(this.getAccordoServizioParteComuneFieldConverter().toColumn(AccordoServizioParteComune.model().VERSIONE,true));
		sqlQueryObjectGet.addSelectField("id_referente");
		sqlQueryObjectGet.addSelectField(this.getAccordoServizioParteComuneFieldConverter().toColumn(AccordoServizioParteComune.model().SERVICE_BINDING,true));
		sqlQueryObjectGet.setANDLogicOperator(true);
		sqlQueryObjectGet.addWhereCondition("id=?");

		// Recupero _accordoServizioParteComune
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject [] searchParams_accordoServizioParteComune = new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject [] { 
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(tableId,Long.class)
		};
		List<Class<?>> listaFieldIdReturnType_accordoServizioParteComune = new ArrayList<Class<?>>();
		listaFieldIdReturnType_accordoServizioParteComune.add(String.class);
		listaFieldIdReturnType_accordoServizioParteComune.add(Integer.class);
		listaFieldIdReturnType_accordoServizioParteComune.add(Long.class);
		listaFieldIdReturnType_accordoServizioParteComune.add(String.class);
		org.openspcoop2.core.commons.search.IdAccordoServizioParteComune id_accordoServizioParteComune = null;
		List<Object> listaFieldId_accordoServizioParteComune = jdbcUtilities.executeQuerySingleResult(sqlQueryObjectGet.createSQLQuery(), jdbcProperties.isShowSql(),
				listaFieldIdReturnType_accordoServizioParteComune, searchParams_accordoServizioParteComune);
		if(listaFieldId_accordoServizioParteComune==null || listaFieldId_accordoServizioParteComune.size()<=0){
			if(throwNotFound){
				throw new NotFoundException("Not Found");
			}
		}
		else{
			// set _accordoServizioParteComune
			id_accordoServizioParteComune = new org.openspcoop2.core.commons.search.IdAccordoServizioParteComune();
			id_accordoServizioParteComune.setNome((String)listaFieldId_accordoServizioParteComune.get(0));
			id_accordoServizioParteComune.setVersione((Integer)listaFieldId_accordoServizioParteComune.get(1));
			Long idSoggettoFK = (Long) listaFieldId_accordoServizioParteComune.get(2);
			id_accordoServizioParteComune.setServiceBinding((String)listaFieldId_accordoServizioParteComune.get(3));
			id_accordoServizioParteComune.
				setIdSoggetto(((IDBSoggettoServiceSearch)this.getServiceManager(connection, jdbcProperties, log).
						getSoggettoServiceSearch()).findId(idSoggettoFK, true));
		}
		
		return id_accordoServizioParteComune;
		
	}

	@Override
	public Long findTableId(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdAccordoServizioParteComune id, boolean throwNotFound)
			throws NotFoundException, ServiceException, NotImplementedException, Exception {
	
		return this.findIdAccordoServizioParteComune(jdbcProperties,log,connection,sqlQueryObject,id,throwNotFound);
			
	}
	
	@Override
	public List<List<Object>> nativeQuery(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, 
											String sql,List<Class<?>> returnClassTypes,Object ... param) throws ServiceException,NotFoundException,NotImplementedException,Exception{
		
		return org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.nativeQuery(jdbcProperties, log, connection, sqlQueryObject,
																							sql,returnClassTypes,param);
														
	}
	
	protected Long findIdAccordoServizioParteComune(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdAccordoServizioParteComune id, boolean throwNotFound) throws NotFoundException, ServiceException, NotImplementedException, Exception {

		if(jdbcProperties==null) {
			throw new ServiceException("Param jdbcProperties is null");
		}
		if(sqlQueryObject==null) {
			throw new ServiceException("Param sqlQueryObject is null");
		}
		if(id==null) {
			throw new ServiceException("Param id is null");
		}
		
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);

		ISQLQueryObject sqlQueryObjectGet = sqlQueryObject.newSQLQueryObject();

		// found idSoggetto
//		if(id.getIdSoggetto()==null){
//			throw new ServiceException("Id soggetto non fornito");
//		}
		Soggetto soggetto = null;
		
		// Object _accordoServizioParteComune
		sqlQueryObjectGet.addFromTable(this.getAccordoServizioParteComuneFieldConverter().toTable(AccordoServizioParteComune.model()));
		sqlQueryObjectGet.addSelectField("id");
		sqlQueryObjectGet.setANDLogicOperator(true);
		sqlQueryObjectGet.setSelectDistinct(true);
		sqlQueryObjectGet.addWhereCondition(this.getAccordoServizioParteComuneFieldConverter().toColumn(AccordoServizioParteComune.model().NOME,true)+"=?");
		if(id.getVersione()!=null)
			sqlQueryObjectGet.addWhereCondition(this.getAccordoServizioParteComuneFieldConverter().toColumn(AccordoServizioParteComune.model().VERSIONE,true)+"=?");
		else
			sqlQueryObjectGet.addWhereCondition(this.getAccordoServizioParteComuneFieldConverter().toColumn(AccordoServizioParteComune.model().VERSIONE,true)+" is null");
		if(id.getIdSoggetto()==null){
			sqlQueryObjectGet.addWhereCondition("( (id_referente is null) OR (id_referente<=0) )");
		}else{
			sqlQueryObjectGet.addWhereCondition("id_referente=?");
			soggetto = this.getServiceManager(connection, jdbcProperties, log).getSoggettoServiceSearch().get(id.getIdSoggetto());
		}
		

		// Recupero _accordoServizioParteComune
		List<org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject> listJDBCObject = new ArrayList<org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject>();
		listJDBCObject.add(new JDBCObject(id.getNome(), String.class));
		if(id.getVersione()!=null){
			listJDBCObject.add(new JDBCObject(id.getVersione(), Integer.class));
		}
		if(soggetto!=null){
			listJDBCObject.add(new JDBCObject(soggetto.getId(), Long.class));
		}
		Long id_accordoServizioParteComune = null;
		try{
			id_accordoServizioParteComune = (Long) jdbcUtilities.executeQuerySingleResult(sqlQueryObjectGet.createSQLQuery(), jdbcProperties.isShowSql(),
						Long.class, listJDBCObject.toArray(new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject [1]));
		}catch(NotFoundException notFound){
			if(throwNotFound){
				throw new NotFoundException(notFound);
			}
		}
		if(id_accordoServizioParteComune==null || id_accordoServizioParteComune<=0){
			if(throwNotFound){
				throw new NotFoundException("Not Found");
			}
		}
		
		return id_accordoServizioParteComune;
	}
}
