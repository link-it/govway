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

import org.openspcoop2.message.llm.CanonicalChatRequest;
import org.openspcoop2.message.llm.stream.LLMProviderStreamTransport;

/**
 * Contratto per un trasformatore outbound back-door: CanonicalChatRequest →
 * payload nel formato del provider (Anthropic Messages, OpenAI Chat Completions
 * verso OpenAI/Azure, Bedrock InvokeModel, ...).
 * <p>
 * Un'implementazione gestisce un singolo provider, identificato da
 * {@link #getProviderId()}. Più provider possono condividere la stessa shape
 * (es. Anthropic API diretta, Bedrock-Anthropic, Vertex-Anthropic): a livello
 * Provider Binding cambia il transport, qui cambia poco. Per il prototipo
 * c'è una sola implementazione Anthropic API diretta.
 * </p>
 *
 * @author Andrea Poli (apoli@link.it)
 */
public interface LLMOutboundProviderRequestTransformer {

	/**
	 * Identificativo del provider supportato. In futuro sarà la chiave di
	 * lookup nel catalogo Provider Binding.
	 */
	String getProviderId();

	/**
	 * Converte una request canonical nel formato del provider, restituendo
	 * il body serializzato e gli eventuali header HTTP statici che il provider
	 * richiede (es. {@code anthropic-version} per Anthropic).
	 * <p>
	 * L'autenticazione (API key, Bearer token) NON è dominio del trasformatore:
	 * resta a carico della configurazione del connettore GovWay.
	 * </p>
	 */
	LLMProviderRequest transform(CanonicalChatRequest request) throws LLMTransformException;

	/**
	 * Path-risorsa dell'endpoint provider per l'operazione canonical (es.
	 * {@code /messages} per Anthropic chat). Il {@code LLMOutboundRequestHandler}
	 * lo applica al {@code TransportRequestContext.functionParameters} così la
	 * URL invocata diventa {@code base-url-connettore + path-risorsa}. Permette
	 * di tenere nel connettore GovWay solo la base URL del provider (es.
	 * {@code https://api.anthropic.com/v1}) e di routare il canonical su path
	 * diversi quando in futuro arriveranno più operazioni (chat, embeddings, ecc.).
	 */
	String getProviderResourcePath(CanonicalChatRequest request);

	/**
	 * Transport streaming che il provider userà nella response. Indica al
	 * {@code LLMInboundResponseHandler} quale reader transport applicare per
	 * estrarre i chunk dal wire framing (SSE/NDJSON/AWS event-stream).
	 * <ul>
	 *   <li>Anthropic /messages, OpenAI /chat/completions: {@link LLMProviderStreamTransport#SSE}</li>
	 *   <li>Ollama /api/chat: {@link LLMProviderStreamTransport#NDJSON}</li>
	 *   <li>Bedrock InvokeModelWithResponseStream: {@link LLMProviderStreamTransport#AWS_EVENT_STREAM}</li>
	 * </ul>
	 */
	LLMProviderStreamTransport getProviderStreamTransport();
}
