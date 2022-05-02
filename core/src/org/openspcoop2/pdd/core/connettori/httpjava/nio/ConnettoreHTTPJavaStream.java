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




package org.openspcoop2.pdd.core.connettori.httpjava.nio;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandler;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.openspcoop2.core.constants.CostantiConnettori;
import org.openspcoop2.core.constants.TransferLengthModes;
import org.openspcoop2.core.transazioni.constants.TipoMessaggio;
import org.openspcoop2.message.OpenSPCoop2RestMessage;
import org.openspcoop2.message.OpenSPCoop2SoapMessage;
import org.openspcoop2.message.constants.Costanti;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.soap.TunnelSoapUtils;
import org.openspcoop2.pdd.core.connettori.ConnettoreException;
import org.openspcoop2.pdd.core.connettori.ConnettoreExtBaseHTTP;
import org.openspcoop2.pdd.core.connettori.ConnettoreMsg;
import org.openspcoop2.pdd.core.connettori.nio.ConnectionConfiguration;
import org.openspcoop2.utils.NameValue;
import org.openspcoop2.utils.io.Base64Utilities;
import org.openspcoop2.utils.io.DumpByteArrayOutputStream;
import org.openspcoop2.utils.transport.TransportUtils;
import org.openspcoop2.utils.transport.http.HttpBodyParameters;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpUtilities;
import org.openspcoop2.utils.transport.http.RFC2047Utilities;



/**
 * ConnettoreHTTPJava
 *
 *
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConnettoreHTTPJavaStream extends ConnettoreExtBaseHTTP {

	
	/* ********  F I E L D S  P R I V A T I  ******** */


	/** Connessione */
	//protected ConnettoreHTTPJava_connection httpClient = null;
	protected HttpRequest.Builder httpRequestBuilder = null;
	protected ConnectionConfiguration httpConnectionConfig = null;
	public ConnectionConfiguration getHttpConnectionConfig() {
		return this.httpConnectionConfig;
	}

	
	/* Costruttori */
	public ConnettoreHTTPJavaStream(){
		super();
	}
	public ConnettoreHTTPJavaStream(boolean https){
		super(https);
	}
	
	
	
	/* ********  METODI  ******** */

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
			URL url = new URL( this.location );	

			
			// Collezione header di trasporto per dump
			Map<String, List<String>> propertiesTrasportoDebug = null;
			if(this.isDumpBinarioRichiesta()) {
				propertiesTrasportoDebug = new HashMap<String, List<String>>();
			}
			
			

			// Costruisco oggetto contenente parametri di connessione
			this.httpConnectionConfig = new ConnectionConfiguration();
			this.httpConnectionConfig.setDebug(this.debug);
			if(this.sslContextProperties!=null){
				this.httpConnectionConfig.setSslContextProperties(this.sslContextProperties);
			}
			if(this.proxyType!=null){
				this.httpConnectionConfig.setProxyHost(this.proxyHostname);
				this.httpConnectionConfig.setProxyPort(this.proxyPort);
			}
