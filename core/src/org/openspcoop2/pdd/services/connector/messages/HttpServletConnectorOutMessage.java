/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it). 
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
package org.openspcoop2.pdd.services.connector.messages;

import java.io.OutputStream;
import java.util.Iterator;

import javax.servlet.http.HttpServletResponse;

import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageProperties;
import org.openspcoop2.message.OpenSPCoop2RestMessage;
import org.openspcoop2.message.OpenSPCoop2SoapMessage;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.services.connector.ConnectorException;
import org.openspcoop2.protocol.engine.constants.IDService;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.resources.Charset;
import org.openspcoop2.utils.transport.http.RFC2047Encoding;
import org.openspcoop2.utils.transport.http.RFC2047Utilities;

/**
 * HttpServletConnectorOutMessage
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class HttpServletConnectorOutMessage implements ConnectorOutMessage {

	protected HttpServletResponse res;
	protected OutputStream outNullable;
	protected IProtocolFactory<?> protocolFactory;
	protected String idModulo;
	protected IDService idModuloAsIDService;
	protected OpenSPCoop2Properties openspcoopProperties;
	
	
	public HttpServletConnectorOutMessage(IProtocolFactory<?> protocolFactory, HttpServletResponse res,
			IDService idModuloAsIDService, String idModulo) throws ConnectorException{
		try{
			this.res = res;
			this.protocolFactory = protocolFactory;
			this.idModuloAsIDService = idModuloAsIDService;
			this.idModulo = idModulo;
			this.openspcoopProperties =  OpenSPCoop2Properties.getInstance();
		}catch(Exception e){
			throw new ConnectorException(e.getMessage(),e);
		}
	}

	private void _sendHeaders(OpenSPCoop2Message msg) throws Exception {
		// Propago eventuali header http
		OpenSPCoop2MessageProperties forwardHeader = null;
		if(ServiceBinding.REST.equals(msg.getServiceBinding())) {
			forwardHeader = msg.getForwardTransportHeader(this.openspcoopProperties.getRESTServicesHeadersForwardConfig(false));
		}
		else {
			forwardHeader = msg.getForwardTransportHeader(this.openspcoopProperties.getSOAPServicesHeadersForwardConfig(false));
		}
		if(forwardHeader!=null && forwardHeader.size()>0){
			Iterator<String> keys = forwardHeader.getKeys();
			while (keys.hasNext()) {
				String key = (String) keys.next();
				String value = forwardHeader.getProperty(key);
				this.setHeader(key, value);
			}
		}
	}
	
	@Override
	public void sendResponse(OpenSPCoop2Message msg, boolean consume) throws ConnectorException {
		try{
			// Propago eventuali header http
			this._sendHeaders(msg);
			
			boolean hasContent = false;
			
			// il save e' necessario con i connettori directVM in caso di errori di validazione
			if(msg!=null && ServiceBinding.SOAP.equals(msg.getServiceBinding())){
				hasContent = true;
				OpenSPCoop2SoapMessage soap = msg.castAsSoap();
				if(soap.getSOAPBody()!=null && soap.getSOAPBody().hasFault()){
					soap.saveChanges();
				}
			}
			if(msg!=null && ServiceBinding.REST.equals(msg.getServiceBinding())){
				OpenSPCoop2RestMessage<?> rest = msg.castAsRest();
				hasContent = rest.hasContent();
			}
			
			if(hasContent) {
				this.outNullable = this.res.getOutputStream();
				msg.writeTo(this.outNullable,consume);
			}
		}catch(Exception e){
			throw new ConnectorException(e.getMessage(),e);
		}
	}
	
	@Override
	public void sendResponse(byte[] message) throws ConnectorException{
		try{
			if(message!=null && message.length>0) {
				this.outNullable = this.res.getOutputStream();
				this.outNullable.write(message);
			}
		}catch(Exception e){
			throw new ConnectorException(e.getMessage(),e);
		}
	}
	
	@Override
	public void sendResponseHeaders(OpenSPCoop2Message message) throws ConnectorException{
		try{
			// Propago eventuali header http
			this._sendHeaders(message);
		}catch(Exception e){
			throw new ConnectorException(e.getMessage(),e);
		}
	}
	
	@Override
	public void setHeader(String key,String value) throws ConnectorException{
		try{		
			boolean encodingRFC2047 = false;
			Charset charsetRFC2047 = null;
			RFC2047Encoding encodingAlgorithmRFC2047 = null;
			boolean validazioneHeaderRFC2047 = false;
			if(this.idModuloAsIDService!=null){
				switch (this.idModuloAsIDService) {
				case PORTA_DELEGATA:
				case PORTA_DELEGATA_INTEGRATION_MANAGER:
				case PORTA_DELEGATA_XML_TO_SOAP:
					encodingRFC2047 = this.openspcoopProperties.isEnabledEncodingRFC2047HeaderValue_ricezioneContenutiApplicativi();
					charsetRFC2047 = this.openspcoopProperties.getCharsetEncodingRFC2047HeaderValue_ricezioneContenutiApplicativi();
					encodingAlgorithmRFC2047 = this.openspcoopProperties.getEncodingRFC2047HeaderValue_ricezioneContenutiApplicativi();
					validazioneHeaderRFC2047 = this.openspcoopProperties.isEnabledValidazioneRFC2047HeaderNameValue_ricezioneContenutiApplicativi();
					break;
				case PORTA_APPLICATIVA:
					encodingRFC2047 = this.openspcoopProperties.isEnabledEncodingRFC2047HeaderValue_ricezioneBuste();
					charsetRFC2047 = this.openspcoopProperties.getCharsetEncodingRFC2047HeaderValue_ricezioneBuste();
					encodingAlgorithmRFC2047 = this.openspcoopProperties.getEncodingRFC2047HeaderValue_ricezioneBuste();
					validazioneHeaderRFC2047 = this.openspcoopProperties.isEnabledValidazioneRFC2047HeaderNameValue_ricezioneBuste();
					break;
				default:
					break;
				}
			}
			
			if(encodingRFC2047){
				if(RFC2047Utilities.isAllCharactersInCharset(value, charsetRFC2047)==false){
					String encoded = RFC2047Utilities.encode(new String(value), charsetRFC2047, encodingAlgorithmRFC2047);
					//System.out.println("@@@@ RESPONSE CODIFICA ["+value+"] in ["+encoded+"]");
					this.setResponseHeader(validazioneHeaderRFC2047, key, encoded);
				}
				else{
					this.setResponseHeader(validazioneHeaderRFC2047, key, value);
				}
			}
			else{
				this.setResponseHeader(validazioneHeaderRFC2047, key, value);
			}	
			
		}catch(Exception e){
			throw new ConnectorException(e.getMessage(),e);
		}
	}
	
	private void setResponseHeader(boolean validazioneHeaderRFC2047, String key, String value) {
    	
    	if(validazioneHeaderRFC2047){
    		try{
        		RFC2047Utilities.validHeader(key, value);
        		this.res.setHeader(key,value);
        	}catch(UtilsException e){
        		if(this.protocolFactory!=null && this.protocolFactory.getLogger()!=null){
        			this.protocolFactory.getLogger().error(e.getMessage(),e);
        		}
        		else{
        			LoggerWrapperFactory.getLogger(HttpServletConnectorOutMessage.class).error(e.getMessage(),e);		
        		}
        	}
    	}
    	else{
    		this.res.setHeader(key,value);
    	}
    	
    }
	
	@Override
	public void setContentLength(int length) throws ConnectorException{
		try{
			this.res.setContentLength(length);
		}catch(Exception e){
			throw new ConnectorException(e.getMessage(),e);
		}
	}
	
	@Override
	public void setContentType(String type) throws ConnectorException{
		try{
			this.res.setContentType(type);
		}catch(Exception e){
			throw new ConnectorException(e.getMessage(),e);
		}
	}
	
	private int status = -1;
	@Override
	public void setStatus(int status) throws ConnectorException{
		try{
			this.res.setStatus(status);
			this.status = status;
		}catch(Exception e){
			throw new ConnectorException(e.getMessage(),e);
		}
	}
	@Override
	public int getResponseStatus() throws ConnectorException{
		return this.status;
	}
	
	@Override
	public void flush(boolean throwException) throws ConnectorException{
		try{
			// Flush and close response
			// NOTA: per poter ottenere l'errore di BrokenPipe sempre, deve essere disabilitato il socketBufferOutput sul servlet container.
			// Se non lo si disabilta, l'errore viene ritornato solo se il messaggio supera la dimensione del buffer (default: 8192K)
			// Ad esempio in tomcat utilizzare (socketBuffer="-1"): 
			//    <Connector protocol="HTTP/1.1" port="8080" address="${jboss.bind.address}" 
            //       connectionTimeout="20000" redirectPort="8443" socketBuffer="-1" />
			if(this.res!=null){
				try{
					this.res.flushBuffer();
				}catch(Exception e){
					if(throwException){
						throw e;
					}
				}
			}
			if(this.outNullable!=null){
				try{
					this.outNullable.flush();
				}catch(Exception e){
					if(throwException){
						throw e;
					}
				}
			}
		}catch(Exception e){
			throw new ConnectorException(e.getMessage(),e);
		}	
	}
	
	@Override
	public void close(boolean throwException) throws ConnectorException{
		try{
			if(this.outNullable!=null){
				try{
					this.outNullable.close();
					this.outNullable = null;
				}catch(Exception e){
					if(throwException){
						throw e;
					}
				}
			}
		}catch(Exception e){
			throw new ConnectorException(e.getMessage(),e);
		}	
	}
}
