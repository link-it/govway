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

/**
 * Contratto per un trasformatore inbound front-door: payload del dialetto del client
 * (OpenAI Chat Completions, Anthropic Messages, ...) → modello canonical interno.
 * <p>
 * Un'implementazione gestisce un singolo {@link LLMDialect}.
 * Il payload in input è il body grezzo (byte[]) ricevuto dal client, già letto
 * dalla servlet ma non ancora parsato.
 * </p>
 *
 * @author Andrea Poli (apoli@link.it)
 */
public interface LLMInboundRequestTransformer {

	/**
	 * Dialetto front-door supportato da questa implementazione.
	 */
	LLMDialect getSupportedDialect();

	/**
	 * Converte il body di una request del dialetto supportato in un CanonicalChatRequest.
	 *
	 * @param payload bytes del body JSON ricevuto dal client
	 * @return istanza canonical
	 * @throws LLMTransformException se il payload non rispetta lo schema atteso
	 */
	CanonicalChatRequest transform(byte[] payload) throws LLMTransformException;
}
