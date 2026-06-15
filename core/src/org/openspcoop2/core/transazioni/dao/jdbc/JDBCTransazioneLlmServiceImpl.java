package org.openspcoop2.core.transazioni.dao.jdbc;

import java.sql.Connection;

import org.openspcoop2.core.transazioni.IdTransazioneLlm;
import org.openspcoop2.core.transazioni.TransazioneLlm;
import org.openspcoop2.generic_project.beans.NonNegativeNumber;
import org.openspcoop2.generic_project.beans.UpdateField;
import org.openspcoop2.generic_project.beans.UpdateModel;
import org.openspcoop2.generic_project.dao.jdbc.IJDBCServiceCRUDWithId;
import org.openspcoop2.generic_project.dao.jdbc.JDBCExpression;
import org.openspcoop2.generic_project.dao.jdbc.JDBCPaginatedExpression;
import org.openspcoop2.generic_project.dao.jdbc.JDBCServiceManagerProperties;
import org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities;
import org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject;
import org.openspcoop2.generic_project.exception.ExpressionException;
import org.openspcoop2.generic_project.exception.ExpressionNotImplementedException;
import org.openspcoop2.generic_project.exception.MultipleResultException;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.generic_project.exception.NotImplementedException;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.generic_project.expression.IExpression;
import org.openspcoop2.utils.jdbc.JDBCAdapterException;
import org.openspcoop2.utils.jdbc.KeyGeneratorException;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLQueryObjectException;
import org.slf4j.Logger;

