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
import org.openspcoop2.core.statistiche.StatisticaGiornaliera;
import org.openspcoop2.core.statistiche.dao.jdbc.JDBCServiceManager;

/**     
 * JDBCStatisticaGiornalieraServiceImpl
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JDBCStatisticaGiornalieraServiceImpl extends JDBCStatisticaGiornalieraServiceSearchImpl
	implements IJDBCServiceCRUDWithoutId<StatisticaGiornaliera, JDBCServiceManager> {

	@Override
	public void create(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, StatisticaGiornaliera statisticaGiornaliera, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotImplementedException,ServiceException,Exception {

		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);
		
		ISQLQueryObject sqlQueryObjectInsert = sqlQueryObject.newSQLQueryObject();
				


		// Object statisticaGiornaliera.getStatisticaBase()
		sqlQueryObjectInsert.addInsertTable(this.getStatisticaGiornalieraFieldConverter().toTable(StatisticaGiornaliera.model().STATISTICA_BASE));
		sqlQueryObjectInsert.addInsertField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_BASE.DATA,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_BASE.ID_PORTA,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_BASE.TIPO_PORTA,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_BASE.TIPO_MITTENTE,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_BASE.MITTENTE,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_BASE.TIPO_DESTINATARIO,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_BASE.DESTINATARIO,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_BASE.TIPO_SERVIZIO,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_BASE.SERVIZIO,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_BASE.VERSIONE_SERVIZIO,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_BASE.AZIONE,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_BASE.SERVIZIO_APPLICATIVO,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_BASE.TRASPORTO_MITTENTE,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_BASE.TOKEN_ISSUER,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_BASE.TOKEN_CLIENT_ID,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_BASE.TOKEN_SUBJECT,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_BASE.TOKEN_USERNAME,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_BASE.TOKEN_MAIL,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_BASE.ESITO,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_BASE.ESITO_CONTESTO,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_BASE.CLIENT_ADDRESS,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_BASE.GRUPPI,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_BASE.NUMERO_TRANSAZIONI,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_BASE.DIMENSIONI_BYTES_BANDA_COMPLESSIVA,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_BASE.DIMENSIONI_BYTES_BANDA_INTERNA,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_BASE.DIMENSIONI_BYTES_BANDA_ESTERNA,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_BASE.LATENZA_TOTALE,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_BASE.LATENZA_PORTA,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_BASE.LATENZA_SERVIZIO,false),"?");

		// Insert statisticaGiornaliera
		org.openspcoop2.utils.jdbc.IKeyGeneratorObject keyGenerator = this.getStatisticaGiornalieraFetch().getKeyGeneratorObject(StatisticaGiornaliera.model());
		long id = jdbcUtilities.insertAndReturnGeneratedKey(sqlQueryObjectInsert, keyGenerator, jdbcProperties.isShowSql(),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaGiornaliera.getStatisticaBase().getData(),StatisticaGiornaliera.model().STATISTICA_BASE.DATA.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaGiornaliera.getStatisticaBase().getIdPorta(),StatisticaGiornaliera.model().STATISTICA_BASE.ID_PORTA.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaGiornaliera.getStatisticaBase().getTipoPorta(),StatisticaGiornaliera.model().STATISTICA_BASE.TIPO_PORTA.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaGiornaliera.getStatisticaBase().getTipoMittente(),StatisticaGiornaliera.model().STATISTICA_BASE.TIPO_MITTENTE.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaGiornaliera.getStatisticaBase().getMittente(),StatisticaGiornaliera.model().STATISTICA_BASE.MITTENTE.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaGiornaliera.getStatisticaBase().getTipoDestinatario(),StatisticaGiornaliera.model().STATISTICA_BASE.TIPO_DESTINATARIO.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaGiornaliera.getStatisticaBase().getDestinatario(),StatisticaGiornaliera.model().STATISTICA_BASE.DESTINATARIO.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaGiornaliera.getStatisticaBase().getTipoServizio(),StatisticaGiornaliera.model().STATISTICA_BASE.TIPO_SERVIZIO.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaGiornaliera.getStatisticaBase().getServizio(),StatisticaGiornaliera.model().STATISTICA_BASE.SERVIZIO.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaGiornaliera.getStatisticaBase().getVersioneServizio(),StatisticaGiornaliera.model().STATISTICA_BASE.VERSIONE_SERVIZIO.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaGiornaliera.getStatisticaBase().getAzione(),StatisticaGiornaliera.model().STATISTICA_BASE.AZIONE.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaGiornaliera.getStatisticaBase().getServizioApplicativo(),StatisticaGiornaliera.model().STATISTICA_BASE.SERVIZIO_APPLICATIVO.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaGiornaliera.getStatisticaBase().getTrasportoMittente(),StatisticaGiornaliera.model().STATISTICA_BASE.TRASPORTO_MITTENTE.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaGiornaliera.getStatisticaBase().getTokenIssuer(),StatisticaGiornaliera.model().STATISTICA_BASE.TOKEN_ISSUER.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaGiornaliera.getStatisticaBase().getTokenClientId(),StatisticaGiornaliera.model().STATISTICA_BASE.TOKEN_CLIENT_ID.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaGiornaliera.getStatisticaBase().getTokenSubject(),StatisticaGiornaliera.model().STATISTICA_BASE.TOKEN_SUBJECT.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaGiornaliera.getStatisticaBase().getTokenUsername(),StatisticaGiornaliera.model().STATISTICA_BASE.TOKEN_USERNAME.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaGiornaliera.getStatisticaBase().getTokenMail(),StatisticaGiornaliera.model().STATISTICA_BASE.TOKEN_MAIL.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaGiornaliera.getStatisticaBase().getEsito(),StatisticaGiornaliera.model().STATISTICA_BASE.ESITO.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaGiornaliera.getStatisticaBase().getEsitoContesto(),StatisticaGiornaliera.model().STATISTICA_BASE.ESITO_CONTESTO.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaGiornaliera.getStatisticaBase().getClientAddress(),StatisticaGiornaliera.model().STATISTICA_BASE.CLIENT_ADDRESS.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaGiornaliera.getStatisticaBase().getGruppi(),StatisticaGiornaliera.model().STATISTICA_BASE.GRUPPI.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaGiornaliera.getStatisticaBase().getNumeroTransazioni(),StatisticaGiornaliera.model().STATISTICA_BASE.NUMERO_TRANSAZIONI.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaGiornaliera.getStatisticaBase().getDimensioniBytesBandaComplessiva(),StatisticaGiornaliera.model().STATISTICA_BASE.DIMENSIONI_BYTES_BANDA_COMPLESSIVA.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaGiornaliera.getStatisticaBase().getDimensioniBytesBandaInterna(),StatisticaGiornaliera.model().STATISTICA_BASE.DIMENSIONI_BYTES_BANDA_INTERNA.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaGiornaliera.getStatisticaBase().getDimensioniBytesBandaEsterna(),StatisticaGiornaliera.model().STATISTICA_BASE.DIMENSIONI_BYTES_BANDA_ESTERNA.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaGiornaliera.getStatisticaBase().getLatenzaTotale(),StatisticaGiornaliera.model().STATISTICA_BASE.LATENZA_TOTALE.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaGiornaliera.getStatisticaBase().getLatenzaPorta(),StatisticaGiornaliera.model().STATISTICA_BASE.LATENZA_PORTA.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaGiornaliera.getStatisticaBase().getLatenzaServizio(),StatisticaGiornaliera.model().STATISTICA_BASE.LATENZA_SERVIZIO.getFieldType())
		);
		statisticaGiornaliera.setId(id);

		// for statisticaGiornaliera
		for (int i = 0; i < statisticaGiornaliera.getStatisticaGiornalieraContenutiList().size(); i++) {


			// Object statisticaGiornaliera.getStatisticaGiornalieraContenutiList().get(i)
			ISQLQueryObject sqlQueryObjectInsert_statisticaGiornalieraContenuti = sqlQueryObjectInsert.newSQLQueryObject();
			sqlQueryObjectInsert_statisticaGiornalieraContenuti.addInsertTable(this.getStatisticaGiornalieraFieldConverter().toTable(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI));
			sqlQueryObjectInsert_statisticaGiornalieraContenuti.addInsertField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.DATA,false),"?");
			sqlQueryObjectInsert_statisticaGiornalieraContenuti.addInsertField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.RISORSA_NOME,false),"?");
			sqlQueryObjectInsert_statisticaGiornalieraContenuti.addInsertField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.RISORSA_VALORE,false),"?");
			sqlQueryObjectInsert_statisticaGiornalieraContenuti.addInsertField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_NOME_1,false),"?");
			sqlQueryObjectInsert_statisticaGiornalieraContenuti.addInsertField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_VALORE_1,false),"?");
			sqlQueryObjectInsert_statisticaGiornalieraContenuti.addInsertField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_NOME_2,false),"?");
			sqlQueryObjectInsert_statisticaGiornalieraContenuti.addInsertField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_VALORE_2,false),"?");
			sqlQueryObjectInsert_statisticaGiornalieraContenuti.addInsertField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_NOME_3,false),"?");
			sqlQueryObjectInsert_statisticaGiornalieraContenuti.addInsertField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_VALORE_3,false),"?");
			sqlQueryObjectInsert_statisticaGiornalieraContenuti.addInsertField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_NOME_4,false),"?");
			sqlQueryObjectInsert_statisticaGiornalieraContenuti.addInsertField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_VALORE_4,false),"?");
			sqlQueryObjectInsert_statisticaGiornalieraContenuti.addInsertField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_NOME_5,false),"?");
			sqlQueryObjectInsert_statisticaGiornalieraContenuti.addInsertField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_VALORE_5,false),"?");
			sqlQueryObjectInsert_statisticaGiornalieraContenuti.addInsertField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_NOME_6,false),"?");
			sqlQueryObjectInsert_statisticaGiornalieraContenuti.addInsertField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_VALORE_6,false),"?");
			sqlQueryObjectInsert_statisticaGiornalieraContenuti.addInsertField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_NOME_7,false),"?");
			sqlQueryObjectInsert_statisticaGiornalieraContenuti.addInsertField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_VALORE_7,false),"?");
			sqlQueryObjectInsert_statisticaGiornalieraContenuti.addInsertField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_NOME_8,false),"?");
			sqlQueryObjectInsert_statisticaGiornalieraContenuti.addInsertField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_VALORE_8,false),"?");
			sqlQueryObjectInsert_statisticaGiornalieraContenuti.addInsertField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_NOME_9,false),"?");
			sqlQueryObjectInsert_statisticaGiornalieraContenuti.addInsertField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_VALORE_9,false),"?");
			sqlQueryObjectInsert_statisticaGiornalieraContenuti.addInsertField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_NOME_10,false),"?");
			sqlQueryObjectInsert_statisticaGiornalieraContenuti.addInsertField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_VALORE_10,false),"?");
			sqlQueryObjectInsert_statisticaGiornalieraContenuti.addInsertField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.NUMERO_TRANSAZIONI,false),"?");
			sqlQueryObjectInsert_statisticaGiornalieraContenuti.addInsertField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.DIMENSIONI_BYTES_BANDA_COMPLESSIVA,false),"?");
			sqlQueryObjectInsert_statisticaGiornalieraContenuti.addInsertField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.DIMENSIONI_BYTES_BANDA_INTERNA,false),"?");
			sqlQueryObjectInsert_statisticaGiornalieraContenuti.addInsertField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.DIMENSIONI_BYTES_BANDA_ESTERNA,false),"?");
			sqlQueryObjectInsert_statisticaGiornalieraContenuti.addInsertField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.LATENZA_TOTALE,false),"?");
			sqlQueryObjectInsert_statisticaGiornalieraContenuti.addInsertField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.LATENZA_PORTA,false),"?");
			sqlQueryObjectInsert_statisticaGiornalieraContenuti.addInsertField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.LATENZA_SERVIZIO,false),"?");
			sqlQueryObjectInsert_statisticaGiornalieraContenuti.addInsertField("id_stat","?");

			// Insert statisticaGiornaliera.getStatisticaGiornalieraContenutiList().get(i)
			org.openspcoop2.utils.jdbc.IKeyGeneratorObject keyGenerator_statisticaGiornalieraContenuti = this.getStatisticaGiornalieraFetch().getKeyGeneratorObject(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI);
			long id_statisticaGiornalieraContenuti = jdbcUtilities.insertAndReturnGeneratedKey(sqlQueryObjectInsert_statisticaGiornalieraContenuti, keyGenerator_statisticaGiornalieraContenuti, jdbcProperties.isShowSql(),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaGiornaliera.getStatisticaGiornalieraContenutiList().get(i).getData(),StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.DATA.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaGiornaliera.getStatisticaGiornalieraContenutiList().get(i).getRisorsaNome(),StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.RISORSA_NOME.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaGiornaliera.getStatisticaGiornalieraContenutiList().get(i).getRisorsaValore(),StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.RISORSA_VALORE.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaGiornaliera.getStatisticaGiornalieraContenutiList().get(i).getFiltroNome1(),StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_NOME_1.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaGiornaliera.getStatisticaGiornalieraContenutiList().get(i).getFiltroValore1(),StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_VALORE_1.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaGiornaliera.getStatisticaGiornalieraContenutiList().get(i).getFiltroNome2(),StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_NOME_2.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaGiornaliera.getStatisticaGiornalieraContenutiList().get(i).getFiltroValore2(),StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_VALORE_2.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaGiornaliera.getStatisticaGiornalieraContenutiList().get(i).getFiltroNome3(),StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_NOME_3.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaGiornaliera.getStatisticaGiornalieraContenutiList().get(i).getFiltroValore3(),StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_VALORE_3.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaGiornaliera.getStatisticaGiornalieraContenutiList().get(i).getFiltroNome4(),StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_NOME_4.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaGiornaliera.getStatisticaGiornalieraContenutiList().get(i).getFiltroValore4(),StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_VALORE_4.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaGiornaliera.getStatisticaGiornalieraContenutiList().get(i).getFiltroNome5(),StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_NOME_5.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaGiornaliera.getStatisticaGiornalieraContenutiList().get(i).getFiltroValore5(),StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_VALORE_5.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaGiornaliera.getStatisticaGiornalieraContenutiList().get(i).getFiltroNome6(),StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_NOME_6.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaGiornaliera.getStatisticaGiornalieraContenutiList().get(i).getFiltroValore6(),StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_VALORE_6.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaGiornaliera.getStatisticaGiornalieraContenutiList().get(i).getFiltroNome7(),StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_NOME_7.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaGiornaliera.getStatisticaGiornalieraContenutiList().get(i).getFiltroValore7(),StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_VALORE_7.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaGiornaliera.getStatisticaGiornalieraContenutiList().get(i).getFiltroNome8(),StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_NOME_8.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaGiornaliera.getStatisticaGiornalieraContenutiList().get(i).getFiltroValore8(),StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_VALORE_8.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaGiornaliera.getStatisticaGiornalieraContenutiList().get(i).getFiltroNome9(),StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_NOME_9.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaGiornaliera.getStatisticaGiornalieraContenutiList().get(i).getFiltroValore9(),StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_VALORE_9.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaGiornaliera.getStatisticaGiornalieraContenutiList().get(i).getFiltroNome10(),StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_NOME_10.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaGiornaliera.getStatisticaGiornalieraContenutiList().get(i).getFiltroValore10(),StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_VALORE_10.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaGiornaliera.getStatisticaGiornalieraContenutiList().get(i).getNumeroTransazioni(),StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.NUMERO_TRANSAZIONI.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaGiornaliera.getStatisticaGiornalieraContenutiList().get(i).getDimensioniBytesBandaComplessiva(),StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.DIMENSIONI_BYTES_BANDA_COMPLESSIVA.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaGiornaliera.getStatisticaGiornalieraContenutiList().get(i).getDimensioniBytesBandaInterna(),StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.DIMENSIONI_BYTES_BANDA_INTERNA.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaGiornaliera.getStatisticaGiornalieraContenutiList().get(i).getDimensioniBytesBandaEsterna(),StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.DIMENSIONI_BYTES_BANDA_ESTERNA.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaGiornaliera.getStatisticaGiornalieraContenutiList().get(i).getLatenzaTotale(),StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.LATENZA_TOTALE.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaGiornaliera.getStatisticaGiornalieraContenutiList().get(i).getLatenzaPorta(),StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.LATENZA_PORTA.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaGiornaliera.getStatisticaGiornalieraContenutiList().get(i).getLatenzaServizio(),StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.LATENZA_SERVIZIO.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(Long.valueOf(id),Long.class)
			);
			statisticaGiornaliera.getStatisticaGiornalieraContenutiList().get(i).setId(id_statisticaGiornalieraContenuti);
		} // fine for 

		
	}

	@Override
	public void update(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, StatisticaGiornaliera statisticaGiornaliera, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		
		Long tableId = statisticaGiornaliera.getId();
		if(tableId==null || tableId<=0){
			throw new Exception("Retrieve tableId failed");
		}

		this.update(jdbcProperties, log, connection, sqlQueryObject, tableId, statisticaGiornaliera, idMappingResolutionBehaviour);
	}
	@Override
	public void update(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, StatisticaGiornaliera statisticaGiornaliera, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException, NotImplementedException, ServiceException, Exception {
	
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);
		
		ISQLQueryObject sqlQueryObjectInsert = sqlQueryObject.newSQLQueryObject();
		ISQLQueryObject sqlQueryObjectDelete = sqlQueryObjectInsert.newSQLQueryObject();
		ISQLQueryObject sqlQueryObjectGet = sqlQueryObjectDelete.newSQLQueryObject();
		ISQLQueryObject sqlQueryObjectUpdate = sqlQueryObjectGet.newSQLQueryObject();
		

		Statistica statisticaGiornaliera_statisticaBase = statisticaGiornaliera.getStatisticaBase();

		// Object statisticaGiornaliera_statisticaBase
		sqlQueryObjectUpdate.setANDLogicOperator(true);
		sqlQueryObjectUpdate.addUpdateTable(this.getStatisticaGiornalieraFieldConverter().toTable(StatisticaGiornaliera.model().STATISTICA_BASE));
		boolean isUpdate = true;
		java.util.List<JDBCObject> lstObjects = new java.util.ArrayList<JDBCObject>();
		sqlQueryObjectUpdate.addUpdateField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_BASE.DATA,false), "?");
		lstObjects.add(new JDBCObject(statisticaGiornaliera_statisticaBase.getData(), StatisticaGiornaliera.model().STATISTICA_BASE.DATA.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_BASE.ID_PORTA,false), "?");
		lstObjects.add(new JDBCObject(statisticaGiornaliera_statisticaBase.getIdPorta(), StatisticaGiornaliera.model().STATISTICA_BASE.ID_PORTA.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_BASE.TIPO_PORTA,false), "?");
		lstObjects.add(new JDBCObject(statisticaGiornaliera_statisticaBase.getTipoPorta(), StatisticaGiornaliera.model().STATISTICA_BASE.TIPO_PORTA.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_BASE.TIPO_MITTENTE,false), "?");
		lstObjects.add(new JDBCObject(statisticaGiornaliera_statisticaBase.getTipoMittente(), StatisticaGiornaliera.model().STATISTICA_BASE.TIPO_MITTENTE.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_BASE.MITTENTE,false), "?");
		lstObjects.add(new JDBCObject(statisticaGiornaliera_statisticaBase.getMittente(), StatisticaGiornaliera.model().STATISTICA_BASE.MITTENTE.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_BASE.TIPO_DESTINATARIO,false), "?");
		lstObjects.add(new JDBCObject(statisticaGiornaliera_statisticaBase.getTipoDestinatario(), StatisticaGiornaliera.model().STATISTICA_BASE.TIPO_DESTINATARIO.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_BASE.DESTINATARIO,false), "?");
		lstObjects.add(new JDBCObject(statisticaGiornaliera_statisticaBase.getDestinatario(), StatisticaGiornaliera.model().STATISTICA_BASE.DESTINATARIO.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_BASE.TIPO_SERVIZIO,false), "?");
		lstObjects.add(new JDBCObject(statisticaGiornaliera_statisticaBase.getTipoServizio(), StatisticaGiornaliera.model().STATISTICA_BASE.TIPO_SERVIZIO.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_BASE.SERVIZIO,false), "?");
		lstObjects.add(new JDBCObject(statisticaGiornaliera_statisticaBase.getServizio(), StatisticaGiornaliera.model().STATISTICA_BASE.SERVIZIO.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_BASE.VERSIONE_SERVIZIO,false), "?");
		lstObjects.add(new JDBCObject(statisticaGiornaliera_statisticaBase.getVersioneServizio(), StatisticaGiornaliera.model().STATISTICA_BASE.VERSIONE_SERVIZIO.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_BASE.AZIONE,false), "?");
		lstObjects.add(new JDBCObject(statisticaGiornaliera_statisticaBase.getAzione(), StatisticaGiornaliera.model().STATISTICA_BASE.AZIONE.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_BASE.SERVIZIO_APPLICATIVO,false), "?");
		lstObjects.add(new JDBCObject(statisticaGiornaliera_statisticaBase.getServizioApplicativo(), StatisticaGiornaliera.model().STATISTICA_BASE.SERVIZIO_APPLICATIVO.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_BASE.TRASPORTO_MITTENTE,false), "?");
		lstObjects.add(new JDBCObject(statisticaGiornaliera_statisticaBase.getTrasportoMittente(), StatisticaGiornaliera.model().STATISTICA_BASE.TRASPORTO_MITTENTE.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_BASE.TOKEN_ISSUER,false), "?");
		lstObjects.add(new JDBCObject(statisticaGiornaliera_statisticaBase.getTokenIssuer(), StatisticaGiornaliera.model().STATISTICA_BASE.TOKEN_ISSUER.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_BASE.TOKEN_CLIENT_ID,false), "?");
		lstObjects.add(new JDBCObject(statisticaGiornaliera_statisticaBase.getTokenClientId(), StatisticaGiornaliera.model().STATISTICA_BASE.TOKEN_CLIENT_ID.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_BASE.TOKEN_SUBJECT,false), "?");
		lstObjects.add(new JDBCObject(statisticaGiornaliera_statisticaBase.getTokenSubject(), StatisticaGiornaliera.model().STATISTICA_BASE.TOKEN_SUBJECT.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_BASE.TOKEN_USERNAME,false), "?");
		lstObjects.add(new JDBCObject(statisticaGiornaliera_statisticaBase.getTokenUsername(), StatisticaGiornaliera.model().STATISTICA_BASE.TOKEN_USERNAME.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_BASE.TOKEN_MAIL,false), "?");
		lstObjects.add(new JDBCObject(statisticaGiornaliera_statisticaBase.getTokenMail(), StatisticaGiornaliera.model().STATISTICA_BASE.TOKEN_MAIL.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_BASE.ESITO,false), "?");
		lstObjects.add(new JDBCObject(statisticaGiornaliera_statisticaBase.getEsito(), StatisticaGiornaliera.model().STATISTICA_BASE.ESITO.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_BASE.ESITO_CONTESTO,false), "?");
		lstObjects.add(new JDBCObject(statisticaGiornaliera_statisticaBase.getEsitoContesto(), StatisticaGiornaliera.model().STATISTICA_BASE.ESITO_CONTESTO.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_BASE.CLIENT_ADDRESS,false), "?");
		lstObjects.add(new JDBCObject(statisticaGiornaliera_statisticaBase.getClientAddress(), StatisticaGiornaliera.model().STATISTICA_BASE.CLIENT_ADDRESS.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_BASE.GRUPPI,false), "?");
		lstObjects.add(new JDBCObject(statisticaGiornaliera_statisticaBase.getGruppi(), StatisticaGiornaliera.model().STATISTICA_BASE.GRUPPI.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_BASE.NUMERO_TRANSAZIONI,false), "?");
		lstObjects.add(new JDBCObject(statisticaGiornaliera_statisticaBase.getNumeroTransazioni(), StatisticaGiornaliera.model().STATISTICA_BASE.NUMERO_TRANSAZIONI.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_BASE.DIMENSIONI_BYTES_BANDA_COMPLESSIVA,false), "?");
		lstObjects.add(new JDBCObject(statisticaGiornaliera_statisticaBase.getDimensioniBytesBandaComplessiva(), StatisticaGiornaliera.model().STATISTICA_BASE.DIMENSIONI_BYTES_BANDA_COMPLESSIVA.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_BASE.DIMENSIONI_BYTES_BANDA_INTERNA,false), "?");
		lstObjects.add(new JDBCObject(statisticaGiornaliera_statisticaBase.getDimensioniBytesBandaInterna(), StatisticaGiornaliera.model().STATISTICA_BASE.DIMENSIONI_BYTES_BANDA_INTERNA.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_BASE.DIMENSIONI_BYTES_BANDA_ESTERNA,false), "?");
		lstObjects.add(new JDBCObject(statisticaGiornaliera_statisticaBase.getDimensioniBytesBandaEsterna(), StatisticaGiornaliera.model().STATISTICA_BASE.DIMENSIONI_BYTES_BANDA_ESTERNA.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_BASE.LATENZA_TOTALE,false), "?");
		lstObjects.add(new JDBCObject(statisticaGiornaliera_statisticaBase.getLatenzaTotale(), StatisticaGiornaliera.model().STATISTICA_BASE.LATENZA_TOTALE.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_BASE.LATENZA_PORTA,false), "?");
		lstObjects.add(new JDBCObject(statisticaGiornaliera_statisticaBase.getLatenzaPorta(), StatisticaGiornaliera.model().STATISTICA_BASE.LATENZA_PORTA.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_BASE.LATENZA_SERVIZIO,false), "?");
		lstObjects.add(new JDBCObject(statisticaGiornaliera_statisticaBase.getLatenzaServizio(), StatisticaGiornaliera.model().STATISTICA_BASE.LATENZA_SERVIZIO.getFieldType()));
		sqlQueryObjectUpdate.addWhereCondition("id=?");
		lstObjects.add(new JDBCObject(tableId, Long.class));

		if(isUpdate) {
			// Update statisticaGiornaliera
			jdbcUtilities.executeUpdate(sqlQueryObjectUpdate.createSQLUpdate(), jdbcProperties.isShowSql(), 
				lstObjects.toArray(new JDBCObject[]{}));
		}
		// for statisticaGiornaliera_statisticaGiornalieraContenuti

		java.util.List<Long> ids_statisticaGiornalieraContenuti_da_non_eliminare = new java.util.ArrayList<Long>();
		for (Object statisticaGiornaliera_statisticaGiornalieraContenuti_object : statisticaGiornaliera.getStatisticaGiornalieraContenutiList()) {
			StatisticaContenuti statisticaGiornaliera_statisticaGiornalieraContenuti = (StatisticaContenuti) statisticaGiornaliera_statisticaGiornalieraContenuti_object;
			if(statisticaGiornaliera_statisticaGiornalieraContenuti.getId() == null || statisticaGiornaliera_statisticaGiornalieraContenuti.getId().longValue() <= 0) {

				long id = statisticaGiornaliera.getId();			

				// Object statisticaGiornaliera_statisticaGiornalieraContenuti
				ISQLQueryObject sqlQueryObjectInsert_statisticaGiornalieraContenuti = sqlQueryObjectInsert.newSQLQueryObject();
				sqlQueryObjectInsert_statisticaGiornalieraContenuti.addInsertTable(this.getStatisticaGiornalieraFieldConverter().toTable(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI));
				sqlQueryObjectInsert_statisticaGiornalieraContenuti.addInsertField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.DATA,false),"?");
				sqlQueryObjectInsert_statisticaGiornalieraContenuti.addInsertField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.RISORSA_NOME,false),"?");
				sqlQueryObjectInsert_statisticaGiornalieraContenuti.addInsertField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.RISORSA_VALORE,false),"?");
				sqlQueryObjectInsert_statisticaGiornalieraContenuti.addInsertField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_NOME_1,false),"?");
				sqlQueryObjectInsert_statisticaGiornalieraContenuti.addInsertField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_VALORE_1,false),"?");
				sqlQueryObjectInsert_statisticaGiornalieraContenuti.addInsertField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_NOME_2,false),"?");
				sqlQueryObjectInsert_statisticaGiornalieraContenuti.addInsertField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_VALORE_2,false),"?");
				sqlQueryObjectInsert_statisticaGiornalieraContenuti.addInsertField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_NOME_3,false),"?");
				sqlQueryObjectInsert_statisticaGiornalieraContenuti.addInsertField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_VALORE_3,false),"?");
				sqlQueryObjectInsert_statisticaGiornalieraContenuti.addInsertField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_NOME_4,false),"?");
				sqlQueryObjectInsert_statisticaGiornalieraContenuti.addInsertField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_VALORE_4,false),"?");
				sqlQueryObjectInsert_statisticaGiornalieraContenuti.addInsertField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_NOME_5,false),"?");
				sqlQueryObjectInsert_statisticaGiornalieraContenuti.addInsertField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_VALORE_5,false),"?");
				sqlQueryObjectInsert_statisticaGiornalieraContenuti.addInsertField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_NOME_6,false),"?");
				sqlQueryObjectInsert_statisticaGiornalieraContenuti.addInsertField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_VALORE_6,false),"?");
				sqlQueryObjectInsert_statisticaGiornalieraContenuti.addInsertField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_NOME_7,false),"?");
				sqlQueryObjectInsert_statisticaGiornalieraContenuti.addInsertField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_VALORE_7,false),"?");
				sqlQueryObjectInsert_statisticaGiornalieraContenuti.addInsertField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_NOME_8,false),"?");
				sqlQueryObjectInsert_statisticaGiornalieraContenuti.addInsertField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_VALORE_8,false),"?");
				sqlQueryObjectInsert_statisticaGiornalieraContenuti.addInsertField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_NOME_9,false),"?");
				sqlQueryObjectInsert_statisticaGiornalieraContenuti.addInsertField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_VALORE_9,false),"?");
				sqlQueryObjectInsert_statisticaGiornalieraContenuti.addInsertField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_NOME_10,false),"?");
				sqlQueryObjectInsert_statisticaGiornalieraContenuti.addInsertField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_VALORE_10,false),"?");
				sqlQueryObjectInsert_statisticaGiornalieraContenuti.addInsertField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.NUMERO_TRANSAZIONI,false),"?");
				sqlQueryObjectInsert_statisticaGiornalieraContenuti.addInsertField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.DIMENSIONI_BYTES_BANDA_COMPLESSIVA,false),"?");
				sqlQueryObjectInsert_statisticaGiornalieraContenuti.addInsertField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.DIMENSIONI_BYTES_BANDA_INTERNA,false),"?");
				sqlQueryObjectInsert_statisticaGiornalieraContenuti.addInsertField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.DIMENSIONI_BYTES_BANDA_ESTERNA,false),"?");
				sqlQueryObjectInsert_statisticaGiornalieraContenuti.addInsertField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.LATENZA_TOTALE,false),"?");
				sqlQueryObjectInsert_statisticaGiornalieraContenuti.addInsertField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.LATENZA_PORTA,false),"?");
				sqlQueryObjectInsert_statisticaGiornalieraContenuti.addInsertField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.LATENZA_SERVIZIO,false),"?");
				sqlQueryObjectInsert_statisticaGiornalieraContenuti.addInsertField("id_stat","?");

				// Insert statisticaGiornaliera_statisticaGiornalieraContenuti
				org.openspcoop2.utils.jdbc.IKeyGeneratorObject keyGenerator_statisticaGiornalieraContenuti = this.getStatisticaGiornalieraFetch().getKeyGeneratorObject(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI);
				long id_statisticaGiornalieraContenuti = jdbcUtilities.insertAndReturnGeneratedKey(sqlQueryObjectInsert_statisticaGiornalieraContenuti, keyGenerator_statisticaGiornalieraContenuti, jdbcProperties.isShowSql(),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaGiornaliera_statisticaGiornalieraContenuti.getData(),StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.DATA.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaGiornaliera_statisticaGiornalieraContenuti.getRisorsaNome(),StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.RISORSA_NOME.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaGiornaliera_statisticaGiornalieraContenuti.getRisorsaValore(),StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.RISORSA_VALORE.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaGiornaliera_statisticaGiornalieraContenuti.getFiltroNome1(),StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_NOME_1.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaGiornaliera_statisticaGiornalieraContenuti.getFiltroValore1(),StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_VALORE_1.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaGiornaliera_statisticaGiornalieraContenuti.getFiltroNome2(),StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_NOME_2.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaGiornaliera_statisticaGiornalieraContenuti.getFiltroValore2(),StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_VALORE_2.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaGiornaliera_statisticaGiornalieraContenuti.getFiltroNome3(),StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_NOME_3.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaGiornaliera_statisticaGiornalieraContenuti.getFiltroValore3(),StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_VALORE_3.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaGiornaliera_statisticaGiornalieraContenuti.getFiltroNome4(),StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_NOME_4.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaGiornaliera_statisticaGiornalieraContenuti.getFiltroValore4(),StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_VALORE_4.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaGiornaliera_statisticaGiornalieraContenuti.getFiltroNome5(),StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_NOME_5.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaGiornaliera_statisticaGiornalieraContenuti.getFiltroValore5(),StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_VALORE_5.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaGiornaliera_statisticaGiornalieraContenuti.getFiltroNome6(),StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_NOME_6.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaGiornaliera_statisticaGiornalieraContenuti.getFiltroValore6(),StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_VALORE_6.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaGiornaliera_statisticaGiornalieraContenuti.getFiltroNome7(),StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_NOME_7.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaGiornaliera_statisticaGiornalieraContenuti.getFiltroValore7(),StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_VALORE_7.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaGiornaliera_statisticaGiornalieraContenuti.getFiltroNome8(),StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_NOME_8.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaGiornaliera_statisticaGiornalieraContenuti.getFiltroValore8(),StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_VALORE_8.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaGiornaliera_statisticaGiornalieraContenuti.getFiltroNome9(),StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_NOME_9.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaGiornaliera_statisticaGiornalieraContenuti.getFiltroValore9(),StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_VALORE_9.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaGiornaliera_statisticaGiornalieraContenuti.getFiltroNome10(),StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_NOME_10.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaGiornaliera_statisticaGiornalieraContenuti.getFiltroValore10(),StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_VALORE_10.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaGiornaliera_statisticaGiornalieraContenuti.getNumeroTransazioni(),StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.NUMERO_TRANSAZIONI.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaGiornaliera_statisticaGiornalieraContenuti.getDimensioniBytesBandaComplessiva(),StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.DIMENSIONI_BYTES_BANDA_COMPLESSIVA.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaGiornaliera_statisticaGiornalieraContenuti.getDimensioniBytesBandaInterna(),StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.DIMENSIONI_BYTES_BANDA_INTERNA.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaGiornaliera_statisticaGiornalieraContenuti.getDimensioniBytesBandaEsterna(),StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.DIMENSIONI_BYTES_BANDA_ESTERNA.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaGiornaliera_statisticaGiornalieraContenuti.getLatenzaTotale(),StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.LATENZA_TOTALE.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaGiornaliera_statisticaGiornalieraContenuti.getLatenzaPorta(),StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.LATENZA_PORTA.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaGiornaliera_statisticaGiornalieraContenuti.getLatenzaServizio(),StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.LATENZA_SERVIZIO.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(Long.valueOf(id),Long.class)
				);
				statisticaGiornaliera_statisticaGiornalieraContenuti.setId(id_statisticaGiornalieraContenuti);

				ids_statisticaGiornalieraContenuti_da_non_eliminare.add(statisticaGiornaliera_statisticaGiornalieraContenuti.getId());
			} else {


				// Object statisticaGiornaliera_statisticaGiornalieraContenuti
				ISQLQueryObject sqlQueryObjectUpdate_statisticaGiornalieraContenuti = sqlQueryObjectUpdate.newSQLQueryObject();
				sqlQueryObjectUpdate_statisticaGiornalieraContenuti.setANDLogicOperator(true);
				sqlQueryObjectUpdate_statisticaGiornalieraContenuti.addUpdateTable(this.getStatisticaGiornalieraFieldConverter().toTable(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI));
				boolean isUpdate_statisticaGiornalieraContenuti = true;
				java.util.List<JDBCObject> lstObjects_statisticaGiornalieraContenuti = new java.util.ArrayList<JDBCObject>();
				sqlQueryObjectUpdate_statisticaGiornalieraContenuti.addUpdateField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.DATA,false), "?");
				lstObjects_statisticaGiornalieraContenuti.add(new JDBCObject(statisticaGiornaliera_statisticaGiornalieraContenuti.getData(), StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.DATA.getFieldType()));
				sqlQueryObjectUpdate_statisticaGiornalieraContenuti.addUpdateField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.RISORSA_NOME,false), "?");
				lstObjects_statisticaGiornalieraContenuti.add(new JDBCObject(statisticaGiornaliera_statisticaGiornalieraContenuti.getRisorsaNome(), StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.RISORSA_NOME.getFieldType()));
				sqlQueryObjectUpdate_statisticaGiornalieraContenuti.addUpdateField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.RISORSA_VALORE,false), "?");
				lstObjects_statisticaGiornalieraContenuti.add(new JDBCObject(statisticaGiornaliera_statisticaGiornalieraContenuti.getRisorsaValore(), StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.RISORSA_VALORE.getFieldType()));
				sqlQueryObjectUpdate_statisticaGiornalieraContenuti.addUpdateField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_NOME_1,false), "?");
				lstObjects_statisticaGiornalieraContenuti.add(new JDBCObject(statisticaGiornaliera_statisticaGiornalieraContenuti.getFiltroNome1(), StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_NOME_1.getFieldType()));
				sqlQueryObjectUpdate_statisticaGiornalieraContenuti.addUpdateField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_VALORE_1,false), "?");
				lstObjects_statisticaGiornalieraContenuti.add(new JDBCObject(statisticaGiornaliera_statisticaGiornalieraContenuti.getFiltroValore1(), StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_VALORE_1.getFieldType()));
				sqlQueryObjectUpdate_statisticaGiornalieraContenuti.addUpdateField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_NOME_2,false), "?");
				lstObjects_statisticaGiornalieraContenuti.add(new JDBCObject(statisticaGiornaliera_statisticaGiornalieraContenuti.getFiltroNome2(), StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_NOME_2.getFieldType()));
				sqlQueryObjectUpdate_statisticaGiornalieraContenuti.addUpdateField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_VALORE_2,false), "?");
				lstObjects_statisticaGiornalieraContenuti.add(new JDBCObject(statisticaGiornaliera_statisticaGiornalieraContenuti.getFiltroValore2(), StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_VALORE_2.getFieldType()));
				sqlQueryObjectUpdate_statisticaGiornalieraContenuti.addUpdateField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_NOME_3,false), "?");
				lstObjects_statisticaGiornalieraContenuti.add(new JDBCObject(statisticaGiornaliera_statisticaGiornalieraContenuti.getFiltroNome3(), StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_NOME_3.getFieldType()));
				sqlQueryObjectUpdate_statisticaGiornalieraContenuti.addUpdateField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_VALORE_3,false), "?");
				lstObjects_statisticaGiornalieraContenuti.add(new JDBCObject(statisticaGiornaliera_statisticaGiornalieraContenuti.getFiltroValore3(), StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_VALORE_3.getFieldType()));
				sqlQueryObjectUpdate_statisticaGiornalieraContenuti.addUpdateField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_NOME_4,false), "?");
				lstObjects_statisticaGiornalieraContenuti.add(new JDBCObject(statisticaGiornaliera_statisticaGiornalieraContenuti.getFiltroNome4(), StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_NOME_4.getFieldType()));
				sqlQueryObjectUpdate_statisticaGiornalieraContenuti.addUpdateField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_VALORE_4,false), "?");
				lstObjects_statisticaGiornalieraContenuti.add(new JDBCObject(statisticaGiornaliera_statisticaGiornalieraContenuti.getFiltroValore4(), StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_VALORE_4.getFieldType()));
				sqlQueryObjectUpdate_statisticaGiornalieraContenuti.addUpdateField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_NOME_5,false), "?");
				lstObjects_statisticaGiornalieraContenuti.add(new JDBCObject(statisticaGiornaliera_statisticaGiornalieraContenuti.getFiltroNome5(), StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_NOME_5.getFieldType()));
				sqlQueryObjectUpdate_statisticaGiornalieraContenuti.addUpdateField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_VALORE_5,false), "?");
				lstObjects_statisticaGiornalieraContenuti.add(new JDBCObject(statisticaGiornaliera_statisticaGiornalieraContenuti.getFiltroValore5(), StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_VALORE_5.getFieldType()));
				sqlQueryObjectUpdate_statisticaGiornalieraContenuti.addUpdateField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_NOME_6,false), "?");
				lstObjects_statisticaGiornalieraContenuti.add(new JDBCObject(statisticaGiornaliera_statisticaGiornalieraContenuti.getFiltroNome6(), StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_NOME_6.getFieldType()));
				sqlQueryObjectUpdate_statisticaGiornalieraContenuti.addUpdateField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_VALORE_6,false), "?");
				lstObjects_statisticaGiornalieraContenuti.add(new JDBCObject(statisticaGiornaliera_statisticaGiornalieraContenuti.getFiltroValore6(), StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_VALORE_6.getFieldType()));
				sqlQueryObjectUpdate_statisticaGiornalieraContenuti.addUpdateField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_NOME_7,false), "?");
				lstObjects_statisticaGiornalieraContenuti.add(new JDBCObject(statisticaGiornaliera_statisticaGiornalieraContenuti.getFiltroNome7(), StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_NOME_7.getFieldType()));
				sqlQueryObjectUpdate_statisticaGiornalieraContenuti.addUpdateField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_VALORE_7,false), "?");
				lstObjects_statisticaGiornalieraContenuti.add(new JDBCObject(statisticaGiornaliera_statisticaGiornalieraContenuti.getFiltroValore7(), StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_VALORE_7.getFieldType()));
				sqlQueryObjectUpdate_statisticaGiornalieraContenuti.addUpdateField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_NOME_8,false), "?");
				lstObjects_statisticaGiornalieraContenuti.add(new JDBCObject(statisticaGiornaliera_statisticaGiornalieraContenuti.getFiltroNome8(), StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_NOME_8.getFieldType()));
				sqlQueryObjectUpdate_statisticaGiornalieraContenuti.addUpdateField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_VALORE_8,false), "?");
				lstObjects_statisticaGiornalieraContenuti.add(new JDBCObject(statisticaGiornaliera_statisticaGiornalieraContenuti.getFiltroValore8(), StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_VALORE_8.getFieldType()));
				sqlQueryObjectUpdate_statisticaGiornalieraContenuti.addUpdateField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_NOME_9,false), "?");
				lstObjects_statisticaGiornalieraContenuti.add(new JDBCObject(statisticaGiornaliera_statisticaGiornalieraContenuti.getFiltroNome9(), StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_NOME_9.getFieldType()));
				sqlQueryObjectUpdate_statisticaGiornalieraContenuti.addUpdateField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_VALORE_9,false), "?");
				lstObjects_statisticaGiornalieraContenuti.add(new JDBCObject(statisticaGiornaliera_statisticaGiornalieraContenuti.getFiltroValore9(), StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_VALORE_9.getFieldType()));
				sqlQueryObjectUpdate_statisticaGiornalieraContenuti.addUpdateField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_NOME_10,false), "?");
				lstObjects_statisticaGiornalieraContenuti.add(new JDBCObject(statisticaGiornaliera_statisticaGiornalieraContenuti.getFiltroNome10(), StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_NOME_10.getFieldType()));
				sqlQueryObjectUpdate_statisticaGiornalieraContenuti.addUpdateField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_VALORE_10,false), "?");
				lstObjects_statisticaGiornalieraContenuti.add(new JDBCObject(statisticaGiornaliera_statisticaGiornalieraContenuti.getFiltroValore10(), StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_VALORE_10.getFieldType()));
				sqlQueryObjectUpdate_statisticaGiornalieraContenuti.addUpdateField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.NUMERO_TRANSAZIONI,false), "?");
				lstObjects_statisticaGiornalieraContenuti.add(new JDBCObject(statisticaGiornaliera_statisticaGiornalieraContenuti.getNumeroTransazioni(), StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.NUMERO_TRANSAZIONI.getFieldType()));
				sqlQueryObjectUpdate_statisticaGiornalieraContenuti.addUpdateField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.DIMENSIONI_BYTES_BANDA_COMPLESSIVA,false), "?");
				lstObjects_statisticaGiornalieraContenuti.add(new JDBCObject(statisticaGiornaliera_statisticaGiornalieraContenuti.getDimensioniBytesBandaComplessiva(), StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.DIMENSIONI_BYTES_BANDA_COMPLESSIVA.getFieldType()));
				sqlQueryObjectUpdate_statisticaGiornalieraContenuti.addUpdateField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.DIMENSIONI_BYTES_BANDA_INTERNA,false), "?");
				lstObjects_statisticaGiornalieraContenuti.add(new JDBCObject(statisticaGiornaliera_statisticaGiornalieraContenuti.getDimensioniBytesBandaInterna(), StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.DIMENSIONI_BYTES_BANDA_INTERNA.getFieldType()));
				sqlQueryObjectUpdate_statisticaGiornalieraContenuti.addUpdateField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.DIMENSIONI_BYTES_BANDA_ESTERNA,false), "?");
				lstObjects_statisticaGiornalieraContenuti.add(new JDBCObject(statisticaGiornaliera_statisticaGiornalieraContenuti.getDimensioniBytesBandaEsterna(), StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.DIMENSIONI_BYTES_BANDA_ESTERNA.getFieldType()));
				sqlQueryObjectUpdate_statisticaGiornalieraContenuti.addUpdateField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.LATENZA_TOTALE,false), "?");
				lstObjects_statisticaGiornalieraContenuti.add(new JDBCObject(statisticaGiornaliera_statisticaGiornalieraContenuti.getLatenzaTotale(), StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.LATENZA_TOTALE.getFieldType()));
				sqlQueryObjectUpdate_statisticaGiornalieraContenuti.addUpdateField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.LATENZA_PORTA,false), "?");
				lstObjects_statisticaGiornalieraContenuti.add(new JDBCObject(statisticaGiornaliera_statisticaGiornalieraContenuti.getLatenzaPorta(), StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.LATENZA_PORTA.getFieldType()));
				sqlQueryObjectUpdate_statisticaGiornalieraContenuti.addUpdateField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.LATENZA_SERVIZIO,false), "?");
				lstObjects_statisticaGiornalieraContenuti.add(new JDBCObject(statisticaGiornaliera_statisticaGiornalieraContenuti.getLatenzaServizio(), StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.LATENZA_SERVIZIO.getFieldType()));
				sqlQueryObjectUpdate_statisticaGiornalieraContenuti.addWhereCondition("id=?");
				ids_statisticaGiornalieraContenuti_da_non_eliminare.add(statisticaGiornaliera_statisticaGiornalieraContenuti.getId());
				lstObjects_statisticaGiornalieraContenuti.add(new JDBCObject(Long.valueOf(statisticaGiornaliera_statisticaGiornalieraContenuti.getId()),Long.class));

				if(isUpdate_statisticaGiornalieraContenuti) {
					// Update statisticaGiornaliera_statisticaGiornalieraContenuti
					jdbcUtilities.executeUpdate(sqlQueryObjectUpdate_statisticaGiornalieraContenuti.createSQLUpdate(), jdbcProperties.isShowSql(), 
						lstObjects_statisticaGiornalieraContenuti.toArray(new JDBCObject[]{}));
				}
			}
		} // fine for statisticaGiornaliera_statisticaGiornalieraContenuti

		// elimino tutte le occorrenze di statisticaGiornaliera_statisticaGiornalieraContenuti non presenti nell'update

		ISQLQueryObject sqlQueryObjectUpdate_statisticaGiornalieraContenuti_deleteList = sqlQueryObjectUpdate.newSQLQueryObject();
		sqlQueryObjectUpdate_statisticaGiornalieraContenuti_deleteList.setANDLogicOperator(true);
		sqlQueryObjectUpdate_statisticaGiornalieraContenuti_deleteList.addDeleteTable(this.getStatisticaGiornalieraFieldConverter().toTable(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI));
		java.util.List<JDBCObject> jdbcObjects_statisticaGiornalieraContenuti_delete = new java.util.ArrayList<JDBCObject>();

		sqlQueryObjectUpdate_statisticaGiornalieraContenuti_deleteList.addWhereCondition("id_stat=?");
		jdbcObjects_statisticaGiornalieraContenuti_delete.add(new JDBCObject(statisticaGiornaliera.getId(), Long.class));

		StringBuffer marks_statisticaGiornalieraContenuti = new StringBuffer();
		if(ids_statisticaGiornalieraContenuti_da_non_eliminare.size() > 0) {
			for(Long ids : ids_statisticaGiornalieraContenuti_da_non_eliminare) {
				if(marks_statisticaGiornalieraContenuti.length() > 0) {
					marks_statisticaGiornalieraContenuti.append(",");
				}
				marks_statisticaGiornalieraContenuti.append("?");
				jdbcObjects_statisticaGiornalieraContenuti_delete.add(new JDBCObject(ids, Long.class));

			}
			sqlQueryObjectUpdate_statisticaGiornalieraContenuti_deleteList.addWhereCondition("id NOT IN ("+marks_statisticaGiornalieraContenuti.toString()+")");
		}

		jdbcUtilities.execute(sqlQueryObjectUpdate_statisticaGiornalieraContenuti_deleteList.createSQLDelete(), jdbcProperties.isShowSql(), jdbcObjects_statisticaGiornalieraContenuti_delete.toArray(new JDBCObject[]{}));



	}
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, StatisticaGiornaliera statisticaGiornaliera, UpdateField ... updateFields) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		
		JDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getStatisticaGiornalieraFieldConverter().toTable(StatisticaGiornaliera.model()), 
				this._getMapTableToPKColumn(), 
				this._getRootTablePrimaryKeyValues(jdbcProperties, log, connection, sqlQueryObject, statisticaGiornaliera),
				this.getStatisticaGiornalieraFieldConverter(), this, null, updateFields);
	}
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, StatisticaGiornaliera statisticaGiornaliera, IExpression condition, UpdateField ... updateFields) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		
		JDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getStatisticaGiornalieraFieldConverter().toTable(StatisticaGiornaliera.model()), 
				this._getMapTableToPKColumn(), 
				this._getRootTablePrimaryKeyValues(jdbcProperties, log, connection, sqlQueryObject, statisticaGiornaliera),
				this.getStatisticaGiornalieraFieldConverter(), this, condition, updateFields);
	}
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, StatisticaGiornaliera statisticaGiornaliera, UpdateModel ... updateModels) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		
		JDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getStatisticaGiornalieraFieldConverter().toTable(StatisticaGiornaliera.model()), 
				this._getMapTableToPKColumn(), 
				this._getRootTablePrimaryKeyValues(jdbcProperties, log, connection, sqlQueryObject, statisticaGiornaliera),
				this.getStatisticaGiornalieraFieldConverter(), this, updateModels);
	}	
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, UpdateField ... updateFields) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		java.util.List<Object> ids = new java.util.ArrayList<Object>();
		ids.add(tableId);
		JDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getStatisticaGiornalieraFieldConverter().toTable(StatisticaGiornaliera.model()), 
				this._getMapTableToPKColumn(), 
				ids,
				this.getStatisticaGiornalieraFieldConverter(), this, null, updateFields);
	}
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, IExpression condition, UpdateField ... updateFields) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		java.util.List<Object> ids = new java.util.ArrayList<Object>();
		ids.add(tableId);
		JDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getStatisticaGiornalieraFieldConverter().toTable(StatisticaGiornaliera.model()), 
				this._getMapTableToPKColumn(), 
				ids,
				this.getStatisticaGiornalieraFieldConverter(), this, condition, updateFields);
	}
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, UpdateModel ... updateModels) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		java.util.List<Object> ids = new java.util.ArrayList<Object>();
		ids.add(tableId);
		JDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getStatisticaGiornalieraFieldConverter().toTable(StatisticaGiornaliera.model()), 
				this._getMapTableToPKColumn(), 
				ids,
				this.getStatisticaGiornalieraFieldConverter(), this, updateModels);
	}
	
	@Override
	public void updateOrCreate(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, StatisticaGiornaliera statisticaGiornaliera, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotImplementedException,ServiceException,Exception {
	
		Long id = statisticaGiornaliera.getId();
		if(id != null && this.exists(jdbcProperties, log, connection, sqlQueryObject, id)) {
			this.update(jdbcProperties, log, connection, sqlQueryObject, statisticaGiornaliera,idMappingResolutionBehaviour);		
		} else {
			this.create(jdbcProperties, log, connection, sqlQueryObject, statisticaGiornaliera,idMappingResolutionBehaviour);
		}
		
	}
	
	@Override
	public void updateOrCreate(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, StatisticaGiornaliera statisticaGiornaliera, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotImplementedException,ServiceException,Exception {
		if(this.exists(jdbcProperties, log, connection, sqlQueryObject, tableId)) {
			this.update(jdbcProperties, log, connection, sqlQueryObject, tableId, statisticaGiornaliera,idMappingResolutionBehaviour);
		} else {
			this.create(jdbcProperties, log, connection, sqlQueryObject, statisticaGiornaliera,idMappingResolutionBehaviour);
		}
	}
	
	@Override
	public void delete(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, StatisticaGiornaliera statisticaGiornaliera) throws NotImplementedException,ServiceException,Exception {
		
		
		Long longId = null;
		if(statisticaGiornaliera.getId()==null){
			throw new Exception("Parameter "+statisticaGiornaliera.getClass().getName()+".id is null");
		}
		if(statisticaGiornaliera.getId()<=0){
			throw new Exception("Parameter "+statisticaGiornaliera.getClass().getName()+".id is less equals 0");
		}
		longId = statisticaGiornaliera.getId();
		
		this._delete(jdbcProperties, log, connection, sqlQueryObject, longId);
		
	}

	private void _delete(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, Long id) throws NotImplementedException,ServiceException,Exception {
	
		if(id!=null && id.longValue()<=0){
			throw new ServiceException("Id is less equals 0");
		}
		
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);
		
		ISQLQueryObject sqlQueryObjectDelete = sqlQueryObject.newSQLQueryObject();
		

		//Recupero oggetto _statisticaGiornalieraContenuti
		ISQLQueryObject sqlQueryObjectDelete_statisticaGiornalieraContenuti_getToDelete = sqlQueryObjectDelete.newSQLQueryObject();
		sqlQueryObjectDelete_statisticaGiornalieraContenuti_getToDelete.setANDLogicOperator(true);
		sqlQueryObjectDelete_statisticaGiornalieraContenuti_getToDelete.addFromTable(this.getStatisticaGiornalieraFieldConverter().toTable(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI));
		sqlQueryObjectDelete_statisticaGiornalieraContenuti_getToDelete.addWhereCondition("id_stat=?");
		java.util.List<Object> statisticaGiornaliera_statisticaGiornalieraContenuti_toDelete_list = (java.util.List<Object>) jdbcUtilities.executeQuery(sqlQueryObjectDelete_statisticaGiornalieraContenuti_getToDelete.createSQLQuery(), jdbcProperties.isShowSql(), StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI, this.getStatisticaGiornalieraFetch(),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(Long.valueOf(id),Long.class));

		// for statisticaGiornaliera_statisticaGiornalieraContenuti
		for (Object statisticaGiornaliera_statisticaGiornalieraContenuti_object : statisticaGiornaliera_statisticaGiornalieraContenuti_toDelete_list) {
			StatisticaContenuti statisticaGiornaliera_statisticaGiornalieraContenuti = (StatisticaContenuti) statisticaGiornaliera_statisticaGiornalieraContenuti_object;

			// Object statisticaGiornaliera_statisticaGiornalieraContenuti
			ISQLQueryObject sqlQueryObjectDelete_statisticaGiornalieraContenuti = sqlQueryObjectDelete.newSQLQueryObject();
			sqlQueryObjectDelete_statisticaGiornalieraContenuti.setANDLogicOperator(true);
			sqlQueryObjectDelete_statisticaGiornalieraContenuti.addDeleteTable(this.getStatisticaGiornalieraFieldConverter().toTable(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI));
			sqlQueryObjectDelete_statisticaGiornalieraContenuti.addWhereCondition("id=?");

			// Delete statisticaGiornaliera_statisticaGiornalieraContenuti
			if(statisticaGiornaliera_statisticaGiornalieraContenuti != null){
				jdbcUtilities.execute(sqlQueryObjectDelete_statisticaGiornalieraContenuti.createSQLDelete(), jdbcProperties.isShowSql(), 
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(Long.valueOf(statisticaGiornaliera_statisticaGiornalieraContenuti.getId()),Long.class));
			}
		} // fine for statisticaGiornaliera_statisticaGiornalieraContenuti

		// Object statisticaGiornaliera_statisticaBase_toDelete
		sqlQueryObjectDelete.setANDLogicOperator(true);
		sqlQueryObjectDelete.addDeleteTable(this.getStatisticaGiornalieraFieldConverter().toTable(StatisticaGiornaliera.model().STATISTICA_BASE));
		if(id != null)
			sqlQueryObjectDelete.addWhereCondition("id=?");

		// Delete statisticaGiornaliera
		jdbcUtilities.execute(sqlQueryObjectDelete.createSQLDelete(), jdbcProperties.isShowSql(), 
			new JDBCObject(id,Long.class));

	}

	
	@Override
	public NonNegativeNumber deleteAll(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject) throws NotImplementedException,ServiceException,Exception {
		
		return this.deleteAll(jdbcProperties, log, connection, sqlQueryObject, new JDBCExpression(this.getStatisticaGiornalieraFieldConverter()));

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
