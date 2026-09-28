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

package org.openspcoop2.utils.transport.http;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Digest calcolato sui byte ricevuti (compressi) durante la decompressione di un contenuto.
 *
 * Il Digest HTTP (RFC 3230) si riferisce ai byte trasmessi, cioè al contenuto dopo l'applicazione del Content-Encoding.
 * Quando il contenuto viene decompresso in ricezione, i byte ricevuti non sono più disponibili al termine della lettura:
 * il digest viene quindi calcolato in stream, mentre il decoder li consuma, senza mantenerne una copia.
 *
 * Il valore è disponibile ({@link #isCompleted()}) solo se il contenuto decompresso è stato letto fino alla fine:
 * in tal caso vengono letti anche gli eventuali byte ricevuti non consumati dal decoder (es. trailer gzip),
 * in modo che il digest copra l'intero contenuto ricevuto.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ContentEncodingWireDigest {

	private static final String[] SUPPORTED_ALGORITHMS = {
			HttpConstants.DIGEST_ALGO_SHA_256, HttpConstants.DIGEST_ALGO_SHA_384, HttpConstants.DIGEST_ALGO_SHA_512 };

	/**
	 * Crea l'oggetto per il calcolo del digest, con l'algoritmo indicato nel valore dell'header Digest (es. 'SHA-256=...').
	 *
	 * @param digestHeaderValue valore dell'header Digest; in presenza di più valori viene considerato il primo
	 * @return l'oggetto per il calcolo del digest, o null se il valore è assente o l'algoritmo non è supportato
	 */
	public static ContentEncodingWireDigest newInstance(String digestHeaderValue) {
		if (digestHeaderValue == null) {
			return null;
		}
		String value = digestHeaderValue.trim();
		int comma = value.indexOf(',');
		if (comma >= 0) {
			value = value.substring(0, comma).trim();
		}
		int eq = value.indexOf('=');
		if (eq <= 0) {
			return null;
		}
		String algo = value.substring(0, eq).trim();
		for (String supported : SUPPORTED_ALGORITHMS) {
			if (supported.equalsIgnoreCase(algo)) {
				try {
					return new ContentEncodingWireDigest(supported, MessageDigest.getInstance(supported));
				} catch (NoSuchAlgorithmException e) {
					return null;
				}
			}
		}
		return null;
	}

	private final String algorithm;
	private final MessageDigest messageDigest;
	private String contentEncoding;
	private byte[] digest;

	private ContentEncodingWireDigest(String algorithm, MessageDigest messageDigest) {
		this.algorithm = algorithm;
		this.messageDigest = messageDigest;
	}

	/** Algoritmo nel formato dell'header Digest (es. 'SHA-256'). */
	public String getAlgorithm() {
		return this.algorithm;
	}

	/** Valore dell'header Content-Encoding dei byte ricevuti (rimosso dal messaggio in seguito alla decompressione). */
	public String getContentEncoding() {
		return this.contentEncoding;
	}

	/** true se il contenuto è stato letto fino alla fine e il digest copre tutti i byte ricevuti. */
	public boolean isCompleted() {
		return this.digest != null;
	}

	/** Digest dei byte ricevuti, o null se non completato ({@link #isCompleted()}). */
	public byte[] getDigest() {
		return this.digest != null ? this.digest.clone() : null;
	}

	/** Stream dei byte ricevuti, da passare al decoder: ogni byte letto aggiorna il digest. */
	InputStream wrapReceived(InputStream received, String contentEncoding) {
		this.contentEncoding = contentEncoding;
		return new DigestInputStream(received, this.messageDigest);
	}

	/** Stream decompresso restituito al chiamante: al raggiungimento della fine completa il digest. */
	InputStream wrapDecoded(InputStream decoded, InputStream receivedWrapped) {
		return new CompletingInputStream(decoded, receivedWrapped);
	}

	private void complete(InputStream receivedWrapped) throws IOException {
		if (this.digest != null) {
			return;
		}
		// byte ricevuti non consumati dal decoder
		byte[] buffer = new byte[1024];
		while (receivedWrapped.read(buffer) >= 0) {
			// il DigestInputStream aggiorna il digest
		}
		this.digest = this.messageDigest.digest();
	}

	private class CompletingInputStream extends FilterInputStream {

		private final InputStream receivedWrapped;

		CompletingInputStream(InputStream decoded, InputStream receivedWrapped) {
			super(decoded);
			this.receivedWrapped = receivedWrapped;
		}

		@Override
		public int read() throws IOException {
			int b = super.read();
			if (b < 0) {
				complete(this.receivedWrapped);
			}
			return b;
		}

		@Override
		public int read(byte[] b, int off, int len) throws IOException {
			int n = super.read(b, off, len);
			if (n < 0) {
				complete(this.receivedWrapped);
			}
			return n;
		}
	}
}
