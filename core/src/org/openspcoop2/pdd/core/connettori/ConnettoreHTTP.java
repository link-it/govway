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

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;

import org.openspcoop2.core.config.ResponseCachingConfigurazione;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.constants.CostantiConnettori;
import org.openspcoop2.core.constants.TipiConnettore;
import org.openspcoop2.core.constants.TransferLengthModes;
import org.openspcoop2.message.OpenSPCoop2SoapMessage;
import org.openspcoop2.message.constants.Costanti;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.soap.TunnelSoapUtils;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.keystore.GestoreKeystoreCaching;
import org.openspcoop2.pdd.mdb.ConsegnaContenutiApplicativi;
import org.openspcoop2.utils.NameValue;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.io.Base64Utilities;
import org.openspcoop2.utils.transport.TransportUtils;
import org.openspcoop2.utils.transport.http.HttpBodyParameters;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpUtilities;
import org.openspcoop2.utils.transport.http.RFC2047Utilities;
import org.openspcoop2.utils.transport.http.SSLUtilities;
import org.openspcoop2.utils.transport.http.WrappedLogSSLSocketFactory;



/**
 * Classe utilizzata per effettuare consegne di messaggi Soap, attraverso
 * l'invocazione di un server http. 
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class ConnettoreHTTP extends ConnettoreBaseHTTP {

	// NOTA: HttpMethod PATCH/LINK/UNLINK funziona solo con java 8
	// Con java7 si ottiene l'errore:
	// Caused by: java.net.ProtocolException: HTTP method PATCH doesn't support output
	//    at sun.net.www.protocol.http.HttpURLConnection.getOutputStream(HttpURLConnection.java:1081)


	
	/* ********  F I E L D S  P R I V A T I  ******** */

	public ByteArrayOutputStream outByte = new ByteArrayOutputStream();
	
	/** SSL Configuration */
	protected boolean connettoreHttps = false;
	protected ConnettoreHTTPSProperties sslContextProperties;
	
	/** Proxy Configuration */
	protected Proxy.Type proxyType = null;
	protected String proxyHostname = null;
	protected int proxyPort;
	protected String proxyUsername;
	protected String proxyPassword;
	
	/** Redirect */
	protected boolean followRedirects = false;
	protected String routeRedirect = null;
	protected int numberRedirect = 0;
	protected int maxNumberRedirects = 5;
	
	/** TransferMode */
	protected TransferLengthModes tlm = null;
	protected int chunkLength = -1;
	
	/** Connessione */
	protected HttpURLConnection httpConn = null;
			
	/* Costruttori */
	public ConnettoreHTTP(){
		this.connettoreHttps = false;
	}
	public ConnettoreHTTP(boolean https){
		this.connettoreHttps = https;
	}
	
	
	
	/* ********  METODI  ******** */

	protected void setSSLContext() throws Exception{
		if(this.connettoreHttps){
			this.sslContextProperties = ConnettoreHTTPSProperties.readProperties(this.properties);
		}
		else {
			String location = this.properties.get(CostantiConnettori.CONNETTORE_LOCATION);
			if(location.trim().startsWith("https")==false){
				if(this.debug){
					this.logger.debug("Location non richiede gestione https ["+location.trim()+"]");
				}
				return;
			}
			boolean urlHttps_overrideDefaultConfiguration = false;
			boolean fruizioni = false;
			if(ConsegnaContenutiApplicativi.ID_MODULO.equals(this.idModulo)){
				urlHttps_overrideDefaultConfiguration = OpenSPCoop2Properties.getInstance().isConnettoreHttp_urlHttps_overrideDefaultConfiguration_consegnaContenutiApplicativi();
				fruizioni = false;
			}
			else{
				// InoltroBuste e InoltroRisposte
				urlHttps_overrideDefaultConfiguration = OpenSPCoop2Properties.getInstance().isConnettoreHttp_urlHttps_overrideDefaultConfiguration_inoltroBuste();
				fruizioni = true;
			}
			if(urlHttps_overrideDefaultConfiguration==false) {
				if(this.debug){
					this.logger.debug("Location https ["+location.trim()+"]; gestione personalizzata dei keystore disabilitata");
				}
				return;
			}
			this.sslContextProperties = ConnettoreHTTP_urlHttps_keystoreRepository.readSSLContext(this.debug, this.logger, this.busta, fruizioni);
		}
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
	
		// Proxy
		if(this.properties.get(CostantiConnettori.CONNETTORE_HTTP_PROXY_TYPE)!=null){
			
			String tipo = this.properties.get(CostantiConnettori.CONNETTORE_HTTP_PROXY_TYPE).trim();
			if(CostantiConnettori.CONNETTORE_HTTP_PROXY_TYPE_VALUE_HTTP.equals(tipo)){
				this.proxyType = Proxy.Type.HTTP;
			}
			else if(CostantiConnettori.CONNETTORE_HTTP_PROXY_TYPE_VALUE_HTTPS.equals(tipo)){
				this.proxyType = Proxy.Type.HTTP;
			}
			else{
				this.errore = "Proprieta' '"+CostantiConnettori.CONNETTORE_HTTP_PROXY_TYPE
						+"' non corretta. Impostato un tipo sconosciuto ["+tipo+"] (valori ammessi: "+CostantiConnettori.CONNETTORE_HTTP_PROXY_TYPE_VALUE_HTTP
						+","+CostantiConnettori.CONNETTORE_HTTP_PROXY_TYPE_VALUE_HTTPS+")";
				return false;
			}
			
			this.proxyHostname = this.properties.get(CostantiConnettori.CONNETTORE_HTTP_PROXY_HOSTNAME);
			if(this.proxyHostname!=null){
				this.proxyHostname = this.proxyHostname.trim();
			}else{
				this.errore = "Proprieta' '"+CostantiConnettori.CONNETTORE_HTTP_PROXY_HOSTNAME+
						"' non impostata, obbligatoria in presenza della proprietà '"+CostantiConnettori.CONNETTORE_HTTP_PROXY_TYPE+"'";
				return false;
			}
			
			String proxyPortTmp = this.properties.get(CostantiConnettori.CONNETTORE_HTTP_PROXY_PORT);
			if(proxyPortTmp!=null){
				proxyPortTmp = proxyPortTmp.trim();
			}else{
				this.errore = "Proprieta' '"+CostantiConnettori.CONNETTORE_HTTP_PROXY_PORT+
						"' non impostata, obbligatoria in presenza della proprietà '"+CostantiConnettori.CONNETTORE_HTTP_PROXY_TYPE+"'";
				return false;
			}
			try{
				this.proxyPort = Integer.parseInt(proxyPortTmp);
			}catch(Exception e){
				this.errore = "Proprieta' '"+CostantiConnettori.CONNETTORE_HTTP_PROXY_PORT+"' non corretta: "+this.readExceptionMessageFromException(e);
				return false;
			}
			
			
			this.proxyUsername = this.properties.get(CostantiConnettori.CONNETTORE_HTTP_PROXY_USERNAME);
			if(this.proxyUsername!=null){
				this.proxyUsername = this.proxyUsername.trim();
			}
			
			this.proxyPassword = this.properties.get(CostantiConnettori.CONNETTORE_HTTP_PROXY_PASSWORD);
			if(this.proxyPassword!=null){
				this.proxyPassword = this.proxyPassword.trim();
			}else{
				if(this.proxyUsername!=null){
					this.errore = "Proprieta' '"+CostantiConnettori.CONNETTORE_HTTP_PROXY_PASSWORD
							+"' non impostata, obbligatoria in presenza della proprietà '"+CostantiConnettori.CONNETTORE_HTTP_PROXY_USERNAME+"'";
					return false;
				}
			}
		}
				
		// TransferMode
		if(ConsegnaContenutiApplicativi.ID_MODULO.equals(this.idModulo)){
			this.tlm = this.openspcoopProperties.getTransferLengthModes_consegnaContenutiApplicativi();
			this.chunkLength = this.openspcoopProperties.getChunkLength_consegnaContenutiApplicativi();
		}
		else{
			// InoltroBuste e InoltroRisposte
			this.tlm = this.openspcoopProperties.getTransferLengthModes_inoltroBuste();
			this.chunkLength = this.openspcoopProperties.getChunkLength_inoltroBuste();
		}
		String tlmTmp = this.properties.get(CostantiConnettori.CONNETTORE_HTTP_DATA_TRANSFER_MODE);
		if(tlmTmp!=null){
			tlmTmp = tlmTmp.trim();
			try{
				this.tlm = TransferLengthModes.getTransferLengthModes(tlmTmp);
			}catch(Exception e){
				this.errore = "Proprieta' '"+CostantiConnettori.CONNETTORE_HTTP_DATA_TRANSFER_MODE
						+"' non impostata correttamente: "+e.getMessage();
				return false;
			}
		}
		if(TransferLengthModes.TRANSFER_ENCODING_CHUNKED.equals(this.tlm)){
			tlmTmp = this.properties.get(CostantiConnettori.CONNETTORE_HTTP_DATA_TRANSFER_MODE_CHUNK_SIZE);
			//this.log.info("PROPERTY! ("+redirectTmp+")");
			if(tlmTmp!=null){
				tlmTmp = tlmTmp.trim();
				this.chunkLength = Integer.parseInt(tlmTmp);
			}
		}
		
		// Redirect
		if(ConsegnaContenutiApplicativi.ID_MODULO.equals(this.idModulo)){
			if(this.isSoap) {
				this.followRedirects = this.openspcoopProperties.isFollowRedirects_consegnaContenutiApplicativi_soap();
			}else {
				this.followRedirects = this.openspcoopProperties.isFollowRedirects_consegnaContenutiApplicativi_rest();
			}
			this.maxNumberRedirects = this.openspcoopProperties.getFollowRedirectsMaxHop_consegnaContenutiApplicativi();
		}
		else{
			// InoltroBuste e InoltroRisposte
			if(this.isSoap) {
				this.followRedirects = this.openspcoopProperties.isFollowRedirects_inoltroBuste_soap();
			}
			else {
				this.followRedirects = this.openspcoopProperties.isFollowRedirects_inoltroBuste_rest();
			}
			this.maxNumberRedirects = this.openspcoopProperties.getFollowRedirectsMaxHop_inoltroBuste();
		}

		String redirectTmp = this.properties.get(CostantiConnettori.CONNETTORE_HTTP_REDIRECT_FOLLOW);
		if(redirectTmp!=null){
			redirectTmp = redirectTmp.trim();
			this.followRedirects = "true".equalsIgnoreCase(redirectTmp) || CostantiConfigurazione.ABILITATO.getValue().equalsIgnoreCase(redirectTmp);
		}
		//this.log.info("FOLLOW! ("+this.followRedirects+")");
		if(this.followRedirects){
			
			redirectTmp = this.properties.get(CostantiConnettori.CONNETTORE_HTTP_REDIRECT_MAX_HOP);
			//this.log.info("PROPERTY! ("+redirectTmp+")");
			if(redirectTmp!=null){
				redirectTmp = redirectTmp.trim();
				this.maxNumberRedirects = Integer.parseInt(redirectTmp);
			}
			
			redirectTmp = this.properties.get(CostantiConnettori._CONNETTORE_HTTP_REDIRECT_NUMBER);
			//this.log.info("PROPERTY! ("+redirectTmp+")");
			if(redirectTmp!=null){
				redirectTmp = redirectTmp.trim();
				this.numberRedirect = Integer.parseInt(redirectTmp);
			}
			
			redirectTmp = this.properties.get(CostantiConnettori._CONNETTORE_HTTP_REDIRECT_ROUTE);
			//this.log.info("PROPERTY! ("+redirectTmp+")");
			if(redirectTmp!=null){
				redirectTmp = redirectTmp.trim();
				this.routeRedirect = redirectTmp;
			}
		}
			
		return sendHTTP(request);

	}

	/**
	 * Si occupa di effettuare la consegna HTTP_POST (sbustando il messaggio SOAP).
	 * Si aspetta di ricevere una risposta non sbustata.
	 *
	 * @return true in caso di consegna con successo, false altrimenti
	 * 
	 */
	protected boolean sendHTTP(ConnettoreMsg request){
		
		try{

			// Gestione https
			SSLContext sslContext = null;
			if(this.sslContextProperties!=null){
				
				// provo a leggere i keystore dalla cache
				if(this.sslContextProperties.getKeyStoreLocation()!=null) {
					try {
						this.sslContextProperties.setKeyStore(GestoreKeystoreCaching.getMerlinKeystore(this.sslContextProperties.getKeyStoreLocation(), 
								this.sslContextProperties.getKeyStoreType(), this.sslContextProperties.getKeyStorePassword()).getKeyStore());
					}catch(Exception e) {
						this.logger.error("Lettura keystore '"+this.sslContextProperties.getKeyStoreLocation()+"' dalla cache fallita: "+e.getMessage(),e);
					}
				}
				if(this.sslContextProperties.getTrustStoreLocation()!=null) {
					try {
						this.sslContextProperties.setTrustStore(GestoreKeystoreCaching.getMerlinTruststore(this.sslContextProperties.getTrustStoreLocation(), 
								this.sslContextProperties.getTrustStoreType(), this.sslContextProperties.getTrustStorePassword()).getTrustStore());
					}catch(Exception e) {
						this.logger.error("Lettura truststore '"+this.sslContextProperties.getTrustStoreLocation()+"' dalla cache fallita: "+e.getMessage(),e);
					}
				}
				if(this.sslContextProperties.getTrustStoreCRLsLocation()!=null) {
					try {
						this.sslContextProperties.setTrustStoreCRLs(GestoreKeystoreCaching.getCRLCertstore(this.sslContextProperties.getTrustStoreCRLsLocation()).getCertStore());
					}catch(Exception e) {
						this.logger.error("Lettura CRLs '"+this.sslContextProperties.getTrustStoreLocation()+"' dalla cache fallita: "+e.getMessage(),e);
					}
				}
				
				if(!this.sslContextProperties.isSecureRandomSet()) {
					if(this.openspcoopProperties.isConnettoreHttps_useSecureRandom()) {
						this.sslContextProperties.setSecureRandom(true);
						if(this.openspcoopProperties.getConnettoreHttps_secureRandomAlgo()!=null) {
							this.sslContextProperties.setSecureRandomAlgorithm(this.openspcoopProperties.getConnettoreHttps_secureRandomAlgo());
						}
					}
				}
				
				StringBuilder bfSSLConfig = new StringBuilder();
				sslContext = SSLUtilities.generateSSLContext(this.sslContextProperties, bfSSLConfig);
				
				if(this.debug)
					this.logger.info(bfSSLConfig.toString(),false);					
			}
			

			// Creazione URL
			if(this.debug)
				this.logger.debug("Creazione URL...");
			this.buildLocation();		
			if(this.debug)
				this.logger.debug("Creazione URL ["+this.location+"]...");
			URL url = new URL( this.location );	

			
			// Collezione header di trasporto per dump
			Map<String, String> propertiesTrasportoDebug = null;
			if(this.isDumpBinario()) {
				propertiesTrasportoDebug = new HashMap<String, String>();
			}

			
			// Creazione Connessione
			URLConnection connection = null;
			if(this.proxyType==null){
				if(this.debug)
					this.logger.info("Creazione connessione alla URL ["+this.location+"]...",false);
				connection = url.openConnection();
			}
			else{
				if(this.debug)
					this.logger.info("Creazione connessione alla URL ["+this.location+"] (via proxy "+
								this.proxyHostname+":"+this.proxyPort+") (username["+this.proxyUsername+"] password["+this.proxyPassword+"])...",false);
					
				if(this.proxyUsername!=null){
					//The problem with the 2nd code is that it sets a new default Authenticator and 
					// I don't want to do that, because this proxy is only used by a part of the application 
					// and a different part of the application could be using a different proxy.
					// Vedi articolo: http://stackoverflow.com/questions/34877470/basic-proxy-authentification-for-https-urls-returns-http-1-0-407-proxy-authentic
					// Authenticator.setDefault(new HttpAuthenticator(this.proxyUsername, this.proxyPassword));
					
					// Soluzione attuale:
					// Dopo aver instaurato la connesione, più sotto nel codice, viene creato l'header Proxy-Authorization
					// NOTA: Works for HTTP only! Doesn't work for HTTPS!
				}
				
				Proxy proxy = new Proxy(this.proxyType, new InetSocketAddress(this.proxyHostname, this.proxyPort));
				connection = url.openConnection(proxy);
			}
			this.httpConn = (HttpURLConnection) connection;	
			
			
			// Imposta Contesto SSL se attivo
			if(this.sslContextProperties!=null){
				HttpsURLConnection httpsConn = (HttpsURLConnection) this.httpConn;
				SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
				if(this.debug) {
					String clientCertificateConfigurated = this.sslContextProperties.getKeyStoreLocation();
					sslSocketFactory = new WrappedLogSSLSocketFactory(sslSocketFactory, 
							this.logger.getLogger(), this.logger.buildMsg(""),
							clientCertificateConfigurated);
				}		
				httpsConn.setSSLSocketFactory(sslSocketFactory);
				
				StringBuilder bfLog = new StringBuilder();
				HostnameVerifier hostnameVerifier = SSLUtilities.generateHostnameVerifier(this.sslContextProperties, bfLog, 
						this.logger.getLogger(), this.loader);
				if(this.debug)
					this.logger.debug(bfLog.toString());
				if(hostnameVerifier!=null){
					httpsConn.setHostnameVerifier(hostnameVerifier);
				}
			}
			else {
				if(this.debug && (this.httpConn instanceof HttpsURLConnection)) {
					HttpsURLConnection httpsConn = (HttpsURLConnection) this.httpConn;
					if(httpsConn.getSSLSocketFactory()!=null) {
						SSLSocketFactory sslSocketFactory = httpsConn.getSSLSocketFactory();
						String clientCertificateConfigurated = SSLUtilities.getJvmHttpsClientCertificateConfigurated();
						sslSocketFactory = new WrappedLogSSLSocketFactory(sslSocketFactory, 
								this.logger.getLogger(), this.logger.buildMsg(""),
								clientCertificateConfigurated);
						httpsConn.setSSLSocketFactory(sslSocketFactory);
					}
				}
			}

			
			// Gestione automatica del redirect
			// The HttpURLConnection‘s follow redirect is just an indicator, in fact it won’t help you to do the “real” http redirection, you still need to handle it manually.
			/*
			if(followRedirect){
				this.httpConn.setInstanceFollowRedirects(true);
			}
			*/
			// Deve essere impostato a false, altrimenti nel caso si intenda leggere gli header o l'input stream di un 302
			// si ottiene il seguente errore:
			//    java.net.HttpRetryException: cannot retry due to redirection, in streaming mode
			this.httpConn.setInstanceFollowRedirects(false);

			
			
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

			
			// Impostazione transfer-length
			if(this.debug)
				this.logger.debug("Impostazione transfer-length...");			
			if(TransferLengthModes.TRANSFER_ENCODING_CHUNKED.equals(this.tlm)){
				HttpUtilities.setChunkedStreamingMode(this.httpConn, this.chunkLength, this.httpMethod, contentTypeRichiesta);
				//this.httpConn.setChunkedStreamingMode(chunkLength);
			}
			if(this.debug)
				this.logger.info("Impostazione transfer-length effettuata (chunkLength:"+this.chunkLength+"): "+this.tlm,false);
			
			
			// Impostazione timeout
			if(this.debug)
				this.logger.debug("Impostazione timeout...");
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
			if(this.debug)
				this.logger.info("Impostazione http timeout CT["+connectionTimeout+"] RT["+readConnectionTimeout+"]",false);
			this.httpConn.setConnectTimeout(connectionTimeout);
			this.httpConn.setReadTimeout(readConnectionTimeout);


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
	    	
	    	
	    	// ForwardProxy
	    	if(this.forwardProxy_headerName!=null && this.forwardProxy_headerValue!=null) {
	    		if(this.requestMsg!=null && this.requestMsg.getTransportRequestContext()!=null) {
	    			this.requestMsg.getTransportRequestContext().removeParameterTrasporto(this.forwardProxy_headerName); // Fix: senno sovrascriveva il vecchio token
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
					String value = (String) this.propertiesTrasporto.get(key);
					if(this.debug)
						this.logger.info("Set Transport Header ["+key+"]=["+value+"]",false);
					
					if(this.encodingRFC2047){
						if(RFC2047Utilities.isAllCharactersInCharset(value, this.charsetRFC2047)==false){
							String encoded = RFC2047Utilities.encode(new String(value), this.charsetRFC2047, this.encodingAlgorithmRFC2047);
							//System.out.println("@@@@ CODIFICA ["+value+"] in ["+encoded+"]");
							if(this.debug)
								this.logger.info("RFC2047 Encoded value in ["+encoded+"] (charset:"+this.charsetRFC2047+" encoding-algorithm:"+this.encodingAlgorithmRFC2047+")",false);
							setRequestHeader(this.validazioneHeaderRFC2047, key, encoded, this.logger, propertiesTrasportoDebug);
						}
						else{
							setRequestHeader(this.validazioneHeaderRFC2047, key, value, this.logger, propertiesTrasportoDebug);
						}
					}
					else{
						setRequestHeader(this.validazioneHeaderRFC2047, key, value, this.logger, propertiesTrasportoDebug);
					}
				}
			}
			
			
			// Impostazione Metodo
			if(this.debug)
				this.logger.info("Impostazione "+this.httpMethod+"...",false);
			HttpUtilities.setStream(this.httpConn, this.httpMethod, contentTypeRichiesta);
			HttpBodyParameters httpBody = new  HttpBodyParameters(this.httpMethod, contentTypeRichiesta);
//			this.httpConn.setRequestMethod( method );
//			this.httpConn.setDoOutput(false);
//			this.httpConn.setDoInput(true);

			
			// Spedizione byte
			if(httpBody.isDoOutput()){
				boolean consumeRequestMessage = true;
				if(this.followRedirects){
					consumeRequestMessage = false;
				}
				if(this.debug)
					this.logger.debug("Spedizione byte (consume-request-message:"+consumeRequestMessage+")...");
				OutputStream out = this.httpConn.getOutputStream();
				if(this.isDumpBinario()) {
					ByteArrayOutputStream bout = new ByteArrayOutputStream();
					if(this.isSoap && this.sbustamentoSoap){
						this.logger.debug("Sbustamento...");
						TunnelSoapUtils.sbustamentoMessaggio(soapMessageRequest,bout);
					}else{
						this.requestMsg.writeTo(bout, consumeRequestMessage);
					}
					bout.flush();
					bout.close();
					out.write(bout.toByteArray());
					bout.close();
					
					this.dumpBinarioRichiestaUscita(bout, contentTypeRichiesta, this.location, propertiesTrasportoDebug);
					
				}else{
					if(this.isSoap && this.sbustamentoSoap){
						if(this.debug)
							this.logger.debug("Sbustamento...");
						TunnelSoapUtils.sbustamentoMessaggio(soapMessageRequest,out);
					}else{
						this.requestMsg.writeTo(out, consumeRequestMessage);
					}
				}
				out.flush();
				out.close();
			}
			else {
				if(this.isDumpBinario()) {
					// devo registrare almeno gli header HTTP
					this.dumpBinarioRichiestaUscita(null, null, this.location, propertiesTrasportoDebug);
				}
			}
			

			// Analisi MimeType e ContentLocation della risposta
			if(this.debug)
				this.logger.debug("Analisi risposta...");
			Map<String, List<String>> mapHeaderHttpResponse = this.httpConn.getHeaderFields();
			if(mapHeaderHttpResponse!=null && mapHeaderHttpResponse.size()>0){
				if(this.propertiesTrasportoRisposta==null){
					this.propertiesTrasportoRisposta = new HashMap<String, String>();
				}
				Iterator<String> itHttpResponse = mapHeaderHttpResponse.keySet().iterator();
				while(itHttpResponse.hasNext()){
					String keyHttpResponse = itHttpResponse.next();
					List<String> valueHttpResponse = mapHeaderHttpResponse.get(keyHttpResponse);
					StringBuilder bfHttpResponse = new StringBuilder();
					for(int i=0;i<valueHttpResponse.size();i++){
						if(i>0){
							bfHttpResponse.append(",");
						}
						bfHttpResponse.append(valueHttpResponse.get(i));
					}
					if(keyHttpResponse==null){ // Check per evitare la coppia che ha come chiave null e come valore HTTP OK 200
						keyHttpResponse=HttpConstants.RETURN_CODE;
					}
					if(this.debug)
						this.logger.debug("HTTP risposta ["+keyHttpResponse+"] ["+bfHttpResponse.toString()+"]...");
					this.propertiesTrasportoRisposta.put(keyHttpResponse, bfHttpResponse.toString());
				}
			}
			
			// TipoRisposta
			this.tipoRisposta = TransportUtils.getObjectAsString(mapHeaderHttpResponse, HttpConstants.CONTENT_TYPE);
			
			// ContentLength della risposta
			String contentLenghtString = TransportUtils.getObjectAsString(mapHeaderHttpResponse, HttpConstants.CONTENT_LENGTH);
			if(contentLenghtString!=null){
				this.contentLength = Integer.valueOf(contentLenghtString);
			}
			else{
				if(this.httpConn.getContentLength()>0){
					this.contentLength = this.httpConn.getContentLength();
				}
			}		
			
		
			// Parametri di imbustamento
			if(this.isSoap){
				this.imbustamentoConAttachment = false;
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
			
			// return code
			this.codice = this.httpConn.getResponseCode();
			this.resultHTTPMessage = this.httpConn.getResponseMessage();
			
			if(this.codice>=400){
				this.isResponse = this.httpConn.getErrorStream();
			}
			else{
				if(this.codice>299){
					
					String redirectLocation = TransportUtils.getObjectAsString(mapHeaderHttpResponse, HttpConstants.REDIRECT_LOCATION);
					
					// 3XX
					if(this.followRedirects){
								
						if(redirectLocation==null){
							throw new Exception("Non è stato rilevato l'header HTTP ["+HttpConstants.REDIRECT_LOCATION+"] necessario alla gestione del Redirect (code:"+this.codice+")"); 
						}
						
						TransportUtils.removeObject(request.getConnectorProperties(), CostantiConnettori.CONNETTORE_LOCATION);
						TransportUtils.removeObject(request.getConnectorProperties(), CostantiConnettori._CONNETTORE_HTTP_REDIRECT_NUMBER);
						TransportUtils.removeObject(request.getConnectorProperties(), CostantiConnettori._CONNETTORE_HTTP_REDIRECT_ROUTE);
						request.getConnectorProperties().put(CostantiConnettori.CONNETTORE_LOCATION, redirectLocation);
						request.getConnectorProperties().put(CostantiConnettori._CONNETTORE_HTTP_REDIRECT_NUMBER, (this.numberRedirect+1)+"" );
						if(this.routeRedirect!=null){
							request.getConnectorProperties().put(CostantiConnettori._CONNETTORE_HTTP_REDIRECT_ROUTE, this.routeRedirect+" -> "+redirectLocation );
						}else{
							request.getConnectorProperties().put(CostantiConnettori._CONNETTORE_HTTP_REDIRECT_ROUTE, redirectLocation );
						}
						
						this.logger.warn("(hope:"+(this.numberRedirect+1)+") Redirect verso ["+redirectLocation+"] ...");
						
						if(this.numberRedirect==this.maxNumberRedirects){
							throw new Exception("Gestione redirect (code:"+this.codice+" "+HttpConstants.REDIRECT_LOCATION+":"+redirectLocation+") non consentita ulteriormente, sono già stati gestiti "+this.maxNumberRedirects+" redirects: "+this.routeRedirect);
						}
						
						boolean acceptOnlyReturnCode_307 = false;
						if(this.isSoap) {
							if(ConsegnaContenutiApplicativi.ID_MODULO.equals(this.idModulo)){
								acceptOnlyReturnCode_307 = this.openspcoopProperties.isAcceptOnlyReturnCode_307_consegnaContenutiApplicativi();
							}
							else{
								// InoltroBuste e InoltroRisposte
								acceptOnlyReturnCode_307 = this.openspcoopProperties.isAcceptOnlyReturnCode_307_inoltroBuste();
							}
						}
						if(acceptOnlyReturnCode_307){
							if(this.codice!=307){
								throw new Exception("Return code ["+this.codice+"] (redirect "+HttpConstants.REDIRECT_LOCATION+":"+redirectLocation+") non consentito dal WS-I Basic Profile (http://www.ws-i.org/Profiles/BasicProfile-1.1-2004-08-24.html#HTTP_Redirect_Status_Codes)");
							}
						}
						
						return this.send(request); // caching ricorsivo non serve
						
					}else{
						if(this.isSoap) {
							throw new Exception("Gestione redirect (code:"+this.codice+" "+HttpConstants.REDIRECT_LOCATION+":"+redirectLocation+") non attiva");
						}
						else {
							this.logger.debug("Gestione redirect (code:"+this.codice+" "+HttpConstants.REDIRECT_LOCATION+":"+redirectLocation+") non attiva");
							if(httpBody.isDoInput()){
								this.isResponse = this.httpConn.getInputStream();
								if(this.isResponse==null) {
									this.isResponse = this.httpConn.getErrorStream();
								}
							}
						}
					}
				}
				else{
					if(this.isSoap && this.acceptOnlyReturnCode_202_200){
						if(this.codice!=200 && this.codice!=202){
							throw new Exception("Return code ["+this.codice+"] non consentito dal WS-I Basic Profile (http://www.ws-i.org/Profiles/BasicProfile-1.1-2004-08-24.html#HTTP_Success_Status_Codes)");
						}
					}
					if(httpBody.isDoInput()){
						this.isResponse = this.httpConn.getInputStream();
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
			
			this.normalizeInputStreamResponse();
			
			this.initCheckContentTypeConfiguration();
			
			if(this.isDumpBinario()){
				this.dumpResponse(this.propertiesTrasportoRisposta);
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
	
			// fine HTTP.
	    	if(this.httpConn!=null){
	    		if(this.debug && this.logger!=null)
					this.logger.debug("Chiusura connessione...");
				this.httpConn.disconnect();
	    	}
	    	
	    	// super.disconnect (Per risorse base)
	    	super.disconnect();
			
    	}catch(Exception e){
    		throw new ConnettoreException("Chiusura connessione non riuscita: "+e.getMessage(),e);
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
	    	if(this.routeRedirect!=null){
	    		l = l+" [redirects route path: "+this.routeRedirect+"]";
	    	}
	    	if(this.proxyType!=null){
	    		l = l+" [proxy: "+this.proxyHostname+":"+this.proxyPort+"]";
	    	}
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
    			this.requestMsg.getTransportRequestContext().removeParameterFormBased(nv.getName()); // Fix: senno sovrascriveva il vecchio token
    		}
    		if(this.propertiesUrlBased==null) {
    			this.propertiesUrlBased = new HashMap<String, String>();
    		}
    		this.propertiesUrlBased.put(nv.getName(), nv.getValue());
    	}
		this.location = ConnettoreUtils.buildLocationWithURLBasedParameter(this.requestMsg, 
				this.connettoreHttps ? TipiConnettore.HTTPS.toString() : TipiConnettore.HTTP.toString(), 
				this.propertiesUrlBased, this.location,
				this.getProtocolFactory(), this.idModulo);
		
		this.updateLocation_forwardProxy(this.location);
    }
    

    private void setRequestHeader(boolean validazioneHeaderRFC2047, String key, String value, ConnettoreLogger logger, Map<String, String> propertiesTrasportoDebug) throws Exception {
    	
    	if(validazioneHeaderRFC2047){
    		try{
        		RFC2047Utilities.validHeader(key, value);
        		setRequestHeader(key,value, propertiesTrasportoDebug);
        	}catch(UtilsException e){
        		logger.error(e.getMessage(),e);
        	}
    	}
    	else{
    		setRequestHeader(key,value, propertiesTrasportoDebug);
    	}
    	
    }
    
    @Override
	protected void setRequestHeader(String key,String value) throws Exception {
    	this.httpConn.setRequestProperty(key,value);
    }

}


class HttpAuthenticator extends Authenticator{
	
	private String username;
	private String password;
	
	HttpAuthenticator(String u,String p){
		super();
		this.username = u;
		this.password = p;
	}
	
	@Override
	public PasswordAuthentication getPasswordAuthentication(){
		return new PasswordAuthentication(this.username, this.password.toCharArray());
	}
}




