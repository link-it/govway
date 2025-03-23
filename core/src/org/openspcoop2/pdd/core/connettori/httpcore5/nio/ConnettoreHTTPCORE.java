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




package org.openspcoop2.pdd.core.connettori.httpcore5.nio;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.hc.client5.http.ConnectionKeepAliveStrategy;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.core5.http.nio.AsyncEntityProducer;
import org.apache.hc.core5.http.nio.AsyncRequestProducer;
import org.apache.hc.core5.http.nio.AsyncResponseConsumer;
import org.apache.hc.core5.http.nio.entity.BasicAsyncEntityProducer;
import org.apache.hc.core5.http.nio.entity.FileEntityProducer;
import org.apache.hc.core5.http.nio.support.BasicRequestProducer;
import org.openspcoop2.core.config.ResponseCachingConfigurazione;
import org.openspcoop2.core.constants.CostantiConnettori;
import org.openspcoop2.core.constants.TransferLengthModes;
import org.openspcoop2.core.transazioni.constants.TipoMessaggio;
import org.openspcoop2.message.OpenSPCoop2RestMessage;
import org.openspcoop2.message.OpenSPCoop2SoapMessage;
import org.openspcoop2.message.constants.Costanti;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.soap.AbstractOpenSPCoop2Message_soap_impl;
import org.openspcoop2.message.soap.TunnelSoapUtils;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.connettori.ConnettoreException;
import org.openspcoop2.pdd.core.connettori.ConnettoreExtBaseHTTP;
import org.openspcoop2.pdd.core.connettori.ConnettoreHttpPoolParams;
import org.openspcoop2.pdd.core.connettori.ConnettoreMsg;
import org.openspcoop2.pdd.core.connettori.httpcore5.ConnettoreHTTPCOREUtils;
import org.openspcoop2.pdd.core.connettori.httpcore5.ConnettoreHttpRequestInterceptor;
import org.openspcoop2.pdd.core.transazioni.TransactionContext;
import org.openspcoop2.pdd.mdb.ConsegnaContenutiApplicativi;
import org.openspcoop2.utils.BooleanNullable;
import org.openspcoop2.utils.NameValue;
import org.openspcoop2.utils.io.Base64Utilities;
import org.openspcoop2.utils.io.DumpByteArrayOutputStream;
import org.openspcoop2.utils.transport.TransportUtils;
import org.openspcoop2.utils.transport.http.ContentTypeUtilities;
import org.openspcoop2.utils.transport.http.HttpBodyParameters;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpUtilities;
import org.openspcoop2.utils.transport.http.RFC2047Utilities;



