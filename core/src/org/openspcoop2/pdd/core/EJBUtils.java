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




package org.openspcoop2.pdd.core;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.soap.SOAPBody;

import org.openspcoop2.core.config.GestioneErrore;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaApplicativaServizioApplicativo;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.constants.TipoBehaviour;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.config.driver.db.DriverConfigurazioneDB;
import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.eccezione.details.DettaglioEccezione;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.integrazione.EsitoRichiesta;
import org.openspcoop2.core.integrazione.utils.EsitoRichiestaXMLUtils;
import org.openspcoop2.core.transazioni.IdTransazioneApplicativoServer;
import org.openspcoop2.core.transazioni.TransazioneApplicativoServer;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.OpenSPCoop2MessageParseResult;
import org.openspcoop2.message.OpenSPCoop2SoapMessage;
import org.openspcoop2.message.constants.MessageRole;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.exception.ParseException;
import org.openspcoop2.message.utils.MessageUtilities;
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
import org.openspcoop2.pdd.core.behaviour.BehaviourLoadBalancer;
import org.openspcoop2.pdd.core.behaviour.BehaviourLoader;
import org.openspcoop2.pdd.core.behaviour.IBehaviour;
import org.openspcoop2.pdd.core.behaviour.StatoFunzionalita;
import org.openspcoop2.pdd.core.behaviour.built_in.multi_deliver.ConfigurazioneGestioneConsegnaNotifiche;
import org.openspcoop2.pdd.core.behaviour.built_in.multi_deliver.GestioneConsegnaNotificheUtils;
import org.openspcoop2.pdd.core.behaviour.built_in.multi_deliver.MessaggioDaNotificare;
import org.openspcoop2.pdd.core.behaviour.built_in.multi_deliver.MultiDeliverBehaviour;
import org.openspcoop2.pdd.core.behaviour.built_in.multi_deliver.MultiDeliverUtils;
import org.openspcoop2.pdd.core.handlers.HandlerException;
import org.openspcoop2.pdd.core.node.INodeSender;
import org.openspcoop2.pdd.core.state.IOpenSPCoopState;
import org.openspcoop2.pdd.core.state.OpenSPCoopState;
import org.openspcoop2.pdd.core.state.OpenSPCoopStateful;
import org.openspcoop2.pdd.core.state.OpenSPCoopStateless;
import org.openspcoop2.pdd.core.transazioni.GestoreConsegnaMultipla;
import org.openspcoop2.pdd.logger.MsgDiagnosticiProperties;
import org.openspcoop2.pdd.logger.MsgDiagnostico;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.pdd.mdb.ConsegnaContenutiApplicativi;
import org.openspcoop2.pdd.mdb.ConsegnaContenutiApplicativiBehaviourMessage;
import org.openspcoop2.pdd.mdb.ConsegnaContenutiApplicativiMessage;
import org.openspcoop2.pdd.mdb.EsitoLib;
import org.openspcoop2.pdd.mdb.InoltroRisposte;
import org.openspcoop2.pdd.mdb.InoltroRisposteMessage;
import org.openspcoop2.pdd.services.ServicesUtils;
import org.openspcoop2.pdd.services.core.RicezioneBusteMessage;
import org.openspcoop2.pdd.services.core.RicezioneContenutiApplicativiMessage;
import org.openspcoop2.pdd.services.error.AbstractErrorGenerator;
import org.openspcoop2.pdd.services.error.RicezioneBusteExternalErrorGenerator;
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
import org.openspcoop2.protocol.sdk.config.ITraduttore;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreCooperazione;
import org.openspcoop2.protocol.sdk.constants.ErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.ErroriCooperazione;
import org.openspcoop2.protocol.sdk.constants.IntegrationFunctionError;
import org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione;
import org.openspcoop2.protocol.sdk.constants.RuoloMessaggio;
import org.openspcoop2.protocol.sdk.state.IState;
import org.openspcoop2.protocol.sdk.state.RequestInfo;
import org.openspcoop2.protocol.sdk.state.StateMessage;
import org.openspcoop2.protocol.sdk.state.StatelessMessage;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.id.UniqueIdentifierManager;
import org.openspcoop2.utils.resources.Loader;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.slf4j.Logger;


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
	private IProtocolFactory<?> protocolFactory = null;
	private IProtocolVersionManager protocolManager = null;
	
	/** DettaglioEccezioneOpenSPCoop2Builder */
	private DettaglioEccezioneOpenSPCoop2Builder dettaglioBuilder = null;
	
	/** RicezioneBusteExternalErrorGenerator */
	private RicezioneBusteExternalErrorGenerator generatoreErrorePortaApplicativa = null;
	private IntegrationFunctionError _integrationFunctionErrorPortaApplicativa;
	private IntegrationFunctionError getIntegrationFunctionErrorPortaApplicativa(boolean erroreValidazione) {
		if(this._integrationFunctionErrorPortaApplicativa!=null) {
			return this._integrationFunctionErrorPortaApplicativa;
		}
		else {
			IntegrationFunctionError ife = AbstractErrorGenerator.getIntegrationInternalError(this.pddContext); // default
			if(erroreValidazione) {
				ife = IntegrationFunctionError.INTERNAL_RESPONSE_ERROR.equals(ife) ? 
						IntegrationFunctionError.INVALID_INTEROPERABILITY_PROFILE_RESPONSE :
						IntegrationFunctionError.INVALID_INTEROPERABILITY_PROFILE_REQUEST;
			}
			return ife;
		}
	}
	public void setGeneratoreErrorePortaApplicativa(RicezioneBusteExternalErrorGenerator generatoreErrorePortaApplicativa) {
		this.generatoreErrorePortaApplicativa = generatoreErrorePortaApplicativa;
	}
	public void setIntegrationFunctionErrorPortaApplicativa(IntegrationFunctionError integrationErrorPortaApplicativa) {
		this._integrationFunctionErrorPortaApplicativa = integrationErrorPortaApplicativa;
	}

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
			this.protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName((String) this.pddContext.getObject(org.openspcoop2.core.constants.Costanti.PROTOCOL_NAME));
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
				
				esito.setDataRispedizioneAggiornata(dataRiconsegna);
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
				
				esito.setDataRispedizioneAggiornata(dataRiconsegna);
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
				
				esito.setDataRispedizioneAggiornata(dataRispedizione);	
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

		String idTransazione = (String) this.pddContext.getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE);
		
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
		if(richiestaDelegata!=null && richiestaDelegata.getIdPortaDelegata()!=null)
			nomePorta = richiestaDelegata.getIdPortaDelegata().getNome();
		
		RollbackRepositoryBuste rollbackBuste = null;
		RollbackRepositoryBuste rollbackBusteRifMessaggio = null;
		GestoreMessaggi msgRequest = null;
		GestoreMessaggi msgResponse = null;
		GestoreMessaggi msgSbloccoRicezioneContenutiApplicativi = null; // Evenutuale sblocco se presente ricezioneContenutiApplicativi per profili oneway/sincroni

		if(richiestaDelegata==null) {
			throw new EJBUtilsException("Param richiestaDelegata is null");
		}
		
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
				Imbustamento imbustatore = new Imbustamento(this.log,this.protocolFactory, this.openSPCoopState.getStatoRichiesta());

				idRisposta = 
					imbustatore.buildID(this.identitaPdD, idTransazione, 
							this.propertiesReader.getGestioneSerializableDB_AttesaAttiva(),
							this.propertiesReader.getGestioneSerializableDB_CheckInterval(),
							RuoloMessaggio.RISPOSTA);
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
						nomePorta, false, false,
						null, null, false);

			} // end Gestione Risposta

			/* ------- Gestione msgSbloccoRicezioneContenutiApplicativi --------------- */
			String idSbloccoRicezioneContenutiApplicativi = null;
			if(consegnaRispostaAsincrona && idModuloInAttesa != null){
				//	Creo stato per msgSbloccoRicezioneContenutiApplicativi
				Imbustamento imbustatore = new Imbustamento(this.log,this.protocolFactory, this.openSPCoopState.getStatoRichiesta());
				idSbloccoRicezioneContenutiApplicativi= 
					imbustatore.buildID(this.identitaPdD, idTransazione, 
							this.propertiesReader.getGestioneSerializableDB_AttesaAttiva(),
							this.propertiesReader.getGestioneSerializableDB_CheckInterval(),
							RuoloMessaggio.RISPOSTA);
				if(idSbloccoRicezioneContenutiApplicativi == null){
					throw new Exception("Identificativo non costruito.");
				}  
				msgSbloccoRicezioneContenutiApplicativi = new GestoreMessaggi( this.openSPCoopState, false, idSbloccoRicezioneContenutiApplicativi,Costanti.INBOX,this.msgDiag,this.pddContext);
				if(this.protocolManager.isHttpEmptyResponseOneWay())
					msgSbloccoRicezioneContenutiApplicativi.registraMessaggio(MessageUtilities.buildEmptyMessage(responseMessageError.getFactory(), responseMessageError.getMessageType(), MessageRole.RESPONSE),correlazioneApplicativa,correlazioneApplicativaRisposta);
				else
					msgSbloccoRicezioneContenutiApplicativi.registraMessaggio(this.buildOpenSPCoopOK(responseMessageError.getMessageType(),this.idSessione),correlazioneApplicativa,correlazioneApplicativaRisposta);
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

		String idTransazione = (String) this.pddContext.getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE);
		
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
		if(richiestaDelegata!=null && richiestaDelegata.getIdPortaDelegata()!=null)
			nomePorta = richiestaDelegata.getIdPortaDelegata().getNome();
		
		if(richiestaDelegata==null) {
			throw new EJBUtilsException("Param richiestaDelegata is null");
		}
		
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
					
					Imbustamento imbustatore = new Imbustamento(this.log,this.protocolFactory, this.openSPCoopState.getStatoRichiesta());
					idRisposta = 
						imbustatore.buildID(this.identitaPdD, idTransazione, 
								this.propertiesReader.getGestioneSerializableDB_AttesaAttiva(),
								this.propertiesReader.getGestioneSerializableDB_CheckInterval(),
								RuoloMessaggio.RISPOSTA);
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
						nomePorta, false, false,
						null, null, false);
			} // end Gestione Risposta






			/* ------- Gestione msgSbloccoRicezioneContenutiApplicativi --------------- */
			if(consegnaRispostaAsincrona && idModuloInAttesa != null){
				//	Creo stato per msgSbloccoRicezioneContenutiApplicativi
				Imbustamento imbustatore = new Imbustamento(this.log,this.protocolFactory,this.openSPCoopState.getStatoRichiesta());
				String idSbloccoRicezioneContenutiApplicativi= 
					imbustatore.buildID(this.identitaPdD, idTransazione, 
							this.propertiesReader.getGestioneSerializableDB_AttesaAttiva(),
							this.propertiesReader.getGestioneSerializableDB_CheckInterval(),
							RuoloMessaggio.RISPOSTA);
				if(idSbloccoRicezioneContenutiApplicativi == null){
					throw new Exception("Identificativo non costruito.");
				}  
				msgSbloccoRicezioneContenutiApplicativi = new GestoreMessaggi(this.openSPCoopState, false, idSbloccoRicezioneContenutiApplicativi,Costanti.INBOX,this.msgDiag,this.pddContext);
				RequestInfo requestInfo = (RequestInfo) this.pddContext.getObject(org.openspcoop2.core.constants.Costanti.REQUEST_INFO);
				MessageType msgTypeSblocco = requestInfo.getIntegrationRequestMessageType();
				if(msgTypeSblocco==null){
					throw new Exception("Versione messaggio non definita");
				}
				if(this.protocolManager.isHttpEmptyResponseOneWay())
					msgSbloccoRicezioneContenutiApplicativi.registraMessaggio(MessageUtilities.buildEmptyMessage(OpenSPCoop2MessageFactory.getDefaultMessageFactory(), msgTypeSblocco, MessageRole.RESPONSE),correlazioneApplicativa,correlazioneApplicativaRisposta);
				else
					msgSbloccoRicezioneContenutiApplicativi.registraMessaggio(this.buildOpenSPCoopOK(msgTypeSblocco,this.idSessione),correlazioneApplicativa,correlazioneApplicativaRisposta);
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
	 * inserendolo all'interno di  un messaggio di tipo {@link org.openspcoop2.pdd.services.core.RicezioneContenutiApplicativiMessage} e 
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
					String idT = PdDContext.getValue(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE, this.pddContext);
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
			busta.setTipoMittente(richiestaDelegata.getIdSoggettoFruitore().getTipo());
			busta.setMittente(richiestaDelegata.getIdSoggettoFruitore().getNome());
			busta.setIdentificativoPortaMittente(richiestaDelegata.getIdSoggettoFruitore().getCodicePorta());
			busta.setTipoDestinatario(richiestaDelegata.getIdServizio().getSoggettoErogatore().getTipo());
			busta.setDestinatario(richiestaDelegata.getIdServizio().getSoggettoErogatore().getNome());
			busta.setIdentificativoPortaDestinatario(richiestaDelegata.getIdServizio().getSoggettoErogatore().getCodicePorta());
			busta.setTipoServizio(richiestaDelegata.getIdServizio().getTipo());
			busta.setServizio(richiestaDelegata.getIdServizio().getNome());
			busta.setVersioneServizio(richiestaDelegata.getIdServizio().getVersione());
			busta.setAzione(richiestaDelegata.getIdServizio().getAzione());
			busta.setID(idBustaConsegna);
			busta.setRiferimentoMessaggio(this.idSessione);
			
			busta.setProfiloDiCollaborazione(richiestaDelegata.getProfiloCollaborazione());
			
			boolean idCollaborazione = false;
			switch (this.protocolFactory.createProtocolVersionManager(richiestaDelegata.getProfiloGestione()).getCollaborazione(busta)) {
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

			if(richiestaDelegata!=null) {
				this.msgDiag.addKeyword(CostantiPdD.KEY_SA_EROGATORE, richiestaDelegata.getServizioApplicativo());
			}
			
			if(richiestaDelegata==null) {
				throw new EJBUtilsException("Param richiestaDelegata is null");
			}
			
			String nomePorta = null;
			if(richiestaDelegata.getIdPortaDelegata()!=null)
				nomePorta = richiestaDelegata.getIdPortaDelegata().getNome();
			
			//	Aggiorno dati di consegna
			this.msgDiag.setServizioApplicativo(richiestaDelegata.getServizioApplicativo());

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
					nomePorta, false, false,
					null, null, false);

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
	public Behaviour sendToConsegnaContenutiApplicativi(RequestInfo requestInfo,
			RichiestaApplicativa richiestaApplicativa,Busta busta,
			GestoreMessaggi gestoreMessaggi,PortaApplicativa pa,RepositoryBuste repositoryBuste) throws EJBUtilsException{
		return sendToConsegnaContenutiApplicativi(requestInfo,richiestaApplicativa, busta, gestoreMessaggi, pa, repositoryBuste, null);
	}
	public Behaviour sendToConsegnaContenutiApplicativi(RequestInfo requestInfo,
			RichiestaApplicativa richiestaApplicativa,Busta busta,
			GestoreMessaggi gestoreMessaggi,PortaApplicativa pa,RepositoryBuste repositoryBuste,
			RichiestaDelegata localForwardRichiestaDelegata) throws EJBUtilsException{

		try{
		
			Behaviour behaviour = null;
			
			
			boolean soggettoVirtuale = false;
			if(richiestaApplicativa!=null &&
					richiestaApplicativa.getIDServizio()!=null &&
							richiestaApplicativa.getIDServizio().getSoggettoErogatore()!=null){	    
				soggettoVirtuale = this.configurazionePdDReader.isSoggettoVirtuale( richiestaApplicativa.getIDServizio().getSoggettoErogatore(), requestInfo );
			}
			
			
			/* ----- Indicazione Stateless ----- */
			boolean stateless = false;
			if(!soggettoVirtuale) {
				if(localForwardRichiestaDelegata!=null){
					stateless = this.portaDiTipoStateless_esclusoOneWay11;
				}else{
					stateless = this.configurazionePdDReader.isModalitaStateless(pa, busta.getProfiloDiCollaborazione());
				}
			}
			
			if(pa!=null){
				this.msgDiag.addKeyword(CostantiPdD.KEY_PORTA_APPLICATIVA, pa.getNome());
			}
			
			
			/* ----- Recupero eventuale processo di behaviour ----- */
			boolean registraNuoviMessaggiViaBehaviour = false;
			@SuppressWarnings("unused")
			int sizeNuoviMessaggiViaBehaviour = -1;
			BehaviourForwardToFilter singleFilterBehaviour = null;
			boolean behaviourResponseTo = false;
			IDServizioApplicativo behaviour_idSA_SyncResponder = null;
			MessaggioDaNotificare tipiMessaggiNotificabili = null;
			IBehaviour behaviourImpl = null;
			// pa is null nel caso di soggetto virtuale
			if(pa!=null && pa.getBehaviour()!=null && pa.getBehaviour().getNome()!=null && !"".equals(pa.getBehaviour().getNome())){
				
				behaviourImpl = BehaviourLoader.newInstance(pa.getBehaviour(), this.msgDiag,
						this.pddContext, this.protocolFactory,
						this.openSPCoopState!=null ? this.openSPCoopState.getStatoRichiesta() : null);
				
				gestoreMessaggi.setPortaDiTipoStateless(stateless);
				behaviour = behaviourImpl.behaviour(gestoreMessaggi, busta, pa, requestInfo);
								
				behaviourResponseTo = behaviour!=null && behaviour.isResponseTo();
				
				if(behaviour!=null) {
					behaviour_idSA_SyncResponder = behaviour.getApplicativeSyncResponder();
				}
				
				if(behaviour!=null && behaviour.getForwardTo()!=null){
					if(behaviour.getForwardTo().size()>1){
						registraNuoviMessaggiViaBehaviour = true;
						if(behaviourResponseTo==false){
							throw new Exception("La consegna di messaggi multipli via custom behaviour (tipo:"+pa.getBehaviour().getNome()+
									") e' permessa solo se viene abilita anche la funzione 'responseTo'");
						}
						for (int i=0; i<behaviour.getForwardTo().size(); i++) {
							BehaviourForwardTo forwardTo = behaviour.getForwardTo().get(i);
							if(forwardTo.getDescription()==null){
								forwardTo.setDescription("ForwardTo["+i+"]");
							}
							if(forwardTo.getMessage()==null){
								throw new Exception("La consegna di messaggi multipli via custom behaviour (tipo:"+pa.getBehaviour().getNome()+
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

					String idTransazione = (String) this.pddContext.getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE);
					IOpenSPCoopState stateGestoreMessaggiBehaviour_newConnection_old = null;
					OpenSPCoopStateless stateGestoreMessaggiBehaviour_newConnection = null;
					OpenSPCoopStateless stateGestoreMessaggiBehaviour_onlyUseConnectionFalse = null;
					boolean oldUseConnection = false;
					try{
						if (stateless && !this.propertiesReader.isServerJ2EE() ) {
							boolean createNew = true;
							if( (gestoreMessaggi.getOpenspcoopstate() instanceof OpenSPCoopStateless)) {
								OpenSPCoopStateless check = (OpenSPCoopStateless) gestoreMessaggi.getOpenspcoopstate();
								if(check.getConnectionDB()!=null && check.getConnectionDB().isClosed()==false) {
									createNew = false;
									stateGestoreMessaggiBehaviour_onlyUseConnectionFalse = (OpenSPCoopStateless) gestoreMessaggi.getOpenspcoopstate();
									oldUseConnection = check.isUseConnection();
									stateGestoreMessaggiBehaviour_onlyUseConnectionFalse.setUseConnection(true);
								}
							}
							
							if(createNew) {
								
								stateGestoreMessaggiBehaviour_newConnection_old = gestoreMessaggi.getOpenspcoopstate();
								
								stateGestoreMessaggiBehaviour_newConnection = new OpenSPCoopStateless();
								stateGestoreMessaggiBehaviour_newConnection.setUseConnection(true);
								stateGestoreMessaggiBehaviour_newConnection.setTempiAttraversamentoPDD(((OpenSPCoopStateless)this.openSPCoopState).getTempiAttraversamentoPDD());
								stateGestoreMessaggiBehaviour_newConnection.setDimensioneMessaggiAttraversamentoPDD(((OpenSPCoopStateless)this.openSPCoopState).getDimensioneMessaggiAttraversamentoPDD());
								stateGestoreMessaggiBehaviour_newConnection.setIDCorrelazioneApplicativa(((OpenSPCoopStateless)this.openSPCoopState).getIDCorrelazioneApplicativa());
								stateGestoreMessaggiBehaviour_newConnection.setIDCorrelazioneApplicativaRisposta(((OpenSPCoopStateless)this.openSPCoopState).getIDCorrelazioneApplicativaRisposta());
								stateGestoreMessaggiBehaviour_newConnection.setPddContext(((OpenSPCoopStateless)this.openSPCoopState).getPddContext());
								stateGestoreMessaggiBehaviour_newConnection.initResource(this.identitaPdD, "EJBUtils.behaviour_"+pa.getBehaviour().getNome(), idTransazione);
								gestoreMessaggi.updateOpenSPCoopState(stateGestoreMessaggiBehaviour_newConnection);
								
							}
						}
					
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
							
							if(stateless){
								repositoryBuste.eliminaUtilizzoPdDFromInBox(busta.getID(),repositoryBuste.isRegistrazioneInCorso());
							}
							else{
								repositoryBuste.eliminaUtilizzoPdDFromInBox(busta.getID(),true);
							}
						
							// Forzo oneway1.1 per farlo registrare anche se siamo in stateless puro
							boolean originalValue = gestoreMessaggi.isOneWayVersione11();
							gestoreMessaggi.setOneWayVersione11(true);
							gestoreMessaggi.logicDeleteMessage();
							gestoreMessaggi.setOneWayVersione11(originalValue);
							
						}
						
						// Applico modifiche effettuate dal modulo Consegna
						if (stateless && !this.propertiesReader.isServerJ2EE() ) {
							if(stateGestoreMessaggiBehaviour_newConnection!=null) {
								if(stateGestoreMessaggiBehaviour_newConnection.resourceReleased()){
									// il modulo di consegna rilascia la risorsa
									stateGestoreMessaggiBehaviour_newConnection.updateResource(idTransazione);
								}
								stateGestoreMessaggiBehaviour_newConnection.commit();
							}
							else if(stateGestoreMessaggiBehaviour_onlyUseConnectionFalse!=null) {
								stateGestoreMessaggiBehaviour_onlyUseConnectionFalse.commit();
							}
						}
						
					}finally{
						if (stateless && !this.propertiesReader.isServerJ2EE() ) {
							if(stateGestoreMessaggiBehaviour_newConnection!=null){
								stateGestoreMessaggiBehaviour_newConnection.releaseResource();
								gestoreMessaggi.updateOpenSPCoopState(stateGestoreMessaggiBehaviour_newConnection_old);
							}
							else if(stateGestoreMessaggiBehaviour_onlyUseConnectionFalse!=null) {
								stateGestoreMessaggiBehaviour_onlyUseConnectionFalse.setUseConnection(oldUseConnection);
							}
						}
					}
				}
				else{
					// se non devo registrare alcun messaggio, e sono in stateless, ma e' stata configurata una replyTo
					// ritorno errore, poiche' deve essere configurato un messaggio da inoltrare.
					if(stateless && behaviourResponseTo){
						throw new Exception("La definizione dell'elemento 'responseTo', via custom behaviour (tipo:"+pa.getBehaviour().getNome()+
								"), in una porta applicativa stateless richiede la definizione almeno di un elemento forwardTo contenente un messaggio da inoltrare.");
					}
				}
				
			}
			
			
			/* ----- Check Tipo ----- */
			if(richiestaApplicativa==null) {
				throw new Exception("Tipo di consegna sconosciuta (RichiestaApplicativa non definita)");
			}
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
				if(soggettoVirtuale){	    
					SoggettoVirtuale soggettiVirtuali = this.configurazionePdDReader.getServiziApplicativi_SoggettiVirtuali(richiestaApplicativa);
					if(soggettiVirtuali == null){
						throw new EJBUtilsConsegnaException("(SoggettoVirtuale) "+this.msgDiag.getMessaggio_replaceKeywords(MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI,"servizioApplicativoNonDefinito"),
								this.msgDiag.getLivello(MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI,"servizioApplicativoNonDefinito"),
								this.msgDiag.getCodice(MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI,"servizioApplicativoNonDefinito"));
					}
					boolean GESTISCI_BEHAVIOUR = true;
					List<String> idServiziApplicativiVirtuali = soggettiVirtuali.getIdServiziApplicativi(GESTISCI_BEHAVIOUR,gestoreMessaggi,busta, requestInfo,
							this.pddContext, this.protocolFactory,
							this.openSPCoopState!=null ? this.openSPCoopState.getStatoRichiesta() : null);
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
			if( serviziApplicativiAbilitati.size() < 1 && (behaviour_idSA_SyncResponder==null) ){
				throw new EJBUtilsConsegnaException(this.msgDiag,MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI,"behaviour.servizioApplicativoNonDefinito");
			}

			

			/* ----- Controllo che servizi applicativi multipli siano collegati solo a profilo OneWay (a meno di abilitare il behaviour personalizzato) ----- */
			// Questo per poter ritornare una risposta
			if(registraNuoviMessaggiViaBehaviour==false || behaviourResponseTo==false){
				if(behaviour_idSA_SyncResponder==null) {
					if( (Costanti.SCENARIO_ONEWAY_INVOCAZIONE_SERVIZIO.equals(richiestaApplicativa.getScenario()) == false) &&
							(serviziApplicativiAbilitati.size() > 1)){
						throw new EJBUtilsConsegnaException(this.msgDiag,MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI,"gestioneProfiloNonOneway.consegnaVersoNServiziApplicativi");
					}else if( (Costanti.SCENARIO_ONEWAY_INVOCAZIONE_SERVIZIO.equals(richiestaApplicativa.getScenario())) && (this.oneWayVersione11==false) && 
							(this.openSPCoopState instanceof OpenSPCoopStateless) &&
							(serviziApplicativiAbilitati.size() > 1) )  {
						for (String nomeServizioApplicativo : serviziApplicativiAbilitati) {
							IDServizioApplicativo idSA = new IDServizioApplicativo();
							idSA.setNome(nomeServizioApplicativo);
							idSA.setIdSoggettoProprietario(richiestaApplicativa.getIDServizio().getSoggettoErogatore());
							ServizioApplicativo sappl = this.configurazionePdDReader.getServizioApplicativo(idSA,requestInfo);
							boolean servizioApplicativoConConnettore = this.configurazionePdDReader.invocazioneServizioConConnettore(sappl);
							boolean getMessageAbilitato = this.configurazionePdDReader.invocazioneServizioConGetMessage(sappl);
							if(servizioApplicativoConConnettore || (getMessageAbilitato==false)){
								throw new EJBUtilsConsegnaException(this.msgDiag,MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI,"gestioneStateless.consegnaVersoNServiziApplicativi");
							}
						}
					}
				}
			}
			
			boolean EFFETTUA_SPEDIZIONE_CONSEGNA_CONTENUTI = true;
			boolean ATTENDI_ESITO_TRANSAZIONE_SINCRONA_PRIMA_DI_SPEDIRE = true; 

			/* ----- Registro destinatari messaggi e spedizione messaggi ----- */
			if(behaviour_idSA_SyncResponder==null && (behaviour==null || !registraNuoviMessaggiViaBehaviour)){
				
				List<String> serviziApplicativiAbilitatiById = DriverConfigurazioneDB.normalizeConnettoriMultpliById(serviziApplicativiAbilitati, pa);
				if(serviziApplicativiAbilitatiById!=null && !serviziApplicativiAbilitatiById.isEmpty()) {
					this.pddContext.addObject(org.openspcoop2.core.constants.Costanti.CONSEGNA_MULTIPLA_CONNETTORI_BY_ID, serviziApplicativiAbilitatiById);
				}
				
				_sendMessageToServiziApplicativi(serviziApplicativiAbilitati, soggettiRealiMappatiInUnSoggettoVirtuale, 
						richiestaApplicativa, localForwardRichiestaDelegata, gestoreMessaggi, busta, pa, repositoryBuste,
						null,null,stateless,this.openSPCoopState,false,
						null,
						EFFETTUA_SPEDIZIONE_CONSEGNA_CONTENUTI,!ATTENDI_ESITO_TRANSAZIONE_SINCRONA_PRIMA_DI_SPEDIRE,
						behaviour!=null ? behaviour.getLoadBalancer() : null, false,
						null,
						requestInfo);
			}
			else{
				
				boolean attendiEsitoTransazioneSincronaPrimaDiSpedire = false;
				
				if(behaviour_idSA_SyncResponder!=null) {
					
					this.pddContext.addObject(org.openspcoop2.core.constants.Costanti.CONSEGNA_MULTIPLA_SINCRONA, "true");
					
					attendiEsitoTransazioneSincronaPrimaDiSpedire = true;
					
					// TODO CHECK QUA O SOPRA RIGUARDANTE IL CASO INTEGRATINO MANAGER ???? NON PERMESSO ESSENDO SINCRONO???
					
					List<String> sa_list = new ArrayList<>();
					sa_list.add(behaviour_idSA_SyncResponder.getNome());
					_sendMessageToServiziApplicativi(sa_list, soggettiRealiMappatiInUnSoggettoVirtuale, 
							richiestaApplicativa, localForwardRichiestaDelegata, gestoreMessaggi, busta, pa, repositoryBuste,
							null,null,stateless,this.openSPCoopState,false,
							null,
							EFFETTUA_SPEDIZIONE_CONSEGNA_CONTENUTI,!ATTENDI_ESITO_TRANSAZIONE_SINCRONA_PRIMA_DI_SPEDIRE,
							null, false,
							null,
							requestInfo);
				}
				
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
					if( serviziApplicativiAbilitatiForwardTo.size() < 1 && (behaviour_idSA_SyncResponder==null) ){
						throw new EJBUtilsConsegnaException(this.msgDiag,MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI,"behaviour.servizioApplicativoNonDefinito");
					}
					
					List<String> serviziApplicativiAbilitatiForwardToNormalizedById = DriverConfigurazioneDB.normalizeConnettoriMultpliById(serviziApplicativiAbilitatiForwardTo, pa);
					if(serviziApplicativiAbilitatiForwardToNormalizedById!=null && !serviziApplicativiAbilitatiForwardToNormalizedById.isEmpty()) {
						this.pddContext.addObject(org.openspcoop2.core.constants.Costanti.CONSEGNA_MULTIPLA_CONNETTORI_BY_ID, serviziApplicativiAbilitatiForwardToNormalizedById);
					}
					this.pddContext.addObject(org.openspcoop2.core.constants.Costanti.CONSEGNA_MULTIPLA_CONNETTORI, serviziApplicativiAbilitatiForwardTo.size());
					if(serviziApplicativiAbilitatiForwardTo!=null && !serviziApplicativiAbilitatiForwardTo.isEmpty()) {
						this.pddContext.addObject(org.openspcoop2.core.constants.Costanti.CONSEGNA_MULTIPLA_CONNETTORI_BY_SA, serviziApplicativiAbilitatiForwardTo);
					}
										
					if(behaviourImpl instanceof MultiDeliverBehaviour) {
						MultiDeliverBehaviour multi = (MultiDeliverBehaviour) behaviourImpl;
						if(TipoBehaviour.CONSEGNA_CON_NOTIFICHE.equals(multi.getBt())) {
							tipiMessaggiNotificabili = org.openspcoop2.pdd.core.behaviour.built_in.multi_deliver.MultiDeliverUtils.readMessaggiNotificabili(pa, serviziApplicativiAbilitatiForwardTo, this.log);
						}
					}
					
					String idTransazione = (String) this.pddContext.getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE);
					OpenSPCoopStateless stateBehaviour_newConnection = null;
					OpenSPCoopStateless stateBehaviour_onlyUseConnectionFalse = null;
					boolean oldUseConnection = false;
					try{
						if (stateless && !this.propertiesReader.isServerJ2EE() ) {
							boolean createNew = true;
							if( (this.openSPCoopState instanceof OpenSPCoopStateless)) {
								OpenSPCoopStateless check = (OpenSPCoopStateless) this.openSPCoopState;
								if(check.getConnectionDB()!=null && check.getConnectionDB().isClosed()==false) {
									createNew = false;
									stateBehaviour_onlyUseConnectionFalse = (OpenSPCoopStateless) this.openSPCoopState;
									oldUseConnection = check.isUseConnection();
									stateBehaviour_onlyUseConnectionFalse.setUseConnection(true);
								}
							}
							
							if(createNew) {
								stateBehaviour_newConnection = new OpenSPCoopStateless();
								stateBehaviour_newConnection.setUseConnection(true);
								stateBehaviour_newConnection.setTempiAttraversamentoPDD(((OpenSPCoopStateless)this.openSPCoopState).getTempiAttraversamentoPDD());
								stateBehaviour_newConnection.setDimensioneMessaggiAttraversamentoPDD(((OpenSPCoopStateless)this.openSPCoopState).getDimensioneMessaggiAttraversamentoPDD());
								stateBehaviour_newConnection.setIDCorrelazioneApplicativa(((OpenSPCoopStateless)this.openSPCoopState).getIDCorrelazioneApplicativa());
								stateBehaviour_newConnection.setIDCorrelazioneApplicativaRisposta(((OpenSPCoopStateless)this.openSPCoopState).getIDCorrelazioneApplicativaRisposta());
								stateBehaviour_newConnection.setPddContext(((OpenSPCoopStateless)this.openSPCoopState).getPddContext());
								stateBehaviour_newConnection.initResource(this.identitaPdD, "EJBUtils.behaviour_"+pa.getBehaviour().getNome(), idTransazione);
							}
						}
					
						// registrazione messaggio
						//  Utilizzo le utility di Memorizzazione Stateless
						Busta bustaNewMessaggio = behaviourForwardTo.getBusta();
						if(bustaNewMessaggio==null){
							bustaNewMessaggio = busta.newInstance();
							bustaNewMessaggio.setID(CostantiPdD.PREFIX_MESSAGGIO_CONNETTORE_MULTIPLO+i+CostantiPdD.SEPARATOR_MESSAGGIO_CONNETTORE_MULTIPLO+busta.getID());
						}
						bustaNewMessaggio.setRiferimentoMessaggio(busta.getID()); // per il timer
						bustaNewMessaggio.addProperty(CostantiPdD.KEY_DESCRIZIONE_BEHAVIOUR, behaviourForwardTo.getDescription());
						IOpenSPCoopState stateNuoviMessaggi = this.openSPCoopState;
						if(stateBehaviour_newConnection!=null){
							stateNuoviMessaggi = stateBehaviour_newConnection;
						}
						else if(stateBehaviour_onlyUseConnectionFalse!=null) {
							stateNuoviMessaggi = stateBehaviour_onlyUseConnectionFalse;
						}
						GestoreMessaggi gestoreNewMessaggio = new GestoreMessaggi(stateNuoviMessaggi,true, bustaNewMessaggio.getID(),
								Costanti.INBOX,this.msgDiag,this.pddContext);
						RepositoryBuste repositoryBusteNewMessaggio = new RepositoryBuste(stateNuoviMessaggi.getStatoRichiesta(),true, this.protocolFactory);
						
						OpenSPCoop2Message messaggioDaNotificare = behaviourForwardTo.getMessage();
						if(tipiMessaggiNotificabili!=null && MessaggioDaNotificare.RISPOSTA.equals(tipiMessaggiNotificabili)) {
							// verrà salvata dopo, la richiesta salvata deve essere un messaggio trascurabile
							messaggioDaNotificare = messaggioDaNotificare.getFactory().createEmptyMessage(MessageType.BINARY, MessageRole.NONE);
						}
						
						Timestamp oraRegistrazione = _forceSaveMessage(this.propertiesReader, gestoreNewMessaggio, null, bustaNewMessaggio, richiestaApplicativa, repositoryBusteNewMessaggio,
								messaggioDaNotificare,stateNuoviMessaggi,
								this.pddContext, this.identitaPdD); // per eventuale thread riconsegna in caso di errore
	//					if(stateless){
	//						((OpenSPCoopStateless)this.openSPCoopState).setRichiestaMsg(behaviourForwardTo.getMessage());
	//					}
											
						// Inoltro messaggio
						_sendMessageToServiziApplicativi(serviziApplicativiAbilitatiForwardTo, soggettiRealiMappatiInUnSoggettoVirtuale, 
								richiestaApplicativa, localForwardRichiestaDelegata, gestoreNewMessaggio, bustaNewMessaggio, pa, repositoryBusteNewMessaggio,
								busta.getID(),behaviourForwardTo.getConfig(), stateless, stateNuoviMessaggi, (stateBehaviour_newConnection!=null),
								behaviourForwardTo.getMessage(),
								!EFFETTUA_SPEDIZIONE_CONSEGNA_CONTENUTI, attendiEsitoTransazioneSincronaPrimaDiSpedire,
								null, true,
								oraRegistrazione,
								requestInfo);
						
						// Applico modifiche effettuate dal modulo Consegna
						if (stateless && !this.propertiesReader.isServerJ2EE() ) {
							if(stateBehaviour_newConnection!=null) {
								if(stateBehaviour_newConnection.resourceReleased()){
									// il modulo di consegna rilascia la risorsa
									stateBehaviour_newConnection.updateResource(idTransazione);
								}
								stateBehaviour_newConnection.commit();
							}
							else if(stateBehaviour_onlyUseConnectionFalse!=null) {
								stateBehaviour_onlyUseConnectionFalse.commit();
							}
						}
						
					}finally{
						if (stateless && !this.propertiesReader.isServerJ2EE() ) {
							if(stateBehaviour_newConnection!=null){
								stateBehaviour_newConnection.releaseResource();
							}
							else if(stateBehaviour_onlyUseConnectionFalse!=null) {
								stateBehaviour_onlyUseConnectionFalse.setUseConnection(oldUseConnection);
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


	@SuppressWarnings("deprecation")
	private static void overwriteIdSoggetto(IDServizio idServizio, IDSoggetto idSoggetto){
		idServizio.setSoggettoErogatore(idSoggetto);
	}

	private void _sendMessageToServiziApplicativi(List<String> serviziApplicativi, SoggettoVirtuale soggettiRealiMappatiInUnSoggettoVirtuale,
			RichiestaApplicativa richiestaApplicativaParam, RichiestaDelegata localForwardRichiestaDelegata,
			GestoreMessaggi gestoreMessaggi, Busta busta, PortaApplicativa pa, RepositoryBuste repositoryBuste,
			String idBustaPreBehaviourNewMessage,BehaviourForwardToConfiguration behaviourForwardToConfiguration,
			boolean stateless,IOpenSPCoopState state,boolean releseaResource,
			OpenSPCoop2Message requestMessageNullable,
			boolean spedizioneConsegnaContenuti, boolean attendiEsitoTransazioneSincronaPrimaDiSpedire,
			BehaviourLoadBalancer loadBalancer, boolean presaInCarico,
			Timestamp dataRegistrazioneMessaggio,
			RequestInfo requestInfo) throws Exception{
		
		// Eventuale indicazione per la registrazione via stateless
		boolean registrazioneMessaggioPerStatelessEffettuata = false;
		
		boolean gestioneSolamenteConIntegrationManager = true;
		
		Map<String, Boolean> mapServizioApplicativoConConnettore = new HashMap<String, Boolean>();
		Map<String, Boolean> mapSbustamentoSoap = new HashMap<String, Boolean>();
		Map<String, Boolean> mapSbustamentoInformazioniProtocollo = new HashMap<String, Boolean>();
		Map<String, Boolean> mapGetMessage = new HashMap<String, Boolean>();
		Map<String, String> mapTipoConsegna = new HashMap<String, String>();
		Map<String, Timestamp> mapOraRegistrazione = new HashMap<String, Timestamp>();
		Map<String, ConsegnaContenutiApplicativiMessage> mapConsegnaContenutiApplicativiMessage = new HashMap<String, ConsegnaContenutiApplicativiMessage>();
		
		RichiestaApplicativa richiestaApplicativa = null;
		if(richiestaApplicativaParam!=null) {
			richiestaApplicativa = (RichiestaApplicativa) richiestaApplicativaParam.clone();
		}
		
		String nomePorta = null;
		if(richiestaApplicativa!=null && richiestaApplicativa.getIdPortaApplicativa()!=null){
			nomePorta = richiestaApplicativa.getIdPortaApplicativa().getNome();
		}
		
		// Fase di Registrazione
		for (String servizioApplicativo : serviziApplicativi) {

			// effettuo correzioni dovute al Soggetto Virtuale
			if(soggettiRealiMappatiInUnSoggettoVirtuale!=null){
				
				String oldDominio = richiestaApplicativa.getIDServizio().getSoggettoErogatore().getCodicePorta();
				overwriteIdSoggetto(richiestaApplicativa.getIDServizio(), soggettiRealiMappatiInUnSoggettoVirtuale.getSoggettoReale(servizioApplicativo));
				richiestaApplicativa.getIDServizio().getSoggettoErogatore().setCodicePorta(oldDominio);
				
				richiestaApplicativa.setIdPortaApplicativa(soggettiRealiMappatiInUnSoggettoVirtuale.getIDPortaApplicativa(servizioApplicativo));
				
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
			
			String coda = null;
			String priorita = null;
			boolean schedulingNonAttivo = false;
			if(presaInCarico) {
				coda = CostantiPdD.TIMER_RICONSEGNA_CONTENUTI_APPLICATIVI_CODA_DEFAULT;
				priorita = CostantiPdD.TIMER_RICONSEGNA_CONTENUTI_APPLICATIVI_PRIORITA_DEFAULT;
			}
			
			boolean gestioneTramiteConnettoriMultipli = false;
			if(idBustaPreBehaviourNewMessage!=null || behaviourForwardToConfiguration!=null) {
				gestioneTramiteConnettoriMultipli = true;
				ConsegnaContenutiApplicativiBehaviourMessage behaviourMsg = new ConsegnaContenutiApplicativiBehaviourMessage();
				behaviourMsg.setIdMessaggioPreBehaviour(idBustaPreBehaviourNewMessage);
				behaviourMsg.setBehaviourForwardToConfiguration(behaviourForwardToConfiguration);
				IdTransazioneApplicativoServer idTransazioneApplicativoServer = new IdTransazioneApplicativoServer();
				idTransazioneApplicativoServer.setIdTransazione(PdDContext.getValue(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE, this.pddContext));
				idTransazioneApplicativoServer.setServizioApplicativoErogatore(servizioApplicativo);
				if(pa!=null && pa.getServizioApplicativoList()!=null) {
					for (PortaApplicativaServizioApplicativo pasa : pa.getServizioApplicativoList()) {
						if(pasa.getNome().equals(servizioApplicativo)) {
							if(pasa.getDatiConnettore()!=null) {
								idTransazioneApplicativoServer.setConnettoreNome(pasa.getDatiConnettore().getNome());
								if(presaInCarico) {
									if(pasa.getDatiConnettore().getCoda()!=null) {
										coda = pasa.getDatiConnettore().getCoda();
									}
									if(pasa.getDatiConnettore().getPriorita()!=null) {
										priorita = pasa.getDatiConnettore().getPriorita();
									}
									if(org.openspcoop2.core.config.constants.StatoFunzionalita.DISABILITATO.equals(pasa.getDatiConnettore().getScheduling())) {
										schedulingNonAttivo = true;
									}
								}
							}
							ConfigurazioneGestioneConsegnaNotifiche configGestioneConsegna = MultiDeliverUtils.read(pasa, this.log); 
							GestioneErrore gestioneErroreBehaviour = GestioneConsegnaNotificheUtils.toGestioneErrore(configGestioneConsegna);		
							behaviourMsg.setGestioneErrore(gestioneErroreBehaviour);
							break;
						}
					}
				}
				behaviourMsg.setIdTransazioneApplicativoServer(idTransazioneApplicativoServer);
				consegnaMSG.setBehaviour(behaviourMsg);
				
				// aggiorno pddContext per evitare che eventuali salvataggi influenzino la transazione
				consegnaMSG.setPddContext(this.pddContext!=null ? (PdDContext) this.pddContext.clone() : null);
			}
			
			if(loadBalancer!=null) {
				consegnaMSG.setLoadBalancer(loadBalancer);
			}
			
			// Aggiungo costante servizio applicativo
			this.msgDiag.addKeyword(CostantiPdD.KEY_SA_EROGATORE, servizioApplicativo);
			
			// identificativo Porta Applicativa
			richiestaApplicativa.setServizioApplicativo(servizioApplicativo);
			
			IDServizioApplicativo idSA = new IDServizioApplicativo();
			idSA.setNome(servizioApplicativo);
			idSA.setIdSoggettoProprietario(richiestaApplicativa.getIDServizio().getSoggettoErogatore());
			ServizioApplicativo sappl = this.configurazionePdDReader.getServizioApplicativo(idSA, requestInfo);

			if(!presaInCarico) {
				// altrimenti il diagnostico deve finire nella parte "presa in carico"
				this.msgDiag.setServizioApplicativo(servizioApplicativo);
			}

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
			if(!servizioApplicativoConConnettore){
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
			}
			
			// Registrazione dati per la gestione del Messaggio
			String tipoConsegna = GestoreMessaggi.CONSEGNA_TRAMITE_INTEGRATION_MANAGER;
			if(servizioApplicativoConConnettore)
				tipoConsegna = GestoreMessaggi.CONSEGNA_TRAMITE_CONNETTORE;

			
			// oraRegistrazione
						
			Timestamp oraRegistrazioneMessaggio = null;
			if(dataRegistrazioneMessaggio!=null) {
				oraRegistrazioneMessaggio = dataRegistrazioneMessaggio;
			}
			else {
				oraRegistrazioneMessaggio = gestoreMessaggi.getOraRegistrazioneMessaggio();
			}

			
			// Registro Destinatario
			if(idBustaPreBehaviourNewMessage!=null){
				// Forzo oneway1.1 per farlo registrare anche se siamo in stateless puro
				gestoreMessaggi.setOneWayVersione11(true);
			}
			gestoreMessaggi.registraDestinatarioMessaggio(servizioApplicativo,
					sbustamento_soap,sbustamento_informazioni_protocollo,
					getMessageAbilitato,tipoConsegna,oraRegistrazioneMessaggio, nomePorta,
					attendiEsitoTransazioneSincronaPrimaDiSpedire, (servizioApplicativoConConnettore && !spedizioneConsegnaContenuti),
					coda, priorita, schedulingNonAttivo);
			
			mapServizioApplicativoConConnettore.put(servizioApplicativo, servizioApplicativoConConnettore);
			mapSbustamentoSoap.put(servizioApplicativo, sbustamento_soap);
			mapSbustamentoInformazioniProtocollo.put(servizioApplicativo, sbustamento_informazioni_protocollo);
			mapGetMessage.put(servizioApplicativo, getMessageAbilitato);
			mapTipoConsegna.put(servizioApplicativo, tipoConsegna);
			mapOraRegistrazione.put(servizioApplicativo, oraRegistrazioneMessaggio);
			mapConsegnaContenutiApplicativiMessage.put(servizioApplicativo, consegnaMSG);
			
			if(getMessageAbilitato && !servizioApplicativoConConnettore && !gestioneTramiteConnettoriMultipli) {
				ConsegnaContenutiApplicativiBehaviourMessage behaviourMsg = new ConsegnaContenutiApplicativiBehaviourMessage();
				//behaviourMsg.setIdMessaggioPreBehaviour(idBustaPreBehaviourNewMessage);
				//behaviourMsg.setBehaviourForwardToConfiguration(behaviourForwardToConfiguration);
				IdTransazioneApplicativoServer idTransazioneApplicativoServer = new IdTransazioneApplicativoServer();
				idTransazioneApplicativoServer.setIdTransazione(PdDContext.getValue(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE, this.pddContext));
				idTransazioneApplicativoServer.setServizioApplicativoErogatore(servizioApplicativo);
				idTransazioneApplicativoServer.setConnettoreNome(CostantiConfigurazione.NOME_CONNETTORE_DEFAULT);
				behaviourMsg.setIdTransazioneApplicativoServer(idTransazioneApplicativoServer);
				consegnaMSG.setBehaviour(behaviourMsg);		
				// aggiorno pddContext per evitare che eventuali salvataggi influenzino la transazione
				consegnaMSG.setPddContext(this.pddContext!=null ? (PdDContext) this.pddContext.clone() : null);
				if(this.pddContext!=null) {
					this.pddContext.addObject(org.openspcoop2.core.constants.Costanti.MESSAGE_BOX, "true");
				}
			}
		}
		
		
		// Applico modifiche della registrazione
		boolean registrazioneDestinatarioEffettuataPerViaBehaviour = false;
		if(idBustaPreBehaviourNewMessage!=null){
			if (stateless && !this.propertiesReader.isServerJ2EE() ) {
				state.commit();
				if(releseaResource) {
					state.releaseResource();
				}
				registrazioneDestinatarioEffettuataPerViaBehaviour = true;
			}
		}
		
		
		// Effettuo spedizione
		
		EJBUtilsMessaggioInConsegna messaggiInConsegna = new EJBUtilsMessaggioInConsegna();
			
		messaggiInConsegna.setServiziApplicativi(serviziApplicativi);
		messaggiInConsegna.setSoggettiRealiMappatiInUnSoggettoVirtuale(soggettiRealiMappatiInUnSoggettoVirtuale);
		messaggiInConsegna.setRichiestaApplicativa(richiestaApplicativa);
		
		messaggiInConsegna.setMapServizioApplicativoConConnettore(mapServizioApplicativoConConnettore);
		messaggiInConsegna.setMapSbustamentoSoap(mapSbustamentoSoap);
		messaggiInConsegna.setMapSbustamentoInformazioniProtocollo(mapSbustamentoInformazioniProtocollo);
		messaggiInConsegna.setMapGetMessage(mapGetMessage);
		messaggiInConsegna.setMapTipoConsegna(mapTipoConsegna);
		messaggiInConsegna.setMapOraRegistrazione(mapOraRegistrazione);
		messaggiInConsegna.setMapConsegnaContenutiApplicativiMessage(mapConsegnaContenutiApplicativiMessage);
		
		messaggiInConsegna.setRegistrazioneMessaggioPerStatelessEffettuata(registrazioneMessaggioPerStatelessEffettuata);
		messaggiInConsegna.setGestioneSolamenteConIntegrationManager(gestioneSolamenteConIntegrationManager);
		messaggiInConsegna.setRegistrazioneDestinatarioEffettuataPerViaBehaviour(registrazioneDestinatarioEffettuataPerViaBehaviour);
		messaggiInConsegna.setStateless(stateless);
		messaggiInConsegna.setOneWayVersione11(this.oneWayVersione11);
		
		messaggiInConsegna.setIdBustaPreBehaviourNewMessage(idBustaPreBehaviourNewMessage);
		messaggiInConsegna.setBusta(busta);
		messaggiInConsegna.setNomePorta(nomePorta);
		
		messaggiInConsegna.setRequestMessageNullable(requestMessageNullable);
		
		messaggiInConsegna.setOraRegistrazioneMessaggio(dataRegistrazioneMessaggio);
		
		if(attendiEsitoTransazioneSincronaPrimaDiSpedire) {
			this.pddContext.addObject(CostantiPdD.TIMER_RICONSEGNA_CONTENUTI_APPLICATIVI_MESSAGGI_SPEDIRE, messaggiInConsegna);
		}
		else {
			EJBUtilsMessaggioInConsegnaEsito esito = sendMessages(this.log, this.msgDiag, state, this.idSessione, 
					repositoryBuste, gestoreMessaggi, this.nodeSender, 
					this.protocolFactory, this.identitaPdD, nomePorta, messaggiInConsegna,
					spedizioneConsegnaContenuti,
					this.pddContext,
					this.configurazionePdDReader);
		
			this.gestioneSolamenteConIntegrationManager = esito.isGestioneSolamenteConIntegrationManager();
			this.gestioneStatelessConIntegrationManager = esito.isGestioneStatelessConIntegrationManager();
		}
		
	}
	
	public static EJBUtilsMessaggioInConsegnaEsito sendMessages(Logger log, MsgDiagnostico msgDiag, IOpenSPCoopState state, String idSessioneParam,
			RepositoryBuste repositoryBuste, GestoreMessaggi gestoreMessaggi, INodeSender nodeSender, 
			IProtocolFactory<?> protocolFactory,IDSoggetto identitaPdD, String idModulo,
			EJBUtilsMessaggioInConsegna message,
			boolean spedizioneConsegnaContenuti,
			PdDContext pddContext,
			ConfigurazionePdDManager configurazionePdDManager) throws Exception {
		
		RequestInfo requestInfo = null;
		if(pddContext!=null && pddContext.containsKey(org.openspcoop2.core.constants.Costanti.REQUEST_INFO)) {
			requestInfo = (RequestInfo) pddContext.getObject(org.openspcoop2.core.constants.Costanti.REQUEST_INFO);
		}
		
		EJBUtilsMessaggioInConsegnaEsito esito = new EJBUtilsMessaggioInConsegnaEsito();
		
		OpenSPCoop2Properties propertiesReader = OpenSPCoop2Properties.getInstance();
		
		List<String> serviziApplicativi = message.getServiziApplicativi();
		SoggettoVirtuale soggettiRealiMappatiInUnSoggettoVirtuale = message.getSoggettiRealiMappatiInUnSoggettoVirtuale();
		RichiestaApplicativa richiestaApplicativa = message.getRichiestaApplicativa();
		
		Map<String, Boolean> mapServizioApplicativoConConnettore = message.getMapServizioApplicativoConConnettore();
		Map<String, Boolean> mapSbustamentoSoap = message.getMapSbustamentoSoap();
		Map<String, Boolean> mapSbustamentoInformazioniProtocollo = message.getMapSbustamentoInformazioniProtocollo();
		Map<String, Boolean> mapGetMessage = message.getMapGetMessage();
		Map<String, String> mapTipoConsegna = message.getMapTipoConsegna();
		Map<String, Timestamp> mapOraRegistrazione = message.getMapOraRegistrazione();
		Map<String, ConsegnaContenutiApplicativiMessage> mapConsegnaContenutiApplicativiMessage = message.getMapConsegnaContenutiApplicativiMessage();
		
		boolean registrazioneMessaggioPerStatelessEffettuata = message.isRegistrazioneMessaggioPerStatelessEffettuata();
		boolean gestioneSolamenteConIntegrationManager = message.isGestioneSolamenteConIntegrationManager();
		boolean registrazioneDestinatarioEffettuataPerViaBehaviour = message.isRegistrazioneDestinatarioEffettuataPerViaBehaviour();
		boolean stateless = message.isStateless();
		boolean oneWayVersione11 = message.isOneWayVersione11();
		
		String idBustaPreBehaviourNewMessage = message.getIdBustaPreBehaviourNewMessage();
		Busta busta = message.getBusta();
		String nomePorta = message.getNomePorta();
		
		OpenSPCoop2Message requestMessageNullable = message.getRequestMessageNullable();
		
		Date dataRegistrazioneMessaggio = message.getOraRegistrazioneMessaggio();
		
		// Fase di Consegna
		for (String servizioApplicativo : serviziApplicativi) {
			
			// effettuo correzioni dovute al Soggetto Virtuale
			if(soggettiRealiMappatiInUnSoggettoVirtuale!=null){
				
				String oldDominio = richiestaApplicativa.getIDServizio().getSoggettoErogatore().getCodicePorta();
				overwriteIdSoggetto(richiestaApplicativa.getIDServizio(), soggettiRealiMappatiInUnSoggettoVirtuale.getSoggettoReale(servizioApplicativo));
				richiestaApplicativa.getIDServizio().getSoggettoErogatore().setCodicePorta(oldDominio);
				
				richiestaApplicativa.setIdPortaApplicativa(soggettiRealiMappatiInUnSoggettoVirtuale.getIDPortaApplicativa(servizioApplicativo));
				
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
			msgDiag.addKeyword(CostantiPdD.KEY_SA_EROGATORE, servizioApplicativo);
			
			// identificativo Porta Applicativa
			richiestaApplicativa.setServizioApplicativo(servizioApplicativo);
						
			// Preparo TransazioneApplicativoServer transazioneApplicativoServer
			TransazioneApplicativoServer transazioneApplicativoServer = null;
			if(consegnaMSG.getBehaviour()!=null && consegnaMSG.getBehaviour().getIdTransazioneApplicativoServer()!=null) {
				
				transazioneApplicativoServer = new TransazioneApplicativoServer();
				transazioneApplicativoServer.setIdTransazione(consegnaMSG.getBehaviour().getIdTransazioneApplicativoServer().getIdTransazione());
				transazioneApplicativoServer.setServizioApplicativoErogatore(consegnaMSG.getBehaviour().getIdTransazioneApplicativoServer().getServizioApplicativoErogatore());
				transazioneApplicativoServer.setConnettoreNome(consegnaMSG.getBehaviour().getIdTransazioneApplicativoServer().getConnettoreNome());
				if(dataRegistrazioneMessaggio!=null) {
					transazioneApplicativoServer.setDataRegistrazione(dataRegistrazioneMessaggio);
				}
				else {
					transazioneApplicativoServer.setDataRegistrazione(DateManager.getDate());
				}
				transazioneApplicativoServer.setProtocollo(protocolFactory.getProtocol());
				transazioneApplicativoServer.setDataAccettazioneRichiesta(DateManager.getDate());
				if(requestMessageNullable!=null) {
					transazioneApplicativoServer.setRichiestaUscitaBytes(requestMessageNullable.getOutgoingMessageContentLength());
				}
				transazioneApplicativoServer.setIdentificativoMessaggio(consegnaMSG.getBusta().getID());
				transazioneApplicativoServer.setClusterIdPresaInCarico(propertiesReader.getClusterId(false));
				transazioneApplicativoServer.setConsegnaTrasparente(servizioApplicativoConConnettore);
				transazioneApplicativoServer.setConsegnaIntegrationManager(getMessageAbilitato);
			
				if(transazioneApplicativoServer!=null) {
					
					try {
						GestoreConsegnaMultipla.getInstance().safeCreate(transazioneApplicativoServer, richiestaApplicativa.getIdPortaApplicativa(), state, requestInfo);
					}catch(Throwable t) {
						log.error("["+transazioneApplicativoServer.getIdTransazione()+"]["+transazioneApplicativoServer.getServizioApplicativoErogatore()+"] Errore durante il salvataggio delle informazioni relative al servizio applicativo: "+t.getMessage(),t);
					}
					
				}
			}
			
			if(transazioneApplicativoServer==null) {
				// altrimenti il diagnostico deve finire nella parte "presa in carico"
				msgDiag.setServizioApplicativo(servizioApplicativo);
			}
			
			// Spedisco al modulo ConsegnaContenutiApplicativi, solo se e' presente una definizione di servizio-applicativo
			if(servizioApplicativoConConnettore){
				
				gestioneSolamenteConIntegrationManager = false;
				
				consegnaMSG.setRichiestaApplicativa(richiestaApplicativa);

				if(spedizioneConsegnaContenuti) {
					if (!stateless || 
							(propertiesReader.isServerJ2EE() && idBustaPreBehaviourNewMessage!=null) 
						) {
						if ( oneWayVersione11 ) {
							OpenSPCoopStateless statelessSerializzabile = ((OpenSPCoopStateless) state).rendiSerializzabile();
							consegnaMSG.setOpenspcoopstate(statelessSerializzabile);
						}
				
						try{
							String idSessione = idSessioneParam;
							if(idBustaPreBehaviourNewMessage!=null){
								idSessione = busta.getID();
							}
							nodeSender.send(consegnaMSG, org.openspcoop2.pdd.mdb.ConsegnaContenutiApplicativi.ID_MODULO, msgDiag, 
									identitaPdD, idModulo, idSessione, gestoreMessaggi);
						} catch (Exception e) {	
							msgDiag.logErroreGenerico(e, "EJBUtils.sendToConsegnaContenutiApplicativi(RichiestaApplicativa).senderJMS");
							throw e;
						}
					}
	
					else {
						((OpenSPCoopStateless)state).setMessageLib(consegnaMSG);
						((OpenSPCoopStateless)state).setDestinatarioRequestMsgLib(ConsegnaContenutiApplicativi.ID_MODULO);
								
						if(idBustaPreBehaviourNewMessage!=null){						
							ConsegnaContenutiApplicativi lib = new ConsegnaContenutiApplicativi(log);
							EsitoLib result = lib.onMessage(state, configurazionePdDManager);
							log.debug("Invocato ConsegnaContenutiApplicativi per ["+busta.getID()+"] con esito: "+result.getStatoInvocazione(),result.getErroreNonGestito());
						}
					}
				}
				else {
					
					if(transazioneApplicativoServer!=null) {
						msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI,"queue.messaggioSchedulato");
					}
					
				}
				
			}else{
				
				if( !registrazioneDestinatarioEffettuataPerViaBehaviour && (state instanceof OpenSPCoopStateless) && (oneWayVersione11==false) ){
					esito.setGestioneStatelessConIntegrationManager(true);
					if(idBustaPreBehaviourNewMessage==null && registrazioneMessaggioPerStatelessEffettuata==false){
						//  Memorizzazione Stateless
						_forceSaveMessage(propertiesReader, gestoreMessaggi, gestoreMessaggi.getOraRegistrazioneMessaggio(), busta, richiestaApplicativa, repositoryBuste,
								((OpenSPCoopStateless)state).getRichiestaMsg(),state,
								pddContext, identitaPdD);
						registrazioneMessaggioPerStatelessEffettuata=true;
					}
					// Forzo oneway1.1 per farlo registrare anche se siamo in stateless puro
					gestoreMessaggi.setOneWayVersione11(true);
					gestoreMessaggi.registraDestinatarioMessaggio(servizioApplicativo,
							sbustamento_soap,sbustamento_informazioni_protocollo,
							getMessageAbilitato,tipoConsegna,oraRegistrazioneMessaggio,
							nomePorta, false, false,
							null, null, false);
					gestoreMessaggi.setOneWayVersione11(false);
				}

				msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI,"integrationManager.messaggioDisponibile");
				
			}
		}
		
		esito.setGestioneSolamenteConIntegrationManager(gestioneSolamenteConIntegrationManager);
		
		return esito;

	}


	private static java.sql.Timestamp _forceSaveMessage(OpenSPCoop2Properties propertiesReader, GestoreMessaggi gestoreMessaggi, Timestamp oraRegistrazione,
			Busta busta,RichiestaApplicativa richiestaApplicativa,RepositoryBuste repositoryBuste,
			OpenSPCoop2Message message,IOpenSPCoopState state,
			PdDContext pddContext, IDSoggetto identitaPdD) throws Exception{
		
		if( state.resourceReleased()) {
			((OpenSPCoopState)state).setUseConnection(true);
			String idTransazione = (String) pddContext.getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE);
			((OpenSPCoopState)state).initResource(identitaPdD, "EJBUtils.saveMessage", idTransazione);
			gestoreMessaggi.updateOpenSPCoopState(state);
			repositoryBuste.updateState(state.getStatoRichiesta());
		}
		
		java.sql.Timestamp oraRegistrazioneT = gestoreMessaggi.registraInformazioniMessaggio_statelessEngine(oraRegistrazione, 
				ConsegnaContenutiApplicativi.ID_MODULO,busta.getRiferimentoMessaggio(),
				richiestaApplicativa.getIdCorrelazioneApplicativa(),null);
		boolean consumeMessage= true;
		gestoreMessaggi.registraMessaggio_statelessEngine(message, !consumeMessage, oraRegistrazioneT); // senno il dump poi successivo non funziona
		String key = "INSERT RegistrazioneBustaForHistory"+Costanti.INBOX+"_"+busta.getID();
		if(repositoryBuste.isRegistrataIntoInBox(busta.getID())){
			repositoryBuste.aggiornaBustaIntoInBox(busta,propertiesReader.getRepositoryIntervalloScadenzaMessaggi(),true);
			repositoryBuste.impostaUtilizzoPdD(busta.getID(), Costanti.INBOX);
		}
		else if(((StateMessage)state.getStatoRichiesta()).getPreparedStatement().containsKey(key)){
			repositoryBuste.aggiornaBustaIntoInBox(busta,propertiesReader.getRepositoryIntervalloScadenzaMessaggi(),true);
			repositoryBuste.impostaUtilizzoPdD(busta.getID(), Costanti.INBOX);
		}else{
			repositoryBuste.registraBustaIntoInBox(busta,propertiesReader.getRepositoryIntervalloScadenzaMessaggi(), true);
			if(!(state instanceof OpenSPCoopStateful)){
				if(busta.sizeProperties()>0){
					Map<String, String> bustaProperties = new HashMap<String, String>();
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
		return oraRegistrazioneT;
	}














	/** ------------------- Metodi per la spedizione di risposte con errori ---------------- */

	/* ---- PROCESSAMENTO --- */
	
	public void sendAsRispostaBustaErroreProcessamento(String idModuloInAttesa,Busta busta,
			ErroreIntegrazione errore,String idCorrelazioneApplicativa,
			String servizioApplicativoFruitore,
			Throwable eProcessamento, ParseException parseException)throws EJBUtilsException,ProtocolException{ 
		Eccezione ecc = new Eccezione(ErroriCooperazione.ERRORE_GENERICO_PROCESSAMENTO_MESSAGGIO.getErroreProcessamento(errore.getDescrizione(this.protocolFactory)),
				false,this.idModulo,this.protocolFactory);
		List<Eccezione> errs = new ArrayList<Eccezione>();
		errs.add(ecc);
		this._sendAsRispostaBustaErroreProcessamento(idModuloInAttesa,busta,errs,errore,idCorrelazioneApplicativa,null,servizioApplicativoFruitore,eProcessamento,parseException,null);
	}
	public void sendAsRispostaBustaErroreProcessamento(String idModuloInAttesa,Busta busta,
			ErroreIntegrazione errore,
			String idCorrelazioneApplicativa,String idCorrelazioneApplicativaRisposta,
			String servizioApplicativoFruitore,
			Throwable eProcessamento, ParseException parseException)throws EJBUtilsException,ProtocolException{ 
		Eccezione ecc = new Eccezione(ErroriCooperazione.ERRORE_GENERICO_PROCESSAMENTO_MESSAGGIO.getErroreProcessamento(errore.getDescrizione(this.protocolFactory)),false,this.idModulo,this.protocolFactory);
		List<Eccezione> errs = new ArrayList<Eccezione>();
		errs.add(ecc);
		this._sendAsRispostaBustaErroreProcessamento(idModuloInAttesa,busta,errs,errore,idCorrelazioneApplicativa,idCorrelazioneApplicativaRisposta,servizioApplicativoFruitore,eProcessamento,parseException,null);
	}
	public void sendAsRispostaBustaErroreProcessamento(String idModuloInAttesa,Busta busta,
			List<Eccezione> errs,String idCorrelazioneApplicativa,String idCorrelazioneApplicativaRisposta,
			String servizioApplicativoFruitore,
			Throwable eProcessamento, ParseException parseException)throws EJBUtilsException,ProtocolException{ 
		this._sendAsRispostaBustaErroreProcessamento(idModuloInAttesa, busta, errs, null, 
				idCorrelazioneApplicativa, idCorrelazioneApplicativaRisposta, servizioApplicativoFruitore, eProcessamento, parseException, null);
	}
	
	public void sendAsRispostaBustaErroreProcessamento(String idModuloInAttesa,Busta busta,
			String idCorrelazioneApplicativa,
			String servizioApplicativoFruitore,
			OpenSPCoop2Message errorMessageParam, String errorDetail)throws EJBUtilsException,ProtocolException{ 
		Eccezione ecc = new Eccezione(ErroriCooperazione.ERRORE_GENERICO_PROCESSAMENTO_MESSAGGIO.getErroreProcessamento(errorDetail),
				false,this.idModulo,this.protocolFactory);
		List<Eccezione> errs = new ArrayList<Eccezione>();
		errs.add(ecc);
		this._sendAsRispostaBustaErroreProcessamento(idModuloInAttesa,busta,errs,null,idCorrelazioneApplicativa,null,servizioApplicativoFruitore,null,null,errorMessageParam);
	}
	public void sendAsRispostaBustaErroreProcessamento(String idModuloInAttesa,Busta busta,
			String idCorrelazioneApplicativa,String idCorrelazioneApplicativaRisposta,
			String servizioApplicativoFruitore,
			OpenSPCoop2Message errorMessageParam, String errorDetail)throws EJBUtilsException,ProtocolException{ 
		Eccezione ecc = new Eccezione(ErroriCooperazione.ERRORE_GENERICO_PROCESSAMENTO_MESSAGGIO.getErroreProcessamento(errorDetail),false,this.idModulo,this.protocolFactory);
		List<Eccezione> errs = new ArrayList<Eccezione>();
		errs.add(ecc);
		this._sendAsRispostaBustaErroreProcessamento(idModuloInAttesa,busta,errs,null,idCorrelazioneApplicativa,idCorrelazioneApplicativaRisposta,servizioApplicativoFruitore,null,null,errorMessageParam);
	}
	public void sendAsRispostaBustaErroreProcessamento(String idModuloInAttesa,Busta busta,
			List<Eccezione> errs,String idCorrelazioneApplicativa,String idCorrelazioneApplicativaRisposta,
			String servizioApplicativoFruitore,
			OpenSPCoop2Message errorMessageParam)throws EJBUtilsException,ProtocolException{ 
		this._sendAsRispostaBustaErroreProcessamento(idModuloInAttesa, busta, errs, null, 
				idCorrelazioneApplicativa, idCorrelazioneApplicativaRisposta, servizioApplicativoFruitore, null,null,errorMessageParam);
	}
	
	private void _sendAsRispostaBustaErroreProcessamento(String idModuloInAttesa,Busta busta,
			List<Eccezione> errs, ErroreIntegrazione errore, 
			String idCorrelazioneApplicativa,String idCorrelazioneApplicativaRisposta,
			String servizioApplicativoFruitore,
			Throwable eProcessamento, ParseException parseException,
			OpenSPCoop2Message errorMessageParam)throws EJBUtilsException,ProtocolException{ 

		if(this.pddContext==null) {
			throw new EJBUtilsException("PddContext undefined");
		}
		
		String idTransazione = (String) this.pddContext.getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE);

		//Costruisco busta Errore 
		Imbustamento imbustatore = new Imbustamento(this.log,this.protocolFactory,this.openSPCoopState.getStatoRichiesta());

		String id_bustaErrore = 
			imbustatore.buildID(this.identitaPdD, idTransazione, 
					this.propertiesReader.getGestioneSerializableDB_AttesaAttiva(),
					this.propertiesReader.getGestioneSerializableDB_CheckInterval(),
					RuoloMessaggio.RISPOSTA);

		//ErroreProcessamentoProtocollo: Header
		busta = this.generatoreErrorePortaApplicativa.getImbustamentoErrore().buildMessaggioErroreProtocollo_Processamento(errs,busta,id_bustaErrore,this.propertiesReader.getTipoTempoBusta(this.implementazionePdDSoggettoMittente));

		// Fix Bug 131: eccezione di processamento
		this.generatoreErrorePortaApplicativa.getImbustamentoErrore().gestioneListaEccezioniMessaggioErroreProtocolloProcessamento(busta);
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
		OpenSPCoop2Message errorMsg = null;
		if(errorMessageParam!=null) {
			errorMsg = errorMessageParam;
		}
		else {
			if(errore==null) {
				DettaglioEccezione dettaglioEccezione = this.dettaglioBuilder.buildDettaglioEccezioneFromBusta(this.identitaPdD,this.tipoPdD,this.idModulo, 
						this.servizioApplicativoErogatore, busta, eProcessamento);
				errorMsg = this.generatoreErrorePortaApplicativa.buildErroreProcessamento(this.pddContext, this.getIntegrationFunctionErrorPortaApplicativa(false), dettaglioEccezione);
			}
			else {
				errorMsg = this.generatoreErrorePortaApplicativa.buildErroreProcessamento(this.pddContext, this.getIntegrationFunctionErrorPortaApplicativa(false), errore, eProcessamento);
			}			
			if(errorMsg == null){
				throw new EJBUtilsException("EJBUtils.sendRispostaErroreProcessamentoProtocollo error: Costruzione Msg Errore Protocollo fallita.");
			}
		}
		IntegrationFunctionError integrationFunctionErrorPortaApplicativa = getIntegrationFunctionErrorPortaApplicativa(false);
		if(integrationFunctionErrorPortaApplicativa!=null && 
				(
						IntegrationFunctionError.SERVICE_UNAVAILABLE.equals(integrationFunctionErrorPortaApplicativa)
						||
						IntegrationFunctionError.ENDPOINT_REQUEST_TIMED_OUT.equals(integrationFunctionErrorPortaApplicativa)
				)
			) {
			// Retry-After
			boolean isEnabled = this.propertiesReader.isEnabledServiceUnavailableRetryAfter_pa_connectionFailed();
			Integer retryAfterSeconds = this.propertiesReader.getServiceUnavailableRetryAfterSeconds_pa_connectionFailed();
			Integer retryAfterBackOffSeconds = this.propertiesReader.getServiceUnavailableRetryAfterSeconds_randomBackoff_pa_connectionFailed();
			if(	isEnabled &&
				retryAfterSeconds!=null && retryAfterSeconds>0) {
				int seconds = retryAfterSeconds;
				if(retryAfterBackOffSeconds!=null && retryAfterBackOffSeconds>0) {
					seconds = seconds + ServicesUtils.getRandom().nextInt(retryAfterBackOffSeconds);
				}
				errorMsg.forceTransportHeader(HttpConstants.RETRY_AFTER, seconds+"");
			}
		}
		if(eProcessamento!=null && eProcessamento instanceof HandlerException){
			HandlerException he = (HandlerException) eProcessamento;
			he.customized(errorMsg);
		}
		if(parseException!=null){
			errorMsg.setParseException(parseException);
			if(this.pddContext!=null) {
				if(MessageRole.REQUEST.equals(parseException.getMessageRole())){
					this.pddContext.addObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RICHIESTA_NON_RICONOSCIUTO, true);
					this.pddContext.addObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RICHIESTA_NON_RICONOSCIUTO_PARSE_EXCEPTION,	parseException);
					errorMsg.addContextProperty(org.openspcoop2.core.constants.Costanti.CONTENUTO_RICHIESTA_NON_RICONOSCIUTO, true);
					errorMsg.addContextProperty(org.openspcoop2.core.constants.Costanti.CONTENUTO_RICHIESTA_NON_RICONOSCIUTO_PARSE_EXCEPTION, parseException);
				}
				else if(MessageRole.RESPONSE.equals(parseException.getMessageRole())){
					this.pddContext.addObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RISPOSTA_NON_RICONOSCIUTO, true);
					this.pddContext.addObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RISPOSTA_NON_RICONOSCIUTO_PARSE_EXCEPTION, parseException);
					errorMsg.addContextProperty(org.openspcoop2.core.constants.Costanti.CONTENUTO_RISPOSTA_NON_RICONOSCIUTO, true);
					errorMsg.addContextProperty(org.openspcoop2.core.constants.Costanti.CONTENUTO_RISPOSTA_NON_RICONOSCIUTO_PARSE_EXCEPTION, parseException);
				}
			}
		}
		sendAsRispostaBustaErrore(idModuloInAttesa,busta,errorMsg,false,idCorrelazioneApplicativa,idCorrelazioneApplicativaRisposta,servizioApplicativoFruitore);
	}

	
	/* ---- VALIDAZIONE --- */

	public void sendAsRispostaBustaErroreValidazione(String idModuloInAttesa,Busta busta,Eccezione eccezione,
			String idCorrelazioneApplicativa,String servizioApplicativoFruitore)throws EJBUtilsException,ProtocolException{ 
		List<Eccezione> v = new ArrayList<Eccezione>();
		v.add(eccezione);
		this._sendAsRispostaBustaErroreValidazione(idModuloInAttesa,busta,v,idCorrelazioneApplicativa,null,servizioApplicativoFruitore);
	}
	public void sendAsRispostaBustaErroreValidazione(String idModuloInAttesa,Busta busta,Eccezione eccezione,
			String idCorrelazioneApplicativa,String idCorrelazioneApplicativaRisposta,String servizioApplicativoFruitore)throws EJBUtilsException,ProtocolException{ 
		List<Eccezione> v = new ArrayList<Eccezione>();
		v.add(eccezione);
		this._sendAsRispostaBustaErroreValidazione(idModuloInAttesa,busta,v,idCorrelazioneApplicativa,idCorrelazioneApplicativaRisposta,servizioApplicativoFruitore);
	}
	public void sendAsRispostaBustaErroreValidazione(String idModuloInAttesa,Busta busta,List<Eccezione> eccezioni,
			String idCorrelazioneApplicativa,String idCorrelazioneApplicativaRisposta,String servizioApplicativoFruitore)throws EJBUtilsException,ProtocolException{ 
		this._sendAsRispostaBustaErroreValidazione(idModuloInAttesa, busta, eccezioni,  
				idCorrelazioneApplicativa, idCorrelazioneApplicativaRisposta, servizioApplicativoFruitore);
	}
	private void _sendAsRispostaBustaErroreValidazione(String idModuloInAttesa,Busta busta,
			List<Eccezione> eccezioni,
			String idCorrelazioneApplicativa,String idCorrelazioneApplicativaRisposta,String servizioApplicativoFruitore)throws EJBUtilsException,ProtocolException{ 

		//Costruisco busta Errore 
		Imbustamento imbustatore = new Imbustamento(this.log,this.protocolFactory,this.openSPCoopState.getStatoRichiesta());
		String id_bustaErrore = 
			imbustatore.buildID(this.identitaPdD, 
					(String) this.pddContext.getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE), 
					this.propertiesReader.getGestioneSerializableDB_AttesaAttiva(),
					this.propertiesReader.getGestioneSerializableDB_CheckInterval(),
					RuoloMessaggio.RISPOSTA);

		// CodiceErrore (da fare prima di imbustamentoErrore che svuola la lista eccezioni)
		CodiceErroreCooperazione codiceErroreCooperazione = null;
		String descrizioneErroreCooperazione = null;
		if(eccezioni!=null && eccezioni.size()>0) {
			Eccezione eccezioneDaInviare = Eccezione.getEccezioneValidazione(ErroriCooperazione.ERRORE_GENERICO_PROTOCOLLO_NON_CORRETTO.getErroreCooperazione(), this.protocolFactory);
			if(eccezioni.size()>1){
				ITraduttore traduttore = this.protocolFactory.createTraduttore();
				StringBuilder bfDescrizione = new StringBuilder();
				for(int k=0; k<eccezioni.size();k++){
					Eccezione error = eccezioni.get(k);
					if(error.getDescrizione(this.protocolFactory)!=null) {
						bfDescrizione.append("["+traduttore.toString(error.getCodiceEccezione(),error.getSubCodiceEccezione())+"] "+error.getDescrizione(this.protocolFactory)+"\n");
					}
				}
				if(bfDescrizione.length()>0)
					eccezioneDaInviare.setDescrizione(bfDescrizione.toString());
			}else{
				eccezioneDaInviare = eccezioni.get(0);
			}
			codiceErroreCooperazione = eccezioneDaInviare.getCodiceEccezione();
			descrizioneErroreCooperazione = eccezioneDaInviare.getDescrizione(this.protocolFactory);
		}
		
		//ErroreValidazioneProtocollo: Header
		busta = this.generatoreErrorePortaApplicativa.getImbustamentoErrore().
				buildMessaggioErroreProtocollo_Validazione(eccezioni,busta,id_bustaErrore,this.propertiesReader.getTipoTempoBusta(this.implementazionePdDSoggettoMittente));	
		if( !( this.identitaPdD.getNome().equals(busta.getMittente()) && 
				this.identitaPdD.getTipo().equals(busta.getTipoMittente()) ) ){
			// Il mittente della busta che sara' spedita e' il router
			busta.setMittente(this.identitaPdD.getNome());
			busta.setTipoMittente(this.identitaPdD.getTipo());
			busta.setIdentificativoPortaMittente(this.identitaPdD.getCodicePorta());
			busta.setIndirizzoMittente(null);
		}

		//ErroreValidazioneProtocollo: Msg
		OpenSPCoop2Message msg = null;
		if(codiceErroreCooperazione!=null && descrizioneErroreCooperazione!=null) {
			msg = this.generatoreErrorePortaApplicativa.buildErroreIntestazione(this.pddContext, this.getIntegrationFunctionErrorPortaApplicativa(true), codiceErroreCooperazione, descrizioneErroreCooperazione);
		}
		else {
			msg = this.generatoreErrorePortaApplicativa.buildErroreIntestazione(this.pddContext, this.getIntegrationFunctionErrorPortaApplicativa(true));
		}
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
	public void sendAsRispostaBustaErrore_inoltroSegnalazioneErrore(Busta busta,List<Eccezione> eccezioni)throws EJBUtilsException,ProtocolException{ 
		
		//String idTransazione = (String) this.pddContext.getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE);
		// Fix devo utilizzare un id di transazione differente
		String idTransazione = null;
		try {
			idTransazione = UniqueIdentifierManager.newUniqueIdentifier().getAsString();
		}catch(Exception e) {
			throw new EJBUtilsException(e.getMessage(),e);
		}

		//Costruisco busta Errore 
		Imbustamento imbustatore = new Imbustamento(this.log, this.protocolFactory,this.openSPCoopState.getStatoRichiesta());
		String id_bustaErrore = 
			imbustatore.buildID(this.identitaPdD, idTransazione, 
					this.propertiesReader.getGestioneSerializableDB_AttesaAttiva(),
					this.propertiesReader.getGestioneSerializableDB_CheckInterval(),
					RuoloMessaggio.RISPOSTA);

		// CodiceErrore (da fare prima di imbustamentoErrore che svuola la lista eccezioni)
		CodiceErroreCooperazione codiceErroreCooperazione = null;
		String descrizioneErroreCooperazione = null;
		if(eccezioni!=null && eccezioni.size()>0) {
			Eccezione eccezioneDaInviare = Eccezione.getEccezioneValidazione(ErroriCooperazione.ERRORE_GENERICO_PROTOCOLLO_NON_CORRETTO.getErroreCooperazione(), this.protocolFactory);
			if(eccezioni.size()>1){
				ITraduttore traduttore = this.protocolFactory.createTraduttore();
				StringBuilder bfDescrizione = new StringBuilder();
				for(int k=0; k<eccezioni.size();k++){
					Eccezione error = eccezioni.get(k);
					if(error.getDescrizione(this.protocolFactory)!=null) {
						bfDescrizione.append("["+traduttore.toString(error.getCodiceEccezione(),error.getSubCodiceEccezione())+"] "+error.getDescrizione(this.protocolFactory)+"\n");
					}
				}
				if(bfDescrizione.length()>0)
					eccezioneDaInviare.setDescrizione(bfDescrizione.toString());
			}else{
				eccezioneDaInviare = eccezioni.get(0);
			}
			codiceErroreCooperazione = eccezioneDaInviare.getCodiceEccezione();
			descrizioneErroreCooperazione = eccezioneDaInviare.getDescrizione(this.protocolFactory);
		}
		
		//ErroreValidazioneProtocollo: Header
		// L'inoltro di segnalazione avviene in SbustamentoRisposte quindi devo riferire l'implemenazione della PdD del Soggetto Destinatario
		busta = this.generatoreErrorePortaApplicativa.getImbustamentoErrore().
				buildMessaggioErroreProtocollo_Validazione(eccezioni,busta,id_bustaErrore,this.propertiesReader.getTipoTempoBusta(this.implementazionePdDSoggettoDestinatario));	

		//ErroreValidazioneProtocollo: Msg
		OpenSPCoop2Message msg = null;
		if(codiceErroreCooperazione!=null && descrizioneErroreCooperazione!=null) {
			msg = this.generatoreErrorePortaApplicativa.buildErroreIntestazione(this.pddContext, this.getIntegrationFunctionErrorPortaApplicativa(false), codiceErroreCooperazione, descrizioneErroreCooperazione);
		}
		else {
			msg = this.generatoreErrorePortaApplicativa.buildErroreIntestazione(this.pddContext, this.getIntegrationFunctionErrorPortaApplicativa(false));
		}
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
	 * messaggio di tipo {@link org.openspcoop2.pdd.services.core.RicezioneBusteMessage} e 
	 * spedendolo nella coda 'toRicezioneBusteXXX'.
	 * La spedizione viene naturalmente effettuata solo se <var>idModuloInAttesa</var> e' diverso da null.
	 * 
	 * @param idModuloInAttesa identificativo del modulo 'RicezioneBusteXXX'. 
	 * 
	 */
	public GestoreMessaggi sendSbloccoRicezioneBuste(String idModuloInAttesa)throws EJBUtilsException,ProtocolException{
		
		String idTransazione = (String) this.pddContext.getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE);
		RequestInfo requestInfo = (RequestInfo) this.pddContext.getObject(org.openspcoop2.core.constants.Costanti.REQUEST_INFO);

		// ID Busta Sblocco
		org.openspcoop2.protocol.engine.builder.Imbustamento imbustatore = new Imbustamento(this.log, this.protocolFactory, this.openSPCoopState.getStatoRichiesta());
		String idSblocco =	
			imbustatore.buildID(this.identitaPdD, idTransazione, 
					this.propertiesReader.getGestioneSerializableDB_AttesaAttiva(),
					this.propertiesReader.getGestioneSerializableDB_CheckInterval(),
					RuoloMessaggio.RISPOSTA);
		return sendBustaRisposta(idModuloInAttesa,null,MessageUtilities.buildEmptyMessage(OpenSPCoop2MessageFactory.getDefaultMessageFactory(), requestInfo.getProtocolRequestMessageType(), MessageRole.RESPONSE)
				,idSblocco,null,null,null,null);
	}
	
	public GestoreMessaggi sendSOAPFault(String idModuloInAttesa,OpenSPCoop2Message msg)throws EJBUtilsException,ProtocolException{
		
		String idTransazione = (String) this.pddContext.getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE);
		
		// ID Busta Sblocco
		org.openspcoop2.protocol.engine.builder.Imbustamento imbustatore = new Imbustamento(this.log, this.protocolFactory, this.openSPCoopState.getStatoRichiesta());
		String idSblocco = 
			imbustatore.buildID(this.identitaPdD, idTransazione, 
					this.propertiesReader.getGestioneSerializableDB_AttesaAttiva(),
					this.propertiesReader.getGestioneSerializableDB_CheckInterval(),
					RuoloMessaggio.RISPOSTA);

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

		String idTransazione = (String) this.pddContext.getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE);
		
		// ID Busta Costruita
		org.openspcoop2.protocol.engine.builder.Imbustamento imbustatore = new Imbustamento(this.log, this.protocolFactory, this.openSPCoopState.getStatoRichiesta());
		String id_busta = 
			imbustatore.buildID(this.identitaPdD, idTransazione, 
					this.propertiesReader.getGestioneSerializableDB_AttesaAttiva(),
					this.propertiesReader.getGestioneSerializableDB_CheckInterval(),
					RuoloMessaggio.RISPOSTA);

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
	public OpenSPCoop2Message buildOpenSPCoopOK(MessageType messageType, String id) throws UtilsException{
		try{
			OpenSPCoop2MessageFactory mf = OpenSPCoop2MessageFactory.getDefaultMessageFactory();
						
			EsitoRichiesta esito = new EsitoRichiesta();
			esito.setMessageId(id);
			esito.setState(org.openspcoop2.core.integrazione.constants.Costanti.PRESA_IN_CARICO);
			
			switch (messageType) {
			case SOAP_11:
			case SOAP_12:
			
				OpenSPCoop2Message responseSOAPMessage = mf.createEmptyMessage(messageType,MessageRole.RESPONSE);
				OpenSPCoop2SoapMessage soapMessage = responseSOAPMessage.castAsSoap();
				SOAPBody soapBody = soapMessage.getSOAPBody();
				byte[]xmlEsito = EsitoRichiestaXMLUtils.generateEsitoRichiesta(esito);
				soapBody.addChildElement(soapMessage.createSOAPElement(xmlEsito));
				return responseSOAPMessage;
				

			case JSON:
				
				byte[] bytes = EsitoRichiestaXMLUtils.generateEsitoRichiestaAsJson(esito).getBytes();
				OpenSPCoop2MessageParseResult pr = mf.createMessage(messageType, MessageRole.RESPONSE, HttpConstants.CONTENT_TYPE_JSON, bytes);
				return pr.getMessage_throwParseException();
				
			default:
				
				bytes = EsitoRichiestaXMLUtils.generateEsitoRichiesta(esito);
				pr = mf.createMessage(MessageType.XML, MessageRole.RESPONSE, HttpConstants.CONTENT_TYPE_XML, bytes);
				return pr.getMessage_throwParseException();
				
			}
			
			

		} catch(Exception e) {
			throw new UtilsException("Creazione MsgOpenSPCoopOK non riuscito: "+e.getMessage(),e);
		}
	}


	
	
	
	
	
	






	/** ------------------- Metodi per la spedizione al nodo 'RicezioneBuste' ---------------- */

	/**
	 * Spedisce un header Protocollo <var>header</var>
	 * al modulo di openspcoop 'RicezioneBusteXXX', 
	 * inserendolo all'interno di  un
	 * messaggio di tipo {@link org.openspcoop2.pdd.services.core.RicezioneBusteMessage} e 
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
					String idT = PdDContext.getValue(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE, this.pddContext);
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
