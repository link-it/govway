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
import org.openspcoop2.pdd.core.AbstractCore;
import org.openspcoop2.pdd.core.GestoreMessaggi;
import org.openspcoop2.pdd.core.JMSSender;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.logger.MsgDiagnostico;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.pdd.mdb.GenericMessage;
import org.openspcoop2.protocol.sdk.state.RequestInfo;
import org.openspcoop2.protocol.sdk.state.RequestInfoConfigUtilities;

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
		
		// Elimino dalla RequestInfo i dati "cached"
		RequestInfo requestInfoBackup = null;
		PdDContext pddContext = null;
		java.util.Map<String,Object> dynamicContext = null;
		if(msg instanceof GenericMessage) {
			GenericMessage mm = (GenericMessage) msg;
			pddContext = mm.getPddContext();
			if(pddContext!=null) {
				requestInfoBackup =	RequestInfoConfigUtilities.normalizeRequestInfoBeforeSerialization(pddContext);
				dynamicContext = org.openspcoop2.core.constants.Costanti.removeDynamicMap(pddContext);
			}
		}		
		try{
			
			JMSSender senderJMS = new JMSSender(codicePorta,idModulo,OpenSPCoop2Logger.getLoggerOpenSPCoopCore(), PdDContext.getValue(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE, this.getPddContext()));
			if(!senderJMS.send(destinazione,
					msg,idMessaggio)){
				if(senderJMS.getException()!=null)
					throw new NodeException("Spedizione jms con errore: "+ senderJMS.getErrore(),senderJMS.getException());
				else
					throw new NodeException("Spedizione jms con errore: "+ senderJMS.getErrore());
			}else{
				msgDiag.highDebug("ObjectMessage send via JMS.");
			}
		} catch (Exception e) {
			throw new NodeException(e.getMessage(),e);
			
		}finally {
			if(requestInfoBackup!=null) {
				RequestInfoConfigUtilities.restoreRequestInfoAfterSerialization(pddContext, requestInfoBackup);
			}
			if(dynamicContext!=null) {
				pddContext.put(org.openspcoop2.core.constants.Costanti.DYNAMIC_MAP_CONTEXT, dynamicContext);
			}
		}
	}

}
