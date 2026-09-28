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

/**
 * Eccezione sollevata quando la copia decompressa di un contenuto ricevuto compresso (Content-Encoding) non può essere
 * prodotta; il motivo è indicato da {@link Motivo}.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class MessageContentDecompressionException extends MessageException {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	public enum Motivo {
		/** encoding diverso da gzip, x-gzip, deflate (es. br, zstd, codifiche multiple) */
		UNSUPPORTED_ENCODING,
		/** byte non validi per l'encoding dichiarato */
		DECOMPRESSION_FAILED,
		/** dimensione decompressa superiore alla soglia */
		THRESHOLD_EXCEEDED
	}

	private final Motivo motivo;
	private final String contentEncoding;
	private final long maxBytes;

	public MessageContentDecompressionException(Motivo motivo, String contentEncoding, String msg) {
		this(motivo, contentEncoding, -1, msg);
	}
	public MessageContentDecompressionException(Motivo motivo, String contentEncoding, long maxBytes, String msg) {
		super(msg);
		this.motivo = motivo;
		this.contentEncoding = contentEncoding;
		this.maxBytes = maxBytes;
	}
	public MessageContentDecompressionException(Motivo motivo, String contentEncoding, String msg, Throwable cause) {
		super(msg, cause);
		this.motivo = motivo;
		this.contentEncoding = contentEncoding;
		this.maxBytes = -1;
	}

	public Motivo getMotivo() {
		return this.motivo;
	}
	public String getContentEncoding() {
		return this.contentEncoding;
	}
	/** soglia superata (in byte) per il motivo {@link Motivo#THRESHOLD_EXCEEDED}, -1 altrimenti */
	public long getMaxBytes() {
		return this.maxBytes;
	}
}
