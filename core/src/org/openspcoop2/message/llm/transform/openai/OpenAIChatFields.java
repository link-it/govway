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
package org.openspcoop2.message.llm.transform.openai;

/**
 * Costanti dei nomi di campo e dei valori ricorrenti nel JSON di OpenAI
 * Chat Completions (request e response, sync e streaming). Centralizzate per
 * evitare duplicazione di string literal tra i sei trasformatori del package.
 *
 * @author Andrea Poli (apoli@link.it)
 */
final class OpenAIChatFields {

	private OpenAIChatFields() {
		// utility class
	}

	/* === campi top-level (request + response) === */
	static final String FIELD_ID = "id";
	static final String FIELD_MODEL = "model";
	static final String FIELD_MESSAGES = "messages";
	static final String FIELD_CHOICES = "choices";
	static final String FIELD_USAGE = "usage";
	static final String FIELD_TOOLS = "tools";

	/* === message / choice / delta === */
	static final String FIELD_ROLE = "role";
	static final String FIELD_CONTENT = "content";
	static final String FIELD_NAME = "name";
	static final String FIELD_MESSAGE = "message";
	static final String FIELD_DELTA = "delta";
	static final String FIELD_INDEX = "index";
	static final String FIELD_FINISH_REASON = "finish_reason";
	static final String FIELD_TOOL_CALLS = "tool_calls";
	static final String FIELD_TOOL_CALL_ID = "tool_call_id";
	static final String FIELD_TEXT = "text";
	static final String FIELD_TYPE = "type";

	/* === tool / function === */
	static final String FIELD_FUNCTION = "function";
	static final String FIELD_ARGUMENTS = "arguments";

	/* === usage === */
	static final String FIELD_PROMPT_TOKENS = "prompt_tokens";
	static final String FIELD_COMPLETION_TOKENS = "completion_tokens";

	/* === valori === */
	static final String ROLE_SYSTEM = "system";
	static final String ROLE_USER = "user";
	static final String ROLE_ASSISTANT = "assistant";
	static final String ROLE_TOOL = "tool";

	static final String TYPE_FUNCTION = "function";

	static final String FINISH_REASON_STOP = "stop";
	static final String FINISH_REASON_LENGTH = "length";
	static final String FINISH_REASON_TOOL_CALLS = "tool_calls";
	static final String FINISH_REASON_FUNCTION_CALL = "function_call";

	/* === streaming === */
	static final String STREAM_DONE_MARKER = "[DONE]";

	/* === resource path === */
	static final String RESOURCE_PATH_CHAT_COMPLETIONS = "/chat/completions";

	/* === campi top-level addizionali === */
	static final String FIELD_OBJECT = "object";
	static final String FIELD_CREATED = "created";
	static final String FIELD_TOTAL_TOKENS = "total_tokens";
	static final String FIELD_MAX_COMPLETION_TOKENS = "max_completion_tokens";
	static final String FIELD_MAX_TOKENS = "max_tokens";
	static final String FIELD_TEMPERATURE = "temperature";
	static final String FIELD_TOP_P = "top_p";
	static final String FIELD_STREAM = "stream";
	static final String FIELD_STOP = "stop";
	static final String FIELD_SYSTEM = "system";
	static final String FIELD_DESCRIPTION = "description";
	static final String FIELD_PARAMETERS = "parameters";

	/* === valori di object === */
	static final String OBJECT_CHAT_COMPLETION = "chat.completion";
	static final String OBJECT_CHAT_COMPLETION_CHUNK = "chat.completion.chunk";

	/* === content block types (lato canonical, propagati nello stream) === */
	static final String BLOCK_TYPE_TEXT = "text";
	static final String BLOCK_TYPE_TOOL_USE = "tool_use";
}