/**     
 * JDBCTransazioneLlmServiceImpl
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JDBCTransazioneLlmServiceImpl extends JDBCTransazioneLlmServiceSearchImpl
	implements IJDBCServiceCRUDWithId<TransazioneLlm, IdTransazioneLlm, JDBCServiceManager> {

	@Override
	public void create(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, TransazioneLlm transazioneLlm, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws SQLQueryObjectException, JDBCAdapterException, ExpressionException, KeyGeneratorException, ServiceException {

		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);
		
		ISQLQueryObject sqlQueryObjectInsert = sqlQueryObject.newSQLQueryObject();
				


		// Object transazioneLlm
		sqlQueryObjectInsert.addInsertTable(this.getTransazioneLlmFieldConverter().toTable(TransazioneLlm.model()));
		sqlQueryObjectInsert.addInsertField(this.getTransazioneLlmFieldConverter().toColumn(TransazioneLlm.model().ID_TRANSAZIONE,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneLlmFieldConverter().toColumn(TransazioneLlm.model().DATA_INGRESSO_RICHIESTA,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneLlmFieldConverter().toColumn(TransazioneLlm.model().LLM_PROVIDER,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneLlmFieldConverter().toColumn(TransazioneLlm.model().LLM_MODEL,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneLlmFieldConverter().toColumn(TransazioneLlm.model().LLM_PROVIDER_BINDING,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneLlmFieldConverter().toColumn(TransazioneLlm.model().TOKEN_INPUT,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneLlmFieldConverter().toColumn(TransazioneLlm.model().TOKEN_OUTPUT,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneLlmFieldConverter().toColumn(TransazioneLlm.model().COST_ESTIMATED,false),"?");

		// Insert transazioneLlm
		org.openspcoop2.utils.jdbc.IKeyGeneratorObject keyGenerator = this.getTransazioneLlmFetch().getKeyGeneratorObject(TransazioneLlm.model());
		long id = jdbcUtilities.insertAndReturnGeneratedKey(sqlQueryObjectInsert, keyGenerator, jdbcProperties.isShowSql(),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazioneLlm.getIdTransazione(),TransazioneLlm.model().ID_TRANSAZIONE.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazioneLlm.getDataIngressoRichiesta(),TransazioneLlm.model().DATA_INGRESSO_RICHIESTA.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazioneLlm.getLlmProvider(),TransazioneLlm.model().LLM_PROVIDER.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazioneLlm.getLlmModel(),TransazioneLlm.model().LLM_MODEL.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazioneLlm.getLlmProviderBinding(),TransazioneLlm.model().LLM_PROVIDER_BINDING.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazioneLlm.getTokenInput(),TransazioneLlm.model().TOKEN_INPUT.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazioneLlm.getTokenOutput(),TransazioneLlm.model().TOKEN_OUTPUT.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazioneLlm.getCostEstimated(),TransazioneLlm.model().COST_ESTIMATED.getFieldType())
		);
		transazioneLlm.setId(id);

		
	}

	@Override
	public void update(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdTransazioneLlm oldId, TransazioneLlm transazioneLlm, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException, NotImplementedException, ServiceException, SQLQueryObjectException, ExpressionException, MultipleResultException, JDBCAdapterException {
		ISQLQueryObject sqlQueryObjectUpdate = sqlQueryObject.newSQLQueryObject();
		Long longIdByLogicId = this.findIdTransazioneLlm(jdbcProperties, log, connection, sqlQueryObjectUpdate.newSQLQueryObject(), oldId, true);
		Long tableId = transazioneLlm.getId();
		if(tableId != null && tableId.longValue() > 0) {
			if(tableId.longValue() != longIdByLogicId.longValue()) {
				throw new ServiceException("Ambiguous parameter: transazioneLlm.id ["+tableId+"] does not match logic id ["+longIdByLogicId+"]");
			}
		} else {
			tableId = longIdByLogicId;
			transazioneLlm.setId(tableId);
		}
		if(tableId==null || tableId<=0){
			throw new ServiceException("Retrieve tableId failed");
		}

		this.update(jdbcProperties, log, connection, sqlQueryObject, tableId, transazioneLlm, idMappingResolutionBehaviour);
	}
	@Override
	public void update(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, TransazioneLlm transazioneLlm, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException, NotImplementedException, ServiceException, SQLQueryObjectException, JDBCAdapterException, ExpressionException {
	
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);
		
		ISQLQueryObject sqlQueryObjectInsert = sqlQueryObject.newSQLQueryObject();
		ISQLQueryObject sqlQueryObjectDelete = sqlQueryObjectInsert.newSQLQueryObject();
		ISQLQueryObject sqlQueryObjectGet = sqlQueryObjectDelete.newSQLQueryObject();
		ISQLQueryObject sqlQueryObjectUpdate = sqlQueryObjectGet.newSQLQueryObject();
		


		// Object transazioneLlm
		sqlQueryObjectUpdate.setANDLogicOperator(true);
		sqlQueryObjectUpdate.addUpdateTable(this.getTransazioneLlmFieldConverter().toTable(TransazioneLlm.model()));

		java.util.List<JDBCObject> lstObjectsTransazioneLlm = new java.util.ArrayList<>();
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneLlmFieldConverter().toColumn(TransazioneLlm.model().ID_TRANSAZIONE,false), "?");
		lstObjectsTransazioneLlm.add(new JDBCObject(transazioneLlm.getIdTransazione(), TransazioneLlm.model().ID_TRANSAZIONE.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneLlmFieldConverter().toColumn(TransazioneLlm.model().DATA_INGRESSO_RICHIESTA,false), "?");
		lstObjectsTransazioneLlm.add(new JDBCObject(transazioneLlm.getDataIngressoRichiesta(), TransazioneLlm.model().DATA_INGRESSO_RICHIESTA.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneLlmFieldConverter().toColumn(TransazioneLlm.model().LLM_PROVIDER,false), "?");
		lstObjectsTransazioneLlm.add(new JDBCObject(transazioneLlm.getLlmProvider(), TransazioneLlm.model().LLM_PROVIDER.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneLlmFieldConverter().toColumn(TransazioneLlm.model().LLM_MODEL,false), "?");
		lstObjectsTransazioneLlm.add(new JDBCObject(transazioneLlm.getLlmModel(), TransazioneLlm.model().LLM_MODEL.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneLlmFieldConverter().toColumn(TransazioneLlm.model().LLM_PROVIDER_BINDING,false), "?");
		lstObjectsTransazioneLlm.add(new JDBCObject(transazioneLlm.getLlmProviderBinding(), TransazioneLlm.model().LLM_PROVIDER_BINDING.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneLlmFieldConverter().toColumn(TransazioneLlm.model().TOKEN_INPUT,false), "?");
		lstObjectsTransazioneLlm.add(new JDBCObject(transazioneLlm.getTokenInput(), TransazioneLlm.model().TOKEN_INPUT.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneLlmFieldConverter().toColumn(TransazioneLlm.model().TOKEN_OUTPUT,false), "?");
		lstObjectsTransazioneLlm.add(new JDBCObject(transazioneLlm.getTokenOutput(), TransazioneLlm.model().TOKEN_OUTPUT.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneLlmFieldConverter().toColumn(TransazioneLlm.model().COST_ESTIMATED,false), "?");
		lstObjectsTransazioneLlm.add(new JDBCObject(transazioneLlm.getCostEstimated(), TransazioneLlm.model().COST_ESTIMATED.getFieldType()));
		sqlQueryObjectUpdate.addWhereCondition("id=?");
		lstObjectsTransazioneLlm.add(new JDBCObject(tableId, Long.class));


		// Update transazioneLlm
		jdbcUtilities.executeUpdate(sqlQueryObjectUpdate.createSQLUpdate(), jdbcProperties.isShowSql(), 
			lstObjectsTransazioneLlm.toArray(new JDBCObject[]{}));

	}
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdTransazioneLlm id, UpdateField ... updateFields) throws Exception {
		
		GenericJDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getTransazioneLlmFieldConverter().toTable(TransazioneLlm.model()), 
				this.getMapTableToPKColumnEngine(), 
				this.getRootTablePrimaryKeyValuesEngine(jdbcProperties, log, connection, sqlQueryObject, id),
				this.getTransazioneLlmFieldConverter(), this, null, updateFields);
	}
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdTransazioneLlm id, IExpression condition, UpdateField ... updateFields) throws Exception {
		
		GenericJDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getTransazioneLlmFieldConverter().toTable(TransazioneLlm.model()), 
				this.getMapTableToPKColumnEngine(), 
				this.getRootTablePrimaryKeyValuesEngine(jdbcProperties, log, connection, sqlQueryObject, id),
				this.getTransazioneLlmFieldConverter(), this, condition, updateFields);
	}
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdTransazioneLlm id, UpdateModel ... updateModels) throws Exception {
		
		GenericJDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getTransazioneLlmFieldConverter().toTable(TransazioneLlm.model()), 
				this.getMapTableToPKColumnEngine(), 
				this.getRootTablePrimaryKeyValuesEngine(jdbcProperties, log, connection, sqlQueryObject, id),
				this.getTransazioneLlmFieldConverter(), this, updateModels);
	}	
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, UpdateField ... updateFields) throws Exception {
		java.util.List<Object> ids = new java.util.ArrayList<>();
		ids.add(tableId);
		GenericJDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getTransazioneLlmFieldConverter().toTable(TransazioneLlm.model()), 
				this.getMapTableToPKColumnEngine(), 
				ids,
				this.getTransazioneLlmFieldConverter(), this, null, updateFields);
	}
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, IExpression condition, UpdateField ... updateFields) throws Exception {
		java.util.List<Object> ids = new java.util.ArrayList<>();
		ids.add(tableId);
		GenericJDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getTransazioneLlmFieldConverter().toTable(TransazioneLlm.model()), 
				this.getMapTableToPKColumnEngine(), 
				ids,
				this.getTransazioneLlmFieldConverter(), this, condition, updateFields);
	}
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, UpdateModel ... updateModels) throws Exception {
		java.util.List<Object> ids = new java.util.ArrayList<>();
		ids.add(tableId);
		GenericJDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getTransazioneLlmFieldConverter().toTable(TransazioneLlm.model()), 
				this.getMapTableToPKColumnEngine(), 
				ids,
				this.getTransazioneLlmFieldConverter(), this, updateModels);
	}
	
	@Override
	public void updateOrCreate(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdTransazioneLlm oldId, TransazioneLlm transazioneLlm, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws MultipleResultException, NotImplementedException, ServiceException, NotFoundException, SQLQueryObjectException, ExpressionException, JDBCAdapterException, KeyGeneratorException {
	
		if(this.exists(jdbcProperties, log, connection, sqlQueryObject, oldId)) {
			this.update(jdbcProperties, log, connection, sqlQueryObject, oldId, transazioneLlm,idMappingResolutionBehaviour);
		} else {
			this.create(jdbcProperties, log, connection, sqlQueryObject, transazioneLlm,idMappingResolutionBehaviour);
		}
		
	}
	
	@Override
	public void updateOrCreate(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, TransazioneLlm transazioneLlm, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws MultipleResultException, SQLQueryObjectException, JDBCAdapterException, ExpressionException, KeyGeneratorException, ServiceException, NotFoundException, NotImplementedException {

		if(this.exists(jdbcProperties, log, connection, sqlQueryObject, tableId)) {
			this.update(jdbcProperties, log, connection, sqlQueryObject, tableId, transazioneLlm,idMappingResolutionBehaviour);
		} else {
			this.create(jdbcProperties, log, connection, sqlQueryObject, transazioneLlm,idMappingResolutionBehaviour);
		}

	}
	
	@Override
	public void delete(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, TransazioneLlm transazioneLlm) throws NotImplementedException,ServiceException, SQLQueryObjectException, JDBCAdapterException, ExpressionException, NotFoundException, MultipleResultException {
		
		
		Long longId = null;
		if( (transazioneLlm.getId()!=null) && (transazioneLlm.getId()>0) ){
			longId = transazioneLlm.getId();
		}
		else{
			IdTransazioneLlm idTransazioneLlm = this.convertToId(jdbcProperties,log,connection,sqlQueryObject,transazioneLlm);
			longId = this.findIdTransazioneLlm(jdbcProperties,log,connection,sqlQueryObject,idTransazioneLlm,false);
			if(longId == null){
				return; // entry not exists
			}
		}		
		
		this.deleteEngine(jdbcProperties, log, connection, sqlQueryObject, longId);
		
	}

	private void deleteEngine(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, Long id) throws ServiceException, SQLQueryObjectException, JDBCAdapterException, ExpressionException {
	
		if(id==null){
			throw new ServiceException("Id is null");
		}
		if(id.longValue()<=0){
			throw new ServiceException("Id is less equals 0");
		}
		
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);
		
		ISQLQueryObject sqlQueryObjectDelete = sqlQueryObject.newSQLQueryObject();
		

		// Object transazioneLlm
		sqlQueryObjectDelete.setANDLogicOperator(true);
		sqlQueryObjectDelete.addDeleteTable(this.getTransazioneLlmFieldConverter().toTable(TransazioneLlm.model()));
		sqlQueryObjectDelete.addWhereCondition("id=?");

		// Delete transazioneLlm
		jdbcUtilities.execute(sqlQueryObjectDelete.createSQLDelete(), jdbcProperties.isShowSql(), 
			new JDBCObject(id,Long.class));

	}

	@Override
	public void deleteById(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdTransazioneLlm idTransazioneLlm) throws NotImplementedException,ServiceException, SQLQueryObjectException, ExpressionException, MultipleResultException, JDBCAdapterException {

		Long id = null;
		try{
			id = this.findIdTransazioneLlm(jdbcProperties, log, connection, sqlQueryObject, idTransazioneLlm, true);
		}catch(NotFoundException notFound){
			return;
		}
		this.deleteEngine(jdbcProperties, log, connection, sqlQueryObject, id);
		
	}
	
	@Override
	public NonNegativeNumber deleteAll(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject) throws ExpressionException, NotImplementedException, ServiceException, SQLQueryObjectException, JDBCAdapterException, ExpressionNotImplementedException {
		
		return this.deleteAll(jdbcProperties, log, connection, sqlQueryObject, new JDBCExpression(this.getTransazioneLlmFieldConverter()));

	}

	@Override
	public NonNegativeNumber deleteAll(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCExpression expression) throws NotImplementedException, ServiceException, SQLQueryObjectException, JDBCAdapterException, ExpressionException, ExpressionNotImplementedException {

		java.util.List<Long> lst = this.findAllTableIds(jdbcProperties, log, connection, sqlQueryObject, new JDBCPaginatedExpression(expression));
		
		for(Long id : lst) {
			this.deleteEngine(jdbcProperties, log, connection, sqlQueryObject, id);
		}
		
		return new NonNegativeNumber(lst.size());
	
	}



	// -- DB
	
	@Override
	public void deleteById(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId) throws ServiceException, NotImplementedException, SQLQueryObjectException, JDBCAdapterException, ExpressionException {
		this.deleteEngine(jdbcProperties, log, connection, sqlQueryObject, Long.valueOf(tableId));
	}
	
	@Override
	public int nativeUpdate(JDBCServiceManagerProperties jdbcProperties, Logger log,Connection connection,ISQLQueryObject sqlObject, String sql,Object ... param) throws ServiceException,NotImplementedException, SQLQueryObjectException, JDBCAdapterException {
	
		return org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.nativeUpdate(jdbcProperties, log, connection, sqlObject,
																							sql,param);
	
	}
}
