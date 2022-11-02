/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it).
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

import org.openspcoop2.utils.sql.ISQLQueryObject;

import org.slf4j.Logger;

import org.openspcoop2.generic_project.dao.jdbc.IJDBCServiceCRUDWithoutId;
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

import org.openspcoop2.core.statistiche.StatisticaInfo;
import org.openspcoop2.core.statistiche.constants.TipoIntervalloStatistico;

/**     
 * JDBCStatisticaInfoServiceImpl
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JDBCStatisticaInfoServiceImpl extends JDBCStatisticaInfoServiceSearchImpl
	implements IJDBCServiceCRUDWithoutId<StatisticaInfo, JDBCServiceManager> {

	@Override
	public void create(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, StatisticaInfo statisticaInfo, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotImplementedException,ServiceException,Exception {

		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);
		
		ISQLQueryObject sqlQueryObjectInsert = sqlQueryObject.newSQLQueryObject();
				


		// Object statisticaInfo
		sqlQueryObjectInsert.addInsertTable(this.getStatisticaInfoFieldConverter().toTable(StatisticaInfo.model()));
		sqlQueryObjectInsert.addInsertField(this.getStatisticaInfoFieldConverter().toColumn(StatisticaInfo.model().TIPO_STATISTICA,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getStatisticaInfoFieldConverter().toColumn(StatisticaInfo.model().DATA_ULTIMA_GENERAZIONE,false),"?");

		// Insert statisticaInfo
		String insertSql = sqlQueryObjectInsert.createSQLInsert();
		jdbcUtilities.execute(insertSql, jdbcProperties.isShowSql(), 
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaInfo.getTipoStatistica(),StatisticaInfo.model().TIPO_STATISTICA.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaInfo.getDataUltimaGenerazione(),StatisticaInfo.model().DATA_ULTIMA_GENERAZIONE.getFieldType())
		);

		
	}

	@Override
	public void update(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, StatisticaInfo statisticaInfo, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		

	
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);
		
		ISQLQueryObject sqlQueryObjectInsert = sqlQueryObject.newSQLQueryObject();
		ISQLQueryObject sqlQueryObjectDelete = sqlQueryObjectInsert.newSQLQueryObject();
		ISQLQueryObject sqlQueryObjectGet = sqlQueryObjectDelete.newSQLQueryObject();
		ISQLQueryObject sqlQueryObjectUpdate = sqlQueryObjectGet.newSQLQueryObject();
		


		// Object statisticaInfo
		sqlQueryObjectUpdate.setANDLogicOperator(true);
		sqlQueryObjectUpdate.addUpdateTable(this.getStatisticaInfoFieldConverter().toTable(StatisticaInfo.model()));
		boolean isUpdate = true;
		java.util.List<JDBCObject> lstObjects = new java.util.ArrayList<JDBCObject>();
		sqlQueryObjectUpdate.addUpdateField(this.getStatisticaInfoFieldConverter().toColumn(StatisticaInfo.model().TIPO_STATISTICA,false), "?");
		lstObjects.add(new JDBCObject(statisticaInfo.getTipoStatistica(), StatisticaInfo.model().TIPO_STATISTICA.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getStatisticaInfoFieldConverter().toColumn(StatisticaInfo.model().DATA_ULTIMA_GENERAZIONE,false), "?");
		lstObjects.add(new JDBCObject(statisticaInfo.getDataUltimaGenerazione(), StatisticaInfo.model().DATA_ULTIMA_GENERAZIONE.getFieldType()));
		sqlQueryObjectUpdate.addWhereCondition(this.getStatisticaInfoFieldConverter().toColumn(StatisticaInfo.model().TIPO_STATISTICA,false)+"=?");
		lstObjects.add(new JDBCObject(statisticaInfo.get_value_tipoStatistica(), String.class));

		if(isUpdate) {
			// Update statisticaInfo
			jdbcUtilities.executeUpdate(sqlQueryObjectUpdate.createSQLUpdate(), jdbcProperties.isShowSql(), 
				lstObjects.toArray(new JDBCObject[]{}));
		}

	}
	@Override
	public void update(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, StatisticaInfo statisticaInfo, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		throw new NotImplementedException("Table without long id column PK");
	}
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, StatisticaInfo statisticaInfo, UpdateField ... updateFields) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		
		GenericJDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getStatisticaInfoFieldConverter().toTable(StatisticaInfo.model()), 
				this._getMapTableToPKColumn(), 
				this._getRootTablePrimaryKeyValues(jdbcProperties, log, connection, sqlQueryObject, statisticaInfo),
				this.getStatisticaInfoFieldConverter(), this, null, updateFields);
	}
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, StatisticaInfo statisticaInfo, IExpression condition, UpdateField ... updateFields) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		
		GenericJDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getStatisticaInfoFieldConverter().toTable(StatisticaInfo.model()), 
				this._getMapTableToPKColumn(), 
				this._getRootTablePrimaryKeyValues(jdbcProperties, log, connection, sqlQueryObject, statisticaInfo),
				this.getStatisticaInfoFieldConverter(), this, condition, updateFields);
	}
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, StatisticaInfo statisticaInfo, UpdateModel ... updateModels) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		
		GenericJDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getStatisticaInfoFieldConverter().toTable(StatisticaInfo.model()), 
				this._getMapTableToPKColumn(), 
				this._getRootTablePrimaryKeyValues(jdbcProperties, log, connection, sqlQueryObject, statisticaInfo),
				this.getStatisticaInfoFieldConverter(), this, updateModels);
	}	
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, UpdateField ... updateFields) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		throw new NotImplementedException("Table without long id column PK");
	}
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, IExpression condition, UpdateField ... updateFields) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		throw new NotImplementedException("Table without long id column PK");
	}
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, UpdateModel ... updateModels) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		throw new NotImplementedException("Table without long id column PK");
	}
	
	@Override
	public void updateOrCreate(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, StatisticaInfo statisticaInfo, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotImplementedException,ServiceException,Exception {
	
//		Long id = statisticaInfo.getId();
//		if(id != null && this.exists(jdbcProperties, log, connection, sqlQueryObject, id)) {
		this.update(jdbcProperties, log, connection, sqlQueryObject, statisticaInfo,idMappingResolutionBehaviour);
//		} else {
//			this.create(jdbcProperties, log, connection, sqlQueryObject, statisticaInfo,idMappingResolutionBehaviour);
//		}
		
	}
	
	@Override
	public void updateOrCreate(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, StatisticaInfo statisticaInfo, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotImplementedException,ServiceException,Exception {
		throw new NotImplementedException("Table without long id column PK");
	}
	
	@Override
	public void delete(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, StatisticaInfo statisticaInfo) throws NotImplementedException,ServiceException,Exception {
		
		TipoIntervalloStatistico idObject = statisticaInfo.getTipoStatistica();
		this._delete(jdbcProperties, log, connection, sqlQueryObject, idObject);
                

	}

	private void _delete(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, Object id) throws NotImplementedException,ServiceException,Exception {
		
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);
		
		ISQLQueryObject sqlQueryObjectDelete = sqlQueryObject.newSQLQueryObject();
		
		if(id == null ){
			throw new ServiceException("ID Logico non trovato");
		}
		if(id instanceof TipoIntervalloStatistico == false){
			throw new ServiceException("Tipo dell'id non valido, atteso["+TipoIntervalloStatistico.class.getName()+"] trovato["+id.getClass().getName()+"]");
		}
		TipoIntervalloStatistico tipoStatistica = (TipoIntervalloStatistico) id;

		// Object statisticaInfo
		sqlQueryObjectDelete.setANDLogicOperator(true);
		sqlQueryObjectDelete.addDeleteTable(this.getStatisticaInfoFieldConverter().toTable(StatisticaInfo.model()));
		sqlQueryObjectDelete.addWhereCondition(this.getStatisticaInfoFieldConverter().toColumn(StatisticaInfo.model().TIPO_STATISTICA,false)+"=?");

		// Delete statisticaInfo
		jdbcUtilities.execute(sqlQueryObjectDelete.createSQLDelete(), jdbcProperties.isShowSql(), 
			new JDBCObject(tipoStatistica.getValue(),String.class));

	}

	
	@Override
	public NonNegativeNumber deleteAll(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject) throws NotImplementedException,ServiceException,Exception {
		
		return this.deleteAll(jdbcProperties, log, connection, sqlQueryObject, new JDBCExpression(this.getStatisticaInfoFieldConverter()));

	}

	@Override
	public NonNegativeNumber deleteAll(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCExpression expression) throws NotImplementedException, ServiceException,Exception {

		java.util.List<Object> lst = this._findAllObjectIds(jdbcProperties, log, connection, sqlQueryObject, new JDBCPaginatedExpression(expression));
		
		for(Object id : lst) {
			this._delete(jdbcProperties, log, connection, sqlQueryObject, id);
		}
		
		return new NonNegativeNumber(lst.size());
	
	}



	// -- DB
	
	@Override
	public void deleteById(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId) throws ServiceException, NotImplementedException, Exception {
		throw new NotImplementedException("Table without long id column PK");
	}
	
	@Override
	public int nativeUpdate(JDBCServiceManagerProperties jdbcProperties, Logger log,Connection connection,ISQLQueryObject sqlObject, String sql,Object ... param) throws ServiceException,NotImplementedException, Exception {
	
		return org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.nativeUpdate(jdbcProperties, log, connection, sqlObject,
																							sql,param);
	
	}
}
