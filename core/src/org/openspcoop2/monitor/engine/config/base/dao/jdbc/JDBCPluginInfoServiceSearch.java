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
package org.openspcoop2.monitor.engine.config.base.dao.jdbc;

import org.openspcoop2.generic_project.dao.IDBServiceUtilities;
import org.openspcoop2.generic_project.dao.jdbc.IJDBCServiceSearchSingleObject;
import org.openspcoop2.generic_project.beans.InUse;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.dao.jdbc.JDBCServiceManagerProperties;
import org.openspcoop2.generic_project.exception.MultipleResultException;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.generic_project.exception.NotImplementedException;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.generic_project.exception.ValidationException;
import org.openspcoop2.generic_project.expression.IExpression;
import org.openspcoop2.generic_project.expression.IPaginatedExpression;
import org.openspcoop2.generic_project.expression.impl.sql.ISQLFieldConverter;
import org.openspcoop2.generic_project.dao.jdbc.JDBCExpression;
import org.openspcoop2.generic_project.dao.jdbc.JDBCPaginatedExpression;
import org.openspcoop2.generic_project.dao.jdbc.JDBCProperties;
import org.openspcoop2.generic_project.dao.jdbc.utils.IJDBCFetch;
import org.openspcoop2.generic_project.dao.jdbc.utils.JDBC_SQLObjectFactory;

import org.openspcoop2.monitor.engine.config.base.dao.jdbc.JDBCServiceManager;
import org.openspcoop2.monitor.engine.config.base.dao.jdbc.JDBCLimitedServiceManager;
import org.openspcoop2.monitor.engine.config.base.PluginInfo;
import org.openspcoop2.monitor.engine.config.base.dao.IPluginInfoServiceSearch;
import org.openspcoop2.monitor.engine.config.base.utils.ProjectInfo;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.openspcoop2.utils.sql.ISQLQueryObject;

/**     
 * Service can be used to search for the backend objects of type {@link org.openspcoop2.monitor.engine.config.base.PluginInfo} 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
*/
public class JDBCPluginInfoServiceSearch implements IPluginInfoServiceSearch, IDBServiceUtilities<PluginInfo> {


	protected JDBCServiceManagerProperties jdbcProperties = null;
	protected JDBCServiceManager jdbcServiceManager = null;
	protected Logger log = null;
	protected IJDBCServiceSearchSingleObject<PluginInfo, JDBCServiceManager> serviceSearch = null;
	protected JDBC_SQLObjectFactory jdbcSqlObjectFactory = null;
	public JDBCPluginInfoServiceSearch(JDBCServiceManager jdbcServiceManager) throws ServiceException {
		this.jdbcServiceManager = jdbcServiceManager;
		this.jdbcProperties = jdbcServiceManager.getJdbcProperties();
		this.log = jdbcServiceManager.getLog();
		this.log.debug(JDBCPluginInfoServiceSearch.class.getName()+ " initialized");
		this.serviceSearch = JDBCProperties.getInstance(org.openspcoop2.monitor.engine.config.base.dao.jdbc.JDBCServiceManager.class.getPackage(),ProjectInfo.getInstance()).getServiceSearch("pluginInfo");
		this.serviceSearch.setServiceManager(new JDBCLimitedServiceManager(this.jdbcServiceManager));
		this.jdbcSqlObjectFactory = new JDBC_SQLObjectFactory();
	}
	
	@Override
	public void validate(PluginInfo pluginInfo) throws ServiceException,
			ValidationException, NotImplementedException {
		org.openspcoop2.generic_project.utils.XSDValidator.validate(pluginInfo, this.log, 
				org.openspcoop2.monitor.engine.config.base.utils.XSDValidator.getXSDValidator(this.log));
	}
	
	@Override
	public IJDBCFetch getFetch() {
		return this.serviceSearch.getFetch();
	}

	@Override
	public ISQLFieldConverter getFieldConverter() {
		return this.serviceSearch.getFieldConverter();
	}
	
		
	@Override
	public PluginInfo get() throws ServiceException, NotFoundException,MultipleResultException, NotImplementedException {
    
		Connection connection = null;
		try{
			
			
			// ISQLQueryObject
			ISQLQueryObject sqlQueryObject = this.jdbcSqlObjectFactory.createSQLQueryObject(this.jdbcProperties.getDatabase());
			sqlQueryObject.setANDLogicOperator(true);
			// Connection sql
			connection = this.jdbcServiceManager.getConnection();
		
			return this.serviceSearch.get(this.jdbcProperties,this.log,connection,sqlQueryObject,null);	
		
		}catch(ServiceException e){
			this.log.error(e.getMessage(),e); throw e;
		}catch(NotFoundException e){
			this.log.debug(e.getMessage(),e); throw e;
		}catch(MultipleResultException e){
			this.log.error(e.getMessage(),e); throw e;
		}catch(NotImplementedException e){
			this.log.error(e.getMessage(),e); throw e;
		}catch(Exception e){
			this.log.error(e.getMessage(),e); throw new ServiceException("Get not completed: "+e.getMessage(),e);
		}finally{
			if(connection!=null){
				this.jdbcServiceManager.closeConnection(connection);
			}
		}
	
	}

