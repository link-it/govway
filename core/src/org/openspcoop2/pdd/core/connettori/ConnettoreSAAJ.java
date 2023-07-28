/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jakarta.xml.soap.MimeHeader;
import jakarta.xml.soap.SOAPConnection;
import jakarta.xml.soap.SOAPConnectionFactory;
import jakarta.xml.soap.SOAPMessage;

import org.openspcoop2.core.config.ResponseCachingConfigurazione;
import org.openspcoop2.core.constants.CostantiConnettori;
import org.openspcoop2.core.transazioni.constants.TipoMessaggio;
import org.openspcoop2.message.MessageUtils;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.OpenSPCoop2SoapMessage;
import org.openspcoop2.message.constants.Costanti;
import org.openspcoop2.message.constants.MessageRole;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.pdd.core.Utilities;
import org.openspcoop2.pdd.mdb.ConsegnaContenutiApplicativi;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.io.Base64Utilities;
import org.openspcoop2.utils.io.DumpByteArrayOutputStream;
import org.openspcoop2.utils.resources.Charset;
import org.openspcoop2.utils.transport.TransportResponseContext;
import org.openspcoop2.utils.transport.TransportUtils;
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

public class ConnettoreSAAJ extends ConnettoreBaseWithResponse {

    @Override
	public String getProtocollo() {
    	return "HTTP";
    }
	
	public static final String ENDPOINT_TYPE = "saaj";
	

	/** Connessione */
	private SOAPConnection connection = null;

	private SOAPMessage soapRequestMessage = null;
	
	
	/* ********  METODI  ******** */

	@Override
	protected boolean initializePreSend(ResponseCachingConfigurazione responseCachingConfig, ConnettoreMsg request) {
		
		if(this.initialize(request, true, responseCachingConfig)==false){
			return false;
		}
		
		return true;
		
	}
	
	@Override
	protected boolean send(ConnettoreMsg request) {

		// analsi i parametri specifici per il connettore
		if(this.properties.get(CostantiConnettori.CONNETTORE_LOCATION)==null){
			this.errore = "Proprieta' '"+CostantiConnettori.CONNETTORE_LOCATION+"' non fornita e richiesta da questo tipo di connettore ["+request.getTipoConnettore()+"]";
			return false;
		}

		return sendSAAJ();

	}


