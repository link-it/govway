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
package org.openspcoop2.core.controllo_congestione.dao.jdbc;

import java.sql.Connection;

import org.openspcoop2.utils.sql.ISQLQueryObject;

import org.slf4j.Logger;

import org.openspcoop2.generic_project.dao.jdbc.IJDBCServiceCRUDSingleObject;
import org.openspcoop2.generic_project.beans.UpdateField;
import org.openspcoop2.generic_project.beans.UpdateModel;

import org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities;
import org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.generic_project.exception.NotImplementedException;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.generic_project.expression.IExpression;

import org.openspcoop2.generic_project.dao.jdbc.JDBCServiceManagerProperties;

import org.openspcoop2.core.controllo_congestione.Cache;
import org.openspcoop2.core.controllo_congestione.ConfigurazioneRateLimiting;
import org.openspcoop2.core.controllo_congestione.ConfigurazioneGenerale;
import org.openspcoop2.core.controllo_congestione.ConfigurazioneControlloTraffico;
import org.openspcoop2.core.controllo_congestione.TempiRispostaErogazione;
import org.openspcoop2.core.controllo_congestione.TempiRispostaFruizione;
import org.openspcoop2.core.controllo_congestione.dao.jdbc.JDBCServiceManager;

/**     
 * JDBCConfigurazioneGeneraleServiceImpl
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JDBCConfigurazioneGeneraleServiceImpl extends JDBCConfigurazioneGeneraleServiceSearchImpl
	implements IJDBCServiceCRUDSingleObject<ConfigurazioneGenerale, JDBCServiceManager> {

	@Override
	public void create(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, ConfigurazioneGenerale configurazioneGenerale, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotImplementedException,ServiceException,Exception {

		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);
		
		ISQLQueryObject sqlQueryObjectInsert = sqlQueryObject.newSQLQueryObject();
				


		// Object configurazioneGenerale.getControlloTraffico()
		sqlQueryObjectInsert.addInsertTable(this.getConfigurazioneGeneraleFieldConverter().toTable(ConfigurazioneGenerale.model().CONTROLLO_TRAFFICO));
		sqlQueryObjectInsert.addInsertField(this.getConfigurazioneGeneraleFieldConverter().toColumn(ConfigurazioneGenerale.model().CONTROLLO_TRAFFICO.CONTROLLO_MAX_THREADS_ENABLED,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getConfigurazioneGeneraleFieldConverter().toColumn(ConfigurazioneGenerale.model().CONTROLLO_TRAFFICO.CONTROLLO_MAX_THREADS_WARNING_ONLY,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getConfigurazioneGeneraleFieldConverter().toColumn(ConfigurazioneGenerale.model().CONTROLLO_TRAFFICO.CONTROLLO_MAX_THREADS_SOGLIA,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getConfigurazioneGeneraleFieldConverter().toColumn(ConfigurazioneGenerale.model().CONTROLLO_TRAFFICO.CONTROLLO_MAX_THREADS_TIPO_ERRORE,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getConfigurazioneGeneraleFieldConverter().toColumn(ConfigurazioneGenerale.model().CONTROLLO_TRAFFICO.CONTROLLO_MAX_THREADS_TIPO_ERRORE_INCLUDI_DESCRIZIONE,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getConfigurazioneGeneraleFieldConverter().toColumn(ConfigurazioneGenerale.model().CONTROLLO_TRAFFICO.CONTROLLO_CONGESTIONE_ENABLED,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getConfigurazioneGeneraleFieldConverter().toColumn(ConfigurazioneGenerale.model().CONTROLLO_TRAFFICO.CONTROLLO_CONGESTIONE_THRESHOLD,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getConfigurazioneGeneraleFieldConverter().toColumn(ConfigurazioneGenerale.model().TEMPI_RISPOSTA_FRUIZIONE.CONNECTION_TIMEOUT,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getConfigurazioneGeneraleFieldConverter().toColumn(ConfigurazioneGenerale.model().TEMPI_RISPOSTA_FRUIZIONE.READ_TIMEOUT,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getConfigurazioneGeneraleFieldConverter().toColumn(ConfigurazioneGenerale.model().TEMPI_RISPOSTA_FRUIZIONE.TEMPO_MEDIO_RISPOSTA,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getConfigurazioneGeneraleFieldConverter().toColumn(ConfigurazioneGenerale.model().TEMPI_RISPOSTA_EROGAZIONE.CONNECTION_TIMEOUT,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getConfigurazioneGeneraleFieldConverter().toColumn(ConfigurazioneGenerale.model().TEMPI_RISPOSTA_EROGAZIONE.READ_TIMEOUT,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getConfigurazioneGeneraleFieldConverter().toColumn(ConfigurazioneGenerale.model().TEMPI_RISPOSTA_EROGAZIONE.TEMPO_MEDIO_RISPOSTA,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getConfigurazioneGeneraleFieldConverter().toColumn(ConfigurazioneGenerale.model().RATE_LIMITING.TIPO_ERRORE,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getConfigurazioneGeneraleFieldConverter().toColumn(ConfigurazioneGenerale.model().RATE_LIMITING.TIPO_ERRORE_INCLUDI_DESCRIZIONE,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getConfigurazioneGeneraleFieldConverter().toColumn(ConfigurazioneGenerale.model().CACHE.CACHE,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getConfigurazioneGeneraleFieldConverter().toColumn(ConfigurazioneGenerale.model().CACHE.SIZE,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getConfigurazioneGeneraleFieldConverter().toColumn(ConfigurazioneGenerale.model().CACHE.ALGORITHM,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getConfigurazioneGeneraleFieldConverter().toColumn(ConfigurazioneGenerale.model().CACHE.IDLE_TIME,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getConfigurazioneGeneraleFieldConverter().toColumn(ConfigurazioneGenerale.model().CACHE.LIFE_TIME,false),"?");

		// Insert configurazioneGenerale
		org.openspcoop2.utils.jdbc.IKeyGeneratorObject keyGenerator = this.getConfigurazioneGeneraleFetch().getKeyGeneratorObject(ConfigurazioneGenerale.model());
		long id = jdbcUtilities.insertAndReturnGeneratedKey(sqlQueryObjectInsert, keyGenerator, jdbcProperties.isShowSql(),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(configurazioneGenerale.getControlloTraffico().getControlloMaxThreadsEnabled(),ConfigurazioneGenerale.model().CONTROLLO_TRAFFICO.CONTROLLO_MAX_THREADS_ENABLED.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(configurazioneGenerale.getControlloTraffico().getControlloMaxThreadsWarningOnly(),ConfigurazioneGenerale.model().CONTROLLO_TRAFFICO.CONTROLLO_MAX_THREADS_WARNING_ONLY.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(configurazioneGenerale.getControlloTraffico().getControlloMaxThreadsSoglia(),ConfigurazioneGenerale.model().CONTROLLO_TRAFFICO.CONTROLLO_MAX_THREADS_SOGLIA.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(configurazioneGenerale.getControlloTraffico().getControlloMaxThreadsTipoErrore(),ConfigurazioneGenerale.model().CONTROLLO_TRAFFICO.CONTROLLO_MAX_THREADS_TIPO_ERRORE.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(configurazioneGenerale.getControlloTraffico().getControlloMaxThreadsTipoErroreIncludiDescrizione(),ConfigurazioneGenerale.model().CONTROLLO_TRAFFICO.CONTROLLO_MAX_THREADS_TIPO_ERRORE_INCLUDI_DESCRIZIONE.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(configurazioneGenerale.getControlloTraffico().getControlloCongestioneEnabled(),ConfigurazioneGenerale.model().CONTROLLO_TRAFFICO.CONTROLLO_CONGESTIONE_ENABLED.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(configurazioneGenerale.getControlloTraffico().getControlloCongestioneThreshold(),ConfigurazioneGenerale.model().CONTROLLO_TRAFFICO.CONTROLLO_CONGESTIONE_THRESHOLD.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(configurazioneGenerale.getTempiRispostaFruizione().getConnectionTimeout(),ConfigurazioneGenerale.model().TEMPI_RISPOSTA_FRUIZIONE.CONNECTION_TIMEOUT.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(configurazioneGenerale.getTempiRispostaFruizione().getReadTimeout(),ConfigurazioneGenerale.model().TEMPI_RISPOSTA_FRUIZIONE.READ_TIMEOUT.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(configurazioneGenerale.getTempiRispostaFruizione().getTempoMedioRisposta(),ConfigurazioneGenerale.model().TEMPI_RISPOSTA_FRUIZIONE.TEMPO_MEDIO_RISPOSTA.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(configurazioneGenerale.getTempiRispostaErogazione().getConnectionTimeout(),ConfigurazioneGenerale.model().TEMPI_RISPOSTA_EROGAZIONE.CONNECTION_TIMEOUT.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(configurazioneGenerale.getTempiRispostaErogazione().getReadTimeout(),ConfigurazioneGenerale.model().TEMPI_RISPOSTA_EROGAZIONE.READ_TIMEOUT.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(configurazioneGenerale.getTempiRispostaErogazione().getTempoMedioRisposta(),ConfigurazioneGenerale.model().TEMPI_RISPOSTA_EROGAZIONE.TEMPO_MEDIO_RISPOSTA.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(configurazioneGenerale.getRateLimiting().getTipoErrore(),ConfigurazioneGenerale.model().RATE_LIMITING.TIPO_ERRORE.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(configurazioneGenerale.getRateLimiting().getTipoErroreIncludiDescrizione(),ConfigurazioneGenerale.model().RATE_LIMITING.TIPO_ERRORE_INCLUDI_DESCRIZIONE.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(configurazioneGenerale.getCache().getCache(),ConfigurazioneGenerale.model().CACHE.CACHE.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(configurazioneGenerale.getCache().getSize(),ConfigurazioneGenerale.model().CACHE.SIZE.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(configurazioneGenerale.getCache().getAlgorithm(),ConfigurazioneGenerale.model().CACHE.ALGORITHM.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(configurazioneGenerale.getCache().getIdleTime(),ConfigurazioneGenerale.model().CACHE.IDLE_TIME.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(configurazioneGenerale.getCache().getLifeTime(),ConfigurazioneGenerale.model().CACHE.LIFE_TIME.getFieldType())
		);
		configurazioneGenerale.setId(id);

		
	}

	@Override
	public void update(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, ConfigurazioneGenerale configurazioneGenerale, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		

	
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);
		
		ISQLQueryObject sqlQueryObjectInsert = sqlQueryObject.newSQLQueryObject();
		ISQLQueryObject sqlQueryObjectDelete = sqlQueryObjectInsert.newSQLQueryObject();
		ISQLQueryObject sqlQueryObjectGet = sqlQueryObjectDelete.newSQLQueryObject();
		ISQLQueryObject sqlQueryObjectUpdate = sqlQueryObjectGet.newSQLQueryObject();
		

		ConfigurazioneControlloTraffico configurazioneGenerale_controlloTraffico = configurazioneGenerale.getControlloTraffico();

		// Object configurazioneGenerale_controlloTraffico
		sqlQueryObjectUpdate.setANDLogicOperator(true);
		sqlQueryObjectUpdate.addUpdateTable(this.getConfigurazioneGeneraleFieldConverter().toTable(ConfigurazioneGenerale.model().CONTROLLO_TRAFFICO));
		boolean isUpdate = true;
		java.util.List<JDBCObject> lstObjects = new java.util.ArrayList<JDBCObject>();
		sqlQueryObjectUpdate.addUpdateField(this.getConfigurazioneGeneraleFieldConverter().toColumn(ConfigurazioneGenerale.model().CONTROLLO_TRAFFICO.CONTROLLO_MAX_THREADS_ENABLED,false), "?");
		lstObjects.add(new JDBCObject(configurazioneGenerale_controlloTraffico.getControlloMaxThreadsEnabled(), ConfigurazioneGenerale.model().CONTROLLO_TRAFFICO.CONTROLLO_MAX_THREADS_ENABLED.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getConfigurazioneGeneraleFieldConverter().toColumn(ConfigurazioneGenerale.model().CONTROLLO_TRAFFICO.CONTROLLO_MAX_THREADS_WARNING_ONLY,false), "?");
		lstObjects.add(new JDBCObject(configurazioneGenerale_controlloTraffico.getControlloMaxThreadsWarningOnly(), ConfigurazioneGenerale.model().CONTROLLO_TRAFFICO.CONTROLLO_MAX_THREADS_WARNING_ONLY.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getConfigurazioneGeneraleFieldConverter().toColumn(ConfigurazioneGenerale.model().CONTROLLO_TRAFFICO.CONTROLLO_MAX_THREADS_SOGLIA,false), "?");
		lstObjects.add(new JDBCObject(configurazioneGenerale_controlloTraffico.getControlloMaxThreadsSoglia(), ConfigurazioneGenerale.model().CONTROLLO_TRAFFICO.CONTROLLO_MAX_THREADS_SOGLIA.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getConfigurazioneGeneraleFieldConverter().toColumn(ConfigurazioneGenerale.model().CONTROLLO_TRAFFICO.CONTROLLO_MAX_THREADS_TIPO_ERRORE,false), "?");
		lstObjects.add(new JDBCObject(configurazioneGenerale_controlloTraffico.getControlloMaxThreadsTipoErrore(), ConfigurazioneGenerale.model().CONTROLLO_TRAFFICO.CONTROLLO_MAX_THREADS_TIPO_ERRORE.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getConfigurazioneGeneraleFieldConverter().toColumn(ConfigurazioneGenerale.model().CONTROLLO_TRAFFICO.CONTROLLO_MAX_THREADS_TIPO_ERRORE_INCLUDI_DESCRIZIONE,false), "?");
		lstObjects.add(new JDBCObject(configurazioneGenerale_controlloTraffico.getControlloMaxThreadsTipoErroreIncludiDescrizione(), ConfigurazioneGenerale.model().CONTROLLO_TRAFFICO.CONTROLLO_MAX_THREADS_TIPO_ERRORE_INCLUDI_DESCRIZIONE.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getConfigurazioneGeneraleFieldConverter().toColumn(ConfigurazioneGenerale.model().CONTROLLO_TRAFFICO.CONTROLLO_CONGESTIONE_ENABLED,false), "?");
		lstObjects.add(new JDBCObject(configurazioneGenerale_controlloTraffico.getControlloCongestioneEnabled(), ConfigurazioneGenerale.model().CONTROLLO_TRAFFICO.CONTROLLO_CONGESTIONE_ENABLED.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getConfigurazioneGeneraleFieldConverter().toColumn(ConfigurazioneGenerale.model().CONTROLLO_TRAFFICO.CONTROLLO_CONGESTIONE_THRESHOLD,false), "?");
		lstObjects.add(new JDBCObject(configurazioneGenerale_controlloTraffico.getControlloCongestioneThreshold(), ConfigurazioneGenerale.model().CONTROLLO_TRAFFICO.CONTROLLO_CONGESTIONE_THRESHOLD.getFieldType()));
		TempiRispostaFruizione configurazioneGenerale_tempiRispostaFruizione = configurazioneGenerale.getTempiRispostaFruizione();
		sqlQueryObjectUpdate.addUpdateField(this.getConfigurazioneGeneraleFieldConverter().toColumn(ConfigurazioneGenerale.model().TEMPI_RISPOSTA_FRUIZIONE.CONNECTION_TIMEOUT,false), "?");
		lstObjects.add(new JDBCObject(configurazioneGenerale_tempiRispostaFruizione.getConnectionTimeout(), ConfigurazioneGenerale.model().TEMPI_RISPOSTA_FRUIZIONE.CONNECTION_TIMEOUT.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getConfigurazioneGeneraleFieldConverter().toColumn(ConfigurazioneGenerale.model().TEMPI_RISPOSTA_FRUIZIONE.READ_TIMEOUT,false), "?");
		lstObjects.add(new JDBCObject(configurazioneGenerale_tempiRispostaFruizione.getReadTimeout(), ConfigurazioneGenerale.model().TEMPI_RISPOSTA_FRUIZIONE.READ_TIMEOUT.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getConfigurazioneGeneraleFieldConverter().toColumn(ConfigurazioneGenerale.model().TEMPI_RISPOSTA_FRUIZIONE.TEMPO_MEDIO_RISPOSTA,false), "?");
		lstObjects.add(new JDBCObject(configurazioneGenerale_tempiRispostaFruizione.getTempoMedioRisposta(), ConfigurazioneGenerale.model().TEMPI_RISPOSTA_FRUIZIONE.TEMPO_MEDIO_RISPOSTA.getFieldType()));
		TempiRispostaErogazione configurazioneGenerale_tempiRispostaErogazione = configurazioneGenerale.getTempiRispostaErogazione();
		sqlQueryObjectUpdate.addUpdateField(this.getConfigurazioneGeneraleFieldConverter().toColumn(ConfigurazioneGenerale.model().TEMPI_RISPOSTA_EROGAZIONE.CONNECTION_TIMEOUT,false), "?");
		lstObjects.add(new JDBCObject(configurazioneGenerale_tempiRispostaErogazione.getConnectionTimeout(), ConfigurazioneGenerale.model().TEMPI_RISPOSTA_EROGAZIONE.CONNECTION_TIMEOUT.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getConfigurazioneGeneraleFieldConverter().toColumn(ConfigurazioneGenerale.model().TEMPI_RISPOSTA_EROGAZIONE.READ_TIMEOUT,false), "?");
		lstObjects.add(new JDBCObject(configurazioneGenerale_tempiRispostaErogazione.getReadTimeout(), ConfigurazioneGenerale.model().TEMPI_RISPOSTA_EROGAZIONE.READ_TIMEOUT.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getConfigurazioneGeneraleFieldConverter().toColumn(ConfigurazioneGenerale.model().TEMPI_RISPOSTA_EROGAZIONE.TEMPO_MEDIO_RISPOSTA,false), "?");
		lstObjects.add(new JDBCObject(configurazioneGenerale_tempiRispostaErogazione.getTempoMedioRisposta(), ConfigurazioneGenerale.model().TEMPI_RISPOSTA_EROGAZIONE.TEMPO_MEDIO_RISPOSTA.getFieldType()));
		ConfigurazioneRateLimiting configurazioneGenerale_rateLimiting = configurazioneGenerale.getRateLimiting();
		sqlQueryObjectUpdate.addUpdateField(this.getConfigurazioneGeneraleFieldConverter().toColumn(ConfigurazioneGenerale.model().RATE_LIMITING.TIPO_ERRORE,false), "?");
		lstObjects.add(new JDBCObject(configurazioneGenerale_rateLimiting.getTipoErrore(), ConfigurazioneGenerale.model().RATE_LIMITING.TIPO_ERRORE.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getConfigurazioneGeneraleFieldConverter().toColumn(ConfigurazioneGenerale.model().RATE_LIMITING.TIPO_ERRORE_INCLUDI_DESCRIZIONE,false), "?");
		lstObjects.add(new JDBCObject(configurazioneGenerale_rateLimiting.getTipoErroreIncludiDescrizione(), ConfigurazioneGenerale.model().RATE_LIMITING.TIPO_ERRORE_INCLUDI_DESCRIZIONE.getFieldType()));
		Cache configurazioneGenerale_cache = configurazioneGenerale.getCache();
		sqlQueryObjectUpdate.addUpdateField(this.getConfigurazioneGeneraleFieldConverter().toColumn(ConfigurazioneGenerale.model().CACHE.CACHE,false), "?");
		lstObjects.add(new JDBCObject(configurazioneGenerale_cache.getCache(), ConfigurazioneGenerale.model().CACHE.CACHE.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getConfigurazioneGeneraleFieldConverter().toColumn(ConfigurazioneGenerale.model().CACHE.SIZE,false), "?");
		lstObjects.add(new JDBCObject(configurazioneGenerale_cache.getSize(), ConfigurazioneGenerale.model().CACHE.SIZE.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getConfigurazioneGeneraleFieldConverter().toColumn(ConfigurazioneGenerale.model().CACHE.ALGORITHM,false), "?");
		lstObjects.add(new JDBCObject(configurazioneGenerale_cache.getAlgorithm(), ConfigurazioneGenerale.model().CACHE.ALGORITHM.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getConfigurazioneGeneraleFieldConverter().toColumn(ConfigurazioneGenerale.model().CACHE.IDLE_TIME,false), "?");
		lstObjects.add(new JDBCObject(configurazioneGenerale_cache.getIdleTime(), ConfigurazioneGenerale.model().CACHE.IDLE_TIME.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getConfigurazioneGeneraleFieldConverter().toColumn(ConfigurazioneGenerale.model().CACHE.LIFE_TIME,false), "?");
		lstObjects.add(new JDBCObject(configurazioneGenerale_cache.getLifeTime(), ConfigurazioneGenerale.model().CACHE.LIFE_TIME.getFieldType()));

		if(isUpdate) {
			// Update configurazioneGenerale
			jdbcUtilities.executeUpdate(sqlQueryObjectUpdate.createSQLUpdate(), jdbcProperties.isShowSql(), 
				lstObjects.toArray(new JDBCObject[]{}));
		}


	}
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, UpdateField ... updateFields) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		
		JDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getConfigurazioneGeneraleFieldConverter().toTable(ConfigurazioneGenerale.model()), 
				this._getMapTableToPKColumn(), 
				this._getRootTablePrimaryKeyValues(jdbcProperties, log, connection, sqlQueryObject),
				this.getConfigurazioneGeneraleFieldConverter(), this, null, updateFields);
	}
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IExpression condition, UpdateField ... updateFields) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		
		JDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getConfigurazioneGeneraleFieldConverter().toTable(ConfigurazioneGenerale.model()), 
				this._getMapTableToPKColumn(), 
				this._getRootTablePrimaryKeyValues(jdbcProperties, log, connection, sqlQueryObject),
				this.getConfigurazioneGeneraleFieldConverter(), this, condition, updateFields);
	}
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, UpdateModel ... updateModels) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		
		JDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getConfigurazioneGeneraleFieldConverter().toTable(ConfigurazioneGenerale.model()), 
				this._getMapTableToPKColumn(), 
				this._getRootTablePrimaryKeyValues(jdbcProperties, log, connection, sqlQueryObject),
				this.getConfigurazioneGeneraleFieldConverter(), this, updateModels);
	}	
	
	
	@Override
	public void updateOrCreate(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, ConfigurazioneGenerale configurazioneGenerale, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotImplementedException,ServiceException,Exception {
	
		if(this.exists(jdbcProperties, log, connection, sqlQueryObject)) {
			this.update(jdbcProperties, log, connection, sqlQueryObject, configurazioneGenerale,idMappingResolutionBehaviour);
		} else {
			this.create(jdbcProperties, log, connection, sqlQueryObject, configurazioneGenerale,idMappingResolutionBehaviour);
		}
		
	}
	
	
	@Override
	public void delete(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, ConfigurazioneGenerale configurazioneGenerale) throws NotImplementedException,ServiceException,Exception {
		
		this._delete(jdbcProperties, log, connection, sqlQueryObject);
	}

	private void _delete(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject) throws NotImplementedException,ServiceException,Exception {
	
		// Used by internal elements (if exists)
		Long id = null;
		try{
			id = this.get(jdbcProperties, log, connection, sqlQueryObject,null).getId();		
		}catch(NotFoundException notFound){
			return;
		}
		
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);
		
		ISQLQueryObject sqlQueryObjectDelete = sqlQueryObject.newSQLQueryObject();
		

		// Object configurazioneGenerale_controlloTraffico_toDelete
		sqlQueryObjectDelete.setANDLogicOperator(true);
		sqlQueryObjectDelete.addDeleteTable(this.getConfigurazioneGeneraleFieldConverter().toTable(ConfigurazioneGenerale.model().CONTROLLO_TRAFFICO));

		// Delete configurazioneGenerale
		jdbcUtilities.execute(sqlQueryObjectDelete.createSQLDelete(), jdbcProperties.isShowSql());

	}

	


	// -- DB
	
	@Override
	public void deleteById(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId) throws ServiceException, NotImplementedException, Exception {
		this._delete(jdbcProperties, log, connection, sqlQueryObject); // long id ignored, object with single instance
	}
}
