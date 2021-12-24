/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.openspcoop2.core.config.ResponseCachingConfigurazione;
import org.openspcoop2.core.transazioni.constants.TipoMessaggio;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.OpenSPCoop2SoapMessage;
import org.openspcoop2.message.constants.Costanti;
import org.openspcoop2.message.constants.MessageRole;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.soap.TunnelSoapUtils;
import org.openspcoop2.pdd.core.Utilities;
import org.openspcoop2.utils.io.DumpByteArrayOutputStream;
import org.openspcoop2.utils.transport.TransportUtils;
import org.openspcoop2.utils.transport.http.HttpConstants;


/**
 * Classe utilizzata per effettuare consegne di messaggi Soap, attraverso
 * l'invocazione di un server http. 
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class ConnettoreNULL extends ConnettoreBase {
	
	public final static String LOCATION = "govway://dev/null";
    
	


	/* ********  METODI  ******** */

	@Override
	protected boolean initializePreSend(ResponseCachingConfigurazione responseCachingConfig, ConnettoreMsg request) {
		
		if(this.initialize(request, false, responseCachingConfig)==false){
			return false;
		}
		
		return true;
		
	}
	
	@Override
	protected boolean send(ConnettoreMsg request) {
		
		this.codice = 200;
		
		try{
			
			// Tipologia di servizio
			MessageType requestMessageType = this.requestMsg.getMessageType();
			OpenSPCoop2SoapMessage soapMessageRequest = null;
			if(this.debug)
				this.logger.debug("Tipologia Servizio: "+this.requestMsg.getServiceBinding());
			if(this.isSoap){
				soapMessageRequest = this.requestMsg.castAsSoap();
			}
			
			
			// Collezione header di trasporto per dump
			Map<String, List<String>> propertiesTrasportoDebug = null;
			if(this.isDumpBinarioRichiesta()) {
				propertiesTrasportoDebug = new HashMap<String, List<String>>();
			}
			
			
			// Impostazione Content-Type
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
				this.logger.info("Impostazione Content-Type ["+contentTypeRichiesta+"]",false);
			if(contentTypeRichiesta!=null){
				setRequestHeader(HttpConstants.CONTENT_TYPE, contentTypeRichiesta, this.logger, propertiesTrasportoDebug);
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
					
					setRequestHeader(key, values, this.logger, propertiesTrasportoDebug);
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
			
			
			
			// SIMULAZIONE WRITE_TO
			boolean consumeRequestMessage = true;
			if(this.debug)
				this.logger.debug("Serializzazione (consume-request-message:"+consumeRequestMessage+")...");
			if(this.isDumpBinarioRichiesta()) {
				DumpByteArrayOutputStream bout = new DumpByteArrayOutputStream(this.dumpBinario_soglia, this.dumpBinario_repositoryFile, this.idTransazione, 
						TipoMessaggio.RICHIESTA_USCITA_DUMP_BINARIO.getValue());
				try {
					if(this.isSoap && this.sbustamentoSoap){
						this.logger.debug("Sbustamento...");
						TunnelSoapUtils.sbustamentoMessaggio(soapMessageRequest,bout);
					}else{
						this.requestMsg.writeTo(bout, consumeRequestMessage);
					}
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
			else {
				org.apache.commons.io.output.NullOutputStream nullOutputStream = org.apache.commons.io.output.NullOutputStream.NULL_OUTPUT_STREAM;
				if(this.isSoap && this.sbustamentoSoap){
					this.logger.debug("Sbustamento...");
					TunnelSoapUtils.sbustamentoMessaggio(soapMessageRequest,nullOutputStream);
				}else{
					this.requestMsg.writeTo(nullOutputStream, consumeRequestMessage);
				}
				nullOutputStream.flush();
				nullOutputStream.close();
			}
		
		}catch(Exception e){
			this.eccezioneProcessamento = e;
			this.logger.error("Riscontrato errore durante la writeTo",e);
			this.errore = "Riscontrato errore durante la writeTo: " +this.readExceptionMessageFromException(e);
			return false;
		}
		
		try{
			
			/* ------------  PostOutRequestHandler ------------- */
			this.postOutRequest();
			
		}catch(Exception e){
			this.eccezioneProcessamento = e;
			this.logger.error("Riscontrato errore durante la gestione PostOutRequestHandler",e);
			this.errore = "Riscontrato errore durante la gestione PostOutRequestHandler: " +this.readExceptionMessageFromException(e);
			return false;
		}
			
//		org.openspcoop2.utils.io.notifier.NotifierInputStreamParams notifierInputStreamParams = null;
//		try{
//			
//			/* ------------  PreInResponseHandler ------------- */
//			this.preInResponse();
//			
//			// Lettura risposta parametri NotifierInputStream per la risposta
//			if(this.preInResponseContext!=null){
//				notifierInputStreamParams = this.preInResponseContext.getNotifierInputStreamParams();
//			}
//			
//		}catch(Exception e){
//			this.eccezioneProcessamento = e;
//			ConnettoreNULL.log.error("Riscontrato errore durante la gestione PreInResponseHandler",e);
//			this.errore = "Riscontrato errore durante la gestione PreInResponseHandler: " +e.getMessage();
//			return false;
//		}
		
		try{
					
			OpenSPCoop2MessageFactory messageFactory = Utilities.getOpenspcoop2MessageFactory(this.logger.getLogger(),this.requestMsg, this.requestInfo, MessageRole.RESPONSE);
			this.responseMsg = messageFactory.createEmptyMessage(this.requestMsg.getMessageType(),MessageRole.RESPONSE);
			
		}catch(Exception e){
			this.eccezioneProcessamento = e;
			this.logger.error("Riscontrato errore durante la generazione di un msg SoapVuoto",e);
			this.errore = "Riscontrato errore durante la generazione di un msg SoapVuoto: " +this.readExceptionMessageFromException(e);
			return false;
		}
		
		return true;
	}

	
    private void setRequestHeader(String key, String value, ConnettoreLogger logger, Map<String, List<String>> propertiesTrasportoDebug) throws Exception {
    	List<String> list = new ArrayList<>(); 
    	list.add(value);
    	this.setRequestHeader(key, list, logger, propertiesTrasportoDebug);
    }
    private void setRequestHeader(String key, List<String> values, ConnettoreLogger logger, Map<String, List<String>> propertiesTrasportoDebug) throws Exception {
    	
    	if(this.debug) {
    		if(values!=null && !values.isEmpty()) {
        		for (String value : values) {
        			this.logger.info("Set proprietà trasporto ["+key+"]=["+value+"]",false);		
        		}
    		}
    	}
    	setRequestHeader(key, values, propertiesTrasportoDebug);
    	
    }
    @Override
	protected void setRequestHeader(String key,List<String> values) throws Exception {
    	// nop
    }
	
    /**
     * Ritorna l'informazione su dove il connettore sta spedendo il messaggio
     * 
     * @return location di inoltro del messaggio
     * @throws ConnettoreException 
     */
    @Override
	public String getLocation() throws ConnettoreException{
    	// Il connettore NULL ho possiede possibilita' di consegnare su di una url
		//return ConnettoreUtils.buildLocationWithURLBasedParameter(this.requestMsg, this.propertiesUrlBased, LOCATION);
    	return LOCATION;
    }
    
}





