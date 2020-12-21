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
package org.openspcoop2.core.allarmi.dao.jdbc;

import org.openspcoop2.generic_project.dao.IDBServiceUtilities;
import org.openspcoop2.generic_project.dao.jdbc.IJDBCServiceSearchWithoutId;
import org.openspcoop2.generic_project.beans.InUse;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.NonNegativeNumber;
import org.openspcoop2.generic_project.beans.UnionExpression;
import org.openspcoop2.generic_project.beans.Union;
import org.openspcoop2.generic_project.beans.FunctionField;
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

import org.openspcoop2.core.allarmi.AllarmeHistory;
import org.openspcoop2.core.allarmi.dao.IDBAllarmeHistoryServiceSearch;
import org.openspcoop2.core.allarmi.utils.ProjectInfo;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.openspcoop2.utils.sql.ISQLQueryObject;

/**     
 * Service can be used to search for the backend objects of type {@link org.openspcoop2.core.allarmi.AllarmeHistory} 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
*/
public class JDBCAllarmeHistoryServiceSearch implements IDBAllarmeHistoryServiceSearch, IDBServiceUtilities<AllarmeHistory> {


	protected JDBCServiceManagerProperties jdbcProperties = null;
	protected JDBCServiceManager jdbcServiceManager = null;
	protected Logger log = null;
	protected IJDBCServiceSearchWithoutId<AllarmeHistory, JDBCServiceManager> serviceSearch = null;
	protected JDBC_SQLObjectFactory jdbcSqlObjectFactory = null;
	public JDBCAllarmeHistoryServiceSearch(JDBCServiceManager jdbcServiceManager) throws ServiceException {
		this.jdbcServiceManager = jdbcServiceManager;
		this.jdbcProperties = jdbcServiceManager.getJdbcProperties();
		this.log = jdbcServiceManager.getLog();
		this.log.debug(JDBCAllarmeHistoryServiceSearch.class.getName()+ " initialized");
		this.serviceSearch = JDBCProperties.getInstance(org.openspcoop2.core.allarmi.dao.jdbc.JDBCServiceManager.class.getPackage(),ProjectInfo.getInstance()).getServiceSearch("allarmeHistory");
		this.serviceSearch.setServiceManager(new JDBCLimitedServiceManager(this.jdbcServiceManager));
		this.jdbcSqlObjectFactory = new JDBC_SQLObjectFactory();
	}
	
