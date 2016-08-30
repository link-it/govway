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

package org.openspcoop2.pdd.services;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

import org.slf4j.Logger;
import org.openspcoop2.message.MessageUtils;
import org.openspcoop2.message.OpenSPCoop2MessageParseResult;
import org.openspcoop2.message.SOAPVersion;
import org.openspcoop2.pdd.core.autenticazione.Credenziali;
import org.openspcoop2.pdd.services.connector.ConnectorException;
import org.openspcoop2.pdd.services.connector.ConnectorInMessage;
import org.openspcoop2.pdd.services.connector.HttpServletConnectorInMessage;
import org.openspcoop2.protocol.engine.URLProtocolContext;
import org.openspcoop2.protocol.engine.constants.IDService;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.utils.Identity;
import org.openspcoop2.utils.io.notifier.NotifierInputStreamParams;

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
	private Identity identity;
	private URLProtocolContext urlProtocolContext;
	
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
	public OpenSPCoop2MessageParseResult getRequest(
			NotifierInputStreamParams notifierInputStreamParams,
			String contentType) throws ConnectorException {
		
		if(this.parseResult!=null){
			return this.parseResult;
		}

		if(this.connectorInMessage instanceof HttpServletConnectorInMessage){
			HttpServletConnectorInMessage http = (HttpServletConnectorInMessage) this.connectorInMessage;
			this.bout = new ByteArrayOutputStream();
			try{
				this.parseResult = http.getRequest(this.bout,notifierInputStreamParams, contentType); // il bout viene chiuso nel metodo interno
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
			this.parseResult = this.connectorInMessage.getRequest(notifierInputStreamParams, contentType);
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
						result.setParseException(MessageUtils.buildParseException(t));
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
	public URLProtocolContext getURLProtocolContext() throws ConnectorException {
		if(this.urlProtocolContext!=null){
			return this.urlProtocolContext;
		}
		this.urlProtocolContext = this.connectorInMessage.getURLProtocolContext();
		return this.urlProtocolContext;
	}
	@Override
	public int getContentLength() throws ConnectorException {
		if(this.contentLength!=null){
			return this.contentLength;
		}
		this.contentLength = this.connectorInMessage.getContentLength();
		return this.contentLength;
	}
	@Override
	public Identity getIdentity() throws ConnectorException {
		if(this.identity!=null){
			return this.identity;
		}
		this.identity = this.connectorInMessage.getIdentity();
		return this.identity;
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
	public IProtocolFactory getProtocolFactory() throws ConnectorException {
		// wrapped method
		return this.connectorInMessage.getProtocolFactory();
	}

	@Override
	public String getSOAPAction(SOAPVersion versioneSoap, String contentType)
			throws ConnectorException {
		// wrapped method
		return this.connectorInMessage.getSOAPAction(versioneSoap,contentType);
	}

	@Override
	public Credenziali getCredenziali() throws ConnectorException {
		// wrapped method
		return this.connectorInMessage.getCredenziali();
	}

	@Override
	public String getLocation(Credenziali credenziali)
			throws ConnectorException {
		// wrapped method
		return this.connectorInMessage.getLocation(credenziali);
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
