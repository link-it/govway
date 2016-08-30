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



package org.openspcoop2.pdd.mdb;

import javax.ejb.EJBException;
import javax.ejb.MessageDrivenBean;
import javax.ejb.MessageDrivenContext;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.slf4j.Logger;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.node.TransactionManager;
import org.openspcoop2.pdd.core.state.OpenSPCoopStateful;
import org.openspcoop2.pdd.logger.Dump;
import org.openspcoop2.pdd.logger.MsgDiagnosticiProperties;
import org.openspcoop2.pdd.logger.MsgDiagnostico;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.pdd.logger.Tracciamento;
import org.openspcoop2.pdd.services.OpenSPCoop2Startup;
import org.openspcoop2.pdd.timers.TimerMonitoraggioRisorse;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.utils.LoggerWrapperFactory;




/**
 * Contiene la definizione di un Message Driven Bean 'Sbustamento', il quale e' un
 * modulo dell'infrastruttura OpenSPCoop.
 * <p>
 * Inoltre la classe contiene due MessageFactory per la costruzione di un oggetto {@link SbustamentoMessage}
 * utilizzato per la spedizione di messaggi JMS al mdb, tramita una coda JMS utilizzata in ricezione.
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author Tronci Fabio (tronci@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class SbustamentoMDB implements MessageDrivenBean, MessageListener {

	 /**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	
	/* ********  F I E L D S  P R I V A T I   ******** */

	/** Message Driven Context */
	private MessageDrivenContext ctxMDB;

	/** Logger utilizzato per debug. */
	private Logger log = null;
	



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
	 * Remove EJBean
	 *
	 * 
	 */
	public void ejbCreate() {}




	/**
	 * Metodo necessario per l'implementazione dell'interfaccia <code>MessageDrivenBean</code>.
	 * 
	 */
	@Override
	public void ejbRemove() {}






	/**
	 * Attivato,  quando viene ricevuto sulla coda associata al mdb (coda RequestOUT_S)
	 * un messaggio JMS. Questo metodo implementa la logica del modulo IdentificazionePortaDelegata
	 * dell'infrastruttura OpenSPCoop.
	 * 
	 */
	@Override
	public void onMessage(Message message) {

		if(message instanceof ObjectMessage) {

			/* ------------ Controllo inizializzazione OpenSPCoop -------------------- */
			if( OpenSPCoop2Startup.initialize == false){
				this.ctxMDB.setRollbackOnly();
				return;
			}
			
			/* logger */
			this.log = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();
			if (this.log == null) {
				System.out.println("["+Sbustamento.ID_MODULO+"]"+" Logger nullo. MDB abortito");
				return;
			}
			
			/* ----------- Controllo risorse disponibili --------------- */
			if( TimerMonitoraggioRisorse.risorseDisponibili == false){
				this.log.error("["+Sbustamento.ID_MODULO+"] Risorse di sistema non disponibili: "+TimerMonitoraggioRisorse.risorsaNonDisponibile.getMessage(),TimerMonitoraggioRisorse.risorsaNonDisponibile);
				this.ctxMDB.setRollbackOnly();
				return;
			}
			if( Tracciamento.tracciamentoDisponibile == false){
				this.log.error("["+Sbustamento.ID_MODULO+"] Tracciatura non disponibile: "+Tracciamento.motivoMalfunzionamentoTracciamento.getMessage(),Tracciamento.motivoMalfunzionamentoTracciamento);
				this.ctxMDB.setRollbackOnly();
				return;
			}
			if( MsgDiagnostico.gestoreDiagnosticaDisponibile == false){
				this.log.error("["+Sbustamento.ID_MODULO+"] Sistema di diagnostica non disponibile: "+MsgDiagnostico.motivoMalfunzionamentoDiagnostici.getMessage(),MsgDiagnostico.motivoMalfunzionamentoDiagnostici);
				this.ctxMDB.setRollbackOnly();
				return;
			}
			if( Dump.sistemaDumpDisponibile == false){
				this.log.error("["+Sbustamento.ID_MODULO+"] Sistema di dump dei contenuti applicativi non disponibile: "+Dump.motivoMalfunzionamentoDump.getMessage(),Dump.motivoMalfunzionamentoDump);
				this.ctxMDB.setRollbackOnly();
				return;
			}
			
			Sbustamento lib = null;
			try{
				lib = new Sbustamento(this.log);
			}catch(Exception e){
				this.log.error("Sbustamento.instanziazione: "+e.getMessage(),e);
				this.ctxMDB.setRollbackOnly();
				return;
			}
			
			
			/* ----------- Controllo inizializzazione libreria ----------- */
			if( lib.inizializzazioneUltimata == false ){
					this.log = LoggerWrapperFactory.getLogger(SbustamentoMDB.class);
					this.log.error("["+Sbustamento.ID_MODULO+"] Inizializzazione MDB non riuscita");
					this.ctxMDB.setRollbackOnly();
					return;
			}
			
			
			/* ------------  Lettura parametri dalla coda associato al MDB 'Sbustamento'  ------------- */

			//Logger dei messaggi diagnostici
			MsgDiagnostico msgDiag = new MsgDiagnostico(Sbustamento.ID_MODULO);

			msgDiag.mediumDebug("Ricezione richiesta (SbustamentoMessage)...");	
			ObjectMessage received = (ObjectMessage)message;
			SbustamentoMessage sbustamentoMsg = null;
			try{
				sbustamentoMsg = (SbustamentoMessage)received.getObject();
			}	catch(javax.jms.JMSException e){ 
				msgDiag.logErroreGenerico(e,"received.getObject(SbustamentoMessage)");
				return; 
			}
			
			// ID associato alla richiesta
			String idRequest = null; //(serve anche per una validazione sincrona)
			try{
				idRequest = received.getStringProperty("ID");
			}	catch(javax.jms.JMSException e){ 
				msgDiag.logErroreGenerico(e,"received.getStringProperty(ID)");
				return;
			}
			
			// Stato
			OpenSPCoopStateful stato = null;
			try { 
				stato = new OpenSPCoopStateful();
				stato.setIDMessaggioSessione(idRequest);
				//System.out.println("STATEFUL SBUSTAMENTO ["+sbustamentoMsg.getBusta().getServizio()+"] ["+sbustamentoMsg.getBusta().getAzione()+"]");
				stato.setMessageLib(sbustamentoMsg);
			}catch (Exception e) {
				this.log.error("["+Sbustamento.ID_MODULO+"] Invocazione della libreria non riuscita (verrà effettuato un rollback sulla coda JMS)",e);
				this.ctxMDB.setRollbackOnly();
				return;
			}
			
			// update diagnostico
			msgDiag.updateState(stato.getStatoRichiesta(),stato.getStatoRisposta());
						
		
			/* PddContext */
			PdDContext pddContext = sbustamentoMsg.getPddContext();
			try{
				msgDiag.setPddContext(pddContext, ProtocolFactoryManager.getInstance().getProtocolFactoryByName((String) pddContext.getObject(org.openspcoop2.core.constants.Costanti.PROTOCOLLO)));
			}catch(ProtocolException e){
				msgDiag.logErroreGenerico(e,"ProtocolFactory.instanziazione");
				this.ctxMDB.setRollbackOnly();
				return;
			}
			
			/* ------------------ Validity Check  --------------- */
			msgDiag.mediumDebug("Transaction Manager...");
			try{
				if(TransactionManager.validityCheck(msgDiag,Sbustamento.ID_MODULO,idRequest,
						Costanti.INBOX,received.getJMSMessageID(),pddContext)==false){
					msgDiag.addKeyword(CostantiPdD.KEY_ID_MESSAGGIO_TRANSACTION_MANAGER, idRequest);
					msgDiag.addKeyword(CostantiPdD.KEY_PROPRIETARIO_MESSAGGIO, Sbustamento.ID_MODULO);
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
				esito =	lib.onMessage(stato);
			}catch (Exception e) {
				this.log.error("["+Sbustamento.ID_MODULO+"] Invocazione della libreria non riuscita (verrà effettuato un rollback sulla coda JMS)",e);
				this.ctxMDB.setRollbackOnly();
				return;
			}
			
			if (esito.isEsitoInvocazione()==false){
				this.log.info("["+Sbustamento.ID_MODULO+"] Invocazione della libreria terminata con esito negativo, verrà effettuato un rollback sulla coda JMS");
				this.ctxMDB.setRollbackOnly();
				return;
			}	
			
			else {
				this.log.debug("["+Sbustamento.ID_MODULO+"] Invocazione della libreria terminata correttamente");
			}
			
			
		}
	}
}
			
			
			
