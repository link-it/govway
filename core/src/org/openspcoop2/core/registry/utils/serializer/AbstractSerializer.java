/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
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
package org.openspcoop2.core.registry.utils.serializer;

import org.openspcoop2.generic_project.exception.SerializerException;
import org.openspcoop2.utils.beans.WriteToSerializerType;
import org.openspcoop2.utils.xml.JaxbUtils;

import org.openspcoop2.core.registry.MessagePart;
import org.openspcoop2.core.registry.Message;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.core.registry.Connettore;
import org.openspcoop2.core.registry.IdSoggetto;
import org.openspcoop2.core.registry.AccordoCooperazionePartecipanti;
import org.openspcoop2.core.registry.AccordoServizioParteComuneServizioCompostoServizioComponente;
import org.openspcoop2.core.registry.IdPortaDominio;
import org.openspcoop2.core.registry.Servizio;
import org.openspcoop2.core.registry.ServizioAzione;
import org.openspcoop2.core.registry.Operation;
import org.openspcoop2.core.registry.PortType;
import org.openspcoop2.core.registry.AccordoCooperazione;
import org.openspcoop2.core.registry.RegistroServizi;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.PortaDominio;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Documento;
import org.openspcoop2.core.registry.Property;
import org.openspcoop2.core.registry.AccordoServizioParteComuneServizioComposto;
import org.openspcoop2.core.registry.Azione;
import org.openspcoop2.core.registry.ServizioAzioneFruitore;
import org.openspcoop2.core.registry.IdAccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.IdAccordoCooperazione;
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
	 Object: servizio
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>servizio</var> of type {@link org.openspcoop2.core.registry.Servizio}
	 * 
	 * @param fileName Xml file to serialize the object <var>servizio</var>
	 * @param servizio Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Servizio servizio) throws SerializerException {
		this.objToXml(fileName, Servizio.class, servizio, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>servizio</var> of type {@link org.openspcoop2.core.registry.Servizio}
	 * 
	 * @param fileName Xml file to serialize the object <var>servizio</var>
	 * @param servizio Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Servizio servizio,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Servizio.class, servizio, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>servizio</var> of type {@link org.openspcoop2.core.registry.Servizio}
	 * 
	 * @param file Xml file to serialize the object <var>servizio</var>
	 * @param servizio Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Servizio servizio) throws SerializerException {
		this.objToXml(file, Servizio.class, servizio, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>servizio</var> of type {@link org.openspcoop2.core.registry.Servizio}
	 * 
	 * @param file Xml file to serialize the object <var>servizio</var>
	 * @param servizio Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Servizio servizio,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Servizio.class, servizio, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>servizio</var> of type {@link org.openspcoop2.core.registry.Servizio}
	 * 
	 * @param out OutputStream to serialize the object <var>servizio</var>
	 * @param servizio Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Servizio servizio) throws SerializerException {
		this.objToXml(out, Servizio.class, servizio, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>servizio</var> of type {@link org.openspcoop2.core.registry.Servizio}
	 * 
	 * @param out OutputStream to serialize the object <var>servizio</var>
	 * @param servizio Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Servizio servizio,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Servizio.class, servizio, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>servizio</var> of type {@link org.openspcoop2.core.registry.Servizio}
	 * 
	 * @param servizio Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Servizio servizio) throws SerializerException {
		return this.objToXml(Servizio.class, servizio, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>servizio</var> of type {@link org.openspcoop2.core.registry.Servizio}
	 * 
	 * @param servizio Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Servizio servizio,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Servizio.class, servizio, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>servizio</var> of type {@link org.openspcoop2.core.registry.Servizio}
	 * 
	 * @param servizio Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Servizio servizio) throws SerializerException {
		return this.objToXml(Servizio.class, servizio, false).toString();
	}
	/**
	 * Serialize to String the object <var>servizio</var> of type {@link org.openspcoop2.core.registry.Servizio}
	 * 
	 * @param servizio Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Servizio servizio,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Servizio.class, servizio, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: servizio-azione
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>servizioAzione</var> of type {@link org.openspcoop2.core.registry.ServizioAzione}
	 * 
	 * @param fileName Xml file to serialize the object <var>servizioAzione</var>
	 * @param servizioAzione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ServizioAzione servizioAzione) throws SerializerException {
		this.objToXml(fileName, ServizioAzione.class, servizioAzione, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>servizioAzione</var> of type {@link org.openspcoop2.core.registry.ServizioAzione}
	 * 
	 * @param fileName Xml file to serialize the object <var>servizioAzione</var>
	 * @param servizioAzione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ServizioAzione servizioAzione,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, ServizioAzione.class, servizioAzione, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>servizioAzione</var> of type {@link org.openspcoop2.core.registry.ServizioAzione}
	 * 
	 * @param file Xml file to serialize the object <var>servizioAzione</var>
	 * @param servizioAzione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ServizioAzione servizioAzione) throws SerializerException {
		this.objToXml(file, ServizioAzione.class, servizioAzione, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>servizioAzione</var> of type {@link org.openspcoop2.core.registry.ServizioAzione}
	 * 
	 * @param file Xml file to serialize the object <var>servizioAzione</var>
	 * @param servizioAzione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ServizioAzione servizioAzione,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, ServizioAzione.class, servizioAzione, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>servizioAzione</var> of type {@link org.openspcoop2.core.registry.ServizioAzione}
	 * 
	 * @param out OutputStream to serialize the object <var>servizioAzione</var>
	 * @param servizioAzione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ServizioAzione servizioAzione) throws SerializerException {
		this.objToXml(out, ServizioAzione.class, servizioAzione, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>servizioAzione</var> of type {@link org.openspcoop2.core.registry.ServizioAzione}
	 * 
	 * @param out OutputStream to serialize the object <var>servizioAzione</var>
	 * @param servizioAzione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ServizioAzione servizioAzione,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, ServizioAzione.class, servizioAzione, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>servizioAzione</var> of type {@link org.openspcoop2.core.registry.ServizioAzione}
	 * 
	 * @param servizioAzione Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ServizioAzione servizioAzione) throws SerializerException {
		return this.objToXml(ServizioAzione.class, servizioAzione, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>servizioAzione</var> of type {@link org.openspcoop2.core.registry.ServizioAzione}
	 * 
	 * @param servizioAzione Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ServizioAzione servizioAzione,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ServizioAzione.class, servizioAzione, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>servizioAzione</var> of type {@link org.openspcoop2.core.registry.ServizioAzione}
	 * 
	 * @param servizioAzione Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ServizioAzione servizioAzione) throws SerializerException {
		return this.objToXml(ServizioAzione.class, servizioAzione, false).toString();
	}
	/**
	 * Serialize to String the object <var>servizioAzione</var> of type {@link org.openspcoop2.core.registry.ServizioAzione}
	 * 
	 * @param servizioAzione Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ServizioAzione servizioAzione,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ServizioAzione.class, servizioAzione, prettyPrint).toString();
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
	 Object: servizio-azione-fruitore
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>servizioAzioneFruitore</var> of type {@link org.openspcoop2.core.registry.ServizioAzioneFruitore}
	 * 
	 * @param fileName Xml file to serialize the object <var>servizioAzioneFruitore</var>
	 * @param servizioAzioneFruitore Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ServizioAzioneFruitore servizioAzioneFruitore) throws SerializerException {
		this.objToXml(fileName, ServizioAzioneFruitore.class, servizioAzioneFruitore, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>servizioAzioneFruitore</var> of type {@link org.openspcoop2.core.registry.ServizioAzioneFruitore}
	 * 
	 * @param fileName Xml file to serialize the object <var>servizioAzioneFruitore</var>
	 * @param servizioAzioneFruitore Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ServizioAzioneFruitore servizioAzioneFruitore,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, ServizioAzioneFruitore.class, servizioAzioneFruitore, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>servizioAzioneFruitore</var> of type {@link org.openspcoop2.core.registry.ServizioAzioneFruitore}
	 * 
	 * @param file Xml file to serialize the object <var>servizioAzioneFruitore</var>
	 * @param servizioAzioneFruitore Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ServizioAzioneFruitore servizioAzioneFruitore) throws SerializerException {
		this.objToXml(file, ServizioAzioneFruitore.class, servizioAzioneFruitore, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>servizioAzioneFruitore</var> of type {@link org.openspcoop2.core.registry.ServizioAzioneFruitore}
	 * 
	 * @param file Xml file to serialize the object <var>servizioAzioneFruitore</var>
	 * @param servizioAzioneFruitore Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ServizioAzioneFruitore servizioAzioneFruitore,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, ServizioAzioneFruitore.class, servizioAzioneFruitore, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>servizioAzioneFruitore</var> of type {@link org.openspcoop2.core.registry.ServizioAzioneFruitore}
	 * 
	 * @param out OutputStream to serialize the object <var>servizioAzioneFruitore</var>
	 * @param servizioAzioneFruitore Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ServizioAzioneFruitore servizioAzioneFruitore) throws SerializerException {
		this.objToXml(out, ServizioAzioneFruitore.class, servizioAzioneFruitore, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>servizioAzioneFruitore</var> of type {@link org.openspcoop2.core.registry.ServizioAzioneFruitore}
	 * 
	 * @param out OutputStream to serialize the object <var>servizioAzioneFruitore</var>
	 * @param servizioAzioneFruitore Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ServizioAzioneFruitore servizioAzioneFruitore,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, ServizioAzioneFruitore.class, servizioAzioneFruitore, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>servizioAzioneFruitore</var> of type {@link org.openspcoop2.core.registry.ServizioAzioneFruitore}
	 * 
	 * @param servizioAzioneFruitore Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ServizioAzioneFruitore servizioAzioneFruitore) throws SerializerException {
		return this.objToXml(ServizioAzioneFruitore.class, servizioAzioneFruitore, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>servizioAzioneFruitore</var> of type {@link org.openspcoop2.core.registry.ServizioAzioneFruitore}
	 * 
	 * @param servizioAzioneFruitore Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ServizioAzioneFruitore servizioAzioneFruitore,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ServizioAzioneFruitore.class, servizioAzioneFruitore, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>servizioAzioneFruitore</var> of type {@link org.openspcoop2.core.registry.ServizioAzioneFruitore}
	 * 
	 * @param servizioAzioneFruitore Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ServizioAzioneFruitore servizioAzioneFruitore) throws SerializerException {
		return this.objToXml(ServizioAzioneFruitore.class, servizioAzioneFruitore, false).toString();
	}
	/**
	 * Serialize to String the object <var>servizioAzioneFruitore</var> of type {@link org.openspcoop2.core.registry.ServizioAzioneFruitore}
	 * 
	 * @param servizioAzioneFruitore Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ServizioAzioneFruitore servizioAzioneFruitore,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ServizioAzioneFruitore.class, servizioAzioneFruitore, prettyPrint).toString();
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
