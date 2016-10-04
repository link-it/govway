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

import java.sql.Connection;

import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.pdd.config.DBManager;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.config.Resource;
import org.openspcoop2.pdd.core.GestoreMessaggi;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.state.OpenSPCoopStateful;
import org.openspcoop2.pdd.logger.MsgDiagnostico;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.pdd.mdb.ConsegnaContenutiApplicativi;
import org.openspcoop2.pdd.mdb.Imbustamento;
import org.openspcoop2.pdd.mdb.ImbustamentoRisposte;
import org.openspcoop2.pdd.mdb.InoltroBuste;
import org.openspcoop2.pdd.mdb.InoltroRisposte;
import org.openspcoop2.pdd.mdb.Sbustamento;
import org.openspcoop2.pdd.mdb.SbustamentoRisposte;
import org.openspcoop2.pdd.services.RicezioneBuste;
import org.openspcoop2.pdd.services.RicezioneContenutiApplicativi;
import org.openspcoop2.protocol.sdk.state.StatefulMessage;
import org.openspcoop2.utils.date.DateManager;


/**
 * Classe che implementa la logica transazionale di OpenSPCoop
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class TransactionManager {

	/** Logger utilizzato per debug. */
	private static org.slf4j.Logger log = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();
	
	/** Variabile che indica il Nome del modulo dell'architettura di OpenSPCoop rappresentato da questa classe */
	public final static String ID_MODULO = "TransactionManager";

	/**
	 * Quando un modulo di OpenSPCoop chiama questo metodo, si aspetta come risposta una indicazione
	 * se utilizzare o meno il messaggio ricevuto.
	 *
	 * @param idModulo Identificativo del Modulo OpenSPCoop
	 * @param idBusta Identificativo del Messaggio Ricevuto
	 * @param tipo Tipo di Messaggio da gestire (INBOX/OUTBOX)
	 * @param idJMS ID JMS del Messaggio ricevuto
	 * @return Il metodo ritorna true se il modulo e' autorizzato a processare il messaggio, false altrimenti.
	 * 
	 */
	public static boolean validityCheck(MsgDiagnostico msgDiag,String idModulo,String idBusta,String tipo,
			String idJMS, PdDContext pddContext) throws Exception{
		return TransactionManager.validityCheck(msgDiag,idModulo,idBusta,tipo,idJMS,pddContext,null);
	}

	/**
	 * Quando un modulo di OpenSPCoop chiama questo metodo, si aspetta come risposta una indicazione
	 * se utilizzare o meno il messaggio ricevuto.
	 *
	 * @param idModulo Identificativo del Modulo OpenSPCoop
	 * @param idBusta Identificativo del Messaggio Ricevuto
	 * @param tipo Tipo di Messaggio da gestire (INBOX/OUTBOX)
	 * @param idJMS ID JMS del Messaggio ricevuto
	 * @param servizioApplicativo Servizio Applicativo utilizzato come chiave insieme all'id
	 * @return Il metodo ritorna true se il modulo e' autorizzato a processare il messaggio, false altrimenti.
	 * 
	 */
	public static boolean validityCheck(MsgDiagnostico msgDiag,String idModulo,String idBusta,String tipo,
			String idJMS, PdDContext pddContext,String servizioApplicativo) throws Exception{
		
		// Risorse
		OpenSPCoop2Properties properties = OpenSPCoop2Properties.getInstance();
		IDSoggetto dominio = properties.getIdentitaPortaDefault(null);
		String idModuloTransaction = TransactionManager.ID_MODULO + "_" + idModulo;
		
		DBManager dbManager = DBManager.getInstance();
		Resource resource = null;

		OpenSPCoopStateful openspcoopstate = new OpenSPCoopStateful();
		StatefulMessage state = new StatefulMessage(null, TransactionManager.log);
		openspcoopstate.setStatoRichiesta(state);
		GestoreMessaggi msg = new GestoreMessaggi(openspcoopstate, true,idBusta,tipo,msgDiag,null);
		
		long scadenzaWhile = DateManager.getTimeMillis() + properties.getTransactionManager_AttesaAttiva();

		
		try{

			// Algoritmo 
			int refreshOnlyCacheCount = 0;
			
			while( DateManager.getTimeMillis() < scadenzaWhile  ){
				
				refreshOnlyCacheCount++;
				// Il riferimento messaggio e il proprietario puo' essere letto dalla cache.
				// Per migliorare le performance, se non e' presente nella cache si assume anche non presente nel database.
				// - Per rifMsg se si chiama il metodo mapRiferimentoIntoIDBusta(true) l'id viene cercato solo nella cache
				// - Per proprietario se si chiama il metodo getProprietario(true) viene cercato solo nella cache
				// La cache puo' cmq svuotarsi velocemente se la dimensione e' troppo piccola rispetto al numero di msg in parallelo gestiti.
				// Quindi ogni 20 volte (se checkInterval=500 ogni 10 secondi) viene controllato anche il database, oltre alla cache.
				boolean checkOnlyCache = properties.isAbilitataCacheGestoreMessaggi();
				if(refreshOnlyCacheCount==(properties.getTransactionManager_CheckDBInterval())){
					msgDiag.highDebug("Re-inizializzo contatore refreshOnlyCacheCount");
					refreshOnlyCacheCount = 1;
					checkOnlyCache = false;
				}
				
				msgDiag.highDebug("Transaction su IDM["+idModulo+"] IDBusta["+idBusta+"] Tipo["+
						tipo+"] IDJMS["+idJMS+"] SA["+servizioApplicativo+"] ancora interval["
						+properties.getTransactionManager_CheckInterval()+"]  millisecondi "+(scadenzaWhile-DateManager.getTimeMillis()));
				
				boolean needConnection = false;
				if(properties.singleConnection_TransactionManager()==false)
					needConnection = true;
				else if(resource==null)
					needConnection = true;
				if( (checkOnlyCache==false) && needConnection ){
					msgDiag.highDebug("Prendo Connessione per TransactionManager");
					try{
						resource = dbManager.getResource(dominio,idModuloTransaction,PdDContext.getValue(org.openspcoop2.core.constants.Costanti.CLUSTER_ID, pddContext));
					}catch(Exception e){
						throw new NodeException("Impossibile ottenere una Risorsa dal DBManager",e);
					}
					if(resource==null)
						throw new NodeException("Risorsa is null");
					if(resource.getResource() == null)
						throw new NodeException("Connessione is null");
					Connection connectionDB = (Connection) resource.getResource();
					state.setConnectionDB(connectionDB);
			
				}
				
				msgDiag.highDebug("getProprietario("+checkOnlyCache+")");
				String proprietarioMessaggio = msg.getProprietario(idModulo,checkOnlyCache);
				msgDiag.highDebug("getProprietario("+checkOnlyCache+") proprietario="+proprietarioMessaggio);
			
				/*
				if( (idModulo.startsWith(RicezioneContenutiApplicativi.ID_MODULO)) ||
					(ConsegnaContenutiApplicativi.ID_MODULO.equals(idModulo)) ){
					//log.info("getProprietarioSerializable");
					proprietarioMessaggio = msg.getProprietario_SerializableRead(properties.getGestioneSerializableDB_AttesaAttiva(),
							properties.getGestioneSerializableDB_CheckInterval());
				} else {
					//log.info("getProprietario");
					proprietarioMessaggio = msg.getProprietario();
				}
				*/
				
				if(proprietarioMessaggio==null){
					// N.B.: anche se il thread eliminazione messaggi elimina un messaggio, poi comunque dopo 500 tentativi il 
					//       messaggio verra' eliminato!
					msgDiag.highDebug("Messaggio per il modulo ["+idModulo+"]: Attesa attiva, modulo precendente [Punto di inizio]");
					// Attesa attiva del modulo:
					if((properties.singleConnection_TransactionManager()==false) && (checkOnlyCache==false)){
						msgDiag.highDebug("Rilascio connessione per TransactionManager");
						dbManager.releaseResource(dominio,idModuloTransaction,resource);
					}
					try{
						//Thread.sleep((new java.util.Random()).nextInt(properties.getTransactionManager_CheckInterval())); // random da 0ms a TransactionManagerCheckInterval ms
						Thread.sleep(properties.getTransactionManager_CheckInterval());
					}catch(Exception eRandom){}
				}
				else if(proprietarioMessaggio.equals(idModulo)){
					msgDiag.highDebug("Messaggio per il modulo ["+idModulo+"]: proprietario["+proprietarioMessaggio+"] OK");
					if( (properties.singleConnection_TransactionManager() && (resource!=null) ) || (checkOnlyCache==false) ){
						msgDiag.highDebug("Rilascio connessione per TransactionManager");
						dbManager.releaseResource(dominio,idModuloTransaction,resource);
					}
					return true; // messaggio per il modulo
				}
				else if( TransactionManager.isModuloPrecedente(idModulo,proprietarioMessaggio,false) == false ){
					msgDiag.highDebug("Messaggio per il modulo ["+idModulo+"]: proprietario["+proprietarioMessaggio+"], scarta");
					if( (properties.singleConnection_TransactionManager() && (resource!=null) ) || (checkOnlyCache==false) ){
						msgDiag.highDebug("Rilascio connessione per TransactionManager");
						dbManager.releaseResource(dominio,idModuloTransaction,resource);
					}
					return false; // scarta messaggio poiche' il messaggio e' gia stato consegnato al modulo successivo
				}
				else{
					// Se avevo onlyCache, non possedevo la connessione
					if(needConnection && checkOnlyCache){
						msgDiag.highDebug("Prendo Connessione per TransactionManager NeedForJMS");
						try{
							resource = dbManager.getResource(dominio,idModuloTransaction,PdDContext.getValue(org.openspcoop2.core.constants.Costanti.CLUSTER_ID, pddContext));
						}catch(Exception e){
							throw new NodeException("Impossibile ottenere una Risorsa dal DBManager",e);
						}
						if(resource==null)
							throw new NodeException("Risorsa is null");
						if(resource.getResource() == null)
							throw new NodeException("Connessione is null");
						Connection connectionDB = (Connection) resource.getResource();
						state.setConnectionDB(connectionDB); 
						
					}
					
					// Aggiornamento primo msg ricevuto
					String idJMSRicevutoPrecedentemente = null;
					if( ConsegnaContenutiApplicativi.ID_MODULO.equals(idModulo)  ){
						idJMSRicevutoPrecedentemente = msg.getIDJMSRicevuto(idModulo,servizioApplicativo);
					}else{
						idJMSRicevutoPrecedentemente =msg.getIDJMSRicevuto(idModulo);
					}

					if(idJMSRicevutoPrecedentemente == null){
						// Se null, e' la prima copia ricevuta, la memorizzo
						if( ConsegnaContenutiApplicativi.ID_MODULO.equals(idModulo)  ){
							msg.aggiornaIDHeaderJMS(idModulo,idJMS,servizioApplicativo);
						}else{
							msg.aggiornaIDHeaderJMS(idModulo,idJMS);
						}
					}else{
						// se non e' null guardo se e' una copia per me
						if(idJMSRicevutoPrecedentemente.equals(idJMS) == false){
							msgDiag.highDebug("Messaggio per il modulo ["+idModulo+"]: un altro messaggio JMS e' gia stato ricevuto, scarta");
							msgDiag.highDebug("Rilascio connessione per TransactionManager");
							dbManager.releaseResource(dominio,idModuloTransaction,resource);
							return false; // scarta messaggio
						}
					}

					msgDiag.highDebug("Messaggio per il modulo ["+idModulo+"]: Attesa attiva, modulo precedente["+proprietarioMessaggio+"]");
					// Attesa attiva del modulo:
					if( properties.singleConnection_TransactionManager()==false ){
						msgDiag.highDebug("Rilascio connessione per TransactionManager");
						dbManager.releaseResource(dominio,idModuloTransaction,resource);
					}
					try{
						Thread.sleep(properties.getTransactionManager_CheckInterval());
					}catch(Exception eRandom){}
				}

			}

			if( properties.singleConnection_TransactionManager() ){
				msgDiag.highDebug("Rilascio connessione per TransactionManager");
				dbManager.releaseResource(dominio,idModuloTransaction,resource);
			}
			
			String msgTerminato = "TransactionManager: Attesa attiva terminata, probabilmente il messaggio e' stato gia gestito ed eliminato IDModulo["+idModulo+"] IDBusta["+idBusta+"] Tipo["+tipo+"]";
			msgDiag.highDebug(msgTerminato);
			TransactionManager.log.warn(msgTerminato);
			return false;

		}catch(Exception e){
			TransactionManager.log.error("TransactionManager exception: "+e.getMessage(),e);
			if(resource != null)
				dbManager.releaseResource(dominio,idModuloTransaction,resource);
			throw e;
		}
	}


	/**
	 * Restituisce true se il modulo <var>proprietarioMessaggio</var> e' un modulo precedente, nel flusso dei messaggi 
	 * all'interno dell'architettura di OpenSPCoop del modulo <var>idModulo</var>
	 *
	 * @param idModulo Identificatore del nodo che effettua il test.
	 * @param proprietarioMessaggio 'Potenziale' modulo precedente
	 * 
	 */
	public static boolean isModuloPrecedente(String idModulo,String proprietarioMessaggio,boolean checkProprietarioNull) throws Exception{

		if(idModulo.startsWith(RicezioneContenutiApplicativi.ID_MODULO)){
			if(Imbustamento.ID_MODULO.equals(proprietarioMessaggio))
				return true;
			else if(InoltroBuste.ID_MODULO.equals(proprietarioMessaggio))
				return true;
			else if(SbustamentoRisposte.ID_MODULO.equals(proprietarioMessaggio))
				return true;
			else if(Sbustamento.ID_MODULO.equals(proprietarioMessaggio))
				return true; // ricevute asincrone!
			else 
				return false;
		}

		else if(idModulo.startsWith(RicezioneBuste.ID_MODULO)){
			if(Sbustamento.ID_MODULO.equals(proprietarioMessaggio))
				return true;
			else if(ConsegnaContenutiApplicativi.ID_MODULO.equals(proprietarioMessaggio))
				return true;
			else if(ImbustamentoRisposte.ID_MODULO.equals(proprietarioMessaggio))
				return true;
			else 
				return false;	
		}

		else if(Imbustamento.ID_MODULO.equals(idModulo)){
			if(checkProprietarioNull){
				if(proprietarioMessaggio == null)
					return true; // punto di inizio
			}
			if(proprietarioMessaggio.startsWith(RicezioneContenutiApplicativi.ID_MODULO))
				return true;
			else 
				return false;
		}

		else if(ImbustamentoRisposte.ID_MODULO.equals(idModulo)){
			if(ConsegnaContenutiApplicativi.ID_MODULO.equals(proprietarioMessaggio))
				return true;
			else 
				return false;
		}

		else if(Sbustamento.ID_MODULO.equals(idModulo)){
			if(checkProprietarioNull){
				if(proprietarioMessaggio == null)
					return true; // punto di inizio
			}
			if(proprietarioMessaggio.startsWith(RicezioneBuste.ID_MODULO))
				return true;
			else 
				return false;
		}

		else if(SbustamentoRisposte.ID_MODULO.equals(idModulo)){
			if(InoltroBuste.ID_MODULO.equals(proprietarioMessaggio))
				return true;
			else 
				return false;
		}

		else if(InoltroBuste.ID_MODULO.equals(idModulo)){
			if(Imbustamento.ID_MODULO.equals(proprietarioMessaggio))
				return true;
			else 
				return false;
		}

		else if(InoltroRisposte.ID_MODULO.equals(idModulo)){
			// E' un punto di uscita senza risposta
			// qualsiasi nodo e' un potenziale precedente
			return true;
		}

		else if(ConsegnaContenutiApplicativi.ID_MODULO.equals(idModulo)){
			if(Sbustamento.ID_MODULO.equals(proprietarioMessaggio))
				return true;
			else if(SbustamentoRisposte.ID_MODULO.equals(proprietarioMessaggio))
				return true;
			else if(Imbustamento.ID_MODULO.equals(proprietarioMessaggio))
				return true;
			else if(InoltroBuste.ID_MODULO.equals(proprietarioMessaggio))
				return true;
			else 
				return false;
		}else{
			return false;
		}
	}

}
