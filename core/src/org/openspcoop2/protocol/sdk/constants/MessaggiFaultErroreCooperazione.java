/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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
package org.openspcoop2.protocol.sdk.constants;

import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;

/**
*
* @author Poli Andrea (apoli@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public enum MessaggiFaultErroreCooperazione {

	FAULT_STRING_PROCESSAMENTO("300 - Errore nel processamento del messaggio di cooperazione"),
    FAULT_STRING_PROCESSAMENTO_SENZA_CODICE("Errore nel processamento del messaggio di cooperazione"),
    FAULT_STRING_VALIDAZIONE("001 - Formato Busta non corretto"),
	FAULT_STRING_VALIDAZIONE_SENZA_CODICE("Formato Busta non corretto");
    
	private final String msg;

	MessaggiFaultErroreCooperazione(String msg){
		this.msg = msg;
	}

	@Override
	public String toString(){
		return this.msg;
	}
	
	public String toString(IProtocolFactory<?> protocolFactory) throws ProtocolException {
		return protocolFactory.createTraduttore().toString(this);
	}

	public boolean equals(String o){
		return o.equals(this.toString());
	}
}
