/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it). 
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
import org.openspcoop2.pdd.services.connector.AsyncResponseCallbackClientEvent;
import org.openspcoop2.pdd.services.connector.ConnectorException;
import org.openspcoop2.pdd.services.connector.IAsyncResponseCallback;
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
public class ConsegnaContenutiApplicativi extends GenericLib implements IAsyncResponseCallback {

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


	private IOpenSPCoopState openspcoopstate = null;
	private RegistroServiziManager registroServiziManager = null;
	private ConfigurazionePdDManager configurazionePdDManager = null;
	private MsgDiagnostico msgDiag = null;
	private ConsegnaContenutiApplicativiMessage consegnaContenutiApplicativiMsg = null;
	
	private PdDContext pddContext = null;
	private String idTransazione = null;
	private Transaction transactionNullable = null;
	private RequestInfo requestInfo = null;
	private String idModuloInAttesa = null; // in caso di richiesta delegata serve per il profilo asincrono
	
	private ProfiloDiCollaborazione profiloCollaborazione = null;
	private EJBUtils ejbUtils = null;
	private GestoreMessaggi msgRequest = null;
	private GestoreMessaggi msgResponse = null;
	private TransazioneApplicativoServer transazioneApplicativoServer = null;
	
	private	ConsegnaInOrdine ordineConsegna = null;
	private Busta bustaIndicazioneConsegnaInOrdine = null;
	
	private Trasformazioni trasformazioni = null;
	private GestoreTrasformazioni gestoreTrasformazioni = null;
	
	private ValidazioneContenutiApplicativi validazioneContenutoApplicativoApplicativo = null;
	private List<Proprieta> proprietaValidazioneContenutoApplicativoApplicativo = null;
					
	private IProtocolFactory<?> protocolFactory = null;
	private IProtocolVersionManager protocolManager = null;
	private DumpConfigurazione dumpConfig = null;
		
	private Date dataPrimaInvocazioneConnettore = null;
	private Date dataTerminataInvocazioneConnettore = null;
	private Date dataConsegna = null;
	private Date oraRegistrazione = null;
		
	private boolean invokerNonSupportato = false;
	private boolean errorConsegna = false;
	private String motivoErroreConsegna = null;
	private Exception eccezioneProcessamentoConnettore = null;
	private boolean riconsegna = false;
	private java.sql.Timestamp dataRiconsegna = null;
		
	private String tipoConnector = null;
	private String connectorClass = null;
	private Exception eInvokerNonSupportato = null;
	private ConnettoreMsg connettoreMsg = null;
	private IConnettore connectorSender = null;
	private IConnettore connectorSenderForDisconnect = null;
	private GestioneErrore gestioneConsegnaConnettore = null;
	private String location = "";
	
	private boolean asynWait = false;
	
	private Busta bustaRichiesta = null;
	private String idMessaggioConsegna = null;
	private String idMessaggioPreBehaviour = null;
	private OpenSPCoop2Message consegnaMessagePrimaTrasformazione = null;
	private OpenSPCoop2Message consegnaMessageTrasformato = null;
	private HttpRequestMethod httpRequestMethod = null;
	private OutRequestContext outRequestContext = null;
	private boolean isRicevutaAsincrona = false; 
	private OutRequestPAMessage outRequestPAMessage;
	private boolean consegnaPerRiferimento = false;
	
	private String idMessageResponse = null;
	private OpenSPCoop2Message responseMessage = null;
	private boolean rispostaPerRiferimento = false;
			
	private String idCorrelazioneApplicativa = null;
	private String idCorrelazioneApplicativaRisposta = null;
	private CorrelazioneApplicativaRisposta correlazioneApplicativaRisposta = null;
		
	private IDSoggetto identitaPdD = null;
	private String servizioApplicativo = null; 
	private String servizioApplicativoFruitore = null;
	private IDSoggetto soggettoFruitore = null;
	private IDServizio idServizio = null;
	private IDAccordo idAccordoServizio = null;
	private String implementazionePdDMittente = null;
	private String implementazionePdDDestinatario = null;
	private String profiloGestione = null;
	private String scenarioCooperazione = null; 
	private IDServizio servizioHeaderIntegrazione = null;
		
	private TransportResponseContext transportResponseContext = null;
	private int codiceRitornato = -1;
	private long responseContentLength = -1;
	private OpenSPCoop2MessageFactory faultMessageFactory = null;
	private SOAPFault soapFault = null;
	private ProblemRFC7807 restProblem = null;
	
	private TipoPdD tipoPdD = null;
	private PortaApplicativa pa = null;
	private IDPortaApplicativa idPA = null;
	private RichiestaApplicativa richiestaApplicativa = null;
	private PortaDelegata pd = null;
	private IDPortaDelegata idPD = null;
	private RichiestaDelegata richiestaDelegata = null;
	private ServizioApplicativo sa = null;
	private boolean portaDiTipoStateless = false;
	private boolean oneWayVersione11;
	
	private LocalForwardEngine localForwardEngine = null;
	private boolean localForward = false;
		
	private boolean existsModuloInAttesaRispostaApplicativa = false;
	private boolean isBlockedTransaction_responseMessageWithTransportCodeError = false;
	private boolean newConnectionForResponse = false; 
		
	private boolean gestioneManifest = false;
	private ProprietaManifestAttachments proprietaManifestAttachments;
	private boolean allegaBody = false;
	private boolean scartaBody = false;
		
	private String [] tipiIntegrazione = null;
	
					
	@Override
	public EsitoLib _onMessage(IOpenSPCoopState openspcoopstate,
			RegistroServiziManager registroServiziManager,ConfigurazionePdDManager configurazionePdDManager, 
			MsgDiagnostico msgDiag) throws OpenSPCoopStateException {

		EsitoLib esitoLib = null;
		try {
			esitoLib = this._process(openspcoopstate,
					registroServiziManager, configurazionePdDManager,
					msgDiag);
			return esitoLib;
		}finally {
			if(this.asyncResponseCallback!=null && !this.asynWait) {
				try {
					this.asyncResponseCallback.asyncComplete(AsyncResponseCallbackClientEvent.NONE, esitoLib);
				}catch(Exception e) {
					throw new OpenSPCoopStateException(e.getMessage(),e);
				}
			}
		}
	}
	
	@Override
	public void asyncComplete(AsyncResponseCallbackClientEvent clientEvent, Object ... args) throws ConnectorException { // Questo metodo verrà chiamato dalla catena di metodi degli oggetti (IAsyncResponseCallback) fatta scaturire dal response callback dell'Async Client NIO
		
		if(this.asyncResponseCallback==null) {
			throw new ConnectorException("Async context not active");
		}
		
		if(args==null || args.length<1) {
			throw new ConnectorException("Async context invalid (EsitoConsegna not found)");
		}
		Object esito = args[0];
		if(! (esito instanceof Boolean)) {
			throw new ConnectorException("Async context invalid (EsitoConsegna with uncorrect type '"+esito.getClass().getName()+"')");
		}
		this.errorConsegna = !((Boolean) esito);
		
		EsitoLib esitoLib = this._complete();
		
		this.asyncResponseCallback.asyncComplete(clientEvent, esitoLib);
	}
	
	public EsitoLib _process(IOpenSPCoopState openspcoopstateParam,
			RegistroServiziManager registroServiziManagerParam,ConfigurazionePdDManager configurazionePdDManagerParam, 
			MsgDiagnostico msgDiagParam) throws OpenSPCoopStateException {
		
		this.openspcoopstate = openspcoopstateParam;
		this.registroServiziManager = registroServiziManagerParam;
		this.configurazionePdDManager = configurazionePdDManagerParam;
		this.msgDiag = msgDiagParam;
		
		this.consegnaContenutiApplicativiMsg = (ConsegnaContenutiApplicativiMessage) this.openspcoopstate.getMessageLib();
		IDPortaApplicativa idApplicativa = null;
		if(this.consegnaContenutiApplicativiMsg.getRichiestaApplicativa()!=null) {
			idApplicativa = this.consegnaContenutiApplicativiMsg.getRichiestaApplicativa().getIdPortaApplicativa();
		}
		
		// Costruisco eventuale oggetto TransazioneServerApplicativo
		ConsegnaContenutiApplicativiBehaviourMessage behaviourConsegna = this.consegnaContenutiApplicativiMsg.getBehaviour();
		if(behaviourConsegna!=null && behaviourConsegna.getIdTransazioneApplicativoServer()!=null) {
			this.transazioneApplicativoServer = new TransazioneApplicativoServer();
			this.transazioneApplicativoServer.setIdTransazione(behaviourConsegna.getIdTransazioneApplicativoServer().getIdTransazione());
			this.transazioneApplicativoServer.setServizioApplicativoErogatore(behaviourConsegna.getIdTransazioneApplicativoServer().getServizioApplicativoErogatore());
			this.transazioneApplicativoServer.setConnettoreNome(behaviourConsegna.getIdTransazioneApplicativoServer().getConnettoreNome());
			if(behaviourConsegna.getOraRegistrazioneTransazioneApplicativoServer()!=null) {
				this.transazioneApplicativoServer.setDataRegistrazione(behaviourConsegna.getOraRegistrazioneTransazioneApplicativoServer());
				this.oraRegistrazione = behaviourConsegna.getOraRegistrazioneTransazioneApplicativoServer();
			}
			else {
				this.transazioneApplicativoServer.setDataRegistrazione(DateManager.getDate());
			}
			String protocol = (String) this.consegnaContenutiApplicativiMsg.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.PROTOCOL_NAME);
			this.transazioneApplicativoServer.setProtocollo(protocol);
			this.transazioneApplicativoServer.setDataAccettazioneRichiesta(DateManager.getDate());
			this.transazioneApplicativoServer.setIdentificativoMessaggio(this.consegnaContenutiApplicativiMsg.getBusta().getID());
			
			this.msgDiag.setTransazioneApplicativoServer(this.transazioneApplicativoServer, idApplicativa);
		}
		BehaviourLoadBalancer loadBalancer = null;
		if(this.consegnaContenutiApplicativiMsg!=null && this.consegnaContenutiApplicativiMsg.getLoadBalancer()!=null) {
			loadBalancer = this.consegnaContenutiApplicativiMsg.getLoadBalancer();
		}
		
