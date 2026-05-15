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
 * Contratto per un trasformatore outbound front-door: CanonicalChatResponse →
 * payload nel dialetto del client (OpenAI Chat Completions, Anthropic Messages, ...).
 * <p>
 * Un'implementazione gestisce un singolo {@link LLMDialect}.
 * </p>
 *
 * @author Andrea Poli (apoli@link.it)
 */
public interface LLMOutboundFrontDoorResponseTransformer {

	/**
	 * Dialetto front-door supportato da questa implementazione.
	 */
	LLMDialect getSupportedDialect();

	/**
	 * Converte una response canonical nel payload bytes da restituire al client
	 * nel dialetto richiesto.
	 */
	byte[] transform(CanonicalChatResponse response) throws LLMTransformException;
}
