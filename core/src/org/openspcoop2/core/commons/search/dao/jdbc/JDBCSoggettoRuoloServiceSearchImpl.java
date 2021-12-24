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
package org.openspcoop2.core.commons.search.dao.jdbc;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.openspcoop2.core.commons.search.IdRuolo;
import org.openspcoop2.core.commons.search.IdSoggetto;
import org.openspcoop2.core.commons.search.IdSoggettoRuolo;
import org.openspcoop2.core.commons.search.Ruolo;
import org.openspcoop2.core.commons.search.Soggetto;
import org.openspcoop2.core.commons.search.SoggettoRuolo;
import org.openspcoop2.core.commons.search.dao.IDBRuoloServiceSearch;
import org.openspcoop2.core.commons.search.dao.IDBSoggettoServiceSearch;
import org.openspcoop2.core.commons.search.dao.jdbc.converter.SoggettoRuoloFieldConverter;
import org.openspcoop2.core.commons.search.dao.jdbc.fetch.SoggettoRuoloFetch;
import org.openspcoop2.generic_project.beans.CustomField;
import org.openspcoop2.generic_project.beans.FunctionField;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.InUse;
import org.openspcoop2.generic_project.beans.NonNegativeNumber;
import org.openspcoop2.generic_project.beans.Union;
import org.openspcoop2.generic_project.beans.UnionExpression;
import org.openspcoop2.generic_project.dao.jdbc.IJDBCServiceSearchWithId;
import org.openspcoop2.generic_project.dao.jdbc.JDBCExpression;
import org.openspcoop2.generic_project.dao.jdbc.JDBCPaginatedExpression;
import org.openspcoop2.generic_project.dao.jdbc.JDBCServiceManagerProperties;
import org.openspcoop2.generic_project.dao.jdbc.utils.IJDBCFetch;
import org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject;
import org.openspcoop2.generic_project.exception.MultipleResultException;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.generic_project.exception.NotImplementedException;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.generic_project.expression.IExpression;
import org.openspcoop2.generic_project.expression.impl.sql.ISQLFieldConverter;
import org.openspcoop2.generic_project.utils.UtilsTemplate;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.slf4j.Logger;

