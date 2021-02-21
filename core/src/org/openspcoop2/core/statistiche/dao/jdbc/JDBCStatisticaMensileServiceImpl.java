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
package org.openspcoop2.core.statistiche.dao.jdbc;

import java.sql.Connection;

import org.openspcoop2.utils.sql.ISQLQueryObject;

import org.slf4j.Logger;

import org.openspcoop2.generic_project.dao.jdbc.IJDBCServiceCRUDWithoutId;
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

import org.openspcoop2.core.statistiche.Statistica;
import org.openspcoop2.core.statistiche.StatisticaContenuti;
import org.openspcoop2.core.statistiche.StatisticaMensile;

/**     
 * JDBCStatisticaMensileServiceImpl
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JDBCStatisticaMensileServiceImpl extends JDBCStatisticaMensileServiceSearchImpl
	implements IJDBCServiceCRUDWithoutId<StatisticaMensile, JDBCServiceManager> {

	@Override
	public void create(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, StatisticaMensile statisticaMensile, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotImplementedException,ServiceException,Exception {

		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);
		
		ISQLQueryObject sqlQueryObjectInsert = sqlQueryObject.newSQLQueryObject();
				


		// Object statisticaMensile.getStatisticaBase()
		sqlQueryObjectInsert.addInsertTable(this.getStatisticaMensileFieldConverter().toTable(StatisticaMensile.model().STATISTICA_BASE));
		sqlQueryObjectInsert.addInsertField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_BASE.DATA,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_BASE.STATO_RECORD,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_BASE.ID_PORTA,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_BASE.TIPO_PORTA,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_BASE.TIPO_MITTENTE,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_BASE.MITTENTE,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_BASE.TIPO_DESTINATARIO,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_BASE.DESTINATARIO,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_BASE.TIPO_SERVIZIO,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_BASE.SERVIZIO,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_BASE.VERSIONE_SERVIZIO,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_BASE.AZIONE,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_BASE.SERVIZIO_APPLICATIVO,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_BASE.TRASPORTO_MITTENTE,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_BASE.TOKEN_ISSUER,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_BASE.TOKEN_CLIENT_ID,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_BASE.TOKEN_SUBJECT,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_BASE.TOKEN_USERNAME,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_BASE.TOKEN_MAIL,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_BASE.ESITO,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_BASE.ESITO_CONTESTO,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_BASE.CLIENT_ADDRESS,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_BASE.GRUPPI,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_BASE.URI_API,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_BASE.CLUSTER_ID,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_BASE.NUMERO_TRANSAZIONI,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_BASE.DIMENSIONI_BYTES_BANDA_COMPLESSIVA,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_BASE.DIMENSIONI_BYTES_BANDA_INTERNA,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_BASE.DIMENSIONI_BYTES_BANDA_ESTERNA,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_BASE.LATENZA_TOTALE,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_BASE.LATENZA_PORTA,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_BASE.LATENZA_SERVIZIO,false),"?");

		// Insert statisticaMensile
		org.openspcoop2.utils.jdbc.IKeyGeneratorObject keyGenerator = this.getStatisticaMensileFetch().getKeyGeneratorObject(StatisticaMensile.model());
		long id = jdbcUtilities.insertAndReturnGeneratedKey(sqlQueryObjectInsert, keyGenerator, jdbcProperties.isShowSql(),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaMensile.getStatisticaBase().getData(),StatisticaMensile.model().STATISTICA_BASE.DATA.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaMensile.getStatisticaBase().getStatoRecord(),StatisticaMensile.model().STATISTICA_BASE.STATO_RECORD.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaMensile.getStatisticaBase().getIdPorta(),StatisticaMensile.model().STATISTICA_BASE.ID_PORTA.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaMensile.getStatisticaBase().getTipoPorta(),StatisticaMensile.model().STATISTICA_BASE.TIPO_PORTA.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaMensile.getStatisticaBase().getTipoMittente(),StatisticaMensile.model().STATISTICA_BASE.TIPO_MITTENTE.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaMensile.getStatisticaBase().getMittente(),StatisticaMensile.model().STATISTICA_BASE.MITTENTE.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaMensile.getStatisticaBase().getTipoDestinatario(),StatisticaMensile.model().STATISTICA_BASE.TIPO_DESTINATARIO.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaMensile.getStatisticaBase().getDestinatario(),StatisticaMensile.model().STATISTICA_BASE.DESTINATARIO.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaMensile.getStatisticaBase().getTipoServizio(),StatisticaMensile.model().STATISTICA_BASE.TIPO_SERVIZIO.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaMensile.getStatisticaBase().getServizio(),StatisticaMensile.model().STATISTICA_BASE.SERVIZIO.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaMensile.getStatisticaBase().getVersioneServizio(),StatisticaMensile.model().STATISTICA_BASE.VERSIONE_SERVIZIO.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaMensile.getStatisticaBase().getAzione(),StatisticaMensile.model().STATISTICA_BASE.AZIONE.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaMensile.getStatisticaBase().getServizioApplicativo(),StatisticaMensile.model().STATISTICA_BASE.SERVIZIO_APPLICATIVO.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaMensile.getStatisticaBase().getTrasportoMittente(),StatisticaMensile.model().STATISTICA_BASE.TRASPORTO_MITTENTE.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaMensile.getStatisticaBase().getTokenIssuer(),StatisticaMensile.model().STATISTICA_BASE.TOKEN_ISSUER.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaMensile.getStatisticaBase().getTokenClientId(),StatisticaMensile.model().STATISTICA_BASE.TOKEN_CLIENT_ID.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaMensile.getStatisticaBase().getTokenSubject(),StatisticaMensile.model().STATISTICA_BASE.TOKEN_SUBJECT.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaMensile.getStatisticaBase().getTokenUsername(),StatisticaMensile.model().STATISTICA_BASE.TOKEN_USERNAME.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaMensile.getStatisticaBase().getTokenMail(),StatisticaMensile.model().STATISTICA_BASE.TOKEN_MAIL.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaMensile.getStatisticaBase().getEsito(),StatisticaMensile.model().STATISTICA_BASE.ESITO.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaMensile.getStatisticaBase().getEsitoContesto(),StatisticaMensile.model().STATISTICA_BASE.ESITO_CONTESTO.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaMensile.getStatisticaBase().getClientAddress(),StatisticaMensile.model().STATISTICA_BASE.CLIENT_ADDRESS.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaMensile.getStatisticaBase().getGruppi(),StatisticaMensile.model().STATISTICA_BASE.GRUPPI.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaMensile.getStatisticaBase().getUriApi(),StatisticaMensile.model().STATISTICA_BASE.URI_API.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaMensile.getStatisticaBase().getClusterId(),StatisticaMensile.model().STATISTICA_BASE.CLUSTER_ID.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaMensile.getStatisticaBase().getNumeroTransazioni(),StatisticaMensile.model().STATISTICA_BASE.NUMERO_TRANSAZIONI.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaMensile.getStatisticaBase().getDimensioniBytesBandaComplessiva(),StatisticaMensile.model().STATISTICA_BASE.DIMENSIONI_BYTES_BANDA_COMPLESSIVA.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaMensile.getStatisticaBase().getDimensioniBytesBandaInterna(),StatisticaMensile.model().STATISTICA_BASE.DIMENSIONI_BYTES_BANDA_INTERNA.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaMensile.getStatisticaBase().getDimensioniBytesBandaEsterna(),StatisticaMensile.model().STATISTICA_BASE.DIMENSIONI_BYTES_BANDA_ESTERNA.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaMensile.getStatisticaBase().getLatenzaTotale(),StatisticaMensile.model().STATISTICA_BASE.LATENZA_TOTALE.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaMensile.getStatisticaBase().getLatenzaPorta(),StatisticaMensile.model().STATISTICA_BASE.LATENZA_PORTA.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaMensile.getStatisticaBase().getLatenzaServizio(),StatisticaMensile.model().STATISTICA_BASE.LATENZA_SERVIZIO.getFieldType())
		);
		statisticaMensile.setId(id);

		// for statisticaMensile
		for (int i = 0; i < statisticaMensile.getStatisticaMensileContenutiList().size(); i++) {


			// Object statisticaMensile.getStatisticaMensileContenutiList().get(i)
			ISQLQueryObject sqlQueryObjectInsert_statisticaMensileContenuti = sqlQueryObjectInsert.newSQLQueryObject();
			sqlQueryObjectInsert_statisticaMensileContenuti.addInsertTable(this.getStatisticaMensileFieldConverter().toTable(StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI));
			sqlQueryObjectInsert_statisticaMensileContenuti.addInsertField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.DATA,false),"?");
			sqlQueryObjectInsert_statisticaMensileContenuti.addInsertField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.RISORSA_NOME,false),"?");
			sqlQueryObjectInsert_statisticaMensileContenuti.addInsertField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.RISORSA_VALORE,false),"?");
			sqlQueryObjectInsert_statisticaMensileContenuti.addInsertField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.FILTRO_NOME_1,false),"?");
			sqlQueryObjectInsert_statisticaMensileContenuti.addInsertField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.FILTRO_VALORE_1,false),"?");
			sqlQueryObjectInsert_statisticaMensileContenuti.addInsertField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.FILTRO_NOME_2,false),"?");
			sqlQueryObjectInsert_statisticaMensileContenuti.addInsertField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.FILTRO_VALORE_2,false),"?");
			sqlQueryObjectInsert_statisticaMensileContenuti.addInsertField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.FILTRO_NOME_3,false),"?");
			sqlQueryObjectInsert_statisticaMensileContenuti.addInsertField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.FILTRO_VALORE_3,false),"?");
			sqlQueryObjectInsert_statisticaMensileContenuti.addInsertField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.FILTRO_NOME_4,false),"?");
			sqlQueryObjectInsert_statisticaMensileContenuti.addInsertField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.FILTRO_VALORE_4,false),"?");
			sqlQueryObjectInsert_statisticaMensileContenuti.addInsertField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.FILTRO_NOME_5,false),"?");
			sqlQueryObjectInsert_statisticaMensileContenuti.addInsertField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.FILTRO_VALORE_5,false),"?");
			sqlQueryObjectInsert_statisticaMensileContenuti.addInsertField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.FILTRO_NOME_6,false),"?");
			sqlQueryObjectInsert_statisticaMensileContenuti.addInsertField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.FILTRO_VALORE_6,false),"?");
			sqlQueryObjectInsert_statisticaMensileContenuti.addInsertField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.FILTRO_NOME_7,false),"?");
			sqlQueryObjectInsert_statisticaMensileContenuti.addInsertField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.FILTRO_VALORE_7,false),"?");
			sqlQueryObjectInsert_statisticaMensileContenuti.addInsertField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.FILTRO_NOME_8,false),"?");
			sqlQueryObjectInsert_statisticaMensileContenuti.addInsertField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.FILTRO_VALORE_8,false),"?");
			sqlQueryObjectInsert_statisticaMensileContenuti.addInsertField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.FILTRO_NOME_9,false),"?");
			sqlQueryObjectInsert_statisticaMensileContenuti.addInsertField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.FILTRO_VALORE_9,false),"?");
			sqlQueryObjectInsert_statisticaMensileContenuti.addInsertField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.FILTRO_NOME_10,false),"?");
			sqlQueryObjectInsert_statisticaMensileContenuti.addInsertField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.FILTRO_VALORE_10,false),"?");
			sqlQueryObjectInsert_statisticaMensileContenuti.addInsertField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.NUMERO_TRANSAZIONI,false),"?");
			sqlQueryObjectInsert_statisticaMensileContenuti.addInsertField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.DIMENSIONI_BYTES_BANDA_COMPLESSIVA,false),"?");
			sqlQueryObjectInsert_statisticaMensileContenuti.addInsertField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.DIMENSIONI_BYTES_BANDA_INTERNA,false),"?");
			sqlQueryObjectInsert_statisticaMensileContenuti.addInsertField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.DIMENSIONI_BYTES_BANDA_ESTERNA,false),"?");
			sqlQueryObjectInsert_statisticaMensileContenuti.addInsertField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.LATENZA_TOTALE,false),"?");
			sqlQueryObjectInsert_statisticaMensileContenuti.addInsertField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.LATENZA_PORTA,false),"?");
			sqlQueryObjectInsert_statisticaMensileContenuti.addInsertField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.LATENZA_SERVIZIO,false),"?");
			sqlQueryObjectInsert_statisticaMensileContenuti.addInsertField("id_stat","?");

			// Insert statisticaMensile.getStatisticaMensileContenutiList().get(i)
			org.openspcoop2.utils.jdbc.IKeyGeneratorObject keyGenerator_statisticaMensileContenuti = this.getStatisticaMensileFetch().getKeyGeneratorObject(StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI);
			long id_statisticaMensileContenuti = jdbcUtilities.insertAndReturnGeneratedKey(sqlQueryObjectInsert_statisticaMensileContenuti, keyGenerator_statisticaMensileContenuti, jdbcProperties.isShowSql(),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaMensile.getStatisticaMensileContenutiList().get(i).getData(),StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.DATA.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaMensile.getStatisticaMensileContenutiList().get(i).getRisorsaNome(),StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.RISORSA_NOME.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaMensile.getStatisticaMensileContenutiList().get(i).getRisorsaValore(),StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.RISORSA_VALORE.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaMensile.getStatisticaMensileContenutiList().get(i).getFiltroNome1(),StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.FILTRO_NOME_1.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaMensile.getStatisticaMensileContenutiList().get(i).getFiltroValore1(),StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.FILTRO_VALORE_1.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaMensile.getStatisticaMensileContenutiList().get(i).getFiltroNome2(),StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.FILTRO_NOME_2.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaMensile.getStatisticaMensileContenutiList().get(i).getFiltroValore2(),StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.FILTRO_VALORE_2.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaMensile.getStatisticaMensileContenutiList().get(i).getFiltroNome3(),StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.FILTRO_NOME_3.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaMensile.getStatisticaMensileContenutiList().get(i).getFiltroValore3(),StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.FILTRO_VALORE_3.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaMensile.getStatisticaMensileContenutiList().get(i).getFiltroNome4(),StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.FILTRO_NOME_4.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaMensile.getStatisticaMensileContenutiList().get(i).getFiltroValore4(),StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.FILTRO_VALORE_4.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaMensile.getStatisticaMensileContenutiList().get(i).getFiltroNome5(),StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.FILTRO_NOME_5.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaMensile.getStatisticaMensileContenutiList().get(i).getFiltroValore5(),StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.FILTRO_VALORE_5.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaMensile.getStatisticaMensileContenutiList().get(i).getFiltroNome6(),StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.FILTRO_NOME_6.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaMensile.getStatisticaMensileContenutiList().get(i).getFiltroValore6(),StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.FILTRO_VALORE_6.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaMensile.getStatisticaMensileContenutiList().get(i).getFiltroNome7(),StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.FILTRO_NOME_7.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaMensile.getStatisticaMensileContenutiList().get(i).getFiltroValore7(),StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.FILTRO_VALORE_7.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaMensile.getStatisticaMensileContenutiList().get(i).getFiltroNome8(),StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.FILTRO_NOME_8.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaMensile.getStatisticaMensileContenutiList().get(i).getFiltroValore8(),StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.FILTRO_VALORE_8.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaMensile.getStatisticaMensileContenutiList().get(i).getFiltroNome9(),StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.FILTRO_NOME_9.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaMensile.getStatisticaMensileContenutiList().get(i).getFiltroValore9(),StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.FILTRO_VALORE_9.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaMensile.getStatisticaMensileContenutiList().get(i).getFiltroNome10(),StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.FILTRO_NOME_10.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaMensile.getStatisticaMensileContenutiList().get(i).getFiltroValore10(),StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.FILTRO_VALORE_10.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaMensile.getStatisticaMensileContenutiList().get(i).getNumeroTransazioni(),StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.NUMERO_TRANSAZIONI.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaMensile.getStatisticaMensileContenutiList().get(i).getDimensioniBytesBandaComplessiva(),StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.DIMENSIONI_BYTES_BANDA_COMPLESSIVA.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaMensile.getStatisticaMensileContenutiList().get(i).getDimensioniBytesBandaInterna(),StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.DIMENSIONI_BYTES_BANDA_INTERNA.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaMensile.getStatisticaMensileContenutiList().get(i).getDimensioniBytesBandaEsterna(),StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.DIMENSIONI_BYTES_BANDA_ESTERNA.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaMensile.getStatisticaMensileContenutiList().get(i).getLatenzaTotale(),StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.LATENZA_TOTALE.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaMensile.getStatisticaMensileContenutiList().get(i).getLatenzaPorta(),StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.LATENZA_PORTA.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaMensile.getStatisticaMensileContenutiList().get(i).getLatenzaServizio(),StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.LATENZA_SERVIZIO.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(Long.valueOf(id),Long.class)
			);
			statisticaMensile.getStatisticaMensileContenutiList().get(i).setId(id_statisticaMensileContenuti);
		} // fine for 

		
	}

	@Override
	public void update(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, StatisticaMensile statisticaMensile, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		
		Long tableId = statisticaMensile.getId();
		if(tableId==null || tableId<=0){
			throw new Exception("Retrieve tableId failed");
		}

		this.update(jdbcProperties, log, connection, sqlQueryObject, tableId, statisticaMensile, idMappingResolutionBehaviour);
	}
	@Override
	public void update(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, StatisticaMensile statisticaMensile, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException, NotImplementedException, ServiceException, Exception {
	
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);
		
		ISQLQueryObject sqlQueryObjectInsert = sqlQueryObject.newSQLQueryObject();
		ISQLQueryObject sqlQueryObjectDelete = sqlQueryObjectInsert.newSQLQueryObject();
		ISQLQueryObject sqlQueryObjectGet = sqlQueryObjectDelete.newSQLQueryObject();
		ISQLQueryObject sqlQueryObjectUpdate = sqlQueryObjectGet.newSQLQueryObject();
		

		Statistica statisticaMensile_statisticaBase = statisticaMensile.getStatisticaBase();

		// Object statisticaMensile_statisticaBase
		sqlQueryObjectUpdate.setANDLogicOperator(true);
		sqlQueryObjectUpdate.addUpdateTable(this.getStatisticaMensileFieldConverter().toTable(StatisticaMensile.model().STATISTICA_BASE));
		boolean isUpdate = true;
		java.util.List<JDBCObject> lstObjects = new java.util.ArrayList<JDBCObject>();
		sqlQueryObjectUpdate.addUpdateField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_BASE.DATA,false), "?");
		lstObjects.add(new JDBCObject(statisticaMensile_statisticaBase.getData(), StatisticaMensile.model().STATISTICA_BASE.DATA.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_BASE.STATO_RECORD,false), "?");
		lstObjects.add(new JDBCObject(statisticaMensile_statisticaBase.getStatoRecord(), StatisticaMensile.model().STATISTICA_BASE.STATO_RECORD.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_BASE.ID_PORTA,false), "?");
		lstObjects.add(new JDBCObject(statisticaMensile_statisticaBase.getIdPorta(), StatisticaMensile.model().STATISTICA_BASE.ID_PORTA.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_BASE.TIPO_PORTA,false), "?");
		lstObjects.add(new JDBCObject(statisticaMensile_statisticaBase.getTipoPorta(), StatisticaMensile.model().STATISTICA_BASE.TIPO_PORTA.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_BASE.TIPO_MITTENTE,false), "?");
		lstObjects.add(new JDBCObject(statisticaMensile_statisticaBase.getTipoMittente(), StatisticaMensile.model().STATISTICA_BASE.TIPO_MITTENTE.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_BASE.MITTENTE,false), "?");
		lstObjects.add(new JDBCObject(statisticaMensile_statisticaBase.getMittente(), StatisticaMensile.model().STATISTICA_BASE.MITTENTE.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_BASE.TIPO_DESTINATARIO,false), "?");
		lstObjects.add(new JDBCObject(statisticaMensile_statisticaBase.getTipoDestinatario(), StatisticaMensile.model().STATISTICA_BASE.TIPO_DESTINATARIO.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_BASE.DESTINATARIO,false), "?");
		lstObjects.add(new JDBCObject(statisticaMensile_statisticaBase.getDestinatario(), StatisticaMensile.model().STATISTICA_BASE.DESTINATARIO.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_BASE.TIPO_SERVIZIO,false), "?");
		lstObjects.add(new JDBCObject(statisticaMensile_statisticaBase.getTipoServizio(), StatisticaMensile.model().STATISTICA_BASE.TIPO_SERVIZIO.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_BASE.SERVIZIO,false), "?");
		lstObjects.add(new JDBCObject(statisticaMensile_statisticaBase.getServizio(), StatisticaMensile.model().STATISTICA_BASE.SERVIZIO.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_BASE.VERSIONE_SERVIZIO,false), "?");
		lstObjects.add(new JDBCObject(statisticaMensile_statisticaBase.getVersioneServizio(), StatisticaMensile.model().STATISTICA_BASE.VERSIONE_SERVIZIO.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_BASE.AZIONE,false), "?");
		lstObjects.add(new JDBCObject(statisticaMensile_statisticaBase.getAzione(), StatisticaMensile.model().STATISTICA_BASE.AZIONE.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_BASE.SERVIZIO_APPLICATIVO,false), "?");
		lstObjects.add(new JDBCObject(statisticaMensile_statisticaBase.getServizioApplicativo(), StatisticaMensile.model().STATISTICA_BASE.SERVIZIO_APPLICATIVO.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_BASE.TRASPORTO_MITTENTE,false), "?");
		lstObjects.add(new JDBCObject(statisticaMensile_statisticaBase.getTrasportoMittente(), StatisticaMensile.model().STATISTICA_BASE.TRASPORTO_MITTENTE.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_BASE.TOKEN_ISSUER,false), "?");
		lstObjects.add(new JDBCObject(statisticaMensile_statisticaBase.getTokenIssuer(), StatisticaMensile.model().STATISTICA_BASE.TOKEN_ISSUER.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_BASE.TOKEN_CLIENT_ID,false), "?");
		lstObjects.add(new JDBCObject(statisticaMensile_statisticaBase.getTokenClientId(), StatisticaMensile.model().STATISTICA_BASE.TOKEN_CLIENT_ID.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_BASE.TOKEN_SUBJECT,false), "?");
		lstObjects.add(new JDBCObject(statisticaMensile_statisticaBase.getTokenSubject(), StatisticaMensile.model().STATISTICA_BASE.TOKEN_SUBJECT.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_BASE.TOKEN_USERNAME,false), "?");
		lstObjects.add(new JDBCObject(statisticaMensile_statisticaBase.getTokenUsername(), StatisticaMensile.model().STATISTICA_BASE.TOKEN_USERNAME.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_BASE.TOKEN_MAIL,false), "?");
		lstObjects.add(new JDBCObject(statisticaMensile_statisticaBase.getTokenMail(), StatisticaMensile.model().STATISTICA_BASE.TOKEN_MAIL.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_BASE.ESITO,false), "?");
		lstObjects.add(new JDBCObject(statisticaMensile_statisticaBase.getEsito(), StatisticaMensile.model().STATISTICA_BASE.ESITO.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_BASE.ESITO_CONTESTO,false), "?");
		lstObjects.add(new JDBCObject(statisticaMensile_statisticaBase.getEsitoContesto(), StatisticaMensile.model().STATISTICA_BASE.ESITO_CONTESTO.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_BASE.CLIENT_ADDRESS,false), "?");
		lstObjects.add(new JDBCObject(statisticaMensile_statisticaBase.getClientAddress(), StatisticaMensile.model().STATISTICA_BASE.CLIENT_ADDRESS.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_BASE.GRUPPI,false), "?");
		lstObjects.add(new JDBCObject(statisticaMensile_statisticaBase.getGruppi(), StatisticaMensile.model().STATISTICA_BASE.GRUPPI.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_BASE.URI_API,false), "?");
		lstObjects.add(new JDBCObject(statisticaMensile_statisticaBase.getUriApi(), StatisticaMensile.model().STATISTICA_BASE.URI_API.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_BASE.CLUSTER_ID,false), "?");
		lstObjects.add(new JDBCObject(statisticaMensile_statisticaBase.getClusterId(), StatisticaMensile.model().STATISTICA_BASE.CLUSTER_ID.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_BASE.NUMERO_TRANSAZIONI,false), "?");
		lstObjects.add(new JDBCObject(statisticaMensile_statisticaBase.getNumeroTransazioni(), StatisticaMensile.model().STATISTICA_BASE.NUMERO_TRANSAZIONI.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_BASE.DIMENSIONI_BYTES_BANDA_COMPLESSIVA,false), "?");
		lstObjects.add(new JDBCObject(statisticaMensile_statisticaBase.getDimensioniBytesBandaComplessiva(), StatisticaMensile.model().STATISTICA_BASE.DIMENSIONI_BYTES_BANDA_COMPLESSIVA.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_BASE.DIMENSIONI_BYTES_BANDA_INTERNA,false), "?");
		lstObjects.add(new JDBCObject(statisticaMensile_statisticaBase.getDimensioniBytesBandaInterna(), StatisticaMensile.model().STATISTICA_BASE.DIMENSIONI_BYTES_BANDA_INTERNA.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_BASE.DIMENSIONI_BYTES_BANDA_ESTERNA,false), "?");
		lstObjects.add(new JDBCObject(statisticaMensile_statisticaBase.getDimensioniBytesBandaEsterna(), StatisticaMensile.model().STATISTICA_BASE.DIMENSIONI_BYTES_BANDA_ESTERNA.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_BASE.LATENZA_TOTALE,false), "?");
		lstObjects.add(new JDBCObject(statisticaMensile_statisticaBase.getLatenzaTotale(), StatisticaMensile.model().STATISTICA_BASE.LATENZA_TOTALE.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_BASE.LATENZA_PORTA,false), "?");
		lstObjects.add(new JDBCObject(statisticaMensile_statisticaBase.getLatenzaPorta(), StatisticaMensile.model().STATISTICA_BASE.LATENZA_PORTA.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_BASE.LATENZA_SERVIZIO,false), "?");
		lstObjects.add(new JDBCObject(statisticaMensile_statisticaBase.getLatenzaServizio(), StatisticaMensile.model().STATISTICA_BASE.LATENZA_SERVIZIO.getFieldType()));
		sqlQueryObjectUpdate.addWhereCondition("id=?");
		lstObjects.add(new JDBCObject(tableId, Long.class));

		if(isUpdate) {
			// Update statisticaMensile
			jdbcUtilities.executeUpdate(sqlQueryObjectUpdate.createSQLUpdate(), jdbcProperties.isShowSql(), 
				lstObjects.toArray(new JDBCObject[]{}));
		}
		// for statisticaMensile_statisticaMensileContenuti

		java.util.List<Long> ids_statisticaMensileContenuti_da_non_eliminare = new java.util.ArrayList<Long>();
		for (Object statisticaMensile_statisticaMensileContenuti_object : statisticaMensile.getStatisticaMensileContenutiList()) {
			StatisticaContenuti statisticaMensile_statisticaMensileContenuti = (StatisticaContenuti) statisticaMensile_statisticaMensileContenuti_object;
			if(statisticaMensile_statisticaMensileContenuti.getId() == null || statisticaMensile_statisticaMensileContenuti.getId().longValue() <= 0) {

				long id = statisticaMensile.getId();			

				// Object statisticaMensile_statisticaMensileContenuti
				ISQLQueryObject sqlQueryObjectInsert_statisticaMensileContenuti = sqlQueryObjectInsert.newSQLQueryObject();
				sqlQueryObjectInsert_statisticaMensileContenuti.addInsertTable(this.getStatisticaMensileFieldConverter().toTable(StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI));
				sqlQueryObjectInsert_statisticaMensileContenuti.addInsertField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.DATA,false),"?");
				sqlQueryObjectInsert_statisticaMensileContenuti.addInsertField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.RISORSA_NOME,false),"?");
				sqlQueryObjectInsert_statisticaMensileContenuti.addInsertField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.RISORSA_VALORE,false),"?");
				sqlQueryObjectInsert_statisticaMensileContenuti.addInsertField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.FILTRO_NOME_1,false),"?");
				sqlQueryObjectInsert_statisticaMensileContenuti.addInsertField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.FILTRO_VALORE_1,false),"?");
				sqlQueryObjectInsert_statisticaMensileContenuti.addInsertField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.FILTRO_NOME_2,false),"?");
				sqlQueryObjectInsert_statisticaMensileContenuti.addInsertField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.FILTRO_VALORE_2,false),"?");
				sqlQueryObjectInsert_statisticaMensileContenuti.addInsertField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.FILTRO_NOME_3,false),"?");
				sqlQueryObjectInsert_statisticaMensileContenuti.addInsertField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.FILTRO_VALORE_3,false),"?");
				sqlQueryObjectInsert_statisticaMensileContenuti.addInsertField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.FILTRO_NOME_4,false),"?");
				sqlQueryObjectInsert_statisticaMensileContenuti.addInsertField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.FILTRO_VALORE_4,false),"?");
				sqlQueryObjectInsert_statisticaMensileContenuti.addInsertField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.FILTRO_NOME_5,false),"?");
				sqlQueryObjectInsert_statisticaMensileContenuti.addInsertField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.FILTRO_VALORE_5,false),"?");
				sqlQueryObjectInsert_statisticaMensileContenuti.addInsertField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.FILTRO_NOME_6,false),"?");
				sqlQueryObjectInsert_statisticaMensileContenuti.addInsertField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.FILTRO_VALORE_6,false),"?");
				sqlQueryObjectInsert_statisticaMensileContenuti.addInsertField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.FILTRO_NOME_7,false),"?");
				sqlQueryObjectInsert_statisticaMensileContenuti.addInsertField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.FILTRO_VALORE_7,false),"?");
				sqlQueryObjectInsert_statisticaMensileContenuti.addInsertField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.FILTRO_NOME_8,false),"?");
				sqlQueryObjectInsert_statisticaMensileContenuti.addInsertField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.FILTRO_VALORE_8,false),"?");
				sqlQueryObjectInsert_statisticaMensileContenuti.addInsertField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.FILTRO_NOME_9,false),"?");
				sqlQueryObjectInsert_statisticaMensileContenuti.addInsertField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.FILTRO_VALORE_9,false),"?");
				sqlQueryObjectInsert_statisticaMensileContenuti.addInsertField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.FILTRO_NOME_10,false),"?");
				sqlQueryObjectInsert_statisticaMensileContenuti.addInsertField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.FILTRO_VALORE_10,false),"?");
				sqlQueryObjectInsert_statisticaMensileContenuti.addInsertField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.NUMERO_TRANSAZIONI,false),"?");
				sqlQueryObjectInsert_statisticaMensileContenuti.addInsertField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.DIMENSIONI_BYTES_BANDA_COMPLESSIVA,false),"?");
				sqlQueryObjectInsert_statisticaMensileContenuti.addInsertField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.DIMENSIONI_BYTES_BANDA_INTERNA,false),"?");
				sqlQueryObjectInsert_statisticaMensileContenuti.addInsertField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.DIMENSIONI_BYTES_BANDA_ESTERNA,false),"?");
				sqlQueryObjectInsert_statisticaMensileContenuti.addInsertField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.LATENZA_TOTALE,false),"?");
				sqlQueryObjectInsert_statisticaMensileContenuti.addInsertField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.LATENZA_PORTA,false),"?");
				sqlQueryObjectInsert_statisticaMensileContenuti.addInsertField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.LATENZA_SERVIZIO,false),"?");
				sqlQueryObjectInsert_statisticaMensileContenuti.addInsertField("id_stat","?");

				// Insert statisticaMensile_statisticaMensileContenuti
				org.openspcoop2.utils.jdbc.IKeyGeneratorObject keyGenerator_statisticaMensileContenuti = this.getStatisticaMensileFetch().getKeyGeneratorObject(StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI);
				long id_statisticaMensileContenuti = jdbcUtilities.insertAndReturnGeneratedKey(sqlQueryObjectInsert_statisticaMensileContenuti, keyGenerator_statisticaMensileContenuti, jdbcProperties.isShowSql(),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaMensile_statisticaMensileContenuti.getData(),StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.DATA.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaMensile_statisticaMensileContenuti.getRisorsaNome(),StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.RISORSA_NOME.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaMensile_statisticaMensileContenuti.getRisorsaValore(),StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.RISORSA_VALORE.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaMensile_statisticaMensileContenuti.getFiltroNome1(),StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.FILTRO_NOME_1.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaMensile_statisticaMensileContenuti.getFiltroValore1(),StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.FILTRO_VALORE_1.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaMensile_statisticaMensileContenuti.getFiltroNome2(),StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.FILTRO_NOME_2.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaMensile_statisticaMensileContenuti.getFiltroValore2(),StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.FILTRO_VALORE_2.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaMensile_statisticaMensileContenuti.getFiltroNome3(),StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.FILTRO_NOME_3.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaMensile_statisticaMensileContenuti.getFiltroValore3(),StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.FILTRO_VALORE_3.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaMensile_statisticaMensileContenuti.getFiltroNome4(),StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.FILTRO_NOME_4.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaMensile_statisticaMensileContenuti.getFiltroValore4(),StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.FILTRO_VALORE_4.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaMensile_statisticaMensileContenuti.getFiltroNome5(),StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.FILTRO_NOME_5.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaMensile_statisticaMensileContenuti.getFiltroValore5(),StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.FILTRO_VALORE_5.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaMensile_statisticaMensileContenuti.getFiltroNome6(),StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.FILTRO_NOME_6.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaMensile_statisticaMensileContenuti.getFiltroValore6(),StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.FILTRO_VALORE_6.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaMensile_statisticaMensileContenuti.getFiltroNome7(),StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.FILTRO_NOME_7.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaMensile_statisticaMensileContenuti.getFiltroValore7(),StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.FILTRO_VALORE_7.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaMensile_statisticaMensileContenuti.getFiltroNome8(),StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.FILTRO_NOME_8.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaMensile_statisticaMensileContenuti.getFiltroValore8(),StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.FILTRO_VALORE_8.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaMensile_statisticaMensileContenuti.getFiltroNome9(),StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.FILTRO_NOME_9.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaMensile_statisticaMensileContenuti.getFiltroValore9(),StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.FILTRO_VALORE_9.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaMensile_statisticaMensileContenuti.getFiltroNome10(),StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.FILTRO_NOME_10.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaMensile_statisticaMensileContenuti.getFiltroValore10(),StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.FILTRO_VALORE_10.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaMensile_statisticaMensileContenuti.getNumeroTransazioni(),StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.NUMERO_TRANSAZIONI.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaMensile_statisticaMensileContenuti.getDimensioniBytesBandaComplessiva(),StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.DIMENSIONI_BYTES_BANDA_COMPLESSIVA.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaMensile_statisticaMensileContenuti.getDimensioniBytesBandaInterna(),StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.DIMENSIONI_BYTES_BANDA_INTERNA.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaMensile_statisticaMensileContenuti.getDimensioniBytesBandaEsterna(),StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.DIMENSIONI_BYTES_BANDA_ESTERNA.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaMensile_statisticaMensileContenuti.getLatenzaTotale(),StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.LATENZA_TOTALE.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaMensile_statisticaMensileContenuti.getLatenzaPorta(),StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.LATENZA_PORTA.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaMensile_statisticaMensileContenuti.getLatenzaServizio(),StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.LATENZA_SERVIZIO.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(Long.valueOf(id),Long.class)
				);
				statisticaMensile_statisticaMensileContenuti.setId(id_statisticaMensileContenuti);

				ids_statisticaMensileContenuti_da_non_eliminare.add(statisticaMensile_statisticaMensileContenuti.getId());
			} else {


				// Object statisticaMensile_statisticaMensileContenuti
				ISQLQueryObject sqlQueryObjectUpdate_statisticaMensileContenuti = sqlQueryObjectUpdate.newSQLQueryObject();
				sqlQueryObjectUpdate_statisticaMensileContenuti.setANDLogicOperator(true);
				sqlQueryObjectUpdate_statisticaMensileContenuti.addUpdateTable(this.getStatisticaMensileFieldConverter().toTable(StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI));
				boolean isUpdate_statisticaMensileContenuti = true;
				java.util.List<JDBCObject> lstObjects_statisticaMensileContenuti = new java.util.ArrayList<JDBCObject>();
				sqlQueryObjectUpdate_statisticaMensileContenuti.addUpdateField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.DATA,false), "?");
				lstObjects_statisticaMensileContenuti.add(new JDBCObject(statisticaMensile_statisticaMensileContenuti.getData(), StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.DATA.getFieldType()));
				sqlQueryObjectUpdate_statisticaMensileContenuti.addUpdateField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.RISORSA_NOME,false), "?");
				lstObjects_statisticaMensileContenuti.add(new JDBCObject(statisticaMensile_statisticaMensileContenuti.getRisorsaNome(), StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.RISORSA_NOME.getFieldType()));
				sqlQueryObjectUpdate_statisticaMensileContenuti.addUpdateField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.RISORSA_VALORE,false), "?");
				lstObjects_statisticaMensileContenuti.add(new JDBCObject(statisticaMensile_statisticaMensileContenuti.getRisorsaValore(), StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.RISORSA_VALORE.getFieldType()));
				sqlQueryObjectUpdate_statisticaMensileContenuti.addUpdateField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.FILTRO_NOME_1,false), "?");
				lstObjects_statisticaMensileContenuti.add(new JDBCObject(statisticaMensile_statisticaMensileContenuti.getFiltroNome1(), StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.FILTRO_NOME_1.getFieldType()));
				sqlQueryObjectUpdate_statisticaMensileContenuti.addUpdateField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.FILTRO_VALORE_1,false), "?");
				lstObjects_statisticaMensileContenuti.add(new JDBCObject(statisticaMensile_statisticaMensileContenuti.getFiltroValore1(), StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.FILTRO_VALORE_1.getFieldType()));
				sqlQueryObjectUpdate_statisticaMensileContenuti.addUpdateField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.FILTRO_NOME_2,false), "?");
				lstObjects_statisticaMensileContenuti.add(new JDBCObject(statisticaMensile_statisticaMensileContenuti.getFiltroNome2(), StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.FILTRO_NOME_2.getFieldType()));
				sqlQueryObjectUpdate_statisticaMensileContenuti.addUpdateField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.FILTRO_VALORE_2,false), "?");
				lstObjects_statisticaMensileContenuti.add(new JDBCObject(statisticaMensile_statisticaMensileContenuti.getFiltroValore2(), StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.FILTRO_VALORE_2.getFieldType()));
				sqlQueryObjectUpdate_statisticaMensileContenuti.addUpdateField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.FILTRO_NOME_3,false), "?");
				lstObjects_statisticaMensileContenuti.add(new JDBCObject(statisticaMensile_statisticaMensileContenuti.getFiltroNome3(), StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.FILTRO_NOME_3.getFieldType()));
				sqlQueryObjectUpdate_statisticaMensileContenuti.addUpdateField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.FILTRO_VALORE_3,false), "?");
				lstObjects_statisticaMensileContenuti.add(new JDBCObject(statisticaMensile_statisticaMensileContenuti.getFiltroValore3(), StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.FILTRO_VALORE_3.getFieldType()));
				sqlQueryObjectUpdate_statisticaMensileContenuti.addUpdateField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.FILTRO_NOME_4,false), "?");
				lstObjects_statisticaMensileContenuti.add(new JDBCObject(statisticaMensile_statisticaMensileContenuti.getFiltroNome4(), StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.FILTRO_NOME_4.getFieldType()));
				sqlQueryObjectUpdate_statisticaMensileContenuti.addUpdateField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.FILTRO_VALORE_4,false), "?");
				lstObjects_statisticaMensileContenuti.add(new JDBCObject(statisticaMensile_statisticaMensileContenuti.getFiltroValore4(), StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.FILTRO_VALORE_4.getFieldType()));
				sqlQueryObjectUpdate_statisticaMensileContenuti.addUpdateField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.FILTRO_NOME_5,false), "?");
				lstObjects_statisticaMensileContenuti.add(new JDBCObject(statisticaMensile_statisticaMensileContenuti.getFiltroNome5(), StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.FILTRO_NOME_5.getFieldType()));
				sqlQueryObjectUpdate_statisticaMensileContenuti.addUpdateField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.FILTRO_VALORE_5,false), "?");
				lstObjects_statisticaMensileContenuti.add(new JDBCObject(statisticaMensile_statisticaMensileContenuti.getFiltroValore5(), StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.FILTRO_VALORE_5.getFieldType()));
				sqlQueryObjectUpdate_statisticaMensileContenuti.addUpdateField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.FILTRO_NOME_6,false), "?");
				lstObjects_statisticaMensileContenuti.add(new JDBCObject(statisticaMensile_statisticaMensileContenuti.getFiltroNome6(), StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.FILTRO_NOME_6.getFieldType()));
				sqlQueryObjectUpdate_statisticaMensileContenuti.addUpdateField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.FILTRO_VALORE_6,false), "?");
				lstObjects_statisticaMensileContenuti.add(new JDBCObject(statisticaMensile_statisticaMensileContenuti.getFiltroValore6(), StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.FILTRO_VALORE_6.getFieldType()));
				sqlQueryObjectUpdate_statisticaMensileContenuti.addUpdateField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.FILTRO_NOME_7,false), "?");
				lstObjects_statisticaMensileContenuti.add(new JDBCObject(statisticaMensile_statisticaMensileContenuti.getFiltroNome7(), StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.FILTRO_NOME_7.getFieldType()));
				sqlQueryObjectUpdate_statisticaMensileContenuti.addUpdateField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.FILTRO_VALORE_7,false), "?");
				lstObjects_statisticaMensileContenuti.add(new JDBCObject(statisticaMensile_statisticaMensileContenuti.getFiltroValore7(), StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.FILTRO_VALORE_7.getFieldType()));
				sqlQueryObjectUpdate_statisticaMensileContenuti.addUpdateField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.FILTRO_NOME_8,false), "?");
				lstObjects_statisticaMensileContenuti.add(new JDBCObject(statisticaMensile_statisticaMensileContenuti.getFiltroNome8(), StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.FILTRO_NOME_8.getFieldType()));
				sqlQueryObjectUpdate_statisticaMensileContenuti.addUpdateField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.FILTRO_VALORE_8,false), "?");
				lstObjects_statisticaMensileContenuti.add(new JDBCObject(statisticaMensile_statisticaMensileContenuti.getFiltroValore8(), StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.FILTRO_VALORE_8.getFieldType()));
				sqlQueryObjectUpdate_statisticaMensileContenuti.addUpdateField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.FILTRO_NOME_9,false), "?");
				lstObjects_statisticaMensileContenuti.add(new JDBCObject(statisticaMensile_statisticaMensileContenuti.getFiltroNome9(), StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.FILTRO_NOME_9.getFieldType()));
				sqlQueryObjectUpdate_statisticaMensileContenuti.addUpdateField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.FILTRO_VALORE_9,false), "?");
				lstObjects_statisticaMensileContenuti.add(new JDBCObject(statisticaMensile_statisticaMensileContenuti.getFiltroValore9(), StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.FILTRO_VALORE_9.getFieldType()));
				sqlQueryObjectUpdate_statisticaMensileContenuti.addUpdateField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.FILTRO_NOME_10,false), "?");
				lstObjects_statisticaMensileContenuti.add(new JDBCObject(statisticaMensile_statisticaMensileContenuti.getFiltroNome10(), StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.FILTRO_NOME_10.getFieldType()));
				sqlQueryObjectUpdate_statisticaMensileContenuti.addUpdateField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.FILTRO_VALORE_10,false), "?");
				lstObjects_statisticaMensileContenuti.add(new JDBCObject(statisticaMensile_statisticaMensileContenuti.getFiltroValore10(), StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.FILTRO_VALORE_10.getFieldType()));
				sqlQueryObjectUpdate_statisticaMensileContenuti.addUpdateField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.NUMERO_TRANSAZIONI,false), "?");
				lstObjects_statisticaMensileContenuti.add(new JDBCObject(statisticaMensile_statisticaMensileContenuti.getNumeroTransazioni(), StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.NUMERO_TRANSAZIONI.getFieldType()));
				sqlQueryObjectUpdate_statisticaMensileContenuti.addUpdateField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.DIMENSIONI_BYTES_BANDA_COMPLESSIVA,false), "?");
				lstObjects_statisticaMensileContenuti.add(new JDBCObject(statisticaMensile_statisticaMensileContenuti.getDimensioniBytesBandaComplessiva(), StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.DIMENSIONI_BYTES_BANDA_COMPLESSIVA.getFieldType()));
				sqlQueryObjectUpdate_statisticaMensileContenuti.addUpdateField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.DIMENSIONI_BYTES_BANDA_INTERNA,false), "?");
				lstObjects_statisticaMensileContenuti.add(new JDBCObject(statisticaMensile_statisticaMensileContenuti.getDimensioniBytesBandaInterna(), StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.DIMENSIONI_BYTES_BANDA_INTERNA.getFieldType()));
				sqlQueryObjectUpdate_statisticaMensileContenuti.addUpdateField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.DIMENSIONI_BYTES_BANDA_ESTERNA,false), "?");
				lstObjects_statisticaMensileContenuti.add(new JDBCObject(statisticaMensile_statisticaMensileContenuti.getDimensioniBytesBandaEsterna(), StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.DIMENSIONI_BYTES_BANDA_ESTERNA.getFieldType()));
				sqlQueryObjectUpdate_statisticaMensileContenuti.addUpdateField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.LATENZA_TOTALE,false), "?");
				lstObjects_statisticaMensileContenuti.add(new JDBCObject(statisticaMensile_statisticaMensileContenuti.getLatenzaTotale(), StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.LATENZA_TOTALE.getFieldType()));
				sqlQueryObjectUpdate_statisticaMensileContenuti.addUpdateField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.LATENZA_PORTA,false), "?");
				lstObjects_statisticaMensileContenuti.add(new JDBCObject(statisticaMensile_statisticaMensileContenuti.getLatenzaPorta(), StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.LATENZA_PORTA.getFieldType()));
				sqlQueryObjectUpdate_statisticaMensileContenuti.addUpdateField(this.getStatisticaMensileFieldConverter().toColumn(StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.LATENZA_SERVIZIO,false), "?");
				lstObjects_statisticaMensileContenuti.add(new JDBCObject(statisticaMensile_statisticaMensileContenuti.getLatenzaServizio(), StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.LATENZA_SERVIZIO.getFieldType()));
				sqlQueryObjectUpdate_statisticaMensileContenuti.addWhereCondition("id=?");
				ids_statisticaMensileContenuti_da_non_eliminare.add(statisticaMensile_statisticaMensileContenuti.getId());
				lstObjects_statisticaMensileContenuti.add(new JDBCObject(Long.valueOf(statisticaMensile_statisticaMensileContenuti.getId()),Long.class));

				if(isUpdate_statisticaMensileContenuti) {
					// Update statisticaMensile_statisticaMensileContenuti
					jdbcUtilities.executeUpdate(sqlQueryObjectUpdate_statisticaMensileContenuti.createSQLUpdate(), jdbcProperties.isShowSql(), 
						lstObjects_statisticaMensileContenuti.toArray(new JDBCObject[]{}));
				}
			}
		} // fine for statisticaMensile_statisticaMensileContenuti

		// elimino tutte le occorrenze di statisticaMensile_statisticaMensileContenuti non presenti nell'update

		ISQLQueryObject sqlQueryObjectUpdate_statisticaMensileContenuti_deleteList = sqlQueryObjectUpdate.newSQLQueryObject();
		sqlQueryObjectUpdate_statisticaMensileContenuti_deleteList.setANDLogicOperator(true);
		sqlQueryObjectUpdate_statisticaMensileContenuti_deleteList.addDeleteTable(this.getStatisticaMensileFieldConverter().toTable(StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI));
		java.util.List<JDBCObject> jdbcObjects_statisticaMensileContenuti_delete = new java.util.ArrayList<JDBCObject>();

		sqlQueryObjectUpdate_statisticaMensileContenuti_deleteList.addWhereCondition("id_stat=?");
		jdbcObjects_statisticaMensileContenuti_delete.add(new JDBCObject(statisticaMensile.getId(), Long.class));

		StringBuilder marks_statisticaMensileContenuti = new StringBuilder();
		if(ids_statisticaMensileContenuti_da_non_eliminare.size() > 0) {
			for(Long ids : ids_statisticaMensileContenuti_da_non_eliminare) {
				if(marks_statisticaMensileContenuti.length() > 0) {
					marks_statisticaMensileContenuti.append(",");
				}
				marks_statisticaMensileContenuti.append("?");
				jdbcObjects_statisticaMensileContenuti_delete.add(new JDBCObject(ids, Long.class));

			}
			sqlQueryObjectUpdate_statisticaMensileContenuti_deleteList.addWhereCondition("id NOT IN ("+marks_statisticaMensileContenuti.toString()+")");
		}

		jdbcUtilities.execute(sqlQueryObjectUpdate_statisticaMensileContenuti_deleteList.createSQLDelete(), jdbcProperties.isShowSql(), jdbcObjects_statisticaMensileContenuti_delete.toArray(new JDBCObject[]{}));



	}
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, StatisticaMensile statisticaMensile, UpdateField ... updateFields) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		
		JDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getStatisticaMensileFieldConverter().toTable(StatisticaMensile.model()), 
				this._getMapTableToPKColumn(), 
				this._getRootTablePrimaryKeyValues(jdbcProperties, log, connection, sqlQueryObject, statisticaMensile),
				this.getStatisticaMensileFieldConverter(), this, null, updateFields);
	}
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, StatisticaMensile statisticaMensile, IExpression condition, UpdateField ... updateFields) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		
		JDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getStatisticaMensileFieldConverter().toTable(StatisticaMensile.model()), 
				this._getMapTableToPKColumn(), 
				this._getRootTablePrimaryKeyValues(jdbcProperties, log, connection, sqlQueryObject, statisticaMensile),
				this.getStatisticaMensileFieldConverter(), this, condition, updateFields);
	}
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, StatisticaMensile statisticaMensile, UpdateModel ... updateModels) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		
		JDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getStatisticaMensileFieldConverter().toTable(StatisticaMensile.model()), 
				this._getMapTableToPKColumn(), 
				this._getRootTablePrimaryKeyValues(jdbcProperties, log, connection, sqlQueryObject, statisticaMensile),
				this.getStatisticaMensileFieldConverter(), this, updateModels);
	}	
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, UpdateField ... updateFields) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		java.util.List<Object> ids = new java.util.ArrayList<Object>();
		ids.add(tableId);
		JDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getStatisticaMensileFieldConverter().toTable(StatisticaMensile.model()), 
				this._getMapTableToPKColumn(), 
				ids,
				this.getStatisticaMensileFieldConverter(), this, null, updateFields);
	}
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, IExpression condition, UpdateField ... updateFields) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		java.util.List<Object> ids = new java.util.ArrayList<Object>();
		ids.add(tableId);
		JDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getStatisticaMensileFieldConverter().toTable(StatisticaMensile.model()), 
				this._getMapTableToPKColumn(), 
				ids,
				this.getStatisticaMensileFieldConverter(), this, condition, updateFields);
	}
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, UpdateModel ... updateModels) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		java.util.List<Object> ids = new java.util.ArrayList<Object>();
		ids.add(tableId);
		JDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getStatisticaMensileFieldConverter().toTable(StatisticaMensile.model()), 
				this._getMapTableToPKColumn(), 
				ids,
				this.getStatisticaMensileFieldConverter(), this, updateModels);
	}
	
	@Override
	public void updateOrCreate(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, StatisticaMensile statisticaMensile, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotImplementedException,ServiceException,Exception {
	
		Long id = statisticaMensile.getId();
		if(id != null && this.exists(jdbcProperties, log, connection, sqlQueryObject, id)) {
			this.update(jdbcProperties, log, connection, sqlQueryObject, statisticaMensile,idMappingResolutionBehaviour);		
		} else {
			this.create(jdbcProperties, log, connection, sqlQueryObject, statisticaMensile,idMappingResolutionBehaviour);
		}
		
	}
	
	@Override
	public void updateOrCreate(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, StatisticaMensile statisticaMensile, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotImplementedException,ServiceException,Exception {
		if(this.exists(jdbcProperties, log, connection, sqlQueryObject, tableId)) {
			this.update(jdbcProperties, log, connection, sqlQueryObject, tableId, statisticaMensile,idMappingResolutionBehaviour);
		} else {
			this.create(jdbcProperties, log, connection, sqlQueryObject, statisticaMensile,idMappingResolutionBehaviour);
		}
	}
	
	@Override
	public void delete(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, StatisticaMensile statisticaMensile) throws NotImplementedException,ServiceException,Exception {
		
		
		Long longId = null;
		if(statisticaMensile.getId()==null){
			throw new Exception("Parameter "+statisticaMensile.getClass().getName()+".id is null");
		}
		if(statisticaMensile.getId()<=0){
			throw new Exception("Parameter "+statisticaMensile.getClass().getName()+".id is less equals 0");
		}
		longId = statisticaMensile.getId();
		
		this._delete(jdbcProperties, log, connection, sqlQueryObject, longId);
		
	}

	private void _delete(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, Long id) throws NotImplementedException,ServiceException,Exception {
	
		if(id!=null && id.longValue()<=0){
			throw new ServiceException("Id is less equals 0");
		}
		
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);
		
		ISQLQueryObject sqlQueryObjectDelete = sqlQueryObject.newSQLQueryObject();
		

		//Recupero oggetto _statisticaMensileContenuti
		ISQLQueryObject sqlQueryObjectDelete_statisticaMensileContenuti_getToDelete = sqlQueryObjectDelete.newSQLQueryObject();
		sqlQueryObjectDelete_statisticaMensileContenuti_getToDelete.setANDLogicOperator(true);
		sqlQueryObjectDelete_statisticaMensileContenuti_getToDelete.addFromTable(this.getStatisticaMensileFieldConverter().toTable(StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI));
		sqlQueryObjectDelete_statisticaMensileContenuti_getToDelete.addWhereCondition("id_stat=?");
		java.util.List<Object> statisticaMensile_statisticaMensileContenuti_toDelete_list = jdbcUtilities.executeQuery(sqlQueryObjectDelete_statisticaMensileContenuti_getToDelete.createSQLQuery(), jdbcProperties.isShowSql(), StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI, this.getStatisticaMensileFetch(),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(Long.valueOf(id),Long.class));

		// for statisticaMensile_statisticaMensileContenuti
		for (Object statisticaMensile_statisticaMensileContenuti_object : statisticaMensile_statisticaMensileContenuti_toDelete_list) {
			StatisticaContenuti statisticaMensile_statisticaMensileContenuti = (StatisticaContenuti) statisticaMensile_statisticaMensileContenuti_object;

			// Object statisticaMensile_statisticaMensileContenuti
			ISQLQueryObject sqlQueryObjectDelete_statisticaMensileContenuti = sqlQueryObjectDelete.newSQLQueryObject();
			sqlQueryObjectDelete_statisticaMensileContenuti.setANDLogicOperator(true);
			sqlQueryObjectDelete_statisticaMensileContenuti.addDeleteTable(this.getStatisticaMensileFieldConverter().toTable(StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI));
			sqlQueryObjectDelete_statisticaMensileContenuti.addWhereCondition("id=?");

			// Delete statisticaMensile_statisticaMensileContenuti
			if(statisticaMensile_statisticaMensileContenuti != null){
				jdbcUtilities.execute(sqlQueryObjectDelete_statisticaMensileContenuti.createSQLDelete(), jdbcProperties.isShowSql(), 
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(Long.valueOf(statisticaMensile_statisticaMensileContenuti.getId()),Long.class));
			}
		} // fine for statisticaMensile_statisticaMensileContenuti

		// Object statisticaMensile_statisticaBase_toDelete
		sqlQueryObjectDelete.setANDLogicOperator(true);
		sqlQueryObjectDelete.addDeleteTable(this.getStatisticaMensileFieldConverter().toTable(StatisticaMensile.model().STATISTICA_BASE));
		if(id != null)
			sqlQueryObjectDelete.addWhereCondition("id=?");

		// Delete statisticaMensile
		jdbcUtilities.execute(sqlQueryObjectDelete.createSQLDelete(), jdbcProperties.isShowSql(), 
			new JDBCObject(id,Long.class));

	}

	
	@Override
	public NonNegativeNumber deleteAll(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject) throws NotImplementedException,ServiceException,Exception {
		
		return this.deleteAll(jdbcProperties, log, connection, sqlQueryObject, new JDBCExpression(this.getStatisticaMensileFieldConverter()));

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
