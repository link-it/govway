/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2015 Link.it srl (http://link.it). 
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
import java.io.SequenceInputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.input.CountingInputStream;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
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

/**
 * Connettore che utilizza la libreria httpcore
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConnettoreHTTPCORE extends ConnettoreBase {

	private static boolean USE_POOL = true;
	
	/** Logger */
	private Logger log = null;
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
		
	/** Identificativo Modulo */
	private String idModulo = null;
		
	/** Identificativo */
	private String idMessaggio;
	
	private InputStream isResponse = null;
	private HttpEntity httpEntityResponse = null;
	private HttpClient httpClient = null;
	
	/** OpenSPCoopProperties */
	private OpenSPCoop2Properties openspcoopProperties = null;
	
	
	private static PoolingClientConnectionManager cm = null;
	private static synchronized void initialize(){
		if(ConnettoreHTTPCORE.cm==null){
			
			// LEGGERE ARTICOLO http://restclient-tool.eclipselabs.org.codespot.com/svn-history/r14/trunk/standalone/src/code/google/restclient/core/Hitter.java
			// PER THREAD SAFE
			
			//System.out.println("INIT POOL");
			
			/*2.8.4. Pooling connection manager
	
			PoolingClientConnectionManager is a more complex implementation that manages a pool of client connections and 
			is able to service connection requests from multiple execution threads. Connections are pooled on a per route basis. 
			A request for a route for which the manager already has a persistent connection available in the pool will be 
			serviced by leasing a connection from the pool rather than creating a brand new connection.
	
			PoolingClientConnectionManager maintains a maximum limit of connections on a per route basis and in total. 
			Per default this implementation will create no more than 2 concurrent connections per given route and 
			no more 20 connections in total. 
			For many real-world applications these limits may prove too constraining, 
			especially if they use HTTP as a transport protocol for their services. 
			Connection limits can be adjusted using the appropriate HTTP parameters.
			 */
			
			SchemeRegistry schemeRegistry = new SchemeRegistry();
			schemeRegistry.register(
			         new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));
			
			ConnettoreHTTPCORE.cm = new PoolingClientConnectionManager(schemeRegistry);
			// Increase max total connection to 200
			ConnettoreHTTPCORE.cm.setMaxTotal(10000);
			// Increase default max connection per route to 20
			ConnettoreHTTPCORE.cm.setDefaultMaxPerRoute(1000);
			// Increase max connections for localhost:80 to 50
			//HttpHost localhost = new HttpHost("locahost", 80);
			//cm.setMaxPerRoute(new HttpRoute(localhost), 50);
			 
			
		}
	}
	private static HttpClient getHttpClient(){
		// Caso senza pool
		return new DefaultHttpClient();
	}
	private static HttpClient getHttpClientFromPool(){
		// Caso con pool
		if(ConnettoreHTTPCORE.cm==null){
			ConnettoreHTTPCORE.initialize();
		}
		//System.out.println("-----GET CONNECTION [START] ----");
		//System.out.println("PRIMA CLOSE AVAILABLE["+cm.getTotalStats().getAvailable()+"] LEASED["
		//		+cm.getTotalStats().getLeased()+"] MAX["+cm.getTotalStats().getMax()+"] PENDING["+cm.getTotalStats().getPending()+"]");
		ConnettoreHTTPCORE.cm.closeExpiredConnections();
		ConnettoreHTTPCORE.cm.closeIdleConnections(30, TimeUnit.SECONDS);
		//System.out.println("DOPO CLOSE AVAILABLE["+cm.getTotalStats().getAvailable()+"] LEASED["
		//		+cm.getTotalStats().getLeased()+"] MAX["+cm.getTotalStats().getMax()+"] PENDING["+cm.getTotalStats().getPending()+"]");
		DefaultHttpClient http = new DefaultHttpClient(ConnettoreHTTPCORE.cm);
		//System.out.println("PRESA LA CONNESSIONE AVAILABLE["+cm.getTotalStats().getAvailable()+"] LEASED["
		//		+cm.getTotalStats().getLeased()+"] MAX["+cm.getTotalStats().getMax()+"] PENDING["+cm.getTotalStats().getPending()+"]");
		//System.out.println("-----GET CONNECTION [END] ----");
		return http;
	}
	
	
	@Override
	public boolean send(ConnettoreMsg request) {
		
		/** Log */
		this.log = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();
		this.openspcoopProperties = OpenSPCoop2Properties.getInstance();
		
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
		
		// Identificativo modulo
		this.idModulo = request.getIdModulo();
		
		// Context per invocazioni handler
		this.outRequestContext = request.getOutRequestContext();
		this.msgDiagnostico = request.getMsgDiagnostico();
		
		try{
			
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
			if(this.debug)
				this.log.debug("["+this.idMessaggio+"] creazione connessione alla URL ["+this.location+"]...");
			if(ConnettoreHTTPCORE.USE_POOL){
				this.httpClient = ConnettoreHTTPCORE.getHttpClientFromPool();
			}
			else{
				this.httpClient = ConnettoreHTTPCORE.getHttpClient();
			}
			HttpPost httppost = new HttpPost(url.toString());
			
			
			
			// Keep-alive
			((DefaultHttpClient) this.httpClient).setKeepAliveStrategy(new ConnectionKeepAliveStrategy() {

			    @Override
				public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
			        // Honor 'keep-alive' header
			        HeaderElementIterator it = new BasicHeaderElementIterator(
			                response.headerIterator(HTTP.CONN_KEEP_ALIVE));
			        while (it.hasNext()) {
			            HeaderElement he = it.nextElement();
			            String param = he.getName(); 
			            String value = he.getValue();
			            if (value != null && param.equalsIgnoreCase("timeout")) {
			                try {
			                	//System.out.println("RETURN HEADER ["+ (Long.parseLong(value) * 1000)+"]");
			                    return Long.parseLong(value) * 1000;
			                } catch(NumberFormatException ignore) {
			                }
			            }
			        }
//			        HttpHost target = (HttpHost) context.getAttribute(
//			                ExecutionContext.HTTP_TARGET_HOST);
//			        if ("www.naughty-server.com".equalsIgnoreCase(target.getHostName())) {
//			            // Keep alive for 5 seconds only
//			            return 5 * 1000;
//			        } else {
//			            // otherwise keep alive for 30 seconds
//			            return 30 * 1000;
//			        }
			        // otherwise keep alive for 2 minutes
			        //System.out.println("RETURN 2 minuti");
		            return 2 * 60 * 1000;
			    }
			    
			});

			
			
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
			httppost.setHeader(Costanti.CONTENT_TYPE, contentTypeRichiesta);
			
			
			
			
			
			// Preparazione messaggio da spedire
			// Spedizione byte
			if(this.debug)
				this.log.debug("["+this.idMessaggio+"] spedizione byte...");
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			if(this.sbustamentoSoap){
				if(this.debug)
					this.log.debug("["+this.idMessaggio+"] Sbustamento...");
				SoapUtils.sbustamentoMessaggio(this.requestMsg,out);
			}else{
				this.requestMsg.writeTo(out, true);
			}
			out.flush();
			out.close();
			if(this.debug){
				this.log.debug("["+this.idMessaggio+"] Messaggio inviato (ContentType:"+contentTypeRichiesta+") :\n"+out.toString());
			}
			HttpEntity httpEntity = new ByteArrayEntity(out.toByteArray());
			httppost.setEntity(httpEntity);
			
			
			
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
				//this.httpConn.setChunkedStreamingMode(chunkLength);
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
			this.httpClient.getParams().setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, connectionTimeout);
			this.httpClient.getParams().setIntParameter(CoreConnectionPNames.SO_TIMEOUT, readConnectionTimeout);
			//this.httpClient.getParams().setIntParameter(CoreConnectionPNames.SO_LINGER, 0);
			//this.httpClient.getParams().setIntParameter(CoreConnectionPNames.SO_KEEPALIVE, true);
			//this.httpConn.setReadTimeout(readConnectionTimeout);
			//this.httpClient.getParams().setIntParameter(CoreConnectionPNames.SO_TIMEOUT, 3000);
            //this.httpClient.getParams().setIntParameter(CoreConnectionPNames.SOCKET_BUFFER_SIZE, 8 * 1024);
            //this.httpClient.getParams().setBooleanParameter(CoreConnectionPNames.TCP_NODELAY, true);
		
			// 2.1. Connection parameters
			//These are parameters that can influence connection operations:
		
			//  * CoreConnectionPNames.SO_KEEPALIVE
			// When the keepalive option is set for a TCP socket and no data has been exchanged across the socket in 
			// Beither direction for 2 hours (NOTE: the actual value is implementation dependent), 
			// TCP automatically sends a keepalive probe to the peer. 
			// This probe is a TCP segment to which the peer must respond. 
			// One of three responses is expected: 
			// 1. The peer responds with the expected ACK. 
			// 		The application is not notified (since everything is OK). 
			// 		TCP will send another probe following another 2 hours of inactivity. 
			// 2. The peer responds with an RST, which tells the local TCP that the peer host has crashed and rebooted. 
			//		The socket is closed. 
			// 3. There is no response from the peer. The socket is closed. 
			// 		The purpose of this option is to detect if the peer host crashes. Valid only for TCP socket: SocketImpl 
			
			//  * CoreConnectionPNames.SO_TIMEOUT='http.socket.timeout':  
			//  	defines the socket timeout (SO_TIMEOUT) in milliseconds, which is the timeout for waiting for data or, 
			//		put differently, a maximum period inactivity between two consecutive data packets). 
			//		A timeout value of zero is interpreted as an infinite timeout. 
			//		This parameter expects a value of type java.lang.Integer. 
			//		If this parameter is not set, read operations will not time out (infinite timeout).
			    
			// * CoreConnectionPNames.TCP_NODELAY='http.tcp.nodelay':  
			//		determines whether Nagle's algorithm is to be used. 
			//		Nagle's algorithm tries to conserve bandwidth by minimizing the number of segments that are sent. 
			//		When applications wish to decrease network latency and increase performance, 
			//		they can disable Nagle's algorithm (that is enable TCP_NODELAY. 
			//		Data will be sent earlier, at the cost of an increase in bandwidth consumption. 
			//		This parameter expects a value of type java.lang.Boolean. 
			//		If this parameter is not set, TCP_NODELAY will be enabled (no delay).
			 
			// * CoreConnectionPNames.SOCKET_BUFFER_SIZE='http.socket.buffer-size':  
			//		determines the size of the internal socket buffer used to buffer data while receiving / transmitting HTTP messages. 
			//		This parameter expects a value of type java.lang.Integer. 
			//		If this parameter is not set, HttpClient will allocate 8192 byte socket buffers.
			
			// * CoreConnectionPNames.SO_LINGER='http.socket.linger':  
			//		sets SO_LINGER with the specified linger time in seconds. 
			//		The maximum timeout value is platform specific. Value 0 implies that the option is disabled. 
			//		Value -1 implies that the JRE default is used. The setting only affects the socket close operation. 
			//		If this parameter is not set, the value -1 (JRE default) will be assumed.
			
			// * CoreConnectionPNames.CONNECTION_TIMEOUT='http.connection.timeout':  
			//		determines the timeout in milliseconds until a connection is established. 
			//		A timeout value of zero is interpreted as an infinite timeout. 
			//		This parameter expects a value of type java.lang.Integer. 
			//		If this parameter is not set, connect operations will not time out (infinite timeout).
			  
			// * CoreConnectionPNames.STALE_CONNECTION_CHECK='http.connection.stalecheck':  
			//		determines whether stale connection check is to be used. 
			//		Disabling stale connection check may result in a noticeable performance improvement 
			//		(the check can cause up to 30 millisecond overhead per request) at the risk of getting an I/O error
			//		when executing a request over a connection that has been closed at the server side. 
			//		This parameter expects a value of type java.lang.Boolean. 
			//		For performance critical operations the check should be disabled. 
			//		If this parameter is not set, the stale connection check will be performed before each request execution.
			  
			// * CoreConnectionPNames.MAX_LINE_LENGTH='http.connection.max-line-length': 
			//		determines the maximum line length limit. If set to a positive value, 
			//		any HTTP line exceeding this limit will cause an java.io.IOException. 
			//		A negative or zero value will effectively disable the check. 
			//		This parameter expects a value of type java.lang.Integer. 
			//		If this parameter is not set, no limit will be enforced.
			   
			// * CoreConnectionPNames.MAX_HEADER_COUNT='http.connection.max-header-count':  
			//		determines the maximum HTTP header count allowed. 
			//		If set to a positive value, the number of HTTP headers received from the data stream 
			//		exceeding this limit will cause an java.io.IOException. 
			//		A negative or zero value will effectively disable the check. 
			//		This parameter expects a value of type java.lang.Integer. 
			//		If this parameter is not set, no limit will be enforced.
			    
			//	* ConnConnectionPNames.MAX_STATUS_LINE_GARBAGE='http.connection.max-status-line-garbage':  
			//		defines the maximum number of ignorable lines before we expect a HTTP response's status line. 
			//		With HTTP/1.1 persistent connections, the problem arises that broken scripts could return a wrong Content-Length 
			//		(there are more bytes sent than specified). Unfortunately, in some cases, 
			//		this cannot be detected after the bad response, but only before the next one. 
			//		So HttpClient must be able to skip those surplus lines this way. 
			//		This parameter expects a value of type java.lang.Integer. 0 disallows all garbage/empty lines before the status line. 
			//		Use java.lang.Integer#MAX_VALUE for unlimited number. 
			//		If this parameter is not set, unlimited number will be assumed.

			
			
			
			// Gestione automatica del redirect
			//this.httpConn.setInstanceFollowRedirects(true); 
			
			
			
			
			
			
			// Aggiunga del SoapAction Header in caso di richiesta SOAP
			if(this.debug)
				this.log.debug("["+this.idMessaggio+"] impostazione soap action...");
			String soapAction = null;
			if(this.sbustamentoSoap == false){
				soapAction = (String) this.requestMsg.getProperty("SOAPAction");
				if(soapAction==null)
					soapAction="\"OpenSPCoop\"";
				httppost.setHeader(Costanti.SOAP_ACTION, soapAction);
				// NOTA non quotare la soap action, per mantenere la trasparenza della PdD
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
				httppost.setHeader("Authorization",authentication);
				if(this.debug)
					this.log.debug("["+this.idMessaggio+"] impostazione autenticazione (username:"+user+" password:"+password+") ["+authentication+"]...");
			}

			
			
			// Impostazione Proprieta del trasporto
			if(this.debug)
				this.log.debug("["+this.idMessaggio+"] impostazione header di trasporto...");
			if(this.propertiesTrasporto != null){
				Enumeration<?> enumSPC = this.propertiesTrasporto.keys();
				while( enumSPC.hasMoreElements() ) {
					String key = (String) enumSPC.nextElement();
					String value = (String) this.propertiesTrasporto.get(key);
					if(this.debug)
						this.log.debug("["+this.idMessaggio+"] set proprieta' ["+key+"]=["+value+"]...");
					httppost.setHeader(key,value);
				}
			}
			
			
			
			
			// Spedizione byte
			if(this.debug)
				this.log.debug("["+this.idMessaggio+"] spedizione byte...");
			// Eseguo la richiesta e prendo la risposta
			HttpResponse postResponse = this.httpClient.execute(httppost);
			this.httpEntityResponse = postResponse.getEntity();
			
			
			
			if(this.debug)
				this.log.debug("["+this.idMessaggio+"] Analisi risposta...");
			Header [] hdrRisposta = postResponse.getAllHeaders();
			if(hdrRisposta!=null){
				for (int i = 0; i < hdrRisposta.length; i++) {
					if(hdrRisposta[i].getName()==null){
						// Check per evitare la coppia che ha come chiave null e come valore HTTP OK 200
						this.propertiesTrasportoRisposta.put("ReturnCode", hdrRisposta[i].getValue());
					}
					else{
						this.propertiesTrasportoRisposta.put(hdrRisposta[i].getName(), hdrRisposta[i].getValue());
					}
				}
			}
			Header tipoRispostaHdr = postResponse.getFirstHeader(Costanti.CONTENT_TYPE);
			if(tipoRispostaHdr==null){
				tipoRispostaHdr = postResponse.getFirstHeader(Costanti.CONTENT_TYPE.toLowerCase());
			}
			if(tipoRispostaHdr==null){
				tipoRispostaHdr = postResponse.getFirstHeader(Costanti.CONTENT_TYPE.toUpperCase());
			}
			String tipoRisposta = null;
			if(tipoRispostaHdr!=null){
				tipoRisposta = tipoRispostaHdr.getValue();
			}
			
			Header locationRispostaHdr = postResponse.getFirstHeader(Costanti.CONTENT_LOCATION);
			if(locationRispostaHdr==null){
				locationRispostaHdr = postResponse.getFirstHeader(Costanti.CONTENT_LOCATION.toLowerCase());
			}
			if(locationRispostaHdr==null){
				locationRispostaHdr = postResponse.getFirstHeader(Costanti.CONTENT_LOCATION.toUpperCase());
			}
			String locationRisposta = null;
			if(locationRispostaHdr!=null){
				locationRisposta = locationRispostaHdr.getValue();
			}
			
			Header contentLengthHdr = postResponse.getFirstHeader(Costanti.CONTENT_LENGTH);
			if(contentLengthHdr==null){
				contentLengthHdr = postResponse.getFirstHeader(Costanti.CONTENT_LENGTH.toLowerCase());
			}
			if(contentLengthHdr==null){
				contentLengthHdr = postResponse.getFirstHeader(Costanti.CONTENT_LENGTH.toUpperCase());
			}
			long contentLenght = -1;
			if(contentLengthHdr!=null){
				contentLenght = Long.parseLong(contentLengthHdr.getValue());
			}
			
			
			
			//System.out.println("TIPO RISPOSTA["+tipoRisposta+"] LOCATION["+locationRisposta+"]");
			
			// ContentLength della risposta
			if(this.httpEntityResponse.getContentLength()>0){
				this.contentLength = this.httpEntityResponse.getContentLength();
			}
			
			// Parametri di imbustamento
			boolean imbustamentoConAttachment = false;
			Header tunnelSoapHdr = postResponse.getFirstHeader(this.openspcoopProperties.getTunnelSOAPKeyWord_headerTrasporto());
			if(tunnelSoapHdr!=null && "true".equals(tunnelSoapHdr.getValue())){
				imbustamentoConAttachment = true;
			}
			Header mimeTypeAttachmentHdr = postResponse.getFirstHeader(this.openspcoopProperties.getTunnelSOAPKeyWordMimeType_headerTrasporto());
			String mimeTypeAttachment = null;
			if(mimeTypeAttachmentHdr!=null){
				mimeTypeAttachment = mimeTypeAttachmentHdr.getValue();
			}
			if(mimeTypeAttachment==null)
				mimeTypeAttachment = Costanti.CONTENT_TYPE_OPENSPCOOP2_TUNNEL_SOAP;
			//System.out.println("IMB["+imbustamentoConAttachment+"] MIME["+mimeTypeAttachment+"]");

			// Ricezione Risposta
			if(this.debug)
				this.log.debug("["+this.idMessaggio+"] Analisi risposta input stream e risultato http...");
			int resultHTTPOperation = postResponse.getStatusLine().getStatusCode();
			String resultHTTPMessage = postResponse.getStatusLine().getReasonPhrase();
			if(resultHTTPOperation<300)
				this.isResponse = this.httpEntityResponse.getContent();
			else{
				this.isResponse = this.httpEntityResponse.getContent();
				
			}
			
			
			
			
			// TODO SET HEADER TRASPORTO DELLA RISPOSTA
			
			
			
			
			/* ------------  PostOutRequestHandler ------------- */
			this.postOutRequest();
			
			
			
			
			/* ------------  PreInResponseHandler ------------- */
			this.preInResponse();
			
			// Lettura risposta parametri NotifierInputStream per la risposta
			NotifierInputStreamParams notifierInputStreamParams = null;
			if(this.preInResponseContext!=null){
				notifierInputStreamParams = this.preInResponseContext.getNotifierInputStreamParams();
			}
			
			
			
			
			//if(this.is!=null && resultHTTPOperation!=202){
			
			//Se non e' null, controllo che non sia vuoto.
			byte[] b = new byte[1];
			if(this.isResponse != null && this.isResponse.read(b) == -1) {
				this.isResponse = null;
			} else {
				this.isResponse = new SequenceInputStream(new ByteArrayInputStream(b),this.isResponse);
			}
			
			String tipoLetturaRisposta = null;
			
			/*
			 * Se il messaggio e' un html di errore me ne esco 
			 */			
			if(resultHTTPOperation>=400 && tipoRisposta!=null && tipoRisposta.contains("text/html")){
				tipoLetturaRisposta = "("+resultHTTPOperation+") " + resultHTTPMessage ;
				this.codice = resultHTTPOperation;
				this.errore = tipoLetturaRisposta;
				return false;
			}
			
			if(this.isResponse!=null){
				
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
								+") non rientra tra quelli conosciuti ("+SOAPVersion.getKnownContentTypes()+")";
					}
					else if(!soapVersionRisposta.equals(this.requestMsg.getVersioneSoap())){
						msgErrore = "Header Content-Type definito nell'http reply ("+tipoRisposta+") indica una versione "+soapVersionRisposta.getSoapVersionAsString()
								+" non compatibile con la versione "+this.requestMsg.getVersioneSoap().getSoapVersionAsString()+" del messaggio di richiesta";
					}
					else{
						msgErrore = "Il valore dell'header HTTP Content-Type definito nell'http reply ("+tipoRisposta
								+") non rientra tra quelli supportati dal protocollo ("+SOAPVersion.getKnownContentTypes(protocolConfiguration.isSupportoSOAP11(), protocolConfiguration.isSupportoSOAP12())+")";
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
						while((readByte = this.isResponse.read(readB))!= -1){
							bout.write(readB,0,readByte);
						}
						this.isResponse.close();
						bout.flush();
						bout.close();
						this.log.debug("["+this.idMessaggio+"] Messaggio ricevuto (ContentType:"+tipoRisposta+") :\n"+bout.toString());
						// Creo nuovo inputStream
						this.isResponse = new ByteArrayInputStream(bout.toByteArray());
					}
					
					if(this.sbustamentoSoap==false){
						if(this.debug)
							this.log.debug("["+this.idMessaggio+"] Ricostruzione normale...");
						
						// Ricostruzione messaggio soap: secondo parametro a false, indica che il messaggio e' gia un SOAPMessage
						tipoLetturaRisposta = "Costruzione messaggio SOAP";
						
						
						
						if(contentLenght>0){
							this.responseMsg = OpenSPCoop2MessageFactory.getMessageFactory().createMessage(this.isResponse,notifierInputStreamParams,false,tipoRisposta,locationRisposta, this.openspcoopProperties.isFileCacheEnable(), this.openspcoopProperties.getAttachmentRepoDir(), this.openspcoopProperties.getFileThreshold());	
						}
						else if(contentLenght==0){
							this.responseMsg = null;
						}
						else{
							//non ho trovato ContentLength. Devo scoprire se c'e' un payload. Costruisco il messaggio e poi provo ad
							//accedere all'envelope
							try{
								byte [] buffer = new byte[1];
								int letti = this.isResponse.read(buffer);
								if(letti==1){
									// Per evitare il propblema del 'Premature end of file' che causa una system.out sul server.log di jboss
									SequenceInputStream sInput = new SequenceInputStream(new ByteArrayInputStream(buffer), this.isResponse);
									this.responseMsg = OpenSPCoop2MessageFactory.getMessageFactory().createMessage(sInput,notifierInputStreamParams,false,tipoRisposta,locationRisposta, this.openspcoopProperties.isFileCacheEnable(), this.openspcoopProperties.getAttachmentRepoDir(), this.openspcoopProperties.getFileThreshold());
								}
							}catch(Exception e){
								this.responseMsg=null;
								// L'errore 'premature end of file' consiste solo nel fatto che una risposta non e' stata ricevuta.
								boolean result2XX = (resultHTTPOperation>=200 && resultHTTPOperation<=299);
								boolean premature =  Utilities.existsInnerMessageException(e, "Premature end of file", true) && result2XX;
								// Se non ho un premature, ed un errore di lettura in 200, allora devo segnalare l'errore, altrimenti comunque 
								// il msg ritornato e' null e nel codiceStato vi e' l'errore.
								
								if( premature == false ){
									if(result2XX){
										this.eccezioneProcessamento = e;
										this.errore = "Errore avvenuto durante la consegna HTTP (lettura risposta): " + e.getMessage();
										this.log.error("Errore avvenuto durante la consegna HTTP (lettura risposta): " + e.getMessage());
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
							cis = new CountingInputStream(this.isResponse);
						
							if(imbustamentoConAttachment){
								if(this.debug)
									this.log.debug("["+this.idMessaggio+"] Imbustamento con attachments...");
								
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
										this.log.debug("["+this.idMessaggio+"] Imbustamento messaggio multipart/related...");
									
									// Imbustamento di un messaggio multipart/related
									tipoLetturaRisposta = "Imbustamento messaggio multipart/related in un SOAP WithAttachments";
									java.io.ByteArrayOutputStream byteBuffer = new java.io.ByteArrayOutputStream();
									byte [] readB = new byte[Utilities.DIMENSIONE_BUFFER];
									int readByte = 0;
									while((readByte = cis.read(readB))!= -1){
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
									this.responseMsg = OpenSPCoop2MessageFactory.getMessageFactory().createMessage(cis,notifierInputStreamParams,true,tipoRisposta,locationRisposta, this.openspcoopProperties.isFileCacheEnable(), this.openspcoopProperties.getAttachmentRepoDir(), this.openspcoopProperties.getFileThreshold());	
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
						boolean result2XX = (resultHTTPOperation>=200 && resultHTTPOperation<=299);
						boolean premature =  Utilities.existsInnerMessageException(e, "Premature end of file", true) && result2XX;
						// Se non ho un premature, ed un errore di lettura in 200, allora devo segnalare l'errore, altrimenti comunque 
						// il msg ritornato e' null e nel codiceStato vi e' l'errore.
						
						if( premature == false ){
							if(result2XX){
								this.eccezioneProcessamento = e;
								this.errore = "Errore avvenuto durante la consegna HTTP (lettura risposta): " + e.getMessage();
								this.log.error("Errore avvenuto durante la consegna HTTP (lettura risposta): " + e.getMessage());
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

			// return code
			this.codice = resultHTTPOperation;

			if(this.debug)
				this.log.debug("["+this.idMessaggio+"] gestione invio/risposta http effettuata con successo");
			
			return true;			
			
			
		}  catch(Exception e){ 
			this.eccezioneProcessamento = e;
			this.log.error("["+this.idMessaggio+"] Errore avvenuto durante la consegna HTTP: "+e.getMessage(),e);
			this.errore = "Errore avvenuto durante la consegna HTTPNIO: "+e.getMessage();
			return false;
		} 

	}

	
	@Override
	public void disconnect() throws ConnettoreException{
    	try{
			// Gestione finale della connessione    		
    		//System.out.println("CHECK CLOSE STREAM...");
	    	if(this.isResponse!=null){
	    		if(this.debug && this.log!=null)
	    			this.log.debug("["+this.idMessaggio+"] chiusura socket...");
	    		//System.out.println("CLOSE STREAM...");
				this.isResponse.close();
				//System.out.println("CLOSE STREAM");
			}				
    	}catch(Exception e){
    		throw new ConnettoreException("Chiusura connessione non riuscita: "+e.getMessage(),e);
    	}
    	try{
			// Gestione finale della connessione
    		//System.out.println("CHECK ENTITY...");
	    	if(this.httpEntityResponse!=null){
	    		if(this.debug && this.log!=null)
	    			this.log.debug("["+this.idMessaggio+"] chiusura httpEntityResponse...");
	    		//System.out.println("CLOSE ENTITY...");
	    		EntityUtils.consume(this.httpEntityResponse);
	    		//System.out.println("CLOSE ENTITY");
			}
	    	
	    	if(this.httpEntityResponse!=null){
	    		
	    	}
				
    	}catch(Exception e){
    		throw new ConnettoreException("Chiusura connessione non riuscita: "+e.getMessage(),e);
    	}
    	try{
	    	// super.disconnect (Per risorse base)
	    	super.disconnect();
    	}catch(Exception e){
    		throw new ConnettoreException("Chiusura risorse non riuscita: "+e.getMessage(),e);
    	}
    }
}
