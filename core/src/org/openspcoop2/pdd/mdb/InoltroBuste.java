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
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPFault;

import org.openspcoop2.core.config.Connettore;
import org.openspcoop2.core.config.DumpConfigurazione;
import org.openspcoop2.core.config.GestioneErrore;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.Proprieta;
import org.openspcoop2.core.config.ResponseCachingConfigurazione;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.Trasformazioni;
import org.openspcoop2.core.config.ValidazioneContenutiApplicativi;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.constants.CostantiConnettori;
import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.eccezione.details.DettaglioEccezione;
import org.openspcoop2.core.eccezione.details.utils.XMLUtils;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.OpenSPCoop2RestJsonMessage;
import org.openspcoop2.message.OpenSPCoop2RestMessage;
import org.openspcoop2.message.OpenSPCoop2RestXmlMessage;
import org.openspcoop2.message.OpenSPCoop2SoapMessage;
import org.openspcoop2.message.constants.MessageRole;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.message.exception.ParseException;
import org.openspcoop2.message.soap.SoapUtils;
import org.openspcoop2.message.soap.mtom.MtomXomReference;
import org.openspcoop2.message.utils.MessageUtilities;
import org.openspcoop2.pdd.config.ClassNameProperties;
import org.openspcoop2.pdd.config.ConfigurazionePdDManager;
import org.openspcoop2.pdd.config.ForwardProxy;
import org.openspcoop2.pdd.config.MTOMProcessorConfig;
import org.openspcoop2.pdd.config.MessageSecurityConfig;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.config.RichiestaDelegata;
import org.openspcoop2.pdd.config.dynamic.PddPluginLoader;
import org.openspcoop2.pdd.core.AbstractCore;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.core.EJBUtils;
import org.openspcoop2.pdd.core.GestoreCorrelazioneApplicativa;
import org.openspcoop2.pdd.core.GestoreMessaggi;
import org.openspcoop2.pdd.core.IntegrationContext;
import org.openspcoop2.pdd.core.MTOMProcessor;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.ProtocolContext;
import org.openspcoop2.pdd.core.ValidatoreMessaggiApplicativi;
import org.openspcoop2.pdd.core.ValidatoreMessaggiApplicativiException;
import org.openspcoop2.pdd.core.ValidatoreMessaggiApplicativiRest;
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
import org.openspcoop2.pdd.core.integrazione.IGestoreIntegrazionePD;
import org.openspcoop2.pdd.core.integrazione.IGestoreIntegrazionePDSoap;
import org.openspcoop2.pdd.core.integrazione.InResponsePDMessage;
import org.openspcoop2.pdd.core.integrazione.OutRequestPDMessage;
import org.openspcoop2.pdd.core.integrazione.UtilitiesIntegrazione;
import org.openspcoop2.pdd.core.state.IOpenSPCoopState;
import org.openspcoop2.pdd.core.state.OpenSPCoopState;
import org.openspcoop2.pdd.core.state.OpenSPCoopStateException;
import org.openspcoop2.pdd.core.state.OpenSPCoopStateful;
import org.openspcoop2.pdd.core.state.OpenSPCoopStateless;
import org.openspcoop2.pdd.core.token.TokenForward;
import org.openspcoop2.pdd.core.transazioni.Transaction;
import org.openspcoop2.pdd.core.transazioni.TransactionContext;
import org.openspcoop2.pdd.core.trasformazioni.GestoreTrasformazioni;
import org.openspcoop2.pdd.core.trasformazioni.GestoreTrasformazioniException;
import org.openspcoop2.pdd.logger.Dump;
import org.openspcoop2.pdd.logger.MsgDiagnosticiProperties;
import org.openspcoop2.pdd.logger.MsgDiagnostico;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.pdd.logger.Tracciamento;
import org.openspcoop2.pdd.services.DirectVMProtocolInfo;
import org.openspcoop2.pdd.services.connector.ConnectorException;
import org.openspcoop2.pdd.services.connector.IAsyncResponseCallback;
import org.openspcoop2.pdd.services.error.AbstractErrorGenerator;
import org.openspcoop2.pdd.services.error.RicezioneBusteExternalErrorGenerator;
import org.openspcoop2.pdd.services.error.RicezioneContenutiApplicativiInternalErrorGenerator;
import org.openspcoop2.pdd.timers.TimerGestoreMessaggi;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.engine.RequestInfo;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.protocol.engine.driver.RepositoryBuste;
import org.openspcoop2.protocol.engine.validator.Validatore;
import org.openspcoop2.protocol.registry.RegistroServiziManager;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.BustaRawContent;
import org.openspcoop2.protocol.sdk.Eccezione;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.Integrazione;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.ProtocolMessage;
import org.openspcoop2.protocol.sdk.SecurityInfo;
import org.openspcoop2.protocol.sdk.Trasmissione;
import org.openspcoop2.protocol.sdk.builder.ProprietaErroreApplicativo;
import org.openspcoop2.protocol.sdk.builder.ProprietaManifestAttachments;
import org.openspcoop2.protocol.sdk.config.IProtocolManager;
import org.openspcoop2.protocol.sdk.config.IProtocolVersionManager;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreCooperazione;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.ErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.ErroriCooperazione;
import org.openspcoop2.protocol.sdk.constants.ErroriIntegrazione;
import org.openspcoop2.protocol.sdk.constants.FaseImbustamento;
import org.openspcoop2.protocol.sdk.constants.FaseSbustamento;
import org.openspcoop2.protocol.sdk.constants.Inoltro;
import org.openspcoop2.protocol.sdk.constants.IntegrationFunctionError;
import org.openspcoop2.protocol.sdk.constants.MessaggiFaultErroreCooperazione;
import org.openspcoop2.protocol.sdk.constants.RuoloMessaggio;
import org.openspcoop2.protocol.sdk.state.StatelessMessage;
import org.openspcoop2.protocol.sdk.tracciamento.EsitoElaborazioneMessaggioTracciato;
import org.openspcoop2.protocol.sdk.validator.IValidatoreErrori;
import org.openspcoop2.protocol.sdk.validator.IValidazioneSemantica;
import org.openspcoop2.protocol.sdk.validator.ProprietaValidazione;
import org.openspcoop2.protocol.sdk.validator.ProprietaValidazioneErrori;
import org.openspcoop2.security.message.MessageSecurityContext;
import org.openspcoop2.security.message.MessageSecurityContextParameters;
import org.openspcoop2.security.message.constants.SecurityConstants;
import org.openspcoop2.security.message.engine.MessageSecurityFactory;
import org.openspcoop2.utils.LimitExceededIOException;
import org.openspcoop2.utils.LimitedInputStream;
import org.openspcoop2.utils.TimeoutIOException;
import org.openspcoop2.utils.TimeoutInputStream;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.digest.IDigestReader;
import org.openspcoop2.utils.io.notifier.NotifierInputStreamParams;
import org.openspcoop2.utils.rest.problem.JsonDeserializer;
import org.openspcoop2.utils.rest.problem.ProblemRFC7807;
import org.openspcoop2.utils.rest.problem.XmlDeserializer;
import org.openspcoop2.utils.transport.TransportResponseContext;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.slf4j.Logger;


