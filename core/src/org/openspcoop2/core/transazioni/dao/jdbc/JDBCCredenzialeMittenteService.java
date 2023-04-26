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

import org.openspcoop2.generic_project.dao.jdbc.IJDBCServiceCRUDWithId;
import org.openspcoop2.core.transazioni.IdCredenzialeMittente;

import org.openspcoop2.generic_project.beans.NonNegativeNumber;
import org.openspcoop2.generic_project.beans.UpdateField;
import org.openspcoop2.generic_project.beans.UpdateModel;
import org.openspcoop2.generic_project.dao.jdbc.JDBCProperties;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.generic_project.exception.NotImplementedException;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.generic_project.exception.ValidationException;
import org.openspcoop2.generic_project.expression.IExpression;
import org.openspcoop2.generic_project.dao.jdbc.JDBCExpression;

import org.openspcoop2.core.transazioni.CredenzialeMittente;
import org.openspcoop2.core.transazioni.dao.IDBCredenzialeMittenteService;
import org.openspcoop2.core.transazioni.utils.ProjectInfo;

import java.sql.Connection;

import org.openspcoop2.utils.sql.ISQLQueryObject;

/**     
 * Service can be used to search for and manage the backend objects of type {@link org.openspcoop2.core.transazioni.CredenzialeMittente} 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class JDBCCredenzialeMittenteService extends JDBCCredenzialeMittenteServiceSearch  implements IDBCredenzialeMittenteService {


	private IJDBCServiceCRUDWithId<CredenzialeMittente, IdCredenzialeMittente, JDBCServiceManager> serviceCRUD = null;
	public JDBCCredenzialeMittenteService(JDBCServiceManager jdbcServiceManager) throws ServiceException {
		super(jdbcServiceManager);
		String msgInit = JDBCCredenzialeMittenteService.class.getName()+ " initialized";
		this.log.debug(msgInit);
		this.serviceCRUD = JDBCProperties.getInstance(org.openspcoop2.core.transazioni.dao.jdbc.JDBCServiceManager.class.getPackage(),ProjectInfo.getInstance()).getServiceCRUD("credenzialeMittente");
		this.serviceCRUD.setServiceManager(new JDBCLimitedServiceManager(this.jdbcServiceManager));
	}

	private static final String PARAMETER_TYPE_PREFIX = "Parameter (type:";
	private ServiceException newServiceExceptionParameterIsNull(){
		return new ServiceException(PARAMETER_TYPE_PREFIX+CredenzialeMittente.class.getName()+") 'credenzialeMittente' is null");
	}
	private ServiceException newServiceExceptionParameterIsLessEqualsZero(){
		return new ServiceException(PARAMETER_TYPE_PREFIX+long.class.getName()+") 'tableId' is less equals 0");
	}
	private ServiceException newServiceExceptionParameterUpdateFieldsIsNull(){
		return new ServiceException(PARAMETER_TYPE_PREFIX+UpdateField.class.getName()+") 'updateFields' is null");
	}
	private ServiceException newServiceExceptionParameterOldIdIsNull(){
		return new ServiceException(PARAMETER_TYPE_PREFIX+IdCredenzialeMittente.class.getName()+") 'oldId' is null");
	}
	private ServiceException newServiceExceptionParameterIdIsNull(){
		return new ServiceException(PARAMETER_TYPE_PREFIX+IdCredenzialeMittente.class.getName()+") 'id' is null");
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
	public void create(CredenzialeMittente credenzialeMittente) throws ServiceException, NotImplementedException {
		try{
			this.create(credenzialeMittente, false, null);
		}catch(ValidationException vE){
			// not possible
			throw new ServiceException(vE.getMessage(), vE);
		}
	}
	
	@Override
	public void create(CredenzialeMittente credenzialeMittente, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws ServiceException, NotImplementedException {
		try{
			this.create(credenzialeMittente, false, idMappingResolutionBehaviour);
		}catch(ValidationException vE){
			// not possible
			throw new ServiceException(vE.getMessage(), vE);
		}
	}
	
	@Override
	public void create(CredenzialeMittente credenzialeMittente, boolean validate) throws ServiceException, NotImplementedException, ValidationException {
		this.create(credenzialeMittente, validate, null);
	}
	
	@Override
	public void create(CredenzialeMittente credenzialeMittente, boolean validate, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws ServiceException, NotImplementedException, ValidationException {
		
		Connection connection = null;
		boolean oldValueAutoCommit = false;
		boolean rollback = false;
		try{
			
			// check parameters
			if(credenzialeMittente==null){
				throw this.newServiceExceptionParameterIsNull();
			}
			
			// validate
			if(validate){
				this.validate(credenzialeMittente);
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
		
			this.serviceCRUD.create(this.jdbcProperties,this.log,connection,sqlQueryObject,credenzialeMittente,idMappingResolutionBehaviour);			

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
	public void update(IdCredenzialeMittente oldId, CredenzialeMittente credenzialeMittente) throws ServiceException, NotFoundException, NotImplementedException {
		try{
			this.update(oldId, credenzialeMittente, false, null);
		}catch(ValidationException vE){
			// not possible
			throw new ServiceException(vE.getMessage(), vE);
		}
	}
	
	@Override
	public void update(IdCredenzialeMittente oldId, CredenzialeMittente credenzialeMittente, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws ServiceException, NotFoundException, NotImplementedException {
		try{
			this.update(oldId, credenzialeMittente, false, idMappingResolutionBehaviour);
		}catch(ValidationException vE){
			// not possible
			throw new ServiceException(vE.getMessage(), vE);
		}
	}
	
	@Override
	public void update(IdCredenzialeMittente oldId, CredenzialeMittente credenzialeMittente, boolean validate) throws ServiceException, NotFoundException, NotImplementedException, ValidationException {
		this.update(oldId, credenzialeMittente, validate, null);
	}
		
	@Override
	public void update(IdCredenzialeMittente oldId, CredenzialeMittente credenzialeMittente, boolean validate, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws ServiceException, NotFoundException, NotImplementedException, ValidationException {
	
		Connection connection = null;
		boolean oldValueAutoCommit = false;
		boolean rollback = false;
		try{
			
			// check parameters
			if(credenzialeMittente==null){
				throw this.newServiceExceptionParameterIsNull();
			}
			if(oldId==null){
				throw this.newServiceExceptionParameterOldIdIsNull();
			}

			// validate
			if(validate){
				this.validate(credenzialeMittente);
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

			this.serviceCRUD.update(this.jdbcProperties,this.log,connection,sqlQueryObject,oldId,credenzialeMittente,idMappingResolutionBehaviour);
			
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
	public void update(long tableId, CredenzialeMittente credenzialeMittente) throws ServiceException, NotFoundException, NotImplementedException {
		try{
			this.update(tableId, credenzialeMittente, false, null);
		}catch(ValidationException vE){
			// not possible
			throw new ServiceException(vE.getMessage(), vE);
		}
	}
	
	@Override
	public void update(long tableId, CredenzialeMittente credenzialeMittente, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws ServiceException, NotFoundException, NotImplementedException {
		try{
			this.update(tableId, credenzialeMittente, false, idMappingResolutionBehaviour);
		}catch(ValidationException vE){
			// not possible
			throw new ServiceException(vE.getMessage(), vE);
		}
	}
	
	@Override
	public void update(long tableId, CredenzialeMittente credenzialeMittente, boolean validate) throws ServiceException, NotFoundException, NotImplementedException, ValidationException {
		this.update(tableId, credenzialeMittente, validate, null);
	}
		
	@Override
	public void update(long tableId, CredenzialeMittente credenzialeMittente, boolean validate, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws ServiceException, NotFoundException, NotImplementedException, ValidationException {
	
		Connection connection = null;
		boolean oldValueAutoCommit = false;
		boolean rollback = false;
		try{
			
			// check parameters
			if(credenzialeMittente==null){
				throw this.newServiceExceptionParameterIsNull();
			}
			if(tableId<=0){
				throw this.newServiceExceptionParameterIsLessEqualsZero();
			}

			// validate
			if(validate){
				this.validate(credenzialeMittente);
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

			this.serviceCRUD.update(this.jdbcProperties,this.log,connection,sqlQueryObject,tableId,credenzialeMittente,idMappingResolutionBehaviour);
			
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
	public void updateFields(IdCredenzialeMittente id, UpdateField ... updateFields) throws ServiceException, NotFoundException, NotImplementedException {
	
		Connection connection = null;
		boolean oldValueAutoCommit = false;
		boolean rollback = false;
		try{
			
			// check parameters
			if(id==null){
				throw this.newServiceExceptionParameterIdIsNull();
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

			this.serviceCRUD.updateFields(this.jdbcProperties,this.log,connection,sqlQueryObject,id,updateFields);
			
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
	public void updateFields(IdCredenzialeMittente id, IExpression condition, UpdateField ... updateFields) throws ServiceException, NotFoundException, NotImplementedException {
	
		Connection connection = null;
		boolean oldValueAutoCommit = false;
		boolean rollback = false;
		try{
			
			// check parameters
			if(id==null){
				throw this.newServiceExceptionParameterIdIsNull();
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

			this.serviceCRUD.updateFields(this.jdbcProperties,this.log,connection,sqlQueryObject,id,condition,updateFields);
			
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
	public void updateFields(IdCredenzialeMittente id, UpdateModel ... updateModels) throws ServiceException, NotFoundException, NotImplementedException {
	
		Connection connection = null;
		boolean oldValueAutoCommit = false;
		boolean rollback = false;
		try{
			
			// check parameters
			if(id==null){
				throw this.newServiceExceptionParameterIdIsNull();
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

			this.serviceCRUD.updateFields(this.jdbcProperties,this.log,connection,sqlQueryObject,id,updateModels);
			
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
	public void updateOrCreate(IdCredenzialeMittente oldId, CredenzialeMittente credenzialeMittente) throws ServiceException, NotImplementedException {
		try{
			this.updateOrCreate(oldId, credenzialeMittente, false, null);
		}catch(ValidationException vE){
			// not possible
			throw new ServiceException(vE.getMessage(), vE);
		}
	}
	
	@Override
	public void updateOrCreate(IdCredenzialeMittente oldId, CredenzialeMittente credenzialeMittente, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws ServiceException, NotImplementedException {
		try{
			this.updateOrCreate(oldId, credenzialeMittente, false, idMappingResolutionBehaviour);
		}catch(ValidationException vE){
			// not possible
			throw new ServiceException(vE.getMessage(), vE);
		}
	}

	@Override
	public void updateOrCreate(IdCredenzialeMittente oldId, CredenzialeMittente credenzialeMittente, boolean validate) throws ServiceException, NotImplementedException, ValidationException {
		this.updateOrCreate(oldId, credenzialeMittente, validate, null);
	}

	@Override
	public void updateOrCreate(IdCredenzialeMittente oldId, CredenzialeMittente credenzialeMittente, boolean validate, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws ServiceException, NotImplementedException, ValidationException {
	
		Connection connection = null;
		boolean oldValueAutoCommit = false;
		boolean rollback = false;
		try{
			
			// check parameters
			if(credenzialeMittente==null){
				throw this.newServiceExceptionParameterIsNull();
			}
			if(oldId==null){
				throw this.newServiceExceptionParameterOldIdIsNull();
			}

			// validate
			if(validate){
				this.validate(credenzialeMittente);
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

			this.serviceCRUD.updateOrCreate(this.jdbcProperties,this.log,connection,sqlQueryObject,oldId,credenzialeMittente,idMappingResolutionBehaviour);
			
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
	public void updateOrCreate(long tableId, CredenzialeMittente credenzialeMittente) throws ServiceException, NotImplementedException {
		try{
			this.updateOrCreate(tableId, credenzialeMittente, false, null);
		}catch(ValidationException vE){
			// not possible
			throw new ServiceException(vE.getMessage(), vE);
		}
	}
	
	@Override
	public void updateOrCreate(long tableId, CredenzialeMittente credenzialeMittente, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws ServiceException, NotImplementedException {
		try{
			this.updateOrCreate(tableId, credenzialeMittente, false, idMappingResolutionBehaviour);
		}catch(ValidationException vE){
			// not possible
			throw new ServiceException(vE.getMessage(), vE);
		}
	}

	@Override
	public void updateOrCreate(long tableId, CredenzialeMittente credenzialeMittente, boolean validate) throws ServiceException, NotImplementedException, ValidationException {
		this.updateOrCreate(tableId, credenzialeMittente, validate, null);
	}

	@Override
	public void updateOrCreate(long tableId, CredenzialeMittente credenzialeMittente, boolean validate, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws ServiceException, NotImplementedException, ValidationException {

		Connection connection = null;
		boolean oldValueAutoCommit = false;
		boolean rollback = false;
		try{
			
			// check parameters
			if(credenzialeMittente==null){
				throw this.newServiceExceptionParameterIsNull();
			}
			if(tableId<=0){
				throw this.newServiceExceptionParameterIsLessEqualsZero();
			}

			// validate
			if(validate){
				this.validate(credenzialeMittente);
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

			this.serviceCRUD.updateOrCreate(this.jdbcProperties,this.log,connection,sqlQueryObject,tableId,credenzialeMittente,idMappingResolutionBehaviour);

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
	public void delete(CredenzialeMittente credenzialeMittente) throws ServiceException,NotImplementedException {
		
		Connection connection = null;
		boolean oldValueAutoCommit = false;
		boolean rollback = false;
		try{
			
			// check parameters
			if(credenzialeMittente==null){
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

			this.serviceCRUD.delete(this.jdbcProperties,this.log,connection,sqlQueryObject,credenzialeMittente);	

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
	public void deleteById(IdCredenzialeMittente id) throws ServiceException, NotImplementedException {

		Connection connection = null;
		boolean oldValueAutoCommit = false;
		boolean rollback = false;
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

			// transaction
			if(this.jdbcProperties.isAutomaticTransactionManagement()){
				oldValueAutoCommit = connection.getAutoCommit();
				connection.setAutoCommit(false);
			}

			this.serviceCRUD.deleteById(this.jdbcProperties,this.log,connection,sqlQueryObject,id);			

		}catch(ServiceException | NotImplementedException e){
			rollback = true;
			this.logError(e); throw e;
		}catch(Exception e){
			rollback = true;
			this.logError(e); throw new ServiceException("DeleteById not completed: "+e.getMessage(),e);
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
	
}
