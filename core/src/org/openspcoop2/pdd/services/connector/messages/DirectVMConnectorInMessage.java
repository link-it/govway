/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it). 
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
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.openspcoop2.core.constants.Costanti;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageParseResult;
import org.openspcoop2.message.config.ServiceBindingConfiguration;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.credenziali.Credenziali;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.pdd.services.DirectVMProtocolInfo;
import org.openspcoop2.pdd.services.connector.ConnectorException;
import org.openspcoop2.pdd.services.connector.ConnectorUtils;
import org.openspcoop2.protocol.engine.RequestInfo;
import org.openspcoop2.protocol.engine.URLProtocolContext;
import org.openspcoop2.protocol.engine.constants.IDService;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.io.notifier.NotifierInputStreamParams;
import org.openspcoop2.utils.transport.Credential;
import org.openspcoop2.utils.transport.TransportUtils;
import org.slf4j.Logger;

/**
 * DirectVMConnectorInMessage
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DirectVMConnectorInMessage implements ConnectorInMessage {

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
	private PdDContext pddContext;
	private Date dataIngressoRichiesta;
	
	public DirectVMConnectorInMessage(OpenSPCoop2Message msg,IDService idModuloAsIDService, String idModulo,
			Map<String, List<String>> trasporto,
			Map<String, List<String>> formUrl,
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
			
			if(trasporto!=null && !trasporto.isEmpty()) {
				Iterator<String> keys = trasporto.keySet().iterator();
				while (keys.hasNext()) {
					String key = (String) keys.next();
					this.headers.put(key, trasporto.get(key));
				}
			}
			
			if(formUrl!=null && !formUrl.isEmpty()) {
				Iterator<String> keys = formUrl.keySet().iterator();
				while (keys.hasNext()) {
					String key = (String) keys.next();
					this.parameters.put(key, formUrl.get(key));
				}
			}
			
			this.protocolFactory = protocolFactory;
			
			this.url = url;
			this.function = function;
			
			this.credenziali = credenziali;
			
			this.functionParameters = functionParameters;
			
			this.directVMProtocolInfo = directVMProtocolInfo;
			
			this.pddContext = pddContext;
			
			if(this.pddContext!=null){
				this.setAttribute(CostantiPdD.OPENSPCOOP2_PDD_CONTEXT_HEADER_HTTP,this.pddContext);
			}
						
			URLProtocolContext urlProtocolContext = new URLProtocolContext();
			
			Map<String, List<String>> pFormBased = new HashMap<String, List<String>>();
			pFormBased.putAll(this.parameters);
			urlProtocolContext.setParameters(pFormBased);
			
			Map<String, List<String>> pTrasporto = new HashMap<String, List<String>>();
			pTrasporto.putAll(this.headers);
			urlProtocolContext.setHeaders(pTrasporto);
			
			urlProtocolContext.setFunction(this.function);
			
			urlProtocolContext.setProtocol(this.protocolFactory.getProtocol(),
					this.protocolFactory.getManifest().getWeb().getContextList().get(0).getName());
			
			urlProtocolContext.setRequestURI(this.url);
			
			urlProtocolContext.setWebContext("/openspcoop2");
			
			urlProtocolContext.setFunctionParameters(this.functionParameters);
			
			this.requestInfo = ConnectorUtils.getRequestInfo(this.protocolFactory, urlProtocolContext);
			
			if(this.pddContext!=null){
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
	public void updateRequestInfo(RequestInfo requestInfo) throws ConnectorException{
		this.requestInfo = requestInfo;
		if(this.pddContext!=null){
			this.setAttribute(Costanti.REQUEST_INFO,this.requestInfo);
		}
	}
	@Override
	public RequestInfo getRequestInfo(){
		return this.requestInfo;
	}
	
	private Map<String, Object> attributes = new HashMap<String, Object>();
	public void setAttribute(String key, Object object) throws ConnectorException {
		this.attributes.put(key, object);
	}
	@Override
	public Object getAttribute(String key) throws ConnectorException {
		return this.attributes.get(key);
	}
	
	
	private Map<String, List<String>> headers = new HashMap<String, List<String>>();
	public void addHeader(String key, String value) throws ConnectorException {
		TransportUtils.addHeader(this.headers, key, value);
	}
	@Override
	public List<String> getHeaderValues(String key) throws ConnectorException{
		return TransportUtils.getRawObject(this.headers, key);
	}
	
	
	private Map<String, List<String>> parameters = new HashMap<String, List<String>>();
	public void setParameter(String key, String value) throws ConnectorException {
		TransportUtils.addParameter(this.parameters, key, value);
	}
	@Override
	public List<String> getParameterValues(String key) throws ConnectorException{
		return TransportUtils.getRawObject(this.parameters, key);
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
			this.dataIngressoRichiesta = DateManager.getDate();
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
			byte[] b = bout.toByteArray();
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
	public String getRemoteAddress() throws ConnectorException{
		return "DirectVM";
	}
	
	@Override
	public void close() throws ConnectorException{
		// nop
	}
	
}