/**     
 * JDBCSoggettoRuoloServiceSearchImpl
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JDBCSoggettoRuoloServiceSearchImpl implements IJDBCServiceSearchWithId<SoggettoRuolo, IdSoggettoRuolo, JDBCServiceManager> {

	private SoggettoRuoloFieldConverter _soggettoRuoloFieldConverter = null;
	public SoggettoRuoloFieldConverter getSoggettoRuoloFieldConverter() {
		if(this._soggettoRuoloFieldConverter==null){
			this._soggettoRuoloFieldConverter = new SoggettoRuoloFieldConverter(this.jdbcServiceManager.getJdbcProperties().getDatabaseType());
		}		
		return this._soggettoRuoloFieldConverter;
	}
	@Override
	public ISQLFieldConverter getFieldConverter() {
		return this.getSoggettoRuoloFieldConverter();
	}
	
	private SoggettoRuoloFetch soggettoRuoloFetch = new SoggettoRuoloFetch();
	public SoggettoRuoloFetch getSoggettoRuoloFetch() {
		return this.soggettoRuoloFetch;
	}
	@Override
	public IJDBCFetch getFetch() {
		return getSoggettoRuoloFetch();
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
	public IdSoggettoRuolo convertToId(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, SoggettoRuolo soggettoRuolo) throws NotImplementedException, ServiceException, Exception{
	
		IdSoggettoRuolo idSoggettoRuolo = new IdSoggettoRuolo();
		idSoggettoRuolo.setIdRuolo(soggettoRuolo.getIdRuolo());
		idSoggettoRuolo.setIdSoggetto(soggettoRuolo.getIdSoggetto());
		return idSoggettoRuolo;
	}
	
	@Override
	public SoggettoRuolo get(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdSoggettoRuolo id, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException, MultipleResultException, NotImplementedException, ServiceException,Exception {
		Long id_soggettoRuolo = ( (id!=null && id.getId()!=null && id.getId()>0) ? id.getId() : this.findIdSoggettoRuolo(jdbcProperties, log, connection, sqlQueryObject, id, true));
		return this._get(jdbcProperties, log, connection, sqlQueryObject, id_soggettoRuolo,idMappingResolutionBehaviour);
		
		
	}
	
	@Override
	public boolean exists(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdSoggettoRuolo id) throws MultipleResultException, NotImplementedException, ServiceException,Exception {

		Long id_soggettoRuolo = this.findIdSoggettoRuolo(jdbcProperties, log, connection, sqlQueryObject, id, false);
		return id_soggettoRuolo != null && id_soggettoRuolo > 0;
		
	}
	
	@Override
	public List<IdSoggettoRuolo> findAllIds(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCPaginatedExpression expression, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotImplementedException, ServiceException,Exception {

		List<IdSoggettoRuolo> list = new ArrayList<IdSoggettoRuolo>();

		// TODO: implementazione non efficente. 
		// Per ottenere una implementazione efficente:
		// 1. Usare metodo select di questa classe indirizzando esattamente i field necessari a create l'ID logico
		// 2. Usare metodo getSoggettoRuoloFetch() sul risultato della select per ottenere un oggetto SoggettoRuolo
		//	  La fetch con la map inserirà nell'oggetto solo i valori estratti 
		// 3. Usare metodo convertToId per ottenere l'id

        List<Long> ids = this.findAllTableIds(jdbcProperties, log, connection, sqlQueryObject, expression);
        
        for(Long id: ids) {
        	SoggettoRuolo soggettoRuolo = this.get(jdbcProperties, log, connection, sqlQueryObject, id, idMappingResolutionBehaviour);
			IdSoggettoRuolo idSoggettoRuolo = this.convertToId(jdbcProperties,log,connection,sqlQueryObject,soggettoRuolo);
        	list.add(idSoggettoRuolo);
        }

        return list;
		
	}
	
	@Override
	public List<SoggettoRuolo> findAll(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCPaginatedExpression expression, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotImplementedException, ServiceException,Exception {

        List<SoggettoRuolo> list = new ArrayList<SoggettoRuolo>();
        
        // TODO: implementazione non efficente. 
		// Per ottenere una implementazione efficente:
		// 1. Usare metodo select di questa classe indirizzando esattamente i field necessari
		// 2. Usare metodo getSoggettoRuoloFetch() sul risultato della select per ottenere un oggetto SoggettoRuolo
		//	  La fetch con la map inserirà nell'oggetto solo i valori estratti 

        List<Long> ids = this.findAllTableIds(jdbcProperties, log, connection, sqlQueryObject, expression);
        
        for(Long id: ids) {
        	list.add(this.get(jdbcProperties, log, connection, sqlQueryObject, id, idMappingResolutionBehaviour));
        }

        return list;      
		
	}
	
	@Override
	public SoggettoRuolo find(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCExpression expression, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) 
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
												this.getSoggettoRuoloFieldConverter(), SoggettoRuolo.model());
		
		sqlQueryObject.addSelectCountField(this.getSoggettoRuoloFieldConverter().toTable(SoggettoRuolo.model())+".id","tot",true);
		
		_join(expression,sqlQueryObject);
		
		return org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.count(jdbcProperties, log, connection, sqlQueryObject, expression,
																			this.getSoggettoRuoloFieldConverter(), SoggettoRuolo.model(),listaQuery);
	}

	@Override
	public InUse inUse(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdSoggettoRuolo id) throws NotFoundException, NotImplementedException, ServiceException,Exception {
		
		Long id_soggettoRuolo = this.findIdSoggettoRuolo(jdbcProperties, log, connection, sqlQueryObject, id, true);
        return this._inUse(jdbcProperties, log, connection, sqlQueryObject, id_soggettoRuolo);
		
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
												this.getSoggettoRuoloFieldConverter(), field);

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
        						expression, this.getSoggettoRuoloFieldConverter(), SoggettoRuolo.model(), 
        						listaQuery,listaParams);
		
		_join(expression,sqlQueryObject);
        
        List<Map<String,Object>> list = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.select(jdbcProperties, log, connection,
        								org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.prepareSqlQueryObjectForSelectDistinct(sqlQueryObject,sqlQueryObjectDistinct), 
        								expression, this.getSoggettoRuoloFieldConverter(), SoggettoRuolo.model(),
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
        						this.getSoggettoRuoloFieldConverter(), SoggettoRuolo.model(), 
        						sqlQueryObjectInnerList, jdbcObjects, union, unionExpression);
		
		if(unionExpression!=null){
			for (int i = 0; i < unionExpression.length; i++) {
				UnionExpression ue = unionExpression[i];
				IExpression expression = ue.getExpression();
				_join(expression,sqlQueryObjectInnerList.get(i));
			}
		}
        
        List<Map<String,Object>> list = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.union(jdbcProperties, log, connection, sqlQueryObject, 
        								this.getSoggettoRuoloFieldConverter(), SoggettoRuolo.model(), 
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
        						this.getSoggettoRuoloFieldConverter(), SoggettoRuolo.model(), 
        						sqlQueryObjectInnerList, jdbcObjects, union, unionExpression);
		
		if(unionExpression!=null){
			for (int i = 0; i < unionExpression.length; i++) {
				UnionExpression ue = unionExpression[i];
				IExpression expression = ue.getExpression();
				_join(expression,sqlQueryObjectInnerList.get(i));
			}
		}
        
        NonNegativeNumber number = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.unionCount(jdbcProperties, log, connection, sqlQueryObject, 
        								this.getSoggettoRuoloFieldConverter(), SoggettoRuolo.model(), 
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
			return new JDBCExpression(this.getSoggettoRuoloFieldConverter());
		}catch(Exception e){
			throw new ServiceException(e);
		}
	}


	@Override
	public JDBCPaginatedExpression newPaginatedExpression(Logger log) throws NotImplementedException, ServiceException {
		try{
			return new JDBCPaginatedExpression(this.getSoggettoRuoloFieldConverter());
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
	public void mappingTableIds(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdSoggettoRuolo id, SoggettoRuolo obj) throws NotFoundException,NotImplementedException,ServiceException,Exception{
		_mappingTableIds(jdbcProperties,log,connection,sqlQueryObject,obj,
				this.get(jdbcProperties,log,connection,sqlQueryObject,id,null));
	}
	
	@Override
	public void mappingTableIds(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, SoggettoRuolo obj) throws NotFoundException,NotImplementedException,ServiceException,Exception{
		_mappingTableIds(jdbcProperties,log,connection,sqlQueryObject,obj,
				this.get(jdbcProperties,log,connection,sqlQueryObject,tableId,null));
	}
	private void _mappingTableIds(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, SoggettoRuolo obj, SoggettoRuolo imgSaved) throws NotFoundException,NotImplementedException,ServiceException,Exception{
		if(imgSaved==null){
			return;
		}
		obj.setId(imgSaved.getId());
		if(obj.getIdRuolo()!=null && 
				imgSaved.getIdRuolo()!=null){
			obj.getIdRuolo().setId(imgSaved.getIdRuolo().getId());
		}
		if(obj.getIdSoggetto()!=null && 
				imgSaved.getIdSoggetto()!=null){
			obj.getIdSoggetto().setId(imgSaved.getIdSoggetto().getId());
		}

	}
	
	@Override
	public SoggettoRuolo get(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException, MultipleResultException, NotImplementedException, ServiceException, Exception {
		return this._get(jdbcProperties, log, connection, sqlQueryObject, Long.valueOf(tableId), idMappingResolutionBehaviour);
	}
	
	private SoggettoRuolo _get(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, Long tableId, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException, MultipleResultException, NotImplementedException, ServiceException, Exception {
	
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);
		
		ISQLQueryObject sqlQueryObjectGet = sqlQueryObject.newSQLQueryObject();
				
		SoggettoRuolo soggettoRuolo = new SoggettoRuolo();
		

		// Object soggettoRuolo
		ISQLQueryObject sqlQueryObjectGet_soggettoRuolo = sqlQueryObjectGet.newSQLQueryObject();
		sqlQueryObjectGet_soggettoRuolo.setANDLogicOperator(true);
		sqlQueryObjectGet_soggettoRuolo.addFromTable(this.getSoggettoRuoloFieldConverter().toTable(SoggettoRuolo.model()));
		sqlQueryObjectGet_soggettoRuolo.addSelectField("id");
		sqlQueryObjectGet_soggettoRuolo.addWhereCondition("id=?");

		// Get soggettoRuolo
		soggettoRuolo = (SoggettoRuolo) jdbcUtilities.executeQuerySingleResult(sqlQueryObjectGet_soggettoRuolo.createSQLQuery(), jdbcProperties.isShowSql(), SoggettoRuolo.model(), this.getSoggettoRuoloFetch(),
			new JDBCObject(tableId,Long.class));


		if(idMappingResolutionBehaviour==null ||
			(org.openspcoop2.generic_project.beans.IDMappingBehaviour.ENABLED.equals(idMappingResolutionBehaviour) || org.openspcoop2.generic_project.beans.IDMappingBehaviour.USE_TABLE_ID.equals(idMappingResolutionBehaviour))
		){
			// Object _soggettoRuolo_ruolo (recupero id)
			ISQLQueryObject sqlQueryObjectGet_soggettoRuolo_ruolo_readFkId = sqlQueryObjectGet.newSQLQueryObject();
			sqlQueryObjectGet_soggettoRuolo_ruolo_readFkId.addFromTable(this.getSoggettoRuoloFieldConverter().toTable(org.openspcoop2.core.commons.search.SoggettoRuolo.model()));
			sqlQueryObjectGet_soggettoRuolo_ruolo_readFkId.addSelectField("id_ruolo");
			sqlQueryObjectGet_soggettoRuolo_ruolo_readFkId.addWhereCondition("id=?");
			sqlQueryObjectGet_soggettoRuolo_ruolo_readFkId.setANDLogicOperator(true);
			Long idFK_soggettoRuolo_ruolo = (Long) jdbcUtilities.executeQuerySingleResult(sqlQueryObjectGet_soggettoRuolo_ruolo_readFkId.createSQLQuery(), jdbcProperties.isShowSql(),Long.class,
					new JDBCObject(soggettoRuolo.getId(),Long.class));
			
			org.openspcoop2.core.commons.search.IdRuolo id_soggettoRuolo_ruolo = null;
			if(idMappingResolutionBehaviour==null || org.openspcoop2.generic_project.beans.IDMappingBehaviour.ENABLED.equals(idMappingResolutionBehaviour)){
				id_soggettoRuolo_ruolo = ((JDBCRuoloServiceSearch)(this.getServiceManager(connection, jdbcProperties, log).getRuoloServiceSearch())).findId(idFK_soggettoRuolo_ruolo, false);
			}else{
				id_soggettoRuolo_ruolo = new org.openspcoop2.core.commons.search.IdRuolo();
			}
			id_soggettoRuolo_ruolo.setId(idFK_soggettoRuolo_ruolo);
			soggettoRuolo.setIdRuolo(id_soggettoRuolo_ruolo);
		}

		if(idMappingResolutionBehaviour==null ||
			(org.openspcoop2.generic_project.beans.IDMappingBehaviour.ENABLED.equals(idMappingResolutionBehaviour) || org.openspcoop2.generic_project.beans.IDMappingBehaviour.USE_TABLE_ID.equals(idMappingResolutionBehaviour))
		){
			// Object _soggettoRuolo_soggetto (recupero id)
			ISQLQueryObject sqlQueryObjectGet_soggettoRuolo_soggetto_readFkId = sqlQueryObjectGet.newSQLQueryObject();
			sqlQueryObjectGet_soggettoRuolo_soggetto_readFkId.addFromTable(this.getSoggettoRuoloFieldConverter().toTable(org.openspcoop2.core.commons.search.SoggettoRuolo.model()));
			sqlQueryObjectGet_soggettoRuolo_soggetto_readFkId.addSelectField("id_soggetto");
			sqlQueryObjectGet_soggettoRuolo_soggetto_readFkId.addWhereCondition("id=?");
			sqlQueryObjectGet_soggettoRuolo_soggetto_readFkId.setANDLogicOperator(true);
			Long idFK_soggettoRuolo_soggetto = (Long) jdbcUtilities.executeQuerySingleResult(sqlQueryObjectGet_soggettoRuolo_soggetto_readFkId.createSQLQuery(), jdbcProperties.isShowSql(),Long.class,
					new JDBCObject(soggettoRuolo.getId(),Long.class));
			
			org.openspcoop2.core.commons.search.IdSoggetto id_soggettoRuolo_soggetto = null;
			if(idMappingResolutionBehaviour==null || org.openspcoop2.generic_project.beans.IDMappingBehaviour.ENABLED.equals(idMappingResolutionBehaviour)){
				id_soggettoRuolo_soggetto = ((JDBCSoggettoServiceSearch)(this.getServiceManager(connection, jdbcProperties, log).getSoggettoServiceSearch())).findId(idFK_soggettoRuolo_soggetto, false);
			}else{
				id_soggettoRuolo_soggetto = new org.openspcoop2.core.commons.search.IdSoggetto();
			}
			id_soggettoRuolo_soggetto.setId(idFK_soggettoRuolo_soggetto);
			soggettoRuolo.setIdSoggetto(id_soggettoRuolo_soggetto);
		}

        return soggettoRuolo;  
	
	} 
	
	@Override
	public boolean exists(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId) throws MultipleResultException, NotImplementedException, ServiceException, Exception {
		return this._exists(jdbcProperties, log, connection, sqlQueryObject, Long.valueOf(tableId));
	}
	
	private boolean _exists(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, Long tableId) throws MultipleResultException, NotImplementedException, ServiceException, Exception {
	
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);
				
		boolean existsSoggettoRuolo = false;

		sqlQueryObject = sqlQueryObject.newSQLQueryObject();
		sqlQueryObject.setANDLogicOperator(true);

		sqlQueryObject.addWhereCondition("id=?");


		// Exists soggettoRuolo
		existsSoggettoRuolo = jdbcUtilities.exists(sqlQueryObject.createSQLQuery(), jdbcProperties.isShowSql(),
			new JDBCObject(tableId,Long.class));

		
        return existsSoggettoRuolo;
	
	}
	
	private void _join(IExpression expression, ISQLQueryObject sqlQueryObject) throws NotImplementedException, ServiceException, Exception{
	
		if(expression.inUseModel(SoggettoRuolo.model().ID_RUOLO,false)){
			String tableName1 = this.getSoggettoRuoloFieldConverter().toAliasTable(SoggettoRuolo.model());
			String tableName2 = this.getSoggettoRuoloFieldConverter().toAliasTable(SoggettoRuolo.model().ID_RUOLO);
			sqlQueryObject.addWhereCondition(tableName1+".id_ruolo="+tableName2+".id");
		}
		if(expression.inUseModel(SoggettoRuolo.model().ID_SOGGETTO,false)){
			String tableName1 = this.getSoggettoRuoloFieldConverter().toAliasTable(SoggettoRuolo.model());
			String tableName2 = this.getSoggettoRuoloFieldConverter().toAliasTable(SoggettoRuolo.model().ID_SOGGETTO);
			sqlQueryObject.addWhereCondition(tableName1+".id_soggetto="+tableName2+".id");
		}
	
	}
	
	protected java.util.List<Object> _getRootTablePrimaryKeyValues(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdSoggettoRuolo id) throws NotFoundException, ServiceException, NotImplementedException, Exception{
	    // Identificativi
        java.util.List<Object> rootTableIdValues = new java.util.ArrayList<Object>();
        Long longId = this.findIdSoggettoRuolo(jdbcProperties, log, connection, sqlQueryObject.newSQLQueryObject(), id, true);
		rootTableIdValues.add(longId);
        return rootTableIdValues;
	}
	
	protected Map<String, List<IField>> _getMapTableToPKColumn() throws NotImplementedException, Exception{
	
		SoggettoRuoloFieldConverter converter = this.getSoggettoRuoloFieldConverter();
		Map<String, List<IField>> mapTableToPKColumn = new java.util.HashMap<String, List<IField>>();
		UtilsTemplate<IField> utilities = new UtilsTemplate<IField>();

		// SoggettoRuolo.model()
		mapTableToPKColumn.put(converter.toTable(SoggettoRuolo.model()),
			utilities.newList(
				new CustomField("id", Long.class, "id", converter.toTable(SoggettoRuolo.model()))
			));

		// SoggettoRuolo.model().ID_RUOLO
		mapTableToPKColumn.put(converter.toTable(SoggettoRuolo.model().ID_RUOLO),
			utilities.newList(
				new CustomField("id", Long.class, "id", converter.toTable(SoggettoRuolo.model().ID_RUOLO))
			));

		// SoggettoRuolo.model().ID_SOGGETTO
		mapTableToPKColumn.put(converter.toTable(SoggettoRuolo.model().ID_SOGGETTO),
			utilities.newList(
				new CustomField("id", Long.class, "id", converter.toTable(SoggettoRuolo.model().ID_SOGGETTO))
			));

        return mapTableToPKColumn;		
	}
	
	@Override
	public List<Long> findAllTableIds(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCPaginatedExpression paginatedExpression) throws ServiceException, NotImplementedException, Exception {
		
		List<Long> list = new ArrayList<Long>();

		sqlQueryObject.setSelectDistinct(true);
		sqlQueryObject.setANDLogicOperator(true);
		sqlQueryObject.addSelectField(this.getSoggettoRuoloFieldConverter().toTable(SoggettoRuolo.model())+".id");
		Class<?> objectIdClass = Long.class;
		
		List<Object> listaQuery = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.prepareFindAll(jdbcProperties, log, connection, sqlQueryObject, paginatedExpression,
												this.getSoggettoRuoloFieldConverter(), SoggettoRuolo.model());
		
		_join(paginatedExpression,sqlQueryObject);
		
		List<Object> listObjects = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.findAll(jdbcProperties, log, connection, sqlQueryObject, paginatedExpression,
																			this.getSoggettoRuoloFieldConverter(), SoggettoRuolo.model(), objectIdClass, listaQuery);
		for(Object object: listObjects) {
			list.add((Long)object);
		}

        return list;
		
	}
	
	@Override
	public long findTableId(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCExpression expression) throws ServiceException, NotFoundException, MultipleResultException, NotImplementedException, Exception {
	
		sqlQueryObject.setSelectDistinct(true);
		sqlQueryObject.setANDLogicOperator(true);
		sqlQueryObject.addSelectField(this.getSoggettoRuoloFieldConverter().toTable(SoggettoRuolo.model())+".id");
		Class<?> objectIdClass = Long.class;
		
		List<Object> listaQuery = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.prepareFind(jdbcProperties, log, connection, sqlQueryObject, expression,
												this.getSoggettoRuoloFieldConverter(), SoggettoRuolo.model());
		
		_join(expression,sqlQueryObject);

		Object res = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.find(jdbcProperties, log, connection, sqlQueryObject, expression,
														this.getSoggettoRuoloFieldConverter(), SoggettoRuolo.model(), objectIdClass, listaQuery);
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
	public IdSoggettoRuolo findId(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, boolean throwNotFound)
			throws NotFoundException, ServiceException, NotImplementedException, Exception {
		
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);

		ISQLQueryObject sqlQueryObjectGet = sqlQueryObject.newSQLQueryObject();

		// Object _soggettoRuolo
		sqlQueryObjectGet.addFromTable(this.getSoggettoRuoloFieldConverter().toTable(SoggettoRuolo.model()));
		sqlQueryObjectGet.addSelectField("id_soggetto");
		sqlQueryObjectGet.addSelectField("id_ruolo");
		sqlQueryObjectGet.setANDLogicOperator(true);
		sqlQueryObjectGet.addWhereCondition("id=?");

		// Recupero _soggettoRuolo
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject [] searchParams_soggettoRuolo = new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject [] { 
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(tableId,Long.class)
		};
		List<Class<?>> listaFieldIdReturnType_soggettoRuolo = new ArrayList<Class<?>>();
		listaFieldIdReturnType_soggettoRuolo.add(Long.class);
		listaFieldIdReturnType_soggettoRuolo.add(Long.class);
		org.openspcoop2.core.commons.search.IdSoggettoRuolo id_soggettoRuolo = null;
		List<Object> listaFieldId_soggettoRuolo = jdbcUtilities.executeQuerySingleResult(sqlQueryObjectGet.createSQLQuery(), jdbcProperties.isShowSql(),
				listaFieldIdReturnType_soggettoRuolo, searchParams_soggettoRuolo);
		if(listaFieldId_soggettoRuolo==null || listaFieldId_soggettoRuolo.size()<=0){
			if(throwNotFound){
				throw new NotFoundException("Not Found");
			}
		}
		else{
			// set _soggettoRuolo
			id_soggettoRuolo = new org.openspcoop2.core.commons.search.IdSoggettoRuolo();
			
			IdSoggetto idSoggetto = new IdSoggetto();
			idSoggetto.setId((Long) listaFieldId_soggettoRuolo.get(0));
			Soggetto soggetto = ((IDBSoggettoServiceSearch)this.getServiceManager(connection, jdbcProperties, log).getSoggettoServiceSearch()).get(idSoggetto.getId());
			idSoggetto.setTipo(soggetto.getTipoSoggetto());
			idSoggetto.setNome(soggetto.getNomeSoggetto());
			id_soggettoRuolo.setIdSoggetto(idSoggetto);
			
			IdRuolo idRuolo = new IdRuolo();
			idRuolo.setId((Long) listaFieldId_soggettoRuolo.get(1));
			Ruolo ruolo = ((IDBRuoloServiceSearch)this.getServiceManager(connection, jdbcProperties, log).getRuoloServiceSearch()).get(idRuolo.getId());
			idRuolo.setNome(ruolo.getNome());
			id_soggettoRuolo.setIdRuolo(idRuolo);
			
		}
		
		return id_soggettoRuolo;
		
	}

	@Override
	public Long findTableId(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdSoggettoRuolo id, boolean throwNotFound)
			throws NotFoundException, ServiceException, NotImplementedException, Exception {
	
		return this.findIdSoggettoRuolo(jdbcProperties,log,connection,sqlQueryObject,id,throwNotFound);
			
	}
	
	@Override
	public List<List<Object>> nativeQuery(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, 
											String sql,List<Class<?>> returnClassTypes,Object ... param) throws ServiceException,NotFoundException,NotImplementedException,Exception{
		
		return org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.nativeQuery(jdbcProperties, log, connection, sqlQueryObject,
																							sql,returnClassTypes,param);
														
	}
	
	protected Long findIdSoggettoRuolo(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdSoggettoRuolo id, boolean throwNotFound) throws NotFoundException, ServiceException, NotImplementedException, Exception {

		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);

		ISQLQueryObject sqlQueryObjectGet = sqlQueryObject.newSQLQueryObject();

		
		// Object _soggettoRuolo
		sqlQueryObjectGet.addFromTable(this.getSoggettoRuoloFieldConverter().toTable(SoggettoRuolo.model()));
		sqlQueryObjectGet.addSelectField("id");
		// Devono essere mappati nella where condition i metodi dell'oggetto id.getXXX
		sqlQueryObjectGet.setANDLogicOperator(true);
		sqlQueryObjectGet.setSelectDistinct(true);
		sqlQueryObjectGet.addWhereCondition("id_soggetto=?");
		sqlQueryObjectGet.addWhereCondition("id_ruolo=?");

		long idSoggetto = ((IDBSoggettoServiceSearch)this.getServiceManager(connection, jdbcProperties, log).getSoggettoServiceSearch()).findTableId(id.getIdSoggetto(), true);
		long idRuolo = ((IDBRuoloServiceSearch)this.getServiceManager(connection, jdbcProperties, log).getRuoloServiceSearch()).findTableId(id.getIdRuolo(), true);
		
		// Recupero _soggettoRuolo
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject [] searchParams_soggettoRuolo = new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject [] { 
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(idSoggetto,long.class),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(idRuolo,long.class)
		};
		Long id_soggettoRuolo = null;
		try{
			id_soggettoRuolo = (Long) jdbcUtilities.executeQuerySingleResult(sqlQueryObjectGet.createSQLQuery(), jdbcProperties.isShowSql(),
						Long.class, searchParams_soggettoRuolo);
		}catch(NotFoundException notFound){
			if(throwNotFound){
				throw new NotFoundException(notFound);
			}
		}
		if(id_soggettoRuolo==null || id_soggettoRuolo<=0){
			if(throwNotFound){
				throw new NotFoundException("Not Found");
			}
		}
		
		return id_soggettoRuolo;
	}
}
