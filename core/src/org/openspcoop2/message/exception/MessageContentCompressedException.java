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

package org.openspcoop2.message.exception;

import org.openspcoop2.message.constants.Costanti;
import org.openspcoop2.message.constants.MessageRole;
import org.openspcoop2.utils.transport.http.HttpConstants;

/**
 * Eccezione sollevata quando una funzionalità richiede di interpretare il contenuto di un messaggio
 * ricevuto compresso (header 'Content-Encoding' diverso da 'identity') senza che sia stata abilitata la
 * decompressione. Il messaggio è comunque inoltrabile così come ricevuto: l'accesso binario ai byte
 * (writeTo, getInputStream) non è soggetto a questa eccezione.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class MessageContentCompressedException extends MessageException {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	private final String contentEncoding;
	private final boolean request;

	public MessageContentCompressedException(String contentEncoding, MessageRole role, boolean soap) {
		super(buildMessage(contentEncoding, isRequest(role), soap));
		this.contentEncoding = contentEncoding;
		this.request = isRequest(role);
	}

	public String getContentEncoding() {
		return this.contentEncoding;
	}
	public boolean isRequest() {
		return this.request;
	}

	private static boolean isRequest(MessageRole role) {
		return MessageRole.REQUEST.equals(role);
	}

	private static String buildMessage(String contentEncoding, boolean request, boolean soap) {
		String tipo = request ? "request" : "response";
		String proprieta = request ? Costanti.PROPERTY_CONTENT_ENCODING_REQUEST_DECOMPRESS : Costanti.PROPERTY_CONTENT_ENCODING_RESPONSE_DECOMPRESS;
		StringBuilder sb = new StringBuilder();
		sb.append("The ");
		if(soap) {
			sb.append("SOAP ");
		}
		sb.append(tipo).append(" message content is not accessible because it is compressed (Content-Encoding: ").append(contentEncoding).append("): ");
		if(soap) {
			sb.append("SOAP messages are always processed by the gateway; ");
		}
		else {
			sb.append("to use features that require access to the content ");
		}
		sb.append("enable decompression of the ").append(tipo).append(" (property '").append(proprieta).append("')");
		return sb.toString();
	}

	/**
	 * Ritorna il valore dell'header 'Content-Encoding' se indica un contenuto compresso, null altrimenti
	 * (header assente, vuoto o 'identity').
	 *
	 * @param contentEncodingHeader valore dell'header (valori multipli già compattati)
	 * @return il Content-Encoding normalizzato (trim) o null
	 */
	public static String getCompressedContentEncoding(String contentEncodingHeader) {
		if(contentEncodingHeader==null) {
			return null;
		}
		String ce = contentEncodingHeader.trim();
		if(ce.isEmpty() || HttpConstants.CONTENT_ENCODING_VALUE_IDENTITY.equalsIgnoreCase(ce)) {
			return null;
		}
		return ce;
	}
}
