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

import org.openspcoop2.core.statistiche.Statistica;
import org.openspcoop2.core.statistiche.StatisticaContenuti;
import org.openspcoop2.core.statistiche.StatisticaOraria;
import org.openspcoop2.core.statistiche.dao.jdbc.JDBCServiceManager;

/**     
 * JDBCStatisticaOrariaServiceImpl
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JDBCStatisticaOrariaServiceImpl extends JDBCStatisticaOrariaServiceSearchImpl
	implements IJDBCServiceCRUDWithoutId<StatisticaOraria, JDBCServiceManager> {

	@Override
	public void create(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, StatisticaOraria statisticaOraria, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotImplementedException,ServiceException,Exception {

		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);
		
		ISQLQueryObject sqlQueryObjectInsert = sqlQueryObject.newSQLQueryObject();
				


		// Object statisticaOraria.getStatisticaBase()
		sqlQueryObjectInsert.addInsertTable(this.getStatisticaOrariaFieldConverter().toTable(StatisticaOraria.model().STATISTICA_BASE));
		sqlQueryObjectInsert.addInsertField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_BASE.DATA,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_BASE.ID_PORTA,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_BASE.TIPO_PORTA,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_BASE.TIPO_MITTENTE,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_BASE.MITTENTE,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_BASE.TIPO_DESTINATARIO,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_BASE.DESTINATARIO,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_BASE.TIPO_SERVIZIO,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_BASE.SERVIZIO,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_BASE.VERSIONE_SERVIZIO,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_BASE.AZIONE,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_BASE.SERVIZIO_APPLICATIVO,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_BASE.TRASPORTO_MITTENTE,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_BASE.TOKEN_ISSUER,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_BASE.TOKEN_CLIENT_ID,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_BASE.TOKEN_SUBJECT,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_BASE.TOKEN_USERNAME,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_BASE.TOKEN_MAIL,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_BASE.ESITO,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_BASE.ESITO_CONTESTO,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_BASE.NUMERO_TRANSAZIONI,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_BASE.DIMENSIONI_BYTES_BANDA_COMPLESSIVA,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_BASE.DIMENSIONI_BYTES_BANDA_INTERNA,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_BASE.DIMENSIONI_BYTES_BANDA_ESTERNA,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_BASE.LATENZA_TOTALE,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_BASE.LATENZA_PORTA,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_BASE.LATENZA_SERVIZIO,false),"?");

		// Insert statisticaOraria
		org.openspcoop2.utils.jdbc.IKeyGeneratorObject keyGenerator = this.getStatisticaOrariaFetch().getKeyGeneratorObject(StatisticaOraria.model());
		long id = jdbcUtilities.insertAndReturnGeneratedKey(sqlQueryObjectInsert, keyGenerator, jdbcProperties.isShowSql(),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaOraria.getStatisticaBase().getData(),StatisticaOraria.model().STATISTICA_BASE.DATA.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaOraria.getStatisticaBase().getIdPorta(),StatisticaOraria.model().STATISTICA_BASE.ID_PORTA.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaOraria.getStatisticaBase().getTipoPorta(),StatisticaOraria.model().STATISTICA_BASE.TIPO_PORTA.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaOraria.getStatisticaBase().getTipoMittente(),StatisticaOraria.model().STATISTICA_BASE.TIPO_MITTENTE.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaOraria.getStatisticaBase().getMittente(),StatisticaOraria.model().STATISTICA_BASE.MITTENTE.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaOraria.getStatisticaBase().getTipoDestinatario(),StatisticaOraria.model().STATISTICA_BASE.TIPO_DESTINATARIO.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaOraria.getStatisticaBase().getDestinatario(),StatisticaOraria.model().STATISTICA_BASE.DESTINATARIO.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaOraria.getStatisticaBase().getTipoServizio(),StatisticaOraria.model().STATISTICA_BASE.TIPO_SERVIZIO.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaOraria.getStatisticaBase().getServizio(),StatisticaOraria.model().STATISTICA_BASE.SERVIZIO.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaOraria.getStatisticaBase().getVersioneServizio(),StatisticaOraria.model().STATISTICA_BASE.VERSIONE_SERVIZIO.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaOraria.getStatisticaBase().getAzione(),StatisticaOraria.model().STATISTICA_BASE.AZIONE.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaOraria.getStatisticaBase().getServizioApplicativo(),StatisticaOraria.model().STATISTICA_BASE.SERVIZIO_APPLICATIVO.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaOraria.getStatisticaBase().getTrasportoMittente(),StatisticaOraria.model().STATISTICA_BASE.TRASPORTO_MITTENTE.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaOraria.getStatisticaBase().getTokenIssuer(),StatisticaOraria.model().STATISTICA_BASE.TOKEN_ISSUER.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaOraria.getStatisticaBase().getTokenClientId(),StatisticaOraria.model().STATISTICA_BASE.TOKEN_CLIENT_ID.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaOraria.getStatisticaBase().getTokenSubject(),StatisticaOraria.model().STATISTICA_BASE.TOKEN_SUBJECT.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaOraria.getStatisticaBase().getTokenUsername(),StatisticaOraria.model().STATISTICA_BASE.TOKEN_USERNAME.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaOraria.getStatisticaBase().getTokenMail(),StatisticaOraria.model().STATISTICA_BASE.TOKEN_MAIL.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaOraria.getStatisticaBase().getEsito(),StatisticaOraria.model().STATISTICA_BASE.ESITO.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaOraria.getStatisticaBase().getEsitoContesto(),StatisticaOraria.model().STATISTICA_BASE.ESITO_CONTESTO.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaOraria.getStatisticaBase().getNumeroTransazioni(),StatisticaOraria.model().STATISTICA_BASE.NUMERO_TRANSAZIONI.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaOraria.getStatisticaBase().getDimensioniBytesBandaComplessiva(),StatisticaOraria.model().STATISTICA_BASE.DIMENSIONI_BYTES_BANDA_COMPLESSIVA.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaOraria.getStatisticaBase().getDimensioniBytesBandaInterna(),StatisticaOraria.model().STATISTICA_BASE.DIMENSIONI_BYTES_BANDA_INTERNA.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaOraria.getStatisticaBase().getDimensioniBytesBandaEsterna(),StatisticaOraria.model().STATISTICA_BASE.DIMENSIONI_BYTES_BANDA_ESTERNA.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaOraria.getStatisticaBase().getLatenzaTotale(),StatisticaOraria.model().STATISTICA_BASE.LATENZA_TOTALE.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaOraria.getStatisticaBase().getLatenzaPorta(),StatisticaOraria.model().STATISTICA_BASE.LATENZA_PORTA.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaOraria.getStatisticaBase().getLatenzaServizio(),StatisticaOraria.model().STATISTICA_BASE.LATENZA_SERVIZIO.getFieldType())
		);
		statisticaOraria.setId(id);

		// for statisticaOraria
		for (int i = 0; i < statisticaOraria.getStatisticaOrariaContenutiList().size(); i++) {


			// Object statisticaOraria.getStatisticaOrariaContenutiList().get(i)
			ISQLQueryObject sqlQueryObjectInsert_statisticaOrariaContenuti = sqlQueryObjectInsert.newSQLQueryObject();
			sqlQueryObjectInsert_statisticaOrariaContenuti.addInsertTable(this.getStatisticaOrariaFieldConverter().toTable(StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI));
			sqlQueryObjectInsert_statisticaOrariaContenuti.addInsertField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.DATA,false),"?");
			sqlQueryObjectInsert_statisticaOrariaContenuti.addInsertField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.RISORSA_NOME,false),"?");
			sqlQueryObjectInsert_statisticaOrariaContenuti.addInsertField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.RISORSA_VALORE,false),"?");
			sqlQueryObjectInsert_statisticaOrariaContenuti.addInsertField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_NOME_1,false),"?");
			sqlQueryObjectInsert_statisticaOrariaContenuti.addInsertField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_VALORE_1,false),"?");
			sqlQueryObjectInsert_statisticaOrariaContenuti.addInsertField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_NOME_2,false),"?");
			sqlQueryObjectInsert_statisticaOrariaContenuti.addInsertField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_VALORE_2,false),"?");
			sqlQueryObjectInsert_statisticaOrariaContenuti.addInsertField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_NOME_3,false),"?");
			sqlQueryObjectInsert_statisticaOrariaContenuti.addInsertField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_VALORE_3,false),"?");
			sqlQueryObjectInsert_statisticaOrariaContenuti.addInsertField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_NOME_4,false),"?");
			sqlQueryObjectInsert_statisticaOrariaContenuti.addInsertField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_VALORE_4,false),"?");
			sqlQueryObjectInsert_statisticaOrariaContenuti.addInsertField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_NOME_5,false),"?");
			sqlQueryObjectInsert_statisticaOrariaContenuti.addInsertField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_VALORE_5,false),"?");
			sqlQueryObjectInsert_statisticaOrariaContenuti.addInsertField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_NOME_6,false),"?");
			sqlQueryObjectInsert_statisticaOrariaContenuti.addInsertField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_VALORE_6,false),"?");
			sqlQueryObjectInsert_statisticaOrariaContenuti.addInsertField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_NOME_7,false),"?");
			sqlQueryObjectInsert_statisticaOrariaContenuti.addInsertField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_VALORE_7,false),"?");
			sqlQueryObjectInsert_statisticaOrariaContenuti.addInsertField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_NOME_8,false),"?");
			sqlQueryObjectInsert_statisticaOrariaContenuti.addInsertField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_VALORE_8,false),"?");
			sqlQueryObjectInsert_statisticaOrariaContenuti.addInsertField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_NOME_9,false),"?");
			sqlQueryObjectInsert_statisticaOrariaContenuti.addInsertField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_VALORE_9,false),"?");
			sqlQueryObjectInsert_statisticaOrariaContenuti.addInsertField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_NOME_10,false),"?");
			sqlQueryObjectInsert_statisticaOrariaContenuti.addInsertField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_VALORE_10,false),"?");
			sqlQueryObjectInsert_statisticaOrariaContenuti.addInsertField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.NUMERO_TRANSAZIONI,false),"?");
			sqlQueryObjectInsert_statisticaOrariaContenuti.addInsertField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.DIMENSIONI_BYTES_BANDA_COMPLESSIVA,false),"?");
			sqlQueryObjectInsert_statisticaOrariaContenuti.addInsertField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.DIMENSIONI_BYTES_BANDA_INTERNA,false),"?");
			sqlQueryObjectInsert_statisticaOrariaContenuti.addInsertField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.DIMENSIONI_BYTES_BANDA_ESTERNA,false),"?");
			sqlQueryObjectInsert_statisticaOrariaContenuti.addInsertField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.LATENZA_TOTALE,false),"?");
			sqlQueryObjectInsert_statisticaOrariaContenuti.addInsertField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.LATENZA_PORTA,false),"?");
			sqlQueryObjectInsert_statisticaOrariaContenuti.addInsertField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.LATENZA_SERVIZIO,false),"?");
			sqlQueryObjectInsert_statisticaOrariaContenuti.addInsertField("id_stat","?");

			// Insert statisticaOraria.getStatisticaOrariaContenutiList().get(i)
			org.openspcoop2.utils.jdbc.IKeyGeneratorObject keyGenerator_statisticaOrariaContenuti = this.getStatisticaOrariaFetch().getKeyGeneratorObject(StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI);
			long id_statisticaOrariaContenuti = jdbcUtilities.insertAndReturnGeneratedKey(sqlQueryObjectInsert_statisticaOrariaContenuti, keyGenerator_statisticaOrariaContenuti, jdbcProperties.isShowSql(),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaOraria.getStatisticaOrariaContenutiList().get(i).getData(),StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.DATA.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaOraria.getStatisticaOrariaContenutiList().get(i).getRisorsaNome(),StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.RISORSA_NOME.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaOraria.getStatisticaOrariaContenutiList().get(i).getRisorsaValore(),StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.RISORSA_VALORE.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaOraria.getStatisticaOrariaContenutiList().get(i).getFiltroNome1(),StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_NOME_1.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaOraria.getStatisticaOrariaContenutiList().get(i).getFiltroValore1(),StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_VALORE_1.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaOraria.getStatisticaOrariaContenutiList().get(i).getFiltroNome2(),StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_NOME_2.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaOraria.getStatisticaOrariaContenutiList().get(i).getFiltroValore2(),StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_VALORE_2.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaOraria.getStatisticaOrariaContenutiList().get(i).getFiltroNome3(),StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_NOME_3.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaOraria.getStatisticaOrariaContenutiList().get(i).getFiltroValore3(),StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_VALORE_3.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaOraria.getStatisticaOrariaContenutiList().get(i).getFiltroNome4(),StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_NOME_4.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaOraria.getStatisticaOrariaContenutiList().get(i).getFiltroValore4(),StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_VALORE_4.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaOraria.getStatisticaOrariaContenutiList().get(i).getFiltroNome5(),StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_NOME_5.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaOraria.getStatisticaOrariaContenutiList().get(i).getFiltroValore5(),StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_VALORE_5.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaOraria.getStatisticaOrariaContenutiList().get(i).getFiltroNome6(),StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_NOME_6.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaOraria.getStatisticaOrariaContenutiList().get(i).getFiltroValore6(),StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_VALORE_6.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaOraria.getStatisticaOrariaContenutiList().get(i).getFiltroNome7(),StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_NOME_7.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaOraria.getStatisticaOrariaContenutiList().get(i).getFiltroValore7(),StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_VALORE_7.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaOraria.getStatisticaOrariaContenutiList().get(i).getFiltroNome8(),StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_NOME_8.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaOraria.getStatisticaOrariaContenutiList().get(i).getFiltroValore8(),StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_VALORE_8.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaOraria.getStatisticaOrariaContenutiList().get(i).getFiltroNome9(),StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_NOME_9.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaOraria.getStatisticaOrariaContenutiList().get(i).getFiltroValore9(),StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_VALORE_9.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaOraria.getStatisticaOrariaContenutiList().get(i).getFiltroNome10(),StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_NOME_10.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaOraria.getStatisticaOrariaContenutiList().get(i).getFiltroValore10(),StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_VALORE_10.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaOraria.getStatisticaOrariaContenutiList().get(i).getNumeroTransazioni(),StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.NUMERO_TRANSAZIONI.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaOraria.getStatisticaOrariaContenutiList().get(i).getDimensioniBytesBandaComplessiva(),StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.DIMENSIONI_BYTES_BANDA_COMPLESSIVA.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaOraria.getStatisticaOrariaContenutiList().get(i).getDimensioniBytesBandaInterna(),StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.DIMENSIONI_BYTES_BANDA_INTERNA.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaOraria.getStatisticaOrariaContenutiList().get(i).getDimensioniBytesBandaEsterna(),StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.DIMENSIONI_BYTES_BANDA_ESTERNA.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaOraria.getStatisticaOrariaContenutiList().get(i).getLatenzaTotale(),StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.LATENZA_TOTALE.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaOraria.getStatisticaOrariaContenutiList().get(i).getLatenzaPorta(),StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.LATENZA_PORTA.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaOraria.getStatisticaOrariaContenutiList().get(i).getLatenzaServizio(),StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.LATENZA_SERVIZIO.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(Long.valueOf(id),Long.class)
			);
			statisticaOraria.getStatisticaOrariaContenutiList().get(i).setId(id_statisticaOrariaContenuti);
		} // fine for 

		
	}

	@Override
	public void update(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, StatisticaOraria statisticaOraria, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		
		Long tableId = statisticaOraria.getId();
		if(tableId==null || tableId<=0){
			throw new Exception("Retrieve tableId failed");
		}

		this.update(jdbcProperties, log, connection, sqlQueryObject, tableId, statisticaOraria, idMappingResolutionBehaviour);
	}
	@Override
	public void update(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, StatisticaOraria statisticaOraria, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException, NotImplementedException, ServiceException, Exception {
	
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);
		
		ISQLQueryObject sqlQueryObjectInsert = sqlQueryObject.newSQLQueryObject();
		ISQLQueryObject sqlQueryObjectDelete = sqlQueryObjectInsert.newSQLQueryObject();
		ISQLQueryObject sqlQueryObjectGet = sqlQueryObjectDelete.newSQLQueryObject();
		ISQLQueryObject sqlQueryObjectUpdate = sqlQueryObjectGet.newSQLQueryObject();
		

		Statistica statisticaOraria_statisticaBase = statisticaOraria.getStatisticaBase();

		// Object statisticaOraria_statisticaBase
		sqlQueryObjectUpdate.setANDLogicOperator(true);
		sqlQueryObjectUpdate.addUpdateTable(this.getStatisticaOrariaFieldConverter().toTable(StatisticaOraria.model().STATISTICA_BASE));
		boolean isUpdate = true;
		java.util.List<JDBCObject> lstObjects = new java.util.ArrayList<JDBCObject>();
		sqlQueryObjectUpdate.addUpdateField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_BASE.DATA,false), "?");
		lstObjects.add(new JDBCObject(statisticaOraria_statisticaBase.getData(), StatisticaOraria.model().STATISTICA_BASE.DATA.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_BASE.ID_PORTA,false), "?");
		lstObjects.add(new JDBCObject(statisticaOraria_statisticaBase.getIdPorta(), StatisticaOraria.model().STATISTICA_BASE.ID_PORTA.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_BASE.TIPO_PORTA,false), "?");
		lstObjects.add(new JDBCObject(statisticaOraria_statisticaBase.getTipoPorta(), StatisticaOraria.model().STATISTICA_BASE.TIPO_PORTA.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_BASE.TIPO_MITTENTE,false), "?");
		lstObjects.add(new JDBCObject(statisticaOraria_statisticaBase.getTipoMittente(), StatisticaOraria.model().STATISTICA_BASE.TIPO_MITTENTE.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_BASE.MITTENTE,false), "?");
		lstObjects.add(new JDBCObject(statisticaOraria_statisticaBase.getMittente(), StatisticaOraria.model().STATISTICA_BASE.MITTENTE.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_BASE.TIPO_DESTINATARIO,false), "?");
		lstObjects.add(new JDBCObject(statisticaOraria_statisticaBase.getTipoDestinatario(), StatisticaOraria.model().STATISTICA_BASE.TIPO_DESTINATARIO.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_BASE.DESTINATARIO,false), "?");
		lstObjects.add(new JDBCObject(statisticaOraria_statisticaBase.getDestinatario(), StatisticaOraria.model().STATISTICA_BASE.DESTINATARIO.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_BASE.TIPO_SERVIZIO,false), "?");
		lstObjects.add(new JDBCObject(statisticaOraria_statisticaBase.getTipoServizio(), StatisticaOraria.model().STATISTICA_BASE.TIPO_SERVIZIO.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_BASE.SERVIZIO,false), "?");
		lstObjects.add(new JDBCObject(statisticaOraria_statisticaBase.getServizio(), StatisticaOraria.model().STATISTICA_BASE.SERVIZIO.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_BASE.VERSIONE_SERVIZIO,false), "?");
		lstObjects.add(new JDBCObject(statisticaOraria_statisticaBase.getVersioneServizio(), StatisticaOraria.model().STATISTICA_BASE.VERSIONE_SERVIZIO.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_BASE.AZIONE,false), "?");
		lstObjects.add(new JDBCObject(statisticaOraria_statisticaBase.getAzione(), StatisticaOraria.model().STATISTICA_BASE.AZIONE.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_BASE.SERVIZIO_APPLICATIVO,false), "?");
		lstObjects.add(new JDBCObject(statisticaOraria_statisticaBase.getServizioApplicativo(), StatisticaOraria.model().STATISTICA_BASE.SERVIZIO_APPLICATIVO.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_BASE.TRASPORTO_MITTENTE,false), "?");
		lstObjects.add(new JDBCObject(statisticaOraria_statisticaBase.getTrasportoMittente(), StatisticaOraria.model().STATISTICA_BASE.TRASPORTO_MITTENTE.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_BASE.TOKEN_ISSUER,false), "?");
		lstObjects.add(new JDBCObject(statisticaOraria_statisticaBase.getTokenIssuer(), StatisticaOraria.model().STATISTICA_BASE.TOKEN_ISSUER.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_BASE.TOKEN_CLIENT_ID,false), "?");
		lstObjects.add(new JDBCObject(statisticaOraria_statisticaBase.getTokenClientId(), StatisticaOraria.model().STATISTICA_BASE.TOKEN_CLIENT_ID.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_BASE.TOKEN_SUBJECT,false), "?");
		lstObjects.add(new JDBCObject(statisticaOraria_statisticaBase.getTokenSubject(), StatisticaOraria.model().STATISTICA_BASE.TOKEN_SUBJECT.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_BASE.TOKEN_USERNAME,false), "?");
		lstObjects.add(new JDBCObject(statisticaOraria_statisticaBase.getTokenUsername(), StatisticaOraria.model().STATISTICA_BASE.TOKEN_USERNAME.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_BASE.TOKEN_MAIL,false), "?");
		lstObjects.add(new JDBCObject(statisticaOraria_statisticaBase.getTokenMail(), StatisticaOraria.model().STATISTICA_BASE.TOKEN_MAIL.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_BASE.ESITO,false), "?");
		lstObjects.add(new JDBCObject(statisticaOraria_statisticaBase.getEsito(), StatisticaOraria.model().STATISTICA_BASE.ESITO.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_BASE.ESITO_CONTESTO,false), "?");
		lstObjects.add(new JDBCObject(statisticaOraria_statisticaBase.getEsitoContesto(), StatisticaOraria.model().STATISTICA_BASE.ESITO_CONTESTO.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_BASE.NUMERO_TRANSAZIONI,false), "?");
		lstObjects.add(new JDBCObject(statisticaOraria_statisticaBase.getNumeroTransazioni(), StatisticaOraria.model().STATISTICA_BASE.NUMERO_TRANSAZIONI.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_BASE.DIMENSIONI_BYTES_BANDA_COMPLESSIVA,false), "?");
		lstObjects.add(new JDBCObject(statisticaOraria_statisticaBase.getDimensioniBytesBandaComplessiva(), StatisticaOraria.model().STATISTICA_BASE.DIMENSIONI_BYTES_BANDA_COMPLESSIVA.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_BASE.DIMENSIONI_BYTES_BANDA_INTERNA,false), "?");
		lstObjects.add(new JDBCObject(statisticaOraria_statisticaBase.getDimensioniBytesBandaInterna(), StatisticaOraria.model().STATISTICA_BASE.DIMENSIONI_BYTES_BANDA_INTERNA.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_BASE.DIMENSIONI_BYTES_BANDA_ESTERNA,false), "?");
		lstObjects.add(new JDBCObject(statisticaOraria_statisticaBase.getDimensioniBytesBandaEsterna(), StatisticaOraria.model().STATISTICA_BASE.DIMENSIONI_BYTES_BANDA_ESTERNA.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_BASE.LATENZA_TOTALE,false), "?");
		lstObjects.add(new JDBCObject(statisticaOraria_statisticaBase.getLatenzaTotale(), StatisticaOraria.model().STATISTICA_BASE.LATENZA_TOTALE.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_BASE.LATENZA_PORTA,false), "?");
		lstObjects.add(new JDBCObject(statisticaOraria_statisticaBase.getLatenzaPorta(), StatisticaOraria.model().STATISTICA_BASE.LATENZA_PORTA.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_BASE.LATENZA_SERVIZIO,false), "?");
		lstObjects.add(new JDBCObject(statisticaOraria_statisticaBase.getLatenzaServizio(), StatisticaOraria.model().STATISTICA_BASE.LATENZA_SERVIZIO.getFieldType()));
		sqlQueryObjectUpdate.addWhereCondition("id=?");
		lstObjects.add(new JDBCObject(tableId, Long.class));

		if(isUpdate) {
			// Update statisticaOraria
			jdbcUtilities.executeUpdate(sqlQueryObjectUpdate.createSQLUpdate(), jdbcProperties.isShowSql(), 
				lstObjects.toArray(new JDBCObject[]{}));
		}
		// for statisticaOraria_statisticaOrariaContenuti

		java.util.List<Long> ids_statisticaOrariaContenuti_da_non_eliminare = new java.util.ArrayList<Long>();
		for (Object statisticaOraria_statisticaOrariaContenuti_object : statisticaOraria.getStatisticaOrariaContenutiList()) {
			StatisticaContenuti statisticaOraria_statisticaOrariaContenuti = (StatisticaContenuti) statisticaOraria_statisticaOrariaContenuti_object;
			if(statisticaOraria_statisticaOrariaContenuti.getId() == null || statisticaOraria_statisticaOrariaContenuti.getId().longValue() <= 0) {

				long id = statisticaOraria.getId();			

				// Object statisticaOraria_statisticaOrariaContenuti
				ISQLQueryObject sqlQueryObjectInsert_statisticaOrariaContenuti = sqlQueryObjectInsert.newSQLQueryObject();
				sqlQueryObjectInsert_statisticaOrariaContenuti.addInsertTable(this.getStatisticaOrariaFieldConverter().toTable(StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI));
				sqlQueryObjectInsert_statisticaOrariaContenuti.addInsertField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.DATA,false),"?");
				sqlQueryObjectInsert_statisticaOrariaContenuti.addInsertField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.RISORSA_NOME,false),"?");
				sqlQueryObjectInsert_statisticaOrariaContenuti.addInsertField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.RISORSA_VALORE,false),"?");
				sqlQueryObjectInsert_statisticaOrariaContenuti.addInsertField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_NOME_1,false),"?");
				sqlQueryObjectInsert_statisticaOrariaContenuti.addInsertField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_VALORE_1,false),"?");
				sqlQueryObjectInsert_statisticaOrariaContenuti.addInsertField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_NOME_2,false),"?");
				sqlQueryObjectInsert_statisticaOrariaContenuti.addInsertField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_VALORE_2,false),"?");
				sqlQueryObjectInsert_statisticaOrariaContenuti.addInsertField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_NOME_3,false),"?");
				sqlQueryObjectInsert_statisticaOrariaContenuti.addInsertField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_VALORE_3,false),"?");
				sqlQueryObjectInsert_statisticaOrariaContenuti.addInsertField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_NOME_4,false),"?");
				sqlQueryObjectInsert_statisticaOrariaContenuti.addInsertField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_VALORE_4,false),"?");
				sqlQueryObjectInsert_statisticaOrariaContenuti.addInsertField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_NOME_5,false),"?");
				sqlQueryObjectInsert_statisticaOrariaContenuti.addInsertField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_VALORE_5,false),"?");
				sqlQueryObjectInsert_statisticaOrariaContenuti.addInsertField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_NOME_6,false),"?");
				sqlQueryObjectInsert_statisticaOrariaContenuti.addInsertField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_VALORE_6,false),"?");
				sqlQueryObjectInsert_statisticaOrariaContenuti.addInsertField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_NOME_7,false),"?");
				sqlQueryObjectInsert_statisticaOrariaContenuti.addInsertField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_VALORE_7,false),"?");
				sqlQueryObjectInsert_statisticaOrariaContenuti.addInsertField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_NOME_8,false),"?");
				sqlQueryObjectInsert_statisticaOrariaContenuti.addInsertField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_VALORE_8,false),"?");
				sqlQueryObjectInsert_statisticaOrariaContenuti.addInsertField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_NOME_9,false),"?");
				sqlQueryObjectInsert_statisticaOrariaContenuti.addInsertField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_VALORE_9,false),"?");
				sqlQueryObjectInsert_statisticaOrariaContenuti.addInsertField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_NOME_10,false),"?");
				sqlQueryObjectInsert_statisticaOrariaContenuti.addInsertField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_VALORE_10,false),"?");
				sqlQueryObjectInsert_statisticaOrariaContenuti.addInsertField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.NUMERO_TRANSAZIONI,false),"?");
				sqlQueryObjectInsert_statisticaOrariaContenuti.addInsertField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.DIMENSIONI_BYTES_BANDA_COMPLESSIVA,false),"?");
				sqlQueryObjectInsert_statisticaOrariaContenuti.addInsertField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.DIMENSIONI_BYTES_BANDA_INTERNA,false),"?");
				sqlQueryObjectInsert_statisticaOrariaContenuti.addInsertField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.DIMENSIONI_BYTES_BANDA_ESTERNA,false),"?");
				sqlQueryObjectInsert_statisticaOrariaContenuti.addInsertField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.LATENZA_TOTALE,false),"?");
				sqlQueryObjectInsert_statisticaOrariaContenuti.addInsertField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.LATENZA_PORTA,false),"?");
				sqlQueryObjectInsert_statisticaOrariaContenuti.addInsertField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.LATENZA_SERVIZIO,false),"?");
				sqlQueryObjectInsert_statisticaOrariaContenuti.addInsertField("id_stat","?");

				// Insert statisticaOraria_statisticaOrariaContenuti
				org.openspcoop2.utils.jdbc.IKeyGeneratorObject keyGenerator_statisticaOrariaContenuti = this.getStatisticaOrariaFetch().getKeyGeneratorObject(StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI);
				long id_statisticaOrariaContenuti = jdbcUtilities.insertAndReturnGeneratedKey(sqlQueryObjectInsert_statisticaOrariaContenuti, keyGenerator_statisticaOrariaContenuti, jdbcProperties.isShowSql(),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaOraria_statisticaOrariaContenuti.getData(),StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.DATA.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaOraria_statisticaOrariaContenuti.getRisorsaNome(),StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.RISORSA_NOME.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaOraria_statisticaOrariaContenuti.getRisorsaValore(),StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.RISORSA_VALORE.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaOraria_statisticaOrariaContenuti.getFiltroNome1(),StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_NOME_1.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaOraria_statisticaOrariaContenuti.getFiltroValore1(),StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_VALORE_1.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaOraria_statisticaOrariaContenuti.getFiltroNome2(),StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_NOME_2.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaOraria_statisticaOrariaContenuti.getFiltroValore2(),StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_VALORE_2.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaOraria_statisticaOrariaContenuti.getFiltroNome3(),StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_NOME_3.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaOraria_statisticaOrariaContenuti.getFiltroValore3(),StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_VALORE_3.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaOraria_statisticaOrariaContenuti.getFiltroNome4(),StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_NOME_4.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaOraria_statisticaOrariaContenuti.getFiltroValore4(),StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_VALORE_4.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaOraria_statisticaOrariaContenuti.getFiltroNome5(),StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_NOME_5.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaOraria_statisticaOrariaContenuti.getFiltroValore5(),StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_VALORE_5.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaOraria_statisticaOrariaContenuti.getFiltroNome6(),StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_NOME_6.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaOraria_statisticaOrariaContenuti.getFiltroValore6(),StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_VALORE_6.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaOraria_statisticaOrariaContenuti.getFiltroNome7(),StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_NOME_7.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaOraria_statisticaOrariaContenuti.getFiltroValore7(),StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_VALORE_7.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaOraria_statisticaOrariaContenuti.getFiltroNome8(),StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_NOME_8.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaOraria_statisticaOrariaContenuti.getFiltroValore8(),StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_VALORE_8.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaOraria_statisticaOrariaContenuti.getFiltroNome9(),StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_NOME_9.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaOraria_statisticaOrariaContenuti.getFiltroValore9(),StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_VALORE_9.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaOraria_statisticaOrariaContenuti.getFiltroNome10(),StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_NOME_10.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaOraria_statisticaOrariaContenuti.getFiltroValore10(),StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_VALORE_10.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaOraria_statisticaOrariaContenuti.getNumeroTransazioni(),StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.NUMERO_TRANSAZIONI.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaOraria_statisticaOrariaContenuti.getDimensioniBytesBandaComplessiva(),StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.DIMENSIONI_BYTES_BANDA_COMPLESSIVA.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaOraria_statisticaOrariaContenuti.getDimensioniBytesBandaInterna(),StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.DIMENSIONI_BYTES_BANDA_INTERNA.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaOraria_statisticaOrariaContenuti.getDimensioniBytesBandaEsterna(),StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.DIMENSIONI_BYTES_BANDA_ESTERNA.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaOraria_statisticaOrariaContenuti.getLatenzaTotale(),StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.LATENZA_TOTALE.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaOraria_statisticaOrariaContenuti.getLatenzaPorta(),StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.LATENZA_PORTA.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(statisticaOraria_statisticaOrariaContenuti.getLatenzaServizio(),StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.LATENZA_SERVIZIO.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(Long.valueOf(id),Long.class)
				);
				statisticaOraria_statisticaOrariaContenuti.setId(id_statisticaOrariaContenuti);

				ids_statisticaOrariaContenuti_da_non_eliminare.add(statisticaOraria_statisticaOrariaContenuti.getId());
			} else {


				// Object statisticaOraria_statisticaOrariaContenuti
				ISQLQueryObject sqlQueryObjectUpdate_statisticaOrariaContenuti = sqlQueryObjectUpdate.newSQLQueryObject();
				sqlQueryObjectUpdate_statisticaOrariaContenuti.setANDLogicOperator(true);
				sqlQueryObjectUpdate_statisticaOrariaContenuti.addUpdateTable(this.getStatisticaOrariaFieldConverter().toTable(StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI));
				boolean isUpdate_statisticaOrariaContenuti = true;
				java.util.List<JDBCObject> lstObjects_statisticaOrariaContenuti = new java.util.ArrayList<JDBCObject>();
				sqlQueryObjectUpdate_statisticaOrariaContenuti.addUpdateField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.DATA,false), "?");
				lstObjects_statisticaOrariaContenuti.add(new JDBCObject(statisticaOraria_statisticaOrariaContenuti.getData(), StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.DATA.getFieldType()));
				sqlQueryObjectUpdate_statisticaOrariaContenuti.addUpdateField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.RISORSA_NOME,false), "?");
				lstObjects_statisticaOrariaContenuti.add(new JDBCObject(statisticaOraria_statisticaOrariaContenuti.getRisorsaNome(), StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.RISORSA_NOME.getFieldType()));
				sqlQueryObjectUpdate_statisticaOrariaContenuti.addUpdateField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.RISORSA_VALORE,false), "?");
				lstObjects_statisticaOrariaContenuti.add(new JDBCObject(statisticaOraria_statisticaOrariaContenuti.getRisorsaValore(), StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.RISORSA_VALORE.getFieldType()));
				sqlQueryObjectUpdate_statisticaOrariaContenuti.addUpdateField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_NOME_1,false), "?");
				lstObjects_statisticaOrariaContenuti.add(new JDBCObject(statisticaOraria_statisticaOrariaContenuti.getFiltroNome1(), StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_NOME_1.getFieldType()));
				sqlQueryObjectUpdate_statisticaOrariaContenuti.addUpdateField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_VALORE_1,false), "?");
				lstObjects_statisticaOrariaContenuti.add(new JDBCObject(statisticaOraria_statisticaOrariaContenuti.getFiltroValore1(), StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_VALORE_1.getFieldType()));
				sqlQueryObjectUpdate_statisticaOrariaContenuti.addUpdateField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_NOME_2,false), "?");
				lstObjects_statisticaOrariaContenuti.add(new JDBCObject(statisticaOraria_statisticaOrariaContenuti.getFiltroNome2(), StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_NOME_2.getFieldType()));
				sqlQueryObjectUpdate_statisticaOrariaContenuti.addUpdateField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_VALORE_2,false), "?");
				lstObjects_statisticaOrariaContenuti.add(new JDBCObject(statisticaOraria_statisticaOrariaContenuti.getFiltroValore2(), StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_VALORE_2.getFieldType()));
				sqlQueryObjectUpdate_statisticaOrariaContenuti.addUpdateField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_NOME_3,false), "?");
				lstObjects_statisticaOrariaContenuti.add(new JDBCObject(statisticaOraria_statisticaOrariaContenuti.getFiltroNome3(), StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_NOME_3.getFieldType()));
				sqlQueryObjectUpdate_statisticaOrariaContenuti.addUpdateField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_VALORE_3,false), "?");
				lstObjects_statisticaOrariaContenuti.add(new JDBCObject(statisticaOraria_statisticaOrariaContenuti.getFiltroValore3(), StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_VALORE_3.getFieldType()));
				sqlQueryObjectUpdate_statisticaOrariaContenuti.addUpdateField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_NOME_4,false), "?");
				lstObjects_statisticaOrariaContenuti.add(new JDBCObject(statisticaOraria_statisticaOrariaContenuti.getFiltroNome4(), StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_NOME_4.getFieldType()));
				sqlQueryObjectUpdate_statisticaOrariaContenuti.addUpdateField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_VALORE_4,false), "?");
				lstObjects_statisticaOrariaContenuti.add(new JDBCObject(statisticaOraria_statisticaOrariaContenuti.getFiltroValore4(), StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_VALORE_4.getFieldType()));
				sqlQueryObjectUpdate_statisticaOrariaContenuti.addUpdateField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_NOME_5,false), "?");
				lstObjects_statisticaOrariaContenuti.add(new JDBCObject(statisticaOraria_statisticaOrariaContenuti.getFiltroNome5(), StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_NOME_5.getFieldType()));
				sqlQueryObjectUpdate_statisticaOrariaContenuti.addUpdateField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_VALORE_5,false), "?");
				lstObjects_statisticaOrariaContenuti.add(new JDBCObject(statisticaOraria_statisticaOrariaContenuti.getFiltroValore5(), StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_VALORE_5.getFieldType()));
				sqlQueryObjectUpdate_statisticaOrariaContenuti.addUpdateField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_NOME_6,false), "?");
				lstObjects_statisticaOrariaContenuti.add(new JDBCObject(statisticaOraria_statisticaOrariaContenuti.getFiltroNome6(), StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_NOME_6.getFieldType()));
				sqlQueryObjectUpdate_statisticaOrariaContenuti.addUpdateField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_VALORE_6,false), "?");
				lstObjects_statisticaOrariaContenuti.add(new JDBCObject(statisticaOraria_statisticaOrariaContenuti.getFiltroValore6(), StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_VALORE_6.getFieldType()));
				sqlQueryObjectUpdate_statisticaOrariaContenuti.addUpdateField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_NOME_7,false), "?");
				lstObjects_statisticaOrariaContenuti.add(new JDBCObject(statisticaOraria_statisticaOrariaContenuti.getFiltroNome7(), StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_NOME_7.getFieldType()));
				sqlQueryObjectUpdate_statisticaOrariaContenuti.addUpdateField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_VALORE_7,false), "?");
				lstObjects_statisticaOrariaContenuti.add(new JDBCObject(statisticaOraria_statisticaOrariaContenuti.getFiltroValore7(), StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_VALORE_7.getFieldType()));
				sqlQueryObjectUpdate_statisticaOrariaContenuti.addUpdateField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_NOME_8,false), "?");
				lstObjects_statisticaOrariaContenuti.add(new JDBCObject(statisticaOraria_statisticaOrariaContenuti.getFiltroNome8(), StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_NOME_8.getFieldType()));
				sqlQueryObjectUpdate_statisticaOrariaContenuti.addUpdateField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_VALORE_8,false), "?");
				lstObjects_statisticaOrariaContenuti.add(new JDBCObject(statisticaOraria_statisticaOrariaContenuti.getFiltroValore8(), StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_VALORE_8.getFieldType()));
				sqlQueryObjectUpdate_statisticaOrariaContenuti.addUpdateField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_NOME_9,false), "?");
				lstObjects_statisticaOrariaContenuti.add(new JDBCObject(statisticaOraria_statisticaOrariaContenuti.getFiltroNome9(), StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_NOME_9.getFieldType()));
				sqlQueryObjectUpdate_statisticaOrariaContenuti.addUpdateField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_VALORE_9,false), "?");
				lstObjects_statisticaOrariaContenuti.add(new JDBCObject(statisticaOraria_statisticaOrariaContenuti.getFiltroValore9(), StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_VALORE_9.getFieldType()));
				sqlQueryObjectUpdate_statisticaOrariaContenuti.addUpdateField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_NOME_10,false), "?");
				lstObjects_statisticaOrariaContenuti.add(new JDBCObject(statisticaOraria_statisticaOrariaContenuti.getFiltroNome10(), StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_NOME_10.getFieldType()));
				sqlQueryObjectUpdate_statisticaOrariaContenuti.addUpdateField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_VALORE_10,false), "?");
				lstObjects_statisticaOrariaContenuti.add(new JDBCObject(statisticaOraria_statisticaOrariaContenuti.getFiltroValore10(), StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_VALORE_10.getFieldType()));
				sqlQueryObjectUpdate_statisticaOrariaContenuti.addUpdateField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.NUMERO_TRANSAZIONI,false), "?");
				lstObjects_statisticaOrariaContenuti.add(new JDBCObject(statisticaOraria_statisticaOrariaContenuti.getNumeroTransazioni(), StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.NUMERO_TRANSAZIONI.getFieldType()));
				sqlQueryObjectUpdate_statisticaOrariaContenuti.addUpdateField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.DIMENSIONI_BYTES_BANDA_COMPLESSIVA,false), "?");
				lstObjects_statisticaOrariaContenuti.add(new JDBCObject(statisticaOraria_statisticaOrariaContenuti.getDimensioniBytesBandaComplessiva(), StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.DIMENSIONI_BYTES_BANDA_COMPLESSIVA.getFieldType()));
				sqlQueryObjectUpdate_statisticaOrariaContenuti.addUpdateField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.DIMENSIONI_BYTES_BANDA_INTERNA,false), "?");
				lstObjects_statisticaOrariaContenuti.add(new JDBCObject(statisticaOraria_statisticaOrariaContenuti.getDimensioniBytesBandaInterna(), StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.DIMENSIONI_BYTES_BANDA_INTERNA.getFieldType()));
				sqlQueryObjectUpdate_statisticaOrariaContenuti.addUpdateField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.DIMENSIONI_BYTES_BANDA_ESTERNA,false), "?");
				lstObjects_statisticaOrariaContenuti.add(new JDBCObject(statisticaOraria_statisticaOrariaContenuti.getDimensioniBytesBandaEsterna(), StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.DIMENSIONI_BYTES_BANDA_ESTERNA.getFieldType()));
				sqlQueryObjectUpdate_statisticaOrariaContenuti.addUpdateField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.LATENZA_TOTALE,false), "?");
				lstObjects_statisticaOrariaContenuti.add(new JDBCObject(statisticaOraria_statisticaOrariaContenuti.getLatenzaTotale(), StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.LATENZA_TOTALE.getFieldType()));
				sqlQueryObjectUpdate_statisticaOrariaContenuti.addUpdateField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.LATENZA_PORTA,false), "?");
				lstObjects_statisticaOrariaContenuti.add(new JDBCObject(statisticaOraria_statisticaOrariaContenuti.getLatenzaPorta(), StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.LATENZA_PORTA.getFieldType()));
				sqlQueryObjectUpdate_statisticaOrariaContenuti.addUpdateField(this.getStatisticaOrariaFieldConverter().toColumn(StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.LATENZA_SERVIZIO,false), "?");
				lstObjects_statisticaOrariaContenuti.add(new JDBCObject(statisticaOraria_statisticaOrariaContenuti.getLatenzaServizio(), StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.LATENZA_SERVIZIO.getFieldType()));
				sqlQueryObjectUpdate_statisticaOrariaContenuti.addWhereCondition("id=?");
				ids_statisticaOrariaContenuti_da_non_eliminare.add(statisticaOraria_statisticaOrariaContenuti.getId());
				lstObjects_statisticaOrariaContenuti.add(new JDBCObject(Long.valueOf(statisticaOraria_statisticaOrariaContenuti.getId()),Long.class));

				if(isUpdate_statisticaOrariaContenuti) {
					// Update statisticaOraria_statisticaOrariaContenuti
					jdbcUtilities.executeUpdate(sqlQueryObjectUpdate_statisticaOrariaContenuti.createSQLUpdate(), jdbcProperties.isShowSql(), 
						lstObjects_statisticaOrariaContenuti.toArray(new JDBCObject[]{}));
				}
			}
		} // fine for statisticaOraria_statisticaOrariaContenuti

		// elimino tutte le occorrenze di statisticaOraria_statisticaOrariaContenuti non presenti nell'update

		ISQLQueryObject sqlQueryObjectUpdate_statisticaOrariaContenuti_deleteList = sqlQueryObjectUpdate.newSQLQueryObject();
		sqlQueryObjectUpdate_statisticaOrariaContenuti_deleteList.setANDLogicOperator(true);
		sqlQueryObjectUpdate_statisticaOrariaContenuti_deleteList.addDeleteTable(this.getStatisticaOrariaFieldConverter().toTable(StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI));
		java.util.List<JDBCObject> jdbcObjects_statisticaOrariaContenuti_delete = new java.util.ArrayList<JDBCObject>();

		sqlQueryObjectUpdate_statisticaOrariaContenuti_deleteList.addWhereCondition("id_stat=?");
		jdbcObjects_statisticaOrariaContenuti_delete.add(new JDBCObject(statisticaOraria.getId(), Long.class));

		StringBuffer marks_statisticaOrariaContenuti = new StringBuffer();
		if(ids_statisticaOrariaContenuti_da_non_eliminare.size() > 0) {
			for(Long ids : ids_statisticaOrariaContenuti_da_non_eliminare) {
				if(marks_statisticaOrariaContenuti.length() > 0) {
					marks_statisticaOrariaContenuti.append(",");
				}
				marks_statisticaOrariaContenuti.append("?");
				jdbcObjects_statisticaOrariaContenuti_delete.add(new JDBCObject(ids, Long.class));

			}
			sqlQueryObjectUpdate_statisticaOrariaContenuti_deleteList.addWhereCondition("id NOT IN ("+marks_statisticaOrariaContenuti.toString()+")");
		}

		jdbcUtilities.execute(sqlQueryObjectUpdate_statisticaOrariaContenuti_deleteList.createSQLDelete(), jdbcProperties.isShowSql(), jdbcObjects_statisticaOrariaContenuti_delete.toArray(new JDBCObject[]{}));



	}
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, StatisticaOraria statisticaOraria, UpdateField ... updateFields) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		
		JDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getStatisticaOrariaFieldConverter().toTable(StatisticaOraria.model()), 
				this._getMapTableToPKColumn(), 
				this._getRootTablePrimaryKeyValues(jdbcProperties, log, connection, sqlQueryObject, statisticaOraria),
				this.getStatisticaOrariaFieldConverter(), this, null, updateFields);
	}
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, StatisticaOraria statisticaOraria, IExpression condition, UpdateField ... updateFields) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		
		JDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getStatisticaOrariaFieldConverter().toTable(StatisticaOraria.model()), 
				this._getMapTableToPKColumn(), 
				this._getRootTablePrimaryKeyValues(jdbcProperties, log, connection, sqlQueryObject, statisticaOraria),
				this.getStatisticaOrariaFieldConverter(), this, condition, updateFields);
	}
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, StatisticaOraria statisticaOraria, UpdateModel ... updateModels) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		
		JDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getStatisticaOrariaFieldConverter().toTable(StatisticaOraria.model()), 
				this._getMapTableToPKColumn(), 
				this._getRootTablePrimaryKeyValues(jdbcProperties, log, connection, sqlQueryObject, statisticaOraria),
				this.getStatisticaOrariaFieldConverter(), this, updateModels);
	}	
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, UpdateField ... updateFields) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		java.util.List<Object> ids = new java.util.ArrayList<Object>();
		ids.add(tableId);
		JDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getStatisticaOrariaFieldConverter().toTable(StatisticaOraria.model()), 
				this._getMapTableToPKColumn(), 
				ids,
				this.getStatisticaOrariaFieldConverter(), this, null, updateFields);
	}
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, IExpression condition, UpdateField ... updateFields) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		java.util.List<Object> ids = new java.util.ArrayList<Object>();
		ids.add(tableId);
		JDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getStatisticaOrariaFieldConverter().toTable(StatisticaOraria.model()), 
				this._getMapTableToPKColumn(), 
				ids,
				this.getStatisticaOrariaFieldConverter(), this, condition, updateFields);
	}
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, UpdateModel ... updateModels) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		java.util.List<Object> ids = new java.util.ArrayList<Object>();
		ids.add(tableId);
		JDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getStatisticaOrariaFieldConverter().toTable(StatisticaOraria.model()), 
				this._getMapTableToPKColumn(), 
				ids,
				this.getStatisticaOrariaFieldConverter(), this, updateModels);
	}
	
	@Override
	public void updateOrCreate(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, StatisticaOraria statisticaOraria, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotImplementedException,ServiceException,Exception {
	
		Long id = statisticaOraria.getId();
		if(id != null && this.exists(jdbcProperties, log, connection, sqlQueryObject, id)) {
			this.update(jdbcProperties, log, connection, sqlQueryObject, statisticaOraria,idMappingResolutionBehaviour);		
		} else {
			this.create(jdbcProperties, log, connection, sqlQueryObject, statisticaOraria,idMappingResolutionBehaviour);
		}
		
	}
	
	@Override
	public void updateOrCreate(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, StatisticaOraria statisticaOraria, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotImplementedException,ServiceException,Exception {
		if(this.exists(jdbcProperties, log, connection, sqlQueryObject, tableId)) {
			this.update(jdbcProperties, log, connection, sqlQueryObject, tableId, statisticaOraria,idMappingResolutionBehaviour);
		} else {
			this.create(jdbcProperties, log, connection, sqlQueryObject, statisticaOraria,idMappingResolutionBehaviour);
		}
	}
	
	@Override
	public void delete(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, StatisticaOraria statisticaOraria) throws NotImplementedException,ServiceException,Exception {
		
		
		Long longId = null;
		if(statisticaOraria.getId()==null){
			throw new Exception("Parameter "+statisticaOraria.getClass().getName()+".id is null");
		}
		if(statisticaOraria.getId()<=0){
			throw new Exception("Parameter "+statisticaOraria.getClass().getName()+".id is less equals 0");
		}
		longId = statisticaOraria.getId();
		
		this._delete(jdbcProperties, log, connection, sqlQueryObject, longId);
		
	}

	private void _delete(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, Long id) throws NotImplementedException,ServiceException,Exception {
	
		if(id!=null && id.longValue()<=0){
			throw new ServiceException("Id is less equals 0");
		}
		
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);
		
		ISQLQueryObject sqlQueryObjectDelete = sqlQueryObject.newSQLQueryObject();
		

		//Recupero oggetto _statisticaOrariaContenuti
		ISQLQueryObject sqlQueryObjectDelete_statisticaOrariaContenuti_getToDelete = sqlQueryObjectDelete.newSQLQueryObject();
		sqlQueryObjectDelete_statisticaOrariaContenuti_getToDelete.setANDLogicOperator(true);
		sqlQueryObjectDelete_statisticaOrariaContenuti_getToDelete.addFromTable(this.getStatisticaOrariaFieldConverter().toTable(StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI));
		sqlQueryObjectDelete_statisticaOrariaContenuti_getToDelete.addWhereCondition("id_stat=?");
		java.util.List<Object> statisticaOraria_statisticaOrariaContenuti_toDelete_list = (java.util.List<Object>) jdbcUtilities.executeQuery(sqlQueryObjectDelete_statisticaOrariaContenuti_getToDelete.createSQLQuery(), jdbcProperties.isShowSql(), StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI, this.getStatisticaOrariaFetch(),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(Long.valueOf(id),Long.class));

		// for statisticaOraria_statisticaOrariaContenuti
		for (Object statisticaOraria_statisticaOrariaContenuti_object : statisticaOraria_statisticaOrariaContenuti_toDelete_list) {
			StatisticaContenuti statisticaOraria_statisticaOrariaContenuti = (StatisticaContenuti) statisticaOraria_statisticaOrariaContenuti_object;

			// Object statisticaOraria_statisticaOrariaContenuti
			ISQLQueryObject sqlQueryObjectDelete_statisticaOrariaContenuti = sqlQueryObjectDelete.newSQLQueryObject();
			sqlQueryObjectDelete_statisticaOrariaContenuti.setANDLogicOperator(true);
			sqlQueryObjectDelete_statisticaOrariaContenuti.addDeleteTable(this.getStatisticaOrariaFieldConverter().toTable(StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI));
			sqlQueryObjectDelete_statisticaOrariaContenuti.addWhereCondition("id=?");

			// Delete statisticaOraria_statisticaOrariaContenuti
			if(statisticaOraria_statisticaOrariaContenuti != null){
				jdbcUtilities.execute(sqlQueryObjectDelete_statisticaOrariaContenuti.createSQLDelete(), jdbcProperties.isShowSql(), 
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(Long.valueOf(statisticaOraria_statisticaOrariaContenuti.getId()),Long.class));
			}
		} // fine for statisticaOraria_statisticaOrariaContenuti

		// Object statisticaOraria_statisticaBase_toDelete
		sqlQueryObjectDelete.setANDLogicOperator(true);
		sqlQueryObjectDelete.addDeleteTable(this.getStatisticaOrariaFieldConverter().toTable(StatisticaOraria.model().STATISTICA_BASE));
		if(id != null)
			sqlQueryObjectDelete.addWhereCondition("id=?");

		// Delete statisticaOraria
		jdbcUtilities.execute(sqlQueryObjectDelete.createSQLDelete(), jdbcProperties.isShowSql(), 
			new JDBCObject(id,Long.class));

	}

	
	@Override
	public NonNegativeNumber deleteAll(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject) throws NotImplementedException,ServiceException,Exception {
		
		return this.deleteAll(jdbcProperties, log, connection, sqlQueryObject, new JDBCExpression(this.getStatisticaOrariaFieldConverter()));

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