		EsitoLib esitoLib = null;
		try {
			if(loadBalancer!=null) {
				try {
					loadBalancer.getLoadBalancerPool().addActiveConnection(loadBalancer.getConnectorName());
				}catch(Throwable t) {
					String prefix = "";
					if(this.transazioneApplicativoServer!=null) {
						prefix = "["+this.transazioneApplicativoServer.getIdTransazione()+"]["+this.transazioneApplicativoServer.getServizioApplicativoErogatore()+"] " ;
					}
					this.log.error(prefix+"Errore durante il salvataggio delle informazioni di load balancer: "+t.getMessage(),t);
				}
			}
			esitoLib = this.engine_onMessage(this.openspcoopstate, this.registroServiziManager, this.configurazionePdDManager, this.msgDiag, this.transazioneApplicativoServer, this.oraRegistrazione);
		}finally {
			if(loadBalancer!=null) {
				try {
					loadBalancer.getLoadBalancerPool().removeActiveConnection(loadBalancer.getConnectorName());
				}catch(Throwable t) {
					String prefix = "";
					if(this.transazioneApplicativoServer!=null) {
						prefix = "["+this.transazioneApplicativoServer.getIdTransazione()+"]["+this.transazioneApplicativoServer.getServizioApplicativoErogatore()+"] " ;
					}
					this.log.error(prefix+"Errore durante il salvataggio delle informazioni di load balancer: "+t.getMessage(),t);
				}
				try {
					boolean erroreUtilizzoConnettore = false;
					if(this.consegnaContenutiApplicativiMsg.getPddContext()!=null){
						Object o = this.consegnaContenutiApplicativiMsg.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.ERRORE_UTILIZZO_CONNETTORE);
						if(o!=null && (o instanceof Boolean)){
							erroreUtilizzoConnettore = (Boolean) o;
						}
					}
					if(erroreUtilizzoConnettore) {
						loadBalancer.getLoadBalancerPool().registerConnectionError(loadBalancer.getConnectorName());
					}
				}catch(Throwable t) {
					String prefix = "";
					if(this.transazioneApplicativoServer!=null) {
						prefix = "["+this.transazioneApplicativoServer.getIdTransazione()+"]["+this.transazioneApplicativoServer.getServizioApplicativoErogatore()+"] " ;
					}
					this.log.error(prefix+"Errore durante il salvataggio delle informazioni di load balancer (register error connection): "+t.getMessage(),t);
				}
			}
		}
				
		if(this.transazioneApplicativoServer!=null) {
			
			if(EsitoLib.OK != esitoLib.getStatoInvocazione()) {
				if(esitoLib.getMotivazioneErroreNonGestito()!=null && !"".equals(esitoLib.getMotivazioneErroreNonGestito())) {
					this.transazioneApplicativoServer.setUltimoErrore(esitoLib.getMotivazioneErroreNonGestito());
				}
				else if(esitoLib.getErroreNonGestito()!=null) {
					Throwable e = Utilities.getInnerNotEmptyMessageException(esitoLib.getErroreNonGestito());
					if(e!=null) {
						this.transazioneApplicativoServer.setUltimoErrore(e.getMessage());
					}
					else {
						this.transazioneApplicativoServer.setUltimoErrore(esitoLib.getErroreNonGestito().toString());
					}
				}
				else {
					this.transazioneApplicativoServer.setUltimoErrore("Errore Generico durante il processamento del messaggio");
				}
			}
			
			try {
				GestoreConsegnaMultipla.getInstance().safeUpdateConsegna(this.transazioneApplicativoServer, idApplicativa, this.openspcoopstate);
			}catch(Throwable t) {
				this.log.error("["+this.transazioneApplicativoServer.getIdTransazione()+"]["+this.transazioneApplicativoServer.getServizioApplicativoErogatore()+"] Errore durante il salvataggio delle informazioni relative al servizio applicativo: "+t.getMessage(),t);
			}
		}
		
		return esitoLib;
	}
	
	private EsitoLib engine_onMessage(IOpenSPCoopState openspcoopstate,
			RegistroServiziManager registroServiziManager,ConfigurazionePdDManager configurazionePdDManager, 
			MsgDiagnostico msgDiag,
			TransazioneApplicativoServer transazioneApplicativoServer,
			Date oraRegistrazione) throws OpenSPCoopStateException {

		this.dataConsegna = DateManager.getDate();
		
		EsitoLib esito = new EsitoLib();
		this.consegnaContenutiApplicativiMsg = (ConsegnaContenutiApplicativiMessage) this.openspcoopstate.getMessageLib();
		
		ConsegnaContenutiApplicativiBehaviourMessage behaviourConsegna = this.consegnaContenutiApplicativiMsg.getBehaviour();
		BehaviourForwardToConfiguration behaviourForwardToConfiguration = null;
		GestioneErrore gestioneErroreBehaviour = null;
		if(behaviourConsegna!=null) {
			this.idMessaggioPreBehaviour = behaviourConsegna.getIdMessaggioPreBehaviour();
			behaviourForwardToConfiguration = behaviourConsegna.getBehaviourForwardToConfiguration();
			gestioneErroreBehaviour = behaviourConsegna.getGestioneErrore();
		}
		
		/* PddContext */
		this.pddContext = this.consegnaContenutiApplicativiMsg.getPddContext();
		this.idTransazione = PdDContext.getValue(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE, this.pddContext);
				
		/* Protocol Factory */
		try{
			this.protocolFactory = this.protocolFactoryManager.getProtocolFactoryByName((String) this.pddContext.getObject(org.openspcoop2.core.constants.Costanti.PROTOCOL_NAME));
		}catch(Exception e){
			this.msgDiag.logErroreGenerico(e, "ProtocolFactory.instanziazione"); 
			this.openspcoopstate.releaseResource();
			esito.setEsitoInvocazione(false); 
			esito.setStatoInvocazioneErroreNonGestito(e);
			return esito;
		}
		
		// Transaction
		try{
			this.transactionNullable = TransactionContext.getTransaction(this.idTransazione);
		}catch(Exception e){
			// La transazione potrebbe essere stata eliminata nelle comunicazioni stateful
//			this.msgDiag.logErroreGenerico(e, "getTransaction"); 
//			this.openspcoopstate.releaseResource();
//			esito.setEsitoInvocazione(false); 
//			esito.setStatoInvocazioneErroreNonGestito(e);
//			return esito;
		}
		
		this.msgDiag.setPddContext(this.pddContext, this.protocolFactory);
		/* ID e tipo di implementazione PdD con cui interoperare */
		this.idMessaggioConsegna = this.openspcoopstate.getIDMessaggioSessione();
		this.implementazionePdDMittente = this.consegnaContenutiApplicativiMsg.getImplementazionePdDSoggettoMittente();
		this.implementazionePdDDestinatario = this.consegnaContenutiApplicativiMsg.getImplementazionePdDSoggettoDestinatario();

		this.richiestaApplicativa = this.consegnaContenutiApplicativiMsg.getRichiestaApplicativa();
		this.richiestaDelegata = this.consegnaContenutiApplicativiMsg.getRichiestaDelegata();
		this.bustaRichiesta = this.consegnaContenutiApplicativiMsg.getBusta(); // in caso di richiesta delegata serve per il profilo asincrono

		this.tipoPdD = TipoPdD.APPLICATIVA;
		if(this.msgDiag.getPorta()==null) {
			if(this.richiestaApplicativa!=null && this.richiestaApplicativa.getIdPortaApplicativa()!=null) {
				this.msgDiag.updatePorta(this.tipoPdD, this.richiestaApplicativa.getIdPortaApplicativa().getNome());
			}
			else if(this.richiestaDelegata!=null && this.richiestaDelegata.getIdPortaDelegata()!=null) {
				this.msgDiag.updatePorta(TipoPdD.DELEGATA, this.richiestaDelegata.getIdPortaDelegata().getNome());
			}
		}
		
		this.msgDiag.setPrefixMsgPersonalizzati(MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI);
		if(this.bustaRichiesta!=null){
			this.msgDiag.addKeywords(this.bustaRichiesta, true);
		}
		String descrizioneBehaviour = "";
		if(this.bustaRichiesta!=null){
			descrizioneBehaviour = this.bustaRichiesta.removeProperty(CostantiPdD.KEY_DESCRIZIONE_BEHAVIOUR);
			if(descrizioneBehaviour==null){
				descrizioneBehaviour = "";
			}
			if(!"".equals(descrizioneBehaviour)){
				descrizioneBehaviour = 	" ("+descrizioneBehaviour+")";
			}
		}
		this.msgDiag.addKeyword(CostantiPdD.KEY_DESCRIZIONE_BEHAVIOUR, descrizioneBehaviour);
		
		// VM ProtocolInfo (se siamo arrivati da un canale VM)
		// Per il caso di LocalForward (se si mettera il tracciamento)
		if(this.pddContext!=null && this.bustaRichiesta!=null)
			DirectVMProtocolInfo.setInfoFromContext(this.pddContext, this.bustaRichiesta);
		
		// Dati per GestoreEventi
		String servizio = null;
		String tipoServizio = null;
		String azione = null;
		if(this.bustaRichiesta!=null){
			servizio = this.bustaRichiesta.getServizio();
			tipoServizio = this.bustaRichiesta.getTipoServizio();
			azione = this.bustaRichiesta.getAzione(); // in caso di richiesta delegata serve per il profilo asincrono
		}
		this.proprietaManifestAttachments = this.propertiesReader.getProprietaManifestAttachments(this.implementazionePdDMittente);
		
		IDSoggetto soggettoErogatoreServizioHeaderIntegrazione = null;
		IDSoggetto soggettoFruitoreHeaderIntegrazione = null;
		
		ResponseCachingConfigurazione responseCachingConfig = null;
		
		if(this.richiestaApplicativa!=null){
			this.identitaPdD = this.richiestaApplicativa.getDominio();
			this.soggettoFruitore = this.richiestaApplicativa.getSoggettoFruitore();
			this.servizioApplicativo = this.richiestaApplicativa.getServizioApplicativo();
			this.idModuloInAttesa = this.richiestaApplicativa.getIdModuloInAttesa();
			this.scenarioCooperazione = this.richiestaApplicativa.getScenario();
			this.isRicevutaAsincrona = this.richiestaApplicativa.isRicevutaAsincrona();
			this.idServizio = this.richiestaApplicativa.getIDServizio();
			if(this.bustaRichiesta!=null){
				if(this.idServizio.getSoggettoErogatore()!=null){
					if(this.idServizio.getSoggettoErogatore().getCodicePorta()==null){
						this.idServizio.getSoggettoErogatore().setCodicePorta(this.bustaRichiesta.getIdentificativoPortaDestinatario());
					}
				}
			}
			if(azione!=null && !"".equals(azione) && this.idServizio!=null && 
					(this.idServizio.getAzione()==null || "".equals(this.idServizio.getAzione()))) {
				this.idServizio.setAzione(azione);
			}
			this.idAccordoServizio = this.richiestaApplicativa.getIdAccordo();
			soggettoErogatoreServizioHeaderIntegrazione = this.idServizio.getSoggettoErogatore();
			this.profiloGestione = this.richiestaApplicativa.getProfiloGestione();
			this.servizioApplicativoFruitore = this.richiestaApplicativa.getIdentitaServizioApplicativoFruitore();
			this.idCorrelazioneApplicativa = this.richiestaApplicativa.getIdCorrelazioneApplicativa();

			this.localForward = this.richiestaApplicativa.isLocalForward();
			
		}else{
			this.identitaPdD = this.richiestaDelegata.getDominio();
			this.soggettoFruitore = this.richiestaDelegata.getIdSoggettoFruitore();
			this.servizioApplicativo = this.richiestaDelegata.getServizioApplicativo();
			this.idModuloInAttesa = this.richiestaDelegata.getIdModuloInAttesa();
			this.scenarioCooperazione = this.richiestaDelegata.getScenario();
			this.isRicevutaAsincrona = this.richiestaDelegata.isRicevutaAsincrona();
			this.idServizio = this.richiestaDelegata.getIdServizio();
			if(this.bustaRichiesta!=null){
				if(this.idServizio.getSoggettoErogatore()!=null){
					if(this.idServizio.getSoggettoErogatore().getCodicePorta()==null){
						this.idServizio.getSoggettoErogatore().setCodicePorta(this.bustaRichiesta.getIdentificativoPortaDestinatario());
					}
				}
			}
			if(azione!=null && !"".equals(azione) && this.idServizio!=null && 
					(this.idServizio.getAzione()==null || "".equals(this.idServizio.getAzione()))) {
				this.idServizio.setAzione(azione);
			}
			this.idAccordoServizio = this.richiestaDelegata.getIdAccordo();
			if ( this.bustaRichiesta!=null && Costanti.SCENARIO_ASINCRONO_SIMMETRICO_CONSEGNA_RISPOSTA.equals(this.scenarioCooperazione) ){
				soggettoErogatoreServizioHeaderIntegrazione = new IDSoggetto(this.bustaRichiesta.getTipoDestinatario(),this.bustaRichiesta.getDestinatario());
				soggettoFruitoreHeaderIntegrazione = new IDSoggetto(this.bustaRichiesta.getTipoMittente(),this.bustaRichiesta.getMittente(), this.bustaRichiesta.getIdentificativoPortaMittente());
			}else{
				soggettoErogatoreServizioHeaderIntegrazione = this.idServizio.getSoggettoErogatore();
			}
			this.profiloGestione = this.richiestaDelegata.getProfiloGestione();
			this.servizioApplicativoFruitore = this.richiestaDelegata.getServizioApplicativo();
			this.idCorrelazioneApplicativa = this.richiestaDelegata.getIdCorrelazioneApplicativa();
		}
		
		if(this.bustaRichiesta!=null){
			// Per ricambiare il servizio in correlato per:
			// - AsincronoAsimmetrico, richiestaStato
			// - AsincronoSimmetrico, risposta
			try{
				this.servizioHeaderIntegrazione = IDServizioFactory.getInstance().getIDServizioFromValues(this.bustaRichiesta.getTipoServizio(), this.bustaRichiesta.getServizio(), 
						soggettoErogatoreServizioHeaderIntegrazione, this.bustaRichiesta.getVersioneServizio());
				this.servizioHeaderIntegrazione.setAzione(this.bustaRichiesta.getAzione());
			}catch(Exception e){
				this.msgDiag.logErroreGenerico(e, "RichiestaApplicativa.getIDServizioFromValues");
				esito.setEsitoInvocazione(false); 
				esito.setStatoInvocazioneErroreNonGestito(e);
				return esito;
			}
		}	
		
		this.msgDiag.mediumDebug("Profilo di gestione ["+ConsegnaContenutiApplicativi.ID_MODULO+"] della busta: "+this.profiloGestione);
		this.msgDiag.setDominio(this.identitaPdD);  // imposto anche il dominio nel msgDiag
		this.msgDiag.setIdCorrelazioneApplicativa(this.idCorrelazioneApplicativa);
		this.msgDiag.addKeyword(CostantiPdD.KEY_SA_EROGATORE, this.servizioApplicativo);
		if(this.servizioApplicativoFruitore!=null){
			this.msgDiag.addKeyword(CostantiPdD.KEY_SA_FRUITORE, this.servizioApplicativoFruitore);
		}
		boolean soggettoVirtuale = false;
		try{
			soggettoVirtuale = this.configurazionePdDManager.isSoggettoVirtuale( this.identitaPdD );
		}catch(Exception e){
			this.msgDiag.logErroreGenerico(e, "isSoggettoVirtuale("+this.identitaPdD+")");
			esito.setEsitoInvocazione(false); 
			esito.setStatoInvocazioneErroreNonGestito(e);
			return esito;
		}
		
		// Aggiornamento Informazioni
		this.msgDiag.setIdMessaggioRichiesta(this.idMessaggioConsegna);
		if(this.idMessaggioPreBehaviour!=null){
			this.msgDiag.setIdMessaggioRichiesta(this.idMessaggioPreBehaviour);
			this.msgDiag.addKeyword(CostantiPdD.KEY_ID_MESSAGGIO_RICHIESTA, this.idMessaggioPreBehaviour);
		}
		if(soggettoFruitoreHeaderIntegrazione!=null)
			this.msgDiag.setFruitore(soggettoFruitoreHeaderIntegrazione);
		else
			this.msgDiag.setFruitore(this.soggettoFruitore);
		if(this.servizioHeaderIntegrazione!=null){
			this.msgDiag.setServizio(this.servizioHeaderIntegrazione);
		}else{
			this.msgDiag.setServizio(this.idServizio);
		}
		if(transazioneApplicativoServer!=null) {
			this.msgDiag.setServizioApplicativo(this.servizioApplicativo);
		}

		// Calcolo Profilo di Collaborazione
		this.msgDiag.mediumDebug("Calcolo profilo di collaborazione...");
		this.profiloCollaborazione = EJBUtils.calcolaProfiloCollaborazione(this.scenarioCooperazione);


		if(this.richiestaApplicativa!=null){
			this.idPA = this.richiestaApplicativa.getIdPortaApplicativa();
			try{
				this.msgDiag.mediumDebug("getPortaApplicativa...");
				this.pa = this.configurazionePdDManager.getPortaApplicativa(this.idPA);
			}catch(Exception e){
				this.msgDiag.logErroreGenerico(e, "RichiestaApplicativa.getPortaApplicativa");
				esito.setEsitoInvocazione(false); 
				esito.setStatoInvocazioneErroreNonGestito(e);
				return esito;
			}
			try{
				this.msgDiag.mediumDebug("getServizioApplicativo(pa)...");
				IDServizioApplicativo idSA = new IDServizioApplicativo();
				idSA.setNome(this.richiestaApplicativa.getServizioApplicativo());
				idSA.setIdSoggettoProprietario(this.richiestaApplicativa.getIDServizio().getSoggettoErogatore());
				this.sa = this.configurazionePdDManager.getServizioApplicativo(idSA);
			}catch(Exception e){
				this.msgDiag.logErroreGenerico(e, "RichiestaApplicativa.getServizioApplicativo");
				esito.setEsitoInvocazione(false); 
				esito.setStatoInvocazioneErroreNonGestito(e);
				return esito;
			}
			if(this.pa!=null)
				this.correlazioneApplicativaRisposta = this.pa.getCorrelazioneApplicativaRisposta();
		}else{
			this.idPD = this.richiestaDelegata.getIdPortaDelegata();
			try{
				this.msgDiag.mediumDebug("getPortaDelegata...");
				this.pd = this.configurazionePdDManager.getPortaDelegata(this.idPD);
			}catch(Exception e){
				this.msgDiag.logErroreGenerico(e, "RichiestaDelegata.getPortaApplicativa");
				esito.setEsitoInvocazione(false); 
				esito.setStatoInvocazioneErroreNonGestito(e);
				return esito;
			}
			try{
				this.msgDiag.mediumDebug("getServizioApplicativo(pd)...");
				IDServizioApplicativo idSA = new IDServizioApplicativo();
				idSA.setNome(this.richiestaDelegata.getServizioApplicativo());
				idSA.setIdSoggettoProprietario(this.richiestaDelegata.getIdSoggettoFruitore());
				this.sa = this.configurazionePdDManager.getServizioApplicativo(idSA);
			}catch(Exception e){
				this.msgDiag.logErroreGenerico(e, "RichiestaDelegata.getServizioApplicativo");
				esito.setEsitoInvocazione(false); 
				esito.setStatoInvocazioneErroreNonGestito(e);
				return esito;
			}
			if(this.pd!=null)
				this.correlazioneApplicativaRisposta = this.pd.getCorrelazioneApplicativaRisposta();
		}



		// Recupero anche pd in caso di local forward
		if(this.localForward && this.pd==null){
			try{
				this.msgDiag.mediumDebug("getPortaDelegata...");
				this.pd = this.configurazionePdDManager.getPortaDelegata(this.richiestaDelegata.getIdPortaDelegata());
			}catch(Exception e){
				this.msgDiag.logErroreGenerico(e, "RichiestaDelegata.getPortaApplicativa");
				esito.setEsitoInvocazione(false); 
				esito.setStatoInvocazioneErroreNonGestito(e);
				return esito;
			}
		}
		


		// La lettura dalla configurazione deve essere dopo il transaction manager
		if(this.richiestaApplicativa!=null && !this.localForward){
			try{
				this.msgDiag.mediumDebug("isAllegaBody(pa)...");
				this.allegaBody = this.configurazionePdDManager.isAllegaBody(this.pa);
			}catch(Exception e){
				this.msgDiag.logErroreGenerico(e, "RichiestaApplicativa.isAllegaBody");
				esito.setEsitoInvocazione(false); 
				esito.setStatoInvocazioneErroreNonGestito(e);
				return esito;
			}	
			try{
				this.msgDiag.mediumDebug("isScartaBody(pa)...");
				this.scartaBody = this.configurazionePdDManager.isScartaBody(this.pa);
			}catch(Exception e){
				this.msgDiag.logErroreGenerico(e, "RichiestaApplicativa.isScartaBody");
				esito.setEsitoInvocazione(false); 
				esito.setStatoInvocazioneErroreNonGestito(e);
				return esito;
			}	
			try{
				this.msgDiag.mediumDebug("getTipiIntegrazione(pa)...");
				this.tipiIntegrazione = this.configurazionePdDManager.getTipiIntegrazione(this.pa);
			}catch(Exception e){
				this.msgDiag.logErroreGenerico(e, "RichiestaApplicativa.getTipiIntegrazione");
				esito.setEsitoInvocazione(false); 
				esito.setStatoInvocazioneErroreNonGestito(e);
				return esito;
			}
			try{
				this.msgDiag.mediumDebug("isModalitaStateless(pa)...");
				this.portaDiTipoStateless = this.configurazionePdDManager.isModalitaStateless(this.pa, this.profiloCollaborazione); 
			}catch(Exception e){
				this.msgDiag.logErroreGenerico(e, "RichiestaApplicativa.isModalitaStateless("+this.profiloCollaborazione+")");
				esito.setEsitoInvocazione(false); 
				esito.setStatoInvocazioneErroreNonGestito(e);
				return esito;
			}
			try{
				this.msgDiag.mediumDebug("isGestioneManifestAttachments(pa)...");
				this.gestioneManifest = this.configurazionePdDManager.isGestioneManifestAttachments(this.pa,this.protocolFactory); 
			}catch(Exception e){
				this.msgDiag.logErroreGenerico(e, "isGestioneManifestAttachments(pa)");
				esito.setEsitoInvocazione(false); 
				esito.setStatoInvocazioneErroreNonGestito(e);
				return esito;
			}
			try{
				this.msgDiag.mediumDebug("readDumpConfig(pa)...");
				this.dumpConfig = this.configurazionePdDManager.getDumpConfigurazione(this.pa);
			}catch(Exception e){
				this.msgDiag.logErroreGenerico(e, "readDumpConfig(pa)");
				esito.setEsitoInvocazione(false); 
				esito.setStatoInvocazioneErroreNonGestito(e);
				return esito;
			}
			try{
				this.msgDiag.mediumDebug("readResponseCachingConfig(pa)...");
				responseCachingConfig = this.configurazionePdDManager.getConfigurazioneResponseCaching(this.pa);
			}catch(Exception e){
				this.msgDiag.logErroreGenerico(e, "readResponseCachingConfig(pa)");
				esito.setEsitoInvocazione(false); 
				esito.setStatoInvocazioneErroreNonGestito(e);
				return esito;
			}
			try{
				this.msgDiag.mediumDebug("readTrasformazioni(pa)...");
				this.trasformazioni = this.configurazionePdDManager.getTrasformazioni(this.pa);
			}catch(Exception e){
				this.msgDiag.logErroreGenerico(e, "readTrasformazioni(pa)");
				esito.setEsitoInvocazione(false); 
				esito.setStatoInvocazioneErroreNonGestito(e);
				return esito;
			}
			
		}else{
			try{
				this.msgDiag.mediumDebug("isAllegaBody(pd)...");
				this.allegaBody = this.configurazionePdDManager.isAllegaBody(this.pd);
			}catch(Exception e){
				this.msgDiag.logErroreGenerico(e, "RichiestaDelegata.isAllegaBody");
				esito.setEsitoInvocazione(false); 
				esito.setStatoInvocazioneErroreNonGestito(e);
				return esito;
			}	
			try{
				this.msgDiag.mediumDebug("isScartaBody(pd)...");
				this.scartaBody = this.configurazionePdDManager.isScartaBody(this.pd);
			}catch(Exception e){
				this.msgDiag.logErroreGenerico(e, "RichiestaDelegata.isScartaBody");
				esito.setEsitoInvocazione(false); 
				esito.setStatoInvocazioneErroreNonGestito(e);
				return esito;
			}	
			try{
				this.msgDiag.mediumDebug("getTipiIntegrazione(pd)...");
				this.tipiIntegrazione = this.configurazionePdDManager.getTipiIntegrazione(this.pd);
			}catch(Exception e){
				this.msgDiag.logErroreGenerico(e, "RichiestaDelegata.getTipiIntegrazione");
				esito.setEsitoInvocazione(false); 
				esito.setStatoInvocazioneErroreNonGestito(e);
				return esito;
			}
			try{
				this.msgDiag.mediumDebug("isModalitaStateless(pd)...");
				this.portaDiTipoStateless = this.configurazionePdDManager.isModalitaStateless(this.pd, this.profiloCollaborazione); 
			}catch(Exception e){
				this.msgDiag.logErroreGenerico(e, "RichiestaDelegata.isModalitaStateless("+this.profiloCollaborazione+")");
				esito.setEsitoInvocazione(false); 
				esito.setStatoInvocazioneErroreNonGestito(e);
				return esito;
			}
			try{
				this.msgDiag.mediumDebug("isGestioneManifestAttachments(pd)...");
				this.gestioneManifest = this.configurazionePdDManager.isGestioneManifestAttachments(this.pd,this.protocolFactory); 
			}catch(Exception e){
				this.msgDiag.logErroreGenerico(e, "isGestioneManifestAttachments(pd)");
				esito.setEsitoInvocazione(false); 
				esito.setStatoInvocazioneErroreNonGestito(e);
				return esito;
			}
			try{
				this.msgDiag.mediumDebug("readDumpConfig(pd)...");
				this.dumpConfig = this.configurazionePdDManager.getDumpConfigurazione(this.pd);
			}catch(Exception e){
				this.msgDiag.logErroreGenerico(e, "readDumpConfig(pd)");
				esito.setEsitoInvocazione(false); 
				esito.setStatoInvocazioneErroreNonGestito(e);
				return esito;
			}
			try{
				this.msgDiag.mediumDebug("readResponseCachingConfig(pd)...");
				responseCachingConfig = this.configurazionePdDManager.getConfigurazioneResponseCaching(this.pd);
			}catch(Exception e){
				this.msgDiag.logErroreGenerico(e, "readResponseCachingConfig(pd)");
				esito.setEsitoInvocazione(false); 
				esito.setStatoInvocazioneErroreNonGestito(e);
				return esito;
			}
			try{
				this.msgDiag.mediumDebug("readTrasformazioni(pd)...");
				this.trasformazioni = this.configurazionePdDManager.getTrasformazioni(this.pd);
			}catch(Exception e){
				this.msgDiag.logErroreGenerico(e, "readTrasformazioni(pd)");
				esito.setEsitoInvocazione(false); 
				esito.setStatoInvocazioneErroreNonGestito(e);
				return esito;
			}
		}

		if(this.idMessaggioPreBehaviour!=null) {
			this.portaDiTipoStateless = false;
		}
		else if(soggettoVirtuale) {
			this.portaDiTipoStateless = false;
		}
		this.pddContext.addObject(org.openspcoop2.core.constants.Costanti.STATELESS, this.portaDiTipoStateless+"");


		
		LocalForwardParameter localForwardParameter = null;
		try{
			
			if(this.localForward){
				
				localForwardParameter = new LocalForwardParameter();
				localForwardParameter.setLog(this.log);
				localForwardParameter.setConfigurazionePdDReader(configurazionePdDManager);
				localForwardParameter.setIdCorrelazioneApplicativa(this.idCorrelazioneApplicativa);
				localForwardParameter.setIdentitaPdD(this.identitaPdD);
				localForwardParameter.setIdModulo(this.idModulo);
				localForwardParameter.setIdRequest(this.idMessaggioConsegna);
				localForwardParameter.setImplementazionePdDDestinatario(this.implementazionePdDDestinatario);
				localForwardParameter.setImplementazionePdDMittente(this.implementazionePdDMittente);
				localForwardParameter.setIdPdDMittente(this.registroServiziManager.getIdPortaDominio(this.soggettoFruitore, null));
				localForwardParameter.setIdPdDDestinatario(this.registroServiziManager.getIdPortaDominio(this.idServizio.getSoggettoErogatore(), null));
				localForwardParameter.setMsgDiag(msgDiag);
				localForwardParameter.setOpenspcoopstate(openspcoopstate);
				localForwardParameter.setPddContext(this.pddContext);
				localForwardParameter.setProtocolFactory(this.protocolFactory);
				localForwardParameter.setRichiestaDelegata(this.richiestaDelegata);
				localForwardParameter.setRichiestaApplicativa(this.richiestaApplicativa);
				localForwardParameter.setStateless(this.portaDiTipoStateless);
				localForwardParameter.setBusta(this.bustaRichiesta);
				
				this.localForwardEngine = new LocalForwardEngine(localForwardParameter);
			}
			
		}catch(Exception e){
			this.msgDiag.logErroreGenerico(e, "LocalForwardEngine.init");
			esito.setEsitoInvocazione(false); 
			esito.setStatoInvocazioneErroreNonGestito(e);
			return esito;
		}
		
		
		


		/* ------------------ Connessione al DB  --------------- */
		this.msgDiag.mediumDebug("Richiesta connessione al database per la gestione della richiesta...");
		this.openspcoopstate.initResource(this.identitaPdD, ConsegnaContenutiApplicativi.ID_MODULO,this.idTransazione);
		this.registroServiziManager.updateState(this.openspcoopstate.getStatoRichiesta(),this.openspcoopstate.getStatoRisposta());
		this.configurazionePdDManager.updateState(this.openspcoopstate.getStatoRichiesta(),this.openspcoopstate.getStatoRisposta());
		this.msgDiag.updateState(this.openspcoopstate.getStatoRichiesta(),this.openspcoopstate.getStatoRisposta());
		


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
		if(this.idMessaggioPreBehaviour!=null){
			this.existsModuloInAttesaRispostaApplicativa = false;
		}
		else if(Costanti.SCENARIO_SINCRONO_INVOCAZIONE_SERVIZIO.equals(this.scenarioCooperazione) ){
			this.existsModuloInAttesaRispostaApplicativa = true;
		}
		else if(Costanti.SCENARIO_ASINCRONO_ASIMMETRICO_POLLING.equals(this.scenarioCooperazione)){
			this.existsModuloInAttesaRispostaApplicativa = true;
		}
		else if (Costanti.SCENARIO_ASINCRONO_ASIMMETRICO_INVOCAZIONE_SERVIZIO.equals(this.scenarioCooperazione) ||
				Costanti.SCENARIO_ASINCRONO_SIMMETRICO_INVOCAZIONE_SERVIZIO.equals(this.scenarioCooperazione) ||
				Costanti.SCENARIO_ASINCRONO_SIMMETRICO_CONSEGNA_RISPOSTA.equals(this.scenarioCooperazione)){
			this.existsModuloInAttesaRispostaApplicativa = this.portaDiTipoStateless || this.isRicevutaAsincrona;
		}
		else if( (this.richiestaApplicativa!=null) && Costanti.SCENARIO_ONEWAY_INVOCAZIONE_SERVIZIO.equals(this.richiestaApplicativa.getScenario()) ){
			this.existsModuloInAttesaRispostaApplicativa = this.portaDiTipoStateless;
		}



		/* ------------------ Inizializzazione Contesto di gestione della Richiesta --------------- */
		this.msgDiag.mediumDebug("Inizializzo contesto per la gestione...");
		// EJBUtils (per eventuali errori)
		try{
			String idMessaggioGestoreMessaggiRichiesta = this.idMessaggioConsegna;
			if(this.idMessaggioPreBehaviour!=null){
				idMessaggioGestoreMessaggiRichiesta = this.bustaRichiesta.getID();
			}
			this.ejbUtils = new EJBUtils(this.identitaPdD,this.tipoPdD,ConsegnaContenutiApplicativi.ID_MODULO,this.idMessaggioConsegna,
					idMessaggioGestoreMessaggiRichiesta,Costanti.INBOX,openspcoopstate,msgDiag,false,
					this.consegnaContenutiApplicativiMsg.getImplementazionePdDSoggettoMittente(),
					this.consegnaContenutiApplicativiMsg.getImplementazionePdDSoggettoDestinatario(),
					this.profiloGestione,this.pddContext
			);
			this.ejbUtils.setServizioApplicativoErogatore(this.servizioApplicativo);
		}catch(Exception e){
			this.msgDiag.logErroreGenerico(e, "EJBUtils.new");
			this.openspcoopstate.releaseResource();
			esito.setEsitoInvocazione(false); 
			esito.setStatoInvocazioneErroreNonGestito(e);

			return esito;
		}
		
		// GestoriMessaggio
		this.msgDiag.mediumDebug("Inizializzo contesto per la gestione (GestoreMessaggio)...");
		String idMessaggioGestoreMessaggiRichiesta = this.idMessaggioConsegna;
		if(this.idMessaggioPreBehaviour!=null){
			idMessaggioGestoreMessaggiRichiesta = this.bustaRichiesta.getID();
		}
		this.msgRequest = new GestoreMessaggi(openspcoopstate, true, idMessaggioGestoreMessaggiRichiesta,Costanti.INBOX,msgDiag,this.pddContext);
		this.msgRequest.setPortaDiTipoStateless(this.portaDiTipoStateless);
		
		// RequestInfo
		this.requestInfo = (RequestInfo) this.pddContext.getObject(org.openspcoop2.core.constants.Costanti.REQUEST_INFO);
		if(this.requestInfo==null || this.idTransazione==null) {
			// devo leggerlo dal messaggio
			try {
				this.consegnaMessagePrimaTrasformazione = this.msgRequest.getMessage(oraRegistrazione);
				correctForwardPathNotifiche(transazioneApplicativoServer, this.consegnaMessagePrimaTrasformazione, this.protocolFactory);
				if(this.requestInfo==null) {
					Object o = this.consegnaMessagePrimaTrasformazione.getContextProperty(org.openspcoop2.core.constants.Costanti.REQUEST_INFO);
					if(o==null) {
						throw new Exception("RequestInfo non presente nel contesto");
					}
					this.requestInfo = (RequestInfo) o;
					this.pddContext.addObject(org.openspcoop2.core.constants.Costanti.REQUEST_INFO,this.requestInfo);
				}
				if(this.idTransazione==null) {
					Object o = this.consegnaMessagePrimaTrasformazione.getContextProperty(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE);
					if(o==null) {
						throw new Exception("IdTransazione non presente nel contesto");
					}
					this.idTransazione = (String) o;
					this.pddContext.addObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE,this.idTransazione);
				}
			}catch(Exception e) {
				this.msgDiag.logErroreGenerico(e, "LetturaMessaggioErrore (Recupero Dati)"); 
				this.openspcoopstate.releaseResource();
				esito.setEsitoInvocazione(false); 
				esito.setStatoInvocazioneErroreNonGestito(e);
				return esito;
			}
		}
		
		RicezioneBusteExternalErrorGenerator generatoreErrorePA = null;
		try{
			generatoreErrorePA = new RicezioneBusteExternalErrorGenerator(this.log,
					this.idModulo, this.requestInfo, this.openspcoopstate.getStatoRichiesta());
			generatoreErrorePA.updateInformazioniCooperazione(this.soggettoFruitore, this.idServizio);
			generatoreErrorePA.updateInformazioniCooperazione(this.servizioApplicativoFruitore);
			generatoreErrorePA.updateTipoPdD(TipoPdD.APPLICATIVA);
			this.ejbUtils.setGeneratoreErrorePortaApplicativa(generatoreErrorePA);
		}catch(Exception e){
			this.msgDiag.logErroreGenerico(e, "RicezioneBusteExternalErrorGenerator.instanziazione"); 
			this.openspcoopstate.releaseResource();
			esito.setEsitoInvocazione(false); 
			esito.setStatoInvocazioneErroreNonGestito(e);
			return esito;
		}

		// Oneway versione 11
		this.oneWayVersione11 = this.consegnaContenutiApplicativiMsg.isOneWayVersione11();
		this.ejbUtils.setOneWayVersione11(this.oneWayVersione11);
		if(this.idMessaggioPreBehaviour!=null){
			this.ejbUtils.setOneWayVersione11(true); // per forzare l'update su db	
		}
		this.msgRequest.setOneWayVersione11(this.oneWayVersione11);







		try{
			if(this.msgRequest.isRiconsegnaMessaggio(this.servizioApplicativo) == false){
				this.openspcoopstate.releaseResource();
				// Per i profili 'sincroni' dove vi e' un modulo in attesa non puo' sussistere una riconsegna del messaggio.
				if(this.existsModuloInAttesaRispostaApplicativa==false){
					this.msgDiag.logPersonalizzato("riconsegnaMessaggioPrematura");
					esito.setEsitoInvocazione(false); 
					esito.setStatoInvocazione(EsitoLib.ERRORE_NON_GESTITO,this.msgDiag.getMessaggio_replaceKeywords("riconsegnaMessaggioPrematura"));
				}else{
					String message = null;
					String posizione = null;
					if( (this.richiestaApplicativa!=null) && Costanti.SCENARIO_ONEWAY_INVOCAZIONE_SERVIZIO.equals(this.richiestaApplicativa.getScenario())){
						message = "Messaggio eliminato durante il controllo di ri-consegna ("+this.servizioApplicativo+","+this.scenarioCooperazione+")";
						posizione = "this.msgRequest.isRiconsegnaMessaggio("+this.servizioApplicativo+","+this.scenarioCooperazione+")";
					}else{
						message = "Messaggio eliminato durante il controllo di ri-consegna ("+this.servizioApplicativo+","+this.scenarioCooperazione+",STATELESS)";
						posizione = "this.msgRequest.isRiconsegnaMessaggio("+this.servizioApplicativo+","+this.scenarioCooperazione+",STATELESS)";
					}
					this.msgDiag.logErroreGenerico(message,posizione);
					esito.setEsitoInvocazione(true); 
					esito.setStatoInvocazione(EsitoLib.ERRORE_NON_GESTITO,message);
				}
				this.log.info(ConsegnaContenutiApplicativi.ID_MODULO + "Riconsegna messaggio prematura");

				return esito;
			}
		}catch(Exception e){
			this.msgDiag.logErroreGenerico(e, "this.msgRequest.isRiconsegnaMessaggio("+this.servizioApplicativo+")");
			this.ejbUtils.rollbackMessage("Errore verifica riconsegna messaggio", this.servizioApplicativo, esito);
			this.openspcoopstate.releaseResource();
			esito.setEsitoInvocazione(false); 
			esito.setStatoInvocazioneErroreNonGestito(e);

			return esito;
		}

		// Consegna da effettuare
		this.msgDiag.mediumDebug("Inizializzo contesto per la gestione (Consegna)...");
		boolean integrationManager = false;
		if(Costanti.SCENARIO_CONSEGNA_CONTENUTI_APPLICATIVI.equals(this.scenarioCooperazione) ||
				Costanti.SCENARIO_ASINCRONO_SIMMETRICO_CONSEGNA_RISPOSTA.equals(this.scenarioCooperazione) ){
			try{
				this.msgDiag.mediumDebug("Inizializzo contesto per la gestione (getConsegnaRispostaAsincrona) [ConsegnaContenuti/AsincronoSimmetricoRisposta]...");
				this.connettoreMsg = this.configurazionePdDManager.getConsegnaRispostaAsincrona(this.sa,this.richiestaDelegata);
				if(Costanti.SCENARIO_CONSEGNA_CONTENUTI_APPLICATIVI.equals(this.scenarioCooperazione)){
					this.connettoreMsg.setCheckPresenzaHeaderPrimaSbustamento(true);
				}
				if(this.connettoreMsg!=null){
					this.connettoreMsg.initPolicyGestioneToken(configurazionePdDManager);
				}
				this.msgDiag.mediumDebug("Inizializzo contesto per la gestione (consegnaRispostaAsincronaConGetMessage) [ConsegnaContenuti/AsincronoSimmetricoRisposta]...");
				integrationManager = this.configurazionePdDManager.consegnaRispostaAsincronaConGetMessage(this.sa);
				this.msgDiag.mediumDebug("Inizializzo contesto per la gestione (getTipoValidazioneContenutoApplicativo) [ConsegnaContenuti/AsincronoSimmetricoRisposta]...");
				this.validazioneContenutoApplicativoApplicativo = this.configurazionePdDManager.getTipoValidazioneContenutoApplicativo(this.pd,this.implementazionePdDMittente, false);
				this.proprietaValidazioneContenutoApplicativoApplicativo = this.pd.getProprietaList();
				this.msgDiag.mediumDebug("Inizializzo contesto per la gestione (consegnaRispostaAsincronaPerRiferimento) [ConsegnaContenuti/AsincronoSimmetricoRisposta]...");
				this.consegnaPerRiferimento = this.configurazionePdDManager.consegnaRispostaAsincronaPerRiferimento(this.sa);
				this.msgDiag.mediumDebug("Inizializzo contesto per la gestione (consegnaRispostaAsincronaRispostaPerRiferimento) [ConsegnaContenuti/AsincronoSimmetricoRisposta]...");
				this.rispostaPerRiferimento = this.configurazionePdDManager.consegnaRispostaAsincronaRispostaPerRiferimento(this.sa);
			}catch(Exception e){
				this.msgDiag.logErroreGenerico(e, "ConsegnaAsincrona.getDatiConsegna(sa:"+this.servizioApplicativo+")");
				this.ejbUtils.rollbackMessage("[ConsegnaAsincrona] Connettore per consegna applicativa non definito:"+e.getMessage(),this.servizioApplicativo, esito);
				esito.setEsitoInvocazione(false); 
				esito.setStatoInvocazioneErroreNonGestito(e);
				return esito;
			}


		}
		else if(Costanti.SCENARIO_ASINCRONO_ASIMMETRICO_POLLING.equals(this.scenarioCooperazione)){
			try{
				this.msgDiag.mediumDebug("Inizializzo contesto per la gestione (getConsegnaRispostaAsincrona) [AsincronoAsimmetricoPolling]...");
				this.connettoreMsg = this.configurazionePdDManager.getConsegnaRispostaAsincrona(this.sa,this.richiestaApplicativa);
				if(this.connettoreMsg!=null){
					this.connettoreMsg.initPolicyGestioneToken(configurazionePdDManager);
				}
				this.msgDiag.mediumDebug("Inizializzo contesto per la gestione (consegnaRispostaAsincronaConGetMessage) [ConsegnaContenuti/AsincronoSimmetricoRisposta]...");
				integrationManager = this.configurazionePdDManager.consegnaRispostaAsincronaConGetMessage(this.sa);
				this.msgDiag.mediumDebug("Inizializzo contesto per la gestione (getTipoValidazioneContenutoApplicativo) [AsincronoAsimmetricoPolling]...");
				this.validazioneContenutoApplicativoApplicativo = this.configurazionePdDManager.getTipoValidazioneContenutoApplicativo(this.pa,this.implementazionePdDMittente, false);
				this.proprietaValidazioneContenutoApplicativoApplicativo = this.pa.getProprietaList();
				this.msgDiag.mediumDebug("Inizializzo contesto per la gestione (consegnaRispostaAsincronaPerRiferimento) [AsincronoAsimmetricoPolling]...");
				this.consegnaPerRiferimento = this.configurazionePdDManager.consegnaRispostaAsincronaPerRiferimento(this.sa);
				this.msgDiag.mediumDebug("Inizializzo contesto per la gestione (consegnaRispostaAsincronaRispostaPerRiferimento) [AsincronoAsimmetricoPolling]...");
				this.rispostaPerRiferimento = this.configurazionePdDManager.consegnaRispostaAsincronaRispostaPerRiferimento(this.sa);
			}catch(Exception e){
				this.msgDiag.logErroreGenerico(e, "AsincronoSimmetricoPolling.getDatiConsegna(sa:"+this.servizioApplicativo+")");
				this.ejbUtils.rollbackMessage("[AsincronoSimmetricoPolling] Connettore per consegna applicativa non definito:"+e.getMessage(),this.servizioApplicativo, esito);
				esito.setEsitoInvocazione(false); 
				esito.setStatoInvocazioneErroreNonGestito(e);
				return esito;
			}
		}else{
			try{
				this.msgDiag.mediumDebug("Inizializzo contesto per la gestione (getInvocazioneServizio)...");
				this.connettoreMsg = this.configurazionePdDManager.getInvocazioneServizio(this.sa,this.richiestaApplicativa);
				if(this.connettoreMsg!=null){
					this.connettoreMsg.initPolicyGestioneToken(configurazionePdDManager);
				}
				this.msgDiag.mediumDebug("Inizializzo contesto per la gestione (consegnaRispostaAsincronaConGetMessage) [ConsegnaContenuti/AsincronoSimmetricoRisposta]...");
				integrationManager = this.configurazionePdDManager.invocazioneServizioConGetMessage(this.sa);
				this.msgDiag.mediumDebug("Inizializzo contesto per la gestione (getTipoValidazioneContenutoApplicativo)...");
				this.validazioneContenutoApplicativoApplicativo = this.configurazionePdDManager.getTipoValidazioneContenutoApplicativo(this.pa,this.implementazionePdDMittente, false);
				this.proprietaValidazioneContenutoApplicativoApplicativo = this.pa.getProprietaList();
				this.msgDiag.mediumDebug("Inizializzo contesto per la gestione (invocazioneServizioPerRiferimento)...");
				this.consegnaPerRiferimento = this.configurazionePdDManager.invocazioneServizioPerRiferimento(this.sa);
				this.msgDiag.mediumDebug("Inizializzo contesto per la gestione (invocazioneServizioRispostaPerRiferimento)...");
				this.rispostaPerRiferimento = this.configurazionePdDManager.invocazioneServizioRispostaPerRiferimento(this.sa);
			}catch(Exception e){
				this.msgDiag.logErroreGenerico(e, "InvocazioneServizio.getDatiConsegna(sa:"+this.servizioApplicativo+")");
				this.ejbUtils.rollbackMessage("Connettore per consegna applicativa non definito:"+e.getMessage(),this.servizioApplicativo, esito);
				esito.setEsitoInvocazione(false); 
				esito.setStatoInvocazioneErroreNonGestito(e);
				return esito;
			}

		}
		this.msgDiag.mediumDebug("Check parametri...");
		if(this.connettoreMsg==null){
			this.msgDiag.logErroreGenerico("Connettore non definito nella configurazione (is null)", "getDatiConsegna(sa:"+this.servizioApplicativo+")");
			this.ejbUtils.rollbackMessage("Connettore per consegna applicativa non definito per il sa ["+this.servizioApplicativo+"]",this.servizioApplicativo, esito);
			esito.setEsitoInvocazione(false); 
			esito.setStatoInvocazione(EsitoLib.ERRORE_NON_GESTITO, "Connettore per consegna applicativa non definito per il sa ["+this.servizioApplicativo+"]");
			return esito;
		}
		this.connettoreMsg.setProtocolFactory(this.protocolFactory);
		this.connettoreMsg.setGestioneManifest(this.gestioneManifest);
		this.connettoreMsg.setProprietaManifestAttachments(this.proprietaManifestAttachments);
		this.connettoreMsg.setLocalForward(this.localForward);
		if(transazioneApplicativoServer!=null) {
			this.transazioneApplicativoServer.setConsegnaIntegrationManager(integrationManager);
			this.connettoreMsg.setTransazioneApplicativoServer(transazioneApplicativoServer);
			this.connettoreMsg.setIdPortaApplicativa(this.idPA);
			this.connettoreMsg.setDataConsegnaTransazioneApplicativoServer(this.dataConsegna);
		}
		ForwardProxy forwardProxy = null;
		if(this.configurazionePdDManager.isForwardProxyEnabled()) {
			try {
				forwardProxy = this.configurazionePdDManager.getForwardProxyConfigErogazione(this.identitaPdD, this.idServizio);
			}catch(Exception e) {
				this.msgDiag.logErroreGenerico(e, "Configurazione ForwardProxy (sa:"+this.servizioApplicativo+")");
				this.ejbUtils.rollbackMessage("Configurazione del connettore errata per la funzionalità govway-proxy; sa ["+this.servizioApplicativo+"]",this.servizioApplicativo, esito);
				esito.setEsitoInvocazione(false); 
				esito.setStatoInvocazione(EsitoLib.ERRORE_NON_GESTITO, "Configurazione del connettore errata per la funzionalità govway-proxy; sa ["+this.servizioApplicativo+"]");
				return esito;
			}
		}
		this.connettoreMsg.setForwardProxy(forwardProxy);
		this.connettoreMsg.setIdAccordo(this.idAccordoServizio);

		// Identificativo di una risposta.
		if(this.existsModuloInAttesaRispostaApplicativa){
			this.msgDiag.mediumDebug("Creazione id risposta...");
			try{
				org.openspcoop2.protocol.engine.builder.Imbustamento imbustatore = 
						new org.openspcoop2.protocol.engine.builder.Imbustamento(this.log,this.protocolFactory,this.openspcoopstate.getStatoRichiesta());
				this.idMessageResponse = 
					imbustatore.buildID(this.identitaPdD, 
							(String) this.pddContext.getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE),
							this.propertiesReader.getGestioneSerializableDB_AttesaAttiva(),
							this.propertiesReader.getGestioneSerializableDB_CheckInterval(),
							RuoloMessaggio.RISPOSTA);
			}catch(Exception e){
				this.msgDiag.logErroreGenerico(e, "imbustatore.buildID(idMessageResponse)");
				if(this.existsModuloInAttesaRispostaApplicativa) {
					try{
						this.sendErroreProcessamento(this.localForward, this.localForwardEngine, this.ejbUtils, 
								ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
								get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_507_COSTRUZIONE_IDENTIFICATIVO),
								this.idModuloInAttesa, this.bustaRichiesta, this.idCorrelazioneApplicativa, this.idCorrelazioneApplicativaRisposta, this.servizioApplicativoFruitore, e,
								(this.responseMessage!=null ? this.responseMessage.getParseException() : null),
								this.pddContext);
						esito.setEsitoInvocazione(true); 
						esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO, "imbustatore.buildID(idMessageResponse)");
					}catch(Exception sendError){
						this.ejbUtils.rollbackMessage("Creazione id di risposta (sendRispostaApplicativa) fallita", esito);
						esito.setStatoInvocazioneErroreNonGestito(sendError);
						esito.setEsitoInvocazione(false);
					}
				}else{
					this.ejbUtils.rollbackMessage("Creazione id di risposta fallita", esito);
					esito.setStatoInvocazioneErroreNonGestito(e);
					esito.setEsitoInvocazione(false);
				}
				this.openspcoopstate.releaseResource();

				return esito;
			}

			// Aggiornamento Informazioni
			this.msgDiag.setIdMessaggioRisposta(this.idMessageResponse);
			this.msgDiag.addKeyword(CostantiPdD.KEY_ID_MESSAGGIO_RISPOSTA, this.idMessageResponse);

		}


		this.msgDiag.mediumDebug("Inizializzo contesto per la gestione (Risposta)...");

		//	Modalita' gestione risposta (Sincrona/Fault/Ricevute...)
		// Per i profili diversi dal sincrono e' possibile impostare dove far ritornare l'errore
		if(Costanti.SCENARIO_ONEWAY_INVOCAZIONE_SERVIZIO.equals(this.scenarioCooperazione)){
			if( (this.consegnaContenutiApplicativiMsg.isStateless()==false) &&  (!this.existsModuloInAttesaRispostaApplicativa) ){
				this.newConnectionForResponse = this.configurazionePdDManager.newConnectionForResponse();
			}
		}else if( (ProfiloDiCollaborazione.SINCRONO.equals(this.bustaRichiesta.getProfiloDiCollaborazione())==false) &&
				(this.consegnaContenutiApplicativiMsg.isStateless()==false) ){
			this.newConnectionForResponse = this.configurazionePdDManager.newConnectionForResponse();
		}
		this.ejbUtils.setReplyOnNewConnection(this.newConnectionForResponse);


		// Gestione indirizzo telematico
		boolean gestioneIndirizzoTelematico = false;
		if(this.consegnaContenutiApplicativiMsg.isStateless()==false){
			gestioneIndirizzoTelematico = this.configurazionePdDManager.isUtilizzoIndirizzoTelematico();
		}
		this.ejbUtils.setUtilizzoIndirizzoTelematico(gestioneIndirizzoTelematico);

		try{
			this.protocolManager = this.protocolFactory.createProtocolVersionManager(this.profiloGestione);
			this.isBlockedTransaction_responseMessageWithTransportCodeError = 
					this.protocolManager.isBlockedTransaction_responseMessageWithTransportCodeError();
		}catch(Exception e){
			this.msgDiag.logErroreGenerico(e, "ProtocolFactory.createProtocolManager("+this.profiloGestione+")");
			this.ejbUtils.rollbackMessage("ProtocolFactory.createProtocolManager("+this.profiloGestione+"):"+e.getMessage(),this.servizioApplicativo, esito);
			esito.setEsitoInvocazione(false); 
			esito.setStatoInvocazioneErroreNonGestito(e);
			return esito;
		}
		boolean consegnaInOrdine = false;
		// Sequenza: deve essere abilitata la consegna affidabile + la collaborazione e infine la consegna in ordine e non deve essere richiesto il profilo linee guida 1.0
		if(Costanti.SCENARIO_ONEWAY_INVOCAZIONE_SERVIZIO.equals(this.scenarioCooperazione)){
			
			if(this.bustaRichiesta!=null) {
				this.bustaIndicazioneConsegnaInOrdine = this.bustaRichiesta.clone();
			}
			else {
				this.bustaIndicazioneConsegnaInOrdine = new Busta(this.protocolFactory.getProtocol());
			}
			this.bustaIndicazioneConsegnaInOrdine.setProfiloDiCollaborazione(this.profiloCollaborazione);
			
			switch (this.protocolManager.getConsegnaInOrdine(this.bustaIndicazioneConsegnaInOrdine)) {
			case ABILITATA:
				consegnaInOrdine = true;
				break;
			case DISABILITATA:
				consegnaInOrdine = false;
				break;
			default:
				boolean gestioneConsegnaInOrdineAbilitata =  this.propertiesReader.isGestioneRiscontri(this.implementazionePdDMittente) && 
				this.propertiesReader.isGestioneElementoCollaborazione(this.implementazionePdDMittente) && 
				this.propertiesReader.isGestioneConsegnaInOrdine(this.implementazionePdDMittente);	
				consegnaInOrdine = gestioneConsegnaInOrdineAbilitata &&
					this.bustaRichiesta.getSequenza()!=-1;
				break;
			}
		}




		// Punto di inizio per la transazione.
		try{	    


			/* ---------------- Check per consegna in ordine ----------------*/
			if(consegnaInOrdine){
				if(this.oneWayVersione11 || openspcoopstate instanceof OpenSPCoopStateful){
					this.msgDiag.mediumDebug("Controllo consegna in ordine...");
					try{
						this.ordineConsegna = new ConsegnaInOrdine(this.openspcoopstate.getStatoRichiesta(),this.protocolFactory);
						if(this.ordineConsegna.isConsegnaInOrdine(this.bustaRichiesta, 
								this.propertiesReader.getGestioneSerializableDB_AttesaAttiva(),
								this.propertiesReader.getGestioneSerializableDB_CheckInterval()) == false){
							// congelamento busta
							this.msgDiag.addKeyword(CostantiPdD.KEY_SEQUENZA_ATTESA, this.ordineConsegna.getSequenzaAttesa()+"");
							if(this.ordineConsegna.getSequenzaAttesa()>this.bustaRichiesta.getSequenza()){
								this.msgDiag.logPersonalizzato("consegnaInOrdine.messaggioGiaConsegnato");
								this.ejbUtils.releaseInboxMessage(true);
								esito.setEsitoInvocazione(true); 
								esito.setStatoInvocazione(EsitoLib.ERRORE_NON_GESTITO, 
										this.msgDiag.getMessaggio_replaceKeywords("consegnaInOrdine.messaggioGiaConsegnato"));
							}else{
								this.msgDiag.logPersonalizzato("consegnaInOrdine.messaggioFuoriOrdine");
								this.ejbUtils.rollbackMessage(this.msgDiag.getMessaggio_replaceKeywords("consegnaInOrdine.messaggioFuoriOrdine"), this.servizioApplicativo, esito);
								esito.setEsitoInvocazione(false);
								esito.setStatoInvocazione(EsitoLib.ERRORE_NON_GESTITO, 
										this.msgDiag.getMessaggio_replaceKeywords("consegnaInOrdine.messaggioFuoriOrdine"));
							}
							this.openspcoopstate.releaseResource();
							return esito;
						}
					}catch(Exception e){
						this.log.error("Riscontrato errore durante la gestione della sequenza per la consegna in ordine",e);
						this.msgDiag.logErroreGenerico(e, "GetioneSequenzaInOrdine");
						this.ejbUtils.rollbackMessage("Errore verifica consegna in ordine: "+e.getMessage(), this.servizioApplicativo, esito);
						this.openspcoopstate.releaseResource();
						esito.setEsitoInvocazione(false); 
						esito.setStatoInvocazioneErroreNonGestito(e);
						return esito;
					}
				}
			}





			/* ------------  Ricostruzione Messaggio Soap da spedire ------------- */
			this.msgDiag.mediumDebug("Ricostruzione SOAPEnvelope di richiesta/consegna...");	
			try{
				if(this.consegnaMessagePrimaTrasformazione==null) {
					if(this.consegnaPerRiferimento==false){
						this.consegnaMessagePrimaTrasformazione = this.msgRequest.getMessage();
						correctForwardPathNotifiche(transazioneApplicativoServer, this.consegnaMessagePrimaTrasformazione, this.protocolFactory);
					}else{
						// consegnaMessage deve contenere il messaggio necessario all'invocazione del metodo pubblicaEvento
						this.consegnaMessagePrimaTrasformazione = 
							this.msgRequest.buildRichiestaPubblicazioneMessaggio_RepositoryMessaggi(this.soggettoFruitore, tipoServizio,servizio,azione);
					}
				}
			}catch(Exception e){
				this.msgDiag.logErroreGenerico(e, "this.msgRequest.getMessage()");

				if(this.existsModuloInAttesaRispostaApplicativa) {
					
					this.sendErroreProcessamento(this.localForward, this.localForwardEngine, this.ejbUtils, 
							ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_509_READ_REQUEST_MSG),
							this.idModuloInAttesa, this.bustaRichiesta, this.idCorrelazioneApplicativa, this.idCorrelazioneApplicativaRisposta, this.servizioApplicativoFruitore, e,
							(this.responseMessage!=null ? this.responseMessage.getParseException() : null),
							this.pddContext);
					
					esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO, "this.msgRequest.getMessage()");
					esito.setEsitoInvocazione(true);
				}else{
					this.ejbUtils.rollbackMessage("Ricostruzione del messaggio Soap da Spedire non riuscita.",this.servizioApplicativo, esito);
					esito.setStatoInvocazioneErroreNonGestito(e);
					esito.setEsitoInvocazione(false);
				}
				this.openspcoopstate.releaseResource();

				return esito;
			}



			/* -----  Header Integrazione ------ */
			String originalID = this.bustaRichiesta.getID();
			if(this.idMessaggioPreBehaviour!=null){
				this.bustaRichiesta.setID(this.idMessaggioPreBehaviour);
			}
			this.msgDiag.mediumDebug("Gestione header di integrazione per la richiesta...");
			HeaderIntegrazione headerIntegrazione = new HeaderIntegrazione(this.idTransazione);
			if(soggettoFruitoreHeaderIntegrazione!=null){
				headerIntegrazione.getBusta().setTipoMittente(soggettoFruitoreHeaderIntegrazione.getTipo());
				headerIntegrazione.getBusta().setMittente(soggettoFruitoreHeaderIntegrazione.getNome());
			}else if(this.soggettoFruitore!=null){
				headerIntegrazione.getBusta().setTipoMittente(this.soggettoFruitore.getTipo());
				headerIntegrazione.getBusta().setMittente(this.soggettoFruitore.getNome());
			}
			if(this.servizioHeaderIntegrazione!=null){
				headerIntegrazione.getBusta().setTipoDestinatario(this.servizioHeaderIntegrazione.getSoggettoErogatore().getTipo());
				headerIntegrazione.getBusta().setDestinatario(this.servizioHeaderIntegrazione.getSoggettoErogatore().getNome());
				headerIntegrazione.getBusta().setTipoServizio(this.servizioHeaderIntegrazione.getTipo());
				headerIntegrazione.getBusta().setServizio(this.servizioHeaderIntegrazione.getNome());
				headerIntegrazione.getBusta().setVersioneServizio(this.servizioHeaderIntegrazione.getVersione());
				headerIntegrazione.getBusta().setAzione(this.servizioHeaderIntegrazione.getAzione());
			}else{
				headerIntegrazione.getBusta().setTipoDestinatario(this.idServizio.getSoggettoErogatore().getTipo());
				headerIntegrazione.getBusta().setDestinatario(this.idServizio.getSoggettoErogatore().getNome());
				headerIntegrazione.getBusta().setTipoServizio(this.idServizio.getTipo());
				headerIntegrazione.getBusta().setServizio(this.idServizio.getNome());
				headerIntegrazione.getBusta().setVersioneServizio(this.idServizio.getVersione());
				headerIntegrazione.getBusta().setAzione(this.idServizio.getAzione());
			}
			headerIntegrazione.getBusta().setID(this.bustaRichiesta.getID());
			headerIntegrazione.getBusta().setRiferimentoMessaggio(this.bustaRichiesta.getRiferimentoMessaggio());
			headerIntegrazione.getBusta().setIdCollaborazione(this.bustaRichiesta.getCollaborazione());
			headerIntegrazione.getBusta().setProfiloDiCollaborazione(this.bustaRichiesta.getProfiloDiCollaborazione());
			headerIntegrazione.setIdApplicativo(this.idCorrelazioneApplicativa);
			headerIntegrazione.setServizioApplicativo(this.servizioApplicativoFruitore);

			Map<String, List<String>> propertiesTrasporto = new HashMap<String, List<String>>();
			Map<String, List<String>> propertiesUrlBased = new HashMap<String, List<String>>();

			if(this.tipiIntegrazione==null){
				if(ConsegnaContenutiApplicativi.defaultPerProtocolloGestoreIntegrazionePA.containsKey(this.protocolFactory.getProtocol())){
					this.tipiIntegrazione = ConsegnaContenutiApplicativi.defaultPerProtocolloGestoreIntegrazionePA.get(this.protocolFactory.getProtocol());
				}else{
					this.tipiIntegrazione = ConsegnaContenutiApplicativi.defaultGestoriIntegrazionePA;
				}
			}
			
			this.outRequestPAMessage = new OutRequestPAMessage();
			this.outRequestPAMessage.setBustaRichiesta(this.bustaRichiesta);
			this.outRequestPAMessage.setMessage(this.consegnaMessagePrimaTrasformazione);
			if(this.pa!=null)
				this.outRequestPAMessage.setPortaApplicativa(this.pa);
			else
				this.outRequestPAMessage.setPortaDelegata(this.pd);
			this.outRequestPAMessage.setHeaders(propertiesTrasporto);
			this.outRequestPAMessage.setParameters(propertiesUrlBased);
			if(this.servizioHeaderIntegrazione!=null){
				this.outRequestPAMessage.setServizio(this.servizioHeaderIntegrazione);
			}else{
				this.outRequestPAMessage.setServizio(this.idServizio);
			}
			if(soggettoFruitoreHeaderIntegrazione!=null){
				this.outRequestPAMessage.setSoggettoMittente(soggettoFruitoreHeaderIntegrazione);
			}else{
				this.outRequestPAMessage.setSoggettoMittente(this.soggettoFruitore);
			}
			for(int i=0; i<this.tipiIntegrazione.length;i++){
				try{
					IGestoreIntegrazionePA gestore = null;
					try{
						gestore = (IGestoreIntegrazionePA) this.pluginLoader.newIntegrazionePortaApplicativa(this.tipiIntegrazione[i]);
					}catch(Exception e){
						throw e;
					}
					if(gestore!=null){
						String classType = null;
						try {
							classType = gestore.getClass().getName();
							AbstractCore.init(gestore, this.pddContext, this.protocolFactory);
						}catch(Exception e){
							throw new Exception("Riscontrato errore durante l'inizializzazione della classe ["+classType+
									"] da utilizzare per la gestione dell'integrazione delle erogazione di tipo ["+this.tipiIntegrazione[i]+"]: "+e.getMessage());
						}
						if(gestore instanceof IGestoreIntegrazionePASoap){
							if(this.propertiesReader.processHeaderIntegrazionePARequest(false)){
								if(this.propertiesReader.deleteHeaderIntegrazioneRequestPA()){
									gestore.setOutRequestHeader(headerIntegrazione,this.outRequestPAMessage);
								}
								else{
									// gia effettuato l'update dell'header in RicezioneBuste
								}
							}else{
								gestore.setOutRequestHeader(headerIntegrazione,this.outRequestPAMessage);
							}
						}else{
							gestore.setOutRequestHeader(headerIntegrazione,this.outRequestPAMessage);
						}
					}else{
						throw new Exception("Gestore non inizializzato");
					}

				}catch(Exception e){
					this.msgDiag.addKeyword(CostantiPdD.KEY_TIPO_HEADER_INTEGRAZIONE,this.tipiIntegrazione[i]);
					this.msgDiag.addKeywordErroreProcessamento(e);
					this.msgDiag.logPersonalizzato("headerIntegrazione.creazioneFallita");
					this.log.error(this.msgDiag.getMessaggio_replaceKeywords("headerIntegrazione.creazioneFallita"), e);
				}
			}






			
			
			
			
			
			
			/* ------------ Trasformazione Richiesta  -------------- */
			
			this.consegnaMessageTrasformato = this.consegnaMessagePrimaTrasformazione;
			if(this.trasformazioni!=null) {
				try {
					this.gestoreTrasformazioni = new GestoreTrasformazioni(this.log, msgDiag, this.idServizio, this.soggettoFruitore, this.servizioApplicativoFruitore, 
							this.trasformazioni, this.transactionNullable, this.pddContext, this.requestInfo, this.tipoPdD,
							generatoreErrorePA);
					this.consegnaMessageTrasformato = this.gestoreTrasformazioni.trasformazioneRichiesta(this.consegnaMessagePrimaTrasformazione, this.bustaRichiesta);
				}
				catch(GestoreTrasformazioniException e) {
					
					this.msgDiag.addKeywordErroreProcessamento(e);
					this.msgDiag.logPersonalizzato("trasformazione.processamentoRichiestaInErrore");
					
					this.pddContext.addObject(org.openspcoop2.core.constants.Costanti.ERRORE_TRASFORMAZIONE_RICHIESTA, "true");
					
					ErroreIntegrazione erroreIntegrazione = this.gestoreTrasformazioni.getErrore();
					if(erroreIntegrazione==null) {
						erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
								get5XX_ErroreProcessamento(e,CodiceErroreIntegrazione.CODICE_562_TRASFORMAZIONE);
					}
					
					String msgErrore = e.getMessage();					
					if(this.existsModuloInAttesaRispostaApplicativa) {
						IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.TRANSFORMATION_RULE_REQUEST_FAILED;
						if(e.getOp2IntegrationFunctionError()!=null) {
							integrationFunctionError = e.getOp2IntegrationFunctionError();
						}
						this.ejbUtils.setIntegrationFunctionErrorPortaApplicativa(integrationFunctionError);
						if(this.localForwardEngine!=null) {
							this.localForwardEngine.setIntegrationFunctionError(integrationFunctionError);
						}
						if(e.getOpenSPCoop2ErrorMessage()!=null) {
							this.sendErroreProcessamento(this.localForward, this.localForwardEngine, this.ejbUtils, 
									e.getOpenSPCoop2ErrorMessage(), msgErrore,
									this.idModuloInAttesa, this.bustaRichiesta, this.idCorrelazioneApplicativa, this.idCorrelazioneApplicativaRisposta, this.servizioApplicativoFruitore);
						}
						else {
							this.sendErroreProcessamento(this.localForward, this.localForwardEngine, this.ejbUtils, 
									erroreIntegrazione,
									this.idModuloInAttesa, this.bustaRichiesta, this.idCorrelazioneApplicativa, this.idCorrelazioneApplicativaRisposta, this.servizioApplicativoFruitore, e,
									(this.responseMessage!=null ? this.responseMessage.getParseException() : null),
									this.pddContext);
						}
						esito.setEsitoInvocazione(true); 
						esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO, msgErrore);
					}else{
						this.ejbUtils.rollbackMessage(msgErrore,this.servizioApplicativo, esito);
						esito.setEsitoInvocazione(false); 
						esito.setStatoInvocazioneErroreNonGestito(e);
					}
					this.openspcoopstate.releaseResource();
					return esito;
				}		
			}	
			
			
			
			
			
			
			
			
			
			
			





			/* ------------------- Definizione connettoreMsg -----------------------*/
						
			// mapping in valori delle keyword delle proprieta di trasporto protocol-properties.
			this.msgDiag.mediumDebug("Impostazione messaggio del connettore...");
			mappingProtocolProperties(this.connettoreMsg.getPropertiesTrasporto(), propertiesTrasporto, 
					soggettoFruitoreHeaderIntegrazione, this.servizioHeaderIntegrazione, this.soggettoFruitore, this.idServizio, this.bustaRichiesta, this.idCorrelazioneApplicativa);
			mappingProtocolProperties(this.connettoreMsg.getPropertiesUrlBased(), propertiesUrlBased, 
					soggettoFruitoreHeaderIntegrazione, this.servizioHeaderIntegrazione, this.soggettoFruitore, this.idServizio, this.bustaRichiesta, this.idCorrelazioneApplicativa);

			// definizione connettore
			this.connettoreMsg.setRequestMessage(this.consegnaMessageTrasformato);
			this.connettoreMsg.setIdModulo(ConsegnaContenutiApplicativi.ID_MODULO);
			this.connettoreMsg.setPropertiesTrasporto(propertiesTrasporto);
			this.connettoreMsg.setPropertiesUrlBased(propertiesUrlBased);
			this.connettoreMsg.setBusta(this.bustaRichiesta);
			this.connettoreMsg.setMsgDiagnostico(msgDiag);
			this.connettoreMsg.setState(this.openspcoopstate.getStatoRichiesta());
			if(this.consegnaMessagePrimaTrasformazione!=null && this.consegnaMessagePrimaTrasformazione.getTransportRequestContext()!=null) {
				this.connettoreMsg.setUrlInvocazionePorta(this.consegnaMessagePrimaTrasformazione.getTransportRequestContext().getUrlInvocazione_formBased());
			}

			this.bustaRichiesta.setID(originalID);









			/* ------------------- Preparo Spedizione -----------------------*/
			this.msgDiag.mediumDebug("Inizializzazione connettore per la spedizione...");
			//	Connettore per consegna
			this.tipoConnector = this.connettoreMsg.getTipoConnettore();
			this.msgDiag.addKeyword(CostantiPdD.KEY_TIPO_CONNETTORE, this.tipoConnector);
			
			// mapping per forward token
			TokenForward tokenForward = null;
			Object oTokenForward = this.consegnaMessageTrasformato.getContextProperty(org.openspcoop2.pdd.core.token.Costanti.MSG_CONTEXT_TOKEN_FORWARD);
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
					
			// Carico connettore richiesto
			if(this.invokerNonSupportato==false){
				try{
					String tipoConnettoreEffettivo = ConnettoreUtils.formatTipoConnettore(this.propertiesReader, this.tipoConnector, (this.asyncResponseCallback!=null));
					this.connectorSender = (IConnettore) this.pluginLoader.newConnettore(tipoConnettoreEffettivo);
				}
				catch(Exception e){
					this.msgDiag.logErroreGenerico(e,"Inizializzazione Connettore"); // l'errore contiene gia tutte le informazioni
					this.invokerNonSupportato = true;
					this.eInvokerNonSupportato = e;
				}
				if(this.connectorSender!=null) {
					try {
						this.connectorClass = this.connectorSender.getClass().getName();
						AbstractCore.init(this.connectorSender, this.pddContext, this.protocolFactory);
					}catch(Exception e){
						this.msgDiag.logErroreGenerico(e,"IConnettore.newInstance(tipo:"+this.tipoConnector+" class:"+this.connectorClass+")");
						this.invokerNonSupportato = true;
						this.eInvokerNonSupportato = e;
					}
				}
				if( (this.invokerNonSupportato == false) && (this.connectorSender == null)){
					this.msgDiag.logErroreGenerico("ConnectorSender is null","IConnettore.newInstance(tipo:"+this.tipoConnector+" class:"+this.connectorClass+")");
					this.invokerNonSupportato = true;
				}
			}
			
			// Imposto tipo di richiesta
			if(this.connectorSender!=null){
				try{
					if(this.connectorSender instanceof ConnettoreBaseHTTP){
						ConnettoreBaseHTTP baseHttp = (ConnettoreBaseHTTP) this.connectorSender;
						baseHttp.setHttpMethod(this.consegnaMessageTrasformato);
						
						if(ServiceBinding.REST.equals(this.consegnaMessageTrasformato.getServiceBinding())){
							this.httpRequestMethod = baseHttp.getHttpMethod();
						}
					}
				}catch(Exception e){
					this.msgDiag.logErroreGenerico(e,"ConnettoreBaseHTTP.setHttpMethod(tipo:"+this.tipoConnector+" class:"+this.connectorClass+")");
					this.invokerNonSupportato = true;
					this.eInvokerNonSupportato = e;
				}
			}

			// Location
			this.location = ConnettoreUtils.getAndReplaceLocationWithBustaValues(this.connectorSender, this.connettoreMsg, this.bustaRichiesta, this.pddContext, this.protocolFactory,  this.log);
			if(this.location!=null){
				String locationWithUrl = ConnettoreUtils.buildLocationWithURLBasedParameter(this.log, this.consegnaMessageTrasformato, this.tipoConnector, this.connettoreMsg.getPropertiesUrlBased(), this.location,
						this.protocolFactory, this.idModulo);
				locationWithUrl = ConnettoreUtils.addProxyInfoToLocationForHTTPConnector(this.tipoConnector, this.connettoreMsg.getConnectorProperties(), locationWithUrl);
				locationWithUrl = ConnettoreUtils.addGovWayProxyInfoToLocationForHTTPConnector(this.connettoreMsg.getForwardProxy(),this.connectorSender, locationWithUrl);
				this.msgDiag.addKeyword(CostantiPdD.KEY_LOCATION, ConnettoreUtils.formatLocation(this.httpRequestMethod, locationWithUrl));
				
				this.pddContext.addObject(CostantiPdD.CONNETTORE_REQUEST_METHOD, this.httpRequestMethod);
				this.pddContext.addObject(CostantiPdD.CONNETTORE_REQUEST_URL, locationWithUrl);
			}
			else{
				this.msgDiag.addKeyword(CostantiPdD.KEY_LOCATION, "N.D.");
			}

			// timeout di default
			if(this.connettoreMsg.getConnectorProperties()==null){
				java.util.Map<String,String> propCon = new java.util.HashMap<String,String>();
				this.connettoreMsg.setConnectorProperties(propCon);
			}
			if(this.connettoreMsg.getConnectorProperties().get(CostantiConnettori.CONNETTORE_CONNECTION_TIMEOUT)==null){
				this.connettoreMsg.getConnectorProperties().put(CostantiConnettori.CONNETTORE_CONNECTION_TIMEOUT,""+this.propertiesReader.getConnectionTimeout_consegnaContenutiApplicativi());
			}
			if(this.connettoreMsg.getConnectorProperties().get(CostantiConnettori.CONNETTORE_READ_CONNECTION_TIMEOUT)==null){
				this.connettoreMsg.getConnectorProperties().put(CostantiConnettori.CONNETTORE_READ_CONNECTION_TIMEOUT,""+this.propertiesReader.getReadConnectionTimeout_consegnaContenutiApplicativi());
			}

			// behaviourForwardToConfiguration
			if(behaviourForwardToConfiguration!=null){
				if(behaviourForwardToConfiguration.getSbustamentoInformazioniProtocollo()!=null){
					if(org.openspcoop2.pdd.core.behaviour.StatoFunzionalita.ABILITATA.equals(behaviourForwardToConfiguration.getSbustamentoInformazioniProtocollo())){
						this.connettoreMsg.setSbustamentoInformazioniProtocollo(true);
					}
					else if(org.openspcoop2.pdd.core.behaviour.StatoFunzionalita.DISABILITATA.equals(behaviourForwardToConfiguration.getSbustamentoInformazioniProtocollo())){
						this.connettoreMsg.setSbustamentoInformazioniProtocollo(false);
					}
				}
				if(behaviourForwardToConfiguration.getSbustamentoSoap()!=null){
					if(org.openspcoop2.pdd.core.behaviour.StatoFunzionalita.ABILITATA.equals(behaviourForwardToConfiguration.getSbustamentoSoap())){
						this.connettoreMsg.setSbustamentoSOAP(true);
					}
					else if(org.openspcoop2.pdd.core.behaviour.StatoFunzionalita.DISABILITATA.equals(behaviourForwardToConfiguration.getSbustamentoSoap())){
						this.connettoreMsg.setSbustamentoSOAP(false);
					}
				}
			}
			
			// GestioneErrore
			if(gestioneErroreBehaviour!=null) {
				this.gestioneConsegnaConnettore = gestioneErroreBehaviour;
			}
			else {
				if(Costanti.SCENARIO_CONSEGNA_CONTENUTI_APPLICATIVI.equals(this.scenarioCooperazione) ||
						Costanti.SCENARIO_ASINCRONO_SIMMETRICO_CONSEGNA_RISPOSTA.equals(this.scenarioCooperazione) ){
					try{
						this.msgDiag.mediumDebug("Inizializzo contesto per la gestione (getGestioneErroreConnettore_RispostaAsincrona) [ConsegnaContenuti/AsincronoSimmetricoRisposta]...");
						this.gestioneConsegnaConnettore = this.configurazionePdDManager.getGestioneErroreConnettore_RispostaAsincrona(this.protocolFactory, this.consegnaMessageTrasformato.getServiceBinding(), this.sa);
					}catch(Exception e){
						this.msgDiag.logErroreGenerico(e, "ConsegnaAsincrona.getDatiConsegna(sa:"+this.servizioApplicativo+")");
						this.ejbUtils.rollbackMessage("[ConsegnaAsincrona] Connettore per consegna applicativa non definito:"+e.getMessage(),this.servizioApplicativo, esito);
						esito.setEsitoInvocazione(false); 
						esito.setStatoInvocazioneErroreNonGestito(e);
						return esito;
					}
	
	
				}
				else if(Costanti.SCENARIO_ASINCRONO_ASIMMETRICO_POLLING.equals(this.scenarioCooperazione)){
					try{
						this.msgDiag.mediumDebug("Inizializzo contesto per la gestione (getGestioneErroreConnettore_RispostaAsincrona) [AsincronoAsimmetricoPolling]...");
						this.gestioneConsegnaConnettore = this.configurazionePdDManager.getGestioneErroreConnettore_RispostaAsincrona(this.protocolFactory, this.consegnaMessageTrasformato.getServiceBinding(), this.sa);
					}catch(Exception e){
						this.msgDiag.logErroreGenerico(e, "AsincronoSimmetricoPolling.getDatiConsegna(sa:"+this.servizioApplicativo+")");
						this.ejbUtils.rollbackMessage("[AsincronoSimmetricoPolling] Connettore per consegna applicativa non definito:"+e.getMessage(),this.servizioApplicativo, esito);
						esito.setEsitoInvocazione(false); 
						esito.setStatoInvocazioneErroreNonGestito(e);
						return esito;
					}
				}else{
					try{
						this.gestioneConsegnaConnettore = this.configurazionePdDManager.getGestioneErroreConnettore_InvocazioneServizio(this.protocolFactory, this.consegnaMessageTrasformato.getServiceBinding(), this.sa);
						this.msgDiag.mediumDebug("Inizializzo contesto per la gestione (invocazioneServizioPerRiferimento)...");
					}catch(Exception e){
						this.msgDiag.logErroreGenerico(e, "InvocazioneServizio.getDatiConsegna(sa:"+this.servizioApplicativo+")");
						this.ejbUtils.rollbackMessage("Connettore per consegna applicativa non definito:"+e.getMessage(),this.servizioApplicativo, esito);
						esito.setEsitoInvocazione(false); 
						esito.setStatoInvocazioneErroreNonGestito(e);
						return esito;
					}
				}
			}
			if(this.gestioneConsegnaConnettore==null){
				this.msgDiag.logErroreGenerico("Gestore Errore di consegna non definito nella configurazione (is null)", "getDatiConsegna(sa:"+this.servizioApplicativo+")");
				this.ejbUtils.rollbackMessage("Gestione Errore di consegna non definito per il sa ["+this.servizioApplicativo+"]",this.servizioApplicativo, esito);
				esito.setEsitoInvocazione(false); 
				esito.setStatoInvocazione(EsitoLib.ERRORE_NON_GESTITO, "Gestione Errore di consegna non definito per il sa ["+this.servizioApplicativo+"]");
				return esito;
			}
			
			
			
			
			
			
			
			/* ------------------- OutRequestHandler -----------------------*/
			try{
				this.outRequestContext = new OutRequestContext(this.log, this.protocolFactory,this.openspcoopstate.getStatoRichiesta());
				
				// Informazioni connettore in uscita
				InfoConnettoreUscita infoConnettoreUscita = new InfoConnettoreUscita();
				infoConnettoreUscita.setLocation(this.location);
				infoConnettoreUscita.setProperties(this.connettoreMsg.getConnectorProperties());
				infoConnettoreUscita.setHeaders(this.connettoreMsg.getPropertiesTrasporto());
				infoConnettoreUscita.setParameters(this.connettoreMsg.getPropertiesUrlBased());
				infoConnettoreUscita.setSbustamentoSoap(this.connettoreMsg.isSbustamentoSOAP());
				infoConnettoreUscita.setSbustamentoInformazioniProtocollo(this.connettoreMsg.isSbustamentoInformazioniProtocollo());
				infoConnettoreUscita.setTipoAutenticazione(this.connettoreMsg.getAutenticazione());
				infoConnettoreUscita.setCredenziali(this.connettoreMsg.getCredenziali());
				infoConnettoreUscita.setTipoConnettore(this.tipoConnector);
				this.outRequestContext.setConnettore(infoConnettoreUscita);
				
				// Informazioni messaggio
				this.outRequestContext.setMessaggio(this.consegnaMessageTrasformato);
				
				// TransazioneApplicativoServer
				this.outRequestContext.setTransazioneApplicativoServer(transazioneApplicativoServer);
				
				// Contesto
				ProtocolContext protocolContext = new ProtocolContext();
				protocolContext.setIdRichiesta(this.idMessaggioConsegna);
				if(this.idMessaggioPreBehaviour!=null){
					protocolContext.setIdRichiesta(this.idMessaggioPreBehaviour);
				}
				protocolContext.setFruitore(this.soggettoFruitore);
				if(this.bustaRichiesta!=null){
					protocolContext.setIndirizzoFruitore(this.bustaRichiesta.getIndirizzoMittente());
				}
				if(this.idServizio!=null){
					protocolContext.setErogatore(this.idServizio.getSoggettoErogatore());
					if(this.bustaRichiesta!=null){
						protocolContext.setIndirizzoErogatore(this.bustaRichiesta.getIndirizzoDestinatario());
					}
					protocolContext.setTipoServizio(this.idServizio.getTipo());
					protocolContext.setServizio(this.idServizio.getNome());
					protocolContext.setVersioneServizio(this.idServizio.getVersione());
					protocolContext.setAzione(this.idServizio.getAzione());
				}
				if(this.idAccordoServizio!=null){
					protocolContext.setIdAccordo(this.idAccordoServizio);
				}
				else if(this.idServizio!=null && this.idServizio.getUriAccordoServizioParteComune()!=null){
					protocolContext.setIdAccordo(IDAccordoFactory.getInstance().getIDAccordoFromUri(this.idServizio.getUriAccordoServizioParteComune()));
				}
				String profiloCollorazioneValue = null;
				if(this.bustaRichiesta!=null){
					profiloCollorazioneValue = this.bustaRichiesta.getProfiloDiCollaborazioneValue();
				}
				protocolContext.setProfiloCollaborazione(this.profiloCollaborazione,profiloCollorazioneValue);
				if(this.bustaRichiesta!=null){
					protocolContext.setCollaborazione(this.bustaRichiesta.getCollaborazione());
				}
				protocolContext.setDominio(this.msgDiag.getDominio());
				protocolContext.setScenarioCooperazione(this.scenarioCooperazione);
				this.outRequestContext.setProtocollo(protocolContext);
				
				// Integrazione
				IntegrationContext integrationContext = new IntegrationContext();
				integrationContext.setIdCorrelazioneApplicativa(this.idCorrelazioneApplicativa);
				integrationContext.setServizioApplicativoFruitore(this.servizioApplicativoFruitore);
				integrationContext.addServizioApplicativoErogatore(this.servizioApplicativo);
				integrationContext.setGestioneStateless(this.portaDiTipoStateless);
				integrationContext.setIdPA(this.idPA);
				integrationContext.setIdPD(this.idPD);
				this.outRequestContext.setIntegrazione(integrationContext);
				
				// Altre informazioni
				this.outRequestContext.setDataElaborazioneMessaggio(DateManager.getDate());
				this.outRequestContext.setPddContext(this.pddContext);
				this.outRequestContext.setTipoPorta(TipoPdD.APPLICATIVA);
				this.outRequestContext.setIdModulo(this.idModulo);

				// Invocazione handler
				GestoreHandlers.outRequest(this.outRequestContext, msgDiag, this.log);
				
				// Riporto messaggio
				this.consegnaMessageTrasformato = this.outRequestContext.getMessaggio();
				
				// Salvo handler
				this.connettoreMsg.setOutRequestContext(this.outRequestContext);
				
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
						this.msgDiag.logErroreGenerico(e, ((HandlerException)e).getIdentitaHandler());
					}
					msgErrore = ((HandlerException)e).getIdentitaHandler()+" error: "+msgErrore;
					if(this.existsModuloInAttesaRispostaApplicativa) {
						erroreIntegrazione = he.convertToErroreIntegrazione();
						integrationFunctionError = he.getIntegrationFunctionError();
					}
				}else{
					this.msgDiag.logErroreGenerico(e, "OutRequestHandler");
					msgErrore = "OutRequestHandler error: "+msgErrore;
				}
				if(this.existsModuloInAttesaRispostaApplicativa) {
					
					if(erroreIntegrazione==null){
						erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
								get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_543_HANDLER_OUT_REQUEST);
					}
					
					if(integrationFunctionError!=null) {
						this.ejbUtils.setIntegrationFunctionErrorPortaApplicativa(integrationFunctionError);
						if(this.localForwardEngine!=null) {
							this.localForwardEngine.setIntegrationFunctionError(integrationFunctionError);
						}
					}
					this.sendErroreProcessamento(this.localForward, this.localForwardEngine, this.ejbUtils, 
							erroreIntegrazione,
							this.idModuloInAttesa, this.bustaRichiesta, this.idCorrelazioneApplicativa, this.idCorrelazioneApplicativaRisposta, this.servizioApplicativoFruitore, e,
							(this.responseMessage!=null ? this.responseMessage.getParseException() : null),
							this.pddContext);
					
					esito.setEsitoInvocazione(true); 
					esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO, msgErrore);
				}else{
					this.ejbUtils.rollbackMessage(msgErrore,this.servizioApplicativo, esito);
					esito.setEsitoInvocazione(false); 
					esito.setStatoInvocazioneErroreNonGestito(e);
				}
				this.openspcoopstate.releaseResource();
				return esito;
			}
			
			
			
			
			
			
			
			
			
			
			
			
			/* --------------- REFRESH LOCATION ----------------- */
			// L'handler puo' aggiornare le properties che contengono le proprieta' del connettore.
			this.location = ConnettoreUtils.getAndReplaceLocationWithBustaValues(this.connectorSender, this.connettoreMsg, this.bustaRichiesta, this.pddContext, this.protocolFactory, this.log);
			if(this.location!=null){
				String locationWithUrl = ConnettoreUtils.buildLocationWithURLBasedParameter(this.log, this.consegnaMessageTrasformato, this.tipoConnector, this.connettoreMsg.getPropertiesUrlBased(), this.location,
						this.protocolFactory, this.idModulo);
				locationWithUrl = ConnettoreUtils.addProxyInfoToLocationForHTTPConnector(this.tipoConnector, this.connettoreMsg.getConnectorProperties(), locationWithUrl);
				locationWithUrl = ConnettoreUtils.addGovWayProxyInfoToLocationForHTTPConnector(this.connettoreMsg.getForwardProxy(),this.connectorSender, locationWithUrl);
				this.msgDiag.addKeyword(CostantiPdD.KEY_LOCATION, ConnettoreUtils.formatLocation(this.httpRequestMethod, locationWithUrl));
				
				this.pddContext.addObject(CostantiPdD.CONNETTORE_REQUEST_METHOD, this.httpRequestMethod);
				this.pddContext.addObject(CostantiPdD.CONNETTORE_REQUEST_URL, locationWithUrl);
			}
			else{
				this.msgDiag.addKeyword(CostantiPdD.KEY_LOCATION, "N.D.");
			}
			
			
			
			
			
			
			
			
			
			
			
			
			/* ------------------- Dump -----------------------*/
				
			// Invoco il metodo getMessage del ConnettoreMsg per provocare l'eventuale sbustamento delle informazioni di protocollo
			this.connettoreMsg.getRequestMessage(this.requestInfo, this.pddContext);
			
			String idMessaggioDumpRichiesta = this.idMessaggioConsegna;
			if(this.idMessaggioPreBehaviour!=null){
				idMessaggioDumpRichiesta = this.idMessaggioPreBehaviour;
			}
			
			Dump dumpApplicativoRichiesta = new Dump(this.identitaPdD,ConsegnaContenutiApplicativi.ID_MODULO,idMessaggioDumpRichiesta,
					this.soggettoFruitore,this.idServizio,TipoPdD.APPLICATIVA,this.msgDiag.getPorta(), this.pddContext,
					this.openspcoopstate.getStatoRichiesta(),this.openspcoopstate.getStatoRisposta(),
					this.dumpConfig);
			if(transazioneApplicativoServer!=null) {
				dumpApplicativoRichiesta.setTransazioneApplicativoServer(transazioneApplicativoServer, this.idPA, this.dataConsegna);
			}
			dumpApplicativoRichiesta.dumpRichiestaUscita(this.consegnaMessageTrasformato, this.outRequestContext.getConnettore());

			
			
			
			
			
			
			
			
			
			
			
			
			
			/* ------------------- 
			   Rilascio Risorse (Le riprendero' dopo aver ottenuto la risposta, se necessario) 
			   Le informazioni nel DB sono state utilizzate fino a questo punto solo in lettura.
			   Eventuali spedizioni JMS sono state effettuate e le risorse gia' rilasciate (non arrivero a questo punto)
			   -----------------------*/
			this.msgDiag.mediumDebug("Rilascio connessione al database...");
			this.openspcoopstate.releaseResource();
			
			
			
			
			
			
			

			// --------------------- spedizione --------------------------
			if(this.invokerNonSupportato==false){
				this.msgDiag.logPersonalizzato("consegnaInCorso");
				// utilizzo connettore
				this.ejbUtils.setSpedizioneMsgIngresso(new Timestamp(this.outRequestContext.getDataElaborazioneMessaggio().getTime()));
				this.dataPrimaInvocazioneConnettore = DateManager.getDate();
				if(this.asyncResponseCallback!=null) {
					//this.errorConsegna = ! 
					//
					// L'errore viene fornito durante l'invocazione dell'asyncComplete
					this.connettoreMsg.setAsyncResponseCallback(this);
					this.connectorSender.send(responseCachingConfig, this.connettoreMsg);
					this.asynWait = true;
				}
				else {
					this.errorConsegna = !this.connectorSender.send(responseCachingConfig, this.connettoreMsg);
				}
			}
			
			if(this.asyncResponseCallback==null || !this.asynWait) {
				return this._complete();
			}
			else {
				return esito; // esito asincrono che non viene utilizzato
			}
		}
		catch(Exception e) {
			return this.doInternalError(e, esito);
		}
		finally {

		}
	}
	
	private EsitoLib _complete() {
		
		EsitoLib esito = new EsitoLib();
		
		try {
			this.dataTerminataInvocazioneConnettore = DateManager.getDate();
						
			
			
			Utilities.printFreeMemory("ConsegnaContenuti - Richiesta risorsa per la gestione della risposta...");
			
			
			
			
			/* ------------  Re-ottengo Connessione al DB -------------- */
			this.msgDiag.mediumDebug("Richiesta risorsa per la gestione della risposta...");
			try{
				boolean gestioneAsincroniStateless = 
					(ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO.equals(this.profiloCollaborazione) ||
							ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO.equals(this.profiloCollaborazione))
							&& this.portaDiTipoStateless;
				boolean oldGestioneConnessione = false;
				if(gestioneAsincroniStateless){
					oldGestioneConnessione = ((OpenSPCoopStateless)this.openspcoopstate).isUseConnection();
					((OpenSPCoopStateless)this.openspcoopstate).setUseConnection(true);
				}
				this.openspcoopstate.updateResource(this.idTransazione);
				if(gestioneAsincroniStateless){
					((OpenSPCoopStateless)this.openspcoopstate).setUseConnection(oldGestioneConnessione);
				}
				
				// Aggiorno risorse
				this.ejbUtils.updateOpenSPCoopState(this.openspcoopstate);
				this.msgRequest.updateOpenSPCoopState(this.openspcoopstate);
				
				// POOL,TRANSACTIONISOLATION:
				//connectionDB.setTransactionIsolation(DBManager.getTransactionIsolationLevel());
			}catch(Exception e){
				this.msgDiag.logErroreGenerico(e,"this.openspcoopstate.updateResource()");
				this.openspcoopstate.releaseResource();
				esito.setEsitoInvocazione(false);	
				esito.setStatoInvocazioneErroreNonGestito(e);
				return esito;
			}
			
			
			
			/* ------------  Analisi Risposta -------------- */
			if(this.invokerNonSupportato==false){
			
				try {
					this.msgDiag.mediumDebug("Analisi Risposta");
				
					// nota per lo stato si intende un esito di errore connettore quando è proprio il connettore a restituire errore.
					// se invece il connettore esce "bene" e restituisce poi un codice http e/o una risposta, si rientra nei casi sottostanti
					this.pddContext.addObject(org.openspcoop2.core.constants.Costanti.ERRORE_UTILIZZO_CONNETTORE, this.errorConsegna);
					
					this.ejbUtils.setRicezioneMsgRisposta(DateManager.getTimestamp());
					this.motivoErroreConsegna = this.connectorSender.getErrore();
					this.eccezioneProcessamentoConnettore = this.connectorSender.getEccezioneProcessamento();
					if(this.errorConsegna && this.motivoErroreConsegna==null){
						this.motivoErroreConsegna = "Errore durante la consegna";
					}
					//	interpretazione esito consegna
					GestoreErroreConnettore gestoreErrore = new GestoreErroreConnettore();
					this.errorConsegna = !gestoreErrore.verificaConsegna(this.gestioneConsegnaConnettore,this.motivoErroreConsegna,this.eccezioneProcessamentoConnettore,this.connectorSender.getCodiceTrasporto(),this.connectorSender.getResponse());
					if(this.errorConsegna){
						this.motivoErroreConsegna = gestoreErrore.getErrore();
						this.riconsegna = gestoreErrore.isRiconsegna();
						this.dataRiconsegna = gestoreErrore.getDataRispedizione();
					}
					// dopo aver verificato se siamo in un caso di errore, vediamo se l'errore è dovuto al codice di trasporto
					// in tal caso rientriamo in un utilizzo del connettore con errore.
					if(this.errorConsegna) {
						if(this.connectorSender.getResponse()==null) {
							this.pddContext.addObject(org.openspcoop2.core.constants.Costanti.ERRORE_UTILIZZO_CONNETTORE, this.errorConsegna);
						}
					}
					if(this.transazioneApplicativoServer!=null) {
						this.transazioneApplicativoServer.setConsegnaTerminata(!this.errorConsegna);
					}
					// raccolta risultati del connettore
					this.soapFault = gestoreErrore.getFault();
					this.restProblem = gestoreErrore.getProblem();
					this.faultMessageFactory = this.connectorSender.getResponse()!=null ? this.connectorSender.getResponse().getFactory() : OpenSPCoop2MessageFactory.getDefaultMessageFactory();
					this.codiceRitornato = this.connectorSender.getCodiceTrasporto();
					this.transportResponseContext = new TransportResponseContext(this.log, this.connectorSender.getCodiceTrasporto()+"", 
							this.connectorSender.getHeaderTrasporto(), 
							this.connectorSender.getContentLength(), 
							this.motivoErroreConsegna, this.connectorSender.getEccezioneProcessamento());
					this.responseMessage = this.connectorSender.getResponse();
					if(this.responseMessage!=null){
						this.responseMessage.setTransportRequestContext(this.consegnaMessagePrimaTrasformazione.getTransportRequestContext());
						this.responseMessage.setTransportResponseContext(this.transportResponseContext);
						this.responseContentLength = this.connectorSender.getContentLength();
					}
					// gestione connessione connettore
					if(this.existsModuloInAttesaRispostaApplicativa) {
						if(this.localForward){
							RepositoryConnettori.salvaConnettorePD(
									//idMessaggioConsegna,
									this.idTransazione,
									this.connectorSender);
						}else{
							RepositoryConnettori.salvaConnettorePA(
									//idMessaggioConsegna, 
									this.idTransazione,
									this.connectorSender);
						}
					}else{
						// Sono nella casistica di messaggio preso in carico.
						// Non si deve chiudere immediatamente la connessione, poiche' nel resto del modulo, il messaggio puo' ancora essere utilizzato (es. dump)
						this.connectorSenderForDisconnect = this.connectorSender;
					}			
					this.msgDiag.addKeyword(CostantiPdD.KEY_CODICE_CONSEGNA, this.codiceRitornato+"");
					if(this.motivoErroreConsegna!=null)
						this.msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_CONSEGNA, this.motivoErroreConsegna);
					
					// Il Connettore potrebbe aggiungere informazioni alla location.
					String tmpLocation = this.connectorSender.getLocation();
					if(tmpLocation!=null){
						
						// salvo la request url originale, se la risposta non è letta dalla cache
						boolean responseCached = false;
						if(this.pddContext.containsKey(ConnettoreBase.RESPONSE_FROM_CACHE)) {
							responseCached = (Boolean) this.pddContext.getObject(ConnettoreBase.RESPONSE_FROM_CACHE);
						}
						if(!responseCached) {
							this.pddContext.addObject(CostantiPdD.CONNETTORE_REQUEST_METHOD, this.httpRequestMethod);
							this.pddContext.addObject(CostantiPdD.CONNETTORE_REQUEST_URL, tmpLocation);
						}
						
						// aggiorno
						this.location = tmpLocation;
						if(responseCached) {
							this.msgDiag.addKeyword(CostantiPdD.KEY_LOCATION, this.location);
						}
						else {
							this.msgDiag.addKeyword(CostantiPdD.KEY_LOCATION, ConnettoreUtils.formatLocation(this.httpRequestMethod, this.location));
						}
					}
				} catch (Exception e) {
					this.msgDiag.addKeywordErroreProcessamento(e, "Analisi risposta fallita");
					this.msgDiag.logErroreGenerico(e,"AnalisiRispostaConnettore");
					String msgErrore = "Analisi risposta del connettore ha provocato un errore: "+e.getMessage();
					this.log.error(msgErrore,e);
					if(this.existsModuloInAttesaRispostaApplicativa) {
						
						this.sendErroreProcessamento(this.localForward, this.localForwardEngine, this.ejbUtils, 
								ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
								get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_500_ERRORE_INTERNO),
								this.idModuloInAttesa, this.bustaRichiesta, this.idCorrelazioneApplicativa, this.idCorrelazioneApplicativaRisposta, this.servizioApplicativoFruitore, e,
								(this.connectorSender!=null && this.connectorSender.getResponse()!=null ? this.connectorSender.getResponse().getParseException() : null),
								this.pddContext);
						
						esito.setEsitoInvocazione(true); 
						esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO, msgErrore);
					}else{
						this.ejbUtils.rollbackMessage(msgErrore,this.servizioApplicativo, esito);
						esito.setEsitoInvocazione(false); 
						esito.setStatoInvocazioneErroreNonGestito(e);
					}
					this.openspcoopstate.releaseResource();
					return esito;
				}
			}




			
			
			
			
			
			
			
			/* ------------ Trasformazione Risposta  -------------- */
			
			boolean dumpRispostaEffettuato = false;
			if(this.trasformazioni!=null && this.responseMessage!=null
					&& this.transazioneApplicativoServer==null // non ha senso per le notifiche asincrone
					) {
				try {
					
					// prima effettuo dump applicativo
					if(this.responseMessage!=null ){
						
						String idMessaggioDump = this.idMessaggioConsegna;
						if(this.idMessaggioPreBehaviour!=null){
							idMessaggioDump = this.idMessaggioPreBehaviour;
						}
						
						Dump dumpApplicativo = new Dump(this.identitaPdD,ConsegnaContenutiApplicativi.ID_MODULO,idMessaggioDump,
								this.soggettoFruitore,this.idServizio,TipoPdD.APPLICATIVA,this.msgDiag.getPorta(), this.pddContext,
								this.openspcoopstate.getStatoRichiesta(),this.openspcoopstate.getStatoRisposta(),
								this.dumpConfig);
						/*if(transazioneApplicativoServer!=null) {
							dumpApplicativo.setTransazioneApplicativoServer(transazioneApplicativoServer, idPA, dataConsegna);
						}*/
						InfoConnettoreUscita infoConnettoreUscita = this.outRequestContext.getConnettore();
						if(infoConnettoreUscita!=null){
							infoConnettoreUscita.setLocation(this.location); // aggiorno location ottenuta dal connettore utilizzato
						}
						dumpApplicativo.dumpRispostaIngresso(this.responseMessage, infoConnettoreUscita, this.connectorSender.getHeaderTrasporto());
						dumpRispostaEffettuato = true;
					}
					
					MessageType messageTypePrimaTrasformazione = this.responseMessage.getMessageType();
					
					this.responseMessage = this.gestoreTrasformazioni.trasformazioneRisposta(this.responseMessage, this.bustaRichiesta);
					
					MessageType messageTypeDopoTrasformazione = (this.responseMessage!=null)  ? this.responseMessage.getMessageType() : null;
					if(messageTypeDopoTrasformazione==null || messageTypePrimaTrasformazione.equals(messageTypeDopoTrasformazione)==false) {
						this.soapFault = null;
						this.restProblem = null;
						if(messageTypeDopoTrasformazione!=null) {
							if(this.responseMessage instanceof OpenSPCoop2SoapMessage){
								if(this.responseMessage.castAsSoap().hasSOAPFault()){
									SOAPBody body = this.responseMessage.castAsSoap().getSOAPBody();
									this.soapFault = body.getFault();
								}
							}
							else {
								if(this.responseMessage instanceof OpenSPCoop2RestJsonMessage ){
									OpenSPCoop2RestJsonMessage msg = this.responseMessage.castAsRestJson();
									if(msg.hasContent() && msg.isProblemDetailsForHttpApis_RFC7807()) {
										JsonDeserializer deserializer = new JsonDeserializer();
										this.restProblem = deserializer.fromString(msg.getContent(), false);
									}
								}
								else if(this.responseMessage instanceof OpenSPCoop2RestXmlMessage ){
									OpenSPCoop2RestXmlMessage msg = this.responseMessage.castAsRestXml();
									if(msg.hasContent() && msg.isProblemDetailsForHttpApis_RFC7807()) {
										XmlDeserializer deserializer = new XmlDeserializer();
										this.restProblem = deserializer.fromNode(msg.getContent(), false);
									}
								}
							}
						}
					}
				}
				catch(Throwable e) {
					
					this.pddContext.addObject(org.openspcoop2.core.constants.Costanti.ERRORE_TRASFORMAZIONE_RISPOSTA, "true");
					
					// prima emetto diagnostico di fine connettore
					emitDiagnostico();

					
					this.msgDiag.addKeywordErroreProcessamento(e);
					this.msgDiag.logPersonalizzato("trasformazione.processamentoRispostaInErrore");
					
					ErroreIntegrazione erroreIntegrazione = this.gestoreTrasformazioni.getErrore();
					if(erroreIntegrazione==null) {
						erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
								get5XX_ErroreProcessamento(e,CodiceErroreIntegrazione.CODICE_562_TRASFORMAZIONE);
					}
					
					String msgErrore = e.getMessage();					
					if(this.existsModuloInAttesaRispostaApplicativa) {
						
						IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.TRANSFORMATION_RULE_RESPONSE_FAILED;
						if(e instanceof GestoreTrasformazioniException && (((GestoreTrasformazioniException)e).getOp2IntegrationFunctionError()!=null)) {
							integrationFunctionError = ((GestoreTrasformazioniException)e).getOp2IntegrationFunctionError();
						}
						this.ejbUtils.setIntegrationFunctionErrorPortaApplicativa(integrationFunctionError);
						if(this.localForwardEngine!=null) {
							this.localForwardEngine.setIntegrationFunctionError(integrationFunctionError);
						}
						
						if(e instanceof GestoreTrasformazioniException && (((GestoreTrasformazioniException)e).getOpenSPCoop2ErrorMessage()!=null)) {
							this.sendErroreProcessamento(this.localForward, this.localForwardEngine, this.ejbUtils, 
									((GestoreTrasformazioniException)e).getOpenSPCoop2ErrorMessage(), msgErrore,
									this.idModuloInAttesa, this.bustaRichiesta, this.idCorrelazioneApplicativa, this.idCorrelazioneApplicativaRisposta, this.servizioApplicativoFruitore);
						}
						else {
							this.sendErroreProcessamento(this.localForward, this.localForwardEngine, this.ejbUtils, 
								erroreIntegrazione,
								this.idModuloInAttesa, this.bustaRichiesta, this.idCorrelazioneApplicativa, this.idCorrelazioneApplicativaRisposta, this.servizioApplicativoFruitore, e,
								(this.connectorSender!=null && this.connectorSender.getResponse()!=null ? this.connectorSender.getResponse().getParseException() : null),
								this.pddContext);
						}
						
						esito.setEsitoInvocazione(true); 
						esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO, msgErrore);
					}else{
						this.ejbUtils.rollbackMessage(msgErrore,this.servizioApplicativo, esito);
						esito.setEsitoInvocazione(false); 
						esito.setStatoInvocazioneErroreNonGestito(e);
					}
					this.openspcoopstate.releaseResource();
					return esito;
				}		
			}
			
			
			
			
			
			
			
			
	

			
			/* -------- OpenSPCoop2Message Update ------------- */
			try {
				if(this.transazioneApplicativoServer==null) { // non ha senso per le notifiche asincrone
					this.msgDiag.mediumDebug("Aggiornamento del messaggio");
					// NOTA la versione SOAP capirla da consegnaMessage, la risposta puo' essere null
					NotifierInputStreamParams nParams = null;
					if(this.invokerNonSupportato==false){
						nParams = this.connectorSender.getNotifierInputStreamParamsResponse();
					}
					this.responseMessage = this.protocolFactory.createProtocolManager().updateOpenSPCoop2MessageResponse(this.responseMessage, 
							this.bustaRichiesta, nParams,
							this.consegnaMessagePrimaTrasformazione.getTransportRequestContext(),this.transportResponseContext,
							this.protocolFactory.getCachedRegistryReader(this.openspcoopstate.getStatoRichiesta()),
							true);
				}
			} catch (Exception e) {
				this.msgDiag.addKeywordErroreProcessamento(e, "Aggiornamento messaggio fallito");
				this.msgDiag.logErroreGenerico(e,"ProtocolManager.updateOpenSPCoop2Message");
				String msgErrore = "ProtocolManager.updateOpenSPCoop2Message error: "+e.getMessage();
				this.log.error(msgErrore,e);
				if(this.existsModuloInAttesaRispostaApplicativa) {
					
					this.sendErroreProcessamento(this.localForward, this.localForwardEngine, this.ejbUtils, 
							ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_525_GESTIONE_FUNZIONALITA_PROTOCOLLO),
							this.idModuloInAttesa, this.bustaRichiesta, this.idCorrelazioneApplicativa, this.idCorrelazioneApplicativaRisposta, this.servizioApplicativoFruitore, e,
							(this.responseMessage!=null ? this.responseMessage.getParseException() : null),
							this.pddContext);
					
					esito.setEsitoInvocazione(true); 
					esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO, msgErrore);
				}else{
					this.ejbUtils.rollbackMessage(msgErrore,this.servizioApplicativo, esito);
					esito.setEsitoInvocazione(false); 
					esito.setStatoInvocazioneErroreNonGestito(e);
				}
				this.openspcoopstate.releaseResource();
				return esito;
			}
			
			
			
			
			
			
			
			
			
			
			
			/* ------------------- InResponseHandler -----------------------*/
			InResponseContext inResponseContext = null;
			if(this.invokerNonSupportato==false){
				try{
					inResponseContext = new InResponseContext(this.log, this.protocolFactory,this.openspcoopstate.getStatoRisposta());
					
					// Informazioni sul messaggio di riposta
					if(this.responseMessage!=null){
						inResponseContext.setMessaggio(this.responseMessage);				
					}
					
					// TransazioneApplicativoServer
					inResponseContext.setTransazioneApplicativoServer(this.transazioneApplicativoServer);
					
					// Informazioni sulla consegna
					inResponseContext.setErroreConsegna(this.motivoErroreConsegna);
					inResponseContext.setResponseHeaders(this.connectorSender.getHeaderTrasporto());
					inResponseContext.setReturnCode(this.codiceRitornato);
					
					// Altre informazioni
					if(this.outRequestContext.getConnettore()!=null){
						this.outRequestContext.getConnettore().setLocation(this.location); // aggiorno location ottenuta dal connettore utilizzato
					}
					inResponseContext.setConnettore(this.outRequestContext.getConnettore());
					inResponseContext.setDataPrimaInvocazioneConnettore(this.dataPrimaInvocazioneConnettore);
					inResponseContext.setDataTerminataInvocazioneConnettore(this.dataTerminataInvocazioneConnettore);
					inResponseContext.setDataAccettazioneRisposta(this.connectorSender.getDataAccettazioneRisposta());
					inResponseContext.setDataElaborazioneMessaggio(this.ejbUtils.getRicezioneMsgRisposta());
					inResponseContext.setProtocollo(this.outRequestContext.getProtocollo());
					inResponseContext.setPddContext(this.pddContext);
					inResponseContext.setIntegrazione(this.outRequestContext.getIntegrazione());
					inResponseContext.setTipoPorta(this.outRequestContext.getTipoPorta());
					
					// Invocazione handler
					GestoreHandlers.inResponse(inResponseContext, this.msgDiag, this.log);
					
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
							this.msgDiag.logErroreGenerico(e, ((HandlerException)e).getIdentitaHandler());
						}
						msgErrore = ((HandlerException)e).getIdentitaHandler()+" error: "+msgErrore;
						if(this.existsModuloInAttesaRispostaApplicativa) {
							erroreIntegrazione = he.convertToErroreIntegrazione();
							integrationFunctionError = he.getIntegrationFunctionError();
						}
					}else{
						this.msgDiag.logErroreGenerico(e, "InResponseHandler");
						msgErrore = "InResponseHandler error: "+msgErrore;
					}
					if(this.existsModuloInAttesaRispostaApplicativa) {
						
						if(erroreIntegrazione==null){
							erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
									get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_544_HANDLER_IN_RESPONSE);
						}
						
						if(integrationFunctionError!=null) {
							this.ejbUtils.setIntegrationFunctionErrorPortaApplicativa(integrationFunctionError);
							if(this.localForwardEngine!=null) {
								this.localForwardEngine.setIntegrationFunctionError(integrationFunctionError);
							}
						}
						this.sendErroreProcessamento(this.localForward, this.localForwardEngine, this.ejbUtils, 
								erroreIntegrazione,
								this.idModuloInAttesa, this.bustaRichiesta, this.idCorrelazioneApplicativa, this.idCorrelazioneApplicativaRisposta, this.servizioApplicativoFruitore, e,
								(this.responseMessage!=null ? this.responseMessage.getParseException() : null),
								this.pddContext);
						
						esito.setEsitoInvocazione(true); 
						esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO, msgErrore);
					}else{
						this.ejbUtils.rollbackMessage(msgErrore,this.servizioApplicativo, esito);
						esito.setEsitoInvocazione(false); 
						esito.setStatoInvocazioneErroreNonGestito(e);
					}
					this.openspcoopstate.releaseResource();
					return esito;
				}
			}
			
			
			
			
			
			
			
			
			
			
			
			
			/* ------------ Controllo che il messaggio non contenga una busta */
			this.msgDiag.mediumDebug("Controllo non esistenza di una busta ...");
			ValidazioneSintattica validatoreSintattico = new ValidazioneSintattica(this.pddContext, this.openspcoopstate.getStatoRichiesta(),this.responseMessage, this.protocolFactory);
			String msgErrore = null;
			try{
				if(validatoreSintattico.verifyProtocolPresence(this.tipoPdD,this.profiloCollaborazione,RuoloMessaggio.RISPOSTA)){
					throw new Exception("Rilevato ProtocolHeader nella risposta");
				}
			} catch (Exception e){
				this.msgDiag.logPersonalizzato("rispostaContenenteBusta");
				this.log.error("CheckProtocolPresence",e);
				if(this.existsModuloInAttesaRispostaApplicativa) {
					
					this.ejbUtils.setIntegrationFunctionErrorPortaApplicativa(IntegrationFunctionError.INTEROPERABILITY_PROFILE_RESPONSE_ALREADY_EXISTS);
					if(this.localForwardEngine!=null) {
						this.localForwardEngine.setIntegrationFunctionError(IntegrationFunctionError.INTEROPERABILITY_PROFILE_RESPONSE_ALREADY_EXISTS);
					}
					this.sendErroreProcessamento(this.localForward, this.localForwardEngine, this.ejbUtils, 
							ErroriIntegrazione.ERRORE_454_BUSTA_PRESENTE_RISPOSTA_APPLICATIVA.getErroreIntegrazione(),
							this.idModuloInAttesa, this.bustaRichiesta, this.idCorrelazioneApplicativa, this.idCorrelazioneApplicativaRisposta, this.servizioApplicativoFruitore, e,
							(this.responseMessage!=null ? this.responseMessage.getParseException() : null),
							this.pddContext);
					
					esito.setEsitoInvocazione(true); 
					esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
							this.msgDiag.getMessaggio_replaceKeywords("rispostaContenenteBusta"));
				}else{
					this.ejbUtils.rollbackMessage(msgErrore,this.servizioApplicativo, esito);
					esito.setEsitoInvocazione(false); 
					esito.setStatoInvocazioneErroreNonGestito(e);
				}
				this.openspcoopstate.releaseResource();
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
			if(this.invokerNonSupportato==false){
				
				// Leggo informazioni di trasporto
				this.codiceRitornato = inResponseContext.getReturnCode();
				this.motivoErroreConsegna = inResponseContext.getErroreConsegna();
				this.responseMessage = inResponseContext.getMessaggio();
				
				String idMessaggioDump = this.idMessaggioConsegna;
				if(this.idMessaggioPreBehaviour!=null){
					idMessaggioDump = this.idMessaggioPreBehaviour;
				}
				
				//	dump applicativo
				if(!dumpRispostaEffettuato && this.responseMessage!=null ){
					Dump dumpApplicativo = new Dump(this.identitaPdD,ConsegnaContenutiApplicativi.ID_MODULO,idMessaggioDump,
							this.soggettoFruitore,this.idServizio,TipoPdD.APPLICATIVA,this.msgDiag.getPorta(), this.pddContext,
							this.openspcoopstate.getStatoRichiesta(),this.openspcoopstate.getStatoRisposta(),
							this.dumpConfig);
					if(this.transazioneApplicativoServer!=null) {
						dumpApplicativo.setTransazioneApplicativoServer(this.transazioneApplicativoServer, this.idPA, this.dataConsegna);
					}
					dumpApplicativo.dumpRispostaIngresso(this.responseMessage, inResponseContext.getConnettore(), inResponseContext.getResponseHeaders());
				}
				
			}
			
			
			
			
			





			/* ------------------- MsgDiagnostico -----------------------*/
			
			emitDiagnostico();










			/* ------------------------- Gestione Errori Consegna ---------------------------- */
			this.msgDiag.mediumDebug("Gestione errore consegna della risposta...");
			// Invoker Non Supportato
			if( this.invokerNonSupportato == true  ){
				String msgErroreConnettoreNonSupportato = "Connettore non supportato [tipo:"+this.tipoConnector+" class:"+this.connectorClass+"]";
				if(this.existsModuloInAttesaRispostaApplicativa) {
					
					this.sendErroreProcessamento(this.localForward, this.localForwardEngine, this.ejbUtils, 
							ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
								get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_515_CONNETTORE_NON_REGISTRATO),
							this.idModuloInAttesa, this.bustaRichiesta, this.idCorrelazioneApplicativa, this.idCorrelazioneApplicativaRisposta, this.servizioApplicativoFruitore, this.eInvokerNonSupportato,
							(this.responseMessage!=null ? this.responseMessage.getParseException() : null),
							this.pddContext);
					
					esito.setEsitoInvocazione(true); 
					esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO, msgErroreConnettoreNonSupportato);
				}else{
					this.ejbUtils.rollbackMessage(msgErroreConnettoreNonSupportato,this.servizioApplicativo, esito);
					esito.setEsitoInvocazione(false); 
					esito.setStatoInvocazione(EsitoLib.ERRORE_NON_GESTITO, msgErroreConnettoreNonSupportato);
				}
				this.openspcoopstate.releaseResource();
				return esito;

			}
			//	Errori avvenuti durante la consegna
			else if(this.errorConsegna){
						
				// Effettuo log dell'eventuale fault
				if(this.soapFault!=null && 
						( 
						  (this.motivoErroreConsegna==null) ||
						  (!this.motivoErroreConsegna.toLowerCase().contains("faultCode") && !this.motivoErroreConsegna.toLowerCase().contains("faultActor") && !this.motivoErroreConsegna.toLowerCase().contains("faultString"))
					    ) 
					){
					// Se non l'ho gia indicato nel motivo di errore, registro il fault
					this.msgDiag.addKeyword(CostantiPdD.KEY_SOAP_FAULT, SoapUtils.safe_toString(this.faultMessageFactory, this.soapFault, this.log));
					this.msgDiag.logPersonalizzato("ricezioneSoapFault");
				}
				else if(this.restProblem!=null && 
						( 
						  (this.motivoErroreConsegna==null) ||
						  (!this.motivoErroreConsegna.toLowerCase().contains(ProblemConstants.CLAIM_TYPE))
					    ) 
					){
					// Se non l'ho gia indicato nel motivo di errore, registro il fault
					this.msgDiag.addKeyword(CostantiPdD.KEY_REST_PROBLEM, this.restProblem.getRaw());
					this.msgDiag.logPersonalizzato("ricezioneRestProblem");
				}
				else{
					
					// Controllo Situazione Anomala ISSUE OP-7
					if(this.responseMessage!=null && ServiceBinding.SOAP.equals(this.responseMessage.getServiceBinding())){
						OpenSPCoop2SoapMessage soapMessageResponse = this.responseMessage.castAsSoap();
						if(soapMessageResponse.getSOAPPart()!=null && 
								soapMessageResponse.getSOAPPart().getEnvelope()!=null &&
								(soapMessageResponse.isSOAPBodyEmpty() || (!soapMessageResponse.hasSOAPFault()) )
								) {
							this.msgDiag.logPersonalizzato("comportamentoAnomalo.erroreConsegna.ricezioneMessaggioDiversoFault");
							if(this.isBlockedTransaction_responseMessageWithTransportCodeError){
								String msgErroreSituazioneAnomale = this.msgDiag.getMessaggio_replaceKeywords("comportamentoAnomalo.erroreConsegna.ricezioneMessaggioDiversoFault");
								if(this.existsModuloInAttesaRispostaApplicativa) {
									
									this.ejbUtils.setIntegrationFunctionErrorPortaApplicativa(IntegrationFunctionError.BAD_RESPONSE);
									if(this.localForwardEngine!=null) {
										this.localForwardEngine.setIntegrationFunctionError(IntegrationFunctionError.BAD_RESPONSE);
									}
									this.sendErroreProcessamento(this.localForward, this.localForwardEngine, this.ejbUtils, 
											ErroriIntegrazione.ERRORE_559_RICEVUTA_RISPOSTA_CON_ERRORE_TRASPORTO.
												get559_RicevutaRispostaConErroreTrasporto(msgErroreSituazioneAnomale),
											this.idModuloInAttesa, this.bustaRichiesta, this.idCorrelazioneApplicativa, this.idCorrelazioneApplicativaRisposta, this.servizioApplicativoFruitore, this.eInvokerNonSupportato,
											(this.responseMessage!=null ? this.responseMessage.getParseException() : null),
											this.pddContext);
									
									esito.setEsitoInvocazione(true); 
									esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO, msgErroreSituazioneAnomale);
								}else{
									this.ejbUtils.rollbackMessage(msgErroreSituazioneAnomale,this.servizioApplicativo, esito);
									esito.setEsitoInvocazione(false); 
									esito.setStatoInvocazione(EsitoLib.ERRORE_NON_GESTITO, msgErroreSituazioneAnomale);
								}
								this.openspcoopstate.releaseResource();
								return esito;
							}
						}
					}
				}
				
				String messaggioErroreConsegnaConnettore = "Consegna ["+this.tipoConnector+"] con errore: "+this.motivoErroreConsegna;
				if(this.existsModuloInAttesaRispostaApplicativa) {
					OpenSPCoop2Message connettoreMsgRequest = this.connettoreMsg.getRequestMessage(this.requestInfo, this.pddContext);
					String requestReadTimeout = null;
					String responseReadTimeout = null;
					if(this.pddContext!=null && this.pddContext.containsKey(TimeoutInputStream.ERROR_MSG_KEY)) {
						String timeoutMessage = PdDContext.getValue(TimeoutInputStream.ERROR_MSG_KEY, this.pddContext);
						if(timeoutMessage!=null && timeoutMessage.startsWith(CostantiPdD.PREFIX_TIMEOUT_REQUEST)) {
							requestReadTimeout = timeoutMessage;
						}
						else if(timeoutMessage!=null && timeoutMessage.startsWith(CostantiPdD.PREFIX_TIMEOUT_RESPONSE)) {
							responseReadTimeout = timeoutMessage;
						}
					}
					String requestLimitExceeded = null;
					String responseLimitExceeded = null;
					if(this.pddContext!=null && this.pddContext.containsKey(LimitedInputStream.ERROR_MSG_KEY)) {
						String limitedExceededMessage = PdDContext.getValue(LimitedInputStream.ERROR_MSG_KEY, this.pddContext);
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
							tParsing = (TimeoutIOException) this.pddContext.getObject(TimeoutInputStream.EXCEPTION_KEY);
							errorMsg = tParsing.getMessage();
						}
						else if(requestLimitExceeded != null) {
							tParsing = (LimitExceededIOException) this.pddContext.getObject(LimitedInputStream.EXCEPTION_KEY);
							errorMsg = tParsing.getMessage();
						}
						else {
							parseException = connettoreMsgRequest.getParseException();
							tParsing = parseException.getParseException();
							errorMsg = tParsing.getMessage();
						}
						
						this.pddContext.addObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RICHIESTA_NON_RICONOSCIUTO, true);
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
						this.ejbUtils.setIntegrationFunctionErrorPortaApplicativa(integrationFunctionError);
						if(this.localForwardEngine!=null) {
							this.localForwardEngine.setIntegrationFunctionError(integrationFunctionError);
						}
						this.sendErroreProcessamento(this.localForward, this.localForwardEngine, this.ejbUtils, 
								ErroriIntegrazione.ERRORE_432_PARSING_EXCEPTION_RICHIESTA.getErrore432_MessaggioRichiestaMalformato(tParsing),
								this.idModuloInAttesa, this.bustaRichiesta, this.idCorrelazioneApplicativa, this.idCorrelazioneApplicativaRisposta, this.servizioApplicativoFruitore, 
								tParsing,parseException,
								this.pddContext);
						
						this.openspcoopstate.releaseResource();
						esito.setEsitoInvocazione(true); 
						esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,errorMsg);
						return esito;
					} else if(this.responseMessage==null && 
							!this.pddContext.containsKey(org.openspcoop2.core.constants.Costanti.CONTENUTO_RISPOSTA_NON_RICONOSCIUTO_PARSE_EXCEPTION)){
						// Genero una risposta di errore
						IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.SERVICE_UNAVAILABLE;
						if(this.eccezioneProcessamentoConnettore!=null && this.motivoErroreConsegna!=null) {
							if(responseReadTimeout!=null) {
								integrationFunctionError = IntegrationFunctionError.ENDPOINT_REQUEST_TIMED_OUT;
								this.pddContext.removeObject(TimeoutInputStream.ERROR_MSG_KEY);
								this.pddContext.removeObject(TimeoutInputStream.EXCEPTION_KEY);
							}
							else if(responseLimitExceeded!=null) {
								integrationFunctionError = IntegrationFunctionError.RESPONSE_SIZE_EXCEEDED;
								this.pddContext.removeObject(LimitedInputStream.ERROR_MSG_KEY);
								this.pddContext.removeObject(LimitedInputStream.EXCEPTION_KEY);
							}
							else if(this.propertiesReader.isServiceUnavailable_ReadTimedOut(this.motivoErroreConsegna)){
								integrationFunctionError = IntegrationFunctionError.ENDPOINT_REQUEST_TIMED_OUT;
							}
						}
						this.ejbUtils.setIntegrationFunctionErrorPortaApplicativa(integrationFunctionError);
						if(this.localForwardEngine!=null) {
							this.localForwardEngine.setIntegrationFunctionError(integrationFunctionError);
						}
						this.sendErroreProcessamento(this.localForward, this.localForwardEngine, this.ejbUtils, 
								ErroriIntegrazione.ERRORE_516_CONNETTORE_UTILIZZO_CON_ERRORE.get516_ServizioApplicativoNonDisponibile(),
								this.idModuloInAttesa, this.bustaRichiesta, this.idCorrelazioneApplicativa, this.idCorrelazioneApplicativaRisposta, this.servizioApplicativoFruitore, this.eccezioneProcessamentoConnettore,
								(this.responseMessage!=null ? this.responseMessage.getParseException() : null),
								this.pddContext);
					
						this.openspcoopstate.releaseResource();
						esito.setEsitoInvocazione(true); 
						esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,messaggioErroreConsegnaConnettore);
						return esito;
					} else if(this.pddContext.containsKey(org.openspcoop2.core.constants.Costanti.CONTENUTO_RISPOSTA_NON_RICONOSCIUTO_PARSE_EXCEPTION) ||
							this.responseMessage.getParseException() != null ||
							responseReadTimeout!=null ||
							responseLimitExceeded!=null){
						
						ParseException parseException = null;
						Throwable tParsing = null;
						String errorMsg = null;
						if(responseReadTimeout != null) {
							tParsing = (TimeoutIOException) this.pddContext.getObject(TimeoutInputStream.EXCEPTION_KEY);
							errorMsg = tParsing.getMessage();
						}
						else if(responseLimitExceeded != null) {
							tParsing = (LimitExceededIOException) this.pddContext.getObject(LimitedInputStream.EXCEPTION_KEY);
							errorMsg = tParsing.getMessage();
						}
						else if(this.pddContext.containsKey(org.openspcoop2.core.constants.Costanti.CONTENUTO_RISPOSTA_NON_RICONOSCIUTO_PARSE_EXCEPTION)){
							parseException = (ParseException) this.pddContext.getObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RISPOSTA_NON_RICONOSCIUTO_PARSE_EXCEPTION);
							tParsing = parseException.getParseException();
							errorMsg = tParsing.getMessage();
						}
						else {
							parseException = this.responseMessage.getParseException();
							tParsing = parseException.getParseException();
							errorMsg = tParsing.getMessage();
						}
						
						this.pddContext.addObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RISPOSTA_NON_RICONOSCIUTO, true);
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
						this.ejbUtils.setIntegrationFunctionErrorPortaApplicativa(integrationFunctionError);
						if(this.localForwardEngine!=null) {
							this.localForwardEngine.setIntegrationFunctionError(integrationFunctionError);
						}
						this.sendErroreProcessamento(this.localForward, this.localForwardEngine, this.ejbUtils, 
								ErroriIntegrazione.ERRORE_440_PARSING_EXCEPTION_RISPOSTA.getErrore440_MessaggioRispostaMalformato(tParsing),
								this.idModuloInAttesa, this.bustaRichiesta, this.idCorrelazioneApplicativa, this.idCorrelazioneApplicativaRisposta, this.servizioApplicativoFruitore, 
								tParsing,parseException,
								this.pddContext);
						
						this.openspcoopstate.releaseResource();
						esito.setEsitoInvocazione(true); 
						esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,errorMsg);
						return esito;
					}
					
				}else {
					if(this.riconsegna){
						this.ejbUtils.rollbackMessage(messaggioErroreConsegnaConnettore,this.dataRiconsegna,this.servizioApplicativo, esito);
						esito.setEsitoInvocazione(false); 
						esito.setStatoInvocazione(EsitoLib.ERRORE_NON_GESTITO,messaggioErroreConsegnaConnettore);
					}else{
						this.ejbUtils.releaseInboxMessage(true); 
						esito.setEsitoInvocazione(true); 
						esito.setStatoInvocazione(EsitoLib.ERRORE_NON_GESTITO,messaggioErroreConsegnaConnettore);
					}
					this.openspcoopstate.releaseResource();
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
			
			this.msgDiag.mediumDebug("Registrazione eventuale fault...");
			// Effettuo log del fault
			if(this.soapFault!=null){
				this.msgDiag.addKeyword(CostantiPdD.KEY_SOAP_FAULT, SoapUtils.safe_toString(this.faultMessageFactory, this.soapFault, this.log));
				this.msgDiag.logPersonalizzato("ricezioneSoapFault");
			}
			else if(this.restProblem!=null){
				// Se non l'ho gia indicato nel motivo di errore, registro il fault
				this.msgDiag.addKeyword(CostantiPdD.KEY_REST_PROBLEM, this.restProblem.getRaw());
				this.msgDiag.logPersonalizzato("ricezioneRestProblem");
			}
			
			if(this.existsModuloInAttesaRispostaApplicativa){

				if( !this.localForward && this.richiestaApplicativa!=null && Costanti.SCENARIO_ONEWAY_INVOCAZIONE_SERVIZIO.equals(this.richiestaApplicativa.getScenario())){

					// Oneway in modalita sincrona

					boolean returnProtocolReply = false;		
					Busta bustaHTTPReply = null;
					
					boolean consegnaAffidabile = false;
					switch (this.protocolManager.getConsegnaAffidabile(this.bustaIndicazioneConsegnaInOrdine)) {
					case ABILITATA:
						consegnaAffidabile = true;
						break;
					case DISABILITATA:
						consegnaAffidabile = false;
						break;
					default:
						consegnaAffidabile = this.bustaRichiesta.isConfermaRicezione() &&
							this.propertiesReader.isGestioneRiscontri(this.implementazionePdDMittente);
						break;
					}
					
					if(consegnaAffidabile){
						this.msgDiag.mediumDebug("Gestione eventuali riscontri da inviare...");
						if(ProfiloDiCollaborazione.ONEWAY.equals(this.bustaRichiesta.getProfiloDiCollaborazione()) ) {	
							TipoOraRegistrazione tipoOraRegistrazione = this.propertiesReader.getTipoTempoBusta(this.implementazionePdDMittente);
							bustaHTTPReply = this.bustaRichiesta.invertiBusta(tipoOraRegistrazione,
									this.protocolFactory.createTraduttore().toString(tipoOraRegistrazione));

							// Riscontro ad hoc
							Riscontro r = new Riscontro();
							r.setID(this.bustaRichiesta.getID());
							r.setOraRegistrazione(bustaHTTPReply.getOraRegistrazione());
							r.setTipoOraRegistrazione(this.propertiesReader.getTipoTempoBusta(this.implementazionePdDMittente));
							bustaHTTPReply.addRiscontro(r);
							bustaHTTPReply.setTipoServizioRichiedenteBustaDiServizio(this.bustaRichiesta.getTipoServizio());
							bustaHTTPReply.setServizioRichiedenteBustaDiServizio(this.bustaRichiesta.getServizio());
							bustaHTTPReply.setVersioneServizioRichiedenteBustaDiServizio(this.bustaRichiesta.getVersioneServizio());
							bustaHTTPReply.setAzioneRichiedenteBustaDiServizio(this.bustaRichiesta.getAzione());

							returnProtocolReply = true;
						}
					}

					if( returnProtocolReply == false){

						if(this.soapFault!=null || this.restProblem!=null){
							// Devo ritornare il SoapFault
							this.msgDiag.mediumDebug("Invio messaggio di fault a Ricezione/Consegna ContenutiApplicativi...");
							this.msgResponse = this.ejbUtils.sendSOAPFault(this.richiestaApplicativa.getIdModuloInAttesa(),this.responseMessage);
						}
						else{
							// Invio sblocco se e' attesa una risposta dal modulo
							// Se non e' abilitato la risposta su di una nuova connessione, e l'indirizzo telematico 
							// non e' abilitato o cmq non presente, allora devo inviare lo sblocco
							this.msgDiag.mediumDebug("Invio messaggio di sblocco a RicezioneBuste...");
							this.msgResponse = this.ejbUtils.sendSbloccoRicezioneBuste(this.richiestaApplicativa.getIdModuloInAttesa());
						}
					}
					else{

						// Invio risposta immediata in seguito alla richiesta ricevuta
						if(this.soapFault!=null || this.restProblem!=null){
							// Devo ritornare il SoapFault
							this.msgDiag.mediumDebug("Invio messaggio di fault a Ricezione/Consegna ContenutiApplicativi...");
							this.msgResponse = this.ejbUtils.sendSOAPFault(this.richiestaApplicativa.getIdModuloInAttesa(),this.responseMessage);
						}
						else{
							this.msgDiag.mediumDebug("Invio messaggio a Ricezione/Consegna ContenutiApplicativi...");
							this.msgResponse = this.ejbUtils.buildAndSendBustaRisposta(this.richiestaApplicativa.getIdModuloInAttesa(),bustaHTTPReply,
									MessageUtilities.buildEmptyMessage(OpenSPCoop2MessageFactory.getDefaultMessageFactory(),
											this.requestInfo.getProtocolRequestMessageType(), MessageRole.RESPONSE),this.profiloGestione,
									this.idCorrelazioneApplicativa,this.idCorrelazioneApplicativaRisposta,this.servizioApplicativoFruitore);
						}
					}
				}
				else{
					/*-- analisi risposta per riferimento -- */
					if(this.rispostaPerRiferimento && !this.errorConsegna){
						this.msgDiag.mediumDebug("Gestione risposta per riferimento...");
						boolean rilasciaRisorsa = false;
						if(this.openspcoopstate instanceof OpenSPCoopStateless){
							OpenSPCoopStateless state = (OpenSPCoopStateless) this.openspcoopstate;
							// La risposta per riferimento ha bisogno della connessione.
							if (state.resourceReleased()){
								// Risorsa e' rilasciata se siamo in RinegoziamentoConnessione
								state.setUseConnection(true);
								state.updateResource(this.idTransazione);
								rilasciaRisorsa = true;
							}else{
								// Non siamo in RinegoziamentoConnessione, basta dire di utilizzare la connessione
								state.setUseConnection(true);
							}
						}
						try{
							if(MessageType.SOAP_11.equals(this.responseMessage.getMessageType())==false){
								throw new Exception("Tipo di messaggio ["+this.responseMessage.getMessageType()+"] non supportato");
							}
							
							// 1. Read IDMessaggio
							// L'id del messaggio deve essere prelevato dal messaggio di risposta ritornato dal gestore eventi.
							Node prelevaMessaggioResponse = this.responseMessage.castAsSoap().getSOAPBody().getFirstChild();
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
							GestoreMessaggi gestoreMsgFromRepository = new GestoreMessaggi(this.openspcoopstate, false, idMessaggioPresenteNelRepository,Costanti.INBOX,this.msgDiag,this.pddContext);
							OpenSPCoop2Message msgFromRepository = gestoreMsgFromRepository.getMessage();
							//if(idMessaggioPresenteNelRepository==null)
							//	throw new Exception("Messaggio non presente nel repository");

							// 3. prendo body applicativo
							byte[] bodyApplicativoPrecedentementePubblicato = TunnelSoapUtils.sbustamentoMessaggio(msgFromRepository);

							// 4. Inserimento dei byte del body applicativo al posto dell'ID,
							//    nel msg ritornato dal GestoreEventi.
							//    La variabile responseMessage deve contenere il messaggio Soap che sara' ritornato a chi ha richiesto un messaggio
							this.responseMessage = gestoreMsgFromRepository.buildRispostaPrelevamentoMessaggio_RepositoryMessaggi(bodyApplicativoPrecedentementePubblicato,msgFromRepository.getMessageType());
							
						}catch(Exception e){
							this.msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_CONSEGNA, ("risposta per riferimento non costruita, "+e.getMessage()));
							if(this.bustaRichiesta.getMittente()!=null && this.bustaRichiesta.getTipoMittente()!=null){
								this.msgDiag.logPersonalizzato("consegnaConErrore");
							}
							else{
								this.msgDiag.logPersonalizzato("consegnaConErrore.mittenteAnonimo");
							}
							
							this.sendErroreProcessamento(this.localForward, this.localForwardEngine, this.ejbUtils, 
									ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
										get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_511_READ_RESPONSE_MSG),
									this.idModuloInAttesa, this.bustaRichiesta, this.idCorrelazioneApplicativa, this.idCorrelazioneApplicativaRisposta, this.servizioApplicativoFruitore, e,
									(this.responseMessage!=null ? this.responseMessage.getParseException() : null),
									this.pddContext);
						
							esito.setEsitoInvocazione(true); 
							esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO, "risposta per riferimento non costruita, "+e.getMessage());
							this.openspcoopstate.releaseResource();
							return esito;
						}finally{
							if(this.openspcoopstate instanceof OpenSPCoopStateless){
								OpenSPCoopStateless state = (OpenSPCoopStateless) this.openspcoopstate;
								if(rilasciaRisorsa){
									state.releaseResource();
								}
								state.setUseConnection(false);
							}
						}
					}

					this.msgDiag.mediumDebug("Gestione risposta...");
					boolean rispostaVuotaValidaPerAsincroniStateless_modalitaAsincrona = false;
					if(this.responseMessage == null && 
							!(this.localForward && this.richiestaApplicativa!=null && Costanti.SCENARIO_ONEWAY_INVOCAZIONE_SERVIZIO.equals(this.richiestaApplicativa.getScenario())) ){

						if( (Costanti.SCENARIO_ASINCRONO_ASIMMETRICO_INVOCAZIONE_SERVIZIO.equals(this.scenarioCooperazione) ||
								Costanti.SCENARIO_ASINCRONO_SIMMETRICO_INVOCAZIONE_SERVIZIO.equals(this.scenarioCooperazione) ||
								Costanti.SCENARIO_ASINCRONO_SIMMETRICO_CONSEGNA_RISPOSTA.equals(this.scenarioCooperazione) ) 
								&& this.portaDiTipoStateless
								&& (!this.isRicevutaAsincrona) ){
							rispostaVuotaValidaPerAsincroniStateless_modalitaAsincrona = true;
							this.responseMessage = MessageUtilities.buildEmptyMessage(OpenSPCoop2MessageFactory.getDefaultMessageFactory(),
									this.requestInfo.getProtocolRequestMessageType(), MessageRole.RESPONSE); // Costruisce messaggio vuoto per inserire busta (ricevuta asincrona)
						}
						else{
							this.msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_CONSEGNA, ("risposta applicativa attesa ma non ricevuta"));
							if(this.bustaRichiesta.getMittente()!=null && this.bustaRichiesta.getTipoMittente()!=null){
								this.msgDiag.logPersonalizzato("consegnaConErrore");
							}
							else{
								this.msgDiag.logPersonalizzato("consegnaConErrore.mittenteAnonimo");
							}
							
							this.ejbUtils.setIntegrationFunctionErrorPortaApplicativa(IntegrationFunctionError.EXPECTED_RESPONSE_NOT_FOUND);
							if(this.localForwardEngine!=null) {
								this.localForwardEngine.setIntegrationFunctionError(IntegrationFunctionError.EXPECTED_RESPONSE_NOT_FOUND);
							}
							this.sendErroreProcessamento(this.localForward, this.localForwardEngine, this.ejbUtils, 
									ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
										get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_517_RISPOSTA_RICHIESTA_NON_RITORNATA),
									this.idModuloInAttesa, this.bustaRichiesta, this.idCorrelazioneApplicativa, this.idCorrelazioneApplicativaRisposta, this.servizioApplicativoFruitore, null,
									(this.responseMessage!=null ? this.responseMessage.getParseException() : null),
									this.pddContext);
							
							this.openspcoopstate.releaseResource();
							esito.setEsitoInvocazione(true); 
							esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,"risposta applicativa attesa ma non ricevuta");

							return esito;
						}

					}
					
					if(rispostaVuotaValidaPerAsincroniStateless_modalitaAsincrona==false
							&& this.transazioneApplicativoServer==null // non ha senso per le notifiche asincrone tutte le operazioni presenti in questo corpo (validazione, correlazione, header di integrazione)
							){
						
						if(this.validazioneContenutoApplicativoApplicativo!=null && this.validazioneContenutoApplicativoApplicativo.getTipo()!=null){
							String tipo = ValidatoreMessaggiApplicativi.getTipo(this.validazioneContenutoApplicativoApplicativo);
							//this.msgContext.getIntegrazione().setTipoValidazioneContenuti(tipo);
							this.msgDiag.addKeyword(CostantiPdD.KEY_TIPO_VALIDAZIONE_CONTENUTI, tipo);
							this.msgDiag.addKeyword(CostantiPdD.KEY_DETAILS_VALIDAZIONE_CONTENUTI,"");
						}
						
						// Verifica xsd  (Se siamo in un caso di risposta applicativa presente)
						if(CostantiConfigurazione.STATO_CON_WARNING_ABILITATO.equals(this.validazioneContenutoApplicativoApplicativo.getStato())||
								CostantiConfigurazione.STATO_CON_WARNING_WARNING_ONLY.equals(this.validazioneContenutoApplicativoApplicativo.getStato())){

							if(this.transactionNullable!=null) {
								this.transactionNullable.getTempiElaborazione().startValidazioneRisposta();
							}
							ByteArrayInputStream binXSD = null;
							try{
								boolean hasContent = false;
								boolean isFault = false;
								if(ServiceBinding.SOAP.equals(this.responseMessage.getServiceBinding())){
									OpenSPCoop2SoapMessage soapMsg = this.responseMessage.castAsSoap();
									hasContent = !soapMsg.isSOAPBodyEmpty();
									isFault = soapMsg.isFault();
								}
								else{
									//org.openspcoop2.message.OpenSPCoop2RestMessage<?> restMsg = this.responseMessage.castAsRest();
									//hasContent = restMsg.hasContent();
									hasContent = true; // devo controllare gli header etc...
									// fix: i problem detail devono far parte dell'interfaccia openapi
									//isFault = restMsg.isProblemDetailsForHttpApis_RFC7807() || MessageRole.FAULT.equals(this.responseMessage.getMessageRole());
									isFault = MessageRole.FAULT.equals(this.responseMessage.getMessageRole());
								}
								
								boolean validazioneAbilitata = true;
								if(ServiceBinding.SOAP.equals(this.responseMessage.getServiceBinding())){
									validazioneAbilitata = hasContent && (isFault==false);
								}
								else {
									validazioneAbilitata = ValidatoreMessaggiApplicativiRest.isValidazioneAbilitata(this.proprietaValidazioneContenutoApplicativoApplicativo, this.responseMessage, this.codiceRitornato);
								}
								if( validazioneAbilitata ){
									
									this.msgDiag.logPersonalizzato("validazioneContenutiApplicativiRispostaInCorso");
									
									boolean readInterface = CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_INTERFACE.equals(this.validazioneContenutoApplicativoApplicativo.getTipo());
									IDServizio idSValidazioneXSD = this.idServizio;
									if(this.servizioHeaderIntegrazione!=null){
										idSValidazioneXSD = this.servizioHeaderIntegrazione;
									}
									
									if(ServiceBinding.SOAP.equals(this.responseMessage.getServiceBinding())){
									
										// Accept mtom message
										List<MtomXomReference> xomReferences = null;
										if(StatoFunzionalita.ABILITATO.equals(this.validazioneContenutoApplicativoApplicativo.getAcceptMtomMessage())){
											this.msgDiag.mediumDebug("Validazione xsd della risposta (mtomFastUnpackagingForXSDConformance)...");
											xomReferences = this.responseMessage.castAsSoap().mtomFastUnpackagingForXSDConformance();
										}
										
										// Init Validatore
										this.msgDiag.mediumDebug("Validazione della risposta (initValidator)...");
										ValidatoreMessaggiApplicativi validatoreMessaggiApplicativi = 
											new ValidatoreMessaggiApplicativi(this.registroServiziManager,idSValidazioneXSD,
													this.responseMessage,readInterface,
													this.propertiesReader.isValidazioneContenutiApplicativi_rpcLiteral_xsiType_gestione(),
													this.proprietaValidazioneContenutoApplicativoApplicativo,
													this.pddContext);
	
										// Validazione WSDL 
										if( CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_INTERFACE.equals(this.validazioneContenutoApplicativoApplicativo.getTipo()) 
												||
												CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_OPENSPCOOP.equals(this.validazioneContenutoApplicativoApplicativo.getTipo())
										){
											this.msgDiag.mediumDebug("Validazione wsdl della risposta ...");
											validatoreMessaggiApplicativi.validateWithWsdlLogicoImplementativo(false);
										}
										
										// Validazione XSD
										this.msgDiag.mediumDebug("Validazione xsd della risposta ...");
										validatoreMessaggiApplicativi.validateWithWsdlDefinitorio(false);
										
										// Validazione WSDL (Restore Original Document)
										if (CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_INTERFACE.equals(this.validazioneContenutoApplicativoApplicativo.getTipo())
											|| CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_OPENSPCOOP.equals(this.validazioneContenutoApplicativoApplicativo.getTipo())) {
											if(this.propertiesReader.isValidazioneContenutiApplicativi_rpcLiteral_xsiType_gestione() &&
													this.propertiesReader.isValidazioneContenutiApplicativi_rpcLiteral_xsiType_ripulituraDopoValidazione()){
												this.msgDiag.mediumDebug("Ripristino elementi modificati per supportare validazione wsdl della risposta ...");
												validatoreMessaggiApplicativi.restoreOriginalDocument(false);
											}
										}
										
										// Ripristino struttura messaggio con xom
										if(xomReferences!=null && xomReferences.size()>0){
											this.msgDiag.mediumDebug("Validazione xsd della risposta (mtomRestoreAfterXSDConformance)...");
											this.responseMessage.castAsSoap().mtomRestoreAfterXSDConformance(xomReferences);
										}
										
									}
									else {
										
										// Init Validatore
										this.msgDiag.mediumDebug("Validazione della risposta (initValidator)...");
										ValidatoreMessaggiApplicativiRest validatoreMessaggiApplicativi = 
											new ValidatoreMessaggiApplicativiRest(this.registroServiziManager, idSValidazioneXSD, this.responseMessage, readInterface, this.proprietaValidazioneContenutoApplicativoApplicativo, 
													this.protocolFactory, this.pddContext);
										
										if(CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_XSD.equals(this.validazioneContenutoApplicativoApplicativo.getTipo()) &&
												this.responseMessage.castAsRest().hasContent()) {
											
											// Validazione XSD
											this.msgDiag.mediumDebug("Validazione xsd della risposta ...");
											validatoreMessaggiApplicativi.validateWithSchemiXSD(false);
											
										}
										else {
											
											// Validazione Interface
											validatoreMessaggiApplicativi.validateResponseWithInterface(this.consegnaMessagePrimaTrasformazione, true);
											
										}
										
									}

									this.msgDiag.logPersonalizzato("validazioneContenutiApplicativiRispostaEffettuata");
								}
								else{
									if(hasContent==false){
										this.msgDiag.addKeyword(CostantiPdD.KEY_DETAILS_VALIDAZIONE_CONTENUTI,CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_VALIDAZIONE_SOAP_BODY_NON_PRESENTE);
										this.msgDiag.logPersonalizzato("validazioneContenutiApplicativiRispostaDisabilitata");
									}
									else if (isFault ){
										this.msgDiag.addKeyword(CostantiPdD.KEY_DETAILS_VALIDAZIONE_CONTENUTI,CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_VALIDAZIONE_SOAP_FAULT_PRESENTE);
										this.msgDiag.logPersonalizzato("validazioneContenutiApplicativiRispostaDisabilitata");
									}
								}

							}catch(ValidatoreMessaggiApplicativiException ex){
								this.msgDiag.addKeywordErroreProcessamento(ex);
								this.log.error("[ValidazioneContenutiApplicativi Risposta] "+ex.getMessage(),ex);
								if (CostantiConfigurazione.STATO_CON_WARNING_WARNING_ONLY.equals(this.validazioneContenutoApplicativoApplicativo.getStato())) {
									this.msgDiag.logPersonalizzato("validazioneContenutiApplicativiRispostaNonRiuscita.warningOnly");
								}
								else {
									this.msgDiag.logPersonalizzato("validazioneContenutiApplicativiRispostaNonRiuscita");
								}
								if(CostantiConfigurazione.STATO_CON_WARNING_WARNING_ONLY.equals(this.validazioneContenutoApplicativoApplicativo.getStato()) == false){
									
									// validazione abilitata
									
									this.pddContext.addObject(org.openspcoop2.core.constants.Costanti.ERRORE_VALIDAZIONE_RISPOSTA, "true");
									
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
									
									this.ejbUtils.setIntegrationFunctionErrorPortaApplicativa(integrationFunctionError);
									if(this.localForwardEngine!=null) {
										this.localForwardEngine.setIntegrationFunctionError(integrationFunctionError);
									}
									
									this.sendErroreProcessamento(this.localForward, this.localForwardEngine, this.ejbUtils, 
											ex.getErrore(),
											this.idModuloInAttesa, this.bustaRichiesta, this.idCorrelazioneApplicativa, this.idCorrelazioneApplicativaRisposta, this.servizioApplicativoFruitore, ex,
											(this.responseMessage!=null ? this.responseMessage.getParseException() : null),
											this.pddContext);
									
									this.openspcoopstate.releaseResource();
									esito.setEsitoInvocazione(true); 
									esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
											this.msgDiag.getMessaggio_replaceKeywords("validazioneContenutiApplicativiNonRiuscita"));
									return esito;
								}
							}catch(Exception ex){
								this.msgDiag.addKeywordErroreProcessamento(ex);
								this.log.error("Riscontrato errore durante la validazione xsd della risposta applicativa",ex);
								if (CostantiConfigurazione.STATO_CON_WARNING_WARNING_ONLY.equals(this.validazioneContenutoApplicativoApplicativo.getStato())) {
									this.msgDiag.logPersonalizzato("validazioneContenutiApplicativiRispostaNonRiuscita.warningOnly");
								}
								else {
									this.msgDiag.logPersonalizzato("validazioneContenutiApplicativiRispostaNonRiuscita");
								}
								if(CostantiConfigurazione.STATO_CON_WARNING_WARNING_ONLY.equals(this.validazioneContenutoApplicativoApplicativo.getStato()) == false){
									
									this.pddContext.addObject(org.openspcoop2.core.constants.Costanti.ERRORE_VALIDAZIONE_RISPOSTA, "true");
									
									// validazione abilitata
									
									this.sendErroreProcessamento(this.localForward, this.localForwardEngine, this.ejbUtils, 
											ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
												get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_531_VALIDAZIONE_TRAMITE_INTERFACCIA_FALLITA),
											this.idModuloInAttesa, this.bustaRichiesta, this.idCorrelazioneApplicativa, this.idCorrelazioneApplicativaRisposta, this.servizioApplicativoFruitore, ex,
											(this.responseMessage!=null ? this.responseMessage.getParseException() : null),
											this.pddContext);
								
									this.openspcoopstate.releaseResource();
									esito.setEsitoInvocazione(true); 
									esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
											this.msgDiag.getMessaggio_replaceKeywords("validazioneContenutiApplicativiNonRiuscita")); 
									return esito;
								}
							}finally{
								if(this.transactionNullable!=null) {
									this.transactionNullable.getTempiElaborazione().endValidazioneRisposta();
								}
								if(binXSD!=null){
									try{
										binXSD.close();
									}catch(Exception e){}
								}
							}
						}
						else{
							this.msgDiag.logPersonalizzato("validazioneContenutiApplicativiRispostaDisabilitata");
						}


						
						/* ------------  Header integrazione Risposta ------------- */
						
						HeaderIntegrazione headerIntegrazioneRisposta = new HeaderIntegrazione(this.idTransazione);		
						InResponsePAMessage inResponsePAMessage = new InResponsePAMessage();
						inResponsePAMessage.setBustaRichiesta(this.bustaRichiesta);
						inResponsePAMessage.setMessage(this.responseMessage);
						if(this.pa!=null)
							inResponsePAMessage.setPortaApplicativa(this.pa);
						else
							inResponsePAMessage.setPortaDelegata(this.pd);
						inResponsePAMessage.setHeaders(this.connectorSender.getHeaderTrasporto());
						inResponsePAMessage.setServizio(this.outRequestPAMessage.getServizio());
						inResponsePAMessage.setSoggettoMittente(this.outRequestPAMessage.getSoggettoMittente());
						Utilities.printFreeMemory("ConsegnaContenutiApplicativi - Gestione Header Integrazione... ");					
						for(int i=0; i<this.tipiIntegrazione.length;i++){
							try{
								IGestoreIntegrazionePA gestore = null;
								try{
									gestore = (IGestoreIntegrazionePA) this.pluginLoader.newIntegrazionePortaApplicativa(this.tipiIntegrazione[i]);
								}catch(Exception e){
									throw e;
								}
								if(gestore!=null){
									String classType = null;
									try {
										classType = gestore.getClass().getName();
										AbstractCore.init(gestore, this.pddContext, this.protocolFactory);
									}catch(Exception e){
										throw new Exception("Riscontrato errore durante l'inizializzazione della classe ["+classType+
												"] da utilizzare per la gestione dell'integrazione (Risposta) delle erogazione di tipo ["+this.tipiIntegrazione[i]+"]: "+e.getMessage());
									}
									if(this.responseMessage!=null){
										gestore.readInResponseHeader(headerIntegrazioneRisposta,inResponsePAMessage);
									}else if( ! (gestore instanceof IGestoreIntegrazionePASoap) ){
										gestore.readInResponseHeader(headerIntegrazioneRisposta,inResponsePAMessage);
									}
								}
							} catch (Exception e) {
								this.log.debug("Errore durante la lettura dell'header di integrazione ["+ this.tipiIntegrazione[i]
										+ "]: "+ e.getMessage(),e);
								this.msgDiag.addKeyword(CostantiPdD.KEY_TIPO_HEADER_INTEGRAZIONE,this.tipiIntegrazione[i]);
								this.msgDiag.addKeywordErroreProcessamento(e);
								this.msgDiag.logPersonalizzato("headerIntegrazione.letturaFallita");
							}
						}
						
						/* ------------  Correlazione Applicativa Risposta ------------- */
						if(this.correlazioneApplicativaRisposta!=null){
							Utilities.printFreeMemory("ConsegnaContenutiApplicativi - CorrelazioneApplicativa... ");	
							GestoreCorrelazioneApplicativa gestoreCorrelazione = null;
							try{
								
								gestoreCorrelazione = 
										new GestoreCorrelazioneApplicativa(this.openspcoopstate.getStatoRisposta(),
												this.log,this.soggettoFruitore,this.idServizio, this.servizioApplicativo, this.protocolFactory,
												this.transactionNullable, this.pddContext);
								
								gestoreCorrelazione.verificaCorrelazioneRisposta(this.correlazioneApplicativaRisposta, this.responseMessage, headerIntegrazioneRisposta, false);
								
								this.idCorrelazioneApplicativaRisposta = gestoreCorrelazione.getIdCorrelazione();
								
								if(this.idCorrelazioneApplicativaRisposta!=null) {
									if(this.transactionNullable!=null) {
										this.transactionNullable.setCorrelazioneApplicativaRisposta(this.idCorrelazioneApplicativaRisposta);
									}
								}
								
								if(this.richiestaApplicativa!=null)
									this.richiestaApplicativa.setIdCorrelazioneApplicativaRisposta(this.idCorrelazioneApplicativaRisposta);
								else if(this.richiestaDelegata!=null)
									this.richiestaDelegata.setIdCorrelazioneApplicativaRisposta(this.idCorrelazioneApplicativaRisposta);
														
								this.msgDiag.setIdCorrelazioneRisposta(this.idCorrelazioneApplicativaRisposta);
								
							}catch(Exception e){
								
								this.pddContext.addObject(org.openspcoop2.core.constants.Costanti.ERRORE_CORRELAZIONE_APPLICATIVA_RISPOSTA, "true");
								
								this.msgDiag.logErroreGenerico(e,"CorrelazioneApplicativaRisposta");
								this.log.error("Riscontrato errore durante il controllo di correlazione applicativa della risposta: "+ e.getMessage(),e);
								
								ErroreIntegrazione errore = null;
								if(gestoreCorrelazione!=null){
									errore = gestoreCorrelazione.getErrore();
								}
								if(errore==null){
									errore = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
											get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_553_CORRELAZIONE_APPLICATIVA_RISPOSTA_NON_RIUSCITA);
								}
								
								this.ejbUtils.setIntegrationFunctionErrorPortaApplicativa(IntegrationFunctionError.APPLICATION_CORRELATION_IDENTIFICATION_RESPONSE_FAILED);
								if(this.localForwardEngine!=null) {
									this.localForwardEngine.setIntegrationFunctionError(IntegrationFunctionError.APPLICATION_CORRELATION_IDENTIFICATION_RESPONSE_FAILED);
								}
								
								this.sendErroreProcessamento(this.localForward, this.localForwardEngine, this.ejbUtils, 
										errore,
										this.idModuloInAttesa, this.bustaRichiesta, this.idCorrelazioneApplicativa, this.idCorrelazioneApplicativaRisposta, this.servizioApplicativoFruitore, e,
										(this.responseMessage!=null ? this.responseMessage.getParseException() : null),
										this.pddContext);
								
								esito.setEsitoInvocazione(true); 
								esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
										"Riscontrato errore durante il controllo di correlazione applicativa della risposta: "+ e.getMessage());
								this.openspcoopstate.releaseResource();
								return esito;
							}
						}
						
						/* ------------  Header integrazione Risposta (Update/Delete) ------------- */
						
						Utilities.printFreeMemory("ConsegnaContenutiApplicativi - Update/Delete Header Integrazione... ");					
						for(int i=0; i<this.tipiIntegrazione.length;i++){
							try{
								IGestoreIntegrazionePA gestore = null;
								try{
									gestore = (IGestoreIntegrazionePA) this.pluginLoader.newIntegrazionePortaApplicativa(this.tipiIntegrazione[i]);
								}catch(Exception e){
									throw e;
								}
								if(gestore!=null){
									String classType = null;
									try {
										classType = gestore.getClass().getName();
										AbstractCore.init(gestore, this.pddContext, this.protocolFactory);
									}catch(Exception e){
										throw new Exception("Riscontrato errore durante l'inizializzazione della classe ["+classType+
												"] da utilizzare per la gestione dell'integrazione (Risposta Update/Delete) delle erogazione di tipo ["+this.tipiIntegrazione[i]+"]: "+e.getMessage());
									}
									if (this.responseMessage!=null && (gestore instanceof IGestoreIntegrazionePASoap) ) {
										if(this.propertiesReader.deleteHeaderIntegrazioneResponsePA()){
											((IGestoreIntegrazionePASoap)gestore).deleteInResponseHeader(inResponsePAMessage);
										}else{
											((IGestoreIntegrazionePASoap)gestore).updateInResponseHeader(inResponsePAMessage, this.idMessaggioConsegna, this.idMessageResponse, this.servizioApplicativoFruitore, 
													this.idCorrelazioneApplicativaRisposta, this.idCorrelazioneApplicativa);
										}
									}
								}
							} catch (Exception e) {
								this.log.debug("Errore durante la lettura dell'header di integrazione ["+ this.tipiIntegrazione[i]
										+ "]: "+ e.getMessage(),e);
								this.msgDiag.addKeyword(CostantiPdD.KEY_TIPO_HEADER_INTEGRAZIONE,this.tipiIntegrazione[i]);
								this.msgDiag.addKeywordErroreProcessamento(e);
								this.msgDiag.logPersonalizzato("headerIntegrazione.letturaFallita");
							}
						}
						
						
						/* ------------  Gestione Funzionalita' speciali per Attachments (Manifest) ------------- */	
						// Funzionalita' necessaria solo per la consegna di un servizio
						if(this.richiestaApplicativa!=null){
							if(this.scartaBody){
								IntegrationFunctionError integrationFunctionError = null;
								try{
									if(this.responseMessage!=null){
										if(ServiceBinding.SOAP.equals(this.responseMessage.getServiceBinding())==false){
											integrationFunctionError = IntegrationFunctionError.NOT_SUPPORTED_BY_PROTOCOL;
											throw new Exception("Funzionalita 'ScartaBody' valida solamente per Service Binding SOAP");
										}
										
										// E' permesso SOLO per messaggi con attachment
										if(this.responseMessage.castAsSoap().countAttachments() <= 0){
											throw new Exception("La funzionalita' e' permessa solo per messaggi SOAP With Attachments");
										}
									}
								}catch(Exception e){
									this.msgDiag.addKeywordErroreProcessamento(e);
									this.msgDiag.logPersonalizzato("funzionalitaScartaBodyNonRiuscita");
																		
									if(integrationFunctionError==null) {
										integrationFunctionError = IntegrationFunctionError.BAD_RESPONSE;
									}
									this.ejbUtils.setIntegrationFunctionErrorPortaApplicativa(integrationFunctionError);
									if(this.localForwardEngine!=null) {
										this.localForwardEngine.setIntegrationFunctionError(integrationFunctionError);
									}
									
									this.sendErroreProcessamento(this.localForward, this.localForwardEngine, this.ejbUtils, 
											ErroriIntegrazione.ERRORE_425_SCARTA_BODY.getErrore425_ScartaBody(e.getMessage()),
											this.idModuloInAttesa, this.bustaRichiesta, this.idCorrelazioneApplicativa, this.idCorrelazioneApplicativaRisposta, this.servizioApplicativoFruitore, e,
											(this.responseMessage!=null ? this.responseMessage.getParseException() : null),
											this.pddContext);
									
									this.openspcoopstate.releaseResource();
									esito.setEsitoInvocazione(true); 
									esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
											this.msgDiag.getMessaggio_replaceKeywords("funzionalitaScartaBodyNonRiuscita")); 
									return esito;
								}
							}
							if(this.allegaBody){
								try{
									if(this.responseMessage!=null){
										TunnelSoapUtils.allegaBody(this.responseMessage, this.propertiesReader.getHeaderSoapActorIntegrazione());
									}
								}catch(Exception e){
									this.msgDiag.addKeywordErroreProcessamento(e);
									this.msgDiag.logPersonalizzato("funzionalitaAllegaBodyNonRiuscita");
									
									this.ejbUtils.setIntegrationFunctionErrorPortaApplicativa(IntegrationFunctionError.BAD_RESPONSE);
									if(this.localForwardEngine!=null) {
										this.localForwardEngine.setIntegrationFunctionError(IntegrationFunctionError.BAD_RESPONSE);
									}
									
									this.sendErroreProcessamento(this.localForward, this.localForwardEngine, this.ejbUtils, 
											ErroriIntegrazione.ERRORE_424_ALLEGA_BODY.getErrore424_AllegaBody(e.getMessage()),
											this.idModuloInAttesa, this.bustaRichiesta, this.idCorrelazioneApplicativa, this.idCorrelazioneApplicativaRisposta, this.servizioApplicativoFruitore, e,
											(this.responseMessage!=null ? this.responseMessage.getParseException() : null),
											this.pddContext);
									
									this.openspcoopstate.releaseResource();
									esito.setEsitoInvocazione(true); 
									esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
											this.msgDiag.getMessaggio_replaceKeywords("funzionalitaAllegaBodyNonRiuscita"));
									return esito;
								}
							}
						}
					}
					
					
					
					// processResponse localforward
					if(this.localForward){
						
						if( this.richiestaApplicativa!=null && Costanti.SCENARIO_ONEWAY_INVOCAZIONE_SERVIZIO.equals(this.richiestaApplicativa.getScenario()) ){
							// Per avere esattamente il solito comportamento dello scenario con protocollo.
							if(this.soapFault==null && this.restProblem==null){
								// devo 'ignorare' la risposta anche se presente, essendo un profilo oneway.
								if(this.responseMessage!=null){
									if(ServiceBinding.SOAP.equals(this.responseMessage.getServiceBinding())){
										SOAPBody body = this.responseMessage.castAsSoap().getSOAPBody();
										if(body!=null)
											body.removeContents();	
									}
								}
							}
						}
						
						this.msgDiag.mediumDebug("Process response message for local forward...");
						try{
							if(this.localForwardEngine.processResponse(this.responseMessage)==false){
								this.localForwardEngine.sendErrore(this.localForwardEngine.getResponseMessageError());
								this.openspcoopstate.releaseResource();
								esito.setEsitoInvocazione(true); 
								esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,"this.localForwardEngine.processResponse==false");
								return esito;
							}
							if(this.localForwardEngine.getResponseMessageAfterProcess()!=null){
								// Messaggio aggiornato
								this.responseMessage = this.localForwardEngine.getResponseMessageAfterProcess();
							}
						}catch(Exception e){
							this.msgDiag.addKeywordErroreProcessamento(e);
							this.msgDiag.logErroreGenerico(e, "localForwardProcessResponse");
							this.sendErroreProcessamento(this.localForward, this.localForwardEngine, this.ejbUtils, 
									ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
										get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_556_LOCAL_FORWARD_PROCESS_RESPONSE_ERROR),
									this.idModuloInAttesa, this.bustaRichiesta, this.idCorrelazioneApplicativa, this.idCorrelazioneApplicativaRisposta, this.servizioApplicativoFruitore, e,
									(this.responseMessage!=null ? this.responseMessage.getParseException() : null),
									this.pddContext);
							
							this.openspcoopstate.releaseResource();
							esito.setEsitoInvocazione(true); 
							esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
									e.getMessage());
							return esito;
						}
					}
					
					
					
					// Salvo messaggio ricevuto
					this.msgDiag.mediumDebug("Registrazione messaggio di risposta nel RepositoryMessaggi...");
					try{
						this.msgResponse = new GestoreMessaggi(this.openspcoopstate, false, this.idMessageResponse,Costanti.OUTBOX,this.msgDiag,this.pddContext);
						this.msgResponse.registraMessaggio(this.responseMessage,this.idCorrelazioneApplicativa,this.idCorrelazioneApplicativaRisposta);
						this.msgResponse.aggiornaRiferimentoMessaggio(this.idMessaggioConsegna);
						this.msgResponse.aggiornaProprietarioMessaggio(ImbustamentoRisposte.ID_MODULO);
						if(this.responseMessage!=null && this.responseMessage.getParseException()!= null)
							throw this.responseMessage.getParseException().getSourceException(); // gestito nel cacth
					}catch(Exception e){
						this.msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_CONSEGNA, ("salvataggio risposta, "+e.getMessage()));
						if(this.bustaRichiesta.getMittente()!=null && this.bustaRichiesta.getTipoMittente()!=null){
							this.msgDiag.logPersonalizzato("consegnaConErrore");
						}
						else{
							this.msgDiag.logPersonalizzato("consegnaConErrore.mittenteAnonimo");
						}
						
						this.msgResponse.deleteMessageFromFileSystem(); // elimino eventuale risposta salvata su fileSystem
						this.msgResponse.aggiornaProprietarioMessaggio(TimerGestoreMessaggi.ID_MODULO);
						if(this.responseMessage.getParseException() == null){
							
							this.sendErroreProcessamento(this.localForward, this.localForwardEngine, this.ejbUtils, 
									ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
										get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_510_SAVE_RESPONSE_MSG),
									this.idModuloInAttesa, this.bustaRichiesta, this.idCorrelazioneApplicativa, this.idCorrelazioneApplicativaRisposta, this.servizioApplicativoFruitore, e,
									(this.responseMessage!=null ? this.responseMessage.getParseException() : null),
									this.pddContext);
							
						} else {
							
							this.ejbUtils.setIntegrationFunctionErrorPortaApplicativa(IntegrationFunctionError.UNPROCESSABLE_RESPONSE_CONTENT);
							if(this.localForwardEngine!=null) {
								this.localForwardEngine.setIntegrationFunctionError(IntegrationFunctionError.UNPROCESSABLE_RESPONSE_CONTENT);
							}
							
							this.pddContext.addObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RISPOSTA_NON_RICONOSCIUTO, true);
							this.sendErroreProcessamento(this.localForward, this.localForwardEngine, this.ejbUtils, 
									ErroriIntegrazione.ERRORE_440_PARSING_EXCEPTION_RISPOSTA.
										getErrore440_MessaggioRispostaMalformato(this.responseMessage.getParseException().getParseException()),
									this.idModuloInAttesa, this.bustaRichiesta, this.idCorrelazioneApplicativa, this.idCorrelazioneApplicativaRisposta, this.servizioApplicativoFruitore, 
									this.responseMessage.getParseException().getParseException(),
									this.responseMessage.getParseException(),
									this.pddContext);
							
						}
						this.openspcoopstate.releaseResource();
						esito.setEsitoInvocazione(true); 
						esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
								"salvataggio risposta, "+e.getMessage());
						return esito;
					}

					// Creazione messaggio di risposta
					if(this.localForward){
						
						this.localForwardEngine.sendResponse(this.idMessageResponse);
						
					}
					else{
						
						ImbustamentoRisposteMessage imbustamentoRisposteMSG = new ImbustamentoRisposteMessage();
						imbustamentoRisposteMSG.setIDMessageResponse(this.idMessageResponse);
						imbustamentoRisposteMSG.setRichiestaApplicativa(this.richiestaApplicativa);
						imbustamentoRisposteMSG.setRichiestaDelegata(this.richiestaDelegata);
						imbustamentoRisposteMSG.setBusta(this.bustaRichiesta);
						imbustamentoRisposteMSG.setSpedizioneMsgIngresso(this.ejbUtils.getSpedizioneMsgIngresso());
						imbustamentoRisposteMSG.setRicezioneMsgRisposta(this.ejbUtils.getRicezioneMsgRisposta());
						imbustamentoRisposteMSG.setOneWayVersione11(this.oneWayVersione11);
						imbustamentoRisposteMSG.setStateless(this.consegnaContenutiApplicativiMsg.isStateless());
						imbustamentoRisposteMSG.setImplementazionePdDSoggettoMittente(this.consegnaContenutiApplicativiMsg.getImplementazionePdDSoggettoMittente());
						imbustamentoRisposteMSG.setImplementazionePdDSoggettoDestinatario(this.consegnaContenutiApplicativiMsg.getImplementazionePdDSoggettoDestinatario());
						imbustamentoRisposteMSG.setPddContext(this.pddContext);
						
	
						// Spedizione risposta al modulo 'ImbustamentoRisposte'
						this.msgDiag.mediumDebug("Invio messaggio al modulo di ImbustamentoRisposte...");
						if (this.openspcoopstate instanceof OpenSPCoopStateful){
							try{
								this.ejbUtils.getNodeSender(this.propertiesReader, this.log).send(imbustamentoRisposteMSG, ImbustamentoRisposte.ID_MODULO, this.msgDiag, 
										this.identitaPdD,ConsegnaContenutiApplicativi.ID_MODULO, this.idMessaggioConsegna,this.msgResponse);
							} catch (Exception e) {
								this.log.error("Spedizione->ImbustamentoRisposte non riuscita",e);	
								this.msgDiag.logErroreGenerico(e,"GenericLib.nodeSender.send(ImbustamentoRisposte)");
								this.msgResponse.deleteMessageFromFileSystem(); // elimino eventuale risposta salvata su fileSystem
								
								this.sendErroreProcessamento(this.localForward, this.localForwardEngine, this.ejbUtils, 
										ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
											get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_512_SEND),
										this.idModuloInAttesa, this.bustaRichiesta, this.idCorrelazioneApplicativa, this.idCorrelazioneApplicativaRisposta, this.servizioApplicativoFruitore, e,
										(this.responseMessage!=null ? this.responseMessage.getParseException() : null),
										this.pddContext);
								
								this.openspcoopstate.releaseResource();
								esito.setEsitoInvocazione(true); 
								esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO, "Spedizione->ImbustamentoRisposte non riuscita"); 
								return esito;
							}
						}
						else ((OpenSPCoopStateless)this.openspcoopstate).setMessageLib(imbustamentoRisposteMSG);
					}

				}

			}











			/* ---------- Aggiornamento numero di sequenza ---------------- */
			if(this.ordineConsegna!=null){				
				if(this.oneWayVersione11 || this.openspcoopstate instanceof OpenSPCoopStateful){
					this.msgDiag.mediumDebug("Aggiornamento numero sequenza per consegna in ordine...");

					this.ordineConsegna.setNextSequenza_daRicevere(this.bustaRichiesta);
				}
			}






			/* ---------- Gestione Transazione Modulo ---------------- */

			// messaggio finale
			this.msgDiag.addKeyword(CostantiPdD.KEY_TIPO_CONNETTORE, this.tipoConnector);
			this.msgDiag.logPersonalizzato("gestioneConsegnaTerminata");

			// Commit JDBC della risposta
			this.msgDiag.mediumDebug("Commit delle operazioni per la gestione della richiesta...");
			this.openspcoopstate.commit();

			// Aggiornamento cache messaggio
			if(this.msgRequest!=null)
				this.msgRequest.addMessaggiIntoCache_readFromTable(ConsegnaContenutiApplicativi.ID_MODULO, "richiesta");
			if(this.msgResponse!=null)
				this.msgResponse.addMessaggiIntoCache_readFromTable(ConsegnaContenutiApplicativi.ID_MODULO, "risposta");

			// Aggiornamento cache proprietario messaggio
			if(this.msgRequest!=null)
				this.msgRequest.addProprietariIntoCache_readFromTable(ConsegnaContenutiApplicativi.ID_MODULO, "richiesta",null,false);
			if(this.msgResponse!=null)
				this.msgResponse.addProprietariIntoCache_readFromTable(ConsegnaContenutiApplicativi.ID_MODULO, "risposta",this.idMessaggioConsegna,false);


		}catch(Throwable e){
			this.log.error("ErroreGenerale",e);
			this.msgDiag.logErroreGenerico(e, "Generale");

			if ( this.msgResponse!=null ){
				this.msgResponse.deleteMessageFromFileSystem(); // elimino eventuale risposta salvata su fileSystem
			}

			if(this.existsModuloInAttesaRispostaApplicativa) {
				try{
					
					this.sendErroreProcessamento(this.localForward, this.localForwardEngine, this.ejbUtils, 
							ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
								getErroreIntegrazione(),
							this.idModuloInAttesa, this.bustaRichiesta, this.idCorrelazioneApplicativa, this.idCorrelazioneApplicativaRisposta, this.servizioApplicativoFruitore, e,
							(this.responseMessage!=null ? this.responseMessage.getParseException() : null),
							this.pddContext);
					
					esito.setEsitoInvocazione(true); 
					esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO, "ErroreGenerale");
				}catch(Exception er){
					this.msgDiag.logErroreGenerico(er,"this.ejbUtils.sendErroreGenerale(profiloConRisposta)");
					this.ejbUtils.rollbackMessage("Spedizione Errore al Mittente durante una richiesta con gestione della risposta non riuscita",this.servizioApplicativo, esito);
					esito.setEsitoInvocazione(false); 
					esito.setStatoInvocazioneErroreNonGestito(er);
				}
			}else{
				this.ejbUtils.rollbackMessage("ErroreGenerale:"+e.getMessage(), this.servizioApplicativo, esito);
				esito.setEsitoInvocazione(false); 
				esito.setStatoInvocazioneErroreNonGestito(e);
			}
			this.openspcoopstate.releaseResource();
			return esito;
		}finally{
			try {
				if(this.transazioneApplicativoServer!=null) {
					if(this.consegnaMessageTrasformato!=null) {
						this.transazioneApplicativoServer.setRichiestaUscitaBytes(this.consegnaMessageTrasformato.getOutgoingMessageContentLength());
					}
					if(this.responseMessage!=null) {
						long incomingResponseMessageContentLength = this.responseMessage.getIncomingMessageContentLength();
						if(incomingResponseMessageContentLength<=0){
							incomingResponseMessageContentLength = this.responseContentLength;
							if(incomingResponseMessageContentLength<0){
								//System.out.println("FLUSH");
								// forzo la lettura del messaggio per impostare la dimensione della richiesta
								try{
									this.responseMessage.writeTo(NullOutputStream.NULL_OUTPUT_STREAM, true);
								}catch(Exception eFlush){}
								incomingResponseMessageContentLength = this.responseMessage.getIncomingMessageContentLength();
							}
						}
						this.transazioneApplicativoServer.setRispostaIngressoBytes(incomingResponseMessageContentLength);
					}
				}
			}catch(Throwable t) {
				this.log.error("Errore durante la lettura delle dimensioni dei messaggi: "+t.getMessage(),t);
			}
			try{
				if(this.connectorSenderForDisconnect!=null)
					this.connectorSenderForDisconnect.disconnect();
			}catch(Exception e){
				try{
					if(this.msgDiag!=null)
						this.msgDiag.logDisconnectError(e, this.location);
				}catch(Exception eDisconnect){
					this.log.error("Errore durante la chiusura delle connessione: "+eDisconnect.getMessage(),e);
				}
			}
		}




		// Elimino SIL destinatario a cui ho consegnato il messaggio
		// a meno di consegna per riferimento
		//	if(connectionDB!=null){
		//		this.msgDiag.mediumDebug("Eliminazione SIL destinatario del messaggio nella tabelle MSG_SERVIZI_APPLICATIVI...");
		if(this.consegnaPerRiferimento==false){
			try{
				String idMessaggioGestoreMessaggiRichiestaEliminazione = this.idMessaggioConsegna;
				if(this.idMessaggioPreBehaviour!=null){
					idMessaggioGestoreMessaggiRichiestaEliminazione = this.bustaRichiesta.getID();
				}
				
				GestoreMessaggi gestoreEliminazioneDestinatario = new GestoreMessaggi(this.openspcoopstate, true, idMessaggioGestoreMessaggiRichiestaEliminazione,Costanti.INBOX,this.msgDiag,this.pddContext);
				if(this.idMessaggioPreBehaviour!=null && !(this.openspcoopstate instanceof OpenSPCoopStateful) ){
					gestoreEliminazioneDestinatario.setOneWayVersione11(true); // per forzare l'update su db	
				}
				else{
					gestoreEliminazioneDestinatario.setOneWayVersione11(this.oneWayVersione11);		
				}
				gestoreEliminazioneDestinatario.eliminaDestinatarioMessaggio(this.servizioApplicativo, null, this.oraRegistrazione);
			}catch(Exception e){
				this.msgDiag.logErroreGenerico(e,"gestoreEliminazioneDestinatario.eliminaDestinatarioMessaggio("+this.servizioApplicativo+",null)");
			}
		}

		//	Rilascio connessione al DB
		this.msgDiag.mediumDebug(ConsegnaContenutiApplicativi.ID_MODULO+ " Rilascio le risorse..");
		this.openspcoopstate.releaseResource();

		this.msgDiag.mediumDebug("Lavoro Terminato.");
		esito.setEsitoInvocazione(true); 
		esito.setStatoInvocazione(EsitoLib.OK,null);
		return esito; 

	}
	

	private EsitoLib doInternalError(Throwable e, EsitoLib esito) {
		this.log.error("ErroreGenerale",e);
		this.msgDiag.logErroreGenerico(e, "Generale");

		if(this.openspcoopstate.resourceReleased()){
			this.msgDiag.logErroreGenerico(e, "ErroreGeneraleNonGestibile");
			esito.setEsitoInvocazione(false);
			esito.setStatoInvocazioneErroreNonGestito(e);
			return esito;
		}else{
			if ( this.msgResponse!=null ){
				this.msgResponse.deleteMessageFromFileSystem(); // elimino eventuale risposta salvata su fileSystem
			}

			if(this.existsModuloInAttesaRispostaApplicativa) {
				try{
					
					this.sendErroreProcessamento(this.localForward, this.localForwardEngine, this.ejbUtils, 
							ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
								getErroreIntegrazione(),
							this.idModuloInAttesa, this.bustaRichiesta, this.idCorrelazioneApplicativa, this.idCorrelazioneApplicativaRisposta, this.servizioApplicativoFruitore, e,
							(this.responseMessage!=null ? this.responseMessage.getParseException() : null),
							this.pddContext);
					
					esito.setEsitoInvocazione(true); 
					esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO, "ErroreGenerale");
				}catch(Exception er){
					this.msgDiag.logErroreGenerico(er,"this.ejbUtils.sendErroreGenerale(profiloConRisposta)");
					this.ejbUtils.rollbackMessage("Spedizione Errore al Mittente durante una richiesta con gestione della risposta non riuscita",this.servizioApplicativo, esito);
					esito.setEsitoInvocazione(false); 
					esito.setStatoInvocazioneErroreNonGestito(er);
				}
			}else{
				this.ejbUtils.rollbackMessage("ErroreGenerale:"+e.getMessage(), this.servizioApplicativo, esito);
				esito.setEsitoInvocazione(false); 
				esito.setStatoInvocazioneErroreNonGestito(e);
			}
			
			this.openspcoopstate.releaseResource();
			return esito;
		}
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
	
	
	private void emitDiagnostico() {
		if(this.invokerNonSupportato==false){
			if(this.bustaRichiesta.getMittente()!=null && this.bustaRichiesta.getTipoMittente()!=null){
				if(this.errorConsegna){
					this.msgDiag.logPersonalizzato("consegnaConErrore");
				}else{
					this.msgDiag.logPersonalizzato("consegnaEffettuata");
				}
			}
			else{
				if(this.errorConsegna){
					this.msgDiag.logPersonalizzato("consegnaConErrore.mittenteAnonimo");
				}else{
					this.msgDiag.logPersonalizzato("consegnaEffettuata.mittenteAnonimo");
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
