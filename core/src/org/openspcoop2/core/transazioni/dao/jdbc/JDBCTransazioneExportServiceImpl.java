/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
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

import java.sql.Connection;

import org.openspcoop2.utils.sql.ISQLQueryObject;

import org.slf4j.Logger;

import org.openspcoop2.generic_project.dao.jdbc.IJDBCServiceCRUDWithoutId;
import org.openspcoop2.generic_project.beans.NonNegativeNumber;
import org.openspcoop2.generic_project.beans.UpdateField;
import org.openspcoop2.generic_project.beans.UpdateModel;

import org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities;
import org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.generic_project.exception.NotImplementedException;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.generic_project.expression.IExpression;
import org.openspcoop2.generic_project.dao.jdbc.JDBCExpression;
import org.openspcoop2.generic_project.dao.jdbc.JDBCPaginatedExpression;

import org.openspcoop2.generic_project.dao.jdbc.JDBCServiceManagerProperties;

import org.openspcoop2.core.transazioni.TransazioneExport;

/**     
 * JDBCTransazioneExportServiceImpl
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JDBCTransazioneExportServiceImpl extends JDBCTransazioneExportServiceSearchImpl
	implements IJDBCServiceCRUDWithoutId<TransazioneExport, JDBCServiceManager> {

	@Override
	public void create(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, TransazioneExport transazioneExport, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotImplementedException,ServiceException,Exception {

		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);
		
		ISQLQueryObject sqlQueryObjectInsert = sqlQueryObject.newSQLQueryObject();
				


		// Object transazioneExport
		sqlQueryObjectInsert.addInsertTable(this.getTransazioneExportFieldConverter().toTable(TransazioneExport.model()));
		sqlQueryObjectInsert.addInsertField(this.getTransazioneExportFieldConverter().toColumn(TransazioneExport.model().INTERVALLO_INIZIO,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneExportFieldConverter().toColumn(TransazioneExport.model().INTERVALLO_FINE,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneExportFieldConverter().toColumn(TransazioneExport.model().NOME,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneExportFieldConverter().toColumn(TransazioneExport.model().EXPORT_STATE,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneExportFieldConverter().toColumn(TransazioneExport.model().EXPORT_ERROR,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneExportFieldConverter().toColumn(TransazioneExport.model().EXPORT_TIME_START,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneExportFieldConverter().toColumn(TransazioneExport.model().EXPORT_TIME_END,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneExportFieldConverter().toColumn(TransazioneExport.model().DELETE_STATE,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneExportFieldConverter().toColumn(TransazioneExport.model().DELETE_ERROR,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneExportFieldConverter().toColumn(TransazioneExport.model().DELETE_TIME_START,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneExportFieldConverter().toColumn(TransazioneExport.model().DELETE_TIME_END,false),"?");

		// Insert transazioneExport
		org.openspcoop2.utils.jdbc.IKeyGeneratorObject keyGenerator = this.getTransazioneExportFetch().getKeyGeneratorObject(TransazioneExport.model());
		long id = jdbcUtilities.insertAndReturnGeneratedKey(sqlQueryObjectInsert, keyGenerator, jdbcProperties.isShowSql(),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazioneExport.getIntervalloInizio(),TransazioneExport.model().INTERVALLO_INIZIO.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazioneExport.getIntervalloFine(),TransazioneExport.model().INTERVALLO_FINE.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazioneExport.getNome(),TransazioneExport.model().NOME.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazioneExport.getExportState(),TransazioneExport.model().EXPORT_STATE.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazioneExport.getExportError(),TransazioneExport.model().EXPORT_ERROR.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazioneExport.getExportTimeStart(),TransazioneExport.model().EXPORT_TIME_START.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazioneExport.getExportTimeEnd(),TransazioneExport.model().EXPORT_TIME_END.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazioneExport.getDeleteState(),TransazioneExport.model().DELETE_STATE.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazioneExport.getDeleteError(),TransazioneExport.model().DELETE_ERROR.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazioneExport.getDeleteTimeStart(),TransazioneExport.model().DELETE_TIME_START.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazioneExport.getDeleteTimeEnd(),TransazioneExport.model().DELETE_TIME_END.getFieldType())
		);
		transazioneExport.setId(id);

		
	}

	@Override
	public void update(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, TransazioneExport transazioneExport, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		
		Long tableId = transazioneExport.getId();
		if(tableId==null || tableId<=0){
			throw new Exception("Retrieve tableId failed");
		}

		this.update(jdbcProperties, log, connection, sqlQueryObject, tableId, transazioneExport, idMappingResolutionBehaviour);
	}
	@Override
	public void update(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, TransazioneExport transazioneExport, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException, NotImplementedException, ServiceException, Exception {
	
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);
		
		ISQLQueryObject sqlQueryObjectInsert = sqlQueryObject.newSQLQueryObject();
		ISQLQueryObject sqlQueryObjectDelete = sqlQueryObjectInsert.newSQLQueryObject();
		ISQLQueryObject sqlQueryObjectGet = sqlQueryObjectDelete.newSQLQueryObject();
		ISQLQueryObject sqlQueryObjectUpdate = sqlQueryObjectGet.newSQLQueryObject();
		


		// Object transazioneExport
		sqlQueryObjectUpdate.setANDLogicOperator(true);
		sqlQueryObjectUpdate.addUpdateTable(this.getTransazioneExportFieldConverter().toTable(TransazioneExport.model()));
		boolean isUpdate_transazioneExport = true;
		java.util.List<JDBCObject> lstObjects_transazioneExport = new java.util.ArrayList<>();
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneExportFieldConverter().toColumn(TransazioneExport.model().INTERVALLO_INIZIO,false), "?");
		lstObjects_transazioneExport.add(new JDBCObject(transazioneExport.getIntervalloInizio(), TransazioneExport.model().INTERVALLO_INIZIO.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneExportFieldConverter().toColumn(TransazioneExport.model().INTERVALLO_FINE,false), "?");
		lstObjects_transazioneExport.add(new JDBCObject(transazioneExport.getIntervalloFine(), TransazioneExport.model().INTERVALLO_FINE.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneExportFieldConverter().toColumn(TransazioneExport.model().NOME,false), "?");
		lstObjects_transazioneExport.add(new JDBCObject(transazioneExport.getNome(), TransazioneExport.model().NOME.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneExportFieldConverter().toColumn(TransazioneExport.model().EXPORT_STATE,false), "?");
		lstObjects_transazioneExport.add(new JDBCObject(transazioneExport.getExportState(), TransazioneExport.model().EXPORT_STATE.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneExportFieldConverter().toColumn(TransazioneExport.model().EXPORT_ERROR,false), "?");
		lstObjects_transazioneExport.add(new JDBCObject(transazioneExport.getExportError(), TransazioneExport.model().EXPORT_ERROR.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneExportFieldConverter().toColumn(TransazioneExport.model().EXPORT_TIME_START,false), "?");
		lstObjects_transazioneExport.add(new JDBCObject(transazioneExport.getExportTimeStart(), TransazioneExport.model().EXPORT_TIME_START.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneExportFieldConverter().toColumn(TransazioneExport.model().EXPORT_TIME_END,false), "?");
		lstObjects_transazioneExport.add(new JDBCObject(transazioneExport.getExportTimeEnd(), TransazioneExport.model().EXPORT_TIME_END.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneExportFieldConverter().toColumn(TransazioneExport.model().DELETE_STATE,false), "?");
		lstObjects_transazioneExport.add(new JDBCObject(transazioneExport.getDeleteState(), TransazioneExport.model().DELETE_STATE.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneExportFieldConverter().toColumn(TransazioneExport.model().DELETE_ERROR,false), "?");
		lstObjects_transazioneExport.add(new JDBCObject(transazioneExport.getDeleteError(), TransazioneExport.model().DELETE_ERROR.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneExportFieldConverter().toColumn(TransazioneExport.model().DELETE_TIME_START,false), "?");
		lstObjects_transazioneExport.add(new JDBCObject(transazioneExport.getDeleteTimeStart(), TransazioneExport.model().DELETE_TIME_START.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneExportFieldConverter().toColumn(TransazioneExport.model().DELETE_TIME_END,false), "?");
		lstObjects_transazioneExport.add(new JDBCObject(transazioneExport.getDeleteTimeEnd(), TransazioneExport.model().DELETE_TIME_END.getFieldType()));
		sqlQueryObjectUpdate.addWhereCondition("id=?");
		lstObjects_transazioneExport.add(new JDBCObject(tableId, Long.class));

		if(isUpdate_transazioneExport) {
			// Update transazioneExport
			jdbcUtilities.executeUpdate(sqlQueryObjectUpdate.createSQLUpdate(), jdbcProperties.isShowSql(), 
				lstObjects_transazioneExport.toArray(new JDBCObject[]{}));
		}


	}
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, TransazioneExport transazioneExport, UpdateField ... updateFields) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		
		GenericJDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getTransazioneExportFieldConverter().toTable(TransazioneExport.model()), 
				this.getMapTableToPKColumnEngine(), 
				this.getRootTablePrimaryKeyValuesEngine(jdbcProperties, log, connection, sqlQueryObject, transazioneExport),
				this.getTransazioneExportFieldConverter(), this, null, updateFields);
	}
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, TransazioneExport transazioneExport, IExpression condition, UpdateField ... updateFields) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		
		GenericJDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getTransazioneExportFieldConverter().toTable(TransazioneExport.model()), 
				this.getMapTableToPKColumnEngine(), 
				this.getRootTablePrimaryKeyValuesEngine(jdbcProperties, log, connection, sqlQueryObject, transazioneExport),
				this.getTransazioneExportFieldConverter(), this, condition, updateFields);
	}
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, TransazioneExport transazioneExport, UpdateModel ... updateModels) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		
		GenericJDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getTransazioneExportFieldConverter().toTable(TransazioneExport.model()), 
				this.getMapTableToPKColumnEngine(), 
				this.getRootTablePrimaryKeyValuesEngine(jdbcProperties, log, connection, sqlQueryObject, transazioneExport),
				this.getTransazioneExportFieldConverter(), this, updateModels);
	}	
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, UpdateField ... updateFields) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		java.util.List<Object> ids = new java.util.ArrayList<>();
		ids.add(tableId);
		GenericJDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getTransazioneExportFieldConverter().toTable(TransazioneExport.model()), 
				this.getMapTableToPKColumnEngine(), 
				ids,
				this.getTransazioneExportFieldConverter(), this, null, updateFields);
	}
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, IExpression condition, UpdateField ... updateFields) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		java.util.List<Object> ids = new java.util.ArrayList<>();
		ids.add(tableId);
		GenericJDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getTransazioneExportFieldConverter().toTable(TransazioneExport.model()), 
				this.getMapTableToPKColumnEngine(), 
				ids,
				this.getTransazioneExportFieldConverter(), this, condition, updateFields);
	}
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, UpdateModel ... updateModels) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		java.util.List<Object> ids = new java.util.ArrayList<>();
		ids.add(tableId);
		GenericJDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getTransazioneExportFieldConverter().toTable(TransazioneExport.model()), 
				this.getMapTableToPKColumnEngine(), 
				ids,
				this.getTransazioneExportFieldConverter(), this, updateModels);
	}
	
	@Override
	public void updateOrCreate(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, TransazioneExport transazioneExport, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotImplementedException,ServiceException,Exception {
	
		Long id = transazioneExport.getId();
		if(id != null && this.exists(jdbcProperties, log, connection, sqlQueryObject, id)) {
			this.update(jdbcProperties, log, connection, sqlQueryObject, transazioneExport,idMappingResolutionBehaviour);		
		} else {
			this.create(jdbcProperties, log, connection, sqlQueryObject, transazioneExport,idMappingResolutionBehaviour);
		}
		
	}
	
	@Override
	public void updateOrCreate(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, TransazioneExport transazioneExport, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotImplementedException,ServiceException,Exception {
		if(this.exists(jdbcProperties, log, connection, sqlQueryObject, tableId)) {
			this.update(jdbcProperties, log, connection, sqlQueryObject, tableId, transazioneExport,idMappingResolutionBehaviour);
		} else {
			this.create(jdbcProperties, log, connection, sqlQueryObject, transazioneExport,idMappingResolutionBehaviour);
		}
	}
	
	@Override
	public void delete(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, TransazioneExport transazioneExport) throws NotImplementedException,ServiceException,Exception {
		
		
		Long longId = null;
		if(transazioneExport.getId()==null){
			throw new Exception("Parameter "+transazioneExport.getClass().getName()+".id is null");
		}
		if(transazioneExport.getId()<=0){
			throw new Exception("Parameter "+transazioneExport.getClass().getName()+".id is less equals 0");
		}
		longId = transazioneExport.getId();
		
		this.deleteEngine(jdbcProperties, log, connection, sqlQueryObject, longId);
		
	}

	private void deleteEngine(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, Long id) throws NotImplementedException,ServiceException,Exception {
	
		if(id!=null && id.longValue()<=0){
			throw new ServiceException("Id is less equals 0");
		}
		
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);
		
		ISQLQueryObject sqlQueryObjectDelete = sqlQueryObject.newSQLQueryObject();
		

		// Object transazioneExport
		sqlQueryObjectDelete.setANDLogicOperator(true);
		sqlQueryObjectDelete.addDeleteTable(this.getTransazioneExportFieldConverter().toTable(TransazioneExport.model()));
		if(id != null)
			sqlQueryObjectDelete.addWhereCondition("id=?");

		// Delete transazioneExport
		jdbcUtilities.execute(sqlQueryObjectDelete.createSQLDelete(), jdbcProperties.isShowSql(), 
			new JDBCObject(id,Long.class));

	}

	
	@Override
	public NonNegativeNumber deleteAll(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject) throws NotImplementedException,ServiceException,Exception {
		
		return this.deleteAll(jdbcProperties, log, connection, sqlQueryObject, new JDBCExpression(this.getTransazioneExportFieldConverter()));

	}

	@Override
	public NonNegativeNumber deleteAll(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCExpression expression) throws NotImplementedException, ServiceException,Exception {

		java.util.List<Long> lst = this.findAllTableIds(jdbcProperties, log, connection, sqlQueryObject, new JDBCPaginatedExpression(expression));
		
		for(Long id : lst) {
			this.deleteEngine(jdbcProperties, log, connection, sqlQueryObject, id);
		}
		
		return new NonNegativeNumber(lst.size());
	
	}



	// -- DB
	
	@Override
	public void deleteById(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId) throws ServiceException, NotImplementedException, Exception {
		this.deleteEngine(jdbcProperties, log, connection, sqlQueryObject, Long.valueOf(tableId));
	}
	
	@Override
	public int nativeUpdate(JDBCServiceManagerProperties jdbcProperties, Logger log,Connection connection,ISQLQueryObject sqlObject, String sql,Object ... param) throws ServiceException,NotImplementedException, Exception {
	
		return org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.nativeUpdate(jdbcProperties, log, connection, sqlObject,
																							sql,param);
	
	}
}
