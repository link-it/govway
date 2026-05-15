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


/**
 * Registry dei trasformatori LLM. Per il prototipo è una coppia di mappe
 * statiche: inbound request (front-door, indicizzati per LLMDialect) e
 * outbound provider request (back-door, indicizzati per providerId).
 * Sarà esteso con: inbound provider response, outbound front-door response,
 * varianti streaming.
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

	static {
		registerInbound(new OpenAIChatInboundRequestTransformer());
		registerInbound(new AnthropicMessagesInboundRequestTransformer());
		registerOutboundProvider(new AnthropicOutboundProviderRequestTransformer());
		registerInboundProviderResponse(new AnthropicInboundProviderResponseTransformer());
		registerOutboundFrontDoorResponse(new OpenAIChatOutboundFrontDoorResponseTransformer());
		registerOutboundFrontDoorResponse(new AnthropicMessagesOutboundFrontDoorResponseTransformer());
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
}
