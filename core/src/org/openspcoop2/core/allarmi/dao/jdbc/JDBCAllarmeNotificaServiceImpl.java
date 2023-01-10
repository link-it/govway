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

package org.openspcoop2.core.allarmi.dao.jdbc;

import java.sql.Connection;

import org.openspcoop2.core.allarmi.AllarmeNotifica;
import org.openspcoop2.generic_project.beans.NonNegativeNumber;
import org.openspcoop2.generic_project.beans.UpdateField;
import org.openspcoop2.generic_project.beans.UpdateModel;
import org.openspcoop2.generic_project.dao.jdbc.IJDBCServiceCRUDWithoutId;
import org.openspcoop2.generic_project.dao.jdbc.JDBCExpression;
import org.openspcoop2.generic_project.dao.jdbc.JDBCPaginatedExpression;
import org.openspcoop2.generic_project.dao.jdbc.JDBCServiceManagerProperties;
import org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject;
import org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.generic_project.exception.NotImplementedException;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.generic_project.expression.IExpression;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.slf4j.Logger;

/**     
 * JDBCAllarmeNotificaServiceImpl
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JDBCAllarmeNotificaServiceImpl extends JDBCAllarmeNotificaServiceSearchImpl
	implements IJDBCServiceCRUDWithoutId<AllarmeNotifica, JDBCServiceManager> {

	@Override
	public void create(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, AllarmeNotifica allarmeNotifica, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotImplementedException,ServiceException,Exception {

		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);
		
		ISQLQueryObject sqlQueryObjectInsert = sqlQueryObject.newSQLQueryObject();
				

		// Object _allarme
		Long id_allarme = null;
		org.openspcoop2.core.allarmi.IdAllarme idLogic_allarme = null;
		idLogic_allarme = allarmeNotifica.getIdAllarme();
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


		// Object allarmeNotifica
		sqlQueryObjectInsert.addInsertTable(this.getAllarmeNotificaFieldConverter().toTable(AllarmeNotifica.model()));
		sqlQueryObjectInsert.addInsertField(this.getAllarmeNotificaFieldConverter().toColumn(AllarmeNotifica.model().DATA_NOTIFICA,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getAllarmeNotificaFieldConverter().toColumn(AllarmeNotifica.model().OLD_STATO,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getAllarmeNotificaFieldConverter().toColumn(AllarmeNotifica.model().OLD_DETTAGLIO_STATO,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getAllarmeNotificaFieldConverter().toColumn(AllarmeNotifica.model().NUOVO_STATO,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getAllarmeNotificaFieldConverter().toColumn(AllarmeNotifica.model().NUOVO_DETTAGLIO_STATO,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getAllarmeNotificaFieldConverter().toColumn(AllarmeNotifica.model().HISTORY_ENTRY,false),"?");
		sqlQueryObjectInsert.addInsertField("id_allarme","?");

		// Insert allarmeNotifica
		org.openspcoop2.utils.jdbc.IKeyGeneratorObject keyGenerator = this.getAllarmeNotificaFetch().getKeyGeneratorObject(AllarmeNotifica.model());
		long id = jdbcUtilities.insertAndReturnGeneratedKey(sqlQueryObjectInsert, keyGenerator, jdbcProperties.isShowSql(),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(allarmeNotifica.getDataNotifica(),AllarmeNotifica.model().DATA_NOTIFICA.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(allarmeNotifica.getOldStato(),AllarmeNotifica.model().OLD_STATO.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(allarmeNotifica.getOldDettaglioStato(),AllarmeNotifica.model().OLD_DETTAGLIO_STATO.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(allarmeNotifica.getNuovoStato(),AllarmeNotifica.model().NUOVO_STATO.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(allarmeNotifica.getNuovoDettaglioStato(),AllarmeNotifica.model().NUOVO_DETTAGLIO_STATO.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(allarmeNotifica.getHistoryEntry(),AllarmeNotifica.model().HISTORY_ENTRY.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(id_allarme,Long.class)
		);
		allarmeNotifica.setId(id);

	}

	@Override
	public void update(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, AllarmeNotifica allarmeNotifica, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		
		Long tableId = allarmeNotifica.getId();
		if(tableId==null || tableId<=0){
			throw new Exception("Retrieve tableId failed");
		}

		this.update(jdbcProperties, log, connection, sqlQueryObject, tableId, allarmeNotifica, idMappingResolutionBehaviour);
	}
	@Override
	public void update(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, AllarmeNotifica allarmeNotifica, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException, NotImplementedException, ServiceException, Exception {
	
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
		idLogic_allarme = allarmeNotifica.getIdAllarme();
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


		// Object allarmeNotifica
		sqlQueryObjectUpdate.setANDLogicOperator(true);
		sqlQueryObjectUpdate.addUpdateTable(this.getAllarmeNotificaFieldConverter().toTable(AllarmeNotifica.model()));
		boolean isUpdate = true;
		java.util.List<JDBCObject> lstObjects = new java.util.ArrayList<JDBCObject>();
		sqlQueryObjectUpdate.addUpdateField(this.getAllarmeNotificaFieldConverter().toColumn(AllarmeNotifica.model().DATA_NOTIFICA,false), "?");
		lstObjects.add(new JDBCObject(allarmeNotifica.getDataNotifica(), AllarmeNotifica.model().DATA_NOTIFICA.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getAllarmeNotificaFieldConverter().toColumn(AllarmeNotifica.model().OLD_STATO,false), "?");
		lstObjects.add(new JDBCObject(allarmeNotifica.getOldStato(), AllarmeNotifica.model().OLD_STATO.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getAllarmeNotificaFieldConverter().toColumn(AllarmeNotifica.model().OLD_DETTAGLIO_STATO,false), "?");
		lstObjects.add(new JDBCObject(allarmeNotifica.getOldDettaglioStato(), AllarmeNotifica.model().OLD_DETTAGLIO_STATO.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getAllarmeNotificaFieldConverter().toColumn(AllarmeNotifica.model().NUOVO_STATO,false), "?");
		lstObjects.add(new JDBCObject(allarmeNotifica.getNuovoStato(), AllarmeNotifica.model().NUOVO_STATO.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getAllarmeNotificaFieldConverter().toColumn(AllarmeNotifica.model().NUOVO_DETTAGLIO_STATO,false), "?");
		lstObjects.add(new JDBCObject(allarmeNotifica.getNuovoDettaglioStato(), AllarmeNotifica.model().NUOVO_DETTAGLIO_STATO.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getAllarmeNotificaFieldConverter().toColumn(AllarmeNotifica.model().HISTORY_ENTRY,false), "?");
		lstObjects.add(new JDBCObject(allarmeNotifica.getHistoryEntry(), AllarmeNotifica.model().HISTORY_ENTRY.getFieldType()));
		if(setIdMappingResolutionBehaviour){
			sqlQueryObjectUpdate.addUpdateField("id_allarme","?");
		}
		if(setIdMappingResolutionBehaviour){
			lstObjects.add(new JDBCObject(id_allarme, Long.class));
		}
		sqlQueryObjectUpdate.addWhereCondition("id=?");
		lstObjects.add(new JDBCObject(tableId, Long.class));

		if(isUpdate) {
			// Update allarmeNotifica
			jdbcUtilities.executeUpdate(sqlQueryObjectUpdate.createSQLUpdate(), jdbcProperties.isShowSql(), 
				lstObjects.toArray(new JDBCObject[]{}));
		}

	}
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, AllarmeNotifica allarmeNotifica, UpdateField ... updateFields) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		
		GenericJDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getAllarmeNotificaFieldConverter().toTable(AllarmeNotifica.model()), 
				this._getMapTableToPKColumn(), 
				this._getRootTablePrimaryKeyValues(jdbcProperties, log, connection, sqlQueryObject, allarmeNotifica),
				this.getAllarmeNotificaFieldConverter(), this, null, updateFields);
	}
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, AllarmeNotifica allarmeNotifica, IExpression condition, UpdateField ... updateFields) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		
		GenericJDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getAllarmeNotificaFieldConverter().toTable(AllarmeNotifica.model()), 
				this._getMapTableToPKColumn(), 
				this._getRootTablePrimaryKeyValues(jdbcProperties, log, connection, sqlQueryObject, allarmeNotifica),
				this.getAllarmeNotificaFieldConverter(), this, condition, updateFields);
	}
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, AllarmeNotifica allarmeNotifica, UpdateModel ... updateModels) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		
		GenericJDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getAllarmeNotificaFieldConverter().toTable(AllarmeNotifica.model()), 
				this._getMapTableToPKColumn(), 
				this._getRootTablePrimaryKeyValues(jdbcProperties, log, connection, sqlQueryObject, allarmeNotifica),
				this.getAllarmeNotificaFieldConverter(), this, updateModels);
	}	
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, UpdateField ... updateFields) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		java.util.List<Object> ids = new java.util.ArrayList<Object>();
		ids.add(tableId);
		GenericJDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getAllarmeNotificaFieldConverter().toTable(AllarmeNotifica.model()), 
				this._getMapTableToPKColumn(), 
				ids,
				this.getAllarmeNotificaFieldConverter(), this, null, updateFields);
	}
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, IExpression condition, UpdateField ... updateFields) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		java.util.List<Object> ids = new java.util.ArrayList<Object>();
		ids.add(tableId);
		GenericJDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getAllarmeNotificaFieldConverter().toTable(AllarmeNotifica.model()), 
				this._getMapTableToPKColumn(), 
				ids,
				this.getAllarmeNotificaFieldConverter(), this, condition, updateFields);
	}
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, UpdateModel ... updateModels) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		java.util.List<Object> ids = new java.util.ArrayList<Object>();
		ids.add(tableId);
		GenericJDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getAllarmeNotificaFieldConverter().toTable(AllarmeNotifica.model()), 
				this._getMapTableToPKColumn(), 
				ids,
				this.getAllarmeNotificaFieldConverter(), this, updateModels);
	}
	
	@Override
	public void updateOrCreate(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, AllarmeNotifica allarmeNotifica, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotImplementedException,ServiceException,Exception {
	
		Long id = allarmeNotifica.getId();
		if(id != null && this.exists(jdbcProperties, log, connection, sqlQueryObject, id)) {
			this.update(jdbcProperties, log, connection, sqlQueryObject, allarmeNotifica,idMappingResolutionBehaviour);		
		} else {
			this.create(jdbcProperties, log, connection, sqlQueryObject, allarmeNotifica,idMappingResolutionBehaviour);
		}
		
	}
	
	@Override
	public void updateOrCreate(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, AllarmeNotifica allarmeNotifica, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotImplementedException,ServiceException,Exception {
		if(this.exists(jdbcProperties, log, connection, sqlQueryObject, tableId)) {
			this.update(jdbcProperties, log, connection, sqlQueryObject, tableId, allarmeNotifica,idMappingResolutionBehaviour);
		} else {
			this.create(jdbcProperties, log, connection, sqlQueryObject, allarmeNotifica,idMappingResolutionBehaviour);
		}
	}
	
	@Override
	public void delete(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, AllarmeNotifica allarmeNotifica) throws NotImplementedException,ServiceException,Exception {
		
		
		Long longId = null;
		if(allarmeNotifica.getId()==null){
			throw new Exception("Parameter "+allarmeNotifica.getClass().getName()+".id is null");
		}
		if(allarmeNotifica.getId()<=0){
			throw new Exception("Parameter "+allarmeNotifica.getClass().getName()+".id is less equals 0");
		}
		longId = allarmeNotifica.getId();
		
		this._delete(jdbcProperties, log, connection, sqlQueryObject, longId);
		
	}

	private void _delete(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, Long id) throws NotImplementedException,ServiceException,Exception {
	
		if(id!=null && id.longValue()<=0){
			throw new ServiceException("Id is less equals 0");
		}
		
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);
		
		ISQLQueryObject sqlQueryObjectDelete = sqlQueryObject.newSQLQueryObject();
		

		// Object allarmeNotifica
		sqlQueryObjectDelete.setANDLogicOperator(true);
		sqlQueryObjectDelete.addDeleteTable(this.getAllarmeNotificaFieldConverter().toTable(AllarmeNotifica.model()));
		if(id != null)
			sqlQueryObjectDelete.addWhereCondition("id=?");

		// Delete allarmeNotifica
		jdbcUtilities.execute(sqlQueryObjectDelete.createSQLDelete(), jdbcProperties.isShowSql(), 
			new JDBCObject(id,Long.class));

	}

	
	@Override
	public NonNegativeNumber deleteAll(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject) throws NotImplementedException,ServiceException,Exception {
		
		return this.deleteAll(jdbcProperties, log, connection, sqlQueryObject, new JDBCExpression(this.getAllarmeNotificaFieldConverter()));

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
	
	@Override
	public int nativeUpdate(JDBCServiceManagerProperties jdbcProperties, Logger log,Connection connection,ISQLQueryObject sqlObject, String sql,Object ... param) throws ServiceException,NotImplementedException, Exception {
	
		return org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.nativeUpdate(jdbcProperties, log, connection, sqlObject,
																							sql,param);
	
	}
}
