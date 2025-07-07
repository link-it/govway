/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it).
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
package org.openspcoop2.core.statistiche.dao.jdbc;

import java.sql.Connection;
import java.util.List;

import org.openspcoop2.core.statistiche.StatistichePdndTracing;
import org.openspcoop2.core.statistiche.dao.IDBStatistichePdndTracingService;
import org.openspcoop2.core.statistiche.utils.ProjectInfo;
import org.openspcoop2.generic_project.beans.CustomField;
import org.openspcoop2.generic_project.beans.NonNegativeNumber;
import org.openspcoop2.generic_project.beans.UpdateField;
import org.openspcoop2.generic_project.beans.UpdateModel;
import org.openspcoop2.generic_project.dao.jdbc.IJDBCServiceCRUDWithoutId;
import org.openspcoop2.generic_project.dao.jdbc.JDBCExpression;
import org.openspcoop2.generic_project.dao.jdbc.JDBCProperties;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.generic_project.exception.NotImplementedException;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.generic_project.exception.ValidationException;
import org.openspcoop2.generic_project.expression.IExpression;
import org.openspcoop2.utils.sql.ISQLQueryObject;

/**     
 * Service can be used to search for and manage the backend objects of type {@link org.openspcoop2.core.statistiche.StatistichePdndTracing} 
 *
 * @author Poli Andrea (poli@link.it)
 * @author Tommaso Burlon (tommaso.burlon@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class JDBCStatistichePdndTracingService extends JDBCStatistichePdndTracingServiceSearch  implements IDBStatistichePdndTracingService {


	private IJDBCServiceCRUDWithoutId<StatistichePdndTracing, JDBCServiceManager> serviceCRUD = null;
	public JDBCStatistichePdndTracingService(JDBCServiceManager jdbcServiceManager) throws ServiceException {
		super(jdbcServiceManager);
		String msgInit = JDBCStatistichePdndTracingService.class.getName()+ " initialized";
		this.log.debug(msgInit);
		this.serviceCRUD = JDBCProperties.getInstance(org.openspcoop2.core.statistiche.dao.jdbc.JDBCServiceManager.class.getPackage(),ProjectInfo.getInstance()).getServiceCRUD("statistichePdndTracing");
		this.serviceCRUD.setServiceManager(new JDBCLimitedServiceManager(this.jdbcServiceManager));
	}

	private static final String PARAMETER_TYPE_PREFIX = "Parameter (type:";
	private ServiceException newServiceExceptionParameterIsNull(){
		return new ServiceException(PARAMETER_TYPE_PREFIX+StatistichePdndTracing.class.getName()+") 'statistichePdndTracing' is null");
	}
	private ServiceException newServiceExceptionParameterIsLessEqualsZero(){
		return new ServiceException(PARAMETER_TYPE_PREFIX+long.class.getName()+") 'tableId' is less equals 0");
	}
	private ServiceException newServiceExceptionParameterUpdateFieldsIsNull(){
		return new ServiceException(PARAMETER_TYPE_PREFIX+UpdateField.class.getName()+") 'updateFields' is null");
	}
	private ServiceException newServiceExceptionParameterConditionIsNull(){
		return new ServiceException(PARAMETER_TYPE_PREFIX+IExpression.class.getName()+") 'condition' is null");
	}
	private ServiceException newServiceExceptionParameterUpdateModelsIsNull(){
		return new ServiceException(PARAMETER_TYPE_PREFIX+UpdateModel.class.getName()+") 'updateModels' is null");
	}
	
	private ServiceException newServiceExceptionUpdateFieldsNotCompleted(Exception e){
		return new ServiceException("UpdateFields not completed: "+e.getMessage(),e);
	}
	
	
	private void releaseResources(boolean rollback, Connection connection, boolean oldValueAutoCommit) throws ServiceException {
		if(this.jdbcProperties.isAutomaticTransactionManagement()){
			manageTransaction(rollback, connection);
			try{
				if(connection!=null)
					connection.setAutoCommit(oldValueAutoCommit);
			}catch(Exception eIgnore){
				// ignore
			}
		}
		if(connection!=null){
			this.jdbcServiceManager.closeConnection(connection);
		}
	}
	private void manageTransaction(boolean rollback, Connection connection) {
		if(rollback){
			try{
				if(connection!=null)
					connection.rollback();
			}catch(Exception eIgnore){
				// ignore
			}
		}else{
			try{
				if(connection!=null)
					connection.commit();
			}catch(Exception eIgnore){
				// ignore
			}
		}
	}
	
	
	
	@Override
	public void create(StatistichePdndTracing statistichePdndTracing) throws ServiceException, NotImplementedException {
		try{
			this.create(statistichePdndTracing, false, null);
		}catch(ValidationException vE){
			// not possible
			throw new ServiceException(vE.getMessage(), vE);
		}
	}
	
	@Override
	public void create(StatistichePdndTracing statistichePdndTracing, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws ServiceException, NotImplementedException {
		try{
			this.create(statistichePdndTracing, false, idMappingResolutionBehaviour);
		}catch(ValidationException vE){
			// not possible
			throw new ServiceException(vE.getMessage(), vE);
		}
	}
	
	@Override
	public void create(StatistichePdndTracing statistichePdndTracing, boolean validate) throws ServiceException, NotImplementedException, ValidationException {
		this.create(statistichePdndTracing, validate, null);
	}
	
	@Override
	public void create(StatistichePdndTracing statistichePdndTracing, boolean validate, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws ServiceException, NotImplementedException, ValidationException {
		
		Connection connection = null;
		boolean oldValueAutoCommit = false;
		boolean rollback = false;
		try{
			
			// check parameters
			if(statistichePdndTracing==null){
				throw this.newServiceExceptionParameterIsNull();
			}
			
			// validate
			if(validate){
				this.validate(statistichePdndTracing);
			}

			// ISQLQueryObject
			ISQLQueryObject sqlQueryObject = this.jdbcSqlObjectFactory.createSQLQueryObject(this.jdbcProperties.getDatabase());
			sqlQueryObject.setANDLogicOperator(true);
			// Connection sql
			connection = this.jdbcServiceManager.getConnection();
	
			// transaction
			if(this.jdbcProperties.isAutomaticTransactionManagement()){
				oldValueAutoCommit = connection.getAutoCommit();
				connection.setAutoCommit(false);
			}
		
			this.serviceCRUD.create(this.jdbcProperties,this.log,connection,sqlQueryObject,statistichePdndTracing,idMappingResolutionBehaviour);			

		}catch(ServiceException | NotImplementedException | ValidationException e){
			rollback = true;
			this.logError(e); throw e;
		}catch(Exception e){
			rollback = true;
			this.logError(e); throw new ServiceException("Create not completed: "+e.getMessage(),e);
		}finally{
			this.releaseResources(rollback, connection, oldValueAutoCommit);
		}

	}

	@Override
	public void update(StatistichePdndTracing statistichePdndTracing) throws ServiceException, NotFoundException, NotImplementedException {
		try{
			this.update(statistichePdndTracing, false, null);
		}catch(ValidationException vE){
			// not possible
			throw new ServiceException(vE.getMessage(), vE);
		}
	}
	
	@Override
	public void update(StatistichePdndTracing statistichePdndTracing, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws ServiceException, NotFoundException, NotImplementedException {
		try{
			this.update(statistichePdndTracing, false, idMappingResolutionBehaviour);
		}catch(ValidationException vE){
			// not possible
			throw new ServiceException(vE.getMessage(), vE);
		}
	}
	
	@Override
	public void update(StatistichePdndTracing statistichePdndTracing, boolean validate) throws ServiceException, NotFoundException, NotImplementedException, ValidationException {
		this.update(statistichePdndTracing, validate, null);
	}
		
	@Override
	public void update(StatistichePdndTracing statistichePdndTracing, boolean validate, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws ServiceException, NotFoundException, NotImplementedException, ValidationException {
	
		Connection connection = null;
		boolean oldValueAutoCommit = false;
		boolean rollback = false;
		try{
			
			// check parameters
			if(statistichePdndTracing==null){
				throw this.newServiceExceptionParameterIsNull();
			}

			// validate
			if(validate){
				this.validate(statistichePdndTracing);
			}

			// ISQLQueryObject
			ISQLQueryObject sqlQueryObject = this.jdbcSqlObjectFactory.createSQLQueryObject(this.jdbcProperties.getDatabase());
			sqlQueryObject.setANDLogicOperator(true);
			// Connection sql
			connection = this.jdbcServiceManager.getConnection();
		
			// transaction
			if(this.jdbcProperties.isAutomaticTransactionManagement()){
				oldValueAutoCommit = connection.getAutoCommit();
				connection.setAutoCommit(false);
			}

			this.serviceCRUD.update(this.jdbcProperties,this.log,connection,sqlQueryObject,statistichePdndTracing,idMappingResolutionBehaviour);
			
		}catch(ServiceException | NotImplementedException | ValidationException e){
			rollback = true;
			this.logError(e); throw e;
		}catch(NotFoundException e){
			rollback = true;
			this.logDebug(e); throw e;
		}catch(Exception e){
			rollback = true;
			this.logError(e); throw new ServiceException("Update not completed: "+e.getMessage(),e);
		}finally{
			this.releaseResources(rollback, connection, oldValueAutoCommit);
		}

	}
	
	@Override
	public void update(long tableId, StatistichePdndTracing statistichePdndTracing) throws ServiceException, NotFoundException, NotImplementedException {
		try{
			this.update(tableId, statistichePdndTracing, false, null);
		}catch(ValidationException vE){
			// not possible
			throw new ServiceException(vE.getMessage(), vE);
		}
	}
	
	@Override
	public void update(long tableId, StatistichePdndTracing statistichePdndTracing, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws ServiceException, NotFoundException, NotImplementedException {
		try{
			this.update(tableId, statistichePdndTracing, false, idMappingResolutionBehaviour);
		}catch(ValidationException vE){
			// not possible
			throw new ServiceException(vE.getMessage(), vE);
		}
	}
	
	@Override
	public void update(long tableId, StatistichePdndTracing statistichePdndTracing, boolean validate) throws ServiceException, NotFoundException, NotImplementedException, ValidationException {
		this.update(tableId, statistichePdndTracing, validate, null);
	}
		
	@Override
	public void update(long tableId, StatistichePdndTracing statistichePdndTracing, boolean validate, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws ServiceException, NotFoundException, NotImplementedException, ValidationException {
	
		Connection connection = null;
		boolean oldValueAutoCommit = false;
		boolean rollback = false;
		try{
			
			// check parameters
			if(statistichePdndTracing==null){
				throw this.newServiceExceptionParameterIsNull();
			}
			if(tableId<=0){
				throw this.newServiceExceptionParameterIsLessEqualsZero();
			}

			// validate
			if(validate){
				this.validate(statistichePdndTracing);
			}

			// ISQLQueryObject
			ISQLQueryObject sqlQueryObject = this.jdbcSqlObjectFactory.createSQLQueryObject(this.jdbcProperties.getDatabase());
			sqlQueryObject.setANDLogicOperator(true);
			// Connection sql
			connection = this.jdbcServiceManager.getConnection();
		
			// transaction
			if(this.jdbcProperties.isAutomaticTransactionManagement()){
				oldValueAutoCommit = connection.getAutoCommit();
				connection.setAutoCommit(false);
			}

			this.serviceCRUD.update(this.jdbcProperties,this.log,connection,sqlQueryObject,tableId,statistichePdndTracing,idMappingResolutionBehaviour);
			
		}catch(ServiceException | NotImplementedException | ValidationException e){
			rollback = true;
			this.logError(e); throw e;
		}catch(NotFoundException e){
			rollback = true;
			this.logDebug(e); throw e;
		}catch(Exception e){
			rollback = true;
			this.logError(e); throw new ServiceException("Update not completed: "+e.getMessage(),e);
		}finally{
			this.releaseResources(rollback, connection, oldValueAutoCommit);
		}

	}
	
	@Override
	public void updateFields(StatistichePdndTracing statistichePdndTracing, UpdateField ... updateFields) throws ServiceException, NotFoundException, NotImplementedException {
	
		Connection connection = null;
		boolean oldValueAutoCommit = false;
		boolean rollback = false;
		try{
			
			// check parameters
			if(statistichePdndTracing==null){
				throw this.newServiceExceptionParameterIsNull();
			}
			if(updateFields==null){
				throw this.newServiceExceptionParameterUpdateFieldsIsNull();
			}

			// ISQLQueryObject
			ISQLQueryObject sqlQueryObject = this.jdbcSqlObjectFactory.createSQLQueryObject(this.jdbcProperties.getDatabase());
			sqlQueryObject.setANDLogicOperator(true);
			// Connection sql
			connection = this.jdbcServiceManager.getConnection();
		
			// transaction
			if(this.jdbcProperties.isAutomaticTransactionManagement()){
				oldValueAutoCommit = connection.getAutoCommit();
				connection.setAutoCommit(false);
			}

			this.serviceCRUD.updateFields(this.jdbcProperties,this.log,connection,sqlQueryObject,statistichePdndTracing,updateFields);
			
		}catch(ServiceException | NotImplementedException e){
			rollback = true;
			this.logError(e); throw e;
		}catch(NotFoundException e){
			rollback = true;
			this.logDebug(e); throw e;
		}catch(Exception e){
			rollback = true;
			this.logError(e); throw this.newServiceExceptionUpdateFieldsNotCompleted(e);
		}finally{
			this.releaseResources(rollback, connection, oldValueAutoCommit);
		}

	}
	
	@Override
	public void updateFields(StatistichePdndTracing statistichePdndTracing, IExpression condition, UpdateField ... updateFields) throws ServiceException, NotFoundException, NotImplementedException {
	
		Connection connection = null;
		boolean oldValueAutoCommit = false;
		boolean rollback = false;
		try{
			
			// check parameters
			if(statistichePdndTracing==null){
				throw this.newServiceExceptionParameterIsNull();
			}
			if(condition==null){
				throw this.newServiceExceptionParameterConditionIsNull();
			}
			if(updateFields==null){
				throw this.newServiceExceptionParameterUpdateFieldsIsNull();
			}

			// ISQLQueryObject
			ISQLQueryObject sqlQueryObject = this.jdbcSqlObjectFactory.createSQLQueryObject(this.jdbcProperties.getDatabase());
			sqlQueryObject.setANDLogicOperator(true);
			// Connection sql
			connection = this.jdbcServiceManager.getConnection();
		
			// transaction
			if(this.jdbcProperties.isAutomaticTransactionManagement()){
				oldValueAutoCommit = connection.getAutoCommit();
				connection.setAutoCommit(false);
			}

			this.serviceCRUD.updateFields(this.jdbcProperties,this.log,connection,sqlQueryObject,statistichePdndTracing,condition,updateFields);
			
		}catch(ServiceException | NotImplementedException e){
			rollback = true;
			this.logError(e); throw e;
		}catch(NotFoundException e){
			rollback = true;
			this.logDebug(e); throw e;
		}catch(Exception e){
			rollback = true;
			this.logError(e); throw this.newServiceExceptionUpdateFieldsNotCompleted(e);
		}finally{
			this.releaseResources(rollback, connection, oldValueAutoCommit);
		}

	}

	@Override
	public void updateFields(StatistichePdndTracing statistichePdndTracing, UpdateModel ... updateModels) throws ServiceException, NotFoundException, NotImplementedException {
	
		Connection connection = null;
		boolean oldValueAutoCommit = false;
		boolean rollback = false;
		try{
			
			// check parameters
			if(statistichePdndTracing==null){
				throw this.newServiceExceptionParameterIsNull();
			}
			if(updateModels==null){
				throw this.newServiceExceptionParameterUpdateModelsIsNull();
			}

			// ISQLQueryObject
			ISQLQueryObject sqlQueryObject = this.jdbcSqlObjectFactory.createSQLQueryObject(this.jdbcProperties.getDatabase());
			sqlQueryObject.setANDLogicOperator(true);
			// Connection sql
			connection = this.jdbcServiceManager.getConnection();
		
			// transaction
			if(this.jdbcProperties.isAutomaticTransactionManagement()){
				oldValueAutoCommit = connection.getAutoCommit();
				connection.setAutoCommit(false);
			}

			this.serviceCRUD.updateFields(this.jdbcProperties,this.log,connection,sqlQueryObject,statistichePdndTracing,updateModels);
			
		}catch(ServiceException | NotImplementedException e){
			rollback = true;
			this.logError(e); throw e;
		}catch(NotFoundException e){
			rollback = true;
			this.logDebug(e); throw e;
		}catch(Exception e){
			rollback = true;
			this.logError(e); throw this.newServiceExceptionUpdateFieldsNotCompleted(e);
		}finally{
			this.releaseResources(rollback, connection, oldValueAutoCommit);
		}

	}

	@Override
	public void updateFields(long tableId, UpdateField ... updateFields) throws ServiceException, NotFoundException, NotImplementedException {
	
		Connection connection = null;
		boolean oldValueAutoCommit = false;
		boolean rollback = false;
		try{
			
			// check parameters
			if(tableId<=0){
				throw this.newServiceExceptionParameterIsLessEqualsZero();
			}
			if(updateFields==null){
				throw this.newServiceExceptionParameterUpdateFieldsIsNull();
			}

			// ISQLQueryObject
			ISQLQueryObject sqlQueryObject = this.jdbcSqlObjectFactory.createSQLQueryObject(this.jdbcProperties.getDatabase());
			sqlQueryObject.setANDLogicOperator(true);
			// Connection sql
			connection = this.jdbcServiceManager.getConnection();
		
			// transaction
			if(this.jdbcProperties.isAutomaticTransactionManagement()){
				oldValueAutoCommit = connection.getAutoCommit();
				connection.setAutoCommit(false);
			}

			this.serviceCRUD.updateFields(this.jdbcProperties,this.log,connection,sqlQueryObject,tableId,updateFields);	
			
		}catch(ServiceException | NotImplementedException e){
			rollback = true;
			this.logError(e); throw e;
		}catch(NotFoundException e){
			rollback = true;
			this.logDebug(e); throw e;
		}catch(Exception e){
			rollback = true;
			this.logError(e); throw this.newServiceExceptionUpdateFieldsNotCompleted(e);
		}finally{
			this.releaseResources(rollback, connection, oldValueAutoCommit);
		}

	}
	
	@Override
	public void updateFields(long tableId, IExpression condition, UpdateField ... updateFields) throws ServiceException, NotFoundException, NotImplementedException {
	
		Connection connection = null;
		boolean oldValueAutoCommit = false;
		boolean rollback = false;
		try{
			
			// check parameters
			if(tableId<=0){
				throw this.newServiceExceptionParameterIsLessEqualsZero();
			}
			if(condition==null){
				throw this.newServiceExceptionParameterConditionIsNull();
			}
			if(updateFields==null){
				throw this.newServiceExceptionParameterUpdateFieldsIsNull();
			}

			// ISQLQueryObject
			ISQLQueryObject sqlQueryObject = this.jdbcSqlObjectFactory.createSQLQueryObject(this.jdbcProperties.getDatabase());
			sqlQueryObject.setANDLogicOperator(true);
			// Connection sql
			connection = this.jdbcServiceManager.getConnection();
		
			// transaction
			if(this.jdbcProperties.isAutomaticTransactionManagement()){
				oldValueAutoCommit = connection.getAutoCommit();
				connection.setAutoCommit(false);
			}

			this.serviceCRUD.updateFields(this.jdbcProperties,this.log,connection,sqlQueryObject,tableId,condition,updateFields);
			
		}catch(ServiceException | NotImplementedException e){
			rollback = true;
			this.logError(e); throw e;
		}catch(NotFoundException e){
			rollback = true;
			this.logDebug(e); throw e;
		}catch(Exception e){
			rollback = true;
			this.logError(e); throw this.newServiceExceptionUpdateFieldsNotCompleted(e);
		}finally{
			this.releaseResources(rollback, connection, oldValueAutoCommit);
		}

	}

	@Override
	public void updateFields(long tableId, UpdateModel ... updateModels) throws ServiceException, NotFoundException, NotImplementedException {
	
		Connection connection = null;
		boolean oldValueAutoCommit = false;
		boolean rollback = false;
		try{
			
			// check parameters
			if(tableId<=0){
				throw this.newServiceExceptionParameterIsLessEqualsZero();
			}
			if(updateModels==null){
				throw this.newServiceExceptionParameterUpdateModelsIsNull();
			}

			// ISQLQueryObject
			ISQLQueryObject sqlQueryObject = this.jdbcSqlObjectFactory.createSQLQueryObject(this.jdbcProperties.getDatabase());
			sqlQueryObject.setANDLogicOperator(true);
			// Connection sql
			connection = this.jdbcServiceManager.getConnection();
		
			// transaction
			if(this.jdbcProperties.isAutomaticTransactionManagement()){
				oldValueAutoCommit = connection.getAutoCommit();
				connection.setAutoCommit(false);
			}

			this.serviceCRUD.updateFields(this.jdbcProperties,this.log,connection,sqlQueryObject,tableId,updateModels);
			
		}catch(ServiceException | NotImplementedException e){
			rollback = true;
			this.logError(e); throw e;
		}catch(NotFoundException e){
			rollback = true;
			this.logDebug(e); throw e;
		}catch(Exception e){
			rollback = true;
			this.logError(e); throw this.newServiceExceptionUpdateFieldsNotCompleted(e);
		}finally{
			this.releaseResources(rollback, connection, oldValueAutoCommit);
		}

	}

	@Override
	public void updateOrCreate(StatistichePdndTracing statistichePdndTracing) throws ServiceException, NotImplementedException {
		try{
			this.updateOrCreate(statistichePdndTracing, false, null);
		}catch(ValidationException vE){
			// not possible
			throw new ServiceException(vE.getMessage(), vE);
		}
	}
	
	@Override
	public void updateOrCreate(StatistichePdndTracing statistichePdndTracing, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws ServiceException, NotImplementedException {
		try{
			this.updateOrCreate(statistichePdndTracing, false, idMappingResolutionBehaviour);
		}catch(ValidationException vE){
			// not possible
			throw new ServiceException(vE.getMessage(), vE);
		}
	}

	@Override
	public void updateOrCreate(StatistichePdndTracing statistichePdndTracing, boolean validate) throws ServiceException, NotImplementedException, ValidationException {
		this.updateOrCreate(statistichePdndTracing, validate, null);
	}

	@Override
	public void updateOrCreate(StatistichePdndTracing statistichePdndTracing, boolean validate, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws ServiceException, NotImplementedException, ValidationException {
	
		Connection connection = null;
		boolean oldValueAutoCommit = false;
		boolean rollback = false;
		try{
			
			// check parameters
			if(statistichePdndTracing==null){
				throw this.newServiceExceptionParameterIsNull();
			}

			// validate
			if(validate){
				this.validate(statistichePdndTracing);
			}

			// ISQLQueryObject
			ISQLQueryObject sqlQueryObject = this.jdbcSqlObjectFactory.createSQLQueryObject(this.jdbcProperties.getDatabase());
			sqlQueryObject.setANDLogicOperator(true);
			// Connection sql
			connection = this.jdbcServiceManager.getConnection();
		
			// transaction
			if(this.jdbcProperties.isAutomaticTransactionManagement()){
				oldValueAutoCommit = connection.getAutoCommit();
				connection.setAutoCommit(false);
			}

			this.serviceCRUD.updateOrCreate(this.jdbcProperties,this.log,connection,sqlQueryObject,statistichePdndTracing,idMappingResolutionBehaviour);
			
		}catch(ServiceException | NotImplementedException | ValidationException e){
			rollback = true;
			this.logError(e); throw e;
		}catch(Exception e){
			rollback = true;
			this.logError(e); throw new ServiceException("UpdateOrCreate not completed: "+e.getMessage(),e);
		}finally{
			this.releaseResources(rollback, connection, oldValueAutoCommit);
		}

	}
	
	@Override
	public void updateOrCreate(long tableId, StatistichePdndTracing statistichePdndTracing) throws ServiceException, NotImplementedException {
		try{
			this.updateOrCreate(tableId, statistichePdndTracing, false, null);
		}catch(ValidationException vE){
			// not possible
			throw new ServiceException(vE.getMessage(), vE);
		}
	}
	
	@Override
	public void updateOrCreate(long tableId, StatistichePdndTracing statistichePdndTracing, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws ServiceException, NotImplementedException {
		try{
			this.updateOrCreate(tableId, statistichePdndTracing, false, idMappingResolutionBehaviour);
		}catch(ValidationException vE){
			// not possible
			throw new ServiceException(vE.getMessage(), vE);
		}
	}

	@Override
	public void updateOrCreate(long tableId, StatistichePdndTracing statistichePdndTracing, boolean validate) throws ServiceException, NotImplementedException, ValidationException {
		this.updateOrCreate(tableId, statistichePdndTracing, validate, null);
	}

	@Override
	public void updateOrCreate(long tableId, StatistichePdndTracing statistichePdndTracing, boolean validate, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws ServiceException, NotImplementedException, ValidationException {

		Connection connection = null;
		boolean oldValueAutoCommit = false;
		boolean rollback = false;
		try{
			
			// check parameters
			if(statistichePdndTracing==null){
				throw this.newServiceExceptionParameterIsNull();
			}
			if(tableId<=0){
				throw this.newServiceExceptionParameterIsLessEqualsZero();
			}

			// validate
			if(validate){
				this.validate(statistichePdndTracing);
			}

			// ISQLQueryObject
			ISQLQueryObject sqlQueryObject = this.jdbcSqlObjectFactory.createSQLQueryObject(this.jdbcProperties.getDatabase());
			sqlQueryObject.setANDLogicOperator(true);
			// Connection sql
			connection = this.jdbcServiceManager.getConnection();
		
			// transaction
			if(this.jdbcProperties.isAutomaticTransactionManagement()){
				oldValueAutoCommit = connection.getAutoCommit();
				connection.setAutoCommit(false);
			}

			this.serviceCRUD.updateOrCreate(this.jdbcProperties,this.log,connection,sqlQueryObject,tableId,statistichePdndTracing,idMappingResolutionBehaviour);

		}catch(ServiceException | NotImplementedException | ValidationException e){
			rollback = true;
			this.logError(e); throw e;
		}catch(Exception e){
			rollback = true;
			this.logError(e); throw new ServiceException("UpdateOrCreate not completed: "+e.getMessage(),e);
		}finally{
			this.releaseResources(rollback, connection, oldValueAutoCommit);
		}

	}
	
	@Override
	public void delete(StatistichePdndTracing statistichePdndTracing) throws ServiceException,NotImplementedException {
		
		Connection connection = null;
		boolean oldValueAutoCommit = false;
		boolean rollback = false;
		try{
			
			// check parameters
			if(statistichePdndTracing==null){
				throw this.newServiceExceptionParameterIsNull();
			}

			// ISQLQueryObject
			ISQLQueryObject sqlQueryObject = this.jdbcSqlObjectFactory.createSQLQueryObject(this.jdbcProperties.getDatabase());
			sqlQueryObject.setANDLogicOperator(true);
			// Connection sql
			connection = this.jdbcServiceManager.getConnection();

			// transaction
			if(this.jdbcProperties.isAutomaticTransactionManagement()){
				oldValueAutoCommit = connection.getAutoCommit();
				connection.setAutoCommit(false);
			}

			this.serviceCRUD.delete(this.jdbcProperties,this.log,connection,sqlQueryObject,statistichePdndTracing);	

		}catch(ServiceException | NotImplementedException e){
			rollback = true;
			this.logError(e); throw e;
		}catch(Exception e){
			rollback = true;
			this.logError(e); throw new ServiceException("Delete not completed: "+e.getMessage(),e);
		}finally{
			this.releaseResources(rollback, connection, oldValueAutoCommit);
		}
	
	}
	


	@Override
	public NonNegativeNumber deleteAll() throws ServiceException, NotImplementedException {

		Connection connection = null;
		boolean oldValueAutoCommit = false;
		boolean rollback = false;
		try{

			// ISQLQueryObject
			ISQLQueryObject sqlQueryObject = this.jdbcSqlObjectFactory.createSQLQueryObject(this.jdbcProperties.getDatabase());
			sqlQueryObject.setANDLogicOperator(true);
			// Connection sql
			connection = this.jdbcServiceManager.getConnection();
		
			// transaction
			if(this.jdbcProperties.isAutomaticTransactionManagement()){
				oldValueAutoCommit = connection.getAutoCommit();
				connection.setAutoCommit(false);
			}

			return this.serviceCRUD.deleteAll(this.jdbcProperties,this.log,connection,sqlQueryObject);	

		}catch(ServiceException | NotImplementedException e){
			rollback = true;
			this.logError(e); throw e;
		}catch(Exception e){
			rollback = true;
			this.logError(e); throw new ServiceException("DeleteAll not completed: "+e.getMessage(),e);
		}finally{
			this.releaseResources(rollback, connection, oldValueAutoCommit);
		}
	
	}

	@Override
	public NonNegativeNumber deleteAll(IExpression expression) throws ServiceException, NotImplementedException {
		
		Connection connection = null;
		boolean oldValueAutoCommit = false;
		boolean rollback = false;
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

			// transaction
			if(this.jdbcProperties.isAutomaticTransactionManagement()){
				oldValueAutoCommit = connection.getAutoCommit();
				connection.setAutoCommit(false);
			}

			return this.serviceCRUD.deleteAll(this.jdbcProperties,this.log,connection,sqlQueryObject,jdbcExpression);
	
		}catch(ServiceException | NotImplementedException e){
			rollback = true;
			this.logError(e); throw e;
		}catch(Exception e){
			rollback = true;
			this.logError(e); throw new ServiceException("DeleteAll(expression) not completed: "+e.getMessage(),e);
		}finally{
			this.releaseResources(rollback, connection, oldValueAutoCommit);
		}
	
	}
	
	// -- DB
	
	@Override
	public void deleteById(long tableId) throws ServiceException, NotImplementedException {
		
		Connection connection = null;
		boolean oldValueAutoCommit = false;
		boolean rollback = false;
		try{
			
			// check parameters
			if(tableId<=0){
				throw new ServiceException("Parameter 'tableId' is less equals 0");
			}
		
			// ISQLQueryObject
			ISQLQueryObject sqlQueryObject = this.jdbcSqlObjectFactory.createSQLQueryObject(this.jdbcProperties.getDatabase());
			sqlQueryObject.setANDLogicOperator(true);
			// Connection sql
			connection = this.jdbcServiceManager.getConnection();

			// transaction
			if(this.jdbcProperties.isAutomaticTransactionManagement()){
				oldValueAutoCommit = connection.getAutoCommit();
				connection.setAutoCommit(false);
			}

			this.serviceCRUD.deleteById(this.jdbcProperties,this.log,connection,sqlQueryObject,tableId);
	
		}catch(ServiceException | NotImplementedException e){
			rollback = true;
			this.logError(e); throw e;
		}catch(Exception e){
			rollback = true;
			this.logError(e); throw new ServiceException("DeleteById(tableId) not completed: "+e.getMessage(),e);
		}finally{
			this.releaseResources(rollback, connection, oldValueAutoCommit);
		}
	
	}
	
	@Override
	public int nativeUpdate(String sql,Object ... param) throws ServiceException, NotImplementedException {
		
		Connection connection = null;
		boolean oldValueAutoCommit = false;
		boolean rollback = false;
		try{
			
			// check parameters
			if(sql==null){
				throw new ServiceException("Parameter 'sql' is null");
			}
		
			// ISQLQueryObject
			ISQLQueryObject sqlQueryObject = this.jdbcSqlObjectFactory.createSQLQueryObject(this.jdbcProperties.getDatabase());
			sqlQueryObject.setANDLogicOperator(true);
			// Connection sql
			connection = this.jdbcServiceManager.getConnection();

			// transaction
			if(this.jdbcProperties.isAutomaticTransactionManagement()){
				oldValueAutoCommit = connection.getAutoCommit();
				connection.setAutoCommit(false);
			}

			return this.serviceCRUD.nativeUpdate(this.jdbcProperties,this.log,connection,sqlQueryObject,sql,param);
	
		}catch(ServiceException | NotImplementedException e){
			rollback = true;
			this.logError(e); throw e;
		}catch(Exception e){
			rollback = true;
			this.logError(e); throw new ServiceException("DeleteById(tableId) not completed: "+e.getMessage(),e);
		}finally{
			this.releaseResources(rollback, connection, oldValueAutoCommit);
		}
	
	}
	
	@Override
	public void forcePublish(List<Long> ids) throws Exception {
		
		IExpression expr = this.newExpression();
		expr.in(new CustomField("id", Long.class, "id", this.getFieldConverter().toTable(StatistichePdndTracing.model())), ids);
		
		this.forcePublish(expr);
	}
	
	
	@Override
	public void forcePublish(IExpression expr) throws Exception {
		Connection connection = null;
		boolean oldValueAutoCommit = false;
		boolean rollback = false;
		try{

			// ISQLQueryObject
			ISQLQueryObject sqlQueryObject = this.jdbcSqlObjectFactory.createSQLQueryObject(this.jdbcProperties.getDatabase());
			sqlQueryObject.setANDLogicOperator(true);
			// Connection sql
			connection = this.jdbcServiceManager.getConnection();
		
			// transaction
			if(this.jdbcProperties.isAutomaticTransactionManagement()){
				oldValueAutoCommit = connection.getAutoCommit();
				connection.setAutoCommit(false);
			}

			((JDBCStatistichePdndTracingServiceImpl)this.serviceCRUD).forcePublish(this.jdbcProperties,this.log,connection,sqlQueryObject, expr);
			
		}catch(ServiceException | NotImplementedException e){
			rollback = true;
			this.logError(e); throw e;
		}catch(NotFoundException e){
			rollback = true;
			this.logDebug(e); throw e;
		}catch(Exception e){
			rollback = true;
			this.logError(e); throw this.newServiceExceptionUpdateFieldsNotCompleted(e);
		}finally{
			this.releaseResources(rollback, connection, oldValueAutoCommit);
		}
	}
	
}
