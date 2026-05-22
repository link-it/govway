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

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import org.openspcoop2.message.llm.stream.AwsEventStreamReader;
import org.openspcoop2.message.llm.stream.LLMProviderStreamReader;
import org.openspcoop2.message.llm.stream.LLMProviderStreamTransport;
import org.openspcoop2.message.llm.stream.SseStreamReader;
import org.openspcoop2.message.llm.transform.anthropic.AnthropicInboundProviderResponseTransformer;
import org.openspcoop2.message.llm.transform.anthropic.AnthropicMessagesChunkDecoder;
import org.openspcoop2.message.llm.transform.anthropic.AnthropicMessagesChunkEncoder;
import org.openspcoop2.message.llm.transform.anthropic.AnthropicMessagesInboundRequestTransformer;
import org.openspcoop2.message.llm.transform.anthropic.AnthropicMessagesOutboundFrontDoorResponseTransformer;
import org.openspcoop2.message.llm.transform.anthropic.AnthropicOutboundProviderRequestTransformer;
import org.openspcoop2.message.llm.transform.awsbedrock.AwsBedrockConverseChunkDecoder;
import org.openspcoop2.message.llm.transform.awsbedrock.AwsBedrockConverseInboundProviderResponseTransformer;
import org.openspcoop2.message.llm.transform.awsbedrock.AwsBedrockConverseOutboundProviderRequestTransformer;
import org.openspcoop2.message.llm.transform.openai.OpenAIChatChunkDecoder;
import org.openspcoop2.message.llm.transform.openai.OpenAIChatChunkEncoder;
import org.openspcoop2.message.llm.transform.openai.OpenAIChatInboundProviderResponseTransformer;
import org.openspcoop2.message.llm.transform.openai.OpenAIChatInboundRequestTransformer;
import org.openspcoop2.message.llm.transform.openai.OpenAIChatOutboundFrontDoorResponseTransformer;
import org.openspcoop2.message.llm.transform.openai.OpenAIChatOutboundProviderRequestTransformer;


/**
 * Registry dei trasformatori LLM. Per il prototipo è un insieme di mappe statiche
 * indicizzate per LLMDialect o providerId. Copre:
 * <ul>
 *   <li>inbound request (front-door, per LLMDialect)</li>
 *   <li>outbound provider request (back-door, per providerId)</li>
 *   <li>inbound provider response (per providerId)</li>
 *   <li>outbound front-door response (per LLMDialect)</li>
 *   <li>provider stream reader (transport, factory: instance-per-stream)</li>
 *   <li>inbound provider chunk decoder (per providerId)</li>
 *   <li>outbound front-door chunk encoder (per LLMDialect, factory: instance-per-stream)</li>
 * </ul>
 *
 * @author Andrea Poli (apoli@link.it)
 */
public final class LLMTransformerRegistry {

	private static final Map<LLMDialect, LLMInboundRequestTransformer> INBOUND_REQUEST =
			new EnumMap<>(LLMDialect.class);

	private static final Map<String, LLMOutboundProviderRequestTransformer> OUTBOUND_PROVIDER_REQUEST =
			new HashMap<>();

	private static final Map<String, LLMInboundProviderResponseTransformer> INBOUND_PROVIDER_RESPONSE =
			new HashMap<>();

	private static final Map<LLMDialect, LLMOutboundFrontDoorResponseTransformer> OUTBOUND_FRONTDOOR_RESPONSE =
			new EnumMap<>(LLMDialect.class);

	private static final Map<LLMProviderStreamTransport, Supplier<LLMProviderStreamReader>> PROVIDER_STREAM_READERS =
			new EnumMap<>(LLMProviderStreamTransport.class);

	private static final Map<String, Supplier<LLMInboundProviderChunkDecoder>> INBOUND_PROVIDER_CHUNK_DECODERS =
			new HashMap<>();

	private static final Map<LLMDialect, Supplier<LLMOutboundFrontDoorChunkEncoder>> OUTBOUND_FRONTDOOR_CHUNK_ENCODERS =
			new EnumMap<>(LLMDialect.class);

