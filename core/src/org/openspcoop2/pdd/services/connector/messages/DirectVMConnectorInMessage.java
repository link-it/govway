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
package org.openspcoop2.pdd.services.connector.messages;

import java.io.ByteArrayOutputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;

import org.openspcoop2.core.constants.Costanti;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.OpenSPCoop2MessageParseResult;
import org.openspcoop2.message.config.ServiceBindingConfiguration;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.autenticazione.Credenziali;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.pdd.services.DirectVMProtocolInfo;
import org.openspcoop2.pdd.services.RequestInfo;
import org.openspcoop2.pdd.services.connector.ConnectorException;
import org.openspcoop2.pdd.services.connector.ConnectorUtils;
import org.openspcoop2.protocol.engine.URLProtocolContext;
import org.openspcoop2.protocol.engine.constants.IDService;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.io.notifier.NotifierInputStreamParams;
import org.openspcoop2.utils.transport.Credential;
import org.slf4j.Logger;

/**
 * DirectVMConnectorInMessage
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author: apoli $
 * @version $Rev: 12237 $, $Date: 2016-10-04 11:41:45 +0200 (Tue, 04 Oct 2016) $
 */
public class DirectVMConnectorInMessage implements ConnectorInMessage {

	public static OpenSPCoop2MessageFactory factory = OpenSPCoop2MessageFactory.getMessageFactory();
	
	private OpenSPCoop2Message message;
	private Logger log;
	private String idModulo;
	private IDService idModuloAsIDService;
	private RequestInfo requestInfo;
	private IProtocolFactory<?> protocolFactory;
	private String function;
	private String url;
	private Credenziali credenziali;
	private String functionParameters;
	private DirectVMProtocolInfo directVMProtocolInfo;
	
	public DirectVMConnectorInMessage(OpenSPCoop2Message msg,IDService idModuloAsIDService, String idModulo,
			Properties trasporto,
			Properties formUrl,
			IProtocolFactory<?> protocolFactory,
			String function, String url,
			Credenziali credenziali,
			String functionParameters,
			ServiceBindingConfiguration serviceBindingConfiguration,
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
						
			URLProtocolContext urlProtocolContext = new URLProtocolContext();
			
			Properties pFormBased = new Properties();
			pFormBased.putAll(this.parameters);
			urlProtocolContext.setParametersFormBased(pFormBased);
			
			Properties pTrasporto = new Properties();
			pTrasporto.putAll(this.headers);
			urlProtocolContext.setParametersFormBased(pTrasporto);
			
			urlProtocolContext.setFunction(this.function);
			
			urlProtocolContext.setProtocol(this.protocolFactory.getManifest().getWeb().getContextList().get(0).getName());
			
			urlProtocolContext.setRequestURI(this.url);
			
			urlProtocolContext.setWebContext("/openspcoop2");
			
			urlProtocolContext.setFunctionParameters(this.functionParameters);
			
			this.requestInfo = ConnectorUtils.getRequestInfo(this.protocolFactory, urlProtocolContext);
			
			if(pddContext!=null){
				this.setAttribute(Costanti.REQUEST_INFO,this.requestInfo);
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
	
	@Override
	public RequestInfo getRequestInfo(){
		return this.requestInfo;
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
	public IProtocolFactory<?> getProtocolFactory() throws ConnectorException{
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
	public String getSOAPAction() throws ConnectorException{
		try{
			return this.message.castAsSoap().getSoapAction();
		}catch(Exception e){
			throw new ConnectorException(e.getMessage(),e);
		}
	}
	
	@Override
	public OpenSPCoop2MessageParseResult getRequest(NotifierInputStreamParams notifierInputStreamParams) throws ConnectorException{
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
			return this.requestInfo.getProtocolContext();
		}catch(Exception e){
			throw new ConnectorException(e.getMessage(),e);
		}
	}
	
	@Override
	public Credential getCredential() throws ConnectorException{
		return this.credenziali;
	}
	
	@Override
	public String getSource() throws ConnectorException{
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
	
}
