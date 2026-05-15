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

import org.openspcoop2.message.llm.CanonicalChatResponse;

/**
 * Contratto per un trasformatore inbound back-door: payload di risposta del
 * provider (Anthropic Messages response, OpenAI Chat Completion response, ...)
 * → CanonicalChatResponse interna.
 * <p>
 * Un'implementazione gestisce un singolo provider, identificato da
 * {@link #getProviderId()}.
 * </p>
 *
 * @author Andrea Poli (apoli@link.it)
 */
public interface LLMInboundProviderResponseTransformer {

	/**
	 * Identificativo del provider supportato (vedi {@link LLMProviders}).
	 */
	String getProviderId();

	/**
	 * Converte una response del provider nel modello canonical interno.
	 *
	 * @param payload bytes della response JSON ricevuta dal provider
	 * @return istanza canonical
	 * @throws LLMTransformException se il payload non rispetta lo schema atteso
	 */
	CanonicalChatResponse transform(byte[] payload) throws LLMTransformException;
}
