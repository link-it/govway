/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it).
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
package org.openspcoop2.core.controllo_traffico.dao.jdbc;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.openspcoop2.core.controllo_traffico.ConfigurazioneGenerale;
import org.openspcoop2.core.controllo_traffico.dao.jdbc.converter.ConfigurazioneGeneraleFieldConverter;
import org.openspcoop2.core.controllo_traffico.dao.jdbc.fetch.ConfigurazioneGeneraleFetch;
import org.openspcoop2.generic_project.beans.CustomField;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.InUse;
import org.openspcoop2.generic_project.dao.jdbc.IJDBCServiceSearchSingleObject;
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
 * JDBCConfigurazioneGeneraleServiceSearchImpl
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JDBCConfigurazioneGeneraleServiceSearchImpl implements IJDBCServiceSearchSingleObject<ConfigurazioneGenerale, JDBCServiceManager> {

	private ConfigurazioneGeneraleFieldConverter _configurazioneGeneraleFieldConverter = null;
	public ConfigurazioneGeneraleFieldConverter getConfigurazioneGeneraleFieldConverter() {
		if(this._configurazioneGeneraleFieldConverter==null){
			this._configurazioneGeneraleFieldConverter = new ConfigurazioneGeneraleFieldConverter(this.jdbcServiceManager.getJdbcProperties().getDatabaseType());
		}		
		return this._configurazioneGeneraleFieldConverter;
	}
	@Override
	public ISQLFieldConverter getFieldConverter() {
		return this.getConfigurazioneGeneraleFieldConverter();
	}
	
	private ConfigurazioneGeneraleFetch configurazioneGeneraleFetch = new ConfigurazioneGeneraleFetch();
	public ConfigurazioneGeneraleFetch getConfigurazioneGeneraleFetch() {
		return this.configurazioneGeneraleFetch;
	}
	@Override
	public IJDBCFetch getFetch() {
		return getConfigurazioneGeneraleFetch();
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
	public ConfigurazioneGenerale get(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException, MultipleResultException, NotImplementedException, ServiceException,Exception {
		return this._get(jdbcProperties, log, connection, sqlQueryObject, null, idMappingResolutionBehaviour);
		
		
	}
	
	@Override
	public boolean exists(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject) throws MultipleResultException, NotImplementedException, ServiceException,Exception {

		return this._exists(jdbcProperties, log, connection, sqlQueryObject, null);
		
	}
	
	
	
	

	@Override
	public InUse inUse(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject) throws NotFoundException, NotImplementedException, ServiceException,Exception {
		
        return this._inUse(jdbcProperties, log, connection, sqlQueryObject, null);
		
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
												this.getConfigurazioneGeneraleFieldConverter(), field);

			return _select(jdbcProperties, log, connection, sqlQueryObject, paginatedExpression, sqlQueryObjectDistinct);
			
		}finally{
			org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.removeFields(sqlQueryObject,paginatedExpression,field);
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
        						expression, this.getConfigurazioneGeneraleFieldConverter(), ConfigurazioneGenerale.model(), 
        						listaQuery,listaParams);
		
		_join(expression,sqlQueryObject);
        
        List<Map<String,Object>> list = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.select(jdbcProperties, log, connection,
        								org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.prepareSqlQueryObjectForSelectDistinct(sqlQueryObject,sqlQueryObjectDistinct), 
        								expression, this.getConfigurazioneGeneraleFieldConverter(), ConfigurazioneGenerale.model(),
        								listaQuery,listaParams,returnField);
		if(list!=null && list.size()>0){
			return list;
		}
		else{
			throw new NotFoundException("Not Found");
		}
	}
	



	// -- ConstructorExpression	

	@Override
	public JDBCExpression newExpression(Logger log) throws NotImplementedException, ServiceException {
		try{
			return new JDBCExpression(this.getConfigurazioneGeneraleFieldConverter());
		}catch(Exception e){
			throw new ServiceException(e);
		}
	}


	@Override
	public JDBCPaginatedExpression newPaginatedExpression(Logger log) throws NotImplementedException, ServiceException {
		try{
			return new JDBCPaginatedExpression(this.getConfigurazioneGeneraleFieldConverter());
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
	public void mappingTableIds(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, ConfigurazioneGenerale obj) throws NotFoundException,NotImplementedException,ServiceException,Exception{
		_mappingTableIds(jdbcProperties,log,connection,sqlQueryObject,obj,
				this.get(jdbcProperties,log,connection,sqlQueryObject,null));
	}
	private void _mappingTableIds(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, ConfigurazioneGenerale obj, ConfigurazioneGenerale imgSaved) throws NotFoundException,NotImplementedException,ServiceException,Exception{
		if(imgSaved==null){
			return;
		}
		obj.setId(imgSaved.getId());

	}
	
	@Override
	public ConfigurazioneGenerale get(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException, MultipleResultException, NotImplementedException, ServiceException, Exception {
		return this._get(jdbcProperties, log, connection, sqlQueryObject, Long.valueOf(tableId), idMappingResolutionBehaviour);
	}
	
	private ConfigurazioneGenerale _get(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, Long tableId, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException, MultipleResultException, NotImplementedException, ServiceException, Exception {
	
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);
		
		ISQLQueryObject sqlQueryObjectGet = sqlQueryObject.newSQLQueryObject();
				
		ConfigurazioneGenerale configurazioneGenerale = new ConfigurazioneGenerale();
		

		// Object configurazioneGenerale
		sqlQueryObjectGet.setANDLogicOperator(true);
		sqlQueryObjectGet.addFromTable(this.getConfigurazioneGeneraleFieldConverter().toTable(ConfigurazioneGenerale.model().CONTROLLO_TRAFFICO));
		sqlQueryObjectGet.addSelectField("id");
		sqlQueryObjectGet.addSelectField(this.getConfigurazioneGeneraleFieldConverter().toColumn(ConfigurazioneGenerale.model().CONTROLLO_TRAFFICO.CONTROLLO_MAX_THREADS_ENABLED,true));
		sqlQueryObjectGet.addSelectField(this.getConfigurazioneGeneraleFieldConverter().toColumn(ConfigurazioneGenerale.model().CONTROLLO_TRAFFICO.CONTROLLO_MAX_THREADS_WARNING_ONLY,true));
		sqlQueryObjectGet.addSelectField(this.getConfigurazioneGeneraleFieldConverter().toColumn(ConfigurazioneGenerale.model().CONTROLLO_TRAFFICO.CONTROLLO_MAX_THREADS_SOGLIA,true));
		sqlQueryObjectGet.addSelectField(this.getConfigurazioneGeneraleFieldConverter().toColumn(ConfigurazioneGenerale.model().CONTROLLO_TRAFFICO.CONTROLLO_MAX_THREADS_TIPO_ERRORE,true));
		sqlQueryObjectGet.addSelectField(this.getConfigurazioneGeneraleFieldConverter().toColumn(ConfigurazioneGenerale.model().CONTROLLO_TRAFFICO.CONTROLLO_MAX_THREADS_TIPO_ERRORE_INCLUDI_DESCRIZIONE,true));
		sqlQueryObjectGet.addSelectField(this.getConfigurazioneGeneraleFieldConverter().toColumn(ConfigurazioneGenerale.model().CONTROLLO_TRAFFICO.CONTROLLO_CONGESTIONE_ENABLED,true));
		sqlQueryObjectGet.addSelectField(this.getConfigurazioneGeneraleFieldConverter().toColumn(ConfigurazioneGenerale.model().CONTROLLO_TRAFFICO.CONTROLLO_CONGESTIONE_THRESHOLD,true));
		sqlQueryObjectGet.addSelectField(this.getConfigurazioneGeneraleFieldConverter().toColumn(ConfigurazioneGenerale.model().TEMPI_RISPOSTA_FRUIZIONE.CONNECTION_TIMEOUT,true));
		sqlQueryObjectGet.addSelectField(this.getConfigurazioneGeneraleFieldConverter().toColumn(ConfigurazioneGenerale.model().TEMPI_RISPOSTA_FRUIZIONE.READ_TIMEOUT,true));
		sqlQueryObjectGet.addSelectField(this.getConfigurazioneGeneraleFieldConverter().toColumn(ConfigurazioneGenerale.model().TEMPI_RISPOSTA_FRUIZIONE.TEMPO_MEDIO_RISPOSTA,true));
		sqlQueryObjectGet.addSelectField(this.getConfigurazioneGeneraleFieldConverter().toColumn(ConfigurazioneGenerale.model().TEMPI_RISPOSTA_EROGAZIONE.CONNECTION_TIMEOUT,true));
		sqlQueryObjectGet.addSelectField(this.getConfigurazioneGeneraleFieldConverter().toColumn(ConfigurazioneGenerale.model().TEMPI_RISPOSTA_EROGAZIONE.READ_TIMEOUT,true));
		sqlQueryObjectGet.addSelectField(this.getConfigurazioneGeneraleFieldConverter().toColumn(ConfigurazioneGenerale.model().TEMPI_RISPOSTA_EROGAZIONE.TEMPO_MEDIO_RISPOSTA,true));
		sqlQueryObjectGet.addSelectField(this.getConfigurazioneGeneraleFieldConverter().toColumn(ConfigurazioneGenerale.model().RATE_LIMITING.TIPO_ERRORE,true));
		sqlQueryObjectGet.addSelectField(this.getConfigurazioneGeneraleFieldConverter().toColumn(ConfigurazioneGenerale.model().RATE_LIMITING.TIPO_ERRORE_INCLUDI_DESCRIZIONE,true));
		sqlQueryObjectGet.addSelectField(this.getConfigurazioneGeneraleFieldConverter().toColumn(ConfigurazioneGenerale.model().CACHE.CACHE,true));
		sqlQueryObjectGet.addSelectField(this.getConfigurazioneGeneraleFieldConverter().toColumn(ConfigurazioneGenerale.model().CACHE.SIZE,true));
		sqlQueryObjectGet.addSelectField(this.getConfigurazioneGeneraleFieldConverter().toColumn(ConfigurazioneGenerale.model().CACHE.ALGORITHM,true));
		sqlQueryObjectGet.addSelectField(this.getConfigurazioneGeneraleFieldConverter().toColumn(ConfigurazioneGenerale.model().CACHE.IDLE_TIME,true));
		sqlQueryObjectGet.addSelectField(this.getConfigurazioneGeneraleFieldConverter().toColumn(ConfigurazioneGenerale.model().CACHE.LIFE_TIME,true));
		// Parameter 'tableId' unused in single instance

		// Get configurazioneGenerale
		configurazioneGenerale = (ConfigurazioneGenerale) jdbcUtilities.executeQuerySingleResult(sqlQueryObjectGet.createSQLQuery(), jdbcProperties.isShowSql(), ConfigurazioneGenerale.model(), this.getConfigurazioneGeneraleFetch());



		
        return configurazioneGenerale;  
	
	} 
	
	@Override
	public boolean exists(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId) throws MultipleResultException, NotImplementedException, ServiceException, Exception {
		return this._exists(jdbcProperties, log, connection, sqlQueryObject, Long.valueOf(tableId));
	}
	
	private boolean _exists(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, Long tableId) throws MultipleResultException, NotImplementedException, ServiceException, Exception {
	
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);
				
		boolean existsConfigurazioneGenerale = false;

		sqlQueryObject = sqlQueryObject.newSQLQueryObject();
		sqlQueryObject.setANDLogicOperator(true);

		sqlQueryObject.addFromTable(this.getConfigurazioneGeneraleFieldConverter().toTable(ConfigurazioneGenerale.model()));
		sqlQueryObject.addSelectField(this.getConfigurazioneGeneraleFieldConverter().toColumn(ConfigurazioneGenerale.model().CONTROLLO_TRAFFICO.CONTROLLO_MAX_THREADS_SOGLIA,true));
		// Parameter 'tableId' unused in single instance

		// Exists configurazioneGenerale
		existsConfigurazioneGenerale = jdbcUtilities.exists(sqlQueryObject.createSQLQuery(), jdbcProperties.isShowSql());

		
        return existsConfigurazioneGenerale;
	
	}
	
	private void _join(IExpression expression, ISQLQueryObject sqlQueryObject) throws NotImplementedException, ServiceException, Exception{
	
        
	}
	
	protected java.util.List<Object> _getRootTablePrimaryKeyValues(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject) throws NotFoundException, ServiceException, NotImplementedException, Exception{
	    // Identificativi
        java.util.List<Object> rootTableIdValues = new java.util.ArrayList<Object>();
        
        return rootTableIdValues;
	}
	
	protected Map<String, List<IField>> _getMapTableToPKColumn() throws NotImplementedException, Exception{
	
		ConfigurazioneGeneraleFieldConverter converter = this.getConfigurazioneGeneraleFieldConverter();
		Map<String, List<IField>> mapTableToPKColumn = new java.util.HashMap<String, List<IField>>();
		UtilsTemplate<IField> utilities = new UtilsTemplate<IField>();

		// ConfigurazioneGenerale.model()
		mapTableToPKColumn.put(converter.toTable(ConfigurazioneGenerale.model()),
			utilities.newList(
				new CustomField("id", Long.class, "id", converter.toTable(ConfigurazioneGenerale.model()))
			));

		// ConfigurazioneGenerale.model().CONTROLLO_TRAFFICO
		mapTableToPKColumn.put(converter.toTable(ConfigurazioneGenerale.model().CONTROLLO_TRAFFICO),
			utilities.newList(
				new CustomField("id", Long.class, "id", converter.toTable(ConfigurazioneGenerale.model().CONTROLLO_TRAFFICO))
			));
        
        return mapTableToPKColumn;		
	}
	
	@Override
	public List<Long> findAllTableIds(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCPaginatedExpression paginatedExpression) throws ServiceException, NotImplementedException, Exception {
		
		List<Long> list = new ArrayList<Long>();

		sqlQueryObject.setSelectDistinct(true);
		sqlQueryObject.setANDLogicOperator(true);
		sqlQueryObject.addSelectField(this.getConfigurazioneGeneraleFieldConverter().toTable(ConfigurazioneGenerale.model())+".id");
		Class<?> objectIdClass = Long.class;
		
		List<Object> listaQuery = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.prepareFindAll(jdbcProperties, log, connection, sqlQueryObject, paginatedExpression,
												this.getConfigurazioneGeneraleFieldConverter(), ConfigurazioneGenerale.model());
		
		_join(paginatedExpression,sqlQueryObject);
		
		List<Object> listObjects = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.findAll(jdbcProperties, log, connection, sqlQueryObject, paginatedExpression,
																			this.getConfigurazioneGeneraleFieldConverter(), ConfigurazioneGenerale.model(), objectIdClass, listaQuery);
		for(Object object: listObjects) {
			list.add((Long)object);
		}

        return list;
		
	}
	
	@Override
	public long findTableId(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCExpression expression) throws ServiceException, NotFoundException, MultipleResultException, NotImplementedException, Exception {
	
		sqlQueryObject.setSelectDistinct(true);
		sqlQueryObject.setANDLogicOperator(true);
		sqlQueryObject.addSelectField(this.getConfigurazioneGeneraleFieldConverter().toTable(ConfigurazioneGenerale.model())+".id");
		Class<?> objectIdClass = Long.class;
		
		List<Object> listaQuery = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.prepareFind(jdbcProperties, log, connection, sqlQueryObject, expression,
												this.getConfigurazioneGeneraleFieldConverter(), ConfigurazioneGenerale.model());
		
		_join(expression,sqlQueryObject);

		Object res = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.find(jdbcProperties, log, connection, sqlQueryObject, expression,
														this.getConfigurazioneGeneraleFieldConverter(), ConfigurazioneGenerale.model(), objectIdClass, listaQuery);
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
	public List<List<Object>> nativeQuery(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, 
											String sql,List<Class<?>> returnClassTypes,Object ... param) throws ServiceException,NotFoundException,NotImplementedException,Exception{
		
		return org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.nativeQuery(jdbcProperties, log, connection, sqlQueryObject,
																							sql,returnClassTypes,param);
														
	}
	
}
