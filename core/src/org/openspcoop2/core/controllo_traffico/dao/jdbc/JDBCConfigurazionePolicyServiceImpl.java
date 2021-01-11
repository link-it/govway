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
package org.openspcoop2.core.controllo_traffico.dao.jdbc;

import java.sql.Connection;

import org.openspcoop2.utils.sql.ISQLQueryObject;

import org.slf4j.Logger;

import org.openspcoop2.generic_project.dao.jdbc.IJDBCServiceCRUDWithId;
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
import org.openspcoop2.core.controllo_traffico.ConfigurazionePolicy;
import org.openspcoop2.core.controllo_traffico.IdPolicy;

/**     
 * JDBCConfigurazionePolicyServiceImpl
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JDBCConfigurazionePolicyServiceImpl extends JDBCConfigurazionePolicyServiceSearchImpl
	implements IJDBCServiceCRUDWithId<ConfigurazionePolicy, IdPolicy, JDBCServiceManager> {

	@Override
	public void create(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, ConfigurazionePolicy configurazionePolicy, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotImplementedException,ServiceException,Exception {

		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);
		
		ISQLQueryObject sqlQueryObjectInsert = sqlQueryObject.newSQLQueryObject();
				


		// Object configurazionePolicy
		sqlQueryObjectInsert.addInsertTable(this.getConfigurazionePolicyFieldConverter().toTable(ConfigurazionePolicy.model()));
		sqlQueryObjectInsert.addInsertField(this.getConfigurazionePolicyFieldConverter().toColumn(ConfigurazionePolicy.model().ID_POLICY,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getConfigurazionePolicyFieldConverter().toColumn(ConfigurazionePolicy.model().BUILT_IN,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getConfigurazionePolicyFieldConverter().toColumn(ConfigurazionePolicy.model().DESCRIZIONE,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getConfigurazionePolicyFieldConverter().toColumn(ConfigurazionePolicy.model().RISORSA,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getConfigurazionePolicyFieldConverter().toColumn(ConfigurazionePolicy.model().SIMULTANEE,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getConfigurazionePolicyFieldConverter().toColumn(ConfigurazionePolicy.model().VALORE,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getConfigurazionePolicyFieldConverter().toColumn(ConfigurazionePolicy.model().VALORE_TIPO_BANDA,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getConfigurazionePolicyFieldConverter().toColumn(ConfigurazionePolicy.model().VALORE_TIPO_LATENZA,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getConfigurazionePolicyFieldConverter().toColumn(ConfigurazionePolicy.model().MODALITA_CONTROLLO,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getConfigurazionePolicyFieldConverter().toColumn(ConfigurazionePolicy.model().TIPO_INTERVALLO_OSSERVAZIONE_REALTIME,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getConfigurazionePolicyFieldConverter().toColumn(ConfigurazionePolicy.model().TIPO_INTERVALLO_OSSERVAZIONE_STATISTICO,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getConfigurazionePolicyFieldConverter().toColumn(ConfigurazionePolicy.model().INTERVALLO_OSSERVAZIONE,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getConfigurazionePolicyFieldConverter().toColumn(ConfigurazionePolicy.model().FINESTRA_OSSERVAZIONE,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getConfigurazionePolicyFieldConverter().toColumn(ConfigurazionePolicy.model().TIPO_APPLICABILITA,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getConfigurazionePolicyFieldConverter().toColumn(ConfigurazionePolicy.model().APPLICABILITA_CON_CONGESTIONE,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getConfigurazionePolicyFieldConverter().toColumn(ConfigurazionePolicy.model().APPLICABILITA_DEGRADO_PRESTAZIONALE,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getConfigurazionePolicyFieldConverter().toColumn(ConfigurazionePolicy.model().DEGRADO_AVG_TIME_MODALITA_CONTROLLO,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getConfigurazionePolicyFieldConverter().toColumn(ConfigurazionePolicy.model().DEGRADO_AVG_TIME_TIPO_INTERVALLO_OSSERVAZIONE_REALTIME,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getConfigurazionePolicyFieldConverter().toColumn(ConfigurazionePolicy.model().DEGRADO_AVG_TIME_TIPO_INTERVALLO_OSSERVAZIONE_STATISTICO,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getConfigurazionePolicyFieldConverter().toColumn(ConfigurazionePolicy.model().DEGRADO_AVG_TIME_INTERVALLO_OSSERVAZIONE,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getConfigurazionePolicyFieldConverter().toColumn(ConfigurazionePolicy.model().DEGRADO_AVG_TIME_FINESTRA_OSSERVAZIONE,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getConfigurazionePolicyFieldConverter().toColumn(ConfigurazionePolicy.model().DEGRADO_AVG_TIME_TIPO_LATENZA,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getConfigurazionePolicyFieldConverter().toColumn(ConfigurazionePolicy.model().APPLICABILITA_STATO_ALLARME,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getConfigurazionePolicyFieldConverter().toColumn(ConfigurazionePolicy.model().ALLARME_NOME,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getConfigurazionePolicyFieldConverter().toColumn(ConfigurazionePolicy.model().ALLARME_STATO,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getConfigurazionePolicyFieldConverter().toColumn(ConfigurazionePolicy.model().ALLARME_NOT_STATO,false),"?");

		// Insert configurazionePolicy
		org.openspcoop2.utils.jdbc.IKeyGeneratorObject keyGenerator = this.getConfigurazionePolicyFetch().getKeyGeneratorObject(ConfigurazionePolicy.model());
		long id = jdbcUtilities.insertAndReturnGeneratedKey(sqlQueryObjectInsert, keyGenerator, jdbcProperties.isShowSql(),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(configurazionePolicy.getIdPolicy(),ConfigurazionePolicy.model().ID_POLICY.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(configurazionePolicy.getBuiltIn(),ConfigurazionePolicy.model().BUILT_IN.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(configurazionePolicy.getDescrizione(),ConfigurazionePolicy.model().DESCRIZIONE.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(configurazionePolicy.getRisorsa(),ConfigurazionePolicy.model().RISORSA.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(configurazionePolicy.getSimultanee(),ConfigurazionePolicy.model().SIMULTANEE.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(configurazionePolicy.getValore(),ConfigurazionePolicy.model().VALORE.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(configurazionePolicy.getValoreTipoBanda(),ConfigurazionePolicy.model().VALORE_TIPO_BANDA.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(configurazionePolicy.getValoreTipoLatenza(),ConfigurazionePolicy.model().VALORE_TIPO_LATENZA.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(configurazionePolicy.getModalitaControllo(),ConfigurazionePolicy.model().MODALITA_CONTROLLO.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(configurazionePolicy.getTipoIntervalloOsservazioneRealtime(),ConfigurazionePolicy.model().TIPO_INTERVALLO_OSSERVAZIONE_REALTIME.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(configurazionePolicy.getTipoIntervalloOsservazioneStatistico(),ConfigurazionePolicy.model().TIPO_INTERVALLO_OSSERVAZIONE_STATISTICO.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(configurazionePolicy.getIntervalloOsservazione(),ConfigurazionePolicy.model().INTERVALLO_OSSERVAZIONE.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(configurazionePolicy.getFinestraOsservazione(),ConfigurazionePolicy.model().FINESTRA_OSSERVAZIONE.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(configurazionePolicy.getTipoApplicabilita(),ConfigurazionePolicy.model().TIPO_APPLICABILITA.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(configurazionePolicy.getApplicabilitaConCongestione(),ConfigurazionePolicy.model().APPLICABILITA_CON_CONGESTIONE.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(configurazionePolicy.getApplicabilitaDegradoPrestazionale(),ConfigurazionePolicy.model().APPLICABILITA_DEGRADO_PRESTAZIONALE.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(configurazionePolicy.getDegradoAvgTimeModalitaControllo(),ConfigurazionePolicy.model().DEGRADO_AVG_TIME_MODALITA_CONTROLLO.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(configurazionePolicy.getDegradoAvgTimeTipoIntervalloOsservazioneRealtime(),ConfigurazionePolicy.model().DEGRADO_AVG_TIME_TIPO_INTERVALLO_OSSERVAZIONE_REALTIME.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(configurazionePolicy.getDegradoAvgTimeTipoIntervalloOsservazioneStatistico(),ConfigurazionePolicy.model().DEGRADO_AVG_TIME_TIPO_INTERVALLO_OSSERVAZIONE_STATISTICO.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(configurazionePolicy.getDegradoAvgTimeIntervalloOsservazione(),ConfigurazionePolicy.model().DEGRADO_AVG_TIME_INTERVALLO_OSSERVAZIONE.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(configurazionePolicy.getDegradoAvgTimeFinestraOsservazione(),ConfigurazionePolicy.model().DEGRADO_AVG_TIME_FINESTRA_OSSERVAZIONE.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(configurazionePolicy.getDegradoAvgTimeTipoLatenza(),ConfigurazionePolicy.model().DEGRADO_AVG_TIME_TIPO_LATENZA.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(configurazionePolicy.getApplicabilitaStatoAllarme(),ConfigurazionePolicy.model().APPLICABILITA_STATO_ALLARME.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(configurazionePolicy.getAllarmeNome(),ConfigurazionePolicy.model().ALLARME_NOME.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(configurazionePolicy.getAllarmeStato(),ConfigurazionePolicy.model().ALLARME_STATO.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(configurazionePolicy.getAllarmeNotStato(),ConfigurazionePolicy.model().ALLARME_NOT_STATO.getFieldType())
		);
		configurazionePolicy.setId(id);

		
	}

	@Override
	public void update(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdPolicy oldId, ConfigurazionePolicy configurazionePolicy, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		ISQLQueryObject sqlQueryObjectUpdate = sqlQueryObject.newSQLQueryObject();
		Long longIdByLogicId = this.findIdConfigurazionePolicy(jdbcProperties, log, connection, sqlQueryObjectUpdate.newSQLQueryObject(), oldId, true);
		Long tableId = configurazionePolicy.getId();
		if(tableId != null && tableId.longValue() > 0) {
			if(tableId.longValue() != longIdByLogicId.longValue()) {
				throw new Exception("Ambiguous parameter: configurazionePolicy.id ["+tableId+"] does not match logic id ["+longIdByLogicId+"]");
			}
		} else {
			tableId = longIdByLogicId;
			configurazionePolicy.setId(tableId);
		}
		if(tableId==null || tableId<=0){
			throw new Exception("Retrieve tableId failed");
		}

		this.update(jdbcProperties, log, connection, sqlQueryObject, tableId, configurazionePolicy, idMappingResolutionBehaviour);
	}
	@Override
	public void update(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, ConfigurazionePolicy configurazionePolicy, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException, NotImplementedException, ServiceException, Exception {
	
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);
		
		ISQLQueryObject sqlQueryObjectInsert = sqlQueryObject.newSQLQueryObject();
		ISQLQueryObject sqlQueryObjectDelete = sqlQueryObjectInsert.newSQLQueryObject();
		ISQLQueryObject sqlQueryObjectGet = sqlQueryObjectDelete.newSQLQueryObject();
		ISQLQueryObject sqlQueryObjectUpdate = sqlQueryObjectGet.newSQLQueryObject();
		


		// Object configurazionePolicy
		sqlQueryObjectUpdate.setANDLogicOperator(true);
		sqlQueryObjectUpdate.addUpdateTable(this.getConfigurazionePolicyFieldConverter().toTable(ConfigurazionePolicy.model()));
		boolean isUpdate = true;
		java.util.List<JDBCObject> lstObjects = new java.util.ArrayList<JDBCObject>();
		sqlQueryObjectUpdate.addUpdateField(this.getConfigurazionePolicyFieldConverter().toColumn(ConfigurazionePolicy.model().ID_POLICY,false), "?");
		lstObjects.add(new JDBCObject(configurazionePolicy.getIdPolicy(), ConfigurazionePolicy.model().ID_POLICY.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getConfigurazionePolicyFieldConverter().toColumn(ConfigurazionePolicy.model().BUILT_IN,false), "?");
		lstObjects.add(new JDBCObject(configurazionePolicy.getBuiltIn(), ConfigurazionePolicy.model().BUILT_IN.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getConfigurazionePolicyFieldConverter().toColumn(ConfigurazionePolicy.model().DESCRIZIONE,false), "?");
		lstObjects.add(new JDBCObject(configurazionePolicy.getDescrizione(), ConfigurazionePolicy.model().DESCRIZIONE.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getConfigurazionePolicyFieldConverter().toColumn(ConfigurazionePolicy.model().RISORSA,false), "?");
		lstObjects.add(new JDBCObject(configurazionePolicy.getRisorsa(), ConfigurazionePolicy.model().RISORSA.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getConfigurazionePolicyFieldConverter().toColumn(ConfigurazionePolicy.model().SIMULTANEE,false), "?");
		lstObjects.add(new JDBCObject(configurazionePolicy.getSimultanee(), ConfigurazionePolicy.model().SIMULTANEE.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getConfigurazionePolicyFieldConverter().toColumn(ConfigurazionePolicy.model().VALORE,false), "?");
		lstObjects.add(new JDBCObject(configurazionePolicy.getValore(), ConfigurazionePolicy.model().VALORE.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getConfigurazionePolicyFieldConverter().toColumn(ConfigurazionePolicy.model().VALORE_TIPO_BANDA,false), "?");
		lstObjects.add(new JDBCObject(configurazionePolicy.getValoreTipoBanda(), ConfigurazionePolicy.model().VALORE_TIPO_BANDA.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getConfigurazionePolicyFieldConverter().toColumn(ConfigurazionePolicy.model().VALORE_TIPO_LATENZA,false), "?");
		lstObjects.add(new JDBCObject(configurazionePolicy.getValoreTipoLatenza(), ConfigurazionePolicy.model().VALORE_TIPO_LATENZA.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getConfigurazionePolicyFieldConverter().toColumn(ConfigurazionePolicy.model().MODALITA_CONTROLLO,false), "?");
		lstObjects.add(new JDBCObject(configurazionePolicy.getModalitaControllo(), ConfigurazionePolicy.model().MODALITA_CONTROLLO.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getConfigurazionePolicyFieldConverter().toColumn(ConfigurazionePolicy.model().TIPO_INTERVALLO_OSSERVAZIONE_REALTIME,false), "?");
		lstObjects.add(new JDBCObject(configurazionePolicy.getTipoIntervalloOsservazioneRealtime(), ConfigurazionePolicy.model().TIPO_INTERVALLO_OSSERVAZIONE_REALTIME.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getConfigurazionePolicyFieldConverter().toColumn(ConfigurazionePolicy.model().TIPO_INTERVALLO_OSSERVAZIONE_STATISTICO,false), "?");
		lstObjects.add(new JDBCObject(configurazionePolicy.getTipoIntervalloOsservazioneStatistico(), ConfigurazionePolicy.model().TIPO_INTERVALLO_OSSERVAZIONE_STATISTICO.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getConfigurazionePolicyFieldConverter().toColumn(ConfigurazionePolicy.model().INTERVALLO_OSSERVAZIONE,false), "?");
		lstObjects.add(new JDBCObject(configurazionePolicy.getIntervalloOsservazione(), ConfigurazionePolicy.model().INTERVALLO_OSSERVAZIONE.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getConfigurazionePolicyFieldConverter().toColumn(ConfigurazionePolicy.model().FINESTRA_OSSERVAZIONE,false), "?");
		lstObjects.add(new JDBCObject(configurazionePolicy.getFinestraOsservazione(), ConfigurazionePolicy.model().FINESTRA_OSSERVAZIONE.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getConfigurazionePolicyFieldConverter().toColumn(ConfigurazionePolicy.model().TIPO_APPLICABILITA,false), "?");
		lstObjects.add(new JDBCObject(configurazionePolicy.getTipoApplicabilita(), ConfigurazionePolicy.model().TIPO_APPLICABILITA.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getConfigurazionePolicyFieldConverter().toColumn(ConfigurazionePolicy.model().APPLICABILITA_CON_CONGESTIONE,false), "?");
		lstObjects.add(new JDBCObject(configurazionePolicy.getApplicabilitaConCongestione(), ConfigurazionePolicy.model().APPLICABILITA_CON_CONGESTIONE.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getConfigurazionePolicyFieldConverter().toColumn(ConfigurazionePolicy.model().APPLICABILITA_DEGRADO_PRESTAZIONALE,false), "?");
		lstObjects.add(new JDBCObject(configurazionePolicy.getApplicabilitaDegradoPrestazionale(), ConfigurazionePolicy.model().APPLICABILITA_DEGRADO_PRESTAZIONALE.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getConfigurazionePolicyFieldConverter().toColumn(ConfigurazionePolicy.model().DEGRADO_AVG_TIME_MODALITA_CONTROLLO,false), "?");
		lstObjects.add(new JDBCObject(configurazionePolicy.getDegradoAvgTimeModalitaControllo(), ConfigurazionePolicy.model().DEGRADO_AVG_TIME_MODALITA_CONTROLLO.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getConfigurazionePolicyFieldConverter().toColumn(ConfigurazionePolicy.model().DEGRADO_AVG_TIME_TIPO_INTERVALLO_OSSERVAZIONE_REALTIME,false), "?");
		lstObjects.add(new JDBCObject(configurazionePolicy.getDegradoAvgTimeTipoIntervalloOsservazioneRealtime(), ConfigurazionePolicy.model().DEGRADO_AVG_TIME_TIPO_INTERVALLO_OSSERVAZIONE_REALTIME.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getConfigurazionePolicyFieldConverter().toColumn(ConfigurazionePolicy.model().DEGRADO_AVG_TIME_TIPO_INTERVALLO_OSSERVAZIONE_STATISTICO,false), "?");
		lstObjects.add(new JDBCObject(configurazionePolicy.getDegradoAvgTimeTipoIntervalloOsservazioneStatistico(), ConfigurazionePolicy.model().DEGRADO_AVG_TIME_TIPO_INTERVALLO_OSSERVAZIONE_STATISTICO.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getConfigurazionePolicyFieldConverter().toColumn(ConfigurazionePolicy.model().DEGRADO_AVG_TIME_INTERVALLO_OSSERVAZIONE,false), "?");
		lstObjects.add(new JDBCObject(configurazionePolicy.getDegradoAvgTimeIntervalloOsservazione(), ConfigurazionePolicy.model().DEGRADO_AVG_TIME_INTERVALLO_OSSERVAZIONE.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getConfigurazionePolicyFieldConverter().toColumn(ConfigurazionePolicy.model().DEGRADO_AVG_TIME_FINESTRA_OSSERVAZIONE,false), "?");
		lstObjects.add(new JDBCObject(configurazionePolicy.getDegradoAvgTimeFinestraOsservazione(), ConfigurazionePolicy.model().DEGRADO_AVG_TIME_FINESTRA_OSSERVAZIONE.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getConfigurazionePolicyFieldConverter().toColumn(ConfigurazionePolicy.model().DEGRADO_AVG_TIME_TIPO_LATENZA,false), "?");
		lstObjects.add(new JDBCObject(configurazionePolicy.getDegradoAvgTimeTipoLatenza(), ConfigurazionePolicy.model().DEGRADO_AVG_TIME_TIPO_LATENZA.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getConfigurazionePolicyFieldConverter().toColumn(ConfigurazionePolicy.model().APPLICABILITA_STATO_ALLARME,false), "?");
		lstObjects.add(new JDBCObject(configurazionePolicy.getApplicabilitaStatoAllarme(), ConfigurazionePolicy.model().APPLICABILITA_STATO_ALLARME.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getConfigurazionePolicyFieldConverter().toColumn(ConfigurazionePolicy.model().ALLARME_NOME,false), "?");
		lstObjects.add(new JDBCObject(configurazionePolicy.getAllarmeNome(), ConfigurazionePolicy.model().ALLARME_NOME.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getConfigurazionePolicyFieldConverter().toColumn(ConfigurazionePolicy.model().ALLARME_STATO,false), "?");
		lstObjects.add(new JDBCObject(configurazionePolicy.getAllarmeStato(), ConfigurazionePolicy.model().ALLARME_STATO.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getConfigurazionePolicyFieldConverter().toColumn(ConfigurazionePolicy.model().ALLARME_NOT_STATO,false), "?");
		lstObjects.add(new JDBCObject(configurazionePolicy.getAllarmeNotStato(), ConfigurazionePolicy.model().ALLARME_NOT_STATO.getFieldType()));
		sqlQueryObjectUpdate.addWhereCondition("id=?");
		lstObjects.add(new JDBCObject(tableId, Long.class));

		if(isUpdate) {
			// Update configurazionePolicy
			jdbcUtilities.executeUpdate(sqlQueryObjectUpdate.createSQLUpdate(), jdbcProperties.isShowSql(), 
				lstObjects.toArray(new JDBCObject[]{}));
		}


	}
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdPolicy id, UpdateField ... updateFields) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		
		JDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getConfigurazionePolicyFieldConverter().toTable(ConfigurazionePolicy.model()), 
				this._getMapTableToPKColumn(), 
				this._getRootTablePrimaryKeyValues(jdbcProperties, log, connection, sqlQueryObject, id),
				this.getConfigurazionePolicyFieldConverter(), this, null, updateFields);
	}
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdPolicy id, IExpression condition, UpdateField ... updateFields) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		
		JDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getConfigurazionePolicyFieldConverter().toTable(ConfigurazionePolicy.model()), 
				this._getMapTableToPKColumn(), 
				this._getRootTablePrimaryKeyValues(jdbcProperties, log, connection, sqlQueryObject, id),
				this.getConfigurazionePolicyFieldConverter(), this, condition, updateFields);
	}
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdPolicy id, UpdateModel ... updateModels) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		
		JDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getConfigurazionePolicyFieldConverter().toTable(ConfigurazionePolicy.model()), 
				this._getMapTableToPKColumn(), 
				this._getRootTablePrimaryKeyValues(jdbcProperties, log, connection, sqlQueryObject, id),
				this.getConfigurazionePolicyFieldConverter(), this, updateModels);
	}	
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, UpdateField ... updateFields) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		java.util.List<Object> ids = new java.util.ArrayList<Object>();
		ids.add(tableId);
		JDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getConfigurazionePolicyFieldConverter().toTable(ConfigurazionePolicy.model()), 
				this._getMapTableToPKColumn(), 
				ids,
				this.getConfigurazionePolicyFieldConverter(), this, null, updateFields);
	}
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, IExpression condition, UpdateField ... updateFields) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		java.util.List<Object> ids = new java.util.ArrayList<Object>();
		ids.add(tableId);
		JDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getConfigurazionePolicyFieldConverter().toTable(ConfigurazionePolicy.model()), 
				this._getMapTableToPKColumn(), 
				ids,
				this.getConfigurazionePolicyFieldConverter(), this, condition, updateFields);
	}
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, UpdateModel ... updateModels) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		java.util.List<Object> ids = new java.util.ArrayList<Object>();
		ids.add(tableId);
		JDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getConfigurazionePolicyFieldConverter().toTable(ConfigurazionePolicy.model()), 
				this._getMapTableToPKColumn(), 
				ids,
				this.getConfigurazionePolicyFieldConverter(), this, updateModels);
	}
	
	@Override
	public void updateOrCreate(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdPolicy oldId, ConfigurazionePolicy configurazionePolicy, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotImplementedException,ServiceException,Exception {
	
		if(this.exists(jdbcProperties, log, connection, sqlQueryObject, oldId)) {
			this.update(jdbcProperties, log, connection, sqlQueryObject, oldId, configurazionePolicy,idMappingResolutionBehaviour);
		} else {
			this.create(jdbcProperties, log, connection, sqlQueryObject, configurazionePolicy,idMappingResolutionBehaviour);
		}
		
	}
	
	@Override
	public void updateOrCreate(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, ConfigurazionePolicy configurazionePolicy, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotImplementedException,ServiceException,Exception {
		if(this.exists(jdbcProperties, log, connection, sqlQueryObject, tableId)) {
			this.update(jdbcProperties, log, connection, sqlQueryObject, tableId, configurazionePolicy,idMappingResolutionBehaviour);
		} else {
			this.create(jdbcProperties, log, connection, sqlQueryObject, configurazionePolicy,idMappingResolutionBehaviour);
		}
	}
	
	@Override
	public void delete(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, ConfigurazionePolicy configurazionePolicy) throws NotImplementedException,ServiceException,Exception {
		
		
		Long longId = null;
		if( (configurazionePolicy.getId()!=null) && (configurazionePolicy.getId()>0) ){
			longId = configurazionePolicy.getId();
		}
		else{
			IdPolicy idConfigurazionePolicy = this.convertToId(jdbcProperties,log,connection,sqlQueryObject,configurazionePolicy);
			longId = this.findIdConfigurazionePolicy(jdbcProperties,log,connection,sqlQueryObject,idConfigurazionePolicy,false);
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
		

		// Object configurazionePolicy
		sqlQueryObjectDelete.setANDLogicOperator(true);
		sqlQueryObjectDelete.addDeleteTable(this.getConfigurazionePolicyFieldConverter().toTable(ConfigurazionePolicy.model()));
		if(id != null)
			sqlQueryObjectDelete.addWhereCondition("id=?");

		// Delete configurazionePolicy
		jdbcUtilities.execute(sqlQueryObjectDelete.createSQLDelete(), jdbcProperties.isShowSql(), 
			new JDBCObject(id,Long.class));

	}

	@Override
	public void deleteById(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdPolicy idConfigurazionePolicy) throws NotImplementedException,ServiceException,Exception {

		Long id = null;
		try{
			id = this.findIdConfigurazionePolicy(jdbcProperties, log, connection, sqlQueryObject, idConfigurazionePolicy, true);
		}catch(NotFoundException notFound){
			return;
		}
		this._delete(jdbcProperties, log, connection, sqlQueryObject, id);
		
	}
	
	@Override
	public NonNegativeNumber deleteAll(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject) throws NotImplementedException,ServiceException,Exception {
		
		return this.deleteAll(jdbcProperties, log, connection, sqlQueryObject, new JDBCExpression(this.getConfigurazionePolicyFieldConverter()));

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
