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
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.apache.soap.encoding.soapenc.Base64;
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
import org.openspcoop2.utils.resources.RFC2047Encoding;
import org.openspcoop2.utils.resources.RFC2047Utilities;

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
	private ConnettoreLogger logger = null;
	
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
	
	
	private static PoolingHttpClientConnectionManager cm = null;
	private static synchronized void initialize(){
		if(ConnettoreHTTPCORE.cm==null){
						
			ConnettoreHTTPCORE.cm = new PoolingHttpClientConnectionManager();
			// Increase max total connection to 200
			ConnettoreHTTPCORE.cm.setMaxTotal(10000);
			// Increase default max connection per route to 20
			ConnettoreHTTPCORE.cm.setDefaultMaxPerRoute(1000);
			// Increase max connections for localhost:80 to 50
			//HttpHost localhost = new HttpHost("locahost", 80);
			//cm.setMaxPerRoute(new HttpRoute(localhost), 50);
			 
			
		}
	}
	private static HttpClient getHttpClient(ConnectionKeepAliveStrategy keepAliveStrategy){
		// Caso senza pool
		
		HttpClientBuilder httpClientBuilder = HttpClients.custom();
		
		if(keepAliveStrategy!=null){
			httpClientBuilder.setKeepAliveStrategy(keepAliveStrategy);
		}
		
		return httpClientBuilder.build();
	}
	private static HttpClient getHttpClientFromPool(ConnectionKeepAliveStrategy keepAliveStrategy){
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
		
		HttpClientBuilder httpClientBuilder = HttpClients.custom();
		httpClientBuilder.setConnectionManager(ConnettoreHTTPCORE.cm);
		if(keepAliveStrategy!=null){
			httpClientBuilder.setKeepAliveStrategy(keepAliveStrategy);
		}
		
		HttpClient http = httpClientBuilder.build();
		
		//System.out.println("PRESA LA CONNESSIONE AVAILABLE["+cm.getTotalStats().getAvailable()+"] LEASED["
		//		+cm.getTotalStats().getLeased()+"] MAX["+cm.getTotalStats().getMax()+"] PENDING["+cm.getTotalStats().getPending()+"]");
		//System.out.println("-----GET CONNECTION [END] ----");
		return http;
	}
	
	
	@Override
	public boolean send(ConnettoreMsg request) {
		
		this.openspcoopProperties = OpenSPCoop2Properties.getInstance();
		
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
		
		// analsi i parametri specifici per il connettore
		if(this.properties.get(CostantiConnettori.CONNETTORE_LOCATION)==null){
			this.errore = "Proprieta' '"+CostantiConnettori.CONNETTORE_LOCATION+"' non fornita e richiesta da questo tipo di connettore ["+request.getTipoConnettore()+"]";
			return false;
		}
		
		// Identificativo modulo
		this.idModulo = request.getIdModulo();
		
		// Context per invocazioni handler
		this.outRequestContext = request.getOutRequestContext();
		this.msgDiagnostico = request.getMsgDiagnostico();
		
		try{
			
			// Creazione URL
			if(this.debug)
				this.logger.debug("Creazione URL...");
			this.location = this.properties.get(CostantiConnettori.CONNETTORE_LOCATION);
			
			// Impostazione Proprieta urlBased
			if(this.propertiesUrlBased != null && this.propertiesUrlBased.size()>0){
				this.location = ConnettoreUtils.buildLocationWithURLBasedParameter(this.propertiesUrlBased, this.location);
			}

			if(this.debug)
				this.logger.debug("Creazione URL ["+this.location+"]...");
			URL url = new URL( this.location );	
			
			
			// Keep-alive
			ConnectionKeepAliveStrategy keepAliveStrategy = new ConnectionKeepAliveStrategyCustom();
			
			
			// Creazione Connessione
			if(this.debug)
				this.logger.info("Creazione connessione alla URL ["+this.location+"]...",false);
			if(ConnettoreHTTPCORE.USE_POOL){
				this.httpClient = ConnettoreHTTPCORE.getHttpClientFromPool(keepAliveStrategy);
			}
			else{
				this.httpClient = ConnettoreHTTPCORE.getHttpClient(keepAliveStrategy);
			}
			HttpPost httppost = new HttpPost(url.toString());
			RequestConfig.Builder requestConfigBuilder = RequestConfig.custom();
			
			
			
			// Alcune implementazioni richiedono di aggiornare il Content-Type
			this.requestMsg.updateContentType();
			
						
			
			// Impostazione Content-Type della Spedizione su HTTP
	        
			if(this.debug)
				this.logger.debug("Impostazione content type...");
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
				this.logger.info("Impostazione http Content-Type ["+contentTypeRichiesta+"]",false);
			httppost.setHeader(Costanti.CONTENT_TYPE, contentTypeRichiesta);
			
			
			
			
			
			// Preparazione messaggio da spedire
			// Spedizione byte
			if(this.debug)
				this.logger.debug("Spedizione byte...");
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			if(this.sbustamentoSoap){
				if(this.debug)
					this.logger.debug("Sbustamento...");
				SoapUtils.sbustamentoMessaggio(this.requestMsg,out);
			}else{
				this.requestMsg.writeTo(out, true);
			}
			out.flush();
			out.close();
			if(this.debug){
				this.logger.info("Messaggio inviato (ContentType:"+contentTypeRichiesta+") :\n"+out.toString(),false);
			}
			HttpEntity httpEntity = new ByteArrayEntity(out.toByteArray());
			httppost.setEntity(httpEntity);

			
			
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
					this.logger.error("Parametro '"+CostantiConnettori.CONNETTORE_READ_CONNECTION_TIMEOUT+"' errato",e);
				}
			}
			if(readConnectionTimeout==-1){
				readConnectionTimeout = HttpUtilities.HTTP_READ_CONNECTION_TIMEOUT;
			}
			if(this.debug)
				this.logger.info("Impostazione http timeout CT["+connectionTimeout+"] RT["+readConnectionTimeout+"]",false);
			requestConfigBuilder.setConnectionRequestTimeout(connectionTimeout);
			requestConfigBuilder.setConnectTimeout(connectionTimeout);
			requestConfigBuilder.setSocketTimeout(readConnectionTimeout);
			


			
			
			
			// Gestione automatica del redirect
			//this.httpConn.setInstanceFollowRedirects(true); 
			
			
			
			
			
			
			// Aggiunga del SoapAction Header in caso di richiesta SOAP
			if(this.debug)
				this.logger.debug("Impostazione soap action...");
			String soapAction = null;
			if(this.sbustamentoSoap == false){
				soapAction = (String) this.requestMsg.getProperty(Costanti.SOAP_ACTION);
				if(soapAction==null)
					soapAction="\"OpenSPCoop\"";
				httppost.setHeader(Costanti.SOAP_ACTION, soapAction);
				// NOTA non quotare la soap action, per mantenere la trasparenza della PdD
				if(this.debug){
					this.logger.info("SOAP Action inviata ["+soapAction+"]",false);
				}
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
				httppost.setHeader(CostantiConnettori.HEADER_HTTP_AUTHORIZATION,authentication);
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
			if(this.propertiesTrasporto != null){
				Enumeration<?> enumSPC = this.propertiesTrasporto.keys();
				while( enumSPC.hasMoreElements() ) {
					String key = (String) enumSPC.nextElement();
					String value = (String) this.propertiesTrasporto.get(key);
					if(this.debug)
						this.logger.info("Set proprieta' ["+key+"]=["+value+"]",false);
					
					if(encodingRFC2047){
						if(RFC2047Utilities.isAllCharactersInCharset(value, charsetRFC2047)==false){
							String encoded = RFC2047Utilities.encode(new String(value), charsetRFC2047, encodingAlgorithmRFC2047);
							//System.out.println("@@@@ CODIFICA ["+value+"] in ["+encoded+"]");
							if(this.debug)
								this.logger.info("RFC2047 Encoded value in ["+encoded+"] (charset:"+charsetRFC2047+" encoding-algorithm:"+encodingAlgorithmRFC2047+")",false);
							setRequestHeader(httppost,validazioneHeaderRFC2047, key, encoded, this.logger);
						}
						else{
							setRequestHeader(httppost,validazioneHeaderRFC2047, key, value, this.logger);
						}
					}
					else{
						setRequestHeader(httppost,validazioneHeaderRFC2047, key, value, this.logger);
					}
				}
			}
			
			
			
			
			// Imposto Configurazione
			httppost.setConfig(requestConfigBuilder.build());
			
			
			
			// Spedizione byte
			if(this.debug)
				this.logger.debug("Spedizione byte...");
			// Eseguo la richiesta e prendo la risposta
			HttpResponse postResponse = this.httpClient.execute(httppost);
			this.httpEntityResponse = postResponse.getEntity();
			
			
			
			if(this.debug)
				this.logger.debug("Analisi risposta...");
			Header [] hdrRisposta = postResponse.getAllHeaders();
			if(hdrRisposta!=null){
				for (int i = 0; i < hdrRisposta.length; i++) {
					if(hdrRisposta[i].getName()==null){
						// Check per evitare la coppia che ha come chiave null e come valore HTTP OK 200
						this.propertiesTrasportoRisposta.put(CostantiConnettori.HEADER_HTTP_RETURN_CODE, hdrRisposta[i].getValue());
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
				this.logger.debug("Analisi risposta input stream e risultato http...");
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
			if(resultHTTPOperation>=400 && tipoRisposta!=null && tipoRisposta.contains(Costanti.CONTENT_TYPE_HTML)){
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
						while((readByte = this.isResponse.read(readB))!= -1){
							bout.write(readB,0,readByte);
						}
						this.isResponse.close();
						bout.flush();
						bout.close();
						this.logger.info("Messaggio ricevuto (ContentType:"+tipoRisposta+") :\n"+bout.toString(),false);
						// Creo nuovo inputStream
						this.isResponse = new ByteArrayInputStream(bout.toByteArray());
					}
					
					if(this.sbustamentoSoap==false){
						if(this.debug)
							this.logger.debug("Ricostruzione normale...");
						
						// Ricostruzione messaggio soap: secondo parametro a false, indica che il messaggio e' gia un SOAPMessage
						tipoLetturaRisposta = "Costruzione messaggio SOAP";
						
						
						
						if(contentLenght>0){
							OpenSPCoop2MessageParseResult pr = OpenSPCoop2MessageFactory.getMessageFactory().createMessage(this.isResponse,notifierInputStreamParams,false,tipoRisposta,locationRisposta, this.openspcoopProperties.isFileCacheEnable(), this.openspcoopProperties.getAttachmentRepoDir(), this.openspcoopProperties.getFileThreshold());	 
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
								int letti = this.isResponse.read(buffer);
								if(letti==1){
									// Per evitare il propblema del 'Premature end of file' che causa una system.out sul server.log di jboss
									SequenceInputStream sInput = new SequenceInputStream(new ByteArrayInputStream(buffer), this.isResponse);
									OpenSPCoop2MessageParseResult pr = OpenSPCoop2MessageFactory.getMessageFactory().createMessage(sInput,notifierInputStreamParams,false,tipoRisposta,locationRisposta, this.openspcoopProperties.isFileCacheEnable(), this.openspcoopProperties.getAttachmentRepoDir(), this.openspcoopProperties.getFileThreshold());
									if(pr.getParseException()!=null){
										this.getPddContext().addObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RISPOSTA_NON_RICONOSCIUTO_PARSE_EXCEPTION, pr.getParseException());
									}
									this.responseMsg = pr.getMessage_throwParseException();
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
										this.errore = "Errore avvenuto durante la consegna HTTP (lettura risposta): " + this.readExceptionMessageFromException(e);
										this.logger.error("Errore avvenuto durante la consegna HTTP (lettura risposta): " + this.readExceptionMessageFromException(e),e);
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
						boolean result2XX = (resultHTTPOperation>=200 && resultHTTPOperation<=299);
						boolean premature =  Utilities.existsInnerMessageException(e, "Premature end of file", true) && result2XX;
						// Se non ho un premature, ed un errore di lettura in 200, allora devo segnalare l'errore, altrimenti comunque 
						// il msg ritornato e' null e nel codiceStato vi e' l'errore.
						
						if( premature == false ){
							if(result2XX){
								this.eccezioneProcessamento = e;
								this.errore = "Errore avvenuto durante la consegna HTTP (lettura risposta): " + this.readExceptionMessageFromException(e);
								this.logger.error("Errore avvenuto durante la consegna HTTP (lettura risposta): " + this.readExceptionMessageFromException(e),e);
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

			// return code
			this.codice = resultHTTPOperation;

			if(this.debug)
				this.logger.info("Gestione invio/risposta http effettuata con successo",false);
			
			return true;			
			
			
		}  catch(Exception e){ 
			this.eccezioneProcessamento = e;
			this.logger.error("Errore avvenuto durante la consegna HTTP: "+this.readExceptionMessageFromException(e),e);
			this.errore = "Errore avvenuto durante la consegna HTTPNIO: "+this.readExceptionMessageFromException(e);
			return false;
		} 

	}

	
	@Override
	public void disconnect() throws ConnettoreException{
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
    	}catch(Exception e){
    		throw new ConnettoreException("Chiusura connessione non riuscita: "+e.getMessage(),e);
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
	
    private void setRequestHeader(HttpPost httppost, boolean validazioneHeaderRFC2047, String key, String value, ConnettoreLogger logger) {
    	
    	if(validazioneHeaderRFC2047){
    		try{
        		RFC2047Utilities.validHeader(key, value);
        		httppost.setHeader(key,value);
        	}catch(UtilsException e){
        		logger.error(e.getMessage(),e);
        	}
    	}
    	else{
    		httppost.setHeader(key,value);
    	}
    	
    }
}

class ConnectionKeepAliveStrategyCustom implements ConnectionKeepAliveStrategy{

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
        return 2 * 60 * 1000;
		
	}
	
}
