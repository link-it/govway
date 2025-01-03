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
package org.openspcoop2.core.controllo_traffico.dao.jdbc;

import java.sql.Connection;

import org.openspcoop2.core.controllo_traffico.ConfigurazioneRateLimitingProprieta;
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
 * JDBCConfigurazioneRateLimitingProprietaServiceImpl
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JDBCConfigurazioneRateLimitingProprietaServiceImpl extends JDBCConfigurazioneRateLimitingProprietaServiceSearchImpl
	implements IJDBCServiceCRUDWithoutId<ConfigurazioneRateLimitingProprieta, JDBCServiceManager> {

	@Override
	public void create(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, ConfigurazioneRateLimitingProprieta configurazioneRateLimitingProprieta, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotImplementedException,ServiceException,Exception {

		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);
		
		ISQLQueryObject sqlQueryObjectInsert = sqlQueryObject.newSQLQueryObject();
				


		// Object configurazioneRateLimitingProprieta
		sqlQueryObjectInsert.addInsertTable(this.getConfigurazioneRateLimitingProprietaFieldConverter().toTable(ConfigurazioneRateLimitingProprieta.model()));
		sqlQueryObjectInsert.addInsertField(this.getConfigurazioneRateLimitingProprietaFieldConverter().toColumn(ConfigurazioneRateLimitingProprieta.model().NOME,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getConfigurazioneRateLimitingProprietaFieldConverter().toColumn(ConfigurazioneRateLimitingProprieta.model().VALORE,false),"?");

		// Insert configurazioneRateLimitingProprieta
		org.openspcoop2.utils.jdbc.IKeyGeneratorObject keyGenerator = this.getConfigurazioneRateLimitingProprietaFetch().getKeyGeneratorObject(ConfigurazioneRateLimitingProprieta.model());
		long id = jdbcUtilities.insertAndReturnGeneratedKey(sqlQueryObjectInsert, keyGenerator, jdbcProperties.isShowSql(),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(configurazioneRateLimitingProprieta.getNome(),ConfigurazioneRateLimitingProprieta.model().NOME.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(configurazioneRateLimitingProprieta.getValore(),ConfigurazioneRateLimitingProprieta.model().VALORE.getFieldType())
		);
		configurazioneRateLimitingProprieta.setId(id);

		
	}

	@Override
	public void update(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, ConfigurazioneRateLimitingProprieta configurazioneRateLimitingProprieta, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		
		Long tableId = configurazioneRateLimitingProprieta.getId();
		if(tableId==null || tableId<=0){
			throw new Exception("Retrieve tableId failed");
		}

		this.update(jdbcProperties, log, connection, sqlQueryObject, tableId, configurazioneRateLimitingProprieta, idMappingResolutionBehaviour);
	}
	@Override
	public void update(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, ConfigurazioneRateLimitingProprieta configurazioneRateLimitingProprieta, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException, NotImplementedException, ServiceException, Exception {
	
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);
		
		ISQLQueryObject sqlQueryObjectInsert = sqlQueryObject.newSQLQueryObject();
		ISQLQueryObject sqlQueryObjectDelete = sqlQueryObjectInsert.newSQLQueryObject();
		ISQLQueryObject sqlQueryObjectGet = sqlQueryObjectDelete.newSQLQueryObject();
		ISQLQueryObject sqlQueryObjectUpdate = sqlQueryObjectGet.newSQLQueryObject();
		


		// Object configurazioneRateLimitingProprieta
		sqlQueryObjectUpdate.setANDLogicOperator(true);
		sqlQueryObjectUpdate.addUpdateTable(this.getConfigurazioneRateLimitingProprietaFieldConverter().toTable(ConfigurazioneRateLimitingProprieta.model()));
		boolean isUpdate = true;
		java.util.List<JDBCObject> lstObjects = new java.util.ArrayList<>();
		sqlQueryObjectUpdate.addUpdateField(this.getConfigurazioneRateLimitingProprietaFieldConverter().toColumn(ConfigurazioneRateLimitingProprieta.model().NOME,false), "?");
		lstObjects.add(new JDBCObject(configurazioneRateLimitingProprieta.getNome(), ConfigurazioneRateLimitingProprieta.model().NOME.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getConfigurazioneRateLimitingProprietaFieldConverter().toColumn(ConfigurazioneRateLimitingProprieta.model().VALORE,false), "?");
		lstObjects.add(new JDBCObject(configurazioneRateLimitingProprieta.getValore(), ConfigurazioneRateLimitingProprieta.model().VALORE.getFieldType()));
		sqlQueryObjectUpdate.addWhereCondition("id=?");
		lstObjects.add(new JDBCObject(tableId, Long.class));

		if(isUpdate) {
			// Update configurazioneRateLimitingProprieta
			jdbcUtilities.executeUpdate(sqlQueryObjectUpdate.createSQLUpdate(), jdbcProperties.isShowSql(), 
				lstObjects.toArray(new JDBCObject[]{}));
		}


	}
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, ConfigurazioneRateLimitingProprieta configurazioneRateLimitingProprieta, UpdateField ... updateFields) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		
		GenericJDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getConfigurazioneRateLimitingProprietaFieldConverter().toTable(ConfigurazioneRateLimitingProprieta.model()), 
				this.getMapTableToPKColumnEngine(), 
				this.getRootTablePrimaryKeyValuesEngine(jdbcProperties, log, connection, sqlQueryObject, configurazioneRateLimitingProprieta),
				this.getConfigurazioneRateLimitingProprietaFieldConverter(), this, null, updateFields);
	}
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, ConfigurazioneRateLimitingProprieta configurazioneRateLimitingProprieta, IExpression condition, UpdateField ... updateFields) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		
		GenericJDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getConfigurazioneRateLimitingProprietaFieldConverter().toTable(ConfigurazioneRateLimitingProprieta.model()), 
				this.getMapTableToPKColumnEngine(), 
				this.getRootTablePrimaryKeyValuesEngine(jdbcProperties, log, connection, sqlQueryObject, configurazioneRateLimitingProprieta),
				this.getConfigurazioneRateLimitingProprietaFieldConverter(), this, condition, updateFields);
	}
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, ConfigurazioneRateLimitingProprieta configurazioneRateLimitingProprieta, UpdateModel ... updateModels) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		
		GenericJDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getConfigurazioneRateLimitingProprietaFieldConverter().toTable(ConfigurazioneRateLimitingProprieta.model()), 
				this.getMapTableToPKColumnEngine(), 
				this.getRootTablePrimaryKeyValuesEngine(jdbcProperties, log, connection, sqlQueryObject, configurazioneRateLimitingProprieta),
				this.getConfigurazioneRateLimitingProprietaFieldConverter(), this, updateModels);
	}	
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, UpdateField ... updateFields) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		java.util.List<Object> ids = new java.util.ArrayList<>();
		ids.add(tableId);
		GenericJDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getConfigurazioneRateLimitingProprietaFieldConverter().toTable(ConfigurazioneRateLimitingProprieta.model()), 
				this.getMapTableToPKColumnEngine(), 
				ids,
				this.getConfigurazioneRateLimitingProprietaFieldConverter(), this, null, updateFields);
	}
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, IExpression condition, UpdateField ... updateFields) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		java.util.List<Object> ids = new java.util.ArrayList<>();
		ids.add(tableId);
		GenericJDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getConfigurazioneRateLimitingProprietaFieldConverter().toTable(ConfigurazioneRateLimitingProprieta.model()), 
				this.getMapTableToPKColumnEngine(), 
				ids,
				this.getConfigurazioneRateLimitingProprietaFieldConverter(), this, condition, updateFields);
	}
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, UpdateModel ... updateModels) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		java.util.List<Object> ids = new java.util.ArrayList<>();
		ids.add(tableId);
		GenericJDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getConfigurazioneRateLimitingProprietaFieldConverter().toTable(ConfigurazioneRateLimitingProprieta.model()), 
				this.getMapTableToPKColumnEngine(), 
				ids,
				this.getConfigurazioneRateLimitingProprietaFieldConverter(), this, updateModels);
	}
	
	@Override
	public void updateOrCreate(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, ConfigurazioneRateLimitingProprieta configurazioneRateLimitingProprieta, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotImplementedException,ServiceException,Exception {
	
		Long id = configurazioneRateLimitingProprieta.getId();
		if(id != null && this.exists(jdbcProperties, log, connection, sqlQueryObject, id)) {
			this.update(jdbcProperties, log, connection, sqlQueryObject, configurazioneRateLimitingProprieta,idMappingResolutionBehaviour);		
		} else {
			this.create(jdbcProperties, log, connection, sqlQueryObject, configurazioneRateLimitingProprieta,idMappingResolutionBehaviour);
		}
		
	}
	
	@Override
	public void updateOrCreate(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, ConfigurazioneRateLimitingProprieta configurazioneRateLimitingProprieta, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotImplementedException,ServiceException,Exception {
		if(this.exists(jdbcProperties, log, connection, sqlQueryObject, tableId)) {
			this.update(jdbcProperties, log, connection, sqlQueryObject, tableId, configurazioneRateLimitingProprieta,idMappingResolutionBehaviour);
		} else {
			this.create(jdbcProperties, log, connection, sqlQueryObject, configurazioneRateLimitingProprieta,idMappingResolutionBehaviour);
		}
	}
	
	@Override
	public void delete(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, ConfigurazioneRateLimitingProprieta configurazioneRateLimitingProprieta) throws NotImplementedException,ServiceException,Exception {
		
		
		Long longId = null;
		if(configurazioneRateLimitingProprieta.getId()==null){
			throw new Exception("Parameter "+configurazioneRateLimitingProprieta.getClass().getName()+".id is null");
		}
		if(configurazioneRateLimitingProprieta.getId()<=0){
			throw new Exception("Parameter "+configurazioneRateLimitingProprieta.getClass().getName()+".id is less equals 0");
		}
		longId = configurazioneRateLimitingProprieta.getId();
		
		this.deleteEngine(jdbcProperties, log, connection, sqlQueryObject, longId);
		
	}

	private void deleteEngine(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, Long id) throws NotImplementedException,ServiceException,Exception {
	
		if(id!=null && id.longValue()<=0){
			throw new ServiceException("Id is less equals 0");
		}
		
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);
		
		ISQLQueryObject sqlQueryObjectDelete = sqlQueryObject.newSQLQueryObject();
		

		// Object configurazioneRateLimitingProprieta
		sqlQueryObjectDelete.setANDLogicOperator(true);
		sqlQueryObjectDelete.addDeleteTable(this.getConfigurazioneRateLimitingProprietaFieldConverter().toTable(ConfigurazioneRateLimitingProprieta.model()));
		if(id != null)
			sqlQueryObjectDelete.addWhereCondition("id=?");

		// Delete configurazioneRateLimitingProprieta
		jdbcUtilities.execute(sqlQueryObjectDelete.createSQLDelete(), jdbcProperties.isShowSql(), 
			new JDBCObject(id,Long.class));

	}

	
	@Override
	public NonNegativeNumber deleteAll(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject) throws NotImplementedException,ServiceException,Exception {
		
		return this.deleteAll(jdbcProperties, log, connection, sqlQueryObject, new JDBCExpression(this.getConfigurazioneRateLimitingProprietaFieldConverter()));

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