/**
 * Contiene la libreria InoltroBuste
 *
 * @author Poli Andrea (apoli@link.it)
 * @author Tronci Fabio (tronci@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class InoltroBuste extends GenericLib implements IAsyncResponseCallback{

	public final static String ID_MODULO = "InoltroBuste";

	/** XMLBuilder */
	protected RicezioneContenutiApplicativiInternalErrorGenerator generatoreErrore;

	
	
	
	
	public InoltroBuste(Logger log) throws GenericLibException {
		super(InoltroBuste.ID_MODULO,log);
		inizializza();
	}

	@Override
	protected synchronized void inizializza() throws GenericLibException {	
		super.inizializza();
		InoltroBuste.initializeService(ClassNameProperties.getInstance(), this.propertiesReader);
	}
	
	
	/** IGestoreIntegrazionePA: lista di gestori, ordinati per priorita' minore */
	public static String[] defaultGestoriIntegrazionePD = null;
	private static java.util.concurrent.ConcurrentHashMap<String, String[]> defaultPerProtocolloGestoreIntegrazionePD = null;

	/** Indicazione se sono state inizializzate le variabili del servizio */
	public static boolean initializeService = false;

	/**
	 * Inizializzatore del servizio InoltroBuste
	 * 
	 * @throws Exception
	 */
	public synchronized static void initializeService(ClassNameProperties className,OpenSPCoop2Properties propertiesReader) {

		if(InoltroBuste.initializeService)
			return;

		boolean error = false;
		Logger logCore = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();
		PddPluginLoader pluginLoader = PddPluginLoader.getInstance();
		
		// Inizializzo IGestoreIntegrazionePD list
		InoltroBuste.defaultGestoriIntegrazionePD = propertiesReader.getTipoIntegrazionePD();
		for(int i=0; i<InoltroBuste.defaultGestoriIntegrazionePD.length; i++){
			try{
				IGestoreIntegrazionePD gestore = pluginLoader.newIntegrazionePortaDelegata(InoltroBuste.defaultGestoriIntegrazionePD[i]);
				gestore.toString();
				logCore.info("Inizializzazione gestore dati di integrazione per le fruizioni di tipo "+
						InoltroBuste.defaultGestoriIntegrazionePD[i]+" effettuata.");
			}catch(Exception e){
				logCore.error(e.getMessage(),e);
				error = true;
			}
		}
		
		// Inizializzo IGestoreIntegrazionePD per protocollo
		InoltroBuste.defaultPerProtocolloGestoreIntegrazionePD = new java.util.concurrent.ConcurrentHashMap<String, String[]>();
		try{
			Enumeration<String> enumProtocols = ProtocolFactoryManager.getInstance().getProtocolNames();
			while (enumProtocols.hasMoreElements()) {
				String protocol = (String) enumProtocols.nextElement();
				String[]tipiIntegrazionePD = propertiesReader.getTipoIntegrazionePD(protocol);
				if(tipiIntegrazionePD!=null && tipiIntegrazionePD.length>0){
					List<String> tipiIntegrazionePerProtocollo = new ArrayList<String>();
					for (int i = 0; i < tipiIntegrazionePD.length; i++) {
						try {
							IGestoreIntegrazionePD gestore = pluginLoader.newIntegrazionePortaDelegata(tipiIntegrazionePD[i]);
							gestore.toString();
							tipiIntegrazionePerProtocollo.add(tipiIntegrazionePD[i]);
							logCore.info("Inizializzazione gestore dati di integrazione (protocollo: "+protocol+") per le fruizioni di tipo "
									+ tipiIntegrazionePD[i]	+ " effettuata.");
						} catch (Exception e) {
							logCore.error(e.getMessage(),e);
							error = true;
						}
					}
					if(tipiIntegrazionePerProtocollo.size()>0){
						InoltroBuste.defaultPerProtocolloGestoreIntegrazionePD.put(protocol, tipiIntegrazionePerProtocollo.toArray(new String[1]));
					}
				}
			}
		}catch(Exception e){
			logCore.error(
					"Riscontrato errore durante l'inizializzazione dei tipi di integrazione per protocollo: "+e.getMessage(),e);
			error = true;
		}
		

		InoltroBuste.initializeService = !error;
	}
		
	
	
	private IOpenSPCoopState openspcoopstate = null;
	private RegistroServiziManager registroServiziManager = null;
	private ConfigurazionePdDManager configurazionePdDManager = null;
	private MsgDiagnostico msgDiag = null;
	private InoltroBusteMessage inoltroBusteMsg = null;
	
	private PdDContext pddContext = null;
	private String idTransazione = null;
	private Transaction transactionNullable = null;
	private RequestInfo requestInfo = null;

	private EJBUtils ejbUtils = null;
	private GestoreMessaggi msgRequest = null;
	private GestoreMessaggi msgResponse = null;
	private RepositoryBuste repositoryBuste = null;
	
	private Trasformazioni trasformazioni = null;
	private GestoreTrasformazioni gestoreTrasformazioni = null;
	
	private MTOMProcessor mtomProcessor = null;
	private MessageSecurityConfig messageSecurityConfig = null;
	private MessageSecurityFactory messageSecurityFactory = null;
	private MessageSecurityContext messageSecurityContext = null;
	private SecurityInfo securityInfo = null;
		
	private IProtocolFactory<?> protocolFactory = null;
	private org.openspcoop2.protocol.sdk.config.ITraduttore traduttore = null;
	private IProtocolVersionManager protocolManager = null;
	private DumpConfigurazione dumpConfig = null;
	private org.openspcoop2.pdd.logger.Tracciamento tracciamento = null;
	
	private Validatore validatore = null;
	private IValidazioneSemantica validazioneSemantica = null;
	private boolean readQualifiedAttribute;
	private boolean validazioneIDBustaCompleta;
	private ProprietaValidazioneErrori pValidazioneErrori = null;
	
	private Date dataPrimaInvocazioneConnettore = null;
	private Date dataTerminataInvocazioneConnettore = null;
		
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
	private boolean rollbackRichiesta = true;
	
	private Busta bustaRichiesta = null;
	private String idMessageRequest = null;
	private OpenSPCoop2Message requestMessagePrimaTrasformazione = null;
	private OpenSPCoop2Message requestMessageTrasformato = null;
	private HttpRequestMethod httpRequestMethod = null;
	private OutRequestContext outRequestContext = null;
	private BustaRawContent<?> headerBustaRichiesta = null;

	private String idMessageResponse = null;
	private OpenSPCoop2Message responseMessage = null;
	private BustaRawContent<?> headerProtocolloRisposta = null;
		
	private String idCorrelazioneApplicativa = null;
	private String idCorrelazioneApplicativaRisposta = null;
	
	private IDSoggetto identitaPdD = null;
	private String servizioApplicativoFruitore = null;
	private IDSoggetto soggettoFruitore = null; 
	private IDServizio idServizio = null;
	private IDAccordo idAccordoServizio = null;
	private String implementazionePdDDestinatario = null;
	private String profiloGestione = null;
	
	private TransportResponseContext transportResponseContext = null;
	private int codiceRitornato = -1;
	private OpenSPCoop2MessageFactory faultMessageFactory = null;
	private SOAPFault soapFault = null;
	private ProblemRFC7807 restProblem = null;
	private boolean enrichSoapFaultApplicativo;
	private boolean enrichSoapFaultPdD;
		
	private TipoPdD tipoPdD = null;
	private PortaDelegata pd = null;
	private RichiestaDelegata richiestaDelegata = null;
	private ServizioApplicativo sa = null;
	private boolean functionAsRouter = false;
	private boolean portaDiTipoStateless= false;
	private boolean statelessAsincrono = false;
	private boolean oneWayVersione11;
	
	private boolean sendRispostaApplicativa = false;
	private boolean gestioneBusteNonRiscontrateAttive = false;
	private boolean isBlockedTransaction_responseMessageWithTransportCodeError = false;
	private boolean sbustamentoInformazioniProtocolloRisposta = false;
	private boolean richiestaAsincronaSimmetricaStateless = false;
	private boolean newConnectionForResponse = false; 
	
	private boolean gestioneManifest = false;
	private ProprietaManifestAttachments proprietaManifestAttachments;
		
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
					this.asyncResponseCallback.asyncComplete(esitoLib);
				}catch(Exception e) {
					throw new OpenSPCoopStateException(e.getMessage(),e);
				}
			}
		}
	}
	
	@Override
	public void asyncComplete(Object ... args) throws ConnectorException { // Questo metodo verrà chiamato dalla catena di metodi degli oggetti (IAsyncResponseCallback) fatta scaturire dal response callback dell'Async Client NIO
		
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
		
		this.asyncResponseCallback.asyncComplete(esitoLib);
	}
	
	public EsitoLib _process(IOpenSPCoopState openspcoopstateParam,
			RegistroServiziManager registroServiziManagerParam,ConfigurazionePdDManager configurazionePdDManagerParam, 
			MsgDiagnostico msgDiagParam) throws OpenSPCoopStateException {

		this.openspcoopstate = openspcoopstateParam;
		this.registroServiziManager = registroServiziManagerParam;
		this.configurazionePdDManager = configurazionePdDManagerParam;
		this.msgDiag = msgDiagParam;
		
		EsitoLib esito = new EsitoLib();
		this.inoltroBusteMsg = (InoltroBusteMessage) this.openspcoopstate.getMessageLib();
		
		/* PddContext */
		this.pddContext = this.inoltroBusteMsg.getPddContext();
		this.idTransazione = PdDContext.getValue(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE, this.pddContext);
						
		/* ID e tipo di implementazione PdD con cui interoperare */
		this.idMessageRequest = this.openspcoopstate.getIDMessaggioSessione();
		this.implementazionePdDDestinatario = this.inoltroBusteMsg.getImplementazionePdDSoggettoDestinatario();
		
		this.richiestaDelegata = this.inoltroBusteMsg.getRichiestaDelegata();
		this.bustaRichiesta = this.inoltroBusteMsg.getBusta();
		this.identitaPdD = this.inoltroBusteMsg.getRichiestaDelegata().getDominio();
		this.profiloGestione = this.richiestaDelegata.getProfiloGestione();
		this.msgDiag.setDominio(this.identitaPdD);  // imposto anche il dominio nel msgDiag
		this.msgDiag.setPrefixMsgPersonalizzati(MsgDiagnosticiProperties.MSG_DIAG_INOLTRO_BUSTE);
		this.msgDiag.addKeywords(this.bustaRichiesta, true);
		this.msgDiag.mediumDebug("Profilo di gestione ["+InoltroBuste.ID_MODULO+"] della busta: "+this.profiloGestione);
		
		IDPortaDelegata idPD = this.richiestaDelegata.getIdPortaDelegata();
		
		this.tipoPdD = TipoPdD.DELEGATA;
		if(idPD!=null) {
			this.msgDiag.updatePorta(this.tipoPdD, idPD.getNome());
		}
		
		Integrazione integrazione = new Integrazione();
		integrazione.setIdModuloInAttesa(this.richiestaDelegata.getIdModuloInAttesa());
		integrazione.setNomePorta(this.richiestaDelegata.getIdPortaDelegata().getNome());
		integrazione.setScenario(this.richiestaDelegata.getScenario());
		integrazione.setServizioApplicativo(this.richiestaDelegata.getServizioApplicativo());

		if(CostantiPdD.SERVIZIO_APPLICATIVO_ANONIMO.equals(this.richiestaDelegata.getServizioApplicativo())==false){
			this.servizioApplicativoFruitore = this.richiestaDelegata.getServizioApplicativo();
			this.msgDiag.addKeyword(CostantiPdD.KEY_SA_FRUITORE, this.servizioApplicativoFruitore);
		}else{
			this.msgDiag.addKeyword(CostantiPdD.KEY_SA_FRUITORE, CostantiPdD.SERVIZIO_APPLICATIVO_ANONIMO);
		}
		this.idCorrelazioneApplicativa = this.richiestaDelegata.getIdCorrelazioneApplicativa();
		this.msgDiag.setIdCorrelazioneApplicativa(this.idCorrelazioneApplicativa);
		this.msgDiag.addKeyword(CostantiPdD.KEY_ID_CORRELAZIONE_APPLICATIVA,this.idCorrelazioneApplicativa);
		
		// Aggiornamento Informazioni messaggio diagnostico
		this.msgDiag.setIdMessaggioRichiesta(this.idMessageRequest);
		this.msgDiag.setFruitore(this.richiestaDelegata.getIdSoggettoFruitore());
		this.msgDiag.setServizio(this.richiestaDelegata.getIdServizio());
		

		
		
		// VM ProtocolInfo (se siamo arrivati da un canale VM)
		if(this.pddContext!=null && this.bustaRichiesta!=null)
			DirectVMProtocolInfo.setInfoFromContext(this.pddContext, this.bustaRichiesta);
		

		/* ------------------ Inizializzo stato OpenSPCoop  --------------- */
		this.msgDiag.mediumDebug("Inizializzo stato per la gestione della richiesta...");
		this.openspcoopstate.initResource(this.identitaPdD, InoltroBuste.ID_MODULO, this.idTransazione);
		this.registroServiziManager.updateState(this.openspcoopstate.getStatoRichiesta(),this.openspcoopstate.getStatoRisposta());
		this.configurazionePdDManager.updateState(this.openspcoopstate.getStatoRichiesta(),this.openspcoopstate.getStatoRisposta());
		this.msgDiag.updateState(this.openspcoopstate.getStatoRichiesta(),this.openspcoopstate.getStatoRisposta());



		/* Protocol Factory */
		try{
			this.protocolFactory = this.protocolFactoryManager.getProtocolFactoryByName((String) this.pddContext.getObject(org.openspcoop2.core.constants.Costanti.PROTOCOL_NAME));
			this.traduttore = this.protocolFactory.createTraduttore();
			this.protocolManager = this.protocolFactory.createProtocolVersionManager(this.inoltroBusteMsg.getRichiestaDelegata().getProfiloGestione());
			this.validazioneSemantica = this.protocolFactory.createValidazioneSemantica(this.openspcoopstate.getStatoRichiesta());
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
		
		this.pValidazioneErrori = new ProprietaValidazioneErrori();
		this.pValidazioneErrori.setIgnoraEccezioniNonGravi(this.protocolManager.isIgnoraEccezioniNonGravi());
		



		/* ------------------ Inizializzazione Contesto di gestione  --------------- */
		this.msgDiag.mediumDebug("Inizializzo contesto per la gestione...");

		// Check FunctionRouting  
		this.msgDiag.mediumDebug("Esamina modalita' di ricezione (PdD/Router)...");
		boolean existsSoggetto = false;
		try{
			existsSoggetto = this.configurazionePdDManager.existsSoggetto(new IDSoggetto(this.bustaRichiesta.getTipoMittente(),this.bustaRichiesta.getMittente()));
		}catch(Exception e){
			this.msgDiag.logErroreGenerico(e, "existsSoggetto("+this.bustaRichiesta.getTipoMittente()+"/"+this.bustaRichiesta.getMittente()+")");  
			this.openspcoopstate.releaseResource();
			esito.setEsitoInvocazione(false);	
			return esito;
		}
		if(existsSoggetto==false){
			// La PdD non gestisce il soggetto mittente della busta.
			// Controllo adesso che sia abilitata la funzione di Router per la PdD, poiche'
			// mi dovrebbe essere arrivata per forza dal modulo di ricezione buste in modalita' Router.
			boolean routerFunctionActive = false;
			try{
				routerFunctionActive = this.configurazionePdDManager.routerFunctionActive();
			}catch(Exception e){
				this.msgDiag.logErroreGenerico(e,"routerFunctionActive()");
				this.openspcoopstate.releaseResource();
				esito.setEsitoInvocazione(false);	
				esito.setStatoInvocazioneErroreNonGestito(e);
				return esito;
			}
			if(routerFunctionActive){
				this.functionAsRouter = true;
			}else{
				this.msgDiag.logPersonalizzato("routingTable.soggettoFruitoreNonGestito");
				this.openspcoopstate.releaseResource();
				esito.setEsitoInvocazione(true);	
				esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
						this.msgDiag.getMessaggio_replaceKeywords("routingTable.soggettoFruitoreNonGestito"));
				return esito;
			}
		}
		if(this.functionAsRouter){
			this.tipoPdD = TipoPdD.ROUTER;
		}

		
		//	GestoriMessaggio
		this.msgRequest = new GestoreMessaggi(this.openspcoopstate, true, this.idMessageRequest,Costanti.OUTBOX,this.msgDiag, this.pddContext);
		
		
		// RequestInfo
		this.requestInfo = (RequestInfo) this.pddContext.getObject(org.openspcoop2.core.constants.Costanti.REQUEST_INFO);
		if(this.requestInfo==null || this.idTransazione==null) {
			// devo leggerlo dal messaggio
			try {
				this.requestMessagePrimaTrasformazione = this.msgRequest.getMessage();
				if(this.requestInfo==null) {
					Object o = this.requestMessagePrimaTrasformazione.getContextProperty(org.openspcoop2.core.constants.Costanti.REQUEST_INFO);
					if(o==null) {
						throw new Exception("RequestInfo non presente nel contesto");
					}
					this.requestInfo = (RequestInfo) o;
					this.pddContext.addObject(org.openspcoop2.core.constants.Costanti.REQUEST_INFO,this.requestInfo);
				}
				if(this.idTransazione==null) {
					Object o = this.requestMessagePrimaTrasformazione.getContextProperty(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE);
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
		
		
		
		// ProprietaErroreApplicativo (No function Router active)
		ProprietaErroreApplicativo proprietaErroreAppl = null;
		if(this.functionAsRouter==false){
			proprietaErroreAppl = this.richiestaDelegata.getFault();
			proprietaErroreAppl.setIdModulo(InoltroBuste.ID_MODULO);
		}
		
		try{
			this.generatoreErrore = new RicezioneContenutiApplicativiInternalErrorGenerator(this.log, this.idModulo, this.requestInfo);
			this.generatoreErrore.updateInformazioniCooperazione(this.richiestaDelegata.getIdSoggettoFruitore(), this.richiestaDelegata.getIdServizio());
			this.generatoreErrore.updateInformazioniCooperazione(this.richiestaDelegata.getServizioApplicativo());
			if(proprietaErroreAppl!=null){
				this.generatoreErrore.updateProprietaErroreApplicativo(proprietaErroreAppl);
			}
			this.generatoreErrore.updateTipoPdD(this.tipoPdD);
		}catch(Exception e){
			this.msgDiag.logErroreGenerico(e, "RicezioneContenutiApplicativiGeneratoreErrore.instanziazione"); 
			this.openspcoopstate.releaseResource();
			esito.setEsitoInvocazione(false); 
			esito.setStatoInvocazioneErroreNonGestito(e);
			return esito;
		}
		
		
		
		
		
		
		
		
		/* ----------- Lettura PortaDelegata e Servizio Applicativo ------------- */
		if(this.functionAsRouter==false){
			try{
				this.pd = this.configurazionePdDManager.getPortaDelegata(this.richiestaDelegata.getIdPortaDelegata());
			}catch(Exception e){
				this.msgDiag.logErroreGenerico(e,"getPortaDelegata()");
				this.openspcoopstate.releaseResource();
				esito.setEsitoInvocazione(false);	
				esito.setStatoInvocazioneErroreNonGestito(e);
				return esito;
			}
			try{
				IDServizioApplicativo idSA = new IDServizioApplicativo();
				idSA.setNome(this.richiestaDelegata.getServizioApplicativo());
				idSA.setIdSoggettoProprietario(this.richiestaDelegata.getIdSoggettoFruitore());
				this.sa = this.configurazionePdDManager.getServizioApplicativo(idSA);
			}catch(Exception e){
				if( !(e instanceof DriverConfigurazioneNotFound) || 
						!(CostantiPdD.SERVIZIO_APPLICATIVO_ANONIMO.equals(this.richiestaDelegata.getServizioApplicativo())) ){
					this.msgDiag.logErroreGenerico(e,"getServizioApplicativo()");
					this.openspcoopstate.releaseResource();
					esito.setEsitoInvocazione(false);	
					esito.setStatoInvocazioneErroreNonGestito(e);
					return esito;
				}
			}
		}
		
		
		Utilities.printFreeMemory("InoltroBuste - Esamina contesto per la gestione...");
		
		this.msgDiag.mediumDebug("Esamina contesto per la gestione...");
		// Modalita' gestione risposta Applicativa (No function Router active) 
		// -a immediata se il profilo e' sincrono 
		// -b immediata se il profilo e' asincrono e ricevutaAsincrona e' abilitata.
		// -c immediata se il profilo e' asincrono asimmetrico di richiesta stato e ricevuta disabilitata
		// -d immediata se il profilo e' oneway e la modalita di trasmissione e' sincrona.
		// -e rollback negli altri casi
		if(this.functionAsRouter==false){
			// a)
			if(Costanti.SCENARIO_SINCRONO_INVOCAZIONE_SERVIZIO.equals(this.richiestaDelegata.getScenario()) ){
				this.sendRispostaApplicativa = true; 
				integrazione.setStateless(true);
			}
			// c) [e b) per la richiestaStato AsincronaAsimmetrica]
			else if(Costanti.SCENARIO_ASINCRONO_ASIMMETRICO_POLLING.equals(this.richiestaDelegata.getScenario())){
				try{
					this.statelessAsincrono = this.configurazionePdDManager.isModalitaStateless(this.pd, this.bustaRichiesta.getProfiloDiCollaborazione());
				}catch(Exception e){
					this.msgDiag.logErroreGenerico(e,"AsincronoAsimmetricoPolling.isModalitaStateless(pd)");
					this.openspcoopstate.releaseResource();      
					esito.setEsitoInvocazione(false);	
					esito.setStatoInvocazioneErroreNonGestito(e);
					return esito;
				}
				this.sendRispostaApplicativa = true; 
				integrazione.setStateless(this.statelessAsincrono);
			}
			// b)
			else if (Costanti.SCENARIO_ASINCRONO_ASIMMETRICO_INVOCAZIONE_SERVIZIO.equals(this.richiestaDelegata.getScenario()) ||
					Costanti.SCENARIO_ASINCRONO_SIMMETRICO_INVOCAZIONE_SERVIZIO.equals(this.richiestaDelegata.getScenario()) ||
					Costanti.SCENARIO_ASINCRONO_SIMMETRICO_CONSEGNA_RISPOSTA.equals(this.richiestaDelegata.getScenario()) ){
				try{
					this.statelessAsincrono = this.configurazionePdDManager.isModalitaStateless(this.pd, this.bustaRichiesta.getProfiloDiCollaborazione());
					this.sendRispostaApplicativa =  this.statelessAsincrono || this.richiestaDelegata.isRicevutaAsincrona();
					this.richiestaAsincronaSimmetricaStateless = Costanti.SCENARIO_ASINCRONO_SIMMETRICO_INVOCAZIONE_SERVIZIO.equals(this.richiestaDelegata.getScenario())
						&& (this.openspcoopstate instanceof OpenSPCoopStateless);
				}catch(Exception e){
					this.msgDiag.logErroreGenerico(e,"Asincrono.isModalitaStateless(pd)");
					this.openspcoopstate.releaseResource();      
					esito.setEsitoInvocazione(false);	
					esito.setStatoInvocazioneErroreNonGestito(e);
					return esito;
				}	
				integrazione.setStateless(this.statelessAsincrono);
			}
			// d)
			else if(Costanti.SCENARIO_ONEWAY_INVOCAZIONE_SERVIZIO.equals(this.richiestaDelegata.getScenario())){
				try{
					this.sendRispostaApplicativa = this.configurazionePdDManager.isModalitaStateless(this.pd, this.bustaRichiesta.getProfiloDiCollaborazione());
				}catch(Exception e){
					this.msgDiag.logErroreGenerico(e,"OnewayInvocazioneServizio.isModalitaStateless(pd)");
					this.openspcoopstate.releaseResource();      
					esito.setEsitoInvocazione(false);	
					esito.setStatoInvocazioneErroreNonGestito(e);
					return esito;
				}	
				integrazione.setStateless(this.sendRispostaApplicativa);
			}
		}
		
		this.pddContext.addObject(org.openspcoop2.core.constants.Costanti.STATELESS, integrazione.isStateless()+"");

		// EJBUtils (per eventuali errori)
		try{
			this.ejbUtils = new EJBUtils(this.identitaPdD,this.tipoPdD,InoltroBuste.ID_MODULO,this.idMessageRequest,
					this.idMessageRequest,Costanti.OUTBOX,this.openspcoopstate, this.msgDiag,this.functionAsRouter,
					this.inoltroBusteMsg.getImplementazionePdDSoggettoMittente(),
					this.inoltroBusteMsg.getImplementazionePdDSoggettoDestinatario(),
					this.profiloGestione,this.pddContext);
		}catch(Exception e){
			this.msgDiag.logErroreGenerico(e, "EJBUtils.new");  
			this.openspcoopstate.releaseResource();
			esito.setEsitoInvocazione(false);	return esito;
		}
		this.ejbUtils.setRouting(this.functionAsRouter);
		
		if(this.functionAsRouter){
			try{
				RicezioneBusteExternalErrorGenerator generatoreErrorePA = new RicezioneBusteExternalErrorGenerator(this.log, this.idModulo, this.requestInfo, this.openspcoopstate.getStatoRichiesta());
				generatoreErrorePA.updateInformazioniCooperazione(this.richiestaDelegata.getIdSoggettoFruitore(), this.richiestaDelegata.getIdServizio());
				generatoreErrorePA.updateTipoPdD(TipoPdD.ROUTER);
				this.ejbUtils.setGeneratoreErrorePortaApplicativa(generatoreErrorePA);
			}catch(Exception e){
				this.msgDiag.logErroreGenerico(e, "RicezioneBusteExternalErrorGenerator.instanziazione"); 
				this.openspcoopstate.releaseResource();
				esito.setEsitoInvocazione(false); 
				esito.setStatoInvocazioneErroreNonGestito(e);
				return esito;
			}
		}

		
		// Oneway versione 11
		this.oneWayVersione11 = this.inoltroBusteMsg.isOneWayVersione11();
		this.ejbUtils.setOneWayVersione11(this.oneWayVersione11);
		this.msgRequest.setOneWayVersione11(this.oneWayVersione11);
		
		// ResponseCaching 
		ResponseCachingConfigurazione responseCachingConfig = null;
		if(this.functionAsRouter==false){
			try{		
				responseCachingConfig = this.configurazionePdDManager.getConfigurazioneResponseCaching(this.pd);	
			}catch(Exception e){
				this.msgDiag.logErroreGenerico(e, "getConfigurazioneResponseCaching(pd)");
				this.ejbUtils.rollbackMessage("Errore nella lettura della configurazione per il salvataggio della risposta in cache", esito);
				this.openspcoopstate.releaseResource();
				esito.setEsitoInvocazione(false);	
				esito.setStatoInvocazioneErroreNonGestito(e);
				return esito;
			}
		}
		
		// Trasformazioni
		if(this.functionAsRouter==false){
			try {
				this.trasformazioni = this.configurazionePdDManager.getTrasformazioni(this.pd);
			}catch(Exception e){
				this.msgDiag.logErroreGenerico(e, "getTrasformazioni(pd)");
				this.ejbUtils.rollbackMessage("Errore nella lettura della configurazione delle trasformazioni", esito);
				this.openspcoopstate.releaseResource();
				esito.setEsitoInvocazione(false);	
				esito.setStatoInvocazioneErroreNonGestito(e);
				return esito;
			}
		}
		
		//Identificazione del tipo di porta
		boolean routingStateless = false;
		if(this.functionAsRouter==false){
			try{		
				this.portaDiTipoStateless = this.configurazionePdDManager.isModalitaStateless(this.pd, this.bustaRichiesta.getProfiloDiCollaborazione());		
			}catch(Exception e){
				this.msgDiag.logErroreGenerico(e, "isModalitaStateless("+this.bustaRichiesta.getProfiloDiCollaborazione()+")");
				this.ejbUtils.rollbackMessage("Errore nella creazione dei gestori messaggio", esito);
				this.openspcoopstate.releaseResource();
				esito.setEsitoInvocazione(false);	
				esito.setStatoInvocazioneErroreNonGestito(e);
				return esito;
			}
		}else{
			routingStateless = CostantiConfigurazione.ABILITATO.equals(this.propertiesReader.getStatelessRouting());
			this.ejbUtils.setRollbackRichiestaInCasoErrore((routingStateless==false));
		}
		this.msgRequest.setPortaDiTipoStateless(this.portaDiTipoStateless);
		this.msgRequest.setRoutingStateless(routingStateless);

		if(this.functionAsRouter==false){
			try{		
				this.sbustamentoInformazioniProtocolloRisposta = this.configurazionePdDManager.invocazionePortaDelegataSbustamentoInformazioniProtocollo(this.sa);
			}catch(Exception e){
				this.msgDiag.logErroreGenerico(e, "isGestioneManifestAttachments(pd)");
				this.ejbUtils.rollbackMessage("Errore durante l'invocazione del metodo isGestioneManifestAttachments", esito);
				this.openspcoopstate.releaseResource();
				esito.setEsitoInvocazione(false);	
				esito.setStatoInvocazioneErroreNonGestito(e);
				return esito;
			}
		}
		
		try{		
			this.isBlockedTransaction_responseMessageWithTransportCodeError = 
						this.protocolManager.isBlockedTransaction_responseMessageWithTransportCodeError();
		}catch(Exception e){
			this.msgDiag.logErroreGenerico(e, "isBlockedTransaction_responseMessageWithTransportCodeError)");
			this.ejbUtils.rollbackMessage("Errore durante l'invocazione del metodo isBlockedTransaction_responseMessageWithTransportCodeError", esito);
			this.openspcoopstate.releaseResource();
			esito.setEsitoInvocazione(false);	
			esito.setStatoInvocazioneErroreNonGestito(e);
			return esito;
		}
		
		



		
		
		
		try{
			if(this.msgRequest.isRiconsegnaMessaggio(null) == false){
				this.openspcoopstate.releaseResource();
				// Per i profili 'sincroni' dove vi e' un modulo in attesa non puo' sussistere una riconsegna del messaggio.
				if(this.sendRispostaApplicativa==false){
					this.msgDiag.logPersonalizzato("riconsegnaMessaggioPrematura");
					esito.setEsitoInvocazione(false);
					esito.setStatoInvocazione(EsitoLib.ERRORE_NON_GESTITO,this.msgDiag.getMessaggio_replaceKeywords("riconsegnaMessaggioPrematura"));
				}else{
					String message = null;
					String posizione = null;
					if(Costanti.SCENARIO_ONEWAY_INVOCAZIONE_SERVIZIO.equals(this.richiestaDelegata.getScenario())){
						message = "Messaggio eliminato durante il controllo di ri-consegna ("+this.richiestaDelegata.getScenario()+")";
						posizione = "this.msgRequest.isRiconsegnaMessaggio("+this.richiestaDelegata.getScenario()+")";
					}else{
						message = "Messaggio eliminato durante il controllo di ri-consegna ("+this.richiestaDelegata.getScenario()+",STATELESS)";
						posizione = "this.msgRequest.isRiconsegnaMessaggio("+this.richiestaDelegata.getScenario()+",STATELESS)";
					}
					this.msgDiag.logErroreGenerico(message,posizione);
					esito.setEsitoInvocazione(true);
					esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,message);
				}
				return esito;
			}
		}catch(Exception e){
			this.msgDiag.logErroreGenerico(e, "this.msgRequest.isRiconsegnaMessaggio()");
			this.ejbUtils.rollbackMessage("Errore verifica riconsegna messaggio", esito);
			this.openspcoopstate.releaseResource();
			esito.setEsitoInvocazione(false);	
			esito.setStatoInvocazioneErroreNonGestito(e);
			return esito;
		}

		// Gestore Funzionalita'
		this.repositoryBuste = new RepositoryBuste(this.openspcoopstate.getStatoRichiesta(), true, this.protocolFactory);
	
		// Tracciamento
		try {
			this.tracciamento = new org.openspcoop2.pdd.logger.Tracciamento(this.identitaPdD,InoltroBuste.ID_MODULO,this.pddContext,this.tipoPdD,this.msgDiag.getPorta(),
					this.openspcoopstate.getStatoRichiesta(),this.openspcoopstate.getStatoRisposta());
		} catch (Exception e) {
			this.msgDiag.logErroreGenerico(e, "ProtocolFactory.instanziazione Imbustamento/Tracciamento"); 
			this.openspcoopstate.releaseResource();
			esito.setEsitoInvocazione(false); 
			esito.setStatoInvocazioneErroreNonGestito(e);
			return esito;
		}
		// rollback Richiesta non deve essere effettuato se ho una risposta/richiesta stato asincrona simmetrica/asimmetrica
		if(this.sendRispostaApplicativa){
			if(Costanti.SCENARIO_ASINCRONO_ASIMMETRICO_POLLING.equals(this.richiestaDelegata.getScenario()) ||
					Costanti.SCENARIO_ASINCRONO_SIMMETRICO_CONSEGNA_RISPOSTA.equals(this.richiestaDelegata.getScenario())){
				this.rollbackRichiesta = false;
			}
		}

		boolean consegnaAffidabile = false;
		switch (this.protocolManager.getConsegnaAffidabile(this.bustaRichiesta)) {
		case ABILITATA:
			consegnaAffidabile = true;
			break;
		case DISABILITATA:
			consegnaAffidabile = false;
			break;
		default:
			consegnaAffidabile = this.propertiesReader.isGestioneRiscontri(this.implementazionePdDDestinatario) && this.bustaRichiesta.isConfermaRicezione();
			break;
		}
		
		// Gestione riscontri per profilo oneway (No function Router active)
		if(this.functionAsRouter==false){
			if(Costanti.SCENARIO_ONEWAY_INVOCAZIONE_SERVIZIO.equals(this.richiestaDelegata.getScenario()) && (this.sendRispostaApplicativa==false) ){
				this.gestioneBusteNonRiscontrateAttive =consegnaAffidabile;
			}else if (Costanti.SCENARIO_ASINCRONO_ASIMMETRICO_INVOCAZIONE_SERVIZIO.equals(this.richiestaDelegata.getScenario()) ||
					Costanti.SCENARIO_ASINCRONO_SIMMETRICO_INVOCAZIONE_SERVIZIO.equals(this.richiestaDelegata.getScenario()) ||
					Costanti.SCENARIO_ASINCRONO_SIMMETRICO_CONSEGNA_RISPOSTA.equals(this.richiestaDelegata.getScenario()) ){
				if(this.richiestaDelegata.isRicevutaAsincrona()==false)
					this.gestioneBusteNonRiscontrateAttive = true;
			}
		}

		// Enrich SOAPFault
		this.enrichSoapFaultApplicativo = this.propertiesReader.isAggiungiDetailErroreApplicativo_SoapFaultApplicativo();
		this.enrichSoapFaultPdD = this.propertiesReader.isAggiungiDetailErroreApplicativo_SoapFaultPdD();
		if(proprietaErroreAppl!=null){
			this.enrichSoapFaultApplicativo = proprietaErroreAppl.isAggiungiDetailErroreApplicativo_SoapFaultApplicativo();
			this.enrichSoapFaultPdD = proprietaErroreAppl.isAggiungiDetailErroreApplicativo_SoapFaultPdD();
		}
		
		// Modalita' gestione risposta (Sia per PdD normale che per Router)
		// - connectionReply per profilo sincrono
		// - parametro configurabile per altri profili
		if(this.functionAsRouter){
			if(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.SINCRONO.equals(this.bustaRichiesta.getProfiloDiCollaborazione())==false
					&& (this.portaDiTipoStateless==false) && (routingStateless==false) ){
				// New connection puo' essere usata solo se non siamo in modalita stateless){
				this.newConnectionForResponse = this.configurazionePdDManager.newConnectionForResponse();
			}
		}
		
		boolean utilizzoIndirizzoTelematico = false;
		if( (this.portaDiTipoStateless==false) && (routingStateless==false)){
			// New connection puo' essere usata solo se non siamo in modalita stateless
			 utilizzoIndirizzoTelematico = this.configurazionePdDManager.isUtilizzoIndirizzoTelematico();
		}

		// Aggiornamento EJBUtils
		this.ejbUtils.setReplyOnNewConnection(this.newConnectionForResponse);
		this.ejbUtils.setUtilizzoIndirizzoTelematico(utilizzoIndirizzoTelematico);
		this.ejbUtils.setScenarioCooperazione(this.richiestaDelegata.getScenario());

		// Identita' errore
		if(this.functionAsRouter == false){
			this.msgDiag.setServizioApplicativo(this.richiestaDelegata.getServizioApplicativo());
		}

		// Proprieta Manifest Attachments
		this.proprietaManifestAttachments = 
			this.propertiesReader.getProprietaManifestAttachments(this.implementazionePdDDestinatario);
		
		// Read QualifiedAttribute
		this.readQualifiedAttribute = this.propertiesReader.isReadQualifiedAttribute(this.implementazionePdDDestinatario);

		// Validazione id completa
		this.validazioneIDBustaCompleta = this.propertiesReader.isValidazioneIDBustaCompleta(this.implementazionePdDDestinatario);

		
		// Esamina informazioni
		this.soggettoFruitore = this.richiestaDelegata.getIdSoggettoFruitore();
		this.idServizio = this.richiestaDelegata.getIdServizio();
		this.idAccordoServizio = this.richiestaDelegata.getIdAccordo();
		ForwardProxy forwardProxy = null;
		if(this.functionAsRouter==false && this.configurazionePdDManager.isForwardProxyEnabled()) {
			try {
				forwardProxy = this.configurazionePdDManager.getForwardProxyConfigFruizione(this.identitaPdD, this.idServizio);
			}catch(Exception e) {
				this.msgDiag.logErroreGenerico(e, "Configurazione del connettore errata per la funzionalità govway-proxy"); 
				this.openspcoopstate.releaseResource();
				esito.setEsitoInvocazione(false); 
				esito.setStatoInvocazioneErroreNonGestito(e);
				return esito;
			}
		}
		
		


		// Punto di inizio per la transazione.
		try{




			Utilities.printFreeMemory("InoltroBuste - Controllo scadenza busta...");

			/* ------------  Controllo Scadenza Busta  ------------- */
			if(this.bustaRichiesta.getScadenza() != null){
				this.msgDiag.mediumDebug("Controllo scadenza busta...");

				Timestamp now = DateManager.getTimestamp();
				if (this.bustaRichiesta.getScadenza().before(now)) {
					// Busta scaduta
					Eccezione ecc = Eccezione.getEccezioneValidazione(ErroriCooperazione.MESSAGGIO_SCADUTO.getErroreCooperazione(), this.protocolFactory);
					this.msgDiag.logPersonalizzato("inoltroBustaScaduta");
					if(this.functionAsRouter){
						this.ejbUtils.sendAsRispostaBustaErroreValidazione(this.richiestaDelegata.getIdModuloInAttesa(),this.bustaRichiesta,ecc,
								this.idCorrelazioneApplicativa,this.idCorrelazioneApplicativaRisposta,this.servizioApplicativoFruitore);
					}else{
						if(this.sendRispostaApplicativa){
							OpenSPCoop2Message responseMessageError = 
									this.generatoreErrore.build(this.pddContext, IntegrationFunctionError.INTERNAL_REQUEST_ERROR,
											ecc,this.richiestaDelegata.getIdSoggettoFruitore(),null);
							this.ejbUtils.sendRispostaApplicativaErrore(responseMessageError,this.richiestaDelegata,this.rollbackRichiesta,this.pd,this.sa);	
						}else{
							this.ejbUtils.releaseOutboxMessage(true);
						}
					}
					this.openspcoopstate.releaseResource();
					esito.setEsitoInvocazione(true);	
					esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
							this.msgDiag.getMessaggio_replaceKeywords("inoltroBustaScaduta"));
					return esito;
				}
			}









			Utilities.printFreeMemory("InoltroBuste - Routing... ");




			/* ------------------ Routing --------------- */
			this.msgDiag.logPersonalizzato("routingTable.esaminaInCorso");
			
			// ConnectorProperties (Punto di accesso della porta di this.identitaPdD.getCodicePorta() a cui spedire la busta)
			Connettore connettore = null;
			String erroreRicercaConnettore = null;
			Exception eForwardRoute = null;
			if(this.functionAsRouter==false){
				try{
					IProtocolManager pm = this.protocolFactory.createProtocolManager();
					if(pm.isStaticRoute()) {
						org.openspcoop2.core.registry.Connettore connettoreProtocol = 
								pm.getStaticRoute(this.soggettoFruitore,this.idServizio,
										this.protocolFactory.getCachedRegistryReader(this.openspcoopstate.getStatoRichiesta()));
						if(connettoreProtocol!=null) {
							connettore = connettoreProtocol.mappingIntoConnettoreConfigurazione();
						}
					}
				}catch(Exception e){
					eForwardRoute = e;
					erroreRicercaConnettore = e.getMessage();
				}
			}
			if(connettore==null && eForwardRoute==null){ // in pratica se non e' stato trovato un connettore via protocol
				try{
					connettore = this.configurazionePdDManager.getForwardRoute(this.soggettoFruitore,this.idServizio,this.functionAsRouter);
				}catch(Exception e){
					eForwardRoute = e;
					erroreRicercaConnettore = e.getMessage();
				}
				if(this.functionAsRouter){
					if(connettore==null){
						try{
							connettore = this.configurazionePdDManager.getForwardRoute(this.idServizio.getSoggettoErogatore(),this.functionAsRouter);
						}catch(Exception e){
							eForwardRoute = e;
							erroreRicercaConnettore = erroreRicercaConnettore+ "\nRicerca in base al solo soggetto destinatario:\n"+ e.getMessage();
						}
					}
				}
			}
			if (connettore == null) {
				if(erroreRicercaConnettore!=null){
					this.msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, erroreRicercaConnettore);
					erroreRicercaConnettore = "Riscontrato errore durante la ricerca del connettore a cui inoltrare la busta: "+erroreRicercaConnettore;
				}else{
					this.msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, "non definito");
					erroreRicercaConnettore = "Riscontrato errore durante la ricerca del connettore a cui inoltrare la busta: non definito";
				}
				this.msgDiag.logPersonalizzato("routingTable.esaminaInCorsoFallita");
				if(this.functionAsRouter){
					this.ejbUtils.sendAsRispostaBustaErroreProcessamento(this.richiestaDelegata.getIdModuloInAttesa(),this.bustaRichiesta,
							ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
								get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_514_ROUTING_CONFIGURATION_ERROR),
							this.idCorrelazioneApplicativa,this.idCorrelazioneApplicativaRisposta,this.servizioApplicativoFruitore,eForwardRoute,
							(this.responseMessage!=null ? this.responseMessage.getParseException() : null));
					esito.setEsitoInvocazione(true);
					esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
							this.msgDiag.getMessaggio_replaceKeywords("routingTable.esaminaInCorsoFallita"));
				}else{
					if(this.sendRispostaApplicativa){
						OpenSPCoop2Message responseMessageError = 
								this.generatoreErrore.build(this.pddContext,IntegrationFunctionError.INTERNAL_REQUEST_ERROR,
										ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
										get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_514_ROUTING_CONFIGURATION_ERROR),eForwardRoute,
											(this.responseMessage!=null ? this.responseMessage.getParseException() : null));
						this.ejbUtils.sendRispostaApplicativaErrore(responseMessageError,this.richiestaDelegata,this.rollbackRichiesta,this.pd,this.sa);
						esito.setEsitoInvocazione(true);
						esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
								this.msgDiag.getMessaggio_replaceKeywords("routingTable.esaminaInCorsoFallita"));
					}else{
						// Se Non e' attivo una gestione dei riscontri, faccio il rollback sulla coda.
						// Altrimenti verra attivato la gestione dei riscontri che riprovera' dopo un tot.
						if(this.gestioneBusteNonRiscontrateAttive==false){
							this.ejbUtils.rollbackMessage(erroreRicercaConnettore, esito);
							esito.setEsitoInvocazione(false);
							esito.setStatoInvocazione(EsitoLib.ERRORE_NON_GESTITO,
									this.msgDiag.getMessaggio_replaceKeywords("routingTable.esaminaInCorsoFallita"));
						}else{
							this.ejbUtils.updateErroreProcessamentoMessage(erroreRicercaConnettore, esito);
							esito.setEsitoInvocazione(true);
							esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
									this.msgDiag.getMessaggio_replaceKeywords("routingTable.esaminaInCorsoFallita"));
						}
					}
				}
				this.openspcoopstate.releaseResource();
				return esito;
			}
			this.msgDiag.logPersonalizzato("routingTable.esaminaEffettuata");







			Utilities.printFreeMemory("InoltroBuste - Trasmissione...");







			/* ----------- Trasmissione ------------------ */
			String soggettoDestinatarioTrasmissione = "";
			Trasmissione tras = null;
			if(this.propertiesReader.isGenerazioneListaTrasmissioni(this.implementazionePdDDestinatario)){
				this.msgDiag.mediumDebug("Gestione trasmissione della busta ...");
				// Tracciamento in busta
				tras = new Trasmissione();
				
				// origine
				tras.setOrigine(this.identitaPdD.getNome());
				tras.setTipoOrigine(this.identitaPdD.getTipo());
				tras.setIdentificativoPortaOrigine(this.identitaPdD.getCodicePorta());
				
				// destinazione
				IDSoggetto destTrasm = null;
				if(connettore.getNomeDestinatarioTrasmissioneBusta()!=null && connettore.getTipoDestinatarioTrasmissioneBusta()!=null)
					destTrasm = new IDSoggetto(connettore.getTipoDestinatarioTrasmissioneBusta(),connettore.getNomeDestinatarioTrasmissioneBusta());
				else
					destTrasm = new IDSoggetto(this.bustaRichiesta.getTipoDestinatario(),
							this.bustaRichiesta.getDestinatario());	 
				tras.setDestinazione(destTrasm.getNome());
				tras.setTipoDestinazione(destTrasm.getTipo());
				try{
					String dominio = this.registroServiziManager.getDominio(destTrasm, null, this.protocolFactory);
					tras.setIdentificativoPortaDestinazione(dominio);
				}catch(Exception e){}
				
				// oraRegistrazione
				tras.setOraRegistrazione(this.bustaRichiesta.getOraRegistrazione());
				tras.setTempo(this.propertiesReader.getTipoTempoBusta(this.implementazionePdDDestinatario));
				
				this.bustaRichiesta.addTrasmissione(tras);
				// net hop is Router?	
				if( (this.idServizio.getSoggettoErogatore().getNome().equals(destTrasm.getNome())==false) ||
						(this.idServizio.getSoggettoErogatore().getTipo().equals(destTrasm.getTipo())==false)	)
					soggettoDestinatarioTrasmissione = " (tramite router "+destTrasm.getTipo()+"/"+destTrasm.getNome()+")";
			}
			this.msgDiag.addKeyword(CostantiPdD.KEY_DESTINATARIO_TRASMISSIONE, soggettoDestinatarioTrasmissione);







			Utilities.printFreeMemory("InoltroBuste - Lettura messaggio soap della richiesta da spedire...");







			/* ------------  Ricostruzione Messaggio Soap da spedire ------------- */	
			this.msgDiag.mediumDebug("Lettura messaggio soap della richiesta da spedire...");
			try{
				if(this.requestMessagePrimaTrasformazione==null) {
					this.requestMessagePrimaTrasformazione = this.msgRequest.getMessage();
				}
			}catch(Exception e){
				this.msgDiag.logErroreGenerico(e, "this.msgRequest.getMessage()");
				if(this.functionAsRouter){
					this.ejbUtils.sendAsRispostaBustaErroreProcessamento(this.richiestaDelegata.getIdModuloInAttesa(),this.bustaRichiesta,
							ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
								get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_509_READ_REQUEST_MSG),
							this.idCorrelazioneApplicativa,this.idCorrelazioneApplicativaRisposta,this.servizioApplicativoFruitore,e,
							(this.responseMessage!=null ? this.responseMessage.getParseException() : null));
					esito.setEsitoInvocazione(true);
					esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,"this.msgRequest.getMessage()");
				}else{
					if(this.sendRispostaApplicativa){
						OpenSPCoop2Message responseMessageError = 
								this.generatoreErrore.build(this.pddContext,IntegrationFunctionError.INTERNAL_REQUEST_ERROR,
										ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
											get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_509_READ_REQUEST_MSG),e,
												(this.responseMessage!=null ? this.responseMessage.getParseException() : null));
						this.ejbUtils.sendRispostaApplicativaErrore(responseMessageError,this.richiestaDelegata,this.rollbackRichiesta,this.pd,this.sa);
						esito.setEsitoInvocazione(true);
						esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,"this.msgRequest.getMessage()");
					}else{
						// Se Non e' attivo una gestione dei riscontri, faccio il rollback sulla coda.
						// Altrimenti verra attivato la gestione dei riscontri che riprovera' dopo un tot.
						if(this.gestioneBusteNonRiscontrateAttive==false){
							this.ejbUtils.rollbackMessage("Ricostruzione del messaggio Soap da Spedire non riuscita.", esito);
							esito.setStatoInvocazioneErroreNonGestito(e);
							esito.setEsitoInvocazione(false);
						}else{
							this.ejbUtils.updateErroreProcessamentoMessage("Ricostruzione del messaggio Soap da Spedire non riuscita.", esito);
							esito.setEsitoInvocazione(true);
							esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,"this.msgRequest.getMessage()");
						}
					}
				}
				this.openspcoopstate.releaseResource();
				return esito;
			}	


			
			
			
			
			
			
			
			/* -----  Header Integrazione ------ */
			this.msgDiag.mediumDebug("Gestione header di integrazione per la richiesta...");
			HeaderIntegrazione headerIntegrazione = new HeaderIntegrazione(this.idTransazione);
			headerIntegrazione.getBusta().setTipoMittente(this.soggettoFruitore.getTipo());
			headerIntegrazione.getBusta().setMittente(this.soggettoFruitore.getNome());
			headerIntegrazione.getBusta().setTipoDestinatario(this.idServizio.getSoggettoErogatore().getTipo());
			headerIntegrazione.getBusta().setDestinatario(this.idServizio.getSoggettoErogatore().getNome());
			headerIntegrazione.getBusta().setTipoServizio(this.idServizio.getTipo());
			headerIntegrazione.getBusta().setServizio(this.idServizio.getNome());
			headerIntegrazione.getBusta().setVersioneServizio(this.idServizio.getVersione());
			headerIntegrazione.getBusta().setAzione(this.idServizio.getAzione());
			headerIntegrazione.getBusta().setID(this.bustaRichiesta.getID());
			headerIntegrazione.getBusta().setRiferimentoMessaggio(this.bustaRichiesta.getRiferimentoMessaggio());
			headerIntegrazione.getBusta().setIdCollaborazione(this.bustaRichiesta.getCollaborazione());
			headerIntegrazione.getBusta().setProfiloDiCollaborazione(this.bustaRichiesta.getProfiloDiCollaborazione());
			headerIntegrazione.setIdApplicativo(this.idCorrelazioneApplicativa);
			headerIntegrazione.setServizioApplicativo(this.servizioApplicativoFruitore);

			Map<String, List<String>>  propertiesTrasporto = new HashMap<String, List<String>> ();
			Map<String, List<String>>  propertiesUrlBased = new HashMap<String, List<String>> ();

			String [] tipiIntegrazionePD = null;
			try {
				if(this.pd!=null)
					tipiIntegrazionePD = this.configurazionePdDManager.getTipiIntegrazione(this.pd);
			} catch (Exception e) {
				this.msgDiag.logErroreGenerico(e, "getTipiIntegrazione(pd)");
				if(this.functionAsRouter){
					this.ejbUtils.sendAsRispostaBustaErroreProcessamento(this.richiestaDelegata.getIdModuloInAttesa(),this.bustaRichiesta,
							ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
								get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE),
							this.idCorrelazioneApplicativa,this.idCorrelazioneApplicativaRisposta,this.servizioApplicativoFruitore,e,
							(this.responseMessage!=null ? this.responseMessage.getParseException() : null));
					esito.setEsitoInvocazione(true);
					esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,"getTipiIntegrazione(pd)");
				}else{
					if(this.sendRispostaApplicativa){
						OpenSPCoop2Message responseMessageError = 
								this.generatoreErrore.build(this.pddContext,IntegrationFunctionError.INTERNAL_REQUEST_ERROR,
										ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
											get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE),e,
												(this.responseMessage!=null ? this.responseMessage.getParseException() : null));
						this.ejbUtils.sendRispostaApplicativaErrore(responseMessageError,this.richiestaDelegata,this.rollbackRichiesta,this.pd,this.sa);
						esito.setEsitoInvocazione(true);
						esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,"getTipiIntegrazione(pd)");
					}else{
						// Se Non e' attivo una gestione dei riscontri, faccio il rollback sulla coda.
						// Altrimenti verra attivato la gestione dei riscontri che riprovera' dopo un tot.
						if(this.gestioneBusteNonRiscontrateAttive==false){
							this.ejbUtils.rollbackMessage("Gestione header Integrazione Richiesta non riuscita.", esito);
							esito.setStatoInvocazioneErroreNonGestito(e);
							esito.setEsitoInvocazione(false);
						}else{
							this.ejbUtils.updateErroreProcessamentoMessage("Ricostruzione del messaggio Soap da Spedire non riuscita.", esito);
							esito.setEsitoInvocazione(true);
							esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,"getTipiIntegrazione(pd)");
						}
					}
				}
				this.openspcoopstate.releaseResource();
				return esito;
			}
			
			if (tipiIntegrazionePD == null){
				if(InoltroBuste.defaultPerProtocolloGestoreIntegrazionePD.containsKey(this.protocolFactory.getProtocol())){
					tipiIntegrazionePD = InoltroBuste.defaultPerProtocolloGestoreIntegrazionePD.get(this.protocolFactory.getProtocol());
				}else{
					tipiIntegrazionePD = InoltroBuste.defaultGestoriIntegrazionePD;
				}
			}
			
			OutRequestPDMessage outRequestPDMessage = new OutRequestPDMessage();
			outRequestPDMessage.setBustaRichiesta(this.bustaRichiesta);
			outRequestPDMessage.setMessage(this.requestMessagePrimaTrasformazione);
			outRequestPDMessage.setPortaDelegata(this.pd);
			outRequestPDMessage.setHeaders(propertiesTrasporto);
			outRequestPDMessage.setParameters(propertiesUrlBased);
			outRequestPDMessage.setServizio(this.idServizio);
			outRequestPDMessage.setSoggettoMittente(this.soggettoFruitore);
			
			for (int i = 0; i < tipiIntegrazionePD.length; i++) {
				try {					
					IGestoreIntegrazionePD gestore = null;
					try{
						gestore = (IGestoreIntegrazionePD) this.pluginLoader.newIntegrazionePortaDelegata(tipiIntegrazionePD[i]);
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
									"] da utilizzare per la gestione dell'integrazione delle fruizioni di tipo ["+tipiIntegrazionePD[i]+"]: "+e.getMessage());
						}
						if(gestore instanceof IGestoreIntegrazionePDSoap){
							if(this.propertiesReader.deleteHeaderIntegrazioneRequestPD()){
								gestore.setOutRequestHeader(headerIntegrazione,outRequestPDMessage);
							}
							else{
								// gia effettuato l'update dell'header in RicezioneBuste
							}
						}else{
							gestore.setOutRequestHeader(headerIntegrazione,outRequestPDMessage);
						}
					}else{
						throw new Exception("Gestore non inizializzato");
					}
					
				} catch (Exception e) {
					this.msgDiag.addKeyword(CostantiPdD.KEY_TIPO_HEADER_INTEGRAZIONE,tipiIntegrazionePD[i]);
					this.msgDiag.addKeywordErroreProcessamento(e);
					this.msgDiag.logPersonalizzato("headerIntegrazione.creazioneFallita");
					this.log.error(this.msgDiag.getMessaggio_replaceKeywords("headerIntegrazione.creazioneFallita"), e);
				}
			}
			
			
			
			
			
			
			
			/* ------------ Trasformazione Richiesta  -------------- */
			
			this.requestMessageTrasformato = this.requestMessagePrimaTrasformazione;
			if(this.trasformazioni!=null) {
				try {
					this.gestoreTrasformazioni = new GestoreTrasformazioni(this.log, this.msgDiag, this.idServizio, this.soggettoFruitore, this.servizioApplicativoFruitore, 
							this.trasformazioni, this.transactionNullable, this.pddContext, this.requestInfo, this.tipoPdD,
							this.generatoreErrore);
					this.requestMessageTrasformato = this.gestoreTrasformazioni.trasformazioneRichiesta(this.requestMessagePrimaTrasformazione, this.bustaRichiesta);
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
					
					if(this.sendRispostaApplicativa){
						
						IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.TRANSFORMATION_RULE_REQUEST_FAILED;
						if(e.getOp2IntegrationFunctionError()!=null) {
							integrationFunctionError = e.getOp2IntegrationFunctionError();
						}
						OpenSPCoop2Message responseMessageError = null;
						if(e.getOpenSPCoop2ErrorMessage()!=null) {
							responseMessageError = e.getOpenSPCoop2ErrorMessage();
						}
						else {
							responseMessageError = this.generatoreErrore.build(this.pddContext,integrationFunctionError,
									erroreIntegrazione,e,
										(this.responseMessage!=null ? this.responseMessage.getParseException() : null));
						}
						
						this.ejbUtils.sendRispostaApplicativaErrore(responseMessageError,this.richiestaDelegata,this.rollbackRichiesta,this.pd,this.sa);
						esito.setEsitoInvocazione(true);
						esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
								"Trasformazione-Richiesta");
						
					}else{
						// Se Non e' attivo una gestione dei riscontri, faccio il rollback sulla coda.
						// Altrimenti verra attivato la gestione dei riscontri che riprovera' dopo un tot.
						if(this.gestioneBusteNonRiscontrateAttive==false){
							this.ejbUtils.rollbackMessage("Trasformazione della richiesta non riuscita: "+e.getMessage(), esito);
							esito.setEsitoInvocazione(false);
							esito.setStatoInvocazione(EsitoLib.ERRORE_NON_GESTITO,
									"Trasformazione-Richiesta");
						}else{
							this.ejbUtils.updateErroreProcessamentoMessage("Trasformazione della richiesta non riuscita: "+e.getMessage(), esito);
							esito.setEsitoInvocazione(true);
							esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
									"Trasformazione-Richiesta");
						}
					}
					this.openspcoopstate.releaseResource();
					return esito;
				}		
			}
			
			
			





			/* ------------  Gestione Funzionalita' speciali per Attachments (Manifest) ------------- */
			boolean scartaBody = false;
			if(this.functionAsRouter==false){
				boolean allegaBody = 
					this.configurazionePdDManager.isAllegaBody(this.pd);
				if(allegaBody){
					// E' stato effettuato prima l'inserimento del body come allegato.
					// Forzo lo scartamento.
					scartaBody = true;
				}else{
					scartaBody = this.configurazionePdDManager.isScartaBody(this.pd);
				}
			}


			Utilities.printFreeMemory("InoltroBuste - Imbustamento...");
			
			
			
			
			
			
			
			
			/* ------------  Imbustamento ------------- */	
			this.msgDiag.mediumDebug("Imbustamento ...");
			if(this.functionAsRouter){
				this.gestioneManifest = this.configurazionePdDManager.isGestioneManifestAttachments();
			}else{
				this.gestioneManifest = this.configurazionePdDManager.isGestioneManifestAttachments(this.pd, this.protocolFactory);
			}
			org.openspcoop2.protocol.engine.builder.Imbustamento imbustatore = null;
			try{
				this.msgDiag.highDebug("Imbustamento (creoImbustamentoUtils) ...");
				imbustatore = 
						new org.openspcoop2.protocol.engine.builder.Imbustamento(this.log, this.protocolFactory, this.openspcoopstate.getStatoRichiesta());
				this.msgDiag.highDebug("Imbustamento (invokeSdk) ...");
				if(this.functionAsRouter){
					if(this.propertiesReader.isGenerazioneListaTrasmissioni(this.implementazionePdDDestinatario)){
						this.msgDiag.highDebug("Tipo Messaggio Richiesta prima dell'imbustamento ["+this.requestMessageTrasformato.getClass().getName()+"]");
						ProtocolMessage protocolMessage = imbustatore.addTrasmissione(this.requestMessageTrasformato, tras, this.readQualifiedAttribute,
								FaseImbustamento.PRIMA_SICUREZZA_MESSAGGIO);
						if(protocolMessage!=null && !protocolMessage.isPhaseUnsupported()) {
							this.headerBustaRichiesta = protocolMessage.getBustaRawContent();
							this.requestMessageTrasformato = protocolMessage.getMessage(); // updated
						}
						this.msgDiag.highDebug("Tipo Messaggio Richiesta dopo l'imbustamento ["+this.requestMessageTrasformato.getClass().getName()+"]");
					}
					else{
						Validatore v = new Validatore(this.requestMessageTrasformato, this.pddContext, this.openspcoopstate.getStatoRichiesta(),this.log, this.protocolFactory);
						this.headerBustaRichiesta = v.getHeaderProtocollo_senzaControlli();
					}
				}else{
					this.msgDiag.highDebug("Tipo Messaggio Richiesta prima dell'imbustamento ["+this.requestMessageTrasformato.getClass().getName()+"]");
					ProtocolMessage protocolMessage = imbustatore.imbustamentoRichiesta(this.requestMessageTrasformato, this.pddContext,
							this.bustaRichiesta,
							integrazione,this.gestioneManifest,scartaBody,this.proprietaManifestAttachments,
							FaseImbustamento.PRIMA_SICUREZZA_MESSAGGIO);
					if(protocolMessage!=null && !protocolMessage.isPhaseUnsupported()) {
						this.headerBustaRichiesta = protocolMessage.getBustaRawContent();
						this.requestMessageTrasformato = protocolMessage.getMessage(); // updated
					}
					this.msgDiag.highDebug("Tipo Messaggio Richiesta dopo l'imbustamento ["+this.requestMessageTrasformato.getClass().getName()+"]");
				}
				this.msgDiag.highDebug("Imbustamento (invokeSdk) terminato");
			}catch(Exception e){
				String msgErroreImbusta = null;
				if(this.functionAsRouter)
					msgErroreImbusta = "imbustatore.before-sec.addTrasmissione";
				else
					msgErroreImbusta = "imbustatore.before-sec.imbustamento";
				this.msgDiag.logErroreGenerico(e, msgErroreImbusta);
				if(this.functionAsRouter){
					this.ejbUtils.sendAsRispostaBustaErroreProcessamento(this.richiestaDelegata.getIdModuloInAttesa(),this.bustaRichiesta,
							ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
								get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_526_GESTIONE_IMBUSTAMENTO),
							this.idCorrelazioneApplicativa,this.idCorrelazioneApplicativaRisposta,this.servizioApplicativoFruitore,e,
							(this.responseMessage!=null ? this.responseMessage.getParseException() : null));
					esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,msgErroreImbusta);
					esito.setEsitoInvocazione(true);
				}else{
					if(this.sendRispostaApplicativa){
						OpenSPCoop2Message responseMessageError = 
								this.generatoreErrore.build(this.pddContext,IntegrationFunctionError.INTEROPERABILITY_PROFILE_ENVELOPING_REQUEST_FAILED,
										ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
											get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_526_GESTIONE_IMBUSTAMENTO),e,
												(this.responseMessage!=null ? this.responseMessage.getParseException() : null));
						this.ejbUtils.sendRispostaApplicativaErrore(responseMessageError,this.richiestaDelegata,this.rollbackRichiesta,this.pd,this.sa);
						esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,msgErroreImbusta);
						esito.setEsitoInvocazione(true);
					}else{
						// Se Non e' attivo una gestione dei riscontri, faccio il rollback sulla coda.
						// Altrimenti verra attivato la gestione dei riscontri che riprovera' dopo un tot.
						if(this.gestioneBusteNonRiscontrateAttive==false){
							this.ejbUtils.rollbackMessage("Imbustamento non riuscito.", esito);
							esito.setEsitoInvocazione(false);
							esito.setStatoInvocazioneErroreNonGestito(e);
						}else{
							this.ejbUtils.updateErroreProcessamentoMessage("Imbustamento non riuscito.", esito);
							esito.setEsitoInvocazione(true);
							esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,msgErroreImbusta);
						}
					}
				}
				this.openspcoopstate.releaseResource();
				return esito;
			}


			
			
			
			/* ------------ Init MTOM Processor / SecurityContext -------------- */
			if(this.functionAsRouter==false){
			
				this.msgDiag.mediumDebug("init MTOM Processor / SecurityContext ...");
				ErroreIntegrazione erroreIntegrazione = null;
				Exception configException = null;
				String oggetto = null;
				
				MTOMProcessorConfig mtomConfig = null;
				try{
					mtomConfig=this.configurazionePdDManager.getPD_MTOMProcessorForSender(this.pd);
				}catch(Exception e){
					oggetto = "LetturaConfigurazioneMTOMProcessorRoleSender";
					erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(e,CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE);
					configException = e;
				}
				
				if(erroreIntegrazione==null){
					try{
						this.messageSecurityConfig=this.configurazionePdDManager.getPD_MessageSecurityForSender(this.pd, this.log, this.requestMessageTrasformato, this.bustaRichiesta, this.requestInfo, this.pddContext);
					}catch(Exception e){
						oggetto = "LetturaConfigurazioneMessageSecurityRoleSender";
						erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
								get5XX_ErroreProcessamento(e,CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE);
						configException = e;
					}
				}
				
				if(erroreIntegrazione!=null){
					this.msgDiag.logErroreGenerico(configException, oggetto);
					if(this.sendRispostaApplicativa){
						OpenSPCoop2Message responseMessageError = null;
						responseMessageError = this.generatoreErrore.build(this.pddContext,IntegrationFunctionError.INTERNAL_REQUEST_ERROR,
								erroreIntegrazione,configException,
								(this.responseMessage!=null ? this.responseMessage.getParseException() : null));
						this.ejbUtils.sendRispostaApplicativaErrore(responseMessageError,this.richiestaDelegata,this.rollbackRichiesta,this.pd,this.sa);
						esito.setEsitoInvocazione(true);
						esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO, configException.getMessage());
					}else{
						// Se Non e' attivo una gestione dei riscontri, faccio il rollback sulla coda.
						// Altrimenti verra attivato la gestione dei riscontri che riprovera' dopo un tot.
						if(this.gestioneBusteNonRiscontrateAttive==false){
							this.ejbUtils.rollbackMessage(oggetto+" non riuscita: "+configException.getMessage(), esito);
							esito.setEsitoInvocazione(false);
							esito.setStatoInvocazione(EsitoLib.ERRORE_NON_GESTITO, configException.getMessage());
						}else{
							this.ejbUtils.updateErroreProcessamentoMessage(oggetto+" non riuscita: "+configException.getMessage(), esito);
							esito.setEsitoInvocazione(true);
							esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO, configException.getMessage());
						}
					}
					this.openspcoopstate.releaseResource();
					return esito;
				}
				else{
					this.mtomProcessor = new MTOMProcessor(mtomConfig, this.messageSecurityConfig, 
							this.tipoPdD, this.msgDiag, this.log, this.pddContext);
				}
								
			}
			
			
			
			
			
			/* ------------ MTOM Processor BeforeSecurity  -------------- */
			if(this.mtomProcessor!=null){
				try{
					this.mtomProcessor.mtomBeforeSecurity(this.requestMessageTrasformato, RuoloMessaggio.RICHIESTA);
				}catch(Exception e){
					// L'errore viene registrato dentro il metodo this.mtomProcessor.mtomBeforeSecurity
					// this.msgDiag.logErroreGenerico(e,"MTOMProcessor(BeforeSec-"+this.mtomProcessor.getMTOMProcessorType()+")");
				
					ErroreIntegrazione erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(e,CodiceErroreIntegrazione.CODICE_557_MTOM_PROCESSOR_ERROR);
					if(this.sendRispostaApplicativa){
						OpenSPCoop2Message responseMessageError = null;
						responseMessageError = this.generatoreErrore.build(this.pddContext,IntegrationFunctionError.ATTACHMENTS_PROCESSING_REQUEST_FAILED,
								erroreIntegrazione,e,
									(this.responseMessage!=null ? this.responseMessage.getParseException() : null));
						this.ejbUtils.sendRispostaApplicativaErrore(responseMessageError,this.richiestaDelegata,this.rollbackRichiesta,this.pd,this.sa);
						esito.setEsitoInvocazione(true);
						esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
								"MTOMProcessor(BeforeSec-"+this.mtomProcessor.getMTOMProcessorType()+")");
					}else{
						// Se Non e' attivo una gestione dei riscontri, faccio il rollback sulla coda.
						// Altrimenti verra attivato la gestione dei riscontri che riprovera' dopo un tot.
						if(this.gestioneBusteNonRiscontrateAttive==false){
							this.ejbUtils.rollbackMessage("MTOMProcessor(BeforeSec-"+this.mtomProcessor.getMTOMProcessorType()+") non riuscita: "+e.getMessage(), esito);
							esito.setEsitoInvocazione(false);
							esito.setStatoInvocazione(EsitoLib.ERRORE_NON_GESTITO,
									"MTOMProcessor(BeforeSec-"+this.mtomProcessor.getMTOMProcessorType()+")");
						}else{
							this.ejbUtils.updateErroreProcessamentoMessage("MTOMProcessor(BeforeSec-"+this.mtomProcessor.getMTOMProcessorType()+") non riuscita: "+e.getMessage(), esito);
							esito.setEsitoInvocazione(true);
							esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
									"MTOMProcessor(BeforeSec-"+this.mtomProcessor.getMTOMProcessorType()+")");
						}
					}
					this.openspcoopstate.releaseResource();
					return esito;
				}
			}
			else{
				if(this.functionAsRouter==false){
					this.msgDiag.logPersonalizzato("mtom.beforeSecurity.processamentoRichiestaDisabilitato");
				}
			}
			
			
			
			



			Utilities.printFreeMemory("InoltroBuste - Gestione MessageSecurity della richiesta ...");


			/* ------------  Gestione Message-Security ------------- */
			this.messageSecurityFactory = new MessageSecurityFactory();
			if(this.functionAsRouter==false){

				// ottiene le proprieta' Message-Security relative alla porta delegata:
				// OneWay -> RequestFlow
				// Sincrono --> RequestFlow
				// Asincrono --> RequestFlow
				String msgErrore = null;
				ErroreIntegrazione erroreIntegrazione = null;
				CodiceErroreCooperazione codiceErroreCooperazione = null;
				Exception messageSecurityException = null;
				if(this.messageSecurityConfig!=null && this.messageSecurityConfig.getFlowParameters()!=null && 
						this.messageSecurityConfig.getFlowParameters().size() > 0){
					try{
						
						this.msgDiag.mediumDebug("Inizializzazione contesto di Message Security della richiesta ...");
						
						// Imposto un context di Base (utilizzato per la successiva ricezione)
						MessageSecurityContextParameters contextParameters = new MessageSecurityContextParameters();
						contextParameters.setUseActorDefaultIfNotDefined(this.propertiesReader.isGenerazioneActorDefault(this.implementazionePdDDestinatario));
						contextParameters.setActorDefault(this.propertiesReader.getActorDefault(this.implementazionePdDDestinatario));
						contextParameters.setLog(this.log);
						contextParameters.setFunctionAsClient(SecurityConstants.SECURITY_CLIENT);
						contextParameters.setPrefixWsuId(this.propertiesReader.getPrefixWsuId());
						contextParameters.setRemoveAllWsuIdRef(this.propertiesReader.isRemoveAllWsuIdRef());
						contextParameters.setIdFruitore(this.soggettoFruitore);
						contextParameters.setIdServizio(this.idServizio);
						contextParameters.setPddFruitore(this.registroServiziManager.getIdPortaDominio(this.soggettoFruitore, null));
						contextParameters.setPddErogatore(this.registroServiziManager.getIdPortaDominio(this.idServizio.getSoggettoErogatore(), null));
						
						this.messageSecurityContext = this.messageSecurityFactory.getMessageSecurityContext(contextParameters);
						this.messageSecurityContext.setOutgoingProperties(this.messageSecurityConfig.getFlowParameters());
						
						String tipoSicurezza = SecurityConstants.convertActionToString(this.messageSecurityContext.getOutgoingProperties());
						this.msgDiag.addKeyword(CostantiPdD.KEY_TIPO_SICUREZZA_MESSAGGIO_RICHIESTA, tipoSicurezza);
						this.pddContext.addObject(CostantiPdD.KEY_TIPO_SICUREZZA_MESSAGGIO_RICHIESTA, tipoSicurezza);
						
						this.msgDiag.mediumDebug("Inizializzazione contesto di Message Security della richiesta completata con successo");
						
						if(org.openspcoop2.security.message.engine.WSSUtilities.isNormalizeToSaajImpl(this.messageSecurityContext)){
							this.msgDiag.mediumDebug("Normalize to saajImpl");
							//System.out.println("InoltroBusteEgov.request.normalize");
							this.requestMessageTrasformato = this.requestMessageTrasformato.normalizeToSaajImpl();
						}
						
						this.msgDiag.logPersonalizzato("messageSecurity.processamentoRichiestaInCorso");
						if(this.messageSecurityContext.processOutgoing(this.requestMessageTrasformato,this.pddContext.getContext(),
								this.transactionNullable!=null ? this.transactionNullable.getTempiElaborazione() : null) == false){
							msgErrore = this.messageSecurityContext.getMsgErrore();
							codiceErroreCooperazione = this.messageSecurityContext.getCodiceErrore();
							
							this.msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO , "["+codiceErroreCooperazione+"] "+msgErrore );
							this.msgDiag.logPersonalizzato("messageSecurity.processamentoRichiestaInErrore");
						}
						else{
							this.msgDiag.logPersonalizzato("messageSecurity.processamentoRichiestaEffettuato");
						}
						
					}catch(Exception e){
						
						this.msgDiag.addKeywordErroreProcessamento(e);
						this.msgDiag.logPersonalizzato("messageSecurity.processamentoRichiestaInErrore");
						this.log.error("[MessageSecurityRequest]" + e.getMessage(),e);
						
						erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
								get5XX_ErroreProcessamento(e,CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE);
						messageSecurityException = e;
					}
				}
				else{
					this.msgDiag.logPersonalizzato("messageSecurity.processamentoRichiestaDisabilitato");
				}
				if(erroreIntegrazione==null && codiceErroreCooperazione==null){
					IDigestReader digestReader = null;
					if(this.messageSecurityContext != null) {
						digestReader = this.messageSecurityContext.getDigestReader(this.requestMessageTrasformato!=null ? this.requestMessageTrasformato.getFactory() : OpenSPCoop2MessageFactory.getDefaultMessageFactory());
					}
					if(digestReader!=null){
						try{
							this.msgDiag.mediumDebug("Lettura informazioni sulla Sicurezza dal Messaggio di richiesta ...");
							this.securityInfo = this.validazioneSemantica.readSecurityInformation(digestReader,this.requestMessageTrasformato);
							this.msgDiag.mediumDebug("Lettura informazioni sulla Sicurezza dal Messaggio di richiesta completata con successo");
						}catch(Exception e){
							this.msgDiag.logErroreGenerico(e,"ErroreLetturaInformazioniSicurezza");
							erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
								get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_549_SECURITY_INFO_READER_ERROR);
						}
					}
				}
				if(erroreIntegrazione!=null || codiceErroreCooperazione!= null){
					if(this.sendRispostaApplicativa){
						OpenSPCoop2Message responseMessageError = null;
						if(erroreIntegrazione!=null){
							responseMessageError = this.generatoreErrore.build(this.pddContext,IntegrationFunctionError.MESSAGE_SECURITY_REQUEST_FAILED,
									erroreIntegrazione,messageSecurityException,
										(this.responseMessage!=null ? this.responseMessage.getParseException() : null));
						}else{
							Eccezione ecc = Eccezione.getEccezioneValidazione(ErroriCooperazione.MESSAGE_SECURITY.getErroreMessageSecurity(msgErrore, codiceErroreCooperazione), this.protocolFactory);
							responseMessageError = this.generatoreErrore.build(this.pddContext,IntegrationFunctionError.MESSAGE_SECURITY_REQUEST_FAILED,
									ecc,this.richiestaDelegata.getIdSoggettoFruitore(),null);
						}
						this.ejbUtils.sendRispostaApplicativaErrore(responseMessageError,this.richiestaDelegata,this.rollbackRichiesta,this.pd,this.sa);
						esito.setEsitoInvocazione(true);
						esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,msgErrore);
					}else{
						// Se Non e' attivo una gestione dei riscontri, faccio il rollback sulla coda.
						// Altrimenti verra attivato la gestione dei riscontri che riprovera' dopo un tot.
						if(this.gestioneBusteNonRiscontrateAttive==false){
							this.ejbUtils.rollbackMessage("Applicazione MessageSecurity-Sender non riuscita: "+msgErrore, esito);
							esito.setEsitoInvocazione(false);
							esito.setStatoInvocazione(EsitoLib.ERRORE_NON_GESTITO,msgErrore);
						}else{
							this.ejbUtils.updateErroreProcessamentoMessage("Applicazione MessageSecurity-Sender non riuscita: "+msgErrore, esito);
							esito.setEsitoInvocazione(true);
							esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,msgErrore);
						}
					}
					this.openspcoopstate.releaseResource();
					return esito;
				}
			}  
			
			
			
			
			
			
			/* ------------ MTOM Processor AfterSecurity  -------------- */
			if(this.mtomProcessor!=null){
				try{
					this.mtomProcessor.mtomAfterSecurity(this.requestMessageTrasformato, RuoloMessaggio.RICHIESTA);
				}catch(Exception e){
					// L'errore viene registrato dentro il metodo this.mtomProcessor.mtomAfterSecurity
					//this.msgDiag.logErroreGenerico(e,"MTOMProcessor(AfterSec-"+this.mtomProcessor.getMTOMProcessorType()+")");
				
					ErroreIntegrazione erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(e,CodiceErroreIntegrazione.CODICE_557_MTOM_PROCESSOR_ERROR);
					if(this.sendRispostaApplicativa){
						OpenSPCoop2Message responseMessageError = null;
						responseMessageError = this.generatoreErrore.build(this.pddContext,IntegrationFunctionError.ATTACHMENTS_PROCESSING_REQUEST_FAILED,
								erroreIntegrazione,e,
									(this.responseMessage!=null ? this.responseMessage.getParseException() : null));
						this.ejbUtils.sendRispostaApplicativaErrore(responseMessageError,this.richiestaDelegata,this.rollbackRichiesta,this.pd,this.sa);
						esito.setEsitoInvocazione(true);
						esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
								"MTOMProcessor(AfterSec-"+this.mtomProcessor.getMTOMProcessorType()+")");
					}else{
						// Se Non e' attivo una gestione dei riscontri, faccio il rollback sulla coda.
						// Altrimenti verra attivato la gestione dei riscontri che riprovera' dopo un tot.
						if(this.gestioneBusteNonRiscontrateAttive==false){
							this.ejbUtils.rollbackMessage("MTOMProcessor(AfterSec-"+this.mtomProcessor.getMTOMProcessorType()+") non riuscita: "+e.getMessage(), esito);
							esito.setEsitoInvocazione(false);
							esito.setStatoInvocazione(EsitoLib.ERRORE_NON_GESTITO,
									"MTOMProcessor(AfterSec-"+this.mtomProcessor.getMTOMProcessorType()+")");
						}else{
							this.ejbUtils.updateErroreProcessamentoMessage("MTOMProcessor(AfterSec-"+this.mtomProcessor.getMTOMProcessorType()+") non riuscita: "+e.getMessage(), esito);
							esito.setEsitoInvocazione(true);
							esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
									"MTOMProcessor(AfterSec-"+this.mtomProcessor.getMTOMProcessorType()+")");
						}
					}
					this.openspcoopstate.releaseResource();
					return esito;
				}
			}
			
			
			
			
			
			
			/* ------------  Imbustamento ------------- */	
			this.msgDiag.mediumDebug("Imbustamento (after security) ...");
			try{
				this.msgDiag.highDebug("Imbustamento (after security) (invokeSdk) ...");
				if(this.functionAsRouter){
					if(this.propertiesReader.isGenerazioneListaTrasmissioni(this.implementazionePdDDestinatario)){
						this.msgDiag.highDebug("Tipo Messaggio Richiesta prima dell'imbustamento (after security) ["+this.requestMessageTrasformato.getClass().getName()+"]");
						ProtocolMessage protocolMessage = imbustatore.addTrasmissione(this.requestMessageTrasformato, tras, this.readQualifiedAttribute,
								FaseImbustamento.DOPO_SICUREZZA_MESSAGGIO);
						if(protocolMessage!=null && !protocolMessage.isPhaseUnsupported()) {
							this.headerBustaRichiesta = protocolMessage.getBustaRawContent();
							this.requestMessageTrasformato = protocolMessage.getMessage(); // updated
						}
						this.msgDiag.highDebug("Tipo Messaggio Richiesta dopo l'imbustamento (after security) ["+this.requestMessageTrasformato.getClass().getName()+"]");
					}
					else{
						// gia' effettuata in fase PRIMA_SICUREZZA_MESSAGGIO
					}
				}else{
					this.msgDiag.highDebug("Tipo Messaggio Richiesta prima dell'imbustamento (after security) ["+this.requestMessageTrasformato.getClass().getName()+"]");
					ProtocolMessage protocolMessage = imbustatore.imbustamentoRichiesta(this.requestMessageTrasformato, this.pddContext,
							this.bustaRichiesta,
							integrazione,this.gestioneManifest,scartaBody,this.proprietaManifestAttachments,
							FaseImbustamento.DOPO_SICUREZZA_MESSAGGIO);
					if(protocolMessage!=null && !protocolMessage.isPhaseUnsupported()) {
						this.headerBustaRichiesta = protocolMessage.getBustaRawContent();
						this.requestMessageTrasformato = protocolMessage.getMessage(); // updated
					}
					this.msgDiag.highDebug("Tipo Messaggio Richiesta dopo l'imbustamento (after security) ["+this.requestMessageTrasformato.getClass().getName()+"]");
				}
				this.msgDiag.highDebug("Imbustamento (after security) (invokeSdk) terminato");
			}catch(Exception e){
				String msgErroreImbusta = null;
				if(this.functionAsRouter)
					msgErroreImbusta = "imbustatore.after-sec.addTrasmissione";
				else
					msgErroreImbusta = "imbustatore.after-sec.imbustamento";
				this.msgDiag.logErroreGenerico(e, msgErroreImbusta);
				if(this.functionAsRouter){
					this.ejbUtils.sendAsRispostaBustaErroreProcessamento(this.richiestaDelegata.getIdModuloInAttesa(),this.bustaRichiesta,
							ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
								get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_526_GESTIONE_IMBUSTAMENTO),
							this.idCorrelazioneApplicativa,this.idCorrelazioneApplicativaRisposta,this.servizioApplicativoFruitore,e,
							(this.responseMessage!=null ? this.responseMessage.getParseException() : null));
					esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,msgErroreImbusta);
					esito.setEsitoInvocazione(true);
				}else{
					if(this.sendRispostaApplicativa){
						
						ErroreIntegrazione erroreIntegrazione = null;
						if(e!=null && e instanceof ProtocolException && ((ProtocolException)e).isInteroperabilityError() ) {
							erroreIntegrazione = ErroriIntegrazione.ERRORE_439_FUNZIONALITA_NOT_SUPPORTED_BY_PROTOCOL.
									getErrore439_FunzionalitaNotSupportedByProtocol(e.getMessage(), this.protocolFactory);
						}
						else {
							erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
									get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_526_GESTIONE_IMBUSTAMENTO);
						}
						
						OpenSPCoop2Message responseMessageError = 
								this.generatoreErrore.build(this.pddContext,IntegrationFunctionError.INTEROPERABILITY_PROFILE_ENVELOPING_REQUEST_FAILED,
										erroreIntegrazione,e,
												(this.responseMessage!=null ? this.responseMessage.getParseException() : null));
						this.ejbUtils.sendRispostaApplicativaErrore(responseMessageError,this.richiestaDelegata,this.rollbackRichiesta,this.pd,this.sa);
						esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,msgErroreImbusta);
						esito.setEsitoInvocazione(true);
					}else{
						// Se Non e' attivo una gestione dei riscontri, faccio il rollback sulla coda.
						// Altrimenti verra attivato la gestione dei riscontri che riprovera' dopo un tot.
						if(this.gestioneBusteNonRiscontrateAttive==false){
							this.ejbUtils.rollbackMessage("Imbustamento non riuscito (after-security).", esito);
							esito.setEsitoInvocazione(false);
							esito.setStatoInvocazioneErroreNonGestito(e);
						}else{
							this.ejbUtils.updateErroreProcessamentoMessage("Imbustamento non riuscito (after-security).", esito);
							esito.setEsitoInvocazione(true);
							esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,msgErroreImbusta);
						}
					}
				}
				this.openspcoopstate.releaseResource();
				return esito;
			}
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			

			Utilities.printFreeMemory("InoltroBuste - Impostazione messaggio del connettore...");





			/* ------------------- Preparo Spedizione -----------------------*/
			
			
			this.msgDiag.mediumDebug("Impostazione messaggio del connettore...");
			// Connettore per consegna
			this.tipoConnector = connettore.getTipo();
			this.msgDiag.addKeyword(CostantiPdD.KEY_TIPO_CONNETTORE, this.tipoConnector);
			org.openspcoop2.core.config.Property [] cps = null;
			if(connettore.getPropertyList().size()>0){
				cps = connettore.getPropertyList().toArray(new org.openspcoop2.core.config.Property[connettore.getPropertyList().size()]);
			}
			this.connettoreMsg = new ConnettoreMsg(this.tipoConnector,this.requestMessageTrasformato,cps);
			this.connettoreMsg.setBusta(this.bustaRichiesta);
			this.connettoreMsg.setIdModulo(InoltroBuste.ID_MODULO);
			this.connettoreMsg.setMsgDiagnostico(this.msgDiag);
			this.connettoreMsg.setState(this.openspcoopstate.getStatoRichiesta());
			this.connettoreMsg.setProtocolFactory(this.protocolFactory);
			this.connettoreMsg.setPropertiesTrasporto(propertiesTrasporto);
			this.connettoreMsg.setPropertiesUrlBased(propertiesUrlBased);
			this.connettoreMsg.initPolicyGestioneToken(this.configurazionePdDManager);
			this.connettoreMsg.setForwardProxy(forwardProxy);
			this.connettoreMsg.setIdAccordo(this.idAccordoServizio);
			
			// mapping per forward token
			TokenForward tokenForward = null;
			Object oTokenForward = this.requestMessageTrasformato.getContextProperty(org.openspcoop2.pdd.core.token.Costanti.MSG_CONTEXT_TOKEN_FORWARD);
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
						baseHttp.setHttpMethod(this.requestMessageTrasformato);
						
						if(ServiceBinding.REST.equals(this.requestMessageTrasformato.getServiceBinding())){
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
			this.location = ConnettoreUtils.getAndReplaceLocationWithBustaValues(this.connectorSender, this.connettoreMsg, this.bustaRichiesta, this.pddContext, this.protocolFactory, this.log);
			if(this.location!=null){
				String locationWithUrl = ConnettoreUtils.buildLocationWithURLBasedParameter(this.requestMessageTrasformato, this.tipoConnector, this.connettoreMsg.getPropertiesUrlBased(), this.location,
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
				this.connettoreMsg.getConnectorProperties().put(CostantiConnettori.CONNETTORE_CONNECTION_TIMEOUT,""+this.propertiesReader.getConnectionTimeout_inoltroBuste());
			}
			if(this.connettoreMsg.getConnectorProperties().get(CostantiConnettori.CONNETTORE_READ_CONNECTION_TIMEOUT)==null){
				this.connettoreMsg.getConnectorProperties().put(CostantiConnettori.CONNETTORE_READ_CONNECTION_TIMEOUT,""+this.propertiesReader.getReadConnectionTimeout_inoltroBuste());
			}

			// User-Agent e X-* header
			UtilitiesIntegrazione httpUtilities = UtilitiesIntegrazione.getInstancePDRequest(this.log);
			if(this.connettoreMsg.getPropertiesTrasporto()==null){
				Map<String, List<String>> trasporto = new HashMap<String, List<String>>();
				this.connettoreMsg.setPropertiesTrasporto(trasporto);
			}
			httpUtilities.setInfoProductTransportProperties(this.connettoreMsg.getPropertiesTrasporto());
			
			
			
			
			
			
			
			
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
				this.outRequestContext.setMessaggio(this.requestMessageTrasformato);
				
				// Contesto
				ProtocolContext protocolContext = new ProtocolContext();
				protocolContext.setFruitore(this.soggettoFruitore);
				if(this.bustaRichiesta!=null){
					protocolContext.setIndirizzoFruitore(this.bustaRichiesta.getIndirizzoMittente());
				}
				protocolContext.setIdRichiesta(this.idMessageRequest);
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
				if(this.bustaRichiesta!=null){
					protocolContext.setProfiloCollaborazione(this.bustaRichiesta.getProfiloDiCollaborazione(),this.bustaRichiesta.getProfiloDiCollaborazioneValue());
					protocolContext.setCollaborazione(this.bustaRichiesta.getCollaborazione());
				}
				protocolContext.setDominio(this.msgDiag.getDominio());
				protocolContext.setScenarioCooperazione(this.richiestaDelegata.getScenario());
				this.outRequestContext.setProtocollo(protocolContext);
				
				// Integrazione
				IntegrationContext integrationContext = new IntegrationContext();
				integrationContext.setIdCorrelazioneApplicativa(this.idCorrelazioneApplicativa);
				integrationContext.setServizioApplicativoFruitore(this.servizioApplicativoFruitore);
				integrationContext.setGestioneStateless(this.portaDiTipoStateless);
				integrationContext.setIdPD(idPD);
				this.outRequestContext.setIntegrazione(integrationContext);
				
				// Altre informazioni
				this.outRequestContext.setDataElaborazioneMessaggio(DateManager.getDate());
				this.outRequestContext.setPddContext(this.pddContext);
				if(this.functionAsRouter)
					this.outRequestContext.setTipoPorta(TipoPdD.ROUTER);
				else
					this.outRequestContext.setTipoPorta(TipoPdD.DELEGATA);
				this.outRequestContext.setIdModulo(this.idModulo);
								
				// Invocazione handler
				GestoreHandlers.outRequest(this.outRequestContext, this.msgDiag, this.log);
				
				// Riporto messaggio
				this.requestMessageTrasformato = this.outRequestContext.getMessaggio();
				
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
					if((this.functionAsRouter || this.sendRispostaApplicativa)) {
						erroreIntegrazione = he.convertToErroreIntegrazione();
						integrationFunctionError = he.getIntegrationFunctionError();
					}
				}else{
					this.msgDiag.logErroreGenerico(e, "OutRequestHandler");
					msgErrore = "OutRequestHandler error: "+msgErrore;
				}
				if(this.functionAsRouter){
					if(erroreIntegrazione==null){
						erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
								get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_543_HANDLER_OUT_REQUEST);
					}
					this.ejbUtils.sendAsRispostaBustaErroreProcessamento(this.richiestaDelegata.getIdModuloInAttesa(),this.bustaRichiesta,
							erroreIntegrazione,
							this.idCorrelazioneApplicativa,this.idCorrelazioneApplicativaRisposta,this.servizioApplicativoFruitore,e,
							(this.responseMessage!=null ? this.responseMessage.getParseException() : null));
					esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,msgErrore);
					esito.setEsitoInvocazione(true);
				}else{
					if(this.sendRispostaApplicativa){
						if(erroreIntegrazione==null){
							erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
									get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_543_HANDLER_OUT_REQUEST);
						}
						if(integrationFunctionError==null) {
							integrationFunctionError = IntegrationFunctionError.INTERNAL_REQUEST_ERROR;
						}
						OpenSPCoop2Message responseMessageError = 
								this.generatoreErrore.build(this.pddContext,integrationFunctionError,erroreIntegrazione,e,
									(this.responseMessage!=null ? this.responseMessage.getParseException() : null));
						if(e instanceof HandlerException){
							HandlerException he = (HandlerException) e;
							he.customized(responseMessageError);
						}
						this.ejbUtils.sendRispostaApplicativaErrore(responseMessageError,this.richiestaDelegata,this.rollbackRichiesta,this.pd,this.sa);
						esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,msgErrore);
						esito.setEsitoInvocazione(true);
					}else{
						// Se Non e' attivo una gestione dei riscontri, faccio il rollback sulla coda.
						// Altrimenti verra attivato la gestione dei riscontri che riprovera' dopo un tot.
						if(this.gestioneBusteNonRiscontrateAttive==false){
							this.ejbUtils.rollbackMessage(msgErrore, esito);
							esito.setEsitoInvocazione(false);
							esito.setStatoInvocazioneErroreNonGestito(e);
						}else{
							this.ejbUtils.updateErroreProcessamentoMessage(msgErrore, esito);
							esito.setEsitoInvocazione(true);
							esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,msgErrore);
						}
					}
				}
				this.openspcoopstate.releaseResource();
				return esito;
			}
			
			
			
			
			
			
			
			
			
			/* --------------- REFRESH LOCATION ----------------- */
			// L'handler puo' aggiornare le properties che contengono le proprieta' del connettore.
			this.location = ConnettoreUtils.getAndReplaceLocationWithBustaValues(this.connectorSender, this.connettoreMsg, this.bustaRichiesta, this.pddContext, this.protocolFactory, this.log);
			if(this.location!=null){
				String locationWithUrl = ConnettoreUtils.buildLocationWithURLBasedParameter(this.requestMessageTrasformato, this.tipoConnector, this.connettoreMsg.getPropertiesUrlBased(), this.location,
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

			
			
			
			
			
			
			
			
			Utilities.printFreeMemory("InoltroBuste - Dump...");
			
			
			
			
			/* ------------------- Dump -----------------------*/
			this.dumpConfig = this.configurazionePdDManager.getDumpConfigurazione(this.pd);
			Dump dumpApplicativoRichiesta = new Dump(this.identitaPdD,InoltroBuste.ID_MODULO,this.idMessageRequest,
					this.soggettoFruitore,this.idServizio,this.tipoPdD,this.msgDiag.getPorta(), this.pddContext,
					this.openspcoopstate.getStatoRichiesta(),this.openspcoopstate.getStatoRisposta(),
					this.dumpConfig);
			dumpApplicativoRichiesta.dumpRichiestaUscita(this.requestMessageTrasformato, this.outRequestContext.getConnettore());
			
			
			
			
			
			
			
			
			
			
			
			
			
			/* ------------------- 
			   Rilascio Risorsa al DB (La riprendero' dopo aver ottenuto la risposta, se necessario) 
			   Le informazioni nel DB sono state utilizzate fino a questo punto solo in lettura.
			   -----------------------*/
			this.msgDiag.mediumDebug("Rilascio risorse...");
			this.openspcoopstate.releaseResource();
			
			
			
			
			
			
			
			Utilities.printFreeMemory("InoltroBuste - spedizione..");
			
			
			

			// --------------------- spedizione --------------------------
			if(this.invokerNonSupportato==false){
				// utilizzo connettore
				this.msgDiag.logPersonalizzato("inoltroInCorso");
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
						
			
			Utilities.printFreeMemory("InoltroBuste - Richiesta risorsa per la gestione della risposta...");
			
			
			
					
			/* ------------  Re-ottengo Connessione al DB -------------- */
			this.msgDiag.mediumDebug("Richiesta risorsa per la gestione della risposta...");
			try{
				
				boolean gestioneAsincroniStateless = 
					(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO.equals(this.bustaRichiesta.getProfiloDiCollaborazione()) ||
							org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO.equals(this.bustaRichiesta.getProfiloDiCollaborazione()))
					&& this.configurazionePdDManager.isModalitaStateless(this.pd, this.bustaRichiesta.getProfiloDiCollaborazione());
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
				this.repositoryBuste.updateState(this.openspcoopstate.getStatoRichiesta());
				
				// POOL,TRANSACTIONISOLATION:
				//connectionDB.setTransactionIsolation(DBManager.getTransactionIsolationLevel());
			}catch(Exception e){
				this.msgDiag.logErroreGenerico(e,"this.openspcoopstate.updateResource()");
				this.openspcoopstate.releaseResource();
				esito.setEsitoInvocazione(false);	
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
					this.gestioneConsegnaConnettore =this.configurazionePdDManager.getGestioneErroreConnettoreComponenteCooperazione(this.protocolFactory, this.requestMessageTrasformato.getServiceBinding());
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
					// raccolta risultati del connettore
					this.soapFault = gestoreErrore.getFault();
					this.restProblem = gestoreErrore.getProblem();
					this.faultMessageFactory = this.connectorSender.getResponse()!=null ? this.connectorSender.getResponse().getFactory() : OpenSPCoop2MessageFactory.getDefaultMessageFactory();
					this.codiceRitornato = this.connectorSender.getCodiceTrasporto();
					this.transportResponseContext = new TransportResponseContext(this.connectorSender.getCodiceTrasporto()+"", 
							this.connectorSender.getHeaderTrasporto(), 
							this.connectorSender.getContentLength(), 
							this.motivoErroreConsegna, this.connectorSender.getEccezioneProcessamento());
					this.responseMessage = this.connectorSender.getResponse();	
					if(this.responseMessage!=null){
						this.responseMessage.setTransportRequestContext(this.requestMessagePrimaTrasformazione.getTransportRequestContext());
						this.responseMessage.setTransportResponseContext(this.transportResponseContext);
					}			
					// gestione connessione connettore
					if(this.functionAsRouter){
						RepositoryConnettori.salvaConnettorePA(
								//idMessageRequest, 
								this.idTransazione,
								this.connectorSender);
					}
					else{
						if(this.sendRispostaApplicativa ) {
							RepositoryConnettori.salvaConnettorePD(
									//idMessageRequest, 
									this.idTransazione,
									this.connectorSender);
						}
						else{
							// Sono nella casistica di messaggio preso in carico.
							// Non si deve chiudere immediatamente la connessione, poiche' nel resto del modulo, il messaggio puo' ancora essere utilizzato (es. dump)
							this.connectorSenderForDisconnect = this.connectorSender;
						}
					}
					
					this.msgDiag.addKeyword(CostantiPdD.KEY_CODICE_CONSEGNA, this.codiceRitornato+"");
					
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
					if(this.functionAsRouter){
						this.ejbUtils.sendAsRispostaBustaErroreProcessamento(this.richiestaDelegata.getIdModuloInAttesa(),this.bustaRichiesta,
								ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
									get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_500_ERRORE_INTERNO),
								this.idCorrelazioneApplicativa,this.idCorrelazioneApplicativaRisposta,this.servizioApplicativoFruitore,e,
								(this.connectorSender!=null && this.connectorSender.getResponse()!=null ? this.connectorSender.getResponse().getParseException() : null));
						esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,msgErrore);
						esito.setEsitoInvocazione(true);
					}else{
						if(this.sendRispostaApplicativa){
							OpenSPCoop2Message responseMessageError = 
									this.generatoreErrore.build(this.pddContext,IntegrationFunctionError.INTERNAL_RESPONSE_ERROR,
											ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
												get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_500_ERRORE_INTERNO),e,
													(this.connectorSender!=null && this.connectorSender.getResponse()!=null ? this.connectorSender.getResponse().getParseException() : null));
							this.ejbUtils.sendRispostaApplicativaErrore(responseMessageError,this.richiestaDelegata,this.rollbackRichiesta,this.pd,this.sa);
							esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,msgErrore);
							esito.setEsitoInvocazione(true);
						}else{
							// Se Non e' attivo una gestione dei riscontri, faccio il rollback sulla coda.
							// Altrimenti verra attivato la gestione dei riscontri che riprovera' dopo un tot.
							if(this.gestioneBusteNonRiscontrateAttive==false){
								this.ejbUtils.rollbackMessage(msgErrore, esito);
								esito.setEsitoInvocazione(false);
								esito.setStatoInvocazioneErroreNonGestito(e);
							}else{
								this.ejbUtils.updateErroreProcessamentoMessage(msgErrore, esito);
								esito.setEsitoInvocazione(true);
								esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,msgErrore);
							}
						}
					}
					this.openspcoopstate.releaseResource();
					return esito;
				}
			}



			
			
			

			
			
			
			

			
			
			/* ------------ Trasformazione Risposta  -------------- */
			
			boolean dumpRispostaEffettuato = false;
			if(this.trasformazioni!=null && this.responseMessage!=null) {
				try {
					
					// prima effettuo dump applicativo
					if(this.responseMessage!=null ){
						Dump dumpApplicativo = new Dump(this.identitaPdD,InoltroBuste.ID_MODULO,this.idMessageRequest,
								this.soggettoFruitore,this.idServizio,this.tipoPdD,this.msgDiag.getPorta(), this.pddContext,
								this.openspcoopstate.getStatoRichiesta(),this.openspcoopstate.getStatoRisposta(),
								this.dumpConfig);
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
					StringBuilder bfMsgErroreSituazioneAnomale = new StringBuilder();
					EsitoElaborazioneMessaggioTracciato esitoTraccia = gestioneTracciamentoFineConnettore(bfMsgErroreSituazioneAnomale);
					if(esitoTraccia!=null) {
						this.tracciamento.registraRichiesta(this.requestMessageTrasformato,this.securityInfo,this.headerBustaRichiesta,this.bustaRichiesta,esitoTraccia,
								Tracciamento.createLocationString(false, this.location),
								this.idCorrelazioneApplicativa);
					}
					
					this.msgDiag.addKeywordErroreProcessamento(e);
					this.msgDiag.logPersonalizzato("trasformazione.processamentoRispostaInErrore");
					
					ErroreIntegrazione erroreIntegrazione = this.gestoreTrasformazioni.getErrore();
					if(erroreIntegrazione==null) {
						erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
								get5XX_ErroreProcessamento(e,CodiceErroreIntegrazione.CODICE_562_TRASFORMAZIONE);
					}
					
					if(this.sendRispostaApplicativa){
						
						IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.TRANSFORMATION_RULE_RESPONSE_FAILED;
						if(e instanceof GestoreTrasformazioniException && (((GestoreTrasformazioniException)e).getOp2IntegrationFunctionError()!=null)) {
							integrationFunctionError = ((GestoreTrasformazioniException)e).getOp2IntegrationFunctionError();
						}
						
						OpenSPCoop2Message responseMessageError = null;
						if(e instanceof GestoreTrasformazioniException && (((GestoreTrasformazioniException)e).getOpenSPCoop2ErrorMessage()!=null)) {
							responseMessageError = ((GestoreTrasformazioniException)e).getOpenSPCoop2ErrorMessage();
						}
						else {
							responseMessageError = this.generatoreErrore.build(this.pddContext,integrationFunctionError,
									erroreIntegrazione,e,
										(this.responseMessage!=null ? this.responseMessage.getParseException() : null));
						}
						this.ejbUtils.sendRispostaApplicativaErrore(responseMessageError,this.richiestaDelegata,this.rollbackRichiesta,this.pd,this.sa);
						esito.setEsitoInvocazione(true);
						esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
								"Trasformazione-Risposta");
					}else{
						// Se Non e' attivo una gestione dei riscontri, faccio il rollback sulla coda.
						// Altrimenti verra attivato la gestione dei riscontri che riprovera' dopo un tot.
						if(this.gestioneBusteNonRiscontrateAttive==false){
							this.ejbUtils.rollbackMessage("Trasformazione della risposta non riuscita: "+e.getMessage(), esito);
							esito.setEsitoInvocazione(false);
							esito.setStatoInvocazione(EsitoLib.ERRORE_NON_GESTITO,
									"Trasformazione-Risposta");
						}else{
							this.ejbUtils.updateErroreProcessamentoMessage("Trasformazione della risposta non riuscita: "+e.getMessage(), esito);
							esito.setEsitoInvocazione(true);
							esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
									"Trasformazione-Risposta");
						}
					}
					this.openspcoopstate.releaseResource();
					return esito;
				}		
			}
			
			
			
			
			
			
			
			
			
			
			
			
			
			/* -------- OpenSPCoop2Message Update ------------- */
			try {
				this.msgDiag.mediumDebug("Aggiornamento del messaggio");
				// NOTA la versione SOAP capirla da requestMessage, la risposta puo' essere null
				NotifierInputStreamParams nParams = null;
				if(this.invokerNonSupportato==false){
					nParams = this.connectorSender.getNotifierInputStreamParamsResponse();
				}
				this.responseMessage = this.protocolFactory.createProtocolManager().updateOpenSPCoop2MessageResponse(this.responseMessage, 
						this.bustaRichiesta, nParams,
						this.requestMessagePrimaTrasformazione.getTransportRequestContext(),this.transportResponseContext,
						this.protocolFactory.getCachedRegistryReader(this.openspcoopstate.getStatoRichiesta()),
						false);
			} catch (Exception e) {
				
				if(e instanceof ProtocolException) {
					ProtocolException pe = (ProtocolException) e;
					if(pe.isForceTrace()) {
						this.msgDiag.mediumDebug("Tracciamento della richiesta...");
						EsitoElaborazioneMessaggioTracciato esitoTraccia = EsitoElaborazioneMessaggioTracciato.getEsitoElaborazioneConErrore(e.getMessage());
						this.tracciamento.registraRichiesta(this.requestMessageTrasformato,this.securityInfo,this.headerBustaRichiesta,this.bustaRichiesta,esitoTraccia,
								Tracciamento.createLocationString(false, this.location),
								this.idCorrelazioneApplicativa);
					}
				}
				
				
				this.msgDiag.addKeywordErroreProcessamento(e, "Aggiornamento messaggio fallito");
				this.msgDiag.logErroreGenerico(e,"ProtocolManager.updateOpenSPCoop2Message");
				String msgErrore = "ProtocolManager.updateOpenSPCoop2Message error: "+e.getMessage();
				this.log.error(msgErrore,e);
				if(this.functionAsRouter){
					this.ejbUtils.sendAsRispostaBustaErroreProcessamento(this.richiestaDelegata.getIdModuloInAttesa(),this.bustaRichiesta,
							ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
								get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_525_GESTIONE_FUNZIONALITA_PROTOCOLLO),
							this.idCorrelazioneApplicativa,this.idCorrelazioneApplicativaRisposta,this.servizioApplicativoFruitore,e,
							(this.responseMessage!=null ? this.responseMessage.getParseException() : null));
					esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,msgErrore);
					esito.setEsitoInvocazione(true);
				}else{
					if(this.sendRispostaApplicativa){
						OpenSPCoop2Message responseMessageError = 
								this.generatoreErrore.build(this.pddContext,IntegrationFunctionError.INTERNAL_RESPONSE_ERROR,
										ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
											get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_525_GESTIONE_FUNZIONALITA_PROTOCOLLO),e,
												(this.responseMessage!=null ? this.responseMessage.getParseException() : null));
						this.ejbUtils.sendRispostaApplicativaErrore(responseMessageError,this.richiestaDelegata,this.rollbackRichiesta,this.pd,this.sa);
						esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,msgErrore);
						esito.setEsitoInvocazione(true);
					}else{
						// Se Non e' attivo una gestione dei riscontri, faccio il rollback sulla coda.
						// Altrimenti verra attivato la gestione dei riscontri che riprovera' dopo un tot.
						if(this.gestioneBusteNonRiscontrateAttive==false){
							this.ejbUtils.rollbackMessage(msgErrore, esito);
							esito.setEsitoInvocazione(false);
							esito.setStatoInvocazioneErroreNonGestito(e);
						}else{
							this.ejbUtils.updateErroreProcessamentoMessage(msgErrore, esito);
							esito.setEsitoInvocazione(true);
							esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,msgErrore);
						}
					}
				}
				this.openspcoopstate.releaseResource();
				return esito;
			}
			
			
			
			
			
			
			
			
			
			
			
			
			Utilities.printFreeMemory("InoltroBuste - InResponseHandler...");
			
			
			
			
			/* ------------------- InResponseHandler -----------------------*/
			InResponseContext inResponseContext = null;
			if(this.invokerNonSupportato==false){
				try{
					inResponseContext = new InResponseContext(this.log, this.protocolFactory,this.openspcoopstate.getStatoRisposta());
					
					// Informazioni sul messaggio di riposta
					if(this.responseMessage!=null){
						inResponseContext.setMessaggio(this.responseMessage);					
					}
					
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
						if((this.functionAsRouter || this.sendRispostaApplicativa)) {
							erroreIntegrazione = he.convertToErroreIntegrazione();
							integrationFunctionError = he.getIntegrationFunctionError();
						}
					}else{
						this.msgDiag.logErroreGenerico(e, "InResponseHandler");
						msgErrore = "InResponseHandler error: "+msgErrore;
					}
					if(this.functionAsRouter){
						if(erroreIntegrazione==null){
							erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
									get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_544_HANDLER_IN_RESPONSE);
						}
						this.ejbUtils.sendAsRispostaBustaErroreProcessamento(this.richiestaDelegata.getIdModuloInAttesa(),this.bustaRichiesta,
								erroreIntegrazione,
								this.idCorrelazioneApplicativa,this.idCorrelazioneApplicativaRisposta,this.servizioApplicativoFruitore,e,
								(this.responseMessage!=null ? this.responseMessage.getParseException() : null));
						esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,msgErrore);
						esito.setEsitoInvocazione(true);
					}else{
						if(this.sendRispostaApplicativa){
							if(erroreIntegrazione==null){
								erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
										get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_544_HANDLER_IN_RESPONSE);
							}
							if(integrationFunctionError==null) {
								integrationFunctionError = IntegrationFunctionError.INTERNAL_RESPONSE_ERROR;
							}
							OpenSPCoop2Message responseMessageError = 
									this.generatoreErrore.build(this.pddContext,integrationFunctionError,
											erroreIntegrazione,e,
												(this.responseMessage!=null ? this.responseMessage.getParseException() : null));
							if(e instanceof HandlerException){
								HandlerException he = (HandlerException) e;
								he.customized(responseMessageError);
							}
							this.ejbUtils.sendRispostaApplicativaErrore(responseMessageError,this.richiestaDelegata,this.rollbackRichiesta,this.pd,this.sa);
							esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,msgErrore);
							esito.setEsitoInvocazione(true);
						}else{
							// Se Non e' attivo una gestione dei riscontri, faccio il rollback sulla coda.
							// Altrimenti verra attivato la gestione dei riscontri che riprovera' dopo un tot.
							if(this.gestioneBusteNonRiscontrateAttive==false){
								this.ejbUtils.rollbackMessage(msgErrore, esito);
								esito.setEsitoInvocazione(false);
								esito.setStatoInvocazioneErroreNonGestito(e);
							}else{
								this.ejbUtils.updateErroreProcessamentoMessage(msgErrore, esito);
								esito.setEsitoInvocazione(true);
								esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,msgErrore);
							}
						}
					}
					this.openspcoopstate.releaseResource();
					return esito;
				}
			}
			
			
			
			
			
			Utilities.printFreeMemory("InoltroBuste - Messaggio di Risposta + Dump...");
			
			
			
			
			
			// --------------------- Messaggio di Risposta + Dump --------------------------
			if(this.invokerNonSupportato==false){
				
				// Leggo informazioni di trasporto
				this.codiceRitornato = inResponseContext.getReturnCode();
				this.motivoErroreConsegna = inResponseContext.getErroreConsegna();
				this.responseMessage = inResponseContext.getMessaggio();
			
				// dump applicativo
				if(!dumpRispostaEffettuato && this.responseMessage!=null ){
					Dump dumpApplicativo = new Dump(this.identitaPdD,InoltroBuste.ID_MODULO,this.idMessageRequest,
							this.soggettoFruitore,this.idServizio,this.tipoPdD,this.msgDiag.getPorta(), this.pddContext,
							this.openspcoopstate.getStatoRichiesta(),this.openspcoopstate.getStatoRisposta(),
							this.dumpConfig);
					dumpApplicativo.dumpRispostaIngresso(this.responseMessage, inResponseContext.getConnettore(), inResponseContext.getResponseHeaders());
				}
				
			}

			
			
			
			
			
			




			/* ------------  Tracciamento Richiesta e Messaggio Diagnostico ------------- */
			if(this.invokerNonSupportato==false){// && errorConsegna==false){

				StringBuilder bfMsgErroreSituazioneAnomale = new StringBuilder();
				EsitoElaborazioneMessaggioTracciato esitoTraccia = gestioneTracciamentoFineConnettore(bfMsgErroreSituazioneAnomale);
				if(esitoTraccia!=null) {
					this.tracciamento.registraRichiesta(this.requestMessageTrasformato,this.securityInfo,this.headerBustaRichiesta,this.bustaRichiesta,esitoTraccia,
							Tracciamento.createLocationString(false, this.location),
							this.idCorrelazioneApplicativa);
				}
				String msgErroreSituazioneAnomale = null;
				if(bfMsgErroreSituazioneAnomale.length()>0) {
					msgErroreSituazioneAnomale = bfMsgErroreSituazioneAnomale.toString();
				}

				// Dopo che ho effettuata la tracciatura ritorno errore se necessario
				if(msgErroreSituazioneAnomale!=null){
					if(this.functionAsRouter){		
						this.ejbUtils.sendAsRispostaBustaErroreProcessamento(this.richiestaDelegata.getIdModuloInAttesa(),this.bustaRichiesta,
								ErroriIntegrazione.ERRORE_559_RICEVUTA_RISPOSTA_CON_ERRORE_TRASPORTO.
									get559_RicevutaRispostaConErroreTrasporto(msgErroreSituazioneAnomale),
								this.idCorrelazioneApplicativa,this.idCorrelazioneApplicativaRisposta,this.servizioApplicativoFruitore,null,
								(this.responseMessage!=null ? this.responseMessage.getParseException() : null));
						esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,msgErroreSituazioneAnomale);
						esito.setEsitoInvocazione(true);
					}else{
						if(this.sendRispostaApplicativa){
							OpenSPCoop2Message responseMessageError = 
									this.generatoreErrore.build(this.pddContext,IntegrationFunctionError.BAD_RESPONSE,
											ErroriIntegrazione.ERRORE_559_RICEVUTA_RISPOSTA_CON_ERRORE_TRASPORTO.
												get559_RicevutaRispostaConErroreTrasporto(msgErroreSituazioneAnomale),null,
													(this.responseMessage!=null ? this.responseMessage.getParseException() : null));
							this.ejbUtils.sendRispostaApplicativaErrore(responseMessageError,this.richiestaDelegata,this.rollbackRichiesta,this.pd,this.sa);
							esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,msgErroreSituazioneAnomale);
							esito.setEsitoInvocazione(true);
						}else{
							// Se Non e' attivo una gestione dei riscontri, faccio il rollback sulla coda.
							// Altrimenti verra attivato la gestione dei riscontri che riprovera' dopo un tot.
							if(this.gestioneBusteNonRiscontrateAttive==false){
								this.ejbUtils.rollbackMessage(msgErroreSituazioneAnomale, esito);
								esito.setEsitoInvocazione(false);
								esito.setStatoInvocazione(EsitoLib.ERRORE_NON_GESTITO,msgErroreSituazioneAnomale);
							}else{
								this.ejbUtils.updateErroreProcessamentoMessage(msgErroreSituazioneAnomale, esito);
								esito.setEsitoInvocazione(true);
								esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,msgErroreSituazioneAnomale);
							}
						}
					}
					this.openspcoopstate.releaseResource();
					return esito;
				}
			}

			
			
			
			
			
			
			
			
		
			



			Utilities.printFreeMemory("InoltroBuste - Analisi della risposta (validazione sintattica/semantica)...");





			/* ------------- Analisi di una risposta ritornata ------------*/
			boolean presenzaRispostaProtocollo = false;
			SecurityInfo securityInfoResponse = null;
			
			boolean sbustamentoInformazioniProtocolloRispostaDopoCorrelazione = false;
			org.openspcoop2.protocol.engine.builder.Sbustamento sbustatore = null;
			Busta bustaRisposta = null;
			boolean sbustamentoManifestRisposta = false;
			
			if(this.responseMessage != null ){
				this.msgDiag.mediumDebug("Analisi della risposta (validazione sintattica)...");

				// ValidazioneSintattica
				ProprietaValidazione property = new ProprietaValidazione();
				property.setValidazioneConSchema(this.configurazionePdDManager.isLivelloValidazioneRigido(this.implementazionePdDDestinatario));
				property.setValidazioneProfiloCollaborazione(this.configurazionePdDManager.isValidazioneProfiloCollaborazione(this.implementazionePdDDestinatario));
				property.setValidazioneManifestAttachments(
						this.configurazionePdDManager.isValidazioneManifestAttachments(this.implementazionePdDDestinatario) &&
						this.gestioneManifest);
				//validator = new ValidatoreSPCoop(responseMessage,property,this.openspcoopstate.getStatoRichiesta(),readQualifiedAttribute);
				if(this.openspcoopstate.getStatoRisposta() instanceof StatelessMessage){
					((StatelessMessage) this.openspcoopstate.getStatoRisposta()).setBustaCorrelata(this.bustaRichiesta);
				}
				if(this.openspcoopstate!=null) {
					property.setRuntimeState(this.openspcoopstate.getStatoRisposta());
					if(this.propertiesReader.isTransazioniUsePddRuntimeDatasource()) {
						property.setTracceState(this.openspcoopstate.getStatoRisposta());
					}
				}
				this.validatore = new Validatore(this.responseMessage, this.pddContext,property,this.openspcoopstate.getStatoRisposta(),this.readQualifiedAttribute, this.protocolFactory);
						
				this.msgDiag.logPersonalizzato("validazioneSintattica");
				presenzaRispostaProtocollo  = this.validatore.validazioneSintattica(this.bustaRichiesta, Boolean.FALSE);
				if(presenzaRispostaProtocollo){
					this.headerProtocolloRisposta = this.validatore.getHeaderProtocollo();
					this.msgDiag.addKeywords(this.validatore.getBusta(), false);
					if(this.validatore.getErroreProcessamento_internalMessage()!=null) {
						this.msgDiag.logErroreGenerico(this.validatore.getErroreProcessamento_internalMessage(), "Validazione Risposta");
					}
				}else{
					if(this.validatore.getErrore()!=null){
						this.log.debug("Messaggio non riconosciuto come busta ("+this.traduttore.toString(this.validatore.getErrore().getCodiceErrore())
								+"): "+this.validatore.getErrore().getDescrizione(this.protocolFactory));
					}
				}
				
				if(presenzaRispostaProtocollo){
					try{
						// ulteriore controllo per evitare che il protocollo trasparente generi una busta di risposta per il profilo oneway
						presenzaRispostaProtocollo = this.protocolFactory.createValidazioneSintattica(this.openspcoopstate.getStatoRisposta()).
								verifyProtocolPresence(this.tipoPdD,this.bustaRichiesta.getProfiloDiCollaborazione(),RuoloMessaggio.RISPOSTA,this.responseMessage);
					} catch (Exception e){
						this.log.debug("Messaggio non riconosciuto come busta: "+e.getMessage());
						presenzaRispostaProtocollo = false;
					} 
				}
								
				if(this.functionAsRouter==false && presenzaRispostaProtocollo ){		

					
					
					/* *** Init MTOM Processor / SecurityContext *** */ 
					this.mtomProcessor = null;
					this.messageSecurityConfig = null;
					this.msgDiag.mediumDebug("init MTOM Processor / SecurityContext ...");
					ErroreIntegrazione erroreIntegrazioneConfig = null;
					Exception configException = null;
					String oggetto = null;
						
					MTOMProcessorConfig mtomConfig = null;
					try{
						mtomConfig=this.configurazionePdDManager.getPD_MTOMProcessorForReceiver(this.pd);
					}catch(Exception e){
						oggetto = "LetturaConfigurazioneMTOMProcessorRoleReceiver";
						erroreIntegrazioneConfig = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
								get5XX_ErroreProcessamento(e,CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE);
						configException = e;
					}					
					try{
						this.messageSecurityConfig=this.configurazionePdDManager.getPD_MessageSecurityForReceiver(this.pd);
					}catch(Exception e){
						oggetto = "LetturaConfigurazioneMessageSecurityRoleReceiver";
						erroreIntegrazioneConfig = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
								get5XX_ErroreProcessamento(e,CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE);
						configException = e;
					}
						
					if(erroreIntegrazioneConfig!=null){
						this.msgDiag.logErroreGenerico(configException, oggetto);
					
						if(this.sendRispostaApplicativa){
							OpenSPCoop2Message responseMessageError = 
									this.generatoreErrore.build(this.pddContext,IntegrationFunctionError.INTERNAL_RESPONSE_ERROR,
											erroreIntegrazioneConfig,configException,
												(this.responseMessage!=null ? this.responseMessage.getParseException() : null));
							this.ejbUtils.sendRispostaApplicativaErrore(responseMessageError,this.richiestaDelegata,this.rollbackRichiesta,this.pd,this.sa);
							esito.setEsitoInvocazione(true);
							esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,configException.getMessage());
						} else{
							// Se Non e' attivo una gestione dei riscontri, faccio il rollback sulla coda.
							// Altrimenti verra attivato la gestione dei riscontri che riprovera' dopo un tot.
							if(this.gestioneBusteNonRiscontrateAttive==false){
								this.ejbUtils.rollbackMessage(oggetto+" non riuscita: "+configException.getMessage(), esito);
								esito.setStatoInvocazioneErroreNonGestito(configException);
								esito.setEsitoInvocazione(false);
							}else{
								this.ejbUtils.updateErroreProcessamentoMessage(oggetto+" non riuscita: "+configException.getMessage(), esito);
								esito.setEsitoInvocazione(true);
								esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,configException.getMessage());
							}
						}
						this.openspcoopstate.releaseResource();
						return esito;
					}
					else{
						this.mtomProcessor = new MTOMProcessor(mtomConfig, this.messageSecurityConfig, 
								this.tipoPdD, this.msgDiag, this.log, this.pddContext);
					}
					
					
					
					
					/* *** MTOM Processor BeforeSecurity  *** */
					if(this.mtomProcessor!=null){
						try{
							this.mtomProcessor.mtomBeforeSecurity(this.responseMessage, RuoloMessaggio.RISPOSTA);
						}catch(Exception e){
							// L'errore viene registrato dentro il metodo this.mtomProcessor.mtomBeforeSecurity
							// this.msgDiag.logErroreGenerico(e,"MTOMProcessor(BeforeSec-"+this.mtomProcessor.getMTOMProcessorType()+")");
						
							ErroreIntegrazione erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
									get5XX_ErroreProcessamento(e,CodiceErroreIntegrazione.CODICE_557_MTOM_PROCESSOR_ERROR);
							if(this.sendRispostaApplicativa){
								OpenSPCoop2Message responseMessageError = this.generatoreErrore.build(this.pddContext,IntegrationFunctionError.ATTACHMENTS_PROCESSING_RESPONSE_FAILED,
										erroreIntegrazione,e,
											(this.responseMessage!=null ? this.responseMessage.getParseException() : null));
								this.ejbUtils.sendRispostaApplicativaErrore(responseMessageError,this.richiestaDelegata,this.rollbackRichiesta,this.pd,this.sa);
								esito.setEsitoInvocazione(true);
								esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
										"MTOMProcessor(BeforeSec-"+this.mtomProcessor.getMTOMProcessorType()+")");
							}else{
								// Se Non e' attivo una gestione dei riscontri, faccio il rollback sulla coda.
								// Altrimenti verra attivato la gestione dei riscontri che riprovera' dopo un tot.
								if(this.gestioneBusteNonRiscontrateAttive==false){
									this.ejbUtils.rollbackMessage("MTOMProcessor(BeforeSec-"+this.mtomProcessor.getMTOMProcessorType()+") non riuscita: "+e.getMessage(), esito);
									esito.setStatoInvocazioneErroreNonGestito(e);
									esito.setEsitoInvocazione(false);
								}else{
									this.ejbUtils.updateErroreProcessamentoMessage("MTOMProcessor(BeforeSec-"+this.mtomProcessor.getMTOMProcessorType()+") non riuscita: "+e.getMessage(), esito);
									esito.setEsitoInvocazione(true);
									esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
											"MTOMProcessor(BeforeSec-"+this.mtomProcessor.getMTOMProcessorType()+")");
								}
							}
							this.openspcoopstate.releaseResource();
							return esito;
						}
					}
					
					
										

					
										
					/* *** Init context sicurezza *** */
					if(this.messageSecurityConfig!=null && this.messageSecurityConfig.getFlowParameters()!=null
							&& this.messageSecurityConfig.getFlowParameters().size()>0){
						
						try{							
							this.msgDiag.mediumDebug("Inizializzazione contesto di Message Security della risposta ...");
														
							if(this.messageSecurityContext==null){
								// se non vi era la richiesta di MessageSecurity
								MessageSecurityContextParameters contextParameters = new MessageSecurityContextParameters();
								contextParameters.setUseActorDefaultIfNotDefined(this.propertiesReader.isGenerazioneActorDefault(this.implementazionePdDDestinatario));
								contextParameters.setActorDefault(this.propertiesReader.getActorDefault(this.implementazionePdDDestinatario));
								contextParameters.setLog(this.log);
								contextParameters.setFunctionAsClient(SecurityConstants.SECURITY_SERVER);
								contextParameters.setPrefixWsuId(this.propertiesReader.getPrefixWsuId());
								contextParameters.setRemoveAllWsuIdRef(this.propertiesReader.isRemoveAllWsuIdRef());
								contextParameters.setIdFruitore(this.soggettoFruitore);
								contextParameters.setIdServizio(this.idServizio);
								contextParameters.setPddFruitore(this.registroServiziManager.getIdPortaDominio(this.soggettoFruitore, null));
								contextParameters.setPddErogatore(this.registroServiziManager.getIdPortaDominio(this.idServizio.getSoggettoErogatore(), null));
								this.messageSecurityContext = new MessageSecurityFactory().getMessageSecurityContext(contextParameters);
							}
							this.messageSecurityContext.setIncomingProperties(this.messageSecurityConfig.getFlowParameters());  
							this.messageSecurityContext.setFunctionAsClient(SecurityConstants.SECURITY_SERVER);
							
							String tipoSicurezza = SecurityConstants.convertActionToString(this.messageSecurityContext.getIncomingProperties());
							this.msgDiag.addKeyword(CostantiPdD.KEY_TIPO_SICUREZZA_MESSAGGIO_RISPOSTA, tipoSicurezza);
							this.pddContext.addObject(CostantiPdD.KEY_TIPO_SICUREZZA_MESSAGGIO_RISPOSTA, tipoSicurezza);
							
							this.msgDiag.mediumDebug("Inizializzazione contesto di Message Security della richiesta completata con successo");
							
							if(this.responseMessage!=null && org.openspcoop2.security.message.engine.WSSUtilities.isNormalizeToSaajImpl(this.messageSecurityContext)){
								this.msgDiag.mediumDebug("Normalize Response to saajImpl");
								//System.out.println("InoltroBusteEgov.response.normalize");
								this.responseMessage = this.responseMessage.normalizeToSaajImpl();
								
								this.validatore.updateMsg(this.responseMessage);
							}
							
						}catch(Exception e){
							this.msgDiag.logErroreGenerico(e,"InizializzazioneContestoSicurezzaRisposta");
							if(this.sendRispostaApplicativa){
								OpenSPCoop2Message responseMessageError = 
										this.generatoreErrore.build(this.pddContext,IntegrationFunctionError.INTERNAL_RESPONSE_ERROR,
												ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
													get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE),e,
														(this.responseMessage!=null ? this.responseMessage.getParseException() : null));
								this.ejbUtils.sendRispostaApplicativaErrore(responseMessageError,this.richiestaDelegata,this.rollbackRichiesta,this.pd,this.sa);
								esito.setEsitoInvocazione(true);
								esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
										"InizializzazioneContestoSicurezzaRisposta");
							} else{
								// Se Non e' attivo una gestione dei riscontri, faccio il rollback sulla coda.
								// Altrimenti verra attivato la gestione dei riscontri che riprovera' dopo un tot.
								if(this.gestioneBusteNonRiscontrateAttive==false){
									this.ejbUtils.rollbackMessage("Errore durante la lettura delle proprieta' di MessageSecurity per la risposta: "+e.getMessage(), esito);
									esito.setStatoInvocazioneErroreNonGestito(e);
									esito.setEsitoInvocazione(false);
								}else{
									this.ejbUtils.updateErroreProcessamentoMessage("Errore durante la lettura delle proprieta' di MessageSecurity per la risposta: "+e.getMessage(), esito);
									esito.setEsitoInvocazione(true);
									esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
											"InizializzazioneContestoSicurezzaRisposta");
								}
							}
							this.openspcoopstate.releaseResource();
							return esito;
						}
					}
					else{
						this.msgDiag.logPersonalizzato("messageSecurity.processamentoRispostaDisabilitato");
					}
					
					
					
					/* *** ReadSecurityInformation *** */
					try{
						IDigestReader digestReader = null;
						if(this.messageSecurityContext != null) {
							digestReader = this.messageSecurityContext.getDigestReader(this.responseMessage!=null ? this.responseMessage.getFactory() : OpenSPCoop2MessageFactory.getDefaultMessageFactory());
						}
						if(digestReader!=null){
							this.msgDiag.mediumDebug("Lettura informazioni sulla Sicurezza dal Messaggio di risposta ...");
							securityInfoResponse = this.validazioneSemantica.readSecurityInformation(digestReader,this.responseMessage);
							this.msgDiag.mediumDebug("Lettura informazioni sulla Sicurezza dal Messaggio di risposta completata con successo");
						}
					}catch(Exception e){
						this.msgDiag.logErroreGenerico(e,"ErroreLetturaInformazioniSicurezza");
						if(this.sendRispostaApplicativa){
							OpenSPCoop2Message responseMessageError = 
									this.generatoreErrore.build(this.pddContext,IntegrationFunctionError.MESSAGE_SECURITY_RESPONSE_FAILED,
											ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
												get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_549_SECURITY_INFO_READER_ERROR),e,
													(this.responseMessage!=null ? this.responseMessage.getParseException() : null));
							this.ejbUtils.sendRispostaApplicativaErrore(responseMessageError,this.richiestaDelegata,this.rollbackRichiesta,this.pd,this.sa);
							esito.setEsitoInvocazione(true);
							esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
									"ErroreLetturaInformazioniSicurezza");
						} else{
							// Se Non e' attivo una gestione dei riscontri, faccio il rollback sulla coda.
							// Altrimenti verra attivato la gestione dei riscontri che riprovera' dopo un tot.
							if(this.gestioneBusteNonRiscontrateAttive==false){
								this.ejbUtils.rollbackMessage("Errore durante la lettura dele informazioni di sicurezza per la risposta: "+e.getMessage(), esito);
								esito.setStatoInvocazioneErroreNonGestito(e);
								esito.setEsitoInvocazione(false);
							}else{
								this.ejbUtils.updateErroreProcessamentoMessage("Errore durante la lettura dele informazioni di sicurezza per la risposta: "+e.getMessage(), esito);
								esito.setEsitoInvocazione(true);
								esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
										"ErroreLetturaInformazioniSicurezza");
							}
						}
						this.openspcoopstate.releaseResource();
						return esito;
					}
					
					
					
					
					/* *** ValidazioneSemantica (e applicazione sicurezza del messaggio) *** */
					
					this.msgDiag.logPersonalizzato("validazioneSemantica.beforeSecurity");
					presenzaRispostaProtocollo = this.validatore.validazioneSemantica_beforeMessageSecurity(this.requestInfo.getProtocolServiceBinding(),true, this.profiloGestione);
					
					if(this.validatore.isRilevatiErroriDuranteValidazioneSemantica()==false){
						
						if(this.messageSecurityContext!= null && this.messageSecurityContext.getIncomingProperties() != null && this.messageSecurityContext.getIncomingProperties().size() > 0){
						
							this.msgDiag.logPersonalizzato("messageSecurity.processamentoRispostaInCorso");
							
							StringBuilder bfErroreSecurity = new StringBuilder();
							presenzaRispostaProtocollo = this.validatore.validazioneSemantica_messageSecurity_process(this.messageSecurityContext, bfErroreSecurity,
									this.transactionNullable!=null ? this.transactionNullable.getTempiElaborazione() : null,
											false);
							
							if(bfErroreSecurity.length()>0){
								this.msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO , bfErroreSecurity.toString() );
								this.msgDiag.logPersonalizzato("messageSecurity.processamentoRispostaInErrore");
							}
							else{
								this.msgDiag.logPersonalizzato("messageSecurity.processamentoRispostaEffettuato");
							}
						}
					
						if(this.validatore.isRilevatiErroriDuranteValidazioneSemantica()==false){
							
							this.msgDiag.logPersonalizzato("validazioneSemantica.afterSecurity");
							presenzaRispostaProtocollo = this.validatore.validazioneSemantica_afterMessageSecurity(this.proprietaManifestAttachments, this.validazioneIDBustaCompleta);
					
						}
					}
					
					
					
					/* *** MTOM Processor AfterSecurity  *** */
					if(this.mtomProcessor!=null){
						try{
							this.mtomProcessor.mtomAfterSecurity(this.responseMessage, RuoloMessaggio.RISPOSTA);
						}catch(Exception e){
							// L'errore viene registrato dentro il metodo this.mtomProcessor.mtomAfterSecurity
							// this.msgDiag.logErroreGenerico(e,"MTOMProcessor(AfterSec-"+this.mtomProcessor.getMTOMProcessorType()+")");
						
							ErroreIntegrazione erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
									get5XX_ErroreProcessamento(e,CodiceErroreIntegrazione.CODICE_557_MTOM_PROCESSOR_ERROR);
							if(this.sendRispostaApplicativa){
								OpenSPCoop2Message responseMessageError = this.generatoreErrore.build(this.pddContext,IntegrationFunctionError.ATTACHMENTS_PROCESSING_RESPONSE_FAILED,
										erroreIntegrazione,e,
											(this.responseMessage!=null ? this.responseMessage.getParseException() : null));
								this.ejbUtils.sendRispostaApplicativaErrore(responseMessageError,this.richiestaDelegata,this.rollbackRichiesta,this.pd,this.sa);
								esito.setEsitoInvocazione(true);
								esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
										"MTOMProcessor(AfterSec-"+this.mtomProcessor.getMTOMProcessorType()+")");
							}else{
								// Se Non e' attivo una gestione dei riscontri, faccio il rollback sulla coda.
								// Altrimenti verra attivato la gestione dei riscontri che riprovera' dopo un tot.
								if(this.gestioneBusteNonRiscontrateAttive==false){
									this.ejbUtils.rollbackMessage("MTOMProcessor(AfterSec-"+this.mtomProcessor.getMTOMProcessorType()+") non riuscita: "+e.getMessage(), esito);
									esito.setStatoInvocazioneErroreNonGestito(e);
									esito.setEsitoInvocazione(false);
								}else{
									this.ejbUtils.updateErroreProcessamentoMessage("MTOMProcessor(AfterSec-"+this.mtomProcessor.getMTOMProcessorType()+") non riuscita: "+e.getMessage(), esito);
									esito.setEsitoInvocazione(true);
									esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
											"MTOMProcessor(AfterSec-"+this.mtomProcessor.getMTOMProcessorType()+")");
								}
							}
							this.openspcoopstate.releaseResource();
							return esito;
						}
					}
					
				}
					
				if(presenzaRispostaProtocollo ){		
					
					// Qua dentro ci entro sia se sono router, che se è ancora rilevata la presenza di un protocollo dopo la validazione semantica
					
					// Estrazione header busta
					this.msgDiag.mediumDebug("Sbustamento della risposta...");
					bustaRisposta = this.validatore.getBusta();
					try{
						boolean isMessaggioErroreProtocollo = this.validatore.isErroreProtocollo();
						
						boolean gestioneManifestRisposta = false;
						if(this.functionAsRouter==false && !isMessaggioErroreProtocollo){
							gestioneManifestRisposta = this.configurazionePdDManager.isGestioneManifestAttachments(this.pd, this.protocolFactory);
							
							List<Eccezione> erroriValidazione = this.validatore.getEccezioniValidazione();
							
							sbustatore = 
									new org.openspcoop2.protocol.engine.builder.Sbustamento(this.protocolFactory, this.openspcoopstate.getStatoRisposta());
							
							// GestioneManifest solo se ho ricevuto una busta correttamente formata nel manifest
							sbustamentoManifestRisposta = gestioneManifestRisposta;
							for(int k = 0; k < erroriValidazione.size() ; k++){
								Eccezione er = erroriValidazione.get(k);
								if(CodiceErroreCooperazione.isEccezioneAllegati(er.getCodiceEccezione())){
									sbustamentoManifestRisposta = false;
								}
							}	
							this.msgDiag.highDebug("Tipo Messaggio Risposta prima dello sbustamento ["+FaseSbustamento.POST_VALIDAZIONE_SEMANTICA_RISPOSTA
									+"] ["+this.responseMessage.getClass().getName()+"]");
							ProtocolMessage protocolMessage = sbustatore.sbustamento(this.responseMessage, this.pddContext,
									bustaRisposta,
									RuoloMessaggio.RISPOSTA,sbustamentoManifestRisposta,this.proprietaManifestAttachments,
									FaseSbustamento.POST_VALIDAZIONE_SEMANTICA_RISPOSTA, this.requestInfo);
							if(protocolMessage!=null) {
								if(!protocolMessage.isUseBustaRawContentReadByValidation()) {
									this.headerProtocolloRisposta = protocolMessage.getBustaRawContent();
								}
								this.responseMessage = protocolMessage.getMessage(); // updated
							}
							this.msgDiag.highDebug("Tipo Messaggio Risposta dopo lo sbustamento ["+FaseSbustamento.POST_VALIDAZIONE_SEMANTICA_RISPOSTA
									+"] ["+this.responseMessage.getClass().getName()+"]");		
							
		
							// Spostato dopo correlazione applicativa
							if(this.sbustamentoInformazioniProtocolloRisposta){
								// effettuo lo stesso sbustamento invocandolo con la nuova fase
								// Questa invocazione andrebbe implementata su ricezionecontenutiApplicativi teoricamente
								sbustamentoInformazioniProtocolloRispostaDopoCorrelazione = true;
//								this.msgDiag.highDebug("Tipo Messaggio Risposta prima dello sbustamento ["+FaseSbustamento.PRE_CONSEGNA_RISPOSTA
//										+"] ["+responseMessage.getClass().getName()+"]");
//								protocolMessage = sbustatore.sbustamento(responseMessage,bustaRisposta,
//										RuoloMessaggio.RISPOSTA,sbustamentoManifestRisposta,proprietaManifestAttachments,
//										FaseSbustamento.PRE_CONSEGNA_RISPOSTA, requestInfo);
//								headerProtocolloRisposta = protocolMessage.getBustaRawContent();
//								responseMessage = protocolMessage.getMessage(); // updated
//								this.msgDiag.highDebug("Tipo Messaggio Risposta dopo lo sbustamento ["+FaseSbustamento.PRE_CONSEGNA_RISPOSTA
//										+"] ["+responseMessage.getClass().getName()+"]");	
							}
							
						}else{
							this.headerProtocolloRisposta = this.validatore.getHeaderProtocollo();
						}
					}catch(Exception e){
						if(this.functionAsRouter==false){
							this.msgDiag.logErroreGenerico(e,"sbustatore.sbustamento("+bustaRisposta.getID()+")");
						}else{
							this.msgDiag.logErroreGenerico(e,"validator.getHeader("+bustaRisposta.getID()+")");
						}
												
						EsitoElaborazioneMessaggioTracciato esitoTraccia = EsitoElaborazioneMessaggioTracciato.getEsitoElaborazioneConErrore("Errore durante lo sbustamento della risposta: "+e.getMessage());
						this.tracciamento.registraRisposta(this.responseMessage,securityInfoResponse,this.headerProtocolloRisposta,bustaRisposta,esitoTraccia,
								Tracciamento.createLocationString(true, this.location),
								this.idCorrelazioneApplicativa,this.idCorrelazioneApplicativaRisposta); // non ancora registrata
						
						if(this.functionAsRouter){
							this.ejbUtils.sendAsRispostaBustaErroreProcessamento(this.richiestaDelegata.getIdModuloInAttesa(),
									this.bustaRichiesta,
									ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
										get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_527_GESTIONE_SBUSTAMENTO),
									this.idCorrelazioneApplicativa,this.idCorrelazioneApplicativaRisposta,this.servizioApplicativoFruitore,e,
									(this.responseMessage!=null ? this.responseMessage.getParseException() : null));
							esito.setEsitoInvocazione(true);
							esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
									"Sbustamento non riuscito");
						}else{
													
							ErroreIntegrazione erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
									get5XX_ErroreProcessamento(e,CodiceErroreIntegrazione.CODICE_527_GESTIONE_SBUSTAMENTO);
							if(this.sendRispostaApplicativa){
								OpenSPCoop2Message responseMessageError = this.generatoreErrore.build(this.pddContext,IntegrationFunctionError.INTERNAL_RESPONSE_ERROR,
										erroreIntegrazione,e,
											(this.responseMessage!=null ? this.responseMessage.getParseException() : null));
								this.ejbUtils.sendRispostaApplicativaErrore(responseMessageError,this.richiestaDelegata,this.rollbackRichiesta,this.pd,this.sa);
								esito.setEsitoInvocazione(true);
								esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,"Sbustamento non riuscito");
							}else{
								// Se Non e' attivo una gestione dei riscontri, faccio il rollback sulla coda.
								// Altrimenti verra attivato la gestione dei riscontri che riprovera' dopo un tot.
								if(this.gestioneBusteNonRiscontrateAttive==false){
									this.ejbUtils.rollbackMessage("Sbustamento non riuscito", esito);
									esito.setStatoInvocazioneErroreNonGestito(e);
									esito.setEsitoInvocazione(false);
								}else{
									this.ejbUtils.updateErroreProcessamentoMessage("Sbustamento non riuscito", esito);
									esito.setEsitoInvocazione(true);
									esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,"Sbustamento non riuscito");
								}
							}
							
						}
						this.openspcoopstate.releaseResource();
						return esito;

					}
				}
			}





			Utilities.printFreeMemory("InoltroBuste - Gestione errore consegna della risposta...");
			boolean faultLogged = false;


			/* ------------------------- Gestione Errori Consegna ---------------------------- */
			this.msgDiag.mediumDebug("Gestione errore consegna della risposta...");
			// Invoker Non Supportato
			if(this.invokerNonSupportato == true){
				String connettoreNonSupportato = "Connettore non supportato [tipo:"+this.tipoConnector+" class:"+this.connectorClass+"]";
				if(this.functionAsRouter){
					this.ejbUtils.sendAsRispostaBustaErroreProcessamento(this.richiestaDelegata.getIdModuloInAttesa(),this.bustaRichiesta,
							ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
								get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_515_CONNETTORE_NON_REGISTRATO),
							this.idCorrelazioneApplicativa,this.idCorrelazioneApplicativaRisposta,this.servizioApplicativoFruitore,this.eInvokerNonSupportato,
							(this.responseMessage!=null ? this.responseMessage.getParseException() : null));
					esito.setEsitoInvocazione(true);
					esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,connettoreNonSupportato);
				}else{
					if(this.sendRispostaApplicativa){
						OpenSPCoop2Message responseMessageError = 
								this.generatoreErrore.build(this.pddContext,IntegrationFunctionError.INTERNAL_REQUEST_ERROR,
										ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
											get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_515_CONNETTORE_NON_REGISTRATO),this.eInvokerNonSupportato,
												(this.responseMessage!=null ? this.responseMessage.getParseException() : null));
						this.ejbUtils.sendRispostaApplicativaErrore(responseMessageError,this.richiestaDelegata,this.rollbackRichiesta,this.pd,this.sa);
						esito.setEsitoInvocazione(true);
						esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,connettoreNonSupportato);
					} else{
						// Se Non e' attivo una gestione dei riscontri, faccio il rollback sulla coda.
						// Altrimenti verra attivato la gestione dei riscontri che riprovera' dopo un tot.
						if(this.gestioneBusteNonRiscontrateAttive==false){
							this.ejbUtils.rollbackMessage(connettoreNonSupportato, esito);
							esito.setEsitoInvocazione(false);
							esito.setStatoInvocazione(EsitoLib.ERRORE_NON_GESTITO,connettoreNonSupportato);
						}else{
							this.ejbUtils.updateErroreProcessamentoMessage("Connettore non supportato [tipo:"+this.tipoConnector+" class:"+this.connectorClass+"].", esito);
							esito.setEsitoInvocazione(true);
							esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,connettoreNonSupportato);
						}
					}
				}
				this.openspcoopstate.releaseResource();
				return esito;
			}

			// Errori avvenuti durante la consegna (senza che cmq sia pervenuta una busta)
			else if(this.errorConsegna && presenzaRispostaProtocollo==false){
				String erroreConnettore = "Consegna ["+this.tipoConnector+"] con errore: "+this.motivoErroreConsegna;
				if(this.functionAsRouter){
					if(this.responseMessage==null){
						//	Genero una risposta di errore, poiche' non presente
						IntegrationFunctionError integrationFunctionError = getIntegrationFunctionErroreConnectionError();
						this.ejbUtils.setIntegrationFunctionErrorPortaApplicativa(integrationFunctionError);
						this.ejbUtils.sendAsRispostaBustaErroreProcessamento(this.richiestaDelegata.getIdModuloInAttesa(),this.bustaRichiesta,
								ErroriIntegrazione.ERRORE_516_CONNETTORE_UTILIZZO_CON_ERRORE.
									get516_PortaDiDominioNonDisponibile(this.bustaRichiesta.getTipoDestinatario()+"-"+this.bustaRichiesta.getDestinatario()),
								this.idCorrelazioneApplicativa,this.idCorrelazioneApplicativaRisposta,this.servizioApplicativoFruitore,this.eccezioneProcessamentoConnettore,
								(this.responseMessage!=null ? this.responseMessage.getParseException() : null));
						this.openspcoopstate.releaseResource();
						esito.setEsitoInvocazione(true);
						esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,erroreConnettore);
						return esito;
					}
				}else{
					//	Effettuo log dell'eventuale fault (registro anche i fault spcoop, potrebbero contenere dei details aggiunti da una PdD.)
					if( this.soapFault!=null && faultLogged==false ){
						this.msgDiag.addKeyword(CostantiPdD.KEY_SOAP_FAULT, SoapUtils.safe_toString(this.faultMessageFactory, this.soapFault, this.log));
						this.msgDiag.logPersonalizzato("ricezioneSoapFault");
						faultLogged = true;
					}
					else if( this.restProblem!=null && faultLogged==false ){
						this.msgDiag.addKeyword(CostantiPdD.KEY_REST_PROBLEM, this.restProblem.getRaw());
						this.msgDiag.logPersonalizzato("ricezioneRestProblem");
						faultLogged = true;
					}
					if(this.sendRispostaApplicativa){
						if(this.responseMessage==null){
							
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
							
							if(this.requestMessagePrimaTrasformazione.getParseException()!=null || 
									requestReadTimeout!=null || 
									requestLimitExceeded!=null){
								this.pddContext.addObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RICHIESTA_NON_RICONOSCIUTO, true);
								
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
									parseException = this.requestMessagePrimaTrasformazione.getParseException();
									tParsing = parseException.getParseException();
									errorMsg = tParsing.getMessage();
								}
								
								IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.UNPROCESSABLE_REQUEST_CONTENT;
								if(requestReadTimeout!=null) {
									integrationFunctionError = IntegrationFunctionError.REQUEST_TIMED_OUT;
								}
								else if(requestLimitExceeded!=null) {
									integrationFunctionError = IntegrationFunctionError.REQUEST_SIZE_EXCEEDED;
								}
								else if(this.requestMessagePrimaTrasformazione.getParseException().getSourceException()!=null &&
										TimeoutIOException.isTimeoutIOException(this.requestMessagePrimaTrasformazione.getParseException().getSourceException())) {
									integrationFunctionError = IntegrationFunctionError.REQUEST_TIMED_OUT;
								}
								else if(this.requestMessagePrimaTrasformazione.getParseException().getSourceException()!=null &&
										LimitExceededIOException.isLimitExceededIOException(this.requestMessagePrimaTrasformazione.getParseException().getSourceException())) {
									integrationFunctionError = IntegrationFunctionError.REQUEST_SIZE_EXCEEDED;
								}
								
								OpenSPCoop2Message responseMessageError = 
										this.generatoreErrore.build(this.pddContext, integrationFunctionError,
											ErroriIntegrazione.ERRORE_432_PARSING_EXCEPTION_RICHIESTA.getErrore432_MessaggioRichiestaMalformato(tParsing),
											tParsing,
											parseException);
								this.ejbUtils.sendRispostaApplicativaErrore(responseMessageError,this.richiestaDelegata,this.rollbackRichiesta,this.pd,this.sa);
								this.openspcoopstate.releaseResource();
								esito.setEsitoInvocazione(true);	
								esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,errorMsg);
								return esito;
							}
							else if(this.pddContext.containsKey(org.openspcoop2.core.constants.Costanti.CONTENUTO_RISPOSTA_NON_RICONOSCIUTO_PARSE_EXCEPTION)
									//|| responseReadTimeout!=null deve essere gestito dopo per generare un errore di connessione. Viene gestito all'interno del metodo 'getIntegrationFunctionErroreConnectionError' dell'else sottostante
									|| responseLimitExceeded!=null
									){
								
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
								OpenSPCoop2Message responseMessageError = 
										this.generatoreErrore.build(this.pddContext,integrationFunctionError,
											ErroriIntegrazione.ERRORE_440_PARSING_EXCEPTION_RISPOSTA.getErrore440_MessaggioRispostaMalformato(tParsing),
											tParsing,
											parseException);
								this.ejbUtils.sendRispostaApplicativaErrore(responseMessageError,this.richiestaDelegata,this.rollbackRichiesta,this.pd,this.sa);
								this.openspcoopstate.releaseResource();
								esito.setEsitoInvocazione(true);	
								esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,errorMsg);
								return esito;
							}
							else {
								IntegrationFunctionError integrationFunctionError = getIntegrationFunctionErroreConnectionError();
								OpenSPCoop2Message responseMessageError = 
										this.generatoreErrore.build(this.pddContext,integrationFunctionError,
												ErroriIntegrazione.ERRORE_516_CONNETTORE_UTILIZZO_CON_ERRORE.
													get516_PortaDiDominioNonDisponibile(this.bustaRichiesta.getTipoDestinatario()+"-"+this.bustaRichiesta.getDestinatario()),
													this.eccezioneProcessamentoConnettore,
																(this.responseMessage!=null ? this.responseMessage.getParseException() : null));
								
								// Retry-After
								boolean isEnabled = this.propertiesReader.isEnabledServiceUnavailableRetryAfter_pd_connectionFailed();
								Integer retryAfterSeconds = this.propertiesReader.getServiceUnavailableRetryAfterSeconds_pd_connectionFailed();
								Integer retryAfterBackOffSeconds = this.propertiesReader.getServiceUnavailableRetryAfterSeconds_randomBackoff_pd_connectionFailed();
								if(	isEnabled &&
									retryAfterSeconds!=null && retryAfterSeconds>0) {
									int seconds = retryAfterSeconds;
									if(retryAfterBackOffSeconds!=null && retryAfterBackOffSeconds>0) {
										seconds = seconds + new Random().nextInt(retryAfterBackOffSeconds);
									}
									responseMessageError.forceTransportHeader(HttpConstants.RETRY_AFTER, seconds+"");
								}
								
								this.ejbUtils.sendRispostaApplicativaErrore(responseMessageError,this.richiestaDelegata,this.rollbackRichiesta,this.pd,this.sa);
								this.openspcoopstate.releaseResource();
								esito.setEsitoInvocazione(true);	
								esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,erroreConnettore);
								return esito;
							}
						}
						else{
							if(this.responseMessage.getParseException()!=null){
								this.pddContext.addObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RISPOSTA_NON_RICONOSCIUTO, true);
								OpenSPCoop2Message responseMessageError = 
										this.generatoreErrore.build(this.pddContext,IntegrationFunctionError.UNPROCESSABLE_RESPONSE_CONTENT,
											ErroriIntegrazione.ERRORE_440_PARSING_EXCEPTION_RISPOSTA.getErrore440_MessaggioRispostaMalformato(this.responseMessage.getParseException().getParseException()),
											this.responseMessage.getParseException().getParseException(),
											this.responseMessage.getParseException());
								this.ejbUtils.sendRispostaApplicativaErrore(responseMessageError,this.richiestaDelegata,this.rollbackRichiesta,this.pd,this.sa);
								this.openspcoopstate.releaseResource();
								esito.setEsitoInvocazione(true);	
								esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,this.responseMessage.getParseException().getParseException().getMessage());
								return esito;
							}
						}
					}else{
						// Se Non e' attivo una gestione dei riscontri, faccio il rollback sulla coda.
						// Altrimenti verra attivato la gestione dei riscontri che riprovera' dopo un tot.
						if(this.gestioneBusteNonRiscontrateAttive==false){
							if(this.riconsegna){
								this.ejbUtils.rollbackMessage(erroreConnettore,this.dataRiconsegna, esito);
								esito.setEsitoInvocazione(false);
								esito.setStatoInvocazione(EsitoLib.ERRORE_NON_GESTITO,erroreConnettore);
							}else{
								this.ejbUtils.releaseOutboxMessage(true);
								esito.setEsitoInvocazione(true);
								esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,erroreConnettore);
							}
						}else{
							this.ejbUtils.updateErroreProcessamentoMessage(erroreConnettore,this.dataRiconsegna, esito);
							esito.setEsitoInvocazione(true);
							esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,erroreConnettore);
						}
						this.openspcoopstate.releaseResource();
						return esito;
					}
				}
			}





			

			
			
			/* ------------------------- Gestione Header Integrazione / Correlazione Applicativa  ---------------------------- */
			
			HeaderIntegrazione headerIntegrazioneRisposta = new HeaderIntegrazione(this.idTransazione);
			
			Utilities.printFreeMemory("InoltroBuste - Gestione Header Integrazione / Correlazione Applicativa... ");
			
				
			// *** Header Integrazione Risposta ***
			this.msgDiag.mediumDebug("Gestione Header Integrazione... ");
			String[] tipiIntegrazionePD_risposta = null;
			InResponsePDMessage inResponsePDMessage = null;
			if(this.propertiesReader.processHeaderIntegrazionePDResponse(this.functionAsRouter)){
				try {
					if(this.pd!=null)
						tipiIntegrazionePD_risposta = this.configurazionePdDManager.getTipiIntegrazione(this.pd);
				} catch (Exception e) {
					this.msgDiag.logErroreGenerico(e, "getTipiIntegrazione(pd)");
					if(this.sendRispostaApplicativa){
						OpenSPCoop2Message responseMessageError = 
								this.generatoreErrore.build(this.pddContext,IntegrationFunctionError.INTERNAL_RESPONSE_ERROR,
										ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
											get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE),e,
											(this.responseMessage!=null ? this.responseMessage.getParseException() : null));
						this.ejbUtils.sendRispostaApplicativaErrore(responseMessageError,this.richiestaDelegata,this.rollbackRichiesta,this.pd,this.sa);
						esito.setEsitoInvocazione(true);
						esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,"getTipiIntegrazione(pd)");
					}else{
						// Se Non e' attivo una gestione dei riscontri, faccio il rollback sulla coda.
						// Altrimenti verra attivato la gestione dei riscontri che riprovera' dopo un tot.
						if(this.gestioneBusteNonRiscontrateAttive==false){
							this.ejbUtils.rollbackMessage("Gestione header Integrazione Risposta non riuscita.", esito);
							esito.setStatoInvocazioneErroreNonGestito(e);
							esito.setEsitoInvocazione(false);
						}else{
							this.ejbUtils.updateErroreProcessamentoMessage("Gestione header Integrazione Risposta non riuscita.", esito);
							esito.setEsitoInvocazione(true);
							esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,"getTipiIntegrazione(pd)");
						}
					}
					this.openspcoopstate.releaseResource();
					return esito;
				}
				
				if (tipiIntegrazionePD_risposta == null){
					if(InoltroBuste.defaultPerProtocolloGestoreIntegrazionePD.containsKey(this.protocolFactory.getProtocol())){
						tipiIntegrazionePD_risposta = InoltroBuste.defaultPerProtocolloGestoreIntegrazionePD.get(this.protocolFactory.getProtocol());
					}else{
						tipiIntegrazionePD_risposta = InoltroBuste.defaultGestoriIntegrazionePD;
					}
				}
				
				inResponsePDMessage = new InResponsePDMessage();
				inResponsePDMessage.setBustaRichiesta(this.bustaRichiesta);
				inResponsePDMessage.setMessage(this.responseMessage);
				inResponsePDMessage.setPortaDelegata(this.pd);
				inResponsePDMessage.setHeaders(this.connectorSender.getHeaderTrasporto());
				inResponsePDMessage.setServizio(this.idServizio);
				inResponsePDMessage.setSoggettoMittente(this.soggettoFruitore);
				for (int i = 0; i < tipiIntegrazionePD_risposta.length; i++) {
					try {
						IGestoreIntegrazionePD gestore = null;
						try{
							gestore = (IGestoreIntegrazionePD) this.pluginLoader.newIntegrazionePortaDelegata(tipiIntegrazionePD_risposta[i]);
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
										"] da utilizzare per la gestione dell'integrazione (Risposta) delle fruizioni di tipo ["+tipiIntegrazionePD_risposta[i]+"]: "+e.getMessage());
							}
							if(this.responseMessage!=null){
								gestore.readInResponseHeader(headerIntegrazioneRisposta,inResponsePDMessage);
							}else if( ! (gestore instanceof IGestoreIntegrazionePDSoap) ){
								gestore.readInResponseHeader(headerIntegrazioneRisposta,inResponsePDMessage);
							}
						} else {
							this.msgDiag.logErroreGenerico("Lettura Gestore header di integrazione ["
											+ tipiIntegrazionePD_risposta[i]+ "]  non riuscita: non inizializzato", 
											"gestoriIntegrazionePD.get("+tipiIntegrazionePD_risposta[i]+")");
						}
					} catch (Exception e) {
						this.log.debug("Errore durante la lettura dell'header di integrazione ["+ tipiIntegrazionePD_risposta[i]
										+ "]: "+ e.getMessage(),e);
						this.msgDiag.addKeyword(CostantiPdD.KEY_TIPO_HEADER_INTEGRAZIONE,tipiIntegrazionePD_risposta[i]);
						this.msgDiag.addKeywordErroreProcessamento(e);
						this.msgDiag.logPersonalizzato("headerIntegrazione.letturaFallita");
					}
				}
			}
		
			// *** Correlazione Applicativa ***
			this.msgDiag.mediumDebug("Gestione Correlazione Applicativa... ");
			
			// Correlazione Applicativa
			if(this.functionAsRouter==false && this.pd!=null && this.pd.getCorrelazioneApplicativaRisposta()!=null){
				GestoreCorrelazioneApplicativa gestoreCorrelazione = null;
				try{
					
					gestoreCorrelazione = 
							new GestoreCorrelazioneApplicativa(this.openspcoopstate.getStatoRisposta(),
									this.log,this.soggettoFruitore,this.idServizio, this.servizioApplicativoFruitore, this.protocolFactory,
									this.transactionNullable, this.pddContext);

					gestoreCorrelazione.verificaCorrelazioneRisposta(this.pd.getCorrelazioneApplicativaRisposta(), this.responseMessage, headerIntegrazioneRisposta, false);
					
					this.idCorrelazioneApplicativaRisposta = gestoreCorrelazione.getIdCorrelazione();
					
					if(this.idCorrelazioneApplicativaRisposta!=null) {
						if(this.transactionNullable!=null) {
							this.transactionNullable.setCorrelazioneApplicativaRisposta(this.idCorrelazioneApplicativaRisposta);
						}
					}
					
					this.msgDiag.setIdCorrelazioneRisposta(this.idCorrelazioneApplicativaRisposta);

					if(this.richiestaDelegata!=null){
						this.richiestaDelegata.setIdCorrelazioneApplicativaRisposta(this.idCorrelazioneApplicativaRisposta);
					}
					
				}catch(Exception e){
					
					this.pddContext.addObject(org.openspcoop2.core.constants.Costanti.ERRORE_CORRELAZIONE_APPLICATIVA_RISPOSTA, "true");
					
					this.msgDiag.logErroreGenerico(e, "gestioneCorrelazioneApplicativaRisposta");
					this.log.error("Riscontrato errore durante il controllo di correlazione applicativa della risposta: "+ e.getMessage(),e);
					
					ErroreIntegrazione errore = null;
					if(gestoreCorrelazione!=null){
						errore = gestoreCorrelazione.getErrore();
					}
					if(errore==null){
						errore = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
								get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_553_CORRELAZIONE_APPLICATIVA_RISPOSTA_NON_RIUSCITA);
					}
					if(this.sendRispostaApplicativa){
						OpenSPCoop2Message responseMessageError = 
								this.generatoreErrore.build(this.pddContext,IntegrationFunctionError.APPLICATION_CORRELATION_IDENTIFICATION_RESPONSE_FAILED,
										errore,e,
											(this.responseMessage!=null ? this.responseMessage.getParseException() : null));
						this.ejbUtils.sendRispostaApplicativaErrore(responseMessageError,this.richiestaDelegata,this.rollbackRichiesta,this.pd,this.sa);
						esito.setEsitoInvocazione(true);
						esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,"gestioneCorrelazioneApplicativaRisposta");
					}else{
						// Se Non e' attivo una gestione dei riscontri, faccio il rollback sulla coda.
						// Altrimenti verra attivato la gestione dei riscontri che riprovera' dopo un tot.
						if(this.gestioneBusteNonRiscontrateAttive==false){
							this.ejbUtils.rollbackMessage("Gestione Correlazione Applicativa Risposta non riuscita.", esito);
							esito.setStatoInvocazioneErroreNonGestito(e);
							esito.setEsitoInvocazione(false);
						}else{
							this.ejbUtils.updateErroreProcessamentoMessage("Gestione Correlazione Applicativa Risposta non riuscita.", esito);
							esito.setEsitoInvocazione(true);
							esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,"gestioneCorrelazioneApplicativaRisposta");
						}
					}
					this.openspcoopstate.releaseResource();
					return esito;
				}
			}







			
			
			
			// Sbustamento Protocollo Dopo Correlazione Applicativa
			
			if( sbustamentoInformazioniProtocolloRispostaDopoCorrelazione ){		
				
				// Qua dentro ci entro sia se sono router, che se è ancora rilevata la presenza di un protocollo dopo la validazione semantica
				
				// Estrazione header busta
				this.msgDiag.mediumDebug("Sbustamento della risposta (PostCorrelazione PreConsegna)...");
				try{
									
					if(this.sbustamentoInformazioniProtocolloRisposta){
						// effettuo lo stesso sbustamento invocandolo con la nuova fase
						// Questa invocazione andrebbe implementata su ricezionecontenutiApplicativi teoricamente
						this.msgDiag.highDebug("Tipo Messaggio Risposta prima dello sbustamento ["+FaseSbustamento.PRE_CONSEGNA_RISPOSTA
								+"] ["+this.responseMessage.getClass().getName()+"]");
						ProtocolMessage protocolMessage = sbustatore.sbustamento(this.responseMessage, this.pddContext,
								bustaRisposta,
								RuoloMessaggio.RISPOSTA,sbustamentoManifestRisposta,this.proprietaManifestAttachments,
								FaseSbustamento.PRE_CONSEGNA_RISPOSTA, this.requestInfo);
						if(protocolMessage!=null ) {
							if(!protocolMessage.isUseBustaRawContentReadByValidation()) {
								this.headerProtocolloRisposta = protocolMessage.getBustaRawContent();
							}
							this.responseMessage = protocolMessage.getMessage(); // updated
						}
						this.msgDiag.highDebug("Tipo Messaggio Risposta dopo lo sbustamento ["+FaseSbustamento.PRE_CONSEGNA_RISPOSTA
								+"] ["+this.responseMessage.getClass().getName()+"]");	
					}
						
				}catch(Exception e){
					this.msgDiag.logErroreGenerico(e,"sbustatore.sbustamento("+bustaRisposta.getID()+")");
					
					EsitoElaborazioneMessaggioTracciato esitoTraccia = EsitoElaborazioneMessaggioTracciato.getEsitoElaborazioneConErrore("Errore durante lo sbustamento della risposta: "+e.getMessage());
					this.tracciamento.registraRisposta(this.responseMessage,securityInfoResponse,this.headerProtocolloRisposta,bustaRisposta,esitoTraccia,
							Tracciamento.createLocationString(true, this.location),
							this.idCorrelazioneApplicativa,this.idCorrelazioneApplicativaRisposta); // non ancora registrata
					
					if(this.functionAsRouter){
						this.ejbUtils.sendAsRispostaBustaErroreProcessamento(this.richiestaDelegata.getIdModuloInAttesa(),
								this.bustaRichiesta,
								ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
									get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_527_GESTIONE_SBUSTAMENTO),
								this.idCorrelazioneApplicativa,this.idCorrelazioneApplicativaRisposta,this.servizioApplicativoFruitore,e,
								(this.responseMessage!=null ? this.responseMessage.getParseException() : null));
						esito.setEsitoInvocazione(true);
						esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
								"Sbustamento non riuscito");
					}else{
												
						ErroreIntegrazione erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
								get5XX_ErroreProcessamento(e,CodiceErroreIntegrazione.CODICE_527_GESTIONE_SBUSTAMENTO);
						if(this.sendRispostaApplicativa){
							OpenSPCoop2Message responseMessageError = this.generatoreErrore.build(this.pddContext,IntegrationFunctionError.INTERNAL_RESPONSE_ERROR,
									erroreIntegrazione,e,
										(this.responseMessage!=null ? this.responseMessage.getParseException() : null));
							this.ejbUtils.sendRispostaApplicativaErrore(responseMessageError,this.richiestaDelegata,this.rollbackRichiesta,this.pd,this.sa);
							esito.setEsitoInvocazione(true);
							esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,"Sbustamento non riuscito");
						}else{
							// Se Non e' attivo una gestione dei riscontri, faccio il rollback sulla coda.
							// Altrimenti verra attivato la gestione dei riscontri che riprovera' dopo un tot.
							if(this.gestioneBusteNonRiscontrateAttive==false){
								this.ejbUtils.rollbackMessage("Sbustamento non riuscito", esito);
								esito.setStatoInvocazioneErroreNonGestito(e);
								esito.setEsitoInvocazione(false);
							}else{
								this.ejbUtils.updateErroreProcessamentoMessage("Sbustamento non riuscito", esito);
								esito.setEsitoInvocazione(true);
								esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,"Sbustamento non riuscito");
							}
						}
						
					}
					this.openspcoopstate.releaseResource();
					return esito;

				}
			}

			
			
			
			
			
			
			
			
			
			
			


			Utilities.printFreeMemory("InoltroBuste - Gestione Risposta... ");








			/* ---------------- Gestione Risposta  (operazioni comuni per tutti i profili) ------------------- */ 
			boolean isMessaggioErroreProtocollo = false;
			boolean bustaDiServizio = false;
			java.util.List<Eccezione> erroriValidazione = null;
			IntegrationFunctionError validazione_integrationFunctionError = null;
			java.util.List<Eccezione> erroriProcessamento = null;
			SbustamentoRisposteMessage sbustamentoRisposteMSG = null;
			DettaglioEccezione dettaglioEccezione = null;

			if(presenzaRispostaProtocollo){

				// Aggiornamento Informazioni
				this.msgDiag.setIdMessaggioRisposta(this.idMessageResponse);

				// Gestione Specifica per Buste
				bustaRisposta = this.validatore.getBusta();
				this.idMessageResponse = bustaRisposta.getID();
				
				// Imposto eventuali informazioni DirectVM
				if(bustaRisposta!=null && this.pddContext!=null){
					DirectVMProtocolInfo.setInfoFromContext(this.pddContext, bustaRisposta);
				}
				
				// Aggiunto risposta a pddContext
				if(bustaRisposta!=null){
					this.pddContext.addObject(CostantiPdD.BUSTA_RISPOSTA, bustaRisposta);
				}

				// Se non impostati, imposto i domini
				org.openspcoop2.pdd.core.Utilities.refreshIdentificativiPorta(bustaRisposta, this.requestInfo.getIdentitaPdD(), this.registroServiziManager, this.protocolFactory);

				// aggiunto dal protocollo
				if(this.richiestaDelegata!=null && this.richiestaDelegata.getIdCollaborazione()==null && bustaRisposta.getCollaborazione()!=null) {
					this.richiestaDelegata.setIdCollaborazione(bustaRisposta.getCollaborazione());
				}
				
				isMessaggioErroreProtocollo = this.validatore.isErroreProtocollo();
				bustaDiServizio = this.validatore.isBustaDiServizio();
				erroriValidazione = this.validatore.getEccezioniValidazione();
				validazione_integrationFunctionError = this.validatore.getErrore_integrationFunctionError();
				erroriProcessamento =this.validatore.getEccezioniProcessamento();

				// Registrazione Msg
				if(isMessaggioErroreProtocollo){
					if(this.validatore.isMessaggioErroreIntestazione()){
						this.msgDiag.addKeyword(CostantiPdD.KEY_TIPO_MESSAGGIO_BUSTA_ERRORE, CostantiPdD.TIPO_MESSAGGIO_BUSTA_ERRORE_INTESTAZIONE);
					}else{
						this.msgDiag.addKeyword(CostantiPdD.KEY_TIPO_MESSAGGIO_BUSTA_ERRORE, CostantiPdD.TIPO_MESSAGGIO_BUSTA_ERRORE_PROCESSAMENTO);
					}
					this.msgDiag.logPersonalizzato("ricezioneMessaggioErrore");
					
					// Esamino se e' presente un elemento DettaglioEccezione
					dettaglioEccezione = XMLUtils.getDettaglioEccezione(this.log,this.responseMessage);
					
				}else{
					this.msgDiag.logPersonalizzato("ricezioneMessaggio");
				}

				// se ci sono errori di processamento gestisco errore.
				if(erroriProcessamento.size()>0){
					StringBuilder errore = new StringBuilder();
					Eccezione ecc = null;
					if( erroriProcessamento.size()>1 ){
						ecc = Eccezione.getEccezioneProcessamento(ErroriCooperazione.ERRORE_GENERICO_PROCESSAMENTO_MESSAGGIO.getErroreCooperazione(), this.protocolFactory);
						for(int k=0; k<erroriProcessamento.size();k++){
							Eccezione eccProcessamento = erroriProcessamento.get(k);
							
							if(k>0) {
								errore.append("\n");
							}
							errore.append("["+this.traduttore.toString(eccProcessamento.getCodiceEccezione(),eccProcessamento.getSubCodiceEccezione())+"]");
							if(eccProcessamento.getDescrizione(this.protocolFactory)!=null){
								errore.append(" "+eccProcessamento.getDescrizione(this.protocolFactory));
							}
							
						}
						if(errore.length()>0)
							ecc.setDescrizione(errore.toString());		
					}else{
						ecc = erroriProcessamento.get(0);
						errore.append("["+this.traduttore.toString(ecc.getCodiceEccezione(),ecc.getSubCodiceEccezione())+"]");
						if(ecc.getDescrizione(this.protocolFactory)!=null){
							errore.append(" "+ecc.getDescrizione(this.protocolFactory));
						}
					}
					EsitoElaborazioneMessaggioTracciato esitoTraccia = null;
					if(isMessaggioErroreProtocollo){
						this.msgDiag.addKeyword(CostantiPdD.KEY_ECCEZIONI, errore.toString());
						this.msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_SBUSTAMENTO_RISPOSTE,"validazioneBustaErrore.listaEccezioniMalformata");
						esitoTraccia = EsitoElaborazioneMessaggioTracciato.getEsitoElaborazioneConErrore(this.msgDiag.getMessaggio_replaceKeywords(MsgDiagnosticiProperties.MSG_DIAG_SBUSTAMENTO_RISPOSTE,"validazioneBustaErrore.listaEccezioniMalformata"));
					}else{
						this.msgDiag.addKeyword(CostantiPdD.KEY_ECCEZIONI, errore.toString());
						this.msgDiag.addKeyword(CostantiPdD.KEY_NUMERO_ECCEZIONI, erroriProcessamento.size()+"");
						this.msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_SBUSTAMENTO_RISPOSTE,"validazioneBusta.bustaNonCorretta");
						esitoTraccia = EsitoElaborazioneMessaggioTracciato.getEsitoElaborazioneConErrore(this.msgDiag.getMessaggio_replaceKeywords(MsgDiagnosticiProperties.MSG_DIAG_SBUSTAMENTO_RISPOSTE,"validazioneBusta.bustaNonCorretta"));
					}
					this.tracciamento.registraRisposta(this.responseMessage,securityInfoResponse,this.headerProtocolloRisposta,bustaRisposta,esitoTraccia,
							Tracciamento.createLocationString(true, this.location),
							this.idCorrelazioneApplicativa,this.idCorrelazioneApplicativaRisposta); // non ancora registrata
					if(this.functionAsRouter){
						this.ejbUtils.sendAsRispostaBustaErroreProcessamento(this.richiestaDelegata.getIdModuloInAttesa(),
								this.bustaRichiesta,erroriProcessamento,
								this.idCorrelazioneApplicativa,this.idCorrelazioneApplicativaRisposta,this.servizioApplicativoFruitore,null,
								(this.responseMessage!=null ? this.responseMessage.getParseException() : null));
					}else{
						String erroreRilevato = "Riscontrato errore durante la validazione della busta di risposta con id["+this.idMessageResponse+"]:\n"+errore.toString();
						if(this.sendRispostaApplicativa){
							OpenSPCoop2Message responseMessageError =  
									this.generatoreErrore.build(this.pddContext,IntegrationFunctionError.INTERNAL_RESPONSE_ERROR,ecc,this.richiestaDelegata.getIdSoggettoFruitore(),
												(this.responseMessage!=null ? this.responseMessage.getParseException() : null));
							this.ejbUtils.sendRispostaApplicativaErrore(responseMessageError,this.richiestaDelegata,this.rollbackRichiesta,this.pd,this.sa);
							esito.setEsitoInvocazione(true);
							esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,erroreRilevato);
						}else{
							// Se Non e' attivo una gestione dei riscontri, faccio il rollback sulla coda.
							// Altrimenti verra attivato la gestione dei riscontri che riprovera' dopo un tot.
							if(this.gestioneBusteNonRiscontrateAttive==false){
								this.ejbUtils.rollbackMessage(erroreRilevato, esito);
								esito.setEsitoInvocazione(false);
								esito.setStatoInvocazione(EsitoLib.ERRORE_NON_GESTITO,erroreRilevato);
							}else{
								this.ejbUtils.updateErroreProcessamentoMessage(erroreRilevato, esito);
								esito.setEsitoInvocazione(true);
								esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,erroreRilevato);
							}
						}
					}
					this.openspcoopstate.releaseResource();
					return esito;
				}

				// Tracciamento Risposta
				this.msgDiag.mediumDebug("Tracciamento della busta di risposta...");
				EsitoElaborazioneMessaggioTracciato esitoTraccia = EsitoElaborazioneMessaggioTracciato.getEsitoElaborazioneMessaggioRicevuto();
				this.tracciamento.registraRisposta(this.responseMessage,securityInfoResponse,this.headerProtocolloRisposta,bustaRisposta,esitoTraccia,
						Tracciamento.createLocationString(true, this.location),
						this.idCorrelazioneApplicativa,this.idCorrelazioneApplicativaRisposta);

				// Costruzione oggetto da spedire al modulo 'SbustamentoRisposte' (se routing non attivo)
				if(this.functionAsRouter==false){
					sbustamentoRisposteMSG = new SbustamentoRisposteMessage();
					sbustamentoRisposteMSG.setRichiestaDelegata(this.richiestaDelegata);
					sbustamentoRisposteMSG.setBusta(bustaRisposta);
					sbustamentoRisposteMSG.setErrors(erroriValidazione, validazione_integrationFunctionError);
					sbustamentoRisposteMSG.setMessaggioErroreProtocollo(isMessaggioErroreProtocollo);
					sbustamentoRisposteMSG.setIsBustaDiServizio(bustaDiServizio);
					sbustamentoRisposteMSG.setRuoloBustaRicevuta(this.validatore.getRuoloBustaRicevuta(this.requestInfo.getProtocolServiceBinding(),true));
					sbustamentoRisposteMSG.setSpedizioneMsgIngresso(this.ejbUtils.getSpedizioneMsgIngresso());
					sbustamentoRisposteMSG.setRicezioneMsgRisposta(this.ejbUtils.getRicezioneMsgRisposta());
					sbustamentoRisposteMSG.setOneWayVersione11(this.oneWayVersione11);
					sbustamentoRisposteMSG.setImplementazionePdDSoggettoMittente(this.inoltroBusteMsg.getImplementazionePdDSoggettoMittente());
					sbustamentoRisposteMSG.setImplementazionePdDSoggettoDestinatario(this.inoltroBusteMsg.getImplementazionePdDSoggettoDestinatario());
					sbustamentoRisposteMSG.setPddContext(this.pddContext);
					sbustamentoRisposteMSG.setDettaglioEccezione(dettaglioEccezione);
					if(this.validatore.getInfoServizio()!=null){
						sbustamentoRisposteMSG.setFiltroDuplicati(Inoltro.SENZA_DUPLICATI.equals(this.validatore.getInfoServizio().getInoltro()));
					}
				}				
				// Costruzione sessione messaggio di risposta
				String tipoMsg = Costanti.INBOX;
				if(this.functionAsRouter)
					tipoMsg = Costanti.OUTBOX;
				this.msgDiag.mediumDebug("Registrazione messaggio di risposta nel RepositoryMessaggi/Buste...");
				this.msgResponse = new GestoreMessaggi(this.openspcoopstate, false, this.idMessageResponse,tipoMsg,this.msgDiag, this.pddContext);
				this.msgResponse.setPortaDiTipoStateless(this.portaDiTipoStateless);
				this.msgResponse.setOneWayVersione11(this.oneWayVersione11);
				

				
				// *** Aggiornamento/Eliminazione header integrazione ***
				this.msgDiag.mediumDebug("Aggiornamento/Eliminazione Header Integrazione... ");
				if(this.responseMessage!=null && tipiIntegrazionePD_risposta!=null){
					for (int i = 0; i < tipiIntegrazionePD_risposta.length; i++) {
						try {
							
							IGestoreIntegrazionePD gestore = null;
							try{
								gestore = (IGestoreIntegrazionePD) this.pluginLoader.newIntegrazionePortaDelegata(tipiIntegrazionePD_risposta[i]);
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
											"] da utilizzare per la gestione dell'integrazione (Risposta Update/Delete) delle fruizioni di tipo ["+tipiIntegrazionePD_risposta[i]+"]: "+e.getMessage());
								}
								if (gestore instanceof IGestoreIntegrazionePDSoap) {
									if(this.propertiesReader.deleteHeaderIntegrazioneResponsePD()){
										((IGestoreIntegrazionePDSoap)gestore).deleteInResponseHeader(inResponsePDMessage);
									}else{
										((IGestoreIntegrazionePDSoap)gestore).updateInResponseHeader(inResponsePDMessage, this.idMessageRequest, this.idMessageResponse, 
												this.servizioApplicativoFruitore, this.idCorrelazioneApplicativaRisposta, this.idCorrelazioneApplicativa);
									}
								}
							}
						} catch (Exception e) {
							String motivoErrore = null;
							if(this.propertiesReader.deleteHeaderIntegrazioneResponsePD()){
								motivoErrore = "deleteHeaderIntegrazione("+ tipiIntegrazionePD_risposta[i]+")";
							}else{
								motivoErrore = "updateHeaderIntegrazione("+ tipiIntegrazionePD_risposta[i]+")";
							}
							this.msgDiag.logErroreGenerico(e,motivoErrore);
							if(this.sendRispostaApplicativa){
								OpenSPCoop2Message responseMessageError = 
										this.generatoreErrore.build(this.pddContext,IntegrationFunctionError.INTERNAL_RESPONSE_ERROR,
												ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
													get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_541_GESTIONE_HEADER_INTEGRAZIONE),e,
														(this.responseMessage!=null ? this.responseMessage.getParseException() : null));
								this.ejbUtils.sendRispostaApplicativaErrore(responseMessageError,this.richiestaDelegata,this.rollbackRichiesta,this.pd,this.sa);
								esito.setEsitoInvocazione(true);
								esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,motivoErrore);
							}else{
								// Se Non e' attivo una gestione dei riscontri, faccio il rollback sulla coda.
								// Altrimenti verra attivato la gestione dei riscontri che riprovera' dopo un tot.
								if(this.gestioneBusteNonRiscontrateAttive==false){
									this.ejbUtils.rollbackMessage(motivoErrore, esito);
									esito.setStatoInvocazioneErroreNonGestito(e);
									esito.setEsitoInvocazione(false);
								}else{
									this.ejbUtils.updateErroreProcessamentoMessage(motivoErrore, esito);
									esito.setEsitoInvocazione(true);
									esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,motivoErrore);
								}
							}
							this.openspcoopstate.releaseResource();
							return esito;
						}
					}
				}
				
				
				
				try{
					RepositoryBuste repositoryResponse = null;
					if(this.functionAsRouter==false){
						repositoryResponse = new RepositoryBuste(this.openspcoopstate.getStatoRisposta(), false, this.protocolFactory);
					}

					if( this.msgResponse.existsMessage_noCache() ){
						// Se il proprietario attuale e' GestoreMessaggi, forzo l'eliminazione e continuo a processare il messaggio.
						String proprietarioMessaggio = this.msgResponse.getProprietario(InoltroBuste.ID_MODULO);
						if(TimerGestoreMessaggi.ID_MODULO.equals(proprietarioMessaggio)){
							this.msgDiag.logPersonalizzato("ricezioneSoapMessage.msgGiaPresente");
							String msg = this.msgDiag.getMessaggio_replaceKeywords("ricezioneSoapMessage.msgGiaPresente");
							if(this.propertiesReader.isMsgGiaInProcessamento_useLock()) {
								this.msgResponse._deleteMessageWithLock(msg,this.propertiesReader.getMsgGiaInProcessamento_AttesaAttiva(),
										this.propertiesReader.getMsgGiaInProcessamento_CheckInterval());
							}
							else {
								this.msgResponse.deleteMessageByNow();
							}
						}else{
							throw new Exception("Altra copia della Busta ricevuta come risposta con id["+this.idMessageResponse+"] in elaborazione dal modulo "+proprietarioMessaggio);
						}
					}

					// Aggiungo ErroreApplicativo come details se non sono router.
					if(this.functionAsRouter==false){
						if(this.soapFault!=null){
							if (!isMessaggioErroreProtocollo) {
								if(this.enrichSoapFaultApplicativo){
									IntegrationFunctionError integrationFunctionError = getIntegrationFunctionErroreConnectionError();
									this.generatoreErrore.getErroreApplicativoBuilderForAddDetailInSoapFault(this.pddContext, this.responseMessage.getMessageType(), integrationFunctionError).
										insertInSOAPFault(ErroriIntegrazione.ERRORE_516_CONNETTORE_UTILIZZO_CON_ERRORE.
												get516_ServizioApplicativoNonDisponibile(), 
												this.responseMessage);
								}
							}
						}
					}

					this.msgResponse.registraMessaggio(this.responseMessage,this.idCorrelazioneApplicativa, this.idCorrelazioneApplicativaRisposta);
					if(this.richiestaAsincronaSimmetricaStateless && (!isMessaggioErroreProtocollo)){
						// In caso di richiestaAsincronaSimmetrica e openspcoop stateless,
						// Devo comunque salvare le informazioni sul msg della ricevuta alla richiesta.
						// Tali informazioni servono per il check nel modulo RicezioneBuste, per verificare di gestire la risposta
						// solo dopo aver terminato di gestire la richiesta con relativa ricevuta.
						this.msgResponse.registraInformazioniMessaggio_statelessEngine(this.ejbUtils.getRicezioneMsgRisposta(), 
								org.openspcoop2.pdd.mdb.SbustamentoRisposte.ID_MODULO,
								this.idMessageRequest,
								this.idCorrelazioneApplicativa, this.idCorrelazioneApplicativaRisposta);
					}

					// registrazione risposta 
					if(this.functionAsRouter==false){
						
						//Se functionAsRouter viene registrato in EJBUtils.sendRispostaProtocollo
						
						this.msgResponse.aggiornaRiferimentoMessaggio(this.idMessageRequest);
						
						boolean bustaRispostaGiaRegistrata = false;
						if( (this.openspcoopstate instanceof OpenSPCoopStateful) || (this.oneWayVersione11)  ){
							bustaRispostaGiaRegistrata =repositoryResponse.isRegistrata(bustaRisposta.getID(),tipoMsg);
						}
						if(bustaRispostaGiaRegistrata){
							repositoryResponse.aggiornaBusta(bustaRisposta,tipoMsg,this.propertiesReader.getRepositoryIntervalloScadenzaMessaggi(),erroriValidazione);
							repositoryResponse.impostaUtilizzoPdD(bustaRisposta.getID(),tipoMsg);
						}
						else{
							repositoryResponse.registraBusta(bustaRisposta,tipoMsg, erroriValidazione ,this.propertiesReader.getRepositoryIntervalloScadenzaMessaggi());
						}
						Integrazione infoIntegrazione = new Integrazione();
						infoIntegrazione.setIdModuloInAttesa(this.richiestaDelegata.getIdModuloInAttesa());
						infoIntegrazione.setNomePorta(this.richiestaDelegata.getIdPortaDelegata().getNome());
						infoIntegrazione.setServizioApplicativo(this.richiestaDelegata.getServizioApplicativo());
						infoIntegrazione.setScenario(this.richiestaDelegata.getScenario());
						repositoryResponse.aggiornaInfoIntegrazione(bustaRisposta.getID(),tipoMsg,infoIntegrazione);
					}

				}catch(Exception e){
					if(this.msgResponse!=null){
						this.msgResponse.deleteMessageFromFileSystem(); // elimino eventuale risposta salvata su fileSystem
					}
					this.msgDiag.logErroreGenerico(e,"RegistrazioneRisposta("+this.idMessageResponse+")");
					if(this.functionAsRouter){
						this.ejbUtils.sendAsRispostaBustaErroreProcessamento(this.richiestaDelegata.getIdModuloInAttesa(),
								this.bustaRichiesta,
								ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
									get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_510_SAVE_RESPONSE_MSG),
								this.idCorrelazioneApplicativa,this.idCorrelazioneApplicativaRisposta,this.servizioApplicativoFruitore,e,
								(this.responseMessage!=null ? this.responseMessage.getParseException() : null));
						esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,"RegistrazioneRisposta("+this.idMessageResponse+")");
						esito.setEsitoInvocazione(true);
					}else{
						if(this.sendRispostaApplicativa){
							OpenSPCoop2Message responseMessageError = null;
							responseMessageError = 
									this.generatoreErrore.build(this.pddContext,IntegrationFunctionError.INTERNAL_RESPONSE_ERROR,
											ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
												get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_510_SAVE_RESPONSE_MSG),
												null, (this.responseMessage!=null ? this.responseMessage.getParseException() : null));
							this.ejbUtils.sendRispostaApplicativaErrore(responseMessageError,this.richiestaDelegata,this.rollbackRichiesta,this.pd,this.sa);
							esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,"RegistrazioneRisposta("+this.idMessageResponse+")");
							esito.setEsitoInvocazione(true);
						}else{
							//Se Non e' attivo una gestione dei riscontri, faccio il rollback sulla coda.
							// Altrimenti verra attivato la gestione dei riscontri che riprovera' dopo un tot.
							if(this.gestioneBusteNonRiscontrateAttive==false){
								this.ejbUtils.rollbackMessage("Salvataggio risposta non riuscito", esito);
								esito.setStatoInvocazioneErroreNonGestito(e);
								esito.setEsitoInvocazione(false);
							}else{
								this.ejbUtils.updateErroreProcessamentoMessage("Salvataggio risposta non riuscito", esito);
								esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,"RegistrazioneRisposta("+this.idMessageResponse+")");
								esito.setEsitoInvocazione(true);
							}
						}
					}
					this.openspcoopstate.releaseResource();
					return esito;
				}
			}








			/* -------Scenario RoutingSincrono -------------
			 * Invio di una busta con profilo sincrono (che non e' un msg di errore).
			 * In questo contesto la risposta deve essere sempre inviata sulla stessa connessione.
			 * Casi di fault:
			 * 1) Ricevere niente, carico HTTP Reply vuoto: 
			 *               E' tutto OK, invio lo sblocco verso RicezioneBuste            
			 * 2) e 3)  Ricevere un MessaggioProtocollo Errore o Normale: 
			 *               Forward verso RicezioneBuste  		   
			 * 4) Ricevere un MessaggioSoap (con o senza Fault) senza una busta associata: 
			 *               E' tutto OK, invio lo sblocco verso RicezioneBuste
			 *               In presenza di un Fault, si effettua la sua registrazione.
			 */ 
			/* -------- Scenario RoutingNotSincrono ------------
			 * Scenario alternativo al RoutingSincrono, include i msg:
			 * - profilo non sincrono
			 * - busta di servizio (servizio non presente)
			 * - busta Errore
			 * Casi di fault:
			 * 1) Ricevere niente, carico HTTP Reply vuoto: 
			 *          E' tutto OK,
			 *          se attiva la gestione sulla stessa connessione invio lo sblocco verso RicezioneBuste.
			 * 2) e 3)  Ricevere un MessaggioProtocollo Errore o Normale: 
			 *          Se attiva la gestione sulla stessa connessione invio la busta verso RicezioneBuste,
			 *          altrimenti la invio al modulo InoltroRisposte		   
			 * 4) Ricevere un MessaggioSoap (con o senza Fault) senza una busta associata: 
			 *           E' tutto OK,
			 *           se attiva la gestione sulla stessa connessione invio lo sblocco verso RicezioneBuste.
			 *           In presenza di un Fault, si effettua la sua registrazione.
			 */ 
			/* --------Scenario Sincrono------------
			 * Invio di una busta con profilo sincrono.
			 * In questo contesto la risposta deve essere sempre inviata sulla stessa connessione.
			 * Casi di fault:
			 * 1) Ricevere niente, carico HTTP Reply vuoto: 
			 *                segnalazione di errore al modulo SbustamentoRisposte
			 * 2) e 3)  Ricevere un MessaggioProtocollo Errore o Normale: forward verso SbustamentoRisposte 		   
			 * 4) Ricevere un MessaggioSoap (con o senza Fault) senza una busta associata: 
			 *               segnalazione di errore al modulo SbustamentoRisposte. 
			 *               In presenza di un Fault, si effettua la sua registrazione.
			 */
			/* -------- Scenario NotSincrono ------------
			 * Scenario alternativo al Sincrono, include i msg:
			 * - profilo non sincrono
			 * - busta Errore
			 * Casi di fault:
			 * 1) Ricevere niente, carico HTTP Reply vuoto: 
			 *          E' tutto OK, non devo fare altro.
			 * 2) e 3)  Ricevere un MessaggioProtocollo Errore o Normale: forward verso SbustamentoRisposte 
			 * 4) Ricevere un MessaggioSoap (con o senza Fault) senza una busta associata: 
			 *              e' tutto OK, non devo fare altro.   
			 *             In presenza di un Fault, si effettua la sua registrazione.
			 */ 


			this.msgDiag.mediumDebug("Registrazione eventuale fault...");
			//	Effettuo log dell'eventuale fault (registro anche i fault spcoop, potrebbero contenere dei details aggiunti da una PdD.)
			if( this.soapFault!=null && faultLogged==false ){
				this.msgDiag.addKeyword(CostantiPdD.KEY_SOAP_FAULT, SoapUtils.safe_toString(this.faultMessageFactory, this.soapFault, this.log));
				this.msgDiag.logPersonalizzato("ricezioneSoapFault");
				faultLogged = true;
			}
			else if( this.restProblem!=null && faultLogged==false ){
				this.msgDiag.addKeyword(CostantiPdD.KEY_REST_PROBLEM, this.restProblem.getRaw());
				this.msgDiag.logPersonalizzato("ricezioneRestProblem");
				faultLogged = true;
			}






			/* ------- Gestione punto 1 (Carico HTTP Reply vuoto) -------- */
			IValidatoreErrori validatoreErrori = this.protocolFactory.createValidatoreErrori(this.openspcoopstate.getStatoRisposta());
			if ( this.responseMessage == null ) {
				if(this.functionAsRouter){

					this.msgDiag.mediumDebug("Gestione punto 1 (Carico HTTP Reply vuoto) [router]...");
					
					// Scenario RoutingSincrono
					if( (org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.SINCRONO.equals(this.bustaRichiesta.getProfiloDiCollaborazione())) &&
							(!validatoreErrori.isBustaErrore(this.bustaRichiesta,this.requestMessageTrasformato,this.pValidazioneErrori)) ){
						this.msgDiag.mediumDebug("Spedizione msg di sblocco al modulo RicezioneBuste...");
						this.ejbUtils.sendSbloccoRicezioneBuste(this.richiestaDelegata.getIdModuloInAttesa());
					}
					// Scenario RoutingNotSincrono
					else{
						if(this.newConnectionForResponse==false){
							this.msgDiag.mediumDebug("Spedizione msg di sblocco al modulo RicezioneBuste...");
							this.msgResponse = this.ejbUtils.sendSbloccoRicezioneBuste(this.richiestaDelegata.getIdModuloInAttesa());
						}
					}

				}
				// Scenario OneWay in modalita sincrona
				else if(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ONEWAY.equals(this.bustaRichiesta.getProfiloDiCollaborazione()) &&
						this.sendRispostaApplicativa){
					this.msgDiag.mediumDebug("Invio messaggio 'OK' al modulo di RicezioneContenutiApplicativi (Carico HTTP Reply vuoto)...");
					if(this.protocolManager.isHttpEmptyResponseOneWay())
						this.msgResponse = this.ejbUtils.sendRispostaApplicativaOK(MessageUtilities.buildEmptyMessage(OpenSPCoop2MessageFactory.getDefaultMessageFactory(),
								this.requestInfo.getIntegrationRequestMessageType(),MessageRole.RESPONSE),
								this.richiestaDelegata,this.pd,this.sa);
					else
						this.msgResponse = this.ejbUtils.sendRispostaApplicativaOK(this.ejbUtils.buildOpenSPCoopOK(this.requestInfo.getIntegrationRequestMessageType(), this.idMessageRequest),this.richiestaDelegata,this.pd,this.sa);
				}
				// Scenario Sincrono
				else if(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.SINCRONO.equals(this.bustaRichiesta.getProfiloDiCollaborazione())){
					
					this.msgDiag.mediumDebug("Gestione punto 1 (Carico HTTP Reply vuoto) [pdd]...");
					if(this.msgResponse!=null){
						this.msgResponse.deleteMessageFromFileSystem(); // elimino eventuale risposta salvata su fileSystem
					}

					this.msgDiag.logPersonalizzato("profiloSincrono.rispostaNonPervenuta");
					OpenSPCoop2Message responseMessageError = 
							this.generatoreErrore.build(this.pddContext,IntegrationFunctionError.EXPECTED_RESPONSE_NOT_FOUND,
									ErroriIntegrazione.ERRORE_517_RISPOSTA_RICHIESTA_NON_RITORNATA.
										get517_RispostaRichiestaNonRitornata(this.bustaRichiesta.getTipoDestinatario()+this.bustaRichiesta.getDestinatario()),null,
											(this.responseMessage!=null ? this.responseMessage.getParseException() : null));							
					this.ejbUtils.sendRispostaApplicativaErrore(responseMessageError,this.richiestaDelegata,this.rollbackRichiesta,this.pd,this.sa);
					this.openspcoopstate.releaseResource();
					esito.setEsitoInvocazione(true);
					esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
							this.msgDiag.getMessaggio_replaceKeywords("profiloSincrono.rispostaNonPervenuta"));
					return esito;
				}
				// Scenario NotSincrono
				if (org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO.equals(this.bustaRichiesta.getProfiloDiCollaborazione()) ||
						org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO.equals(this.bustaRichiesta.getProfiloDiCollaborazione()) ) {
				
					boolean attesaRispostaSullaConnessioneHttp = true;
					if(!Costanti.SCENARIO_ASINCRONO_ASIMMETRICO_POLLING.equals(this.richiestaDelegata.getScenario())){
						if(this.newConnectionForResponse){
							attesaRispostaSullaConnessioneHttp = false;
						}
					}
					
					if(attesaRispostaSullaConnessioneHttp){
						
						this.msgDiag.mediumDebug("Gestione punto 1 (Carico HTTP Reply vuoto) (Asincroni) [pdd]...");
						if(this.msgResponse!=null){
							this.msgResponse.deleteMessageFromFileSystem(); // elimino eventuale risposta salvata su fileSystem
						}

						this.msgDiag.logPersonalizzato("profiloAsincrono.rispostaNonPervenuta");
						OpenSPCoop2Message responseMessageError = 
								this.generatoreErrore.build(this.pddContext,IntegrationFunctionError.EXPECTED_RESPONSE_NOT_FOUND,
										ErroriIntegrazione.ERRORE_517_RISPOSTA_RICHIESTA_NON_RITORNATA.
											get517_RispostaRichiestaNonRitornata(this.bustaRichiesta.getTipoDestinatario()+this.bustaRichiesta.getDestinatario()),null,
												(this.responseMessage!=null ? this.responseMessage.getParseException() : null));							
						this.ejbUtils.sendRispostaApplicativaErrore(responseMessageError,this.richiestaDelegata,this.rollbackRichiesta,this.pd,this.sa);
						this.openspcoopstate.releaseResource();
						esito.setEsitoInvocazione(true);
						esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
								this.msgDiag.getMessaggio_replaceKeywords("profiloAsincrono.rispostaNonPervenuta"));
						return esito;
						
					}
					else{
					
						if(this.protocolManager.isHttpEmptyResponseOneWay())
							this.msgResponse = this.ejbUtils.sendRispostaApplicativaOK(MessageUtilities.buildEmptyMessage(OpenSPCoop2MessageFactory.getDefaultMessageFactory(),
									this.requestInfo.getIntegrationRequestMessageType(),MessageRole.RESPONSE),this.richiestaDelegata,this.pd,this.sa);
						else
							this.msgResponse = this.ejbUtils.sendRispostaApplicativaOK(this.ejbUtils.buildOpenSPCoopOK(this.requestInfo.getIntegrationRequestMessageType(), this.idMessageRequest),this.richiestaDelegata,this.pd,this.sa);
					}
					
					
				}
			}








			/* ------- Gestione punto 2 o 3 (MessaggioErroreProtocollo e MessaggioProtocollo) -------- */
			else if(presenzaRispostaProtocollo){
				if(isMessaggioErroreProtocollo){	
					// 2) Ricevuto Messaggio Errore Protocollo da spedire al modulo Sbustamento.
					this.msgDiag.logPersonalizzato("rispostaRicevuta.messaggioErrore");
				}else{   
					// 3) Ricevuto Messaggio Protocollo (Puo' contenere un riscontro o qualcos'altro...) 
					this.msgDiag.logPersonalizzato("rispostaRicevuta.messaggio");
				}


				// Consegna al modulo opportuno
				if(this.functionAsRouter){ 

					this.msgDiag.mediumDebug("Gestione punto 2/3 (MessaggioErroreProtocollo e MessaggioProtocollo) [router]...");

					// Scenario Sincrono
					if( (org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.SINCRONO.equals(this.bustaRichiesta.getProfiloDiCollaborazione())) &&
							(!validatoreErrori.isBustaErrore(this.bustaRichiesta,this.requestMessageTrasformato,this.pValidazioneErrori)) ){
						this.msgDiag.mediumDebug("Gestione punto 2 (MessaggioErroreProtocollo e MessaggioProtocollo) [router] (sincrono)...");
						this.ejbUtils.sendBustaRisposta(this.richiestaDelegata.getIdModuloInAttesa(),bustaRisposta,null,
								this.idCorrelazioneApplicativa,this.idCorrelazioneApplicativaRisposta,this.servizioApplicativoFruitore);
					}
					// Scenario NotSincrono
					else{
						this.msgDiag.mediumDebug("Gestione punto 2 (MessaggioErroreProtocollo e MessaggioProtocollo) [router] (nonSincrono)...");
						this.ejbUtils.sendBustaRisposta(this.richiestaDelegata.getIdModuloInAttesa(),bustaRisposta,null,
								this.idCorrelazioneApplicativa,this.idCorrelazioneApplicativaRisposta,this.servizioApplicativoFruitore);
					}

				}

				else{	

					this.msgDiag.mediumDebug("Gestione punto 2/3 (MessaggioErroreProtocollo e MessaggioProtocollo) [pdd]...");

					// Rollback se la gestione dell'errore mi indica di effettuarlo (se non attesa risposta).
					if(this.sendRispostaApplicativa==false && this.errorConsegna && this.gestioneBusteNonRiscontrateAttive==false){

						// Comunicazione Asincrona (OneWay e Asincroni senza ricevuta applicativa)
						StringBuilder bfDettagliEccezione = new StringBuilder();
						if(bustaRisposta.sizeListaEccezioni()>0)
							this.msgDiag.addKeyword(CostantiPdD.KEY_ECCEZIONI, bustaRisposta.toStringListaEccezioni(this.protocolFactory));
						else if(dettaglioEccezione!=null && dettaglioEccezione.getExceptions()!=null){
							for(int i=0; i<dettaglioEccezione.getExceptions().sizeExceptionList(); i++){
								if(i>0)
									bfDettagliEccezione.append("\n");
								org.openspcoop2.core.eccezione.details.Eccezione eccDettaglio = dettaglioEccezione.getExceptions().getException(i);
								if(eccDettaglio.getSeverity()!=null && eccDettaglio.getContext()!=null){
									bfDettagliEccezione.append("Eccezione ("+eccDettaglio.getType()+") "+eccDettaglio.getSeverity()+" con codice ["+eccDettaglio.getCode()+"] - "
											+eccDettaglio.getContext() +", descrizione errore: "+eccDettaglio.getDescription());
								}else{
									bfDettagliEccezione.append("Eccezione ("+eccDettaglio.getType()+") con codice ["+eccDettaglio.getCode()+"], descrizione errore: "+eccDettaglio.getDescription());
								}
							}
							if(dettaglioEccezione.getDetails()!=null){
								for(int i=0; i<dettaglioEccezione.getDetails().sizeDetailList(); i++){
									bfDettagliEccezione.append("\n");
									org.openspcoop2.core.eccezione.details.Dettaglio eccDettaglio = dettaglioEccezione.getDetails().getDetail(i);
									bfDettagliEccezione.append("Dettaglio ["+eccDettaglio.getType()+"]: "+eccDettaglio.getBase());
								}
							}
							this.msgDiag.addKeyword(CostantiPdD.KEY_ECCEZIONI, bfDettagliEccezione.toString());
						}
						else{
							this.msgDiag.addKeyword(CostantiPdD.KEY_ECCEZIONI, this.motivoErroreConsegna);
						}
						this.msgDiag.logPersonalizzato("ricezioneMessaggioErrore.rollback");
						
						String motivoErroreRiconsegna = null;
						if(bustaRisposta.sizeListaEccezioni()>0)
							motivoErroreRiconsegna = "Consegna ["+this.tipoConnector+"] con errore: "+bustaRisposta.toStringListaEccezioni(this.protocolFactory);
						else if(dettaglioEccezione!=null){
							motivoErroreRiconsegna = "Consegna ["+this.tipoConnector+"] con errore: "+bfDettagliEccezione.toString();
						}
						else{
							motivoErroreRiconsegna = "Consegna ["+this.tipoConnector+"] con errore: "+this.motivoErroreConsegna;
						}
						
						if(this.riconsegna){
							this.ejbUtils.rollbackMessage(motivoErroreRiconsegna,this.dataRiconsegna , esito);
							esito.setStatoInvocazione(EsitoLib.ERRORE_NON_GESTITO,motivoErroreRiconsegna);
							esito.setEsitoInvocazione(false);
						}else{
							//TODO CHECKME relaseoutbox con il messaggio di risposta o di richiesta ?
							this.ejbUtils.releaseOutboxMessage(false);
							esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,motivoErroreRiconsegna);
							esito.setEsitoInvocazione(true);
						}

						if(this.msgResponse!=null){
							this.msgResponse.deleteMessageFromFileSystem(); // elimino eventuale risposta salvata su fileSystem
						}
						this.openspcoopstate.releaseResource();
						return esito;

					}




					//	Verifica xsd  (Se siamo in un caso di risposta applicativa presente)
					if(this.sendRispostaApplicativa && isMessaggioErroreProtocollo==false && 
							erroriValidazione.size()<=0 &&
							erroriProcessamento.size()<=0){
						this.msgDiag.mediumDebug("Controllo validazione xsd abilitata/disabilitata...");
						ValidazioneContenutiApplicativi validazioneContenutoApplicativoApplicativo =  null;
						List<Proprieta> proprietaValidazioneContenutoApplicativoApplicativo = null;
						try{ 
							validazioneContenutoApplicativoApplicativo = this.configurazionePdDManager.getTipoValidazioneContenutoApplicativo(this.pd,this.implementazionePdDDestinatario, false);
							proprietaValidazioneContenutoApplicativoApplicativo = this.pd.getProprietaList();
						}catch(Exception ex){
							this.msgDiag.logErroreGenerico(ex,"getTipoValidazioneContenutoApplicativo(pd)");
							if(this.msgResponse!=null){
								this.msgResponse.deleteMessageFromFileSystem(); // elimino eventuale risposta salvata su fileSystem
							}
							OpenSPCoop2Message responseMessageError = 
									this.generatoreErrore.build(this.pddContext,IntegrationFunctionError.INTERNAL_RESPONSE_ERROR,
											ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
												get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE),ex,
													(this.responseMessage!=null ? this.responseMessage.getParseException() : null));
							this.ejbUtils.sendRispostaApplicativaErrore(responseMessageError,this.richiestaDelegata,this.rollbackRichiesta,this.pd,this.sa);
							this.openspcoopstate.releaseResource();
							esito.setEsitoInvocazione(true);
							esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
									"getTipoValidazioneContenutoApplicativo(pd)");
							return esito;
						}
						if(validazioneContenutoApplicativoApplicativo!=null && validazioneContenutoApplicativoApplicativo.getTipo()!=null){
							String tipo = ValidatoreMessaggiApplicativi.getTipo(validazioneContenutoApplicativoApplicativo);
							//this.msgContext.getIntegrazione().setTipoValidazioneContenuti(tipo);
							this.msgDiag.addKeyword(CostantiPdD.KEY_TIPO_VALIDAZIONE_CONTENUTI, tipo);
							this.msgDiag.addKeyword(CostantiPdD.KEY_DETAILS_VALIDAZIONE_CONTENUTI,"");
						}
						if(CostantiConfigurazione.STATO_CON_WARNING_ABILITATO.equals(validazioneContenutoApplicativoApplicativo.getStato())||
								CostantiConfigurazione.STATO_CON_WARNING_WARNING_ONLY.equals(validazioneContenutoApplicativoApplicativo.getStato())){

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
									//OpenSPCoop2RestMessage<?> restMsg = responseMessage.castAsRest();
									//hasContent = restMsg.hasContent();
									hasContent = true; // devo controllare gli header etc...
									//isFault = restMsg.isProblemDetailsForHttpApis_RFC7807() || MessageRole.FAULT.equals(responseMessage.getMessageRole());
									// fix: i problem detail devono far parte dell'interfaccia openapi
									isFault = MessageRole.FAULT.equals(this.responseMessage.getMessageRole());
								}
								
								boolean validazioneAbilitata = true;
								if(ServiceBinding.SOAP.equals(this.responseMessage.getServiceBinding())){
									validazioneAbilitata = hasContent && (isFault==false);
								}
								else {
									validazioneAbilitata = ValidatoreMessaggiApplicativiRest.isValidazioneAbilitata(proprietaValidazioneContenutoApplicativoApplicativo, this.responseMessage, this.codiceRitornato);
								}
								if( validazioneAbilitata ){
									
									this.msgDiag.logPersonalizzato("validazioneContenutiApplicativiRispostaInCorso");
									
									boolean readInterface = CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_INTERFACE.equals(validazioneContenutoApplicativoApplicativo.getTipo());
											
									if(ServiceBinding.SOAP.equals(this.responseMessage.getServiceBinding())){
									
										// Accept mtom message
										List<MtomXomReference> xomReferences = null;
										if(StatoFunzionalita.ABILITATO.equals(validazioneContenutoApplicativoApplicativo.getAcceptMtomMessage())){
											this.msgDiag.mediumDebug("Validazione xsd della risposta (mtomFastUnpackagingForXSDConformance)...");
											if(ServiceBinding.SOAP.equals(this.responseMessage.getServiceBinding())==false){
												throw new Exception("Funzionalita 'AcceptMtomMessage' valida solamente per Service Binding SOAP");
											}
											xomReferences = this.responseMessage.castAsSoap().mtomFastUnpackagingForXSDConformance();
										}
										
										// Init Validatore
										//System.out.println("MESSAGGIO PRIMA DI VALIDARE: "+requestMessage.getAsString(responseMessage.getSOAPPart().getEnvelope(), false));
										this.msgDiag.mediumDebug("Validazione della risposta (initValidator)...");
										ValidatoreMessaggiApplicativi validatoreMessaggiApplicativi = 
											new ValidatoreMessaggiApplicativi(this.registroServiziManager,this.richiestaDelegata.getIdServizio(),
													this.responseMessage,readInterface,
													this.propertiesReader.isValidazioneContenutiApplicativi_rpcLiteral_xsiType_gestione(),
													proprietaValidazioneContenutoApplicativoApplicativo,
													this.pddContext);
	
										// Validazione WSDL 
										if( CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_INTERFACE.equals(validazioneContenutoApplicativoApplicativo.getTipo()) 
												||
												CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_OPENSPCOOP.equals(validazioneContenutoApplicativoApplicativo.getTipo())
										){
											this.msgDiag.mediumDebug("Validazione wsdl della risposta ...");
											validatoreMessaggiApplicativi.validateWithWsdlLogicoImplementativo(false);
										}
										
										// Validazione XSD
										this.msgDiag.mediumDebug("Validazione xsd della risposta (validazione)...");
										validatoreMessaggiApplicativi.validateWithWsdlDefinitorio(false);
										
										// Validazione WSDL (Restore Original Document)
										if (CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_INTERFACE.equals(validazioneContenutoApplicativoApplicativo.getTipo())
											|| CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_OPENSPCOOP.equals(validazioneContenutoApplicativoApplicativo.getTipo())) {
											if(this.propertiesReader.isValidazioneContenutiApplicativi_rpcLiteral_xsiType_gestione() &&
													this.propertiesReader.isValidazioneContenutiApplicativi_rpcLiteral_xsiType_ripulituraDopoValidazione()){
												this.msgDiag.mediumDebug("Ripristino elementi modificati per supportare validazione wsdl della risposta ...");
												validatoreMessaggiApplicativi.restoreOriginalDocument(false);
											}
										}
										
										// Ripristino struttura messaggio con xom
										if(xomReferences!=null && xomReferences.size()>0){
											this.msgDiag.mediumDebug("Validazione xsd della risposta (mtomRestoreAfterXSDConformance)...");
											if(ServiceBinding.SOAP.equals(this.responseMessage.getServiceBinding())==false){
												throw new Exception("Funzionalita 'AcceptMtomMessage' valida solamente per Service Binding SOAP");
											}
											this.responseMessage.castAsSoap().mtomRestoreAfterXSDConformance(xomReferences);
										}
										
									}
									else {
										
										// Init Validatore
										this.msgDiag.mediumDebug("Validazione della risposta (initValidator)...");
										ValidatoreMessaggiApplicativiRest validatoreMessaggiApplicativi = 
											new ValidatoreMessaggiApplicativiRest(this.registroServiziManager, this.richiestaDelegata.getIdServizio(), this.responseMessage, readInterface, proprietaValidazioneContenutoApplicativoApplicativo, 
													this.protocolFactory, this.pddContext);
										
										if(CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_XSD.equals(validazioneContenutoApplicativoApplicativo.getTipo()) &&
												this.responseMessage.castAsRest().hasContent()) {
											
											// Validazione XSD
											this.msgDiag.mediumDebug("Validazione xsd della risposta ...");
											validatoreMessaggiApplicativi.validateWithSchemiXSD(false);
											
										}
										else {
											
											// Validazione Interface
											validatoreMessaggiApplicativi.validateResponseWithInterface(this.requestMessagePrimaTrasformazione, false);
											
										}
										
									}
									
									this.msgDiag.logPersonalizzato("validazioneContenutiApplicativiRispostaEffettuata");
								}
								else{
									if(hasContent==false){
										this.msgDiag.addKeyword(CostantiPdD.KEY_DETAILS_VALIDAZIONE_CONTENUTI,CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_VALIDAZIONE_SOAP_BODY_NON_PRESENTE);
										this.msgDiag.logPersonalizzato("validazioneContenutiApplicativiRispostaDisabilitata");
									}
									else if (isFault){
										this.msgDiag.addKeyword(CostantiPdD.KEY_DETAILS_VALIDAZIONE_CONTENUTI,CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_VALIDAZIONE_SOAP_FAULT_PRESENTE);
										this.msgDiag.logPersonalizzato("validazioneContenutiApplicativiRispostaDisabilitata");
									}
								}

							}catch(ValidatoreMessaggiApplicativiException ex){
								this.msgDiag.addKeywordErroreProcessamento(ex);
								this.log.error("[ValidazioneContenutiApplicativi Risposta] "+ex.getMessage(),ex);
								if (CostantiConfigurazione.STATO_CON_WARNING_WARNING_ONLY.equals(validazioneContenutoApplicativoApplicativo.getStato())) {
									this.msgDiag.logPersonalizzato("validazioneContenutiApplicativiRispostaNonRiuscita.warningOnly");
								}
								else {
									this.msgDiag.logPersonalizzato("validazioneContenutiApplicativiRispostaNonRiuscita");
								}
								if(CostantiConfigurazione.STATO_CON_WARNING_WARNING_ONLY.equals(validazioneContenutoApplicativoApplicativo.getStato()) == false){
									
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
									
									// validazione abilitata
									if(this.msgResponse!=null){
										this.msgResponse.deleteMessageFromFileSystem(); // elimino eventuale risposta salvata su fileSystem
									}
									OpenSPCoop2Message responseMessageError = 
											this.generatoreErrore.build(this.pddContext,integrationFunctionError,
													ex.getErrore(),ex,
														(this.responseMessage!=null ? this.responseMessage.getParseException() : null));
									this.ejbUtils.sendRispostaApplicativaErrore(responseMessageError,this.richiestaDelegata,this.rollbackRichiesta,this.pd,this.sa);
									this.openspcoopstate.releaseResource();
									esito.setEsitoInvocazione(true);
									esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
											this.msgDiag.getMessaggio_replaceKeywords("validazioneContenutiApplicativiNonRiuscita"));
									return esito;
								}
							}catch(Exception ex){
								this.msgDiag.addKeywordErroreProcessamento(ex);
								this.log.error("Riscontrato errore durante la validazione xsd della risposta applicativa",ex);
								if (CostantiConfigurazione.STATO_CON_WARNING_WARNING_ONLY.equals(validazioneContenutoApplicativoApplicativo.getStato())) {
									this.msgDiag.logPersonalizzato("validazioneContenutiApplicativiRispostaNonRiuscita.warningOnly");
								}
								else {
									this.msgDiag.logPersonalizzato("validazioneContenutiApplicativiRispostaNonRiuscita");
								}
								if(CostantiConfigurazione.STATO_CON_WARNING_WARNING_ONLY.equals(validazioneContenutoApplicativoApplicativo.getStato()) == false){
									
									this.pddContext.addObject(org.openspcoop2.core.constants.Costanti.ERRORE_VALIDAZIONE_RISPOSTA, "true");
									
									// validazione abilitata
									if(this.msgResponse!=null){
										this.msgResponse.deleteMessageFromFileSystem(); // elimino eventuale risposta salvata su fileSystem
									}
									OpenSPCoop2Message responseMessageError = 
											this.generatoreErrore.build(this.pddContext,IntegrationFunctionError.INTERNAL_RESPONSE_ERROR,
													ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
														get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_531_VALIDAZIONE_TRAMITE_INTERFACCIA_FALLITA),ex,
															(this.responseMessage!=null ? this.responseMessage.getParseException() : null));
									this.ejbUtils.sendRispostaApplicativaErrore(responseMessageError,this.richiestaDelegata,this.rollbackRichiesta,this.pd,this.sa);
									this.openspcoopstate.releaseResource();
									esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
											this.msgDiag.getMessaggio_replaceKeywords("validazioneContenutiApplicativiNonRiuscita"));
									esito.setEsitoInvocazione(true);
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
					}





					//msgResponse.aggiornaInformazioni(bustaRisposta.getServizio(),bustaRisposta.getAzione());
					this.msgDiag.mediumDebug("Aggiorna proprietario messaggio risposta...");
					this.msgResponse.aggiornaProprietarioMessaggio(SbustamentoRisposte.ID_MODULO);
					((OpenSPCoopState)this.openspcoopstate).setMessageLib(sbustamentoRisposteMSG);
					
					if (this.openspcoopstate instanceof OpenSPCoopStateful) {

						this.msgDiag.mediumDebug("Spedizione messaggio verso SbustamentoRisposte...");
						try{
							this.ejbUtils.getNodeSender(this.propertiesReader, this.log).send(sbustamentoRisposteMSG, SbustamentoRisposte.ID_MODULO, this.msgDiag, 
									this.identitaPdD,InoltroBuste.ID_MODULO, this.idMessageRequest,this.msgResponse);
						} catch (Exception e) {
							this.log.error("Spedizione->SbustamentoRisposte non riuscita",e);
							if(this.msgResponse!=null){
								this.msgResponse.deleteMessageFromFileSystem(); // elimino eventuale risposta salvata su fileSystem
							}
							this.msgDiag.logErroreGenerico(e,"GenericLib.nodeSender.send(SbustamentoRisposte)");
							if(this.sendRispostaApplicativa){
								OpenSPCoop2Message responseMessageError = 
										this.generatoreErrore.build(this.pddContext,IntegrationFunctionError.INTERNAL_RESPONSE_ERROR,
												ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
													get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_512_SEND),e,
														(this.responseMessage!=null ? this.responseMessage.getParseException() : null));
								this.ejbUtils.sendRispostaApplicativaErrore(responseMessageError,this.richiestaDelegata,this.rollbackRichiesta,this.pd,this.sa);
								esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
										"Spedizione->SbustamentoRisposte non riuscita");
								esito.setEsitoInvocazione(true);
							}else{
								//Se Non e' attivo una gestione dei riscontri, faccio il rollback sulla coda.
								// Altrimenti verra attivato la gestione dei riscontri che riprovera' dopo un tot.
								if(this.gestioneBusteNonRiscontrateAttive==false){
									this.ejbUtils.rollbackMessage("Spedizione risposta al modulo di Sbustamento non riuscito", esito);
									esito.setStatoInvocazione(EsitoLib.ERRORE_NON_GESTITO,
											"Spedizione->SbustamentoRisposte non riuscita");
									esito.setEsitoInvocazione(false);
								}else{
									this.ejbUtils.updateErroreProcessamentoMessage("Spedizione risposta al modulo di Sbustamento non riuscito", esito);
									esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
											"Spedizione->SbustamentoRisposte non riuscita");
									esito.setEsitoInvocazione(true);
								}
							}
							this.openspcoopstate.releaseResource();
							return esito;
						}
					}		

				}
			} 







			/* ------- Gestione punto 4 (SoapFault senza una busta associata) -------- */
			else{

				// Nei casi di soapFault con una busta non rientreremo in questo caso.
				// Nel protocollo trasparente si rientra cmq di sopra poichè viene sempre ritornato 'true' in presenzaBustaRisposta
				this.pddContext.addObject(org.openspcoop2.core.constants.Costanti.ERRORE_SOAP_FAULT_SERVER, true);
				
				// Fault registrato prima comunque
//				if(fault!=null && faultLogged==false){	
//					this.msgDiag.addKeyword(CostantiPdD.KEY_SOAP_FAULT, SoapUtils.toString(fault));
//					this.msgDiag.logPersonalizzato("ricezioneSoapFault");
//					faultLogged = true;
//				}
				if(this.functionAsRouter){
					this.msgDiag.mediumDebug("Gestione punto 4 (SoapFault senza una busta associata) [router] ...");

					if(this.soapFault!=null || this.restProblem!=null){
						if(this.soapFault!=null && this.enrichSoapFaultPdD){
							IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.BAD_RESPONSE;
							this.generatoreErrore.getErroreApplicativoBuilderForAddDetailInSoapFault(this.pddContext, this.responseMessage.getMessageType(),integrationFunctionError).
								insertRoutingErrorInSOAPFault(this.identitaPdD,InoltroBuste.ID_MODULO,
									this.msgDiag.getMessaggio_replaceKeywords("ricezioneSoapFault"), this.responseMessage);
						}
						
						if( (org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.SINCRONO.equals(this.bustaRichiesta.getProfiloDiCollaborazione())==false) &&
								this.newConnectionForResponse ) {
							// ritorno l'errore su di una nuova connessione.
							this.ejbUtils.sendAsRispostaBustaErroreProcessamento(this.richiestaDelegata.getIdModuloInAttesa(), this.bustaRichiesta, 
									ErroriIntegrazione.ERRORE_518_RISPOSTA_RICHIESTA_RITORNATA_COME_FAULT.
										get518_RispostaRichiestaRitornataComeFault(this.bustaRichiesta.getTipoDestinatario()+this.bustaRichiesta.getDestinatario()),
									this.idCorrelazioneApplicativa,this.idCorrelazioneApplicativaRisposta,this.servizioApplicativoFruitore,null,
									(this.responseMessage!=null ? this.responseMessage.getParseException() : null));
						}else{
							this.ejbUtils.sendSOAPFault(this.richiestaDelegata.getIdModuloInAttesa(), this.responseMessage);
						}
						
					}else{
					
						// Scenario RoutingSincrono
						if( (org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.SINCRONO.equals(this.bustaRichiesta.getProfiloDiCollaborazione())) &&
								(!validatoreErrori.isBustaErrore(this.bustaRichiesta,this.requestMessageTrasformato,this.pValidazioneErrori)) ){
							this.msgDiag.mediumDebug("Gestione punto 4 (SoapFault senza una busta associata) [router] (sincrono)...");
							this.ejbUtils.sendSbloccoRicezioneBuste(this.richiestaDelegata.getIdModuloInAttesa());
						}
	
						// Scenario RoutingNotSincrono
						else{
							if(this.newConnectionForResponse==false){
								this.msgDiag.logPersonalizzato("ricezioneSoapMessage.headerProtocolloNonPresente");
								this.ejbUtils.sendSbloccoRicezioneBuste(this.richiestaDelegata.getIdModuloInAttesa());
							}
						}
					}

				}
				// Scenario Sincrono
				else if(this.sendRispostaApplicativa){

					this.msgDiag.mediumDebug("Gestione punto 4 (SoapFault senza una busta associata) [pdd] ...");

					if(this.msgResponse!=null){
						this.msgResponse.deleteMessageFromFileSystem(); // elimino eventuale risposta salvata su fileSystem
					}
					boolean fineGestione = true;
					if(this.soapFault==null && this.restProblem==null){
						
						if(Costanti.SCENARIO_ONEWAY_INVOCAZIONE_SERVIZIO.equals(this.richiestaDelegata.getScenario())){
							// Altrimenti entro qua per via del sendRispostaApplicativa=true a caso del oneway stateless
							
							boolean hasContent = false;
							if(ServiceBinding.SOAP.equals(this.responseMessage.getServiceBinding())){
								OpenSPCoop2SoapMessage soapMsg = this.responseMessage.castAsSoap();
								hasContent = soapMsg.getSOAPBody()!=null;
								if(hasContent){
									hasContent = SoapUtils.getFirstNotEmptyChildNode(soapMsg.getFactory(), soapMsg.getSOAPBody(), false)!=null;
								}
							}
							else{
								OpenSPCoop2RestMessage<?> restMsg = this.responseMessage.castAsRest();
								hasContent = restMsg.hasContent();
							}
							
							if(hasContent){
								this.msgDiag.logPersonalizzato("ricezioneSoapMessage.headerProtocolloNonPresente");
							}
							this.msgDiag.mediumDebug("Invio messaggio 'OK' al modulo di RicezioneContenutiApplicativi (Carico HTTP Reply vuoto)...");
							if(this.protocolManager.isHttpEmptyResponseOneWay())
								this.msgResponse = this.ejbUtils.sendRispostaApplicativaOK(MessageUtilities.buildEmptyMessage(OpenSPCoop2MessageFactory.getDefaultMessageFactory(),
										this.requestInfo.getIntegrationRequestMessageType(),MessageRole.RESPONSE),this.richiestaDelegata,this.pd,this.sa);
							else
								this.msgResponse = this.ejbUtils.sendRispostaApplicativaOK(this.ejbUtils.buildOpenSPCoopOK(this.requestInfo.getIntegrationRequestMessageType(), this.idMessageRequest),this.richiestaDelegata,this.pd,this.sa);
							fineGestione = false;
						}else{
							// Se non ho un fault, e non sono in profilo oneway stateless segnalo l'anomalia
							this.msgDiag.logPersonalizzato("ricezioneSoapMessage.headerProtocolloNonPresente");
							OpenSPCoop2Message responseMessageError = this.generatoreErrore.build(this.pddContext,IntegrationFunctionError.BAD_RESPONSE,
									ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
											get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_528_RISPOSTA_RICHIESTA_NON_VALIDA),
											null,
											(this.responseMessage!=null ? this.responseMessage.getParseException() : null));
							this.ejbUtils.sendRispostaApplicativaErrore(responseMessageError,this.richiestaDelegata,this.rollbackRichiesta,this.pd,this.sa);
						 }
					}
					else{
						if(this.enrichSoapFaultPdD){
							IntegrationFunctionError integrationFunctionError = getIntegrationFunctionErroreConnectionError();
							this.generatoreErrore.getErroreApplicativoBuilderForAddDetailInSoapFault(this.pddContext, this.responseMessage.getMessageType(), integrationFunctionError).
								insertInSOAPFault(ErroriIntegrazione.ERRORE_516_CONNETTORE_UTILIZZO_CON_ERRORE.
										get516_PortaDiDominioNonDisponibile(this.bustaRichiesta.getTipoDestinatario()+"-"+this.bustaRichiesta.getDestinatario()), 
										this.responseMessage);
						}
						this.ejbUtils.sendRispostaApplicativaErrore(this.responseMessage,this.richiestaDelegata,this.rollbackRichiesta,this.pd,this.sa);
					}
					
					if(fineGestione){
						this.openspcoopstate.releaseResource();
						esito.setEsitoInvocazione(true);
						esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
								"Gestione punto 4 (SoapFault senza una busta associata) [pdd]");
						return esito;
					}
				}
				// Scenario NotSincrono
				// OK
			}







			Utilities.printFreeMemory("InoltroBuste - Elimino utilizzo busta dalla PdD ...");






			/* ---------- Gestione Transazione Modulo ---------------- */
			this.msgDiag.mediumDebug("Elimino utilizzo busta dalla PdD ...");
			this.repositoryBuste.eliminaUtilizzoPdDFromOutBox(this.idMessageRequest,this.statelessAsincrono);
			
			if (this.oneWayVersione11) {
				this.repositoryBuste.eliminaBustaStatelessFromOutBox(this.idMessageRequest);
			}
			// Se e' indicato di non eliminare il messaggio di RICHIESTA
			// non deve essere fatto poiche' sara' riutilizzato dal Thread che re-invia la busta
			if(this.gestioneBusteNonRiscontrateAttive==false){
				this.msgDiag.mediumDebug("Aggiorno proprietario messaggio richiesta ...");
				this.msgRequest.aggiornaProprietarioMessaggio(TimerGestoreMessaggi.ID_MODULO);
			}
			else {
				if (this.oneWayVersione11 && sbustamentoRisposteMSG!=null) {
					// Se sono in onewayVersione11, ho diritto ad utilizzare la connessione.
					// Non devo farla utilizzare pero' per SbustamentoRisposte
					((OpenSPCoopStateless)this.openspcoopstate).setUseConnection(false); 
					EsitoLib esitoLib = null;
					try{
						SbustamentoRisposte sbustamentoLib = new SbustamentoRisposte(this.log);
						esitoLib = sbustamentoLib.onMessage(this.openspcoopstate);
					}finally{
						((OpenSPCoopStateless)this.openspcoopstate).setUseConnection(true);
					}
					if ( ! esitoLib.isEsitoInvocazione() ) 
						throw new Exception(InoltroBuste.ID_MODULO + " chiamata alla libreria SbustamentoRisposte fallita " + esito.getStatoInvocazione());
					else{
						this.msgDiag.mediumDebug("Aggiorno proprietario messaggio richiesta ...");
						this.msgRequest.aggiornaProprietarioMessaggio(TimerGestoreMessaggi.ID_MODULO);
					}					
				}
			}

			// messaggio finale
			this.msgDiag.addKeyword(CostantiPdD.KEY_TIPO_CONNETTORE, this.tipoConnector);
			this.msgDiag.logPersonalizzato("gestioneConsegnaTerminata");

			// Commit JDBC
			this.msgDiag.mediumDebug("Commit delle operazioni per la gestione della richiesta...");
			this.openspcoopstate.commit();

			// Rilascio connessione al DB
			this.msgDiag.mediumDebug("Rilascio connessione al database...");
			this.openspcoopstate.releaseResource();

			// Aggiornamento cache messaggio
			if(this.msgRequest!=null)
				this.msgRequest.addMessaggiIntoCache_readFromTable(InoltroBuste.ID_MODULO, "richiesta");
			if(this.msgResponse!=null)
				this.msgResponse.addMessaggiIntoCache_readFromTable(InoltroBuste.ID_MODULO, "risposta");

			//	Aggiornamento cache proprietario messaggio
			if(this.msgRequest!=null)
				this.msgRequest.addProprietariIntoCache_readFromTable(InoltroBuste.ID_MODULO, "richiesta",null,this.functionAsRouter);
			if(this.msgResponse!=null)
				this.msgResponse.addProprietariIntoCache_readFromTable(InoltroBuste.ID_MODULO, "risposta",this.idMessageRequest,this.functionAsRouter);


			this.msgDiag.mediumDebug("Lavoro Terminato.");
			esito.setEsitoInvocazione(true);	
			esito.setStatoInvocazione(EsitoLib.OK, null);
			return esito; 


		}catch(Throwable e){
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

				if( this.sendRispostaApplicativa ){
					OpenSPCoop2Message responseMessageError = 
							this.generatoreErrore.build(this.pddContext,AbstractErrorGenerator.getIntegrationInternalError(this.pddContext),
									ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.getErroreIntegrazione(),e,
										(this.responseMessage!=null ? this.responseMessage.getParseException() : null));
					try{
						this.ejbUtils.sendRispostaApplicativaErrore(responseMessageError,this.richiestaDelegata,this.rollbackRichiesta,this.pd,this.sa);
						esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO, "ErroreGenerale");
						esito.setEsitoInvocazione(true);
					}catch(Exception er){
						this.msgDiag.logErroreGenerico(er,"this.ejbUtils.sendErroreGenerale(profiloConRisposta)");
						this.ejbUtils.rollbackMessage("Spedizione Errore al Mittente durante una richiesta sincrona non riuscita", esito);
						esito.setStatoInvocazioneErroreNonGestito(er);
						esito.setEsitoInvocazione(false);
					}
				}else{
					this.ejbUtils.rollbackMessage("ErroreGenerale:"+e.getMessage(), esito);
					esito.setStatoInvocazioneErroreNonGestito(e);
					esito.setEsitoInvocazione(false);
				}
				this.openspcoopstate.releaseResource();
				return esito;
			}
		}finally{
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
			// *** GB ***
			if(this.validatore!=null){
				if(this.validatore.getValidatoreSintattico()!=null){
					this.validatore.getValidatoreSintattico().setHeaderSOAP(null);
				}
				this.validatore.setValidatoreSintattico(null);
			}
			this.validatore = null;
			this.headerProtocolloRisposta = null;
			// *** GB ***
		}
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

			if( this.sendRispostaApplicativa ){
				OpenSPCoop2Message responseMessageError = 
						this.generatoreErrore.build(this.pddContext,AbstractErrorGenerator.getIntegrationInternalError(this.pddContext),
								ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.getErroreIntegrazione(),e,
									(this.responseMessage!=null ? this.responseMessage.getParseException() : null));
				try{
					this.ejbUtils.sendRispostaApplicativaErrore(responseMessageError,this.richiestaDelegata,this.rollbackRichiesta,this.pd,this.sa);
					esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO, "ErroreGenerale");
					esito.setEsitoInvocazione(true);
				}catch(Exception er){
					this.msgDiag.logErroreGenerico(er,"this.ejbUtils.sendErroreGenerale(profiloConRisposta)");
					this.ejbUtils.rollbackMessage("Spedizione Errore al Mittente durante una richiesta sincrona non riuscita", esito);
					esito.setStatoInvocazioneErroreNonGestito(er);
					esito.setEsitoInvocazione(false);
				}
			}else{
				this.ejbUtils.rollbackMessage("ErroreGenerale:"+e.getMessage(), esito);
				esito.setStatoInvocazioneErroreNonGestito(e);
				esito.setEsitoInvocazione(false);
			}
			this.openspcoopstate.releaseResource();
			return esito;
		}
	}
	
	private EsitoElaborazioneMessaggioTracciato gestioneTracciamentoFineConnettore(StringBuilder bfMsgErroreSituazioneAnomale) throws Exception {
		
		String msgErroreSituazioneAnomale = null;

		// Tracciamento effettuato:

		// - SEMPRE se vi e' da tornare una risposta applicativa (sendRispostaApplicativa=true)

		// - SEMPRE se si ha e' un router: functionAsRouter

		// - SEMPPRE se non vi sono errori (errorConsegna=false)
		
		// Settings msgDiag
		if(this.errorConsegna){
			// Una busta contiene per forza di cose un SOAPFault
			// Una busta la si riconosce dal motivo di errore.
			if(this.soapFault!=null && this.soapFault.getFaultString()!=null && 
					(this.soapFault.getFaultString().equals(this.traduttore.toString(MessaggiFaultErroreCooperazione.FAULT_STRING_VALIDAZIONE)) || this.soapFault.getFaultString().equals(this.traduttore.toString(MessaggiFaultErroreCooperazione.FAULT_STRING_PROCESSAMENTO)))){
				this.msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_CONSEGNA, this.soapFault.getFaultString());
				this.msgDiag.logPersonalizzato("inoltroConErrore");
			}else{
				this.msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_CONSEGNA, this.motivoErroreConsegna);
				this.msgDiag.logPersonalizzato("inoltroConErrore");
				
				// Controllo Situazione Anomala ISSUE OP-7
				if(this.responseMessage!=null && ServiceBinding.SOAP.equals(this.responseMessage.getServiceBinding())){
					OpenSPCoop2SoapMessage soapMessageResponse = this.responseMessage.castAsSoap();
					if(soapMessageResponse.getSOAPPart()!=null && 
							soapMessageResponse.getSOAPPart().getEnvelope()!=null &&
							(soapMessageResponse.isSOAPBodyEmpty() || (!soapMessageResponse.hasSOAPFault()))
							){
						this.msgDiag.logPersonalizzato("comportamentoAnomalo.erroreConsegna.ricezioneMessaggioDiversoFault");
						if(this.isBlockedTransaction_responseMessageWithTransportCodeError){
							msgErroreSituazioneAnomale = this.msgDiag.getMessaggio_replaceKeywords("comportamentoAnomalo.erroreConsegna.ricezioneMessaggioDiversoFault");
							this.log.error(msgErroreSituazioneAnomale);
							bfMsgErroreSituazioneAnomale.append(msgErroreSituazioneAnomale);
						}
					}
				}
			}
		}else{	
			if(this.soapFault!=null && this.soapFault.getFaultString()!=null && 
					(this.soapFault.getFaultString().equals(this.traduttore.toString(MessaggiFaultErroreCooperazione.FAULT_STRING_VALIDAZIONE))) ){
				this.msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_CONSEGNA, this.soapFault.getFaultString());
				this.msgDiag.logPersonalizzato("inoltroConErrore");
			}
			else{
				this.msgDiag.logPersonalizzato("inoltroEffettuato");
			}
		}
		
		// Se non vi e' da tornare una risposta applicativa
		// - Se vi e' una consegna con errore, e non vi e' un soap fault, non viene tracciato (verra' rispedito).
		// - Altrimenti viene tracciato anche se:
		//       - messaggio errore protocollo
		//       - messaggio protocollo con fault applicativo
		//       - FaultServer
		if( (this.errorConsegna==false) || (this.functionAsRouter) || (this.sendRispostaApplicativa) || 
				( this.soapFault!=null) ){
			this.msgDiag.mediumDebug("Tracciamento della richiesta...");
			EsitoElaborazioneMessaggioTracciato esitoTraccia = null;
			if(this.errorConsegna){
				esitoTraccia = EsitoElaborazioneMessaggioTracciato.getEsitoElaborazioneConErrore(this.msgDiag.getMessaggio_replaceKeywords("inoltroConErrore"));
			}else{
				if(this.soapFault!=null && this.soapFault.getFaultString()!=null && 
						(this.soapFault.getFaultString().equals(this.traduttore.toString(MessaggiFaultErroreCooperazione.FAULT_STRING_VALIDAZIONE))) ){
					esitoTraccia = EsitoElaborazioneMessaggioTracciato.getEsitoElaborazioneConErrore(this.msgDiag.getMessaggio_replaceKeywords("inoltroConErrore"));
				}else{
					esitoTraccia = EsitoElaborazioneMessaggioTracciato.getEsitoElaborazioneMessaggioInviato();
				}
			}
			return esitoTraccia;
		}
		
		return null;

	}
	
	private IntegrationFunctionError getIntegrationFunctionErroreConnectionError() {
		IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.SERVICE_UNAVAILABLE;
		
		String responseReadTimeout = null;
		if(this.pddContext!=null && this.pddContext.containsKey(TimeoutInputStream.ERROR_MSG_KEY)) {
			String timeoutMessage = PdDContext.getValue(TimeoutInputStream.ERROR_MSG_KEY, this.pddContext);
			if(timeoutMessage!=null && timeoutMessage.startsWith(CostantiPdD.PREFIX_TIMEOUT_RESPONSE)) {
				responseReadTimeout = timeoutMessage;
			}
		}
		if(responseReadTimeout!=null) {
			integrationFunctionError = IntegrationFunctionError.ENDPOINT_REQUEST_TIMED_OUT;
			this.pddContext.removeObject(TimeoutInputStream.ERROR_MSG_KEY);
			this.pddContext.removeObject(TimeoutInputStream.EXCEPTION_KEY);
		}
		else if(this.eccezioneProcessamentoConnettore!=null && this.motivoErroreConsegna!=null) {
			if(this.propertiesReader.isServiceUnavailable_ReadTimedOut(this.motivoErroreConsegna)){
				integrationFunctionError = IntegrationFunctionError.ENDPOINT_REQUEST_TIMED_OUT;
			}
		}
		return integrationFunctionError;
	}
}