	@Override
	public void validate(AllarmeHistory allarmeHistory) throws ServiceException,
			ValidationException, NotImplementedException {
		org.openspcoop2.generic_project.utils.XSDValidator.validate(allarmeHistory, this.log, 
				org.openspcoop2.core.allarmi.utils.XSDValidator.getXSDValidator(this.log));
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
	public List<AllarmeHistory> findAll(IPaginatedExpression expression) throws ServiceException, NotImplementedException {

		Connection connection = null;
		try{
			
			// check parameters
			if(expression==null){
				throw new Exception("Parameter (type:"+IPaginatedExpression.class.getName()+") 'expression' is null");
			}
			if( ! (expression instanceof JDBCPaginatedExpression) ){
				throw new Exception("Parameter (type:"+expression.getClass().getName()+") 'expression' has wrong type, expect "+JDBCPaginatedExpression.class.getName());
			}
			JDBCPaginatedExpression jdbcPaginatedExpression = (JDBCPaginatedExpression) expression;
			this.log.debug("sql = "+jdbcPaginatedExpression.toSql());

			// ISQLQueryObject
			ISQLQueryObject sqlQueryObject = this.jdbcSqlObjectFactory.createSQLQueryObject(this.jdbcProperties.getDatabase());
			sqlQueryObject.setANDLogicOperator(true);
			// Connection sql
			connection = this.jdbcServiceManager.getConnection();

			return this.serviceSearch.findAll(this.jdbcProperties,this.log,connection,sqlQueryObject,jdbcPaginatedExpression,null);			
	
		}catch(ServiceException e){
			this.log.error(e.getMessage(),e); throw e;
		}catch(NotImplementedException e){
			this.log.error(e.getMessage(),e); throw e;
		}catch(Exception e){
			this.log.error(e.getMessage(),e); throw new ServiceException("FindAll not completed: "+e.getMessage(),e);
		}finally{
			if(connection!=null){
				this.jdbcServiceManager.closeConnection(connection);
			}
		}
		
	}
	
	@Override
	public List<AllarmeHistory> findAll(IPaginatedExpression expression, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws ServiceException, NotImplementedException {

		Connection connection = null;
		try{
			
			// check parameters
			if(idMappingResolutionBehaviour==null){
				throw new Exception("Parameter (type:"+org.openspcoop2.generic_project.beans.IDMappingBehaviour.class.getName()+") 'idMappingResolutionBehaviour' is null");
			}
			if(expression==null){
				throw new Exception("Parameter (type:"+IPaginatedExpression.class.getName()+") 'expression' is null");
			}
			if( ! (expression instanceof JDBCPaginatedExpression) ){
				throw new Exception("Parameter (type:"+expression.getClass().getName()+") 'expression' has wrong type, expect "+JDBCPaginatedExpression.class.getName());
			}
			JDBCPaginatedExpression jdbcPaginatedExpression = (JDBCPaginatedExpression) expression;
			this.log.debug("sql = "+jdbcPaginatedExpression.toSql());

			// ISQLQueryObject
			ISQLQueryObject sqlQueryObject = this.jdbcSqlObjectFactory.createSQLQueryObject(this.jdbcProperties.getDatabase());
			sqlQueryObject.setANDLogicOperator(true);
			// Connection sql
			connection = this.jdbcServiceManager.getConnection();

			return this.serviceSearch.findAll(this.jdbcProperties,this.log,connection,sqlQueryObject,jdbcPaginatedExpression,idMappingResolutionBehaviour);			
	
		}catch(ServiceException e){
			this.log.error(e.getMessage(),e); throw e;
		}catch(NotImplementedException e){
			this.log.error(e.getMessage(),e); throw e;
		}catch(Exception e){
			this.log.error(e.getMessage(),e); throw new ServiceException("FindAll not completed: "+e.getMessage(),e);
		}finally{
			if(connection!=null){
				this.jdbcServiceManager.closeConnection(connection);
			}
		}
		
	}

	@Override
	public AllarmeHistory find(IExpression expression) throws ServiceException, NotFoundException, MultipleResultException, NotImplementedException {

		Connection connection = null;
		try{
			
			// check parameters
			if(expression==null){
				throw new Exception("Parameter (type:"+IPaginatedExpression.class.getName()+") 'expression' is null");
			}
			if( ! (expression instanceof JDBCExpression) ){
				throw new Exception("Parameter (type:"+expression.getClass().getName()+") 'expression' has wrong type, expect "+JDBCExpression.class.getName());
			}
			JDBCExpression jdbcExpression = (JDBCExpression) expression;
			this.log.debug("sql = "+jdbcExpression.toSql());

			// ISQLQueryObject
			ISQLQueryObject sqlQueryObject = this.jdbcSqlObjectFactory.createSQLQueryObject(this.jdbcProperties.getDatabase());
			sqlQueryObject.setANDLogicOperator(true);
			// Connection sql
			connection = this.jdbcServiceManager.getConnection();

			return this.serviceSearch.find(this.jdbcProperties,this.log,connection,sqlQueryObject,jdbcExpression,null);			

		}catch(ServiceException e){
			this.log.error(e.getMessage(),e); throw e;
		}catch(NotFoundException e){
			this.log.debug(e.getMessage(),e); throw e;
		}catch(MultipleResultException e){
			this.log.error(e.getMessage(),e); throw e;
		}catch(NotImplementedException e){
			this.log.error(e.getMessage(),e); throw e;
		}catch(Exception e){
			this.log.error(e.getMessage(),e); throw new ServiceException("Find not completed: "+e.getMessage(),e);
		}finally{
			if(connection!=null){
				this.jdbcServiceManager.closeConnection(connection);
			}
		}
		
	}
	
	@Override
	public AllarmeHistory find(IExpression expression, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws ServiceException, NotFoundException, MultipleResultException, NotImplementedException {

		Connection connection = null;
		try{
			
			// check parameters
			if(idMappingResolutionBehaviour==null){
				throw new Exception("Parameter (type:"+org.openspcoop2.generic_project.beans.IDMappingBehaviour.class.getName()+") 'idMappingResolutionBehaviour' is null");
			}
			if(expression==null){
				throw new Exception("Parameter (type:"+IPaginatedExpression.class.getName()+") 'expression' is null");
			}
			if( ! (expression instanceof JDBCExpression) ){
				throw new Exception("Parameter (type:"+expression.getClass().getName()+") 'expression' has wrong type, expect "+JDBCExpression.class.getName());
			}
			JDBCExpression jdbcExpression = (JDBCExpression) expression;
			this.log.debug("sql = "+jdbcExpression.toSql());

			// ISQLQueryObject
			ISQLQueryObject sqlQueryObject = this.jdbcSqlObjectFactory.createSQLQueryObject(this.jdbcProperties.getDatabase());
			sqlQueryObject.setANDLogicOperator(true);
			// Connection sql
			connection = this.jdbcServiceManager.getConnection();

			return this.serviceSearch.find(this.jdbcProperties,this.log,connection,sqlQueryObject,jdbcExpression,idMappingResolutionBehaviour);			

		}catch(ServiceException e){
			this.log.error(e.getMessage(),e); throw e;
		}catch(NotFoundException e){
			this.log.debug(e.getMessage(),e); throw e;
		}catch(MultipleResultException e){
			this.log.error(e.getMessage(),e); throw e;
		}catch(NotImplementedException e){
			this.log.error(e.getMessage(),e); throw e;
		}catch(Exception e){
			this.log.error(e.getMessage(),e); throw new ServiceException("Find not completed: "+e.getMessage(),e);
		}finally{
			if(connection!=null){
				this.jdbcServiceManager.closeConnection(connection);
			}
		}
		
	}

	@Override
	public NonNegativeNumber count(IExpression expression) throws ServiceException, NotImplementedException {

		Connection connection = null;
		try{
			
			// check parameters
			if(expression==null){
				throw new Exception("Parameter (type:"+IPaginatedExpression.class.getName()+") 'expression' is null");
			}
			if( ! (expression instanceof JDBCExpression) ){
				throw new Exception("Parameter (type:"+expression.getClass().getName()+") 'expression' has wrong type, expect "+JDBCExpression.class.getName());
			}
			JDBCExpression jdbcExpression = (JDBCExpression) expression;
			this.log.debug("sql = "+jdbcExpression.toSql());
			
			// ISQLQueryObject
			ISQLQueryObject sqlQueryObject = this.jdbcSqlObjectFactory.createSQLQueryObject(this.jdbcProperties.getDatabase());
			sqlQueryObject.setANDLogicOperator(true);
			// Connection sql
			connection = this.jdbcServiceManager.getConnection();

			return this.serviceSearch.count(this.jdbcProperties,this.log,connection,sqlQueryObject,jdbcExpression);
	
		}catch(ServiceException e){
			this.log.error(e.getMessage(),e); throw e;
		}catch(NotImplementedException e){
			this.log.error(e.getMessage(),e); throw e;
		}catch(Exception e){
			this.log.error(e.getMessage(),e); throw new ServiceException("Count not completed: "+e.getMessage(),e);
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
	public Object aggregate(IExpression expression, FunctionField functionField) throws ServiceException,NotFoundException,NotImplementedException {

		Connection connection = null;
		try{
			
			// check parameters
			if(expression==null){
				throw new Exception("Parameter (type:"+IExpression.class.getName()+") 'expression' is null");
			}
			if( ! (expression instanceof JDBCExpression) ){
				throw new Exception("Parameter (type:"+expression.getClass().getName()+") 'expression' has wrong type, expect "+JDBCExpression.class.getName());
			}
			JDBCExpression jdbcExpression = (JDBCExpression) expression;
			this.log.debug("sql = "+jdbcExpression.toSql());

			// ISQLQueryObject
			ISQLQueryObject sqlQueryObject = this.jdbcSqlObjectFactory.createSQLQueryObject(this.jdbcProperties.getDatabase());
			sqlQueryObject.setANDLogicOperator(true);
			// Connection sql
			connection = this.jdbcServiceManager.getConnection();

			return this.serviceSearch.aggregate(this.jdbcProperties,this.log,connection,sqlQueryObject,jdbcExpression,functionField);			
	
		}catch(ServiceException e){
			this.log.error(e.getMessage(),e); throw e;
		}catch(NotFoundException e){
			this.log.debug(e.getMessage(),e); throw e;
		}catch(NotImplementedException e){
			this.log.error(e.getMessage(),e); throw e;
		}catch(Exception e){
			this.log.error(e.getMessage(),e); throw new ServiceException("Aggregate not completed: "+e.getMessage(),e);
		}finally{
			if(connection!=null){
				this.jdbcServiceManager.closeConnection(connection);
			}
		}
		
	}
	
	@Override
	public Map<String,Object> aggregate(IExpression expression, FunctionField ... functionField) throws ServiceException,NotFoundException,NotImplementedException {

		Connection connection = null;
		try{
			
			// check parameters
			if(expression==null){
				throw new Exception("Parameter (type:"+IExpression.class.getName()+") 'expression' is null");
			}
			if( ! (expression instanceof JDBCExpression) ){
				throw new Exception("Parameter (type:"+expression.getClass().getName()+") 'expression' has wrong type, expect "+JDBCExpression.class.getName());
			}
			JDBCExpression jdbcExpression = (JDBCExpression) expression;
			this.log.debug("sql = "+jdbcExpression.toSql());

			// ISQLQueryObject
			ISQLQueryObject sqlQueryObject = this.jdbcSqlObjectFactory.createSQLQueryObject(this.jdbcProperties.getDatabase());
			sqlQueryObject.setANDLogicOperator(true);
			// Connection sql
			connection = this.jdbcServiceManager.getConnection();

			return this.serviceSearch.aggregate(this.jdbcProperties,this.log,connection,sqlQueryObject,jdbcExpression,functionField);			
	
		}catch(ServiceException e){
			this.log.error(e.getMessage(),e); throw e;
		}catch(NotFoundException e){
			this.log.debug(e.getMessage(),e); throw e;
		}catch(NotImplementedException e){
			this.log.error(e.getMessage(),e); throw e;
		}catch(Exception e){
			this.log.error(e.getMessage(),e); throw new ServiceException("Aggregate not completed: "+e.getMessage(),e);
		}finally{
			if(connection!=null){
				this.jdbcServiceManager.closeConnection(connection);
			}
		}
		
	}
	
	@Override
	public List<Map<String,Object>> groupBy(IExpression expression, FunctionField ... functionField) throws ServiceException,NotFoundException,NotImplementedException {

		Connection connection = null;
		try{
			
			// check parameters
			if(expression==null){
				throw new Exception("Parameter (type:"+IExpression.class.getName()+") 'expression' is null");
			}
			if( ! (expression instanceof JDBCExpression) ){
				throw new Exception("Parameter (type:"+expression.getClass().getName()+") 'expression' has wrong type, expect "+JDBCExpression.class.getName());
			}
			JDBCExpression jdbcExpression = (JDBCExpression) expression;
			this.log.debug("sql = "+jdbcExpression.toSql());

			// ISQLQueryObject
			ISQLQueryObject sqlQueryObject = this.jdbcSqlObjectFactory.createSQLQueryObject(this.jdbcProperties.getDatabase());
			sqlQueryObject.setANDLogicOperator(true);
			// Connection sql
			connection = this.jdbcServiceManager.getConnection();

			return this.serviceSearch.groupBy(this.jdbcProperties,this.log,connection,sqlQueryObject,jdbcExpression,functionField);			
	
		}catch(ServiceException e){
			this.log.error(e.getMessage(),e); throw e;
		}catch(NotFoundException e){
			this.log.debug(e.getMessage(),e); throw e;
		}catch(NotImplementedException e){
			this.log.error(e.getMessage(),e); throw e;
		}catch(Exception e){
			this.log.error(e.getMessage(),e); throw new ServiceException("GroupBy not completed: "+e.getMessage(),e);
		}finally{
			if(connection!=null){
				this.jdbcServiceManager.closeConnection(connection);
			}
		}
		
	}
	
	@Override
	public List<Map<String,Object>> groupBy(IPaginatedExpression paginatedExpression, FunctionField ... functionField) throws ServiceException,NotFoundException,NotImplementedException {

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

			return this.serviceSearch.groupBy(this.jdbcProperties,this.log,connection,sqlQueryObject,jdbcPaginatedExpression,functionField);			
	
		}catch(ServiceException e){
			this.log.error(e.getMessage(),e); throw e;
		}catch(NotFoundException e){
			this.log.debug(e.getMessage(),e); throw e;
		}catch(NotImplementedException e){
			this.log.error(e.getMessage(),e); throw e;
		}catch(Exception e){
			this.log.error(e.getMessage(),e); throw new ServiceException("GroupBy not completed: "+e.getMessage(),e);
		}finally{
			if(connection!=null){
				this.jdbcServiceManager.closeConnection(connection);
			}
		}
		
	}
	
	@Override
	public List<Map<String,Object>> union(Union union, UnionExpression ... unionExpression) throws ServiceException,NotFoundException,NotImplementedException {

		Connection connection = null;
		try{
			
			// check parameters
			if(unionExpression==null){
				throw new Exception("Parameter (type:"+UnionExpression.class.getName()+") 'unionExpression' is null");
			}
			
			// ISQLQueryObject
			ISQLQueryObject sqlQueryObject = this.jdbcSqlObjectFactory.createSQLQueryObject(this.jdbcProperties.getDatabase());
			sqlQueryObject.setANDLogicOperator(true);
			// Connection sql
			connection = this.jdbcServiceManager.getConnection();

			return this.serviceSearch.union(this.jdbcProperties,this.log,connection,sqlQueryObject,union,unionExpression);			
	
		}catch(ServiceException e){
			this.log.error(e.getMessage(),e); throw e;
		}catch(NotFoundException e){
			this.log.debug(e.getMessage(),e); throw e;
		}catch(NotImplementedException e){
			this.log.error(e.getMessage(),e); throw e;
		}catch(Exception e){
			this.log.error(e.getMessage(),e); throw new ServiceException("Union not completed: "+e.getMessage(),e);
		}finally{
			if(connection!=null){
				this.jdbcServiceManager.closeConnection(connection);
			}
		}
		
	}
	
	@Override
	public NonNegativeNumber unionCount(Union union, UnionExpression ... unionExpression) throws ServiceException,NotFoundException,NotImplementedException {

		Connection connection = null;
		try{
			
			// check parameters
			if(unionExpression==null){
				throw new Exception("Parameter (type:"+UnionExpression.class.getName()+") 'unionExpression' is null");
			}
			
			// ISQLQueryObject
			ISQLQueryObject sqlQueryObject = this.jdbcSqlObjectFactory.createSQLQueryObject(this.jdbcProperties.getDatabase());
			sqlQueryObject.setANDLogicOperator(true);
			// Connection sql
			connection = this.jdbcServiceManager.getConnection();

			return this.serviceSearch.unionCount(this.jdbcProperties,this.log,connection,sqlQueryObject,union,unionExpression);			
	
		}catch(ServiceException e){
			this.log.error(e.getMessage(),e); throw e;
		}catch(NotFoundException e){
			this.log.debug(e.getMessage(),e); throw e;
		}catch(NotImplementedException e){
			this.log.error(e.getMessage(),e); throw e;
		}catch(Exception e){
			this.log.error(e.getMessage(),e); throw new ServiceException("UnionCount not completed: "+e.getMessage(),e);
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
	

	// -- DB
	
		
	@Override
	public AllarmeHistory get(long tableId) throws ServiceException, NotFoundException,MultipleResultException, NotImplementedException {
    
		Connection connection = null;
		try{
			
			// check parameters
			if(tableId<=0){
				throw new Exception("Parameter 'tableId' is less equals 0");
			}
			
			// ISQLQueryObject
			ISQLQueryObject sqlQueryObject = this.jdbcSqlObjectFactory.createSQLQueryObject(this.jdbcProperties.getDatabase());
			sqlQueryObject.setANDLogicOperator(true);
			// Connection sql
			connection = this.jdbcServiceManager.getConnection();
		
			return this.serviceSearch.get(this.jdbcProperties,this.log,connection,sqlQueryObject,tableId,null);
		
		}catch(ServiceException e){
			this.log.error(e.getMessage(),e); throw e;
		}catch(NotFoundException e){
			this.log.debug(e.getMessage(),e); throw e;
		}catch(MultipleResultException e){
			this.log.error(e.getMessage(),e); throw e;
		}catch(NotImplementedException e){
			this.log.error(e.getMessage(),e); throw e;
		}catch(Exception e){
			this.log.error(e.getMessage(),e); throw new ServiceException("Get(tableId) not completed: "+e.getMessage(),e);
		}finally{
			if(connection!=null){
				this.jdbcServiceManager.closeConnection(connection);
			}
		}
	
	}
	
	@Override
	public AllarmeHistory get(long tableId,org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws ServiceException, NotFoundException,MultipleResultException, NotImplementedException {
    
		Connection connection = null;
		try{
			
			// check parameters
			if(tableId<=0){
				throw new Exception("Parameter 'tableId' is less equals 0");
			}
			if(idMappingResolutionBehaviour==null){
				throw new Exception("Parameter (type:"+org.openspcoop2.generic_project.beans.IDMappingBehaviour.class.getName()+") 'idMappingResolutionBehaviour' is null");
			}
			
			// ISQLQueryObject
			ISQLQueryObject sqlQueryObject = this.jdbcSqlObjectFactory.createSQLQueryObject(this.jdbcProperties.getDatabase());
			sqlQueryObject.setANDLogicOperator(true);
			// Connection sql
			connection = this.jdbcServiceManager.getConnection();
		
			return this.serviceSearch.get(this.jdbcProperties,this.log,connection,sqlQueryObject,tableId,idMappingResolutionBehaviour);
		
		}catch(ServiceException e){
			this.log.error(e.getMessage(),e); throw e;
		}catch(NotFoundException e){
			this.log.debug(e.getMessage(),e); throw e;
		}catch(MultipleResultException e){
			this.log.error(e.getMessage(),e); throw e;
		}catch(NotImplementedException e){
			this.log.error(e.getMessage(),e); throw e;
		}catch(Exception e){
			this.log.error(e.getMessage(),e); throw new ServiceException("Get(tableId,idMappingResolutionBehaviour) not completed: "+e.getMessage(),e);
		}finally{
			if(connection!=null){
				this.jdbcServiceManager.closeConnection(connection);
			}
		}
	
	}
	
	@Override
	public boolean exists(long tableId) throws MultipleResultException,ServiceException,NotImplementedException {

		Connection connection = null;
		try{
			
			// check parameters
			if(tableId<=0){
				throw new Exception("Parameter 'tableId' is less equals 0");
			}

			// ISQLQueryObject
			ISQLQueryObject sqlQueryObject = this.jdbcSqlObjectFactory.createSQLQueryObject(this.jdbcProperties.getDatabase());
			sqlQueryObject.setANDLogicOperator(true);
			// Connection sql
			connection = this.jdbcServiceManager.getConnection();

			return this.serviceSearch.exists(this.jdbcProperties,this.log,connection,sqlQueryObject,tableId);			
	
		}catch(MultipleResultException e){
			this.log.error(e.getMessage(),e); throw e;
		}catch(ServiceException e){
			this.log.error(e.getMessage(),e); throw e;
		}catch(NotImplementedException e){
			this.log.error(e.getMessage(),e); throw e;
		}catch(Exception e){
			this.log.error(e.getMessage(),e); throw new ServiceException("Exists(tableId) not completed: "+e.getMessage(),e);
		}finally{
			if(connection!=null){
				this.jdbcServiceManager.closeConnection(connection);
			}
		}
		
	}
	
	@Override
	public List<Long> findAllTableIds(IPaginatedExpression expression) throws ServiceException, NotImplementedException {
		
		Connection connection = null;
		try{
			
			// check parameters
			if(expression==null){
				throw new Exception("Parameter (type:"+IPaginatedExpression.class.getName()+") 'expression' is null");
			}
			if( ! (expression instanceof JDBCPaginatedExpression) ){
				throw new Exception("Parameter (type:"+expression.getClass().getName()+") 'expression' has wrong type, expect "+JDBCPaginatedExpression.class.getName());
			}
			JDBCPaginatedExpression jdbcPaginatedExpression = (JDBCPaginatedExpression) expression;
			this.log.debug("sql = "+jdbcPaginatedExpression.toSql());

			// ISQLQueryObject
			ISQLQueryObject sqlQueryObject = this.jdbcSqlObjectFactory.createSQLQueryObject(this.jdbcProperties.getDatabase());
			sqlQueryObject.setANDLogicOperator(true);
			// Connection sql
			connection = this.jdbcServiceManager.getConnection();
			
			return this.serviceSearch.findAllTableIds(this.jdbcProperties,this.log,connection,sqlQueryObject,jdbcPaginatedExpression);
	
		}catch(ServiceException e){
			this.log.error(e.getMessage(),e); throw e;
		}catch(NotImplementedException e){
			this.log.error(e.getMessage(),e); throw e;
		}catch(Exception e){
			this.log.error(e.getMessage(),e); throw new ServiceException("findAllTableIds not completed: "+e.getMessage(),e);
		}finally{
			if(connection!=null){
				this.jdbcServiceManager.closeConnection(connection);
			}
		}
		
	}
	
	@Override
	public long findTableId(IExpression expression) throws ServiceException, NotFoundException, MultipleResultException, NotImplementedException {
	
		Connection connection = null;
		try{
			
			// check parameters
			if(expression==null){
				throw new Exception("Parameter (type:"+IPaginatedExpression.class.getName()+") 'expression' is null");
			}
			if( ! (expression instanceof JDBCExpression) ){
				throw new Exception("Parameter (type:"+expression.getClass().getName()+") 'expression' has wrong type, expect "+JDBCExpression.class.getName());
			}
			JDBCExpression jdbcExpression = (JDBCExpression) expression;
			this.log.debug("sql = "+jdbcExpression.toSql());

			// ISQLQueryObject
			ISQLQueryObject sqlQueryObject = this.jdbcSqlObjectFactory.createSQLQueryObject(this.jdbcProperties.getDatabase());
			sqlQueryObject.setANDLogicOperator(true);
			// Connection sql
			connection = this.jdbcServiceManager.getConnection();

			return this.serviceSearch.findTableId(this.jdbcProperties,this.log,connection,sqlQueryObject,jdbcExpression);			

		}catch(ServiceException e){
			this.log.error(e.getMessage(),e); throw e;
		}catch(NotFoundException e){
			this.log.debug(e.getMessage(),e); throw e;
		}catch(MultipleResultException e){
			this.log.error(e.getMessage(),e); throw e;
		}catch(NotImplementedException e){
			this.log.error(e.getMessage(),e); throw e;
		}catch(Exception e){
			this.log.error(e.getMessage(),e); throw new ServiceException("findTableId not completed: "+e.getMessage(),e);
		}finally{
			if(connection!=null){
				this.jdbcServiceManager.closeConnection(connection);
			}
		}
	
	}
	
	@Override
	public InUse inUse(long tableId) throws ServiceException, NotFoundException, NotImplementedException {
	
		Connection connection = null;
		try{
			
			// check parameters
			if(tableId<=0){
				throw new Exception("Parameter 'tableId' is less equals 0");
			}
			
			// ISQLQueryObject
			ISQLQueryObject sqlQueryObject = this.jdbcSqlObjectFactory.createSQLQueryObject(this.jdbcProperties.getDatabase());
			sqlQueryObject.setANDLogicOperator(true);
			// Connection sql
			connection = this.jdbcServiceManager.getConnection();

			return this.serviceSearch.inUse(this.jdbcProperties,this.log,connection,sqlQueryObject,tableId);		
	
		}catch(ServiceException e){
			this.log.error(e.getMessage(),e); throw e;
		}catch(NotFoundException e){
			this.log.debug(e.getMessage(),e); throw e;
		}catch(NotImplementedException e){
			this.log.error(e.getMessage(),e); throw e;
		}catch(Exception e){
			this.log.error(e.getMessage(),e); throw new ServiceException("InUse(tableId) not completed: "+e.getMessage(),e);
		}finally{
			if(connection!=null){
				this.jdbcServiceManager.closeConnection(connection);
			}
		}
	
	}
	
	
	@Override
	public void disableSelectForUpdate() throws ServiceException,NotImplementedException {
		this.jdbcSqlObjectFactory.setSelectForUpdate(false);
	}

	@Override
	public void enableSelectForUpdate() throws ServiceException,NotImplementedException {
		this.jdbcSqlObjectFactory.setSelectForUpdate(true);
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
