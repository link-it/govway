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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Reader del transport SSE (Server-Sent Events): testo UTF-8, eventi separati
 * da riga vuota ({@code \n\n}). Estrae i chunk grezzi parsando i campi
 * {@code event:} e {@code data:} di ciascun evento, ignora {@code id:},
 * {@code retry:} e commenti {@code :...} (non rilevanti per i flussi LLM).
 * <p>
 * Più righe {@code data: ...} consecutive vengono concatenate con {@code \n}
 * come da spec SSE.
 * </p>
 *
 * @author Andrea Poli (apoli@link.it)
 */
public class SseStreamReader implements LLMProviderStreamReader {

	private static final String FIELD_EVENT = "event:";
	private static final String FIELD_DATA = "data:";

	private BufferedReader reader;

	@Override
	public LLMProviderStreamTransport getTransport() {
		return LLMProviderStreamTransport.SSE;
	}

	@Override
	public void bind(InputStream source) {
		this.reader = new BufferedReader(new InputStreamReader(source, StandardCharsets.UTF_8));
	}

	@Override
	public LLMProviderRawChunk readNextChunk() throws IOException {
		if (this.reader == null) {
			throw new IOException("SseStreamReader non inizializzato: chiamare bind() prima");
		}
		String eventType = null;
		StringBuilder data = null;
		String line;
		while ((line = this.reader.readLine()) != null) {
			if (line.isEmpty()) {
				// fine evento: emetti se c'è almeno un campo significativo
				if (eventType != null || data != null) {
					return new LLMProviderRawChunk(eventType, data != null ? data.toString() : null);
				}
				// linea vuota in mezzo a niente: ignora
				continue;
			}
			if (line.startsWith(":")) {
				// commento SSE: ignora
				continue;
			}
			if (line.startsWith(FIELD_EVENT)) {
				eventType = stripFieldValue(line, FIELD_EVENT.length());
			} else if (line.startsWith(FIELD_DATA)) {
				String value = stripFieldValue(line, FIELD_DATA.length());
				if (data == null) {
					data = new StringBuilder(value);
				} else {
					data.append('\n').append(value);
				}
			}
			// id:, retry: e altri campi non rilevanti per i flussi LLM: ignora
		}
		// EOF: se c'è un evento residuo non terminato da riga vuota, emettilo
		if (eventType != null || data != null) {
			return new LLMProviderRawChunk(eventType, data != null ? data.toString() : null);
		}
		return null;
	}

	/**
	 * Rimuove il prefisso del field-name e lo space opzionale dopo i due punti
	 * (es. {@code "event: foo"} → {@code "foo"}, {@code "data:bar"} → {@code "bar"}).
	 */
	private static String stripFieldValue(String line, int prefixLen) {
		String value = line.substring(prefixLen);
		if (!value.isEmpty() && value.charAt(0) == ' ') {
			value = value.substring(1);
		}
		return value;
	}
}