/**
 * ConnettoreHTTPCORE5
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConnettoreHTTPCORE extends ConnettoreExtBaseHTTP {

	public static final String ENDPOINT_TYPE = "httpcore-nio";
	public static final String ID_HTTPCORE = "HTTPCore5-NIO";

	protected static final String MSG_RELEASE_RESOURCES_FAILED = "Release resources failed: ";
	
	private static boolean gestioneRedirectTramiteLibrerieApache = false; // con l'implementazione di govway, vengono registrati gli hop nei diagnostici 
	
	
	/* ********  F I E L D S  P R I V A T I  ******** */

	private HttpUriRequestBase httpRequest = null;
	
	protected ConnettoreHTTPCOREConnectionConfig httpConnectionConfig = null;
	
	protected DumpByteArrayOutputStream cloasebleDumpBout;

	protected URL url;
	
	protected int connectionTimeout = -1;
	protected boolean connectionTimeoutConfigurazioneGlobale = true;
	protected int readConnectionTimeout = -1;
	protected boolean readConnectionTimeoutConfigurazioneGlobale = true;
	
	/* Costruttori */
	public ConnettoreHTTPCORE(){
		super();
	}
	public ConnettoreHTTPCORE(boolean https){
		super(https);
	}
	
	
	
	/* ********  METODI  ******** */

	private String entryPointThreadName = null;
	private String actualThreadName = null;
	@Override
	protected boolean initialize(ConnettoreMsg request, boolean connectorPropertiesRequired, ResponseCachingConfigurazione responseCachingConfig){
		
		this.entryPointThreadName = Thread.currentThread().getName();
		this.actualThreadName = this.entryPointThreadName;
		
		return super.initialize(request, connectorPropertiesRequired, responseCachingConfig);
	}
	
	/**
	 * Si occupa di effettuare la consegna HTTP_POST (sbustando il messaggio SOAP).
	 * Si aspetta di ricevere una risposta non sbustata.
	 *
	 * @return true in caso di consegna con successo, false altrimenti
	 * 
	 */
	@Override
	protected boolean sendHTTP(ConnettoreMsg request){
		
		try{
			
			// Creazione URL
			if(this.debug)
				this.logger.debug("Creazione URL...");
			this.buildLocation();		
			if(this.debug)
				this.logger.debug("Creazione URL ["+this.location+"]...");
			this.url = new URI( this.location ).toURL();	

			
			// Keep-alive
			ConnectionKeepAliveStrategy keepAliveStrategy = null; /**new CustomKeepAliveStrategy();*/
			
			
			
			// Collezione header di trasporto per dump
			Map<String, List<String>> propertiesTrasportoDebug = null;
			if(this.isDumpBinarioRichiesta()) {
				propertiesTrasportoDebug = new HashMap<>();
			}
			
			// ConnettoreHTTPCOREConnectionConfig
			this.httpConnectionConfig = new ConnettoreHTTPCOREConnectionConfig();
			this.httpConnectionConfig.setDebug(this.debug);
			this.httpConnectionConfig.setSslContextProperties(this.sslContextProperties);
			
			if(this.properties.get(CostantiConnettori.CONNETTORE_CONNECTION_TIMEOUT)!=null){
				try{
					this.connectionTimeout = Integer.parseInt(this.properties.get(CostantiConnettori.CONNETTORE_CONNECTION_TIMEOUT));
					this.connectionTimeoutConfigurazioneGlobale = this.properties.containsKey(CostantiConnettori.CONNETTORE_CONNECTION_TIMEOUT_GLOBALE);
				}catch(Exception e){
					this.logger.error("Parametro '"+CostantiConnettori.CONNETTORE_CONNECTION_TIMEOUT+"' errato",e);
				}
			}
			if(this.connectionTimeout==-1){
				this.connectionTimeout = HttpUtilities.HTTP_CONNECTION_TIMEOUT;
			}
			this.httpConnectionConfig.setConnectionTimeout(this.connectionTimeout);
			
			
			// Lettura Parametri Pool
			ConnettoreHttpPoolParams poolParams = ConnettoreHttpPoolParamsBuilder.newConnettoreHttpPoolParams(this.openspcoopProperties, this.url, this.proprietaPorta);
			this.httpConnectionConfig.setHttpPoolParams(poolParams);
			
			
			// Creazione interceptor
			ConnettoreHttpRequestInterceptor httpRequestInterceptor = null;
			if(this.debug) {
				this.recHeaderForInterceptor = new HashMap<>();
				httpRequestInterceptor = new ConnettoreHttpRequestInterceptor(this.logger, this.recHeaderForInterceptor);
			}
			
			
			// HttpMethod
			if(this.httpMethod==null){
				throw new ConnettoreException("HttpRequestMethod non definito");
			}
			this.httpRequest = ConnettoreHTTPCOREUtils.buildHttpRequest(this.httpMethod, this.url);
			if(this.httpRequest==null){
				throw new ConnettoreException("HttpRequest non definita ?");
			}
			
			
			
			// Tipologia di servizio
			MessageType requestMessageType = this.requestMsg.getMessageType();
			OpenSPCoop2SoapMessage soapMessageRequest = null;
			if(this.debug)
				this.logger.debug("Tipologia Servizio: "+this.requestMsg.getServiceBinding());
			if(this.isSoap){
				soapMessageRequest = this.requestMsg.castAsSoap();
			}
			
			
			// Alcune implementazioni richiedono di aggiornare il Content-Type
			this.requestMsg.updateContentType();
			
			
			
			
			
			// Proxy Authentication BASIC
			if(this.proxyType!=null && this.proxyUsername!=null){
				if(this.debug)
					this.logger.debug("Impostazione autenticazione per proxy (username["+this.proxyUsername+"] password["+this.proxyPassword+"]) ...");
				if(this.proxyUsername!=null && this.proxyPassword!=null){
					String authentication = this.proxyUsername + ":" + this.proxyPassword;
					authentication = HttpConstants.AUTHORIZATION_PREFIX_BASIC + Base64Utilities.encodeAsString(authentication.getBytes());
					setRequestHeader(HttpConstants.PROXY_AUTHORIZATION,authentication, propertiesTrasportoDebug);
					if(this.debug)
						this.logger.info("Impostazione autenticazione per proxy (username["+this.proxyUsername+"] password["+this.proxyPassword+"]) ["+authentication+"]",false);
				}
			}
			
			
			
			
			
			// Impostazione Content-Type della Spedizione su HTTP	        
			String contentTypeRichiesta = null;
			if(this.debug)
				this.logger.debug("Impostazione content type...");
			if(this.isSoap){
				if(this.sbustamentoSoap && soapMessageRequest.countAttachments()>0 && TunnelSoapUtils.isTunnelOpenSPCoopSoap(soapMessageRequest)){
					contentTypeRichiesta = TunnelSoapUtils.getContentTypeTunnelOpenSPCoopSoap(soapMessageRequest.getSOAPBody());
				}else{
					contentTypeRichiesta = this.requestMsg.getContentType();
				}
				if(contentTypeRichiesta==null){
					throw new ConnettoreException("Content-Type del messaggio da spedire non definito");
				}
			}
			else{
				contentTypeRichiesta = this.requestMsg.getContentType();
				// Content-Type non obbligatorio in REST
			}
			if(this.debug)
				this.logger.info("Impostazione http Content-Type ["+contentTypeRichiesta+"]",false);
			if(contentTypeRichiesta!=null){
				setRequestHeader(HttpConstants.CONTENT_TYPE,contentTypeRichiesta, propertiesTrasportoDebug);
			}
			
			
			
			// Impostazione transfer-length
			if(this.debug)
				this.logger.debug("Impostazione transfer-length...");
			boolean transferEncodingChunked = TransferLengthModes.TRANSFER_ENCODING_CHUNKED.equals(this.tlm);
			if(this.debug)
				this.logger.info("Impostazione transfer-length effettuata (chunkLength:"+this.chunkLength+"): "+this.tlm,false);
			
			
			
			// Impostazione timeout
			if(this.debug)
				this.logger.debug("Impostazione timeout...");
			if(this.properties.get(CostantiConnettori.CONNETTORE_READ_CONNECTION_TIMEOUT)!=null){
				try{
					this.readConnectionTimeout = Integer.parseInt(this.properties.get(CostantiConnettori.CONNETTORE_READ_CONNECTION_TIMEOUT));
					this.readConnectionTimeoutConfigurazioneGlobale = this.properties.containsKey(CostantiConnettori.CONNETTORE_READ_CONNECTION_TIMEOUT_GLOBALE);
				}catch(Exception e){
					this.logger.error("Parametro "+CostantiConnettori.CONNETTORE_READ_CONNECTION_TIMEOUT+" errato",e);
				}
			}
			if(this.readConnectionTimeout==-1){
				this.readConnectionTimeout = HttpUtilities.HTTP_READ_CONNECTION_TIMEOUT;
			}
			if(this.debug)
				this.logger.info("Impostazione http timeout CT["+this.connectionTimeout+"] RT["+this.readConnectionTimeout+"]",false);
			this.httpConnectionConfig.setReadTimeout(this.readConnectionTimeout);
			

			// Gestione automatica del redirect
			if(this.followRedirects) {
				if(gestioneRedirectTramiteLibrerieApache && !this.isRest) {
					this.httpConnectionConfig.setFollowRedirect(true);
					if(this.maxNumberRedirects>0) {
						this.httpConnectionConfig.setMaxNumberRedirects(this.maxNumberRedirects);
					}
				}
				else {
					this.httpConnectionConfig.setFollowRedirect(false);
				}
			}
			else {
				this.httpConnectionConfig.setFollowRedirect(false);
			}
			
			
			// Authentication BASIC
			if(this.debug)
				this.logger.debug("Impostazione autenticazione...");
			String user = null;
			String password = null;
			if(this.credenziali!=null){
				user = this.credenziali.getUser();
				password = this.credenziali.getPassword();
			}else{
				user = this.properties.get(CostantiConnettori.CONNETTORE_USERNAME);
				password = this.properties.get(CostantiConnettori.CONNETTORE_PASSWORD);
			}
			if(user!=null && password!=null){
				String authentication = user + ":" + password;
				authentication = HttpConstants.AUTHORIZATION_PREFIX_BASIC + Base64Utilities.encodeAsString(authentication.getBytes());
				setRequestHeader(HttpConstants.AUTHORIZATION,authentication, propertiesTrasportoDebug);
				if(this.debug) {
					this.logger.info("Impostazione autenticazione (username:"+user+" password:"+password+") ["+authentication+"]",false);
				}
			}

			
			// Authentication Token
			NameValue nv = this.getTokenHeader();
	    	if(nv!=null) {
	    		if(this.requestMsg!=null && this.requestMsg.getTransportRequestContext()!=null) {
	    			this.requestMsg.getTransportRequestContext().removeHeader(nv.getName()); // Fix: senno sovrascriveva il vecchio token
	    		}
	    		setRequestHeader(nv.getName(),nv.getValue(), propertiesTrasportoDebug);
	    		if(this.debug) {
					this.logger.info("Impostazione autenticazione token (header-name '"+nv.getName()+"' value '"+nv.getValue()+"')",false);
	    		}
	    	}
	    	
	    	    	
	    	// Authentication Api Key
			String apiKey = this.properties.get(CostantiConnettori.CONNETTORE_APIKEY);
			if(apiKey!=null && StringUtils.isNotEmpty(apiKey)){
				String apiKeyHeader = this.properties.get(CostantiConnettori.CONNETTORE_APIKEY_HEADER);
				if(apiKeyHeader==null || StringUtils.isEmpty(apiKeyHeader)) {
					apiKeyHeader = CostantiConnettori.DEFAULT_HEADER_API_KEY;
				}
				setRequestHeader(apiKeyHeader,apiKey, propertiesTrasportoDebug);
				if(this.debug)
					this.logger.info("Impostazione autenticazione api key ["+apiKeyHeader+"]=["+apiKey+"]",false);
				
				String appId = this.properties.get(CostantiConnettori.CONNETTORE_APIKEY_APPID);
				if(appId!=null && StringUtils.isNotEmpty(appId)){
					String appIdHeader = this.properties.get(CostantiConnettori.CONNETTORE_APIKEY_APPID_HEADER);
					if(appIdHeader==null || StringUtils.isEmpty(appIdHeader)) {
						appIdHeader = CostantiConnettori.DEFAULT_HEADER_APP_ID;
					}
					setRequestHeader(appIdHeader,appId, propertiesTrasportoDebug);
					if(this.debug)
						this.logger.info("Impostazione autenticazione api key (app id) ["+appIdHeader+"]=["+appId+"]",false);
				}
			}
	    	
	    	
			
	    	// ForwardProxy
	    	if(this.forwardProxyHeaderName!=null && this.forwardProxyHeaderValue!=null) {
	    		if(this.requestMsg!=null && this.requestMsg.getTransportRequestContext()!=null) {
	    			this.requestMsg.getTransportRequestContext().removeHeader(this.forwardProxyHeaderName); // Fix: senno sovrascriveva il vecchio token
	    		}
	    		setRequestHeader(this.forwardProxyHeaderName,this.forwardProxyHeaderValue, propertiesTrasportoDebug);
	    		if(this.debug) {
					this.logger.info("Impostazione ForwardProxy (header-name '"+this.forwardProxyHeaderName+"' value '"+this.forwardProxyHeaderValue+"')",false);
	    		}
	    	}

			
	    	
			// Impostazione Proprieta del trasporto
			if(this.debug)
				this.logger.debug("Impostazione header di trasporto...");
			this.forwardHttpRequestHeader();
			if(this.propertiesTrasporto != null){
				Iterator<String> keys = this.propertiesTrasporto.keySet().iterator();
				while (keys.hasNext()) {
					String key = keys.next();
					List<String> values = this.propertiesTrasporto.get(key);
					if(this.debug &&
			    		values!=null && !values.isEmpty()) {
		        		for (String value : values) {
		        			this.logger.info("Set Transport Header ["+key+"]=["+value+"]",false);
		        		}
			    	}
					
					if(this.encodingRFC2047){
						List<String> valuesEncoded = new ArrayList<>();
						if(values!=null && !values.isEmpty()) {
			        		for (String value : values) {
			        			if(!RFC2047Utilities.isAllCharactersInCharset(value, this.charsetRFC2047)){
									String encoded = RFC2047Utilities.encode(new String(value), this.charsetRFC2047, this.encodingAlgorithmRFC2047);
									/**System.out.println("@@@@ CODIFICA ["+value+"] in ["+encoded+"]");*/
									if(this.debug)
										this.logger.info("RFC2047 Encoded value in ["+encoded+"] (charset:"+this.charsetRFC2047+" encoding-algorithm:"+this.encodingAlgorithmRFC2047+")",false);
									valuesEncoded.add(encoded);
								}
								else{
									valuesEncoded.add(value);
								}
			        		}
						}
						setRequestHeader(this.validazioneHeaderRFC2047, key, valuesEncoded, this.logger, propertiesTrasportoDebug);
					}
					else{
						setRequestHeader(this.validazioneHeaderRFC2047, key, values, this.logger, propertiesTrasportoDebug);
					}
				}
			}
			
			

			// Aggiunga del SoapAction Header in caso di richiesta SOAP
			// spostato sotto il forwardHeader per consentire alle trasformazioni di modificarla
			if(this.isSoap && this.sbustamentoSoap == false){
				if(this.debug)
					this.logger.debug("Impostazione soap action...");
				boolean existsTransportProperties = false;
				if(TransportUtils.containsKey(this.propertiesTrasporto, Costanti.SOAP11_MANDATORY_HEADER_HTTP_SOAP_ACTION)){
					this.soapAction = TransportUtils.getFirstValue(this.propertiesTrasporto, Costanti.SOAP11_MANDATORY_HEADER_HTTP_SOAP_ACTION);
					existsTransportProperties = (this.soapAction!=null);
				}
				if(!existsTransportProperties) {
					this.soapAction = soapMessageRequest.getSoapAction();
				}
				if(this.soapAction==null){
					this.soapAction="\"OpenSPCoop\"";
				}
				if(MessageType.SOAP_11.equals(this.requestMsg.getMessageType()) && !existsTransportProperties){
					// NOTA non quotare la soap action, per mantenere la trasparenza della PdD
					setRequestHeader(Costanti.SOAP11_MANDATORY_HEADER_HTTP_SOAP_ACTION,this.soapAction, propertiesTrasportoDebug);
				}
				if(this.debug)
					this.logger.info("SOAP Action inviata ["+this.soapAction+"]",false);
			}
			
			
			
			
			
			// Impostazione Metodo
			if(this.debug)
				this.logger.info("Impostazione "+this.httpMethod+"...",false);
			HttpBodyParameters httpBody = new  HttpBodyParameters(this.httpMethod, contentTypeRichiesta);
			
			
			
			// Preparazione messaggio da spedire
			// Spedizione byte
			AsyncEntityProducer entityProducer = null;
			if(httpBody.isDoOutput()){
				boolean consumeRequestMessage = true;
				if(this.followRedirects && !gestioneRedirectTramiteLibrerieApache){
					consumeRequestMessage = false;
				}
				if(this.debug)
					this.logger.debug("Spedizione byte (consume-request-message:"+consumeRequestMessage+")...");
				boolean hasContentBuilded = false;
				boolean hasContent = false;
				OpenSPCoop2RestMessage<?> restMessage = null;
				AbstractOpenSPCoop2Message_soap_impl<?> soapMessage = null;
				boolean useMessageObjectEntity = this.openspcoopProperties.isNIOConfigAsyncClientUseCustomMessageObjectEntity();
				if(this.isRest) {
					restMessage = this.requestMsg.castAsRest();
					hasContent = restMessage.hasContent();
					hasContentBuilded = restMessage.isContentBuilded();
				}
				else if(useMessageObjectEntity && this.requestMsg instanceof AbstractOpenSPCoop2Message_soap_impl<?>) {
					soapMessage = (AbstractOpenSPCoop2Message_soap_impl<?>) this.requestMsg;
					hasContent = soapMessage.hasContent();
					hasContentBuilded = soapMessage.isContentBuilded();
				}
				
				if(
						(!transferEncodingChunked) ||  // se non è transferEncodingChunked, bisogna conoscere il content length
						this.isDumpBinarioRichiesta() || // dump abilitato o come debug o su db
						(this.isSoap && soapMessage==null) || // soap non ti dipo AbstractOpenSPCoop2Message_soap_impl (metodi hasContent e isContentBuilded non disponibili)
						hasContentBuilded || // contenuto della richiesta già in memoria
						!consumeRequestMessage // non devo consumare la richiesta
					) {
					this.cloasebleDumpBout = new DumpByteArrayOutputStream(this.dumpBinarioSoglia, this.dumpBinarioRepositoryFile, this.idTransazione, 
							TipoMessaggio.RICHIESTA_USCITA_DUMP_BINARIO.getValue());
					try {
						if(this.isSoap && this.sbustamentoSoap){
							if(this.debug)
								this.logger.debug("Sbustamento...");
							TunnelSoapUtils.sbustamentoMessaggio(soapMessageRequest,this.cloasebleDumpBout);
						}else{
							this.requestMsg.writeTo(this.cloasebleDumpBout, consumeRequestMessage);
						}
						this.cloasebleDumpBout.flush();
						this.cloasebleDumpBout.close();
						if(this.isDumpBinarioRichiesta()) {
							this.dumpBinarioRichiestaUscita(this.cloasebleDumpBout, requestMessageType, contentTypeRichiesta, this.location, propertiesTrasportoDebug);
						}
						
						String baseMimeType = ContentTypeUtilities.readBaseTypeFromContentType(contentTypeRichiesta);
						org.apache.hc.core5.http.ContentType ct = org.apache.hc.core5.http.ContentType.create(baseMimeType);
						if(this.cloasebleDumpBout.isSerializedOnFileSystem()) {
							entityProducer = new FileEntityProducer(this.cloasebleDumpBout.getSerializedFile(), ct, transferEncodingChunked);
						}
						else {
							entityProducer = new BasicAsyncEntityProducer(this.cloasebleDumpBout.toByteArray(), ct, transferEncodingChunked);
						}						
					}finally {
						if(!this.cloasebleDumpBout.isSerializedOnFileSystem()) { // sennò devo rilasciare il file dopo aver fatto la send
							try {
								this.cloasebleDumpBout.clearResources();
							}catch(Exception t) {
								this.logger.error(MSG_RELEASE_RESOURCES_FAILED+t.getMessage(),t);
							}finally {
								this.cloasebleDumpBout = null;
							}
						}
					}
				}
				else {
					// Siamo per forza rest o soap con contenuto non costruito
					if(hasContent) {
						String baseMimeType = ContentTypeUtilities.readBaseTypeFromContentType(contentTypeRichiesta);
						org.apache.hc.core5.http.ContentType ct = org.apache.hc.core5.http.ContentType.create(baseMimeType);
						
						if(useMessageObjectEntity) {
							entityProducer = new ConnettoreHTTPCOREMessageObjectEntityProducer(this.requestMsg, consumeRequestMessage, ct, null, this.logger);
						}
						else {
							InputStream isRequest = null;
							if(this.isRest) {
								isRequest = restMessage.getInputStream();
							}
							else {
								throw new ConnettoreException("InputStream non accessibile per messaggio soap");
							}						
							entityProducer = new ConnettoreHTTPCOREInputStreamEntityProducer(isRequest, ct, null, this.logger);
						}
					}
				}
			}
			else {
				if(this.isDumpBinarioRichiesta()) {
					// devo registrare almeno gli header HTTP
					this.dumpBinarioRichiestaUscita(null, null, null, this.location, propertiesTrasportoDebug);
				}
			}
			
			
			// Creazione Connessione
			// Proxy AUTH
			if(this.proxyType==null){
				if(this.debug)
					this.logger.info("Predispongo parametri per connessione alla URL ["+this.location+"]...",false);
			}
			else {
				if(this.debug) {
					this.logger.info("Predispongo parametri per connessione alla URL ["+this.location+"] (via proxy "+
						this.proxyHostname+":"+this.proxyPort+") (username["+this.proxyUsername+"] psw["+this.proxyPassword+"])...",false);
				}
				this.httpConnectionConfig.setProxyHost(this.proxyHostname);
				this.httpConnectionConfig.setProxyPort(this.proxyPort);
			}
			ConnettoreHTTPCOREConnection conn = ConnettoreHTTPCOREConnectionManager.getConnettoreHTTPCOREConnection(this.httpConnectionConfig, 
					this.loader, this.logger, 
					this.requestInfo,
					keepAliveStrategy, httpRequestInterceptor);
			CloseableHttpAsyncClient httpClient = conn.getHttpclient();
			
			// Imposto Configurazione
			this.httpRequest.setConfig(conn.getRequestConfig());
			
			
				
			// Spedizione byte
			if(this.debug)
				this.logger.debug("NIOV - Predisposizione callback...");
			ConnettoreHTTPCOREResponseCallback responseCallback = new ConnettoreHTTPCOREResponseCallback(this, request, httpBody);
			/**System.out.println("CLIENT ["+httpClient.getHttpclient().getClass().getName()+"]");*/
			ConnettoreHTTPCORETransactionThreadContextSwitchEntityProducer<?> producer = null;
			if(entityProducer!=null) {
				producer = new ConnettoreHTTPCORETransactionThreadContextSwitchEntityProducer<>(this, entityProducer);
			}
			AsyncRequestProducer requestProducer = new BasicRequestProducer(this.httpRequest, producer);
			AsyncResponseConsumer<ConnettoreHTTPCOREResponse> responseConsumer = null;
			boolean stream = OpenSPCoop2Properties.getInstance().isNIOConfigAsyncResponseStreamEnabled();
			int dimensioneBuffer = OpenSPCoop2Properties.getInstance().getNIOConfigAsyncResponsePipedUnblockedStreamBuffer();
			if(stream) {
				boolean delegata = !ConsegnaContenutiApplicativi.ID_MODULO.equals(this.idModulo);
				responseConsumer = new ConnettoreHTTPCOREInputStreamEntityConsumer(this.httpMethod,this.logger, dimensioneBuffer, this.readConnectionTimeout, delegata);	
			}
			else {
				responseConsumer = new ConnettoreHTTPCOREExtendAbstractBinResponseConsumer();
			}
			this.removeThreadLocalContext("ConnettoreHTTPRequest", BooleanNullable.TRUE());
			httpClient.execute(requestProducer, responseConsumer, HttpClientContext.create(), responseCallback);
						
			if(this.debug) {
				this.logger.debug("NIO - Terminata gestione richiesta");
			}
			
			this.asyncWait = true;
			return true; // stato ritornato ignorato, verra' impostato dalla response callback

		}  catch(Exception e){ 
			this.eccezioneProcessamento = e;
			String msgErrore = this.readExceptionMessageFromException(e);
			if(this.generateErrorWithConnectorPrefix) {
				this.errore = "Errore avvenuto durante la consegna HTTP: "+msgErrore;
			}
			else {
				this.errore = msgErrore;
			}
			this.logger.error("Errore avvenuto durante la consegna HTTP: "+msgErrore,e);
			
			this.processConnectionTimeoutException(this.connectionTimeout, this.connectionTimeoutConfigurazioneGlobale, e, msgErrore);
			
			this.processReadTimeoutException(this.readConnectionTimeout, this.readConnectionTimeoutConfigurazioneGlobale, e, msgErrore);
			
			return false;
		} finally {
			// se per caso non l'ho ancora chiuso lo faccio
			if(this.cloasebleDumpBout!=null) { 
				try {
					this.cloasebleDumpBout.clearResources();
					this.cloasebleDumpBout = null;
				}catch(Exception t) {
					this.logger.error(MSG_RELEASE_RESOURCES_FAILED+t.getMessage(),t);
				}
			}
		}
	}
	
	public void checkThreadLocalContext(String function, BooleanNullable switchThreadLocalContextDoneHolder) throws IOException {
		String tName = Thread.currentThread().getName();
		if(function!=null && this.entryPointThreadName!=null) {
			// debug
		}
		/**System.out.println("("+function+")("+this.idTransazione+") SWITCH THREAD CONTEXT DA [entry:"+this.entryPointThreadName+"][actual:"+this.actualThreadName+"] -> ["+tName+"] ...");*/
		boolean switchThreadLocalContextDone = false;
		if(switchThreadLocalContextDoneHolder!=null && switchThreadLocalContextDoneHolder.getValue()!=null) {
			switchThreadLocalContextDone = switchThreadLocalContextDoneHolder.getValue().booleanValue();
		}
		if(this.transactionNullable!=null && this.idTransazione!=null && !switchThreadLocalContextDone &&
			!tName.equals(this.actualThreadName)) {
			try {
				// lo aggiungo al thread della libreria httpasync NIO
				/**System.out.println("("+function+")("+this.idTransazione+") SWITCH THREAD CONTEXT DA [entry:"+this.entryPointThreadName+"][actual:"+this.actualThreadName+"] -> ["+tName+"] OK");*/
				TransactionContext.setTransactionThreadLocal(this.idTransazione, this.transactionNullable);
				if(switchThreadLocalContextDoneHolder!=null) {
					switchThreadLocalContextDoneHolder.setValue(true);
				}
				this.actualThreadName = tName;
			}catch(Exception e) {
				this.logger.error("checkThreadLocalContext failed: "+e.getMessage(),e);
				throw new IOException(e.getMessage(),e);
			}
		}
	}
	public void removeThreadLocalContext(String function, BooleanNullable switchThreadLocalContextDoneHolder) {
		String tName = Thread.currentThread().getName();
		if(tName!=null && function!=null) {
			// debug
		}
		/**System.out.println("("+function+")("+this.idTransazione+") REMOVE THREAD CONTEXT in ["+tName+"] ...");*/
		boolean switchThreadLocalContextDone = false;
		if(switchThreadLocalContextDoneHolder!=null && switchThreadLocalContextDoneHolder.getValue()!=null) {
			switchThreadLocalContextDone = switchThreadLocalContextDoneHolder.getValue().booleanValue();
		}
		if(this.transactionNullable!=null && this.idTransazione!=null && switchThreadLocalContextDone) {
			// lo rimuovo dal thread della libreria httpasync NIO
			/**System.out.println("("+function+")("+this.idTransazione+") REMOVE THREAD CONTEXT in ["+tName+"] OK");*/
			TransactionContext.removeTransaction(this.idTransazione);
			switchThreadLocalContextDoneHolder.setValue(false);
		}
	}
	
    /**
     * Effettua la disconnessione 
     */
    @Override
	public void disconnect() throws ConnettoreException{
    	try{
    	
			// Gestione finale della connessione
	    	if(this.isResponse!=null){
	    		if(this.debug && this.logger!=null)
	    			this.logger.debug("Chiusura socket...");
				this.isResponse.close();
			}
		    	
	    	// super.disconnect (Per risorse base)
	    	super.disconnect();
			
    	}catch(Exception e){
    		throw new ConnettoreException("Chiusura connessione non riuscita: "+e.getMessage(),e);
    	}
    }
    
    private Map<String,List<String>> recHeaderForInterceptor = null;
    @Override
	protected void setRequestHeader(String key, List<String> values) throws ConnettoreException {
    	if(values!=null && !values.isEmpty()) {
    		for (String value : values) {
    			this.httpRequest.addHeader(key,value);
    			if(this.recHeaderForInterceptor!=null) {
    				this.recHeaderForInterceptor.put(key,values);
    			}	
			}
    	}
    }
}