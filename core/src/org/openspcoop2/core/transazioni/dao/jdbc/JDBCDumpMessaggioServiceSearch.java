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
package org.openspcoop2.core.transazioni.dao.jdbc;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.List;
import java.util.Map;

import org.openspcoop2.core.transazioni.DumpMessaggio;
import org.openspcoop2.core.transazioni.IdDumpMessaggio;
import org.openspcoop2.core.transazioni.dao.IDBDumpMessaggioServiceSearch;
import org.openspcoop2.core.transazioni.utils.ProjectInfo;
import org.openspcoop2.generic_project.beans.FunctionField;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.InUse;
import org.openspcoop2.generic_project.beans.NonNegativeNumber;
import org.openspcoop2.generic_project.beans.Union;
import org.openspcoop2.generic_project.beans.UnionExpression;
import org.openspcoop2.generic_project.dao.IDBServiceUtilities;
import org.openspcoop2.generic_project.dao.jdbc.IJDBCServiceSearchWithId;
import org.openspcoop2.generic_project.dao.jdbc.JDBCExpression;
import org.openspcoop2.generic_project.dao.jdbc.JDBCPaginatedExpression;
import org.openspcoop2.generic_project.dao.jdbc.JDBCProperties;
import org.openspcoop2.generic_project.dao.jdbc.JDBCServiceManagerProperties;
import org.openspcoop2.generic_project.dao.jdbc.utils.IJDBCFetch;
import org.openspcoop2.generic_project.dao.jdbc.utils.JDBC_SQLObjectFactory;
import org.openspcoop2.generic_project.exception.ExpressionException;
import org.openspcoop2.generic_project.exception.MultipleResultException;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.generic_project.exception.NotImplementedException;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.generic_project.exception.ValidationException;
import org.openspcoop2.generic_project.expression.IExpression;
import org.openspcoop2.generic_project.expression.IPaginatedExpression;
import org.openspcoop2.generic_project.expression.impl.sql.ISQLFieldConverter;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.slf4j.Logger;

/**     
 * Service can be used to search for the backend objects of type {@link org.openspcoop2.core.transazioni.DumpMessaggio} 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
*/
public class JDBCDumpMessaggioServiceSearch implements IDBDumpMessaggioServiceSearch, IDBServiceUtilities<DumpMessaggio> {


	protected JDBCServiceManagerProperties jdbcProperties = null;
	protected JDBCServiceManager jdbcServiceManager = null;
	protected Logger log = null;
	protected IJDBCServiceSearchWithId<DumpMessaggio, IdDumpMessaggio, JDBCServiceManager> serviceSearch = null;
	protected JDBC_SQLObjectFactory jdbcSqlObjectFactory = null;
	public JDBCDumpMessaggioServiceSearch(JDBCServiceManager jdbcServiceManager) throws ServiceException {
		this.jdbcServiceManager = jdbcServiceManager;
		this.jdbcProperties = jdbcServiceManager.getJdbcProperties();
		this.log = jdbcServiceManager.getLog();
		String msgInit = JDBCDumpMessaggioServiceSearch.class.getName()+ " initialized";
		this.log.debug(msgInit);
		this.serviceSearch = JDBCProperties.getInstance(org.openspcoop2.core.transazioni.dao.jdbc.JDBCServiceManager.class.getPackage(),ProjectInfo.getInstance()).getServiceSearch("dumpMessaggio");
		this.serviceSearch.setServiceManager(new JDBCLimitedServiceManager(this.jdbcServiceManager));
		this.jdbcSqlObjectFactory = new JDBC_SQLObjectFactory();
	}
	
	protected void logError(Exception e) {
		if(e!=null && this.log!=null) {
			this.log.error(e.getMessage(),e);
		}
	}
	protected void logDebug(Exception e) {
		if(e!=null && this.log!=null) {
			this.log.debug(e.getMessage(),e);
		}
	}
	protected void logJDBCExpression(JDBCExpression jdbcExpression) throws ExpressionException{
		if(this.log!=null) {
			String msgDebug = "sql = "+jdbcExpression.toSql();
			this.log.debug(msgDebug);
		}
	}
	protected void logJDBCPaginatedExpression(JDBCPaginatedExpression jdbcPaginatedExpression) throws ExpressionException{
		if(this.log!=null) {
			String msgDebug = "sql = "+jdbcPaginatedExpression.toSql();
			this.log.debug(msgDebug);
		}
	}
	
	private static final String PARAMETER_TYPE_PREFIX = "Parameter (type:";
		
	private ServiceException newServiceExceptionParameterObjIsNull(){
		return new ServiceException(PARAMETER_TYPE_PREFIX+DumpMessaggio.class.getName()+") 'obj' is null");
	}
	protected ServiceException newServiceExceptionParameterIdIsNull(){
		return new ServiceException(PARAMETER_TYPE_PREFIX+IdDumpMessaggio.class.getName()+") 'id' is null");
	}
	private ServiceException newServiceExceptionParameterIdMappingResolutionBehaviourIsNull(){
		return new ServiceException(PARAMETER_TYPE_PREFIX+org.openspcoop2.generic_project.beans.IDMappingBehaviour.class.getName()+") 'idMappingResolutionBehaviour' is null");
	}

	protected ServiceException newServiceExceptionParameterExpressionWrongType(IExpression expression){
		return new ServiceException(PARAMETER_TYPE_PREFIX+expression.getClass().getName()+") 'expression' has wrong type, expect "+JDBCExpression.class.getName());
	}
	protected ServiceException newServiceExceptionParameterExpressionIsNull(){
		return new ServiceException(PARAMETER_TYPE_PREFIX+IExpression.class.getName()+") 'expression' is null");
	}
	
