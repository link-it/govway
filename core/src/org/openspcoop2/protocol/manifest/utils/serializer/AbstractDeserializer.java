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
package org.openspcoop2.protocol.manifest.utils.serializer;

import org.openspcoop2.generic_project.exception.DeserializerException;

import org.openspcoop2.protocol.manifest.Protocol;
import org.openspcoop2.protocol.manifest.Openspcoop2;
import org.openspcoop2.protocol.manifest.Binding;
import org.openspcoop2.protocol.manifest.Web;
import org.openspcoop2.protocol.manifest.Registry;
import org.openspcoop2.protocol.manifest.UrlMapping;
import org.openspcoop2.protocol.manifest.ServiceTypes;
import org.openspcoop2.protocol.manifest.Service;
import org.openspcoop2.protocol.manifest.SoapMediaTypeMapping;
import org.openspcoop2.protocol.manifest.SoapMediaTypeCollection;
import org.openspcoop2.protocol.manifest.SoapMediaTypeDefaultMapping;
import org.openspcoop2.protocol.manifest.SoapMediaTypeUndefinedMapping;
import org.openspcoop2.protocol.manifest.IntegrationErrorCode;
import org.openspcoop2.protocol.manifest.Integration;
import org.openspcoop2.protocol.manifest.RestConfiguration;
import org.openspcoop2.protocol.manifest.IntegrationErrorConfiguration;
import org.openspcoop2.protocol.manifest.RestMediaTypeCollection;
import org.openspcoop2.protocol.manifest.InterfacesConfiguration;
import org.openspcoop2.protocol.manifest.RestCollaborationProfile;
import org.openspcoop2.protocol.manifest.Functionality;
import org.openspcoop2.protocol.manifest.IntegrationErrorCollection;
import org.openspcoop2.protocol.manifest.SoapConfiguration;
import org.openspcoop2.protocol.manifest.IntegrationError;
import org.openspcoop2.protocol.manifest.IntegrationConfigurationElementName;
import org.openspcoop2.protocol.manifest.IntegrationConfigurationName;
import org.openspcoop2.protocol.manifest.IntegrationConfiguration;
import org.openspcoop2.protocol.manifest.IntegrationConfigurationResourceIdentification;
import org.openspcoop2.protocol.manifest.IntegrationConfigurationResourceIdentificationModes;
import org.openspcoop2.protocol.manifest.IntegrationConfigurationResourceIdentificationSpecificResource;
import org.openspcoop2.protocol.manifest.SoapHeaderBypassMustUnderstandHeader;
import org.openspcoop2.protocol.manifest.SoapHeaderBypassMustUnderstand;
import org.openspcoop2.protocol.manifest.Organization;
import org.openspcoop2.protocol.manifest.Versions;
import org.openspcoop2.protocol.manifest.SubContextMapping;
import org.openspcoop2.protocol.manifest.WebEmptyContext;
import org.openspcoop2.protocol.manifest.EmptySubContextMapping;
import org.openspcoop2.protocol.manifest.RestMediaTypeMapping;
import org.openspcoop2.protocol.manifest.OrganizationTypes;
import org.openspcoop2.protocol.manifest.InterfaceConfiguration;
import org.openspcoop2.protocol.manifest.Version;
import org.openspcoop2.protocol.manifest.CollaborationProfile;
import org.openspcoop2.protocol.manifest.ServiceType;
import org.openspcoop2.protocol.manifest.Transaction;
import org.openspcoop2.protocol.manifest.IntegrationConfigurationResourceIdentificationMode;
import org.openspcoop2.protocol.manifest.RFC7807;
import org.openspcoop2.protocol.manifest.RestMediaTypeDefaultMapping;
import org.openspcoop2.protocol.manifest.RestMediaTypeUndefinedMapping;
import org.openspcoop2.protocol.manifest.OrganizationType;
import org.openspcoop2.protocol.manifest.Context;
import org.openspcoop2.protocol.manifest.DefaultIntegrationError;

import java.io.InputStream;
import java.io.File;

