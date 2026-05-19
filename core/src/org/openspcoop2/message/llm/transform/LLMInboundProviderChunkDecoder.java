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
package org.openspcoop2.message.llm.transform;

import java.util.List;

import org.openspcoop2.message.llm.stream.CanonicalStreamEvent;
import org.openspcoop2.message.llm.stream.LLMProviderRawChunk;

/**
 * Decoder semantico del singolo chunk grezzo emesso dal transport reader del
 * provider verso il modello canonical stream. Indipendente dal transport
 * (SSE/NDJSON/AWS event-stream): riceve già il chunk estratto dal framing e
 * traduce il contenuto del payload (di solito JSON) in 0..N
 * {@link CanonicalStreamEvent}.
 * <p>
 * Implementazioni MVP: {@code AnthropicMessagesChunkDecoder}. Future: decoder
 * OpenAI Chat Completions (per provider OpenAI/Azure/compat), Bedrock-Anthropic
 * (sblocca il container base64), Ollama, ecc.
 * </p>
 *
 * @author Andrea Poli (apoli@link.it)
 */
public interface LLMInboundProviderChunkDecoder {

	/**
	 * Identificativo del provider supportato (es. {@code "anthropic"}).
	 */
	String getProviderId();

	/**
	 * Trasforma il chunk grezzo in zero o più eventi canonical.
	 * Lista vuota = il chunk è semanticamente innocuo (es. ping ignorato).
	 */
	List<CanonicalStreamEvent> decode(LLMProviderRawChunk chunk) throws LLMTransformException;
}
