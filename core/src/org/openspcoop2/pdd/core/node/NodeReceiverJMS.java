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

import org.slf4j.Logger;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.AbstractCore;
import org.openspcoop2.pdd.core.JMSReceiver;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.logger.MsgDiagnostico;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.pdd.services.RicezioneBuste;
import org.openspcoop2.pdd.services.RicezioneBusteMessage;
import org.openspcoop2.pdd.services.RicezioneContenutiApplicativi;
import org.openspcoop2.pdd.services.RicezioneContenutiApplicativiMessage;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.protocol.sdk.Busta;

/**
 * Classe utilizzata per la ricezione di messaggi contenuti nell'architettura di OpenSPCoop (versione JMS).
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class NodeReceiverJMS extends AbstractCore implements INodeReceiver{

	private static OpenSPCoop2Properties openspcoopProperties = 
		OpenSPCoop2Properties.getInstance();
	
	/**
	 * Ricezione di un messaggio  
	 *
	 * @param codicePorta Codice Porta per cui effettuare la receive
	 * @param idModulo Nodo destinatario per cui effettuare la ricezione. 
	 * @param timeout Timeout sulla ricezione
	 * @param checkInterval Intervallo di check sulla coda
	 * @return true se la ricezione JMS e' andata a buon fine, false altrimenti.
	 * 
	 */
	@Override
	public Object receive(MsgDiagnostico msgDiag, IDSoggetto codicePorta,String idModulo,String idMessaggio, 
			long timeout,long checkInterval) throws NodeException,NodeTimeoutException{
		/* ------------  Lettura parametri del messaggio ricevuto e ValidityCheck -------------- */
		Object objReturn = null;
		Logger log = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();
		try{
			JMSReceiver receiverJMS = new JMSReceiver(codicePorta,idModulo,NodeReceiverJMS.openspcoopProperties.singleConnection_NodeReceiver(),log, PdDContext.getValue(org.openspcoop2.core.constants.Costanti.CLUSTER_ID, this.getPddContext()));
			while(true){ // un msg deve essere ricevuto per forza.
				
				String strMessageSelector = "ID = '"+idMessaggio+"'";
				msgDiag.highDebug("Ricezione messaggio selector["+strMessageSelector+"]....");
				if(receiverJMS.receive(idModulo,strMessageSelector,
						timeout,checkInterval) == false){
					throw new javax.jms.JMSException(receiverJMS.getErrore());
				}
				msgDiag.highDebug("Ricezione effettuata");
				objReturn = receiverJMS.getObjectReceived();	
				if(objReturn == null)
					throw new javax.jms.JMSException("Oggetto ricevuto dalla coda jms is null");
				
				String idRisposta = null;
				String tipoMessaggio = null;
				if(idModulo.startsWith(RicezioneContenutiApplicativi.ID_MODULO)){
					msgDiag.highDebug("Lettura risposta per RicezioneContenutiApplicativi...");
					idRisposta = ((RicezioneContenutiApplicativiMessage)objReturn).getIdBustaRisposta();
					tipoMessaggio = Costanti.INBOX;
					msgDiag.highDebug("Lettura risposta per RicezioneContenutiApplicativi effettuata");
				}
				else if(idModulo.startsWith(RicezioneBuste.ID_MODULO)){
					msgDiag.highDebug("Lettura risposta per RicezioneBuste...");
					Busta bustaRisposta =  ((RicezioneBusteMessage)objReturn).getBustaRisposta();
					if(bustaRisposta!=null)
						idRisposta = bustaRisposta.getID();
					else
						idRisposta = ((RicezioneBusteMessage)objReturn).getIdMessaggioSblocco();
					tipoMessaggio = Costanti.OUTBOX;
					msgDiag.highDebug("Lettura risposta per RicezioneBuste effettuata");
				}
				if(TransactionManager.validityCheck(msgDiag,idModulo,idRisposta,
						tipoMessaggio,receiverJMS.getIdHeaderJMS(),this.getPddContext())==false){
					msgDiag.highDebug("Messaggio con id["+idMessaggio+"] non ha superato il validity check");
					log.error("Messaggio con id["+idMessaggio+"] non ha superato il validity check");
				}else{
					break;
				}
			}
		} catch (Exception e) {
			if(e.getMessage()!=null){
				if(e.getMessage().indexOf("Messaggio non ricevuto")>=0){
					throw new NodeTimeoutException("Risposta per la gestione della richiesta, "
							+e.getMessage(),e);
				}else{
					throw new NodeException("Riscontrato errore nella ricezione del messaggio di risposta per la gestione della richiesta:"
							+e.getMessage(),e);
				}
			}else{
				throw new NodeException("Riscontrato errore nella ricezione del messaggio di risposta per la gestione della richiesta",e);
			}
			
		}
		return objReturn;
	}
	
}
