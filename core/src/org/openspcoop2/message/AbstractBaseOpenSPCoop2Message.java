/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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

package org.openspcoop2.message;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.soap.SOAPBody;

import org.openspcoop2.message.constants.Costanti;
import org.openspcoop2.message.constants.MessageRole;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.message.context.ContentLength;
import org.openspcoop2.message.context.ContentTypeParameters;
import org.openspcoop2.message.context.Credentials;
import org.openspcoop2.message.context.ForcedResponse;
import org.openspcoop2.message.context.HeaderParameters;
import org.openspcoop2.message.context.MessageContext;
import org.openspcoop2.message.context.SerializedContext;
import org.openspcoop2.message.context.SerializedParameter;
import org.openspcoop2.message.context.StringParameter;
import org.openspcoop2.message.context.UrlParameters;
import org.openspcoop2.message.exception.MessageException;
import org.openspcoop2.message.exception.MessageNotSupportedException;
import org.openspcoop2.message.exception.ParseException;
import org.openspcoop2.message.exception.ParseExceptionUtils;
import org.openspcoop2.message.soap.SoapUtils;
import org.openspcoop2.message.soap.reader.OpenSPCoop2MessageSoapStreamReader;
import org.openspcoop2.message.utils.TransportUtilities;
import org.openspcoop2.message.xml.DynamicNamespaceContextFactory;
import org.openspcoop2.message.xml.XMLUtils;
import org.openspcoop2.utils.beans.WriteToSerializerType;
import org.openspcoop2.utils.io.notifier.NotifierInputStream;
import org.openspcoop2.utils.serialization.JavaDeserializer;
import org.openspcoop2.utils.serialization.JavaSerializer;
import org.openspcoop2.utils.transport.Credential;
import org.openspcoop2.utils.transport.TransportRequestContext;
import org.openspcoop2.utils.transport.TransportResponseContext;
import org.openspcoop2.utils.transport.TransportUtils;
import org.openspcoop2.utils.xml.DynamicNamespaceContext;
import org.w3c.dom.Node;

