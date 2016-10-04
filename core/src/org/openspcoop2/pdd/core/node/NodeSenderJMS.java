/*
 * OpenSPCoop - Customizable API Gateway 
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

import java.io.Serializable;

import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.pdd.core.AbstractCore;
import org.openspcoop2.pdd.core.GestoreMessaggi;
import org.openspcoop2.pdd.core.JMSSender;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.logger.MsgDiagnostico;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;

/**
 * Classe utilizzata per la spedizione di messaggi contenuti nell'architettura di OpenSPCoop (versione JMS).
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class NodeSenderJMS extends AbstractCore implements INodeSender{

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
	@Override
	public void send(Serializable msg, String destinazione, MsgDiagnostico msgDiag,
			IDSoggetto codicePorta, String idModulo, String idMessaggio, GestoreMessaggi gm)
			throws NodeException {
		try{
			JMSSender senderJMS = new JMSSender(codicePorta,idModulo,OpenSPCoop2Logger.getLoggerOpenSPCoopCore(), PdDContext.getValue(org.openspcoop2.core.constants.Costanti.CLUSTER_ID, this.getPddContext()));
			if(senderJMS.send(destinazione,
					msg,idMessaggio) == false){
				if(senderJMS.getException()!=null)
					throw new Exception("Spedizione jms con errore: "+ senderJMS.getErrore(),senderJMS.getException());
				else
					throw new Exception("Spedizione jms con errore: "+ senderJMS.getErrore());
			}else{
				msgDiag.highDebug("ObjectMessage send via JMS.");
			}
		} catch (Exception e) {
			throw new NodeException(e.getMessage(),e);
			
		}
	}

}
