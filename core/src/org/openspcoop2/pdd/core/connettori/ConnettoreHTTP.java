/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2014 Link.it srl (http://link.it). All rights reserved. 
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
import java.io.FileInputStream;
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
import java.security.KeyStore;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import org.apache.log4j.Logger;
import org.apache.soap.encoding.soapenc.Base64;
import org.openspcoop2.core.constants.TransferLengthModes;
import org.openspcoop2.message.Costanti;
import org.openspcoop2.message.MailcapActivationReader;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.SOAPVersion;
import org.openspcoop2.message.SoapUtils;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.autenticazione.Credenziali;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.pdd.mdb.ConsegnaContenutiApplicativi;
import org.openspcoop2.pdd.services.ServletUtils;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.config.IProtocolConfiguration;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.io.notifier.NotifierInputStreamParams;
import org.openspcoop2.utils.resources.HttpUtilities;
import org.openspcoop2.utils.resources.Loader;



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

	/** REDIRECT LOCATION HEADER */
	public final static String REDIRECT_LOCATION_HEADER = "Location";

	
	/* ********  F I E L D S  P R I V A T I  ******** */

	public ByteArrayOutputStream outByte = new ByteArrayOutputStream();

	/** Msg di richiesta */
	private OpenSPCoop2Message requestMsg;
	/** Proprieta' del connettore */
	private java.util.Hashtable<String,String> properties;
	/** Indicazione su di un eventuale sbustamento SOAP */
	private boolean sbustamentoSoap;
	/** Proprieta' del trasporto che deve gestire il connettore */
	private java.util.Properties propertiesTrasporto;
	/** Proprieta' urlBased che deve gestire il connettore */
	private java.util.Properties propertiesUrlBased;
	/** Tipo di Autenticazione */
	//private String tipoAutenticazione;
	/** Credenziali per l'autenticazione */
	private Credenziali credenziali;
	/** Busta */
	private Busta busta;
	/** Indicazione se siamo in modalita' debug */
	private boolean debug = false;

	/** OpenSPCoopProperties */
	private OpenSPCoop2Properties openspcoopProperties = null;

	/** Identificativo */
	private String idMessaggio;
	
	/** Logger */
	private Logger log = null;

	/** Loader loader */
	private Loader loader = null;
	
	/** Identificativo Modulo */
	private String idModulo = null;
	
	/** SSL Configuration */
	private boolean https = false;
	private ConnettoreHTTPSProperties sslContextProperties;
	
	/** Proxy Configuration */
	private Proxy.Type proxyType = null;
	private String proxyUrl = null;
	private int proxyPort;
	private String proxyUsername;
	private String proxyPassword;
	
	/** Redirect */
	private boolean followRedirects = false;
	private String routeRedirect = null;
	private int numberRedirect = 0;
	private int maxNumberRedirects = 5;
	
	/** Connessione */
	private InputStream is = null;
	private HttpURLConnection httpConn = null;

	/* Costruttori */
	public ConnettoreHTTP(){
		this.https = false;
	}
	public ConnettoreHTTP(boolean https){
		this.https = https;
	}
	
	
	
	
	/* ********  METODI  ******** */

	/**
	 * Si occupa di effettuare la consegna.
	 *
	 * @param request Messaggio da Consegnare
	 * @return true in caso di consegna con successo, false altrimenti
	 * 
	 */
	@Override
	public boolean send(ConnettoreMsg request){

		/** Log */
		this.log = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();
		this.openspcoopProperties = OpenSPCoop2Properties.getInstance();
		this.loader = Loader.getInstance();

		if(request==null){
			this.errore = "Messaggio da consegnare is Null (ConnettoreMsg)";
			return false;
		}

		// Raccolta parametri
		try{
			this.requestMsg =  request.getRequestMessage();
		}catch(Exception e){
			this.eccezioneProcessamento = e;
			this.log.error("Errore durante la lettura del messaggio da consegnare: "+e.getMessage(),e);
			this.errore = "Errore durante la lettura del messaggio da consegnare: "+e.getMessage();
			return false;
		}
		this.properties = request.getConnectorProperties();
		this.sbustamentoSoap = request.isSbustamentoSOAP();
		this.propertiesTrasporto = request.getPropertiesTrasporto();
		this.propertiesUrlBased = request.getPropertiesUrlBased();
		this.busta = request.getBusta();
		if(this.busta!=null)
			this.idMessaggio=this.busta.getID();

		//this.tipoAutenticazione = request.getAutenticazione();
		this.credenziali = request.getCredenziali();

		// analisi messaggio da spedire
		if(this.requestMsg==null){
			this.errore = "Messaggio da consegnare is Null (Msg)";
			return false;
		}

		// analsi i parametri specifici per il connettore
		if(this.properties == null)
			this.errore = "Proprieta' del connettore non definite";
		if(this.properties.size() == 0)
			this.errore = "Proprieta' del connettore non definite";
		if(this.properties.get("location")==null){
			this.errore = "Proprieta' 'location' non fornita e richiesta da questo tipo di connettore ["+request.getTipoConnettore()+"]";
			return false;
		}

		// Debug mode
		if(this.properties.get("debug")!=null){
			if("true".equalsIgnoreCase(this.properties.get("debug").trim()))
				this.debug = true;
		}
		
		// HTTPS
		if(this.https){
			try{
				this.sslContextProperties = ConnettoreHTTPSProperties.readProperties(this.properties);
			}catch(Exception e){
				this.eccezioneProcessamento = e;
				this.log.error("[HTTPS error]"+ e.getMessage());
				this.errore = "[HTTPS error]"+ e.getMessage();
				return false;
			}
		}
	
		// Proxy
		if(this.properties.get("proxyType")!=null){
			
			String tipo = this.properties.get("proxyType").trim();
			if("http".equals(tipo)){
				this.proxyType = Proxy.Type.HTTP;
			}
			else if("https".equals(tipo)){
				this.proxyType = Proxy.Type.HTTP;
			}
			else{
				this.errore = "Proprieta' 'proxyType' non corretta. Impostato un tipo sconosciuto ["+tipo+"] (valori ammessi: http,https)";
				return false;
			}
			
			this.proxyUrl = this.properties.get("proxyUrl");
			if(this.proxyUrl!=null){
				this.proxyUrl = this.proxyUrl.trim();
			}else{
				this.errore = "Proprieta' 'proxyUrl' non impostata, obbligatoria in presenza della proprietà 'proxyType'";
				return false;
			}
			
			String proxyPortTmp = this.properties.get("proxyPort");
			if(proxyPortTmp!=null){
				proxyPortTmp = proxyPortTmp.trim();
			}else{
				this.errore = "Proprieta' 'proxyPort' non impostata, obbligatoria in presenza della proprietà 'proxyType'";
				return false;
			}
			try{
				this.proxyPort = Integer.parseInt(proxyPortTmp);
			}catch(Exception e){
				this.errore = "Proprieta' 'proxyPort' non corretta: "+e.getMessage();
				return false;
			}
			
			
			this.proxyUsername = this.properties.get("proxyUsername");
			if(this.proxyUsername!=null){
				this.proxyUsername = this.proxyUsername.trim();
			}
			
			this.proxyPassword = this.properties.get("proxyPassword");
			if(this.proxyPassword!=null){
				this.proxyPassword = this.proxyPassword.trim();
			}else{
				if(this.proxyUsername!=null){
					this.errore = "Proprieta' 'proxyPassword' non impostata, obbligatoria in presenza della proprietà 'proxyUsername'";
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
		
		String redirectTmp = this.properties.get("followRedirects");
		if(redirectTmp!=null){
			redirectTmp = redirectTmp.trim();
			this.followRedirects = Boolean.parseBoolean(redirectTmp);
		}
		//this.log.info("FOLLOW! ("+this.followRedirects+")");
		if(this.followRedirects){
			
			redirectTmp = this.properties.get("numberRedirect");
			//this.log.info("PROPERTY! ("+redirectTmp+")");
			if(redirectTmp!=null){
				redirectTmp = redirectTmp.trim();
				this.numberRedirect = Integer.parseInt(redirectTmp);
			}
			
			redirectTmp = this.properties.get("routeRedirect");
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
	private boolean sendHTTP(ConnettoreMsg request){
		
		FileInputStream finKeyStore = null;
		FileInputStream finTrustStore = null;
		try{

			// Gestione https
			SSLContext sslContext = null;
			if(this.https){
				
				if(this.debug)
					this.log.debug("Creo contesto ssl...");
				KeyManager[] km = null;
				TrustManager[] tm = null;
				
				// Autenticazione CLIENT
				if(this.sslContextProperties.getKeyStoreLocation()!=null){
					if(this.debug)
						this.log.debug("Gestione keystore...");
					KeyStore keystore = KeyStore.getInstance(this.sslContextProperties.getKeyStoreType()); // JKS,PKCS12,jceks,bks,uber,gkr
					finKeyStore = new FileInputStream(this.sslContextProperties.getKeyStoreLocation());
					keystore.load(finKeyStore, this.sslContextProperties.getKeyStorePassword().toCharArray());
					KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(this.sslContextProperties.getKeyManagementAlgorithm());
					keyManagerFactory.init(keystore, this.sslContextProperties.getKeyPassword().toCharArray());
					km = keyManagerFactory.getKeyManagers();
					if(this.debug)
						this.log.debug("Gestione keystore effettuata");
				}
				
				
				// Autenticazione SERVER
				if(this.sslContextProperties.getTrustStoreLocation()!=null){
					if(this.debug)
						this.log.debug("Gestione truststore...");
					KeyStore truststore = KeyStore.getInstance(this.sslContextProperties.getTrustStoreType()); // JKS,PKCS12,jceks,bks,uber,gkr
					finTrustStore = new FileInputStream(this.sslContextProperties.getTrustStoreLocation());
					truststore.load(finTrustStore, this.sslContextProperties.getTrustStorePassword().toCharArray());
					TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(this.sslContextProperties.getTrustManagementAlgorithm());
					trustManagerFactory.init(truststore);
					tm = trustManagerFactory.getTrustManagers();
					if(this.debug)
						this.log.debug("Gestione truststore effettuata");
				}
				
				// Creo contesto SSL
				sslContext = SSLContext.getInstance(this.sslContextProperties.getSslType());
				sslContext.init(km, tm, null);				
			}
			

			// Creazione URL
			if(this.debug)
				this.log.debug("["+this.idMessaggio+"] creazione URL...");
			this.location = this.properties.get("location");
			
			// Impostazione Proprieta urlBased
			if(this.propertiesUrlBased != null && this.propertiesUrlBased.size()>0){
				this.location = ConnettoreUtils.buildLocationWithURLBasedParameter(this.propertiesUrlBased, this.location);
			}

			if(this.debug)
				this.log.debug("["+this.idMessaggio+"] creazione URL ["+this.location+"]...");
			URL url = new URL( this.location );	

			// Creazione Connessione
			URLConnection connection = null;
			if(this.proxyType==null){
				if(this.debug){
					this.log.debug("["+this.idMessaggio+"] creazione connessione alla URL ["+this.location+"]...");
				}
				connection = url.openConnection();
			}
			else{
				if(this.debug){
					this.log.debug("["+this.idMessaggio+"] creazione connessione alla URL ["+this.location+"] (via proxy "+
								this.proxyUrl+":"+this.proxyPassword+") (username["+this.proxyUsername+"] password["+this.proxyPassword+"])...");
				}
					
				if(this.proxyUsername!=null){
					Authenticator.setDefault(new HttpAuthenticator(this.proxyUsername, this.proxyPassword));
				}
				
				Proxy proxy = new Proxy(this.proxyType, new InetSocketAddress(this.proxyUrl, this.proxyPort));
				connection = url.openConnection(proxy);
			}
			this.httpConn = (HttpURLConnection) connection;	
			
			// Imposta Contesto SSL se attivo
			if(this.https){
				HttpsURLConnection httpsConn = (HttpsURLConnection) this.httpConn;
				httpsConn.setSSLSocketFactory(sslContext.getSocketFactory());
				
				if(this.sslContextProperties.isHostnameVerifier()){
					if(this.sslContextProperties.getClassNameHostnameVerifier()!=null){
						this.log.debug("["+this.idMessaggio+"] HostNamve verifier enabled ["+this.sslContextProperties.getClassNameHostnameVerifier()+"]");
						HostnameVerifier verifica = (HostnameVerifier) this.loader.newInstance(this.sslContextProperties.getClassNameHostnameVerifier());
						httpsConn.setHostnameVerifier(verifica);
					}else{
						this.log.debug("["+this.idMessaggio+"] HostNamve verifier enabled");
					}
				}else{
					this.log.debug("["+this.idMessaggio+"] HostNamve verifier disabled");
					ConnettoreHTTPSHostNameVerifierDisabled disabilitato = new ConnettoreHTTPSHostNameVerifierDisabled(this.log);
					httpsConn.setHostnameVerifier(disabilitato);
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
	        
			if(this.debug)
				this.log.debug("["+this.idMessaggio+"] impostazione content type...");
			String contentTypeRichiesta = null;
			if(this.sbustamentoSoap && this.requestMsg.countAttachments()>0 && SoapUtils.isTunnelOpenSPCoopSoap(this.requestMsg.getSOAPBody())){
				contentTypeRichiesta = SoapUtils.getContentTypeTunnelOpenSPCoopSoap(this.requestMsg.getSOAPBody());
			}else{
				contentTypeRichiesta = this.requestMsg.getContentType();
			}
			if(contentTypeRichiesta==null){
                                throw new Exception("Content-Type del messaggio da spedire non definito");
			}
			if(this.debug)
				this.log.debug("["+this.idMessaggio+"] impostazione http content type ["+contentTypeRichiesta+"]...");
			this.httpConn.setRequestProperty("Content-Type",contentTypeRichiesta);	

			// Impostazione transfer-length
			if(this.debug)
				this.log.debug("["+this.idMessaggio+"] impostazione transfer-length...");
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
				this.httpConn.setChunkedStreamingMode(chunkLength);
			}
			if(this.debug)
				this.log.debug("["+this.idMessaggio+"] impostazione transfer-length effettuata (chunkLength:"+chunkLength+"): "+tlm);
			
			// Impostazione timeout
			if(this.debug)
				this.log.debug("["+this.idMessaggio+"] impostazione timeout...");
			int connectionTimeout = -1;
			int readConnectionTimeout = -1;
			if(this.properties.get("connection-timeout")!=null){
				try{
					connectionTimeout = Integer.parseInt(this.properties.get("connection-timeout"));
				}catch(Exception e){
					this.log.error("Parametro connection-timeout errato",e);
				}
			}
			if(connectionTimeout==-1){
				connectionTimeout = HttpUtilities.HTTP_CONNECTION_TIMEOUT;
			}
			if(this.properties.get("read-connection-timeout")!=null){
				try{
					readConnectionTimeout = Integer.parseInt(this.properties.get("read-connection-timeout"));
				}catch(Exception e){
					this.log.error("Parametro read-connection-timeout errato",e);
				}
			}
			if(readConnectionTimeout==-1){
				readConnectionTimeout = HttpUtilities.HTTP_READ_CONNECTION_TIMEOUT;
			}
			if(this.debug)
				this.log.debug("["+this.idMessaggio+"] impostazione http timeout CT["+connectionTimeout+"] RT["+readConnectionTimeout+"]...");
			this.httpConn.setConnectTimeout(connectionTimeout);
			this.httpConn.setReadTimeout(readConnectionTimeout);


			// Aggiunga del SoapAction Header in caso di richiesta SOAP
			if(this.debug)
				this.log.debug("["+this.idMessaggio+"] impostazione soap action...");
			String soapAction = null;
			if(this.sbustamentoSoap == false){
				soapAction = (String) this.requestMsg.getProperty("SOAPAction");
				if(soapAction==null && SOAPVersion.SOAP11.equals(this.requestMsg.getVersioneSoap())){
					soapAction="\"OpenSPCoop\"";
				}
				if(SOAPVersion.SOAP11.equals(this.requestMsg.getVersioneSoap())){
					this.httpConn.setRequestProperty(SOAPVersion.SOAP11_MANDATORY_HEADER_HTTP_SOAP_ACTION,soapAction); // NOTA non quotare la soap action, per mantenere la trasparenza della PdD
				}
				if(this.debug){
					this.log.debug("["+this.idMessaggio+"] SOAP Action inviata ["+soapAction+"]");
				}
			}

			// Authentication BASIC
			if(this.debug)
				this.log.debug("["+this.idMessaggio+"] impostazione autenticazione...");
			String user = null;
			String password = null;
			if(this.credenziali!=null){
				user = this.credenziali.getUsername();
				password = this.credenziali.getPassword();
			}else{
				user = this.properties.get("user");
				password = this.properties.get("password");
			}
			if(user!=null && password!=null){
				String authentication = user + ":" + password;
				authentication = "Basic " + 
				Base64.encode(authentication.getBytes());
				this.httpConn.setRequestProperty("Authorization",authentication);
				if(this.debug)
					this.log.debug("["+this.idMessaggio+"] impostazione autenticazione (username:"+user+" password:"+password+") ["+authentication+"]...");
			}

			// Impostazione Proprieta del trasporto
			if(this.debug)
				this.log.debug("["+this.idMessaggio+"] impostazione header di trasporto...");
			if(this.propertiesTrasporto != null){
				Enumeration<?> enumProperties = this.propertiesTrasporto.keys();
				while( enumProperties.hasMoreElements() ) {
					String key = (String) enumProperties.nextElement();
					String value = (String) this.propertiesTrasporto.get(key);
					if(this.debug)
						this.log.debug("["+this.idMessaggio+"] set proprieta' ["+key+"]=["+value+"]...");
					this.httpConn.setRequestProperty(key,value);
				}
			}
			
			if(this.debug)
				this.log.debug("["+this.idMessaggio+"] impostazione POST...");
			this.httpConn.setRequestMethod( "POST" );
			this.httpConn.setDoOutput(true);
			this.httpConn.setDoInput(true);

			// Spedizione byte
			boolean consumeRequestMessage = true;
			if(this.followRedirects){
				consumeRequestMessage = false;
			}
			if(this.debug)
				this.log.debug("["+this.idMessaggio+"] spedizione byte (consume-request-message:"+consumeRequestMessage+")...");
			OutputStream out = this.httpConn.getOutputStream();
			if(this.debug){
				ByteArrayOutputStream bout = new ByteArrayOutputStream();
				if(this.sbustamentoSoap){
					this.log.debug("["+this.idMessaggio+"] Sbustamento...");
					SoapUtils.sbustamentoMessaggio(this.requestMsg,bout);
				}else{
					this.requestMsg.writeTo(bout, consumeRequestMessage);
				}
				bout.flush();
				bout.close();
				out.write(bout.toByteArray());
				this.log.debug("["+this.idMessaggio+"] Messaggio inviato (ContentType:"+contentTypeRichiesta+") :\n"+bout.toString());
				bout.close();
			}else{
				if(this.sbustamentoSoap){
					if(this.debug)
						this.log.debug("["+this.idMessaggio+"] Sbustamento...");
					SoapUtils.sbustamentoMessaggio(this.requestMsg,out);
				}else{
					this.requestMsg.writeTo(out, consumeRequestMessage);
				}
			}
			out.close();

			// Analisi MimeType e ContentLocation della risposta
			if(this.debug)
				this.log.debug("["+this.idMessaggio+"] Analisi risposta...");
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
						keyHttpResponse="ReturnCode";
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
			if(ConsegnaContenutiApplicativi.ID_MODULO.equals(this.idModulo)){
				acceptOnlyReturnCode_202_200 = this.openspcoopProperties.isAcceptOnlyReturnCode_200_202_consegnaContenutiApplicativi();
			}
			else{
				// InoltroBuste e InoltroRisposte
				acceptOnlyReturnCode_202_200 = this.openspcoopProperties.isAcceptOnlyReturnCode_200_202_inoltroBuste();
			}
			if(this.debug)
				this.log.debug("["+this.idMessaggio+"] Analisi risposta input stream e risultato http...");
			
			// return code
			this.codice = this.httpConn.getResponseCode();
			String resultHTTPMessage = this.httpConn.getResponseMessage();
			
			if(this.codice>=400){
				this.is = this.httpConn.getErrorStream();
			}
			else{
				if(this.codice>299){
					
					String redirectLocation = this.httpConn.getHeaderField(REDIRECT_LOCATION_HEADER);
					if(redirectLocation==null){
						redirectLocation = this.httpConn.getHeaderField(REDIRECT_LOCATION_HEADER.toLowerCase());
					}
					if(redirectLocation==null){
						redirectLocation = this.httpConn.getHeaderField(REDIRECT_LOCATION_HEADER.toUpperCase());
					}
					if(redirectLocation==null){
						throw new Exception("Non è stato rilevato l'header HTTP ["+REDIRECT_LOCATION_HEADER+"] necessario alla gestione del Redirect (code:"+this.codice+")"); 
					}
					
					// 3XX
					if(this.followRedirects){
												
						request.getConnectorProperties().remove("location");
						request.getConnectorProperties().remove("numberRedirect");
						request.getConnectorProperties().remove("routeRedirect");
						request.getConnectorProperties().put("location", redirectLocation);
						request.getConnectorProperties().put("numberRedirect", (this.numberRedirect+1)+"" );
						if(this.routeRedirect!=null){
							request.getConnectorProperties().put("routeRedirect", this.routeRedirect+" -> "+redirectLocation );
						}else{
							request.getConnectorProperties().put("routeRedirect", redirectLocation );
						}
						
						this.log.warn("["+this.idMessaggio+"] (hope:"+(this.numberRedirect+1)+") Redirect verso ["+redirectLocation+"] ...");
						
						if(this.numberRedirect==this.maxNumberRedirects){
							throw new Exception("Gestione redirect (code:"+this.codice+" "+REDIRECT_LOCATION_HEADER+":"+redirectLocation+") non consentita ulteriormente, sono già stati gestiti "+this.maxNumberRedirects+" redirects: "+this.routeRedirect);
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
								throw new Exception("Return code ["+this.codice+"] (redirect "+REDIRECT_LOCATION_HEADER+":"+redirectLocation+") non consentito dal WS-I Basic Profile (http://www.ws-i.org/Profiles/BasicProfile-1.1-2004-08-24.html#HTTP_Redirect_Status_Codes)");
							}
						}
						
						return this.send(request);
						
					}else{
						throw new Exception("Gestione redirect (code:"+this.codice+" "+REDIRECT_LOCATION_HEADER+":"+redirectLocation+") non attiva");
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
				this.log.info("["+this.idMessaggio+"] Stream di risposta (return-code:"+this.codice+") is null");
			}
			
			String tipoLetturaRisposta = null;
			
			/*
			 * Se il messaggio e' un html di errore me ne esco 
			 */			
			if(this.codice>=400 && tipoRisposta!=null && tipoRisposta.contains("text/html")){
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
						soapVersionRisposta = ServletUtils.getVersioneSoap(this.log,tipoRisposta);
					}catch(Exception e){
						this.log.error("SOAPVersion response unknown: "+e.getMessage(),e);
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
						throw new Exception(msgErrore);
					}else{
						this.log.warn(msgErrore+"; viene utilizzato forzatamente il tipo: "+SOAPVersion.SOAP11.getContentTypeForMessageWithoutAttachments());
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
						this.log.debug("["+this.idMessaggio+"] Messaggio ricevuto (ContentType:"+tipoRisposta+") :\n"+bout.toString());
						// Creo nuovo inputStream
						this.is = new ByteArrayInputStream(bout.toByteArray());
					}
					
					if(this.sbustamentoSoap==false){
						if(this.debug)
							this.log.debug("["+this.idMessaggio+"] Ricostruzione normale...");
						
						// Ricostruzione messaggio soap: secondo parametro a false, indica che il messaggio e' gia un SOAPMessage
						tipoLetturaRisposta = "Costruzione messaggio SOAP";
						
						
						
						if(contentLenght>0){
							this.responseMsg = OpenSPCoop2MessageFactory.getMessageFactory().createMessage(this.is,notifierInputStreamParams,false,tipoRisposta,locationRisposta, this.openspcoopProperties.isFileCacheEnable(), this.openspcoopProperties.getAttachmentRepoDir(), this.openspcoopProperties.getFileThreshold());	
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
									this.responseMsg = OpenSPCoop2MessageFactory.getMessageFactory().createMessage(sInput,notifierInputStreamParams,false,tipoRisposta,locationRisposta, this.openspcoopProperties.isFileCacheEnable(), this.openspcoopProperties.getAttachmentRepoDir(), this.openspcoopProperties.getFileThreshold());
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
									this.errore = "Errore avvenuto durante la consegna HTTP (lettura risposta): " + e.getMessage();
									this.log.error("Errore avvenuto durante la consegna HTTP (lettura risposta): " + e.getMessage());
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
						if(imbustamentoConAttachment){
							if(this.debug)
								this.log.debug("["+this.idMessaggio+"] Imbustamento con attachments...");
							
							// Imbustamento per Tunnel OpenSPCoop
							tipoLetturaRisposta = "Costruzione messaggio SOAP per Tunnel con mimeType "+mimeTypeAttachment;
							try{
								this.responseMsg = SoapUtils.
								imbustamentoMessaggioConAttachment(this.requestMsg.getVersioneSoap(), this.is,mimeTypeAttachment,
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
									this.log.debug("["+this.idMessaggio+"] Imbustamento messaggio multipart/related...");
								
								// Imbustamento di un messaggio multipart/related
								tipoLetturaRisposta = "Imbustamento messaggio multipart/related in un SOAP WithAttachments";
								java.io.ByteArrayOutputStream byteBuffer = new java.io.ByteArrayOutputStream();
								byte [] readB = new byte[Utilities.DIMENSIONE_BUFFER];
								int readByte = 0;
								while((readByte = this.is.read(readB))!= -1){
									byteBuffer.write(readB,0,readByte);
								}
								if(byteBuffer.size()>0){
									this.responseMsg = SoapUtils.imbustamentoMessaggio(notifierInputStreamParams,byteBuffer.toByteArray(),this.openspcoopProperties.isDeleteInstructionTargetMachineXml(), this.openspcoopProperties.isFileCacheEnable(), this.openspcoopProperties.getAttachmentRepoDir(), this.openspcoopProperties.getFileThreshold());
								}
							}else{
								
								if(this.debug)
									this.log.debug("["+this.idMessaggio+"] Imbustamento messaggio...");
								// Imbustamento di un messaggio normale: secondo parametro a true, indica che il messaggio deve essere imbustato in un msg SOAP
								tipoLetturaRisposta = "Imbustamento messaggio xml in un messaggio SOAP";
								this.responseMsg = OpenSPCoop2MessageFactory.getMessageFactory().createMessage(this.is,notifierInputStreamParams,true,tipoRisposta,locationRisposta, this.openspcoopProperties.isFileCacheEnable(), this.openspcoopProperties.getAttachmentRepoDir(), this.openspcoopProperties.getFileThreshold());	
							}

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
							this.errore = "Errore avvenuto durante la consegna HTTP (lettura risposta): " + e.getMessage();
							this.log.error("Errore avvenuto durante la consegna HTTP (lettura risposta): " + e.getMessage());
							if(result2XX){
								return false;
							}
						}
					}
				}catch(Exception e){
					this.eccezioneProcessamento = e;
					this.errore = "Errore avvenuto durante la consegna HTTP ("+tipoLetturaRisposta+"): " + e.getMessage();
					this.log.error("Errore avvenuto durante la consegna HTTP ("+tipoLetturaRisposta+")",e);
					return false;
				}

				

				// save Msg
				if(this.debug)
					this.log.debug("["+this.idMessaggio+"] save messaggio...");
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
					this.errore = "Errore avvenuto durante la consegna HTTP (salvataggio risposta): " + e.getMessage();
					this.log.error("Errore avvenuto durante la consegna HTTP (salvataggio risposta): " + e.getMessage());
					return false;
				}

			}

			if(this.debug)
				this.log.debug("["+this.idMessaggio+"] gestione invio/risposta http effettuata con successo");
			
			return true;

		}  catch(Exception e){ 
			this.eccezioneProcessamento = e;
			this.errore = "Errore avvenuto durante la consegna HTTP: "+e.getMessage();
			this.log.error("["+this.idMessaggio+"] Errore avvenuto durante la consegna HTTP: "+e.getMessage(),e);
			return false;
		} finally{
			try{
				if(finKeyStore!=null){
					finKeyStore.close();
				}
			}catch(Exception e){}
			try{
				if(finTrustStore!=null){
					finTrustStore.close();
				}
			}catch(Exception e){}
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
	    		if(this.debug && this.log!=null)
	    			this.log.debug("["+this.idMessaggio+"] chiusura socket...");
				this.is.close();
			}
	
			// fine HTTP.
	    	if(this.httpConn!=null){
	    		if(this.debug && this.log!=null)
					this.log.debug("["+this.idMessaggio+"] chiusura connessione...");
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




