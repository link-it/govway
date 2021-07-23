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




package org.openspcoop2.pdd.core.connettori.nio;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.hc.client5.http.classic.methods.HttpDelete;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpHead;
import org.apache.hc.client5.http.classic.methods.HttpOptions;
import org.apache.hc.client5.http.classic.methods.HttpPatch;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpTrace;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.core5.http.nio.AsyncEntityProducer;
import org.apache.hc.core5.http.nio.AsyncRequestProducer;
import org.apache.hc.core5.http.nio.AsyncResponseConsumer;
import org.apache.hc.core5.http.nio.entity.BasicAsyncEntityProducer;
import org.apache.hc.core5.http.nio.entity.FileEntityProducer;
import org.apache.hc.core5.http.nio.support.BasicRequestProducer;
import org.openspcoop2.core.constants.CostantiConnettori;
import org.openspcoop2.core.constants.TransferLengthModes;
import org.openspcoop2.core.transazioni.constants.TipoMessaggio;
import org.openspcoop2.message.OpenSPCoop2RestMessage;
import org.openspcoop2.message.OpenSPCoop2SoapMessage;
import org.openspcoop2.message.constants.Costanti;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.soap.TunnelSoapUtils;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.connettori.ConnettoreException;
import org.openspcoop2.pdd.core.connettori.ConnettoreExtBaseHTTP;
import org.openspcoop2.pdd.core.connettori.ConnettoreMsg;
import org.openspcoop2.pdd.core.connettori.CustomHttpCore5Entity;
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
public class ConnettoreHTTPCORE5 extends ConnettoreExtBaseHTTP {

	
	/* ********  F I E L D S  P R I V A T I  ******** */


	/** Connessione */
	//protected ConnettoreHTTPCORE_connection httpClient = null;
	protected HttpUriRequestBase httpRequest = null;
	protected ConnectionConfiguration httpConnectionConfig = null;
	public ConnectionConfiguration getHttpConnectionConfig() {
		return this.httpConnectionConfig;
	}

	private boolean stream = OpenSPCoop2Properties.getInstance().isNIOConfig_asyncClient_doStream();
	private int dimensione_buffer = OpenSPCoop2Properties.getInstance().getNIOConfig_asyncClient_buffer();

	
	/* Costruttori */
	public ConnettoreHTTPCORE5(){
		super();
	}
	public ConnettoreHTTPCORE5(boolean https){
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
			ConnettoreHTTPCORE5_connection httpClient = ConnettoreHTTPCORE5_connectionManager.getConnettoreNIO(this.httpConnectionConfig, this.loader, this.logger, bfDebug);
			if(this.debug) {
				this.logger.debug("Creazione Connessione ...");
				this.logger.debug(bfDebug.toString());
			}
			
			
			// HttpMethod
			if(this.httpMethod==null){
				throw new Exception("HttpRequestMethod non definito");
			}
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
				case LINK:
				case UNLINK:
				default:
					this.httpRequest = new CustomHttpCore5Entity(this.httpMethod, url.toString());
			}
			if(this.httpRequest==null){
				throw new Exception("HttpRequest non definito ?");
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
			
			
			// HttpMethod
			if(this.httpMethod==null){
				throw new Exception("HttpRequestMethod non definito");
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
			AsyncEntityProducer entityProducer = null;
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
				if(this.isDumpBinarioRichiesta() || this.isSoap || hasContentRestBuilded ||
						TransferLengthModes.CONTENT_LENGTH.equals(this.tlm)) {
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
						
						String baseMimeType = ContentTypeUtilities.readBaseTypeFromContentType(contentTypeRichiesta);
						org.apache.hc.core5.http.ContentType ct = org.apache.hc.core5.http.ContentType.create(baseMimeType);
						if(bout.isSerializedOnFileSystem()) {
							entityProducer = new FileEntityProducer(bout.getSerializedFile(), ct, TransferLengthModes.TRANSFER_ENCODING_CHUNKED.equals(this.tlm));
						}
						else {
							entityProducer = new BasicAsyncEntityProducer(bout.toByteArray(), ct, TransferLengthModes.TRANSFER_ENCODING_CHUNKED.equals(this.tlm));
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
						String baseMimeType = ContentTypeUtilities.readBaseTypeFromContentType(contentTypeRichiesta);
						org.apache.hc.core5.http.ContentType ct = org.apache.hc.core5.http.ContentType.create(baseMimeType);
						entityProducer = new ConnettoreHTTPCORE5_inputStreamEntityProducer(isRequest, ct, null, TransferLengthModes.TRANSFER_ENCODING_CHUNKED.equals(this.tlm));
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
			
		
			// Spedizione byte
			if(this.debug)
				this.logger.debug("Spedizione byte...");
			ConnettoreHTTPCORE5_responseCallback responseCallback = new ConnettoreHTTPCORE5_responseCallback(this, request, httpBody);
			//System.out.println("CLIENT ["+httpClient.getHttpclient().getClass().getName()+"]");
			AsyncRequestProducer requestProducer = new BasicRequestProducer(this.httpRequest, entityProducer);
			//SimpleHttpRequest s = new 
			AsyncResponseConsumer<ConnettoreHTTPCORE5_httpResponse> responseConsumer = new ConnettoreHTTPCORE5_inputStreamEntityConsumer();
			httpClient.getHttpclient().execute(requestProducer, responseConsumer, HttpClientContext.create(), responseCallback);
			
			// CAPIRE SE SERVE E SEMMAI BUTTARE VIA LE PROPERTIES AGGIUNTE!
//			
//			if(this.stream && streamOut!=null) {
//				if(this.isSoap && this.sbustamentoSoap){
//					if(this.debug)
//						this.logger.debug("Sbustamento...");
//					TunnelSoapUtils.sbustamentoMessaggio(soapMessageRequest,streamOut);
//				}else{
//					this.requestMsg.writeTo(streamOut, consumeRequestMessage);
//				}
//			}
			
//			try {
//				if(this.debug) {
//					this.logger.debug("NIO - Sync Wait ...");
//				}
//				synchronized (this.httpRequest) {
//					if(this.callbackResponseFinished) { // questo controllo serve per evitare che si vada in wait sleep dopo che la callback e' già terminata (e quindi ha già fatto il notify)
//						// la callback associata alla chiamata precedente 'getHttpclient().execute' è già terminata. Non serve dormire.
//						if(this.debug) {
//							this.logger.debug("NIO - Sync Wait non necessario, callback gia' terminata");
//						}
//					}
//					else {
//						if(this.debug) {
//							this.logger.debug("NIO - Wait ...");
//						}
//						this.httpRequest.wait(readConnectionTimeout); // sincronizzo sulla richiesta
//					}
//				}
//			}catch(Throwable t) {
//				throw new Exception("Read Timeout expired ("+readConnectionTimeout+")",t);
//			}
			
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
    			this.httpRequest.addHeader(key,value);		
			}
    	}
    }
}