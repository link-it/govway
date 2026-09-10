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
package org.openspcoop2.message.llm;

/**
 * Classificazione canonical dell'errore restituito dal provider LLM.
 * <p>
 * I trasformatori outbound front-door mappano ogni valore sulla stringa attesa dal
 * dialetto del client (es. {@code invalid_request_error} / {@code authentication_error}
 * per Anthropic, {@code invalid_request_error} / {@code server_error} per OpenAI).
 * La classificazione è derivata dallo stato HTTP della response del provider, che è
 * l'unica informazione omogenea tra i provider supportati.
 * </p>
 *
 * @author Andrea Poli (apoli@link.it)
 */
public enum CanonicalErrorType {

	INVALID_REQUEST,
	AUTHENTICATION,
	BILLING,
	PERMISSION,
	NOT_FOUND,
	REQUEST_TOO_LARGE,
	RATE_LIMIT,
	TIMEOUT,
	API_ERROR,
	OVERLOADED;

	public static CanonicalErrorType fromHttpStatus(int httpStatus) {
		switch (httpStatus) {
			case 400: return INVALID_REQUEST;
			case 401: return AUTHENTICATION;
			case 402: return BILLING;
			case 403: return PERMISSION;
			case 404: return NOT_FOUND;
			case 408: return TIMEOUT;
			case 413: return REQUEST_TOO_LARGE;
			case 429: return RATE_LIMIT;
			case 503: return OVERLOADED;
			case 504: return TIMEOUT;
			case 529: return OVERLOADED;
			default: break;
		}
		if (httpStatus >= 400 && httpStatus < 500) {
			return INVALID_REQUEST;
		}
		return API_ERROR;
	}
}
