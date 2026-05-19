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

/**
 * Chunk "grezzo" emesso da un {@link LLMProviderStreamReader}: il singolo evento
 * del transport protocol (SSE event, NDJSON line, AWS event-stream record),
 * estratto dal wire framing ma non ancora interpretato semanticamente.
 * <p>
 * Campi tipici per i tre transport:
 * </p>
 * <table border="1">
 *   <tr><th>Transport</th><th>eventType</th><th>dataPayload</th></tr>
 *   <tr><td>SSE</td>
 *       <td>valore del campo {@code event:} (es. "message_start"), null se assente</td>
 *       <td>valore del campo {@code data:} (di solito JSON)</td></tr>
 *   <tr><td>NDJSON</td>
 *       <td>null (sempre)</td>
 *       <td>la singola linea JSON</td></tr>
 *   <tr><td>AWS event-stream</td>
 *       <td>valore dell'header {@code :event-type}</td>
 *       <td>payload, già base64-decodificato se il container Bedrock lo wrappava</td></tr>
 * </table>
 * <p>
 * Il decoder semantico ({@link LLMInboundProviderChunkDecoder}) consuma il chunk
 * grezzo e produce zero o più {@link CanonicalStreamEvent}.
 * </p>
 *
 * @author Andrea Poli (apoli@link.it)
 */
public class LLMProviderRawChunk {

	private final String eventType;
	private final String dataPayload;

	public LLMProviderRawChunk(String eventType, String dataPayload) {
		this.eventType = eventType;
		this.dataPayload = dataPayload;
	}

	public String getEventType() {
		return this.eventType;
	}

	public String getDataPayload() {
		return this.dataPayload;
	}
}
