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

package org.openspcoop2.pdd.core.connettori;

import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.config.DumpConfigurazione;
import org.openspcoop2.core.config.InvocazioneCredenziali;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.Proprieta;
import org.openspcoop2.core.config.ResponseCachingConfigurazione;
import org.openspcoop2.core.config.ResponseCachingConfigurazioneControl;
import org.openspcoop2.core.config.ResponseCachingConfigurazioneRegola;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.constants.Costanti;
import org.openspcoop2.core.constants.CostantiConnettori;
import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.transazioni.constants.TipoMessaggio;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageProperties;
import org.openspcoop2.message.constants.MessageRole;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.message.exception.MessageException;
import org.openspcoop2.message.exception.ParseExceptionUtils;
import org.openspcoop2.pdd.config.ConfigurazionePdDManager;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.AbstractCore;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.controllo_traffico.PolicyTimeoutConfig;
import org.openspcoop2.pdd.core.controllo_traffico.SogliaDimensioneMessaggio;
import org.openspcoop2.pdd.core.dynamic.DynamicInfo;
import org.openspcoop2.pdd.core.dynamic.DynamicUtils;
import org.openspcoop2.pdd.core.handlers.GestoreHandlers;
import org.openspcoop2.pdd.core.handlers.HandlerException;
import org.openspcoop2.pdd.core.handlers.OutRequestContext;
import org.openspcoop2.pdd.core.handlers.PostOutRequestContext;
import org.openspcoop2.pdd.core.handlers.PreInResponseContext;
import org.openspcoop2.pdd.core.response_caching.GestoreCacheResponseCaching;
import org.openspcoop2.pdd.core.response_caching.ResponseCached;
import org.openspcoop2.pdd.core.token.EsitoNegoziazioneToken;
import org.openspcoop2.pdd.core.token.GestoreToken;
import org.openspcoop2.pdd.core.token.PolicyNegoziazioneToken;
import org.openspcoop2.pdd.core.transazioni.Transaction;
import org.openspcoop2.pdd.core.transazioni.TransactionContext;
import org.openspcoop2.pdd.logger.MsgDiagnostico;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.pdd.logger.transazioni.ConfigurazioneTracciamento;
import org.openspcoop2.pdd.mdb.ConsegnaContenutiApplicativi;
import org.openspcoop2.pdd.services.connector.AsyncResponseCallbackClientEvent;
import org.openspcoop2.pdd.services.connector.IAsyncResponseCallback;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.dump.DumpException;
import org.openspcoop2.protocol.sdk.dump.Messaggio;
import org.openspcoop2.protocol.sdk.state.IState;
import org.openspcoop2.protocol.sdk.state.RequestInfo;
import org.openspcoop2.protocol.utils.ModIUtils;
import org.openspcoop2.protocol.utils.ModIValidazioneSemanticaProfiloSicurezza;
import org.openspcoop2.utils.MapKey;
import org.openspcoop2.utils.NameValue;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.date.DateUtils;
import org.openspcoop2.utils.io.DumpByteArrayOutputStream;
import org.openspcoop2.utils.io.notifier.NotifierInputStreamParams;
import org.openspcoop2.utils.resources.Loader;
import org.openspcoop2.utils.transport.TransportUtils;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpUtilities;
import org.slf4j.Logger;

