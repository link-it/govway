/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it).
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
package org.openspcoop2.utils.logger.constants.context;

import java.io.Serializable;

/**
 * Result
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public enum Result implements Serializable {

	SUCCESS, 
	
	CLIENT_ERROR,  // errore causato da informazioni errate (o mancanti) fornite dal client 
	CLIENT_CONNECTION_ERROR, // la connessione del Client che ha scaturito la richiesta non è più disponibile
	
	SERVER_ERROR, // errore avvenuto durante l'invio della richiesta o la lettura della risposta da un server
	SERVER_CONNECTION_ERROR, // server non contattabile a causa di problemi di connessione (es. connection refused, connection timed out ...)
	SERVER_APPLICATION_ERROR, // il server contattato ha restituito un errore applicativo utilizzando standard noti (SOAPFault, Problem RFC 7807)
	
	PROCESSING_ERROR, // errore avvenuto durante il processamento della richiesta
	
	INTERNAL_ERROR  // errore inatteso (es. database non disponibile)

	;  
	
}
