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
 * Errore restituito dal provider LLM nel modello canonical interno.
 * <p>
 * Popolato dai trasformatori inbound a partire dal body di errore nativo del provider
 * (che ha forma diversa per ogni provider: {@code error.message} per OpenAI e Anthropic,
 * {@code message} per AWS Bedrock) e serializzato dai trasformatori outbound front-door
 * nell'envelope di errore del dialetto richiesto dal client.
 * </p>
 *
 * @author Andrea Poli (apoli@link.it)
 */
public class CanonicalError {

	/** Classificazione dell'errore, derivata dallo stato HTTP della response del provider. */
	private CanonicalErrorType type;

	/** Messaggio descrittivo estratto dal body di errore del provider. */
	private String message;

	/** Codice/tipo nativo dichiarato dal provider (es. "invalid_api_key", "ValidationException"). */
	private String code;

	/** Stato HTTP della response del provider. */
	private int httpStatus;

	public CanonicalError() {
	}

	public CanonicalError(CanonicalErrorType type, String message, String code, int httpStatus) {
		this.type = type;
		this.message = message;
		this.code = code;
		this.httpStatus = httpStatus;
	}

	public CanonicalErrorType getType() {
		return this.type;
	}

	public void setType(CanonicalErrorType type) {
		this.type = type;
	}

	public String getMessage() {
		return this.message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getCode() {
		return this.code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public int getHttpStatus() {
		return this.httpStatus;
	}

	public void setHttpStatus(int httpStatus) {
		this.httpStatus = httpStatus;
	}
}
