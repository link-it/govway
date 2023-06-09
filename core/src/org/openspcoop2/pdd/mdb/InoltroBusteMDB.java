/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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

import javax.ejb.EJBException;
import javax.ejb.MessageDrivenBean;
import javax.ejb.MessageDrivenContext;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

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
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.state.RequestInfo;
import org.openspcoop2.utils.LoggerWrapperFactory;





/**
 * Contiene la definizione di un Message Driven Bean 'InoltroBuste', il quale e' un
 * modulo dell'infrastruttura OpenSPCoop.
 * <p>
 * Inoltre la classe contiene due MessageFactory per la costruzione di un oggetto {@link InoltroBusteMessage}
 * utilizzato per la spedizione di messaggi JMS al mdb, tramita una coda JMS utilizzata in ricezione.
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author Tronci Fabio (tronci@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class InoltroBusteMDB implements MessageDrivenBean, MessageListener {

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
	 * Attivato,  quando viene ricevuto sulla coda associata al mdb (coda InoltroBuste)
	 * un messaggio JMS. Questo metodo implementa la logica del modulo InoltroBuste
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

			Logger log = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();
			
			/* ----------- Controllo risorse disponibili --------------- */
			if( !TimerMonitoraggioRisorseThread.risorseDisponibili){
				if(log!=null) {
					log.error("["+InoltroBuste.ID_MODULO+"] Risorse di sistema non disponibili: "+TimerMonitoraggioRisorseThread.risorsaNonDisponibile.getMessage(),TimerMonitoraggioRisorseThread.risorsaNonDisponibile);
				}
				this.ctxMDB.setRollbackOnly();
				return;
			}
			if( !Tracciamento.tracciamentoDisponibile){
				if(log!=null) {
					log.error("["+InoltroBuste.ID_MODULO+"] Tracciatura non disponibile: "+Tracciamento.motivoMalfunzionamentoTracciamento.getMessage(),Tracciamento.motivoMalfunzionamentoTracciamento);
				}
				this.ctxMDB.setRollbackOnly();
				return;
			}
			if( !MsgDiagnostico.gestoreDiagnosticaDisponibile){
				if(log!=null) {
					log.error("["+InoltroBuste.ID_MODULO+"] Sistema di diagnostica non disponibile: "+MsgDiagnostico.motivoMalfunzionamentoDiagnostici.getMessage(),MsgDiagnostico.motivoMalfunzionamentoDiagnostici);
				}
				this.ctxMDB.setRollbackOnly();
				return;
			}
			if( !Dump.isSistemaDumpDisponibile()){
				log.error("["+InoltroBuste.ID_MODULO+"] Sistema di dump dei contenuti applicativi non disponibile: "+Dump.getMotivoMalfunzionamentoDump().getMessage(),Dump.getMotivoMalfunzionamentoDump());
				this.ctxMDB.setRollbackOnly();
				return;
			}
			
			
			/* Logger */
			log = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();
			if (log == null) {
				LoggerWrapperFactory.getLogger(InoltroBusteMDB.class).error("["+ImbustamentoRisposte.ID_MODULO+"]"+" Logger nullo.");
			}
			

			
			/* Creazione oggetto libreria */
			
			InoltroBuste lib = null;
			try{
				lib = new InoltroBuste(log);
			}catch(Exception e){
				if(log!=null) {
					log.error("InoltroBuste.instanziazione: "+e.getMessage(),e);
				}
				this.ctxMDB.setRollbackOnly();
				return;
			}
			
			
			
			/* ----------- Controllo inizializzazione libreria ----------- */
			if( !lib.getInizializzazioneUltimata() ){
				log = LoggerWrapperFactory.getLogger(InoltroBusteMDB.class);
				log.error("["+InoltroBuste.ID_MODULO+"] Inizializzazione non riuscita");
				this.ctxMDB.setRollbackOnly();
				return;
			}
			





			/* ------------  Lettura parametri della richiesta  ------------- */

			// Logger dei messaggi diagnostici
			MsgDiagnostico msgDiag = MsgDiagnostico.newInstance(TipoPdD.DELEGATA, InoltroBuste.ID_MODULO);

			//	Ricezione Messaggio
			msgDiag.mediumDebug("Ricezione richiesta (InoltroBusteMessage)...");
			ObjectMessage received = (ObjectMessage)message;
			InoltroBusteMessage inoltroBusteMsg = null;
			try{
				inoltroBusteMsg = (InoltroBusteMessage) received.getObject();
			}	catch(javax.jms.JMSException e){ 
				msgDiag.logErroreGenerico(e,"received.getObject(InoltroBusteMessage)");
				return; 
			}	
			if(inoltroBusteMsg.getRichiestaDelegata()!=null && inoltroBusteMsg.getRichiestaDelegata().getIdPortaDelegata()!=null) {
				
				RequestInfo requestInfo = null;
				if(inoltroBusteMsg.getPddContext()!=null && inoltroBusteMsg.getPddContext().containsKey(org.openspcoop2.core.constants.Costanti.REQUEST_INFO)) {
					requestInfo = (RequestInfo) inoltroBusteMsg.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.REQUEST_INFO);
				}
				
				msgDiag.updatePorta(inoltroBusteMsg.getRichiestaDelegata().getIdPortaDelegata().getNome(), requestInfo);
			}
			
			// ID associato alla richiesta 
			String idMessageRequest = null;
			try{
				idMessageRequest = received.getStringProperty("ID");
			}	catch(javax.jms.JMSException e){ 
				msgDiag.logErroreGenerico(e,"received.getStringProperty(ID)");
				return;
			}
					
			// Stato
			IOpenSPCoopState stato = null;
			try {
				
				if ( inoltroBusteMsg.getOpenspcoopstate() != null ){
					stato = inoltroBusteMsg.getOpenspcoopstate();
					((OpenSPCoopStateless)stato).setUseConnection(true);
					((OpenSPCoopStateless)stato).setMessageLib(inoltroBusteMsg);
					((OpenSPCoopStateless)stato).setIDMessaggioSessione(idMessageRequest);
				}
				
				else {
					stato = new OpenSPCoopStateful();
					((OpenSPCoopStateful)stato).setIDMessaggioSessione(idMessageRequest); 
					((OpenSPCoopStateful)stato).setMessageLib(inoltroBusteMsg);
				}
				
			}catch (Exception e) {
				if(log!=null) {
					log.error("["+InoltroBuste.ID_MODULO+"] Invocazione della libreria non riuscita (verrà effettuato un rollback sulla coda JMS)",e);
				}
				this.ctxMDB.setRollbackOnly();
				return;
			}
			
			// update diagnostico
			msgDiag.updateState(stato.getStatoRichiesta(),stato.getStatoRisposta());
			
			
			/* PddContext */
			PdDContext pddContext = inoltroBusteMsg.getPddContext();
			try{
				msgDiag.setPddContext(pddContext, ProtocolFactoryManager.getInstance().getProtocolFactoryByName((String) pddContext.getObject(org.openspcoop2.core.constants.Costanti.PROTOCOL_NAME)));
			}catch(ProtocolException e){
				msgDiag.logErroreGenerico(e,"ProtocolFactory.instanziazione");
				this.ctxMDB.setRollbackOnly();
				return;
			}

			/* ------------------ Validity Check  --------------- */
			msgDiag.mediumDebug("Transaction Manager...");
			try{
				if(!TransactionManager.validityCheck(msgDiag,InoltroBuste.ID_MODULO,idMessageRequest,
						Costanti.OUTBOX,received.getJMSMessageID(),pddContext)){
					msgDiag.addKeyword(CostantiPdD.KEY_ID_MESSAGGIO_TRANSACTION_MANAGER, idMessageRequest);
					msgDiag.addKeyword(CostantiPdD.KEY_PROPRIETARIO_MESSAGGIO, InoltroBuste.ID_MODULO);
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
				if(log!=null) {
					log.error("["+InoltroBuste.ID_MODULO+"] Invocazione della libreria non riuscita (verrà effettuato un rollback sulla coda JMS)",e);
				}
				this.ctxMDB.setRollbackOnly();
				return;
			}

	
			if (!esito.isEsitoInvocazione()){
				if(log!=null) {
					log.info("["+InoltroBuste.ID_MODULO+"] Invocazione della libreria terminata con esito negativo, verrà effettuato un rollback sulla coda JMS");
				}
				this.ctxMDB.setRollbackOnly();
			}	
			
			else {
				if(log!=null) {
					log.debug("["+InoltroBuste.ID_MODULO+"] Invocazione della libreria terminata correttamente");
				}
			}
		}
	}
}

