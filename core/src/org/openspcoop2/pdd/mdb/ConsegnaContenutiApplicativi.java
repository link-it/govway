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

import java.io.ByteArrayInputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPFault;

import org.apache.commons.io.output.NullOutputStream;
import org.openspcoop2.core.config.CorrelazioneApplicativaRisposta;
import org.openspcoop2.core.config.DumpConfigurazione;
import org.openspcoop2.core.config.GestioneErrore;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.Proprieta;
import org.openspcoop2.core.config.ResponseCachingConfigurazione;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.Trasformazioni;
import org.openspcoop2.core.config.ValidazioneContenutiApplicativi;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.constants.ProprietaProtocolloValore;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.constants.CostantiConnettori;
import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.core.transazioni.TransazioneApplicativoServer;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.OpenSPCoop2RestJsonMessage;
import org.openspcoop2.message.OpenSPCoop2RestXmlMessage;
import org.openspcoop2.message.OpenSPCoop2SoapMessage;
import org.openspcoop2.message.constants.MessageRole;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.message.exception.ParseException;
import org.openspcoop2.message.soap.SoapUtils;
import org.openspcoop2.message.soap.TunnelSoapUtils;
import org.openspcoop2.message.soap.mtom.MtomXomReference;
import org.openspcoop2.message.utils.MessageUtilities;
import org.openspcoop2.pdd.config.ClassNameProperties;
import org.openspcoop2.pdd.config.ConfigurazionePdDManager;
import org.openspcoop2.pdd.config.ForwardProxy;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.config.RichiestaApplicativa;
import org.openspcoop2.pdd.config.RichiestaDelegata;
import org.openspcoop2.pdd.config.dynamic.PddPluginLoader;
import org.openspcoop2.pdd.core.AbstractCore;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.core.EJBUtils;
import org.openspcoop2.pdd.core.EJBUtilsException;
import org.openspcoop2.pdd.core.GestoreCorrelazioneApplicativa;
import org.openspcoop2.pdd.core.GestoreMessaggi;
import org.openspcoop2.pdd.core.IntegrationContext;
import org.openspcoop2.pdd.core.LocalForwardEngine;
import org.openspcoop2.pdd.core.LocalForwardException;
import org.openspcoop2.pdd.core.LocalForwardParameter;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.ProtocolContext;
import org.openspcoop2.pdd.core.ValidatoreMessaggiApplicativi;
import org.openspcoop2.pdd.core.ValidatoreMessaggiApplicativiException;
import org.openspcoop2.pdd.core.ValidatoreMessaggiApplicativiRest;
import org.openspcoop2.pdd.core.behaviour.BehaviourForwardToConfiguration;
import org.openspcoop2.pdd.core.behaviour.BehaviourLoadBalancer;
import org.openspcoop2.pdd.core.connettori.ConnettoreBase;
import org.openspcoop2.pdd.core.connettori.ConnettoreBaseHTTP;
import org.openspcoop2.pdd.core.connettori.ConnettoreMsg;
import org.openspcoop2.pdd.core.connettori.ConnettoreUtils;
import org.openspcoop2.pdd.core.connettori.GestoreErroreConnettore;
import org.openspcoop2.pdd.core.connettori.IConnettore;
import org.openspcoop2.pdd.core.connettori.InfoConnettoreUscita;
import org.openspcoop2.pdd.core.connettori.RepositoryConnettori;
import org.openspcoop2.pdd.core.handlers.GestoreHandlers;
import org.openspcoop2.pdd.core.handlers.HandlerException;
import org.openspcoop2.pdd.core.handlers.InResponseContext;
import org.openspcoop2.pdd.core.handlers.OutRequestContext;
import org.openspcoop2.pdd.core.integrazione.HeaderIntegrazione;
import org.openspcoop2.pdd.core.integrazione.IGestoreIntegrazionePA;
import org.openspcoop2.pdd.core.integrazione.IGestoreIntegrazionePASoap;
import org.openspcoop2.pdd.core.integrazione.InResponsePAMessage;
import org.openspcoop2.pdd.core.integrazione.OutRequestPAMessage;
import org.openspcoop2.pdd.core.state.IOpenSPCoopState;
import org.openspcoop2.pdd.core.state.OpenSPCoopStateDBManager;
import org.openspcoop2.pdd.core.state.OpenSPCoopStateException;
import org.openspcoop2.pdd.core.state.OpenSPCoopStateful;
import org.openspcoop2.pdd.core.state.OpenSPCoopStateless;
import org.openspcoop2.pdd.core.token.TokenForward;
import org.openspcoop2.pdd.core.transazioni.GestoreConsegnaMultipla;
import org.openspcoop2.pdd.core.transazioni.Transaction;
import org.openspcoop2.pdd.core.transazioni.TransactionContext;
import org.openspcoop2.pdd.core.trasformazioni.GestoreTrasformazioni;
import org.openspcoop2.pdd.core.trasformazioni.GestoreTrasformazioniException;
import org.openspcoop2.pdd.logger.Dump;
import org.openspcoop2.pdd.logger.MsgDiagnosticiProperties;
import org.openspcoop2.pdd.logger.MsgDiagnostico;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.pdd.services.DirectVMProtocolInfo;
import org.openspcoop2.pdd.services.ServicesUtils;
import org.openspcoop2.pdd.services.error.RicezioneBusteExternalErrorGenerator;
import org.openspcoop2.pdd.timers.TimerGestoreMessaggi;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.engine.RequestInfo;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.protocol.engine.driver.ConsegnaInOrdine;
import org.openspcoop2.protocol.engine.validator.ValidazioneSintattica;
import org.openspcoop2.protocol.registry.RegistroServiziManager;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.Riscontro;
import org.openspcoop2.protocol.sdk.builder.ProprietaManifestAttachments;
import org.openspcoop2.protocol.sdk.config.IProtocolVersionManager;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.ErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.ErroriIntegrazione;
import org.openspcoop2.protocol.sdk.constants.IntegrationFunctionError;
import org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione;
import org.openspcoop2.protocol.sdk.constants.RuoloMessaggio;
import org.openspcoop2.protocol.sdk.constants.TipoOraRegistrazione;
import org.openspcoop2.utils.LimitExceededIOException;
import org.openspcoop2.utils.LimitedInputStream;
import org.openspcoop2.utils.TimeoutIOException;
import org.openspcoop2.utils.TimeoutInputStream;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.io.Base64Utilities;
import org.openspcoop2.utils.io.notifier.NotifierInputStreamParams;
import org.openspcoop2.utils.rest.problem.JsonDeserializer;
import org.openspcoop2.utils.rest.problem.ProblemConstants;
import org.openspcoop2.utils.rest.problem.ProblemRFC7807;
import org.openspcoop2.utils.rest.problem.XmlDeserializer;
import org.openspcoop2.utils.transport.TransportRequestContext;
import org.openspcoop2.utils.transport.TransportResponseContext;
import org.openspcoop2.utils.transport.TransportUtils;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.slf4j.Logger;
import org.w3c.dom.Node;

