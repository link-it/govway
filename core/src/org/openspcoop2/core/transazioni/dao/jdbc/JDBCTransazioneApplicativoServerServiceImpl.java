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
package org.openspcoop2.core.transazioni.dao.jdbc;

import java.sql.Connection;

import org.openspcoop2.utils.sql.ISQLQueryObject;

import org.slf4j.Logger;

import org.openspcoop2.generic_project.dao.jdbc.IJDBCServiceCRUDWithId;
import org.openspcoop2.core.transazioni.IdTransazioneApplicativoServer;
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

import org.openspcoop2.core.transazioni.TransazioneApplicativoServer;

/**     
 * JDBCTransazioneApplicativoServerServiceImpl
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JDBCTransazioneApplicativoServerServiceImpl extends JDBCTransazioneApplicativoServerServiceSearchImpl
	implements IJDBCServiceCRUDWithId<TransazioneApplicativoServer, IdTransazioneApplicativoServer, JDBCServiceManager> {

	@Override
	public void create(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, TransazioneApplicativoServer transazioneApplicativoServer, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotImplementedException,ServiceException,Exception {

		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);
		
		ISQLQueryObject sqlQueryObjectInsert = sqlQueryObject.newSQLQueryObject();
				


		// Object transazioneApplicativoServer
		sqlQueryObjectInsert.addInsertTable(this.getTransazioneApplicativoServerFieldConverter().toTable(TransazioneApplicativoServer.model()));
		sqlQueryObjectInsert.addInsertField(this.getTransazioneApplicativoServerFieldConverter().toColumn(TransazioneApplicativoServer.model().ID_TRANSAZIONE,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneApplicativoServerFieldConverter().toColumn(TransazioneApplicativoServer.model().SERVIZIO_APPLICATIVO_EROGATORE,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneApplicativoServerFieldConverter().toColumn(TransazioneApplicativoServer.model().CONNETTORE_NOME,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneApplicativoServerFieldConverter().toColumn(TransazioneApplicativoServer.model().DATA_REGISTRAZIONE,false),"?");
		// NONSERIALIZZATO SU DB sqlQueryObjectInsert.addInsertField(this.getTransazioneApplicativoServerFieldConverter().toColumn(TransazioneApplicativoServer.model().PROTOCOLLO,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneApplicativoServerFieldConverter().toColumn(TransazioneApplicativoServer.model().CONSEGNA_TERMINATA,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneApplicativoServerFieldConverter().toColumn(TransazioneApplicativoServer.model().DATA_MESSAGGIO_SCADUTO,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneApplicativoServerFieldConverter().toColumn(TransazioneApplicativoServer.model().DETTAGLIO_ESITO,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneApplicativoServerFieldConverter().toColumn(TransazioneApplicativoServer.model().CONSEGNA_TRASPARENTE,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneApplicativoServerFieldConverter().toColumn(TransazioneApplicativoServer.model().CONSEGNA_INTEGRATION_MANAGER,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneApplicativoServerFieldConverter().toColumn(TransazioneApplicativoServer.model().IDENTIFICATIVO_MESSAGGIO,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneApplicativoServerFieldConverter().toColumn(TransazioneApplicativoServer.model().DATA_ACCETTAZIONE_RICHIESTA,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneApplicativoServerFieldConverter().toColumn(TransazioneApplicativoServer.model().DATA_USCITA_RICHIESTA,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneApplicativoServerFieldConverter().toColumn(TransazioneApplicativoServer.model().DATA_ACCETTAZIONE_RISPOSTA,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneApplicativoServerFieldConverter().toColumn(TransazioneApplicativoServer.model().DATA_INGRESSO_RISPOSTA,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneApplicativoServerFieldConverter().toColumn(TransazioneApplicativoServer.model().RICHIESTA_USCITA_BYTES,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneApplicativoServerFieldConverter().toColumn(TransazioneApplicativoServer.model().RISPOSTA_INGRESSO_BYTES,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneApplicativoServerFieldConverter().toColumn(TransazioneApplicativoServer.model().LOCATION_CONNETTORE,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneApplicativoServerFieldConverter().toColumn(TransazioneApplicativoServer.model().CODICE_RISPOSTA,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneApplicativoServerFieldConverter().toColumn(TransazioneApplicativoServer.model().FAULT,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneApplicativoServerFieldConverter().toColumn(TransazioneApplicativoServer.model().FORMATO_FAULT,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneApplicativoServerFieldConverter().toColumn(TransazioneApplicativoServer.model().DATA_PRIMO_TENTATIVO,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneApplicativoServerFieldConverter().toColumn(TransazioneApplicativoServer.model().NUMERO_TENTATIVI,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneApplicativoServerFieldConverter().toColumn(TransazioneApplicativoServer.model().CLUSTER_ID_PRESA_IN_CARICO,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneApplicativoServerFieldConverter().toColumn(TransazioneApplicativoServer.model().CLUSTER_ID_CONSEGNA,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneApplicativoServerFieldConverter().toColumn(TransazioneApplicativoServer.model().DATA_ULTIMO_ERRORE,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneApplicativoServerFieldConverter().toColumn(TransazioneApplicativoServer.model().DETTAGLIO_ESITO_ULTIMO_ERRORE,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneApplicativoServerFieldConverter().toColumn(TransazioneApplicativoServer.model().CODICE_RISPOSTA_ULTIMO_ERRORE,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneApplicativoServerFieldConverter().toColumn(TransazioneApplicativoServer.model().ULTIMO_ERRORE,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneApplicativoServerFieldConverter().toColumn(TransazioneApplicativoServer.model().LOCATION_ULTIMO_ERRORE,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneApplicativoServerFieldConverter().toColumn(TransazioneApplicativoServer.model().CLUSTER_ID_ULTIMO_ERRORE,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneApplicativoServerFieldConverter().toColumn(TransazioneApplicativoServer.model().FAULT_ULTIMO_ERRORE,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneApplicativoServerFieldConverter().toColumn(TransazioneApplicativoServer.model().FORMATO_FAULT_ULTIMO_ERRORE,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneApplicativoServerFieldConverter().toColumn(TransazioneApplicativoServer.model().DATA_PRIMO_PRELIEVO_IM,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneApplicativoServerFieldConverter().toColumn(TransazioneApplicativoServer.model().DATA_PRELIEVO_IM,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneApplicativoServerFieldConverter().toColumn(TransazioneApplicativoServer.model().NUMERO_PRELIEVI_IM,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneApplicativoServerFieldConverter().toColumn(TransazioneApplicativoServer.model().DATA_ELIMINAZIONE_IM,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneApplicativoServerFieldConverter().toColumn(TransazioneApplicativoServer.model().CLUSTER_ID_PRELIEVO_IM,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneApplicativoServerFieldConverter().toColumn(TransazioneApplicativoServer.model().CLUSTER_ID_ELIMINAZIONE_IM,false),"?");

		// Insert transazioneApplicativoServer
		org.openspcoop2.utils.jdbc.IKeyGeneratorObject keyGenerator = this.getTransazioneApplicativoServerFetch().getKeyGeneratorObject(TransazioneApplicativoServer.model());
		long id = jdbcUtilities.insertAndReturnGeneratedKey(sqlQueryObjectInsert, keyGenerator, jdbcProperties.isShowSql(),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazioneApplicativoServer.getIdTransazione(),TransazioneApplicativoServer.model().ID_TRANSAZIONE.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazioneApplicativoServer.getServizioApplicativoErogatore(),TransazioneApplicativoServer.model().SERVIZIO_APPLICATIVO_EROGATORE.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazioneApplicativoServer.getConnettoreNome(),TransazioneApplicativoServer.model().CONNETTORE_NOME.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazioneApplicativoServer.getDataRegistrazione(),TransazioneApplicativoServer.model().DATA_REGISTRAZIONE.getFieldType()),
			// NONSERIALIZZATO SU DB new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazioneApplicativoServer.getProtocollo(),TransazioneApplicativoServer.model().PROTOCOLLO.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazioneApplicativoServer.getConsegnaTerminata(),TransazioneApplicativoServer.model().CONSEGNA_TERMINATA.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazioneApplicativoServer.getDataMessaggioScaduto(),TransazioneApplicativoServer.model().DATA_MESSAGGIO_SCADUTO.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazioneApplicativoServer.getDettaglioEsito(),TransazioneApplicativoServer.model().DETTAGLIO_ESITO.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazioneApplicativoServer.getConsegnaTrasparente(),TransazioneApplicativoServer.model().CONSEGNA_TRASPARENTE.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazioneApplicativoServer.getConsegnaIntegrationManager(),TransazioneApplicativoServer.model().CONSEGNA_INTEGRATION_MANAGER.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazioneApplicativoServer.getIdentificativoMessaggio(),TransazioneApplicativoServer.model().IDENTIFICATIVO_MESSAGGIO.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazioneApplicativoServer.getDataAccettazioneRichiesta(),TransazioneApplicativoServer.model().DATA_ACCETTAZIONE_RICHIESTA.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazioneApplicativoServer.getDataUscitaRichiesta(),TransazioneApplicativoServer.model().DATA_USCITA_RICHIESTA.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazioneApplicativoServer.getDataAccettazioneRisposta(),TransazioneApplicativoServer.model().DATA_ACCETTAZIONE_RISPOSTA.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazioneApplicativoServer.getDataIngressoRisposta(),TransazioneApplicativoServer.model().DATA_INGRESSO_RISPOSTA.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazioneApplicativoServer.getRichiestaUscitaBytes(),TransazioneApplicativoServer.model().RICHIESTA_USCITA_BYTES.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazioneApplicativoServer.getRispostaIngressoBytes(),TransazioneApplicativoServer.model().RISPOSTA_INGRESSO_BYTES.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazioneApplicativoServer.getLocationConnettore(),TransazioneApplicativoServer.model().LOCATION_CONNETTORE.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazioneApplicativoServer.getCodiceRisposta(),TransazioneApplicativoServer.model().CODICE_RISPOSTA.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazioneApplicativoServer.getFault(),TransazioneApplicativoServer.model().FAULT.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazioneApplicativoServer.getFormatoFault(),TransazioneApplicativoServer.model().FORMATO_FAULT.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazioneApplicativoServer.getDataPrimoTentativo(),TransazioneApplicativoServer.model().DATA_PRIMO_TENTATIVO.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazioneApplicativoServer.getNumeroTentativi(),TransazioneApplicativoServer.model().NUMERO_TENTATIVI.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazioneApplicativoServer.getClusterIdPresaInCarico(),TransazioneApplicativoServer.model().CLUSTER_ID_PRESA_IN_CARICO.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazioneApplicativoServer.getClusterIdConsegna(),TransazioneApplicativoServer.model().CLUSTER_ID_CONSEGNA.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazioneApplicativoServer.getDataUltimoErrore(),TransazioneApplicativoServer.model().DATA_ULTIMO_ERRORE.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazioneApplicativoServer.getDettaglioEsitoUltimoErrore(),TransazioneApplicativoServer.model().DETTAGLIO_ESITO_ULTIMO_ERRORE.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazioneApplicativoServer.getCodiceRispostaUltimoErrore(),TransazioneApplicativoServer.model().CODICE_RISPOSTA_ULTIMO_ERRORE.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazioneApplicativoServer.getUltimoErrore(),TransazioneApplicativoServer.model().ULTIMO_ERRORE.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazioneApplicativoServer.getLocationUltimoErrore(),TransazioneApplicativoServer.model().LOCATION_ULTIMO_ERRORE.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazioneApplicativoServer.getClusterIdUltimoErrore(),TransazioneApplicativoServer.model().CLUSTER_ID_ULTIMO_ERRORE.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazioneApplicativoServer.getFaultUltimoErrore(),TransazioneApplicativoServer.model().FAULT_ULTIMO_ERRORE.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazioneApplicativoServer.getFormatoFaultUltimoErrore(),TransazioneApplicativoServer.model().FORMATO_FAULT_ULTIMO_ERRORE.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazioneApplicativoServer.getDataPrimoPrelievoIm(),TransazioneApplicativoServer.model().DATA_PRIMO_PRELIEVO_IM.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazioneApplicativoServer.getDataPrelievoIm(),TransazioneApplicativoServer.model().DATA_PRELIEVO_IM.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazioneApplicativoServer.getNumeroPrelieviIm(),TransazioneApplicativoServer.model().NUMERO_PRELIEVI_IM.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazioneApplicativoServer.getDataEliminazioneIm(),TransazioneApplicativoServer.model().DATA_ELIMINAZIONE_IM.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazioneApplicativoServer.getClusterIdPrelievoIm(),TransazioneApplicativoServer.model().CLUSTER_ID_PRELIEVO_IM.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazioneApplicativoServer.getClusterIdEliminazioneIm(),TransazioneApplicativoServer.model().CLUSTER_ID_ELIMINAZIONE_IM.getFieldType())
		);
		transazioneApplicativoServer.setId(id);

		
	}

	@Override
	public void update(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdTransazioneApplicativoServer oldId, TransazioneApplicativoServer transazioneApplicativoServer, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		
		boolean efficente = true;
		
		if(efficente) {
		
			this._update(jdbcProperties, log, connection, sqlQueryObject, -1, transazioneApplicativoServer, idMappingResolutionBehaviour, oldId);
			
		}
		else {
		
			ISQLQueryObject sqlQueryObjectUpdate = sqlQueryObject.newSQLQueryObject();
			Long longIdByLogicId = this.findIdTransazioneApplicativoServer(jdbcProperties, log, connection, sqlQueryObjectUpdate.newSQLQueryObject(), oldId, true);
			Long tableId = transazioneApplicativoServer.getId();
			if(tableId != null && tableId.longValue() > 0) {
				if(tableId.longValue() != longIdByLogicId.longValue()) {
					throw new Exception("Ambiguous parameter: transazioneApplicativoServer.id ["+tableId+"] does not match logic id ["+longIdByLogicId+"]");
				}
			} else {
				tableId = longIdByLogicId;
				transazioneApplicativoServer.setId(tableId);
			}
			if(tableId==null || tableId<=0){
				throw new Exception("Retrieve tableId failed");
			}
	
			this.update(jdbcProperties, log, connection, sqlQueryObject, tableId, transazioneApplicativoServer, idMappingResolutionBehaviour);
			
		}
	}
	@Override
	public void update(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, TransazioneApplicativoServer transazioneApplicativoServer, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		this._update(jdbcProperties, log, connection, sqlQueryObject, tableId, transazioneApplicativoServer, idMappingResolutionBehaviour, null);
	}
	private void _update(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, 
			long tableId, TransazioneApplicativoServer transazioneApplicativoServer, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour,
			IdTransazioneApplicativoServer idLogico) throws NotFoundException, NotImplementedException, ServiceException, Exception {
	
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);
		
		ISQLQueryObject sqlQueryObjectInsert = sqlQueryObject.newSQLQueryObject();
		ISQLQueryObject sqlQueryObjectDelete = sqlQueryObjectInsert.newSQLQueryObject();
		ISQLQueryObject sqlQueryObjectGet = sqlQueryObjectDelete.newSQLQueryObject();
		ISQLQueryObject sqlQueryObjectUpdate = sqlQueryObjectGet.newSQLQueryObject();
		


		// Object transazioneApplicativoServer
		sqlQueryObjectUpdate.setANDLogicOperator(true);
		sqlQueryObjectUpdate.addUpdateTable(this.getTransazioneApplicativoServerFieldConverter().toTable(TransazioneApplicativoServer.model()));
		boolean isUpdate_transazioneApplicativoServer = true;
		java.util.List<JDBCObject> lstObjects_transazioneApplicativoServer = new java.util.ArrayList<JDBCObject>();
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneApplicativoServerFieldConverter().toColumn(TransazioneApplicativoServer.model().ID_TRANSAZIONE,false), "?");
		lstObjects_transazioneApplicativoServer.add(new JDBCObject(transazioneApplicativoServer.getIdTransazione(), TransazioneApplicativoServer.model().ID_TRANSAZIONE.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneApplicativoServerFieldConverter().toColumn(TransazioneApplicativoServer.model().SERVIZIO_APPLICATIVO_EROGATORE,false), "?");
		lstObjects_transazioneApplicativoServer.add(new JDBCObject(transazioneApplicativoServer.getServizioApplicativoErogatore(), TransazioneApplicativoServer.model().SERVIZIO_APPLICATIVO_EROGATORE.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneApplicativoServerFieldConverter().toColumn(TransazioneApplicativoServer.model().CONNETTORE_NOME,false), "?");
		lstObjects_transazioneApplicativoServer.add(new JDBCObject(transazioneApplicativoServer.getConnettoreNome(), TransazioneApplicativoServer.model().CONNETTORE_NOME.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneApplicativoServerFieldConverter().toColumn(TransazioneApplicativoServer.model().DATA_REGISTRAZIONE,false), "?");
		lstObjects_transazioneApplicativoServer.add(new JDBCObject(transazioneApplicativoServer.getDataRegistrazione(), TransazioneApplicativoServer.model().DATA_REGISTRAZIONE.getFieldType()));
		// NONSERIALIZZATO SU DB sqlQueryObjectUpdate.addUpdateField(this.getTransazioneApplicativoServerFieldConverter().toColumn(TransazioneApplicativoServer.model().PROTOCOLLO,false), "?");
		// NONSERIALIZZATO SU DB lstObjects_transazioneApplicativoServer.add(new JDBCObject(transazioneApplicativoServer.getProtocollo(), TransazioneApplicativoServer.model().PROTOCOLLO.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneApplicativoServerFieldConverter().toColumn(TransazioneApplicativoServer.model().CONSEGNA_TERMINATA,false), "?");
		lstObjects_transazioneApplicativoServer.add(new JDBCObject(transazioneApplicativoServer.getConsegnaTerminata(), TransazioneApplicativoServer.model().CONSEGNA_TERMINATA.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneApplicativoServerFieldConverter().toColumn(TransazioneApplicativoServer.model().DATA_MESSAGGIO_SCADUTO,false), "?");
		lstObjects_transazioneApplicativoServer.add(new JDBCObject(transazioneApplicativoServer.getDataMessaggioScaduto(), TransazioneApplicativoServer.model().DATA_MESSAGGIO_SCADUTO.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneApplicativoServerFieldConverter().toColumn(TransazioneApplicativoServer.model().DETTAGLIO_ESITO,false), "?");
		lstObjects_transazioneApplicativoServer.add(new JDBCObject(transazioneApplicativoServer.getDettaglioEsito(), TransazioneApplicativoServer.model().DETTAGLIO_ESITO.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneApplicativoServerFieldConverter().toColumn(TransazioneApplicativoServer.model().CONSEGNA_TRASPARENTE,false), "?");
		lstObjects_transazioneApplicativoServer.add(new JDBCObject(transazioneApplicativoServer.getConsegnaTrasparente(), TransazioneApplicativoServer.model().CONSEGNA_TRASPARENTE.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneApplicativoServerFieldConverter().toColumn(TransazioneApplicativoServer.model().CONSEGNA_INTEGRATION_MANAGER,false), "?");
		lstObjects_transazioneApplicativoServer.add(new JDBCObject(transazioneApplicativoServer.getConsegnaIntegrationManager(), TransazioneApplicativoServer.model().CONSEGNA_INTEGRATION_MANAGER.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneApplicativoServerFieldConverter().toColumn(TransazioneApplicativoServer.model().IDENTIFICATIVO_MESSAGGIO,false), "?");
		lstObjects_transazioneApplicativoServer.add(new JDBCObject(transazioneApplicativoServer.getIdentificativoMessaggio(), TransazioneApplicativoServer.model().IDENTIFICATIVO_MESSAGGIO.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneApplicativoServerFieldConverter().toColumn(TransazioneApplicativoServer.model().DATA_ACCETTAZIONE_RICHIESTA,false), "?");
		lstObjects_transazioneApplicativoServer.add(new JDBCObject(transazioneApplicativoServer.getDataAccettazioneRichiesta(), TransazioneApplicativoServer.model().DATA_ACCETTAZIONE_RICHIESTA.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneApplicativoServerFieldConverter().toColumn(TransazioneApplicativoServer.model().DATA_USCITA_RICHIESTA,false), "?");
		lstObjects_transazioneApplicativoServer.add(new JDBCObject(transazioneApplicativoServer.getDataUscitaRichiesta(), TransazioneApplicativoServer.model().DATA_USCITA_RICHIESTA.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneApplicativoServerFieldConverter().toColumn(TransazioneApplicativoServer.model().DATA_ACCETTAZIONE_RISPOSTA,false), "?");
		lstObjects_transazioneApplicativoServer.add(new JDBCObject(transazioneApplicativoServer.getDataAccettazioneRisposta(), TransazioneApplicativoServer.model().DATA_ACCETTAZIONE_RISPOSTA.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneApplicativoServerFieldConverter().toColumn(TransazioneApplicativoServer.model().DATA_INGRESSO_RISPOSTA,false), "?");
		lstObjects_transazioneApplicativoServer.add(new JDBCObject(transazioneApplicativoServer.getDataIngressoRisposta(), TransazioneApplicativoServer.model().DATA_INGRESSO_RISPOSTA.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneApplicativoServerFieldConverter().toColumn(TransazioneApplicativoServer.model().RICHIESTA_USCITA_BYTES,false), "?");
		lstObjects_transazioneApplicativoServer.add(new JDBCObject(transazioneApplicativoServer.getRichiestaUscitaBytes(), TransazioneApplicativoServer.model().RICHIESTA_USCITA_BYTES.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneApplicativoServerFieldConverter().toColumn(TransazioneApplicativoServer.model().RISPOSTA_INGRESSO_BYTES,false), "?");
		lstObjects_transazioneApplicativoServer.add(new JDBCObject(transazioneApplicativoServer.getRispostaIngressoBytes(), TransazioneApplicativoServer.model().RISPOSTA_INGRESSO_BYTES.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneApplicativoServerFieldConverter().toColumn(TransazioneApplicativoServer.model().LOCATION_CONNETTORE,false), "?");
		lstObjects_transazioneApplicativoServer.add(new JDBCObject(transazioneApplicativoServer.getLocationConnettore(), TransazioneApplicativoServer.model().LOCATION_CONNETTORE.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneApplicativoServerFieldConverter().toColumn(TransazioneApplicativoServer.model().CODICE_RISPOSTA,false), "?");
		lstObjects_transazioneApplicativoServer.add(new JDBCObject(transazioneApplicativoServer.getCodiceRisposta(), TransazioneApplicativoServer.model().CODICE_RISPOSTA.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneApplicativoServerFieldConverter().toColumn(TransazioneApplicativoServer.model().FAULT,false), "?");
		lstObjects_transazioneApplicativoServer.add(new JDBCObject(transazioneApplicativoServer.getFault(), TransazioneApplicativoServer.model().FAULT.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneApplicativoServerFieldConverter().toColumn(TransazioneApplicativoServer.model().FORMATO_FAULT,false), "?");
		lstObjects_transazioneApplicativoServer.add(new JDBCObject(transazioneApplicativoServer.getFormatoFault(), TransazioneApplicativoServer.model().FORMATO_FAULT.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneApplicativoServerFieldConverter().toColumn(TransazioneApplicativoServer.model().DATA_PRIMO_TENTATIVO,false), "?");
		lstObjects_transazioneApplicativoServer.add(new JDBCObject(transazioneApplicativoServer.getDataPrimoTentativo(), TransazioneApplicativoServer.model().DATA_PRIMO_TENTATIVO.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneApplicativoServerFieldConverter().toColumn(TransazioneApplicativoServer.model().NUMERO_TENTATIVI,false), "?");
		lstObjects_transazioneApplicativoServer.add(new JDBCObject(transazioneApplicativoServer.getNumeroTentativi(), TransazioneApplicativoServer.model().NUMERO_TENTATIVI.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneApplicativoServerFieldConverter().toColumn(TransazioneApplicativoServer.model().CLUSTER_ID_PRESA_IN_CARICO,false), "?");
		lstObjects_transazioneApplicativoServer.add(new JDBCObject(transazioneApplicativoServer.getClusterIdPresaInCarico(), TransazioneApplicativoServer.model().CLUSTER_ID_PRESA_IN_CARICO.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneApplicativoServerFieldConverter().toColumn(TransazioneApplicativoServer.model().CLUSTER_ID_CONSEGNA,false), "?");
		lstObjects_transazioneApplicativoServer.add(new JDBCObject(transazioneApplicativoServer.getClusterIdConsegna(), TransazioneApplicativoServer.model().CLUSTER_ID_CONSEGNA.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneApplicativoServerFieldConverter().toColumn(TransazioneApplicativoServer.model().DATA_ULTIMO_ERRORE,false), "?");
		lstObjects_transazioneApplicativoServer.add(new JDBCObject(transazioneApplicativoServer.getDataUltimoErrore(), TransazioneApplicativoServer.model().DATA_ULTIMO_ERRORE.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneApplicativoServerFieldConverter().toColumn(TransazioneApplicativoServer.model().DETTAGLIO_ESITO_ULTIMO_ERRORE,false), "?");
		lstObjects_transazioneApplicativoServer.add(new JDBCObject(transazioneApplicativoServer.getDettaglioEsitoUltimoErrore(), TransazioneApplicativoServer.model().DETTAGLIO_ESITO_ULTIMO_ERRORE.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneApplicativoServerFieldConverter().toColumn(TransazioneApplicativoServer.model().CODICE_RISPOSTA_ULTIMO_ERRORE,false), "?");
		lstObjects_transazioneApplicativoServer.add(new JDBCObject(transazioneApplicativoServer.getCodiceRispostaUltimoErrore(), TransazioneApplicativoServer.model().CODICE_RISPOSTA_ULTIMO_ERRORE.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneApplicativoServerFieldConverter().toColumn(TransazioneApplicativoServer.model().ULTIMO_ERRORE,false), "?");
		lstObjects_transazioneApplicativoServer.add(new JDBCObject(transazioneApplicativoServer.getUltimoErrore(), TransazioneApplicativoServer.model().ULTIMO_ERRORE.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneApplicativoServerFieldConverter().toColumn(TransazioneApplicativoServer.model().LOCATION_ULTIMO_ERRORE,false), "?");
		lstObjects_transazioneApplicativoServer.add(new JDBCObject(transazioneApplicativoServer.getLocationUltimoErrore(), TransazioneApplicativoServer.model().LOCATION_ULTIMO_ERRORE.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneApplicativoServerFieldConverter().toColumn(TransazioneApplicativoServer.model().CLUSTER_ID_ULTIMO_ERRORE,false), "?");
		lstObjects_transazioneApplicativoServer.add(new JDBCObject(transazioneApplicativoServer.getClusterIdUltimoErrore(), TransazioneApplicativoServer.model().CLUSTER_ID_ULTIMO_ERRORE.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneApplicativoServerFieldConverter().toColumn(TransazioneApplicativoServer.model().FAULT_ULTIMO_ERRORE,false), "?");
		lstObjects_transazioneApplicativoServer.add(new JDBCObject(transazioneApplicativoServer.getFaultUltimoErrore(), TransazioneApplicativoServer.model().FAULT_ULTIMO_ERRORE.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneApplicativoServerFieldConverter().toColumn(TransazioneApplicativoServer.model().FORMATO_FAULT_ULTIMO_ERRORE,false), "?");
		lstObjects_transazioneApplicativoServer.add(new JDBCObject(transazioneApplicativoServer.getFormatoFaultUltimoErrore(), TransazioneApplicativoServer.model().FORMATO_FAULT_ULTIMO_ERRORE.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneApplicativoServerFieldConverter().toColumn(TransazioneApplicativoServer.model().DATA_PRIMO_PRELIEVO_IM,false), "?");
		lstObjects_transazioneApplicativoServer.add(new JDBCObject(transazioneApplicativoServer.getDataPrimoPrelievoIm(), TransazioneApplicativoServer.model().DATA_PRIMO_PRELIEVO_IM.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneApplicativoServerFieldConverter().toColumn(TransazioneApplicativoServer.model().DATA_PRELIEVO_IM,false), "?");
		lstObjects_transazioneApplicativoServer.add(new JDBCObject(transazioneApplicativoServer.getDataPrelievoIm(), TransazioneApplicativoServer.model().DATA_PRELIEVO_IM.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneApplicativoServerFieldConverter().toColumn(TransazioneApplicativoServer.model().NUMERO_PRELIEVI_IM,false), "?");
		lstObjects_transazioneApplicativoServer.add(new JDBCObject(transazioneApplicativoServer.getNumeroPrelieviIm(), TransazioneApplicativoServer.model().NUMERO_PRELIEVI_IM.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneApplicativoServerFieldConverter().toColumn(TransazioneApplicativoServer.model().DATA_ELIMINAZIONE_IM,false), "?");
		lstObjects_transazioneApplicativoServer.add(new JDBCObject(transazioneApplicativoServer.getDataEliminazioneIm(), TransazioneApplicativoServer.model().DATA_ELIMINAZIONE_IM.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneApplicativoServerFieldConverter().toColumn(TransazioneApplicativoServer.model().CLUSTER_ID_PRELIEVO_IM,false), "?");
		lstObjects_transazioneApplicativoServer.add(new JDBCObject(transazioneApplicativoServer.getClusterIdPrelievoIm(), TransazioneApplicativoServer.model().CLUSTER_ID_PRELIEVO_IM.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneApplicativoServerFieldConverter().toColumn(TransazioneApplicativoServer.model().CLUSTER_ID_ELIMINAZIONE_IM,false), "?");
		lstObjects_transazioneApplicativoServer.add(new JDBCObject(transazioneApplicativoServer.getClusterIdEliminazioneIm(), TransazioneApplicativoServer.model().CLUSTER_ID_ELIMINAZIONE_IM.getFieldType()));
		if(idLogico!=null) {
			sqlQueryObjectUpdate.addWhereCondition(this.getTransazioneApplicativoServerFieldConverter().toColumn(TransazioneApplicativoServer.model().ID_TRANSAZIONE,false)+ "=?");
			sqlQueryObjectUpdate.addWhereCondition(this.getTransazioneApplicativoServerFieldConverter().toColumn(TransazioneApplicativoServer.model().SERVIZIO_APPLICATIVO_EROGATORE,false)+ "=?");
			sqlQueryObjectUpdate.setANDLogicOperator(true);
			lstObjects_transazioneApplicativoServer.add(new JDBCObject(idLogico.getIdTransazione(), String.class));
			lstObjects_transazioneApplicativoServer.add(new JDBCObject(idLogico.getServizioApplicativoErogatore(), String.class));
		}
		else {
			sqlQueryObjectUpdate.addWhereCondition("id=?");
			lstObjects_transazioneApplicativoServer.add(new JDBCObject(tableId, Long.class));
		}

		if(isUpdate_transazioneApplicativoServer) {
			// Update transazioneApplicativoServer
			jdbcUtilities.executeUpdate(sqlQueryObjectUpdate.createSQLUpdate(), jdbcProperties.isShowSql(), 
				lstObjects_transazioneApplicativoServer.toArray(new JDBCObject[]{}));
		}


	}
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdTransazioneApplicativoServer id, UpdateField ... updateFields) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		
		JDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getTransazioneApplicativoServerFieldConverter().toTable(TransazioneApplicativoServer.model()), 
				this._getMapTableToPKColumn(), 
				this._getRootTablePrimaryKeyValues(jdbcProperties, log, connection, sqlQueryObject, id),
				this.getTransazioneApplicativoServerFieldConverter(), this, null, updateFields);
	}
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdTransazioneApplicativoServer id, IExpression condition, UpdateField ... updateFields) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		
		JDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getTransazioneApplicativoServerFieldConverter().toTable(TransazioneApplicativoServer.model()), 
				this._getMapTableToPKColumn(), 
				this._getRootTablePrimaryKeyValues(jdbcProperties, log, connection, sqlQueryObject, id),
				this.getTransazioneApplicativoServerFieldConverter(), this, condition, updateFields);
	}
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdTransazioneApplicativoServer id, UpdateModel ... updateModels) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		
		JDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getTransazioneApplicativoServerFieldConverter().toTable(TransazioneApplicativoServer.model()), 
				this._getMapTableToPKColumn(), 
				this._getRootTablePrimaryKeyValues(jdbcProperties, log, connection, sqlQueryObject, id),
				this.getTransazioneApplicativoServerFieldConverter(), this, updateModels);
	}	
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, UpdateField ... updateFields) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		java.util.List<Object> ids = new java.util.ArrayList<Object>();
		ids.add(tableId);
		JDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getTransazioneApplicativoServerFieldConverter().toTable(TransazioneApplicativoServer.model()), 
				this._getMapTableToPKColumn(), 
				ids,
				this.getTransazioneApplicativoServerFieldConverter(), this, null, updateFields);
	}
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, IExpression condition, UpdateField ... updateFields) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		java.util.List<Object> ids = new java.util.ArrayList<Object>();
		ids.add(tableId);
		JDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getTransazioneApplicativoServerFieldConverter().toTable(TransazioneApplicativoServer.model()), 
				this._getMapTableToPKColumn(), 
				ids,
				this.getTransazioneApplicativoServerFieldConverter(), this, condition, updateFields);
	}
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, UpdateModel ... updateModels) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		java.util.List<Object> ids = new java.util.ArrayList<Object>();
		ids.add(tableId);
		JDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getTransazioneApplicativoServerFieldConverter().toTable(TransazioneApplicativoServer.model()), 
				this._getMapTableToPKColumn(), 
				ids,
				this.getTransazioneApplicativoServerFieldConverter(), this, updateModels);
	}
	
	@Override
	public void updateOrCreate(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdTransazioneApplicativoServer oldId, TransazioneApplicativoServer transazioneApplicativoServer, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotImplementedException,ServiceException,Exception {
	
		if(this.exists(jdbcProperties, log, connection, sqlQueryObject, oldId)) {
			this.update(jdbcProperties, log, connection, sqlQueryObject, oldId, transazioneApplicativoServer,idMappingResolutionBehaviour);
		} else {
			this.create(jdbcProperties, log, connection, sqlQueryObject, transazioneApplicativoServer,idMappingResolutionBehaviour);
		}
		
	}
	
	@Override
	public void updateOrCreate(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, TransazioneApplicativoServer transazioneApplicativoServer, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotImplementedException,ServiceException,Exception {
		if(this.exists(jdbcProperties, log, connection, sqlQueryObject, tableId)) {
			this.update(jdbcProperties, log, connection, sqlQueryObject, tableId, transazioneApplicativoServer,idMappingResolutionBehaviour);
		} else {
			this.create(jdbcProperties, log, connection, sqlQueryObject, transazioneApplicativoServer,idMappingResolutionBehaviour);
		}
	}
	
	@Override
	public void delete(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, TransazioneApplicativoServer transazioneApplicativoServer) throws NotImplementedException,ServiceException,Exception {
		
		
		Long longId = null;
		if( (transazioneApplicativoServer.getId()!=null) && (transazioneApplicativoServer.getId()>0) ){
			longId = transazioneApplicativoServer.getId();
		}
		else{
			IdTransazioneApplicativoServer idTransazioneApplicativoServer = this.convertToId(jdbcProperties,log,connection,sqlQueryObject,transazioneApplicativoServer);
			longId = this.findIdTransazioneApplicativoServer(jdbcProperties,log,connection,sqlQueryObject,idTransazioneApplicativoServer,false);
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
		

		// Object transazioneApplicativoServer
		sqlQueryObjectDelete.setANDLogicOperator(true);
		sqlQueryObjectDelete.addDeleteTable(this.getTransazioneApplicativoServerFieldConverter().toTable(TransazioneApplicativoServer.model()));
		if(id != null)
			sqlQueryObjectDelete.addWhereCondition("id=?");

		// Delete transazioneApplicativoServer
		jdbcUtilities.execute(sqlQueryObjectDelete.createSQLDelete(), jdbcProperties.isShowSql(), 
			new JDBCObject(id,Long.class));

	}

	@Override
	public void deleteById(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdTransazioneApplicativoServer idTransazioneApplicativoServer) throws NotImplementedException,ServiceException,Exception {

		Long id = null;
		try{
			id = this.findIdTransazioneApplicativoServer(jdbcProperties, log, connection, sqlQueryObject, idTransazioneApplicativoServer, true);
		}catch(NotFoundException notFound){
			return;
		}
		this._delete(jdbcProperties, log, connection, sqlQueryObject, id);
		
	}
	
	@Override
	public NonNegativeNumber deleteAll(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject) throws NotImplementedException,ServiceException,Exception {
		
		return this.deleteAll(jdbcProperties, log, connection, sqlQueryObject, new JDBCExpression(this.getTransazioneApplicativoServerFieldConverter()));

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
