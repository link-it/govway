/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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

import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;

import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.Proprieta;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageProperties;
import org.openspcoop2.message.OpenSPCoop2RestMessage;
import org.openspcoop2.message.OpenSPCoop2SoapMessage;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.message.exception.MessageException;
import org.openspcoop2.pdd.config.ConfigurazionePdDManager;
import org.openspcoop2.pdd.config.CostantiProprieta;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.services.connector.ConnectorException;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.constants.IDService;
import org.openspcoop2.protocol.sdk.state.RequestInfo;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.io.DumpByteArrayOutputStream;
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
	protected RequestInfo requestInfo;
	protected String idModulo;
	protected IDService idModuloAsIDService;
	protected OpenSPCoop2Properties openspcoopProperties;
	
	
	public HttpServletConnectorOutMessage(RequestInfo requestInfo,IProtocolFactory<?> protocolFactory, HttpServletResponse res,
			IDService idModuloAsIDService, String idModulo) throws ConnectorException{
		try{
			this.res = res;
			this.protocolFactory = protocolFactory;
			this.requestInfo = requestInfo;
			this.idModuloAsIDService = idModuloAsIDService;
			this.idModulo = idModulo;
			this.openspcoopProperties =  OpenSPCoop2Properties.getInstance();
		}catch(Exception e){
			throw new ConnectorException(e.getMessage(),e);
		}
	}

	private void sendHeadersEngine(OpenSPCoop2Message msg) throws ConnectorException, MessageException {
		if(msg==null) {
			throw new ConnectorException("Message is null");
		}
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
				String key = keys.next();
				List<String> values = forwardHeader.getPropertyValues(key);
				if(values!=null && !values.isEmpty()) {
					for (String value : values) {
						this.addHeader(key, value);	
					}
				}
			}
		}
	}
	
	@Override
	public void sendResponse(OpenSPCoop2Message msg, boolean consume) throws ConnectorException {
		try{
			if(msg==null) {
				throw new ConnectorException("Message is null");
			}
			
			// Propago eventuali header http
			this.sendHeadersEngine(msg);
			
			boolean hasContent = false;
			
			// il save e' necessario con i connettori directVM in caso di errori di validazione
			if(ServiceBinding.SOAP.equals(msg.getServiceBinding())){
				hasContent = true;
				OpenSPCoop2SoapMessage soap = msg.castAsSoap();
				if(soap.hasSOAPFault()){
					soap.saveChanges();
				}
			}
			if(ServiceBinding.REST.equals(msg.getServiceBinding())){
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
	public void sendResponse(DumpByteArrayOutputStream message) throws ConnectorException{
		try{
			if(message!=null && message.size()>0) {
				this.outNullable = this.res.getOutputStream();
				if(message.isSerializedOnFileSystem()) {
					try(FileInputStream fin = new FileInputStream(message.getSerializedFile())) {
						Utilities.copy(fin, this.outNullable);
					}
				}
				else {
					this.outNullable.write(message.toByteArray());
				}
			}
		}catch(Exception e){
			throw new ConnectorException(e.getMessage(),e);
		}
	}
	
	@Override
	public void sendResponseHeaders(OpenSPCoop2Message message) throws ConnectorException{
		try{
			// Propago eventuali header http
			this.sendHeadersEngine(message);
		}catch(Exception e){
			throw new ConnectorException(e.getMessage(),e);
		}
	}
	
	@Override
	public void setHeader(String key,String value) throws ConnectorException{
		putHeaderEngine(key,value,false);
	}
	@Override
	public void addHeader(String key,String value) throws ConnectorException{
		putHeaderEngine(key,value,true);
	}
	private void putHeaderEngine(String key,String value, boolean add) throws ConnectorException{
		try{
			if(value==null) {
				return;
			}
			
			boolean encodingRFC2047 = false;
			Charset charsetRFC2047 = null;
			RFC2047Encoding encodingAlgorithmRFC2047 = null;
			boolean validazioneHeaderRFC2047 = false;
			List<Proprieta> listProprieta = null;
			if(this.idModuloAsIDService!=null){
				switch (this.idModuloAsIDService) {
				case PORTA_DELEGATA:
				case PORTA_DELEGATA_INTEGRATION_MANAGER:
				case PORTA_DELEGATA_XML_TO_SOAP:
					encodingRFC2047 = this.openspcoopProperties.isEnabledEncodingRFC2047HeaderValueRicezioneContenutiApplicativi();
					charsetRFC2047 = this.openspcoopProperties.getCharsetEncodingRFC2047HeaderValueRicezioneContenutiApplicativi();
					encodingAlgorithmRFC2047 = this.openspcoopProperties.getEncodingRFC2047HeaderValueRicezioneContenutiApplicativi();
					validazioneHeaderRFC2047 = this.openspcoopProperties.isEnabledValidazioneRFC2047HeaderNameValueRicezioneContenutiApplicativi();
					listProprieta = readProprietaPortaDelegata();
					break;
				case PORTA_APPLICATIVA:
					encodingRFC2047 = this.openspcoopProperties.isEnabledEncodingRFC2047HeaderValueRicezioneBuste();
					charsetRFC2047 = this.openspcoopProperties.getCharsetEncodingRFC2047HeaderValueRicezioneBuste();
					encodingAlgorithmRFC2047 = this.openspcoopProperties.getEncodingRFC2047HeaderValueRicezioneBuste();
					validazioneHeaderRFC2047 = this.openspcoopProperties.isEnabledValidazioneRFC2047HeaderNameValueRicezioneBuste();
					listProprieta = readProprietaPortaApplicativa();
					break;
				default:
					break;
				}
			}
			
			encodingRFC2047 = CostantiProprieta.isConnettoriHeaderValueEncodingRFC2047ResponseEnabled(listProprieta, encodingRFC2047);
			charsetRFC2047 = CostantiProprieta.getConnettoriHeaderValueEncodingRFC2047ResponseCharset(listProprieta, charsetRFC2047);
			encodingAlgorithmRFC2047 = CostantiProprieta.getConnettoriHeaderValueEncodingRFC2047ResponseType(listProprieta, encodingAlgorithmRFC2047);
			validazioneHeaderRFC2047 = CostantiProprieta.isConnettoriHeaderValidationResponseEnabled(listProprieta, validazioneHeaderRFC2047);
			/**System.out.println("@@@@ encodingRFC2047["+encodingRFC2047+"] charsetRFC2047["+charsetRFC2047+"] encodingAlgorithmRFC2047["+encodingAlgorithmRFC2047+"] validazioneHeaderRFC2047["+validazioneHeaderRFC2047+"]");*/
			if(encodingRFC2047){
				/**System.out.println("@@@@ CONTROLLO '"+value+" rispetto a '"+charsetRFC2047+"': "+RFC2047Utilities.isAllCharactersInCharset(value, charsetRFC2047));*/
				if(!RFC2047Utilities.isAllCharactersInCharset(value, charsetRFC2047)){
					String encoded = RFC2047Utilities.encode((value+""), charsetRFC2047, encodingAlgorithmRFC2047);
					/**System.out.println("@@@@ RESPONSE CODIFICA ["+value+"] in ["+encoded+"]");*/
					this.putResponseHeader(validazioneHeaderRFC2047, key, encoded, add);
				}
				else{
					this.putResponseHeader(validazioneHeaderRFC2047, key, value, add);
				}
			}
			else{
				this.putResponseHeader(validazioneHeaderRFC2047, key, value, add);
			}	
			
		}catch(Exception e){
			throw new ConnectorException(e.getMessage(),e);
		}
	}
	
	private List<Proprieta> readProprietaPortaApplicativa(){
		if(this.requestInfo!=null && this.requestInfo.getProtocolContext()!=null && this.requestInfo.getProtocolContext().getInterfaceName()!=null && 
				StringUtils.isNotEmpty(this.requestInfo.getProtocolContext().getInterfaceName())) {
			IDPortaApplicativa idPA = new IDPortaApplicativa();
			idPA.setNome(this.requestInfo.getProtocolContext().getInterfaceName());
			try {
				PortaApplicativa pa = ConfigurazionePdDManager.getInstance().getPortaApplicativaSafeMethod(idPA, this.requestInfo);
				if(pa!=null && pa.sizeProprieta()>0) {
					return pa.getProprieta();
				}
			}catch(Exception e) {
				if(this.protocolFactory!=null && this.protocolFactory.getLogger()!=null) {
					this.protocolFactory.getLogger().error("Accesso porta applicativa ["+this.requestInfo.getProtocolContext().getInterfaceName()+"] fallito: "+e.getMessage(),e);
				}
			}
		}
		return null;
	}
	private List<Proprieta> readProprietaPortaDelegata(){
		if(this.requestInfo!=null && this.requestInfo.getProtocolContext()!=null && this.requestInfo.getProtocolContext().getInterfaceName()!=null && 
				StringUtils.isNotEmpty(this.requestInfo.getProtocolContext().getInterfaceName())) {
			IDPortaDelegata idPD = new IDPortaDelegata();
			idPD.setNome(this.requestInfo.getProtocolContext().getInterfaceName());
			try {
				PortaDelegata pd = ConfigurazionePdDManager.getInstance().getPortaDelegataSafeMethod(idPD, this.requestInfo);
				if(pd!=null && pd.sizeProprieta()>0) {
					return pd.getProprieta();
				}
			}catch(Exception e) {
				if(this.protocolFactory!=null && this.protocolFactory.getLogger()!=null) {
					this.protocolFactory.getLogger().error("Accesso porta applicativa ["+this.requestInfo.getProtocolContext().getInterfaceName()+"] fallito: "+e.getMessage(),e);
				}
			}
		}
		return null;
	}
	
	private void putResponseHeader(boolean validazioneHeaderRFC2047, String key, String value, boolean add) {
    	
    	if(validazioneHeaderRFC2047){
    		try{
        		RFC2047Utilities.validHeader(key, value);
        		if(add) {
        			this.res.addHeader(key,value);
        		}
        		else {
        			this.res.setHeader(key,value);
        		}
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
    		if(add) {
    			this.res.addHeader(key,value);
    		}
    		else {
    			this.res.setHeader(key,value);
    		}
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
