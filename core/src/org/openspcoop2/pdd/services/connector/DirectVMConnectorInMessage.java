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
package org.openspcoop2.pdd.services.connector;

import java.io.ByteArrayOutputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.openspcoop2.message.Costanti;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.OpenSPCoop2MessageParseResult;
import org.openspcoop2.message.SOAPVersion;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.autenticazione.Credenziali;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.protocol.engine.URLProtocolContext;
import org.openspcoop2.protocol.engine.constants.IDService;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.utils.Identity;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.io.notifier.NotifierInputStreamParams;

/**
 * DirectVMConnectorInMessage
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DirectVMConnectorInMessage implements ConnectorInMessage {

	public static OpenSPCoop2MessageFactory factory = OpenSPCoop2MessageFactory.getMessageFactory();
	
	private OpenSPCoop2Message message;
	private Logger log;
	private String idModulo;
	private IDService idModuloAsIDService;
	private IProtocolFactory protocolFactory;
	private String function;
	private String url;
	private Credenziali credenziali;
	private String functionParameters;
	private DirectVMProtocolInfo directVMProtocolInfo;
	
	public DirectVMConnectorInMessage(OpenSPCoop2Message msg,IDService idModuloAsIDService, String idModulo,
			Properties trasporto,
			Properties formUrl,
			IProtocolFactory protocolFactory,
			String function, String url,
			Credenziali credenziali,
			String functionParameters,
			DirectVMProtocolInfo directVMProtocolInfo,
			PdDContext pddContext) throws ConnectorException{
		try{
			this.message = msg;
			
			this.log = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();
			if(this.log==null)
				this.log = LoggerWrapperFactory.getLogger(DirectVMConnectorInMessage.class);
			
			this.idModuloAsIDService = idModuloAsIDService;
			this.idModulo = idModulo;
			
			Enumeration<?> enTrasporto = trasporto.keys();
			while (enTrasporto.hasMoreElements()) {
				String key = (String) enTrasporto.nextElement();
				this.headers.put(key, trasporto.getProperty(key));
			}
			
			Enumeration<?> enUrlBased = formUrl.keys();
			while (enUrlBased.hasMoreElements()) {
				String key = (String) enUrlBased.nextElement();
				this.parameters.put(key, formUrl.getProperty(key));
			}
			
			this.protocolFactory = protocolFactory;
			
			this.url = url;
			this.function = function;
			
			this.credenziali = credenziali;
			
			this.functionParameters = functionParameters;
			
			this.directVMProtocolInfo = directVMProtocolInfo;
			
			if(pddContext!=null){
				this.setAttribute(CostantiPdD.OPENSPCOOP2_PDD_CONTEXT_HEADER_HTTP,pddContext);
			}
			
		}catch(Exception e){
			throw new ConnectorException(e.getMessage(),e);
		}
	}

	public DirectVMProtocolInfo getDirectVMProtocolInfo() {
		return this.directVMProtocolInfo;
	}
	
	@Override
	public IDService getIdModuloAsIDService(){
		return this.idModuloAsIDService;
	}
	
	@Override
	public String getIdModulo(){
		return this.idModulo;
	}
	
	
	private Map<String, Object> attributes = new Hashtable<String, Object>();
	public void setAttribute(String key, Object object) throws ConnectorException {
		this.attributes.put(key, object);
	}
	@Override
	public Object getAttribute(String key) throws ConnectorException {
		return this.attributes.get(key);
	}
	
	
	private Map<String, String> headers = new Hashtable<String, String>();
	public void setHeader(String key, String value) throws ConnectorException {
		this.headers.put(key, value);
	}
	@Override
	public String getHeader(String key) throws ConnectorException{
		return this.headers.get(key);
	}
	
	
	private Map<String, String> parameters = new Hashtable<String, String>();
	public void setParameter(String key, String value) throws ConnectorException {
		this.parameters.put(key, value);
	}
	@Override
	public String getParameter(String key) throws ConnectorException{
		return this.parameters.get(key);
	}

	
	@Override
	public IProtocolFactory getProtocolFactory() throws ConnectorException{
		return this.protocolFactory;
	}
	
	@Override
	public String getContentType() throws ConnectorException{
		try{
			return this.message.getContentType();
		}catch(Exception e){
			throw new ConnectorException(e.getMessage(),e);
		}
	}
	
	@Override
	public String getSOAPAction(SOAPVersion versioneSoap, String contentType) throws ConnectorException{
		try{
			String soapAction = null;
			if(this.message.getMimeHeaders()!=null){
				String [] hdrs = this.message.getMimeHeaders().getHeader(Costanti.SOAP_ACTION);
				if(hdrs!=null && hdrs.length>0){
					soapAction = hdrs[0];
				}
			}
			
			if(SOAPVersion.SOAP11.equals(versioneSoap)){
				
				if(soapAction==null){
					throw new Exception("Header http '"+SOAPVersion.SOAP11_MANDATORY_HEADER_HTTP_SOAP_ACTION+"' non valorizzato (null)");
				}
				soapAction = soapAction.trim();
				if(soapAction.startsWith("\"")){
					if(!soapAction.endsWith("\"")){
						throw new Exception("Header http '"+SOAPVersion.SOAP11_MANDATORY_HEADER_HTTP_SOAP_ACTION+"' non valorizzato correttamente (action quotata? Non è stato trovato il carattere di chiusura \" ma è presente quello di apertura)");
					}	
				}
				if(soapAction.endsWith("\"")){
					if(!soapAction.startsWith("\"")){
						throw new Exception("Header http '"+SOAPVersion.SOAP11_MANDATORY_HEADER_HTTP_SOAP_ACTION+"' non valorizzato correttamente (action quotata? Non è stato trovato il carattere di apertura \" ma è presente quello di chiusura)");
					}	
				}
				return soapAction;
			}
		
			else{
				// The SOAP 1.1 mandatory SOAPAction HTTP header has been removed in SOAP 1.2. In its place is an optional action parameter on the application/soap+xml media type.
				return soapAction;
			}
			
		}catch(Exception e){
			throw new ConnectorException(e.getMessage(),e);
		}
	}
	
	@Override
	public OpenSPCoop2MessageParseResult getRequest(NotifierInputStreamParams notifierInputStreamParams, String contentType) throws ConnectorException{
		try{
			OpenSPCoop2MessageParseResult pr = new OpenSPCoop2MessageParseResult();
			pr.setMessage(this.message);
			return pr;
		}catch(Exception e){
			throw new ConnectorException(e.getMessage(),e);
		}	
	}
	
	@Override
	public byte[] getRequest() throws ConnectorException{
		try{
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			this.message.writeTo(bout, true);
			bout.flush();
			bout.close();
			return bout.toByteArray();
		}catch(Exception e){
			throw new ConnectorException(e.getMessage(),e);
		}
	}
	
	@Override
	public URLProtocolContext getURLProtocolContext() throws ConnectorException{
		try{
			URLProtocolContext urlProtocolContext = new URLProtocolContext();
			
			Properties pFormBased = new Properties();
			pFormBased.putAll(this.parameters);
			urlProtocolContext.setParametersFormBased(pFormBased);
			
			Properties pTrasporto = new Properties();
			pTrasporto.putAll(this.headers);
			urlProtocolContext.setParametersFormBased(pTrasporto);
			
			urlProtocolContext.setFunction(this.function);
			
			urlProtocolContext.setProtocol(this.protocolFactory.getManifest().getWeb().getContextList().get(0));
			
			urlProtocolContext.setRequestURI(this.url);
			
			urlProtocolContext.setWebContext("/openspcoop2");
			
			urlProtocolContext.setFunctionParameters(this.functionParameters);
			
			return urlProtocolContext;
		}catch(Exception e){
			throw new ConnectorException(e.getMessage(),e);
		}
	}
	
	@Override
	public Credenziali getCredenziali() throws ConnectorException{
		return this.credenziali;
	}
	
	@Override
	public String getLocation(Credenziali credenziali) throws ConnectorException{
		return "DirectVM_"+this.function;
	}
	
	@Override
	public String getProtocol() throws ConnectorException{
		return "DirectVM";
	}
	
	@Override
	public int getContentLength() throws ConnectorException{
		return -1; // devo gestire il chunked
	}
	
	@Override
	public void close() throws ConnectorException{
		// nop
	}
	
	@Override
	public Identity getIdentity() throws ConnectorException{
		if(this.credenziali!=null){
			Identity identity = new Identity();
			identity.setUsername(this.credenziali.getUsername());
			identity.setPassword(this.credenziali.getPassword());
			identity.setSubject(this.credenziali.getSubject());
			return identity;
		}
		return null;
	}
}
