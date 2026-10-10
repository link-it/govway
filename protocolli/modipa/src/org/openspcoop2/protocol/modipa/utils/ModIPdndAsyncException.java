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

package org.openspcoop2.protocol.modipa.utils;

import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.IntegrationFunctionError;

/**
 * Errore rilevato nella gestione degli scambi di dati asincroni PDND, con l'indicazione dell'errore da restituire al client
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ModIPdndAsyncException extends ProtocolException {

	private static final long serialVersionUID = 1L;

	public ModIPdndAsyncException(IntegrationFunctionError integrationFunctionError, String msg) {
		super(msg);
		this.setIntegrationFunctionError(integrationFunctionError);
	}
	public ModIPdndAsyncException(IntegrationFunctionError integrationFunctionError, String msg, Throwable cause) {
		super(msg, cause);
		this.setIntegrationFunctionError(integrationFunctionError);
	}
	
	public static ModIPdndAsyncException notFound(String msg) {
		return new ModIPdndAsyncException(IntegrationFunctionError.ASYNC_INTERACTION_NOT_FOUND, msg);
	}
	public static ModIPdndAsyncException expired(String msg) {
		return new ModIPdndAsyncException(IntegrationFunctionError.ASYNC_INTERACTION_EXPIRED, msg);
	}
	public static ModIPdndAsyncException invalidState(String msg) {
		return new ModIPdndAsyncException(IntegrationFunctionError.ASYNC_INTERACTION_INVALID_STATE, msg);
	}
	public static ModIPdndAsyncException invalidRequest(String msg) {
		return new ModIPdndAsyncException(IntegrationFunctionError.ASYNC_INTERACTION_INVALID_REQUEST, msg);
	}
	/**
	 * Errore non imputabile al client (es. configurazione incompleta o errore nell'accesso alle informazioni): viene restituito un errore 5xx
	 */
	public static ModIPdndAsyncException configurationError(String msg) {
		return new ModIPdndAsyncException(IntegrationFunctionError.INTERNAL_REQUEST_ERROR, msg);
	}
	public static ModIPdndAsyncException internalError(String msg, Throwable cause) {
		return new ModIPdndAsyncException(IntegrationFunctionError.INTERNAL_REQUEST_ERROR, msg, cause);
	}
}
