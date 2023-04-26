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

import java.sql.Connection;

import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.sql.ISQLQueryObject;

import org.slf4j.Logger;

import org.openspcoop2.generic_project.dao.jdbc.IJDBCServiceCRUDWithId;
import org.openspcoop2.core.transazioni.IdCredenzialeMittente;
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

import org.openspcoop2.core.transazioni.CredenzialeMittente;

/**     
 * JDBCCredenzialeMittenteServiceImpl
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JDBCCredenzialeMittenteServiceImpl extends JDBCCredenzialeMittenteServiceSearchImpl
	implements IJDBCServiceCRUDWithId<CredenzialeMittente, IdCredenzialeMittente, JDBCServiceManager> {

	@Override
	public void create(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, CredenzialeMittente credenzialeMittente, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotImplementedException,ServiceException,Exception {

		if(TipiDatabase.POSTGRESQL.equals(jdbcProperties.getDatabase()) && org.openspcoop2.utils.jdbc.PostgreSQLUtilities.containsNullByteSequence(credenzialeMittente.getCredenziale())){
			credenzialeMittente.setCredenziale(org.openspcoop2.utils.jdbc.PostgreSQLUtilities.normalizeString(credenzialeMittente.getCredenziale()));
		}
		
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);
		
		ISQLQueryObject sqlQueryObjectInsert = sqlQueryObject.newSQLQueryObject();
				


		// Object credenzialeMittente
		sqlQueryObjectInsert.addInsertTable(this.getCredenzialeMittenteFieldConverter().toTable(CredenzialeMittente.model()));
		sqlQueryObjectInsert.addInsertField(this.getCredenzialeMittenteFieldConverter().toColumn(CredenzialeMittente.model().TIPO,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getCredenzialeMittenteFieldConverter().toColumn(CredenzialeMittente.model().CREDENZIALE,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getCredenzialeMittenteFieldConverter().toColumn(CredenzialeMittente.model().ORA_REGISTRAZIONE,false),"?");

		// Insert credenzialeMittente
		org.openspcoop2.utils.jdbc.IKeyGeneratorObject keyGenerator = this.getCredenzialeMittenteFetch().getKeyGeneratorObject(CredenzialeMittente.model());
		long id = jdbcUtilities.insertAndReturnGeneratedKey(sqlQueryObjectInsert, keyGenerator, jdbcProperties.isShowSql(),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(credenzialeMittente.getTipo(),CredenzialeMittente.model().TIPO.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(credenzialeMittente.getCredenziale(),CredenzialeMittente.model().CREDENZIALE.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(credenzialeMittente.getOraRegistrazione(),CredenzialeMittente.model().ORA_REGISTRAZIONE.getFieldType())
		);
		credenzialeMittente.setId(id);

		
	}

	@Override
	public void update(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdCredenzialeMittente oldId, CredenzialeMittente credenzialeMittente, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		ISQLQueryObject sqlQueryObjectUpdate = sqlQueryObject.newSQLQueryObject();
		Long longIdByLogicId = this.findIdCredenzialeMittente(jdbcProperties, log, connection, sqlQueryObjectUpdate.newSQLQueryObject(), oldId, true);
		Long tableId = credenzialeMittente.getId();
		if(tableId != null && tableId.longValue() > 0) {
			if(tableId.longValue() != longIdByLogicId.longValue()) {
				throw new Exception("Ambiguous parameter: credenzialeMittente.id ["+tableId+"] does not match logic id ["+longIdByLogicId+"]");
			}
		} else {
			tableId = longIdByLogicId;
			credenzialeMittente.setId(tableId);
		}
		if(tableId==null || tableId<=0){
			throw new Exception("Retrieve tableId failed");
		}

		this.update(jdbcProperties, log, connection, sqlQueryObject, tableId, credenzialeMittente, idMappingResolutionBehaviour);
	}
	@Override
	public void update(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, CredenzialeMittente credenzialeMittente, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException, NotImplementedException, ServiceException, Exception {
	
		if(TipiDatabase.POSTGRESQL.equals(jdbcProperties.getDatabase()) && org.openspcoop2.utils.jdbc.PostgreSQLUtilities.containsNullByteSequence(credenzialeMittente.getCredenziale())){
			credenzialeMittente.setCredenziale(org.openspcoop2.utils.jdbc.PostgreSQLUtilities.normalizeString(credenzialeMittente.getCredenziale()));
		}
		
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);
		
		ISQLQueryObject sqlQueryObjectInsert = sqlQueryObject.newSQLQueryObject();
		ISQLQueryObject sqlQueryObjectDelete = sqlQueryObjectInsert.newSQLQueryObject();
		ISQLQueryObject sqlQueryObjectGet = sqlQueryObjectDelete.newSQLQueryObject();
		ISQLQueryObject sqlQueryObjectUpdate = sqlQueryObjectGet.newSQLQueryObject();
		


		// Object credenzialeMittente
		sqlQueryObjectUpdate.setANDLogicOperator(true);
		sqlQueryObjectUpdate.addUpdateTable(this.getCredenzialeMittenteFieldConverter().toTable(CredenzialeMittente.model()));
		boolean isUpdate_credenzialeMittente = true;
		java.util.List<JDBCObject> lstObjects_credenzialeMittente = new java.util.ArrayList<>();
		sqlQueryObjectUpdate.addUpdateField(this.getCredenzialeMittenteFieldConverter().toColumn(CredenzialeMittente.model().TIPO,false), "?");
		lstObjects_credenzialeMittente.add(new JDBCObject(credenzialeMittente.getTipo(), CredenzialeMittente.model().TIPO.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getCredenzialeMittenteFieldConverter().toColumn(CredenzialeMittente.model().CREDENZIALE,false), "?");
		lstObjects_credenzialeMittente.add(new JDBCObject(credenzialeMittente.getCredenziale(), CredenzialeMittente.model().CREDENZIALE.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getCredenzialeMittenteFieldConverter().toColumn(CredenzialeMittente.model().ORA_REGISTRAZIONE,false), "?");
		lstObjects_credenzialeMittente.add(new JDBCObject(credenzialeMittente.getOraRegistrazione(), CredenzialeMittente.model().ORA_REGISTRAZIONE.getFieldType()));
		sqlQueryObjectUpdate.addWhereCondition("id=?");
		lstObjects_credenzialeMittente.add(new JDBCObject(tableId, Long.class));

		if(isUpdate_credenzialeMittente) {
			// Update credenzialeMittente
			jdbcUtilities.executeUpdate(sqlQueryObjectUpdate.createSQLUpdate(), jdbcProperties.isShowSql(), 
				lstObjects_credenzialeMittente.toArray(new JDBCObject[]{}));
		}


	}
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdCredenzialeMittente id, UpdateField ... updateFields) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		
		GenericJDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getCredenzialeMittenteFieldConverter().toTable(CredenzialeMittente.model()), 
				this._getMapTableToPKColumn(), 
				this._getRootTablePrimaryKeyValues(jdbcProperties, log, connection, sqlQueryObject, id),
				this.getCredenzialeMittenteFieldConverter(), this, null, updateFields);
	}
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdCredenzialeMittente id, IExpression condition, UpdateField ... updateFields) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		
		GenericJDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getCredenzialeMittenteFieldConverter().toTable(CredenzialeMittente.model()), 
				this._getMapTableToPKColumn(), 
				this._getRootTablePrimaryKeyValues(jdbcProperties, log, connection, sqlQueryObject, id),
				this.getCredenzialeMittenteFieldConverter(), this, condition, updateFields);
	}
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdCredenzialeMittente id, UpdateModel ... updateModels) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		
		GenericJDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getCredenzialeMittenteFieldConverter().toTable(CredenzialeMittente.model()), 
				this._getMapTableToPKColumn(), 
				this._getRootTablePrimaryKeyValues(jdbcProperties, log, connection, sqlQueryObject, id),
				this.getCredenzialeMittenteFieldConverter(), this, updateModels);
	}	
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, UpdateField ... updateFields) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		java.util.List<Object> ids = new java.util.ArrayList<>();
		ids.add(tableId);
		GenericJDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getCredenzialeMittenteFieldConverter().toTable(CredenzialeMittente.model()), 
				this._getMapTableToPKColumn(), 
				ids,
				this.getCredenzialeMittenteFieldConverter(), this, null, updateFields);
	}
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, IExpression condition, UpdateField ... updateFields) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		java.util.List<Object> ids = new java.util.ArrayList<>();
		ids.add(tableId);
		GenericJDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getCredenzialeMittenteFieldConverter().toTable(CredenzialeMittente.model()), 
				this._getMapTableToPKColumn(), 
				ids,
				this.getCredenzialeMittenteFieldConverter(), this, condition, updateFields);
	}
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, UpdateModel ... updateModels) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		java.util.List<Object> ids = new java.util.ArrayList<>();
		ids.add(tableId);
		GenericJDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getCredenzialeMittenteFieldConverter().toTable(CredenzialeMittente.model()), 
				this._getMapTableToPKColumn(), 
				ids,
				this.getCredenzialeMittenteFieldConverter(), this, updateModels);
	}
	
	@Override
	public void updateOrCreate(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdCredenzialeMittente oldId, CredenzialeMittente credenzialeMittente, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotImplementedException,ServiceException,Exception {
	
		if(this.exists(jdbcProperties, log, connection, sqlQueryObject, oldId)) {
			this.update(jdbcProperties, log, connection, sqlQueryObject, oldId, credenzialeMittente,idMappingResolutionBehaviour);
		} else {
			this.create(jdbcProperties, log, connection, sqlQueryObject, credenzialeMittente,idMappingResolutionBehaviour);
		}
		
	}
	
	@Override
	public void updateOrCreate(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, CredenzialeMittente credenzialeMittente, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotImplementedException,ServiceException,Exception {
		if(this.exists(jdbcProperties, log, connection, sqlQueryObject, tableId)) {
			this.update(jdbcProperties, log, connection, sqlQueryObject, tableId, credenzialeMittente,idMappingResolutionBehaviour);
		} else {
			this.create(jdbcProperties, log, connection, sqlQueryObject, credenzialeMittente,idMappingResolutionBehaviour);
		}
	}
	
	@Override
	public void delete(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, CredenzialeMittente credenzialeMittente) throws NotImplementedException,ServiceException,Exception {
		
		
		Long longId = null;
		if( (credenzialeMittente.getId()!=null) && (credenzialeMittente.getId()>0) ){
			longId = credenzialeMittente.getId();
		}
		else{
			IdCredenzialeMittente idCredenzialeMittente = this.convertToId(jdbcProperties,log,connection,sqlQueryObject,credenzialeMittente);
			longId = this.findIdCredenzialeMittente(jdbcProperties,log,connection,sqlQueryObject,idCredenzialeMittente,false);
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
		

		// Object credenzialeMittente
		sqlQueryObjectDelete.setANDLogicOperator(true);
		sqlQueryObjectDelete.addDeleteTable(this.getCredenzialeMittenteFieldConverter().toTable(CredenzialeMittente.model()));
		if(id != null)
			sqlQueryObjectDelete.addWhereCondition("id=?");

		// Delete credenzialeMittente
		jdbcUtilities.execute(sqlQueryObjectDelete.createSQLDelete(), jdbcProperties.isShowSql(), 
			new JDBCObject(id,Long.class));

	}

	@Override
	public void deleteById(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdCredenzialeMittente idCredenzialeMittente) throws NotImplementedException,ServiceException,Exception {

		Long id = null;
		try{
			id = this.findIdCredenzialeMittente(jdbcProperties, log, connection, sqlQueryObject, idCredenzialeMittente, true);
		}catch(NotFoundException notFound){
			return;
		}
		this._delete(jdbcProperties, log, connection, sqlQueryObject, id);
		
	}
	
	@Override
	public NonNegativeNumber deleteAll(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject) throws NotImplementedException,ServiceException,Exception {
		
		return this.deleteAll(jdbcProperties, log, connection, sqlQueryObject, new JDBCExpression(this.getCredenzialeMittenteFieldConverter()));

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
