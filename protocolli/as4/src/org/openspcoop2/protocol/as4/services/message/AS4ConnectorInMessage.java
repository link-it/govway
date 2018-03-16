/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
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
package org.openspcoop2.protocol.as4.services.message;

import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;

import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.UserMessage;
import org.openspcoop2.core.constants.Costanti;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.OpenSPCoop2MessageParseResult;
import org.openspcoop2.message.constants.MessageRole;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.credenziali.Credenziali;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.pdd.services.connector.ConnectorException;
import org.openspcoop2.pdd.services.connector.ConnectorUtils;
import org.openspcoop2.pdd.services.connector.RicezioneBusteConnector;
import org.openspcoop2.pdd.services.connector.messages.ConnectorInMessage;
import org.openspcoop2.pdd.services.core.RicezioneBuste;
import org.openspcoop2.protocol.as4.config.AS4Properties;
import org.openspcoop2.protocol.as4.constants.AS4Costanti;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.engine.RequestInfo;
import org.openspcoop2.protocol.engine.URLProtocolContext;
import org.openspcoop2.protocol.engine.constants.IDService;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.io.notifier.NotifierInputStreamParams;
import org.openspcoop2.utils.transport.Credential;
import org.slf4j.Logger;

/**
 * DirectVMConnectorInMessage
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AS4ConnectorInMessage implements ConnectorInMessage {

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
	private PdDContext pddContext;
	private Date dataIngressoRichiesta;
	
	public AS4ConnectorInMessage(UserMessage userMessage,HashMap<String, byte[]> content) throws ConnectorException{
		try{
			this.message = OpenSPCoop2MessageFactory.getMessageFactory().createEmptyMessage(MessageType.SOAP_12, MessageRole.REQUEST);
			this.message.addContextProperty(AS4Costanti.AS4_CONTEXT_USER_MESSAGE, userMessage);
			this.message.addContextProperty(AS4Costanti.AS4_CONTEXT_CONTENT, content);
			
			this.log = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();
			if(this.log==null)
				this.log = LoggerWrapperFactory.getLogger(AS4ConnectorInMessage.class);
			
			this.idModuloAsIDService = RicezioneBusteConnector.ID_SERVICE;
			this.idModulo = RicezioneBuste.ID_MODULO+"_JmsDomibus";
			
			this.protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(AS4Costanti.PROTOCOL_NAME);
			
			this.function = URLProtocolContext.PA_FUNCTION;
			this.url = "/as4/"+this.function;
			
			this.credenziali = null;
			
			this.functionParameters = null;
			
			this.pddContext = new PdDContext();
			
						
			URLProtocolContext urlProtocolContext = new URLProtocolContext();
			
			Properties pFormBased = new Properties();
			pFormBased.putAll(this.parameters);
			urlProtocolContext.setParametersFormBased(pFormBased);
			
			Properties pTrasporto = new Properties();
			pTrasporto.putAll(this.headers);
			urlProtocolContext.setParametersFormBased(pTrasporto);
			
			urlProtocolContext.setFunction(this.function);
			
			urlProtocolContext.setProtocol(this.protocolFactory.getProtocol(),
					this.protocolFactory.getManifest().getWeb().getContextList().get(0).getName());
			
			urlProtocolContext.setRequestURI(this.url);
			
			urlProtocolContext.setWebContext("/openspcoop2");
			
			urlProtocolContext.setFunctionParameters(this.functionParameters);
			
			urlProtocolContext.setSource("domibus/jmsQueue/"+AS4Properties.getInstance().getDomibusGatewayJMS_queueReceiver());
			
			this.requestInfo = ConnectorUtils.getRequestInfo(this.protocolFactory, urlProtocolContext);
			
			if(this.pddContext!=null){
				this.setAttribute(Costanti.REQUEST_INFO,this.requestInfo);
			}
			
			this.message.setTransportRequestContext(urlProtocolContext);
			
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
		if(this.pddContext!=null){
			this.setAttribute(Costanti.REQUEST_INFO,this.requestInfo);
		}
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
			this.dataIngressoRichiesta = DateManager.getDate();
			return pr;
		}catch(Exception e){
			throw new ConnectorException(e.getMessage(),e);
		}	
	}
	
	@Override
	public byte[] getRequest() throws ConnectorException{
		try{
			this.dataIngressoRichiesta = DateManager.getDate();
			return null;
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
		return "DomibusJMS_"+this.function;
	}
	
	@Override
	public String getProtocol() throws ConnectorException{
		return "DomibusJMS";
	}
	
	@Override
	public int getContentLength() throws ConnectorException{
		return -1; // devo gestire il chunked
	}
	
	@Override
	public String getRemoteAddress() throws ConnectorException{
		return "DomibusJMS";
	}
	
	@Override
	public void close() throws ConnectorException{
		// nop
	}
	
}
