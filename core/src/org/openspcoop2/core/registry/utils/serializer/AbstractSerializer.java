/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it).
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
package org.openspcoop2.core.registry.utils.serializer;

import org.openspcoop2.generic_project.exception.SerializerException;
import org.openspcoop2.utils.beans.WriteToSerializerType;
import org.openspcoop2.utils.xml.JaxbUtils;

import org.openspcoop2.core.registry.Message;
import org.openspcoop2.core.registry.Operation;
import org.openspcoop2.core.registry.ProtocolProperty;
import org.openspcoop2.core.registry.ResourceParameter;
import org.openspcoop2.core.registry.ResourceRequest;
import org.openspcoop2.core.registry.ResourceRepresentation;
import org.openspcoop2.core.registry.Resource;
import org.openspcoop2.core.registry.ResourceResponse;
import org.openspcoop2.core.registry.IdRuolo;
import org.openspcoop2.core.registry.ResourceRepresentationJson;
import org.openspcoop2.core.registry.MessagePart;
import org.openspcoop2.core.registry.IdPortaDominio;
import org.openspcoop2.core.registry.Documento;
import org.openspcoop2.core.registry.IdSoggetto;
import org.openspcoop2.core.registry.IdAccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.GruppoAccordo;
import org.openspcoop2.core.registry.GruppiAccordo;
import org.openspcoop2.core.registry.IdScope;
import org.openspcoop2.core.registry.ConfigurazioneServizio;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.core.registry.Property;
import org.openspcoop2.core.registry.Ruolo;
import org.openspcoop2.core.registry.AccordoCooperazionePartecipanti;
import org.openspcoop2.core.registry.Proprieta;
import org.openspcoop2.core.registry.Azione;
import org.openspcoop2.core.registry.Connettore;
import org.openspcoop2.core.registry.ConfigurazioneServizioAzione;
import org.openspcoop2.core.registry.RuoloSoggetto;
import org.openspcoop2.core.registry.Scope;
import org.openspcoop2.core.registry.ResourceRepresentationXml;
import org.openspcoop2.core.registry.AccordoServizioParteComuneServizioCompostoServizioComponente;
import org.openspcoop2.core.registry.AccordoServizioParteComuneServizioComposto;
import org.openspcoop2.core.registry.PortType;
import org.openspcoop2.core.registry.IdAccordoCooperazione;
import org.openspcoop2.core.registry.AccordoCooperazione;
import org.openspcoop2.core.registry.CredenzialiSoggetto;
import org.openspcoop2.core.registry.RegistroServizi;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.PortaDominio;
import org.openspcoop2.core.registry.Gruppo;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.IdGruppo;
import org.openspcoop2.core.registry.RuoliSoggetto;
import org.openspcoop2.core.registry.IdAccordoServizioParteComune;

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
			}catch(Exception e){
				// ignore
			}
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
			}catch(Exception e){
				// ignore
			}
			try{
				fout.close();
			}catch(Exception e){
				// ignore
			}
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
			}catch(Exception e){
				// ignore
			}
			try{
				bout.close();
			}catch(Exception e){
				// ignore
			}
		}
		return bout;
	}




	/*
	 =================================================================================
	 Object: message
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>message</var> of type {@link org.openspcoop2.core.registry.Message}
	 * 
	 * @param fileName Xml file to serialize the object <var>message</var>
	 * @param message Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Message message) throws SerializerException {
		this.objToXml(fileName, Message.class, message, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>message</var> of type {@link org.openspcoop2.core.registry.Message}
	 * 
	 * @param fileName Xml file to serialize the object <var>message</var>
	 * @param message Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Message message,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Message.class, message, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>message</var> of type {@link org.openspcoop2.core.registry.Message}
	 * 
	 * @param file Xml file to serialize the object <var>message</var>
	 * @param message Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Message message) throws SerializerException {
		this.objToXml(file, Message.class, message, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>message</var> of type {@link org.openspcoop2.core.registry.Message}
	 * 
	 * @param file Xml file to serialize the object <var>message</var>
	 * @param message Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Message message,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Message.class, message, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>message</var> of type {@link org.openspcoop2.core.registry.Message}
	 * 
	 * @param out OutputStream to serialize the object <var>message</var>
	 * @param message Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Message message) throws SerializerException {
		this.objToXml(out, Message.class, message, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>message</var> of type {@link org.openspcoop2.core.registry.Message}
	 * 
	 * @param out OutputStream to serialize the object <var>message</var>
	 * @param message Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Message message,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Message.class, message, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>message</var> of type {@link org.openspcoop2.core.registry.Message}
	 * 
	 * @param message Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Message message) throws SerializerException {
		return this.objToXml(Message.class, message, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>message</var> of type {@link org.openspcoop2.core.registry.Message}
	 * 
	 * @param message Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Message message,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Message.class, message, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>message</var> of type {@link org.openspcoop2.core.registry.Message}
	 * 
	 * @param message Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Message message) throws SerializerException {
		return this.objToXml(Message.class, message, false).toString();
	}
	/**
	 * Serialize to String the object <var>message</var> of type {@link org.openspcoop2.core.registry.Message}
	 * 
	 * @param message Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Message message,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Message.class, message, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: operation
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>operation</var> of type {@link org.openspcoop2.core.registry.Operation}
	 * 
	 * @param fileName Xml file to serialize the object <var>operation</var>
	 * @param operation Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Operation operation) throws SerializerException {
		this.objToXml(fileName, Operation.class, operation, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>operation</var> of type {@link org.openspcoop2.core.registry.Operation}
	 * 
	 * @param fileName Xml file to serialize the object <var>operation</var>
	 * @param operation Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Operation operation,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Operation.class, operation, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>operation</var> of type {@link org.openspcoop2.core.registry.Operation}
	 * 
	 * @param file Xml file to serialize the object <var>operation</var>
	 * @param operation Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Operation operation) throws SerializerException {
		this.objToXml(file, Operation.class, operation, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>operation</var> of type {@link org.openspcoop2.core.registry.Operation}
	 * 
	 * @param file Xml file to serialize the object <var>operation</var>
	 * @param operation Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Operation operation,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Operation.class, operation, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>operation</var> of type {@link org.openspcoop2.core.registry.Operation}
	 * 
	 * @param out OutputStream to serialize the object <var>operation</var>
	 * @param operation Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Operation operation) throws SerializerException {
		this.objToXml(out, Operation.class, operation, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>operation</var> of type {@link org.openspcoop2.core.registry.Operation}
	 * 
	 * @param out OutputStream to serialize the object <var>operation</var>
	 * @param operation Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Operation operation,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Operation.class, operation, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>operation</var> of type {@link org.openspcoop2.core.registry.Operation}
	 * 
	 * @param operation Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Operation operation) throws SerializerException {
		return this.objToXml(Operation.class, operation, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>operation</var> of type {@link org.openspcoop2.core.registry.Operation}
	 * 
	 * @param operation Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Operation operation,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Operation.class, operation, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>operation</var> of type {@link org.openspcoop2.core.registry.Operation}
	 * 
	 * @param operation Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Operation operation) throws SerializerException {
		return this.objToXml(Operation.class, operation, false).toString();
	}
	/**
	 * Serialize to String the object <var>operation</var> of type {@link org.openspcoop2.core.registry.Operation}
	 * 
	 * @param operation Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Operation operation,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Operation.class, operation, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: protocol-property
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>protocolProperty</var> of type {@link org.openspcoop2.core.registry.ProtocolProperty}
	 * 
	 * @param fileName Xml file to serialize the object <var>protocolProperty</var>
	 * @param protocolProperty Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ProtocolProperty protocolProperty) throws SerializerException {
		this.objToXml(fileName, ProtocolProperty.class, protocolProperty, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>protocolProperty</var> of type {@link org.openspcoop2.core.registry.ProtocolProperty}
	 * 
	 * @param fileName Xml file to serialize the object <var>protocolProperty</var>
	 * @param protocolProperty Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ProtocolProperty protocolProperty,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, ProtocolProperty.class, protocolProperty, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>protocolProperty</var> of type {@link org.openspcoop2.core.registry.ProtocolProperty}
	 * 
	 * @param file Xml file to serialize the object <var>protocolProperty</var>
	 * @param protocolProperty Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ProtocolProperty protocolProperty) throws SerializerException {
		this.objToXml(file, ProtocolProperty.class, protocolProperty, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>protocolProperty</var> of type {@link org.openspcoop2.core.registry.ProtocolProperty}
	 * 
	 * @param file Xml file to serialize the object <var>protocolProperty</var>
	 * @param protocolProperty Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ProtocolProperty protocolProperty,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, ProtocolProperty.class, protocolProperty, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>protocolProperty</var> of type {@link org.openspcoop2.core.registry.ProtocolProperty}
	 * 
	 * @param out OutputStream to serialize the object <var>protocolProperty</var>
	 * @param protocolProperty Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ProtocolProperty protocolProperty) throws SerializerException {
		this.objToXml(out, ProtocolProperty.class, protocolProperty, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>protocolProperty</var> of type {@link org.openspcoop2.core.registry.ProtocolProperty}
	 * 
	 * @param out OutputStream to serialize the object <var>protocolProperty</var>
	 * @param protocolProperty Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ProtocolProperty protocolProperty,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, ProtocolProperty.class, protocolProperty, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>protocolProperty</var> of type {@link org.openspcoop2.core.registry.ProtocolProperty}
	 * 
	 * @param protocolProperty Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ProtocolProperty protocolProperty) throws SerializerException {
		return this.objToXml(ProtocolProperty.class, protocolProperty, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>protocolProperty</var> of type {@link org.openspcoop2.core.registry.ProtocolProperty}
	 * 
	 * @param protocolProperty Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ProtocolProperty protocolProperty,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ProtocolProperty.class, protocolProperty, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>protocolProperty</var> of type {@link org.openspcoop2.core.registry.ProtocolProperty}
	 * 
	 * @param protocolProperty Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ProtocolProperty protocolProperty) throws SerializerException {
		return this.objToXml(ProtocolProperty.class, protocolProperty, false).toString();
	}
	/**
	 * Serialize to String the object <var>protocolProperty</var> of type {@link org.openspcoop2.core.registry.ProtocolProperty}
	 * 
	 * @param protocolProperty Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ProtocolProperty protocolProperty,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ProtocolProperty.class, protocolProperty, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: resource-parameter
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>resourceParameter</var> of type {@link org.openspcoop2.core.registry.ResourceParameter}
	 * 
	 * @param fileName Xml file to serialize the object <var>resourceParameter</var>
	 * @param resourceParameter Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ResourceParameter resourceParameter) throws SerializerException {
		this.objToXml(fileName, ResourceParameter.class, resourceParameter, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>resourceParameter</var> of type {@link org.openspcoop2.core.registry.ResourceParameter}
	 * 
	 * @param fileName Xml file to serialize the object <var>resourceParameter</var>
	 * @param resourceParameter Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ResourceParameter resourceParameter,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, ResourceParameter.class, resourceParameter, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>resourceParameter</var> of type {@link org.openspcoop2.core.registry.ResourceParameter}
	 * 
	 * @param file Xml file to serialize the object <var>resourceParameter</var>
	 * @param resourceParameter Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ResourceParameter resourceParameter) throws SerializerException {
		this.objToXml(file, ResourceParameter.class, resourceParameter, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>resourceParameter</var> of type {@link org.openspcoop2.core.registry.ResourceParameter}
	 * 
	 * @param file Xml file to serialize the object <var>resourceParameter</var>
	 * @param resourceParameter Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ResourceParameter resourceParameter,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, ResourceParameter.class, resourceParameter, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>resourceParameter</var> of type {@link org.openspcoop2.core.registry.ResourceParameter}
	 * 
	 * @param out OutputStream to serialize the object <var>resourceParameter</var>
	 * @param resourceParameter Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ResourceParameter resourceParameter) throws SerializerException {
		this.objToXml(out, ResourceParameter.class, resourceParameter, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>resourceParameter</var> of type {@link org.openspcoop2.core.registry.ResourceParameter}
	 * 
	 * @param out OutputStream to serialize the object <var>resourceParameter</var>
	 * @param resourceParameter Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ResourceParameter resourceParameter,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, ResourceParameter.class, resourceParameter, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>resourceParameter</var> of type {@link org.openspcoop2.core.registry.ResourceParameter}
	 * 
	 * @param resourceParameter Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ResourceParameter resourceParameter) throws SerializerException {
		return this.objToXml(ResourceParameter.class, resourceParameter, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>resourceParameter</var> of type {@link org.openspcoop2.core.registry.ResourceParameter}
	 * 
	 * @param resourceParameter Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ResourceParameter resourceParameter,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ResourceParameter.class, resourceParameter, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>resourceParameter</var> of type {@link org.openspcoop2.core.registry.ResourceParameter}
	 * 
	 * @param resourceParameter Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ResourceParameter resourceParameter) throws SerializerException {
		return this.objToXml(ResourceParameter.class, resourceParameter, false).toString();
	}
	/**
	 * Serialize to String the object <var>resourceParameter</var> of type {@link org.openspcoop2.core.registry.ResourceParameter}
	 * 
	 * @param resourceParameter Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ResourceParameter resourceParameter,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ResourceParameter.class, resourceParameter, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: resource-request
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>resourceRequest</var> of type {@link org.openspcoop2.core.registry.ResourceRequest}
	 * 
	 * @param fileName Xml file to serialize the object <var>resourceRequest</var>
	 * @param resourceRequest Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ResourceRequest resourceRequest) throws SerializerException {
		this.objToXml(fileName, ResourceRequest.class, resourceRequest, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>resourceRequest</var> of type {@link org.openspcoop2.core.registry.ResourceRequest}
	 * 
	 * @param fileName Xml file to serialize the object <var>resourceRequest</var>
	 * @param resourceRequest Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ResourceRequest resourceRequest,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, ResourceRequest.class, resourceRequest, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>resourceRequest</var> of type {@link org.openspcoop2.core.registry.ResourceRequest}
	 * 
	 * @param file Xml file to serialize the object <var>resourceRequest</var>
	 * @param resourceRequest Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ResourceRequest resourceRequest) throws SerializerException {
		this.objToXml(file, ResourceRequest.class, resourceRequest, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>resourceRequest</var> of type {@link org.openspcoop2.core.registry.ResourceRequest}
	 * 
	 * @param file Xml file to serialize the object <var>resourceRequest</var>
	 * @param resourceRequest Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ResourceRequest resourceRequest,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, ResourceRequest.class, resourceRequest, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>resourceRequest</var> of type {@link org.openspcoop2.core.registry.ResourceRequest}
	 * 
	 * @param out OutputStream to serialize the object <var>resourceRequest</var>
	 * @param resourceRequest Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ResourceRequest resourceRequest) throws SerializerException {
		this.objToXml(out, ResourceRequest.class, resourceRequest, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>resourceRequest</var> of type {@link org.openspcoop2.core.registry.ResourceRequest}
	 * 
	 * @param out OutputStream to serialize the object <var>resourceRequest</var>
	 * @param resourceRequest Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ResourceRequest resourceRequest,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, ResourceRequest.class, resourceRequest, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>resourceRequest</var> of type {@link org.openspcoop2.core.registry.ResourceRequest}
	 * 
	 * @param resourceRequest Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ResourceRequest resourceRequest) throws SerializerException {
		return this.objToXml(ResourceRequest.class, resourceRequest, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>resourceRequest</var> of type {@link org.openspcoop2.core.registry.ResourceRequest}
	 * 
	 * @param resourceRequest Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ResourceRequest resourceRequest,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ResourceRequest.class, resourceRequest, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>resourceRequest</var> of type {@link org.openspcoop2.core.registry.ResourceRequest}
	 * 
	 * @param resourceRequest Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ResourceRequest resourceRequest) throws SerializerException {
		return this.objToXml(ResourceRequest.class, resourceRequest, false).toString();
	}
	/**
	 * Serialize to String the object <var>resourceRequest</var> of type {@link org.openspcoop2.core.registry.ResourceRequest}
	 * 
	 * @param resourceRequest Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ResourceRequest resourceRequest,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ResourceRequest.class, resourceRequest, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: resource-representation
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>resourceRepresentation</var> of type {@link org.openspcoop2.core.registry.ResourceRepresentation}
	 * 
	 * @param fileName Xml file to serialize the object <var>resourceRepresentation</var>
	 * @param resourceRepresentation Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ResourceRepresentation resourceRepresentation) throws SerializerException {
		this.objToXml(fileName, ResourceRepresentation.class, resourceRepresentation, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>resourceRepresentation</var> of type {@link org.openspcoop2.core.registry.ResourceRepresentation}
	 * 
	 * @param fileName Xml file to serialize the object <var>resourceRepresentation</var>
	 * @param resourceRepresentation Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ResourceRepresentation resourceRepresentation,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, ResourceRepresentation.class, resourceRepresentation, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>resourceRepresentation</var> of type {@link org.openspcoop2.core.registry.ResourceRepresentation}
	 * 
	 * @param file Xml file to serialize the object <var>resourceRepresentation</var>
	 * @param resourceRepresentation Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ResourceRepresentation resourceRepresentation) throws SerializerException {
		this.objToXml(file, ResourceRepresentation.class, resourceRepresentation, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>resourceRepresentation</var> of type {@link org.openspcoop2.core.registry.ResourceRepresentation}
	 * 
	 * @param file Xml file to serialize the object <var>resourceRepresentation</var>
	 * @param resourceRepresentation Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ResourceRepresentation resourceRepresentation,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, ResourceRepresentation.class, resourceRepresentation, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>resourceRepresentation</var> of type {@link org.openspcoop2.core.registry.ResourceRepresentation}
	 * 
	 * @param out OutputStream to serialize the object <var>resourceRepresentation</var>
	 * @param resourceRepresentation Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ResourceRepresentation resourceRepresentation) throws SerializerException {
		this.objToXml(out, ResourceRepresentation.class, resourceRepresentation, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>resourceRepresentation</var> of type {@link org.openspcoop2.core.registry.ResourceRepresentation}
	 * 
	 * @param out OutputStream to serialize the object <var>resourceRepresentation</var>
	 * @param resourceRepresentation Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ResourceRepresentation resourceRepresentation,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, ResourceRepresentation.class, resourceRepresentation, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>resourceRepresentation</var> of type {@link org.openspcoop2.core.registry.ResourceRepresentation}
	 * 
	 * @param resourceRepresentation Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ResourceRepresentation resourceRepresentation) throws SerializerException {
		return this.objToXml(ResourceRepresentation.class, resourceRepresentation, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>resourceRepresentation</var> of type {@link org.openspcoop2.core.registry.ResourceRepresentation}
	 * 
	 * @param resourceRepresentation Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ResourceRepresentation resourceRepresentation,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ResourceRepresentation.class, resourceRepresentation, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>resourceRepresentation</var> of type {@link org.openspcoop2.core.registry.ResourceRepresentation}
	 * 
	 * @param resourceRepresentation Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ResourceRepresentation resourceRepresentation) throws SerializerException {
		return this.objToXml(ResourceRepresentation.class, resourceRepresentation, false).toString();
	}
	/**
	 * Serialize to String the object <var>resourceRepresentation</var> of type {@link org.openspcoop2.core.registry.ResourceRepresentation}
	 * 
	 * @param resourceRepresentation Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ResourceRepresentation resourceRepresentation,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ResourceRepresentation.class, resourceRepresentation, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: resource
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>resource</var> of type {@link org.openspcoop2.core.registry.Resource}
	 * 
	 * @param fileName Xml file to serialize the object <var>resource</var>
	 * @param resource Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Resource resource) throws SerializerException {
		this.objToXml(fileName, Resource.class, resource, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>resource</var> of type {@link org.openspcoop2.core.registry.Resource}
	 * 
	 * @param fileName Xml file to serialize the object <var>resource</var>
	 * @param resource Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Resource resource,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Resource.class, resource, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>resource</var> of type {@link org.openspcoop2.core.registry.Resource}
	 * 
	 * @param file Xml file to serialize the object <var>resource</var>
	 * @param resource Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Resource resource) throws SerializerException {
		this.objToXml(file, Resource.class, resource, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>resource</var> of type {@link org.openspcoop2.core.registry.Resource}
	 * 
	 * @param file Xml file to serialize the object <var>resource</var>
	 * @param resource Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Resource resource,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Resource.class, resource, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>resource</var> of type {@link org.openspcoop2.core.registry.Resource}
	 * 
	 * @param out OutputStream to serialize the object <var>resource</var>
	 * @param resource Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Resource resource) throws SerializerException {
		this.objToXml(out, Resource.class, resource, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>resource</var> of type {@link org.openspcoop2.core.registry.Resource}
	 * 
	 * @param out OutputStream to serialize the object <var>resource</var>
	 * @param resource Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Resource resource,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Resource.class, resource, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>resource</var> of type {@link org.openspcoop2.core.registry.Resource}
	 * 
	 * @param resource Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Resource resource) throws SerializerException {
		return this.objToXml(Resource.class, resource, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>resource</var> of type {@link org.openspcoop2.core.registry.Resource}
	 * 
	 * @param resource Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Resource resource,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Resource.class, resource, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>resource</var> of type {@link org.openspcoop2.core.registry.Resource}
	 * 
	 * @param resource Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Resource resource) throws SerializerException {
		return this.objToXml(Resource.class, resource, false).toString();
	}
	/**
	 * Serialize to String the object <var>resource</var> of type {@link org.openspcoop2.core.registry.Resource}
	 * 
	 * @param resource Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Resource resource,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Resource.class, resource, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: resource-response
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>resourceResponse</var> of type {@link org.openspcoop2.core.registry.ResourceResponse}
	 * 
	 * @param fileName Xml file to serialize the object <var>resourceResponse</var>
	 * @param resourceResponse Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ResourceResponse resourceResponse) throws SerializerException {
		this.objToXml(fileName, ResourceResponse.class, resourceResponse, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>resourceResponse</var> of type {@link org.openspcoop2.core.registry.ResourceResponse}
	 * 
	 * @param fileName Xml file to serialize the object <var>resourceResponse</var>
	 * @param resourceResponse Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ResourceResponse resourceResponse,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, ResourceResponse.class, resourceResponse, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>resourceResponse</var> of type {@link org.openspcoop2.core.registry.ResourceResponse}
	 * 
	 * @param file Xml file to serialize the object <var>resourceResponse</var>
	 * @param resourceResponse Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ResourceResponse resourceResponse) throws SerializerException {
		this.objToXml(file, ResourceResponse.class, resourceResponse, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>resourceResponse</var> of type {@link org.openspcoop2.core.registry.ResourceResponse}
	 * 
	 * @param file Xml file to serialize the object <var>resourceResponse</var>
	 * @param resourceResponse Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ResourceResponse resourceResponse,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, ResourceResponse.class, resourceResponse, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>resourceResponse</var> of type {@link org.openspcoop2.core.registry.ResourceResponse}
	 * 
	 * @param out OutputStream to serialize the object <var>resourceResponse</var>
	 * @param resourceResponse Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ResourceResponse resourceResponse) throws SerializerException {
		this.objToXml(out, ResourceResponse.class, resourceResponse, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>resourceResponse</var> of type {@link org.openspcoop2.core.registry.ResourceResponse}
	 * 
	 * @param out OutputStream to serialize the object <var>resourceResponse</var>
	 * @param resourceResponse Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ResourceResponse resourceResponse,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, ResourceResponse.class, resourceResponse, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>resourceResponse</var> of type {@link org.openspcoop2.core.registry.ResourceResponse}
	 * 
	 * @param resourceResponse Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ResourceResponse resourceResponse) throws SerializerException {
		return this.objToXml(ResourceResponse.class, resourceResponse, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>resourceResponse</var> of type {@link org.openspcoop2.core.registry.ResourceResponse}
	 * 
	 * @param resourceResponse Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ResourceResponse resourceResponse,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ResourceResponse.class, resourceResponse, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>resourceResponse</var> of type {@link org.openspcoop2.core.registry.ResourceResponse}
	 * 
	 * @param resourceResponse Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ResourceResponse resourceResponse) throws SerializerException {
		return this.objToXml(ResourceResponse.class, resourceResponse, false).toString();
	}
	/**
	 * Serialize to String the object <var>resourceResponse</var> of type {@link org.openspcoop2.core.registry.ResourceResponse}
	 * 
	 * @param resourceResponse Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ResourceResponse resourceResponse,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ResourceResponse.class, resourceResponse, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: id-ruolo
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>idRuolo</var> of type {@link org.openspcoop2.core.registry.IdRuolo}
	 * 
	 * @param fileName Xml file to serialize the object <var>idRuolo</var>
	 * @param idRuolo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,IdRuolo idRuolo) throws SerializerException {
		this.objToXml(fileName, IdRuolo.class, idRuolo, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>idRuolo</var> of type {@link org.openspcoop2.core.registry.IdRuolo}
	 * 
	 * @param fileName Xml file to serialize the object <var>idRuolo</var>
	 * @param idRuolo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,IdRuolo idRuolo,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, IdRuolo.class, idRuolo, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>idRuolo</var> of type {@link org.openspcoop2.core.registry.IdRuolo}
	 * 
	 * @param file Xml file to serialize the object <var>idRuolo</var>
	 * @param idRuolo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,IdRuolo idRuolo) throws SerializerException {
		this.objToXml(file, IdRuolo.class, idRuolo, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>idRuolo</var> of type {@link org.openspcoop2.core.registry.IdRuolo}
	 * 
	 * @param file Xml file to serialize the object <var>idRuolo</var>
	 * @param idRuolo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,IdRuolo idRuolo,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, IdRuolo.class, idRuolo, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>idRuolo</var> of type {@link org.openspcoop2.core.registry.IdRuolo}
	 * 
	 * @param out OutputStream to serialize the object <var>idRuolo</var>
	 * @param idRuolo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,IdRuolo idRuolo) throws SerializerException {
		this.objToXml(out, IdRuolo.class, idRuolo, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>idRuolo</var> of type {@link org.openspcoop2.core.registry.IdRuolo}
	 * 
	 * @param out OutputStream to serialize the object <var>idRuolo</var>
	 * @param idRuolo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,IdRuolo idRuolo,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, IdRuolo.class, idRuolo, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>idRuolo</var> of type {@link org.openspcoop2.core.registry.IdRuolo}
	 * 
	 * @param idRuolo Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(IdRuolo idRuolo) throws SerializerException {
		return this.objToXml(IdRuolo.class, idRuolo, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>idRuolo</var> of type {@link org.openspcoop2.core.registry.IdRuolo}
	 * 
	 * @param idRuolo Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(IdRuolo idRuolo,boolean prettyPrint) throws SerializerException {
		return this.objToXml(IdRuolo.class, idRuolo, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>idRuolo</var> of type {@link org.openspcoop2.core.registry.IdRuolo}
	 * 
	 * @param idRuolo Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(IdRuolo idRuolo) throws SerializerException {
		return this.objToXml(IdRuolo.class, idRuolo, false).toString();
	}
	/**
	 * Serialize to String the object <var>idRuolo</var> of type {@link org.openspcoop2.core.registry.IdRuolo}
	 * 
	 * @param idRuolo Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(IdRuolo idRuolo,boolean prettyPrint) throws SerializerException {
		return this.objToXml(IdRuolo.class, idRuolo, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: resource-representation-json
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>resourceRepresentationJson</var> of type {@link org.openspcoop2.core.registry.ResourceRepresentationJson}
	 * 
	 * @param fileName Xml file to serialize the object <var>resourceRepresentationJson</var>
	 * @param resourceRepresentationJson Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ResourceRepresentationJson resourceRepresentationJson) throws SerializerException {
		this.objToXml(fileName, ResourceRepresentationJson.class, resourceRepresentationJson, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>resourceRepresentationJson</var> of type {@link org.openspcoop2.core.registry.ResourceRepresentationJson}
	 * 
	 * @param fileName Xml file to serialize the object <var>resourceRepresentationJson</var>
	 * @param resourceRepresentationJson Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ResourceRepresentationJson resourceRepresentationJson,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, ResourceRepresentationJson.class, resourceRepresentationJson, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>resourceRepresentationJson</var> of type {@link org.openspcoop2.core.registry.ResourceRepresentationJson}
	 * 
	 * @param file Xml file to serialize the object <var>resourceRepresentationJson</var>
	 * @param resourceRepresentationJson Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ResourceRepresentationJson resourceRepresentationJson) throws SerializerException {
		this.objToXml(file, ResourceRepresentationJson.class, resourceRepresentationJson, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>resourceRepresentationJson</var> of type {@link org.openspcoop2.core.registry.ResourceRepresentationJson}
	 * 
	 * @param file Xml file to serialize the object <var>resourceRepresentationJson</var>
	 * @param resourceRepresentationJson Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ResourceRepresentationJson resourceRepresentationJson,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, ResourceRepresentationJson.class, resourceRepresentationJson, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>resourceRepresentationJson</var> of type {@link org.openspcoop2.core.registry.ResourceRepresentationJson}
	 * 
	 * @param out OutputStream to serialize the object <var>resourceRepresentationJson</var>
	 * @param resourceRepresentationJson Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ResourceRepresentationJson resourceRepresentationJson) throws SerializerException {
		this.objToXml(out, ResourceRepresentationJson.class, resourceRepresentationJson, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>resourceRepresentationJson</var> of type {@link org.openspcoop2.core.registry.ResourceRepresentationJson}
	 * 
	 * @param out OutputStream to serialize the object <var>resourceRepresentationJson</var>
	 * @param resourceRepresentationJson Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ResourceRepresentationJson resourceRepresentationJson,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, ResourceRepresentationJson.class, resourceRepresentationJson, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>resourceRepresentationJson</var> of type {@link org.openspcoop2.core.registry.ResourceRepresentationJson}
	 * 
	 * @param resourceRepresentationJson Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ResourceRepresentationJson resourceRepresentationJson) throws SerializerException {
		return this.objToXml(ResourceRepresentationJson.class, resourceRepresentationJson, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>resourceRepresentationJson</var> of type {@link org.openspcoop2.core.registry.ResourceRepresentationJson}
	 * 
	 * @param resourceRepresentationJson Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ResourceRepresentationJson resourceRepresentationJson,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ResourceRepresentationJson.class, resourceRepresentationJson, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>resourceRepresentationJson</var> of type {@link org.openspcoop2.core.registry.ResourceRepresentationJson}
	 * 
	 * @param resourceRepresentationJson Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ResourceRepresentationJson resourceRepresentationJson) throws SerializerException {
		return this.objToXml(ResourceRepresentationJson.class, resourceRepresentationJson, false).toString();
	}
	/**
	 * Serialize to String the object <var>resourceRepresentationJson</var> of type {@link org.openspcoop2.core.registry.ResourceRepresentationJson}
	 * 
	 * @param resourceRepresentationJson Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ResourceRepresentationJson resourceRepresentationJson,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ResourceRepresentationJson.class, resourceRepresentationJson, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: message-part
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>messagePart</var> of type {@link org.openspcoop2.core.registry.MessagePart}
	 * 
	 * @param fileName Xml file to serialize the object <var>messagePart</var>
	 * @param messagePart Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,MessagePart messagePart) throws SerializerException {
		this.objToXml(fileName, MessagePart.class, messagePart, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>messagePart</var> of type {@link org.openspcoop2.core.registry.MessagePart}
	 * 
	 * @param fileName Xml file to serialize the object <var>messagePart</var>
	 * @param messagePart Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,MessagePart messagePart,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, MessagePart.class, messagePart, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>messagePart</var> of type {@link org.openspcoop2.core.registry.MessagePart}
	 * 
	 * @param file Xml file to serialize the object <var>messagePart</var>
	 * @param messagePart Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,MessagePart messagePart) throws SerializerException {
		this.objToXml(file, MessagePart.class, messagePart, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>messagePart</var> of type {@link org.openspcoop2.core.registry.MessagePart}
	 * 
	 * @param file Xml file to serialize the object <var>messagePart</var>
	 * @param messagePart Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,MessagePart messagePart,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, MessagePart.class, messagePart, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>messagePart</var> of type {@link org.openspcoop2.core.registry.MessagePart}
	 * 
	 * @param out OutputStream to serialize the object <var>messagePart</var>
	 * @param messagePart Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,MessagePart messagePart) throws SerializerException {
		this.objToXml(out, MessagePart.class, messagePart, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>messagePart</var> of type {@link org.openspcoop2.core.registry.MessagePart}
	 * 
	 * @param out OutputStream to serialize the object <var>messagePart</var>
	 * @param messagePart Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,MessagePart messagePart,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, MessagePart.class, messagePart, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>messagePart</var> of type {@link org.openspcoop2.core.registry.MessagePart}
	 * 
	 * @param messagePart Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(MessagePart messagePart) throws SerializerException {
		return this.objToXml(MessagePart.class, messagePart, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>messagePart</var> of type {@link org.openspcoop2.core.registry.MessagePart}
	 * 
	 * @param messagePart Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(MessagePart messagePart,boolean prettyPrint) throws SerializerException {
		return this.objToXml(MessagePart.class, messagePart, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>messagePart</var> of type {@link org.openspcoop2.core.registry.MessagePart}
	 * 
	 * @param messagePart Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(MessagePart messagePart) throws SerializerException {
		return this.objToXml(MessagePart.class, messagePart, false).toString();
	}
	/**
	 * Serialize to String the object <var>messagePart</var> of type {@link org.openspcoop2.core.registry.MessagePart}
	 * 
	 * @param messagePart Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(MessagePart messagePart,boolean prettyPrint) throws SerializerException {
		return this.objToXml(MessagePart.class, messagePart, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: id-porta-dominio
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>idPortaDominio</var> of type {@link org.openspcoop2.core.registry.IdPortaDominio}
	 * 
	 * @param fileName Xml file to serialize the object <var>idPortaDominio</var>
	 * @param idPortaDominio Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,IdPortaDominio idPortaDominio) throws SerializerException {
		this.objToXml(fileName, IdPortaDominio.class, idPortaDominio, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>idPortaDominio</var> of type {@link org.openspcoop2.core.registry.IdPortaDominio}
	 * 
	 * @param fileName Xml file to serialize the object <var>idPortaDominio</var>
	 * @param idPortaDominio Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,IdPortaDominio idPortaDominio,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, IdPortaDominio.class, idPortaDominio, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>idPortaDominio</var> of type {@link org.openspcoop2.core.registry.IdPortaDominio}
	 * 
	 * @param file Xml file to serialize the object <var>idPortaDominio</var>
	 * @param idPortaDominio Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,IdPortaDominio idPortaDominio) throws SerializerException {
		this.objToXml(file, IdPortaDominio.class, idPortaDominio, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>idPortaDominio</var> of type {@link org.openspcoop2.core.registry.IdPortaDominio}
	 * 
	 * @param file Xml file to serialize the object <var>idPortaDominio</var>
	 * @param idPortaDominio Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,IdPortaDominio idPortaDominio,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, IdPortaDominio.class, idPortaDominio, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>idPortaDominio</var> of type {@link org.openspcoop2.core.registry.IdPortaDominio}
	 * 
	 * @param out OutputStream to serialize the object <var>idPortaDominio</var>
	 * @param idPortaDominio Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,IdPortaDominio idPortaDominio) throws SerializerException {
		this.objToXml(out, IdPortaDominio.class, idPortaDominio, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>idPortaDominio</var> of type {@link org.openspcoop2.core.registry.IdPortaDominio}
	 * 
	 * @param out OutputStream to serialize the object <var>idPortaDominio</var>
	 * @param idPortaDominio Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,IdPortaDominio idPortaDominio,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, IdPortaDominio.class, idPortaDominio, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>idPortaDominio</var> of type {@link org.openspcoop2.core.registry.IdPortaDominio}
	 * 
	 * @param idPortaDominio Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(IdPortaDominio idPortaDominio) throws SerializerException {
		return this.objToXml(IdPortaDominio.class, idPortaDominio, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>idPortaDominio</var> of type {@link org.openspcoop2.core.registry.IdPortaDominio}
	 * 
	 * @param idPortaDominio Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(IdPortaDominio idPortaDominio,boolean prettyPrint) throws SerializerException {
		return this.objToXml(IdPortaDominio.class, idPortaDominio, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>idPortaDominio</var> of type {@link org.openspcoop2.core.registry.IdPortaDominio}
	 * 
	 * @param idPortaDominio Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(IdPortaDominio idPortaDominio) throws SerializerException {
		return this.objToXml(IdPortaDominio.class, idPortaDominio, false).toString();
	}
	/**
	 * Serialize to String the object <var>idPortaDominio</var> of type {@link org.openspcoop2.core.registry.IdPortaDominio}
	 * 
	 * @param idPortaDominio Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(IdPortaDominio idPortaDominio,boolean prettyPrint) throws SerializerException {
		return this.objToXml(IdPortaDominio.class, idPortaDominio, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: documento
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>documento</var> of type {@link org.openspcoop2.core.registry.Documento}
	 * 
	 * @param fileName Xml file to serialize the object <var>documento</var>
	 * @param documento Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Documento documento) throws SerializerException {
		this.objToXml(fileName, Documento.class, documento, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>documento</var> of type {@link org.openspcoop2.core.registry.Documento}
	 * 
	 * @param fileName Xml file to serialize the object <var>documento</var>
	 * @param documento Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Documento documento,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Documento.class, documento, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>documento</var> of type {@link org.openspcoop2.core.registry.Documento}
	 * 
	 * @param file Xml file to serialize the object <var>documento</var>
	 * @param documento Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Documento documento) throws SerializerException {
		this.objToXml(file, Documento.class, documento, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>documento</var> of type {@link org.openspcoop2.core.registry.Documento}
	 * 
	 * @param file Xml file to serialize the object <var>documento</var>
	 * @param documento Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Documento documento,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Documento.class, documento, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>documento</var> of type {@link org.openspcoop2.core.registry.Documento}
	 * 
	 * @param out OutputStream to serialize the object <var>documento</var>
	 * @param documento Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Documento documento) throws SerializerException {
		this.objToXml(out, Documento.class, documento, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>documento</var> of type {@link org.openspcoop2.core.registry.Documento}
	 * 
	 * @param out OutputStream to serialize the object <var>documento</var>
	 * @param documento Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Documento documento,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Documento.class, documento, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>documento</var> of type {@link org.openspcoop2.core.registry.Documento}
	 * 
	 * @param documento Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Documento documento) throws SerializerException {
		return this.objToXml(Documento.class, documento, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>documento</var> of type {@link org.openspcoop2.core.registry.Documento}
	 * 
	 * @param documento Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Documento documento,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Documento.class, documento, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>documento</var> of type {@link org.openspcoop2.core.registry.Documento}
	 * 
	 * @param documento Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Documento documento) throws SerializerException {
		return this.objToXml(Documento.class, documento, false).toString();
	}
	/**
	 * Serialize to String the object <var>documento</var> of type {@link org.openspcoop2.core.registry.Documento}
	 * 
	 * @param documento Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Documento documento,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Documento.class, documento, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: id-soggetto
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>idSoggetto</var> of type {@link org.openspcoop2.core.registry.IdSoggetto}
	 * 
	 * @param fileName Xml file to serialize the object <var>idSoggetto</var>
	 * @param idSoggetto Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,IdSoggetto idSoggetto) throws SerializerException {
		this.objToXml(fileName, IdSoggetto.class, idSoggetto, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>idSoggetto</var> of type {@link org.openspcoop2.core.registry.IdSoggetto}
	 * 
	 * @param fileName Xml file to serialize the object <var>idSoggetto</var>
	 * @param idSoggetto Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,IdSoggetto idSoggetto,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, IdSoggetto.class, idSoggetto, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>idSoggetto</var> of type {@link org.openspcoop2.core.registry.IdSoggetto}
	 * 
	 * @param file Xml file to serialize the object <var>idSoggetto</var>
	 * @param idSoggetto Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,IdSoggetto idSoggetto) throws SerializerException {
		this.objToXml(file, IdSoggetto.class, idSoggetto, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>idSoggetto</var> of type {@link org.openspcoop2.core.registry.IdSoggetto}
	 * 
	 * @param file Xml file to serialize the object <var>idSoggetto</var>
	 * @param idSoggetto Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,IdSoggetto idSoggetto,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, IdSoggetto.class, idSoggetto, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>idSoggetto</var> of type {@link org.openspcoop2.core.registry.IdSoggetto}
	 * 
	 * @param out OutputStream to serialize the object <var>idSoggetto</var>
	 * @param idSoggetto Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,IdSoggetto idSoggetto) throws SerializerException {
		this.objToXml(out, IdSoggetto.class, idSoggetto, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>idSoggetto</var> of type {@link org.openspcoop2.core.registry.IdSoggetto}
	 * 
	 * @param out OutputStream to serialize the object <var>idSoggetto</var>
	 * @param idSoggetto Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,IdSoggetto idSoggetto,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, IdSoggetto.class, idSoggetto, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>idSoggetto</var> of type {@link org.openspcoop2.core.registry.IdSoggetto}
	 * 
	 * @param idSoggetto Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(IdSoggetto idSoggetto) throws SerializerException {
		return this.objToXml(IdSoggetto.class, idSoggetto, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>idSoggetto</var> of type {@link org.openspcoop2.core.registry.IdSoggetto}
	 * 
	 * @param idSoggetto Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(IdSoggetto idSoggetto,boolean prettyPrint) throws SerializerException {
		return this.objToXml(IdSoggetto.class, idSoggetto, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>idSoggetto</var> of type {@link org.openspcoop2.core.registry.IdSoggetto}
	 * 
	 * @param idSoggetto Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(IdSoggetto idSoggetto) throws SerializerException {
		return this.objToXml(IdSoggetto.class, idSoggetto, false).toString();
	}
	/**
	 * Serialize to String the object <var>idSoggetto</var> of type {@link org.openspcoop2.core.registry.IdSoggetto}
	 * 
	 * @param idSoggetto Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(IdSoggetto idSoggetto,boolean prettyPrint) throws SerializerException {
		return this.objToXml(IdSoggetto.class, idSoggetto, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: id-accordo-servizio-parte-specifica
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>idAccordoServizioParteSpecifica</var> of type {@link org.openspcoop2.core.registry.IdAccordoServizioParteSpecifica}
	 * 
	 * @param fileName Xml file to serialize the object <var>idAccordoServizioParteSpecifica</var>
	 * @param idAccordoServizioParteSpecifica Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,IdAccordoServizioParteSpecifica idAccordoServizioParteSpecifica) throws SerializerException {
		this.objToXml(fileName, IdAccordoServizioParteSpecifica.class, idAccordoServizioParteSpecifica, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>idAccordoServizioParteSpecifica</var> of type {@link org.openspcoop2.core.registry.IdAccordoServizioParteSpecifica}
	 * 
	 * @param fileName Xml file to serialize the object <var>idAccordoServizioParteSpecifica</var>
	 * @param idAccordoServizioParteSpecifica Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,IdAccordoServizioParteSpecifica idAccordoServizioParteSpecifica,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, IdAccordoServizioParteSpecifica.class, idAccordoServizioParteSpecifica, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>idAccordoServizioParteSpecifica</var> of type {@link org.openspcoop2.core.registry.IdAccordoServizioParteSpecifica}
	 * 
	 * @param file Xml file to serialize the object <var>idAccordoServizioParteSpecifica</var>
	 * @param idAccordoServizioParteSpecifica Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,IdAccordoServizioParteSpecifica idAccordoServizioParteSpecifica) throws SerializerException {
		this.objToXml(file, IdAccordoServizioParteSpecifica.class, idAccordoServizioParteSpecifica, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>idAccordoServizioParteSpecifica</var> of type {@link org.openspcoop2.core.registry.IdAccordoServizioParteSpecifica}
	 * 
	 * @param file Xml file to serialize the object <var>idAccordoServizioParteSpecifica</var>
	 * @param idAccordoServizioParteSpecifica Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,IdAccordoServizioParteSpecifica idAccordoServizioParteSpecifica,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, IdAccordoServizioParteSpecifica.class, idAccordoServizioParteSpecifica, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>idAccordoServizioParteSpecifica</var> of type {@link org.openspcoop2.core.registry.IdAccordoServizioParteSpecifica}
	 * 
	 * @param out OutputStream to serialize the object <var>idAccordoServizioParteSpecifica</var>
	 * @param idAccordoServizioParteSpecifica Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,IdAccordoServizioParteSpecifica idAccordoServizioParteSpecifica) throws SerializerException {
		this.objToXml(out, IdAccordoServizioParteSpecifica.class, idAccordoServizioParteSpecifica, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>idAccordoServizioParteSpecifica</var> of type {@link org.openspcoop2.core.registry.IdAccordoServizioParteSpecifica}
	 * 
	 * @param out OutputStream to serialize the object <var>idAccordoServizioParteSpecifica</var>
	 * @param idAccordoServizioParteSpecifica Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,IdAccordoServizioParteSpecifica idAccordoServizioParteSpecifica,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, IdAccordoServizioParteSpecifica.class, idAccordoServizioParteSpecifica, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>idAccordoServizioParteSpecifica</var> of type {@link org.openspcoop2.core.registry.IdAccordoServizioParteSpecifica}
	 * 
	 * @param idAccordoServizioParteSpecifica Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(IdAccordoServizioParteSpecifica idAccordoServizioParteSpecifica) throws SerializerException {
		return this.objToXml(IdAccordoServizioParteSpecifica.class, idAccordoServizioParteSpecifica, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>idAccordoServizioParteSpecifica</var> of type {@link org.openspcoop2.core.registry.IdAccordoServizioParteSpecifica}
	 * 
	 * @param idAccordoServizioParteSpecifica Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(IdAccordoServizioParteSpecifica idAccordoServizioParteSpecifica,boolean prettyPrint) throws SerializerException {
		return this.objToXml(IdAccordoServizioParteSpecifica.class, idAccordoServizioParteSpecifica, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>idAccordoServizioParteSpecifica</var> of type {@link org.openspcoop2.core.registry.IdAccordoServizioParteSpecifica}
	 * 
	 * @param idAccordoServizioParteSpecifica Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(IdAccordoServizioParteSpecifica idAccordoServizioParteSpecifica) throws SerializerException {
		return this.objToXml(IdAccordoServizioParteSpecifica.class, idAccordoServizioParteSpecifica, false).toString();
	}
	/**
	 * Serialize to String the object <var>idAccordoServizioParteSpecifica</var> of type {@link org.openspcoop2.core.registry.IdAccordoServizioParteSpecifica}
	 * 
	 * @param idAccordoServizioParteSpecifica Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(IdAccordoServizioParteSpecifica idAccordoServizioParteSpecifica,boolean prettyPrint) throws SerializerException {
		return this.objToXml(IdAccordoServizioParteSpecifica.class, idAccordoServizioParteSpecifica, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: gruppo-accordo
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>gruppoAccordo</var> of type {@link org.openspcoop2.core.registry.GruppoAccordo}
	 * 
	 * @param fileName Xml file to serialize the object <var>gruppoAccordo</var>
	 * @param gruppoAccordo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,GruppoAccordo gruppoAccordo) throws SerializerException {
		this.objToXml(fileName, GruppoAccordo.class, gruppoAccordo, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>gruppoAccordo</var> of type {@link org.openspcoop2.core.registry.GruppoAccordo}
	 * 
	 * @param fileName Xml file to serialize the object <var>gruppoAccordo</var>
	 * @param gruppoAccordo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,GruppoAccordo gruppoAccordo,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, GruppoAccordo.class, gruppoAccordo, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>gruppoAccordo</var> of type {@link org.openspcoop2.core.registry.GruppoAccordo}
	 * 
	 * @param file Xml file to serialize the object <var>gruppoAccordo</var>
	 * @param gruppoAccordo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,GruppoAccordo gruppoAccordo) throws SerializerException {
		this.objToXml(file, GruppoAccordo.class, gruppoAccordo, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>gruppoAccordo</var> of type {@link org.openspcoop2.core.registry.GruppoAccordo}
	 * 
	 * @param file Xml file to serialize the object <var>gruppoAccordo</var>
	 * @param gruppoAccordo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,GruppoAccordo gruppoAccordo,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, GruppoAccordo.class, gruppoAccordo, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>gruppoAccordo</var> of type {@link org.openspcoop2.core.registry.GruppoAccordo}
	 * 
	 * @param out OutputStream to serialize the object <var>gruppoAccordo</var>
	 * @param gruppoAccordo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,GruppoAccordo gruppoAccordo) throws SerializerException {
		this.objToXml(out, GruppoAccordo.class, gruppoAccordo, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>gruppoAccordo</var> of type {@link org.openspcoop2.core.registry.GruppoAccordo}
	 * 
	 * @param out OutputStream to serialize the object <var>gruppoAccordo</var>
	 * @param gruppoAccordo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,GruppoAccordo gruppoAccordo,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, GruppoAccordo.class, gruppoAccordo, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>gruppoAccordo</var> of type {@link org.openspcoop2.core.registry.GruppoAccordo}
	 * 
	 * @param gruppoAccordo Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(GruppoAccordo gruppoAccordo) throws SerializerException {
		return this.objToXml(GruppoAccordo.class, gruppoAccordo, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>gruppoAccordo</var> of type {@link org.openspcoop2.core.registry.GruppoAccordo}
	 * 
	 * @param gruppoAccordo Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(GruppoAccordo gruppoAccordo,boolean prettyPrint) throws SerializerException {
		return this.objToXml(GruppoAccordo.class, gruppoAccordo, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>gruppoAccordo</var> of type {@link org.openspcoop2.core.registry.GruppoAccordo}
	 * 
	 * @param gruppoAccordo Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(GruppoAccordo gruppoAccordo) throws SerializerException {
		return this.objToXml(GruppoAccordo.class, gruppoAccordo, false).toString();
	}
	/**
	 * Serialize to String the object <var>gruppoAccordo</var> of type {@link org.openspcoop2.core.registry.GruppoAccordo}
	 * 
	 * @param gruppoAccordo Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(GruppoAccordo gruppoAccordo,boolean prettyPrint) throws SerializerException {
		return this.objToXml(GruppoAccordo.class, gruppoAccordo, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: gruppi-accordo
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>gruppiAccordo</var> of type {@link org.openspcoop2.core.registry.GruppiAccordo}
	 * 
	 * @param fileName Xml file to serialize the object <var>gruppiAccordo</var>
	 * @param gruppiAccordo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,GruppiAccordo gruppiAccordo) throws SerializerException {
		this.objToXml(fileName, GruppiAccordo.class, gruppiAccordo, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>gruppiAccordo</var> of type {@link org.openspcoop2.core.registry.GruppiAccordo}
	 * 
	 * @param fileName Xml file to serialize the object <var>gruppiAccordo</var>
	 * @param gruppiAccordo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,GruppiAccordo gruppiAccordo,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, GruppiAccordo.class, gruppiAccordo, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>gruppiAccordo</var> of type {@link org.openspcoop2.core.registry.GruppiAccordo}
	 * 
	 * @param file Xml file to serialize the object <var>gruppiAccordo</var>
	 * @param gruppiAccordo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,GruppiAccordo gruppiAccordo) throws SerializerException {
		this.objToXml(file, GruppiAccordo.class, gruppiAccordo, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>gruppiAccordo</var> of type {@link org.openspcoop2.core.registry.GruppiAccordo}
	 * 
	 * @param file Xml file to serialize the object <var>gruppiAccordo</var>
	 * @param gruppiAccordo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,GruppiAccordo gruppiAccordo,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, GruppiAccordo.class, gruppiAccordo, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>gruppiAccordo</var> of type {@link org.openspcoop2.core.registry.GruppiAccordo}
	 * 
	 * @param out OutputStream to serialize the object <var>gruppiAccordo</var>
	 * @param gruppiAccordo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,GruppiAccordo gruppiAccordo) throws SerializerException {
		this.objToXml(out, GruppiAccordo.class, gruppiAccordo, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>gruppiAccordo</var> of type {@link org.openspcoop2.core.registry.GruppiAccordo}
	 * 
	 * @param out OutputStream to serialize the object <var>gruppiAccordo</var>
	 * @param gruppiAccordo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,GruppiAccordo gruppiAccordo,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, GruppiAccordo.class, gruppiAccordo, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>gruppiAccordo</var> of type {@link org.openspcoop2.core.registry.GruppiAccordo}
	 * 
	 * @param gruppiAccordo Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(GruppiAccordo gruppiAccordo) throws SerializerException {
		return this.objToXml(GruppiAccordo.class, gruppiAccordo, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>gruppiAccordo</var> of type {@link org.openspcoop2.core.registry.GruppiAccordo}
	 * 
	 * @param gruppiAccordo Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(GruppiAccordo gruppiAccordo,boolean prettyPrint) throws SerializerException {
		return this.objToXml(GruppiAccordo.class, gruppiAccordo, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>gruppiAccordo</var> of type {@link org.openspcoop2.core.registry.GruppiAccordo}
	 * 
	 * @param gruppiAccordo Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(GruppiAccordo gruppiAccordo) throws SerializerException {
		return this.objToXml(GruppiAccordo.class, gruppiAccordo, false).toString();
	}
	/**
	 * Serialize to String the object <var>gruppiAccordo</var> of type {@link org.openspcoop2.core.registry.GruppiAccordo}
	 * 
	 * @param gruppiAccordo Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(GruppiAccordo gruppiAccordo,boolean prettyPrint) throws SerializerException {
		return this.objToXml(GruppiAccordo.class, gruppiAccordo, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: id-scope
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>idScope</var> of type {@link org.openspcoop2.core.registry.IdScope}
	 * 
	 * @param fileName Xml file to serialize the object <var>idScope</var>
	 * @param idScope Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,IdScope idScope) throws SerializerException {
		this.objToXml(fileName, IdScope.class, idScope, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>idScope</var> of type {@link org.openspcoop2.core.registry.IdScope}
	 * 
	 * @param fileName Xml file to serialize the object <var>idScope</var>
	 * @param idScope Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,IdScope idScope,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, IdScope.class, idScope, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>idScope</var> of type {@link org.openspcoop2.core.registry.IdScope}
	 * 
	 * @param file Xml file to serialize the object <var>idScope</var>
	 * @param idScope Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,IdScope idScope) throws SerializerException {
		this.objToXml(file, IdScope.class, idScope, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>idScope</var> of type {@link org.openspcoop2.core.registry.IdScope}
	 * 
	 * @param file Xml file to serialize the object <var>idScope</var>
	 * @param idScope Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,IdScope idScope,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, IdScope.class, idScope, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>idScope</var> of type {@link org.openspcoop2.core.registry.IdScope}
	 * 
	 * @param out OutputStream to serialize the object <var>idScope</var>
	 * @param idScope Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,IdScope idScope) throws SerializerException {
		this.objToXml(out, IdScope.class, idScope, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>idScope</var> of type {@link org.openspcoop2.core.registry.IdScope}
	 * 
	 * @param out OutputStream to serialize the object <var>idScope</var>
	 * @param idScope Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,IdScope idScope,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, IdScope.class, idScope, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>idScope</var> of type {@link org.openspcoop2.core.registry.IdScope}
	 * 
	 * @param idScope Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(IdScope idScope) throws SerializerException {
		return this.objToXml(IdScope.class, idScope, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>idScope</var> of type {@link org.openspcoop2.core.registry.IdScope}
	 * 
	 * @param idScope Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(IdScope idScope,boolean prettyPrint) throws SerializerException {
		return this.objToXml(IdScope.class, idScope, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>idScope</var> of type {@link org.openspcoop2.core.registry.IdScope}
	 * 
	 * @param idScope Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(IdScope idScope) throws SerializerException {
		return this.objToXml(IdScope.class, idScope, false).toString();
	}
	/**
	 * Serialize to String the object <var>idScope</var> of type {@link org.openspcoop2.core.registry.IdScope}
	 * 
	 * @param idScope Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(IdScope idScope,boolean prettyPrint) throws SerializerException {
		return this.objToXml(IdScope.class, idScope, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: configurazione-servizio
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>configurazioneServizio</var> of type {@link org.openspcoop2.core.registry.ConfigurazioneServizio}
	 * 
	 * @param fileName Xml file to serialize the object <var>configurazioneServizio</var>
	 * @param configurazioneServizio Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ConfigurazioneServizio configurazioneServizio) throws SerializerException {
		this.objToXml(fileName, ConfigurazioneServizio.class, configurazioneServizio, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>configurazioneServizio</var> of type {@link org.openspcoop2.core.registry.ConfigurazioneServizio}
	 * 
	 * @param fileName Xml file to serialize the object <var>configurazioneServizio</var>
	 * @param configurazioneServizio Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ConfigurazioneServizio configurazioneServizio,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, ConfigurazioneServizio.class, configurazioneServizio, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>configurazioneServizio</var> of type {@link org.openspcoop2.core.registry.ConfigurazioneServizio}
	 * 
	 * @param file Xml file to serialize the object <var>configurazioneServizio</var>
	 * @param configurazioneServizio Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ConfigurazioneServizio configurazioneServizio) throws SerializerException {
		this.objToXml(file, ConfigurazioneServizio.class, configurazioneServizio, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>configurazioneServizio</var> of type {@link org.openspcoop2.core.registry.ConfigurazioneServizio}
	 * 
	 * @param file Xml file to serialize the object <var>configurazioneServizio</var>
	 * @param configurazioneServizio Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ConfigurazioneServizio configurazioneServizio,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, ConfigurazioneServizio.class, configurazioneServizio, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>configurazioneServizio</var> of type {@link org.openspcoop2.core.registry.ConfigurazioneServizio}
	 * 
	 * @param out OutputStream to serialize the object <var>configurazioneServizio</var>
	 * @param configurazioneServizio Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ConfigurazioneServizio configurazioneServizio) throws SerializerException {
		this.objToXml(out, ConfigurazioneServizio.class, configurazioneServizio, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>configurazioneServizio</var> of type {@link org.openspcoop2.core.registry.ConfigurazioneServizio}
	 * 
	 * @param out OutputStream to serialize the object <var>configurazioneServizio</var>
	 * @param configurazioneServizio Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ConfigurazioneServizio configurazioneServizio,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, ConfigurazioneServizio.class, configurazioneServizio, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>configurazioneServizio</var> of type {@link org.openspcoop2.core.registry.ConfigurazioneServizio}
	 * 
	 * @param configurazioneServizio Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ConfigurazioneServizio configurazioneServizio) throws SerializerException {
		return this.objToXml(ConfigurazioneServizio.class, configurazioneServizio, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>configurazioneServizio</var> of type {@link org.openspcoop2.core.registry.ConfigurazioneServizio}
	 * 
	 * @param configurazioneServizio Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ConfigurazioneServizio configurazioneServizio,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ConfigurazioneServizio.class, configurazioneServizio, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>configurazioneServizio</var> of type {@link org.openspcoop2.core.registry.ConfigurazioneServizio}
	 * 
	 * @param configurazioneServizio Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ConfigurazioneServizio configurazioneServizio) throws SerializerException {
		return this.objToXml(ConfigurazioneServizio.class, configurazioneServizio, false).toString();
	}
	/**
	 * Serialize to String the object <var>configurazioneServizio</var> of type {@link org.openspcoop2.core.registry.ConfigurazioneServizio}
	 * 
	 * @param configurazioneServizio Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ConfigurazioneServizio configurazioneServizio,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ConfigurazioneServizio.class, configurazioneServizio, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: accordo-servizio-parte-specifica
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>accordoServizioParteSpecifica</var> of type {@link org.openspcoop2.core.registry.AccordoServizioParteSpecifica}
	 * 
	 * @param fileName Xml file to serialize the object <var>accordoServizioParteSpecifica</var>
	 * @param accordoServizioParteSpecifica Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,AccordoServizioParteSpecifica accordoServizioParteSpecifica) throws SerializerException {
		this.objToXml(fileName, AccordoServizioParteSpecifica.class, accordoServizioParteSpecifica, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>accordoServizioParteSpecifica</var> of type {@link org.openspcoop2.core.registry.AccordoServizioParteSpecifica}
	 * 
	 * @param fileName Xml file to serialize the object <var>accordoServizioParteSpecifica</var>
	 * @param accordoServizioParteSpecifica Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,AccordoServizioParteSpecifica accordoServizioParteSpecifica,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, AccordoServizioParteSpecifica.class, accordoServizioParteSpecifica, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>accordoServizioParteSpecifica</var> of type {@link org.openspcoop2.core.registry.AccordoServizioParteSpecifica}
	 * 
	 * @param file Xml file to serialize the object <var>accordoServizioParteSpecifica</var>
	 * @param accordoServizioParteSpecifica Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,AccordoServizioParteSpecifica accordoServizioParteSpecifica) throws SerializerException {
		this.objToXml(file, AccordoServizioParteSpecifica.class, accordoServizioParteSpecifica, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>accordoServizioParteSpecifica</var> of type {@link org.openspcoop2.core.registry.AccordoServizioParteSpecifica}
	 * 
	 * @param file Xml file to serialize the object <var>accordoServizioParteSpecifica</var>
	 * @param accordoServizioParteSpecifica Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,AccordoServizioParteSpecifica accordoServizioParteSpecifica,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, AccordoServizioParteSpecifica.class, accordoServizioParteSpecifica, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>accordoServizioParteSpecifica</var> of type {@link org.openspcoop2.core.registry.AccordoServizioParteSpecifica}
	 * 
	 * @param out OutputStream to serialize the object <var>accordoServizioParteSpecifica</var>
	 * @param accordoServizioParteSpecifica Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,AccordoServizioParteSpecifica accordoServizioParteSpecifica) throws SerializerException {
		this.objToXml(out, AccordoServizioParteSpecifica.class, accordoServizioParteSpecifica, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>accordoServizioParteSpecifica</var> of type {@link org.openspcoop2.core.registry.AccordoServizioParteSpecifica}
	 * 
	 * @param out OutputStream to serialize the object <var>accordoServizioParteSpecifica</var>
	 * @param accordoServizioParteSpecifica Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,AccordoServizioParteSpecifica accordoServizioParteSpecifica,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, AccordoServizioParteSpecifica.class, accordoServizioParteSpecifica, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>accordoServizioParteSpecifica</var> of type {@link org.openspcoop2.core.registry.AccordoServizioParteSpecifica}
	 * 
	 * @param accordoServizioParteSpecifica Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(AccordoServizioParteSpecifica accordoServizioParteSpecifica) throws SerializerException {
		return this.objToXml(AccordoServizioParteSpecifica.class, accordoServizioParteSpecifica, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>accordoServizioParteSpecifica</var> of type {@link org.openspcoop2.core.registry.AccordoServizioParteSpecifica}
	 * 
	 * @param accordoServizioParteSpecifica Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(AccordoServizioParteSpecifica accordoServizioParteSpecifica,boolean prettyPrint) throws SerializerException {
		return this.objToXml(AccordoServizioParteSpecifica.class, accordoServizioParteSpecifica, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>accordoServizioParteSpecifica</var> of type {@link org.openspcoop2.core.registry.AccordoServizioParteSpecifica}
	 * 
	 * @param accordoServizioParteSpecifica Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(AccordoServizioParteSpecifica accordoServizioParteSpecifica) throws SerializerException {
		return this.objToXml(AccordoServizioParteSpecifica.class, accordoServizioParteSpecifica, false).toString();
	}
	/**
	 * Serialize to String the object <var>accordoServizioParteSpecifica</var> of type {@link org.openspcoop2.core.registry.AccordoServizioParteSpecifica}
	 * 
	 * @param accordoServizioParteSpecifica Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(AccordoServizioParteSpecifica accordoServizioParteSpecifica,boolean prettyPrint) throws SerializerException {
		return this.objToXml(AccordoServizioParteSpecifica.class, accordoServizioParteSpecifica, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: fruitore
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>fruitore</var> of type {@link org.openspcoop2.core.registry.Fruitore}
	 * 
	 * @param fileName Xml file to serialize the object <var>fruitore</var>
	 * @param fruitore Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Fruitore fruitore) throws SerializerException {
		this.objToXml(fileName, Fruitore.class, fruitore, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>fruitore</var> of type {@link org.openspcoop2.core.registry.Fruitore}
	 * 
	 * @param fileName Xml file to serialize the object <var>fruitore</var>
	 * @param fruitore Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Fruitore fruitore,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Fruitore.class, fruitore, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>fruitore</var> of type {@link org.openspcoop2.core.registry.Fruitore}
	 * 
	 * @param file Xml file to serialize the object <var>fruitore</var>
	 * @param fruitore Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Fruitore fruitore) throws SerializerException {
		this.objToXml(file, Fruitore.class, fruitore, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>fruitore</var> of type {@link org.openspcoop2.core.registry.Fruitore}
	 * 
	 * @param file Xml file to serialize the object <var>fruitore</var>
	 * @param fruitore Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Fruitore fruitore,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Fruitore.class, fruitore, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>fruitore</var> of type {@link org.openspcoop2.core.registry.Fruitore}
	 * 
	 * @param out OutputStream to serialize the object <var>fruitore</var>
	 * @param fruitore Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Fruitore fruitore) throws SerializerException {
		this.objToXml(out, Fruitore.class, fruitore, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>fruitore</var> of type {@link org.openspcoop2.core.registry.Fruitore}
	 * 
	 * @param out OutputStream to serialize the object <var>fruitore</var>
	 * @param fruitore Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Fruitore fruitore,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Fruitore.class, fruitore, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>fruitore</var> of type {@link org.openspcoop2.core.registry.Fruitore}
	 * 
	 * @param fruitore Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Fruitore fruitore) throws SerializerException {
		return this.objToXml(Fruitore.class, fruitore, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>fruitore</var> of type {@link org.openspcoop2.core.registry.Fruitore}
	 * 
	 * @param fruitore Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Fruitore fruitore,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Fruitore.class, fruitore, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>fruitore</var> of type {@link org.openspcoop2.core.registry.Fruitore}
	 * 
	 * @param fruitore Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Fruitore fruitore) throws SerializerException {
		return this.objToXml(Fruitore.class, fruitore, false).toString();
	}
	/**
	 * Serialize to String the object <var>fruitore</var> of type {@link org.openspcoop2.core.registry.Fruitore}
	 * 
	 * @param fruitore Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Fruitore fruitore,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Fruitore.class, fruitore, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: Property
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>property</var> of type {@link org.openspcoop2.core.registry.Property}
	 * 
	 * @param fileName Xml file to serialize the object <var>property</var>
	 * @param property Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Property property) throws SerializerException {
		this.objToXml(fileName, Property.class, property, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>property</var> of type {@link org.openspcoop2.core.registry.Property}
	 * 
	 * @param fileName Xml file to serialize the object <var>property</var>
	 * @param property Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Property property,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Property.class, property, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>property</var> of type {@link org.openspcoop2.core.registry.Property}
	 * 
	 * @param file Xml file to serialize the object <var>property</var>
	 * @param property Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Property property) throws SerializerException {
		this.objToXml(file, Property.class, property, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>property</var> of type {@link org.openspcoop2.core.registry.Property}
	 * 
	 * @param file Xml file to serialize the object <var>property</var>
	 * @param property Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Property property,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Property.class, property, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>property</var> of type {@link org.openspcoop2.core.registry.Property}
	 * 
	 * @param out OutputStream to serialize the object <var>property</var>
	 * @param property Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Property property) throws SerializerException {
		this.objToXml(out, Property.class, property, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>property</var> of type {@link org.openspcoop2.core.registry.Property}
	 * 
	 * @param out OutputStream to serialize the object <var>property</var>
	 * @param property Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Property property,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Property.class, property, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>property</var> of type {@link org.openspcoop2.core.registry.Property}
	 * 
	 * @param property Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Property property) throws SerializerException {
		return this.objToXml(Property.class, property, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>property</var> of type {@link org.openspcoop2.core.registry.Property}
	 * 
	 * @param property Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Property property,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Property.class, property, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>property</var> of type {@link org.openspcoop2.core.registry.Property}
	 * 
	 * @param property Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Property property) throws SerializerException {
		return this.objToXml(Property.class, property, false).toString();
	}
	/**
	 * Serialize to String the object <var>property</var> of type {@link org.openspcoop2.core.registry.Property}
	 * 
	 * @param property Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Property property,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Property.class, property, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: ruolo
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>ruolo</var> of type {@link org.openspcoop2.core.registry.Ruolo}
	 * 
	 * @param fileName Xml file to serialize the object <var>ruolo</var>
	 * @param ruolo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Ruolo ruolo) throws SerializerException {
		this.objToXml(fileName, Ruolo.class, ruolo, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>ruolo</var> of type {@link org.openspcoop2.core.registry.Ruolo}
	 * 
	 * @param fileName Xml file to serialize the object <var>ruolo</var>
	 * @param ruolo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Ruolo ruolo,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Ruolo.class, ruolo, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>ruolo</var> of type {@link org.openspcoop2.core.registry.Ruolo}
	 * 
	 * @param file Xml file to serialize the object <var>ruolo</var>
	 * @param ruolo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Ruolo ruolo) throws SerializerException {
		this.objToXml(file, Ruolo.class, ruolo, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>ruolo</var> of type {@link org.openspcoop2.core.registry.Ruolo}
	 * 
	 * @param file Xml file to serialize the object <var>ruolo</var>
	 * @param ruolo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Ruolo ruolo,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Ruolo.class, ruolo, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>ruolo</var> of type {@link org.openspcoop2.core.registry.Ruolo}
	 * 
	 * @param out OutputStream to serialize the object <var>ruolo</var>
	 * @param ruolo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Ruolo ruolo) throws SerializerException {
		this.objToXml(out, Ruolo.class, ruolo, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>ruolo</var> of type {@link org.openspcoop2.core.registry.Ruolo}
	 * 
	 * @param out OutputStream to serialize the object <var>ruolo</var>
	 * @param ruolo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Ruolo ruolo,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Ruolo.class, ruolo, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>ruolo</var> of type {@link org.openspcoop2.core.registry.Ruolo}
	 * 
	 * @param ruolo Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Ruolo ruolo) throws SerializerException {
		return this.objToXml(Ruolo.class, ruolo, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>ruolo</var> of type {@link org.openspcoop2.core.registry.Ruolo}
	 * 
	 * @param ruolo Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Ruolo ruolo,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Ruolo.class, ruolo, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>ruolo</var> of type {@link org.openspcoop2.core.registry.Ruolo}
	 * 
	 * @param ruolo Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Ruolo ruolo) throws SerializerException {
		return this.objToXml(Ruolo.class, ruolo, false).toString();
	}
	/**
	 * Serialize to String the object <var>ruolo</var> of type {@link org.openspcoop2.core.registry.Ruolo}
	 * 
	 * @param ruolo Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Ruolo ruolo,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Ruolo.class, ruolo, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: accordo-cooperazione-partecipanti
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>accordoCooperazionePartecipanti</var> of type {@link org.openspcoop2.core.registry.AccordoCooperazionePartecipanti}
	 * 
	 * @param fileName Xml file to serialize the object <var>accordoCooperazionePartecipanti</var>
	 * @param accordoCooperazionePartecipanti Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,AccordoCooperazionePartecipanti accordoCooperazionePartecipanti) throws SerializerException {
		this.objToXml(fileName, AccordoCooperazionePartecipanti.class, accordoCooperazionePartecipanti, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>accordoCooperazionePartecipanti</var> of type {@link org.openspcoop2.core.registry.AccordoCooperazionePartecipanti}
	 * 
	 * @param fileName Xml file to serialize the object <var>accordoCooperazionePartecipanti</var>
	 * @param accordoCooperazionePartecipanti Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,AccordoCooperazionePartecipanti accordoCooperazionePartecipanti,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, AccordoCooperazionePartecipanti.class, accordoCooperazionePartecipanti, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>accordoCooperazionePartecipanti</var> of type {@link org.openspcoop2.core.registry.AccordoCooperazionePartecipanti}
	 * 
	 * @param file Xml file to serialize the object <var>accordoCooperazionePartecipanti</var>
	 * @param accordoCooperazionePartecipanti Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,AccordoCooperazionePartecipanti accordoCooperazionePartecipanti) throws SerializerException {
		this.objToXml(file, AccordoCooperazionePartecipanti.class, accordoCooperazionePartecipanti, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>accordoCooperazionePartecipanti</var> of type {@link org.openspcoop2.core.registry.AccordoCooperazionePartecipanti}
	 * 
	 * @param file Xml file to serialize the object <var>accordoCooperazionePartecipanti</var>
	 * @param accordoCooperazionePartecipanti Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,AccordoCooperazionePartecipanti accordoCooperazionePartecipanti,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, AccordoCooperazionePartecipanti.class, accordoCooperazionePartecipanti, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>accordoCooperazionePartecipanti</var> of type {@link org.openspcoop2.core.registry.AccordoCooperazionePartecipanti}
	 * 
	 * @param out OutputStream to serialize the object <var>accordoCooperazionePartecipanti</var>
	 * @param accordoCooperazionePartecipanti Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,AccordoCooperazionePartecipanti accordoCooperazionePartecipanti) throws SerializerException {
		this.objToXml(out, AccordoCooperazionePartecipanti.class, accordoCooperazionePartecipanti, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>accordoCooperazionePartecipanti</var> of type {@link org.openspcoop2.core.registry.AccordoCooperazionePartecipanti}
	 * 
	 * @param out OutputStream to serialize the object <var>accordoCooperazionePartecipanti</var>
	 * @param accordoCooperazionePartecipanti Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,AccordoCooperazionePartecipanti accordoCooperazionePartecipanti,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, AccordoCooperazionePartecipanti.class, accordoCooperazionePartecipanti, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>accordoCooperazionePartecipanti</var> of type {@link org.openspcoop2.core.registry.AccordoCooperazionePartecipanti}
	 * 
	 * @param accordoCooperazionePartecipanti Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(AccordoCooperazionePartecipanti accordoCooperazionePartecipanti) throws SerializerException {
		return this.objToXml(AccordoCooperazionePartecipanti.class, accordoCooperazionePartecipanti, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>accordoCooperazionePartecipanti</var> of type {@link org.openspcoop2.core.registry.AccordoCooperazionePartecipanti}
	 * 
	 * @param accordoCooperazionePartecipanti Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(AccordoCooperazionePartecipanti accordoCooperazionePartecipanti,boolean prettyPrint) throws SerializerException {
		return this.objToXml(AccordoCooperazionePartecipanti.class, accordoCooperazionePartecipanti, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>accordoCooperazionePartecipanti</var> of type {@link org.openspcoop2.core.registry.AccordoCooperazionePartecipanti}
	 * 
	 * @param accordoCooperazionePartecipanti Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(AccordoCooperazionePartecipanti accordoCooperazionePartecipanti) throws SerializerException {
		return this.objToXml(AccordoCooperazionePartecipanti.class, accordoCooperazionePartecipanti, false).toString();
	}
	/**
	 * Serialize to String the object <var>accordoCooperazionePartecipanti</var> of type {@link org.openspcoop2.core.registry.AccordoCooperazionePartecipanti}
	 * 
	 * @param accordoCooperazionePartecipanti Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(AccordoCooperazionePartecipanti accordoCooperazionePartecipanti,boolean prettyPrint) throws SerializerException {
		return this.objToXml(AccordoCooperazionePartecipanti.class, accordoCooperazionePartecipanti, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: proprieta
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>proprieta</var> of type {@link org.openspcoop2.core.registry.Proprieta}
	 * 
	 * @param fileName Xml file to serialize the object <var>proprieta</var>
	 * @param proprieta Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Proprieta proprieta) throws SerializerException {
		this.objToXml(fileName, Proprieta.class, proprieta, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>proprieta</var> of type {@link org.openspcoop2.core.registry.Proprieta}
	 * 
	 * @param fileName Xml file to serialize the object <var>proprieta</var>
	 * @param proprieta Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Proprieta proprieta,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Proprieta.class, proprieta, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>proprieta</var> of type {@link org.openspcoop2.core.registry.Proprieta}
	 * 
	 * @param file Xml file to serialize the object <var>proprieta</var>
	 * @param proprieta Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Proprieta proprieta) throws SerializerException {
		this.objToXml(file, Proprieta.class, proprieta, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>proprieta</var> of type {@link org.openspcoop2.core.registry.Proprieta}
	 * 
	 * @param file Xml file to serialize the object <var>proprieta</var>
	 * @param proprieta Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Proprieta proprieta,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Proprieta.class, proprieta, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>proprieta</var> of type {@link org.openspcoop2.core.registry.Proprieta}
	 * 
	 * @param out OutputStream to serialize the object <var>proprieta</var>
	 * @param proprieta Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Proprieta proprieta) throws SerializerException {
		this.objToXml(out, Proprieta.class, proprieta, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>proprieta</var> of type {@link org.openspcoop2.core.registry.Proprieta}
	 * 
	 * @param out OutputStream to serialize the object <var>proprieta</var>
	 * @param proprieta Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Proprieta proprieta,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Proprieta.class, proprieta, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>proprieta</var> of type {@link org.openspcoop2.core.registry.Proprieta}
	 * 
	 * @param proprieta Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Proprieta proprieta) throws SerializerException {
		return this.objToXml(Proprieta.class, proprieta, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>proprieta</var> of type {@link org.openspcoop2.core.registry.Proprieta}
	 * 
	 * @param proprieta Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Proprieta proprieta,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Proprieta.class, proprieta, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>proprieta</var> of type {@link org.openspcoop2.core.registry.Proprieta}
	 * 
	 * @param proprieta Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Proprieta proprieta) throws SerializerException {
		return this.objToXml(Proprieta.class, proprieta, false).toString();
	}
	/**
	 * Serialize to String the object <var>proprieta</var> of type {@link org.openspcoop2.core.registry.Proprieta}
	 * 
	 * @param proprieta Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Proprieta proprieta,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Proprieta.class, proprieta, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: azione
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>azione</var> of type {@link org.openspcoop2.core.registry.Azione}
	 * 
	 * @param fileName Xml file to serialize the object <var>azione</var>
	 * @param azione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Azione azione) throws SerializerException {
		this.objToXml(fileName, Azione.class, azione, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>azione</var> of type {@link org.openspcoop2.core.registry.Azione}
	 * 
	 * @param fileName Xml file to serialize the object <var>azione</var>
	 * @param azione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Azione azione,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Azione.class, azione, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>azione</var> of type {@link org.openspcoop2.core.registry.Azione}
	 * 
	 * @param file Xml file to serialize the object <var>azione</var>
	 * @param azione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Azione azione) throws SerializerException {
		this.objToXml(file, Azione.class, azione, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>azione</var> of type {@link org.openspcoop2.core.registry.Azione}
	 * 
	 * @param file Xml file to serialize the object <var>azione</var>
	 * @param azione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Azione azione,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Azione.class, azione, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>azione</var> of type {@link org.openspcoop2.core.registry.Azione}
	 * 
	 * @param out OutputStream to serialize the object <var>azione</var>
	 * @param azione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Azione azione) throws SerializerException {
		this.objToXml(out, Azione.class, azione, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>azione</var> of type {@link org.openspcoop2.core.registry.Azione}
	 * 
	 * @param out OutputStream to serialize the object <var>azione</var>
	 * @param azione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Azione azione,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Azione.class, azione, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>azione</var> of type {@link org.openspcoop2.core.registry.Azione}
	 * 
	 * @param azione Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Azione azione) throws SerializerException {
		return this.objToXml(Azione.class, azione, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>azione</var> of type {@link org.openspcoop2.core.registry.Azione}
	 * 
	 * @param azione Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Azione azione,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Azione.class, azione, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>azione</var> of type {@link org.openspcoop2.core.registry.Azione}
	 * 
	 * @param azione Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Azione azione) throws SerializerException {
		return this.objToXml(Azione.class, azione, false).toString();
	}
	/**
	 * Serialize to String the object <var>azione</var> of type {@link org.openspcoop2.core.registry.Azione}
	 * 
	 * @param azione Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Azione azione,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Azione.class, azione, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: connettore
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>connettore</var> of type {@link org.openspcoop2.core.registry.Connettore}
	 * 
	 * @param fileName Xml file to serialize the object <var>connettore</var>
	 * @param connettore Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Connettore connettore) throws SerializerException {
		this.objToXml(fileName, Connettore.class, connettore, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>connettore</var> of type {@link org.openspcoop2.core.registry.Connettore}
	 * 
	 * @param fileName Xml file to serialize the object <var>connettore</var>
	 * @param connettore Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Connettore connettore,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Connettore.class, connettore, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>connettore</var> of type {@link org.openspcoop2.core.registry.Connettore}
	 * 
	 * @param file Xml file to serialize the object <var>connettore</var>
	 * @param connettore Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Connettore connettore) throws SerializerException {
		this.objToXml(file, Connettore.class, connettore, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>connettore</var> of type {@link org.openspcoop2.core.registry.Connettore}
	 * 
	 * @param file Xml file to serialize the object <var>connettore</var>
	 * @param connettore Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Connettore connettore,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Connettore.class, connettore, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>connettore</var> of type {@link org.openspcoop2.core.registry.Connettore}
	 * 
	 * @param out OutputStream to serialize the object <var>connettore</var>
	 * @param connettore Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Connettore connettore) throws SerializerException {
		this.objToXml(out, Connettore.class, connettore, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>connettore</var> of type {@link org.openspcoop2.core.registry.Connettore}
	 * 
	 * @param out OutputStream to serialize the object <var>connettore</var>
	 * @param connettore Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Connettore connettore,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Connettore.class, connettore, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>connettore</var> of type {@link org.openspcoop2.core.registry.Connettore}
	 * 
	 * @param connettore Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Connettore connettore) throws SerializerException {
		return this.objToXml(Connettore.class, connettore, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>connettore</var> of type {@link org.openspcoop2.core.registry.Connettore}
	 * 
	 * @param connettore Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Connettore connettore,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Connettore.class, connettore, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>connettore</var> of type {@link org.openspcoop2.core.registry.Connettore}
	 * 
	 * @param connettore Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Connettore connettore) throws SerializerException {
		return this.objToXml(Connettore.class, connettore, false).toString();
	}
	/**
	 * Serialize to String the object <var>connettore</var> of type {@link org.openspcoop2.core.registry.Connettore}
	 * 
	 * @param connettore Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Connettore connettore,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Connettore.class, connettore, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: configurazione-servizio-azione
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>configurazioneServizioAzione</var> of type {@link org.openspcoop2.core.registry.ConfigurazioneServizioAzione}
	 * 
	 * @param fileName Xml file to serialize the object <var>configurazioneServizioAzione</var>
	 * @param configurazioneServizioAzione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ConfigurazioneServizioAzione configurazioneServizioAzione) throws SerializerException {
		this.objToXml(fileName, ConfigurazioneServizioAzione.class, configurazioneServizioAzione, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>configurazioneServizioAzione</var> of type {@link org.openspcoop2.core.registry.ConfigurazioneServizioAzione}
	 * 
	 * @param fileName Xml file to serialize the object <var>configurazioneServizioAzione</var>
	 * @param configurazioneServizioAzione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ConfigurazioneServizioAzione configurazioneServizioAzione,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, ConfigurazioneServizioAzione.class, configurazioneServizioAzione, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>configurazioneServizioAzione</var> of type {@link org.openspcoop2.core.registry.ConfigurazioneServizioAzione}
	 * 
	 * @param file Xml file to serialize the object <var>configurazioneServizioAzione</var>
	 * @param configurazioneServizioAzione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ConfigurazioneServizioAzione configurazioneServizioAzione) throws SerializerException {
		this.objToXml(file, ConfigurazioneServizioAzione.class, configurazioneServizioAzione, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>configurazioneServizioAzione</var> of type {@link org.openspcoop2.core.registry.ConfigurazioneServizioAzione}
	 * 
	 * @param file Xml file to serialize the object <var>configurazioneServizioAzione</var>
	 * @param configurazioneServizioAzione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ConfigurazioneServizioAzione configurazioneServizioAzione,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, ConfigurazioneServizioAzione.class, configurazioneServizioAzione, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>configurazioneServizioAzione</var> of type {@link org.openspcoop2.core.registry.ConfigurazioneServizioAzione}
	 * 
	 * @param out OutputStream to serialize the object <var>configurazioneServizioAzione</var>
	 * @param configurazioneServizioAzione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ConfigurazioneServizioAzione configurazioneServizioAzione) throws SerializerException {
		this.objToXml(out, ConfigurazioneServizioAzione.class, configurazioneServizioAzione, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>configurazioneServizioAzione</var> of type {@link org.openspcoop2.core.registry.ConfigurazioneServizioAzione}
	 * 
	 * @param out OutputStream to serialize the object <var>configurazioneServizioAzione</var>
	 * @param configurazioneServizioAzione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ConfigurazioneServizioAzione configurazioneServizioAzione,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, ConfigurazioneServizioAzione.class, configurazioneServizioAzione, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>configurazioneServizioAzione</var> of type {@link org.openspcoop2.core.registry.ConfigurazioneServizioAzione}
	 * 
	 * @param configurazioneServizioAzione Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ConfigurazioneServizioAzione configurazioneServizioAzione) throws SerializerException {
		return this.objToXml(ConfigurazioneServizioAzione.class, configurazioneServizioAzione, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>configurazioneServizioAzione</var> of type {@link org.openspcoop2.core.registry.ConfigurazioneServizioAzione}
	 * 
	 * @param configurazioneServizioAzione Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ConfigurazioneServizioAzione configurazioneServizioAzione,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ConfigurazioneServizioAzione.class, configurazioneServizioAzione, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>configurazioneServizioAzione</var> of type {@link org.openspcoop2.core.registry.ConfigurazioneServizioAzione}
	 * 
	 * @param configurazioneServizioAzione Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ConfigurazioneServizioAzione configurazioneServizioAzione) throws SerializerException {
		return this.objToXml(ConfigurazioneServizioAzione.class, configurazioneServizioAzione, false).toString();
	}
	/**
	 * Serialize to String the object <var>configurazioneServizioAzione</var> of type {@link org.openspcoop2.core.registry.ConfigurazioneServizioAzione}
	 * 
	 * @param configurazioneServizioAzione Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ConfigurazioneServizioAzione configurazioneServizioAzione,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ConfigurazioneServizioAzione.class, configurazioneServizioAzione, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: ruolo-soggetto
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>ruoloSoggetto</var> of type {@link org.openspcoop2.core.registry.RuoloSoggetto}
	 * 
	 * @param fileName Xml file to serialize the object <var>ruoloSoggetto</var>
	 * @param ruoloSoggetto Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,RuoloSoggetto ruoloSoggetto) throws SerializerException {
		this.objToXml(fileName, RuoloSoggetto.class, ruoloSoggetto, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>ruoloSoggetto</var> of type {@link org.openspcoop2.core.registry.RuoloSoggetto}
	 * 
	 * @param fileName Xml file to serialize the object <var>ruoloSoggetto</var>
	 * @param ruoloSoggetto Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,RuoloSoggetto ruoloSoggetto,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, RuoloSoggetto.class, ruoloSoggetto, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>ruoloSoggetto</var> of type {@link org.openspcoop2.core.registry.RuoloSoggetto}
	 * 
	 * @param file Xml file to serialize the object <var>ruoloSoggetto</var>
	 * @param ruoloSoggetto Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,RuoloSoggetto ruoloSoggetto) throws SerializerException {
		this.objToXml(file, RuoloSoggetto.class, ruoloSoggetto, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>ruoloSoggetto</var> of type {@link org.openspcoop2.core.registry.RuoloSoggetto}
	 * 
	 * @param file Xml file to serialize the object <var>ruoloSoggetto</var>
	 * @param ruoloSoggetto Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,RuoloSoggetto ruoloSoggetto,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, RuoloSoggetto.class, ruoloSoggetto, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>ruoloSoggetto</var> of type {@link org.openspcoop2.core.registry.RuoloSoggetto}
	 * 
	 * @param out OutputStream to serialize the object <var>ruoloSoggetto</var>
	 * @param ruoloSoggetto Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,RuoloSoggetto ruoloSoggetto) throws SerializerException {
		this.objToXml(out, RuoloSoggetto.class, ruoloSoggetto, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>ruoloSoggetto</var> of type {@link org.openspcoop2.core.registry.RuoloSoggetto}
	 * 
	 * @param out OutputStream to serialize the object <var>ruoloSoggetto</var>
	 * @param ruoloSoggetto Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,RuoloSoggetto ruoloSoggetto,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, RuoloSoggetto.class, ruoloSoggetto, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>ruoloSoggetto</var> of type {@link org.openspcoop2.core.registry.RuoloSoggetto}
	 * 
	 * @param ruoloSoggetto Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(RuoloSoggetto ruoloSoggetto) throws SerializerException {
		return this.objToXml(RuoloSoggetto.class, ruoloSoggetto, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>ruoloSoggetto</var> of type {@link org.openspcoop2.core.registry.RuoloSoggetto}
	 * 
	 * @param ruoloSoggetto Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(RuoloSoggetto ruoloSoggetto,boolean prettyPrint) throws SerializerException {
		return this.objToXml(RuoloSoggetto.class, ruoloSoggetto, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>ruoloSoggetto</var> of type {@link org.openspcoop2.core.registry.RuoloSoggetto}
	 * 
	 * @param ruoloSoggetto Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(RuoloSoggetto ruoloSoggetto) throws SerializerException {
		return this.objToXml(RuoloSoggetto.class, ruoloSoggetto, false).toString();
	}
	/**
	 * Serialize to String the object <var>ruoloSoggetto</var> of type {@link org.openspcoop2.core.registry.RuoloSoggetto}
	 * 
	 * @param ruoloSoggetto Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(RuoloSoggetto ruoloSoggetto,boolean prettyPrint) throws SerializerException {
		return this.objToXml(RuoloSoggetto.class, ruoloSoggetto, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: scope
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>scope</var> of type {@link org.openspcoop2.core.registry.Scope}
	 * 
	 * @param fileName Xml file to serialize the object <var>scope</var>
	 * @param scope Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Scope scope) throws SerializerException {
		this.objToXml(fileName, Scope.class, scope, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>scope</var> of type {@link org.openspcoop2.core.registry.Scope}
	 * 
	 * @param fileName Xml file to serialize the object <var>scope</var>
	 * @param scope Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Scope scope,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Scope.class, scope, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>scope</var> of type {@link org.openspcoop2.core.registry.Scope}
	 * 
	 * @param file Xml file to serialize the object <var>scope</var>
	 * @param scope Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Scope scope) throws SerializerException {
		this.objToXml(file, Scope.class, scope, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>scope</var> of type {@link org.openspcoop2.core.registry.Scope}
	 * 
	 * @param file Xml file to serialize the object <var>scope</var>
	 * @param scope Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Scope scope,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Scope.class, scope, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>scope</var> of type {@link org.openspcoop2.core.registry.Scope}
	 * 
	 * @param out OutputStream to serialize the object <var>scope</var>
	 * @param scope Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Scope scope) throws SerializerException {
		this.objToXml(out, Scope.class, scope, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>scope</var> of type {@link org.openspcoop2.core.registry.Scope}
	 * 
	 * @param out OutputStream to serialize the object <var>scope</var>
	 * @param scope Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Scope scope,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Scope.class, scope, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>scope</var> of type {@link org.openspcoop2.core.registry.Scope}
	 * 
	 * @param scope Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Scope scope) throws SerializerException {
		return this.objToXml(Scope.class, scope, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>scope</var> of type {@link org.openspcoop2.core.registry.Scope}
	 * 
	 * @param scope Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Scope scope,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Scope.class, scope, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>scope</var> of type {@link org.openspcoop2.core.registry.Scope}
	 * 
	 * @param scope Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Scope scope) throws SerializerException {
		return this.objToXml(Scope.class, scope, false).toString();
	}
	/**
	 * Serialize to String the object <var>scope</var> of type {@link org.openspcoop2.core.registry.Scope}
	 * 
	 * @param scope Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Scope scope,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Scope.class, scope, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: resource-representation-xml
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>resourceRepresentationXml</var> of type {@link org.openspcoop2.core.registry.ResourceRepresentationXml}
	 * 
	 * @param fileName Xml file to serialize the object <var>resourceRepresentationXml</var>
	 * @param resourceRepresentationXml Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ResourceRepresentationXml resourceRepresentationXml) throws SerializerException {
		this.objToXml(fileName, ResourceRepresentationXml.class, resourceRepresentationXml, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>resourceRepresentationXml</var> of type {@link org.openspcoop2.core.registry.ResourceRepresentationXml}
	 * 
	 * @param fileName Xml file to serialize the object <var>resourceRepresentationXml</var>
	 * @param resourceRepresentationXml Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ResourceRepresentationXml resourceRepresentationXml,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, ResourceRepresentationXml.class, resourceRepresentationXml, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>resourceRepresentationXml</var> of type {@link org.openspcoop2.core.registry.ResourceRepresentationXml}
	 * 
	 * @param file Xml file to serialize the object <var>resourceRepresentationXml</var>
	 * @param resourceRepresentationXml Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ResourceRepresentationXml resourceRepresentationXml) throws SerializerException {
		this.objToXml(file, ResourceRepresentationXml.class, resourceRepresentationXml, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>resourceRepresentationXml</var> of type {@link org.openspcoop2.core.registry.ResourceRepresentationXml}
	 * 
	 * @param file Xml file to serialize the object <var>resourceRepresentationXml</var>
	 * @param resourceRepresentationXml Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ResourceRepresentationXml resourceRepresentationXml,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, ResourceRepresentationXml.class, resourceRepresentationXml, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>resourceRepresentationXml</var> of type {@link org.openspcoop2.core.registry.ResourceRepresentationXml}
	 * 
	 * @param out OutputStream to serialize the object <var>resourceRepresentationXml</var>
	 * @param resourceRepresentationXml Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ResourceRepresentationXml resourceRepresentationXml) throws SerializerException {
		this.objToXml(out, ResourceRepresentationXml.class, resourceRepresentationXml, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>resourceRepresentationXml</var> of type {@link org.openspcoop2.core.registry.ResourceRepresentationXml}
	 * 
	 * @param out OutputStream to serialize the object <var>resourceRepresentationXml</var>
	 * @param resourceRepresentationXml Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ResourceRepresentationXml resourceRepresentationXml,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, ResourceRepresentationXml.class, resourceRepresentationXml, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>resourceRepresentationXml</var> of type {@link org.openspcoop2.core.registry.ResourceRepresentationXml}
	 * 
	 * @param resourceRepresentationXml Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ResourceRepresentationXml resourceRepresentationXml) throws SerializerException {
		return this.objToXml(ResourceRepresentationXml.class, resourceRepresentationXml, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>resourceRepresentationXml</var> of type {@link org.openspcoop2.core.registry.ResourceRepresentationXml}
	 * 
	 * @param resourceRepresentationXml Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ResourceRepresentationXml resourceRepresentationXml,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ResourceRepresentationXml.class, resourceRepresentationXml, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>resourceRepresentationXml</var> of type {@link org.openspcoop2.core.registry.ResourceRepresentationXml}
	 * 
	 * @param resourceRepresentationXml Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ResourceRepresentationXml resourceRepresentationXml) throws SerializerException {
		return this.objToXml(ResourceRepresentationXml.class, resourceRepresentationXml, false).toString();
	}
	/**
	 * Serialize to String the object <var>resourceRepresentationXml</var> of type {@link org.openspcoop2.core.registry.ResourceRepresentationXml}
	 * 
	 * @param resourceRepresentationXml Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ResourceRepresentationXml resourceRepresentationXml,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ResourceRepresentationXml.class, resourceRepresentationXml, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: accordo-servizio-parte-comune-servizio-composto-servizio-componente
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>accordoServizioParteComuneServizioCompostoServizioComponente</var> of type {@link org.openspcoop2.core.registry.AccordoServizioParteComuneServizioCompostoServizioComponente}
	 * 
	 * @param fileName Xml file to serialize the object <var>accordoServizioParteComuneServizioCompostoServizioComponente</var>
	 * @param accordoServizioParteComuneServizioCompostoServizioComponente Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,AccordoServizioParteComuneServizioCompostoServizioComponente accordoServizioParteComuneServizioCompostoServizioComponente) throws SerializerException {
		this.objToXml(fileName, AccordoServizioParteComuneServizioCompostoServizioComponente.class, accordoServizioParteComuneServizioCompostoServizioComponente, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>accordoServizioParteComuneServizioCompostoServizioComponente</var> of type {@link org.openspcoop2.core.registry.AccordoServizioParteComuneServizioCompostoServizioComponente}
	 * 
	 * @param fileName Xml file to serialize the object <var>accordoServizioParteComuneServizioCompostoServizioComponente</var>
	 * @param accordoServizioParteComuneServizioCompostoServizioComponente Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,AccordoServizioParteComuneServizioCompostoServizioComponente accordoServizioParteComuneServizioCompostoServizioComponente,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, AccordoServizioParteComuneServizioCompostoServizioComponente.class, accordoServizioParteComuneServizioCompostoServizioComponente, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>accordoServizioParteComuneServizioCompostoServizioComponente</var> of type {@link org.openspcoop2.core.registry.AccordoServizioParteComuneServizioCompostoServizioComponente}
	 * 
	 * @param file Xml file to serialize the object <var>accordoServizioParteComuneServizioCompostoServizioComponente</var>
	 * @param accordoServizioParteComuneServizioCompostoServizioComponente Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,AccordoServizioParteComuneServizioCompostoServizioComponente accordoServizioParteComuneServizioCompostoServizioComponente) throws SerializerException {
		this.objToXml(file, AccordoServizioParteComuneServizioCompostoServizioComponente.class, accordoServizioParteComuneServizioCompostoServizioComponente, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>accordoServizioParteComuneServizioCompostoServizioComponente</var> of type {@link org.openspcoop2.core.registry.AccordoServizioParteComuneServizioCompostoServizioComponente}
	 * 
	 * @param file Xml file to serialize the object <var>accordoServizioParteComuneServizioCompostoServizioComponente</var>
	 * @param accordoServizioParteComuneServizioCompostoServizioComponente Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,AccordoServizioParteComuneServizioCompostoServizioComponente accordoServizioParteComuneServizioCompostoServizioComponente,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, AccordoServizioParteComuneServizioCompostoServizioComponente.class, accordoServizioParteComuneServizioCompostoServizioComponente, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>accordoServizioParteComuneServizioCompostoServizioComponente</var> of type {@link org.openspcoop2.core.registry.AccordoServizioParteComuneServizioCompostoServizioComponente}
	 * 
	 * @param out OutputStream to serialize the object <var>accordoServizioParteComuneServizioCompostoServizioComponente</var>
	 * @param accordoServizioParteComuneServizioCompostoServizioComponente Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,AccordoServizioParteComuneServizioCompostoServizioComponente accordoServizioParteComuneServizioCompostoServizioComponente) throws SerializerException {
		this.objToXml(out, AccordoServizioParteComuneServizioCompostoServizioComponente.class, accordoServizioParteComuneServizioCompostoServizioComponente, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>accordoServizioParteComuneServizioCompostoServizioComponente</var> of type {@link org.openspcoop2.core.registry.AccordoServizioParteComuneServizioCompostoServizioComponente}
	 * 
	 * @param out OutputStream to serialize the object <var>accordoServizioParteComuneServizioCompostoServizioComponente</var>
	 * @param accordoServizioParteComuneServizioCompostoServizioComponente Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,AccordoServizioParteComuneServizioCompostoServizioComponente accordoServizioParteComuneServizioCompostoServizioComponente,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, AccordoServizioParteComuneServizioCompostoServizioComponente.class, accordoServizioParteComuneServizioCompostoServizioComponente, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>accordoServizioParteComuneServizioCompostoServizioComponente</var> of type {@link org.openspcoop2.core.registry.AccordoServizioParteComuneServizioCompostoServizioComponente}
	 * 
	 * @param accordoServizioParteComuneServizioCompostoServizioComponente Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(AccordoServizioParteComuneServizioCompostoServizioComponente accordoServizioParteComuneServizioCompostoServizioComponente) throws SerializerException {
		return this.objToXml(AccordoServizioParteComuneServizioCompostoServizioComponente.class, accordoServizioParteComuneServizioCompostoServizioComponente, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>accordoServizioParteComuneServizioCompostoServizioComponente</var> of type {@link org.openspcoop2.core.registry.AccordoServizioParteComuneServizioCompostoServizioComponente}
	 * 
	 * @param accordoServizioParteComuneServizioCompostoServizioComponente Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(AccordoServizioParteComuneServizioCompostoServizioComponente accordoServizioParteComuneServizioCompostoServizioComponente,boolean prettyPrint) throws SerializerException {
		return this.objToXml(AccordoServizioParteComuneServizioCompostoServizioComponente.class, accordoServizioParteComuneServizioCompostoServizioComponente, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>accordoServizioParteComuneServizioCompostoServizioComponente</var> of type {@link org.openspcoop2.core.registry.AccordoServizioParteComuneServizioCompostoServizioComponente}
	 * 
	 * @param accordoServizioParteComuneServizioCompostoServizioComponente Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(AccordoServizioParteComuneServizioCompostoServizioComponente accordoServizioParteComuneServizioCompostoServizioComponente) throws SerializerException {
		return this.objToXml(AccordoServizioParteComuneServizioCompostoServizioComponente.class, accordoServizioParteComuneServizioCompostoServizioComponente, false).toString();
	}
	/**
	 * Serialize to String the object <var>accordoServizioParteComuneServizioCompostoServizioComponente</var> of type {@link org.openspcoop2.core.registry.AccordoServizioParteComuneServizioCompostoServizioComponente}
	 * 
	 * @param accordoServizioParteComuneServizioCompostoServizioComponente Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(AccordoServizioParteComuneServizioCompostoServizioComponente accordoServizioParteComuneServizioCompostoServizioComponente,boolean prettyPrint) throws SerializerException {
		return this.objToXml(AccordoServizioParteComuneServizioCompostoServizioComponente.class, accordoServizioParteComuneServizioCompostoServizioComponente, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: accordo-servizio-parte-comune-servizio-composto
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>accordoServizioParteComuneServizioComposto</var> of type {@link org.openspcoop2.core.registry.AccordoServizioParteComuneServizioComposto}
	 * 
	 * @param fileName Xml file to serialize the object <var>accordoServizioParteComuneServizioComposto</var>
	 * @param accordoServizioParteComuneServizioComposto Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,AccordoServizioParteComuneServizioComposto accordoServizioParteComuneServizioComposto) throws SerializerException {
		this.objToXml(fileName, AccordoServizioParteComuneServizioComposto.class, accordoServizioParteComuneServizioComposto, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>accordoServizioParteComuneServizioComposto</var> of type {@link org.openspcoop2.core.registry.AccordoServizioParteComuneServizioComposto}
	 * 
	 * @param fileName Xml file to serialize the object <var>accordoServizioParteComuneServizioComposto</var>
	 * @param accordoServizioParteComuneServizioComposto Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,AccordoServizioParteComuneServizioComposto accordoServizioParteComuneServizioComposto,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, AccordoServizioParteComuneServizioComposto.class, accordoServizioParteComuneServizioComposto, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>accordoServizioParteComuneServizioComposto</var> of type {@link org.openspcoop2.core.registry.AccordoServizioParteComuneServizioComposto}
	 * 
	 * @param file Xml file to serialize the object <var>accordoServizioParteComuneServizioComposto</var>
	 * @param accordoServizioParteComuneServizioComposto Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,AccordoServizioParteComuneServizioComposto accordoServizioParteComuneServizioComposto) throws SerializerException {
		this.objToXml(file, AccordoServizioParteComuneServizioComposto.class, accordoServizioParteComuneServizioComposto, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>accordoServizioParteComuneServizioComposto</var> of type {@link org.openspcoop2.core.registry.AccordoServizioParteComuneServizioComposto}
	 * 
	 * @param file Xml file to serialize the object <var>accordoServizioParteComuneServizioComposto</var>
	 * @param accordoServizioParteComuneServizioComposto Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,AccordoServizioParteComuneServizioComposto accordoServizioParteComuneServizioComposto,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, AccordoServizioParteComuneServizioComposto.class, accordoServizioParteComuneServizioComposto, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>accordoServizioParteComuneServizioComposto</var> of type {@link org.openspcoop2.core.registry.AccordoServizioParteComuneServizioComposto}
	 * 
	 * @param out OutputStream to serialize the object <var>accordoServizioParteComuneServizioComposto</var>
	 * @param accordoServizioParteComuneServizioComposto Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,AccordoServizioParteComuneServizioComposto accordoServizioParteComuneServizioComposto) throws SerializerException {
		this.objToXml(out, AccordoServizioParteComuneServizioComposto.class, accordoServizioParteComuneServizioComposto, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>accordoServizioParteComuneServizioComposto</var> of type {@link org.openspcoop2.core.registry.AccordoServizioParteComuneServizioComposto}
	 * 
	 * @param out OutputStream to serialize the object <var>accordoServizioParteComuneServizioComposto</var>
	 * @param accordoServizioParteComuneServizioComposto Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,AccordoServizioParteComuneServizioComposto accordoServizioParteComuneServizioComposto,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, AccordoServizioParteComuneServizioComposto.class, accordoServizioParteComuneServizioComposto, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>accordoServizioParteComuneServizioComposto</var> of type {@link org.openspcoop2.core.registry.AccordoServizioParteComuneServizioComposto}
	 * 
	 * @param accordoServizioParteComuneServizioComposto Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(AccordoServizioParteComuneServizioComposto accordoServizioParteComuneServizioComposto) throws SerializerException {
		return this.objToXml(AccordoServizioParteComuneServizioComposto.class, accordoServizioParteComuneServizioComposto, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>accordoServizioParteComuneServizioComposto</var> of type {@link org.openspcoop2.core.registry.AccordoServizioParteComuneServizioComposto}
	 * 
	 * @param accordoServizioParteComuneServizioComposto Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(AccordoServizioParteComuneServizioComposto accordoServizioParteComuneServizioComposto,boolean prettyPrint) throws SerializerException {
		return this.objToXml(AccordoServizioParteComuneServizioComposto.class, accordoServizioParteComuneServizioComposto, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>accordoServizioParteComuneServizioComposto</var> of type {@link org.openspcoop2.core.registry.AccordoServizioParteComuneServizioComposto}
	 * 
	 * @param accordoServizioParteComuneServizioComposto Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(AccordoServizioParteComuneServizioComposto accordoServizioParteComuneServizioComposto) throws SerializerException {
		return this.objToXml(AccordoServizioParteComuneServizioComposto.class, accordoServizioParteComuneServizioComposto, false).toString();
	}
	/**
	 * Serialize to String the object <var>accordoServizioParteComuneServizioComposto</var> of type {@link org.openspcoop2.core.registry.AccordoServizioParteComuneServizioComposto}
	 * 
	 * @param accordoServizioParteComuneServizioComposto Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(AccordoServizioParteComuneServizioComposto accordoServizioParteComuneServizioComposto,boolean prettyPrint) throws SerializerException {
		return this.objToXml(AccordoServizioParteComuneServizioComposto.class, accordoServizioParteComuneServizioComposto, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: port-type
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>portType</var> of type {@link org.openspcoop2.core.registry.PortType}
	 * 
	 * @param fileName Xml file to serialize the object <var>portType</var>
	 * @param portType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,PortType portType) throws SerializerException {
		this.objToXml(fileName, PortType.class, portType, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>portType</var> of type {@link org.openspcoop2.core.registry.PortType}
	 * 
	 * @param fileName Xml file to serialize the object <var>portType</var>
	 * @param portType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,PortType portType,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, PortType.class, portType, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>portType</var> of type {@link org.openspcoop2.core.registry.PortType}
	 * 
	 * @param file Xml file to serialize the object <var>portType</var>
	 * @param portType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,PortType portType) throws SerializerException {
		this.objToXml(file, PortType.class, portType, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>portType</var> of type {@link org.openspcoop2.core.registry.PortType}
	 * 
	 * @param file Xml file to serialize the object <var>portType</var>
	 * @param portType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,PortType portType,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, PortType.class, portType, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>portType</var> of type {@link org.openspcoop2.core.registry.PortType}
	 * 
	 * @param out OutputStream to serialize the object <var>portType</var>
	 * @param portType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,PortType portType) throws SerializerException {
		this.objToXml(out, PortType.class, portType, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>portType</var> of type {@link org.openspcoop2.core.registry.PortType}
	 * 
	 * @param out OutputStream to serialize the object <var>portType</var>
	 * @param portType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,PortType portType,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, PortType.class, portType, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>portType</var> of type {@link org.openspcoop2.core.registry.PortType}
	 * 
	 * @param portType Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(PortType portType) throws SerializerException {
		return this.objToXml(PortType.class, portType, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>portType</var> of type {@link org.openspcoop2.core.registry.PortType}
	 * 
	 * @param portType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(PortType portType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(PortType.class, portType, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>portType</var> of type {@link org.openspcoop2.core.registry.PortType}
	 * 
	 * @param portType Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(PortType portType) throws SerializerException {
		return this.objToXml(PortType.class, portType, false).toString();
	}
	/**
	 * Serialize to String the object <var>portType</var> of type {@link org.openspcoop2.core.registry.PortType}
	 * 
	 * @param portType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(PortType portType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(PortType.class, portType, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: id-accordo-cooperazione
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>idAccordoCooperazione</var> of type {@link org.openspcoop2.core.registry.IdAccordoCooperazione}
	 * 
	 * @param fileName Xml file to serialize the object <var>idAccordoCooperazione</var>
	 * @param idAccordoCooperazione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,IdAccordoCooperazione idAccordoCooperazione) throws SerializerException {
		this.objToXml(fileName, IdAccordoCooperazione.class, idAccordoCooperazione, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>idAccordoCooperazione</var> of type {@link org.openspcoop2.core.registry.IdAccordoCooperazione}
	 * 
	 * @param fileName Xml file to serialize the object <var>idAccordoCooperazione</var>
	 * @param idAccordoCooperazione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,IdAccordoCooperazione idAccordoCooperazione,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, IdAccordoCooperazione.class, idAccordoCooperazione, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>idAccordoCooperazione</var> of type {@link org.openspcoop2.core.registry.IdAccordoCooperazione}
	 * 
	 * @param file Xml file to serialize the object <var>idAccordoCooperazione</var>
	 * @param idAccordoCooperazione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,IdAccordoCooperazione idAccordoCooperazione) throws SerializerException {
		this.objToXml(file, IdAccordoCooperazione.class, idAccordoCooperazione, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>idAccordoCooperazione</var> of type {@link org.openspcoop2.core.registry.IdAccordoCooperazione}
	 * 
	 * @param file Xml file to serialize the object <var>idAccordoCooperazione</var>
	 * @param idAccordoCooperazione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,IdAccordoCooperazione idAccordoCooperazione,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, IdAccordoCooperazione.class, idAccordoCooperazione, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>idAccordoCooperazione</var> of type {@link org.openspcoop2.core.registry.IdAccordoCooperazione}
	 * 
	 * @param out OutputStream to serialize the object <var>idAccordoCooperazione</var>
	 * @param idAccordoCooperazione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,IdAccordoCooperazione idAccordoCooperazione) throws SerializerException {
		this.objToXml(out, IdAccordoCooperazione.class, idAccordoCooperazione, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>idAccordoCooperazione</var> of type {@link org.openspcoop2.core.registry.IdAccordoCooperazione}
	 * 
	 * @param out OutputStream to serialize the object <var>idAccordoCooperazione</var>
	 * @param idAccordoCooperazione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,IdAccordoCooperazione idAccordoCooperazione,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, IdAccordoCooperazione.class, idAccordoCooperazione, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>idAccordoCooperazione</var> of type {@link org.openspcoop2.core.registry.IdAccordoCooperazione}
	 * 
	 * @param idAccordoCooperazione Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(IdAccordoCooperazione idAccordoCooperazione) throws SerializerException {
		return this.objToXml(IdAccordoCooperazione.class, idAccordoCooperazione, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>idAccordoCooperazione</var> of type {@link org.openspcoop2.core.registry.IdAccordoCooperazione}
	 * 
	 * @param idAccordoCooperazione Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(IdAccordoCooperazione idAccordoCooperazione,boolean prettyPrint) throws SerializerException {
		return this.objToXml(IdAccordoCooperazione.class, idAccordoCooperazione, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>idAccordoCooperazione</var> of type {@link org.openspcoop2.core.registry.IdAccordoCooperazione}
	 * 
	 * @param idAccordoCooperazione Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(IdAccordoCooperazione idAccordoCooperazione) throws SerializerException {
		return this.objToXml(IdAccordoCooperazione.class, idAccordoCooperazione, false).toString();
	}
	/**
	 * Serialize to String the object <var>idAccordoCooperazione</var> of type {@link org.openspcoop2.core.registry.IdAccordoCooperazione}
	 * 
	 * @param idAccordoCooperazione Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(IdAccordoCooperazione idAccordoCooperazione,boolean prettyPrint) throws SerializerException {
		return this.objToXml(IdAccordoCooperazione.class, idAccordoCooperazione, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: accordo-cooperazione
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>accordoCooperazione</var> of type {@link org.openspcoop2.core.registry.AccordoCooperazione}
	 * 
	 * @param fileName Xml file to serialize the object <var>accordoCooperazione</var>
	 * @param accordoCooperazione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,AccordoCooperazione accordoCooperazione) throws SerializerException {
		this.objToXml(fileName, AccordoCooperazione.class, accordoCooperazione, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>accordoCooperazione</var> of type {@link org.openspcoop2.core.registry.AccordoCooperazione}
	 * 
	 * @param fileName Xml file to serialize the object <var>accordoCooperazione</var>
	 * @param accordoCooperazione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,AccordoCooperazione accordoCooperazione,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, AccordoCooperazione.class, accordoCooperazione, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>accordoCooperazione</var> of type {@link org.openspcoop2.core.registry.AccordoCooperazione}
	 * 
	 * @param file Xml file to serialize the object <var>accordoCooperazione</var>
	 * @param accordoCooperazione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,AccordoCooperazione accordoCooperazione) throws SerializerException {
		this.objToXml(file, AccordoCooperazione.class, accordoCooperazione, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>accordoCooperazione</var> of type {@link org.openspcoop2.core.registry.AccordoCooperazione}
	 * 
	 * @param file Xml file to serialize the object <var>accordoCooperazione</var>
	 * @param accordoCooperazione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,AccordoCooperazione accordoCooperazione,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, AccordoCooperazione.class, accordoCooperazione, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>accordoCooperazione</var> of type {@link org.openspcoop2.core.registry.AccordoCooperazione}
	 * 
	 * @param out OutputStream to serialize the object <var>accordoCooperazione</var>
	 * @param accordoCooperazione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,AccordoCooperazione accordoCooperazione) throws SerializerException {
		this.objToXml(out, AccordoCooperazione.class, accordoCooperazione, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>accordoCooperazione</var> of type {@link org.openspcoop2.core.registry.AccordoCooperazione}
	 * 
	 * @param out OutputStream to serialize the object <var>accordoCooperazione</var>
	 * @param accordoCooperazione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,AccordoCooperazione accordoCooperazione,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, AccordoCooperazione.class, accordoCooperazione, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>accordoCooperazione</var> of type {@link org.openspcoop2.core.registry.AccordoCooperazione}
	 * 
	 * @param accordoCooperazione Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(AccordoCooperazione accordoCooperazione) throws SerializerException {
		return this.objToXml(AccordoCooperazione.class, accordoCooperazione, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>accordoCooperazione</var> of type {@link org.openspcoop2.core.registry.AccordoCooperazione}
	 * 
	 * @param accordoCooperazione Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(AccordoCooperazione accordoCooperazione,boolean prettyPrint) throws SerializerException {
		return this.objToXml(AccordoCooperazione.class, accordoCooperazione, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>accordoCooperazione</var> of type {@link org.openspcoop2.core.registry.AccordoCooperazione}
	 * 
	 * @param accordoCooperazione Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(AccordoCooperazione accordoCooperazione) throws SerializerException {
		return this.objToXml(AccordoCooperazione.class, accordoCooperazione, false).toString();
	}
	/**
	 * Serialize to String the object <var>accordoCooperazione</var> of type {@link org.openspcoop2.core.registry.AccordoCooperazione}
	 * 
	 * @param accordoCooperazione Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(AccordoCooperazione accordoCooperazione,boolean prettyPrint) throws SerializerException {
		return this.objToXml(AccordoCooperazione.class, accordoCooperazione, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: credenziali-soggetto
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>credenzialiSoggetto</var> of type {@link org.openspcoop2.core.registry.CredenzialiSoggetto}
	 * 
	 * @param fileName Xml file to serialize the object <var>credenzialiSoggetto</var>
	 * @param credenzialiSoggetto Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,CredenzialiSoggetto credenzialiSoggetto) throws SerializerException {
		this.objToXml(fileName, CredenzialiSoggetto.class, credenzialiSoggetto, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>credenzialiSoggetto</var> of type {@link org.openspcoop2.core.registry.CredenzialiSoggetto}
	 * 
	 * @param fileName Xml file to serialize the object <var>credenzialiSoggetto</var>
	 * @param credenzialiSoggetto Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,CredenzialiSoggetto credenzialiSoggetto,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, CredenzialiSoggetto.class, credenzialiSoggetto, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>credenzialiSoggetto</var> of type {@link org.openspcoop2.core.registry.CredenzialiSoggetto}
	 * 
	 * @param file Xml file to serialize the object <var>credenzialiSoggetto</var>
	 * @param credenzialiSoggetto Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,CredenzialiSoggetto credenzialiSoggetto) throws SerializerException {
		this.objToXml(file, CredenzialiSoggetto.class, credenzialiSoggetto, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>credenzialiSoggetto</var> of type {@link org.openspcoop2.core.registry.CredenzialiSoggetto}
	 * 
	 * @param file Xml file to serialize the object <var>credenzialiSoggetto</var>
	 * @param credenzialiSoggetto Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,CredenzialiSoggetto credenzialiSoggetto,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, CredenzialiSoggetto.class, credenzialiSoggetto, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>credenzialiSoggetto</var> of type {@link org.openspcoop2.core.registry.CredenzialiSoggetto}
	 * 
	 * @param out OutputStream to serialize the object <var>credenzialiSoggetto</var>
	 * @param credenzialiSoggetto Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,CredenzialiSoggetto credenzialiSoggetto) throws SerializerException {
		this.objToXml(out, CredenzialiSoggetto.class, credenzialiSoggetto, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>credenzialiSoggetto</var> of type {@link org.openspcoop2.core.registry.CredenzialiSoggetto}
	 * 
	 * @param out OutputStream to serialize the object <var>credenzialiSoggetto</var>
	 * @param credenzialiSoggetto Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,CredenzialiSoggetto credenzialiSoggetto,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, CredenzialiSoggetto.class, credenzialiSoggetto, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>credenzialiSoggetto</var> of type {@link org.openspcoop2.core.registry.CredenzialiSoggetto}
	 * 
	 * @param credenzialiSoggetto Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(CredenzialiSoggetto credenzialiSoggetto) throws SerializerException {
		return this.objToXml(CredenzialiSoggetto.class, credenzialiSoggetto, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>credenzialiSoggetto</var> of type {@link org.openspcoop2.core.registry.CredenzialiSoggetto}
	 * 
	 * @param credenzialiSoggetto Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(CredenzialiSoggetto credenzialiSoggetto,boolean prettyPrint) throws SerializerException {
		return this.objToXml(CredenzialiSoggetto.class, credenzialiSoggetto, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>credenzialiSoggetto</var> of type {@link org.openspcoop2.core.registry.CredenzialiSoggetto}
	 * 
	 * @param credenzialiSoggetto Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(CredenzialiSoggetto credenzialiSoggetto) throws SerializerException {
		return this.objToXml(CredenzialiSoggetto.class, credenzialiSoggetto, false).toString();
	}
	/**
	 * Serialize to String the object <var>credenzialiSoggetto</var> of type {@link org.openspcoop2.core.registry.CredenzialiSoggetto}
	 * 
	 * @param credenzialiSoggetto Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(CredenzialiSoggetto credenzialiSoggetto,boolean prettyPrint) throws SerializerException {
		return this.objToXml(CredenzialiSoggetto.class, credenzialiSoggetto, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: registro-servizi
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>registroServizi</var> of type {@link org.openspcoop2.core.registry.RegistroServizi}
	 * 
	 * @param fileName Xml file to serialize the object <var>registroServizi</var>
	 * @param registroServizi Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,RegistroServizi registroServizi) throws SerializerException {
		this.objToXml(fileName, RegistroServizi.class, registroServizi, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>registroServizi</var> of type {@link org.openspcoop2.core.registry.RegistroServizi}
	 * 
	 * @param fileName Xml file to serialize the object <var>registroServizi</var>
	 * @param registroServizi Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,RegistroServizi registroServizi,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, RegistroServizi.class, registroServizi, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>registroServizi</var> of type {@link org.openspcoop2.core.registry.RegistroServizi}
	 * 
	 * @param file Xml file to serialize the object <var>registroServizi</var>
	 * @param registroServizi Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,RegistroServizi registroServizi) throws SerializerException {
		this.objToXml(file, RegistroServizi.class, registroServizi, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>registroServizi</var> of type {@link org.openspcoop2.core.registry.RegistroServizi}
	 * 
	 * @param file Xml file to serialize the object <var>registroServizi</var>
	 * @param registroServizi Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,RegistroServizi registroServizi,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, RegistroServizi.class, registroServizi, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>registroServizi</var> of type {@link org.openspcoop2.core.registry.RegistroServizi}
	 * 
	 * @param out OutputStream to serialize the object <var>registroServizi</var>
	 * @param registroServizi Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,RegistroServizi registroServizi) throws SerializerException {
		this.objToXml(out, RegistroServizi.class, registroServizi, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>registroServizi</var> of type {@link org.openspcoop2.core.registry.RegistroServizi}
	 * 
	 * @param out OutputStream to serialize the object <var>registroServizi</var>
	 * @param registroServizi Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,RegistroServizi registroServizi,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, RegistroServizi.class, registroServizi, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>registroServizi</var> of type {@link org.openspcoop2.core.registry.RegistroServizi}
	 * 
	 * @param registroServizi Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(RegistroServizi registroServizi) throws SerializerException {
		return this.objToXml(RegistroServizi.class, registroServizi, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>registroServizi</var> of type {@link org.openspcoop2.core.registry.RegistroServizi}
	 * 
	 * @param registroServizi Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(RegistroServizi registroServizi,boolean prettyPrint) throws SerializerException {
		return this.objToXml(RegistroServizi.class, registroServizi, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>registroServizi</var> of type {@link org.openspcoop2.core.registry.RegistroServizi}
	 * 
	 * @param registroServizi Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(RegistroServizi registroServizi) throws SerializerException {
		return this.objToXml(RegistroServizi.class, registroServizi, false).toString();
	}
	/**
	 * Serialize to String the object <var>registroServizi</var> of type {@link org.openspcoop2.core.registry.RegistroServizi}
	 * 
	 * @param registroServizi Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(RegistroServizi registroServizi,boolean prettyPrint) throws SerializerException {
		return this.objToXml(RegistroServizi.class, registroServizi, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: accordo-servizio-parte-comune
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>accordoServizioParteComune</var> of type {@link org.openspcoop2.core.registry.AccordoServizioParteComune}
	 * 
	 * @param fileName Xml file to serialize the object <var>accordoServizioParteComune</var>
	 * @param accordoServizioParteComune Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,AccordoServizioParteComune accordoServizioParteComune) throws SerializerException {
		this.objToXml(fileName, AccordoServizioParteComune.class, accordoServizioParteComune, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>accordoServizioParteComune</var> of type {@link org.openspcoop2.core.registry.AccordoServizioParteComune}
	 * 
	 * @param fileName Xml file to serialize the object <var>accordoServizioParteComune</var>
	 * @param accordoServizioParteComune Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,AccordoServizioParteComune accordoServizioParteComune,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, AccordoServizioParteComune.class, accordoServizioParteComune, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>accordoServizioParteComune</var> of type {@link org.openspcoop2.core.registry.AccordoServizioParteComune}
	 * 
	 * @param file Xml file to serialize the object <var>accordoServizioParteComune</var>
	 * @param accordoServizioParteComune Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,AccordoServizioParteComune accordoServizioParteComune) throws SerializerException {
		this.objToXml(file, AccordoServizioParteComune.class, accordoServizioParteComune, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>accordoServizioParteComune</var> of type {@link org.openspcoop2.core.registry.AccordoServizioParteComune}
	 * 
	 * @param file Xml file to serialize the object <var>accordoServizioParteComune</var>
	 * @param accordoServizioParteComune Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,AccordoServizioParteComune accordoServizioParteComune,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, AccordoServizioParteComune.class, accordoServizioParteComune, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>accordoServizioParteComune</var> of type {@link org.openspcoop2.core.registry.AccordoServizioParteComune}
	 * 
	 * @param out OutputStream to serialize the object <var>accordoServizioParteComune</var>
	 * @param accordoServizioParteComune Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,AccordoServizioParteComune accordoServizioParteComune) throws SerializerException {
		this.objToXml(out, AccordoServizioParteComune.class, accordoServizioParteComune, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>accordoServizioParteComune</var> of type {@link org.openspcoop2.core.registry.AccordoServizioParteComune}
	 * 
	 * @param out OutputStream to serialize the object <var>accordoServizioParteComune</var>
	 * @param accordoServizioParteComune Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,AccordoServizioParteComune accordoServizioParteComune,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, AccordoServizioParteComune.class, accordoServizioParteComune, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>accordoServizioParteComune</var> of type {@link org.openspcoop2.core.registry.AccordoServizioParteComune}
	 * 
	 * @param accordoServizioParteComune Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(AccordoServizioParteComune accordoServizioParteComune) throws SerializerException {
		return this.objToXml(AccordoServizioParteComune.class, accordoServizioParteComune, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>accordoServizioParteComune</var> of type {@link org.openspcoop2.core.registry.AccordoServizioParteComune}
	 * 
	 * @param accordoServizioParteComune Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(AccordoServizioParteComune accordoServizioParteComune,boolean prettyPrint) throws SerializerException {
		return this.objToXml(AccordoServizioParteComune.class, accordoServizioParteComune, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>accordoServizioParteComune</var> of type {@link org.openspcoop2.core.registry.AccordoServizioParteComune}
	 * 
	 * @param accordoServizioParteComune Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(AccordoServizioParteComune accordoServizioParteComune) throws SerializerException {
		return this.objToXml(AccordoServizioParteComune.class, accordoServizioParteComune, false).toString();
	}
	/**
	 * Serialize to String the object <var>accordoServizioParteComune</var> of type {@link org.openspcoop2.core.registry.AccordoServizioParteComune}
	 * 
	 * @param accordoServizioParteComune Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(AccordoServizioParteComune accordoServizioParteComune,boolean prettyPrint) throws SerializerException {
		return this.objToXml(AccordoServizioParteComune.class, accordoServizioParteComune, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: porta-dominio
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>portaDominio</var> of type {@link org.openspcoop2.core.registry.PortaDominio}
	 * 
	 * @param fileName Xml file to serialize the object <var>portaDominio</var>
	 * @param portaDominio Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,PortaDominio portaDominio) throws SerializerException {
		this.objToXml(fileName, PortaDominio.class, portaDominio, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>portaDominio</var> of type {@link org.openspcoop2.core.registry.PortaDominio}
	 * 
	 * @param fileName Xml file to serialize the object <var>portaDominio</var>
	 * @param portaDominio Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,PortaDominio portaDominio,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, PortaDominio.class, portaDominio, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>portaDominio</var> of type {@link org.openspcoop2.core.registry.PortaDominio}
	 * 
	 * @param file Xml file to serialize the object <var>portaDominio</var>
	 * @param portaDominio Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,PortaDominio portaDominio) throws SerializerException {
		this.objToXml(file, PortaDominio.class, portaDominio, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>portaDominio</var> of type {@link org.openspcoop2.core.registry.PortaDominio}
	 * 
	 * @param file Xml file to serialize the object <var>portaDominio</var>
	 * @param portaDominio Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,PortaDominio portaDominio,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, PortaDominio.class, portaDominio, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>portaDominio</var> of type {@link org.openspcoop2.core.registry.PortaDominio}
	 * 
	 * @param out OutputStream to serialize the object <var>portaDominio</var>
	 * @param portaDominio Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,PortaDominio portaDominio) throws SerializerException {
		this.objToXml(out, PortaDominio.class, portaDominio, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>portaDominio</var> of type {@link org.openspcoop2.core.registry.PortaDominio}
	 * 
	 * @param out OutputStream to serialize the object <var>portaDominio</var>
	 * @param portaDominio Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,PortaDominio portaDominio,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, PortaDominio.class, portaDominio, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>portaDominio</var> of type {@link org.openspcoop2.core.registry.PortaDominio}
	 * 
	 * @param portaDominio Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(PortaDominio portaDominio) throws SerializerException {
		return this.objToXml(PortaDominio.class, portaDominio, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>portaDominio</var> of type {@link org.openspcoop2.core.registry.PortaDominio}
	 * 
	 * @param portaDominio Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(PortaDominio portaDominio,boolean prettyPrint) throws SerializerException {
		return this.objToXml(PortaDominio.class, portaDominio, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>portaDominio</var> of type {@link org.openspcoop2.core.registry.PortaDominio}
	 * 
	 * @param portaDominio Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(PortaDominio portaDominio) throws SerializerException {
		return this.objToXml(PortaDominio.class, portaDominio, false).toString();
	}
	/**
	 * Serialize to String the object <var>portaDominio</var> of type {@link org.openspcoop2.core.registry.PortaDominio}
	 * 
	 * @param portaDominio Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(PortaDominio portaDominio,boolean prettyPrint) throws SerializerException {
		return this.objToXml(PortaDominio.class, portaDominio, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: gruppo
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>gruppo</var> of type {@link org.openspcoop2.core.registry.Gruppo}
	 * 
	 * @param fileName Xml file to serialize the object <var>gruppo</var>
	 * @param gruppo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Gruppo gruppo) throws SerializerException {
		this.objToXml(fileName, Gruppo.class, gruppo, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>gruppo</var> of type {@link org.openspcoop2.core.registry.Gruppo}
	 * 
	 * @param fileName Xml file to serialize the object <var>gruppo</var>
	 * @param gruppo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Gruppo gruppo,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Gruppo.class, gruppo, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>gruppo</var> of type {@link org.openspcoop2.core.registry.Gruppo}
	 * 
	 * @param file Xml file to serialize the object <var>gruppo</var>
	 * @param gruppo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Gruppo gruppo) throws SerializerException {
		this.objToXml(file, Gruppo.class, gruppo, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>gruppo</var> of type {@link org.openspcoop2.core.registry.Gruppo}
	 * 
	 * @param file Xml file to serialize the object <var>gruppo</var>
	 * @param gruppo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Gruppo gruppo,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Gruppo.class, gruppo, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>gruppo</var> of type {@link org.openspcoop2.core.registry.Gruppo}
	 * 
	 * @param out OutputStream to serialize the object <var>gruppo</var>
	 * @param gruppo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Gruppo gruppo) throws SerializerException {
		this.objToXml(out, Gruppo.class, gruppo, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>gruppo</var> of type {@link org.openspcoop2.core.registry.Gruppo}
	 * 
	 * @param out OutputStream to serialize the object <var>gruppo</var>
	 * @param gruppo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Gruppo gruppo,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Gruppo.class, gruppo, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>gruppo</var> of type {@link org.openspcoop2.core.registry.Gruppo}
	 * 
	 * @param gruppo Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Gruppo gruppo) throws SerializerException {
		return this.objToXml(Gruppo.class, gruppo, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>gruppo</var> of type {@link org.openspcoop2.core.registry.Gruppo}
	 * 
	 * @param gruppo Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Gruppo gruppo,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Gruppo.class, gruppo, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>gruppo</var> of type {@link org.openspcoop2.core.registry.Gruppo}
	 * 
	 * @param gruppo Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Gruppo gruppo) throws SerializerException {
		return this.objToXml(Gruppo.class, gruppo, false).toString();
	}
	/**
	 * Serialize to String the object <var>gruppo</var> of type {@link org.openspcoop2.core.registry.Gruppo}
	 * 
	 * @param gruppo Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Gruppo gruppo,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Gruppo.class, gruppo, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: soggetto
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>soggetto</var> of type {@link org.openspcoop2.core.registry.Soggetto}
	 * 
	 * @param fileName Xml file to serialize the object <var>soggetto</var>
	 * @param soggetto Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Soggetto soggetto) throws SerializerException {
		this.objToXml(fileName, Soggetto.class, soggetto, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>soggetto</var> of type {@link org.openspcoop2.core.registry.Soggetto}
	 * 
	 * @param fileName Xml file to serialize the object <var>soggetto</var>
	 * @param soggetto Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Soggetto soggetto,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Soggetto.class, soggetto, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>soggetto</var> of type {@link org.openspcoop2.core.registry.Soggetto}
	 * 
	 * @param file Xml file to serialize the object <var>soggetto</var>
	 * @param soggetto Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Soggetto soggetto) throws SerializerException {
		this.objToXml(file, Soggetto.class, soggetto, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>soggetto</var> of type {@link org.openspcoop2.core.registry.Soggetto}
	 * 
	 * @param file Xml file to serialize the object <var>soggetto</var>
	 * @param soggetto Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Soggetto soggetto,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Soggetto.class, soggetto, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>soggetto</var> of type {@link org.openspcoop2.core.registry.Soggetto}
	 * 
	 * @param out OutputStream to serialize the object <var>soggetto</var>
	 * @param soggetto Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Soggetto soggetto) throws SerializerException {
		this.objToXml(out, Soggetto.class, soggetto, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>soggetto</var> of type {@link org.openspcoop2.core.registry.Soggetto}
	 * 
	 * @param out OutputStream to serialize the object <var>soggetto</var>
	 * @param soggetto Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Soggetto soggetto,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Soggetto.class, soggetto, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>soggetto</var> of type {@link org.openspcoop2.core.registry.Soggetto}
	 * 
	 * @param soggetto Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Soggetto soggetto) throws SerializerException {
		return this.objToXml(Soggetto.class, soggetto, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>soggetto</var> of type {@link org.openspcoop2.core.registry.Soggetto}
	 * 
	 * @param soggetto Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Soggetto soggetto,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Soggetto.class, soggetto, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>soggetto</var> of type {@link org.openspcoop2.core.registry.Soggetto}
	 * 
	 * @param soggetto Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Soggetto soggetto) throws SerializerException {
		return this.objToXml(Soggetto.class, soggetto, false).toString();
	}
	/**
	 * Serialize to String the object <var>soggetto</var> of type {@link org.openspcoop2.core.registry.Soggetto}
	 * 
	 * @param soggetto Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Soggetto soggetto,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Soggetto.class, soggetto, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: id-gruppo
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>idGruppo</var> of type {@link org.openspcoop2.core.registry.IdGruppo}
	 * 
	 * @param fileName Xml file to serialize the object <var>idGruppo</var>
	 * @param idGruppo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,IdGruppo idGruppo) throws SerializerException {
		this.objToXml(fileName, IdGruppo.class, idGruppo, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>idGruppo</var> of type {@link org.openspcoop2.core.registry.IdGruppo}
	 * 
	 * @param fileName Xml file to serialize the object <var>idGruppo</var>
	 * @param idGruppo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,IdGruppo idGruppo,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, IdGruppo.class, idGruppo, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>idGruppo</var> of type {@link org.openspcoop2.core.registry.IdGruppo}
	 * 
	 * @param file Xml file to serialize the object <var>idGruppo</var>
	 * @param idGruppo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,IdGruppo idGruppo) throws SerializerException {
		this.objToXml(file, IdGruppo.class, idGruppo, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>idGruppo</var> of type {@link org.openspcoop2.core.registry.IdGruppo}
	 * 
	 * @param file Xml file to serialize the object <var>idGruppo</var>
	 * @param idGruppo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,IdGruppo idGruppo,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, IdGruppo.class, idGruppo, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>idGruppo</var> of type {@link org.openspcoop2.core.registry.IdGruppo}
	 * 
	 * @param out OutputStream to serialize the object <var>idGruppo</var>
	 * @param idGruppo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,IdGruppo idGruppo) throws SerializerException {
		this.objToXml(out, IdGruppo.class, idGruppo, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>idGruppo</var> of type {@link org.openspcoop2.core.registry.IdGruppo}
	 * 
	 * @param out OutputStream to serialize the object <var>idGruppo</var>
	 * @param idGruppo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,IdGruppo idGruppo,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, IdGruppo.class, idGruppo, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>idGruppo</var> of type {@link org.openspcoop2.core.registry.IdGruppo}
	 * 
	 * @param idGruppo Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(IdGruppo idGruppo) throws SerializerException {
		return this.objToXml(IdGruppo.class, idGruppo, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>idGruppo</var> of type {@link org.openspcoop2.core.registry.IdGruppo}
	 * 
	 * @param idGruppo Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(IdGruppo idGruppo,boolean prettyPrint) throws SerializerException {
		return this.objToXml(IdGruppo.class, idGruppo, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>idGruppo</var> of type {@link org.openspcoop2.core.registry.IdGruppo}
	 * 
	 * @param idGruppo Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(IdGruppo idGruppo) throws SerializerException {
		return this.objToXml(IdGruppo.class, idGruppo, false).toString();
	}
	/**
	 * Serialize to String the object <var>idGruppo</var> of type {@link org.openspcoop2.core.registry.IdGruppo}
	 * 
	 * @param idGruppo Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(IdGruppo idGruppo,boolean prettyPrint) throws SerializerException {
		return this.objToXml(IdGruppo.class, idGruppo, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: ruoli-soggetto
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>ruoliSoggetto</var> of type {@link org.openspcoop2.core.registry.RuoliSoggetto}
	 * 
	 * @param fileName Xml file to serialize the object <var>ruoliSoggetto</var>
	 * @param ruoliSoggetto Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,RuoliSoggetto ruoliSoggetto) throws SerializerException {
		this.objToXml(fileName, RuoliSoggetto.class, ruoliSoggetto, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>ruoliSoggetto</var> of type {@link org.openspcoop2.core.registry.RuoliSoggetto}
	 * 
	 * @param fileName Xml file to serialize the object <var>ruoliSoggetto</var>
	 * @param ruoliSoggetto Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,RuoliSoggetto ruoliSoggetto,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, RuoliSoggetto.class, ruoliSoggetto, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>ruoliSoggetto</var> of type {@link org.openspcoop2.core.registry.RuoliSoggetto}
	 * 
	 * @param file Xml file to serialize the object <var>ruoliSoggetto</var>
	 * @param ruoliSoggetto Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,RuoliSoggetto ruoliSoggetto) throws SerializerException {
		this.objToXml(file, RuoliSoggetto.class, ruoliSoggetto, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>ruoliSoggetto</var> of type {@link org.openspcoop2.core.registry.RuoliSoggetto}
	 * 
	 * @param file Xml file to serialize the object <var>ruoliSoggetto</var>
	 * @param ruoliSoggetto Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,RuoliSoggetto ruoliSoggetto,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, RuoliSoggetto.class, ruoliSoggetto, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>ruoliSoggetto</var> of type {@link org.openspcoop2.core.registry.RuoliSoggetto}
	 * 
	 * @param out OutputStream to serialize the object <var>ruoliSoggetto</var>
	 * @param ruoliSoggetto Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,RuoliSoggetto ruoliSoggetto) throws SerializerException {
		this.objToXml(out, RuoliSoggetto.class, ruoliSoggetto, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>ruoliSoggetto</var> of type {@link org.openspcoop2.core.registry.RuoliSoggetto}
	 * 
	 * @param out OutputStream to serialize the object <var>ruoliSoggetto</var>
	 * @param ruoliSoggetto Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,RuoliSoggetto ruoliSoggetto,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, RuoliSoggetto.class, ruoliSoggetto, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>ruoliSoggetto</var> of type {@link org.openspcoop2.core.registry.RuoliSoggetto}
	 * 
	 * @param ruoliSoggetto Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(RuoliSoggetto ruoliSoggetto) throws SerializerException {
		return this.objToXml(RuoliSoggetto.class, ruoliSoggetto, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>ruoliSoggetto</var> of type {@link org.openspcoop2.core.registry.RuoliSoggetto}
	 * 
	 * @param ruoliSoggetto Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(RuoliSoggetto ruoliSoggetto,boolean prettyPrint) throws SerializerException {
		return this.objToXml(RuoliSoggetto.class, ruoliSoggetto, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>ruoliSoggetto</var> of type {@link org.openspcoop2.core.registry.RuoliSoggetto}
	 * 
	 * @param ruoliSoggetto Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(RuoliSoggetto ruoliSoggetto) throws SerializerException {
		return this.objToXml(RuoliSoggetto.class, ruoliSoggetto, false).toString();
	}
	/**
	 * Serialize to String the object <var>ruoliSoggetto</var> of type {@link org.openspcoop2.core.registry.RuoliSoggetto}
	 * 
	 * @param ruoliSoggetto Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(RuoliSoggetto ruoliSoggetto,boolean prettyPrint) throws SerializerException {
		return this.objToXml(RuoliSoggetto.class, ruoliSoggetto, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: id-accordo-servizio-parte-comune
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>idAccordoServizioParteComune</var> of type {@link org.openspcoop2.core.registry.IdAccordoServizioParteComune}
	 * 
	 * @param fileName Xml file to serialize the object <var>idAccordoServizioParteComune</var>
	 * @param idAccordoServizioParteComune Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,IdAccordoServizioParteComune idAccordoServizioParteComune) throws SerializerException {
		this.objToXml(fileName, IdAccordoServizioParteComune.class, idAccordoServizioParteComune, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>idAccordoServizioParteComune</var> of type {@link org.openspcoop2.core.registry.IdAccordoServizioParteComune}
	 * 
	 * @param fileName Xml file to serialize the object <var>idAccordoServizioParteComune</var>
	 * @param idAccordoServizioParteComune Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,IdAccordoServizioParteComune idAccordoServizioParteComune,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, IdAccordoServizioParteComune.class, idAccordoServizioParteComune, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>idAccordoServizioParteComune</var> of type {@link org.openspcoop2.core.registry.IdAccordoServizioParteComune}
	 * 
	 * @param file Xml file to serialize the object <var>idAccordoServizioParteComune</var>
	 * @param idAccordoServizioParteComune Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,IdAccordoServizioParteComune idAccordoServizioParteComune) throws SerializerException {
		this.objToXml(file, IdAccordoServizioParteComune.class, idAccordoServizioParteComune, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>idAccordoServizioParteComune</var> of type {@link org.openspcoop2.core.registry.IdAccordoServizioParteComune}
	 * 
	 * @param file Xml file to serialize the object <var>idAccordoServizioParteComune</var>
	 * @param idAccordoServizioParteComune Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,IdAccordoServizioParteComune idAccordoServizioParteComune,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, IdAccordoServizioParteComune.class, idAccordoServizioParteComune, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>idAccordoServizioParteComune</var> of type {@link org.openspcoop2.core.registry.IdAccordoServizioParteComune}
	 * 
	 * @param out OutputStream to serialize the object <var>idAccordoServizioParteComune</var>
	 * @param idAccordoServizioParteComune Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,IdAccordoServizioParteComune idAccordoServizioParteComune) throws SerializerException {
		this.objToXml(out, IdAccordoServizioParteComune.class, idAccordoServizioParteComune, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>idAccordoServizioParteComune</var> of type {@link org.openspcoop2.core.registry.IdAccordoServizioParteComune}
	 * 
	 * @param out OutputStream to serialize the object <var>idAccordoServizioParteComune</var>
	 * @param idAccordoServizioParteComune Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,IdAccordoServizioParteComune idAccordoServizioParteComune,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, IdAccordoServizioParteComune.class, idAccordoServizioParteComune, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>idAccordoServizioParteComune</var> of type {@link org.openspcoop2.core.registry.IdAccordoServizioParteComune}
	 * 
	 * @param idAccordoServizioParteComune Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(IdAccordoServizioParteComune idAccordoServizioParteComune) throws SerializerException {
		return this.objToXml(IdAccordoServizioParteComune.class, idAccordoServizioParteComune, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>idAccordoServizioParteComune</var> of type {@link org.openspcoop2.core.registry.IdAccordoServizioParteComune}
	 * 
	 * @param idAccordoServizioParteComune Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(IdAccordoServizioParteComune idAccordoServizioParteComune,boolean prettyPrint) throws SerializerException {
		return this.objToXml(IdAccordoServizioParteComune.class, idAccordoServizioParteComune, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>idAccordoServizioParteComune</var> of type {@link org.openspcoop2.core.registry.IdAccordoServizioParteComune}
	 * 
	 * @param idAccordoServizioParteComune Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(IdAccordoServizioParteComune idAccordoServizioParteComune) throws SerializerException {
		return this.objToXml(IdAccordoServizioParteComune.class, idAccordoServizioParteComune, false).toString();
	}
	/**
	 * Serialize to String the object <var>idAccordoServizioParteComune</var> of type {@link org.openspcoop2.core.registry.IdAccordoServizioParteComune}
	 * 
	 * @param idAccordoServizioParteComune Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(IdAccordoServizioParteComune idAccordoServizioParteComune,boolean prettyPrint) throws SerializerException {
		return this.objToXml(IdAccordoServizioParteComune.class, idAccordoServizioParteComune, prettyPrint).toString();
	}
	
	
	

}
