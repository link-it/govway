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




package org.openspcoop2.pdd.core;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import javax.xml.soap.SOAPBody;

import org.slf4j.Logger;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.eccezione.details.DettaglioEccezione;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.integrazione.EsitoRichiesta;
import org.openspcoop2.core.integrazione.utils.EsitoRichiestaXMLUtils;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.ParseException;
import org.openspcoop2.message.SOAPVersion;
import org.openspcoop2.message.SoapUtils;
import org.openspcoop2.pdd.config.ClassNameProperties;
import org.openspcoop2.pdd.config.ConfigurazionePdDManager;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.config.RichiestaApplicativa;
import org.openspcoop2.pdd.config.RichiestaDelegata;
import org.openspcoop2.pdd.config.SoggettoVirtuale;
import org.openspcoop2.pdd.core.behaviour.Behaviour;
import org.openspcoop2.pdd.core.behaviour.BehaviourForwardTo;
import org.openspcoop2.pdd.core.behaviour.BehaviourForwardToConfiguration;
import org.openspcoop2.pdd.core.behaviour.BehaviourForwardToFilter;
import org.openspcoop2.pdd.core.behaviour.IBehaviour;
import org.openspcoop2.pdd.core.behaviour.StatoFunzionalita;
import org.openspcoop2.pdd.core.node.INodeSender;
import org.openspcoop2.pdd.core.state.IOpenSPCoopState;
import org.openspcoop2.pdd.core.state.OpenSPCoopStateful;
import org.openspcoop2.pdd.core.state.OpenSPCoopStateless;
import org.openspcoop2.pdd.logger.MsgDiagnosticiProperties;
import org.openspcoop2.pdd.logger.MsgDiagnostico;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.pdd.mdb.ConsegnaContenutiApplicativi;
import org.openspcoop2.pdd.mdb.ConsegnaContenutiApplicativiMessage;
import org.openspcoop2.pdd.mdb.EsitoLib;
import org.openspcoop2.pdd.mdb.InoltroRisposte;
import org.openspcoop2.pdd.mdb.InoltroRisposteMessage;
import org.openspcoop2.pdd.services.RicezioneBusteMessage;
import org.openspcoop2.pdd.services.RicezioneContenutiApplicativiMessage;
import org.openspcoop2.pdd.timers.TimerGestoreMessaggi;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.engine.builder.DettaglioEccezioneOpenSPCoop2Builder;
import org.openspcoop2.protocol.engine.builder.Imbustamento;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.protocol.engine.driver.RepositoryBuste;
import org.openspcoop2.protocol.engine.driver.RollbackRepositoryBuste;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.Eccezione;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.Integrazione;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.config.IProtocolVersionManager;
import org.openspcoop2.protocol.sdk.constants.ErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.ErroriCooperazione;
import org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione;
import org.openspcoop2.protocol.sdk.state.IState;
import org.openspcoop2.protocol.sdk.state.StateMessage;
import org.openspcoop2.protocol.sdk.state.StatelessMessage;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.resources.Loader;


