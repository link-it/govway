/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2026 Link.it srl (https://link.it). 
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
package org.openspcoop2.core.statistiche.dao.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.jdbc.BinaryStreamNotSupportedException;
import org.openspcoop2.utils.jdbc.JDBCAdapterException;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLQueryObjectException;
import org.slf4j.Logger;

import org.openspcoop2.generic_project.dao.jdbc.IJDBCServiceCRUDWithoutId;
import org.openspcoop2.generic_project.beans.NonNegativeNumber;
import org.openspcoop2.generic_project.beans.UpdateField;
import org.openspcoop2.generic_project.beans.UpdateModel;

import org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities;
import org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject;
import org.openspcoop2.generic_project.exception.ExpressionException;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.generic_project.expression.IExpression;
import org.openspcoop2.generic_project.dao.jdbc.JDBCExpression;
import org.openspcoop2.generic_project.dao.jdbc.JDBCPaginatedExpression;

import org.openspcoop2.generic_project.dao.jdbc.JDBCServiceManagerProperties;

import org.openspcoop2.core.statistiche.StatistichePdndTracing;

/**     
 * JDBCStatistichePdndTracingServiceImpl
 *
 * @author Poli Andrea (poli@link.it)
 * @author Tommaso Burlon (tommaso.burlon@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JDBCStatistichePdndTracingServiceImpl extends JDBCStatistichePdndTracingServiceSearchImpl
	implements IJDBCServiceCRUDWithoutId<StatistichePdndTracing, JDBCServiceManager> {

	private static void sanitizeTextColumns(TipiDatabase dbType, StatistichePdndTracing statistichePdndTracing) {
		statistichePdndTracing.setErrorDetails(org.openspcoop2.utils.jdbc.NullByteTextColumnSanitizer.sanitize(dbType, statistichePdndTracing.getErrorDetails()));
	}

	@Override
	public void create(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, StatistichePdndTracing statistichePdndTracing, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws Exception {

		if(org.openspcoop2.utils.jdbc.NullByteTextColumnSanitizer.needsSanitization(jdbcProperties.getDatabase())){
			sanitizeTextColumns(jdbcProperties.getDatabase(), statistichePdndTracing);
		}

		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities =
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);
		
		ISQLQueryObject sqlQueryObjectInsert = sqlQueryObject.newSQLQueryObject();
				


		// Object statistichePdndTracing
		sqlQueryObjectInsert.addInsertTable(this.getStatistichePdndTracingFieldConverter().toTable(StatistichePdndTracing.model()));
		sqlQueryObjectInsert.addInsertField(this.getStatistichePdndTracingFieldConverter().toColumn(StatistichePdndTracing.model().DATA_TRACCIAMENTO,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getStatistichePdndTracingFieldConverter().toColumn(StatistichePdndTracing.model().DATA_REGISTRAZIONE,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getStatistichePdndTracingFieldConverter().toColumn(StatistichePdndTracing.model().DATA_PUBBLICAZIONE,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getStatistichePdndTracingFieldConverter().toColumn(StatistichePdndTracing.model().PDD_CODICE,false),"?");
		/** Fix out of memory: #1720 sqlQueryObjectInsert.addInsertField(this.getStatistichePdndTracingFieldConverter().toColumn(StatistichePdndTracing.model().CSV,false),"?");*/
		sqlQueryObjectInsert.addInsertField(this.getStatistichePdndTracingFieldConverter().toColumn(StatistichePdndTracing.model().METHOD,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getStatistichePdndTracingFieldConverter().toColumn(StatistichePdndTracing.model().STATO_PDND,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getStatistichePdndTracingFieldConverter().toColumn(StatistichePdndTracing.model().TENTATIVI_PUBBLICAZIONE,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getStatistichePdndTracingFieldConverter().toColumn(StatistichePdndTracing.model().FORCE_PUBLISH,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getStatistichePdndTracingFieldConverter().toColumn(StatistichePdndTracing.model().STATO,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getStatistichePdndTracingFieldConverter().toColumn(StatistichePdndTracing.model().TRACING_ID,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getStatistichePdndTracingFieldConverter().toColumn(StatistichePdndTracing.model().ERROR_DETAILS,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getStatistichePdndTracingFieldConverter().toColumn(StatistichePdndTracing.model().HISTORY,false),"?");

		// Insert statistichePdndTracing
		org.openspcoop2.utils.jdbc.IKeyGeneratorObject keyGenerator = this.getStatistichePdndTracingFetch().getKeyGeneratorObject(StatistichePdndTracing.model());
		long id = jdbcUtilities.insertAndReturnGeneratedKey(sqlQueryObjectInsert, keyGenerator, jdbcProperties.isShowSql(),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statistichePdndTracing.getDataTracciamento(),StatistichePdndTracing.model().DATA_TRACCIAMENTO.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statistichePdndTracing.getDataRegistrazione(),StatistichePdndTracing.model().DATA_REGISTRAZIONE.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statistichePdndTracing.getDataPubblicazione(),StatistichePdndTracing.model().DATA_PUBBLICAZIONE.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statistichePdndTracing.getPddCodice(),StatistichePdndTracing.model().PDD_CODICE.getFieldType()),
			/** Fix out of memory: #1720 new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statistichePdndTracing.getCsv(),StatistichePdndTracing.model().CSV.getFieldType()),*/
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statistichePdndTracing.getMethod(),StatistichePdndTracing.model().METHOD.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statistichePdndTracing.getStatoPdnd(),StatistichePdndTracing.model().STATO_PDND.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statistichePdndTracing.getTentativiPubblicazione(),StatistichePdndTracing.model().TENTATIVI_PUBBLICAZIONE.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statistichePdndTracing.getForcePublish(),StatistichePdndTracing.model().FORCE_PUBLISH.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statistichePdndTracing.getStato(),StatistichePdndTracing.model().STATO.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statistichePdndTracing.getTracingId(),StatistichePdndTracing.model().TRACING_ID.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statistichePdndTracing.getErrorDetails(),StatistichePdndTracing.model().ERROR_DETAILS.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statistichePdndTracing.getHistory(),StatistichePdndTracing.model().HISTORY.getFieldType())
		);
		statistichePdndTracing.setId(id);

		
	}

	@Override
	public void update(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, StatistichePdndTracing statistichePdndTracing, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws Exception {
		
		Long tableId = statistichePdndTracing.getId();
		if(tableId==null || tableId<=0){
			throw new Exception("Retrieve tableId failed");
		}

		this.update(jdbcProperties, log, connection, sqlQueryObject, tableId, statistichePdndTracing, idMappingResolutionBehaviour);
	}
	@Override
	public void update(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, StatistichePdndTracing statistichePdndTracing, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws Exception {

		if(org.openspcoop2.utils.jdbc.NullByteTextColumnSanitizer.needsSanitization(jdbcProperties.getDatabase())){
			sanitizeTextColumns(jdbcProperties.getDatabase(), statistichePdndTracing);
		}

		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities =
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);
		
		ISQLQueryObject sqlQueryObjectInsert = sqlQueryObject.newSQLQueryObject();
		ISQLQueryObject sqlQueryObjectDelete = sqlQueryObjectInsert.newSQLQueryObject();
		ISQLQueryObject sqlQueryObjectGet = sqlQueryObjectDelete.newSQLQueryObject();
		ISQLQueryObject sqlQueryObjectUpdate = sqlQueryObjectGet.newSQLQueryObject();
		


		// Object statistichePdndTracing
		sqlQueryObjectUpdate.setANDLogicOperator(true);
		sqlQueryObjectUpdate.addUpdateTable(this.getStatistichePdndTracingFieldConverter().toTable(StatistichePdndTracing.model()));
		boolean isUpdate = true;
		java.util.List<JDBCObject> lstObjects = new java.util.ArrayList<>();
		sqlQueryObjectUpdate.addUpdateField(this.getStatistichePdndTracingFieldConverter().toColumn(StatistichePdndTracing.model().DATA_TRACCIAMENTO,false), "?");
		lstObjects.add(new JDBCObject(statistichePdndTracing.getDataTracciamento(), StatistichePdndTracing.model().DATA_TRACCIAMENTO.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getStatistichePdndTracingFieldConverter().toColumn(StatistichePdndTracing.model().DATA_REGISTRAZIONE,false), "?");
		lstObjects.add(new JDBCObject(statistichePdndTracing.getDataRegistrazione(), StatistichePdndTracing.model().DATA_REGISTRAZIONE.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getStatistichePdndTracingFieldConverter().toColumn(StatistichePdndTracing.model().DATA_PUBBLICAZIONE,false), "?");
		lstObjects.add(new JDBCObject(statistichePdndTracing.getDataPubblicazione(), StatistichePdndTracing.model().DATA_PUBBLICAZIONE.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getStatistichePdndTracingFieldConverter().toColumn(StatistichePdndTracing.model().PDD_CODICE,false), "?");
		lstObjects.add(new JDBCObject(statistichePdndTracing.getPddCodice(), StatistichePdndTracing.model().PDD_CODICE.getFieldType()));
		/** Fix out of memory: #1720
		sqlQueryObjectUpdate.addUpdateField(this.getStatistichePdndTracingFieldConverter().toColumn(StatistichePdndTracing.model().CSV,false), "?")
		lstObjects.add(new JDBCObject(statistichePdndTracing.getCsv(), StatistichePdndTracing.model().CSV.getFieldType()));
		 */
		sqlQueryObjectUpdate.addUpdateField(this.getStatistichePdndTracingFieldConverter().toColumn(StatistichePdndTracing.model().METHOD,false), "?");
		lstObjects.add(new JDBCObject(statistichePdndTracing.getMethod(), StatistichePdndTracing.model().METHOD.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getStatistichePdndTracingFieldConverter().toColumn(StatistichePdndTracing.model().STATO_PDND,false), "?");
		lstObjects.add(new JDBCObject(statistichePdndTracing.getStatoPdnd(), StatistichePdndTracing.model().STATO_PDND.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getStatistichePdndTracingFieldConverter().toColumn(StatistichePdndTracing.model().TENTATIVI_PUBBLICAZIONE,false), "?");
		lstObjects.add(new JDBCObject(statistichePdndTracing.getTentativiPubblicazione(), StatistichePdndTracing.model().TENTATIVI_PUBBLICAZIONE.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getStatistichePdndTracingFieldConverter().toColumn(StatistichePdndTracing.model().FORCE_PUBLISH,false), "?");
		lstObjects.add(new JDBCObject(statistichePdndTracing.getForcePublish(), StatistichePdndTracing.model().FORCE_PUBLISH.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getStatistichePdndTracingFieldConverter().toColumn(StatistichePdndTracing.model().STATO,false), "?");
		lstObjects.add(new JDBCObject(statistichePdndTracing.getStato(), StatistichePdndTracing.model().STATO.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getStatistichePdndTracingFieldConverter().toColumn(StatistichePdndTracing.model().TRACING_ID,false), "?");
		lstObjects.add(new JDBCObject(statistichePdndTracing.getTracingId(), StatistichePdndTracing.model().TRACING_ID.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getStatistichePdndTracingFieldConverter().toColumn(StatistichePdndTracing.model().ERROR_DETAILS,false), "?");
		lstObjects.add(new JDBCObject(statistichePdndTracing.getErrorDetails(), StatistichePdndTracing.model().ERROR_DETAILS.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getStatistichePdndTracingFieldConverter().toColumn(StatistichePdndTracing.model().HISTORY,false), "?");
		lstObjects.add(new JDBCObject(statistichePdndTracing.getHistory(), StatistichePdndTracing.model().HISTORY.getFieldType()));
		sqlQueryObjectUpdate.addWhereCondition("id=?");
		lstObjects.add(new JDBCObject(tableId, Long.class));

		if(isUpdate) {
			// Update statistichePdndTracing
			jdbcUtilities.executeUpdate(sqlQueryObjectUpdate.createSQLUpdate(), jdbcProperties.isShowSql(), 
				lstObjects.toArray(new JDBCObject[]{}));
		}


	}
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, StatistichePdndTracing statistichePdndTracing, UpdateField ... updateFields) throws Exception {
		
		GenericJDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getStatistichePdndTracingFieldConverter().toTable(StatistichePdndTracing.model()), 
				this._getMapTableToPKColumn(), 
				this._getRootTablePrimaryKeyValues(jdbcProperties, log, connection, sqlQueryObject, statistichePdndTracing),
				this.getStatistichePdndTracingFieldConverter(), this, null, updateFields);
	}
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, StatistichePdndTracing statistichePdndTracing, IExpression condition, UpdateField ... updateFields) throws Exception {
		
		GenericJDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getStatistichePdndTracingFieldConverter().toTable(StatistichePdndTracing.model()), 
				this._getMapTableToPKColumn(), 
				this._getRootTablePrimaryKeyValues(jdbcProperties, log, connection, sqlQueryObject, statistichePdndTracing),
				this.getStatistichePdndTracingFieldConverter(), this, condition, updateFields);
	}
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, StatistichePdndTracing statistichePdndTracing, UpdateModel ... updateModels) throws Exception {
		
		GenericJDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getStatistichePdndTracingFieldConverter().toTable(StatistichePdndTracing.model()), 
				this._getMapTableToPKColumn(), 
				this._getRootTablePrimaryKeyValues(jdbcProperties, log, connection, sqlQueryObject, statistichePdndTracing),
				this.getStatistichePdndTracingFieldConverter(), this, updateModels);
	}	
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, UpdateField ... updateFields) throws Exception {
		java.util.List<Object> ids = new java.util.ArrayList<Object>();
		ids.add(tableId);
		GenericJDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getStatistichePdndTracingFieldConverter().toTable(StatistichePdndTracing.model()), 
				this._getMapTableToPKColumn(), 
				ids,
				this.getStatistichePdndTracingFieldConverter(), this, null, updateFields);
	}
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, IExpression condition, UpdateField ... updateFields) throws Exception {
		java.util.List<Object> ids = new java.util.ArrayList<>();
		ids.add(tableId);
		GenericJDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getStatistichePdndTracingFieldConverter().toTable(StatistichePdndTracing.model()), 
				this._getMapTableToPKColumn(), 
				ids,
				this.getStatistichePdndTracingFieldConverter(), this, condition, updateFields);
	}
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, UpdateModel ... updateModels) throws Exception {
		java.util.List<Object> ids = new java.util.ArrayList<>();
		ids.add(tableId);
		GenericJDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getStatistichePdndTracingFieldConverter().toTable(StatistichePdndTracing.model()), 
				this._getMapTableToPKColumn(), 
				ids,
				this.getStatistichePdndTracingFieldConverter(), this, updateModels);
	}
	
	@Override
	public void updateOrCreate(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, StatistichePdndTracing statistichePdndTracing, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws Exception {
	
		Long id = statistichePdndTracing.getId();
		if(id != null && this.exists(jdbcProperties, log, connection, sqlQueryObject, id)) {
			this.update(jdbcProperties, log, connection, sqlQueryObject, statistichePdndTracing,idMappingResolutionBehaviour);		
		} else {
			this.create(jdbcProperties, log, connection, sqlQueryObject, statistichePdndTracing,idMappingResolutionBehaviour);
		}
		
	}
	
	@Override
	public void updateOrCreate(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, StatistichePdndTracing statistichePdndTracing, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws Exception {
		if(this.exists(jdbcProperties, log, connection, sqlQueryObject, tableId)) {
			this.update(jdbcProperties, log, connection, sqlQueryObject, tableId, statistichePdndTracing,idMappingResolutionBehaviour);
		} else {
			this.create(jdbcProperties, log, connection, sqlQueryObject, statistichePdndTracing,idMappingResolutionBehaviour);
		}
	}
	
	@Override
	public void delete(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, StatistichePdndTracing statistichePdndTracing) throws Exception {
		
		
		Long longId = null;
		if(statistichePdndTracing.getId()==null){
			throw new ServiceException("Parameter "+statistichePdndTracing.getClass().getName()+".id is null");
		}
		if(statistichePdndTracing.getId()<=0){
			throw new ServiceException("Parameter "+statistichePdndTracing.getClass().getName()+".id is less equals 0");
		}
		longId = statistichePdndTracing.getId();
		
		this._delete(jdbcProperties, log, connection, sqlQueryObject, longId);
		
	}

	private void _delete(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, Long id) throws Exception {
	
		if(id==null){
			throw new ServiceException("Id is null");
		}
		if(id.longValue()<=0){
			throw new ServiceException("Id is less equals 0");
		}
		
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);
		
		ISQLQueryObject sqlQueryObjectDelete = sqlQueryObject.newSQLQueryObject();
		

		// Object statistichePdndTracing
		sqlQueryObjectDelete.setANDLogicOperator(true);
		sqlQueryObjectDelete.addDeleteTable(this.getStatistichePdndTracingFieldConverter().toTable(StatistichePdndTracing.model()));
		sqlQueryObjectDelete.addWhereCondition("id=?");

		// Delete statistichePdndTracing
		jdbcUtilities.execute(sqlQueryObjectDelete.createSQLDelete(), jdbcProperties.isShowSql(), 
			new JDBCObject(id,Long.class));

	}

	
	@Override
	public NonNegativeNumber deleteAll(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject) throws Exception {
		
		return this.deleteAll(jdbcProperties, log, connection, sqlQueryObject, new JDBCExpression(this.getStatistichePdndTracingFieldConverter()));

	}

	@Override
	public NonNegativeNumber deleteAll(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCExpression expression) throws Exception {

		java.util.List<Long> lst = this.findAllTableIds(jdbcProperties, log, connection, sqlQueryObject, new JDBCPaginatedExpression(expression));
		
		for(Long id : lst) {
			this._delete(jdbcProperties, log, connection, sqlQueryObject, id);
		}
		
		return new NonNegativeNumber(lst.size());
	
	}



	// -- DB
	
	@Override
	public void deleteById(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId) throws Exception {
		this._delete(jdbcProperties, log, connection, sqlQueryObject, Long.valueOf(tableId));
	}
	
	@Override
	public int nativeUpdate(JDBCServiceManagerProperties jdbcProperties, Logger log,Connection connection,ISQLQueryObject sqlObject, String sql,Object ... param) throws Exception {
	
		return org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.nativeUpdate(jdbcProperties, log, connection, sqlObject,
																							sql,param);
	
	}
	
	public void setCsvStream(JDBCServiceManagerProperties jdbcProperties, Logger log,
			Connection connection, ISQLQueryObject sqlQueryObject,
			long tableId, java.io.InputStream csvStream) throws JDBCAdapterException, ExpressionException, SQLQueryObjectException, SQLException, UtilsException, BinaryStreamNotSupportedException, NotFoundException {

		if(jdbcProperties!=null) {
			// nop
		}
		
		org.openspcoop2.utils.jdbc.IJDBCAdapter jdbcAdapter =
			org.openspcoop2.utils.jdbc.JDBCAdapterFactory.createJDBCAdapter(
				this.getStatistichePdndTracingFieldConverter().getDatabaseType());

		ISQLQueryObject sqlQueryObjectUpdate = sqlQueryObject.newSQLQueryObject();
		sqlQueryObjectUpdate.setANDLogicOperator(true);
		sqlQueryObjectUpdate.addUpdateTable(this.getStatistichePdndTracingFieldConverter()
			.toTable(StatistichePdndTracing.model()));
		sqlQueryObjectUpdate.addUpdateField(this.getStatistichePdndTracingFieldConverter()
			.toColumn(StatistichePdndTracing.model().CSV, false), "?");
		sqlQueryObjectUpdate.addWhereCondition("id=?");

		java.sql.PreparedStatement pstmt = null;
		try {
			String sql = sqlQueryObjectUpdate.createSQLUpdate();
			log.debug("set csv stream: {}, id: {} ", sql, tableId);

			pstmt = connection.prepareStatement(sql);
			jdbcAdapter.setBinaryData(pstmt, 1, csvStream, true);
			pstmt.setLong(2, tableId);

			
			int rows = pstmt.executeUpdate();
			if(rows == 0) {
				throw new org.openspcoop2.generic_project.exception.NotFoundException(
					"Entry with id["+tableId+"] not found");
			}
		} finally {
			try { if(pstmt!=null) pstmt.close(); } catch(Exception eClose) { /* ignore */ }
		}
	}

	public void forcePublish(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IExpression expr) throws Exception {
		
		UpdateField field = new UpdateField(StatistichePdndTracing.model().FORCE_PUBLISH, true);

		GenericJDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getStatistichePdndTracingFieldConverter().toTable(StatistichePdndTracing.model()), 
				this._getMapTableToPKColumn(), 
				null,
				this.getStatistichePdndTracingFieldConverter(), this, expr, field);
			
	}
}