/**	
 * Contiene una classe base per i connettori
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class ConnettoreBase extends AbstractCore implements IConnettore {

	public static final MapKey<String> RESPONSE_FROM_CACHE = org.openspcoop2.utils.Map.newMapKey("RESPONSE_READED_FROM_CACHE");
	public static final String LOCATION_CACHED = "govway://responseCaching";
	public static final String LOCATION_CACHED_SEPARATOR_REQUEST_URL = "\n";
	
	/** Proprieta' del connettore */
	protected java.util.Map<String,String> properties;
	
	/** Tipo di Connettore */
	protected String tipoConnettore;
	
	/** Msg di richiesta */
	protected OpenSPCoop2Message requestMsg;	
	protected boolean isSoap = false;
	public boolean isSoap() {
		return this.isSoap;
	}
	protected boolean isRest = false;
	public boolean isRest() {
		return this.isRest;
	}
	
	/** Indicazione su di un eventuale sbustamento SOAP */
	protected boolean sbustamentoSoap;
	
	/** Proprieta' del trasporto che deve gestire il connettore */
	protected Map<String, List<String>> propertiesTrasporto;
	
	/** Proprieta' urlBased che deve gestire il connettore */
	protected Map<String, List<String>> propertiesUrlBased;
	
	/** Tipo di Autenticazione */
	/**private String tipoAutenticazione;*/
	/** Credenziali per l'autenticazione */
	protected InvocazioneCredenziali credenziali;
	
	/** Busta */
	protected Busta busta;
	
	/** Indicazione se siamo in modalita' debug */
	protected boolean debug = false;
	public boolean isDebug() {
		return this.debug;
	}

	/** OpenSPCoopProperties */
	protected OpenSPCoop2Properties openspcoopProperties = null;

	/** Identificativo */
	protected String idMessaggio;
	
	/** Logger */
	protected ConnettoreLogger logger = null;
	public ConnettoreLogger getLogger() {
		return this.logger;
	}

	/** Loader loader */
	protected Loader loader = null;
	
	/** Errore generato con un prefisso del connettore */
	protected boolean generateErrorWithConnectorPrefix = true;
	public boolean isGenerateErrorWithConnectorPrefix() {
		return this.generateErrorWithConnectorPrefix;
	}
	
	/** Identificativo Modulo */
	protected String idModulo = null;
	public String getIdModulo() {
		return this.idModulo;
	}
	
	/** motivo di un eventuale errore */
	protected String errore = null;
	public void setErrore(String errore) {
		this.errore = errore;
	}
	
	/** Messaggio di risposta */
	protected OpenSPCoop2Message responseMsg = null;
	public void setResponseMsg(OpenSPCoop2Message responseMsg) {
		this.responseMsg = responseMsg;
	}
	
	/** Codice Operazione Effettuata */
	protected int codice;
	public void setCodiceTrasporto(int codice) {
		this.codice = codice;
	}
	
	/** ContentLength */
	protected long contentLength = -1;
	public void setContentLength(long contentLength) {
		this.contentLength = contentLength;
	}

	/** File tmp */
	protected String location = null;
	public String internalGetLocationValue() {
		return this.location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	/** Eccezione processamento */
	protected Exception eccezioneProcessamento = null;
	public void setEccezioneProcessamento(Exception eccezioneProcessamento) {
		this.eccezioneProcessamento = eccezioneProcessamento;
	}
	
	/** Proprieta' del trasporto della risposta */
	protected Map<String, List<String>> propertiesTrasportoRisposta = new HashMap<>();
	public Map<String, List<String>> getPropertiesTrasportoRisposta() {
		return this.propertiesTrasportoRisposta;
	}

	/** CreationDate */
	protected Date creationDate;
	
	/** MessaggioDiagnostico */
	protected MsgDiagnostico msgDiagnostico;
	/** OutRequestContext */
	protected OutRequestContext outRequestContext;
	/** PostOutRequestContext */
	protected PostOutRequestContext postOutRequestContext;
	/** PreInResponseContext */
	protected PreInResponseContext preInResponseContext;
	public PreInResponseContext getPreInResponseContext() {
		return this.preInResponseContext;
	}
	/** State */
	protected IState state;
	
	/** SOAPAction */
	protected String soapAction = null;
	
	/** RequestInfo */
	protected RequestInfo requestInfo;
	
	/** Caching Response */
	private boolean responseAlready = false;
	private ResponseCachingConfigurazione responseCachingConfig = null;
	private String responseCachingDigest = null;

	/** Policy Token */
	private PolicyNegoziazioneToken policyNegoziazioneToken;
	protected PolicyTimeoutConfig policyTimeoutConfig;
	
	protected Date dataRichiestaInoltrata;
	@Override
	public Date getDataRichiestaInoltrata() {
		return this.dataRichiestaInoltrata;
	}
	
	protected Date dataAccettazioneRisposta;
    @Override
	public Date getDataAccettazioneRisposta(){
    	return this.dataAccettazioneRisposta;
    }
    
    protected DumpRaw dumpRaw = null;
    protected boolean logFileTraceHeaders = false;
    protected boolean logFileTracePayload = false;
	
    private boolean registerSendIntoContext = true;
    public void setRegisterSendIntoContext(boolean registerSendIntoContext) {
		this.registerSendIntoContext = registerSendIntoContext;
	}

    private IDAccordo idAccordo = null;
    protected IDAccordo getIdAccordo() {
		return this.idAccordo;
	}

    protected String idTransazione;
    protected Transaction transactionNullable = null;
	
	protected int dumpBinarioSoglia;
	protected File dumpBinarioRepositoryFile;
    
	protected PortaApplicativa pa;
	protected String nomeConnettoreAsincrono;
	protected PortaDelegata pd;
	
	protected boolean useTimeoutInputStream = false;
	
	protected boolean useLimitedInputStream = false;
	protected SogliaDimensioneMessaggio limitBytes = null;
	
	protected boolean useDiagnosticInputStream = false;
	
	protected List<Proprieta> proprietaPorta;
	
	protected IAsyncResponseCallback asyncResponseCallback;
	protected boolean asyncWait = false;
	protected boolean asyncInvocationSuccess = false;
	public void setAsyncInvocationSuccess(boolean asyncInvocationSuccess) {
		this.asyncInvocationSuccess = asyncInvocationSuccess;
	}
	
	protected ConnettoreBase(){
		this.creationDate = DateManager.getDate();
	}
	
	public void setPa(PortaApplicativa pa) {
		this.pa = pa;
	}

	public void setPd(PortaDelegata pd) {
		this.pd = pd;
	}
	
	protected boolean initialize(ConnettoreMsg request, boolean connectorPropertiesRequired, ResponseCachingConfigurazione responseCachingConfig){
		
		this.openspcoopProperties = OpenSPCoop2Properties.getInstance();
		this.loader = Loader.getInstance();

		if(request==null){
			this.errore = "Messaggio da consegnare is Null (ConnettoreMsg)";
			return false;
		}
		
		// async context
		this.asyncResponseCallback = request.getAsyncResponseCallback();
		
		// Raccolta parametri per costruttore logger
		this.properties = request.getConnectorProperties();
		if(connectorPropertiesRequired){
			if(this.properties == null)
				this.errore = "Proprietà del connettore non definite";
			else if(this.properties.size() == 0)
				this.errore = "Proprietà del connettore vuote";
		}
		
		// tipoConnetore
		this.tipoConnettore = request.getTipoConnettore();
		
		// generateErrorWithConnectorPrefix
		this.generateErrorWithConnectorPrefix = request.isGenerateErrorWithConnectorPrefix();
		
		// - Busta
		this.busta = request.getBusta();
		if(this.busta!=null)
			this.idMessaggio=this.busta.getID();
		
		// - Debug mode
		if(this.properties!=null && this.properties.get(CostantiConnettori.CONNETTORE_DEBUG)!=null &&
			"true".equalsIgnoreCase(this.properties.get(CostantiConnettori.CONNETTORE_DEBUG).trim())) {
			this.debug = true;
		}
	
		// Logger
		this.logger = new ConnettoreLogger(this.debug, this.idMessaggio, this.getPddContext());
		
		// MessageConfiguration
		if(this.getPddContext()!=null && this.getPddContext().containsKey(org.openspcoop2.core.constants.Costanti.REQUEST_INFO)){
			this.requestInfo = (RequestInfo) this.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.REQUEST_INFO);
		}
		
		// State
		this.state = request.getState();
		
		// Raccolta altri parametri
		try{
			this.requestMsg =  request.getRequestMessage(this.requestInfo, this.getPddContext());
		}catch(Exception e){
			this.eccezioneProcessamento = e;
			this.logger.error("Errore durante la lettura del messaggio da consegnare: "+this.readExceptionMessageFromException(e),e);
			this.errore = "Errore durante la lettura del messaggio da consegnare: "+this.readExceptionMessageFromException(e);
			return false;
		}
		if(this.requestMsg==null){
			this.errore = "Messaggio da consegnare is Null (Msg)";
			return false;
		}
		if(ServiceBinding.SOAP.equals(this.requestMsg.getServiceBinding())){
			this.isSoap = true;
		}
		else{
			this.isRest = true;
		}
		this.sbustamentoSoap = request.isSbustamentoSOAP();
		this.propertiesTrasporto = request.getPropertiesTrasporto();
		this.propertiesUrlBased = request.getPropertiesUrlBased();

		// Credenziali
		/**this.tipoAutenticazione = request.getAutenticazione();*/
		this.credenziali = request.getCredenziali();

		// Identificativo modulo
		this.idModulo = request.getIdModulo();
		
		// PolicyConfig
		this.policyTimeoutConfig = request.getPolicyTimeoutConfig();
				
		// Context per invocazioni handler
		this.outRequestContext = request.getOutRequestContext();
		this.msgDiagnostico = request.getMsgDiagnostico();

		// Limit
		if(this.getPddContext()!=null && this.getPddContext().containsKey(org.openspcoop2.core.constants.Costanti.LIMITED_STREAM)){
			Object o = this.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.LIMITED_STREAM);
			if(o instanceof SogliaDimensioneMessaggio soglia) {
				try {
					if(soglia!=null && soglia.getSogliaKb()>0) {
						this.useLimitedInputStream = true;
						this.limitBytes = soglia;
					}
				}catch(Exception t) {
					// ignore
				}
			}
		}
		
		// Timeout, Dump
		this.useTimeoutInputStream = this.openspcoopProperties.isConnettoriUseTimeoutInputStream();
		boolean dumpBinario = this.debug;
		DumpConfigurazione dumpConfigurazione = null;
		String protocol = this.getProtocolFactory()!=null ? this.getProtocolFactory().getProtocol() : null;
		IDSoggetto dominio = this.requestInfo!=null ? this.requestInfo.getIdentitaPdD() : this.openspcoopProperties.getIdentitaPortaDefault(protocol, this.requestInfo);
		String nomePorta = (this.requestInfo!=null && this.requestInfo.getProtocolContext()!=null) ? this.requestInfo.getProtocolContext().getInterfaceName() : null;
		if(this.policyTimeoutConfig!=null) {
			// entro qua tramite la gestione dei token di validazione, negoziazione e attribute authority
			try {
				ConfigurazionePdDManager configurazionePdDManager = ConfigurazionePdDManager.getInstance();
				if(this.pa!=null) {
					this.useTimeoutInputStream = configurazionePdDManager.isConnettoriUseTimeoutInputStream(this.pa);
					this.proprietaPorta = this.pa.getProprietaList();
				}
				else if(this.pd!=null) {
					this.useTimeoutInputStream = configurazionePdDManager.isConnettoriUseTimeoutInputStream(this.pd);
					this.proprietaPorta = this.pd.getProprietaList();
				}
			}catch(Exception e){
				this.eccezioneProcessamento = e;
				this.logger.error("Errore durante l'inizializzazione della configurazione di timeout: "+this.readExceptionMessageFromException(e),e);
				this.errore = "Errore durante l'inizializzazione della configurazione di timeout: "+this.readExceptionMessageFromException(e);
				return false;
			}
		}
		else if(this.idModulo!=null) {
			try {
				// Soglia Dump
				this.dumpBinarioSoglia = this.openspcoopProperties.getDumpBinarioInMemoryThreshold();
				this.dumpBinarioRepositoryFile = this.openspcoopProperties.getDumpBinarioRepository();
				
				ConfigurazionePdDManager configurazionePdDManager = ConfigurazionePdDManager.getInstance();
				if(ConsegnaContenutiApplicativi.ID_MODULO.equals(this.idModulo)){
					
					IDPortaApplicativa idPA = null;
					if(request.getIdPortaApplicativa()!=null) {
						idPA = request.getIdPortaApplicativa();
					}
					else if(nomePorta!=null) {
						idPA = new IDPortaApplicativa();
						idPA.setNome(nomePorta);
					}
					
					ConfigurazioneTracciamento configurazioneTracciamento = new ConfigurazioneTracciamento(this.logger.getLogger(), configurazionePdDManager, TipoPdD.APPLICATIVA);
					this.logFileTraceHeaders = configurazioneTracciamento.isTransazioniFileTraceDumpBinarioConnettoreHeaderEnabled();
					this.logFileTracePayload = configurazioneTracciamento.isTransazioniFileTraceDumpBinarioConnettorePayloadEnabled();
					
					if(idPA!=null) {
						this.pa = configurazionePdDManager.getPortaApplicativaSafeMethod(idPA, this.requestInfo);
						if(this.pa!=null) {
							this.useTimeoutInputStream = configurazionePdDManager.isConnettoriUseTimeoutInputStream(this.pa);
							dumpConfigurazione = configurazionePdDManager.getDumpConfigurazione(this.pa);
							configurazioneTracciamento = new ConfigurazioneTracciamento(this.logger.getLogger(), configurazionePdDManager, this.pa);
							this.logFileTraceHeaders = configurazioneTracciamento.isTransazioniFileTraceDumpBinarioConnettoreHeaderEnabled();
							this.logFileTracePayload = configurazioneTracciamento.isTransazioniFileTraceDumpBinarioConnettorePayloadEnabled();
							this.proprietaPorta = this.pa.getProprietaList();
							if(request.getTransazioneApplicativoServer()!=null) {
								this.nomeConnettoreAsincrono = request.getTransazioneApplicativoServer().getConnettoreNome();
							}
						}
					}
					
				}
				else {
					
					IDPortaDelegata idPD = null;
					if(nomePorta!=null) {
						idPD = new IDPortaDelegata();
						idPD.setNome(nomePorta);
					}
					
					ConfigurazioneTracciamento configurazioneTracciamento = new ConfigurazioneTracciamento(this.logger.getLogger(), configurazionePdDManager, TipoPdD.DELEGATA);
					this.logFileTraceHeaders = configurazioneTracciamento.isTransazioniFileTraceDumpBinarioConnettoreHeaderEnabled();
					this.logFileTracePayload = configurazioneTracciamento.isTransazioniFileTraceDumpBinarioConnettorePayloadEnabled();
					
					if(idPD!=null) {
						this.pd = configurazionePdDManager.getPortaDelegataSafeMethod(idPD, this.requestInfo);
						if(this.pd!=null) {
							this.useTimeoutInputStream = configurazionePdDManager.isConnettoriUseTimeoutInputStream(this.pd);
							dumpConfigurazione = configurazionePdDManager.getDumpConfigurazione(this.pd);
							configurazioneTracciamento = new ConfigurazioneTracciamento(this.logger.getLogger(), configurazionePdDManager, this.pd);
							this.logFileTraceHeaders = configurazioneTracciamento.isTransazioniFileTraceDumpBinarioConnettoreHeaderEnabled();
							this.logFileTracePayload = configurazioneTracciamento.isTransazioniFileTraceDumpBinarioConnettorePayloadEnabled();
							this.proprietaPorta = this.pd.getProprietaList();
						}
					}
					
				}
			
				if(request.getTransazioneApplicativoServer()!=null) {
					this.logFileTraceHeaders = false;
					this.logFileTracePayload = false;
				}
				
				this.dumpRaw = new DumpRaw(this.logger,dominio,this.idModulo,this.outRequestContext.getTipoPorta(), 
						dumpBinario, 
						dumpConfigurazione,
						this.logFileTraceHeaders, this.logFileTracePayload);
				if(this.dumpRaw.isActiveDumpDatabase()) {
					if(request.getTransazioneApplicativoServer()!=null) {
						this.dumpRaw.initDump(nomePorta, this.outRequestContext.getPddContext(),
								request.getTransazioneApplicativoServer(), 
								request.getIdPortaApplicativa(), request.getDataConsegnaTransazioneApplicativoServer());
					}
					else {
						this.dumpRaw.initDump(nomePorta, this.outRequestContext.getPddContext());
					}
				}
			}catch(Exception e){
				this.eccezioneProcessamento = e;
				this.logger.error("Errore durante l'inizializzazione del dump binario: "+this.readExceptionMessageFromException(e),e);
				this.errore = "Errore durante l'inizializzazione del dump binario: "+this.readExceptionMessageFromException(e);
				return false;
			}
		}
		
		// Diagnostic Input Stream
		if(this.idModulo!=null && this.msgDiagnostico!=null) {
			if(ConsegnaContenutiApplicativi.ID_MODULO.equals(this.idModulo)){
				this.useDiagnosticInputStream = this.openspcoopProperties.isConnettoriUseDiagnosticInputStreamConsegnaContenutiApplicativi();
			}
			else {
				this.useDiagnosticInputStream = this.openspcoopProperties.isConnettoriUseDiagnosticInputStreamInoltroBuste();
			}
		}
		
		
		// IdTransazione
		if(this.getPddContext()!=null) {
			Object oIdTransazione = this.getPddContext().getObject(Costanti.ID_TRANSAZIONE);
			if(oIdTransazione instanceof String s){
				this.idTransazione = s;
			}
		}
		try{
			if(this.idTransazione!=null) {
				this.transactionNullable = TransactionContext.getTransaction(this.idTransazione);
			}
		}catch(Throwable e){
			// La transazione potrebbe essere stata eliminata nelle comunicazioni stateful
		}
		
		// Cache Response
		this.responseCachingConfig = responseCachingConfig;
		if(this.responseCachingConfig!=null && StatoFunzionalita.ABILITATO.equals(this.responseCachingConfig.getStato()) &&
			this.requestMsg!=null) {
			Object digestO = this.requestMsg.getContextProperty(CostantiPdD.RESPONSE_CACHE_REQUEST_DIGEST);
			if(digestO!=null) {
									
				if(this.transactionNullable!=null) {
					this.transactionNullable.getTempiElaborazione().startResponseCachingReadFromCache();
				}
				
				this.responseCachingDigest = (String) digestO;
				try{
					ResponseCached responseCached =  GestoreCacheResponseCaching.getInstance().readByDigest(this.responseCachingDigest);
					if(responseCached!=null) {
					
						// esiste una risposta cachata, verifico eventuali direttive
						ResponseCachingConfigurazioneControl cacheControl = this.responseCachingConfig.getControl();
						if(cacheControl==null) {
							cacheControl = new ResponseCachingConfigurazioneControl(); // uso i valori di default
						}
						if(cacheControl!=null) {
					
							Map<String, List<String>> trasportoRichiesta = null;
							if(this.requestMsg!=null && this.requestMsg.getTransportRequestContext()!=null && 
									this.requestMsg.getTransportRequestContext().getHeaders()!=null) {
								trasportoRichiesta = this.requestMsg.getTransportRequestContext().getHeaders();
							}
							
							if(cacheControl.isNoCache() &&
								HttpUtilities.isDirectiveNoCache(trasportoRichiesta)) {
								GestoreCacheResponseCaching.getInstance().removeByUUID(responseCached.getUuid());
								responseCached = null;
							}
							
							if(responseCached!=null &&
								cacheControl.isMaxAge()) {
								Integer maxAge = HttpUtilities.getDirectiveCacheMaxAge(trasportoRichiesta);
								if(maxAge!=null && maxAge.intValue()>0 &&
									responseCached.getAgeInSeconds() > maxAge.intValue()) {
									GestoreCacheResponseCaching.getInstance().removeByUUID(responseCached.getUuid());
									responseCached = null;
								}
							}
								
						}
						
						if(responseCached!=null) {
						
							OpenSPCoop2Message msgResponse = responseCached.toOpenSPCoop2Message(
									org.openspcoop2.pdd.core.Utilities.getOpenspcoop2MessageFactory(this.logger.getLogger(), this.requestMsg, this.requestInfo, MessageRole.RESPONSE),
									this.openspcoopProperties.getAttachmentsProcessingMode(),
									this.openspcoopProperties.getCachingResponseHeaderCacheKey());
													
							this.responseMsg = msgResponse;
							
							this.dataAccettazioneRisposta = DateManager.getDate();
							
							org.openspcoop2.utils.transport.TransportResponseContext transportResponseContenxt = msgResponse.getTransportResponseContext();
							
							if(transportResponseContenxt!=null && transportResponseContenxt.getCodiceTrasporto()!=null){
								try {
									this.codice = Integer.parseInt(transportResponseContenxt.getCodiceTrasporto());
								}catch(Exception e) {
									this.logger.error("Errore durante la conversione del codice di trasporto ["+transportResponseContenxt.getCodiceTrasporto()+"]");
									this.codice = 200;
								}
							}
							else {
								this.codice = 200;
							}
							
							if(transportResponseContenxt!=null) {
								this.propertiesTrasportoRisposta = transportResponseContenxt.getHeaders();
							}
							
							this.contentLength = responseCached.getMessageLength();
							
							if(this.getPddContext()!=null) {
								this.getPddContext().addObject(RESPONSE_FROM_CACHE, true);
							}
							
							this.location = LOCATION_CACHED;
							
							
							this.responseAlready = true;
							
						}
					}
				}catch(Exception e){
					this.eccezioneProcessamento = e;
					this.logger.error("Errore durante la lettura della cache delle risposte: "+this.readExceptionMessageFromException(e),e);
					this.errore = "Errore durante la lettura della cache delle risposte: "+this.readExceptionMessageFromException(e);
					return false;
				}finally {
					if(this.transactionNullable!=null) {
						this.transactionNullable.getTempiElaborazione().endResponseCachingReadFromCache();
					}
				}
			}
		}

		// Negoziazione Token
		this.policyNegoziazioneToken = request.getPolicyNegoziazioneToken();
				
		// API
		this.idAccordo = request.getIdAccordo();
		
		return true;
	}
	
	private static final String FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
	protected NameValue getTokenHeader() throws ConnettoreException {
		return this.getTokenParameter(true);
	}
	protected NameValue getTokenQueryParameter() throws ConnettoreException {
		return this.getTokenParameter(false);
	}
	private ModIValidazioneSemanticaProfiloSicurezza modIValidazioneSemanticaProfiloSicurezza;
	public void setModIValidazioneSemanticaProfiloSicurezza(
			ModIValidazioneSemanticaProfiloSicurezza modIValidazioneSemanticaProfiloSicurezza) {
		this.modIValidazioneSemanticaProfiloSicurezza = modIValidazioneSemanticaProfiloSicurezza;
	}
	private NameValue getTokenParameter(boolean header) throws ConnettoreException {
		if(this.policyNegoziazioneToken!=null) {
			try {
				GestoreToken.validazioneConfigurazione(this.policyNegoziazioneToken); // assicura che la configurazione sia corretta
				
				String forwardMode = this.policyNegoziazioneToken.getForwardTokenMode();
				NameValue n = null;
				if(org.openspcoop2.pdd.core.token.Costanti.POLICY_RETRIEVE_TOKEN_FORWARD_MODE_RFC6750_HEADER.equals(forwardMode)) {
					if(header) {
						n = new NameValue();
						n.setName(HttpConstants.AUTHORIZATION);
						n.setValue(HttpConstants.AUTHORIZATION_PREFIX_BEARER);
					}
					else {
						return null;
					}
				}
				else if(org.openspcoop2.pdd.core.token.Costanti.POLICY_TOKEN_FORWARD_TRASPARENTE_MODE_RFC6750_URL.equals(forwardMode)) {
					if(!header) {
						n = new NameValue();
						n.setName(org.openspcoop2.pdd.core.token.Costanti.RFC6750_URI_QUERY_PARAMETER_ACCESS_TOKEN);
					}
					else {
						return null;
					}
				}
				else if(org.openspcoop2.pdd.core.token.Costanti.POLICY_TOKEN_FORWARD_TRASPARENTE_MODE_CUSTOM_HEADER.equals(forwardMode)) {
					if(header) {
						n = new NameValue();
						n.setName(this.policyNegoziazioneToken.getForwardTokenModeCustomHeader());
					}
					else {
						return null;
					}
				}
				else if(org.openspcoop2.pdd.core.token.Costanti.POLICY_TOKEN_FORWARD_TRASPARENTE_MODE_CUSTOM_URL.equals(forwardMode)) {
					if(!header) {
						n = new NameValue();
						n.setName(this.policyNegoziazioneToken.getForwardTokenModeCustomUrl());
					}
					else {
						return null;
					}
				}
				
				if(this.debug) {
					this.logger.debug("Negoziazione token '"+this.policyNegoziazioneToken.getName()+"' ...");
				}
				logPersonalizzato("negoziazioneToken.inCorso");
				EsitoNegoziazioneToken esitoNegoziazione = GestoreToken.endpointToken(this.debug, this.logger.getLogger(), this.policyNegoziazioneToken, 
						this.busta, this.requestInfo, 
						ConsegnaContenutiApplicativi.ID_MODULO.equals(this.idModulo) ? TipoPdD.APPLICATIVA : TipoPdD.DELEGATA, this.idModulo, this.pa, this.pd,
						this.getPddContext(), this.getProtocolFactory());
				if(this.debug) {
					this.logger.debug("Negoziazione token '"+this.policyNegoziazioneToken.getName()+"' terminata");
				}
				
				SimpleDateFormat sdf = DateUtils.getDefaultDateTimeFormatter(FORMAT);
								
				if(esitoNegoziazione==null) {
					throw new ConnettoreException("Esito Negoziazione non ritornato ?");
				}
				if(!esitoNegoziazione.isValido()) {
					throw new ConnettoreException(esitoNegoziazione.getDetails(),esitoNegoziazione.getEccezioneProcessamento());
				}
				if(esitoNegoziazione.isInCache()) {
					if(esitoNegoziazione.getInformazioniNegoziazioneToken().getExpiresIn()!=null) {
						this.logger.debug("Presente in cache access_token '"+esitoNegoziazione.getToken()+"'; expire in ("+sdf.format(esitoNegoziazione.getInformazioniNegoziazioneToken().getExpiresIn())+")");
					}
					else {
						this.logger.debug("Presente in cache access_token '"+esitoNegoziazione.getToken()+"'; no expire");
					}
					logPersonalizzato("negoziazioneToken.inCache");
				}
				else {
					if(esitoNegoziazione.getInformazioniNegoziazioneToken().getExpiresIn()!=null) {
						this.logger.debug("Recuperato access_token '"+esitoNegoziazione.getToken()+"'; expire in ("+sdf.format(esitoNegoziazione.getInformazioniNegoziazioneToken().getExpiresIn())+")");
					}
					else {
						this.logger.debug("Recuperato access_token '"+esitoNegoziazione.getToken()+"'; no expire");
					}
					logPersonalizzato("negoziazioneToken.completata");
				}
				
				if(this.modIValidazioneSemanticaProfiloSicurezza!=null) {
					String jti = ModIUtils.readJti(esitoNegoziazione.getToken(), this.logger.getLogger());
					if(jti!=null && StringUtils.isNotEmpty(jti)) {
						ModIUtils.replaceBustaIdWithJtiTokenId(this.modIValidazioneSemanticaProfiloSicurezza, jti);
						if(this.msgDiagnostico!=null) {
							this.msgDiagnostico.updateKeywordIdMessaggioRichiesta(this.busta.getID());
						}
						if(this.getPddContext()!=null) {
							this.getPddContext().put(org.openspcoop2.core.constants.Costanti.MODI_JTI_REQUEST_ID, jti);
						}
					}
					
					logPersonalizzato("inoltroInCorso");
				}
				
				if(n.getValue()!=null) {
					n.setValue(n.getValue()+esitoNegoziazione.getToken());
				}
				else {
					n.setValue(esitoNegoziazione.getToken());
				}
				return n;

			}catch(Exception e) {
				if(this.getPddContext()!=null) {
					this.getPddContext().addObject(org.openspcoop2.core.constants.Costanti.ERRORE_NEGOZIAZIONE_TOKEN, "true");
				}
				throw new ConnettoreException(e.getMessage(),e);
			}
		}
		
		return null;
	}
	protected void logPersonalizzato(String idDiagnostico) {
		if(this.msgDiagnostico!=null) {
			try {
				this.msgDiagnostico.logPersonalizzato(idDiagnostico);
			}catch(Exception t) {
				this.logger.error("Emissione diagnostica '"+idDiagnostico+"' fallita: "+t.getMessage(),t);
			}
		}
	}
	
	private void saveResponseInCache() {
				
		if(this.transactionNullable!=null) {
			this.transactionNullable.getTempiElaborazione().startResponseCachingSaveInCache();
		}
		
		try {
			if(this.responseCachingConfig!=null && StatoFunzionalita.ABILITATO.equals(this.responseCachingConfig.getStato()) &&
					this.responseMsg!=null) { // jms, null ha la response null
				
				boolean saveInCache = true;
				
				int cacheTimeoutSeconds = this.responseCachingConfig.getCacheTimeoutSeconds().intValue();
				
				Long kbMax = this.responseCachingConfig.getMaxMessageSize();
				long byteMax = -1;
				if(kbMax!=null) {
					byteMax = kbMax.longValue() * 1024;
				}
				
				Map<String, List<String>> trasportoRichiesta = null;
				if(this.requestMsg!=null && this.requestMsg.getTransportRequestContext()!=null && 
						this.requestMsg.getTransportRequestContext().getHeaders()!=null) {
					trasportoRichiesta = this.requestMsg.getTransportRequestContext().getHeaders();
				}
				
				ResponseCachingConfigurazioneControl cacheControl = this.responseCachingConfig.getControl();
				if(cacheControl==null) {
					cacheControl = new ResponseCachingConfigurazioneControl(); // uso i valori di default
				}
				if(cacheControl!=null &&
					cacheControl.isNoStore() &&
					HttpUtilities.isDirectiveNoStore(trasportoRichiesta)) {
					saveInCache = false;
				}
				
				if(saveInCache &&
					this.responseCachingConfig.sizeRegolaList()>0) {
						
					int returnCode = internalReadReturnCode();
					
					boolean isFault = this.responseMsg.isFault();
					
					ResponseCachingConfigurazioneRegola regolaGeneraleIncludeFault = null;
					
					saveInCache = false; // se ci sono delle regole, salvo solamente se la regola ha un match
					for (ResponseCachingConfigurazioneRegola regola : this.responseCachingConfig.getRegolaList()) {
						
						boolean skip = false;
						if(returnCode>0 && regola.getReturnCodeMin()!=null &&
							returnCode<regola.getReturnCodeMin().intValue()) {
							skip=true;
						}
						if(!skip && returnCode>0 && regola.getReturnCodeMax()!=null &&
							returnCode>regola.getReturnCodeMax().intValue()) {
							skip=true;
						}
						
						if(!skip && isFault && !regola.isFault()) {
							skip=true;
						}
						else if(!skip && !isFault && regola.isFault()) {
							// in questo caso la regola va bene poichè l'istruzione fault indica semplicemente se includere o meno un fault
							// e quindi essendo il messaggio non un fault la regola match.
							// Però prima di usarla verifico se esiste una regola specifica senza fault
							if(regolaGeneraleIncludeFault==null) {
								regolaGeneraleIncludeFault = regola;
							}
							skip=true;
						}
						if(!skip) {
						
							// ho un match
							saveInCache = true;
							if(regola.getCacheTimeoutSeconds()!=null) {
								cacheTimeoutSeconds = regola.getCacheTimeoutSeconds().intValue();
							}
							break;
							
						}
					}
					
					if(!saveInCache && regolaGeneraleIncludeFault!=null) {
						// ho il match di un messaggio che non è un fault con una regola che fa includere anche i fault
						saveInCache = true;
						if(regolaGeneraleIncludeFault.getCacheTimeoutSeconds()!=null) {
							cacheTimeoutSeconds = regolaGeneraleIncludeFault.getCacheTimeoutSeconds().intValue();
						}
					}
				}
				
				if(saveInCache &&			
					kbMax!=null &&
					this.contentLength>0 &&
					this.contentLength>byteMax) {
					this.logger.debug("Messaggio non salvato in cache, nonostante la configurazione lo richiesta poichè la sua dimensione ("+this.contentLength+ 
							" bytes) supera la dimensione massima consentita ("+byteMax+" bytes)");
					saveInCache = false;
				}
				
				if(saveInCache) {
					ResponseCached responseCached = ResponseCached.toResponseCached(this.responseMsg, cacheTimeoutSeconds);
					if(kbMax!=null && this.contentLength<=0 &&
						// ricontrollo poichè non era disponibile l'informazione
						responseCached.getMessageLength()>byteMax) {
						this.logger.debug("Messaggio non salvato in cache, nonostante la configurazione lo richiesta poichè la sua dimensione ("+responseCached.getMessageLength()+ 
								" bytes) supera la dimensione massima consentita ("+byteMax+" bytes)");
						saveInCache = false;
					}
					if(saveInCache) {
						GestoreCacheResponseCaching.getInstance().save(this.responseCachingDigest, responseCached);
					}
				}
			}
		}catch(Throwable e){
			this.logger.error("Errore durante il salvataggio nella cache delle risposte: "+this.readExceptionMessageFromException(e),e);
		}finally {
			if(this.transactionNullable!=null) {
				this.transactionNullable.getTempiElaborazione().endResponseCachingSaveInCache();
			}
		}
	}
	private int internalReadReturnCode() {
		int returnCode = -1;
		try {
			if(this.responseMsg.getTransportResponseContext()!=null && this.responseMsg.getTransportResponseContext().getCodiceTrasporto()!=null) {
				returnCode = Integer.parseInt(this.responseMsg.getTransportResponseContext().getCodiceTrasporto());
			}
		}catch(Exception e) {
			// ignore
		}
		return returnCode;
	}
	
	protected abstract boolean initializePreSend(ResponseCachingConfigurazione responseCachingConfig, ConnettoreMsg request);
	
	protected abstract boolean send(ConnettoreMsg request);
		
	@Override
	public boolean send(ResponseCachingConfigurazione responseCachingConfig, ConnettoreMsg request){
		
		try {
		
			if(!this.initializePreSend(responseCachingConfig, request)){
				return false;
			}
			
			// caching response
			if(this.responseAlready) {
				return true;
			}
			
			boolean returnEsitoSend = send(request);
			if(returnEsitoSend) {
				saveResponseInCache();
			}
			return returnEsitoSend;
			
		}finally {
			if(this.asyncResponseCallback!=null && !this.asyncWait) {
				try {
					this.asyncResponseCallback.asyncComplete(AsyncResponseCallbackClientEvent.NONE, this.asyncInvocationSuccess);
				}catch(Exception e) {
					this.logger.error("AsyncCallback.complete: "+e.getMessage(),e);
				}
			}
		}
		
	}
	
	public void asyncComplete(AsyncResponseCallbackClientEvent clientEvent) {
		try {
			if(this.asyncResponseCallback==null) {
				this.logger.error("Async context not active");
				this.errore = "Async context not active";
			}
			else {
				this.asyncResponseCallback.asyncComplete(clientEvent, this.asyncInvocationSuccess);
			}
		}catch(Throwable e) {
			this.logger.error("AsyncCallback.complete: "+e.getMessage(),e);
		}
	}
	
	/**
	 * In caso di avvenuto errore in fase di consegna, questo metodo ritorna il motivo dell'errore.
	 *
	 * @return motivo dell'errore (se avvenuto in fase di consegna).
	 * 
	 */
	@Override
	public String getErrore(){
		return this.errore;
	}

	/**
	 * In caso di avvenuta consegna, questo metodo ritorna il codice di trasporto della consegna.
	 *
	 * @return se avvenuta una consegna ritorna il codice di trasporto della consegna.
	 * 
	 */
	@Override
	public int getCodiceTrasporto(){
		return this.codice;
	}

    /**
     * In caso di avvenuta consegna, questo metodo ritorna l'header del trasporto
     *
     * @return se avvenuta una consegna ritorna l'header del trasporto
     * 
     */
    @Override
	public Map<String, List<String>> getHeaderTrasporto(){
    	Map<String, List<String>> m = null;
		if(this.propertiesTrasportoRisposta.size()>0){
    		m = this.propertiesTrasportoRisposta;
    	}
		return m;
    }
	
	/**
	 * Ritorna la risposta pervenuta in seguita alla consegna effettuata
	 *
	 * @return la risposta.
	 * 
	 */
	@Override
	public OpenSPCoop2Message getResponse(){
		if(this.responseMsg!=null) {
			if(this.getProtocolFactory()!=null) {
				this.responseMsg.setProtocolName(this.getProtocolFactory().getProtocol());
			}
			if(this.getPddContext()!=null) {
				this.responseMsg.setTransactionId(PdDContext.getValue(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE, this.getPddContext()));
			}
		}
		return this.responseMsg;
	}

	/**
     * In caso di avvenuta consegna, questo metodo ritorna, se disponibile, la dimensione della risposta (-1 se non disponibile)
     *
     * @return se avvenuta una consegna,  questo metodo ritorna, se disponibile, la dimensione della risposta (-1 se non disponibile)
     * 
     */
	@Override
	public long getContentLength() {
		return this.contentLength;
	}
	
    /**
     * Ritorna l'informazione su dove il connettore sta spedendo il messaggio
     * 
     * @return location di inoltro del messaggio
     */
    @Override
	public String getLocation() throws ConnettoreException{
    	return this.location;
    }
    
    /**
     * Ritorna l'eccezione in caso di errore di processamento
     * 
     * @return eccezione in caso di errore di processamento
     */
    @Override
	public Exception getEccezioneProcessamento(){
    	return this.eccezioneProcessamento;
    }
	
	/**
     * Effettua la disconnessione 
     */
    @Override
	public void disconnect() throws ConnettoreException{
        
    	try{
    	
    		// Rilascio eventuali risorse associate al messaggio di risposte 
			// Tali risorse non sono ancora rilasciate, se durante il flusso e' stato generato un nuovo messaggio.
			// Due chiamate della close non provocano problemi, la seconda non comporta nessun effetto essendo lo stream 'NotifierInputStream' gia' chiuso
			if(this.responseMsg!=null && this.responseMsg.getNotifierInputStream()!=null){
				this.responseMsg.getNotifierInputStream().close();
			}
			
    	}catch(Exception e){
    		throw new ConnettoreException("Chiusura connessione non riuscita: "+e.getMessage(),e);
    	}
    	
    }
	
    /**
     * Data di creazione del connettore 
     */
    @Override
	public Date getCreationDate() throws ConnettoreException{
    	return this.creationDate;
    }
	
    
    /**
     * Ritorna le opzioni di NotifierInputStreamParams per la risposta
     * 
     * @return NotifierInputStreamParams
     * @throws ConnettoreException
     */
    @Override
	public NotifierInputStreamParams getNotifierInputStreamParamsResponse() throws ConnettoreException{
    	if(this.preInResponseContext !=null){
    		return this.preInResponseContext.getNotifierInputStreamParams();
    	}
    	return null;
    }
    
    
    public void postOutRequest() throws ConnettoreException{
    	
    	if(this.registerSendIntoContext && this.getPddContext()!=null) {
    		this.getPddContext().addObject(Costanti.RICHIESTA_INOLTRATA_BACKEND, Costanti.RICHIESTA_INOLTRATA_BACKEND_VALORE);
    	}
	   
    	if(this.msgDiagnostico!=null && this.outRequestContext!=null){
    		
    		this.postOutRequestContext = new PostOutRequestContext(this.outRequestContext);
    		this.postOutRequestContext.setCodiceTrasporto(this.getCodiceTrasporto());
    		this.postOutRequestContext.setResponseHeaders(this.getHeaderTrasporto());
    		
    		// invocazione handler
    		try{
    			GestoreHandlers.postOutRequest(this.postOutRequestContext, this.msgDiagnostico, OpenSPCoop2Logger.getLoggerOpenSPCoopCore());
    		}catch(Exception e){
				if(e instanceof HandlerException he){
					if(he.isEmettiDiagnostico()){
						this.msgDiagnostico.logErroreGenerico(e, ((HandlerException)e).getIdentitaHandler());
					}
					else{
						OpenSPCoop2Logger.getLoggerOpenSPCoopCore().error(e.getMessage(),e);
					}
				}else{
					this.msgDiagnostico.logErroreGenerico(e, "PostOutRequestHandler");
				}
				throw new ConnettoreException(e.getMessage(),e);
    		}
    	}
    	
    }
    
    public void preInResponse() throws ConnettoreException{
    	
    	this.dataAccettazioneRisposta = DateManager.getDate();
    	
    	if(this.msgDiagnostico!=null && this.outRequestContext!=null){
        
    		PostOutRequestContext postContext = this.postOutRequestContext;
    		if(postContext==null){
    			postContext = new PostOutRequestContext(this.outRequestContext);
    			postContext.setCodiceTrasporto(this.getCodiceTrasporto());
    			postContext.setResponseHeaders(this.getHeaderTrasporto());
    		}
    		
    		this.preInResponseContext = new PreInResponseContext(postContext);
    		
    		// invocazione handler
    		try{
    			GestoreHandlers.preInResponse(this.preInResponseContext, this.msgDiagnostico, OpenSPCoop2Logger.getLoggerOpenSPCoopCore());
    		}catch(Exception e){
				if(e instanceof HandlerException he){
					if(he.isEmettiDiagnostico()){
						this.msgDiagnostico.logErroreGenerico(e, ((HandlerException)e).getIdentitaHandler());
					}
					else{
						OpenSPCoop2Logger.getLoggerOpenSPCoopCore().error(e.getMessage(),e);
					}
				}else{
					this.msgDiagnostico.logErroreGenerico(e, "PreInResponseHandler");
				}
				throw new ConnettoreException(e.getMessage(),e);
    		}
    	}
    	 	
    }
        
    public String readExceptionMessageFromException(Throwable e) {
    	return readConnectionExceptionMessageFromException(e);
    }
    public static String readConnectionExceptionMessageFromException(Throwable e) {
    	
    	// In questo metodo è possibile gestire meglio la casistica dei messaggi di errore ritornati.
    	
    	// 1. Host Unknown
    	// java.net.UnknownHostException
    	if(e instanceof java.net.UnknownHostException){
    		return "unknown host '"+e.getMessage()+"'";
    	}
    	
    	// 2. java.net.SocketException
    	String se = readSocketExceptionFromException(e);
    	if(se!=null) {
    		return se;
    	}
    	
    	// 3. java.security.cert.CertificateException
    	String ce = readCertificateExceptionFromException(e);
    	if(ce!=null) {
    		return ce;
    	}
    	
    	// 4. java.io.IOException
    	String ioe = readIOExceptionFromException(e);
    	if(ioe!=null) {
    		return ioe;
    	}
    	
    	// 5. ClientAbortException (Succede nel caso il buffer del client non sia più disponibile)
    	if(Utilities.existsInnerException(e, "org.apache.catalina.connector.ClientAbortException")){
    		Throwable internal = Utilities.getInnerException(e, "org.apache.catalina.connector.ClientAbortException");
    		if(internalIsNotNullMessageException(internal)){
    			return "ClientAbortException - "+ internalBuildException(e, internal);
    		}
    	}
    	    	    	    	
    	// Check Null Message
    	if(internalIsNotNullMessageException(e)){
    		return e.getMessage();
    	}
    	else{
    		Throwable tNotEmpty = ParseExceptionUtils.getInnerNotEmptyMessageException(e);
    		if(tNotEmpty!=null){
    			return tNotEmpty.getMessage();
    		}
    		else{
    			return "ErrorOccurs - "+ e.getMessage();
    		}
    	}
    	
    }
    private static String readSocketExceptionFromException(Throwable e) {
    	if(e instanceof java.net.SocketException s &&
    		internalIsNotNullMessageException(s)){
    		return s.getMessage();
    	}
    	if(Utilities.existsInnerException(e, java.net.SocketException.class)){
    		Throwable internal = Utilities.getInnerException(e, java.net.SocketException.class);
    		if(internalIsNotNullMessageException(internal)){
    			return internalBuildException(e, internal);
    		}
    	}
    	return null;
    }
    private static String readCertificateExceptionFromException(Throwable e) {
    	if(e instanceof java.security.cert.CertificateException ce &&
    		internalIsNotNullMessageException(ce)){
    		return ce.getMessage();
    	}
    	if(Utilities.existsInnerInstanceException(e, java.security.cert.CertificateException.class)){
    		boolean last = true;
    		
    		Throwable internalNotYetValid = Utilities.getInnerInstanceException(e, java.security.cert.CertificateNotYetValidException.class, last);
    		if(internalNotYetValid!=null &&
	    		internalIsNotNullMessageException(internalNotYetValid)){
    			return internalBuildException(e, internalNotYetValid);
    		}
    		
    		Throwable internalExpired = Utilities.getInnerInstanceException(e, java.security.cert.CertificateExpiredException.class, last);
    		if(internalExpired!=null &&
	    		internalIsNotNullMessageException(internalExpired)){
    			return internalBuildException(e, internalExpired);
    		}
    		
    		// Anche in questo caso vengono due eccezioni uguali
    		/**Throwable internalRevoked = Utilities.getInnerInstanceException(e, java.security.cert.CertificateRevokedException.class, last);
    		if(internalRevoked!=null) {
	    		if(_isNotNullMessageException(internalRevoked)){
	    			return _buildException(e, internalRevoked);
	    		}
    		}*/
    		
    		// solo nei casi precedenti costruisco una eccezione con la parte interna.
    		// Negli altri casi (es. certificato non presente nel truststore 'unable to find valid certification path to requested target') verrebbero due eccezioni uguali
    		if(internalIsNotNullMessageException(e)){
    			return e.getMessage();
    		}
    	}
    	return null;
    }
    private static String readIOExceptionFromException(Throwable e) {
    	if(e instanceof java.io.IOException io && /** || java.io.IOException.class.isInstance(e)){ */
    		internalIsNotNullMessageException(io)){
    		return io.getMessage();
    	}
    	if(Utilities.existsInnerException(e, java.io.IOException.class)){
    		Throwable internal = Utilities.getInnerException(e, java.io.IOException.class);
    		if(internalIsNotNullMessageException(internal)){
    			return internalBuildException(e, internal);
    		}
    	}
    	return null;
    }
    protected String buildException(Throwable original,Throwable internal){
    	return internalBuildException(original, internal);
    }
    private static String internalBuildException(Throwable original,Throwable internal){
    	if(internalIsNotNullMessageException(original)){
    		String internalMessage = internal.getMessage();
    		String originalMessage = original.getMessage();
    		if(originalMessage!=null && !originalMessage.equals(internalMessage)) {
    			return internalMessage + " (sourceException: "+originalMessage+")";
    		}
    		else {
    			return internal.getMessage();
    		}
    	}
    	else{
    		return internal.getMessage();
    	}
    }
    protected boolean isNotNullMessageException(Throwable tmp){
    	return internalIsNotNullMessageException(tmp);
    }
    private static boolean internalIsNotNullMessageException(Throwable tmp){
    	return tmp.getMessage()!=null && !"".equals(tmp.getMessage()) && !"null".equalsIgnoreCase(tmp.getMessage());
    }
    
    protected boolean isDumpBinarioRichiesta() {
    	return this.debug || (this.dumpRaw!=null && this.dumpRaw.isActiveDumpDatabaseRichiesta()); 
    			/**this.logFileTrace;*/
    }
    public boolean isDumpBinarioRisposta() {
    	return this.debug || (this.dumpRaw!=null && this.dumpRaw.isActiveDumpDatabaseRisposta()); 
    			/**this.logFileTrace;*/
    }
    
    private InfoConnettoreUscita infoConnettoreUscita = null;
    protected void emitDiagnosticStartDumpBinarioRichiestaUscita() {
    	if(this.dumpRaw!=null && this.dumpRaw.isActiveDumpDatabaseRichiesta()) {
			this.dumpRaw.emitDiagnosticStartDumpBinarioRichiestaUscita();
    	}
    }
    protected void dumpBinarioRichiestaUscita(DumpByteArrayOutputStream bout, MessageType messageType, String contentTypeRichiesta,String location, Map<String, List<String>> trasporto) throws DumpException {
    	if(this.debug){
    		String content = null;
	    	if(bout!=null) {
	    		content = bout.toString();
	    		this.logger.info("Messaggio inviato (ContentType:"+contentTypeRichiesta+") :\n"+content,false);
	    	}
	    	else {
	    		this.logger.info("Messaggio inviato senza contenuto nell'http-payload", false);
	    	}
		}
    	if(this.dumpRaw!=null && this.dumpRaw.isActiveDumpDatabaseRichiesta()) {
			this.infoConnettoreUscita = new InfoConnettoreUscita();
			this.infoConnettoreUscita.setLocation(location);
			this.infoConnettoreUscita.setHeaders(trasporto);
	    	this.dumpRaw.dumpRequest(bout, messageType, this.infoConnettoreUscita);
    	}
    }
    protected void emitDiagnosticStartDumpBinarioRispostaIngresso() {
    	if(this.dumpRaw!=null && this.dumpRaw.isActiveDumpDatabaseRisposta()) {
			this.dumpRaw.emitDiagnosticStartDumpBinarioRispostaIngresso();
    	}
    }
    protected void dumpBinarioRispostaIngresso(DumpByteArrayOutputStream raw, MessageType messageType, Map<String, List<String>> trasportoRisposta) throws DumpException {
    	if(this.dumpRaw!=null && this.dumpRaw.isActiveDumpDatabaseRisposta()) {
    		this.dumpRaw.dumpResponse(raw, messageType, this.infoConnettoreUscita, trasportoRisposta);
    	}
    }
    
    protected Map<String, Object> dynamicMap = null;
    protected Map<String, Object> buildDynamicMap(ConnettoreMsg connettoreMsg){
    	if(this.dynamicMap==null) {
    		this.dynamicMap = new HashMap<>();
    	}
    	DynamicInfo dInfo = new DynamicInfo(connettoreMsg, this.getPddContext());
    	Logger log = null;
    	if(this.logger!=null) {
    		log = this.logger.getLogger();
    	}
    	if(log==null) {
    		log = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();
    	}
    	DynamicUtils.fillDynamicMap(log,this.dynamicMap, dInfo);
		return this.dynamicMap;
    }
    
    private String getPropertyPrefix(String name) {
    	return "Proprieta' '"+name+"' ";
    }
    
    protected String getDynamicProperty(String tipoConnettore,boolean required,String name,Map<String,Object> dynamicMap) throws ConnettoreException{
		String tmp = this.properties.get(name);
		if(tmp!=null){
			tmp = tmp.trim();
		}
		if(tmp==null || "".equals(tmp)){
			if(required){
				throw new ConnettoreException(getPropertyPrefix(name)+"non fornita e richiesta da questo tipo di connettore ["+tipoConnettore+"]");
			}
			return null;
		}
		try {
			return DynamicUtils.convertDynamicPropertyValue(name, tmp, dynamicMap, this.getPddContext(), false);
		}catch(Exception e) {
			throw new ConnettoreException(e.getMessage(),e);
		}
    }
    
   
    
    protected boolean isBooleanProperty(String tipoConnettore,boolean defaultValue,String name){
    	if(tipoConnettore!=null) {
    		// nop
    	}
  		String tmp = this.properties.get(name);
  		if(tmp!=null){
  			tmp = tmp.trim();
  		}
  		if(tmp==null || "".equals(tmp)){
  			return defaultValue;
  		}
  		return "true".equalsIgnoreCase(tmp) || CostantiConfigurazione.ABILITATO.getValue().equalsIgnoreCase(tmp);
    }
    
    protected Integer getIntegerProperty(String tipoConnettore,boolean required,String name) throws ConnettoreException{
		String tmp = this.properties.get(name);
		if(tmp!=null){
			tmp = tmp.trim();
		}
		if(tmp==null || "".equals(tmp)){
			if(required){
				throw new ConnettoreException(getPropertyPrefix(name)+"non fornita e richiesta da questo tipo di connettore ["+tipoConnettore+"]");
			}
			return null;
		}
		try{
			return Integer.parseInt(tmp);
		}catch(Exception e){
			throw new ConnettoreException(getPropertyPrefix(name)+"contiene un valore non corretto: "+e.getMessage(),e);
		}

    }
    
    
	protected void forwardHttpRequestHeader() throws MessageException {
		OpenSPCoop2MessageProperties forwardHeader = null;
		if(ServiceBinding.REST.equals(this.requestMsg.getServiceBinding())) {
			forwardHeader = this.requestMsg.getForwardTransportHeader(this.openspcoopProperties.getRESTServicesHeadersForwardConfig(true));
		}
		else {
			forwardHeader = this.requestMsg.getForwardTransportHeader(this.openspcoopProperties.getSOAPServicesHeadersForwardConfig(true));
		}
				
		if(forwardHeader!=null && forwardHeader.size()>0){
			if(this.debug)
				this.logger.debug("Forward header di trasporto (size:"+forwardHeader.size()+") ...");
			if(this.propertiesTrasporto==null){
				this.propertiesTrasporto = new HashMap<>();
			}
			forwardHttpRequestHeader(forwardHeader);
		}
	}
	private void forwardHttpRequestHeader(OpenSPCoop2MessageProperties forwardHeader) {
		Iterator<String> keys = forwardHeader.getKeys();
		while (keys.hasNext()) {
			String key = keys.next();
			List<String> values = forwardHeader.getPropertyValues(key);
			if(values!=null && !values.isEmpty()) {
				List<String> vs = new ArrayList<>(values); // per evitare che la add sottostante aggiunga alla collezione e si continui a iterare
				for (String value : vs) {
					if(this.debug)
						this.logger.debug("Forward Transport Header ["+key+"]=["+value+"]");
					TransportUtils.addHeader(this.propertiesTrasporto, key, value);		
				}
			}
		}
	}
    
    
    private Map<String,List<String>> headersImpostati = new HashMap<>(); // per evitare che header generati nel conettore siano sovrascritti da eventuali forward. Gli header del connettore vengono impostati prima del forward.
    protected void clearRequestHeader() {
    	this.headersImpostati.clear();
    }
    private Messaggio messaggioDumpUscita = null;
    protected void setRequestHeader(String key,List<String> values) throws ConnettoreException {
    	// ridefinito nei connettori dove esistono header da spedire 
    } 
    protected void setRequestHeader(String key, String value, Map<String, List<String>> propertiesTrasportoDebug) throws ConnettoreException {
    	List<String> list = new ArrayList<>(); 
    	list.add(value);
    	this.setRequestHeader(key, list, propertiesTrasportoDebug);
    }
    protected void setRequestHeader(String key,List<String> values, Map<String, List<String>> propertiesTrasportoDebug) throws ConnettoreException {
    	if(!this.headersImpostati.containsKey(key)) {
    		this.headersImpostati.put(key,values);
	    	this.setRequestHeader(key,values);
	    	if(propertiesTrasportoDebug!=null) {
	    		propertiesTrasportoDebug.put(key, values);
	    	}
    	}
    	else {
    		// Update eventuale dump in TransazioneContext
    		setRequestHeaderUpdateTransactionContext(key);
    	}
    }
    private void setRequestHeaderUpdateTransactionContext(String key) {
    	if(this.messaggioDumpUscita==null &&
				this.openspcoopProperties.isTransazioniSaveDumpInUniqueTransaction() && this.transactionNullable!=null) {
			try {
				for (Messaggio messaggio : this.transactionNullable.getMessaggi()) {
					if(TipoMessaggio.RICHIESTA_USCITA.equals(messaggio.getTipoMessaggio())) {
						this.messaggioDumpUscita = messaggio;
						break;
					}
				}
			}
			catch(Exception e){
				this.logger.error("Adeguamento dump http header non riuscito: "+e.getMessage(),e);
			}
		}
		if(this.messaggioDumpUscita!=null) {
			if(this.messaggioDumpUscita.getHeaders()==null) {
				this.messaggioDumpUscita.setHeaders(new HashMap<>());
			}
			TransportUtils.removeRawObject(this.messaggioDumpUscita.getHeaders(), key);
			this.messaggioDumpUscita.getHeaders().put(key, this.headersImpostati.get(key));
		}
    }
    
    private boolean emitDiagnosticResponseRead = false;
    protected void emitDiagnosticResponseRead(InputStream is) {
		if(is!=null && this.msgDiagnostico!=null && !this.emitDiagnosticResponseRead) {
			this.msgDiagnostico.logPersonalizzato("ricezioneRisposta.firstAccessRequestStream");
			this.emitDiagnosticResponseRead = true;
		}
    }
}
