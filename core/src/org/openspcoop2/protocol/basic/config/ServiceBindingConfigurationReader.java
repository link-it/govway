/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
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

package org.openspcoop2.protocol.basic.config;

import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.PortType;
import org.openspcoop2.core.registry.Resource;
import org.openspcoop2.core.registry.ResourceRepresentation;
import org.openspcoop2.core.registry.ResourceResponse;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.message.config.ConfigurationServiceBindingRest;
import org.openspcoop2.message.config.ConfigurationServiceBindingSoap;
import org.openspcoop2.message.config.ContextUrlCollection;
import org.openspcoop2.message.config.IntegrationErrorCollection;
import org.openspcoop2.message.config.RestBinding;
import org.openspcoop2.message.config.ServiceBindingConfiguration;
import org.openspcoop2.message.config.SoapBinding;
import org.openspcoop2.message.constants.IntegrationError;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.message.exception.MessageException;
import org.openspcoop2.protocol.manifest.EmptySubContextMapping;
import org.openspcoop2.protocol.manifest.Openspcoop2;
import org.openspcoop2.protocol.manifest.RestConfiguration;
import org.openspcoop2.protocol.manifest.RestMediaTypeCollection;
import org.openspcoop2.protocol.manifest.RestMediaTypeDefaultMapping;
import org.openspcoop2.protocol.manifest.RestMediaTypeMapping;
import org.openspcoop2.protocol.manifest.RestMediaTypeUndefinedMapping;
import org.openspcoop2.protocol.manifest.SoapConfiguration;
import org.openspcoop2.protocol.manifest.SoapMediaTypeCollection;
import org.openspcoop2.protocol.manifest.SoapMediaTypeDefaultMapping;
import org.openspcoop2.protocol.manifest.SoapMediaTypeMapping;
import org.openspcoop2.protocol.manifest.SoapMediaTypeUndefinedMapping;
import org.openspcoop2.protocol.manifest.SubContextMapping;
import org.openspcoop2.protocol.manifest.constants.Costanti;
import org.openspcoop2.protocol.manifest.constants.DefaultIntegrationErrorMessageType;
import org.openspcoop2.protocol.manifest.constants.IntegrationErrorMessageType;
import org.openspcoop2.protocol.manifest.constants.RestMessageType;
import org.openspcoop2.protocol.manifest.constants.SoapMessageType;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.registry.IRegistryReader;
import org.openspcoop2.protocol.sdk.registry.RegistryNotFound;
import org.openspcoop2.utils.transport.TransportRequestContext;

