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

import java.nio.charset.StandardCharsets;

import org.openspcoop2.message.llm.CanonicalError;
import org.openspcoop2.message.llm.CanonicalErrorType;
import org.openspcoop2.utils.json.JSONUtils;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Estrazione lenient del messaggio di errore dal body nativo dei provider LLM.
 * <p>
 * Copre le forme note dei provider supportati, senza fallire su forme sconosciute
 * (il body di errore non è mai garantito da contratto):
 * </p>
 * <ul>
 *   <li>OpenAI: <code>{"error":{"message":"...","type":"...","code":"..."}}</code></li>
 *   <li>Anthropic: <code>{"type":"error","error":{"type":"...","message":"..."}}</code></li>
 *   <li>AWS Bedrock: <code>{"message":"..."}</code> (con eventuale <code>__type</code>)</li>
 * </ul>
 * <p>
 * La classificazione dell'errore è derivata dallo stato HTTP: è l'unica informazione
 * omogenea tra provider diversi.
 * </p>
 *
 * @author Andrea Poli (apoli@link.it)
 */
public class LLMProviderErrorParser {

	private static final String FIELD_ERROR = "error";
	private static final String FIELD_MESSAGE = "message";
	private static final String FIELD_MESSAGE_AWS = "Message";
	private static final String FIELD_TYPE = "type";
	private static final String FIELD_CODE = "code";
	private static final String FIELD_TYPE_AWS = "__type";

	private LLMProviderErrorParser() {
	}

	public static CanonicalError parse(byte[] payload, int httpStatus) {
		CanonicalError error = new CanonicalError();
		error.setHttpStatus(httpStatus);
		error.setType(CanonicalErrorType.fromHttpStatus(httpStatus));
		JsonNode root = readTree(payload);
		if (root == null) {
			error.setMessage(fallbackMessage(payload));
			return error;
		}
		JsonNode errorNode = root.path(FIELD_ERROR);
		if (errorNode.isObject()) {
			error.setMessage(firstText(errorNode, FIELD_MESSAGE, FIELD_MESSAGE_AWS));
			error.setCode(firstText(errorNode, FIELD_CODE, FIELD_TYPE));
		} else if (errorNode.isTextual()) {
			error.setMessage(errorNode.asText());
		}
		if (error.getMessage() == null) {
			error.setMessage(firstText(root, FIELD_MESSAGE, FIELD_MESSAGE_AWS));
		}
		if (error.getCode() == null) {
			error.setCode(firstText(root, FIELD_TYPE_AWS, FIELD_CODE));
		}
		if (error.getMessage() == null) {
			error.setMessage(fallbackMessage(payload));
		}
		return error;
	}

	private static JsonNode readTree(byte[] payload) {
		if (payload == null || payload.length == 0) {
			return null;
		}
		try {
			JsonNode root = JSONUtils.getObjectMapper().readTree(payload);
			return root != null && root.isObject() ? root : null;
		} catch (Exception e) {
			return null;
		}
	}

	private static String firstText(JsonNode node, String... fields) {
		for (String field : fields) {
			if (node.hasNonNull(field) && node.get(field).isTextual()) {
				String value = node.get(field).asText();
				if (!value.isEmpty()) {
					return value;
				}
			}
		}
		return null;
	}

	/** Body non JSON o senza campi noti: riportiamo il testo grezzo, troncato. */
	private static String fallbackMessage(byte[] payload) {
		if (payload == null || payload.length == 0) {
			return null;
		}
		String raw = new String(payload, StandardCharsets.UTF_8).trim();
		if (raw.isEmpty()) {
			return null;
		}
		return raw.length() > 1024 ? raw.substring(0, 1024) : raw;
	}
}
