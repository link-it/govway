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

import java.io.IOException;
import java.io.InputStream;

/**
 * Reader del transport streaming del provider: estrae i chunk grezzi dal wire
 * framing (SSE/NDJSON/AWS event-stream) senza interpretarli semanticamente.
 * <p>
 * Implementazioni:
 * </p>
 * <ul>
 *   <li>{@code SseStreamReader}: testo, framing {@code \n\n}</li>
 *   <li>{@code NdjsonStreamReader}: testo, framing {@code \n}</li>
 *   <li>{@code AwsEventStreamReader}: binario, prelude/headers/payload/CRC</li>
 * </ul>
 *
 * @author Andrea Poli (apoli@link.it)
 */
public interface LLMProviderStreamReader {

	/**
	 * Transport supportato dall'implementazione.
	 */
	LLMProviderStreamTransport getTransport();

	/**
	 * Inizializza il reader sull'InputStream sorgente. Il reader può tenere uno
	 * stato interno (es. buffer linee per SSE). Da invocare una sola volta prima
	 * di chiamare {@link #readNextChunk()}.
	 */
	void bind(InputStream source);

	/**
	 * Legge il prossimo chunk del transport (blocca finché non disponibile o EOF).
	 * Ritorna {@code null} a EOF dello stream sorgente, oppure il prossimo
	 * {@link LLMProviderRawChunk} disponibile.
	 */
	LLMProviderRawChunk readNextChunk() throws IOException;
}