	/**
	 * Si occupa di effettuare la consegna SOAP (invocazione di un WebService).
	 * Si aspetta di ricevere una risposta non sbustata.
	 *
	 * @return true in caso di consegna con successo, false altrimenti
	 * 
	 */
	private boolean sendSAAJ(){
		try{
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
				this.logger.info("Impostazione http timeout CT["+connectionTimeout+"] RT["+readConnectionTimeout+"]...",false);
			
			// check validity message
			if(ServiceBinding.SOAP.equals(this.requestMsg.getServiceBinding())==false) {
				throw new Exception("Connettore utilizzabile solamente in un contesto SOAP (trovato: "+this.requestMsg.getServiceBinding()+")");
			}
			
			// check validita URL
			if(this.debug)
				this.logger.debug("Check validita URL...");
			this.location = this.properties.get(CostantiConnettori.CONNETTORE_LOCATION);
			this.location = ConnettoreUtils.buildLocationWithURLBasedParameter(this.logger!=null ? this.logger.getLogger() : null, this.requestMsg, ENDPOINT_TYPE, this.propertiesUrlBased, this.location,
					this.getProtocolFactory(), this.idModulo);
			
			URL urlTest = new URL( this.location );
			URLConnection connectionTest = urlTest.openConnection();
			HttpURLConnection httpConnTest = (HttpURLConnection) connectionTest;
			httpConnTest.setRequestMethod( HttpRequestMethod.POST.name() );
			httpConnTest.setConnectTimeout(connectionTimeout);
			httpConnTest.setReadTimeout(readConnectionTimeout);
			httpConnTest.setDoOutput(true);
			java.io.OutputStream outTest = httpConnTest.getOutputStream();
			outTest.close();
			httpConnTest.disconnect();

			
			
			// Check tipo di messaggio
			MessageType requestMessageType = this.requestMsg.getMessageType();
			if(ServiceBinding.SOAP.equals(this.requestMsg.getServiceBinding())==false){
				throw new Exception("Connettore utilizzabile solamente per tipologia di servizio SOAP");
			}
			OpenSPCoop2SoapMessage soapRequestMessage = this.requestMsg.castAsSoap();
			this.soapRequestMessage = MessageUtils.getSOAPMessage(soapRequestMessage, false, this.idTransazione);

			// Collezione header di trasporto per dump
			Map<String, List<String>> propertiesTrasportoDebug = null;
			if(this.isDumpBinarioRichiesta()) {
				propertiesTrasportoDebug = new HashMap<>();
			}
			
			
			// impostazione property
			if(this.debug)
				this.logger.debug("PrefixOptimization...");
			String prefixOptimization = "true";
			if(this.properties.get("prefix-optimization")!=null){
				if("false".equalsIgnoreCase(this.properties.get("prefix-optimization")) )
					prefixOptimization = "false";
				this.logger.info("Prefix Optimization = '"+prefixOptimization+"'",false);
				//AxisProperties.setProperty(AxisEngine.PROP_ENABLE_NAMESPACE_PREFIX_OPTIMIZATION,prefixOptimization);  		
			}
			

			// Consegna
			if(this.debug)
				this.logger.debug("Creazione connessione...");	
			OpenSPCoop2MessageFactory messageFactory = Utilities.getOpenspcoop2MessageFactory(this.logger.getLogger(),this.requestMsg, this.requestInfo, MessageRole.NONE);
			SOAPConnectionFactory soapConnFactory = messageFactory.getSOAPConnectionFactory();
			this.connection = soapConnFactory.createConnection();
							    
			
			try{
				// Impostazione Proprieta del trasporto
				
				// Impostazione Content-Type
				String contentTypeRichiesta = null;
				if(this.debug)
					this.logger.debug("Impostazione content type...");
				if(this.isSoap){
					contentTypeRichiesta = this.requestMsg.getContentType();
					if(contentTypeRichiesta==null){
						throw new Exception("Content-Type del messaggio da spedire non definito");
					}
				}
				else{
					contentTypeRichiesta = this.requestMsg.getContentType();
					// Content-Type non obbligatorio in REST
				}
				if(this.debug)
					this.logger.info("Impostazione Content-Type ["+contentTypeRichiesta+"]",false);
				if(contentTypeRichiesta!=null){
					setRequestHeader(HttpConstants.CONTENT_TYPE, contentTypeRichiesta, propertiesTrasportoDebug);
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
				
				// Impostazione timeout
				if(this.debug)
					this.logger.debug("Set timeout...");
				//TODO: connection.setTimeout(readConnectionTimeout);
				
				
				
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
					Iterator<String> keys = this.propertiesTrasporto.keySet().iterator();
					while (keys.hasNext()) {
						String key = (String) keys.next();
						if(HttpConstants.USER_AGENT.equalsIgnoreCase(key)){
							continue;
						}
						List<String> values = this.propertiesTrasporto.get(key);
						if(this.debug) {
				    		if(values!=null && !values.isEmpty()) {
				        		for (String value : values) {
				        			this.logger.info("Set Transport Header ["+key+"]=["+value+"]",false);
				        		}
				    		}
				    	}
						
						if(encodingRFC2047){
							List<String> valuesEncoded = new ArrayList<>();
							if(values!=null && !values.isEmpty()) {
				        		for (String value : values) {
				        			if(RFC2047Utilities.isAllCharactersInCharset(value, charsetRFC2047)==false){
										String encoded = RFC2047Utilities.encode(new String(value), charsetRFC2047, encodingAlgorithmRFC2047);
										//System.out.println("@@@@ CODIFICA ["+value+"] in ["+encoded+"]");
										if(this.debug)
											this.logger.info("RFC2047 Encoded value in ["+encoded+"] (charset:"+charsetRFC2047+" encoding-algorithm:"+encodingAlgorithmRFC2047+")",false);
										valuesEncoded.add(encoded);
									}
									else{
										valuesEncoded.add(value);
									}
				        		}
							}
							setRequestHeader(validazioneHeaderRFC2047, key, valuesEncoded, this.logger, propertiesTrasportoDebug);
						}
						else{
							setRequestHeader(validazioneHeaderRFC2047, key, values, this.logger, propertiesTrasportoDebug);
						}
					}
				}
				
				
				
				
				// Aggiunta del SoapAction Header in caso di richiesta SOAP
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
						this.soapAction = soapRequestMessage.getSoapAction();
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
				
				
				
				if(this.isDumpBinarioRichiesta()) {
					DumpByteArrayOutputStream bout = new DumpByteArrayOutputStream(this.dumpBinario_soglia, this.dumpBinario_repositoryFile, this.idTransazione, 
							TipoMessaggio.RICHIESTA_USCITA_DUMP_BINARIO.getValue());
					try {
						this.emitDiagnosticStartDumpBinarioRichiestaUscita();
						
						this.requestMsg.writeTo(bout, false);
						bout.flush();
						bout.close();
						
						this.dumpBinarioRichiestaUscita(bout, requestMessageType, contentTypeRichiesta, this.location, propertiesTrasportoDebug);
					}finally {
						try {
							bout.clearResources();
						}catch(Throwable t) {
							this.logger.error("Release resources failed: "+t.getMessage(),t);
						}
					}
				}
								
				// Send
				if(this.debug)
					this.logger.debug("Send...");
				
				SOAPMessage soapMsg = this.connection.call( this.soapRequestMessage, this.location);
				
				this.dataRichiestaInoltrata = DateManager.getDate();
				
				this.responseMsg = messageFactory.
						createMessage(this.requestMsg.getMessageType(),MessageRole.RESPONSE,soapMsg);
				
				this.dataAccettazioneRisposta = DateManager.getDate();
				
			}catch(jakarta.xml.soap.SOAPException sendError){
				this.eccezioneProcessamento = sendError;
				String errorMsg = this.readExceptionMessageFromException(sendError);
				this.connection.close();
				this.errore = "Errore avvenuto durante la consegna SOAP (lettura risposta): " + errorMsg;
				return false;
			}

			// check consistenza risposta
			if(this.debug)
				this.logger.debug("Check consistenza...");
			SOAPMessage soapResponse = null;
			if(this.responseMsg!=null){
				OpenSPCoop2SoapMessage soapMessageResponse = this.responseMsg.castAsSoap();
				soapResponse = soapMessageResponse.getSOAPMessage();
				if(soapMessageResponse.getSOAPBody()!=null){
					if(soapMessageResponse.hasSOAPFault() ){
						if(soapMessageResponse.getSOAPBody().getFault().getFaultString().indexOf("Premature end of file") != -1){
							// L'errore 'premature end of file' consiste solo nel fatto che una risposta non e' stata ricevuta.
							this.responseMsg = null;
						}
					}
				}
			}

			if(soapResponse!=null){
				// Analisi MimeType e ContentLocation della risposta
				if(soapResponse.getMimeHeaders()!=null){
					
					if(this.propertiesTrasportoRisposta==null){
						this.propertiesTrasportoRisposta = new HashMap<>();
					}
					
					Iterator<?> it = soapResponse.getMimeHeaders().getAllHeaders();
					while (it.hasNext()) {
						MimeHeader mh = (MimeHeader) it.next();
						if(mh!=null && mh.getName()!=null && mh.getValue()!=null) {
							TransportUtils.addHeader(this.propertiesTrasportoRisposta,mh.getName(),mh.getValue());
							if("content-length".equalsIgnoreCase(mh.getName())){
								try{
									this.contentLength = Integer.parseInt(mh.getValue());
								}catch(Exception e){}
							}
							if("content-type".equalsIgnoreCase(mh.getName())){
								try{
									this.tipoRisposta = mh.getValue();
								}catch(Exception e){}
							}
						}
					}
				}
			}
			
			// return code
			this.codice = 200;

			// content length
			if(this.responseMsg!=null && this.contentLength<0){
				this.contentLength =  this.responseMsg.getIncomingMessageContentLength();
			}
			
			if(this.responseMsg!=null){
				TransportResponseContext responseContext = new TransportResponseContext(this.logger.getLogger());
				responseContext.setCodiceTrasporto(this.codice+"");
				responseContext.setContentLength(this.contentLength);
				responseContext.setHeaders(this.propertiesTrasportoRisposta);
				this.responseMsg.setTransportResponseContext(responseContext);
				
				this.messageTypeResponse = this.requestMsg.getMessageType();
				if(this.tipoRisposta==null) {
					this.tipoRisposta = this.requestMsg.getContentType();
				}
			}
			
			if(this.isDumpBinarioRisposta()){
				if(this.responseMsg!=null){
					// Registro Debug.
					DumpByteArrayOutputStream bout = null;
					try {
						try {
							bout = new DumpByteArrayOutputStream(this.dumpBinario_soglia, this.dumpBinario_repositoryFile, this.idTransazione, 
									TipoMessaggio.RISPOSTA_INGRESSO_DUMP_BINARIO.getValue());
							
							this.emitDiagnosticStartDumpBinarioRispostaIngresso();
							
							this.responseMsg.writeTo(bout, false);
						}finally {
							try {
								if(bout!=null) {
									bout.flush();
								}
							}catch(Throwable t) {
								// ignore
							}
							try {
								if(bout!=null) {
									bout.close();
								}
							}catch(Throwable t) {
								// ignore
							}
						}
						if(this.debug) {
							this.logger.info("Messaggio ricevuto (ContentType:"+this.tipoRisposta+") :\n"+bout.toString(),false);
						}
						
						this.dumpBinarioRispostaIngresso(bout, this.messageTypeResponse, this.propertiesTrasportoRisposta);
						
						if(this.contentLength<0){
							this.contentLength=bout.size(); // la prendo dal dump, poiche' in saaj non ho modo di agganciare il counting stream
						}
						
					}finally {
						try {
							if(bout!=null) {
								bout.clearResources();
							}
						}catch(Throwable t) {
							this.logger.error("Release resources failed: "+t.getMessage(),t);
						}
					}
				}
				else {
					if(this.debug) {
						if(this.tipoRisposta!=null) {
							this.logger.info("Messaggio ricevuto (ContentType:"+this.tipoRisposta+") senza contenuto nell'http-reply",false);
						}
						else {
							this.logger.info("Messaggio ricevuto senza contenuto nell'http-reply",false);
						}
					}
					
					// devo registrare almeno gli header HTTP
					this.emitDiagnosticStartDumpBinarioRispostaIngresso();
					this.dumpBinarioRispostaIngresso(null, null, this.propertiesTrasportoRisposta);
				}
			}
			
			
			if(this.debug)
				this.logger.info("Gestione invio/risposta http effettuata con successo",false);
			
			
			
			
			
			/* ------------  PostOutRequestHandler ------------- */
			this.postOutRequest();
			
			
			
			
			
//			/* ------------  PreInResponseHandler ------------- */
			// Con connettore SAAJ non e' utilizzabile
//			this.preInResponse();
//			
//			// Lettura risposta parametri NotifierInputStream per la risposta
//			org.openspcoop.utils.io.notifier.NotifierInputStreamParams notifierInputStreamParams = null;
//			if(this.preInResponseContext!=null){
//				notifierInputStreamParams = this.preInResponseContext.getNotifierInputStreamParams();
//			}
			
			
			
			
			return true;
		}  catch(Exception e){ 
			this.eccezioneProcessamento = e;
			String msgErrore = this.readExceptionMessageFromException(e);
			if(this.generateErrorWithConnectorPrefix) {
				this.errore = "Errore avvenuto durante la consegna SOAP: "+msgErrore;
			}
			else {
				this.errore = msgErrore;
			}
			this.logger.error("Errore avvenuto durante la consegna SOAP: "+msgErrore,e);
			return false;
		}
	}

