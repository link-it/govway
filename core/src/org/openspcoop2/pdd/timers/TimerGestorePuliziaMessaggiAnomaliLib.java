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
import org.openspcoop2.protocol.engine.driver.RollbackRepositoryBuste;
import org.openspcoop2.protocol.sdk.state.StateMessage;
import org.openspcoop2.utils.date.DateManager;

/**
 * Implementazione dell'interfaccia {@link TimerGestorePuliziaMessaggiAnomali} del Gestore
 * dei threads di servizio di OpenSPCoop.
 * 
 *  
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class TimerGestorePuliziaMessaggiAnomaliLib{

	private MsgDiagnostico msgDiag = null;
	private Logger logTimer = null;
	private OpenSPCoop2Properties propertiesReader = null;
	private boolean logQuery = false;
	private int limit = CostantiPdD.LIMIT_MESSAGGI_GESTORI;
	private boolean orderByQuery;
	
	public TimerGestorePuliziaMessaggiAnomaliLib(MsgDiagnostico msgDiag,Logger log,OpenSPCoop2Properties p,boolean logQuery,
			int limit,boolean orderByQuery){
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
			this.logTimer.error("["+TimerGestorePuliziaMessaggiAnomali.ID_MODULO+"] Rilevato sistema in shutdown");
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
			this.logTimer.error("["+TimerGestorePuliziaMessaggiAnomali.ID_MODULO+"] Risorse di sistema non disponibili: "+TimerMonitoraggioRisorse.risorsaNonDisponibile.getMessage(),TimerMonitoraggioRisorse.risorsaNonDisponibile);
			return;
		}
		if( MsgDiagnostico.gestoreDiagnosticaDisponibile == false){
			this.logTimer.error("["+TimerGestorePuliziaMessaggiAnomali.ID_MODULO+"] Sistema di diagnostica non disponibile: "+MsgDiagnostico.motivoMalfunzionamentoDiagnostici.getMessage(),MsgDiagnostico.motivoMalfunzionamentoDiagnostici);
			return;
		}
		
		
		this.msgDiag.logPersonalizzato("controlloInCorso");
		this.logTimer.info(this.msgDiag.getMessaggio_replaceKeywords("controlloInCorso"));
		long startControlloRepositoryMessaggi = DateManager.getTimeMillis();
		

		RollbackRepositoryBuste rollbackMessaggio = null;
		
		OpenSPCoopStateful openspcoopstate = new OpenSPCoopStateful();
		
		try {

			openspcoopstate.initResource(this.propertiesReader.getIdentitaPortaDefault(null), TimerGestorePuliziaMessaggiAnomali.ID_MODULO, null);
		
			
			// Messaggi da eliminare 
			GestoreMessaggi gestoreMsgSearch = new GestoreMessaggi(openspcoopstate, true,this.logTimer, this.msgDiag,null);
			Vector<String> busteInutiliINBOX = gestoreMsgSearch.readBusteNonRiferiteDaMessaggiFromInBox(this.limit,this.logQuery,
					this.propertiesReader.isForceIndex(),this.orderByQuery);
			Vector<String> busteInutiliOUTBOX = gestoreMsgSearch.readBusteNonRiferiteDaMessaggiFromOutBox(this.limit,this.logQuery,
					this.propertiesReader.isForceIndex(),this.orderByQuery);
			
			if(this.logQuery){
				if( ! (busteInutiliINBOX.size()>0 ||   busteInutiliOUTBOX.size() > 0)){
					this.logTimer.info("Non sono stati trovati messaggi anomali");
				}
			}

			
			while( busteInutiliINBOX.size()>0 ||
				  busteInutiliOUTBOX.size() > 0){
			
				this.msgDiag.addKeyword(CostantiPdD.KEY_TIMER_GESTORE_MESSAGGI_INCONSISTENTI_NUM_MSG_INBOX, busteInutiliINBOX.size()+"");
				this.msgDiag.addKeyword(CostantiPdD.KEY_TIMER_GESTORE_MESSAGGI_INCONSISTENTI_NUM_MSG_OUTBOX, busteInutiliOUTBOX.size()+"");
				
				this.msgDiag.logPersonalizzato("ricercaMessaggiDaEliminare");
						
				
				// Buste non piu' riferite da messaggi
				// Eliminazione Messaggi from INBOX
				if(this.logQuery)
					this.logTimer.info("Trovate "+busteInutiliINBOX.size()+" buste non piu' riferita da messaggi da eliminare (INBOX) ...");
				this.msgDiag.addKeyword(CostantiPdD.KEY_TIPO_MESSAGGIO,Costanti.INBOX);
				for(int i=0; i<busteInutiliINBOX.size(); i++){
					
					String idMsgDaEliminare = busteInutiliINBOX.get(i);
					this.msgDiag.addKeyword(CostantiPdD.KEY_ID_MESSAGGIO_DA_ELIMINARE,idMsgDaEliminare);
					
					try{
						//	rollback messaggio scaduto (eventuale profilo + accesso_pdd)
						rollbackMessaggio = new RollbackRepositoryBuste(idMsgDaEliminare,openspcoopstate.getStatoRichiesta(),true);
						rollbackMessaggio.rollbackBustaIntoInBox(false); //l'history viene eliminato con la scadenza della busta
						((StateMessage)openspcoopstate.getStatoRichiesta()).executePreparedStatement();
						
						this.msgDiag.logPersonalizzato("eliminazioneMessaggio");
						if(this.logQuery){
							this.logTimer.debug(this.msgDiag.getMessaggio_replaceKeywords("eliminazioneMessaggio"));
						}
						
					}catch(Exception e){
						if(rollbackMessaggio!=null){
							((StateMessage)openspcoopstate.getStatoRichiesta()).closePreparedStatement();
							rollbackMessaggio = null;
						}
						this.msgDiag.logErroreGenerico(e,"EliminazioneBustaInbox("+idMsgDaEliminare+")");
						this.logTimer.error("ErroreEliminazioneBustaInbox("+idMsgDaEliminare+"): "+e.getMessage(),e);
					}
				}
				if(this.logQuery)
					this.logTimer.info("Eliminate "+busteInutiliINBOX.size()+" buste non piu' riferita da messaggi (INBOX)");
				
				
				// Buste non piu' riferite da messaggi
				// Eliminazione Messaggi from OUTBOX
				if(this.logQuery)
					this.logTimer.info("Trovate "+busteInutiliOUTBOX.size()+" buste non piu' riferita da messaggi da eliminare (OUTBOX) ...");
				this.msgDiag.addKeyword(CostantiPdD.KEY_TIPO_MESSAGGIO,Costanti.OUTBOX);
				for(int i=0; i<busteInutiliOUTBOX.size(); i++){
					
					String idMsgDaEliminare = busteInutiliOUTBOX.get(i);
					this.msgDiag.addKeyword(CostantiPdD.KEY_ID_MESSAGGIO_DA_ELIMINARE,idMsgDaEliminare);
					
					try{
						//	rollback messaggio scaduto (eventuale profilo + accesso_pdd)
						rollbackMessaggio = new RollbackRepositoryBuste(idMsgDaEliminare,openspcoopstate.getStatoRichiesta(),true);
						rollbackMessaggio.rollbackBustaIntoOutBox(false); //l'history viene eliminato con la scadenza della busta
						((StateMessage)openspcoopstate.getStatoRichiesta()).executePreparedStatement();
						
						this.msgDiag.logPersonalizzato("eliminazioneMessaggio");
						if(this.logQuery){
							this.logTimer.debug(this.msgDiag.getMessaggio_replaceKeywords("eliminazioneMessaggio"));
						}
						
					}catch(Exception e){
						if(rollbackMessaggio!=null){
							((StateMessage)openspcoopstate.getStatoRichiesta()).closePreparedStatement();
							rollbackMessaggio = null;
						}
						this.msgDiag.logErroreGenerico(e,"EliminazioneBustaOutbox("+idMsgDaEliminare+")");
						this.logTimer.error("EliminazioneBustaOutbox("+idMsgDaEliminare+"): "+e.getMessage(),e);
					}
				}
				if(this.logQuery)
					this.logTimer.info("Eliminate "+busteInutiliOUTBOX.size()+" buste non piu' riferita da messaggi (OUTBOX)");
				
				
				// Check altri messaggi da eliminare
				busteInutiliINBOX = gestoreMsgSearch.readBusteNonRiferiteDaMessaggiFromInBox(this.limit,this.logQuery,
						this.propertiesReader.isForceIndex(),this.orderByQuery);
				busteInutiliOUTBOX = gestoreMsgSearch.readBusteNonRiferiteDaMessaggiFromOutBox(this.limit,this.logQuery,
						this.propertiesReader.isForceIndex(),this.orderByQuery);
				
			}
			
			
			// end
			long endControlloRepositoryMessaggi = DateManager.getTimeMillis();
			this.logTimer.info("Pulizia Messaggi Anomali terminata in "+((endControlloRepositoryMessaggi-startControlloRepositoryMessaggi)/1000) +" secondi");
			
		} catch (Exception e) {
			this.msgDiag.logErroreGenerico(e,"GestioneMessaggiInconsistenti");
			this.logTimer.error("Riscontrato errore durante la pulizia dei messaggi anomali: "+e.getMessage(),e);
		}finally{
			if(openspcoopstate!=null)
				openspcoopstate.releaseResource();
		}
	}

}
