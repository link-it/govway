/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.SequenceInputStream;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

import org.apache.commons.io.input.CountingInputStream;
import org.apache.soap.encoding.soapenc.Base64;
import org.openspcoop2.core.api.constants.CostantiApi;
import org.openspcoop2.core.api.constants.MethodType;
import org.openspcoop2.core.api.utils.Imbustamento;
import org.openspcoop2.core.api.utils.Sbustamento;
import org.openspcoop2.core.constants.CostantiConnettori;
import org.openspcoop2.core.constants.TransferLengthModes;
import org.openspcoop2.message.Costanti;
import org.openspcoop2.message.MailcapActivationReader;
import org.openspcoop2.message.MessageUtils;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.OpenSPCoop2MessageParseResult;
import org.openspcoop2.message.SOAPVersion;
import org.openspcoop2.message.SoapUtils;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.autenticazione.Credenziali;
import org.openspcoop2.pdd.mdb.ConsegnaContenutiApplicativi;
import org.openspcoop2.pdd.services.ServletUtils;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.config.IProtocolConfiguration;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.io.notifier.NotifierInputStreamParams;
import org.openspcoop2.utils.resources.Charset;
import org.openspcoop2.utils.resources.HttpUtilities;
import org.openspcoop2.utils.resources.Loader;
import org.openspcoop2.utils.resources.RFC2047Encoding;
import org.openspcoop2.utils.resources.RFC2047Utilities;
import org.openspcoop2.utils.resources.SSLUtilities;



