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

import java.sql.Connection;

import org.openspcoop2.utils.sql.ISQLQueryObject;

import org.slf4j.Logger;

import org.openspcoop2.generic_project.dao.jdbc.IJDBCServiceCRUDWithoutId;
import org.openspcoop2.generic_project.beans.NonNegativeNumber;
import org.openspcoop2.generic_project.beans.UpdateField;
import org.openspcoop2.generic_project.beans.UpdateModel;

import org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities;
import org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.generic_project.exception.NotImplementedException;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.generic_project.expression.IExpression;
import org.openspcoop2.generic_project.dao.jdbc.JDBCExpression;
import org.openspcoop2.generic_project.dao.jdbc.JDBCPaginatedExpression;

import org.openspcoop2.generic_project.dao.jdbc.JDBCServiceManagerProperties;

import org.openspcoop2.core.allarmi.AllarmeHistory;

/**     
 * JDBCAllarmeHistoryServiceImpl
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JDBCAllarmeHistoryServiceImpl extends JDBCAllarmeHistoryServiceSearchImpl
	implements IJDBCServiceCRUDWithoutId<AllarmeHistory, JDBCServiceManager> {

	@Override
	public void create(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, AllarmeHistory allarmeHistory, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotImplementedException,ServiceException,Exception {

		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);
		
		ISQLQueryObject sqlQueryObjectInsert = sqlQueryObject.newSQLQueryObject();
				

		// Object _allarme
		Long id_allarme = null;
		org.openspcoop2.core.allarmi.IdAllarme idLogic_allarme = null;
		idLogic_allarme = allarmeHistory.getIdAllarme();
		if(idLogic_allarme!=null){
			if(idMappingResolutionBehaviour==null ||
				(org.openspcoop2.generic_project.beans.IDMappingBehaviour.ENABLED.equals(idMappingResolutionBehaviour))){
				id_allarme = ((JDBCAllarmeServiceSearch)(this.getServiceManager().getAllarmeServiceSearch())).findTableId(idLogic_allarme, false);
			}
			else if(org.openspcoop2.generic_project.beans.IDMappingBehaviour.USE_TABLE_ID.equals(idMappingResolutionBehaviour)){
				id_allarme = idLogic_allarme.getId();
				if(id_allarme==null || id_allarme<=0){
					throw new Exception("Logic id not contains table id");
				}
			}
		}
		else {
			throw new ServiceException("IdAllarme not defined");
		}


		// Object allarmeHistory
		sqlQueryObjectInsert.addInsertTable(this.getAllarmeHistoryFieldConverter().toTable(AllarmeHistory.model()));
		sqlQueryObjectInsert.addInsertField(this.getAllarmeHistoryFieldConverter().toColumn(AllarmeHistory.model().ENABLED,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getAllarmeHistoryFieldConverter().toColumn(AllarmeHistory.model().STATO,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getAllarmeHistoryFieldConverter().toColumn(AllarmeHistory.model().DETTAGLIO_STATO,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getAllarmeHistoryFieldConverter().toColumn(AllarmeHistory.model().ACKNOWLEDGED,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getAllarmeHistoryFieldConverter().toColumn(AllarmeHistory.model().TIMESTAMP_UPDATE,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getAllarmeHistoryFieldConverter().toColumn(AllarmeHistory.model().UTENTE,false),"?");
		sqlQueryObjectInsert.addInsertField("id_allarme","?");

		// Insert allarmeHistory
		org.openspcoop2.utils.jdbc.IKeyGeneratorObject keyGenerator = this.getAllarmeHistoryFetch().getKeyGeneratorObject(AllarmeHistory.model());
		long id = jdbcUtilities.insertAndReturnGeneratedKey(sqlQueryObjectInsert, keyGenerator, jdbcProperties.isShowSql(),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(allarmeHistory.getEnabled(),AllarmeHistory.model().ENABLED.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(allarmeHistory.getStato(),AllarmeHistory.model().STATO.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(allarmeHistory.getDettaglioStato(),AllarmeHistory.model().DETTAGLIO_STATO.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(allarmeHistory.getAcknowledged(),AllarmeHistory.model().ACKNOWLEDGED.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(allarmeHistory.getTimestampUpdate(),AllarmeHistory.model().TIMESTAMP_UPDATE.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(allarmeHistory.getUtente(),AllarmeHistory.model().UTENTE.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(id_allarme,Long.class)
		);
		allarmeHistory.setId(id);

	}

	@Override
	public void update(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, AllarmeHistory allarmeHistory, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		
		Long tableId = allarmeHistory.getId();
		if(tableId==null || tableId<=0){
			throw new Exception("Retrieve tableId failed");
		}

		this.update(jdbcProperties, log, connection, sqlQueryObject, tableId, allarmeHistory, idMappingResolutionBehaviour);
	}
	@Override
	public void update(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, AllarmeHistory allarmeHistory, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException, NotImplementedException, ServiceException, Exception {
	
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);
		
		ISQLQueryObject sqlQueryObjectInsert = sqlQueryObject.newSQLQueryObject();
		ISQLQueryObject sqlQueryObjectDelete = sqlQueryObjectInsert.newSQLQueryObject();
		ISQLQueryObject sqlQueryObjectGet = sqlQueryObjectDelete.newSQLQueryObject();
		ISQLQueryObject sqlQueryObjectUpdate = sqlQueryObjectGet.newSQLQueryObject();
		
		boolean setIdMappingResolutionBehaviour = 
			(idMappingResolutionBehaviour==null) ||
			org.openspcoop2.generic_project.beans.IDMappingBehaviour.ENABLED.equals(idMappingResolutionBehaviour) ||
			org.openspcoop2.generic_project.beans.IDMappingBehaviour.USE_TABLE_ID.equals(idMappingResolutionBehaviour);
			

		// Object _allarme
		Long id_allarme = null;
		org.openspcoop2.core.allarmi.IdAllarme idLogic_allarme = null;
		idLogic_allarme = allarmeHistory.getIdAllarme();
		if(idLogic_allarme!=null){
			if(idMappingResolutionBehaviour==null ||
				(org.openspcoop2.generic_project.beans.IDMappingBehaviour.ENABLED.equals(idMappingResolutionBehaviour))){
				id_allarme = ((JDBCAllarmeServiceSearch)(this.getServiceManager().getAllarmeServiceSearch())).findTableId(idLogic_allarme, false);
			}
			else if(org.openspcoop2.generic_project.beans.IDMappingBehaviour.USE_TABLE_ID.equals(idMappingResolutionBehaviour)){
				id_allarme = idLogic_allarme.getId();
				if(id_allarme==null || id_allarme<=0){
					throw new Exception("Logic id not contains table id");
				}
			}
		}
		else {
			throw new ServiceException("IdAllarme not defined");
		}


		// Object allarmeHistory
		sqlQueryObjectUpdate.setANDLogicOperator(true);
		sqlQueryObjectUpdate.addUpdateTable(this.getAllarmeHistoryFieldConverter().toTable(AllarmeHistory.model()));
		boolean isUpdate = true;
		java.util.List<JDBCObject> lstObjects = new java.util.ArrayList<JDBCObject>();
		sqlQueryObjectUpdate.addUpdateField(this.getAllarmeHistoryFieldConverter().toColumn(AllarmeHistory.model().ENABLED,false), "?");
		lstObjects.add(new JDBCObject(allarmeHistory.getEnabled(), AllarmeHistory.model().ENABLED.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getAllarmeHistoryFieldConverter().toColumn(AllarmeHistory.model().STATO,false), "?");
		lstObjects.add(new JDBCObject(allarmeHistory.getStato(), AllarmeHistory.model().STATO.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getAllarmeHistoryFieldConverter().toColumn(AllarmeHistory.model().DETTAGLIO_STATO,false), "?");
		lstObjects.add(new JDBCObject(allarmeHistory.getDettaglioStato(), AllarmeHistory.model().DETTAGLIO_STATO.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getAllarmeHistoryFieldConverter().toColumn(AllarmeHistory.model().ACKNOWLEDGED,false), "?");
		lstObjects.add(new JDBCObject(allarmeHistory.getAcknowledged(), AllarmeHistory.model().ACKNOWLEDGED.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getAllarmeHistoryFieldConverter().toColumn(AllarmeHistory.model().TIMESTAMP_UPDATE,false), "?");
		lstObjects.add(new JDBCObject(allarmeHistory.getTimestampUpdate(), AllarmeHistory.model().TIMESTAMP_UPDATE.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getAllarmeHistoryFieldConverter().toColumn(AllarmeHistory.model().UTENTE,false), "?");
		lstObjects.add(new JDBCObject(allarmeHistory.getUtente(), AllarmeHistory.model().UTENTE.getFieldType()));
		if(setIdMappingResolutionBehaviour){
			sqlQueryObjectUpdate.addUpdateField("id_allarme","?");
		}
		if(setIdMappingResolutionBehaviour){
			lstObjects.add(new JDBCObject(id_allarme, Long.class));
		}
		sqlQueryObjectUpdate.addWhereCondition("id=?");
		lstObjects.add(new JDBCObject(tableId, Long.class));

		if(isUpdate) {
			// Update allarmeHistory
			jdbcUtilities.executeUpdate(sqlQueryObjectUpdate.createSQLUpdate(), jdbcProperties.isShowSql(), 
				lstObjects.toArray(new JDBCObject[]{}));
		}

	}
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, AllarmeHistory allarmeHistory, UpdateField ... updateFields) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		
		JDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getAllarmeHistoryFieldConverter().toTable(AllarmeHistory.model()), 
				this._getMapTableToPKColumn(), 
				this._getRootTablePrimaryKeyValues(jdbcProperties, log, connection, sqlQueryObject, allarmeHistory),
				this.getAllarmeHistoryFieldConverter(), this, null, updateFields);
	}
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, AllarmeHistory allarmeHistory, IExpression condition, UpdateField ... updateFields) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		
		JDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getAllarmeHistoryFieldConverter().toTable(AllarmeHistory.model()), 
				this._getMapTableToPKColumn(), 
				this._getRootTablePrimaryKeyValues(jdbcProperties, log, connection, sqlQueryObject, allarmeHistory),
				this.getAllarmeHistoryFieldConverter(), this, condition, updateFields);
	}
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, AllarmeHistory allarmeHistory, UpdateModel ... updateModels) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		
		JDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getAllarmeHistoryFieldConverter().toTable(AllarmeHistory.model()), 
				this._getMapTableToPKColumn(), 
				this._getRootTablePrimaryKeyValues(jdbcProperties, log, connection, sqlQueryObject, allarmeHistory),
				this.getAllarmeHistoryFieldConverter(), this, updateModels);
	}	
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, UpdateField ... updateFields) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		java.util.List<Object> ids = new java.util.ArrayList<Object>();
		ids.add(tableId);
		JDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getAllarmeHistoryFieldConverter().toTable(AllarmeHistory.model()), 
				this._getMapTableToPKColumn(), 
				ids,
				this.getAllarmeHistoryFieldConverter(), this, null, updateFields);
	}
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, IExpression condition, UpdateField ... updateFields) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		java.util.List<Object> ids = new java.util.ArrayList<Object>();
		ids.add(tableId);
		JDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getAllarmeHistoryFieldConverter().toTable(AllarmeHistory.model()), 
				this._getMapTableToPKColumn(), 
				ids,
				this.getAllarmeHistoryFieldConverter(), this, condition, updateFields);
	}
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, UpdateModel ... updateModels) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		java.util.List<Object> ids = new java.util.ArrayList<Object>();
		ids.add(tableId);
		JDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getAllarmeHistoryFieldConverter().toTable(AllarmeHistory.model()), 
				this._getMapTableToPKColumn(), 
				ids,
				this.getAllarmeHistoryFieldConverter(), this, updateModels);
	}
	
	@Override
	public void updateOrCreate(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, AllarmeHistory allarmeHistory, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotImplementedException,ServiceException,Exception {
	
		Long id = allarmeHistory.getId();
		if(id != null && this.exists(jdbcProperties, log, connection, sqlQueryObject, id)) {
			this.update(jdbcProperties, log, connection, sqlQueryObject, allarmeHistory,idMappingResolutionBehaviour);		
		} else {
			this.create(jdbcProperties, log, connection, sqlQueryObject, allarmeHistory,idMappingResolutionBehaviour);
		}
		
	}
	
	@Override
	public void updateOrCreate(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, AllarmeHistory allarmeHistory, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotImplementedException,ServiceException,Exception {
		if(this.exists(jdbcProperties, log, connection, sqlQueryObject, tableId)) {
			this.update(jdbcProperties, log, connection, sqlQueryObject, tableId, allarmeHistory,idMappingResolutionBehaviour);
		} else {
			this.create(jdbcProperties, log, connection, sqlQueryObject, allarmeHistory,idMappingResolutionBehaviour);
		}
	}
	
	@Override
	public void delete(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, AllarmeHistory allarmeHistory) throws NotImplementedException,ServiceException,Exception {
		
		
		Long longId = null;
		if(allarmeHistory.getId()==null){
			throw new Exception("Parameter "+allarmeHistory.getClass().getName()+".id is null");
		}
		if(allarmeHistory.getId()<=0){
			throw new Exception("Parameter "+allarmeHistory.getClass().getName()+".id is less equals 0");
		}
		longId = allarmeHistory.getId();
		
		this._delete(jdbcProperties, log, connection, sqlQueryObject, longId);
		
	}

	private void _delete(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, Long id) throws NotImplementedException,ServiceException,Exception {
	
		if(id!=null && id.longValue()<=0){
			throw new ServiceException("Id is less equals 0");
		}
		
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);
		
		ISQLQueryObject sqlQueryObjectDelete = sqlQueryObject.newSQLQueryObject();
		

		// Object allarmeHistory
		sqlQueryObjectDelete.setANDLogicOperator(true);
		sqlQueryObjectDelete.addDeleteTable(this.getAllarmeHistoryFieldConverter().toTable(AllarmeHistory.model()));
		if(id != null)
			sqlQueryObjectDelete.addWhereCondition("id=?");

		// Delete allarmeHistory
		jdbcUtilities.execute(sqlQueryObjectDelete.createSQLDelete(), jdbcProperties.isShowSql(), 
			new JDBCObject(id,Long.class));

	}

	
	@Override
	public NonNegativeNumber deleteAll(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject) throws NotImplementedException,ServiceException,Exception {
		
		return this.deleteAll(jdbcProperties, log, connection, sqlQueryObject, new JDBCExpression(this.getAllarmeHistoryFieldConverter()));

	}

	@Override
	public NonNegativeNumber deleteAll(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCExpression expression) throws NotImplementedException, ServiceException,Exception {

		java.util.List<Long> lst = this.findAllTableIds(jdbcProperties, log, connection, sqlQueryObject, new JDBCPaginatedExpression(expression));
		
		for(Long id : lst) {
			this._delete(jdbcProperties, log, connection, sqlQueryObject, id);
		}
		
		return new NonNegativeNumber(lst.size());
	
	}



	// -- DB
	
	@Override
	public void deleteById(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId) throws ServiceException, NotImplementedException, Exception {
		this._delete(jdbcProperties, log, connection, sqlQueryObject, Long.valueOf(tableId));
	}
}