/**
 * AbstractBaseOpenSPCoop2Message
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class AbstractBaseOpenSPCoop2Message implements org.openspcoop2.message.OpenSPCoop2Message {

	/* Message Factory */
	protected OpenSPCoop2MessageFactory messageFactory;
	
	/* Trasporto */	
	public TransportRequestContext transportRequestContext;
	public TransportResponseContext transportResponseContext;	
	public Map<String, List<String>> forceTransportHeaders = new HashMap<>();
	public Map<String, List<String>> forceUrlProperties = new HashMap<>();
	public OpenSPCoop2MessageProperties forwardTransportHeader = new OpenSPCoop2MessageProperties();
	public OpenSPCoop2MessageProperties forwardUrlProperties = new OpenSPCoop2MessageProperties();
		
	/* Forced Response */
	public String forcedResponseCode;
	public boolean forcedEmptyResponse;
	public ForcedResponseMessage forcedResponse;
	
	/* Context */	
	public Map<String, Object> context = new HashMap<String, Object>();
	private String transactionId;
	
	/* ContentType */
	public Map<String, String> contentTypeParamaters = new HashMap<String, String>();
	
	/* MessageType */
	public MessageType messageType;
	
	/* MessageRole */
	public MessageRole messageRole;
	
	/* Content Length */
	public long outgoingsize = -1;
	public long incomingsize = -1;
	public Long incomingSizeForced = null;
	
	/* Errors */
	public ParseException parseException;
	
	/* Protocol Plugin */
	public String protocolName;
	
	/* Stream */
	public NotifierInputStream notifierInputStream;
	
	/* Indicazione se la normalizzazione dei namespace per gli attributi xsi:type deve essere effettuata */
	public boolean normalizeNamespaceXSIType = false;
	

	public AbstractBaseOpenSPCoop2Message(OpenSPCoop2MessageFactory messageFactory) {
		this.messageFactory = messageFactory;
	}
	
	
	/* Message Factory */
	
	@Override
	public OpenSPCoop2MessageFactory getFactory() {
		return this.messageFactory;
	}
	
	
	
	/* Normalize to SAAJ */
	
	@Override
	public OpenSPCoop2Message normalizeToSaajImpl() throws MessageException{
		return this;
	}
	
	
	/* Copy Resources to another instance */
	
	@Override
	public void copyResourcesTo(OpenSPCoop2Message newInstance) throws MessageException{
		this.copyResourcesTo(newInstance, false);
	}
	
	@Override
	public void copyResourcesTo(OpenSPCoop2Message newInstance,  boolean skipTransportInfo) throws MessageException{
		
		// Le seguenti risorse non vengono ricopiate poichè varieranno nella nuova versione
		// - contentTypeParamaters
		// - messageType
		// - outgoingsize
		// - notifierInputStream
		
		if(this.parseException!=null){
			throw new MessageException(this.getParseException().getSourceException());
		}
		
		if(newInstance instanceof AbstractBaseOpenSPCoop2Message){
			AbstractBaseOpenSPCoop2Message base = (AbstractBaseOpenSPCoop2Message) newInstance; 
			if(!skipTransportInfo) {
				base.transportRequestContext = this.transportRequestContext;
				base.transportResponseContext = this.transportResponseContext;
				base.forceTransportHeaders = this.forceTransportHeaders;
				base.forceUrlProperties = this.forceUrlProperties;
				base.forwardTransportHeader = this.forwardTransportHeader;
				base.forwardUrlProperties = this.forwardUrlProperties;
				base.forcedResponseCode = this.forcedResponseCode;
				base.forcedEmptyResponse = this.forcedEmptyResponse;
				base.forcedResponse = this.forcedResponse;
			}
			base.context = this.context;
			base.transactionId = this.transactionId;
			base.messageRole = this.messageRole;
			if(!skipTransportInfo) {
				base.incomingsize = this.incomingsize;
				base.incomingSizeForced = this.incomingSizeForced;
			}
			base.protocolName = this.protocolName;
		}
		else{
			// Viene riversato solo quello che è possibile riversare
			if(!skipTransportInfo) {
				newInstance.setTransportRequestContext(this.transportRequestContext);
				newInstance.setTransportResponseContext(this.transportResponseContext);
				
				if(this.forceTransportHeaders!=null && !this.forceTransportHeaders.isEmpty()) {
					for (String key : this.forceTransportHeaders.keySet()) {
						List<String> values = this.forceTransportHeaders.get(key);
						newInstance.forceTransportHeader(key, values);
					}
				}
				if(this.forceUrlProperties!=null && !this.forceUrlProperties.isEmpty()) {
					for (String key : this.forceUrlProperties.keySet()) {
						List<String> values = this.forceUrlProperties.get(key);
						newInstance.forceUrlProperty(key, values);
					}
				}
				
				newInstance.setForcedResponseCode(this.forcedResponseCode);
				if(this.forcedEmptyResponse) {
					newInstance.forceEmptyResponse();
				}
				if(this.forcedResponse!=null) {
					newInstance.forceResponse(this.forcedResponse);
				}
				newInstance.setForcedResponseCode(this.forcedResponseCode);
			}
			if(this.context.size()>0){
				Iterator<String> it = this.context.keySet().iterator();
				while (it.hasNext()) {
					String contextKey = (String) it.next();
					newInstance.addContextProperty(contextKey, this.context.get(contextKey));
				}
			}	
			newInstance.setTransactionId(this.transactionId);
			newInstance.setMessageRole(this.messageRole);
			newInstance.setProtocolName(this.protocolName);
		}
	}
	
	@Override
	public MessageContext serializeResourcesTo() throws MessageException{
		try {
			MessageContext msgContext = new MessageContext();
			
			/* Trasporto */	
			if(this.transportRequestContext!=null) {
				org.openspcoop2.message.context.TransportRequestContext ctx = new org.openspcoop2.message.context.TransportRequestContext();
				
				if(this.transportRequestContext.getParameters()!=null && !this.transportRequestContext.getParameters().isEmpty()) {
					List<StringParameter> l = this._msgContext_convertTo(this.transportRequestContext.getParameters());
					if(l!=null && l.size()>0) {
						UrlParameters urlParameters = new UrlParameters();
						urlParameters.getUrlParameterList().addAll(l);
						ctx.setUrlParameters(urlParameters);
					}
				}			
				if(this.transportRequestContext.getHeaders()!=null && !this.transportRequestContext.getHeaders().isEmpty()) {
					List<StringParameter> l = this._msgContext_convertTo(this.transportRequestContext.getHeaders());
					if(l!=null && l.size()>0) {
						HeaderParameters headerParameters = new HeaderParameters();
						headerParameters.getHeaderParameterList().addAll(l);
						ctx.setHeaderParameters(headerParameters);
					}
				}
				if(this.transportRequestContext.getCredential()!=null) {
					Credentials credentials = new Credentials();
					credentials.setPrincipal(this.transportRequestContext.getCredential().getPrincipal());
					credentials.setSubject(this.transportRequestContext.getCredential().getSubject());
					credentials.setUsername(this.transportRequestContext.getCredential().getUsername());
					credentials.setPassword(this.transportRequestContext.getCredential().getPassword());
					ctx.setCredentials(credentials);
				}
				ctx.setWebContext(this.transportRequestContext.getWebContext());
				ctx.setRequestUri(this.transportRequestContext.getRequestURI());
				ctx.setRequestType(this.transportRequestContext.getRequestType());
				ctx.setSource(this.transportRequestContext.getSource());
				ctx.setProtocolName(this.transportRequestContext.getProtocolName());
				ctx.setProtocolWebContext(this.transportRequestContext.getProtocolWebContext());
				ctx.setFunction(this.transportRequestContext.getFunction());
				ctx.setFunctionParameters(this.transportRequestContext.getFunctionParameters());
				ctx.setInterfaceName(this.transportRequestContext.getInterfaceName());

				msgContext.setTransportRequestContext(ctx);
			}
			if(this.transportResponseContext!=null) {
				org.openspcoop2.message.context.TransportResponseContext ctx = new org.openspcoop2.message.context.TransportResponseContext();
				
				if(this.transportResponseContext.getHeaders()!=null && !this.transportResponseContext.getHeaders().isEmpty()) {
					List<StringParameter> l = this._msgContext_convertTo(this.transportResponseContext.getHeaders());
					if(l!=null && l.size()>0) {
						HeaderParameters headerParameters = new HeaderParameters();
						headerParameters.getHeaderParameterList().addAll(l);
						ctx.setHeaderParameters(headerParameters);
					}
				}
				ctx.setTransportCode(this.transportResponseContext.getCodiceTrasporto());
				if(this.transportResponseContext.getContentLength()>=0) {
					ctx.setContentLength(this.transportResponseContext.getContentLength());
				}
				ctx.setError(this.transportResponseContext.getErrore());
				
				msgContext.setTransportResponseContext(ctx);
			}
			
			/* Forced Response */
			if(this.forcedResponseCode!=null || this.forcedEmptyResponse || this.forcedResponse!=null) {
				ForcedResponse forcedResponse = new ForcedResponse();
				forcedResponse.setResponseCode(this.forcedResponseCode);
				forcedResponse.setEmptyResponse(this.forcedEmptyResponse);
				if(this.forcedResponse!=null) {
					org.openspcoop2.message.context.ForcedResponseMessage forcedResponseMessage = new org.openspcoop2.message.context.ForcedResponseMessage();
					forcedResponseMessage.setContent(this.forcedResponse.getContent());
					forcedResponseMessage.setContentType(this.forcedResponse.getContentType());
					forcedResponseMessage.setResponseCode(this.forcedResponse.getResponseCode());
					if(this.forcedResponse.getHeadersValues()!=null && !this.forcedResponse.getHeadersValues().isEmpty()) {
						List<StringParameter> l = this._msgContext_convertTo(this.forcedResponse.getHeadersValues());
						if(l!=null && l.size()>0) {
							HeaderParameters headerParameters = new HeaderParameters();
							headerParameters.getHeaderParameterList().addAll(l);
							forcedResponseMessage.setHeaderParameters(headerParameters);
						}
					}
					forcedResponse.setResponseMessage(forcedResponseMessage);
				}
				msgContext.setForcedResponse(forcedResponse);
			}
						
			/* Context */	
			if(this.context!=null && this.context.size()>0) {
				SerializedContext serializedContext = null;
				Iterator<String> it = this.context.keySet().iterator();
				JavaSerializer jSerializer = new JavaSerializer();
				while (it.hasNext()) {
					String key = (String) it.next();
					Object o = this.context.get(key);
					if(o instanceof Serializable) {
						if(serializedContext==null) {
							serializedContext = new SerializedContext();
						}
						SerializedParameter p = new SerializedParameter();
						p.setNome(key);
						p.setClasse(o.getClass().getName());
						ByteArrayOutputStream bout = new ByteArrayOutputStream();
						jSerializer.writeObject(o, bout);
						bout.flush();
						bout.close();
						p.setBase(bout.toByteArray());
						serializedContext.addProperty(p);
					}
				}
				msgContext.setSerializedContext(serializedContext);
			}
	
			/* ContentType */
			if(this.contentTypeParamaters!=null && this.contentTypeParamaters.size()>0) {
				ContentTypeParameters contentTypeParameters = new ContentTypeParameters();
				Iterator<String> it = this.contentTypeParamaters.keySet().iterator();
				while (it.hasNext()) {
					String key = (String) it.next();
					String value = this.contentTypeParamaters.get(key);
					StringParameter parameter = new StringParameter();
					parameter.setNome(key);
					parameter.setBase(value);
					contentTypeParameters.addParameter(parameter);
				}		
				msgContext.setContentTypeParameters(contentTypeParameters);
			}
			
			/* MessageType */
			msgContext.setMessageType(this.messageType.name());
			
			/* MessageRole */
			msgContext.setMessageRole(this.messageRole.name());
						
			/* Content Length */
			if(this.outgoingsize >=0 || this.incomingsize >=0 || this.incomingSizeForced!=null) {
				ContentLength contentLength = new ContentLength();
				if(this.outgoingsize >=0) {
					contentLength.setOutgoingSize(this.outgoingsize);
				}
				if(this.incomingsize >=0) {
					contentLength.setIncomingSize(this.incomingsize);
				}
				if(this.incomingSizeForced!=null) {
					contentLength.setIncomingSizeForced(this.incomingSizeForced);
				}
				msgContext.setContentLength(contentLength);
			}
			
			/* Errors */
			// non serializzabile
			
			/* Protocol Plugin */
			msgContext.setProtocol(this.protocolName);
			
			/* Stream */
			// non serializzabile
				
			return msgContext;
			
		}catch(Exception e) {
			throw new MessageException(e.getMessage(),e);
		}		
	}
	protected List<StringParameter> _msgContext_convertTo(Map<String, List<String>> p){
		List<StringParameter> l = new ArrayList<>();
		if(p.size()>0) {
			Iterator<String> keys = p.keySet().iterator();
			while (keys.hasNext()) {
				String key = (String) keys.next();
				List<String> values = p.get(key);
				if(values!=null && !values.isEmpty()) {
					for (String value : values) {
						StringParameter sp = new StringParameter();
						sp.setBase(value);
						sp.setNome(key);
						l.add(sp);		
					}
				}
			}
		}
		return l;
	}
	
	@Override
	public void serializeResourcesTo(OutputStream os) throws MessageException{
		try {
			MessageContext messageContext = this.serializeResourcesTo();
			messageContext.writeTo(os, WriteToSerializerType.XML_JAXB);
		}catch(Exception e) {
			throw new MessageException(e.getMessage(),e);
		}	
	}
	
	@Override
	public void readResourcesFrom(MessageContext messageContext) throws MessageException{
		
		try {
		
			/* Trasporto */	
			if(messageContext.getTransportRequestContext()!=null) {
				this.transportRequestContext = new TransportRequestContext();
				
				if(messageContext.getTransportRequestContext().getUrlParameters()!=null &&
						messageContext.getTransportRequestContext().getUrlParameters().sizeUrlParameterList()>0) {
					Map<String, List<String>> p = this._msgContext_convertTo(messageContext.getTransportRequestContext().getUrlParameters().getUrlParameterList());
					if(p!=null && p.size()>0) {
						this.transportRequestContext.setParameters(p);
					}	
				}
				if(messageContext.getTransportRequestContext().getHeaderParameters()!=null &&
						messageContext.getTransportRequestContext().getHeaderParameters().sizeHeaderParameterList()>0) {
					Map<String, List<String>> p = this._msgContext_convertTo(messageContext.getTransportRequestContext().getHeaderParameters().getHeaderParameterList());
					if(p!=null && p.size()>0) {
						this.transportRequestContext.setHeaders(p);
					}	
				}
				if(messageContext.getTransportRequestContext().getCredentials()!=null) {
					Credential credentials = new Credential();
					credentials.setPrincipal(messageContext.getTransportRequestContext().getCredentials().getPrincipal());
					credentials.setSubject(messageContext.getTransportRequestContext().getCredentials().getSubject());
					credentials.setUsername(messageContext.getTransportRequestContext().getCredentials().getUsername());
					credentials.setPassword(messageContext.getTransportRequestContext().getCredentials().getPassword());
					this.transportRequestContext.setCredentials(credentials);
				}
				if(messageContext.getTransportRequestContext().getWebContext()!=null) {
					this.transportRequestContext.setWebContext(messageContext.getTransportRequestContext().getWebContext());
				}
				if(messageContext.getTransportRequestContext().getRequestUri()!=null) {
					this.transportRequestContext.setRequestURI(messageContext.getTransportRequestContext().getRequestUri());
				}
				if(messageContext.getTransportRequestContext().getRequestType()!=null) {
					this.transportRequestContext.setRequestType(messageContext.getTransportRequestContext().getRequestType());
				}
				if(messageContext.getTransportRequestContext().getSource()!=null) {
					this.transportRequestContext.setSource(messageContext.getTransportRequestContext().getSource());
				}
				if(messageContext.getTransportRequestContext().getProtocolName()!=null) {
					this.transportRequestContext.setProtocol(messageContext.getTransportRequestContext().getProtocolName(),
							messageContext.getTransportRequestContext().getProtocolWebContext());
				}
				if(messageContext.getTransportRequestContext().getFunction()!=null) {
					this.transportRequestContext.setFunction(messageContext.getTransportRequestContext().getFunction());
				}
				if(messageContext.getTransportRequestContext().getFunctionParameters()!=null) {
					this.transportRequestContext.setFunctionParameters(messageContext.getTransportRequestContext().getFunctionParameters());
				}
				if(messageContext.getTransportRequestContext().getInterfaceName()!=null) {
					this.transportRequestContext.setInterfaceName(messageContext.getTransportRequestContext().getInterfaceName());
				}
				
			}
			if(messageContext.getTransportResponseContext()!=null) {
				this.transportResponseContext = new TransportResponseContext();
				
				if(messageContext.getTransportResponseContext().getHeaderParameters()!=null &&
						messageContext.getTransportResponseContext().getHeaderParameters().sizeHeaderParameterList()>0) {
					Map<String, List<String>> p = this._msgContext_convertTo(messageContext.getTransportResponseContext().getHeaderParameters().getHeaderParameterList());
					if(p!=null && p.size()>0) {
						this.transportResponseContext.setHeaders(p);
					}	
				}
				this.transportResponseContext.setCodiceTrasporto(messageContext.getTransportResponseContext().getTransportCode());
				if(messageContext.getTransportResponseContext().getContentLength()!=null) {
					this.transportResponseContext.setContentLength(messageContext.getTransportResponseContext().getContentLength());
				}
				this.transportResponseContext.setErrore(messageContext.getTransportResponseContext().getError());
			}
				
			/* Forced Response */
			if(messageContext.getForcedResponse()!=null) {
				this.forcedResponseCode = messageContext.getForcedResponse().getResponseCode();
				this.forcedEmptyResponse = messageContext.getForcedResponse().isEmptyResponse();
				if(messageContext.getForcedResponse().getResponseMessage()!=null) {
					ForcedResponseMessage f = new ForcedResponseMessage();
					f.setContent(messageContext.getForcedResponse().getResponseMessage().getContent());
					f.setContentType(messageContext.getForcedResponse().getResponseMessage().getContentType());
					f.setResponseCode(messageContext.getForcedResponse().getResponseMessage().getResponseCode());
					if(messageContext.getForcedResponse().getResponseMessage().getHeaderParameters()!=null &&
							messageContext.getForcedResponse().getResponseMessage().getHeaderParameters().sizeHeaderParameterList()>0) {
						Map<String, List<String>> p = this._msgContext_convertTo(messageContext.getForcedResponse().getResponseMessage().getHeaderParameters().getHeaderParameterList());
						if(p!=null && p.size()>0) {
							f.setHeadersValues(p);
						}	
					}
					this.forcedResponse = f;
				}
			}
			
			/* Context */	
			if(messageContext.getSerializedContext()!=null) {
				JavaDeserializer jDeserializer = new JavaDeserializer();
				for (SerializedParameter p : messageContext.getSerializedContext().getPropertyList()) {
					Object o = jDeserializer.readObject(new ByteArrayInputStream(p.getBase()), Class.forName(p.getClasse()));
					this.context.put(p.getNome(), o);
				}
			}
			
			/* ContentType */
			if(messageContext.getContentTypeParameters()!=null &&
					messageContext.getContentTypeParameters().sizeParameterList()>0){
				for (StringParameter par : messageContext.getContentTypeParameters().getParameterList()) {
					this.contentTypeParamaters.put(par.getNome(), par.getBase());
				}
			}
			
			/* MessageType */
			MessageType mt = MessageType.valueOf(messageContext.getMessageType());
			if(mt==null) {
				throw new Exception("MessageType ["+messageContext.getMessageType()+"] unknown");
			}
			if(mt.equals(this.messageType)==false) {
				throw new Exception("MessageType ["+mt+"] different from instance value ["+this.messageType+"]");
			}
			
			/* MessageRole */
			MessageRole mr = MessageRole.valueOf(messageContext.getMessageRole());
			if(mr==null) {
				throw new Exception("MessageRole ["+messageContext.getMessageRole()+"] unknown");
			}
			if(mr.equals(this.messageRole)==false) {
				throw new Exception("MessageRole ["+mr+"] different from instance value ["+this.messageRole+"]");
			}
			
			/* Content Length */
			if(messageContext.getContentLength()!=null) {
				if(messageContext.getContentLength().getOutgoingSize()!=null) {
					this.outgoingsize = messageContext.getContentLength().getOutgoingSize();
				}
				if(messageContext.getContentLength().getIncomingSize()!=null) {
					this.incomingsize = messageContext.getContentLength().getIncomingSize();
				}
				if(messageContext.getContentLength().getIncomingSizeForced()!=null) {
					this.incomingSizeForced = messageContext.getContentLength().getIncomingSizeForced();
				}
			}
			
			/* Errors */
			// non serializzabile
			
			/* Protocol Plugin */
			if(messageContext.getProtocol()!=null) {
				this.protocolName = messageContext.getProtocol();
			}
			
			/* Stream */
			// non serializzabile
			
		}catch(Exception e) {
			throw new MessageException(e.getMessage(),e);
		}	
	}
	protected Map<String, List<String>> _msgContext_convertTo(List<StringParameter> list){
		Map<String, List<String>> p = new HashMap<String, List<String>>();
		if(list.size()>0) {
			for (StringParameter stringParameter : list) {
				TransportUtils.put(p, stringParameter.getNome(), stringParameter.getBase(), true);
			}
		}
		return p;
	}
	
	@Override
	public void readResourcesFrom(InputStream is) throws MessageException{
		MessageContext messageContext = null;
		try {
			org.openspcoop2.message.context.utils.serializer.JaxbDeserializer deserializer = 
					new org.openspcoop2.message.context.utils.serializer.JaxbDeserializer();
			messageContext = deserializer.readMessageContext(is);
		}catch(Exception e) {
			throw new MessageException(e.getMessage(),e);
		}	
		this.readResourcesFrom(messageContext);
	}
	
	
	
	/* Cast */
	
	@Override
	public OpenSPCoop2SoapMessage castAsSoap() throws MessageException{
		if(this instanceof OpenSPCoop2SoapMessage){
			return (OpenSPCoop2SoapMessage) this;
		}
		else{
			throw new MessageException("Instance (type:"+this.getMessageType()+" class:"+this.getClass().getName()+") not usable in SOAP Context");
		}
	}
	
	@Override
	public OpenSPCoop2RestMessage<?> castAsRest() throws MessageException{
		if(this instanceof OpenSPCoop2RestMessage){
			return (OpenSPCoop2RestMessage<?>) this;
		}
		else{
			throw new MessageException("Instance (type:"+this.getMessageType()+" class:"+this.getClass().getName()+") not usable in REST Context");
		}
	}
	
	@Override
	public OpenSPCoop2RestXmlMessage castAsRestXml() throws MessageException{
		if(this instanceof OpenSPCoop2RestXmlMessage){
			return (OpenSPCoop2RestXmlMessage) this;
		}
		else{
			throw new MessageException("Instance (type:"+this.getMessageType()+" class:"+this.getClass().getName()+") not usable in REST-Xml Context");
		}
	}
	
	@Override
	public OpenSPCoop2RestJsonMessage castAsRestJson() throws MessageException{
		if(this instanceof OpenSPCoop2RestJsonMessage){
			return (OpenSPCoop2RestJsonMessage) this;
		}
		else{
			throw new MessageException("Instance (type:"+this.getMessageType()+" class:"+this.getClass().getName()+") not usable in REST-Json Context");
		}
	}
	
	@Override
	public OpenSPCoop2RestBinaryMessage castAsRestBinary() throws MessageException{
		if(this instanceof OpenSPCoop2RestBinaryMessage){
			return (OpenSPCoop2RestBinaryMessage) this;
		}
		else{
			throw new MessageException("Instance (type:"+this.getMessageType()+" class:"+this.getClass().getName()+") not usable in REST-Binary Context");
		}
	}
	
	@Override
	public OpenSPCoop2RestMimeMultipartMessage castAsRestMimeMultipart() throws MessageException{
		if(this instanceof OpenSPCoop2RestMimeMultipartMessage){
			return (OpenSPCoop2RestMimeMultipartMessage) this;
		}
		else{
			throw new MessageException("Instance (type:"+this.getMessageType()+" class:"+this.getClass().getName()+") not usable in REST-MimeMultipart Context");
		}
	}
	
	
	

	/* Trasporto */
	
	@Override
	public void setTransportRequestContext(TransportRequestContext transportRequestContext){
		this.transportRequestContext = transportRequestContext;
	}
	@Override
	public TransportRequestContext getTransportRequestContext(){
		return this.transportRequestContext;
	}
	@Override
	public void setTransportResponseContext(TransportResponseContext transportResponseContext){
		this.transportResponseContext = transportResponseContext;
	}
	@Override
	public TransportResponseContext getTransportResponseContext(){
		return this.transportResponseContext;
	}
	@Override
	public void forceTransportHeader(String name, String value) {
		TransportUtils.setHeader(this.forceTransportHeaders, name, value); // deve sovrascrivere l'eventuale esistente
	}
	@Override
	public void forceTransportHeader(String name, List<String> values) {
		TransportUtils.removeObject(this.forceTransportHeaders, name); // deve sovrascrivere l'eventuale esistente
		if(values!=null && !values.isEmpty()) {
			for (String value : values) {
				TransportUtils.addHeader(this.forceTransportHeaders, name, value);	
			}
		}
	}
	@Override
	public Map<String, List<String>> getForceTransportHeaders(){
		return this.forceTransportHeaders!=null ? new HashMap<String, List<String>>(this.forceTransportHeaders) : null; // viene ritornata una immagine clonata. Per aggiungere un valore usare metodo force
	}
	@Override
	public void forceUrlProperty(String name, String value) {
		TransportUtils.setParameter(this.forceUrlProperties,name, value); // deve sovrascrivere l'eventuale esistente
	}
	@Override
	public void forceUrlProperty(String name, List<String> values) {
		TransportUtils.removeObject(this.forceUrlProperties, name); // deve sovrascrivere l'eventuale esistente
		if(values!=null && !values.isEmpty()) {
			for (String value : values) {
				TransportUtils.addParameter(this.forceUrlProperties, name, value);	
			}
		}
	}
	@Override
	public Map<String, List<String>> getForceUrlProperties(){
		return this.forceUrlProperties!=null ? new HashMap<String, List<String>>(this.forceUrlProperties) : null; // viene ritornata una immagine clonata. Per aggiungere un valore usare metodo force
	}
	@Override
	public OpenSPCoop2MessageProperties getForwardTransportHeader(ForwardConfig forwardConfig) throws MessageException{
		OpenSPCoop2MessageProperties msg = this._getForwardTransportHeader(forwardConfig);
		if(msg==null) {
			msg = new OpenSPCoop2MessageProperties();			
		}
		if(this.forceTransportHeaders!=null && this.forceTransportHeaders.size()>0) {
			Iterator<String> it = this.forceTransportHeaders.keySet().iterator();
			while (it.hasNext()) {
				String key = (String) it.next();
				List<String> values = this.forceTransportHeaders.get(key);
				msg.removePropertyValues(key);
				msg.setProperty(key, values);
			}
		}
		return msg;
	}
	protected OpenSPCoop2MessageProperties _getForwardTransportHeader(ForwardConfig forwardConfig) throws MessageException{
		try{
			if(forwardConfig==null || forwardConfig.isForwardEnable()==false) {
				return this.forwardTransportHeader;
			}
			if(this.forwardTransportHeader.isInitialize()==false){
				
				Map<String, List<String>> transportHeaders = null;
				if(MessageRole.REQUEST.equals(this.messageRole)){
					if(this.transportRequestContext!=null){
						transportHeaders = this.transportRequestContext.getHeaders();
					}
				}
				else{
					// vale sia per la risposta normale che fault
					if(this.transportResponseContext!=null){ 
						transportHeaders = this.transportResponseContext.getHeaders();
					}
				}
				
				if(transportHeaders!=null && transportHeaders.size()>0){
					TransportUtilities.initializeTransportHeaders(this.forwardTransportHeader, this.messageRole, transportHeaders, forwardConfig);
					this.forwardTransportHeader.setInitialize(true);
				}
			
			}
			return this.forwardTransportHeader;
		}catch(Exception e){
			throw new MessageException(e.getMessage(),e);
		}
	}
	
	@Override
	public OpenSPCoop2MessageProperties getForwardUrlProperties(ForwardConfig forwardConfig) throws MessageException{
		OpenSPCoop2MessageProperties msg = this._getForwardUrlProperties(forwardConfig);
		if(msg==null) {
			msg = new OpenSPCoop2MessageProperties();			
		}
		if(this.forceUrlProperties!=null && this.forceUrlProperties.size()>0) {
			Iterator<String> it = this.forceUrlProperties.keySet().iterator();
			while (it.hasNext()) {
				String key = (String) it.next();
				List<String> values = this.forceUrlProperties.get(key);
				msg.removePropertyValues(key);
				msg.setProperty(key, values);
			}
		}
		return msg;
	}
	protected OpenSPCoop2MessageProperties _getForwardUrlProperties(ForwardConfig forwardConfig) throws MessageException{
		try{
			if(forwardConfig==null || forwardConfig.isForwardEnable()==false) {
				return this.forwardUrlProperties;
			}
			if(this.forwardUrlProperties.isInitialize()==false){
				
				Map<String, List<String>> forwardUrlParamters = null;
				if(this.transportRequestContext!=null){
					forwardUrlParamters = this.transportRequestContext.getParameters();
				}
				
				if(forwardUrlParamters!=null && forwardUrlParamters.size()>0){
					TransportUtilities.initializeForwardUrlParameters(this.forwardUrlProperties, this.messageRole, forwardUrlParamters, forwardConfig);
					this.forwardUrlProperties.setInitialize(true);
				}
			
			}
			return this.forwardUrlProperties;
		}catch(Exception e){
			throw new MessageException(e.getMessage(),e);
		}
	}
	
	
	/* Forced Response */
	
	@Override
	public void setForcedResponseCode(String code){
		this.forcedResponseCode = code;
	}
	@Override
	public String getForcedResponseCode(){
		return this.forcedResponseCode;
	}
	@Override
	public boolean isForcedEmptyResponse() {
		return this.forcedEmptyResponse;
	}
	@Override
	public void forceEmptyResponse() {
		this.forcedEmptyResponse = true;
	}
	@Override
	public void forceResponse(ForcedResponseMessage msg) {
		this.forcedResponse = msg;
	}
	@Override
	public ForcedResponseMessage getForcedResponse() {
		return this.forcedResponse;
	}
	

	/* Context */
	
	@Override
	public void addContextProperty(String property, Object value){
		this.context.put(property, value);
	}
	@Override
	public Iterator<String> keysContextProperty(){
		return this.context.keySet().iterator();
	}
	@Override
	public Object getContextProperty(String property){
		return this.context.get(property);
	}
	@Override
	public Object removeContextProperty(String property){
		return this.context.remove(property);
	}
	
	@Override
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}
	@Override
	public String getTransactionId() {
		return this.transactionId;
	}
	
	
	/* ContentType */
	
	@Override
	public void addContentTypeParameter(String name,String value) throws MessageException{
		this.contentTypeParamaters.put(name, value);
	}
	
	@Override
	public void removeContentTypeParameter(String name) throws MessageException{
		this.contentTypeParamaters.remove(name);
	}
	
	
	/* MessageType */
	
	@Override
	public MessageType getMessageType(){
		return this.messageType;
	}
	
	@Override
	public void setMessageType(MessageType messageType){
		this.messageType = messageType;
	}
	
	
	/* MessageRole */
	
	@Override
	public MessageRole getMessageRole() {
		return this.messageRole;
	}
	
	@Override
	public void setMessageRole(MessageRole messageRole) {
		this.messageRole = messageRole;
	}
	
	protected SOAPBody _getSOAPBody_internalAnalyze()throws MessageException, MessageNotSupportedException {
		OpenSPCoop2SoapMessage soapMsg = this.castAsSoap();
		return soapMsg.getSOAPBody();
	}
	@Override
	public boolean isFault() throws MessageException {
		try {
			boolean isFault = false;
			if(ServiceBinding.SOAP.equals(this.getServiceBinding())){
				if(MessageRole.FAULT.equals(this.getMessageRole())){
					isFault =  true;
				}
				else {				
					OpenSPCoop2SoapMessage soapMsg = this.castAsSoap();		
					OpenSPCoop2MessageSoapStreamReader soapReader = soapMsg.getSoapReader();
					if(soapReader!=null && soapReader.isParsingComplete()) {
						isFault = soapReader.isFault();
					}
					else {
						SOAPBody soapBody = this._getSOAPBody_internalAnalyze();
						boolean hasContent = soapBody!=null;
						if(hasContent){
							hasContent = SoapUtils.getFirstNotEmptyChildNode(this.messageFactory, soapBody, false)!=null;
						}
						isFault = (hasContent && soapBody.hasFault());
					}
				}
			}
			else{
				if(MessageRole.FAULT.equals(this.getMessageRole())){
					isFault =  true;
				}
				else {
					OpenSPCoop2RestMessage<?> restMsg = this.castAsRest();
					isFault = restMsg.isProblemDetailsForHttpApis_RFC7807();
				}
			}
			return isFault;
		}catch(Exception e) {
			throw new MessageException(e.getMessage(),e);
		}
	}
	
	
	/* ServiceBinding */
	
	@Override
	public ServiceBinding getServiceBinding(){
		if(this instanceof OpenSPCoop2SoapMessage){
			return ServiceBinding.SOAP;
		}
		else{
			return ServiceBinding.REST;
		}
	}
	
	
	
	/* Content Length */
	
	@Override
	public void updateIncomingMessageContentLength(){
		// NOP (Viene letta tutta nel costruttore)
	}
	
	@Override
	public void updateIncomingMessageContentLength(long incomingsize){
		this.incomingSizeForced = incomingsize;
	}
	
	@Override
	public long getIncomingMessageContentLength() {
		if(this.incomingSizeForced!=null){
			return this.incomingSizeForced;
		}else{
			return this.incomingsize;
		}
	}

	@Override
	public void updateOutgoingMessageContentLength(long outgoingsize){
		this.outgoingsize = outgoingsize;
	}
	
	@Override
	public long getOutgoingMessageContentLength() {
		return this.outgoingsize;
	}
	
	
	
	/* Node Utilities */
	
	@Override
	public String getAsString(Node ele, boolean consume){
		try {
			return XMLUtils.getInstance(this.messageFactory).toString(ele,true);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} 
	}
	
	@Override
	public byte[] getAsByte(Node ele, boolean consume){
		try {
			return XMLUtils.getInstance(this.messageFactory).toByteArray(ele,true);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} 
	}
	
	@Override
	public void addNamespaceXSITypeIfNotExists(Node element, Node root) throws MessageException {
		try {
			if(this.normalizeNamespaceXSIType) {
				DynamicNamespaceContext dnc = null;
				DynamicNamespaceContextFactory dncFactory = DynamicNamespaceContextFactory.getInstance(this.messageFactory);
				if(root instanceof javax.xml.soap.SOAPEnvelope) {
					javax.xml.soap.SOAPEnvelope soapEnvelope = (javax.xml.soap.SOAPEnvelope) root;
					if(Costanti.SOAP12_ENVELOPE_NAMESPACE.equals(soapEnvelope.getNamespaceURI())) {
						dnc = dncFactory.getNamespaceContextFromSoapEnvelope12(soapEnvelope);
					}
					else {
						dnc = dncFactory.getNamespaceContextFromSoapEnvelope11(soapEnvelope);
					}
				}
				else {
					dnc = dncFactory.getNamespaceContext(root);
				}
				XMLUtils.getInstance(this.messageFactory).addNamespaceXSITypeIfNotExists(element, dnc, true);
			}
		}catch(Exception e) {
			throw new MessageException(e.getMessage(),e);
		}
	}
	
	
	
	/* Errors */
	
	@Override
	public ParseException getParseException(){
		return this.parseException;
	}

	@Override
	public void setParseException(ParseException e){
		this.parseException = e;
	}
	
	@Override
	public void setParseException(Throwable e){
		this.parseException = ParseExceptionUtils.buildParseException(e, this.messageRole);
	}
	
	
	/* Protocol Plugin */
	
	@Override
	public void setProtocolName(String protocolName) {
		this.protocolName = protocolName;
	}

	@Override
	public String getProtocolName() {
		return this.protocolName;
	}
	
	
	/* Stream */
	
	@Override
	public void setNotifierInputStream(NotifierInputStream is) {
		this.notifierInputStream = is;
	}

	@Override
	public NotifierInputStream getNotifierInputStream() {
		return this.notifierInputStream;
	}
	
}