	/**
     * Effettua la disconnessione 
     */
    @Override
	public void disconnect() throws ConnettoreException{
    	
    	List<Throwable> listExceptionChiusura = new ArrayList<Throwable>();
		try{
    	
	    	if(this.connection!=null){
				if(this.debug && this.logger!=null)
					this.logger.debug("Connection.close ...");
				try {
					this.connection.close();
				}catch(Throwable t) {
					if(this.logger!=null) {
						this.logger.debug("Chiusura socket fallita: "+t.getMessage(),t);
					}
					listExceptionChiusura.add(t);
				}
	    	}
	    	
	    	// super.disconnect (Per risorse base)
	    	try {
	    		super.disconnect();
	    	}catch(Throwable t) {
	    		if(this.logger!=null) {
					this.logger.debug("Chiusura risorse fallita: "+t.getMessage(),t);
	    		}
    			listExceptionChiusura.add(t);
    		}
	    	
    	}catch(Exception e){
    		throw new ConnettoreException("Chiusura connessione non riuscita: "+e.getMessage(),e);
    	}	
		
		if(listExceptionChiusura!=null && !listExceptionChiusura.isEmpty()) {
			org.openspcoop2.utils.UtilsMultiException multiException = new org.openspcoop2.utils.UtilsMultiException(listExceptionChiusura.toArray(new Throwable[1]));
			throw new ConnettoreException("Chiusura connessione non riuscita: "+multiException.getMessage(),multiException);
		}
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
    		setRequestHeader(key,values, propertiesTrasportoDebug);
    	}
    	
    }
    
    @Override
	protected void setRequestHeader(String key,List<String> values) throws Exception {
    	if(values!=null && !values.isEmpty()) {
    		for (String value : values) {
    			this.soapRequestMessage.getMimeHeaders().addHeader(key,value);	
			}
    	}
    }
    
}





