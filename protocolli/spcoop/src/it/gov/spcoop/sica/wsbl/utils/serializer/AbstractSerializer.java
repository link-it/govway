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
package it.gov.spcoop.sica.wsbl.utils.serializer;

import org.openspcoop2.generic_project.exception.SerializerException;
import org.openspcoop2.utils.beans.WriteToSerializerType;
import org.openspcoop2.utils.xml.JaxbUtils;

import it.gov.spcoop.sica.wsbl.EventTypeMessage;
import it.gov.spcoop.sica.wsbl.EventType;
import it.gov.spcoop.sica.wsbl.StateTypeInitial;
import it.gov.spcoop.sica.wsbl.CompletionModeTypeCompensateMessage;
import it.gov.spcoop.sica.wsbl.CompletionModeType;
import it.gov.spcoop.sica.wsbl.TransitionType;
import it.gov.spcoop.sica.wsbl.GuardType;
import it.gov.spcoop.sica.wsbl.EventListType;
import it.gov.spcoop.sica.wsbl.TemporalConditionType;
import it.gov.spcoop.sica.wsbl.ConceptualBehavior;
import it.gov.spcoop.sica.wsbl.StatesType;
import it.gov.spcoop.sica.wsbl.TransitionsType;
import it.gov.spcoop.sica.wsbl.MessageBehavior;
import it.gov.spcoop.sica.wsbl.MessagesTypes;
import it.gov.spcoop.sica.wsbl.StateTypeFinal;
import it.gov.spcoop.sica.wsbl.Message;
import it.gov.spcoop.sica.wsbl.StateTypeNormal;

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
	 Object: eventTypeMessage
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>eventTypeMessage</var> of type {@link it.gov.spcoop.sica.wsbl.EventTypeMessage}
	 * 
	 * @param fileName Xml file to serialize the object <var>eventTypeMessage</var>
	 * @param eventTypeMessage Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,EventTypeMessage eventTypeMessage) throws SerializerException {
		this.objToXml(fileName, EventTypeMessage.class, eventTypeMessage, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>eventTypeMessage</var> of type {@link it.gov.spcoop.sica.wsbl.EventTypeMessage}
	 * 
	 * @param fileName Xml file to serialize the object <var>eventTypeMessage</var>
	 * @param eventTypeMessage Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,EventTypeMessage eventTypeMessage,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, EventTypeMessage.class, eventTypeMessage, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>eventTypeMessage</var> of type {@link it.gov.spcoop.sica.wsbl.EventTypeMessage}
	 * 
	 * @param file Xml file to serialize the object <var>eventTypeMessage</var>
	 * @param eventTypeMessage Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,EventTypeMessage eventTypeMessage) throws SerializerException {
		this.objToXml(file, EventTypeMessage.class, eventTypeMessage, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>eventTypeMessage</var> of type {@link it.gov.spcoop.sica.wsbl.EventTypeMessage}
	 * 
	 * @param file Xml file to serialize the object <var>eventTypeMessage</var>
	 * @param eventTypeMessage Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,EventTypeMessage eventTypeMessage,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, EventTypeMessage.class, eventTypeMessage, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>eventTypeMessage</var> of type {@link it.gov.spcoop.sica.wsbl.EventTypeMessage}
	 * 
	 * @param out OutputStream to serialize the object <var>eventTypeMessage</var>
	 * @param eventTypeMessage Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,EventTypeMessage eventTypeMessage) throws SerializerException {
		this.objToXml(out, EventTypeMessage.class, eventTypeMessage, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>eventTypeMessage</var> of type {@link it.gov.spcoop.sica.wsbl.EventTypeMessage}
	 * 
	 * @param out OutputStream to serialize the object <var>eventTypeMessage</var>
	 * @param eventTypeMessage Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,EventTypeMessage eventTypeMessage,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, EventTypeMessage.class, eventTypeMessage, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>eventTypeMessage</var> of type {@link it.gov.spcoop.sica.wsbl.EventTypeMessage}
	 * 
	 * @param eventTypeMessage Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(EventTypeMessage eventTypeMessage) throws SerializerException {
		return this.objToXml(EventTypeMessage.class, eventTypeMessage, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>eventTypeMessage</var> of type {@link it.gov.spcoop.sica.wsbl.EventTypeMessage}
	 * 
	 * @param eventTypeMessage Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(EventTypeMessage eventTypeMessage,boolean prettyPrint) throws SerializerException {
		return this.objToXml(EventTypeMessage.class, eventTypeMessage, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>eventTypeMessage</var> of type {@link it.gov.spcoop.sica.wsbl.EventTypeMessage}
	 * 
	 * @param eventTypeMessage Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(EventTypeMessage eventTypeMessage) throws SerializerException {
		return this.objToXml(EventTypeMessage.class, eventTypeMessage, false).toString();
	}
	/**
	 * Serialize to String the object <var>eventTypeMessage</var> of type {@link it.gov.spcoop.sica.wsbl.EventTypeMessage}
	 * 
	 * @param eventTypeMessage Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(EventTypeMessage eventTypeMessage,boolean prettyPrint) throws SerializerException {
		return this.objToXml(EventTypeMessage.class, eventTypeMessage, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: eventType
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>eventType</var> of type {@link it.gov.spcoop.sica.wsbl.EventType}
	 * 
	 * @param fileName Xml file to serialize the object <var>eventType</var>
	 * @param eventType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,EventType eventType) throws SerializerException {
		this.objToXml(fileName, EventType.class, eventType, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>eventType</var> of type {@link it.gov.spcoop.sica.wsbl.EventType}
	 * 
	 * @param fileName Xml file to serialize the object <var>eventType</var>
	 * @param eventType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,EventType eventType,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, EventType.class, eventType, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>eventType</var> of type {@link it.gov.spcoop.sica.wsbl.EventType}
	 * 
	 * @param file Xml file to serialize the object <var>eventType</var>
	 * @param eventType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,EventType eventType) throws SerializerException {
		this.objToXml(file, EventType.class, eventType, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>eventType</var> of type {@link it.gov.spcoop.sica.wsbl.EventType}
	 * 
	 * @param file Xml file to serialize the object <var>eventType</var>
	 * @param eventType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,EventType eventType,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, EventType.class, eventType, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>eventType</var> of type {@link it.gov.spcoop.sica.wsbl.EventType}
	 * 
	 * @param out OutputStream to serialize the object <var>eventType</var>
	 * @param eventType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,EventType eventType) throws SerializerException {
		this.objToXml(out, EventType.class, eventType, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>eventType</var> of type {@link it.gov.spcoop.sica.wsbl.EventType}
	 * 
	 * @param out OutputStream to serialize the object <var>eventType</var>
	 * @param eventType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,EventType eventType,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, EventType.class, eventType, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>eventType</var> of type {@link it.gov.spcoop.sica.wsbl.EventType}
	 * 
	 * @param eventType Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(EventType eventType) throws SerializerException {
		return this.objToXml(EventType.class, eventType, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>eventType</var> of type {@link it.gov.spcoop.sica.wsbl.EventType}
	 * 
	 * @param eventType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(EventType eventType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(EventType.class, eventType, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>eventType</var> of type {@link it.gov.spcoop.sica.wsbl.EventType}
	 * 
	 * @param eventType Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(EventType eventType) throws SerializerException {
		return this.objToXml(EventType.class, eventType, false).toString();
	}
	/**
	 * Serialize to String the object <var>eventType</var> of type {@link it.gov.spcoop.sica.wsbl.EventType}
	 * 
	 * @param eventType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(EventType eventType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(EventType.class, eventType, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: StateTypeInitial
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>stateTypeInitial</var> of type {@link it.gov.spcoop.sica.wsbl.StateTypeInitial}
	 * 
	 * @param fileName Xml file to serialize the object <var>stateTypeInitial</var>
	 * @param stateTypeInitial Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,StateTypeInitial stateTypeInitial) throws SerializerException {
		this.objToXml(fileName, StateTypeInitial.class, stateTypeInitial, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>stateTypeInitial</var> of type {@link it.gov.spcoop.sica.wsbl.StateTypeInitial}
	 * 
	 * @param fileName Xml file to serialize the object <var>stateTypeInitial</var>
	 * @param stateTypeInitial Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,StateTypeInitial stateTypeInitial,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, StateTypeInitial.class, stateTypeInitial, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>stateTypeInitial</var> of type {@link it.gov.spcoop.sica.wsbl.StateTypeInitial}
	 * 
	 * @param file Xml file to serialize the object <var>stateTypeInitial</var>
	 * @param stateTypeInitial Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,StateTypeInitial stateTypeInitial) throws SerializerException {
		this.objToXml(file, StateTypeInitial.class, stateTypeInitial, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>stateTypeInitial</var> of type {@link it.gov.spcoop.sica.wsbl.StateTypeInitial}
	 * 
	 * @param file Xml file to serialize the object <var>stateTypeInitial</var>
	 * @param stateTypeInitial Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,StateTypeInitial stateTypeInitial,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, StateTypeInitial.class, stateTypeInitial, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>stateTypeInitial</var> of type {@link it.gov.spcoop.sica.wsbl.StateTypeInitial}
	 * 
	 * @param out OutputStream to serialize the object <var>stateTypeInitial</var>
	 * @param stateTypeInitial Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,StateTypeInitial stateTypeInitial) throws SerializerException {
		this.objToXml(out, StateTypeInitial.class, stateTypeInitial, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>stateTypeInitial</var> of type {@link it.gov.spcoop.sica.wsbl.StateTypeInitial}
	 * 
	 * @param out OutputStream to serialize the object <var>stateTypeInitial</var>
	 * @param stateTypeInitial Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,StateTypeInitial stateTypeInitial,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, StateTypeInitial.class, stateTypeInitial, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>stateTypeInitial</var> of type {@link it.gov.spcoop.sica.wsbl.StateTypeInitial}
	 * 
	 * @param stateTypeInitial Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(StateTypeInitial stateTypeInitial) throws SerializerException {
		return this.objToXml(StateTypeInitial.class, stateTypeInitial, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>stateTypeInitial</var> of type {@link it.gov.spcoop.sica.wsbl.StateTypeInitial}
	 * 
	 * @param stateTypeInitial Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(StateTypeInitial stateTypeInitial,boolean prettyPrint) throws SerializerException {
		return this.objToXml(StateTypeInitial.class, stateTypeInitial, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>stateTypeInitial</var> of type {@link it.gov.spcoop.sica.wsbl.StateTypeInitial}
	 * 
	 * @param stateTypeInitial Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(StateTypeInitial stateTypeInitial) throws SerializerException {
		return this.objToXml(StateTypeInitial.class, stateTypeInitial, false).toString();
	}
	/**
	 * Serialize to String the object <var>stateTypeInitial</var> of type {@link it.gov.spcoop.sica.wsbl.StateTypeInitial}
	 * 
	 * @param stateTypeInitial Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(StateTypeInitial stateTypeInitial,boolean prettyPrint) throws SerializerException {
		return this.objToXml(StateTypeInitial.class, stateTypeInitial, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: completionModeTypeCompensateMessage
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>completionModeTypeCompensateMessage</var> of type {@link it.gov.spcoop.sica.wsbl.CompletionModeTypeCompensateMessage}
	 * 
	 * @param fileName Xml file to serialize the object <var>completionModeTypeCompensateMessage</var>
	 * @param completionModeTypeCompensateMessage Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,CompletionModeTypeCompensateMessage completionModeTypeCompensateMessage) throws SerializerException {
		this.objToXml(fileName, CompletionModeTypeCompensateMessage.class, completionModeTypeCompensateMessage, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>completionModeTypeCompensateMessage</var> of type {@link it.gov.spcoop.sica.wsbl.CompletionModeTypeCompensateMessage}
	 * 
	 * @param fileName Xml file to serialize the object <var>completionModeTypeCompensateMessage</var>
	 * @param completionModeTypeCompensateMessage Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,CompletionModeTypeCompensateMessage completionModeTypeCompensateMessage,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, CompletionModeTypeCompensateMessage.class, completionModeTypeCompensateMessage, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>completionModeTypeCompensateMessage</var> of type {@link it.gov.spcoop.sica.wsbl.CompletionModeTypeCompensateMessage}
	 * 
	 * @param file Xml file to serialize the object <var>completionModeTypeCompensateMessage</var>
	 * @param completionModeTypeCompensateMessage Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,CompletionModeTypeCompensateMessage completionModeTypeCompensateMessage) throws SerializerException {
		this.objToXml(file, CompletionModeTypeCompensateMessage.class, completionModeTypeCompensateMessage, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>completionModeTypeCompensateMessage</var> of type {@link it.gov.spcoop.sica.wsbl.CompletionModeTypeCompensateMessage}
	 * 
	 * @param file Xml file to serialize the object <var>completionModeTypeCompensateMessage</var>
	 * @param completionModeTypeCompensateMessage Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,CompletionModeTypeCompensateMessage completionModeTypeCompensateMessage,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, CompletionModeTypeCompensateMessage.class, completionModeTypeCompensateMessage, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>completionModeTypeCompensateMessage</var> of type {@link it.gov.spcoop.sica.wsbl.CompletionModeTypeCompensateMessage}
	 * 
	 * @param out OutputStream to serialize the object <var>completionModeTypeCompensateMessage</var>
	 * @param completionModeTypeCompensateMessage Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,CompletionModeTypeCompensateMessage completionModeTypeCompensateMessage) throws SerializerException {
		this.objToXml(out, CompletionModeTypeCompensateMessage.class, completionModeTypeCompensateMessage, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>completionModeTypeCompensateMessage</var> of type {@link it.gov.spcoop.sica.wsbl.CompletionModeTypeCompensateMessage}
	 * 
	 * @param out OutputStream to serialize the object <var>completionModeTypeCompensateMessage</var>
	 * @param completionModeTypeCompensateMessage Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,CompletionModeTypeCompensateMessage completionModeTypeCompensateMessage,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, CompletionModeTypeCompensateMessage.class, completionModeTypeCompensateMessage, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>completionModeTypeCompensateMessage</var> of type {@link it.gov.spcoop.sica.wsbl.CompletionModeTypeCompensateMessage}
	 * 
	 * @param completionModeTypeCompensateMessage Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(CompletionModeTypeCompensateMessage completionModeTypeCompensateMessage) throws SerializerException {
		return this.objToXml(CompletionModeTypeCompensateMessage.class, completionModeTypeCompensateMessage, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>completionModeTypeCompensateMessage</var> of type {@link it.gov.spcoop.sica.wsbl.CompletionModeTypeCompensateMessage}
	 * 
	 * @param completionModeTypeCompensateMessage Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(CompletionModeTypeCompensateMessage completionModeTypeCompensateMessage,boolean prettyPrint) throws SerializerException {
		return this.objToXml(CompletionModeTypeCompensateMessage.class, completionModeTypeCompensateMessage, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>completionModeTypeCompensateMessage</var> of type {@link it.gov.spcoop.sica.wsbl.CompletionModeTypeCompensateMessage}
	 * 
	 * @param completionModeTypeCompensateMessage Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(CompletionModeTypeCompensateMessage completionModeTypeCompensateMessage) throws SerializerException {
		return this.objToXml(CompletionModeTypeCompensateMessage.class, completionModeTypeCompensateMessage, false).toString();
	}
	/**
	 * Serialize to String the object <var>completionModeTypeCompensateMessage</var> of type {@link it.gov.spcoop.sica.wsbl.CompletionModeTypeCompensateMessage}
	 * 
	 * @param completionModeTypeCompensateMessage Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(CompletionModeTypeCompensateMessage completionModeTypeCompensateMessage,boolean prettyPrint) throws SerializerException {
		return this.objToXml(CompletionModeTypeCompensateMessage.class, completionModeTypeCompensateMessage, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: completionModeType
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>completionModeType</var> of type {@link it.gov.spcoop.sica.wsbl.CompletionModeType}
	 * 
	 * @param fileName Xml file to serialize the object <var>completionModeType</var>
	 * @param completionModeType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,CompletionModeType completionModeType) throws SerializerException {
		this.objToXml(fileName, CompletionModeType.class, completionModeType, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>completionModeType</var> of type {@link it.gov.spcoop.sica.wsbl.CompletionModeType}
	 * 
	 * @param fileName Xml file to serialize the object <var>completionModeType</var>
	 * @param completionModeType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,CompletionModeType completionModeType,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, CompletionModeType.class, completionModeType, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>completionModeType</var> of type {@link it.gov.spcoop.sica.wsbl.CompletionModeType}
	 * 
	 * @param file Xml file to serialize the object <var>completionModeType</var>
	 * @param completionModeType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,CompletionModeType completionModeType) throws SerializerException {
		this.objToXml(file, CompletionModeType.class, completionModeType, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>completionModeType</var> of type {@link it.gov.spcoop.sica.wsbl.CompletionModeType}
	 * 
	 * @param file Xml file to serialize the object <var>completionModeType</var>
	 * @param completionModeType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,CompletionModeType completionModeType,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, CompletionModeType.class, completionModeType, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>completionModeType</var> of type {@link it.gov.spcoop.sica.wsbl.CompletionModeType}
	 * 
	 * @param out OutputStream to serialize the object <var>completionModeType</var>
	 * @param completionModeType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,CompletionModeType completionModeType) throws SerializerException {
		this.objToXml(out, CompletionModeType.class, completionModeType, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>completionModeType</var> of type {@link it.gov.spcoop.sica.wsbl.CompletionModeType}
	 * 
	 * @param out OutputStream to serialize the object <var>completionModeType</var>
	 * @param completionModeType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,CompletionModeType completionModeType,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, CompletionModeType.class, completionModeType, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>completionModeType</var> of type {@link it.gov.spcoop.sica.wsbl.CompletionModeType}
	 * 
	 * @param completionModeType Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(CompletionModeType completionModeType) throws SerializerException {
		return this.objToXml(CompletionModeType.class, completionModeType, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>completionModeType</var> of type {@link it.gov.spcoop.sica.wsbl.CompletionModeType}
	 * 
	 * @param completionModeType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(CompletionModeType completionModeType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(CompletionModeType.class, completionModeType, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>completionModeType</var> of type {@link it.gov.spcoop.sica.wsbl.CompletionModeType}
	 * 
	 * @param completionModeType Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(CompletionModeType completionModeType) throws SerializerException {
		return this.objToXml(CompletionModeType.class, completionModeType, false).toString();
	}
	/**
	 * Serialize to String the object <var>completionModeType</var> of type {@link it.gov.spcoop.sica.wsbl.CompletionModeType}
	 * 
	 * @param completionModeType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(CompletionModeType completionModeType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(CompletionModeType.class, completionModeType, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: transitionType
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>transitionType</var> of type {@link it.gov.spcoop.sica.wsbl.TransitionType}
	 * 
	 * @param fileName Xml file to serialize the object <var>transitionType</var>
	 * @param transitionType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,TransitionType transitionType) throws SerializerException {
		this.objToXml(fileName, TransitionType.class, transitionType, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>transitionType</var> of type {@link it.gov.spcoop.sica.wsbl.TransitionType}
	 * 
	 * @param fileName Xml file to serialize the object <var>transitionType</var>
	 * @param transitionType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,TransitionType transitionType,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, TransitionType.class, transitionType, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>transitionType</var> of type {@link it.gov.spcoop.sica.wsbl.TransitionType}
	 * 
	 * @param file Xml file to serialize the object <var>transitionType</var>
	 * @param transitionType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,TransitionType transitionType) throws SerializerException {
		this.objToXml(file, TransitionType.class, transitionType, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>transitionType</var> of type {@link it.gov.spcoop.sica.wsbl.TransitionType}
	 * 
	 * @param file Xml file to serialize the object <var>transitionType</var>
	 * @param transitionType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,TransitionType transitionType,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, TransitionType.class, transitionType, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>transitionType</var> of type {@link it.gov.spcoop.sica.wsbl.TransitionType}
	 * 
	 * @param out OutputStream to serialize the object <var>transitionType</var>
	 * @param transitionType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,TransitionType transitionType) throws SerializerException {
		this.objToXml(out, TransitionType.class, transitionType, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>transitionType</var> of type {@link it.gov.spcoop.sica.wsbl.TransitionType}
	 * 
	 * @param out OutputStream to serialize the object <var>transitionType</var>
	 * @param transitionType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,TransitionType transitionType,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, TransitionType.class, transitionType, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>transitionType</var> of type {@link it.gov.spcoop.sica.wsbl.TransitionType}
	 * 
	 * @param transitionType Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(TransitionType transitionType) throws SerializerException {
		return this.objToXml(TransitionType.class, transitionType, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>transitionType</var> of type {@link it.gov.spcoop.sica.wsbl.TransitionType}
	 * 
	 * @param transitionType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(TransitionType transitionType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(TransitionType.class, transitionType, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>transitionType</var> of type {@link it.gov.spcoop.sica.wsbl.TransitionType}
	 * 
	 * @param transitionType Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(TransitionType transitionType) throws SerializerException {
		return this.objToXml(TransitionType.class, transitionType, false).toString();
	}
	/**
	 * Serialize to String the object <var>transitionType</var> of type {@link it.gov.spcoop.sica.wsbl.TransitionType}
	 * 
	 * @param transitionType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(TransitionType transitionType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(TransitionType.class, transitionType, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: guardType
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>guardType</var> of type {@link it.gov.spcoop.sica.wsbl.GuardType}
	 * 
	 * @param fileName Xml file to serialize the object <var>guardType</var>
	 * @param guardType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,GuardType guardType) throws SerializerException {
		this.objToXml(fileName, GuardType.class, guardType, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>guardType</var> of type {@link it.gov.spcoop.sica.wsbl.GuardType}
	 * 
	 * @param fileName Xml file to serialize the object <var>guardType</var>
	 * @param guardType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,GuardType guardType,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, GuardType.class, guardType, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>guardType</var> of type {@link it.gov.spcoop.sica.wsbl.GuardType}
	 * 
	 * @param file Xml file to serialize the object <var>guardType</var>
	 * @param guardType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,GuardType guardType) throws SerializerException {
		this.objToXml(file, GuardType.class, guardType, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>guardType</var> of type {@link it.gov.spcoop.sica.wsbl.GuardType}
	 * 
	 * @param file Xml file to serialize the object <var>guardType</var>
	 * @param guardType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,GuardType guardType,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, GuardType.class, guardType, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>guardType</var> of type {@link it.gov.spcoop.sica.wsbl.GuardType}
	 * 
	 * @param out OutputStream to serialize the object <var>guardType</var>
	 * @param guardType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,GuardType guardType) throws SerializerException {
		this.objToXml(out, GuardType.class, guardType, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>guardType</var> of type {@link it.gov.spcoop.sica.wsbl.GuardType}
	 * 
	 * @param out OutputStream to serialize the object <var>guardType</var>
	 * @param guardType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,GuardType guardType,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, GuardType.class, guardType, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>guardType</var> of type {@link it.gov.spcoop.sica.wsbl.GuardType}
	 * 
	 * @param guardType Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(GuardType guardType) throws SerializerException {
		return this.objToXml(GuardType.class, guardType, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>guardType</var> of type {@link it.gov.spcoop.sica.wsbl.GuardType}
	 * 
	 * @param guardType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(GuardType guardType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(GuardType.class, guardType, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>guardType</var> of type {@link it.gov.spcoop.sica.wsbl.GuardType}
	 * 
	 * @param guardType Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(GuardType guardType) throws SerializerException {
		return this.objToXml(GuardType.class, guardType, false).toString();
	}
	/**
	 * Serialize to String the object <var>guardType</var> of type {@link it.gov.spcoop.sica.wsbl.GuardType}
	 * 
	 * @param guardType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(GuardType guardType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(GuardType.class, guardType, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: eventListType
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>eventListType</var> of type {@link it.gov.spcoop.sica.wsbl.EventListType}
	 * 
	 * @param fileName Xml file to serialize the object <var>eventListType</var>
	 * @param eventListType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,EventListType eventListType) throws SerializerException {
		this.objToXml(fileName, EventListType.class, eventListType, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>eventListType</var> of type {@link it.gov.spcoop.sica.wsbl.EventListType}
	 * 
	 * @param fileName Xml file to serialize the object <var>eventListType</var>
	 * @param eventListType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,EventListType eventListType,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, EventListType.class, eventListType, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>eventListType</var> of type {@link it.gov.spcoop.sica.wsbl.EventListType}
	 * 
	 * @param file Xml file to serialize the object <var>eventListType</var>
	 * @param eventListType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,EventListType eventListType) throws SerializerException {
		this.objToXml(file, EventListType.class, eventListType, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>eventListType</var> of type {@link it.gov.spcoop.sica.wsbl.EventListType}
	 * 
	 * @param file Xml file to serialize the object <var>eventListType</var>
	 * @param eventListType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,EventListType eventListType,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, EventListType.class, eventListType, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>eventListType</var> of type {@link it.gov.spcoop.sica.wsbl.EventListType}
	 * 
	 * @param out OutputStream to serialize the object <var>eventListType</var>
	 * @param eventListType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,EventListType eventListType) throws SerializerException {
		this.objToXml(out, EventListType.class, eventListType, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>eventListType</var> of type {@link it.gov.spcoop.sica.wsbl.EventListType}
	 * 
	 * @param out OutputStream to serialize the object <var>eventListType</var>
	 * @param eventListType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,EventListType eventListType,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, EventListType.class, eventListType, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>eventListType</var> of type {@link it.gov.spcoop.sica.wsbl.EventListType}
	 * 
	 * @param eventListType Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(EventListType eventListType) throws SerializerException {
		return this.objToXml(EventListType.class, eventListType, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>eventListType</var> of type {@link it.gov.spcoop.sica.wsbl.EventListType}
	 * 
	 * @param eventListType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(EventListType eventListType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(EventListType.class, eventListType, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>eventListType</var> of type {@link it.gov.spcoop.sica.wsbl.EventListType}
	 * 
	 * @param eventListType Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(EventListType eventListType) throws SerializerException {
		return this.objToXml(EventListType.class, eventListType, false).toString();
	}
	/**
	 * Serialize to String the object <var>eventListType</var> of type {@link it.gov.spcoop.sica.wsbl.EventListType}
	 * 
	 * @param eventListType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(EventListType eventListType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(EventListType.class, eventListType, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: temporalConditionType
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>temporalConditionType</var> of type {@link it.gov.spcoop.sica.wsbl.TemporalConditionType}
	 * 
	 * @param fileName Xml file to serialize the object <var>temporalConditionType</var>
	 * @param temporalConditionType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,TemporalConditionType temporalConditionType) throws SerializerException {
		this.objToXml(fileName, TemporalConditionType.class, temporalConditionType, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>temporalConditionType</var> of type {@link it.gov.spcoop.sica.wsbl.TemporalConditionType}
	 * 
	 * @param fileName Xml file to serialize the object <var>temporalConditionType</var>
	 * @param temporalConditionType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,TemporalConditionType temporalConditionType,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, TemporalConditionType.class, temporalConditionType, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>temporalConditionType</var> of type {@link it.gov.spcoop.sica.wsbl.TemporalConditionType}
	 * 
	 * @param file Xml file to serialize the object <var>temporalConditionType</var>
	 * @param temporalConditionType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,TemporalConditionType temporalConditionType) throws SerializerException {
		this.objToXml(file, TemporalConditionType.class, temporalConditionType, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>temporalConditionType</var> of type {@link it.gov.spcoop.sica.wsbl.TemporalConditionType}
	 * 
	 * @param file Xml file to serialize the object <var>temporalConditionType</var>
	 * @param temporalConditionType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,TemporalConditionType temporalConditionType,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, TemporalConditionType.class, temporalConditionType, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>temporalConditionType</var> of type {@link it.gov.spcoop.sica.wsbl.TemporalConditionType}
	 * 
	 * @param out OutputStream to serialize the object <var>temporalConditionType</var>
	 * @param temporalConditionType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,TemporalConditionType temporalConditionType) throws SerializerException {
		this.objToXml(out, TemporalConditionType.class, temporalConditionType, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>temporalConditionType</var> of type {@link it.gov.spcoop.sica.wsbl.TemporalConditionType}
	 * 
	 * @param out OutputStream to serialize the object <var>temporalConditionType</var>
	 * @param temporalConditionType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,TemporalConditionType temporalConditionType,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, TemporalConditionType.class, temporalConditionType, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>temporalConditionType</var> of type {@link it.gov.spcoop.sica.wsbl.TemporalConditionType}
	 * 
	 * @param temporalConditionType Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(TemporalConditionType temporalConditionType) throws SerializerException {
		return this.objToXml(TemporalConditionType.class, temporalConditionType, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>temporalConditionType</var> of type {@link it.gov.spcoop.sica.wsbl.TemporalConditionType}
	 * 
	 * @param temporalConditionType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(TemporalConditionType temporalConditionType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(TemporalConditionType.class, temporalConditionType, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>temporalConditionType</var> of type {@link it.gov.spcoop.sica.wsbl.TemporalConditionType}
	 * 
	 * @param temporalConditionType Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(TemporalConditionType temporalConditionType) throws SerializerException {
		return this.objToXml(TemporalConditionType.class, temporalConditionType, false).toString();
	}
	/**
	 * Serialize to String the object <var>temporalConditionType</var> of type {@link it.gov.spcoop.sica.wsbl.TemporalConditionType}
	 * 
	 * @param temporalConditionType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(TemporalConditionType temporalConditionType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(TemporalConditionType.class, temporalConditionType, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: ConceptualBehavior
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>conceptualBehavior</var> of type {@link it.gov.spcoop.sica.wsbl.ConceptualBehavior}
	 * 
	 * @param fileName Xml file to serialize the object <var>conceptualBehavior</var>
	 * @param conceptualBehavior Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ConceptualBehavior conceptualBehavior) throws SerializerException {
		this.objToXml(fileName, ConceptualBehavior.class, conceptualBehavior, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>conceptualBehavior</var> of type {@link it.gov.spcoop.sica.wsbl.ConceptualBehavior}
	 * 
	 * @param fileName Xml file to serialize the object <var>conceptualBehavior</var>
	 * @param conceptualBehavior Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ConceptualBehavior conceptualBehavior,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, ConceptualBehavior.class, conceptualBehavior, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>conceptualBehavior</var> of type {@link it.gov.spcoop.sica.wsbl.ConceptualBehavior}
	 * 
	 * @param file Xml file to serialize the object <var>conceptualBehavior</var>
	 * @param conceptualBehavior Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ConceptualBehavior conceptualBehavior) throws SerializerException {
		this.objToXml(file, ConceptualBehavior.class, conceptualBehavior, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>conceptualBehavior</var> of type {@link it.gov.spcoop.sica.wsbl.ConceptualBehavior}
	 * 
	 * @param file Xml file to serialize the object <var>conceptualBehavior</var>
	 * @param conceptualBehavior Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ConceptualBehavior conceptualBehavior,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, ConceptualBehavior.class, conceptualBehavior, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>conceptualBehavior</var> of type {@link it.gov.spcoop.sica.wsbl.ConceptualBehavior}
	 * 
	 * @param out OutputStream to serialize the object <var>conceptualBehavior</var>
	 * @param conceptualBehavior Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ConceptualBehavior conceptualBehavior) throws SerializerException {
		this.objToXml(out, ConceptualBehavior.class, conceptualBehavior, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>conceptualBehavior</var> of type {@link it.gov.spcoop.sica.wsbl.ConceptualBehavior}
	 * 
	 * @param out OutputStream to serialize the object <var>conceptualBehavior</var>
	 * @param conceptualBehavior Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ConceptualBehavior conceptualBehavior,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, ConceptualBehavior.class, conceptualBehavior, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>conceptualBehavior</var> of type {@link it.gov.spcoop.sica.wsbl.ConceptualBehavior}
	 * 
	 * @param conceptualBehavior Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ConceptualBehavior conceptualBehavior) throws SerializerException {
		return this.objToXml(ConceptualBehavior.class, conceptualBehavior, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>conceptualBehavior</var> of type {@link it.gov.spcoop.sica.wsbl.ConceptualBehavior}
	 * 
	 * @param conceptualBehavior Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ConceptualBehavior conceptualBehavior,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ConceptualBehavior.class, conceptualBehavior, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>conceptualBehavior</var> of type {@link it.gov.spcoop.sica.wsbl.ConceptualBehavior}
	 * 
	 * @param conceptualBehavior Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ConceptualBehavior conceptualBehavior) throws SerializerException {
		return this.objToXml(ConceptualBehavior.class, conceptualBehavior, false).toString();
	}
	/**
	 * Serialize to String the object <var>conceptualBehavior</var> of type {@link it.gov.spcoop.sica.wsbl.ConceptualBehavior}
	 * 
	 * @param conceptualBehavior Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ConceptualBehavior conceptualBehavior,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ConceptualBehavior.class, conceptualBehavior, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: statesType
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>statesType</var> of type {@link it.gov.spcoop.sica.wsbl.StatesType}
	 * 
	 * @param fileName Xml file to serialize the object <var>statesType</var>
	 * @param statesType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,StatesType statesType) throws SerializerException {
		this.objToXml(fileName, StatesType.class, statesType, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>statesType</var> of type {@link it.gov.spcoop.sica.wsbl.StatesType}
	 * 
	 * @param fileName Xml file to serialize the object <var>statesType</var>
	 * @param statesType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,StatesType statesType,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, StatesType.class, statesType, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>statesType</var> of type {@link it.gov.spcoop.sica.wsbl.StatesType}
	 * 
	 * @param file Xml file to serialize the object <var>statesType</var>
	 * @param statesType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,StatesType statesType) throws SerializerException {
		this.objToXml(file, StatesType.class, statesType, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>statesType</var> of type {@link it.gov.spcoop.sica.wsbl.StatesType}
	 * 
	 * @param file Xml file to serialize the object <var>statesType</var>
	 * @param statesType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,StatesType statesType,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, StatesType.class, statesType, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>statesType</var> of type {@link it.gov.spcoop.sica.wsbl.StatesType}
	 * 
	 * @param out OutputStream to serialize the object <var>statesType</var>
	 * @param statesType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,StatesType statesType) throws SerializerException {
		this.objToXml(out, StatesType.class, statesType, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>statesType</var> of type {@link it.gov.spcoop.sica.wsbl.StatesType}
	 * 
	 * @param out OutputStream to serialize the object <var>statesType</var>
	 * @param statesType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,StatesType statesType,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, StatesType.class, statesType, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>statesType</var> of type {@link it.gov.spcoop.sica.wsbl.StatesType}
	 * 
	 * @param statesType Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(StatesType statesType) throws SerializerException {
		return this.objToXml(StatesType.class, statesType, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>statesType</var> of type {@link it.gov.spcoop.sica.wsbl.StatesType}
	 * 
	 * @param statesType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(StatesType statesType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(StatesType.class, statesType, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>statesType</var> of type {@link it.gov.spcoop.sica.wsbl.StatesType}
	 * 
	 * @param statesType Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(StatesType statesType) throws SerializerException {
		return this.objToXml(StatesType.class, statesType, false).toString();
	}
	/**
	 * Serialize to String the object <var>statesType</var> of type {@link it.gov.spcoop.sica.wsbl.StatesType}
	 * 
	 * @param statesType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(StatesType statesType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(StatesType.class, statesType, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: transitionsType
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>transitionsType</var> of type {@link it.gov.spcoop.sica.wsbl.TransitionsType}
	 * 
	 * @param fileName Xml file to serialize the object <var>transitionsType</var>
	 * @param transitionsType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,TransitionsType transitionsType) throws SerializerException {
		this.objToXml(fileName, TransitionsType.class, transitionsType, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>transitionsType</var> of type {@link it.gov.spcoop.sica.wsbl.TransitionsType}
	 * 
	 * @param fileName Xml file to serialize the object <var>transitionsType</var>
	 * @param transitionsType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,TransitionsType transitionsType,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, TransitionsType.class, transitionsType, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>transitionsType</var> of type {@link it.gov.spcoop.sica.wsbl.TransitionsType}
	 * 
	 * @param file Xml file to serialize the object <var>transitionsType</var>
	 * @param transitionsType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,TransitionsType transitionsType) throws SerializerException {
		this.objToXml(file, TransitionsType.class, transitionsType, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>transitionsType</var> of type {@link it.gov.spcoop.sica.wsbl.TransitionsType}
	 * 
	 * @param file Xml file to serialize the object <var>transitionsType</var>
	 * @param transitionsType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,TransitionsType transitionsType,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, TransitionsType.class, transitionsType, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>transitionsType</var> of type {@link it.gov.spcoop.sica.wsbl.TransitionsType}
	 * 
	 * @param out OutputStream to serialize the object <var>transitionsType</var>
	 * @param transitionsType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,TransitionsType transitionsType) throws SerializerException {
		this.objToXml(out, TransitionsType.class, transitionsType, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>transitionsType</var> of type {@link it.gov.spcoop.sica.wsbl.TransitionsType}
	 * 
	 * @param out OutputStream to serialize the object <var>transitionsType</var>
	 * @param transitionsType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,TransitionsType transitionsType,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, TransitionsType.class, transitionsType, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>transitionsType</var> of type {@link it.gov.spcoop.sica.wsbl.TransitionsType}
	 * 
	 * @param transitionsType Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(TransitionsType transitionsType) throws SerializerException {
		return this.objToXml(TransitionsType.class, transitionsType, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>transitionsType</var> of type {@link it.gov.spcoop.sica.wsbl.TransitionsType}
	 * 
	 * @param transitionsType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(TransitionsType transitionsType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(TransitionsType.class, transitionsType, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>transitionsType</var> of type {@link it.gov.spcoop.sica.wsbl.TransitionsType}
	 * 
	 * @param transitionsType Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(TransitionsType transitionsType) throws SerializerException {
		return this.objToXml(TransitionsType.class, transitionsType, false).toString();
	}
	/**
	 * Serialize to String the object <var>transitionsType</var> of type {@link it.gov.spcoop.sica.wsbl.TransitionsType}
	 * 
	 * @param transitionsType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(TransitionsType transitionsType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(TransitionsType.class, transitionsType, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: MessageBehavior
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>messageBehavior</var> of type {@link it.gov.spcoop.sica.wsbl.MessageBehavior}
	 * 
	 * @param fileName Xml file to serialize the object <var>messageBehavior</var>
	 * @param messageBehavior Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,MessageBehavior messageBehavior) throws SerializerException {
		this.objToXml(fileName, MessageBehavior.class, messageBehavior, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>messageBehavior</var> of type {@link it.gov.spcoop.sica.wsbl.MessageBehavior}
	 * 
	 * @param fileName Xml file to serialize the object <var>messageBehavior</var>
	 * @param messageBehavior Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,MessageBehavior messageBehavior,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, MessageBehavior.class, messageBehavior, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>messageBehavior</var> of type {@link it.gov.spcoop.sica.wsbl.MessageBehavior}
	 * 
	 * @param file Xml file to serialize the object <var>messageBehavior</var>
	 * @param messageBehavior Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,MessageBehavior messageBehavior) throws SerializerException {
		this.objToXml(file, MessageBehavior.class, messageBehavior, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>messageBehavior</var> of type {@link it.gov.spcoop.sica.wsbl.MessageBehavior}
	 * 
	 * @param file Xml file to serialize the object <var>messageBehavior</var>
	 * @param messageBehavior Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,MessageBehavior messageBehavior,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, MessageBehavior.class, messageBehavior, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>messageBehavior</var> of type {@link it.gov.spcoop.sica.wsbl.MessageBehavior}
	 * 
	 * @param out OutputStream to serialize the object <var>messageBehavior</var>
	 * @param messageBehavior Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,MessageBehavior messageBehavior) throws SerializerException {
		this.objToXml(out, MessageBehavior.class, messageBehavior, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>messageBehavior</var> of type {@link it.gov.spcoop.sica.wsbl.MessageBehavior}
	 * 
	 * @param out OutputStream to serialize the object <var>messageBehavior</var>
	 * @param messageBehavior Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,MessageBehavior messageBehavior,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, MessageBehavior.class, messageBehavior, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>messageBehavior</var> of type {@link it.gov.spcoop.sica.wsbl.MessageBehavior}
	 * 
	 * @param messageBehavior Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(MessageBehavior messageBehavior) throws SerializerException {
		return this.objToXml(MessageBehavior.class, messageBehavior, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>messageBehavior</var> of type {@link it.gov.spcoop.sica.wsbl.MessageBehavior}
	 * 
	 * @param messageBehavior Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(MessageBehavior messageBehavior,boolean prettyPrint) throws SerializerException {
		return this.objToXml(MessageBehavior.class, messageBehavior, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>messageBehavior</var> of type {@link it.gov.spcoop.sica.wsbl.MessageBehavior}
	 * 
	 * @param messageBehavior Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(MessageBehavior messageBehavior) throws SerializerException {
		return this.objToXml(MessageBehavior.class, messageBehavior, false).toString();
	}
	/**
	 * Serialize to String the object <var>messageBehavior</var> of type {@link it.gov.spcoop.sica.wsbl.MessageBehavior}
	 * 
	 * @param messageBehavior Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(MessageBehavior messageBehavior,boolean prettyPrint) throws SerializerException {
		return this.objToXml(MessageBehavior.class, messageBehavior, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: messagesTypes
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>messagesTypes</var> of type {@link it.gov.spcoop.sica.wsbl.MessagesTypes}
	 * 
	 * @param fileName Xml file to serialize the object <var>messagesTypes</var>
	 * @param messagesTypes Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,MessagesTypes messagesTypes) throws SerializerException {
		this.objToXml(fileName, MessagesTypes.class, messagesTypes, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>messagesTypes</var> of type {@link it.gov.spcoop.sica.wsbl.MessagesTypes}
	 * 
	 * @param fileName Xml file to serialize the object <var>messagesTypes</var>
	 * @param messagesTypes Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,MessagesTypes messagesTypes,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, MessagesTypes.class, messagesTypes, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>messagesTypes</var> of type {@link it.gov.spcoop.sica.wsbl.MessagesTypes}
	 * 
	 * @param file Xml file to serialize the object <var>messagesTypes</var>
	 * @param messagesTypes Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,MessagesTypes messagesTypes) throws SerializerException {
		this.objToXml(file, MessagesTypes.class, messagesTypes, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>messagesTypes</var> of type {@link it.gov.spcoop.sica.wsbl.MessagesTypes}
	 * 
	 * @param file Xml file to serialize the object <var>messagesTypes</var>
	 * @param messagesTypes Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,MessagesTypes messagesTypes,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, MessagesTypes.class, messagesTypes, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>messagesTypes</var> of type {@link it.gov.spcoop.sica.wsbl.MessagesTypes}
	 * 
	 * @param out OutputStream to serialize the object <var>messagesTypes</var>
	 * @param messagesTypes Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,MessagesTypes messagesTypes) throws SerializerException {
		this.objToXml(out, MessagesTypes.class, messagesTypes, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>messagesTypes</var> of type {@link it.gov.spcoop.sica.wsbl.MessagesTypes}
	 * 
	 * @param out OutputStream to serialize the object <var>messagesTypes</var>
	 * @param messagesTypes Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,MessagesTypes messagesTypes,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, MessagesTypes.class, messagesTypes, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>messagesTypes</var> of type {@link it.gov.spcoop.sica.wsbl.MessagesTypes}
	 * 
	 * @param messagesTypes Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(MessagesTypes messagesTypes) throws SerializerException {
		return this.objToXml(MessagesTypes.class, messagesTypes, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>messagesTypes</var> of type {@link it.gov.spcoop.sica.wsbl.MessagesTypes}
	 * 
	 * @param messagesTypes Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(MessagesTypes messagesTypes,boolean prettyPrint) throws SerializerException {
		return this.objToXml(MessagesTypes.class, messagesTypes, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>messagesTypes</var> of type {@link it.gov.spcoop.sica.wsbl.MessagesTypes}
	 * 
	 * @param messagesTypes Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(MessagesTypes messagesTypes) throws SerializerException {
		return this.objToXml(MessagesTypes.class, messagesTypes, false).toString();
	}
	/**
	 * Serialize to String the object <var>messagesTypes</var> of type {@link it.gov.spcoop.sica.wsbl.MessagesTypes}
	 * 
	 * @param messagesTypes Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(MessagesTypes messagesTypes,boolean prettyPrint) throws SerializerException {
		return this.objToXml(MessagesTypes.class, messagesTypes, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: StateTypeFinal
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>stateTypeFinal</var> of type {@link it.gov.spcoop.sica.wsbl.StateTypeFinal}
	 * 
	 * @param fileName Xml file to serialize the object <var>stateTypeFinal</var>
	 * @param stateTypeFinal Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,StateTypeFinal stateTypeFinal) throws SerializerException {
		this.objToXml(fileName, StateTypeFinal.class, stateTypeFinal, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>stateTypeFinal</var> of type {@link it.gov.spcoop.sica.wsbl.StateTypeFinal}
	 * 
	 * @param fileName Xml file to serialize the object <var>stateTypeFinal</var>
	 * @param stateTypeFinal Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,StateTypeFinal stateTypeFinal,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, StateTypeFinal.class, stateTypeFinal, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>stateTypeFinal</var> of type {@link it.gov.spcoop.sica.wsbl.StateTypeFinal}
	 * 
	 * @param file Xml file to serialize the object <var>stateTypeFinal</var>
	 * @param stateTypeFinal Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,StateTypeFinal stateTypeFinal) throws SerializerException {
		this.objToXml(file, StateTypeFinal.class, stateTypeFinal, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>stateTypeFinal</var> of type {@link it.gov.spcoop.sica.wsbl.StateTypeFinal}
	 * 
	 * @param file Xml file to serialize the object <var>stateTypeFinal</var>
	 * @param stateTypeFinal Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,StateTypeFinal stateTypeFinal,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, StateTypeFinal.class, stateTypeFinal, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>stateTypeFinal</var> of type {@link it.gov.spcoop.sica.wsbl.StateTypeFinal}
	 * 
	 * @param out OutputStream to serialize the object <var>stateTypeFinal</var>
	 * @param stateTypeFinal Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,StateTypeFinal stateTypeFinal) throws SerializerException {
		this.objToXml(out, StateTypeFinal.class, stateTypeFinal, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>stateTypeFinal</var> of type {@link it.gov.spcoop.sica.wsbl.StateTypeFinal}
	 * 
	 * @param out OutputStream to serialize the object <var>stateTypeFinal</var>
	 * @param stateTypeFinal Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,StateTypeFinal stateTypeFinal,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, StateTypeFinal.class, stateTypeFinal, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>stateTypeFinal</var> of type {@link it.gov.spcoop.sica.wsbl.StateTypeFinal}
	 * 
	 * @param stateTypeFinal Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(StateTypeFinal stateTypeFinal) throws SerializerException {
		return this.objToXml(StateTypeFinal.class, stateTypeFinal, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>stateTypeFinal</var> of type {@link it.gov.spcoop.sica.wsbl.StateTypeFinal}
	 * 
	 * @param stateTypeFinal Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(StateTypeFinal stateTypeFinal,boolean prettyPrint) throws SerializerException {
		return this.objToXml(StateTypeFinal.class, stateTypeFinal, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>stateTypeFinal</var> of type {@link it.gov.spcoop.sica.wsbl.StateTypeFinal}
	 * 
	 * @param stateTypeFinal Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(StateTypeFinal stateTypeFinal) throws SerializerException {
		return this.objToXml(StateTypeFinal.class, stateTypeFinal, false).toString();
	}
	/**
	 * Serialize to String the object <var>stateTypeFinal</var> of type {@link it.gov.spcoop.sica.wsbl.StateTypeFinal}
	 * 
	 * @param stateTypeFinal Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(StateTypeFinal stateTypeFinal,boolean prettyPrint) throws SerializerException {
		return this.objToXml(StateTypeFinal.class, stateTypeFinal, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: message
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>message</var> of type {@link it.gov.spcoop.sica.wsbl.Message}
	 * 
	 * @param fileName Xml file to serialize the object <var>message</var>
	 * @param message Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Message message) throws SerializerException {
		this.objToXml(fileName, Message.class, message, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>message</var> of type {@link it.gov.spcoop.sica.wsbl.Message}
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
	 * Serialize to file system in <var>file</var> the object <var>message</var> of type {@link it.gov.spcoop.sica.wsbl.Message}
	 * 
	 * @param file Xml file to serialize the object <var>message</var>
	 * @param message Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Message message) throws SerializerException {
		this.objToXml(file, Message.class, message, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>message</var> of type {@link it.gov.spcoop.sica.wsbl.Message}
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
	 * Serialize to output stream <var>out</var> the object <var>message</var> of type {@link it.gov.spcoop.sica.wsbl.Message}
	 * 
	 * @param out OutputStream to serialize the object <var>message</var>
	 * @param message Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Message message) throws SerializerException {
		this.objToXml(out, Message.class, message, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>message</var> of type {@link it.gov.spcoop.sica.wsbl.Message}
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
	 * Serialize to byte array the object <var>message</var> of type {@link it.gov.spcoop.sica.wsbl.Message}
	 * 
	 * @param message Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Message message) throws SerializerException {
		return this.objToXml(Message.class, message, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>message</var> of type {@link it.gov.spcoop.sica.wsbl.Message}
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
	 * Serialize to String the object <var>message</var> of type {@link it.gov.spcoop.sica.wsbl.Message}
	 * 
	 * @param message Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Message message) throws SerializerException {
		return this.objToXml(Message.class, message, false).toString();
	}
	/**
	 * Serialize to String the object <var>message</var> of type {@link it.gov.spcoop.sica.wsbl.Message}
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
	 Object: StateTypeNormal
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>stateTypeNormal</var> of type {@link it.gov.spcoop.sica.wsbl.StateTypeNormal}
	 * 
	 * @param fileName Xml file to serialize the object <var>stateTypeNormal</var>
	 * @param stateTypeNormal Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,StateTypeNormal stateTypeNormal) throws SerializerException {
		this.objToXml(fileName, StateTypeNormal.class, stateTypeNormal, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>stateTypeNormal</var> of type {@link it.gov.spcoop.sica.wsbl.StateTypeNormal}
	 * 
	 * @param fileName Xml file to serialize the object <var>stateTypeNormal</var>
	 * @param stateTypeNormal Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,StateTypeNormal stateTypeNormal,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, StateTypeNormal.class, stateTypeNormal, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>stateTypeNormal</var> of type {@link it.gov.spcoop.sica.wsbl.StateTypeNormal}
	 * 
	 * @param file Xml file to serialize the object <var>stateTypeNormal</var>
	 * @param stateTypeNormal Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,StateTypeNormal stateTypeNormal) throws SerializerException {
		this.objToXml(file, StateTypeNormal.class, stateTypeNormal, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>stateTypeNormal</var> of type {@link it.gov.spcoop.sica.wsbl.StateTypeNormal}
	 * 
	 * @param file Xml file to serialize the object <var>stateTypeNormal</var>
	 * @param stateTypeNormal Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,StateTypeNormal stateTypeNormal,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, StateTypeNormal.class, stateTypeNormal, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>stateTypeNormal</var> of type {@link it.gov.spcoop.sica.wsbl.StateTypeNormal}
	 * 
	 * @param out OutputStream to serialize the object <var>stateTypeNormal</var>
	 * @param stateTypeNormal Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,StateTypeNormal stateTypeNormal) throws SerializerException {
		this.objToXml(out, StateTypeNormal.class, stateTypeNormal, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>stateTypeNormal</var> of type {@link it.gov.spcoop.sica.wsbl.StateTypeNormal}
	 * 
	 * @param out OutputStream to serialize the object <var>stateTypeNormal</var>
	 * @param stateTypeNormal Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,StateTypeNormal stateTypeNormal,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, StateTypeNormal.class, stateTypeNormal, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>stateTypeNormal</var> of type {@link it.gov.spcoop.sica.wsbl.StateTypeNormal}
	 * 
	 * @param stateTypeNormal Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(StateTypeNormal stateTypeNormal) throws SerializerException {
		return this.objToXml(StateTypeNormal.class, stateTypeNormal, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>stateTypeNormal</var> of type {@link it.gov.spcoop.sica.wsbl.StateTypeNormal}
	 * 
	 * @param stateTypeNormal Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(StateTypeNormal stateTypeNormal,boolean prettyPrint) throws SerializerException {
		return this.objToXml(StateTypeNormal.class, stateTypeNormal, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>stateTypeNormal</var> of type {@link it.gov.spcoop.sica.wsbl.StateTypeNormal}
	 * 
	 * @param stateTypeNormal Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(StateTypeNormal stateTypeNormal) throws SerializerException {
		return this.objToXml(StateTypeNormal.class, stateTypeNormal, false).toString();
	}
	/**
	 * Serialize to String the object <var>stateTypeNormal</var> of type {@link it.gov.spcoop.sica.wsbl.StateTypeNormal}
	 * 
	 * @param stateTypeNormal Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(StateTypeNormal stateTypeNormal,boolean prettyPrint) throws SerializerException {
		return this.objToXml(StateTypeNormal.class, stateTypeNormal, prettyPrint).toString();
	}
	
	
	

}
