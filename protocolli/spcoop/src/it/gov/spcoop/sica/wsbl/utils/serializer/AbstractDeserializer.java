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
package it.gov.spcoop.sica.wsbl.utils.serializer;

import org.openspcoop2.generic_project.exception.DeserializerException;

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

import java.io.InputStream;
import java.io.File;

/**     
 * XML Deserializer of beans
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public abstract class AbstractDeserializer extends org.openspcoop2.generic_project.serializer.AbstractDeserializerBase {



	/*
	 =================================================================================
	 Object: eventTypeMessage
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link it.gov.spcoop.sica.wsbl.EventTypeMessage}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.gov.spcoop.sica.wsbl.EventTypeMessage}
	 * @return Object type {@link it.gov.spcoop.sica.wsbl.EventTypeMessage}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public EventTypeMessage readEventTypeMessage(String fileName) throws DeserializerException {
		return (EventTypeMessage) this.xmlToObj(fileName, EventTypeMessage.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.gov.spcoop.sica.wsbl.EventTypeMessage}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.gov.spcoop.sica.wsbl.EventTypeMessage}
	 * @return Object type {@link it.gov.spcoop.sica.wsbl.EventTypeMessage}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public EventTypeMessage readEventTypeMessage(File file) throws DeserializerException {
		return (EventTypeMessage) this.xmlToObj(file, EventTypeMessage.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.gov.spcoop.sica.wsbl.EventTypeMessage}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.gov.spcoop.sica.wsbl.EventTypeMessage}
	 * @return Object type {@link it.gov.spcoop.sica.wsbl.EventTypeMessage}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public EventTypeMessage readEventTypeMessage(InputStream in) throws DeserializerException {
		return (EventTypeMessage) this.xmlToObj(in, EventTypeMessage.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.gov.spcoop.sica.wsbl.EventTypeMessage}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.gov.spcoop.sica.wsbl.EventTypeMessage}
	 * @return Object type {@link it.gov.spcoop.sica.wsbl.EventTypeMessage}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public EventTypeMessage readEventTypeMessage(byte[] in) throws DeserializerException {
		return (EventTypeMessage) this.xmlToObj(in, EventTypeMessage.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.gov.spcoop.sica.wsbl.EventTypeMessage}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.gov.spcoop.sica.wsbl.EventTypeMessage}
	 * @return Object type {@link it.gov.spcoop.sica.wsbl.EventTypeMessage}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public EventTypeMessage readEventTypeMessageFromString(String in) throws DeserializerException {
		return (EventTypeMessage) this.xmlToObj(in.getBytes(), EventTypeMessage.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: eventType
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link it.gov.spcoop.sica.wsbl.EventType}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.gov.spcoop.sica.wsbl.EventType}
	 * @return Object type {@link it.gov.spcoop.sica.wsbl.EventType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public EventType readEventType(String fileName) throws DeserializerException {
		return (EventType) this.xmlToObj(fileName, EventType.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.gov.spcoop.sica.wsbl.EventType}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.gov.spcoop.sica.wsbl.EventType}
	 * @return Object type {@link it.gov.spcoop.sica.wsbl.EventType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public EventType readEventType(File file) throws DeserializerException {
		return (EventType) this.xmlToObj(file, EventType.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.gov.spcoop.sica.wsbl.EventType}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.gov.spcoop.sica.wsbl.EventType}
	 * @return Object type {@link it.gov.spcoop.sica.wsbl.EventType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public EventType readEventType(InputStream in) throws DeserializerException {
		return (EventType) this.xmlToObj(in, EventType.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.gov.spcoop.sica.wsbl.EventType}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.gov.spcoop.sica.wsbl.EventType}
	 * @return Object type {@link it.gov.spcoop.sica.wsbl.EventType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public EventType readEventType(byte[] in) throws DeserializerException {
		return (EventType) this.xmlToObj(in, EventType.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.gov.spcoop.sica.wsbl.EventType}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.gov.spcoop.sica.wsbl.EventType}
	 * @return Object type {@link it.gov.spcoop.sica.wsbl.EventType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public EventType readEventTypeFromString(String in) throws DeserializerException {
		return (EventType) this.xmlToObj(in.getBytes(), EventType.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: StateTypeInitial
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link it.gov.spcoop.sica.wsbl.StateTypeInitial}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.gov.spcoop.sica.wsbl.StateTypeInitial}
	 * @return Object type {@link it.gov.spcoop.sica.wsbl.StateTypeInitial}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public StateTypeInitial readStateTypeInitial(String fileName) throws DeserializerException {
		return (StateTypeInitial) this.xmlToObj(fileName, StateTypeInitial.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.gov.spcoop.sica.wsbl.StateTypeInitial}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.gov.spcoop.sica.wsbl.StateTypeInitial}
	 * @return Object type {@link it.gov.spcoop.sica.wsbl.StateTypeInitial}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public StateTypeInitial readStateTypeInitial(File file) throws DeserializerException {
		return (StateTypeInitial) this.xmlToObj(file, StateTypeInitial.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.gov.spcoop.sica.wsbl.StateTypeInitial}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.gov.spcoop.sica.wsbl.StateTypeInitial}
	 * @return Object type {@link it.gov.spcoop.sica.wsbl.StateTypeInitial}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public StateTypeInitial readStateTypeInitial(InputStream in) throws DeserializerException {
		return (StateTypeInitial) this.xmlToObj(in, StateTypeInitial.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.gov.spcoop.sica.wsbl.StateTypeInitial}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.gov.spcoop.sica.wsbl.StateTypeInitial}
	 * @return Object type {@link it.gov.spcoop.sica.wsbl.StateTypeInitial}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public StateTypeInitial readStateTypeInitial(byte[] in) throws DeserializerException {
		return (StateTypeInitial) this.xmlToObj(in, StateTypeInitial.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.gov.spcoop.sica.wsbl.StateTypeInitial}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.gov.spcoop.sica.wsbl.StateTypeInitial}
	 * @return Object type {@link it.gov.spcoop.sica.wsbl.StateTypeInitial}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public StateTypeInitial readStateTypeInitialFromString(String in) throws DeserializerException {
		return (StateTypeInitial) this.xmlToObj(in.getBytes(), StateTypeInitial.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: completionModeTypeCompensateMessage
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link it.gov.spcoop.sica.wsbl.CompletionModeTypeCompensateMessage}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.gov.spcoop.sica.wsbl.CompletionModeTypeCompensateMessage}
	 * @return Object type {@link it.gov.spcoop.sica.wsbl.CompletionModeTypeCompensateMessage}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public CompletionModeTypeCompensateMessage readCompletionModeTypeCompensateMessage(String fileName) throws DeserializerException {
		return (CompletionModeTypeCompensateMessage) this.xmlToObj(fileName, CompletionModeTypeCompensateMessage.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.gov.spcoop.sica.wsbl.CompletionModeTypeCompensateMessage}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.gov.spcoop.sica.wsbl.CompletionModeTypeCompensateMessage}
	 * @return Object type {@link it.gov.spcoop.sica.wsbl.CompletionModeTypeCompensateMessage}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public CompletionModeTypeCompensateMessage readCompletionModeTypeCompensateMessage(File file) throws DeserializerException {
		return (CompletionModeTypeCompensateMessage) this.xmlToObj(file, CompletionModeTypeCompensateMessage.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.gov.spcoop.sica.wsbl.CompletionModeTypeCompensateMessage}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.gov.spcoop.sica.wsbl.CompletionModeTypeCompensateMessage}
	 * @return Object type {@link it.gov.spcoop.sica.wsbl.CompletionModeTypeCompensateMessage}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public CompletionModeTypeCompensateMessage readCompletionModeTypeCompensateMessage(InputStream in) throws DeserializerException {
		return (CompletionModeTypeCompensateMessage) this.xmlToObj(in, CompletionModeTypeCompensateMessage.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.gov.spcoop.sica.wsbl.CompletionModeTypeCompensateMessage}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.gov.spcoop.sica.wsbl.CompletionModeTypeCompensateMessage}
	 * @return Object type {@link it.gov.spcoop.sica.wsbl.CompletionModeTypeCompensateMessage}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public CompletionModeTypeCompensateMessage readCompletionModeTypeCompensateMessage(byte[] in) throws DeserializerException {
		return (CompletionModeTypeCompensateMessage) this.xmlToObj(in, CompletionModeTypeCompensateMessage.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.gov.spcoop.sica.wsbl.CompletionModeTypeCompensateMessage}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.gov.spcoop.sica.wsbl.CompletionModeTypeCompensateMessage}
	 * @return Object type {@link it.gov.spcoop.sica.wsbl.CompletionModeTypeCompensateMessage}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public CompletionModeTypeCompensateMessage readCompletionModeTypeCompensateMessageFromString(String in) throws DeserializerException {
		return (CompletionModeTypeCompensateMessage) this.xmlToObj(in.getBytes(), CompletionModeTypeCompensateMessage.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: completionModeType
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link it.gov.spcoop.sica.wsbl.CompletionModeType}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.gov.spcoop.sica.wsbl.CompletionModeType}
	 * @return Object type {@link it.gov.spcoop.sica.wsbl.CompletionModeType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public CompletionModeType readCompletionModeType(String fileName) throws DeserializerException {
		return (CompletionModeType) this.xmlToObj(fileName, CompletionModeType.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.gov.spcoop.sica.wsbl.CompletionModeType}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.gov.spcoop.sica.wsbl.CompletionModeType}
	 * @return Object type {@link it.gov.spcoop.sica.wsbl.CompletionModeType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public CompletionModeType readCompletionModeType(File file) throws DeserializerException {
		return (CompletionModeType) this.xmlToObj(file, CompletionModeType.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.gov.spcoop.sica.wsbl.CompletionModeType}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.gov.spcoop.sica.wsbl.CompletionModeType}
	 * @return Object type {@link it.gov.spcoop.sica.wsbl.CompletionModeType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public CompletionModeType readCompletionModeType(InputStream in) throws DeserializerException {
		return (CompletionModeType) this.xmlToObj(in, CompletionModeType.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.gov.spcoop.sica.wsbl.CompletionModeType}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.gov.spcoop.sica.wsbl.CompletionModeType}
	 * @return Object type {@link it.gov.spcoop.sica.wsbl.CompletionModeType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public CompletionModeType readCompletionModeType(byte[] in) throws DeserializerException {
		return (CompletionModeType) this.xmlToObj(in, CompletionModeType.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.gov.spcoop.sica.wsbl.CompletionModeType}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.gov.spcoop.sica.wsbl.CompletionModeType}
	 * @return Object type {@link it.gov.spcoop.sica.wsbl.CompletionModeType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public CompletionModeType readCompletionModeTypeFromString(String in) throws DeserializerException {
		return (CompletionModeType) this.xmlToObj(in.getBytes(), CompletionModeType.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: transitionType
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link it.gov.spcoop.sica.wsbl.TransitionType}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.gov.spcoop.sica.wsbl.TransitionType}
	 * @return Object type {@link it.gov.spcoop.sica.wsbl.TransitionType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public TransitionType readTransitionType(String fileName) throws DeserializerException {
		return (TransitionType) this.xmlToObj(fileName, TransitionType.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.gov.spcoop.sica.wsbl.TransitionType}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.gov.spcoop.sica.wsbl.TransitionType}
	 * @return Object type {@link it.gov.spcoop.sica.wsbl.TransitionType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public TransitionType readTransitionType(File file) throws DeserializerException {
		return (TransitionType) this.xmlToObj(file, TransitionType.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.gov.spcoop.sica.wsbl.TransitionType}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.gov.spcoop.sica.wsbl.TransitionType}
	 * @return Object type {@link it.gov.spcoop.sica.wsbl.TransitionType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public TransitionType readTransitionType(InputStream in) throws DeserializerException {
		return (TransitionType) this.xmlToObj(in, TransitionType.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.gov.spcoop.sica.wsbl.TransitionType}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.gov.spcoop.sica.wsbl.TransitionType}
	 * @return Object type {@link it.gov.spcoop.sica.wsbl.TransitionType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public TransitionType readTransitionType(byte[] in) throws DeserializerException {
		return (TransitionType) this.xmlToObj(in, TransitionType.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.gov.spcoop.sica.wsbl.TransitionType}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.gov.spcoop.sica.wsbl.TransitionType}
	 * @return Object type {@link it.gov.spcoop.sica.wsbl.TransitionType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public TransitionType readTransitionTypeFromString(String in) throws DeserializerException {
		return (TransitionType) this.xmlToObj(in.getBytes(), TransitionType.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: guardType
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link it.gov.spcoop.sica.wsbl.GuardType}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.gov.spcoop.sica.wsbl.GuardType}
	 * @return Object type {@link it.gov.spcoop.sica.wsbl.GuardType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public GuardType readGuardType(String fileName) throws DeserializerException {
		return (GuardType) this.xmlToObj(fileName, GuardType.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.gov.spcoop.sica.wsbl.GuardType}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.gov.spcoop.sica.wsbl.GuardType}
	 * @return Object type {@link it.gov.spcoop.sica.wsbl.GuardType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public GuardType readGuardType(File file) throws DeserializerException {
		return (GuardType) this.xmlToObj(file, GuardType.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.gov.spcoop.sica.wsbl.GuardType}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.gov.spcoop.sica.wsbl.GuardType}
	 * @return Object type {@link it.gov.spcoop.sica.wsbl.GuardType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public GuardType readGuardType(InputStream in) throws DeserializerException {
		return (GuardType) this.xmlToObj(in, GuardType.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.gov.spcoop.sica.wsbl.GuardType}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.gov.spcoop.sica.wsbl.GuardType}
	 * @return Object type {@link it.gov.spcoop.sica.wsbl.GuardType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public GuardType readGuardType(byte[] in) throws DeserializerException {
		return (GuardType) this.xmlToObj(in, GuardType.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.gov.spcoop.sica.wsbl.GuardType}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.gov.spcoop.sica.wsbl.GuardType}
	 * @return Object type {@link it.gov.spcoop.sica.wsbl.GuardType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public GuardType readGuardTypeFromString(String in) throws DeserializerException {
		return (GuardType) this.xmlToObj(in.getBytes(), GuardType.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: eventListType
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link it.gov.spcoop.sica.wsbl.EventListType}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.gov.spcoop.sica.wsbl.EventListType}
	 * @return Object type {@link it.gov.spcoop.sica.wsbl.EventListType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public EventListType readEventListType(String fileName) throws DeserializerException {
		return (EventListType) this.xmlToObj(fileName, EventListType.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.gov.spcoop.sica.wsbl.EventListType}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.gov.spcoop.sica.wsbl.EventListType}
	 * @return Object type {@link it.gov.spcoop.sica.wsbl.EventListType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public EventListType readEventListType(File file) throws DeserializerException {
		return (EventListType) this.xmlToObj(file, EventListType.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.gov.spcoop.sica.wsbl.EventListType}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.gov.spcoop.sica.wsbl.EventListType}
	 * @return Object type {@link it.gov.spcoop.sica.wsbl.EventListType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public EventListType readEventListType(InputStream in) throws DeserializerException {
		return (EventListType) this.xmlToObj(in, EventListType.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.gov.spcoop.sica.wsbl.EventListType}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.gov.spcoop.sica.wsbl.EventListType}
	 * @return Object type {@link it.gov.spcoop.sica.wsbl.EventListType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public EventListType readEventListType(byte[] in) throws DeserializerException {
		return (EventListType) this.xmlToObj(in, EventListType.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.gov.spcoop.sica.wsbl.EventListType}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.gov.spcoop.sica.wsbl.EventListType}
	 * @return Object type {@link it.gov.spcoop.sica.wsbl.EventListType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public EventListType readEventListTypeFromString(String in) throws DeserializerException {
		return (EventListType) this.xmlToObj(in.getBytes(), EventListType.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: temporalConditionType
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link it.gov.spcoop.sica.wsbl.TemporalConditionType}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.gov.spcoop.sica.wsbl.TemporalConditionType}
	 * @return Object type {@link it.gov.spcoop.sica.wsbl.TemporalConditionType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public TemporalConditionType readTemporalConditionType(String fileName) throws DeserializerException {
		return (TemporalConditionType) this.xmlToObj(fileName, TemporalConditionType.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.gov.spcoop.sica.wsbl.TemporalConditionType}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.gov.spcoop.sica.wsbl.TemporalConditionType}
	 * @return Object type {@link it.gov.spcoop.sica.wsbl.TemporalConditionType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public TemporalConditionType readTemporalConditionType(File file) throws DeserializerException {
		return (TemporalConditionType) this.xmlToObj(file, TemporalConditionType.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.gov.spcoop.sica.wsbl.TemporalConditionType}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.gov.spcoop.sica.wsbl.TemporalConditionType}
	 * @return Object type {@link it.gov.spcoop.sica.wsbl.TemporalConditionType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public TemporalConditionType readTemporalConditionType(InputStream in) throws DeserializerException {
		return (TemporalConditionType) this.xmlToObj(in, TemporalConditionType.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.gov.spcoop.sica.wsbl.TemporalConditionType}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.gov.spcoop.sica.wsbl.TemporalConditionType}
	 * @return Object type {@link it.gov.spcoop.sica.wsbl.TemporalConditionType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public TemporalConditionType readTemporalConditionType(byte[] in) throws DeserializerException {
		return (TemporalConditionType) this.xmlToObj(in, TemporalConditionType.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.gov.spcoop.sica.wsbl.TemporalConditionType}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.gov.spcoop.sica.wsbl.TemporalConditionType}
	 * @return Object type {@link it.gov.spcoop.sica.wsbl.TemporalConditionType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public TemporalConditionType readTemporalConditionTypeFromString(String in) throws DeserializerException {
		return (TemporalConditionType) this.xmlToObj(in.getBytes(), TemporalConditionType.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: ConceptualBehavior
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link it.gov.spcoop.sica.wsbl.ConceptualBehavior}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.gov.spcoop.sica.wsbl.ConceptualBehavior}
	 * @return Object type {@link it.gov.spcoop.sica.wsbl.ConceptualBehavior}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConceptualBehavior readConceptualBehavior(String fileName) throws DeserializerException {
		return (ConceptualBehavior) this.xmlToObj(fileName, ConceptualBehavior.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.gov.spcoop.sica.wsbl.ConceptualBehavior}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.gov.spcoop.sica.wsbl.ConceptualBehavior}
	 * @return Object type {@link it.gov.spcoop.sica.wsbl.ConceptualBehavior}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConceptualBehavior readConceptualBehavior(File file) throws DeserializerException {
		return (ConceptualBehavior) this.xmlToObj(file, ConceptualBehavior.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.gov.spcoop.sica.wsbl.ConceptualBehavior}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.gov.spcoop.sica.wsbl.ConceptualBehavior}
	 * @return Object type {@link it.gov.spcoop.sica.wsbl.ConceptualBehavior}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConceptualBehavior readConceptualBehavior(InputStream in) throws DeserializerException {
		return (ConceptualBehavior) this.xmlToObj(in, ConceptualBehavior.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.gov.spcoop.sica.wsbl.ConceptualBehavior}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.gov.spcoop.sica.wsbl.ConceptualBehavior}
	 * @return Object type {@link it.gov.spcoop.sica.wsbl.ConceptualBehavior}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConceptualBehavior readConceptualBehavior(byte[] in) throws DeserializerException {
		return (ConceptualBehavior) this.xmlToObj(in, ConceptualBehavior.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.gov.spcoop.sica.wsbl.ConceptualBehavior}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.gov.spcoop.sica.wsbl.ConceptualBehavior}
	 * @return Object type {@link it.gov.spcoop.sica.wsbl.ConceptualBehavior}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConceptualBehavior readConceptualBehaviorFromString(String in) throws DeserializerException {
		return (ConceptualBehavior) this.xmlToObj(in.getBytes(), ConceptualBehavior.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: statesType
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link it.gov.spcoop.sica.wsbl.StatesType}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.gov.spcoop.sica.wsbl.StatesType}
	 * @return Object type {@link it.gov.spcoop.sica.wsbl.StatesType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public StatesType readStatesType(String fileName) throws DeserializerException {
		return (StatesType) this.xmlToObj(fileName, StatesType.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.gov.spcoop.sica.wsbl.StatesType}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.gov.spcoop.sica.wsbl.StatesType}
	 * @return Object type {@link it.gov.spcoop.sica.wsbl.StatesType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public StatesType readStatesType(File file) throws DeserializerException {
		return (StatesType) this.xmlToObj(file, StatesType.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.gov.spcoop.sica.wsbl.StatesType}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.gov.spcoop.sica.wsbl.StatesType}
	 * @return Object type {@link it.gov.spcoop.sica.wsbl.StatesType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public StatesType readStatesType(InputStream in) throws DeserializerException {
		return (StatesType) this.xmlToObj(in, StatesType.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.gov.spcoop.sica.wsbl.StatesType}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.gov.spcoop.sica.wsbl.StatesType}
	 * @return Object type {@link it.gov.spcoop.sica.wsbl.StatesType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public StatesType readStatesType(byte[] in) throws DeserializerException {
		return (StatesType) this.xmlToObj(in, StatesType.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.gov.spcoop.sica.wsbl.StatesType}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.gov.spcoop.sica.wsbl.StatesType}
	 * @return Object type {@link it.gov.spcoop.sica.wsbl.StatesType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public StatesType readStatesTypeFromString(String in) throws DeserializerException {
		return (StatesType) this.xmlToObj(in.getBytes(), StatesType.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: transitionsType
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link it.gov.spcoop.sica.wsbl.TransitionsType}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.gov.spcoop.sica.wsbl.TransitionsType}
	 * @return Object type {@link it.gov.spcoop.sica.wsbl.TransitionsType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public TransitionsType readTransitionsType(String fileName) throws DeserializerException {
		return (TransitionsType) this.xmlToObj(fileName, TransitionsType.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.gov.spcoop.sica.wsbl.TransitionsType}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.gov.spcoop.sica.wsbl.TransitionsType}
	 * @return Object type {@link it.gov.spcoop.sica.wsbl.TransitionsType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public TransitionsType readTransitionsType(File file) throws DeserializerException {
		return (TransitionsType) this.xmlToObj(file, TransitionsType.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.gov.spcoop.sica.wsbl.TransitionsType}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.gov.spcoop.sica.wsbl.TransitionsType}
	 * @return Object type {@link it.gov.spcoop.sica.wsbl.TransitionsType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public TransitionsType readTransitionsType(InputStream in) throws DeserializerException {
		return (TransitionsType) this.xmlToObj(in, TransitionsType.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.gov.spcoop.sica.wsbl.TransitionsType}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.gov.spcoop.sica.wsbl.TransitionsType}
	 * @return Object type {@link it.gov.spcoop.sica.wsbl.TransitionsType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public TransitionsType readTransitionsType(byte[] in) throws DeserializerException {
		return (TransitionsType) this.xmlToObj(in, TransitionsType.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.gov.spcoop.sica.wsbl.TransitionsType}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.gov.spcoop.sica.wsbl.TransitionsType}
	 * @return Object type {@link it.gov.spcoop.sica.wsbl.TransitionsType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public TransitionsType readTransitionsTypeFromString(String in) throws DeserializerException {
		return (TransitionsType) this.xmlToObj(in.getBytes(), TransitionsType.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: MessageBehavior
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link it.gov.spcoop.sica.wsbl.MessageBehavior}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.gov.spcoop.sica.wsbl.MessageBehavior}
	 * @return Object type {@link it.gov.spcoop.sica.wsbl.MessageBehavior}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public MessageBehavior readMessageBehavior(String fileName) throws DeserializerException {
		return (MessageBehavior) this.xmlToObj(fileName, MessageBehavior.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.gov.spcoop.sica.wsbl.MessageBehavior}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.gov.spcoop.sica.wsbl.MessageBehavior}
	 * @return Object type {@link it.gov.spcoop.sica.wsbl.MessageBehavior}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public MessageBehavior readMessageBehavior(File file) throws DeserializerException {
		return (MessageBehavior) this.xmlToObj(file, MessageBehavior.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.gov.spcoop.sica.wsbl.MessageBehavior}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.gov.spcoop.sica.wsbl.MessageBehavior}
	 * @return Object type {@link it.gov.spcoop.sica.wsbl.MessageBehavior}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public MessageBehavior readMessageBehavior(InputStream in) throws DeserializerException {
		return (MessageBehavior) this.xmlToObj(in, MessageBehavior.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.gov.spcoop.sica.wsbl.MessageBehavior}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.gov.spcoop.sica.wsbl.MessageBehavior}
	 * @return Object type {@link it.gov.spcoop.sica.wsbl.MessageBehavior}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public MessageBehavior readMessageBehavior(byte[] in) throws DeserializerException {
		return (MessageBehavior) this.xmlToObj(in, MessageBehavior.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.gov.spcoop.sica.wsbl.MessageBehavior}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.gov.spcoop.sica.wsbl.MessageBehavior}
	 * @return Object type {@link it.gov.spcoop.sica.wsbl.MessageBehavior}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public MessageBehavior readMessageBehaviorFromString(String in) throws DeserializerException {
		return (MessageBehavior) this.xmlToObj(in.getBytes(), MessageBehavior.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: messagesTypes
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link it.gov.spcoop.sica.wsbl.MessagesTypes}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.gov.spcoop.sica.wsbl.MessagesTypes}
	 * @return Object type {@link it.gov.spcoop.sica.wsbl.MessagesTypes}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public MessagesTypes readMessagesTypes(String fileName) throws DeserializerException {
		return (MessagesTypes) this.xmlToObj(fileName, MessagesTypes.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.gov.spcoop.sica.wsbl.MessagesTypes}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.gov.spcoop.sica.wsbl.MessagesTypes}
	 * @return Object type {@link it.gov.spcoop.sica.wsbl.MessagesTypes}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public MessagesTypes readMessagesTypes(File file) throws DeserializerException {
		return (MessagesTypes) this.xmlToObj(file, MessagesTypes.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.gov.spcoop.sica.wsbl.MessagesTypes}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.gov.spcoop.sica.wsbl.MessagesTypes}
	 * @return Object type {@link it.gov.spcoop.sica.wsbl.MessagesTypes}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public MessagesTypes readMessagesTypes(InputStream in) throws DeserializerException {
		return (MessagesTypes) this.xmlToObj(in, MessagesTypes.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.gov.spcoop.sica.wsbl.MessagesTypes}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.gov.spcoop.sica.wsbl.MessagesTypes}
	 * @return Object type {@link it.gov.spcoop.sica.wsbl.MessagesTypes}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public MessagesTypes readMessagesTypes(byte[] in) throws DeserializerException {
		return (MessagesTypes) this.xmlToObj(in, MessagesTypes.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.gov.spcoop.sica.wsbl.MessagesTypes}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.gov.spcoop.sica.wsbl.MessagesTypes}
	 * @return Object type {@link it.gov.spcoop.sica.wsbl.MessagesTypes}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public MessagesTypes readMessagesTypesFromString(String in) throws DeserializerException {
		return (MessagesTypes) this.xmlToObj(in.getBytes(), MessagesTypes.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: StateTypeFinal
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link it.gov.spcoop.sica.wsbl.StateTypeFinal}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.gov.spcoop.sica.wsbl.StateTypeFinal}
	 * @return Object type {@link it.gov.spcoop.sica.wsbl.StateTypeFinal}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public StateTypeFinal readStateTypeFinal(String fileName) throws DeserializerException {
		return (StateTypeFinal) this.xmlToObj(fileName, StateTypeFinal.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.gov.spcoop.sica.wsbl.StateTypeFinal}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.gov.spcoop.sica.wsbl.StateTypeFinal}
	 * @return Object type {@link it.gov.spcoop.sica.wsbl.StateTypeFinal}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public StateTypeFinal readStateTypeFinal(File file) throws DeserializerException {
		return (StateTypeFinal) this.xmlToObj(file, StateTypeFinal.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.gov.spcoop.sica.wsbl.StateTypeFinal}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.gov.spcoop.sica.wsbl.StateTypeFinal}
	 * @return Object type {@link it.gov.spcoop.sica.wsbl.StateTypeFinal}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public StateTypeFinal readStateTypeFinal(InputStream in) throws DeserializerException {
		return (StateTypeFinal) this.xmlToObj(in, StateTypeFinal.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.gov.spcoop.sica.wsbl.StateTypeFinal}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.gov.spcoop.sica.wsbl.StateTypeFinal}
	 * @return Object type {@link it.gov.spcoop.sica.wsbl.StateTypeFinal}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public StateTypeFinal readStateTypeFinal(byte[] in) throws DeserializerException {
		return (StateTypeFinal) this.xmlToObj(in, StateTypeFinal.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.gov.spcoop.sica.wsbl.StateTypeFinal}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.gov.spcoop.sica.wsbl.StateTypeFinal}
	 * @return Object type {@link it.gov.spcoop.sica.wsbl.StateTypeFinal}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public StateTypeFinal readStateTypeFinalFromString(String in) throws DeserializerException {
		return (StateTypeFinal) this.xmlToObj(in.getBytes(), StateTypeFinal.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: message
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link it.gov.spcoop.sica.wsbl.Message}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.gov.spcoop.sica.wsbl.Message}
	 * @return Object type {@link it.gov.spcoop.sica.wsbl.Message}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Message readMessage(String fileName) throws DeserializerException {
		return (Message) this.xmlToObj(fileName, Message.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.gov.spcoop.sica.wsbl.Message}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.gov.spcoop.sica.wsbl.Message}
	 * @return Object type {@link it.gov.spcoop.sica.wsbl.Message}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Message readMessage(File file) throws DeserializerException {
		return (Message) this.xmlToObj(file, Message.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.gov.spcoop.sica.wsbl.Message}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.gov.spcoop.sica.wsbl.Message}
	 * @return Object type {@link it.gov.spcoop.sica.wsbl.Message}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Message readMessage(InputStream in) throws DeserializerException {
		return (Message) this.xmlToObj(in, Message.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.gov.spcoop.sica.wsbl.Message}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.gov.spcoop.sica.wsbl.Message}
	 * @return Object type {@link it.gov.spcoop.sica.wsbl.Message}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Message readMessage(byte[] in) throws DeserializerException {
		return (Message) this.xmlToObj(in, Message.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.gov.spcoop.sica.wsbl.Message}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.gov.spcoop.sica.wsbl.Message}
	 * @return Object type {@link it.gov.spcoop.sica.wsbl.Message}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Message readMessageFromString(String in) throws DeserializerException {
		return (Message) this.xmlToObj(in.getBytes(), Message.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: StateTypeNormal
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link it.gov.spcoop.sica.wsbl.StateTypeNormal}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.gov.spcoop.sica.wsbl.StateTypeNormal}
	 * @return Object type {@link it.gov.spcoop.sica.wsbl.StateTypeNormal}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public StateTypeNormal readStateTypeNormal(String fileName) throws DeserializerException {
		return (StateTypeNormal) this.xmlToObj(fileName, StateTypeNormal.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.gov.spcoop.sica.wsbl.StateTypeNormal}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.gov.spcoop.sica.wsbl.StateTypeNormal}
	 * @return Object type {@link it.gov.spcoop.sica.wsbl.StateTypeNormal}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public StateTypeNormal readStateTypeNormal(File file) throws DeserializerException {
		return (StateTypeNormal) this.xmlToObj(file, StateTypeNormal.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.gov.spcoop.sica.wsbl.StateTypeNormal}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.gov.spcoop.sica.wsbl.StateTypeNormal}
	 * @return Object type {@link it.gov.spcoop.sica.wsbl.StateTypeNormal}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public StateTypeNormal readStateTypeNormal(InputStream in) throws DeserializerException {
		return (StateTypeNormal) this.xmlToObj(in, StateTypeNormal.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.gov.spcoop.sica.wsbl.StateTypeNormal}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.gov.spcoop.sica.wsbl.StateTypeNormal}
	 * @return Object type {@link it.gov.spcoop.sica.wsbl.StateTypeNormal}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public StateTypeNormal readStateTypeNormal(byte[] in) throws DeserializerException {
		return (StateTypeNormal) this.xmlToObj(in, StateTypeNormal.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.gov.spcoop.sica.wsbl.StateTypeNormal}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.gov.spcoop.sica.wsbl.StateTypeNormal}
	 * @return Object type {@link it.gov.spcoop.sica.wsbl.StateTypeNormal}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public StateTypeNormal readStateTypeNormalFromString(String in) throws DeserializerException {
		return (StateTypeNormal) this.xmlToObj(in.getBytes(), StateTypeNormal.class);
	}	
	
	
	

}
