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

package org.openspcoop2.pdd.core.connettori;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;

import org.apache.hc.client5.http.ConnectionKeepAliveStrategy;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.classic.methods.HttpDelete;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpHead;
import org.apache.hc.client5.http.classic.methods.HttpOptions;
import org.apache.hc.client5.http.classic.methods.HttpPatch;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpTrace;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.socket.ConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.DefaultHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.ConnectionReuseStrategy;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HeaderElement;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.config.Registry;
import org.apache.hc.core5.http.config.RegistryBuilder;
import org.apache.hc.core5.http.impl.DefaultConnectionReuseStrategy;
import org.apache.hc.core5.http.io.entity.ByteArrayEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.FileEntity;
import org.apache.hc.core5.http.io.entity.InputStreamEntity;
import org.apache.hc.core5.http.message.BasicHeaderElementIterator;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.util.TimeValue;
import org.openspcoop2.core.config.ResponseCachingConfigurazione;
import org.openspcoop2.core.constants.CostantiConnettori;
import org.openspcoop2.core.constants.TransferLengthModes;
import org.openspcoop2.core.transazioni.constants.TipoMessaggio;
import org.openspcoop2.message.OpenSPCoop2RestMessage;
import org.openspcoop2.message.OpenSPCoop2SoapMessage;
import org.openspcoop2.message.constants.Costanti;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.soap.TunnelSoapUtils;
import org.openspcoop2.pdd.mdb.ConsegnaContenutiApplicativi;
import org.openspcoop2.utils.NameValue;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.io.Base64Utilities;
import org.openspcoop2.utils.io.DumpByteArrayOutputStream;
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
public class ConnettoreHTTPCORE5 extends ConnettoreBaseHTTP {

	public static final String ENDPOINT_TYPE = "httpcore5";
	
	
	private static boolean USE_POOL = true;
	
	private HttpEntity httpEntityResponse = null;
	private HttpClient httpClient = null;
		
	private HttpUriRequestBase httpRequest;
	
	
	/* Costruttori */
	public ConnettoreHTTPCORE5(){
		super();
	}
	public ConnettoreHTTPCORE5(boolean https){
		super(https);
	}
	
	
	private static Map<String, PoolingHttpClientConnectionManager> cmMap = new HashMap<String, PoolingHttpClientConnectionManager>();
	private static synchronized void initialize(String key, SSLConnectionSocketFactory sslConnectionSocketFactory){
		if(!ConnettoreHTTPCORE5.cmMap.containsKey(key)){
						
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
			// Increase max total connection to 200
			cm.setMaxTotal(200);
			// Increase default max connection per route to 20
			cm.setDefaultMaxPerRoute(5);
			// Increase max connections for localhost:80 to 50
			//HttpHost localhost = new HttpHost("locahost", 80);
			//cm.setMaxPerRoute(new HttpRoute(localhost), 50);
			 
			ConnettoreHTTPCORE5.cmMap.put(key, cm);
		}
	}
	
