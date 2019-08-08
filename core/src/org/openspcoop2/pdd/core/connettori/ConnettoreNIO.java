/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it). 
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

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpTrace;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.nio.entity.NByteArrayEntity;
import org.openspcoop2.core.constants.CostantiConnettori;
import org.openspcoop2.core.constants.TransferLengthModes;
import org.openspcoop2.message.OpenSPCoop2SoapMessage;
import org.openspcoop2.message.constants.Costanti;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.soap.TunnelSoapUtils;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.mdb.ConsegnaContenutiApplicativi;
import org.openspcoop2.utils.NameValue;
import org.openspcoop2.utils.io.Base64Utilities;
import org.openspcoop2.utils.io.notifier.unblocked.PipedUnblockedOutputStream;
import org.openspcoop2.utils.io.notifier.unblocked.PipedUnblockedStream;
import org.openspcoop2.utils.resources.Charset;
import org.openspcoop2.utils.transport.http.HttpBodyParameters;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpUtilities;
import org.openspcoop2.utils.transport.http.RFC2047Encoding;
import org.openspcoop2.utils.transport.http.RFC2047Utilities;



/**
 * Classe utilizzata per effettuare consegne di messaggi Soap, attraverso
 * l'invocazione di un server http. 
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class ConnettoreNIO extends ConnettoreExtBaseHTTP {

	
	/* ********  F I E L D S  P R I V A T I  ******** */


	/** Connessione */
	protected ConnettoreNIO_connection httpClient = null;
	protected HttpRequestBase httpRequest = null;
	protected boolean invocationSuccess = true;
	
	private boolean stream = OpenSPCoop2Properties.getInstance().isNIOConfig_asyncClient_doStream();
	private int dimensione_buffer = OpenSPCoop2Properties.getInstance().getNIOConfig_asyncClient_buffer();

	
	/* Costruttori */
	public ConnettoreNIO(){
		super();
	}
	public ConnettoreNIO(boolean https){
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

			// Parametri di connessione
			StringBuffer bfDebug =new StringBuffer();
			ConnettoreNIO_connectionConfig connectionConfig = new ConnettoreNIO_connectionConfig();
			if(this.sslContextProperties!=null){
				connectionConfig.setSslConfig(this.sslContextProperties);
			}
			if(this.proxyType!=null){
				connectionConfig.setProxyHost(this.proxyHostname);
				connectionConfig.setProxyPort(this.proxyPort);
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
			connectionConfig.setConnectionTimeout(connectionTimeout);
			connectionConfig.setReadTimeout(readConnectionTimeout);
			

			// Connessione
			this.httpClient = ConnettoreNIO_connectionManager.getConnettoreNIO(connectionConfig, this.loader, this.logger.getLogger(), bfDebug);
			if(this.debug) {
				this.logger.debug("Creazione Connessione ...");
				this.logger.debug(bfDebug.toString());
			}
			
			
			// Creazione URL
			if(this.debug)
				this.logger.debug("Creazione URL...");
			this.buildLocation();		
			if(this.debug)
				this.logger.debug("Creazione URL ["+this.location+"]...");
			URL url = new URL( this.location );	

			
			// Collezione header di trasporto per dump
			Properties propertiesTrasportoDebug = null;
			if(this.debug) {
				propertiesTrasportoDebug = new Properties();
			}

			
			// Creazione Richiesta
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
					this.httpRequest = new HttpEntityEnclosingRequestBase() {
						
						HttpRequestMethod httpMethod;
						public HttpEntityEnclosingRequestBase init(HttpRequestMethod httpMethod) {
							this.httpMethod = httpMethod;
							return this;
						}
						
						@Override
						public String getMethod() {
							return this.httpMethod.toString();
						}
						
					}.init(this.httpMethod);	
					break;	
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
				if(this.sbustamentoSoap && soapMessageRequest.countAttachments()>0 && TunnelSoapUtils.isTunnelOpenSPCoopSoap(soapMessageRequest.getSOAPBody())){
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
		

			// Aggiunga del SoapAction Header in caso di richiesta SOAP
			if(this.isSoap && this.sbustamentoSoap == false){
				if(this.debug)
					this.logger.debug("Impostazione soap action...");
				this.soapAction = soapMessageRequest.getSoapAction();
				if(this.soapAction==null){
					this.soapAction="\"OpenSPCoop\"";
				}
				if(MessageType.SOAP_11.equals(this.requestMsg.getMessageType())){
					// NOTA non quotare la soap action, per mantenere la trasparenza della PdD
					setRequestHeader(Costanti.SOAP11_MANDATORY_HEADER_HTTP_SOAP_ACTION,this.soapAction, propertiesTrasportoDebug);
				}
				if(this.debug)
					this.logger.info("SOAP Action inviata ["+this.soapAction+"]",false);
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
	    			this.requestMsg.getTransportRequestContext().removeParameterTrasporto(nv.getName()); // Fix: senno sovrascriveva il vecchio token
	    		}
	    		setRequestHeader(nv.getName(),nv.getValue(), propertiesTrasportoDebug);
	    		if(this.debug)
					this.logger.info("Impostazione autenticazione token (header-name '"+nv.getName()+"' value '"+nv.getValue()+"')",false);
	    	}
			
			// Impostazione Proprieta del trasporto
			if(this.debug)
				this.logger.debug("Impostazione header di trasporto...");
			boolean encodingRFC2047 = false;
			Charset charsetRFC2047 = null;
			RFC2047Encoding encodingAlgorithmRFC2047 = null;
			boolean validazioneHeaderRFC2047 = false;
			if(this.idModulo!=null){
				if(ConsegnaContenutiApplicativi.ID_MODULO.equals(this.idModulo)){
					encodingRFC2047 = this.openspcoopProperties.isEnabledEncodingRFC2047HeaderValue_consegnaContenutiApplicativi();
					charsetRFC2047 = this.openspcoopProperties.getCharsetEncodingRFC2047HeaderValue_consegnaContenutiApplicativi();
					encodingAlgorithmRFC2047 = this.openspcoopProperties.getEncodingRFC2047HeaderValue_consegnaContenutiApplicativi();
					validazioneHeaderRFC2047 = this.openspcoopProperties.isEnabledValidazioneRFC2047HeaderNameValue_consegnaContenutiApplicativi();
				}else{
					encodingRFC2047 = this.openspcoopProperties.isEnabledEncodingRFC2047HeaderValue_inoltroBuste();
					charsetRFC2047 = this.openspcoopProperties.getCharsetEncodingRFC2047HeaderValue_inoltroBuste();
					encodingAlgorithmRFC2047 = this.openspcoopProperties.getEncodingRFC2047HeaderValue_inoltroBuste();
					validazioneHeaderRFC2047 = this.openspcoopProperties.isEnabledValidazioneRFC2047HeaderNameValue_inoltroBuste();
				}
			}
			this.forwardHttpRequestHeader();
			if(this.propertiesTrasporto != null){
				Enumeration<?> enumProperties = this.propertiesTrasporto.keys();
				while( enumProperties.hasMoreElements() ) {
					String key = (String) enumProperties.nextElement();
					String value = (String) this.propertiesTrasporto.get(key);
					if(this.debug)
						this.logger.info("Set Transport Header ["+key+"]=["+value+"]",false);
					
					if(encodingRFC2047){
						if(RFC2047Utilities.isAllCharactersInCharset(value, charsetRFC2047)==false){
							String encoded = RFC2047Utilities.encode(new String(value), charsetRFC2047, encodingAlgorithmRFC2047);
							//System.out.println("@@@@ CODIFICA ["+value+"] in ["+encoded+"]");
							if(this.debug)
								this.logger.info("RFC2047 Encoded value in ["+encoded+"] (charset:"+charsetRFC2047+" encoding-algorithm:"+encodingAlgorithmRFC2047+")",false);
							setRequestHeader(validazioneHeaderRFC2047, key, encoded, this.logger, propertiesTrasportoDebug);
						}
						else{
							setRequestHeader(validazioneHeaderRFC2047, key, value, this.logger, propertiesTrasportoDebug);
						}
					}
					else{
						setRequestHeader(validazioneHeaderRFC2047, key, value, this.logger, propertiesTrasportoDebug);
					}
				}
			}
			
			
			// Impostazione Metodo
			if(this.debug)
				this.logger.info("Impostazione "+this.httpMethod+"...",false);
			HttpBodyParameters httpBody = new  HttpBodyParameters(this.httpMethod, contentTypeRichiesta);

			
			// Spedizione byte
			PipedUnblockedOutputStream streamOut = null;
			boolean consumeRequestMessage = true;
			if(this.followRedirects){
				consumeRequestMessage = false;
			}
			if(httpBody.isDoOutput()){
				
				HttpEntity httpEntity = null;
				
				if(!this.stream || 
						this.debug || 
						TransferLengthModes.CONTENT_LENGTH.equals(this.tlm)) {
					
					if(this.debug)
						this.logger.debug("Spedizione byte (consume-request-message:"+consumeRequestMessage+")...");
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					if(this.isSoap && this.sbustamentoSoap){
						if(this.debug)
							this.logger.debug("Sbustamento...");
						TunnelSoapUtils.sbustamentoMessaggio(soapMessageRequest,out);
					}else{
						this.requestMsg.writeTo(out, consumeRequestMessage);
					}
					out.flush();
					out.close();
					if(this.debug){
						this.logger.info("Messaggio inviato (ContentType:"+contentTypeRichiesta+") :\n"+out.toString(),false);
						
						this.dumpBinarioRichiestaUscita(out.toByteArray(), this.location, propertiesTrasportoDebug);				
					}
					out.flush();
					out.close();
				
					httpEntity = new NByteArrayEntity( out.toByteArray() );
					//((NByteArrayEntity)httpEntity).setContentType( contentTypeRichiesta );
					
					// Impostazione transfer-length
					if(this.debug)
						this.logger.debug("Impostazione transfer-length...");			
					if(TransferLengthModes.TRANSFER_ENCODING_CHUNKED.equals(this.tlm)){					
						((NByteArrayEntity)httpEntity).setChunked(true);
					}
					if(this.debug)
						this.logger.info("Impostazione transfer-length effettuata: "+this.tlm,false);
					
				}
				else {
					PipedUnblockedStream pipe = new PipedUnblockedStream(this.logger.getLogger(), this.dimensione_buffer);
					streamOut = new PipedUnblockedOutputStream(pipe);
					httpEntity = new InputStreamEntity(pipe);
				}
					
				if(this.httpRequest instanceof HttpEntityEnclosingRequestBase){
					((HttpEntityEnclosingRequestBase)this.httpRequest).setEntity(httpEntity);
				}
				else{
					throw new Exception("Tipo ["+this.httpRequest.getClass().getName()+"] non utilizzabile per una richiesta di tipo ["+this.httpMethod+"]");
				}
			}
			
			ConnettoreNIO_responseCallback responseCallback = new ConnettoreNIO_responseCallback(this, request, httpBody);
			this.httpClient.getHttpclient().execute(this.httpRequest, responseCallback);
			
			if(this.stream && streamOut!=null) {
				if(this.isSoap && this.sbustamentoSoap){
					if(this.debug)
						this.logger.debug("Sbustamento...");
					TunnelSoapUtils.sbustamentoMessaggio(soapMessageRequest,streamOut);
				}else{
					this.requestMsg.writeTo(streamOut, consumeRequestMessage);
				}
			}
			
			try {
				this.httpRequest.wait(readConnectionTimeout); // sincronizzo sulla richiesta
			}catch(Throwable t) {
				throw new Exception("Read Timeout expired ("+readConnectionTimeout+")",t);
			}
			
			return this.invocationSuccess;

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
	protected void setRequestHeader(String key,String value) {
    	this.httpRequest.addHeader(key,value);
    }
}



