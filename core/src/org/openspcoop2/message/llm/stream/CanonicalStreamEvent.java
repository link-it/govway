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
package org.openspcoop2.message.llm.stream;

/**
 * Evento del modello canonical-stream. Forma block-based ispirata ad Anthropic Messages
 * streaming, è il "vocabolario" che decoder provider-side e encoder front-door-side
 * usano per scambiarsi gli eventi della response in streaming.
 * <p>
 * Mapping di riferimento (canonical → dialect/provider):
 * </p>
 * <ul>
 *   <li>{@link CanonicalStreamMessageStart}: inizio della risposta, expone id/model/role
 *       e (se disponibile) un primo accenno di usage iniziale.
 *       In Anthropic deriva da {@code message_start}; in OpenAI dal primo chunk con
 *       {@code delta.role:"assistant"}.</li>
 *   <li>{@link CanonicalStreamContentBlockStart}: apertura di un blocco semantico
 *       (text / tool_use / ...). Specifico Anthropic; in OpenAI è implicito.</li>
 *   <li>{@link CanonicalStreamTextDelta}: incremento di testo da concatenare al blocco
 *       text corrente. Anthropic {@code content_block_delta}+{@code text_delta},
 *       OpenAI {@code delta.content}.</li>
 *   <li>{@link CanonicalStreamToolUseDelta}: incremento JSON parziale dell'input di un
 *       tool_use. Anthropic {@code content_block_delta}+{@code input_json_delta},
 *       OpenAI {@code delta.tool_calls[].function.arguments}.</li>
 *   <li>{@link CanonicalStreamContentBlockStop}: chiusura del blocco corrente.
 *       Specifico Anthropic.</li>
 *   <li>{@link CanonicalStreamMessageDelta}: finalizzazione del messaggio con
 *       {@code stop_reason} e usage finale. Anthropic {@code message_delta},
 *       OpenAI ultimo chunk con {@code finish_reason} + eventuale extra {@code usage}.</li>
 *   <li>{@link CanonicalStreamMessageStop}: chiusura totale del messaggio.
 *       In OpenAI corrisponde a {@code data: [DONE]\n\n}.</li>
 *   <li>{@link CanonicalStreamPing}: keep-alive interno provider, ignorabile o
 *       propagabile come tale.</li>
 * </ul>
 *
 * @author Andrea Poli (apoli@link.it)
 */
public abstract class CanonicalStreamEvent {

	private final String type;

	protected CanonicalStreamEvent(String type) {
		this.type = type;
	}

	public String getType() {
		return this.type;
	}
}
