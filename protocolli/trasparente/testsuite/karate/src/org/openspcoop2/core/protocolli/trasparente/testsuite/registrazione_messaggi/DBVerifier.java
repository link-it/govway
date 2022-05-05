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

package org.openspcoop2.core.protocolli.trasparente.testsuite.registrazione_messaggi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;

import org.openspcoop2.core.protocolli.trasparente.testsuite.Bodies;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.DbUtils;
import org.openspcoop2.core.transazioni.constants.TipoMessaggio;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.transport.http.ContentTypeUtilities;
import org.slf4j.Logger;

/**
* DBVerifier
*
* @author Francesco Scarlato (scarlato@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class DBVerifier {
	
	private static DbUtils dbUtils() {
		return ConfigLoader.getDbUtils();
	}
	
	private static Logger log() {
		return ConfigLoader.getLoggerRegistrazioneMessaggi();
	}
	
	public static void verify(String idTransazione, 
			boolean richiestaIngressoHeader, boolean richiestaIngressoBody,
			boolean richiestaUscitaHeader, boolean richiestaUscitaBody,
			boolean rispostaIngressoHeader, boolean rispostaIngressoBody,
			boolean rispostaUscitaHeader, boolean rispostaUscitaBody,
			boolean multipartParsing, int numAttachments,
			String contentType, long contentLength, String formatoMessaggio) throws Exception  {
		
		// La scrittura su database avviene dopo aver risposto al client
		
		Utilities.sleep(100); 
		try {
			DBVerifier._verify(idTransazione, 
					richiestaIngressoHeader, richiestaIngressoBody,
					richiestaUscitaHeader, richiestaUscitaBody,
					rispostaIngressoHeader, rispostaIngressoBody,
					rispostaUscitaHeader, rispostaUscitaBody,
					multipartParsing, numAttachments,
					contentType, contentLength, formatoMessaggio);
		}catch(Throwable t) {
			Utilities.sleep(500);
			try {
				DBVerifier._verify(idTransazione, 
						richiestaIngressoHeader, richiestaIngressoBody,
						richiestaUscitaHeader, richiestaUscitaBody,
						rispostaIngressoHeader, rispostaIngressoBody,
						rispostaUscitaHeader, rispostaUscitaBody,
						multipartParsing, numAttachments,
						contentType, contentLength, formatoMessaggio);
			}catch(Throwable t2) {
				Utilities.sleep(2000);
				try {
					DBVerifier._verify(idTransazione, 
							richiestaIngressoHeader, richiestaIngressoBody,
							richiestaUscitaHeader, richiestaUscitaBody,
							rispostaIngressoHeader, rispostaIngressoBody,
							rispostaUscitaHeader, rispostaUscitaBody,
							multipartParsing, numAttachments,
							contentType, contentLength, formatoMessaggio);
				}catch(Throwable t3) {
					Utilities.sleep(5000);
					try {
						DBVerifier._verify(idTransazione, 
								richiestaIngressoHeader, richiestaIngressoBody,
								richiestaUscitaHeader, richiestaUscitaBody,
								rispostaIngressoHeader, rispostaIngressoBody,
								rispostaUscitaHeader, rispostaUscitaBody,
								multipartParsing, numAttachments,
								contentType, contentLength, formatoMessaggio);
					}catch(Throwable t4) {
						if(contentLength<Bodies.BIG_SIZE) {
							throw t4;
						}
						else {
							Utilities.sleep(30000); // per jenkins
							DBVerifier._verify(idTransazione, 
									richiestaIngressoHeader, richiestaIngressoBody,
									richiestaUscitaHeader, richiestaUscitaBody,
									rispostaIngressoHeader, rispostaIngressoBody,
									rispostaUscitaHeader, rispostaUscitaBody,
									multipartParsing, numAttachments,
									contentType, contentLength, formatoMessaggio);
						}
					}
				}
			}
		}
	}
	
	public static void _verify(String idTransazione, 
			boolean richiestaIngressoHeader, boolean richiestaIngressoBody,
			boolean richiestaUscitaHeader, boolean richiestaUscitaBody,
			boolean rispostaIngressoHeader, boolean rispostaIngressoBody,
			boolean rispostaUscitaHeader, boolean rispostaUscitaBody,
			boolean multipartParsing, int numAttachments,
			String contentType, long contentLength, String formatoMessaggio) throws Exception  {
		
		int expectedDumpMessages = 0;
		if(richiestaIngressoHeader || richiestaIngressoBody) {
			expectedDumpMessages++;
		}
		if(richiestaUscitaHeader || richiestaUscitaBody) {
			expectedDumpMessages++;
		}
		if(rispostaIngressoHeader || rispostaIngressoBody) {
			expectedDumpMessages++;
		}
		if(rispostaUscitaHeader || rispostaUscitaBody) {
			expectedDumpMessages++;
		}
		
		// numero di messaggi
		
		String query = "select count(*) from dump_messaggi where id_transazione = ?";
		log().info(query);
		
		int count = dbUtils().readValue(query, Integer.class, idTransazione);
		assertEquals("IdTransazione: "+idTransazione, expectedDumpMessages, count);
		
		// verifica singolo messaggio
		
		verify_message(idTransazione, multipartParsing ? TipoMessaggio.RICHIESTA_INGRESSO : TipoMessaggio.RICHIESTA_INGRESSO_DUMP_BINARIO,
				richiestaIngressoHeader, richiestaIngressoBody,
				multipartParsing, numAttachments,
				contentType, contentLength, formatoMessaggio);
		verify_message(idTransazione, multipartParsing ? TipoMessaggio.RICHIESTA_USCITA : TipoMessaggio.RICHIESTA_USCITA_DUMP_BINARIO,
				richiestaUscitaHeader, richiestaUscitaBody,
				multipartParsing, numAttachments,
				contentType, contentLength, formatoMessaggio);

		verify_message(idTransazione, multipartParsing ? TipoMessaggio.RISPOSTA_INGRESSO : TipoMessaggio.RISPOSTA_INGRESSO_DUMP_BINARIO,
				rispostaIngressoHeader, rispostaIngressoBody,
				multipartParsing, numAttachments,
				contentType, contentLength, formatoMessaggio);
		verify_message(idTransazione, multipartParsing ? TipoMessaggio.RISPOSTA_USCITA : TipoMessaggio.RISPOSTA_USCITA_DUMP_BINARIO,
				rispostaUscitaHeader, rispostaUscitaBody,
				multipartParsing, numAttachments,
				contentType, contentLength, formatoMessaggio);
		
	}
	
	private static void verify_message(String idTransazione, TipoMessaggio tipoMessaggio,
			boolean header, boolean body,
			boolean multipartParsing, int numAttachmentsExpected,
			String contentTypeExpected, long contentLengthExpected, String formatoMessaggioExpected) throws Exception {
	
		
		String query = "select id,tipo_messaggio,formato_messaggio,content_type,content_length,header_ext from dump_messaggi where id_transazione = ? and tipo_messaggio=?";
		if(body) {
			query = query+" AND body is not null";
		}
		else {
			query = query+" AND body is null";
		}
		log().info(query);
		
		String msg = "IdTransazione: "+idTransazione+" tipoMessaggio: "+tipoMessaggio.getValue();
		
		List<Map<String, Object>> rows = dbUtils().readRows(query, idTransazione, tipoMessaggio.getValue());
		if(!header && !body) {
			if(rows!=null) {
				assertEquals(msg, 0, rows.size());
			}
			return;
		}
		
		
		assertNotNull(msg, rows);
		assertEquals(msg, 1, rows.size());
		
		Long idMessaggio = null;
		String contentType = null;
		Long contentLenght = -1l;
		String formatoMessaggio = null;
		String headerExt = null;
		Map<String, Object> row = rows.get(0);
		for (String key : row.keySet()) {
			log().debug("Row["+key+"]="+row.get(key));
		}
		
		Object oIdMessaggio = row.get("id");
		assertNotNull(msg, oIdMessaggio);
		assertTrue(msg+" objectId classe '"+oIdMessaggio.getClass().getName()+"'", (oIdMessaggio instanceof Integer || oIdMessaggio instanceof Long));
		if(oIdMessaggio instanceof Integer) {
			idMessaggio = Long.valueOf( (Integer)oIdMessaggio );
		}
		else {
			idMessaggio = (Long)oIdMessaggio;
		}
		
		
		contentType = (String) row.get("content_type");
		if(body) {
			assertNotNull(msg,contentType);
			if(numAttachmentsExpected==0) {
				assertEquals(msg,contentTypeExpected, contentType);
			}
			else {
				assertTrue(msg+" Verifico content-type '"+contentType+"' sia multipart", ContentTypeUtilities.isMultipartType(contentType));
			}
		}
		else {
			assertTrue(msg+" Verifico che il content-type '"+contentType+"' sia null",contentType==null);
		}
		
		if(contentLengthExpected>0) {
			Object oContentLength = row.get("content_length");
			if(body) {
				assertNotNull(msg,oContentLength);
				assertTrue(msg+" objectContentLength classe '"+oContentLength.getClass().getName()+"'", (oContentLength instanceof Integer || oContentLength instanceof Long));
				if(oContentLength instanceof Integer) {
					contentLenght = Long.valueOf( (Integer)oContentLength );
				}
				else {
					contentLenght = (Long)oContentLength;
				}
				assertEquals(msg,contentLengthExpected, contentLenght.longValue());
			}
			else {
				assertTrue(msg+" Verifico che content-length '"+oContentLength+"' sia null",oContentLength==null);
			}
		}
		
		formatoMessaggio = (String) row.get("formato_messaggio");
		assertNotNull(msg,formatoMessaggio);
		assertEquals(msg,formatoMessaggioExpected, formatoMessaggio);
		
		
		// numero di allegati
		
		query = "select count(*) from dump_allegati where id_messaggio = ?";
		log().info(query);
		
		int count = dbUtils().readValue(query, Integer.class, idMessaggio);
		assertEquals(msg,multipartParsing?numAttachmentsExpected : 0, count);
		
		
		// header http
		
		query = "select count(*) from dump_header_trasporto where id_messaggio = ?";
		log().info(query);
		
		int countHeaders = dbUtils().readValue(query, Integer.class, idMessaggio);
		headerExt = (String) row.get("header_ext");
		boolean compactHeaders = headerExt!=null && headerExt.length()>0;
		String message = msg+" Headers trovati tabella:"+countHeaders+" compact:"+compactHeaders;
		log().info(message);
		if(header) {
			assertTrue(message, (countHeaders>0 || compactHeaders));
		}
		else {
			assertTrue(message, (countHeaders==0 && !compactHeaders));
		}

	}
}
