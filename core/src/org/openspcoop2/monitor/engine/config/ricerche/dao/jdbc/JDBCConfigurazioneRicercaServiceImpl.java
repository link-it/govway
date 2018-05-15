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
package org.openspcoop2.monitor.engine.config.ricerche.dao.jdbc;

import java.sql.Connection;

import org.openspcoop2.utils.sql.ISQLQueryObject;

import org.slf4j.Logger;

import org.openspcoop2.monitor.engine.config.base.constants.TipoPlugin;
import org.openspcoop2.monitor.engine.config.base.dao.jdbc.JDBCConfigurazioneServizioAzioneBaseLib;
import org.openspcoop2.monitor.engine.config.base.dao.jdbc.JDBCPluginsBaseLib;

import org.openspcoop2.generic_project.dao.jdbc.IJDBCServiceCRUDWithId;
import org.openspcoop2.monitor.engine.config.ricerche.IdConfigurazioneRicerca;
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

import org.openspcoop2.monitor.engine.config.ricerche.ConfigurazioneRicerca;
import org.openspcoop2.monitor.engine.config.ricerche.dao.jdbc.JDBCServiceManager;

/**     
 * JDBCConfigurazioneRicercaServiceImpl
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JDBCConfigurazioneRicercaServiceImpl extends JDBCConfigurazioneRicercaServiceSearchImpl
	implements IJDBCServiceCRUDWithId<ConfigurazioneRicerca, IdConfigurazioneRicerca, JDBCServiceManager> {

	@Override
	public void create(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, ConfigurazioneRicerca configurazioneRicerca, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotImplementedException,ServiceException,Exception {

		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);
		
		ISQLQueryObject sqlQueryObjectInsert = sqlQueryObject.newSQLQueryObject();
				

        // Object _configurazioneServizioAzione
		Long id_configurazioneServizioAzione = 
				JDBCConfigurazioneServizioAzioneBaseLib.getIdConfigurazioneServizioAzione(connection, jdbcProperties, log, 
						configurazioneRicerca.getIdConfigurazioneServizioAzione().getAzione(),
						configurazioneRicerca.getIdConfigurazioneServizioAzione().getIdConfigurazioneServizio().getAccordo(), 
						configurazioneRicerca.getIdConfigurazioneServizioAzione().getIdConfigurazioneServizio().getTipoSoggettoReferente(), 
						configurazioneRicerca.getIdConfigurazioneServizioAzione().getIdConfigurazioneServizio().getNomeSoggettoReferente(), 
						configurazioneRicerca.getIdConfigurazioneServizioAzione().getIdConfigurazioneServizio().getVersione(), 
						configurazioneRicerca.getIdConfigurazioneServizioAzione().getIdConfigurazioneServizio().getServizio(), 
						true);

		// Object _plugin
		if(configurazioneRicerca.getPlugin()==null || configurazioneRicerca.getPlugin().getClassName()==null){
			throw new ServiceException("ClassName del plugin non fornito");
		}
		Long id_plugin = JDBCPluginsBaseLib.getIdPlugin(connection, jdbcProperties, log, TipoPlugin.RICERCA, configurazioneRicerca.getPlugin().getClassName(), true);


		// Object configurazioneRicerca
		sqlQueryObjectInsert.addInsertTable(this.getConfigurazioneRicercaFieldConverter().toTable(ConfigurazioneRicerca.model()));
		sqlQueryObjectInsert.addInsertField(this.getConfigurazioneRicercaFieldConverter().toColumn(ConfigurazioneRicerca.model().ID_CONFIGURAZIONE_RICERCA,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getConfigurazioneRicercaFieldConverter().toColumn(ConfigurazioneRicerca.model().ENABLED,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getConfigurazioneRicercaFieldConverter().toColumn(ConfigurazioneRicerca.model().LABEL,false),"?");
		sqlQueryObjectInsert.addInsertField("id_configurazione","?");
		sqlQueryObjectInsert.addInsertField("id_plugin","?");

		// Insert configurazioneRicerca
		org.openspcoop2.utils.jdbc.IKeyGeneratorObject keyGenerator = this.getConfigurazioneRicercaFetch().getKeyGeneratorObject(ConfigurazioneRicerca.model());
		long id = jdbcUtilities.insertAndReturnGeneratedKey(sqlQueryObjectInsert, keyGenerator, jdbcProperties.isShowSql(),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(configurazioneRicerca.getIdConfigurazioneRicerca(),ConfigurazioneRicerca.model().ID_CONFIGURAZIONE_RICERCA.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(configurazioneRicerca.getEnabled(),ConfigurazioneRicerca.model().ENABLED.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(configurazioneRicerca.getLabel(),ConfigurazioneRicerca.model().LABEL.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(id_configurazioneServizioAzione,Long.class),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(id_plugin,Long.class)
		);
		configurazioneRicerca.setId(id);

		
	}

	@Override
	public void update(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdConfigurazioneRicerca oldId, ConfigurazioneRicerca configurazioneRicerca, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		ISQLQueryObject sqlQueryObjectUpdate = sqlQueryObject.newSQLQueryObject();
		Long longIdByLogicId = this.findIdConfigurazioneRicerca(jdbcProperties, log, connection, sqlQueryObjectUpdate.newSQLQueryObject(), oldId, true);
		Long tableId = configurazioneRicerca.getId();
		if(tableId != null && tableId.longValue() > 0) {
			if(tableId.longValue() != longIdByLogicId.longValue()) {
				throw new Exception("Ambiguous parameter: configurazioneRicerca.id ["+tableId+"] does not match logic id ["+longIdByLogicId+"]");
			}
		} else {
			tableId = longIdByLogicId;
			configurazioneRicerca.setId(tableId);
		}
		if(tableId==null || tableId<=0){
			throw new Exception("Retrieve tableId failed");
		}

		this.update(jdbcProperties, log, connection, sqlQueryObject, tableId, configurazioneRicerca, idMappingResolutionBehaviour);
	}
	@Override
	public void update(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, ConfigurazioneRicerca configurazioneRicerca, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException, NotImplementedException, ServiceException, Exception {
	
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
			

		// Object _configurazioneRicerca_configurazioneServizioAzione
		Long id_configurazioneRicerca_configurazioneServizioAzione = null;
		org.openspcoop2.monitor.engine.config.ricerche.IdConfigurazioneServizioAzione idLogic_configurazioneRicerca_configurazioneServizioAzione = null;
		idLogic_configurazioneRicerca_configurazioneServizioAzione = configurazioneRicerca.getIdConfigurazioneServizioAzione();
		if(idLogic_configurazioneRicerca_configurazioneServizioAzione!=null){
			if(idMappingResolutionBehaviour==null ||
				(org.openspcoop2.generic_project.beans.IDMappingBehaviour.ENABLED.equals(idMappingResolutionBehaviour))){
				id_configurazioneRicerca_configurazioneServizioAzione = 
						JDBCConfigurazioneServizioAzioneBaseLib.getIdConfigurazioneServizioAzione(connection, jdbcProperties, log, 
								idLogic_configurazioneRicerca_configurazioneServizioAzione.getAzione(),
								idLogic_configurazioneRicerca_configurazioneServizioAzione.getIdConfigurazioneServizio().getAccordo(), 
								idLogic_configurazioneRicerca_configurazioneServizioAzione.getIdConfigurazioneServizio().getTipoSoggettoReferente(), 
								idLogic_configurazioneRicerca_configurazioneServizioAzione.getIdConfigurazioneServizio().getNomeSoggettoReferente(), 
								idLogic_configurazioneRicerca_configurazioneServizioAzione.getIdConfigurazioneServizio().getVersione(), 
								idLogic_configurazioneRicerca_configurazioneServizioAzione.getIdConfigurazioneServizio().getServizio(), 
								true);
			}
			else if(org.openspcoop2.generic_project.beans.IDMappingBehaviour.USE_TABLE_ID.equals(idMappingResolutionBehaviour)){
				id_configurazioneRicerca_configurazioneServizioAzione = idLogic_configurazioneRicerca_configurazioneServizioAzione.getId();
				if(id_configurazioneRicerca_configurazioneServizioAzione==null || id_configurazioneRicerca_configurazioneServizioAzione<=0){
					throw new Exception("Logic id not contains table id");
				}
			}
			configurazioneRicerca.setId(tableId);
		}

		// Object _plugin
		if(configurazioneRicerca.getPlugin()==null || configurazioneRicerca.getPlugin().getClassName()==null){
			throw new ServiceException("ClassName del plugin non fornito");
		}
		Long id_plugin = JDBCPluginsBaseLib.getIdPlugin(connection, jdbcProperties, log, TipoPlugin.RICERCA, configurazioneRicerca.getPlugin().getClassName(), true);
		
		// Object configurazioneRicerca
		sqlQueryObjectUpdate.setANDLogicOperator(true);
		sqlQueryObjectUpdate.addUpdateTable(this.getConfigurazioneRicercaFieldConverter().toTable(ConfigurazioneRicerca.model()));
		boolean isUpdate_configurazioneRicerca = true;
		java.util.List<JDBCObject> lstObjects_configurazioneRicerca = new java.util.ArrayList<JDBCObject>();
		sqlQueryObjectUpdate.addUpdateField(this.getConfigurazioneRicercaFieldConverter().toColumn(ConfigurazioneRicerca.model().ID_CONFIGURAZIONE_RICERCA,false), "?");
		lstObjects_configurazioneRicerca.add(new JDBCObject(configurazioneRicerca.getIdConfigurazioneRicerca(), ConfigurazioneRicerca.model().ID_CONFIGURAZIONE_RICERCA.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getConfigurazioneRicercaFieldConverter().toColumn(ConfigurazioneRicerca.model().ENABLED,false), "?");
		lstObjects_configurazioneRicerca.add(new JDBCObject(configurazioneRicerca.getEnabled(), ConfigurazioneRicerca.model().ENABLED.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getConfigurazioneRicercaFieldConverter().toColumn(ConfigurazioneRicerca.model().LABEL,false), "?");
		lstObjects_configurazioneRicerca.add(new JDBCObject(configurazioneRicerca.getLabel(), ConfigurazioneRicerca.model().LABEL.getFieldType()));
		if(setIdMappingResolutionBehaviour){
			sqlQueryObjectUpdate.addUpdateField("id_configurazione","?");
		}
		if(setIdMappingResolutionBehaviour){
			sqlQueryObjectUpdate.addUpdateField("id_plugin", "?");
		}
		if(setIdMappingResolutionBehaviour){
			lstObjects_configurazioneRicerca.add(new JDBCObject(id_configurazioneRicerca_configurazioneServizioAzione, Long.class));
		}
		if(setIdMappingResolutionBehaviour){
			lstObjects_configurazioneRicerca.add(new JDBCObject(id_plugin, Long.class));
		}
		sqlQueryObjectUpdate.addWhereCondition("pid=?");
		lstObjects_configurazioneRicerca.add(new JDBCObject(tableId, Long.class));

		if(isUpdate_configurazioneRicerca) {
			// Update configurazioneRicerca
			jdbcUtilities.executeUpdate(sqlQueryObjectUpdate.createSQLUpdate(), jdbcProperties.isShowSql(), 
				lstObjects_configurazioneRicerca.toArray(new JDBCObject[]{}));
		}

	}
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdConfigurazioneRicerca id, UpdateField ... updateFields) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		
		JDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getConfigurazioneRicercaFieldConverter().toTable(ConfigurazioneRicerca.model()), 
				this._getMapTableToPKColumn(), 
				this._getRootTablePrimaryKeyValues(jdbcProperties, log, connection, sqlQueryObject, id),
				this.getConfigurazioneRicercaFieldConverter(), this, null, updateFields);
	}
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdConfigurazioneRicerca id, IExpression condition, UpdateField ... updateFields) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		
		JDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getConfigurazioneRicercaFieldConverter().toTable(ConfigurazioneRicerca.model()), 
				this._getMapTableToPKColumn(), 
				this._getRootTablePrimaryKeyValues(jdbcProperties, log, connection, sqlQueryObject, id),
				this.getConfigurazioneRicercaFieldConverter(), this, condition, updateFields);
	}
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdConfigurazioneRicerca id, UpdateModel ... updateModels) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		
		JDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getConfigurazioneRicercaFieldConverter().toTable(ConfigurazioneRicerca.model()), 
				this._getMapTableToPKColumn(), 
				this._getRootTablePrimaryKeyValues(jdbcProperties, log, connection, sqlQueryObject, id),
				this.getConfigurazioneRicercaFieldConverter(), this, updateModels);
	}	
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, UpdateField ... updateFields) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		java.util.List<Object> ids = new java.util.ArrayList<Object>();
		ids.add(tableId);
		JDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getConfigurazioneRicercaFieldConverter().toTable(ConfigurazioneRicerca.model()), 
				this._getMapTableToPKColumn(), 
				ids,
				this.getConfigurazioneRicercaFieldConverter(), this, null, updateFields);
	}
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, IExpression condition, UpdateField ... updateFields) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		java.util.List<Object> ids = new java.util.ArrayList<Object>();
		ids.add(tableId);
		JDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getConfigurazioneRicercaFieldConverter().toTable(ConfigurazioneRicerca.model()), 
				this._getMapTableToPKColumn(), 
				ids,
				this.getConfigurazioneRicercaFieldConverter(), this, condition, updateFields);
	}
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, UpdateModel ... updateModels) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		java.util.List<Object> ids = new java.util.ArrayList<Object>();
		ids.add(tableId);
		JDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getConfigurazioneRicercaFieldConverter().toTable(ConfigurazioneRicerca.model()), 
				this._getMapTableToPKColumn(), 
				ids,
				this.getConfigurazioneRicercaFieldConverter(), this, updateModels);
	}
	
	@Override
	public void updateOrCreate(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdConfigurazioneRicerca oldId, ConfigurazioneRicerca configurazioneRicerca, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotImplementedException,ServiceException,Exception {
	
		if(this.exists(jdbcProperties, log, connection, sqlQueryObject, oldId)) {
			this.update(jdbcProperties, log, connection, sqlQueryObject, oldId, configurazioneRicerca,idMappingResolutionBehaviour);
		} else {
			this.create(jdbcProperties, log, connection, sqlQueryObject, configurazioneRicerca,idMappingResolutionBehaviour);
		}
		
	}
	
	@Override
	public void updateOrCreate(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, ConfigurazioneRicerca configurazioneRicerca, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotImplementedException,ServiceException,Exception {
		if(this.exists(jdbcProperties, log, connection, sqlQueryObject, tableId)) {
			this.update(jdbcProperties, log, connection, sqlQueryObject, tableId, configurazioneRicerca,idMappingResolutionBehaviour);
		} else {
			this.create(jdbcProperties, log, connection, sqlQueryObject, configurazioneRicerca,idMappingResolutionBehaviour);
		}
	}
	
	@Override
	public void delete(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, ConfigurazioneRicerca configurazioneRicerca) throws NotImplementedException,ServiceException,Exception {
		
		
		Long longId = null;
		if( (configurazioneRicerca.getId()!=null) && (configurazioneRicerca.getId()>0) ){
			longId = configurazioneRicerca.getId();
		}
		else{
			IdConfigurazioneRicerca idConfigurazioneRicerca = this.convertToId(jdbcProperties,log,connection,sqlQueryObject,configurazioneRicerca);
			longId = this.findIdConfigurazioneRicerca(jdbcProperties,log,connection,sqlQueryObject,idConfigurazioneRicerca,false);
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
		

		// Object configurazioneRicerca
		sqlQueryObjectDelete.setANDLogicOperator(true);
		sqlQueryObjectDelete.addDeleteTable(this.getConfigurazioneRicercaFieldConverter().toTable(ConfigurazioneRicerca.model()));
		if(id != null)
			sqlQueryObjectDelete.addWhereCondition("pid=?");

		// Delete configurazioneRicerca
		jdbcUtilities.execute(sqlQueryObjectDelete.createSQLDelete(), jdbcProperties.isShowSql(), 
			new JDBCObject(id,Long.class));

	}

	@Override
	public void deleteById(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdConfigurazioneRicerca idConfigurazioneRicerca) throws NotImplementedException,ServiceException,Exception {

		Long id = null;
		try{
			id = this.findIdConfigurazioneRicerca(jdbcProperties, log, connection, sqlQueryObject, idConfigurazioneRicerca, true);
		}catch(NotFoundException notFound){
			return;
		}
		this._delete(jdbcProperties, log, connection, sqlQueryObject, id);
		
	}
	
	@Override
	public NonNegativeNumber deleteAll(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject) throws NotImplementedException,ServiceException,Exception {
		
		return this.deleteAll(jdbcProperties, log, connection, sqlQueryObject, new JDBCExpression(this.getConfigurazioneRicercaFieldConverter()));

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
