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
package org.openspcoop2.monitor.engine.config.statistiche.dao.jdbc;

import java.sql.Connection;

import org.openspcoop2.utils.sql.ISQLQueryObject;

import org.slf4j.Logger;

import org.openspcoop2.monitor.engine.config.base.constants.TipoPlugin;
import org.openspcoop2.monitor.engine.config.base.dao.jdbc.JDBCConfigurazioneServizioAzioneBaseLib;
import org.openspcoop2.monitor.engine.config.base.dao.jdbc.JDBCPluginsBaseLib;

import org.openspcoop2.generic_project.dao.jdbc.IJDBCServiceCRUDWithId;
import org.openspcoop2.monitor.engine.config.statistiche.IdConfigurazioneStatistica;
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

import org.openspcoop2.monitor.engine.config.statistiche.ConfigurazioneStatistica;

/**     
 * JDBCConfigurazioneStatisticaServiceImpl
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JDBCConfigurazioneStatisticaServiceImpl extends JDBCConfigurazioneStatisticaServiceSearchImpl
	implements IJDBCServiceCRUDWithId<ConfigurazioneStatistica, IdConfigurazioneStatistica, JDBCServiceManager> {

	@Override
	public void create(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, ConfigurazioneStatistica configurazioneStatistica, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotImplementedException,ServiceException,Exception {

		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);
		
		ISQLQueryObject sqlQueryObjectInsert = sqlQueryObject.newSQLQueryObject();
		
		if(configurazioneStatistica.getIdConfigurazioneServizioAzione()==null){
			throw new ServiceException("IdConfigurazioneServizioAzione is null");
		}
		if(configurazioneStatistica.getIdConfigurazioneServizioAzione().getAzione()==null){
			throw new ServiceException("IdConfigurazioneServizioAzione.azione is null");
		}
		if(configurazioneStatistica.getIdConfigurazioneServizioAzione().getIdConfigurazioneServizio()==null){
			throw new ServiceException("IdConfigurazioneServizioAzione.idConfigurazioneServizio is null");
		}
				
		 // Object _configurazioneServizioAzione
		Long id_configurazioneServizioAzione = 
				JDBCConfigurazioneServizioAzioneBaseLib.getIdConfigurazioneServizioAzione(connection, jdbcProperties, log, 
						configurazioneStatistica.getIdConfigurazioneServizioAzione().getAzione(),
						configurazioneStatistica.getIdConfigurazioneServizioAzione().getIdConfigurazioneServizio().getAccordo(), 
						configurazioneStatistica.getIdConfigurazioneServizioAzione().getIdConfigurazioneServizio().getTipoSoggettoReferente(), 
						configurazioneStatistica.getIdConfigurazioneServizioAzione().getIdConfigurazioneServizio().getNomeSoggettoReferente(), 
						configurazioneStatistica.getIdConfigurazioneServizioAzione().getIdConfigurazioneServizio().getVersione(), 
						configurazioneStatistica.getIdConfigurazioneServizioAzione().getIdConfigurazioneServizio().getServizio(), 
						true);
		

		// Object _plugin
		if(configurazioneStatistica.getPlugin()==null || configurazioneStatistica.getPlugin().getClassName()==null){
			throw new ServiceException("ClassName del plugin non fornito");
		}
		Long id_plugin = JDBCPluginsBaseLib.getIdPlugin(connection, jdbcProperties, log, TipoPlugin.STATISTICA, configurazioneStatistica.getPlugin().getClassName(), true);


		// Object configurazioneStatistica
		sqlQueryObjectInsert.addInsertTable(this.getConfigurazioneStatisticaFieldConverter().toTable(ConfigurazioneStatistica.model()));
		sqlQueryObjectInsert.addInsertField(this.getConfigurazioneStatisticaFieldConverter().toColumn(ConfigurazioneStatistica.model().ID_CONFIGURAZIONE_STATISTICA,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getConfigurazioneStatisticaFieldConverter().toColumn(ConfigurazioneStatistica.model().ENABLED,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getConfigurazioneStatisticaFieldConverter().toColumn(ConfigurazioneStatistica.model().LABEL,false),"?");
		sqlQueryObjectInsert.addInsertField("id_configurazione","?");
		sqlQueryObjectInsert.addInsertField("id_plugin","?");

		// Insert configurazioneStatistica
		org.openspcoop2.utils.jdbc.IKeyGeneratorObject keyGenerator = this.getConfigurazioneStatisticaFetch().getKeyGeneratorObject(ConfigurazioneStatistica.model());
		long id = jdbcUtilities.insertAndReturnGeneratedKey(sqlQueryObjectInsert, keyGenerator, jdbcProperties.isShowSql(),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(configurazioneStatistica.getIdConfigurazioneStatistica(),ConfigurazioneStatistica.model().ID_CONFIGURAZIONE_STATISTICA.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(configurazioneStatistica.getEnabled(),ConfigurazioneStatistica.model().ENABLED.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(configurazioneStatistica.getLabel(),ConfigurazioneStatistica.model().LABEL.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(id_configurazioneServizioAzione,Long.class),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(id_plugin,Long.class)
		);
		configurazioneStatistica.setId(id);

		
	}

	@Override
	public void update(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdConfigurazioneStatistica oldId, ConfigurazioneStatistica configurazioneStatistica, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		ISQLQueryObject sqlQueryObjectUpdate = sqlQueryObject.newSQLQueryObject();
		Long longIdByLogicId = this.findIdConfigurazioneStatistica(jdbcProperties, log, connection, sqlQueryObjectUpdate.newSQLQueryObject(), oldId, true);
		Long tableId = configurazioneStatistica.getId();
		if(tableId != null && tableId.longValue() > 0) {
			if(tableId.longValue() != longIdByLogicId.longValue()) {
				throw new Exception("Ambiguous parameter: configurazioneStatistica.id ["+tableId+"] does not match logic id ["+longIdByLogicId+"]");
			}
		} else {
			tableId = longIdByLogicId;
			configurazioneStatistica.setId(tableId);
		}
		if(tableId==null || tableId<=0){
			throw new Exception("Retrieve tableId failed");
		}

		this.update(jdbcProperties, log, connection, sqlQueryObject, tableId, configurazioneStatistica, idMappingResolutionBehaviour);
	}
	@Override
	public void update(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, ConfigurazioneStatistica configurazioneStatistica, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException, NotImplementedException, ServiceException, Exception {
	
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
			

		// Object _configurazioneStatistica_configurazioneServizioAzione
		Long id_configurazioneStatistica_configurazioneServizioAzione = null;
		org.openspcoop2.monitor.engine.config.statistiche.IdConfigurazioneServizioAzione idLogic_configurazioneStatistica_configurazioneServizioAzione = null;
		idLogic_configurazioneStatistica_configurazioneServizioAzione = configurazioneStatistica.getIdConfigurazioneServizioAzione();
		if(idLogic_configurazioneStatistica_configurazioneServizioAzione!=null){
			if(idMappingResolutionBehaviour==null ||
				(org.openspcoop2.generic_project.beans.IDMappingBehaviour.ENABLED.equals(idMappingResolutionBehaviour))){
				id_configurazioneStatistica_configurazioneServizioAzione = 
						JDBCConfigurazioneServizioAzioneBaseLib.getIdConfigurazioneServizioAzione(connection, jdbcProperties, log, 
								idLogic_configurazioneStatistica_configurazioneServizioAzione.getAzione(),
								idLogic_configurazioneStatistica_configurazioneServizioAzione.getIdConfigurazioneServizio().getAccordo(), 
								idLogic_configurazioneStatistica_configurazioneServizioAzione.getIdConfigurazioneServizio().getTipoSoggettoReferente(), 
								idLogic_configurazioneStatistica_configurazioneServizioAzione.getIdConfigurazioneServizio().getNomeSoggettoReferente(), 
								idLogic_configurazioneStatistica_configurazioneServizioAzione.getIdConfigurazioneServizio().getVersione(), 
								idLogic_configurazioneStatistica_configurazioneServizioAzione.getIdConfigurazioneServizio().getServizio(), 
								true);
			}
			else if(org.openspcoop2.generic_project.beans.IDMappingBehaviour.USE_TABLE_ID.equals(idMappingResolutionBehaviour)){
				id_configurazioneStatistica_configurazioneServizioAzione = idLogic_configurazioneStatistica_configurazioneServizioAzione.getId();
				if(id_configurazioneStatistica_configurazioneServizioAzione==null || id_configurazioneStatistica_configurazioneServizioAzione<=0){
					throw new Exception("Logic id not contains table id");
				}
			}
			configurazioneStatistica.setId(tableId);
		}
		
		// Object _plugin
		if(configurazioneStatistica.getPlugin()==null || configurazioneStatistica.getPlugin().getClassName()==null){
			throw new ServiceException("ClassName del plugin non fornito");
		}
		Long id_plugin = JDBCPluginsBaseLib.getIdPlugin(connection, jdbcProperties, log, TipoPlugin.STATISTICA, configurazioneStatistica.getPlugin().getClassName(), true);


		// Object configurazioneStatistica
		sqlQueryObjectUpdate.setANDLogicOperator(true);
		sqlQueryObjectUpdate.addUpdateTable(this.getConfigurazioneStatisticaFieldConverter().toTable(ConfigurazioneStatistica.model()));
		boolean isUpdate_configurazioneStatistica = true;
		java.util.List<JDBCObject> lstObjects_configurazioneStatistica = new java.util.ArrayList<JDBCObject>();
		sqlQueryObjectUpdate.addUpdateField(this.getConfigurazioneStatisticaFieldConverter().toColumn(ConfigurazioneStatistica.model().ID_CONFIGURAZIONE_STATISTICA,false), "?");
		lstObjects_configurazioneStatistica.add(new JDBCObject(configurazioneStatistica.getIdConfigurazioneStatistica(), ConfigurazioneStatistica.model().ID_CONFIGURAZIONE_STATISTICA.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getConfigurazioneStatisticaFieldConverter().toColumn(ConfigurazioneStatistica.model().ENABLED,false), "?");
		lstObjects_configurazioneStatistica.add(new JDBCObject(configurazioneStatistica.getEnabled(), ConfigurazioneStatistica.model().ENABLED.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getConfigurazioneStatisticaFieldConverter().toColumn(ConfigurazioneStatistica.model().LABEL,false), "?");
		lstObjects_configurazioneStatistica.add(new JDBCObject(configurazioneStatistica.getLabel(), ConfigurazioneStatistica.model().LABEL.getFieldType()));
		if(setIdMappingResolutionBehaviour){
			sqlQueryObjectUpdate.addUpdateField("id_configurazione","?");
		}
		if(setIdMappingResolutionBehaviour){
			sqlQueryObjectUpdate.addUpdateField("id_plugin", "?");
		}
		if(setIdMappingResolutionBehaviour){
			lstObjects_configurazioneStatistica.add(new JDBCObject(id_configurazioneStatistica_configurazioneServizioAzione, Long.class));
		}
		if(setIdMappingResolutionBehaviour){
			lstObjects_configurazioneStatistica.add(new JDBCObject(id_plugin, Long.class));
		}
		sqlQueryObjectUpdate.addWhereCondition("pid=?");
		lstObjects_configurazioneStatistica.add(new JDBCObject(tableId, Long.class));

		if(isUpdate_configurazioneStatistica) {
			// Update configurazioneStatistica
			jdbcUtilities.executeUpdate(sqlQueryObjectUpdate.createSQLUpdate(), jdbcProperties.isShowSql(), 
				lstObjects_configurazioneStatistica.toArray(new JDBCObject[]{}));
		}

		
	}
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdConfigurazioneStatistica id, UpdateField ... updateFields) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		
		JDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getConfigurazioneStatisticaFieldConverter().toTable(ConfigurazioneStatistica.model()), 
				this._getMapTableToPKColumn(), 
				this._getRootTablePrimaryKeyValues(jdbcProperties, log, connection, sqlQueryObject, id),
				this.getConfigurazioneStatisticaFieldConverter(), this, null, updateFields);
	}
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdConfigurazioneStatistica id, IExpression condition, UpdateField ... updateFields) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		
		JDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getConfigurazioneStatisticaFieldConverter().toTable(ConfigurazioneStatistica.model()), 
				this._getMapTableToPKColumn(), 
				this._getRootTablePrimaryKeyValues(jdbcProperties, log, connection, sqlQueryObject, id),
				this.getConfigurazioneStatisticaFieldConverter(), this, condition, updateFields);
	}
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdConfigurazioneStatistica id, UpdateModel ... updateModels) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		
		JDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getConfigurazioneStatisticaFieldConverter().toTable(ConfigurazioneStatistica.model()), 
				this._getMapTableToPKColumn(), 
				this._getRootTablePrimaryKeyValues(jdbcProperties, log, connection, sqlQueryObject, id),
				this.getConfigurazioneStatisticaFieldConverter(), this, updateModels);
	}	
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, UpdateField ... updateFields) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		java.util.List<Object> ids = new java.util.ArrayList<Object>();
		ids.add(tableId);
		JDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getConfigurazioneStatisticaFieldConverter().toTable(ConfigurazioneStatistica.model()), 
				this._getMapTableToPKColumn(), 
				ids,
				this.getConfigurazioneStatisticaFieldConverter(), this, null, updateFields);
	}
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, IExpression condition, UpdateField ... updateFields) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		java.util.List<Object> ids = new java.util.ArrayList<Object>();
		ids.add(tableId);
		JDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getConfigurazioneStatisticaFieldConverter().toTable(ConfigurazioneStatistica.model()), 
				this._getMapTableToPKColumn(), 
				ids,
				this.getConfigurazioneStatisticaFieldConverter(), this, condition, updateFields);
	}
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, UpdateModel ... updateModels) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		java.util.List<Object> ids = new java.util.ArrayList<Object>();
		ids.add(tableId);
		JDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getConfigurazioneStatisticaFieldConverter().toTable(ConfigurazioneStatistica.model()), 
				this._getMapTableToPKColumn(), 
				ids,
				this.getConfigurazioneStatisticaFieldConverter(), this, updateModels);
	}
	
	@Override
	public void updateOrCreate(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdConfigurazioneStatistica oldId, ConfigurazioneStatistica configurazioneStatistica, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotImplementedException,ServiceException,Exception {
	
		if(this.exists(jdbcProperties, log, connection, sqlQueryObject, oldId)) {
			this.update(jdbcProperties, log, connection, sqlQueryObject, oldId, configurazioneStatistica,idMappingResolutionBehaviour);
		} else {
			this.create(jdbcProperties, log, connection, sqlQueryObject, configurazioneStatistica,idMappingResolutionBehaviour);
		}
		
	}
	
	@Override
	public void updateOrCreate(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, ConfigurazioneStatistica configurazioneStatistica, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotImplementedException,ServiceException,Exception {
		if(this.exists(jdbcProperties, log, connection, sqlQueryObject, tableId)) {
			this.update(jdbcProperties, log, connection, sqlQueryObject, tableId, configurazioneStatistica,idMappingResolutionBehaviour);
		} else {
			this.create(jdbcProperties, log, connection, sqlQueryObject, configurazioneStatistica,idMappingResolutionBehaviour);
		}
	}
	
	@Override
	public void delete(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, ConfigurazioneStatistica configurazioneStatistica) throws NotImplementedException,ServiceException,Exception {
		
		
		Long longId = null;
		if( (configurazioneStatistica.getId()!=null) && (configurazioneStatistica.getId()>0) ){
			longId = configurazioneStatistica.getId();
		}
		else{
			IdConfigurazioneStatistica idConfigurazioneStatistica = this.convertToId(jdbcProperties,log,connection,sqlQueryObject,configurazioneStatistica);
			longId = this.findIdConfigurazioneStatistica(jdbcProperties,log,connection,sqlQueryObject,idConfigurazioneStatistica,false);
			if(longId == null){
				return; // entry not exists
			}
		}		
		
		this._delete(jdbcProperties, log, connection, sqlQueryObject, longId);
		
	}

	private void _delete(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, Long id) throws NotImplementedException,ServiceException,Exception {
	
		if(id!=null && id.longValue()<=0){
			throw new ServiceException("Id is less equals 0");
		}
		
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);
		
		ISQLQueryObject sqlQueryObjectDelete = sqlQueryObject.newSQLQueryObject();
		

		// Object configurazioneStatistica
		sqlQueryObjectDelete.setANDLogicOperator(true);
		sqlQueryObjectDelete.addDeleteTable(this.getConfigurazioneStatisticaFieldConverter().toTable(ConfigurazioneStatistica.model()));
		if(id != null)
			sqlQueryObjectDelete.addWhereCondition("pid=?");

		// Delete configurazioneStatistica
		jdbcUtilities.execute(sqlQueryObjectDelete.createSQLDelete(), jdbcProperties.isShowSql(), 
			new JDBCObject(id,Long.class));

	}

	@Override
	public void deleteById(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdConfigurazioneStatistica idConfigurazioneStatistica) throws NotImplementedException,ServiceException,Exception {

		Long id = null;
		try{
			id = this.findIdConfigurazioneStatistica(jdbcProperties, log, connection, sqlQueryObject, idConfigurazioneStatistica, true);
		}catch(NotFoundException notFound){
			return;
		}
		this._delete(jdbcProperties, log, connection, sqlQueryObject, id);
		
	}
	
	@Override
	public NonNegativeNumber deleteAll(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject) throws NotImplementedException,ServiceException,Exception {
		
		return this.deleteAll(jdbcProperties, log, connection, sqlQueryObject, new JDBCExpression(this.getConfigurazioneStatisticaFieldConverter()));

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
