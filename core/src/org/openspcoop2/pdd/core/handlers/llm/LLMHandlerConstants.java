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
	 * Risolto dall'{@code LLMOutboundRequestHandler} via il LLM Provider Binding
	 * configurato sul connettore.
	 */
	public static final MapKey<String> PDD_CTX_LLM_PROVIDER = Map.newMapKey("llm.providerId");

	/**
	 * Chiave PdDContext che ospita il nome (user-defined) del LLM Provider registrato
	 * (es. "Claude-Anthropic"). Diverso da {@link #PDD_CTX_LLM_PROVIDER} che e' il
	 * tipo tecnico del dialect ("anthropic"/"openai"/"awsBedrock"). Usato per il
	 * salvataggio nella tabella {@code transazioni_llm.llm_provider}.
	 */
	public static final MapKey<String> PDD_CTX_LLM_PROVIDER_NAME = Map.newMapKey("llm.providerName");

	/**
	 * Chiave PdDContext che ospita il nome (user-defined) del LLM Model logico
	 * referenziato dal binding (es. "claude-haiku-4-5"). Usato per il salvataggio
	 * nella tabella {@code transazioni_llm.llm_model}.
	 */
	public static final MapKey<String> PDD_CTX_LLM_MODEL_NAME = Map.newMapKey("llm.modelName");

	/**
	 * Chiave PdDContext che ospita il nome (user-defined) del LLM Provider Binding
	 * selezionato sul connettore (es. "Anthropic-Direct-Claude-Haiku-4-5"). Usato per
	 * il salvataggio nella tabella {@code transazioni_llm.llm_provider_binding}.
	 */
	public static final MapKey<String> PDD_CTX_LLM_PROVIDER_BINDING_NAME = Map.newMapKey("llm.providerBindingName");

	/**
	 * Chiave PdDContext che ospita il Vendor Model Id richiesto dall'API del Provider
	 * concreto (es. "claude-haiku-4-5-20251001" per Anthropic direct,
	 * "anthropic.claude-haiku-4-5-20251001-v1:0" per Bedrock). I
	 * {@code LLMOutboundProviderRequestTransformer} lo leggono e lo iniettano nel
	 * payload provider-specific al posto del model logico canonico.
	 */
	public static final MapKey<String> PDD_CTX_LLM_VENDOR_MODEL_ID = Map.newMapKey("llm.vendorModelId");

	/**
	 * Chiave PdDContext che ospita il prezzo unitario di input (USD per 1M token)
	 * del binding selezionato. Letto come {@link Double}, opzionale. Usato per
	 * calcolare {@code cost_estimated} a fine transazione (input_tokens / 1_000_000 * price).
	 */
	public static final MapKey<String> PDD_CTX_LLM_PRICE_INPUT = Map.newMapKey("llm.priceInput");

	/**
	 * Chiave PdDContext che ospita il prezzo unitario di output (USD per 1M token).
	 * Stessa semantica di {@link #PDD_CTX_LLM_PRICE_INPUT}.
	 */
	public static final MapKey<String> PDD_CTX_LLM_PRICE_OUTPUT = Map.newMapKey("llm.priceOutput");

	/**
	 * Chiave PdDContext che ospita il divisore applicato al conteggio token input nel
	 * calcolo del costo: {@code cost_input = inputTokens / divisor * priceInput}. Default
	 * {@code 1.000.000} (prezzo per 1M token).
	 */
	public static final MapKey<String> PDD_CTX_LLM_PRICE_INPUT_DIVISOR = Map.newMapKey("llm.priceInputDivisor");

	/**
	 * Chiave PdDContext che ospita il divisore applicato al conteggio token output.
	 * Stessa semantica di {@link #PDD_CTX_LLM_PRICE_INPUT_DIVISOR}.
	 */
	public static final MapKey<String> PDD_CTX_LLM_PRICE_OUTPUT_DIVISOR = Map.newMapKey("llm.priceOutputDivisor");

	/**
	 * Chiave PdDContext che ospita la {@link org.openspcoop2.message.llm.CanonicalUsage} osservata
	 * dal {@link org.openspcoop2.message.llm.stream.ChunkTransformInputStream} durante lo streaming
	 * (eventi {@code message_start} / {@code message_delta} con campo usage). Aggiornata in modo
	 * cumulativo: il valore finale al termine dello stream e' usato per popolare
	 * {@code transazioni_llm.token_input/token_output}. In modalita' sync l'usage e' gia'
	 * disponibile via {@link #PDD_CTX_LLM_CANONICAL_RESPONSE}, quindi questa chiave resta nulla.
	 */
	public static final MapKey<String> PDD_CTX_LLM_STREAM_USAGE = Map.newMapKey("llm.streamUsage");

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