	private ServiceException newServiceExceptionParameterPaginatedExpressionIsNull(){
		return new ServiceException(PARAMETER_TYPE_PREFIX+IPaginatedExpression.class.getName()+") 'expression' is null");
	}
	private ServiceException newServiceExceptionParameterPaginatedExpressionIsNullErrorParameterPaginated(){
		return new ServiceException(PARAMETER_TYPE_PREFIX+IPaginatedExpression.class.getName()+") 'paginatedExpression' is null");
	}
	private ServiceException newServiceExceptionParameterPaginatedExpressionWrongType(IPaginatedExpression expression){
		return new ServiceException(PARAMETER_TYPE_PREFIX+expression.getClass().getName()+") 'expression' has wrong type, expect "+JDBCPaginatedExpression.class.getName());
	}
	private ServiceException newServiceExceptionParameterPaginatedExpressionWrongTypeErrorParameterPaginated(IPaginatedExpression paginatedExpression){
		return new ServiceException(PARAMETER_TYPE_PREFIX+paginatedExpression.getClass().getName()+") 'paginatedExpression' has wrong type, expect "+JDBCPaginatedExpression.class.getName());
	}
	
	private ServiceException newServiceExceptionParameterUnionExpressionIsNull(){
		return new ServiceException(PARAMETER_TYPE_PREFIX+UnionExpression.class.getName()+") 'unionExpression' is null");
	}
	
	private ServiceException newServiceExceptionParameterWithTypeTableIdLessEqualsZero(){
		return new ServiceException(PARAMETER_TYPE_PREFIX+IdDumpMessaggio.class.getName()+") 'tableId' is lessEquals 0");
	}
	private ServiceException newServiceExceptionParameterTableIdLessEqualsZero(){
		return new ServiceException("Parameter 'tableId' is less equals 0");
	}
	