	private HttpClient buildHttpClient(ConnectionKeepAliveStrategy keepAliveStrategy, SSLSocketFactory sslSocketFactory, boolean usePool) throws UtilsException{
		
		HttpClientBuilder httpClientBuilder = HttpClients.custom();
			
		// Imposta Contesto SSL se attivo
		
		String key = "default";
		if(this.sslContextProperties!=null){
			key = this.sslContextProperties.toString();
		}
		
		SSLConnectionSocketFactory sslConnectionSocketFactory = null;
		if(this.sslContextProperties!=null && 
				(!usePool || !ConnettoreHTTPCORE5.cmMap.containsKey(key))){
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
			if(!ConnettoreHTTPCORE5.cmMap.containsKey(key)){
				ConnettoreHTTPCORE5.initialize(key, sslConnectionSocketFactory);
			}
			
			PoolingHttpClientConnectionManager cm = ConnettoreHTTPCORE5.cmMap.get(key);
			
			//System.out.println("-----GET CONNECTION [START] ----");
			//System.out.println("PRIMA CLOSE AVAILABLE["+cm.getTotalStats().getAvailable()+"] LEASED["
			//		+cm.getTotalStats().getLeased()+"] MAX["+cm.getTotalStats().getMax()+"] PENDING["+cm.getTotalStats().getPending()+"]");
			// BLOCKED ConnettoreHTTPCORE.cm.closeExpiredConnections();
			// BLOCKED ConnettoreHTTPCORE.cm.closeIdleConnections(30, java.util.concurrent.TimeUnit.SECONDS);
			//System.out.println("DOPO CLOSE AVAILABLE["+cm.getTotalStats().getAvailable()+"] LEASED["
			//		+cm.getTotalStats().getLeased()+"] MAX["+cm.getTotalStats().getMax()+"] PENDING["+cm.getTotalStats().getPending()+"]");
			httpClientBuilder.setConnectionManager(cm);
		}
		else {
//			if(sslConnectionSocketFactory!=null) {
//				httpClientBuilder.setSSLSocketFactory(sslConnectionSocketFactory);		
//			}
		}
		
		ConnectionReuseStrategy defaultClientConnectionReuseStrategy = new DefaultConnectionReuseStrategy();
		httpClientBuilder.setConnectionReuseStrategy(defaultClientConnectionReuseStrategy);
		
		if(keepAliveStrategy!=null){
			httpClientBuilder.setKeepAliveStrategy(keepAliveStrategy);
		}
				
		//System.out.println("PRESA LA CONNESSIONE AVAILABLE["+cm.getTotalStats().getAvailable()+"] LEASED["
		//		+cm.getTotalStats().getLeased()+"] MAX["+cm.getTotalStats().getMax()+"] PENDING["+cm.getTotalStats().getPending()+"]");
		//System.out.println("-----GET CONNECTION [END] ----");
		
		return httpClientBuilder.build();
	}
	
	
	@Override
	protected boolean initializePreSend(ResponseCachingConfigurazione responseCachingConfig, ConnettoreMsg request) {
		
		if(this.initialize(request, true, responseCachingConfig)==false){
			return false;
		}
		
		return true;
		
	}
	
