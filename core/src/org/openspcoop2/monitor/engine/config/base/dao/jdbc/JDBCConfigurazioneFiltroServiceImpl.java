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
package org.openspcoop2.monitor.engine.config.base.dao.jdbc;

import java.sql.Connection;

import org.openspcoop2.utils.sql.ISQLQueryObject;

import org.slf4j.Logger;

import org.openspcoop2.generic_project.dao.jdbc.IJDBCServiceCRUDWithId;
import org.openspcoop2.monitor.engine.config.base.IdConfigurazioneFiltro;
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

import org.openspcoop2.monitor.engine.config.base.ConfigurazioneFiltro;

/**     
 * JDBCConfigurazioneFiltroServiceImpl
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JDBCConfigurazioneFiltroServiceImpl extends JDBCConfigurazioneFiltroServiceSearchImpl
	implements IJDBCServiceCRUDWithId<ConfigurazioneFiltro, IdConfigurazioneFiltro, JDBCServiceManager> {

	@Override
	public void create(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, ConfigurazioneFiltro configurazioneFiltro, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotImplementedException,ServiceException,Exception {

		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);
		
		ISQLQueryObject sqlQueryObjectInsert = sqlQueryObject.newSQLQueryObject();
				


		// Object configurazioneFiltro
		sqlQueryObjectInsert.addInsertTable(this.getConfigurazioneFiltroFieldConverter().toTable(ConfigurazioneFiltro.model()));
		sqlQueryObjectInsert.addInsertField(this.getConfigurazioneFiltroFieldConverter().toColumn(ConfigurazioneFiltro.model().NOME,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getConfigurazioneFiltroFieldConverter().toColumn(ConfigurazioneFiltro.model().DESCRIZIONE,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getConfigurazioneFiltroFieldConverter().toColumn(ConfigurazioneFiltro.model().TIPO_MITTENTE,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getConfigurazioneFiltroFieldConverter().toColumn(ConfigurazioneFiltro.model().NOME_MITTENTE,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getConfigurazioneFiltroFieldConverter().toColumn(ConfigurazioneFiltro.model().IDPORTA_MITTENTE,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getConfigurazioneFiltroFieldConverter().toColumn(ConfigurazioneFiltro.model().TIPO_DESTINATARIO,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getConfigurazioneFiltroFieldConverter().toColumn(ConfigurazioneFiltro.model().NOME_DESTINATARIO,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getConfigurazioneFiltroFieldConverter().toColumn(ConfigurazioneFiltro.model().IDPORTA_DESTINATARIO,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getConfigurazioneFiltroFieldConverter().toColumn(ConfigurazioneFiltro.model().TIPO_SERVIZIO,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getConfigurazioneFiltroFieldConverter().toColumn(ConfigurazioneFiltro.model().NOME_SERVIZIO,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getConfigurazioneFiltroFieldConverter().toColumn(ConfigurazioneFiltro.model().VERSIONE_SERVIZIO,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getConfigurazioneFiltroFieldConverter().toColumn(ConfigurazioneFiltro.model().AZIONE,false),"?");

		// Insert configurazioneFiltro
		org.openspcoop2.utils.jdbc.IKeyGeneratorObject keyGenerator = this.getConfigurazioneFiltroFetch().getKeyGeneratorObject(ConfigurazioneFiltro.model());
		long id = jdbcUtilities.insertAndReturnGeneratedKey(sqlQueryObjectInsert, keyGenerator, jdbcProperties.isShowSql(),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(configurazioneFiltro.getNome(),ConfigurazioneFiltro.model().NOME.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(configurazioneFiltro.getDescrizione(),ConfigurazioneFiltro.model().DESCRIZIONE.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(configurazioneFiltro.getTipoMittente(),ConfigurazioneFiltro.model().TIPO_MITTENTE.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(configurazioneFiltro.getNomeMittente(),ConfigurazioneFiltro.model().NOME_MITTENTE.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(configurazioneFiltro.getIdportaMittente(),ConfigurazioneFiltro.model().IDPORTA_MITTENTE.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(configurazioneFiltro.getTipoDestinatario(),ConfigurazioneFiltro.model().TIPO_DESTINATARIO.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(configurazioneFiltro.getNomeDestinatario(),ConfigurazioneFiltro.model().NOME_DESTINATARIO.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(configurazioneFiltro.getIdportaDestinatario(),ConfigurazioneFiltro.model().IDPORTA_DESTINATARIO.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(configurazioneFiltro.getTipoServizio(),ConfigurazioneFiltro.model().TIPO_SERVIZIO.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(configurazioneFiltro.getNomeServizio(),ConfigurazioneFiltro.model().NOME_SERVIZIO.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(configurazioneFiltro.getVersioneServizio(),ConfigurazioneFiltro.model().VERSIONE_SERVIZIO.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(configurazioneFiltro.getAzione(),ConfigurazioneFiltro.model().AZIONE.getFieldType())
		);
		configurazioneFiltro.setId(id);

		
	}

	@Override
	public void update(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdConfigurazioneFiltro oldId, ConfigurazioneFiltro configurazioneFiltro, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		ISQLQueryObject sqlQueryObjectUpdate = sqlQueryObject.newSQLQueryObject();
		Long longIdByLogicId = this.findIdConfigurazioneFiltro(jdbcProperties, log, connection, sqlQueryObjectUpdate.newSQLQueryObject(), oldId, true);
		Long tableId = configurazioneFiltro.getId();
		if(tableId != null && tableId.longValue() > 0) {
			if(tableId.longValue() != longIdByLogicId.longValue()) {
				throw new Exception("Ambiguous parameter: configurazioneFiltro.id ["+tableId+"] does not match logic id ["+longIdByLogicId+"]");
			}
		} else {
			tableId = longIdByLogicId;
			configurazioneFiltro.setId(tableId);
		}
		if(tableId==null || tableId<=0){
			throw new Exception("Retrieve tableId failed");
		}

		this.update(jdbcProperties, log, connection, sqlQueryObject, tableId, configurazioneFiltro, idMappingResolutionBehaviour);
	}
	@Override
	public void update(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, ConfigurazioneFiltro configurazioneFiltro, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException, NotImplementedException, ServiceException, Exception {
	
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);
		
		ISQLQueryObject sqlQueryObjectInsert = sqlQueryObject.newSQLQueryObject();
		ISQLQueryObject sqlQueryObjectDelete = sqlQueryObjectInsert.newSQLQueryObject();
		ISQLQueryObject sqlQueryObjectGet = sqlQueryObjectDelete.newSQLQueryObject();
		ISQLQueryObject sqlQueryObjectUpdate = sqlQueryObjectGet.newSQLQueryObject();
		
		@SuppressWarnings("unused")
		boolean setIdMappingResolutionBehaviour = 
			(idMappingResolutionBehaviour==null) ||
			org.openspcoop2.generic_project.beans.IDMappingBehaviour.ENABLED.equals(idMappingResolutionBehaviour) ||
			org.openspcoop2.generic_project.beans.IDMappingBehaviour.USE_TABLE_ID.equals(idMappingResolutionBehaviour);
			


		// Object configurazioneFiltro
		sqlQueryObjectUpdate.setANDLogicOperator(true);
		sqlQueryObjectUpdate.addUpdateTable(this.getConfigurazioneFiltroFieldConverter().toTable(ConfigurazioneFiltro.model()));
		boolean isUpdate_configurazioneFiltro = true;
		java.util.List<JDBCObject> lstObjects_configurazioneFiltro = new java.util.ArrayList<JDBCObject>();
		sqlQueryObjectUpdate.addUpdateField(this.getConfigurazioneFiltroFieldConverter().toColumn(ConfigurazioneFiltro.model().NOME,false), "?");
		lstObjects_configurazioneFiltro.add(new JDBCObject(configurazioneFiltro.getNome(), ConfigurazioneFiltro.model().NOME.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getConfigurazioneFiltroFieldConverter().toColumn(ConfigurazioneFiltro.model().DESCRIZIONE,false), "?");
		lstObjects_configurazioneFiltro.add(new JDBCObject(configurazioneFiltro.getDescrizione(), ConfigurazioneFiltro.model().DESCRIZIONE.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getConfigurazioneFiltroFieldConverter().toColumn(ConfigurazioneFiltro.model().TIPO_MITTENTE,false), "?");
		lstObjects_configurazioneFiltro.add(new JDBCObject(configurazioneFiltro.getTipoMittente(), ConfigurazioneFiltro.model().TIPO_MITTENTE.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getConfigurazioneFiltroFieldConverter().toColumn(ConfigurazioneFiltro.model().NOME_MITTENTE,false), "?");
		lstObjects_configurazioneFiltro.add(new JDBCObject(configurazioneFiltro.getNomeMittente(), ConfigurazioneFiltro.model().NOME_MITTENTE.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getConfigurazioneFiltroFieldConverter().toColumn(ConfigurazioneFiltro.model().IDPORTA_MITTENTE,false), "?");
		lstObjects_configurazioneFiltro.add(new JDBCObject(configurazioneFiltro.getIdportaMittente(), ConfigurazioneFiltro.model().IDPORTA_MITTENTE.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getConfigurazioneFiltroFieldConverter().toColumn(ConfigurazioneFiltro.model().TIPO_DESTINATARIO,false), "?");
		lstObjects_configurazioneFiltro.add(new JDBCObject(configurazioneFiltro.getTipoDestinatario(), ConfigurazioneFiltro.model().TIPO_DESTINATARIO.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getConfigurazioneFiltroFieldConverter().toColumn(ConfigurazioneFiltro.model().NOME_DESTINATARIO,false), "?");
		lstObjects_configurazioneFiltro.add(new JDBCObject(configurazioneFiltro.getNomeDestinatario(), ConfigurazioneFiltro.model().NOME_DESTINATARIO.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getConfigurazioneFiltroFieldConverter().toColumn(ConfigurazioneFiltro.model().IDPORTA_DESTINATARIO,false), "?");
		lstObjects_configurazioneFiltro.add(new JDBCObject(configurazioneFiltro.getIdportaDestinatario(), ConfigurazioneFiltro.model().IDPORTA_DESTINATARIO.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getConfigurazioneFiltroFieldConverter().toColumn(ConfigurazioneFiltro.model().TIPO_SERVIZIO,false), "?");
		lstObjects_configurazioneFiltro.add(new JDBCObject(configurazioneFiltro.getTipoServizio(), ConfigurazioneFiltro.model().TIPO_SERVIZIO.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getConfigurazioneFiltroFieldConverter().toColumn(ConfigurazioneFiltro.model().NOME_SERVIZIO,false), "?");
		lstObjects_configurazioneFiltro.add(new JDBCObject(configurazioneFiltro.getNomeServizio(), ConfigurazioneFiltro.model().NOME_SERVIZIO.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getConfigurazioneFiltroFieldConverter().toColumn(ConfigurazioneFiltro.model().VERSIONE_SERVIZIO,false), "?");
		lstObjects_configurazioneFiltro.add(new JDBCObject(configurazioneFiltro.getVersioneServizio(), ConfigurazioneFiltro.model().VERSIONE_SERVIZIO.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getConfigurazioneFiltroFieldConverter().toColumn(ConfigurazioneFiltro.model().AZIONE,false), "?");
		lstObjects_configurazioneFiltro.add(new JDBCObject(configurazioneFiltro.getAzione(), ConfigurazioneFiltro.model().AZIONE.getFieldType()));
		sqlQueryObjectUpdate.addWhereCondition("id=?");
		lstObjects_configurazioneFiltro.add(new JDBCObject(tableId, Long.class));

		if(isUpdate_configurazioneFiltro) {
			// Update configurazioneFiltro
			jdbcUtilities.executeUpdate(sqlQueryObjectUpdate.createSQLUpdate(), jdbcProperties.isShowSql(), 
				lstObjects_configurazioneFiltro.toArray(new JDBCObject[]{}));
		}


	}
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdConfigurazioneFiltro id, UpdateField ... updateFields) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		
		JDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getConfigurazioneFiltroFieldConverter().toTable(ConfigurazioneFiltro.model()), 
				this._getMapTableToPKColumn(), 
				this._getRootTablePrimaryKeyValues(jdbcProperties, log, connection, sqlQueryObject, id),
				this.getConfigurazioneFiltroFieldConverter(), this, null, updateFields);
	}
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdConfigurazioneFiltro id, IExpression condition, UpdateField ... updateFields) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		
		JDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getConfigurazioneFiltroFieldConverter().toTable(ConfigurazioneFiltro.model()), 
				this._getMapTableToPKColumn(), 
				this._getRootTablePrimaryKeyValues(jdbcProperties, log, connection, sqlQueryObject, id),
				this.getConfigurazioneFiltroFieldConverter(), this, condition, updateFields);
	}
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdConfigurazioneFiltro id, UpdateModel ... updateModels) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		
		JDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getConfigurazioneFiltroFieldConverter().toTable(ConfigurazioneFiltro.model()), 
				this._getMapTableToPKColumn(), 
				this._getRootTablePrimaryKeyValues(jdbcProperties, log, connection, sqlQueryObject, id),
				this.getConfigurazioneFiltroFieldConverter(), this, updateModels);
	}	
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, UpdateField ... updateFields) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		java.util.List<Object> ids = new java.util.ArrayList<Object>();
		ids.add(tableId);
		JDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getConfigurazioneFiltroFieldConverter().toTable(ConfigurazioneFiltro.model()), 
				this._getMapTableToPKColumn(), 
				ids,
				this.getConfigurazioneFiltroFieldConverter(), this, null, updateFields);
	}
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, IExpression condition, UpdateField ... updateFields) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		java.util.List<Object> ids = new java.util.ArrayList<Object>();
		ids.add(tableId);
		JDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getConfigurazioneFiltroFieldConverter().toTable(ConfigurazioneFiltro.model()), 
				this._getMapTableToPKColumn(), 
				ids,
				this.getConfigurazioneFiltroFieldConverter(), this, condition, updateFields);
	}
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, UpdateModel ... updateModels) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		java.util.List<Object> ids = new java.util.ArrayList<Object>();
		ids.add(tableId);
		JDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getConfigurazioneFiltroFieldConverter().toTable(ConfigurazioneFiltro.model()), 
				this._getMapTableToPKColumn(), 
				ids,
				this.getConfigurazioneFiltroFieldConverter(), this, updateModels);
	}
	
	@Override
	public void updateOrCreate(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdConfigurazioneFiltro oldId, ConfigurazioneFiltro configurazioneFiltro, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotImplementedException,ServiceException,Exception {
	
		if(this.exists(jdbcProperties, log, connection, sqlQueryObject, oldId)) {
			this.update(jdbcProperties, log, connection, sqlQueryObject, oldId, configurazioneFiltro,idMappingResolutionBehaviour);
		} else {
			this.create(jdbcProperties, log, connection, sqlQueryObject, configurazioneFiltro,idMappingResolutionBehaviour);
		}
		
	}
	
	@Override
	public void updateOrCreate(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, ConfigurazioneFiltro configurazioneFiltro, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotImplementedException,ServiceException,Exception {
		if(this.exists(jdbcProperties, log, connection, sqlQueryObject, tableId)) {
			this.update(jdbcProperties, log, connection, sqlQueryObject, tableId, configurazioneFiltro,idMappingResolutionBehaviour);
		} else {
			this.create(jdbcProperties, log, connection, sqlQueryObject, configurazioneFiltro,idMappingResolutionBehaviour);
		}
	}
	
	@Override
	public void delete(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, ConfigurazioneFiltro configurazioneFiltro) throws NotImplementedException,ServiceException,Exception {
		
		
		Long longId = null;
		if( (configurazioneFiltro.getId()!=null) && (configurazioneFiltro.getId()>0) ){
			longId = configurazioneFiltro.getId();
		}
		else{
			IdConfigurazioneFiltro idConfigurazioneFiltro = this.convertToId(jdbcProperties,log,connection,sqlQueryObject,configurazioneFiltro);
			longId = this.findIdConfigurazioneFiltro(jdbcProperties,log,connection,sqlQueryObject,idConfigurazioneFiltro,false);
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
		

		// Object configurazioneFiltro
		sqlQueryObjectDelete.setANDLogicOperator(true);
		sqlQueryObjectDelete.addDeleteTable(this.getConfigurazioneFiltroFieldConverter().toTable(ConfigurazioneFiltro.model()));
		if(id != null)
			sqlQueryObjectDelete.addWhereCondition("id=?");

		// Delete configurazioneFiltro
		jdbcUtilities.execute(sqlQueryObjectDelete.createSQLDelete(), jdbcProperties.isShowSql(), 
			new JDBCObject(id,Long.class));

	}

	@Override
	public void deleteById(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdConfigurazioneFiltro idConfigurazioneFiltro) throws NotImplementedException,ServiceException,Exception {

		Long id = null;
		try{
			id = this.findIdConfigurazioneFiltro(jdbcProperties, log, connection, sqlQueryObject, idConfigurazioneFiltro, true);
		}catch(NotFoundException notFound){
			return;
		}
		this._delete(jdbcProperties, log, connection, sqlQueryObject, id);
		
	}
	
	@Override
	public NonNegativeNumber deleteAll(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject) throws NotImplementedException,ServiceException,Exception {
		
		return this.deleteAll(jdbcProperties, log, connection, sqlQueryObject, new JDBCExpression(this.getConfigurazioneFiltroFieldConverter()));

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
