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

import org.openspcoop2.message.llm.CanonicalError;
import org.openspcoop2.message.llm.CanonicalErrorType;

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
	/* Valori di tool_choice.type: coincidono con quelli canonical (CanonicalToolChoiceMode). */

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

	/* === error === */
	static final String FIELD_ERROR = "error";
	static final String TYPE_ERROR = "error";
	static final String EVENT_ERROR = "error";
	static final String ERROR_TYPE_INVALID_REQUEST = "invalid_request_error";
	static final String ERROR_TYPE_AUTHENTICATION = "authentication_error";
	static final String ERROR_TYPE_BILLING = "billing_error";
	static final String ERROR_TYPE_PERMISSION = "permission_error";
	static final String ERROR_TYPE_NOT_FOUND = "not_found_error";
	static final String ERROR_TYPE_REQUEST_TOO_LARGE = "request_too_large";
	static final String ERROR_TYPE_RATE_LIMIT = "rate_limit_error";
	static final String ERROR_TYPE_TIMEOUT = "timeout_error";
	static final String ERROR_TYPE_API = "api_error";
	static final String ERROR_TYPE_OVERLOADED = "overloaded_error";

	/* === resource path / header HTTP === */
	static final String RESOURCE_PATH_MESSAGES = "/messages";
	static final String HEADER_ANTHROPIC_VERSION = "anthropic-version";

	/**
	 * Mappa la classificazione canonical sul valore {@code error.type} atteso da Anthropic:
	 * l'SDK client modella l'errore come union discriminata su questo campo, quindi un valore
	 * fuori dall'insieme noto non sarebbe deserializzabile.
	 */
	static String errorType(CanonicalErrorType type) {
		if (type == null) {
			return ERROR_TYPE_API;
		}
		switch (type) {
			case INVALID_REQUEST: return ERROR_TYPE_INVALID_REQUEST;
			case AUTHENTICATION: return ERROR_TYPE_AUTHENTICATION;
			case BILLING: return ERROR_TYPE_BILLING;
			case PERMISSION: return ERROR_TYPE_PERMISSION;
			case NOT_FOUND: return ERROR_TYPE_NOT_FOUND;
			case REQUEST_TOO_LARGE: return ERROR_TYPE_REQUEST_TOO_LARGE;
			case RATE_LIMIT: return ERROR_TYPE_RATE_LIMIT;
			case TIMEOUT: return ERROR_TYPE_TIMEOUT;
			case OVERLOADED: return ERROR_TYPE_OVERLOADED;
			case API_ERROR:
			default: return ERROR_TYPE_API;
		}
	}

	/**
	 * Messaggio di errore da esporre: l'envelope Anthropic non ha un campo per il codice
	 * nativo del provider, che viene quindi anteposto al messaggio quando aggiunge informazione.
	 */
	static String errorMessage(CanonicalError error, String mappedType) {
		if (error == null) {
			return "";
		}
		String message = error.getMessage() != null ? error.getMessage() : "";
		String code = error.getCode();
		if (code == null || code.isEmpty() || code.equals(mappedType) || code.equals(message)) {
			return message;
		}
		return message.isEmpty() ? code : (code + ": " + message);
	}

	/** Mapping inverso di {@link #errorType(CanonicalErrorType)}, usato dal decoder degli eventi di errore. */
	static CanonicalErrorType canonicalErrorType(String anthropicErrorType) {
		if (anthropicErrorType == null) {
			return CanonicalErrorType.API_ERROR;
		}
		switch (anthropicErrorType) {
			case ERROR_TYPE_INVALID_REQUEST: return CanonicalErrorType.INVALID_REQUEST;
			case ERROR_TYPE_AUTHENTICATION: return CanonicalErrorType.AUTHENTICATION;
			case ERROR_TYPE_BILLING: return CanonicalErrorType.BILLING;
			case ERROR_TYPE_PERMISSION: return CanonicalErrorType.PERMISSION;
			case ERROR_TYPE_NOT_FOUND: return CanonicalErrorType.NOT_FOUND;
			case ERROR_TYPE_REQUEST_TOO_LARGE: return CanonicalErrorType.REQUEST_TOO_LARGE;
			case ERROR_TYPE_RATE_LIMIT: return CanonicalErrorType.RATE_LIMIT;
			case ERROR_TYPE_TIMEOUT: return CanonicalErrorType.TIMEOUT;
			case ERROR_TYPE_OVERLOADED: return CanonicalErrorType.OVERLOADED;
			default: return CanonicalErrorType.API_ERROR;
		}
	}
}
