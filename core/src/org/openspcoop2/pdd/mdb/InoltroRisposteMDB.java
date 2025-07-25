/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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



package org.openspcoop2.pdd.mdb;

import jakarta.ejb.EJBException;
import jakarta.ejb.MessageDrivenBean;
import jakarta.ejb.MessageDrivenContext;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import jakarta.jms.ObjectMessage;

import org.slf4j.Logger;
import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.node.TransactionManager;
import org.openspcoop2.pdd.core.state.IOpenSPCoopState;
import org.openspcoop2.pdd.core.state.OpenSPCoopStateful;
import org.openspcoop2.pdd.core.state.OpenSPCoopStateless;
import org.openspcoop2.pdd.logger.Dump;
import org.openspcoop2.pdd.logger.MsgDiagnosticiProperties;
import org.openspcoop2.pdd.logger.MsgDiagnostico;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.pdd.logger.Tracciamento;
import org.openspcoop2.pdd.services.OpenSPCoop2Startup;
import org.openspcoop2.pdd.timers.TimerMonitoraggioRisorseThread;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.utils.LoggerWrapperFactory;


/**
 * Contiene la definizione di un Message Driven Bean 'InoltroRisposte', il quale e' un
 * modulo dell'infrastruttura OpenSPCoop.
 * <p>
 * Inoltre la classe contiene due MessageFactory per la costruzione di un oggetto {@link InoltroRisposteMessage}
 * utilizzato per la spedizione di messaggi JMS al mdb, tramita una coda JMS utilizzata in ricezione.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author Tronci Fabio (tronci@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class InoltroRisposteMDB implements MessageDrivenBean, MessageListener {

	 /**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;



	/* ********  F I E L D S  P R I V A T I   ******** */

	/** Message Driven Context */
	private transient MessageDrivenContext ctxMDB;


	
	/* ********  M E T O D I   ******** */

	/**
	 * Imposta il Contesto del Message Driven Bean.
	 * Metodo necessario per l'implementazione dell'interfaccia <code>MessageDrivenBean</code>.
	 *
	 * @param mdc Contesto del Message Driven Bean.
	 * 
	 */
	@Override
	public void setMessageDrivenContext(MessageDrivenContext mdc) throws EJBException { 
		this.ctxMDB = mdc;
	}



	/**
	 * Metodo necessario per l'implementazione dell'interfaccia <code>MessageDrivenBean</code>.
	 *
	 * 
	 */
	public void ejbCreate() {
		// nop
	}



	/**
	 * Metodo necessario per l'implementazione dell'interfaccia <code>MessageDrivenBean</code>.
	 * 
	 */
	@Override
	public void ejbRemove() {
		// nop
	}



	/**
	 * Attivato,  quando viene ricevuto sulla coda associata al mdb (coda InoltroRisposte)
	 * un messaggio JMS. Questo metodo implementa la logica del modulo InoltroRisposte
	 * dell'infrastruttura OpenSPCoop.
	 * 
	 */
	@Override
	public void onMessage(Message message) {
		if(message instanceof ObjectMessage) {

			/* ------------ Controllo inizializzazione OpenSPCoop -------------------- */
			if( !OpenSPCoop2Startup.initialize){
				this.ctxMDB.setRollbackOnly();
				return;
			}

			/* logger */
			Logger log = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();
			if (log == null) {
				LoggerWrapperFactory.getLogger(InoltroRisposteMDB.class).error("["+InoltroRisposte.ID_MODULO+"]"+" Logger nullo. MDB abortito");
				return;
			}
			
			/* ----------- Controllo risorse disponibili --------------- */
			if( !TimerMonitoraggioRisorseThread.isRisorseDisponibili()){
				log.error("["+InoltroRisposte.ID_MODULO+"] Risorse di sistema non disponibili: "+TimerMonitoraggioRisorseThread.getRisorsaNonDisponibile().getMessage(),TimerMonitoraggioRisorseThread.getRisorsaNonDisponibile());
				this.ctxMDB.setRollbackOnly();
				return;
			}
			if( !Tracciamento.tracciamentoDisponibile){
				log.error("["+InoltroRisposte.ID_MODULO+"] Tracciatura non disponibile: "+Tracciamento.motivoMalfunzionamentoTracciamento.getMessage(),Tracciamento.motivoMalfunzionamentoTracciamento);
				this.ctxMDB.setRollbackOnly();
				return;
			}
			if( !MsgDiagnostico.gestoreDiagnosticaDisponibile){
				log.error("["+InoltroRisposte.ID_MODULO+"] Sistema di diagnostica non disponibile: "+MsgDiagnostico.motivoMalfunzionamentoDiagnostici.getMessage(),MsgDiagnostico.motivoMalfunzionamentoDiagnostici);
				this.ctxMDB.setRollbackOnly();
				return;
			}
			if( !Dump.isSistemaDumpDisponibile()){
				log.error("["+InoltroRisposte.ID_MODULO+"] Sistema di dump dei contenuti applicativi non disponibile: "+Dump.getMotivoMalfunzionamentoDump().getMessage(),Dump.getMotivoMalfunzionamentoDump());
				this.ctxMDB.setRollbackOnly();
				return;
			}


			InoltroRisposte lib = null;
			try{
				lib = new InoltroRisposte(log);
			}catch(Exception e){
				log.error("InoltroRisposte.instanziazione: "+e.getMessage(),e);
				this.ctxMDB.setRollbackOnly();
				return;
			}
			
			
			/* ----------- Controllo inizializzazione lib ----------- */
			if( !lib.inizializzazioneUltimata ){
				log = LoggerWrapperFactory.getLogger(InoltroRisposteMDB.class);
				log.error("["+InoltroRisposte.ID_MODULO+"] Inizializzazione MDB non riuscita");
				this.ctxMDB.setRollbackOnly();
				return;
			}
			
			/* ------------  Lettura parametri della richiesta  ------------- */

			// Logger dei messaggi diagnostici
			MsgDiagnostico msgDiag = MsgDiagnostico.newInstance(TipoPdD.APPLICATIVA, InoltroRisposte.ID_MODULO);

			//	Ricezione Messaggio
			msgDiag.mediumDebug("Ricezione richiesta (InoltroRisposteMessage)...");
			ObjectMessage received = (ObjectMessage)message;
			InoltroRisposteMessage inoltroRisposteMsg = null;
			try{
				inoltroRisposteMsg = (InoltroRisposteMessage)received.getObject();
			}	catch(jakarta.jms.JMSException e){ 
				msgDiag.logErroreGenerico(e,"received.getObject(InoltroRisposteMessage)");
				return; 
			}
			
			// ID associato alla richiesta
			String idMessageRequest = null; //(serve anche per una validazione sincrona)
			try{
				idMessageRequest = received.getStringProperty("ID");
			}	catch(jakarta.jms.JMSException e){ 
				msgDiag.logErroreGenerico(e,"received.getStringProperty(ID)");
				return;
			}
			
			// Stato
			IOpenSPCoopState stato = null;
			try {
				
				if ( inoltroRisposteMsg.getOpenspcoopstate() != null ){
					stato = inoltroRisposteMsg.getOpenspcoopstate();
					((OpenSPCoopStateless)stato).setUseConnection(true);
					((OpenSPCoopStateless)stato).setMessageLib(inoltroRisposteMsg);
					((OpenSPCoopStateless)stato).setIDMessaggioSessione(idMessageRequest);
				}
				
				else {
					stato = new OpenSPCoopStateful();
					((OpenSPCoopStateful)stato).setIDMessaggioSessione(idMessageRequest); 
					((OpenSPCoopStateful)stato).setMessageLib(inoltroRisposteMsg);
				}
				
			}catch (Exception e) {
				log.error("["+InoltroRisposte.ID_MODULO+"] Invocazione della libreria non riuscita (verrà effettuato un rollback sulla coda JMS)",e);
				this.ctxMDB.setRollbackOnly();
				return;
			}
			
			// update diagnostico
			msgDiag.updateState(stato.getStatoRichiesta(),stato.getStatoRisposta());
						

			/* PddContext */
			PdDContext pddContext = inoltroRisposteMsg.getPddContext();
			try{
				msgDiag.setPddContext(pddContext, ProtocolFactoryManager.getInstance().getProtocolFactoryByName((String) pddContext.getObject(org.openspcoop2.core.constants.Costanti.PROTOCOL_NAME)));
			}catch(ProtocolException e){
				msgDiag.logErroreGenerico(e,"ProtocolFactory.instanziazione");
				this.ctxMDB.setRollbackOnly();
				return;
			}
			

			
			Busta busta = inoltroRisposteMsg.getBustaRisposta();
			
			/* ------------------ Validity Check  --------------- */
			msgDiag.mediumDebug("Transaction Manager...");
			try{
				if(!TransactionManager.validityCheck(msgDiag,InoltroRisposte.ID_MODULO,busta.getID(),
						Costanti.OUTBOX,received.getJMSMessageID(),pddContext)){
					msgDiag.addKeyword(CostantiPdD.KEY_ID_MESSAGGIO_TRANSACTION_MANAGER, idMessageRequest);
					msgDiag.addKeyword(CostantiPdD.KEY_PROPRIETARIO_MESSAGGIO, InoltroRisposte.ID_MODULO);
					msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_ALL,"transactionManager.validityCheckError");
					return;
				}
			}catch(Exception e){
				msgDiag.logErroreGenerico(e,"TransactionManager.validityCheck");
				this.ctxMDB.setRollbackOnly();
				return;
			}
			
			
			EsitoLib esito = null;
			try {
						
				esito = lib.onMessage(stato);
				
			}catch (Exception e) {
				log.error("["+InoltroRisposte.ID_MODULO+"] Invocazione della libreria non riuscita (verrà effettuato un rollback sulla coda JMS)",e);
				this.ctxMDB.setRollbackOnly();
				return;
			}
			
			if (!esito.isEsitoInvocazione()){
				log.info("["+InoltroRisposte.ID_MODULO+"] Invocazione della libreria terminata con esito negativo, verrà effettuato un rollback sulla coda JMS");
				this.ctxMDB.setRollbackOnly();
			}	
			
			else {
				log.debug("["+InoltroRisposte.ID_MODULO+"] Invocazione della libreria terminata correttamente");
			}
			
		}
	}
}
			
		
