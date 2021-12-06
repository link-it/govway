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
import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.sql.ISQLQueryObject;

import org.slf4j.Logger;

import org.openspcoop2.generic_project.dao.jdbc.IJDBCServiceCRUDWithId;
import java.lang.String;
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

import org.openspcoop2.core.transazioni.Transazione;
import org.openspcoop2.core.transazioni.TransazioneExtendedInfo;

/**     
 * JDBCTransazioneServiceImpl
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JDBCTransazioneServiceImpl extends JDBCTransazioneServiceSearchImpl
	implements IJDBCServiceCRUDWithId<Transazione, String, JDBCServiceManager> {

	@Override
	public void create(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, Transazione transazione, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotImplementedException,ServiceException,Exception {

		if(TipiDatabase.POSTGRESQL.equals(jdbcProperties.getDatabase())){
			if(transazione.getFaultCooperazione()!=null && org.openspcoop2.utils.jdbc.PostgreSQLUtilities.containsNullByteSequence(transazione.getFaultCooperazione())) {
				transazione.setFaultCooperazione(org.openspcoop2.utils.jdbc.PostgreSQLUtilities.normalizeString(transazione.getFaultCooperazione()));
			}
			if(transazione.getFaultIntegrazione()!=null && org.openspcoop2.utils.jdbc.PostgreSQLUtilities.containsNullByteSequence(transazione.getFaultIntegrazione())) {
				transazione.setFaultIntegrazione(org.openspcoop2.utils.jdbc.PostgreSQLUtilities.normalizeString(transazione.getFaultIntegrazione()));
			}
			if(transazione.getCredenziali()!=null && org.openspcoop2.utils.jdbc.PostgreSQLUtilities.containsNullByteSequence(transazione.getCredenziali())) {
				transazione.setCredenziali(org.openspcoop2.utils.jdbc.PostgreSQLUtilities.normalizeString(transazione.getCredenziali()));
			}
		}
			
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);
		
		ISQLQueryObject sqlQueryObjectInsert = sqlQueryObject.newSQLQueryObject();
				


		// Object transazione
		sqlQueryObjectInsert.addInsertTable(this.getTransazioneFieldConverter().toTable(Transazione.model()));
		sqlQueryObjectInsert.addInsertField(this.getTransazioneFieldConverter().toColumn(Transazione.model().ID_TRANSAZIONE,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneFieldConverter().toColumn(Transazione.model().STATO,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneFieldConverter().toColumn(Transazione.model().RUOLO_TRANSAZIONE,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneFieldConverter().toColumn(Transazione.model().ESITO,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneFieldConverter().toColumn(Transazione.model().ESITO_SINCRONO,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneFieldConverter().toColumn(Transazione.model().CONSEGNE_MULTIPLE_IN_CORSO,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneFieldConverter().toColumn(Transazione.model().ESITO_CONTESTO,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneFieldConverter().toColumn(Transazione.model().PROTOCOLLO,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneFieldConverter().toColumn(Transazione.model().TIPO_RICHIESTA,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneFieldConverter().toColumn(Transazione.model().CODICE_RISPOSTA_INGRESSO,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneFieldConverter().toColumn(Transazione.model().CODICE_RISPOSTA_USCITA,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneFieldConverter().toColumn(Transazione.model().DATA_ACCETTAZIONE_RICHIESTA,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneFieldConverter().toColumn(Transazione.model().DATA_INGRESSO_RICHIESTA,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneFieldConverter().toColumn(Transazione.model().DATA_USCITA_RICHIESTA,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneFieldConverter().toColumn(Transazione.model().DATA_ACCETTAZIONE_RISPOSTA,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneFieldConverter().toColumn(Transazione.model().DATA_INGRESSO_RISPOSTA,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneFieldConverter().toColumn(Transazione.model().DATA_USCITA_RISPOSTA,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneFieldConverter().toColumn(Transazione.model().RICHIESTA_INGRESSO_BYTES,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneFieldConverter().toColumn(Transazione.model().RICHIESTA_USCITA_BYTES,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneFieldConverter().toColumn(Transazione.model().RISPOSTA_INGRESSO_BYTES,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneFieldConverter().toColumn(Transazione.model().RISPOSTA_USCITA_BYTES,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneFieldConverter().toColumn(Transazione.model().PDD_CODICE,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneFieldConverter().toColumn(Transazione.model().PDD_TIPO_SOGGETTO,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneFieldConverter().toColumn(Transazione.model().PDD_NOME_SOGGETTO,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneFieldConverter().toColumn(Transazione.model().PDD_RUOLO,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneFieldConverter().toColumn(Transazione.model().FAULT_INTEGRAZIONE,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneFieldConverter().toColumn(Transazione.model().FORMATO_FAULT_INTEGRAZIONE,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneFieldConverter().toColumn(Transazione.model().FAULT_COOPERAZIONE,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneFieldConverter().toColumn(Transazione.model().FORMATO_FAULT_COOPERAZIONE,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneFieldConverter().toColumn(Transazione.model().TIPO_SOGGETTO_FRUITORE,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneFieldConverter().toColumn(Transazione.model().NOME_SOGGETTO_FRUITORE,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneFieldConverter().toColumn(Transazione.model().IDPORTA_SOGGETTO_FRUITORE,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneFieldConverter().toColumn(Transazione.model().INDIRIZZO_SOGGETTO_FRUITORE,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneFieldConverter().toColumn(Transazione.model().TIPO_SOGGETTO_EROGATORE,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneFieldConverter().toColumn(Transazione.model().NOME_SOGGETTO_EROGATORE,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneFieldConverter().toColumn(Transazione.model().IDPORTA_SOGGETTO_EROGATORE,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneFieldConverter().toColumn(Transazione.model().INDIRIZZO_SOGGETTO_EROGATORE,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneFieldConverter().toColumn(Transazione.model().ID_MESSAGGIO_RICHIESTA,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneFieldConverter().toColumn(Transazione.model().ID_MESSAGGIO_RISPOSTA,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneFieldConverter().toColumn(Transazione.model().DATA_ID_MSG_RICHIESTA,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneFieldConverter().toColumn(Transazione.model().DATA_ID_MSG_RISPOSTA,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneFieldConverter().toColumn(Transazione.model().PROFILO_COLLABORAZIONE_OP_2,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneFieldConverter().toColumn(Transazione.model().PROFILO_COLLABORAZIONE_PROT,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneFieldConverter().toColumn(Transazione.model().ID_COLLABORAZIONE,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneFieldConverter().toColumn(Transazione.model().URI_ACCORDO_SERVIZIO,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneFieldConverter().toColumn(Transazione.model().TIPO_SERVIZIO,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneFieldConverter().toColumn(Transazione.model().NOME_SERVIZIO,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneFieldConverter().toColumn(Transazione.model().VERSIONE_SERVIZIO,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneFieldConverter().toColumn(Transazione.model().AZIONE,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneFieldConverter().toColumn(Transazione.model().ID_ASINCRONO,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneFieldConverter().toColumn(Transazione.model().TIPO_SERVIZIO_CORRELATO,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneFieldConverter().toColumn(Transazione.model().NOME_SERVIZIO_CORRELATO,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneFieldConverter().toColumn(Transazione.model().HEADER_PROTOCOLLO_RICHIESTA,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneFieldConverter().toColumn(Transazione.model().DIGEST_RICHIESTA,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneFieldConverter().toColumn(Transazione.model().PROTOCOLLO_EXT_INFO_RICHIESTA,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneFieldConverter().toColumn(Transazione.model().HEADER_PROTOCOLLO_RISPOSTA,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneFieldConverter().toColumn(Transazione.model().DIGEST_RISPOSTA,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneFieldConverter().toColumn(Transazione.model().PROTOCOLLO_EXT_INFO_RISPOSTA,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneFieldConverter().toColumn(Transazione.model().TRACCIA_RICHIESTA,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneFieldConverter().toColumn(Transazione.model().TRACCIA_RISPOSTA,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneFieldConverter().toColumn(Transazione.model().DIAGNOSTICI,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneFieldConverter().toColumn(Transazione.model().DIAGNOSTICI_LIST_1,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneFieldConverter().toColumn(Transazione.model().DIAGNOSTICI_LIST_2,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneFieldConverter().toColumn(Transazione.model().DIAGNOSTICI_LIST_EXT,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneFieldConverter().toColumn(Transazione.model().DIAGNOSTICI_EXT,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneFieldConverter().toColumn(Transazione.model().ID_CORRELAZIONE_APPLICATIVA,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneFieldConverter().toColumn(Transazione.model().ID_CORRELAZIONE_APPLICATIVA_RISPOSTA,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneFieldConverter().toColumn(Transazione.model().SERVIZIO_APPLICATIVO_FRUITORE,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneFieldConverter().toColumn(Transazione.model().SERVIZIO_APPLICATIVO_EROGATORE,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneFieldConverter().toColumn(Transazione.model().OPERAZIONE_IM,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneFieldConverter().toColumn(Transazione.model().LOCATION_RICHIESTA,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneFieldConverter().toColumn(Transazione.model().LOCATION_RISPOSTA,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneFieldConverter().toColumn(Transazione.model().NOME_PORTA,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneFieldConverter().toColumn(Transazione.model().CREDENZIALI,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneFieldConverter().toColumn(Transazione.model().LOCATION_CONNETTORE,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneFieldConverter().toColumn(Transazione.model().URL_INVOCAZIONE,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneFieldConverter().toColumn(Transazione.model().TRASPORTO_MITTENTE,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneFieldConverter().toColumn(Transazione.model().TOKEN_ISSUER,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneFieldConverter().toColumn(Transazione.model().TOKEN_CLIENT_ID,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneFieldConverter().toColumn(Transazione.model().TOKEN_SUBJECT,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneFieldConverter().toColumn(Transazione.model().TOKEN_USERNAME,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneFieldConverter().toColumn(Transazione.model().TOKEN_MAIL,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneFieldConverter().toColumn(Transazione.model().TOKEN_INFO,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneFieldConverter().toColumn(Transazione.model().TEMPI_ELABORAZIONE,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneFieldConverter().toColumn(Transazione.model().DUPLICATI_RICHIESTA,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneFieldConverter().toColumn(Transazione.model().DUPLICATI_RISPOSTA,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneFieldConverter().toColumn(Transazione.model().CLUSTER_ID,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneFieldConverter().toColumn(Transazione.model().SOCKET_CLIENT_ADDRESS,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneFieldConverter().toColumn(Transazione.model().TRANSPORT_CLIENT_ADDRESS,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneFieldConverter().toColumn(Transazione.model().CLIENT_ADDRESS,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneFieldConverter().toColumn(Transazione.model().EVENTI_GESTIONE,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneFieldConverter().toColumn(Transazione.model().TIPO_API,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneFieldConverter().toColumn(Transazione.model().URI_API,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getTransazioneFieldConverter().toColumn(Transazione.model().GRUPPI,false),"?");
		if(transazione.sizeTransazioneExtendedInfoList()>0){
			for (TransazioneExtendedInfo transazioneExtedendInfo : transazione.getTransazioneExtendedInfoList()) {
				sqlQueryObjectInsert.addInsertField(transazioneExtedendInfo.getNome(),"?");
			}
		}
			
		// Insert transazione
		String insertSql = sqlQueryObjectInsert.createSQLInsert();
		List<JDBCObject> listaJDBCObject = new ArrayList<JDBCObject>();
		listaJDBCObject.add(new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazione.getIdTransazione(),Transazione.model().ID_TRANSAZIONE.getFieldType()) );
		listaJDBCObject.add(new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazione.getStato(),Transazione.model().STATO.getFieldType()) );
		listaJDBCObject.add(new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazione.getRuoloTransazione(),Transazione.model().RUOLO_TRANSAZIONE.getFieldType()) );
		listaJDBCObject.add(new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazione.getEsito(),Transazione.model().ESITO.getFieldType()) );
		listaJDBCObject.add(new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazione.getEsitoSincrono(),Transazione.model().ESITO_SINCRONO.getFieldType()) );
		listaJDBCObject.add(new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazione.getConsegneMultipleInCorso(),Transazione.model().CONSEGNE_MULTIPLE_IN_CORSO.getFieldType()) );
		listaJDBCObject.add(new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazione.getEsitoContesto(),Transazione.model().ESITO_CONTESTO.getFieldType()) );
		listaJDBCObject.add(new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazione.getProtocollo(),Transazione.model().PROTOCOLLO.getFieldType()) );
		listaJDBCObject.add(new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazione.getTipoRichiesta(),Transazione.model().TIPO_RICHIESTA.getFieldType()) );
		listaJDBCObject.add(new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazione.getCodiceRispostaIngresso(),Transazione.model().CODICE_RISPOSTA_INGRESSO.getFieldType()) );
		listaJDBCObject.add(new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazione.getCodiceRispostaUscita(),Transazione.model().CODICE_RISPOSTA_USCITA.getFieldType()) );
		listaJDBCObject.add(new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazione.getDataAccettazioneRichiesta(),Transazione.model().DATA_ACCETTAZIONE_RICHIESTA.getFieldType()) );
		listaJDBCObject.add(new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazione.getDataIngressoRichiesta(),Transazione.model().DATA_INGRESSO_RICHIESTA.getFieldType()) );
		listaJDBCObject.add(new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazione.getDataUscitaRichiesta(),Transazione.model().DATA_USCITA_RICHIESTA.getFieldType()) );
		listaJDBCObject.add(new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazione.getDataAccettazioneRisposta(),Transazione.model().DATA_ACCETTAZIONE_RISPOSTA.getFieldType()) );
		listaJDBCObject.add(new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazione.getDataIngressoRisposta(),Transazione.model().DATA_INGRESSO_RISPOSTA.getFieldType()) );
		listaJDBCObject.add(new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazione.getDataUscitaRisposta(),Transazione.model().DATA_USCITA_RISPOSTA.getFieldType()) );
		listaJDBCObject.add(new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazione.getRichiestaIngressoBytes(),Transazione.model().RICHIESTA_INGRESSO_BYTES.getFieldType()) );
		listaJDBCObject.add(new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazione.getRichiestaUscitaBytes(),Transazione.model().RICHIESTA_USCITA_BYTES.getFieldType()) );
		listaJDBCObject.add(new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazione.getRispostaIngressoBytes(),Transazione.model().RISPOSTA_INGRESSO_BYTES.getFieldType()) );
		listaJDBCObject.add(new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazione.getRispostaUscitaBytes(),Transazione.model().RISPOSTA_USCITA_BYTES.getFieldType()) );
		listaJDBCObject.add(new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazione.getPddCodice(),Transazione.model().PDD_CODICE.getFieldType()) );
		listaJDBCObject.add(new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazione.getPddTipoSoggetto(),Transazione.model().PDD_TIPO_SOGGETTO.getFieldType()) );
		listaJDBCObject.add(new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazione.getPddNomeSoggetto(),Transazione.model().PDD_NOME_SOGGETTO.getFieldType()) );
		listaJDBCObject.add(new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazione.getPddRuolo(),Transazione.model().PDD_RUOLO.getFieldType()) );
		listaJDBCObject.add(new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazione.getFaultIntegrazione(),Transazione.model().FAULT_INTEGRAZIONE.getFieldType()) );
		listaJDBCObject.add(new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazione.getFormatoFaultIntegrazione(),Transazione.model().FORMATO_FAULT_INTEGRAZIONE.getFieldType()) );
		listaJDBCObject.add(new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazione.getFaultCooperazione(),Transazione.model().FAULT_COOPERAZIONE.getFieldType()) );
		listaJDBCObject.add(new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazione.getFormatoFaultCooperazione(),Transazione.model().FORMATO_FAULT_COOPERAZIONE.getFieldType()) );
		listaJDBCObject.add(new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazione.getTipoSoggettoFruitore(),Transazione.model().TIPO_SOGGETTO_FRUITORE.getFieldType()) );
		listaJDBCObject.add(new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazione.getNomeSoggettoFruitore(),Transazione.model().NOME_SOGGETTO_FRUITORE.getFieldType()) );
		listaJDBCObject.add(new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazione.getIdportaSoggettoFruitore(),Transazione.model().IDPORTA_SOGGETTO_FRUITORE.getFieldType()) );
		listaJDBCObject.add(new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazione.getIndirizzoSoggettoFruitore(),Transazione.model().INDIRIZZO_SOGGETTO_FRUITORE.getFieldType()) );
		listaJDBCObject.add(new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazione.getTipoSoggettoErogatore(),Transazione.model().TIPO_SOGGETTO_EROGATORE.getFieldType()) );
		listaJDBCObject.add(new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazione.getNomeSoggettoErogatore(),Transazione.model().NOME_SOGGETTO_EROGATORE.getFieldType()) );
		listaJDBCObject.add(new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazione.getIdportaSoggettoErogatore(),Transazione.model().IDPORTA_SOGGETTO_EROGATORE.getFieldType()) );
		listaJDBCObject.add(new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazione.getIndirizzoSoggettoErogatore(),Transazione.model().INDIRIZZO_SOGGETTO_EROGATORE.getFieldType()) );
		listaJDBCObject.add(new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazione.getIdMessaggioRichiesta(),Transazione.model().ID_MESSAGGIO_RICHIESTA.getFieldType()) );
		listaJDBCObject.add(new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazione.getIdMessaggioRisposta(),Transazione.model().ID_MESSAGGIO_RISPOSTA.getFieldType()) );
		listaJDBCObject.add(new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazione.getDataIdMsgRichiesta(),Transazione.model().DATA_ID_MSG_RICHIESTA.getFieldType()) );
		listaJDBCObject.add(new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazione.getDataIdMsgRisposta(),Transazione.model().DATA_ID_MSG_RISPOSTA.getFieldType()) );
		listaJDBCObject.add(new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazione.getProfiloCollaborazioneOp2(),Transazione.model().PROFILO_COLLABORAZIONE_OP_2.getFieldType()) );
		listaJDBCObject.add(new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazione.getProfiloCollaborazioneProt(),Transazione.model().PROFILO_COLLABORAZIONE_PROT.getFieldType()) );
		listaJDBCObject.add(new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazione.getIdCollaborazione(),Transazione.model().ID_COLLABORAZIONE.getFieldType()) );
		listaJDBCObject.add(new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazione.getUriAccordoServizio(),Transazione.model().URI_ACCORDO_SERVIZIO.getFieldType()) );
		listaJDBCObject.add(new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazione.getTipoServizio(),Transazione.model().TIPO_SERVIZIO.getFieldType()) );
		listaJDBCObject.add(new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazione.getNomeServizio(),Transazione.model().NOME_SERVIZIO.getFieldType()) );
		listaJDBCObject.add(new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazione.getVersioneServizio(),Transazione.model().VERSIONE_SERVIZIO.getFieldType()) );
		listaJDBCObject.add(new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazione.getAzione(),Transazione.model().AZIONE.getFieldType()) );
		listaJDBCObject.add(new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazione.getIdAsincrono(),Transazione.model().ID_ASINCRONO.getFieldType()) );
		listaJDBCObject.add(new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazione.getTipoServizioCorrelato(),Transazione.model().TIPO_SERVIZIO_CORRELATO.getFieldType()) );
		listaJDBCObject.add(new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazione.getNomeServizioCorrelato(),Transazione.model().NOME_SERVIZIO_CORRELATO.getFieldType()) );
		listaJDBCObject.add(new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazione.getHeaderProtocolloRichiesta(),Transazione.model().HEADER_PROTOCOLLO_RICHIESTA.getFieldType()) );
		listaJDBCObject.add(new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazione.getDigestRichiesta(),Transazione.model().DIGEST_RICHIESTA.getFieldType()) );
		listaJDBCObject.add(new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazione.getProtocolloExtInfoRichiesta(),Transazione.model().PROTOCOLLO_EXT_INFO_RICHIESTA.getFieldType()) );
		listaJDBCObject.add(new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazione.getHeaderProtocolloRisposta(),Transazione.model().HEADER_PROTOCOLLO_RISPOSTA.getFieldType()) );
		listaJDBCObject.add(new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazione.getDigestRisposta(),Transazione.model().DIGEST_RISPOSTA.getFieldType()) );
		listaJDBCObject.add(new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazione.getProtocolloExtInfoRisposta(),Transazione.model().PROTOCOLLO_EXT_INFO_RISPOSTA.getFieldType()) );
		listaJDBCObject.add(new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazione.getTracciaRichiesta(),Transazione.model().TRACCIA_RICHIESTA.getFieldType()) );
		listaJDBCObject.add(new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazione.getTracciaRisposta(),Transazione.model().TRACCIA_RISPOSTA.getFieldType()) );
		listaJDBCObject.add(new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazione.getDiagnostici(),Transazione.model().DIAGNOSTICI.getFieldType()) );
		listaJDBCObject.add(new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazione.getDiagnosticiList1(),Transazione.model().DIAGNOSTICI_LIST_1.getFieldType()) );
		listaJDBCObject.add(new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazione.getDiagnosticiList2(),Transazione.model().DIAGNOSTICI_LIST_2.getFieldType()) );
		listaJDBCObject.add(new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazione.getDiagnosticiListExt(),Transazione.model().DIAGNOSTICI_LIST_EXT.getFieldType()) );
		listaJDBCObject.add(new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazione.getDiagnosticiExt(),Transazione.model().DIAGNOSTICI_EXT.getFieldType()) );
		listaJDBCObject.add(new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazione.getIdCorrelazioneApplicativa(),Transazione.model().ID_CORRELAZIONE_APPLICATIVA.getFieldType()) );
		listaJDBCObject.add(new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazione.getIdCorrelazioneApplicativaRisposta(),Transazione.model().ID_CORRELAZIONE_APPLICATIVA_RISPOSTA.getFieldType()) );
		listaJDBCObject.add(new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazione.getServizioApplicativoFruitore(),Transazione.model().SERVIZIO_APPLICATIVO_FRUITORE.getFieldType()) );
		listaJDBCObject.add(new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazione.getServizioApplicativoErogatore(),Transazione.model().SERVIZIO_APPLICATIVO_EROGATORE.getFieldType()) );
		listaJDBCObject.add(new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazione.getOperazioneIm(),Transazione.model().OPERAZIONE_IM.getFieldType()) );
		listaJDBCObject.add(new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazione.getLocationRichiesta(),Transazione.model().LOCATION_RICHIESTA.getFieldType()) );
		listaJDBCObject.add(new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazione.getLocationRisposta(),Transazione.model().LOCATION_RISPOSTA.getFieldType()) );
		listaJDBCObject.add(new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazione.getNomePorta(),Transazione.model().NOME_PORTA.getFieldType()) );
		listaJDBCObject.add(new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazione.getCredenziali(),Transazione.model().CREDENZIALI.getFieldType()) );
		listaJDBCObject.add(new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazione.getLocationConnettore(),Transazione.model().LOCATION_CONNETTORE.getFieldType()) );
		listaJDBCObject.add(new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazione.getUrlInvocazione(),Transazione.model().URL_INVOCAZIONE.getFieldType()) );
		listaJDBCObject.add(new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazione.getTrasportoMittente(),Transazione.model().TRASPORTO_MITTENTE.getFieldType()) );
		listaJDBCObject.add(new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazione.getTokenIssuer(),Transazione.model().TOKEN_ISSUER.getFieldType()) );
		listaJDBCObject.add(new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazione.getTokenClientId(),Transazione.model().TOKEN_CLIENT_ID.getFieldType()) );
		listaJDBCObject.add(new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazione.getTokenSubject(),Transazione.model().TOKEN_SUBJECT.getFieldType()) );
		listaJDBCObject.add(new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazione.getTokenUsername(),Transazione.model().TOKEN_USERNAME.getFieldType()) );
		listaJDBCObject.add(new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazione.getTokenMail(),Transazione.model().TOKEN_MAIL.getFieldType()) );
		listaJDBCObject.add(new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazione.getTokenInfo(),Transazione.model().TOKEN_INFO.getFieldType()) );
		listaJDBCObject.add(new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazione.getTempiElaborazione(),Transazione.model().TEMPI_ELABORAZIONE.getFieldType()) );
		listaJDBCObject.add(new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazione.getDuplicatiRichiesta(),Transazione.model().DUPLICATI_RICHIESTA.getFieldType()) );
		listaJDBCObject.add(new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazione.getDuplicatiRisposta(),Transazione.model().DUPLICATI_RISPOSTA.getFieldType()) );
		listaJDBCObject.add(new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazione.getClusterId(),Transazione.model().CLUSTER_ID.getFieldType()) );
		listaJDBCObject.add(new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazione.getSocketClientAddress(),Transazione.model().SOCKET_CLIENT_ADDRESS.getFieldType()) );
		listaJDBCObject.add(new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazione.getTransportClientAddress(),Transazione.model().TRANSPORT_CLIENT_ADDRESS.getFieldType()) );
		listaJDBCObject.add(new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazione.getClientAddress(),Transazione.model().CLIENT_ADDRESS.getFieldType()));
		listaJDBCObject.add(new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazione.getEventiGestione(),Transazione.model().EVENTI_GESTIONE.getFieldType()));
		listaJDBCObject.add(new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazione.getTipoApi(),Transazione.model().TIPO_API.getFieldType()));
		listaJDBCObject.add(new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazione.getUriApi(),Transazione.model().URI_API.getFieldType()));
		listaJDBCObject.add(new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazione.getGruppi(),Transazione.model().GRUPPI.getFieldType()));
		// transazione-extended-info: serve ad estendere la tabella transazioni su progetti specifici (es. pdc, fwuuid)
		if(transazione.sizeTransazioneExtendedInfoList()>0){
			for (TransazioneExtendedInfo transazioneExtedendInfo : transazione.getTransazioneExtendedInfoList()) {
				listaJDBCObject.add( new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(transazioneExtedendInfo.getValore(),String.class));
			}
		}
		jdbcUtilities.execute(insertSql, jdbcProperties.isShowSql(), 
				listaJDBCObject.toArray(new JDBCObject[1])
		);
		
	}

	@Override
	public void update(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, String oldId, Transazione transazione, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		
		if(TipiDatabase.POSTGRESQL.equals(jdbcProperties.getDatabase())){
			if(transazione.getFaultCooperazione()!=null && org.openspcoop2.utils.jdbc.PostgreSQLUtilities.containsNullByteSequence(transazione.getFaultCooperazione())) {
				transazione.setFaultCooperazione(org.openspcoop2.utils.jdbc.PostgreSQLUtilities.normalizeString(transazione.getFaultCooperazione()));
			}
			if(transazione.getFaultIntegrazione()!=null && org.openspcoop2.utils.jdbc.PostgreSQLUtilities.containsNullByteSequence(transazione.getFaultIntegrazione())) {
				transazione.setFaultIntegrazione(org.openspcoop2.utils.jdbc.PostgreSQLUtilities.normalizeString(transazione.getFaultIntegrazione()));
			}
			if(transazione.getCredenziali()!=null && org.openspcoop2.utils.jdbc.PostgreSQLUtilities.containsNullByteSequence(transazione.getCredenziali())) {
				transazione.setCredenziali(org.openspcoop2.utils.jdbc.PostgreSQLUtilities.normalizeString(transazione.getCredenziali()));
			}
		}
			
		ISQLQueryObject sqlQueryObjectUpdate = sqlQueryObject.newSQLQueryObject();
		Object longIdByLogicId = this.findIdTransazione(jdbcProperties, log, connection, sqlQueryObjectUpdate.newSQLQueryObject(), oldId, true, false);
		if(longIdByLogicId==null){
			throw new Exception("Retrieve logicId failed");
		}

	
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);
		
		// Object transazione
		sqlQueryObjectUpdate.setANDLogicOperator(true);
		sqlQueryObjectUpdate.addUpdateTable(this.getTransazioneFieldConverter().toTable(Transazione.model()));
		boolean isUpdate_transazione = true;
		java.util.List<JDBCObject> lstObjects_transazione = new java.util.ArrayList<JDBCObject>();
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneFieldConverter().toColumn(Transazione.model().ID_TRANSAZIONE,false), "?");
		lstObjects_transazione.add(new JDBCObject(transazione.getIdTransazione(), Transazione.model().ID_TRANSAZIONE.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneFieldConverter().toColumn(Transazione.model().STATO,false), "?");
		lstObjects_transazione.add(new JDBCObject(transazione.getStato(), Transazione.model().STATO.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneFieldConverter().toColumn(Transazione.model().RUOLO_TRANSAZIONE,false), "?");
		lstObjects_transazione.add(new JDBCObject(transazione.getRuoloTransazione(), Transazione.model().RUOLO_TRANSAZIONE.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneFieldConverter().toColumn(Transazione.model().ESITO,false), "?");
		lstObjects_transazione.add(new JDBCObject(transazione.getEsito(), Transazione.model().ESITO.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneFieldConverter().toColumn(Transazione.model().ESITO_SINCRONO,false), "?");
		lstObjects_transazione.add(new JDBCObject(transazione.getEsitoSincrono(), Transazione.model().ESITO_SINCRONO.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneFieldConverter().toColumn(Transazione.model().CONSEGNE_MULTIPLE_IN_CORSO,false), "?");
		lstObjects_transazione.add(new JDBCObject(transazione.getConsegneMultipleInCorso(), Transazione.model().CONSEGNE_MULTIPLE_IN_CORSO.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneFieldConverter().toColumn(Transazione.model().ESITO_CONTESTO,false), "?");
		lstObjects_transazione.add(new JDBCObject(transazione.getEsitoContesto(), Transazione.model().ESITO_CONTESTO.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneFieldConverter().toColumn(Transazione.model().PROTOCOLLO,false), "?");
		lstObjects_transazione.add(new JDBCObject(transazione.getProtocollo(), Transazione.model().PROTOCOLLO.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneFieldConverter().toColumn(Transazione.model().TIPO_RICHIESTA,false), "?");
		lstObjects_transazione.add(new JDBCObject(transazione.getTipoRichiesta(), Transazione.model().TIPO_RICHIESTA.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneFieldConverter().toColumn(Transazione.model().CODICE_RISPOSTA_INGRESSO,false), "?");
		lstObjects_transazione.add(new JDBCObject(transazione.getCodiceRispostaIngresso(), Transazione.model().CODICE_RISPOSTA_INGRESSO.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneFieldConverter().toColumn(Transazione.model().CODICE_RISPOSTA_USCITA,false), "?");
		lstObjects_transazione.add(new JDBCObject(transazione.getCodiceRispostaUscita(), Transazione.model().CODICE_RISPOSTA_USCITA.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneFieldConverter().toColumn(Transazione.model().DATA_ACCETTAZIONE_RICHIESTA,false), "?");
		lstObjects_transazione.add(new JDBCObject(transazione.getDataAccettazioneRichiesta(), Transazione.model().DATA_ACCETTAZIONE_RICHIESTA.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneFieldConverter().toColumn(Transazione.model().DATA_INGRESSO_RICHIESTA,false), "?");
		lstObjects_transazione.add(new JDBCObject(transazione.getDataIngressoRichiesta(), Transazione.model().DATA_INGRESSO_RICHIESTA.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneFieldConverter().toColumn(Transazione.model().DATA_USCITA_RICHIESTA,false), "?");
		lstObjects_transazione.add(new JDBCObject(transazione.getDataUscitaRichiesta(), Transazione.model().DATA_USCITA_RICHIESTA.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneFieldConverter().toColumn(Transazione.model().DATA_ACCETTAZIONE_RISPOSTA,false), "?");
		lstObjects_transazione.add(new JDBCObject(transazione.getDataAccettazioneRisposta(), Transazione.model().DATA_ACCETTAZIONE_RISPOSTA.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneFieldConverter().toColumn(Transazione.model().DATA_INGRESSO_RISPOSTA,false), "?");
		lstObjects_transazione.add(new JDBCObject(transazione.getDataIngressoRisposta(), Transazione.model().DATA_INGRESSO_RISPOSTA.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneFieldConverter().toColumn(Transazione.model().DATA_USCITA_RISPOSTA,false), "?");
		lstObjects_transazione.add(new JDBCObject(transazione.getDataUscitaRisposta(), Transazione.model().DATA_USCITA_RISPOSTA.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneFieldConverter().toColumn(Transazione.model().RICHIESTA_INGRESSO_BYTES,false), "?");
		lstObjects_transazione.add(new JDBCObject(transazione.getRichiestaIngressoBytes(), Transazione.model().RICHIESTA_INGRESSO_BYTES.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneFieldConverter().toColumn(Transazione.model().RICHIESTA_USCITA_BYTES,false), "?");
		lstObjects_transazione.add(new JDBCObject(transazione.getRichiestaUscitaBytes(), Transazione.model().RICHIESTA_USCITA_BYTES.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneFieldConverter().toColumn(Transazione.model().RISPOSTA_INGRESSO_BYTES,false), "?");
		lstObjects_transazione.add(new JDBCObject(transazione.getRispostaIngressoBytes(), Transazione.model().RISPOSTA_INGRESSO_BYTES.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneFieldConverter().toColumn(Transazione.model().RISPOSTA_USCITA_BYTES,false), "?");
		lstObjects_transazione.add(new JDBCObject(transazione.getRispostaUscitaBytes(), Transazione.model().RISPOSTA_USCITA_BYTES.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneFieldConverter().toColumn(Transazione.model().PDD_CODICE,false), "?");
		lstObjects_transazione.add(new JDBCObject(transazione.getPddCodice(), Transazione.model().PDD_CODICE.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneFieldConverter().toColumn(Transazione.model().PDD_TIPO_SOGGETTO,false), "?");
		lstObjects_transazione.add(new JDBCObject(transazione.getPddTipoSoggetto(), Transazione.model().PDD_TIPO_SOGGETTO.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneFieldConverter().toColumn(Transazione.model().PDD_NOME_SOGGETTO,false), "?");
		lstObjects_transazione.add(new JDBCObject(transazione.getPddNomeSoggetto(), Transazione.model().PDD_NOME_SOGGETTO.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneFieldConverter().toColumn(Transazione.model().PDD_RUOLO,false), "?");
		lstObjects_transazione.add(new JDBCObject(transazione.getPddRuolo(), Transazione.model().PDD_RUOLO.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneFieldConverter().toColumn(Transazione.model().FAULT_INTEGRAZIONE,false), "?");
		lstObjects_transazione.add(new JDBCObject(transazione.getFaultIntegrazione(), Transazione.model().FAULT_INTEGRAZIONE.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneFieldConverter().toColumn(Transazione.model().FORMATO_FAULT_INTEGRAZIONE,false), "?");
		lstObjects_transazione.add(new JDBCObject(transazione.getFormatoFaultIntegrazione(), Transazione.model().FORMATO_FAULT_INTEGRAZIONE.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneFieldConverter().toColumn(Transazione.model().FAULT_COOPERAZIONE,false), "?");
		lstObjects_transazione.add(new JDBCObject(transazione.getFaultCooperazione(), Transazione.model().FAULT_COOPERAZIONE.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneFieldConverter().toColumn(Transazione.model().FORMATO_FAULT_COOPERAZIONE,false), "?");
		lstObjects_transazione.add(new JDBCObject(transazione.getFormatoFaultCooperazione(), Transazione.model().FORMATO_FAULT_COOPERAZIONE.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneFieldConverter().toColumn(Transazione.model().TIPO_SOGGETTO_FRUITORE,false), "?");
		lstObjects_transazione.add(new JDBCObject(transazione.getTipoSoggettoFruitore(), Transazione.model().TIPO_SOGGETTO_FRUITORE.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneFieldConverter().toColumn(Transazione.model().NOME_SOGGETTO_FRUITORE,false), "?");
		lstObjects_transazione.add(new JDBCObject(transazione.getNomeSoggettoFruitore(), Transazione.model().NOME_SOGGETTO_FRUITORE.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneFieldConverter().toColumn(Transazione.model().IDPORTA_SOGGETTO_FRUITORE,false), "?");
		lstObjects_transazione.add(new JDBCObject(transazione.getIdportaSoggettoFruitore(), Transazione.model().IDPORTA_SOGGETTO_FRUITORE.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneFieldConverter().toColumn(Transazione.model().INDIRIZZO_SOGGETTO_FRUITORE,false), "?");
		lstObjects_transazione.add(new JDBCObject(transazione.getIndirizzoSoggettoFruitore(), Transazione.model().INDIRIZZO_SOGGETTO_FRUITORE.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneFieldConverter().toColumn(Transazione.model().TIPO_SOGGETTO_EROGATORE,false), "?");
		lstObjects_transazione.add(new JDBCObject(transazione.getTipoSoggettoErogatore(), Transazione.model().TIPO_SOGGETTO_EROGATORE.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneFieldConverter().toColumn(Transazione.model().NOME_SOGGETTO_EROGATORE,false), "?");
		lstObjects_transazione.add(new JDBCObject(transazione.getNomeSoggettoErogatore(), Transazione.model().NOME_SOGGETTO_EROGATORE.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneFieldConverter().toColumn(Transazione.model().IDPORTA_SOGGETTO_EROGATORE,false), "?");
		lstObjects_transazione.add(new JDBCObject(transazione.getIdportaSoggettoErogatore(), Transazione.model().IDPORTA_SOGGETTO_EROGATORE.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneFieldConverter().toColumn(Transazione.model().INDIRIZZO_SOGGETTO_EROGATORE,false), "?");
		lstObjects_transazione.add(new JDBCObject(transazione.getIndirizzoSoggettoErogatore(), Transazione.model().INDIRIZZO_SOGGETTO_EROGATORE.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneFieldConverter().toColumn(Transazione.model().ID_MESSAGGIO_RICHIESTA,false), "?");
		lstObjects_transazione.add(new JDBCObject(transazione.getIdMessaggioRichiesta(), Transazione.model().ID_MESSAGGIO_RICHIESTA.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneFieldConverter().toColumn(Transazione.model().ID_MESSAGGIO_RISPOSTA,false), "?");
		lstObjects_transazione.add(new JDBCObject(transazione.getIdMessaggioRisposta(), Transazione.model().ID_MESSAGGIO_RISPOSTA.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneFieldConverter().toColumn(Transazione.model().DATA_ID_MSG_RICHIESTA,false), "?");
		lstObjects_transazione.add(new JDBCObject(transazione.getDataIdMsgRichiesta(), Transazione.model().DATA_ID_MSG_RICHIESTA.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneFieldConverter().toColumn(Transazione.model().DATA_ID_MSG_RISPOSTA,false), "?");
		lstObjects_transazione.add(new JDBCObject(transazione.getDataIdMsgRisposta(), Transazione.model().DATA_ID_MSG_RISPOSTA.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneFieldConverter().toColumn(Transazione.model().PROFILO_COLLABORAZIONE_OP_2,false), "?");
		lstObjects_transazione.add(new JDBCObject(transazione.getProfiloCollaborazioneOp2(), Transazione.model().PROFILO_COLLABORAZIONE_OP_2.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneFieldConverter().toColumn(Transazione.model().PROFILO_COLLABORAZIONE_PROT,false), "?");
		lstObjects_transazione.add(new JDBCObject(transazione.getProfiloCollaborazioneProt(), Transazione.model().PROFILO_COLLABORAZIONE_PROT.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneFieldConverter().toColumn(Transazione.model().ID_COLLABORAZIONE,false), "?");
		lstObjects_transazione.add(new JDBCObject(transazione.getIdCollaborazione(), Transazione.model().ID_COLLABORAZIONE.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneFieldConverter().toColumn(Transazione.model().URI_ACCORDO_SERVIZIO,false), "?");
		lstObjects_transazione.add(new JDBCObject(transazione.getUriAccordoServizio(), Transazione.model().URI_ACCORDO_SERVIZIO.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneFieldConverter().toColumn(Transazione.model().TIPO_SERVIZIO,false), "?");
		lstObjects_transazione.add(new JDBCObject(transazione.getTipoServizio(), Transazione.model().TIPO_SERVIZIO.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneFieldConverter().toColumn(Transazione.model().NOME_SERVIZIO,false), "?");
		lstObjects_transazione.add(new JDBCObject(transazione.getNomeServizio(), Transazione.model().NOME_SERVIZIO.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneFieldConverter().toColumn(Transazione.model().VERSIONE_SERVIZIO,false), "?");
		lstObjects_transazione.add(new JDBCObject(transazione.getVersioneServizio(), Transazione.model().VERSIONE_SERVIZIO.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneFieldConverter().toColumn(Transazione.model().AZIONE,false), "?");
		lstObjects_transazione.add(new JDBCObject(transazione.getAzione(), Transazione.model().AZIONE.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneFieldConverter().toColumn(Transazione.model().ID_ASINCRONO,false), "?");
		lstObjects_transazione.add(new JDBCObject(transazione.getIdAsincrono(), Transazione.model().ID_ASINCRONO.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneFieldConverter().toColumn(Transazione.model().TIPO_SERVIZIO_CORRELATO,false), "?");
		lstObjects_transazione.add(new JDBCObject(transazione.getTipoServizioCorrelato(), Transazione.model().TIPO_SERVIZIO_CORRELATO.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneFieldConverter().toColumn(Transazione.model().NOME_SERVIZIO_CORRELATO,false), "?");
		lstObjects_transazione.add(new JDBCObject(transazione.getNomeServizioCorrelato(), Transazione.model().NOME_SERVIZIO_CORRELATO.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneFieldConverter().toColumn(Transazione.model().HEADER_PROTOCOLLO_RICHIESTA,false), "?");
		lstObjects_transazione.add(new JDBCObject(transazione.getHeaderProtocolloRichiesta(), Transazione.model().HEADER_PROTOCOLLO_RICHIESTA.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneFieldConverter().toColumn(Transazione.model().DIGEST_RICHIESTA,false), "?");
		lstObjects_transazione.add(new JDBCObject(transazione.getDigestRichiesta(), Transazione.model().DIGEST_RICHIESTA.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneFieldConverter().toColumn(Transazione.model().PROTOCOLLO_EXT_INFO_RICHIESTA,false), "?");
		lstObjects_transazione.add(new JDBCObject(transazione.getProtocolloExtInfoRichiesta(), Transazione.model().PROTOCOLLO_EXT_INFO_RICHIESTA.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneFieldConverter().toColumn(Transazione.model().HEADER_PROTOCOLLO_RISPOSTA,false), "?");
		lstObjects_transazione.add(new JDBCObject(transazione.getHeaderProtocolloRisposta(), Transazione.model().HEADER_PROTOCOLLO_RISPOSTA.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneFieldConverter().toColumn(Transazione.model().DIGEST_RISPOSTA,false), "?");
		lstObjects_transazione.add(new JDBCObject(transazione.getDigestRisposta(), Transazione.model().DIGEST_RISPOSTA.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneFieldConverter().toColumn(Transazione.model().PROTOCOLLO_EXT_INFO_RISPOSTA,false), "?");
		lstObjects_transazione.add(new JDBCObject(transazione.getProtocolloExtInfoRisposta(), Transazione.model().PROTOCOLLO_EXT_INFO_RISPOSTA.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneFieldConverter().toColumn(Transazione.model().TRACCIA_RICHIESTA,false), "?");
		lstObjects_transazione.add(new JDBCObject(transazione.getTracciaRichiesta(), Transazione.model().TRACCIA_RICHIESTA.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneFieldConverter().toColumn(Transazione.model().TRACCIA_RISPOSTA,false), "?");
		lstObjects_transazione.add(new JDBCObject(transazione.getTracciaRisposta(), Transazione.model().TRACCIA_RISPOSTA.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneFieldConverter().toColumn(Transazione.model().DIAGNOSTICI,false), "?");
		lstObjects_transazione.add(new JDBCObject(transazione.getDiagnostici(), Transazione.model().DIAGNOSTICI.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneFieldConverter().toColumn(Transazione.model().DIAGNOSTICI_LIST_1,false), "?");
		lstObjects_transazione.add(new JDBCObject(transazione.getDiagnosticiList1(), Transazione.model().DIAGNOSTICI_LIST_1.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneFieldConverter().toColumn(Transazione.model().DIAGNOSTICI_LIST_2,false), "?");
		lstObjects_transazione.add(new JDBCObject(transazione.getDiagnosticiList2(), Transazione.model().DIAGNOSTICI_LIST_2.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneFieldConverter().toColumn(Transazione.model().DIAGNOSTICI_LIST_EXT,false), "?");
		lstObjects_transazione.add(new JDBCObject(transazione.getDiagnosticiListExt(), Transazione.model().DIAGNOSTICI_LIST_EXT.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneFieldConverter().toColumn(Transazione.model().DIAGNOSTICI_EXT,false), "?");
		lstObjects_transazione.add(new JDBCObject(transazione.getDiagnosticiExt(), Transazione.model().DIAGNOSTICI_EXT.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneFieldConverter().toColumn(Transazione.model().ID_CORRELAZIONE_APPLICATIVA,false), "?");
		lstObjects_transazione.add(new JDBCObject(transazione.getIdCorrelazioneApplicativa(), Transazione.model().ID_CORRELAZIONE_APPLICATIVA.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneFieldConverter().toColumn(Transazione.model().ID_CORRELAZIONE_APPLICATIVA_RISPOSTA,false), "?");
		lstObjects_transazione.add(new JDBCObject(transazione.getIdCorrelazioneApplicativaRisposta(), Transazione.model().ID_CORRELAZIONE_APPLICATIVA_RISPOSTA.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneFieldConverter().toColumn(Transazione.model().SERVIZIO_APPLICATIVO_FRUITORE,false), "?");
		lstObjects_transazione.add(new JDBCObject(transazione.getServizioApplicativoFruitore(), Transazione.model().SERVIZIO_APPLICATIVO_FRUITORE.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneFieldConverter().toColumn(Transazione.model().SERVIZIO_APPLICATIVO_EROGATORE,false), "?");
		lstObjects_transazione.add(new JDBCObject(transazione.getServizioApplicativoErogatore(), Transazione.model().SERVIZIO_APPLICATIVO_EROGATORE.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneFieldConverter().toColumn(Transazione.model().OPERAZIONE_IM,false), "?");
		lstObjects_transazione.add(new JDBCObject(transazione.getOperazioneIm(), Transazione.model().OPERAZIONE_IM.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneFieldConverter().toColumn(Transazione.model().LOCATION_RICHIESTA,false), "?");
		lstObjects_transazione.add(new JDBCObject(transazione.getLocationRichiesta(), Transazione.model().LOCATION_RICHIESTA.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneFieldConverter().toColumn(Transazione.model().LOCATION_RISPOSTA,false), "?");
		lstObjects_transazione.add(new JDBCObject(transazione.getLocationRisposta(), Transazione.model().LOCATION_RISPOSTA.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneFieldConverter().toColumn(Transazione.model().NOME_PORTA,false), "?");
		lstObjects_transazione.add(new JDBCObject(transazione.getNomePorta(), Transazione.model().NOME_PORTA.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneFieldConverter().toColumn(Transazione.model().CREDENZIALI,false), "?");
		lstObjects_transazione.add(new JDBCObject(transazione.getCredenziali(), Transazione.model().CREDENZIALI.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneFieldConverter().toColumn(Transazione.model().LOCATION_CONNETTORE,false), "?");
		lstObjects_transazione.add(new JDBCObject(transazione.getLocationConnettore(), Transazione.model().LOCATION_CONNETTORE.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneFieldConverter().toColumn(Transazione.model().URL_INVOCAZIONE,false), "?");
		lstObjects_transazione.add(new JDBCObject(transazione.getUrlInvocazione(), Transazione.model().URL_INVOCAZIONE.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneFieldConverter().toColumn(Transazione.model().TRASPORTO_MITTENTE,false), "?");
		lstObjects_transazione.add(new JDBCObject(transazione.getTrasportoMittente(), Transazione.model().TRASPORTO_MITTENTE.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneFieldConverter().toColumn(Transazione.model().TOKEN_ISSUER,false), "?");
		lstObjects_transazione.add(new JDBCObject(transazione.getTokenIssuer(), Transazione.model().TOKEN_ISSUER.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneFieldConverter().toColumn(Transazione.model().TOKEN_CLIENT_ID,false), "?");
		lstObjects_transazione.add(new JDBCObject(transazione.getTokenClientId(), Transazione.model().TOKEN_CLIENT_ID.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneFieldConverter().toColumn(Transazione.model().TOKEN_SUBJECT,false), "?");
		lstObjects_transazione.add(new JDBCObject(transazione.getTokenSubject(), Transazione.model().TOKEN_SUBJECT.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneFieldConverter().toColumn(Transazione.model().TOKEN_USERNAME,false), "?");
		lstObjects_transazione.add(new JDBCObject(transazione.getTokenUsername(), Transazione.model().TOKEN_USERNAME.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneFieldConverter().toColumn(Transazione.model().TOKEN_MAIL,false), "?");
		lstObjects_transazione.add(new JDBCObject(transazione.getTokenMail(), Transazione.model().TOKEN_MAIL.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneFieldConverter().toColumn(Transazione.model().TOKEN_INFO,false), "?");
		lstObjects_transazione.add(new JDBCObject(transazione.getTokenInfo(), Transazione.model().TOKEN_INFO.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneFieldConverter().toColumn(Transazione.model().TEMPI_ELABORAZIONE,false), "?");
		lstObjects_transazione.add(new JDBCObject(transazione.getTempiElaborazione(), Transazione.model().TEMPI_ELABORAZIONE.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneFieldConverter().toColumn(Transazione.model().DUPLICATI_RICHIESTA,false), "?");
		lstObjects_transazione.add(new JDBCObject(transazione.getDuplicatiRichiesta(), Transazione.model().DUPLICATI_RICHIESTA.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneFieldConverter().toColumn(Transazione.model().DUPLICATI_RISPOSTA,false), "?");
		lstObjects_transazione.add(new JDBCObject(transazione.getDuplicatiRisposta(), Transazione.model().DUPLICATI_RISPOSTA.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneFieldConverter().toColumn(Transazione.model().CLUSTER_ID,false), "?");
		lstObjects_transazione.add(new JDBCObject(transazione.getClusterId(), Transazione.model().CLUSTER_ID.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneFieldConverter().toColumn(Transazione.model().SOCKET_CLIENT_ADDRESS,false), "?");
		lstObjects_transazione.add(new JDBCObject(transazione.getSocketClientAddress(), Transazione.model().SOCKET_CLIENT_ADDRESS.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneFieldConverter().toColumn(Transazione.model().TRANSPORT_CLIENT_ADDRESS,false), "?");
		lstObjects_transazione.add(new JDBCObject(transazione.getTransportClientAddress(), Transazione.model().TRANSPORT_CLIENT_ADDRESS.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneFieldConverter().toColumn(Transazione.model().CLIENT_ADDRESS,false), "?");
		lstObjects_transazione.add(new JDBCObject(transazione.getClientAddress(), Transazione.model().CLIENT_ADDRESS.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneFieldConverter().toColumn(Transazione.model().EVENTI_GESTIONE,false), "?");
		lstObjects_transazione.add(new JDBCObject(transazione.getEventiGestione(), Transazione.model().EVENTI_GESTIONE.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneFieldConverter().toColumn(Transazione.model().TIPO_API,false), "?");
		lstObjects_transazione.add(new JDBCObject(transazione.getTipoApi(), Transazione.model().TIPO_API.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneFieldConverter().toColumn(Transazione.model().URI_API,false), "?");
		lstObjects_transazione.add(new JDBCObject(transazione.getUriApi(), Transazione.model().URI_API.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getTransazioneFieldConverter().toColumn(Transazione.model().GRUPPI,false), "?");
		lstObjects_transazione.add(new JDBCObject(transazione.getGruppi(), Transazione.model().GRUPPI.getFieldType()));
		if(transazione.sizeTransazioneExtendedInfoList()>0){
			for (TransazioneExtendedInfo transazioneExtedendInfo : transazione.getTransazioneExtendedInfoList()) {
				sqlQueryObjectUpdate.addUpdateField(transazioneExtedendInfo.getNome(), "?");
				lstObjects_transazione.add(new JDBCObject(transazioneExtedendInfo.getValore(), String.class));
			}
		}
		sqlQueryObjectUpdate.addWhereCondition(this.getTransazioneFieldConverter().toColumn(Transazione.model().ID_TRANSAZIONE,false)+"=?");
		lstObjects_transazione.add(new JDBCObject(longIdByLogicId, longIdByLogicId.getClass()));

		if(isUpdate_transazione) {
			// Update transazione
			jdbcUtilities.executeUpdate(sqlQueryObjectUpdate.createSQLUpdate(), jdbcProperties.isShowSql(), 
				lstObjects_transazione.toArray(new JDBCObject[]{}));
		}
		
	}
	@Override
	public void update(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, Transazione transazione, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		throw new NotImplementedException("Table without long id column PK");
	}
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, String id, UpdateField ... updateFields) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		
		JDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getTransazioneFieldConverter().toTable(Transazione.model()), 
				this._getMapTableToPKColumn(), 
				this._getRootTablePrimaryKeyValues(jdbcProperties, log, connection, sqlQueryObject, id),
				this.getTransazioneFieldConverter(), this, null, updateFields);
	}
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, String id, IExpression condition, UpdateField ... updateFields) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		
		JDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getTransazioneFieldConverter().toTable(Transazione.model()), 
				this._getMapTableToPKColumn(), 
				this._getRootTablePrimaryKeyValues(jdbcProperties, log, connection, sqlQueryObject, id),
				this.getTransazioneFieldConverter(), this, condition, updateFields);
	}
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, String id, UpdateModel ... updateModels) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		
		JDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getTransazioneFieldConverter().toTable(Transazione.model()), 
				this._getMapTableToPKColumn(), 
				this._getRootTablePrimaryKeyValues(jdbcProperties, log, connection, sqlQueryObject, id),
				this.getTransazioneFieldConverter(), this, updateModels);
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
	public void updateOrCreate(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, String oldId, Transazione transazione, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotImplementedException,ServiceException,Exception {
	
		if(this.exists(jdbcProperties, log, connection, sqlQueryObject, oldId)) {
			this.update(jdbcProperties, log, connection, sqlQueryObject, oldId, transazione,idMappingResolutionBehaviour);
		} else {
			this.create(jdbcProperties, log, connection, sqlQueryObject, transazione,idMappingResolutionBehaviour);
		}
		
	}
	
	@Override
	public void updateOrCreate(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, Transazione transazione, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotImplementedException,ServiceException,Exception {
		throw new NotImplementedException("Table without long id column PK");
	}
	
	@Override
	public void delete(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, Transazione transazione) throws NotImplementedException,ServiceException,Exception {
		
		
		String idObject = transazione.getIdTransazione();
		if(idObject==null){
			throw new ServiceException("IdTransazione non presente nell'oggetto");
		}
		
		this._delete(jdbcProperties, log, connection, sqlQueryObject, idObject);
        
	}

	private void _delete(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, Object id) throws NotImplementedException,ServiceException,Exception {
		
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);
		
		ISQLQueryObject sqlQueryObjectDelete = sqlQueryObject.newSQLQueryObject();
		
		// Object transazione
		sqlQueryObjectDelete.setANDLogicOperator(true);
		sqlQueryObjectDelete.addDeleteTable(this.getTransazioneFieldConverter().toTable(Transazione.model()));
		sqlQueryObjectDelete.addWhereCondition(this.getTransazioneFieldConverter().toColumn(Transazione.model().ID_TRANSAZIONE,false)+"=?");

		// Delete transazione
		jdbcUtilities.execute(sqlQueryObjectDelete.createSQLDelete(), jdbcProperties.isShowSql(), 
			new JDBCObject(id,id.getClass()));

	}

	@Override
	public void deleteById(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, String idTransazione) throws NotImplementedException,ServiceException,Exception {

		Object id = null;
		try{
			id = this.findIdTransazione(jdbcProperties, log, connection, sqlQueryObject, idTransazione, true, true);
		}catch(NotFoundException notFound){
			return;
		}			
		this._delete(jdbcProperties, log, connection, sqlQueryObject, id);
		
	}
	
	@Override
	public NonNegativeNumber deleteAll(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject) throws NotImplementedException,ServiceException,Exception {
		
		return this.deleteAll(jdbcProperties, log, connection, sqlQueryObject, new JDBCExpression(this.getTransazioneFieldConverter()));

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
	
		return org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.nativeUpdate(jdbcProperties, log, connection, sqlObject,
																							sql,param);
	
	}
}
