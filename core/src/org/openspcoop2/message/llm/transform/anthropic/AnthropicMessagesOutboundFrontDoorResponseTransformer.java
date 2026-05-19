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
package org.openspcoop2.message.llm.transform.anthropic;

import org.openspcoop2.message.llm.CanonicalChatResponse;
import org.openspcoop2.message.llm.transform.LLMDialect;
import org.openspcoop2.message.llm.transform.LLMOutboundFrontDoorResponseTransformer;
import org.openspcoop2.message.llm.transform.LLMTransformException;
import org.openspcoop2.utils.json.JSONUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Trasformatore outbound front-door: CanonicalChatResponse → JSON Anthropic Messages response.
 * <p>
 * Il canonical è modellato sulla forma Anthropic, quindi la serializzazione è
 * quasi diretta. L'unica differenza è la presenza nei payload di response
 * Anthropic dei campi top-level {@code type="message"} e {@code role="assistant"}
 * che il canonical non porta (non necessari internamente).
 * </p>
 *
 * @author Andrea Poli (apoli@link.it)
 */
public class AnthropicMessagesOutboundFrontDoorResponseTransformer implements LLMOutboundFrontDoorResponseTransformer {

	@Override
	public LLMDialect getSupportedDialect() {
		return LLMDialect.ANTHROPIC_MESSAGES_V1;
	}

	@Override
	public byte[] transform(CanonicalChatResponse response) throws LLMTransformException {
		if (response == null) {
			throw new LLMTransformException("CanonicalChatResponse nullo");
		}
		try {
			ObjectMapper mapper = JSONUtils.getObjectMapper();
			ObjectNode out = mapper.valueToTree(response);
			ensureEnvelopeFields(out);
			return mapper.writeValueAsBytes(out);
		} catch (Exception e) {
			throw new LLMTransformException("Errore nella serializzazione canonical → Anthropic Messages response: " + e.getMessage(), e);
		}
	}

	private void ensureEnvelopeFields(ObjectNode out) {
		if (!out.hasNonNull(AnthropicMessagesFields.FIELD_TYPE)) {
			out.put(AnthropicMessagesFields.FIELD_TYPE, AnthropicMessagesFields.FIELD_MESSAGE);
		}
		if (!out.hasNonNull(AnthropicMessagesFields.FIELD_ROLE)) {
			out.put(AnthropicMessagesFields.FIELD_ROLE, AnthropicMessagesFields.ROLE_ASSISTANT);
		}
	}
}