	@Override
	public void validate(DumpMessaggio dumpMessaggio) throws ServiceException,
			ValidationException, NotImplementedException {
		org.openspcoop2.generic_project.utils.XSDValidator.validate(dumpMessaggio, this.log, 
				org.openspcoop2.core.transazioni.utils.XSDValidator.getXSDValidator(this.log));
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
	public IdDumpMessaggio convertToId(DumpMessaggio obj)
			throws ServiceException, NotImplementedException {
		
		Connection connection = null;
		try{
			
			// check parameters
			if(obj==null){
				throw this.newServiceExceptionParameterObjIsNull();
			}
			
			// ISQLQueryObject
			ISQLQueryObject sqlQueryObject = this.jdbcSqlObjectFactory.createSQLQueryObject(this.jdbcProperties.getDatabase());
			sqlQueryObject.setANDLogicOperator(true);
			// Connection sql
			connection = this.jdbcServiceManager.getConnection();
		
			return this.serviceSearch.convertToId(this.jdbcProperties,this.log,connection,sqlQueryObject,obj);
		
		}catch(ServiceException | NotImplementedException e){
			this.logError(e); throw e;
		}catch(Exception e){
			this.logError(e); throw new ServiceException("ConvertToId not completed: "+e.getMessage(),e);
		}finally{
			if(connection!=null){
				this.jdbcServiceManager.closeConnection(connection);
			}
		}
		
	}
		
	@Override
	public DumpMessaggio get(IdDumpMessaggio id) throws ServiceException, NotFoundException,MultipleResultException, NotImplementedException {
    
		Connection connection = null;
		try{
			
			// check parameters
			if(id==null){
				throw this.newServiceExceptionParameterIdIsNull();
			}
			
			// ISQLQueryObject
			ISQLQueryObject sqlQueryObject = this.jdbcSqlObjectFactory.createSQLQueryObject(this.jdbcProperties.getDatabase());
			sqlQueryObject.setANDLogicOperator(true);
			// Connection sql
			connection = this.jdbcServiceManager.getConnection();
		
			return this.serviceSearch.get(this.jdbcProperties,this.log,connection,sqlQueryObject,id,null);
		
		}catch(ServiceException | MultipleResultException | NotImplementedException e){
			this.logError(e); throw e;
		}catch(NotFoundException e){
			this.logDebug(e); throw e;
		}catch(Exception e){
			this.logError(e); throw new ServiceException("Get not completed: "+e.getMessage(),e);
		}finally{
			if(connection!=null){
				this.jdbcServiceManager.closeConnection(connection);
			}
		}
	
	}

	@Override
	public DumpMessaggio get(IdDumpMessaggio id, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws ServiceException, NotFoundException,MultipleResultException, NotImplementedException {
		Connection connection = null;
		try{
			
			// check parameters
			if(id==null){
				throw this.newServiceExceptionParameterIdIsNull();
			}
			if(idMappingResolutionBehaviour==null){
				throw this.newServiceExceptionParameterIdMappingResolutionBehaviourIsNull();
			}
			
			// ISQLQueryObject
			ISQLQueryObject sqlQueryObject = this.jdbcSqlObjectFactory.createSQLQueryObject(this.jdbcProperties.getDatabase());
			sqlQueryObject.setANDLogicOperator(true);
			// Connection sql
			connection = this.jdbcServiceManager.getConnection();
		
			return this.serviceSearch.get(this.jdbcProperties,this.log,connection,sqlQueryObject,id,idMappingResolutionBehaviour);
		
		}catch(ServiceException | MultipleResultException | NotImplementedException e){
			this.logError(e); throw e;
		}catch(NotFoundException e){
			this.logDebug(e); throw e;
		}catch(Exception e){
			this.logError(e); throw new ServiceException("Get (idMappingResolutionBehaviour) not completed: "+e.getMessage(),e);
		}finally{
			if(connection!=null){
				this.jdbcServiceManager.closeConnection(connection);
			}
		}
	
	}

	@Override
	public boolean exists(IdDumpMessaggio id) throws MultipleResultException,ServiceException,NotImplementedException {

		Connection connection = null;
		try{
			
			// check parameters
			if(id==null){
				throw this.newServiceExceptionParameterIdIsNull();
			}

			// ISQLQueryObject
			ISQLQueryObject sqlQueryObject = this.jdbcSqlObjectFactory.createSQLQueryObject(this.jdbcProperties.getDatabase());
			sqlQueryObject.setANDLogicOperator(true);
			// Connection sql
			connection = this.jdbcServiceManager.getConnection();

			return this.serviceSearch.exists(this.jdbcProperties,this.log,connection,sqlQueryObject,id);
	
		}catch(MultipleResultException | ServiceException | NotImplementedException e){
			this.logError(e); throw e;
		}catch(Exception e){
			this.logError(e); throw new ServiceException("Exists not completed: "+e.getMessage(),e);
		}finally{
			if(connection!=null){
				this.jdbcServiceManager.closeConnection(connection);
			}
		}
		
	}
	
	@Override
	public List<IdDumpMessaggio> findAllIds(IPaginatedExpression expression) throws ServiceException, NotImplementedException {

		Connection connection = null;
		try{
			
			// check parameters
			if(expression==null){
				throw this.newServiceExceptionParameterPaginatedExpressionIsNull();
			}
			if( ! (expression instanceof JDBCPaginatedExpression) ){
				throw this.newServiceExceptionParameterPaginatedExpressionWrongType(expression);
			}
			JDBCPaginatedExpression jdbcPaginatedExpression = (JDBCPaginatedExpression) expression;
			logJDBCPaginatedExpression(jdbcPaginatedExpression);

			// ISQLQueryObject
			ISQLQueryObject sqlQueryObject = this.jdbcSqlObjectFactory.createSQLQueryObject(this.jdbcProperties.getDatabase());
			sqlQueryObject.setANDLogicOperator(true);
			// Connection sql
			connection = this.jdbcServiceManager.getConnection();
			
			return this.serviceSearch.findAllIds(this.jdbcProperties,this.log,connection,sqlQueryObject,jdbcPaginatedExpression,null);
	
		}catch(ServiceException | NotImplementedException e){
			this.logError(e); throw e;
		}catch(Exception e){
			this.logError(e); throw new ServiceException("FindAllIds not completed: "+e.getMessage(),e);
		}finally{
			if(connection!=null){
				this.jdbcServiceManager.closeConnection(connection);
			}
		}
		
	}
	
	@Override
	public List<IdDumpMessaggio> findAllIds(IPaginatedExpression expression, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws ServiceException, NotImplementedException {

		Connection connection = null;
		try{
			
			// check parameters
			if(idMappingResolutionBehaviour==null){
				throw this.newServiceExceptionParameterIdMappingResolutionBehaviourIsNull();
			}
			if(expression==null){
				throw this.newServiceExceptionParameterPaginatedExpressionIsNull();
			}
			if( ! (expression instanceof JDBCPaginatedExpression) ){
				throw this.newServiceExceptionParameterPaginatedExpressionWrongType(expression);
			}
			JDBCPaginatedExpression jdbcPaginatedExpression = (JDBCPaginatedExpression) expression;
			logJDBCPaginatedExpression(jdbcPaginatedExpression);

			// ISQLQueryObject
			ISQLQueryObject sqlQueryObject = this.jdbcSqlObjectFactory.createSQLQueryObject(this.jdbcProperties.getDatabase());
			sqlQueryObject.setANDLogicOperator(true);
			// Connection sql
			connection = this.jdbcServiceManager.getConnection();
			
			return this.serviceSearch.findAllIds(this.jdbcProperties,this.log,connection,sqlQueryObject,jdbcPaginatedExpression,idMappingResolutionBehaviour);
	
		}catch(ServiceException | NotImplementedException e){
			this.logError(e); throw e;
		}catch(Exception e){
			this.logError(e); throw new ServiceException("FindAllIds not completed: "+e.getMessage(),e);
		}finally{
			if(connection!=null){
				this.jdbcServiceManager.closeConnection(connection);
			}
		}
		
	}

	@Override
	public List<DumpMessaggio> findAll(IPaginatedExpression expression) throws ServiceException, NotImplementedException {

		Connection connection = null;
		try{
			
			// check parameters
			if(expression==null){
				throw this.newServiceExceptionParameterPaginatedExpressionIsNull();
			}
			if( ! (expression instanceof JDBCPaginatedExpression) ){
				throw this.newServiceExceptionParameterPaginatedExpressionWrongType(expression);
			}
			JDBCPaginatedExpression jdbcPaginatedExpression = (JDBCPaginatedExpression) expression;
			logJDBCPaginatedExpression(jdbcPaginatedExpression);

			// ISQLQueryObject
			ISQLQueryObject sqlQueryObject = this.jdbcSqlObjectFactory.createSQLQueryObject(this.jdbcProperties.getDatabase());
			sqlQueryObject.setANDLogicOperator(true);
			// Connection sql
			connection = this.jdbcServiceManager.getConnection();

			return this.serviceSearch.findAll(this.jdbcProperties,this.log,connection,sqlQueryObject,jdbcPaginatedExpression,null);			
	
		}catch(ServiceException | NotImplementedException e){
			this.logError(e); throw e;
		}catch(Exception e){
			this.logError(e); throw new ServiceException("FindAll not completed: "+e.getMessage(),e);
		}finally{
			if(connection!=null){
				this.jdbcServiceManager.closeConnection(connection);
			}
		}
		
	}
	
	@Override
	public List<DumpMessaggio> findAll(IPaginatedExpression expression, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws ServiceException, NotImplementedException {

		Connection connection = null;
		try{
			
			// check parameters
			if(idMappingResolutionBehaviour==null){
				throw this.newServiceExceptionParameterIdMappingResolutionBehaviourIsNull();
			}
			if(expression==null){
				throw this.newServiceExceptionParameterPaginatedExpressionIsNull();
			}
			if( ! (expression instanceof JDBCPaginatedExpression) ){
				throw this.newServiceExceptionParameterPaginatedExpressionWrongType(expression);
			}
			JDBCPaginatedExpression jdbcPaginatedExpression = (JDBCPaginatedExpression) expression;
			logJDBCPaginatedExpression(jdbcPaginatedExpression);

			// ISQLQueryObject
			ISQLQueryObject sqlQueryObject = this.jdbcSqlObjectFactory.createSQLQueryObject(this.jdbcProperties.getDatabase());
			sqlQueryObject.setANDLogicOperator(true);
			// Connection sql
			connection = this.jdbcServiceManager.getConnection();

			return this.serviceSearch.findAll(this.jdbcProperties,this.log,connection,sqlQueryObject,jdbcPaginatedExpression,idMappingResolutionBehaviour);			
	
		}catch(ServiceException | NotImplementedException e){
			this.logError(e); throw e;
		}catch(Exception e){
			this.logError(e); throw new ServiceException("FindAll not completed: "+e.getMessage(),e);
		}finally{
			if(connection!=null){
				this.jdbcServiceManager.closeConnection(connection);
			}
		}
		
	}

	@Override
	public DumpMessaggio find(IExpression expression) throws ServiceException, NotFoundException, MultipleResultException, NotImplementedException {

		Connection connection = null;
		try{
			
			// check parameters
			if(expression==null){
				throw this.newServiceExceptionParameterExpressionIsNull();
			}
			if( ! (expression instanceof JDBCExpression) ){
				throw this.newServiceExceptionParameterExpressionWrongType(expression);
			}
			JDBCExpression jdbcExpression = (JDBCExpression) expression;
			this.logJDBCExpression(jdbcExpression);

			// ISQLQueryObject
			ISQLQueryObject sqlQueryObject = this.jdbcSqlObjectFactory.createSQLQueryObject(this.jdbcProperties.getDatabase());
			sqlQueryObject.setANDLogicOperator(true);
			// Connection sql
			connection = this.jdbcServiceManager.getConnection();

			return this.serviceSearch.find(this.jdbcProperties,this.log,connection,sqlQueryObject,jdbcExpression,null);			

		}catch(ServiceException | MultipleResultException | NotImplementedException e){
			this.logError(e); throw e;
		}catch(NotFoundException e){
			this.logDebug(e); throw e;
		}catch(Exception e){
			this.logError(e); throw new ServiceException("Find not completed: "+e.getMessage(),e);
		}finally{
			if(connection!=null){
				this.jdbcServiceManager.closeConnection(connection);
			}
		}
		
	}
	
	@Override
	public DumpMessaggio find(IExpression expression, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws ServiceException, NotFoundException, MultipleResultException, NotImplementedException {

		Connection connection = null;
		try{
			
			// check parameters
			if(idMappingResolutionBehaviour==null){
				throw this.newServiceExceptionParameterIdMappingResolutionBehaviourIsNull();
			}
			if(expression==null){
				throw this.newServiceExceptionParameterExpressionIsNull();
			}
			if( ! (expression instanceof JDBCExpression) ){
				throw this.newServiceExceptionParameterExpressionWrongType(expression);
			}
			JDBCExpression jdbcExpression = (JDBCExpression) expression;
			this.logJDBCExpression(jdbcExpression);

			// ISQLQueryObject
			ISQLQueryObject sqlQueryObject = this.jdbcSqlObjectFactory.createSQLQueryObject(this.jdbcProperties.getDatabase());
			sqlQueryObject.setANDLogicOperator(true);
			// Connection sql
			connection = this.jdbcServiceManager.getConnection();

			return this.serviceSearch.find(this.jdbcProperties,this.log,connection,sqlQueryObject,jdbcExpression,idMappingResolutionBehaviour);			

		}catch(ServiceException | MultipleResultException | NotImplementedException e){
			this.logError(e); throw e;
		}catch(NotFoundException e){
			this.logDebug(e); throw e;
		}catch(Exception e){
			this.logError(e); throw new ServiceException("Find not completed: "+e.getMessage(),e);
		}finally{
			if(connection!=null){
				this.jdbcServiceManager.closeConnection(connection);
			}
		}
		
	}

	@Override
	public JDBCDumpMessaggioStream getContentInputStream(IExpression expression) 
			throws ServiceException {
		
		Connection connection = null;
		try{
			
			// check parameters
			if(expression==null){
				throw newServiceExceptionParameterPaginatedExpressionIsNull();
			}
			if( ! (expression instanceof JDBCExpression) ){
				throw newServiceExceptionParameterExpressionWrongType(expression);
			}
			JDBCExpression jdbcExpression = (JDBCExpression) expression;
			logJDBCExpression(jdbcExpression);

			// ISQLQueryObject
			ISQLQueryObject sqlQueryObject = this.jdbcSqlObjectFactory.createSQLQueryObject(this.jdbcProperties.getDatabase());
			sqlQueryObject.setANDLogicOperator(true);
			// Connection sql
			connection = this.jdbcServiceManager.getConnection();

			Method method = this.serviceSearch.getClass().getMethod("getContentInputStream", 
					JDBCServiceManagerProperties.class, Logger.class, 
					Connection.class, JDBCServiceManager.class, 
					ISQLQueryObject.class, JDBCExpression.class);
			return (JDBCDumpMessaggioStream) method.invoke(this.serviceSearch, 
					this.jdbcProperties,this.log, 
					connection, this.jdbcServiceManager, 
					sqlQueryObject,jdbcExpression);			

		}catch(ServiceException e){
			this.logError(e); throw e;
		}catch(Exception e){
			this.logError(e); throw new ServiceException("Find not completed: "+e.getMessage(),e);
		}finally{
			/**
			 * OP-1635
			if(connection!=null){
				this.jdbcServiceManager.closeConnection(connection);
			}
			*/
		}
		
	}

	@Override
	public NonNegativeNumber count(IExpression expression) throws ServiceException, NotImplementedException {

		Connection connection = null;
		try{
			
			// check parameters
			if(expression==null){
				throw this.newServiceExceptionParameterExpressionIsNull();
			}
			if( ! (expression instanceof JDBCExpression) ){
				throw this.newServiceExceptionParameterExpressionWrongType(expression);
			}
			JDBCExpression jdbcExpression = (JDBCExpression) expression;
			this.logJDBCExpression(jdbcExpression);
			
			// ISQLQueryObject
			ISQLQueryObject sqlQueryObject = this.jdbcSqlObjectFactory.createSQLQueryObject(this.jdbcProperties.getDatabase());
			sqlQueryObject.setANDLogicOperator(true);
			// Connection sql
			connection = this.jdbcServiceManager.getConnection();

			return this.serviceSearch.count(this.jdbcProperties,this.log,connection,sqlQueryObject,jdbcExpression);
	
		}catch(ServiceException | NotImplementedException e){
			this.logError(e); throw e;
		}catch(Exception e){
			this.logError(e); throw new ServiceException("Count not completed: "+e.getMessage(),e);
		}finally{
			if(connection!=null){
				this.jdbcServiceManager.closeConnection(connection);
			}
		}
		
	}

	@Override
	public InUse inUse(IdDumpMessaggio id) throws ServiceException, NotFoundException,NotImplementedException {

		Connection connection = null;
		try{
			
			// check parameters
			if(id==null){
				throw this.newServiceExceptionParameterIdIsNull();
			}
			
			// ISQLQueryObject
			ISQLQueryObject sqlQueryObject = this.jdbcSqlObjectFactory.createSQLQueryObject(this.jdbcProperties.getDatabase());
			sqlQueryObject.setANDLogicOperator(true);
			// Connection sql
			connection = this.jdbcServiceManager.getConnection();

			return this.serviceSearch.inUse(this.jdbcProperties,this.log,connection,sqlQueryObject,id);	
	
		}catch(ServiceException | NotImplementedException e){
			this.logError(e); throw e;
		}catch(NotFoundException e){
			this.logDebug(e); throw e;
		}catch(Exception e){
			this.logError(e); throw new ServiceException("InUse not completed: "+e.getMessage(),e);
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
				throw this.newServiceExceptionParameterPaginatedExpressionIsNullErrorParameterPaginated();
			}
			if( ! (paginatedExpression instanceof JDBCPaginatedExpression) ){
				throw this.newServiceExceptionParameterPaginatedExpressionWrongTypeErrorParameterPaginated(paginatedExpression);
			}
			JDBCPaginatedExpression jdbcPaginatedExpression = (JDBCPaginatedExpression) paginatedExpression;
			logJDBCPaginatedExpression(jdbcPaginatedExpression);

			// ISQLQueryObject
			ISQLQueryObject sqlQueryObject = this.jdbcSqlObjectFactory.createSQLQueryObject(this.jdbcProperties.getDatabase());
			sqlQueryObject.setANDLogicOperator(true);
			// Connection sql
			connection = this.jdbcServiceManager.getConnection();

			return this.serviceSearch.select(this.jdbcProperties,this.log,connection,sqlQueryObject,jdbcPaginatedExpression,field);			
	
		}catch(ServiceException | NotImplementedException e){
			this.logError(e); throw e;
		}catch(NotFoundException e){
			this.logDebug(e); throw e;
		}catch(Exception e){
			this.logError(e); throw new ServiceException("Select 'field' not completed: "+e.getMessage(),e);
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
				throw this.newServiceExceptionParameterPaginatedExpressionIsNullErrorParameterPaginated();
			}
			if( ! (paginatedExpression instanceof JDBCPaginatedExpression) ){
				throw this.newServiceExceptionParameterPaginatedExpressionWrongTypeErrorParameterPaginated(paginatedExpression);
			}
			JDBCPaginatedExpression jdbcPaginatedExpression = (JDBCPaginatedExpression) paginatedExpression;
			logJDBCPaginatedExpression(jdbcPaginatedExpression);

			// ISQLQueryObject
			ISQLQueryObject sqlQueryObject = this.jdbcSqlObjectFactory.createSQLQueryObject(this.jdbcProperties.getDatabase());
			sqlQueryObject.setANDLogicOperator(true);
			// Connection sql
			connection = this.jdbcServiceManager.getConnection();

			return this.serviceSearch.select(this.jdbcProperties,this.log,connection,sqlQueryObject,jdbcPaginatedExpression,distinct,field);			
	
		}catch(ServiceException | NotImplementedException e){
			this.logError(e); throw e;
		}catch(NotFoundException e){
			this.logDebug(e); throw e;
		}catch(Exception e){
			this.logError(e); throw new ServiceException("Select 'distinct:"+distinct+"' field not completed: "+e.getMessage(),e);
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
				throw this.newServiceExceptionParameterPaginatedExpressionIsNullErrorParameterPaginated();
			}
			if( ! (paginatedExpression instanceof JDBCPaginatedExpression) ){
				throw this.newServiceExceptionParameterPaginatedExpressionWrongTypeErrorParameterPaginated(paginatedExpression);
			}
			JDBCPaginatedExpression jdbcPaginatedExpression = (JDBCPaginatedExpression) paginatedExpression;
			logJDBCPaginatedExpression(jdbcPaginatedExpression);

			// ISQLQueryObject
			ISQLQueryObject sqlQueryObject = this.jdbcSqlObjectFactory.createSQLQueryObject(this.jdbcProperties.getDatabase());
			sqlQueryObject.setANDLogicOperator(true);
			// Connection sql
			connection = this.jdbcServiceManager.getConnection();

			return this.serviceSearch.select(this.jdbcProperties,this.log,connection,sqlQueryObject,jdbcPaginatedExpression,field);			
	
		}catch(ServiceException | NotImplementedException e){
			this.logError(e); throw e;
		}catch(NotFoundException e){
			this.logDebug(e); throw e;
		}catch(Exception e){
			this.logError(e); throw new ServiceException("Select not completed: "+e.getMessage(),e);
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
				throw this.newServiceExceptionParameterPaginatedExpressionIsNullErrorParameterPaginated();
			}
			if( ! (paginatedExpression instanceof JDBCPaginatedExpression) ){
				throw this.newServiceExceptionParameterPaginatedExpressionWrongTypeErrorParameterPaginated(paginatedExpression);
			}
			JDBCPaginatedExpression jdbcPaginatedExpression = (JDBCPaginatedExpression) paginatedExpression;
			logJDBCPaginatedExpression(jdbcPaginatedExpression);

			// ISQLQueryObject
			ISQLQueryObject sqlQueryObject = this.jdbcSqlObjectFactory.createSQLQueryObject(this.jdbcProperties.getDatabase());
			sqlQueryObject.setANDLogicOperator(true);
			// Connection sql
			connection = this.jdbcServiceManager.getConnection();

			return this.serviceSearch.select(this.jdbcProperties,this.log,connection,sqlQueryObject,jdbcPaginatedExpression,distinct,field);			
	
		}catch(ServiceException | NotImplementedException e){
			this.logError(e); throw e;
		}catch(NotFoundException e){
			this.logDebug(e); throw e;
		}catch(Exception e){
			this.logError(e); throw new ServiceException("Select distinct:"+distinct+" not completed: "+e.getMessage(),e);
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
				throw this.newServiceExceptionParameterExpressionIsNull();
			}
			if( ! (expression instanceof JDBCExpression) ){
				throw this.newServiceExceptionParameterExpressionWrongType(expression);
			}
			JDBCExpression jdbcExpression = (JDBCExpression) expression;
			this.logJDBCExpression(jdbcExpression);

			// ISQLQueryObject
			ISQLQueryObject sqlQueryObject = this.jdbcSqlObjectFactory.createSQLQueryObject(this.jdbcProperties.getDatabase());
			sqlQueryObject.setANDLogicOperator(true);
			// Connection sql
			connection = this.jdbcServiceManager.getConnection();

			return this.serviceSearch.aggregate(this.jdbcProperties,this.log,connection,sqlQueryObject,jdbcExpression,functionField);			
	
		}catch(ServiceException | NotImplementedException e){
			this.logError(e); throw e;
		}catch(NotFoundException e){
			this.logDebug(e); throw e;
		}catch(Exception e){
			this.logError(e); throw new ServiceException("Aggregate not completed: "+e.getMessage(),e);
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
				throw this.newServiceExceptionParameterExpressionIsNull();
			}
			if( ! (expression instanceof JDBCExpression) ){
				throw this.newServiceExceptionParameterExpressionWrongType(expression);
			}
			JDBCExpression jdbcExpression = (JDBCExpression) expression;
			this.logJDBCExpression(jdbcExpression);

			// ISQLQueryObject
			ISQLQueryObject sqlQueryObject = this.jdbcSqlObjectFactory.createSQLQueryObject(this.jdbcProperties.getDatabase());
			sqlQueryObject.setANDLogicOperator(true);
			// Connection sql
			connection = this.jdbcServiceManager.getConnection();

			return this.serviceSearch.aggregate(this.jdbcProperties,this.log,connection,sqlQueryObject,jdbcExpression,functionField);			
	
		}catch(ServiceException | NotImplementedException e){
			this.logError(e); throw e;
		}catch(NotFoundException e){
			this.logDebug(e); throw e;
		}catch(Exception e){
			this.logError(e); throw new ServiceException("Aggregate not completed: "+e.getMessage(),e);
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
				throw this.newServiceExceptionParameterExpressionIsNull();
			}
			if( ! (expression instanceof JDBCExpression) ){
				throw this.newServiceExceptionParameterExpressionWrongType(expression);
			}
			JDBCExpression jdbcExpression = (JDBCExpression) expression;
			this.logJDBCExpression(jdbcExpression);