	@Override
	protected boolean send(ConnettoreMsg request) {
		
		// HTTPS
		try{
			this.setSSLContext();
		}catch(Exception e){
			this.eccezioneProcessamento = e;
			this.logger.error("[HTTPS error]"+ this.readExceptionMessageFromException(e),e);
			this.errore = "[HTTPS error]"+ this.readExceptionMessageFromException(e);
			return false;
		}
		
		try{
			
			// Creazione URL
			if(this.debug)
				this.logger.debug("Creazione URL...");
			this.buildLocation();		
			if(this.debug)
				this.logger.debug("Creazione URL ["+this.location+"]...");
			URL url = new URL( this.location );	
			
			
			// Keep-alive
			ConnectionKeepAliveStrategy keepAliveStrategy = null; //new ConnectionKeepAliveStrategyCustomHttp5();
			
			
			
			// Collezione header di trasporto per dump
			Map<String, List<String>> propertiesTrasportoDebug = null;
			if(this.isDumpBinarioRichiesta()) {
				propertiesTrasportoDebug = new HashMap<String, List<String>>();
			}

			
			// Creazione Connessione
			if(this.debug)
				this.logger.info("Creazione connessione alla URL ["+this.location+"]...",false);
			this.httpClient = buildHttpClient(keepAliveStrategy, buildSSLContextFactory(), ConnettoreHTTPCORE5.USE_POOL);
			
			// HttpMethod
			if(this.httpMethod==null){
				throw new Exception("HttpRequestMethod non definito");
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
					this.httpRequest = new HttpPost(url.toString());
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
					this.httpRequest = new CustomHttpCore5Entity(this.httpMethod, url.toString());
					break;
			}
			if(this.httpMethod==null){
				throw new Exception("HttpRequest non definito ?");
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
					throw new Exception("Content-Type del messaggio da spedire non definito");
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
			boolean transferEncodingChunked = false;
			TransferLengthModes tlm = null;
			int chunkLength = -1;
			if(ConsegnaContenutiApplicativi.ID_MODULO.equals(this.idModulo)){
				tlm = this.openspcoopProperties.getTransferLengthModes_consegnaContenutiApplicativi();
				chunkLength = this.openspcoopProperties.getChunkLength_consegnaContenutiApplicativi();
			}
			else{
				// InoltroBuste e InoltroRisposte
				tlm = this.openspcoopProperties.getTransferLengthModes_inoltroBuste();
				chunkLength = this.openspcoopProperties.getChunkLength_inoltroBuste();
			}
			transferEncodingChunked = TransferLengthModes.TRANSFER_ENCODING_CHUNKED.equals(tlm);
			if(transferEncodingChunked){
				//this.httpConn.setChunkedStreamingMode(chunkLength);
			}
			if(this.debug)
				this.logger.info("Impostazione transfer-length effettuata (chunkLength:"+chunkLength+"): "+tlm,false);
			
			
			
			// Impostazione timeout
			if(this.debug)
				this.logger.debug("Impostazione timeout...");
			long connectionTimeout = -1;
			long readConnectionTimeout = -1;
			if(this.properties.get(CostantiConnettori.CONNETTORE_CONNECTION_TIMEOUT)!=null){
				try{
					connectionTimeout = Long.valueOf(this.properties.get(CostantiConnettori.CONNETTORE_CONNECTION_TIMEOUT));
				}catch(Exception e){
					this.logger.error("Parametro '"+CostantiConnettori.CONNETTORE_CONNECTION_TIMEOUT+"' errato",e);
				}
			}
			if(connectionTimeout==-1){
				connectionTimeout = HttpUtilities.HTTP_CONNECTION_TIMEOUT;
			}
			if(this.properties.get(CostantiConnettori.CONNETTORE_READ_CONNECTION_TIMEOUT)!=null){
				try{
					readConnectionTimeout = Long.valueOf(this.properties.get(CostantiConnettori.CONNETTORE_READ_CONNECTION_TIMEOUT));
				}catch(Exception e){
					this.logger.error("Parametro '"+CostantiConnettori.CONNETTORE_READ_CONNECTION_TIMEOUT+"' errato",e);
				}
			}
			if(readConnectionTimeout==-1){
				readConnectionTimeout = HttpUtilities.HTTP_READ_CONNECTION_TIMEOUT;
			}
			if(this.debug)
				this.logger.info("Impostazione http timeout CT["+connectionTimeout+"] RT["+readConnectionTimeout+"]",false);
			requestConfigBuilder.setConnectionRequestTimeout(connectionTimeout, TimeUnit.MILLISECONDS);
			requestConfigBuilder.setConnectTimeout(connectionTimeout, TimeUnit.MILLISECONDS);
			requestConfigBuilder.setResponseTimeout(readConnectionTimeout, TimeUnit.MILLISECONDS);
			


			
			
			
			// Gestione automatica del redirect
			//this.httpConn.setInstanceFollowRedirects(true); 

			
			
			
			
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
	    		if(this.debug)
					this.logger.info("Impostazione autenticazione token (header-name '"+nv.getName()+"' value '"+nv.getValue()+"')",false);
	    	}
	    	
	    	
	    	
	    	
	    	// ForwardProxy
	    	if(this.forwardProxy_headerName!=null && this.forwardProxy_headerValue!=null) {
	    		if(this.requestMsg!=null && this.requestMsg.getTransportRequestContext()!=null) {
	    			this.requestMsg.getTransportRequestContext().removeHeader(this.forwardProxy_headerName); // Fix: senno sovrascriveva il vecchio token
	    		}
	    		setRequestHeader(this.forwardProxy_headerName,this.forwardProxy_headerValue, propertiesTrasportoDebug);
	    		if(this.debug)
					this.logger.info("Impostazione ForwardProxy (header-name '"+this.forwardProxy_headerName+"' value '"+this.forwardProxy_headerValue+"')",false);
	    	}

			
			
			// Impostazione Proprieta del trasporto
			if(this.debug)
				this.logger.debug("Impostazione header di trasporto...");
			this.forwardHttpRequestHeader();
			if(this.propertiesTrasporto != null){
				Iterator<String> keys = this.propertiesTrasporto.keySet().iterator();
				while (keys.hasNext()) {
					String key = (String) keys.next();
					List<String> values = this.propertiesTrasporto.get(key);
					if(this.debug) {
			    		if(values!=null && !values.isEmpty()) {
			        		for (String value : values) {
			        			this.logger.info("Set Transport Header ["+key+"]=["+value+"]",false);
			        		}
			    		}
			    	}
					
					if(this.encodingRFC2047){
						List<String> valuesEncoded = new ArrayList<String>();
						if(values!=null && !values.isEmpty()) {
			        		for (String value : values) {
			        			if(RFC2047Utilities.isAllCharactersInCharset(value, this.charsetRFC2047)==false){
									String encoded = RFC2047Utilities.encode(new String(value), this.charsetRFC2047, this.encodingAlgorithmRFC2047);
									//System.out.println("@@@@ CODIFICA ["+value+"] in ["+encoded+"]");
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
					this.setRequestHeader(Costanti.SOAP11_MANDATORY_HEADER_HTTP_SOAP_ACTION,this.soapAction, propertiesTrasportoDebug);
				}
				if(this.debug)
					this.logger.info("SOAP Action inviata ["+this.soapAction+"]",false);
			}
			
			
			
			
			
			// Impostazione Metodo
			HttpBodyParameters httpBody = new  HttpBodyParameters(this.httpMethod, contentTypeRichiesta);
			
			
			
			// Preparazione messaggio da spedire
			// Spedizione byte
			if(httpBody.isDoOutput()){
				if(this.debug)
					this.logger.debug("Spedizione byte...");
				boolean hasContentRestBuilded = false;
				boolean hasContentRest = false;
				OpenSPCoop2RestMessage<?> restMessage = null;
				if(this.isRest) {
					restMessage = this.requestMsg.castAsRest();
					hasContentRest = restMessage.hasContent();
					hasContentRestBuilded = restMessage.isContentBuilded();
				}
				if(this.isDumpBinarioRichiesta() || this.isSoap || hasContentRestBuilded) {
					DumpByteArrayOutputStream bout = new DumpByteArrayOutputStream(this.dumpBinario_soglia, this.dumpBinario_repositoryFile, this.idTransazione, 
							TipoMessaggio.RICHIESTA_USCITA_DUMP_BINARIO.getValue());
					try {
						if(this.isSoap && this.sbustamentoSoap){
							if(this.debug)
								this.logger.debug("Sbustamento...");
							TunnelSoapUtils.sbustamentoMessaggio(soapMessageRequest,bout);
						}else{
							this.requestMsg.writeTo(bout, true);
						}
						bout.flush();
						bout.close();
						if(this.isDumpBinarioRichiesta()) {
							this.dumpBinarioRichiestaUscita(bout, requestMessageType, contentTypeRichiesta, this.location, propertiesTrasportoDebug);
						}
						
						HttpEntity httpEntity = null;
						String baseMimeType = ContentTypeUtilities.readBaseTypeFromContentType(contentTypeRichiesta);
						org.apache.hc.core5.http.ContentType ct = org.apache.hc.core5.http.ContentType.create(baseMimeType);
						if(bout.isSerializedOnFileSystem()) {
							httpEntity = new FileEntity(bout.getSerializedFile(), ct);
						}
						else {
							httpEntity = new ByteArrayEntity(bout.toByteArray(), ct);
						}
						this.httpRequest.setEntity(httpEntity);
					}finally {
						try {
							bout.clearResources();
						}catch(Throwable t) {
							this.logger.error("Release resources failed: "+t.getMessage(),t);
						}
					}
				}
				else {
					// Siamo per forza rest con contenuto non costruito
					if(hasContentRest) {
						InputStream isRequest = this.requestMsg.castAsRest().getInputStream();
						String baseMimeType = ContentTypeUtilities.readBaseTypeFromContentType(contentTypeRichiesta);
						org.apache.hc.core5.http.ContentType ct = org.apache.hc.core5.http.ContentType.create(baseMimeType);
						HttpEntity httpEntity = new InputStreamEntity(isRequest, ct);
						this.httpRequest.setEntity(httpEntity);
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
			ClassicHttpResponse httpResponse = (ClassicHttpResponse) this.httpClient.execute(this.httpRequest);
			this.httpEntityResponse = httpResponse.getEntity();
			
			
			
			if(this.debug)
				this.logger.debug("Analisi risposta...");
			Header [] hdrRisposta = httpResponse.getHeaders();
			Map<String, List<String>> mapHeaderHttpResponse = new HashMap<String, List<String>>();
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
						list = new ArrayList<String>();
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
				if(this.httpEntityResponse.getContentLength()>0){
					this.contentLength = this.httpEntityResponse.getContentLength();
				}
			}
			
			
			//System.out.println("TIPO RISPOSTA["+tipoRisposta+"] LOCATION["+locationRisposta+"]");
			
			// Parametri di imbustamento
			if(this.isSoap){
				if("true".equals(TransportUtils.getObjectAsString(mapHeaderHttpResponse, this.openspcoopProperties.getTunnelSOAPKeyWord_headerTrasporto()))){
					this.imbustamentoConAttachment = true;
				}
				this.mimeTypeAttachment = TransportUtils.getObjectAsString(mapHeaderHttpResponse, this.openspcoopProperties.getTunnelSOAPKeyWordMimeType_headerTrasporto());
				if(this.mimeTypeAttachment==null)
					this.mimeTypeAttachment = HttpConstants.CONTENT_TYPE_OPENSPCOOP2_TUNNEL_SOAP;
				//System.out.println("IMB["+imbustamentoConAttachment+"] MIME["+mimeTypeAttachment+"]");
			}

			// Ricezione Risposta
			if(this.debug)
				this.logger.debug("Analisi risposta input stream e risultato http...");
			this.initConfigurationAcceptOnlyReturnCode_202_200();
			
			this.codice = httpResponse.getCode();
			this.resultHTTPMessage = httpResponse.getReasonPhrase();
			
			if(this.codice<300)
				if(this.isSoap && this.acceptOnlyReturnCode_202_200){
					if(this.codice!=200 && this.codice!=202){
						throw new Exception("Return code ["+this.codice+"] non consentito dal WS-I Basic Profile (http://www.ws-i.org/Profiles/BasicProfile-1.1-2004-08-24.html#HTTP_Success_Status_Codes)");
					}
				}
				if(httpBody.isDoInput()){
					this.isResponse = this.httpEntityResponse.getContent();
				}
			else{
				this.isResponse = this.httpEntityResponse.getContent();
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
			
			this.normalizeInputStreamResponse((int)readConnectionTimeout);
			
			this.initCheckContentTypeConfiguration();
			
			if(this.isDumpBinarioRisposta()){
				if(!this.dumpResponse(this.propertiesTrasportoRisposta)) {
					return false;
				}
			}
					
			if(this.isRest){
				
				if(this.doRestResponse()==false){
					return false;
				}
				
			}
			else{
			
				if(this.doSoapResponse()==false){
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
			return false;
		} 

	}

	
	@Override
	public void disconnect() throws ConnettoreException{
		List<Throwable> listExceptionChiusura = new ArrayList<Throwable>();
		try{
			// Gestione finale della connessione    		
    		//System.out.println("CHECK CLOSE STREAM...");
	    	if(this.isResponse!=null){
	    		if(this.debug && this.logger!=null)
	    			this.logger.debug("Chiusura socket...");
	    		//System.out.println("CLOSE STREAM...");
				this.isResponse.close();
				//System.out.println("CLOSE STREAM");
			}				
		}
		catch(Throwable t) {
			this.logger.debug("Chiusura socket fallita: "+t.getMessage(),t);
			listExceptionChiusura.add(t);
    	}
    	try{
			// Gestione finale della connessione
    		//System.out.println("CHECK ENTITY...");
	    	if(this.httpEntityResponse!=null){
	    		if(this.debug && this.logger!=null)
	    			this.logger.debug("Chiusura httpEntityResponse...");
	    		//System.out.println("CLOSE ENTITY...");
	    		EntityUtils.consume(this.httpEntityResponse);
	    		//System.out.println("CLOSE ENTITY");
			}
	    	
	    	if(this.httpEntityResponse!=null){
	    		
	    	}
				
    	}catch(Throwable t) {
			this.logger.debug("Chiusura connessione fallita: "+t.getMessage(),t);
			listExceptionChiusura.add(t);
		}
    	try{
	    	// super.disconnect (Per risorse base)
	    	super.disconnect();
    	}catch(Throwable t) {
			this.logger.debug("Chiusura risorse fallita: "+t.getMessage(),t);
			listExceptionChiusura.add(t);
		}
    	
    	if(listExceptionChiusura!=null && !listExceptionChiusura.isEmpty()) {
			org.openspcoop2.utils.UtilsMultiException multiException = new org.openspcoop2.utils.UtilsMultiException(listExceptionChiusura.toArray(new Throwable[1]));
			throw new ConnettoreException("Chiusura connessione non riuscita: "+multiException.getMessage(),multiException);
		}
    }
		
	
	
    /**
     * Ritorna l'informazione su dove il connettore sta spedendo il messaggio
     * 
     * @return location di inoltro del messaggio
     */
    @Override
	public String getLocation(){
    	if(this.location==null){
    		// può darsi che per un errore non sia ancora stata inizializzata la location
    		try{
    			this.buildLocation();
    		}catch(Throwable t){}
    	}
    	if(this.location!=null){
    		String l = new String(this.location);
//	    	if(this.forwardProxy!=null && this.forwardProxy.isEnabled()) {
//	    		l = l+" [govway-proxy]";
//	    	}
	    	return l;
    	}
    	return null;
    }
    private void buildLocation() throws ConnettoreException {
    	this.location = TransportUtils.getObjectAsString(this.properties,CostantiConnettori.CONNETTORE_LOCATION);	
    	NameValue nv = this.getTokenQueryParameter();
    	if(nv!=null) {
    		if(this.requestMsg!=null && this.requestMsg.getTransportRequestContext()!=null) {
    			this.requestMsg.getTransportRequestContext().removeParameter(nv.getName()); // Fix: senno sovrascriveva il vecchio token
    		}
    		if(this.propertiesUrlBased==null) {
    			this.propertiesUrlBased = new HashMap<String,List<String>>();
    		}
    		TransportUtils.setParameter(this.propertiesUrlBased, nv.getName(), nv.getValue());
    	}
		this.location = ConnettoreUtils.buildLocationWithURLBasedParameter(this.requestMsg, 
				ConnettoreHTTPCORE5.ENDPOINT_TYPE, 
				this.propertiesUrlBased, this.location,
				this.getProtocolFactory(), this.idModulo);
		
		this.updateLocation_forwardProxy(this.location);
    }
	
	
	
    private void setRequestHeader(boolean validazioneHeaderRFC2047, String key, List<String> values, ConnettoreLogger logger, Map<String, List<String>> propertiesTrasportoDebug) throws Exception {
    	if(validazioneHeaderRFC2047){
    		try{
        		RFC2047Utilities.validHeader(key, values);
        		setRequestHeader(key, values, propertiesTrasportoDebug);
        	}catch(UtilsException e){
        		logger.error(e.getMessage(),e);
        	}
    	}
    	else{
    		setRequestHeader(key, values, propertiesTrasportoDebug);
    	}
    	
    }
    
    @Override
	protected void setRequestHeader(String key, List<String> values) throws Exception {
    	if(values!=null && !values.isEmpty()) {
    		for (String value : values) {
    			this.httpRequest.addHeader(key,value);		
			}
    	}
    }
}

class ConnectionKeepAliveStrategyCustomHttp5 implements ConnectionKeepAliveStrategy{

	@Override
	public TimeValue getKeepAliveDuration(HttpResponse response, HttpContext context) {
		
		 // Honor 'keep-alive' header
        BasicHeaderElementIterator it = new BasicHeaderElementIterator(
                response.headerIterator("Keep-Alive"));
        while (it.hasNext()) {
            HeaderElement he = it.next();
            String param = he.getName(); 
            String value = he.getValue();
            if (value != null && param.equalsIgnoreCase("timeout")) {
                try {
                	//System.out.println("RETURN HEADER ["+ (Long.parseLong(value) * 1000)+"]");
                    long v = Long.parseLong(value) * 1000;
                    TimeValue tv = TimeValue.ofMilliseconds(v);
                    return tv;
                } catch(NumberFormatException ignore) {
                }
            }
        }
//        HttpHost target = (HttpHost) context.getAttribute(
//                ExecutionContext.HTTP_TARGET_HOST);
//        if ("www.naughty-server.com".equalsIgnoreCase(target.getHostName())) {
//            // Keep alive for 5 seconds only
//            return 5 * 1000;
//        } else {
//            // otherwise keep alive for 30 seconds
//            return 30 * 1000;
//        }
        // otherwise keep alive for 2 minutes
        //System.out.println("RETURN 2 minuti");
        long v = 2 * 60 * 1000;
        TimeValue tv = TimeValue.ofMilliseconds(v);
        return tv;
		
	}
	
}