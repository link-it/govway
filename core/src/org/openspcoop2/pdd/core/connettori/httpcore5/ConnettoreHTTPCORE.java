/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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

package org.openspcoop2.pdd.core.connettori.httpcore5;

import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;

import org.apache.commons.lang.StringUtils;
import org.apache.hc.client5.http.ConnectionKeepAliveStrategy;
import org.apache.hc.client5.http.HttpRoute;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.classic.methods.HttpDelete;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpHead;
import org.apache.hc.client5.http.classic.methods.HttpOptions;
import org.apache.hc.client5.http.classic.methods.HttpPatch;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.client5.http.classic.methods.HttpTrace;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.socket.ConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.DefaultHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.ConnectionReuseStrategy;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.config.Registry;
import org.apache.hc.core5.http.config.RegistryBuilder;
import org.apache.hc.core5.http.impl.DefaultConnectionReuseStrategy;
import org.apache.hc.core5.http.io.entity.ByteArrayEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.FileEntity;
import org.apache.hc.core5.http.io.entity.InputStreamEntity;
import org.apache.hc.core5.pool.PoolStats;
import org.apache.hc.core5.util.TimeValue;
import org.openspcoop2.core.constants.CostantiConnettori;
import org.openspcoop2.core.constants.TransferLengthModes;
import org.openspcoop2.core.transazioni.constants.TipoMessaggio;
import org.openspcoop2.message.OpenSPCoop2RestMessage;
import org.openspcoop2.message.OpenSPCoop2SoapMessage;
import org.openspcoop2.message.constants.Costanti;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.soap.AbstractOpenSPCoop2Message_soap_impl;
import org.openspcoop2.message.soap.TunnelSoapUtils;
import org.openspcoop2.message.utils.MessageWriteToRunnable;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.core.connettori.ConnettoreException;
import org.openspcoop2.pdd.core.connettori.ConnettoreExtBaseHTTP;
import org.openspcoop2.pdd.core.connettori.ConnettoreMsg;
import org.openspcoop2.pdd.mdb.ConsegnaContenutiApplicativi;
import org.openspcoop2.pdd.services.connector.ConnectorApplicativeThreadPool;
import org.openspcoop2.utils.NameValue;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.io.Base64Utilities;
import org.openspcoop2.utils.io.DumpByteArrayOutputStream;
import org.openspcoop2.utils.io.notifier.unblocked.IPipedUnblockedStream;
import org.openspcoop2.utils.io.notifier.unblocked.PipedUnblockedOutputStream;
import org.openspcoop2.utils.io.notifier.unblocked.PipedUnblockedStreamFactory;
import org.openspcoop2.utils.transport.TransportUtils;
import org.openspcoop2.utils.transport.http.ContentTypeUtilities;
import org.openspcoop2.utils.transport.http.HttpBodyParameters;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpUtilities;
import org.openspcoop2.utils.transport.http.RFC2047Utilities;
import org.openspcoop2.utils.transport.http.SSLUtilities;
import org.openspcoop2.utils.transport.http.WrappedLogSSLSocketFactory;

