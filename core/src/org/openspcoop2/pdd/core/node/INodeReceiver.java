/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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



package org.openspcoop2.pdd.core.node;

import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.pdd.core.ICore;
import org.openspcoop2.pdd.logger.MsgDiagnostico;

/**
 * Classe utilizzata per la ricezione di messaggi contenuti nell'architettura di OpenSPCoop.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public interface INodeReceiver extends ICore {
	
	/**
	 * Ricezione di un messaggio  
	 *
	 * @param codicePorta Codice Porta per cui effettuare la receive
	 * @param idModulo Nodo destinatario per cui effettuare la ricezione. 
	 * @param idMessaggio Identificativo del messaggio
	 * @param timeout Timeout sulla ricezione
	 * @param checkInterval Intervallo di check sulla coda
	 * @return true se la ricezione JMS e' andata a buon fine, false altrimenti.
	 * 
	 */
	public Object receive(MsgDiagnostico msgDiag, IDSoggetto codicePorta,String idModulo,String idMessaggio,
			long timeout,long checkInterval) throws NodeException,NodeTimeoutException;
	
}
