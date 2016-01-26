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

import java.sql.Connection;

import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.pdd.config.DBManager;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.config.Resource;
import org.openspcoop2.pdd.core.AbstractCore;
import org.openspcoop2.pdd.core.GestoreMessaggi;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.state.OpenSPCoopStateful;
import org.openspcoop2.pdd.logger.MsgDiagnostico;
import org.openspcoop2.pdd.services.RicezioneBuste;
import org.openspcoop2.pdd.services.RicezioneBusteMessage;
import org.openspcoop2.pdd.services.RicezioneContenutiApplicativi;
import org.openspcoop2.pdd.services.RicezioneContenutiApplicativiMessage;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.protocol.engine.driver.RepositoryBuste;
import org.openspcoop2.protocol.sdk.state.StateMessage;
import org.openspcoop2.protocol.sdk.state.StatefulMessage;

/**
 * Classe utilizzata per la ricezione di messaggi contenuti nell'architettura di OpenSPCoop (versione DB).
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class NodeReceiverDB extends AbstractCore implements INodeReceiver{


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
	public Object receive(MsgDiagnostico msgDiag, IDSoggetto codicePorta, String idModulo, String idMessaggio, 
			long timeout, long checkInterval) throws NodeException,NodeTimeoutException{

		/* ------------  Lettura parametri del messaggio ricevuto e ValidityCheck -------------- */
		Object objReturn = null;

		// connessione al database
		DBManager dbManager = DBManager.getInstance();
		Resource resource = null;
		Connection connectionDB = null;
						
		// GestoreMessaggi
		String tipoMessaggio = null;
		if(idModulo.startsWith(RicezioneContenutiApplicativi.ID_MODULO)){
			tipoMessaggio = Costanti.INBOX;
		}
		else if(idModulo.startsWith(RicezioneBuste.ID_MODULO)){
			tipoMessaggio = Costanti.OUTBOX;
		}
		
		OpenSPCoopStateful openspcoopstate = new OpenSPCoopStateful();
		openspcoopstate.setStatoRichiesta(new StatefulMessage(null, null));
		openspcoopstate.setStatoRisposta(new StatefulMessage(null, null));
		
		GestoreMessaggi gestoreMessaggioRichiesta = new GestoreMessaggi(openspcoopstate, false, idMessaggio,tipoMessaggio,msgDiag,null);

		try{
			long attesa = 0;
			String idRisposta = null;
			GestoreMessaggi gestoreMessaggioRisposta = null;
			boolean messaggioPresente = false;
			
			int refreshOnlyCacheCount = 0;
			
			while(attesa<timeout){

				refreshOnlyCacheCount++;
				msgDiag.highDebug("Analisi richiesta con ID ["+idMessaggio+"] tipo ["+tipoMessaggio+"] contatoreRif["+refreshOnlyCacheCount+"]");
				
				int nodeDBRefresh = NodeReceiverDB.openspcoopProperties.getNodeReceiverCheckDBInterval();
				int nodeRiferimentoMsgRefresh = NodeReceiverDB.openspcoopProperties.getNodeReceiverCheckDBInterval();
				try{
					if(nodeRiferimentoMsgRefresh>2)
						nodeRiferimentoMsgRefresh = nodeDBRefresh / 2;
				}catch(Exception e){
					msgDiag.highDebug("CheckDBInterval (proprieta' 'org.openspcoop.pdd.nodeReceiver.checkDB') non corretto: "+e.getMessage());
				}
				
				msgDiag.highDebug("Proprieta' nodeDBRefresh["+nodeDBRefresh+"] nodeRiferimentoMsgRefresh["+nodeRiferimentoMsgRefresh+"]");
				
				// Il riferimento messaggio e il proprietario puo' essere letto dalla cache.
				// Per migliorare le performance, se non e' presente nella cache si assume anche non presente nel database.
				// - Per rifMsg se si chiama il metodo mapRiferimentoIntoIDBusta(true) l'id viene cercato solo nella cache
				// - Per proprietario se si chiama il metodo getProprietario(true) viene cercato solo nella cache
				// La cache puo' cmq svuotarsi velocemente se la dimensione e' troppo piccola rispetto al numero di msg in parallelo gestiti.
				// Quindi ogni nodeDBRefresh volte (se checkInterval=500 ogni nodeDBRefresh*500 millisecondi) viene controllato anche il database, oltre alla cache.
				boolean checkOnlyCache = NodeReceiverDB.openspcoopProperties.isAbilitataCacheGestoreMessaggi();
				if(refreshOnlyCacheCount==(nodeDBRefresh+1)){
					msgDiag.highDebug("Re-inizializzo contatore refreshOnlyCacheCount");
					refreshOnlyCacheCount = 1;
					checkOnlyCache = false;
				}
				
				// Il riferimentoMessaggio puo' cambiare l'associazione nel tempo.
				// Es. rispostaSincrona che e' un MessaggioErroreProtocollo.... prima viene salvato l'errore, e poi viene salvato il msg ritornato al servizio applicativo.
				// Entrambi, in ordine avranno come key il rifMessaggio.
				// Ogni (nodeDBRefresh/2) volte (se checkInterval=500 ogni (nodeDBRefresh/2)*500 millisecondi) viene fatto il refresh del messaggio
				if( (refreshOnlyCacheCount%nodeRiferimentoMsgRefresh)==0 ){
					msgDiag.highDebug("Re-inizializzo idRisposta e gestoreMessaggioRisposta");
					idRisposta = null;
					gestoreMessaggioRisposta = null;
				}
				
				// Prendo la connessione solo se mi serve per la ricerca su database
				boolean needConnection = false;
				if(NodeReceiverDB.openspcoopProperties.singleConnection_TransactionManager()==false)
					needConnection = true;
				else if(resource==null)
					needConnection = true;
				if( (checkOnlyCache==false) && needConnection ){
					msgDiag.highDebug("Prendo Connessione per NodeReceiver");
					try{
						resource = dbManager.getResource(codicePorta,idModulo, PdDContext.getValue(org.openspcoop2.core.constants.Costanti.CLUSTER_ID, this.getPddContext()));
					}catch(Exception e){
						throw new NodeException("Impossibile ottenere una Risorsa dal DBManager",e);
					}
					if(resource==null)
						throw new NodeException("Risorsa is null");
					if(resource.getResource() == null)	
						throw new NodeException("Connessione is null");
					connectionDB = (Connection) resource.getResource();
					((StateMessage)openspcoopstate.getStatoRichiesta()).setConnectionDB(connectionDB);
					((StateMessage)openspcoopstate.getStatoRisposta()).setConnectionDB(connectionDB);
				}
				
				// Check esistenza messaggio
				if(idRisposta==null){
					msgDiag.highDebug("Analisi richiesta con ID ["+idMessaggio+"] tipo ["+tipoMessaggio+"]: lettura ID Risposta");
					idRisposta = gestoreMessaggioRichiesta.mapRiferimentoIntoIDBusta(checkOnlyCache); //only cache
				}
				if(idRisposta!=null){
					if(gestoreMessaggioRisposta==null){
						msgDiag.highDebug("Analisi richiesta con ID ["+idMessaggio+"] tipo ["+tipoMessaggio+"]: Costruisco GestoreRisposta per ID["+idRisposta+"]");
						gestoreMessaggioRisposta = new GestoreMessaggi(openspcoopstate, false, idRisposta,tipoMessaggio,msgDiag,null);
						msgDiag.highDebug("Analisi risposta con ID ["+idRisposta+"] tipo ["+tipoMessaggio+"]: existsMessage("+checkOnlyCache+")");
						if(checkOnlyCache)
							messaggioPresente = gestoreMessaggioRisposta.existsMessage_onlyCache();
						else
							messaggioPresente = gestoreMessaggioRisposta.existsMessage();
					}else{
						if(messaggioPresente==false){
							msgDiag.highDebug("Analisi risposta con ID ["+idRisposta+"] tipo ["+tipoMessaggio+"]: existsMessage("+checkOnlyCache+")");
							if(checkOnlyCache)
								messaggioPresente = gestoreMessaggioRisposta.existsMessage_onlyCache();
							else
								messaggioPresente = gestoreMessaggioRisposta.existsMessage();
						}
					}
				}
				
				msgDiag.highDebug("Analisi risposta con ID ["+idRisposta+"] tipo ["+tipoMessaggio+"]: existsMessage="+messaggioPresente);
				
				
				if(messaggioPresente){

					// read Proprietario
					/*String proprietario = null;
					if( idModulo.startsWith(RicezioneContenutiApplicativi.ID_MODULO)) {
						proprietario = gestoreMessaggioRisposta.getProprietario_SerializableRead(NodeReceiverDB.openspcoopProperties.getGestioneSerializableDB_AttesaAttiva(),NodeReceiverDB.openspcoopProperties.getGestioneSerializableDB_CheckInterval());
					} else {
						proprietario = gestoreMessaggioRisposta.getProprietario();
					}*/
					msgDiag.highDebug("getProprietario("+checkOnlyCache+")");
					String proprietario = gestoreMessaggioRisposta.getProprietario(idModulo,checkOnlyCache);
					msgDiag.highDebug("getProprietario("+checkOnlyCache+") proprietario="+proprietario);
					
					// check proprietario
					if( idModulo.startsWith(RicezioneContenutiApplicativi.ID_MODULO)) {
						messaggioPresente = idModulo.equals(proprietario);
					}else if( idModulo.startsWith(RicezioneBuste.ID_MODULO)) {
						messaggioPresente = idModulo.equals(proprietario);
					}
					msgDiag.highDebug("Analisi risposta con ID ["+idRisposta+"] tipo ["+tipoMessaggio+"] proprietario["+proprietario+" existsMessage="+messaggioPresente);
					
				}
				
				// Gestione messaggio
				if(messaggioPresente==false){
					
					//	rilascio e riprendo la connessione ogni checkInterval fino ad un timeout o alla ricezione di un oggetto
					if( (NodeReceiverDB.openspcoopProperties.singleConnection_NodeReceiver()==false) && (checkOnlyCache==false) ){
						msgDiag.highDebug("Rilascio connessione per NodeReceiver");
						dbManager.releaseResource(codicePorta, idModulo, resource);
					}
					
					msgDiag.highDebug("Sleep...");
					try{
						Thread.sleep(checkInterval);
					}catch(Exception e){}
					attesa = attesa + checkInterval;

				}else{
					
					// Prendo informazioni da RepositoryBuste, se non era settata la connesione, la setto
					if( needConnection && checkOnlyCache){
						msgDiag.highDebug("Prendo Connessione per NodeReceiver");
						try{
							resource = dbManager.getResource(codicePorta,idModulo, PdDContext.getValue(org.openspcoop2.core.constants.Costanti.CLUSTER_ID, this.getPddContext()));
						}catch(Exception e){
							throw new NodeException("Impossibile ottenere una Risorsa dal DBManager",e);
						}
						if(resource==null)
							throw new NodeException("Risorsa is null");
						if(resource.getResource() == null)
							throw new NodeException("Connessione is null");
						connectionDB = (Connection) resource.getResource();
					}
					
					StatefulMessage state = new StatefulMessage(connectionDB, null);
					if(idModulo.startsWith(RicezioneContenutiApplicativi.ID_MODULO)){
						msgDiag.highDebug("Lettura risposta per RicezioneContenutiApplicativi...");
						objReturn = new RicezioneContenutiApplicativiMessage();
						((RicezioneContenutiApplicativiMessage)objReturn).setIdBustaRisposta(idRisposta);
						RepositoryBuste repositoryBuste = new RepositoryBuste(state, false,null);
						((RicezioneContenutiApplicativiMessage)objReturn).setIdCollaborazione(repositoryBuste.getCollaborazioneFromInBox(idRisposta));
						((RicezioneContenutiApplicativiMessage)objReturn).setProfiloCollaborazione(repositoryBuste.getProfiloCollaborazioneFromInBox(idRisposta),
								repositoryBuste.getProfiloCollaborazioneValueFromInBox(idRisposta));
						try{
							((RicezioneContenutiApplicativiMessage)objReturn).setPddContext(gestoreMessaggioRisposta.getPdDContext());
						}catch(Exception e){}
						msgDiag.highDebug("Lettura risposta per RicezioneContenutiApplicativi effettuata");
					}
					else if(idModulo.startsWith(RicezioneBuste.ID_MODULO)){
						msgDiag.highDebug("Lettura risposta per RicezioneBuste...");
						objReturn = new RicezioneBusteMessage();
						RepositoryBuste repositoryBuste = new RepositoryBuste(state, false,null);
						if(repositoryBuste.isRegistrataIntoOutBox(idRisposta)){
							((RicezioneBusteMessage)objReturn).setBustaRisposta(repositoryBuste.getBustaFromOutBox(idRisposta));
						}else{
							((RicezioneBusteMessage)objReturn).setIdMessaggioSblocco(idRisposta);
						}
						try{
							((RicezioneBusteMessage)objReturn).setPddContext(gestoreMessaggioRisposta.getPdDContext());
						}catch(Exception e){}
						msgDiag.highDebug("Lettura risposta per RicezioneBuste effettuata");
					}
					
					// rilascio e riprendo la connessione ogni checkInterval fino ad un timeout o alla ricezione di un oggetto
					if( (NodeReceiverDB.openspcoopProperties.singleConnection_NodeReceiver()==false) && (checkOnlyCache==false) ){
						msgDiag.highDebug("Rilascio connessione per NodeReceiver");
						dbManager.releaseResource(codicePorta, idModulo, resource);
					}
					
					msgDiag.highDebug("Fine Lettura");
					break;
				}

			}

		} catch (Exception e) {
			throw new NodeException("Riscontrato errore nella ricezione del messaggio di risposta per la gestione della richiesta:"
					+e.getMessage(),e);
		} finally{
			msgDiag.highDebug("Rilascio connessione per NodeReceiver");
			dbManager.releaseResource(codicePorta, idModulo, resource);
		}
		
		if(objReturn == null){
			throw new NodeTimeoutException("Riscontrato errore durante ricezione del messaggio: Messaggio non ricevuto");
		}
		
		return objReturn;
	}

}