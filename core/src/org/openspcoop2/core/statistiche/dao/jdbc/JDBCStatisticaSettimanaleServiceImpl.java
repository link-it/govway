/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it).
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

import org.openspcoop2.core.statistiche.StatisticaSettimanale;
import org.openspcoop2.core.statistiche.Statistica;
import org.openspcoop2.core.statistiche.StatisticaContenuti;
import org.openspcoop2.core.statistiche.dao.jdbc.JDBCServiceManager;

/**     
 * JDBCStatisticaSettimanaleServiceImpl
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JDBCStatisticaSettimanaleServiceImpl extends JDBCStatisticaSettimanaleServiceSearchImpl
	implements IJDBCServiceCRUDWithoutId<StatisticaSettimanale, JDBCServiceManager> {

	@Override
	public void create(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, StatisticaSettimanale statisticaSettimanale, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotImplementedException,ServiceException,Exception {

		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);
		
		ISQLQueryObject sqlQueryObjectInsert = sqlQueryObject.newSQLQueryObject();
				


		// Object statisticaSettimanale.getStatisticaBase()
		sqlQueryObjectInsert.addInsertTable(this.getStatisticaSettimanaleFieldConverter().toTable(StatisticaSettimanale.model().STATISTICA_BASE));
		sqlQueryObjectInsert.addInsertField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_BASE.DATA,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_BASE.ID_PORTA,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_BASE.TIPO_PORTA,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_BASE.TIPO_MITTENTE,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_BASE.MITTENTE,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_BASE.TIPO_DESTINATARIO,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_BASE.DESTINATARIO,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_BASE.TIPO_SERVIZIO,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_BASE.SERVIZIO,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_BASE.VERSIONE_SERVIZIO,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_BASE.AZIONE,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_BASE.SERVIZIO_APPLICATIVO,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_BASE.TRASPORTO_MITTENTE,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_BASE.TOKEN_ISSUER,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_BASE.TOKEN_CLIENT_ID,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_BASE.TOKEN_SUBJECT,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_BASE.TOKEN_USERNAME,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_BASE.TOKEN_MAIL,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_BASE.ESITO,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_BASE.ESITO_CONTESTO,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_BASE.CLIENT_ADDRESS,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_BASE.GRUPPI,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_BASE.NUMERO_TRANSAZIONI,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_BASE.DIMENSIONI_BYTES_BANDA_COMPLESSIVA,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_BASE.DIMENSIONI_BYTES_BANDA_INTERNA,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_BASE.DIMENSIONI_BYTES_BANDA_ESTERNA,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_BASE.LATENZA_TOTALE,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_BASE.LATENZA_PORTA,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_BASE.LATENZA_SERVIZIO,false),"?");

		// Insert statisticaSettimanale
		org.openspcoop2.utils.jdbc.IKeyGeneratorObject keyGenerator = this.getStatisticaSettimanaleFetch().getKeyGeneratorObject(StatisticaSettimanale.model());
		long id = jdbcUtilities.insertAndReturnGeneratedKey(sqlQueryObjectInsert, keyGenerator, jdbcProperties.isShowSql(),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaSettimanale.getStatisticaBase().getData(),StatisticaSettimanale.model().STATISTICA_BASE.DATA.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaSettimanale.getStatisticaBase().getIdPorta(),StatisticaSettimanale.model().STATISTICA_BASE.ID_PORTA.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaSettimanale.getStatisticaBase().getTipoPorta(),StatisticaSettimanale.model().STATISTICA_BASE.TIPO_PORTA.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaSettimanale.getStatisticaBase().getTipoMittente(),StatisticaSettimanale.model().STATISTICA_BASE.TIPO_MITTENTE.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaSettimanale.getStatisticaBase().getMittente(),StatisticaSettimanale.model().STATISTICA_BASE.MITTENTE.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaSettimanale.getStatisticaBase().getTipoDestinatario(),StatisticaSettimanale.model().STATISTICA_BASE.TIPO_DESTINATARIO.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaSettimanale.getStatisticaBase().getDestinatario(),StatisticaSettimanale.model().STATISTICA_BASE.DESTINATARIO.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaSettimanale.getStatisticaBase().getTipoServizio(),StatisticaSettimanale.model().STATISTICA_BASE.TIPO_SERVIZIO.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaSettimanale.getStatisticaBase().getServizio(),StatisticaSettimanale.model().STATISTICA_BASE.SERVIZIO.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaSettimanale.getStatisticaBase().getVersioneServizio(),StatisticaSettimanale.model().STATISTICA_BASE.VERSIONE_SERVIZIO.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaSettimanale.getStatisticaBase().getAzione(),StatisticaSettimanale.model().STATISTICA_BASE.AZIONE.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaSettimanale.getStatisticaBase().getServizioApplicativo(),StatisticaSettimanale.model().STATISTICA_BASE.SERVIZIO_APPLICATIVO.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaSettimanale.getStatisticaBase().getTrasportoMittente(),StatisticaSettimanale.model().STATISTICA_BASE.TRASPORTO_MITTENTE.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaSettimanale.getStatisticaBase().getTokenIssuer(),StatisticaSettimanale.model().STATISTICA_BASE.TOKEN_ISSUER.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaSettimanale.getStatisticaBase().getTokenClientId(),StatisticaSettimanale.model().STATISTICA_BASE.TOKEN_CLIENT_ID.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaSettimanale.getStatisticaBase().getTokenSubject(),StatisticaSettimanale.model().STATISTICA_BASE.TOKEN_SUBJECT.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaSettimanale.getStatisticaBase().getTokenUsername(),StatisticaSettimanale.model().STATISTICA_BASE.TOKEN_USERNAME.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaSettimanale.getStatisticaBase().getTokenMail(),StatisticaSettimanale.model().STATISTICA_BASE.TOKEN_MAIL.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaSettimanale.getStatisticaBase().getEsito(),StatisticaSettimanale.model().STATISTICA_BASE.ESITO.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaSettimanale.getStatisticaBase().getEsitoContesto(),StatisticaSettimanale.model().STATISTICA_BASE.ESITO_CONTESTO.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaSettimanale.getStatisticaBase().getClientAddress(),StatisticaSettimanale.model().STATISTICA_BASE.CLIENT_ADDRESS.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaSettimanale.getStatisticaBase().getGruppi(),StatisticaSettimanale.model().STATISTICA_BASE.GRUPPI.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaSettimanale.getStatisticaBase().getNumeroTransazioni(),StatisticaSettimanale.model().STATISTICA_BASE.NUMERO_TRANSAZIONI.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaSettimanale.getStatisticaBase().getDimensioniBytesBandaComplessiva(),StatisticaSettimanale.model().STATISTICA_BASE.DIMENSIONI_BYTES_BANDA_COMPLESSIVA.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaSettimanale.getStatisticaBase().getDimensioniBytesBandaInterna(),StatisticaSettimanale.model().STATISTICA_BASE.DIMENSIONI_BYTES_BANDA_INTERNA.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaSettimanale.getStatisticaBase().getDimensioniBytesBandaEsterna(),StatisticaSettimanale.model().STATISTICA_BASE.DIMENSIONI_BYTES_BANDA_ESTERNA.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaSettimanale.getStatisticaBase().getLatenzaTotale(),StatisticaSettimanale.model().STATISTICA_BASE.LATENZA_TOTALE.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaSettimanale.getStatisticaBase().getLatenzaPorta(),StatisticaSettimanale.model().STATISTICA_BASE.LATENZA_PORTA.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaSettimanale.getStatisticaBase().getLatenzaServizio(),StatisticaSettimanale.model().STATISTICA_BASE.LATENZA_SERVIZIO.getFieldType())
		);
		statisticaSettimanale.setId(id);

		// for statisticaSettimanale
		for (int i = 0; i < statisticaSettimanale.getStatisticaSettimanaleContenutiList().size(); i++) {


			// Object statisticaSettimanale.getStatisticaSettimanaleContenutiList().get(i)
			ISQLQueryObject sqlQueryObjectInsert_statisticaSettimanaleContenuti = sqlQueryObjectInsert.newSQLQueryObject();
			sqlQueryObjectInsert_statisticaSettimanaleContenuti.addInsertTable(this.getStatisticaSettimanaleFieldConverter().toTable(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI));
			sqlQueryObjectInsert_statisticaSettimanaleContenuti.addInsertField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.DATA,false),"?");
			sqlQueryObjectInsert_statisticaSettimanaleContenuti.addInsertField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.RISORSA_NOME,false),"?");
			sqlQueryObjectInsert_statisticaSettimanaleContenuti.addInsertField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.RISORSA_VALORE,false),"?");
			sqlQueryObjectInsert_statisticaSettimanaleContenuti.addInsertField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_NOME_1,false),"?");
			sqlQueryObjectInsert_statisticaSettimanaleContenuti.addInsertField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_VALORE_1,false),"?");
			sqlQueryObjectInsert_statisticaSettimanaleContenuti.addInsertField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_NOME_2,false),"?");
			sqlQueryObjectInsert_statisticaSettimanaleContenuti.addInsertField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_VALORE_2,false),"?");
			sqlQueryObjectInsert_statisticaSettimanaleContenuti.addInsertField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_NOME_3,false),"?");
			sqlQueryObjectInsert_statisticaSettimanaleContenuti.addInsertField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_VALORE_3,false),"?");
			sqlQueryObjectInsert_statisticaSettimanaleContenuti.addInsertField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_NOME_4,false),"?");
			sqlQueryObjectInsert_statisticaSettimanaleContenuti.addInsertField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_VALORE_4,false),"?");
			sqlQueryObjectInsert_statisticaSettimanaleContenuti.addInsertField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_NOME_5,false),"?");
			sqlQueryObjectInsert_statisticaSettimanaleContenuti.addInsertField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_VALORE_5,false),"?");
			sqlQueryObjectInsert_statisticaSettimanaleContenuti.addInsertField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_NOME_6,false),"?");
			sqlQueryObjectInsert_statisticaSettimanaleContenuti.addInsertField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_VALORE_6,false),"?");
			sqlQueryObjectInsert_statisticaSettimanaleContenuti.addInsertField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_NOME_7,false),"?");
			sqlQueryObjectInsert_statisticaSettimanaleContenuti.addInsertField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_VALORE_7,false),"?");
			sqlQueryObjectInsert_statisticaSettimanaleContenuti.addInsertField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_NOME_8,false),"?");
			sqlQueryObjectInsert_statisticaSettimanaleContenuti.addInsertField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_VALORE_8,false),"?");
			sqlQueryObjectInsert_statisticaSettimanaleContenuti.addInsertField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_NOME_9,false),"?");
			sqlQueryObjectInsert_statisticaSettimanaleContenuti.addInsertField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_VALORE_9,false),"?");
			sqlQueryObjectInsert_statisticaSettimanaleContenuti.addInsertField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_NOME_10,false),"?");
			sqlQueryObjectInsert_statisticaSettimanaleContenuti.addInsertField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_VALORE_10,false),"?");
			sqlQueryObjectInsert_statisticaSettimanaleContenuti.addInsertField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.NUMERO_TRANSAZIONI,false),"?");
			sqlQueryObjectInsert_statisticaSettimanaleContenuti.addInsertField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.DIMENSIONI_BYTES_BANDA_COMPLESSIVA,false),"?");
			sqlQueryObjectInsert_statisticaSettimanaleContenuti.addInsertField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.DIMENSIONI_BYTES_BANDA_INTERNA,false),"?");
			sqlQueryObjectInsert_statisticaSettimanaleContenuti.addInsertField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.DIMENSIONI_BYTES_BANDA_ESTERNA,false),"?");
			sqlQueryObjectInsert_statisticaSettimanaleContenuti.addInsertField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.LATENZA_TOTALE,false),"?");
			sqlQueryObjectInsert_statisticaSettimanaleContenuti.addInsertField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.LATENZA_PORTA,false),"?");
			sqlQueryObjectInsert_statisticaSettimanaleContenuti.addInsertField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.LATENZA_SERVIZIO,false),"?");
			sqlQueryObjectInsert_statisticaSettimanaleContenuti.addInsertField("id_stat","?");

			// Insert statisticaSettimanale.getStatisticaSettimanaleContenutiList().get(i)
			org.openspcoop2.utils.jdbc.IKeyGeneratorObject keyGenerator_statisticaSettimanaleContenuti = this.getStatisticaSettimanaleFetch().getKeyGeneratorObject(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI);
			long id_statisticaSettimanaleContenuti = jdbcUtilities.insertAndReturnGeneratedKey(sqlQueryObjectInsert_statisticaSettimanaleContenuti, keyGenerator_statisticaSettimanaleContenuti, jdbcProperties.isShowSql(),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaSettimanale.getStatisticaSettimanaleContenutiList().get(i).getData(),StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.DATA.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaSettimanale.getStatisticaSettimanaleContenutiList().get(i).getRisorsaNome(),StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.RISORSA_NOME.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaSettimanale.getStatisticaSettimanaleContenutiList().get(i).getRisorsaValore(),StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.RISORSA_VALORE.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaSettimanale.getStatisticaSettimanaleContenutiList().get(i).getFiltroNome1(),StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_NOME_1.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaSettimanale.getStatisticaSettimanaleContenutiList().get(i).getFiltroValore1(),StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_VALORE_1.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaSettimanale.getStatisticaSettimanaleContenutiList().get(i).getFiltroNome2(),StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_NOME_2.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaSettimanale.getStatisticaSettimanaleContenutiList().get(i).getFiltroValore2(),StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_VALORE_2.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaSettimanale.getStatisticaSettimanaleContenutiList().get(i).getFiltroNome3(),StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_NOME_3.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaSettimanale.getStatisticaSettimanaleContenutiList().get(i).getFiltroValore3(),StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_VALORE_3.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaSettimanale.getStatisticaSettimanaleContenutiList().get(i).getFiltroNome4(),StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_NOME_4.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaSettimanale.getStatisticaSettimanaleContenutiList().get(i).getFiltroValore4(),StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_VALORE_4.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaSettimanale.getStatisticaSettimanaleContenutiList().get(i).getFiltroNome5(),StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_NOME_5.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaSettimanale.getStatisticaSettimanaleContenutiList().get(i).getFiltroValore5(),StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_VALORE_5.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaSettimanale.getStatisticaSettimanaleContenutiList().get(i).getFiltroNome6(),StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_NOME_6.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaSettimanale.getStatisticaSettimanaleContenutiList().get(i).getFiltroValore6(),StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_VALORE_6.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaSettimanale.getStatisticaSettimanaleContenutiList().get(i).getFiltroNome7(),StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_NOME_7.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaSettimanale.getStatisticaSettimanaleContenutiList().get(i).getFiltroValore7(),StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_VALORE_7.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaSettimanale.getStatisticaSettimanaleContenutiList().get(i).getFiltroNome8(),StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_NOME_8.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaSettimanale.getStatisticaSettimanaleContenutiList().get(i).getFiltroValore8(),StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_VALORE_8.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaSettimanale.getStatisticaSettimanaleContenutiList().get(i).getFiltroNome9(),StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_NOME_9.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaSettimanale.getStatisticaSettimanaleContenutiList().get(i).getFiltroValore9(),StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_VALORE_9.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaSettimanale.getStatisticaSettimanaleContenutiList().get(i).getFiltroNome10(),StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_NOME_10.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaSettimanale.getStatisticaSettimanaleContenutiList().get(i).getFiltroValore10(),StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_VALORE_10.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaSettimanale.getStatisticaSettimanaleContenutiList().get(i).getNumeroTransazioni(),StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.NUMERO_TRANSAZIONI.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaSettimanale.getStatisticaSettimanaleContenutiList().get(i).getDimensioniBytesBandaComplessiva(),StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.DIMENSIONI_BYTES_BANDA_COMPLESSIVA.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaSettimanale.getStatisticaSettimanaleContenutiList().get(i).getDimensioniBytesBandaInterna(),StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.DIMENSIONI_BYTES_BANDA_INTERNA.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaSettimanale.getStatisticaSettimanaleContenutiList().get(i).getDimensioniBytesBandaEsterna(),StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.DIMENSIONI_BYTES_BANDA_ESTERNA.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaSettimanale.getStatisticaSettimanaleContenutiList().get(i).getLatenzaTotale(),StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.LATENZA_TOTALE.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaSettimanale.getStatisticaSettimanaleContenutiList().get(i).getLatenzaPorta(),StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.LATENZA_PORTA.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaSettimanale.getStatisticaSettimanaleContenutiList().get(i).getLatenzaServizio(),StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.LATENZA_SERVIZIO.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(Long.valueOf(id),Long.class)
			);
			statisticaSettimanale.getStatisticaSettimanaleContenutiList().get(i).setId(id_statisticaSettimanaleContenuti);
		} // fine for 

		
	}

	@Override
	public void update(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, StatisticaSettimanale statisticaSettimanale, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		
		Long tableId = statisticaSettimanale.getId();
		if(tableId==null || tableId<=0){
			throw new Exception("Retrieve tableId failed");
		}

		this.update(jdbcProperties, log, connection, sqlQueryObject, tableId, statisticaSettimanale, idMappingResolutionBehaviour);
	}
	@Override
	public void update(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, StatisticaSettimanale statisticaSettimanale, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException, NotImplementedException, ServiceException, Exception {
	
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);
		
		ISQLQueryObject sqlQueryObjectInsert = sqlQueryObject.newSQLQueryObject();
		ISQLQueryObject sqlQueryObjectDelete = sqlQueryObjectInsert.newSQLQueryObject();
		ISQLQueryObject sqlQueryObjectGet = sqlQueryObjectDelete.newSQLQueryObject();
		ISQLQueryObject sqlQueryObjectUpdate = sqlQueryObjectGet.newSQLQueryObject();
		

		Statistica statisticaSettimanale_statisticaBase = statisticaSettimanale.getStatisticaBase();

		// Object statisticaSettimanale_statisticaBase
		sqlQueryObjectUpdate.setANDLogicOperator(true);
		sqlQueryObjectUpdate.addUpdateTable(this.getStatisticaSettimanaleFieldConverter().toTable(StatisticaSettimanale.model().STATISTICA_BASE));
		boolean isUpdate = true;
		java.util.List<JDBCObject> lstObjects = new java.util.ArrayList<JDBCObject>();
		sqlQueryObjectUpdate.addUpdateField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_BASE.DATA,false), "?");
		lstObjects.add(new JDBCObject(statisticaSettimanale_statisticaBase.getData(), StatisticaSettimanale.model().STATISTICA_BASE.DATA.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_BASE.ID_PORTA,false), "?");
		lstObjects.add(new JDBCObject(statisticaSettimanale_statisticaBase.getIdPorta(), StatisticaSettimanale.model().STATISTICA_BASE.ID_PORTA.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_BASE.TIPO_PORTA,false), "?");
		lstObjects.add(new JDBCObject(statisticaSettimanale_statisticaBase.getTipoPorta(), StatisticaSettimanale.model().STATISTICA_BASE.TIPO_PORTA.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_BASE.TIPO_MITTENTE,false), "?");
		lstObjects.add(new JDBCObject(statisticaSettimanale_statisticaBase.getTipoMittente(), StatisticaSettimanale.model().STATISTICA_BASE.TIPO_MITTENTE.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_BASE.MITTENTE,false), "?");
		lstObjects.add(new JDBCObject(statisticaSettimanale_statisticaBase.getMittente(), StatisticaSettimanale.model().STATISTICA_BASE.MITTENTE.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_BASE.TIPO_DESTINATARIO,false), "?");
		lstObjects.add(new JDBCObject(statisticaSettimanale_statisticaBase.getTipoDestinatario(), StatisticaSettimanale.model().STATISTICA_BASE.TIPO_DESTINATARIO.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_BASE.DESTINATARIO,false), "?");
		lstObjects.add(new JDBCObject(statisticaSettimanale_statisticaBase.getDestinatario(), StatisticaSettimanale.model().STATISTICA_BASE.DESTINATARIO.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_BASE.TIPO_SERVIZIO,false), "?");
		lstObjects.add(new JDBCObject(statisticaSettimanale_statisticaBase.getTipoServizio(), StatisticaSettimanale.model().STATISTICA_BASE.TIPO_SERVIZIO.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_BASE.SERVIZIO,false), "?");
		lstObjects.add(new JDBCObject(statisticaSettimanale_statisticaBase.getServizio(), StatisticaSettimanale.model().STATISTICA_BASE.SERVIZIO.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_BASE.VERSIONE_SERVIZIO,false), "?");
		lstObjects.add(new JDBCObject(statisticaSettimanale_statisticaBase.getVersioneServizio(), StatisticaSettimanale.model().STATISTICA_BASE.VERSIONE_SERVIZIO.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_BASE.AZIONE,false), "?");
		lstObjects.add(new JDBCObject(statisticaSettimanale_statisticaBase.getAzione(), StatisticaSettimanale.model().STATISTICA_BASE.AZIONE.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_BASE.SERVIZIO_APPLICATIVO,false), "?");
		lstObjects.add(new JDBCObject(statisticaSettimanale_statisticaBase.getServizioApplicativo(), StatisticaSettimanale.model().STATISTICA_BASE.SERVIZIO_APPLICATIVO.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_BASE.TRASPORTO_MITTENTE,false), "?");
		lstObjects.add(new JDBCObject(statisticaSettimanale_statisticaBase.getTrasportoMittente(), StatisticaSettimanale.model().STATISTICA_BASE.TRASPORTO_MITTENTE.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_BASE.TOKEN_ISSUER,false), "?");
		lstObjects.add(new JDBCObject(statisticaSettimanale_statisticaBase.getTokenIssuer(), StatisticaSettimanale.model().STATISTICA_BASE.TOKEN_ISSUER.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_BASE.TOKEN_CLIENT_ID,false), "?");
		lstObjects.add(new JDBCObject(statisticaSettimanale_statisticaBase.getTokenClientId(), StatisticaSettimanale.model().STATISTICA_BASE.TOKEN_CLIENT_ID.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_BASE.TOKEN_SUBJECT,false), "?");
		lstObjects.add(new JDBCObject(statisticaSettimanale_statisticaBase.getTokenSubject(), StatisticaSettimanale.model().STATISTICA_BASE.TOKEN_SUBJECT.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_BASE.TOKEN_USERNAME,false), "?");
		lstObjects.add(new JDBCObject(statisticaSettimanale_statisticaBase.getTokenUsername(), StatisticaSettimanale.model().STATISTICA_BASE.TOKEN_USERNAME.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_BASE.TOKEN_MAIL,false), "?");
		lstObjects.add(new JDBCObject(statisticaSettimanale_statisticaBase.getTokenMail(), StatisticaSettimanale.model().STATISTICA_BASE.TOKEN_MAIL.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_BASE.ESITO,false), "?");
		lstObjects.add(new JDBCObject(statisticaSettimanale_statisticaBase.getEsito(), StatisticaSettimanale.model().STATISTICA_BASE.ESITO.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_BASE.ESITO_CONTESTO,false), "?");
		lstObjects.add(new JDBCObject(statisticaSettimanale_statisticaBase.getEsitoContesto(), StatisticaSettimanale.model().STATISTICA_BASE.ESITO_CONTESTO.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_BASE.CLIENT_ADDRESS,false), "?");
		lstObjects.add(new JDBCObject(statisticaSettimanale_statisticaBase.getClientAddress(), StatisticaSettimanale.model().STATISTICA_BASE.CLIENT_ADDRESS.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_BASE.GRUPPI,false), "?");
		lstObjects.add(new JDBCObject(statisticaSettimanale_statisticaBase.getGruppi(), StatisticaSettimanale.model().STATISTICA_BASE.GRUPPI.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_BASE.NUMERO_TRANSAZIONI,false), "?");
		lstObjects.add(new JDBCObject(statisticaSettimanale_statisticaBase.getNumeroTransazioni(), StatisticaSettimanale.model().STATISTICA_BASE.NUMERO_TRANSAZIONI.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_BASE.DIMENSIONI_BYTES_BANDA_COMPLESSIVA,false), "?");
		lstObjects.add(new JDBCObject(statisticaSettimanale_statisticaBase.getDimensioniBytesBandaComplessiva(), StatisticaSettimanale.model().STATISTICA_BASE.DIMENSIONI_BYTES_BANDA_COMPLESSIVA.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_BASE.DIMENSIONI_BYTES_BANDA_INTERNA,false), "?");
		lstObjects.add(new JDBCObject(statisticaSettimanale_statisticaBase.getDimensioniBytesBandaInterna(), StatisticaSettimanale.model().STATISTICA_BASE.DIMENSIONI_BYTES_BANDA_INTERNA.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_BASE.DIMENSIONI_BYTES_BANDA_ESTERNA,false), "?");
		lstObjects.add(new JDBCObject(statisticaSettimanale_statisticaBase.getDimensioniBytesBandaEsterna(), StatisticaSettimanale.model().STATISTICA_BASE.DIMENSIONI_BYTES_BANDA_ESTERNA.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_BASE.LATENZA_TOTALE,false), "?");
		lstObjects.add(new JDBCObject(statisticaSettimanale_statisticaBase.getLatenzaTotale(), StatisticaSettimanale.model().STATISTICA_BASE.LATENZA_TOTALE.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_BASE.LATENZA_PORTA,false), "?");
		lstObjects.add(new JDBCObject(statisticaSettimanale_statisticaBase.getLatenzaPorta(), StatisticaSettimanale.model().STATISTICA_BASE.LATENZA_PORTA.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_BASE.LATENZA_SERVIZIO,false), "?");
		lstObjects.add(new JDBCObject(statisticaSettimanale_statisticaBase.getLatenzaServizio(), StatisticaSettimanale.model().STATISTICA_BASE.LATENZA_SERVIZIO.getFieldType()));
		sqlQueryObjectUpdate.addWhereCondition("id=?");
		lstObjects.add(new JDBCObject(tableId, Long.class));

		if(isUpdate) {
			// Update statisticaSettimanale
			jdbcUtilities.executeUpdate(sqlQueryObjectUpdate.createSQLUpdate(), jdbcProperties.isShowSql(), 
				lstObjects.toArray(new JDBCObject[]{}));
		}
		// for statisticaSettimanale_statisticaSettimanaleContenuti

		java.util.List<Long> ids_statisticaSettimanaleContenuti_da_non_eliminare = new java.util.ArrayList<Long>();
		for (Object statisticaSettimanale_statisticaSettimanaleContenuti_object : statisticaSettimanale.getStatisticaSettimanaleContenutiList()) {
			StatisticaContenuti statisticaSettimanale_statisticaSettimanaleContenuti = (StatisticaContenuti) statisticaSettimanale_statisticaSettimanaleContenuti_object;
			if(statisticaSettimanale_statisticaSettimanaleContenuti.getId() == null || statisticaSettimanale_statisticaSettimanaleContenuti.getId().longValue() <= 0) {

				long id = statisticaSettimanale.getId();			

				// Object statisticaSettimanale_statisticaSettimanaleContenuti
				ISQLQueryObject sqlQueryObjectInsert_statisticaSettimanaleContenuti = sqlQueryObjectInsert.newSQLQueryObject();
				sqlQueryObjectInsert_statisticaSettimanaleContenuti.addInsertTable(this.getStatisticaSettimanaleFieldConverter().toTable(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI));
				sqlQueryObjectInsert_statisticaSettimanaleContenuti.addInsertField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.DATA,false),"?");
				sqlQueryObjectInsert_statisticaSettimanaleContenuti.addInsertField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.RISORSA_NOME,false),"?");
				sqlQueryObjectInsert_statisticaSettimanaleContenuti.addInsertField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.RISORSA_VALORE,false),"?");
				sqlQueryObjectInsert_statisticaSettimanaleContenuti.addInsertField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_NOME_1,false),"?");
				sqlQueryObjectInsert_statisticaSettimanaleContenuti.addInsertField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_VALORE_1,false),"?");
				sqlQueryObjectInsert_statisticaSettimanaleContenuti.addInsertField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_NOME_2,false),"?");
				sqlQueryObjectInsert_statisticaSettimanaleContenuti.addInsertField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_VALORE_2,false),"?");
				sqlQueryObjectInsert_statisticaSettimanaleContenuti.addInsertField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_NOME_3,false),"?");
				sqlQueryObjectInsert_statisticaSettimanaleContenuti.addInsertField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_VALORE_3,false),"?");
				sqlQueryObjectInsert_statisticaSettimanaleContenuti.addInsertField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_NOME_4,false),"?");
				sqlQueryObjectInsert_statisticaSettimanaleContenuti.addInsertField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_VALORE_4,false),"?");
				sqlQueryObjectInsert_statisticaSettimanaleContenuti.addInsertField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_NOME_5,false),"?");
				sqlQueryObjectInsert_statisticaSettimanaleContenuti.addInsertField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_VALORE_5,false),"?");
				sqlQueryObjectInsert_statisticaSettimanaleContenuti.addInsertField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_NOME_6,false),"?");
				sqlQueryObjectInsert_statisticaSettimanaleContenuti.addInsertField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_VALORE_6,false),"?");
				sqlQueryObjectInsert_statisticaSettimanaleContenuti.addInsertField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_NOME_7,false),"?");
				sqlQueryObjectInsert_statisticaSettimanaleContenuti.addInsertField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_VALORE_7,false),"?");
				sqlQueryObjectInsert_statisticaSettimanaleContenuti.addInsertField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_NOME_8,false),"?");
				sqlQueryObjectInsert_statisticaSettimanaleContenuti.addInsertField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_VALORE_8,false),"?");
				sqlQueryObjectInsert_statisticaSettimanaleContenuti.addInsertField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_NOME_9,false),"?");
				sqlQueryObjectInsert_statisticaSettimanaleContenuti.addInsertField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_VALORE_9,false),"?");
				sqlQueryObjectInsert_statisticaSettimanaleContenuti.addInsertField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_NOME_10,false),"?");
				sqlQueryObjectInsert_statisticaSettimanaleContenuti.addInsertField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_VALORE_10,false),"?");
				sqlQueryObjectInsert_statisticaSettimanaleContenuti.addInsertField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.NUMERO_TRANSAZIONI,false),"?");
				sqlQueryObjectInsert_statisticaSettimanaleContenuti.addInsertField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.DIMENSIONI_BYTES_BANDA_COMPLESSIVA,false),"?");
				sqlQueryObjectInsert_statisticaSettimanaleContenuti.addInsertField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.DIMENSIONI_BYTES_BANDA_INTERNA,false),"?");
				sqlQueryObjectInsert_statisticaSettimanaleContenuti.addInsertField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.DIMENSIONI_BYTES_BANDA_ESTERNA,false),"?");
				sqlQueryObjectInsert_statisticaSettimanaleContenuti.addInsertField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.LATENZA_TOTALE,false),"?");
				sqlQueryObjectInsert_statisticaSettimanaleContenuti.addInsertField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.LATENZA_PORTA,false),"?");
				sqlQueryObjectInsert_statisticaSettimanaleContenuti.addInsertField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.LATENZA_SERVIZIO,false),"?");
				sqlQueryObjectInsert_statisticaSettimanaleContenuti.addInsertField("id_stat","?");

				// Insert statisticaSettimanale_statisticaSettimanaleContenuti
				org.openspcoop2.utils.jdbc.IKeyGeneratorObject keyGenerator_statisticaSettimanaleContenuti = this.getStatisticaSettimanaleFetch().getKeyGeneratorObject(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI);
				long id_statisticaSettimanaleContenuti = jdbcUtilities.insertAndReturnGeneratedKey(sqlQueryObjectInsert_statisticaSettimanaleContenuti, keyGenerator_statisticaSettimanaleContenuti, jdbcProperties.isShowSql(),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaSettimanale_statisticaSettimanaleContenuti.getData(),StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.DATA.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaSettimanale_statisticaSettimanaleContenuti.getRisorsaNome(),StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.RISORSA_NOME.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaSettimanale_statisticaSettimanaleContenuti.getRisorsaValore(),StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.RISORSA_VALORE.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaSettimanale_statisticaSettimanaleContenuti.getFiltroNome1(),StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_NOME_1.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaSettimanale_statisticaSettimanaleContenuti.getFiltroValore1(),StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_VALORE_1.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaSettimanale_statisticaSettimanaleContenuti.getFiltroNome2(),StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_NOME_2.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaSettimanale_statisticaSettimanaleContenuti.getFiltroValore2(),StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_VALORE_2.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaSettimanale_statisticaSettimanaleContenuti.getFiltroNome3(),StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_NOME_3.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaSettimanale_statisticaSettimanaleContenuti.getFiltroValore3(),StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_VALORE_3.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaSettimanale_statisticaSettimanaleContenuti.getFiltroNome4(),StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_NOME_4.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaSettimanale_statisticaSettimanaleContenuti.getFiltroValore4(),StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_VALORE_4.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaSettimanale_statisticaSettimanaleContenuti.getFiltroNome5(),StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_NOME_5.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaSettimanale_statisticaSettimanaleContenuti.getFiltroValore5(),StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_VALORE_5.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaSettimanale_statisticaSettimanaleContenuti.getFiltroNome6(),StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_NOME_6.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaSettimanale_statisticaSettimanaleContenuti.getFiltroValore6(),StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_VALORE_6.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaSettimanale_statisticaSettimanaleContenuti.getFiltroNome7(),StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_NOME_7.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaSettimanale_statisticaSettimanaleContenuti.getFiltroValore7(),StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_VALORE_7.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaSettimanale_statisticaSettimanaleContenuti.getFiltroNome8(),StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_NOME_8.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaSettimanale_statisticaSettimanaleContenuti.getFiltroValore8(),StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_VALORE_8.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaSettimanale_statisticaSettimanaleContenuti.getFiltroNome9(),StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_NOME_9.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaSettimanale_statisticaSettimanaleContenuti.getFiltroValore9(),StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_VALORE_9.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaSettimanale_statisticaSettimanaleContenuti.getFiltroNome10(),StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_NOME_10.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaSettimanale_statisticaSettimanaleContenuti.getFiltroValore10(),StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_VALORE_10.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaSettimanale_statisticaSettimanaleContenuti.getNumeroTransazioni(),StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.NUMERO_TRANSAZIONI.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaSettimanale_statisticaSettimanaleContenuti.getDimensioniBytesBandaComplessiva(),StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.DIMENSIONI_BYTES_BANDA_COMPLESSIVA.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaSettimanale_statisticaSettimanaleContenuti.getDimensioniBytesBandaInterna(),StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.DIMENSIONI_BYTES_BANDA_INTERNA.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaSettimanale_statisticaSettimanaleContenuti.getDimensioniBytesBandaEsterna(),StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.DIMENSIONI_BYTES_BANDA_ESTERNA.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaSettimanale_statisticaSettimanaleContenuti.getLatenzaTotale(),StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.LATENZA_TOTALE.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaSettimanale_statisticaSettimanaleContenuti.getLatenzaPorta(),StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.LATENZA_PORTA.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaSettimanale_statisticaSettimanaleContenuti.getLatenzaServizio(),StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.LATENZA_SERVIZIO.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(Long.valueOf(id),Long.class)
				);
				statisticaSettimanale_statisticaSettimanaleContenuti.setId(id_statisticaSettimanaleContenuti);

				ids_statisticaSettimanaleContenuti_da_non_eliminare.add(statisticaSettimanale_statisticaSettimanaleContenuti.getId());
			} else {


				// Object statisticaSettimanale_statisticaSettimanaleContenuti
				ISQLQueryObject sqlQueryObjectUpdate_statisticaSettimanaleContenuti = sqlQueryObjectUpdate.newSQLQueryObject();
				sqlQueryObjectUpdate_statisticaSettimanaleContenuti.setANDLogicOperator(true);
				sqlQueryObjectUpdate_statisticaSettimanaleContenuti.addUpdateTable(this.getStatisticaSettimanaleFieldConverter().toTable(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI));
				boolean isUpdate_statisticaSettimanaleContenuti = true;
				java.util.List<JDBCObject> lstObjects_statisticaSettimanaleContenuti = new java.util.ArrayList<JDBCObject>();
				sqlQueryObjectUpdate_statisticaSettimanaleContenuti.addUpdateField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.DATA,false), "?");
				lstObjects_statisticaSettimanaleContenuti.add(new JDBCObject(statisticaSettimanale_statisticaSettimanaleContenuti.getData(), StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.DATA.getFieldType()));
				sqlQueryObjectUpdate_statisticaSettimanaleContenuti.addUpdateField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.RISORSA_NOME,false), "?");
				lstObjects_statisticaSettimanaleContenuti.add(new JDBCObject(statisticaSettimanale_statisticaSettimanaleContenuti.getRisorsaNome(), StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.RISORSA_NOME.getFieldType()));
				sqlQueryObjectUpdate_statisticaSettimanaleContenuti.addUpdateField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.RISORSA_VALORE,false), "?");
				lstObjects_statisticaSettimanaleContenuti.add(new JDBCObject(statisticaSettimanale_statisticaSettimanaleContenuti.getRisorsaValore(), StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.RISORSA_VALORE.getFieldType()));
				sqlQueryObjectUpdate_statisticaSettimanaleContenuti.addUpdateField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_NOME_1,false), "?");
				lstObjects_statisticaSettimanaleContenuti.add(new JDBCObject(statisticaSettimanale_statisticaSettimanaleContenuti.getFiltroNome1(), StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_NOME_1.getFieldType()));
				sqlQueryObjectUpdate_statisticaSettimanaleContenuti.addUpdateField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_VALORE_1,false), "?");
				lstObjects_statisticaSettimanaleContenuti.add(new JDBCObject(statisticaSettimanale_statisticaSettimanaleContenuti.getFiltroValore1(), StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_VALORE_1.getFieldType()));
				sqlQueryObjectUpdate_statisticaSettimanaleContenuti.addUpdateField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_NOME_2,false), "?");
				lstObjects_statisticaSettimanaleContenuti.add(new JDBCObject(statisticaSettimanale_statisticaSettimanaleContenuti.getFiltroNome2(), StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_NOME_2.getFieldType()));
				sqlQueryObjectUpdate_statisticaSettimanaleContenuti.addUpdateField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_VALORE_2,false), "?");
				lstObjects_statisticaSettimanaleContenuti.add(new JDBCObject(statisticaSettimanale_statisticaSettimanaleContenuti.getFiltroValore2(), StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_VALORE_2.getFieldType()));
				sqlQueryObjectUpdate_statisticaSettimanaleContenuti.addUpdateField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_NOME_3,false), "?");
				lstObjects_statisticaSettimanaleContenuti.add(new JDBCObject(statisticaSettimanale_statisticaSettimanaleContenuti.getFiltroNome3(), StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_NOME_3.getFieldType()));
				sqlQueryObjectUpdate_statisticaSettimanaleContenuti.addUpdateField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_VALORE_3,false), "?");
				lstObjects_statisticaSettimanaleContenuti.add(new JDBCObject(statisticaSettimanale_statisticaSettimanaleContenuti.getFiltroValore3(), StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_VALORE_3.getFieldType()));
				sqlQueryObjectUpdate_statisticaSettimanaleContenuti.addUpdateField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_NOME_4,false), "?");
				lstObjects_statisticaSettimanaleContenuti.add(new JDBCObject(statisticaSettimanale_statisticaSettimanaleContenuti.getFiltroNome4(), StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_NOME_4.getFieldType()));
				sqlQueryObjectUpdate_statisticaSettimanaleContenuti.addUpdateField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_VALORE_4,false), "?");
				lstObjects_statisticaSettimanaleContenuti.add(new JDBCObject(statisticaSettimanale_statisticaSettimanaleContenuti.getFiltroValore4(), StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_VALORE_4.getFieldType()));
				sqlQueryObjectUpdate_statisticaSettimanaleContenuti.addUpdateField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_NOME_5,false), "?");
				lstObjects_statisticaSettimanaleContenuti.add(new JDBCObject(statisticaSettimanale_statisticaSettimanaleContenuti.getFiltroNome5(), StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_NOME_5.getFieldType()));
				sqlQueryObjectUpdate_statisticaSettimanaleContenuti.addUpdateField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_VALORE_5,false), "?");
				lstObjects_statisticaSettimanaleContenuti.add(new JDBCObject(statisticaSettimanale_statisticaSettimanaleContenuti.getFiltroValore5(), StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_VALORE_5.getFieldType()));
				sqlQueryObjectUpdate_statisticaSettimanaleContenuti.addUpdateField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_NOME_6,false), "?");
				lstObjects_statisticaSettimanaleContenuti.add(new JDBCObject(statisticaSettimanale_statisticaSettimanaleContenuti.getFiltroNome6(), StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_NOME_6.getFieldType()));
				sqlQueryObjectUpdate_statisticaSettimanaleContenuti.addUpdateField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_VALORE_6,false), "?");
				lstObjects_statisticaSettimanaleContenuti.add(new JDBCObject(statisticaSettimanale_statisticaSettimanaleContenuti.getFiltroValore6(), StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_VALORE_6.getFieldType()));
				sqlQueryObjectUpdate_statisticaSettimanaleContenuti.addUpdateField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_NOME_7,false), "?");
				lstObjects_statisticaSettimanaleContenuti.add(new JDBCObject(statisticaSettimanale_statisticaSettimanaleContenuti.getFiltroNome7(), StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_NOME_7.getFieldType()));
				sqlQueryObjectUpdate_statisticaSettimanaleContenuti.addUpdateField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_VALORE_7,false), "?");
				lstObjects_statisticaSettimanaleContenuti.add(new JDBCObject(statisticaSettimanale_statisticaSettimanaleContenuti.getFiltroValore7(), StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_VALORE_7.getFieldType()));
				sqlQueryObjectUpdate_statisticaSettimanaleContenuti.addUpdateField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_NOME_8,false), "?");
				lstObjects_statisticaSettimanaleContenuti.add(new JDBCObject(statisticaSettimanale_statisticaSettimanaleContenuti.getFiltroNome8(), StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_NOME_8.getFieldType()));
				sqlQueryObjectUpdate_statisticaSettimanaleContenuti.addUpdateField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_VALORE_8,false), "?");
				lstObjects_statisticaSettimanaleContenuti.add(new JDBCObject(statisticaSettimanale_statisticaSettimanaleContenuti.getFiltroValore8(), StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_VALORE_8.getFieldType()));
				sqlQueryObjectUpdate_statisticaSettimanaleContenuti.addUpdateField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_NOME_9,false), "?");
				lstObjects_statisticaSettimanaleContenuti.add(new JDBCObject(statisticaSettimanale_statisticaSettimanaleContenuti.getFiltroNome9(), StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_NOME_9.getFieldType()));
				sqlQueryObjectUpdate_statisticaSettimanaleContenuti.addUpdateField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_VALORE_9,false), "?");
				lstObjects_statisticaSettimanaleContenuti.add(new JDBCObject(statisticaSettimanale_statisticaSettimanaleContenuti.getFiltroValore9(), StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_VALORE_9.getFieldType()));
				sqlQueryObjectUpdate_statisticaSettimanaleContenuti.addUpdateField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_NOME_10,false), "?");
				lstObjects_statisticaSettimanaleContenuti.add(new JDBCObject(statisticaSettimanale_statisticaSettimanaleContenuti.getFiltroNome10(), StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_NOME_10.getFieldType()));
				sqlQueryObjectUpdate_statisticaSettimanaleContenuti.addUpdateField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_VALORE_10,false), "?");
				lstObjects_statisticaSettimanaleContenuti.add(new JDBCObject(statisticaSettimanale_statisticaSettimanaleContenuti.getFiltroValore10(), StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_VALORE_10.getFieldType()));
				sqlQueryObjectUpdate_statisticaSettimanaleContenuti.addUpdateField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.NUMERO_TRANSAZIONI,false), "?");
				lstObjects_statisticaSettimanaleContenuti.add(new JDBCObject(statisticaSettimanale_statisticaSettimanaleContenuti.getNumeroTransazioni(), StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.NUMERO_TRANSAZIONI.getFieldType()));
				sqlQueryObjectUpdate_statisticaSettimanaleContenuti.addUpdateField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.DIMENSIONI_BYTES_BANDA_COMPLESSIVA,false), "?");
				lstObjects_statisticaSettimanaleContenuti.add(new JDBCObject(statisticaSettimanale_statisticaSettimanaleContenuti.getDimensioniBytesBandaComplessiva(), StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.DIMENSIONI_BYTES_BANDA_COMPLESSIVA.getFieldType()));
				sqlQueryObjectUpdate_statisticaSettimanaleContenuti.addUpdateField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.DIMENSIONI_BYTES_BANDA_INTERNA,false), "?");
				lstObjects_statisticaSettimanaleContenuti.add(new JDBCObject(statisticaSettimanale_statisticaSettimanaleContenuti.getDimensioniBytesBandaInterna(), StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.DIMENSIONI_BYTES_BANDA_INTERNA.getFieldType()));
				sqlQueryObjectUpdate_statisticaSettimanaleContenuti.addUpdateField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.DIMENSIONI_BYTES_BANDA_ESTERNA,false), "?");
				lstObjects_statisticaSettimanaleContenuti.add(new JDBCObject(statisticaSettimanale_statisticaSettimanaleContenuti.getDimensioniBytesBandaEsterna(), StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.DIMENSIONI_BYTES_BANDA_ESTERNA.getFieldType()));
				sqlQueryObjectUpdate_statisticaSettimanaleContenuti.addUpdateField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.LATENZA_TOTALE,false), "?");
				lstObjects_statisticaSettimanaleContenuti.add(new JDBCObject(statisticaSettimanale_statisticaSettimanaleContenuti.getLatenzaTotale(), StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.LATENZA_TOTALE.getFieldType()));
				sqlQueryObjectUpdate_statisticaSettimanaleContenuti.addUpdateField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.LATENZA_PORTA,false), "?");
				lstObjects_statisticaSettimanaleContenuti.add(new JDBCObject(statisticaSettimanale_statisticaSettimanaleContenuti.getLatenzaPorta(), StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.LATENZA_PORTA.getFieldType()));
				sqlQueryObjectUpdate_statisticaSettimanaleContenuti.addUpdateField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.LATENZA_SERVIZIO,false), "?");
				lstObjects_statisticaSettimanaleContenuti.add(new JDBCObject(statisticaSettimanale_statisticaSettimanaleContenuti.getLatenzaServizio(), StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.LATENZA_SERVIZIO.getFieldType()));
				sqlQueryObjectUpdate_statisticaSettimanaleContenuti.addWhereCondition("id=?");
				ids_statisticaSettimanaleContenuti_da_non_eliminare.add(statisticaSettimanale_statisticaSettimanaleContenuti.getId());
				lstObjects_statisticaSettimanaleContenuti.add(new JDBCObject(Long.valueOf(statisticaSettimanale_statisticaSettimanaleContenuti.getId()),Long.class));

				if(isUpdate_statisticaSettimanaleContenuti) {
					// Update statisticaSettimanale_statisticaSettimanaleContenuti
					jdbcUtilities.executeUpdate(sqlQueryObjectUpdate_statisticaSettimanaleContenuti.createSQLUpdate(), jdbcProperties.isShowSql(), 
						lstObjects_statisticaSettimanaleContenuti.toArray(new JDBCObject[]{}));
				}
			}
		} // fine for statisticaSettimanale_statisticaSettimanaleContenuti

		// elimino tutte le occorrenze di statisticaSettimanale_statisticaSettimanaleContenuti non presenti nell'update

		ISQLQueryObject sqlQueryObjectUpdate_statisticaSettimanaleContenuti_deleteList = sqlQueryObjectUpdate.newSQLQueryObject();
		sqlQueryObjectUpdate_statisticaSettimanaleContenuti_deleteList.setANDLogicOperator(true);
		sqlQueryObjectUpdate_statisticaSettimanaleContenuti_deleteList.addDeleteTable(this.getStatisticaSettimanaleFieldConverter().toTable(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI));
		java.util.List<JDBCObject> jdbcObjects_statisticaSettimanaleContenuti_delete = new java.util.ArrayList<JDBCObject>();

		sqlQueryObjectUpdate_statisticaSettimanaleContenuti_deleteList.addWhereCondition("id_stat=?");
		jdbcObjects_statisticaSettimanaleContenuti_delete.add(new JDBCObject(statisticaSettimanale.getId(), Long.class));

		StringBuffer marks_statisticaSettimanaleContenuti = new StringBuffer();
		if(ids_statisticaSettimanaleContenuti_da_non_eliminare.size() > 0) {
			for(Long ids : ids_statisticaSettimanaleContenuti_da_non_eliminare) {
				if(marks_statisticaSettimanaleContenuti.length() > 0) {
					marks_statisticaSettimanaleContenuti.append(",");
				}
				marks_statisticaSettimanaleContenuti.append("?");
				jdbcObjects_statisticaSettimanaleContenuti_delete.add(new JDBCObject(ids, Long.class));

			}
			sqlQueryObjectUpdate_statisticaSettimanaleContenuti_deleteList.addWhereCondition("id NOT IN ("+marks_statisticaSettimanaleContenuti.toString()+")");
		}

		jdbcUtilities.execute(sqlQueryObjectUpdate_statisticaSettimanaleContenuti_deleteList.createSQLDelete(), jdbcProperties.isShowSql(), jdbcObjects_statisticaSettimanaleContenuti_delete.toArray(new JDBCObject[]{}));



	}
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, StatisticaSettimanale statisticaSettimanale, UpdateField ... updateFields) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		
		JDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getStatisticaSettimanaleFieldConverter().toTable(StatisticaSettimanale.model()), 
				this._getMapTableToPKColumn(), 
				this._getRootTablePrimaryKeyValues(jdbcProperties, log, connection, sqlQueryObject, statisticaSettimanale),
				this.getStatisticaSettimanaleFieldConverter(), this, null, updateFields);
	}
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, StatisticaSettimanale statisticaSettimanale, IExpression condition, UpdateField ... updateFields) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		
		JDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getStatisticaSettimanaleFieldConverter().toTable(StatisticaSettimanale.model()), 
				this._getMapTableToPKColumn(), 
				this._getRootTablePrimaryKeyValues(jdbcProperties, log, connection, sqlQueryObject, statisticaSettimanale),
				this.getStatisticaSettimanaleFieldConverter(), this, condition, updateFields);
	}
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, StatisticaSettimanale statisticaSettimanale, UpdateModel ... updateModels) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		
		JDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getStatisticaSettimanaleFieldConverter().toTable(StatisticaSettimanale.model()), 
				this._getMapTableToPKColumn(), 
				this._getRootTablePrimaryKeyValues(jdbcProperties, log, connection, sqlQueryObject, statisticaSettimanale),
				this.getStatisticaSettimanaleFieldConverter(), this, updateModels);
	}	
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, UpdateField ... updateFields) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		java.util.List<Object> ids = new java.util.ArrayList<Object>();
		ids.add(tableId);
		JDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getStatisticaSettimanaleFieldConverter().toTable(StatisticaSettimanale.model()), 
				this._getMapTableToPKColumn(), 
				ids,
				this.getStatisticaSettimanaleFieldConverter(), this, null, updateFields);
	}
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, IExpression condition, UpdateField ... updateFields) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		java.util.List<Object> ids = new java.util.ArrayList<Object>();
		ids.add(tableId);
		JDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getStatisticaSettimanaleFieldConverter().toTable(StatisticaSettimanale.model()), 
				this._getMapTableToPKColumn(), 
				ids,
				this.getStatisticaSettimanaleFieldConverter(), this, condition, updateFields);
	}
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, UpdateModel ... updateModels) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		java.util.List<Object> ids = new java.util.ArrayList<Object>();
		ids.add(tableId);
		JDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getStatisticaSettimanaleFieldConverter().toTable(StatisticaSettimanale.model()), 
				this._getMapTableToPKColumn(), 
				ids,
				this.getStatisticaSettimanaleFieldConverter(), this, updateModels);
	}
	
	@Override
	public void updateOrCreate(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, StatisticaSettimanale statisticaSettimanale, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotImplementedException,ServiceException,Exception {
	
		Long id = statisticaSettimanale.getId();
		if(id != null && this.exists(jdbcProperties, log, connection, sqlQueryObject, id)) {
			this.update(jdbcProperties, log, connection, sqlQueryObject, statisticaSettimanale,idMappingResolutionBehaviour);		
		} else {
			this.create(jdbcProperties, log, connection, sqlQueryObject, statisticaSettimanale,idMappingResolutionBehaviour);
		}
		
	}
	
	@Override
	public void updateOrCreate(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, StatisticaSettimanale statisticaSettimanale, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotImplementedException,ServiceException,Exception {
		if(this.exists(jdbcProperties, log, connection, sqlQueryObject, tableId)) {
			this.update(jdbcProperties, log, connection, sqlQueryObject, tableId, statisticaSettimanale,idMappingResolutionBehaviour);
		} else {
			this.create(jdbcProperties, log, connection, sqlQueryObject, statisticaSettimanale,idMappingResolutionBehaviour);
		}
	}
	
	@Override
	public void delete(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, StatisticaSettimanale statisticaSettimanale) throws NotImplementedException,ServiceException,Exception {
		
		
		Long longId = null;
		if(statisticaSettimanale.getId()==null){
			throw new Exception("Parameter "+statisticaSettimanale.getClass().getName()+".id is null");
		}
		if(statisticaSettimanale.getId()<=0){
			throw new Exception("Parameter "+statisticaSettimanale.getClass().getName()+".id is less equals 0");
		}
		longId = statisticaSettimanale.getId();
		
		this._delete(jdbcProperties, log, connection, sqlQueryObject, longId);
		
	}

	private void _delete(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, Long id) throws NotImplementedException,ServiceException,Exception {
	
		if(id!=null && id.longValue()<=0){
			throw new ServiceException("Id is less equals 0");
		}
		
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);
		
		ISQLQueryObject sqlQueryObjectDelete = sqlQueryObject.newSQLQueryObject();
		

		//Recupero oggetto _statisticaSettimanaleContenuti
		ISQLQueryObject sqlQueryObjectDelete_statisticaSettimanaleContenuti_getToDelete = sqlQueryObjectDelete.newSQLQueryObject();
		sqlQueryObjectDelete_statisticaSettimanaleContenuti_getToDelete.setANDLogicOperator(true);
		sqlQueryObjectDelete_statisticaSettimanaleContenuti_getToDelete.addFromTable(this.getStatisticaSettimanaleFieldConverter().toTable(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI));
		sqlQueryObjectDelete_statisticaSettimanaleContenuti_getToDelete.addWhereCondition("id_stat=?");
		java.util.List<Object> statisticaSettimanale_statisticaSettimanaleContenuti_toDelete_list = (java.util.List<Object>) jdbcUtilities.executeQuery(sqlQueryObjectDelete_statisticaSettimanaleContenuti_getToDelete.createSQLQuery(), jdbcProperties.isShowSql(), StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI, this.getStatisticaSettimanaleFetch(),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(Long.valueOf(id),Long.class));

		// for statisticaSettimanale_statisticaSettimanaleContenuti
		for (Object statisticaSettimanale_statisticaSettimanaleContenuti_object : statisticaSettimanale_statisticaSettimanaleContenuti_toDelete_list) {
			StatisticaContenuti statisticaSettimanale_statisticaSettimanaleContenuti = (StatisticaContenuti) statisticaSettimanale_statisticaSettimanaleContenuti_object;

			// Object statisticaSettimanale_statisticaSettimanaleContenuti
			ISQLQueryObject sqlQueryObjectDelete_statisticaSettimanaleContenuti = sqlQueryObjectDelete.newSQLQueryObject();
			sqlQueryObjectDelete_statisticaSettimanaleContenuti.setANDLogicOperator(true);
			sqlQueryObjectDelete_statisticaSettimanaleContenuti.addDeleteTable(this.getStatisticaSettimanaleFieldConverter().toTable(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI));
			sqlQueryObjectDelete_statisticaSettimanaleContenuti.addWhereCondition("id=?");

			// Delete statisticaSettimanale_statisticaSettimanaleContenuti
			if(statisticaSettimanale_statisticaSettimanaleContenuti != null){
				jdbcUtilities.execute(sqlQueryObjectDelete_statisticaSettimanaleContenuti.createSQLDelete(), jdbcProperties.isShowSql(), 
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(Long.valueOf(statisticaSettimanale_statisticaSettimanaleContenuti.getId()),Long.class));
			}
		} // fine for statisticaSettimanale_statisticaSettimanaleContenuti

		// Object statisticaSettimanale_statisticaBase_toDelete
		sqlQueryObjectDelete.setANDLogicOperator(true);
		sqlQueryObjectDelete.addDeleteTable(this.getStatisticaSettimanaleFieldConverter().toTable(StatisticaSettimanale.model().STATISTICA_BASE));
		if(id != null)
			sqlQueryObjectDelete.addWhereCondition("id=?");

		// Delete statisticaSettimanale
		jdbcUtilities.execute(sqlQueryObjectDelete.createSQLDelete(), jdbcProperties.isShowSql(), 
			new JDBCObject(id,Long.class));

	}

	
	@Override
	public NonNegativeNumber deleteAll(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject) throws NotImplementedException,ServiceException,Exception {
		
		return this.deleteAll(jdbcProperties, log, connection, sqlQueryObject, new JDBCExpression(this.getStatisticaSettimanaleFieldConverter()));

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
