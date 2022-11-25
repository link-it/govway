/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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



package org.openspcoop2.pdd.timers;

import java.sql.Connection;
import java.util.Date;
import java.util.List;

import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.transazioni.TransazioneApplicativoServer;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.core.GestoreCorrelazioneApplicativa;
import org.openspcoop2.pdd.core.GestoreMessaggi;
import org.openspcoop2.pdd.core.JMSReceiver;
import org.openspcoop2.pdd.core.MessaggioServizioApplicativo;
import org.openspcoop2.pdd.core.connettori.IConnettore;
import org.openspcoop2.pdd.core.connettori.RepositoryConnettori;
import org.openspcoop2.pdd.core.state.OpenSPCoopStateful;
import org.openspcoop2.pdd.core.transazioni.GestoreConsegnaMultipla;
import org.openspcoop2.pdd.logger.MsgDiagnostico;
import org.openspcoop2.pdd.services.OpenSPCoop2Startup;
import org.openspcoop2.pdd.services.core.RicezioneBuste;
import org.openspcoop2.pdd.services.core.RicezioneContenutiApplicativi;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.protocol.engine.driver.RollbackRepositoryBuste;
import org.openspcoop2.protocol.sdk.state.StateMessage;
import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.id.serial.InfoStatistics;
import org.openspcoop2.utils.semaphore.Semaphore;
import org.openspcoop2.utils.semaphore.SemaphoreConfiguration;
import org.openspcoop2.utils.semaphore.SemaphoreMapping;
import org.slf4j.Logger;


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

	private static TimerState STATE_MESSAGGI_ELIMINATI = TimerState.OFF; // abilitato in OpenSPCoop2Startup al momento dell'avvio
	public static TimerState getSTATE_MESSAGGI_ELIMINATI() {
		return STATE_MESSAGGI_ELIMINATI;
	}
	public static void setSTATE_MESSAGGI_ELIMINATI(TimerState sTATE_MESSAGGI_ELIMINATI) {
		STATE_MESSAGGI_ELIMINATI = sTATE_MESSAGGI_ELIMINATI;
	}

	private static TimerState STATE_MESSAGGI_SCADUTI = TimerState.OFF; // abilitato in OpenSPCoop2Startup al momento dell'avvio
	public static TimerState getSTATE_MESSAGGI_SCADUTI() {
		return STATE_MESSAGGI_SCADUTI;
	}
	public static void setSTATE_MESSAGGI_SCADUTI(TimerState sTATE_MESSAGGI_SCADUTI) {
		STATE_MESSAGGI_SCADUTI = sTATE_MESSAGGI_SCADUTI;
	}

	private static TimerState STATE_MESSAGGI_NON_GESTITI = TimerState.OFF; // abilitato in OpenSPCoop2Startup al momento dell'avvio
	public static TimerState getSTATE_MESSAGGI_NON_GESTITI() {
		return STATE_MESSAGGI_NON_GESTITI;
	}
	public static void setSTATE_MESSAGGI_NON_GESTITI(TimerState sTATE_MESSAGGI_NON_GESTITI) {
		STATE_MESSAGGI_NON_GESTITI = sTATE_MESSAGGI_NON_GESTITI;
	}

	private static TimerState STATE_CORRELAZIONE_APPLICATIVA = TimerState.OFF; // abilitato in OpenSPCoop2Startup al momento dell'avvio
	public static TimerState getSTATE_CORRELAZIONE_APPLICATIVA() {
		return STATE_CORRELAZIONE_APPLICATIVA;
	}
	public static void setSTATE_CORRELAZIONE_APPLICATIVA(TimerState sTATE_CORRELAZIONE_APPLICATIVA) {
		STATE_CORRELAZIONE_APPLICATIVA = sTATE_CORRELAZIONE_APPLICATIVA;
	}

	private static TimerState STATE_VERIFICA_CONNESSIONI_ATTIVE = TimerState.OFF; // abilitato in OpenSPCoop2Startup al momento dell'avvio
	public static TimerState getSTATE_VERIFICA_CONNESSIONI_ATTIVE() {
		return STATE_VERIFICA_CONNESSIONI_ATTIVE;
	}
	public static void setSTATE_VERIFICA_CONNESSIONI_ATTIVE(TimerState sTATE_VERIFICA_CONNESSIONI_ATTIVE) {
		STATE_VERIFICA_CONNESSIONI_ATTIVE = sTATE_VERIFICA_CONNESSIONI_ATTIVE;
	}

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

	private TimerLock timerLock = null;
	private TimerLock timerLockCorrelazioneApplicativa = null;

	/** Semaforo */
	private Semaphore semaphore = null;
	private InfoStatistics semaphore_statistics;

	public TimerGestoreMessaggiLib(MsgDiagnostico msgDiag,Logger log,OpenSPCoop2Properties p,
			long scadenzaMessaggio,boolean logQuery,
			int limit,boolean orderByQuery, 
			long scadenzaCorrelazioneApplicativa,
			boolean filtraCorrelazioniApplicativeScaduteRispettoOraRegistrazione,
			boolean filtraCorrelazioniApplicativeScaduteRispettoOraRegistrazione_escludiCorrelazioniConScadenzaImpostata) throws TimerException{
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

		// deve essere utilizzato lo stesso lock per GestoreMessaggi, ConsegnaContenuti, GestoreBuste per risolvere problema di eliminazione descritto in GestoreMessaggi metodo deleteMessageWithLock
		if(this.propertiesReader.isMsgGiaInProcessamento_useLock()) {
			this.timerLock = new TimerLock(TipoLock._getLockGestioneRepositoryMessaggi());
		}
		else {
			this.timerLock = new TimerLock(TipoLock.GESTIONE_PULIZIA_REPOSITORY_MESSAGGI);
		}

		this.timerLockCorrelazioneApplicativa = new TimerLock(TipoLock.GESTIONE_CORRELAZIONE_APPLICATIVA); 

		if(this.propertiesReader.isTimerLockByDatabase()) {
			this.semaphore_statistics = new InfoStatistics();

			SemaphoreConfiguration config = GestoreMessaggi.newSemaphoreConfiguration(this.propertiesReader.getTimerGestoreMessaggi_lockMaxLife(), 
					this.propertiesReader.getTimerGestoreMessaggi_lockIdleTime());

			TipiDatabase databaseType = TipiDatabase.toEnumConstant(this.propertiesReader.getDatabaseType());
			try {
				this.semaphore = new Semaphore(this.semaphore_statistics, SemaphoreMapping.newInstance(this.timerLock.getIdLock()), 
						config, databaseType, this.logTimer);
			}catch(Exception e) {
				throw new TimerException(e.getMessage(),e);
			}
		}
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
		if( TimerMonitoraggioRisorseThread.risorseDisponibili == false){
			this.logTimer.error("["+TimerGestoreMessaggi.ID_MODULO+"] Risorse di sistema non disponibili: "+TimerMonitoraggioRisorseThread.risorsaNonDisponibile.getMessage(),TimerMonitoraggioRisorseThread.risorsaNonDisponibile);
			return;
		}
		if( MsgDiagnostico.gestoreDiagnosticaDisponibile == false){
			this.logTimer.error("["+TimerGestoreMessaggi.ID_MODULO+"] Sistema di diagnostica non disponibile: "+MsgDiagnostico.motivoMalfunzionamentoDiagnostici.getMessage(),MsgDiagnostico.motivoMalfunzionamentoDiagnostici);
			return;
		}
		
		// Controllo che il timer non sia stato momentaneamente disabilitato
		if(!TimerState.ENABLED.equals(STATE_MESSAGGI_ELIMINATI) && 
				!TimerState.ENABLED.equals(STATE_MESSAGGI_SCADUTI) && 
				!TimerState.ENABLED.equals(STATE_MESSAGGI_NON_GESTITI) &&
				!TimerState.ENABLED.equals(STATE_CORRELAZIONE_APPLICATIVA) && 
				!TimerState.ENABLED.equals(STATE_VERIFICA_CONNESSIONI_ATTIVE)) {
			this.msgDiag.logPersonalizzato("disabilitato");
			this.logTimer.info(this.msgDiag.getMessaggio_replaceKeywords("disabilitato"));
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
			openspcoopstate.initResource(this.propertiesReader.getIdentitaPortaDefaultWithoutProtocol(),TimerGestoreMessaggi.ID_MODULO, null);
			Connection connectionDB = ((StateMessage)openspcoopstate.getStatoRichiesta()).getConnectionDB();

			// filtroJMS
			JMSReceiver receiverJMS = null;
			if(CostantiConfigurazione.COMUNICAZIONE_INFRASTRUTTURALE_JMS.equals(this.propertiesReader.getNodeReceiver()))
				receiverJMS = new JMSReceiver(this.propertiesReader.getIdentitaPortaDefaultWithoutProtocol(),"ForcedDeleteMessage",this.propertiesReader.singleConnection_NodeReceiver(),this.logTimer, null);

			// Messaggi da eliminare 
			GestoreMessaggi gestoreMsgSearch = new GestoreMessaggi(openspcoopstate, true,this.logTimer,this.msgDiag, null);
			long timeoutRicezioneContenutiApplicativiNonGestiti = this.propertiesReader.getNodeReceiverTimeoutRicezioneContenutiApplicativi() * 3;
			long timeoutRicezioneBusteNonGestiti = this.propertiesReader.getNodeReceiverTimeoutRicezioneBuste() * 3;
			boolean trovatiMessaggi = true;

			while(trovatiMessaggi){

				trovatiMessaggi = false;


				Date now = null;
				if(!this.propertiesReader.isMsgGiaInProcessamento_useLock()) {
					now = DateManager.getDate(); // vedi spiegazione nel metodo deleteMessageByOraRegistrazione di GestoreMessaggi
				}


				/* --- Messaggi da eliminare (non scaduti) --- */ 
				
				List<String> idMsgInutiliINBOX = null;
				List<String> idMsgInutiliOUTBOX = null;
				
				if(TimerState.ENABLED.equals(STATE_MESSAGGI_ELIMINATI)) {
				
					// Eliminazione Messaggi from INBOX
					String causaMessaggiINBOXDaEliminareNonScaduti = "Eliminazione messaggi INBOX marcati logicamente da eliminare";
					try{
						GestoreMessaggi.acquireLock(
								this.semaphore, connectionDB, this.timerLock,
								this.msgDiag, causaMessaggiINBOXDaEliminareNonScaduti, 
								this.propertiesReader.getTimerGestoreMessaggi_getLockAttesaAttiva(), 
								this.propertiesReader.getTimerGestoreMessaggi_getLockCheckInterval());
	
						idMsgInutiliINBOX = gestoreMsgSearch.readMessaggiInutiliIntoInbox(TimerGestoreMessaggi.ID_MODULO,this.limit,this.logQuery,this.orderByQuery,now);
						int gestiti = 0;
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
									try{
										GestoreMessaggi.updateLock(
												this.semaphore, connectionDB, this.timerLock,
												this.msgDiag, "Eliminazione messaggio INBOX con id ["+idMsgDaEliminare+"] ...");
									}catch(Throwable e){
										this.msgDiag.logErroreGenerico(e,"EliminazioneMessaggioInbox("+idMsgDaEliminare+")-UpdateLock");
										this.logTimer.error("ErroreEliminazioneMessaggioInbox("+idMsgDaEliminare+")-UpdateLock: "+e.getMessage(),e);
										break;
									}
	
									// eliminazione messaggio
									gestoreMsg = new GestoreMessaggi(openspcoopstate, true
											,idMsgDaEliminare,Costanti.INBOX,this.msgDiag,null);
									if(this.propertiesReader.isMsgGiaInProcessamento_useLock()) {
										gestoreMsg._deleteMessageWithoutLock();
									}
									else {
										gestoreMsg.deleteMessageByOraRegistrazione(now);
									}
	
									this.msgDiag.logPersonalizzato("eliminazioneMessaggio");
									if(this.logQuery)
										this.logTimer.debug(this.msgDiag.getMessaggio_replaceKeywords("eliminazioneMessaggio"));
	
									gestiti++;
	
								}catch(Exception e){
									this.msgDiag.logErroreGenerico(e,"EliminazioneMessaggioInbox("+idMsgDaEliminare+")");
									this.logTimer.error("ErroreEliminazioneMessaggioInbox("+idMsgDaEliminare+"): "+e.getMessage(),e);
								}
							}
	
							if(this.logQuery)
								this.logTimer.info("Eliminati "+gestiti+" messaggi (cancellazione logica) nel repository (INBOX)");
						}
						else{
							if(this.logQuery)
								this.logTimer.info("Non sono stati trovati messaggi (cancellazione logica) da eliminare nel repository (INBOX)");
						}
					}finally{
						try{
							GestoreMessaggi.releaseLock(
									this.semaphore, connectionDB, this.timerLock,
									this.msgDiag, causaMessaggiINBOXDaEliminareNonScaduti);
						}catch(Exception e){
							// ignore
						}
					}
	
					//	Eliminazione Messaggi from OUTBOX
					String causaMessaggiOUTBOXDaEliminareNonScaduti = "Eliminazione messaggi OUTBOX marcati logicamente da eliminare";
					try{
						GestoreMessaggi.acquireLock(
								this.semaphore, connectionDB, this.timerLock,
								this.msgDiag, causaMessaggiOUTBOXDaEliminareNonScaduti, 
								this.propertiesReader.getTimerGestoreMessaggi_getLockAttesaAttiva(), 
								this.propertiesReader.getTimerGestoreMessaggi_getLockCheckInterval());
	
						idMsgInutiliOUTBOX = gestoreMsgSearch.readMessaggiInutiliIntoOutbox(TimerGestoreMessaggi.ID_MODULO,this.limit,this.logQuery,this.orderByQuery,now);
						int gestiti = 0;
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
									try{
										GestoreMessaggi.updateLock(
												this.semaphore, connectionDB, this.timerLock,
												this.msgDiag, "Eliminazione messaggio OUTBOX con id ["+idMsgDaEliminare+"] ...");
									}catch(Throwable e){
										this.msgDiag.logErroreGenerico(e,"EliminazioneMessaggioOutbox("+idMsgDaEliminare+")-UpdateLock");
										this.logTimer.error("ErroreEliminazioneMessaggioOutbox("+idMsgDaEliminare+")-UpdateLock: "+e.getMessage(),e);
										break;
									}
	
									// eliminazione messaggio
									gestoreMsg = new GestoreMessaggi(openspcoopstate, true,idMsgDaEliminare,Costanti.OUTBOX,this.msgDiag,null);
									if(this.propertiesReader.isMsgGiaInProcessamento_useLock()) {
										gestoreMsg._deleteMessageWithoutLock();
									}
									else {
										gestoreMsg.deleteMessageByOraRegistrazione(now);
									}
	
									this.msgDiag.logPersonalizzato("eliminazioneMessaggio");
									if(this.logQuery)
										this.logTimer.debug(this.msgDiag.getMessaggio_replaceKeywords("eliminazioneMessaggio"));
	
									gestiti++;
	
								}catch(Exception e){
									this.msgDiag.logErroreGenerico(e,"EliminazioneMessaggioOutbox("+idMsgDaEliminare+")");
									this.logTimer.error("ErroreEliminazioneMessaggioOutbox("+idMsgDaEliminare+"): "+e.getMessage(),e);
								}		
							}
	
							if(this.logQuery)
								this.logTimer.info("Eliminati "+gestiti+" messaggi (cancellazione logica) nel repository (OUTBOX)");
						}
						else{
							if(this.logQuery)
								this.logTimer.info("Non sono stati trovati messaggi (cancellazione logica) da eliminare nel repository (OUTBOX)");
						}
					}finally{
						try{
							GestoreMessaggi.releaseLock(
									this.semaphore, connectionDB, this.timerLock,
									this.msgDiag, causaMessaggiOUTBOXDaEliminareNonScaduti);
						}catch(Exception e){
							// ignore
						}
					}
					
				}
				else {
					if(this.logQuery) {
						this.logTimer.info("Gestione messaggi (cancellazione logica) disabilitata");
					}	
				}






				/* --- Messaggi scaduti, verra' effettuato anche un rollback delle Buste. --- */

				List<String> idMsgScadutiINBOX = null;
				List<String> idMsgScadutiOUTBOX = null;
				
				if(TimerState.ENABLED.equals(STATE_MESSAGGI_SCADUTI)) {
				
					//	Eliminazione Messaggi from INBOX
					String causaMessaggiINBOXScaduti = "Eliminazione messaggi INBOX scaduti";
					try{
						GestoreMessaggi.acquireLock(
								this.semaphore, connectionDB, this.timerLock,
								this.msgDiag, causaMessaggiINBOXScaduti, 
								this.propertiesReader.getTimerGestoreMessaggi_getLockAttesaAttiva(), 
								this.propertiesReader.getTimerGestoreMessaggi_getLockCheckInterval());
	
						idMsgScadutiINBOX = gestoreMsgSearch.readMessaggiScadutiIntoInbox(this.scadenzaMessaggio,this.limit,this.logQuery,this.orderByQuery,now);
						int gestiti = 0;
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
									try{
										GestoreMessaggi.updateLock(
												this.semaphore, connectionDB, this.timerLock,
												this.msgDiag, "Eliminazione messaggio scaduto INBOX con id ["+idMsgDaEliminare+"] ...");
									}catch(Throwable e){
										this.msgDiag.logErroreGenerico(e,"EliminazioneMessaggioScadutoInbox("+idMsgDaEliminare+")-UpdateLock");
										this.logTimer.error("ErroreEliminazioneMessaggioScadutoInbox("+idMsgDaEliminare+")-UpdateLock: "+e.getMessage(),e);
										break;
									}
	
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
	
									// aggiorno eventuali destinatari nella transazione prima di eliminare il messaggio
									try {
										List<MessaggioServizioApplicativo> listMsgServiziApplicativi = gestoreMsg.readInfoDestinatari(this.logQuery, this.logTimer);
										if(listMsgServiziApplicativi!=null && !listMsgServiziApplicativi.isEmpty()) {
											
											for (MessaggioServizioApplicativo messaggioServizioApplicativo : listMsgServiziApplicativi) {
												TransazioneApplicativoServer transazioneApplicativoServer = new TransazioneApplicativoServer();
												transazioneApplicativoServer.setIdTransazione(messaggioServizioApplicativo.getIdTransazione());
												transazioneApplicativoServer.setServizioApplicativoErogatore(messaggioServizioApplicativo.getServizioApplicativo());
												transazioneApplicativoServer.setDataRegistrazione(messaggioServizioApplicativo.getOraRegistrazione());
												transazioneApplicativoServer.setDataMessaggioScaduto(DateManager.getDate());
												transazioneApplicativoServer.setProtocollo(this.propertiesReader.getDefaultProtocolName()); // non è importante per impostare la scadenza
												IDPortaApplicativa idPA = new IDPortaApplicativa();
												idPA.setNome(messaggioServizioApplicativo.getNomePorta());
												try {
													GestoreConsegnaMultipla.getInstance().safeUpdateMessaggioScaduto(transazioneApplicativoServer, idPA, openspcoopstate);
												}catch(Throwable t) {
													this.logTimer.error("["+transazioneApplicativoServer.getIdTransazione()+"]["+transazioneApplicativoServer.getServizioApplicativoErogatore()+"] Errore durante il salvataggio delle informazioni relative al servizio applicativo: "+t.getMessage(),t);
												}
											}
										}
									}catch(Throwable t) {
										this.logTimer.error("Errore durante l'aggiornamento dello stato degli applicativi nelle transazioni: "+t.getMessage(),t);
									}
									
									// Eliminazione effettiva
									((StateMessage)openspcoopstate.getStatoRichiesta()).executePreparedStatement();
									//	eliminazione messaggio
									if(this.propertiesReader.isMsgGiaInProcessamento_useLock()) {
										gestoreMsg._deleteMessageWithoutLock();
									}
									else {
										gestoreMsg.deleteMessageByOraRegistrazione(now);
									}
	
									this.msgDiag.logPersonalizzato("eliminazioneMessaggio");
									if(this.logQuery)
										this.logTimer.debug(this.msgDiag.getMessaggio_replaceKeywords("eliminazioneMessaggio"));
	
									gestiti++;
	
								}catch(Exception e){
	
									((StateMessage)openspcoopstate.getStatoRichiesta()).closePreparedStatement();
									this.msgDiag.logErroreGenerico(e,"EliminazioneMessaggioScadutoInbox("+idMsgDaEliminare+")");
									this.logTimer.error("ErroreEliminazioneMessaggioScadutoInbox("+idMsgDaEliminare+"): "+e.getMessage(),e);
								}
							}
	
							if(this.logQuery)
								this.logTimer.info("Eliminati "+gestiti+" messaggi (scaduti) nel repository (INBOX)");
						}
						else{
							if(this.logQuery)
								this.logTimer.info("Non sono stati trovati messaggi (scaduti) da eliminare nel repository (INBOX)");
						}
					}finally{
						try{
							GestoreMessaggi.releaseLock(
									this.semaphore, connectionDB, this.timerLock,
									this.msgDiag, causaMessaggiINBOXScaduti);
						}catch(Exception e){
							// ignore
						}
					}
	
					// Eliminazione Messaggi from OUTBOX
					String causaMessaggiOUTBOXScaduti = "Eliminazione messaggi OUTBOX scaduti";
					try{
						GestoreMessaggi.acquireLock(
								this.semaphore, connectionDB, this.timerLock,
								this.msgDiag, causaMessaggiOUTBOXScaduti, 
								this.propertiesReader.getTimerGestoreMessaggi_getLockAttesaAttiva(), 
								this.propertiesReader.getTimerGestoreMessaggi_getLockCheckInterval());
	
						idMsgScadutiOUTBOX = gestoreMsgSearch.readMessaggiScadutiIntoOutbox(this.scadenzaMessaggio,this.limit,this.logQuery,this.orderByQuery, now);
						int gestiti = 0;
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
									try{
										GestoreMessaggi.updateLock(
												this.semaphore, connectionDB, this.timerLock,
												this.msgDiag, "Eliminazione messaggio scaduto OUTBOX con id ["+idMsgDaEliminare+"] ...");
									}catch(Throwable e){
										this.msgDiag.logErroreGenerico(e,"EliminazioneMessaggioScadutoOutbox("+idMsgDaEliminare+")-UpdateLock");
										this.logTimer.error("ErroreEliminazioneMessaggioScadutoOutbox("+idMsgDaEliminare+")-UpdateLock: "+e.getMessage(),e);
										break;
									}
	
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
									if(this.propertiesReader.isMsgGiaInProcessamento_useLock()) {
										gestoreMsg._deleteMessageWithoutLock();
									}
									else {
										gestoreMsg.deleteMessageByOraRegistrazione(now);
									}
	
									this.msgDiag.logPersonalizzato("eliminazioneMessaggio");
									if(this.logQuery)
										this.logTimer.debug(this.msgDiag.getMessaggio_replaceKeywords("eliminazioneMessaggio"));
	
									gestiti++;
	
								}catch(Exception e){
									((StateMessage)openspcoopstate.getStatoRichiesta()).closePreparedStatement();
									this.msgDiag.logErroreGenerico(e,"EliminazioneMessaggioScadutoOutbox("+idMsgDaEliminare+")");
									this.logTimer.error("ErroreEliminazioneMessaggioScadutoOutbox("+idMsgDaEliminare+"): "+e.getMessage(),e);
								}		
							}
	
							if(this.logQuery)
								this.logTimer.info("Eliminati "+gestiti+" messaggi (scaduti) nel repository (OUTBOX)");
	
						}
						else{
							if(this.logQuery)
								this.logTimer.info("Non sono stati trovati messaggi (scaduti) da eliminare nel repository (OUTBOX)");
						}
					}finally{
						try{
							GestoreMessaggi.releaseLock(
									this.semaphore, connectionDB, this.timerLock,
									this.msgDiag, causaMessaggiOUTBOXScaduti);
						}catch(Exception e){
							// ignore
						}
					}

				}
				else {
					if(this.logQuery) {
						this.logTimer.info("Gestione messaggi (scaduti) disabilitata");
					}	
				}




				List<String> idMsgServizioRicezioneContenutiApplicativiNonGestiti = null;
				List<String> idMsgServizioRicezioneBusteNonGestiti = null;
				
				if(TimerState.ENABLED.equals(STATE_MESSAGGI_NON_GESTITI)) {

					/* --- Messaggi non gestiti dal servizio web RicezioneContenutiApplicativi, verra' effettuato anche un rollback delle Buste. ---*/
	
					//	Eliminazione Messaggi from INBOX
					String causaMessaggiINBOXNonGestitiRicezioneContenutiApplicativi = "Eliminazione messaggi INBOX non gestiti dal servizio RicezioneContenutiApplicativi";
					try{
						GestoreMessaggi.acquireLock(
								this.semaphore, connectionDB, this.timerLock,
								this.msgDiag, causaMessaggiINBOXNonGestitiRicezioneContenutiApplicativi, 
								this.propertiesReader.getTimerGestoreMessaggi_getLockAttesaAttiva(), 
								this.propertiesReader.getTimerGestoreMessaggi_getLockCheckInterval());
	
						idMsgServizioRicezioneContenutiApplicativiNonGestiti = 
								gestoreMsgSearch.readMsgForRicezioneContenutiApplicativiNonGestiti(timeoutRicezioneContenutiApplicativiNonGestiti, this.limit,this.logQuery,this.orderByQuery);
						int gestiti = 0;
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
									try{
										GestoreMessaggi.updateLock(
												this.semaphore, connectionDB, this.timerLock,
												this.msgDiag, "Eliminazione messaggio INBOX non gestiti dal servizio RicezioneContenutiApplicativi con id ["+idMsgDaEliminare+"] ...");
									}catch(Throwable e){
										this.msgDiag.logErroreGenerico(e,"EliminazioneMessaggioRicezioneContenutiApplicativiNonGestiti("+idMsgDaEliminare+")-UpdateLock");
										this.logTimer.error("ErroreEliminazioneMessaggioRicezioneContenutiApplicativiNonGestiti("+idMsgDaEliminare+")-UpdateLock: "+e.getMessage(),e);
										break;
									}
	
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
	
									gestiti++;
	
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
								this.logTimer.info("Eliminati "+gestiti+" messaggi (non gestiti da 'RicezioneContenutiApplicativi') nel repository (INBOX)");
	
						}
						else{
							if(this.logQuery)
								this.logTimer.info("Non sono stati trovati messaggi (non gestiti da 'RicezioneContenutiApplicativi') da eliminare nel repository (INBOX)");
						}
					}finally{
						try{
							GestoreMessaggi.releaseLock(
									this.semaphore, connectionDB, this.timerLock,
									this.msgDiag, causaMessaggiINBOXNonGestitiRicezioneContenutiApplicativi);
						}catch(Exception e){
							// ignore
						}
					}
	
	
	
					/* --- Messaggi non gestiti dal servizio web RicezioneBuste, verra' effettuato anche un rollback delle Buste. --- */
	
					//	Eliminazione Messaggi from OUTBOX
					String causaMessaggiOUTBOXNonGestitiRicezioneBuste = "Eliminazione messaggi OUTBOX non gestiti dal servizio RicezioneBuste";
					try{
						GestoreMessaggi.acquireLock(
								this.semaphore, connectionDB, this.timerLock,
								this.msgDiag, causaMessaggiOUTBOXNonGestitiRicezioneBuste, 
								this.propertiesReader.getTimerGestoreMessaggi_getLockAttesaAttiva(), 
								this.propertiesReader.getTimerGestoreMessaggi_getLockCheckInterval());
	
						idMsgServizioRicezioneBusteNonGestiti = 
								gestoreMsgSearch.readMsgForRicezioneBusteNonGestiti(timeoutRicezioneBusteNonGestiti, this.limit,this.logQuery,this.orderByQuery);
						int gestiti = 0;
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
									try{
										GestoreMessaggi.updateLock(
												this.semaphore, connectionDB, this.timerLock,
												this.msgDiag, "Eliminazione messaggio OUTBOX non gestiti dal servizio RicezioneBuste con id ["+idMsgDaEliminare+"] ...");
									}catch(Throwable e){
										this.msgDiag.logErroreGenerico(e,"EliminazioneMessaggioRicezioneBusteNonGestiti("+idMsgDaEliminare+")-UpdateLock");
										this.logTimer.error("ErroreEliminazioneMessaggioRicezioneBusteNonGestiti("+idMsgDaEliminare+")-UpdateLock: "+e.getMessage(),e);
										break;
									}
	
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
									if(this.propertiesReader.isMsgGiaInProcessamento_useLock()) {
										gestoreMsg._deleteMessageWithoutLock();
									}
									else {
										gestoreMsg.deleteMessageByOraRegistrazione(now);
									}
	
									this.msgDiag.logPersonalizzato("eliminazioneMessaggio");
									if(this.logQuery)
										this.logTimer.debug(this.msgDiag.getMessaggio_replaceKeywords("eliminazioneMessaggio"));
	
									gestiti++;
	
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
								this.logTimer.info("Eliminati "+gestiti+" messaggi (non gestiti da 'RicezioneBuste') nel repository (OUTBOX)");
	
						}
						else{
							if(this.logQuery)
								this.logTimer.info("Non sono stati trovati messaggi (non gestiti da 'RicezioneBuste') da eliminare nel repository (OUTBOX)");
						}
					}finally{
						try{
							GestoreMessaggi.releaseLock(
									this.semaphore, connectionDB, this.timerLock,
									this.msgDiag, causaMessaggiOUTBOXNonGestitiRicezioneBuste);
						}catch(Exception e){
							// ignore
						}
					}
					
				}
				else {
					if(this.logQuery) {
						this.logTimer.info("Gestione messaggi (non gestiti) disabilitata");
					}	
				}
				
				

				// log finale  
				if(trovatiMessaggi){
					this.msgDiag.addKeyword(CostantiPdD.KEY_TIMER_GESTORE_MESSAGGI_NUM_MSG_INBOX_COMPLETATI, idMsgInutiliINBOX!=null ? idMsgInutiliINBOX.size()+"" : 0+"");
					this.msgDiag.addKeyword(CostantiPdD.KEY_TIMER_GESTORE_MESSAGGI_NUM_MSG_OUTBOX_COMPLETATI, idMsgInutiliOUTBOX!=null ? idMsgInutiliOUTBOX.size()+"" : 0+"");
					this.msgDiag.addKeyword(CostantiPdD.KEY_TIMER_GESTORE_MESSAGGI_NUM_MSG_INBOX_SCADUTI, idMsgScadutiINBOX!=null ? idMsgScadutiINBOX.size()+"" : 0+"");
					this.msgDiag.addKeyword(CostantiPdD.KEY_TIMER_GESTORE_MESSAGGI_NUM_MSG_OUTBOX_SCADUTI, idMsgScadutiOUTBOX!=null ? idMsgScadutiOUTBOX.size()+"" : 0+"");
					this.msgDiag.addKeyword(CostantiPdD.KEY_TIMER_GESTORE_MESSAGGI_NUM_MSG_NON_GESTITI_RICEZIONE_CONTENUTI_APPLICATIVI, 
							idMsgServizioRicezioneContenutiApplicativiNonGestiti!=null ? idMsgServizioRicezioneContenutiApplicativiNonGestiti.size()+"" : 0+"");
					this.msgDiag.addKeyword(CostantiPdD.KEY_TIMER_GESTORE_MESSAGGI_NUM_MSG_NON_GESTITI_RICEZIONE_BUSTE, 
							idMsgServizioRicezioneBusteNonGestiti!=null ? idMsgServizioRicezioneBusteNonGestiti.size()+"" : 0+"");
					this.msgDiag.logPersonalizzato("ricercaMessaggiDaEliminare");
				}

			}







			if(TimerState.ENABLED.equals(STATE_CORRELAZIONE_APPLICATIVA)) {

				//	CorrelazioniApplicative da eliminare
				GestoreCorrelazioneApplicativa gestoreCorrelazioneApplicativa = new GestoreCorrelazioneApplicativa(openspcoopstate.getStatoRichiesta(),this.logTimer,null,null);
	
	
				// -- Scadute (Correlazioni per cui era stata impostata una scadenza) --
				String causaCorrelazioniApplicativeScadute = "Eliminazione correlazioni applicative scadute";
				List<Long> correlazioniScadute;
				try{
					GestoreMessaggi.acquireLock(
							this.semaphore, connectionDB, this.timerLockCorrelazioneApplicativa,
							this.msgDiag, causaCorrelazioniApplicativeScadute, 
							this.propertiesReader.getGestioneSerializableDB_AttesaAttiva(), 
							this.propertiesReader.getGestioneSerializableDB_CheckInterval());
	
					correlazioniScadute = gestoreCorrelazioneApplicativa.getCorrelazioniScadute(this.limit,this.logQuery,this.orderByQuery);
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
	
						int gestiti = 0;
						while(correlazioniScadute.size()>0){
							Long idCorrelazioneScaduta = correlazioniScadute.remove(0);
	
							String idCorrelato = null;
							String idApplicativo = null;
	
							try{	
								String [] id = gestoreCorrelazioneApplicativa.getIDMappingCorrelazioneApplicativa(idCorrelazioneScaduta);
								idCorrelato = id[0];
								idApplicativo = id[1];
								try{
									GestoreMessaggi.updateLock(
											this.semaphore, connectionDB, this.timerLockCorrelazioneApplicativa,
											this.msgDiag, "Eliminazione correlazione applicativa scaduta (id_applicativo:"+idApplicativo+",id_busta:"+idCorrelato+") ...");
								}catch(Throwable e){
									this.msgDiag.logErroreGenerico(e,"EliminazioneCorrelazioni(id_applicativo:"+idApplicativo+",id_busta:"+idCorrelato+")-UpdateLock");
									this.logTimer.error("ErroreEliminazioneCorrelazioni(id_applicativo:"+idApplicativo+",id_busta:"+idCorrelato+")-UpdateLock: "+e.getMessage(),e);
									break;
								}
	
								this.msgDiag.addKeyword(CostantiPdD.KEY_ID_MESSAGGIO_DA_ELIMINARE,idCorrelato);
								this.msgDiag.addKeyword(CostantiPdD.KEY_ID_CORRELAZIONE_APPLICATIVA,idApplicativo);
	
								gestoreCorrelazioneApplicativa.deleteCorrelazioneApplicativa(idCorrelazioneScaduta);
								this.msgDiag.logPersonalizzato("eliminazioneCorrelazioneApplicativaScaduta");
								if(this.logQuery)
									this.logTimer.debug(this.msgDiag.getMessaggio_replaceKeywords("eliminazioneCorrelazioneApplicativaScaduta"));
	
								gestiti++;
	
							}catch(Exception e){
								this.msgDiag.logErroreGenerico(e,"EliminazioneCorrelazioni(id_applicativo:"+idApplicativo+",id_busta:"+idCorrelato+")");
								this.logTimer.error("ErroreEliminazioneCorrelazioni(id_applicativo:"+idApplicativo+",id_busta:"+idCorrelato+"): "+e.getMessage(),e);
							}
						}
	
						if(this.logQuery)
							this.logTimer.info("Eliminate "+gestiti+" correlazioni applicative (scadute)");
	
						boolean cerca = true;
						try{
							GestoreMessaggi.updateLock(
									this.semaphore, connectionDB, this.timerLockCorrelazioneApplicativa,
									this.msgDiag, "Ricerca nuove correlazioni scadute  ...");
						}catch(Throwable e){
							this.msgDiag.logErroreGenerico(e,"RicercaNuoveCorrelazioni-UpdateLock");
							this.logTimer.error("RicercaNuoveCorrelazioni-UpdateLock: "+e.getMessage(),e);
							cerca = false;
						}
						
						if(cerca) {
							// Check altre correlazioni da eliminare
							correlazioniScadute = gestoreCorrelazioneApplicativa.getCorrelazioniScadute(this.limit,this.logQuery,this.orderByQuery);
						}
						else {
							correlazioniScadute = new java.util.ArrayList<Long>(); // per uscire dal while
						}
					}
				}finally{
					try{
						GestoreMessaggi.releaseLock(
								this.semaphore, connectionDB, this.timerLockCorrelazioneApplicativa,
								this.msgDiag, causaCorrelazioniApplicativeScadute);
					}catch(Exception e){
						// ignore
					}
				}
	
	
				// -- Scadute rispetto ora registrazione
				if(this.filtraCorrelazioniApplicativeScaduteRispettoOraRegistrazione){
					String causaCorrelazioniApplicativeScaduteRispettoOraRegistrazione = "Eliminazione correlazioni applicative scadute rispetto ora registrazione";
					List<Long> correlazioniScaduteRispettoOraRegistrazione = null; 
					try{
						GestoreMessaggi.acquireLock(
								this.semaphore, connectionDB, this.timerLockCorrelazioneApplicativa,
								this.msgDiag, causaCorrelazioniApplicativeScaduteRispettoOraRegistrazione, 
								this.propertiesReader.getGestioneSerializableDB_AttesaAttiva(), 
								this.propertiesReader.getGestioneSerializableDB_CheckInterval());
	
						correlazioniScaduteRispettoOraRegistrazione = gestoreCorrelazioneApplicativa.getCorrelazioniScaduteRispettoOraRegistrazione(this.limit, this.scadenzaCorrelazioneApplicativa, 
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
	
							int gestiti = 0;
							while(correlazioniScaduteRispettoOraRegistrazione.size()>0){
								Long idCorrelazioneScaduta = correlazioniScaduteRispettoOraRegistrazione.remove(0);
	
								String idCorrelato = null;
								String idApplicativo = null;
	
								try{
									String [] id = gestoreCorrelazioneApplicativa.getIDMappingCorrelazioneApplicativa(idCorrelazioneScaduta);
									idCorrelato = id[0];
									idApplicativo = id[1];
									try{
										GestoreMessaggi.updateLock(
												this.semaphore, connectionDB, this.timerLockCorrelazioneApplicativa,
												this.msgDiag, "Eliminazione correlazione applicativa scaduta rispetto ora registrazione (id_applicativo:"+idApplicativo+",id_busta:"+idCorrelato+") ...");
									}catch(Throwable e){
										this.msgDiag.logErroreGenerico(e,"EliminazioneCorrelazioniRispettoOraRegistrazione(id_applicativo:"+idApplicativo+",id_busta:"+idCorrelato+")-UpdateLock");
										this.logTimer.error("ErroreEliminazioneCorrelazioniRispettoOraRegistrazione(id_applicativo:"+idApplicativo+",id_busta:"+idCorrelato+")-UpdateLock: "+e.getMessage(),e);
										break;
									}
	
									this.msgDiag.addKeyword(CostantiPdD.KEY_ID_MESSAGGIO_DA_ELIMINARE,idCorrelato);
									this.msgDiag.addKeyword(CostantiPdD.KEY_ID_CORRELAZIONE_APPLICATIVA,idApplicativo);
	
									gestoreCorrelazioneApplicativa.deleteCorrelazioneApplicativa(idCorrelazioneScaduta);
									this.msgDiag.logPersonalizzato("eliminazioneCorrelazioneApplicativaScaduta");
									if(this.logQuery)
										this.logTimer.debug(this.msgDiag.getMessaggio_replaceKeywords("eliminazioneCorrelazioneApplicativaScaduta"));
	
									gestiti++;
									
								}catch(Exception e){
									this.msgDiag.logErroreGenerico(e,"EliminazioneCorrelazioniRispettoOraRegistrazione(id_applicativo:"+idApplicativo+",id_busta:"+idCorrelato+")");
									this.logTimer.error("ErroreEliminazioneCorrelazioniRispettoOraRegistrazione(id_applicativo:"+idApplicativo+",id_busta:"+idCorrelato+"): "+e.getMessage(),e);
								}
							}
	
							if(this.logQuery)
								this.logTimer.info("Eliminate "+gestiti+" correlazioni applicative (scadute rispetto ora registrazione)");
	
							boolean cerca = true;
							try{
								GestoreMessaggi.updateLock(
										this.semaphore, connectionDB, this.timerLockCorrelazioneApplicativa,
										this.msgDiag, "Ricerca nuove correlazioni scadute rispetto ora registrazione ...");													
							}catch(Throwable e){
								this.msgDiag.logErroreGenerico(e,"RicercaNuoveCorrelazioni-UpdateLock");
								this.logTimer.error("RicercaNuoveCorrelazioni-UpdateLock: "+e.getMessage(),e);
								cerca = false;
							}
							
							if(cerca) {
								// Check altre correlazioni da eliminare
								correlazioniScaduteRispettoOraRegistrazione = 
										gestoreCorrelazioneApplicativa.getCorrelazioniScaduteRispettoOraRegistrazione(this.limit, this.scadenzaCorrelazioneApplicativa, 
												this.logQuery,this.orderByQuery,this.filtraCorrelazioniApplicativeScaduteRispettoOraRegistrazione_escludiCorrelazioniConScadenzaImpostata);
							}
							else {
								correlazioniScaduteRispettoOraRegistrazione = new java.util.ArrayList<Long>(); // per uscire dal while
							}
						}
					}finally{
						try{
							GestoreMessaggi.releaseLock(
									this.semaphore, connectionDB, this.timerLockCorrelazioneApplicativa,
									this.msgDiag, causaCorrelazioniApplicativeScaduteRispettoOraRegistrazione);
						}catch(Exception e){
							// ignore
						}
					}
				}
			}
			else {
				if(this.logQuery) {
					this.logTimer.info("Gestione correlazione applicativa disabilitata");
				}	
			}


			// end
			long endControlloRepositoryMessaggi = DateManager.getTimeMillis();
			long diff = (endControlloRepositoryMessaggi-startControlloRepositoryMessaggi);
			this.logTimer.info("Controllo Repository Messaggi terminato in "+Utilities.convertSystemTimeIntoString_millisecondi(diff, true));

		} 
		catch(TimerLockNotAvailableException t) {
			// msg diagnostico emesso durante l'emissione dell'eccezione
			this.logTimer.info(t.getMessage(),t);
		}
		catch (Exception e) {
			this.msgDiag.logErroreGenerico(e,"GestioneMessaggi");
			this.logTimer.error("Riscontrato errore durante la gestione del repository dei messaggi: "+ e.getMessage(),e);
		}finally{
			if(openspcoopstate!=null)
				openspcoopstate.releaseResource();
		}


		try {
			if(TimerState.ENABLED.equals(STATE_VERIFICA_CONNESSIONI_ATTIVE)) {

				startControlloRepositoryMessaggi = DateManager.getTimeMillis();
				this.logTimer.info("Verifico connessioni attive...");

				// Connettori: porta delegata
				List<String> identificatoriConnettoriPD = RepositoryConnettori.getIdentificatoriConnettori_pd();
				if(this.logQuery){
					if(identificatoriConnettoriPD==null || identificatoriConnettoriPD.isEmpty()){
						this.logTimer.info("Non sono state trovate connessioni attive sulle PorteDelegate");
					}
				}
				if(identificatoriConnettoriPD!=null){

					boolean verificaEffettuata = false;
					if(this.logQuery){
						if(!identificatoriConnettoriPD.isEmpty()){
							this.logTimer.info("Sono state trovate connessioni attive sulle PorteDelegate, verifica in corso ...");
						}
					}

					for (String identificatoreConnessione : identificatoriConnettoriPD) {
						verificaEffettuata = true;
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
							this.logTimer.debug("SCADENZA ["+scadenza.toString()+"]   CONNETTORE ["+((connettore!=null && connettore.getCreationDate()!=null) ? connettore.getCreationDate().toString() : null)+"]");
							if(connettore!=null && connettore.getCreationDate()!=null && connettore.getCreationDate().before(scadenza)){
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
								if(connettore==null) {
									this.logTimer.debug("NON PIU' PRESENTE");
								}
								else if(connettore.getCreationDate()==null) {
									this.logTimer.debug("SENZA DATA DI CREAZIONE");
								}
								else {
									this.logTimer.debug("NON SCADUTO");
								}
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
				List<String> identificatoriConnettoriPA = RepositoryConnettori.getIdentificatoriConnettori_pa();
				if(this.logQuery){
					if(identificatoriConnettoriPA==null || identificatoriConnettoriPA.isEmpty()==false){
						this.logTimer.info("Non sono state trovate connessioni attive sulle PorteApplicative");
					}
				}
				if(identificatoriConnettoriPA!=null){

					boolean verificaEffettuata = false;
					if(this.logQuery){
						if(!identificatoriConnettoriPD.isEmpty()){
							this.logTimer.info("Sono state trovate connessioni attive sulle PorteApplicative, verifica in corso ...");
						}
					}

					for (String identificatoreConnessione : identificatoriConnettoriPA) {
						verificaEffettuata = true;
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
							this.logTimer.debug("SCADENZA ["+scadenza.toString()+"]   CONNETTORE ["+((connettore!=null && connettore.getCreationDate()!=null) ? connettore.getCreationDate().toString() : null)+"]");
							if(connettore!=null && connettore.getCreationDate()!=null && connettore.getCreationDate().before(scadenza)){
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
								if(connettore==null) {
									this.logTimer.debug("NON PIU' PRESENTE");
								}
								else if(connettore.getCreationDate()==null) {
									this.logTimer.debug("SENZA DATA DI CREAZIONE");
								}
								else {
									this.logTimer.debug("NON SCADUTO");
								}
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

				// end
				long endControlloRepositoryMessaggi = DateManager.getTimeMillis();
				long diff = (endControlloRepositoryMessaggi-startControlloRepositoryMessaggi);
				this.logTimer.info("Terminata verifica connessioni attive in "+Utilities.convertSystemTimeIntoString_millisecondi(diff, true));
			}
			else{
				this.logTimer.warn("Non verifico connessioni attive");
			}

		} 
		catch (Exception e) {
			this.msgDiag.logErroreGenerico(e,"GestioneMessaggi");
			this.logTimer.error("Riscontrato errore durante la gestione del repository dei messaggi: "+ e.getMessage(),e);
		}
	}

}
