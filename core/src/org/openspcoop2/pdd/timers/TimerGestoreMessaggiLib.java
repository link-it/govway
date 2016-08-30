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



package org.openspcoop2.pdd.timers;

import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;

import org.slf4j.Logger;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.core.GestoreCorrelazioneApplicativa;
import org.openspcoop2.pdd.core.GestoreMessaggi;
import org.openspcoop2.pdd.core.JMSReceiver;
import org.openspcoop2.pdd.core.connettori.IConnettore;
import org.openspcoop2.pdd.core.connettori.RepositoryConnettori;
import org.openspcoop2.pdd.core.state.OpenSPCoopStateful;
import org.openspcoop2.pdd.logger.MsgDiagnostico;
import org.openspcoop2.pdd.services.OpenSPCoop2Startup;
import org.openspcoop2.pdd.services.RicezioneBuste;
import org.openspcoop2.pdd.services.RicezioneContenutiApplicativi;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.protocol.engine.driver.RollbackRepositoryBuste;
import org.openspcoop2.protocol.sdk.state.StateMessage;
import org.openspcoop2.utils.date.DateManager;


/**
 * Implementazione dell'interfaccia {@link TimerGestoreMessaggi} del Gestore
 * dei threads di servizio di OpenSPCoop.
 * 
 *  
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class TimerGestoreMessaggiLib  {

	
	private MsgDiagnostico msgDiag = null;
	private Logger logTimer = null;
	private OpenSPCoop2Properties propertiesReader = null;
	private long scadenzaMessaggio = -1;
	private boolean logQuery = false;
	private int limit = CostantiPdD.LIMIT_MESSAGGI_GESTORI;
	private boolean orderByQuery;
	private long scadenzaCorrelazioneApplicativa;
	private boolean filtraCorrelazioniApplicativeScaduteRispettoOraRegistrazione;
	private boolean filtraCorrelazioniApplicativeScaduteRispettoOraRegistrazione_escludiCorrelazioniConScadenzaImpostata;
	
	public TimerGestoreMessaggiLib(MsgDiagnostico msgDiag,Logger log,OpenSPCoop2Properties p,
			long scadenzaMessaggio,boolean logQuery,
			int limit,boolean orderByQuery, 
			long scadenzaCorrelazioneApplicativa,
			boolean filtraCorrelazioniApplicativeScaduteRispettoOraRegistrazione,
			boolean filtraCorrelazioniApplicativeScaduteRispettoOraRegistrazione_escludiCorrelazioniConScadenzaImpostata){
		 this.msgDiag = msgDiag;
		 this.logTimer = log;
		 this.propertiesReader = p;
		 this.scadenzaMessaggio = scadenzaMessaggio;
		 this.logQuery = logQuery;
		 this.limit = limit;
		 this.orderByQuery = orderByQuery;
		 this.scadenzaCorrelazioneApplicativa = scadenzaCorrelazioneApplicativa;
		 this.filtraCorrelazioniApplicativeScaduteRispettoOraRegistrazione = filtraCorrelazioniApplicativeScaduteRispettoOraRegistrazione;
		 this.filtraCorrelazioniApplicativeScaduteRispettoOraRegistrazione_escludiCorrelazioniConScadenzaImpostata =
				 filtraCorrelazioniApplicativeScaduteRispettoOraRegistrazione_escludiCorrelazioniConScadenzaImpostata;
	}


	public void check() throws TimerException {

		// Controllo che il sistema non sia andando in shutdown
		if(OpenSPCoop2Startup.contextDestroyed){
			this.logTimer.error("["+TimerGestoreMessaggi.ID_MODULO+"] Rilevato sistema in shutdown");
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
			this.logTimer.error("["+TimerGestoreMessaggi.ID_MODULO+"] Risorse di sistema non disponibili: "+TimerMonitoraggioRisorse.risorsaNonDisponibile.getMessage(),TimerMonitoraggioRisorse.risorsaNonDisponibile);
			return;
		}
		if( MsgDiagnostico.gestoreDiagnosticaDisponibile == false){
			this.logTimer.error("["+TimerGestoreMessaggi.ID_MODULO+"] Sistema di diagnostica non disponibile: "+MsgDiagnostico.motivoMalfunzionamentoDiagnostici.getMessage(),MsgDiagnostico.motivoMalfunzionamentoDiagnostici);
			return;
		}

		// Prendo la gestione
		this.msgDiag.logPersonalizzato("controlloInCorso");
		this.logTimer.info(this.msgDiag.getMessaggio_replaceKeywords("controlloInCorso"));
		long startControlloRepositoryMessaggi = DateManager.getTimeMillis();

		GestoreMessaggi gestoreMsg= null;
		RollbackRepositoryBuste rollbackMessaggio = null;
		RollbackRepositoryBuste rollbackRepository = null;
		
		
		OpenSPCoopStateful openspcoopstate = new OpenSPCoopStateful();
		try {
			openspcoopstate.initResource(this.propertiesReader.getIdentitaPortaDefault(null),TimerGestoreMessaggi.ID_MODULO, null);
						
			// filtroJMS
			JMSReceiver receiverJMS = null;
			if(CostantiConfigurazione.COMUNICAZIONE_INFRASTRUTTURALE_JMS.equals(this.propertiesReader.getNodeReceiver()))
				receiverJMS = new JMSReceiver(this.propertiesReader.getIdentitaPortaDefault(null),"ForcedDeleteMessage",this.propertiesReader.singleConnection_NodeReceiver(),this.logTimer, null);

			// Messaggi da eliminare 
			GestoreMessaggi gestoreMsgSearch = new GestoreMessaggi(openspcoopstate, true,this.logTimer,this.msgDiag, null);
			long timeoutRicezioneContenutiApplicativiNonGestiti = this.propertiesReader.getNodeReceiverTimeoutRicezioneContenutiApplicativi() * 3;
			long timeoutRicezioneBusteNonGestiti = this.propertiesReader.getNodeReceiverTimeoutRicezioneBuste() * 3;
			boolean trovatiMessaggi = true;
			
			while(trovatiMessaggi){
			
				trovatiMessaggi = false;
			

			
				
				/* --- Messaggi da eliminare (non scaduti) --- */ 
				
				// Eliminazione Messaggi from INBOX
				String causaMessaggiINBOXDaEliminareNonScaduti = "Eliminazione messaggi INBOX marcati logicamente da eliminare";
				Vector<String> idMsgInutiliINBOX = null;
                try{
                	GestoreMessaggi.acquireLock(this.msgDiag, causaMessaggiINBOXDaEliminareNonScaduti, 
                			this.propertiesReader.getMsgGiaInProcessamento_AttesaAttiva(), 
                			this.propertiesReader.getMsgGiaInProcessamento_CheckInterval());
                	
                	idMsgInutiliINBOX = gestoreMsgSearch.readMessaggiInutiliIntoInbox(TimerGestoreMessaggi.ID_MODULO,this.limit,this.logQuery,this.orderByQuery);
					if(idMsgInutiliINBOX.size()>0){
						if(this.logQuery)
							this.logTimer.info("Trovati "+idMsgInutiliINBOX.size()+" messaggi (cancellazione logica) da eliminare nel repository (INBOX) ...");
						trovatiMessaggi = true;
		                
						this.msgDiag.addKeyword(CostantiPdD.KEY_TIMER_GESTORE_MESSAGGI_TIPO_RICERCA_MSG_DA_ELIMINARE,"MessaggiCompletati");
						this.msgDiag.addKeyword(CostantiPdD.KEY_TIPO_MESSAGGIO,Costanti.INBOX);
						for(int i=0; i<idMsgInutiliINBOX.size(); i++){
							
							String idMsgDaEliminare = idMsgInutiliINBOX.get(i);
							this.msgDiag.addKeyword(CostantiPdD.KEY_ID_MESSAGGIO_DA_ELIMINARE,idMsgDaEliminare);
							
							try{
								// eliminazione messaggio
								gestoreMsg = new GestoreMessaggi(openspcoopstate, true
										,idMsgDaEliminare,Costanti.INBOX,this.msgDiag,null);
								gestoreMsg.deleteMessageWithoutLock();
								
								this.msgDiag.logPersonalizzato("eliminazioneMessaggio");
								if(this.logQuery)
									this.logTimer.debug(this.msgDiag.getMessaggio_replaceKeywords("eliminazioneMessaggio"));
								
							}catch(Exception e){
								this.msgDiag.logErroreGenerico(e,"EliminazioneMessaggioInbox("+idMsgDaEliminare+")");
								this.logTimer.error("ErroreEliminazioneMessaggioInbox("+idMsgDaEliminare+"): "+e.getMessage(),e);
							}
						}
						
						if(this.logQuery)
		                	this.logTimer.info("Eliminati "+idMsgInutiliINBOX.size()+" messaggi (cancellazione logica) nel repository (INBOX)");
					}
					else{
						if(this.logQuery)
							this.logTimer.info("Non sono stati trovati messaggi (cancellazione logica) da eliminare nel repository (INBOX)");
					}
                }finally{
    				try{
    					GestoreMessaggi.releaseLock(this.msgDiag, causaMessaggiINBOXDaEliminareNonScaduti);
    				}catch(Exception e){}
    			}
					
				//	Eliminazione Messaggi from OUTBOX
                String causaMessaggiOUTBOXDaEliminareNonScaduti = "Eliminazione messaggi OUTBOX marcati logicamente da eliminare";
                Vector<String> idMsgInutiliOUTBOX = null;
                try{
                	GestoreMessaggi.acquireLock(this.msgDiag, causaMessaggiOUTBOXDaEliminareNonScaduti, 
                			this.propertiesReader.getMsgGiaInProcessamento_AttesaAttiva(), 
                			this.propertiesReader.getMsgGiaInProcessamento_CheckInterval());
        		
                	idMsgInutiliOUTBOX = gestoreMsgSearch.readMessaggiInutiliIntoOutbox(TimerGestoreMessaggi.ID_MODULO,this.limit,this.logQuery,this.orderByQuery);
					if(idMsgInutiliOUTBOX.size()>0){
						if(this.logQuery)
							this.logTimer.info("Trovati "+idMsgInutiliOUTBOX.size()+" messaggi (cancellazione logica) da eliminare nel repository (OUTBOX) ...");
						trovatiMessaggi = true;
		                
	                	this.msgDiag.addKeyword(CostantiPdD.KEY_TIMER_GESTORE_MESSAGGI_TIPO_RICERCA_MSG_DA_ELIMINARE,"MessaggiCompletati");
						this.msgDiag.addKeyword(CostantiPdD.KEY_TIPO_MESSAGGIO,Costanti.OUTBOX);
						for(int i=0; i<idMsgInutiliOUTBOX.size(); i++){
							
							String idMsgDaEliminare = idMsgInutiliOUTBOX.get(i);
							this.msgDiag.addKeyword(CostantiPdD.KEY_ID_MESSAGGIO_DA_ELIMINARE,idMsgDaEliminare);
							
							try{
								// eliminazione messaggio
								gestoreMsg = new GestoreMessaggi(openspcoopstate, true,idMsgDaEliminare,Costanti.OUTBOX,this.msgDiag,null);
								gestoreMsg.deleteMessageWithoutLock();
								
								this.msgDiag.logPersonalizzato("eliminazioneMessaggio");
								if(this.logQuery)
									this.logTimer.debug(this.msgDiag.getMessaggio_replaceKeywords("eliminazioneMessaggio"));
								
							}catch(Exception e){
								this.msgDiag.logErroreGenerico(e,"EliminazioneMessaggioOutbox("+idMsgDaEliminare+")");
								this.logTimer.error("ErroreEliminazioneMessaggioOutbox("+idMsgDaEliminare+"): "+e.getMessage(),e);
							}		
						}
						
						if(this.logQuery)
							this.logTimer.info("Eliminati "+idMsgInutiliOUTBOX.size()+" messaggi (cancellazione logica) nel repository (OUTBOX)");
					}
					else{
						if(this.logQuery)
							this.logTimer.info("Non sono stati trovati messaggi (cancellazione logica) da eliminare nel repository (OUTBOX)");
					}
                }finally{
    				try{
    					GestoreMessaggi.releaseLock(this.msgDiag, causaMessaggiOUTBOXDaEliminareNonScaduti);
    				}catch(Exception e){}
    			}
				
				
				
				
				
				
				/* --- Messaggi scaduti, verra' effettuato anche un rollback delle Buste. --- */
                
				//	Eliminazione Messaggi from INBOX
                String causaMessaggiINBOXScaduti = "Eliminazione messaggi INBOX scaduti";
                Vector<String> idMsgScadutiINBOX = null;
                try{
                	GestoreMessaggi.acquireLock(this.msgDiag, causaMessaggiINBOXScaduti, 
                			this.propertiesReader.getMsgGiaInProcessamento_AttesaAttiva(), 
                			this.propertiesReader.getMsgGiaInProcessamento_CheckInterval());
        		
                	idMsgScadutiINBOX = gestoreMsgSearch.readMessaggiScadutiIntoInbox(this.scadenzaMessaggio,this.limit,this.logQuery,this.orderByQuery);
					if(idMsgScadutiINBOX.size()>0){
						if(this.logQuery)
							this.logTimer.info("Trovati "+idMsgScadutiINBOX.size()+" messaggi (scaduti) da eliminare nel repository (INBOX) ...");
						trovatiMessaggi = true;
		                
	                	this.msgDiag.addKeyword(CostantiPdD.KEY_TIMER_GESTORE_MESSAGGI_TIPO_RICERCA_MSG_DA_ELIMINARE,"MessaggiScaduti");
						this.msgDiag.addKeyword(CostantiPdD.KEY_TIPO_MESSAGGIO,Costanti.INBOX);
						for(int i=0; i<idMsgScadutiINBOX.size(); i++){
							
							String idMsgDaEliminare = idMsgScadutiINBOX.get(i);
							this.msgDiag.addKeyword(CostantiPdD.KEY_ID_MESSAGGIO_DA_ELIMINARE,idMsgDaEliminare);
							
							try{
								gestoreMsg = new GestoreMessaggi(openspcoopstate, true,idMsgDaEliminare,Costanti.INBOX,this.msgDiag,null);
								
								//	rollback messaggio scaduto (eventuale profilo + accesso_pdd)
								rollbackMessaggio = new RollbackRepositoryBuste(idMsgDaEliminare,openspcoopstate.getStatoRichiesta(),true);
								rollbackMessaggio.rollbackBustaIntoInBox();
								
								// rollback repository + // rollback jms receiver
								String rifMsg = gestoreMsg.getRiferimentoMessaggio();
								if(rifMsg==null){
									if(receiverJMS!=null){
										// rollback jms receiver
										String strMessageSelector = "ID = '"+idMsgDaEliminare+"'";
										if (receiverJMS.clean(RicezioneBuste.ID_MODULO,strMessageSelector)){
											this.msgDiag.addKeyword(CostantiPdD.KEY_TIMER_GESTORE_MESSAGGI_CODA_JMS_FILTRO_MSG_DA_ELIMINARE,
													RicezioneBuste.ID_MODULO);
											this.msgDiag.addKeyword(CostantiPdD.KEY_TIMER_GESTORE_MESSAGGI_RICERCA_MSG_DA_ELIMINARE_PER_RIFERIMENTO,"false");
											this.msgDiag.logPersonalizzato("messaggioNonConsumato.codaJMS");
										}
									}
								}else{
									// rollback repository
									rollbackRepository = new RollbackRepositoryBuste(rifMsg,openspcoopstate.getStatoRichiesta(),true);
									rollbackRepository.rollbackBustaIntoOutBox(false); // non effettuo il rollback dell'history (riscontro/ricevuta arrivera...)
									
									if(receiverJMS!=null){
										// rollback jms receiver
										String strMessageSelector = "ID = '"+rifMsg+"'";
										if (receiverJMS.clean(RicezioneContenutiApplicativi.ID_MODULO,strMessageSelector)){
											this.msgDiag.addKeyword(CostantiPdD.KEY_TIMER_GESTORE_MESSAGGI_CODA_JMS_FILTRO_MSG_DA_ELIMINARE,
													RicezioneContenutiApplicativi.ID_MODULO);
											this.msgDiag.addKeyword(CostantiPdD.KEY_TIMER_GESTORE_MESSAGGI_RICERCA_MSG_DA_ELIMINARE_PER_RIFERIMENTO,"true");
											this.msgDiag.logPersonalizzato("messaggioNonConsumato.codaJMS");
										}
									}
								}
								
								((StateMessage)openspcoopstate.getStatoRichiesta()).executePreparedStatement();
								//	eliminazione messaggio
								gestoreMsg.deleteMessageWithoutLock();
								
								this.msgDiag.logPersonalizzato("eliminazioneMessaggio");
								if(this.logQuery)
									this.logTimer.debug(this.msgDiag.getMessaggio_replaceKeywords("eliminazioneMessaggio"));
								
							}catch(Exception e){
			
								((StateMessage)openspcoopstate.getStatoRichiesta()).closePreparedStatement();
								this.msgDiag.logErroreGenerico(e,"EliminazioneMessaggioScadutoInbox("+idMsgDaEliminare+")");
								this.logTimer.error("ErroreEliminazioneMessaggioScadutoInbox("+idMsgDaEliminare+"): "+e.getMessage(),e);
							}
						}
						
						if(this.logQuery)
							this.logTimer.info("Eliminati "+idMsgScadutiINBOX.size()+" messaggi (scaduti) nel repository (INBOX)");
					}
					else{
						if(this.logQuery)
							this.logTimer.info("Non sono stati trovati messaggi (scaduti) da eliminare nel repository (INBOX)");
					}
                }finally{
    				try{
    					GestoreMessaggi.releaseLock(this.msgDiag, causaMessaggiINBOXScaduti);
    				}catch(Exception e){}
    			}
				
				// Eliminazione Messaggi from OUTBOX
                String causaMessaggiOUTBOXScaduti = "Eliminazione messaggi OUTBOX scaduti";
                Vector<String> idMsgScadutiOUTBOX = null;
                try{
                	GestoreMessaggi.acquireLock(this.msgDiag, causaMessaggiOUTBOXScaduti, 
                			this.propertiesReader.getMsgGiaInProcessamento_AttesaAttiva(), 
                			this.propertiesReader.getMsgGiaInProcessamento_CheckInterval());
        		
                	idMsgScadutiOUTBOX = gestoreMsgSearch.readMessaggiScadutiIntoOutbox(this.scadenzaMessaggio,this.limit,this.logQuery,this.orderByQuery);
					if(idMsgScadutiOUTBOX.size()>0){
						if(this.logQuery)
							this.logTimer.info("Trovati "+idMsgScadutiOUTBOX.size()+" messaggi (scaduti) da eliminare nel repository (OUTBOX) ...");
						trovatiMessaggi = true;
		                
	                	this.msgDiag.addKeyword(CostantiPdD.KEY_TIMER_GESTORE_MESSAGGI_TIPO_RICERCA_MSG_DA_ELIMINARE,"MessaggiScaduti");
						this.msgDiag.addKeyword(CostantiPdD.KEY_TIPO_MESSAGGIO,Costanti.OUTBOX);
						for(int i=0; i<idMsgScadutiOUTBOX.size(); i++){
							
							String idMsgDaEliminare = idMsgScadutiOUTBOX.get(i);
							this.msgDiag.addKeyword(CostantiPdD.KEY_ID_MESSAGGIO_DA_ELIMINARE,idMsgDaEliminare);
							
							try{
								gestoreMsg = new GestoreMessaggi(openspcoopstate, true,idMsgDaEliminare,Costanti.OUTBOX,this.msgDiag,null);
								
								//	rollback messaggio scaduto (eventuale profilo + accesso_pdd)
								rollbackMessaggio = new RollbackRepositoryBuste(idMsgDaEliminare,openspcoopstate.getStatoRichiesta(),true);
								rollbackMessaggio.rollbackBustaIntoOutBox();
								
								//	rollback repository + // rollback jms receiver
								String rifMsg = gestoreMsg.getRiferimentoMessaggio();
								if(rifMsg==null){
									if(receiverJMS!=null){
										//	rollback jms receiver
										String strMessageSelector = "ID = '"+idMsgDaEliminare+"'";
										if (receiverJMS.clean(RicezioneContenutiApplicativi.ID_MODULO,strMessageSelector)){
											this.msgDiag.addKeyword(CostantiPdD.KEY_TIMER_GESTORE_MESSAGGI_CODA_JMS_FILTRO_MSG_DA_ELIMINARE,
													RicezioneContenutiApplicativi.ID_MODULO);
											this.msgDiag.addKeyword(CostantiPdD.KEY_TIMER_GESTORE_MESSAGGI_RICERCA_MSG_DA_ELIMINARE_PER_RIFERIMENTO,"false");
											this.msgDiag.logPersonalizzato("messaggioNonConsumato.codaJMS");
										}
									}
								}else{
									// rollback repository
									rollbackRepository = new RollbackRepositoryBuste(rifMsg,openspcoopstate.getStatoRichiesta(),true);
									rollbackRepository.rollbackBustaIntoInBox(false); // non effettuo il rollback dell'history (busta e' ricevuta)
								
									if(receiverJMS!=null){
										//	rollback jms receiver
										String strMessageSelector = "ID = '"+rifMsg+"'";
										if (receiverJMS.clean(RicezioneBuste.ID_MODULO,strMessageSelector)){
											this.msgDiag.addKeyword(CostantiPdD.KEY_TIMER_GESTORE_MESSAGGI_CODA_JMS_FILTRO_MSG_DA_ELIMINARE,
													RicezioneBuste.ID_MODULO);
											this.msgDiag.addKeyword(CostantiPdD.KEY_TIMER_GESTORE_MESSAGGI_RICERCA_MSG_DA_ELIMINARE_PER_RIFERIMENTO,"true");
											this.msgDiag.logPersonalizzato("messaggioNonConsumato.codaJMS");
										}
									}
								}
								((StateMessage)openspcoopstate.getStatoRichiesta()).executePreparedStatement();
								//	eliminazione messaggio
								gestoreMsg.deleteMessageWithoutLock();
								
								this.msgDiag.logPersonalizzato("eliminazioneMessaggio");
								if(this.logQuery)
									this.logTimer.debug(this.msgDiag.getMessaggio_replaceKeywords("eliminazioneMessaggio"));
								
							}catch(Exception e){
								((StateMessage)openspcoopstate.getStatoRichiesta()).closePreparedStatement();
								this.msgDiag.logErroreGenerico(e,"EliminazioneMessaggioScadutoOutbox("+idMsgDaEliminare+")");
								this.logTimer.error("ErroreEliminazioneMessaggioScadutoOutbox("+idMsgDaEliminare+"): "+e.getMessage(),e);
							}		
						}
						
						if(this.logQuery)
								this.logTimer.info("Eliminati "+idMsgScadutiOUTBOX.size()+" messaggi (scaduti) nel repository (OUTBOX)");
						
	                }
	                else{
	                	if(this.logQuery)
	                		this.logTimer.info("Non sono stati trovati messaggi (scaduti) da eliminare nel repository (OUTBOX)");
	                }
                }finally{
    				try{
    					GestoreMessaggi.releaseLock(this.msgDiag, causaMessaggiOUTBOXScaduti);
    				}catch(Exception e){}
    			}
				
				
				
						
				
				
				
				/* --- Messaggi non gestiti dal servizio web RicezioneContenutiApplicativi, verra' effettuato anche un rollback delle Buste. ---*/
                
				//	Eliminazione Messaggi from INBOX
                String causaMessaggiINBOXNonGestitiRicezioneContenutiApplicativi = "Eliminazione messaggi INBOX non gestiti dal servizio RicezioneContenutiApplicativi";
                Vector<String> idMsgServizioRicezioneContenutiApplicativiNonGestiti = null;
                try{
                	GestoreMessaggi.acquireLock(this.msgDiag, causaMessaggiINBOXNonGestitiRicezioneContenutiApplicativi, 
                			this.propertiesReader.getMsgGiaInProcessamento_AttesaAttiva(), 
                			this.propertiesReader.getMsgGiaInProcessamento_CheckInterval());
        		
                	idMsgServizioRicezioneContenutiApplicativiNonGestiti = 
                			gestoreMsgSearch.readMsgForRicezioneContenutiApplicativiNonGestiti(timeoutRicezioneContenutiApplicativiNonGestiti, this.limit,this.logQuery,this.orderByQuery);
					if(idMsgServizioRicezioneContenutiApplicativiNonGestiti.size()>0){
						if(this.logQuery)
							this.logTimer.info("Trovati "+idMsgServizioRicezioneContenutiApplicativiNonGestiti.size()+" messaggi (non gestiti da 'RicezioneContenutiApplicativi') da eliminare nel repository (INBOX) ...");
						trovatiMessaggi = true;
						
	                	this.msgDiag.addKeyword(CostantiPdD.KEY_TIMER_GESTORE_MESSAGGI_TIPO_RICERCA_MSG_DA_ELIMINARE,"MessaggiNonGestitiRicezioneContenutiApplicativi");
						this.msgDiag.addKeyword(CostantiPdD.KEY_TIPO_MESSAGGIO,Costanti.INBOX);
						for(int i=0; i<idMsgServizioRicezioneContenutiApplicativiNonGestiti.size(); i++){
							String idMsgDaEliminare = idMsgServizioRicezioneContenutiApplicativiNonGestiti.get(i);
							String idBustaDaEliminare = idMsgDaEliminare.split("@")[0];
							String servizioApplicativoDaEliminare = idMsgDaEliminare.split("@")[1];
							this.msgDiag.addKeyword(CostantiPdD.KEY_ID_MESSAGGIO_DA_ELIMINARE,idBustaDaEliminare);
							
							try{
								gestoreMsg = new GestoreMessaggi(openspcoopstate, true,idBustaDaEliminare,Costanti.INBOX,this.msgDiag,null);
								
								// pulizia jms receiver
								if(receiverJMS!=null){
									String rifMsg = gestoreMsg.getRiferimentoMessaggio();
									if(rifMsg!=null){
										// rollback jms receiver
										String strMessageSelector = "ID = '"+rifMsg+"'";
										if (receiverJMS.clean(RicezioneContenutiApplicativi.ID_MODULO,strMessageSelector)){
											this.msgDiag.addKeyword(CostantiPdD.KEY_TIMER_GESTORE_MESSAGGI_CODA_JMS_FILTRO_MSG_DA_ELIMINARE,
													RicezioneContenutiApplicativi.ID_MODULO);
											this.msgDiag.addKeyword(CostantiPdD.KEY_TIMER_GESTORE_MESSAGGI_RICERCA_MSG_DA_ELIMINARE_PER_RIFERIMENTO,"true");
											this.msgDiag.logPersonalizzato("messaggioNonConsumato.codaJMS");
										}
									}
								}
								
								//	eliminazione servizioApplicativo
								gestoreMsg.eliminaDestinatarioMessaggio(servizioApplicativoDaEliminare,gestoreMsg.getRiferimentoMessaggio());
								
								// Se non ci sono altri destinatari effettua rollback
								if(gestoreMsg.existsServiziApplicativiDestinatariMessaggio()==false){
									rollbackMessaggio = new RollbackRepositoryBuste(idBustaDaEliminare,openspcoopstate.getStatoRichiesta(),true);
									rollbackMessaggio.rollbackBustaIntoInBox();
									((StateMessage)openspcoopstate.getStatoRichiesta()).executePreparedStatement();
								}
								
								this.msgDiag.logPersonalizzato("eliminazioneDestinatarioMessaggio");
								if(this.logQuery)
									this.logTimer.debug(this.msgDiag.getMessaggio_replaceKeywords("eliminazioneDestinatarioMessaggio"));
							
							}catch(Exception e){
								if(rollbackMessaggio!=null){
									((StateMessage)openspcoopstate.getStatoRichiesta()).closePreparedStatement();
									rollbackMessaggio = null;
								}
								this.msgDiag.logErroreGenerico(e,"EliminazioneMessaggioRicezioneContenutiApplicativiNonGestiti("+idMsgDaEliminare+")");
								this.logTimer.error("ErroreEliminazioneMessaggioRicezioneContenutiApplicativiNonGestiti("+idMsgDaEliminare+"): "+e.getMessage(),e);
							}
						}
						
						if(this.logQuery)
							this.logTimer.info("Eliminati "+idMsgServizioRicezioneContenutiApplicativiNonGestiti.size()+" messaggi (non gestiti da 'RicezioneContenutiApplicativi') nel repository (INBOX)");
					
					}
					else{
						if(this.logQuery)
							this.logTimer.info("Non sono stati trovati messaggi (non gestiti da 'RicezioneContenutiApplicativi') da eliminare nel repository (INBOX)");
					}
                }finally{
    				try{
    					GestoreMessaggi.releaseLock(this.msgDiag, causaMessaggiINBOXNonGestitiRicezioneContenutiApplicativi);
    				}catch(Exception e){}
    			}
				
				
				
				
				
				
				
				/* --- Messaggi non gestiti dal servizio web RicezioneBuste, verra' effettuato anche un rollback delle Buste. --- */
			
				//	Eliminazione Messaggi from OUTBOX
                String causaMessaggiOUTBOXNonGestitiRicezioneBuste = "Eliminazione messaggi OUTBOX non gestiti dal servizio RicezioneBuste";
                Vector<String> idMsgServizioRicezioneBusteNonGestiti = null;
                try{
                	GestoreMessaggi.acquireLock(this.msgDiag, causaMessaggiOUTBOXNonGestitiRicezioneBuste, 
                			this.propertiesReader.getMsgGiaInProcessamento_AttesaAttiva(), 
                			this.propertiesReader.getMsgGiaInProcessamento_CheckInterval());
        		
                	idMsgServizioRicezioneBusteNonGestiti = 
                			gestoreMsgSearch.readMsgForRicezioneBusteNonGestiti(timeoutRicezioneBusteNonGestiti, this.limit,this.logQuery,this.orderByQuery);
					if(idMsgServizioRicezioneBusteNonGestiti.size()>0){
						if(this.logQuery)
							this.logTimer.info("Trovati "+idMsgServizioRicezioneBusteNonGestiti.size()+" messaggi (non gestiti da 'RicezioneBuste') da eliminare nel repository (OUTBOX) ...");
						trovatiMessaggi = true;
						
		                this.msgDiag.addKeyword(CostantiPdD.KEY_TIMER_GESTORE_MESSAGGI_TIPO_RICERCA_MSG_DA_ELIMINARE,"MessaggiNonGestitiRicezioneBuste");
						this.msgDiag.addKeyword(CostantiPdD.KEY_TIPO_MESSAGGIO,Costanti.OUTBOX);
						for(int i=0; i<idMsgServizioRicezioneBusteNonGestiti.size(); i++){
							String idMsgDaEliminare = idMsgServizioRicezioneBusteNonGestiti.get(i);
							this.msgDiag.addKeyword(CostantiPdD.KEY_ID_MESSAGGIO_DA_ELIMINARE,idMsgDaEliminare);
							try{
								gestoreMsg = new GestoreMessaggi(openspcoopstate, true,idMsgDaEliminare,Costanti.OUTBOX,this.msgDiag,null);
								
								//	rollback messaggio scaduto (eventuale profilo + accesso_pdd)
								rollbackMessaggio = new RollbackRepositoryBuste(idMsgDaEliminare,openspcoopstate.getStatoRichiesta(),true);
								rollbackMessaggio.rollbackBustaIntoOutBox();
								((StateMessage)openspcoopstate.getStatoRichiesta()).executePreparedStatement();
								
								// ollback jms receiver
								if(receiverJMS!=null){
									String rifMsg = gestoreMsg.getRiferimentoMessaggio();
									if(rifMsg!=null){
										// rollback jms receiver
										String strMessageSelector = "ID = '"+rifMsg+"'";
										if (receiverJMS.clean(RicezioneBuste.ID_MODULO,strMessageSelector)){
											this.msgDiag.addKeyword(CostantiPdD.KEY_TIMER_GESTORE_MESSAGGI_CODA_JMS_FILTRO_MSG_DA_ELIMINARE,
													RicezioneBuste.ID_MODULO);
											this.msgDiag.addKeyword(CostantiPdD.KEY_TIMER_GESTORE_MESSAGGI_RICERCA_MSG_DA_ELIMINARE_PER_RIFERIMENTO,"false");
											this.msgDiag.logPersonalizzato("messaggioNonConsumato.codaJMS");
										}
									}
								}
								
								//	eliminazione messaggio
								gestoreMsg.deleteMessageWithoutLock();
								
								this.msgDiag.logPersonalizzato("eliminazioneMessaggio");
								if(this.logQuery)
									this.logTimer.debug(this.msgDiag.getMessaggio_replaceKeywords("eliminazioneMessaggio"));
								
							}catch(Exception e){
								if(rollbackMessaggio!=null){
									((StateMessage)openspcoopstate.getStatoRichiesta()).closePreparedStatement();
									rollbackMessaggio = null;
								}
								this.msgDiag.logErroreGenerico(e,"EliminazioneMessaggioRicezioneBusteNonGestiti("+idMsgDaEliminare+")");
								this.logTimer.error("ErroreEliminazioneMessaggioRicezioneBusteNonGestiti("+idMsgDaEliminare+"): "+e.getMessage(),e);
							}
						}
						
						if(this.logQuery)
							this.logTimer.info("Eliminati "+idMsgServizioRicezioneBusteNonGestiti.size()+" messaggi (non gestiti da 'RicezioneBuste') nel repository (OUTBOX)");
				
					}
					else{
						if(this.logQuery)
							this.logTimer.info("Non sono stati trovati messaggi (non gestiti da 'RicezioneBuste') da eliminare nel repository (OUTBOX)");
					}
                }finally{
    				try{
    					GestoreMessaggi.releaseLock(this.msgDiag, causaMessaggiOUTBOXNonGestitiRicezioneBuste);
    				}catch(Exception e){}
    			}
				
				
				// log finale  
				if(trovatiMessaggi){
					this.msgDiag.addKeyword(CostantiPdD.KEY_TIMER_GESTORE_MESSAGGI_NUM_MSG_INBOX_COMPLETATI, idMsgInutiliINBOX.size()+"");
					this.msgDiag.addKeyword(CostantiPdD.KEY_TIMER_GESTORE_MESSAGGI_NUM_MSG_OUTBOX_COMPLETATI, idMsgInutiliOUTBOX.size()+"");
					this.msgDiag.addKeyword(CostantiPdD.KEY_TIMER_GESTORE_MESSAGGI_NUM_MSG_INBOX_SCADUTI, idMsgScadutiINBOX.size()+"");
					this.msgDiag.addKeyword(CostantiPdD.KEY_TIMER_GESTORE_MESSAGGI_NUM_MSG_OUTBOX_SCADUTI, idMsgScadutiOUTBOX.size()+"");
					this.msgDiag.addKeyword(CostantiPdD.KEY_TIMER_GESTORE_MESSAGGI_NUM_MSG_NON_GESTITI_RICEZIONE_CONTENUTI_APPLICATIVI, 
							idMsgServizioRicezioneContenutiApplicativiNonGestiti.size()+"");
					this.msgDiag.addKeyword(CostantiPdD.KEY_TIMER_GESTORE_MESSAGGI_NUM_MSG_NON_GESTITI_RICEZIONE_BUSTE, 
							idMsgServizioRicezioneBusteNonGestiti.size()+"");
					this.msgDiag.logPersonalizzato("ricercaMessaggiDaEliminare");
				}
				
			}

			
			
			
			
			
			
			
			//	CorrelazioniApplicative da eliminare
			GestoreCorrelazioneApplicativa gestoreCorrelazioneApplicativa = new GestoreCorrelazioneApplicativa(openspcoopstate.getStatoRichiesta(),this.logTimer,null);
			
			
			// -- Scadute (Correlazioni per cui era stata impostata una scadenza) --
			java.util.Vector<Long> correlazioniScadute = gestoreCorrelazioneApplicativa.getCorrelazioniScadute(this.limit,this.logQuery,this.orderByQuery);
			this.msgDiag.addKeyword(CostantiPdD.KEY_TIMER_GESTORE_MESSAGGI_TIPO_RICERCA_MSG_DA_ELIMINARE,"CorrelazioniApplicative");
			if(this.logQuery){
				if(correlazioniScadute.size()<=0){
					this.logTimer.info("Non sono state trovate correlazioni applicative (scadute) da eliminare");
				}
			}
			
			while(correlazioniScadute.size()>0){
				
				if(this.logQuery)
					this.logTimer.info("Trovate "+correlazioniScadute.size()+" correlazioni applicative (scadute) da eliminare ...");
				
				this.msgDiag.addKeyword(CostantiPdD.KEY_TIMER_GESTORE_MESSAGGI_NUM_CORRELAZIONI_APPLICATIVE_SCADUTE, correlazioniScadute.size()+"");
				this.msgDiag.logPersonalizzato("ricercaCorrelazioniApplicativeScadute");
				
				int size = correlazioniScadute.size();
				while(correlazioniScadute.size()>0){
					Long idCorrelazioneScaduta = correlazioniScadute.remove(0);

					String idCorrelato = null;
					String idApplicativo = null;
					
					try{

						String [] id = gestoreCorrelazioneApplicativa.getIDMappingCorrelazioneApplicativa(idCorrelazioneScaduta);
						
						this.msgDiag.addKeyword(CostantiPdD.KEY_ID_MESSAGGIO_DA_ELIMINARE,id[0]);
						this.msgDiag.addKeyword(CostantiPdD.KEY_ID_CORRELAZIONE_APPLICATIVA,id[1]);
						
						gestoreCorrelazioneApplicativa.deleteCorrelazioneApplicativa(idCorrelazioneScaduta);
						this.msgDiag.logPersonalizzato("eliminazioneCorrelazioneApplicativaScaduta");
						if(this.logQuery)
							this.logTimer.debug(this.msgDiag.getMessaggio_replaceKeywords("eliminazioneCorrelazioneApplicativaScaduta"));
						
					}catch(Exception e){
						this.msgDiag.logErroreGenerico(e,"EliminazioneCorrelazioni(id_applicativo:"+idApplicativo+",id_busta:"+idCorrelato+")");
						this.logTimer.error("ErroreEliminazioneCorrelazioni(id_applicativo:"+idApplicativo+",id_busta:"+idCorrelato+"): "+e.getMessage(),e);
					}
				}
				
				if(this.logQuery)
					this.logTimer.info("Eliminate "+size+" correlazioni applicative (scadute)");
				
				// Check altre correlazioni da eliminare
				correlazioniScadute = gestoreCorrelazioneApplicativa.getCorrelazioniScadute(this.limit,this.logQuery,this.orderByQuery);
			}
			
			
			// -- Scadute rispetto ora registrazione
			if(this.filtraCorrelazioniApplicativeScaduteRispettoOraRegistrazione){
				java.util.Vector<Long> correlazioniScaduteRispettoOraRegistrazione = 
						gestoreCorrelazioneApplicativa.getCorrelazioniScaduteRispettoOraRegistrazione(this.limit, this.scadenzaCorrelazioneApplicativa, 
								this.logQuery,this.orderByQuery,this.filtraCorrelazioniApplicativeScaduteRispettoOraRegistrazione_escludiCorrelazioniConScadenzaImpostata);
				if(this.logQuery){
					if(correlazioniScaduteRispettoOraRegistrazione.size()<=0){
						this.logTimer.info("Non sono state trovate correlazioni applicative (scadute rispetto ora registrazione) da eliminare");
					}
				}
				while(correlazioniScaduteRispettoOraRegistrazione.size()>0){
					
					if(this.logQuery)
						this.logTimer.info("Trovate "+correlazioniScaduteRispettoOraRegistrazione.size()+" correlazioni applicative (scadute rispetto ora registrazione) da eliminare ...");
					
					this.msgDiag.addKeyword(CostantiPdD.KEY_TIMER_GESTORE_MESSAGGI_NUM_CORRELAZIONI_APPLICATIVE_SCADUTE, correlazioniScaduteRispettoOraRegistrazione.size()+"");
					this.msgDiag.logPersonalizzato("ricercaCorrelazioniApplicativeScaduteRispettoOraRegistrazione");
					
					int size = correlazioniScaduteRispettoOraRegistrazione.size();
					while(correlazioniScaduteRispettoOraRegistrazione.size()>0){
						Long idCorrelazioneScaduta = correlazioniScaduteRispettoOraRegistrazione.remove(0);
						
						String idCorrelato = null;
						String idApplicativo = null;
						
						try{
							String [] id = gestoreCorrelazioneApplicativa.getIDMappingCorrelazioneApplicativa(idCorrelazioneScaduta);
							
							this.msgDiag.addKeyword(CostantiPdD.KEY_ID_MESSAGGIO_DA_ELIMINARE,id[0]);
							this.msgDiag.addKeyword(CostantiPdD.KEY_ID_CORRELAZIONE_APPLICATIVA,id[1]);
							
							gestoreCorrelazioneApplicativa.deleteCorrelazioneApplicativa(idCorrelazioneScaduta);
							this.msgDiag.logPersonalizzato("eliminazioneCorrelazioneApplicativaScaduta");
							if(this.logQuery)
								this.logTimer.debug(this.msgDiag.getMessaggio_replaceKeywords("eliminazioneCorrelazioneApplicativaScaduta"));
							
						}catch(Exception e){
							this.msgDiag.logErroreGenerico(e,"EliminazioneCorrelazioniRispettoOraRegistrazione(id_applicativo:"+idApplicativo+",id_busta:"+idCorrelato+")");
							this.logTimer.error("ErroreEliminazioneCorrelazioniRispettoOraRegistrazione(id_applicativo:"+idApplicativo+",id_busta:"+idCorrelato+"): "+e.getMessage(),e);
						}
					}
					
					if(this.logQuery)
						this.logTimer.info("Eliminate "+size+" correlazioni applicative (scadute rispetto ora registrazione)");
					
					// Check altre correlazioni da eliminare
					correlazioniScaduteRispettoOraRegistrazione = 
							gestoreCorrelazioneApplicativa.getCorrelazioniScaduteRispettoOraRegistrazione(this.limit, this.scadenzaCorrelazioneApplicativa, 
									this.logQuery,this.orderByQuery,this.filtraCorrelazioniApplicativeScaduteRispettoOraRegistrazione_escludiCorrelazioniConScadenzaImpostata);
				}
			}
			
			
			
			
			
			if(this.propertiesReader.isTimerGestoreMessaggiVerificaConnessioniAttive()){
			
				this.logTimer.info("Verifico connessioni attive...");
				
				// Connettori: porta delegata
				Enumeration<String> identificatoriConnettoriPD = RepositoryConnettori.getIdentificatoriConnettori_pd();
				if(this.logQuery){
					if(identificatoriConnettoriPD==null || identificatoriConnettoriPD.hasMoreElements()==false){
						this.logTimer.info("Non sono state trovate connessioni attive sulle PorteDelegate");
					}
				}
				if(identificatoriConnettoriPD!=null){
					
					boolean verificaEffettuata = false;
					if(this.logQuery){
						if(identificatoriConnettoriPD.hasMoreElements()){
							this.logTimer.info("Sono state trovate connessioni attive sulle PorteDelegate, verifica in corso ...");
						}
					}
					
					while (identificatoriConnettoriPD.hasMoreElements()) {
						verificaEffettuata = true;
						String identificatoreConnessione = identificatoriConnettoriPD.nextElement();
						String tipoConnettore = null;
						IConnettore connettore = null;
						try{
							connettore = RepositoryConnettori.getConnettorePD(identificatoreConnessione);
							// puo' essere stato eliminato nel frattempo
							if(connettore!=null){
								tipoConnettore = connettore.getClass().getSimpleName();
							}
							int millisecondiScadenza = this.propertiesReader.getConnectionLife_inoltroBuste();
							Date now = DateManager.getDate();
							Date scadenza = new Date(now.getTime()-millisecondiScadenza);
							String tipoConnessione = "tipo:"+tipoConnettore+" porta:delegata id_busta:"+identificatoreConnessione;
							this.logTimer.debug("--------------------------------------");
							this.logTimer.debug(tipoConnessione);
							this.logTimer.debug("SCADENZA ["+scadenza.toString()+"]   CONNETTORE ["+connettore.getCreationDate().toString()+"]");
							if(connettore.getCreationDate().before(scadenza)){
								this.logTimer.info("SCADUTO");
								this.msgDiag.addKeyword(CostantiPdD.KEY_TIMER_GESTORE_MESSAGGI_TIPO_CONNESSIONE,tipoConnessione);
								this.msgDiag.addKeyword(CostantiPdD.KEY_TIMER_GESTORE_MESSAGGI_DATA_CREAZIONE_CONNESSIONE,connettore.getCreationDate().toString());
								this.msgDiag.logPersonalizzato("connessioneScaduta.EliminazioneInCorso");
								
								connettore = RepositoryConnettori.removeConnettorePD(identificatoreConnessione);
								if(connettore!=null){
									connettore.disconnect();
								}
								
								this.msgDiag.logPersonalizzato("connessioneScaduta.EliminazioneEffettuata");
							}
							else{
								this.logTimer.debug("NON SCADUTO");
							}
							
							this.logTimer.debug("--------------------------------------");
							
						}catch(Exception e){
							// non mi interessa gestire l'eventuale eccezione, a parte registrarla
							if(tipoConnettore!=null){
								String msgErrore = "EliminazioneConnessione(tipo:"+tipoConnettore+" porta:delegata id_busta:"+identificatoreConnessione+")";
								this.msgDiag.logErroreGenerico(e,msgErrore);
								this.logTimer.error("Errore"+msgErrore+": "+e.getMessage(),e);
							}
							else{
								String msgErrore = "EliminazioneConnessione(porta:delegata id_busta:"+identificatoreConnessione+")";
								this.msgDiag.logErroreGenerico(e,msgErrore);
								this.logTimer.error("Errore"+msgErrore+": "+e.getMessage(),e);
							}
						}
						catch(Throwable e){
							// non mi interessa gestire l'eventuale eccezione, a parte registrarla
							if(tipoConnettore!=null){
								String msgErrore = "EliminazioneConnessione(tipo:"+tipoConnettore+" porta:delegata id_busta:"+identificatoreConnessione+")";
								this.msgDiag.logErroreGenerico(e,msgErrore);
								this.logTimer.error("Errore"+msgErrore+": "+e.getMessage(),e);
							}
							else{
								String msgErrore = "EliminazioneConnessione(porta:delegata id_busta:"+identificatoreConnessione+")";
								this.msgDiag.logErroreGenerico(e,msgErrore);
								this.logTimer.error("Errore"+msgErrore+": "+e.getMessage(),e);
							}
						}
					}
					
					if(this.logQuery){
						if(verificaEffettuata){
							this.logTimer.info("Terminata verifica connessioni attive sulle PorteDelegate");
						}
					}
				}
				
				
				// Connettori: porta applicativa
				Enumeration<String> identificatoriConnettoriPA = RepositoryConnettori.getIdentificatoriConnettori_pa();
				if(this.logQuery){
					if(identificatoriConnettoriPA==null || identificatoriConnettoriPA.hasMoreElements()==false){
						this.logTimer.info("Non sono state trovate connessioni attive sulle PorteApplicative");
					}
				}
				if(identificatoriConnettoriPA!=null){
					
					boolean verificaEffettuata = false;
					if(this.logQuery){
						if(identificatoriConnettoriPD.hasMoreElements()){
							this.logTimer.info("Sono state trovate connessioni attive sulle PorteApplicative, verifica in corso ...");
						}
					}
					
					while (identificatoriConnettoriPA.hasMoreElements()) {
						String identificatoreConnessione = identificatoriConnettoriPA.nextElement();
						String tipoConnettore = null;
						IConnettore connettore = null;
						try{
							connettore = RepositoryConnettori.getConnettorePA(identificatoreConnessione);
							// puo' essere stato eliminato nel frattempo
							if(connettore!=null){
								tipoConnettore = connettore.getClass().getSimpleName();
							}
							int millisecondiScadenza = this.propertiesReader.getConnectionLife_consegnaContenutiApplicativi();
							Date now = DateManager.getDate();
							Date scadenza = new Date(now.getTime()-millisecondiScadenza);
							String tipoConnessione = "tipo:"+tipoConnettore+" porta:applicativa id_busta:"+identificatoreConnessione;
							this.logTimer.debug("--------------------------------------");
							this.logTimer.debug(tipoConnessione);
							this.logTimer.debug("SCADENZA ["+scadenza.toString()+"]   CONNETTORE ["+connettore.getCreationDate().toString()+"]");
							if(connettore.getCreationDate().before(scadenza)){
								this.logTimer.info("SCADUTO");
								this.msgDiag.addKeyword(CostantiPdD.KEY_TIMER_GESTORE_MESSAGGI_TIPO_CONNESSIONE,tipoConnessione);
								this.msgDiag.addKeyword(CostantiPdD.KEY_TIMER_GESTORE_MESSAGGI_DATA_CREAZIONE_CONNESSIONE,connettore.getCreationDate().toString());
								this.msgDiag.logPersonalizzato("connessioneScaduta.EliminazioneInCorso");
								
								connettore = RepositoryConnettori.removeConnettorePA(identificatoreConnessione);
								if(connettore!=null){
									connettore.disconnect();
								}
								
								this.msgDiag.logPersonalizzato("connessioneScaduta.EliminazioneEffettuata");
							}
							else{
								this.logTimer.debug("NON SCADUTO");
							}
							
							this.logTimer.debug("--------------------------------------");
							
						}catch(Exception e){
							// non mi interessa gestire l'eventuale eccezione, a parte registrarla
							if(tipoConnettore!=null){
								String msgErrore = "EliminazioneConnessione(tipo:"+tipoConnettore+" porta:applicativa id_busta:"+identificatoreConnessione+")";
								this.msgDiag.logErroreGenerico(e,msgErrore);
								this.logTimer.error("Errore"+msgErrore+": "+e.getMessage(),e);
							}
							else{
								String msgErrore = "EliminazioneConnessione(porta:applicativa id_busta:"+identificatoreConnessione+")";
								this.msgDiag.logErroreGenerico(e,msgErrore);
								this.logTimer.error("Errore"+msgErrore+": "+e.getMessage(),e);
							}
						}
						catch(Throwable e){
							// non mi interessa gestire l'eventuale eccezione, a parte registrarla
							if(tipoConnettore!=null){
								String msgErrore = "EliminazioneConnessione(tipo:"+tipoConnettore+" porta:applicativa id_busta:"+identificatoreConnessione+")";
								this.msgDiag.logErroreGenerico(e,msgErrore);
								this.logTimer.error("Errore"+msgErrore+": "+e.getMessage(),e);
							}	
							else{
								String msgErrore = "EliminazioneConnessione(porta:applicativa id_busta:"+identificatoreConnessione+")";
								this.msgDiag.logErroreGenerico(e,msgErrore);
								this.logTimer.error("Errore"+msgErrore+": "+e.getMessage(),e);
							}
						}
					}
					
					if(this.logQuery){
						if(verificaEffettuata){
							this.logTimer.info("Terminata verifica connessioni attive sulle PorteApplicative");
						}
					}
					
					
				}
				
			}
			else{
				this.logTimer.warn("Non verifico connessioni attive");
			}
			
			
			
			// end
            long endControlloRepositoryMessaggi = DateManager.getTimeMillis();
            this.logTimer.info("Controllo Repository Messaggi terminata in "+((endControlloRepositoryMessaggi-startControlloRepositoryMessaggi)/1000) +" secondi");

			
		} catch (Exception e) {
			this.msgDiag.logErroreGenerico(e,"GestioneMessaggi");
			this.logTimer.error("Riscontrato errore durante la gestione del repository dei messaggi: "+ e.getMessage(),e);
		}finally{
			if(openspcoopstate!=null)
				openspcoopstate.releaseResource();
		}
	}

}