//			if(this.followRedirects && this.isRest()) {
//				this.httpConnectionConfig.setFollowRedirect(this.followRedirects);
//				this.httpConnectionConfig.setMaxNumberRedirects(this.maxNumberRedirects);
//			}
			int connectionTimeout = -1;
			int readConnectionTimeout = -1;
			if(this.properties.get(CostantiConnettori.CONNETTORE_CONNECTION_TIMEOUT)!=null){
				try{
					connectionTimeout = Integer.parseInt(this.properties.get(CostantiConnettori.CONNETTORE_CONNECTION_TIMEOUT));
				}catch(Exception e){
					this.logger.error("Parametro '"+CostantiConnettori.CONNETTORE_CONNECTION_TIMEOUT+"' errato",e);
				}
			}
			if(connectionTimeout==-1){
				connectionTimeout = HttpUtilities.HTTP_CONNECTION_TIMEOUT;
			}
			if(this.properties.get(CostantiConnettori.CONNETTORE_READ_CONNECTION_TIMEOUT)!=null){
				try{
					readConnectionTimeout = Integer.parseInt(this.properties.get(CostantiConnettori.CONNETTORE_READ_CONNECTION_TIMEOUT));
				}catch(Exception e){
					this.logger.error("Parametro "+CostantiConnettori.CONNETTORE_READ_CONNECTION_TIMEOUT+" errato",e);
				}
			}
			if(readConnectionTimeout==-1){
				readConnectionTimeout = HttpUtilities.HTTP_READ_CONNECTION_TIMEOUT;
			}
			this.httpConnectionConfig.setConnectionTimeout(connectionTimeout);
			this.httpConnectionConfig.setReadTimeout(readConnectionTimeout);
			

			// Creazione Connessione
			StringBuilder bfDebug =new StringBuilder();
			ConnettoreHTTPJava_connection httpClient = ConnettoreHTTPJava_connectionManager.getConnettoreNIO(this.httpConnectionConfig, this.loader, this.logger, bfDebug);
			if(this.debug) {
				this.logger.debug("Creazione Connessione ...");
				this.logger.debug(bfDebug.toString());
			}

			this.httpRequestBuilder = HttpRequest.newBuilder().uri( URI.create( url.toString() ) );

			// HttpMethod
			if(this.httpMethod==null){
				throw new Exception("HttpRequestMethod non definito");
			}
			
			// Proxy AUTH
			if(this.proxyType==null){
				if(this.debug)
					this.logger.info("Predispongo parametri per connessione alla URL ["+this.location+"]...",false);
			}
			else {
				this.logger.info("Predispongo parametri per connessione alla URL ["+this.location+"] (via proxy "+
						this.proxyHostname+":"+this.proxyPort+") (username["+this.proxyUsername+"] password["+this.proxyPassword+"])...",false);
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
				setRequestHeader(HttpConstants.CONTENT_TYPE,contentTypeRichiesta, propertiesTrasportoDebug);
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
				if(this.debug)
					this.logger.info("Impostazione autenticazione (username:"+user+" password:"+password+") ["+authentication+"]",false);
			}

			
			// Authentication Token
			NameValue nv = this.getTokenHeader();
	    	if(nv!=null) {
	    		if(this.requestMsg!=null && this.requestMsg.getTransportRequestContext()!=null) {
	    			this.requestMsg.getTransportRequestContext().removeHeader(nv.getName()); // Fix: senno sovrascriveva il vecchio token
	    		}
	    		setRequestHeader(nv.getName(),nv.getValue(), propertiesTrasportoDebug);
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
			BodyPublisher bodyPublisher = null;
			if(httpBody.isDoOutput()){
				boolean consumeRequestMessage = true;
				if(this.followRedirects){
					consumeRequestMessage = false;
				}
				if(this.debug)
					this.logger.debug("Spedizione byte (consume-request-message:"+consumeRequestMessage+")...");
				boolean hasContentRestBuilded = false;
				boolean hasContentRest = false;
				OpenSPCoop2RestMessage<?> restMessage = null;
				if(this.isRest) {
					restMessage = this.requestMsg.castAsRest();
					hasContentRest = restMessage.hasContent();
					hasContentRestBuilded = restMessage.isContentBuilded();
				}
				if(this.isDumpBinarioRichiesta() || this.isSoap || hasContentRestBuilded ||
						TransferLengthModes.CONTENT_LENGTH.equals(this.tlm) || !consumeRequestMessage) {
					DumpByteArrayOutputStream bout = new DumpByteArrayOutputStream(this.dumpBinario_soglia, this.dumpBinario_repositoryFile, this.idTransazione, 
							TipoMessaggio.RICHIESTA_USCITA_DUMP_BINARIO.getValue());
					try {
						if(this.isSoap && this.sbustamentoSoap){
							if(this.debug)
								this.logger.debug("Sbustamento...");
							TunnelSoapUtils.sbustamentoMessaggio(soapMessageRequest,bout);
						}else{
							this.requestMsg.writeTo(bout, consumeRequestMessage);
						}
						bout.flush();
						bout.close();
						if(this.isDumpBinarioRichiesta()) {
							this.dumpBinarioRichiestaUscita(bout, requestMessageType, contentTypeRichiesta, this.location, propertiesTrasportoDebug);
						}
						
						if(bout.isSerializedOnFileSystem()) {
							bodyPublisher = BodyPublishers.ofFile( bout.getSerializedFile().toPath() );
//							entityProducer = new FileEntityProducer(bout.getSerializedFile(), ct, TransferLengthModes.TRANSFER_ENCODING_CHUNKED.equals(this.tlm));
						}
						else {
							bodyPublisher = BodyPublishers.ofInputStream( () -> new ByteArrayInputStream( bout.toByteArray() ) );
//							entityProducer = new BasicAsyncEntityProducer(bout.toByteArray(), ct, TransferLengthModes.TRANSFER_ENCODING_CHUNKED.equals(this.tlm));
						}						
						if(this.debug)
							this.logger.info("Impostazione transfer-length effettuata: "+this.tlm,false);
						
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
						bodyPublisher = BodyPublishers.ofInputStream( () -> isRequest );
						// TBK content-type e' gia' negli headers ?
//						String baseMimeType = ContentTypeUtilities.readBaseTypeFromContentType(contentTypeRichiesta);
//						org.apache.hc.core5.http.ContentType ct = org.apache.hc.core5.http.ContentType.create(baseMimeType);
//						entityProducer = new ConnettoreHTTPCORE5_inputStreamEntityProducer(isRequest, ct, null, TransferLengthModes.TRANSFER_ENCODING_CHUNKED.equals(this.tlm));
						if(this.debug)
							this.logger.info("Impostazione transfer-length effettuata: "+this.tlm,false);
					}
				}
			}
			else {
				if(this.isDumpBinarioRichiesta()) {
					// devo registrare almeno gli header HTTP
					this.dumpBinarioRichiestaUscita(null, null, null, this.location, propertiesTrasportoDebug);
				}
			}

			// TBK da definire
			switch (this.httpMethod) {
				case GET:
					this.httpRequestBuilder = this.httpRequestBuilder.GET();
					break;
				case DELETE:
					this.httpRequestBuilder = this.httpRequestBuilder.DELETE();
					break;
				case POST:
					this.httpRequestBuilder = this.httpRequestBuilder.POST( bodyPublisher  );
					break;
				case PUT:
					this.httpRequestBuilder = this.httpRequestBuilder.PUT( bodyPublisher );
					break;
				case HEAD:
				case OPTIONS:
				case TRACE:
				case PATCH:
				case LINK:
				case UNLINK:
				default:
					this.httpRequestBuilder = this.httpRequestBuilder.method(this.httpMethod.toString(), bodyPublisher);
					break;
			}
			if(this.httpRequestBuilder==null){
				throw new Exception("HttpRequest non definito ?");
			}

		
			// Spedizione byte
			if(this.debug)
				this.logger.debug("Spedizione byte...");
//			ConnettoreHTTPCORE5_responseCallback responseCallback = new ConnettoreHTTPCORE5_responseCallback(this, request, httpBody);
//			AsyncRequestProducer requestProducer = new BasicRequestProducer(this.httpRequestBuilder, entityProducer);
//			AsyncResponseConsumer<ConnettoreHTTPCORE5_httpResponse> responseConsumer = new ConnettoreHTTPCORE5_inputStreamEntityConsumer();
//			httpClient.getHttpclient().execute(requestProducer, responseConsumer, HttpClientContext.create(), responseCallback);

			// TBK aggiungere le configurazioni sulla request ? es: request timeout

			// TBK
			ConnettoreHTTPJavaStream_responseCallback responseCallback = new ConnettoreHTTPJavaStream_responseCallback( this, request, httpBody );
			BodyHandler<InputStream> responseBodyHandler = BodyHandlers.ofInputStream();
			CompletableFuture< HttpResponse<InputStream> > sendAsync =
					httpClient.getHttpclient().sendAsync( this.httpRequestBuilder.build(), responseBodyHandler  );
			sendAsync.thenAccept( response -> {
									responseCallback.completed( new ConnettoreHTTPJavaStream_responseConsumer( response ) );
								  })
					 .exceptionally( (t) -> {
						 			// TBK
						 			t.printStackTrace();
						 			responseCallback.failed( t );
						 			return null;
					 			  });
			
			if(this.debug) {
				this.logger.debug("NIO - Terminata gestione richiesta");
			}
			
			this.asynWait = true;
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
			return false;
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
    
    @Override
	protected void setRequestHeader(String key, List<String> values) throws Exception {
    	if(values!=null && !values.isEmpty()) {
    		for (String value : values) {
    			this.httpRequestBuilder.header( key, value );		
			}
    	}
    }
}