/**
 * Contiene la libreria ConsegnaContenutiApplicativi
 *
 * @author Poli Andrea (apoli@link.it)
 * @author Tronci Fabio (tronci@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class ConsegnaContenutiApplicativi extends GenericLib {

	public static final String ID_MODULO = "ConsegnaContenutiApplicativi";

	public ConsegnaContenutiApplicativi(Logger log) throws GenericLibException {
		super(ConsegnaContenutiApplicativi.ID_MODULO, log);
		inizializza();
	}

	@Override
	protected synchronized void inizializza() throws GenericLibException {
		super.inizializza();
		ConsegnaContenutiApplicativi.initializeService(ClassNameProperties.getInstance(), this.propertiesReader);
	}


	/** IGestoreIntegrazionePA: lista di gestori, ordinati per priorita' minore */
	public static String[] defaultGestoriIntegrazionePA = null;
	private static java.util.concurrent.ConcurrentHashMap<String, String[]> defaultPerProtocolloGestoreIntegrazionePA = null;
	
	/** Indicazione se sono state inizializzate le variabili del servizio */
	public static boolean initializeService = false;

	/**
	 * Inizializzatore del servizio ConsegnaContenutiApplicativi
	 * 
	 * @throws Exception
	 */
	public synchronized static void initializeService(ClassNameProperties className,OpenSPCoop2Properties propertiesReader) {

		if(ConsegnaContenutiApplicativi.initializeService)
			return;

		boolean error = false;
		Logger logCore = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();
		PddPluginLoader pluginLoader = PddPluginLoader.getInstance();
		
		// Inizializzo IGestoreIntegrazionePA list
		ConsegnaContenutiApplicativi.defaultGestoriIntegrazionePA = propertiesReader.getTipoIntegrazionePA();
		for(int i=0; i<ConsegnaContenutiApplicativi.defaultGestoriIntegrazionePA.length; i++){
			try{
				IGestoreIntegrazionePA gestore = pluginLoader.newIntegrazionePortaApplicativa(ConsegnaContenutiApplicativi.defaultGestoriIntegrazionePA[i]);
				gestore.toString();
				logCore.info("Inizializzazione gestore dati di integrazione per le erogazioni di tipo "+
						ConsegnaContenutiApplicativi.defaultGestoriIntegrazionePA[i]+" effettuata.");
			}catch(Exception e){
				logCore.error(e.getMessage(),e);
				error = true;
			}
		}
		
		// Inizializzo IGestoreIntegrazionePA per protocollo
		ConsegnaContenutiApplicativi.defaultPerProtocolloGestoreIntegrazionePA = new java.util.concurrent.ConcurrentHashMap<String, String[]>();
		try{
			Enumeration<String> enumProtocols = ProtocolFactoryManager.getInstance().getProtocolNames();
			while (enumProtocols.hasMoreElements()) {
				String protocol = (String) enumProtocols.nextElement();
				String [] tipiIntegrazionePA = propertiesReader.getTipoIntegrazionePA(protocol);
				if(tipiIntegrazionePA!=null && tipiIntegrazionePA.length>0){
					List<String> tipiIntegrazionePerProtocollo = new ArrayList<String>();
					for (int i = 0; i < tipiIntegrazionePA.length; i++) {
						try {
							IGestoreIntegrazionePA gestore =  pluginLoader.newIntegrazionePortaApplicativa(tipiIntegrazionePA[i]);
							gestore.toString();
							tipiIntegrazionePerProtocollo.add(tipiIntegrazionePA[i]);
							logCore.info("Inizializzazione gestore dati di integrazione (protocollo: "+protocol+") per le erogazioni di tipo "+
									tipiIntegrazionePA[i]+" effettuata.");
						} catch (Exception e) {
							logCore.error(e.getMessage(),e);
							error = true;
						}
					}
					if(tipiIntegrazionePerProtocollo.size()>0){
						ConsegnaContenutiApplicativi.defaultPerProtocolloGestoreIntegrazionePA.put(protocol, tipiIntegrazionePerProtocollo.toArray(new String[1]));
					}
				}
			}
		}catch(Exception e){
			logCore.error(
					"Riscontrato errore durante l'inizializzazione dei tipi di integrazione per protocollo: "+e.getMessage(),e);
			error = true;
		}
		
		

		ConsegnaContenutiApplicativi.initializeService = !error;
	}




	/**
	 * Attivato,  quando viene ricevuto sulla coda associata al mdb (coda RequestIN_Soap)
	 * un messaggio JMS. Questo metodo implementa la logica del modulo ConsegnaContenutiApplicativi
	 * dell'infrastruttura OpenSPCoop.
	 * 
	 */
	@Override
	public EsitoLib _onMessage(IOpenSPCoopState openspcoopstate,
			RegistroServiziManager registroServiziManager,ConfigurazionePdDManager configurazionePdDManager, 
			MsgDiagnostico msgDiag) throws OpenSPCoopStateException {
		
		ConsegnaContenutiApplicativiMessage consegnaContenutiApplicativiMsg = (ConsegnaContenutiApplicativiMessage) openspcoopstate.getMessageLib();
		IDPortaApplicativa idApplicativa = null;
		if(consegnaContenutiApplicativiMsg.getRichiestaApplicativa()!=null) {
			idApplicativa = consegnaContenutiApplicativiMsg.getRichiestaApplicativa().getIdPortaApplicativa();
		}
		
		OpenSPCoopStateDBManager dbManagerSource = OpenSPCoopStateDBManager.runtime;
		
		// Costruisco eventuale oggetto TransazioneServerApplicativo
		TransazioneApplicativoServer transazioneApplicativoServer = null;
		ConsegnaContenutiApplicativiBehaviourMessage behaviourConsegna = consegnaContenutiApplicativiMsg.getBehaviour();
		Date oraRegistrazione = null;
		if(behaviourConsegna!=null && behaviourConsegna.getIdTransazioneApplicativoServer()!=null) {
			transazioneApplicativoServer = new TransazioneApplicativoServer();
			transazioneApplicativoServer.setIdTransazione(behaviourConsegna.getIdTransazioneApplicativoServer().getIdTransazione());
			transazioneApplicativoServer.setServizioApplicativoErogatore(behaviourConsegna.getIdTransazioneApplicativoServer().getServizioApplicativoErogatore());
			transazioneApplicativoServer.setConnettoreNome(behaviourConsegna.getIdTransazioneApplicativoServer().getConnettoreNome());
			if(behaviourConsegna.getOraRegistrazioneTransazioneApplicativoServer()!=null) {
				transazioneApplicativoServer.setDataRegistrazione(behaviourConsegna.getOraRegistrazioneTransazioneApplicativoServer());
				oraRegistrazione = behaviourConsegna.getOraRegistrazioneTransazioneApplicativoServer();
			}
			else {
				transazioneApplicativoServer.setDataRegistrazione(DateManager.getDate());
			}
			String protocol = (String) consegnaContenutiApplicativiMsg.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.PROTOCOL_NAME);
			transazioneApplicativoServer.setProtocollo(protocol);
			transazioneApplicativoServer.setDataAccettazioneRichiesta(DateManager.getDate());
			transazioneApplicativoServer.setIdentificativoMessaggio(consegnaContenutiApplicativiMsg.getBusta().getID());
			transazioneApplicativoServer.setConsegnaTrasparente(true);
			
			msgDiag.setTransazioneApplicativoServer(transazioneApplicativoServer, idApplicativa);
			
			dbManagerSource = OpenSPCoopStateDBManager.consegnePreseInCarico;
		}
		BehaviourLoadBalancer loadBalancer = null;
		if(consegnaContenutiApplicativiMsg!=null && consegnaContenutiApplicativiMsg.getLoadBalancer()!=null) {
			loadBalancer = consegnaContenutiApplicativiMsg.getLoadBalancer();
		}
		
		EsitoLib esitoLib = null;
		try {
			if(loadBalancer!=null) {
				try {
					loadBalancer.getLoadBalancerPool().addActiveConnection(loadBalancer.getConnectorName());
				}catch(Throwable t) {
					String prefix = "";
					if(transazioneApplicativoServer!=null) {
						prefix = "["+transazioneApplicativoServer.getIdTransazione()+"]["+transazioneApplicativoServer.getServizioApplicativoErogatore()+"] " ;
					}
					this.log.error(prefix+"Errore durante il salvataggio delle informazioni di load balancer: "+t.getMessage(),t);
				}
			}
			esitoLib = this.engine_onMessage(openspcoopstate, dbManagerSource,
					registroServiziManager, configurazionePdDManager, msgDiag, transazioneApplicativoServer, oraRegistrazione);
		}finally {
			if(loadBalancer!=null) {
				try {
					loadBalancer.getLoadBalancerPool().removeActiveConnection(loadBalancer.getConnectorName());
				}catch(Throwable t) {
					String prefix = "";
					if(transazioneApplicativoServer!=null) {
						prefix = "["+transazioneApplicativoServer.getIdTransazione()+"]["+transazioneApplicativoServer.getServizioApplicativoErogatore()+"] " ;
					}
					this.log.error(prefix+"Errore durante il salvataggio delle informazioni di load balancer: "+t.getMessage(),t);
				}
				try {
					boolean erroreUtilizzoConnettore = false;
					if(consegnaContenutiApplicativiMsg.getPddContext()!=null){
						Object o = consegnaContenutiApplicativiMsg.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.ERRORE_UTILIZZO_CONNETTORE);
						if(o!=null && (o instanceof Boolean)){
							erroreUtilizzoConnettore = (Boolean) o;
						}
					}
					if(erroreUtilizzoConnettore) {
						loadBalancer.getLoadBalancerPool().registerConnectionError(loadBalancer.getConnectorName());
					}
				}catch(Throwable t) {
					String prefix = "";
					if(transazioneApplicativoServer!=null) {
						prefix = "["+transazioneApplicativoServer.getIdTransazione()+"]["+transazioneApplicativoServer.getServizioApplicativoErogatore()+"] " ;
					}
					this.log.error(prefix+"Errore durante il salvataggio delle informazioni di load balancer (register error connection): "+t.getMessage(),t);
				}
			}
		}
				
		if(transazioneApplicativoServer!=null) {
			
			if(EsitoLib.OK != esitoLib.getStatoInvocazione()) {
				if(esitoLib.getMotivazioneErroreNonGestito()!=null && !"".equals(esitoLib.getMotivazioneErroreNonGestito())) {
					transazioneApplicativoServer.setUltimoErrore(esitoLib.getMotivazioneErroreNonGestito());
				}
				else if(esitoLib.getErroreNonGestito()!=null) {
					Throwable e = Utilities.getInnerNotEmptyMessageException(esitoLib.getErroreNonGestito());
					if(e!=null) {
						transazioneApplicativoServer.setUltimoErrore(e.getMessage());
					}
					else {
						transazioneApplicativoServer.setUltimoErrore(esitoLib.getErroreNonGestito().toString());
					}
				}
				else {
					transazioneApplicativoServer.setUltimoErrore("Errore Generico durante il processamento del messaggio");
				}
			}
			
			try {
				GestoreConsegnaMultipla.getInstance().safeUpdateConsegna(transazioneApplicativoServer, idApplicativa, openspcoopstate);
			}catch(Throwable t) {
				this.log.error("["+transazioneApplicativoServer.getIdTransazione()+"]["+transazioneApplicativoServer.getServizioApplicativoErogatore()+"] Errore durante il salvataggio delle informazioni relative al servizio applicativo: "+t.getMessage(),t);
			}
		}
		
		return esitoLib;
	}
	
	private EsitoLib engine_onMessage(IOpenSPCoopState openspcoopstate, OpenSPCoopStateDBManager dbManagerSource,
			RegistroServiziManager registroServiziManager,ConfigurazionePdDManager configurazionePdDManager, 
			MsgDiagnostico msgDiag,
			TransazioneApplicativoServer transazioneApplicativoServer,
			Date oraRegistrazione) throws OpenSPCoopStateException {

		Date dataConsegna = DateManager.getDate();
		
		EsitoLib esito = new EsitoLib();
		ConsegnaContenutiApplicativiMessage consegnaContenutiApplicativiMsg = (ConsegnaContenutiApplicativiMessage) openspcoopstate.getMessageLib();
		
		ConsegnaContenutiApplicativiBehaviourMessage behaviourConsegna = consegnaContenutiApplicativiMsg.getBehaviour();
		String idMessaggioPreBehaviour = null;
		BehaviourForwardToConfiguration behaviourForwardToConfiguration = null;
		GestioneErrore gestioneErroreBehaviour = null;
		if(behaviourConsegna!=null) {
			idMessaggioPreBehaviour = behaviourConsegna.getIdMessaggioPreBehaviour();
			behaviourForwardToConfiguration = behaviourConsegna.getBehaviourForwardToConfiguration();
			gestioneErroreBehaviour = behaviourConsegna.getGestioneErrore();
		}
		
		/* PddContext */
		PdDContext pddContext = consegnaContenutiApplicativiMsg.getPddContext();
		String idTransazione = PdDContext.getValue(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE, pddContext);
				
		/* Protocol Factory */
		IProtocolFactory<?> protocolFactory = null;
		try{
			protocolFactory = this.protocolFactoryManager.getProtocolFactoryByName((String) pddContext.getObject(org.openspcoop2.core.constants.Costanti.PROTOCOL_NAME));
		}catch(Exception e){
			msgDiag.logErroreGenerico(e, "ProtocolFactory.instanziazione"); 
			openspcoopstate.releaseResource();
			esito.setEsitoInvocazione(false); 
			esito.setStatoInvocazioneErroreNonGestito(e);
			return esito;
		}
		
		// Transaction
		Transaction transactionNullable = null;
		try{
			transactionNullable = TransactionContext.getTransaction(idTransazione);
		}catch(Exception e){
			// La transazione potrebbe essere stata eliminata nelle comunicazioni stateful
//			msgDiag.logErroreGenerico(e, "getTransaction"); 
//			openspcoopstate.releaseResource();
//			esito.setEsitoInvocazione(false); 
//			esito.setStatoInvocazioneErroreNonGestito(e);
//			return esito;
		}
		
		msgDiag.setPddContext(pddContext, protocolFactory);
		/* ID e tipo di implementazione PdD con cui interoperare */
		String idMessaggioConsegna = openspcoopstate.getIDMessaggioSessione();
		String implementazionePdDMittente = consegnaContenutiApplicativiMsg.getImplementazionePdDSoggettoMittente();
		String implementazionePdDDestinatario = consegnaContenutiApplicativiMsg.getImplementazionePdDSoggettoDestinatario();

		RichiestaApplicativa richiestaApplicativa = consegnaContenutiApplicativiMsg.getRichiestaApplicativa();
		RichiestaDelegata richiestaDelegata = consegnaContenutiApplicativiMsg.getRichiestaDelegata();
		Busta bustaRichiesta = consegnaContenutiApplicativiMsg.getBusta(); // in caso di richiesta delegata serve per il profilo asincrono

		TipoPdD tipoPdD = TipoPdD.APPLICATIVA;
		if(msgDiag.getPorta()==null) {
			if(richiestaApplicativa!=null && richiestaApplicativa.getIdPortaApplicativa()!=null) {
				msgDiag.updatePorta(tipoPdD, richiestaApplicativa.getIdPortaApplicativa().getNome());
			}
			else if(richiestaDelegata!=null && richiestaDelegata.getIdPortaDelegata()!=null) {
				msgDiag.updatePorta(TipoPdD.DELEGATA, richiestaDelegata.getIdPortaDelegata().getNome());
			}
		}
		
		msgDiag.setPrefixMsgPersonalizzati(MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI);
		if(bustaRichiesta!=null){
			msgDiag.addKeywords(bustaRichiesta, true);
		}
		String descrizioneBehaviour = "";
		if(bustaRichiesta!=null){
			descrizioneBehaviour = bustaRichiesta.removeProperty(CostantiPdD.KEY_DESCRIZIONE_BEHAVIOUR);
			if(descrizioneBehaviour==null){
				descrizioneBehaviour = "";
			}
			if(!"".equals(descrizioneBehaviour)){
				descrizioneBehaviour = 	" ("+descrizioneBehaviour+")";
			}
		}
		msgDiag.addKeyword(CostantiPdD.KEY_DESCRIZIONE_BEHAVIOUR, descrizioneBehaviour);
		
		// VM ProtocolInfo (se siamo arrivati da un canale VM)
		// Per il caso di LocalForward (se si mettera il tracciamento)
		if(pddContext!=null && bustaRichiesta!=null)
			DirectVMProtocolInfo.setInfoFromContext(pddContext, bustaRichiesta);
		
		// Dati per GestoreEventi
		String servizio = null;
		String tipoServizio = null;
		String azione = null;
		if(bustaRichiesta!=null){
			servizio = bustaRichiesta.getServizio();
			tipoServizio = bustaRichiesta.getTipoServizio();
			azione = bustaRichiesta.getAzione(); // in caso di richiesta delegata serve per il profilo asincrono
		}
		String idModuloInAttesa = null; // in caso di richiesta delegata serve per il profilo asincrono
		IDSoggetto identitaPdD = null; 
		IDSoggetto soggettoFruitore = null; 
		String servizioApplicativo = null; 
		String scenarioCooperazione = null; 
		IDServizio idServizio = null;
		IDAccordo idAccordoServizio = null;
		boolean isRicevutaAsincrona = false; 
		String [] tipiIntegrazione = null;
		boolean allegaBody = false;
		boolean scartaBody = false;
		String servizioApplicativoFruitore = null;
		String idCorrelazioneApplicativa = null;
		String idCorrelazioneApplicativaRisposta = null;
		boolean portaDiTipoStateless = false;
		CorrelazioneApplicativaRisposta correlazioneApplicativaRisposta = null;
		boolean gestioneManifest = false;
		ProprietaManifestAttachments proprietaManifestAttachments = this.propertiesReader.getProprietaManifestAttachments(implementazionePdDMittente);
		Trasformazioni trasformazioni = null;

		IDSoggetto soggettoErogatoreServizioHeaderIntegrazione = null;
		IDSoggetto soggettoFruitoreHeaderIntegrazione = null;
		String profiloGestione = null;
		
		DumpConfigurazione dumpConfig = null;
		
		boolean localForward = false;
		
		ResponseCachingConfigurazione responseCachingConfig = null;
		
		if(richiestaApplicativa!=null){
			identitaPdD = richiestaApplicativa.getDominio();
			soggettoFruitore = richiestaApplicativa.getSoggettoFruitore();
			servizioApplicativo = richiestaApplicativa.getServizioApplicativo();
			idModuloInAttesa = richiestaApplicativa.getIdModuloInAttesa();
			scenarioCooperazione = richiestaApplicativa.getScenario();
			isRicevutaAsincrona = richiestaApplicativa.isRicevutaAsincrona();
			idServizio = richiestaApplicativa.getIDServizio();
			if(bustaRichiesta!=null){
				if(idServizio.getSoggettoErogatore()!=null){
					if(idServizio.getSoggettoErogatore().getCodicePorta()==null){
						idServizio.getSoggettoErogatore().setCodicePorta(bustaRichiesta.getIdentificativoPortaDestinatario());
					}
				}
			}
			if(azione!=null && !"".equals(azione) && idServizio!=null && 
					(idServizio.getAzione()==null || "".equals(idServizio.getAzione()))) {
				idServizio.setAzione(azione);
			}
			idAccordoServizio = richiestaApplicativa.getIdAccordo();
			soggettoErogatoreServizioHeaderIntegrazione = idServizio.getSoggettoErogatore();
			profiloGestione = richiestaApplicativa.getProfiloGestione();
			servizioApplicativoFruitore = richiestaApplicativa.getIdentitaServizioApplicativoFruitore();
			idCorrelazioneApplicativa = richiestaApplicativa.getIdCorrelazioneApplicativa();

			localForward = richiestaApplicativa.isLocalForward();
			
		}else{
			identitaPdD = richiestaDelegata.getDominio();
			soggettoFruitore = richiestaDelegata.getIdSoggettoFruitore();
			servizioApplicativo = richiestaDelegata.getServizioApplicativo();
			idModuloInAttesa = richiestaDelegata.getIdModuloInAttesa();
			scenarioCooperazione = richiestaDelegata.getScenario();
			isRicevutaAsincrona = richiestaDelegata.isRicevutaAsincrona();
			idServizio = richiestaDelegata.getIdServizio();
			if(bustaRichiesta!=null){
				if(idServizio.getSoggettoErogatore()!=null){
					if(idServizio.getSoggettoErogatore().getCodicePorta()==null){
						idServizio.getSoggettoErogatore().setCodicePorta(bustaRichiesta.getIdentificativoPortaDestinatario());
					}
				}
			}
			if(azione!=null && !"".equals(azione) && idServizio!=null && 
					(idServizio.getAzione()==null || "".equals(idServizio.getAzione()))) {
				idServizio.setAzione(azione);
			}
			idAccordoServizio = richiestaDelegata.getIdAccordo();
			if ( bustaRichiesta!=null && Costanti.SCENARIO_ASINCRONO_SIMMETRICO_CONSEGNA_RISPOSTA.equals(scenarioCooperazione) ){
				soggettoErogatoreServizioHeaderIntegrazione = new IDSoggetto(bustaRichiesta.getTipoDestinatario(),bustaRichiesta.getDestinatario());
				soggettoFruitoreHeaderIntegrazione = new IDSoggetto(bustaRichiesta.getTipoMittente(),bustaRichiesta.getMittente(), bustaRichiesta.getIdentificativoPortaMittente());
			}else{
				soggettoErogatoreServizioHeaderIntegrazione = idServizio.getSoggettoErogatore();
			}
			profiloGestione = richiestaDelegata.getProfiloGestione();
			servizioApplicativoFruitore = richiestaDelegata.getServizioApplicativo();
			idCorrelazioneApplicativa = richiestaDelegata.getIdCorrelazioneApplicativa();
		}
		
		IDServizio servizioHeaderIntegrazione = null;
		if(bustaRichiesta!=null){
			// Per ricambiare il servizio in correlato per:
			// - AsincronoAsimmetrico, richiestaStato
			// - AsincronoSimmetrico, risposta
			try{
				servizioHeaderIntegrazione = IDServizioFactory.getInstance().getIDServizioFromValues(bustaRichiesta.getTipoServizio(), bustaRichiesta.getServizio(), 
						soggettoErogatoreServizioHeaderIntegrazione, bustaRichiesta.getVersioneServizio());
				servizioHeaderIntegrazione.setAzione(bustaRichiesta.getAzione());
			}catch(Exception e){
				msgDiag.logErroreGenerico(e, "RichiestaApplicativa.getIDServizioFromValues");
				esito.setEsitoInvocazione(false); 
				esito.setStatoInvocazioneErroreNonGestito(e);
				return esito;
			}
		}	
		
		msgDiag.mediumDebug("Profilo di gestione ["+ConsegnaContenutiApplicativi.ID_MODULO+"] della busta: "+profiloGestione);
		msgDiag.setDominio(identitaPdD);  // imposto anche il dominio nel msgDiag
		msgDiag.setIdCorrelazioneApplicativa(idCorrelazioneApplicativa);
		msgDiag.addKeyword(CostantiPdD.KEY_SA_EROGATORE, servizioApplicativo);
		if(servizioApplicativoFruitore!=null){
			msgDiag.addKeyword(CostantiPdD.KEY_SA_FRUITORE, servizioApplicativoFruitore);
		}
		boolean soggettoVirtuale = false;
		try{
			soggettoVirtuale = configurazionePdDManager.isSoggettoVirtuale( identitaPdD );
		}catch(Exception e){
			msgDiag.logErroreGenerico(e, "isSoggettoVirtuale("+identitaPdD+")");
			esito.setEsitoInvocazione(false); 
			esito.setStatoInvocazioneErroreNonGestito(e);
			return esito;
		}
		
		// Aggiornamento Informazioni
		msgDiag.setIdMessaggioRichiesta(idMessaggioConsegna);
		if(idMessaggioPreBehaviour!=null){
			msgDiag.setIdMessaggioRichiesta(idMessaggioPreBehaviour);
			msgDiag.addKeyword(CostantiPdD.KEY_ID_MESSAGGIO_RICHIESTA, idMessaggioPreBehaviour);
		}
		if(soggettoFruitoreHeaderIntegrazione!=null)
			msgDiag.setFruitore(soggettoFruitoreHeaderIntegrazione);
		else
			msgDiag.setFruitore(soggettoFruitore);
		if(servizioHeaderIntegrazione!=null){
			msgDiag.setServizio(servizioHeaderIntegrazione);
		}else{
			msgDiag.setServizio(idServizio);
		}
		if(transazioneApplicativoServer!=null) {
			msgDiag.setServizioApplicativo(servizioApplicativo);
		}

		// Calcolo Profilo di Collaborazione
		msgDiag.mediumDebug("Calcolo profilo di collaborazione...");
		ProfiloDiCollaborazione profiloCollaborazione = EJBUtils.calcolaProfiloCollaborazione(scenarioCooperazione);


		PortaApplicativa pa = null;
		IDPortaApplicativa idPA = null;
		PortaDelegata pd = null;
		IDPortaDelegata idPD = null;
		ServizioApplicativo sa = null;
		if(richiestaApplicativa!=null){
			idPA = richiestaApplicativa.getIdPortaApplicativa();
			try{
				msgDiag.mediumDebug("getPortaApplicativa...");
				pa = configurazionePdDManager.getPortaApplicativa(idPA);
			}catch(Exception e){
				msgDiag.logErroreGenerico(e, "RichiestaApplicativa.getPortaApplicativa");
				esito.setEsitoInvocazione(false); 
				esito.setStatoInvocazioneErroreNonGestito(e);
				return esito;
			}
			try{
				msgDiag.mediumDebug("getServizioApplicativo(pa)...");
				IDServizioApplicativo idSA = new IDServizioApplicativo();
				idSA.setNome(richiestaApplicativa.getServizioApplicativo());
				idSA.setIdSoggettoProprietario(richiestaApplicativa.getIDServizio().getSoggettoErogatore());
				sa = configurazionePdDManager.getServizioApplicativo(idSA);
			}catch(Exception e){
				msgDiag.logErroreGenerico(e, "RichiestaApplicativa.getServizioApplicativo");
				esito.setEsitoInvocazione(false); 
				esito.setStatoInvocazioneErroreNonGestito(e);
				return esito;
			}
			if(pa!=null)
				correlazioneApplicativaRisposta = pa.getCorrelazioneApplicativaRisposta();
		}else{
			idPD = richiestaDelegata.getIdPortaDelegata();
			try{
				msgDiag.mediumDebug("getPortaDelegata...");
				pd = configurazionePdDManager.getPortaDelegata(idPD);
			}catch(Exception e){
				msgDiag.logErroreGenerico(e, "RichiestaDelegata.getPortaApplicativa");
				esito.setEsitoInvocazione(false); 
				esito.setStatoInvocazioneErroreNonGestito(e);
				return esito;
			}
			try{
				msgDiag.mediumDebug("getServizioApplicativo(pd)...");
				IDServizioApplicativo idSA = new IDServizioApplicativo();
				idSA.setNome(richiestaDelegata.getServizioApplicativo());
				idSA.setIdSoggettoProprietario(richiestaDelegata.getIdSoggettoFruitore());
				sa = configurazionePdDManager.getServizioApplicativo(idSA);
			}catch(Exception e){
				msgDiag.logErroreGenerico(e, "RichiestaDelegata.getServizioApplicativo");
				esito.setEsitoInvocazione(false); 
				esito.setStatoInvocazioneErroreNonGestito(e);
				return esito;
			}
			if(pd!=null)
				correlazioneApplicativaRisposta = pd.getCorrelazioneApplicativaRisposta();
		}



		// Recupero anche pd in caso di local forward
		if(localForward && pd==null){
			try{
				msgDiag.mediumDebug("getPortaDelegata...");
				pd = configurazionePdDManager.getPortaDelegata(richiestaDelegata.getIdPortaDelegata());
			}catch(Exception e){
				msgDiag.logErroreGenerico(e, "RichiestaDelegata.getPortaApplicativa");
				esito.setEsitoInvocazione(false); 
				esito.setStatoInvocazioneErroreNonGestito(e);
				return esito;
			}
		}
		


		// La lettura dalla configurazione deve essere dopo il transaction manager
		if(richiestaApplicativa!=null && !localForward){
			try{
				msgDiag.mediumDebug("isAllegaBody(pa)...");
				allegaBody = configurazionePdDManager.isAllegaBody(pa);
			}catch(Exception e){
				msgDiag.logErroreGenerico(e, "RichiestaApplicativa.isAllegaBody");
				esito.setEsitoInvocazione(false); 
				esito.setStatoInvocazioneErroreNonGestito(e);
				return esito;
			}	
			try{
				msgDiag.mediumDebug("isScartaBody(pa)...");
				scartaBody = configurazionePdDManager.isScartaBody(pa);
			}catch(Exception e){
				msgDiag.logErroreGenerico(e, "RichiestaApplicativa.isScartaBody");
				esito.setEsitoInvocazione(false); 
				esito.setStatoInvocazioneErroreNonGestito(e);
				return esito;
			}	
			try{
				msgDiag.mediumDebug("getTipiIntegrazione(pa)...");
				tipiIntegrazione = configurazionePdDManager.getTipiIntegrazione(pa);
			}catch(Exception e){
				msgDiag.logErroreGenerico(e, "RichiestaApplicativa.getTipiIntegrazione");
				esito.setEsitoInvocazione(false); 
				esito.setStatoInvocazioneErroreNonGestito(e);
				return esito;
			}
			try{
				msgDiag.mediumDebug("isModalitaStateless(pa)...");
				portaDiTipoStateless = configurazionePdDManager.isModalitaStateless(pa, profiloCollaborazione); 
			}catch(Exception e){
				msgDiag.logErroreGenerico(e, "RichiestaApplicativa.isModalitaStateless("+profiloCollaborazione+")");
				esito.setEsitoInvocazione(false); 
				esito.setStatoInvocazioneErroreNonGestito(e);
				return esito;
			}
			try{
				msgDiag.mediumDebug("isGestioneManifestAttachments(pa)...");
				gestioneManifest = configurazionePdDManager.isGestioneManifestAttachments(pa,protocolFactory); 
			}catch(Exception e){
				msgDiag.logErroreGenerico(e, "isGestioneManifestAttachments(pa)");
				esito.setEsitoInvocazione(false); 
				esito.setStatoInvocazioneErroreNonGestito(e);
				return esito;
			}
			try{
				msgDiag.mediumDebug("readDumpConfig(pa)...");
				dumpConfig = configurazionePdDManager.getDumpConfigurazione(pa);
			}catch(Exception e){
				msgDiag.logErroreGenerico(e, "readDumpConfig(pa)");
				esito.setEsitoInvocazione(false); 
				esito.setStatoInvocazioneErroreNonGestito(e);
				return esito;
			}
			try{
				msgDiag.mediumDebug("readResponseCachingConfig(pa)...");
				responseCachingConfig = configurazionePdDManager.getConfigurazioneResponseCaching(pa);
			}catch(Exception e){
				msgDiag.logErroreGenerico(e, "readResponseCachingConfig(pa)");
				esito.setEsitoInvocazione(false); 
				esito.setStatoInvocazioneErroreNonGestito(e);
				return esito;
			}
			try{
				msgDiag.mediumDebug("readTrasformazioni(pa)...");
				trasformazioni = configurazionePdDManager.getTrasformazioni(pa);
			}catch(Exception e){
				msgDiag.logErroreGenerico(e, "readTrasformazioni(pa)");
				esito.setEsitoInvocazione(false); 
				esito.setStatoInvocazioneErroreNonGestito(e);
				return esito;
			}
			
		}else{
			try{
				msgDiag.mediumDebug("isAllegaBody(pd)...");
				allegaBody = configurazionePdDManager.isAllegaBody(pd);
			}catch(Exception e){
				msgDiag.logErroreGenerico(e, "RichiestaDelegata.isAllegaBody");
				esito.setEsitoInvocazione(false); 
				esito.setStatoInvocazioneErroreNonGestito(e);
				return esito;
			}	
			try{
				msgDiag.mediumDebug("isScartaBody(pd)...");
				scartaBody = configurazionePdDManager.isScartaBody(pd);
			}catch(Exception e){
				msgDiag.logErroreGenerico(e, "RichiestaDelegata.isScartaBody");
				esito.setEsitoInvocazione(false); 
				esito.setStatoInvocazioneErroreNonGestito(e);
				return esito;
			}	
			try{
				msgDiag.mediumDebug("getTipiIntegrazione(pd)...");
				tipiIntegrazione = configurazionePdDManager.getTipiIntegrazione(pd);
			}catch(Exception e){
				msgDiag.logErroreGenerico(e, "RichiestaDelegata.getTipiIntegrazione");
				esito.setEsitoInvocazione(false); 
				esito.setStatoInvocazioneErroreNonGestito(e);
				return esito;
			}
			try{
				msgDiag.mediumDebug("isModalitaStateless(pd)...");
				portaDiTipoStateless = configurazionePdDManager.isModalitaStateless(pd, profiloCollaborazione); 
			}catch(Exception e){
				msgDiag.logErroreGenerico(e, "RichiestaDelegata.isModalitaStateless("+profiloCollaborazione+")");
				esito.setEsitoInvocazione(false); 
				esito.setStatoInvocazioneErroreNonGestito(e);
				return esito;
			}
			try{
				msgDiag.mediumDebug("isGestioneManifestAttachments(pd)...");
				gestioneManifest = configurazionePdDManager.isGestioneManifestAttachments(pd,protocolFactory); 
			}catch(Exception e){
				msgDiag.logErroreGenerico(e, "isGestioneManifestAttachments(pd)");
				esito.setEsitoInvocazione(false); 
				esito.setStatoInvocazioneErroreNonGestito(e);
				return esito;
			}
			try{
				msgDiag.mediumDebug("readDumpConfig(pd)...");
				dumpConfig = configurazionePdDManager.getDumpConfigurazione(pd);
			}catch(Exception e){
				msgDiag.logErroreGenerico(e, "readDumpConfig(pd)");
				esito.setEsitoInvocazione(false); 
				esito.setStatoInvocazioneErroreNonGestito(e);
				return esito;
			}
			try{
				msgDiag.mediumDebug("readResponseCachingConfig(pd)...");
				responseCachingConfig = configurazionePdDManager.getConfigurazioneResponseCaching(pd);
			}catch(Exception e){
				msgDiag.logErroreGenerico(e, "readResponseCachingConfig(pd)");
				esito.setEsitoInvocazione(false); 
				esito.setStatoInvocazioneErroreNonGestito(e);
				return esito;
			}
			try{
				msgDiag.mediumDebug("readTrasformazioni(pd)...");
				trasformazioni = configurazionePdDManager.getTrasformazioni(pd);
			}catch(Exception e){
				msgDiag.logErroreGenerico(e, "readTrasformazioni(pd)");
				esito.setEsitoInvocazione(false); 
				esito.setStatoInvocazioneErroreNonGestito(e);
				return esito;
			}
		}

		if(idMessaggioPreBehaviour!=null) {
			portaDiTipoStateless = false;
		}
		else if(soggettoVirtuale) {
			portaDiTipoStateless = false;
		}
		pddContext.addObject(org.openspcoop2.core.constants.Costanti.STATELESS, portaDiTipoStateless+"");


		
		LocalForwardEngine localForwardEngine = null;
		LocalForwardParameter localForwardParameter = null;
		try{
			
			if(localForward){
				
				localForwardParameter = new LocalForwardParameter();
				localForwardParameter.setLog(this.log);
				localForwardParameter.setConfigurazionePdDReader(configurazionePdDManager);
				localForwardParameter.setIdCorrelazioneApplicativa(idCorrelazioneApplicativa);
				localForwardParameter.setIdentitaPdD(identitaPdD);
				localForwardParameter.setIdModulo(this.idModulo);
				localForwardParameter.setIdRequest(idMessaggioConsegna);
				localForwardParameter.setImplementazionePdDDestinatario(implementazionePdDDestinatario);
				localForwardParameter.setImplementazionePdDMittente(implementazionePdDMittente);
				localForwardParameter.setIdPdDMittente(registroServiziManager.getIdPortaDominio(soggettoFruitore, null));
				localForwardParameter.setIdPdDDestinatario(registroServiziManager.getIdPortaDominio(idServizio.getSoggettoErogatore(), null));
				localForwardParameter.setMsgDiag(msgDiag);
				localForwardParameter.setOpenspcoopstate(openspcoopstate);
				localForwardParameter.setPddContext(pddContext);
				localForwardParameter.setProtocolFactory(protocolFactory);
				localForwardParameter.setRichiestaDelegata(richiestaDelegata);
				localForwardParameter.setRichiestaApplicativa(richiestaApplicativa);
				localForwardParameter.setStateless(portaDiTipoStateless);
				localForwardParameter.setBusta(bustaRichiesta);
				
				localForwardEngine = new LocalForwardEngine(localForwardParameter);
			}
			
		}catch(Exception e){
			msgDiag.logErroreGenerico(e, "LocalForwardEngine.init");
			esito.setEsitoInvocazione(false); 
			esito.setStatoInvocazioneErroreNonGestito(e);
			return esito;
		}
		
		
		


		/* ------------------ Connessione al DB  --------------- */
		msgDiag.mediumDebug("Richiesta connessione al database per la gestione della richiesta...");
		openspcoopstate.initResource(identitaPdD, ConsegnaContenutiApplicativi.ID_MODULO,idTransazione,dbManagerSource);
		registroServiziManager.updateState(openspcoopstate.getStatoRichiesta(),openspcoopstate.getStatoRisposta());
		configurazionePdDManager.updateState(openspcoopstate.getStatoRichiesta(),openspcoopstate.getStatoRisposta());
		msgDiag.updateState(openspcoopstate.getStatoRichiesta(),openspcoopstate.getStatoRisposta());
		


		/* ----------- Analisi tipo di Consegna Contenuti Applicativi:
	       OneWay (InvocazioneServizio) in modalita asincrona:
	       - Non serve aspettare una risposta applicativa
	       - Non serve generare una risposta (eventuale busta con riscontri e' gia' stata inviata)
	       OneWay (InvocazioneServizio) in modalita sincrona:
	       - Bisogna aspettare una risposta applicativa sincrona
	       - Bisogna solo sbloccare RicezioneBuste non appena ricevuto http 200.
	       Sincrono (InvocazioneServizio):
	       - Bisogna aspettare una risposta applicativa sincrona
	       - Bisogna quindi generare un busta della risposta sincrona
	       AsincronoSimmetrico (InvocazioneServizio):
	       - Non serve aspettare una risposta applicativa se la ricevuta applicativa e' disabilitata
	         altrimenti deve essere aspettata.
	       - Non serve generare una risposta (la ricevuta e' gia' stata inviata) se la ricevuta applicativa e' disabilitata
	         altrimenti bisogna generare un busta contenente la ricevuta
	      AsincronoSimmetrico (ConsegnaRisposta):
	       - Non serve aspettare una risposta applicativa se la ricevuta applicativa e' disabilitata
	         altrimenti deve essere aspettata.
	       - Non serve generare una risposta (la ricevuta e' gia' stata inviata) se la ricevuta applicativa e' disabilitata
	         altrimenti bisogna generare un busta contenente la ricevuta
	       AsincronoAsimmetrico (InvocazioneServizio):
	       - Non serve aspettare una risposta applicativa (sara' poi richiesta con il polling) se la ricevuta applicativa e' disabilitata
	         altrimenti deve essere aspettata.
		   - Non serve generare una risposta (la ricevuta e' gia' stata inviata) se la ricevuta applicativa e' disabilitata
	         altrimenti bisogna generare un busta contenente la ricevuta
	       AsincronoAsimmetrico (Polling):
	       - Bisogna aspettare una risposta applicativa 'sincrona' comunque
	       - Bisogna quindi generare un busta della risposta contenente il risultato del polling
	       ConsegnaContenutiApplicativi:
	       - Non serve aspettare una risposta applicativa
	       - Non serve generare una risposta (e' la parte finale di consegna gia' della risposta)
	       ------------- */
		boolean existsModuloInAttesaRispostaApplicativa = false;
		if(idMessaggioPreBehaviour!=null){
			existsModuloInAttesaRispostaApplicativa = false;
		}
		else if(Costanti.SCENARIO_SINCRONO_INVOCAZIONE_SERVIZIO.equals(scenarioCooperazione) ){
			existsModuloInAttesaRispostaApplicativa = true;
		}
		else if(Costanti.SCENARIO_ASINCRONO_ASIMMETRICO_POLLING.equals(scenarioCooperazione)){
			existsModuloInAttesaRispostaApplicativa = true;
		}
		else if (Costanti.SCENARIO_ASINCRONO_ASIMMETRICO_INVOCAZIONE_SERVIZIO.equals(scenarioCooperazione) ||
				Costanti.SCENARIO_ASINCRONO_SIMMETRICO_INVOCAZIONE_SERVIZIO.equals(scenarioCooperazione) ||
				Costanti.SCENARIO_ASINCRONO_SIMMETRICO_CONSEGNA_RISPOSTA.equals(scenarioCooperazione)){
			existsModuloInAttesaRispostaApplicativa = portaDiTipoStateless || isRicevutaAsincrona;
		}
		else if( (richiestaApplicativa!=null) && Costanti.SCENARIO_ONEWAY_INVOCAZIONE_SERVIZIO.equals(richiestaApplicativa.getScenario()) ){
			existsModuloInAttesaRispostaApplicativa = portaDiTipoStateless;
		}



		/* ------------------ Inizializzazione Contesto di gestione della Richiesta --------------- */
		msgDiag.mediumDebug("Inizializzo contesto per la gestione...");
		// EJBUtils (per eventuali errori)
		EJBUtils ejbUtils = null;
		try{
			String idMessaggioGestoreMessaggiRichiesta = idMessaggioConsegna;
			if(idMessaggioPreBehaviour!=null){
				idMessaggioGestoreMessaggiRichiesta = bustaRichiesta.getID();
			}
			ejbUtils = new EJBUtils(identitaPdD,tipoPdD,ConsegnaContenutiApplicativi.ID_MODULO,idMessaggioConsegna,
					idMessaggioGestoreMessaggiRichiesta,Costanti.INBOX,openspcoopstate,msgDiag,false,
					consegnaContenutiApplicativiMsg.getImplementazionePdDSoggettoMittente(),
					consegnaContenutiApplicativiMsg.getImplementazionePdDSoggettoDestinatario(),
					profiloGestione,pddContext
			);
			ejbUtils.setServizioApplicativoErogatore(servizioApplicativo);
		}catch(Exception e){
			msgDiag.logErroreGenerico(e, "EJBUtils.new");
			openspcoopstate.releaseResource();
			esito.setEsitoInvocazione(false); 
			esito.setStatoInvocazioneErroreNonGestito(e);

			return esito;
		}
		
		// GestoriMessaggio
		msgDiag.mediumDebug("Inizializzo contesto per la gestione (GestoreMessaggio)...");
		String idMessaggioGestoreMessaggiRichiesta = idMessaggioConsegna;
		if(idMessaggioPreBehaviour!=null){
			idMessaggioGestoreMessaggiRichiesta = bustaRichiesta.getID();
		}
		GestoreMessaggi msgRequest = new GestoreMessaggi(openspcoopstate, true, idMessaggioGestoreMessaggiRichiesta,Costanti.INBOX,msgDiag,pddContext);
		OpenSPCoop2Message consegnaMessagePrimaTrasformazione = null;
		GestoreMessaggi msgResponse = null;
		msgRequest.setPortaDiTipoStateless(portaDiTipoStateless);
		
		// RequestInfo
		RequestInfo requestInfo = (RequestInfo) pddContext.getObject(org.openspcoop2.core.constants.Costanti.REQUEST_INFO);
		if(requestInfo==null || idTransazione==null) {
			// devo leggerlo dal messaggio
			try {
				consegnaMessagePrimaTrasformazione = msgRequest.getMessage(oraRegistrazione);
				correctForwardPathNotifiche(transazioneApplicativoServer, consegnaMessagePrimaTrasformazione, protocolFactory);
				if(requestInfo==null) {
					Object o = consegnaMessagePrimaTrasformazione.getContextProperty(org.openspcoop2.core.constants.Costanti.REQUEST_INFO);
					if(o==null) {
						throw new Exception("RequestInfo non presente nel contesto");
					}
					requestInfo = (RequestInfo) o;
					pddContext.addObject(org.openspcoop2.core.constants.Costanti.REQUEST_INFO,requestInfo);
				}
				if(idTransazione==null) {
					Object o = consegnaMessagePrimaTrasformazione.getContextProperty(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE);
					if(o==null) {
						throw new Exception("IdTransazione non presente nel contesto");
					}
					idTransazione = (String) o;
					pddContext.addObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE,idTransazione);
				}
			}catch(Exception e) {
				msgDiag.logErroreGenerico(e, "LetturaMessaggioErrore (Recupero Dati)"); 
				openspcoopstate.releaseResource();
				esito.setEsitoInvocazione(false); 
				esito.setStatoInvocazioneErroreNonGestito(e);
				return esito;
			}
		}
		
		RicezioneBusteExternalErrorGenerator generatoreErrorePA = null;
		try{
			generatoreErrorePA = new RicezioneBusteExternalErrorGenerator(this.log,
					this.idModulo, requestInfo, openspcoopstate.getStatoRichiesta());
			generatoreErrorePA.updateInformazioniCooperazione(soggettoFruitore, idServizio);
			generatoreErrorePA.updateInformazioniCooperazione(servizioApplicativoFruitore);
			generatoreErrorePA.updateTipoPdD(TipoPdD.APPLICATIVA);
			ejbUtils.setGeneratoreErrorePortaApplicativa(generatoreErrorePA);
		}catch(Exception e){
			msgDiag.logErroreGenerico(e, "RicezioneBusteExternalErrorGenerator.instanziazione"); 
			openspcoopstate.releaseResource();
			esito.setEsitoInvocazione(false); 
			esito.setStatoInvocazioneErroreNonGestito(e);
			return esito;
		}

		// Oneway versione 11
		boolean oneWayVersione11 = consegnaContenutiApplicativiMsg.isOneWayVersione11();
		ejbUtils.setOneWayVersione11(oneWayVersione11);
		if(idMessaggioPreBehaviour!=null){
			ejbUtils.setOneWayVersione11(true); // per forzare l'update su db	
		}
		msgRequest.setOneWayVersione11(oneWayVersione11);







		OpenSPCoop2Message responseMessage = null;


		try{
			if(msgRequest.isRiconsegnaMessaggio(servizioApplicativo) == false){
				openspcoopstate.releaseResource();
				// Per i profili 'sincroni' dove vi e' un modulo in attesa non puo' sussistere una riconsegna del messaggio.
				if(existsModuloInAttesaRispostaApplicativa==false){
					msgDiag.logPersonalizzato("riconsegnaMessaggioPrematura");
					esito.setEsitoInvocazione(false); 
					esito.setStatoInvocazione(EsitoLib.ERRORE_NON_GESTITO,msgDiag.getMessaggio_replaceKeywords("riconsegnaMessaggioPrematura"));
				}else{
					String message = null;
					String posizione = null;
					if( (richiestaApplicativa!=null) && Costanti.SCENARIO_ONEWAY_INVOCAZIONE_SERVIZIO.equals(richiestaApplicativa.getScenario())){
						message = "Messaggio eliminato durante il controllo di ri-consegna ("+servizioApplicativo+","+scenarioCooperazione+")";
						posizione = "msgRequest.isRiconsegnaMessaggio("+servizioApplicativo+","+scenarioCooperazione+")";
					}else{
						message = "Messaggio eliminato durante il controllo di ri-consegna ("+servizioApplicativo+","+scenarioCooperazione+",STATELESS)";
						posizione = "msgRequest.isRiconsegnaMessaggio("+servizioApplicativo+","+scenarioCooperazione+",STATELESS)";
					}
					msgDiag.logErroreGenerico(message,posizione);
					esito.setEsitoInvocazione(true); 
					esito.setStatoInvocazione(EsitoLib.ERRORE_NON_GESTITO,message);
				}
				this.log.info(ConsegnaContenutiApplicativi.ID_MODULO + "Riconsegna messaggio prematura");

				return esito;
			}
		}catch(Exception e){
			msgDiag.logErroreGenerico(e, "msgRequest.isRiconsegnaMessaggio("+servizioApplicativo+")");
			ejbUtils.rollbackMessage("Errore verifica riconsegna messaggio", servizioApplicativo, esito);
			openspcoopstate.releaseResource();
			esito.setEsitoInvocazione(false); 
			esito.setStatoInvocazioneErroreNonGestito(e);

			return esito;
		}

		// Funzionalita'
		ConsegnaInOrdine ordineConsegna = null;

		// Consegna da effettuare
		msgDiag.mediumDebug("Inizializzo contesto per la gestione (Consegna)...");
		ConnettoreMsg connettoreMsg = null;
		boolean consegnaPerRiferimento = false;
		boolean rispostaPerRiferimento = false;
		boolean integrationManager = false;
		ValidazioneContenutiApplicativi validazioneContenutoApplicativoApplicativo = null;
		List<Proprieta> proprietaValidazioneContenutoApplicativoApplicativo = null;
		if(Costanti.SCENARIO_CONSEGNA_CONTENUTI_APPLICATIVI.equals(scenarioCooperazione) ||
				Costanti.SCENARIO_ASINCRONO_SIMMETRICO_CONSEGNA_RISPOSTA.equals(scenarioCooperazione) ){
			try{
				msgDiag.mediumDebug("Inizializzo contesto per la gestione (getConsegnaRispostaAsincrona) [ConsegnaContenuti/AsincronoSimmetricoRisposta]...");
				connettoreMsg = configurazionePdDManager.getConsegnaRispostaAsincrona(sa,richiestaDelegata);
				if(Costanti.SCENARIO_CONSEGNA_CONTENUTI_APPLICATIVI.equals(scenarioCooperazione)){
					connettoreMsg.setCheckPresenzaHeaderPrimaSbustamento(true);
				}
				if(connettoreMsg!=null){
					connettoreMsg.initPolicyGestioneToken(configurazionePdDManager);
				}
				msgDiag.mediumDebug("Inizializzo contesto per la gestione (consegnaRispostaAsincronaConGetMessage) [ConsegnaContenuti/AsincronoSimmetricoRisposta]...");
				integrationManager = configurazionePdDManager.consegnaRispostaAsincronaConGetMessage(sa);
				msgDiag.mediumDebug("Inizializzo contesto per la gestione (getTipoValidazioneContenutoApplicativo) [ConsegnaContenuti/AsincronoSimmetricoRisposta]...");
				validazioneContenutoApplicativoApplicativo = configurazionePdDManager.getTipoValidazioneContenutoApplicativo(pd,implementazionePdDMittente, false);
				proprietaValidazioneContenutoApplicativoApplicativo = pd.getProprietaList();
				msgDiag.mediumDebug("Inizializzo contesto per la gestione (consegnaRispostaAsincronaPerRiferimento) [ConsegnaContenuti/AsincronoSimmetricoRisposta]...");
				consegnaPerRiferimento = configurazionePdDManager.consegnaRispostaAsincronaPerRiferimento(sa);
				msgDiag.mediumDebug("Inizializzo contesto per la gestione (consegnaRispostaAsincronaRispostaPerRiferimento) [ConsegnaContenuti/AsincronoSimmetricoRisposta]...");
				rispostaPerRiferimento = configurazionePdDManager.consegnaRispostaAsincronaRispostaPerRiferimento(sa);
			}catch(Exception e){
				msgDiag.logErroreGenerico(e, "ConsegnaAsincrona.getDatiConsegna(sa:"+servizioApplicativo+")");
				ejbUtils.rollbackMessage("[ConsegnaAsincrona] Connettore per consegna applicativa non definito:"+e.getMessage(),servizioApplicativo, esito);
				esito.setEsitoInvocazione(false); 
				esito.setStatoInvocazioneErroreNonGestito(e);
				return esito;
			}


		}
		else if(Costanti.SCENARIO_ASINCRONO_ASIMMETRICO_POLLING.equals(scenarioCooperazione)){
			try{
				msgDiag.mediumDebug("Inizializzo contesto per la gestione (getConsegnaRispostaAsincrona) [AsincronoAsimmetricoPolling]...");
				connettoreMsg = configurazionePdDManager.getConsegnaRispostaAsincrona(sa,richiestaApplicativa);
				if(connettoreMsg!=null){
					connettoreMsg.initPolicyGestioneToken(configurazionePdDManager);
				}
				msgDiag.mediumDebug("Inizializzo contesto per la gestione (consegnaRispostaAsincronaConGetMessage) [ConsegnaContenuti/AsincronoSimmetricoRisposta]...");
				integrationManager = configurazionePdDManager.consegnaRispostaAsincronaConGetMessage(sa);
				msgDiag.mediumDebug("Inizializzo contesto per la gestione (getTipoValidazioneContenutoApplicativo) [AsincronoAsimmetricoPolling]...");
				validazioneContenutoApplicativoApplicativo = configurazionePdDManager.getTipoValidazioneContenutoApplicativo(pa,implementazionePdDMittente, false);
				proprietaValidazioneContenutoApplicativoApplicativo = pa.getProprietaList();
				msgDiag.mediumDebug("Inizializzo contesto per la gestione (consegnaRispostaAsincronaPerRiferimento) [AsincronoAsimmetricoPolling]...");
				consegnaPerRiferimento = configurazionePdDManager.consegnaRispostaAsincronaPerRiferimento(sa);
				msgDiag.mediumDebug("Inizializzo contesto per la gestione (consegnaRispostaAsincronaRispostaPerRiferimento) [AsincronoAsimmetricoPolling]...");
				rispostaPerRiferimento = configurazionePdDManager.consegnaRispostaAsincronaRispostaPerRiferimento(sa);
			}catch(Exception e){
				msgDiag.logErroreGenerico(e, "AsincronoSimmetricoPolling.getDatiConsegna(sa:"+servizioApplicativo+")");
				ejbUtils.rollbackMessage("[AsincronoSimmetricoPolling] Connettore per consegna applicativa non definito:"+e.getMessage(),servizioApplicativo, esito);
				esito.setEsitoInvocazione(false); 
				esito.setStatoInvocazioneErroreNonGestito(e);
				return esito;
			}
		}else{
			try{
				msgDiag.mediumDebug("Inizializzo contesto per la gestione (getInvocazioneServizio)...");
				connettoreMsg = configurazionePdDManager.getInvocazioneServizio(sa,richiestaApplicativa);
				if(connettoreMsg!=null){
					connettoreMsg.initPolicyGestioneToken(configurazionePdDManager);
				}
				msgDiag.mediumDebug("Inizializzo contesto per la gestione (consegnaRispostaAsincronaConGetMessage) [ConsegnaContenuti/AsincronoSimmetricoRisposta]...");
				integrationManager = configurazionePdDManager.invocazioneServizioConGetMessage(sa);
				msgDiag.mediumDebug("Inizializzo contesto per la gestione (getTipoValidazioneContenutoApplicativo)...");
				validazioneContenutoApplicativoApplicativo = configurazionePdDManager.getTipoValidazioneContenutoApplicativo(pa,implementazionePdDMittente, false);
				proprietaValidazioneContenutoApplicativoApplicativo = pa.getProprietaList();
				msgDiag.mediumDebug("Inizializzo contesto per la gestione (invocazioneServizioPerRiferimento)...");
				consegnaPerRiferimento = configurazionePdDManager.invocazioneServizioPerRiferimento(sa);
				msgDiag.mediumDebug("Inizializzo contesto per la gestione (invocazioneServizioRispostaPerRiferimento)...");
				rispostaPerRiferimento = configurazionePdDManager.invocazioneServizioRispostaPerRiferimento(sa);
			}catch(Exception e){
				msgDiag.logErroreGenerico(e, "InvocazioneServizio.getDatiConsegna(sa:"+servizioApplicativo+")");
				ejbUtils.rollbackMessage("Connettore per consegna applicativa non definito:"+e.getMessage(),servizioApplicativo, esito);
				esito.setEsitoInvocazione(false); 
				esito.setStatoInvocazioneErroreNonGestito(e);
				return esito;
			}

		}
		msgDiag.mediumDebug("Check parametri...");
		if(connettoreMsg==null){
			msgDiag.logErroreGenerico("Connettore non definito nella configurazione (is null)", "getDatiConsegna(sa:"+servizioApplicativo+")");
			ejbUtils.rollbackMessage("Connettore per consegna applicativa non definito per il sa ["+servizioApplicativo+"]",servizioApplicativo, esito);
			esito.setEsitoInvocazione(false); 
			esito.setStatoInvocazione(EsitoLib.ERRORE_NON_GESTITO, "Connettore per consegna applicativa non definito per il sa ["+servizioApplicativo+"]");
			return esito;
		}
		connettoreMsg.setProtocolFactory(protocolFactory);
		connettoreMsg.setGestioneManifest(gestioneManifest);
		connettoreMsg.setProprietaManifestAttachments(proprietaManifestAttachments);
		connettoreMsg.setLocalForward(localForward);
		if(transazioneApplicativoServer!=null) {
			transazioneApplicativoServer.setConsegnaIntegrationManager(integrationManager);
			connettoreMsg.setTransazioneApplicativoServer(transazioneApplicativoServer);
			connettoreMsg.setIdPortaApplicativa(idPA);
			connettoreMsg.setDataConsegnaTransazioneApplicativoServer(dataConsegna);
		}
		ForwardProxy forwardProxy = null;
		if(configurazionePdDManager.isForwardProxyEnabled()) {
			try {
				forwardProxy = configurazionePdDManager.getForwardProxyConfigErogazione(identitaPdD, idServizio, null);
			}catch(Exception e) {
				msgDiag.logErroreGenerico(e, "Configurazione ForwardProxy (sa:"+servizioApplicativo+")");
				ejbUtils.rollbackMessage("Configurazione del connettore errata per la funzionalità govway-proxy; sa ["+servizioApplicativo+"]",servizioApplicativo, esito);
				esito.setEsitoInvocazione(false); 
				esito.setStatoInvocazione(EsitoLib.ERRORE_NON_GESTITO, "Configurazione del connettore errata per la funzionalità govway-proxy; sa ["+servizioApplicativo+"]");
				return esito;
			}
		}
		connettoreMsg.setForwardProxy(forwardProxy);
		connettoreMsg.setIdAccordo(idAccordoServizio);

		// Identificativo di una risposta.
		String idMessageResponse = null;
		if(existsModuloInAttesaRispostaApplicativa){
			msgDiag.mediumDebug("Creazione id risposta...");
			try{
				org.openspcoop2.protocol.engine.builder.Imbustamento imbustatore = 
						new org.openspcoop2.protocol.engine.builder.Imbustamento(this.log,protocolFactory,openspcoopstate.getStatoRichiesta());
				idMessageResponse = 
					imbustatore.buildID(identitaPdD, 
							(String) pddContext.getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE),
							this.propertiesReader.getGestioneSerializableDB_AttesaAttiva(),
							this.propertiesReader.getGestioneSerializableDB_CheckInterval(),
							RuoloMessaggio.RISPOSTA);
			}catch(Exception e){
				msgDiag.logErroreGenerico(e, "imbustatore.buildID(idMessageResponse)");
				if(existsModuloInAttesaRispostaApplicativa) {
					try{
						this.sendErroreProcessamento(localForward, localForwardEngine, ejbUtils, 
								ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
								get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_507_COSTRUZIONE_IDENTIFICATIVO),
								idModuloInAttesa, bustaRichiesta, idCorrelazioneApplicativa, idCorrelazioneApplicativaRisposta, servizioApplicativoFruitore, e,
								(responseMessage!=null ? responseMessage.getParseException() : null),
								pddContext);
						esito.setEsitoInvocazione(true); 
						esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO, "imbustatore.buildID(idMessageResponse)");
					}catch(Exception sendError){
						ejbUtils.rollbackMessage("Creazione id di risposta (sendRispostaApplicativa) fallita", esito);
						esito.setStatoInvocazioneErroreNonGestito(sendError);
						esito.setEsitoInvocazione(false);
					}
				}else{
					ejbUtils.rollbackMessage("Creazione id di risposta fallita", esito);
					esito.setStatoInvocazioneErroreNonGestito(e);
					esito.setEsitoInvocazione(false);
				}
				openspcoopstate.releaseResource();

				return esito;
			}

			// Aggiornamento Informazioni
			msgDiag.setIdMessaggioRisposta(idMessageResponse);
			msgDiag.addKeyword(CostantiPdD.KEY_ID_MESSAGGIO_RISPOSTA, idMessageResponse);

		}


		msgDiag.mediumDebug("Inizializzo contesto per la gestione (Risposta)...");

		//	Modalita' gestione risposta (Sincrona/Fault/Ricevute...)
		// Per i profili diversi dal sincrono e' possibile impostare dove far ritornare l'errore
		boolean newConnectionForResponse = false; 
		if(Costanti.SCENARIO_ONEWAY_INVOCAZIONE_SERVIZIO.equals(scenarioCooperazione)){
			if( (consegnaContenutiApplicativiMsg.isStateless()==false) &&  (!existsModuloInAttesaRispostaApplicativa) ){
				newConnectionForResponse = configurazionePdDManager.newConnectionForResponse();
			}
		}else if( (ProfiloDiCollaborazione.SINCRONO.equals(bustaRichiesta.getProfiloDiCollaborazione())==false) &&
				(consegnaContenutiApplicativiMsg.isStateless()==false) ){
			newConnectionForResponse = configurazionePdDManager.newConnectionForResponse();
		}
		ejbUtils.setReplyOnNewConnection(newConnectionForResponse);


		// Gestione indirizzo telematico
		boolean gestioneIndirizzoTelematico = false;
		if(consegnaContenutiApplicativiMsg.isStateless()==false){
			gestioneIndirizzoTelematico = configurazionePdDManager.isUtilizzoIndirizzoTelematico();
		}
		ejbUtils.setUtilizzoIndirizzoTelematico(gestioneIndirizzoTelematico);

		IProtocolVersionManager protocolManager = null;
		boolean isBlockedTransaction_responseMessageWithTransportCodeError = false;
		try{
			protocolManager = protocolFactory.createProtocolVersionManager(profiloGestione);
			isBlockedTransaction_responseMessageWithTransportCodeError = 
					protocolManager.isBlockedTransaction_responseMessageWithTransportCodeError();
		}catch(Exception e){
			msgDiag.logErroreGenerico(e, "ProtocolFactory.createProtocolManager("+profiloGestione+")");
			ejbUtils.rollbackMessage("ProtocolFactory.createProtocolManager("+profiloGestione+"):"+e.getMessage(),servizioApplicativo, esito);
			esito.setEsitoInvocazione(false); 
			esito.setStatoInvocazioneErroreNonGestito(e);
			return esito;
		}
		boolean consegnaInOrdine = false;
		// Sequenza: deve essere abilitata la consegna affidabile + la collaborazione e infine la consegna in ordine e non deve essere richiesto il profilo linee guida 1.0
		Busta bustaIndicazioneConsegnaInOrdine = null;
		if(Costanti.SCENARIO_ONEWAY_INVOCAZIONE_SERVIZIO.equals(scenarioCooperazione)){
			
			if(bustaRichiesta!=null) {
				bustaIndicazioneConsegnaInOrdine = bustaRichiesta.clone();
			}
			else {
				bustaIndicazioneConsegnaInOrdine = new Busta(protocolFactory.getProtocol());
			}
			bustaIndicazioneConsegnaInOrdine.setProfiloDiCollaborazione(profiloCollaborazione);
			
			switch (protocolManager.getConsegnaInOrdine(bustaIndicazioneConsegnaInOrdine)) {
			case ABILITATA:
				consegnaInOrdine = true;
				break;
			case DISABILITATA:
				consegnaInOrdine = false;
				break;
			default:
				boolean gestioneConsegnaInOrdineAbilitata =  this.propertiesReader.isGestioneRiscontri(implementazionePdDMittente) && 
				this.propertiesReader.isGestioneElementoCollaborazione(implementazionePdDMittente) && 
				this.propertiesReader.isGestioneConsegnaInOrdine(implementazionePdDMittente);	
				consegnaInOrdine = gestioneConsegnaInOrdineAbilitata &&
					bustaRichiesta.getSequenza()!=-1;
				break;
			}
		}




		// Punto di inizio per la transazione.
		IConnettore connectorSenderForDisconnect = null;
		String location = "";
		OpenSPCoop2Message consegnaMessageTrasformato = null;
		long responseContentLength = -1;
		try{	    


			/* ---------------- Check per consegna in ordine ----------------*/
			if(consegnaInOrdine){
				if(oneWayVersione11 || openspcoopstate instanceof OpenSPCoopStateful){
					msgDiag.mediumDebug("Controllo consegna in ordine...");
					try{
						ordineConsegna = new ConsegnaInOrdine(openspcoopstate.getStatoRichiesta(),protocolFactory);
						if(ordineConsegna.isConsegnaInOrdine(bustaRichiesta, 
								this.propertiesReader.getGestioneSerializableDB_AttesaAttiva(),
								this.propertiesReader.getGestioneSerializableDB_CheckInterval()) == false){
							// congelamento busta
							msgDiag.addKeyword(CostantiPdD.KEY_SEQUENZA_ATTESA, ordineConsegna.getSequenzaAttesa()+"");
							if(ordineConsegna.getSequenzaAttesa()>bustaRichiesta.getSequenza()){
								msgDiag.logPersonalizzato("consegnaInOrdine.messaggioGiaConsegnato");
								ejbUtils.releaseInboxMessage(true);
								esito.setEsitoInvocazione(true); 
								esito.setStatoInvocazione(EsitoLib.ERRORE_NON_GESTITO, 
										msgDiag.getMessaggio_replaceKeywords("consegnaInOrdine.messaggioGiaConsegnato"));
							}else{
								msgDiag.logPersonalizzato("consegnaInOrdine.messaggioFuoriOrdine");
								ejbUtils.rollbackMessage(msgDiag.getMessaggio_replaceKeywords("consegnaInOrdine.messaggioFuoriOrdine"), servizioApplicativo, esito);
								esito.setEsitoInvocazione(false);
								esito.setStatoInvocazione(EsitoLib.ERRORE_NON_GESTITO, 
										msgDiag.getMessaggio_replaceKeywords("consegnaInOrdine.messaggioFuoriOrdine"));
							}
							openspcoopstate.releaseResource();
							return esito;
						}
					}catch(Exception e){
						this.log.error("Riscontrato errore durante la gestione della sequenza per la consegna in ordine",e);
						msgDiag.logErroreGenerico(e, "GetioneSequenzaInOrdine");
						ejbUtils.rollbackMessage("Errore verifica consegna in ordine: "+e.getMessage(), servizioApplicativo, esito);
						openspcoopstate.releaseResource();
						esito.setEsitoInvocazione(false); 
						esito.setStatoInvocazioneErroreNonGestito(e);
						return esito;
					}
				}
			}





			/* ------------  Ricostruzione Messaggio Soap da spedire ------------- */
			msgDiag.mediumDebug("Ricostruzione SOAPEnvelope di richiesta/consegna...");	
			try{
				if(consegnaMessagePrimaTrasformazione==null) {
					if(consegnaPerRiferimento==false){
						consegnaMessagePrimaTrasformazione = msgRequest.getMessage();
						correctForwardPathNotifiche(transazioneApplicativoServer, consegnaMessagePrimaTrasformazione, protocolFactory);
					}else{
						// consegnaMessage deve contenere il messaggio necessario all'invocazione del metodo pubblicaEvento
						consegnaMessagePrimaTrasformazione = 
							msgRequest.buildRichiestaPubblicazioneMessaggio_RepositoryMessaggi(soggettoFruitore, tipoServizio,servizio,azione);
					}
				}
			}catch(Exception e){
				msgDiag.logErroreGenerico(e, "msgRequest.getMessage()");

				if(existsModuloInAttesaRispostaApplicativa) {
					
					this.sendErroreProcessamento(localForward, localForwardEngine, ejbUtils, 
							ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_509_READ_REQUEST_MSG),
							idModuloInAttesa, bustaRichiesta, idCorrelazioneApplicativa, idCorrelazioneApplicativaRisposta, servizioApplicativoFruitore, e,
							(responseMessage!=null ? responseMessage.getParseException() : null),
							pddContext);
					
					esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO, "msgRequest.getMessage()");
					esito.setEsitoInvocazione(true);
				}else{
					ejbUtils.rollbackMessage("Ricostruzione del messaggio Soap da Spedire non riuscita.",servizioApplicativo, esito);
					esito.setStatoInvocazioneErroreNonGestito(e);
					esito.setEsitoInvocazione(false);
				}
				openspcoopstate.releaseResource();

				return esito;
			}



			/* -----  Header Integrazione ------ */
			String originalID = bustaRichiesta.getID();
			if(idMessaggioPreBehaviour!=null){
				bustaRichiesta.setID(idMessaggioPreBehaviour);
			}
			msgDiag.mediumDebug("Gestione header di integrazione per la richiesta...");
			HeaderIntegrazione headerIntegrazione = new HeaderIntegrazione(idTransazione);
			if(soggettoFruitoreHeaderIntegrazione!=null){
				headerIntegrazione.getBusta().setTipoMittente(soggettoFruitoreHeaderIntegrazione.getTipo());
				headerIntegrazione.getBusta().setMittente(soggettoFruitoreHeaderIntegrazione.getNome());
			}else if(soggettoFruitore!=null){
				headerIntegrazione.getBusta().setTipoMittente(soggettoFruitore.getTipo());
				headerIntegrazione.getBusta().setMittente(soggettoFruitore.getNome());
			}
			if(servizioHeaderIntegrazione!=null){
				headerIntegrazione.getBusta().setTipoDestinatario(servizioHeaderIntegrazione.getSoggettoErogatore().getTipo());
				headerIntegrazione.getBusta().setDestinatario(servizioHeaderIntegrazione.getSoggettoErogatore().getNome());
				headerIntegrazione.getBusta().setTipoServizio(servizioHeaderIntegrazione.getTipo());
				headerIntegrazione.getBusta().setServizio(servizioHeaderIntegrazione.getNome());
				headerIntegrazione.getBusta().setVersioneServizio(servizioHeaderIntegrazione.getVersione());
				headerIntegrazione.getBusta().setAzione(servizioHeaderIntegrazione.getAzione());
			}else{
				headerIntegrazione.getBusta().setTipoDestinatario(idServizio.getSoggettoErogatore().getTipo());
				headerIntegrazione.getBusta().setDestinatario(idServizio.getSoggettoErogatore().getNome());
				headerIntegrazione.getBusta().setTipoServizio(idServizio.getTipo());
				headerIntegrazione.getBusta().setServizio(idServizio.getNome());
				headerIntegrazione.getBusta().setVersioneServizio(idServizio.getVersione());
				headerIntegrazione.getBusta().setAzione(idServizio.getAzione());
			}
			headerIntegrazione.getBusta().setID(bustaRichiesta.getID());
			headerIntegrazione.getBusta().setRiferimentoMessaggio(bustaRichiesta.getRiferimentoMessaggio());
			headerIntegrazione.getBusta().setIdCollaborazione(bustaRichiesta.getCollaborazione());
			headerIntegrazione.getBusta().setProfiloDiCollaborazione(bustaRichiesta.getProfiloDiCollaborazione());
			headerIntegrazione.setIdApplicativo(idCorrelazioneApplicativa);
			headerIntegrazione.setServizioApplicativo(servizioApplicativoFruitore);

			Map<String, List<String>> propertiesTrasporto = new HashMap<String, List<String>>();
			Map<String, List<String>> propertiesUrlBased = new HashMap<String, List<String>>();

			if(tipiIntegrazione==null){
				if(ConsegnaContenutiApplicativi.defaultPerProtocolloGestoreIntegrazionePA.containsKey(protocolFactory.getProtocol())){
					tipiIntegrazione = ConsegnaContenutiApplicativi.defaultPerProtocolloGestoreIntegrazionePA.get(protocolFactory.getProtocol());
				}else{
					tipiIntegrazione = ConsegnaContenutiApplicativi.defaultGestoriIntegrazionePA;
				}
			}
			
			OutRequestPAMessage outRequestPAMessage = new OutRequestPAMessage();
			outRequestPAMessage.setBustaRichiesta(bustaRichiesta);
			outRequestPAMessage.setMessage(consegnaMessagePrimaTrasformazione);
			if(pa!=null)
				outRequestPAMessage.setPortaApplicativa(pa);
			else
				outRequestPAMessage.setPortaDelegata(pd);
			outRequestPAMessage.setHeaders(propertiesTrasporto);
			outRequestPAMessage.setParameters(propertiesUrlBased);
			if(servizioHeaderIntegrazione!=null){
				outRequestPAMessage.setServizio(servizioHeaderIntegrazione);
			}else{
				outRequestPAMessage.setServizio(idServizio);
			}
			if(soggettoFruitoreHeaderIntegrazione!=null){
				outRequestPAMessage.setSoggettoMittente(soggettoFruitoreHeaderIntegrazione);
			}else{
				outRequestPAMessage.setSoggettoMittente(soggettoFruitore);
			}
			for(int i=0; i<tipiIntegrazione.length;i++){
				try{
					IGestoreIntegrazionePA gestore = null;
					try{
						gestore = (IGestoreIntegrazionePA) this.pluginLoader.newIntegrazionePortaApplicativa(tipiIntegrazione[i]);
					}catch(Exception e){
						throw e;
					}
					if(gestore!=null){
						String classType = null;
						try {
							classType = gestore.getClass().getName();
							AbstractCore.init(gestore, pddContext, protocolFactory);
						}catch(Exception e){
							throw new Exception("Riscontrato errore durante l'inizializzazione della classe ["+classType+
									"] da utilizzare per la gestione dell'integrazione delle erogazione di tipo ["+tipiIntegrazione[i]+"]: "+e.getMessage());
						}
						if(gestore instanceof IGestoreIntegrazionePASoap){
							if(this.propertiesReader.processHeaderIntegrazionePARequest(false)){
								if(this.propertiesReader.deleteHeaderIntegrazioneRequestPA()){
									gestore.setOutRequestHeader(headerIntegrazione,outRequestPAMessage);
								}
								else{
									// gia effettuato l'update dell'header in RicezioneBuste
								}
							}else{
								gestore.setOutRequestHeader(headerIntegrazione,outRequestPAMessage);
							}
						}else{
							gestore.setOutRequestHeader(headerIntegrazione,outRequestPAMessage);
						}
					}else{
						throw new Exception("Gestore non inizializzato");
					}

				}catch(Exception e){
					msgDiag.addKeyword(CostantiPdD.KEY_TIPO_HEADER_INTEGRAZIONE,tipiIntegrazione[i]);
					msgDiag.addKeywordErroreProcessamento(e);
					msgDiag.logPersonalizzato("headerIntegrazione.creazioneFallita");
					this.log.error(msgDiag.getMessaggio_replaceKeywords("headerIntegrazione.creazioneFallita"), e);
				}
			}






			
			
			
			
			
			
			/* ------------ Trasformazione Richiesta  -------------- */
			
			GestoreTrasformazioni gestoreTrasformazioni = null;
			consegnaMessageTrasformato = consegnaMessagePrimaTrasformazione;
			if(trasformazioni!=null) {
				try {
					gestoreTrasformazioni = new GestoreTrasformazioni(this.log, msgDiag, idServizio, soggettoFruitore, servizioApplicativoFruitore, 
							trasformazioni, transactionNullable, pddContext, requestInfo, tipoPdD,
							generatoreErrorePA);
					consegnaMessageTrasformato = gestoreTrasformazioni.trasformazioneRichiesta(consegnaMessagePrimaTrasformazione, bustaRichiesta);
				}
				catch(GestoreTrasformazioniException e) {
					
					msgDiag.addKeywordErroreProcessamento(e);
					msgDiag.logPersonalizzato("trasformazione.processamentoRichiestaInErrore");
					
					pddContext.addObject(org.openspcoop2.core.constants.Costanti.ERRORE_TRASFORMAZIONE_RICHIESTA, "true");
					
					ErroreIntegrazione erroreIntegrazione = gestoreTrasformazioni.getErrore();
					if(erroreIntegrazione==null) {
						erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
								get5XX_ErroreProcessamento(e,CodiceErroreIntegrazione.CODICE_562_TRASFORMAZIONE);
					}
					
					String msgErrore = e.getMessage();					
					if(existsModuloInAttesaRispostaApplicativa) {
						IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.TRANSFORMATION_RULE_REQUEST_FAILED;
						if(e.getOp2IntegrationFunctionError()!=null) {
							integrationFunctionError = e.getOp2IntegrationFunctionError();
						}
						ejbUtils.setIntegrationFunctionErrorPortaApplicativa(integrationFunctionError);
						if(localForwardEngine!=null) {
							localForwardEngine.setIntegrationFunctionError(integrationFunctionError);
						}
						if(e.getOpenSPCoop2ErrorMessage()!=null) {
							this.sendErroreProcessamento(localForward, localForwardEngine, ejbUtils, 
									e.getOpenSPCoop2ErrorMessage(), msgErrore,
									idModuloInAttesa, bustaRichiesta, idCorrelazioneApplicativa, idCorrelazioneApplicativaRisposta, servizioApplicativoFruitore);
						}
						else {
							this.sendErroreProcessamento(localForward, localForwardEngine, ejbUtils, 
									erroreIntegrazione,
									idModuloInAttesa, bustaRichiesta, idCorrelazioneApplicativa, idCorrelazioneApplicativaRisposta, servizioApplicativoFruitore, e,
									(responseMessage!=null ? responseMessage.getParseException() : null),
									pddContext);
						}
						esito.setEsitoInvocazione(true); 
						esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO, msgErrore);
					}else{
						ejbUtils.rollbackMessage(msgErrore,servizioApplicativo, esito);
						esito.setEsitoInvocazione(false); 
						esito.setStatoInvocazioneErroreNonGestito(e);
					}
					openspcoopstate.releaseResource();
					return esito;
				}		
			}	
			
			
			
			
			
			
			
			
			
			
			





			/* ------------------- Definizione connettoreMsg -----------------------*/
						
			// mapping in valori delle keyword delle proprieta di trasporto protocol-properties.
			msgDiag.mediumDebug("Impostazione messaggio del connettore...");
			mappingProtocolProperties(connettoreMsg.getPropertiesTrasporto(), propertiesTrasporto, 
					soggettoFruitoreHeaderIntegrazione, servizioHeaderIntegrazione, soggettoFruitore, idServizio, bustaRichiesta, idCorrelazioneApplicativa);
			mappingProtocolProperties(connettoreMsg.getPropertiesUrlBased(), propertiesUrlBased, 
					soggettoFruitoreHeaderIntegrazione, servizioHeaderIntegrazione, soggettoFruitore, idServizio, bustaRichiesta, idCorrelazioneApplicativa);

			// definizione connettore
			connettoreMsg.setRequestMessage(consegnaMessageTrasformato);
			connettoreMsg.setIdModulo(ConsegnaContenutiApplicativi.ID_MODULO);
			connettoreMsg.setPropertiesTrasporto(propertiesTrasporto);
			connettoreMsg.setPropertiesUrlBased(propertiesUrlBased);
			connettoreMsg.setBusta(bustaRichiesta);
			connettoreMsg.setMsgDiagnostico(msgDiag);
			connettoreMsg.setState(openspcoopstate.getStatoRichiesta());
			if(consegnaMessagePrimaTrasformazione!=null && consegnaMessagePrimaTrasformazione.getTransportRequestContext()!=null) {
				connettoreMsg.setUrlInvocazionePorta(consegnaMessagePrimaTrasformazione.getTransportRequestContext().getUrlInvocazione_formBased());
			}

			bustaRichiesta.setID(originalID);









			/* ------------------- Preparo Spedizione -----------------------*/
			msgDiag.mediumDebug("Inizializzazione connettore per la spedizione...");
			//	Connettore per consegna
			String tipoConnector = connettoreMsg.getTipoConnettore();
			msgDiag.addKeyword(CostantiPdD.KEY_TIPO_CONNETTORE, tipoConnector);
			IConnettore connectorSender = null;

			// mapping per forward token
			TokenForward tokenForward = null;
			Object oTokenForward = consegnaMessageTrasformato.getContextProperty(org.openspcoop2.pdd.core.token.Costanti.MSG_CONTEXT_TOKEN_FORWARD);
			if(oTokenForward!=null) {
				tokenForward = (TokenForward) oTokenForward;
			}
			if(tokenForward!=null) {
				if(tokenForward.getTrasporto()!=null && tokenForward.getTrasporto().size()>0) {
					propertiesTrasporto.putAll(tokenForward.getTrasporto());
				}
				if(tokenForward.getUrl()!=null && tokenForward.getUrl().size()>0) {
					propertiesUrlBased.putAll(tokenForward.getUrl());
				}
			}
			
			// Risposte del connettore
			int codiceRitornato = -1;
			TransportResponseContext transportResponseContext = null;

			// Stato consegna tramite connettore
			boolean errorConsegna = false;
			boolean riconsegna = false;
			java.sql.Timestamp dataRiconsegna = null;
			String motivoErroreConsegna = null;
			boolean invokerNonSupportato = false;
			SOAPFault soapFault = null;
			ProblemRFC7807 restProblem = null;
			OpenSPCoop2MessageFactory faultMessageFactory = null;
			Exception eccezioneProcessamentoConnettore = null;

			// Carico connettore richiesto
			String connectorClass = null;
			Exception eInvokerNonSupportato = null;
			if(invokerNonSupportato==false){
				try{
					connectorSender = (IConnettore) this.pluginLoader.newConnettore(tipoConnector);
				}
				catch(Exception e){
					msgDiag.logErroreGenerico(e,"Inizializzazione Connettore"); // l'errore contiene gia tutte le informazioni
					invokerNonSupportato = true;
					eInvokerNonSupportato = e;
				}
				if(connectorSender!=null) {
					try {
						connectorClass = connectorSender.getClass().getName();
						AbstractCore.init(connectorSender, pddContext, protocolFactory);
					}catch(Exception e){
						msgDiag.logErroreGenerico(e,"IConnettore.newInstance(tipo:"+tipoConnector+" class:"+connectorClass+")");
						invokerNonSupportato = true;
						eInvokerNonSupportato = e;
					}
				}
				if( (invokerNonSupportato == false) && (connectorSender == null)){
					msgDiag.logErroreGenerico("ConnectorSender is null","IConnettore.newInstance(tipo:"+tipoConnector+" class:"+connectorClass+")");
					invokerNonSupportato = true;
				}
			}
			
			// Imposto tipo di richiesta
			HttpRequestMethod httpRequestMethod = null;
			if(connectorSender!=null){
				try{
					if(connectorSender instanceof ConnettoreBaseHTTP){
						ConnettoreBaseHTTP baseHttp = (ConnettoreBaseHTTP) connectorSender;
						baseHttp.setHttpMethod(consegnaMessageTrasformato);
						
						if(ServiceBinding.REST.equals(consegnaMessageTrasformato.getServiceBinding())){
							httpRequestMethod = baseHttp.getHttpMethod();
						}
					}
				}catch(Exception e){
					msgDiag.logErroreGenerico(e,"ConnettoreBaseHTTP.setHttpMethod(tipo:"+tipoConnector+" class:"+connectorClass+")");
					invokerNonSupportato = true;
					eInvokerNonSupportato = e;
				}
			}

			// Location
			location = ConnettoreUtils.getAndReplaceLocationWithBustaValues(connectorSender, connettoreMsg, bustaRichiesta, pddContext, protocolFactory,  this.log);
			if(location!=null){
				String locationWithUrl = ConnettoreUtils.buildLocationWithURLBasedParameter(this.log, consegnaMessageTrasformato, connettoreMsg.getTipoConnettore(), connettoreMsg.getPropertiesUrlBased(), location,
						protocolFactory, this.idModulo);
				locationWithUrl = ConnettoreUtils.addProxyInfoToLocationForHTTPConnector(connettoreMsg.getTipoConnettore(), connettoreMsg.getConnectorProperties(), locationWithUrl);
				locationWithUrl = ConnettoreUtils.addGovWayProxyInfoToLocationForHTTPConnector(connettoreMsg.getForwardProxy(),connectorSender, locationWithUrl);
				msgDiag.addKeyword(CostantiPdD.KEY_LOCATION, ConnettoreUtils.formatLocation(httpRequestMethod, locationWithUrl));
				
				pddContext.addObject(CostantiPdD.CONNETTORE_REQUEST_METHOD, httpRequestMethod);
				pddContext.addObject(CostantiPdD.CONNETTORE_REQUEST_URL, locationWithUrl);
			}
			else{
				msgDiag.addKeyword(CostantiPdD.KEY_LOCATION, "N.D.");
			}

			// timeout di default
			if(connettoreMsg.getConnectorProperties()==null){
				java.util.Map<String,String> propCon = new java.util.HashMap<String,String>();
				connettoreMsg.setConnectorProperties(propCon);
			}
			if(connettoreMsg.getConnectorProperties().get(CostantiConnettori.CONNETTORE_CONNECTION_TIMEOUT)==null){
				connettoreMsg.getConnectorProperties().put(CostantiConnettori.CONNETTORE_CONNECTION_TIMEOUT,""+this.propertiesReader.getConnectionTimeout_consegnaContenutiApplicativi());
			}
			if(connettoreMsg.getConnectorProperties().get(CostantiConnettori.CONNETTORE_READ_CONNECTION_TIMEOUT)==null){
				connettoreMsg.getConnectorProperties().put(CostantiConnettori.CONNETTORE_READ_CONNECTION_TIMEOUT,""+this.propertiesReader.getReadConnectionTimeout_consegnaContenutiApplicativi());
			}

			// behaviourForwardToConfiguration
			if(behaviourForwardToConfiguration!=null){
				if(behaviourForwardToConfiguration.getSbustamentoInformazioniProtocollo()!=null){
					if(org.openspcoop2.pdd.core.behaviour.StatoFunzionalita.ABILITATA.equals(behaviourForwardToConfiguration.getSbustamentoInformazioniProtocollo())){
						connettoreMsg.setSbustamentoInformazioniProtocollo(true);
					}
					else if(org.openspcoop2.pdd.core.behaviour.StatoFunzionalita.DISABILITATA.equals(behaviourForwardToConfiguration.getSbustamentoInformazioniProtocollo())){
						connettoreMsg.setSbustamentoInformazioniProtocollo(false);
					}
				}
				if(behaviourForwardToConfiguration.getSbustamentoSoap()!=null){
					if(org.openspcoop2.pdd.core.behaviour.StatoFunzionalita.ABILITATA.equals(behaviourForwardToConfiguration.getSbustamentoSoap())){
						connettoreMsg.setSbustamentoSOAP(true);
					}
					else if(org.openspcoop2.pdd.core.behaviour.StatoFunzionalita.DISABILITATA.equals(behaviourForwardToConfiguration.getSbustamentoSoap())){
						connettoreMsg.setSbustamentoSOAP(false);
					}
				}
			}
			
			// GestioneErrore
			GestioneErrore gestioneConsegnaConnettore = null;
			if(gestioneErroreBehaviour!=null) {
				gestioneConsegnaConnettore = gestioneErroreBehaviour;
			}
			else {
				if(Costanti.SCENARIO_CONSEGNA_CONTENUTI_APPLICATIVI.equals(scenarioCooperazione) ||
						Costanti.SCENARIO_ASINCRONO_SIMMETRICO_CONSEGNA_RISPOSTA.equals(scenarioCooperazione) ){
					try{
						msgDiag.mediumDebug("Inizializzo contesto per la gestione (getGestioneErroreConnettore_RispostaAsincrona) [ConsegnaContenuti/AsincronoSimmetricoRisposta]...");
						gestioneConsegnaConnettore = configurazionePdDManager.getGestioneErroreConnettore_RispostaAsincrona(protocolFactory, consegnaMessageTrasformato.getServiceBinding(), sa);
					}catch(Exception e){
						msgDiag.logErroreGenerico(e, "ConsegnaAsincrona.getDatiConsegna(sa:"+servizioApplicativo+")");
						ejbUtils.rollbackMessage("[ConsegnaAsincrona] Connettore per consegna applicativa non definito:"+e.getMessage(),servizioApplicativo, esito);
						esito.setEsitoInvocazione(false); 
						esito.setStatoInvocazioneErroreNonGestito(e);
						return esito;
					}
	
	
				}
				else if(Costanti.SCENARIO_ASINCRONO_ASIMMETRICO_POLLING.equals(scenarioCooperazione)){
					try{
						msgDiag.mediumDebug("Inizializzo contesto per la gestione (getGestioneErroreConnettore_RispostaAsincrona) [AsincronoAsimmetricoPolling]...");
						gestioneConsegnaConnettore = configurazionePdDManager.getGestioneErroreConnettore_RispostaAsincrona(protocolFactory, consegnaMessageTrasformato.getServiceBinding(), sa);
					}catch(Exception e){
						msgDiag.logErroreGenerico(e, "AsincronoSimmetricoPolling.getDatiConsegna(sa:"+servizioApplicativo+")");
						ejbUtils.rollbackMessage("[AsincronoSimmetricoPolling] Connettore per consegna applicativa non definito:"+e.getMessage(),servizioApplicativo, esito);
						esito.setEsitoInvocazione(false); 
						esito.setStatoInvocazioneErroreNonGestito(e);
						return esito;
					}
				}else{
					try{
						gestioneConsegnaConnettore = configurazionePdDManager.getGestioneErroreConnettore_InvocazioneServizio(protocolFactory, consegnaMessageTrasformato.getServiceBinding(), sa);
						msgDiag.mediumDebug("Inizializzo contesto per la gestione (invocazioneServizioPerRiferimento)...");
					}catch(Exception e){
						msgDiag.logErroreGenerico(e, "InvocazioneServizio.getDatiConsegna(sa:"+servizioApplicativo+")");
						ejbUtils.rollbackMessage("Connettore per consegna applicativa non definito:"+e.getMessage(),servizioApplicativo, esito);
						esito.setEsitoInvocazione(false); 
						esito.setStatoInvocazioneErroreNonGestito(e);
						return esito;
					}
				}
			}
			if(gestioneConsegnaConnettore==null){
				msgDiag.logErroreGenerico("Gestore Errore di consegna non definito nella configurazione (is null)", "getDatiConsegna(sa:"+servizioApplicativo+")");
				ejbUtils.rollbackMessage("Gestione Errore di consegna non definito per il sa ["+servizioApplicativo+"]",servizioApplicativo, esito);
				esito.setEsitoInvocazione(false); 
				esito.setStatoInvocazione(EsitoLib.ERRORE_NON_GESTITO, "Gestione Errore di consegna non definito per il sa ["+servizioApplicativo+"]");
				return esito;
			}
			
			
			
			
			
			
			
			/* ------------------- OutRequestHandler -----------------------*/
			OutRequestContext outRequestContext = null;
			try{
				outRequestContext = new OutRequestContext(this.log,protocolFactory,openspcoopstate.getStatoRichiesta());
				
				// Informazioni connettore in uscita
				InfoConnettoreUscita infoConnettoreUscita = new InfoConnettoreUscita();
				infoConnettoreUscita.setLocation(location);
				infoConnettoreUscita.setProperties(connettoreMsg.getConnectorProperties());
				infoConnettoreUscita.setHeaders(connettoreMsg.getPropertiesTrasporto());
				infoConnettoreUscita.setParameters(connettoreMsg.getPropertiesUrlBased());
				infoConnettoreUscita.setSbustamentoSoap(connettoreMsg.isSbustamentoSOAP());
				infoConnettoreUscita.setSbustamentoInformazioniProtocollo(connettoreMsg.isSbustamentoInformazioniProtocollo());
				infoConnettoreUscita.setTipoAutenticazione(connettoreMsg.getAutenticazione());
				infoConnettoreUscita.setCredenziali(connettoreMsg.getCredenziali());
				infoConnettoreUscita.setTipoConnettore(connettoreMsg.getTipoConnettore());
				outRequestContext.setConnettore(infoConnettoreUscita);
				
				// Informazioni messaggio
				outRequestContext.setMessaggio(consegnaMessageTrasformato);
				
				// TransazioneApplicativoServer
				outRequestContext.setTransazioneApplicativoServer(transazioneApplicativoServer);
				
				// Contesto
				ProtocolContext protocolContext = new ProtocolContext();
				protocolContext.setIdRichiesta(idMessaggioConsegna);
				if(idMessaggioPreBehaviour!=null){
					protocolContext.setIdRichiesta(idMessaggioPreBehaviour);
				}
				protocolContext.setFruitore(soggettoFruitore);
				if(bustaRichiesta!=null){
					protocolContext.setIndirizzoFruitore(bustaRichiesta.getIndirizzoMittente());
				}
				if(idServizio!=null){
					protocolContext.setErogatore(idServizio.getSoggettoErogatore());
					if(bustaRichiesta!=null){
						protocolContext.setIndirizzoErogatore(bustaRichiesta.getIndirizzoDestinatario());
					}
					protocolContext.setTipoServizio(idServizio.getTipo());
					protocolContext.setServizio(idServizio.getNome());
					protocolContext.setVersioneServizio(idServizio.getVersione());
					protocolContext.setAzione(idServizio.getAzione());
				}
				if(idAccordoServizio!=null){
					protocolContext.setIdAccordo(idAccordoServizio);
				}
				else if(idServizio!=null && idServizio.getUriAccordoServizioParteComune()!=null){
					protocolContext.setIdAccordo(IDAccordoFactory.getInstance().getIDAccordoFromUri(idServizio.getUriAccordoServizioParteComune()));
				}
				String profiloCollorazioneValue = null;
				if(bustaRichiesta!=null){
					profiloCollorazioneValue = bustaRichiesta.getProfiloDiCollaborazioneValue();
				}
				protocolContext.setProfiloCollaborazione(profiloCollaborazione,profiloCollorazioneValue);
				if(bustaRichiesta!=null){
					protocolContext.setCollaborazione(bustaRichiesta.getCollaborazione());
				}
				protocolContext.setDominio(msgDiag.getDominio());
				protocolContext.setScenarioCooperazione(scenarioCooperazione);
				outRequestContext.setProtocollo(protocolContext);
				
				// Integrazione
				IntegrationContext integrationContext = new IntegrationContext();
				integrationContext.setIdCorrelazioneApplicativa(idCorrelazioneApplicativa);
				integrationContext.setServizioApplicativoFruitore(servizioApplicativoFruitore);
				integrationContext.addServizioApplicativoErogatore(servizioApplicativo);
				integrationContext.setGestioneStateless(portaDiTipoStateless);
				integrationContext.setIdPA(idPA);
				integrationContext.setIdPD(idPD);
				outRequestContext.setIntegrazione(integrationContext);
				
				// Altre informazioni
				outRequestContext.setDataElaborazioneMessaggio(DateManager.getDate());
				outRequestContext.setPddContext(pddContext);
				outRequestContext.setTipoPorta(TipoPdD.APPLICATIVA);
				outRequestContext.setIdModulo(this.idModulo);

				// Invocazione handler
				GestoreHandlers.outRequest(outRequestContext, msgDiag, this.log);
				
				// Riporto messaggio
				consegnaMessageTrasformato = outRequestContext.getMessaggio();
				
				// Salvo handler
				connettoreMsg.setOutRequestContext(outRequestContext);
				
			}
			catch(Exception e){
				String msgErrore = null;
				if(e!=null && e.getMessage()!=null){
					if(e.getMessage().length()>150){
						msgErrore = e.getMessage().substring(0, 150);
					}else{
						msgErrore = e.getMessage();
					}
				}
				ErroreIntegrazione erroreIntegrazione = null;
				IntegrationFunctionError integrationFunctionError = null;
				if(e instanceof HandlerException){
					HandlerException he = (HandlerException) e;
					if(he.isEmettiDiagnostico()){
						msgDiag.logErroreGenerico(e, ((HandlerException)e).getIdentitaHandler());
					}
					msgErrore = ((HandlerException)e).getIdentitaHandler()+" error: "+msgErrore;
					if(existsModuloInAttesaRispostaApplicativa) {
						erroreIntegrazione = he.convertToErroreIntegrazione();
						integrationFunctionError = he.getIntegrationFunctionError();
					}
				}else{
					msgDiag.logErroreGenerico(e, "OutRequestHandler");
					msgErrore = "OutRequestHandler error: "+msgErrore;
				}
				if(existsModuloInAttesaRispostaApplicativa) {
					
					if(erroreIntegrazione==null){
						erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
								get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_543_HANDLER_OUT_REQUEST);
					}
					
					if(integrationFunctionError!=null) {
						ejbUtils.setIntegrationFunctionErrorPortaApplicativa(integrationFunctionError);
						if(localForwardEngine!=null) {
							localForwardEngine.setIntegrationFunctionError(integrationFunctionError);
						}
					}
					this.sendErroreProcessamento(localForward, localForwardEngine, ejbUtils, 
							erroreIntegrazione,
							idModuloInAttesa, bustaRichiesta, idCorrelazioneApplicativa, idCorrelazioneApplicativaRisposta, servizioApplicativoFruitore, e,
							(responseMessage!=null ? responseMessage.getParseException() : null),
							pddContext);
					
					esito.setEsitoInvocazione(true); 
					esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO, msgErrore);
				}else{
					ejbUtils.rollbackMessage(msgErrore,servizioApplicativo, esito);
					esito.setEsitoInvocazione(false); 
					esito.setStatoInvocazioneErroreNonGestito(e);
				}
				openspcoopstate.releaseResource();
				return esito;
			}
			
			
			
			
			
			
			
			
			
			
			
			
			/* --------------- REFRESH LOCATION ----------------- */
			// L'handler puo' aggiornare le properties che contengono le proprieta' del connettore.
			location = ConnettoreUtils.getAndReplaceLocationWithBustaValues(connectorSender, connettoreMsg, bustaRichiesta, pddContext, protocolFactory, this.log);
			if(location!=null){
				String locationWithUrl = ConnettoreUtils.buildLocationWithURLBasedParameter(this.log, consegnaMessageTrasformato, connettoreMsg.getTipoConnettore(), connettoreMsg.getPropertiesUrlBased(), location,
						protocolFactory, this.idModulo);
				locationWithUrl = ConnettoreUtils.addProxyInfoToLocationForHTTPConnector(connettoreMsg.getTipoConnettore(), connettoreMsg.getConnectorProperties(), locationWithUrl);
				locationWithUrl = ConnettoreUtils.addGovWayProxyInfoToLocationForHTTPConnector(connettoreMsg.getForwardProxy(),connectorSender, locationWithUrl);
				msgDiag.addKeyword(CostantiPdD.KEY_LOCATION, ConnettoreUtils.formatLocation(httpRequestMethod, locationWithUrl));
				
				pddContext.addObject(CostantiPdD.CONNETTORE_REQUEST_METHOD, httpRequestMethod);
				pddContext.addObject(CostantiPdD.CONNETTORE_REQUEST_URL, locationWithUrl);
			}
			else{
				msgDiag.addKeyword(CostantiPdD.KEY_LOCATION, "N.D.");
			}
			
			
			
			
			
			
			
			
			
			
			
			
			/* ------------------- Dump -----------------------*/
				
			// Invoco il metodo getMessage del ConnettoreMsg per provocare l'eventuale sbustamento delle informazioni di protocollo
			connettoreMsg.getRequestMessage(requestInfo, pddContext);
			
			String idMessaggioDumpRichiesta = idMessaggioConsegna;
			if(idMessaggioPreBehaviour!=null){
				idMessaggioDumpRichiesta = idMessaggioPreBehaviour;
			}
			
			Dump dumpApplicativoRichiesta = new Dump(identitaPdD,ConsegnaContenutiApplicativi.ID_MODULO,idMessaggioDumpRichiesta,
					soggettoFruitore,idServizio,TipoPdD.APPLICATIVA,msgDiag.getPorta(),pddContext,
					openspcoopstate.getStatoRichiesta(),openspcoopstate.getStatoRisposta(),
					dumpConfig);
			if(transazioneApplicativoServer!=null) {
				dumpApplicativoRichiesta.setTransazioneApplicativoServer(transazioneApplicativoServer, idPA, dataConsegna);
			}
			dumpApplicativoRichiesta.dumpRichiestaUscita(consegnaMessageTrasformato, outRequestContext.getConnettore());

			
			
			
			
			
			
			
			
			
			
			
			
			
			/* ------------------- 
			   Rilascio Risorse (Le riprendero' dopo aver ottenuto la risposta, se necessario) 
			   Le informazioni nel DB sono state utilizzate fino a questo punto solo in lettura.
			   Eventuali spedizioni JMS sono state effettuate e le risorse gia' rilasciate (non arrivero a questo punto)
			   -----------------------*/
			msgDiag.mediumDebug("Rilascio connessione al database...");
			openspcoopstate.releaseResource();
			
			
			
			
			
			
			

			// --------------------- spedizione --------------------------
			Date dataPrimaInvocazioneConnettore = null;
			Date dataTerminataInvocazioneConnettore = null;
			if(invokerNonSupportato==false){
				msgDiag.logPersonalizzato("consegnaInCorso");
				// utilizzo connettore
				ejbUtils.setSpedizioneMsgIngresso(new Timestamp(outRequestContext.getDataElaborazioneMessaggio().getTime()));
				dataPrimaInvocazioneConnettore = DateManager.getDate();
				errorConsegna = !connectorSender.send(responseCachingConfig, connettoreMsg);
				dataTerminataInvocazioneConnettore = DateManager.getDate();
			}
			
			
			
			Utilities.printFreeMemory("ConsegnaContenuti - Richiesta risorsa per la gestione della risposta...");
			
			
			
			
			/* ------------  Re-ottengo Connessione al DB -------------- */
			msgDiag.mediumDebug("Richiesta risorsa per la gestione della risposta...");
			try{
				boolean gestioneAsincroniStateless = 
					(ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO.equals(profiloCollaborazione) ||
							ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO.equals(profiloCollaborazione))
							&& portaDiTipoStateless;
				boolean oldGestioneConnessione = false;
				if(gestioneAsincroniStateless){
					oldGestioneConnessione = ((OpenSPCoopStateless)openspcoopstate).isUseConnection();
					((OpenSPCoopStateless)openspcoopstate).setUseConnection(true);
				}
				openspcoopstate.updateResource(idTransazione);
				if(gestioneAsincroniStateless){
					((OpenSPCoopStateless)openspcoopstate).setUseConnection(oldGestioneConnessione);
				}
				
				// Aggiorno risorse
				ejbUtils.updateOpenSPCoopState(openspcoopstate);
				msgRequest.updateOpenSPCoopState(openspcoopstate);
				
				// POOL,TRANSACTIONISOLATION:
				//connectionDB.setTransactionIsolation(DBManager.getTransactionIsolationLevel());
			}catch(Exception e){
				msgDiag.logErroreGenerico(e,"openspcoopstate.updateResource()");
				openspcoopstate.releaseResource();
				esito.setEsitoInvocazione(false);	
				esito.setStatoInvocazioneErroreNonGestito(e);
				return esito;
			}
			
			
			
			/* ------------  Analisi Risposta -------------- */
			if(invokerNonSupportato==false){
			
				try {
					msgDiag.mediumDebug("Analisi Risposta");
				
					// nota per lo stato si intende un esito di errore connettore quando è proprio il connettore a restituire errore.
					// se invece il connettore esce "bene" e restituisce poi un codice http e/o una risposta, si rientra nei casi sottostanti
					pddContext.addObject(org.openspcoop2.core.constants.Costanti.ERRORE_UTILIZZO_CONNETTORE, errorConsegna);
					
					ejbUtils.setRicezioneMsgRisposta(DateManager.getTimestamp());
					motivoErroreConsegna = connectorSender.getErrore();
					eccezioneProcessamentoConnettore = connectorSender.getEccezioneProcessamento();
					if(errorConsegna && motivoErroreConsegna==null){
						motivoErroreConsegna = "Errore durante la consegna";
					}
					//	interpretazione esito consegna
					GestoreErroreConnettore gestoreErrore = new GestoreErroreConnettore();
					errorConsegna = !gestoreErrore.verificaConsegna(gestioneConsegnaConnettore,motivoErroreConsegna,eccezioneProcessamentoConnettore,connectorSender);
					if(errorConsegna){
						motivoErroreConsegna = gestoreErrore.getErrore();
						riconsegna = gestoreErrore.isRiconsegna();
						dataRiconsegna = gestoreErrore.getDataRispedizione();
					}
					// dopo aver verificato se siamo in un caso di errore, vediamo se l'errore è dovuto al codice di trasporto
					// in tal caso rientriamo in un utilizzo del connettore con errore.
					if(errorConsegna) {
						if(connectorSender.getResponse()==null) {
							pddContext.addObject(org.openspcoop2.core.constants.Costanti.ERRORE_UTILIZZO_CONNETTORE, errorConsegna);
						}
					}
					if(transazioneApplicativoServer!=null) {
						transazioneApplicativoServer.setConsegnaTerminata(!errorConsegna);
					}
					// raccolta risultati del connettore
					soapFault = gestoreErrore.getFault();
					restProblem = gestoreErrore.getProblem();
					faultMessageFactory = connectorSender.getResponse()!=null ? connectorSender.getResponse().getFactory() : OpenSPCoop2MessageFactory.getDefaultMessageFactory();
					codiceRitornato = connectorSender.getCodiceTrasporto();
					transportResponseContext = new TransportResponseContext(this.log, connectorSender.getCodiceTrasporto()+"", 
							connectorSender.getHeaderTrasporto(), 
							connectorSender.getContentLength(), 
							motivoErroreConsegna, connectorSender.getEccezioneProcessamento());
					responseMessage = connectorSender.getResponse();
					if(responseMessage!=null){
						responseMessage.setTransportRequestContext(consegnaMessagePrimaTrasformazione.getTransportRequestContext());
						responseMessage.setTransportResponseContext(transportResponseContext);
						responseContentLength = connectorSender.getContentLength();
					}
					// gestione connessione connettore
					if(existsModuloInAttesaRispostaApplicativa) {
						if(localForward){
							RepositoryConnettori.salvaConnettorePD(
									//idMessaggioConsegna,
									idTransazione,
									connectorSender);
						}else{
							RepositoryConnettori.salvaConnettorePA(
									//idMessaggioConsegna, 
									idTransazione,
									connectorSender);
						}
					}else{
						// Sono nella casistica di messaggio preso in carico.
						// Non si deve chiudere immediatamente la connessione, poiche' nel resto del modulo, il messaggio puo' ancora essere utilizzato (es. dump)
						connectorSenderForDisconnect = connectorSender;
					}			
					msgDiag.addKeyword(CostantiPdD.KEY_CODICE_CONSEGNA, codiceRitornato+"");
					if(motivoErroreConsegna!=null)
						msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_CONSEGNA, motivoErroreConsegna);
					
					// Il Connettore potrebbe aggiungere informazioni alla location.
					String tmpLocation = connectorSender.getLocation();
					if(tmpLocation!=null){
						
						// salvo la request url originale, se la risposta non è letta dalla cache
						boolean responseCached = false;
						if(pddContext.containsKey(ConnettoreBase.RESPONSE_FROM_CACHE)) {
							responseCached = (Boolean) pddContext.getObject(ConnettoreBase.RESPONSE_FROM_CACHE);
						}
						if(!responseCached) {
							pddContext.addObject(CostantiPdD.CONNETTORE_REQUEST_METHOD, httpRequestMethod);
							pddContext.addObject(CostantiPdD.CONNETTORE_REQUEST_URL, tmpLocation);
						}
						
						// aggiorno
						location = tmpLocation;
						if(responseCached) {
							msgDiag.addKeyword(CostantiPdD.KEY_LOCATION, location);
						}
						else {
							msgDiag.addKeyword(CostantiPdD.KEY_LOCATION, ConnettoreUtils.formatLocation(httpRequestMethod, location));
						}
					}
					
					/* ------------ Check Charset ------------- */
					try {
						if(transportResponseContext!=null) {
							boolean checkEnabled = false;
							List<String> ctDefault = null;
							if(consegnaMessageTrasformato!=null && ServiceBinding.SOAP.equals(consegnaMessageTrasformato.getServiceBinding())){
								if(this.propertiesReader.isControlloCharsetContentTypeAbilitatoRicezioneBusteSoap()) {
									checkEnabled = true;
									ctDefault = this.propertiesReader.getControlloCharsetContentTypeAbilitatoRicezioneBusteSoap();
								}
							}
							else {
								if(this.propertiesReader.isControlloCharsetContentTypeAbilitatoRicezioneBusteRest()) {
									checkEnabled = true;
									ctDefault = this.propertiesReader.getControlloCharsetContentTypeAbilitatoRicezioneBusteRest();
								}
							}
							if(checkEnabled) {
								if(transportResponseContext.getContentType()!=null) {
									ServicesUtils.checkCharset(transportResponseContext.getContentType(), ctDefault, msgDiag, false, TipoPdD.APPLICATIVA);
								}
							}
						}
					}catch(Throwable t) {
						String ct = null;
						try {
							if(transportResponseContext!=null) {
								ct = transportResponseContext.getContentType();
							}
						}catch(Throwable tRead) {}	
						this.log.error("Avvenuto errore durante il controllo del charset della risposta (Content-Type: "+ct+"): "+t.getMessage(),t);
					}
					
				} catch (Exception e) {
					msgDiag.addKeywordErroreProcessamento(e, "Analisi risposta fallita");
					msgDiag.logErroreGenerico(e,"AnalisiRispostaConnettore");
					String msgErrore = "Analisi risposta del connettore ha provocato un errore: "+e.getMessage();
					this.log.error(msgErrore,e);
					if(existsModuloInAttesaRispostaApplicativa) {
						
						this.sendErroreProcessamento(localForward, localForwardEngine, ejbUtils, 
								ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
								get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_500_ERRORE_INTERNO),
								idModuloInAttesa, bustaRichiesta, idCorrelazioneApplicativa, idCorrelazioneApplicativaRisposta, servizioApplicativoFruitore, e,
								(connectorSender!=null && connectorSender.getResponse()!=null ? connectorSender.getResponse().getParseException() : null),
								pddContext);
						
						esito.setEsitoInvocazione(true); 
						esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO, msgErrore);
					}else{
						ejbUtils.rollbackMessage(msgErrore,servizioApplicativo, esito);
						esito.setEsitoInvocazione(false); 
						esito.setStatoInvocazioneErroreNonGestito(e);
					}
					openspcoopstate.releaseResource();
					return esito;
				}
			}




			
			
			
			
			
			
			
			/* ------------ Trasformazione Risposta  -------------- */
			
			boolean dumpRispostaEffettuato = false;
			if(trasformazioni!=null && responseMessage!=null
					&& transazioneApplicativoServer==null // non ha senso per le notifiche asincrone
					) {
				try {
					
					// prima effettuo dump applicativo
					if(responseMessage!=null ){
						
						String idMessaggioDump = idMessaggioConsegna;
						if(idMessaggioPreBehaviour!=null){
							idMessaggioDump = idMessaggioPreBehaviour;
						}
						
						Dump dumpApplicativo = new Dump(identitaPdD,ConsegnaContenutiApplicativi.ID_MODULO,idMessaggioDump,
								soggettoFruitore,idServizio,TipoPdD.APPLICATIVA,msgDiag.getPorta(),pddContext,
								openspcoopstate.getStatoRichiesta(),openspcoopstate.getStatoRisposta(),
								dumpConfig);
						/*if(transazioneApplicativoServer!=null) {
							dumpApplicativo.setTransazioneApplicativoServer(transazioneApplicativoServer, idPA, dataConsegna);
						}*/
						InfoConnettoreUscita infoConnettoreUscita = outRequestContext.getConnettore();
						if(infoConnettoreUscita!=null){
							infoConnettoreUscita.setLocation(location); // aggiorno location ottenuta dal connettore utilizzato
						}
						dumpApplicativo.dumpRispostaIngresso(responseMessage, infoConnettoreUscita, connectorSender.getHeaderTrasporto());
						dumpRispostaEffettuato = true;
					}
					
					MessageType messageTypePrimaTrasformazione = responseMessage.getMessageType();
					
					responseMessage = gestoreTrasformazioni.trasformazioneRisposta(responseMessage, bustaRichiesta);
					
					MessageType messageTypeDopoTrasformazione = (responseMessage!=null)  ? responseMessage.getMessageType() : null;
					if(messageTypeDopoTrasformazione==null || messageTypePrimaTrasformazione.equals(messageTypeDopoTrasformazione)==false) {
						soapFault = null;
						restProblem = null;
						if(messageTypeDopoTrasformazione!=null) {
							if(responseMessage instanceof OpenSPCoop2SoapMessage){
								if(responseMessage.castAsSoap().hasSOAPFault()){
									SOAPBody body = responseMessage.castAsSoap().getSOAPBody();
									soapFault = body.getFault();
								}
							}
							else {
								if(responseMessage instanceof OpenSPCoop2RestJsonMessage ){
									OpenSPCoop2RestJsonMessage msg = responseMessage.castAsRestJson();
									if(msg.hasContent() && msg.isProblemDetailsForHttpApis_RFC7807()) {
										JsonDeserializer deserializer = new JsonDeserializer();
										restProblem = deserializer.fromString(msg.getContent(), false);
									}
								}
								else if(responseMessage instanceof OpenSPCoop2RestXmlMessage ){
									OpenSPCoop2RestXmlMessage msg = responseMessage.castAsRestXml();
									if(msg.hasContent() && msg.isProblemDetailsForHttpApis_RFC7807()) {
										XmlDeserializer deserializer = new XmlDeserializer();
										restProblem = deserializer.fromNode(msg.getContent(), false);
									}
								}
							}
						}
					}
				}
				catch(Throwable e) {
					
					pddContext.addObject(org.openspcoop2.core.constants.Costanti.ERRORE_TRASFORMAZIONE_RISPOSTA, "true");
					
					// prima emetto diagnostico di fine connettore
					emitDiagnostico(invokerNonSupportato, bustaRichiesta, msgDiag, errorConsegna);

					
					msgDiag.addKeywordErroreProcessamento(e);
					msgDiag.logPersonalizzato("trasformazione.processamentoRispostaInErrore");
					
					ErroreIntegrazione erroreIntegrazione = gestoreTrasformazioni.getErrore();
					if(erroreIntegrazione==null) {
						erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
								get5XX_ErroreProcessamento(e,CodiceErroreIntegrazione.CODICE_562_TRASFORMAZIONE);
					}
					
					String msgErrore = e.getMessage();					
					if(existsModuloInAttesaRispostaApplicativa) {
						
						IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.TRANSFORMATION_RULE_RESPONSE_FAILED;
						if(e instanceof GestoreTrasformazioniException && (((GestoreTrasformazioniException)e).getOp2IntegrationFunctionError()!=null)) {
							integrationFunctionError = ((GestoreTrasformazioniException)e).getOp2IntegrationFunctionError();
						}
						ejbUtils.setIntegrationFunctionErrorPortaApplicativa(integrationFunctionError);
						if(localForwardEngine!=null) {
							localForwardEngine.setIntegrationFunctionError(integrationFunctionError);
						}
						
						if(e instanceof GestoreTrasformazioniException && (((GestoreTrasformazioniException)e).getOpenSPCoop2ErrorMessage()!=null)) {
							this.sendErroreProcessamento(localForward, localForwardEngine, ejbUtils, 
									((GestoreTrasformazioniException)e).getOpenSPCoop2ErrorMessage(), msgErrore,
									idModuloInAttesa, bustaRichiesta, idCorrelazioneApplicativa, idCorrelazioneApplicativaRisposta, servizioApplicativoFruitore);
						}
						else {
							this.sendErroreProcessamento(localForward, localForwardEngine, ejbUtils, 
								erroreIntegrazione,
								idModuloInAttesa, bustaRichiesta, idCorrelazioneApplicativa, idCorrelazioneApplicativaRisposta, servizioApplicativoFruitore, e,
								(connectorSender!=null && connectorSender.getResponse()!=null ? connectorSender.getResponse().getParseException() : null),
								pddContext);
						}
						
						esito.setEsitoInvocazione(true); 
						esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO, msgErrore);
					}else{
						ejbUtils.rollbackMessage(msgErrore,servizioApplicativo, esito);
						esito.setEsitoInvocazione(false); 
						esito.setStatoInvocazioneErroreNonGestito(e);
					}
					openspcoopstate.releaseResource();
					return esito;
				}		
			}
			
			
			
			
			
			
			
			
	

			
			/* -------- OpenSPCoop2Message Update ------------- */
			try {
				if(transazioneApplicativoServer==null) { // non ha senso per le notifiche asincrone
					msgDiag.mediumDebug("Aggiornamento del messaggio");
					// NOTA la versione SOAP capirla da consegnaMessage, la risposta puo' essere null
					NotifierInputStreamParams nParams = null;
					if(invokerNonSupportato==false){
						nParams = connectorSender.getNotifierInputStreamParamsResponse();
					}
					responseMessage = protocolFactory.createProtocolManager().updateOpenSPCoop2MessageResponse(responseMessage, 
							bustaRichiesta, nParams,
							consegnaMessagePrimaTrasformazione.getTransportRequestContext(),transportResponseContext,
							protocolFactory.getCachedRegistryReader(openspcoopstate.getStatoRichiesta()),
							true);
				}
			} catch (Exception e) {
				msgDiag.addKeywordErroreProcessamento(e, "Aggiornamento messaggio fallito");
				msgDiag.logErroreGenerico(e,"ProtocolManager.updateOpenSPCoop2Message");
				String msgErrore = "ProtocolManager.updateOpenSPCoop2Message error: "+e.getMessage();
				this.log.error(msgErrore,e);
				if(existsModuloInAttesaRispostaApplicativa) {
					
					this.sendErroreProcessamento(localForward, localForwardEngine, ejbUtils, 
							ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_525_GESTIONE_FUNZIONALITA_PROTOCOLLO),
							idModuloInAttesa, bustaRichiesta, idCorrelazioneApplicativa, idCorrelazioneApplicativaRisposta, servizioApplicativoFruitore, e,
							(responseMessage!=null ? responseMessage.getParseException() : null),
							pddContext);
					
					esito.setEsitoInvocazione(true); 
					esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO, msgErrore);
				}else{
					ejbUtils.rollbackMessage(msgErrore,servizioApplicativo, esito);
					esito.setEsitoInvocazione(false); 
					esito.setStatoInvocazioneErroreNonGestito(e);
				}
				openspcoopstate.releaseResource();
				return esito;
			}
			
			
			
			
			
			
			
			
			
			
			
			/* ------------------- InResponseHandler -----------------------*/
			InResponseContext inResponseContext = null;
			if(invokerNonSupportato==false){
				try{
					inResponseContext = new InResponseContext(this.log,protocolFactory,openspcoopstate.getStatoRisposta());
					
					// Informazioni sul messaggio di riposta
					if(responseMessage!=null){
						inResponseContext.setMessaggio(responseMessage);				
					}
					
					// TransazioneApplicativoServer
					inResponseContext.setTransazioneApplicativoServer(transazioneApplicativoServer);
					
					// Informazioni sulla consegna
					inResponseContext.setErroreConsegna(motivoErroreConsegna);
					inResponseContext.setResponseHeaders(connectorSender.getHeaderTrasporto());
					inResponseContext.setReturnCode(codiceRitornato);
					
					// Altre informazioni
					if(outRequestContext.getConnettore()!=null){
						outRequestContext.getConnettore().setLocation(location); // aggiorno location ottenuta dal connettore utilizzato
					}
					inResponseContext.setConnettore(outRequestContext.getConnettore());
					inResponseContext.setDataPrimaInvocazioneConnettore(dataPrimaInvocazioneConnettore);
					inResponseContext.setDataTerminataInvocazioneConnettore(dataTerminataInvocazioneConnettore);
					inResponseContext.setDataAccettazioneRisposta(connectorSender.getDataAccettazioneRisposta());
					inResponseContext.setDataElaborazioneMessaggio(ejbUtils.getRicezioneMsgRisposta());
					inResponseContext.setProtocollo(outRequestContext.getProtocollo());
					inResponseContext.setPddContext(pddContext);
					inResponseContext.setIntegrazione(outRequestContext.getIntegrazione());
					inResponseContext.setTipoPorta(outRequestContext.getTipoPorta());
					
					// Invocazione handler
					GestoreHandlers.inResponse(inResponseContext, msgDiag, this.log);
					
				}
				catch(Exception e){
					String msgErrore = null;
					if(e!=null && e.getMessage()!=null){
						if(e.getMessage().length()>150){
							msgErrore = e.getMessage().substring(0, 150);
						}else{
							msgErrore = e.getMessage();
						}
					}
					ErroreIntegrazione erroreIntegrazione = null;
					IntegrationFunctionError integrationFunctionError = null;
					if(e instanceof HandlerException){
						HandlerException he = (HandlerException) e;
						if(he.isEmettiDiagnostico()){
							msgDiag.logErroreGenerico(e, ((HandlerException)e).getIdentitaHandler());
						}
						msgErrore = ((HandlerException)e).getIdentitaHandler()+" error: "+msgErrore;
						if(existsModuloInAttesaRispostaApplicativa) {
							erroreIntegrazione = he.convertToErroreIntegrazione();
							integrationFunctionError = he.getIntegrationFunctionError();
						}
					}else{
						msgDiag.logErroreGenerico(e, "InResponseHandler");
						msgErrore = "InResponseHandler error: "+msgErrore;
					}
					if(existsModuloInAttesaRispostaApplicativa) {
						
						if(erroreIntegrazione==null){
							erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
									get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_544_HANDLER_IN_RESPONSE);
						}
						
						if(integrationFunctionError!=null) {
							ejbUtils.setIntegrationFunctionErrorPortaApplicativa(integrationFunctionError);
							if(localForwardEngine!=null) {
								localForwardEngine.setIntegrationFunctionError(integrationFunctionError);
							}
						}
						this.sendErroreProcessamento(localForward, localForwardEngine, ejbUtils, 
								erroreIntegrazione,
								idModuloInAttesa, bustaRichiesta, idCorrelazioneApplicativa, idCorrelazioneApplicativaRisposta, servizioApplicativoFruitore, e,
								(responseMessage!=null ? responseMessage.getParseException() : null),
								pddContext);
						
						esito.setEsitoInvocazione(true); 
						esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO, msgErrore);
					}else{
						ejbUtils.rollbackMessage(msgErrore,servizioApplicativo, esito);
						esito.setEsitoInvocazione(false); 
						esito.setStatoInvocazioneErroreNonGestito(e);
					}
					openspcoopstate.releaseResource();
					return esito;
				}
			}
			
			
			
			
			
			
			
			
			
			
			
			
			/* ------------ Controllo che il messaggio non contenga una busta */
			msgDiag.mediumDebug("Controllo non esistenza di una busta ...");
			ValidazioneSintattica validatoreSintattico = new ValidazioneSintattica(pddContext, openspcoopstate.getStatoRichiesta(),responseMessage, protocolFactory);
			String msgErrore = null;
			try{
				if(validatoreSintattico.verifyProtocolPresence(tipoPdD,profiloCollaborazione,RuoloMessaggio.RISPOSTA)){
					throw new Exception("Rilevato ProtocolHeader nella risposta");
				}
			} catch (Exception e){
				msgDiag.logPersonalizzato("rispostaContenenteBusta");
				this.log.error("CheckProtocolPresence",e);
				if(existsModuloInAttesaRispostaApplicativa) {
					
					ejbUtils.setIntegrationFunctionErrorPortaApplicativa(IntegrationFunctionError.INTEROPERABILITY_PROFILE_RESPONSE_ALREADY_EXISTS);
					if(localForwardEngine!=null) {
						localForwardEngine.setIntegrationFunctionError(IntegrationFunctionError.INTEROPERABILITY_PROFILE_RESPONSE_ALREADY_EXISTS);
					}
					this.sendErroreProcessamento(localForward, localForwardEngine, ejbUtils, 
							ErroriIntegrazione.ERRORE_454_BUSTA_PRESENTE_RISPOSTA_APPLICATIVA.getErroreIntegrazione(),
							idModuloInAttesa, bustaRichiesta, idCorrelazioneApplicativa, idCorrelazioneApplicativaRisposta, servizioApplicativoFruitore, e,
							(responseMessage!=null ? responseMessage.getParseException() : null),
							pddContext);
					
					esito.setEsitoInvocazione(true); 
					esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
							msgDiag.getMessaggio_replaceKeywords("rispostaContenenteBusta"));
				}else{
					ejbUtils.rollbackMessage(msgErrore,servizioApplicativo, esito);
					esito.setEsitoInvocazione(false); 
					esito.setStatoInvocazioneErroreNonGestito(e);
				}
				openspcoopstate.releaseResource();
				return esito;
			}finally{
				// *** GB ***
				if(validatoreSintattico!=null){
					validatoreSintattico.setHeaderSOAP(null);
				}
				validatoreSintattico = null;
				// *** GB ***
			}

			
			
			
			
			
			
			
			
			
			
			
			
			// --------------------- Messaggio di Risposta + Dump --------------------------
			if(invokerNonSupportato==false){
				
				// Leggo informazioni di trasporto
				codiceRitornato = inResponseContext.getReturnCode();
				motivoErroreConsegna = inResponseContext.getErroreConsegna();
				responseMessage = inResponseContext.getMessaggio();
				
				String idMessaggioDump = idMessaggioConsegna;
				if(idMessaggioPreBehaviour!=null){
					idMessaggioDump = idMessaggioPreBehaviour;
				}
				
				//	dump applicativo
				if(!dumpRispostaEffettuato && responseMessage!=null ){
					Dump dumpApplicativo = new Dump(identitaPdD,ConsegnaContenutiApplicativi.ID_MODULO,idMessaggioDump,
							soggettoFruitore,idServizio,TipoPdD.APPLICATIVA,msgDiag.getPorta(),pddContext,
							openspcoopstate.getStatoRichiesta(),openspcoopstate.getStatoRisposta(),
							dumpConfig);
					if(transazioneApplicativoServer!=null) {
						dumpApplicativo.setTransazioneApplicativoServer(transazioneApplicativoServer, idPA, dataConsegna);
					}
					dumpApplicativo.dumpRispostaIngresso(responseMessage, inResponseContext.getConnettore(), inResponseContext.getResponseHeaders());
				}
				
			}
			
			
			
			
			





			/* ------------------- MsgDiagnostico -----------------------*/
			
			emitDiagnostico(invokerNonSupportato, bustaRichiesta, msgDiag, errorConsegna);










			/* ------------------------- Gestione Errori Consegna ---------------------------- */
			msgDiag.mediumDebug("Gestione errore consegna della risposta...");
			// Invoker Non Supportato
			if( invokerNonSupportato == true  ){
				String msgErroreConnettoreNonSupportato = "Connettore non supportato [tipo:"+tipoConnector+" class:"+connectorClass+"]";
				if(existsModuloInAttesaRispostaApplicativa) {
					
					this.sendErroreProcessamento(localForward, localForwardEngine, ejbUtils, 
							ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
								get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_515_CONNETTORE_NON_REGISTRATO),
							idModuloInAttesa, bustaRichiesta, idCorrelazioneApplicativa, idCorrelazioneApplicativaRisposta, servizioApplicativoFruitore, eInvokerNonSupportato,
							(responseMessage!=null ? responseMessage.getParseException() : null),
							pddContext);
					
					esito.setEsitoInvocazione(true); 
					esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO, msgErroreConnettoreNonSupportato);
				}else{
					ejbUtils.rollbackMessage(msgErroreConnettoreNonSupportato,servizioApplicativo, esito);
					esito.setEsitoInvocazione(false); 
					esito.setStatoInvocazione(EsitoLib.ERRORE_NON_GESTITO, msgErroreConnettoreNonSupportato);
				}
				openspcoopstate.releaseResource();
				return esito;

			}
			//	Errori avvenuti durante la consegna
			else if(errorConsegna){
						
				// Effettuo log dell'eventuale fault
				if(soapFault!=null && 
						( 
						  (motivoErroreConsegna==null) ||
						  (!motivoErroreConsegna.toLowerCase().contains("faultCode") && !motivoErroreConsegna.toLowerCase().contains("faultActor") && !motivoErroreConsegna.toLowerCase().contains("faultString"))
					    ) 
					){
					// Se non l'ho gia indicato nel motivo di errore, registro il fault
					msgDiag.addKeyword(CostantiPdD.KEY_SOAP_FAULT, SoapUtils.safe_toString(faultMessageFactory, soapFault, this.log));
					msgDiag.logPersonalizzato("ricezioneSoapFault");
				}
				else if(restProblem!=null && 
						( 
						  (motivoErroreConsegna==null) ||
						  (!motivoErroreConsegna.toLowerCase().contains(ProblemConstants.CLAIM_TYPE))
					    ) 
					){
					// Se non l'ho gia indicato nel motivo di errore, registro il fault
					msgDiag.addKeyword(CostantiPdD.KEY_REST_PROBLEM, restProblem.getRaw());
					msgDiag.logPersonalizzato("ricezioneRestProblem");
				}
				else{
					
					// Controllo Situazione Anomala ISSUE OP-7
					if(responseMessage!=null && ServiceBinding.SOAP.equals(responseMessage.getServiceBinding())){
						OpenSPCoop2SoapMessage soapMessageResponse = responseMessage.castAsSoap();
						if(soapMessageResponse.getSOAPPart()!=null && 
								soapMessageResponse.getSOAPPart().getEnvelope()!=null &&
								(soapMessageResponse.isSOAPBodyEmpty() || (!soapMessageResponse.hasSOAPFault()) )
								) {
							msgDiag.logPersonalizzato("comportamentoAnomalo.erroreConsegna.ricezioneMessaggioDiversoFault");
							if(isBlockedTransaction_responseMessageWithTransportCodeError){
								String msgErroreSituazioneAnomale = msgDiag.getMessaggio_replaceKeywords("comportamentoAnomalo.erroreConsegna.ricezioneMessaggioDiversoFault");
								if(existsModuloInAttesaRispostaApplicativa) {
									
									ejbUtils.setIntegrationFunctionErrorPortaApplicativa(IntegrationFunctionError.BAD_RESPONSE);
									if(localForwardEngine!=null) {
										localForwardEngine.setIntegrationFunctionError(IntegrationFunctionError.BAD_RESPONSE);
									}
									this.sendErroreProcessamento(localForward, localForwardEngine, ejbUtils, 
											ErroriIntegrazione.ERRORE_559_RICEVUTA_RISPOSTA_CON_ERRORE_TRASPORTO.
												get559_RicevutaRispostaConErroreTrasporto(msgErroreSituazioneAnomale),
											idModuloInAttesa, bustaRichiesta, idCorrelazioneApplicativa, idCorrelazioneApplicativaRisposta, servizioApplicativoFruitore, eInvokerNonSupportato,
											(responseMessage!=null ? responseMessage.getParseException() : null),
											pddContext);
									
									esito.setEsitoInvocazione(true); 
									esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO, msgErroreSituazioneAnomale);
								}else{
									ejbUtils.rollbackMessage(msgErroreSituazioneAnomale,servizioApplicativo, esito);
									esito.setEsitoInvocazione(false); 
									esito.setStatoInvocazione(EsitoLib.ERRORE_NON_GESTITO, msgErroreSituazioneAnomale);
								}
								openspcoopstate.releaseResource();
								return esito;
							}
						}
					}
				}
				
				String messaggioErroreConsegnaConnettore = "Consegna ["+tipoConnector+"] con errore: "+motivoErroreConsegna;
				if(existsModuloInAttesaRispostaApplicativa) {
					OpenSPCoop2Message connettoreMsgRequest = connettoreMsg.getRequestMessage(requestInfo, pddContext);
					String requestReadTimeout = null;
					String responseReadTimeout = null;
					if(pddContext!=null && pddContext.containsKey(TimeoutInputStream.ERROR_MSG_KEY)) {
						String timeoutMessage = PdDContext.getValue(TimeoutInputStream.ERROR_MSG_KEY, pddContext);
						if(timeoutMessage!=null && timeoutMessage.startsWith(CostantiPdD.PREFIX_TIMEOUT_REQUEST)) {
							requestReadTimeout = timeoutMessage;
						}
						else if(timeoutMessage!=null && timeoutMessage.startsWith(CostantiPdD.PREFIX_TIMEOUT_RESPONSE)) {
							responseReadTimeout = timeoutMessage;
						}
					}
					String requestLimitExceeded = null;
					String responseLimitExceeded = null;
					if(pddContext!=null && pddContext.containsKey(LimitedInputStream.ERROR_MSG_KEY)) {
						String limitedExceededMessage = PdDContext.getValue(LimitedInputStream.ERROR_MSG_KEY, pddContext);
						if(limitedExceededMessage!=null && limitedExceededMessage.startsWith(CostantiPdD.PREFIX_LIMITED_REQUEST)) {
							requestLimitExceeded = limitedExceededMessage;
						}
						else if(limitedExceededMessage!=null && limitedExceededMessage.startsWith(CostantiPdD.PREFIX_LIMITED_RESPONSE)) {
							responseLimitExceeded = limitedExceededMessage;
						}
					}
					if(connettoreMsgRequest.getParseException() != null || 
							requestReadTimeout!=null || 
							requestLimitExceeded!=null){
						
						ParseException parseException = null;
						Throwable tParsing = null;
						String errorMsg = null;
						if(requestReadTimeout != null) {
							tParsing = (TimeoutIOException) pddContext.getObject(TimeoutInputStream.EXCEPTION_KEY);
							errorMsg = tParsing.getMessage();
						}
						else if(requestLimitExceeded != null) {
							tParsing = (LimitExceededIOException) pddContext.getObject(LimitedInputStream.EXCEPTION_KEY);
							errorMsg = tParsing.getMessage();
						}
						else {
							parseException = connettoreMsgRequest.getParseException();
							tParsing = parseException.getParseException();
							errorMsg = tParsing.getMessage();
						}
						
						pddContext.addObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RICHIESTA_NON_RICONOSCIUTO, true);
						IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.UNPROCESSABLE_REQUEST_CONTENT;
						if(requestReadTimeout!=null) {
							integrationFunctionError = IntegrationFunctionError.REQUEST_TIMED_OUT;
						}
						else if(requestLimitExceeded!=null) {
							integrationFunctionError = IntegrationFunctionError.REQUEST_SIZE_EXCEEDED;
						}
						else if(connettoreMsgRequest.getParseException().getSourceException()!=null &&
								TimeoutIOException.isTimeoutIOException(connettoreMsgRequest.getParseException().getSourceException())) {
							integrationFunctionError = IntegrationFunctionError.REQUEST_TIMED_OUT;
						}
						else if(connettoreMsgRequest.getParseException().getSourceException()!=null &&
								LimitExceededIOException.isLimitExceededIOException(connettoreMsgRequest.getParseException().getSourceException())) {
							integrationFunctionError = IntegrationFunctionError.REQUEST_SIZE_EXCEEDED;
						}
						ejbUtils.setIntegrationFunctionErrorPortaApplicativa(integrationFunctionError);
						if(localForwardEngine!=null) {
							localForwardEngine.setIntegrationFunctionError(integrationFunctionError);
						}
						this.sendErroreProcessamento(localForward, localForwardEngine, ejbUtils, 
								ErroriIntegrazione.ERRORE_432_PARSING_EXCEPTION_RICHIESTA.getErrore432_MessaggioRichiestaMalformato(tParsing),
								idModuloInAttesa, bustaRichiesta, idCorrelazioneApplicativa, idCorrelazioneApplicativaRisposta, servizioApplicativoFruitore, 
								tParsing,parseException,
								pddContext);
						
						openspcoopstate.releaseResource();
						esito.setEsitoInvocazione(true); 
						esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,errorMsg);
						return esito;
					} else if(responseMessage==null && 
							!pddContext.containsKey(org.openspcoop2.core.constants.Costanti.CONTENUTO_RISPOSTA_NON_RICONOSCIUTO_PARSE_EXCEPTION)){
						// Genero una risposta di errore
						IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.SERVICE_UNAVAILABLE;
						if(eccezioneProcessamentoConnettore!=null && motivoErroreConsegna!=null) {
							if(responseReadTimeout!=null) {
								integrationFunctionError = IntegrationFunctionError.ENDPOINT_REQUEST_TIMED_OUT;
								pddContext.removeObject(TimeoutInputStream.ERROR_MSG_KEY);
								pddContext.removeObject(TimeoutInputStream.EXCEPTION_KEY);
							}
							else if(responseLimitExceeded!=null) {
								integrationFunctionError = IntegrationFunctionError.RESPONSE_SIZE_EXCEEDED;
								pddContext.removeObject(LimitedInputStream.ERROR_MSG_KEY);
								pddContext.removeObject(LimitedInputStream.EXCEPTION_KEY);
							}
							else if(this.propertiesReader.isServiceUnavailable_ReadTimedOut(motivoErroreConsegna)){
								integrationFunctionError = IntegrationFunctionError.ENDPOINT_REQUEST_TIMED_OUT;
							}
						}
						ejbUtils.setIntegrationFunctionErrorPortaApplicativa(integrationFunctionError);
						if(localForwardEngine!=null) {
							localForwardEngine.setIntegrationFunctionError(integrationFunctionError);
						}
						this.sendErroreProcessamento(localForward, localForwardEngine, ejbUtils, 
								ErroriIntegrazione.ERRORE_516_CONNETTORE_UTILIZZO_CON_ERRORE.get516_ServizioApplicativoNonDisponibile(),
								idModuloInAttesa, bustaRichiesta, idCorrelazioneApplicativa, idCorrelazioneApplicativaRisposta, servizioApplicativoFruitore, eccezioneProcessamentoConnettore,
								(responseMessage!=null ? responseMessage.getParseException() : null),
								pddContext);
					
						openspcoopstate.releaseResource();
						esito.setEsitoInvocazione(true); 
						esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,messaggioErroreConsegnaConnettore);
						return esito;
					} else if(pddContext.containsKey(org.openspcoop2.core.constants.Costanti.CONTENUTO_RISPOSTA_NON_RICONOSCIUTO_PARSE_EXCEPTION) ||
							responseMessage.getParseException() != null ||
							responseReadTimeout!=null ||
							responseLimitExceeded!=null){
						
						ParseException parseException = null;
						Throwable tParsing = null;
						String errorMsg = null;
						if(responseReadTimeout != null) {
							tParsing = (TimeoutIOException) pddContext.getObject(TimeoutInputStream.EXCEPTION_KEY);
							errorMsg = tParsing.getMessage();
						}
						else if(responseLimitExceeded != null) {
							tParsing = (LimitExceededIOException) pddContext.getObject(LimitedInputStream.EXCEPTION_KEY);
							errorMsg = tParsing.getMessage();
						}
						else if(pddContext.containsKey(org.openspcoop2.core.constants.Costanti.CONTENUTO_RISPOSTA_NON_RICONOSCIUTO_PARSE_EXCEPTION)){
							parseException = (ParseException) pddContext.getObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RISPOSTA_NON_RICONOSCIUTO_PARSE_EXCEPTION);
							tParsing = parseException.getParseException();
							errorMsg = tParsing.getMessage();
						}
						else {
							parseException = responseMessage.getParseException();
							tParsing = parseException.getParseException();
							errorMsg = tParsing.getMessage();
						}
						
						pddContext.addObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RISPOSTA_NON_RICONOSCIUTO, true);
						IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.UNPROCESSABLE_RESPONSE_CONTENT;
						if(responseReadTimeout!=null) {
							integrationFunctionError = IntegrationFunctionError.ENDPOINT_REQUEST_TIMED_OUT;
						}
						else if(responseLimitExceeded!=null) {
							integrationFunctionError = IntegrationFunctionError.RESPONSE_SIZE_EXCEEDED;
						}
						else if(parseException.getSourceException()!=null &&
								TimeoutIOException.isTimeoutIOException(parseException.getSourceException())) {
							integrationFunctionError = IntegrationFunctionError.ENDPOINT_REQUEST_TIMED_OUT;
						}
						else if(parseException.getSourceException()!=null &&
								LimitExceededIOException.isLimitExceededIOException(parseException.getSourceException())) {
							integrationFunctionError = IntegrationFunctionError.RESPONSE_SIZE_EXCEEDED;
						}
						ejbUtils.setIntegrationFunctionErrorPortaApplicativa(integrationFunctionError);
						if(localForwardEngine!=null) {
							localForwardEngine.setIntegrationFunctionError(integrationFunctionError);
						}
						this.sendErroreProcessamento(localForward, localForwardEngine, ejbUtils, 
								ErroriIntegrazione.ERRORE_440_PARSING_EXCEPTION_RISPOSTA.getErrore440_MessaggioRispostaMalformato(tParsing),
								idModuloInAttesa, bustaRichiesta, idCorrelazioneApplicativa, idCorrelazioneApplicativaRisposta, servizioApplicativoFruitore, 
								tParsing,parseException,
								pddContext);
						
						openspcoopstate.releaseResource();
						esito.setEsitoInvocazione(true); 
						esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,errorMsg);
						return esito;
					}
					
				}else {
					if(riconsegna){
						ejbUtils.rollbackMessage(messaggioErroreConsegnaConnettore,dataRiconsegna,servizioApplicativo, esito);
						esito.setEsitoInvocazione(false); 
						esito.setStatoInvocazione(EsitoLib.ERRORE_NON_GESTITO,messaggioErroreConsegnaConnettore);
					}else{
						ejbUtils.releaseInboxMessage(true); 
						esito.setEsitoInvocazione(true); 
						esito.setStatoInvocazione(EsitoLib.ERRORE_NON_GESTITO,messaggioErroreConsegnaConnettore);
					}
					openspcoopstate.releaseResource();
					this.log.info(ConsegnaContenutiApplicativi.ID_MODULO + " errore avvenuto durante la consegna");

					return esito;
				}
			}





























			/* ----------- Gestione della risposta applicativa da eseguire nei seguenti casi:
               AsincronoSimmetrico (InvocazioneServizio e ConsegnaRisposta):
               	  se isRicevutaApplicativa
               	     - Bisogna aspettare una risposta applicativa 
                     - Bisogna quindi generare un busta per la ricevuta asincrona
                  altrimenti
                     - Non serve aspettare una risposta applicativa 
                     - Se si riceve un Fault, il fault viene loggato.
               AsincronoAsimmetrico(InvocazioneServizio): 
                  se isRicevutaApplicativa
               	     - Bisogna aspettare una risposta applicativa 
                     - Bisogna quindi generare un busta per la ricevuta asincrona
                  altrimenti
                     - Non serve aspettare una risposta applicativa 
                     - Se si riceve un Fault, il fault viene loggato.
               AsincronoAsimmetrico (Polling):
	              - Bisogna aspettare una risposta applicativa 'sincrona'
                  - Bisogna quindi generare un busta della risposta contenente il risultato del polling          
	           Sincrono:
                  - Bisogna aspettare una risposta applicativa sincrona
                  - Bisogna quindi generare un busta della risposta  sincrona
               Altro (OneWay):
                  - Non bisogna aspettare una risposta applicativa
                  - Se si riceve un Fault, il fault viene loggato.
	        ------------- */
			
			msgDiag.mediumDebug("Registrazione eventuale fault...");
			// Effettuo log del fault
			if(soapFault!=null){
				msgDiag.addKeyword(CostantiPdD.KEY_SOAP_FAULT, SoapUtils.safe_toString(faultMessageFactory, soapFault, this.log));
				msgDiag.logPersonalizzato("ricezioneSoapFault");
			}
			else if(restProblem!=null){
				// Se non l'ho gia indicato nel motivo di errore, registro il fault
				msgDiag.addKeyword(CostantiPdD.KEY_REST_PROBLEM, restProblem.getRaw());
				msgDiag.logPersonalizzato("ricezioneRestProblem");
			}
			
			if(existsModuloInAttesaRispostaApplicativa){

				if( !localForward && richiestaApplicativa!=null && Costanti.SCENARIO_ONEWAY_INVOCAZIONE_SERVIZIO.equals(richiestaApplicativa.getScenario())){

					// Oneway in modalita sincrona

					boolean returnProtocolReply = false;		
					Busta bustaHTTPReply = null;
					
					boolean consegnaAffidabile = false;
					switch (protocolManager.getConsegnaAffidabile(bustaIndicazioneConsegnaInOrdine)) {
					case ABILITATA:
						consegnaAffidabile = true;
						break;
					case DISABILITATA:
						consegnaAffidabile = false;
						break;
					default:
						consegnaAffidabile = bustaRichiesta.isConfermaRicezione() &&
							this.propertiesReader.isGestioneRiscontri(implementazionePdDMittente);
						break;
					}
					
					if(consegnaAffidabile){
						msgDiag.mediumDebug("Gestione eventuali riscontri da inviare...");
						if(ProfiloDiCollaborazione.ONEWAY.equals(bustaRichiesta.getProfiloDiCollaborazione()) ) {	
							TipoOraRegistrazione tipoOraRegistrazione = this.propertiesReader.getTipoTempoBusta(implementazionePdDMittente);
							bustaHTTPReply = bustaRichiesta.invertiBusta(tipoOraRegistrazione,
									protocolFactory.createTraduttore().toString(tipoOraRegistrazione));

							// Riscontro ad hoc
							Riscontro r = new Riscontro();
							r.setID(bustaRichiesta.getID());
							r.setOraRegistrazione(bustaHTTPReply.getOraRegistrazione());
							r.setTipoOraRegistrazione(this.propertiesReader.getTipoTempoBusta(implementazionePdDMittente));
							bustaHTTPReply.addRiscontro(r);
							bustaHTTPReply.setTipoServizioRichiedenteBustaDiServizio(bustaRichiesta.getTipoServizio());
							bustaHTTPReply.setServizioRichiedenteBustaDiServizio(bustaRichiesta.getServizio());
							bustaHTTPReply.setVersioneServizioRichiedenteBustaDiServizio(bustaRichiesta.getVersioneServizio());
							bustaHTTPReply.setAzioneRichiedenteBustaDiServizio(bustaRichiesta.getAzione());

							returnProtocolReply = true;
						}
					}

					if( returnProtocolReply == false){

						if(soapFault!=null || restProblem!=null){
							// Devo ritornare il SoapFault
							msgDiag.mediumDebug("Invio messaggio di fault a Ricezione/Consegna ContenutiApplicativi...");
							msgResponse = ejbUtils.sendSOAPFault(richiestaApplicativa.getIdModuloInAttesa(),responseMessage);
						}
						else{
							// Invio sblocco se e' attesa una risposta dal modulo
							// Se non e' abilitato la risposta su di una nuova connessione, e l'indirizzo telematico 
							// non e' abilitato o cmq non presente, allora devo inviare lo sblocco
							msgDiag.mediumDebug("Invio messaggio di sblocco a RicezioneBuste...");
							msgResponse = ejbUtils.sendSbloccoRicezioneBuste(richiestaApplicativa.getIdModuloInAttesa());
						}
					}
					else{

						// Invio risposta immediata in seguito alla richiesta ricevuta
						if(soapFault!=null || restProblem!=null){
							// Devo ritornare il SoapFault
							msgDiag.mediumDebug("Invio messaggio di fault a Ricezione/Consegna ContenutiApplicativi...");
							msgResponse = ejbUtils.sendSOAPFault(richiestaApplicativa.getIdModuloInAttesa(),responseMessage);
						}
						else{
							msgDiag.mediumDebug("Invio messaggio a Ricezione/Consegna ContenutiApplicativi...");
							msgResponse = ejbUtils.buildAndSendBustaRisposta(richiestaApplicativa.getIdModuloInAttesa(),bustaHTTPReply,
									MessageUtilities.buildEmptyMessage(OpenSPCoop2MessageFactory.getDefaultMessageFactory(),
											requestInfo.getProtocolRequestMessageType(), MessageRole.RESPONSE),profiloGestione,
									idCorrelazioneApplicativa,idCorrelazioneApplicativaRisposta,servizioApplicativoFruitore);
						}
					}
				}
				else{
					/*-- analisi risposta per riferimento -- */
					if(rispostaPerRiferimento && !errorConsegna){
						msgDiag.mediumDebug("Gestione risposta per riferimento...");
						boolean rilasciaRisorsa = false;
						if(openspcoopstate instanceof OpenSPCoopStateless){
							OpenSPCoopStateless state = (OpenSPCoopStateless) openspcoopstate;
							// La risposta per riferimento ha bisogno della connessione.
							if (state.resourceReleased()){
								// Risorsa e' rilasciata se siamo in RinegoziamentoConnessione
								state.setUseConnection(true);
								state.updateResource(idTransazione);
								rilasciaRisorsa = true;
							}else{
								// Non siamo in RinegoziamentoConnessione, basta dire di utilizzare la connessione
								state.setUseConnection(true);
							}
						}
						try{
							if(MessageType.SOAP_11.equals(responseMessage.getMessageType())==false){
								throw new Exception("Tipo di messaggio ["+responseMessage.getMessageType()+"] non supportato");
							}
							
							// 1. Read IDMessaggio
							// L'id del messaggio deve essere prelevato dal messaggio di risposta ritornato dal gestore eventi.
							Node prelevaMessaggioResponse = responseMessage.castAsSoap().getSOAPBody().getFirstChild();
							if(prelevaMessaggioResponse==null)
								throw new Exception("Identificativo non presente [prelevaMessaggioResponse]");
							Node prelevaMessaggioReturn = prelevaMessaggioResponse.getFirstChild();
							if(prelevaMessaggioReturn==null)
								throw new Exception("Identificativo non presente [prelevaMessaggioReturn]");
							Node idMessaggioPresenteNelRepositoryNode = prelevaMessaggioReturn.getFirstChild();
							if(idMessaggioPresenteNelRepositoryNode==null)
								throw new Exception("Identificativo non presente [idMessaggioPresenteNelRepositoryNode]");
							byte[] idMessaggioPresenteNelRepositoryByte = Base64Utilities.decode(idMessaggioPresenteNelRepositoryNode.getNodeValue());
							String idMessaggioPresenteNelRepository = new String(idMessaggioPresenteNelRepositoryByte);
							//if(idMessaggioPresenteNelRepository==null)
							//	throw new Exception("Identificativo non presente");


							// 2. get Messaggio dal Repository
							GestoreMessaggi gestoreMsgFromRepository = new GestoreMessaggi(openspcoopstate, false, idMessaggioPresenteNelRepository,Costanti.INBOX,msgDiag,pddContext);
							OpenSPCoop2Message msgFromRepository = gestoreMsgFromRepository.getMessage();
							//if(idMessaggioPresenteNelRepository==null)
							//	throw new Exception("Messaggio non presente nel repository");

							// 3. prendo body applicativo
							byte[] bodyApplicativoPrecedentementePubblicato = TunnelSoapUtils.sbustamentoMessaggio(msgFromRepository);

							// 4. Inserimento dei byte del body applicativo al posto dell'ID,
							//    nel msg ritornato dal GestoreEventi.
							//    La variabile responseMessage deve contenere il messaggio Soap che sara' ritornato a chi ha richiesto un messaggio
							responseMessage = gestoreMsgFromRepository.buildRispostaPrelevamentoMessaggio_RepositoryMessaggi(bodyApplicativoPrecedentementePubblicato,msgFromRepository.getMessageType());
							
						}catch(Exception e){
							msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_CONSEGNA, ("risposta per riferimento non costruita, "+e.getMessage()));
							if(bustaRichiesta.getMittente()!=null && bustaRichiesta.getTipoMittente()!=null){
								msgDiag.logPersonalizzato("consegnaConErrore");
							}
							else{
								msgDiag.logPersonalizzato("consegnaConErrore.mittenteAnonimo");
							}
							
							this.sendErroreProcessamento(localForward, localForwardEngine, ejbUtils, 
									ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
										get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_511_READ_RESPONSE_MSG),
									idModuloInAttesa, bustaRichiesta, idCorrelazioneApplicativa, idCorrelazioneApplicativaRisposta, servizioApplicativoFruitore, e,
									(responseMessage!=null ? responseMessage.getParseException() : null),
									pddContext);
						
							esito.setEsitoInvocazione(true); 
							esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO, "risposta per riferimento non costruita, "+e.getMessage());
							openspcoopstate.releaseResource();
							return esito;
						}finally{
							if(openspcoopstate instanceof OpenSPCoopStateless){
								OpenSPCoopStateless state = (OpenSPCoopStateless) openspcoopstate;
								if(rilasciaRisorsa){
									state.releaseResource();
								}
								state.setUseConnection(false);
							}
						}
					}

					msgDiag.mediumDebug("Gestione risposta...");
					boolean rispostaVuotaValidaPerAsincroniStateless_modalitaAsincrona = false;
					if(responseMessage == null && 
							!(localForward && richiestaApplicativa!=null && Costanti.SCENARIO_ONEWAY_INVOCAZIONE_SERVIZIO.equals(richiestaApplicativa.getScenario())) ){

						if( (Costanti.SCENARIO_ASINCRONO_ASIMMETRICO_INVOCAZIONE_SERVIZIO.equals(scenarioCooperazione) ||
								Costanti.SCENARIO_ASINCRONO_SIMMETRICO_INVOCAZIONE_SERVIZIO.equals(scenarioCooperazione) ||
								Costanti.SCENARIO_ASINCRONO_SIMMETRICO_CONSEGNA_RISPOSTA.equals(scenarioCooperazione) ) 
								&& portaDiTipoStateless
								&& (!isRicevutaAsincrona) ){
							rispostaVuotaValidaPerAsincroniStateless_modalitaAsincrona = true;
							responseMessage = MessageUtilities.buildEmptyMessage(OpenSPCoop2MessageFactory.getDefaultMessageFactory(),
									requestInfo.getProtocolRequestMessageType(), MessageRole.RESPONSE); // Costruisce messaggio vuoto per inserire busta (ricevuta asincrona)
						}
						else{
							msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_CONSEGNA, ("risposta applicativa attesa ma non ricevuta"));
							if(bustaRichiesta.getMittente()!=null && bustaRichiesta.getTipoMittente()!=null){
								msgDiag.logPersonalizzato("consegnaConErrore");
							}
							else{
								msgDiag.logPersonalizzato("consegnaConErrore.mittenteAnonimo");
							}
							
							ejbUtils.setIntegrationFunctionErrorPortaApplicativa(IntegrationFunctionError.EXPECTED_RESPONSE_NOT_FOUND);
							if(localForwardEngine!=null) {
								localForwardEngine.setIntegrationFunctionError(IntegrationFunctionError.EXPECTED_RESPONSE_NOT_FOUND);
							}
							this.sendErroreProcessamento(localForward, localForwardEngine, ejbUtils, 
									ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
										get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_517_RISPOSTA_RICHIESTA_NON_RITORNATA),
									idModuloInAttesa, bustaRichiesta, idCorrelazioneApplicativa, idCorrelazioneApplicativaRisposta, servizioApplicativoFruitore, null,
									(responseMessage!=null ? responseMessage.getParseException() : null),
									pddContext);
							
							openspcoopstate.releaseResource();
							esito.setEsitoInvocazione(true); 
							esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,"risposta applicativa attesa ma non ricevuta");

							return esito;
						}

					}
					
					if(rispostaVuotaValidaPerAsincroniStateless_modalitaAsincrona==false
							&& transazioneApplicativoServer==null // non ha senso per le notifiche asincrone tutte le operazioni presenti in questo corpo (validazione, correlazione, header di integrazione)
							){
						
						if(validazioneContenutoApplicativoApplicativo!=null && validazioneContenutoApplicativoApplicativo.getTipo()!=null){
							String tipo = ValidatoreMessaggiApplicativi.getTipo(validazioneContenutoApplicativoApplicativo);
							//this.msgContext.getIntegrazione().setTipoValidazioneContenuti(tipo);
							msgDiag.addKeyword(CostantiPdD.KEY_TIPO_VALIDAZIONE_CONTENUTI, tipo);
							msgDiag.addKeyword(CostantiPdD.KEY_DETAILS_VALIDAZIONE_CONTENUTI,"");
						}
						
						// Verifica xsd  (Se siamo in un caso di risposta applicativa presente)
						if(CostantiConfigurazione.STATO_CON_WARNING_ABILITATO.equals(validazioneContenutoApplicativoApplicativo.getStato())||
								CostantiConfigurazione.STATO_CON_WARNING_WARNING_ONLY.equals(validazioneContenutoApplicativoApplicativo.getStato())){

							if(transactionNullable!=null) {
								transactionNullable.getTempiElaborazione().startValidazioneRisposta();
							}
							ByteArrayInputStream binXSD = null;
							try{
								boolean hasContent = false;
								boolean isFault = false;
								if(ServiceBinding.SOAP.equals(responseMessage.getServiceBinding())){
									OpenSPCoop2SoapMessage soapMsg = responseMessage.castAsSoap();
									hasContent = !soapMsg.isSOAPBodyEmpty();
									isFault = soapMsg.isFault();
								}
								else{
									//org.openspcoop2.message.OpenSPCoop2RestMessage<?> restMsg = responseMessage.castAsRest();
									//hasContent = restMsg.hasContent();
									hasContent = true; // devo controllare gli header etc...
									// fix: i problem detail devono far parte dell'interfaccia openapi
									//isFault = restMsg.isProblemDetailsForHttpApis_RFC7807() || MessageRole.FAULT.equals(responseMessage.getMessageRole());
									isFault = MessageRole.FAULT.equals(responseMessage.getMessageRole());
								}
								
								boolean validazioneAbilitata = true;
								if(ServiceBinding.SOAP.equals(responseMessage.getServiceBinding())){
									validazioneAbilitata = hasContent && (isFault==false);
								}
								else {
									validazioneAbilitata = ValidatoreMessaggiApplicativiRest.isValidazioneAbilitata(proprietaValidazioneContenutoApplicativoApplicativo, responseMessage, codiceRitornato);
								}
								if( validazioneAbilitata ){
									
									msgDiag.logPersonalizzato("validazioneContenutiApplicativiRispostaInCorso");
									
									boolean readInterface = CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_INTERFACE.equals(validazioneContenutoApplicativoApplicativo.getTipo());
									IDServizio idSValidazioneXSD = idServizio;
									if(servizioHeaderIntegrazione!=null){
										idSValidazioneXSD = servizioHeaderIntegrazione;
									}
									
									if(ServiceBinding.SOAP.equals(responseMessage.getServiceBinding())){
									
										// Accept mtom message
										List<MtomXomReference> xomReferences = null;
										if(StatoFunzionalita.ABILITATO.equals(validazioneContenutoApplicativoApplicativo.getAcceptMtomMessage())){
											msgDiag.mediumDebug("Validazione xsd della risposta (mtomFastUnpackagingForXSDConformance)...");
											xomReferences = responseMessage.castAsSoap().mtomFastUnpackagingForXSDConformance();
										}
										
										// Init Validatore
										msgDiag.mediumDebug("Validazione della risposta (initValidator)...");
										ValidatoreMessaggiApplicativi validatoreMessaggiApplicativi = 
											new ValidatoreMessaggiApplicativi(registroServiziManager,idSValidazioneXSD,
													responseMessage,readInterface,
													this.propertiesReader.isValidazioneContenutiApplicativi_rpcLiteral_xsiType_gestione(),
													proprietaValidazioneContenutoApplicativoApplicativo,
													pddContext);
	
										// Validazione WSDL 
										if( CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_INTERFACE.equals(validazioneContenutoApplicativoApplicativo.getTipo()) 
												||
												CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_OPENSPCOOP.equals(validazioneContenutoApplicativoApplicativo.getTipo())
										){
											msgDiag.mediumDebug("Validazione wsdl della risposta ...");
											validatoreMessaggiApplicativi.validateWithWsdlLogicoImplementativo(false);
										}
										
										// Validazione XSD
										msgDiag.mediumDebug("Validazione xsd della risposta ...");
										validatoreMessaggiApplicativi.validateWithWsdlDefinitorio(false);
										
										// Validazione WSDL (Restore Original Document)
										if (CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_INTERFACE.equals(validazioneContenutoApplicativoApplicativo.getTipo())
											|| CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_OPENSPCOOP.equals(validazioneContenutoApplicativoApplicativo.getTipo())) {
											if(this.propertiesReader.isValidazioneContenutiApplicativi_rpcLiteral_xsiType_gestione() &&
													this.propertiesReader.isValidazioneContenutiApplicativi_rpcLiteral_xsiType_ripulituraDopoValidazione()){
												msgDiag.mediumDebug("Ripristino elementi modificati per supportare validazione wsdl della risposta ...");
												validatoreMessaggiApplicativi.restoreOriginalDocument(false);
											}
										}
										
										// Ripristino struttura messaggio con xom
										if(xomReferences!=null && xomReferences.size()>0){
											msgDiag.mediumDebug("Validazione xsd della risposta (mtomRestoreAfterXSDConformance)...");
											responseMessage.castAsSoap().mtomRestoreAfterXSDConformance(xomReferences);
										}
										
									}
									else {
										
										// Init Validatore
										msgDiag.mediumDebug("Validazione della risposta (initValidator)...");
										ValidatoreMessaggiApplicativiRest validatoreMessaggiApplicativi = 
											new ValidatoreMessaggiApplicativiRest(registroServiziManager, idSValidazioneXSD, responseMessage, readInterface, proprietaValidazioneContenutoApplicativoApplicativo, 
													protocolFactory, pddContext);
										
										if(CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_XSD.equals(validazioneContenutoApplicativoApplicativo.getTipo()) &&
												responseMessage.castAsRest().hasContent()) {
											
											// Validazione XSD
											msgDiag.mediumDebug("Validazione xsd della risposta ...");
											validatoreMessaggiApplicativi.validateWithSchemiXSD(false);
											
										}
										else {
											
											// Validazione Interface
											validatoreMessaggiApplicativi.validateResponseWithInterface(consegnaMessagePrimaTrasformazione, true);
											
										}
										
									}

									msgDiag.logPersonalizzato("validazioneContenutiApplicativiRispostaEffettuata");
								}
								else{
									if(hasContent==false){
										msgDiag.addKeyword(CostantiPdD.KEY_DETAILS_VALIDAZIONE_CONTENUTI,CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_VALIDAZIONE_SOAP_BODY_NON_PRESENTE);
										msgDiag.logPersonalizzato("validazioneContenutiApplicativiRispostaDisabilitata");
									}
									else if (isFault ){
										msgDiag.addKeyword(CostantiPdD.KEY_DETAILS_VALIDAZIONE_CONTENUTI,CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_VALIDAZIONE_SOAP_FAULT_PRESENTE);
										msgDiag.logPersonalizzato("validazioneContenutiApplicativiRispostaDisabilitata");
									}
								}

							}catch(ValidatoreMessaggiApplicativiException ex){
								msgDiag.addKeywordErroreProcessamento(ex);
								this.log.error("[ValidazioneContenutiApplicativi Risposta] "+ex.getMessage(),ex);
								if (CostantiConfigurazione.STATO_CON_WARNING_WARNING_ONLY.equals(validazioneContenutoApplicativoApplicativo.getStato())) {
									msgDiag.logPersonalizzato("validazioneContenutiApplicativiRispostaNonRiuscita.warningOnly");
								}
								else {
									msgDiag.logPersonalizzato("validazioneContenutiApplicativiRispostaNonRiuscita");
								}
								if(CostantiConfigurazione.STATO_CON_WARNING_WARNING_ONLY.equals(validazioneContenutoApplicativoApplicativo.getStato()) == false){
									
									// validazione abilitata
									
									pddContext.addObject(org.openspcoop2.core.constants.Costanti.ERRORE_VALIDAZIONE_RISPOSTA, "true");
									
									IntegrationFunctionError integrationFunctionError = null;
									if(ex.getErrore()!=null &&
											(
													//CodiceErroreIntegrazione.CODICE_417_COSTRUZIONE_VALIDATORE_TRAMITE_INTERFACCIA_FALLITA.equals(ex.getErrore().getCodiceErrore()) ||
													CodiceErroreIntegrazione.CODICE_419_VALIDAZIONE_RISPOSTA_TRAMITE_INTERFACCIA_FALLITA.equals(ex.getErrore().getCodiceErrore()) 
											)
										){
										integrationFunctionError = IntegrationFunctionError.INVALID_RESPONSE_CONTENT;
									}
									else{
										integrationFunctionError = IntegrationFunctionError.INTERNAL_RESPONSE_ERROR;
									}
									
									ejbUtils.setIntegrationFunctionErrorPortaApplicativa(integrationFunctionError);
									if(localForwardEngine!=null) {
										localForwardEngine.setIntegrationFunctionError(integrationFunctionError);
									}
									
									this.sendErroreProcessamento(localForward, localForwardEngine, ejbUtils, 
											ex.getErrore(),
											idModuloInAttesa, bustaRichiesta, idCorrelazioneApplicativa, idCorrelazioneApplicativaRisposta, servizioApplicativoFruitore, ex,
											(responseMessage!=null ? responseMessage.getParseException() : null),
											pddContext);
									
									openspcoopstate.releaseResource();
									esito.setEsitoInvocazione(true); 
									esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
											msgDiag.getMessaggio_replaceKeywords("validazioneContenutiApplicativiNonRiuscita"));
									return esito;
								}
							}catch(Exception ex){
								msgDiag.addKeywordErroreProcessamento(ex);
								this.log.error("Riscontrato errore durante la validazione xsd della risposta applicativa",ex);
								if (CostantiConfigurazione.STATO_CON_WARNING_WARNING_ONLY.equals(validazioneContenutoApplicativoApplicativo.getStato())) {
									msgDiag.logPersonalizzato("validazioneContenutiApplicativiRispostaNonRiuscita.warningOnly");
								}
								else {
									msgDiag.logPersonalizzato("validazioneContenutiApplicativiRispostaNonRiuscita");
								}
								if(CostantiConfigurazione.STATO_CON_WARNING_WARNING_ONLY.equals(validazioneContenutoApplicativoApplicativo.getStato()) == false){
									
									pddContext.addObject(org.openspcoop2.core.constants.Costanti.ERRORE_VALIDAZIONE_RISPOSTA, "true");
									
									// validazione abilitata
									
									this.sendErroreProcessamento(localForward, localForwardEngine, ejbUtils, 
											ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
												get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_531_VALIDAZIONE_TRAMITE_INTERFACCIA_FALLITA),
											idModuloInAttesa, bustaRichiesta, idCorrelazioneApplicativa, idCorrelazioneApplicativaRisposta, servizioApplicativoFruitore, ex,
											(responseMessage!=null ? responseMessage.getParseException() : null),
											pddContext);
								
									openspcoopstate.releaseResource();
									esito.setEsitoInvocazione(true); 
									esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
											msgDiag.getMessaggio_replaceKeywords("validazioneContenutiApplicativiNonRiuscita")); 
									return esito;
								}
							}finally{
								if(transactionNullable!=null) {
									transactionNullable.getTempiElaborazione().endValidazioneRisposta();
								}
								if(binXSD!=null){
									try{
										binXSD.close();
									}catch(Exception e){}
								}
							}
						}
						else{
							msgDiag.logPersonalizzato("validazioneContenutiApplicativiRispostaDisabilitata");
						}


						
						/* ------------  Header integrazione Risposta ------------- */
						
						HeaderIntegrazione headerIntegrazioneRisposta = new HeaderIntegrazione(idTransazione);		
						InResponsePAMessage inResponsePAMessage = new InResponsePAMessage();
						inResponsePAMessage.setBustaRichiesta(bustaRichiesta);
						inResponsePAMessage.setMessage(responseMessage);
						if(pa!=null)
							inResponsePAMessage.setPortaApplicativa(pa);
						else
							inResponsePAMessage.setPortaDelegata(pd);
						inResponsePAMessage.setHeaders(connectorSender.getHeaderTrasporto());
						inResponsePAMessage.setServizio(outRequestPAMessage.getServizio());
						inResponsePAMessage.setSoggettoMittente(outRequestPAMessage.getSoggettoMittente());
						Utilities.printFreeMemory("ConsegnaContenutiApplicativi - Gestione Header Integrazione... ");					
						for(int i=0; i<tipiIntegrazione.length;i++){
							try{
								IGestoreIntegrazionePA gestore = null;
								try{
									gestore = (IGestoreIntegrazionePA) this.pluginLoader.newIntegrazionePortaApplicativa(tipiIntegrazione[i]);
								}catch(Exception e){
									throw e;
								}
								if(gestore!=null){
									String classType = null;
									try {
										classType = gestore.getClass().getName();
										AbstractCore.init(gestore, pddContext, protocolFactory);
									}catch(Exception e){
										throw new Exception("Riscontrato errore durante l'inizializzazione della classe ["+classType+
												"] da utilizzare per la gestione dell'integrazione (Risposta) delle erogazione di tipo ["+tipiIntegrazione[i]+"]: "+e.getMessage());
									}
									if(responseMessage!=null){
										gestore.readInResponseHeader(headerIntegrazioneRisposta,inResponsePAMessage);
									}else if( ! (gestore instanceof IGestoreIntegrazionePASoap) ){
										gestore.readInResponseHeader(headerIntegrazioneRisposta,inResponsePAMessage);
									}
								}
							} catch (Exception e) {
								this.log.debug("Errore durante la lettura dell'header di integrazione ["+ tipiIntegrazione[i]
										+ "]: "+ e.getMessage(),e);
								msgDiag.addKeyword(CostantiPdD.KEY_TIPO_HEADER_INTEGRAZIONE,tipiIntegrazione[i]);
								msgDiag.addKeywordErroreProcessamento(e);
								msgDiag.logPersonalizzato("headerIntegrazione.letturaFallita");
							}
						}
						
						/* ------------  Correlazione Applicativa Risposta ------------- */
						if(correlazioneApplicativaRisposta!=null){
							Utilities.printFreeMemory("ConsegnaContenutiApplicativi - CorrelazioneApplicativa... ");	
							GestoreCorrelazioneApplicativa gestoreCorrelazione = null;
							try{
								
								gestoreCorrelazione = 
										new GestoreCorrelazioneApplicativa(openspcoopstate.getStatoRisposta(),
												this.log,soggettoFruitore,idServizio, servizioApplicativo,protocolFactory,
												transactionNullable, pddContext);
								
								gestoreCorrelazione.verificaCorrelazioneRisposta(correlazioneApplicativaRisposta, responseMessage, headerIntegrazioneRisposta, false);
								
								idCorrelazioneApplicativaRisposta = gestoreCorrelazione.getIdCorrelazione();
								
								if(idCorrelazioneApplicativaRisposta!=null) {
									if(transactionNullable!=null) {
										transactionNullable.setCorrelazioneApplicativaRisposta(idCorrelazioneApplicativaRisposta);
									}
								}
								
								if(richiestaApplicativa!=null)
									richiestaApplicativa.setIdCorrelazioneApplicativaRisposta(idCorrelazioneApplicativaRisposta);
								else if(richiestaDelegata!=null)
									richiestaDelegata.setIdCorrelazioneApplicativaRisposta(idCorrelazioneApplicativaRisposta);
														
								msgDiag.setIdCorrelazioneRisposta(idCorrelazioneApplicativaRisposta);
								
							}catch(Exception e){
								
								pddContext.addObject(org.openspcoop2.core.constants.Costanti.ERRORE_CORRELAZIONE_APPLICATIVA_RISPOSTA, "true");
								
								msgDiag.logErroreGenerico(e,"CorrelazioneApplicativaRisposta");
								this.log.error("Riscontrato errore durante il controllo di correlazione applicativa della risposta: "+ e.getMessage(),e);
								
								ErroreIntegrazione errore = null;
								if(gestoreCorrelazione!=null){
									errore = gestoreCorrelazione.getErrore();
								}
								if(errore==null){
									errore = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
											get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_553_CORRELAZIONE_APPLICATIVA_RISPOSTA_NON_RIUSCITA);
								}
								
								ejbUtils.setIntegrationFunctionErrorPortaApplicativa(IntegrationFunctionError.APPLICATION_CORRELATION_IDENTIFICATION_RESPONSE_FAILED);
								if(localForwardEngine!=null) {
									localForwardEngine.setIntegrationFunctionError(IntegrationFunctionError.APPLICATION_CORRELATION_IDENTIFICATION_RESPONSE_FAILED);
								}
								
								this.sendErroreProcessamento(localForward, localForwardEngine, ejbUtils, 
										errore,
										idModuloInAttesa, bustaRichiesta, idCorrelazioneApplicativa, idCorrelazioneApplicativaRisposta, servizioApplicativoFruitore, e,
										(responseMessage!=null ? responseMessage.getParseException() : null),
										pddContext);
								
								esito.setEsitoInvocazione(true); 
								esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
										"Riscontrato errore durante il controllo di correlazione applicativa della risposta: "+ e.getMessage());
								openspcoopstate.releaseResource();
								return esito;
							}
						}
						
						/* ------------  Header integrazione Risposta (Update/Delete) ------------- */
						
						Utilities.printFreeMemory("ConsegnaContenutiApplicativi - Update/Delete Header Integrazione... ");					
						for(int i=0; i<tipiIntegrazione.length;i++){
							try{
								IGestoreIntegrazionePA gestore = null;
								try{
									gestore = (IGestoreIntegrazionePA) this.pluginLoader.newIntegrazionePortaApplicativa(tipiIntegrazione[i]);
								}catch(Exception e){
									throw e;
								}
								if(gestore!=null){
									String classType = null;
									try {
										classType = gestore.getClass().getName();
										AbstractCore.init(gestore, pddContext, protocolFactory);
									}catch(Exception e){
										throw new Exception("Riscontrato errore durante l'inizializzazione della classe ["+classType+
												"] da utilizzare per la gestione dell'integrazione (Risposta Update/Delete) delle erogazione di tipo ["+tipiIntegrazione[i]+"]: "+e.getMessage());
									}
									if (responseMessage!=null && (gestore instanceof IGestoreIntegrazionePASoap) ) {
										if(this.propertiesReader.deleteHeaderIntegrazioneResponsePA()){
											((IGestoreIntegrazionePASoap)gestore).deleteInResponseHeader(inResponsePAMessage);
										}else{
											((IGestoreIntegrazionePASoap)gestore).updateInResponseHeader(inResponsePAMessage, idMessaggioConsegna, idMessageResponse, servizioApplicativoFruitore, 
													idCorrelazioneApplicativaRisposta, idCorrelazioneApplicativa);
										}
									}
								}
							} catch (Exception e) {
								this.log.debug("Errore durante la lettura dell'header di integrazione ["+ tipiIntegrazione[i]
										+ "]: "+ e.getMessage(),e);
								msgDiag.addKeyword(CostantiPdD.KEY_TIPO_HEADER_INTEGRAZIONE,tipiIntegrazione[i]);
								msgDiag.addKeywordErroreProcessamento(e);
								msgDiag.logPersonalizzato("headerIntegrazione.letturaFallita");
							}
						}
						
						
						/* ------------  Gestione Funzionalita' speciali per Attachments (Manifest) ------------- */	
						// Funzionalita' necessaria solo per la consegna di un servizio
						if(richiestaApplicativa!=null){
							if(scartaBody){
								IntegrationFunctionError integrationFunctionError = null;
								try{
									if(responseMessage!=null){
										if(ServiceBinding.SOAP.equals(responseMessage.getServiceBinding())==false){
											integrationFunctionError = IntegrationFunctionError.NOT_SUPPORTED_BY_PROTOCOL;
											throw new Exception("Funzionalita 'ScartaBody' valida solamente per Service Binding SOAP");
										}
										
										// E' permesso SOLO per messaggi con attachment
										if(responseMessage.castAsSoap().countAttachments() <= 0){
											throw new Exception("La funzionalita' e' permessa solo per messaggi SOAP With Attachments");
										}
									}
								}catch(Exception e){
									msgDiag.addKeywordErroreProcessamento(e);
									msgDiag.logPersonalizzato("funzionalitaScartaBodyNonRiuscita");
																		
									if(integrationFunctionError==null) {
										integrationFunctionError = IntegrationFunctionError.BAD_RESPONSE;
									}
									ejbUtils.setIntegrationFunctionErrorPortaApplicativa(integrationFunctionError);
									if(localForwardEngine!=null) {
										localForwardEngine.setIntegrationFunctionError(integrationFunctionError);
									}
									
									this.sendErroreProcessamento(localForward, localForwardEngine, ejbUtils, 
											ErroriIntegrazione.ERRORE_425_SCARTA_BODY.getErrore425_ScartaBody(e.getMessage()),
											idModuloInAttesa, bustaRichiesta, idCorrelazioneApplicativa, idCorrelazioneApplicativaRisposta, servizioApplicativoFruitore, e,
											(responseMessage!=null ? responseMessage.getParseException() : null),
											pddContext);
									
									openspcoopstate.releaseResource();
									esito.setEsitoInvocazione(true); 
									esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
											msgDiag.getMessaggio_replaceKeywords("funzionalitaScartaBodyNonRiuscita")); 
									return esito;
								}
							}
							if(allegaBody){
								try{
									if(responseMessage!=null){
										TunnelSoapUtils.allegaBody(responseMessage, this.propertiesReader.getHeaderSoapActorIntegrazione());
									}
								}catch(Exception e){
									msgDiag.addKeywordErroreProcessamento(e);
									msgDiag.logPersonalizzato("funzionalitaAllegaBodyNonRiuscita");
									
									ejbUtils.setIntegrationFunctionErrorPortaApplicativa(IntegrationFunctionError.BAD_RESPONSE);
									if(localForwardEngine!=null) {
										localForwardEngine.setIntegrationFunctionError(IntegrationFunctionError.BAD_RESPONSE);
									}
									
									this.sendErroreProcessamento(localForward, localForwardEngine, ejbUtils, 
											ErroriIntegrazione.ERRORE_424_ALLEGA_BODY.getErrore424_AllegaBody(e.getMessage()),
											idModuloInAttesa, bustaRichiesta, idCorrelazioneApplicativa, idCorrelazioneApplicativaRisposta, servizioApplicativoFruitore, e,
											(responseMessage!=null ? responseMessage.getParseException() : null),
											pddContext);
									
									openspcoopstate.releaseResource();
									esito.setEsitoInvocazione(true); 
									esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
											msgDiag.getMessaggio_replaceKeywords("funzionalitaAllegaBodyNonRiuscita"));
									return esito;
								}
							}
						}
					}
					
					
					
					// processResponse localforward
					if(localForward){
						
						if( richiestaApplicativa!=null && Costanti.SCENARIO_ONEWAY_INVOCAZIONE_SERVIZIO.equals(richiestaApplicativa.getScenario()) ){
							// Per avere esattamente il solito comportamento dello scenario con protocollo.
							if(soapFault==null && restProblem==null){
								// devo 'ignorare' la risposta anche se presente, essendo un profilo oneway.
								if(responseMessage!=null){
									if(ServiceBinding.SOAP.equals(responseMessage.getServiceBinding())){
										SOAPBody body = responseMessage.castAsSoap().getSOAPBody();
										if(body!=null)
											body.removeContents();	
									}
								}
							}
						}
						
						msgDiag.mediumDebug("Process response message for local forward...");
						try{
							if(localForwardEngine.processResponse(responseMessage)==false){
								localForwardEngine.sendErrore(localForwardEngine.getResponseMessageError());
								openspcoopstate.releaseResource();
								esito.setEsitoInvocazione(true); 
								esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,"localForwardEngine.processResponse==false");
								return esito;
							}
							if(localForwardEngine.getResponseMessageAfterProcess()!=null){
								// Messaggio aggiornato
								responseMessage = localForwardEngine.getResponseMessageAfterProcess();
							}
						}catch(Exception e){
							msgDiag.addKeywordErroreProcessamento(e);
							msgDiag.logErroreGenerico(e, "localForwardProcessResponse");
							this.sendErroreProcessamento(localForward, localForwardEngine, ejbUtils, 
									ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
										get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_556_LOCAL_FORWARD_PROCESS_RESPONSE_ERROR),
									idModuloInAttesa, bustaRichiesta, idCorrelazioneApplicativa, idCorrelazioneApplicativaRisposta, servizioApplicativoFruitore, e,
									(responseMessage!=null ? responseMessage.getParseException() : null),
									pddContext);
							
							openspcoopstate.releaseResource();
							esito.setEsitoInvocazione(true); 
							esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
									e.getMessage());
							return esito;
						}
					}
					
					
					
					// Salvo messaggio ricevuto
					msgDiag.mediumDebug("Registrazione messaggio di risposta nel RepositoryMessaggi...");
					try{
						msgResponse = new GestoreMessaggi(openspcoopstate, false, idMessageResponse,Costanti.OUTBOX,msgDiag,pddContext);
						msgResponse.registraMessaggio(responseMessage,idCorrelazioneApplicativa,idCorrelazioneApplicativaRisposta);
						msgResponse.aggiornaRiferimentoMessaggio(idMessaggioConsegna);
						msgResponse.aggiornaProprietarioMessaggio(ImbustamentoRisposte.ID_MODULO);
						if(responseMessage!=null && responseMessage.getParseException()!= null)
							throw responseMessage.getParseException().getSourceException(); // gestito nel cacth
					}catch(Exception e){
						msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_CONSEGNA, ("salvataggio risposta, "+e.getMessage()));
						if(bustaRichiesta.getMittente()!=null && bustaRichiesta.getTipoMittente()!=null){
							msgDiag.logPersonalizzato("consegnaConErrore");
						}
						else{
							msgDiag.logPersonalizzato("consegnaConErrore.mittenteAnonimo");
						}
						
						msgResponse.deleteMessageFromFileSystem(); // elimino eventuale risposta salvata su fileSystem
						msgResponse.aggiornaProprietarioMessaggio(TimerGestoreMessaggi.ID_MODULO);
						if(responseMessage.getParseException() == null){
							
							this.sendErroreProcessamento(localForward, localForwardEngine, ejbUtils, 
									ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
										get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_510_SAVE_RESPONSE_MSG),
									idModuloInAttesa, bustaRichiesta, idCorrelazioneApplicativa, idCorrelazioneApplicativaRisposta, servizioApplicativoFruitore, e,
									(responseMessage!=null ? responseMessage.getParseException() : null),
									pddContext);
							
						} else {
							
							ejbUtils.setIntegrationFunctionErrorPortaApplicativa(IntegrationFunctionError.UNPROCESSABLE_RESPONSE_CONTENT);
							if(localForwardEngine!=null) {
								localForwardEngine.setIntegrationFunctionError(IntegrationFunctionError.UNPROCESSABLE_RESPONSE_CONTENT);
							}
							
							pddContext.addObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RISPOSTA_NON_RICONOSCIUTO, true);
							this.sendErroreProcessamento(localForward, localForwardEngine, ejbUtils, 
									ErroriIntegrazione.ERRORE_440_PARSING_EXCEPTION_RISPOSTA.
										getErrore440_MessaggioRispostaMalformato(responseMessage.getParseException().getParseException()),
									idModuloInAttesa, bustaRichiesta, idCorrelazioneApplicativa, idCorrelazioneApplicativaRisposta, servizioApplicativoFruitore, 
									responseMessage.getParseException().getParseException(),
									responseMessage.getParseException(),
									pddContext);
							
						}
						openspcoopstate.releaseResource();
						esito.setEsitoInvocazione(true); 
						esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
								"salvataggio risposta, "+e.getMessage());
						return esito;
					}

					// Creazione messaggio di risposta
					if(localForward){
						
						localForwardEngine.sendResponse(idMessageResponse);
						
					}
					else{
						
						ImbustamentoRisposteMessage imbustamentoRisposteMSG = new ImbustamentoRisposteMessage();
						imbustamentoRisposteMSG.setIDMessageResponse(idMessageResponse);
						imbustamentoRisposteMSG.setRichiestaApplicativa(richiestaApplicativa);
						imbustamentoRisposteMSG.setRichiestaDelegata(richiestaDelegata);
						imbustamentoRisposteMSG.setBusta(bustaRichiesta);
						imbustamentoRisposteMSG.setSpedizioneMsgIngresso(ejbUtils.getSpedizioneMsgIngresso());
						imbustamentoRisposteMSG.setRicezioneMsgRisposta(ejbUtils.getRicezioneMsgRisposta());
						imbustamentoRisposteMSG.setOneWayVersione11(oneWayVersione11);
						imbustamentoRisposteMSG.setStateless(consegnaContenutiApplicativiMsg.isStateless());
						imbustamentoRisposteMSG.setImplementazionePdDSoggettoMittente(consegnaContenutiApplicativiMsg.getImplementazionePdDSoggettoMittente());
						imbustamentoRisposteMSG.setImplementazionePdDSoggettoDestinatario(consegnaContenutiApplicativiMsg.getImplementazionePdDSoggettoDestinatario());
						imbustamentoRisposteMSG.setPddContext(pddContext);
						
	
						// Spedizione risposta al modulo 'ImbustamentoRisposte'
						msgDiag.mediumDebug("Invio messaggio al modulo di ImbustamentoRisposte...");
						if (openspcoopstate instanceof OpenSPCoopStateful){
							try{
								ejbUtils.getNodeSender(this.propertiesReader, this.log).send(imbustamentoRisposteMSG, ImbustamentoRisposte.ID_MODULO, msgDiag, 
										identitaPdD,ConsegnaContenutiApplicativi.ID_MODULO, idMessaggioConsegna,msgResponse);
							} catch (Exception e) {
								this.log.error("Spedizione->ImbustamentoRisposte non riuscita",e);	
								msgDiag.logErroreGenerico(e,"GenericLib.nodeSender.send(ImbustamentoRisposte)");
								msgResponse.deleteMessageFromFileSystem(); // elimino eventuale risposta salvata su fileSystem
								
								this.sendErroreProcessamento(localForward, localForwardEngine, ejbUtils, 
										ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
											get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_512_SEND),
										idModuloInAttesa, bustaRichiesta, idCorrelazioneApplicativa, idCorrelazioneApplicativaRisposta, servizioApplicativoFruitore, e,
										(responseMessage!=null ? responseMessage.getParseException() : null),
										pddContext);
								
								openspcoopstate.releaseResource();
								esito.setEsitoInvocazione(true); 
								esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO, "Spedizione->ImbustamentoRisposte non riuscita"); 
								return esito;
							}
						}
						else ((OpenSPCoopStateless)openspcoopstate).setMessageLib(imbustamentoRisposteMSG);
					}

				}

			}











			/* ---------- Aggiornamento numero di sequenza ---------------- */
			if(ordineConsegna!=null){				
				if(oneWayVersione11 || openspcoopstate instanceof OpenSPCoopStateful){
					msgDiag.mediumDebug("Aggiornamento numero sequenza per consegna in ordine...");

					ordineConsegna.setNextSequenza_daRicevere(bustaRichiesta);
				}
			}






			/* ---------- Gestione Transazione Modulo ---------------- */

			// messaggio finale
			msgDiag.addKeyword(CostantiPdD.KEY_TIPO_CONNETTORE, tipoConnector);
			msgDiag.logPersonalizzato("gestioneConsegnaTerminata");

			// Commit JDBC della risposta
			msgDiag.mediumDebug("Commit delle operazioni per la gestione della richiesta...");
			openspcoopstate.commit();

			// Aggiornamento cache messaggio
			if(msgRequest!=null)
				msgRequest.addMessaggiIntoCache_readFromTable(ConsegnaContenutiApplicativi.ID_MODULO, "richiesta");
			if(msgResponse!=null)
				msgResponse.addMessaggiIntoCache_readFromTable(ConsegnaContenutiApplicativi.ID_MODULO, "risposta");

			// Aggiornamento cache proprietario messaggio
			if(msgRequest!=null)
				msgRequest.addProprietariIntoCache_readFromTable(ConsegnaContenutiApplicativi.ID_MODULO, "richiesta",null,false);
			if(msgResponse!=null)
				msgResponse.addProprietariIntoCache_readFromTable(ConsegnaContenutiApplicativi.ID_MODULO, "risposta",idMessaggioConsegna,false);


		}catch(Throwable e){
			this.log.error("ErroreGenerale",e);
			msgDiag.logErroreGenerico(e, "Generale");

			if ( msgResponse!=null ){
				msgResponse.deleteMessageFromFileSystem(); // elimino eventuale risposta salvata su fileSystem
			}

			if(existsModuloInAttesaRispostaApplicativa) {
				try{
					
					this.sendErroreProcessamento(localForward, localForwardEngine, ejbUtils, 
							ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
								getErroreIntegrazione(),
							idModuloInAttesa, bustaRichiesta, idCorrelazioneApplicativa, idCorrelazioneApplicativaRisposta, servizioApplicativoFruitore, e,
							(responseMessage!=null ? responseMessage.getParseException() : null),
							pddContext);
					
					esito.setEsitoInvocazione(true); 
					esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO, "ErroreGenerale");
				}catch(Exception er){
					msgDiag.logErroreGenerico(er,"ejbUtils.sendErroreGenerale(profiloConRisposta)");
					ejbUtils.rollbackMessage("Spedizione Errore al Mittente durante una richiesta con gestione della risposta non riuscita",servizioApplicativo, esito);
					esito.setEsitoInvocazione(false); 
					esito.setStatoInvocazioneErroreNonGestito(er);
				}
			}else{
				ejbUtils.rollbackMessage("ErroreGenerale:"+e.getMessage(), servizioApplicativo, esito);
				esito.setEsitoInvocazione(false); 
				esito.setStatoInvocazioneErroreNonGestito(e);
			}
			openspcoopstate.releaseResource();
			return esito;
		}finally{
			try {
				if(transazioneApplicativoServer!=null) {
					if(consegnaMessageTrasformato!=null) {
						transazioneApplicativoServer.setRichiestaUscitaBytes(consegnaMessageTrasformato.getOutgoingMessageContentLength());
					}
					if(responseMessage!=null) {
						long incomingResponseMessageContentLength = responseMessage.getIncomingMessageContentLength();
						if(incomingResponseMessageContentLength<=0){
							incomingResponseMessageContentLength = responseContentLength;
							if(incomingResponseMessageContentLength<0){
								//System.out.println("FLUSH");
								// forzo la lettura del messaggio per impostare la dimensione della richiesta
								try{
									responseMessage.writeTo(NullOutputStream.NULL_OUTPUT_STREAM, true);
								}catch(Exception eFlush){}
								incomingResponseMessageContentLength = responseMessage.getIncomingMessageContentLength();
							}
						}
						transazioneApplicativoServer.setRispostaIngressoBytes(incomingResponseMessageContentLength);
					}
				}
			}catch(Throwable t) {
				this.log.error("Errore durante la lettura delle dimensioni dei messaggi: "+t.getMessage(),t);
			}
			try{
				if(connectorSenderForDisconnect!=null)
					connectorSenderForDisconnect.disconnect();
			}catch(Exception e){
				try{
					if(msgDiag!=null)
						msgDiag.logDisconnectError(e, location);
				}catch(Exception eDisconnect){
					this.log.error("Errore durante la chiusura delle connessione: "+eDisconnect.getMessage(),e);
				}
			}
		}




		// Elimino SIL destinatario a cui ho consegnato il messaggio
		// a meno di consegna per riferimento
		//	if(connectionDB!=null){
		//		msgDiag.mediumDebug("Eliminazione SIL destinatario del messaggio nella tabelle MSG_SERVIZI_APPLICATIVI...");
		if(consegnaPerRiferimento==false){
			try{
				String idMessaggioGestoreMessaggiRichiestaEliminazione = idMessaggioConsegna;
				if(idMessaggioPreBehaviour!=null){
					idMessaggioGestoreMessaggiRichiestaEliminazione = bustaRichiesta.getID();
				}
				
				GestoreMessaggi gestoreEliminazioneDestinatario = new GestoreMessaggi(openspcoopstate, true, idMessaggioGestoreMessaggiRichiestaEliminazione,Costanti.INBOX,msgDiag,pddContext);
				if(idMessaggioPreBehaviour!=null && !(openspcoopstate instanceof OpenSPCoopStateful) ){
					gestoreEliminazioneDestinatario.setOneWayVersione11(true); // per forzare l'update su db	
				}
				else{
					gestoreEliminazioneDestinatario.setOneWayVersione11(oneWayVersione11);		
				}
				gestoreEliminazioneDestinatario.eliminaDestinatarioMessaggio(servizioApplicativo, null, oraRegistrazione);
			}catch(Exception e){
				msgDiag.logErroreGenerico(e,"gestoreEliminazioneDestinatario.eliminaDestinatarioMessaggio("+servizioApplicativo+",null)");
			}
		}

		//	Rilascio connessione al DB
		msgDiag.mediumDebug(ConsegnaContenutiApplicativi.ID_MODULO+ " Rilascio le risorse..");
		openspcoopstate.releaseResource();

		msgDiag.mediumDebug("Lavoro Terminato.");
		esito.setEsitoInvocazione(true); 
		esito.setStatoInvocazione(EsitoLib.OK,null);
		return esito; 

	}
	
		
	private void sendErroreProcessamento(boolean localForward,LocalForwardEngine localForwardEngine, EJBUtils ejbUtils, ErroreIntegrazione errore,
			String idModuloInAttesa,Busta bustaRichiesta,String idCorrelazioneApplicativa, String idCorrelazioneApplicativaRisposta,
			String servizioApplicativoFruitore, Throwable e, ParseException parseException,
			PdDContext pddContext) throws LocalForwardException, EJBUtilsException, ProtocolException{
		if(localForward){
			localForwardEngine.sendErrore(pddContext, errore,e,parseException);
		}else{
			ejbUtils.sendAsRispostaBustaErroreProcessamento(idModuloInAttesa,bustaRichiesta,
					errore,	idCorrelazioneApplicativa,idCorrelazioneApplicativaRisposta,servizioApplicativoFruitore,e,parseException);
		}
	}
	
	private void sendErroreProcessamento(boolean localForward,LocalForwardEngine localForwardEngine, EJBUtils ejbUtils, OpenSPCoop2Message errorMessage, String errorDetail,
			String idModuloInAttesa,Busta bustaRichiesta,String idCorrelazioneApplicativa, String idCorrelazioneApplicativaRisposta,
			String servizioApplicativoFruitore) throws LocalForwardException, EJBUtilsException, ProtocolException{
		if(localForward){
			localForwardEngine.sendErrore(errorMessage);
		}else{
			ejbUtils.sendAsRispostaBustaErroreProcessamento(idModuloInAttesa,bustaRichiesta,
					idCorrelazioneApplicativa,idCorrelazioneApplicativaRisposta,servizioApplicativoFruitore,
					errorMessage, errorDetail);
		}
	}

	private void mappingProtocolProperties(Map<String, List<String>> protocolProperties,Map<String, List<String>> propertiesDaImpostare,
			IDSoggetto soggettoFruitoreHeaderIntegrazione, IDServizio servizioHeaderIntegrazione,
			IDSoggetto soggettoFruitore,IDServizio idServizio,
			Busta bustaRichiesta, String idCorrelazioneApplicativa){
		// mapping in valori delle keyword delle proprieta di trasporto protocol-properties.
		if(protocolProperties != null){
			Iterator<String> keys = protocolProperties.keySet().iterator();
			while (keys.hasNext()) {
				String key = (String) keys.next();
				List<String> values = protocolProperties.get(key);
				String value = TransportUtils.getFirstValue(values);
				
				if(ProprietaProtocolloValore.TIPO_MITTENTE.equals(value)){
					if(soggettoFruitoreHeaderIntegrazione!=null && soggettoFruitoreHeaderIntegrazione.getTipo()!=null){
						TransportUtils.setHeader(propertiesDaImpostare,key,soggettoFruitoreHeaderIntegrazione.getTipo());
					}else if(soggettoFruitore!=null && soggettoFruitore.getTipo()!=null){
						TransportUtils.setHeader(propertiesDaImpostare,key,soggettoFruitore.getTipo());
					}else if(bustaRichiesta!=null && bustaRichiesta.getTipoMittente()!=null){
						TransportUtils.setHeader(propertiesDaImpostare,key,bustaRichiesta.getTipoMittente());
					}
				}
				if(ProprietaProtocolloValore.MITTENTE.equals(value)){
					if(soggettoFruitoreHeaderIntegrazione!=null && soggettoFruitoreHeaderIntegrazione.getNome()!=null){
						TransportUtils.setHeader(propertiesDaImpostare,key,soggettoFruitoreHeaderIntegrazione.getNome());
					}else if(soggettoFruitore!=null && soggettoFruitore.getNome()!=null){
						TransportUtils.setHeader(propertiesDaImpostare,key,soggettoFruitore.getNome());
					}else if(bustaRichiesta!=null && bustaRichiesta.getMittente()!=null){
						TransportUtils.setHeader(propertiesDaImpostare,key,bustaRichiesta.getMittente());
					}
				}
				if(ProprietaProtocolloValore.IDENTIFICATIVO_PORTA_MITTENTE.equals(value)){
					if(soggettoFruitoreHeaderIntegrazione!=null && soggettoFruitoreHeaderIntegrazione.getCodicePorta()!=null){
						TransportUtils.setHeader(propertiesDaImpostare,key,soggettoFruitoreHeaderIntegrazione.getCodicePorta());
					}else if(soggettoFruitore!=null && soggettoFruitore.getCodicePorta()!=null){
						TransportUtils.setHeader(propertiesDaImpostare,key,soggettoFruitore.getCodicePorta());
					}else if(bustaRichiesta!=null && bustaRichiesta.getIdentificativoPortaMittente()!=null){
						TransportUtils.setHeader(propertiesDaImpostare,key,bustaRichiesta.getIdentificativoPortaMittente());
					}
				}
					
				if(ProprietaProtocolloValore.TIPO_DESTINATARIO.equals(value)){
					if(servizioHeaderIntegrazione!=null && servizioHeaderIntegrazione.getSoggettoErogatore()!=null &&
							servizioHeaderIntegrazione.getSoggettoErogatore().getTipo()!=null){
						TransportUtils.setHeader(propertiesDaImpostare,key,servizioHeaderIntegrazione.getSoggettoErogatore().getTipo());
					}else if(idServizio!=null && idServizio.getSoggettoErogatore()!=null &&
							idServizio.getSoggettoErogatore().getTipo()!=null){
						TransportUtils.setHeader(propertiesDaImpostare,key,idServizio.getSoggettoErogatore().getTipo());
					}else if(bustaRichiesta!=null && bustaRichiesta.getTipoDestinatario()!=null){
						TransportUtils.setHeader(propertiesDaImpostare,key,bustaRichiesta.getTipoDestinatario());
					}
				}	
				if(ProprietaProtocolloValore.DESTINATARIO.equals(value)){
					if(servizioHeaderIntegrazione!=null && servizioHeaderIntegrazione.getSoggettoErogatore()!=null &&
							servizioHeaderIntegrazione.getSoggettoErogatore().getNome()!=null){
						TransportUtils.setHeader(propertiesDaImpostare,key,servizioHeaderIntegrazione.getSoggettoErogatore().getNome());
					}else if(idServizio!=null && idServizio.getSoggettoErogatore()!=null &&
							idServizio.getSoggettoErogatore().getNome()!=null){
						TransportUtils.setHeader(propertiesDaImpostare,key,idServizio.getSoggettoErogatore().getNome());
					}else if(bustaRichiesta!=null && bustaRichiesta.getDestinatario()!=null){
						TransportUtils.setHeader(propertiesDaImpostare,key,bustaRichiesta.getDestinatario());
					}
				}
				if(ProprietaProtocolloValore.IDENTIFICATIVO_PORTA_DESTINATARIO.equals(value)){
					if(servizioHeaderIntegrazione!=null && servizioHeaderIntegrazione.getSoggettoErogatore()!=null && 
							servizioHeaderIntegrazione.getSoggettoErogatore().getCodicePorta()!=null){
						TransportUtils.setHeader(propertiesDaImpostare,key,servizioHeaderIntegrazione.getSoggettoErogatore().getCodicePorta());
					}else if(idServizio!=null && idServizio.getSoggettoErogatore()!=null && 
							idServizio.getSoggettoErogatore().getCodicePorta()!=null){
						TransportUtils.setHeader(propertiesDaImpostare,key,idServizio.getSoggettoErogatore().getCodicePorta());
					}else if(bustaRichiesta!=null && bustaRichiesta.getIdentificativoPortaDestinatario()!=null){
						TransportUtils.setHeader(propertiesDaImpostare,key,bustaRichiesta.getIdentificativoPortaDestinatario());
					}
				}

				if(ProprietaProtocolloValore.TIPO_SERVIZIO.equals(value)){
					if(servizioHeaderIntegrazione!=null && servizioHeaderIntegrazione.getTipo()!=null){
						TransportUtils.setHeader(propertiesDaImpostare,key,servizioHeaderIntegrazione.getTipo());
					}else if(idServizio!=null && idServizio.getTipo()!=null){
						TransportUtils.setHeader(propertiesDaImpostare,key,idServizio.getTipo());
					}else if(bustaRichiesta!=null && bustaRichiesta.getTipoServizio()!=null){
						TransportUtils.setHeader(propertiesDaImpostare,key,bustaRichiesta.getTipoServizio());
					}
				}
				if(ProprietaProtocolloValore.SERVIZIO.equals(value)){
					if(servizioHeaderIntegrazione!=null && servizioHeaderIntegrazione.getNome()!=null){
						TransportUtils.setHeader(propertiesDaImpostare,key,servizioHeaderIntegrazione.getNome());
					}else if(idServizio!=null && idServizio.getNome()!=null){
						TransportUtils.setHeader(propertiesDaImpostare,key,idServizio.getNome());
					}else if(bustaRichiesta!=null && bustaRichiesta.getServizio()!=null){
						TransportUtils.setHeader(propertiesDaImpostare,key,bustaRichiesta.getServizio());
					}
				}
				if(ProprietaProtocolloValore.VERSIONE_SERVIZIO.equals(value)){
					if(servizioHeaderIntegrazione!=null && servizioHeaderIntegrazione.getVersione()!=null){
						TransportUtils.setHeader(propertiesDaImpostare,key,servizioHeaderIntegrazione.getVersione().intValue()+"");
					}else if(idServizio!=null && idServizio.getVersione()!=null){
						TransportUtils.setHeader(propertiesDaImpostare,key,idServizio.getVersione().intValue()+"");
					}else if(bustaRichiesta!=null && bustaRichiesta.getVersioneServizio()!=null){
						TransportUtils.setHeader(propertiesDaImpostare,key,bustaRichiesta.getVersioneServizio().intValue()+"");
					}
				}
				
				if(ProprietaProtocolloValore.AZIONE.equals(value)){
					if(servizioHeaderIntegrazione!=null && servizioHeaderIntegrazione.getAzione()!=null){
						TransportUtils.setHeader(propertiesDaImpostare,key,servizioHeaderIntegrazione.getAzione());
					}else if(idServizio!=null && idServizio.getAzione()!=null){
						TransportUtils.setHeader(propertiesDaImpostare,key,idServizio.getAzione());
					}else if(bustaRichiesta!=null && bustaRichiesta.getAzione()!=null){
						TransportUtils.setHeader(propertiesDaImpostare,key,bustaRichiesta.getAzione());
					}
				}
				
				if(ProprietaProtocolloValore.IDENTIFICATIVO.equals(value)){
					if(bustaRichiesta!=null && bustaRichiesta.getID()!=null){
						TransportUtils.setHeader(propertiesDaImpostare,key,bustaRichiesta.getID());
					}
				}

				if(ProprietaProtocolloValore.IDENTIFICATIVO_CORRELAZIONE_APPLICATIVA.equals(value)){
					if(idCorrelazioneApplicativa!=null){
						TransportUtils.setHeader(propertiesDaImpostare,key,idCorrelazioneApplicativa);
					}
				}
			}
		}
	}
	
	
	private void emitDiagnostico(boolean invokerNonSupportato, Busta bustaRichiesta, MsgDiagnostico msgDiag, boolean errorConsegna) {
		if(invokerNonSupportato==false){
			if(bustaRichiesta.getMittente()!=null && bustaRichiesta.getTipoMittente()!=null){
				if(errorConsegna){
					msgDiag.logPersonalizzato("consegnaConErrore");
				}else{
					msgDiag.logPersonalizzato("consegnaEffettuata");
				}
			}
			else{
				if(errorConsegna){
					msgDiag.logPersonalizzato("consegnaConErrore.mittenteAnonimo");
				}else{
					msgDiag.logPersonalizzato("consegnaEffettuata.mittenteAnonimo");
				}
			}
		}
	}
	
	private void correctForwardPathNotifiche(TransazioneApplicativoServer transazioneApplicativoServer, OpenSPCoop2Message msg, IProtocolFactory<?> pf) throws ProtocolException {
		if(transazioneApplicativoServer!=null && ServiceBinding.REST.equals(msg.getServiceBinding())) {
			// non deve essere effettuato il forward del contesto nel path
			
			TransportRequestContext requestContext = msg.getTransportRequestContext();
			if(requestContext!=null) {
				String resourcePath = requestContext.getFunctionParameters();
				if(resourcePath!=null){
					if(resourcePath.startsWith("/")){
						resourcePath = resourcePath.substring(1);
					}
					if(requestContext.getInterfaceName()!=null) {
						if(resourcePath.startsWith(requestContext.getInterfaceName())){
							requestContext.setFunctionParameters(requestContext.getInterfaceName());
						}		
						else {
							String normalizedInterfaceName = ConnettoreUtils.normalizeInterfaceName(msg, ConsegnaContenutiApplicativi.ID_MODULO, pf);
							if(normalizedInterfaceName!=null && resourcePath.startsWith(normalizedInterfaceName)){
								requestContext.setFunctionParameters(normalizedInterfaceName);
							}
						}
					}
				}
			}
		}
	}
}