	static {
		// Front-door request transformer (per LLMDialect)
		registerInbound(new OpenAIChatInboundRequestTransformer());
		registerInbound(new AnthropicMessagesInboundRequestTransformer());

		// Front-door response transformer (per LLMDialect)
		registerOutboundFrontDoorResponse(new OpenAIChatOutboundFrontDoorResponseTransformer());
		registerOutboundFrontDoorResponse(new AnthropicMessagesOutboundFrontDoorResponseTransformer());

		// Provider request transformer (per providerId)
		registerOutboundProvider(new AnthropicOutboundProviderRequestTransformer());
		registerOutboundProvider(new OpenAIChatOutboundProviderRequestTransformer());
		registerOutboundProvider(new AwsBedrockConverseOutboundProviderRequestTransformer());

		// Provider response transformer (per providerId)
		registerInboundProviderResponse(new AnthropicInboundProviderResponseTransformer());
		registerInboundProviderResponse(new OpenAIChatInboundProviderResponseTransformer());
		registerInboundProviderResponse(new AwsBedrockConverseInboundProviderResponseTransformer());

		// Streaming: transport reader per transport, chunk decoder per providerId, chunk encoder per LLMDialect (front-door è sempre SSE).
		// I decoder vengono registrati come factory (nuova istanza per ogni stream) perché alcune implementazioni
		// sono stateful — es. OpenAIChatChunkDecoder traccia message_start/content_block_start già emessi.
		PROVIDER_STREAM_READERS.put(LLMProviderStreamTransport.SSE, SseStreamReader::new);
		PROVIDER_STREAM_READERS.put(LLMProviderStreamTransport.AWS_EVENT_STREAM, AwsEventStreamReader::new);
		INBOUND_PROVIDER_CHUNK_DECODERS.put(LLMProviders.ANTHROPIC, AnthropicMessagesChunkDecoder::new);
		INBOUND_PROVIDER_CHUNK_DECODERS.put(LLMProviders.OPENAI, OpenAIChatChunkDecoder::new);
		INBOUND_PROVIDER_CHUNK_DECODERS.put(LLMProviders.AWS_BEDROCK, AwsBedrockConverseChunkDecoder::new);
		OUTBOUND_FRONTDOOR_CHUNK_ENCODERS.put(LLMDialect.OPENAI_CHAT_V1, OpenAIChatChunkEncoder::new);
		OUTBOUND_FRONTDOOR_CHUNK_ENCODERS.put(LLMDialect.ANTHROPIC_MESSAGES_V1, AnthropicMessagesChunkEncoder::new);
	}

	private LLMTransformerRegistry() {
		// utility class
	}

	private static void registerInbound(LLMInboundRequestTransformer transformer) {
		INBOUND_REQUEST.put(transformer.getSupportedDialect(), transformer);
	}

	private static void registerOutboundProvider(LLMOutboundProviderRequestTransformer transformer) {
		OUTBOUND_PROVIDER_REQUEST.put(transformer.getProviderId(), transformer);
	}

	private static void registerInboundProviderResponse(LLMInboundProviderResponseTransformer transformer) {
		INBOUND_PROVIDER_RESPONSE.put(transformer.getProviderId(), transformer);
	}

	private static void registerOutboundFrontDoorResponse(LLMOutboundFrontDoorResponseTransformer transformer) {
		OUTBOUND_FRONTDOOR_RESPONSE.put(transformer.getSupportedDialect(), transformer);
	}

	/**
	 * Risolve il trasformatore inbound della richiesta per il dialetto front-door dichiarato dall'API.
	 *
	 * @param format dialetto LLM ({@link LLMDialect#isLLM()} deve essere true)
	 * @return il trasformatore registrato
	 * @throws LLMTransformException se non esiste un trasformatore per quel formato
	 */
	public static LLMInboundRequestTransformer getInboundRequestTransformer(LLMDialect format) throws LLMTransformException {
		if (format == null) {
			throw new LLMTransformException("LLMDialect nullo: impossibile risolvere il trasformatore inbound");
		}
		LLMInboundRequestTransformer t = INBOUND_REQUEST.get(format);
		if (t == null) {
			throw new LLMTransformException("Nessun inbound request transformer registrato per LLMDialect=" + format.getValue());
		}
		return t;
	}

	/**
	 * Risolve il trasformatore outbound della richiesta verso un provider back-door.
	 *
	 * @param providerId identificativo del provider (vedi {@link LLMProviders})
	 * @return il trasformatore registrato
	 * @throws LLMTransformException se non esiste un trasformatore per quel provider
	 */
	public static LLMOutboundProviderRequestTransformer getOutboundProviderRequestTransformer(String providerId) throws LLMTransformException {
		if (providerId == null || providerId.isEmpty()) {
			throw new LLMTransformException("providerId nullo o vuoto: impossibile risolvere il trasformatore outbound");
		}
		LLMOutboundProviderRequestTransformer t = OUTBOUND_PROVIDER_REQUEST.get(providerId);
		if (t == null) {
			throw new LLMTransformException("Nessun outbound provider request transformer registrato per providerId=" + providerId);
		}
		return t;
	}

