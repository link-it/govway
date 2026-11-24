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
package org.openspcoop2.message.soap.reader;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.openspcoop2.message.MessageUtils;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.exception.SoapEnvelopeStructureException;
import org.openspcoop2.message.exception.SoapEnvelopeStructureIOException;
import org.openspcoop2.utils.Utilities;

/**
 * Stream che verifica la struttura dell'Envelope SOAP sui byte letti (vedi {@link SoapEnvelopeStructureStreamChecker}).
 *
 * All'individuazione di un'anomalia, i byte che la contengono non vengono restituiti: viene sollevata una
 * {@link SoapEnvelopeStructureIOException}, così che chi sta inoltrando il messaggio non consegni la parte non conforme.
 * Prima di sollevare l'eccezione:
 * - lo stream sottostante viene letto fino alla fine senza restituire i byte, affinché le registrazioni che lo osservano
 *   (es. dump del messaggio ricevuto) siano complete; la lettura resta soggetta ai limiti configurati (dimensione, timeout);
 * - l'anomalia viene registrata come errore di parsing del messaggio, per la gestione dell'errore e l'esito della transazione.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SoapEnvelopeStructureCheckInputStream extends FilterInputStream {

	private final SoapEnvelopeStructureStreamChecker checker;
	private final OpenSPCoop2Message message;
	private SoapEnvelopeStructureIOException error;
	private boolean ended = false;

	public SoapEnvelopeStructureCheckInputStream(InputStream in, SoapEnvelopeStructureStreamChecker checker, OpenSPCoop2Message message) {
		super(in);
		this.checker = checker;
		this.message = message;
	}

	public SoapEnvelopeStructureStreamChecker getChecker() {
		return this.checker;
	}

	@Override
	public int read() throws IOException {
		byte[] b = new byte[1];
		int r = read(b, 0, 1);
		return r==-1 ? -1 : (b[0] & 0xFF);
	}

	@Override
	public int read(byte[] b) throws IOException {
		return read(b, 0, b.length);
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		if(this.error!=null) {
			throw this.error;
		}
		if(this.ended) {
			return -1;
		}
		int r = this.in.read(b, off, len);
		try {
			if(r==-1) {
				this.ended = true;
				this.checker.end();
			}
			else if(r>0) {
				this.checker.update(b, off, r);
			}
		}catch(SoapEnvelopeStructureException e) {
			throw fail(e);
		}
		return r;
	}

	@Override
	public long skip(long n) throws IOException {
		// i byte saltati devono comunque essere verificati
		if(n<=0) {
			return 0;
		}
		byte[] buffer = new byte[(int) Math.min(n, Utilities.DIMENSIONE_BUFFER)];
		long skipped = 0;
		while(skipped<n) {
			int r = read(buffer, 0, (int) Math.min(buffer.length, n-skipped));
			if(r==-1) {
				break;
			}
			skipped += r;
		}
		return skipped;
	}

	@Override
	public boolean markSupported() {
		return false;
	}
	@Override
	public synchronized void mark(int readlimit) {
		// non supportato
	}
	@Override
	public synchronized void reset() throws IOException {
		throw new IOException("mark/reset not supported");
	}

	private SoapEnvelopeStructureIOException fail(SoapEnvelopeStructureException e) {
		drain();
		if(this.message!=null) {
			MessageUtils.registerParseException(this.message, e, true);
		}
		this.error = new SoapEnvelopeStructureIOException(e);
		return this.error;
	}

	private void drain() {
		byte[] buffer = new byte[Utilities.DIMENSIONE_BUFFER];
		try {
			while(this.in.read(buffer)!=-1) {
				// i byte letti non vengono restituiti
			}
		}catch(Exception e) {
			// l'anomalia della struttura resta l'errore principale
		}
		this.ended = true;
	}
}
