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
import org.openspcoop2.message.llm.LLMContextKeys;
import org.openspcoop2.utils.json.JSONUtils;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Conversione dell'errore generato dal gateway (problem detail RFC7807) nell'envelope di
 * errore del dialetto del client.
 * <p>
 * Gli errori prodotti da GovWay stesso (violazione di una policy di rate limiting,
 * autenticazione, autorizzazione, validazione, ...) non attraversano i trasformatori LLM:
 * vengono costruiti dal builder degli errori applicativi. Su un'API LLM il client parla
 * OpenAI o Anthropic e il suo SDK si aspetta l'envelope di errore di quel dialetto, non un
 * {@code application/problem+json}: questa classe fa da ponte, ed è invocata dal builder
 * degli errori quando il contesto della transazione dichiara un dialetto LLM.
 * </p>
 *
 * @author Andrea Poli (apoli@link.it)
 */
public class LLMFrontDoorErrorEnvelope {

	private static final String FIELD_STATUS = "status";
	private static final String FIELD_TITLE = "title";
	private static final String FIELD_DETAIL = "detail";

	private LLMFrontDoorErrorEnvelope() {
	}

	/**
	 * Ritorna il body di errore nel dialetto dichiarato dal contesto, oppure null se la
	 * transazione non è un flusso LLM o se la conversione non è possibile: in quel caso il
	 * chiamante mantiene il body originale.
	 *
	 * @param context contesto della transazione (PdDContext), può essere null
	 * @param problemDetail body di errore generato dal gateway (problem detail JSON)
	 * @param httpStatus stato HTTP che verrà restituito al client, 0 se non noto
	 */
	public static byte[] convert(org.openspcoop2.utils.Map<Object> context, byte[] problemDetail, int httpStatus) {
		LLMDialect dialect = readDialect(context);
		if (dialect == null) {
			return null;
		}
		try {
			CanonicalError error = buildError(problemDetail, httpStatus);
			return LLMTransformerRegistry.getOutboundFrontDoorResponseTransformer(dialect).transformError(error);
		} catch (Exception e) {
			return null;
		}
	}

	private static LLMDialect readDialect(org.openspcoop2.utils.Map<Object> context) {
		if (context == null) {
			return null;
		}
		Object o = context.getObject(LLMContextKeys.PDD_CTX_LLM_FORMATO);
		if (o instanceof LLMDialect) {
			return (LLMDialect) o;
		}
		if (o instanceof String) {
			return LLMDialect.fromValue((String) o);
		}
		return null;
	}

	private static CanonicalError buildError(byte[] problemDetail, int httpStatus) {
		CanonicalError error = new CanonicalError();
		JsonNode root = readTree(problemDetail);
		int status = httpStatus;
		if (root != null && root.hasNonNull(FIELD_STATUS) && root.get(FIELD_STATUS).isInt()) {
			status = root.get(FIELD_STATUS).asInt();
		}
		error.setHttpStatus(status);
		error.setType(CanonicalErrorType.fromHttpStatus(status));
		if (root != null) {
			String detail = text(root, FIELD_DETAIL);
			String title = text(root, FIELD_TITLE);
			error.setMessage(detail != null ? detail : title);
			if (detail != null) {
				error.setCode(title);
			}
		}
		if (error.getMessage() == null) {
			error.setMessage(fallbackMessage(problemDetail, status));
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

	private static String text(JsonNode node, String field) {
		if (node.hasNonNull(field) && node.get(field).isTextual()) {
			String value = node.get(field).asText();
			if (!value.isEmpty()) {
				return value;
			}
		}
		return null;
	}

	/** Body non riconosciuto (o assente): riportiamo il testo grezzo troncato, altrimenti lo stato HTTP. */
	private static String fallbackMessage(byte[] payload, int status) {
		if (payload != null && payload.length > 0) {
			String raw = new String(payload, StandardCharsets.UTF_8).trim();
			if (!raw.isEmpty()) {
				return raw.length() > 1024 ? raw.substring(0, 1024) : raw;
			}
		}
		return status > 0 ? ("HTTP " + status) : "";
	}
}
