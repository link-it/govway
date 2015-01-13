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

import org.apache.log4j.Logger;
import org.apache.soap.encoding.soapenc.Base64;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.core.autenticazione.Credenziali;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.utils.resources.HttpUtilities;

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
	private Logger log = null;


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

		this.log = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();
		
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
		this.busta = request.getBusta();
		if(this.busta!=null)
			this.idMessaggio=this.busta.getID();
		this.propertiesTrasporto = request.getPropertiesTrasporto();
		this.propertiesUrlBased = request.getPropertiesUrlBased();
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
			
			// check validita URL
			if(this.debug)
				this.log.debug("["+this.idMessaggio+"] check validita URL...");
			this.location = this.properties.get("location");
			URL urlTest = new URL( this.location );
			URLConnection connectionTest = urlTest.openConnection();
			HttpURLConnection httpConnTest = (HttpURLConnection) connectionTest;
			httpConnTest.setRequestMethod( "POST" );
			httpConnTest.setConnectTimeout(connectionTimeout);
			httpConnTest.setReadTimeout(readConnectionTimeout);
			httpConnTest.setDoOutput(true);
			java.io.OutputStream outTest = httpConnTest.getOutputStream();
			outTest.close();
			httpConnTest.disconnect();

			// impostazione property
			if(this.debug)
				this.log.debug("["+this.idMessaggio+"] prefixOptimization...");
			String prefixOptimization = "true";
			if(this.properties.get("prefix-optimization")!=null){
				if("false".equalsIgnoreCase(this.properties.get("prefix-optimization")) )
					prefixOptimization = "false";
				this.log.info("Prefix Optimization = '"+prefixOptimization+"'");
				
				//AxisProperties.setProperty(AxisEngine.PROP_ENABLE_NAMESPACE_PREFIX_OPTIMIZATION,prefixOptimization);  		
			}

			// Consegna
			if(this.debug)
				this.log.debug("["+this.idMessaggio+"] creazione connessione...");	
			SOAPConnectionFactory soapConnFactory = OpenSPCoop2MessageFactory.getMessageFactory().getSOAPConnectionFactory();
			SOAPConnection connection = soapConnFactory.createConnection();
							    
			
			// Creazione URL
			if(this.debug)
				this.log.debug("["+this.idMessaggio+"] creazione URL...");
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
						this.log.debug("["+this.idMessaggio+"] impostazione header di trasporto...");
					if(this.propertiesTrasporto != null){
						Enumeration<?> enumSPC = this.propertiesTrasporto.keys();
						while( enumSPC.hasMoreElements() ) {
							String key = (String) enumSPC.nextElement();
							if(CostantiPdD.HEADER_HTTP_USER_AGENT.equalsIgnoreCase(key)==false){
								String value = (String) this.propertiesTrasporto.get(key);
								if(this.debug)
									this.log.debug("["+this.idMessaggio+"] set proprieta' ["+key+"]=["+value+"]...");
								this.requestMsg.getMimeHeaders().addHeader(key,value);
							}
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
						this.requestMsg.getMimeHeaders().addHeader("Authorization",authentication);
						if(this.debug)
							this.log.debug("["+this.idMessaggio+"] impostazione autenticazione (username:"+user+" password:"+password+") ["+authentication+"]...");
					}
				}
				
				// Impostazione timeout
				if(this.debug)
					this.log.debug("["+this.idMessaggio+"] set timeout...");
				//TODO: connection.setTimeout(readConnectionTimeout);
				
				// Send
				if(this.debug)
					this.log.debug("["+this.idMessaggio+"] send...");
				
				this.responseMsg = OpenSPCoop2MessageFactory.getMessageFactory().createMessage(connection.call((SOAPMessage) this.requestMsg,urlConnection));
				
			}catch(javax.xml.soap.SOAPException sendError){
				this.eccezioneProcessamento = sendError;
				String errorMsg = sendError.getMessage();
				connection.close();
				this.errore = "Errore avvenuto durante la consegna SOAP (lettura risposta): " + errorMsg;
				return false;
			}

			// check consistenza risposta
			if(this.debug)
				this.log.debug("["+this.idMessaggio+"] check consistenza...");
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
				this.log.debug("["+this.idMessaggio+"] gestione invio/risposta http effettuata con successo");
			
			
			
			
			
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
			this.log.error("Errore avvenuto durante la consegna SOAP: "+e.getMessage());
			this.errore = "Errore avvenuto durante la consegna SOAP: "+e.getMessage();
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
				if(this.debug && this.log!=null)
					this.log.debug("["+this.idMessaggio+"] connection.close ...");
				this.connection.close();
	    	}
	    	
	    	// super.disconnect (Per risorse base)
	    	super.disconnect();
	    	
    	}catch(Exception e){
    		throw new ConnettoreException("Chiusura connessione non riuscita: "+e.getMessage(),e);
    	}	
    }
    
}





