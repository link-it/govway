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
import org.openspcoop2.core.commons.search.IdFruitore;
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
import org.openspcoop2.core.commons.search.dao.jdbc.converter.FruitoreFieldConverter;
import org.openspcoop2.core.commons.search.dao.jdbc.fetch.FruitoreFetch;
import org.openspcoop2.core.commons.search.dao.IDBAccordoServizioParteSpecificaServiceSearch;
import org.openspcoop2.core.commons.search.dao.IDBSoggettoServiceSearch;
import org.openspcoop2.core.commons.search.Fruitore;
import org.openspcoop2.core.commons.search.IdAccordoServizioParteSpecifica;

/**     
 * JDBCFruitoreServiceSearchImpl
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JDBCFruitoreServiceSearchImpl implements IJDBCServiceSearchWithId<Fruitore, IdFruitore, JDBCServiceManager> {

	private FruitoreFieldConverter _fruitoreFieldConverter = null;
	public FruitoreFieldConverter getFruitoreFieldConverter() {
		if(this._fruitoreFieldConverter==null){
			this._fruitoreFieldConverter = new FruitoreFieldConverter(this.jdbcServiceManager.getJdbcProperties().getDatabaseType());
		}		
		return this._fruitoreFieldConverter;
	}
	@Override
	public ISQLFieldConverter getFieldConverter() {
		return this.getFruitoreFieldConverter();
	}
	
	private FruitoreFetch fruitoreFetch = new FruitoreFetch();
	public FruitoreFetch getFruitoreFetch() {
		return this.fruitoreFetch;
	}
	@Override
	public IJDBCFetch getFetch() {
		return getFruitoreFetch();
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
	public IdFruitore convertToId(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, Fruitore fruitore) throws NotImplementedException, ServiceException, Exception{
	
		IdFruitore idFruitore = new IdFruitore();
		idFruitore.setIdAccordoServizioParteSpecifica(fruitore.getIdAccordoServizioParteSpecifica());
        idFruitore.setIdFruitore(fruitore.getIdFruitore());
        return idFruitore;
	}
	
	@Override
	public Fruitore get(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdFruitore id, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException, MultipleResultException, NotImplementedException, ServiceException,Exception {
		Long id_fruitore = ( (id!=null && id.getId()!=null && id.getId()>0) ? id.getId() : this.findIdFruitore(jdbcProperties, log, connection, sqlQueryObject, id, true));
		return this._get(jdbcProperties, log, connection, sqlQueryObject, id_fruitore,idMappingResolutionBehaviour);
		
		
	}
	
	@Override
	public boolean exists(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdFruitore id) throws MultipleResultException, NotImplementedException, ServiceException,Exception {

		Long id_fruitore = this.findIdFruitore(jdbcProperties, log, connection, sqlQueryObject, id, false);
		return id_fruitore != null && id_fruitore > 0;
		
	}
	
	@Override
	public List<IdFruitore> findAllIds(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCPaginatedExpression expression, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotImplementedException, ServiceException,Exception {

		List<IdFruitore> list = new ArrayList<IdFruitore>();

		// TODO: implementazione non efficente. 
		// Per ottenere una implementazione efficente:
		// 1. Usare metodo select di questa classe indirizzando esattamente i field necessari a create l'ID logico
		// 2. Usare metodo getFruitoreFetch() sul risultato della select per ottenere un oggetto Fruitore
		//	  La fetch con la map inserirà nell'oggetto solo i valori estratti 
		// 3. Usare metodo convertToId per ottenere l'id

        List<Long> ids = this.findAllTableIds(jdbcProperties, log, connection, sqlQueryObject, expression);
        
        for(Long id: ids) {
        	Fruitore fruitore = this.get(jdbcProperties, log, connection, sqlQueryObject, id, idMappingResolutionBehaviour);
			IdFruitore idFruitore = this.convertToId(jdbcProperties,log,connection,sqlQueryObject,fruitore);
        	list.add(idFruitore);
        }

        return list;
		
	}
	
	@Override
	public List<Fruitore> findAll(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCPaginatedExpression expression, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotImplementedException, ServiceException,Exception {

        List<Fruitore> list = new ArrayList<Fruitore>();
        
        // TODO: implementazione non efficente. 
		// Per ottenere una implementazione efficente:
		// 1. Usare metodo select di questa classe indirizzando esattamente i field necessari
		// 2. Usare metodo getFruitoreFetch() sul risultato della select per ottenere un oggetto Fruitore
		//	  La fetch con la map inserirà nell'oggetto solo i valori estratti 

        List<Long> ids = this.findAllTableIds(jdbcProperties, log, connection, sqlQueryObject, expression);
        
        for(Long id: ids) {
        	list.add(this.get(jdbcProperties, log, connection, sqlQueryObject, id, idMappingResolutionBehaviour));
        }

        return list;      
		
	}
	
	@Override
	public Fruitore find(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCExpression expression, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) 
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
												this.getFruitoreFieldConverter(), Fruitore.model());
		
		sqlQueryObject.addSelectCountField(this.getFruitoreFieldConverter().toTable(Fruitore.model())+".id","tot",true);
		
		_join(expression,sqlQueryObject);
		
		return org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.count(jdbcProperties, log, connection, sqlQueryObject, expression,
																			this.getFruitoreFieldConverter(), Fruitore.model(),listaQuery);
	}

	@Override
	public InUse inUse(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdFruitore id) throws NotFoundException, NotImplementedException, ServiceException,Exception {
		
		Long id_fruitore = this.findIdFruitore(jdbcProperties, log, connection, sqlQueryObject, id, true);
        return this._inUse(jdbcProperties, log, connection, sqlQueryObject, id_fruitore);
		
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
												this.getFruitoreFieldConverter(), field);

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
        						expression, this.getFruitoreFieldConverter(), Fruitore.model(), 
        						listaQuery,listaParams);
		
		_join(expression,sqlQueryObject);
        
        List<Map<String,Object>> list = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.select(jdbcProperties, log, connection,
        								org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.prepareSqlQueryObjectForSelectDistinct(sqlQueryObject,sqlQueryObjectDistinct), 
        								expression, this.getFruitoreFieldConverter(), Fruitore.model(),
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
        						this.getFruitoreFieldConverter(), Fruitore.model(), 
        						sqlQueryObjectInnerList, jdbcObjects, union, unionExpression);
		
		if(unionExpression!=null){
			for (int i = 0; i < unionExpression.length; i++) {
				UnionExpression ue = unionExpression[i];
				IExpression expression = ue.getExpression();
				_join(expression,sqlQueryObjectInnerList.get(i));
			}
		}
        
        List<Map<String,Object>> list = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.union(jdbcProperties, log, connection, sqlQueryObject, 
        								this.getFruitoreFieldConverter(), Fruitore.model(), 
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
        						this.getFruitoreFieldConverter(), Fruitore.model(), 
        						sqlQueryObjectInnerList, jdbcObjects, union, unionExpression);
		
		if(unionExpression!=null){
			for (int i = 0; i < unionExpression.length; i++) {
				UnionExpression ue = unionExpression[i];
				IExpression expression = ue.getExpression();
				_join(expression,sqlQueryObjectInnerList.get(i));
			}
		}
        
        NonNegativeNumber number = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.unionCount(jdbcProperties, log, connection, sqlQueryObject, 
        								this.getFruitoreFieldConverter(), Fruitore.model(), 
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
			return new JDBCExpression(this.getFruitoreFieldConverter());
		}catch(Exception e){
			throw new ServiceException(e);
		}
	}


	@Override
	public JDBCPaginatedExpression newPaginatedExpression(Logger log) throws NotImplementedException, ServiceException {
		try{
			return new JDBCPaginatedExpression(this.getFruitoreFieldConverter());
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
	public void mappingTableIds(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdFruitore id, Fruitore obj) throws NotFoundException,NotImplementedException,ServiceException,Exception{
		_mappingTableIds(jdbcProperties,log,connection,sqlQueryObject,obj,
				this.get(jdbcProperties,log,connection,sqlQueryObject,id,null));
	}
	
	@Override
	public void mappingTableIds(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, Fruitore obj) throws NotFoundException,NotImplementedException,ServiceException,Exception{
		_mappingTableIds(jdbcProperties,log,connection,sqlQueryObject,obj,
				this.get(jdbcProperties,log,connection,sqlQueryObject,tableId,null));
	}
	private void _mappingTableIds(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, Fruitore obj, Fruitore imgSaved) throws NotFoundException,NotImplementedException,ServiceException,Exception{
		if(imgSaved==null){
			return;
		}
		obj.setId(imgSaved.getId());
		if(obj.getIdFruitore()!=null && 
				imgSaved.getIdFruitore()!=null){
			obj.getIdFruitore().setId(imgSaved.getIdFruitore().getId());
		}
		if(obj.getIdAccordoServizioParteSpecifica()!=null && 
				imgSaved.getIdAccordoServizioParteSpecifica()!=null){
			obj.getIdAccordoServizioParteSpecifica().setId(imgSaved.getIdAccordoServizioParteSpecifica().getId());
			if(obj.getIdAccordoServizioParteSpecifica().getIdErogatore()!=null && 
					imgSaved.getIdAccordoServizioParteSpecifica().getIdErogatore()!=null){
				obj.getIdAccordoServizioParteSpecifica().getIdErogatore().setId(imgSaved.getIdAccordoServizioParteSpecifica().getIdErogatore().getId());
			}
		}

	}
	
	@Override
	public Fruitore get(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException, MultipleResultException, NotImplementedException, ServiceException, Exception {
		return this._get(jdbcProperties, log, connection, sqlQueryObject, Long.valueOf(tableId), idMappingResolutionBehaviour);
	}
	
	private Fruitore _get(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, Long tableId, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException, MultipleResultException, NotImplementedException, ServiceException, Exception {
	
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);
		
		ISQLQueryObject sqlQueryObjectGet = sqlQueryObject.newSQLQueryObject();
				
		Fruitore fruitore = new Fruitore();
		

		// Object fruitore
		ISQLQueryObject sqlQueryObjectGet_fruitore = sqlQueryObjectGet.newSQLQueryObject();
		sqlQueryObjectGet_fruitore.setANDLogicOperator(true);
		sqlQueryObjectGet_fruitore.addFromTable(this.getFruitoreFieldConverter().toTable(Fruitore.model()));
		sqlQueryObjectGet_fruitore.addSelectField("id");
		sqlQueryObjectGet_fruitore.addSelectField(this.getFruitoreFieldConverter().toColumn(Fruitore.model().ORA_REGISTRAZIONE,true));
		sqlQueryObjectGet_fruitore.addWhereCondition("id=?");

		// Get fruitore
		fruitore = (Fruitore) jdbcUtilities.executeQuerySingleResult(sqlQueryObjectGet_fruitore.createSQLQuery(), jdbcProperties.isShowSql(), Fruitore.model(), this.getFruitoreFetch(),
			new JDBCObject(tableId,Long.class));


		// Object _fruitore_soggetto (recupero id)
		ISQLQueryObject sqlQueryObjectGet_fruitore_soggetto_readFkId = sqlQueryObjectGet.newSQLQueryObject();
		sqlQueryObjectGet_fruitore_soggetto_readFkId.addFromTable("servizi_fruitori");
		sqlQueryObjectGet_fruitore_soggetto_readFkId.addSelectField("id_soggetto");
		sqlQueryObjectGet_fruitore_soggetto_readFkId.addWhereCondition("id=?");
		sqlQueryObjectGet_fruitore_soggetto_readFkId.setANDLogicOperator(true);
		Long idFK_fruitore_soggetto = (Long) jdbcUtilities.executeQuerySingleResult(sqlQueryObjectGet_fruitore_soggetto_readFkId.createSQLQuery(), jdbcProperties.isShowSql(),Long.class,
				new JDBCObject(fruitore.getId(),Long.class));
		
		// Object _fruitore_soggetto
		ISQLQueryObject sqlQueryObjectGet_fruitore_soggetto = sqlQueryObjectGet.newSQLQueryObject();
		sqlQueryObjectGet_fruitore_soggetto.addFromTable("soggetti");
		sqlQueryObjectGet_fruitore_soggetto.addSelectField("tipo_soggetto");
		sqlQueryObjectGet_fruitore_soggetto.addSelectField("nome_soggetto");
		sqlQueryObjectGet_fruitore_soggetto.setANDLogicOperator(true);
		sqlQueryObjectGet_fruitore_soggetto.addWhereCondition("id=?");

		// Recupero _fruitore_soggetto
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject [] searchParams_fruitore_soggetto = new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject [] { 
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(idFK_fruitore_soggetto,Long.class)
		};
		List<Class<?>> listaFieldIdReturnType_fruitore_soggetto = new ArrayList<Class<?>>();
		listaFieldIdReturnType_fruitore_soggetto.add(String.class);
		listaFieldIdReturnType_fruitore_soggetto.add(String.class);
		List<Object> listaFieldId_fruitore_soggetto = jdbcUtilities.executeQuerySingleResult(sqlQueryObjectGet_fruitore_soggetto.createSQLQuery(), jdbcProperties.isShowSql(),
				listaFieldIdReturnType_fruitore_soggetto, searchParams_fruitore_soggetto);
		// set _fruitore_soggetto
		IdSoggetto id_fruitore_soggetto = new IdSoggetto();
		id_fruitore_soggetto.setTipo((String)listaFieldId_fruitore_soggetto.get(0));
		id_fruitore_soggetto.setNome((String)listaFieldId_fruitore_soggetto.get(1));
		fruitore.setIdFruitore(id_fruitore_soggetto);

		// Object _fruitore_accordoServizioParteSpecifica (recupero id)
		ISQLQueryObject sqlQueryObjectGet_fruitore_accordoServizioParteSpecifica_readFkId = sqlQueryObjectGet.newSQLQueryObject();
		sqlQueryObjectGet_fruitore_accordoServizioParteSpecifica_readFkId.addFromTable("servizi_fruitori");
		sqlQueryObjectGet_fruitore_accordoServizioParteSpecifica_readFkId.addSelectField("id_servizio");
		sqlQueryObjectGet_fruitore_accordoServizioParteSpecifica_readFkId.addWhereCondition("id=?");
		sqlQueryObjectGet_fruitore_accordoServizioParteSpecifica_readFkId.setANDLogicOperator(true);
		Long idFK_fruitore_accordoServizioParteSpecifica = (Long) jdbcUtilities.executeQuerySingleResult(sqlQueryObjectGet_fruitore_accordoServizioParteSpecifica_readFkId.createSQLQuery(), jdbcProperties.isShowSql(),Long.class,
				new JDBCObject(fruitore.getId(),Long.class));
		
		// Object _fruitore_accordoServizioParteSpecifica
		ISQLQueryObject sqlQueryObjectGet_fruitore_accordoServizioParteSpecifica = sqlQueryObjectGet.newSQLQueryObject();
		sqlQueryObjectGet_fruitore_accordoServizioParteSpecifica.addFromTable("servizi");
		sqlQueryObjectGet_fruitore_accordoServizioParteSpecifica.addSelectField("tipo_servizio");
		sqlQueryObjectGet_fruitore_accordoServizioParteSpecifica.addSelectField("nome_servizio");
		sqlQueryObjectGet_fruitore_accordoServizioParteSpecifica.addSelectField("id_soggetto");
		sqlQueryObjectGet_fruitore_accordoServizioParteSpecifica.setANDLogicOperator(true);
		sqlQueryObjectGet_fruitore_accordoServizioParteSpecifica.addWhereCondition("id=?");

		// Recupero _fruitore_accordoServizioParteSpecifica
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject [] searchParams_fruitore_accordoServizioParteSpecifica = new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject [] { 
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(idFK_fruitore_accordoServizioParteSpecifica,Long.class)
		};
		List<Class<?>> listaFieldIdReturnType_fruitore_accordoServizioParteSpecifica = new ArrayList<Class<?>>();
		listaFieldIdReturnType_fruitore_accordoServizioParteSpecifica.add(String.class);
		listaFieldIdReturnType_fruitore_accordoServizioParteSpecifica.add(String.class);
		listaFieldIdReturnType_fruitore_accordoServizioParteSpecifica.add(Long.class);
		List<Object> listaFieldId_fruitore_accordoServizioParteSpecifica = jdbcUtilities.executeQuerySingleResult(sqlQueryObjectGet_fruitore_accordoServizioParteSpecifica.createSQLQuery(), jdbcProperties.isShowSql(),
				listaFieldIdReturnType_fruitore_accordoServizioParteSpecifica, searchParams_fruitore_accordoServizioParteSpecifica);
		// set _fruitore_accordoServizioParteSpecifica
		IdAccordoServizioParteSpecifica id_fruitore_accordoServizioParteSpecifica = new IdAccordoServizioParteSpecifica();
		id_fruitore_accordoServizioParteSpecifica.setTipo((String)listaFieldId_fruitore_accordoServizioParteSpecifica.get(0));
		id_fruitore_accordoServizioParteSpecifica.setNome((String)listaFieldId_fruitore_accordoServizioParteSpecifica.get(1));
		
		// Recupero IdSoggettoAPS
		long idSoggettoAccordoParteSpecifica = (Long) listaFieldId_fruitore_accordoServizioParteSpecifica.get(2);
		
		// Recupero IdSoggettoAPS query
		ISQLQueryObject sqlQueryObjectGet_erogatore_soggetto = sqlQueryObjectGet.newSQLQueryObject();
		sqlQueryObjectGet_erogatore_soggetto.addFromTable("soggetti");
		sqlQueryObjectGet_erogatore_soggetto.addSelectField("tipo_soggetto");
		sqlQueryObjectGet_erogatore_soggetto.addSelectField("nome_soggetto");
		sqlQueryObjectGet_erogatore_soggetto.setANDLogicOperator(true);
		sqlQueryObjectGet_erogatore_soggetto.addWhereCondition("id=?");
		
		// Recupero IdSoggettoAPS execute
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject [] searchParams_erogatore_soggetto = new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject [] { 
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(idSoggettoAccordoParteSpecifica,Long.class)
		};
		List<Class<?>> listaFieldIdReturnType_erogatore_soggetto = new ArrayList<Class<?>>();
		listaFieldIdReturnType_erogatore_soggetto.add(String.class);
		listaFieldIdReturnType_erogatore_soggetto.add(String.class);
		List<Object> listaFieldId_erogatore_soggetto = jdbcUtilities.executeQuerySingleResult(sqlQueryObjectGet_fruitore_soggetto.createSQLQuery(), jdbcProperties.isShowSql(),
				listaFieldIdReturnType_erogatore_soggetto, searchParams_erogatore_soggetto);
		// set _fruitore_soggetto
		IdSoggetto id_erogatore_soggetto = new IdSoggetto();
		id_erogatore_soggetto.setTipo((String)listaFieldId_erogatore_soggetto.get(0));
		id_erogatore_soggetto.setNome((String)listaFieldId_erogatore_soggetto.get(1));
		id_fruitore_accordoServizioParteSpecifica.setIdErogatore(id_erogatore_soggetto);
		
		// Set idAccordoServizioParteSpecifica nel fruitore
		fruitore.setIdAccordoServizioParteSpecifica(id_fruitore_accordoServizioParteSpecifica);

		
        return fruitore;  
	
	} 
	
	@Override
	public boolean exists(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId) throws MultipleResultException, NotImplementedException, ServiceException, Exception {
		return this._exists(jdbcProperties, log, connection, sqlQueryObject, Long.valueOf(tableId));
	}
	
	private boolean _exists(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, Long tableId) throws MultipleResultException, NotImplementedException, ServiceException, Exception {
	
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);
				
		boolean existsFruitore = false;

		sqlQueryObject = sqlQueryObject.newSQLQueryObject();
		sqlQueryObject.setANDLogicOperator(true);

		sqlQueryObject.addFromTable(this.getFruitoreFieldConverter().toTable(Fruitore.model()));
		sqlQueryObject.addSelectField(this.getFruitoreFieldConverter().toColumn(Fruitore.model().ORA_REGISTRAZIONE,true));
		sqlQueryObject.addWhereCondition("id=?");


		// Exists fruitore
		existsFruitore = jdbcUtilities.exists(sqlQueryObject.createSQLQuery(), jdbcProperties.isShowSql(),
			new JDBCObject(tableId,Long.class));

		
        return existsFruitore;
	
	}
	
	private void _join(IExpression expression, ISQLQueryObject sqlQueryObject) throws NotImplementedException, ServiceException, Exception{
	
		if(expression.inUseModel(Fruitore.model().ID_FRUITORE,false)){
			String tableName1 = this.getFruitoreFieldConverter().toTable(Fruitore.model());
			//String tableName2 = "sfr";
			String tableName2 =  this.getFruitoreFieldConverter().toAliasTable(Fruitore.model().ID_FRUITORE);
			sqlQueryObject.addWhereCondition(tableName1+".id_soggetto="+tableName2+".id");
		}
		if(expression.inUseModel(Fruitore.model().ID_ACCORDO_SERVIZIO_PARTE_SPECIFICA,false)){
			String tableName1 = this.getFruitoreFieldConverter().toTable(Fruitore.model());
			String tableName2 = this.getFruitoreFieldConverter().toTable(Fruitore.model().ID_ACCORDO_SERVIZIO_PARTE_SPECIFICA);
			sqlQueryObject.addWhereCondition(tableName1+".id_servizio="+tableName2+".id");
		}
		if(expression.inUseModel(Fruitore.model().ID_ACCORDO_SERVIZIO_PARTE_SPECIFICA.ID_EROGATORE,false)){
			String tableName1 = this.getFruitoreFieldConverter().toTable(Fruitore.model().ID_ACCORDO_SERVIZIO_PARTE_SPECIFICA);
			String tableName2 = this.getFruitoreFieldConverter().toAliasTable(Fruitore.model().ID_ACCORDO_SERVIZIO_PARTE_SPECIFICA.ID_EROGATORE);
			//String tableName2 = "ser";
			sqlQueryObject.addWhereCondition(tableName1+".id_soggetto="+tableName2+".id");
		}
		
		
        if(expression.inUseModel(Fruitore.model().ID_ACCORDO_SERVIZIO_PARTE_SPECIFICA.ID_EROGATORE,false)){
			if(expression.inUseModel(Fruitore.model().ID_ACCORDO_SERVIZIO_PARTE_SPECIFICA,false)==false){
				sqlQueryObject.addFromTable(this.getFruitoreFieldConverter().toTable(Fruitore.model().ID_ACCORDO_SERVIZIO_PARTE_SPECIFICA));
			}
		}
        
	}
	
	protected java.util.List<Object> _getRootTablePrimaryKeyValues(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdFruitore id) throws NotFoundException, ServiceException, NotImplementedException, Exception{
	    // Identificativi
        java.util.List<Object> rootTableIdValues = new java.util.ArrayList<Object>();
        Long longId = this.findIdFruitore(jdbcProperties, log, connection, sqlQueryObject.newSQLQueryObject(), id, true);
		rootTableIdValues.add(longId);
        return rootTableIdValues;
	}
	
	protected Map<String, List<IField>> _getMapTableToPKColumn() throws NotImplementedException, Exception{
	
		FruitoreFieldConverter converter = this.getFruitoreFieldConverter();
		Map<String, List<IField>> mapTableToPKColumn = new java.util.HashMap<String, List<IField>>();
		UtilsTemplate<IField> utilities = new UtilsTemplate<IField>();

		// Fruitore.model()
		mapTableToPKColumn.put(converter.toTable(Fruitore.model()),
			utilities.newList(
				new CustomField("id", Long.class, "id", converter.toTable(Fruitore.model()))
			));

		// Fruitore.model().ID_FRUITORE
		mapTableToPKColumn.put(converter.toTable(Fruitore.model().ID_FRUITORE),
			utilities.newList(
				new CustomField("id", Long.class, "id", converter.toTable(Fruitore.model().ID_FRUITORE))
			));

		// Fruitore.model().ID_ACCORDO_SERVIZIO_PARTE_SPECIFICA
		mapTableToPKColumn.put(converter.toTable(Fruitore.model().ID_ACCORDO_SERVIZIO_PARTE_SPECIFICA),
			utilities.newList(
				new CustomField("id", Long.class, "id", converter.toTable(Fruitore.model().ID_ACCORDO_SERVIZIO_PARTE_SPECIFICA))
			));

		// Fruitore.model().ID_ACCORDO_SERVIZIO_PARTE_SPECIFICA.ID_EROGATORE
		mapTableToPKColumn.put(converter.toTable(Fruitore.model().ID_ACCORDO_SERVIZIO_PARTE_SPECIFICA.ID_EROGATORE),
			utilities.newList(
				new CustomField("id", Long.class, "id", converter.toTable(Fruitore.model().ID_ACCORDO_SERVIZIO_PARTE_SPECIFICA.ID_EROGATORE))
			));

        return mapTableToPKColumn;		
	}
	
	@Override
	public List<Long> findAllTableIds(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCPaginatedExpression paginatedExpression) throws ServiceException, NotImplementedException, Exception {
		
		List<Long> list = new ArrayList<Long>();

		sqlQueryObject.setSelectDistinct(true);
		sqlQueryObject.setANDLogicOperator(true);
		sqlQueryObject.addSelectField(this.getFruitoreFieldConverter().toTable(Fruitore.model())+".id");
		Class<?> objectIdClass = Long.class;
		
		List<Object> listaQuery = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.prepareFindAll(jdbcProperties, log, connection, sqlQueryObject, paginatedExpression,
												this.getFruitoreFieldConverter(), Fruitore.model());
		
		_join(paginatedExpression,sqlQueryObject);
		
		List<Object> listObjects = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.findAll(jdbcProperties, log, connection, sqlQueryObject, paginatedExpression,
																			this.getFruitoreFieldConverter(), Fruitore.model(), objectIdClass, listaQuery);
		for(Object object: listObjects) {
			list.add((Long)object);
		}

        return list;
		
	}
	
	@Override
	public long findTableId(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCExpression expression) throws ServiceException, NotFoundException, MultipleResultException, NotImplementedException, Exception {
	
		sqlQueryObject.setSelectDistinct(true);
		sqlQueryObject.setANDLogicOperator(true);
		sqlQueryObject.addSelectField(this.getFruitoreFieldConverter().toTable(Fruitore.model())+".id");
		Class<?> objectIdClass = Long.class;
		
		List<Object> listaQuery = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.prepareFind(jdbcProperties, log, connection, sqlQueryObject, expression,
												this.getFruitoreFieldConverter(), Fruitore.model());
		
		_join(expression,sqlQueryObject);

		Object res = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.find(jdbcProperties, log, connection, sqlQueryObject, expression,
														this.getFruitoreFieldConverter(), Fruitore.model(), objectIdClass, listaQuery);
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
	public IdFruitore findId(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, boolean throwNotFound)
			throws NotFoundException, ServiceException, NotImplementedException, Exception {
		
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);

		ISQLQueryObject sqlQueryObjectGet = sqlQueryObject.newSQLQueryObject();
         
		// Object _fruitore
		sqlQueryObjectGet.addFromTable(this.getFruitoreFieldConverter().toTable(Fruitore.model()));
		sqlQueryObjectGet.addSelectField("id_soggetto");
		sqlQueryObjectGet.addSelectField("id_servizio");
		sqlQueryObjectGet.setANDLogicOperator(true);
		sqlQueryObjectGet.addWhereCondition("id=?");

		// Recupero _fruitore
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject [] searchParams_fruitore = new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject [] { 
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(tableId,Long.class)
		};
		List<Class<?>> listaFieldIdReturnType_fruitore = new ArrayList<Class<?>>();
		listaFieldIdReturnType_fruitore.add(Long.class);
		listaFieldIdReturnType_fruitore.add(Long.class);
		org.openspcoop2.core.commons.search.IdFruitore id_fruitore = null;
		List<Object> listaFieldId_fruitore = jdbcUtilities.executeQuerySingleResult(sqlQueryObjectGet.createSQLQuery(), jdbcProperties.isShowSql(),
				listaFieldIdReturnType_fruitore, searchParams_fruitore);
		if(listaFieldId_fruitore==null || listaFieldId_fruitore.size()<=0){
			if(throwNotFound){
				throw new NotFoundException("Not Found");
			}
		}
		else{
			// set _fruitore
			id_fruitore = new org.openspcoop2.core.commons.search.IdFruitore();
			
			Long idSoggettoFK = (Long) listaFieldId_fruitore.get(0);
			id_fruitore.
				setIdFruitore(((IDBSoggettoServiceSearch)this.getServiceManager(connection, jdbcProperties, log).
						getSoggettoServiceSearch()).findId(idSoggettoFK, true));
			
			Long idParteSpecificaFK = (Long) listaFieldId_fruitore.get(1);
			id_fruitore.
				setIdAccordoServizioParteSpecifica(((IDBAccordoServizioParteSpecificaServiceSearch)this.getServiceManager(connection, jdbcProperties, log).
						getAccordoServizioParteSpecificaServiceSearch()).findId(idParteSpecificaFK, true));
		}
		
		return id_fruitore;
		
	}

	@Override
	public Long findTableId(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdFruitore id, boolean throwNotFound)
			throws NotFoundException, ServiceException, NotImplementedException, Exception {
	
		return this.findIdFruitore(jdbcProperties,log,connection,sqlQueryObject,id,throwNotFound);
			
	}
	
	@Override
	public List<List<Object>> nativeQuery(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, 
											String sql,List<Class<?>> returnClassTypes,Object ... param) throws ServiceException,NotFoundException,NotImplementedException,Exception{
		
		return org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.nativeQuery(jdbcProperties, log, connection, sqlQueryObject,
																							sql,returnClassTypes,param);
														
	}
	
	protected Long findIdFruitore(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdFruitore id, boolean throwNotFound) throws NotFoundException, ServiceException, NotImplementedException, Exception {

		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);

		ISQLQueryObject sqlQueryObjectGet = sqlQueryObject.newSQLQueryObject();

		IDBSoggettoServiceSearch soggettoServiceSearch = (IDBSoggettoServiceSearch) this.getServiceManager(connection, jdbcProperties, log).getSoggettoServiceSearch();
		Long idSoggettoFruitore = soggettoServiceSearch.get(id.getIdFruitore()).getId();
		
		IDBAccordoServizioParteSpecificaServiceSearch accordoServizioParteSpecificaServiceSearch = (IDBAccordoServizioParteSpecificaServiceSearch) this.getServiceManager(connection, jdbcProperties, log).getAccordoServizioParteSpecificaServiceSearch();
		Long idAccordoServizioParteSpecifica = accordoServizioParteSpecificaServiceSearch.get(id.getIdAccordoServizioParteSpecifica()).getId();
		
		// Object _fruitore
		sqlQueryObjectGet.addFromTable(this.getFruitoreFieldConverter().toTable(Fruitore.model()));
		sqlQueryObjectGet.addSelectField("id");
		// Devono essere mappati nella where condition i metodi dell'oggetto id.getXXX
		sqlQueryObjectGet.setANDLogicOperator(true);
		sqlQueryObjectGet.setSelectDistinct(true);
		sqlQueryObjectGet.addWhereCondition("id_soggetto=?");
		sqlQueryObjectGet.addWhereCondition("id_servizio=?");

		// Recupero _fruitore
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject [] searchParams_fruitore = new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject [] { 
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(idSoggettoFruitore,Long.class),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(idAccordoServizioParteSpecifica,Long.class)
		};
		Long id_fruitore = null;
		try{
			id_fruitore = (Long) jdbcUtilities.executeQuerySingleResult(sqlQueryObjectGet.createSQLQuery(), jdbcProperties.isShowSql(),
						Long.class, searchParams_fruitore);
		}catch(NotFoundException notFound){
			if(throwNotFound){
				throw new NotFoundException(notFound);
			}
		}
		if(id_fruitore==null || id_fruitore<=0){
			if(throwNotFound){
				throw new NotFoundException("Not Found");
			}
		}
		
		return id_fruitore;
	}
}
