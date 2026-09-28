/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2026 Link.it srl (https://link.it). 
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

package org.openspcoop2.message.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2RestJsonMessage;
import org.openspcoop2.message.OpenSPCoop2RestXmlMessage;
import org.openspcoop2.message.exception.MessageContentDecompressionException;
import org.openspcoop2.message.exception.MessageContentDecompressionException.Motivo;
import org.openspcoop2.message.exception.MessageException;
import org.openspcoop2.message.exception.MessageNotSupportedException;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.rest.problem.JsonDeserializer;
import org.openspcoop2.utils.rest.problem.ProblemRFC7807;
import org.openspcoop2.utils.rest.problem.XmlDeserializer;
import org.openspcoop2.utils.transport.http.ContentEncodingDecoder;

/**
 * Accesso ai contenuti ricevuti compressi (Content-Encoding) e non decompressi, per le funzionalità che devono
 * interpretarli senza modificare il messaggio (es. Problem Details, fault registrati): viene prodotta una copia
 * decompressa a partire dai byte così come ricevuti (accesso binario); il messaggio inoltrato non viene modificato.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class CompressedContentUtilities {
	
	private CompressedContentUtilities() {}

	/** Valore di soglia che indica l'assenza di un limite sulla dimensione decompressa */
	public static final long NO_THRESHOLD = -1;
	
	/**
	 * Copia decompressa del contenuto.
	 * 
	 * @param msg messaggio
	 * @param maxBytes dimensione massima della copia decompressa ({@link #NO_THRESHOLD} per nessun limite)
	 * @return i byte decompressi, null se il contenuto non è compresso
	 * @throws MessageContentDecompressionException se l'encoding non è supportato, la decompressione fallisce o la soglia viene superata
	 * @throws MessageException errore nell'accesso ai byte del messaggio
	 */
	public static byte[] decompress(OpenSPCoop2Message msg, long maxBytes) throws MessageException {
		String contentEncoding = msg.getContentEncodingCompressed();
		byte[] bNull = null;
		if(contentEncoding==null) {
			return bNull;
		}
		if(!ContentEncodingDecoder.isSupported(contentEncoding)) {
			throw new MessageContentDecompressionException(Motivo.UNSUPPORTED_ENCODING, contentEncoding, 
					"Content-Encoding '"+contentEncoding+"' not supported (supported encodings: "+ContentEncodingDecoder.SUPPORTED_DECOMPRESS_LIST+")");
		}
		ByteArrayOutputStream wire = new ByteArrayOutputStream();
		msg.writeTo(wire, false); // accesso binario: byte così come ricevuti
		ByteArrayOutputStream decompressed = new ByteArrayOutputStream();
		try(InputStream is = ContentEncodingDecoder.decode(new ByteArrayInputStream(wire.toByteArray()), contentEncoding)){
			byte[] buffer = new byte[8192];
			long total = 0;
			int letti;
			while((letti = is.read(buffer))!=-1) {
				total = total + letti;
				if(maxBytes>=0 && total>maxBytes) {
					// interruzione a soglia+1: la copia decompressa non supera mai la soglia (zip bomb)
					throw new MessageContentDecompressionException(Motivo.THRESHOLD_EXCEEDED, contentEncoding, maxBytes,
							"Decompressed content (Content-Encoding: "+contentEncoding+") exceeds the threshold of "+maxBytes+" bytes");
				}
				decompressed.write(buffer, 0, letti);
			}
		}catch(MessageContentDecompressionException e) {
			throw e;
		}catch(Exception e) {
			throw new MessageContentDecompressionException(Motivo.DECOMPRESSION_FAILED, contentEncoding, 
					"Decompression (Content-Encoding: "+contentEncoding+") failed: "+e.getMessage(), e);
		}
		return decompressed.toByteArray();
	}
	
	/**
	 * Byte in chiaro di un contenuto (es. fault o Problem Details) da riportare in diagnostici o registrare nella transazione.
	 * 
	 * @param msg messaggio
	 * @param maxBytes dimensione massima della copia decompressa ({@link #NO_THRESHOLD} per nessun limite)
	 * @return i byte del contenuto (la copia decompressa se ricevuto compresso), null se il contenuto è compresso e non è
	 *         possibile decomprimerlo (encoding non supportato, decompressione fallita, soglia superata)
	 * @throws MessageException errore nell'accesso ai byte del messaggio
	 */
	public static byte[] getContentForFault(OpenSPCoop2Message msg, long maxBytes) throws MessageException {
		if(msg.getContentEncodingCompressed()==null) {
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			msg.writeTo(bout, false);
			return bout.toByteArray();
		}
		byte [] bNull = null;
		try {
			return decompress(msg, maxBytes);
		}catch(MessageContentDecompressionException e) {
			return bNull;
		}
	}
	
	/**
	 * Interpretazione di un Problem Details (RFC 7807) JSON o XML, in chiaro o compresso.
	 * 
	 * @param msg messaggio (REST JSON o XML)
	 * @param maxBytes dimensione massima della copia decompressa ({@link #NO_THRESHOLD} per nessun limite)
	 * @return il problem, null se il messaggio non è JSON o XML
	 * @throws MessageContentDecompressionException se il problem è compresso e non è possibile decomprimerlo
	 * @throws UtilsException se il contenuto non è un Problem Details valido
	 */
	public static ProblemRFC7807 parseProblemRFC7807(OpenSPCoop2Message msg, long maxBytes) throws MessageException, MessageNotSupportedException, UtilsException {
		byte[] decompressed = decompress(msg, maxBytes);
		if(msg instanceof OpenSPCoop2RestJsonMessage) {
			JsonDeserializer deserializer = new JsonDeserializer();
			if(decompressed!=null) {
				return deserializer.fromByteArray(decompressed, false);
			}
			return deserializer.fromString(msg.castAsRestJson().getContent(), false);
		}
		else if(msg instanceof OpenSPCoop2RestXmlMessage) {
			XmlDeserializer deserializer = new XmlDeserializer();
			if(decompressed!=null) {
				return deserializer.fromByteArray(decompressed, false);
			}
			return deserializer.fromNode(msg.castAsRestXml().getContent(), false);
		}
		return null;
	}
}