			// ISQLQueryObject
			ISQLQueryObject sqlQueryObject = this.jdbcSqlObjectFactory.createSQLQueryObject(this.jdbcProperties.getDatabase());
			sqlQueryObject.setANDLogicOperator(true);
			// Connection sql
			connection = this.jdbcServiceManager.getConnection();

			return this.serviceSearch.groupBy(this.jdbcProperties,this.log,connection,sqlQueryObject,jdbcExpression,functionField);			
	
		}catch(ServiceException | NotImplementedException e){
			this.logError(e); throw e;
		}catch(NotFoundException e){
			this.logDebug(e); throw e;
		}catch(Exception e){
			this.logError(e); throw new ServiceException("GroupBy not completed: "+e.getMessage(),e);
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
				throw this.newServiceExceptionParameterPaginatedExpressionIsNullErrorParameterPaginated();
			}
			if( ! (paginatedExpression instanceof JDBCPaginatedExpression) ){
				throw this.newServiceExceptionParameterPaginatedExpressionWrongTypeErrorParameterPaginated(paginatedExpression);
			}
			JDBCPaginatedExpression jdbcPaginatedExpression = (JDBCPaginatedExpression) paginatedExpression;
			logJDBCPaginatedExpression(jdbcPaginatedExpression);

			// ISQLQueryObject
			ISQLQueryObject sqlQueryObject = this.jdbcSqlObjectFactory.createSQLQueryObject(this.jdbcProperties.getDatabase());
			sqlQueryObject.setANDLogicOperator(true);
			// Connection sql
			connection = this.jdbcServiceManager.getConnection();

			return this.serviceSearch.groupBy(this.jdbcProperties,this.log,connection,sqlQueryObject,jdbcPaginatedExpression,functionField);			
	
		}catch(ServiceException | NotImplementedException e){
			this.logError(e); throw e;
		}catch(NotFoundException e){
			this.logDebug(e); throw e;
		}catch(Exception e){
			this.logError(e); throw new ServiceException("GroupBy not completed: "+e.getMessage(),e);
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
				throw this.newServiceExceptionParameterUnionExpressionIsNull();
			}
			
			// ISQLQueryObject
			ISQLQueryObject sqlQueryObject = this.jdbcSqlObjectFactory.createSQLQueryObject(this.jdbcProperties.getDatabase());
			sqlQueryObject.setANDLogicOperator(true);
			// Connection sql
			connection = this.jdbcServiceManager.getConnection();

			return this.serviceSearch.union(this.jdbcProperties,this.log,connection,sqlQueryObject,union,unionExpression);			
	
		}catch(ServiceException | NotImplementedException e){
			this.logError(e); throw e;
		}catch(NotFoundException e){
			this.logDebug(e); throw e;
		}catch(Exception e){
			this.logError(e); throw new ServiceException("Union not completed: "+e.getMessage(),e);
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
				throw this.newServiceExceptionParameterUnionExpressionIsNull();
			}
			
			// ISQLQueryObject
			ISQLQueryObject sqlQueryObject = this.jdbcSqlObjectFactory.createSQLQueryObject(this.jdbcProperties.getDatabase());
			sqlQueryObject.setANDLogicOperator(true);
			// Connection sql
			connection = this.jdbcServiceManager.getConnection();

			return this.serviceSearch.unionCount(this.jdbcProperties,this.log,connection,sqlQueryObject,union,unionExpression);			
	
		}catch(ServiceException | NotImplementedException e){
			this.logError(e); throw e;
		}catch(NotFoundException e){
			this.logDebug(e); throw e;
		}catch(Exception e){
			this.logError(e); throw new ServiceException("UnionCount not completed: "+e.getMessage(),e);
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
	public void mappingTableIds(IdDumpMessaggio id, DumpMessaggio obj) throws ServiceException,NotFoundException,NotImplementedException{
		Connection connection = null;
		try{
			
			// check parameters
			if(id==null){
				throw this.newServiceExceptionParameterIdIsNull();
			}
			if(obj==null){
				throw this.newServiceExceptionParameterObjIsNull();
			}
			
			// ISQLQueryObject
			ISQLQueryObject sqlQueryObject = this.jdbcSqlObjectFactory.createSQLQueryObject(this.jdbcProperties.getDatabase());
			sqlQueryObject.setANDLogicOperator(true);
			// Connection sql
			connection = this.jdbcServiceManager.getConnection();
		
			this.serviceSearch.mappingTableIds(this.jdbcProperties,this.log,connection,sqlQueryObject,id,obj);
		
		}catch(ServiceException | NotImplementedException e){
			this.logError(e); throw e;
		}catch(NotFoundException e){
			this.logDebug(e); throw e;
		}catch(Exception e){
			this.logError(e); throw new ServiceException("mappingIds(IdObject) not completed: "+e.getMessage(),e);
		}finally{
			if(connection!=null){
				this.jdbcServiceManager.closeConnection(connection);
			}
		}
	}
	
	@Override
	public void mappingTableIds(long tableId, DumpMessaggio obj) throws ServiceException,NotFoundException,NotImplementedException{
		Connection connection = null;
		try{
			
			// check parameters
			if(tableId<=0){
				throw this.newServiceExceptionParameterWithTypeTableIdLessEqualsZero();
			}
			if(obj==null){
				throw this.newServiceExceptionParameterObjIsNull();
			}
			
			// ISQLQueryObject
			ISQLQueryObject sqlQueryObject = this.jdbcSqlObjectFactory.createSQLQueryObject(this.jdbcProperties.getDatabase());
			sqlQueryObject.setANDLogicOperator(true);
			// Connection sql
			connection = this.jdbcServiceManager.getConnection();
		
			this.serviceSearch.mappingTableIds(this.jdbcProperties,this.log,connection,sqlQueryObject,tableId,obj);
		
		}catch(ServiceException | NotImplementedException e){
			this.logError(e); throw e;
		}catch(NotFoundException e){
			this.logDebug(e); throw e;
		}catch(Exception e){
			this.logError(e); throw new ServiceException("mappingIds(tableId) not completed: "+e.getMessage(),e);
		}finally{
			if(connection!=null){
				this.jdbcServiceManager.closeConnection(connection);
			}
		}
	}
		
	@Override
	public DumpMessaggio get(long tableId) throws ServiceException, NotFoundException,MultipleResultException, NotImplementedException {
    
		Connection connection = null;
		try{
			
			// check parameters
			if(tableId<=0){
				throw this.newServiceExceptionParameterTableIdLessEqualsZero();
			}
			
			// ISQLQueryObject
			ISQLQueryObject sqlQueryObject = this.jdbcSqlObjectFactory.createSQLQueryObject(this.jdbcProperties.getDatabase());
			sqlQueryObject.setANDLogicOperator(true);
			// Connection sql
			connection = this.jdbcServiceManager.getConnection();
		
			return this.serviceSearch.get(this.jdbcProperties,this.log,connection,sqlQueryObject,tableId,null);
		
		}catch(ServiceException | MultipleResultException | NotImplementedException e){
			this.logError(e); throw e;
		}catch(NotFoundException e){
			this.logDebug(e); throw e;
		}catch(Exception e){
			this.logError(e); throw new ServiceException("Get(tableId) not completed: "+e.getMessage(),e);
		}finally{
			if(connection!=null){
				this.jdbcServiceManager.closeConnection(connection);
			}
		}
	
	}
	
	@Override
	public DumpMessaggio get(long tableId,org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws ServiceException, NotFoundException,MultipleResultException, NotImplementedException {
    
		Connection connection = null;
		try{
			
			// check parameters
			if(tableId<=0){
				throw this.newServiceExceptionParameterTableIdLessEqualsZero();
			}
			if(idMappingResolutionBehaviour==null){
				throw this.newServiceExceptionParameterIdMappingResolutionBehaviourIsNull();
			}
			
			// ISQLQueryObject
			ISQLQueryObject sqlQueryObject = this.jdbcSqlObjectFactory.createSQLQueryObject(this.jdbcProperties.getDatabase());
			sqlQueryObject.setANDLogicOperator(true);
			// Connection sql
			connection = this.jdbcServiceManager.getConnection();
		
			return this.serviceSearch.get(this.jdbcProperties,this.log,connection,sqlQueryObject,tableId,idMappingResolutionBehaviour);
		
		}catch(ServiceException | MultipleResultException | NotImplementedException e){
			this.logError(e); throw e;
		}catch(NotFoundException e){
			this.logDebug(e); throw e;
		}catch(Exception e){
			this.logError(e); throw new ServiceException("Get(tableId,idMappingResolutionBehaviour) not completed: "+e.getMessage(),e);
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
				throw this.newServiceExceptionParameterTableIdLessEqualsZero();
			}

			// ISQLQueryObject
			ISQLQueryObject sqlQueryObject = this.jdbcSqlObjectFactory.createSQLQueryObject(this.jdbcProperties.getDatabase());
			sqlQueryObject.setANDLogicOperator(true);
			// Connection sql
			connection = this.jdbcServiceManager.getConnection();

			return this.serviceSearch.exists(this.jdbcProperties,this.log,connection,sqlQueryObject,tableId);			
	
		}catch(MultipleResultException | ServiceException | NotImplementedException e){
			this.logError(e); throw e;
		}catch(Exception e){
			this.logError(e); throw new ServiceException("Exists(tableId) not completed: "+e.getMessage(),e);
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
				throw this.newServiceExceptionParameterPaginatedExpressionIsNull();
			}
			if( ! (expression instanceof JDBCPaginatedExpression) ){
				throw this.newServiceExceptionParameterPaginatedExpressionWrongType(expression);
			}
			JDBCPaginatedExpression jdbcPaginatedExpression = (JDBCPaginatedExpression) expression;
			logJDBCPaginatedExpression(jdbcPaginatedExpression);

			// ISQLQueryObject
			ISQLQueryObject sqlQueryObject = this.jdbcSqlObjectFactory.createSQLQueryObject(this.jdbcProperties.getDatabase());
			sqlQueryObject.setANDLogicOperator(true);
			// Connection sql
			connection = this.jdbcServiceManager.getConnection();
			
			return this.serviceSearch.findAllTableIds(this.jdbcProperties,this.log,connection,sqlQueryObject,jdbcPaginatedExpression);
	
		}catch(ServiceException | NotImplementedException e){
			this.logError(e); throw e;
		}catch(Exception e){
			this.logError(e); throw new ServiceException("findAllTableIds not completed: "+e.getMessage(),e);
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
				throw this.newServiceExceptionParameterPaginatedExpressionIsNull();
			}
			if( ! (expression instanceof JDBCExpression) ){
				throw this.newServiceExceptionParameterExpressionWrongType(expression);
			}
			JDBCExpression jdbcExpression = (JDBCExpression) expression;
			this.logJDBCExpression(jdbcExpression);

			// ISQLQueryObject
			ISQLQueryObject sqlQueryObject = this.jdbcSqlObjectFactory.createSQLQueryObject(this.jdbcProperties.getDatabase());
			sqlQueryObject.setANDLogicOperator(true);
			// Connection sql
			connection = this.jdbcServiceManager.getConnection();

			return this.serviceSearch.findTableId(this.jdbcProperties,this.log,connection,sqlQueryObject,jdbcExpression);			

		}catch(ServiceException | MultipleResultException | NotImplementedException e){
			this.logError(e); throw e;
		}catch(NotFoundException e){
			this.logDebug(e); throw e;
		}catch(Exception e){
			this.logError(e); throw new ServiceException("findTableId not completed: "+e.getMessage(),e);
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
				throw this.newServiceExceptionParameterTableIdLessEqualsZero();
			}
			
			// ISQLQueryObject
			ISQLQueryObject sqlQueryObject = this.jdbcSqlObjectFactory.createSQLQueryObject(this.jdbcProperties.getDatabase());
			sqlQueryObject.setANDLogicOperator(true);
			// Connection sql
			connection = this.jdbcServiceManager.getConnection();

			return this.serviceSearch.inUse(this.jdbcProperties,this.log,connection,sqlQueryObject,tableId);		
	
		}catch(ServiceException | NotImplementedException e){
			this.logError(e); throw e;
		}catch(NotFoundException e){
			this.logDebug(e); throw e;
		}catch(Exception e){
			this.logError(e); throw new ServiceException("InUse(tableId) not completed: "+e.getMessage(),e);
		}finally{
			if(connection!=null){
				this.jdbcServiceManager.closeConnection(connection);
			}
		}
	
	}
	
	@Override
	public IdDumpMessaggio findId(long tableId, boolean throwNotFound)
			throws NotFoundException, ServiceException, NotImplementedException {
		
		Connection connection = null;
		try{
			
			// check parameters
			if(tableId<=0){
				throw this.newServiceExceptionParameterTableIdLessEqualsZero();
			}
			
			// ISQLQueryObject
			ISQLQueryObject sqlQueryObject = this.jdbcSqlObjectFactory.createSQLQueryObject(this.jdbcProperties.getDatabase());
			sqlQueryObject.setANDLogicOperator(true);
			// Connection sql
			connection = this.jdbcServiceManager.getConnection();

			return this.serviceSearch.findId(this.jdbcProperties,this.log,connection,sqlQueryObject,tableId,throwNotFound);		
	
		}catch(ServiceException | NotImplementedException e){
			this.logError(e); throw e;
		}catch(NotFoundException e){
			this.logDebug(e); throw e;
		}catch(Exception e){
			this.logError(e); throw new ServiceException("findId(tableId,throwNotFound) not completed: "+e.getMessage(),e);
		}finally{
			if(connection!=null){
				this.jdbcServiceManager.closeConnection(connection);
			}
		}
		
	}

	@Override
	public Long findTableId(IdDumpMessaggio id, boolean throwNotFound)
			throws NotFoundException, ServiceException, NotImplementedException {
		
		Connection connection = null;
		try{
			
			// check parameters
			if(id==null){
				throw new ServiceException("Parameter 'id' is null");
			}
			
			// ISQLQueryObject
			ISQLQueryObject sqlQueryObject = this.jdbcSqlObjectFactory.createSQLQueryObject(this.jdbcProperties.getDatabase());
			sqlQueryObject.setANDLogicOperator(true);
			// Connection sql
			connection = this.jdbcServiceManager.getConnection();

			return this.serviceSearch.findTableId(this.jdbcProperties,this.log,connection,sqlQueryObject,id,throwNotFound);		
	
		}catch(ServiceException | NotImplementedException e){
			this.logError(e); throw e;
		}catch(NotFoundException e){
			this.logDebug(e); throw e;
		}catch(Exception e){
			this.logError(e); throw new ServiceException("findId(tableId,throwNotFound) not completed: "+e.getMessage(),e);
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
			if(returnClassTypes==null || returnClassTypes.isEmpty()){
				throw new ServiceException("Parameter 'returnClassTypes' is less equals 0");
			}
			
			// ISQLQueryObject
			ISQLQueryObject sqlQueryObject = this.jdbcSqlObjectFactory.createSQLQueryObject(this.jdbcProperties.getDatabase());
			sqlQueryObject.setANDLogicOperator(true);
			// Connection sql
			connection = this.jdbcServiceManager.getConnection();

			return this.serviceSearch.nativeQuery(this.jdbcProperties,this.log,connection,sqlQueryObject,sql,returnClassTypes,param);		
	
		}catch(ServiceException | NotImplementedException e){
			this.logError(e); throw e;
		}catch(NotFoundException e){
			this.logDebug(e); throw e;
		}catch(Exception e){
			this.logError(e); throw new ServiceException("nativeQuery not completed: "+e.getMessage(),e);
		}finally{
			if(connection!=null){
				this.jdbcServiceManager.closeConnection(connection);
			}
		}
	
	}
	
}
