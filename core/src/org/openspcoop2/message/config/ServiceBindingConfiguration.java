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

package org.openspcoop2.message.config;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.mail.internet.ContentType;

import org.openspcoop2.message.constants.Costanti;
import org.openspcoop2.message.constants.MessageRole;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.message.exception.MessageException;
import org.openspcoop2.utils.transport.TransportRequestContext;
import org.openspcoop2.utils.transport.http.ContentTypeUtilities;

/**
 * MessageConfiguration
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ServiceBindingConfiguration implements Serializable {

	public static String normalizeContext(String context){
		if(context==null || "".equals(context)){
			return Costanti.CONTEXT_EMPTY;
		}
		return context;
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private ServiceBinding defaultBinding;
	private ConfigurationServiceBindingSoap soap;	
	private ConfigurationServiceBindingRest rest;
	private ContextUrlCollection contextUrlCollection;
	
	private Map<String, ServiceBinding> restrictedServiceBindingContextUrl = new HashMap<String, ServiceBinding>();
	private Map<String, ServiceBinding> restrictedServiceBindingServiceType = new HashMap<String, ServiceBinding>();
	
	public ServiceBindingConfiguration(ServiceBinding defaultBinding, 
			ConfigurationServiceBindingSoap soap, ConfigurationServiceBindingRest rest,
			ContextUrlCollection contextUrlCollection) throws MessageException{
		if(defaultBinding==null){
			throw new MessageException("Default ServiceBinding not defined");
		}
		if(soap==null){
			throw new MessageException("SOAP-ServiceBinding not defined");
		}
		if(rest==null){
			throw new MessageException("REST-ServiceBinding not defined");
		}
		this.defaultBinding = defaultBinding;
		this.soap = soap;
		this.rest = rest;
		if(contextUrlCollection==null){
			this.contextUrlCollection = new ContextUrlCollection(this.soap.getBinding(),this.rest.getBinding());
		}
		else{
			this.contextUrlCollection = contextUrlCollection;
		}
	}
	
	
	// ----- SERVICE BINDING ------
	
	public ServiceBinding getDefaultBinding() {
		return this.defaultBinding;
	}
	
	public boolean isServiceBindingSupported(ServiceBinding serviceBinding){
		if(ServiceBinding.SOAP.equals(serviceBinding)){
			return this.soap.isEnabled();
		}
		else {
			return this.rest.isEnabled();
		}
	}
	
	public List<MessageType> getMessageTypeSupported(ServiceBinding serviceBinding){
		if(ServiceBinding.SOAP.equals(serviceBinding)){
			return this.soap.getMessageTypeSupported();
		}
		else {
			return this.rest.getMessageTypeSupported();
		}
	}
	
	public boolean isServiceBindingContextEnabled(ServiceBinding serviceBinding, String contextParam){
		String context = normalizeContext(contextParam);
		if(this.restrictedServiceBindingContextUrl.containsKey(context)){
			if(ServiceBinding.SOAP.equals(this.restrictedServiceBindingContextUrl.get(context))){
				return ServiceBinding.SOAP.equals(serviceBinding);
			}
			else {
				return ServiceBinding.REST.equals(serviceBinding);
			}
		}
		return true;
	}
		

	public boolean isServiceBindingServiceTypeEnabled(ServiceBinding serviceBinding, String serviceType){
		if(this.restrictedServiceBindingServiceType.containsKey(serviceType)){
			if(ServiceBinding.SOAP.equals(this.restrictedServiceBindingServiceType.get(serviceType))){
				return ServiceBinding.SOAP.equals(serviceBinding);
			}
			else {
				return ServiceBinding.REST.equals(serviceBinding);
			}
		}
		return true;
	}
	
	public void addServiceBindingContextRestriction(ServiceBinding serviceBinding, String contextParam){
		String context = normalizeContext(contextParam);
		if(this.restrictedServiceBindingContextUrl.containsKey(context)){
			this.restrictedServiceBindingContextUrl.remove(context);
		}
		this.restrictedServiceBindingContextUrl.put(context, serviceBinding);
	}
	
	public void addServiceBindingEmptyContextRestriction(ServiceBinding serviceBinding){
		this.addServiceBindingContextRestriction(serviceBinding, null);
	}
	
	public void addServiceBindingServiceTypeRestriction(ServiceBinding serviceBinding, String serviceType){
		if(this.restrictedServiceBindingServiceType.containsKey(serviceType)){
			this.restrictedServiceBindingServiceType.remove(serviceType);
		}
		this.restrictedServiceBindingServiceType.put(serviceType, serviceBinding);
	}
	
	
	
	// ----- INTEGRATION ERROR ------
	
	public IntegrationErrorCollection getInternalIntegrationErrorConfiguration(ServiceBinding serviceBinding) {
		if(ServiceBinding.SOAP.equals(serviceBinding)){
			return this.soap.getInternalIntegrationErrorConfiguration();
		}
		else {
			return this.rest.getInternalIntegrationErrorConfiguration();
		}
	}
	public IntegrationErrorCollection getExternalIntegrationErrorConfiguration(ServiceBinding serviceBinding) {
		if(ServiceBinding.SOAP.equals(serviceBinding)){
			return this.soap.getExternalIntegrationErrorConfiguration();
		}
		else {
			return this.rest.getExternalIntegrationErrorConfiguration();
		}
	}
	

	
	// ----- MESSAGE TYPES ------
	
	public boolean existsContextUrlMapping(){
		return this.contextUrlCollection!=null && this.contextUrlCollection.sizeContextUrl()>0;
	}
	
	public List<String> getContentTypesSupported(ServiceBinding serviceBinding, MessageRole messageType, 
			TransportRequestContext transportContext) throws MessageException{
		return getContentTypesSupported(serviceBinding, messageType, 
				transportContext.getProtocolWebContext(), transportContext.getFunction(), transportContext.getFunctionParameters());
	}
	public List<String> getContentTypesSupported(ServiceBinding serviceBinding, MessageRole messageType, 
			String protocol, String function, String functionParameters) throws MessageException{
		
		if(serviceBinding==null){
			throw new MessageException("ServiceBinding not defined");
		}
		if(messageType==null){
			throw new MessageException("MessageType not defined");
		}
		
		AbstractMediaTypeCollection configurationFlow;

		if(ServiceBinding.SOAP.equals(serviceBinding)){
			if(this.soap.isEnabled()==false){
				throw new MessageException("Typology ["+serviceBinding+"] not supported");
			}
			if(MessageRole.REQUEST.equals(messageType)){
				configurationFlow = this.soap.getRequest();
			}
			else{
				configurationFlow = this.soap.getResponse();
			}
		}
		else{
			if(this.rest.isEnabled()==false){
				throw new MessageException("Typology ["+serviceBinding+"] not supported");
			}
			if(MessageRole.REQUEST.equals(messageType)){
				configurationFlow = this.rest.getRequest();
			}
			else{
				configurationFlow = this.rest.getResponse();
			}
		}

		MessageType mpv = this.contextUrlCollection.getMessageType(protocol,function,functionParameters);
		if(mpv!=null){
			List<String> ct = this.contextUrlCollection.getContentTypesRestriction(protocol,function,functionParameters);
			if(ct==null){
				ct = new ArrayList<>();
			}
			if(ct.size()<=0){
				ct.add(Costanti.CONTENT_TYPE_ALL);
			}
			return ct;
		}
		return configurationFlow.getContentTypes();

	}
	
	public String getContentTypesSupportedAsString(ServiceBinding serviceBinding, MessageRole messageType, 
			TransportRequestContext transportContext) throws MessageException{
		return getContentTypesSupportedAsString(serviceBinding, messageType, 
				transportContext.getProtocolWebContext(), transportContext.getFunction(), transportContext.getFunctionParameters());
	}
	public String getContentTypesSupportedAsString(ServiceBinding serviceBinding, MessageRole messageType, 
			String protocol, String function, String functionParameters) throws MessageException{
		StringBuilder bf = new StringBuilder();
		for (String ct : this.getContentTypesSupported(serviceBinding, messageType, 
				protocol,function,functionParameters)) {
			if(bf.length()>0){
				bf.append(", ");
			}
			bf.append(ct);
		}
		return bf.toString();
	}
	
	public MessageType getRequestMessageType(ServiceBinding serviceBinding, 
			TransportRequestContext transportContext, 
			String contentType) throws MessageException{
		return this.getMessageType(serviceBinding, MessageRole.REQUEST, 
				transportContext.getProtocolWebContext(), transportContext.getFunction(), transportContext.getFunctionParameters(), 
				contentType, null);
	}
	public MessageType getResponseMessageType(ServiceBinding serviceBinding, 
			TransportRequestContext transportContext, 
			String contentType, Integer status) throws MessageException{
		return this.getMessageType(serviceBinding, MessageRole.RESPONSE, 
				transportContext.getProtocolWebContext(), transportContext.getFunction(), transportContext.getFunctionParameters(), 
				contentType, status);
	}
	public MessageType getMessageType(ServiceBinding serviceBinding, MessageRole messageRole, 
			TransportRequestContext transportContext, 
			String contentType, Integer status) throws MessageException{
		return this.getMessageType(serviceBinding, messageRole, 
				transportContext.getProtocolWebContext(), transportContext.getFunction(), transportContext.getFunctionParameters(), 
				contentType, status);
	}
	public MessageType getMessageType(ServiceBinding serviceBinding, MessageRole messageRole, 
			String protocol, String function, String functionParameters, 
			String contentType, Integer status) throws MessageException{
		
		if(serviceBinding==null){
			throw new MessageException("ServiceBinding not defined");
		}
		if(messageRole==null){
			throw new MessageException("MessageRole not defined");
		}
		
		String mediaType = contentType;
		boolean withAttachments = false;
		boolean mtom = false;
		if(ServiceBinding.SOAP.equals(serviceBinding) && mediaType!=null){
			try{
				if(ContentTypeUtilities.isMultipartRelated(mediaType)){
					withAttachments = true;
					String internal = ContentTypeUtilities.getInternalMultipartContentType(mediaType);
					if(internal!=null){
						mediaType = internal;
						mtom = ContentTypeUtilities.isMtom(mediaType);
						if(mtom){
							withAttachments = false;
						}
					}
				}
			}catch(Exception e){
				throw new MessageException(e.getMessage(),e);
			}
		}
		try{
			if(mediaType!=null){
				ContentType ct = new ContentType(mediaType);
				mediaType = ct.getBaseType();
			}
		}catch(Exception e){
			throw new MessageException(e.getMessage(),e);
		}
		
		MessageType messageType = readMessageTypeEngine(serviceBinding, messageRole, 
				protocol, function, functionParameters,
				status,
				mediaType);
		
		// check compatibilità rispetto alla tipologia
			
		if(messageType!=null && ServiceBinding.SOAP.equals(serviceBinding)){
			if(withAttachments){
				if(MessageType.SOAP_11.equals(messageType)){
					if(this.soap.getBinding().isBinding_soap11_withAttachments()==false){
						throw new MessageException("SOAPWithAttachments disabled in Soap11 Binding");
					}
				}
				else{
					if(this.soap.getBinding().isBinding_soap12_withAttachments()==false){
						throw new MessageException("SOAPWithAttachments disabled in Soap12 Binding");
					}
				}
			}
			if(mtom){
				if(MessageType.SOAP_11.equals(messageType)){
					if(this.soap.getBinding().isBinding_soap11_mtom()==false){
						throw new MessageException("MTOM disabled in Soap11 Binding");
					}
				}
				else{
					if(this.soap.getBinding().isBinding_soap12_mtom()==false){
						throw new MessageException("MTOM disabled in Soap12 Binding");
					}
				}
			}
		}
		
		return messageType;

	}

	private MessageType readMessageTypeEngine(ServiceBinding serviceBinding, MessageRole messageRole, 
			String protocol, String function, String functionParameters,
			Integer status,
			String mediaType) throws MessageException {
		MessageType messageType = this.contextUrlCollection.getMessageType(protocol,function,functionParameters);
		if(messageType!=null){
			
			// check sulla restrizione dei content-type
			List<String> ct = this.contextUrlCollection.getContentTypesRestriction(protocol,function,functionParameters);
			if(ct!=null && ct.size()>0){
				if(mediaType!=null && ct.contains(mediaType)){
					return messageType;
				}
			}
			else{
				return messageType;	
			}
			
		}
		
		AbstractMediaTypeCollection mediaTypeCollecton;

		if(ServiceBinding.SOAP.equals(serviceBinding)){
			if(this.soap.isEnabled()==false){
				throw new MessageException("Typology ["+serviceBinding+"] not supported");
			}
			if(MessageRole.REQUEST.equals(messageRole)){
				mediaTypeCollecton = this.soap.getRequest();
			}
			else{
				mediaTypeCollecton = this.soap.getResponse();
			}
		}
		else{
			if(this.rest.isEnabled()==false){
				throw new MessageException("Typology ["+serviceBinding+"] not supported");
			}
			if(MessageRole.REQUEST.equals(messageRole)){
				mediaTypeCollecton = this.rest.getRequest();
			}
			else{
				mediaTypeCollecton = this.rest.getResponse();
			}
		}
				
		messageType = mediaTypeCollecton.getMessageProcessor(mediaType,status); // può essere null
		return messageType;
	}
}