/**
 * Connettore che utilizza la libreria httpcore5
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConnettoreHTTPCORE extends ConnettoreExtBaseHTTP {

	public static final String ENDPOINT_TYPE = "httpcore";
	
	
	private static final boolean USE_POOL = true;
	private static boolean gestioneRedirectTramiteLibrerieApache = false; // con l'implementazione di govway, vengono registrati gli hop nei diagnostici 
	
	private HttpEntity httpEntityResponse = null;
		
	private HttpUriRequestBase httpRequest;
	
	@SuppressWarnings("unused")
	private CloseableHttpClient cloasebleHttpClient;
	
	private DumpByteArrayOutputStream cloasebleDumpBout;
	
	/* Costruttori */
	public ConnettoreHTTPCORE(){
		super();
	}
	public ConnettoreHTTPCORE(boolean https){
		super(https);
	}
	
	private static final boolean THREAD_CHECK_POOL = false;
	private static void setThreadCheckPool(PoolingHttpClientConnectionManager connectionManager) {
		ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
		executorService.scheduleAtFixedRate(() -> {
			
			System.out.println("\n\n\nPRIMA");
			print(connectionManager);
						
		    connectionManager.closeExpired();
		    connectionManager.closeIdle(TimeValue.ofSeconds(30));
		    
		    System.out.println("DOPO");
			print(connectionManager);
		    
		    
		}, 30, 30, TimeUnit.SECONDS);
	}
	private static void print(PoolingHttpClientConnectionManager connectionManager) {
		// Statistiche totali
	    PoolStats totalStats = connectionManager.getTotalStats();
	    System.out.println("  Totali - In uso: " + totalStats.getLeased() + "; Disponibili: " +
	                       totalStats.getAvailable() + "; In attesa: " + totalStats.getPending());

	    // Ottieni tutte le rotte gestite dal connection manager
	    Set<HttpRoute> routes = connectionManager.getRoutes();

	    for (HttpRoute route : routes) {
	    	PoolStats routeStats = connectionManager.getStats(route);
	        System.out.println("  Rotta: " + route + " - In uso: " + routeStats.getLeased() +
	                           "; Disponibili: " + routeStats.getAvailable() +
	                           "; In attesa: " + routeStats.getPending());
	    }
	}
	
	private static Map<String, PoolingHttpClientConnectionManager> cmMap = new HashMap<>();
	private static synchronized void initialize(String key, SSLConnectionSocketFactory sslConnectionSocketFactory, ConnettoreHttpPoolParams poolParams, long connectionTimeout){
		if(!ConnettoreHTTPCORE.cmMap.containsKey(key)){
						
			PoolingHttpClientConnectionManager cm = null;
			if(sslConnectionSocketFactory!=null) {
				Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder
				        .<ConnectionSocketFactory> create().register("https", sslConnectionSocketFactory)
				        .build();
				cm = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
			}
			else {
				cm = new PoolingHttpClientConnectionManager();
			}
			
			// Increase max total connection
			cm.setMaxTotal(poolParams.getMaxTotal());
			// Increase default max connection per route
			cm.setDefaultMaxPerRoute(poolParams.getDefaultMaxPerRoute());
			// Increase max connections for localhost:80
			/**HttpHost localhost = new HttpHost("locahost", 80);
			cm.setMaxPerRoute(new HttpRoute(localhost), 50);*/
			 
			ConnectionConfig.Builder buider = ConnectionConfig.custom();
			buider.setConnectTimeout(connectionTimeout, TimeUnit.MILLISECONDS);
			/**
			 * specifica l'intervallo di tempo di inattività dopo il quale le connessioni persistenti dovrebbero essere convalidate prima di essere riutilizzate. 
			 * Questo è importante per evitare l'uso di connessioni che potrebbero essere state chiuse dal server o interrotte per altri motivi.
			 * Un parametro troppo basso, a meno che non ci sia una necessità specifica, non va usato poiché ciò potrebbe introdurre un overhead significativo e influire sulle prestazioni complessive dell'applicazione.
			 **/
			if(poolParams.getValidateAfterInactivity()!=null) {
				buider.setValidateAfterInactivity(poolParams.getValidateAfterInactivity().intValue(), TimeUnit.MILLISECONDS);
			}
			ConnectionConfig config = buider.build();
			cm.setDefaultConnectionConfig(config);
			
			if(THREAD_CHECK_POOL) {
				setThreadCheckPool(cm);
			}
			
			ConnettoreHTTPCORE.cmMap.put(key, cm);
		}
	}
	
	private CloseableHttpClient buildHttpClient(ConnettoreHttpPoolParams poolParams, long connectionTimeout, 
			ConnectionKeepAliveStrategy keepAliveStrategy, SSLSocketFactory sslSocketFactory, boolean usePool,
			ConnettoreHttpRequestInterceptor httpRequestInterceptor) throws UtilsException{
		
		HttpClientBuilder httpClientBuilder = HttpClients.custom();
			
		// Imposta Contesto SSL se attivo
		
		String prefixCT = "connTimeout["+connectionTimeout+"] ";
		
		String prefixPoolParams = " "+poolParams.toString()+ " ";
		
		String key = null;
		if(this.sslContextProperties!=null){
			key = prefixCT + prefixPoolParams + "https " +this.sslContextProperties.toString();
		}
		else {
			key = prefixCT + prefixPoolParams + "http";
		}
		
		SSLConnectionSocketFactory sslConnectionSocketFactory = null;
		if(this.sslContextProperties!=null && 
				(!usePool || !ConnettoreHTTPCORE.cmMap.containsKey(key))){
			if(this.debug) {
				String clientCertificateConfigurated = this.sslContextProperties.getKeyStoreLocation();
				sslSocketFactory = new WrappedLogSSLSocketFactory(sslSocketFactory, 
						this.logger.getLogger(), this.logger.buildMsg(""),
						clientCertificateConfigurated);
			}		
			
			StringBuilder bfLog = new StringBuilder();
			HostnameVerifier hostnameVerifier = SSLUtilities.generateHostnameVerifier(this.sslContextProperties, bfLog, 
					this.logger.getLogger(), this.loader);
			if(this.debug)
				this.logger.debug(bfLog.toString());
			
			if(hostnameVerifier==null) {
				hostnameVerifier = new DefaultHostnameVerifier();
			}
			sslConnectionSocketFactory = new SSLConnectionSocketFactory(sslSocketFactory, hostnameVerifier);
		}
		
		if(usePool) {
						
			// Caso con pool
			if(!ConnettoreHTTPCORE.cmMap.containsKey(key)){
				ConnettoreHTTPCORE.initialize(key, sslConnectionSocketFactory, poolParams, connectionTimeout);
			}
			
			PoolingHttpClientConnectionManager cm = ConnettoreHTTPCORE.cmMap.get(key);
			
			/**System.out.println("-----GET CONNECTION [START] ----");
			System.out.println("PRIMA CLOSE AVAILABLE["+cm.getTotalStats().getAvailable()+"] LEASED["
					+cm.getTotalStats().getLeased()+"] MAX["+cm.getTotalStats().getMax()+"] PENDING["+cm.getTotalStats().getPending()+"]");
			 BLOCKED ConnettoreHTTPCORE.cm.closeExpiredConnections();
			 BLOCKED ConnettoreHTTPCORE.cm.closeIdleConnections(30, java.util.concurrent.TimeUnit.SECONDS);
			System.out.println("DOPO CLOSE AVAILABLE["+cm.getTotalStats().getAvailable()+"] LEASED["
					+cm.getTotalStats().getLeased()+"] MAX["+cm.getTotalStats().getMax()+"] PENDING["+cm.getTotalStats().getPending()+"]");*/
			httpClientBuilder.setConnectionManager(cm);
		}
		else {
			/**if(sslConnectionSocketFactory!=null) {
				httpClientBuilder.setSSLSocketFactory(sslConnectionSocketFactory);		
			}*/
		}
		
		ConnectionReuseStrategy defaultClientConnectionReuseStrategy = new DefaultConnectionReuseStrategy();
		httpClientBuilder.setConnectionReuseStrategy(defaultClientConnectionReuseStrategy);
		
		if(keepAliveStrategy!=null){
			httpClientBuilder.setKeepAliveStrategy(keepAliveStrategy);
		}
				
		/**System.out.println("PRESA LA CONNESSIONE AVAILABLE["+cm.getTotalStats().getAvailable()+"] LEASED["
				+cm.getTotalStats().getLeased()+"] MAX["+cm.getTotalStats().getMax()+"] PENDING["+cm.getTotalStats().getPending()+"]");
		System.out.println("-----GET CONNECTION [END] ----");*/
		
		if(httpRequestInterceptor!=null) {
			httpClientBuilder.addRequestInterceptorLast(httpRequestInterceptor);
		}
		
		return httpClientBuilder.build();
	}
	
	
	@Override
	protected boolean sendHTTP(ConnettoreMsg request) {
		
		int connectionTimeout = -1;
		boolean connectionTimeoutConfigurazioneGlobale = true;
		int readConnectionTimeout = -1;
		boolean readConnectionTimeoutConfigurazioneGlobale = true;
		try{
			
			// Creazione URL
			if(this.debug)
				this.logger.debug("Creazione URL...");
			this.buildLocation();		
			if(this.debug)
				this.logger.debug("Creazione URL ["+this.location+"]...");
			URL url = new URI( this.location ).toURL();	
			
			
			// Keep-alive
			ConnectionKeepAliveStrategy keepAliveStrategy = null; /**new CustomKeepAliveStrategy();*/
			
			
			
			// Collezione header di trasporto per dump
			Map<String, List<String>> propertiesTrasportoDebug = null;
			if(this.isDumpBinarioRichiesta()) {
				propertiesTrasportoDebug = new HashMap<>();
			}

			
			// Lettura connectionTimeout
			if(this.properties.get(CostantiConnettori.CONNETTORE_CONNECTION_TIMEOUT)!=null){
				try{
					connectionTimeout = Integer.parseInt(this.properties.get(CostantiConnettori.CONNETTORE_CONNECTION_TIMEOUT));
					connectionTimeoutConfigurazioneGlobale = this.properties.containsKey(CostantiConnettori.CONNETTORE_CONNECTION_TIMEOUT_GLOBALE);
				}catch(Exception e){
					this.logger.error("Parametro '"+CostantiConnettori.CONNETTORE_CONNECTION_TIMEOUT+"' errato",e);
				}
			}
			if(connectionTimeout==-1){
				connectionTimeout = HttpUtilities.HTTP_CONNECTION_TIMEOUT;
			}
			
			// Lettura Parametri Pool
			ConnettoreHttpPoolParams poolParams = new ConnettoreHttpPoolParams(this.openspcoopProperties, url, this.proprietaPorta);
			
			// Creazione interceptor
			ConnettoreHttpRequestInterceptor httpRequestInterceptor = null;
			if(this.debug) {
				this.recHeaderForInterceptor = new HashMap<>();
				httpRequestInterceptor = new ConnettoreHttpRequestInterceptor(this.logger, this.recHeaderForInterceptor);
			}
			
			// Creazione Connessione
			if(this.debug)
				this.logger.info("Creazione connessione alla URL ["+this.location+"]...",false);
			HttpClient httpClient = buildHttpClient(poolParams, connectionTimeout, keepAliveStrategy, buildSSLContextFactory(), 
					ConnettoreHTTPCORE.USE_POOL,
					httpRequestInterceptor);
			
			// HttpMethod
			if(this.httpMethod==null){
				throw new ConnettoreException("HttpRequestMethod non definito");
			}
			this.httpRequest = null;
			switch (this.httpMethod) {
				case GET:
					this.httpRequest = new HttpGet(url.toString());
					break;
				case DELETE:
					this.httpRequest = new HttpDelete(url.toString());
					break;
				case HEAD:
					this.httpRequest = new HttpHead(url.toString());
					break;
				case POST:
					this.httpRequest = new HttpPost(url.toString());
					break;
				case PUT:
					this.httpRequest = new HttpPut(url.toString());
					break;
				case OPTIONS:
					this.httpRequest = new HttpOptions(url.toString());
					break;
				case TRACE:
					this.httpRequest = new HttpTrace(url.toString());
					break;
				case PATCH:
					this.httpRequest = new HttpPatch(url.toString());
					break;	
				default:
					this.httpRequest = new CustomHttpCoreEntity(this.httpMethod, url.toString());
					break;
			}
			if(this.httpMethod==null){
				throw new ConnettoreException("HttpRequest non definito ?");
			}
			RequestConfig.Builder requestConfigBuilder = RequestConfig.custom();
			
			
			
			// Tipologia di servizio
			OpenSPCoop2SoapMessage soapMessageRequest = null;
			MessageType requestMessageType = this.requestMsg.getMessageType();
			if(this.debug)
				this.logger.debug("Tipologia Servizio: "+this.requestMsg.getServiceBinding());
			if(this.isSoap){
				soapMessageRequest = this.requestMsg.castAsSoap();
			}
			
			
			
			// Alcune implementazioni richiedono di aggiornare il Content-Type
			this.requestMsg.updateContentType();
			
						
			
			// Impostazione Content-Type della Spedizione su HTTP
	        
			if(this.debug)
				this.logger.debug("Impostazione content type...");
			String contentTypeRichiesta = null;
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
				this.setRequestHeader(HttpConstants.CONTENT_TYPE, contentTypeRichiesta, propertiesTrasportoDebug);
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
					readConnectionTimeout = Integer.parseInt(this.properties.get(CostantiConnettori.CONNETTORE_READ_CONNECTION_TIMEOUT));
					readConnectionTimeoutConfigurazioneGlobale = this.properties.containsKey(CostantiConnettori.CONNETTORE_READ_CONNECTION_TIMEOUT_GLOBALE);
				}catch(Exception e){
					this.logger.error("Parametro '"+CostantiConnettori.CONNETTORE_READ_CONNECTION_TIMEOUT+"' errato",e);
				}
			}
			if(readConnectionTimeout==-1){
				readConnectionTimeout = HttpUtilities.HTTP_READ_CONNECTION_TIMEOUT;
			}
			if(this.debug)
				this.logger.info("Impostazione http timeout CT["+connectionTimeout+"] RT["+readConnectionTimeout+"]",false);
			requestConfigBuilder.setConnectionRequestTimeout(connectionTimeout, TimeUnit.MILLISECONDS); // quanto tempo il client aspetterà per ottenere una connessione dal pool.
			/** requestConfigBuilder.setConnectTimeout(connectionTimeout, TimeUnit.MILLISECONDS); spostato in initialize della connnection */
			requestConfigBuilder.setResponseTimeout(readConnectionTimeout, TimeUnit.MILLISECONDS);
			


			
			
			
			// Gestione automatica del redirect
			if(this.followRedirects) {
				if(gestioneRedirectTramiteLibrerieApache && !this.isRest) {
					requestConfigBuilder.setRedirectsEnabled(true);
					requestConfigBuilder.setCircularRedirectsAllowed(true);
					if(this.maxNumberRedirects>0) {
						requestConfigBuilder.setMaxRedirects(this.maxNumberRedirects);
					}
				}
				else {
					requestConfigBuilder.setRedirectsEnabled(false);
				}
			}
			else {
				requestConfigBuilder.setRedirectsEnabled(false);
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
				this.setRequestHeader(HttpConstants.AUTHORIZATION,authentication, propertiesTrasportoDebug);
				if(this.debug)
					this.logger.info("Impostazione autenticazione (username:"+user+" password:"+password+") ["+authentication+"]",false);
			}
			
			
			
			// Authentication Token
			NameValue nv = this.getTokenHeader();
	    	if(nv!=null) {
	    		if(this.requestMsg!=null && this.requestMsg.getTransportRequestContext()!=null) {
	    			this.requestMsg.getTransportRequestContext().removeHeader(nv.getName()); // Fix: senno sovrascriveva il vecchio token
	    		}
	    		this.setRequestHeader(nv.getName(),nv.getValue(), propertiesTrasportoDebug);
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
	    	if(this.forwardProxy_headerName!=null && this.forwardProxy_headerValue!=null) {
	    		if(this.requestMsg!=null && this.requestMsg.getTransportRequestContext()!=null) {
	    			this.requestMsg.getTransportRequestContext().removeHeader(this.forwardProxy_headerName); // Fix: senno sovrascriveva il vecchio token
	    		}
	    		setRequestHeader(this.forwardProxy_headerName,this.forwardProxy_headerValue, propertiesTrasportoDebug);
	    		if(this.debug) {
					this.logger.info("Impostazione ForwardProxy (header-name '"+this.forwardProxy_headerName+"' value '"+this.forwardProxy_headerValue+"')",false);
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
									String encoded = RFC2047Utilities.encode(value+"", this.charsetRFC2047, this.encodingAlgorithmRFC2047);
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
						this.setRequestHeader(this.validazioneHeaderRFC2047, key, values, this.logger, propertiesTrasportoDebug);
					}
				}
			}
			
			
			
			
			
			
			
			
			
			// Aggiunga del SoapAction Header in caso di richiesta SOAP
			// spostato sotto il forwardHeader per consentire alle trasformazioni di modificarla
			if(this.isSoap && !this.sbustamentoSoap){
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
					this.setRequestHeader(Costanti.SOAP11_MANDATORY_HEADER_HTTP_SOAP_ACTION,this.soapAction, propertiesTrasportoDebug);
				}
				if(this.debug)
					this.logger.info("SOAP Action inviata ["+this.soapAction+"]",false);
			}
			
			
			
			
			
			// Impostazione Metodo
			HttpBodyParameters httpBody = new  HttpBodyParameters(this.httpMethod, contentTypeRichiesta);
			
			
			
			// Preparazione messaggio da spedire
			// Spedizione byte
			MessageWriteToRunnable messageWriteToRunnable = null;
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
				if(this.isRest) {
					restMessage = this.requestMsg.castAsRest();
					hasContent = restMessage.hasContent();
					hasContentBuilded = restMessage.isContentBuilded();
				}
				else if(this.requestMsg instanceof AbstractOpenSPCoop2Message_soap_impl<?>) {
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
					this.cloasebleDumpBout = new DumpByteArrayOutputStream(this.dumpBinario_soglia, this.dumpBinario_repositoryFile, this.idTransazione, 
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
						
						HttpEntity httpEntity = null;
						String baseMimeType = ContentTypeUtilities.readBaseTypeFromContentType(contentTypeRichiesta);
						org.apache.hc.core5.http.ContentType ct = org.apache.hc.core5.http.ContentType.create(baseMimeType);
						if(this.cloasebleDumpBout.isSerializedOnFileSystem()) {
							if(transferEncodingChunked) {
								httpEntity = new FileStreamingEntity(this.cloasebleDumpBout.getSerializedFile(), ct, null);
							}
							else {
								httpEntity = new FileEntity(this.cloasebleDumpBout.getSerializedFile(), ct);
							}
						}
						else {
							httpEntity = new ByteArrayEntity(this.cloasebleDumpBout.toByteArray(), ct, transferEncodingChunked);
						}
						this.httpRequest.setEntity(httpEntity);
					}finally {
						if(!this.cloasebleDumpBout.isSerializedOnFileSystem()) { // sennò devo rilasciare il file dopo aver fatto la send
							try {
								this.cloasebleDumpBout.clearResources();
							}catch(Exception t) {
								this.logger.error("Release resources failed: "+t.getMessage(),t);
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
												
						boolean useMessageObjectEntity = this.openspcoopProperties.isBIOConfigSyncClientUseCustomMessageObjectEntity();
						if(useMessageObjectEntity) {
							MessageObjectEntity moe = new MessageObjectEntity(this.requestMsg, consumeRequestMessage, ct, null);
							this.httpRequest.setEntity(moe);
						}
						else {
							InputStream isRequest = null;
							if(this.isRest) {
								isRequest = restMessage.getInputStream();
							}
							else {
								int requestReadTimeout = readConnectionTimeout; // uso lo stesso tempo impostato per l'attesa della risposta 
								if(this.getPddContext()!=null && this.getPddContext().containsKey(CostantiPdD.REQUEST_READ_TIMEOUT)) {
									Object o = this.getPddContext().get(CostantiPdD.REQUEST_READ_TIMEOUT);
									if(o instanceof Integer t) {
										requestReadTimeout = t;
									}
								}
								requestReadTimeout = requestReadTimeout + 1000; // aggiungo un ulteriore secondo per far scattare prima il timeout sull'input stream
								isRequest = PipedUnblockedStreamFactory.newPipedUnblockedStream(this.logger.getLogger(), this.openspcoopProperties.getBIOConfigSyncClientPipedUnblockedStreamBuffer(), 
										requestReadTimeout, "Request");
								PipedUnblockedOutputStream puos = new PipedUnblockedOutputStream((IPipedUnblockedStream) isRequest);
								messageWriteToRunnable = new MessageWriteToRunnable(this.logger.getLogger(), this.requestMsg, puos, consumeRequestMessage);
								ConnectorApplicativeThreadPool.executeInSyncRequestPool(messageWriteToRunnable); // la scrittura si fermerà su buffer e poi prenderà a funzionare mentre viene letto l'input stream dal connettore httpcore
							}
							HttpEntity httpEntity = new InputStreamEntity(isRequest, ct);
							this.httpRequest.setEntity(httpEntity);
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
			
			
			// Imposto Configurazione
			this.httpRequest.setConfig(requestConfigBuilder.build());
			
			
			
			// Spedizione byte
			if(this.debug)
				this.logger.debug("Spedizione byte...");
			// Eseguo la richiesta e prendo la risposta
			HttpHost httpHost = HttpHost.create(this.httpRequest.getUri());
			/** DEPRECATO: ClassicHttpResponse httpResponse = (ClassicHttpResponse) httpClient.execute(this.httpRequest); */
			ClassicHttpResponse httpResponse = httpClient.executeOpen(httpHost, this.httpRequest, 
					null); //new BasicHttpContext()); the context to use for the execution, or {@code null} to use the default context
			if(this.cloasebleDumpBout!=null) { 
				try {
					this.cloasebleDumpBout.clearResources();
				}catch(Exception t) {
					this.logger.error("Release resources failed: "+t.getMessage(),t);
				}finally {
					this.cloasebleDumpBout = null;	
				}
			}
			if(messageWriteToRunnable!=null && messageWriteToRunnable.getException()!=null) {
				throw messageWriteToRunnable.getException();
			}
			this.httpEntityResponse = httpResponse.getEntity();
			
			
			
			if(this.debug)
				this.logger.debug("Analisi risposta...");
			Header [] hdrRisposta = httpResponse.getHeaders();
			Map<String, List<String>> mapHeaderHttpResponse = new HashMap<>();
			if(hdrRisposta!=null){
				for (int i = 0; i < hdrRisposta.length; i++) {
					
					String key = null;
					String value = null;
					
					if(hdrRisposta[i].getName()==null){
						// Check per evitare la coppia che ha come chiave null e come valore HTTP OK 200
						if(this.debug)
							this.logger.debug("HTTP risposta ["+HttpConstants.RETURN_CODE+"] ["+hdrRisposta[i].getValue()+"]...");
						key = HttpConstants.RETURN_CODE;
						value = hdrRisposta[i].getValue();
					}
					else{
						if(this.debug)
							this.logger.debug("HTTP risposta ["+hdrRisposta[i].getName()+"] ["+hdrRisposta[i].getValue()+"]...");
						key = hdrRisposta[i].getName();
						value = hdrRisposta[i].getValue();
					}
					
					TransportUtils.addHeader(this.propertiesTrasportoRisposta, key, value);
					
					List<String> list = null;
					if(mapHeaderHttpResponse.containsKey(key)) {
						list = mapHeaderHttpResponse.get(key);
					}
					if(list==null) {
						list = new ArrayList<>();
						mapHeaderHttpResponse.put(key, list);
					}
					list.add(value);
				}
			}
			
			this.tipoRisposta = TransportUtils.getObjectAsString(mapHeaderHttpResponse, HttpConstants.CONTENT_TYPE);
			
			String contentLengthHdr = TransportUtils.getObjectAsString(mapHeaderHttpResponse, HttpConstants.CONTENT_LENGTH);
			if(contentLengthHdr!=null){
				this.contentLength = Long.parseLong(contentLengthHdr);
			}
			else {
				if(this.httpEntityResponse!=null && this.httpEntityResponse.getContentLength()>0){
					this.contentLength = this.httpEntityResponse.getContentLength();
				}
			}
			
			
			/**System.out.println("TIPO RISPOSTA["+tipoRisposta+"] LOCATION["+locationRisposta+"]");*/
			
			// Parametri di imbustamento
			if(this.isSoap){
				if("true".equals(TransportUtils.getObjectAsString(mapHeaderHttpResponse, this.openspcoopProperties.getTunnelSOAPKeyWord_headerTrasporto()))){
					this.imbustamentoConAttachment = true;
				}
				this.mimeTypeAttachment = TransportUtils.getObjectAsString(mapHeaderHttpResponse, this.openspcoopProperties.getTunnelSOAPKeyWordMimeType_headerTrasporto());
				if(this.mimeTypeAttachment==null)
					this.mimeTypeAttachment = HttpConstants.CONTENT_TYPE_OPENSPCOOP2_TUNNEL_SOAP;
				/**System.out.println("IMB["+imbustamentoConAttachment+"] MIME["+mimeTypeAttachment+"]");*/
			}

			// Ricezione Risposta
			if(this.debug)
				this.logger.debug("Analisi risposta input stream e risultato http...");
			this.initConfigurationAcceptOnlyReturnCode_202_200();
			
			this.codice = httpResponse.getCode();
			this.resultHTTPMessage = httpResponse.getReasonPhrase();
			
			if(this.codice<300) {
				if(this.isSoap && this.acceptOnlyReturnCode_202_200 &&
					this.codice!=200 && this.codice!=202){
					throw new ConnettoreException("Return code ["+this.codice+"] non consentito dal WS-I Basic Profile (http://www.ws-i.org/Profiles/BasicProfile-1.1-2004-08-24.html#HTTP_Success_Status_Codes)");
				}
				if(httpBody.isDoInput() && this.httpEntityResponse!=null){
					this.isResponse = this.httpEntityResponse.getContent();
				}
			}else{
				if(this.codice<400){
					String redirectLocation = TransportUtils.getObjectAsString(mapHeaderHttpResponse, HttpConstants.REDIRECT_LOCATION);
					
					// 3XX
					if(this.followRedirects && !gestioneRedirectTramiteLibrerieApache){
								
						if(redirectLocation==null){
							throw new ConnettoreException("Non è stato rilevato l'header HTTP ["+HttpConstants.REDIRECT_LOCATION+"] necessario alla gestione del Redirect (code:"+this.codice+")"); 
						}
						
						TransportUtils.removeObject(request.getConnectorProperties(), CostantiConnettori.CONNETTORE_LOCATION);
						TransportUtils.removeObject(request.getConnectorProperties(), CostantiConnettori.CONNETTORE_HTTP_REDIRECT_NUMBER);
						TransportUtils.removeObject(request.getConnectorProperties(), CostantiConnettori.CONNETTORE_HTTP_REDIRECT_ROUTE);
						request.getConnectorProperties().put(CostantiConnettori.CONNETTORE_LOCATION, redirectLocation);
						request.getConnectorProperties().put(CostantiConnettori.CONNETTORE_HTTP_REDIRECT_NUMBER, (this.numberRedirect+1)+"" );
						if(this.routeRedirect!=null){
							request.getConnectorProperties().put(CostantiConnettori.CONNETTORE_HTTP_REDIRECT_ROUTE, this.routeRedirect+" -> "+redirectLocation );
						}else{
							request.getConnectorProperties().put(CostantiConnettori.CONNETTORE_HTTP_REDIRECT_ROUTE, redirectLocation );
						}
						if(this.originalAbsolutePrefixForRelativeRedirectLocation==null) {
							this.originalAbsolutePrefixForRelativeRedirectLocation = url.getProtocol()+"://"+url.getHost()+":"+url.getPort();
						}
						this.redirectLocation = redirectLocation; // per la prossima build()
						if(this.redirectLocation.startsWith("/")) {
							// relative
							this.redirectLocation = this.originalAbsolutePrefixForRelativeRedirectLocation + this.redirectLocation;
						}
						
						this.logger.warn("(hope:"+(this.numberRedirect+1)+") Redirect verso ["+redirectLocation+"] ...");
						
						if(this.numberRedirect==this.maxNumberRedirects){
							throw new ConnettoreException("Gestione redirect (code:"+this.codice+" "+HttpConstants.REDIRECT_LOCATION+":"+redirectLocation+") non consentita ulteriormente, sono già stati gestiti "+this.maxNumberRedirects+" redirects: "+this.routeRedirect);
						}
						
						boolean acceptOnlyReturnCode307 = false;
						if(this.isSoap) {
							if(ConsegnaContenutiApplicativi.ID_MODULO.equals(this.idModulo)){
								acceptOnlyReturnCode307 = this.openspcoopProperties.isAcceptOnlyReturnCode_307_consegnaContenutiApplicativi();
							}
							else{
								// InoltroBuste e InoltroRisposte
								acceptOnlyReturnCode307 = this.openspcoopProperties.isAcceptOnlyReturnCode_307_inoltroBuste();
							}
						}
						if(acceptOnlyReturnCode307 &&
							this.codice!=307){
							throw new ConnettoreException("Return code ["+this.codice+"] (redirect "+HttpConstants.REDIRECT_LOCATION+":"+redirectLocation+") non consentito dal WS-I Basic Profile (http://www.ws-i.org/Profiles/BasicProfile-1.1-2004-08-24.html#HTTP_Redirect_Status_Codes)");
						}
						// Annullo precedente immagine
						this.clearRequestHeader();
						if(this.propertiesTrasportoRisposta!=null) {
							this.propertiesTrasportoRisposta.clear();
						}
						this.contentLength = -1;
						try {
							return this.send(request); // caching ricorsivo non serve
						}finally {
							/**System.out.println("CHECK ["+redirectLocation+"]");
							if(this.responseMsg!=null) {
								System.out.println("MSG ["+this.responseMsg.getContentType()+"]");
								this.responseMsg.writeTo(System.out, false);
							}*/
						}
						
					}else{
						if(this.isSoap) {
							throw new ConnettoreException("Gestione redirect (code:"+this.codice+" "+HttpConstants.REDIRECT_LOCATION+":"+redirectLocation+") non attiva");
						}
						else {
							this.logger.debug("Gestione redirect (code:"+this.codice+" "+HttpConstants.REDIRECT_LOCATION+":"+redirectLocation+") non attiva");
							
							if(this.location!=null && redirectLocation!=null){
								this.location = this.location+" [redirect-location: "+redirectLocation+"]";
					    	}
							
							if(this.httpEntityResponse!=null) {
								this.isResponse = this.httpEntityResponse.getContent();
							}
						}
					}	
				}
				else {
					if(this.httpEntityResponse!=null) {
						this.isResponse = this.httpEntityResponse.getContent();
					}
				}
			}
			
			
			
						
			
			
			
			/* ------------  PostOutRequestHandler ------------- */
			this.postOutRequest();
			
			
			
			
			/* ------------  PreInResponseHandler ------------- */
			this.preInResponse();
			
			// Lettura risposta parametri NotifierInputStream per la risposta
			this.notifierInputStreamParams = null;
			if(this.preInResponseContext!=null){
				this.notifierInputStreamParams = this.preInResponseContext.getNotifierInputStreamParams();
			}
			
			
			
			/* ------------  Gestione Risposta ------------- */
			
			this.normalizeInputStreamResponse((int)readConnectionTimeout, readConnectionTimeoutConfigurazioneGlobale);
			
			this.initCheckContentTypeConfiguration();
			
			if(this.isDumpBinarioRisposta() &&
				!this.dumpResponse(this.propertiesTrasportoRisposta)) {
				return false;
			}
					
			if(this.isRest){
				
				if(!this.doRestResponse()){
					return false;
				}
				
			}
			else{
			
				if(!this.doSoapResponse()){
					return false;
				}
				
			}
			
			if(this.debug)
				this.logger.info("Gestione invio/risposta http effettuata con successo",false);
			
			return true;			
						
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
			
			this.processConnectionTimeoutException(connectionTimeout, connectionTimeoutConfigurazioneGlobale, e, msgErrore);
			
			this.processReadTimeoutException(readConnectionTimeout, readConnectionTimeoutConfigurazioneGlobale, e, msgErrore);
			
			return false;
		} finally {
			// se per caso non l'ho ancora chiuso lo faccio
			if(this.cloasebleDumpBout!=null) { 
				try {
					this.cloasebleDumpBout.clearResources();
					this.cloasebleDumpBout = null;
				}catch(Exception t) {
					this.logger.error("Release resources failed: "+t.getMessage(),t);
				}
			}
		}

	}

	
	@Override
	public void disconnect() throws ConnettoreException{
		List<Exception> listExceptionChiusura = new ArrayList<>();
		try{
			// Gestione finale della connessione    		
    		if(this.isResponse!=null){
	    		if(this.debug && this.logger!=null)
	    			this.logger.debug("Chiusura socket...");
	    		this.isResponse.close();
			}				
		}
		catch(Exception t) {
			if(this.logger!=null) {
				this.logger.debug("Chiusura socket fallita: "+t.getMessage(),t);
			}
			listExceptionChiusura.add(t);
    	}
    	try{
			// Gestione finale della connessione
    		if(this.httpEntityResponse!=null){
	    		if(this.debug && this.logger!=null)
	    			this.logger.debug("Chiusura httpEntityResponse...");
	    		EntityUtils.consume(this.httpEntityResponse);			
	    	}
				
    	}catch(Exception t) {
    		if(this.logger!=null) {
				this.logger.debug("Chiusura connessione fallita: "+t.getMessage(),t);
    		}
			listExceptionChiusura.add(t);
		}
    	/**
    	 * 
    	 * NOTA: Non bisogna chiudere il CloseableHttpClient dopo ogni richiesta quando si utilizza un pool di connessioni. 
    	 * Bisogna mantenerlo aperto per permettere al pool di gestire e riutilizzare le connessioni, migliorando così le prestazioni dell'applicazione. 
    	 * 
    	 */
    	/**try{
			// chiusura della connessione
    		if(this.cloasebleHttpClient!=null){
	    		if(this.debug && this.logger!=null)
	    			this.logger.debug("Chiusura cloasebleHttpClient...");
	    		this.cloasebleHttpClient.close(org.apache.hc.core5.io.CloseMode.GRACEFUL); // tenta di chiudere le connessioni in modo ordinato, permettendo alle richieste in corso di completarsi.
	    	}
				
    	}catch(Exception t) {
    		if(this.logger!=null) {
				this.logger.debug("Chiusura cloasebleHttpClient fallita: "+t.getMessage(),t);
    		}
			listExceptionChiusura.add(t);
		}*/
    	try{
	    	// super.disconnect (Per risorse base)
	    	super.disconnect();
    	}catch(Exception t) {
    		if(this.logger!=null) {
				this.logger.debug("Chiusura risorse fallita: "+t.getMessage(),t);
    		}
			listExceptionChiusura.add(t);
		}
    	
    	if(!listExceptionChiusura.isEmpty()) {
			org.openspcoop2.utils.UtilsMultiException multiException = new org.openspcoop2.utils.UtilsMultiException(listExceptionChiusura.toArray(new Throwable[1]));
			throw new ConnettoreException("Chiusura connessione non riuscita: "+multiException.getMessage(),multiException);
		}
    }
		
	
	@Override
	protected String getTipoImplConnettore() {
		if(this.tipoConnettore!=null && StringUtils.isNotEmpty(this.tipoConnettore)) {
			return this.tipoConnettore;
		}
		else {
			return this.connettoreHttps ? ConnettoreHTTPSCORE.ENDPOINT_TYPE : ConnettoreHTTPCORE.ENDPOINT_TYPE;
		}
	}
    
	private Map<String,List<String>> recHeaderForInterceptor = null;
    @Override
	protected void setRequestHeader(String key, List<String> values) throws Exception {
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
