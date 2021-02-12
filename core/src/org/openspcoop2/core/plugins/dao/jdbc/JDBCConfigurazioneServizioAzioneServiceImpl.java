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
package org.openspcoop2.core.plugins.dao.jdbc;

import java.sql.Connection;

import org.openspcoop2.utils.sql.ISQLQueryObject;

import org.slf4j.Logger;

import org.openspcoop2.generic_project.dao.jdbc.IJDBCServiceCRUDWithId;
import org.openspcoop2.core.plugins.IdConfigurazioneServizioAzione;
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
import org.openspcoop2.core.plugins.ConfigurazioneServizio;
import org.openspcoop2.core.plugins.ConfigurazioneServizioAzione;

/**     
 * JDBCConfigurazioneServizioAzioneServiceImpl
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JDBCConfigurazioneServizioAzioneServiceImpl extends JDBCConfigurazioneServizioAzioneServiceSearchImpl
	implements IJDBCServiceCRUDWithId<ConfigurazioneServizioAzione, IdConfigurazioneServizioAzione, JDBCServiceManager> {

	@Override
	public void create(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, ConfigurazioneServizioAzione configurazioneServizioAzione, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotImplementedException,ServiceException,Exception {

		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);
		
		ISQLQueryObject sqlQueryObjectInsert = sqlQueryObject.newSQLQueryObject();
				

		// Object _configurazioneServizio
		if(configurazioneServizioAzione.getIdConfigurazioneServizio()==null){
			throw new ServiceException("IdConfigurazioneServizio is null");
		}
				
		IExpression expressionSearchConfigurazioneServizio = this.getServiceManager().getConfigurazioneServizioServiceSearch().newExpression();
		expressionSearchConfigurazioneServizio.
			and().
			equals(ConfigurazioneServizio.model().ACCORDO, configurazioneServizioAzione.getIdConfigurazioneServizio().getAccordo()).
			equals(ConfigurazioneServizio.model().TIPO_SOGGETTO_REFERENTE, configurazioneServizioAzione.getIdConfigurazioneServizio().getTipoSoggettoReferente()).
			equals(ConfigurazioneServizio.model().NOME_SOGGETTO_REFERENTE, configurazioneServizioAzione.getIdConfigurazioneServizio().getNomeSoggettoReferente()).
			equals(ConfigurazioneServizio.model().VERSIONE, configurazioneServizioAzione.getIdConfigurazioneServizio().getVersione()).
			equals(ConfigurazioneServizio.model().SERVIZIO, configurazioneServizioAzione.getIdConfigurazioneServizio().getServizio());
		ConfigurazioneServizio configurazioneServizio = this.getServiceManager().getConfigurazioneServizioServiceSearch().find(expressionSearchConfigurazioneServizio);
		Long id_configurazioneServizio = configurazioneServizio.getId();


		// Object configurazioneServizioAzione
		sqlQueryObjectInsert.addInsertTable(this.getConfigurazioneServizioAzioneFieldConverter().toTable(ConfigurazioneServizioAzione.model()));
		sqlQueryObjectInsert.addInsertField(this.getConfigurazioneServizioAzioneFieldConverter().toColumn(ConfigurazioneServizioAzione.model().AZIONE,false),"?");
		sqlQueryObjectInsert.addInsertField("id_config_servizio","?");

		// Insert configurazioneServizioAzione
		org.openspcoop2.utils.jdbc.IKeyGeneratorObject keyGenerator = this.getConfigurazioneServizioAzioneFetch().getKeyGeneratorObject(ConfigurazioneServizioAzione.model());
		long id = jdbcUtilities.insertAndReturnGeneratedKey(sqlQueryObjectInsert, keyGenerator, jdbcProperties.isShowSql(),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(configurazioneServizioAzione.getAzione(),ConfigurazioneServizioAzione.model().AZIONE.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(id_configurazioneServizio,Long.class)
		);
		configurazioneServizioAzione.setId(id);

	
	}

	@Override
	public void update(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdConfigurazioneServizioAzione oldId, ConfigurazioneServizioAzione configurazioneServizioAzione, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		ISQLQueryObject sqlQueryObjectUpdate = sqlQueryObject.newSQLQueryObject();
		Long longIdByLogicId = this.findIdConfigurazioneServizioAzione(jdbcProperties, log, connection, sqlQueryObjectUpdate.newSQLQueryObject(), oldId, true);
		Long tableId = configurazioneServizioAzione.getId();
		if(tableId != null && tableId.longValue() > 0) {
			if(tableId.longValue() != longIdByLogicId.longValue()) {
				throw new Exception("Ambiguous parameter: configurazioneServizioAzione.id ["+tableId+"] does not match logic id ["+longIdByLogicId+"]");
			}
		} else {
			tableId = longIdByLogicId;
			configurazioneServizioAzione.setId(tableId);
		}
		if(tableId==null || tableId<=0){
			throw new Exception("Retrieve tableId failed");
		}

		this.update(jdbcProperties, log, connection, sqlQueryObject, tableId, configurazioneServizioAzione, idMappingResolutionBehaviour);
	}
	@Override
	public void update(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, ConfigurazioneServizioAzione configurazioneServizioAzione, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException, NotImplementedException, ServiceException, Exception {
	
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
			

		// Object _configurazioneServizioAzione_configurazioneServizio
		Long id_configurazioneServizioAzione_configurazioneServizio = null;
		org.openspcoop2.core.plugins.IdConfigurazioneServizio idLogic_configurazioneServizioAzione_configurazioneServizio = null;
		idLogic_configurazioneServizioAzione_configurazioneServizio = configurazioneServizioAzione.getIdConfigurazioneServizio();
		if(idLogic_configurazioneServizioAzione_configurazioneServizio!=null){
			if(idMappingResolutionBehaviour==null ||
				(org.openspcoop2.generic_project.beans.IDMappingBehaviour.ENABLED.equals(idMappingResolutionBehaviour))){
				id_configurazioneServizioAzione_configurazioneServizio = ((JDBCConfigurazioneServizioServiceSearch)(this.getServiceManager().getConfigurazioneServizioServiceSearch())).findTableId(idLogic_configurazioneServizioAzione_configurazioneServizio, false);
			}
			else if(org.openspcoop2.generic_project.beans.IDMappingBehaviour.USE_TABLE_ID.equals(idMappingResolutionBehaviour)){
				id_configurazioneServizioAzione_configurazioneServizio = idLogic_configurazioneServizioAzione_configurazioneServizio.getId();
				if(id_configurazioneServizioAzione_configurazioneServizio==null || id_configurazioneServizioAzione_configurazioneServizio<=0){
					throw new Exception("Logic id not contains table id");
				}
			}
		}


		// Object configurazioneServizioAzione
		sqlQueryObjectUpdate.setANDLogicOperator(true);
		sqlQueryObjectUpdate.addUpdateTable(this.getConfigurazioneServizioAzioneFieldConverter().toTable(ConfigurazioneServizioAzione.model()));
		boolean isUpdate_configurazioneServizioAzione = true;
		java.util.List<JDBCObject> lstObjects_configurazioneServizioAzione = new java.util.ArrayList<JDBCObject>();
		sqlQueryObjectUpdate.addUpdateField(this.getConfigurazioneServizioAzioneFieldConverter().toColumn(ConfigurazioneServizioAzione.model().AZIONE,false), "?");
		lstObjects_configurazioneServizioAzione.add(new JDBCObject(configurazioneServizioAzione.getAzione(), ConfigurazioneServizioAzione.model().AZIONE.getFieldType()));
		if(setIdMappingResolutionBehaviour){
			sqlQueryObjectUpdate.addUpdateField("id_config_servizio","?");
		}
		if(setIdMappingResolutionBehaviour){
			lstObjects_configurazioneServizioAzione.add(new JDBCObject(id_configurazioneServizioAzione_configurazioneServizio, Long.class));
		}
		sqlQueryObjectUpdate.addWhereCondition("id=?");
		lstObjects_configurazioneServizioAzione.add(new JDBCObject(tableId, Long.class));

		if(isUpdate_configurazioneServizioAzione) {
			// Update configurazioneServizioAzione
			jdbcUtilities.executeUpdate(sqlQueryObjectUpdate.createSQLUpdate(), jdbcProperties.isShowSql(), 
				lstObjects_configurazioneServizioAzione.toArray(new JDBCObject[]{}));
		}


	}
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdConfigurazioneServizioAzione id, UpdateField ... updateFields) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		
		JDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getConfigurazioneServizioAzioneFieldConverter().toTable(ConfigurazioneServizioAzione.model()), 
				this._getMapTableToPKColumn(), 
				this._getRootTablePrimaryKeyValues(jdbcProperties, log, connection, sqlQueryObject, id),
				this.getConfigurazioneServizioAzioneFieldConverter(), this, null, updateFields);
	}
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdConfigurazioneServizioAzione id, IExpression condition, UpdateField ... updateFields) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		
		JDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getConfigurazioneServizioAzioneFieldConverter().toTable(ConfigurazioneServizioAzione.model()), 
				this._getMapTableToPKColumn(), 
				this._getRootTablePrimaryKeyValues(jdbcProperties, log, connection, sqlQueryObject, id),
				this.getConfigurazioneServizioAzioneFieldConverter(), this, condition, updateFields);
	}
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdConfigurazioneServizioAzione id, UpdateModel ... updateModels) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		
		JDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getConfigurazioneServizioAzioneFieldConverter().toTable(ConfigurazioneServizioAzione.model()), 
				this._getMapTableToPKColumn(), 
				this._getRootTablePrimaryKeyValues(jdbcProperties, log, connection, sqlQueryObject, id),
				this.getConfigurazioneServizioAzioneFieldConverter(), this, updateModels);
	}	
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, UpdateField ... updateFields) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		java.util.List<Object> ids = new java.util.ArrayList<Object>();
		ids.add(tableId);
		JDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getConfigurazioneServizioAzioneFieldConverter().toTable(ConfigurazioneServizioAzione.model()), 
				this._getMapTableToPKColumn(), 
				ids,
				this.getConfigurazioneServizioAzioneFieldConverter(), this, null, updateFields);
	}
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, IExpression condition, UpdateField ... updateFields) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		java.util.List<Object> ids = new java.util.ArrayList<Object>();
		ids.add(tableId);
		JDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getConfigurazioneServizioAzioneFieldConverter().toTable(ConfigurazioneServizioAzione.model()), 
				this._getMapTableToPKColumn(), 
				ids,
				this.getConfigurazioneServizioAzioneFieldConverter(), this, condition, updateFields);
	}
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, UpdateModel ... updateModels) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		java.util.List<Object> ids = new java.util.ArrayList<Object>();
		ids.add(tableId);
		JDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getConfigurazioneServizioAzioneFieldConverter().toTable(ConfigurazioneServizioAzione.model()), 
				this._getMapTableToPKColumn(), 
				ids,
				this.getConfigurazioneServizioAzioneFieldConverter(), this, updateModels);
	}
	
	@Override
	public void updateOrCreate(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdConfigurazioneServizioAzione oldId, ConfigurazioneServizioAzione configurazioneServizioAzione, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotImplementedException,ServiceException,Exception {
	
		if(this.exists(jdbcProperties, log, connection, sqlQueryObject, oldId)) {
			this.update(jdbcProperties, log, connection, sqlQueryObject, oldId, configurazioneServizioAzione,idMappingResolutionBehaviour);
		} else {
			this.create(jdbcProperties, log, connection, sqlQueryObject, configurazioneServizioAzione,idMappingResolutionBehaviour);
		}
		
	}
	
	@Override
	public void updateOrCreate(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, ConfigurazioneServizioAzione configurazioneServizioAzione, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotImplementedException,ServiceException,Exception {
		if(this.exists(jdbcProperties, log, connection, sqlQueryObject, tableId)) {
			this.update(jdbcProperties, log, connection, sqlQueryObject, tableId, configurazioneServizioAzione,idMappingResolutionBehaviour);
		} else {
			this.create(jdbcProperties, log, connection, sqlQueryObject, configurazioneServizioAzione,idMappingResolutionBehaviour);
		}
	}
	
	@Override
	public void delete(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, ConfigurazioneServizioAzione configurazioneServizioAzione) throws NotImplementedException,ServiceException,Exception {
		
		
		Long longId = null;
		if( (configurazioneServizioAzione.getId()!=null) && (configurazioneServizioAzione.getId()>0) ){
			longId = configurazioneServizioAzione.getId();
		}
		else{
			IdConfigurazioneServizioAzione idConfigurazioneServizioAzione = this.convertToId(jdbcProperties,log,connection,sqlQueryObject,configurazioneServizioAzione);
			longId = this.findIdConfigurazioneServizioAzione(jdbcProperties,log,connection,sqlQueryObject,idConfigurazioneServizioAzione,false);
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
		

		// Object configurazioneServizioAzione
		sqlQueryObjectDelete.setANDLogicOperator(true);
		sqlQueryObjectDelete.addDeleteTable(this.getConfigurazioneServizioAzioneFieldConverter().toTable(ConfigurazioneServizioAzione.model()));
		if(id != null)
			sqlQueryObjectDelete.addWhereCondition("id=?");

		// Delete configurazioneServizioAzione
		jdbcUtilities.execute(sqlQueryObjectDelete.createSQLDelete(), jdbcProperties.isShowSql(), 
			new JDBCObject(id,Long.class));

	}

	@Override
	public void deleteById(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdConfigurazioneServizioAzione idConfigurazioneServizioAzione) throws NotImplementedException,ServiceException,Exception {

		Long id = null;
		try{
			id = this.findIdConfigurazioneServizioAzione(jdbcProperties, log, connection, sqlQueryObject, idConfigurazioneServizioAzione, true);
		}catch(NotFoundException notFound){
			return;
		}
		this._delete(jdbcProperties, log, connection, sqlQueryObject, id);
		
	}
	
	@Override
	public NonNegativeNumber deleteAll(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject) throws NotImplementedException,ServiceException,Exception {
		
		return this.deleteAll(jdbcProperties, log, connection, sqlQueryObject, new JDBCExpression(this.getConfigurazioneServizioAzioneFieldConverter()));

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
	
		return org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.nativeUpdate(jdbcProperties, log, connection, sqlObject,
																							sql,param);
	
	}
}