	@Override
	public PluginInfo get(org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws ServiceException, NotFoundException,MultipleResultException, NotImplementedException {
		Connection connection = null;
		try{
			
			if(idMappingResolutionBehaviour==null){
				throw new Exception("Parameter (type:"+org.openspcoop2.generic_project.beans.IDMappingBehaviour.class.getName()+") 'idMappingResolutionBehaviour' is null");
			}
			
			// ISQLQueryObject
			ISQLQueryObject sqlQueryObject = this.jdbcSqlObjectFactory.createSQLQueryObject(this.jdbcProperties.getDatabase());
			sqlQueryObject.setANDLogicOperator(true);
			// Connection sql
			connection = this.jdbcServiceManager.getConnection();
		
			return this.serviceSearch.get(this.jdbcProperties,this.log,connection,sqlQueryObject,idMappingResolutionBehaviour);	
		
		}catch(ServiceException e){
			this.log.error(e.getMessage(),e); throw e;
		}catch(NotFoundException e){
			this.log.debug(e.getMessage(),e); throw e;
		}catch(MultipleResultException e){
			this.log.error(e.getMessage(),e); throw e;
		}catch(NotImplementedException e){
			this.log.error(e.getMessage(),e); throw e;
		}catch(Exception e){
			this.log.error(e.getMessage(),e); throw new ServiceException("Get (idMappingResolutionBehaviour) not completed: "+e.getMessage(),e);
		}finally{
			if(connection!=null){
				this.jdbcServiceManager.closeConnection(connection);
			}
		}
	
	}

	@Override
	public boolean exists() throws MultipleResultException,ServiceException,NotImplementedException {

		Connection connection = null;
		try{
			

			// ISQLQueryObject
			ISQLQueryObject sqlQueryObject = this.jdbcSqlObjectFactory.createSQLQueryObject(this.jdbcProperties.getDatabase());
			sqlQueryObject.setANDLogicOperator(true);
			// Connection sql
			connection = this.jdbcServiceManager.getConnection();

			return this.serviceSearch.exists(this.jdbcProperties,this.log,connection,sqlQueryObject);
	
		}catch(MultipleResultException e){
			this.log.error(e.getMessage(),e); throw e;
		}catch(ServiceException e){
			this.log.error(e.getMessage(),e); throw e;
		}catch(NotImplementedException e){
			this.log.error(e.getMessage(),e); throw e;
		}catch(Exception e){
			this.log.error(e.getMessage(),e); throw new ServiceException("Exists not completed: "+e.getMessage(),e);
		}finally{
			if(connection!=null){
				this.jdbcServiceManager.closeConnection(connection);
			}
		}
		
	}
	




	@Override
	public InUse inUse() throws ServiceException, NotFoundException,NotImplementedException {

		Connection connection = null;
		try{
			
			
			// ISQLQueryObject
			ISQLQueryObject sqlQueryObject = this.jdbcSqlObjectFactory.createSQLQueryObject(this.jdbcProperties.getDatabase());
			sqlQueryObject.setANDLogicOperator(true);
			// Connection sql
			connection = this.jdbcServiceManager.getConnection();

			return this.serviceSearch.inUse(this.jdbcProperties,this.log,connection,sqlQueryObject);	
	
		}catch(ServiceException e){
			this.log.error(e.getMessage(),e); throw e;
		}catch(NotFoundException e){
			this.log.debug(e.getMessage(),e); throw e;
		}catch(NotImplementedException e){
			this.log.error(e.getMessage(),e); throw e;
		}catch(Exception e){
			this.log.error(e.getMessage(),e); throw new ServiceException("InUse not completed: "+e.getMessage(),e);
		}finally{
			if(connection!=null){
				this.jdbcServiceManager.closeConnection(connection);
			}
		}
		
	}
	
	@Override
	public List<Object> select(IPaginatedExpression paginatedExpression, IField field) throws ServiceException,NotFoundException,NotImplementedException {
	
		Connection connection = null;
		try{
			
			// check parameters
			if(paginatedExpression==null){
				throw new Exception("Parameter (type:"+IPaginatedExpression.class.getName()+") 'paginatedExpression' is null");
			}
			if( ! (paginatedExpression instanceof JDBCPaginatedExpression) ){
				throw new Exception("Parameter (type:"+paginatedExpression.getClass().getName()+") 'paginatedExpression' has wrong type, expect "+JDBCPaginatedExpression.class.getName());
			}
			JDBCPaginatedExpression jdbcPaginatedExpression = (JDBCPaginatedExpression) paginatedExpression;
			this.log.debug("sql = "+jdbcPaginatedExpression.toSql());

			// ISQLQueryObject
			ISQLQueryObject sqlQueryObject = this.jdbcSqlObjectFactory.createSQLQueryObject(this.jdbcProperties.getDatabase());
			sqlQueryObject.setANDLogicOperator(true);
			// Connection sql
			connection = this.jdbcServiceManager.getConnection();

			return this.serviceSearch.select(this.jdbcProperties,this.log,connection,sqlQueryObject,jdbcPaginatedExpression,field);			
	
		}catch(ServiceException e){
			this.log.error(e.getMessage(),e); throw e;
		}catch(NotFoundException e){
			this.log.debug(e.getMessage(),e); throw e;
		}catch(NotImplementedException e){
			this.log.error(e.getMessage(),e); throw e;
		}catch(Exception e){
			this.log.error(e.getMessage(),e); throw new ServiceException("Select not completed: "+e.getMessage(),e);
		}finally{
			if(connection!=null){
				this.jdbcServiceManager.closeConnection(connection);
			}
		}
	
	}
	
	@Override
	public List<Object> select(IPaginatedExpression paginatedExpression, boolean distinct, IField field) throws ServiceException,NotFoundException,NotImplementedException {

		Connection connection = null;
		try{
			
			// check parameters
			if(paginatedExpression==null){
				throw new Exception("Parameter (type:"+IPaginatedExpression.class.getName()+") 'paginatedExpression' is null");
			}
			if( ! (paginatedExpression instanceof JDBCPaginatedExpression) ){
				throw new Exception("Parameter (type:"+paginatedExpression.getClass().getName()+") 'paginatedExpression' has wrong type, expect "+JDBCPaginatedExpression.class.getName());
			}
			JDBCPaginatedExpression jdbcPaginatedExpression = (JDBCPaginatedExpression) paginatedExpression;
			this.log.debug("sql = "+jdbcPaginatedExpression.toSql());

			// ISQLQueryObject
			ISQLQueryObject sqlQueryObject = this.jdbcSqlObjectFactory.createSQLQueryObject(this.jdbcProperties.getDatabase());
			sqlQueryObject.setANDLogicOperator(true);
			// Connection sql
			connection = this.jdbcServiceManager.getConnection();

			return this.serviceSearch.select(this.jdbcProperties,this.log,connection,sqlQueryObject,jdbcPaginatedExpression,distinct,field);			
	
		}catch(ServiceException e){
			this.log.error(e.getMessage(),e); throw e;
		}catch(NotFoundException e){
			this.log.debug(e.getMessage(),e); throw e;
		}catch(NotImplementedException e){
			this.log.error(e.getMessage(),e); throw e;
		}catch(Exception e){
			this.log.error(e.getMessage(),e); throw new ServiceException("Select not completed: "+e.getMessage(),e);
		}finally{
			if(connection!=null){
				this.jdbcServiceManager.closeConnection(connection);
			}
		}
		
	}
	
	@Override
	public List<Map<String,Object>> select(IPaginatedExpression paginatedExpression, IField ... field) throws ServiceException,NotFoundException,NotImplementedException {
	
		Connection connection = null;
		try{
			
			// check parameters
			if(paginatedExpression==null){
				throw new Exception("Parameter (type:"+IPaginatedExpression.class.getName()+") 'paginatedExpression' is null");
			}
			if( ! (paginatedExpression instanceof JDBCPaginatedExpression) ){
				throw new Exception("Parameter (type:"+paginatedExpression.getClass().getName()+") 'paginatedExpression' has wrong type, expect "+JDBCPaginatedExpression.class.getName());
			}
			JDBCPaginatedExpression jdbcPaginatedExpression = (JDBCPaginatedExpression) paginatedExpression;
			this.log.debug("sql = "+jdbcPaginatedExpression.toSql());

			// ISQLQueryObject
			ISQLQueryObject sqlQueryObject = this.jdbcSqlObjectFactory.createSQLQueryObject(this.jdbcProperties.getDatabase());
			sqlQueryObject.setANDLogicOperator(true);
			// Connection sql
			connection = this.jdbcServiceManager.getConnection();

			return this.serviceSearch.select(this.jdbcProperties,this.log,connection,sqlQueryObject,jdbcPaginatedExpression,field);			
	
		}catch(ServiceException e){
			this.log.error(e.getMessage(),e); throw e;
		}catch(NotFoundException e){
			this.log.debug(e.getMessage(),e); throw e;
		}catch(NotImplementedException e){
			this.log.error(e.getMessage(),e); throw e;
		}catch(Exception e){
			this.log.error(e.getMessage(),e); throw new ServiceException("Select not completed: "+e.getMessage(),e);
		}finally{
			if(connection!=null){
				this.jdbcServiceManager.closeConnection(connection);
			}
		}
	
	}
	@Override
	public List<Map<String,Object>> select(IPaginatedExpression paginatedExpression, boolean distinct, IField ... field) throws ServiceException,NotFoundException,NotImplementedException {

		Connection connection = null;
		try{
			
			// check parameters
			if(paginatedExpression==null){
				throw new Exception("Parameter (type:"+IPaginatedExpression.class.getName()+") 'paginatedExpression' is null");
			}
			if( ! (paginatedExpression instanceof JDBCPaginatedExpression) ){
				throw new Exception("Parameter (type:"+paginatedExpression.getClass().getName()+") 'paginatedExpression' has wrong type, expect "+JDBCPaginatedExpression.class.getName());
			}
			JDBCPaginatedExpression jdbcPaginatedExpression = (JDBCPaginatedExpression) paginatedExpression;
			this.log.debug("sql = "+jdbcPaginatedExpression.toSql());

			// ISQLQueryObject
			ISQLQueryObject sqlQueryObject = this.jdbcSqlObjectFactory.createSQLQueryObject(this.jdbcProperties.getDatabase());
			sqlQueryObject.setANDLogicOperator(true);
			// Connection sql
			connection = this.jdbcServiceManager.getConnection();

			return this.serviceSearch.select(this.jdbcProperties,this.log,connection,sqlQueryObject,jdbcPaginatedExpression,distinct,field);			
	
		}catch(ServiceException e){
			this.log.error(e.getMessage(),e); throw e;
		}catch(NotFoundException e){
			this.log.debug(e.getMessage(),e); throw e;
		}catch(NotImplementedException e){
			this.log.error(e.getMessage(),e); throw e;
		}catch(Exception e){
			this.log.error(e.getMessage(),e); throw new ServiceException("Select not completed: "+e.getMessage(),e);
		}finally{
			if(connection!=null){
				this.jdbcServiceManager.closeConnection(connection);
			}
		}
		
	}
	
	
	
	
	
	

	@Override
	public IExpression newExpression() throws ServiceException,NotImplementedException {

		return this.serviceSearch.newExpression(this.log);

	}

	@Override
	public IPaginatedExpression newPaginatedExpression() throws ServiceException, NotImplementedException {

		return this.serviceSearch.newPaginatedExpression(this.log);

	}
	
	@Override
	public IExpression toExpression(IPaginatedExpression paginatedExpression) throws ServiceException,NotImplementedException {

		return this.serviceSearch.toExpression((JDBCPaginatedExpression)paginatedExpression,this.log);

	}

	@Override
	public IPaginatedExpression toPaginatedExpression(IExpression expression) throws ServiceException, NotImplementedException {

		return this.serviceSearch.toPaginatedExpression((JDBCExpression)expression,this.log);

	}
	

	
	@Override
	public List<List<Object>> nativeQuery(String sql,List<Class<?>> returnClassTypes,Object ... param) throws ServiceException,NotFoundException,NotImplementedException{
	
		Connection connection = null;
		try{
			
			// check parameters
			if(returnClassTypes==null || returnClassTypes.size()<=0){
				throw new Exception("Parameter 'returnClassTypes' is less equals 0");
			}
			
			// ISQLQueryObject
			ISQLQueryObject sqlQueryObject = this.jdbcSqlObjectFactory.createSQLQueryObject(this.jdbcProperties.getDatabase());
			sqlQueryObject.setANDLogicOperator(true);
			// Connection sql
			connection = this.jdbcServiceManager.getConnection();

			return this.serviceSearch.nativeQuery(this.jdbcProperties,this.log,connection,sqlQueryObject,sql,returnClassTypes,param);		
	
		}catch(ServiceException e){
			this.log.error(e.getMessage(),e); throw e;
		}catch(NotFoundException e){
			this.log.debug(e.getMessage(),e); throw e;
		}catch(NotImplementedException e){
			this.log.error(e.getMessage(),e); throw e;
		}catch(Exception e){
			this.log.error(e.getMessage(),e); throw new ServiceException("nativeQuery not completed: "+e.getMessage(),e);
		}finally{
			if(connection!=null){
				this.jdbcServiceManager.closeConnection(connection);
			}
		}
	
	}
	
}
