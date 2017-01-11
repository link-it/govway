/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2017 Link.it srl (http://link.it). 
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

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

import org.openspcoop2.message.OpenSPCoop2MessageParseResult;
import org.openspcoop2.message.exception.ParseExceptionUtils;
import org.openspcoop2.pdd.services.RequestInfo;
import org.openspcoop2.pdd.services.connector.ConnectorException;
import org.openspcoop2.protocol.engine.URLProtocolContext;
import org.openspcoop2.protocol.engine.constants.IDService;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.utils.io.notifier.NotifierInputStreamParams;
import org.openspcoop2.utils.transport.Credential;
import org.slf4j.Logger;

/**
 * DumpRawConnectorInMessage
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DumpRawConnectorInMessage implements ConnectorInMessage {

	private ConnectorInMessage connectorInMessage;
	private Logger log;
	private ByteArrayOutputStream bout = null;
	private OpenSPCoop2MessageParseResult parseResult = null;
	private String contentType;
	private Integer contentLength;
	
	public DumpRawConnectorInMessage(Logger log,ConnectorInMessage connectorInMessage){
		this.log = log;
		this.connectorInMessage = connectorInMessage;
	}

	public ConnectorInMessage getWrappedConnectorInMessage() {
		return this.connectorInMessage;
	}
	
	public byte[] getRequestAsByte(){
		if(this.bout!=null){
			return this.bout.toByteArray();
		}
		return null;
	}
	public String getRequestAsString(){
		if(this.bout!=null){
			return this.bout.toString();
		}
		return null;
	}
	public boolean isParsingRequestError(){
		return this.parseResult!=null && this.parseResult.getParseException()!=null;
	}
	public String getParsingRequestErrorAsString(){
		if(this.parseResult!=null && this.parseResult.getParseException()!=null){
			try{
				ByteArrayOutputStream bout = new ByteArrayOutputStream();
				PrintWriter pw = new PrintWriter(bout);
				this.parseResult.getParseException().getSourceException().printStackTrace(pw);
				pw.flush();
				bout.flush();
				pw.close();
				bout.close();
				return bout.toString();
			}catch(Exception e){
				return "ParsingRequestError, serializazione eccezione non riuscita: "+e.getMessage();
			}
		}
		return null;
	}
	
	
	@Override
	public OpenSPCoop2MessageParseResult getRequest(NotifierInputStreamParams notifierInputStreamParams) throws ConnectorException {
		
		if(this.parseResult!=null){
			return this.parseResult;
		}

		if(this.connectorInMessage instanceof HttpServletConnectorInMessage){
			HttpServletConnectorInMessage http = (HttpServletConnectorInMessage) this.connectorInMessage;
			this.bout = new ByteArrayOutputStream();
			try{
				this.parseResult = http.getRequest(this.bout,notifierInputStreamParams); // il bout viene chiuso nel metodo interno
				if(this.bout.size()<=0){
					this.bout = null;
				}
			}finally{
				try{
					if(this.bout!=null){
						this.bout.flush();
					}
				}catch(Throwable close){}
				try{
					if(this.bout!=null){
						this.bout.close();
					}
				}catch(Throwable close){}
			}
		}
		else{
			this.parseResult = this.connectorInMessage.getRequest(notifierInputStreamParams);
			if(this.parseResult.getMessage()!=null){
				try{
					this.bout = new ByteArrayOutputStream();
					this.parseResult.getMessage().writeTo(this.bout, false);
				}catch(Throwable t){
					this.bout = null;
					OpenSPCoop2MessageParseResult result = new OpenSPCoop2MessageParseResult();
					if(this.parseResult.getMessage().getParseException()!=null){
						result.setParseException(this.parseResult.getMessage().getParseException());
					}
					else{
						result.setParseException(ParseExceptionUtils.buildParseException(t));
					}
					return result;
				}finally{
					try{
						if(this.bout!=null){
							this.bout.flush();
						}
					}catch(Throwable close){}
					try{
						if(this.bout!=null){
							this.bout.close();
						}
					}catch(Throwable close){}
				}
			}
		}
		
		return this.parseResult;
		
	}

	@Override
	public byte[] getRequest() throws ConnectorException {
		if(this.bout!=null){
			return this.bout.toByteArray();
		}
		
		try{
			this.bout = new ByteArrayOutputStream();
			byte [] tmp = this.connectorInMessage.getRequest();
			if(tmp!=null){
				this.bout.write(tmp);
			}
		}catch(Throwable t){
			try{
				this.bout = new ByteArrayOutputStream();
				this.bout.write(("getRequest error: "+t.getMessage()).getBytes());
			}catch(Throwable tWrite){}
			this.log.error("getRequest error: "+t.getMessage(),t);
		}finally{
			try{
				if(this.bout!=null){
					this.bout.flush();
				}
			}catch(Throwable close){}
			try{
				if(this.bout!=null){
					this.bout.close();
				}
			}catch(Throwable close){}
		}
		
		if(this.bout!=null){
			return this.bout.toByteArray();
		}
		else{
			return null;
		}
	}
	
	@Override
	public String getContentType() throws ConnectorException {
		if(this.contentType!=null){
			return this.contentType;
		}
		this.contentType = this.connectorInMessage.getContentType();
		return this.contentType;
	}
	@Override
	public int getContentLength() throws ConnectorException {
		if(this.contentLength!=null){
			return this.contentLength;
		}
		this.contentLength = this.connectorInMessage.getContentLength();
		return this.contentLength;
	}
	
	
	
	// Wrapped Only
	
	@Override
	public IDService getIdModuloAsIDService() {
		// wrapped method
		return this.connectorInMessage.getIdModuloAsIDService();
	}

	@Override
	public String getIdModulo() {
		// wrapped method
		return this.connectorInMessage.getIdModulo();
	}

	@Override
	public void updateRequestInfo(RequestInfo requestInfo) throws ConnectorException{
		// wrapped method
		this.connectorInMessage.updateRequestInfo(requestInfo);
	}
	
	@Override
	public RequestInfo getRequestInfo(){
		// wrapped method
		return this.connectorInMessage.getRequestInfo();
	}
	
	@Override
	public Object getAttribute(String key) throws ConnectorException {
		// wrapped method
		return this.connectorInMessage.getAttribute(key);
	}

	@Override
	public String getHeader(String key) throws ConnectorException {
		// wrapped method
		return this.connectorInMessage.getHeader(key);
	}

	@Override
	public String getParameter(String key) throws ConnectorException {
		// wrapped method
		return this.connectorInMessage.getParameter(key);
	}

	@Override
	public IProtocolFactory<?> getProtocolFactory() throws ConnectorException {
		// wrapped method
		return this.connectorInMessage.getProtocolFactory();
	}

	@Override
	public String getSOAPAction()
			throws ConnectorException {
		// wrapped method
		return this.connectorInMessage.getSOAPAction();
	}

	@Override
	public URLProtocolContext getURLProtocolContext() throws ConnectorException {
		// wrapped method
		return this.connectorInMessage.getURLProtocolContext();
	}
	
	@Override
	public Credential getCredential() throws ConnectorException {
		// wrapped method
		return this.connectorInMessage.getCredential();
	}

	@Override
	public String getSource()
			throws ConnectorException {
		// wrapped method
		return this.connectorInMessage.getSource();
	}

	@Override
	public String getProtocol() throws ConnectorException {
		// wrapped method
		return this.connectorInMessage.getProtocol();
	}

	@Override
	public void close() throws ConnectorException {
		// wrapped method
		this.connectorInMessage.close();
	}

}
