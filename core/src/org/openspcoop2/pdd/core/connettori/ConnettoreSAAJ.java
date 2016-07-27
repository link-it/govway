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

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;

import javax.xml.soap.MimeHeader;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPMessage;

import org.apache.soap.encoding.soapenc.Base64;
import org.openspcoop2.core.api.constants.MethodType;
import org.openspcoop2.core.constants.CostantiConnettori;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.core.autenticazione.Credenziali;
import org.openspcoop2.pdd.mdb.ConsegnaContenutiApplicativi;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.resources.Charset;
import org.openspcoop2.utils.resources.HttpUtilities;
import org.openspcoop2.utils.resources.RFC2047Encoding;
import org.openspcoop2.utils.resources.RFC2047Utilities;

/**
 * Classe utilizzata per effettuare consegne di messaggi Soap, attraverso
 * l'invocazione di un server http. 
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class ConnettoreSAAJ extends ConnettoreBase {

	/** Logger utilizzato per debug. */
	private ConnettoreLogger logger = null;


	/* ********  F I E L D S  P R I V A T I  ******** */

	/** Msg di richiesta */
	private OpenSPCoop2Message requestMsg;
	/** Proprieta' del connettore */
	private java.util.Hashtable<String,String> properties;
	/** Busta */
	private Busta busta;
	/** Proprieta' del trasporto che deve gestire il connettore */
	private java.util.Properties propertiesTrasporto;
	/** Proprieta' urlBased che deve gestire il connettore */
	private java.util.Properties propertiesUrlBased;
	/** Tipo di Autenticazione */
	//private String tipoAutenticazione;
	/** Credenziali per l'autenticazione */
	private Credenziali credenziali;
	/** Identificativo */
	private String idMessaggio;
	
	/** Indicazione se siamo in modalita' debug */
	private boolean debug = false;

	/** Connessione */
	private SOAPConnection connection = null;
	
	/** Identificativo Modulo */
	private String idModulo = null;
	
	/** OpenSPCoopProperties */
	private OpenSPCoop2Properties openspcoopProperties = null;
	
	
	
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
		this.propertiesTrasporto = request.getPropertiesTrasporto();
		this.propertiesUrlBased = request.getPropertiesUrlBased();
		this.credenziali = request.getCredenziali();

		// Identificativo modulo
		this.idModulo = request.getIdModulo();
		
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

		// Context per invocazioni handler
		this.outRequestContext = request.getOutRequestContext();
		this.msgDiagnostico = request.getMsgDiagnostico();

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
			
			// check validita URL
			if(this.debug)
				this.logger.debug("Check validita URL...");
			this.location = this.properties.get(CostantiConnettori.CONNETTORE_LOCATION);
			URL urlTest = new URL( this.location );
			URLConnection connectionTest = urlTest.openConnection();
			HttpURLConnection httpConnTest = (HttpURLConnection) connectionTest;
			httpConnTest.setRequestMethod( MethodType.POST.name() );
			httpConnTest.setConnectTimeout(connectionTimeout);
			httpConnTest.setReadTimeout(readConnectionTimeout);
			httpConnTest.setDoOutput(true);
			java.io.OutputStream outTest = httpConnTest.getOutputStream();
			outTest.close();
			httpConnTest.disconnect();

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
			SOAPConnectionFactory soapConnFactory = OpenSPCoop2MessageFactory.getMessageFactory().getSOAPConnectionFactory();
			SOAPConnection connection = soapConnFactory.createConnection();
							    
			
			// Creazione URL
			if(this.debug)
				this.logger.debug("Creazione URL...");
			// Impostazione Proprieta urlBased
			if(this.propertiesUrlBased != null && this.propertiesUrlBased.size()>0){
				this.location = ConnettoreUtils.buildLocationWithURLBasedParameter(this.propertiesUrlBased, this.location);
			}
			String urlConnection = this.location;

			try{
				// Impostazione Proprieta del trasporto
				if(this.requestMsg.getMimeHeaders()!=null){
												
					// Header di trasporto
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
							if(CostantiPdD.HEADER_HTTP_USER_AGENT.equalsIgnoreCase(key)==false){
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
						this.requestMsg.getMimeHeaders().addHeader(CostantiConnettori.HEADER_HTTP_AUTHORIZATION,authentication);
						if(this.debug)
							this.logger.info("Impostazione autenticazione (username:"+user+" password:"+password+") ["+authentication+"]",false);
					}
				}
				
				// Impostazione timeout
				if(this.debug)
					this.logger.debug("Set timeout...");
				//TODO: connection.setTimeout(readConnectionTimeout);
				
				// Send
				if(this.debug)
					this.logger.debug("Send...");
				
				this.responseMsg = OpenSPCoop2MessageFactory.getMessageFactory().createMessage(this.requestMsg.getVersioneSoap(),connection.call((SOAPMessage) this.requestMsg,urlConnection));
				
			}catch(javax.xml.soap.SOAPException sendError){
				this.eccezioneProcessamento = sendError;
				String errorMsg = this.readExceptionMessageFromException(sendError);
				connection.close();
				this.errore = "Errore avvenuto durante la consegna SOAP (lettura risposta): " + errorMsg;
				return false;
			}

			// check consistenza risposta
			if(this.debug)
				this.logger.debug("Check consistenza...");
			if(this.responseMsg!=null){
				if(this.responseMsg.getSOAPBody()!=null){
					if(this.responseMsg.getSOAPBody().hasFault() ){
						if(this.responseMsg.getSOAPBody().getFault().getFaultString().indexOf("Premature end of file") != -1){
							// L'errore 'premature end of file' consiste solo nel fatto che una risposta non e' stata ricevuta.
							this.responseMsg = null;
						}
					}
				}
			}

			if(this.responseMsg!=null){
				// Analisi MimeType e ContentLocation della risposta
				if(this.responseMsg.getMimeHeaders()!=null){
					
					if(this.propertiesTrasportoRisposta==null){
						this.propertiesTrasportoRisposta = new Properties();
					}
					
					Iterator<?> it = this.responseMsg.getMimeHeaders().getAllHeaders();
					while (it.hasNext()) {
						MimeHeader mh = (MimeHeader) it.next();
						this.propertiesTrasportoRisposta.put(mh.getName(),mh.getValue());
						if("content-length".equalsIgnoreCase(mh.getName())){
							try{
								this.contentLength = Integer.parseInt(mh.getValue());
							}catch(Exception e){}
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
			this.logger.error("Errore avvenuto durante la consegna SOAP: "+this.readExceptionMessageFromException(e),e);
			this.errore = "Errore avvenuto durante la consegna SOAP: "+this.readExceptionMessageFromException(e);
			return false;
		}
	}

	/**
     * Effettua la disconnessione 
     */
    @Override
	public void disconnect() throws ConnettoreException{
    	
    	try{
    	
	    	if(this.connection!=null){
				if(this.debug && this.logger!=null)
					this.logger.debug("Connection.close ...");
				this.connection.close();
	    	}
	    	
	    	// super.disconnect (Per risorse base)
	    	super.disconnect();
	    	
    	}catch(Exception e){
    		throw new ConnettoreException("Chiusura connessione non riuscita: "+e.getMessage(),e);
    	}	
    }
    
    private void setRequestHeader(boolean validazioneHeaderRFC2047, String key, String value, ConnettoreLogger logger) {
    	
    	if(validazioneHeaderRFC2047){
    		try{
        		RFC2047Utilities.validHeader(key, value);
        		this.requestMsg.getMimeHeaders().addHeader(key,value);
        	}catch(UtilsException e){
        		logger.error(e.getMessage(),e);
        	}
    	}
    	else{
    		this.requestMsg.getMimeHeaders().addHeader(key,value);
    	}
    	
    }
    
}





