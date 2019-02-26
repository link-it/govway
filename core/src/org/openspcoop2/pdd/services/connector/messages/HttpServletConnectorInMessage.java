/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it). 
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.OpenSPCoop2MessageParseResult;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.exception.ParseExceptionUtils;
import org.openspcoop2.message.soap.SoapUtils;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.pdd.services.connector.ConnectorException;
import org.openspcoop2.protocol.engine.RequestInfo;
import org.openspcoop2.protocol.engine.URLProtocolContext;
import org.openspcoop2.protocol.engine.constants.IDService;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.io.notifier.NotifierInputStreamParams;
import org.openspcoop2.utils.transport.Credential;
import org.slf4j.Logger;

/**
 * HttpServletConnectorInMessage
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class HttpServletConnectorInMessage implements ConnectorInMessage {

	public static OpenSPCoop2MessageFactory factory = OpenSPCoop2MessageFactory.getMessageFactory();
	
	protected RequestInfo requestInfo;
	protected HttpServletRequest req;
	protected OpenSPCoop2Properties openspcoopProperties;
	protected OpenSPCoop2Message message;
	protected InputStream is;
	protected Logger log;
	protected String idModulo;
	private IDService idModuloAsIDService;
	private MessageType requestMessageType;
	protected Date dataIngressoRichiesta;
	
	public HttpServletConnectorInMessage(RequestInfo requestInfo, HttpServletRequest req,
			IDService idModuloAsIDService, String idModulo) throws ConnectorException{
		try{
			this.requestInfo = requestInfo;
			this.req = req;
			this.openspcoopProperties = OpenSPCoop2Properties.getInstance();
			this.is = this.req.getInputStream();
			
			this.log = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();
			if(this.log==null)
				this.log = LoggerWrapperFactory.getLogger(HttpServletConnectorInMessage.class);
			
			this.idModuloAsIDService = idModuloAsIDService;
			this.idModulo = idModulo;
			
			if(IDService.PORTA_APPLICATIVA.equals(idModuloAsIDService)){
				this.requestMessageType = this.getRequestInfo().getProtocolRequestMessageType();
			}
			else{
				this.requestMessageType = this.getRequestInfo().getIntegrationRequestMessageType();
			}
			
		}catch(Exception e){
			throw new ConnectorException(e.getMessage(),e);
		}
	}
	
	@Override
	public IDService getIdModuloAsIDService(){
		return this.idModuloAsIDService;
	}
	
	@Override
	public String getIdModulo(){
		return this.idModulo;
	}
	
	@Override
	public void updateRequestInfo(RequestInfo requestInfo) throws ConnectorException{
		this.requestInfo = requestInfo;
		if(IDService.PORTA_APPLICATIVA.equals(this.idModuloAsIDService)){
			this.requestMessageType = this.getRequestInfo().getProtocolRequestMessageType();
		}
		else{
			this.requestMessageType = this.getRequestInfo().getIntegrationRequestMessageType();
		}
	}
	
	@Override
	public RequestInfo getRequestInfo(){
		return this.requestInfo;
	}
	
	@Override
	public Object getAttribute(String key) throws ConnectorException {
		return this.req.getAttribute(key);
	}
	
	@Override
	public String getHeader(String key) throws ConnectorException{
		return this.req.getHeader(key);
	}
	
	@Override
	public String getParameter(String key) throws ConnectorException{
		return this.req.getParameter(key);
	}

	@Override
	public IProtocolFactory<?> getProtocolFactory() throws ConnectorException{
		return this.requestInfo.getProtocolFactory();
	}
	
	@Override
	public String getContentType() throws ConnectorException{
		try{
			return this.requestInfo.getProtocolContext().getContentType(true);
		}catch(Exception e){
			throw new ConnectorException(e.getMessage(),e);
		}
	}
	
	@Override
	public String getSOAPAction() throws ConnectorException{
		try{
			String contentType = this.getContentType();
			return SoapUtils.getSoapAction(this.requestInfo.getProtocolContext(), this.requestMessageType, contentType);
		}catch(Exception e){
			throw new ConnectorException(e.getMessage(),e);
		}
	}
	
	@Override
	public OpenSPCoop2MessageParseResult getRequest(NotifierInputStreamParams notifierInputStreamParams) throws ConnectorException{
		try{
			OpenSPCoop2MessageParseResult pr = factory.createMessage(this.requestMessageType,
					this.requestInfo.getProtocolContext(),
					this.is,notifierInputStreamParams,
					this.openspcoopProperties.getAttachmentsProcessingMode());
			this.dataIngressoRichiesta = DateManager.getDate();
			return pr;
		}catch(Exception e){
			throw new ConnectorException(e.getMessage(),e);
		}	
	}
	// Metodo utile per il dump
	public OpenSPCoop2MessageParseResult getRequest(ByteArrayOutputStream buffer,NotifierInputStreamParams notifierInputStreamParams) throws ConnectorException{
		try{
			byte[] b = null;
			ByteArrayInputStream bin = null;
			try{
				b = Utilities.getAsByteArray(this.is,false); // se l'input stream is empty ritorna null grazie al parametro false
				if(b!=null) {
					bin = new ByteArrayInputStream(b);
				}
			}catch(Throwable t){
				OpenSPCoop2MessageParseResult result = new OpenSPCoop2MessageParseResult();
				result.setParseException(ParseExceptionUtils.buildParseException(t));
				return result;
			}
			if(b!=null) {
				buffer.write(b);
			}
			OpenSPCoop2MessageParseResult pr = factory.createMessage(this.requestMessageType,
					this.requestInfo.getProtocolContext(),
					bin,notifierInputStreamParams,
					this.openspcoopProperties.getAttachmentsProcessingMode());
			this.dataIngressoRichiesta = DateManager.getDate();
			return pr;
		}catch(Throwable t){
			//throw new ConnectorException(e.getMessage(),e);
			OpenSPCoop2MessageParseResult result = new OpenSPCoop2MessageParseResult();
			result.setParseException(ParseExceptionUtils.buildParseException(t));
			return result;
		}	
	}
	
	@Override
	public byte[] getRequest() throws ConnectorException{
		try{
			byte[] b = Utilities.getAsByteArray(this.is,false); // se l'input stream is empty ritorna null grazie al parametro false
			this.dataIngressoRichiesta = DateManager.getDate();
			return b;
		}catch(Exception e){
			throw new ConnectorException(e.getMessage(),e);
		}
	}
	
	@Override
	public Date getDataIngressoRichiesta(){	
		return this.dataIngressoRichiesta;
	}
	
	@Override
	public URLProtocolContext getURLProtocolContext() throws ConnectorException{
		try{
			return this.requestInfo.getProtocolContext();
		}catch(Exception e){
			throw new ConnectorException(e.getMessage(),e);
		}
	}
	
	@Override
	public Credential getCredential() throws ConnectorException{
		try{
			return this.requestInfo.getProtocolContext().getCredential();
		}catch(Exception e){
			throw new ConnectorException(e.getMessage(),e);
		}	
	}
	
	@Override
	public String getSource() throws ConnectorException{
		try{
			 return this.requestInfo.getProtocolContext().getSource();
		}catch(Exception e){
			throw new ConnectorException(e.getMessage(),e);
		}	
	}
	
	@Override
	public String getProtocol() throws ConnectorException{
		return this.req.getProtocol();
	}
	
	@Override
	public int getContentLength() throws ConnectorException{
		return this.req.getContentLength();
	}
	
	@Override
	public void close() throws ConnectorException{
		try{
			if(this.is!=null){
				try{
					this.is.close();
					this.is = null;
				}catch(Exception e){}
			}
		}catch(Exception e){
			throw new ConnectorException(e.getMessage(),e);
		}	
	}
	
	@Override
	public String getRemoteAddress() throws ConnectorException{
		return this.req.getRemoteAddr();
	}
	
	public HttpServletRequest getHttpServletRequest(){
		return this.req;
	}
}