/**
 * Libreria contenente metodi utili per lo smistamento delle buste 
 * all'interno dei moduli EJB di openspcoop.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class EJBUtils {

	/** Logger utilizzato per debug. */
	private Logger log = null;

	private IOpenSPCoopState openSPCoopState;

	/** Indicazione sul modulo  */
	private String idModulo = null;
	/** Tipo di Porta */
	private TipoPdD tipoPdD;
	/** Indicazione sul codice porta  */
	private IDSoggetto identitaPdD = null;
	/** Identificativo generato per la richiesta ed utilizzato per collegare la gestione del servizio richiesto, 
	tra i vari nodi dell'infrastruttura openspcoop. */
	private String idSessione;
	/** Identificativo del messaggio in gestione 
	(questo id coincide con l'id della sessione nella richiesta ma non nella risposta) */
	private String idMessage;
	/** Tipo di Messaggio (INBOX/OUTBOX) */
	private String tipo;
	/** Scenario di Cooperazione */
	private String scenarioCooperazione;
	/** MessaggioDiagnostico */
	private MsgDiagnostico msgDiag;
	
	/** Indicazione se siamo in modalita oneway11 */
	private boolean oneWayVersione11 = false;
	/** Indicazione se la porta delegata/applicativa richiesta una modalita stateless (oneway o sincrono) 
	 *  Il caso oneway11 possiedera questo booleano con il valore false.
	 * */
	private boolean portaDiTipoStateless_esclusoOneWay11;
	/** Indicazione se deve essere gestita la richiesta in caso di errore */
	private boolean rollbackRichiestaInCasoErrore = true;
	/** Indicazione se deve essere gestita la richiesta in caso di errore */
	private boolean rollbackRichiestaInCasoErrore_rollbackHistory = true;
	/** Indicazione se siamo in modalita di routing */
	private boolean routing;
	
	
	/** PropertiesReader */
	private OpenSPCoop2Properties propertiesReader;
	/** ConfigurazionePdDReader */
	private ConfigurazionePdDManager configurazionePdDReader;
	/** Indicazione se la classe viene utilizzata dal router */
	private boolean functionAsRouter = false;
	
	/** Gestione tempi di attraversamento */
	private Timestamp spedizioneMsgIngresso;
	private Timestamp ricezioneMsgRisposta;

	/** Reply on new Connection: buste, escluse le buste di risposta sincrona */
	private boolean replyOnNewConnection;
	/** Utilizzo indirizzo telematico: utilizzo dell'indirizzo telematico */
	private boolean utilizzoIndirizzoTelematico;

	/** Tipologia di porta di domino del soggetto mittente */
	private String implementazionePdDSoggettoMittente;
	/** Tipologia di porta di domino del soggetto destinatario */
	private String implementazionePdDSoggettoDestinatario;
	
	/** ServizioApplicativo erogatore */
	private String servizioApplicativoErogatore;
	
	/** PdDContext */
	private PdDContext pddContext;
	public void setPddContext(PdDContext pddContext) {
		this.pddContext = pddContext;
	}
	
	/** OLD Function Name */
	private String oldFunctionName;

	/** INodeSender */
	private INodeSender nodeSender = null;

	/** ProtocolFactory */
	IProtocolFactory protocolFactory = null;
	IProtocolVersionManager protocolManager = null;
	
	DettaglioEccezioneOpenSPCoop2Builder dettaglioBuilder = null;
	
	public synchronized void initializeNodeSender(OpenSPCoop2Properties propertiesReader,Logger logCore) throws EJBUtilsException{
		if(this.nodeSender!=null)
			return; // inizializzato da un altro thread

		// Inizializzazione NodeSender
		String classTypeNodeSender = null;
		try{
			classTypeNodeSender = ClassNameProperties.getInstance().getNodeSender(propertiesReader.getNodeSender());
			this.nodeSender = (INodeSender) Loader.getInstance().newInstance(classTypeNodeSender);
			AbstractCore.init(this.nodeSender, this.pddContext, this.protocolFactory);
			logCore.info("Inizializzazione gestore NodeSender di tipo "+classTypeNodeSender+" effettuata.");
		}catch(Exception e){
			throw new EJBUtilsException("Riscontrato errore durante il caricamento della classe ["+classTypeNodeSender+
					"] da utilizzare per la spedizione nell'infrastruttura: "+e.getMessage());
		}
	}

	public INodeSender getNodeSender(OpenSPCoop2Properties propertiesReader,Logger log)throws EJBUtilsException{
		if(this.nodeSender==null)
			this.initializeNodeSender(propertiesReader, log);
		return this.nodeSender;
	}



	/**
	 * Costruttore
	 *
	 * @param identitaPdD Codice del dominio che sta gestendo la richiesta.
	 * @param aIDModulo Identificativo del Sender.
	 * @param idSessione Identificativo generato per la richiesta ed utilizzato per collegare la gestione del servizio richiesto, 
	 *        tra i vari nodi dell'infrastruttura openspcoop (serve per la spedizione verso altri moduli).
	 * @param idMessage Identificativo del messaggio in gestione 
	 *	      (questo id coincide con l'id della sessione nella richiesta ma non nella risposta)
	 * @param tipo di Messaggio (INBOX/OUTBOX)
	 * @param openspcoop_state
	 * 
	 */
	public EJBUtils(IDSoggetto identitaPdD,TipoPdD tipoPdD,String aIDModulo,String idSessione,
			String idMessage,String tipo, IOpenSPCoopState openspcoop_state ,MsgDiagnostico msgDiag,boolean functionAsRouter,
			String implementazionePdDSoggettoMittente, String implementazionePdDSoggettoDestinatario,
			String profiloGestione,PdDContext pddContext) throws EJBUtilsException {
		this.identitaPdD = identitaPdD;
		this.tipoPdD = tipoPdD;
		this.idModulo = aIDModulo;
		this.idSessione = idSessione;
		this.idMessage = idMessage; 
		this.tipo = tipo;
		this.openSPCoopState = openspcoop_state;
		this.functionAsRouter = functionAsRouter;
		this.log = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();
		if(this.log==null){
			this.log = LoggerWrapperFactory.getLogger(EJBUtils.class);
		}
		this.msgDiag = msgDiag;
		this.propertiesReader = OpenSPCoop2Properties.getInstance();
		if(openspcoop_state==null)
			this.configurazionePdDReader = ConfigurazionePdDManager.getInstance();
		else
			this.configurazionePdDReader = ConfigurazionePdDManager.getInstance(openspcoop_state.getStatoRichiesta(),openspcoop_state.getStatoRisposta());

		this.implementazionePdDSoggettoMittente = implementazionePdDSoggettoMittente;
		this.implementazionePdDSoggettoDestinatario = implementazionePdDSoggettoDestinatario;
		
		this.pddContext = pddContext;
		
		try{
			this.protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName((String) this.pddContext.getObject(org.openspcoop2.core.constants.Costanti.PROTOCOLLO));
			this.protocolManager = this.protocolFactory.createProtocolVersionManager(profiloGestione);
			this.dettaglioBuilder = new DettaglioEccezioneOpenSPCoop2Builder(this.log, this.protocolFactory);
		}catch(Exception e){
			throw new EJBUtilsException(e.getMessage(),e);
		}
		
		// richiede all'interno pddContext e protocolFactory
		this.initializeNodeSender(this.propertiesReader, this.log);
		
	}
	public void setIdMessage(String idMessage) {
		this.idMessage = idMessage;
	}

	private void setEJBSuffixFunctionName(){
		if(this.msgDiag!=null){
			this.oldFunctionName = this.msgDiag.getFunzione();
			this.msgDiag.setFunzione(this.msgDiag.getFunzione()+".EJBUtils");
		}
	}
	private void unsetEJBSuffixFunctionName(){
		if(this.msgDiag!=null && this.oldFunctionName!=null){
			this.msgDiag.setFunzione(this.oldFunctionName);
		}
	}


	/**
	 * Aggiorna openspcoop_state
	 *
	 * 
	 * 
	 */
	public void updateOpenSPCoopState(IOpenSPCoopState openSPCoopState){
		this.openSPCoopState = openSPCoopState;
	}


	/**
	 * Imposta l'identificativo di Sessione
	 *
	 * @param id identificativo di Sessione
	 * 
	 */
	public void updateIdSessione(String id){
		this.idSessione = id;
	}
	/**
	 * Imposta l'utilizzo della reply su una nuova connessione.
	 *
	 * @param replyOnNewConnection Utilizzo della reply su una nuova connessione.
	 * 
	 */
	public void setReplyOnNewConnection(boolean replyOnNewConnection) {
		this.replyOnNewConnection = replyOnNewConnection;
	}
	/**
	 * Imposta l'utilizzo di un eventuale indirizzo telematico.
	 *
	 * @param utilizzoIndirizzoTelematico utilizzo di un eventuale indirizzo telematico.
	 * 
	 */
	public void setUtilizzoIndirizzoTelematico(boolean utilizzoIndirizzoTelematico) {
		this.utilizzoIndirizzoTelematico = utilizzoIndirizzoTelematico;
	}














	/** ------------------- Metodi utilizzati per rilasciare le risorse con errore all'interno dell'EJB ---------------- */    

	/*  Tutti questi metodi lavorano senza il riferimento all' ctxEJB perche' il rollback JMS viene delegato al chiamante
	 * */
	public void rollbackMessage(String motivoRollbackEJB, EsitoLib esito){

		this.setEJBSuffixFunctionName();
		try{

			// Imposto Motivo dell'errore
			GestoreMessaggi msg = new GestoreMessaggi(this.openSPCoopState, true, this.idMessage,this.tipo,this.msgDiag,this.pddContext);
			msg.setOneWayVersione11(this.oneWayVersione11);
			msg.aggiornaErroreProcessamentoMessaggio(motivoRollbackEJB, null);

			esito.setErroreProcessamentoMessaggioAggiornato(true);
			
		}catch(Exception e){

			this.msgDiag.logErroreGenerico(e, "EJBUtils.rollbackMessage(motivoRollbackEJB)");

		}finally{
			this.unsetEJBSuffixFunctionName();
		}
	}

	public void rollbackMessage(String motivoRollbackEJB, String servizioApplicativo, EsitoLib esito){

		this.setEJBSuffixFunctionName();
		try{

			// Imposto Motivo dell'errore - lavora sempre sulla richiesta
			GestoreMessaggi msg = new GestoreMessaggi(this.openSPCoopState, true ,this.idMessage,this.tipo,this.msgDiag,this.pddContext);
			msg.setOneWayVersione11(this.oneWayVersione11);
			msg.aggiornaErroreProcessamentoMessaggio(motivoRollbackEJB, servizioApplicativo);

			esito.setErroreProcessamentoMessaggioAggiornato(true);
			
		}catch(Exception e){

			this.msgDiag.logErroreGenerico(e, "EJBUtils.rollbackMessage(motivoRollbackEJB,servizioApplicativo)");

		}finally{
			this.unsetEJBSuffixFunctionName();
		}
	}

	public void rollbackMessage(String motivoRollbackEJB, Timestamp dataRiconsegna, String servizioApplicativo, EsitoLib esito){

		this.setEJBSuffixFunctionName();
		try{

			// Imposto Motivo dell'errore
			GestoreMessaggi msg = new GestoreMessaggi(this.openSPCoopState, true, this.idMessage,this.tipo,this.msgDiag,this.pddContext);
			msg.setOneWayVersione11(this.oneWayVersione11);
			msg.aggiornaErroreProcessamentoMessaggio(motivoRollbackEJB, servizioApplicativo);

			esito.setErroreProcessamentoMessaggioAggiornato(true);
			
			if(dataRiconsegna!=null){
				msg.aggiornaDataRispedizione(dataRiconsegna,servizioApplicativo);
				
				esito.setDataRispedizioneAggiornata(true);
			}

		}catch(Exception e){

			this.msgDiag.logErroreGenerico(e, "EJBUtils.rollbackMessage(motivoRollbackEJB,dataRiconsegna,servizioApplicativo)");

		}finally{
			this.unsetEJBSuffixFunctionName();
		}
	}


	public void rollbackMessage(String motivoRollbackEJB, Timestamp dataRiconsegna, EsitoLib esito){

		this.setEJBSuffixFunctionName();
		try{

			// Imposto Motivo dell'errore
			GestoreMessaggi msg = new GestoreMessaggi(this.openSPCoopState, true, this.idMessage,this.tipo,this.msgDiag,this.pddContext);
			msg.setOneWayVersione11(this.oneWayVersione11);
			msg.aggiornaErroreProcessamentoMessaggio(motivoRollbackEJB, null);

			esito.setErroreProcessamentoMessaggioAggiornato(true);
			
			if(dataRiconsegna!=null){
				msg.aggiornaDataRispedizione(dataRiconsegna,null);
				
				esito.setDataRispedizioneAggiornata(true);
			}

		}catch(Exception e){

			this.msgDiag.logErroreGenerico(e, "EJBUtils.rollbackMessage(motivoRollbackEJB,dataRiconsegna)");

		}finally{
			this.unsetEJBSuffixFunctionName();
		}
	}

	/**
	 * Rappresenta una situazione in cui un EJB deve mantenere in coda il messaggio che ha provato a gestire, a causa di errori
	 * 
	 * @param motivoRollbackEJB Motivo dell'errore che ha causato il Rollback sull'EJB
	 * 
	 */
	public void updateErroreProcessamentoMessage(String motivoRollbackEJB, EsitoLib esito){
		updateErroreProcessamentoMessage(motivoRollbackEJB,null,esito);
	}
	/**
	 * Rappresenta una situazione in cui un EJB deve mantenere in coda il messaggio che ha provato a gestire, a causa di errori
	 * 
	 * @param motivoRollbackEJB Motivo dell'errore che ha causato il Rollback sull'EJB
	 * @param dataRispedizione Data per la rispedizione del msg
	 * 
	 */
	public void updateErroreProcessamentoMessage(String motivoRollbackEJB, java.sql.Timestamp dataRispedizione, EsitoLib esito){

		this.setEJBSuffixFunctionName();
		try{

			// Imposto Motivo dell'errore
			GestoreMessaggi msg = new GestoreMessaggi(this.openSPCoopState, true, this.idMessage,this.tipo,this.msgDiag,this.pddContext);
			msg.setOneWayVersione11(this.oneWayVersione11);
			msg.aggiornaErroreProcessamentoMessaggio(motivoRollbackEJB,null);
			
			esito.setErroreProcessamentoMessaggioAggiornato(true);
			
			if(dataRispedizione!=null){
				msg.aggiornaDataRispedizione(dataRispedizione,null);
				
				esito.setDataRispedizioneAggiornata(true);	
			}

		}catch(Exception e){

			this.msgDiag.logErroreGenerico(e, "EJBUtils.updateErroreProcessamentoMessage");

		}finally{
			this.unsetEJBSuffixFunctionName();
		}
	}









	/** ------------------- Metodi utilizzati per rilasciare le risorse all'interno dell'EJB ---------------- */    



	/**
	 * Rilascia un messaggio precedentemente salvato, e 
	 * effettua anche un rollback di un OutBox Message utilizzando l'id del Messaggio
	 *
	 * 
	 */
	public void releaseOutboxMessage(boolean isMessaggioRichiesta) throws EJBUtilsException{
		releaseMessage(true,false,null,null,true, isMessaggioRichiesta);
	}

	/**
	 * Rilascia un messaggio precedentemente salvato, e 
	 * effettua anche un rollback di un OutBox Message utilizzando l'id del Messaggio
	 *
	 * 
	 */
	public void releaseOutboxMessage(boolean cleanHistory, boolean isMessaggioRichiesta) throws EJBUtilsException{
		releaseMessage(true,false,null,null,cleanHistory, isMessaggioRichiesta);
	}

	/**
	 * Rilascia un messaggio precedentemente salvato, 
	 * e effettua anche un rollback di un OutBox Message utilizzando l'id del Messaggio
	 * Inoltre effettua anche un rollback di un Outbox/Inbox Message (tipoRiferimentoMessaggio) utilizzando il riferimentoMessaggio
	 *
	 * @param tipo Tipo di Messaggio su cui effettuare il Rollback
	 * @param id Identificativo del Messaggio su cui effettuare il Rollback
	 * 
	 */
	public void releaseOutboxMessage(String tipo,String id, boolean isMessaggioRichiesta) throws EJBUtilsException{
		if(Costanti.INBOX.equals(tipo)==false && Costanti.OUTBOX.equals(tipo)==false)
			throw new EJBUtilsException("EJBUtils.releaseOutboxMessage error: Tipo di Messaggio non definito");
		if(id ==null)
			throw new EJBUtilsException("EJBUtils.releaseOutboxMessage error: ID Messaggio non definito");
		releaseMessage(true,false,tipo,id,true, isMessaggioRichiesta);
	}

	/**
	 * Rilascia un messaggio precedentemente salvato, e 
	 * effettua anche un rollback di un InBox Message utilizzando l'id del Messaggio
	 *
	 * 
	 */
	public void releaseInboxMessage(boolean isMessaggioRichiesta) throws EJBUtilsException{
		releaseMessage(false,true,null,null,true, isMessaggioRichiesta);
	}

	/**
	 * Rilascia un messaggio precedentemente salvato, e 
	 * effettua anche un rollback di un InBox Message utilizzando l'id del Messaggio
	 *
	 * 
	 */
	public void releaseInboxMessage(boolean cleanHistory, boolean isMessaggioRichiesta) throws EJBUtilsException{
		releaseMessage(false,true,null,null,cleanHistory, isMessaggioRichiesta);
	}

	/**
	 * Rilascia un messaggio precedentemente salvato, 
	 * e effettua anche un rollback di un OutBox Message utilizzando l'id del Messaggio
	 * Inoltre effettua anche un rollback di un Outbox/Inbox Message (tipoRiferimentoMessaggio) utilizzando il riferimentoMessaggio
	 *
	 * @param tipo Tipo di Messaggio su cui effettuare il Rollback
	 * @param id Identificativo del Messaggio su cui effettuare il Rollback
	 * 
	 */
	public void releaseInboxMessage(String tipo,String id, boolean isMessaggioRichiesta) throws EJBUtilsException{
		if(Costanti.INBOX.equals(tipo)==false && Costanti.OUTBOX.equals(tipo)==false)
			throw new EJBUtilsException("EJBUtils.releaseOutboxMessage error: Tipo di Messaggio non definito");
		if(id ==null)
			throw new EJBUtilsException("EJBUtils.releaseOutboxMessage error: ID Messaggio non definito");
		releaseMessage(false,true,tipo,id,true, isMessaggioRichiesta);
	}

	/**
	 * Rilascia un messaggio precedentemente salvato, e se richiesto effettua anche un rollback
	 *
	 * @param rollbackOutbox indicazione se effettuare un Rollback di un OutBox Message
	 * @param rollbackInbox indicazione se effettuare un Rollback di un InBox Message
	 * @param tipo Tipo di Messaggio su cui effettuare il Rollback
	 * @param id Identificativo del Messaggio su cui effettuare il Rollback
	 * 
	 */
	private void releaseMessage(boolean rollbackOutbox,boolean rollbackInbox,String tipo,String id,boolean cleanHistory, boolean isMessaggioRichiesta) throws EJBUtilsException{ 

		StateMessage stateMSG = (isMessaggioRichiesta) ? (StateMessage) this.openSPCoopState.getStatoRichiesta() 
				: (StateMessage) this.openSPCoopState.getStatoRisposta();	

		if (stateMSG instanceof StatelessMessage && !this.oneWayVersione11){
			return;
		}
		
		RollbackRepositoryBuste rollbackBuste = null;
		RollbackRepositoryBuste rollbackBusteRifMessaggio = null;
		GestoreMessaggi msg = null;
		try{

			/* ------- Gestione Msg --------------- */

			// Rollback del messaggio
			if(rollbackOutbox){
				rollbackBuste = new RollbackRepositoryBuste(this.idMessage, stateMSG, this.oneWayVersione11);
				rollbackBuste.rollbackBustaIntoOutBox(cleanHistory);
			}else if(rollbackInbox){
				rollbackBuste = new RollbackRepositoryBuste(this.idMessage, stateMSG, this.oneWayVersione11);
				rollbackBuste.rollbackBustaIntoInBox(cleanHistory);
			}

			// Rollback sul RiferimentoMessaggio
			if(tipo!=null && id!=null){
				rollbackBusteRifMessaggio = new RollbackRepositoryBuste(id, stateMSG, this.oneWayVersione11);
				if(Costanti.OUTBOX.equals(tipo))
					rollbackBusteRifMessaggio.rollbackBustaIntoOutBox();
				else
					rollbackBusteRifMessaggio.rollbackBustaIntoInBox();		
			}

			// Imposto ProprietarioMessaggioRichiesta per eliminazione
			msg = new GestoreMessaggi(this.openSPCoopState, true, this.idMessage,this.tipo,this.msgDiag,this.pddContext);
			msg.setOneWayVersione11(this.oneWayVersione11);
			msg.aggiornaProprietarioMessaggio(TimerGestoreMessaggi.ID_MODULO);

			/* ------ Commit delle modifiche ------ */
			this.openSPCoopState.commit();

			// Aggiornamento cache proprietario messaggio
			String idBustaRichiestaCorrelata = null;
			if(this.idMessage.equals(this.idSessione)==false)
				idBustaRichiestaCorrelata = this.idSessione;
			msg.addProprietariIntoCache_readFromTable("EJBUtils."+this.idModulo, "releaseMessage",idBustaRichiestaCorrelata,this.functionAsRouter);

		}catch (Exception e) {	
			stateMSG.closePreparedStatement();
			// Rilancio l'eccezione
			throw new EJBUtilsException("EJBUtils.releaseMessage error: "+e.getMessage(),e);
		}  	
	}





	/** ------------------- Metodi per la spedizione di risposte Applicative Errore---------------- */

	/**
	 * Spedisce un Messaggio di risposta Applicativo  al modulo di openspcoop che gli compete.
	 * La spedizione viene naturalmente effettuata solo se l'idModuloInAttesa nella richiesta delegata e' diverso da null 
	 * o se e' stata definita una ricezione di contenuti asincroni.
	 *
	 * @param responseMessageError Message di errore applicativo
	 * @param richiestaDelegata Dati che caratterizzano la richiesta.
	 * 
	 */
	public void sendRispostaApplicativaErrore(OpenSPCoop2Message responseMessageError, 
			RichiestaDelegata richiestaDelegata,
			PortaDelegata pd,ServizioApplicativo sa)throws EJBUtilsException{
		sendRispostaApplicativaErrore(responseMessageError,richiestaDelegata,true,pd,sa);
	}

	/**
	 * Spedisce un Messaggio di risposta Applicativo  al modulo di openspcoop che gli compete.
	 * La spedizione viene naturalmente effettuata solo se l'idModuloInAttesa nella richiesta delegata e' diverso da null 
	 * o se e' stata definita una ricezione di contenuti asincroni.
	 *
	 * @param responseMessageError Message di errore applicativo
	 * @param richiestaDelegata Dati che caratterizzano la richiesta.
	 * @param rollbackRichiesta Indicazione se effettuare il rollback della richiesta
	 * 
	 */
	public void sendRispostaApplicativaErrore(OpenSPCoop2Message responseMessageError, 
			RichiestaDelegata richiestaDelegata,boolean rollbackRichiesta,
			PortaDelegata pd,ServizioApplicativo sa) throws EJBUtilsException{ 

		String idTransazione = (String) this.pddContext.getObject(org.openspcoop2.core.constants.Costanti.CLUSTER_ID);
		
		if (this.openSPCoopState instanceof OpenSPCoopStateless)
			((OpenSPCoopStateless)this.openSPCoopState).setRispostaMsg(responseMessageError);

		StateMessage statoRichiesta = (StateMessage) this.openSPCoopState.getStatoRichiesta();
		StateMessage statoRisposta = (StateMessage) this.openSPCoopState.getStatoRisposta();

		String correlazioneApplicativa = null;
		if(richiestaDelegata!=null)
			correlazioneApplicativa = richiestaDelegata.getIdCorrelazioneApplicativa();
		
		String correlazioneApplicativaRisposta = null;
		if(richiestaDelegata!=null)
			correlazioneApplicativaRisposta = richiestaDelegata.getIdCorrelazioneApplicativaRisposta();
		
		String nomePorta = null;
		if(richiestaDelegata!=null)
			nomePorta = richiestaDelegata.getLocationPD();
		
		RollbackRepositoryBuste rollbackBuste = null;
		RollbackRepositoryBuste rollbackBusteRifMessaggio = null;
		GestoreMessaggi msgRequest = null;
		GestoreMessaggi msgResponse = null;
		GestoreMessaggi msgSbloccoRicezioneContenutiApplicativi = null; // Evenutuale sblocco se presente ricezioneContenutiApplicativi per profili oneway/sincroni

		// Aggiungo costante servizio applicativo
		this.msgDiag.addKeyword(CostantiPdD.KEY_SA_EROGATORE, richiestaDelegata.getServizioApplicativo());
		
		this.setEJBSuffixFunctionName();
		try{


			/* ------- Raccolta dati ------ */
			String idModuloInAttesa = richiestaDelegata.getIdModuloInAttesa();


			/* IMPOSTATA A FALSE IN AUTOMATICO PER FAR TORNARE GLI ERRORI SEMPRE SULLA SOLITA CONNESSIONE */
			// Se riservira' basta scommentare la riga sottostante.
			//boolean consegnaRispostaAsincrona = richiestaDelegata.getUtilizzoConsegnaAsincrona() &&  this.configurazionePdDReader.existsConsegnaRispostaAsincrona(richiestaDelegata);
			boolean consegnaRispostaAsincrona = false;


			/* ------- Gestione MsgRichiesta --------------- */


			// RollbackApplicativo elimino tutti gli accessi.
			// (uso l'ID della sessione, poiche' tutti i dati 'protocollo' sono salvati con l'ID della richiesta e non della risposta)
			// Il rollback della richiesta serve in caso di errori.
			if(rollbackRichiesta){
				rollbackBusteRifMessaggio = new RollbackRepositoryBuste(this.idSessione, statoRichiesta, this.oneWayVersione11);
				rollbackBusteRifMessaggio.rollbackBustaIntoOutBox(this.rollbackRichiestaInCasoErrore_rollbackHistory);
			}else{
				// se sono in un flusso di richiesta (imbustamento/inoltroBuste devo cmq eliminare l'accesso da pdd)
				rollbackBusteRifMessaggio = new RollbackRepositoryBuste(this.idSessione, statoRichiesta, this.oneWayVersione11);
				rollbackBusteRifMessaggio.clearAccessiIntoOutBox(false,false,true);
			}

			if(this.idSessione.equals(this.idMessage)==false){
				// Siamo in SbustamentoRisposte...
				// Rollback del messaggio di risposta
				rollbackBuste = new RollbackRepositoryBuste(this.idMessage, statoRichiesta, this.oneWayVersione11);
				rollbackBuste.rollbackBustaIntoInBox();
			}

			// Imposto ProprietarioMessaggioRichiesta per eliminazione
			msgRequest = new GestoreMessaggi(this.openSPCoopState, true , this.idMessage,this.tipo,this.msgDiag,this.pddContext);
			msgRequest.setOneWayVersione11(this.oneWayVersione11);
			msgRequest.aggiornaProprietarioMessaggio(TimerGestoreMessaggi.ID_MODULO);








			/* ------- Gestione MsgRisposta --------------- */


			// Creo stato di risposta
			String idRisposta = null;
			if( (consegnaRispostaAsincrona) || (idModuloInAttesa != null) ){

				// --- Creo lo stato del messaggio,visto che sto' spedendo una risposta generata da OpenSPCoop, 
				// creando anche un IDResponse)
				Imbustamento imbustatore = new Imbustamento(this.protocolFactory);

				idRisposta = 
					imbustatore.buildID(this.openSPCoopState.getStatoRichiesta(),this.identitaPdD, idTransazione, 
							this.propertiesReader.getGestioneSerializableDB_AttesaAttiva(),
							this.propertiesReader.getGestioneSerializableDB_CheckInterval(),
							Boolean.FALSE);
				if(idRisposta == null){
					throw new Exception("Identificativo non costruito.");
				}  

				// oraRegistrazione
				Timestamp oraRegistrazioneMessaggio = DateManager.getTimestamp();

				msgResponse = new GestoreMessaggi(this.openSPCoopState, false, idRisposta,Costanti.INBOX,this.msgDiag,this.pddContext);
				msgResponse.registraMessaggio(responseMessageError,oraRegistrazioneMessaggio, saveMessageStateless(pd),
						correlazioneApplicativa,correlazioneApplicativaRisposta);
				msgResponse.aggiornaRiferimentoMessaggio(this.idSessione);
				
				// --- Aggiorno Proprietario
				if( consegnaRispostaAsincrona ){
					// Aggiorno proprietario Messaggio, consegna asincrona: CONSEGNA_BUSTE_SOAP
					msgResponse.aggiornaProprietarioMessaggio(ConsegnaContenutiApplicativi.ID_MODULO);
				}else{
					if(idModuloInAttesa != null){
						// Aggiorno proprietario Messaggio, consegna sincrona: ACCETTAZIONEXXX
						msgResponse.aggiornaProprietarioMessaggio(idModuloInAttesa);
					}
				}

				// --- Lettura dati di configurazione 
				boolean servizioApplicativoConConnettore = false;
				boolean getMessageAbilitato = false;
				boolean sbustamentoSoap = false;
				boolean sbustamentoInformazioniProtocollo = true;
				try{
					servizioApplicativoConConnettore = this.configurazionePdDReader.consegnaRispostaAsincronaConConnettore(sa);
					getMessageAbilitato = this.configurazionePdDReader.consegnaRispostaAsincronaConGetMessage(sa);
					sbustamentoSoap = this.configurazionePdDReader.consegnaRispostaAsincronaConSbustamento(sa);
					sbustamentoInformazioniProtocollo = this.configurazionePdDReader.consegnaRispostaAsincronaConSbustamentoInformazioniProtocollo(sa);
				}catch(DriverConfigurazioneNotFound de){}

				if( (getMessageAbilitato==false) && (servizioApplicativoConConnettore==false) && (idModuloInAttesa==null)){
					this.msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI,"consegnaNonDefinita");
					return; 
				}

				String tipoConsegna = GestoreMessaggi.CONSEGNA_TRAMITE_INTEGRATION_MANAGER;

				// --- Spedizione al successivo modulo 
				if( consegnaRispostaAsincrona ){
					if(servizioApplicativoConConnettore){
						// spedizione al modulo ConsegnaContenutiApplicativi
						richiestaDelegata.setScenario(Costanti.SCENARIO_CONSEGNA_CONTENUTI_APPLICATIVI);
						sendToConsegnaContenutiApplicativi_rispostaConsegnataModalitaAsincrona(richiestaDelegata,idRisposta, msgResponse);
						tipoConsegna = GestoreMessaggi.CONSEGNA_TRAMITE_CONNETTORE;
					}else{
						this.msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI,"integrationManager.messaggioDisponibile");
					}
				}else{
					if(idModuloInAttesa != null){
						// spedizione al modulo RicezioneContenutiApplicativiXXX
						sendToRicezioneContenutiApplicativi(idModuloInAttesa,idRisposta,
								richiestaDelegata.getIdCollaborazione(),
								richiestaDelegata.getProfiloCollaborazione(),richiestaDelegata.getProfiloCollaborazioneValue());
						tipoConsegna = GestoreMessaggi.CONSEGNA_TRAMITE_CONNECTION_REPLY;
					}
				}

				// ---- Registra SIL Destinatario del Messaggio
				msgResponse.registraDestinatarioMessaggio(richiestaDelegata.getServizioApplicativo(),
						sbustamentoSoap,sbustamentoInformazioniProtocollo,getMessageAbilitato,tipoConsegna,oraRegistrazioneMessaggio,
						nomePorta);

			} // end Gestione Risposta

			/* ------- Gestione msgSbloccoRicezioneContenutiApplicativi --------------- */
			String idSbloccoRicezioneContenutiApplicativi = null;
			if(consegnaRispostaAsincrona && idModuloInAttesa != null){
				//	Creo stato per msgSbloccoRicezioneContenutiApplicativi
				Imbustamento imbustatore = new Imbustamento(this.protocolFactory);
				idSbloccoRicezioneContenutiApplicativi= 
					imbustatore.buildID(this.openSPCoopState.getStatoRichiesta(),this.identitaPdD, idTransazione, 
							this.propertiesReader.getGestioneSerializableDB_AttesaAttiva(),
							this.propertiesReader.getGestioneSerializableDB_CheckInterval(),
							Boolean.FALSE);
				if(idSbloccoRicezioneContenutiApplicativi == null){
					throw new Exception("Identificativo non costruito.");
				}  
				msgSbloccoRicezioneContenutiApplicativi = new GestoreMessaggi( this.openSPCoopState, false, idSbloccoRicezioneContenutiApplicativi,Costanti.INBOX,this.msgDiag,this.pddContext);
				if(this.protocolManager.isHttpEmptyResponseOneWay())
					msgSbloccoRicezioneContenutiApplicativi.registraMessaggio(org.openspcoop2.message.SoapUtils.build_Soap_Empty(responseMessageError.getVersioneSoap()),correlazioneApplicativa,correlazioneApplicativaRisposta);
				else
					msgSbloccoRicezioneContenutiApplicativi.registraMessaggio(this.buildOpenSPCoopOK_soapMsg(responseMessageError.getVersioneSoap(),this.idSessione),correlazioneApplicativa,correlazioneApplicativaRisposta);
				msgSbloccoRicezioneContenutiApplicativi.aggiornaRiferimentoMessaggio(this.idSessione);
				//	--- Aggiorno Proprietario
				msgSbloccoRicezioneContenutiApplicativi.aggiornaProprietarioMessaggio(idModuloInAttesa);
				//	--- Spedizione al modulo RicezioneContenutiApplicativiXXX
				sendToRicezioneContenutiApplicativi(idModuloInAttesa,idSbloccoRicezioneContenutiApplicativi,
						richiestaDelegata.getIdCollaborazione(),
						richiestaDelegata.getProfiloCollaborazione(),richiestaDelegata.getProfiloCollaborazioneValue());
			}


			/* ------ Commit/Close connessione al DB ------ */
			this.openSPCoopState.commit();

			// Aggiornamento cache messaggio
			if(msgSbloccoRicezioneContenutiApplicativi !=null)
				msgSbloccoRicezioneContenutiApplicativi.addMessaggiIntoCache_readFromTable(this.idModulo, "sendRispostaApplicativaErrore [sblocco]");
			if(msgResponse !=null)
				msgResponse.addMessaggiIntoCache_readFromTable(this.idModulo, "sendRispostaApplicativaErrore [risposta]");

			// Aggiornamento cache proprietario messaggio
			if(msgRequest!=null){
				String idRichiestaCorrelata = null;
				if(this.idMessage.equals(this.idSessione)==false)
					idRichiestaCorrelata = this.idSessione;
				msgRequest.addProprietariIntoCache_readFromTable(this.idModulo, "sendRispostaApplicativaErrore [richiesta]",idRichiestaCorrelata,this.functionAsRouter);
			}
			if(msgSbloccoRicezioneContenutiApplicativi !=null)
				msgSbloccoRicezioneContenutiApplicativi.addProprietariIntoCache_readFromTable(this.idModulo, "sendRispostaApplicativaErrore [sblocco]",this.idSessione,this.functionAsRouter);
			if(msgResponse !=null)
				msgResponse.addProprietariIntoCache_readFromTable(this.idModulo, "sendRispostaApplicativaErrore [risposta]",this.idSessione,this.functionAsRouter);

		}catch (Exception e) {	
			this.msgDiag.logErroreGenerico(e, "EJBUtils.sendRispostaApplicativaErrore");
			statoRichiesta.closePreparedStatement();
			statoRisposta.closePreparedStatement();
			throw new EJBUtilsException("EJBUtils.sendRispostaApplicativaErrore error: "+e.getMessage(),e);
		}
		finally{
			this.unsetEJBSuffixFunctionName();
		}	
	}
















	/** ------------------- Metodi per la spedizione di risposte Applicative ---------------- */

	/**
	 * Spedisce un Messaggio di risposta Applicativo  al modulo di openspcoop che gli compete.
	 * La spedizione viene naturalmente effettuata solo se l'idModuloInAttesa nella richiesta delegata e' diverso da null 
	 * o se e' stata definita una ricezione di contenuti asincroni.
	 *
	 * @param richiestaDelegata Dati che caratterizzano la richiesta.
	 * 
	 */
	public GestoreMessaggi sendRispostaApplicativa(RichiestaDelegata richiestaDelegata,
			PortaDelegata pd,ServizioApplicativo sa)throws EJBUtilsException{ 
		return sendRispostaApplicativa(null,richiestaDelegata,this.idMessage,pd,sa);
	}


	/**
	 * Spedisce un Messaggio di risposta Applicativo  al modulo di openspcoop che gli compete.
	 * La spedizione viene naturalmente effettuata solo se l'idModuloInAttesa nella richiesta delegata  e' diverso da null 
	 * o se e' stata definita una ricezione di contenuti asincroni.
	 *
	 * @param msg Messaggio SOAP che contiene un messaggio di ok applicativo
	 * @param richiestaDelegata Dati che caratterizzano la richiesta.
	 * 
	 */
	public GestoreMessaggi sendRispostaApplicativaOK(OpenSPCoop2Message msg, RichiestaDelegata richiestaDelegata,
			PortaDelegata pd,ServizioApplicativo sa)throws EJBUtilsException { 
		return sendRispostaApplicativa(msg,richiestaDelegata,null,pd,sa);
	}

	/**
	 * Spedisce un Messaggio di risposta Applicativo  al modulo di openspcoop che gli compete.
	 * La spedizione viene naturalmente effettuata solo se l'idModuloInAttesa nella richiesta delegata  e' diverso da null 
	 * o se e' stata definita una ricezione di contenuti asincroni.
	 *
	 * @param msg Messaggio SOAP che contiene un messaggio di ok applicativo
	 * @param richiestaDelegata Dati che caratterizzano la richiesta.
	 * @param idModuloInAttesa identificativo del modulo 'RicezioneContenutiApplicativiXXX'. 
	 * @param idBustaRisposta Identificativo del messaggio di risposta
	 * @return Messaggio di risposta 
	 * 
	 */
	private GestoreMessaggi sendRispostaApplicativa(OpenSPCoop2Message msg, RichiestaDelegata richiestaDelegata, String idBustaRisposta,
			PortaDelegata pd,ServizioApplicativo sa) throws EJBUtilsException{ 

		String idTransazione = (String) this.pddContext.getObject(org.openspcoop2.core.constants.Costanti.CLUSTER_ID);
		
		StateMessage statoRisposta = (StateMessage) this.openSPCoopState.getStatoRisposta();
		
		GestoreMessaggi msgResponse = null;
		GestoreMessaggi msgSbloccoRicezioneContenutiApplicativi = null; // Evenutuale sblocco se presente ricezioneContenutiApplicativi per profili oneway/sincroni

		String correlazioneApplicativa = null;
		if(richiestaDelegata!=null)
			correlazioneApplicativa = richiestaDelegata.getIdCorrelazioneApplicativa();
		
		String correlazioneApplicativaRisposta = null;
		if(richiestaDelegata!=null)
			correlazioneApplicativaRisposta = richiestaDelegata.getIdCorrelazioneApplicativaRisposta();
		
		String nomePorta = null;
		if(richiestaDelegata!=null)
			nomePorta = richiestaDelegata.getLocationPD();
		
		// Aggiungo costante servizio applicativo
		this.msgDiag.addKeyword(CostantiPdD.KEY_SA_EROGATORE, richiestaDelegata.getServizioApplicativo());
		
		this.setEJBSuffixFunctionName();
		try{

			/* ------- Raccolta dati ------ */
			String idModuloInAttesa = richiestaDelegata.getIdModuloInAttesa();
			boolean existsConsegnaRispostaAsincrona = false;
			try{
				existsConsegnaRispostaAsincrona = this.configurazionePdDReader.existsConsegnaRispostaAsincrona(sa);
			}catch(DriverConfigurazioneNotFound de){}
			boolean consegnaRispostaAsincrona = richiestaDelegata.getUtilizzoConsegnaAsincrona() &&  existsConsegnaRispostaAsincrona;


			/* ------- Gestione MsgRisposta --------------- */
			String idRisposta = idBustaRisposta;
			if( (consegnaRispostaAsincrona) || (idModuloInAttesa != null) ){

				// oraRegistrazione
				Timestamp oraRegistrazioneMessaggio = DateManager.getTimestamp();

				// --- Creo lo stato del messaggio,se sto' spedendo una risposta generata da OpenSPCoop, creando anche un IDResponse)
				if(idRisposta == null){
					// Creo ID Risposta
					
					Imbustamento imbustatore = new Imbustamento(this.protocolFactory);
					idRisposta = 
						imbustatore.buildID(this.openSPCoopState.getStatoRichiesta(),this.identitaPdD, idTransazione, 
								this.propertiesReader.getGestioneSerializableDB_AttesaAttiva(),
								this.propertiesReader.getGestioneSerializableDB_CheckInterval(),
								Boolean.FALSE);
					if(idRisposta == null){
						throw new Exception("Identificativo non costruito.");
					}  
					msgResponse = new GestoreMessaggi(this.openSPCoopState, false , idRisposta,Costanti.INBOX,this.msgDiag,this.pddContext);
					msgResponse.registraMessaggio(msg,oraRegistrazioneMessaggio, saveMessageStateless(pd),correlazioneApplicativa,correlazioneApplicativaRisposta);
				}else{
					// Carico lo stato di un messaggio, esistente
					msgResponse = new GestoreMessaggi(this.openSPCoopState, false ,idRisposta,Costanti.INBOX,this.msgDiag,this.pddContext);
					oraRegistrazioneMessaggio = msgResponse.getOraRegistrazioneMessaggio();
				}

				// --- Aggiorno informazioni sul messaggio
				msgResponse.aggiornaRiferimentoMessaggio(this.idSessione);

				// --- Aggiorno Proprietario
				if( consegnaRispostaAsincrona ){
					// Aggiorno proprietario Messaggio, consegna asincrona: CONSEGNA_BUSTE_SOAP
					msgResponse.aggiornaProprietarioMessaggio(ConsegnaContenutiApplicativi.ID_MODULO);
				}else{
					if(idModuloInAttesa != null){
						// Aggiorno proprietario Messaggio, consegna sincrona: ACCETTAZIONEXXX
						msgResponse.aggiornaProprietarioMessaggio(idModuloInAttesa);
					}
				}

				//	--- Lettura dati di configurazione 
				boolean servizioApplicativoConConnettore = false;
				boolean getMessageAbilitato = false;
				boolean sbustamentoSoap = false;
				boolean sbustamentoInformazioniProtocollo = true;
				try{
					servizioApplicativoConConnettore = this.configurazionePdDReader.consegnaRispostaAsincronaConConnettore(sa);
					getMessageAbilitato = this.configurazionePdDReader.consegnaRispostaAsincronaConGetMessage(sa);
					sbustamentoSoap = this.configurazionePdDReader.consegnaRispostaAsincronaConSbustamento(sa);
					sbustamentoInformazioniProtocollo = this.configurazionePdDReader.consegnaRispostaAsincronaConSbustamentoInformazioniProtocollo(sa);
				}catch(DriverConfigurazioneNotFound de){}

				if( (getMessageAbilitato==false) && (servizioApplicativoConConnettore==false) && (idModuloInAttesa==null)){
					this.msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI,"consegnaNonDefinita");
					return msgResponse; 
				}else if( (servizioApplicativoConConnettore==false) && (idModuloInAttesa==null)){
					this.msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI,"integrationManager.messaggioDisponibile");
				}

				String tipoConsegna = GestoreMessaggi.CONSEGNA_TRAMITE_INTEGRATION_MANAGER;


				// --- Spedizione al successivo modulo 
				try{
					if( consegnaRispostaAsincrona ){
						if(servizioApplicativoConConnettore){
							// spedizione al modulo ConsegnaContenutiApplicativi
							richiestaDelegata.setScenario(Costanti.SCENARIO_CONSEGNA_CONTENUTI_APPLICATIVI);
							sendToConsegnaContenutiApplicativi_rispostaConsegnataModalitaAsincrona(richiestaDelegata,idRisposta, msgResponse);
							tipoConsegna=GestoreMessaggi.CONSEGNA_TRAMITE_CONNETTORE;
						}else{
							this.msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI,"integrationManager.messaggioDisponibile");
						}
					}else{
						if(idModuloInAttesa != null){
							// spedizione al modulo RicezioneContenutiApplicativiXXX
							sendToRicezioneContenutiApplicativi(idModuloInAttesa,idRisposta,
									richiestaDelegata.getIdCollaborazione(),
									richiestaDelegata.getProfiloCollaborazione(),richiestaDelegata.getProfiloCollaborazioneValue());
							tipoConsegna=GestoreMessaggi.CONSEGNA_TRAMITE_CONNECTION_REPLY;
						}
					}
				}catch(Exception e){
					throw e; // rilancio eccezione;
				}

				// --- Registra SIL Destinatario del Messaggio
				msgResponse.registraDestinatarioMessaggio(richiestaDelegata.getServizioApplicativo(),
						sbustamentoSoap,sbustamentoInformazioniProtocollo,getMessageAbilitato,tipoConsegna,oraRegistrazioneMessaggio,
						nomePorta);
			} // end Gestione Risposta






			/* ------- Gestione msgSbloccoRicezioneContenutiApplicativi --------------- */
			if(consegnaRispostaAsincrona && idModuloInAttesa != null){
				//	Creo stato per msgSbloccoRicezioneContenutiApplicativi
				Imbustamento imbustatore = new Imbustamento(this.protocolFactory);
				String idSbloccoRicezioneContenutiApplicativi= 
					imbustatore.buildID(this.openSPCoopState.getStatoRichiesta(),this.identitaPdD, idTransazione, 
							this.propertiesReader.getGestioneSerializableDB_AttesaAttiva(),
							this.propertiesReader.getGestioneSerializableDB_CheckInterval(),
							Boolean.FALSE);
				if(idSbloccoRicezioneContenutiApplicativi == null){
					throw new Exception("Identificativo non costruito.");
				}  
				msgSbloccoRicezioneContenutiApplicativi = new GestoreMessaggi(this.openSPCoopState, false, idSbloccoRicezioneContenutiApplicativi,Costanti.INBOX,this.msgDiag,this.pddContext);
				if(this.protocolManager.isHttpEmptyResponseOneWay())
					msgSbloccoRicezioneContenutiApplicativi.registraMessaggio(org.openspcoop2.message.SoapUtils.build_Soap_Empty(msg.getVersioneSoap()),correlazioneApplicativa,correlazioneApplicativaRisposta);
				else
					msgSbloccoRicezioneContenutiApplicativi.registraMessaggio(this.buildOpenSPCoopOK_soapMsg(msg.getVersioneSoap(),this.idSessione),correlazioneApplicativa,correlazioneApplicativaRisposta);
				msgSbloccoRicezioneContenutiApplicativi.aggiornaRiferimentoMessaggio(this.idSessione);
				//	--- Aggiorno Proprietario
				msgSbloccoRicezioneContenutiApplicativi.aggiornaProprietarioMessaggio(idModuloInAttesa);
				//	--- Spedizione al modulo RicezioneContenutiApplicativiXXX
				sendToRicezioneContenutiApplicativi(idModuloInAttesa,idSbloccoRicezioneContenutiApplicativi,
						richiestaDelegata.getIdCollaborazione(),
						richiestaDelegata.getProfiloCollaborazione(),richiestaDelegata.getProfiloCollaborazioneValue());
			}

			// Ritorno gestore della risposta
			return msgResponse;

		}catch (Exception e) {	
			this.msgDiag.logErroreGenerico(e, "EJBUtils.sendRispostaApplicativa");
			
			if(msgResponse!=null)
				statoRisposta.closePreparedStatement();
			//	msgResponse.closePreparedStatement();

			// Rilancio l'eccezione
			throw new EJBUtilsException("EJBUtils.sendRispostaApplicativa error: "+e.getMessage(),e);
		} finally{
			this.unsetEJBSuffixFunctionName();
		}

	}













	/** ------------------- Metodo per la spedizione al nodo 'RicezioneContenutiApplicativi' ---------------- */

	/**
	 * Spedisce una risposta applicativa  
	 * al modulo di openspcoop RicezioneContenutiApplicativiXXX, 
	 * inserendolo all'interno di  un messaggio di tipo {@link org.openspcoop2.pdd.services.RicezioneContenutiApplicativiMessage} e 
	 * spedendolo nella coda 'toRicezioneContenutiApplicativiXXX'.
	 *
	 * @param idModuloInAttesa identificativo del modulo 'RicezioneContenutiApplicativiXXX'.
	 * @param idRisposta Identificativo del messaggio di risposta
	 * 
	 */
	public void sendToRicezioneContenutiApplicativi(String idModuloInAttesa,String idRisposta, String idCollaborazione, 
			ProfiloDiCollaborazione profiloCollaborazione, String profiloCollaborazioneValue) throws EJBUtilsException{

		if (this.openSPCoopState instanceof OpenSPCoopStateless) {
			RicezioneContenutiApplicativiMessage accettazioneMSG = new RicezioneContenutiApplicativiMessage();
			accettazioneMSG.setIdBustaRisposta(idRisposta);
			accettazioneMSG.setIdCollaborazione(idCollaborazione);
			accettazioneMSG.setProfiloCollaborazione(profiloCollaborazione,profiloCollaborazioneValue);
			accettazioneMSG.setPddContext(this.pddContext);
			((OpenSPCoopStateless)this.openSPCoopState).setMessageLib(accettazioneMSG);
			return;
		}
		
		if( CostantiConfigurazione.COMUNICAZIONE_INFRASTRUTTURALE_JMS.equals(this.propertiesReader.getNodeReceiver())){ 
			try{
				// Creazione RicezioneContenutiApplicativiMessage
				this.msgDiag.highDebug("[EJBUtils] Creazione ObjectMessage for send via JMS.");
				RicezioneContenutiApplicativiMessage accettazioneMSG = new RicezioneContenutiApplicativiMessage();
				accettazioneMSG.setIdBustaRisposta(idRisposta);
				accettazioneMSG.setIdCollaborazione(idCollaborazione);
				accettazioneMSG.setProfiloCollaborazione(profiloCollaborazione, profiloCollaborazioneValue);
				accettazioneMSG.setPddContext(this.pddContext);

				if (this.openSPCoopState instanceof OpenSPCoopStateless)
					((OpenSPCoopStateless)this.openSPCoopState).setMessageLib(accettazioneMSG);

				else { //send modalita Stateful
					// NOTA: usare realmente JMSSender e non l'interfaccia INodeSender, perche' questo punto riguarda INodeReceiver!!
					String idT = PdDContext.getValue(org.openspcoop2.core.constants.Costanti.CLUSTER_ID, this.pddContext);
					JMSSender senderJMS = new JMSSender(this.identitaPdD,this.idModulo,this.log,idT);
					if(senderJMS.send(idModuloInAttesa,
							accettazioneMSG,this.idSessione) == false){
						this.msgDiag.logErroreGenerico(senderJMS.getErrore(), "EJBUtils.sendToRicezioneContenutiApplicativi.senderJMS");
						throw new Exception("[EJBUtils]  SendJMSError: "+senderJMS.getErrore());
					}
					this.msgDiag.highDebug("[EJBUtils]  ObjectMessage send via JMS.");
				}
			} catch (Exception e) {	
				this.msgDiag.logErroreGenerico(e, "EJBUtils.sendToRicezioneContenutiApplicativi");
				throw new EJBUtilsException("EJBUtils.sendToRicezioneContenutiApplicativi error: "+e.getMessage(),e);
			}
		}
	}













	/** ------------------- Metodo per la spedizione al nodo 'ConsegnaContenutiApplicativi' ---------------- */

	/**
	 * Spedisce una risposta applicativa  
	 * al modulo di openspcoop ConsegnaContenutiApplicativi, 
	 * inserendolo all'interno di  un messaggio di tipo {@link org.openspcoop2.pdd.mdb.ConsegnaContenutiApplicativiMessage} e 
	 * spedendolo nella coda 'toConsegnaContenutiApplicativi'.
	 *
	 * @param richiestaDelegata RichiestaDelegata
	 * @param idBustaConsegna Identificatore del messaggio da consegnare
	 * 
	 */
	public void sendToConsegnaContenutiApplicativi_rispostaConsegnataModalitaAsincrona(RichiestaDelegata richiestaDelegata, String idBustaConsegna, GestoreMessaggi gm) throws EJBUtilsException{

		// La richiesta delegata avra' sempre e soltanto un servizio applicativo, quello che ha invocato la porta delegata
		try{

			// Creazione RicezioneContenutiApplicativiMessage
			this.msgDiag.highDebug("[EJBUtils]  Creazione ObjectMessage for send nell'infrastruttura.");
			ConsegnaContenutiApplicativiMessage consegnaMSG = new ConsegnaContenutiApplicativiMessage();
			consegnaMSG.setRichiestaDelegata(richiestaDelegata);
			consegnaMSG.setOneWayVersione11(this.oneWayVersione11);
			consegnaMSG.setStateless(this.portaDiTipoStateless_esclusoOneWay11);
			consegnaMSG.setImplementazionePdDSoggettoMittente(this.implementazionePdDSoggettoMittente);
			consegnaMSG.setImplementazionePdDSoggettoDestinatario(this.implementazionePdDSoggettoDestinatario);
			consegnaMSG.setPddContext(this.pddContext);

			// Costrusce una busta opportuna per la gestione asincrona
			Busta busta = new Busta(this.protocolFactory.getProtocol());
			busta.setTipoMittente(richiestaDelegata.getSoggettoFruitore().getTipo());
			busta.setMittente(richiestaDelegata.getSoggettoFruitore().getNome());
			busta.setIdentificativoPortaMittente(richiestaDelegata.getSoggettoFruitore().getCodicePorta());
			busta.setTipoDestinatario(richiestaDelegata.getIdServizio().getSoggettoErogatore().getTipo());
			busta.setDestinatario(richiestaDelegata.getIdServizio().getSoggettoErogatore().getNome());
			busta.setIdentificativoPortaDestinatario(richiestaDelegata.getIdServizio().getSoggettoErogatore().getCodicePorta());
			busta.setTipoServizio(richiestaDelegata.getIdServizio().getTipoServizio());
			busta.setServizio(richiestaDelegata.getIdServizio().getServizio());
			busta.setVersioneServizio(richiestaDelegata.getIdServizio().getVersioneServizio());
			busta.setAzione(richiestaDelegata.getIdServizio().getAzione());
			busta.setID(idBustaConsegna);
			busta.setRiferimentoMessaggio(this.idSessione);
			
			boolean idCollaborazione = false;
			switch (this.protocolFactory.createProtocolVersionManager(richiestaDelegata.getProfiloGestione()).getCollaborazione(richiestaDelegata.getProfiloCollaborazione())) {
			case ABILITATA:
				idCollaborazione = true;
				break;
			case DISABILITATA:
				idCollaborazione = false;
				break;
			default:
				idCollaborazione = this.propertiesReader.isGestioneElementoCollaborazione(this.implementazionePdDSoggettoDestinatario);
				break;
			}
			
			if( idCollaborazione ){
				busta.setCollaborazione(richiestaDelegata.getIdCollaborazione());
			}
			consegnaMSG.setBusta(busta);

			this.nodeSender.send(consegnaMSG, org.openspcoop2.pdd.mdb.ConsegnaContenutiApplicativi.ID_MODULO, this.msgDiag, 
					this.identitaPdD,this.idModulo, idBustaConsegna, gm);

		} catch (Exception e) {
			this.log.error("Spedizione->ConsegnaContenutiApplicativi(RichiestaDelegata) non riuscita",e);	
			this.msgDiag.logErroreGenerico(e, "EJBUtils.sendToConsegnaContenutiApplicativi(RichiestaDelegata)");
			throw new EJBUtilsException("EJBUtils.sendToConsegnaContenutiApplicativi error: "+e.getMessage(),e);
		}
	}

	/**
	 * Spedisce una risposta applicativa  
	 * al modulo di openspcoop ConsegnaContenutiApplicativi, 
	 * inserendolo all'interno di  un messaggio di tipo {@link org.openspcoop2.pdd.mdb.ConsegnaContenutiApplicativiMessage} e 
	 * spedendolo nella coda 'toConsegnaContenutiApplicativi'.
	 *
	 * @param richiestaDelegata RichiestaDelegata
	 * @param busta Busta che raccoglie i dati dell'invocazione di servizio
	 * @param gestoreMessaggi il Gestore Messaggi utilizzato per la registrazione dei destinatari del messaggio
	 * 
	 */
	public void sendToConsegnaContenutiApplicativi_gestioneMessaggio(RichiestaDelegata richiestaDelegata,Busta busta,
			GestoreMessaggi gestoreMessaggi,
			ServizioApplicativo sa) throws EJBUtilsException{

		try{

			this.msgDiag.addKeyword(CostantiPdD.KEY_SA_EROGATORE, richiestaDelegata.getServizioApplicativo());
			
			String nomePorta = null;
			if(richiestaDelegata!=null)
				nomePorta = richiestaDelegata.getLocationPD();
			
			//	Aggiorno dati di consegna
			this.msgDiag.setServizioApplicativo(richiestaDelegata.getServizioApplicativo());
			this.msgDiag.logCorrelazioneServizioApplicativo();

			gestoreMessaggi.aggiornaRiferimentoMessaggio(busta.getRiferimentoMessaggio());
			boolean servizioApplicativoRicezioneAsincronaConConnettore = this.configurazionePdDReader.consegnaRispostaAsincronaConConnettore(sa);
			boolean getMessageAbilitato = this.configurazionePdDReader.consegnaRispostaAsincronaConGetMessage(sa);
			if( getMessageAbilitato==false && servizioApplicativoRicezioneAsincronaConConnettore==false ){
				throw new EJBUtilsConsegnaException(this.msgDiag,MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI,"consegnaNonDefinita");
			}
			boolean sbustamentoSoap= this.configurazionePdDReader.consegnaRispostaAsincronaConSbustamento(sa);
			boolean sbustamentoInformazioniProtocollo = this.configurazionePdDReader.consegnaRispostaAsincronaConSbustamentoInformazioniProtocollo(sa);
			String tipoConsegna = GestoreMessaggi.CONSEGNA_TRAMITE_INTEGRATION_MANAGER;
			if(servizioApplicativoRicezioneAsincronaConConnettore)
				tipoConsegna = GestoreMessaggi.CONSEGNA_TRAMITE_CONNETTORE;

			// oraRegistrazione
			Timestamp oraRegistrazioneMessaggio = gestoreMessaggi.getOraRegistrazioneMessaggio();

			gestoreMessaggi.registraDestinatarioMessaggio(richiestaDelegata.getServizioApplicativo(),
					sbustamentoSoap,sbustamentoInformazioniProtocollo,getMessageAbilitato,tipoConsegna,oraRegistrazioneMessaggio,
					nomePorta);

			// Aggiungo costante servizio applicativo
			this.msgDiag.addKeyword(CostantiPdD.KEY_SA_EROGATORE, richiestaDelegata.getServizioApplicativo());
			
			if(servizioApplicativoRicezioneAsincronaConConnettore){

				// La richiesta delegata avra' sempre e soltanto un servizio applicativo, quello che ha invocato la porta delegata
				try{

					// Creazione RicezioneContenutiApplicativiMessage
					this.msgDiag.highDebug("[EJBUtils] Creazione ObjectMessage for send nell'infrastruttura.");
					ConsegnaContenutiApplicativiMessage consegnaMSG = new ConsegnaContenutiApplicativiMessage();
					consegnaMSG.setRichiestaDelegata(richiestaDelegata);
					consegnaMSG.setBusta(busta);
					consegnaMSG.setOneWayVersione11(this.oneWayVersione11);
					consegnaMSG.setStateless(this.portaDiTipoStateless_esclusoOneWay11);
					consegnaMSG.setImplementazionePdDSoggettoMittente(this.implementazionePdDSoggettoMittente);
					consegnaMSG.setImplementazionePdDSoggettoDestinatario(this.implementazionePdDSoggettoDestinatario);
					consegnaMSG.setPddContext(this.pddContext);

					if (this.openSPCoopState instanceof OpenSPCoopStateless)
						((OpenSPCoopStateless)this.openSPCoopState).setMessageLib(consegnaMSG);

					else { //send modalita Stateful
						this.nodeSender.send(consegnaMSG, org.openspcoop2.pdd.mdb.ConsegnaContenutiApplicativi.ID_MODULO, this.msgDiag, 
								this.identitaPdD,this.idModulo, busta.getID(), gestoreMessaggi);
					}
				} catch (Exception e) {	
					throw e;
				}
			}
			else{

				// CONNETTORE non presente
				// un connettore deve per forza essere presente se:
				// - profilo asincrono e ricevuta applicativa abilitata
				if(richiestaDelegata.isRicevutaAsincrona() && 
						(ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO.equals(busta.getProfiloDiCollaborazione()) ||
								ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO.equals(busta.getProfiloDiCollaborazione()) ) ){		
					throw new EJBUtilsConsegnaException(this.msgDiag,MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI,"gestioneProfiloAsincrono.servizioNonUtilizzabile");
				}else{
					this.msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI,"integrationManager.messaggioDisponibile");
				}
			}

		} catch (EJBUtilsConsegnaException e) {	
			throw e;
		} catch (Exception e) {
			this.log.error("Spedizione->ConsegnaContenutiApplicativi_gestoreMessaggi(RichiestaDelegata) non riuscita",e);	
			this.msgDiag.logErroreGenerico(e, "EJBUtils.sendToConsegnaContenutiApplicativi_gestoreMessaggi(RichiestaDelegata)");
			throw new EJBUtilsException("EJBUtils.sendToConsegnaContenutiApplicativi error: "+e.getMessage(),e);
		}
	}


	/**
	 * Spedisce una risposta applicativa  
	 * al modulo di openspcoop ConsegnaContenutiApplicativi, 
	 * inserendolo all'interno di  un messaggio di tipo {@link org.openspcoop2.pdd.mdb.ConsegnaContenutiApplicativiMessage} e 
	 * spedendolo nella coda 'toConsegnaContenutiApplicativi'.
	 * Utilizza la definizione dell'invocazione servizio se il tipo di consegna e':
	 * <ul>
	 *  <li>OneWay_InvocazioneServizio
	 *  <li>Sincrono_InvocazioneServizio
	 *  <li>AsincronoSimmetrico_InvocazioneServizio
	 *  <li>AsincronoAsimmetrico_InvocazioneServizio
	 * </ul>
	 * Utilizza invece la definizione della consegna contenuti asincroni, se il tipo e': AsincronoAsimmetrico_Polling
	 * Se il tipo e' diverso da quelli elencati, viene sollevata una eccezione.
	 *
	 * @param richiestaApplicativa RichiestaApplicativa
	 * @param busta Busta che raccoglie i dati dell'invocazione di servizio
	 * @param gestoreMessaggi il Gestore Messaggi utilizzato per la registrazione dei destinatari del messaggio
	 * 
	 */
	private boolean gestioneStatelessConIntegrationManager = false;
	public boolean isGestioneStatelessConIntegrationManager() {
		return this.gestioneStatelessConIntegrationManager;
	}
	private boolean gestioneSolamenteConIntegrationManager = false;
	public boolean isGestioneSolamenteConIntegrationManager() {
		return this.gestioneSolamenteConIntegrationManager;
	}
	public Behaviour sendToConsegnaContenutiApplicativi(RichiestaApplicativa richiestaApplicativa,Busta busta,
			GestoreMessaggi gestoreMessaggi,PortaApplicativa pa,RepositoryBuste repositoryBuste) throws EJBUtilsException{
		return sendToConsegnaContenutiApplicativi(richiestaApplicativa, busta, gestoreMessaggi, pa, repositoryBuste, null);
	}
	public Behaviour sendToConsegnaContenutiApplicativi(RichiestaApplicativa richiestaApplicativa,Busta busta,
			GestoreMessaggi gestoreMessaggi,PortaApplicativa pa,RepositoryBuste repositoryBuste,
			RichiestaDelegata localForwardRichiestaDelegata) throws EJBUtilsException{

		try{
		
			Behaviour behaviour = null;
			
			
			/* ----- Indicazione Stateless ----- */
			boolean stateless = false;
			if(localForwardRichiestaDelegata!=null){
				stateless = this.portaDiTipoStateless_esclusoOneWay11;
			}else{
				stateless = this.configurazionePdDReader.isModalitaStateless(pa, busta.getProfiloDiCollaborazione());
			}
			
			
			
			/* ----- Recupero eventuale processo di behaviour ----- */
			boolean registraNuoviMessaggiViaBehaviour = false;
			@SuppressWarnings("unused")
			int sizeNuoviMessaggiViaBehaviour = -1;
			BehaviourForwardToFilter singleFilterBehaviour = null;
			boolean behaviourResponseTo = false;
			// pa is null nel caso di soggetto virtuale
			if(pa!=null && pa.getBehaviour()!=null && !"".equals(pa.getBehaviour())){
				String tipoBehaviour = ClassNameProperties.getInstance().getBehaviour(pa.getBehaviour());
				if(tipoBehaviour==null){
					throw new Exception("Tipo di behaviour ["+pa.getBehaviour()+"] sconosciuto");
				}
				this.msgDiag.addKeyword(CostantiPdD.KEY_TIPO_BEHAVIOUR, pa.getBehaviour());
				IBehaviour behaviourImpl = (IBehaviour) Loader.getInstance().newInstance(tipoBehaviour);
				gestoreMessaggi.setPortaDiTipoStateless(stateless);
				behaviour = behaviourImpl.behaviour(gestoreMessaggi, busta);
				
				behaviourResponseTo = behaviour!=null && behaviour.isResponseTo();
				
				if(behaviour!=null && behaviour.getForwardTo()!=null){
					if(behaviour.getForwardTo().size()>1){
						registraNuoviMessaggiViaBehaviour = true;
						if(behaviourResponseTo==false){
							throw new Exception("La consegna di messaggi multipli via custom behaviour (tipo:"+pa.getBehaviour()+
									") e' permessa solo se viene abilita anche la funzione 'responseTo'");
						}
						for (int i=0; i<behaviour.getForwardTo().size(); i++) {
							BehaviourForwardTo forwardTo = behaviour.getForwardTo().get(i);
							if(forwardTo.getDescription()==null){
								forwardTo.setDescription("ForwardTo["+i+"]");
							}
							if(forwardTo.getMessage()==null){
								throw new Exception("La consegna di messaggi multipli via custom behaviour (tipo:"+pa.getBehaviour()+
										") richiede che vengano definiti tutti i messaggi da inoltrare. Trovato un elemento ForwardTo ("+
										forwardTo.getDescription()+") che non contiene alcun messaggio");
							}
						}
					}
					else if(behaviour.getForwardTo().size()==1){
						
						BehaviourForwardTo forwardTo = behaviour.getForwardTo().get(0);
						if(forwardTo.getDescription()==null){
							forwardTo.setDescription("ForwardTo");
						}
						
						if(forwardTo.getMessage()!=null){
							if(behaviourResponseTo){
								registraNuoviMessaggiViaBehaviour = true;
							}
							else{
								// e' stata effettuata solamente una trasformazione del messaggio
								// Se siamo stateless non devo fare niente, l'oggetto in memoria contiene gia' la modifica.
								// Altrimenti se stiamo stateful viene comunque applicata l'eliminazione del vecchio messaggio
								// ed il salvataggio del nuovo modificato.
								if(!stateless){
									registraNuoviMessaggiViaBehaviour = true;
								}
							}
						}
						else{							
							singleFilterBehaviour = behaviour.getForwardTo().get(0).getFilter();
						}
					}
				}
				
				if(registraNuoviMessaggiViaBehaviour){
					
					sizeNuoviMessaggiViaBehaviour = behaviour.getForwardTo().size();
						
					// elimino l'attuale messaggio
					if(localForwardRichiestaDelegata!=null){
						
						if(stateless){
							repositoryBuste.eliminaUtilizzoPdDFromInBox(busta.getID(),false);
						}
						else{
							repositoryBuste.eliminaUtilizzoPdDFromInBox(busta.getID(),true);
						}
						
						gestoreMessaggi.aggiornaProprietarioMessaggio(TimerGestoreMessaggi.ID_MODULO);
						
					}
					else{
												
						repositoryBuste.eliminaUtilizzoPdDFromInBox(busta.getID(),true);
					
						// Forzo oneway1.1 per farlo registrare anche se siamo in stateless puro
						boolean originalValue = gestoreMessaggi.isOneWayVersione11();
						gestoreMessaggi.setOneWayVersione11(true);
						gestoreMessaggi.logicDeleteMessage();
						gestoreMessaggi.setOneWayVersione11(originalValue);
						
					}
					
				}
				else{
					// se non devo registrare alcun messaggio, e sono in stateless, ma e' stata configurata una replyTo
					// ritorno errore, poiche' deve essere configurato un messaggio da inoltrare.
					if(stateless && behaviourResponseTo){
						throw new Exception("La definizione dell'elemento 'responseTo', via custom behaviour (tipo:"+pa.getBehaviour()+
								"), in una porta applicativa stateless richiede la definizione almeno di un elemento forwardTo contenente un messaggio da inoltrare.");
					}
				}
				
			}
			
			
			/* ----- Check Tipo ----- */
			if( (Costanti.SCENARIO_ONEWAY_INVOCAZIONE_SERVIZIO.equals(richiestaApplicativa.getScenario()) == false) &&
					(Costanti.SCENARIO_SINCRONO_INVOCAZIONE_SERVIZIO.equals(richiestaApplicativa.getScenario()) == false) && 
					(Costanti.SCENARIO_ASINCRONO_SIMMETRICO_INVOCAZIONE_SERVIZIO.equals(richiestaApplicativa.getScenario()) == false) &&
					(Costanti.SCENARIO_ASINCRONO_ASIMMETRICO_INVOCAZIONE_SERVIZIO.equals(richiestaApplicativa.getScenario()) == false) &&
					(Costanti.SCENARIO_ASINCRONO_ASIMMETRICO_POLLING.equals(richiestaApplicativa.getScenario()) == false)   ){
				throw new Exception("Tipo di consegna sconosciuta");
			}
			

			/* ----- Raccolta servizi applicativi collegati alla porta applicativa ----- */
			String [] serviziApplicativiConfigurazione = null;
			SoggettoVirtuale soggettiRealiMappatiInUnSoggettoVirtuale = null;
			IDSoggetto soggettoDestinatario = richiestaApplicativa.getIDServizio().getSoggettoErogatore();
			if( Costanti.SCENARIO_ASINCRONO_ASIMMETRICO_POLLING.equals(richiestaApplicativa.getScenario())  ){
				// Lettura da ricezione contenuti asincroni
				serviziApplicativiConfigurazione  = this.configurazionePdDReader.getServiziApplicativi(pa);
				if(serviziApplicativiConfigurazione == null){
					throw new EJBUtilsConsegnaException(this.msgDiag,MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI,"servizioApplicativoNonDefinito");
				}
			}else{
				// lettura da invocazione servizio
				if(this.configurazionePdDReader.isSoggettoVirtuale( soggettoDestinatario )){	    
					SoggettoVirtuale soggettiVirtuali = this.configurazionePdDReader.getServiziApplicativi_SoggettiVirtuali(richiestaApplicativa);
					if(soggettiVirtuali == null){
						throw new EJBUtilsConsegnaException("(SoggettoVirtuale) "+this.msgDiag.getMessaggio_replaceKeywords(MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI,"servizioApplicativoNonDefinito"),
								this.msgDiag.getLivello(MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI,"servizioApplicativoNonDefinito"),
								this.msgDiag.getCodice(MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI,"servizioApplicativoNonDefinito"));
					}
					boolean GESTISCI_BEHAVIOUR = true;
					List<String> idServiziApplicativiVirtuali = soggettiVirtuali.getIdServiziApplicativi(GESTISCI_BEHAVIOUR,gestoreMessaggi,busta);
					if(idServiziApplicativiVirtuali.size()>0){
						serviziApplicativiConfigurazione = idServiziApplicativiVirtuali.toArray(new String[1]);
					}
					soggettiRealiMappatiInUnSoggettoVirtuale = soggettiVirtuali;
				}else{
					serviziApplicativiConfigurazione  = this.configurazionePdDReader.getServiziApplicativi(pa);
					if(serviziApplicativiConfigurazione == null){ 
						throw new EJBUtilsConsegnaException(this.msgDiag,MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI,"servizioApplicativoNonDefinito");
					}
				}
			}


			/* ----- Controllo esistenza di almeno un servizio applicativo ----- */
			if( serviziApplicativiConfigurazione==null || serviziApplicativiConfigurazione.length < 1){
				throw new EJBUtilsConsegnaException(this.msgDiag,MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI,"servizioApplicativoNonDefinito");
			}
			List<String> serviziApplicativiAbilitati = new ArrayList<String>();
			List<IDServizioApplicativo> idServiziApplicativiAbilitati = new ArrayList<IDServizioApplicativo>();
			for (int i = 0; i < serviziApplicativiConfigurazione.length; i++) {
				IDServizioApplicativo idSA = new IDServizioApplicativo();
				idSA.setIdSoggettoProprietario(soggettoDestinatario);
				idSA.setNome(serviziApplicativiConfigurazione[i]);
				serviziApplicativiAbilitati.add(serviziApplicativiConfigurazione[i]);
				idServiziApplicativiAbilitati.add(idSA);
			}
			if(singleFilterBehaviour!=null){
				// Dentro qua non si passa se siamo in modalità soggetto virtuale
				// Il behaviuor sul filtro è gestito direttamente dentro il metodo soggettiVirtuali.getIdServiziApplicativi() sopra
				idServiziApplicativiAbilitati = singleFilterBehaviour.aggiornaDestinatariAbilitati(idServiziApplicativiAbilitati);
				serviziApplicativiAbilitati = new ArrayList<String>();
				for (IDServizioApplicativo idServizioApplicativo : idServiziApplicativiAbilitati) {
					serviziApplicativiAbilitati.add(idServizioApplicativo.getNome());
				}
			}
			if( serviziApplicativiAbilitati.size() < 1){
				throw new EJBUtilsConsegnaException(this.msgDiag,MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI,"behaviour.servizioApplicativoNonDefinito");
			}

			

			/* ----- Controllo che servizi applicativi multipli siano collegati solo a profilo OneWay (a meno di abilitare il behaviour personalizzato) ----- */
			// Questo per poter ritornare una risposta
			if(registraNuoviMessaggiViaBehaviour==false || behaviourResponseTo==false){
				if( (Costanti.SCENARIO_ONEWAY_INVOCAZIONE_SERVIZIO.equals(richiestaApplicativa.getScenario()) == false) &&
						(serviziApplicativiAbilitati.size() > 1)){
					throw new EJBUtilsConsegnaException(this.msgDiag,MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI,"gestioneProfiloNonOneway.consegnaVersoNServiziApplicativi");
				}else if( (Costanti.SCENARIO_ONEWAY_INVOCAZIONE_SERVIZIO.equals(richiestaApplicativa.getScenario())) && (this.oneWayVersione11==false) && 
						(this.openSPCoopState instanceof OpenSPCoopStateless) &&
						(serviziApplicativiAbilitati.size() > 1) )  {
					for (String nomeServizioApplicativo : serviziApplicativiAbilitati) {
						ServizioApplicativo sappl = this.configurazionePdDReader.getServizioApplicativo(richiestaApplicativa.getIdPA(), 
								nomeServizioApplicativo);
						boolean servizioApplicativoConConnettore = this.configurazionePdDReader.invocazioneServizioConConnettore(sappl);
						boolean getMessageAbilitato = this.configurazionePdDReader.invocazioneServizioConGetMessage(sappl);
						if(servizioApplicativoConConnettore || (getMessageAbilitato==false)){
							throw new EJBUtilsConsegnaException(this.msgDiag,MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI,"gestioneStateless.consegnaVersoNServiziApplicativi");
						}
					}
				}
			}
			
	

			/* ----- Registro destinatari messaggi e spedizione messaggi ----- */
			if(behaviour==null || !registraNuoviMessaggiViaBehaviour){
				_sendMessageToServiziApplicativi(serviziApplicativiAbilitati, soggettiRealiMappatiInUnSoggettoVirtuale, 
						richiestaApplicativa, localForwardRichiestaDelegata, gestoreMessaggi, busta, pa, repositoryBuste,
						null,null,stateless,this.openSPCoopState);
			}
			else{
													
				List<BehaviourForwardTo> forwardTo = behaviour.getForwardTo();
				for (int i=0; i<forwardTo.size(); i++) {
					
					BehaviourForwardTo behaviourForwardTo = forwardTo.get(i);
					
					// check servizi applicativi
					List<String> serviziApplicativiAbilitatiForwardTo = new ArrayList<String>();
					List<IDServizioApplicativo> idServiziApplicativiAbilitatiForwardTo = new ArrayList<IDServizioApplicativo>();
					serviziApplicativiAbilitatiForwardTo.addAll(serviziApplicativiAbilitati);
					idServiziApplicativiAbilitatiForwardTo.addAll(idServiziApplicativiAbilitati);
					if(behaviourForwardTo.getFilter()!=null){
						idServiziApplicativiAbilitatiForwardTo = behaviourForwardTo.getFilter().aggiornaDestinatariAbilitati(idServiziApplicativiAbilitati);
						serviziApplicativiAbilitatiForwardTo = new ArrayList<String>();
						for (IDServizioApplicativo idServizioApplicativo : idServiziApplicativiAbilitatiForwardTo) {
							serviziApplicativiAbilitatiForwardTo.add(idServizioApplicativo.getNome());
						}
					}	
					if( serviziApplicativiAbilitatiForwardTo.size() < 1){
						throw new EJBUtilsConsegnaException(this.msgDiag,MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI,"behaviour.servizioApplicativoNonDefinito");
					}
					
					String idTransazione = (String) this.pddContext.getObject(org.openspcoop2.core.constants.Costanti.CLUSTER_ID);
					OpenSPCoopStateless stateBehaviour = null;
					try{
						if (stateless && !this.propertiesReader.isServerJ2EE() ) {
							stateBehaviour = new OpenSPCoopStateless();
							stateBehaviour.setUseConnection(true);
							stateBehaviour.setTempiAttraversamentoPDD(((OpenSPCoopStateless)this.openSPCoopState).getTempiAttraversamentoPDD());
							stateBehaviour.setDimensioneMessaggiAttraversamentoPDD(((OpenSPCoopStateless)this.openSPCoopState).getDimensioneMessaggiAttraversamentoPDD());
							stateBehaviour.setIDCorrelazioneApplicativa(((OpenSPCoopStateless)this.openSPCoopState).getIDCorrelazioneApplicativa());
							stateBehaviour.setIDCorrelazioneApplicativaRisposta(((OpenSPCoopStateless)this.openSPCoopState).getIDCorrelazioneApplicativaRisposta());
							stateBehaviour.setPddContext(((OpenSPCoopStateless)this.openSPCoopState).getPddContext());
							stateBehaviour.initResource(this.identitaPdD, "EJBUtils.behaviour_"+pa.getBehaviour(), idTransazione);
						}
					
						// registrazione messaggio
						//  Utilizzo le utility di Memorizzazione Stateless
						Busta bustaNewMessaggio = behaviourForwardTo.getBusta();
						if(bustaNewMessaggio==null){
							bustaNewMessaggio = busta.clone();
							bustaNewMessaggio.setID("Forward"+i+"_"+busta.getID());
						}
						bustaNewMessaggio.setRiferimentoMessaggio(busta.getID()); // per il timer
						bustaNewMessaggio.addProperty(CostantiPdD.KEY_DESCRIZIONE_BEHAVIOUR, behaviourForwardTo.getDescription());
						IOpenSPCoopState stateNuoviMessaggi = this.openSPCoopState;
						if(stateBehaviour!=null){
							stateNuoviMessaggi = stateBehaviour;
						}
						GestoreMessaggi gestoreNewMessaggio = new GestoreMessaggi(stateNuoviMessaggi,true, bustaNewMessaggio.getID(),
								Costanti.INBOX,this.msgDiag,this.pddContext);
						RepositoryBuste repositoryBusteNewMessaggio = new RepositoryBuste(stateNuoviMessaggi.getStatoRichiesta(),true, this.protocolFactory);
						this._forceSaveMessage(gestoreNewMessaggio, null, bustaNewMessaggio, richiestaApplicativa, repositoryBusteNewMessaggio,
								behaviourForwardTo.getMessage(),stateNuoviMessaggi); // per eventuale thread riconsegna in caso di errore
	//					if(stateless){
	//						((OpenSPCoopStateless)this.openSPCoopState).setRichiestaMsg(behaviourForwardTo.getMessage());
	//					}
											
						// Inoltro messaggio
						_sendMessageToServiziApplicativi(serviziApplicativiAbilitatiForwardTo, soggettiRealiMappatiInUnSoggettoVirtuale, 
								richiestaApplicativa, localForwardRichiestaDelegata, gestoreNewMessaggio, bustaNewMessaggio, pa, repositoryBusteNewMessaggio,
								busta.getID(),behaviourForwardTo.getConfig(),stateless,stateNuoviMessaggi);
						
						// Applico modifiche effettuate dal modulo Consegna
						if (stateless && !this.propertiesReader.isServerJ2EE() ) {
							if(stateBehaviour.resourceReleased()){
								// il modulo di consegna rilascia la risorsa
								stateBehaviour.updateResource(idTransazione);
							}
							stateBehaviour.commit();
						}
						
					}finally{
						if (stateless && !this.propertiesReader.isServerJ2EE() ) {
							if(stateBehaviour!=null){
								stateBehaviour.releaseResource();
							}
						}
					}
				}
					
			}

			
			return behaviour;
			
		} catch (EJBUtilsConsegnaException e) {	
			throw e;
		} catch (Exception e) {
			this.log.error("Spedizione->ConsegnaContenutiApplicativi(RichiestaApplicativa) non riuscita",e);
			this.log.error("EJBUtils.sendToConsegnaContenutiApplicativi(RichiestaApplicativa)",e);
			this.msgDiag.logErroreGenerico(e, "EJBUtils.sendToConsegnaContenutiApplicativi(RichiestaApplicativa)");
			throw new EJBUtilsException("EJBUtils.sendToConsegnaContenutiApplicativi error: "+e.getMessage(),e);
		}
	}



	private void _sendMessageToServiziApplicativi(List<String> serviziApplicativi, SoggettoVirtuale soggettiRealiMappatiInUnSoggettoVirtuale,
			RichiestaApplicativa richiestaApplicativa, RichiestaDelegata localForwardRichiestaDelegata,
			GestoreMessaggi gestoreMessaggi, Busta busta, PortaApplicativa pa, RepositoryBuste repositoryBuste,
			String idBustaPreBehaviourNewMessage,BehaviourForwardToConfiguration behaviourForwardToConfiguration,
			boolean stateless,IOpenSPCoopState state) throws Exception{
		
		// Eventuale indicazione per la registrazione via stateless
		boolean registrazioneMessaggioPerStatelessEffettuata = false;
		
		boolean gestioneSolamenteConIntegrationManager = true;
		
		Hashtable<String, Boolean> mapServizioApplicativoConConnettore = new Hashtable<String, Boolean>();
		Hashtable<String, Boolean> mapSbustamentoSoap = new Hashtable<String, Boolean>();
		Hashtable<String, Boolean> mapSbustamentoInformazioniProtocollo = new Hashtable<String, Boolean>();
		Hashtable<String, Boolean> mapGetMessage = new Hashtable<String, Boolean>();
		Hashtable<String, String> mapTipoConsegna = new Hashtable<String, String>();
		Hashtable<String, Timestamp> mapOraRegistrazione = new Hashtable<String, Timestamp>();
		Hashtable<String, ConsegnaContenutiApplicativiMessage> mapConsegnaContenutiApplicativiMessage = new Hashtable<String, ConsegnaContenutiApplicativiMessage>();
		
		String nomePorta = null;
		if(richiestaApplicativa!=null && richiestaApplicativa.getIdPAbyNome()!=null){
			nomePorta = richiestaApplicativa.getIdPAbyNome().getNome();
		}
		
		// Fase di Registrazione
		for (String servizioApplicativo : serviziApplicativi) {

			// effettuo correzioni dovute al Soggetto Virtuale
			if(soggettiRealiMappatiInUnSoggettoVirtuale!=null){
				
				String oldDominio = richiestaApplicativa.getIDServizio().getSoggettoErogatore().getCodicePorta();
				richiestaApplicativa.getIDServizio().setSoggettoErogatore(soggettiRealiMappatiInUnSoggettoVirtuale.getSoggettoReale(servizioApplicativo));
				richiestaApplicativa.getIDServizio().getSoggettoErogatore().setCodicePorta(oldDominio);
				
				richiestaApplicativa.setIdPAbyNome(soggettiRealiMappatiInUnSoggettoVirtuale.getIDPortaApplicativa(servizioApplicativo));
				
				servizioApplicativo = soggettiRealiMappatiInUnSoggettoVirtuale.getNomeServizioApplicativo(servizioApplicativo);
			}
			
			this.msgDiag.highDebug("[EJBUtils] Creazione ObjectMessage for send nell'infrastruttura.");
			ConsegnaContenutiApplicativiMessage consegnaMSG = new ConsegnaContenutiApplicativiMessage();
			consegnaMSG.setBusta(busta);
			consegnaMSG.setOneWayVersione11(this.oneWayVersione11);
			consegnaMSG.setStateless(this.portaDiTipoStateless_esclusoOneWay11);
			consegnaMSG.setImplementazionePdDSoggettoMittente(this.implementazionePdDSoggettoMittente);
			consegnaMSG.setImplementazionePdDSoggettoDestinatario(this.implementazionePdDSoggettoDestinatario);
			consegnaMSG.setPddContext(this.pddContext);
			consegnaMSG.setRichiestaDelegata(localForwardRichiestaDelegata);
			if(idBustaPreBehaviourNewMessage!=null){
				consegnaMSG.setIdMessaggioPreBehaviour(idBustaPreBehaviourNewMessage);
			}
			consegnaMSG.setBehaviourForwardToConfiguration(behaviourForwardToConfiguration);
			
			// Aggiungo costante servizio applicativo
			this.msgDiag.addKeyword(CostantiPdD.KEY_SA_EROGATORE, servizioApplicativo);
			
			// identificativo Porta Applicativa
			richiestaApplicativa.setServizioApplicativo(servizioApplicativo);
			
			ServizioApplicativo sappl = this.configurazionePdDReader.getServizioApplicativo(richiestaApplicativa.getIdPA(), 
					servizioApplicativo);

			this.msgDiag.setServizioApplicativo(servizioApplicativo);
			this.msgDiag.logCorrelazioneServizioApplicativo();

			// Raccolgo dati per la registrazione
			boolean servizioApplicativoConConnettore = true; // la ricezione contenuti asincroni lo deve obbligatoriamente contenere
			boolean getMessageAbilitato = false; // servizio getMessage non utilizzabile per ricezione contenuti asincroni
			boolean sbustamento_soap = false;
			boolean sbustamento_informazioni_protocollo = true;
			if( Costanti.SCENARIO_ASINCRONO_ASIMMETRICO_POLLING.equals(richiestaApplicativa.getScenario())  ){
				// RICEZIONE CONTENUTI ASINCRONI: lettura parametri
				sbustamento_soap = this.configurazionePdDReader.consegnaRispostaAsincronaConSbustamento(sappl);
				sbustamento_informazioni_protocollo = 
					this.configurazionePdDReader.consegnaRispostaAsincronaConSbustamentoInformazioniProtocollo(sappl);
				servizioApplicativoConConnettore = this.configurazionePdDReader.consegnaRispostaAsincronaConConnettore(sappl);
			}else{
				// INVOCAZIONE SERVIZIO: lettura parametri
				servizioApplicativoConConnettore = this.configurazionePdDReader.invocazioneServizioConConnettore(sappl);
				sbustamento_soap = this.configurazionePdDReader.invocazioneServizioConSbustamento(sappl);
				sbustamento_informazioni_protocollo =
					this.configurazionePdDReader.invocazioneServizioConSbustamentoInformazioniProtocollo(sappl);
				getMessageAbilitato = this.configurazionePdDReader.invocazioneServizioConGetMessage(sappl);
			}
			// refresh info di sbustamento in base alla configurazione del behaviour
			if(behaviourForwardToConfiguration!=null){
				if(behaviourForwardToConfiguration.getSbustamentoInformazioniProtocollo()!=null){
					if(StatoFunzionalita.ABILITATA.equals(behaviourForwardToConfiguration.getSbustamentoInformazioniProtocollo())){
						sbustamento_informazioni_protocollo = true;
					}
					else if(StatoFunzionalita.DISABILITATA.equals(behaviourForwardToConfiguration.getSbustamentoInformazioniProtocollo())){
						sbustamento_informazioni_protocollo = false;
					}
				}
				if(behaviourForwardToConfiguration.getSbustamentoSoap()!=null){
					if(StatoFunzionalita.ABILITATA.equals(behaviourForwardToConfiguration.getSbustamentoSoap())){
						sbustamento_soap = true;
					}
					else if(StatoFunzionalita.DISABILITATA.equals(behaviourForwardToConfiguration.getSbustamentoSoap())){
						sbustamento_soap = false;
					}
				}
			}
			if(localForwardRichiestaDelegata!=null){
				// Se vi e' stato un local forward, il protocollo non sussiste.
				sbustamento_informazioni_protocollo = false;
			}
			// check di incoerenza
			if( getMessageAbilitato==false && servizioApplicativoConConnettore==false ){
				throw new EJBUtilsConsegnaException(this.msgDiag,MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI,"consegnaNonDefinita");
			}
			if((state instanceof OpenSPCoopStateless) && (this.oneWayVersione11==false) && getMessageAbilitato && servizioApplicativoConConnettore
					&& (idBustaPreBehaviourNewMessage==null)){
				throw new EJBUtilsConsegnaException(this.msgDiag,MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI,"gestioneStateless.integrationManager");
			}

			// Registrazione dati per la gestione del Messaggio
			String tipoConsegna = GestoreMessaggi.CONSEGNA_TRAMITE_INTEGRATION_MANAGER;
			if(servizioApplicativoConConnettore)
				tipoConsegna = GestoreMessaggi.CONSEGNA_TRAMITE_CONNETTORE;

			
			// oraRegistrazione
			Timestamp oraRegistrazioneMessaggio = gestoreMessaggi.getOraRegistrazioneMessaggio();

			
			// Registro Destinatario
			if(idBustaPreBehaviourNewMessage!=null){
				// Forzo oneway1.1 per farlo registrare anche se siamo in stateless puro
				gestoreMessaggi.setOneWayVersione11(true);
			}
			gestoreMessaggi.registraDestinatarioMessaggio(servizioApplicativo,
					sbustamento_soap,sbustamento_informazioni_protocollo,
					getMessageAbilitato,tipoConsegna,oraRegistrazioneMessaggio, nomePorta);
			
			mapServizioApplicativoConConnettore.put(servizioApplicativo, servizioApplicativoConConnettore);
			mapSbustamentoSoap.put(servizioApplicativo, sbustamento_soap);
			mapSbustamentoInformazioniProtocollo.put(servizioApplicativo, sbustamento_informazioni_protocollo);
			mapGetMessage.put(servizioApplicativo, getMessageAbilitato);
			mapTipoConsegna.put(servizioApplicativo, tipoConsegna);
			mapOraRegistrazione.put(servizioApplicativo, oraRegistrazioneMessaggio);
			mapConsegnaContenutiApplicativiMessage.put(servizioApplicativo, consegnaMSG);
		}
		
		
		// Applico modifiche della registrazione
		boolean registrazioneDestinatarioEffettuataPerViaBehaviour = false;
		if(idBustaPreBehaviourNewMessage!=null){
			if (stateless && !this.propertiesReader.isServerJ2EE() ) {
				state.commit();
				state.releaseResource();
				registrazioneDestinatarioEffettuataPerViaBehaviour = true;
			}
		}
		
		
		// Fase di Consegna
		for (String servizioApplicativo : serviziApplicativi) {
			
			// effettuo correzioni dovute al Soggetto Virtuale
			if(soggettiRealiMappatiInUnSoggettoVirtuale!=null){
				
				String oldDominio = richiestaApplicativa.getIDServizio().getSoggettoErogatore().getCodicePorta();
				richiestaApplicativa.getIDServizio().setSoggettoErogatore(soggettiRealiMappatiInUnSoggettoVirtuale.getSoggettoReale(servizioApplicativo));
				richiestaApplicativa.getIDServizio().getSoggettoErogatore().setCodicePorta(oldDominio);
				
				richiestaApplicativa.setIdPAbyNome(soggettiRealiMappatiInUnSoggettoVirtuale.getIDPortaApplicativa(servizioApplicativo));
				
				servizioApplicativo = soggettiRealiMappatiInUnSoggettoVirtuale.getNomeServizioApplicativo(servizioApplicativo);
			}
			
			boolean servizioApplicativoConConnettore = mapServizioApplicativoConConnettore.get(servizioApplicativo);
			boolean sbustamento_soap = mapSbustamentoSoap.get(servizioApplicativo);
			boolean sbustamento_informazioni_protocollo = mapSbustamentoInformazioniProtocollo.get(servizioApplicativo);
			boolean getMessageAbilitato = mapGetMessage.get(servizioApplicativo);
			String tipoConsegna = mapTipoConsegna.get(servizioApplicativo);
			Timestamp oraRegistrazioneMessaggio = mapOraRegistrazione.get(servizioApplicativo);
			ConsegnaContenutiApplicativiMessage consegnaMSG = mapConsegnaContenutiApplicativiMessage.get(servizioApplicativo);
					
			// Aggiungo costante servizio applicativo
			this.msgDiag.addKeyword(CostantiPdD.KEY_SA_EROGATORE, servizioApplicativo);
			
			// identificativo Porta Applicativa
			richiestaApplicativa.setServizioApplicativo(servizioApplicativo);
			
			this.msgDiag.setServizioApplicativo(servizioApplicativo);
			
			
			// Spedisco al modulo ConsegnaContenutiApplicativi, solo se e' presente una definizione di servizio-applicativo
			if(servizioApplicativoConConnettore){
				
				gestioneSolamenteConIntegrationManager = false;
				
				consegnaMSG.setRichiestaApplicativa(richiestaApplicativa);

				if (!stateless || 
						(this.propertiesReader.isServerJ2EE() && idBustaPreBehaviourNewMessage!=null) 
					) {
					if ( this.oneWayVersione11 ) {
						OpenSPCoopStateless statelessSerializzabile = ((OpenSPCoopStateless) state).rendiSerializzabile();
						consegnaMSG.setOpenspcoopstate(statelessSerializzabile);
					}
			
					try{
						String idSessione = this.idSessione;
						if(idBustaPreBehaviourNewMessage!=null){
							idSessione = busta.getID();
						}
						this.nodeSender.send(consegnaMSG, org.openspcoop2.pdd.mdb.ConsegnaContenutiApplicativi.ID_MODULO, this.msgDiag, 
								this.identitaPdD,this.idModulo, idSessione, gestoreMessaggi);
					} catch (Exception e) {	
						this.msgDiag.logErroreGenerico(e, "EJBUtils.sendToConsegnaContenutiApplicativi(RichiestaApplicativa).senderJMS");
						throw e;
					}
				}

				else {
					((OpenSPCoopStateless)state).setMessageLib(consegnaMSG);
					((OpenSPCoopStateless)state).setDestinatarioRequestMsgLib(ConsegnaContenutiApplicativi.ID_MODULO);
							
					if(idBustaPreBehaviourNewMessage!=null){						
						ConsegnaContenutiApplicativi lib = new ConsegnaContenutiApplicativi(this.log);
						EsitoLib result = lib.onMessage(state);
						this.log.debug("Invocato ConsegnaContenutiApplicativi per ["+busta.getID()+"] con esito: "+result.getStatoInvocazione(),result.getErroreNonGestito());
					}
				}


			}else{
				// CONNETTORE non presente
				// un connettore deve per forza essere presente se:
				// - profilo sincrono
				// - profilo asincrono e ricevuta applicativa abilitata
				// - profilo asincrono asimmetrico richiesta e ricevuta applicativa abilitata
				// - polling asincrono asimmetrico (richiesta-stato)
				if(idBustaPreBehaviourNewMessage==null){
					if(ProfiloDiCollaborazione.SINCRONO.equals(busta.getProfiloDiCollaborazione())){
						throw new EJBUtilsConsegnaException(this.msgDiag,MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI,"trasmissioneSincrona.servizioNonUtilizzabile");
					}
					else if(richiestaApplicativa.isRicevutaAsincrona() && 
							(ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO.equals(busta.getProfiloDiCollaborazione()) ||
									ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO.equals(busta.getProfiloDiCollaborazione()) ) ){		
						throw new EJBUtilsConsegnaException(this.msgDiag,MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI,"gestioneProfiloAsincrono.servizioNonUtilizzabile");
					}else if( Costanti.SCENARIO_ASINCRONO_ASIMMETRICO_POLLING.equals(richiestaApplicativa.getScenario())  ){
						throw new EJBUtilsConsegnaException("(Polling AsincronoAsimmetrico) "+this.msgDiag.getMessaggio_replaceKeywords(MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI,"trasmissioneSincrona.servizioNonUtilizzabile"),
								this.msgDiag.getLivello(MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI,"trasmissioneSincrona.servizioNonUtilizzabile"),
								this.msgDiag.getCodice(MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI,"trasmissioneSincrona.servizioNonUtilizzabile"));
					}
				}
				
				if( !registrazioneDestinatarioEffettuataPerViaBehaviour && (state instanceof OpenSPCoopStateless) && (this.oneWayVersione11==false) ){
					this.gestioneStatelessConIntegrationManager = true;
					if(idBustaPreBehaviourNewMessage==null && registrazioneMessaggioPerStatelessEffettuata==false){
						//  Memorizzazione Stateless
						this._forceSaveMessage(gestoreMessaggi, gestoreMessaggi.getOraRegistrazioneMessaggio(), busta, richiestaApplicativa, repositoryBuste,
								((OpenSPCoopStateless)state).getRichiestaMsg(),state);
						registrazioneMessaggioPerStatelessEffettuata=true;
					}
					// Forzo oneway1.1 per farlo registrare anche se siamo in stateless puro
					gestoreMessaggi.setOneWayVersione11(true);
					gestoreMessaggi.registraDestinatarioMessaggio(servizioApplicativo,
							sbustamento_soap,sbustamento_informazioni_protocollo,
							getMessageAbilitato,tipoConsegna,oraRegistrazioneMessaggio,
							nomePorta);
					gestoreMessaggi.setOneWayVersione11(false);
				}
				this.msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI,"integrationManager.messaggioDisponibile");
				
			}
		}
		
		this.gestioneSolamenteConIntegrationManager = gestioneSolamenteConIntegrationManager;
	}


	private void _forceSaveMessage(GestoreMessaggi gestoreMessaggi, Timestamp oraRegistrazione,
			Busta busta,RichiestaApplicativa richiestaApplicativa,RepositoryBuste repositoryBuste,
			OpenSPCoop2Message message,IOpenSPCoopState state) throws Exception{
		gestoreMessaggi.registraInformazioniMessaggio_statelessEngine(oraRegistrazione, 
				ConsegnaContenutiApplicativi.ID_MODULO,busta.getRiferimentoMessaggio(),
				richiestaApplicativa.getIdCorrelazioneApplicativa(),null);
		gestoreMessaggi.registraMessaggio_statelessEngine(message);
		String key = "INSERT RegistrazioneBustaForHistory"+Costanti.INBOX+"_"+busta.getID();
		if(repositoryBuste.isRegistrataIntoInBox(busta.getID())){
			repositoryBuste.aggiornaBustaIntoInBox(busta,this.propertiesReader.getRepositoryIntervalloScadenzaMessaggi());
			repositoryBuste.impostaUtilizzoPdD(busta.getID(), Costanti.INBOX);
		}
		else if(((StateMessage)state.getStatoRichiesta()).getPreparedStatement().containsKey(key)){
			repositoryBuste.aggiornaBustaIntoInBox(busta,this.propertiesReader.getRepositoryIntervalloScadenzaMessaggi());
			repositoryBuste.impostaUtilizzoPdD(busta.getID(), Costanti.INBOX);
		}else{
			repositoryBuste.registraBustaIntoInBox(busta,this.propertiesReader.getRepositoryIntervalloScadenzaMessaggi());
			if(!(state instanceof OpenSPCoopStateful)){
				if(busta.sizeProperties()>0){
					Hashtable<String, String> bustaProperties = new Hashtable<String, String>();
					String[]propertyNames = busta.getPropertiesNames();
					if(propertyNames!=null){
						for (int i = 0; i < propertyNames.length; i++) {
							String keyP = propertyNames[i];
							String valueP = busta.getProperty(key);
							if(keyP!=null && valueP!=null){
								bustaProperties.put(keyP, valueP);
							}
						}
					}
					repositoryBuste.aggiornaProprietaBustaIntoInBox(bustaProperties, busta.getID());
				}
			}
		}
	}














	/** ------------------- Metodi per la spedizione di risposte con errori ---------------- */

	/**
	 * Spedisce un messaggio ErroreProtocolloProcessamento al modulo di OpenSPCoop piu' indicato, a seconda che
	 * la busta debba essere spedita sulla reply della connessione HTTP 
	 * (nodo RicezioneBuste, la spedizione viene naturalmente effettuata solo se <var>idModuloInAttesa</var> e' diverso da null)
	 * o su di una nuova connessione (nodo InoltroRisposte)
	 * La spedizione viene naturalmente effettuata solo se <var>idModuloInAttesa</var> e' diverso da null.
	 *
	 * @param idModuloInAttesa identificativo del modulo 'RicezioneBusteXXX'. 
	 * @param busta Busta da utilizzare per la costruzione della busta di risposta   
	 * @param errore Errore
	 * 
	 */
	
	public void sendAsRispostaBustaErroreProcessamento(String idModuloInAttesa,Busta busta,
			ErroreIntegrazione errore,String idCorrelazioneApplicativa,
			String servizioApplicativoFruitore,
			Throwable eProcessamento, ParseException parseException)throws EJBUtilsException,ProtocolException{ 
		Eccezione ecc = new Eccezione(ErroriCooperazione.ERRORE_GENERICO_PROCESSAMENTO_MESSAGGIO.getErroreProcessamento(errore.getDescrizione(this.protocolFactory)),
				false,this.idModulo,this.protocolFactory);
		Vector<Eccezione> errs = new Vector<Eccezione>();
		errs.add(ecc);
		sendAsRispostaBustaErroreProcessamento(idModuloInAttesa,busta,errs,idCorrelazioneApplicativa,null,servizioApplicativoFruitore,eProcessamento,parseException);
	}
	public void sendAsRispostaBustaErroreProcessamento(String idModuloInAttesa,Busta busta,
			ErroreIntegrazione errore,
			String idCorrelazioneApplicativa,String idCorrelazioneApplicativaRisposta,
			String servizioApplicativoFruitore,
			Throwable eProcessamento, ParseException parseException)throws EJBUtilsException,ProtocolException{ 
		Eccezione ecc = new Eccezione(ErroriCooperazione.ERRORE_GENERICO_PROCESSAMENTO_MESSAGGIO.getErroreProcessamento(errore.getDescrizione(this.protocolFactory)),false,this.idModulo,this.protocolFactory);
		Vector<Eccezione> errs = new Vector<Eccezione>();
		errs.add(ecc);
		sendAsRispostaBustaErroreProcessamento(idModuloInAttesa,busta,errs,idCorrelazioneApplicativa,idCorrelazioneApplicativaRisposta,servizioApplicativoFruitore,eProcessamento,parseException);
	}


	/**
	 * Spedisce un messaggio ErroreProtocolloProcessamento al modulo di OpenSPCoop piu' indicato, a seconda che
	 * la busta debba essere spedita sulla reply della connessione HTTP 
	 * (nodo RicezioneBuste, la spedizione viene naturalmente effettuata solo se <var>idModuloInAttesa</var> e' diverso da null)
	 * o su di una nuova connessione (nodo InoltroRisposte)
	 * La spedizione viene naturalmente effettuata solo se <var>idModuloInAttesa</var> e' diverso da null.
	 *
	 * @param idModuloInAttesa identificativo del modulo 'RicezioneBusteXXX'. 
	 * @param busta Busta da utilizzare per la costruzione della busta di risposta   
	 * @param errs Eccezioni
	 * 
	 */
	public void sendAsRispostaBustaErroreProcessamento(String idModuloInAttesa,Busta busta,
			Vector<Eccezione> errs,String idCorrelazioneApplicativa,String idCorrelazioneApplicativaRisposta,
			String servizioApplicativoFruitore,
			Throwable eProcessamento, ParseException parseException)throws EJBUtilsException,ProtocolException{ 

		String idTransazione = (String) this.pddContext.getObject(org.openspcoop2.core.constants.Costanti.CLUSTER_ID);
		SOAPVersion versioneSoap = (SOAPVersion) this.pddContext.getObject(org.openspcoop2.core.constants.Costanti.SOAP_VERSION);
		//Costruisco busta Errore 
		Imbustamento imbustatore = new Imbustamento(this.protocolFactory);

		String id_bustaErrore = 
			imbustatore.buildID(this.openSPCoopState.getStatoRichiesta(),this.identitaPdD, idTransazione, 
					this.propertiesReader.getGestioneSerializableDB_AttesaAttiva(),
					this.propertiesReader.getGestioneSerializableDB_CheckInterval(),
					Boolean.FALSE);

		//ErroreProcessamentoProtocollo: Header
		busta = imbustatore.buildMessaggioErroreProtocollo_Processamento(errs,busta,id_bustaErrore,this.propertiesReader.getTipoTempoBusta(this.implementazionePdDSoggettoMittente));

		DettaglioEccezione dettaglioEccezione = null;
		if(this.protocolManager.isGenerazioneDetailsSOAPFaultProtocollo_EccezioneProcessamento()){
			dettaglioEccezione = this.dettaglioBuilder.buildDettaglioEccezioneFromBusta(this.identitaPdD,this.tipoPdD,this.idModulo, this.servizioApplicativoErogatore, busta, eProcessamento);
		}
		
		// Fix Bug 131: eccezione di processamento
		imbustatore.gestioneListaEccezioniMessaggioErroreProtocolloProcessamento(busta);
		// Fix Bug 131
		
		if( !( this.identitaPdD.getNome().equals(busta.getMittente()) && 
				this.identitaPdD.getTipo().equals(busta.getTipoMittente()) ) ){
			// Il mittente della busta che sara' spedita e' il router
			busta.setMittente(this.identitaPdD.getNome());
			busta.setTipoMittente(this.identitaPdD.getTipo());
			busta.setIdentificativoPortaMittente(this.identitaPdD.getCodicePorta());
			busta.setIndirizzoMittente(null);
		}

		//ErroreProcessamentoProtocollo: error Msg
		OpenSPCoop2Message errorMsg = 
			imbustatore.buildSoapMsgErroreProtocollo_Processamento(dettaglioEccezione, versioneSoap, this.propertiesReader.isForceSoapPrefixCompatibilitaOpenSPCoopV1());
		if(errorMsg == null){
			throw new EJBUtilsException("EJBUtils.sendRispostaErroreProcessamentoProtocollo error: Costruzione Msg Errore Protocollo fallita.");
		}
		if(parseException!=null){
			errorMsg.setParseException(parseException);
		}
		sendAsRispostaBustaErrore(idModuloInAttesa,busta,errorMsg,false,idCorrelazioneApplicativa,idCorrelazioneApplicativaRisposta,servizioApplicativoFruitore);
	}

	/**
	 * Spedisce un messaggio ErroreProtocolloValidazione al modulo di OpenSPCoop piu' indicato, a seconda che
	 * la busta debba essere spedita sulla reply della connessione HTTP 
	 * (nodo RicezioneBuste, la spedizione viene naturalmente effettuata solo se <var>idModuloInAttesa</var> e' diverso da null)
	 * o su di una nuova connessione (nodo InoltroRisposte)
	 * La spedizione viene naturalmente effettuata solo se <var>idModuloInAttesa</var> e' diverso da null.
	 *
	 * @param idModuloInAttesa identificativo del modulo 'RicezioneBusteXXX'. 
	 * @param busta Busta da utilizzare per la costruzione della busta di risposta
	 * @param eccezione Eccezione di validazione
	 * 
	 */
	public void sendAsRispostaBustaErroreValidazione(String idModuloInAttesa,Busta busta,Eccezione eccezione,
			String idCorrelazioneApplicativa,String servizioApplicativoFruitore)throws EJBUtilsException,ProtocolException{ 
		Vector<Eccezione> v = new Vector<Eccezione>();
		v.add(eccezione);
		sendAsRispostaBustaErroreValidazione(idModuloInAttesa,busta,v,idCorrelazioneApplicativa,null,servizioApplicativoFruitore);
	}
	public void sendAsRispostaBustaErroreValidazione(String idModuloInAttesa,Busta busta,Eccezione eccezione,
			String idCorrelazioneApplicativa,String idCorrelazioneApplicativaRisposta,String servizioApplicativoFruitore)throws EJBUtilsException,ProtocolException{ 
		Vector<Eccezione> v = new Vector<Eccezione>();
		v.add(eccezione);
		sendAsRispostaBustaErroreValidazione(idModuloInAttesa,busta,v,idCorrelazioneApplicativa,idCorrelazioneApplicativaRisposta,servizioApplicativoFruitore);
	}


	/**
	 * Spedisce un messaggio ErroreProtocolloValidazione al modulo di OpenSPCoop piu' indicato, a seconda che
	 * la busta debba essere spedita sulla reply della connessione HTTP 
	 * (nodo RicezioneBuste, la spedizione viene naturalmente effettuata solo se <var>idModuloInAttesa</var> e' diverso da null)
	 * o su di una nuova connessione (nodo InoltroRisposte)
	 * La spedizione viene naturalmente effettuata solo se <var>idModuloInAttesa</var> e' diverso da null.
	 *
	 * @param idModuloInAttesa identificativo del modulo 'RicezioneBusteXXX'. 
	 * @param busta Busta da utilizzare per la costruzione della busta di risposta
	 * @param eccezioni Eccezioni di validazione
	 * 
	 */
	public void sendAsRispostaBustaErroreValidazione(String idModuloInAttesa,Busta busta,Vector<Eccezione> eccezioni,
			String idCorrelazioneApplicativa,String idCorrelazioneApplicativaRisposta,String servizioApplicativoFruitore)throws EJBUtilsException,ProtocolException{ 

		//Costruisco busta Errore 
		SOAPVersion versioneSoap = (SOAPVersion) this.pddContext.getObject(org.openspcoop2.core.constants.Costanti.SOAP_VERSION);
		Imbustamento imbustatore = new Imbustamento(this.protocolFactory);
		String id_bustaErrore = 
			imbustatore.buildID(this.openSPCoopState.getStatoRichiesta(),this.identitaPdD, 
					(String) this.pddContext.getObject(org.openspcoop2.core.constants.Costanti.CLUSTER_ID), 
					this.propertiesReader.getGestioneSerializableDB_AttesaAttiva(),
					this.propertiesReader.getGestioneSerializableDB_CheckInterval(),
					Boolean.FALSE);

		//ErroreValidazioneProtocollo: Header
		busta = imbustatore.buildMessaggioErroreProtocollo_Validazione(eccezioni,busta,id_bustaErrore,this.propertiesReader.getTipoTempoBusta(this.implementazionePdDSoggettoMittente));	
		if( !( this.identitaPdD.getNome().equals(busta.getMittente()) && 
				this.identitaPdD.getTipo().equals(busta.getTipoMittente()) ) ){
			// Il mittente della busta che sara' spedita e' il router
			busta.setMittente(this.identitaPdD.getNome());
			busta.setTipoMittente(this.identitaPdD.getTipo());
			busta.setIdentificativoPortaMittente(this.identitaPdD.getCodicePorta());
			busta.setIndirizzoMittente(null);
		}

		//ErroreValidazioneProtocollo: Msg
		OpenSPCoop2Message msg = 
			imbustatore.buildSoapMsgErroreProtocollo_Validazione(versioneSoap, this.propertiesReader.isForceSoapPrefixCompatibilitaOpenSPCoopV1());
		
		if(msg == null){
			throw new EJBUtilsException("EJBUtils.sendRispostaErroreValidazioneProtocollo error: Costruzione messaggio Errore Protocollo fallita.");
		}

		sendAsRispostaBustaErrore(idModuloInAttesa,busta,msg,false,idCorrelazioneApplicativa,idCorrelazioneApplicativaRisposta,servizioApplicativoFruitore);
	}

	/**
	 * Spedisce una busta che segnala la ricezione di una busta malformata al mittente della busta
	 *
	 * @param busta Busta
	 * @param eccezioni Eccezioni di validazione 
	 * 
	 */
	public void sendAsRispostaBustaErrore_inoltroSegnalazioneErrore(Busta busta,Vector<Eccezione> eccezioni)throws EJBUtilsException,ProtocolException{ 
		
		String idTransazione = (String) this.pddContext.getObject(org.openspcoop2.core.constants.Costanti.CLUSTER_ID);
		SOAPVersion versioneSoap = (SOAPVersion) this.pddContext.getObject(org.openspcoop2.core.constants.Costanti.SOAP_VERSION);
		//Costruisco busta Errore 
		Imbustamento imbustatore = new Imbustamento(this.protocolFactory);
		String id_bustaErrore = 
			imbustatore.buildID(this.openSPCoopState.getStatoRichiesta(),this.identitaPdD, idTransazione, 
					this.propertiesReader.getGestioneSerializableDB_AttesaAttiva(),
					this.propertiesReader.getGestioneSerializableDB_CheckInterval(),
					Boolean.FALSE);

		//ErroreValidazioneProtocollo: Header
		// L'inoltro di segnalazione avviene in SbustamentoRisposte quindi devo riferire l'implemenazione della PdD del Soggetto Destinatario
		busta = imbustatore.buildMessaggioErroreProtocollo_Validazione(eccezioni,busta,id_bustaErrore,this.propertiesReader.getTipoTempoBusta(this.implementazionePdDSoggettoDestinatario));	

		//ErroreValidazioneProtocollo: Msg
		OpenSPCoop2Message msg = 
			imbustatore.buildSoapMsgErroreProtocollo_Validazione(versioneSoap, this.propertiesReader.isForceSoapPrefixCompatibilitaOpenSPCoopV1());
		if(msg == null){
			throw new EJBUtilsException("EJBUtils.sendRispostaErroreProtocollo_BustaRispostaMalformata error: Costruzione Msg Errore Protocollo fallita.");
		}

		sendAsRispostaBustaErrore(null,busta,msg,true,null,null,null);
	}

	/**
	 * Spedisce una busta di Risposta al modulo di OpenSPCoop piu' indicato, a seconda che
	 * la busta debba essere spedita sulla reply della connessione HTTP 
	 * (nodo RicezioneBuste, la spedizione viene naturalmente effettuata solo se <var>idModuloInAttesa</var> e' diverso da null)
	 * o su di una nuova connessione (nodo InoltroRisposte)
	 * La spedizione viene naturalmente effettuata solo se <var>idModuloInAttesa</var> e' diverso da null.
	 *
	 * @param idModuloInAttesa identificativo del modulo 'RicezioneBusteXXX'. 
	 * @param busta Busta 
	 * @param msg Msg Applicativo (SOAP) da inserire nella busta di risposta.
	 * @param segnalazioneBustaRispostaMalformata Indicazione se la busta inviata segnala la precedente ricezione di una busta
	 *        di risposta malformata (in tal caso la richiesta non viene eliminata, non esistentone una)
	 * 
	 */
	private void sendAsRispostaBustaErrore(String idModuloInAttesa,Busta busta,OpenSPCoop2Message msg,
			boolean segnalazioneBustaRispostaMalformata,String idCorrelazioneApplicativa,String idCorrelazioneApplicativaRisposta,String servizioApplicativoFruitore)throws EJBUtilsException{ 

		StateMessage statoRichiesta = (StateMessage) this.openSPCoopState.getStatoRichiesta();
		StateMessage statoRisposta = (StateMessage) this.openSPCoopState.getStatoRisposta();		


		RollbackRepositoryBuste rollbackBuste = null;
		RepositoryBuste repositoryBuste = null;
		GestoreMessaggi msgRequest = null;
		GestoreMessaggi msgResponse = null;
		this.setEJBSuffixFunctionName();
		try{
			
						
			
			/* ------- Gestione MsgRichiesta --------------- */
			boolean addIntoCache = false;
			if(this.rollbackRichiestaInCasoErrore){
				// RollbackApplicativo
				// (uso l'ID della sessione, poiche' tutti i dati 'protocollo' sono salvati con l'ID della richiesta e non della risposta)
				rollbackBuste = new RollbackRepositoryBuste(this.idSessione, statoRichiesta, this.oneWayVersione11);
				if(this.routing){
					rollbackBuste.rollbackBustaIntoOutBox(this.rollbackRichiestaInCasoErrore_rollbackHistory);
				}else{
					rollbackBuste.rollbackBustaIntoInBox(this.rollbackRichiestaInCasoErrore_rollbackHistory);
				}
		
				// Imposto ProprietarioMessaggioRichiesta per eliminazione, se il messaggio da spedire esiste.
				if( (segnalazioneBustaRispostaMalformata==false) && (this.idMessage!=null)){
					msgRequest = new GestoreMessaggi(this.openSPCoopState, true, this.idMessage,this.tipo,this.msgDiag,this.pddContext);
					msgRequest.setOneWayVersione11(this.oneWayVersione11);
					if(msgRequest.existsMessage_noCache()){
						msgRequest.aggiornaProprietarioMessaggio(TimerGestoreMessaggi.ID_MODULO);
						addIntoCache = true;
					}else{
						addIntoCache = false;
					}
				}
			}else{
				msgRequest = new GestoreMessaggi(this.openSPCoopState, true, this.idMessage,this.tipo,this.msgDiag,this.pddContext);
				addIntoCache = false;
			}
			


			/* ------- Gestione MsgRisposta --------------- */

			// Esamina tipo di risposta (indirizzo di spedizione)
			boolean httpReply = true;
			if( this.replyOnNewConnection )
				httpReply = false;
			if( busta.getIndirizzoDestinatario()!=null &&
					this.utilizzoIndirizzoTelematico)
				httpReply = false;
			if( segnalazioneBustaRispostaMalformata )
				httpReply = false;

			// Devo generare una risposta solo se devo inviarla verso una nuova connessione,
			// o se esiste un nodo a cui inviare la risposta (idModuloInAttesa)
			if( (httpReply==false) || (idModuloInAttesa != null) ){

				boolean salvaSuDatabasePerStateless = false;
				if(httpReply==false){
					salvaSuDatabasePerStateless = true; // Il messaggio inviato su una nuova connessione è come fosse una richiesta
				}
				
				// Salvataggio busta da inviare
				repositoryBuste = new RepositoryBuste(statoRisposta, salvaSuDatabasePerStateless, this.protocolFactory);
				repositoryBuste.registraBustaIntoOutBox(busta, this.propertiesReader.getRepositoryIntervalloScadenzaMessaggi()); 
				Integrazione infoIntegrazione = new Integrazione();
				infoIntegrazione.setIdModuloInAttesa(idModuloInAttesa);
				infoIntegrazione.setScenario(this.scenarioCooperazione);
				repositoryBuste.aggiornaInfoIntegrazioneIntoOutBox(busta.getID(),infoIntegrazione);


				// --- Genero una risposta 
				msgResponse = new GestoreMessaggi(this.openSPCoopState, salvaSuDatabasePerStateless ,busta.getID(),Costanti.OUTBOX,this.msgDiag,this.pddContext);
				if(httpReply==false){
					msgResponse.setOneWayVersione11(this.oneWayVersione11);
				}
				// --- creazione nuovo stato
				if(this.openSPCoopState instanceof OpenSPCoopStateless){
					msgResponse.registraMessaggio(msg,((OpenSPCoopStateless)this.openSPCoopState).getTempiAttraversamentoPDD().getRicezioneMsgIngresso(),idCorrelazioneApplicativa,idCorrelazioneApplicativaRisposta);
				}else{
					msgResponse.registraMessaggio(msg,idCorrelazioneApplicativa,idCorrelazioneApplicativaRisposta);
				}
				// ---- aggiorno riferimentoMessaggio
				msgResponse.aggiornaRiferimentoMessaggio(this.idSessione);
				
				// --- Aggiornamento Proprietario messaggio: aggiorna il destinatario del messageLib se siamo in stateless mode
				if(httpReply==false){
					// Aggiorno proprietario Messaggio: INOLTRO RISPOSTE	
					msgResponse.aggiornaProprietarioMessaggio(InoltroRisposte.ID_MODULO);  

				}else{
					if(idModuloInAttesa != null){		   
						// Aggiorno proprietario Messaggio: RICEZIONE BUSTE 
						msgResponse.aggiornaProprietarioMessaggio(idModuloInAttesa);
					}
				}

				// --- Spedizione al successivo modulo
				try{
					if(httpReply==false){
						// spedizione al modulo InoltroRisposte
						sendToInoltroRisposte(busta,segnalazioneBustaRispostaMalformata,null, msgResponse,idCorrelazioneApplicativa,idCorrelazioneApplicativaRisposta,servizioApplicativoFruitore,true);		
					}else{
						if(idModuloInAttesa != null){
							// spedizione al modulo RicezioneBusteXXX
							sendBustaToRicezioneBuste(idModuloInAttesa,busta,null);
						}
					}
				}catch(Exception e){
					throw e; // rilancio eccezione;
				}

			}// end Gestione Risposta


			/* ------- Commit/Close Connessione DB --------------- */
			this.openSPCoopState.commit();


			// Aggiornamento cache messaggio
			if(msgResponse !=null)
				msgResponse.addMessaggiIntoCache_readFromTable(this.idModulo, "sendRispostaErroreProtocollo [risposta]");

			//	Aggiornamento cache proprietario messaggio
			if(addIntoCache){
				String idRichiestaCorrelata = null;
				if(this.idMessage.equals(this.idSessione)==false)
					idRichiestaCorrelata = this.idSessione;
				msgRequest.addProprietariIntoCache_readFromTable(this.idModulo, "sendRispostaErroreProtocollo [richiesta]",idRichiestaCorrelata,this.functionAsRouter);

			}
			if(msgResponse!=null)
				msgResponse.addProprietariIntoCache_readFromTable(this.idModulo, "sendRispostaErroreProtocollo [risposta]",this.idSessione,this.functionAsRouter);

		}catch (Exception e) {	
			this.msgDiag.logErroreGenerico(e, "EJBUtils.sendRispostaErroreProtocollo");
			statoRichiesta.closePreparedStatement();
			statoRisposta.closePreparedStatement();
			// Rilancio l'eccezione
			throw new EJBUtilsException("EJBUtils.sendRispostaErroreProtocollo error: "+e.getMessage(),e);

		}  	finally{
			this.unsetEJBSuffixFunctionName();
		}

	}

	
	
	
	
	
	
	
	
	
	
	
	

	/**
	 * Spedisce un Messaggio di Sblocco 'Risposta'
	 * al modulo di openspcoop 'RicezioneBusteXXX', 
	 * inserendolo all'interno di  un
	 * messaggio di tipo {@link org.openspcoop2.pdd.services.RicezioneBusteMessage} e 
	 * spedendolo nella coda 'toRicezioneBusteXXX'.
	 * La spedizione viene naturalmente effettuata solo se <var>idModuloInAttesa</var> e' diverso da null.
	 * 
	 * @param idModuloInAttesa identificativo del modulo 'RicezioneBusteXXX'. 
	 * 
	 */
	public GestoreMessaggi sendSbloccoRicezioneBuste(String idModuloInAttesa)throws EJBUtilsException,ProtocolException{
		
		String idTransazione = (String) this.pddContext.getObject(org.openspcoop2.core.constants.Costanti.CLUSTER_ID);
		SOAPVersion versioneSoap = (SOAPVersion) this.pddContext.getObject(org.openspcoop2.core.constants.Costanti.SOAP_VERSION);
		// ID Busta Sblocco
		org.openspcoop2.protocol.engine.builder.Imbustamento imbustatore = new Imbustamento(this.protocolFactory);
		String idSblocco =	
			imbustatore.buildID(this.openSPCoopState.getStatoRichiesta(),this.identitaPdD, idTransazione, 
					this.propertiesReader.getGestioneSerializableDB_AttesaAttiva(),
					this.propertiesReader.getGestioneSerializableDB_CheckInterval(),
					Boolean.FALSE);
		return sendBustaRisposta(idModuloInAttesa,null,SoapUtils.build_Soap_Empty(versioneSoap),idSblocco,null,null,null,null);
	}
	
	public GestoreMessaggi sendSOAPFault(String idModuloInAttesa,OpenSPCoop2Message msg)throws EJBUtilsException,ProtocolException{
		
		String idTransazione = (String) this.pddContext.getObject(org.openspcoop2.core.constants.Costanti.CLUSTER_ID);
		
		// ID Busta Sblocco
		org.openspcoop2.protocol.engine.builder.Imbustamento imbustatore = new Imbustamento(this.protocolFactory);
		String idSblocco = 
			imbustatore.buildID(this.openSPCoopState.getStatoRichiesta(),this.identitaPdD, idTransazione, 
					this.propertiesReader.getGestioneSerializableDB_AttesaAttiva(),
					this.propertiesReader.getGestioneSerializableDB_CheckInterval(),
					Boolean.FALSE);

		return sendBustaRisposta(idModuloInAttesa,null,msg,idSblocco,null,null,null,null);
	}

	/**
	 * Spedisce una busta di Risposta al modulo di OpenSPCoop piu' indicato, a seconda che
	 * la busta debba essere spedita sulla reply della connessione HTTP 
	 * (nodo RicezioneBuste, la spedizione viene naturalmente effettuata solo se <var>idModuloInAttesa</var> e' diverso da null)
	 * o su di una nuova connessione (nodo InoltroRisposte), nel caso di indirizzo telematico o nuova risposta
	 * La spedizione viene naturalmente effettuata solo se <var>idModuloInAttesa</var> e' diverso da null.
	 *
	 * @param idModuloInAttesa identificativo del modulo 'RicezioneBusteXXX'. 
	 * @param busta Busta he definisce la busta di risposta
	 * @param msg Msg Applicativo (SOAP) da inserire nella busta di risposta.
	 * 
	 */
	public GestoreMessaggi buildAndSendBustaRisposta(String idModuloInAttesa,Busta busta,
			OpenSPCoop2Message msg,String profiloGestione,
			String idCorrelazioneApplicativa,String servizioApplicativoFruitore)throws EJBUtilsException,ProtocolException{
		return buildAndSendBustaRisposta(idModuloInAttesa, busta, msg, profiloGestione, idCorrelazioneApplicativa, null, servizioApplicativoFruitore);
	}
	public GestoreMessaggi buildAndSendBustaRisposta(String idModuloInAttesa,Busta busta,
			OpenSPCoop2Message msg,String profiloGestione,
			String idCorrelazioneApplicativa,String idCorrelazioneApplicativaRisposta,String servizioApplicativoFruitore)throws EJBUtilsException,ProtocolException{

		String idTransazione = (String) this.pddContext.getObject(org.openspcoop2.core.constants.Costanti.CLUSTER_ID);
		
		// ID Busta Costruita
		org.openspcoop2.protocol.engine.builder.Imbustamento imbustatore = new Imbustamento(this.protocolFactory);
		String id_busta = 
			imbustatore.buildID(this.openSPCoopState.getStatoRichiesta(),this.identitaPdD, idTransazione, 
					this.propertiesReader.getGestioneSerializableDB_AttesaAttiva(),
					this.propertiesReader.getGestioneSerializableDB_CheckInterval(),
					Boolean.FALSE);

		// Aggiungo ID
		if(busta!=null)
			busta.setID(id_busta);

		return sendBustaRisposta(idModuloInAttesa,busta,msg,null,profiloGestione,idCorrelazioneApplicativa,idCorrelazioneApplicativaRisposta,servizioApplicativoFruitore);

	}

	/**
	 * Spedisce una busta di Risposta al modulo di OpenSPCoop piu'indicato, a seconda che
	 * la busta debba essere spedita sulla reply della connessione HTTP 
	 * (nodo RicezioneBuste, la spedizione viene naturalmente effettuata solo se <var>idModuloInAttesa</var> e' diverso da null)
	 * o su di una nuova connessione (nodo InoltroRisposte)
	 * La spedizione viene naturalmente effettuata solo se <var>idModuloInAttesa</var> e' diverso da null.
	 *
	 * @param idModuloInAttesa identificativo del modulo 'RicezioneBusteXXX'. 
	 * @param busta Busta  
	 * @return Pstmt da eseguire per la gestione della risposta
	 * 
	 */
	public GestoreMessaggi sendBustaRisposta(String idModuloInAttesa,
			Busta busta,String profiloGestione,String idCorrelazioneApplicativa,String idCorrelazioneApplicativaRisposta,String servizioApplicativoFruitore)throws EJBUtilsException{
		return sendBustaRisposta(idModuloInAttesa,busta,null,null,profiloGestione,idCorrelazioneApplicativa,idCorrelazioneApplicativaRisposta,servizioApplicativoFruitore);
	}

	/**
	 * Spedisce una busta di Risposta al modulo di OpenSPCoop piu' indicato, a seconda che
	 * la busta debba essere spedita sulla reply della connessione HTTP 
	 * (nodo RicezioneBuste, la spedizione viene naturalmente effettuata solo se <var>idModuloInAttesa</var> e' diverso da null)
	 * o su di una nuova connessione (nodo InoltroRisposte)
	 * La spedizione viene naturalmente effettuata solo se <var>idModuloInAttesa</var> e' diverso da null.
	 *
	 * @param idModuloInAttesa identificativo del modulo 'RicezioneBusteXXX'. 
	 * @param busta Busta 
	 * @param msg Msg Applicativo (SOAP) da inserire nella busta di risposta.
	 * @param repository Repository dove effettuare il salvataggio della busta	 
	 * @param idSbloccoModulo Identificativo di sblocco del modulo RicezioneBuste
	 * @return Pstmt da eseguire per la gestione della risposta
	 * 
	 */
	private GestoreMessaggi sendBustaRisposta(String idModuloInAttesa,
			Busta busta,OpenSPCoop2Message msg,String idSbloccoModulo,
			String profiloGestione,String idCorrelazioneApplicativa,String idCorrelazioneApplicativaRisposta,String servizioApplicativoFruitore)throws EJBUtilsException{ 

		IState statoRisposta = this.openSPCoopState.getStatoRisposta();	

		GestoreMessaggi msgResponse = null;
		this.setEJBSuffixFunctionName();
		try{
			
			/* ------- Gestione MsgRisposta --------------- */

			boolean httpReply = true;
			// Esamina tipo di risposta (indirizzo di spedizione) a meno di messaggi
			// di sblocco modulo.
			if(idSbloccoModulo==null){
				if( this.replyOnNewConnection )
					httpReply = false;
				if(busta!=null){
					if( busta.getIndirizzoDestinatario()!=null &&
							this.utilizzoIndirizzoTelematico){
						httpReply = false;		
					}
				}
			}

			// Devo generare una risposta solo se devo inviarla verso una nuova connessione,
			// o se esiste un nodo a cui inviare la risposta (idModuloInAttesa)
			if( (httpReply==false) || (idModuloInAttesa != null) ){

				boolean salvaSuDatabasePerStateless = false;
				if(httpReply==false && (this.functionAsRouter==false) && (this.openSPCoopState instanceof OpenSPCoopStateless)){
					salvaSuDatabasePerStateless = true; // Il messaggio inviato su una nuova connessione è come fosse una richiesta
				}
				
				// Salvataggio busta da inviare
				if(busta!=null){
					RepositoryBuste repositoryRisposta = new RepositoryBuste(statoRisposta, this.log, salvaSuDatabasePerStateless, this.protocolFactory);
					repositoryRisposta.registraBustaIntoOutBox(busta, this.propertiesReader.getRepositoryIntervalloScadenzaMessaggi());
					Integrazione infoIntegrazione = new Integrazione();
					infoIntegrazione.setIdModuloInAttesa(idModuloInAttesa);
					infoIntegrazione.setScenario(this.scenarioCooperazione);
					repositoryRisposta.aggiornaInfoIntegrazioneIntoOutBox(busta.getID(),infoIntegrazione);
				}

				// --- Se devo generare una risposta 
				// e mi e' chiesto di gestire un messaggio di risposta con errore (msg!=null), lo effettuo.
				String idRisposta = null;
				if(busta!=null)
					idRisposta = busta.getID();
				else
					idRisposta = idSbloccoModulo;
				msgResponse = new GestoreMessaggi(this.openSPCoopState, salvaSuDatabasePerStateless, idRisposta,Costanti.OUTBOX,this.msgDiag, this.pddContext);
				if(httpReply==false){
					msgResponse.setOneWayVersione11(this.oneWayVersione11);
				}
				if( msg!=null ){	 
					// creazione nuovo stato
					if(this.openSPCoopState instanceof OpenSPCoopStateless){
						msgResponse.registraMessaggio(msg,((OpenSPCoopStateless)this.openSPCoopState).getTempiAttraversamentoPDD().getRicezioneMsgIngresso(),idCorrelazioneApplicativa,idCorrelazioneApplicativaRisposta);
					}else{
						msgResponse.registraMessaggio(msg,idCorrelazioneApplicativa,idCorrelazioneApplicativaRisposta);
					}
				}
				// else{ Carico lo stato di un messaggio, esistente


				//---	aggiorno riferimentoMsg
				msgResponse.aggiornaRiferimentoMessaggio(this.idSessione);

				// --- Aggiornamento Proprietario messaggio
				if(httpReply==false){
					// Aggiorno proprietario Messaggio: INOLTRO RISPOSTE	
					msgResponse.aggiornaProprietarioMessaggio(InoltroRisposte.ID_MODULO);
				}else{
					if(idModuloInAttesa != null){		   
						// Aggiorno proprietario Messaggio: RICEZIONE BUSTE
						msgResponse.aggiornaProprietarioMessaggio(idModuloInAttesa);
					}
				}

				// --- Spedizione al successivo modulo
				try{
					if(httpReply==false){
						// spedizione al modulo InoltroRisposte
						sendToInoltroRisposte(busta,false,profiloGestione, msgResponse,idCorrelazioneApplicativa,idCorrelazioneApplicativaRisposta,servizioApplicativoFruitore,true);	
					}else{
						if(idModuloInAttesa != null){
							// spedizione al modulo RicezioneBusteXXX
							sendBustaToRicezioneBuste(idModuloInAttesa,busta,idSbloccoModulo);
						}
					}
				}catch(Exception e){
					throw e; // rilancio eccezione;
				}

			}// end Gestione Risposta



			/* ------- Ritorna le pstmt da eseguire per la risposta --------------- */
			return msgResponse;

		}catch (Exception e) {	
			this.msgDiag.logErroreGenerico(e, "EJBUtils.sendRispostaProtocollo");
			if(msgResponse!=null )
				((StateMessage)statoRisposta).closePreparedStatement();
			//	Rilancio l'eccezione
			throw new EJBUtilsException("EJBUtils.sendRispostaProtocollo error: "+e.getMessage(),e);

		}  	finally{
			this.unsetEJBSuffixFunctionName();
		}

	}




	/** ------------------- Metodi per la spedizione al nodo 'InoltroRisposte' ---------------- */

	/**
	 * Spedisce una busta di risposta   
	 * al modulo di openspcoop {@link org.openspcoop2.pdd.mdb.InoltroRisposteMDB}, 
	 * inserendolo all'interno di  un messaggio di tipo {@link org.openspcoop2.pdd.mdb.InoltroRisposteMessage} e 
	 * spedendolo nella coda 'toInoltroRisposte'.
	 *
	 * @param busta Busta utilizzata per la costruzione della busta di risposta
	 * @param inoltroSegnalazioneErrore Indicazione se la busta inviata segnala la precedente ricezione di una busta
	 *        di risposta malformata
	 * 
	 */
	public void sendToInoltroRisposte(Busta busta,
			boolean inoltroSegnalazioneErrore,String profiloGestione, GestoreMessaggi gm,
			String idCorrelazioneApplicativa,String idCorrelazioneApplicativaRisposta,String servizioApplicativoFruitore,boolean imbustamento)throws EJBUtilsException{

		if (   (this.openSPCoopState instanceof OpenSPCoopStateful)  ||
				this.oneWayVersione11) {
			
			try{
				this.msgDiag.highDebug("[EJBUtils] Creazione ObjectMessage for send nell'infrastruttura.");
				// costruzione
				InoltroRisposteMessage inoltroMSG = new InoltroRisposteMessage();
				inoltroMSG.setDominio(this.identitaPdD);
				inoltroMSG.setInoltroSegnalazioneErrore(inoltroSegnalazioneErrore);
				inoltroMSG.setBustaRisposta(busta);
				inoltroMSG.setProfiloGestione(profiloGestione);	
				inoltroMSG.setOneWayVersione11(this.oneWayVersione11);
				inoltroMSG.setIdCorrelazioneApplicativa(idCorrelazioneApplicativa);
				inoltroMSG.setIdCorrelazioneApplicativaRisposta(idCorrelazioneApplicativaRisposta);
				inoltroMSG.setServizioApplicativoFruitore(servizioApplicativoFruitore);
				inoltroMSG.setImbustamento(imbustamento);
				inoltroMSG.setImplementazionePdDSoggettoMittente(this.implementazionePdDSoggettoMittente);
				inoltroMSG.setImplementazionePdDSoggettoDestinatario(this.implementazionePdDSoggettoDestinatario);
				inoltroMSG.setPddContext(this.pddContext);
	
				if ( this.oneWayVersione11 ) {
					
					OpenSPCoopStateless statelessSerializzabile = ((OpenSPCoopStateless) this.openSPCoopState).rendiSerializzabile();
					inoltroMSG.setOpenspcoopstate(statelessSerializzabile);
				}

				// send
				this.nodeSender.send(inoltroMSG, org.openspcoop2.pdd.mdb.InoltroRisposte.ID_MODULO, this.msgDiag, 
						this.identitaPdD,this.idModulo, this.idSessione,gm);
	
			} catch (Exception e) {
				this.log.error("Spedizione->InoltroRisposte non riuscita",e);	
				this.msgDiag.logErroreGenerico(e, "EJBUtils.sendToInoltroRisposte");
				throw new EJBUtilsException("EJBUtils.sendToInoltroRisposte error: "+e.getMessage(),e);
			}
			
		}
	}







	
	
	/** ------------------- Metodi per la generazione di messaggi  'OK' ---------------- */
	
	/**
	 * Metodo che si occupa di costruire un messaggio SOAPElement 'openspcoop OK'. 
	 *
	 * @return messaggio Soap 'OK' in caso di successo, null altrimenti.
	 * 
	 */
	public OpenSPCoop2Message buildOpenSPCoopOK_soapMsg(SOAPVersion versioneSoap, String id) throws UtilsException{
		try{
			OpenSPCoop2MessageFactory mf = OpenSPCoop2MessageFactory.getMessageFactory();
			OpenSPCoop2Message responseSOAPMessage = mf.createMessage(versioneSoap);
			SOAPBody soapBody = responseSOAPMessage.getSOAPBody();
			
			EsitoRichiesta esito = new EsitoRichiesta();
			esito.setIdentificativoMessaggio(id);
			esito.setStato(org.openspcoop2.core.integrazione.constants.Costanti.PRESA_IN_CARICO);
			
			byte[]xmlEsito = EsitoRichiestaXMLUtils.generateEsitoRichiesta(esito);
			
			soapBody.addChildElement(responseSOAPMessage.createSOAPElement(xmlEsito));
			return responseSOAPMessage;

		} catch(Exception e) {
			throw new UtilsException("Creazione MsgOpenSPCoopOK non riuscito: "+e.getMessage(),e);
		}
	}


	/**
	 * Metodo che si occupa di costruire un messaggio SOAPElement 'openspcoop OK'. 
	 *
	 * @return bytes del messaggio Soap 'OK' in caso di successo, null altrimenti.
	 * 
	 */
	public byte[] buildOpenSPCoopOK(SOAPVersion versioneSoap, String id) throws UtilsException{
		try{
			
			EsitoRichiesta esito = new EsitoRichiesta();
			esito.setIdentificativoMessaggio(id);
			esito.setStato(org.openspcoop2.core.integrazione.constants.Costanti.PRESA_IN_CARICO);
			return EsitoRichiestaXMLUtils.generateEsitoRichiesta(esito);

		} catch(Exception e) {
			throw new UtilsException("Creazione MsgOpenSPCoopOK non riuscito: "+e.getMessage(),e);
		}
	}
	
	
	
	
	
	






	/** ------------------- Metodi per la spedizione al nodo 'RicezioneBuste' ---------------- */

	/**
	 * Spedisce un header Protocollo <var>header</var>
	 * al modulo di openspcoop 'RicezioneBusteXXX', 
	 * inserendolo all'interno di  un
	 * messaggio di tipo {@link org.openspcoop2.pdd.services.RicezioneBusteMessage} e 
	 * spedendolo nella coda 'toRicezioneBusteXXX'.
	 *
	 * @param idModuloInAttesa identificativo del modulo 'RicezioneBusteXXX'. 
	 * @param busta Busta utilizzata per la costruzione della busta di risposta
	 * @param idSbloccoModulo Identificativo del messaggio di sblocco se il messaggio non contiene una busta
	 * 
	 */
	public void sendBustaToRicezioneBuste(String idModuloInAttesa,Busta busta, String idSbloccoModulo) throws EJBUtilsException{

		if (this.openSPCoopState instanceof OpenSPCoopStateless) {
			RicezioneBusteMessage ricezioneMSG = new RicezioneBusteMessage();
			ricezioneMSG.setBustaRisposta(busta);
			ricezioneMSG.setIdMessaggioSblocco(idSbloccoModulo);
			ricezioneMSG.setPddContext(this.pddContext);
			((OpenSPCoopStateless)this.openSPCoopState).setMessageLib(ricezioneMSG);
		}
		
		if( CostantiConfigurazione.COMUNICAZIONE_INFRASTRUTTURALE_JMS.equals(this.propertiesReader.getNodeReceiver())){ 

			try{
				this.msgDiag.highDebug("Creazione ObjectMessage for send via JMS.");
				// costruzione
				RicezioneBusteMessage ricezioneMSG = new RicezioneBusteMessage();
				ricezioneMSG.setBustaRisposta(busta);
				ricezioneMSG.setIdMessaggioSblocco(idSbloccoModulo);
				ricezioneMSG.setPddContext(this.pddContext);

				if (this.openSPCoopState instanceof OpenSPCoopStateless) 
					((OpenSPCoopStateless)this.openSPCoopState).setMessageLib(ricezioneMSG);

				else { //Stateful mode:
					// NOTA: usare realmente JMSSender e non l'interfaccia INodeSender, perche' questo punto riguarda INodeReceiver!!
					String idT = PdDContext.getValue(org.openspcoop2.core.constants.Costanti.CLUSTER_ID, this.pddContext);
					JMSSender senderJMS = new JMSSender(this.identitaPdD,this.idModulo,this.log,idT);
					java.util.Properties prop = new java.util.Properties();
					prop.put("ID",this.idSessione);

					if(senderJMS.send(idModuloInAttesa,
							ricezioneMSG,prop) == false){
						this.msgDiag.logErroreGenerico(senderJMS.getErrore(), "EJBUtils.sendBustaToRicezioneBuste.senderJMS");
						throw new Exception("SendJMSError: "+senderJMS.getErrore());
					}

					this.msgDiag.highDebug("ObjectMessage send via JMS.");
				}
			} catch (Exception e) {	
				this.msgDiag.logErroreGenerico(e, "EJBUtils.sendBustaToRicezioneBuste");
				throw new EJBUtilsException("EJBUtils.sendToRicezioneBuste error: "+e.getMessage(),e);
			}
		}
	}

	public void setScenarioCooperazione(String scenarioCooperazione) {
		this.scenarioCooperazione = scenarioCooperazione;
	}

	public Timestamp getRicezioneMsgRisposta() {
		return this.ricezioneMsgRisposta;
	}

	public void setRicezioneMsgRisposta(Timestamp ricezioneMsgRisposta) {
		this.ricezioneMsgRisposta = ricezioneMsgRisposta;
	}

	public Timestamp getSpedizioneMsgIngresso() {
		return this.spedizioneMsgIngresso;
	}

	public void setSpedizioneMsgIngresso(Timestamp spedizioneMsgIngresso) {
		this.spedizioneMsgIngresso = spedizioneMsgIngresso;
	}

	
	/** restituisce true solo se i bytes del messaggio vanno salvati nel db */
	public boolean saveMessageStateless(PortaDelegata pd) throws Exception {
		ProfiloDiCollaborazione profiloCollaborazione = EJBUtils.calcolaProfiloCollaborazione(this.scenarioCooperazione);
		return this.configurazionePdDReader.isModalitaStateless(pd, profiloCollaborazione);
	}


	public void setOneWayVersione11(boolean oneWay11) {
		this.oneWayVersione11 = oneWay11;
	}

	public void setPortaDiTipoStateless_esclusoOneWay11(boolean stateless) {
		this.portaDiTipoStateless_esclusoOneWay11 = stateless;
	}

	public void setRouting(boolean routing) {
		this.routing = routing;
	}

	public void setRollbackRichiestaInCasoErrore(
			boolean rollbackRichiestaInCasoErrore) {
		this.rollbackRichiestaInCasoErrore = rollbackRichiestaInCasoErrore;
	}
	
	public void setRollbackRichiestaInCasoErrore_rollbackHistory(
			boolean rollbackRichiestaInCasoErrore_rollbackHistory) {
		this.rollbackRichiestaInCasoErrore_rollbackHistory = rollbackRichiestaInCasoErrore_rollbackHistory;
	}


	public static ProfiloDiCollaborazione calcolaProfiloCollaborazione(String scenarioCooperazione){
		// Calcolo Profilo di Collaborazione
		ProfiloDiCollaborazione profiloCollaborazione = null;
		if( Costanti.SCENARIO_ONEWAY_INVOCAZIONE_SERVIZIO.equals(scenarioCooperazione) ){
			profiloCollaborazione = ProfiloDiCollaborazione.ONEWAY;
		}
		else if(Costanti.SCENARIO_SINCRONO_INVOCAZIONE_SERVIZIO.equals(scenarioCooperazione) ){
			profiloCollaborazione = ProfiloDiCollaborazione.SINCRONO;
		}
		else if(Costanti.SCENARIO_ASINCRONO_ASIMMETRICO_POLLING.equals(scenarioCooperazione) || Costanti.SCENARIO_ASINCRONO_ASIMMETRICO_INVOCAZIONE_SERVIZIO.equals(scenarioCooperazione)){
			profiloCollaborazione = ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO;
		}
		else if (Costanti.SCENARIO_ASINCRONO_SIMMETRICO_INVOCAZIONE_SERVIZIO.equals(scenarioCooperazione) ||
				Costanti.SCENARIO_ASINCRONO_SIMMETRICO_CONSEGNA_RISPOSTA.equals(scenarioCooperazione)){
			profiloCollaborazione = ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO;
		}
		return profiloCollaborazione;
	}

	public String getServizioApplicativoErogatore() {
		return this.servizioApplicativoErogatore;
	}

	public void setServizioApplicativoErogatore(String servizioApplicativoErogatore) {
		this.servizioApplicativoErogatore = servizioApplicativoErogatore;
	}

}
