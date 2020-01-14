/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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

import org.openspcoop2.generic_project.exception.SerializerException;
import org.openspcoop2.utils.beans.WriteToSerializerType;
import org.openspcoop2.utils.xml.JaxbUtils;

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

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.File;
import java.lang.reflect.Method;

import javax.xml.bind.JAXBElement;

/**     
 * XML Serializer of beans
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class AbstractSerializer {


	protected abstract WriteToSerializerType getType();
	
	protected void _objToXml(OutputStream out, Class<?> c, Object object,
			boolean prettyPrint) throws Exception {
		if(object instanceof JAXBElement){
			// solo per il tipo WriteToSerializerType.JAXB
			JaxbUtils.objToXml(out, c, object, prettyPrint);
		}else{
			Method m = c.getMethod("writeTo", OutputStream.class, WriteToSerializerType.class);
			m.invoke(object, out, this.getType());
		}
	}
	
	protected void objToXml(OutputStream out,Class<?> c,Object object,boolean prettyPrint) throws SerializerException{
		try{
			this._objToXml(out, c, object, prettyPrint);
		}catch(Exception e){
			throw new SerializerException(e.getMessage(), e);
		}
		finally{
			try{
				out.flush();
			}catch(Exception e){}
		}
	}
	protected void objToXml(String fileName,Class<?> c,Object object,boolean prettyPrint) throws SerializerException{
		try{
			this.objToXml(new File(fileName), c, object, prettyPrint);
		}catch(Exception e){
			throw new SerializerException(e.getMessage(), e);
		}
	}
	protected void objToXml(File file,Class<?> c,Object object,boolean prettyPrint) throws SerializerException{
		FileOutputStream fout = null;
		try{
			fout = new FileOutputStream(file);
			this._objToXml(fout, c, object, prettyPrint);
		}catch(Exception e){
			throw new SerializerException(e.getMessage(), e);
		}
		finally{
			try{
				fout.flush();
			}catch(Exception e){}
			try{
				fout.close();
			}catch(Exception e){}
		}
	}
	protected ByteArrayOutputStream objToXml(Class<?> c,Object object,boolean prettyPrint) throws SerializerException{
		ByteArrayOutputStream bout = null;
		try{
			bout = new ByteArrayOutputStream();
			this._objToXml(bout, c, object, prettyPrint);
		}catch(Exception e){
			throw new SerializerException(e.getMessage(), e);
		}
		finally{
			try{
				bout.flush();
			}catch(Exception e){}
			try{
				bout.close();
			}catch(Exception e){}
		}
		return bout;
	}




	/*
	 =================================================================================
	 Object: protocol
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>protocol</var> of type {@link org.openspcoop2.protocol.manifest.Protocol}
	 * 
	 * @param fileName Xml file to serialize the object <var>protocol</var>
	 * @param protocol Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Protocol protocol) throws SerializerException {
		this.objToXml(fileName, Protocol.class, protocol, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>protocol</var> of type {@link org.openspcoop2.protocol.manifest.Protocol}
	 * 
	 * @param fileName Xml file to serialize the object <var>protocol</var>
	 * @param protocol Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Protocol protocol,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Protocol.class, protocol, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>protocol</var> of type {@link org.openspcoop2.protocol.manifest.Protocol}
	 * 
	 * @param file Xml file to serialize the object <var>protocol</var>
	 * @param protocol Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Protocol protocol) throws SerializerException {
		this.objToXml(file, Protocol.class, protocol, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>protocol</var> of type {@link org.openspcoop2.protocol.manifest.Protocol}
	 * 
	 * @param file Xml file to serialize the object <var>protocol</var>
	 * @param protocol Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Protocol protocol,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Protocol.class, protocol, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>protocol</var> of type {@link org.openspcoop2.protocol.manifest.Protocol}
	 * 
	 * @param out OutputStream to serialize the object <var>protocol</var>
	 * @param protocol Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Protocol protocol) throws SerializerException {
		this.objToXml(out, Protocol.class, protocol, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>protocol</var> of type {@link org.openspcoop2.protocol.manifest.Protocol}
	 * 
	 * @param out OutputStream to serialize the object <var>protocol</var>
	 * @param protocol Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Protocol protocol,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Protocol.class, protocol, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>protocol</var> of type {@link org.openspcoop2.protocol.manifest.Protocol}
	 * 
	 * @param protocol Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Protocol protocol) throws SerializerException {
		return this.objToXml(Protocol.class, protocol, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>protocol</var> of type {@link org.openspcoop2.protocol.manifest.Protocol}
	 * 
	 * @param protocol Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Protocol protocol,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Protocol.class, protocol, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>protocol</var> of type {@link org.openspcoop2.protocol.manifest.Protocol}
	 * 
	 * @param protocol Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Protocol protocol) throws SerializerException {
		return this.objToXml(Protocol.class, protocol, false).toString();
	}
	/**
	 * Serialize to String the object <var>protocol</var> of type {@link org.openspcoop2.protocol.manifest.Protocol}
	 * 
	 * @param protocol Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Protocol protocol,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Protocol.class, protocol, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: openspcoop2
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>openspcoop2</var> of type {@link org.openspcoop2.protocol.manifest.Openspcoop2}
	 * 
	 * @param fileName Xml file to serialize the object <var>openspcoop2</var>
	 * @param openspcoop2 Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Openspcoop2 openspcoop2) throws SerializerException {
		this.objToXml(fileName, Openspcoop2.class, openspcoop2, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>openspcoop2</var> of type {@link org.openspcoop2.protocol.manifest.Openspcoop2}
	 * 
	 * @param fileName Xml file to serialize the object <var>openspcoop2</var>
	 * @param openspcoop2 Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Openspcoop2 openspcoop2,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Openspcoop2.class, openspcoop2, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>openspcoop2</var> of type {@link org.openspcoop2.protocol.manifest.Openspcoop2}
	 * 
	 * @param file Xml file to serialize the object <var>openspcoop2</var>
	 * @param openspcoop2 Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Openspcoop2 openspcoop2) throws SerializerException {
		this.objToXml(file, Openspcoop2.class, openspcoop2, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>openspcoop2</var> of type {@link org.openspcoop2.protocol.manifest.Openspcoop2}
	 * 
	 * @param file Xml file to serialize the object <var>openspcoop2</var>
	 * @param openspcoop2 Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Openspcoop2 openspcoop2,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Openspcoop2.class, openspcoop2, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>openspcoop2</var> of type {@link org.openspcoop2.protocol.manifest.Openspcoop2}
	 * 
	 * @param out OutputStream to serialize the object <var>openspcoop2</var>
	 * @param openspcoop2 Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Openspcoop2 openspcoop2) throws SerializerException {
		this.objToXml(out, Openspcoop2.class, openspcoop2, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>openspcoop2</var> of type {@link org.openspcoop2.protocol.manifest.Openspcoop2}
	 * 
	 * @param out OutputStream to serialize the object <var>openspcoop2</var>
	 * @param openspcoop2 Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Openspcoop2 openspcoop2,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Openspcoop2.class, openspcoop2, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>openspcoop2</var> of type {@link org.openspcoop2.protocol.manifest.Openspcoop2}
	 * 
	 * @param openspcoop2 Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Openspcoop2 openspcoop2) throws SerializerException {
		return this.objToXml(Openspcoop2.class, openspcoop2, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>openspcoop2</var> of type {@link org.openspcoop2.protocol.manifest.Openspcoop2}
	 * 
	 * @param openspcoop2 Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Openspcoop2 openspcoop2,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Openspcoop2.class, openspcoop2, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>openspcoop2</var> of type {@link org.openspcoop2.protocol.manifest.Openspcoop2}
	 * 
	 * @param openspcoop2 Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Openspcoop2 openspcoop2) throws SerializerException {
		return this.objToXml(Openspcoop2.class, openspcoop2, false).toString();
	}
	/**
	 * Serialize to String the object <var>openspcoop2</var> of type {@link org.openspcoop2.protocol.manifest.Openspcoop2}
	 * 
	 * @param openspcoop2 Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Openspcoop2 openspcoop2,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Openspcoop2.class, openspcoop2, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: binding
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>binding</var> of type {@link org.openspcoop2.protocol.manifest.Binding}
	 * 
	 * @param fileName Xml file to serialize the object <var>binding</var>
	 * @param binding Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Binding binding) throws SerializerException {
		this.objToXml(fileName, Binding.class, binding, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>binding</var> of type {@link org.openspcoop2.protocol.manifest.Binding}
	 * 
	 * @param fileName Xml file to serialize the object <var>binding</var>
	 * @param binding Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Binding binding,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Binding.class, binding, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>binding</var> of type {@link org.openspcoop2.protocol.manifest.Binding}
	 * 
	 * @param file Xml file to serialize the object <var>binding</var>
	 * @param binding Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Binding binding) throws SerializerException {
		this.objToXml(file, Binding.class, binding, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>binding</var> of type {@link org.openspcoop2.protocol.manifest.Binding}
	 * 
	 * @param file Xml file to serialize the object <var>binding</var>
	 * @param binding Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Binding binding,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Binding.class, binding, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>binding</var> of type {@link org.openspcoop2.protocol.manifest.Binding}
	 * 
	 * @param out OutputStream to serialize the object <var>binding</var>
	 * @param binding Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Binding binding) throws SerializerException {
		this.objToXml(out, Binding.class, binding, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>binding</var> of type {@link org.openspcoop2.protocol.manifest.Binding}
	 * 
	 * @param out OutputStream to serialize the object <var>binding</var>
	 * @param binding Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Binding binding,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Binding.class, binding, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>binding</var> of type {@link org.openspcoop2.protocol.manifest.Binding}
	 * 
	 * @param binding Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Binding binding) throws SerializerException {
		return this.objToXml(Binding.class, binding, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>binding</var> of type {@link org.openspcoop2.protocol.manifest.Binding}
	 * 
	 * @param binding Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Binding binding,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Binding.class, binding, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>binding</var> of type {@link org.openspcoop2.protocol.manifest.Binding}
	 * 
	 * @param binding Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Binding binding) throws SerializerException {
		return this.objToXml(Binding.class, binding, false).toString();
	}
	/**
	 * Serialize to String the object <var>binding</var> of type {@link org.openspcoop2.protocol.manifest.Binding}
	 * 
	 * @param binding Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Binding binding,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Binding.class, binding, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: web
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>web</var> of type {@link org.openspcoop2.protocol.manifest.Web}
	 * 
	 * @param fileName Xml file to serialize the object <var>web</var>
	 * @param web Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Web web) throws SerializerException {
		this.objToXml(fileName, Web.class, web, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>web</var> of type {@link org.openspcoop2.protocol.manifest.Web}
	 * 
	 * @param fileName Xml file to serialize the object <var>web</var>
	 * @param web Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Web web,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Web.class, web, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>web</var> of type {@link org.openspcoop2.protocol.manifest.Web}
	 * 
	 * @param file Xml file to serialize the object <var>web</var>
	 * @param web Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Web web) throws SerializerException {
		this.objToXml(file, Web.class, web, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>web</var> of type {@link org.openspcoop2.protocol.manifest.Web}
	 * 
	 * @param file Xml file to serialize the object <var>web</var>
	 * @param web Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Web web,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Web.class, web, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>web</var> of type {@link org.openspcoop2.protocol.manifest.Web}
	 * 
	 * @param out OutputStream to serialize the object <var>web</var>
	 * @param web Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Web web) throws SerializerException {
		this.objToXml(out, Web.class, web, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>web</var> of type {@link org.openspcoop2.protocol.manifest.Web}
	 * 
	 * @param out OutputStream to serialize the object <var>web</var>
	 * @param web Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Web web,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Web.class, web, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>web</var> of type {@link org.openspcoop2.protocol.manifest.Web}
	 * 
	 * @param web Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Web web) throws SerializerException {
		return this.objToXml(Web.class, web, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>web</var> of type {@link org.openspcoop2.protocol.manifest.Web}
	 * 
	 * @param web Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Web web,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Web.class, web, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>web</var> of type {@link org.openspcoop2.protocol.manifest.Web}
	 * 
	 * @param web Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Web web) throws SerializerException {
		return this.objToXml(Web.class, web, false).toString();
	}
	/**
	 * Serialize to String the object <var>web</var> of type {@link org.openspcoop2.protocol.manifest.Web}
	 * 
	 * @param web Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Web web,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Web.class, web, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: registry
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>registry</var> of type {@link org.openspcoop2.protocol.manifest.Registry}
	 * 
	 * @param fileName Xml file to serialize the object <var>registry</var>
	 * @param registry Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Registry registry) throws SerializerException {
		this.objToXml(fileName, Registry.class, registry, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>registry</var> of type {@link org.openspcoop2.protocol.manifest.Registry}
	 * 
	 * @param fileName Xml file to serialize the object <var>registry</var>
	 * @param registry Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Registry registry,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Registry.class, registry, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>registry</var> of type {@link org.openspcoop2.protocol.manifest.Registry}
	 * 
	 * @param file Xml file to serialize the object <var>registry</var>
	 * @param registry Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Registry registry) throws SerializerException {
		this.objToXml(file, Registry.class, registry, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>registry</var> of type {@link org.openspcoop2.protocol.manifest.Registry}
	 * 
	 * @param file Xml file to serialize the object <var>registry</var>
	 * @param registry Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Registry registry,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Registry.class, registry, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>registry</var> of type {@link org.openspcoop2.protocol.manifest.Registry}
	 * 
	 * @param out OutputStream to serialize the object <var>registry</var>
	 * @param registry Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Registry registry) throws SerializerException {
		this.objToXml(out, Registry.class, registry, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>registry</var> of type {@link org.openspcoop2.protocol.manifest.Registry}
	 * 
	 * @param out OutputStream to serialize the object <var>registry</var>
	 * @param registry Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Registry registry,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Registry.class, registry, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>registry</var> of type {@link org.openspcoop2.protocol.manifest.Registry}
	 * 
	 * @param registry Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Registry registry) throws SerializerException {
		return this.objToXml(Registry.class, registry, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>registry</var> of type {@link org.openspcoop2.protocol.manifest.Registry}
	 * 
	 * @param registry Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Registry registry,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Registry.class, registry, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>registry</var> of type {@link org.openspcoop2.protocol.manifest.Registry}
	 * 
	 * @param registry Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Registry registry) throws SerializerException {
		return this.objToXml(Registry.class, registry, false).toString();
	}
	/**
	 * Serialize to String the object <var>registry</var> of type {@link org.openspcoop2.protocol.manifest.Registry}
	 * 
	 * @param registry Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Registry registry,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Registry.class, registry, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: urlMapping
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>urlMapping</var> of type {@link org.openspcoop2.protocol.manifest.UrlMapping}
	 * 
	 * @param fileName Xml file to serialize the object <var>urlMapping</var>
	 * @param urlMapping Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,UrlMapping urlMapping) throws SerializerException {
		this.objToXml(fileName, UrlMapping.class, urlMapping, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>urlMapping</var> of type {@link org.openspcoop2.protocol.manifest.UrlMapping}
	 * 
	 * @param fileName Xml file to serialize the object <var>urlMapping</var>
	 * @param urlMapping Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,UrlMapping urlMapping,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, UrlMapping.class, urlMapping, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>urlMapping</var> of type {@link org.openspcoop2.protocol.manifest.UrlMapping}
	 * 
	 * @param file Xml file to serialize the object <var>urlMapping</var>
	 * @param urlMapping Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,UrlMapping urlMapping) throws SerializerException {
		this.objToXml(file, UrlMapping.class, urlMapping, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>urlMapping</var> of type {@link org.openspcoop2.protocol.manifest.UrlMapping}
	 * 
	 * @param file Xml file to serialize the object <var>urlMapping</var>
	 * @param urlMapping Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,UrlMapping urlMapping,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, UrlMapping.class, urlMapping, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>urlMapping</var> of type {@link org.openspcoop2.protocol.manifest.UrlMapping}
	 * 
	 * @param out OutputStream to serialize the object <var>urlMapping</var>
	 * @param urlMapping Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,UrlMapping urlMapping) throws SerializerException {
		this.objToXml(out, UrlMapping.class, urlMapping, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>urlMapping</var> of type {@link org.openspcoop2.protocol.manifest.UrlMapping}
	 * 
	 * @param out OutputStream to serialize the object <var>urlMapping</var>
	 * @param urlMapping Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,UrlMapping urlMapping,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, UrlMapping.class, urlMapping, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>urlMapping</var> of type {@link org.openspcoop2.protocol.manifest.UrlMapping}
	 * 
	 * @param urlMapping Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(UrlMapping urlMapping) throws SerializerException {
		return this.objToXml(UrlMapping.class, urlMapping, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>urlMapping</var> of type {@link org.openspcoop2.protocol.manifest.UrlMapping}
	 * 
	 * @param urlMapping Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(UrlMapping urlMapping,boolean prettyPrint) throws SerializerException {
		return this.objToXml(UrlMapping.class, urlMapping, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>urlMapping</var> of type {@link org.openspcoop2.protocol.manifest.UrlMapping}
	 * 
	 * @param urlMapping Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(UrlMapping urlMapping) throws SerializerException {
		return this.objToXml(UrlMapping.class, urlMapping, false).toString();
	}
	/**
	 * Serialize to String the object <var>urlMapping</var> of type {@link org.openspcoop2.protocol.manifest.UrlMapping}
	 * 
	 * @param urlMapping Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(UrlMapping urlMapping,boolean prettyPrint) throws SerializerException {
		return this.objToXml(UrlMapping.class, urlMapping, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: ServiceTypes
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>serviceTypes</var> of type {@link org.openspcoop2.protocol.manifest.ServiceTypes}
	 * 
	 * @param fileName Xml file to serialize the object <var>serviceTypes</var>
	 * @param serviceTypes Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ServiceTypes serviceTypes) throws SerializerException {
		this.objToXml(fileName, ServiceTypes.class, serviceTypes, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>serviceTypes</var> of type {@link org.openspcoop2.protocol.manifest.ServiceTypes}
	 * 
	 * @param fileName Xml file to serialize the object <var>serviceTypes</var>
	 * @param serviceTypes Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ServiceTypes serviceTypes,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, ServiceTypes.class, serviceTypes, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>serviceTypes</var> of type {@link org.openspcoop2.protocol.manifest.ServiceTypes}
	 * 
	 * @param file Xml file to serialize the object <var>serviceTypes</var>
	 * @param serviceTypes Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ServiceTypes serviceTypes) throws SerializerException {
		this.objToXml(file, ServiceTypes.class, serviceTypes, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>serviceTypes</var> of type {@link org.openspcoop2.protocol.manifest.ServiceTypes}
	 * 
	 * @param file Xml file to serialize the object <var>serviceTypes</var>
	 * @param serviceTypes Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ServiceTypes serviceTypes,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, ServiceTypes.class, serviceTypes, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>serviceTypes</var> of type {@link org.openspcoop2.protocol.manifest.ServiceTypes}
	 * 
	 * @param out OutputStream to serialize the object <var>serviceTypes</var>
	 * @param serviceTypes Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ServiceTypes serviceTypes) throws SerializerException {
		this.objToXml(out, ServiceTypes.class, serviceTypes, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>serviceTypes</var> of type {@link org.openspcoop2.protocol.manifest.ServiceTypes}
	 * 
	 * @param out OutputStream to serialize the object <var>serviceTypes</var>
	 * @param serviceTypes Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ServiceTypes serviceTypes,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, ServiceTypes.class, serviceTypes, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>serviceTypes</var> of type {@link org.openspcoop2.protocol.manifest.ServiceTypes}
	 * 
	 * @param serviceTypes Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ServiceTypes serviceTypes) throws SerializerException {
		return this.objToXml(ServiceTypes.class, serviceTypes, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>serviceTypes</var> of type {@link org.openspcoop2.protocol.manifest.ServiceTypes}
	 * 
	 * @param serviceTypes Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ServiceTypes serviceTypes,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ServiceTypes.class, serviceTypes, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>serviceTypes</var> of type {@link org.openspcoop2.protocol.manifest.ServiceTypes}
	 * 
	 * @param serviceTypes Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ServiceTypes serviceTypes) throws SerializerException {
		return this.objToXml(ServiceTypes.class, serviceTypes, false).toString();
	}
	/**
	 * Serialize to String the object <var>serviceTypes</var> of type {@link org.openspcoop2.protocol.manifest.ServiceTypes}
	 * 
	 * @param serviceTypes Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ServiceTypes serviceTypes,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ServiceTypes.class, serviceTypes, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: Service
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>service</var> of type {@link org.openspcoop2.protocol.manifest.Service}
	 * 
	 * @param fileName Xml file to serialize the object <var>service</var>
	 * @param service Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Service service) throws SerializerException {
		this.objToXml(fileName, Service.class, service, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>service</var> of type {@link org.openspcoop2.protocol.manifest.Service}
	 * 
	 * @param fileName Xml file to serialize the object <var>service</var>
	 * @param service Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Service service,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Service.class, service, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>service</var> of type {@link org.openspcoop2.protocol.manifest.Service}
	 * 
	 * @param file Xml file to serialize the object <var>service</var>
	 * @param service Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Service service) throws SerializerException {
		this.objToXml(file, Service.class, service, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>service</var> of type {@link org.openspcoop2.protocol.manifest.Service}
	 * 
	 * @param file Xml file to serialize the object <var>service</var>
	 * @param service Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Service service,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Service.class, service, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>service</var> of type {@link org.openspcoop2.protocol.manifest.Service}
	 * 
	 * @param out OutputStream to serialize the object <var>service</var>
	 * @param service Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Service service) throws SerializerException {
		this.objToXml(out, Service.class, service, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>service</var> of type {@link org.openspcoop2.protocol.manifest.Service}
	 * 
	 * @param out OutputStream to serialize the object <var>service</var>
	 * @param service Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Service service,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Service.class, service, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>service</var> of type {@link org.openspcoop2.protocol.manifest.Service}
	 * 
	 * @param service Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Service service) throws SerializerException {
		return this.objToXml(Service.class, service, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>service</var> of type {@link org.openspcoop2.protocol.manifest.Service}
	 * 
	 * @param service Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Service service,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Service.class, service, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>service</var> of type {@link org.openspcoop2.protocol.manifest.Service}
	 * 
	 * @param service Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Service service) throws SerializerException {
		return this.objToXml(Service.class, service, false).toString();
	}
	/**
	 * Serialize to String the object <var>service</var> of type {@link org.openspcoop2.protocol.manifest.Service}
	 * 
	 * @param service Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Service service,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Service.class, service, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: SoapMediaTypeMapping
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>soapMediaTypeMapping</var> of type {@link org.openspcoop2.protocol.manifest.SoapMediaTypeMapping}
	 * 
	 * @param fileName Xml file to serialize the object <var>soapMediaTypeMapping</var>
	 * @param soapMediaTypeMapping Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,SoapMediaTypeMapping soapMediaTypeMapping) throws SerializerException {
		this.objToXml(fileName, SoapMediaTypeMapping.class, soapMediaTypeMapping, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>soapMediaTypeMapping</var> of type {@link org.openspcoop2.protocol.manifest.SoapMediaTypeMapping}
	 * 
	 * @param fileName Xml file to serialize the object <var>soapMediaTypeMapping</var>
	 * @param soapMediaTypeMapping Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,SoapMediaTypeMapping soapMediaTypeMapping,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, SoapMediaTypeMapping.class, soapMediaTypeMapping, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>soapMediaTypeMapping</var> of type {@link org.openspcoop2.protocol.manifest.SoapMediaTypeMapping}
	 * 
	 * @param file Xml file to serialize the object <var>soapMediaTypeMapping</var>
	 * @param soapMediaTypeMapping Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,SoapMediaTypeMapping soapMediaTypeMapping) throws SerializerException {
		this.objToXml(file, SoapMediaTypeMapping.class, soapMediaTypeMapping, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>soapMediaTypeMapping</var> of type {@link org.openspcoop2.protocol.manifest.SoapMediaTypeMapping}
	 * 
	 * @param file Xml file to serialize the object <var>soapMediaTypeMapping</var>
	 * @param soapMediaTypeMapping Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,SoapMediaTypeMapping soapMediaTypeMapping,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, SoapMediaTypeMapping.class, soapMediaTypeMapping, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>soapMediaTypeMapping</var> of type {@link org.openspcoop2.protocol.manifest.SoapMediaTypeMapping}
	 * 
	 * @param out OutputStream to serialize the object <var>soapMediaTypeMapping</var>
	 * @param soapMediaTypeMapping Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,SoapMediaTypeMapping soapMediaTypeMapping) throws SerializerException {
		this.objToXml(out, SoapMediaTypeMapping.class, soapMediaTypeMapping, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>soapMediaTypeMapping</var> of type {@link org.openspcoop2.protocol.manifest.SoapMediaTypeMapping}
	 * 
	 * @param out OutputStream to serialize the object <var>soapMediaTypeMapping</var>
	 * @param soapMediaTypeMapping Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,SoapMediaTypeMapping soapMediaTypeMapping,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, SoapMediaTypeMapping.class, soapMediaTypeMapping, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>soapMediaTypeMapping</var> of type {@link org.openspcoop2.protocol.manifest.SoapMediaTypeMapping}
	 * 
	 * @param soapMediaTypeMapping Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(SoapMediaTypeMapping soapMediaTypeMapping) throws SerializerException {
		return this.objToXml(SoapMediaTypeMapping.class, soapMediaTypeMapping, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>soapMediaTypeMapping</var> of type {@link org.openspcoop2.protocol.manifest.SoapMediaTypeMapping}
	 * 
	 * @param soapMediaTypeMapping Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(SoapMediaTypeMapping soapMediaTypeMapping,boolean prettyPrint) throws SerializerException {
		return this.objToXml(SoapMediaTypeMapping.class, soapMediaTypeMapping, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>soapMediaTypeMapping</var> of type {@link org.openspcoop2.protocol.manifest.SoapMediaTypeMapping}
	 * 
	 * @param soapMediaTypeMapping Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(SoapMediaTypeMapping soapMediaTypeMapping) throws SerializerException {
		return this.objToXml(SoapMediaTypeMapping.class, soapMediaTypeMapping, false).toString();
	}
	/**
	 * Serialize to String the object <var>soapMediaTypeMapping</var> of type {@link org.openspcoop2.protocol.manifest.SoapMediaTypeMapping}
	 * 
	 * @param soapMediaTypeMapping Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(SoapMediaTypeMapping soapMediaTypeMapping,boolean prettyPrint) throws SerializerException {
		return this.objToXml(SoapMediaTypeMapping.class, soapMediaTypeMapping, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: SoapMediaTypeCollection
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>soapMediaTypeCollection</var> of type {@link org.openspcoop2.protocol.manifest.SoapMediaTypeCollection}
	 * 
	 * @param fileName Xml file to serialize the object <var>soapMediaTypeCollection</var>
	 * @param soapMediaTypeCollection Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,SoapMediaTypeCollection soapMediaTypeCollection) throws SerializerException {
		this.objToXml(fileName, SoapMediaTypeCollection.class, soapMediaTypeCollection, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>soapMediaTypeCollection</var> of type {@link org.openspcoop2.protocol.manifest.SoapMediaTypeCollection}
	 * 
	 * @param fileName Xml file to serialize the object <var>soapMediaTypeCollection</var>
	 * @param soapMediaTypeCollection Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,SoapMediaTypeCollection soapMediaTypeCollection,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, SoapMediaTypeCollection.class, soapMediaTypeCollection, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>soapMediaTypeCollection</var> of type {@link org.openspcoop2.protocol.manifest.SoapMediaTypeCollection}
	 * 
	 * @param file Xml file to serialize the object <var>soapMediaTypeCollection</var>
	 * @param soapMediaTypeCollection Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,SoapMediaTypeCollection soapMediaTypeCollection) throws SerializerException {
		this.objToXml(file, SoapMediaTypeCollection.class, soapMediaTypeCollection, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>soapMediaTypeCollection</var> of type {@link org.openspcoop2.protocol.manifest.SoapMediaTypeCollection}
	 * 
	 * @param file Xml file to serialize the object <var>soapMediaTypeCollection</var>
	 * @param soapMediaTypeCollection Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,SoapMediaTypeCollection soapMediaTypeCollection,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, SoapMediaTypeCollection.class, soapMediaTypeCollection, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>soapMediaTypeCollection</var> of type {@link org.openspcoop2.protocol.manifest.SoapMediaTypeCollection}
	 * 
	 * @param out OutputStream to serialize the object <var>soapMediaTypeCollection</var>
	 * @param soapMediaTypeCollection Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,SoapMediaTypeCollection soapMediaTypeCollection) throws SerializerException {
		this.objToXml(out, SoapMediaTypeCollection.class, soapMediaTypeCollection, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>soapMediaTypeCollection</var> of type {@link org.openspcoop2.protocol.manifest.SoapMediaTypeCollection}
	 * 
	 * @param out OutputStream to serialize the object <var>soapMediaTypeCollection</var>
	 * @param soapMediaTypeCollection Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,SoapMediaTypeCollection soapMediaTypeCollection,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, SoapMediaTypeCollection.class, soapMediaTypeCollection, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>soapMediaTypeCollection</var> of type {@link org.openspcoop2.protocol.manifest.SoapMediaTypeCollection}
	 * 
	 * @param soapMediaTypeCollection Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(SoapMediaTypeCollection soapMediaTypeCollection) throws SerializerException {
		return this.objToXml(SoapMediaTypeCollection.class, soapMediaTypeCollection, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>soapMediaTypeCollection</var> of type {@link org.openspcoop2.protocol.manifest.SoapMediaTypeCollection}
	 * 
	 * @param soapMediaTypeCollection Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(SoapMediaTypeCollection soapMediaTypeCollection,boolean prettyPrint) throws SerializerException {
		return this.objToXml(SoapMediaTypeCollection.class, soapMediaTypeCollection, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>soapMediaTypeCollection</var> of type {@link org.openspcoop2.protocol.manifest.SoapMediaTypeCollection}
	 * 
	 * @param soapMediaTypeCollection Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(SoapMediaTypeCollection soapMediaTypeCollection) throws SerializerException {
		return this.objToXml(SoapMediaTypeCollection.class, soapMediaTypeCollection, false).toString();
	}
	/**
	 * Serialize to String the object <var>soapMediaTypeCollection</var> of type {@link org.openspcoop2.protocol.manifest.SoapMediaTypeCollection}
	 * 
	 * @param soapMediaTypeCollection Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(SoapMediaTypeCollection soapMediaTypeCollection,boolean prettyPrint) throws SerializerException {
		return this.objToXml(SoapMediaTypeCollection.class, soapMediaTypeCollection, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: SoapMediaTypeDefaultMapping
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>soapMediaTypeDefaultMapping</var> of type {@link org.openspcoop2.protocol.manifest.SoapMediaTypeDefaultMapping}
	 * 
	 * @param fileName Xml file to serialize the object <var>soapMediaTypeDefaultMapping</var>
	 * @param soapMediaTypeDefaultMapping Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,SoapMediaTypeDefaultMapping soapMediaTypeDefaultMapping) throws SerializerException {
		this.objToXml(fileName, SoapMediaTypeDefaultMapping.class, soapMediaTypeDefaultMapping, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>soapMediaTypeDefaultMapping</var> of type {@link org.openspcoop2.protocol.manifest.SoapMediaTypeDefaultMapping}
	 * 
	 * @param fileName Xml file to serialize the object <var>soapMediaTypeDefaultMapping</var>
	 * @param soapMediaTypeDefaultMapping Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,SoapMediaTypeDefaultMapping soapMediaTypeDefaultMapping,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, SoapMediaTypeDefaultMapping.class, soapMediaTypeDefaultMapping, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>soapMediaTypeDefaultMapping</var> of type {@link org.openspcoop2.protocol.manifest.SoapMediaTypeDefaultMapping}
	 * 
	 * @param file Xml file to serialize the object <var>soapMediaTypeDefaultMapping</var>
	 * @param soapMediaTypeDefaultMapping Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,SoapMediaTypeDefaultMapping soapMediaTypeDefaultMapping) throws SerializerException {
		this.objToXml(file, SoapMediaTypeDefaultMapping.class, soapMediaTypeDefaultMapping, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>soapMediaTypeDefaultMapping</var> of type {@link org.openspcoop2.protocol.manifest.SoapMediaTypeDefaultMapping}
	 * 
	 * @param file Xml file to serialize the object <var>soapMediaTypeDefaultMapping</var>
	 * @param soapMediaTypeDefaultMapping Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,SoapMediaTypeDefaultMapping soapMediaTypeDefaultMapping,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, SoapMediaTypeDefaultMapping.class, soapMediaTypeDefaultMapping, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>soapMediaTypeDefaultMapping</var> of type {@link org.openspcoop2.protocol.manifest.SoapMediaTypeDefaultMapping}
	 * 
	 * @param out OutputStream to serialize the object <var>soapMediaTypeDefaultMapping</var>
	 * @param soapMediaTypeDefaultMapping Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,SoapMediaTypeDefaultMapping soapMediaTypeDefaultMapping) throws SerializerException {
		this.objToXml(out, SoapMediaTypeDefaultMapping.class, soapMediaTypeDefaultMapping, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>soapMediaTypeDefaultMapping</var> of type {@link org.openspcoop2.protocol.manifest.SoapMediaTypeDefaultMapping}
	 * 
	 * @param out OutputStream to serialize the object <var>soapMediaTypeDefaultMapping</var>
	 * @param soapMediaTypeDefaultMapping Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,SoapMediaTypeDefaultMapping soapMediaTypeDefaultMapping,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, SoapMediaTypeDefaultMapping.class, soapMediaTypeDefaultMapping, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>soapMediaTypeDefaultMapping</var> of type {@link org.openspcoop2.protocol.manifest.SoapMediaTypeDefaultMapping}
	 * 
	 * @param soapMediaTypeDefaultMapping Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(SoapMediaTypeDefaultMapping soapMediaTypeDefaultMapping) throws SerializerException {
		return this.objToXml(SoapMediaTypeDefaultMapping.class, soapMediaTypeDefaultMapping, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>soapMediaTypeDefaultMapping</var> of type {@link org.openspcoop2.protocol.manifest.SoapMediaTypeDefaultMapping}
	 * 
	 * @param soapMediaTypeDefaultMapping Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(SoapMediaTypeDefaultMapping soapMediaTypeDefaultMapping,boolean prettyPrint) throws SerializerException {
		return this.objToXml(SoapMediaTypeDefaultMapping.class, soapMediaTypeDefaultMapping, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>soapMediaTypeDefaultMapping</var> of type {@link org.openspcoop2.protocol.manifest.SoapMediaTypeDefaultMapping}
	 * 
	 * @param soapMediaTypeDefaultMapping Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(SoapMediaTypeDefaultMapping soapMediaTypeDefaultMapping) throws SerializerException {
		return this.objToXml(SoapMediaTypeDefaultMapping.class, soapMediaTypeDefaultMapping, false).toString();
	}
	/**
	 * Serialize to String the object <var>soapMediaTypeDefaultMapping</var> of type {@link org.openspcoop2.protocol.manifest.SoapMediaTypeDefaultMapping}
	 * 
	 * @param soapMediaTypeDefaultMapping Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(SoapMediaTypeDefaultMapping soapMediaTypeDefaultMapping,boolean prettyPrint) throws SerializerException {
		return this.objToXml(SoapMediaTypeDefaultMapping.class, soapMediaTypeDefaultMapping, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: SoapMediaTypeUndefinedMapping
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>soapMediaTypeUndefinedMapping</var> of type {@link org.openspcoop2.protocol.manifest.SoapMediaTypeUndefinedMapping}
	 * 
	 * @param fileName Xml file to serialize the object <var>soapMediaTypeUndefinedMapping</var>
	 * @param soapMediaTypeUndefinedMapping Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,SoapMediaTypeUndefinedMapping soapMediaTypeUndefinedMapping) throws SerializerException {
		this.objToXml(fileName, SoapMediaTypeUndefinedMapping.class, soapMediaTypeUndefinedMapping, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>soapMediaTypeUndefinedMapping</var> of type {@link org.openspcoop2.protocol.manifest.SoapMediaTypeUndefinedMapping}
	 * 
	 * @param fileName Xml file to serialize the object <var>soapMediaTypeUndefinedMapping</var>
	 * @param soapMediaTypeUndefinedMapping Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,SoapMediaTypeUndefinedMapping soapMediaTypeUndefinedMapping,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, SoapMediaTypeUndefinedMapping.class, soapMediaTypeUndefinedMapping, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>soapMediaTypeUndefinedMapping</var> of type {@link org.openspcoop2.protocol.manifest.SoapMediaTypeUndefinedMapping}
	 * 
	 * @param file Xml file to serialize the object <var>soapMediaTypeUndefinedMapping</var>
	 * @param soapMediaTypeUndefinedMapping Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,SoapMediaTypeUndefinedMapping soapMediaTypeUndefinedMapping) throws SerializerException {
		this.objToXml(file, SoapMediaTypeUndefinedMapping.class, soapMediaTypeUndefinedMapping, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>soapMediaTypeUndefinedMapping</var> of type {@link org.openspcoop2.protocol.manifest.SoapMediaTypeUndefinedMapping}
	 * 
	 * @param file Xml file to serialize the object <var>soapMediaTypeUndefinedMapping</var>
	 * @param soapMediaTypeUndefinedMapping Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,SoapMediaTypeUndefinedMapping soapMediaTypeUndefinedMapping,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, SoapMediaTypeUndefinedMapping.class, soapMediaTypeUndefinedMapping, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>soapMediaTypeUndefinedMapping</var> of type {@link org.openspcoop2.protocol.manifest.SoapMediaTypeUndefinedMapping}
	 * 
	 * @param out OutputStream to serialize the object <var>soapMediaTypeUndefinedMapping</var>
	 * @param soapMediaTypeUndefinedMapping Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,SoapMediaTypeUndefinedMapping soapMediaTypeUndefinedMapping) throws SerializerException {
		this.objToXml(out, SoapMediaTypeUndefinedMapping.class, soapMediaTypeUndefinedMapping, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>soapMediaTypeUndefinedMapping</var> of type {@link org.openspcoop2.protocol.manifest.SoapMediaTypeUndefinedMapping}
	 * 
	 * @param out OutputStream to serialize the object <var>soapMediaTypeUndefinedMapping</var>
	 * @param soapMediaTypeUndefinedMapping Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,SoapMediaTypeUndefinedMapping soapMediaTypeUndefinedMapping,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, SoapMediaTypeUndefinedMapping.class, soapMediaTypeUndefinedMapping, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>soapMediaTypeUndefinedMapping</var> of type {@link org.openspcoop2.protocol.manifest.SoapMediaTypeUndefinedMapping}
	 * 
	 * @param soapMediaTypeUndefinedMapping Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(SoapMediaTypeUndefinedMapping soapMediaTypeUndefinedMapping) throws SerializerException {
		return this.objToXml(SoapMediaTypeUndefinedMapping.class, soapMediaTypeUndefinedMapping, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>soapMediaTypeUndefinedMapping</var> of type {@link org.openspcoop2.protocol.manifest.SoapMediaTypeUndefinedMapping}
	 * 
	 * @param soapMediaTypeUndefinedMapping Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(SoapMediaTypeUndefinedMapping soapMediaTypeUndefinedMapping,boolean prettyPrint) throws SerializerException {
		return this.objToXml(SoapMediaTypeUndefinedMapping.class, soapMediaTypeUndefinedMapping, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>soapMediaTypeUndefinedMapping</var> of type {@link org.openspcoop2.protocol.manifest.SoapMediaTypeUndefinedMapping}
	 * 
	 * @param soapMediaTypeUndefinedMapping Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(SoapMediaTypeUndefinedMapping soapMediaTypeUndefinedMapping) throws SerializerException {
		return this.objToXml(SoapMediaTypeUndefinedMapping.class, soapMediaTypeUndefinedMapping, false).toString();
	}
	/**
	 * Serialize to String the object <var>soapMediaTypeUndefinedMapping</var> of type {@link org.openspcoop2.protocol.manifest.SoapMediaTypeUndefinedMapping}
	 * 
	 * @param soapMediaTypeUndefinedMapping Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(SoapMediaTypeUndefinedMapping soapMediaTypeUndefinedMapping,boolean prettyPrint) throws SerializerException {
		return this.objToXml(SoapMediaTypeUndefinedMapping.class, soapMediaTypeUndefinedMapping, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: Integration
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>integration</var> of type {@link org.openspcoop2.protocol.manifest.Integration}
	 * 
	 * @param fileName Xml file to serialize the object <var>integration</var>
	 * @param integration Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Integration integration) throws SerializerException {
		this.objToXml(fileName, Integration.class, integration, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>integration</var> of type {@link org.openspcoop2.protocol.manifest.Integration}
	 * 
	 * @param fileName Xml file to serialize the object <var>integration</var>
	 * @param integration Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Integration integration,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Integration.class, integration, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>integration</var> of type {@link org.openspcoop2.protocol.manifest.Integration}
	 * 
	 * @param file Xml file to serialize the object <var>integration</var>
	 * @param integration Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Integration integration) throws SerializerException {
		this.objToXml(file, Integration.class, integration, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>integration</var> of type {@link org.openspcoop2.protocol.manifest.Integration}
	 * 
	 * @param file Xml file to serialize the object <var>integration</var>
	 * @param integration Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Integration integration,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Integration.class, integration, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>integration</var> of type {@link org.openspcoop2.protocol.manifest.Integration}
	 * 
	 * @param out OutputStream to serialize the object <var>integration</var>
	 * @param integration Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Integration integration) throws SerializerException {
		this.objToXml(out, Integration.class, integration, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>integration</var> of type {@link org.openspcoop2.protocol.manifest.Integration}
	 * 
	 * @param out OutputStream to serialize the object <var>integration</var>
	 * @param integration Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Integration integration,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Integration.class, integration, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>integration</var> of type {@link org.openspcoop2.protocol.manifest.Integration}
	 * 
	 * @param integration Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Integration integration) throws SerializerException {
		return this.objToXml(Integration.class, integration, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>integration</var> of type {@link org.openspcoop2.protocol.manifest.Integration}
	 * 
	 * @param integration Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Integration integration,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Integration.class, integration, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>integration</var> of type {@link org.openspcoop2.protocol.manifest.Integration}
	 * 
	 * @param integration Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Integration integration) throws SerializerException {
		return this.objToXml(Integration.class, integration, false).toString();
	}
	/**
	 * Serialize to String the object <var>integration</var> of type {@link org.openspcoop2.protocol.manifest.Integration}
	 * 
	 * @param integration Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Integration integration,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Integration.class, integration, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: RestConfiguration
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>restConfiguration</var> of type {@link org.openspcoop2.protocol.manifest.RestConfiguration}
	 * 
	 * @param fileName Xml file to serialize the object <var>restConfiguration</var>
	 * @param restConfiguration Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,RestConfiguration restConfiguration) throws SerializerException {
		this.objToXml(fileName, RestConfiguration.class, restConfiguration, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>restConfiguration</var> of type {@link org.openspcoop2.protocol.manifest.RestConfiguration}
	 * 
	 * @param fileName Xml file to serialize the object <var>restConfiguration</var>
	 * @param restConfiguration Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,RestConfiguration restConfiguration,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, RestConfiguration.class, restConfiguration, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>restConfiguration</var> of type {@link org.openspcoop2.protocol.manifest.RestConfiguration}
	 * 
	 * @param file Xml file to serialize the object <var>restConfiguration</var>
	 * @param restConfiguration Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,RestConfiguration restConfiguration) throws SerializerException {
		this.objToXml(file, RestConfiguration.class, restConfiguration, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>restConfiguration</var> of type {@link org.openspcoop2.protocol.manifest.RestConfiguration}
	 * 
	 * @param file Xml file to serialize the object <var>restConfiguration</var>
	 * @param restConfiguration Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,RestConfiguration restConfiguration,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, RestConfiguration.class, restConfiguration, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>restConfiguration</var> of type {@link org.openspcoop2.protocol.manifest.RestConfiguration}
	 * 
	 * @param out OutputStream to serialize the object <var>restConfiguration</var>
	 * @param restConfiguration Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,RestConfiguration restConfiguration) throws SerializerException {
		this.objToXml(out, RestConfiguration.class, restConfiguration, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>restConfiguration</var> of type {@link org.openspcoop2.protocol.manifest.RestConfiguration}
	 * 
	 * @param out OutputStream to serialize the object <var>restConfiguration</var>
	 * @param restConfiguration Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,RestConfiguration restConfiguration,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, RestConfiguration.class, restConfiguration, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>restConfiguration</var> of type {@link org.openspcoop2.protocol.manifest.RestConfiguration}
	 * 
	 * @param restConfiguration Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(RestConfiguration restConfiguration) throws SerializerException {
		return this.objToXml(RestConfiguration.class, restConfiguration, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>restConfiguration</var> of type {@link org.openspcoop2.protocol.manifest.RestConfiguration}
	 * 
	 * @param restConfiguration Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(RestConfiguration restConfiguration,boolean prettyPrint) throws SerializerException {
		return this.objToXml(RestConfiguration.class, restConfiguration, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>restConfiguration</var> of type {@link org.openspcoop2.protocol.manifest.RestConfiguration}
	 * 
	 * @param restConfiguration Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(RestConfiguration restConfiguration) throws SerializerException {
		return this.objToXml(RestConfiguration.class, restConfiguration, false).toString();
	}
	/**
	 * Serialize to String the object <var>restConfiguration</var> of type {@link org.openspcoop2.protocol.manifest.RestConfiguration}
	 * 
	 * @param restConfiguration Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(RestConfiguration restConfiguration,boolean prettyPrint) throws SerializerException {
		return this.objToXml(RestConfiguration.class, restConfiguration, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: IntegrationErrorConfiguration
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>integrationErrorConfiguration</var> of type {@link org.openspcoop2.protocol.manifest.IntegrationErrorConfiguration}
	 * 
	 * @param fileName Xml file to serialize the object <var>integrationErrorConfiguration</var>
	 * @param integrationErrorConfiguration Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,IntegrationErrorConfiguration integrationErrorConfiguration) throws SerializerException {
		this.objToXml(fileName, IntegrationErrorConfiguration.class, integrationErrorConfiguration, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>integrationErrorConfiguration</var> of type {@link org.openspcoop2.protocol.manifest.IntegrationErrorConfiguration}
	 * 
	 * @param fileName Xml file to serialize the object <var>integrationErrorConfiguration</var>
	 * @param integrationErrorConfiguration Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,IntegrationErrorConfiguration integrationErrorConfiguration,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, IntegrationErrorConfiguration.class, integrationErrorConfiguration, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>integrationErrorConfiguration</var> of type {@link org.openspcoop2.protocol.manifest.IntegrationErrorConfiguration}
	 * 
	 * @param file Xml file to serialize the object <var>integrationErrorConfiguration</var>
	 * @param integrationErrorConfiguration Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,IntegrationErrorConfiguration integrationErrorConfiguration) throws SerializerException {
		this.objToXml(file, IntegrationErrorConfiguration.class, integrationErrorConfiguration, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>integrationErrorConfiguration</var> of type {@link org.openspcoop2.protocol.manifest.IntegrationErrorConfiguration}
	 * 
	 * @param file Xml file to serialize the object <var>integrationErrorConfiguration</var>
	 * @param integrationErrorConfiguration Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,IntegrationErrorConfiguration integrationErrorConfiguration,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, IntegrationErrorConfiguration.class, integrationErrorConfiguration, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>integrationErrorConfiguration</var> of type {@link org.openspcoop2.protocol.manifest.IntegrationErrorConfiguration}
	 * 
	 * @param out OutputStream to serialize the object <var>integrationErrorConfiguration</var>
	 * @param integrationErrorConfiguration Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,IntegrationErrorConfiguration integrationErrorConfiguration) throws SerializerException {
		this.objToXml(out, IntegrationErrorConfiguration.class, integrationErrorConfiguration, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>integrationErrorConfiguration</var> of type {@link org.openspcoop2.protocol.manifest.IntegrationErrorConfiguration}
	 * 
	 * @param out OutputStream to serialize the object <var>integrationErrorConfiguration</var>
	 * @param integrationErrorConfiguration Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,IntegrationErrorConfiguration integrationErrorConfiguration,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, IntegrationErrorConfiguration.class, integrationErrorConfiguration, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>integrationErrorConfiguration</var> of type {@link org.openspcoop2.protocol.manifest.IntegrationErrorConfiguration}
	 * 
	 * @param integrationErrorConfiguration Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(IntegrationErrorConfiguration integrationErrorConfiguration) throws SerializerException {
		return this.objToXml(IntegrationErrorConfiguration.class, integrationErrorConfiguration, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>integrationErrorConfiguration</var> of type {@link org.openspcoop2.protocol.manifest.IntegrationErrorConfiguration}
	 * 
	 * @param integrationErrorConfiguration Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(IntegrationErrorConfiguration integrationErrorConfiguration,boolean prettyPrint) throws SerializerException {
		return this.objToXml(IntegrationErrorConfiguration.class, integrationErrorConfiguration, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>integrationErrorConfiguration</var> of type {@link org.openspcoop2.protocol.manifest.IntegrationErrorConfiguration}
	 * 
	 * @param integrationErrorConfiguration Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(IntegrationErrorConfiguration integrationErrorConfiguration) throws SerializerException {
		return this.objToXml(IntegrationErrorConfiguration.class, integrationErrorConfiguration, false).toString();
	}
	/**
	 * Serialize to String the object <var>integrationErrorConfiguration</var> of type {@link org.openspcoop2.protocol.manifest.IntegrationErrorConfiguration}
	 * 
	 * @param integrationErrorConfiguration Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(IntegrationErrorConfiguration integrationErrorConfiguration,boolean prettyPrint) throws SerializerException {
		return this.objToXml(IntegrationErrorConfiguration.class, integrationErrorConfiguration, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: RestMediaTypeCollection
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>restMediaTypeCollection</var> of type {@link org.openspcoop2.protocol.manifest.RestMediaTypeCollection}
	 * 
	 * @param fileName Xml file to serialize the object <var>restMediaTypeCollection</var>
	 * @param restMediaTypeCollection Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,RestMediaTypeCollection restMediaTypeCollection) throws SerializerException {
		this.objToXml(fileName, RestMediaTypeCollection.class, restMediaTypeCollection, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>restMediaTypeCollection</var> of type {@link org.openspcoop2.protocol.manifest.RestMediaTypeCollection}
	 * 
	 * @param fileName Xml file to serialize the object <var>restMediaTypeCollection</var>
	 * @param restMediaTypeCollection Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,RestMediaTypeCollection restMediaTypeCollection,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, RestMediaTypeCollection.class, restMediaTypeCollection, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>restMediaTypeCollection</var> of type {@link org.openspcoop2.protocol.manifest.RestMediaTypeCollection}
	 * 
	 * @param file Xml file to serialize the object <var>restMediaTypeCollection</var>
	 * @param restMediaTypeCollection Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,RestMediaTypeCollection restMediaTypeCollection) throws SerializerException {
		this.objToXml(file, RestMediaTypeCollection.class, restMediaTypeCollection, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>restMediaTypeCollection</var> of type {@link org.openspcoop2.protocol.manifest.RestMediaTypeCollection}
	 * 
	 * @param file Xml file to serialize the object <var>restMediaTypeCollection</var>
	 * @param restMediaTypeCollection Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,RestMediaTypeCollection restMediaTypeCollection,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, RestMediaTypeCollection.class, restMediaTypeCollection, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>restMediaTypeCollection</var> of type {@link org.openspcoop2.protocol.manifest.RestMediaTypeCollection}
	 * 
	 * @param out OutputStream to serialize the object <var>restMediaTypeCollection</var>
	 * @param restMediaTypeCollection Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,RestMediaTypeCollection restMediaTypeCollection) throws SerializerException {
		this.objToXml(out, RestMediaTypeCollection.class, restMediaTypeCollection, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>restMediaTypeCollection</var> of type {@link org.openspcoop2.protocol.manifest.RestMediaTypeCollection}
	 * 
	 * @param out OutputStream to serialize the object <var>restMediaTypeCollection</var>
	 * @param restMediaTypeCollection Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,RestMediaTypeCollection restMediaTypeCollection,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, RestMediaTypeCollection.class, restMediaTypeCollection, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>restMediaTypeCollection</var> of type {@link org.openspcoop2.protocol.manifest.RestMediaTypeCollection}
	 * 
	 * @param restMediaTypeCollection Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(RestMediaTypeCollection restMediaTypeCollection) throws SerializerException {
		return this.objToXml(RestMediaTypeCollection.class, restMediaTypeCollection, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>restMediaTypeCollection</var> of type {@link org.openspcoop2.protocol.manifest.RestMediaTypeCollection}
	 * 
	 * @param restMediaTypeCollection Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(RestMediaTypeCollection restMediaTypeCollection,boolean prettyPrint) throws SerializerException {
		return this.objToXml(RestMediaTypeCollection.class, restMediaTypeCollection, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>restMediaTypeCollection</var> of type {@link org.openspcoop2.protocol.manifest.RestMediaTypeCollection}
	 * 
	 * @param restMediaTypeCollection Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(RestMediaTypeCollection restMediaTypeCollection) throws SerializerException {
		return this.objToXml(RestMediaTypeCollection.class, restMediaTypeCollection, false).toString();
	}
	/**
	 * Serialize to String the object <var>restMediaTypeCollection</var> of type {@link org.openspcoop2.protocol.manifest.RestMediaTypeCollection}
	 * 
	 * @param restMediaTypeCollection Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(RestMediaTypeCollection restMediaTypeCollection,boolean prettyPrint) throws SerializerException {
		return this.objToXml(RestMediaTypeCollection.class, restMediaTypeCollection, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: InterfacesConfiguration
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>interfacesConfiguration</var> of type {@link org.openspcoop2.protocol.manifest.InterfacesConfiguration}
	 * 
	 * @param fileName Xml file to serialize the object <var>interfacesConfiguration</var>
	 * @param interfacesConfiguration Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,InterfacesConfiguration interfacesConfiguration) throws SerializerException {
		this.objToXml(fileName, InterfacesConfiguration.class, interfacesConfiguration, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>interfacesConfiguration</var> of type {@link org.openspcoop2.protocol.manifest.InterfacesConfiguration}
	 * 
	 * @param fileName Xml file to serialize the object <var>interfacesConfiguration</var>
	 * @param interfacesConfiguration Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,InterfacesConfiguration interfacesConfiguration,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, InterfacesConfiguration.class, interfacesConfiguration, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>interfacesConfiguration</var> of type {@link org.openspcoop2.protocol.manifest.InterfacesConfiguration}
	 * 
	 * @param file Xml file to serialize the object <var>interfacesConfiguration</var>
	 * @param interfacesConfiguration Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,InterfacesConfiguration interfacesConfiguration) throws SerializerException {
		this.objToXml(file, InterfacesConfiguration.class, interfacesConfiguration, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>interfacesConfiguration</var> of type {@link org.openspcoop2.protocol.manifest.InterfacesConfiguration}
	 * 
	 * @param file Xml file to serialize the object <var>interfacesConfiguration</var>
	 * @param interfacesConfiguration Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,InterfacesConfiguration interfacesConfiguration,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, InterfacesConfiguration.class, interfacesConfiguration, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>interfacesConfiguration</var> of type {@link org.openspcoop2.protocol.manifest.InterfacesConfiguration}
	 * 
	 * @param out OutputStream to serialize the object <var>interfacesConfiguration</var>
	 * @param interfacesConfiguration Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,InterfacesConfiguration interfacesConfiguration) throws SerializerException {
		this.objToXml(out, InterfacesConfiguration.class, interfacesConfiguration, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>interfacesConfiguration</var> of type {@link org.openspcoop2.protocol.manifest.InterfacesConfiguration}
	 * 
	 * @param out OutputStream to serialize the object <var>interfacesConfiguration</var>
	 * @param interfacesConfiguration Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,InterfacesConfiguration interfacesConfiguration,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, InterfacesConfiguration.class, interfacesConfiguration, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>interfacesConfiguration</var> of type {@link org.openspcoop2.protocol.manifest.InterfacesConfiguration}
	 * 
	 * @param interfacesConfiguration Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(InterfacesConfiguration interfacesConfiguration) throws SerializerException {
		return this.objToXml(InterfacesConfiguration.class, interfacesConfiguration, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>interfacesConfiguration</var> of type {@link org.openspcoop2.protocol.manifest.InterfacesConfiguration}
	 * 
	 * @param interfacesConfiguration Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(InterfacesConfiguration interfacesConfiguration,boolean prettyPrint) throws SerializerException {
		return this.objToXml(InterfacesConfiguration.class, interfacesConfiguration, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>interfacesConfiguration</var> of type {@link org.openspcoop2.protocol.manifest.InterfacesConfiguration}
	 * 
	 * @param interfacesConfiguration Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(InterfacesConfiguration interfacesConfiguration) throws SerializerException {
		return this.objToXml(InterfacesConfiguration.class, interfacesConfiguration, false).toString();
	}
	/**
	 * Serialize to String the object <var>interfacesConfiguration</var> of type {@link org.openspcoop2.protocol.manifest.InterfacesConfiguration}
	 * 
	 * @param interfacesConfiguration Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(InterfacesConfiguration interfacesConfiguration,boolean prettyPrint) throws SerializerException {
		return this.objToXml(InterfacesConfiguration.class, interfacesConfiguration, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: RestCollaborationProfile
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>restCollaborationProfile</var> of type {@link org.openspcoop2.protocol.manifest.RestCollaborationProfile}
	 * 
	 * @param fileName Xml file to serialize the object <var>restCollaborationProfile</var>
	 * @param restCollaborationProfile Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,RestCollaborationProfile restCollaborationProfile) throws SerializerException {
		this.objToXml(fileName, RestCollaborationProfile.class, restCollaborationProfile, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>restCollaborationProfile</var> of type {@link org.openspcoop2.protocol.manifest.RestCollaborationProfile}
	 * 
	 * @param fileName Xml file to serialize the object <var>restCollaborationProfile</var>
	 * @param restCollaborationProfile Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,RestCollaborationProfile restCollaborationProfile,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, RestCollaborationProfile.class, restCollaborationProfile, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>restCollaborationProfile</var> of type {@link org.openspcoop2.protocol.manifest.RestCollaborationProfile}
	 * 
	 * @param file Xml file to serialize the object <var>restCollaborationProfile</var>
	 * @param restCollaborationProfile Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,RestCollaborationProfile restCollaborationProfile) throws SerializerException {
		this.objToXml(file, RestCollaborationProfile.class, restCollaborationProfile, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>restCollaborationProfile</var> of type {@link org.openspcoop2.protocol.manifest.RestCollaborationProfile}
	 * 
	 * @param file Xml file to serialize the object <var>restCollaborationProfile</var>
	 * @param restCollaborationProfile Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,RestCollaborationProfile restCollaborationProfile,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, RestCollaborationProfile.class, restCollaborationProfile, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>restCollaborationProfile</var> of type {@link org.openspcoop2.protocol.manifest.RestCollaborationProfile}
	 * 
	 * @param out OutputStream to serialize the object <var>restCollaborationProfile</var>
	 * @param restCollaborationProfile Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,RestCollaborationProfile restCollaborationProfile) throws SerializerException {
		this.objToXml(out, RestCollaborationProfile.class, restCollaborationProfile, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>restCollaborationProfile</var> of type {@link org.openspcoop2.protocol.manifest.RestCollaborationProfile}
	 * 
	 * @param out OutputStream to serialize the object <var>restCollaborationProfile</var>
	 * @param restCollaborationProfile Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,RestCollaborationProfile restCollaborationProfile,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, RestCollaborationProfile.class, restCollaborationProfile, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>restCollaborationProfile</var> of type {@link org.openspcoop2.protocol.manifest.RestCollaborationProfile}
	 * 
	 * @param restCollaborationProfile Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(RestCollaborationProfile restCollaborationProfile) throws SerializerException {
		return this.objToXml(RestCollaborationProfile.class, restCollaborationProfile, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>restCollaborationProfile</var> of type {@link org.openspcoop2.protocol.manifest.RestCollaborationProfile}
	 * 
	 * @param restCollaborationProfile Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(RestCollaborationProfile restCollaborationProfile,boolean prettyPrint) throws SerializerException {
		return this.objToXml(RestCollaborationProfile.class, restCollaborationProfile, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>restCollaborationProfile</var> of type {@link org.openspcoop2.protocol.manifest.RestCollaborationProfile}
	 * 
	 * @param restCollaborationProfile Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(RestCollaborationProfile restCollaborationProfile) throws SerializerException {
		return this.objToXml(RestCollaborationProfile.class, restCollaborationProfile, false).toString();
	}
	/**
	 * Serialize to String the object <var>restCollaborationProfile</var> of type {@link org.openspcoop2.protocol.manifest.RestCollaborationProfile}
	 * 
	 * @param restCollaborationProfile Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(RestCollaborationProfile restCollaborationProfile,boolean prettyPrint) throws SerializerException {
		return this.objToXml(RestCollaborationProfile.class, restCollaborationProfile, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: Functionality
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>functionality</var> of type {@link org.openspcoop2.protocol.manifest.Functionality}
	 * 
	 * @param fileName Xml file to serialize the object <var>functionality</var>
	 * @param functionality Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Functionality functionality) throws SerializerException {
		this.objToXml(fileName, Functionality.class, functionality, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>functionality</var> of type {@link org.openspcoop2.protocol.manifest.Functionality}
	 * 
	 * @param fileName Xml file to serialize the object <var>functionality</var>
	 * @param functionality Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Functionality functionality,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Functionality.class, functionality, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>functionality</var> of type {@link org.openspcoop2.protocol.manifest.Functionality}
	 * 
	 * @param file Xml file to serialize the object <var>functionality</var>
	 * @param functionality Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Functionality functionality) throws SerializerException {
		this.objToXml(file, Functionality.class, functionality, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>functionality</var> of type {@link org.openspcoop2.protocol.manifest.Functionality}
	 * 
	 * @param file Xml file to serialize the object <var>functionality</var>
	 * @param functionality Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Functionality functionality,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Functionality.class, functionality, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>functionality</var> of type {@link org.openspcoop2.protocol.manifest.Functionality}
	 * 
	 * @param out OutputStream to serialize the object <var>functionality</var>
	 * @param functionality Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Functionality functionality) throws SerializerException {
		this.objToXml(out, Functionality.class, functionality, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>functionality</var> of type {@link org.openspcoop2.protocol.manifest.Functionality}
	 * 
	 * @param out OutputStream to serialize the object <var>functionality</var>
	 * @param functionality Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Functionality functionality,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Functionality.class, functionality, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>functionality</var> of type {@link org.openspcoop2.protocol.manifest.Functionality}
	 * 
	 * @param functionality Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Functionality functionality) throws SerializerException {
		return this.objToXml(Functionality.class, functionality, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>functionality</var> of type {@link org.openspcoop2.protocol.manifest.Functionality}
	 * 
	 * @param functionality Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Functionality functionality,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Functionality.class, functionality, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>functionality</var> of type {@link org.openspcoop2.protocol.manifest.Functionality}
	 * 
	 * @param functionality Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Functionality functionality) throws SerializerException {
		return this.objToXml(Functionality.class, functionality, false).toString();
	}
	/**
	 * Serialize to String the object <var>functionality</var> of type {@link org.openspcoop2.protocol.manifest.Functionality}
	 * 
	 * @param functionality Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Functionality functionality,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Functionality.class, functionality, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: IntegrationErrorCollection
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>integrationErrorCollection</var> of type {@link org.openspcoop2.protocol.manifest.IntegrationErrorCollection}
	 * 
	 * @param fileName Xml file to serialize the object <var>integrationErrorCollection</var>
	 * @param integrationErrorCollection Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,IntegrationErrorCollection integrationErrorCollection) throws SerializerException {
		this.objToXml(fileName, IntegrationErrorCollection.class, integrationErrorCollection, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>integrationErrorCollection</var> of type {@link org.openspcoop2.protocol.manifest.IntegrationErrorCollection}
	 * 
	 * @param fileName Xml file to serialize the object <var>integrationErrorCollection</var>
	 * @param integrationErrorCollection Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,IntegrationErrorCollection integrationErrorCollection,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, IntegrationErrorCollection.class, integrationErrorCollection, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>integrationErrorCollection</var> of type {@link org.openspcoop2.protocol.manifest.IntegrationErrorCollection}
	 * 
	 * @param file Xml file to serialize the object <var>integrationErrorCollection</var>
	 * @param integrationErrorCollection Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,IntegrationErrorCollection integrationErrorCollection) throws SerializerException {
		this.objToXml(file, IntegrationErrorCollection.class, integrationErrorCollection, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>integrationErrorCollection</var> of type {@link org.openspcoop2.protocol.manifest.IntegrationErrorCollection}
	 * 
	 * @param file Xml file to serialize the object <var>integrationErrorCollection</var>
	 * @param integrationErrorCollection Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,IntegrationErrorCollection integrationErrorCollection,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, IntegrationErrorCollection.class, integrationErrorCollection, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>integrationErrorCollection</var> of type {@link org.openspcoop2.protocol.manifest.IntegrationErrorCollection}
	 * 
	 * @param out OutputStream to serialize the object <var>integrationErrorCollection</var>
	 * @param integrationErrorCollection Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,IntegrationErrorCollection integrationErrorCollection) throws SerializerException {
		this.objToXml(out, IntegrationErrorCollection.class, integrationErrorCollection, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>integrationErrorCollection</var> of type {@link org.openspcoop2.protocol.manifest.IntegrationErrorCollection}
	 * 
	 * @param out OutputStream to serialize the object <var>integrationErrorCollection</var>
	 * @param integrationErrorCollection Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,IntegrationErrorCollection integrationErrorCollection,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, IntegrationErrorCollection.class, integrationErrorCollection, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>integrationErrorCollection</var> of type {@link org.openspcoop2.protocol.manifest.IntegrationErrorCollection}
	 * 
	 * @param integrationErrorCollection Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(IntegrationErrorCollection integrationErrorCollection) throws SerializerException {
		return this.objToXml(IntegrationErrorCollection.class, integrationErrorCollection, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>integrationErrorCollection</var> of type {@link org.openspcoop2.protocol.manifest.IntegrationErrorCollection}
	 * 
	 * @param integrationErrorCollection Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(IntegrationErrorCollection integrationErrorCollection,boolean prettyPrint) throws SerializerException {
		return this.objToXml(IntegrationErrorCollection.class, integrationErrorCollection, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>integrationErrorCollection</var> of type {@link org.openspcoop2.protocol.manifest.IntegrationErrorCollection}
	 * 
	 * @param integrationErrorCollection Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(IntegrationErrorCollection integrationErrorCollection) throws SerializerException {
		return this.objToXml(IntegrationErrorCollection.class, integrationErrorCollection, false).toString();
	}
	/**
	 * Serialize to String the object <var>integrationErrorCollection</var> of type {@link org.openspcoop2.protocol.manifest.IntegrationErrorCollection}
	 * 
	 * @param integrationErrorCollection Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(IntegrationErrorCollection integrationErrorCollection,boolean prettyPrint) throws SerializerException {
		return this.objToXml(IntegrationErrorCollection.class, integrationErrorCollection, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: SoapConfiguration
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>soapConfiguration</var> of type {@link org.openspcoop2.protocol.manifest.SoapConfiguration}
	 * 
	 * @param fileName Xml file to serialize the object <var>soapConfiguration</var>
	 * @param soapConfiguration Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,SoapConfiguration soapConfiguration) throws SerializerException {
		this.objToXml(fileName, SoapConfiguration.class, soapConfiguration, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>soapConfiguration</var> of type {@link org.openspcoop2.protocol.manifest.SoapConfiguration}
	 * 
	 * @param fileName Xml file to serialize the object <var>soapConfiguration</var>
	 * @param soapConfiguration Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,SoapConfiguration soapConfiguration,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, SoapConfiguration.class, soapConfiguration, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>soapConfiguration</var> of type {@link org.openspcoop2.protocol.manifest.SoapConfiguration}
	 * 
	 * @param file Xml file to serialize the object <var>soapConfiguration</var>
	 * @param soapConfiguration Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,SoapConfiguration soapConfiguration) throws SerializerException {
		this.objToXml(file, SoapConfiguration.class, soapConfiguration, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>soapConfiguration</var> of type {@link org.openspcoop2.protocol.manifest.SoapConfiguration}
	 * 
	 * @param file Xml file to serialize the object <var>soapConfiguration</var>
	 * @param soapConfiguration Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,SoapConfiguration soapConfiguration,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, SoapConfiguration.class, soapConfiguration, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>soapConfiguration</var> of type {@link org.openspcoop2.protocol.manifest.SoapConfiguration}
	 * 
	 * @param out OutputStream to serialize the object <var>soapConfiguration</var>
	 * @param soapConfiguration Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,SoapConfiguration soapConfiguration) throws SerializerException {
		this.objToXml(out, SoapConfiguration.class, soapConfiguration, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>soapConfiguration</var> of type {@link org.openspcoop2.protocol.manifest.SoapConfiguration}
	 * 
	 * @param out OutputStream to serialize the object <var>soapConfiguration</var>
	 * @param soapConfiguration Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,SoapConfiguration soapConfiguration,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, SoapConfiguration.class, soapConfiguration, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>soapConfiguration</var> of type {@link org.openspcoop2.protocol.manifest.SoapConfiguration}
	 * 
	 * @param soapConfiguration Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(SoapConfiguration soapConfiguration) throws SerializerException {
		return this.objToXml(SoapConfiguration.class, soapConfiguration, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>soapConfiguration</var> of type {@link org.openspcoop2.protocol.manifest.SoapConfiguration}
	 * 
	 * @param soapConfiguration Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(SoapConfiguration soapConfiguration,boolean prettyPrint) throws SerializerException {
		return this.objToXml(SoapConfiguration.class, soapConfiguration, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>soapConfiguration</var> of type {@link org.openspcoop2.protocol.manifest.SoapConfiguration}
	 * 
	 * @param soapConfiguration Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(SoapConfiguration soapConfiguration) throws SerializerException {
		return this.objToXml(SoapConfiguration.class, soapConfiguration, false).toString();
	}
	/**
	 * Serialize to String the object <var>soapConfiguration</var> of type {@link org.openspcoop2.protocol.manifest.SoapConfiguration}
	 * 
	 * @param soapConfiguration Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(SoapConfiguration soapConfiguration,boolean prettyPrint) throws SerializerException {
		return this.objToXml(SoapConfiguration.class, soapConfiguration, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: IntegrationError
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>integrationError</var> of type {@link org.openspcoop2.protocol.manifest.IntegrationError}
	 * 
	 * @param fileName Xml file to serialize the object <var>integrationError</var>
	 * @param integrationError Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,IntegrationError integrationError) throws SerializerException {
		this.objToXml(fileName, IntegrationError.class, integrationError, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>integrationError</var> of type {@link org.openspcoop2.protocol.manifest.IntegrationError}
	 * 
	 * @param fileName Xml file to serialize the object <var>integrationError</var>
	 * @param integrationError Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,IntegrationError integrationError,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, IntegrationError.class, integrationError, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>integrationError</var> of type {@link org.openspcoop2.protocol.manifest.IntegrationError}
	 * 
	 * @param file Xml file to serialize the object <var>integrationError</var>
	 * @param integrationError Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,IntegrationError integrationError) throws SerializerException {
		this.objToXml(file, IntegrationError.class, integrationError, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>integrationError</var> of type {@link org.openspcoop2.protocol.manifest.IntegrationError}
	 * 
	 * @param file Xml file to serialize the object <var>integrationError</var>
	 * @param integrationError Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,IntegrationError integrationError,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, IntegrationError.class, integrationError, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>integrationError</var> of type {@link org.openspcoop2.protocol.manifest.IntegrationError}
	 * 
	 * @param out OutputStream to serialize the object <var>integrationError</var>
	 * @param integrationError Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,IntegrationError integrationError) throws SerializerException {
		this.objToXml(out, IntegrationError.class, integrationError, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>integrationError</var> of type {@link org.openspcoop2.protocol.manifest.IntegrationError}
	 * 
	 * @param out OutputStream to serialize the object <var>integrationError</var>
	 * @param integrationError Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,IntegrationError integrationError,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, IntegrationError.class, integrationError, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>integrationError</var> of type {@link org.openspcoop2.protocol.manifest.IntegrationError}
	 * 
	 * @param integrationError Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(IntegrationError integrationError) throws SerializerException {
		return this.objToXml(IntegrationError.class, integrationError, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>integrationError</var> of type {@link org.openspcoop2.protocol.manifest.IntegrationError}
	 * 
	 * @param integrationError Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(IntegrationError integrationError,boolean prettyPrint) throws SerializerException {
		return this.objToXml(IntegrationError.class, integrationError, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>integrationError</var> of type {@link org.openspcoop2.protocol.manifest.IntegrationError}
	 * 
	 * @param integrationError Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(IntegrationError integrationError) throws SerializerException {
		return this.objToXml(IntegrationError.class, integrationError, false).toString();
	}
	/**
	 * Serialize to String the object <var>integrationError</var> of type {@link org.openspcoop2.protocol.manifest.IntegrationError}
	 * 
	 * @param integrationError Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(IntegrationError integrationError,boolean prettyPrint) throws SerializerException {
		return this.objToXml(IntegrationError.class, integrationError, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: IntegrationConfigurationElementName
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>integrationConfigurationElementName</var> of type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationElementName}
	 * 
	 * @param fileName Xml file to serialize the object <var>integrationConfigurationElementName</var>
	 * @param integrationConfigurationElementName Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,IntegrationConfigurationElementName integrationConfigurationElementName) throws SerializerException {
		this.objToXml(fileName, IntegrationConfigurationElementName.class, integrationConfigurationElementName, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>integrationConfigurationElementName</var> of type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationElementName}
	 * 
	 * @param fileName Xml file to serialize the object <var>integrationConfigurationElementName</var>
	 * @param integrationConfigurationElementName Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,IntegrationConfigurationElementName integrationConfigurationElementName,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, IntegrationConfigurationElementName.class, integrationConfigurationElementName, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>integrationConfigurationElementName</var> of type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationElementName}
	 * 
	 * @param file Xml file to serialize the object <var>integrationConfigurationElementName</var>
	 * @param integrationConfigurationElementName Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,IntegrationConfigurationElementName integrationConfigurationElementName) throws SerializerException {
		this.objToXml(file, IntegrationConfigurationElementName.class, integrationConfigurationElementName, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>integrationConfigurationElementName</var> of type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationElementName}
	 * 
	 * @param file Xml file to serialize the object <var>integrationConfigurationElementName</var>
	 * @param integrationConfigurationElementName Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,IntegrationConfigurationElementName integrationConfigurationElementName,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, IntegrationConfigurationElementName.class, integrationConfigurationElementName, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>integrationConfigurationElementName</var> of type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationElementName}
	 * 
	 * @param out OutputStream to serialize the object <var>integrationConfigurationElementName</var>
	 * @param integrationConfigurationElementName Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,IntegrationConfigurationElementName integrationConfigurationElementName) throws SerializerException {
		this.objToXml(out, IntegrationConfigurationElementName.class, integrationConfigurationElementName, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>integrationConfigurationElementName</var> of type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationElementName}
	 * 
	 * @param out OutputStream to serialize the object <var>integrationConfigurationElementName</var>
	 * @param integrationConfigurationElementName Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,IntegrationConfigurationElementName integrationConfigurationElementName,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, IntegrationConfigurationElementName.class, integrationConfigurationElementName, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>integrationConfigurationElementName</var> of type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationElementName}
	 * 
	 * @param integrationConfigurationElementName Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(IntegrationConfigurationElementName integrationConfigurationElementName) throws SerializerException {
		return this.objToXml(IntegrationConfigurationElementName.class, integrationConfigurationElementName, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>integrationConfigurationElementName</var> of type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationElementName}
	 * 
	 * @param integrationConfigurationElementName Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(IntegrationConfigurationElementName integrationConfigurationElementName,boolean prettyPrint) throws SerializerException {
		return this.objToXml(IntegrationConfigurationElementName.class, integrationConfigurationElementName, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>integrationConfigurationElementName</var> of type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationElementName}
	 * 
	 * @param integrationConfigurationElementName Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(IntegrationConfigurationElementName integrationConfigurationElementName) throws SerializerException {
		return this.objToXml(IntegrationConfigurationElementName.class, integrationConfigurationElementName, false).toString();
	}
	/**
	 * Serialize to String the object <var>integrationConfigurationElementName</var> of type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationElementName}
	 * 
	 * @param integrationConfigurationElementName Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(IntegrationConfigurationElementName integrationConfigurationElementName,boolean prettyPrint) throws SerializerException {
		return this.objToXml(IntegrationConfigurationElementName.class, integrationConfigurationElementName, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: IntegrationConfigurationName
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>integrationConfigurationName</var> of type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationName}
	 * 
	 * @param fileName Xml file to serialize the object <var>integrationConfigurationName</var>
	 * @param integrationConfigurationName Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,IntegrationConfigurationName integrationConfigurationName) throws SerializerException {
		this.objToXml(fileName, IntegrationConfigurationName.class, integrationConfigurationName, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>integrationConfigurationName</var> of type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationName}
	 * 
	 * @param fileName Xml file to serialize the object <var>integrationConfigurationName</var>
	 * @param integrationConfigurationName Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,IntegrationConfigurationName integrationConfigurationName,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, IntegrationConfigurationName.class, integrationConfigurationName, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>integrationConfigurationName</var> of type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationName}
	 * 
	 * @param file Xml file to serialize the object <var>integrationConfigurationName</var>
	 * @param integrationConfigurationName Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,IntegrationConfigurationName integrationConfigurationName) throws SerializerException {
		this.objToXml(file, IntegrationConfigurationName.class, integrationConfigurationName, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>integrationConfigurationName</var> of type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationName}
	 * 
	 * @param file Xml file to serialize the object <var>integrationConfigurationName</var>
	 * @param integrationConfigurationName Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,IntegrationConfigurationName integrationConfigurationName,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, IntegrationConfigurationName.class, integrationConfigurationName, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>integrationConfigurationName</var> of type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationName}
	 * 
	 * @param out OutputStream to serialize the object <var>integrationConfigurationName</var>
	 * @param integrationConfigurationName Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,IntegrationConfigurationName integrationConfigurationName) throws SerializerException {
		this.objToXml(out, IntegrationConfigurationName.class, integrationConfigurationName, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>integrationConfigurationName</var> of type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationName}
	 * 
	 * @param out OutputStream to serialize the object <var>integrationConfigurationName</var>
	 * @param integrationConfigurationName Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,IntegrationConfigurationName integrationConfigurationName,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, IntegrationConfigurationName.class, integrationConfigurationName, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>integrationConfigurationName</var> of type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationName}
	 * 
	 * @param integrationConfigurationName Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(IntegrationConfigurationName integrationConfigurationName) throws SerializerException {
		return this.objToXml(IntegrationConfigurationName.class, integrationConfigurationName, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>integrationConfigurationName</var> of type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationName}
	 * 
	 * @param integrationConfigurationName Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(IntegrationConfigurationName integrationConfigurationName,boolean prettyPrint) throws SerializerException {
		return this.objToXml(IntegrationConfigurationName.class, integrationConfigurationName, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>integrationConfigurationName</var> of type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationName}
	 * 
	 * @param integrationConfigurationName Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(IntegrationConfigurationName integrationConfigurationName) throws SerializerException {
		return this.objToXml(IntegrationConfigurationName.class, integrationConfigurationName, false).toString();
	}
	/**
	 * Serialize to String the object <var>integrationConfigurationName</var> of type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationName}
	 * 
	 * @param integrationConfigurationName Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(IntegrationConfigurationName integrationConfigurationName,boolean prettyPrint) throws SerializerException {
		return this.objToXml(IntegrationConfigurationName.class, integrationConfigurationName, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: IntegrationConfiguration
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>integrationConfiguration</var> of type {@link org.openspcoop2.protocol.manifest.IntegrationConfiguration}
	 * 
	 * @param fileName Xml file to serialize the object <var>integrationConfiguration</var>
	 * @param integrationConfiguration Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,IntegrationConfiguration integrationConfiguration) throws SerializerException {
		this.objToXml(fileName, IntegrationConfiguration.class, integrationConfiguration, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>integrationConfiguration</var> of type {@link org.openspcoop2.protocol.manifest.IntegrationConfiguration}
	 * 
	 * @param fileName Xml file to serialize the object <var>integrationConfiguration</var>
	 * @param integrationConfiguration Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,IntegrationConfiguration integrationConfiguration,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, IntegrationConfiguration.class, integrationConfiguration, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>integrationConfiguration</var> of type {@link org.openspcoop2.protocol.manifest.IntegrationConfiguration}
	 * 
	 * @param file Xml file to serialize the object <var>integrationConfiguration</var>
	 * @param integrationConfiguration Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,IntegrationConfiguration integrationConfiguration) throws SerializerException {
		this.objToXml(file, IntegrationConfiguration.class, integrationConfiguration, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>integrationConfiguration</var> of type {@link org.openspcoop2.protocol.manifest.IntegrationConfiguration}
	 * 
	 * @param file Xml file to serialize the object <var>integrationConfiguration</var>
	 * @param integrationConfiguration Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,IntegrationConfiguration integrationConfiguration,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, IntegrationConfiguration.class, integrationConfiguration, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>integrationConfiguration</var> of type {@link org.openspcoop2.protocol.manifest.IntegrationConfiguration}
	 * 
	 * @param out OutputStream to serialize the object <var>integrationConfiguration</var>
	 * @param integrationConfiguration Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,IntegrationConfiguration integrationConfiguration) throws SerializerException {
		this.objToXml(out, IntegrationConfiguration.class, integrationConfiguration, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>integrationConfiguration</var> of type {@link org.openspcoop2.protocol.manifest.IntegrationConfiguration}
	 * 
	 * @param out OutputStream to serialize the object <var>integrationConfiguration</var>
	 * @param integrationConfiguration Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,IntegrationConfiguration integrationConfiguration,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, IntegrationConfiguration.class, integrationConfiguration, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>integrationConfiguration</var> of type {@link org.openspcoop2.protocol.manifest.IntegrationConfiguration}
	 * 
	 * @param integrationConfiguration Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(IntegrationConfiguration integrationConfiguration) throws SerializerException {
		return this.objToXml(IntegrationConfiguration.class, integrationConfiguration, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>integrationConfiguration</var> of type {@link org.openspcoop2.protocol.manifest.IntegrationConfiguration}
	 * 
	 * @param integrationConfiguration Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(IntegrationConfiguration integrationConfiguration,boolean prettyPrint) throws SerializerException {
		return this.objToXml(IntegrationConfiguration.class, integrationConfiguration, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>integrationConfiguration</var> of type {@link org.openspcoop2.protocol.manifest.IntegrationConfiguration}
	 * 
	 * @param integrationConfiguration Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(IntegrationConfiguration integrationConfiguration) throws SerializerException {
		return this.objToXml(IntegrationConfiguration.class, integrationConfiguration, false).toString();
	}
	/**
	 * Serialize to String the object <var>integrationConfiguration</var> of type {@link org.openspcoop2.protocol.manifest.IntegrationConfiguration}
	 * 
	 * @param integrationConfiguration Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(IntegrationConfiguration integrationConfiguration,boolean prettyPrint) throws SerializerException {
		return this.objToXml(IntegrationConfiguration.class, integrationConfiguration, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: IntegrationConfigurationResourceIdentification
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>integrationConfigurationResourceIdentification</var> of type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationResourceIdentification}
	 * 
	 * @param fileName Xml file to serialize the object <var>integrationConfigurationResourceIdentification</var>
	 * @param integrationConfigurationResourceIdentification Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,IntegrationConfigurationResourceIdentification integrationConfigurationResourceIdentification) throws SerializerException {
		this.objToXml(fileName, IntegrationConfigurationResourceIdentification.class, integrationConfigurationResourceIdentification, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>integrationConfigurationResourceIdentification</var> of type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationResourceIdentification}
	 * 
	 * @param fileName Xml file to serialize the object <var>integrationConfigurationResourceIdentification</var>
	 * @param integrationConfigurationResourceIdentification Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,IntegrationConfigurationResourceIdentification integrationConfigurationResourceIdentification,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, IntegrationConfigurationResourceIdentification.class, integrationConfigurationResourceIdentification, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>integrationConfigurationResourceIdentification</var> of type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationResourceIdentification}
	 * 
	 * @param file Xml file to serialize the object <var>integrationConfigurationResourceIdentification</var>
	 * @param integrationConfigurationResourceIdentification Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,IntegrationConfigurationResourceIdentification integrationConfigurationResourceIdentification) throws SerializerException {
		this.objToXml(file, IntegrationConfigurationResourceIdentification.class, integrationConfigurationResourceIdentification, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>integrationConfigurationResourceIdentification</var> of type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationResourceIdentification}
	 * 
	 * @param file Xml file to serialize the object <var>integrationConfigurationResourceIdentification</var>
	 * @param integrationConfigurationResourceIdentification Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,IntegrationConfigurationResourceIdentification integrationConfigurationResourceIdentification,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, IntegrationConfigurationResourceIdentification.class, integrationConfigurationResourceIdentification, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>integrationConfigurationResourceIdentification</var> of type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationResourceIdentification}
	 * 
	 * @param out OutputStream to serialize the object <var>integrationConfigurationResourceIdentification</var>
	 * @param integrationConfigurationResourceIdentification Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,IntegrationConfigurationResourceIdentification integrationConfigurationResourceIdentification) throws SerializerException {
		this.objToXml(out, IntegrationConfigurationResourceIdentification.class, integrationConfigurationResourceIdentification, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>integrationConfigurationResourceIdentification</var> of type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationResourceIdentification}
	 * 
	 * @param out OutputStream to serialize the object <var>integrationConfigurationResourceIdentification</var>
	 * @param integrationConfigurationResourceIdentification Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,IntegrationConfigurationResourceIdentification integrationConfigurationResourceIdentification,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, IntegrationConfigurationResourceIdentification.class, integrationConfigurationResourceIdentification, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>integrationConfigurationResourceIdentification</var> of type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationResourceIdentification}
	 * 
	 * @param integrationConfigurationResourceIdentification Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(IntegrationConfigurationResourceIdentification integrationConfigurationResourceIdentification) throws SerializerException {
		return this.objToXml(IntegrationConfigurationResourceIdentification.class, integrationConfigurationResourceIdentification, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>integrationConfigurationResourceIdentification</var> of type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationResourceIdentification}
	 * 
	 * @param integrationConfigurationResourceIdentification Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(IntegrationConfigurationResourceIdentification integrationConfigurationResourceIdentification,boolean prettyPrint) throws SerializerException {
		return this.objToXml(IntegrationConfigurationResourceIdentification.class, integrationConfigurationResourceIdentification, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>integrationConfigurationResourceIdentification</var> of type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationResourceIdentification}
	 * 
	 * @param integrationConfigurationResourceIdentification Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(IntegrationConfigurationResourceIdentification integrationConfigurationResourceIdentification) throws SerializerException {
		return this.objToXml(IntegrationConfigurationResourceIdentification.class, integrationConfigurationResourceIdentification, false).toString();
	}
	/**
	 * Serialize to String the object <var>integrationConfigurationResourceIdentification</var> of type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationResourceIdentification}
	 * 
	 * @param integrationConfigurationResourceIdentification Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(IntegrationConfigurationResourceIdentification integrationConfigurationResourceIdentification,boolean prettyPrint) throws SerializerException {
		return this.objToXml(IntegrationConfigurationResourceIdentification.class, integrationConfigurationResourceIdentification, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: IntegrationConfigurationResourceIdentificationModes
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>integrationConfigurationResourceIdentificationModes</var> of type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationResourceIdentificationModes}
	 * 
	 * @param fileName Xml file to serialize the object <var>integrationConfigurationResourceIdentificationModes</var>
	 * @param integrationConfigurationResourceIdentificationModes Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,IntegrationConfigurationResourceIdentificationModes integrationConfigurationResourceIdentificationModes) throws SerializerException {
		this.objToXml(fileName, IntegrationConfigurationResourceIdentificationModes.class, integrationConfigurationResourceIdentificationModes, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>integrationConfigurationResourceIdentificationModes</var> of type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationResourceIdentificationModes}
	 * 
	 * @param fileName Xml file to serialize the object <var>integrationConfigurationResourceIdentificationModes</var>
	 * @param integrationConfigurationResourceIdentificationModes Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,IntegrationConfigurationResourceIdentificationModes integrationConfigurationResourceIdentificationModes,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, IntegrationConfigurationResourceIdentificationModes.class, integrationConfigurationResourceIdentificationModes, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>integrationConfigurationResourceIdentificationModes</var> of type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationResourceIdentificationModes}
	 * 
	 * @param file Xml file to serialize the object <var>integrationConfigurationResourceIdentificationModes</var>
	 * @param integrationConfigurationResourceIdentificationModes Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,IntegrationConfigurationResourceIdentificationModes integrationConfigurationResourceIdentificationModes) throws SerializerException {
		this.objToXml(file, IntegrationConfigurationResourceIdentificationModes.class, integrationConfigurationResourceIdentificationModes, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>integrationConfigurationResourceIdentificationModes</var> of type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationResourceIdentificationModes}
	 * 
	 * @param file Xml file to serialize the object <var>integrationConfigurationResourceIdentificationModes</var>
	 * @param integrationConfigurationResourceIdentificationModes Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,IntegrationConfigurationResourceIdentificationModes integrationConfigurationResourceIdentificationModes,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, IntegrationConfigurationResourceIdentificationModes.class, integrationConfigurationResourceIdentificationModes, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>integrationConfigurationResourceIdentificationModes</var> of type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationResourceIdentificationModes}
	 * 
	 * @param out OutputStream to serialize the object <var>integrationConfigurationResourceIdentificationModes</var>
	 * @param integrationConfigurationResourceIdentificationModes Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,IntegrationConfigurationResourceIdentificationModes integrationConfigurationResourceIdentificationModes) throws SerializerException {
		this.objToXml(out, IntegrationConfigurationResourceIdentificationModes.class, integrationConfigurationResourceIdentificationModes, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>integrationConfigurationResourceIdentificationModes</var> of type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationResourceIdentificationModes}
	 * 
	 * @param out OutputStream to serialize the object <var>integrationConfigurationResourceIdentificationModes</var>
	 * @param integrationConfigurationResourceIdentificationModes Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,IntegrationConfigurationResourceIdentificationModes integrationConfigurationResourceIdentificationModes,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, IntegrationConfigurationResourceIdentificationModes.class, integrationConfigurationResourceIdentificationModes, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>integrationConfigurationResourceIdentificationModes</var> of type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationResourceIdentificationModes}
	 * 
	 * @param integrationConfigurationResourceIdentificationModes Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(IntegrationConfigurationResourceIdentificationModes integrationConfigurationResourceIdentificationModes) throws SerializerException {
		return this.objToXml(IntegrationConfigurationResourceIdentificationModes.class, integrationConfigurationResourceIdentificationModes, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>integrationConfigurationResourceIdentificationModes</var> of type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationResourceIdentificationModes}
	 * 
	 * @param integrationConfigurationResourceIdentificationModes Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(IntegrationConfigurationResourceIdentificationModes integrationConfigurationResourceIdentificationModes,boolean prettyPrint) throws SerializerException {
		return this.objToXml(IntegrationConfigurationResourceIdentificationModes.class, integrationConfigurationResourceIdentificationModes, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>integrationConfigurationResourceIdentificationModes</var> of type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationResourceIdentificationModes}
	 * 
	 * @param integrationConfigurationResourceIdentificationModes Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(IntegrationConfigurationResourceIdentificationModes integrationConfigurationResourceIdentificationModes) throws SerializerException {
		return this.objToXml(IntegrationConfigurationResourceIdentificationModes.class, integrationConfigurationResourceIdentificationModes, false).toString();
	}
	/**
	 * Serialize to String the object <var>integrationConfigurationResourceIdentificationModes</var> of type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationResourceIdentificationModes}
	 * 
	 * @param integrationConfigurationResourceIdentificationModes Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(IntegrationConfigurationResourceIdentificationModes integrationConfigurationResourceIdentificationModes,boolean prettyPrint) throws SerializerException {
		return this.objToXml(IntegrationConfigurationResourceIdentificationModes.class, integrationConfigurationResourceIdentificationModes, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: IntegrationConfigurationResourceIdentificationSpecificResource
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>integrationConfigurationResourceIdentificationSpecificResource</var> of type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationResourceIdentificationSpecificResource}
	 * 
	 * @param fileName Xml file to serialize the object <var>integrationConfigurationResourceIdentificationSpecificResource</var>
	 * @param integrationConfigurationResourceIdentificationSpecificResource Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,IntegrationConfigurationResourceIdentificationSpecificResource integrationConfigurationResourceIdentificationSpecificResource) throws SerializerException {
		this.objToXml(fileName, IntegrationConfigurationResourceIdentificationSpecificResource.class, integrationConfigurationResourceIdentificationSpecificResource, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>integrationConfigurationResourceIdentificationSpecificResource</var> of type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationResourceIdentificationSpecificResource}
	 * 
	 * @param fileName Xml file to serialize the object <var>integrationConfigurationResourceIdentificationSpecificResource</var>
	 * @param integrationConfigurationResourceIdentificationSpecificResource Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,IntegrationConfigurationResourceIdentificationSpecificResource integrationConfigurationResourceIdentificationSpecificResource,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, IntegrationConfigurationResourceIdentificationSpecificResource.class, integrationConfigurationResourceIdentificationSpecificResource, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>integrationConfigurationResourceIdentificationSpecificResource</var> of type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationResourceIdentificationSpecificResource}
	 * 
	 * @param file Xml file to serialize the object <var>integrationConfigurationResourceIdentificationSpecificResource</var>
	 * @param integrationConfigurationResourceIdentificationSpecificResource Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,IntegrationConfigurationResourceIdentificationSpecificResource integrationConfigurationResourceIdentificationSpecificResource) throws SerializerException {
		this.objToXml(file, IntegrationConfigurationResourceIdentificationSpecificResource.class, integrationConfigurationResourceIdentificationSpecificResource, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>integrationConfigurationResourceIdentificationSpecificResource</var> of type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationResourceIdentificationSpecificResource}
	 * 
	 * @param file Xml file to serialize the object <var>integrationConfigurationResourceIdentificationSpecificResource</var>
	 * @param integrationConfigurationResourceIdentificationSpecificResource Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,IntegrationConfigurationResourceIdentificationSpecificResource integrationConfigurationResourceIdentificationSpecificResource,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, IntegrationConfigurationResourceIdentificationSpecificResource.class, integrationConfigurationResourceIdentificationSpecificResource, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>integrationConfigurationResourceIdentificationSpecificResource</var> of type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationResourceIdentificationSpecificResource}
	 * 
	 * @param out OutputStream to serialize the object <var>integrationConfigurationResourceIdentificationSpecificResource</var>
	 * @param integrationConfigurationResourceIdentificationSpecificResource Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,IntegrationConfigurationResourceIdentificationSpecificResource integrationConfigurationResourceIdentificationSpecificResource) throws SerializerException {
		this.objToXml(out, IntegrationConfigurationResourceIdentificationSpecificResource.class, integrationConfigurationResourceIdentificationSpecificResource, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>integrationConfigurationResourceIdentificationSpecificResource</var> of type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationResourceIdentificationSpecificResource}
	 * 
	 * @param out OutputStream to serialize the object <var>integrationConfigurationResourceIdentificationSpecificResource</var>
	 * @param integrationConfigurationResourceIdentificationSpecificResource Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,IntegrationConfigurationResourceIdentificationSpecificResource integrationConfigurationResourceIdentificationSpecificResource,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, IntegrationConfigurationResourceIdentificationSpecificResource.class, integrationConfigurationResourceIdentificationSpecificResource, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>integrationConfigurationResourceIdentificationSpecificResource</var> of type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationResourceIdentificationSpecificResource}
	 * 
	 * @param integrationConfigurationResourceIdentificationSpecificResource Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(IntegrationConfigurationResourceIdentificationSpecificResource integrationConfigurationResourceIdentificationSpecificResource) throws SerializerException {
		return this.objToXml(IntegrationConfigurationResourceIdentificationSpecificResource.class, integrationConfigurationResourceIdentificationSpecificResource, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>integrationConfigurationResourceIdentificationSpecificResource</var> of type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationResourceIdentificationSpecificResource}
	 * 
	 * @param integrationConfigurationResourceIdentificationSpecificResource Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(IntegrationConfigurationResourceIdentificationSpecificResource integrationConfigurationResourceIdentificationSpecificResource,boolean prettyPrint) throws SerializerException {
		return this.objToXml(IntegrationConfigurationResourceIdentificationSpecificResource.class, integrationConfigurationResourceIdentificationSpecificResource, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>integrationConfigurationResourceIdentificationSpecificResource</var> of type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationResourceIdentificationSpecificResource}
	 * 
	 * @param integrationConfigurationResourceIdentificationSpecificResource Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(IntegrationConfigurationResourceIdentificationSpecificResource integrationConfigurationResourceIdentificationSpecificResource) throws SerializerException {
		return this.objToXml(IntegrationConfigurationResourceIdentificationSpecificResource.class, integrationConfigurationResourceIdentificationSpecificResource, false).toString();
	}
	/**
	 * Serialize to String the object <var>integrationConfigurationResourceIdentificationSpecificResource</var> of type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationResourceIdentificationSpecificResource}
	 * 
	 * @param integrationConfigurationResourceIdentificationSpecificResource Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(IntegrationConfigurationResourceIdentificationSpecificResource integrationConfigurationResourceIdentificationSpecificResource,boolean prettyPrint) throws SerializerException {
		return this.objToXml(IntegrationConfigurationResourceIdentificationSpecificResource.class, integrationConfigurationResourceIdentificationSpecificResource, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: SoapHeaderBypassMustUnderstandHeader
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>soapHeaderBypassMustUnderstandHeader</var> of type {@link org.openspcoop2.protocol.manifest.SoapHeaderBypassMustUnderstandHeader}
	 * 
	 * @param fileName Xml file to serialize the object <var>soapHeaderBypassMustUnderstandHeader</var>
	 * @param soapHeaderBypassMustUnderstandHeader Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,SoapHeaderBypassMustUnderstandHeader soapHeaderBypassMustUnderstandHeader) throws SerializerException {
		this.objToXml(fileName, SoapHeaderBypassMustUnderstandHeader.class, soapHeaderBypassMustUnderstandHeader, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>soapHeaderBypassMustUnderstandHeader</var> of type {@link org.openspcoop2.protocol.manifest.SoapHeaderBypassMustUnderstandHeader}
	 * 
	 * @param fileName Xml file to serialize the object <var>soapHeaderBypassMustUnderstandHeader</var>
	 * @param soapHeaderBypassMustUnderstandHeader Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,SoapHeaderBypassMustUnderstandHeader soapHeaderBypassMustUnderstandHeader,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, SoapHeaderBypassMustUnderstandHeader.class, soapHeaderBypassMustUnderstandHeader, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>soapHeaderBypassMustUnderstandHeader</var> of type {@link org.openspcoop2.protocol.manifest.SoapHeaderBypassMustUnderstandHeader}
	 * 
	 * @param file Xml file to serialize the object <var>soapHeaderBypassMustUnderstandHeader</var>
	 * @param soapHeaderBypassMustUnderstandHeader Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,SoapHeaderBypassMustUnderstandHeader soapHeaderBypassMustUnderstandHeader) throws SerializerException {
		this.objToXml(file, SoapHeaderBypassMustUnderstandHeader.class, soapHeaderBypassMustUnderstandHeader, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>soapHeaderBypassMustUnderstandHeader</var> of type {@link org.openspcoop2.protocol.manifest.SoapHeaderBypassMustUnderstandHeader}
	 * 
	 * @param file Xml file to serialize the object <var>soapHeaderBypassMustUnderstandHeader</var>
	 * @param soapHeaderBypassMustUnderstandHeader Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,SoapHeaderBypassMustUnderstandHeader soapHeaderBypassMustUnderstandHeader,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, SoapHeaderBypassMustUnderstandHeader.class, soapHeaderBypassMustUnderstandHeader, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>soapHeaderBypassMustUnderstandHeader</var> of type {@link org.openspcoop2.protocol.manifest.SoapHeaderBypassMustUnderstandHeader}
	 * 
	 * @param out OutputStream to serialize the object <var>soapHeaderBypassMustUnderstandHeader</var>
	 * @param soapHeaderBypassMustUnderstandHeader Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,SoapHeaderBypassMustUnderstandHeader soapHeaderBypassMustUnderstandHeader) throws SerializerException {
		this.objToXml(out, SoapHeaderBypassMustUnderstandHeader.class, soapHeaderBypassMustUnderstandHeader, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>soapHeaderBypassMustUnderstandHeader</var> of type {@link org.openspcoop2.protocol.manifest.SoapHeaderBypassMustUnderstandHeader}
	 * 
	 * @param out OutputStream to serialize the object <var>soapHeaderBypassMustUnderstandHeader</var>
	 * @param soapHeaderBypassMustUnderstandHeader Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,SoapHeaderBypassMustUnderstandHeader soapHeaderBypassMustUnderstandHeader,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, SoapHeaderBypassMustUnderstandHeader.class, soapHeaderBypassMustUnderstandHeader, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>soapHeaderBypassMustUnderstandHeader</var> of type {@link org.openspcoop2.protocol.manifest.SoapHeaderBypassMustUnderstandHeader}
	 * 
	 * @param soapHeaderBypassMustUnderstandHeader Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(SoapHeaderBypassMustUnderstandHeader soapHeaderBypassMustUnderstandHeader) throws SerializerException {
		return this.objToXml(SoapHeaderBypassMustUnderstandHeader.class, soapHeaderBypassMustUnderstandHeader, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>soapHeaderBypassMustUnderstandHeader</var> of type {@link org.openspcoop2.protocol.manifest.SoapHeaderBypassMustUnderstandHeader}
	 * 
	 * @param soapHeaderBypassMustUnderstandHeader Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(SoapHeaderBypassMustUnderstandHeader soapHeaderBypassMustUnderstandHeader,boolean prettyPrint) throws SerializerException {
		return this.objToXml(SoapHeaderBypassMustUnderstandHeader.class, soapHeaderBypassMustUnderstandHeader, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>soapHeaderBypassMustUnderstandHeader</var> of type {@link org.openspcoop2.protocol.manifest.SoapHeaderBypassMustUnderstandHeader}
	 * 
	 * @param soapHeaderBypassMustUnderstandHeader Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(SoapHeaderBypassMustUnderstandHeader soapHeaderBypassMustUnderstandHeader) throws SerializerException {
		return this.objToXml(SoapHeaderBypassMustUnderstandHeader.class, soapHeaderBypassMustUnderstandHeader, false).toString();
	}
	/**
	 * Serialize to String the object <var>soapHeaderBypassMustUnderstandHeader</var> of type {@link org.openspcoop2.protocol.manifest.SoapHeaderBypassMustUnderstandHeader}
	 * 
	 * @param soapHeaderBypassMustUnderstandHeader Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(SoapHeaderBypassMustUnderstandHeader soapHeaderBypassMustUnderstandHeader,boolean prettyPrint) throws SerializerException {
		return this.objToXml(SoapHeaderBypassMustUnderstandHeader.class, soapHeaderBypassMustUnderstandHeader, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: SoapHeaderBypassMustUnderstand
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>soapHeaderBypassMustUnderstand</var> of type {@link org.openspcoop2.protocol.manifest.SoapHeaderBypassMustUnderstand}
	 * 
	 * @param fileName Xml file to serialize the object <var>soapHeaderBypassMustUnderstand</var>
	 * @param soapHeaderBypassMustUnderstand Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,SoapHeaderBypassMustUnderstand soapHeaderBypassMustUnderstand) throws SerializerException {
		this.objToXml(fileName, SoapHeaderBypassMustUnderstand.class, soapHeaderBypassMustUnderstand, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>soapHeaderBypassMustUnderstand</var> of type {@link org.openspcoop2.protocol.manifest.SoapHeaderBypassMustUnderstand}
	 * 
	 * @param fileName Xml file to serialize the object <var>soapHeaderBypassMustUnderstand</var>
	 * @param soapHeaderBypassMustUnderstand Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,SoapHeaderBypassMustUnderstand soapHeaderBypassMustUnderstand,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, SoapHeaderBypassMustUnderstand.class, soapHeaderBypassMustUnderstand, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>soapHeaderBypassMustUnderstand</var> of type {@link org.openspcoop2.protocol.manifest.SoapHeaderBypassMustUnderstand}
	 * 
	 * @param file Xml file to serialize the object <var>soapHeaderBypassMustUnderstand</var>
	 * @param soapHeaderBypassMustUnderstand Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,SoapHeaderBypassMustUnderstand soapHeaderBypassMustUnderstand) throws SerializerException {
		this.objToXml(file, SoapHeaderBypassMustUnderstand.class, soapHeaderBypassMustUnderstand, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>soapHeaderBypassMustUnderstand</var> of type {@link org.openspcoop2.protocol.manifest.SoapHeaderBypassMustUnderstand}
	 * 
	 * @param file Xml file to serialize the object <var>soapHeaderBypassMustUnderstand</var>
	 * @param soapHeaderBypassMustUnderstand Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,SoapHeaderBypassMustUnderstand soapHeaderBypassMustUnderstand,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, SoapHeaderBypassMustUnderstand.class, soapHeaderBypassMustUnderstand, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>soapHeaderBypassMustUnderstand</var> of type {@link org.openspcoop2.protocol.manifest.SoapHeaderBypassMustUnderstand}
	 * 
	 * @param out OutputStream to serialize the object <var>soapHeaderBypassMustUnderstand</var>
	 * @param soapHeaderBypassMustUnderstand Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,SoapHeaderBypassMustUnderstand soapHeaderBypassMustUnderstand) throws SerializerException {
		this.objToXml(out, SoapHeaderBypassMustUnderstand.class, soapHeaderBypassMustUnderstand, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>soapHeaderBypassMustUnderstand</var> of type {@link org.openspcoop2.protocol.manifest.SoapHeaderBypassMustUnderstand}
	 * 
	 * @param out OutputStream to serialize the object <var>soapHeaderBypassMustUnderstand</var>
	 * @param soapHeaderBypassMustUnderstand Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,SoapHeaderBypassMustUnderstand soapHeaderBypassMustUnderstand,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, SoapHeaderBypassMustUnderstand.class, soapHeaderBypassMustUnderstand, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>soapHeaderBypassMustUnderstand</var> of type {@link org.openspcoop2.protocol.manifest.SoapHeaderBypassMustUnderstand}
	 * 
	 * @param soapHeaderBypassMustUnderstand Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(SoapHeaderBypassMustUnderstand soapHeaderBypassMustUnderstand) throws SerializerException {
		return this.objToXml(SoapHeaderBypassMustUnderstand.class, soapHeaderBypassMustUnderstand, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>soapHeaderBypassMustUnderstand</var> of type {@link org.openspcoop2.protocol.manifest.SoapHeaderBypassMustUnderstand}
	 * 
	 * @param soapHeaderBypassMustUnderstand Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(SoapHeaderBypassMustUnderstand soapHeaderBypassMustUnderstand,boolean prettyPrint) throws SerializerException {
		return this.objToXml(SoapHeaderBypassMustUnderstand.class, soapHeaderBypassMustUnderstand, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>soapHeaderBypassMustUnderstand</var> of type {@link org.openspcoop2.protocol.manifest.SoapHeaderBypassMustUnderstand}
	 * 
	 * @param soapHeaderBypassMustUnderstand Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(SoapHeaderBypassMustUnderstand soapHeaderBypassMustUnderstand) throws SerializerException {
		return this.objToXml(SoapHeaderBypassMustUnderstand.class, soapHeaderBypassMustUnderstand, false).toString();
	}
	/**
	 * Serialize to String the object <var>soapHeaderBypassMustUnderstand</var> of type {@link org.openspcoop2.protocol.manifest.SoapHeaderBypassMustUnderstand}
	 * 
	 * @param soapHeaderBypassMustUnderstand Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(SoapHeaderBypassMustUnderstand soapHeaderBypassMustUnderstand,boolean prettyPrint) throws SerializerException {
		return this.objToXml(SoapHeaderBypassMustUnderstand.class, soapHeaderBypassMustUnderstand, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: Organization
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>organization</var> of type {@link org.openspcoop2.protocol.manifest.Organization}
	 * 
	 * @param fileName Xml file to serialize the object <var>organization</var>
	 * @param organization Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Organization organization) throws SerializerException {
		this.objToXml(fileName, Organization.class, organization, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>organization</var> of type {@link org.openspcoop2.protocol.manifest.Organization}
	 * 
	 * @param fileName Xml file to serialize the object <var>organization</var>
	 * @param organization Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Organization organization,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Organization.class, organization, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>organization</var> of type {@link org.openspcoop2.protocol.manifest.Organization}
	 * 
	 * @param file Xml file to serialize the object <var>organization</var>
	 * @param organization Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Organization organization) throws SerializerException {
		this.objToXml(file, Organization.class, organization, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>organization</var> of type {@link org.openspcoop2.protocol.manifest.Organization}
	 * 
	 * @param file Xml file to serialize the object <var>organization</var>
	 * @param organization Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Organization organization,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Organization.class, organization, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>organization</var> of type {@link org.openspcoop2.protocol.manifest.Organization}
	 * 
	 * @param out OutputStream to serialize the object <var>organization</var>
	 * @param organization Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Organization organization) throws SerializerException {
		this.objToXml(out, Organization.class, organization, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>organization</var> of type {@link org.openspcoop2.protocol.manifest.Organization}
	 * 
	 * @param out OutputStream to serialize the object <var>organization</var>
	 * @param organization Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Organization organization,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Organization.class, organization, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>organization</var> of type {@link org.openspcoop2.protocol.manifest.Organization}
	 * 
	 * @param organization Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Organization organization) throws SerializerException {
		return this.objToXml(Organization.class, organization, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>organization</var> of type {@link org.openspcoop2.protocol.manifest.Organization}
	 * 
	 * @param organization Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Organization organization,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Organization.class, organization, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>organization</var> of type {@link org.openspcoop2.protocol.manifest.Organization}
	 * 
	 * @param organization Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Organization organization) throws SerializerException {
		return this.objToXml(Organization.class, organization, false).toString();
	}
	/**
	 * Serialize to String the object <var>organization</var> of type {@link org.openspcoop2.protocol.manifest.Organization}
	 * 
	 * @param organization Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Organization organization,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Organization.class, organization, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: Versions
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>versions</var> of type {@link org.openspcoop2.protocol.manifest.Versions}
	 * 
	 * @param fileName Xml file to serialize the object <var>versions</var>
	 * @param versions Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Versions versions) throws SerializerException {
		this.objToXml(fileName, Versions.class, versions, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>versions</var> of type {@link org.openspcoop2.protocol.manifest.Versions}
	 * 
	 * @param fileName Xml file to serialize the object <var>versions</var>
	 * @param versions Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Versions versions,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Versions.class, versions, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>versions</var> of type {@link org.openspcoop2.protocol.manifest.Versions}
	 * 
	 * @param file Xml file to serialize the object <var>versions</var>
	 * @param versions Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Versions versions) throws SerializerException {
		this.objToXml(file, Versions.class, versions, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>versions</var> of type {@link org.openspcoop2.protocol.manifest.Versions}
	 * 
	 * @param file Xml file to serialize the object <var>versions</var>
	 * @param versions Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Versions versions,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Versions.class, versions, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>versions</var> of type {@link org.openspcoop2.protocol.manifest.Versions}
	 * 
	 * @param out OutputStream to serialize the object <var>versions</var>
	 * @param versions Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Versions versions) throws SerializerException {
		this.objToXml(out, Versions.class, versions, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>versions</var> of type {@link org.openspcoop2.protocol.manifest.Versions}
	 * 
	 * @param out OutputStream to serialize the object <var>versions</var>
	 * @param versions Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Versions versions,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Versions.class, versions, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>versions</var> of type {@link org.openspcoop2.protocol.manifest.Versions}
	 * 
	 * @param versions Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Versions versions) throws SerializerException {
		return this.objToXml(Versions.class, versions, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>versions</var> of type {@link org.openspcoop2.protocol.manifest.Versions}
	 * 
	 * @param versions Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Versions versions,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Versions.class, versions, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>versions</var> of type {@link org.openspcoop2.protocol.manifest.Versions}
	 * 
	 * @param versions Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Versions versions) throws SerializerException {
		return this.objToXml(Versions.class, versions, false).toString();
	}
	/**
	 * Serialize to String the object <var>versions</var> of type {@link org.openspcoop2.protocol.manifest.Versions}
	 * 
	 * @param versions Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Versions versions,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Versions.class, versions, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: SubContextMapping
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>subContextMapping</var> of type {@link org.openspcoop2.protocol.manifest.SubContextMapping}
	 * 
	 * @param fileName Xml file to serialize the object <var>subContextMapping</var>
	 * @param subContextMapping Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,SubContextMapping subContextMapping) throws SerializerException {
		this.objToXml(fileName, SubContextMapping.class, subContextMapping, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>subContextMapping</var> of type {@link org.openspcoop2.protocol.manifest.SubContextMapping}
	 * 
	 * @param fileName Xml file to serialize the object <var>subContextMapping</var>
	 * @param subContextMapping Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,SubContextMapping subContextMapping,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, SubContextMapping.class, subContextMapping, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>subContextMapping</var> of type {@link org.openspcoop2.protocol.manifest.SubContextMapping}
	 * 
	 * @param file Xml file to serialize the object <var>subContextMapping</var>
	 * @param subContextMapping Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,SubContextMapping subContextMapping) throws SerializerException {
		this.objToXml(file, SubContextMapping.class, subContextMapping, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>subContextMapping</var> of type {@link org.openspcoop2.protocol.manifest.SubContextMapping}
	 * 
	 * @param file Xml file to serialize the object <var>subContextMapping</var>
	 * @param subContextMapping Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,SubContextMapping subContextMapping,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, SubContextMapping.class, subContextMapping, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>subContextMapping</var> of type {@link org.openspcoop2.protocol.manifest.SubContextMapping}
	 * 
	 * @param out OutputStream to serialize the object <var>subContextMapping</var>
	 * @param subContextMapping Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,SubContextMapping subContextMapping) throws SerializerException {
		this.objToXml(out, SubContextMapping.class, subContextMapping, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>subContextMapping</var> of type {@link org.openspcoop2.protocol.manifest.SubContextMapping}
	 * 
	 * @param out OutputStream to serialize the object <var>subContextMapping</var>
	 * @param subContextMapping Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,SubContextMapping subContextMapping,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, SubContextMapping.class, subContextMapping, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>subContextMapping</var> of type {@link org.openspcoop2.protocol.manifest.SubContextMapping}
	 * 
	 * @param subContextMapping Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(SubContextMapping subContextMapping) throws SerializerException {
		return this.objToXml(SubContextMapping.class, subContextMapping, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>subContextMapping</var> of type {@link org.openspcoop2.protocol.manifest.SubContextMapping}
	 * 
	 * @param subContextMapping Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(SubContextMapping subContextMapping,boolean prettyPrint) throws SerializerException {
		return this.objToXml(SubContextMapping.class, subContextMapping, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>subContextMapping</var> of type {@link org.openspcoop2.protocol.manifest.SubContextMapping}
	 * 
	 * @param subContextMapping Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(SubContextMapping subContextMapping) throws SerializerException {
		return this.objToXml(SubContextMapping.class, subContextMapping, false).toString();
	}
	/**
	 * Serialize to String the object <var>subContextMapping</var> of type {@link org.openspcoop2.protocol.manifest.SubContextMapping}
	 * 
	 * @param subContextMapping Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(SubContextMapping subContextMapping,boolean prettyPrint) throws SerializerException {
		return this.objToXml(SubContextMapping.class, subContextMapping, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: WebEmptyContext
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>webEmptyContext</var> of type {@link org.openspcoop2.protocol.manifest.WebEmptyContext}
	 * 
	 * @param fileName Xml file to serialize the object <var>webEmptyContext</var>
	 * @param webEmptyContext Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,WebEmptyContext webEmptyContext) throws SerializerException {
		this.objToXml(fileName, WebEmptyContext.class, webEmptyContext, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>webEmptyContext</var> of type {@link org.openspcoop2.protocol.manifest.WebEmptyContext}
	 * 
	 * @param fileName Xml file to serialize the object <var>webEmptyContext</var>
	 * @param webEmptyContext Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,WebEmptyContext webEmptyContext,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, WebEmptyContext.class, webEmptyContext, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>webEmptyContext</var> of type {@link org.openspcoop2.protocol.manifest.WebEmptyContext}
	 * 
	 * @param file Xml file to serialize the object <var>webEmptyContext</var>
	 * @param webEmptyContext Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,WebEmptyContext webEmptyContext) throws SerializerException {
		this.objToXml(file, WebEmptyContext.class, webEmptyContext, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>webEmptyContext</var> of type {@link org.openspcoop2.protocol.manifest.WebEmptyContext}
	 * 
	 * @param file Xml file to serialize the object <var>webEmptyContext</var>
	 * @param webEmptyContext Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,WebEmptyContext webEmptyContext,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, WebEmptyContext.class, webEmptyContext, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>webEmptyContext</var> of type {@link org.openspcoop2.protocol.manifest.WebEmptyContext}
	 * 
	 * @param out OutputStream to serialize the object <var>webEmptyContext</var>
	 * @param webEmptyContext Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,WebEmptyContext webEmptyContext) throws SerializerException {
		this.objToXml(out, WebEmptyContext.class, webEmptyContext, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>webEmptyContext</var> of type {@link org.openspcoop2.protocol.manifest.WebEmptyContext}
	 * 
	 * @param out OutputStream to serialize the object <var>webEmptyContext</var>
	 * @param webEmptyContext Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,WebEmptyContext webEmptyContext,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, WebEmptyContext.class, webEmptyContext, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>webEmptyContext</var> of type {@link org.openspcoop2.protocol.manifest.WebEmptyContext}
	 * 
	 * @param webEmptyContext Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(WebEmptyContext webEmptyContext) throws SerializerException {
		return this.objToXml(WebEmptyContext.class, webEmptyContext, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>webEmptyContext</var> of type {@link org.openspcoop2.protocol.manifest.WebEmptyContext}
	 * 
	 * @param webEmptyContext Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(WebEmptyContext webEmptyContext,boolean prettyPrint) throws SerializerException {
		return this.objToXml(WebEmptyContext.class, webEmptyContext, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>webEmptyContext</var> of type {@link org.openspcoop2.protocol.manifest.WebEmptyContext}
	 * 
	 * @param webEmptyContext Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(WebEmptyContext webEmptyContext) throws SerializerException {
		return this.objToXml(WebEmptyContext.class, webEmptyContext, false).toString();
	}
	/**
	 * Serialize to String the object <var>webEmptyContext</var> of type {@link org.openspcoop2.protocol.manifest.WebEmptyContext}
	 * 
	 * @param webEmptyContext Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(WebEmptyContext webEmptyContext,boolean prettyPrint) throws SerializerException {
		return this.objToXml(WebEmptyContext.class, webEmptyContext, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: EmptySubContextMapping
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>emptySubContextMapping</var> of type {@link org.openspcoop2.protocol.manifest.EmptySubContextMapping}
	 * 
	 * @param fileName Xml file to serialize the object <var>emptySubContextMapping</var>
	 * @param emptySubContextMapping Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,EmptySubContextMapping emptySubContextMapping) throws SerializerException {
		this.objToXml(fileName, EmptySubContextMapping.class, emptySubContextMapping, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>emptySubContextMapping</var> of type {@link org.openspcoop2.protocol.manifest.EmptySubContextMapping}
	 * 
	 * @param fileName Xml file to serialize the object <var>emptySubContextMapping</var>
	 * @param emptySubContextMapping Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,EmptySubContextMapping emptySubContextMapping,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, EmptySubContextMapping.class, emptySubContextMapping, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>emptySubContextMapping</var> of type {@link org.openspcoop2.protocol.manifest.EmptySubContextMapping}
	 * 
	 * @param file Xml file to serialize the object <var>emptySubContextMapping</var>
	 * @param emptySubContextMapping Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,EmptySubContextMapping emptySubContextMapping) throws SerializerException {
		this.objToXml(file, EmptySubContextMapping.class, emptySubContextMapping, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>emptySubContextMapping</var> of type {@link org.openspcoop2.protocol.manifest.EmptySubContextMapping}
	 * 
	 * @param file Xml file to serialize the object <var>emptySubContextMapping</var>
	 * @param emptySubContextMapping Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,EmptySubContextMapping emptySubContextMapping,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, EmptySubContextMapping.class, emptySubContextMapping, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>emptySubContextMapping</var> of type {@link org.openspcoop2.protocol.manifest.EmptySubContextMapping}
	 * 
	 * @param out OutputStream to serialize the object <var>emptySubContextMapping</var>
	 * @param emptySubContextMapping Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,EmptySubContextMapping emptySubContextMapping) throws SerializerException {
		this.objToXml(out, EmptySubContextMapping.class, emptySubContextMapping, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>emptySubContextMapping</var> of type {@link org.openspcoop2.protocol.manifest.EmptySubContextMapping}
	 * 
	 * @param out OutputStream to serialize the object <var>emptySubContextMapping</var>
	 * @param emptySubContextMapping Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,EmptySubContextMapping emptySubContextMapping,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, EmptySubContextMapping.class, emptySubContextMapping, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>emptySubContextMapping</var> of type {@link org.openspcoop2.protocol.manifest.EmptySubContextMapping}
	 * 
	 * @param emptySubContextMapping Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(EmptySubContextMapping emptySubContextMapping) throws SerializerException {
		return this.objToXml(EmptySubContextMapping.class, emptySubContextMapping, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>emptySubContextMapping</var> of type {@link org.openspcoop2.protocol.manifest.EmptySubContextMapping}
	 * 
	 * @param emptySubContextMapping Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(EmptySubContextMapping emptySubContextMapping,boolean prettyPrint) throws SerializerException {
		return this.objToXml(EmptySubContextMapping.class, emptySubContextMapping, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>emptySubContextMapping</var> of type {@link org.openspcoop2.protocol.manifest.EmptySubContextMapping}
	 * 
	 * @param emptySubContextMapping Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(EmptySubContextMapping emptySubContextMapping) throws SerializerException {
		return this.objToXml(EmptySubContextMapping.class, emptySubContextMapping, false).toString();
	}
	/**
	 * Serialize to String the object <var>emptySubContextMapping</var> of type {@link org.openspcoop2.protocol.manifest.EmptySubContextMapping}
	 * 
	 * @param emptySubContextMapping Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(EmptySubContextMapping emptySubContextMapping,boolean prettyPrint) throws SerializerException {
		return this.objToXml(EmptySubContextMapping.class, emptySubContextMapping, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: RestMediaTypeMapping
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>restMediaTypeMapping</var> of type {@link org.openspcoop2.protocol.manifest.RestMediaTypeMapping}
	 * 
	 * @param fileName Xml file to serialize the object <var>restMediaTypeMapping</var>
	 * @param restMediaTypeMapping Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,RestMediaTypeMapping restMediaTypeMapping) throws SerializerException {
		this.objToXml(fileName, RestMediaTypeMapping.class, restMediaTypeMapping, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>restMediaTypeMapping</var> of type {@link org.openspcoop2.protocol.manifest.RestMediaTypeMapping}
	 * 
	 * @param fileName Xml file to serialize the object <var>restMediaTypeMapping</var>
	 * @param restMediaTypeMapping Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,RestMediaTypeMapping restMediaTypeMapping,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, RestMediaTypeMapping.class, restMediaTypeMapping, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>restMediaTypeMapping</var> of type {@link org.openspcoop2.protocol.manifest.RestMediaTypeMapping}
	 * 
	 * @param file Xml file to serialize the object <var>restMediaTypeMapping</var>
	 * @param restMediaTypeMapping Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,RestMediaTypeMapping restMediaTypeMapping) throws SerializerException {
		this.objToXml(file, RestMediaTypeMapping.class, restMediaTypeMapping, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>restMediaTypeMapping</var> of type {@link org.openspcoop2.protocol.manifest.RestMediaTypeMapping}
	 * 
	 * @param file Xml file to serialize the object <var>restMediaTypeMapping</var>
	 * @param restMediaTypeMapping Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,RestMediaTypeMapping restMediaTypeMapping,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, RestMediaTypeMapping.class, restMediaTypeMapping, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>restMediaTypeMapping</var> of type {@link org.openspcoop2.protocol.manifest.RestMediaTypeMapping}
	 * 
	 * @param out OutputStream to serialize the object <var>restMediaTypeMapping</var>
	 * @param restMediaTypeMapping Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,RestMediaTypeMapping restMediaTypeMapping) throws SerializerException {
		this.objToXml(out, RestMediaTypeMapping.class, restMediaTypeMapping, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>restMediaTypeMapping</var> of type {@link org.openspcoop2.protocol.manifest.RestMediaTypeMapping}
	 * 
	 * @param out OutputStream to serialize the object <var>restMediaTypeMapping</var>
	 * @param restMediaTypeMapping Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,RestMediaTypeMapping restMediaTypeMapping,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, RestMediaTypeMapping.class, restMediaTypeMapping, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>restMediaTypeMapping</var> of type {@link org.openspcoop2.protocol.manifest.RestMediaTypeMapping}
	 * 
	 * @param restMediaTypeMapping Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(RestMediaTypeMapping restMediaTypeMapping) throws SerializerException {
		return this.objToXml(RestMediaTypeMapping.class, restMediaTypeMapping, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>restMediaTypeMapping</var> of type {@link org.openspcoop2.protocol.manifest.RestMediaTypeMapping}
	 * 
	 * @param restMediaTypeMapping Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(RestMediaTypeMapping restMediaTypeMapping,boolean prettyPrint) throws SerializerException {
		return this.objToXml(RestMediaTypeMapping.class, restMediaTypeMapping, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>restMediaTypeMapping</var> of type {@link org.openspcoop2.protocol.manifest.RestMediaTypeMapping}
	 * 
	 * @param restMediaTypeMapping Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(RestMediaTypeMapping restMediaTypeMapping) throws SerializerException {
		return this.objToXml(RestMediaTypeMapping.class, restMediaTypeMapping, false).toString();
	}
	/**
	 * Serialize to String the object <var>restMediaTypeMapping</var> of type {@link org.openspcoop2.protocol.manifest.RestMediaTypeMapping}
	 * 
	 * @param restMediaTypeMapping Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(RestMediaTypeMapping restMediaTypeMapping,boolean prettyPrint) throws SerializerException {
		return this.objToXml(RestMediaTypeMapping.class, restMediaTypeMapping, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: OrganizationTypes
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>organizationTypes</var> of type {@link org.openspcoop2.protocol.manifest.OrganizationTypes}
	 * 
	 * @param fileName Xml file to serialize the object <var>organizationTypes</var>
	 * @param organizationTypes Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,OrganizationTypes organizationTypes) throws SerializerException {
		this.objToXml(fileName, OrganizationTypes.class, organizationTypes, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>organizationTypes</var> of type {@link org.openspcoop2.protocol.manifest.OrganizationTypes}
	 * 
	 * @param fileName Xml file to serialize the object <var>organizationTypes</var>
	 * @param organizationTypes Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,OrganizationTypes organizationTypes,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, OrganizationTypes.class, organizationTypes, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>organizationTypes</var> of type {@link org.openspcoop2.protocol.manifest.OrganizationTypes}
	 * 
	 * @param file Xml file to serialize the object <var>organizationTypes</var>
	 * @param organizationTypes Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,OrganizationTypes organizationTypes) throws SerializerException {
		this.objToXml(file, OrganizationTypes.class, organizationTypes, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>organizationTypes</var> of type {@link org.openspcoop2.protocol.manifest.OrganizationTypes}
	 * 
	 * @param file Xml file to serialize the object <var>organizationTypes</var>
	 * @param organizationTypes Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,OrganizationTypes organizationTypes,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, OrganizationTypes.class, organizationTypes, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>organizationTypes</var> of type {@link org.openspcoop2.protocol.manifest.OrganizationTypes}
	 * 
	 * @param out OutputStream to serialize the object <var>organizationTypes</var>
	 * @param organizationTypes Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,OrganizationTypes organizationTypes) throws SerializerException {
		this.objToXml(out, OrganizationTypes.class, organizationTypes, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>organizationTypes</var> of type {@link org.openspcoop2.protocol.manifest.OrganizationTypes}
	 * 
	 * @param out OutputStream to serialize the object <var>organizationTypes</var>
	 * @param organizationTypes Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,OrganizationTypes organizationTypes,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, OrganizationTypes.class, organizationTypes, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>organizationTypes</var> of type {@link org.openspcoop2.protocol.manifest.OrganizationTypes}
	 * 
	 * @param organizationTypes Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(OrganizationTypes organizationTypes) throws SerializerException {
		return this.objToXml(OrganizationTypes.class, organizationTypes, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>organizationTypes</var> of type {@link org.openspcoop2.protocol.manifest.OrganizationTypes}
	 * 
	 * @param organizationTypes Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(OrganizationTypes organizationTypes,boolean prettyPrint) throws SerializerException {
		return this.objToXml(OrganizationTypes.class, organizationTypes, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>organizationTypes</var> of type {@link org.openspcoop2.protocol.manifest.OrganizationTypes}
	 * 
	 * @param organizationTypes Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(OrganizationTypes organizationTypes) throws SerializerException {
		return this.objToXml(OrganizationTypes.class, organizationTypes, false).toString();
	}
	/**
	 * Serialize to String the object <var>organizationTypes</var> of type {@link org.openspcoop2.protocol.manifest.OrganizationTypes}
	 * 
	 * @param organizationTypes Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(OrganizationTypes organizationTypes,boolean prettyPrint) throws SerializerException {
		return this.objToXml(OrganizationTypes.class, organizationTypes, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: InterfaceConfiguration
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>interfaceConfiguration</var> of type {@link org.openspcoop2.protocol.manifest.InterfaceConfiguration}
	 * 
	 * @param fileName Xml file to serialize the object <var>interfaceConfiguration</var>
	 * @param interfaceConfiguration Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,InterfaceConfiguration interfaceConfiguration) throws SerializerException {
		this.objToXml(fileName, InterfaceConfiguration.class, interfaceConfiguration, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>interfaceConfiguration</var> of type {@link org.openspcoop2.protocol.manifest.InterfaceConfiguration}
	 * 
	 * @param fileName Xml file to serialize the object <var>interfaceConfiguration</var>
	 * @param interfaceConfiguration Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,InterfaceConfiguration interfaceConfiguration,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, InterfaceConfiguration.class, interfaceConfiguration, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>interfaceConfiguration</var> of type {@link org.openspcoop2.protocol.manifest.InterfaceConfiguration}
	 * 
	 * @param file Xml file to serialize the object <var>interfaceConfiguration</var>
	 * @param interfaceConfiguration Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,InterfaceConfiguration interfaceConfiguration) throws SerializerException {
		this.objToXml(file, InterfaceConfiguration.class, interfaceConfiguration, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>interfaceConfiguration</var> of type {@link org.openspcoop2.protocol.manifest.InterfaceConfiguration}
	 * 
	 * @param file Xml file to serialize the object <var>interfaceConfiguration</var>
	 * @param interfaceConfiguration Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,InterfaceConfiguration interfaceConfiguration,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, InterfaceConfiguration.class, interfaceConfiguration, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>interfaceConfiguration</var> of type {@link org.openspcoop2.protocol.manifest.InterfaceConfiguration}
	 * 
	 * @param out OutputStream to serialize the object <var>interfaceConfiguration</var>
	 * @param interfaceConfiguration Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,InterfaceConfiguration interfaceConfiguration) throws SerializerException {
		this.objToXml(out, InterfaceConfiguration.class, interfaceConfiguration, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>interfaceConfiguration</var> of type {@link org.openspcoop2.protocol.manifest.InterfaceConfiguration}
	 * 
	 * @param out OutputStream to serialize the object <var>interfaceConfiguration</var>
	 * @param interfaceConfiguration Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,InterfaceConfiguration interfaceConfiguration,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, InterfaceConfiguration.class, interfaceConfiguration, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>interfaceConfiguration</var> of type {@link org.openspcoop2.protocol.manifest.InterfaceConfiguration}
	 * 
	 * @param interfaceConfiguration Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(InterfaceConfiguration interfaceConfiguration) throws SerializerException {
		return this.objToXml(InterfaceConfiguration.class, interfaceConfiguration, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>interfaceConfiguration</var> of type {@link org.openspcoop2.protocol.manifest.InterfaceConfiguration}
	 * 
	 * @param interfaceConfiguration Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(InterfaceConfiguration interfaceConfiguration,boolean prettyPrint) throws SerializerException {
		return this.objToXml(InterfaceConfiguration.class, interfaceConfiguration, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>interfaceConfiguration</var> of type {@link org.openspcoop2.protocol.manifest.InterfaceConfiguration}
	 * 
	 * @param interfaceConfiguration Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(InterfaceConfiguration interfaceConfiguration) throws SerializerException {
		return this.objToXml(InterfaceConfiguration.class, interfaceConfiguration, false).toString();
	}
	/**
	 * Serialize to String the object <var>interfaceConfiguration</var> of type {@link org.openspcoop2.protocol.manifest.InterfaceConfiguration}
	 * 
	 * @param interfaceConfiguration Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(InterfaceConfiguration interfaceConfiguration,boolean prettyPrint) throws SerializerException {
		return this.objToXml(InterfaceConfiguration.class, interfaceConfiguration, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: Version
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>version</var> of type {@link org.openspcoop2.protocol.manifest.Version}
	 * 
	 * @param fileName Xml file to serialize the object <var>version</var>
	 * @param version Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Version version) throws SerializerException {
		this.objToXml(fileName, Version.class, version, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>version</var> of type {@link org.openspcoop2.protocol.manifest.Version}
	 * 
	 * @param fileName Xml file to serialize the object <var>version</var>
	 * @param version Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Version version,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Version.class, version, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>version</var> of type {@link org.openspcoop2.protocol.manifest.Version}
	 * 
	 * @param file Xml file to serialize the object <var>version</var>
	 * @param version Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Version version) throws SerializerException {
		this.objToXml(file, Version.class, version, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>version</var> of type {@link org.openspcoop2.protocol.manifest.Version}
	 * 
	 * @param file Xml file to serialize the object <var>version</var>
	 * @param version Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Version version,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Version.class, version, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>version</var> of type {@link org.openspcoop2.protocol.manifest.Version}
	 * 
	 * @param out OutputStream to serialize the object <var>version</var>
	 * @param version Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Version version) throws SerializerException {
		this.objToXml(out, Version.class, version, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>version</var> of type {@link org.openspcoop2.protocol.manifest.Version}
	 * 
	 * @param out OutputStream to serialize the object <var>version</var>
	 * @param version Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Version version,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Version.class, version, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>version</var> of type {@link org.openspcoop2.protocol.manifest.Version}
	 * 
	 * @param version Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Version version) throws SerializerException {
		return this.objToXml(Version.class, version, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>version</var> of type {@link org.openspcoop2.protocol.manifest.Version}
	 * 
	 * @param version Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Version version,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Version.class, version, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>version</var> of type {@link org.openspcoop2.protocol.manifest.Version}
	 * 
	 * @param version Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Version version) throws SerializerException {
		return this.objToXml(Version.class, version, false).toString();
	}
	/**
	 * Serialize to String the object <var>version</var> of type {@link org.openspcoop2.protocol.manifest.Version}
	 * 
	 * @param version Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Version version,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Version.class, version, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: CollaborationProfile
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>collaborationProfile</var> of type {@link org.openspcoop2.protocol.manifest.CollaborationProfile}
	 * 
	 * @param fileName Xml file to serialize the object <var>collaborationProfile</var>
	 * @param collaborationProfile Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,CollaborationProfile collaborationProfile) throws SerializerException {
		this.objToXml(fileName, CollaborationProfile.class, collaborationProfile, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>collaborationProfile</var> of type {@link org.openspcoop2.protocol.manifest.CollaborationProfile}
	 * 
	 * @param fileName Xml file to serialize the object <var>collaborationProfile</var>
	 * @param collaborationProfile Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,CollaborationProfile collaborationProfile,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, CollaborationProfile.class, collaborationProfile, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>collaborationProfile</var> of type {@link org.openspcoop2.protocol.manifest.CollaborationProfile}
	 * 
	 * @param file Xml file to serialize the object <var>collaborationProfile</var>
	 * @param collaborationProfile Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,CollaborationProfile collaborationProfile) throws SerializerException {
		this.objToXml(file, CollaborationProfile.class, collaborationProfile, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>collaborationProfile</var> of type {@link org.openspcoop2.protocol.manifest.CollaborationProfile}
	 * 
	 * @param file Xml file to serialize the object <var>collaborationProfile</var>
	 * @param collaborationProfile Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,CollaborationProfile collaborationProfile,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, CollaborationProfile.class, collaborationProfile, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>collaborationProfile</var> of type {@link org.openspcoop2.protocol.manifest.CollaborationProfile}
	 * 
	 * @param out OutputStream to serialize the object <var>collaborationProfile</var>
	 * @param collaborationProfile Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,CollaborationProfile collaborationProfile) throws SerializerException {
		this.objToXml(out, CollaborationProfile.class, collaborationProfile, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>collaborationProfile</var> of type {@link org.openspcoop2.protocol.manifest.CollaborationProfile}
	 * 
	 * @param out OutputStream to serialize the object <var>collaborationProfile</var>
	 * @param collaborationProfile Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,CollaborationProfile collaborationProfile,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, CollaborationProfile.class, collaborationProfile, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>collaborationProfile</var> of type {@link org.openspcoop2.protocol.manifest.CollaborationProfile}
	 * 
	 * @param collaborationProfile Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(CollaborationProfile collaborationProfile) throws SerializerException {
		return this.objToXml(CollaborationProfile.class, collaborationProfile, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>collaborationProfile</var> of type {@link org.openspcoop2.protocol.manifest.CollaborationProfile}
	 * 
	 * @param collaborationProfile Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(CollaborationProfile collaborationProfile,boolean prettyPrint) throws SerializerException {
		return this.objToXml(CollaborationProfile.class, collaborationProfile, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>collaborationProfile</var> of type {@link org.openspcoop2.protocol.manifest.CollaborationProfile}
	 * 
	 * @param collaborationProfile Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(CollaborationProfile collaborationProfile) throws SerializerException {
		return this.objToXml(CollaborationProfile.class, collaborationProfile, false).toString();
	}
	/**
	 * Serialize to String the object <var>collaborationProfile</var> of type {@link org.openspcoop2.protocol.manifest.CollaborationProfile}
	 * 
	 * @param collaborationProfile Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(CollaborationProfile collaborationProfile,boolean prettyPrint) throws SerializerException {
		return this.objToXml(CollaborationProfile.class, collaborationProfile, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: ServiceType
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>serviceType</var> of type {@link org.openspcoop2.protocol.manifest.ServiceType}
	 * 
	 * @param fileName Xml file to serialize the object <var>serviceType</var>
	 * @param serviceType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ServiceType serviceType) throws SerializerException {
		this.objToXml(fileName, ServiceType.class, serviceType, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>serviceType</var> of type {@link org.openspcoop2.protocol.manifest.ServiceType}
	 * 
	 * @param fileName Xml file to serialize the object <var>serviceType</var>
	 * @param serviceType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ServiceType serviceType,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, ServiceType.class, serviceType, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>serviceType</var> of type {@link org.openspcoop2.protocol.manifest.ServiceType}
	 * 
	 * @param file Xml file to serialize the object <var>serviceType</var>
	 * @param serviceType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ServiceType serviceType) throws SerializerException {
		this.objToXml(file, ServiceType.class, serviceType, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>serviceType</var> of type {@link org.openspcoop2.protocol.manifest.ServiceType}
	 * 
	 * @param file Xml file to serialize the object <var>serviceType</var>
	 * @param serviceType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ServiceType serviceType,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, ServiceType.class, serviceType, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>serviceType</var> of type {@link org.openspcoop2.protocol.manifest.ServiceType}
	 * 
	 * @param out OutputStream to serialize the object <var>serviceType</var>
	 * @param serviceType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ServiceType serviceType) throws SerializerException {
		this.objToXml(out, ServiceType.class, serviceType, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>serviceType</var> of type {@link org.openspcoop2.protocol.manifest.ServiceType}
	 * 
	 * @param out OutputStream to serialize the object <var>serviceType</var>
	 * @param serviceType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ServiceType serviceType,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, ServiceType.class, serviceType, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>serviceType</var> of type {@link org.openspcoop2.protocol.manifest.ServiceType}
	 * 
	 * @param serviceType Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ServiceType serviceType) throws SerializerException {
		return this.objToXml(ServiceType.class, serviceType, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>serviceType</var> of type {@link org.openspcoop2.protocol.manifest.ServiceType}
	 * 
	 * @param serviceType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ServiceType serviceType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ServiceType.class, serviceType, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>serviceType</var> of type {@link org.openspcoop2.protocol.manifest.ServiceType}
	 * 
	 * @param serviceType Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ServiceType serviceType) throws SerializerException {
		return this.objToXml(ServiceType.class, serviceType, false).toString();
	}
	/**
	 * Serialize to String the object <var>serviceType</var> of type {@link org.openspcoop2.protocol.manifest.ServiceType}
	 * 
	 * @param serviceType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ServiceType serviceType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ServiceType.class, serviceType, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: transaction
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>transaction</var> of type {@link org.openspcoop2.protocol.manifest.Transaction}
	 * 
	 * @param fileName Xml file to serialize the object <var>transaction</var>
	 * @param transaction Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Transaction transaction) throws SerializerException {
		this.objToXml(fileName, Transaction.class, transaction, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>transaction</var> of type {@link org.openspcoop2.protocol.manifest.Transaction}
	 * 
	 * @param fileName Xml file to serialize the object <var>transaction</var>
	 * @param transaction Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Transaction transaction,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Transaction.class, transaction, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>transaction</var> of type {@link org.openspcoop2.protocol.manifest.Transaction}
	 * 
	 * @param file Xml file to serialize the object <var>transaction</var>
	 * @param transaction Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Transaction transaction) throws SerializerException {
		this.objToXml(file, Transaction.class, transaction, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>transaction</var> of type {@link org.openspcoop2.protocol.manifest.Transaction}
	 * 
	 * @param file Xml file to serialize the object <var>transaction</var>
	 * @param transaction Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Transaction transaction,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Transaction.class, transaction, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>transaction</var> of type {@link org.openspcoop2.protocol.manifest.Transaction}
	 * 
	 * @param out OutputStream to serialize the object <var>transaction</var>
	 * @param transaction Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Transaction transaction) throws SerializerException {
		this.objToXml(out, Transaction.class, transaction, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>transaction</var> of type {@link org.openspcoop2.protocol.manifest.Transaction}
	 * 
	 * @param out OutputStream to serialize the object <var>transaction</var>
	 * @param transaction Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Transaction transaction,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Transaction.class, transaction, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>transaction</var> of type {@link org.openspcoop2.protocol.manifest.Transaction}
	 * 
	 * @param transaction Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Transaction transaction) throws SerializerException {
		return this.objToXml(Transaction.class, transaction, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>transaction</var> of type {@link org.openspcoop2.protocol.manifest.Transaction}
	 * 
	 * @param transaction Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Transaction transaction,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Transaction.class, transaction, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>transaction</var> of type {@link org.openspcoop2.protocol.manifest.Transaction}
	 * 
	 * @param transaction Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Transaction transaction) throws SerializerException {
		return this.objToXml(Transaction.class, transaction, false).toString();
	}
	/**
	 * Serialize to String the object <var>transaction</var> of type {@link org.openspcoop2.protocol.manifest.Transaction}
	 * 
	 * @param transaction Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Transaction transaction,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Transaction.class, transaction, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: IntegrationConfigurationResourceIdentificationMode
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>integrationConfigurationResourceIdentificationMode</var> of type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationResourceIdentificationMode}
	 * 
	 * @param fileName Xml file to serialize the object <var>integrationConfigurationResourceIdentificationMode</var>
	 * @param integrationConfigurationResourceIdentificationMode Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,IntegrationConfigurationResourceIdentificationMode integrationConfigurationResourceIdentificationMode) throws SerializerException {
		this.objToXml(fileName, IntegrationConfigurationResourceIdentificationMode.class, integrationConfigurationResourceIdentificationMode, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>integrationConfigurationResourceIdentificationMode</var> of type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationResourceIdentificationMode}
	 * 
	 * @param fileName Xml file to serialize the object <var>integrationConfigurationResourceIdentificationMode</var>
	 * @param integrationConfigurationResourceIdentificationMode Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,IntegrationConfigurationResourceIdentificationMode integrationConfigurationResourceIdentificationMode,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, IntegrationConfigurationResourceIdentificationMode.class, integrationConfigurationResourceIdentificationMode, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>integrationConfigurationResourceIdentificationMode</var> of type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationResourceIdentificationMode}
	 * 
	 * @param file Xml file to serialize the object <var>integrationConfigurationResourceIdentificationMode</var>
	 * @param integrationConfigurationResourceIdentificationMode Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,IntegrationConfigurationResourceIdentificationMode integrationConfigurationResourceIdentificationMode) throws SerializerException {
		this.objToXml(file, IntegrationConfigurationResourceIdentificationMode.class, integrationConfigurationResourceIdentificationMode, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>integrationConfigurationResourceIdentificationMode</var> of type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationResourceIdentificationMode}
	 * 
	 * @param file Xml file to serialize the object <var>integrationConfigurationResourceIdentificationMode</var>
	 * @param integrationConfigurationResourceIdentificationMode Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,IntegrationConfigurationResourceIdentificationMode integrationConfigurationResourceIdentificationMode,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, IntegrationConfigurationResourceIdentificationMode.class, integrationConfigurationResourceIdentificationMode, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>integrationConfigurationResourceIdentificationMode</var> of type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationResourceIdentificationMode}
	 * 
	 * @param out OutputStream to serialize the object <var>integrationConfigurationResourceIdentificationMode</var>
	 * @param integrationConfigurationResourceIdentificationMode Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,IntegrationConfigurationResourceIdentificationMode integrationConfigurationResourceIdentificationMode) throws SerializerException {
		this.objToXml(out, IntegrationConfigurationResourceIdentificationMode.class, integrationConfigurationResourceIdentificationMode, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>integrationConfigurationResourceIdentificationMode</var> of type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationResourceIdentificationMode}
	 * 
	 * @param out OutputStream to serialize the object <var>integrationConfigurationResourceIdentificationMode</var>
	 * @param integrationConfigurationResourceIdentificationMode Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,IntegrationConfigurationResourceIdentificationMode integrationConfigurationResourceIdentificationMode,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, IntegrationConfigurationResourceIdentificationMode.class, integrationConfigurationResourceIdentificationMode, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>integrationConfigurationResourceIdentificationMode</var> of type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationResourceIdentificationMode}
	 * 
	 * @param integrationConfigurationResourceIdentificationMode Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(IntegrationConfigurationResourceIdentificationMode integrationConfigurationResourceIdentificationMode) throws SerializerException {
		return this.objToXml(IntegrationConfigurationResourceIdentificationMode.class, integrationConfigurationResourceIdentificationMode, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>integrationConfigurationResourceIdentificationMode</var> of type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationResourceIdentificationMode}
	 * 
	 * @param integrationConfigurationResourceIdentificationMode Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(IntegrationConfigurationResourceIdentificationMode integrationConfigurationResourceIdentificationMode,boolean prettyPrint) throws SerializerException {
		return this.objToXml(IntegrationConfigurationResourceIdentificationMode.class, integrationConfigurationResourceIdentificationMode, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>integrationConfigurationResourceIdentificationMode</var> of type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationResourceIdentificationMode}
	 * 
	 * @param integrationConfigurationResourceIdentificationMode Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(IntegrationConfigurationResourceIdentificationMode integrationConfigurationResourceIdentificationMode) throws SerializerException {
		return this.objToXml(IntegrationConfigurationResourceIdentificationMode.class, integrationConfigurationResourceIdentificationMode, false).toString();
	}
	/**
	 * Serialize to String the object <var>integrationConfigurationResourceIdentificationMode</var> of type {@link org.openspcoop2.protocol.manifest.IntegrationConfigurationResourceIdentificationMode}
	 * 
	 * @param integrationConfigurationResourceIdentificationMode Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(IntegrationConfigurationResourceIdentificationMode integrationConfigurationResourceIdentificationMode,boolean prettyPrint) throws SerializerException {
		return this.objToXml(IntegrationConfigurationResourceIdentificationMode.class, integrationConfigurationResourceIdentificationMode, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: RFC7807
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>rfC7807</var> of type {@link org.openspcoop2.protocol.manifest.RFC7807}
	 * 
	 * @param fileName Xml file to serialize the object <var>rfC7807</var>
	 * @param rfC7807 Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,RFC7807 rfC7807) throws SerializerException {
		this.objToXml(fileName, RFC7807.class, rfC7807, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>rfC7807</var> of type {@link org.openspcoop2.protocol.manifest.RFC7807}
	 * 
	 * @param fileName Xml file to serialize the object <var>rfC7807</var>
	 * @param rfC7807 Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,RFC7807 rfC7807,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, RFC7807.class, rfC7807, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>rfC7807</var> of type {@link org.openspcoop2.protocol.manifest.RFC7807}
	 * 
	 * @param file Xml file to serialize the object <var>rfC7807</var>
	 * @param rfC7807 Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,RFC7807 rfC7807) throws SerializerException {
		this.objToXml(file, RFC7807.class, rfC7807, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>rfC7807</var> of type {@link org.openspcoop2.protocol.manifest.RFC7807}
	 * 
	 * @param file Xml file to serialize the object <var>rfC7807</var>
	 * @param rfC7807 Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,RFC7807 rfC7807,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, RFC7807.class, rfC7807, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>rfC7807</var> of type {@link org.openspcoop2.protocol.manifest.RFC7807}
	 * 
	 * @param out OutputStream to serialize the object <var>rfC7807</var>
	 * @param rfC7807 Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,RFC7807 rfC7807) throws SerializerException {
		this.objToXml(out, RFC7807.class, rfC7807, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>rfC7807</var> of type {@link org.openspcoop2.protocol.manifest.RFC7807}
	 * 
	 * @param out OutputStream to serialize the object <var>rfC7807</var>
	 * @param rfC7807 Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,RFC7807 rfC7807,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, RFC7807.class, rfC7807, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>rfC7807</var> of type {@link org.openspcoop2.protocol.manifest.RFC7807}
	 * 
	 * @param rfC7807 Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(RFC7807 rfC7807) throws SerializerException {
		return this.objToXml(RFC7807.class, rfC7807, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>rfC7807</var> of type {@link org.openspcoop2.protocol.manifest.RFC7807}
	 * 
	 * @param rfC7807 Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(RFC7807 rfC7807,boolean prettyPrint) throws SerializerException {
		return this.objToXml(RFC7807.class, rfC7807, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>rfC7807</var> of type {@link org.openspcoop2.protocol.manifest.RFC7807}
	 * 
	 * @param rfC7807 Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(RFC7807 rfC7807) throws SerializerException {
		return this.objToXml(RFC7807.class, rfC7807, false).toString();
	}
	/**
	 * Serialize to String the object <var>rfC7807</var> of type {@link org.openspcoop2.protocol.manifest.RFC7807}
	 * 
	 * @param rfC7807 Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(RFC7807 rfC7807,boolean prettyPrint) throws SerializerException {
		return this.objToXml(RFC7807.class, rfC7807, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: RestMediaTypeDefaultMapping
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>restMediaTypeDefaultMapping</var> of type {@link org.openspcoop2.protocol.manifest.RestMediaTypeDefaultMapping}
	 * 
	 * @param fileName Xml file to serialize the object <var>restMediaTypeDefaultMapping</var>
	 * @param restMediaTypeDefaultMapping Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,RestMediaTypeDefaultMapping restMediaTypeDefaultMapping) throws SerializerException {
		this.objToXml(fileName, RestMediaTypeDefaultMapping.class, restMediaTypeDefaultMapping, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>restMediaTypeDefaultMapping</var> of type {@link org.openspcoop2.protocol.manifest.RestMediaTypeDefaultMapping}
	 * 
	 * @param fileName Xml file to serialize the object <var>restMediaTypeDefaultMapping</var>
	 * @param restMediaTypeDefaultMapping Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,RestMediaTypeDefaultMapping restMediaTypeDefaultMapping,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, RestMediaTypeDefaultMapping.class, restMediaTypeDefaultMapping, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>restMediaTypeDefaultMapping</var> of type {@link org.openspcoop2.protocol.manifest.RestMediaTypeDefaultMapping}
	 * 
	 * @param file Xml file to serialize the object <var>restMediaTypeDefaultMapping</var>
	 * @param restMediaTypeDefaultMapping Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,RestMediaTypeDefaultMapping restMediaTypeDefaultMapping) throws SerializerException {
		this.objToXml(file, RestMediaTypeDefaultMapping.class, restMediaTypeDefaultMapping, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>restMediaTypeDefaultMapping</var> of type {@link org.openspcoop2.protocol.manifest.RestMediaTypeDefaultMapping}
	 * 
	 * @param file Xml file to serialize the object <var>restMediaTypeDefaultMapping</var>
	 * @param restMediaTypeDefaultMapping Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,RestMediaTypeDefaultMapping restMediaTypeDefaultMapping,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, RestMediaTypeDefaultMapping.class, restMediaTypeDefaultMapping, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>restMediaTypeDefaultMapping</var> of type {@link org.openspcoop2.protocol.manifest.RestMediaTypeDefaultMapping}
	 * 
	 * @param out OutputStream to serialize the object <var>restMediaTypeDefaultMapping</var>
	 * @param restMediaTypeDefaultMapping Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,RestMediaTypeDefaultMapping restMediaTypeDefaultMapping) throws SerializerException {
		this.objToXml(out, RestMediaTypeDefaultMapping.class, restMediaTypeDefaultMapping, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>restMediaTypeDefaultMapping</var> of type {@link org.openspcoop2.protocol.manifest.RestMediaTypeDefaultMapping}
	 * 
	 * @param out OutputStream to serialize the object <var>restMediaTypeDefaultMapping</var>
	 * @param restMediaTypeDefaultMapping Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,RestMediaTypeDefaultMapping restMediaTypeDefaultMapping,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, RestMediaTypeDefaultMapping.class, restMediaTypeDefaultMapping, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>restMediaTypeDefaultMapping</var> of type {@link org.openspcoop2.protocol.manifest.RestMediaTypeDefaultMapping}
	 * 
	 * @param restMediaTypeDefaultMapping Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(RestMediaTypeDefaultMapping restMediaTypeDefaultMapping) throws SerializerException {
		return this.objToXml(RestMediaTypeDefaultMapping.class, restMediaTypeDefaultMapping, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>restMediaTypeDefaultMapping</var> of type {@link org.openspcoop2.protocol.manifest.RestMediaTypeDefaultMapping}
	 * 
	 * @param restMediaTypeDefaultMapping Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(RestMediaTypeDefaultMapping restMediaTypeDefaultMapping,boolean prettyPrint) throws SerializerException {
		return this.objToXml(RestMediaTypeDefaultMapping.class, restMediaTypeDefaultMapping, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>restMediaTypeDefaultMapping</var> of type {@link org.openspcoop2.protocol.manifest.RestMediaTypeDefaultMapping}
	 * 
	 * @param restMediaTypeDefaultMapping Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(RestMediaTypeDefaultMapping restMediaTypeDefaultMapping) throws SerializerException {
		return this.objToXml(RestMediaTypeDefaultMapping.class, restMediaTypeDefaultMapping, false).toString();
	}
	/**
	 * Serialize to String the object <var>restMediaTypeDefaultMapping</var> of type {@link org.openspcoop2.protocol.manifest.RestMediaTypeDefaultMapping}
	 * 
	 * @param restMediaTypeDefaultMapping Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(RestMediaTypeDefaultMapping restMediaTypeDefaultMapping,boolean prettyPrint) throws SerializerException {
		return this.objToXml(RestMediaTypeDefaultMapping.class, restMediaTypeDefaultMapping, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: RestMediaTypeUndefinedMapping
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>restMediaTypeUndefinedMapping</var> of type {@link org.openspcoop2.protocol.manifest.RestMediaTypeUndefinedMapping}
	 * 
	 * @param fileName Xml file to serialize the object <var>restMediaTypeUndefinedMapping</var>
	 * @param restMediaTypeUndefinedMapping Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,RestMediaTypeUndefinedMapping restMediaTypeUndefinedMapping) throws SerializerException {
		this.objToXml(fileName, RestMediaTypeUndefinedMapping.class, restMediaTypeUndefinedMapping, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>restMediaTypeUndefinedMapping</var> of type {@link org.openspcoop2.protocol.manifest.RestMediaTypeUndefinedMapping}
	 * 
	 * @param fileName Xml file to serialize the object <var>restMediaTypeUndefinedMapping</var>
	 * @param restMediaTypeUndefinedMapping Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,RestMediaTypeUndefinedMapping restMediaTypeUndefinedMapping,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, RestMediaTypeUndefinedMapping.class, restMediaTypeUndefinedMapping, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>restMediaTypeUndefinedMapping</var> of type {@link org.openspcoop2.protocol.manifest.RestMediaTypeUndefinedMapping}
	 * 
	 * @param file Xml file to serialize the object <var>restMediaTypeUndefinedMapping</var>
	 * @param restMediaTypeUndefinedMapping Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,RestMediaTypeUndefinedMapping restMediaTypeUndefinedMapping) throws SerializerException {
		this.objToXml(file, RestMediaTypeUndefinedMapping.class, restMediaTypeUndefinedMapping, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>restMediaTypeUndefinedMapping</var> of type {@link org.openspcoop2.protocol.manifest.RestMediaTypeUndefinedMapping}
	 * 
	 * @param file Xml file to serialize the object <var>restMediaTypeUndefinedMapping</var>
	 * @param restMediaTypeUndefinedMapping Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,RestMediaTypeUndefinedMapping restMediaTypeUndefinedMapping,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, RestMediaTypeUndefinedMapping.class, restMediaTypeUndefinedMapping, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>restMediaTypeUndefinedMapping</var> of type {@link org.openspcoop2.protocol.manifest.RestMediaTypeUndefinedMapping}
	 * 
	 * @param out OutputStream to serialize the object <var>restMediaTypeUndefinedMapping</var>
	 * @param restMediaTypeUndefinedMapping Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,RestMediaTypeUndefinedMapping restMediaTypeUndefinedMapping) throws SerializerException {
		this.objToXml(out, RestMediaTypeUndefinedMapping.class, restMediaTypeUndefinedMapping, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>restMediaTypeUndefinedMapping</var> of type {@link org.openspcoop2.protocol.manifest.RestMediaTypeUndefinedMapping}
	 * 
	 * @param out OutputStream to serialize the object <var>restMediaTypeUndefinedMapping</var>
	 * @param restMediaTypeUndefinedMapping Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,RestMediaTypeUndefinedMapping restMediaTypeUndefinedMapping,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, RestMediaTypeUndefinedMapping.class, restMediaTypeUndefinedMapping, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>restMediaTypeUndefinedMapping</var> of type {@link org.openspcoop2.protocol.manifest.RestMediaTypeUndefinedMapping}
	 * 
	 * @param restMediaTypeUndefinedMapping Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(RestMediaTypeUndefinedMapping restMediaTypeUndefinedMapping) throws SerializerException {
		return this.objToXml(RestMediaTypeUndefinedMapping.class, restMediaTypeUndefinedMapping, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>restMediaTypeUndefinedMapping</var> of type {@link org.openspcoop2.protocol.manifest.RestMediaTypeUndefinedMapping}
	 * 
	 * @param restMediaTypeUndefinedMapping Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(RestMediaTypeUndefinedMapping restMediaTypeUndefinedMapping,boolean prettyPrint) throws SerializerException {
		return this.objToXml(RestMediaTypeUndefinedMapping.class, restMediaTypeUndefinedMapping, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>restMediaTypeUndefinedMapping</var> of type {@link org.openspcoop2.protocol.manifest.RestMediaTypeUndefinedMapping}
	 * 
	 * @param restMediaTypeUndefinedMapping Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(RestMediaTypeUndefinedMapping restMediaTypeUndefinedMapping) throws SerializerException {
		return this.objToXml(RestMediaTypeUndefinedMapping.class, restMediaTypeUndefinedMapping, false).toString();
	}
	/**
	 * Serialize to String the object <var>restMediaTypeUndefinedMapping</var> of type {@link org.openspcoop2.protocol.manifest.RestMediaTypeUndefinedMapping}
	 * 
	 * @param restMediaTypeUndefinedMapping Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(RestMediaTypeUndefinedMapping restMediaTypeUndefinedMapping,boolean prettyPrint) throws SerializerException {
		return this.objToXml(RestMediaTypeUndefinedMapping.class, restMediaTypeUndefinedMapping, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: OrganizationType
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>organizationType</var> of type {@link org.openspcoop2.protocol.manifest.OrganizationType}
	 * 
	 * @param fileName Xml file to serialize the object <var>organizationType</var>
	 * @param organizationType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,OrganizationType organizationType) throws SerializerException {
		this.objToXml(fileName, OrganizationType.class, organizationType, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>organizationType</var> of type {@link org.openspcoop2.protocol.manifest.OrganizationType}
	 * 
	 * @param fileName Xml file to serialize the object <var>organizationType</var>
	 * @param organizationType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,OrganizationType organizationType,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, OrganizationType.class, organizationType, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>organizationType</var> of type {@link org.openspcoop2.protocol.manifest.OrganizationType}
	 * 
	 * @param file Xml file to serialize the object <var>organizationType</var>
	 * @param organizationType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,OrganizationType organizationType) throws SerializerException {
		this.objToXml(file, OrganizationType.class, organizationType, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>organizationType</var> of type {@link org.openspcoop2.protocol.manifest.OrganizationType}
	 * 
	 * @param file Xml file to serialize the object <var>organizationType</var>
	 * @param organizationType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,OrganizationType organizationType,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, OrganizationType.class, organizationType, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>organizationType</var> of type {@link org.openspcoop2.protocol.manifest.OrganizationType}
	 * 
	 * @param out OutputStream to serialize the object <var>organizationType</var>
	 * @param organizationType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,OrganizationType organizationType) throws SerializerException {
		this.objToXml(out, OrganizationType.class, organizationType, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>organizationType</var> of type {@link org.openspcoop2.protocol.manifest.OrganizationType}
	 * 
	 * @param out OutputStream to serialize the object <var>organizationType</var>
	 * @param organizationType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,OrganizationType organizationType,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, OrganizationType.class, organizationType, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>organizationType</var> of type {@link org.openspcoop2.protocol.manifest.OrganizationType}
	 * 
	 * @param organizationType Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(OrganizationType organizationType) throws SerializerException {
		return this.objToXml(OrganizationType.class, organizationType, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>organizationType</var> of type {@link org.openspcoop2.protocol.manifest.OrganizationType}
	 * 
	 * @param organizationType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(OrganizationType organizationType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(OrganizationType.class, organizationType, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>organizationType</var> of type {@link org.openspcoop2.protocol.manifest.OrganizationType}
	 * 
	 * @param organizationType Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(OrganizationType organizationType) throws SerializerException {
		return this.objToXml(OrganizationType.class, organizationType, false).toString();
	}
	/**
	 * Serialize to String the object <var>organizationType</var> of type {@link org.openspcoop2.protocol.manifest.OrganizationType}
	 * 
	 * @param organizationType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(OrganizationType organizationType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(OrganizationType.class, organizationType, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: Context
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>context</var> of type {@link org.openspcoop2.protocol.manifest.Context}
	 * 
	 * @param fileName Xml file to serialize the object <var>context</var>
	 * @param context Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Context context) throws SerializerException {
		this.objToXml(fileName, Context.class, context, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>context</var> of type {@link org.openspcoop2.protocol.manifest.Context}
	 * 
	 * @param fileName Xml file to serialize the object <var>context</var>
	 * @param context Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Context context,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Context.class, context, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>context</var> of type {@link org.openspcoop2.protocol.manifest.Context}
	 * 
	 * @param file Xml file to serialize the object <var>context</var>
	 * @param context Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Context context) throws SerializerException {
		this.objToXml(file, Context.class, context, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>context</var> of type {@link org.openspcoop2.protocol.manifest.Context}
	 * 
	 * @param file Xml file to serialize the object <var>context</var>
	 * @param context Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Context context,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Context.class, context, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>context</var> of type {@link org.openspcoop2.protocol.manifest.Context}
	 * 
	 * @param out OutputStream to serialize the object <var>context</var>
	 * @param context Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Context context) throws SerializerException {
		this.objToXml(out, Context.class, context, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>context</var> of type {@link org.openspcoop2.protocol.manifest.Context}
	 * 
	 * @param out OutputStream to serialize the object <var>context</var>
	 * @param context Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Context context,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Context.class, context, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>context</var> of type {@link org.openspcoop2.protocol.manifest.Context}
	 * 
	 * @param context Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Context context) throws SerializerException {
		return this.objToXml(Context.class, context, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>context</var> of type {@link org.openspcoop2.protocol.manifest.Context}
	 * 
	 * @param context Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Context context,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Context.class, context, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>context</var> of type {@link org.openspcoop2.protocol.manifest.Context}
	 * 
	 * @param context Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Context context) throws SerializerException {
		return this.objToXml(Context.class, context, false).toString();
	}
	/**
	 * Serialize to String the object <var>context</var> of type {@link org.openspcoop2.protocol.manifest.Context}
	 * 
	 * @param context Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Context context,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Context.class, context, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: DefaultIntegrationError
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>defaultIntegrationError</var> of type {@link org.openspcoop2.protocol.manifest.DefaultIntegrationError}
	 * 
	 * @param fileName Xml file to serialize the object <var>defaultIntegrationError</var>
	 * @param defaultIntegrationError Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,DefaultIntegrationError defaultIntegrationError) throws SerializerException {
		this.objToXml(fileName, DefaultIntegrationError.class, defaultIntegrationError, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>defaultIntegrationError</var> of type {@link org.openspcoop2.protocol.manifest.DefaultIntegrationError}
	 * 
	 * @param fileName Xml file to serialize the object <var>defaultIntegrationError</var>
	 * @param defaultIntegrationError Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,DefaultIntegrationError defaultIntegrationError,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, DefaultIntegrationError.class, defaultIntegrationError, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>defaultIntegrationError</var> of type {@link org.openspcoop2.protocol.manifest.DefaultIntegrationError}
	 * 
	 * @param file Xml file to serialize the object <var>defaultIntegrationError</var>
	 * @param defaultIntegrationError Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,DefaultIntegrationError defaultIntegrationError) throws SerializerException {
		this.objToXml(file, DefaultIntegrationError.class, defaultIntegrationError, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>defaultIntegrationError</var> of type {@link org.openspcoop2.protocol.manifest.DefaultIntegrationError}
	 * 
	 * @param file Xml file to serialize the object <var>defaultIntegrationError</var>
	 * @param defaultIntegrationError Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,DefaultIntegrationError defaultIntegrationError,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, DefaultIntegrationError.class, defaultIntegrationError, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>defaultIntegrationError</var> of type {@link org.openspcoop2.protocol.manifest.DefaultIntegrationError}
	 * 
	 * @param out OutputStream to serialize the object <var>defaultIntegrationError</var>
	 * @param defaultIntegrationError Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,DefaultIntegrationError defaultIntegrationError) throws SerializerException {
		this.objToXml(out, DefaultIntegrationError.class, defaultIntegrationError, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>defaultIntegrationError</var> of type {@link org.openspcoop2.protocol.manifest.DefaultIntegrationError}
	 * 
	 * @param out OutputStream to serialize the object <var>defaultIntegrationError</var>
	 * @param defaultIntegrationError Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,DefaultIntegrationError defaultIntegrationError,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, DefaultIntegrationError.class, defaultIntegrationError, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>defaultIntegrationError</var> of type {@link org.openspcoop2.protocol.manifest.DefaultIntegrationError}
	 * 
	 * @param defaultIntegrationError Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(DefaultIntegrationError defaultIntegrationError) throws SerializerException {
		return this.objToXml(DefaultIntegrationError.class, defaultIntegrationError, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>defaultIntegrationError</var> of type {@link org.openspcoop2.protocol.manifest.DefaultIntegrationError}
	 * 
	 * @param defaultIntegrationError Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(DefaultIntegrationError defaultIntegrationError,boolean prettyPrint) throws SerializerException {
		return this.objToXml(DefaultIntegrationError.class, defaultIntegrationError, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>defaultIntegrationError</var> of type {@link org.openspcoop2.protocol.manifest.DefaultIntegrationError}
	 * 
	 * @param defaultIntegrationError Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(DefaultIntegrationError defaultIntegrationError) throws SerializerException {
		return this.objToXml(DefaultIntegrationError.class, defaultIntegrationError, false).toString();
	}
	/**
	 * Serialize to String the object <var>defaultIntegrationError</var> of type {@link org.openspcoop2.protocol.manifest.DefaultIntegrationError}
	 * 
	 * @param defaultIntegrationError Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(DefaultIntegrationError defaultIntegrationError,boolean prettyPrint) throws SerializerException {
		return this.objToXml(DefaultIntegrationError.class, defaultIntegrationError, prettyPrint).toString();
	}
	
	
	

}
