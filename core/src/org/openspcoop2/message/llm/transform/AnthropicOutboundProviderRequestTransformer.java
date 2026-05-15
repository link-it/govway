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

import java.util.LinkedHashMap;
import java.util.Map;

import org.openspcoop2.message.llm.CanonicalChatRequest;
import org.openspcoop2.utils.json.JSONUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Trasformatore outbound provider: CanonicalChatRequest → JSON Anthropic Messages.
 * <p>
 * Il canonical è modellato sulla forma Anthropic (block-based, system top-level,
 * naming snake_case allineato), quindi la trasformazione è quasi un noop. Le
 * uniche cose che il trasformatore garantisce:
 * </p>
 * <ul>
 *   <li>{@code max_tokens} valorizzato (Anthropic lo richiede obbligatorio).
 *       Se assente nel canonical, applichiamo un default conservativo.</li>
 *   <li>{@code sourceDialect} del canonical resta fuori dal payload provider
 *       (è già {@code @JsonIgnore} sul modello).</li>
 *   <li>Header HTTP {@code anthropic-version} dichiarato nel risultato della
 *       trasformazione: l'handler li applicherà al messaggio prima del send.</li>
 * </ul>
 *
 * Header di autenticazione ({@code x-api-key}) sono gestiti dal connettore GovWay
 * via configurazione "Autenticazione API Key", non da questo trasformatore.
 *
 * @author Andrea Poli (apoli@link.it)
 */
public class AnthropicOutboundProviderRequestTransformer implements LLMOutboundProviderRequestTransformer {

	/** Default applicato se la request canonical non specifica max_tokens. */
	private static final int DEFAULT_MAX_TOKENS = 4096;

	/** Versione dell'API Anthropic Messages stabile di riferimento per il prototipo. */
	private static final String ANTHROPIC_VERSION_HEADER = "anthropic-version";
	private static final String ANTHROPIC_VERSION_VALUE = "2023-06-01";

	/** Path-risorsa dell'endpoint Anthropic per chat sincrona (native Messages API). */
	private static final String RESOURCE_PATH_CHAT = "/messages";

	@Override
	public String getProviderId() {
		return LLMProviders.ANTHROPIC;
	}

	@Override
	public String getProviderResourcePath(CanonicalChatRequest request) {
		// Per il prototipo c'è una sola operazione canonical (chat). In futuro,
		// quando arriveranno embeddings/batch/ecc. si farà uno switch sulla
		// natura del canonical.
		return RESOURCE_PATH_CHAT;
	}

	@Override
	public LLMProviderRequest transform(CanonicalChatRequest request) throws LLMTransformException {
		if (request == null) {
			throw new LLMTransformException("CanonicalChatRequest nullo");
		}
		try {
			ObjectMapper mapper = JSONUtils.getObjectMapper();
			ObjectNode out = mapper.valueToTree(request);
			ensureMaxTokens(out);
			byte[] body = mapper.writeValueAsBytes(out);
			return new LLMProviderRequest(body, buildHeaders());
		} catch (Exception e) {
			throw new LLMTransformException("Errore nella serializzazione canonical → Anthropic Messages: " + e.getMessage(), e);
		}
	}

	private void ensureMaxTokens(ObjectNode out) {
		if (!out.hasNonNull("max_tokens")) {
			out.put("max_tokens", DEFAULT_MAX_TOKENS);
		}
	}

	private Map<String, String> buildHeaders() {
		Map<String, String> headers = new LinkedHashMap<>();
		headers.put(ANTHROPIC_VERSION_HEADER, ANTHROPIC_VERSION_VALUE);
		return headers;
	}
}