/**
 * ServiceBindingConfigurationReader
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ServiceBindingConfigurationReader  {

	public static ServiceBinding getServiceBinding(IDServizio idServizio, IRegistryReader registryReader) throws ProtocolException, RegistryNotFound{
		try{
			
			AccordoServizioParteSpecifica aps = registryReader.getAccordoServizioParteSpecifica(idServizio);
			IDAccordo idAccordoParteComune = IDAccordoFactory.getInstance().getIDAccordoFromUri(aps.getAccordoServizioParteComune());
			AccordoServizioParteComune apc = registryReader.getAccordoServizioParteComune(idAccordoParteComune);
			switch (apc.getServiceBinding()) {
				case SOAP:
					return ServiceBinding.SOAP;
				case REST:
					return ServiceBinding.REST;
			}
			throw new ProtocolException("Service ["+idServizio+"] not found");
			
		}
		catch(RegistryNotFound notFound) {
			throw notFound;
		}
		catch(Exception e){
			throw new ProtocolException(e.getMessage(),e);
		}
	}

	public static ServiceBindingConfiguration getDefaultServiceBindingConfiguration(Openspcoop2 manifest, TransportRequestContext transportRequest) throws ProtocolException{
		return getServiceBindingConfiguration(manifest, transportRequest, null, null, null);
	}
	
	private static MessageType convertMessageType(org.openspcoop2.core.registry.constants.MessageType mt) {
		if(mt!=null) {
			switch (mt) {
			case SOAP_11:
				return MessageType.SOAP_11;
			case SOAP_12:
				return MessageType.SOAP_12;
			case XML:
				return MessageType.XML;
			case JSON:
				return MessageType.JSON;
			case BINARY:
				return MessageType.BINARY;
			case MIME_MULTIPART:
				return MessageType.MIME_MULTIPART;
			}
		}
		return null;
	}
	
	public static ServiceBindingConfiguration getServiceBindingConfiguration(Openspcoop2 manifest, TransportRequestContext transportRequest, 
			ServiceBinding serviceBinding, IDServizio idServizio, IRegistryReader registryReader) throws ProtocolException{
		
		try{
		
			ServiceBinding defaultBinding = getServiceBindingDefault(manifest);
			ConfigurationServiceBindingSoap soap = readConfigurationServiceBindingSoap(manifest);
			ConfigurationServiceBindingRest rest = readConfigurationServiceBindingRest(manifest);
			ContextUrlCollection contextUrlCollection = readContextUrlCollection(manifest,soap,rest);
			
			// aggiornamento per contesto
			String context = null;
			if(transportRequest!=null) {
				context = transportRequest.getProtocolWebContext();
			}
			if(context!=null && !Costanti.CONTEXT_EMPTY.equals(context)){
				for (int i = 0; i < manifest.getWeb().sizeContextList(); i++) {
					if(manifest.getWeb().getContext(i).getName().equals(context)){
						if(manifest.getWeb().getContext(i).getSoapMediaTypeCollection()!=null){
							updateMediaTypeCollection(soap, manifest.getWeb().getContext(i).getSoapMediaTypeCollection(), true, true);
						}
						if(manifest.getWeb().getContext(i).getRestMediaTypeCollection()!=null){
							updateMediaTypeCollection(rest, manifest.getWeb().getContext(i).getRestMediaTypeCollection(), true, true);
						}
					}
				}
			}
			else if(manifest.getWeb().getEmptyContext()!=null){
				if(manifest.getWeb().getEmptyContext().getSoapMediaTypeCollection()!=null){
					updateMediaTypeCollection(soap, manifest.getWeb().getEmptyContext().getSoapMediaTypeCollection(), true, true);
				}
				if(manifest.getWeb().getEmptyContext().getRestMediaTypeCollection()!=null){
					updateMediaTypeCollection(rest, manifest.getWeb().getEmptyContext().getRestMediaTypeCollection(), true, true);
				}
			}
			
			if(serviceBinding!=null && idServizio!=null){
				
				// aggiornamento per tipo servizio
				for (int i = 0; i < manifest.getRegistry().getService().getTypes().sizeTypeList(); i++) {
					String serviceType = manifest.getRegistry().getService().getTypes().getType(i).getName();
					if(serviceType.equals(idServizio.getTipo())){
						if( manifest.getRegistry().getService().getTypes().getType(i).getSoapMediaTypeCollection()!=null){
							updateMediaTypeCollection(soap, manifest.getRegistry().getService().getTypes().getType(i).getSoapMediaTypeCollection(), true, true);
						}
						if( manifest.getRegistry().getService().getTypes().getType(i).getRestMediaTypeCollection()!=null){
							updateMediaTypeCollection(rest, manifest.getRegistry().getService().getTypes().getType(i).getRestMediaTypeCollection(), true, true);
						}
					}
				}				
				
				// Accordi
				AccordoServizioParteComune aspc = null;
				AccordoServizioParteSpecifica asps = null;
				try {
					asps = registryReader.getAccordoServizioParteSpecifica(idServizio, false);
				}catch(RegistryNotFound notFound) {}
				if(asps!=null) {
					try {
						IDAccordo idAccordo = IDAccordoFactory.getInstance().getIDAccordoFromUri(asps.getAccordoServizioParteComune());
						aspc = registryReader.getAccordoServizioParteComune(idAccordo, false);
					}catch(RegistryNotFound notFound) {}
				}
				
				// ricerca per servizio dell'accordo parte comune
				// Se presente viene forzato un message type indipendente dal mediaType e valido sia per la richiesta che per la risposta
				if(aspc!=null) {
					MessageType messageTypeService = convertMessageType(aspc.getMessageType());
					if(messageTypeService!=null){
						if(ServiceBinding.SOAP.equals(serviceBinding)){
							updateSoapMediaTypeCollection(soap, messageTypeService, true, true);
						}
						else if(ServiceBinding.REST.equals(serviceBinding)){
							updateRestMediaTypeCollection(rest, messageTypeService, true, true);
						}
					}
				}
				
				if(ServiceBinding.SOAP.equals(serviceBinding) && aspc!=null && asps!=null && asps.getPortType()!=null) {
					// ricerca per port-type dell'accordo parte comune
					// Se presente viene forzato un message type indipendente dal mediaType e valido sia per la richiesta che per la risposta
					for (PortType pt : aspc.getPortTypeList()) {
						if(pt.getNome().equals(asps.getPortType())) {
							MessageType messageTypeService = convertMessageType(pt.getMessageType());
							if(messageTypeService!=null){
								if(ServiceBinding.SOAP.equals(serviceBinding)){
									updateSoapMediaTypeCollection(soap, messageTypeService, true, true);
								}
							}
							break;
						}
					}
				}
				
				// ricerca per servizio dell'accordo parte specifica
				// Se presente viene forzato un message type indipendente dal mediaType e valido sia per la richiesta che per la risposta
				if(asps!=null) {
					MessageType messageTypeParteSpecifica = convertMessageType(asps.getMessageType());
					if(messageTypeParteSpecifica!=null){
						if(ServiceBinding.SOAP.equals(serviceBinding)){
							updateSoapMediaTypeCollection(soap, messageTypeParteSpecifica, true, true);
						}
						else if(ServiceBinding.REST.equals(serviceBinding)){
							updateRestMediaTypeCollection(rest, messageTypeParteSpecifica, true, true);
						}
					}
				}
				
				if(idServizio.getAzione()!=null && aspc!=null && ServiceBinding.REST.equals(serviceBinding)) {
				
					if(aspc.sizeResourceList()>0) {
						for (Resource resource : aspc.getResourceList()) {
							if(resource.getNome().equals(idServizio.getAzione())) {
								
								// Se presente viene forzato un message type indipendente dal mediaType e valido sia per la richiesta che per la risposta
								MessageType messageTypeRisorsa = convertMessageType(resource.getMessageType());
								if(messageTypeRisorsa!=null){
									updateRestMediaTypeCollection(rest, messageTypeRisorsa, true, true);
								}
								
								// Se presente viene forzato un message type indipendente dal mediaType ma valido solo per la richiesta
								MessageType messageTypeAzioneRichiesta = convertMessageType(resource.getRequestMessageType());
								if(messageTypeAzioneRichiesta!=null){
									updateRestMediaTypeCollection(rest, messageTypeAzioneRichiesta, true, false);
								}
								
								// Se presente viene forzato un message type indipendente dal mediaType ma valido solo per la risposta
								MessageType messageTypeAzioneRisposta = convertMessageType(resource.getResponseMessageType());
								if(messageTypeAzioneRisposta!=null){
									updateRestMediaTypeCollection(rest, messageTypeAzioneRisposta, false, true);
								}
								
								// ultimo step di replace singolo mediaType sulla richiesta o sulla risposta (solo per REST)
								if(resource.getRequest()!=null && resource.getRequest().sizeRepresentationList()>0) {
									for (ResourceRepresentation rr : resource.getRequest().getRepresentationList()) {
										MessageType messageTypeMediaType = convertMessageType(rr.getMessageType());
										if(messageTypeMediaType!=null) {
											rest.getRequest().addOrReplaceMediaType(rr.getMediaType(), messageTypeMediaType, false);
										}
									}
								}
								if(resource.sizeResponseList()>0) {
									for (ResourceResponse response : resource.getResponseList()) {
										if(response.sizeRepresentationList()>0) {
											for (ResourceRepresentation rr : response.getRepresentationList()) {
												MessageType messageTypeMediaType = convertMessageType(rr.getMessageType());
												if(messageTypeMediaType!=null) {
													rest.getResponse().addOrReplaceMediaType(rr.getMediaType(), response.getStatus(), messageTypeMediaType, false);
												}
											}
										}
									}
								}
								break;
							}
						}
					}

				}
			}
			
			ServiceBindingConfiguration config = new ServiceBindingConfiguration(defaultBinding, soap, rest, contextUrlCollection);
			addServiceBindingRestriction(manifest, config);
			return config;
			
		}catch(Exception e){
			throw new ProtocolException(e.getMessage(),e);
		}
	}
		
	private static ServiceBinding getServiceBindingDefault(Openspcoop2 manifest){
		
		ServiceBinding defaultBinding = null;
		
		// Default
		org.openspcoop2.protocol.manifest.constants.ServiceBinding defaultBindingManifest = manifest.getBinding().getDefault();
		if(defaultBindingManifest!=null){
			switch (defaultBindingManifest) {
			case SOAP:
				defaultBinding = ServiceBinding.SOAP;
				break;
			case REST:
				defaultBinding = ServiceBinding.REST;
				break;
			}
		}
		else{
			if(manifest.getBinding().getSoap()!=null){
				defaultBinding = ServiceBinding.SOAP;
			}
			else{
				defaultBinding = ServiceBinding.REST;
			}
		}
		
		return defaultBinding;
	}
	
	private static ConfigurationServiceBindingSoap readConfigurationServiceBindingSoap(Openspcoop2 manifest) throws MessageException{
		
		SoapConfiguration soapConfig = manifest.getBinding().getSoap();
		boolean enabled = soapConfig!=null;
		SoapBinding binding = null;
		IntegrationErrorCollection internalIntegrationErrorConfiguration = null;
		IntegrationErrorCollection externalIntegrationErrorConfiguration = null;
		
		if(enabled){
			
			binding = new SoapBinding(soapConfig.isSoap11(), soapConfig.isSoap11WithAttachments(), soapConfig.isSoap11Mtom(), 
					soapConfig.isSoap12(), soapConfig.isSoap12WithAttachments(), soapConfig.isSoap12Mtom());
			
			internalIntegrationErrorConfiguration = readIntegrationErrorConfiguration(soapConfig.getIntegrationError().getInternal());
			externalIntegrationErrorConfiguration = readIntegrationErrorConfiguration(soapConfig.getIntegrationError().getExternal());
			
		}
		
		ConfigurationServiceBindingSoap soap = new ConfigurationServiceBindingSoap(enabled, binding, internalIntegrationErrorConfiguration, externalIntegrationErrorConfiguration);
		soap.init();
		
		if(enabled){
			
			if(soapConfig.getMediaTypeCollection()!=null){
				updateMediaTypeCollection(soap, soapConfig.getMediaTypeCollection(), true, true);
			}
			
		}
		
		return soap;
			
	}
	
	private static void updateMediaTypeCollection(ConfigurationServiceBindingSoap soap, SoapMediaTypeCollection mediaTypesCollection, 
			boolean request, boolean response) throws MessageException{
		
		if(mediaTypesCollection.sizeMediaTypeList()<=0 && mediaTypesCollection.getDefault()==null && mediaTypesCollection.getUndefined()==null){
			return;
		}
		if(request){
			soap.getRequest().clear();
		}
		if(response){
			soap.getResponse().clear();
		}
		
		for (int i = 0; i < mediaTypesCollection.sizeMediaTypeList(); i++) {
			SoapMediaTypeMapping mapping = mediaTypesCollection.getMediaType(i);
			if(request){
				soap.getRequest().addMediaType(mapping.getBase(),  convertToMessageType(mapping.getMessageType()), mapping.isRegExpr());
			}
			if(response){
				soap.getResponse().addMediaType(mapping.getBase(), convertToMessageType(mapping.getMessageType()), mapping.isRegExpr());
			}
		}
		
		if(mediaTypesCollection.getDefault()!=null){
			if(request){
				soap.getRequest().addDefaultMediaType(convertToMessageType(mediaTypesCollection.getDefault().getMessageType()));
			}
			if(response){
				soap.getResponse().addDefaultMediaType(convertToMessageType(mediaTypesCollection.getDefault().getMessageType()));
			}
		}
		
		if(mediaTypesCollection.getUndefined()!=null){
			if(request){
				soap.getRequest().addUndefinedMediaType(convertToMessageType(mediaTypesCollection.getUndefined().getMessageType()));
			}
			if(response){
				soap.getResponse().addUndefinedMediaType(convertToMessageType(mediaTypesCollection.getUndefined().getMessageType()));
			}
		}
	}
	
	private static void updateSoapMediaTypeCollection(ConfigurationServiceBindingSoap soap, MessageType messageType, boolean request, boolean response) throws MessageException{
		SoapMediaTypeCollection mediaTypeCollection = new SoapMediaTypeCollection();
		
		SoapMediaTypeDefaultMapping defaultMapping = new SoapMediaTypeDefaultMapping();
		defaultMapping.setMessageType(convertToSoapMessageType(messageType));
		mediaTypeCollection.setDefault(defaultMapping);
		
		SoapMediaTypeUndefinedMapping undefinedMapping = new SoapMediaTypeUndefinedMapping();
		undefinedMapping.setMessageType(convertToSoapMessageType(messageType));
		mediaTypeCollection.setUndefined(undefinedMapping);
		
		updateMediaTypeCollection(soap, mediaTypeCollection, request, response);
	}
	
	private static MessageType convertToMessageType(SoapMessageType messageType){
		switch (messageType) {
		case SOAP_11:
			return MessageType.SOAP_11;
		case SOAP_12:
			return MessageType.SOAP_12;
		}
		return null;
	}
	
	@SuppressWarnings("incomplete-switch")
	private static SoapMessageType convertToSoapMessageType(MessageType messageType){
		switch (messageType) {
		case SOAP_11:
			return SoapMessageType.SOAP_11;
		case SOAP_12:
			return SoapMessageType.SOAP_12;
		}
		return null;
	}
	
	private static ConfigurationServiceBindingRest readConfigurationServiceBindingRest(Openspcoop2 manifest) throws MessageException{
		
		RestConfiguration restConfig = manifest.getBinding().getRest();
		boolean enabled = restConfig!=null;
		RestBinding binding = null;
		IntegrationErrorCollection internalIntegrationErrorConfiguration = null;
		IntegrationErrorCollection externalIntegrationErrorConfiguration = null;
		
		if(enabled){
			
			binding = new RestBinding(restConfig.isXml(), restConfig.isJson(), restConfig.isBinary(), restConfig.isMimeMultipart());
			
			internalIntegrationErrorConfiguration = readIntegrationErrorConfiguration(restConfig.getIntegrationError().getInternal());
			externalIntegrationErrorConfiguration = readIntegrationErrorConfiguration(restConfig.getIntegrationError().getExternal());
		}
		
		ConfigurationServiceBindingRest rest = new ConfigurationServiceBindingRest(enabled, binding, internalIntegrationErrorConfiguration, externalIntegrationErrorConfiguration);
		rest.init();
		
		if(enabled){
			
			if(restConfig.getMediaTypeCollection()!=null){
				updateMediaTypeCollection(rest, restConfig.getMediaTypeCollection(), true, true);
			}
			
		}
		
		return rest;
			
	}
	
	private static void updateMediaTypeCollection(ConfigurationServiceBindingRest rest, RestMediaTypeCollection mediaTypesCollection, boolean request, boolean response) throws MessageException{
		
		if(mediaTypesCollection.sizeMediaTypeList()<=0 && mediaTypesCollection.getDefault()==null && mediaTypesCollection.getUndefined()==null){
			return;
		}
		if(request){
			rest.getRequest().clear();
		}
		if(response){
			rest.getResponse().clear();
		}
		
		for (int i = 0; i < mediaTypesCollection.sizeMediaTypeList(); i++) {
			RestMediaTypeMapping mapping = mediaTypesCollection.getMediaType(i);
			if(request){
				rest.getRequest().addMediaType(mapping.getBase(), convertToMessageType(mapping.getMessageType()), mapping.isRegExpr());
			}
			if(response){
				rest.getResponse().addMediaType(mapping.getBase(), convertToMessageType(mapping.getMessageType()), mapping.isRegExpr());
			}
		}
		
		if(mediaTypesCollection.getDefault()!=null){
			if(request){
				rest.getRequest().addDefaultMediaType(convertToMessageType(mediaTypesCollection.getDefault().getMessageType()));
			}
			if(response){
				rest.getResponse().addDefaultMediaType(convertToMessageType(mediaTypesCollection.getDefault().getMessageType()));
			}
		}
		
		if(mediaTypesCollection.getUndefined()!=null){
			if(request){
				rest.getRequest().addUndefinedMediaType(convertToMessageType(mediaTypesCollection.getUndefined().getMessageType()));
			}
			if(response){
				rest.getResponse().addUndefinedMediaType(convertToMessageType(mediaTypesCollection.getUndefined().getMessageType()));
			}
		}
	}
	
	private static void updateRestMediaTypeCollection(ConfigurationServiceBindingRest rest, MessageType messageType, boolean request, boolean response) throws MessageException{
		RestMediaTypeCollection mediaTypeCollection = new RestMediaTypeCollection();
		
		RestMediaTypeDefaultMapping defaultMapping = new RestMediaTypeDefaultMapping();
		defaultMapping.setMessageType(convertToRestMessageType(messageType));
		mediaTypeCollection.setDefault(defaultMapping);
		
		RestMediaTypeUndefinedMapping undefinedMapping = new RestMediaTypeUndefinedMapping();
		undefinedMapping.setMessageType(convertToRestMessageType(messageType));
		mediaTypeCollection.setUndefined(undefinedMapping);
		
		updateMediaTypeCollection(rest, mediaTypeCollection, request, response);
	}
	
	private static MessageType convertToMessageType(RestMessageType messageType){
		switch (messageType) {
		case XML:
			return MessageType.XML;
		case JSON:
			return MessageType.JSON;
		case BINARY:
			return MessageType.BINARY;
		case MIME_MULTIPART:
			return MessageType.MIME_MULTIPART;
		}
		return null;
	}
	
	@SuppressWarnings("incomplete-switch")
	private static RestMessageType convertToRestMessageType(MessageType messageType){
		switch (messageType) {
		case XML:
			return RestMessageType.XML;
		case JSON:
			return RestMessageType.JSON;
		case BINARY:
			return RestMessageType.BINARY;
		case MIME_MULTIPART:
			return RestMessageType.MIME_MULTIPART;
		}
		return null;
	}
	
	private static IntegrationErrorCollection readIntegrationErrorConfiguration(org.openspcoop2.protocol.manifest.IntegrationErrorCollection config) throws MessageException{
		IntegrationErrorCollection integrationErrorConfiguration = new IntegrationErrorCollection();
		if(config.getDefault()!=null){
			integrationErrorConfiguration.addIntegrationError(IntegrationError.DEFAULT, 
					convertToMessageType(config.getDefault().getMessageType()), config.getDefault().getHttpReturnCode());
		}
		if(config.getAuthentication()!=null){
			integrationErrorConfiguration.addIntegrationError(IntegrationError.AUTHENTICATION, 
					convertToMessageType(config.getAuthentication().getMessageType()), config.getAuthentication().getHttpReturnCode());
		}
		if(config.getAuthorization()!=null){
			integrationErrorConfiguration.addIntegrationError(IntegrationError.AUTHORIZATION, 
					convertToMessageType(config.getAuthorization().getMessageType()), config.getAuthorization().getHttpReturnCode());
		}
		if(config.getNotFound()!=null){
			integrationErrorConfiguration.addIntegrationError(IntegrationError.NOT_FOUND, 
					convertToMessageType(config.getNotFound().getMessageType()), config.getNotFound().getHttpReturnCode());
		}
		if(config.getBadRequest()!=null){
			integrationErrorConfiguration.addIntegrationError(IntegrationError.BAD_REQUEST, 
					convertToMessageType(config.getBadRequest().getMessageType()), config.getBadRequest().getHttpReturnCode());
		}
		if(config.getTooManyRequests()!=null){
			integrationErrorConfiguration.addIntegrationError(IntegrationError.TOO_MANY_REQUESTS, 
					convertToMessageType(config.getTooManyRequests().getMessageType()), config.getTooManyRequests().getHttpReturnCode());
		}
		if(config.getInternalError()!=null){
			integrationErrorConfiguration.addIntegrationError(IntegrationError.INTERNAL_ERROR, 
					convertToMessageType(config.getInternalError().getMessageType()), config.getInternalError().getHttpReturnCode());
		}
		return integrationErrorConfiguration;
	}
	
	private static org.openspcoop2.message.constants.IntegrationErrorMessageType convertToMessageType(DefaultIntegrationErrorMessageType messageType){
		switch (messageType) {
		case SOAP_11:
			return org.openspcoop2.message.constants.IntegrationErrorMessageType.SOAP_11;
		case SOAP_12:
			return org.openspcoop2.message.constants.IntegrationErrorMessageType.SOAP_12;
		case XML:
			return org.openspcoop2.message.constants.IntegrationErrorMessageType.XML;
		case JSON:
			return org.openspcoop2.message.constants.IntegrationErrorMessageType.JSON;
		case NONE:
			return org.openspcoop2.message.constants.IntegrationErrorMessageType.NONE;
		}
		return null;
	}
	
	private static org.openspcoop2.message.constants.IntegrationErrorMessageType convertToMessageType(IntegrationErrorMessageType messageType){
		switch (messageType) {
		case SOAP_AS_REQUEST:
			return org.openspcoop2.message.constants.IntegrationErrorMessageType.SOAP_AS_REQUEST;
		case SOAP_11:
			return org.openspcoop2.message.constants.IntegrationErrorMessageType.SOAP_11;
		case SOAP_12:
			return org.openspcoop2.message.constants.IntegrationErrorMessageType.SOAP_12;
		case XML:
			return org.openspcoop2.message.constants.IntegrationErrorMessageType.XML;
		case JSON:
			return org.openspcoop2.message.constants.IntegrationErrorMessageType.JSON;
		case NONE:
			return org.openspcoop2.message.constants.IntegrationErrorMessageType.NONE;
		case SAME_AS_REQUEST:
			return org.openspcoop2.message.constants.IntegrationErrorMessageType.SAME_AS_REQUEST;
		}
		return null;
	}
	
	private static void addServiceBindingRestriction(Openspcoop2 manifest,ServiceBindingConfiguration config){
		
		for (int i = 0; i < manifest.getWeb().sizeContextList(); i++) {
			String context = manifest.getWeb().getContext(i).getName();
			org.openspcoop2.protocol.manifest.constants.ServiceBinding serviceBinding = manifest.getWeb().getContext(i).getBinding();
			if(serviceBinding!=null){
				config.addServiceBindingContextRestriction(convertToServiceBinding(serviceBinding), context);
			}
		}
		
		if(manifest.getWeb().getEmptyContext()!=null){
			org.openspcoop2.protocol.manifest.constants.ServiceBinding serviceBinding = manifest.getWeb().getEmptyContext().getBinding();
			if(serviceBinding!=null){
				config.addServiceBindingEmptyContextRestriction(convertToServiceBinding(serviceBinding));
			}
		}
		
		for (int i = 0; i < manifest.getRegistry().getService().getTypes().sizeTypeList(); i++) {
			String serviceType = manifest.getRegistry().getService().getTypes().getType(i).getName();
			org.openspcoop2.protocol.manifest.constants.ServiceBinding serviceBinding = manifest.getRegistry().getService().getTypes().getType(i).getBinding();
			if(serviceBinding!=null){
				config.addServiceBindingServiceTypeRestriction(convertToServiceBinding(serviceBinding), serviceType);
			}
		}
		
	}
	
	private static ServiceBinding convertToServiceBinding(org.openspcoop2.protocol.manifest.constants.ServiceBinding serviceBinding){
		switch (serviceBinding) {
		case SOAP:
			return ServiceBinding.SOAP;
		case REST:
			return ServiceBinding.REST;
		}
		return null;
	}
	
	private static ContextUrlCollection readContextUrlCollection(Openspcoop2 manifest, ConfigurationServiceBindingSoap soap, ConfigurationServiceBindingRest rest) throws MessageException{
		
		ContextUrlCollection urlCollection = new ContextUrlCollection(soap.getBinding(), rest.getBinding());	
		
		for (int i = 0; i < manifest.getWeb().sizeContextList(); i++) {
			String context = manifest.getWeb().getContext(i).getName();
			if(manifest.getWeb().getContext(i).sizeSubContextList()>0){
				for (int j = 0; j < manifest.getWeb().getContext(i).sizeSubContextList(); j++) {
					SubContextMapping subContext = manifest.getWeb().getContext(i).getSubContext(j);
					urlCollection.addContext(context,subContext.get_value_function(),subContext.getBase(),convertToMessageType(subContext.getMessageType()));
				}
			}
			if(manifest.getWeb().getContext(i).getEmptySubContext()!=null){
				EmptySubContextMapping subContext = manifest.getWeb().getContext(i).getEmptySubContext();
				urlCollection.addContext(context,subContext.get_value_function(),null, convertToMessageType(subContext.getMessageType()));
			}
		}
		
		if(manifest.getWeb().getEmptyContext()!=null){
			if(manifest.getWeb().getEmptyContext().sizeSubContextList()>0){
				for (int j = 0; j < manifest.getWeb().getEmptyContext().sizeSubContextList(); j++) {
					SubContextMapping subContext = manifest.getWeb().getEmptyContext().getSubContext(j);
					urlCollection.addContext(null,subContext.get_value_function(),subContext.getBase(), convertToMessageType(subContext.getMessageType()));
				}
			}
			if(manifest.getWeb().getEmptyContext().getEmptySubContext()!=null){
				EmptySubContextMapping subContext = manifest.getWeb().getEmptyContext().getEmptySubContext();
				urlCollection.addContext(null,subContext.get_value_function(),null, convertToMessageType(subContext.getMessageType()));
			}
		}
		
		return urlCollection;
			
	}
	
	private static MessageType convertToMessageType(org.openspcoop2.protocol.manifest.constants.MessageType messageType){
		switch (messageType) {
		case SOAP_11:
			return MessageType.SOAP_11;
		case SOAP_12:
			return MessageType.SOAP_12;
		case XML:
			return MessageType.XML;
		case JSON:
			return MessageType.JSON;
		case BINARY:
			return MessageType.BINARY;
		case MIME_MULTIPART:
			return MessageType.MIME_MULTIPART;
		}
		return null;
	}
}
