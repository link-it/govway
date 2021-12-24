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
import org.openspcoop2.pdd.core.state.OpenSPCoopStateful;
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
import org.openspcoop2.utils.LoggerWrapperFactory;




/**
 * Contiene la definizione di un Message Driven Bean 'Imbustamento', il quale e' un
 * modulo dell'infrastruttura OpenSPCoop.
 * <p>
 * Inoltre la classe contiene due MessageFactory per la costruzione di un oggetto {@link ImbustamentoMessage}
 * utilizzato per la spedizione di messaggi JMS al mdb, tramita una coda JMS utilizzata in ricezione.
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author Tronci Fabio (tronci@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class ImbustamentoMDB implements MessageDrivenBean, MessageListener {

	 /**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;


	/* ********  F I E L D S  P R I V A T I   S T A T I C I  ******** */

	/** Logger utilizzato per debug. */
	private Logger log = null;


	/* ********  F I E L D S  P R I V A T I   ******** */

	/** Message Driven Context */
	private MessageDrivenContext ctxMDB;
	

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
	 * Viene chiamato in causa per istanziare le connessioni JMS 
	 * al primo utilizzo del thread che implementa l'mdb definito in questa classe.
	 *
	 * 
	 */
	public void ejbCreate() {}



	/**
	 * Viene chiamato in causa per rimuovere la connessione JMS e la connessione al database,
	 * entrambe create dal metodo <code>ejbCreate()</code>.
	 * Metodo necessario per l'implementazione dell'interfaccia <code>MessageDrivenBean</code>.
	 * 
	 */
	@Override
	public void ejbRemove() {}




	/**
	 * Attivato,  quando viene ricevuto sulla coda associata al mdb (coda Imbustamento)
	 * un messaggio JMS. Questo metodo implementa la logica del modulo Imbustamento
	 * dell'infrastruttura OpenSPCoop.
	 * 
	 */
	@Override
	public void onMessage(Message message) {
		if(message instanceof ObjectMessage) {


			/* ------------ Controllo inizializzazione OpenSPCoop -------------------- */
			if( OpenSPCoop2Startup.initialize == false ){
				this.ctxMDB.setRollbackOnly();
				return;
			}
			
			/* logger */
			this.log = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();
			if (this.log == null) {
				System.out.println("["+Imbustamento.ID_MODULO+"]"+" Logger nullo. MDB abortito");
				return;
			}
					
			/* ----------- Controllo risorse disponibili --------------- */
			if( TimerMonitoraggioRisorseThread.risorseDisponibili == false){
				this.log.error("["+Imbustamento.ID_MODULO+"] Risorse di sistema non disponibili: "+TimerMonitoraggioRisorseThread.risorsaNonDisponibile.getMessage(),TimerMonitoraggioRisorseThread.risorsaNonDisponibile);
				this.ctxMDB.setRollbackOnly();
				return;
			}
			if( Tracciamento.tracciamentoDisponibile == false){
				this.log.error("["+Imbustamento.ID_MODULO+"] Tracciatura non disponibile: "+Tracciamento.motivoMalfunzionamentoTracciamento.getMessage(),Tracciamento.motivoMalfunzionamentoTracciamento);
				this.ctxMDB.setRollbackOnly();
				return;
			}
			if( MsgDiagnostico.gestoreDiagnosticaDisponibile == false){
				this.log.error("["+Imbustamento.ID_MODULO+"] Sistema di diagnostica non disponibile: "+MsgDiagnostico.motivoMalfunzionamentoDiagnostici.getMessage(),MsgDiagnostico.motivoMalfunzionamentoDiagnostici);
				this.ctxMDB.setRollbackOnly();
				return;
			}
			if( Dump.sistemaDumpDisponibile == false){
				this.log.error("["+Imbustamento.ID_MODULO+"] Sistema di dump dei contenuti applicativi non disponibile: "+Dump.motivoMalfunzionamentoDump.getMessage(),Dump.motivoMalfunzionamentoDump);
				this.ctxMDB.setRollbackOnly();
				return;
			}
			
			
			/* Creazione libreria e verifica che sia inizializzata */
			Imbustamento lib = null;
			try{
				lib = new Imbustamento(this.log);
			}catch(Exception e){
				this.log.error("Imbustamento.instanziazione: "+e.getMessage(),e);
				this.ctxMDB.setRollbackOnly();
				return;
			}
			
			
			if (lib.inizializzazioneUltimata==false){
				this.log = LoggerWrapperFactory.getLogger(ImbustamentoMDB.class);
				this.log.error("["+Imbustamento.ID_MODULO+"] Inizializzazione MDB non riuscita");
				this.ctxMDB.setRollbackOnly();
				return;
			}


			/* ------------  Lettura parametri dalla coda associato al MDB 'Imbustamento'  ------------- */

			// Logger dei messaggi diagnostici
			MsgDiagnostico msgDiag = MsgDiagnostico.newInstance(TipoPdD.DELEGATA,Imbustamento.ID_MODULO);

			// Ricezione Messaggio
			msgDiag.mediumDebug("Ricezione richiesta (ImbustamentoMessage)...");
			ObjectMessage received = (ObjectMessage)message;
			ImbustamentoMessage imbustamentoMsg = null;
			try{
				imbustamentoMsg = (ImbustamentoMessage)received.getObject();
			}	catch(javax.jms.JMSException e){ 
				msgDiag.logErroreGenerico(e,"received.getObject(ImbustamentoMessage)");
				return; 
			}
			if(imbustamentoMsg.getRichiestaDelegata()!=null && imbustamentoMsg.getRichiestaDelegata().getIdPortaDelegata()!=null) {
				msgDiag.updatePorta(imbustamentoMsg.getRichiestaDelegata().getIdPortaDelegata().getNome());	
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
			OpenSPCoopStateful stato = null;
			try {
				stato = new OpenSPCoopStateful();
				stato.setIDMessaggioSessione(idMessageRequest);
				//System.out.println("STATEFUL IMBUSTAMENTO ["+imbustamentoMsg.getInfoServizio().getIDServizio().getServizio()+"] ["+imbustamentoMsg.getInfoServizio().getIDServizio().getAzione()+"]");
				stato.setMessageLib(imbustamentoMsg);	
			}catch (Exception e) {
				this.log.error("["+Imbustamento.ID_MODULO+"] Invocazione della libreria non riuscita (verrà effettuato un rollback sulla coda JMS)",e);
				this.ctxMDB.setRollbackOnly();
				return;
			}
			
			// update diagnostico
			msgDiag.updateState(stato.getStatoRichiesta(),stato.getStatoRisposta());
			
			
					
			/* PddContext */
			PdDContext pddContext = imbustamentoMsg.getPddContext();
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
				if(TransactionManager.validityCheck(msgDiag,Imbustamento.ID_MODULO,idMessageRequest,
						Costanti.OUTBOX,received.getJMSMessageID(),pddContext)==false){
					msgDiag.addKeyword(CostantiPdD.KEY_ID_MESSAGGIO_TRANSACTION_MANAGER, idMessageRequest);
					msgDiag.addKeyword(CostantiPdD.KEY_PROPRIETARIO_MESSAGGIO, Imbustamento.ID_MODULO);
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
				this.log.error("["+Imbustamento.ID_MODULO+"] Invocazione della libreria non riuscita (verrà effettuato un rollback sulla coda JMS)",e);
				this.ctxMDB.setRollbackOnly();
				return;
			}
		
			if (esito.isEsitoInvocazione()==false){
				this.log.info("["+Imbustamento.ID_MODULO+"] Invocazione della libreria terminata con esito negativo, verrà effettuato un rollback sulla coda JMS");
				this.ctxMDB.setRollbackOnly();
			}	
			
			else {
				this.log.debug("["+Imbustamento.ID_MODULO+"] Invocazione della libreria terminata correttamente");
			
			}
		}
	}
}		
