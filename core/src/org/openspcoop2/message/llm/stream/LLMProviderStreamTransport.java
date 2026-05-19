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
 * Tipi di transport streaming usati dai provider LLM.
 * <ul>
 *   <li>{@link #SSE}: testo UTF-8 con framing {@code \n\n}, usato da Anthropic,
 *       OpenAI, Mistral, Google AI, Cohere, Groq, DeepSeek, xAI</li>
 *   <li>{@link #NDJSON}: testo UTF-8 con framing {@code \n} singolo, usato da
 *       Ollama, vLLM (talvolta), LocalAI</li>
 *   <li>{@link #AWS_EVENT_STREAM}: binario prelude+headers+payload+CRC, usato
 *       da AWS Bedrock (InvokeModelWithResponseStream, ConverseStream)</li>
 * </ul>
 * <p>
 * La front-door verso il client espone <strong>sempre SSE</strong>: il transport
 * è una proprietà del solo back-door provider (vedi
 * {@code LLMOutboundProviderRequestTransformer.getProviderStreamTransport()}).
 * </p>
 *
 * @author Andrea Poli (apoli@link.it)
 */
public enum LLMProviderStreamTransport {

	SSE("sse"),
	NDJSON("ndjson"),
	AWS_EVENT_STREAM("aws-event-stream");

	private final String value;

	LLMProviderStreamTransport(String value) {
		this.value = value;
	}

	public String getValue() {
		return this.value;
	}
}
