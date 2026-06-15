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
package org.openspcoop2.message.llm.stream;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.openspcoop2.message.llm.transform.LLMInboundProviderChunkDecoder;
import org.openspcoop2.message.llm.transform.LLMOutboundFrontDoorChunkEncoder;
import org.openspcoop2.message.llm.transform.LLMTransformException;

/**
 * InputStream che traduce on-the-fly i chunk di una response streaming dal
 * dialect/transport del provider al dialect SSE del front-door, componendo
 * {@link LLMProviderStreamReader} → {@link LLMInboundProviderChunkDecoder} →
 * {@link LLMOutboundFrontDoorChunkEncoder}.
 * <p>
 * Pattern read-pull: il chiamante chiede bytes con {@link #read(byte[], int, int)};
 * il wrapper legge un chunk grezzo dal reader, lo passa al decoder ottenendo zero
 * o più eventi canonical, l'encoder produce i bytes nel dialect front-door che
 * vanno in un buffer interno e vengono restituiti man mano. Al raggiungimento
 * di EOF dello stream sorgente viene emesso (una volta) il terminator
 * dell'encoder (es. {@code data: [DONE]\n\n} per OpenAI).
 * </p>
 *
 * @author Andrea Poli (apoli@link.it)
 */
public class ChunkTransformInputStream extends FilterInputStream {

	private final LLMProviderStreamReader reader;
	private final LLMInboundProviderChunkDecoder decoder;
	private final LLMOutboundFrontDoorChunkEncoder encoder;
	/** Observer opzionale invocato ogni volta che il decoder emette un evento con usage
	 *  (es. message_start con input_tokens, message_delta con output_tokens). Permette al
	 *  livello applicativo di accumulare il totale per la transazione (vedi
	 *  {@code LLMHandlerSupport.accumulateLLMStreamUsage}). Puo' essere null. */
	private final java.util.function.Consumer<org.openspcoop2.message.llm.CanonicalUsage> usageObserver;

	/** Buffer di byte pronti per il consumer (output del transformer). */
	private byte[] outBuffer;
	private int outOffset;

	private boolean upstreamEof;
	private boolean terminatorEmitted;

	public ChunkTransformInputStream(InputStream source,
			LLMProviderStreamReader reader,
			LLMInboundProviderChunkDecoder decoder,
			LLMOutboundFrontDoorChunkEncoder encoder) {
		this(source, reader, decoder, encoder, null);
	}

	public ChunkTransformInputStream(InputStream source,
			LLMProviderStreamReader reader,
			LLMInboundProviderChunkDecoder decoder,
			LLMOutboundFrontDoorChunkEncoder encoder,
			java.util.function.Consumer<org.openspcoop2.message.llm.CanonicalUsage> usageObserver) {
		super(source);
		this.reader = reader;
		this.decoder = decoder;
		this.encoder = encoder;
		this.usageObserver = usageObserver;
		this.reader.bind(source);
	}

	@Override
	public int read() throws IOException {
		byte[] one = new byte[1];
		int n = read(one, 0, 1);
		return n <= 0 ? -1 : (one[0] & 0xff);
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		if (b == null) {
			throw new NullPointerException();
		}
		if (off < 0 || len < 0 || len > b.length - off) {
			throw new IndexOutOfBoundsException();
		}
		if (len == 0) {
			return 0;
		}
		while (!hasOutputAvailable()) {
			if (this.upstreamEof) {
				if (emitTerminatorIfPending()) {
					break;
				}
				return -1;
			}
			pumpNextChunk();
		}
		return drainTo(b, off, len);
	}

	@Override
	public void close() throws IOException {
		// chiude lo stream sorgente; il reader non possiede risorse oltre al sorgente.
		super.close();
	}


	/* === pipeline interna === */

	private boolean hasOutputAvailable() {
		return this.outBuffer != null && this.outOffset < this.outBuffer.length;
	}

	private int drainTo(byte[] dst, int off, int len) {
		int available = this.outBuffer.length - this.outOffset;
		int toCopy = Math.min(available, len);
		System.arraycopy(this.outBuffer, this.outOffset, dst, off, toCopy);
		this.outOffset += toCopy;
		if (this.outOffset >= this.outBuffer.length) {
			this.outBuffer = null;
			this.outOffset = 0;
		}
		return toCopy;
	}

	private void pumpNextChunk() throws IOException {
		LLMProviderRawChunk raw = this.reader.readNextChunk();
		if (raw == null) {
			this.upstreamEof = true;
			return;
		}
		try {
			List<CanonicalStreamEvent> events = this.decoder.decode(raw);
			if (events == null || events.isEmpty()) {
				return; // chunk semanticamente innocuo: salta
			}
			notifyUsage(events);
			byte[] out = encodeAll(events);
			if (out.length > 0) {
				this.outBuffer = out;
				this.outOffset = 0;
			}
		} catch (LLMTransformException e) {
			throw new IOException("Errore nella trasformazione chunk SSE LLM: " + e.getMessage(), e);
		}
	}

	private void notifyUsage(List<CanonicalStreamEvent> events) {
		if (this.usageObserver == null) return;
		for (CanonicalStreamEvent ev : events) {
			org.openspcoop2.message.llm.CanonicalUsage usage = null;
			if (ev instanceof CanonicalStreamMessageStart) {
				usage = ((CanonicalStreamMessageStart) ev).getUsage();
			} else if (ev instanceof CanonicalStreamMessageDelta) {
				usage = ((CanonicalStreamMessageDelta) ev).getUsage();
			}
			if (usage != null) {
				this.usageObserver.accept(usage);
			}
		}
	}

	private byte[] encodeAll(List<CanonicalStreamEvent> events) throws LLMTransformException {
		// Concatena gli output di più eventi canonical in un singolo blob da consegnare.
		// Tipicamente è 1 chunk per evento, ma alcuni eventi possono produrre 0 bytes.
		int total = 0;
		byte[][] parts = new byte[events.size()][];
		for (int i = 0; i < events.size(); i++) {
			byte[] enc = this.encoder.encode(events.get(i));
			parts[i] = enc != null ? enc : new byte[0];
			total += parts[i].length;
		}
		if (total == 0) {
			return new byte[0];
		}
		byte[] out = new byte[total];
		int pos = 0;
		for (byte[] part : parts) {
			if (part.length > 0) {
				System.arraycopy(part, 0, out, pos, part.length);
				pos += part.length;
			}
		}
		return out;
	}

	private boolean emitTerminatorIfPending() {
		if (this.terminatorEmitted) {
			return false;
		}
		this.terminatorEmitted = true;
		byte[] term = this.encoder.terminator();
		if (term == null || term.length == 0) {
			return false;
		}
		this.outBuffer = term;
		this.outOffset = 0;
		return true;
	}
}
