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

/**
 * Costanti dei nomi di campo e dei valori ricorrenti nel JSON di Anthropic
 * Messages (request e response, sync e streaming). Centralizzate per evitare
 * duplicazione di string literal tra i trasformatori del package.
 *
 * @author Andrea Poli (apoli@link.it)
 */
final class AnthropicMessagesFields {

	private AnthropicMessagesFields() {
		// utility class
	}

	/* === campi top-level === */
	static final String FIELD_ID = "id";
	static final String FIELD_MODEL = "model";
	static final String FIELD_MESSAGES = "messages";
	static final String FIELD_MESSAGE = "message";
	static final String FIELD_SYSTEM = "system";
	static final String FIELD_MAX_TOKENS = "max_tokens";
	static final String FIELD_TEMPERATURE = "temperature";
	static final String FIELD_TOP_P = "top_p";
	static final String FIELD_TOP_K = "top_k";
	static final String FIELD_STOP_SEQUENCES = "stop_sequences";
	static final String FIELD_STOP_REASON = "stop_reason";
	static final String FIELD_STOP_SEQUENCE = "stop_sequence";
	static final String FIELD_STREAM = "stream";
	static final String FIELD_TOOLS = "tools";
	static final String FIELD_TOOL_CHOICE = "tool_choice";

	/* === message / content === */
	static final String FIELD_ROLE = "role";
	static final String FIELD_CONTENT = "content";
	static final String FIELD_TYPE = "type";
	static final String FIELD_NAME = "name";
	static final String FIELD_TEXT = "text";
	static final String FIELD_INPUT = "input";
	static final String FIELD_INPUT_SCHEMA = "input_schema";
	static final String FIELD_DESCRIPTION = "description";

	/* === tool_use / tool_result === */
	static final String FIELD_TOOL_USE_ID = "tool_use_id";
	static final String FIELD_IS_ERROR = "is_error";

	/* === usage === */
	static final String FIELD_USAGE = "usage";
	static final String FIELD_INPUT_TOKENS = "input_tokens";
	static final String FIELD_OUTPUT_TOKENS = "output_tokens";

	/* === streaming === */
	static final String FIELD_INDEX = "index";
	static final String FIELD_DELTA = "delta";
	static final String FIELD_CONTENT_BLOCK = "content_block";
	static final String FIELD_PARTIAL_JSON = "partial_json";

	/* === valori === */
	static final String BLOCK_TYPE_TEXT = "text";
	static final String BLOCK_TYPE_TOOL_USE = "tool_use";
	static final String BLOCK_TYPE_TOOL_RESULT = "tool_result";

	static final String EVENT_MESSAGE_START = "message_start";
	static final String EVENT_MESSAGE_DELTA = "message_delta";
	static final String EVENT_MESSAGE_STOP = "message_stop";
	static final String EVENT_CONTENT_BLOCK_START = "content_block_start";
	static final String EVENT_CONTENT_BLOCK_DELTA = "content_block_delta";
	static final String EVENT_CONTENT_BLOCK_STOP = "content_block_stop";
	static final String EVENT_PING = "ping";

	static final String DELTA_TYPE_TEXT = "text_delta";
	static final String DELTA_TYPE_INPUT_JSON = "input_json_delta";

	/* === ruoli === */
	static final String ROLE_USER = "user";
	static final String ROLE_ASSISTANT = "assistant";

	/* === resource path / header HTTP === */
	static final String RESOURCE_PATH_MESSAGES = "/messages";
	static final String HEADER_ANTHROPIC_VERSION = "anthropic-version";
}
