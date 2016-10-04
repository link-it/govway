/*
 * OpenSPCoop - Customizable API Gateway 
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
package org.openspcoop2.pdd.services.connector;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.openspcoop2.message.MessageUtils;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.OpenSPCoop2MessageParseResult;
import org.openspcoop2.message.SOAPVersion;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.autenticazione.Credenziali;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.pdd.services.ServletUtils;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.engine.URLProtocolContext;
import org.openspcoop2.protocol.engine.constants.IDService;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.utils.Identity;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.io.notifier.NotifierInputStreamParams;

/**
 * HttpServletConnectorInMessage
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class HttpServletConnectorInMessage implements ConnectorInMessage {

	public static OpenSPCoop2MessageFactory factory = OpenSPCoop2MessageFactory.getMessageFactory();
	
	protected HttpServletRequest req;
	protected OpenSPCoop2Properties openspcoopProperties;
	protected OpenSPCoop2Message message;
	protected InputStream is;
	protected Logger log;
	protected String idModulo;
	private IDService idModuloAsIDService;
	protected Identity identity;
	
	public HttpServletConnectorInMessage(HttpServletRequest req,IDService idModuloAsIDService, String idModulo) throws ConnectorException{
		try{
			this.req = req;
			this.openspcoopProperties = OpenSPCoop2Properties.getInstance();
			this.is = this.req.getInputStream();
			
			this.log = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();
			if(this.log==null)
				this.log = LoggerWrapperFactory.getLogger(HttpServletConnectorInMessage.class);
			
			this.idModuloAsIDService = idModuloAsIDService;
			this.idModulo = idModulo;
			
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
	public IProtocolFactory getProtocolFactory() throws ConnectorException{
		try{
			return ProtocolFactoryManager.getInstance().getProtocolFactoryByServletContext(this.req);
		}catch(Exception e){
			throw new ConnectorException(e.getMessage(),e);
		}
	}
	
	@Override
	public String getContentType() throws ConnectorException{
		try{
			return ServletUtils.readContentTypeFromHeader(this.req);
		}catch(Exception e){
			throw new ConnectorException(e.getMessage(),e);
		}
	}
	
	@Override
	public String getSOAPAction(SOAPVersion versioneSoap, String contentType) throws ConnectorException{
		try{
			return ServletUtils.getSoapAction(this.req, versioneSoap, contentType);
		}catch(Exception e){
			throw new ConnectorException(e.getMessage(),e);
		}
	}
	
	@Override
	public OpenSPCoop2MessageParseResult getRequest(NotifierInputStreamParams notifierInputStreamParams, String contentType) throws ConnectorException{
		try{
			return factory.createMessage(this.is,notifierInputStreamParams,false,contentType,this.req.getContextPath(), 
					this.openspcoopProperties.isFileCacheEnable(), this.openspcoopProperties.getAttachmentRepoDir(), this.openspcoopProperties.getFileThreshold());
		}catch(Exception e){
			throw new ConnectorException(e.getMessage(),e);
		}	
	}
	// Metodo utile per il dump
	public OpenSPCoop2MessageParseResult getRequest(ByteArrayOutputStream buffer,NotifierInputStreamParams notifierInputStreamParams, String contentType) throws ConnectorException{
		try{
			byte[] b = null;
			ByteArrayInputStream bin = null;
			try{
				b = Utilities.getAsByteArray(this.is);
				bin = new ByteArrayInputStream(b);
			}catch(Throwable t){
				OpenSPCoop2MessageParseResult result = new OpenSPCoop2MessageParseResult();
				result.setParseException(MessageUtils.buildParseException(t));
				return result;
			}
			buffer.write(b);
			return factory.createMessage(bin,notifierInputStreamParams,false,contentType,this.req.getContextPath(), 
					this.openspcoopProperties.isFileCacheEnable(), this.openspcoopProperties.getAttachmentRepoDir(), this.openspcoopProperties.getFileThreshold());
		}catch(Throwable t){
			//throw new ConnectorException(e.getMessage(),e);
			OpenSPCoop2MessageParseResult result = new OpenSPCoop2MessageParseResult();
			result.setParseException(MessageUtils.buildParseException(t));
			return result;
		}	
	}
	
	@Override
	public byte[] getRequest() throws ConnectorException{
		try{
			return Utilities.getAsByteArray(this.is);
		}catch(Exception e){
			throw new ConnectorException(e.getMessage(),e);
		}
	}
	
	@Override
	public URLProtocolContext getURLProtocolContext() throws ConnectorException{
		try{
			return ServletUtils.getParametriInvocazionePorta(this.req,this.log);
		}catch(Exception e){
			throw new ConnectorException(e.getMessage(),e);
		}
	}
	
	@Override
	public Credenziali getCredenziali() throws ConnectorException{
		try{
			 return ServletUtils.getCredenziali(this.req,this.log);
		}catch(Exception e){
			throw new ConnectorException(e.getMessage(),e);
		}	
	}
	
	@Override
	public String getLocation(Credenziali credenziali) throws ConnectorException{
		try{
			 return ServletUtils.getLocation(this.req, credenziali);
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
	public Identity getIdentity() throws ConnectorException{
		if(this.identity==null){
			this.initIdentity();
		}
		return this.identity;
	}
	private synchronized void initIdentity(){
		if(this.identity==null){
			this.identity = new Identity(this.req);
		}
	} 
	
	public HttpServletRequest getHttpServletRequest(){
		return this.req;
	}
}