	/**
	 * Risolve il trasformatore inbound della response proveniente da un provider back-door.
	 *
	 * @param providerId identificativo del provider (vedi {@link LLMProviders})
	 * @return il trasformatore registrato
	 * @throws LLMTransformException se non esiste un trasformatore per quel provider
	 */
	public static LLMInboundProviderResponseTransformer getInboundProviderResponseTransformer(String providerId) throws LLMTransformException {
		if (providerId == null || providerId.isEmpty()) {
			throw new LLMTransformException("providerId nullo o vuoto: impossibile risolvere il trasformatore inbound response");
		}
		LLMInboundProviderResponseTransformer t = INBOUND_PROVIDER_RESPONSE.get(providerId);
		if (t == null) {
			throw new LLMTransformException("Nessun inbound provider response transformer registrato per providerId=" + providerId);
		}
		return t;
	}

	/**
	 * Risolve il trasformatore outbound della response verso il dialetto del client front-door.
	 *
	 * @param format dialetto LLM front-door dichiarato dall'API
	 * @return il trasformatore registrato
	 * @throws LLMTransformException se non esiste un trasformatore per quel formato
	 */
	public static LLMOutboundFrontDoorResponseTransformer getOutboundFrontDoorResponseTransformer(LLMDialect format) throws LLMTransformException {
		if (format == null) {
			throw new LLMTransformException("LLMDialect nullo: impossibile risolvere il trasformatore outbound front-door");
		}
		LLMOutboundFrontDoorResponseTransformer t = OUTBOUND_FRONTDOOR_RESPONSE.get(format);
		if (t == null) {
			throw new LLMTransformException("Nessun outbound front-door response transformer registrato per LLMDialect=" + format.getValue());
		}
		return t;
	}

	/**
	 * Risolve un'istanza fresh del reader di transport per i chunk in arrivo dal provider.
	 * Ogni invocazione ritorna una nuova istanza perché i reader sono stateful (bufferano
	 * la lettura in corso).
	 *
	 * @param transport tipo di transport streaming dichiarato dal provider request transformer
	 *                  (vedi {@link LLMOutboundProviderRequestTransformer#getProviderStreamTransport()})
	 * @throws LLMTransformException se non esiste un reader per quel transport
	 */
	public static LLMProviderStreamReader newProviderStreamReader(LLMProviderStreamTransport transport) throws LLMTransformException {
		if (transport == null) {
			throw new LLMTransformException("LLMProviderStreamTransport nullo: impossibile risolvere il reader");
		}
		Supplier<LLMProviderStreamReader> f = PROVIDER_STREAM_READERS.get(transport);
		if (f == null) {
			throw new LLMTransformException("Nessun provider stream reader registrato per transport=" + transport.getValue());
		}
		return f.get();
	}

	/**
	 * Risolve un'istanza fresh del decoder semantico dei chunk provider verso canonical
	 * stream events. Decoder potenzialmente stateful (es. {@code OpenAIChatChunkDecoder}
	 * traccia message_start/content_block_start già emessi nello stream corrente),
	 * istanza per stream.
	 */
	public static LLMInboundProviderChunkDecoder newInboundProviderChunkDecoder(String providerId) throws LLMTransformException {
		if (providerId == null || providerId.isEmpty()) {
			throw new LLMTransformException("providerId nullo o vuoto: impossibile risolvere il chunk decoder");
		}
		Supplier<LLMInboundProviderChunkDecoder> supplier = INBOUND_PROVIDER_CHUNK_DECODERS.get(providerId);
		if (supplier == null) {
			throw new LLMTransformException("Nessun inbound provider chunk decoder registrato per providerId=" + providerId);
		}
		return supplier.get();
	}

	/**
	 * Risolve un'istanza fresh dell'encoder front-door per i chunk verso il client.
	 * Encoder potenzialmente stateful (es. OpenAI mantiene id/model dei chunk),
	 * istanza per stream.
	 */
	public static LLMOutboundFrontDoorChunkEncoder newOutboundFrontDoorChunkEncoder(LLMDialect dialect) throws LLMTransformException {
		if (dialect == null) {
			throw new LLMTransformException("LLMDialect nullo: impossibile risolvere il chunk encoder");
		}
		Supplier<LLMOutboundFrontDoorChunkEncoder> f = OUTBOUND_FRONTDOOR_CHUNK_ENCODERS.get(dialect);
		if (f == null) {
			throw new LLMTransformException("Nessun outbound front-door chunk encoder registrato per LLMDialect=" + dialect.getValue());
		}
		return f.get();
	}
}
