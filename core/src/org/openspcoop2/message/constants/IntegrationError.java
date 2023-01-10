/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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

package org.openspcoop2.message.constants;

import java.io.Serializable;

/**
 * IntegrationError
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public enum IntegrationError implements Serializable {
	
	// 4xx errore del client
	AUTHENTICATION, 
	AUTHORIZATION, 
	NOT_FOUND, 
	BAD_REQUEST, 
	CONFLICT,
	REQUEST_TOO_LARGE,
	LIMIT_EXCEEDED,
	TOO_MANY_REQUESTS, 
	
	// backend non raggiungibile
	SERVICE_UNAVAILABLE,
	ENDPOINT_REQUEST_TIMED_OUT,
	
	// risposta ritornata ha generato un errore durante il suo processamento
	BAD_RESPONSE,
	
	// errore generico avvenuto durante la gestione della richiesta
	INTERNAL_REQUEST_ERROR,
	
	// errore generico avvenuto durante la gestione della risposta
	INTERNAL_RESPONSE_ERROR,
	
	// default
	DEFAULT;
	
}
