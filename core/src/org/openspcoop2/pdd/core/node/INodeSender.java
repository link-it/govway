/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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



package org.openspcoop2.pdd.core.node;

import java.io.Serializable;

import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.pdd.core.GestoreMessaggi;
import org.openspcoop2.pdd.core.ICore;
import org.openspcoop2.pdd.logger.MsgDiagnostico;

/**
 * Classe utilizzata per la spedizione di messaggi contenuti nell'architettura di OpenSPCoop.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public interface INodeSender extends ICore {
	
	/**
	 * Spedizione di un messaggio  
	 *
	 * @param msg Messaggio
	 * @param destinazione Modulo di destinazione del msg
	 * @param codicePorta Codice Porta per cui effettuare la receive
	 * @param idModulo Nodo destinatario per cui effettuare la ricezione. 
	 * @param idMessaggio Identificativo del messaggio
	 * 
	 */
	public void send(Serializable msg,String destinazione, MsgDiagnostico msgDiag,IDSoggetto codicePorta,
			String idModulo,String idMessaggio, GestoreMessaggi gm) throws NodeException;	
}