/**     
 * XML Deserializer of beans
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public abstract class AbstractDeserializer extends org.openspcoop2.generic_project.serializer.AbstractDeserializer {



	/*
	 =================================================================================
	 Object: protocol
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.protocol.manifest.Protocol}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.Protocol}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.Protocol}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Protocol readProtocol(String fileName) throws DeserializerException {
		return (Protocol) this.xmlToObj(fileName, Protocol.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.protocol.manifest.Protocol}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.Protocol}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.Protocol}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Protocol readProtocol(File file) throws DeserializerException {
		return (Protocol) this.xmlToObj(file, Protocol.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.Protocol}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.Protocol}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.Protocol}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Protocol readProtocol(InputStream in) throws DeserializerException {
		return (Protocol) this.xmlToObj(in, Protocol.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.Protocol}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.Protocol}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.Protocol}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Protocol readProtocol(byte[] in) throws DeserializerException {
		return (Protocol) this.xmlToObj(in, Protocol.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.Protocol}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.Protocol}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.Protocol}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Protocol readProtocolFromString(String in) throws DeserializerException {
		return (Protocol) this.xmlToObj(in.getBytes(), Protocol.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: openspcoop2
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.protocol.manifest.Openspcoop2}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.Openspcoop2}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.Openspcoop2}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Openspcoop2 readOpenspcoop2(String fileName) throws DeserializerException {
		return (Openspcoop2) this.xmlToObj(fileName, Openspcoop2.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.protocol.manifest.Openspcoop2}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.Openspcoop2}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.Openspcoop2}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Openspcoop2 readOpenspcoop2(File file) throws DeserializerException {
		return (Openspcoop2) this.xmlToObj(file, Openspcoop2.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.Openspcoop2}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.Openspcoop2}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.Openspcoop2}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Openspcoop2 readOpenspcoop2(InputStream in) throws DeserializerException {
		return (Openspcoop2) this.xmlToObj(in, Openspcoop2.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.Openspcoop2}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.Openspcoop2}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.Openspcoop2}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Openspcoop2 readOpenspcoop2(byte[] in) throws DeserializerException {
		return (Openspcoop2) this.xmlToObj(in, Openspcoop2.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.Openspcoop2}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.Openspcoop2}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.Openspcoop2}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Openspcoop2 readOpenspcoop2FromString(String in) throws DeserializerException {
		return (Openspcoop2) this.xmlToObj(in.getBytes(), Openspcoop2.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: binding
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.protocol.manifest.Binding}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.Binding}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.Binding}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Binding readBinding(String fileName) throws DeserializerException {
		return (Binding) this.xmlToObj(fileName, Binding.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.protocol.manifest.Binding}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.Binding}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.Binding}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Binding readBinding(File file) throws DeserializerException {
		return (Binding) this.xmlToObj(file, Binding.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.Binding}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.Binding}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.Binding}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Binding readBinding(InputStream in) throws DeserializerException {
		return (Binding) this.xmlToObj(in, Binding.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.Binding}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.Binding}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.Binding}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Binding readBinding(byte[] in) throws DeserializerException {
		return (Binding) this.xmlToObj(in, Binding.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.Binding}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.Binding}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.Binding}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Binding readBindingFromString(String in) throws DeserializerException {
		return (Binding) this.xmlToObj(in.getBytes(), Binding.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: web
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.protocol.manifest.Web}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.Web}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.Web}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Web readWeb(String fileName) throws DeserializerException {
		return (Web) this.xmlToObj(fileName, Web.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.protocol.manifest.Web}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.Web}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.Web}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Web readWeb(File file) throws DeserializerException {
		return (Web) this.xmlToObj(file, Web.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.Web}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.Web}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.Web}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Web readWeb(InputStream in) throws DeserializerException {
		return (Web) this.xmlToObj(in, Web.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.Web}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.Web}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.Web}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Web readWeb(byte[] in) throws DeserializerException {
		return (Web) this.xmlToObj(in, Web.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.Web}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.Web}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.Web}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Web readWebFromString(String in) throws DeserializerException {
		return (Web) this.xmlToObj(in.getBytes(), Web.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: registry
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.protocol.manifest.Registry}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.Registry}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.Registry}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Registry readRegistry(String fileName) throws DeserializerException {
		return (Registry) this.xmlToObj(fileName, Registry.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.protocol.manifest.Registry}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.Registry}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.Registry}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Registry readRegistry(File file) throws DeserializerException {
		return (Registry) this.xmlToObj(file, Registry.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.Registry}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.Registry}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.Registry}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Registry readRegistry(InputStream in) throws DeserializerException {
		return (Registry) this.xmlToObj(in, Registry.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.Registry}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.Registry}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.Registry}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Registry readRegistry(byte[] in) throws DeserializerException {
		return (Registry) this.xmlToObj(in, Registry.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.Registry}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.Registry}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.Registry}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Registry readRegistryFromString(String in) throws DeserializerException {
		return (Registry) this.xmlToObj(in.getBytes(), Registry.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: urlMapping
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.protocol.manifest.UrlMapping}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.UrlMapping}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.UrlMapping}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public UrlMapping readUrlMapping(String fileName) throws DeserializerException {
		return (UrlMapping) this.xmlToObj(fileName, UrlMapping.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.protocol.manifest.UrlMapping}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.UrlMapping}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.UrlMapping}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public UrlMapping readUrlMapping(File file) throws DeserializerException {
		return (UrlMapping) this.xmlToObj(file, UrlMapping.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.UrlMapping}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.UrlMapping}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.UrlMapping}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public UrlMapping readUrlMapping(InputStream in) throws DeserializerException {
		return (UrlMapping) this.xmlToObj(in, UrlMapping.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.UrlMapping}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.UrlMapping}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.UrlMapping}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public UrlMapping readUrlMapping(byte[] in) throws DeserializerException {
		return (UrlMapping) this.xmlToObj(in, UrlMapping.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.UrlMapping}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.UrlMapping}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.UrlMapping}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public UrlMapping readUrlMappingFromString(String in) throws DeserializerException {
		return (UrlMapping) this.xmlToObj(in.getBytes(), UrlMapping.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: ServiceTypes
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.protocol.manifest.ServiceTypes}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.ServiceTypes}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.ServiceTypes}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ServiceTypes readServiceTypes(String fileName) throws DeserializerException {
		return (ServiceTypes) this.xmlToObj(fileName, ServiceTypes.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.protocol.manifest.ServiceTypes}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.ServiceTypes}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.ServiceTypes}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ServiceTypes readServiceTypes(File file) throws DeserializerException {
		return (ServiceTypes) this.xmlToObj(file, ServiceTypes.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.ServiceTypes}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.ServiceTypes}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.ServiceTypes}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ServiceTypes readServiceTypes(InputStream in) throws DeserializerException {
		return (ServiceTypes) this.xmlToObj(in, ServiceTypes.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.ServiceTypes}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.ServiceTypes}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.ServiceTypes}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ServiceTypes readServiceTypes(byte[] in) throws DeserializerException {
		return (ServiceTypes) this.xmlToObj(in, ServiceTypes.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.ServiceTypes}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.ServiceTypes}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.ServiceTypes}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ServiceTypes readServiceTypesFromString(String in) throws DeserializerException {
		return (ServiceTypes) this.xmlToObj(in.getBytes(), ServiceTypes.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: Service
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.protocol.manifest.Service}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.Service}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.Service}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Service readService(String fileName) throws DeserializerException {
		return (Service) this.xmlToObj(fileName, Service.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.protocol.manifest.Service}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.Service}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.Service}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Service readService(File file) throws DeserializerException {
		return (Service) this.xmlToObj(file, Service.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.Service}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.Service}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.Service}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Service readService(InputStream in) throws DeserializerException {
		return (Service) this.xmlToObj(in, Service.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.Service}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.Service}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.Service}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Service readService(byte[] in) throws DeserializerException {
		return (Service) this.xmlToObj(in, Service.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.Service}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.Service}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.Service}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Service readServiceFromString(String in) throws DeserializerException {
		return (Service) this.xmlToObj(in.getBytes(), Service.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: SoapMediaTypeMapping
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.protocol.manifest.SoapMediaTypeMapping}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.SoapMediaTypeMapping}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.SoapMediaTypeMapping}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SoapMediaTypeMapping readSoapMediaTypeMapping(String fileName) throws DeserializerException {
		return (SoapMediaTypeMapping) this.xmlToObj(fileName, SoapMediaTypeMapping.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.protocol.manifest.SoapMediaTypeMapping}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.SoapMediaTypeMapping}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.SoapMediaTypeMapping}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SoapMediaTypeMapping readSoapMediaTypeMapping(File file) throws DeserializerException {
		return (SoapMediaTypeMapping) this.xmlToObj(file, SoapMediaTypeMapping.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.SoapMediaTypeMapping}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.SoapMediaTypeMapping}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.SoapMediaTypeMapping}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SoapMediaTypeMapping readSoapMediaTypeMapping(InputStream in) throws DeserializerException {
		return (SoapMediaTypeMapping) this.xmlToObj(in, SoapMediaTypeMapping.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.SoapMediaTypeMapping}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.SoapMediaTypeMapping}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.SoapMediaTypeMapping}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SoapMediaTypeMapping readSoapMediaTypeMapping(byte[] in) throws DeserializerException {
		return (SoapMediaTypeMapping) this.xmlToObj(in, SoapMediaTypeMapping.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.SoapMediaTypeMapping}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.SoapMediaTypeMapping}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.SoapMediaTypeMapping}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SoapMediaTypeMapping readSoapMediaTypeMappingFromString(String in) throws DeserializerException {
		return (SoapMediaTypeMapping) this.xmlToObj(in.getBytes(), SoapMediaTypeMapping.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: SoapMediaTypeCollection
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.protocol.manifest.SoapMediaTypeCollection}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.SoapMediaTypeCollection}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.SoapMediaTypeCollection}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SoapMediaTypeCollection readSoapMediaTypeCollection(String fileName) throws DeserializerException {
		return (SoapMediaTypeCollection) this.xmlToObj(fileName, SoapMediaTypeCollection.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.protocol.manifest.SoapMediaTypeCollection}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.SoapMediaTypeCollection}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.SoapMediaTypeCollection}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SoapMediaTypeCollection readSoapMediaTypeCollection(File file) throws DeserializerException {
		return (SoapMediaTypeCollection) this.xmlToObj(file, SoapMediaTypeCollection.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.SoapMediaTypeCollection}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.SoapMediaTypeCollection}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.SoapMediaTypeCollection}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SoapMediaTypeCollection readSoapMediaTypeCollection(InputStream in) throws DeserializerException {
		return (SoapMediaTypeCollection) this.xmlToObj(in, SoapMediaTypeCollection.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.SoapMediaTypeCollection}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.SoapMediaTypeCollection}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.SoapMediaTypeCollection}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SoapMediaTypeCollection readSoapMediaTypeCollection(byte[] in) throws DeserializerException {
		return (SoapMediaTypeCollection) this.xmlToObj(in, SoapMediaTypeCollection.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.SoapMediaTypeCollection}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.SoapMediaTypeCollection}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.SoapMediaTypeCollection}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SoapMediaTypeCollection readSoapMediaTypeCollectionFromString(String in) throws DeserializerException {
		return (SoapMediaTypeCollection) this.xmlToObj(in.getBytes(), SoapMediaTypeCollection.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: SoapMediaTypeDefaultMapping
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.protocol.manifest.SoapMediaTypeDefaultMapping}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.SoapMediaTypeDefaultMapping}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.SoapMediaTypeDefaultMapping}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SoapMediaTypeDefaultMapping readSoapMediaTypeDefaultMapping(String fileName) throws DeserializerException {
		return (SoapMediaTypeDefaultMapping) this.xmlToObj(fileName, SoapMediaTypeDefaultMapping.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.protocol.manifest.SoapMediaTypeDefaultMapping}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.SoapMediaTypeDefaultMapping}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.SoapMediaTypeDefaultMapping}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SoapMediaTypeDefaultMapping readSoapMediaTypeDefaultMapping(File file) throws DeserializerException {
		return (SoapMediaTypeDefaultMapping) this.xmlToObj(file, SoapMediaTypeDefaultMapping.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.SoapMediaTypeDefaultMapping}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.SoapMediaTypeDefaultMapping}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.SoapMediaTypeDefaultMapping}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SoapMediaTypeDefaultMapping readSoapMediaTypeDefaultMapping(InputStream in) throws DeserializerException {
		return (SoapMediaTypeDefaultMapping) this.xmlToObj(in, SoapMediaTypeDefaultMapping.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.SoapMediaTypeDefaultMapping}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.SoapMediaTypeDefaultMapping}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.SoapMediaTypeDefaultMapping}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SoapMediaTypeDefaultMapping readSoapMediaTypeDefaultMapping(byte[] in) throws DeserializerException {
		return (SoapMediaTypeDefaultMapping) this.xmlToObj(in, SoapMediaTypeDefaultMapping.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.SoapMediaTypeDefaultMapping}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.SoapMediaTypeDefaultMapping}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.SoapMediaTypeDefaultMapping}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SoapMediaTypeDefaultMapping readSoapMediaTypeDefaultMappingFromString(String in) throws DeserializerException {
		return (SoapMediaTypeDefaultMapping) this.xmlToObj(in.getBytes(), SoapMediaTypeDefaultMapping.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: SoapMediaTypeUndefinedMapping
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.protocol.manifest.SoapMediaTypeUndefinedMapping}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.SoapMediaTypeUndefinedMapping}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.SoapMediaTypeUndefinedMapping}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SoapMediaTypeUndefinedMapping readSoapMediaTypeUndefinedMapping(String fileName) throws DeserializerException {
		return (SoapMediaTypeUndefinedMapping) this.xmlToObj(fileName, SoapMediaTypeUndefinedMapping.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.protocol.manifest.SoapMediaTypeUndefinedMapping}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.SoapMediaTypeUndefinedMapping}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.SoapMediaTypeUndefinedMapping}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SoapMediaTypeUndefinedMapping readSoapMediaTypeUndefinedMapping(File file) throws DeserializerException {
		return (SoapMediaTypeUndefinedMapping) this.xmlToObj(file, SoapMediaTypeUndefinedMapping.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.SoapMediaTypeUndefinedMapping}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.SoapMediaTypeUndefinedMapping}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.SoapMediaTypeUndefinedMapping}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SoapMediaTypeUndefinedMapping readSoapMediaTypeUndefinedMapping(InputStream in) throws DeserializerException {
		return (SoapMediaTypeUndefinedMapping) this.xmlToObj(in, SoapMediaTypeUndefinedMapping.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.SoapMediaTypeUndefinedMapping}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.SoapMediaTypeUndefinedMapping}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.SoapMediaTypeUndefinedMapping}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SoapMediaTypeUndefinedMapping readSoapMediaTypeUndefinedMapping(byte[] in) throws DeserializerException {
		return (SoapMediaTypeUndefinedMapping) this.xmlToObj(in, SoapMediaTypeUndefinedMapping.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.SoapMediaTypeUndefinedMapping}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.SoapMediaTypeUndefinedMapping}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.SoapMediaTypeUndefinedMapping}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SoapMediaTypeUndefinedMapping readSoapMediaTypeUndefinedMappingFromString(String in) throws DeserializerException {
		return (SoapMediaTypeUndefinedMapping) this.xmlToObj(in.getBytes(), SoapMediaTypeUndefinedMapping.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: IntegrationErrorCode
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.protocol.manifest.IntegrationErrorCode}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.IntegrationErrorCode}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.IntegrationErrorCode}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IntegrationErrorCode readIntegrationErrorCode(String fileName) throws DeserializerException {
		return (IntegrationErrorCode) this.xmlToObj(fileName, IntegrationErrorCode.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.protocol.manifest.IntegrationErrorCode}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.IntegrationErrorCode}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.IntegrationErrorCode}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IntegrationErrorCode readIntegrationErrorCode(File file) throws DeserializerException {
		return (IntegrationErrorCode) this.xmlToObj(file, IntegrationErrorCode.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.IntegrationErrorCode}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.IntegrationErrorCode}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.IntegrationErrorCode}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IntegrationErrorCode readIntegrationErrorCode(InputStream in) throws DeserializerException {
		return (IntegrationErrorCode) this.xmlToObj(in, IntegrationErrorCode.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.IntegrationErrorCode}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.IntegrationErrorCode}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.IntegrationErrorCode}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IntegrationErrorCode readIntegrationErrorCode(byte[] in) throws DeserializerException {
		return (IntegrationErrorCode) this.xmlToObj(in, IntegrationErrorCode.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.IntegrationErrorCode}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.IntegrationErrorCode}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.IntegrationErrorCode}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IntegrationErrorCode readIntegrationErrorCodeFromString(String in) throws DeserializerException {
		return (IntegrationErrorCode) this.xmlToObj(in.getBytes(), IntegrationErrorCode.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: Integration
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.protocol.manifest.Integration}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.Integration}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.Integration}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Integration readIntegration(String fileName) throws DeserializerException {
		return (Integration) this.xmlToObj(fileName, Integration.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.protocol.manifest.Integration}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.Integration}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.Integration}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Integration readIntegration(File file) throws DeserializerException {
		return (Integration) this.xmlToObj(file, Integration.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.Integration}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.Integration}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.Integration}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Integration readIntegration(InputStream in) throws DeserializerException {
		return (Integration) this.xmlToObj(in, Integration.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.Integration}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.Integration}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.Integration}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Integration readIntegration(byte[] in) throws DeserializerException {
		return (Integration) this.xmlToObj(in, Integration.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.Integration}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.Integration}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.Integration}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Integration readIntegrationFromString(String in) throws DeserializerException {
		return (Integration) this.xmlToObj(in.getBytes(), Integration.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: RestConfiguration
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.protocol.manifest.RestConfiguration}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.RestConfiguration}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.RestConfiguration}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RestConfiguration readRestConfiguration(String fileName) throws DeserializerException {
		return (RestConfiguration) this.xmlToObj(fileName, RestConfiguration.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.protocol.manifest.RestConfiguration}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.RestConfiguration}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.RestConfiguration}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RestConfiguration readRestConfiguration(File file) throws DeserializerException {
		return (RestConfiguration) this.xmlToObj(file, RestConfiguration.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.RestConfiguration}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.RestConfiguration}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.RestConfiguration}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RestConfiguration readRestConfiguration(InputStream in) throws DeserializerException {
		return (RestConfiguration) this.xmlToObj(in, RestConfiguration.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.RestConfiguration}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.RestConfiguration}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.RestConfiguration}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RestConfiguration readRestConfiguration(byte[] in) throws DeserializerException {
		return (RestConfiguration) this.xmlToObj(in, RestConfiguration.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.RestConfiguration}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.RestConfiguration}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.RestConfiguration}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RestConfiguration readRestConfigurationFromString(String in) throws DeserializerException {
		return (RestConfiguration) this.xmlToObj(in.getBytes(), RestConfiguration.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: IntegrationErrorConfiguration
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.protocol.manifest.IntegrationErrorConfiguration}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.IntegrationErrorConfiguration}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.IntegrationErrorConfiguration}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IntegrationErrorConfiguration readIntegrationErrorConfiguration(String fileName) throws DeserializerException {
		return (IntegrationErrorConfiguration) this.xmlToObj(fileName, IntegrationErrorConfiguration.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.protocol.manifest.IntegrationErrorConfiguration}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.IntegrationErrorConfiguration}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.IntegrationErrorConfiguration}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IntegrationErrorConfiguration readIntegrationErrorConfiguration(File file) throws DeserializerException {
		return (IntegrationErrorConfiguration) this.xmlToObj(file, IntegrationErrorConfiguration.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.IntegrationErrorConfiguration}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.IntegrationErrorConfiguration}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.IntegrationErrorConfiguration}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IntegrationErrorConfiguration readIntegrationErrorConfiguration(InputStream in) throws DeserializerException {
		return (IntegrationErrorConfiguration) this.xmlToObj(in, IntegrationErrorConfiguration.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.IntegrationErrorConfiguration}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.IntegrationErrorConfiguration}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.IntegrationErrorConfiguration}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IntegrationErrorConfiguration readIntegrationErrorConfiguration(byte[] in) throws DeserializerException {
		return (IntegrationErrorConfiguration) this.xmlToObj(in, IntegrationErrorConfiguration.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.IntegrationErrorConfiguration}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.IntegrationErrorConfiguration}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.IntegrationErrorConfiguration}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IntegrationErrorConfiguration readIntegrationErrorConfigurationFromString(String in) throws DeserializerException {
		return (IntegrationErrorConfiguration) this.xmlToObj(in.getBytes(), IntegrationErrorConfiguration.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: RestMediaTypeCollection
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.protocol.manifest.RestMediaTypeCollection}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.RestMediaTypeCollection}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.RestMediaTypeCollection}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RestMediaTypeCollection readRestMediaTypeCollection(String fileName) throws DeserializerException {
		return (RestMediaTypeCollection) this.xmlToObj(fileName, RestMediaTypeCollection.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.protocol.manifest.RestMediaTypeCollection}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.RestMediaTypeCollection}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.RestMediaTypeCollection}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RestMediaTypeCollection readRestMediaTypeCollection(File file) throws DeserializerException {
		return (RestMediaTypeCollection) this.xmlToObj(file, RestMediaTypeCollection.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.RestMediaTypeCollection}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.RestMediaTypeCollection}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.RestMediaTypeCollection}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RestMediaTypeCollection readRestMediaTypeCollection(InputStream in) throws DeserializerException {
		return (RestMediaTypeCollection) this.xmlToObj(in, RestMediaTypeCollection.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.RestMediaTypeCollection}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.RestMediaTypeCollection}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.RestMediaTypeCollection}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RestMediaTypeCollection readRestMediaTypeCollection(byte[] in) throws DeserializerException {
		return (RestMediaTypeCollection) this.xmlToObj(in, RestMediaTypeCollection.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.RestMediaTypeCollection}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.RestMediaTypeCollection}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.RestMediaTypeCollection}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RestMediaTypeCollection readRestMediaTypeCollectionFromString(String in) throws DeserializerException {
		return (RestMediaTypeCollection) this.xmlToObj(in.getBytes(), RestMediaTypeCollection.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: InterfacesConfiguration
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.protocol.manifest.InterfacesConfiguration}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.InterfacesConfiguration}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.InterfacesConfiguration}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public InterfacesConfiguration readInterfacesConfiguration(String fileName) throws DeserializerException {
		return (InterfacesConfiguration) this.xmlToObj(fileName, InterfacesConfiguration.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.protocol.manifest.InterfacesConfiguration}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.InterfacesConfiguration}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.InterfacesConfiguration}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public InterfacesConfiguration readInterfacesConfiguration(File file) throws DeserializerException {
		return (InterfacesConfiguration) this.xmlToObj(file, InterfacesConfiguration.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.InterfacesConfiguration}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.InterfacesConfiguration}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.InterfacesConfiguration}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public InterfacesConfiguration readInterfacesConfiguration(InputStream in) throws DeserializerException {
		return (InterfacesConfiguration) this.xmlToObj(in, InterfacesConfiguration.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.InterfacesConfiguration}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.InterfacesConfiguration}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.InterfacesConfiguration}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public InterfacesConfiguration readInterfacesConfiguration(byte[] in) throws DeserializerException {
		return (InterfacesConfiguration) this.xmlToObj(in, InterfacesConfiguration.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.InterfacesConfiguration}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.InterfacesConfiguration}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.InterfacesConfiguration}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public InterfacesConfiguration readInterfacesConfigurationFromString(String in) throws DeserializerException {
		return (InterfacesConfiguration) this.xmlToObj(in.getBytes(), InterfacesConfiguration.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: RestCollaborationProfile
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.protocol.manifest.RestCollaborationProfile}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.RestCollaborationProfile}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.RestCollaborationProfile}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RestCollaborationProfile readRestCollaborationProfile(String fileName) throws DeserializerException {
		return (RestCollaborationProfile) this.xmlToObj(fileName, RestCollaborationProfile.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.protocol.manifest.RestCollaborationProfile}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.RestCollaborationProfile}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.RestCollaborationProfile}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RestCollaborationProfile readRestCollaborationProfile(File file) throws DeserializerException {
		return (RestCollaborationProfile) this.xmlToObj(file, RestCollaborationProfile.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.RestCollaborationProfile}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.RestCollaborationProfile}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.RestCollaborationProfile}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RestCollaborationProfile readRestCollaborationProfile(InputStream in) throws DeserializerException {
		return (RestCollaborationProfile) this.xmlToObj(in, RestCollaborationProfile.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.RestCollaborationProfile}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.RestCollaborationProfile}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.RestCollaborationProfile}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RestCollaborationProfile readRestCollaborationProfile(byte[] in) throws DeserializerException {
		return (RestCollaborationProfile) this.xmlToObj(in, RestCollaborationProfile.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.RestCollaborationProfile}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.RestCollaborationProfile}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.RestCollaborationProfile}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RestCollaborationProfile readRestCollaborationProfileFromString(String in) throws DeserializerException {
		return (RestCollaborationProfile) this.xmlToObj(in.getBytes(), RestCollaborationProfile.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: Functionality
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.protocol.manifest.Functionality}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.Functionality}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.Functionality}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Functionality readFunctionality(String fileName) throws DeserializerException {
		return (Functionality) this.xmlToObj(fileName, Functionality.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.protocol.manifest.Functionality}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.Functionality}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.Functionality}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Functionality readFunctionality(File file) throws DeserializerException {
		return (Functionality) this.xmlToObj(file, Functionality.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.Functionality}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.Functionality}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.Functionality}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Functionality readFunctionality(InputStream in) throws DeserializerException {
		return (Functionality) this.xmlToObj(in, Functionality.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.Functionality}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.Functionality}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.Functionality}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Functionality readFunctionality(byte[] in) throws DeserializerException {
		return (Functionality) this.xmlToObj(in, Functionality.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.Functionality}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.Functionality}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.Functionality}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Functionality readFunctionalityFromString(String in) throws DeserializerException {
		return (Functionality) this.xmlToObj(in.getBytes(), Functionality.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: IntegrationErrorCollection
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.protocol.manifest.IntegrationErrorCollection}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.IntegrationErrorCollection}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.IntegrationErrorCollection}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IntegrationErrorCollection readIntegrationErrorCollection(String fileName) throws DeserializerException {
		return (IntegrationErrorCollection) this.xmlToObj(fileName, IntegrationErrorCollection.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.protocol.manifest.IntegrationErrorCollection}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.IntegrationErrorCollection}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.IntegrationErrorCollection}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IntegrationErrorCollection readIntegrationErrorCollection(File file) throws DeserializerException {
		return (IntegrationErrorCollection) this.xmlToObj(file, IntegrationErrorCollection.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.IntegrationErrorCollection}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.IntegrationErrorCollection}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.IntegrationErrorCollection}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IntegrationErrorCollection readIntegrationErrorCollection(InputStream in) throws DeserializerException {
		return (IntegrationErrorCollection) this.xmlToObj(in, IntegrationErrorCollection.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.IntegrationErrorCollection}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.IntegrationErrorCollection}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.IntegrationErrorCollection}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IntegrationErrorCollection readIntegrationErrorCollection(byte[] in) throws DeserializerException {
		return (IntegrationErrorCollection) this.xmlToObj(in, IntegrationErrorCollection.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.IntegrationErrorCollection}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.IntegrationErrorCollection}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.IntegrationErrorCollection}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IntegrationErrorCollection readIntegrationErrorCollectionFromString(String in) throws DeserializerException {
		return (IntegrationErrorCollection) this.xmlToObj(in.getBytes(), IntegrationErrorCollection.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: SoapConfiguration
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.protocol.manifest.SoapConfiguration}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.SoapConfiguration}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.SoapConfiguration}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SoapConfiguration readSoapConfiguration(String fileName) throws DeserializerException {
		return (SoapConfiguration) this.xmlToObj(fileName, SoapConfiguration.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.protocol.manifest.SoapConfiguration}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.SoapConfiguration}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.SoapConfiguration}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SoapConfiguration readSoapConfiguration(File file) throws DeserializerException {
		return (SoapConfiguration) this.xmlToObj(file, SoapConfiguration.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.SoapConfiguration}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.SoapConfiguration}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.SoapConfiguration}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SoapConfiguration readSoapConfiguration(InputStream in) throws DeserializerException {
		return (SoapConfiguration) this.xmlToObj(in, SoapConfiguration.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.SoapConfiguration}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.SoapConfiguration}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.SoapConfiguration}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SoapConfiguration readSoapConfiguration(byte[] in) throws DeserializerException {
		return (SoapConfiguration) this.xmlToObj(in, SoapConfiguration.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.SoapConfiguration}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.SoapConfiguration}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.SoapConfiguration}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SoapConfiguration readSoapConfigurationFromString(String in) throws DeserializerException {
		return (SoapConfiguration) this.xmlToObj(in.getBytes(), SoapConfiguration.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: IntegrationError
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.protocol.manifest.IntegrationError}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.IntegrationError}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.IntegrationError}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IntegrationError readIntegrationError(String fileName) throws DeserializerException {
		return (IntegrationError) this.xmlToObj(fileName, IntegrationError.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.protocol.manifest.IntegrationError}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.IntegrationError}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.IntegrationError}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IntegrationError readIntegrationError(File file) throws DeserializerException {
		return (IntegrationError) this.xmlToObj(file, IntegrationError.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.IntegrationError}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.IntegrationError}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.IntegrationError}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IntegrationError readIntegrationError(InputStream in) throws DeserializerException {
		return (IntegrationError) this.xmlToObj(in, IntegrationError.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.IntegrationError}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.IntegrationError}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.IntegrationError}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IntegrationError readIntegrationError(byte[] in) throws DeserializerException {
		return (IntegrationError) this.xmlToObj(in, IntegrationError.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.IntegrationError}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.IntegrationError}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.IntegrationError}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IntegrationError readIntegrationErrorFromString(String in) throws DeserializerException {
		return (IntegrationError) this.xmlToObj(in.getBytes(), IntegrationError.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: IntegrationConfigurationElementName
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationElementName}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationElementName}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationElementName}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IntegrationConfigurationElementName readIntegrationConfigurationElementName(String fileName) throws DeserializerException {
		return (IntegrationConfigurationElementName) this.xmlToObj(fileName, IntegrationConfigurationElementName.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationElementName}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationElementName}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationElementName}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IntegrationConfigurationElementName readIntegrationConfigurationElementName(File file) throws DeserializerException {
		return (IntegrationConfigurationElementName) this.xmlToObj(file, IntegrationConfigurationElementName.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationElementName}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationElementName}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationElementName}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IntegrationConfigurationElementName readIntegrationConfigurationElementName(InputStream in) throws DeserializerException {
		return (IntegrationConfigurationElementName) this.xmlToObj(in, IntegrationConfigurationElementName.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationElementName}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationElementName}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationElementName}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IntegrationConfigurationElementName readIntegrationConfigurationElementName(byte[] in) throws DeserializerException {
		return (IntegrationConfigurationElementName) this.xmlToObj(in, IntegrationConfigurationElementName.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationElementName}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationElementName}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationElementName}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IntegrationConfigurationElementName readIntegrationConfigurationElementNameFromString(String in) throws DeserializerException {
		return (IntegrationConfigurationElementName) this.xmlToObj(in.getBytes(), IntegrationConfigurationElementName.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: IntegrationConfigurationName
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationName}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationName}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationName}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IntegrationConfigurationName readIntegrationConfigurationName(String fileName) throws DeserializerException {
		return (IntegrationConfigurationName) this.xmlToObj(fileName, IntegrationConfigurationName.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationName}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationName}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationName}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IntegrationConfigurationName readIntegrationConfigurationName(File file) throws DeserializerException {
		return (IntegrationConfigurationName) this.xmlToObj(file, IntegrationConfigurationName.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationName}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationName}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationName}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IntegrationConfigurationName readIntegrationConfigurationName(InputStream in) throws DeserializerException {
		return (IntegrationConfigurationName) this.xmlToObj(in, IntegrationConfigurationName.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationName}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationName}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationName}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IntegrationConfigurationName readIntegrationConfigurationName(byte[] in) throws DeserializerException {
		return (IntegrationConfigurationName) this.xmlToObj(in, IntegrationConfigurationName.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationName}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationName}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationName}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IntegrationConfigurationName readIntegrationConfigurationNameFromString(String in) throws DeserializerException {
		return (IntegrationConfigurationName) this.xmlToObj(in.getBytes(), IntegrationConfigurationName.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: IntegrationConfiguration
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.protocol.manifest.IntegrationConfiguration}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.IntegrationConfiguration}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.IntegrationConfiguration}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IntegrationConfiguration readIntegrationConfiguration(String fileName) throws DeserializerException {
		return (IntegrationConfiguration) this.xmlToObj(fileName, IntegrationConfiguration.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.protocol.manifest.IntegrationConfiguration}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.IntegrationConfiguration}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.IntegrationConfiguration}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IntegrationConfiguration readIntegrationConfiguration(File file) throws DeserializerException {
		return (IntegrationConfiguration) this.xmlToObj(file, IntegrationConfiguration.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.IntegrationConfiguration}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.IntegrationConfiguration}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.IntegrationConfiguration}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IntegrationConfiguration readIntegrationConfiguration(InputStream in) throws DeserializerException {
		return (IntegrationConfiguration) this.xmlToObj(in, IntegrationConfiguration.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.IntegrationConfiguration}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.IntegrationConfiguration}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.IntegrationConfiguration}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IntegrationConfiguration readIntegrationConfiguration(byte[] in) throws DeserializerException {
		return (IntegrationConfiguration) this.xmlToObj(in, IntegrationConfiguration.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.IntegrationConfiguration}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.IntegrationConfiguration}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.IntegrationConfiguration}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IntegrationConfiguration readIntegrationConfigurationFromString(String in) throws DeserializerException {
		return (IntegrationConfiguration) this.xmlToObj(in.getBytes(), IntegrationConfiguration.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: IntegrationConfigurationResourceIdentification
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationResourceIdentification}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationResourceIdentification}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationResourceIdentification}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IntegrationConfigurationResourceIdentification readIntegrationConfigurationResourceIdentification(String fileName) throws DeserializerException {
		return (IntegrationConfigurationResourceIdentification) this.xmlToObj(fileName, IntegrationConfigurationResourceIdentification.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationResourceIdentification}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationResourceIdentification}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationResourceIdentification}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IntegrationConfigurationResourceIdentification readIntegrationConfigurationResourceIdentification(File file) throws DeserializerException {
		return (IntegrationConfigurationResourceIdentification) this.xmlToObj(file, IntegrationConfigurationResourceIdentification.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationResourceIdentification}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationResourceIdentification}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationResourceIdentification}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IntegrationConfigurationResourceIdentification readIntegrationConfigurationResourceIdentification(InputStream in) throws DeserializerException {
		return (IntegrationConfigurationResourceIdentification) this.xmlToObj(in, IntegrationConfigurationResourceIdentification.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationResourceIdentification}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationResourceIdentification}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationResourceIdentification}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IntegrationConfigurationResourceIdentification readIntegrationConfigurationResourceIdentification(byte[] in) throws DeserializerException {
		return (IntegrationConfigurationResourceIdentification) this.xmlToObj(in, IntegrationConfigurationResourceIdentification.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationResourceIdentification}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationResourceIdentification}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationResourceIdentification}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IntegrationConfigurationResourceIdentification readIntegrationConfigurationResourceIdentificationFromString(String in) throws DeserializerException {
		return (IntegrationConfigurationResourceIdentification) this.xmlToObj(in.getBytes(), IntegrationConfigurationResourceIdentification.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: IntegrationConfigurationResourceIdentificationModes
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationResourceIdentificationModes}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationResourceIdentificationModes}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationResourceIdentificationModes}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IntegrationConfigurationResourceIdentificationModes readIntegrationConfigurationResourceIdentificationModes(String fileName) throws DeserializerException {
		return (IntegrationConfigurationResourceIdentificationModes) this.xmlToObj(fileName, IntegrationConfigurationResourceIdentificationModes.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationResourceIdentificationModes}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationResourceIdentificationModes}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationResourceIdentificationModes}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IntegrationConfigurationResourceIdentificationModes readIntegrationConfigurationResourceIdentificationModes(File file) throws DeserializerException {
		return (IntegrationConfigurationResourceIdentificationModes) this.xmlToObj(file, IntegrationConfigurationResourceIdentificationModes.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationResourceIdentificationModes}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationResourceIdentificationModes}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationResourceIdentificationModes}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IntegrationConfigurationResourceIdentificationModes readIntegrationConfigurationResourceIdentificationModes(InputStream in) throws DeserializerException {
		return (IntegrationConfigurationResourceIdentificationModes) this.xmlToObj(in, IntegrationConfigurationResourceIdentificationModes.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationResourceIdentificationModes}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationResourceIdentificationModes}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationResourceIdentificationModes}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IntegrationConfigurationResourceIdentificationModes readIntegrationConfigurationResourceIdentificationModes(byte[] in) throws DeserializerException {
		return (IntegrationConfigurationResourceIdentificationModes) this.xmlToObj(in, IntegrationConfigurationResourceIdentificationModes.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationResourceIdentificationModes}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationResourceIdentificationModes}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationResourceIdentificationModes}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IntegrationConfigurationResourceIdentificationModes readIntegrationConfigurationResourceIdentificationModesFromString(String in) throws DeserializerException {
		return (IntegrationConfigurationResourceIdentificationModes) this.xmlToObj(in.getBytes(), IntegrationConfigurationResourceIdentificationModes.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: IntegrationConfigurationResourceIdentificationSpecificResource
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationResourceIdentificationSpecificResource}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationResourceIdentificationSpecificResource}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationResourceIdentificationSpecificResource}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IntegrationConfigurationResourceIdentificationSpecificResource readIntegrationConfigurationResourceIdentificationSpecificResource(String fileName) throws DeserializerException {
		return (IntegrationConfigurationResourceIdentificationSpecificResource) this.xmlToObj(fileName, IntegrationConfigurationResourceIdentificationSpecificResource.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationResourceIdentificationSpecificResource}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationResourceIdentificationSpecificResource}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationResourceIdentificationSpecificResource}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IntegrationConfigurationResourceIdentificationSpecificResource readIntegrationConfigurationResourceIdentificationSpecificResource(File file) throws DeserializerException {
		return (IntegrationConfigurationResourceIdentificationSpecificResource) this.xmlToObj(file, IntegrationConfigurationResourceIdentificationSpecificResource.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationResourceIdentificationSpecificResource}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationResourceIdentificationSpecificResource}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationResourceIdentificationSpecificResource}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IntegrationConfigurationResourceIdentificationSpecificResource readIntegrationConfigurationResourceIdentificationSpecificResource(InputStream in) throws DeserializerException {
		return (IntegrationConfigurationResourceIdentificationSpecificResource) this.xmlToObj(in, IntegrationConfigurationResourceIdentificationSpecificResource.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationResourceIdentificationSpecificResource}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationResourceIdentificationSpecificResource}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationResourceIdentificationSpecificResource}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IntegrationConfigurationResourceIdentificationSpecificResource readIntegrationConfigurationResourceIdentificationSpecificResource(byte[] in) throws DeserializerException {
		return (IntegrationConfigurationResourceIdentificationSpecificResource) this.xmlToObj(in, IntegrationConfigurationResourceIdentificationSpecificResource.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationResourceIdentificationSpecificResource}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationResourceIdentificationSpecificResource}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationResourceIdentificationSpecificResource}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IntegrationConfigurationResourceIdentificationSpecificResource readIntegrationConfigurationResourceIdentificationSpecificResourceFromString(String in) throws DeserializerException {
		return (IntegrationConfigurationResourceIdentificationSpecificResource) this.xmlToObj(in.getBytes(), IntegrationConfigurationResourceIdentificationSpecificResource.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: SoapHeaderBypassMustUnderstandHeader
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.protocol.manifest.SoapHeaderBypassMustUnderstandHeader}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.SoapHeaderBypassMustUnderstandHeader}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.SoapHeaderBypassMustUnderstandHeader}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SoapHeaderBypassMustUnderstandHeader readSoapHeaderBypassMustUnderstandHeader(String fileName) throws DeserializerException {
		return (SoapHeaderBypassMustUnderstandHeader) this.xmlToObj(fileName, SoapHeaderBypassMustUnderstandHeader.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.protocol.manifest.SoapHeaderBypassMustUnderstandHeader}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.SoapHeaderBypassMustUnderstandHeader}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.SoapHeaderBypassMustUnderstandHeader}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SoapHeaderBypassMustUnderstandHeader readSoapHeaderBypassMustUnderstandHeader(File file) throws DeserializerException {
		return (SoapHeaderBypassMustUnderstandHeader) this.xmlToObj(file, SoapHeaderBypassMustUnderstandHeader.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.SoapHeaderBypassMustUnderstandHeader}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.SoapHeaderBypassMustUnderstandHeader}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.SoapHeaderBypassMustUnderstandHeader}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SoapHeaderBypassMustUnderstandHeader readSoapHeaderBypassMustUnderstandHeader(InputStream in) throws DeserializerException {
		return (SoapHeaderBypassMustUnderstandHeader) this.xmlToObj(in, SoapHeaderBypassMustUnderstandHeader.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.SoapHeaderBypassMustUnderstandHeader}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.SoapHeaderBypassMustUnderstandHeader}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.SoapHeaderBypassMustUnderstandHeader}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SoapHeaderBypassMustUnderstandHeader readSoapHeaderBypassMustUnderstandHeader(byte[] in) throws DeserializerException {
		return (SoapHeaderBypassMustUnderstandHeader) this.xmlToObj(in, SoapHeaderBypassMustUnderstandHeader.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.SoapHeaderBypassMustUnderstandHeader}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.SoapHeaderBypassMustUnderstandHeader}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.SoapHeaderBypassMustUnderstandHeader}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SoapHeaderBypassMustUnderstandHeader readSoapHeaderBypassMustUnderstandHeaderFromString(String in) throws DeserializerException {
		return (SoapHeaderBypassMustUnderstandHeader) this.xmlToObj(in.getBytes(), SoapHeaderBypassMustUnderstandHeader.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: SoapHeaderBypassMustUnderstand
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.protocol.manifest.SoapHeaderBypassMustUnderstand}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.SoapHeaderBypassMustUnderstand}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.SoapHeaderBypassMustUnderstand}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SoapHeaderBypassMustUnderstand readSoapHeaderBypassMustUnderstand(String fileName) throws DeserializerException {
		return (SoapHeaderBypassMustUnderstand) this.xmlToObj(fileName, SoapHeaderBypassMustUnderstand.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.protocol.manifest.SoapHeaderBypassMustUnderstand}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.SoapHeaderBypassMustUnderstand}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.SoapHeaderBypassMustUnderstand}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SoapHeaderBypassMustUnderstand readSoapHeaderBypassMustUnderstand(File file) throws DeserializerException {
		return (SoapHeaderBypassMustUnderstand) this.xmlToObj(file, SoapHeaderBypassMustUnderstand.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.SoapHeaderBypassMustUnderstand}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.SoapHeaderBypassMustUnderstand}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.SoapHeaderBypassMustUnderstand}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SoapHeaderBypassMustUnderstand readSoapHeaderBypassMustUnderstand(InputStream in) throws DeserializerException {
		return (SoapHeaderBypassMustUnderstand) this.xmlToObj(in, SoapHeaderBypassMustUnderstand.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.SoapHeaderBypassMustUnderstand}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.SoapHeaderBypassMustUnderstand}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.SoapHeaderBypassMustUnderstand}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SoapHeaderBypassMustUnderstand readSoapHeaderBypassMustUnderstand(byte[] in) throws DeserializerException {
		return (SoapHeaderBypassMustUnderstand) this.xmlToObj(in, SoapHeaderBypassMustUnderstand.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.SoapHeaderBypassMustUnderstand}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.SoapHeaderBypassMustUnderstand}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.SoapHeaderBypassMustUnderstand}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SoapHeaderBypassMustUnderstand readSoapHeaderBypassMustUnderstandFromString(String in) throws DeserializerException {
		return (SoapHeaderBypassMustUnderstand) this.xmlToObj(in.getBytes(), SoapHeaderBypassMustUnderstand.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: Organization
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.protocol.manifest.Organization}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.Organization}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.Organization}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Organization readOrganization(String fileName) throws DeserializerException {
		return (Organization) this.xmlToObj(fileName, Organization.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.protocol.manifest.Organization}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.Organization}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.Organization}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Organization readOrganization(File file) throws DeserializerException {
		return (Organization) this.xmlToObj(file, Organization.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.Organization}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.Organization}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.Organization}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Organization readOrganization(InputStream in) throws DeserializerException {
		return (Organization) this.xmlToObj(in, Organization.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.Organization}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.Organization}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.Organization}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Organization readOrganization(byte[] in) throws DeserializerException {
		return (Organization) this.xmlToObj(in, Organization.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.Organization}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.Organization}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.Organization}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Organization readOrganizationFromString(String in) throws DeserializerException {
		return (Organization) this.xmlToObj(in.getBytes(), Organization.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: Versions
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.protocol.manifest.Versions}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.Versions}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.Versions}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Versions readVersions(String fileName) throws DeserializerException {
		return (Versions) this.xmlToObj(fileName, Versions.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.protocol.manifest.Versions}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.Versions}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.Versions}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Versions readVersions(File file) throws DeserializerException {
		return (Versions) this.xmlToObj(file, Versions.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.Versions}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.Versions}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.Versions}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Versions readVersions(InputStream in) throws DeserializerException {
		return (Versions) this.xmlToObj(in, Versions.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.Versions}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.Versions}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.Versions}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Versions readVersions(byte[] in) throws DeserializerException {
		return (Versions) this.xmlToObj(in, Versions.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.Versions}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.Versions}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.Versions}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Versions readVersionsFromString(String in) throws DeserializerException {
		return (Versions) this.xmlToObj(in.getBytes(), Versions.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: SubContextMapping
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.protocol.manifest.SubContextMapping}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.SubContextMapping}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.SubContextMapping}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SubContextMapping readSubContextMapping(String fileName) throws DeserializerException {
		return (SubContextMapping) this.xmlToObj(fileName, SubContextMapping.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.protocol.manifest.SubContextMapping}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.SubContextMapping}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.SubContextMapping}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SubContextMapping readSubContextMapping(File file) throws DeserializerException {
		return (SubContextMapping) this.xmlToObj(file, SubContextMapping.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.SubContextMapping}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.SubContextMapping}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.SubContextMapping}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SubContextMapping readSubContextMapping(InputStream in) throws DeserializerException {
		return (SubContextMapping) this.xmlToObj(in, SubContextMapping.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.SubContextMapping}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.SubContextMapping}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.SubContextMapping}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SubContextMapping readSubContextMapping(byte[] in) throws DeserializerException {
		return (SubContextMapping) this.xmlToObj(in, SubContextMapping.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.SubContextMapping}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.SubContextMapping}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.SubContextMapping}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SubContextMapping readSubContextMappingFromString(String in) throws DeserializerException {
		return (SubContextMapping) this.xmlToObj(in.getBytes(), SubContextMapping.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: WebEmptyContext
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.protocol.manifest.WebEmptyContext}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.WebEmptyContext}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.WebEmptyContext}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public WebEmptyContext readWebEmptyContext(String fileName) throws DeserializerException {
		return (WebEmptyContext) this.xmlToObj(fileName, WebEmptyContext.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.protocol.manifest.WebEmptyContext}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.WebEmptyContext}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.WebEmptyContext}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public WebEmptyContext readWebEmptyContext(File file) throws DeserializerException {
		return (WebEmptyContext) this.xmlToObj(file, WebEmptyContext.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.WebEmptyContext}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.WebEmptyContext}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.WebEmptyContext}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public WebEmptyContext readWebEmptyContext(InputStream in) throws DeserializerException {
		return (WebEmptyContext) this.xmlToObj(in, WebEmptyContext.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.WebEmptyContext}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.WebEmptyContext}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.WebEmptyContext}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public WebEmptyContext readWebEmptyContext(byte[] in) throws DeserializerException {
		return (WebEmptyContext) this.xmlToObj(in, WebEmptyContext.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.WebEmptyContext}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.WebEmptyContext}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.WebEmptyContext}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public WebEmptyContext readWebEmptyContextFromString(String in) throws DeserializerException {
		return (WebEmptyContext) this.xmlToObj(in.getBytes(), WebEmptyContext.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: EmptySubContextMapping
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.protocol.manifest.EmptySubContextMapping}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.EmptySubContextMapping}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.EmptySubContextMapping}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public EmptySubContextMapping readEmptySubContextMapping(String fileName) throws DeserializerException {
		return (EmptySubContextMapping) this.xmlToObj(fileName, EmptySubContextMapping.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.protocol.manifest.EmptySubContextMapping}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.EmptySubContextMapping}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.EmptySubContextMapping}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public EmptySubContextMapping readEmptySubContextMapping(File file) throws DeserializerException {
		return (EmptySubContextMapping) this.xmlToObj(file, EmptySubContextMapping.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.EmptySubContextMapping}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.EmptySubContextMapping}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.EmptySubContextMapping}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public EmptySubContextMapping readEmptySubContextMapping(InputStream in) throws DeserializerException {
		return (EmptySubContextMapping) this.xmlToObj(in, EmptySubContextMapping.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.EmptySubContextMapping}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.EmptySubContextMapping}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.EmptySubContextMapping}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public EmptySubContextMapping readEmptySubContextMapping(byte[] in) throws DeserializerException {
		return (EmptySubContextMapping) this.xmlToObj(in, EmptySubContextMapping.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.EmptySubContextMapping}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.EmptySubContextMapping}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.EmptySubContextMapping}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public EmptySubContextMapping readEmptySubContextMappingFromString(String in) throws DeserializerException {
		return (EmptySubContextMapping) this.xmlToObj(in.getBytes(), EmptySubContextMapping.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: RestMediaTypeMapping
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.protocol.manifest.RestMediaTypeMapping}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.RestMediaTypeMapping}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.RestMediaTypeMapping}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RestMediaTypeMapping readRestMediaTypeMapping(String fileName) throws DeserializerException {
		return (RestMediaTypeMapping) this.xmlToObj(fileName, RestMediaTypeMapping.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.protocol.manifest.RestMediaTypeMapping}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.RestMediaTypeMapping}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.RestMediaTypeMapping}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RestMediaTypeMapping readRestMediaTypeMapping(File file) throws DeserializerException {
		return (RestMediaTypeMapping) this.xmlToObj(file, RestMediaTypeMapping.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.RestMediaTypeMapping}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.RestMediaTypeMapping}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.RestMediaTypeMapping}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RestMediaTypeMapping readRestMediaTypeMapping(InputStream in) throws DeserializerException {
		return (RestMediaTypeMapping) this.xmlToObj(in, RestMediaTypeMapping.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.RestMediaTypeMapping}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.RestMediaTypeMapping}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.RestMediaTypeMapping}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RestMediaTypeMapping readRestMediaTypeMapping(byte[] in) throws DeserializerException {
		return (RestMediaTypeMapping) this.xmlToObj(in, RestMediaTypeMapping.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.RestMediaTypeMapping}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.RestMediaTypeMapping}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.RestMediaTypeMapping}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RestMediaTypeMapping readRestMediaTypeMappingFromString(String in) throws DeserializerException {
		return (RestMediaTypeMapping) this.xmlToObj(in.getBytes(), RestMediaTypeMapping.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: OrganizationTypes
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.protocol.manifest.OrganizationTypes}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.OrganizationTypes}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.OrganizationTypes}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public OrganizationTypes readOrganizationTypes(String fileName) throws DeserializerException {
		return (OrganizationTypes) this.xmlToObj(fileName, OrganizationTypes.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.protocol.manifest.OrganizationTypes}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.OrganizationTypes}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.OrganizationTypes}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public OrganizationTypes readOrganizationTypes(File file) throws DeserializerException {
		return (OrganizationTypes) this.xmlToObj(file, OrganizationTypes.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.OrganizationTypes}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.OrganizationTypes}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.OrganizationTypes}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public OrganizationTypes readOrganizationTypes(InputStream in) throws DeserializerException {
		return (OrganizationTypes) this.xmlToObj(in, OrganizationTypes.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.OrganizationTypes}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.OrganizationTypes}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.OrganizationTypes}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public OrganizationTypes readOrganizationTypes(byte[] in) throws DeserializerException {
		return (OrganizationTypes) this.xmlToObj(in, OrganizationTypes.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.OrganizationTypes}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.OrganizationTypes}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.OrganizationTypes}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public OrganizationTypes readOrganizationTypesFromString(String in) throws DeserializerException {
		return (OrganizationTypes) this.xmlToObj(in.getBytes(), OrganizationTypes.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: InterfaceConfiguration
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.protocol.manifest.InterfaceConfiguration}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.InterfaceConfiguration}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.InterfaceConfiguration}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public InterfaceConfiguration readInterfaceConfiguration(String fileName) throws DeserializerException {
		return (InterfaceConfiguration) this.xmlToObj(fileName, InterfaceConfiguration.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.protocol.manifest.InterfaceConfiguration}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.InterfaceConfiguration}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.InterfaceConfiguration}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public InterfaceConfiguration readInterfaceConfiguration(File file) throws DeserializerException {
		return (InterfaceConfiguration) this.xmlToObj(file, InterfaceConfiguration.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.InterfaceConfiguration}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.InterfaceConfiguration}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.InterfaceConfiguration}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public InterfaceConfiguration readInterfaceConfiguration(InputStream in) throws DeserializerException {
		return (InterfaceConfiguration) this.xmlToObj(in, InterfaceConfiguration.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.InterfaceConfiguration}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.InterfaceConfiguration}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.InterfaceConfiguration}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public InterfaceConfiguration readInterfaceConfiguration(byte[] in) throws DeserializerException {
		return (InterfaceConfiguration) this.xmlToObj(in, InterfaceConfiguration.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.InterfaceConfiguration}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.InterfaceConfiguration}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.InterfaceConfiguration}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public InterfaceConfiguration readInterfaceConfigurationFromString(String in) throws DeserializerException {
		return (InterfaceConfiguration) this.xmlToObj(in.getBytes(), InterfaceConfiguration.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: Version
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.protocol.manifest.Version}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.Version}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.Version}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Version readVersion(String fileName) throws DeserializerException {
		return (Version) this.xmlToObj(fileName, Version.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.protocol.manifest.Version}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.Version}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.Version}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Version readVersion(File file) throws DeserializerException {
		return (Version) this.xmlToObj(file, Version.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.Version}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.Version}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.Version}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Version readVersion(InputStream in) throws DeserializerException {
		return (Version) this.xmlToObj(in, Version.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.Version}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.Version}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.Version}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Version readVersion(byte[] in) throws DeserializerException {
		return (Version) this.xmlToObj(in, Version.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.Version}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.Version}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.Version}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Version readVersionFromString(String in) throws DeserializerException {
		return (Version) this.xmlToObj(in.getBytes(), Version.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: CollaborationProfile
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.protocol.manifest.CollaborationProfile}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.CollaborationProfile}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.CollaborationProfile}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public CollaborationProfile readCollaborationProfile(String fileName) throws DeserializerException {
		return (CollaborationProfile) this.xmlToObj(fileName, CollaborationProfile.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.protocol.manifest.CollaborationProfile}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.CollaborationProfile}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.CollaborationProfile}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public CollaborationProfile readCollaborationProfile(File file) throws DeserializerException {
		return (CollaborationProfile) this.xmlToObj(file, CollaborationProfile.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.CollaborationProfile}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.CollaborationProfile}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.CollaborationProfile}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public CollaborationProfile readCollaborationProfile(InputStream in) throws DeserializerException {
		return (CollaborationProfile) this.xmlToObj(in, CollaborationProfile.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.CollaborationProfile}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.CollaborationProfile}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.CollaborationProfile}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public CollaborationProfile readCollaborationProfile(byte[] in) throws DeserializerException {
		return (CollaborationProfile) this.xmlToObj(in, CollaborationProfile.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.CollaborationProfile}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.CollaborationProfile}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.CollaborationProfile}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public CollaborationProfile readCollaborationProfileFromString(String in) throws DeserializerException {
		return (CollaborationProfile) this.xmlToObj(in.getBytes(), CollaborationProfile.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: ServiceType
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.protocol.manifest.ServiceType}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.ServiceType}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.ServiceType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ServiceType readServiceType(String fileName) throws DeserializerException {
		return (ServiceType) this.xmlToObj(fileName, ServiceType.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.protocol.manifest.ServiceType}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.ServiceType}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.ServiceType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ServiceType readServiceType(File file) throws DeserializerException {
		return (ServiceType) this.xmlToObj(file, ServiceType.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.ServiceType}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.ServiceType}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.ServiceType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ServiceType readServiceType(InputStream in) throws DeserializerException {
		return (ServiceType) this.xmlToObj(in, ServiceType.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.ServiceType}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.ServiceType}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.ServiceType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ServiceType readServiceType(byte[] in) throws DeserializerException {
		return (ServiceType) this.xmlToObj(in, ServiceType.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.ServiceType}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.ServiceType}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.ServiceType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ServiceType readServiceTypeFromString(String in) throws DeserializerException {
		return (ServiceType) this.xmlToObj(in.getBytes(), ServiceType.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: transaction
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.protocol.manifest.Transaction}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.Transaction}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.Transaction}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Transaction readTransaction(String fileName) throws DeserializerException {
		return (Transaction) this.xmlToObj(fileName, Transaction.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.protocol.manifest.Transaction}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.Transaction}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.Transaction}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Transaction readTransaction(File file) throws DeserializerException {
		return (Transaction) this.xmlToObj(file, Transaction.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.Transaction}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.Transaction}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.Transaction}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Transaction readTransaction(InputStream in) throws DeserializerException {
		return (Transaction) this.xmlToObj(in, Transaction.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.Transaction}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.Transaction}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.Transaction}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Transaction readTransaction(byte[] in) throws DeserializerException {
		return (Transaction) this.xmlToObj(in, Transaction.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.Transaction}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.Transaction}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.Transaction}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Transaction readTransactionFromString(String in) throws DeserializerException {
		return (Transaction) this.xmlToObj(in.getBytes(), Transaction.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: IntegrationConfigurationResourceIdentificationMode
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationResourceIdentificationMode}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationResourceIdentificationMode}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationResourceIdentificationMode}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IntegrationConfigurationResourceIdentificationMode readIntegrationConfigurationResourceIdentificationMode(String fileName) throws DeserializerException {
		return (IntegrationConfigurationResourceIdentificationMode) this.xmlToObj(fileName, IntegrationConfigurationResourceIdentificationMode.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationResourceIdentificationMode}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationResourceIdentificationMode}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationResourceIdentificationMode}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IntegrationConfigurationResourceIdentificationMode readIntegrationConfigurationResourceIdentificationMode(File file) throws DeserializerException {
		return (IntegrationConfigurationResourceIdentificationMode) this.xmlToObj(file, IntegrationConfigurationResourceIdentificationMode.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationResourceIdentificationMode}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationResourceIdentificationMode}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationResourceIdentificationMode}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IntegrationConfigurationResourceIdentificationMode readIntegrationConfigurationResourceIdentificationMode(InputStream in) throws DeserializerException {
		return (IntegrationConfigurationResourceIdentificationMode) this.xmlToObj(in, IntegrationConfigurationResourceIdentificationMode.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationResourceIdentificationMode}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationResourceIdentificationMode}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationResourceIdentificationMode}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IntegrationConfigurationResourceIdentificationMode readIntegrationConfigurationResourceIdentificationMode(byte[] in) throws DeserializerException {
		return (IntegrationConfigurationResourceIdentificationMode) this.xmlToObj(in, IntegrationConfigurationResourceIdentificationMode.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationResourceIdentificationMode}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationResourceIdentificationMode}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationResourceIdentificationMode}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IntegrationConfigurationResourceIdentificationMode readIntegrationConfigurationResourceIdentificationModeFromString(String in) throws DeserializerException {
		return (IntegrationConfigurationResourceIdentificationMode) this.xmlToObj(in.getBytes(), IntegrationConfigurationResourceIdentificationMode.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: RFC7807
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.protocol.manifest.RFC7807}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.RFC7807}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.RFC7807}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RFC7807 readRFC7807(String fileName) throws DeserializerException {
		return (RFC7807) this.xmlToObj(fileName, RFC7807.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.protocol.manifest.RFC7807}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.RFC7807}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.RFC7807}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RFC7807 readRFC7807(File file) throws DeserializerException {
		return (RFC7807) this.xmlToObj(file, RFC7807.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.RFC7807}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.RFC7807}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.RFC7807}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RFC7807 readRFC7807(InputStream in) throws DeserializerException {
		return (RFC7807) this.xmlToObj(in, RFC7807.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.RFC7807}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.RFC7807}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.RFC7807}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RFC7807 readRFC7807(byte[] in) throws DeserializerException {
		return (RFC7807) this.xmlToObj(in, RFC7807.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.RFC7807}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.RFC7807}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.RFC7807}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RFC7807 readRFC7807FromString(String in) throws DeserializerException {
		return (RFC7807) this.xmlToObj(in.getBytes(), RFC7807.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: RestMediaTypeDefaultMapping
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.protocol.manifest.RestMediaTypeDefaultMapping}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.RestMediaTypeDefaultMapping}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.RestMediaTypeDefaultMapping}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RestMediaTypeDefaultMapping readRestMediaTypeDefaultMapping(String fileName) throws DeserializerException {
		return (RestMediaTypeDefaultMapping) this.xmlToObj(fileName, RestMediaTypeDefaultMapping.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.protocol.manifest.RestMediaTypeDefaultMapping}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.RestMediaTypeDefaultMapping}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.RestMediaTypeDefaultMapping}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RestMediaTypeDefaultMapping readRestMediaTypeDefaultMapping(File file) throws DeserializerException {
		return (RestMediaTypeDefaultMapping) this.xmlToObj(file, RestMediaTypeDefaultMapping.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.RestMediaTypeDefaultMapping}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.RestMediaTypeDefaultMapping}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.RestMediaTypeDefaultMapping}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RestMediaTypeDefaultMapping readRestMediaTypeDefaultMapping(InputStream in) throws DeserializerException {
		return (RestMediaTypeDefaultMapping) this.xmlToObj(in, RestMediaTypeDefaultMapping.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.RestMediaTypeDefaultMapping}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.RestMediaTypeDefaultMapping}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.RestMediaTypeDefaultMapping}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RestMediaTypeDefaultMapping readRestMediaTypeDefaultMapping(byte[] in) throws DeserializerException {
		return (RestMediaTypeDefaultMapping) this.xmlToObj(in, RestMediaTypeDefaultMapping.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.RestMediaTypeDefaultMapping}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.RestMediaTypeDefaultMapping}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.RestMediaTypeDefaultMapping}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RestMediaTypeDefaultMapping readRestMediaTypeDefaultMappingFromString(String in) throws DeserializerException {
		return (RestMediaTypeDefaultMapping) this.xmlToObj(in.getBytes(), RestMediaTypeDefaultMapping.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: RestMediaTypeUndefinedMapping
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.protocol.manifest.RestMediaTypeUndefinedMapping}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.RestMediaTypeUndefinedMapping}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.RestMediaTypeUndefinedMapping}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RestMediaTypeUndefinedMapping readRestMediaTypeUndefinedMapping(String fileName) throws DeserializerException {
		return (RestMediaTypeUndefinedMapping) this.xmlToObj(fileName, RestMediaTypeUndefinedMapping.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.protocol.manifest.RestMediaTypeUndefinedMapping}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.RestMediaTypeUndefinedMapping}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.RestMediaTypeUndefinedMapping}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RestMediaTypeUndefinedMapping readRestMediaTypeUndefinedMapping(File file) throws DeserializerException {
		return (RestMediaTypeUndefinedMapping) this.xmlToObj(file, RestMediaTypeUndefinedMapping.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.RestMediaTypeUndefinedMapping}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.RestMediaTypeUndefinedMapping}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.RestMediaTypeUndefinedMapping}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RestMediaTypeUndefinedMapping readRestMediaTypeUndefinedMapping(InputStream in) throws DeserializerException {
		return (RestMediaTypeUndefinedMapping) this.xmlToObj(in, RestMediaTypeUndefinedMapping.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.RestMediaTypeUndefinedMapping}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.RestMediaTypeUndefinedMapping}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.RestMediaTypeUndefinedMapping}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RestMediaTypeUndefinedMapping readRestMediaTypeUndefinedMapping(byte[] in) throws DeserializerException {
		return (RestMediaTypeUndefinedMapping) this.xmlToObj(in, RestMediaTypeUndefinedMapping.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.RestMediaTypeUndefinedMapping}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.RestMediaTypeUndefinedMapping}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.RestMediaTypeUndefinedMapping}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RestMediaTypeUndefinedMapping readRestMediaTypeUndefinedMappingFromString(String in) throws DeserializerException {
		return (RestMediaTypeUndefinedMapping) this.xmlToObj(in.getBytes(), RestMediaTypeUndefinedMapping.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: OrganizationType
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.protocol.manifest.OrganizationType}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.OrganizationType}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.OrganizationType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public OrganizationType readOrganizationType(String fileName) throws DeserializerException {
		return (OrganizationType) this.xmlToObj(fileName, OrganizationType.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.protocol.manifest.OrganizationType}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.OrganizationType}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.OrganizationType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public OrganizationType readOrganizationType(File file) throws DeserializerException {
		return (OrganizationType) this.xmlToObj(file, OrganizationType.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.OrganizationType}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.OrganizationType}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.OrganizationType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public OrganizationType readOrganizationType(InputStream in) throws DeserializerException {
		return (OrganizationType) this.xmlToObj(in, OrganizationType.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.OrganizationType}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.OrganizationType}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.OrganizationType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public OrganizationType readOrganizationType(byte[] in) throws DeserializerException {
		return (OrganizationType) this.xmlToObj(in, OrganizationType.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.OrganizationType}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.OrganizationType}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.OrganizationType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public OrganizationType readOrganizationTypeFromString(String in) throws DeserializerException {
		return (OrganizationType) this.xmlToObj(in.getBytes(), OrganizationType.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: Context
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.protocol.manifest.Context}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.Context}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.Context}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Context readContext(String fileName) throws DeserializerException {
		return (Context) this.xmlToObj(fileName, Context.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.protocol.manifest.Context}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.Context}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.Context}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Context readContext(File file) throws DeserializerException {
		return (Context) this.xmlToObj(file, Context.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.Context}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.Context}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.Context}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Context readContext(InputStream in) throws DeserializerException {
		return (Context) this.xmlToObj(in, Context.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.Context}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.Context}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.Context}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Context readContext(byte[] in) throws DeserializerException {
		return (Context) this.xmlToObj(in, Context.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.Context}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.Context}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.Context}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Context readContextFromString(String in) throws DeserializerException {
		return (Context) this.xmlToObj(in.getBytes(), Context.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: DefaultIntegrationError
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.protocol.manifest.DefaultIntegrationError}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.DefaultIntegrationError}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.DefaultIntegrationError}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DefaultIntegrationError readDefaultIntegrationError(String fileName) throws DeserializerException {
		return (DefaultIntegrationError) this.xmlToObj(fileName, DefaultIntegrationError.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.protocol.manifest.DefaultIntegrationError}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.DefaultIntegrationError}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.DefaultIntegrationError}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DefaultIntegrationError readDefaultIntegrationError(File file) throws DeserializerException {
		return (DefaultIntegrationError) this.xmlToObj(file, DefaultIntegrationError.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.DefaultIntegrationError}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.DefaultIntegrationError}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.DefaultIntegrationError}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DefaultIntegrationError readDefaultIntegrationError(InputStream in) throws DeserializerException {
		return (DefaultIntegrationError) this.xmlToObj(in, DefaultIntegrationError.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.DefaultIntegrationError}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.DefaultIntegrationError}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.DefaultIntegrationError}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DefaultIntegrationError readDefaultIntegrationError(byte[] in) throws DeserializerException {
		return (DefaultIntegrationError) this.xmlToObj(in, DefaultIntegrationError.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.DefaultIntegrationError}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.DefaultIntegrationError}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.DefaultIntegrationError}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DefaultIntegrationError readDefaultIntegrationErrorFromString(String in) throws DeserializerException {
		return (DefaultIntegrationError) this.xmlToObj(in.getBytes(), DefaultIntegrationError.class);
	}	
	
	
	

}
