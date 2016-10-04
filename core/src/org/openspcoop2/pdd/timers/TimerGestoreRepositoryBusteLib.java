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



package org.openspcoop2.pdd.timers;

import java.util.Vector;

import org.slf4j.Logger;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.core.GestoreMessaggi;
import org.openspcoop2.pdd.core.state.OpenSPCoopStateful;
import org.openspcoop2.pdd.logger.MsgDiagnostico;
import org.openspcoop2.pdd.services.OpenSPCoop2Startup;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.protocol.engine.driver.RepositoryBuste;
import org.openspcoop2.utils.date.DateManager;

/**
 * Implementazione dell'interfaccia {@link TimerGestoreRepositoryBuste} del Gestore
 * dei threads di servizio di OpenSPCoop.
 * 
 *  
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class TimerGestoreRepositoryBusteLib {

	private MsgDiagnostico msgDiag = null;
	private Logger logTimer = null;
	private OpenSPCoop2Properties propertiesReader = null;
	private boolean logQuery = false;
	private int limit = CostantiPdD.LIMIT_MESSAGGI_GESTORI;
	private boolean orderByQuery;
	
	public TimerGestoreRepositoryBusteLib(MsgDiagnostico msgDiag,Logger log,OpenSPCoop2Properties p,boolean logQuery,int limit,boolean orderByQuery){
		 this.msgDiag = msgDiag;
		 this.logTimer = log;
		 this.propertiesReader = p;
		 this.logQuery = logQuery;
		 this.limit = limit;
		 this.orderByQuery = orderByQuery;
	}
	
	public void check() throws TimerException {
		
		// Controllo che il sistema non sia andando in shutdown
		if(OpenSPCoop2Startup.contextDestroyed){
			this.logTimer.error("["+TimerGestoreRepositoryBuste.ID_MODULO+"] Rilevato sistema in shutdown");
			return;
		}
		
		// Controllo che l'inizializzazione corretta delle risorse sia effettuata
		if(OpenSPCoop2Startup.initialize==false){
			this.msgDiag.logFatalError("inizializzazione di OpenSPCoop non effettuata", "Check Inizializzazione");
			String msgErrore = "Riscontrato errore: inizializzazione del Timer o di OpenSPCoop non effettuata";
			this.logTimer.error(msgErrore);
			throw new TimerException(msgErrore);
		}
				
		// Controllo risorse di sistema disponibili
		if( TimerMonitoraggioRisorse.risorseDisponibili == false){
			this.logTimer.error("["+TimerGestoreRepositoryBuste.ID_MODULO+"] Risorse di sistema non disponibili: "+TimerMonitoraggioRisorse.risorsaNonDisponibile.getMessage(),TimerMonitoraggioRisorse.risorsaNonDisponibile);
			return;
		}
		if( MsgDiagnostico.gestoreDiagnosticaDisponibile == false){
			this.logTimer.error("["+TimerGestoreRepositoryBuste.ID_MODULO+"] Sistema di diagnostica non disponibile: "+MsgDiagnostico.motivoMalfunzionamentoDiagnostici.getMessage(),MsgDiagnostico.motivoMalfunzionamentoDiagnostici);
			return;
		}
		
		this.msgDiag.logPersonalizzato("controlloInCorso");
		this.logTimer.info(this.msgDiag.getMessaggio_replaceKeywords("controlloInCorso"));
		long startControlloRepositoryBuste = DateManager.getTimeMillis();
		
		
		OpenSPCoopStateful openspcoopstate = new OpenSPCoopStateful();
		try {

			openspcoopstate.initResource(this.propertiesReader.getIdentitaPortaDefault(null), TimerGestoreRepositoryBuste.ID_MODULO, null);
						
			// Messaggi da eliminare 
			RepositoryBuste repositoryBuste = new RepositoryBuste(openspcoopstate.getStatoRichiesta(),this.logTimer,null);
			boolean trovatiMessaggi = true;
			
			while(trovatiMessaggi){
				
				trovatiMessaggi = false;
										
				// Eliminazione Messaggi from INBOX
				String causaMessaggiINBOX = "Eliminazione buste (INBOX) marcate logicamente da eliminare";
				Vector<String> idMsgINBOX = null;
                try{
                	GestoreMessaggi.acquireLock(this.msgDiag, causaMessaggiINBOX, this.propertiesReader.getMsgGiaInProcessamento_AttesaAttiva(), this.propertiesReader.getMsgGiaInProcessamento_CheckInterval());
        		
                	idMsgINBOX = repositoryBuste.getBusteDaEliminareFromInBox(this.limit,this.logQuery,
                			this.propertiesReader.isForceIndex(),this.propertiesReader.isRepositoryBusteFiltraBusteScaduteRispettoOraRegistrazione(),
                			this.orderByQuery);
					if(idMsgINBOX.size()>0){
						if(this.logQuery)
							this.logTimer.info("Trovate "+idMsgINBOX.size()+" buste da eliminare nel repository (INBOX) ...");
						trovatiMessaggi = true;
		                	
	                	this.msgDiag.addKeyword(CostantiPdD.KEY_TIPO_MESSAGGIO,Costanti.INBOX);
						for(int i=0; i<idMsgINBOX.size(); i++){
							
							String idMsgDaEliminare = idMsgINBOX.get(i);
							this.msgDiag.addKeyword(CostantiPdD.KEY_ID_MESSAGGIO_DA_ELIMINARE,idMsgDaEliminare);
							
							try{
								repositoryBuste.eliminaBustaFromInBox(idMsgDaEliminare);
								
								this.msgDiag.logPersonalizzato("eliminazioneMessaggio");
								if(this.logQuery)
									this.logTimer.debug(this.msgDiag.getMessaggio_replaceKeywords("eliminazioneMessaggio"));
								
							}catch(Exception e){
								this.msgDiag.logErroreGenerico(e,"EliminazioneBustaInbox("+idMsgDaEliminare+")");
								this.logTimer.error("ErroreEliminazioneBustaInbox("+idMsgDaEliminare+"): "+e.getMessage(),e);
							}
						}
						
						if(this.logQuery)
							this.logTimer.info("Eliminate "+idMsgINBOX.size()+" buste nel repository (INBOX)");
					}
					else{
						if(this.logQuery)
							this.logTimer.info("Non sono state trovate buste da eliminare nel repository (INBOX)");
					}
                }finally{
    				try{
    					GestoreMessaggi.releaseLock(this.msgDiag, causaMessaggiINBOX);
    				}catch(Exception e){}
    			}
				
				//	Eliminazione Messaggi from OUTBOX
                String causaMessaggiOUTBOX = "Eliminazione buste (OUTBOX) marcate logicamente da eliminare";
                Vector<String> idMsgOUTBOX = null;
                try{
                	GestoreMessaggi.acquireLock(this.msgDiag, causaMessaggiOUTBOX, this.propertiesReader.getMsgGiaInProcessamento_AttesaAttiva(), this.propertiesReader.getMsgGiaInProcessamento_CheckInterval());
        		
                	idMsgOUTBOX = repositoryBuste.getBusteDaEliminareFromOutBox(this.limit,this.logQuery,
                			this.propertiesReader.isForceIndex(),this.propertiesReader.isRepositoryBusteFiltraBusteScaduteRispettoOraRegistrazione(),
                			this.orderByQuery);
					if(idMsgOUTBOX.size()>0){
						if(this.logQuery)
							this.logTimer.info("Trovate "+idMsgOUTBOX.size()+" buste da eliminare nel repository (OUTBOX) ...");
						trovatiMessaggi = true;
		                	
	                	this.msgDiag.addKeyword(CostantiPdD.KEY_TIPO_MESSAGGIO,Costanti.OUTBOX);
						for(int i=0; i<idMsgOUTBOX.size(); i++){
							
							String idMsgDaEliminare = idMsgOUTBOX.get(i);
							this.msgDiag.addKeyword(CostantiPdD.KEY_ID_MESSAGGIO_DA_ELIMINARE,idMsgDaEliminare);
							
							try{
								repositoryBuste.eliminaBustaFromOutBox(idMsgDaEliminare);
								
								this.msgDiag.logPersonalizzato("eliminazioneMessaggio");
								if(this.logQuery)
									this.logTimer.debug(this.msgDiag.getMessaggio_replaceKeywords("eliminazioneMessaggio"));
								
							}catch(Exception e){
								this.msgDiag.logErroreGenerico(e,"EliminazioneBustaOutbox("+idMsgDaEliminare+")");
								this.logTimer.error("ErroreEliminazioneBustaOutbox("+idMsgDaEliminare+"): "+e.getMessage(),e);
							}
						}
						
						if(this.logQuery)
							this.logTimer.info("Eliminate "+idMsgOUTBOX.size()+" buste nel repository (OUTBOX)");
					}
					else{
						if(this.logQuery)
							this.logTimer.info("Non sono state trovate buste da eliminare nel repository (OUTBOX)");
					}
                }finally{
    				try{
    					GestoreMessaggi.releaseLock(this.msgDiag, causaMessaggiOUTBOX);
    				}catch(Exception e){}
    			}
			
								
				if(trovatiMessaggi){
					this.msgDiag.addKeyword(CostantiPdD.KEY_TIMER_GESTORE_REPOSITORY_BUSTE_NUM_MSG_INBOX, idMsgINBOX.size()+"");
					this.msgDiag.addKeyword(CostantiPdD.KEY_TIMER_GESTORE_REPOSITORY_BUSTE_NUM_MSG_OUTBOX, idMsgOUTBOX.size()+"");
					this.msgDiag.logPersonalizzato("ricercaMessaggiDaEliminare");
				}
				
			}
			
			
			long endControlloRepositoryBuste = DateManager.getTimeMillis();
			this.logTimer.info("Controllo Repository Buste terminata in "+((endControlloRepositoryBuste-startControlloRepositoryBuste)/1000) +" secondi");
			
		} catch (Exception e) {
			this.msgDiag.logErroreGenerico(e,"GestioneBuste");
			this.logTimer.error("Riscontrato errore durante l'eliminazione delle buste: "+e.getMessage(),e);
		}finally{
			if(openspcoopstate!=null)
				openspcoopstate.releaseResource();
		}
	}

}
