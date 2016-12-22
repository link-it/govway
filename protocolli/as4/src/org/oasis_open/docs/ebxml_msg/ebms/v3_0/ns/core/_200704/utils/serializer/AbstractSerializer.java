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
package org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.utils.serializer;

import org.openspcoop2.generic_project.exception.SerializerException;
import org.openspcoop2.utils.beans.WriteToSerializerType;
import org.openspcoop2.utils.xml.JaxbUtils;

import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Property;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessageProperties;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessageInfo;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.UserMessage;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartyInfo;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.CollaborationInfo;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PayloadInfo;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.SignalMessage;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Messaging;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartyId;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.To;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartProperties;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.AgreementRef;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Service;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartInfo;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.From;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Schema;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Error;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Description;

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
	 Object: Property
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>property</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Property}
	 * 
	 * @param fileName Xml file to serialize the object <var>property</var>
	 * @param property Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Property property) throws SerializerException {
		this.objToXml(fileName, Property.class, property, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>property</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Property}
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
	 * Serialize to file system in <var>file</var> the object <var>property</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Property}
	 * 
	 * @param file Xml file to serialize the object <var>property</var>
	 * @param property Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Property property) throws SerializerException {
		this.objToXml(file, Property.class, property, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>property</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Property}
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
	 * Serialize to output stream <var>out</var> the object <var>property</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Property}
	 * 
	 * @param out OutputStream to serialize the object <var>property</var>
	 * @param property Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Property property) throws SerializerException {
		this.objToXml(out, Property.class, property, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>property</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Property}
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
	 * Serialize to byte array the object <var>property</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Property}
	 * 
	 * @param property Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Property property) throws SerializerException {
		return this.objToXml(Property.class, property, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>property</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Property}
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
	 * Serialize to String the object <var>property</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Property}
	 * 
	 * @param property Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Property property) throws SerializerException {
		return this.objToXml(Property.class, property, false).toString();
	}
	/**
	 * Serialize to String the object <var>property</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Property}
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
	 Object: MessageProperties
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>messageProperties</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessageProperties}
	 * 
	 * @param fileName Xml file to serialize the object <var>messageProperties</var>
	 * @param messageProperties Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,MessageProperties messageProperties) throws SerializerException {
		this.objToXml(fileName, MessageProperties.class, messageProperties, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>messageProperties</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessageProperties}
	 * 
	 * @param fileName Xml file to serialize the object <var>messageProperties</var>
	 * @param messageProperties Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,MessageProperties messageProperties,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, MessageProperties.class, messageProperties, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>messageProperties</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessageProperties}
	 * 
	 * @param file Xml file to serialize the object <var>messageProperties</var>
	 * @param messageProperties Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,MessageProperties messageProperties) throws SerializerException {
		this.objToXml(file, MessageProperties.class, messageProperties, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>messageProperties</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessageProperties}
	 * 
	 * @param file Xml file to serialize the object <var>messageProperties</var>
	 * @param messageProperties Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,MessageProperties messageProperties,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, MessageProperties.class, messageProperties, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>messageProperties</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessageProperties}
	 * 
	 * @param out OutputStream to serialize the object <var>messageProperties</var>
	 * @param messageProperties Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,MessageProperties messageProperties) throws SerializerException {
		this.objToXml(out, MessageProperties.class, messageProperties, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>messageProperties</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessageProperties}
	 * 
	 * @param out OutputStream to serialize the object <var>messageProperties</var>
	 * @param messageProperties Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,MessageProperties messageProperties,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, MessageProperties.class, messageProperties, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>messageProperties</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessageProperties}
	 * 
	 * @param messageProperties Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(MessageProperties messageProperties) throws SerializerException {
		return this.objToXml(MessageProperties.class, messageProperties, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>messageProperties</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessageProperties}
	 * 
	 * @param messageProperties Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(MessageProperties messageProperties,boolean prettyPrint) throws SerializerException {
		return this.objToXml(MessageProperties.class, messageProperties, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>messageProperties</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessageProperties}
	 * 
	 * @param messageProperties Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(MessageProperties messageProperties) throws SerializerException {
		return this.objToXml(MessageProperties.class, messageProperties, false).toString();
	}
	/**
	 * Serialize to String the object <var>messageProperties</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessageProperties}
	 * 
	 * @param messageProperties Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(MessageProperties messageProperties,boolean prettyPrint) throws SerializerException {
		return this.objToXml(MessageProperties.class, messageProperties, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: MessageInfo
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>messageInfo</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessageInfo}
	 * 
	 * @param fileName Xml file to serialize the object <var>messageInfo</var>
	 * @param messageInfo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,MessageInfo messageInfo) throws SerializerException {
		this.objToXml(fileName, MessageInfo.class, messageInfo, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>messageInfo</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessageInfo}
	 * 
	 * @param fileName Xml file to serialize the object <var>messageInfo</var>
	 * @param messageInfo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,MessageInfo messageInfo,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, MessageInfo.class, messageInfo, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>messageInfo</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessageInfo}
	 * 
	 * @param file Xml file to serialize the object <var>messageInfo</var>
	 * @param messageInfo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,MessageInfo messageInfo) throws SerializerException {
		this.objToXml(file, MessageInfo.class, messageInfo, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>messageInfo</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessageInfo}
	 * 
	 * @param file Xml file to serialize the object <var>messageInfo</var>
	 * @param messageInfo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,MessageInfo messageInfo,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, MessageInfo.class, messageInfo, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>messageInfo</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessageInfo}
	 * 
	 * @param out OutputStream to serialize the object <var>messageInfo</var>
	 * @param messageInfo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,MessageInfo messageInfo) throws SerializerException {
		this.objToXml(out, MessageInfo.class, messageInfo, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>messageInfo</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessageInfo}
	 * 
	 * @param out OutputStream to serialize the object <var>messageInfo</var>
	 * @param messageInfo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,MessageInfo messageInfo,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, MessageInfo.class, messageInfo, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>messageInfo</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessageInfo}
	 * 
	 * @param messageInfo Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(MessageInfo messageInfo) throws SerializerException {
		return this.objToXml(MessageInfo.class, messageInfo, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>messageInfo</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessageInfo}
	 * 
	 * @param messageInfo Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(MessageInfo messageInfo,boolean prettyPrint) throws SerializerException {
		return this.objToXml(MessageInfo.class, messageInfo, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>messageInfo</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessageInfo}
	 * 
	 * @param messageInfo Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(MessageInfo messageInfo) throws SerializerException {
		return this.objToXml(MessageInfo.class, messageInfo, false).toString();
	}
	/**
	 * Serialize to String the object <var>messageInfo</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessageInfo}
	 * 
	 * @param messageInfo Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(MessageInfo messageInfo,boolean prettyPrint) throws SerializerException {
		return this.objToXml(MessageInfo.class, messageInfo, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: UserMessage
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>userMessage</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.UserMessage}
	 * 
	 * @param fileName Xml file to serialize the object <var>userMessage</var>
	 * @param userMessage Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,UserMessage userMessage) throws SerializerException {
		this.objToXml(fileName, UserMessage.class, userMessage, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>userMessage</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.UserMessage}
	 * 
	 * @param fileName Xml file to serialize the object <var>userMessage</var>
	 * @param userMessage Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,UserMessage userMessage,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, UserMessage.class, userMessage, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>userMessage</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.UserMessage}
	 * 
	 * @param file Xml file to serialize the object <var>userMessage</var>
	 * @param userMessage Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,UserMessage userMessage) throws SerializerException {
		this.objToXml(file, UserMessage.class, userMessage, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>userMessage</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.UserMessage}
	 * 
	 * @param file Xml file to serialize the object <var>userMessage</var>
	 * @param userMessage Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,UserMessage userMessage,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, UserMessage.class, userMessage, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>userMessage</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.UserMessage}
	 * 
	 * @param out OutputStream to serialize the object <var>userMessage</var>
	 * @param userMessage Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,UserMessage userMessage) throws SerializerException {
		this.objToXml(out, UserMessage.class, userMessage, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>userMessage</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.UserMessage}
	 * 
	 * @param out OutputStream to serialize the object <var>userMessage</var>
	 * @param userMessage Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,UserMessage userMessage,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, UserMessage.class, userMessage, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>userMessage</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.UserMessage}
	 * 
	 * @param userMessage Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(UserMessage userMessage) throws SerializerException {
		return this.objToXml(UserMessage.class, userMessage, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>userMessage</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.UserMessage}
	 * 
	 * @param userMessage Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(UserMessage userMessage,boolean prettyPrint) throws SerializerException {
		return this.objToXml(UserMessage.class, userMessage, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>userMessage</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.UserMessage}
	 * 
	 * @param userMessage Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(UserMessage userMessage) throws SerializerException {
		return this.objToXml(UserMessage.class, userMessage, false).toString();
	}
	/**
	 * Serialize to String the object <var>userMessage</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.UserMessage}
	 * 
	 * @param userMessage Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(UserMessage userMessage,boolean prettyPrint) throws SerializerException {
		return this.objToXml(UserMessage.class, userMessage, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: PartyInfo
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>partyInfo</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartyInfo}
	 * 
	 * @param fileName Xml file to serialize the object <var>partyInfo</var>
	 * @param partyInfo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,PartyInfo partyInfo) throws SerializerException {
		this.objToXml(fileName, PartyInfo.class, partyInfo, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>partyInfo</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartyInfo}
	 * 
	 * @param fileName Xml file to serialize the object <var>partyInfo</var>
	 * @param partyInfo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,PartyInfo partyInfo,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, PartyInfo.class, partyInfo, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>partyInfo</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartyInfo}
	 * 
	 * @param file Xml file to serialize the object <var>partyInfo</var>
	 * @param partyInfo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,PartyInfo partyInfo) throws SerializerException {
		this.objToXml(file, PartyInfo.class, partyInfo, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>partyInfo</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartyInfo}
	 * 
	 * @param file Xml file to serialize the object <var>partyInfo</var>
	 * @param partyInfo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,PartyInfo partyInfo,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, PartyInfo.class, partyInfo, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>partyInfo</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartyInfo}
	 * 
	 * @param out OutputStream to serialize the object <var>partyInfo</var>
	 * @param partyInfo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,PartyInfo partyInfo) throws SerializerException {
		this.objToXml(out, PartyInfo.class, partyInfo, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>partyInfo</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartyInfo}
	 * 
	 * @param out OutputStream to serialize the object <var>partyInfo</var>
	 * @param partyInfo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,PartyInfo partyInfo,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, PartyInfo.class, partyInfo, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>partyInfo</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartyInfo}
	 * 
	 * @param partyInfo Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(PartyInfo partyInfo) throws SerializerException {
		return this.objToXml(PartyInfo.class, partyInfo, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>partyInfo</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartyInfo}
	 * 
	 * @param partyInfo Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(PartyInfo partyInfo,boolean prettyPrint) throws SerializerException {
		return this.objToXml(PartyInfo.class, partyInfo, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>partyInfo</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartyInfo}
	 * 
	 * @param partyInfo Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(PartyInfo partyInfo) throws SerializerException {
		return this.objToXml(PartyInfo.class, partyInfo, false).toString();
	}
	/**
	 * Serialize to String the object <var>partyInfo</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartyInfo}
	 * 
	 * @param partyInfo Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(PartyInfo partyInfo,boolean prettyPrint) throws SerializerException {
		return this.objToXml(PartyInfo.class, partyInfo, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: CollaborationInfo
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>collaborationInfo</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.CollaborationInfo}
	 * 
	 * @param fileName Xml file to serialize the object <var>collaborationInfo</var>
	 * @param collaborationInfo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,CollaborationInfo collaborationInfo) throws SerializerException {
		this.objToXml(fileName, CollaborationInfo.class, collaborationInfo, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>collaborationInfo</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.CollaborationInfo}
	 * 
	 * @param fileName Xml file to serialize the object <var>collaborationInfo</var>
	 * @param collaborationInfo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,CollaborationInfo collaborationInfo,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, CollaborationInfo.class, collaborationInfo, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>collaborationInfo</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.CollaborationInfo}
	 * 
	 * @param file Xml file to serialize the object <var>collaborationInfo</var>
	 * @param collaborationInfo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,CollaborationInfo collaborationInfo) throws SerializerException {
		this.objToXml(file, CollaborationInfo.class, collaborationInfo, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>collaborationInfo</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.CollaborationInfo}
	 * 
	 * @param file Xml file to serialize the object <var>collaborationInfo</var>
	 * @param collaborationInfo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,CollaborationInfo collaborationInfo,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, CollaborationInfo.class, collaborationInfo, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>collaborationInfo</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.CollaborationInfo}
	 * 
	 * @param out OutputStream to serialize the object <var>collaborationInfo</var>
	 * @param collaborationInfo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,CollaborationInfo collaborationInfo) throws SerializerException {
		this.objToXml(out, CollaborationInfo.class, collaborationInfo, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>collaborationInfo</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.CollaborationInfo}
	 * 
	 * @param out OutputStream to serialize the object <var>collaborationInfo</var>
	 * @param collaborationInfo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,CollaborationInfo collaborationInfo,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, CollaborationInfo.class, collaborationInfo, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>collaborationInfo</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.CollaborationInfo}
	 * 
	 * @param collaborationInfo Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(CollaborationInfo collaborationInfo) throws SerializerException {
		return this.objToXml(CollaborationInfo.class, collaborationInfo, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>collaborationInfo</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.CollaborationInfo}
	 * 
	 * @param collaborationInfo Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(CollaborationInfo collaborationInfo,boolean prettyPrint) throws SerializerException {
		return this.objToXml(CollaborationInfo.class, collaborationInfo, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>collaborationInfo</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.CollaborationInfo}
	 * 
	 * @param collaborationInfo Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(CollaborationInfo collaborationInfo) throws SerializerException {
		return this.objToXml(CollaborationInfo.class, collaborationInfo, false).toString();
	}
	/**
	 * Serialize to String the object <var>collaborationInfo</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.CollaborationInfo}
	 * 
	 * @param collaborationInfo Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(CollaborationInfo collaborationInfo,boolean prettyPrint) throws SerializerException {
		return this.objToXml(CollaborationInfo.class, collaborationInfo, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: PayloadInfo
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>payloadInfo</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PayloadInfo}
	 * 
	 * @param fileName Xml file to serialize the object <var>payloadInfo</var>
	 * @param payloadInfo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,PayloadInfo payloadInfo) throws SerializerException {
		this.objToXml(fileName, PayloadInfo.class, payloadInfo, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>payloadInfo</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PayloadInfo}
	 * 
	 * @param fileName Xml file to serialize the object <var>payloadInfo</var>
	 * @param payloadInfo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,PayloadInfo payloadInfo,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, PayloadInfo.class, payloadInfo, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>payloadInfo</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PayloadInfo}
	 * 
	 * @param file Xml file to serialize the object <var>payloadInfo</var>
	 * @param payloadInfo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,PayloadInfo payloadInfo) throws SerializerException {
		this.objToXml(file, PayloadInfo.class, payloadInfo, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>payloadInfo</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PayloadInfo}
	 * 
	 * @param file Xml file to serialize the object <var>payloadInfo</var>
	 * @param payloadInfo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,PayloadInfo payloadInfo,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, PayloadInfo.class, payloadInfo, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>payloadInfo</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PayloadInfo}
	 * 
	 * @param out OutputStream to serialize the object <var>payloadInfo</var>
	 * @param payloadInfo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,PayloadInfo payloadInfo) throws SerializerException {
		this.objToXml(out, PayloadInfo.class, payloadInfo, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>payloadInfo</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PayloadInfo}
	 * 
	 * @param out OutputStream to serialize the object <var>payloadInfo</var>
	 * @param payloadInfo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,PayloadInfo payloadInfo,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, PayloadInfo.class, payloadInfo, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>payloadInfo</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PayloadInfo}
	 * 
	 * @param payloadInfo Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(PayloadInfo payloadInfo) throws SerializerException {
		return this.objToXml(PayloadInfo.class, payloadInfo, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>payloadInfo</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PayloadInfo}
	 * 
	 * @param payloadInfo Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(PayloadInfo payloadInfo,boolean prettyPrint) throws SerializerException {
		return this.objToXml(PayloadInfo.class, payloadInfo, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>payloadInfo</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PayloadInfo}
	 * 
	 * @param payloadInfo Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(PayloadInfo payloadInfo) throws SerializerException {
		return this.objToXml(PayloadInfo.class, payloadInfo, false).toString();
	}
	/**
	 * Serialize to String the object <var>payloadInfo</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PayloadInfo}
	 * 
	 * @param payloadInfo Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(PayloadInfo payloadInfo,boolean prettyPrint) throws SerializerException {
		return this.objToXml(PayloadInfo.class, payloadInfo, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: SignalMessage
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>signalMessage</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.SignalMessage}
	 * 
	 * @param fileName Xml file to serialize the object <var>signalMessage</var>
	 * @param signalMessage Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,SignalMessage signalMessage) throws SerializerException {
		this.objToXml(fileName, SignalMessage.class, signalMessage, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>signalMessage</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.SignalMessage}
	 * 
	 * @param fileName Xml file to serialize the object <var>signalMessage</var>
	 * @param signalMessage Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,SignalMessage signalMessage,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, SignalMessage.class, signalMessage, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>signalMessage</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.SignalMessage}
	 * 
	 * @param file Xml file to serialize the object <var>signalMessage</var>
	 * @param signalMessage Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,SignalMessage signalMessage) throws SerializerException {
		this.objToXml(file, SignalMessage.class, signalMessage, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>signalMessage</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.SignalMessage}
	 * 
	 * @param file Xml file to serialize the object <var>signalMessage</var>
	 * @param signalMessage Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,SignalMessage signalMessage,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, SignalMessage.class, signalMessage, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>signalMessage</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.SignalMessage}
	 * 
	 * @param out OutputStream to serialize the object <var>signalMessage</var>
	 * @param signalMessage Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,SignalMessage signalMessage) throws SerializerException {
		this.objToXml(out, SignalMessage.class, signalMessage, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>signalMessage</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.SignalMessage}
	 * 
	 * @param out OutputStream to serialize the object <var>signalMessage</var>
	 * @param signalMessage Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,SignalMessage signalMessage,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, SignalMessage.class, signalMessage, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>signalMessage</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.SignalMessage}
	 * 
	 * @param signalMessage Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(SignalMessage signalMessage) throws SerializerException {
		return this.objToXml(SignalMessage.class, signalMessage, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>signalMessage</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.SignalMessage}
	 * 
	 * @param signalMessage Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(SignalMessage signalMessage,boolean prettyPrint) throws SerializerException {
		return this.objToXml(SignalMessage.class, signalMessage, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>signalMessage</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.SignalMessage}
	 * 
	 * @param signalMessage Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(SignalMessage signalMessage) throws SerializerException {
		return this.objToXml(SignalMessage.class, signalMessage, false).toString();
	}
	/**
	 * Serialize to String the object <var>signalMessage</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.SignalMessage}
	 * 
	 * @param signalMessage Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(SignalMessage signalMessage,boolean prettyPrint) throws SerializerException {
		return this.objToXml(SignalMessage.class, signalMessage, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: Messaging
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>messaging</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Messaging}
	 * 
	 * @param fileName Xml file to serialize the object <var>messaging</var>
	 * @param messaging Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Messaging messaging) throws SerializerException {
		this.objToXml(fileName, Messaging.class, messaging, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>messaging</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Messaging}
	 * 
	 * @param fileName Xml file to serialize the object <var>messaging</var>
	 * @param messaging Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Messaging messaging,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Messaging.class, messaging, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>messaging</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Messaging}
	 * 
	 * @param file Xml file to serialize the object <var>messaging</var>
	 * @param messaging Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Messaging messaging) throws SerializerException {
		this.objToXml(file, Messaging.class, messaging, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>messaging</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Messaging}
	 * 
	 * @param file Xml file to serialize the object <var>messaging</var>
	 * @param messaging Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Messaging messaging,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Messaging.class, messaging, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>messaging</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Messaging}
	 * 
	 * @param out OutputStream to serialize the object <var>messaging</var>
	 * @param messaging Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Messaging messaging) throws SerializerException {
		this.objToXml(out, Messaging.class, messaging, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>messaging</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Messaging}
	 * 
	 * @param out OutputStream to serialize the object <var>messaging</var>
	 * @param messaging Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Messaging messaging,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Messaging.class, messaging, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>messaging</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Messaging}
	 * 
	 * @param messaging Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Messaging messaging) throws SerializerException {
		return this.objToXml(Messaging.class, messaging, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>messaging</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Messaging}
	 * 
	 * @param messaging Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Messaging messaging,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Messaging.class, messaging, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>messaging</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Messaging}
	 * 
	 * @param messaging Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Messaging messaging) throws SerializerException {
		return this.objToXml(Messaging.class, messaging, false).toString();
	}
	/**
	 * Serialize to String the object <var>messaging</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Messaging}
	 * 
	 * @param messaging Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Messaging messaging,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Messaging.class, messaging, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: PartyId
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>partyId</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartyId}
	 * 
	 * @param fileName Xml file to serialize the object <var>partyId</var>
	 * @param partyId Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,PartyId partyId) throws SerializerException {
		this.objToXml(fileName, PartyId.class, partyId, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>partyId</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartyId}
	 * 
	 * @param fileName Xml file to serialize the object <var>partyId</var>
	 * @param partyId Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,PartyId partyId,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, PartyId.class, partyId, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>partyId</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartyId}
	 * 
	 * @param file Xml file to serialize the object <var>partyId</var>
	 * @param partyId Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,PartyId partyId) throws SerializerException {
		this.objToXml(file, PartyId.class, partyId, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>partyId</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartyId}
	 * 
	 * @param file Xml file to serialize the object <var>partyId</var>
	 * @param partyId Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,PartyId partyId,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, PartyId.class, partyId, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>partyId</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartyId}
	 * 
	 * @param out OutputStream to serialize the object <var>partyId</var>
	 * @param partyId Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,PartyId partyId) throws SerializerException {
		this.objToXml(out, PartyId.class, partyId, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>partyId</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartyId}
	 * 
	 * @param out OutputStream to serialize the object <var>partyId</var>
	 * @param partyId Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,PartyId partyId,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, PartyId.class, partyId, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>partyId</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartyId}
	 * 
	 * @param partyId Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(PartyId partyId) throws SerializerException {
		return this.objToXml(PartyId.class, partyId, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>partyId</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartyId}
	 * 
	 * @param partyId Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(PartyId partyId,boolean prettyPrint) throws SerializerException {
		return this.objToXml(PartyId.class, partyId, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>partyId</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartyId}
	 * 
	 * @param partyId Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(PartyId partyId) throws SerializerException {
		return this.objToXml(PartyId.class, partyId, false).toString();
	}
	/**
	 * Serialize to String the object <var>partyId</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartyId}
	 * 
	 * @param partyId Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(PartyId partyId,boolean prettyPrint) throws SerializerException {
		return this.objToXml(PartyId.class, partyId, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: To
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>to</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.To}
	 * 
	 * @param fileName Xml file to serialize the object <var>to</var>
	 * @param to Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,To to) throws SerializerException {
		this.objToXml(fileName, To.class, to, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>to</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.To}
	 * 
	 * @param fileName Xml file to serialize the object <var>to</var>
	 * @param to Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,To to,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, To.class, to, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>to</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.To}
	 * 
	 * @param file Xml file to serialize the object <var>to</var>
	 * @param to Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,To to) throws SerializerException {
		this.objToXml(file, To.class, to, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>to</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.To}
	 * 
	 * @param file Xml file to serialize the object <var>to</var>
	 * @param to Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,To to,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, To.class, to, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>to</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.To}
	 * 
	 * @param out OutputStream to serialize the object <var>to</var>
	 * @param to Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,To to) throws SerializerException {
		this.objToXml(out, To.class, to, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>to</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.To}
	 * 
	 * @param out OutputStream to serialize the object <var>to</var>
	 * @param to Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,To to,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, To.class, to, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>to</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.To}
	 * 
	 * @param to Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(To to) throws SerializerException {
		return this.objToXml(To.class, to, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>to</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.To}
	 * 
	 * @param to Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(To to,boolean prettyPrint) throws SerializerException {
		return this.objToXml(To.class, to, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>to</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.To}
	 * 
	 * @param to Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(To to) throws SerializerException {
		return this.objToXml(To.class, to, false).toString();
	}
	/**
	 * Serialize to String the object <var>to</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.To}
	 * 
	 * @param to Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(To to,boolean prettyPrint) throws SerializerException {
		return this.objToXml(To.class, to, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: PartProperties
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>partProperties</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartProperties}
	 * 
	 * @param fileName Xml file to serialize the object <var>partProperties</var>
	 * @param partProperties Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,PartProperties partProperties) throws SerializerException {
		this.objToXml(fileName, PartProperties.class, partProperties, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>partProperties</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartProperties}
	 * 
	 * @param fileName Xml file to serialize the object <var>partProperties</var>
	 * @param partProperties Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,PartProperties partProperties,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, PartProperties.class, partProperties, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>partProperties</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartProperties}
	 * 
	 * @param file Xml file to serialize the object <var>partProperties</var>
	 * @param partProperties Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,PartProperties partProperties) throws SerializerException {
		this.objToXml(file, PartProperties.class, partProperties, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>partProperties</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartProperties}
	 * 
	 * @param file Xml file to serialize the object <var>partProperties</var>
	 * @param partProperties Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,PartProperties partProperties,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, PartProperties.class, partProperties, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>partProperties</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartProperties}
	 * 
	 * @param out OutputStream to serialize the object <var>partProperties</var>
	 * @param partProperties Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,PartProperties partProperties) throws SerializerException {
		this.objToXml(out, PartProperties.class, partProperties, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>partProperties</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartProperties}
	 * 
	 * @param out OutputStream to serialize the object <var>partProperties</var>
	 * @param partProperties Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,PartProperties partProperties,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, PartProperties.class, partProperties, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>partProperties</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartProperties}
	 * 
	 * @param partProperties Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(PartProperties partProperties) throws SerializerException {
		return this.objToXml(PartProperties.class, partProperties, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>partProperties</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartProperties}
	 * 
	 * @param partProperties Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(PartProperties partProperties,boolean prettyPrint) throws SerializerException {
		return this.objToXml(PartProperties.class, partProperties, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>partProperties</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartProperties}
	 * 
	 * @param partProperties Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(PartProperties partProperties) throws SerializerException {
		return this.objToXml(PartProperties.class, partProperties, false).toString();
	}
	/**
	 * Serialize to String the object <var>partProperties</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartProperties}
	 * 
	 * @param partProperties Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(PartProperties partProperties,boolean prettyPrint) throws SerializerException {
		return this.objToXml(PartProperties.class, partProperties, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: AgreementRef
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>agreementRef</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.AgreementRef}
	 * 
	 * @param fileName Xml file to serialize the object <var>agreementRef</var>
	 * @param agreementRef Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,AgreementRef agreementRef) throws SerializerException {
		this.objToXml(fileName, AgreementRef.class, agreementRef, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>agreementRef</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.AgreementRef}
	 * 
	 * @param fileName Xml file to serialize the object <var>agreementRef</var>
	 * @param agreementRef Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,AgreementRef agreementRef,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, AgreementRef.class, agreementRef, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>agreementRef</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.AgreementRef}
	 * 
	 * @param file Xml file to serialize the object <var>agreementRef</var>
	 * @param agreementRef Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,AgreementRef agreementRef) throws SerializerException {
		this.objToXml(file, AgreementRef.class, agreementRef, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>agreementRef</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.AgreementRef}
	 * 
	 * @param file Xml file to serialize the object <var>agreementRef</var>
	 * @param agreementRef Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,AgreementRef agreementRef,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, AgreementRef.class, agreementRef, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>agreementRef</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.AgreementRef}
	 * 
	 * @param out OutputStream to serialize the object <var>agreementRef</var>
	 * @param agreementRef Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,AgreementRef agreementRef) throws SerializerException {
		this.objToXml(out, AgreementRef.class, agreementRef, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>agreementRef</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.AgreementRef}
	 * 
	 * @param out OutputStream to serialize the object <var>agreementRef</var>
	 * @param agreementRef Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,AgreementRef agreementRef,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, AgreementRef.class, agreementRef, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>agreementRef</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.AgreementRef}
	 * 
	 * @param agreementRef Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(AgreementRef agreementRef) throws SerializerException {
		return this.objToXml(AgreementRef.class, agreementRef, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>agreementRef</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.AgreementRef}
	 * 
	 * @param agreementRef Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(AgreementRef agreementRef,boolean prettyPrint) throws SerializerException {
		return this.objToXml(AgreementRef.class, agreementRef, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>agreementRef</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.AgreementRef}
	 * 
	 * @param agreementRef Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(AgreementRef agreementRef) throws SerializerException {
		return this.objToXml(AgreementRef.class, agreementRef, false).toString();
	}
	/**
	 * Serialize to String the object <var>agreementRef</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.AgreementRef}
	 * 
	 * @param agreementRef Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(AgreementRef agreementRef,boolean prettyPrint) throws SerializerException {
		return this.objToXml(AgreementRef.class, agreementRef, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: Service
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>service</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Service}
	 * 
	 * @param fileName Xml file to serialize the object <var>service</var>
	 * @param service Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Service service) throws SerializerException {
		this.objToXml(fileName, Service.class, service, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>service</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Service}
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
	 * Serialize to file system in <var>file</var> the object <var>service</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Service}
	 * 
	 * @param file Xml file to serialize the object <var>service</var>
	 * @param service Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Service service) throws SerializerException {
		this.objToXml(file, Service.class, service, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>service</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Service}
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
	 * Serialize to output stream <var>out</var> the object <var>service</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Service}
	 * 
	 * @param out OutputStream to serialize the object <var>service</var>
	 * @param service Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Service service) throws SerializerException {
		this.objToXml(out, Service.class, service, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>service</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Service}
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
	 * Serialize to byte array the object <var>service</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Service}
	 * 
	 * @param service Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Service service) throws SerializerException {
		return this.objToXml(Service.class, service, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>service</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Service}
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
	 * Serialize to String the object <var>service</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Service}
	 * 
	 * @param service Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Service service) throws SerializerException {
		return this.objToXml(Service.class, service, false).toString();
	}
	/**
	 * Serialize to String the object <var>service</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Service}
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
	 Object: PartInfo
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>partInfo</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartInfo}
	 * 
	 * @param fileName Xml file to serialize the object <var>partInfo</var>
	 * @param partInfo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,PartInfo partInfo) throws SerializerException {
		this.objToXml(fileName, PartInfo.class, partInfo, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>partInfo</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartInfo}
	 * 
	 * @param fileName Xml file to serialize the object <var>partInfo</var>
	 * @param partInfo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,PartInfo partInfo,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, PartInfo.class, partInfo, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>partInfo</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartInfo}
	 * 
	 * @param file Xml file to serialize the object <var>partInfo</var>
	 * @param partInfo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,PartInfo partInfo) throws SerializerException {
		this.objToXml(file, PartInfo.class, partInfo, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>partInfo</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartInfo}
	 * 
	 * @param file Xml file to serialize the object <var>partInfo</var>
	 * @param partInfo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,PartInfo partInfo,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, PartInfo.class, partInfo, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>partInfo</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartInfo}
	 * 
	 * @param out OutputStream to serialize the object <var>partInfo</var>
	 * @param partInfo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,PartInfo partInfo) throws SerializerException {
		this.objToXml(out, PartInfo.class, partInfo, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>partInfo</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartInfo}
	 * 
	 * @param out OutputStream to serialize the object <var>partInfo</var>
	 * @param partInfo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,PartInfo partInfo,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, PartInfo.class, partInfo, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>partInfo</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartInfo}
	 * 
	 * @param partInfo Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(PartInfo partInfo) throws SerializerException {
		return this.objToXml(PartInfo.class, partInfo, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>partInfo</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartInfo}
	 * 
	 * @param partInfo Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(PartInfo partInfo,boolean prettyPrint) throws SerializerException {
		return this.objToXml(PartInfo.class, partInfo, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>partInfo</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartInfo}
	 * 
	 * @param partInfo Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(PartInfo partInfo) throws SerializerException {
		return this.objToXml(PartInfo.class, partInfo, false).toString();
	}
	/**
	 * Serialize to String the object <var>partInfo</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartInfo}
	 * 
	 * @param partInfo Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(PartInfo partInfo,boolean prettyPrint) throws SerializerException {
		return this.objToXml(PartInfo.class, partInfo, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: From
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>from</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.From}
	 * 
	 * @param fileName Xml file to serialize the object <var>from</var>
	 * @param from Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,From from) throws SerializerException {
		this.objToXml(fileName, From.class, from, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>from</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.From}
	 * 
	 * @param fileName Xml file to serialize the object <var>from</var>
	 * @param from Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,From from,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, From.class, from, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>from</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.From}
	 * 
	 * @param file Xml file to serialize the object <var>from</var>
	 * @param from Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,From from) throws SerializerException {
		this.objToXml(file, From.class, from, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>from</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.From}
	 * 
	 * @param file Xml file to serialize the object <var>from</var>
	 * @param from Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,From from,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, From.class, from, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>from</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.From}
	 * 
	 * @param out OutputStream to serialize the object <var>from</var>
	 * @param from Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,From from) throws SerializerException {
		this.objToXml(out, From.class, from, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>from</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.From}
	 * 
	 * @param out OutputStream to serialize the object <var>from</var>
	 * @param from Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,From from,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, From.class, from, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>from</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.From}
	 * 
	 * @param from Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(From from) throws SerializerException {
		return this.objToXml(From.class, from, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>from</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.From}
	 * 
	 * @param from Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(From from,boolean prettyPrint) throws SerializerException {
		return this.objToXml(From.class, from, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>from</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.From}
	 * 
	 * @param from Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(From from) throws SerializerException {
		return this.objToXml(From.class, from, false).toString();
	}
	/**
	 * Serialize to String the object <var>from</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.From}
	 * 
	 * @param from Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(From from,boolean prettyPrint) throws SerializerException {
		return this.objToXml(From.class, from, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: Schema
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>schema</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Schema}
	 * 
	 * @param fileName Xml file to serialize the object <var>schema</var>
	 * @param schema Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Schema schema) throws SerializerException {
		this.objToXml(fileName, Schema.class, schema, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>schema</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Schema}
	 * 
	 * @param fileName Xml file to serialize the object <var>schema</var>
	 * @param schema Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Schema schema,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Schema.class, schema, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>schema</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Schema}
	 * 
	 * @param file Xml file to serialize the object <var>schema</var>
	 * @param schema Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Schema schema) throws SerializerException {
		this.objToXml(file, Schema.class, schema, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>schema</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Schema}
	 * 
	 * @param file Xml file to serialize the object <var>schema</var>
	 * @param schema Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Schema schema,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Schema.class, schema, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>schema</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Schema}
	 * 
	 * @param out OutputStream to serialize the object <var>schema</var>
	 * @param schema Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Schema schema) throws SerializerException {
		this.objToXml(out, Schema.class, schema, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>schema</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Schema}
	 * 
	 * @param out OutputStream to serialize the object <var>schema</var>
	 * @param schema Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Schema schema,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Schema.class, schema, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>schema</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Schema}
	 * 
	 * @param schema Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Schema schema) throws SerializerException {
		return this.objToXml(Schema.class, schema, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>schema</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Schema}
	 * 
	 * @param schema Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Schema schema,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Schema.class, schema, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>schema</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Schema}
	 * 
	 * @param schema Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Schema schema) throws SerializerException {
		return this.objToXml(Schema.class, schema, false).toString();
	}
	/**
	 * Serialize to String the object <var>schema</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Schema}
	 * 
	 * @param schema Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Schema schema,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Schema.class, schema, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: Error
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>error</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Error}
	 * 
	 * @param fileName Xml file to serialize the object <var>error</var>
	 * @param error Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Error error) throws SerializerException {
		this.objToXml(fileName, Error.class, error, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>error</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Error}
	 * 
	 * @param fileName Xml file to serialize the object <var>error</var>
	 * @param error Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Error error,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Error.class, error, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>error</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Error}
	 * 
	 * @param file Xml file to serialize the object <var>error</var>
	 * @param error Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Error error) throws SerializerException {
		this.objToXml(file, Error.class, error, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>error</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Error}
	 * 
	 * @param file Xml file to serialize the object <var>error</var>
	 * @param error Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Error error,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Error.class, error, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>error</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Error}
	 * 
	 * @param out OutputStream to serialize the object <var>error</var>
	 * @param error Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Error error) throws SerializerException {
		this.objToXml(out, Error.class, error, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>error</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Error}
	 * 
	 * @param out OutputStream to serialize the object <var>error</var>
	 * @param error Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Error error,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Error.class, error, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>error</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Error}
	 * 
	 * @param error Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Error error) throws SerializerException {
		return this.objToXml(Error.class, error, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>error</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Error}
	 * 
	 * @param error Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Error error,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Error.class, error, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>error</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Error}
	 * 
	 * @param error Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Error error) throws SerializerException {
		return this.objToXml(Error.class, error, false).toString();
	}
	/**
	 * Serialize to String the object <var>error</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Error}
	 * 
	 * @param error Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Error error,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Error.class, error, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: Description
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>description</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Description}
	 * 
	 * @param fileName Xml file to serialize the object <var>description</var>
	 * @param description Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Description description) throws SerializerException {
		this.objToXml(fileName, Description.class, description, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>description</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Description}
	 * 
	 * @param fileName Xml file to serialize the object <var>description</var>
	 * @param description Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Description description,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Description.class, description, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>description</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Description}
	 * 
	 * @param file Xml file to serialize the object <var>description</var>
	 * @param description Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Description description) throws SerializerException {
		this.objToXml(file, Description.class, description, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>description</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Description}
	 * 
	 * @param file Xml file to serialize the object <var>description</var>
	 * @param description Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Description description,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Description.class, description, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>description</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Description}
	 * 
	 * @param out OutputStream to serialize the object <var>description</var>
	 * @param description Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Description description) throws SerializerException {
		this.objToXml(out, Description.class, description, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>description</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Description}
	 * 
	 * @param out OutputStream to serialize the object <var>description</var>
	 * @param description Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Description description,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Description.class, description, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>description</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Description}
	 * 
	 * @param description Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Description description) throws SerializerException {
		return this.objToXml(Description.class, description, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>description</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Description}
	 * 
	 * @param description Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Description description,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Description.class, description, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>description</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Description}
	 * 
	 * @param description Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Description description) throws SerializerException {
		return this.objToXml(Description.class, description, false).toString();
	}
	/**
	 * Serialize to String the object <var>description</var> of type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Description}
	 * 
	 * @param description Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Description description,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Description.class, description, prettyPrint).toString();
	}
	
	
	

}
