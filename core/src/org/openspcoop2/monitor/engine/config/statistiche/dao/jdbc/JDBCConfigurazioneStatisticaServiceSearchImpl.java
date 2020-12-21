/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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
package org.openspcoop2.monitor.engine.config.statistiche.dao.jdbc;

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
import org.openspcoop2.monitor.engine.config.statistiche.IdConfigurazioneStatistica;
import org.openspcoop2.monitor.engine.config.statistiche.InfoPlugin;
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
import org.openspcoop2.monitor.engine.config.statistiche.dao.jdbc.converter.ConfigurazioneStatisticaFieldConverter;
import org.openspcoop2.monitor.engine.config.statistiche.dao.jdbc.fetch.ConfigurazioneStatisticaFetch;
import org.openspcoop2.monitor.engine.config.statistiche.ConfigurazioneStatistica;

import org.openspcoop2.monitor.engine.config.base.IdConfigurazioneServizioAzione;
import org.openspcoop2.monitor.engine.config.base.Plugin;
import org.openspcoop2.monitor.engine.config.base.dao.jdbc.JDBCConfigurazioneServizioAzioneBaseLib;
import org.openspcoop2.monitor.engine.config.base.dao.jdbc.JDBCPluginsBaseLib;

/**     
 * JDBCConfigurazioneStatisticaServiceSearchImpl
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JDBCConfigurazioneStatisticaServiceSearchImpl implements IJDBCServiceSearchWithId<ConfigurazioneStatistica, IdConfigurazioneStatistica, JDBCServiceManager> {

	private ConfigurazioneStatisticaFieldConverter _configurazioneStatisticaFieldConverter = null;
	public ConfigurazioneStatisticaFieldConverter getConfigurazioneStatisticaFieldConverter() {
		if(this._configurazioneStatisticaFieldConverter==null){
			this._configurazioneStatisticaFieldConverter = new ConfigurazioneStatisticaFieldConverter(this.jdbcServiceManager.getJdbcProperties().getDatabaseType());
		}		
		return this._configurazioneStatisticaFieldConverter;
	}
	@Override
	public ISQLFieldConverter getFieldConverter() {
		return this.getConfigurazioneStatisticaFieldConverter();
	}
	
	private ConfigurazioneStatisticaFetch configurazioneStatisticaFetch = new ConfigurazioneStatisticaFetch();
	public ConfigurazioneStatisticaFetch getConfigurazioneStatisticaFetch() {
		return this.configurazioneStatisticaFetch;
	}
	@Override
	public IJDBCFetch getFetch() {
		return getConfigurazioneStatisticaFetch();
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
	public IdConfigurazioneStatistica convertToId(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, ConfigurazioneStatistica configurazioneStatistica) throws NotImplementedException, ServiceException, Exception{
	
		IdConfigurazioneStatistica idConfigurazioneStatistica = new IdConfigurazioneStatistica();
		idConfigurazioneStatistica.setIdConfigurazioneStatistica(configurazioneStatistica.getIdConfigurazioneStatistica());
		idConfigurazioneStatistica.setIdConfigurazioneServizioAzione(configurazioneStatistica.getIdConfigurazioneServizioAzione());
        return idConfigurazioneStatistica;
	}
	
	@Override
	public ConfigurazioneStatistica get(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdConfigurazioneStatistica id, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException, MultipleResultException, NotImplementedException, ServiceException,Exception {
		Long id_configurazioneStatistica = ( (id!=null && id.getId()!=null && id.getId()>0) ? id.getId() : this.findIdConfigurazioneStatistica(jdbcProperties, log, connection, sqlQueryObject, id, true));
		return this._get(jdbcProperties, log, connection, sqlQueryObject, id_configurazioneStatistica,idMappingResolutionBehaviour);
		
		
	}
	
	@Override
	public boolean exists(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdConfigurazioneStatistica id) throws MultipleResultException, NotImplementedException, ServiceException,Exception {

		Long id_configurazioneStatistica = this.findIdConfigurazioneStatistica(jdbcProperties, log, connection, sqlQueryObject, id, false);
		return id_configurazioneStatistica != null && id_configurazioneStatistica > 0;
		
	}
	
	@Override
	public List<IdConfigurazioneStatistica> findAllIds(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCPaginatedExpression expression, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotImplementedException, ServiceException,Exception {

		List<IdConfigurazioneStatistica> list = new ArrayList<IdConfigurazioneStatistica>();

		// TODO: implementazione non efficente. 
		// Per ottenere una implementazione efficente:
		// 1. Usare metodo select di questa classe indirizzando esattamente i field necessari a create l'ID logico
		// 2. Usare metodo getConfigurazioneStatisticaFetch() sul risultato della select per ottenere un oggetto ConfigurazioneStatistica
		//	  La fetch con la map inserirà nell'oggetto solo i valori estratti 
		// 3. Usare metodo convertToId per ottenere l'id

        List<Long> ids = this.findAllTableIds(jdbcProperties, log, connection, sqlQueryObject, expression);
        
        for(Long id: ids) {
        	ConfigurazioneStatistica configurazioneStatistica = this.get(jdbcProperties, log, connection, sqlQueryObject, id, idMappingResolutionBehaviour);
			IdConfigurazioneStatistica idConfigurazioneStatistica = this.convertToId(jdbcProperties,log,connection,sqlQueryObject,configurazioneStatistica);
        	list.add(idConfigurazioneStatistica);
        }

        return list;
		
	}
	
	@Override
	public List<ConfigurazioneStatistica> findAll(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCPaginatedExpression expression, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotImplementedException, ServiceException,Exception {

        List<ConfigurazioneStatistica> list = new ArrayList<ConfigurazioneStatistica>();
        
        // TODO: implementazione non efficente. 
		// Per ottenere una implementazione efficente:
		// 1. Usare metodo select di questa classe indirizzando esattamente i field necessari
		// 2. Usare metodo getConfigurazioneStatisticaFetch() sul risultato della select per ottenere un oggetto ConfigurazioneStatistica
		//	  La fetch con la map inserirà nell'oggetto solo i valori estratti 

        List<Long> ids = this.findAllTableIds(jdbcProperties, log, connection, sqlQueryObject, expression);
        
        for(Long id: ids) {
        	list.add(this.get(jdbcProperties, log, connection, sqlQueryObject, id, idMappingResolutionBehaviour));
        }

        return list;      
		
	}
	
	@Override
	public ConfigurazioneStatistica find(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCExpression expression, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) 
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
												this.getConfigurazioneStatisticaFieldConverter(), ConfigurazioneStatistica.model());
		
		sqlQueryObject.addSelectCountField(this.getConfigurazioneStatisticaFieldConverter().toTable(ConfigurazioneStatistica.model())+".pid","tot",true);
		
		_join(expression,sqlQueryObject);
		
		return org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.count(jdbcProperties, log, connection, sqlQueryObject, expression,
																			this.getConfigurazioneStatisticaFieldConverter(), ConfigurazioneStatistica.model(),listaQuery);
	}

	@Override
	public InUse inUse(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdConfigurazioneStatistica id) throws NotFoundException, NotImplementedException, ServiceException,Exception {
		
		Long id_configurazioneStatistica = this.findIdConfigurazioneStatistica(jdbcProperties, log, connection, sqlQueryObject, id, true);
        return this._inUse(jdbcProperties, log, connection, sqlQueryObject, id_configurazioneStatistica);
		
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
												this.getConfigurazioneStatisticaFieldConverter(), field);

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
        						expression, this.getConfigurazioneStatisticaFieldConverter(), ConfigurazioneStatistica.model(), 
        						listaQuery,listaParams);
		
		_join(expression,sqlQueryObject);
        
        List<Map<String,Object>> list = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.select(jdbcProperties, log, connection,
        								org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.prepareSqlQueryObjectForSelectDistinct(sqlQueryObject,sqlQueryObjectDistinct), 
        								expression, this.getConfigurazioneStatisticaFieldConverter(), ConfigurazioneStatistica.model(),
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
        						this.getConfigurazioneStatisticaFieldConverter(), ConfigurazioneStatistica.model(), 
        						sqlQueryObjectInnerList, jdbcObjects, union, unionExpression);
		
		if(unionExpression!=null){
			for (int i = 0; i < unionExpression.length; i++) {
				UnionExpression ue = unionExpression[i];
				IExpression expression = ue.getExpression();
				_join(expression,sqlQueryObjectInnerList.get(i));
			}
		}
        
        List<Map<String,Object>> list = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.union(jdbcProperties, log, connection, sqlQueryObject, 
        								this.getConfigurazioneStatisticaFieldConverter(), ConfigurazioneStatistica.model(), 
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
        						this.getConfigurazioneStatisticaFieldConverter(), ConfigurazioneStatistica.model(), 
        						sqlQueryObjectInnerList, jdbcObjects, union, unionExpression);
		
		if(unionExpression!=null){
			for (int i = 0; i < unionExpression.length; i++) {
				UnionExpression ue = unionExpression[i];
				IExpression expression = ue.getExpression();
				_join(expression,sqlQueryObjectInnerList.get(i));
			}
		}
        
        NonNegativeNumber number = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.unionCount(jdbcProperties, log, connection, sqlQueryObject, 
        								this.getConfigurazioneStatisticaFieldConverter(), ConfigurazioneStatistica.model(), 
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
			return new JDBCExpression(this.getConfigurazioneStatisticaFieldConverter());
		}catch(Exception e){
			throw new ServiceException(e);
		}
	}


	@Override
	public JDBCPaginatedExpression newPaginatedExpression(Logger log) throws NotImplementedException, ServiceException {
		try{
			return new JDBCPaginatedExpression(this.getConfigurazioneStatisticaFieldConverter());
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
	public void mappingTableIds(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdConfigurazioneStatistica id, ConfigurazioneStatistica obj) throws NotFoundException,NotImplementedException,ServiceException,Exception{
		_mappingTableIds(jdbcProperties,log,connection,sqlQueryObject,obj,
				this.get(jdbcProperties,log,connection,sqlQueryObject,id,null));
	}
	
	@Override
	public void mappingTableIds(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, ConfigurazioneStatistica obj) throws NotFoundException,NotImplementedException,ServiceException,Exception{
		_mappingTableIds(jdbcProperties,log,connection,sqlQueryObject,obj,
				this.get(jdbcProperties,log,connection,sqlQueryObject,tableId,null));
	}
	private void _mappingTableIds(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, ConfigurazioneStatistica obj, ConfigurazioneStatistica imgSaved) throws NotFoundException,NotImplementedException,ServiceException,Exception{
		if(imgSaved==null){
			return;
		}
		obj.setId(imgSaved.getId());
		if(obj.getIdConfigurazioneServizioAzione()!=null && 
				imgSaved.getIdConfigurazioneServizioAzione()!=null){
			obj.getIdConfigurazioneServizioAzione().setId(imgSaved.getIdConfigurazioneServizioAzione().getId());
			if(obj.getIdConfigurazioneServizioAzione().getIdConfigurazioneServizio()!=null && 
					imgSaved.getIdConfigurazioneServizioAzione().getIdConfigurazioneServizio()!=null){
				obj.getIdConfigurazioneServizioAzione().getIdConfigurazioneServizio().setId(imgSaved.getIdConfigurazioneServizioAzione().getIdConfigurazioneServizio().getId());
			}
		}
		if(obj.getPlugin()!=null && 
				imgSaved.getPlugin()!=null){
			obj.getPlugin().setId(imgSaved.getPlugin().getId());
		}

	}
	
	@Override
	public ConfigurazioneStatistica get(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException, MultipleResultException, NotImplementedException, ServiceException, Exception {
		return this._get(jdbcProperties, log, connection, sqlQueryObject, Long.valueOf(tableId), idMappingResolutionBehaviour);
	}
	
	private ConfigurazioneStatistica _get(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, Long tableId, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException, MultipleResultException, NotImplementedException, ServiceException, Exception {
	
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);
		
		ISQLQueryObject sqlQueryObjectGet = sqlQueryObject.newSQLQueryObject();
				
		ConfigurazioneStatistica configurazioneStatistica = new ConfigurazioneStatistica();
		

		// Object configurazioneStatistica
		ISQLQueryObject sqlQueryObjectGet_configurazioneStatistica = sqlQueryObjectGet.newSQLQueryObject();
		sqlQueryObjectGet_configurazioneStatistica.setANDLogicOperator(true);
		sqlQueryObjectGet_configurazioneStatistica.addFromTable(this.getConfigurazioneStatisticaFieldConverter().toTable(ConfigurazioneStatistica.model()));
		sqlQueryObjectGet_configurazioneStatistica.addSelectField("pid");
		sqlQueryObjectGet_configurazioneStatistica.addSelectField(this.getConfigurazioneStatisticaFieldConverter().toColumn(ConfigurazioneStatistica.model().ID_CONFIGURAZIONE_STATISTICA,true));
		sqlQueryObjectGet_configurazioneStatistica.addSelectField(this.getConfigurazioneStatisticaFieldConverter().toColumn(ConfigurazioneStatistica.model().ENABLED,true));
		sqlQueryObjectGet_configurazioneStatistica.addSelectField(this.getConfigurazioneStatisticaFieldConverter().toColumn(ConfigurazioneStatistica.model().LABEL,true));
		sqlQueryObjectGet_configurazioneStatistica.addWhereCondition("pid=?");

		// Get configurazioneStatistica
		configurazioneStatistica = (ConfigurazioneStatistica) jdbcUtilities.executeQuerySingleResult(sqlQueryObjectGet_configurazioneStatistica.createSQLQuery(), jdbcProperties.isShowSql(), ConfigurazioneStatistica.model(), this.getConfigurazioneStatisticaFetch(),
			new JDBCObject(tableId,Long.class));


		if(idMappingResolutionBehaviour==null ||
			(org.openspcoop2.generic_project.beans.IDMappingBehaviour.ENABLED.equals(idMappingResolutionBehaviour) || org.openspcoop2.generic_project.beans.IDMappingBehaviour.USE_TABLE_ID.equals(idMappingResolutionBehaviour))
		){
			// Object _configurazioneStatistica_configurazioneServizioAzione (recupero id)
			ISQLQueryObject sqlQueryObjectGet_configurazioneStatistica_configurazioneServizioAzione_readFkId = sqlQueryObjectGet.newSQLQueryObject();
			sqlQueryObjectGet_configurazioneStatistica_configurazioneServizioAzione_readFkId.addFromTable(this.getConfigurazioneStatisticaFieldConverter().toTable(org.openspcoop2.monitor.engine.config.statistiche.ConfigurazioneStatistica.model()));
			sqlQueryObjectGet_configurazioneStatistica_configurazioneServizioAzione_readFkId.addSelectField("id_configurazione");
			sqlQueryObjectGet_configurazioneStatistica_configurazioneServizioAzione_readFkId.addWhereCondition("pid=?");
			sqlQueryObjectGet_configurazioneStatistica_configurazioneServizioAzione_readFkId.setANDLogicOperator(true);
			Long idFK_configurazioneStatistica_configurazioneServizioAzione = (Long) jdbcUtilities.executeQuerySingleResult(sqlQueryObjectGet_configurazioneStatistica_configurazioneServizioAzione_readFkId.createSQLQuery(), jdbcProperties.isShowSql(),Long.class,
					new JDBCObject(configurazioneStatistica.getId(),Long.class));
		
			IdConfigurazioneServizioAzione _tmpIdConfigurazioneServizioAzione = 
					JDBCConfigurazioneServizioAzioneBaseLib.getIdConfigurazioneServizioAzione(connection, jdbcProperties, log, idFK_configurazioneStatistica_configurazioneServizioAzione);
			org.openspcoop2.monitor.engine.config.statistiche.IdConfigurazioneServizioAzione idConfigurazioneServizioAzione = new org.openspcoop2.monitor.engine.config.statistiche.IdConfigurazioneServizioAzione();
			idConfigurazioneServizioAzione.setAzione(_tmpIdConfigurazioneServizioAzione.getAzione());
			org.openspcoop2.monitor.engine.config.statistiche.IdConfigurazioneServizio idConfigurazioneServizio = new org.openspcoop2.monitor.engine.config.statistiche.IdConfigurazioneServizio();
			idConfigurazioneServizio.setAccordo(_tmpIdConfigurazioneServizioAzione.getIdConfigurazioneServizio().getAccordo());
			idConfigurazioneServizio.setTipoSoggettoReferente(_tmpIdConfigurazioneServizioAzione.getIdConfigurazioneServizio().getTipoSoggettoReferente());
			idConfigurazioneServizio.setNomeSoggettoReferente(_tmpIdConfigurazioneServizioAzione.getIdConfigurazioneServizio().getNomeSoggettoReferente());
			idConfigurazioneServizio.setVersione(_tmpIdConfigurazioneServizioAzione.getIdConfigurazioneServizio().getVersione());
			idConfigurazioneServizio.setServizio(_tmpIdConfigurazioneServizioAzione.getIdConfigurazioneServizio().getServizio());
			idConfigurazioneServizioAzione.setIdConfigurazioneServizio(idConfigurazioneServizio);
			configurazioneStatistica.setIdConfigurazioneServizioAzione(idConfigurazioneServizioAzione);
		}
		
		if(idMappingResolutionBehaviour==null ||
				(org.openspcoop2.generic_project.beans.IDMappingBehaviour.ENABLED.equals(idMappingResolutionBehaviour) || org.openspcoop2.generic_project.beans.IDMappingBehaviour.USE_TABLE_ID.equals(idMappingResolutionBehaviour))
			){
			// Object _configurazioneRicerca_plugin (recupero id)
			ISQLQueryObject sqlQueryObjectGet_configurazioneRicerca_plugin_readFkId = sqlQueryObjectGet.newSQLQueryObject();
			sqlQueryObjectGet_configurazioneRicerca_plugin_readFkId.addFromTable(this.getConfigurazioneStatisticaFieldConverter().toTable(org.openspcoop2.monitor.engine.config.statistiche.ConfigurazioneStatistica.model()));
			sqlQueryObjectGet_configurazioneRicerca_plugin_readFkId.addSelectField("id_plugin");
			sqlQueryObjectGet_configurazioneRicerca_plugin_readFkId.addWhereCondition("pid=?");
			sqlQueryObjectGet_configurazioneRicerca_plugin_readFkId.setANDLogicOperator(true);
			Long idFK_configurazioneRicerca_plugin = (Long) jdbcUtilities.executeQuerySingleResult(sqlQueryObjectGet_configurazioneRicerca_plugin_readFkId.createSQLQuery(), jdbcProperties.isShowSql(),Long.class,
					new JDBCObject(configurazioneStatistica.getId(),Long.class));
			
			Plugin plugin = JDBCPluginsBaseLib.getPlugin(connection, jdbcProperties, log, idFK_configurazioneRicerca_plugin);
			InfoPlugin info = new InfoPlugin();
			info.setTipoPlugin(plugin.getTipoPlugin());
			info.setTipo(plugin.getTipo());
			info.setClassName(plugin.getClassName());
			info.setDescrizione(plugin.getDescrizione());
			info.setLabel(plugin.getLabel());
			configurazioneStatistica.setPlugin(info);
		}
		
        return configurazioneStatistica;  
	
	} 
	
	@Override
	public boolean exists(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId) throws MultipleResultException, NotImplementedException, ServiceException, Exception {
		return this._exists(jdbcProperties, log, connection, sqlQueryObject, Long.valueOf(tableId));
	}
	
	private boolean _exists(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, Long tableId) throws MultipleResultException, NotImplementedException, ServiceException, Exception {
	
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);
				
		boolean existsConfigurazioneStatistica = false;

		sqlQueryObject = sqlQueryObject.newSQLQueryObject();
		sqlQueryObject.setANDLogicOperator(true);

		sqlQueryObject.addFromTable(this.getConfigurazioneStatisticaFieldConverter().toTable(ConfigurazioneStatistica.model()));
		sqlQueryObject.addSelectField(this.getConfigurazioneStatisticaFieldConverter().toColumn(ConfigurazioneStatistica.model().ID_CONFIGURAZIONE_STATISTICA,true));
		sqlQueryObject.addWhereCondition("pid=?");


		// Exists configurazioneStatistica
		existsConfigurazioneStatistica = jdbcUtilities.exists(sqlQueryObject.createSQLQuery(), jdbcProperties.isShowSql(),
			new JDBCObject(tableId,Long.class));

		
        return existsConfigurazioneStatistica;
	
	}
	
	private void _join(IExpression expression, ISQLQueryObject sqlQueryObject) throws NotImplementedException, ServiceException, Exception{
	
		if(expression.inUseModel(ConfigurazioneStatistica.model().ID_CONFIGURAZIONE_SERVIZIO_AZIONE,false)){
			String tableName1 = this.getConfigurazioneStatisticaFieldConverter().toTable(ConfigurazioneStatistica.model());
			String tableName2 = this.getConfigurazioneStatisticaFieldConverter().toTable(ConfigurazioneStatistica.model().ID_CONFIGURAZIONE_SERVIZIO_AZIONE);
			sqlQueryObject.addWhereCondition(tableName1+".id_configurazione="+tableName2+".id");
		}
		if(expression.inUseModel(ConfigurazioneStatistica.model().ID_CONFIGURAZIONE_SERVIZIO_AZIONE.ID_CONFIGURAZIONE_SERVIZIO,false)){
			String tableName1 = this.getConfigurazioneStatisticaFieldConverter().toTable(ConfigurazioneStatistica.model().ID_CONFIGURAZIONE_SERVIZIO_AZIONE);
			String tableName2 = this.getConfigurazioneStatisticaFieldConverter().toTable(ConfigurazioneStatistica.model().ID_CONFIGURAZIONE_SERVIZIO_AZIONE.ID_CONFIGURAZIONE_SERVIZIO);
			sqlQueryObject.addWhereCondition(tableName1+".id_config_servizio="+tableName2+".id");
		}
		if(expression.inUseModel(ConfigurazioneStatistica.model().PLUGIN,false)){
			String tableName1 = this.getConfigurazioneStatisticaFieldConverter().toTable(ConfigurazioneStatistica.model().PLUGIN);
			String tableName2 = this.getConfigurazioneStatisticaFieldConverter().toTable(ConfigurazioneStatistica.model());
			sqlQueryObject.addWhereCondition(tableName1+".id="+tableName2+".id_plugin");
		}
		
		// Check FROM Table necessarie per le join di oggetti annidati dal secondo livello in poi dove pero' non viene poi utilizzato l'oggetto del livello precedente nella espressione
		if(expression.inUseModel(ConfigurazioneStatistica.model().ID_CONFIGURAZIONE_SERVIZIO_AZIONE.ID_CONFIGURAZIONE_SERVIZIO,false)){
			if(expression.inUseModel(ConfigurazioneStatistica.model().ID_CONFIGURAZIONE_SERVIZIO_AZIONE,false)==false){
				sqlQueryObject.addFromTable(this.getConfigurazioneStatisticaFieldConverter().toTable(ConfigurazioneStatistica.model().ID_CONFIGURAZIONE_SERVIZIO_AZIONE));
			}
		}

	}
	
	protected java.util.List<Object> _getRootTablePrimaryKeyValues(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdConfigurazioneStatistica id) throws NotFoundException, ServiceException, NotImplementedException, Exception{
	    // Identificativi
        java.util.List<Object> rootTableIdValues = new java.util.ArrayList<Object>();
        Long longId = this.findIdConfigurazioneStatistica(jdbcProperties, log, connection, sqlQueryObject.newSQLQueryObject(), id, true);
		rootTableIdValues.add(longId);        
        return rootTableIdValues;
	}
	
	protected Map<String, List<IField>> _getMapTableToPKColumn() throws NotImplementedException, Exception{
	
		ConfigurazioneStatisticaFieldConverter converter = this.getConfigurazioneStatisticaFieldConverter();
		Map<String, List<IField>> mapTableToPKColumn = new java.util.Hashtable<String, List<IField>>();
		UtilsTemplate<IField> utilities = new UtilsTemplate<IField>();

		// ConfigurazioneStatistica.model()
		mapTableToPKColumn.put(converter.toTable(ConfigurazioneStatistica.model()),
			utilities.newList(
				new CustomField("id", Long.class, "pid", converter.toTable(ConfigurazioneStatistica.model()))
			));

		// ConfigurazioneStatistica.model().ID_CONFIGURAZIONE_SERVIZIO_AZIONE
		mapTableToPKColumn.put(converter.toTable(ConfigurazioneStatistica.model().ID_CONFIGURAZIONE_SERVIZIO_AZIONE),
			utilities.newList(
				new CustomField("id", Long.class, "id", converter.toTable(ConfigurazioneStatistica.model().ID_CONFIGURAZIONE_SERVIZIO_AZIONE))
			));

		// ConfigurazioneStatistica.model().ID_CONFIGURAZIONE_SERVIZIO_AZIONE.ID_CONFIGURAZIONE_SERVIZIO
		mapTableToPKColumn.put(converter.toTable(ConfigurazioneStatistica.model().ID_CONFIGURAZIONE_SERVIZIO_AZIONE.ID_CONFIGURAZIONE_SERVIZIO),
			utilities.newList(
				new CustomField("id", Long.class, "id", converter.toTable(ConfigurazioneStatistica.model().ID_CONFIGURAZIONE_SERVIZIO_AZIONE.ID_CONFIGURAZIONE_SERVIZIO))
			));

		// ConfigurazioneStatistica.model().PLUGIN
		mapTableToPKColumn.put(converter.toTable(ConfigurazioneStatistica.model().PLUGIN),
			utilities.newList(
				new CustomField("id", Long.class, "id", converter.toTable(ConfigurazioneStatistica.model().PLUGIN))
			));
        
        return mapTableToPKColumn;		
	}
	
	@Override
	public List<Long> findAllTableIds(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCPaginatedExpression paginatedExpression) throws ServiceException, NotImplementedException, Exception {
		
		List<Long> list = new ArrayList<Long>();

		sqlQueryObject.setSelectDistinct(true);
		sqlQueryObject.setANDLogicOperator(true);
		sqlQueryObject.addSelectField(this.getConfigurazioneStatisticaFieldConverter().toTable(ConfigurazioneStatistica.model())+".pid");
		Class<?> objectIdClass = Long.class;
		
		List<Object> listaQuery = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.prepareFindAll(jdbcProperties, log, connection, sqlQueryObject, paginatedExpression,
												this.getConfigurazioneStatisticaFieldConverter(), ConfigurazioneStatistica.model());
		
		_join(paginatedExpression,sqlQueryObject);
		
		List<Object> listObjects = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.findAll(jdbcProperties, log, connection, sqlQueryObject, paginatedExpression,
																			this.getConfigurazioneStatisticaFieldConverter(), ConfigurazioneStatistica.model(), objectIdClass, listaQuery);
		for(Object object: listObjects) {
			list.add((Long)object);
		}

        return list;
		
	}
	
	@Override
	public long findTableId(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCExpression expression) throws ServiceException, NotFoundException, MultipleResultException, NotImplementedException, Exception {
	
		sqlQueryObject.setSelectDistinct(true);
		sqlQueryObject.setANDLogicOperator(true);
		sqlQueryObject.addSelectField(this.getConfigurazioneStatisticaFieldConverter().toTable(ConfigurazioneStatistica.model())+".pid");
		Class<?> objectIdClass = Long.class;
		
		List<Object> listaQuery = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.prepareFind(jdbcProperties, log, connection, sqlQueryObject, expression,
												this.getConfigurazioneStatisticaFieldConverter(), ConfigurazioneStatistica.model());
		
		_join(expression,sqlQueryObject);

		Object res = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.find(jdbcProperties, log, connection, sqlQueryObject, expression,
														this.getConfigurazioneStatisticaFieldConverter(), ConfigurazioneStatistica.model(), objectIdClass, listaQuery);
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
	public IdConfigurazioneStatistica findId(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, boolean throwNotFound)
			throws NotFoundException, ServiceException, NotImplementedException, Exception {
		
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);

		ISQLQueryObject sqlQueryObjectGet = sqlQueryObject.newSQLQueryObject();

         
		// Object _configurazioneStatistica
		sqlQueryObjectGet.addFromTable(this.getConfigurazioneStatisticaFieldConverter().toTable(ConfigurazioneStatistica.model()));
		sqlQueryObjectGet.addSelectField(this.getConfigurazioneStatisticaFieldConverter().toColumn(ConfigurazioneStatistica.model().ID_CONFIGURAZIONE_STATISTICA,true));
		sqlQueryObjectGet.addSelectField("id_configurazione");
		sqlQueryObjectGet.setANDLogicOperator(true);
		sqlQueryObjectGet.addWhereCondition("pid=?");

		// Recupero _configurazioneStatistica
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject [] searchParams_configurazioneStatistica = new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject [] { 
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(tableId,Long.class)
		};
		List<Class<?>> listaFieldIdReturnType_configurazioneStatistica = new ArrayList<Class<?>>();
		listaFieldIdReturnType_configurazioneStatistica.add(String.class);
		listaFieldIdReturnType_configurazioneStatistica.add(Long.class);
		org.openspcoop2.monitor.engine.config.statistiche.IdConfigurazioneStatistica id_configurazioneStatistica = null;
		List<Object> listaFieldId_configurazioneStatistica = jdbcUtilities.executeQuerySingleResult(sqlQueryObjectGet.createSQLQuery(), jdbcProperties.isShowSql(),
				listaFieldIdReturnType_configurazioneStatistica, searchParams_configurazioneStatistica);
		if(listaFieldId_configurazioneStatistica==null || listaFieldId_configurazioneStatistica.size()<=0){
			if(throwNotFound){
				throw new NotFoundException("Not Found");
			}
		}
		else{
			// set _configurazioneStatistica
			id_configurazioneStatistica = new org.openspcoop2.monitor.engine.config.statistiche.IdConfigurazioneStatistica();
			id_configurazioneStatistica.setIdConfigurazioneStatistica((String)listaFieldId_configurazioneStatistica.get(0));
			
			Long idFK_configurazioneStatistica_configurazioneServizioAzione = (Long) listaFieldId_configurazioneStatistica.get(1);
			IdConfigurazioneServizioAzione _tmpIdConfigurazioneServizioAzione = 
					JDBCConfigurazioneServizioAzioneBaseLib.getIdConfigurazioneServizioAzione(connection, jdbcProperties, log, idFK_configurazioneStatistica_configurazioneServizioAzione);
			
			org.openspcoop2.monitor.engine.config.statistiche.IdConfigurazioneServizioAzione idConfigurazioneServizioAzione = new org.openspcoop2.monitor.engine.config.statistiche.IdConfigurazioneServizioAzione();
			idConfigurazioneServizioAzione.setAzione(_tmpIdConfigurazioneServizioAzione.getAzione());
			org.openspcoop2.monitor.engine.config.statistiche.IdConfigurazioneServizio idConfigurazioneServizio = new org.openspcoop2.monitor.engine.config.statistiche.IdConfigurazioneServizio();
			idConfigurazioneServizio.setAccordo(_tmpIdConfigurazioneServizioAzione.getIdConfigurazioneServizio().getAccordo());
			idConfigurazioneServizio.setTipoSoggettoReferente(_tmpIdConfigurazioneServizioAzione.getIdConfigurazioneServizio().getTipoSoggettoReferente());
			idConfigurazioneServizio.setNomeSoggettoReferente(_tmpIdConfigurazioneServizioAzione.getIdConfigurazioneServizio().getNomeSoggettoReferente());
			idConfigurazioneServizio.setVersione(_tmpIdConfigurazioneServizioAzione.getIdConfigurazioneServizio().getVersione());
			idConfigurazioneServizio.setServizio(_tmpIdConfigurazioneServizioAzione.getIdConfigurazioneServizio().getServizio());
			idConfigurazioneServizioAzione.setIdConfigurazioneServizio(idConfigurazioneServizio);
			id_configurazioneStatistica.setIdConfigurazioneServizioAzione(idConfigurazioneServizioAzione);
		}
		
		return id_configurazioneStatistica;
		
	}

	@Override
	public Long findTableId(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdConfigurazioneStatistica id, boolean throwNotFound)
			throws NotFoundException, ServiceException, NotImplementedException, Exception {
	
		return this.findIdConfigurazioneStatistica(jdbcProperties,log,connection,sqlQueryObject,id,throwNotFound);
			
	}
	
	@Override
	public List<List<Object>> nativeQuery(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, 
											String sql,List<Class<?>> returnClassTypes,Object ... param) throws ServiceException,NotFoundException,NotImplementedException,Exception{
		
		return org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.nativeQuery(jdbcProperties, log, connection, sqlQueryObject,
																							sql,returnClassTypes,param);
														
	}
	
	protected Long findIdConfigurazioneStatistica(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdConfigurazioneStatistica id, boolean throwNotFound) throws NotFoundException, ServiceException, NotImplementedException, Exception {

		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);

		ISQLQueryObject sqlQueryObjectGet = sqlQueryObject.newSQLQueryObject();

		// Object _configurazioneServizioAzione
		
		if(id==null){
			throw new ServiceException("Id is null");
		}
		if(id.getIdConfigurazioneServizioAzione()==null){
			throw new ServiceException("IdConfigurazioneServizioAzione is null");
		}
		if(id.getIdConfigurazioneServizioAzione().getAzione()==null){
			throw new ServiceException("IdConfigurazioneServizioAzione.azione is null");
		}
		if(id.getIdConfigurazioneServizioAzione().getIdConfigurazioneServizio()==null){
			throw new ServiceException("IdConfigurazioneServizioAzione.idConfigurazioneServizio is null");
		}
		if(id.getIdConfigurazioneStatistica()==null){
			throw new ServiceException("IdConfigurazioneStatistica is null");
		}
				
		Long id_configurazioneServizioAzione = 
				JDBCConfigurazioneServizioAzioneBaseLib.getIdConfigurazioneServizioAzione(connection, jdbcProperties, log, 
						id.getIdConfigurazioneServizioAzione().getAzione(),
						id.getIdConfigurazioneServizioAzione().getIdConfigurazioneServizio().getAccordo(), 
						id.getIdConfigurazioneServizioAzione().getIdConfigurazioneServizio().getTipoSoggettoReferente(), 
						id.getIdConfigurazioneServizioAzione().getIdConfigurazioneServizio().getNomeSoggettoReferente(), 
						id.getIdConfigurazioneServizioAzione().getIdConfigurazioneServizio().getVersione(), 
						id.getIdConfigurazioneServizioAzione().getIdConfigurazioneServizio().getServizio(),
						throwNotFound);
		if(id_configurazioneServizioAzione==null){
			return null; // permesso se throwNotFound==false
		}
		
		
		// Object _configurazioneStatistica
		sqlQueryObjectGet.addFromTable(this.getConfigurazioneStatisticaFieldConverter().toTable(ConfigurazioneStatistica.model()));
		sqlQueryObjectGet.addSelectField("pid");
		sqlQueryObjectGet.setANDLogicOperator(true);
		sqlQueryObjectGet.setSelectDistinct(true);
		sqlQueryObjectGet.addWhereCondition(this.getConfigurazioneStatisticaFieldConverter().toColumn(ConfigurazioneStatistica.model().ID_CONFIGURAZIONE_STATISTICA,true)+"=?");
		sqlQueryObjectGet.addWhereCondition("id_configurazione=?");

		// Recupero _configurazioneStatistica
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject [] searchParams_configurazioneStatistica = new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject []  { 
				new JDBCObject( id.getIdConfigurazioneStatistica(), String.class ),
				new JDBCObject( id_configurazioneServizioAzione, Long.class )
		};
		Long id_configurazioneStatistica = null;
		try{
			id_configurazioneStatistica = (Long) jdbcUtilities.executeQuerySingleResult(sqlQueryObjectGet.createSQLQuery(), jdbcProperties.isShowSql(),
						Long.class, searchParams_configurazioneStatistica);
		}catch(NotFoundException notFound){
			if(throwNotFound){
				throw new NotFoundException(notFound);
			}
		}
		if(id_configurazioneStatistica==null || id_configurazioneStatistica<=0){
			if(throwNotFound){
				throw new NotFoundException("Not Found");
			}
		}
		
		return id_configurazioneStatistica;
	}
}
