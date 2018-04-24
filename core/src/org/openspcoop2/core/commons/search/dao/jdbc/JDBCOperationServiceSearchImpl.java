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
import org.openspcoop2.core.commons.search.IdOperation;
import org.openspcoop2.core.commons.search.IdPortType;
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
import org.openspcoop2.core.commons.search.dao.jdbc.converter.OperationFieldConverter;
import org.openspcoop2.core.commons.search.dao.jdbc.fetch.OperationFetch;
import org.openspcoop2.core.commons.search.dao.IDBPortTypeServiceSearch;
import org.openspcoop2.core.commons.search.dao.jdbc.JDBCServiceManager;

import org.openspcoop2.core.commons.search.Operation;
import org.openspcoop2.core.commons.search.PortType;

/**     
 * JDBCOperationServiceSearchImpl
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JDBCOperationServiceSearchImpl implements IJDBCServiceSearchWithId<Operation, IdOperation, JDBCServiceManager> {

	private OperationFieldConverter _operationFieldConverter = null;
	public OperationFieldConverter getOperationFieldConverter() {
		if(this._operationFieldConverter==null){
			this._operationFieldConverter = new OperationFieldConverter(this.jdbcServiceManager.getJdbcProperties().getDatabaseType());
		}		
		return this._operationFieldConverter;
	}
	@Override
	public ISQLFieldConverter getFieldConverter() {
		return this.getOperationFieldConverter();
	}
	
	private OperationFetch operationFetch = new OperationFetch();
	public OperationFetch getOperationFetch() {
		return this.operationFetch;
	}
	@Override
	public IJDBCFetch getFetch() {
		return getOperationFetch();
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
	public IdOperation convertToId(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, Operation operation) throws NotImplementedException, ServiceException, Exception{
	
		IdOperation idOperation = new IdOperation();
		idOperation.setNome(operation.getNome());
        idOperation.setIdPortType(operation.getIdPortType());
        return idOperation;

	}
	
	@Override
	public Operation get(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdOperation id, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException, MultipleResultException, NotImplementedException, ServiceException,Exception {
		Long id_operation = ( (id!=null && id.getId()!=null && id.getId()>0) ? id.getId() : this.findIdOperation(jdbcProperties, log, connection, sqlQueryObject, id, true));
		return this._get(jdbcProperties, log, connection, sqlQueryObject, id_operation,idMappingResolutionBehaviour);
		
		
	}
	
	@Override
	public boolean exists(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdOperation id) throws MultipleResultException, NotImplementedException, ServiceException,Exception {

		Long id_operation = this.findIdOperation(jdbcProperties, log, connection, sqlQueryObject, id, false);
		return id_operation != null && id_operation > 0;
		
	}
	
	@Override
	public List<IdOperation> findAllIds(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCPaginatedExpression expression, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotImplementedException, ServiceException,Exception {

		List<IdOperation> list = new ArrayList<IdOperation>();

		// TODO: implementazione non efficente. 
		// Per ottenere una implementazione efficente:
		// 1. Usare metodo select di questa classe indirizzando esattamente i field necessari a create l'ID logico
		// 2. Usare metodo getOperationFetch() sul risultato della select per ottenere un oggetto Operation
		//	  La fetch con la map inserirà nell'oggetto solo i valori estratti 
		// 3. Usare metodo convertToId per ottenere l'id

        List<Long> ids = this.findAllTableIds(jdbcProperties, log, connection, sqlQueryObject, expression);
        
        for(Long id: ids) {
        	Operation operation = this.get(jdbcProperties, log, connection, sqlQueryObject, id, idMappingResolutionBehaviour);
			IdOperation idOperation = this.convertToId(jdbcProperties,log,connection,sqlQueryObject,operation);
        	list.add(idOperation);
        }

        return list;
		
	}
	
	@Override
	public List<Operation> findAll(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCPaginatedExpression expression, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotImplementedException, ServiceException,Exception {

        List<Operation> list = new ArrayList<Operation>();
        
        // TODO: implementazione non efficente. 
		// Per ottenere una implementazione efficente:
		// 1. Usare metodo select di questa classe indirizzando esattamente i field necessari
		// 2. Usare metodo getOperationFetch() sul risultato della select per ottenere un oggetto Operation
		//	  La fetch con la map inserirà nell'oggetto solo i valori estratti 

        List<Long> ids = this.findAllTableIds(jdbcProperties, log, connection, sqlQueryObject, expression);
        
        for(Long id: ids) {
        	list.add(this.get(jdbcProperties, log, connection, sqlQueryObject, id, idMappingResolutionBehaviour));
        }

        return list;      
		
	}
	
	@Override
	public Operation find(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCExpression expression, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) 
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
												this.getOperationFieldConverter(), Operation.model());
		
		sqlQueryObject.addSelectCountField(this.getOperationFieldConverter().toTable(Operation.model())+".id","tot",true);
		
		_join(expression,sqlQueryObject);
		
		return org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.count(jdbcProperties, log, connection, sqlQueryObject, expression,
																			this.getOperationFieldConverter(), Operation.model(),listaQuery);
	}

	@Override
	public InUse inUse(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdOperation id) throws NotFoundException, NotImplementedException, ServiceException,Exception {
		
		Long id_operation = this.findIdOperation(jdbcProperties, log, connection, sqlQueryObject, id, true);
        return this._inUse(jdbcProperties, log, connection, sqlQueryObject, id_operation);
		
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
												this.getOperationFieldConverter(), field);

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
        						expression, this.getOperationFieldConverter(), Operation.model(), 
        						listaQuery,listaParams);
		
		_join(expression,sqlQueryObject);
        
        List<Map<String,Object>> list = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.select(jdbcProperties, log, connection,
        								org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.prepareSqlQueryObjectForSelectDistinct(sqlQueryObject,sqlQueryObjectDistinct), 
        								expression, this.getOperationFieldConverter(), Operation.model(),
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
        						this.getOperationFieldConverter(), Operation.model(), 
        						sqlQueryObjectInnerList, jdbcObjects, union, unionExpression);
		
		if(unionExpression!=null){
			for (int i = 0; i < unionExpression.length; i++) {
				UnionExpression ue = unionExpression[i];
				IExpression expression = ue.getExpression();
				_join(expression,sqlQueryObjectInnerList.get(i));
			}
		}
        
        List<Map<String,Object>> list = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.union(jdbcProperties, log, connection, sqlQueryObject, 
        								this.getOperationFieldConverter(), Operation.model(), 
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
        						this.getOperationFieldConverter(), Operation.model(), 
        						sqlQueryObjectInnerList, jdbcObjects, union, unionExpression);
		
		if(unionExpression!=null){
			for (int i = 0; i < unionExpression.length; i++) {
				UnionExpression ue = unionExpression[i];
				IExpression expression = ue.getExpression();
				_join(expression,sqlQueryObjectInnerList.get(i));
			}
		}
        
        NonNegativeNumber number = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.unionCount(jdbcProperties, log, connection, sqlQueryObject, 
        								this.getOperationFieldConverter(), Operation.model(), 
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
			return new JDBCExpression(this.getOperationFieldConverter());
		}catch(Exception e){
			throw new ServiceException(e);
		}
	}


	@Override
	public JDBCPaginatedExpression newPaginatedExpression(Logger log) throws NotImplementedException, ServiceException {
		try{
			return new JDBCPaginatedExpression(this.getOperationFieldConverter());
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
	public void mappingTableIds(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdOperation id, Operation obj) throws NotFoundException,NotImplementedException,ServiceException,Exception{
		_mappingTableIds(jdbcProperties,log,connection,sqlQueryObject,obj,
				this.get(jdbcProperties,log,connection,sqlQueryObject,id,null));
	}
	
	@Override
	public void mappingTableIds(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, Operation obj) throws NotFoundException,NotImplementedException,ServiceException,Exception{
		_mappingTableIds(jdbcProperties,log,connection,sqlQueryObject,obj,
				this.get(jdbcProperties,log,connection,sqlQueryObject,tableId,null));
	}
	private void _mappingTableIds(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, Operation obj, Operation imgSaved) throws NotFoundException,NotImplementedException,ServiceException,Exception{
		if(imgSaved==null){
			return;
		}
		obj.setId(imgSaved.getId());
		if(obj.getIdPortType()!=null && 
				imgSaved.getIdPortType()!=null){
			obj.getIdPortType().setId(imgSaved.getIdPortType().getId());
			if(obj.getIdPortType().getIdAccordoServizioParteComune()!=null && 
					imgSaved.getIdPortType().getIdAccordoServizioParteComune()!=null){
				obj.getIdPortType().getIdAccordoServizioParteComune().setId(imgSaved.getIdPortType().getIdAccordoServizioParteComune().getId());
				if(obj.getIdPortType().getIdAccordoServizioParteComune().getIdSoggetto()!=null && 
						imgSaved.getIdPortType().getIdAccordoServizioParteComune().getIdSoggetto()!=null){
					obj.getIdPortType().getIdAccordoServizioParteComune().getIdSoggetto().setId(imgSaved.getIdPortType().getIdAccordoServizioParteComune().getIdSoggetto().getId());
				}
			}
		}

	}
	
	@Override
	public Operation get(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException, MultipleResultException, NotImplementedException, ServiceException, Exception {
		return this._get(jdbcProperties, log, connection, sqlQueryObject, Long.valueOf(tableId), idMappingResolutionBehaviour);
	}
	
	private Operation _get(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, Long tableId, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException, MultipleResultException, NotImplementedException, ServiceException, Exception {
	
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);
		
		ISQLQueryObject sqlQueryObjectGet = sqlQueryObject.newSQLQueryObject();
				
		Operation operation = new Operation();
		

		// Object operation
		ISQLQueryObject sqlQueryObjectGet_operation = sqlQueryObjectGet.newSQLQueryObject();
		sqlQueryObjectGet_operation.setANDLogicOperator(true);
		sqlQueryObjectGet_operation.addFromTable(this.getOperationFieldConverter().toTable(Operation.model()));
		sqlQueryObjectGet_operation.addSelectField("id");
		sqlQueryObjectGet_operation.addSelectField(this.getOperationFieldConverter().toColumn(Operation.model().NOME,true));
		sqlQueryObjectGet_operation.addWhereCondition("id=?");

		// Get operation
		operation = (Operation) jdbcUtilities.executeQuerySingleResult(sqlQueryObjectGet_operation.createSQLQuery(), jdbcProperties.isShowSql(), Operation.model(), this.getOperationFetch(),
			new JDBCObject(tableId,Long.class));

		
		// Recupero idPortType
		ISQLQueryObject sqlQueryObjectGet_accordoServizioParteComune_pt = sqlQueryObjectGet.newSQLQueryObject();
		sqlQueryObjectGet_accordoServizioParteComune_pt.addFromTable(this.getOperationFieldConverter().toTable(Operation.model()));
		sqlQueryObjectGet_accordoServizioParteComune_pt.addSelectField("id_port_type");
		sqlQueryObjectGet_accordoServizioParteComune_pt.setANDLogicOperator(true);
		sqlQueryObjectGet_accordoServizioParteComune_pt.addWhereCondition("id=?");
		
		// Recupero _accordoServizioParteComune_soggetto
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject [] searchParams_accordoServizioParteComune_pt = new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject [] { 
				new JDBCObject(operation.getId(), Long.class)
		};
		Long id_accordoServizioParteComune_pt = 
			(Long) jdbcUtilities.executeQuerySingleResult(sqlQueryObjectGet_accordoServizioParteComune_pt.createSQLQuery(), jdbcProperties.isShowSql(),
			Long.class, searchParams_accordoServizioParteComune_pt);
		
		PortType pt = ((IDBPortTypeServiceSearch)this.getServiceManager().getPortTypeServiceSearch()).get(id_accordoServizioParteComune_pt);
		IdPortType idPt = new IdPortType();
		idPt.setNome(pt.getNome());
		idPt.setIdAccordoServizioParteComune(pt.getIdAccordoServizioParteComune());
		operation.setIdPortType(idPt);
		
        return operation;  
	
	} 
	
	@Override
	public boolean exists(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId) throws MultipleResultException, NotImplementedException, ServiceException, Exception {
		return this._exists(jdbcProperties, log, connection, sqlQueryObject, Long.valueOf(tableId));
	}
	
	private boolean _exists(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, Long tableId) throws MultipleResultException, NotImplementedException, ServiceException, Exception {
	
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);
				
		boolean existsOperation = false;

		sqlQueryObject = sqlQueryObject.newSQLQueryObject();
		sqlQueryObject.setANDLogicOperator(true);

		sqlQueryObject.addFromTable(this.getOperationFieldConverter().toTable(Operation.model()));
		sqlQueryObject.addSelectField(this.getOperationFieldConverter().toColumn(Operation.model().NOME,true));
		sqlQueryObject.addWhereCondition("id=?");


		// Exists operation
		existsOperation = jdbcUtilities.exists(sqlQueryObject.createSQLQuery(), jdbcProperties.isShowSql(),
			new JDBCObject(tableId,Long.class));

		
        return existsOperation;
	
	}
	
	private void _join(IExpression expression, ISQLQueryObject sqlQueryObject) throws NotImplementedException, ServiceException, Exception{
	
		if(expression.inUseModel(Operation.model().ID_PORT_TYPE,false)){
			String tableName1 = this.getOperationFieldConverter().toTable(Operation.model());
			String tableName2 = this.getOperationFieldConverter().toTable(Operation.model().ID_PORT_TYPE);
			sqlQueryObject.addWhereCondition(tableName1+".id_port_type="+tableName2+".id");
		}
		if(expression.inUseModel(Operation.model().ID_PORT_TYPE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE,false)){
			String tableName1 = this.getOperationFieldConverter().toTable(Operation.model().ID_PORT_TYPE);
			String tableName2 = this.getOperationFieldConverter().toTable(Operation.model().ID_PORT_TYPE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE);
			sqlQueryObject.addWhereCondition(tableName1+".id_accordo="+tableName2+".id");
		}
		if(expression.inUseModel(Operation.model().ID_PORT_TYPE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO,false)){
			String tableName1 = this.getOperationFieldConverter().toTable(Operation.model().ID_PORT_TYPE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE);
			String tableName2 = this.getOperationFieldConverter().toTable(Operation.model().ID_PORT_TYPE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO);
			sqlQueryObject.addWhereCondition(tableName1+".id_referente="+tableName2+".id");
		}
		
		// Check FROM Table necessarie per le join di oggetti annidati dal secondo livello in poi dove pero' non viene poi utilizzato l'oggetto del livello precedente nella espressione
		if(expression.inUseModel(Operation.model().ID_PORT_TYPE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE,false)){
			if(expression.inUseModel(Operation.model().ID_PORT_TYPE,false)==false){
				sqlQueryObject.addFromTable(this.getOperationFieldConverter().toTable(Operation.model().ID_PORT_TYPE));
			}
		}
		if(expression.inUseModel(Operation.model().ID_PORT_TYPE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO,false)){
			if(expression.inUseModel(Operation.model().ID_PORT_TYPE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE,false)==false){
				sqlQueryObject.addFromTable(this.getOperationFieldConverter().toTable(Operation.model().ID_PORT_TYPE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE));
			}
		}
        
	}
	
	protected java.util.List<Object> _getRootTablePrimaryKeyValues(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdOperation id) throws NotFoundException, ServiceException, NotImplementedException, Exception{
	    // Identificativi
        java.util.List<Object> rootTableIdValues = new java.util.ArrayList<Object>();
        Long longId = this.findIdOperation(jdbcProperties, log, connection, sqlQueryObject.newSQLQueryObject(), id, true);
		rootTableIdValues.add(longId);
        return rootTableIdValues;
	}
	
	protected Map<String, List<IField>> _getMapTableToPKColumn() throws NotImplementedException, Exception{
	
		OperationFieldConverter converter = this.getOperationFieldConverter();
		Map<String, List<IField>> mapTableToPKColumn = new java.util.Hashtable<String, List<IField>>();
		UtilsTemplate<IField> utilities = new UtilsTemplate<IField>();

		// Operation.model()
		mapTableToPKColumn.put(converter.toTable(Operation.model()),
			utilities.newList(
				new CustomField("id", Long.class, "id", converter.toTable(Operation.model()))
			));

		// Operation.model().ID_PORT_TYPE
		mapTableToPKColumn.put(converter.toTable(Operation.model().ID_PORT_TYPE),
			utilities.newList(
				new CustomField("id", Long.class, "id", converter.toTable(Operation.model().ID_PORT_TYPE))
			));

		// Operation.model().ID_PORT_TYPE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE
		mapTableToPKColumn.put(converter.toTable(Operation.model().ID_PORT_TYPE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE),
			utilities.newList(
				new CustomField("id", Long.class, "id", converter.toTable(Operation.model().ID_PORT_TYPE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE))
			));

		// Operation.model().ID_PORT_TYPE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO
		mapTableToPKColumn.put(converter.toTable(Operation.model().ID_PORT_TYPE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO),
			utilities.newList(
				new CustomField("id", Long.class, "id", converter.toTable(Operation.model().ID_PORT_TYPE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO))
			));

        return mapTableToPKColumn;		
	}
	
	@Override
	public List<Long> findAllTableIds(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCPaginatedExpression paginatedExpression) throws ServiceException, NotImplementedException, Exception {
		
		List<Long> list = new ArrayList<Long>();

		sqlQueryObject.setSelectDistinct(true);
		sqlQueryObject.setANDLogicOperator(true);
		sqlQueryObject.addSelectField(this.getOperationFieldConverter().toTable(Operation.model())+".id");
		Class<?> objectIdClass = Long.class;
		
		List<Object> listaQuery = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.prepareFindAll(jdbcProperties, log, connection, sqlQueryObject, paginatedExpression,
												this.getOperationFieldConverter(), Operation.model());
		
		_join(paginatedExpression,sqlQueryObject);
		
		List<Object> listObjects = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.findAll(jdbcProperties, log, connection, sqlQueryObject, paginatedExpression,
																			this.getOperationFieldConverter(), Operation.model(), objectIdClass, listaQuery);
		for(Object object: listObjects) {
			list.add((Long)object);
		}

        return list;
		
	}
	
	@Override
	public long findTableId(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCExpression expression) throws ServiceException, NotFoundException, MultipleResultException, NotImplementedException, Exception {
	
		sqlQueryObject.setSelectDistinct(true);
		sqlQueryObject.setANDLogicOperator(true);
		sqlQueryObject.addSelectField(this.getOperationFieldConverter().toTable(Operation.model())+".id");
		Class<?> objectIdClass = Long.class;
		
		List<Object> listaQuery = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.prepareFind(jdbcProperties, log, connection, sqlQueryObject, expression,
												this.getOperationFieldConverter(), Operation.model());
		
		_join(expression,sqlQueryObject);

		Object res = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.find(jdbcProperties, log, connection, sqlQueryObject, expression,
														this.getOperationFieldConverter(), Operation.model(), objectIdClass, listaQuery);
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
	public IdOperation findId(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, boolean throwNotFound)
			throws NotFoundException, ServiceException, NotImplementedException, Exception {
		
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);

		ISQLQueryObject sqlQueryObjectGet = sqlQueryObject.newSQLQueryObject();              

		// Object _operation
		sqlQueryObjectGet.addFromTable(this.getOperationFieldConverter().toTable(Operation.model()));
		sqlQueryObjectGet.addSelectField(this.getOperationFieldConverter().toColumn(Operation.model().NOME,true));
		sqlQueryObjectGet.addSelectField("id_port_type");
		sqlQueryObjectGet.setANDLogicOperator(true);
		sqlQueryObjectGet.addWhereCondition("id=?");

		// Recupero _operation
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject [] searchParams_operation = new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject [] { 
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(tableId,Long.class)
		};
		List<Class<?>> listaFieldIdReturnType_operation = new ArrayList<Class<?>>();
		listaFieldIdReturnType_operation.add(String.class);
		listaFieldIdReturnType_operation.add(Long.class);
		org.openspcoop2.core.commons.search.IdOperation id_operation = null;
		List<Object> listaFieldId_operation = jdbcUtilities.executeQuerySingleResult(sqlQueryObjectGet.createSQLQuery(), jdbcProperties.isShowSql(),
				listaFieldIdReturnType_operation, searchParams_operation);
		if(listaFieldId_operation==null || listaFieldId_operation.size()<=0){
			if(throwNotFound){
				throw new NotFoundException("Not Found");
			}
		}
		else{
			// set _operation
			id_operation = new org.openspcoop2.core.commons.search.IdOperation();
			id_operation.setNome((String)listaFieldId_operation.get(0));
			
			Long idPortTypeFK = (Long) listaFieldId_operation.get(1);
			id_operation.
				setIdPortType(((IDBPortTypeServiceSearch)this.getServiceManager().
						getPortTypeServiceSearch()).findId(idPortTypeFK, true));
		}
		
		return id_operation;
		
	}

	@Override
	public Long findTableId(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdOperation id, boolean throwNotFound)
			throws NotFoundException, ServiceException, NotImplementedException, Exception {
	
		return this.findIdOperation(jdbcProperties,log,connection,sqlQueryObject,id,throwNotFound);
			
	}
	
	@Override
	public List<List<Object>> nativeQuery(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, 
											String sql,List<Class<?>> returnClassTypes,Object ... param) throws ServiceException,NotFoundException,NotImplementedException,Exception{
		
		return org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.nativeQuery(jdbcProperties, log, connection, sqlQueryObject,
																							sql,returnClassTypes,param);
														
	}
	
	protected Long findIdOperation(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdOperation id, boolean throwNotFound) throws NotFoundException, ServiceException, NotImplementedException, Exception {

		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);

		ISQLQueryObject sqlQueryObjectGet = sqlQueryObject.newSQLQueryObject();

		if(id.getIdPortType()==null){
			throw new ServiceException("IdPortType non definito");
		}
		if(id.getIdPortType().getIdAccordoServizioParteComune()==null){
			throw new ServiceException("IdPortType.getIdAccordoServizioParteComune non definito");
		}
		if(id.getIdPortType().getIdAccordoServizioParteComune().getIdSoggetto()==null){
			throw new ServiceException("IdPortType.getIdAccordoServizioParteComune.getIdSoggetto non definito");
		}
		
		
		// Recupero idPortType
		PortType pt = this.getServiceManager().getPortTypeServiceSearch().get(id.getIdPortType());
		
		
		// Object _operation
		sqlQueryObjectGet.addFromTable(this.getOperationFieldConverter().toTable(Operation.model()));
		sqlQueryObjectGet.addSelectField("id");
		sqlQueryObjectGet.setANDLogicOperator(true);
		sqlQueryObjectGet.setSelectDistinct(true);
		sqlQueryObjectGet.addWhereCondition(this.getOperationFieldConverter().toColumn(Operation.model().NOME,true)+"=?");
		sqlQueryObjectGet.addWhereCondition("id_port_type=?");

		// Recupero _operation
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject [] searchParams_operation = new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject [] { 
				new JDBCObject(id.getNome(), String.class),
				new JDBCObject(pt.getId(), Long.class)
		};
		Long id_operation = null;
		try{
			id_operation = (Long) jdbcUtilities.executeQuerySingleResult(sqlQueryObjectGet.createSQLQuery(), jdbcProperties.isShowSql(),
						Long.class, searchParams_operation);
		}catch(NotFoundException notFound){
			if(throwNotFound){
				throw new NotFoundException(notFound);
			}
		}
		if(id_operation==null || id_operation<=0){
			if(throwNotFound){
				throw new NotFoundException("Not Found");
			}
		}
		
		return id_operation;
	}
}
