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
import org.openspcoop2.core.transazioni.IdDumpMessaggio;
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

import org.openspcoop2.core.transazioni.DumpHeaderTrasporto;
import org.openspcoop2.core.transazioni.DumpHeaderAllegato;
import org.openspcoop2.core.transazioni.DumpContenuto;
import org.openspcoop2.core.transazioni.DumpAllegato;
import org.openspcoop2.core.transazioni.DumpMessaggio;
import org.openspcoop2.core.transazioni.DumpMultipartHeader;

/**     
 * JDBCDumpMessaggioServiceImpl
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JDBCDumpMessaggioServiceImpl extends JDBCDumpMessaggioServiceSearchImpl
	implements IJDBCServiceCRUDWithId<DumpMessaggio, IdDumpMessaggio, JDBCServiceManager> {

	@Override
	public void create(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, DumpMessaggio dumpMessaggio, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotImplementedException,ServiceException,Exception {

		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);
		
		ISQLQueryObject sqlQueryObjectInsert = sqlQueryObject.newSQLQueryObject();
				


		// Object dumpMessaggio
		sqlQueryObjectInsert.addInsertTable(this.getDumpMessaggioFieldConverter().toTable(DumpMessaggio.model()));
		sqlQueryObjectInsert.addInsertField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().ID_TRANSAZIONE,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().PROTOCOLLO,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().SERVIZIO_APPLICATIVO_EROGATORE,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().DATA_CONSEGNA_EROGATORE,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().TIPO_MESSAGGIO,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().FORMATO_MESSAGGIO,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().CONTENT_TYPE,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().CONTENT_LENGTH,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().MULTIPART_CONTENT_TYPE,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().MULTIPART_CONTENT_ID,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().MULTIPART_CONTENT_LOCATION,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().BODY,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().DUMP_TIMESTAMP,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().POST_PROCESS_HEADER,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().POST_PROCESS_FILENAME,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().POST_PROCESS_CONTENT,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().POST_PROCESS_CONFIG_ID,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().POST_PROCESS_TIMESTAMP,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().POST_PROCESSED,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().MULTIPART_HEADER_EXT,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().HEADER_EXT,false),"?");

		// Insert dumpMessaggio
		org.openspcoop2.utils.jdbc.IKeyGeneratorObject keyGenerator = this.getDumpMessaggioFetch().getKeyGeneratorObject(DumpMessaggio.model());
		long id = jdbcUtilities.insertAndReturnGeneratedKey(sqlQueryObjectInsert, keyGenerator, jdbcProperties.isShowSql(),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(dumpMessaggio.getIdTransazione(),DumpMessaggio.model().ID_TRANSAZIONE.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(dumpMessaggio.getProtocollo(),DumpMessaggio.model().PROTOCOLLO.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(dumpMessaggio.getServizioApplicativoErogatore(),DumpMessaggio.model().SERVIZIO_APPLICATIVO_EROGATORE.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(dumpMessaggio.getDataConsegnaErogatore(),DumpMessaggio.model().DATA_CONSEGNA_EROGATORE.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(dumpMessaggio.getTipoMessaggio(),DumpMessaggio.model().TIPO_MESSAGGIO.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(dumpMessaggio.getFormatoMessaggio(),DumpMessaggio.model().FORMATO_MESSAGGIO.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(dumpMessaggio.getContentType(),DumpMessaggio.model().CONTENT_TYPE.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(dumpMessaggio.getContentLength(),DumpMessaggio.model().CONTENT_LENGTH.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(dumpMessaggio.getMultipartContentType(),DumpMessaggio.model().MULTIPART_CONTENT_TYPE.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(dumpMessaggio.getMultipartContentId(),DumpMessaggio.model().MULTIPART_CONTENT_ID.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(dumpMessaggio.getMultipartContentLocation(),DumpMessaggio.model().MULTIPART_CONTENT_LOCATION.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(dumpMessaggio.getBody(),DumpMessaggio.model().BODY.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(dumpMessaggio.getDumpTimestamp(),DumpMessaggio.model().DUMP_TIMESTAMP.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(dumpMessaggio.getPostProcessHeader(),DumpMessaggio.model().POST_PROCESS_HEADER.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(dumpMessaggio.getPostProcessFilename(),DumpMessaggio.model().POST_PROCESS_FILENAME.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(dumpMessaggio.getPostProcessContent(),DumpMessaggio.model().POST_PROCESS_CONTENT.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(dumpMessaggio.getPostProcessConfigId(),DumpMessaggio.model().POST_PROCESS_CONFIG_ID.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(dumpMessaggio.getPostProcessTimestamp(),DumpMessaggio.model().POST_PROCESS_TIMESTAMP.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(dumpMessaggio.getPostProcessed(),DumpMessaggio.model().POST_PROCESSED.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(dumpMessaggio.getMultipartHeaderExt(),DumpMessaggio.model().MULTIPART_HEADER_EXT.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(dumpMessaggio.getHeaderExt(),DumpMessaggio.model().HEADER_EXT.getFieldType())
		);
		dumpMessaggio.setId(id);

		// for dumpMessaggio
		for (int i = 0; i < dumpMessaggio.getAllegatoList().size(); i++) {


			// Object dumpMessaggio.getAllegatoList().get(i)
			ISQLQueryObject sqlQueryObjectInsert_allegato = sqlQueryObjectInsert.newSQLQueryObject();
			sqlQueryObjectInsert_allegato.addInsertTable(this.getDumpMessaggioFieldConverter().toTable(DumpMessaggio.model().ALLEGATO));
			sqlQueryObjectInsert_allegato.addInsertField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().ALLEGATO.CONTENT_TYPE,false),"?");
			sqlQueryObjectInsert_allegato.addInsertField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().ALLEGATO.CONTENT_ID,false),"?");
			sqlQueryObjectInsert_allegato.addInsertField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().ALLEGATO.CONTENT_LOCATION,false),"?");
			sqlQueryObjectInsert_allegato.addInsertField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().ALLEGATO.ALLEGATO,false),"?");
			sqlQueryObjectInsert_allegato.addInsertField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().ALLEGATO.DUMP_TIMESTAMP,false),"?");
			sqlQueryObjectInsert_allegato.addInsertField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().ALLEGATO.HEADER_EXT,false),"?");
			sqlQueryObjectInsert_allegato.addInsertField("id_messaggio","?");

			// Insert dumpMessaggio.getAllegatoList().get(i)
			org.openspcoop2.utils.jdbc.IKeyGeneratorObject keyGenerator_allegato = this.getDumpMessaggioFetch().getKeyGeneratorObject(DumpMessaggio.model().ALLEGATO);
			long id_allegato = jdbcUtilities.insertAndReturnGeneratedKey(sqlQueryObjectInsert_allegato, keyGenerator_allegato, jdbcProperties.isShowSql(),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(dumpMessaggio.getAllegatoList().get(i).getContentType(),DumpMessaggio.model().ALLEGATO.CONTENT_TYPE.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(dumpMessaggio.getAllegatoList().get(i).getContentId(),DumpMessaggio.model().ALLEGATO.CONTENT_ID.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(dumpMessaggio.getAllegatoList().get(i).getContentLocation(),DumpMessaggio.model().ALLEGATO.CONTENT_LOCATION.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(dumpMessaggio.getAllegatoList().get(i).getAllegato(),DumpMessaggio.model().ALLEGATO.ALLEGATO.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(dumpMessaggio.getAllegatoList().get(i).getDumpTimestamp(),DumpMessaggio.model().ALLEGATO.DUMP_TIMESTAMP.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(dumpMessaggio.getAllegatoList().get(i).getHeaderExt(),DumpMessaggio.model().ALLEGATO.HEADER_EXT.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(Long.valueOf(id),Long.class)
			);
			dumpMessaggio.getAllegatoList().get(i).setId(id_allegato);

			// for dumpMessaggio.getAllegatoList().get(i)
			for (int i_allegato = 0; i_allegato < dumpMessaggio.getAllegatoList().get(i).getHeaderList().size(); i_allegato++) {

				// Bug fix OPPT-466 per gestione empty string as null on oracle
				// Rilasciare il vincolo di not null 'fisico' sul database.
				// Imporlo logicamente, in modo che non siano permessi insert o update con valori null.
				// Dopodichè, se nella get viene recuperato un valore null, deve essere trasformato in stringa vuota.
				DumpHeaderAllegato dht = dumpMessaggio.getAllegatoList().get(i).getHeaderList().get(i_allegato);
				if(dht.getValore()==null){
					throw new ServiceException("Header ["+dht.getNome()+"] dell'allegato-"+i+" ["+dumpMessaggio.getAllegatoList().get(i).getContentId()+"] with value null");
				}

				// Object dumpMessaggio.getAllegatoList().get(i).getHeaderList().get(i_allegato)
				ISQLQueryObject sqlQueryObjectInsert_allegato_header = sqlQueryObjectInsert.newSQLQueryObject();
				sqlQueryObjectInsert_allegato_header.addInsertTable(this.getDumpMessaggioFieldConverter().toTable(DumpMessaggio.model().ALLEGATO.HEADER));
				sqlQueryObjectInsert_allegato_header.addInsertField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().ALLEGATO.HEADER.NOME,false),"?");
				sqlQueryObjectInsert_allegato_header.addInsertField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().ALLEGATO.HEADER.VALORE,false),"?");
				sqlQueryObjectInsert_allegato_header.addInsertField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().ALLEGATO.HEADER.DUMP_TIMESTAMP,false),"?");
				sqlQueryObjectInsert_allegato_header.addInsertField("id_allegato","?");

				// Insert dumpMessaggio.getAllegatoList().get(i).getHeaderList().get(i_allegato)
				org.openspcoop2.utils.jdbc.IKeyGeneratorObject keyGenerator_allegato_header = this.getDumpMessaggioFetch().getKeyGeneratorObject(DumpMessaggio.model().ALLEGATO.HEADER);
				long id_allegato_header = jdbcUtilities.insertAndReturnGeneratedKey(sqlQueryObjectInsert_allegato_header, keyGenerator_allegato_header, jdbcProperties.isShowSql(),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(dumpMessaggio.getAllegatoList().get(i).getHeaderList().get(i_allegato).getNome(),DumpMessaggio.model().ALLEGATO.HEADER.NOME.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(dumpMessaggio.getAllegatoList().get(i).getHeaderList().get(i_allegato).getValore(),DumpMessaggio.model().ALLEGATO.HEADER.VALORE.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(dumpMessaggio.getAllegatoList().get(i).getHeaderList().get(i_allegato).getDumpTimestamp(),DumpMessaggio.model().ALLEGATO.HEADER.DUMP_TIMESTAMP.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(Long.valueOf(id_allegato),Long.class)
				);
				dumpMessaggio.getAllegatoList().get(i).getHeaderList().get(i_allegato).setId(id_allegato_header);
			} // fine for _allegato
		} // fine for 

		// for dumpMessaggio
		for (int i = 0; i < dumpMessaggio.getContenutoList().size(); i++) {


			// Object dumpMessaggio.getContenutoList().get(i)
			ISQLQueryObject sqlQueryObjectInsert_contenuto = sqlQueryObjectInsert.newSQLQueryObject();
			sqlQueryObjectInsert_contenuto.addInsertTable(this.getDumpMessaggioFieldConverter().toTable(DumpMessaggio.model().CONTENUTO));
			sqlQueryObjectInsert_contenuto.addInsertField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().CONTENUTO.NOME,false),"?");
			sqlQueryObjectInsert_contenuto.addInsertField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().CONTENUTO.VALORE,false),"?");
			sqlQueryObjectInsert_contenuto.addInsertField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().CONTENUTO.VALORE_AS_BYTES,false),"?");
			sqlQueryObjectInsert_contenuto.addInsertField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().CONTENUTO.DUMP_TIMESTAMP,false),"?");
			sqlQueryObjectInsert_contenuto.addInsertField("id_messaggio","?");

			// Insert dumpMessaggio.getContenutoList().get(i)
			org.openspcoop2.utils.jdbc.IKeyGeneratorObject keyGenerator_contenuto = this.getDumpMessaggioFetch().getKeyGeneratorObject(DumpMessaggio.model().CONTENUTO);
			long id_contenuto = jdbcUtilities.insertAndReturnGeneratedKey(sqlQueryObjectInsert_contenuto, keyGenerator_contenuto, jdbcProperties.isShowSql(),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(dumpMessaggio.getContenutoList().get(i).getNome(),DumpMessaggio.model().CONTENUTO.NOME.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(dumpMessaggio.getContenutoList().get(i).getValore(),DumpMessaggio.model().CONTENUTO.VALORE.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(dumpMessaggio.getContenutoList().get(i).getValoreAsBytes(),DumpMessaggio.model().CONTENUTO.VALORE_AS_BYTES.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(dumpMessaggio.getContenutoList().get(i).getDumpTimestamp(),DumpMessaggio.model().CONTENUTO.DUMP_TIMESTAMP.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(Long.valueOf(id),Long.class)
			);
			dumpMessaggio.getContenutoList().get(i).setId(id_contenuto);
		} // fine for 

		// for dumpMessaggio
		for (int i = 0; i < dumpMessaggio.getHeaderTrasportoList().size(); i++) {

			// Bug fix OPPT-466 per gestione empty string as null on oracle
			// Rilasciare il vincolo di not null 'fisico' sul database.
			// Imporlo logicamente, in modo che non siano permessi insert o update con valori null.
			// Dopodichè, se nella get viene recuperato un valore null, deve essere trasformato in stringa vuota.
			DumpHeaderTrasporto dht = dumpMessaggio.getHeaderTrasporto(i);
			if(dht.getValore()==null){
				throw new ServiceException("Header ["+dht.getNome()+"] with value null");
			}

			// Object dumpMessaggio.getHeaderTrasportoList().get(i)
			ISQLQueryObject sqlQueryObjectInsert_headerTrasporto = sqlQueryObjectInsert.newSQLQueryObject();
			sqlQueryObjectInsert_headerTrasporto.addInsertTable(this.getDumpMessaggioFieldConverter().toTable(DumpMessaggio.model().HEADER_TRASPORTO));
			sqlQueryObjectInsert_headerTrasporto.addInsertField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().HEADER_TRASPORTO.NOME,false),"?");
			sqlQueryObjectInsert_headerTrasporto.addInsertField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().HEADER_TRASPORTO.VALORE,false),"?");
			sqlQueryObjectInsert_headerTrasporto.addInsertField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().HEADER_TRASPORTO.DUMP_TIMESTAMP,false),"?");
			sqlQueryObjectInsert_headerTrasporto.addInsertField("id_messaggio","?");

			// Insert dumpMessaggio.getHeaderTrasportoList().get(i)
			org.openspcoop2.utils.jdbc.IKeyGeneratorObject keyGenerator_headerTrasporto = this.getDumpMessaggioFetch().getKeyGeneratorObject(DumpMessaggio.model().HEADER_TRASPORTO);
			long id_headerTrasporto = jdbcUtilities.insertAndReturnGeneratedKey(sqlQueryObjectInsert_headerTrasporto, keyGenerator_headerTrasporto, jdbcProperties.isShowSql(),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(dumpMessaggio.getHeaderTrasportoList().get(i).getNome(),DumpMessaggio.model().HEADER_TRASPORTO.NOME.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(dumpMessaggio.getHeaderTrasportoList().get(i).getValore(),DumpMessaggio.model().HEADER_TRASPORTO.VALORE.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(dumpMessaggio.getHeaderTrasportoList().get(i).getDumpTimestamp(),DumpMessaggio.model().HEADER_TRASPORTO.DUMP_TIMESTAMP.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(Long.valueOf(id),Long.class)
			);
			dumpMessaggio.getHeaderTrasportoList().get(i).setId(id_headerTrasporto);
		} // fine for 

		// for dumpMessaggio
		for (int i = 0; i < dumpMessaggio.getMultipartHeaderList().size(); i++) {

			// Bug fix OPPT-466 per gestione empty string as null on oracle
			// Rilasciare il vincolo di not null 'fisico' sul database.
			// Imporlo logicamente, in modo che non siano permessi insert o update con valori null.
			// Dopodichè, se nella get viene recuperato un valore null, deve essere trasformato in stringa vuota.
			DumpMultipartHeader dht = dumpMessaggio.getMultipartHeader(i);
			if(dht.getValore()==null){
				throw new ServiceException("Header ["+dht.getNome()+"] with value null");
			}

			// Object dumpMessaggio.getMultipartHeaderList().get(i)
			ISQLQueryObject sqlQueryObjectInsert_multipartHeader = sqlQueryObjectInsert.newSQLQueryObject();
			sqlQueryObjectInsert_multipartHeader.addInsertTable(this.getDumpMessaggioFieldConverter().toTable(DumpMessaggio.model().MULTIPART_HEADER));
			sqlQueryObjectInsert_multipartHeader.addInsertField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().MULTIPART_HEADER.NOME,false),"?");
			sqlQueryObjectInsert_multipartHeader.addInsertField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().MULTIPART_HEADER.VALORE,false),"?");
			sqlQueryObjectInsert_multipartHeader.addInsertField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().MULTIPART_HEADER.DUMP_TIMESTAMP,false),"?");
			sqlQueryObjectInsert_multipartHeader.addInsertField("id_messaggio","?");

			// Insert dumpMessaggio.getMultipartHeaderList().get(i)
			org.openspcoop2.utils.jdbc.IKeyGeneratorObject keyGenerator_multipartHeader = this.getDumpMessaggioFetch().getKeyGeneratorObject(DumpMessaggio.model().MULTIPART_HEADER);
			long id_multipartHeader = jdbcUtilities.insertAndReturnGeneratedKey(sqlQueryObjectInsert_multipartHeader, keyGenerator_multipartHeader, jdbcProperties.isShowSql(),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(dumpMessaggio.getMultipartHeaderList().get(i).getNome(),DumpMessaggio.model().MULTIPART_HEADER.NOME.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(dumpMessaggio.getMultipartHeaderList().get(i).getValore(),DumpMessaggio.model().MULTIPART_HEADER.VALORE.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(dumpMessaggio.getMultipartHeaderList().get(i).getDumpTimestamp(),DumpMessaggio.model().MULTIPART_HEADER.DUMP_TIMESTAMP.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(Long.valueOf(id),Long.class)
			);
			dumpMessaggio.getMultipartHeaderList().get(i).setId(id_multipartHeader);
		} // fine for 

		
	}

	@Override
	public void update(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdDumpMessaggio oldId, DumpMessaggio dumpMessaggio, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		ISQLQueryObject sqlQueryObjectUpdate = sqlQueryObject.newSQLQueryObject();
		Long longIdByLogicId = this.findIdDumpMessaggio(jdbcProperties, log, connection, sqlQueryObjectUpdate.newSQLQueryObject(), oldId, true);
		Long tableId = dumpMessaggio.getId();
		if(tableId != null && tableId.longValue() > 0) {
			if(tableId.longValue() != longIdByLogicId.longValue()) {
				throw new Exception("Ambiguous parameter: dumpMessaggio.id ["+tableId+"] does not match logic id ["+longIdByLogicId+"]");
			}
		} else {
			tableId = longIdByLogicId;
			dumpMessaggio.setId(tableId);
		}
		if(tableId==null || tableId<=0){
			throw new Exception("Retrieve tableId failed");
		}

		this.update(jdbcProperties, log, connection, sqlQueryObject, tableId, dumpMessaggio, idMappingResolutionBehaviour);
	}
	@Override
	public void update(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, DumpMessaggio dumpMessaggio, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException, NotImplementedException, ServiceException, Exception {
	
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);
		
		ISQLQueryObject sqlQueryObjectInsert = sqlQueryObject.newSQLQueryObject();
		ISQLQueryObject sqlQueryObjectDelete = sqlQueryObjectInsert.newSQLQueryObject();
		ISQLQueryObject sqlQueryObjectGet = sqlQueryObjectDelete.newSQLQueryObject();
		ISQLQueryObject sqlQueryObjectUpdate = sqlQueryObjectGet.newSQLQueryObject();
		


		// Object dumpMessaggio
		sqlQueryObjectUpdate.setANDLogicOperator(true);
		sqlQueryObjectUpdate.addUpdateTable(this.getDumpMessaggioFieldConverter().toTable(DumpMessaggio.model()));
		boolean isUpdate_dumpMessaggio = true;
		java.util.List<JDBCObject> lstObjects_dumpMessaggio = new java.util.ArrayList<JDBCObject>();
		sqlQueryObjectUpdate.addUpdateField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().ID_TRANSAZIONE,false), "?");
		lstObjects_dumpMessaggio.add(new JDBCObject(dumpMessaggio.getIdTransazione(), DumpMessaggio.model().ID_TRANSAZIONE.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().PROTOCOLLO,false), "?");
		lstObjects_dumpMessaggio.add(new JDBCObject(dumpMessaggio.getProtocollo(), DumpMessaggio.model().PROTOCOLLO.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().SERVIZIO_APPLICATIVO_EROGATORE,false), "?");
		lstObjects_dumpMessaggio.add(new JDBCObject(dumpMessaggio.getServizioApplicativoErogatore(), DumpMessaggio.model().SERVIZIO_APPLICATIVO_EROGATORE.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().DATA_CONSEGNA_EROGATORE,false), "?");
		lstObjects_dumpMessaggio.add(new JDBCObject(dumpMessaggio.getDataConsegnaErogatore(), DumpMessaggio.model().DATA_CONSEGNA_EROGATORE.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().TIPO_MESSAGGIO,false), "?");
		lstObjects_dumpMessaggio.add(new JDBCObject(dumpMessaggio.getTipoMessaggio(), DumpMessaggio.model().TIPO_MESSAGGIO.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().FORMATO_MESSAGGIO,false), "?");
		lstObjects_dumpMessaggio.add(new JDBCObject(dumpMessaggio.getFormatoMessaggio(), DumpMessaggio.model().FORMATO_MESSAGGIO.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().CONTENT_TYPE,false), "?");
		lstObjects_dumpMessaggio.add(new JDBCObject(dumpMessaggio.getContentType(), DumpMessaggio.model().CONTENT_TYPE.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().CONTENT_LENGTH,false), "?");
		lstObjects_dumpMessaggio.add(new JDBCObject(dumpMessaggio.getContentLength(), DumpMessaggio.model().CONTENT_LENGTH.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().MULTIPART_CONTENT_TYPE,false), "?");
		lstObjects_dumpMessaggio.add(new JDBCObject(dumpMessaggio.getMultipartContentType(), DumpMessaggio.model().MULTIPART_CONTENT_TYPE.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().MULTIPART_CONTENT_ID,false), "?");
		lstObjects_dumpMessaggio.add(new JDBCObject(dumpMessaggio.getMultipartContentId(), DumpMessaggio.model().MULTIPART_CONTENT_ID.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().MULTIPART_CONTENT_LOCATION,false), "?");
		lstObjects_dumpMessaggio.add(new JDBCObject(dumpMessaggio.getMultipartContentLocation(), DumpMessaggio.model().MULTIPART_CONTENT_LOCATION.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().BODY,false), "?");
		lstObjects_dumpMessaggio.add(new JDBCObject(dumpMessaggio.getBody(), DumpMessaggio.model().BODY.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().DUMP_TIMESTAMP,false), "?");
		lstObjects_dumpMessaggio.add(new JDBCObject(dumpMessaggio.getDumpTimestamp(), DumpMessaggio.model().DUMP_TIMESTAMP.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().POST_PROCESS_HEADER,false), "?");
		lstObjects_dumpMessaggio.add(new JDBCObject(dumpMessaggio.getPostProcessHeader(), DumpMessaggio.model().POST_PROCESS_HEADER.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().POST_PROCESS_FILENAME,false), "?");
		lstObjects_dumpMessaggio.add(new JDBCObject(dumpMessaggio.getPostProcessFilename(), DumpMessaggio.model().POST_PROCESS_FILENAME.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().POST_PROCESS_CONTENT,false), "?");
		lstObjects_dumpMessaggio.add(new JDBCObject(dumpMessaggio.getPostProcessContent(), DumpMessaggio.model().POST_PROCESS_CONTENT.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().POST_PROCESS_CONFIG_ID,false), "?");
		lstObjects_dumpMessaggio.add(new JDBCObject(dumpMessaggio.getPostProcessConfigId(), DumpMessaggio.model().POST_PROCESS_CONFIG_ID.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().POST_PROCESS_TIMESTAMP,false), "?");
		lstObjects_dumpMessaggio.add(new JDBCObject(dumpMessaggio.getPostProcessTimestamp(), DumpMessaggio.model().POST_PROCESS_TIMESTAMP.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().POST_PROCESSED,false), "?");
		lstObjects_dumpMessaggio.add(new JDBCObject(dumpMessaggio.getPostProcessed(), DumpMessaggio.model().POST_PROCESSED.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().MULTIPART_HEADER_EXT,false), "?");
		lstObjects_dumpMessaggio.add(new JDBCObject(dumpMessaggio.getMultipartHeaderExt(), DumpMessaggio.model().MULTIPART_HEADER_EXT.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().HEADER_EXT,false), "?");
		lstObjects_dumpMessaggio.add(new JDBCObject(dumpMessaggio.getHeaderExt(), DumpMessaggio.model().HEADER_EXT.getFieldType()));
		sqlQueryObjectUpdate.addWhereCondition("id=?");
		lstObjects_dumpMessaggio.add(new JDBCObject(tableId, Long.class));

		if(isUpdate_dumpMessaggio) {
			// Update dumpMessaggio
			jdbcUtilities.executeUpdate(sqlQueryObjectUpdate.createSQLUpdate(), jdbcProperties.isShowSql(), 
				lstObjects_dumpMessaggio.toArray(new JDBCObject[]{}));
		}
		// for dumpMessaggio_multipartHeader

		java.util.List<Long> ids_dumpMessaggio_multipartHeader_da_non_eliminare = new java.util.ArrayList<Long>();
		for (Object dumpMessaggio_multipartHeader_object : dumpMessaggio.getMultipartHeaderList()) {
			DumpMultipartHeader dumpMessaggio_multipartHeader = (DumpMultipartHeader) dumpMessaggio_multipartHeader_object;
			
			// Bug fix OPPT-466 per gestione empty string as null on oracle
			// Rilasciare il vincolo di not null 'fisico' sul database.
			// Imporlo logicamente, in modo che non siano permessi insert o update con valori null.
			// Dopodichè, se nella get viene recuperato un valore null, deve essere trasformato in stringa vuota.
			if(dumpMessaggio_multipartHeader.getValore()==null){
				throw new ServiceException("Header ["+dumpMessaggio_multipartHeader.getNome()+"] with value null");
			}
			
			if(dumpMessaggio_multipartHeader.getId() == null || dumpMessaggio_multipartHeader.getId().longValue() <= 0) {

				long id = dumpMessaggio.getId();			

				// Object dumpMessaggio_multipartHeader
				ISQLQueryObject sqlQueryObjectInsert_dumpMessaggio_multipartHeader = sqlQueryObjectInsert.newSQLQueryObject();
				sqlQueryObjectInsert_dumpMessaggio_multipartHeader.addInsertTable(this.getDumpMessaggioFieldConverter().toTable(DumpMessaggio.model().MULTIPART_HEADER));
				sqlQueryObjectInsert_dumpMessaggio_multipartHeader.addInsertField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().MULTIPART_HEADER.NOME,false),"?");
				sqlQueryObjectInsert_dumpMessaggio_multipartHeader.addInsertField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().MULTIPART_HEADER.VALORE,false),"?");
				sqlQueryObjectInsert_dumpMessaggio_multipartHeader.addInsertField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().MULTIPART_HEADER.DUMP_TIMESTAMP,false),"?");
				sqlQueryObjectInsert_dumpMessaggio_multipartHeader.addInsertField("id_messaggio","?");

				// Insert dumpMessaggio_multipartHeader
				org.openspcoop2.utils.jdbc.IKeyGeneratorObject keyGenerator_dumpMessaggio_multipartHeader = this.getDumpMessaggioFetch().getKeyGeneratorObject(DumpMessaggio.model().MULTIPART_HEADER);
				long id_dumpMessaggio_multipartHeader = jdbcUtilities.insertAndReturnGeneratedKey(sqlQueryObjectInsert_dumpMessaggio_multipartHeader, keyGenerator_dumpMessaggio_multipartHeader, jdbcProperties.isShowSql(),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(dumpMessaggio_multipartHeader.getNome(),DumpMessaggio.model().MULTIPART_HEADER.NOME.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(dumpMessaggio_multipartHeader.getValore(),DumpMessaggio.model().MULTIPART_HEADER.VALORE.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(dumpMessaggio_multipartHeader.getDumpTimestamp(),DumpMessaggio.model().MULTIPART_HEADER.DUMP_TIMESTAMP.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(Long.valueOf(id),Long.class)
				);
				dumpMessaggio_multipartHeader.setId(id_dumpMessaggio_multipartHeader);

				ids_dumpMessaggio_multipartHeader_da_non_eliminare.add(dumpMessaggio_multipartHeader.getId());
			} else {


				// Object dumpMessaggio_multipartHeader
				ISQLQueryObject sqlQueryObjectUpdate_dumpMessaggio_multipartHeader = sqlQueryObjectUpdate.newSQLQueryObject();
				sqlQueryObjectUpdate_dumpMessaggio_multipartHeader.setANDLogicOperator(true);
				sqlQueryObjectUpdate_dumpMessaggio_multipartHeader.addUpdateTable(this.getDumpMessaggioFieldConverter().toTable(DumpMessaggio.model().MULTIPART_HEADER));
				boolean isUpdate_dumpMessaggio_multipartHeader = true;
				java.util.List<JDBCObject> lstObjects_dumpMessaggio_multipartHeader = new java.util.ArrayList<JDBCObject>();
				sqlQueryObjectUpdate_dumpMessaggio_multipartHeader.addUpdateField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().MULTIPART_HEADER.NOME,false), "?");
				lstObjects_dumpMessaggio_multipartHeader.add(new JDBCObject(dumpMessaggio_multipartHeader.getNome(), DumpMessaggio.model().MULTIPART_HEADER.NOME.getFieldType()));
				sqlQueryObjectUpdate_dumpMessaggio_multipartHeader.addUpdateField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().MULTIPART_HEADER.VALORE,false), "?");
				lstObjects_dumpMessaggio_multipartHeader.add(new JDBCObject(dumpMessaggio_multipartHeader.getValore(), DumpMessaggio.model().MULTIPART_HEADER.VALORE.getFieldType()));
				sqlQueryObjectUpdate_dumpMessaggio_multipartHeader.addUpdateField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().MULTIPART_HEADER.DUMP_TIMESTAMP,false), "?");
				lstObjects_dumpMessaggio_multipartHeader.add(new JDBCObject(dumpMessaggio_multipartHeader.getDumpTimestamp(), DumpMessaggio.model().MULTIPART_HEADER.DUMP_TIMESTAMP.getFieldType()));
				sqlQueryObjectUpdate_dumpMessaggio_multipartHeader.addWhereCondition("id=?");
				ids_dumpMessaggio_multipartHeader_da_non_eliminare.add(dumpMessaggio_multipartHeader.getId());
				lstObjects_dumpMessaggio_multipartHeader.add(new JDBCObject(Long.valueOf(dumpMessaggio_multipartHeader.getId()),Long.class));

				if(isUpdate_dumpMessaggio_multipartHeader) {
					// Update dumpMessaggio_multipartHeader
					jdbcUtilities.executeUpdate(sqlQueryObjectUpdate_dumpMessaggio_multipartHeader.createSQLUpdate(), jdbcProperties.isShowSql(), 
						lstObjects_dumpMessaggio_multipartHeader.toArray(new JDBCObject[]{}));
				}
			}
		} // fine for dumpMessaggio_multipartHeader

		// elimino tutte le occorrenze di dumpMessaggio_multipartHeader non presenti nell'update

		ISQLQueryObject sqlQueryObjectUpdate_multipartHeader_deleteList = sqlQueryObjectUpdate.newSQLQueryObject();
		sqlQueryObjectUpdate_multipartHeader_deleteList.setANDLogicOperator(true);
		sqlQueryObjectUpdate_multipartHeader_deleteList.addDeleteTable(this.getDumpMessaggioFieldConverter().toTable(DumpMessaggio.model().MULTIPART_HEADER));
		java.util.List<JDBCObject> jdbcObjects_dumpMessaggio_multipartHeader_delete = new java.util.ArrayList<JDBCObject>();

		sqlQueryObjectUpdate_multipartHeader_deleteList.addWhereCondition("id_messaggio=?");
		jdbcObjects_dumpMessaggio_multipartHeader_delete.add(new JDBCObject(dumpMessaggio.getId(), Long.class));

		StringBuilder marks_dumpMessaggio_multipartHeader = new StringBuilder();
		if(ids_dumpMessaggio_multipartHeader_da_non_eliminare.size() > 0) {
			for(Long ids : ids_dumpMessaggio_multipartHeader_da_non_eliminare) {
				if(marks_dumpMessaggio_multipartHeader.length() > 0) {
					marks_dumpMessaggio_multipartHeader.append(",");
				}
				marks_dumpMessaggio_multipartHeader.append("?");
				jdbcObjects_dumpMessaggio_multipartHeader_delete.add(new JDBCObject(ids, Long.class));

			}
			sqlQueryObjectUpdate_multipartHeader_deleteList.addWhereCondition("id NOT IN ("+marks_dumpMessaggio_multipartHeader.toString()+")");
		}

		jdbcUtilities.execute(sqlQueryObjectUpdate_multipartHeader_deleteList.createSQLDelete(), jdbcProperties.isShowSql(), jdbcObjects_dumpMessaggio_multipartHeader_delete.toArray(new JDBCObject[]{}));

		// for dumpMessaggio_headerTrasporto

		java.util.List<Long> ids_dumpMessaggio_headerTrasporto_da_non_eliminare = new java.util.ArrayList<Long>();
		for (Object dumpMessaggio_headerTrasporto_object : dumpMessaggio.getHeaderTrasportoList()) {
			DumpHeaderTrasporto dumpMessaggio_headerTrasporto = (DumpHeaderTrasporto) dumpMessaggio_headerTrasporto_object;
			
			// Bug fix OPPT-466 per gestione empty string as null on oracle
			// Rilasciare il vincolo di not null 'fisico' sul database.
			// Imporlo logicamente, in modo che non siano permessi insert o update con valori null.
			// Dopodichè, se nella get viene recuperato un valore null, deve essere trasformato in stringa vuota.
			if(dumpMessaggio_headerTrasporto.getValore()==null){
				throw new ServiceException("Header ["+dumpMessaggio_headerTrasporto.getNome()+"] with value null");
			}
			
			if(dumpMessaggio_headerTrasporto.getId() == null || dumpMessaggio_headerTrasporto.getId().longValue() <= 0) {

				long id = dumpMessaggio.getId();			

				// Object dumpMessaggio_headerTrasporto
				ISQLQueryObject sqlQueryObjectInsert_dumpMessaggio_headerTrasporto = sqlQueryObjectInsert.newSQLQueryObject();
				sqlQueryObjectInsert_dumpMessaggio_headerTrasporto.addInsertTable(this.getDumpMessaggioFieldConverter().toTable(DumpMessaggio.model().HEADER_TRASPORTO));
				sqlQueryObjectInsert_dumpMessaggio_headerTrasporto.addInsertField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().HEADER_TRASPORTO.NOME,false),"?");
				sqlQueryObjectInsert_dumpMessaggio_headerTrasporto.addInsertField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().HEADER_TRASPORTO.VALORE,false),"?");
				sqlQueryObjectInsert_dumpMessaggio_headerTrasporto.addInsertField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().HEADER_TRASPORTO.DUMP_TIMESTAMP,false),"?");
				sqlQueryObjectInsert_dumpMessaggio_headerTrasporto.addInsertField("id_messaggio","?");

				// Insert dumpMessaggio_headerTrasporto
				org.openspcoop2.utils.jdbc.IKeyGeneratorObject keyGenerator_dumpMessaggio_headerTrasporto = this.getDumpMessaggioFetch().getKeyGeneratorObject(DumpMessaggio.model().HEADER_TRASPORTO);
				long id_dumpMessaggio_headerTrasporto = jdbcUtilities.insertAndReturnGeneratedKey(sqlQueryObjectInsert_dumpMessaggio_headerTrasporto, keyGenerator_dumpMessaggio_headerTrasporto, jdbcProperties.isShowSql(),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(dumpMessaggio_headerTrasporto.getNome(),DumpMessaggio.model().HEADER_TRASPORTO.NOME.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(dumpMessaggio_headerTrasporto.getValore(),DumpMessaggio.model().HEADER_TRASPORTO.VALORE.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(dumpMessaggio_headerTrasporto.getDumpTimestamp(),DumpMessaggio.model().HEADER_TRASPORTO.DUMP_TIMESTAMP.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(Long.valueOf(id),Long.class)
				);
				dumpMessaggio_headerTrasporto.setId(id_dumpMessaggio_headerTrasporto);

				ids_dumpMessaggio_headerTrasporto_da_non_eliminare.add(dumpMessaggio_headerTrasporto.getId());
			} else {


				// Object dumpMessaggio_headerTrasporto
				ISQLQueryObject sqlQueryObjectUpdate_dumpMessaggio_headerTrasporto = sqlQueryObjectUpdate.newSQLQueryObject();
				sqlQueryObjectUpdate_dumpMessaggio_headerTrasporto.setANDLogicOperator(true);
				sqlQueryObjectUpdate_dumpMessaggio_headerTrasporto.addUpdateTable(this.getDumpMessaggioFieldConverter().toTable(DumpMessaggio.model().HEADER_TRASPORTO));
				boolean isUpdate_dumpMessaggio_headerTrasporto = true;
				java.util.List<JDBCObject> lstObjects_dumpMessaggio_headerTrasporto = new java.util.ArrayList<JDBCObject>();
				sqlQueryObjectUpdate_dumpMessaggio_headerTrasporto.addUpdateField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().HEADER_TRASPORTO.NOME,false), "?");
				lstObjects_dumpMessaggio_headerTrasporto.add(new JDBCObject(dumpMessaggio_headerTrasporto.getNome(), DumpMessaggio.model().HEADER_TRASPORTO.NOME.getFieldType()));
				sqlQueryObjectUpdate_dumpMessaggio_headerTrasporto.addUpdateField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().HEADER_TRASPORTO.VALORE,false), "?");
				lstObjects_dumpMessaggio_headerTrasporto.add(new JDBCObject(dumpMessaggio_headerTrasporto.getValore(), DumpMessaggio.model().HEADER_TRASPORTO.VALORE.getFieldType()));
				sqlQueryObjectUpdate_dumpMessaggio_headerTrasporto.addUpdateField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().HEADER_TRASPORTO.DUMP_TIMESTAMP,false), "?");
				lstObjects_dumpMessaggio_headerTrasporto.add(new JDBCObject(dumpMessaggio_headerTrasporto.getDumpTimestamp(), DumpMessaggio.model().HEADER_TRASPORTO.DUMP_TIMESTAMP.getFieldType()));
				sqlQueryObjectUpdate_dumpMessaggio_headerTrasporto.addWhereCondition("id=?");
				ids_dumpMessaggio_headerTrasporto_da_non_eliminare.add(dumpMessaggio_headerTrasporto.getId());
				lstObjects_dumpMessaggio_headerTrasporto.add(new JDBCObject(Long.valueOf(dumpMessaggio_headerTrasporto.getId()),Long.class));

				if(isUpdate_dumpMessaggio_headerTrasporto) {
					// Update dumpMessaggio_headerTrasporto
					jdbcUtilities.executeUpdate(sqlQueryObjectUpdate_dumpMessaggio_headerTrasporto.createSQLUpdate(), jdbcProperties.isShowSql(), 
						lstObjects_dumpMessaggio_headerTrasporto.toArray(new JDBCObject[]{}));
				}
			}
		} // fine for dumpMessaggio_headerTrasporto

		// elimino tutte le occorrenze di dumpMessaggio_headerTrasporto non presenti nell'update

		ISQLQueryObject sqlQueryObjectUpdate_headerTrasporto_deleteList = sqlQueryObjectUpdate.newSQLQueryObject();
		sqlQueryObjectUpdate_headerTrasporto_deleteList.setANDLogicOperator(true);
		sqlQueryObjectUpdate_headerTrasporto_deleteList.addDeleteTable(this.getDumpMessaggioFieldConverter().toTable(DumpMessaggio.model().HEADER_TRASPORTO));
		java.util.List<JDBCObject> jdbcObjects_dumpMessaggio_headerTrasporto_delete = new java.util.ArrayList<JDBCObject>();

		sqlQueryObjectUpdate_headerTrasporto_deleteList.addWhereCondition("id_messaggio=?");
		jdbcObjects_dumpMessaggio_headerTrasporto_delete.add(new JDBCObject(dumpMessaggio.getId(), Long.class));

		StringBuilder marks_dumpMessaggio_headerTrasporto = new StringBuilder();
		if(ids_dumpMessaggio_headerTrasporto_da_non_eliminare.size() > 0) {
			for(Long ids : ids_dumpMessaggio_headerTrasporto_da_non_eliminare) {
				if(marks_dumpMessaggio_headerTrasporto.length() > 0) {
					marks_dumpMessaggio_headerTrasporto.append(",");
				}
				marks_dumpMessaggio_headerTrasporto.append("?");
				jdbcObjects_dumpMessaggio_headerTrasporto_delete.add(new JDBCObject(ids, Long.class));

			}
			sqlQueryObjectUpdate_headerTrasporto_deleteList.addWhereCondition("id NOT IN ("+marks_dumpMessaggio_headerTrasporto.toString()+")");
		}

		jdbcUtilities.execute(sqlQueryObjectUpdate_headerTrasporto_deleteList.createSQLDelete(), jdbcProperties.isShowSql(), jdbcObjects_dumpMessaggio_headerTrasporto_delete.toArray(new JDBCObject[]{}));

		// for dumpMessaggio_allegato

		java.util.List<Long> ids_dumpMessaggio_allegato_da_non_eliminare = new java.util.ArrayList<Long>();
		for (Object dumpMessaggio_allegato_object : dumpMessaggio.getAllegatoList()) {
			DumpAllegato dumpMessaggio_allegato = (DumpAllegato) dumpMessaggio_allegato_object;
			if(dumpMessaggio_allegato.getId() == null || dumpMessaggio_allegato.getId().longValue() <= 0) {

				long id = dumpMessaggio.getId();			

				// Object dumpMessaggio_allegato
				ISQLQueryObject sqlQueryObjectInsert_dumpMessaggio_allegato = sqlQueryObjectInsert.newSQLQueryObject();
				sqlQueryObjectInsert_dumpMessaggio_allegato.addInsertTable(this.getDumpMessaggioFieldConverter().toTable(DumpMessaggio.model().ALLEGATO));
				sqlQueryObjectInsert_dumpMessaggio_allegato.addInsertField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().ALLEGATO.CONTENT_TYPE,false),"?");
				sqlQueryObjectInsert_dumpMessaggio_allegato.addInsertField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().ALLEGATO.CONTENT_ID,false),"?");
				sqlQueryObjectInsert_dumpMessaggio_allegato.addInsertField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().ALLEGATO.CONTENT_LOCATION,false),"?");
				sqlQueryObjectInsert_dumpMessaggio_allegato.addInsertField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().ALLEGATO.ALLEGATO,false),"?");
				sqlQueryObjectInsert_dumpMessaggio_allegato.addInsertField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().ALLEGATO.DUMP_TIMESTAMP,false),"?");
				sqlQueryObjectInsert_dumpMessaggio_allegato.addInsertField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().ALLEGATO.HEADER_EXT,false),"?");
				sqlQueryObjectInsert_dumpMessaggio_allegato.addInsertField("id_messaggio","?");

				// Insert dumpMessaggio_allegato
				org.openspcoop2.utils.jdbc.IKeyGeneratorObject keyGenerator_dumpMessaggio_allegato = this.getDumpMessaggioFetch().getKeyGeneratorObject(DumpMessaggio.model().ALLEGATO);
				long id_dumpMessaggio_allegato = jdbcUtilities.insertAndReturnGeneratedKey(sqlQueryObjectInsert_dumpMessaggio_allegato, keyGenerator_dumpMessaggio_allegato, jdbcProperties.isShowSql(),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(dumpMessaggio_allegato.getContentType(),DumpMessaggio.model().ALLEGATO.CONTENT_TYPE.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(dumpMessaggio_allegato.getContentId(),DumpMessaggio.model().ALLEGATO.CONTENT_ID.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(dumpMessaggio_allegato.getContentLocation(),DumpMessaggio.model().ALLEGATO.CONTENT_LOCATION.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(dumpMessaggio_allegato.getAllegato(),DumpMessaggio.model().ALLEGATO.ALLEGATO.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(dumpMessaggio_allegato.getDumpTimestamp(),DumpMessaggio.model().ALLEGATO.DUMP_TIMESTAMP.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(dumpMessaggio_allegato.getHeaderExt(),DumpMessaggio.model().ALLEGATO.HEADER_EXT.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(Long.valueOf(id),Long.class)
				);
				dumpMessaggio_allegato.setId(id_dumpMessaggio_allegato);

				// for dumpMessaggio_allegato
				for (int i_dumpMessaggio_allegato = 0; i_dumpMessaggio_allegato < dumpMessaggio_allegato.getHeaderList().size(); i_dumpMessaggio_allegato++) {

					// Bug fix OPPT-466 per gestione empty string as null on oracle
					// Rilasciare il vincolo di not null 'fisico' sul database.
					// Imporlo logicamente, in modo che non siano permessi insert o update con valori null.
					// Dopodichè, se nella get viene recuperato un valore null, deve essere trasformato in stringa vuota.
					if(dumpMessaggio_allegato.getHeaderList().get(i_dumpMessaggio_allegato).getValore()==null){
						throw new ServiceException("Header ["+dumpMessaggio_allegato.getHeaderList().get(i_dumpMessaggio_allegato).getNome()+"] dell'allegato ["+dumpMessaggio_allegato.getContentId()+"] with value null");
					}

					// Object dumpMessaggio_allegato.getHeaderList().get(i_dumpMessaggio_allegato)
					ISQLQueryObject sqlQueryObjectInsert_dumpMessaggio_allegato_header = sqlQueryObjectInsert.newSQLQueryObject();
					sqlQueryObjectInsert_dumpMessaggio_allegato_header.addInsertTable(this.getDumpMessaggioFieldConverter().toTable(DumpMessaggio.model().ALLEGATO.HEADER));
					sqlQueryObjectInsert_dumpMessaggio_allegato_header.addInsertField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().ALLEGATO.HEADER.NOME,false),"?");
					sqlQueryObjectInsert_dumpMessaggio_allegato_header.addInsertField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().ALLEGATO.HEADER.VALORE,false),"?");
					sqlQueryObjectInsert_dumpMessaggio_allegato_header.addInsertField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().ALLEGATO.HEADER.DUMP_TIMESTAMP,false),"?");
					sqlQueryObjectInsert_dumpMessaggio_allegato_header.addInsertField("id_allegato","?");

					// Insert dumpMessaggio_allegato.getHeaderList().get(i_dumpMessaggio_allegato)
					org.openspcoop2.utils.jdbc.IKeyGeneratorObject keyGenerator_dumpMessaggio_allegato_header = this.getDumpMessaggioFetch().getKeyGeneratorObject(DumpMessaggio.model().ALLEGATO.HEADER);
					long id_dumpMessaggio_allegato_header = jdbcUtilities.insertAndReturnGeneratedKey(sqlQueryObjectInsert_dumpMessaggio_allegato_header, keyGenerator_dumpMessaggio_allegato_header, jdbcProperties.isShowSql(),
						new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(dumpMessaggio_allegato.getHeaderList().get(i_dumpMessaggio_allegato).getNome(),DumpMessaggio.model().ALLEGATO.HEADER.NOME.getFieldType()),
						new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(dumpMessaggio_allegato.getHeaderList().get(i_dumpMessaggio_allegato).getValore(),DumpMessaggio.model().ALLEGATO.HEADER.VALORE.getFieldType()),
						new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(dumpMessaggio_allegato.getHeaderList().get(i_dumpMessaggio_allegato).getDumpTimestamp(),DumpMessaggio.model().ALLEGATO.HEADER.DUMP_TIMESTAMP.getFieldType()),
						new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(Long.valueOf(id_dumpMessaggio_allegato),Long.class)
					);
					dumpMessaggio_allegato.getHeaderList().get(i_dumpMessaggio_allegato).setId(id_dumpMessaggio_allegato_header);
				} // fine for _dumpMessaggio_allegato

				ids_dumpMessaggio_allegato_da_non_eliminare.add(dumpMessaggio_allegato.getId());
			} else {


				// Object dumpMessaggio_allegato
				ISQLQueryObject sqlQueryObjectUpdate_dumpMessaggio_allegato = sqlQueryObjectUpdate.newSQLQueryObject();
				sqlQueryObjectUpdate_dumpMessaggio_allegato.setANDLogicOperator(true);
				sqlQueryObjectUpdate_dumpMessaggio_allegato.addUpdateTable(this.getDumpMessaggioFieldConverter().toTable(DumpMessaggio.model().ALLEGATO));
				boolean isUpdate_dumpMessaggio_allegato = true;
				java.util.List<JDBCObject> lstObjects_dumpMessaggio_allegato = new java.util.ArrayList<JDBCObject>();
				sqlQueryObjectUpdate_dumpMessaggio_allegato.addUpdateField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().ALLEGATO.CONTENT_TYPE,false), "?");
				lstObjects_dumpMessaggio_allegato.add(new JDBCObject(dumpMessaggio_allegato.getContentType(), DumpMessaggio.model().ALLEGATO.CONTENT_TYPE.getFieldType()));
				sqlQueryObjectUpdate_dumpMessaggio_allegato.addUpdateField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().ALLEGATO.CONTENT_ID,false), "?");
				lstObjects_dumpMessaggio_allegato.add(new JDBCObject(dumpMessaggio_allegato.getContentId(), DumpMessaggio.model().ALLEGATO.CONTENT_ID.getFieldType()));
				sqlQueryObjectUpdate_dumpMessaggio_allegato.addUpdateField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().ALLEGATO.CONTENT_LOCATION,false), "?");
				lstObjects_dumpMessaggio_allegato.add(new JDBCObject(dumpMessaggio_allegato.getContentLocation(), DumpMessaggio.model().ALLEGATO.CONTENT_LOCATION.getFieldType()));
				sqlQueryObjectUpdate_dumpMessaggio_allegato.addUpdateField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().ALLEGATO.ALLEGATO,false), "?");
				lstObjects_dumpMessaggio_allegato.add(new JDBCObject(dumpMessaggio_allegato.getAllegato(), DumpMessaggio.model().ALLEGATO.ALLEGATO.getFieldType()));
				sqlQueryObjectUpdate_dumpMessaggio_allegato.addUpdateField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().ALLEGATO.DUMP_TIMESTAMP,false), "?");
				lstObjects_dumpMessaggio_allegato.add(new JDBCObject(dumpMessaggio_allegato.getDumpTimestamp(), DumpMessaggio.model().ALLEGATO.DUMP_TIMESTAMP.getFieldType()));
				sqlQueryObjectUpdate_dumpMessaggio_allegato.addUpdateField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().ALLEGATO.HEADER_EXT,false), "?");
				lstObjects_dumpMessaggio_allegato.add(new JDBCObject(dumpMessaggio_allegato.getHeaderExt(), DumpMessaggio.model().ALLEGATO.HEADER_EXT.getFieldType()));
				sqlQueryObjectUpdate_dumpMessaggio_allegato.addWhereCondition("id=?");
				ids_dumpMessaggio_allegato_da_non_eliminare.add(dumpMessaggio_allegato.getId());
				lstObjects_dumpMessaggio_allegato.add(new JDBCObject(Long.valueOf(dumpMessaggio_allegato.getId()),Long.class));

				if(isUpdate_dumpMessaggio_allegato) {
					// Update dumpMessaggio_allegato
					jdbcUtilities.executeUpdate(sqlQueryObjectUpdate_dumpMessaggio_allegato.createSQLUpdate(), jdbcProperties.isShowSql(), 
						lstObjects_dumpMessaggio_allegato.toArray(new JDBCObject[]{}));
				}
				// for dumpMessaggio_allegato_header

				java.util.List<Long> ids_dumpMessaggio_allegato_header_da_non_eliminare = new java.util.ArrayList<Long>();
				for (Object dumpMessaggio_allegato_header_object : dumpMessaggio_allegato.getHeaderList()) {
					DumpHeaderAllegato dumpMessaggio_allegato_header = (DumpHeaderAllegato) dumpMessaggio_allegato_header_object;
					if(dumpMessaggio_allegato_header.getId() == null || dumpMessaggio_allegato_header.getId().longValue() <= 0) {

						long id_dumpMessaggio_allegato = dumpMessaggio_allegato.getId();					

						// Object dumpMessaggio_allegato_header
						ISQLQueryObject sqlQueryObjectInsert_dumpMessaggio_allegato_header = sqlQueryObjectInsert.newSQLQueryObject();
						sqlQueryObjectInsert_dumpMessaggio_allegato_header.addInsertTable(this.getDumpMessaggioFieldConverter().toTable(DumpMessaggio.model().ALLEGATO.HEADER));
						sqlQueryObjectInsert_dumpMessaggio_allegato_header.addInsertField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().ALLEGATO.HEADER.NOME,false),"?");
						sqlQueryObjectInsert_dumpMessaggio_allegato_header.addInsertField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().ALLEGATO.HEADER.VALORE,false),"?");
						sqlQueryObjectInsert_dumpMessaggio_allegato_header.addInsertField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().ALLEGATO.HEADER.DUMP_TIMESTAMP,false),"?");
						sqlQueryObjectInsert_dumpMessaggio_allegato_header.addInsertField("id_allegato","?");

						// Insert dumpMessaggio_allegato_header
						org.openspcoop2.utils.jdbc.IKeyGeneratorObject keyGenerator_dumpMessaggio_allegato_header = this.getDumpMessaggioFetch().getKeyGeneratorObject(DumpMessaggio.model().ALLEGATO.HEADER);
						long id_dumpMessaggio_allegato_header = jdbcUtilities.insertAndReturnGeneratedKey(sqlQueryObjectInsert_dumpMessaggio_allegato_header, keyGenerator_dumpMessaggio_allegato_header, jdbcProperties.isShowSql(),
							new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(dumpMessaggio_allegato_header.getNome(),DumpMessaggio.model().ALLEGATO.HEADER.NOME.getFieldType()),
							new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(dumpMessaggio_allegato_header.getValore(),DumpMessaggio.model().ALLEGATO.HEADER.VALORE.getFieldType()),
							new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(dumpMessaggio_allegato_header.getDumpTimestamp(),DumpMessaggio.model().ALLEGATO.HEADER.DUMP_TIMESTAMP.getFieldType()),
							new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(Long.valueOf(id_dumpMessaggio_allegato),Long.class)
						);
						dumpMessaggio_allegato_header.setId(id_dumpMessaggio_allegato_header);

						ids_dumpMessaggio_allegato_header_da_non_eliminare.add(dumpMessaggio_allegato_header.getId());
					} else {


						// Object dumpMessaggio_allegato_header
						ISQLQueryObject sqlQueryObjectUpdate_dumpMessaggio_allegato_header = sqlQueryObjectUpdate.newSQLQueryObject();
						sqlQueryObjectUpdate_dumpMessaggio_allegato_header.setANDLogicOperator(true);
						sqlQueryObjectUpdate_dumpMessaggio_allegato_header.addUpdateTable(this.getDumpMessaggioFieldConverter().toTable(DumpMessaggio.model().ALLEGATO.HEADER));
						boolean isUpdate_dumpMessaggio_allegato_header = true;
						java.util.List<JDBCObject> lstObjects_dumpMessaggio_allegato_header = new java.util.ArrayList<JDBCObject>();
						sqlQueryObjectUpdate_dumpMessaggio_allegato_header.addUpdateField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().ALLEGATO.HEADER.NOME,false), "?");
						lstObjects_dumpMessaggio_allegato_header.add(new JDBCObject(dumpMessaggio_allegato_header.getNome(), DumpMessaggio.model().ALLEGATO.HEADER.NOME.getFieldType()));
						sqlQueryObjectUpdate_dumpMessaggio_allegato_header.addUpdateField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().ALLEGATO.HEADER.VALORE,false), "?");
						lstObjects_dumpMessaggio_allegato_header.add(new JDBCObject(dumpMessaggio_allegato_header.getValore(), DumpMessaggio.model().ALLEGATO.HEADER.VALORE.getFieldType()));
						sqlQueryObjectUpdate_dumpMessaggio_allegato_header.addUpdateField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().ALLEGATO.HEADER.DUMP_TIMESTAMP,false), "?");
						lstObjects_dumpMessaggio_allegato_header.add(new JDBCObject(dumpMessaggio_allegato_header.getDumpTimestamp(), DumpMessaggio.model().ALLEGATO.HEADER.DUMP_TIMESTAMP.getFieldType()));
						sqlQueryObjectUpdate_dumpMessaggio_allegato_header.addWhereCondition("id=?");
						ids_dumpMessaggio_allegato_header_da_non_eliminare.add(dumpMessaggio_allegato_header.getId());
						lstObjects_dumpMessaggio_allegato_header.add(new JDBCObject(Long.valueOf(dumpMessaggio_allegato_header.getId()),Long.class));

						if(isUpdate_dumpMessaggio_allegato_header) {
							// Update dumpMessaggio_allegato_header
							jdbcUtilities.executeUpdate(sqlQueryObjectUpdate_dumpMessaggio_allegato_header.createSQLUpdate(), jdbcProperties.isShowSql(), 
								lstObjects_dumpMessaggio_allegato_header.toArray(new JDBCObject[]{}));
						}
					}
				} // fine for dumpMessaggio_allegato_header

				// elimino tutte le occorrenze di dumpMessaggio_allegato_header non presenti nell'update

				ISQLQueryObject sqlQueryObjectUpdate_dumpMessaggio_allegato_header_deleteList = sqlQueryObjectUpdate.newSQLQueryObject();
				sqlQueryObjectUpdate_dumpMessaggio_allegato_header_deleteList.setANDLogicOperator(true);
				sqlQueryObjectUpdate_dumpMessaggio_allegato_header_deleteList.addDeleteTable(this.getDumpMessaggioFieldConverter().toTable(DumpMessaggio.model().ALLEGATO.HEADER));
				java.util.List<JDBCObject> jdbcObjects_dumpMessaggio_allegato_header_delete = new java.util.ArrayList<JDBCObject>();

				sqlQueryObjectUpdate_dumpMessaggio_allegato_header_deleteList.addWhereCondition("id_allegato=?");
				jdbcObjects_dumpMessaggio_allegato_header_delete.add(new JDBCObject(dumpMessaggio_allegato.getId(), Long.class));

				StringBuilder marks_dumpMessaggio_allegato_header = new StringBuilder();
				if(ids_dumpMessaggio_allegato_header_da_non_eliminare.size() > 0) {
					for(Long ids : ids_dumpMessaggio_allegato_header_da_non_eliminare) {
						if(marks_dumpMessaggio_allegato_header.length() > 0) {
							marks_dumpMessaggio_allegato_header.append(",");
						}
						marks_dumpMessaggio_allegato_header.append("?");
						jdbcObjects_dumpMessaggio_allegato_header_delete.add(new JDBCObject(ids, Long.class));

					}
					sqlQueryObjectUpdate_dumpMessaggio_allegato_header_deleteList.addWhereCondition("id NOT IN ("+marks_dumpMessaggio_allegato_header.toString()+")");
				}

				jdbcUtilities.execute(sqlQueryObjectUpdate_dumpMessaggio_allegato_header_deleteList.createSQLDelete(), jdbcProperties.isShowSql(), jdbcObjects_dumpMessaggio_allegato_header_delete.toArray(new JDBCObject[]{}));

			}
		} // fine for dumpMessaggio_allegato

		// elimino tutte le occorrenze di dumpMessaggio_allegato non presenti nell'update

		ISQLQueryObject sqlQueryObjectUpdate_allegato_deleteList = sqlQueryObjectUpdate.newSQLQueryObject();
		sqlQueryObjectUpdate_allegato_deleteList.setANDLogicOperator(true);
		sqlQueryObjectUpdate_allegato_deleteList.addDeleteTable(this.getDumpMessaggioFieldConverter().toTable(DumpMessaggio.model().ALLEGATO));
		java.util.List<JDBCObject> jdbcObjects_dumpMessaggio_allegato_delete = new java.util.ArrayList<JDBCObject>();

		sqlQueryObjectUpdate_allegato_deleteList.addWhereCondition("id_messaggio=?");
		jdbcObjects_dumpMessaggio_allegato_delete.add(new JDBCObject(dumpMessaggio.getId(), Long.class));

		StringBuilder marks_dumpMessaggio_allegato = new StringBuilder();
		if(ids_dumpMessaggio_allegato_da_non_eliminare.size() > 0) {
			for(Long ids : ids_dumpMessaggio_allegato_da_non_eliminare) {
				if(marks_dumpMessaggio_allegato.length() > 0) {
					marks_dumpMessaggio_allegato.append(",");
				}
				marks_dumpMessaggio_allegato.append("?");
				jdbcObjects_dumpMessaggio_allegato_delete.add(new JDBCObject(ids, Long.class));

			}
			sqlQueryObjectUpdate_allegato_deleteList.addWhereCondition("id NOT IN ("+marks_dumpMessaggio_allegato.toString()+")");
		}

		jdbcUtilities.execute(sqlQueryObjectUpdate_allegato_deleteList.createSQLDelete(), jdbcProperties.isShowSql(), jdbcObjects_dumpMessaggio_allegato_delete.toArray(new JDBCObject[]{}));

		// for dumpMessaggio_contenuto

		java.util.List<Long> ids_dumpMessaggio_contenuto_da_non_eliminare = new java.util.ArrayList<Long>();
		for (Object dumpMessaggio_contenuto_object : dumpMessaggio.getContenutoList()) {
			DumpContenuto dumpMessaggio_contenuto = (DumpContenuto) dumpMessaggio_contenuto_object;
			if(dumpMessaggio_contenuto.getId() == null || dumpMessaggio_contenuto.getId().longValue() <= 0) {

				long id = dumpMessaggio.getId();			

				// Object dumpMessaggio_contenuto
				ISQLQueryObject sqlQueryObjectInsert_dumpMessaggio_contenuto = sqlQueryObjectInsert.newSQLQueryObject();
				sqlQueryObjectInsert_dumpMessaggio_contenuto.addInsertTable(this.getDumpMessaggioFieldConverter().toTable(DumpMessaggio.model().CONTENUTO));
				sqlQueryObjectInsert_dumpMessaggio_contenuto.addInsertField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().CONTENUTO.NOME,false),"?");
				sqlQueryObjectInsert_dumpMessaggio_contenuto.addInsertField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().CONTENUTO.VALORE,false),"?");
				sqlQueryObjectInsert_dumpMessaggio_contenuto.addInsertField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().CONTENUTO.VALORE_AS_BYTES,false),"?");
				sqlQueryObjectInsert_dumpMessaggio_contenuto.addInsertField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().CONTENUTO.DUMP_TIMESTAMP,false),"?");
				sqlQueryObjectInsert_dumpMessaggio_contenuto.addInsertField("id_messaggio","?");

				// Insert dumpMessaggio_contenuto
				org.openspcoop2.utils.jdbc.IKeyGeneratorObject keyGenerator_dumpMessaggio_contenuto = this.getDumpMessaggioFetch().getKeyGeneratorObject(DumpMessaggio.model().CONTENUTO);
				long id_dumpMessaggio_contenuto = jdbcUtilities.insertAndReturnGeneratedKey(sqlQueryObjectInsert_dumpMessaggio_contenuto, keyGenerator_dumpMessaggio_contenuto, jdbcProperties.isShowSql(),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(dumpMessaggio_contenuto.getNome(),DumpMessaggio.model().CONTENUTO.NOME.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(dumpMessaggio_contenuto.getValore(),DumpMessaggio.model().CONTENUTO.VALORE.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(dumpMessaggio_contenuto.getValoreAsBytes(),DumpMessaggio.model().CONTENUTO.VALORE_AS_BYTES.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(dumpMessaggio_contenuto.getDumpTimestamp(),DumpMessaggio.model().CONTENUTO.DUMP_TIMESTAMP.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(Long.valueOf(id),Long.class)
				);
				dumpMessaggio_contenuto.setId(id_dumpMessaggio_contenuto);

				ids_dumpMessaggio_contenuto_da_non_eliminare.add(dumpMessaggio_contenuto.getId());
			} else {


				// Object dumpMessaggio_contenuto
				ISQLQueryObject sqlQueryObjectUpdate_dumpMessaggio_contenuto = sqlQueryObjectUpdate.newSQLQueryObject();
				sqlQueryObjectUpdate_dumpMessaggio_contenuto.setANDLogicOperator(true);
				sqlQueryObjectUpdate_dumpMessaggio_contenuto.addUpdateTable(this.getDumpMessaggioFieldConverter().toTable(DumpMessaggio.model().CONTENUTO));
				boolean isUpdate_dumpMessaggio_contenuto = true;
				java.util.List<JDBCObject> lstObjects_dumpMessaggio_contenuto = new java.util.ArrayList<JDBCObject>();
				sqlQueryObjectUpdate_dumpMessaggio_contenuto.addUpdateField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().CONTENUTO.NOME,false), "?");
				lstObjects_dumpMessaggio_contenuto.add(new JDBCObject(dumpMessaggio_contenuto.getNome(), DumpMessaggio.model().CONTENUTO.NOME.getFieldType()));
				sqlQueryObjectUpdate_dumpMessaggio_contenuto.addUpdateField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().CONTENUTO.VALORE,false), "?");
				lstObjects_dumpMessaggio_contenuto.add(new JDBCObject(dumpMessaggio_contenuto.getValore(), DumpMessaggio.model().CONTENUTO.VALORE.getFieldType()));
				sqlQueryObjectUpdate_dumpMessaggio_contenuto.addUpdateField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().CONTENUTO.VALORE_AS_BYTES,false), "?");
				lstObjects_dumpMessaggio_contenuto.add(new JDBCObject(dumpMessaggio_contenuto.getValoreAsBytes(), DumpMessaggio.model().CONTENUTO.VALORE_AS_BYTES.getFieldType()));
				sqlQueryObjectUpdate_dumpMessaggio_contenuto.addUpdateField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().CONTENUTO.DUMP_TIMESTAMP,false), "?");
				lstObjects_dumpMessaggio_contenuto.add(new JDBCObject(dumpMessaggio_contenuto.getDumpTimestamp(), DumpMessaggio.model().CONTENUTO.DUMP_TIMESTAMP.getFieldType()));
				sqlQueryObjectUpdate_dumpMessaggio_contenuto.addWhereCondition("id=?");
				ids_dumpMessaggio_contenuto_da_non_eliminare.add(dumpMessaggio_contenuto.getId());
				lstObjects_dumpMessaggio_contenuto.add(new JDBCObject(Long.valueOf(dumpMessaggio_contenuto.getId()),Long.class));

				if(isUpdate_dumpMessaggio_contenuto) {
					// Update dumpMessaggio_contenuto
					jdbcUtilities.executeUpdate(sqlQueryObjectUpdate_dumpMessaggio_contenuto.createSQLUpdate(), jdbcProperties.isShowSql(), 
						lstObjects_dumpMessaggio_contenuto.toArray(new JDBCObject[]{}));
				}
			}
		} // fine for dumpMessaggio_contenuto

		// elimino tutte le occorrenze di dumpMessaggio_contenuto non presenti nell'update

		ISQLQueryObject sqlQueryObjectUpdate_contenuto_deleteList = sqlQueryObjectUpdate.newSQLQueryObject();
		sqlQueryObjectUpdate_contenuto_deleteList.setANDLogicOperator(true);
		sqlQueryObjectUpdate_contenuto_deleteList.addDeleteTable(this.getDumpMessaggioFieldConverter().toTable(DumpMessaggio.model().CONTENUTO));
		java.util.List<JDBCObject> jdbcObjects_dumpMessaggio_contenuto_delete = new java.util.ArrayList<JDBCObject>();

		sqlQueryObjectUpdate_contenuto_deleteList.addWhereCondition("id_messaggio=?");
		jdbcObjects_dumpMessaggio_contenuto_delete.add(new JDBCObject(dumpMessaggio.getId(), Long.class));

		StringBuilder marks_dumpMessaggio_contenuto = new StringBuilder();
		if(ids_dumpMessaggio_contenuto_da_non_eliminare.size() > 0) {
			for(Long ids : ids_dumpMessaggio_contenuto_da_non_eliminare) {
				if(marks_dumpMessaggio_contenuto.length() > 0) {
					marks_dumpMessaggio_contenuto.append(",");
				}
				marks_dumpMessaggio_contenuto.append("?");
				jdbcObjects_dumpMessaggio_contenuto_delete.add(new JDBCObject(ids, Long.class));

			}
			sqlQueryObjectUpdate_contenuto_deleteList.addWhereCondition("id NOT IN ("+marks_dumpMessaggio_contenuto.toString()+")");
		}

		jdbcUtilities.execute(sqlQueryObjectUpdate_contenuto_deleteList.createSQLDelete(), jdbcProperties.isShowSql(), jdbcObjects_dumpMessaggio_contenuto_delete.toArray(new JDBCObject[]{}));



	}
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdDumpMessaggio id, UpdateField ... updateFields) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		
		JDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getDumpMessaggioFieldConverter().toTable(DumpMessaggio.model()), 
				this._getMapTableToPKColumn(), 
				this._getRootTablePrimaryKeyValues(jdbcProperties, log, connection, sqlQueryObject, id),
				this.getDumpMessaggioFieldConverter(), this, null, updateFields);
	}
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdDumpMessaggio id, IExpression condition, UpdateField ... updateFields) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		
		JDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getDumpMessaggioFieldConverter().toTable(DumpMessaggio.model()), 
				this._getMapTableToPKColumn(), 
				this._getRootTablePrimaryKeyValues(jdbcProperties, log, connection, sqlQueryObject, id),
				this.getDumpMessaggioFieldConverter(), this, condition, updateFields);
	}
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdDumpMessaggio id, UpdateModel ... updateModels) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		
		JDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getDumpMessaggioFieldConverter().toTable(DumpMessaggio.model()), 
				this._getMapTableToPKColumn(), 
				this._getRootTablePrimaryKeyValues(jdbcProperties, log, connection, sqlQueryObject, id),
				this.getDumpMessaggioFieldConverter(), this, updateModels);
	}	
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, UpdateField ... updateFields) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		java.util.List<Object> ids = new java.util.ArrayList<Object>();
		ids.add(tableId);
		JDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getDumpMessaggioFieldConverter().toTable(DumpMessaggio.model()), 
				this._getMapTableToPKColumn(), 
				ids,
				this.getDumpMessaggioFieldConverter(), this, null, updateFields);
	}
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, IExpression condition, UpdateField ... updateFields) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		java.util.List<Object> ids = new java.util.ArrayList<Object>();
		ids.add(tableId);
		JDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getDumpMessaggioFieldConverter().toTable(DumpMessaggio.model()), 
				this._getMapTableToPKColumn(), 
				ids,
				this.getDumpMessaggioFieldConverter(), this, condition, updateFields);
	}
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, UpdateModel ... updateModels) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		java.util.List<Object> ids = new java.util.ArrayList<Object>();
		ids.add(tableId);
		JDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getDumpMessaggioFieldConverter().toTable(DumpMessaggio.model()), 
				this._getMapTableToPKColumn(), 
				ids,
				this.getDumpMessaggioFieldConverter(), this, updateModels);
	}
	
	@Override
	public void updateOrCreate(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdDumpMessaggio oldId, DumpMessaggio dumpMessaggio, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotImplementedException,ServiceException,Exception {
	
		if(this.exists(jdbcProperties, log, connection, sqlQueryObject, oldId)) {
			this.update(jdbcProperties, log, connection, sqlQueryObject, oldId, dumpMessaggio,idMappingResolutionBehaviour);
		} else {
			this.create(jdbcProperties, log, connection, sqlQueryObject, dumpMessaggio,idMappingResolutionBehaviour);
		}
		
	}
	
	@Override
	public void updateOrCreate(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, DumpMessaggio dumpMessaggio, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotImplementedException,ServiceException,Exception {
		if(this.exists(jdbcProperties, log, connection, sqlQueryObject, tableId)) {
			this.update(jdbcProperties, log, connection, sqlQueryObject, tableId, dumpMessaggio,idMappingResolutionBehaviour);
		} else {
			this.create(jdbcProperties, log, connection, sqlQueryObject, dumpMessaggio,idMappingResolutionBehaviour);
		}
	}
	
	@Override
	public void delete(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, DumpMessaggio dumpMessaggio) throws NotImplementedException,ServiceException,Exception {
		
		
		Long longId = null;
		if( (dumpMessaggio.getId()!=null) && (dumpMessaggio.getId()>0) ){
			longId = dumpMessaggio.getId();
		}
		else{
			IdDumpMessaggio idDumpMessaggio = this.convertToId(jdbcProperties,log,connection,sqlQueryObject,dumpMessaggio);
			longId = this.findIdDumpMessaggio(jdbcProperties,log,connection,sqlQueryObject,idDumpMessaggio,false);
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
		

		//Recupero oggetto _dumpMessaggio_multipartHeader
		ISQLQueryObject sqlQueryObjectDelete_dumpMessaggio_multipartHeader_getToDelete = sqlQueryObjectDelete.newSQLQueryObject();
		sqlQueryObjectDelete_dumpMessaggio_multipartHeader_getToDelete.setANDLogicOperator(true);
		sqlQueryObjectDelete_dumpMessaggio_multipartHeader_getToDelete.addFromTable(this.getDumpMessaggioFieldConverter().toTable(DumpMessaggio.model().MULTIPART_HEADER));
		sqlQueryObjectDelete_dumpMessaggio_multipartHeader_getToDelete.addWhereCondition("id_messaggio=?");
		java.util.List<Object> dumpMessaggio_multipartHeader_toDelete_list = jdbcUtilities.executeQuery(sqlQueryObjectDelete_dumpMessaggio_multipartHeader_getToDelete.createSQLQuery(), jdbcProperties.isShowSql(), DumpMessaggio.model().MULTIPART_HEADER, this.getDumpMessaggioFetch(),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(Long.valueOf(id),Long.class));

		// for dumpMessaggio_multipartHeader
		for (Object dumpMessaggio_multipartHeader_object : dumpMessaggio_multipartHeader_toDelete_list) {
			DumpMultipartHeader dumpMessaggio_multipartHeader = (DumpMultipartHeader) dumpMessaggio_multipartHeader_object;

			// Object dumpMessaggio_multipartHeader
			ISQLQueryObject sqlQueryObjectDelete_dumpMessaggio_multipartHeader = sqlQueryObjectDelete.newSQLQueryObject();
			sqlQueryObjectDelete_dumpMessaggio_multipartHeader.setANDLogicOperator(true);
			sqlQueryObjectDelete_dumpMessaggio_multipartHeader.addDeleteTable(this.getDumpMessaggioFieldConverter().toTable(DumpMessaggio.model().MULTIPART_HEADER));
			sqlQueryObjectDelete_dumpMessaggio_multipartHeader.addWhereCondition("id=?");

			// Delete dumpMessaggio_multipartHeader
			if(dumpMessaggio_multipartHeader != null){
				jdbcUtilities.execute(sqlQueryObjectDelete_dumpMessaggio_multipartHeader.createSQLDelete(), jdbcProperties.isShowSql(), 
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(Long.valueOf(dumpMessaggio_multipartHeader.getId()),Long.class));
			}
		} // fine for dumpMessaggio_multipartHeader

		//Recupero oggetto _dumpMessaggio_headerTrasporto
		ISQLQueryObject sqlQueryObjectDelete_dumpMessaggio_headerTrasporto_getToDelete = sqlQueryObjectDelete.newSQLQueryObject();
		sqlQueryObjectDelete_dumpMessaggio_headerTrasporto_getToDelete.setANDLogicOperator(true);
		sqlQueryObjectDelete_dumpMessaggio_headerTrasporto_getToDelete.addFromTable(this.getDumpMessaggioFieldConverter().toTable(DumpMessaggio.model().HEADER_TRASPORTO));
		sqlQueryObjectDelete_dumpMessaggio_headerTrasporto_getToDelete.addWhereCondition("id_messaggio=?");
		java.util.List<Object> dumpMessaggio_headerTrasporto_toDelete_list = jdbcUtilities.executeQuery(sqlQueryObjectDelete_dumpMessaggio_headerTrasporto_getToDelete.createSQLQuery(), jdbcProperties.isShowSql(), DumpMessaggio.model().HEADER_TRASPORTO, this.getDumpMessaggioFetch(),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(Long.valueOf(id),Long.class));

		// for dumpMessaggio_headerTrasporto
		for (Object dumpMessaggio_headerTrasporto_object : dumpMessaggio_headerTrasporto_toDelete_list) {
			DumpHeaderTrasporto dumpMessaggio_headerTrasporto = (DumpHeaderTrasporto) dumpMessaggio_headerTrasporto_object;

			// Object dumpMessaggio_headerTrasporto
			ISQLQueryObject sqlQueryObjectDelete_dumpMessaggio_headerTrasporto = sqlQueryObjectDelete.newSQLQueryObject();
			sqlQueryObjectDelete_dumpMessaggio_headerTrasporto.setANDLogicOperator(true);
			sqlQueryObjectDelete_dumpMessaggio_headerTrasporto.addDeleteTable(this.getDumpMessaggioFieldConverter().toTable(DumpMessaggio.model().HEADER_TRASPORTO));
			sqlQueryObjectDelete_dumpMessaggio_headerTrasporto.addWhereCondition("id=?");

			// Delete dumpMessaggio_headerTrasporto
			if(dumpMessaggio_headerTrasporto != null){
				jdbcUtilities.execute(sqlQueryObjectDelete_dumpMessaggio_headerTrasporto.createSQLDelete(), jdbcProperties.isShowSql(), 
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(Long.valueOf(dumpMessaggio_headerTrasporto.getId()),Long.class));
			}
		} // fine for dumpMessaggio_headerTrasporto

		//Recupero oggetto _dumpMessaggio_allegato
		ISQLQueryObject sqlQueryObjectDelete_dumpMessaggio_allegato_getToDelete = sqlQueryObjectDelete.newSQLQueryObject();
		sqlQueryObjectDelete_dumpMessaggio_allegato_getToDelete.setANDLogicOperator(true);
		sqlQueryObjectDelete_dumpMessaggio_allegato_getToDelete.addFromTable(this.getDumpMessaggioFieldConverter().toTable(DumpMessaggio.model().ALLEGATO));
		sqlQueryObjectDelete_dumpMessaggio_allegato_getToDelete.addWhereCondition("id_messaggio=?");
		java.util.List<Object> dumpMessaggio_allegato_toDelete_list = jdbcUtilities.executeQuery(sqlQueryObjectDelete_dumpMessaggio_allegato_getToDelete.createSQLQuery(), jdbcProperties.isShowSql(), DumpMessaggio.model().ALLEGATO, this.getDumpMessaggioFetch(),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(Long.valueOf(id),Long.class));

		// for dumpMessaggio_allegato
		for (Object dumpMessaggio_allegato_object : dumpMessaggio_allegato_toDelete_list) {
			DumpAllegato dumpMessaggio_allegato = (DumpAllegato) dumpMessaggio_allegato_object;

			//Recupero oggetto _dumpMessaggio_allegato_header
			ISQLQueryObject sqlQueryObjectDelete_dumpMessaggio_allegato_dumpMessaggio_allegato_header_getToDelete = sqlQueryObjectDelete.newSQLQueryObject();
			sqlQueryObjectDelete_dumpMessaggio_allegato_dumpMessaggio_allegato_header_getToDelete.setANDLogicOperator(true);
			sqlQueryObjectDelete_dumpMessaggio_allegato_dumpMessaggio_allegato_header_getToDelete.addFromTable(this.getDumpMessaggioFieldConverter().toTable(DumpMessaggio.model().ALLEGATO.HEADER));
			sqlQueryObjectDelete_dumpMessaggio_allegato_dumpMessaggio_allegato_header_getToDelete.addWhereCondition("id_allegato=?");
			java.util.List<Object> dumpMessaggio_allegato_header_toDelete_list = jdbcUtilities.executeQuery(sqlQueryObjectDelete_dumpMessaggio_allegato_dumpMessaggio_allegato_header_getToDelete.createSQLQuery(), jdbcProperties.isShowSql(), DumpMessaggio.model().ALLEGATO.HEADER, this.getDumpMessaggioFetch(),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(Long.valueOf(dumpMessaggio_allegato.getId()),Long.class));

			// for dumpMessaggio_allegato_header
			for (Object dumpMessaggio_allegato_header_object : dumpMessaggio_allegato_header_toDelete_list) {
				DumpHeaderAllegato dumpMessaggio_allegato_header = (DumpHeaderAllegato) dumpMessaggio_allegato_header_object;

				// Object dumpMessaggio_allegato_header
				ISQLQueryObject sqlQueryObjectDelete_dumpMessaggio_allegato_header = sqlQueryObjectDelete.newSQLQueryObject();
				sqlQueryObjectDelete_dumpMessaggio_allegato_header.setANDLogicOperator(true);
				sqlQueryObjectDelete_dumpMessaggio_allegato_header.addDeleteTable(this.getDumpMessaggioFieldConverter().toTable(DumpMessaggio.model().ALLEGATO.HEADER));
				sqlQueryObjectDelete_dumpMessaggio_allegato_header.addWhereCondition("id=?");

				// Delete dumpMessaggio_allegato_header
				if(dumpMessaggio_allegato_header != null){
					jdbcUtilities.execute(sqlQueryObjectDelete_dumpMessaggio_allegato_header.createSQLDelete(), jdbcProperties.isShowSql(), 
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(Long.valueOf(dumpMessaggio_allegato_header.getId()),Long.class));
				}
			} // fine for dumpMessaggio_allegato_header

			// Object dumpMessaggio_allegato
			ISQLQueryObject sqlQueryObjectDelete_dumpMessaggio_allegato = sqlQueryObjectDelete.newSQLQueryObject();
			sqlQueryObjectDelete_dumpMessaggio_allegato.setANDLogicOperator(true);
			sqlQueryObjectDelete_dumpMessaggio_allegato.addDeleteTable(this.getDumpMessaggioFieldConverter().toTable(DumpMessaggio.model().ALLEGATO));
			sqlQueryObjectDelete_dumpMessaggio_allegato.addWhereCondition("id=?");

			// Delete dumpMessaggio_allegato
			if(dumpMessaggio_allegato != null){
				jdbcUtilities.execute(sqlQueryObjectDelete_dumpMessaggio_allegato.createSQLDelete(), jdbcProperties.isShowSql(), 
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(Long.valueOf(dumpMessaggio_allegato.getId()),Long.class));
			}
		} // fine for dumpMessaggio_allegato

		//Recupero oggetto _dumpMessaggio_contenuto
		ISQLQueryObject sqlQueryObjectDelete_dumpMessaggio_contenuto_getToDelete = sqlQueryObjectDelete.newSQLQueryObject();
		sqlQueryObjectDelete_dumpMessaggio_contenuto_getToDelete.setANDLogicOperator(true);
		sqlQueryObjectDelete_dumpMessaggio_contenuto_getToDelete.addFromTable(this.getDumpMessaggioFieldConverter().toTable(DumpMessaggio.model().CONTENUTO));
		sqlQueryObjectDelete_dumpMessaggio_contenuto_getToDelete.addWhereCondition("id_messaggio=?");
		java.util.List<Object> dumpMessaggio_contenuto_toDelete_list = jdbcUtilities.executeQuery(sqlQueryObjectDelete_dumpMessaggio_contenuto_getToDelete.createSQLQuery(), jdbcProperties.isShowSql(), DumpMessaggio.model().CONTENUTO, this.getDumpMessaggioFetch(),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(Long.valueOf(id),Long.class));

		// for dumpMessaggio_contenuto
		for (Object dumpMessaggio_contenuto_object : dumpMessaggio_contenuto_toDelete_list) {
			DumpContenuto dumpMessaggio_contenuto = (DumpContenuto) dumpMessaggio_contenuto_object;

			// Object dumpMessaggio_contenuto
			ISQLQueryObject sqlQueryObjectDelete_dumpMessaggio_contenuto = sqlQueryObjectDelete.newSQLQueryObject();
			sqlQueryObjectDelete_dumpMessaggio_contenuto.setANDLogicOperator(true);
			sqlQueryObjectDelete_dumpMessaggio_contenuto.addDeleteTable(this.getDumpMessaggioFieldConverter().toTable(DumpMessaggio.model().CONTENUTO));
			sqlQueryObjectDelete_dumpMessaggio_contenuto.addWhereCondition("id=?");

			// Delete dumpMessaggio_contenuto
			if(dumpMessaggio_contenuto != null){
				jdbcUtilities.execute(sqlQueryObjectDelete_dumpMessaggio_contenuto.createSQLDelete(), jdbcProperties.isShowSql(), 
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(Long.valueOf(dumpMessaggio_contenuto.getId()),Long.class));
			}
		} // fine for dumpMessaggio_contenuto

		// Object dumpMessaggio
		sqlQueryObjectDelete.setANDLogicOperator(true);
		sqlQueryObjectDelete.addDeleteTable(this.getDumpMessaggioFieldConverter().toTable(DumpMessaggio.model()));
		if(id != null)
			sqlQueryObjectDelete.addWhereCondition("id=?");

		// Delete dumpMessaggio
		jdbcUtilities.execute(sqlQueryObjectDelete.createSQLDelete(), jdbcProperties.isShowSql(), 
			new JDBCObject(id,Long.class));

	}

	@Override
	public void deleteById(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdDumpMessaggio idDumpMessaggio) throws NotImplementedException,ServiceException,Exception {

		Long id = null;
		try{
			id = this.findIdDumpMessaggio(jdbcProperties, log, connection, sqlQueryObject, idDumpMessaggio, true);
		}catch(NotFoundException notFound){
			return;
		}
		this._delete(jdbcProperties, log, connection, sqlQueryObject, id);
		
	}
	
	@Override
	public NonNegativeNumber deleteAll(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject) throws NotImplementedException,ServiceException,Exception {
		
		return this.deleteAll(jdbcProperties, log, connection, sqlQueryObject, new JDBCExpression(this.getDumpMessaggioFieldConverter()));

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