/**
 * Classe utilizzata per effettuare consegne di messaggi Soap, attraverso
 * l'invocazione di un server http. 
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class ConnettoreHTTP extends ConnettoreBase {

	
	/* ********  F I E L D S  P R I V A T I  ******** */

	public ByteArrayOutputStream outByte = new ByteArrayOutputStream();

	/** Msg di richiesta */
	protected OpenSPCoop2Message requestMsg;
	/** Proprieta' del connettore */
	protected java.util.Hashtable<String,String> properties;
	/** Indicazione su di un eventuale sbustamento SOAP */
	protected boolean sbustamentoSoap;
	/** Proprieta' del trasporto che deve gestire il connettore */
	protected java.util.Properties propertiesTrasporto;
	/** Proprieta' urlBased che deve gestire il connettore */
	protected java.util.Properties propertiesUrlBased;
	/** Tipo di Autenticazione */
	//private String tipoAutenticazione;
	/** Credenziali per l'autenticazione */
	protected Credenziali credenziali;
	/** Busta */
	protected Busta busta;
	/** Indicazione se siamo in modalita' debug */
	protected boolean debug = false;

	/** OpenSPCoopProperties */
	protected OpenSPCoop2Properties openspcoopProperties = null;

	/** Identificativo */
	protected String idMessaggio;
	
	/** Logger */
	protected ConnettoreLogger logger = null;

	/** Loader loader */
	protected Loader loader = null;
	
	/** Identificativo Modulo */
	protected String idModulo = null;
	
	/** SSL Configuration */
	protected boolean connettoreHttps = false;
	protected ConnettoreHTTPSProperties sslContextProperties;
	
	/** Proxy Configuration */
	protected Proxy.Type proxyType = null;
	protected String proxyUrl = null;
	protected int proxyPort;
	protected String proxyUsername;
	protected String proxyPassword;
	
	/** Redirect */
	protected boolean followRedirects = false;
	protected String routeRedirect = null;
	protected int numberRedirect = 0;
	protected int maxNumberRedirects = 5;
	
	/** Connessione */
	protected InputStream is = null;
	protected HttpURLConnection httpConn = null;
	
	/** httpMethod */
	protected String httpMethod = null;
	public String getHttpMethod() {
		return this.httpMethod;
	}
	
	/** Indicazione su di un eventuale sbustamento API */
	protected boolean sbustamentoApi;	
	public boolean isSbustamentoApi() {
		return this.sbustamentoApi;
	}
	
	
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
	}
	
	/**
	 * Si occupa di effettuare la consegna.
	 *
	 * @param request Messaggio da Consegnare
	 * @return true in caso di consegna con successo, false altrimenti
	 * 
	 */
	@Override
	public boolean send(ConnettoreMsg request){

		this.openspcoopProperties = OpenSPCoop2Properties.getInstance();
		this.loader = Loader.getInstance();

		if(request==null){
			this.errore = "Messaggio da consegnare is Null (ConnettoreMsg)";
			return false;
		}

		// Raccolta parametri per costruttore logger
		this.properties = request.getConnectorProperties();
		if(this.properties == null)
			this.errore = "Proprieta' del connettore non definite";
		if(this.properties.size() == 0)
			this.errore = "Proprieta' del connettore non definite";
		// - Busta
		this.busta = request.getBusta();
		if(this.busta!=null)
			this.idMessaggio=this.busta.getID();
		// - Debug mode
		if(this.properties.get(CostantiConnettori.CONNETTORE_DEBUG)!=null){
			if("true".equalsIgnoreCase(this.properties.get(CostantiConnettori.CONNETTORE_DEBUG).trim()))
				this.debug = true;
		}
	
		// Logger
		this.logger = new ConnettoreLogger(this.debug, this.idMessaggio, this.getPddContext());
		
		// Raccolta altri parametri
		try{
			this.requestMsg =  request.getRequestMessage();
		}catch(Exception e){
			this.eccezioneProcessamento = e;
			this.logger.error("Errore durante la lettura del messaggio da consegnare: "+this.readExceptionMessageFromException(e),e);
			this.errore = "Errore durante la lettura del messaggio da consegnare: "+this.readExceptionMessageFromException(e);
			return false;
		}
		this.sbustamentoSoap = request.isSbustamentoSOAP();
		this.propertiesTrasporto = request.getPropertiesTrasporto();
		this.propertiesUrlBased = request.getPropertiesUrlBased();
		
		try{
			Object o = this.requestMsg.getContextProperty(CostantiApi.MESSAGGIO_API);
			if(o!=null){
				this.sbustamentoApi = (Boolean) o;
			}
		}catch(Exception e){
			this.eccezioneProcessamento = e;
			this.logger.error("Errore durante la lettura del messaggio da consegnare: "+this.readExceptionMessageFromException(e),e);
			this.errore = "Errore durante la lettura del messaggio da consegnare: "+this.readExceptionMessageFromException(e);
			return false;
		}

		//this.tipoAutenticazione = request.getAutenticazione();
		this.credenziali = request.getCredenziali();

		// analisi messaggio da spedire
		if(this.requestMsg==null){
			this.errore = "Messaggio da consegnare is Null (Msg)";
			return false;
		}

		// analsi i parametri specifici per il connettore
		if(this.properties.get(CostantiConnettori.CONNETTORE_LOCATION)==null){
			this.errore = "Proprieta' '"+CostantiConnettori.CONNETTORE_LOCATION+"' non fornita e richiesta da questo tipo di connettore ["+request.getTipoConnettore()+"]";
			return false;
		}
		
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
			
			this.proxyUrl = this.properties.get(CostantiConnettori.CONNETTORE_HTTP_PROXY_URL);
			if(this.proxyUrl!=null){
				this.proxyUrl = this.proxyUrl.trim();
			}else{
				this.errore = "Proprieta' '"+CostantiConnettori.CONNETTORE_HTTP_PROXY_URL+
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
		
		// Identificativo modulo
		this.idModulo = request.getIdModulo();
				
		// Context per invocazioni handler
		this.outRequestContext = request.getOutRequestContext();
		this.msgDiagnostico = request.getMsgDiagnostico();
		
		// Redirect
		if(ConsegnaContenutiApplicativi.ID_MODULO.equals(this.idModulo)){
			this.followRedirects = this.openspcoopProperties.isFollowRedirects_consegnaContenutiApplicativi();
			this.maxNumberRedirects = this.openspcoopProperties.getFollowRedirectsMaxHop_consegnaContenutiApplicativi();
		}
		else{
			// InoltroBuste e InoltroRisposte
			this.followRedirects = this.openspcoopProperties.isFollowRedirects_inoltroBuste();
			this.maxNumberRedirects = this.openspcoopProperties.getFollowRedirectsMaxHop_inoltroBuste();
		}
		
		String redirectTmp = this.properties.get(CostantiConnettori.CONNETTORE_HTTP_REDIRECT_FOLLOW);
		if(redirectTmp!=null){
			redirectTmp = redirectTmp.trim();
			this.followRedirects = Boolean.parseBoolean(redirectTmp);
		}
		//this.log.info("FOLLOW! ("+this.followRedirects+")");
		if(this.followRedirects){
			
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
		
		Sbustamento sbustamentoAPIengine = null;
		Imbustamento imbustamentoAPIengine = null;
		try{

			// Sbustamento API
			if(this.sbustamentoApi){
				sbustamentoAPIengine = new Sbustamento(this.requestMsg);
			}
			
			
			// Gestione https
			SSLContext sslContext = null;
			if(this.sslContextProperties!=null){
				
				StringBuffer bfSSLConfig = new StringBuffer();
				sslContext = SSLUtilities.generateSSLContext(this.sslContextProperties, bfSSLConfig);
				
				if(this.debug)
					this.logger.info(bfSSLConfig.toString(),false);					
			}
			

			// Creazione URL
			if(this.debug)
				this.logger.debug("Creazione URL...");

			this.location = this.properties.get(CostantiConnettori.CONNETTORE_LOCATION);			
			// Impostazione Proprieta urlBased
			if(this.sbustamentoApi){
				if(this.debug)
					this.logger.debug("Build URL for API ...");
				this.location = sbustamentoAPIengine.buildUrl(this.location,this.propertiesUrlBased);
			}
			else{
				if(this.propertiesUrlBased != null && this.propertiesUrlBased.size()>0){
					this.location = ConnettoreUtils.buildLocationWithURLBasedParameter(this.propertiesUrlBased, this.location);
				}
			}			
			if(this.debug)
				this.logger.debug("Creazione URL ["+this.location+"]...");
			URL url = new URL( this.location );	


			
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
								this.proxyUrl+":"+this.proxyPassword+") (username["+this.proxyUsername+"] password["+this.proxyPassword+"])...",false);
					
				if(this.proxyUsername!=null){
					Authenticator.setDefault(new HttpAuthenticator(this.proxyUsername, this.proxyPassword));
				}
				
				Proxy proxy = new Proxy(this.proxyType, new InetSocketAddress(this.proxyUrl, this.proxyPort));
				connection = url.openConnection(proxy);
			}
			this.httpConn = (HttpURLConnection) connection;	
			
			
			// Imposta Contesto SSL se attivo
			if(this.sslContextProperties!=null){
				HttpsURLConnection httpsConn = (HttpsURLConnection) this.httpConn;
				httpsConn.setSSLSocketFactory(sslContext.getSocketFactory());
				
				StringBuffer bfLog = new StringBuffer();
				HostnameVerifier hostnameVerifier = SSLUtilities.generateHostnameVerifier(this.sslContextProperties, bfLog, 
						this.logger.getLogger(), this.loader);
				if(this.debug)
					this.logger.debug(bfLog.toString());
				if(hostnameVerifier!=null){
					httpsConn.setHostnameVerifier(hostnameVerifier);
				}
			}

			
			// Gestione automatica del redirect
			// The HttpURLConnection‘s follow redirect is just an indicator, in fact it won’t help you to do the “real” http redirection, you still need to handle it manually.
			/*
			if(followRedirect){
				this.httpConn.setInstanceFollowRedirects(true);
			}
			*/
			
			
			
			// Alcune implementazioni richiedono di aggiornare il Content-Type
			this.requestMsg.updateContentType();
			
					
			// Impostazione Content-Type della Spedizione su HTTP	        
			String contentTypeRichiesta = null;
			if(this.debug)
				this.logger.debug("Impostazione content type...");
			if(this.sbustamentoApi){
				contentTypeRichiesta = sbustamentoAPIengine.getContentType();
			}
			else{
				if(this.sbustamentoSoap && this.requestMsg.countAttachments()>0 && SoapUtils.isTunnelOpenSPCoopSoap(this.requestMsg.getSOAPBody())){
					contentTypeRichiesta = SoapUtils.getContentTypeTunnelOpenSPCoopSoap(this.requestMsg.getSOAPBody());
				}else{
					contentTypeRichiesta = this.requestMsg.getContentType();
				}
				if(contentTypeRichiesta==null){
	                                throw new Exception("Content-Type del messaggio da spedire non definito");
				}
			}
			if(this.debug)
				this.logger.info("Impostazione http Content-Type ["+contentTypeRichiesta+"]",false);
			if(contentTypeRichiesta!=null){
				this.httpConn.setRequestProperty("Content-Type",contentTypeRichiesta);
			}
			
			
			// HttpMethod
			this.httpMethod = MethodType.POST.name();
			if(this.sbustamentoApi){
				this.httpMethod = sbustamentoAPIengine.getHttpMethod();
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
				HttpUtilities.setChunkedStreamingMode(this.httpConn, chunkLength, this.httpMethod, contentTypeRichiesta);
				//this.httpConn.setChunkedStreamingMode(chunkLength);
			}
			if(this.debug)
				this.logger.info("Impostazione transfer-length effettuata (chunkLength:"+chunkLength+"): "+tlm,false);
			
			
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
			if(this.debug)
				this.logger.debug("Impostazione soap action...");
			String soapAction = null;
			if(this.sbustamentoSoap == false && this.sbustamentoApi==false){
				soapAction = (String) this.requestMsg.getProperty(Costanti.SOAP_ACTION);
				if(soapAction==null && SOAPVersion.SOAP11.equals(this.requestMsg.getVersioneSoap())){
					soapAction="\"OpenSPCoop\"";
				}
				if(SOAPVersion.SOAP11.equals(this.requestMsg.getVersioneSoap())){
					this.httpConn.setRequestProperty(SOAPVersion.SOAP11_MANDATORY_HEADER_HTTP_SOAP_ACTION,soapAction); // NOTA non quotare la soap action, per mantenere la trasparenza della PdD
				}
				if(this.debug)
					this.logger.info("SOAP Action inviata ["+soapAction+"]",false);
			}

			
			// Authentication BASIC
			if(this.debug)
				this.logger.debug("Impostazione autenticazione...");
			String user = null;
			String password = null;
			if(this.credenziali!=null){
				user = this.credenziali.getUsername();
				password = this.credenziali.getPassword();
			}else{
				user = this.properties.get(CostantiConnettori.CONNETTORE_USERNAME);
				password = this.properties.get(CostantiConnettori.CONNETTORE_PASSWORD);
			}
			if(user!=null && password!=null){
				String authentication = user + ":" + password;
				authentication = CostantiConnettori.HEADER_HTTP_AUTHORIZATION_PREFIX_BASIC + 
				Base64.encode(authentication.getBytes());
				this.httpConn.setRequestProperty(CostantiConnettori.HEADER_HTTP_AUTHORIZATION,authentication);
				if(this.debug)
					this.logger.info("Impostazione autenticazione (username:"+user+" password:"+password+") ["+authentication+"]",false);
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
			if(this.sbustamentoApi){
				if(this.debug)
					this.logger.debug("Aggiunta header di trasporto api...");
				Properties pTrasporto = sbustamentoAPIengine.getTransportProperties();
				if(pTrasporto!=null && pTrasporto.size()>0){
					if(this.propertiesTrasporto==null){
						this.propertiesTrasporto = new Properties();
					}
					this.propertiesTrasporto.putAll(pTrasporto);
					if(this.debug)
						this.logger.debug("Aggiunta header di trasporto api ("+pTrasporto.size()+")");
				}
			}
			if(this.propertiesTrasporto != null){
				Enumeration<?> enumProperties = this.propertiesTrasporto.keys();
				while( enumProperties.hasMoreElements() ) {
					String key = (String) enumProperties.nextElement();
					String value = (String) this.propertiesTrasporto.get(key);
					if(this.debug)
						this.logger.info("Set proprieta' ["+key+"]=["+value+"]",false);
					
					if(encodingRFC2047){
						if(RFC2047Utilities.isAllCharactersInCharset(value, charsetRFC2047)==false){
							String encoded = RFC2047Utilities.encode(new String(value), charsetRFC2047, encodingAlgorithmRFC2047);
							//System.out.println("@@@@ CODIFICA ["+value+"] in ["+encoded+"]");
							if(this.debug)
								this.logger.info("RFC2047 Encoded value in ["+encoded+"] (charset:"+charsetRFC2047+" encoding-algorithm:"+encodingAlgorithmRFC2047+")",false);
							setRequestHeader(validazioneHeaderRFC2047, key, encoded, this.logger);
						}
						else{
							setRequestHeader(validazioneHeaderRFC2047, key, value, this.logger);
						}
					}
					else{
						setRequestHeader(validazioneHeaderRFC2047, key, value, this.logger);
					}
				}
			}
			
			
			// Impostazione Metodo
			if(this.debug)
				this.logger.info("Impostazione "+this.httpMethod+"...",false);
			HttpUtilities.setStream(this.httpConn, this.httpMethod, contentTypeRichiesta);
//			this.httpConn.setRequestMethod( method );
//			this.httpConn.setDoOutput(false);
//			this.httpConn.setDoInput(true);

			
			// Spedizione byte
			boolean consumeRequestMessage = true;
			if(this.followRedirects){
				consumeRequestMessage = false;
			}
			if(this.debug)
				this.logger.debug("Spedizione byte (consume-request-message:"+consumeRequestMessage+")...");
			if(this.sbustamentoApi){
				byte[] resource = sbustamentoAPIengine.getBody();
				if(resource!=null){
					OutputStream out = this.httpConn.getOutputStream();
					if(this.debug)
						this.logger.info("Messaggio inviato (ContentType:"+contentTypeRichiesta+") :\n"+new String(resource),false);
					out.write(resource);
					out.flush();
					out.close();
				}
			}
			else{
				OutputStream out = this.httpConn.getOutputStream();
				if(this.debug){
					ByteArrayOutputStream bout = new ByteArrayOutputStream();
					if(this.sbustamentoSoap){
						this.logger.debug("Sbustamento...");
						SoapUtils.sbustamentoMessaggio(this.requestMsg,bout);
					}else{
						this.requestMsg.writeTo(bout, consumeRequestMessage);
					}
					bout.flush();
					bout.close();
					out.write(bout.toByteArray());
					this.logger.info("Messaggio inviato (ContentType:"+contentTypeRichiesta+") :\n"+bout.toString(),false);
					bout.close();
				}else{
					if(this.sbustamentoSoap){
						if(this.debug)
							this.logger.debug("Sbustamento...");
						SoapUtils.sbustamentoMessaggio(this.requestMsg,out);
					}else{
						this.requestMsg.writeTo(out, consumeRequestMessage);
					}
				}
				out.flush();
				out.close();
			}
			

			// Analisi MimeType e ContentLocation della risposta
			if(this.debug)
				this.logger.debug("Analisi risposta...");
			Map<String, List<String>> mapHeaderHttpResponse = this.httpConn.getHeaderFields();
			if(mapHeaderHttpResponse!=null && mapHeaderHttpResponse.size()>0){
				if(this.propertiesTrasportoRisposta==null){
					this.propertiesTrasportoRisposta = new Properties();
				}
				Iterator<String> itHttpResponse = mapHeaderHttpResponse.keySet().iterator();
				while(itHttpResponse.hasNext()){
					String keyHttpResponse = itHttpResponse.next();
					List<String> valueHttpResponse = mapHeaderHttpResponse.get(keyHttpResponse);
					StringBuffer bfHttpResponse = new StringBuffer();
					for(int i=0;i<valueHttpResponse.size();i++){
						if(i>0){
							bfHttpResponse.append(",");
						}
						bfHttpResponse.append(valueHttpResponse.get(i));
					}
					if(keyHttpResponse==null){ // Check per evitare la coppia che ha come chiave null e come valore HTTP OK 200
						keyHttpResponse=CostantiConnettori.HEADER_HTTP_RETURN_CODE;
					}
					this.propertiesTrasportoRisposta.put(keyHttpResponse, bfHttpResponse.toString());
				}
			}
			
			String tipoRisposta = this.httpConn.getHeaderField(Costanti.CONTENT_TYPE);
			if(tipoRisposta==null){
				tipoRisposta = this.httpConn.getHeaderField(Costanti.CONTENT_TYPE.toLowerCase());
			}
			if(tipoRisposta==null){
				tipoRisposta = this.httpConn.getHeaderField(Costanti.CONTENT_TYPE.toUpperCase());
			}
			String locationRisposta = this.httpConn.getHeaderField(Costanti.CONTENT_LOCATION);
			if(locationRisposta==null){
				locationRisposta = this.httpConn.getHeaderField(Costanti.CONTENT_LOCATION.toLowerCase());
			}
			if(locationRisposta==null){
				locationRisposta = this.httpConn.getHeaderField(Costanti.CONTENT_LOCATION.toUpperCase());
			}
			int contentLenght = -1;
			String contentLenghtString = this.httpConn.getHeaderField(Costanti.CONTENT_LENGTH);
			
			if(contentLenghtString==null){
				contentLenghtString = this.httpConn.getHeaderField(Costanti.CONTENT_LENGTH.toLowerCase());
			}
			if(contentLenghtString==null){
				contentLenghtString = this.httpConn.getHeaderField(Costanti.CONTENT_LENGTH.toUpperCase());
			}
			if(contentLenghtString!=null){
				contentLenght = Integer.valueOf(contentLenghtString);
			}
					
			//System.out.println("TIPO RISPOSTA["+tipoRisposta+"] LOCATION["+locationRisposta+"]");
			
			// ContentLength della risposta
			if(this.httpConn.getContentLength()>0){
				this.contentLength = this.httpConn.getContentLength();
			}
			
			// Parametri di imbustamento
			boolean imbustamentoConAttachment = false;
			if("true".equals(this.httpConn.getHeaderField(this.openspcoopProperties.getTunnelSOAPKeyWord_headerTrasporto()))){
				imbustamentoConAttachment = true;
			}
			String mimeTypeAttachment = this.httpConn.getHeaderField(this.openspcoopProperties.getTunnelSOAPKeyWordMimeType_headerTrasporto());
			if(mimeTypeAttachment==null)
				mimeTypeAttachment = Costanti.CONTENT_TYPE_OPENSPCOOP2_TUNNEL_SOAP;
			//System.out.println("IMB["+imbustamentoConAttachment+"] MIME["+mimeTypeAttachment+"]");

			// Ricezione Risposta
			boolean acceptOnlyReturnCode_202_200 = true;
			if(this.sbustamentoApi){
				acceptOnlyReturnCode_202_200 = false;
			}
			else{
				if(ConsegnaContenutiApplicativi.ID_MODULO.equals(this.idModulo)){
					acceptOnlyReturnCode_202_200 = this.openspcoopProperties.isAcceptOnlyReturnCode_200_202_consegnaContenutiApplicativi();
				}
				else{
					// InoltroBuste e InoltroRisposte
					acceptOnlyReturnCode_202_200 = this.openspcoopProperties.isAcceptOnlyReturnCode_200_202_inoltroBuste();
				}
			}
			if(this.debug)
				this.logger.debug("Analisi risposta input stream e risultato http...");
			
			// return code
			this.codice = this.httpConn.getResponseCode();
			String resultHTTPMessage = this.httpConn.getResponseMessage();
			
			if(this.codice>=400){
				this.is = this.httpConn.getErrorStream();
			}
			else{
				if(this.codice>299){
					
					String redirectLocation = this.httpConn.getHeaderField(CostantiConnettori.HEADER_HTTP_REDIRECT_LOCATION);
					if(redirectLocation==null){
						redirectLocation = this.httpConn.getHeaderField(CostantiConnettori.HEADER_HTTP_REDIRECT_LOCATION.toLowerCase());
					}
					if(redirectLocation==null){
						redirectLocation = this.httpConn.getHeaderField(CostantiConnettori.HEADER_HTTP_REDIRECT_LOCATION.toUpperCase());
					}
					if(redirectLocation==null){
						throw new Exception("Non è stato rilevato l'header HTTP ["+CostantiConnettori.HEADER_HTTP_REDIRECT_LOCATION+"] necessario alla gestione del Redirect (code:"+this.codice+")"); 
					}
					
					// 3XX
					if(this.followRedirects){
												
						request.getConnectorProperties().remove(CostantiConnettori.CONNETTORE_LOCATION);
						request.getConnectorProperties().remove(CostantiConnettori._CONNETTORE_HTTP_REDIRECT_NUMBER);
						request.getConnectorProperties().remove(CostantiConnettori._CONNETTORE_HTTP_REDIRECT_ROUTE);
						request.getConnectorProperties().put(CostantiConnettori.CONNETTORE_LOCATION, redirectLocation);
						request.getConnectorProperties().put(CostantiConnettori._CONNETTORE_HTTP_REDIRECT_NUMBER, (this.numberRedirect+1)+"" );
						if(this.routeRedirect!=null){
							request.getConnectorProperties().put(CostantiConnettori._CONNETTORE_HTTP_REDIRECT_ROUTE, this.routeRedirect+" -> "+redirectLocation );
						}else{
							request.getConnectorProperties().put(CostantiConnettori._CONNETTORE_HTTP_REDIRECT_ROUTE, redirectLocation );
						}
						
						this.logger.warn("(hope:"+(this.numberRedirect+1)+") Redirect verso ["+redirectLocation+"] ...");
						
						if(this.numberRedirect==this.maxNumberRedirects){
							throw new Exception("Gestione redirect (code:"+this.codice+" "+CostantiConnettori.HEADER_HTTP_REDIRECT_LOCATION+":"+redirectLocation+") non consentita ulteriormente, sono già stati gestiti "+this.maxNumberRedirects+" redirects: "+this.routeRedirect);
						}
						
						boolean acceptOnlyReturnCode_307 = false;
						if(ConsegnaContenutiApplicativi.ID_MODULO.equals(this.idModulo)){
							acceptOnlyReturnCode_307 = this.openspcoopProperties.isAcceptOnlyReturnCode_307_consegnaContenutiApplicativi();
						}
						else{
							// InoltroBuste e InoltroRisposte
							acceptOnlyReturnCode_307 = this.openspcoopProperties.isAcceptOnlyReturnCode_307_inoltroBuste();
						}
						if(acceptOnlyReturnCode_307){
							if(this.codice!=307){
								throw new Exception("Return code ["+this.codice+"] (redirect "+CostantiConnettori.HEADER_HTTP_REDIRECT_LOCATION+":"+redirectLocation+") non consentito dal WS-I Basic Profile (http://www.ws-i.org/Profiles/BasicProfile-1.1-2004-08-24.html#HTTP_Redirect_Status_Codes)");
							}
						}
						
						return this.send(request);
						
					}else{
						throw new Exception("Gestione redirect (code:"+this.codice+" "+CostantiConnettori.HEADER_HTTP_REDIRECT_LOCATION+":"+redirectLocation+") non attiva");
					}
				}
				else{
					if(acceptOnlyReturnCode_202_200){
						if(this.codice!=200 && this.codice!=202){
							throw new Exception("Return code ["+this.codice+"] non consentito dal WS-I Basic Profile (http://www.ws-i.org/Profiles/BasicProfile-1.1-2004-08-24.html#HTTP_Success_Status_Codes)");
						}
					}
					this.is = this.httpConn.getInputStream();
				}
			}
			

			
			/* ------------  PostOutRequestHandler ------------- */
			this.postOutRequest();
			
			
			
			
			/* ------------  PreInResponseHandler ------------- */
			this.preInResponse();
			
			// Lettura risposta parametri NotifierInputStream per la risposta
			NotifierInputStreamParams notifierInputStreamParams = null;
			if(this.preInResponseContext!=null){
				notifierInputStreamParams = this.preInResponseContext.getNotifierInputStreamParams();
			}
			
			
			
			//Se non e' null, controllo che non sia vuoto.
			byte[] b = new byte[1];
			if(this.is!=null){
				if(this.is.read(b) == -1) {
					this.is = null;
				} else {
					// Metodo alternativo: java.io.PushbackInputStream
					this.is = new SequenceInputStream(new ByteArrayInputStream(b),this.is);
				}
			}
			else{
				this.logger.info("Stream di risposta (return-code:"+this.codice+") is null",true);
			}
			
			String tipoLetturaRisposta = null;
			
			
			if(this.sbustamentoApi){
				
				
				if(this.debug)
					this.logger.debug("gestione API in corso ...");
				
				imbustamentoAPIengine = new Imbustamento(this.requestMsg.getVersioneSoap(), notifierInputStreamParams, this.is, MethodType.toEnumConstant(this.httpMethod), 
						tipoRisposta, this.propertiesTrasportoRisposta, this.codice, resultHTTPMessage, this.openspcoopProperties.getAPIServicesWhiteListResponseHeaderList());
				
				this.responseMsg = imbustamentoAPIengine.getMessage();
				
			}
			else{
			
				// gestione ordinaria via WS/SOAP
			
				if(this.debug)
					this.logger.debug("gestione WS/SOAP in corso ...");
				
				/*
				 * Se il messaggio e' un html di errore me ne esco 
				 */			
				if(this.codice>=400 && tipoRisposta!=null && tipoRisposta.contains(Costanti.CONTENT_TYPE_HTML)){
					tipoLetturaRisposta = "("+this.codice+") " + resultHTTPMessage ;
					
					// Registro HTML ricevuto.
					String htmlRicevuto = null;
					if(this.is!=null){
						ByteArrayOutputStream bout = new ByteArrayOutputStream();
						byte [] readB = new byte[Utilities.DIMENSIONE_BUFFER];
						int readByte = 0;
						while((readByte = this.is.read(readB))!= -1){
							bout.write(readB,0,readByte);
						}
						this.is.close();
						bout.flush();
						bout.close();
						htmlRicevuto = bout.toString();
					}
					
					if(this.debug){
						if(htmlRicevuto!=null && !"".equals(htmlRicevuto)){
							this.logger.info("Messaggio ricevuto (ContentType:"+tipoRisposta+") :\n"+htmlRicevuto,false);
						}
						else{
							this.logger.info("Messaggio ricevuto (ContentType:"+tipoRisposta+") senza contenuto nell'http-reply",false);
						}
					}
					
					if(htmlRicevuto!=null && !"".equals(htmlRicevuto))
						this.errore = tipoLetturaRisposta +"\nhttp response: "+htmlRicevuto;
					else
						this.errore = tipoLetturaRisposta;
					return false;
				}
				
				if(this.is!=null){
					
					SOAPVersion soapVersionRisposta = null;
					//Exception soapVersionUnknown = null;
					IProtocolConfiguration protocolConfiguration = this.getProtocolFactory().createProtocolConfiguration();
					if(tipoRisposta!=null){
						try{
							soapVersionRisposta = ServletUtils.getVersioneSoap(this.logger.getLogger(),tipoRisposta);
						}catch(Exception e){
							this.logger.error("SOAPVersion response unknown: "+e.getMessage(),e);
							//soapVersionUnknown = e;
						}
					}
					
					if(tipoRisposta==null || 
							soapVersionRisposta==null ||
							!soapVersionRisposta.equals(this.requestMsg.getVersioneSoap()) || 
							!ServletUtils.isContentTypeSupported(this.requestMsg.getVersioneSoap(), this.getProtocolFactory())){
												
						boolean checkContentType = true;
						if(this.idModulo!=null){
							if(ConsegnaContenutiApplicativi.ID_MODULO.equals(this.idModulo)){
								checkContentType = this.openspcoopProperties.isControlloContentTypeAbilitatoRicezioneBuste();
							}else{
								checkContentType = this.openspcoopProperties.isControlloContentTypeAbilitatoRicezioneContenutiApplicativi();
							}
						}
						String msgErrore = null;
						if(tipoRisposta==null){
							msgErrore = "Header Content-Type non definito nell'http reply";
						}
						else if(soapVersionRisposta==null){
							msgErrore = "Il valore dell'header HTTP Content-Type definito nell'http reply ("+tipoRisposta
									+") non rientra tra quelli conosciuti ("+SOAPVersion.getKnownContentTypesAsString()+")";
						}
						else if(!soapVersionRisposta.equals(this.requestMsg.getVersioneSoap())){
							msgErrore = "Header Content-Type definito nell'http reply ("+tipoRisposta+") indica una versione "+soapVersionRisposta.getSoapVersionAsString()
									+" non compatibile con la versione "+this.requestMsg.getVersioneSoap().getSoapVersionAsString()+" del messaggio di richiesta";
						}
						else{
							msgErrore = "Il valore dell'header HTTP Content-Type definito nell'http reply ("+tipoRisposta
									+") non rientra tra quelli supportati dal protocollo ("+SOAPVersion.getKnownContentTypesAsString(protocolConfiguration.isSupportoSOAP11(), protocolConfiguration.isSupportoSOAP12())+")";
						}
						
						if(checkContentType){
							Exception e = new Exception(msgErrore);
							this.getPddContext().addObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RISPOSTA_NON_RICONOSCIUTO, true);
							this.getPddContext().addObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RISPOSTA_NON_RICONOSCIUTO_PARSE_EXCEPTION,
									MessageUtils.buildParseException(e));
							throw e;
						}else{
							this.logger.warn(msgErrore+"; viene utilizzato forzatamente il tipo: "+SOAPVersion.SOAP11.getContentTypeForMessageWithoutAttachments());
							tipoRisposta = SOAPVersion.SOAP11.getContentTypeForMessageWithoutAttachments();
						}
					}
					
	
					try{
						if(this.debug){
							// Registro Debug.
							ByteArrayOutputStream bout = new ByteArrayOutputStream();
							byte [] readB = new byte[Utilities.DIMENSIONE_BUFFER];
							int readByte = 0;
							while((readByte = this.is.read(readB))!= -1){
								bout.write(readB,0,readByte);
							}
							this.is.close();
							bout.flush();
							bout.close();
							this.logger.info("Messaggio ricevuto (ContentType:"+tipoRisposta+") :\n"+bout.toString(),false);
							// Creo nuovo inputStream
							this.is = new ByteArrayInputStream(bout.toByteArray());
						}
						
						if(this.sbustamentoSoap==false){
							if(this.debug)
								this.logger.debug("Ricostruzione normale...");
							
							// Ricostruzione messaggio soap: secondo parametro a false, indica che il messaggio e' gia un SOAPMessage
							tipoLetturaRisposta = "Parsing Risposta SOAP";
							
							
							
							if(contentLenght>0){
								OpenSPCoop2MessageParseResult pr = OpenSPCoop2MessageFactory.getMessageFactory().createMessage(this.is,notifierInputStreamParams,false,tipoRisposta,locationRisposta, this.openspcoopProperties.isFileCacheEnable(), this.openspcoopProperties.getAttachmentRepoDir(), this.openspcoopProperties.getFileThreshold());	
								if(pr.getParseException()!=null){
									this.getPddContext().addObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RISPOSTA_NON_RICONOSCIUTO_PARSE_EXCEPTION, pr.getParseException());
								}
								this.responseMsg = pr.getMessage_throwParseException();
							}
							else if(contentLenght==0){
								this.responseMsg = null;
							}
							else{
								//non ho trovato ContentLength. Devo scoprire se c'e' un payload. Costruisco il messaggio e poi provo ad
								//accedere all'envelope
								try{
									byte [] buffer = new byte[1];
									int letti = this.is.read(buffer);
									if(letti==1){
										// Per evitare il propblema del 'Premature end of file' che causa una system.out sul server.log di jboss
										SequenceInputStream sInput = new SequenceInputStream(new ByteArrayInputStream(buffer), this.is);
										OpenSPCoop2MessageParseResult pr = OpenSPCoop2MessageFactory.getMessageFactory().createMessage(sInput,notifierInputStreamParams,false,tipoRisposta,locationRisposta, this.openspcoopProperties.isFileCacheEnable(), this.openspcoopProperties.getAttachmentRepoDir(), this.openspcoopProperties.getFileThreshold());
										if(pr.getParseException()!=null){
											this.getPddContext().addObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RISPOSTA_NON_RICONOSCIUTO_PARSE_EXCEPTION, pr.getParseException());
										}
										this.responseMsg = pr.getMessage_throwParseException();
									}
								}catch(Exception e){
									this.responseMsg=null;
									// L'errore 'premature end of file' consiste solo nel fatto che una risposta non e' stata ricevuta.
									boolean result2XX = (this.codice>=200 && this.codice<=299);
									boolean premature =  Utilities.existsInnerMessageException(e, "Premature end of file", true) && result2XX;
									// Se non ho un premature, ed un errore di lettura in 200, allora devo segnalare l'errore, altrimenti comunque 
									// il msg ritornato e' null e nel codiceStato vi e' l'errore.
									
									if( premature == false ){
										this.eccezioneProcessamento = e;
										this.errore = "Errore avvenuto durante la consegna HTTP (lettura risposta): " + this.readExceptionMessageFromException(e);
										this.logger.error("Errore avvenuto durante la consegna HTTP (lettura risposta): " + this.readExceptionMessageFromException(e),e);
										if(result2XX){
											return false;
										}
									}
								}
							}
							
							// Check eventuale 'xml instruction presente nel body' 
							/*try{
								this.checkXMLInstructionTargetMachine();
							}catch(Exception e){
								this.errore = "Errore avvenuto durante la consegna HTTP (salvataggio risposta, check xml instruction): " + e.getMessage();
								this.log.error("Errore avvenuto durante la consegna HTTP (salvataggio risposta, check xml instruction)",e);
								return false;
							}*/
						}else{
							CountingInputStream cis = null;
							try{
								cis = new CountingInputStream(this.is);
								
								if(imbustamentoConAttachment){
									if(this.debug)
										this.logger.debug("Imbustamento con attachments...");
									
									// Imbustamento per Tunnel OpenSPCoop
									tipoLetturaRisposta = "Costruzione messaggio SOAP per Tunnel con mimeType "+mimeTypeAttachment;
									try{
										this.responseMsg = SoapUtils.
										imbustamentoMessaggioConAttachment(this.requestMsg.getVersioneSoap(), cis,mimeTypeAttachment,
												MailcapActivationReader.existsDataContentHandler(mimeTypeAttachment),tipoRisposta, this.openspcoopProperties.getHeaderSoapActorIntegrazione());
									}catch(UtilsException e){
										if(e.getMessage()!=null && e.getMessage().equals("Contenuto da imbustare non presente")){
											// L'errore consiste solo nel fatto che una risposta non e' stata ricevuta.
										}else{
											throw e;
										}
									}
								}else{
									if(tipoRisposta!=null && tipoRisposta.contains("multipart/related")){
										
										if(this.debug)
											this.logger.debug("Imbustamento messaggio multipart/related...");
										
										// Imbustamento di un messaggio multipart/related
										tipoLetturaRisposta = "Imbustamento messaggio multipart/related in un SOAP WithAttachments";
										java.io.ByteArrayOutputStream byteBuffer = new java.io.ByteArrayOutputStream();
										byte [] readB = new byte[Utilities.DIMENSIONE_BUFFER];
										int readByte = 0;
										while((readByte = cis.read(readB))!= -1){
											byteBuffer.write(readB,0,readByte);
										}
										if(byteBuffer.size()>0){
											OpenSPCoop2MessageParseResult pr = SoapUtils.imbustamentoMessaggio(notifierInputStreamParams,byteBuffer.toByteArray(),this.openspcoopProperties.isDeleteInstructionTargetMachineXml(), this.openspcoopProperties.isFileCacheEnable(), this.openspcoopProperties.getAttachmentRepoDir(), this.openspcoopProperties.getFileThreshold());
											if(pr.getParseException()!=null){
												this.getPddContext().addObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RISPOSTA_NON_RICONOSCIUTO_PARSE_EXCEPTION, pr.getParseException());
											}
											this.responseMsg = pr.getMessage_throwParseException();
										}
									}else{
										
										if(this.debug)
											this.logger.debug("Imbustamento messaggio...");
										// Imbustamento di un messaggio normale: secondo parametro a true, indica che il messaggio deve essere imbustato in un msg SOAP
										tipoLetturaRisposta = "Imbustamento messaggio xml in un messaggio SOAP";
										OpenSPCoop2MessageParseResult pr = OpenSPCoop2MessageFactory.getMessageFactory().createMessage(cis,notifierInputStreamParams,true,tipoRisposta,locationRisposta, this.openspcoopProperties.isFileCacheEnable(), this.openspcoopProperties.getAttachmentRepoDir(), this.openspcoopProperties.getFileThreshold());
										if(pr.getParseException()!=null){
											this.getPddContext().addObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RISPOSTA_NON_RICONOSCIUTO_PARSE_EXCEPTION, pr.getParseException());
										}
										this.responseMsg = pr.getMessage_throwParseException();
									}
		
								}
								
								if(this.responseMsg!=null){
									this.responseMsg.updateIncomingMessageContentLength(cis.getByteCount());
								}
								
							}finally{
								try{
									if(cis!=null){
										cis.close();
									}
								}catch(Exception eClose){}
							}
							
						}
						try{
							if(this.responseMsg!=null){
								this.responseMsg.getSOAPPart().getEnvelope();
							}
						}
						catch(Exception e){
							this.responseMsg=null;
							// L'errore 'premature end of file' consiste solo nel fatto che una risposta non e' stata ricevuta.
							boolean result2XX = (this.codice>=200 && this.codice<=299);
							boolean premature =  Utilities.existsInnerMessageException(e, "Premature end of file", true) && result2XX;
							// Se non ho un premature, ed un errore di lettura in 200, allora devo segnalare l'errore, altrimenti comunque 
							// il msg ritornato e' null e nel codiceStato vi e' l'errore.
							
							if( premature == false ){
								this.eccezioneProcessamento = e;
								this.errore = "Errore avvenuto durante la consegna HTTP (lettura risposta): " + this.readExceptionMessageFromException(e);
								this.logger.error("Errore avvenuto durante la consegna HTTP (lettura risposta): " + this.readExceptionMessageFromException(e),e);
								if(result2XX){
									return false;
								}
							}
						}
					}catch(Exception e){
						this.eccezioneProcessamento = e;
						this.errore = "Errore avvenuto durante la consegna HTTP ("+tipoLetturaRisposta+"): " + this.readExceptionMessageFromException(e);
						this.logger.error("Errore avvenuto durante la consegna HTTP ("+tipoLetturaRisposta+")",e);
						return false;
					}
	
					
	
					// save Msg
					if(this.debug)
						this.logger.debug("Save messaggio...");
					try{
						if(this.responseMsg!=null){
							// save changes.
							// N.B. il countAttachments serve per il msg con attachments come saveMessage!
							if(this.responseMsg.countAttachments()==0){
								this.responseMsg.getSOAPPart();
							}
						}
					}catch(Exception e){
						this.eccezioneProcessamento = e;
						this.errore = "Errore avvenuto durante la consegna HTTP (salvataggio risposta): " + this.readExceptionMessageFromException(e);
						this.logger.error("Errore avvenuto durante la consegna HTTP (salvataggio risposta): " + this.readExceptionMessageFromException(e),e);
						return false;
					}
	
				}
				
			}

			if(this.debug)
				this.logger.info("Gestione invio/risposta http effettuata con successo",false);
			
			return true;

		}  catch(Exception e){ 
			this.eccezioneProcessamento = e;
			this.errore = "Errore avvenuto durante la consegna HTTP: "+this.readExceptionMessageFromException(e);
			this.logger.error("Errore avvenuto durante la consegna HTTP: "+this.readExceptionMessageFromException(e),e);
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
	    	if(this.is!=null){
	    		if(this.debug && this.logger!=null)
	    			this.logger.debug("Chiusura socket...");
				this.is.close();
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
    	if(this.routeRedirect==null){
    		return this.location;
    	}
    	else{
    		return this.location+" [redirects route path: "+this.routeRedirect+"]";
    	}
    }
    

    private void setRequestHeader(boolean validazioneHeaderRFC2047, String key, String value, ConnettoreLogger logger) {
    	
    	if(validazioneHeaderRFC2047){
    		try{
        		RFC2047Utilities.validHeader(key, value);
        		this.httpConn.setRequestProperty(key,value);
        	}catch(UtilsException e){
        		logger.error(e.getMessage(),e);
        	}
    	}
    	else{
    		this.httpConn.setRequestProperty(key,value);
    	}
    	
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




