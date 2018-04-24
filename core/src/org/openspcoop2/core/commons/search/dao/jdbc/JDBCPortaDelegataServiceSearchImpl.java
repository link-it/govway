/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it).
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
import org.openspcoop2.core.commons.search.IdPortaDelegata;
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
import org.openspcoop2.core.commons.search.dao.jdbc.converter.PortaDelegataFieldConverter;
import org.openspcoop2.core.commons.search.dao.jdbc.fetch.PortaDelegataFetch;
import org.openspcoop2.core.commons.search.dao.jdbc.JDBCServiceManager;

import org.openspcoop2.core.commons.search.PortaDelegata;
import org.openspcoop2.core.commons.search.PortaDelegataServizioApplicativo;

/**     
 * JDBCPortaDelegataServiceSearchImpl
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JDBCPortaDelegataServiceSearchImpl implements IJDBCServiceSearchWithId<PortaDelegata, IdPortaDelegata, JDBCServiceManager> {

	private PortaDelegataFieldConverter _portaDelegataFieldConverter = null;
	public PortaDelegataFieldConverter getPortaDelegataFieldConverter() {
		if(this._portaDelegataFieldConverter==null){
			this._portaDelegataFieldConverter = new PortaDelegataFieldConverter(this.jdbcServiceManager.getJdbcProperties().getDatabaseType());
		}		
		return this._portaDelegataFieldConverter;
	}
	@Override
	public ISQLFieldConverter getFieldConverter() {
		return this.getPortaDelegataFieldConverter();
	}
	
	private PortaDelegataFetch portaDelegataFetch = new PortaDelegataFetch();
	public PortaDelegataFetch getPortaDelegataFetch() {
		return this.portaDelegataFetch;
	}
	@Override
	public IJDBCFetch getFetch() {
		return getPortaDelegataFetch();
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
	

	@Override
	public IdPortaDelegata convertToId(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, PortaDelegata portaDelegata) throws NotImplementedException, ServiceException, Exception{
	
		IdPortaDelegata idPortaDelegata = new IdPortaDelegata();
		// idPortaDelegata.setXXX(portaDelegata.getYYY());
		// ...
		// idPortaDelegata.setXXX(portaDelegata.getYYY());
		// TODO: popola IdPortaDelegata
	
		/* 
	     * TODO: implement code that returns the object id
	    */
	
	    // Delete this line when you have implemented the method
	    int throwNotImplemented = 1;
	    if(throwNotImplemented==1){
	            throw new NotImplementedException("NotImplemented");
	    }
	    // Delete this line when you have implemented the method 
	
		return idPortaDelegata;
	}
	
	@Override
	public PortaDelegata get(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdPortaDelegata id, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException, MultipleResultException, NotImplementedException, ServiceException,Exception {
		Long id_portaDelegata = ( (id!=null && id.getId()!=null && id.getId()>0) ? id.getId() : this.findIdPortaDelegata(jdbcProperties, log, connection, sqlQueryObject, id, true));
		return this._get(jdbcProperties, log, connection, sqlQueryObject, id_portaDelegata,idMappingResolutionBehaviour);
		
		
	}
	
	@Override
	public boolean exists(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdPortaDelegata id) throws MultipleResultException, NotImplementedException, ServiceException,Exception {

		Long id_portaDelegata = this.findIdPortaDelegata(jdbcProperties, log, connection, sqlQueryObject, id, false);
		return id_portaDelegata != null && id_portaDelegata > 0;
		
	}
	
	@Override
	public List<IdPortaDelegata> findAllIds(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCPaginatedExpression expression, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotImplementedException, ServiceException,Exception {

		List<IdPortaDelegata> list = new ArrayList<IdPortaDelegata>();

		// TODO: implementazione non efficente. 
		// Per ottenere una implementazione efficente:
		// 1. Usare metodo select di questa classe indirizzando esattamente i field necessari a create l'ID logico
		// 2. Usare metodo getPortaDelegataFetch() sul risultato della select per ottenere un oggetto PortaDelegata
		//	  La fetch con la map inserirà nell'oggetto solo i valori estratti 
		// 3. Usare metodo convertToId per ottenere l'id

        List<Long> ids = this.findAllTableIds(jdbcProperties, log, connection, sqlQueryObject, expression);
        
        for(Long id: ids) {
        	PortaDelegata portaDelegata = this.get(jdbcProperties, log, connection, sqlQueryObject, id, idMappingResolutionBehaviour);
			IdPortaDelegata idPortaDelegata = this.convertToId(jdbcProperties,log,connection,sqlQueryObject,portaDelegata);
        	list.add(idPortaDelegata);
        }

        return list;
		
	}
	
	@Override
	public List<PortaDelegata> findAll(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCPaginatedExpression expression, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotImplementedException, ServiceException,Exception {

        List<PortaDelegata> list = new ArrayList<PortaDelegata>();
        
        // TODO: implementazione non efficente. 
		// Per ottenere una implementazione efficente:
		// 1. Usare metodo select di questa classe indirizzando esattamente i field necessari
		// 2. Usare metodo getPortaDelegataFetch() sul risultato della select per ottenere un oggetto PortaDelegata
		//	  La fetch con la map inserirà nell'oggetto solo i valori estratti 

        List<Long> ids = this.findAllTableIds(jdbcProperties, log, connection, sqlQueryObject, expression);
        
        for(Long id: ids) {
        	list.add(this.get(jdbcProperties, log, connection, sqlQueryObject, id, idMappingResolutionBehaviour));
        }

        return list;      
		
	}
	
	@Override
	public PortaDelegata find(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCExpression expression, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) 
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
		
		List<Object> listaQuery = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.prepareCount(jdbcProperties, log, connection, sqlQueryObject, expression,
												this.getPortaDelegataFieldConverter(), PortaDelegata.model());
		
		sqlQueryObject.addSelectCountField(this.getPortaDelegataFieldConverter().toTable(PortaDelegata.model())+".id","tot",true);
		
		_join(expression,sqlQueryObject);
		
		return org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.count(jdbcProperties, log, connection, sqlQueryObject, expression,
																			this.getPortaDelegataFieldConverter(), PortaDelegata.model(),listaQuery);
	}

	@Override
	public InUse inUse(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdPortaDelegata id) throws NotFoundException, NotImplementedException, ServiceException,Exception {
		
		Long id_portaDelegata = this.findIdPortaDelegata(jdbcProperties, log, connection, sqlQueryObject, id, true);
        return this._inUse(jdbcProperties, log, connection, sqlQueryObject, id_portaDelegata);
		
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
		return org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.selectSingleObject(map);
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
		
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.setFields(sqlQueryObject,paginatedExpression,field);
		try{
		
			ISQLQueryObject sqlQueryObjectDistinct = 
						org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.prepareSqlQueryObjectForSelectDistinct(distinct,sqlQueryObject, paginatedExpression, log,
												this.getPortaDelegataFieldConverter(), field);

			return _select(jdbcProperties, log, connection, sqlQueryObject, paginatedExpression, sqlQueryObjectDistinct);
			
		}finally{
			org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.removeFields(sqlQueryObject,paginatedExpression,field);
		}
	}

	@Override
	public Object aggregate(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, 
													JDBCExpression expression, FunctionField functionField) throws ServiceException,NotFoundException,NotImplementedException,Exception {
		Map<String,Object> map = 
			this.aggregate(jdbcProperties, log, connection, sqlQueryObject, expression, new FunctionField[]{functionField});
		return org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.selectAggregateObject(map,functionField);
	}
	
	@Override
	public Map<String,Object> aggregate(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, 
													JDBCExpression expression, FunctionField ... functionField) throws ServiceException,NotFoundException,NotImplementedException,Exception {													
		
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.setFields(sqlQueryObject,expression,functionField);
		try{
			List<Map<String,Object>> list = _select(jdbcProperties, log, connection, sqlQueryObject, expression);
			return list.get(0);
		}finally{
			org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.removeFields(sqlQueryObject,expression,functionField);
		}
	}

	@Override
	public List<Map<String,Object>> groupBy(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, 
													JDBCExpression expression, FunctionField ... functionField) throws ServiceException,NotFoundException,NotImplementedException,Exception {
		
		if(expression.getGroupByFields().size()<=0){
			throw new ServiceException("GroupBy conditions not found in expression");
		}
		
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.setFields(sqlQueryObject,expression,functionField);
		try{
			return _select(jdbcProperties, log, connection, sqlQueryObject, expression);
		}finally{
			org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.removeFields(sqlQueryObject,expression,functionField);
		}
	}
	

	@Override
	public List<Map<String,Object>> groupBy(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, 
													JDBCPaginatedExpression paginatedExpression, FunctionField ... functionField) throws ServiceException,NotFoundException,NotImplementedException,Exception {
		
		if(paginatedExpression.getGroupByFields().size()<=0){
			throw new ServiceException("GroupBy conditions not found in expression");
		}
		
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.setFields(sqlQueryObject,paginatedExpression,functionField);
		try{
			return _select(jdbcProperties, log, connection, sqlQueryObject, paginatedExpression);
		}finally{
			org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.removeFields(sqlQueryObject,paginatedExpression,functionField);
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
		List<Object> returnField = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.prepareSelect(jdbcProperties, log, connection, sqlQueryObject, 
        						expression, this.getPortaDelegataFieldConverter(), PortaDelegata.model(), 
        						listaQuery,listaParams);
		
		_join(expression,sqlQueryObject);
        
        List<Map<String,Object>> list = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.select(jdbcProperties, log, connection,
        								org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.prepareSqlQueryObjectForSelectDistinct(sqlQueryObject,sqlQueryObjectDistinct), 
        								expression, this.getPortaDelegataFieldConverter(), PortaDelegata.model(),
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
		List<Class<?>> returnClassTypes = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.prepareUnion(jdbcProperties, log, connection, sqlQueryObject, 
        						this.getPortaDelegataFieldConverter(), PortaDelegata.model(), 
        						sqlQueryObjectInnerList, jdbcObjects, union, unionExpression);
		
		if(unionExpression!=null){
			for (int i = 0; i < unionExpression.length; i++) {
				UnionExpression ue = unionExpression[i];
				IExpression expression = ue.getExpression();
				_join(expression,sqlQueryObjectInnerList.get(i));
			}
		}
        
        List<Map<String,Object>> list = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.union(jdbcProperties, log, connection, sqlQueryObject, 
        								this.getPortaDelegataFieldConverter(), PortaDelegata.model(), 
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
		List<Class<?>> returnClassTypes = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.prepareUnionCount(jdbcProperties, log, connection, sqlQueryObject, 
        						this.getPortaDelegataFieldConverter(), PortaDelegata.model(), 
        						sqlQueryObjectInnerList, jdbcObjects, union, unionExpression);
		
		if(unionExpression!=null){
			for (int i = 0; i < unionExpression.length; i++) {
				UnionExpression ue = unionExpression[i];
				IExpression expression = ue.getExpression();
				_join(expression,sqlQueryObjectInnerList.get(i));
			}
		}
        
        NonNegativeNumber number = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.unionCount(jdbcProperties, log, connection, sqlQueryObject, 
        								this.getPortaDelegataFieldConverter(), PortaDelegata.model(), 
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
			return new JDBCExpression(this.getPortaDelegataFieldConverter());
		}catch(Exception e){
			throw new ServiceException(e);
		}
	}


	@Override
	public JDBCPaginatedExpression newPaginatedExpression(Logger log) throws NotImplementedException, ServiceException {
		try{
			return new JDBCPaginatedExpression(this.getPortaDelegataFieldConverter());
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
	public void mappingTableIds(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdPortaDelegata id, PortaDelegata obj) throws NotFoundException,NotImplementedException,ServiceException,Exception{
		_mappingTableIds(jdbcProperties,log,connection,sqlQueryObject,obj,
				this.get(jdbcProperties,log,connection,sqlQueryObject,id,null));
	}
	
	@Override
	public void mappingTableIds(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, PortaDelegata obj) throws NotFoundException,NotImplementedException,ServiceException,Exception{
		_mappingTableIds(jdbcProperties,log,connection,sqlQueryObject,obj,
				this.get(jdbcProperties,log,connection,sqlQueryObject,tableId,null));
	}
	private void _mappingTableIds(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, PortaDelegata obj, PortaDelegata imgSaved) throws NotFoundException,NotImplementedException,ServiceException,Exception{
		if(imgSaved==null){
			return;
		}
		obj.setId(imgSaved.getId());
		if(obj.getIdSoggetto()!=null && 
				imgSaved.getIdSoggetto()!=null){
			obj.getIdSoggetto().setId(imgSaved.getIdSoggetto().getId());
		}
		if(obj.getPortaDelegataServizioApplicativoList()!=null){
			List<org.openspcoop2.core.commons.search.PortaDelegataServizioApplicativo> listObj_ = obj.getPortaDelegataServizioApplicativoList();
			for(org.openspcoop2.core.commons.search.PortaDelegataServizioApplicativo itemObj_ : listObj_){
				org.openspcoop2.core.commons.search.PortaDelegataServizioApplicativo itemAlreadySaved_ = null;
				if(imgSaved.getPortaDelegataServizioApplicativoList()!=null){
					List<org.openspcoop2.core.commons.search.PortaDelegataServizioApplicativo> listImgSaved_ = imgSaved.getPortaDelegataServizioApplicativoList();
					for(org.openspcoop2.core.commons.search.PortaDelegataServizioApplicativo itemImgSaved_ : listImgSaved_){
						boolean objEqualsToImgSaved_ = false;
						// TODO verify equals
						// objEqualsToImgSaved_ = org.openspcoop2.generic_project.utils.Utilities.equals(itemObj_.getXXX(),itemImgSaved_.getXXX()) &&
						// 						 			...
						//						 			org.openspcoop2.generic_project.utils.Utilities.equals(itemObj_.getYYY(),itemImgSaved_.getYYY());
						if(objEqualsToImgSaved_){
							itemAlreadySaved_=itemImgSaved_;
							break;
						}
					}
				}
				if(itemAlreadySaved_!=null){
					itemObj_.setId(itemAlreadySaved_.getId());
					if(itemObj_.getIdServizioApplicativo()!=null && 
							itemAlreadySaved_.getIdServizioApplicativo()!=null){
						itemObj_.getIdServizioApplicativo().setId(itemAlreadySaved_.getIdServizioApplicativo().getId());
						if(itemObj_.getIdServizioApplicativo().getIdSoggetto()!=null && 
								itemAlreadySaved_.getIdServizioApplicativo().getIdSoggetto()!=null){
							itemObj_.getIdServizioApplicativo().getIdSoggetto().setId(itemAlreadySaved_.getIdServizioApplicativo().getIdSoggetto().getId());
						}
					}
				}
			}
		}

		/* 
         * TODO: implement code for id mapping
        */

        // Delete this line when you have implemented the method
        int throwNotImplemented = 1;
        if(throwNotImplemented==1){
                throw new NotImplementedException("NotImplemented");
        }
        // Delete this line when you have implemented the method                
	}
	
	@Override
	public PortaDelegata get(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException, MultipleResultException, NotImplementedException, ServiceException, Exception {
		return this._get(jdbcProperties, log, connection, sqlQueryObject, Long.valueOf(tableId), idMappingResolutionBehaviour);
	}
	
	private PortaDelegata _get(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, Long tableId, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException, MultipleResultException, NotImplementedException, ServiceException, Exception {
	
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);
		
		ISQLQueryObject sqlQueryObjectGet = sqlQueryObject.newSQLQueryObject();
				
		PortaDelegata portaDelegata = new PortaDelegata();
		

		// Object portaDelegata
		ISQLQueryObject sqlQueryObjectGet_portaDelegata = sqlQueryObjectGet.newSQLQueryObject();
		sqlQueryObjectGet_portaDelegata.setANDLogicOperator(true);
		sqlQueryObjectGet_portaDelegata.addFromTable(this.getPortaDelegataFieldConverter().toTable(PortaDelegata.model()));
		sqlQueryObjectGet_portaDelegata.addSelectField("id");
		sqlQueryObjectGet_portaDelegata.addSelectField(this.getPortaDelegataFieldConverter().toColumn(PortaDelegata.model().NOME,true));
		sqlQueryObjectGet_portaDelegata.addSelectField(this.getPortaDelegataFieldConverter().toColumn(PortaDelegata.model().STATO,true));
		sqlQueryObjectGet_portaDelegata.addSelectField(this.getPortaDelegataFieldConverter().toColumn(PortaDelegata.model().TIPO_SOGGETTO_EROGATORE,true));
		sqlQueryObjectGet_portaDelegata.addSelectField(this.getPortaDelegataFieldConverter().toColumn(PortaDelegata.model().NOME_SOGGETTO_EROGATORE,true));
		sqlQueryObjectGet_portaDelegata.addSelectField(this.getPortaDelegataFieldConverter().toColumn(PortaDelegata.model().TIPO_SERVIZIO,true));
		sqlQueryObjectGet_portaDelegata.addSelectField(this.getPortaDelegataFieldConverter().toColumn(PortaDelegata.model().NOME_SERVIZIO,true));
		sqlQueryObjectGet_portaDelegata.addSelectField(this.getPortaDelegataFieldConverter().toColumn(PortaDelegata.model().NOME_AZIONE,true));
		sqlQueryObjectGet_portaDelegata.addWhereCondition("id=?");

		// Get portaDelegata
		portaDelegata = (PortaDelegata) jdbcUtilities.executeQuerySingleResult(sqlQueryObjectGet_portaDelegata.createSQLQuery(), jdbcProperties.isShowSql(), PortaDelegata.model(), this.getPortaDelegataFetch(),
			new JDBCObject(tableId,Long.class));


		if(idMappingResolutionBehaviour==null ||
			(org.openspcoop2.generic_project.beans.IDMappingBehaviour.ENABLED.equals(idMappingResolutionBehaviour) || org.openspcoop2.generic_project.beans.IDMappingBehaviour.USE_TABLE_ID.equals(idMappingResolutionBehaviour))
		){
			// Object _portaDelegata_soggetto (recupero id)
			ISQLQueryObject sqlQueryObjectGet_portaDelegata_soggetto_readFkId = sqlQueryObjectGet.newSQLQueryObject();
			sqlQueryObjectGet_portaDelegata_soggetto_readFkId.addFromTable(this.getPortaDelegataFieldConverter().toTable(org.openspcoop2.core.commons.search.PortaDelegata.model()));
			sqlQueryObjectGet_portaDelegata_soggetto_readFkId.addSelectField("id_soggetto");
			sqlQueryObjectGet_portaDelegata_soggetto_readFkId.addWhereCondition("id=?");
			sqlQueryObjectGet_portaDelegata_soggetto_readFkId.setANDLogicOperator(true);
			Long idFK_portaDelegata_soggetto = (Long) jdbcUtilities.executeQuerySingleResult(sqlQueryObjectGet_portaDelegata_soggetto_readFkId.createSQLQuery(), jdbcProperties.isShowSql(),Long.class,
					new JDBCObject(portaDelegata.getId(),Long.class));
			
			org.openspcoop2.core.commons.search.IdSoggetto id_portaDelegata_soggetto = null;
			if(idMappingResolutionBehaviour==null || org.openspcoop2.generic_project.beans.IDMappingBehaviour.ENABLED.equals(idMappingResolutionBehaviour)){
				id_portaDelegata_soggetto = ((JDBCSoggettoServiceSearch)(this.getServiceManager().getSoggettoServiceSearch())).findId(idFK_portaDelegata_soggetto, false);
			}else{
				id_portaDelegata_soggetto = new org.openspcoop2.core.commons.search.IdSoggetto();
			}
			id_portaDelegata_soggetto.setId(idFK_portaDelegata_soggetto);
			//TODO Impostare il corretto metodo che contiene l'identificativo logico
			//portaDelegata.setSoggetto(id_portaDelegata_soggetto);
		}


		// Object portaDelegata_portaDelegataServizioApplicativo
		ISQLQueryObject sqlQueryObjectGet_portaDelegata_portaDelegataServizioApplicativo = sqlQueryObjectGet.newSQLQueryObject();
		sqlQueryObjectGet_portaDelegata_portaDelegataServizioApplicativo.setANDLogicOperator(true);
		sqlQueryObjectGet_portaDelegata_portaDelegataServizioApplicativo.addFromTable(this.getPortaDelegataFieldConverter().toTable(PortaDelegata.model().PORTA_DELEGATA_SERVIZIO_APPLICATIVO));
		sqlQueryObjectGet_portaDelegata_portaDelegataServizioApplicativo.addSelectField("id");
		sqlQueryObjectGet_portaDelegata_portaDelegataServizioApplicativo.addWhereCondition("id_porta=?");

		// Get portaDelegata_portaDelegataServizioApplicativo
		java.util.List<Object> portaDelegata_portaDelegataServizioApplicativo_list = (java.util.List<Object>) jdbcUtilities.executeQuery(sqlQueryObjectGet_portaDelegata_portaDelegataServizioApplicativo.createSQLQuery(), jdbcProperties.isShowSql(), PortaDelegata.model().PORTA_DELEGATA_SERVIZIO_APPLICATIVO, this.getPortaDelegataFetch(),
			new JDBCObject(portaDelegata.getId(),Long.class));

		if(portaDelegata_portaDelegataServizioApplicativo_list != null) {
			for (Object portaDelegata_portaDelegataServizioApplicativo_object: portaDelegata_portaDelegataServizioApplicativo_list) {
				PortaDelegataServizioApplicativo portaDelegata_portaDelegataServizioApplicativo = (PortaDelegataServizioApplicativo) portaDelegata_portaDelegataServizioApplicativo_object;


				if(idMappingResolutionBehaviour==null ||
					(org.openspcoop2.generic_project.beans.IDMappingBehaviour.ENABLED.equals(idMappingResolutionBehaviour) || org.openspcoop2.generic_project.beans.IDMappingBehaviour.USE_TABLE_ID.equals(idMappingResolutionBehaviour))
				){
					// Object _portaDelegata_portaDelegataServizioApplicativo_servizioApplicativo (recupero id)
					ISQLQueryObject sqlQueryObjectGet_portaDelegata_portaDelegataServizioApplicativo_servizioApplicativo_readFkId = sqlQueryObjectGet.newSQLQueryObject();
					sqlQueryObjectGet_portaDelegata_portaDelegataServizioApplicativo_servizioApplicativo_readFkId.addFromTable(this.getPortaDelegataFieldConverter().toTable(org.openspcoop2.core.commons.search.PortaDelegata.model().PORTA_DELEGATA_SERVIZIO_APPLICATIVO));
					sqlQueryObjectGet_portaDelegata_portaDelegataServizioApplicativo_servizioApplicativo_readFkId.addSelectField("id_servizio_applicativo");
					sqlQueryObjectGet_portaDelegata_portaDelegataServizioApplicativo_servizioApplicativo_readFkId.addWhereCondition("id=?");
					sqlQueryObjectGet_portaDelegata_portaDelegataServizioApplicativo_servizioApplicativo_readFkId.setANDLogicOperator(true);
					Long idFK_portaDelegata_portaDelegataServizioApplicativo_servizioApplicativo = (Long) jdbcUtilities.executeQuerySingleResult(sqlQueryObjectGet_portaDelegata_portaDelegataServizioApplicativo_servizioApplicativo_readFkId.createSQLQuery(), jdbcProperties.isShowSql(),Long.class,
							new JDBCObject(portaDelegata_portaDelegataServizioApplicativo.getId(),Long.class));
					
					org.openspcoop2.core.commons.search.IdServizioApplicativo id_portaDelegata_portaDelegataServizioApplicativo_servizioApplicativo = null;
					if(idMappingResolutionBehaviour==null || org.openspcoop2.generic_project.beans.IDMappingBehaviour.ENABLED.equals(idMappingResolutionBehaviour)){
						id_portaDelegata_portaDelegataServizioApplicativo_servizioApplicativo = ((JDBCServizioApplicativoServiceSearch)(this.getServiceManager().getServizioApplicativoServiceSearch())).findId(idFK_portaDelegata_portaDelegataServizioApplicativo_servizioApplicativo, false);
					}else{
						id_portaDelegata_portaDelegataServizioApplicativo_servizioApplicativo = new org.openspcoop2.core.commons.search.IdServizioApplicativo();
					}
					id_portaDelegata_portaDelegataServizioApplicativo_servizioApplicativo.setId(idFK_portaDelegata_portaDelegataServizioApplicativo_servizioApplicativo);
					//TODO Impostare il corretto metodo che contiene l'identificativo logico
					//portaDelegata_portaDelegataServizioApplicativo.setServizioApplicativo(id_portaDelegata_portaDelegataServizioApplicativo_servizioApplicativo);
				}

				portaDelegata.addPortaDelegataServizioApplicativo(portaDelegata_portaDelegataServizioApplicativo);
			}
		}

		/* 
		 * TODO: implement code that returns the object identified by the id
		*/
		
		// Delete this line when you have implemented the method
		int throwNotImplemented = 1;
		if(throwNotImplemented==1){
		        throw new NotImplementedException("NotImplemented");
		}
		// Delete this line when you have implemented the method                
		
        return portaDelegata;  
	
	} 
	
	@Override
	public boolean exists(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId) throws MultipleResultException, NotImplementedException, ServiceException, Exception {
		return this._exists(jdbcProperties, log, connection, sqlQueryObject, Long.valueOf(tableId));
	}
	
	private boolean _exists(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, Long tableId) throws MultipleResultException, NotImplementedException, ServiceException, Exception {
	
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);
				
		boolean existsPortaDelegata = false;

		sqlQueryObject = sqlQueryObject.newSQLQueryObject();
		sqlQueryObject.setANDLogicOperator(true);

		sqlQueryObject.addFromTable(this.getPortaDelegataFieldConverter().toTable(PortaDelegata.model()));
		sqlQueryObject.addSelectField(this.getPortaDelegataFieldConverter().toColumn(PortaDelegata.model().NOME,true));
		sqlQueryObject.addWhereCondition("id=?");


		// Exists portaDelegata
		existsPortaDelegata = jdbcUtilities.exists(sqlQueryObject.createSQLQuery(), jdbcProperties.isShowSql(),
			new JDBCObject(tableId,Long.class));

		
        return existsPortaDelegata;
	
	}
	
	private void _join(IExpression expression, ISQLQueryObject sqlQueryObject) throws NotImplementedException, ServiceException, Exception{
	
		/* 
		 * TODO: implement code that implement the join condition
		*/
		/*
		if(expression.inUseModel(PortaDelegata.model().XXXX,false)){
			String tableName1 = this.getPortaDelegataFieldConverter().toAliasTable(PortaDelegata.model());
			String tableName2 = this.getPortaDelegataFieldConverter().toAliasTable(PortaDelegata.model().XXX);
			sqlQueryObject.addWhereCondition(tableName1+".id="+tableName2+".id_table1");
		}
		*/
		
		/* 
         * TODO: implementa il codice che aggiunge la condizione FROM Table per le condizioni di join di oggetti annidati dal secondo livello in poi 
         *       La addFromTable deve essere aggiunta solo se l'oggetto del livello precedente non viene utilizzato nella espressione 
         *		 altrimenti il metodo sopra 'toSqlForPreparedStatementWithFromCondition' si occupa gia' di aggiungerla
        */
        /*
        if(expression.inUseModel(PortaDelegata.model().LEVEL1.LEVEL2,false)){
			if(expression.inUseModel(PortaDelegata.model().LEVEL1,false)==false){
				sqlQueryObject.addFromTable(this.getPortaDelegataFieldConverter().toTable(PortaDelegata.model().LEVEL1));
			}
		}
		...
		if(expression.inUseModel(PortaDelegata.model()....LEVELN.LEVELN+1,false)){
			if(expression.inUseModel(PortaDelegata.model().LEVELN,false)==false){
				sqlQueryObject.addFromTable(this.getPortaDelegataFieldConverter().toTable(PortaDelegata.model().LEVELN));
			}
		}
		*/
		
		// Delete this line when you have implemented the join condition
		int throwNotImplemented = 1;
		if(throwNotImplemented==1){
		        throw new NotImplementedException("NotImplemented");
		}
		// Delete this line when you have implemented the join condition
        
	}
	
	protected java.util.List<Object> _getRootTablePrimaryKeyValues(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdPortaDelegata id) throws NotFoundException, ServiceException, NotImplementedException, Exception{
	    // Identificativi
        java.util.List<Object> rootTableIdValues = new java.util.ArrayList<Object>();
        // TODO: Define the column values used to identify the primary key
		Long longId = this.findIdPortaDelegata(jdbcProperties, log, connection, sqlQueryObject.newSQLQueryObject(), id, true);
		rootTableIdValues.add(longId);
        
        // Delete this line when you have verified the method
		int throwNotImplemented = 1;
		if(throwNotImplemented==1){
		        throw new NotImplementedException("NotImplemented");
		}
		// Delete this line when you have verified the method
        
        return rootTableIdValues;
	}
	
	protected Map<String, List<IField>> _getMapTableToPKColumn() throws NotImplementedException, Exception{
	
		PortaDelegataFieldConverter converter = this.getPortaDelegataFieldConverter();
		Map<String, List<IField>> mapTableToPKColumn = new java.util.Hashtable<String, List<IField>>();
		UtilsTemplate<IField> utilities = new UtilsTemplate<IField>();

		// TODO: Define the columns used to identify the primary key
		//		  If a table doesn't have a primary key, don't add it to this map

		// PortaDelegata.model()
		mapTableToPKColumn.put(converter.toTable(PortaDelegata.model()),
			utilities.newList(
				new CustomField("id", Long.class, "id", converter.toTable(PortaDelegata.model()))
			));

		// PortaDelegata.model().ID_SOGGETTO
		mapTableToPKColumn.put(converter.toTable(PortaDelegata.model().ID_SOGGETTO),
			utilities.newList(
				new CustomField("id", Long.class, "id", converter.toTable(PortaDelegata.model().ID_SOGGETTO))
			));

		// PortaDelegata.model().PORTA_DELEGATA_SERVIZIO_APPLICATIVO
		mapTableToPKColumn.put(converter.toTable(PortaDelegata.model().PORTA_DELEGATA_SERVIZIO_APPLICATIVO),
			utilities.newList(
				new CustomField("id", Long.class, "id", converter.toTable(PortaDelegata.model().PORTA_DELEGATA_SERVIZIO_APPLICATIVO))
			));

		// PortaDelegata.model().PORTA_DELEGATA_SERVIZIO_APPLICATIVO.ID_SERVIZIO_APPLICATIVO
		mapTableToPKColumn.put(converter.toTable(PortaDelegata.model().PORTA_DELEGATA_SERVIZIO_APPLICATIVO.ID_SERVIZIO_APPLICATIVO),
			utilities.newList(
				new CustomField("id", Long.class, "id", converter.toTable(PortaDelegata.model().PORTA_DELEGATA_SERVIZIO_APPLICATIVO.ID_SERVIZIO_APPLICATIVO))
			));

		// PortaDelegata.model().PORTA_DELEGATA_SERVIZIO_APPLICATIVO.ID_SERVIZIO_APPLICATIVO.ID_SOGGETTO
		mapTableToPKColumn.put(converter.toTable(PortaDelegata.model().PORTA_DELEGATA_SERVIZIO_APPLICATIVO.ID_SERVIZIO_APPLICATIVO.ID_SOGGETTO),
			utilities.newList(
				new CustomField("id", Long.class, "id", converter.toTable(PortaDelegata.model().PORTA_DELEGATA_SERVIZIO_APPLICATIVO.ID_SERVIZIO_APPLICATIVO.ID_SOGGETTO))
			));


        // Delete this line when you have verified the method
		int throwNotImplemented = 1;
		if(throwNotImplemented==1){
		        throw new NotImplementedException("NotImplemented");
		}
		// Delete this line when you have verified the method
        
        return mapTableToPKColumn;		
	}
	
	@Override
	public List<Long> findAllTableIds(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCPaginatedExpression paginatedExpression) throws ServiceException, NotImplementedException, Exception {
		
		List<Long> list = new ArrayList<Long>();

		sqlQueryObject.setSelectDistinct(true);
		sqlQueryObject.setANDLogicOperator(true);
		sqlQueryObject.addSelectField(this.getPortaDelegataFieldConverter().toTable(PortaDelegata.model())+".id");
		Class<?> objectIdClass = Long.class;
		
		List<Object> listaQuery = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.prepareFindAll(jdbcProperties, log, connection, sqlQueryObject, paginatedExpression,
												this.getPortaDelegataFieldConverter(), PortaDelegata.model());
		
		_join(paginatedExpression,sqlQueryObject);
		
		List<Object> listObjects = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.findAll(jdbcProperties, log, connection, sqlQueryObject, paginatedExpression,
																			this.getPortaDelegataFieldConverter(), PortaDelegata.model(), objectIdClass, listaQuery);
		for(Object object: listObjects) {
			list.add((Long)object);
		}

        return list;
		
	}
	
	@Override
	public long findTableId(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCExpression expression) throws ServiceException, NotFoundException, MultipleResultException, NotImplementedException, Exception {
	
		sqlQueryObject.setSelectDistinct(true);
		sqlQueryObject.setANDLogicOperator(true);
		sqlQueryObject.addSelectField(this.getPortaDelegataFieldConverter().toTable(PortaDelegata.model())+".id");
		Class<?> objectIdClass = Long.class;
		
		List<Object> listaQuery = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.prepareFind(jdbcProperties, log, connection, sqlQueryObject, expression,
												this.getPortaDelegataFieldConverter(), PortaDelegata.model());
		
		_join(expression,sqlQueryObject);

		Object res = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.find(jdbcProperties, log, connection, sqlQueryObject, expression,
														this.getPortaDelegataFieldConverter(), PortaDelegata.model(), objectIdClass, listaQuery);
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
	public IdPortaDelegata findId(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, boolean throwNotFound)
			throws NotFoundException, ServiceException, NotImplementedException, Exception {
		
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);

		ISQLQueryObject sqlQueryObjectGet = sqlQueryObject.newSQLQueryObject();

		/* 
		 * TODO: implement code that returns the object identified by the id
		*/

		// Delete this line when you have implemented the method
		int throwNotImplemented = 1;
		if(throwNotImplemented==1){
		        throw new NotImplementedException("NotImplemented");
		}
 		// Delete this line when you have implemented the method                

		// Object _portaDelegata
		//TODO Implementare la ricerca dell'id
		sqlQueryObjectGet.addFromTable(this.getPortaDelegataFieldConverter().toTable(PortaDelegata.model()));
		// TODO select field for identify ObjectId
		//sqlQueryObjectGet.addSelectField(this.getPortaDelegataFieldConverter().toColumn(PortaDelegata.model().NOME_COLONNA_1,true));
		//...
		//sqlQueryObjectGet.addSelectField(this.getPortaDelegataFieldConverter().toColumn(PortaDelegata.model().NOME_COLONNA_N,true));
		sqlQueryObjectGet.setANDLogicOperator(true);
		sqlQueryObjectGet.addWhereCondition("id=?");

		// Recupero _portaDelegata
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject [] searchParams_portaDelegata = new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject [] { 
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(tableId,Long.class)
		};
		List<Class<?>> listaFieldIdReturnType_portaDelegata = new ArrayList<Class<?>>();
		//listaFieldIdReturnType_portaDelegata.add(Id1.class);
		//...
		//listaFieldIdReturnType_portaDelegata.add(IdN.class);
		org.openspcoop2.core.commons.search.IdPortaDelegata id_portaDelegata = null;
		List<Object> listaFieldId_portaDelegata = jdbcUtilities.executeQuerySingleResult(sqlQueryObjectGet.createSQLQuery(), jdbcProperties.isShowSql(),
				listaFieldIdReturnType_portaDelegata, searchParams_portaDelegata);
		if(listaFieldId_portaDelegata==null || listaFieldId_portaDelegata.size()<=0){
			if(throwNotFound){
				throw new NotFoundException("Not Found");
			}
		}
		else{
			// set _portaDelegata
			id_portaDelegata = new org.openspcoop2.core.commons.search.IdPortaDelegata();
			// id_portaDelegata.setId1(listaFieldId_portaDelegata.get(0));
			// ...
			// id_portaDelegata.setIdN(listaFieldId_portaDelegata.get(N-1));
		}
		
		return id_portaDelegata;
		
	}

	@Override
	public Long findTableId(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdPortaDelegata id, boolean throwNotFound)
			throws NotFoundException, ServiceException, NotImplementedException, Exception {
	
		return this.findIdPortaDelegata(jdbcProperties,log,connection,sqlQueryObject,id,throwNotFound);
			
	}
	
	@Override
	public List<List<Object>> nativeQuery(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, 
											String sql,List<Class<?>> returnClassTypes,Object ... param) throws ServiceException,NotFoundException,NotImplementedException,Exception{
		
		return org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.nativeQuery(jdbcProperties, log, connection, sqlQueryObject,
																							sql,returnClassTypes,param);
														
	}
	
	protected Long findIdPortaDelegata(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdPortaDelegata id, boolean throwNotFound) throws NotFoundException, ServiceException, NotImplementedException, Exception {

		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);

		ISQLQueryObject sqlQueryObjectGet = sqlQueryObject.newSQLQueryObject();

		/* 
		 * TODO: implement code that returns the object identified by the id
		*/

		// Delete this line when you have implemented the method
		int throwNotImplemented = 1;
		if(throwNotImplemented==1){
		        throw new NotImplementedException("NotImplemented");
		}
 		// Delete this line when you have implemented the method                

		// Object _portaDelegata
		//TODO Implementare la ricerca dell'id
		sqlQueryObjectGet.addFromTable(this.getPortaDelegataFieldConverter().toTable(PortaDelegata.model()));
		sqlQueryObjectGet.addSelectField("id");
		// Devono essere mappati nella where condition i metodi dell'oggetto id.getXXX
		sqlQueryObjectGet.setANDLogicOperator(true);
		sqlQueryObjectGet.setSelectDistinct(true);
		//sqlQueryObjectGet.addWhereCondition(this.getPortaDelegataFieldConverter().toColumn(PortaDelegata.model().NOME_COLONNA_1,true)+"=?");
		// ...
		//sqlQueryObjectGet.addWhereCondition(this.getPortaDelegataFieldConverter().toColumn(PortaDelegata.model().NOME_COLONNA_N,true)+"=?");

		// Recupero _portaDelegata
		// TODO Aggiungere i valori dei parametri di ricerca sopra definiti recuperandoli con i metodi dell'oggetto id.getXXX
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject [] searchParams_portaDelegata = new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject [] { 
			//new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(object,object.class),
			//...
			//new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(object,object.class)
		};
		Long id_portaDelegata = null;
		try{
			id_portaDelegata = (Long) jdbcUtilities.executeQuerySingleResult(sqlQueryObjectGet.createSQLQuery(), jdbcProperties.isShowSql(),
						Long.class, searchParams_portaDelegata);
		}catch(NotFoundException notFound){
			if(throwNotFound){
				throw new NotFoundException(notFound);
			}
		}
		if(id_portaDelegata==null || id_portaDelegata<=0){
			if(throwNotFound){
				throw new NotFoundException("Not Found");
			}
		}
		
		return id_portaDelegata;
	}
}
