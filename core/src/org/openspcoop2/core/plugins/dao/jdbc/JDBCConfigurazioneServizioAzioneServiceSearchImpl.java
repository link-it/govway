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
package org.openspcoop2.core.plugins.dao.jdbc;

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
import org.openspcoop2.core.plugins.IdConfigurazioneServizioAzione;
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
import org.openspcoop2.core.plugins.dao.jdbc.converter.ConfigurazioneServizioAzioneFieldConverter;
import org.openspcoop2.core.plugins.dao.jdbc.fetch.ConfigurazioneServizioAzioneFetch;
import org.openspcoop2.core.plugins.dao.IDBConfigurazioneServizioServiceSearch;
import org.openspcoop2.core.plugins.ConfigurazioneServizio;
import org.openspcoop2.core.plugins.ConfigurazioneServizioAzione;
import org.openspcoop2.core.plugins.IdConfigurazioneServizio;

/**     
 * JDBCConfigurazioneServizioAzioneServiceSearchImpl
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JDBCConfigurazioneServizioAzioneServiceSearchImpl implements IJDBCServiceSearchWithId<ConfigurazioneServizioAzione, IdConfigurazioneServizioAzione, JDBCServiceManager> {

	private ConfigurazioneServizioAzioneFieldConverter _configurazioneServizioAzioneFieldConverter = null;
	public ConfigurazioneServizioAzioneFieldConverter getConfigurazioneServizioAzioneFieldConverter() {
		if(this._configurazioneServizioAzioneFieldConverter==null){
			this._configurazioneServizioAzioneFieldConverter = new ConfigurazioneServizioAzioneFieldConverter(this.jdbcServiceManager.getJdbcProperties().getDatabaseType());
		}		
		return this._configurazioneServizioAzioneFieldConverter;
	}
	@Override
	public ISQLFieldConverter getFieldConverter() {
		return this.getConfigurazioneServizioAzioneFieldConverter();
	}
	
	private ConfigurazioneServizioAzioneFetch configurazioneServizioAzioneFetch = new ConfigurazioneServizioAzioneFetch();
	public ConfigurazioneServizioAzioneFetch getConfigurazioneServizioAzioneFetch() {
		return this.configurazioneServizioAzioneFetch;
	}
	@Override
	public IJDBCFetch getFetch() {
		return getConfigurazioneServizioAzioneFetch();
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
	public IdConfigurazioneServizioAzione convertToId(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, ConfigurazioneServizioAzione configurazioneServizioAzione) throws NotImplementedException, ServiceException, Exception{
	
		IdConfigurazioneServizioAzione idConfigurazioneServizioAzione = new IdConfigurazioneServizioAzione();
		idConfigurazioneServizioAzione.setAzione(configurazioneServizioAzione.getAzione());
        idConfigurazioneServizioAzione.setIdConfigurazioneServizio(configurazioneServizioAzione.getIdConfigurazioneServizio());
        return idConfigurazioneServizioAzione;

	}
	
	@Override
	public ConfigurazioneServizioAzione get(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdConfigurazioneServizioAzione id, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException, MultipleResultException, NotImplementedException, ServiceException,Exception {
		Long id_configurazioneServizioAzione = ( (id!=null && id.getId()!=null && id.getId()>0) ? id.getId() : this.findIdConfigurazioneServizioAzione(jdbcProperties, log, connection, sqlQueryObject, id, true));
		return this.getEngine(jdbcProperties, log, connection, sqlQueryObject, id_configurazioneServizioAzione,idMappingResolutionBehaviour);
		
		
	}
	
	@Override
	public boolean exists(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdConfigurazioneServizioAzione id) throws MultipleResultException, NotImplementedException, ServiceException,Exception {

		Long id_configurazioneServizioAzione = this.findIdConfigurazioneServizioAzione(jdbcProperties, log, connection, sqlQueryObject, id, false);
		return id_configurazioneServizioAzione != null && id_configurazioneServizioAzione > 0;
		
	}
	
	@Override
	public List<IdConfigurazioneServizioAzione> findAllIds(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCPaginatedExpression expression, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotImplementedException, ServiceException,Exception {

		List<IdConfigurazioneServizioAzione> list = new ArrayList<IdConfigurazioneServizioAzione>();

		// TODO: implementazione non efficiente. 
		// Per ottenere una implementazione efficiente:
		// 1. Usare metodo select di questa classe indirizzando esattamente i field necessari a create l'ID logico
		// 2. Usare metodo getConfigurazioneServizioAzioneFetch() sul risultato della select per ottenere un oggetto ConfigurazioneServizioAzione
		//	  La fetch con la map inserirà nell'oggetto solo i valori estratti 
		// 3. Usare metodo convertToId per ottenere l'id

        List<Long> ids = this.findAllTableIds(jdbcProperties, log, connection, sqlQueryObject, expression);
        
        for(Long id: ids) {
        	ConfigurazioneServizioAzione configurazioneServizioAzione = this.get(jdbcProperties, log, connection, sqlQueryObject, id, idMappingResolutionBehaviour);
			IdConfigurazioneServizioAzione idConfigurazioneServizioAzione = this.convertToId(jdbcProperties,log,connection,sqlQueryObject,configurazioneServizioAzione);
        	list.add(idConfigurazioneServizioAzione);
        }

        return list;
		
	}
	
	@Override
	public List<ConfigurazioneServizioAzione> findAll(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCPaginatedExpression expression, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotImplementedException, ServiceException,Exception {

        List<ConfigurazioneServizioAzione> list = new ArrayList<ConfigurazioneServizioAzione>();
        
        // TODO: implementazione non efficiente. 
		// Per ottenere una implementazione efficiente:
		// 1. Usare metodo select di questa classe indirizzando esattamente i field necessari
		// 2. Usare metodo getConfigurazioneServizioAzioneFetch() sul risultato della select per ottenere un oggetto ConfigurazioneServizioAzione
		//	  La fetch con la map inserirà nell'oggetto solo i valori estratti 

        List<Long> ids = this.findAllTableIds(jdbcProperties, log, connection, sqlQueryObject, expression);
        
        for(Long id: ids) {
        	list.add(this.get(jdbcProperties, log, connection, sqlQueryObject, id, idMappingResolutionBehaviour));
        }

        return list;      
		
	}
	
	@Override
	public ConfigurazioneServizioAzione find(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCExpression expression, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) 
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
												this.getConfigurazioneServizioAzioneFieldConverter(), ConfigurazioneServizioAzione.model());
		
		sqlQueryObject.addSelectCountField(this.getConfigurazioneServizioAzioneFieldConverter().toTable(ConfigurazioneServizioAzione.model())+".id","tot",true);
		
		joinEngine(expression,sqlQueryObject);
		
		return org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.count(jdbcProperties, log, connection, sqlQueryObject, expression,
																			this.getConfigurazioneServizioAzioneFieldConverter(), ConfigurazioneServizioAzione.model(),listaQuery);
	}

	@Override
	public InUse inUse(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdConfigurazioneServizioAzione id) throws NotFoundException, NotImplementedException, ServiceException,Exception {
		
		Long id_configurazioneServizioAzione = this.findIdConfigurazioneServizioAzione(jdbcProperties, log, connection, sqlQueryObject, id, true);
        return this.inUseEngine(jdbcProperties, log, connection, sqlQueryObject, id_configurazioneServizioAzione);
		
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
												this.getConfigurazioneServizioAzioneFieldConverter(), field);

			return selectEngine(jdbcProperties, log, connection, sqlQueryObject, paginatedExpression, sqlQueryObjectDistinct);
			
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
			List<Map<String,Object>> list = selectEngine(jdbcProperties, log, connection, sqlQueryObject, expression);
			return list.get(0);
		}finally{
			org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.removeFields(sqlQueryObject,expression,functionField);
		}
	}

	@Override
	public List<Map<String,Object>> groupBy(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, 
													JDBCExpression expression, FunctionField ... functionField) throws ServiceException,NotFoundException,NotImplementedException,Exception {
		
		if(expression.getGroupByFields().isEmpty()){
			throw new ServiceException("GroupBy conditions not found in expression");
		}
		
		org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.setFields(sqlQueryObject,expression,functionField);
		try{
			return selectEngine(jdbcProperties, log, connection, sqlQueryObject, expression);
		}finally{
			org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.removeFields(sqlQueryObject,expression,functionField);
		}
	}
	

	@Override
	public List<Map<String,Object>> groupBy(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, 
													JDBCPaginatedExpression paginatedExpression, FunctionField ... functionField) throws ServiceException,NotFoundException,NotImplementedException,Exception {
		
		if(paginatedExpression.getGroupByFields().isEmpty()){
			throw new ServiceException("GroupBy conditions not found in expression");
		}
		
		org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.setFields(sqlQueryObject,paginatedExpression,functionField);
		try{
			return selectEngine(jdbcProperties, log, connection, sqlQueryObject, paginatedExpression);
		}finally{
			org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.removeFields(sqlQueryObject,paginatedExpression,functionField);
		}
	}
	
	protected List<Map<String,Object>> selectEngine(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, 
												IExpression expression) throws ServiceException,NotFoundException,NotImplementedException,Exception {
		return selectEngine(jdbcProperties, log, connection, sqlQueryObject, expression, null);
	}
	protected List<Map<String,Object>> selectEngine(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, 
												IExpression expression, ISQLQueryObject sqlQueryObjectDistinct) throws ServiceException,NotFoundException,NotImplementedException,Exception {
		
		List<Object> listaQuery = new ArrayList<>();
		List<JDBCObject> listaParams = new ArrayList<>();
		List<Object> returnField = org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.prepareSelect(jdbcProperties, log, connection, sqlQueryObject, 
        						expression, this.getConfigurazioneServizioAzioneFieldConverter(), ConfigurazioneServizioAzione.model(), 
        						listaQuery,listaParams);
		
		joinEngine(expression,sqlQueryObject);
        
        List<Map<String,Object>> list = org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.select(jdbcProperties, log, connection,
        								org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.prepareSqlQueryObjectForSelectDistinct(sqlQueryObject,sqlQueryObjectDistinct), 
        								expression, this.getConfigurazioneServizioAzioneFieldConverter(), ConfigurazioneServizioAzione.model(),
        								listaQuery,listaParams,returnField);
		if(list!=null && !list.isEmpty()){
			return list;
		}
		else{
			throw org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.newNotFoundException();
		}
	}
	
	@Override
	public List<Map<String,Object>> union(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, 
												Union union, UnionExpression ... unionExpression) throws ServiceException,NotFoundException,NotImplementedException,Exception {		
		
		List<ISQLQueryObject> sqlQueryObjectInnerList = new ArrayList<>();
		List<JDBCObject> jdbcObjects = new ArrayList<>();
		List<Class<?>> returnClassTypes = org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.prepareUnion(jdbcProperties, log, connection, sqlQueryObject, 
        						this.getConfigurazioneServizioAzioneFieldConverter(), ConfigurazioneServizioAzione.model(), 
        						sqlQueryObjectInnerList, jdbcObjects, union, unionExpression);
		
		if(unionExpression!=null){
			for (int i = 0; i < unionExpression.length; i++) {
				UnionExpression ue = unionExpression[i];
				IExpression expression = ue.getExpression();
				joinEngine(expression,sqlQueryObjectInnerList.get(i));
			}
		}
        
        List<Map<String,Object>> list = org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.union(jdbcProperties, log, connection, sqlQueryObject, 
        								this.getConfigurazioneServizioAzioneFieldConverter(), ConfigurazioneServizioAzione.model(), 
        								sqlQueryObjectInnerList, jdbcObjects, returnClassTypes, union, unionExpression);
        if(list!=null && !list.isEmpty()){
			return list;
		}
		else{
			throw org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.newNotFoundException();
		}								
	}
	
	@Override
	public NonNegativeNumber unionCount(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, 
												Union union, UnionExpression ... unionExpression) throws ServiceException,NotFoundException,NotImplementedException,Exception {		
		
		List<ISQLQueryObject> sqlQueryObjectInnerList = new ArrayList<>();
		List<JDBCObject> jdbcObjects = new ArrayList<>();
		List<Class<?>> returnClassTypes = org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.prepareUnionCount(jdbcProperties, log, connection, sqlQueryObject, 
        						this.getConfigurazioneServizioAzioneFieldConverter(), ConfigurazioneServizioAzione.model(), 
        						sqlQueryObjectInnerList, jdbcObjects, union, unionExpression);
		
		if(unionExpression!=null){
			for (int i = 0; i < unionExpression.length; i++) {
				UnionExpression ue = unionExpression[i];
				IExpression expression = ue.getExpression();
				joinEngine(expression,sqlQueryObjectInnerList.get(i));
			}
		}
        
        NonNegativeNumber number = org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.unionCount(jdbcProperties, log, connection, sqlQueryObject, 
        								this.getConfigurazioneServizioAzioneFieldConverter(), ConfigurazioneServizioAzione.model(), 
        								sqlQueryObjectInnerList, jdbcObjects, returnClassTypes, union, unionExpression);
        if(number!=null && number.longValue()>=0){
			return number;
		}
		else{
			throw org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.newNotFoundException();
		}
	}



	// -- ConstructorExpression	

	@Override
	public JDBCExpression newExpression(Logger log) throws NotImplementedException, ServiceException {
		try{
			return new JDBCExpression(this.getConfigurazioneServizioAzioneFieldConverter());
		}catch(Exception e){
			throw new ServiceException(e);
		}
	}


	@Override
	public JDBCPaginatedExpression newPaginatedExpression(Logger log) throws NotImplementedException, ServiceException {
		try{
			return new JDBCPaginatedExpression(this.getConfigurazioneServizioAzioneFieldConverter());
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
	public void mappingTableIds(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdConfigurazioneServizioAzione id, ConfigurazioneServizioAzione obj) throws NotFoundException,NotImplementedException,ServiceException,Exception{
		_mappingTableIds(jdbcProperties,log,connection,sqlQueryObject,obj,
				this.get(jdbcProperties,log,connection,sqlQueryObject,id,null));
	}
	
	@Override
	public void mappingTableIds(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, ConfigurazioneServizioAzione obj) throws NotFoundException,NotImplementedException,ServiceException,Exception{
		_mappingTableIds(jdbcProperties,log,connection,sqlQueryObject,obj,
				this.get(jdbcProperties,log,connection,sqlQueryObject,tableId,null));
	}
	private void _mappingTableIds(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, ConfigurazioneServizioAzione obj, ConfigurazioneServizioAzione imgSaved) throws NotFoundException,NotImplementedException,ServiceException,Exception{
		if(imgSaved==null){
			return;
		}
		obj.setId(imgSaved.getId());
		if(obj.getIdConfigurazioneServizio()!=null && 
				imgSaved.getIdConfigurazioneServizio()!=null){
			obj.getIdConfigurazioneServizio().setId(imgSaved.getIdConfigurazioneServizio().getId());
		}

	}
	
	@Override
	public ConfigurazioneServizioAzione get(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException, MultipleResultException, NotImplementedException, ServiceException, Exception {
		return this.getEngine(jdbcProperties, log, connection, sqlQueryObject, Long.valueOf(tableId), idMappingResolutionBehaviour);
	}
	
	private ConfigurazioneServizioAzione getEngine(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, Long tableId, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException, MultipleResultException, NotImplementedException, ServiceException, Exception {
	
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);
		
		ISQLQueryObject sqlQueryObjectGet = sqlQueryObject.newSQLQueryObject();
				
		ConfigurazioneServizioAzione configurazioneServizioAzione = new ConfigurazioneServizioAzione();
		

		// Object configurazioneServizioAzione
		ISQLQueryObject sqlQueryObjectGet_configurazioneServizioAzione = sqlQueryObjectGet.newSQLQueryObject();
		sqlQueryObjectGet_configurazioneServizioAzione.setANDLogicOperator(true);
		sqlQueryObjectGet_configurazioneServizioAzione.addFromTable(this.getConfigurazioneServizioAzioneFieldConverter().toTable(ConfigurazioneServizioAzione.model()));
		sqlQueryObjectGet_configurazioneServizioAzione.addSelectField("id");
		sqlQueryObjectGet_configurazioneServizioAzione.addSelectField(this.getConfigurazioneServizioAzioneFieldConverter().toColumn(ConfigurazioneServizioAzione.model().AZIONE,true));
		sqlQueryObjectGet_configurazioneServizioAzione.addWhereCondition("id=?");

		// Get configurazioneServizioAzione
		configurazioneServizioAzione = (ConfigurazioneServizioAzione) jdbcUtilities.executeQuerySingleResult(sqlQueryObjectGet_configurazioneServizioAzione.createSQLQuery(), jdbcProperties.isShowSql(), ConfigurazioneServizioAzione.model(), this.getConfigurazioneServizioAzioneFetch(),
			new JDBCObject(tableId,Long.class));


		if(idMappingResolutionBehaviour==null ||
			(org.openspcoop2.generic_project.beans.IDMappingBehaviour.ENABLED.equals(idMappingResolutionBehaviour) || org.openspcoop2.generic_project.beans.IDMappingBehaviour.USE_TABLE_ID.equals(idMappingResolutionBehaviour))
		){
			// Recupero IdConfigurazioneServizio
			ISQLQueryObject sqlQueryObjectGet_idConfigurazioneServizio = sqlQueryObjectGet.newSQLQueryObject();
			sqlQueryObjectGet_idConfigurazioneServizio.addFromTable(this.getConfigurazioneServizioAzioneFieldConverter().toTable(ConfigurazioneServizioAzione.model()));
			sqlQueryObjectGet_idConfigurazioneServizio.addSelectField("id_config_servizio");
			sqlQueryObjectGet_idConfigurazioneServizio.addWhereCondition("id=?");
			sqlQueryObjectGet_idConfigurazioneServizio.setANDLogicOperator(true);
			Long id_idConfigurazioneServizio = (Long) jdbcUtilities.executeQuerySingleResult(sqlQueryObjectGet_idConfigurazioneServizio.createSQLQuery(), jdbcProperties.isShowSql(),Long.class,
					new JDBCObject(configurazioneServizioAzione.getId(),Long.class));
		
			ConfigurazioneServizio configuraServizio = 
					((IDBConfigurazioneServizioServiceSearch)this.getServiceManager().getConfigurazioneServizioServiceSearch()).get(id_idConfigurazioneServizio);
			IdConfigurazioneServizio idConfigurazioneServizio = new IdConfigurazioneServizio();
			idConfigurazioneServizio.setAccordo(configuraServizio.getAccordo());
			idConfigurazioneServizio.setTipoSoggettoReferente(configuraServizio.getTipoSoggettoReferente());
			idConfigurazioneServizio.setNomeSoggettoReferente(configuraServizio.getNomeSoggettoReferente());
			idConfigurazioneServizio.setVersione(configuraServizio.getVersione());
			idConfigurazioneServizio.setServizio(configuraServizio.getServizio());
			configurazioneServizioAzione.setIdConfigurazioneServizio(idConfigurazioneServizio);
		}
		
        return configurazioneServizioAzione;  
	
	} 
	
	@Override
	public boolean exists(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId) throws MultipleResultException, NotImplementedException, ServiceException, Exception {
		return this._exists(jdbcProperties, log, connection, sqlQueryObject, Long.valueOf(tableId));
	}
	
	private boolean _exists(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, Long tableId) throws MultipleResultException, NotImplementedException, ServiceException, Exception {
	
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);
				
		boolean existsConfigurazioneServizioAzione = false;

		sqlQueryObject = sqlQueryObject.newSQLQueryObject();
		sqlQueryObject.setANDLogicOperator(true);

		sqlQueryObject.addFromTable(this.getConfigurazioneServizioAzioneFieldConverter().toTable(ConfigurazioneServizioAzione.model()));
		sqlQueryObject.addSelectField(this.getConfigurazioneServizioAzioneFieldConverter().toColumn(ConfigurazioneServizioAzione.model().AZIONE,true));
		sqlQueryObject.addWhereCondition("id=?");


		// Exists configurazioneServizioAzione
		existsConfigurazioneServizioAzione = jdbcUtilities.exists(sqlQueryObject.createSQLQuery(), jdbcProperties.isShowSql(),
			new JDBCObject(tableId,Long.class));

		
        return existsConfigurazioneServizioAzione;
	
	}
	
	private void joinEngine(IExpression expression, ISQLQueryObject sqlQueryObject) throws NotImplementedException, ServiceException, Exception{
	
		if(expression.inUseModel(ConfigurazioneServizioAzione.model().ID_CONFIGURAZIONE_SERVIZIO,false)){
			String tableName1 = this.getConfigurazioneServizioAzioneFieldConverter().toTable(ConfigurazioneServizioAzione.model());
			String tableName2 = this.getConfigurazioneServizioAzioneFieldConverter().toTable(ConfigurazioneServizioAzione.model().ID_CONFIGURAZIONE_SERVIZIO);
			sqlQueryObject.addWhereCondition(tableName1+".id_config_servizio="+tableName2+".id");
		}
        
	}
	
	protected java.util.List<Object> getRootTablePrimaryKeyValuesEngine(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdConfigurazioneServizioAzione id) throws NotFoundException, ServiceException, NotImplementedException, Exception{
	    // Identificativi
        java.util.List<Object> rootTableIdValues = new java.util.ArrayList<>();
        Long longId = this.findIdConfigurazioneServizioAzione(jdbcProperties, log, connection, sqlQueryObject.newSQLQueryObject(), id, true);
		rootTableIdValues.add(longId);        
        return rootTableIdValues;
	}
	
	protected Map<String, List<IField>> getMapTableToPKColumnEngine() throws NotImplementedException, Exception{
	
		ConfigurazioneServizioAzioneFieldConverter converter = this.getConfigurazioneServizioAzioneFieldConverter();
		Map<String, List<IField>> mapTableToPKColumn = new java.util.HashMap<>();
		UtilsTemplate<IField> utilities = new UtilsTemplate<>();

		// ConfigurazioneServizioAzione.model()
		mapTableToPKColumn.put(converter.toTable(ConfigurazioneServizioAzione.model()),
			utilities.newList(
				new CustomField("id", Long.class, "id", converter.toTable(ConfigurazioneServizioAzione.model()))
			));

		// ConfigurazioneServizioAzione.model().ID_CONFIGURAZIONE_SERVIZIO
		mapTableToPKColumn.put(converter.toTable(ConfigurazioneServizioAzione.model().ID_CONFIGURAZIONE_SERVIZIO),
			utilities.newList(
				new CustomField("id", Long.class, "id", converter.toTable(ConfigurazioneServizioAzione.model().ID_CONFIGURAZIONE_SERVIZIO))
			));
        
        return mapTableToPKColumn;		
	}
	
	@Override
	public List<Long> findAllTableIds(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCPaginatedExpression paginatedExpression) throws ServiceException, NotImplementedException, Exception {
		
		List<Long> list = new ArrayList<Long>();

		sqlQueryObject.setSelectDistinct(true);
		sqlQueryObject.setANDLogicOperator(true);
		sqlQueryObject.addSelectField(this.getConfigurazioneServizioAzioneFieldConverter().toTable(ConfigurazioneServizioAzione.model())+".id");
		Class<?> objectIdClass = Long.class;
		
		List<Object> listaQuery = org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.prepareFindAll(jdbcProperties, log, connection, sqlQueryObject, paginatedExpression,
												this.getConfigurazioneServizioAzioneFieldConverter(), ConfigurazioneServizioAzione.model());
		
		joinEngine(paginatedExpression,sqlQueryObject);
		
		List<Object> listObjects = org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.findAll(jdbcProperties, log, connection, sqlQueryObject, paginatedExpression,
																			this.getConfigurazioneServizioAzioneFieldConverter(), ConfigurazioneServizioAzione.model(), objectIdClass, listaQuery);
		for(Object object: listObjects) {
			list.add((Long)object);
		}

        return list;
		
	}
	
	@Override
	public long findTableId(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCExpression expression) throws ServiceException, NotFoundException, MultipleResultException, NotImplementedException, Exception {
	
		sqlQueryObject.setSelectDistinct(true);
		sqlQueryObject.setANDLogicOperator(true);
		sqlQueryObject.addSelectField(this.getConfigurazioneServizioAzioneFieldConverter().toTable(ConfigurazioneServizioAzione.model())+".id");
		Class<?> objectIdClass = Long.class;
		
		List<Object> listaQuery = org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.prepareFind(jdbcProperties, log, connection, sqlQueryObject, expression,
												this.getConfigurazioneServizioAzioneFieldConverter(), ConfigurazioneServizioAzione.model());
		
		joinEngine(expression,sqlQueryObject);

		Object res = org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.find(jdbcProperties, log, connection, sqlQueryObject, expression,
														this.getConfigurazioneServizioAzioneFieldConverter(), ConfigurazioneServizioAzione.model(), objectIdClass, listaQuery);
		if(res!=null && (((Long) res).longValue()>0) ){
			return ((Long) res).longValue();
		}
		else{
			throw org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.newNotFoundException();
		}
		
	}

	@Override
	public InUse inUse(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId) throws ServiceException, NotFoundException, NotImplementedException, Exception {
		return this.inUseEngine(jdbcProperties, log, connection, sqlQueryObject, Long.valueOf(tableId));
	}

	private InUse inUseEngine(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, Long tableId) throws ServiceException, NotFoundException, NotImplementedException, Exception {

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
	public IdConfigurazioneServizioAzione findId(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, boolean throwNotFound)
			throws NotFoundException, ServiceException, NotImplementedException, Exception {
		
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);

		ISQLQueryObject sqlQueryObjectGet = sqlQueryObject.newSQLQueryObject();

         
		// Object _configurazioneServizioAzione
		sqlQueryObjectGet.addFromTable(this.getConfigurazioneServizioAzioneFieldConverter().toTable(ConfigurazioneServizioAzione.model()));
		sqlQueryObjectGet.addSelectField(this.getConfigurazioneServizioAzioneFieldConverter().toColumn(ConfigurazioneServizioAzione.model().AZIONE,true));
		sqlQueryObjectGet.addSelectField("id_config_servizio");
		sqlQueryObjectGet.addSelectField(this.getConfigurazioneServizioAzioneFieldConverter().toColumn(ConfigurazioneServizioAzione.model().ID_CONFIGURAZIONE_SERVIZIO.TIPO_SOGGETTO_REFERENTE,true));
		sqlQueryObjectGet.addSelectField(this.getConfigurazioneServizioAzioneFieldConverter().toColumn(ConfigurazioneServizioAzione.model().ID_CONFIGURAZIONE_SERVIZIO.NOME_SOGGETTO_REFERENTE,true));
		sqlQueryObjectGet.addSelectField(this.getConfigurazioneServizioAzioneFieldConverter().toColumn(ConfigurazioneServizioAzione.model().ID_CONFIGURAZIONE_SERVIZIO.ACCORDO,true));
		sqlQueryObjectGet.addSelectField(this.getConfigurazioneServizioAzioneFieldConverter().toColumn(ConfigurazioneServizioAzione.model().ID_CONFIGURAZIONE_SERVIZIO.SERVIZIO,true));
		sqlQueryObjectGet.addSelectField(this.getConfigurazioneServizioAzioneFieldConverter().toColumn(ConfigurazioneServizioAzione.model().ID_CONFIGURAZIONE_SERVIZIO.VERSIONE,true));
		sqlQueryObjectGet.setANDLogicOperator(true);
		sqlQueryObjectGet.addWhereCondition("id=?");

		// Recupero _configurazioneServizioAzione
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject [] searchParams_configurazioneServizioAzione = new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject [] { 
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(tableId,Long.class)
		};
		List<Class<?>> listaFieldIdReturnType_configurazioneServizioAzione = new ArrayList<Class<?>>();
		listaFieldIdReturnType_configurazioneServizioAzione.add(String.class);
		listaFieldIdReturnType_configurazioneServizioAzione.add(Long.class);
		listaFieldIdReturnType_configurazioneServizioAzione.add(String.class);
		listaFieldIdReturnType_configurazioneServizioAzione.add(String.class);
		listaFieldIdReturnType_configurazioneServizioAzione.add(String.class);
		listaFieldIdReturnType_configurazioneServizioAzione.add(String.class);
		listaFieldIdReturnType_configurazioneServizioAzione.add(Integer.class);
		org.openspcoop2.core.plugins.IdConfigurazioneServizioAzione id_configurazioneServizioAzione = null;
		List<Object> listaFieldId_configurazioneServizioAzione = jdbcUtilities.executeQuerySingleResult(sqlQueryObjectGet.createSQLQuery(), jdbcProperties.isShowSql(),
				listaFieldIdReturnType_configurazioneServizioAzione, searchParams_configurazioneServizioAzione);
		if(listaFieldId_configurazioneServizioAzione==null || listaFieldId_configurazioneServizioAzione.size()<=0){
			if(throwNotFound){
				throw org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.newNotFoundException();
			}
		}
		else{
			// set _configurazioneServizioAzione
			id_configurazioneServizioAzione = new org.openspcoop2.core.plugins.IdConfigurazioneServizioAzione();
			id_configurazioneServizioAzione.setAzione((String)listaFieldId_configurazioneServizioAzione.get(0));
			Long idConfigurazioneServizio = (Long)listaFieldId_configurazioneServizioAzione.get(1);
			IdConfigurazioneServizio idConf = new IdConfigurazioneServizio();
			idConf.setTipoSoggettoReferente((String)listaFieldId_configurazioneServizioAzione.get(2));
			idConf.setNomeSoggettoReferente((String)listaFieldId_configurazioneServizioAzione.get(3));
			idConf.setAccordo((String)listaFieldId_configurazioneServizioAzione.get(4));
			idConf.setId(idConfigurazioneServizio);
			idConf.setServizio((String)listaFieldId_configurazioneServizioAzione.get(5));
			idConf.setVersione((Integer)listaFieldId_configurazioneServizioAzione.get(6));
			id_configurazioneServizioAzione.setIdConfigurazioneServizio(idConf);
		}
		
		return id_configurazioneServizioAzione;
		
	}

	@Override
	public Long findTableId(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdConfigurazioneServizioAzione id, boolean throwNotFound)
			throws NotFoundException, ServiceException, NotImplementedException, Exception {
	
		return this.findIdConfigurazioneServizioAzione(jdbcProperties,log,connection,sqlQueryObject,id,throwNotFound);
			
	}
	
	@Override
	public List<List<Object>> nativeQuery(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, 
											String sql,List<Class<?>> returnClassTypes,Object ... param) throws ServiceException,NotFoundException,NotImplementedException,Exception{
		
		return org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.nativeQuery(jdbcProperties, log, connection, sqlQueryObject,
																							sql,returnClassTypes,param);
														
	}
	
	protected Long findIdConfigurazioneServizioAzione(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdConfigurazioneServizioAzione id, boolean throwNotFound) throws NotFoundException, ServiceException, NotImplementedException, Exception {

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

		// Object _configurazioneServizio
		if(id.getIdConfigurazioneServizio()==null){
			throw new ServiceException("IdConfigurazioneServizio is null");
		}
				
		IExpression expressionSearchConfigurazioneServizio = this.getServiceManager().getConfigurazioneServizioServiceSearch().newExpression();
		expressionSearchConfigurazioneServizio.
													and().
													equals(ConfigurazioneServizio.model().ACCORDO, id.getIdConfigurazioneServizio().getAccordo()).
													equals(ConfigurazioneServizio.model().TIPO_SOGGETTO_REFERENTE, id.getIdConfigurazioneServizio().getTipoSoggettoReferente()).
													equals(ConfigurazioneServizio.model().NOME_SOGGETTO_REFERENTE, id.getIdConfigurazioneServizio().getNomeSoggettoReferente()).
													equals(ConfigurazioneServizio.model().VERSIONE, id.getIdConfigurazioneServizio().getVersione()).
													equals(ConfigurazioneServizio.model().SERVIZIO, id.getIdConfigurazioneServizio().getServizio());
		ConfigurazioneServizio configurazioneServizio = this.getServiceManager().getConfigurazioneServizioServiceSearch().find(expressionSearchConfigurazioneServizio);
		Long id_configurazioneServizio = configurazioneServizio.getId();
		
		
		// Object _configurazioneServizioAzione
		sqlQueryObjectGet.addFromTable(this.getConfigurazioneServizioAzioneFieldConverter().toTable(ConfigurazioneServizioAzione.model()));
		sqlQueryObjectGet.addSelectField("id");
		sqlQueryObjectGet.setANDLogicOperator(true);
		sqlQueryObjectGet.setSelectDistinct(true);
		sqlQueryObjectGet.addWhereCondition("id_config_servizio=?");
		sqlQueryObjectGet.addWhereCondition(ConfigurazioneServizioAzione.model().AZIONE.getFieldName() + "=?");

		// Recupero _configurazioneServizioAzione
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject [] searchParams_configurazioneServizioAzione = new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject [] { 
				new JDBCObject( id_configurazioneServizio, Long.class ),
				new JDBCObject( id.getAzione(), String.class )
		};
		Long id_configurazioneServizioAzione = null;
		try{
			id_configurazioneServizioAzione = (Long) jdbcUtilities.executeQuerySingleResult(sqlQueryObjectGet.createSQLQuery(), jdbcProperties.isShowSql(),
						Long.class, searchParams_configurazioneServizioAzione);
		}catch(NotFoundException notFound){
			if(throwNotFound){
				throw new NotFoundException(notFound);
			}
		}
		if(id_configurazioneServizioAzione==null || id_configurazioneServizioAzione<=0){
			if(throwNotFound){
				throw org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.newNotFoundException();
			}
		}
		
		return id_configurazioneServizioAzione;
	}
}
