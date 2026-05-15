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
package org.openspcoop2.pdd.core.handlers.llm;

import org.openspcoop2.utils.Map;
import org.openspcoop2.utils.MapKey;

/**
 * Chiavi standard nel PdDContext usate dagli handler LLM per condividere stato
 * lungo il ciclo di vita della transazione. Usano {@link MapKey} per coerenza
 * con l'API tipizzata di {@link org.openspcoop2.pdd.core.PdDContext}.
 *
 * @author Andrea Poli (apoli@link.it)
 */
public final class LLMHandlerConstants {

	/**
	 * Chiave PdDContext che ospita il {@link org.openspcoop2.message.llm.transform.LLMDialect}
	 * dell'API LLM. Quando presente nel context indica che la transazione è un
	 * flusso LLM: gli handler LLM si attivano, altrimenti rimangono no-op.
	 */
	public static final MapKey<String> PDD_CTX_LLM_FORMATO = Map.newMapKey("llm.formatoSpecifica");

	/**
	 * Chiave PdDContext che ospita l'identificativo del provider back-end (es. "anthropic").
	 * Per il prototipo il valore è hardcoded ad {@code anthropic}; in futuro sarà
	 * risolto dal catalogo Provider Binding.
	 */
	public static final MapKey<String> PDD_CTX_LLM_PROVIDER = Map.newMapKey("llm.providerId");

	/**
	 * Chiave PdDContext che ospita il {@link org.openspcoop2.utils.transport.TransportResponseContext}
	 * del messaggio response originale dal provider, ripulito da Content-Encoding e
	 * Content-Length (che vengono ricalcolati per il body trasformato). Permette di
	 * preservare gli header utili (rate-limit, request-id, ecc.) attraverso la
	 * trasformazione canonical e ri-applicarli al messaggio finale verso il client.
	 */
	public static final MapKey<String> PDD_CTX_LLM_RESPONSE_HEADERS = Map.newMapKey("llm.responseHeaders");

	/**
	 * Chiave PdDContext che ospita il {@link org.openspcoop2.message.llm.CanonicalChatRequest}
	 * parsato dall'{@code LLMInboundRequestHandler}. Viaggia nel context invece che nel
	 * messaggio perché altri step della pipeline (es. validazione contenuti) potrebbero
	 * sostituire il messaggio nel context con un OpenSPCoop2Message_json_impl standard.
	 */
	public static final MapKey<String> PDD_CTX_LLM_CANONICAL_REQUEST = Map.newMapKey("llm.canonicalRequest");

	/**
	 * Chiave PdDContext che ospita la {@link org.openspcoop2.message.llm.CanonicalChatResponse}
	 * parsata dall'{@code LLMInboundResponseHandler}. Stesso pattern di
	 * {@link #PDD_CTX_LLM_CANONICAL_REQUEST}: il canonical viaggia nel context, non
	 * nel messaggio.
	 */
	public static final MapKey<String> PDD_CTX_LLM_CANONICAL_RESPONSE = Map.newMapKey("llm.canonicalResponse");

	/**
	 * Chiave PdDContext che indica se la transazione è in modalità streaming
	 * (il client ha richiesto {@code stream:true} nel body). Valore: {@link Boolean}.
	 * <p>
	 * Popolata dall'{@code LLMInboundRequestHandler} leggendo {@code canonical.stream}
	 * dopo che gli inbound transformer del dialect front-door lo hanno valorizzato.
	 * Gli handler downstream (OutRequest, InResponse, OutResponse) la consultano
	 * per decidere se trattare body singolo (sync) o chunk SSE (stream).
	 * </p>
	 */
	public static final MapKey<String> PDD_CTX_LLM_STREAM = Map.newMapKey("llm.stream");

	private LLMHandlerConstants() {
		// utility class
	}
}
