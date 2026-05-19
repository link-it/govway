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

import org.openspcoop2.message.llm.stream.CanonicalStreamEvent;

/**
 * Encoder semantico canonical → chunk SSE front-door. La front-door verso il
 * client espone <strong>sempre SSE</strong>, quindi qui non si sceglie il
 * transport, solo il dialect: il chunk emesso è già nel formato che il client
 * (parlante dialect OpenAI o Anthropic) si aspetta.
 * <p>
 * Implementazioni MVP: {@code OpenAIChatChunkEncoder},
 * {@code AnthropicMessagesChunkEncoder}.
 * </p>
 *
 * @author Andrea Poli (apoli@link.it)
 */
public interface LLMOutboundFrontDoorChunkEncoder {

	/**
	 * Dialect front-door supportato dall'encoder.
	 */
	LLMDialect getSupportedDialect();

	/**
	 * Encoda un evento canonical nei bytes del chunk SSE front-door
	 * (di solito uno o più {@code event: ...\ndata: ...\n\n}).
	 * Ritorna array vuoto se l'evento canonical non produce alcun chunk
	 * front-door (es. ping ignorato dal client OpenAI).
	 */
	byte[] encode(CanonicalStreamEvent event) throws LLMTransformException;

	/**
	 * Bytes da emettere al termine dello stream (es. {@code data: [DONE]\n\n}
	 * per OpenAI). Ritorna array vuoto se l'encoder non richiede terminator.
	 */
	byte[] terminator();
}
