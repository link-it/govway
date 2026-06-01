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
package org.openspcoop2.utils.transport.http;

/**
 * Strategia di gestione di un Content-Type 'multipart/related' privo del parametro 'type'
 * obbligatorio per RFC 2387 §3.1, W3C SOAP-with-Attachments §3 e WS-I AP 1.0 R2904.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public enum MultipartMissingTypeBehavior {

	/** Nessuna compensazione: il messaggio viene lasciato al classifier standard. */
	NONE,

	/**
	 * Deriva il parametro 'type' dalla versione SOAP della richiesta associata.
	 * Applicabile solo lato risposta. Sul lato richiesta cade su NONE.
	 */
	INFER_FROM_REQUEST,

	/**
	 * Ispeziona i primi byte del payload alla ricerca del Content-Type
	 * della root part del multipart (RFC 2046).
	 */
	INFER_FROM_BODY,

	/** Forza il parametro 'type' a "text/xml" (SOAP 1.1). */
	FORCE_SOAP11,

	/** Forza il parametro 'type' a "application/soap+xml" (SOAP 1.2). */
	FORCE_SOAP12;

	/**
	 * Forma canonica del valore così come accettata nelle property (camelCase).
	 * Es. {@code INFER_FROM_REQUEST} → "inferFromRequest".
	 */
	public String getValue() {
		switch (this) {
		case NONE:
			return "none";
		case INFER_FROM_REQUEST:
			return "inferFromRequest";
		case INFER_FROM_BODY:
			return "inferFromBody";
		case FORCE_SOAP11:
			return "forceSoap11";
		case FORCE_SOAP12:
			return "forceSoap12";
		}
		return this.name();
	}

	/**
	 * Mappa una stringa case-insensitive nel valore enum.
	 * @return l'enum corrispondente oppure null se il valore non è riconosciuto.
	 */
	public static MultipartMissingTypeBehavior parse(String value) {
		if (value == null) {
			return null;
		}
		String v = value.trim();
		if (v.isEmpty()) {
			return null;
		}
		for (MultipartMissingTypeBehavior b : values()) {
			if (b.name().equalsIgnoreCase(v)) {
				return b;
			}
		}
		// alias con notazione camelCase più amichevole per le property
		if ("none".equalsIgnoreCase(v)) {
			return NONE;
		}
		if ("inferFromRequest".equalsIgnoreCase(v)) {
			return INFER_FROM_REQUEST;
		}
		if ("inferFromBody".equalsIgnoreCase(v)) {
			return INFER_FROM_BODY;
		}
		if ("forceSoap11".equalsIgnoreCase(v)) {
			return FORCE_SOAP11;
		}
		if ("forceSoap12".equalsIgnoreCase(v)) {
			return FORCE_SOAP12;
		}
		return null;
	}
}
