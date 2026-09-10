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
import org.openspcoop2.message.llm.CanonicalError;

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

	/**
	 * Converte il body di una response di errore del provider (HTTP non 2xx) nel modello
	 * canonical interno. L'implementazione di default copre le forme di errore dei provider
	 * supportati via {@link LLMProviderErrorParser}; un provider con un envelope di errore
	 * particolare può ridefinirla.
	 *
	 * @param payload bytes del body di errore ricevuto dal provider (può essere vuoto o non JSON)
	 * @param httpStatus stato HTTP della response del provider
	 * @return errore canonical, mai null
	 */
	default CanonicalError transformError(byte[] payload, int httpStatus) {
		return LLMProviderErrorParser.parse(payload, httpStatus);
	}
}
