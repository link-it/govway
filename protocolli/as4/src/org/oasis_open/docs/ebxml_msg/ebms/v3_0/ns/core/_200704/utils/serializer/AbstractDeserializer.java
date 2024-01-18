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
package org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.utils.serializer;

import org.openspcoop2.generic_project.exception.DeserializerException;

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
	 Object: Property
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Property}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Property}
	 * @return Object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Property}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Property readProperty(String fileName) throws DeserializerException {
		return (Property) this.xmlToObj(fileName, Property.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Property}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Property}
	 * @return Object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Property}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Property readProperty(File file) throws DeserializerException {
		return (Property) this.xmlToObj(file, Property.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Property}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Property}
	 * @return Object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Property}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Property readProperty(InputStream in) throws DeserializerException {
		return (Property) this.xmlToObj(in, Property.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Property}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Property}
	 * @return Object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Property}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Property readProperty(byte[] in) throws DeserializerException {
		return (Property) this.xmlToObj(in, Property.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Property}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Property}
	 * @return Object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Property}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Property readPropertyFromString(String in) throws DeserializerException {
		return (Property) this.xmlToObj(in.getBytes(), Property.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: MessageProperties
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessageProperties}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessageProperties}
	 * @return Object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessageProperties}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public MessageProperties readMessageProperties(String fileName) throws DeserializerException {
		return (MessageProperties) this.xmlToObj(fileName, MessageProperties.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessageProperties}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessageProperties}
	 * @return Object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessageProperties}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public MessageProperties readMessageProperties(File file) throws DeserializerException {
		return (MessageProperties) this.xmlToObj(file, MessageProperties.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessageProperties}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessageProperties}
	 * @return Object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessageProperties}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public MessageProperties readMessageProperties(InputStream in) throws DeserializerException {
		return (MessageProperties) this.xmlToObj(in, MessageProperties.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessageProperties}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessageProperties}
	 * @return Object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessageProperties}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public MessageProperties readMessageProperties(byte[] in) throws DeserializerException {
		return (MessageProperties) this.xmlToObj(in, MessageProperties.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessageProperties}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessageProperties}
	 * @return Object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessageProperties}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public MessageProperties readMessagePropertiesFromString(String in) throws DeserializerException {
		return (MessageProperties) this.xmlToObj(in.getBytes(), MessageProperties.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: MessageInfo
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessageInfo}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessageInfo}
	 * @return Object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessageInfo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public MessageInfo readMessageInfo(String fileName) throws DeserializerException {
		return (MessageInfo) this.xmlToObj(fileName, MessageInfo.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessageInfo}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessageInfo}
	 * @return Object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessageInfo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public MessageInfo readMessageInfo(File file) throws DeserializerException {
		return (MessageInfo) this.xmlToObj(file, MessageInfo.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessageInfo}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessageInfo}
	 * @return Object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessageInfo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public MessageInfo readMessageInfo(InputStream in) throws DeserializerException {
		return (MessageInfo) this.xmlToObj(in, MessageInfo.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessageInfo}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessageInfo}
	 * @return Object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessageInfo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public MessageInfo readMessageInfo(byte[] in) throws DeserializerException {
		return (MessageInfo) this.xmlToObj(in, MessageInfo.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessageInfo}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessageInfo}
	 * @return Object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessageInfo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public MessageInfo readMessageInfoFromString(String in) throws DeserializerException {
		return (MessageInfo) this.xmlToObj(in.getBytes(), MessageInfo.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: UserMessage
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.UserMessage}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.UserMessage}
	 * @return Object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.UserMessage}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public UserMessage readUserMessage(String fileName) throws DeserializerException {
		return (UserMessage) this.xmlToObj(fileName, UserMessage.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.UserMessage}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.UserMessage}
	 * @return Object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.UserMessage}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public UserMessage readUserMessage(File file) throws DeserializerException {
		return (UserMessage) this.xmlToObj(file, UserMessage.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.UserMessage}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.UserMessage}
	 * @return Object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.UserMessage}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public UserMessage readUserMessage(InputStream in) throws DeserializerException {
		return (UserMessage) this.xmlToObj(in, UserMessage.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.UserMessage}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.UserMessage}
	 * @return Object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.UserMessage}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public UserMessage readUserMessage(byte[] in) throws DeserializerException {
		return (UserMessage) this.xmlToObj(in, UserMessage.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.UserMessage}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.UserMessage}
	 * @return Object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.UserMessage}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public UserMessage readUserMessageFromString(String in) throws DeserializerException {
		return (UserMessage) this.xmlToObj(in.getBytes(), UserMessage.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: PartyInfo
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartyInfo}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartyInfo}
	 * @return Object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartyInfo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PartyInfo readPartyInfo(String fileName) throws DeserializerException {
		return (PartyInfo) this.xmlToObj(fileName, PartyInfo.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartyInfo}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartyInfo}
	 * @return Object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartyInfo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PartyInfo readPartyInfo(File file) throws DeserializerException {
		return (PartyInfo) this.xmlToObj(file, PartyInfo.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartyInfo}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartyInfo}
	 * @return Object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartyInfo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PartyInfo readPartyInfo(InputStream in) throws DeserializerException {
		return (PartyInfo) this.xmlToObj(in, PartyInfo.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartyInfo}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartyInfo}
	 * @return Object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartyInfo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PartyInfo readPartyInfo(byte[] in) throws DeserializerException {
		return (PartyInfo) this.xmlToObj(in, PartyInfo.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartyInfo}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartyInfo}
	 * @return Object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartyInfo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PartyInfo readPartyInfoFromString(String in) throws DeserializerException {
		return (PartyInfo) this.xmlToObj(in.getBytes(), PartyInfo.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: CollaborationInfo
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.CollaborationInfo}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.CollaborationInfo}
	 * @return Object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.CollaborationInfo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public CollaborationInfo readCollaborationInfo(String fileName) throws DeserializerException {
		return (CollaborationInfo) this.xmlToObj(fileName, CollaborationInfo.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.CollaborationInfo}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.CollaborationInfo}
	 * @return Object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.CollaborationInfo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public CollaborationInfo readCollaborationInfo(File file) throws DeserializerException {
		return (CollaborationInfo) this.xmlToObj(file, CollaborationInfo.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.CollaborationInfo}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.CollaborationInfo}
	 * @return Object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.CollaborationInfo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public CollaborationInfo readCollaborationInfo(InputStream in) throws DeserializerException {
		return (CollaborationInfo) this.xmlToObj(in, CollaborationInfo.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.CollaborationInfo}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.CollaborationInfo}
	 * @return Object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.CollaborationInfo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public CollaborationInfo readCollaborationInfo(byte[] in) throws DeserializerException {
		return (CollaborationInfo) this.xmlToObj(in, CollaborationInfo.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.CollaborationInfo}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.CollaborationInfo}
	 * @return Object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.CollaborationInfo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public CollaborationInfo readCollaborationInfoFromString(String in) throws DeserializerException {
		return (CollaborationInfo) this.xmlToObj(in.getBytes(), CollaborationInfo.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: PayloadInfo
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PayloadInfo}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PayloadInfo}
	 * @return Object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PayloadInfo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PayloadInfo readPayloadInfo(String fileName) throws DeserializerException {
		return (PayloadInfo) this.xmlToObj(fileName, PayloadInfo.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PayloadInfo}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PayloadInfo}
	 * @return Object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PayloadInfo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PayloadInfo readPayloadInfo(File file) throws DeserializerException {
		return (PayloadInfo) this.xmlToObj(file, PayloadInfo.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PayloadInfo}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PayloadInfo}
	 * @return Object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PayloadInfo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PayloadInfo readPayloadInfo(InputStream in) throws DeserializerException {
		return (PayloadInfo) this.xmlToObj(in, PayloadInfo.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PayloadInfo}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PayloadInfo}
	 * @return Object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PayloadInfo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PayloadInfo readPayloadInfo(byte[] in) throws DeserializerException {
		return (PayloadInfo) this.xmlToObj(in, PayloadInfo.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PayloadInfo}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PayloadInfo}
	 * @return Object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PayloadInfo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PayloadInfo readPayloadInfoFromString(String in) throws DeserializerException {
		return (PayloadInfo) this.xmlToObj(in.getBytes(), PayloadInfo.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: SignalMessage
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.SignalMessage}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.SignalMessage}
	 * @return Object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.SignalMessage}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SignalMessage readSignalMessage(String fileName) throws DeserializerException {
		return (SignalMessage) this.xmlToObj(fileName, SignalMessage.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.SignalMessage}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.SignalMessage}
	 * @return Object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.SignalMessage}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SignalMessage readSignalMessage(File file) throws DeserializerException {
		return (SignalMessage) this.xmlToObj(file, SignalMessage.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.SignalMessage}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.SignalMessage}
	 * @return Object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.SignalMessage}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SignalMessage readSignalMessage(InputStream in) throws DeserializerException {
		return (SignalMessage) this.xmlToObj(in, SignalMessage.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.SignalMessage}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.SignalMessage}
	 * @return Object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.SignalMessage}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SignalMessage readSignalMessage(byte[] in) throws DeserializerException {
		return (SignalMessage) this.xmlToObj(in, SignalMessage.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.SignalMessage}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.SignalMessage}
	 * @return Object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.SignalMessage}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SignalMessage readSignalMessageFromString(String in) throws DeserializerException {
		return (SignalMessage) this.xmlToObj(in.getBytes(), SignalMessage.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: Messaging
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Messaging}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Messaging}
	 * @return Object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Messaging}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Messaging readMessaging(String fileName) throws DeserializerException {
		return (Messaging) this.xmlToObj(fileName, Messaging.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Messaging}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Messaging}
	 * @return Object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Messaging}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Messaging readMessaging(File file) throws DeserializerException {
		return (Messaging) this.xmlToObj(file, Messaging.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Messaging}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Messaging}
	 * @return Object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Messaging}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Messaging readMessaging(InputStream in) throws DeserializerException {
		return (Messaging) this.xmlToObj(in, Messaging.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Messaging}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Messaging}
	 * @return Object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Messaging}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Messaging readMessaging(byte[] in) throws DeserializerException {
		return (Messaging) this.xmlToObj(in, Messaging.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Messaging}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Messaging}
	 * @return Object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Messaging}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Messaging readMessagingFromString(String in) throws DeserializerException {
		return (Messaging) this.xmlToObj(in.getBytes(), Messaging.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: PartyId
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartyId}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartyId}
	 * @return Object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartyId}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PartyId readPartyId(String fileName) throws DeserializerException {
		return (PartyId) this.xmlToObj(fileName, PartyId.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartyId}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartyId}
	 * @return Object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartyId}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PartyId readPartyId(File file) throws DeserializerException {
		return (PartyId) this.xmlToObj(file, PartyId.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartyId}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartyId}
	 * @return Object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartyId}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PartyId readPartyId(InputStream in) throws DeserializerException {
		return (PartyId) this.xmlToObj(in, PartyId.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartyId}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartyId}
	 * @return Object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartyId}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PartyId readPartyId(byte[] in) throws DeserializerException {
		return (PartyId) this.xmlToObj(in, PartyId.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartyId}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartyId}
	 * @return Object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartyId}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PartyId readPartyIdFromString(String in) throws DeserializerException {
		return (PartyId) this.xmlToObj(in.getBytes(), PartyId.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: To
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.To}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.To}
	 * @return Object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.To}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public To readTo(String fileName) throws DeserializerException {
		return (To) this.xmlToObj(fileName, To.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.To}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.To}
	 * @return Object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.To}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public To readTo(File file) throws DeserializerException {
		return (To) this.xmlToObj(file, To.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.To}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.To}
	 * @return Object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.To}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public To readTo(InputStream in) throws DeserializerException {
		return (To) this.xmlToObj(in, To.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.To}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.To}
	 * @return Object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.To}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public To readTo(byte[] in) throws DeserializerException {
		return (To) this.xmlToObj(in, To.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.To}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.To}
	 * @return Object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.To}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public To readToFromString(String in) throws DeserializerException {
		return (To) this.xmlToObj(in.getBytes(), To.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: PartProperties
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartProperties}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartProperties}
	 * @return Object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartProperties}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PartProperties readPartProperties(String fileName) throws DeserializerException {
		return (PartProperties) this.xmlToObj(fileName, PartProperties.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartProperties}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartProperties}
	 * @return Object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartProperties}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PartProperties readPartProperties(File file) throws DeserializerException {
		return (PartProperties) this.xmlToObj(file, PartProperties.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartProperties}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartProperties}
	 * @return Object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartProperties}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PartProperties readPartProperties(InputStream in) throws DeserializerException {
		return (PartProperties) this.xmlToObj(in, PartProperties.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartProperties}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartProperties}
	 * @return Object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartProperties}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PartProperties readPartProperties(byte[] in) throws DeserializerException {
		return (PartProperties) this.xmlToObj(in, PartProperties.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartProperties}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartProperties}
	 * @return Object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartProperties}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PartProperties readPartPropertiesFromString(String in) throws DeserializerException {
		return (PartProperties) this.xmlToObj(in.getBytes(), PartProperties.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: AgreementRef
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.AgreementRef}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.AgreementRef}
	 * @return Object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.AgreementRef}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AgreementRef readAgreementRef(String fileName) throws DeserializerException {
		return (AgreementRef) this.xmlToObj(fileName, AgreementRef.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.AgreementRef}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.AgreementRef}
	 * @return Object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.AgreementRef}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AgreementRef readAgreementRef(File file) throws DeserializerException {
		return (AgreementRef) this.xmlToObj(file, AgreementRef.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.AgreementRef}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.AgreementRef}
	 * @return Object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.AgreementRef}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AgreementRef readAgreementRef(InputStream in) throws DeserializerException {
		return (AgreementRef) this.xmlToObj(in, AgreementRef.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.AgreementRef}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.AgreementRef}
	 * @return Object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.AgreementRef}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AgreementRef readAgreementRef(byte[] in) throws DeserializerException {
		return (AgreementRef) this.xmlToObj(in, AgreementRef.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.AgreementRef}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.AgreementRef}
	 * @return Object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.AgreementRef}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AgreementRef readAgreementRefFromString(String in) throws DeserializerException {
		return (AgreementRef) this.xmlToObj(in.getBytes(), AgreementRef.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: Service
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Service}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Service}
	 * @return Object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Service}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Service readService(String fileName) throws DeserializerException {
		return (Service) this.xmlToObj(fileName, Service.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Service}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Service}
	 * @return Object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Service}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Service readService(File file) throws DeserializerException {
		return (Service) this.xmlToObj(file, Service.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Service}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Service}
	 * @return Object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Service}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Service readService(InputStream in) throws DeserializerException {
		return (Service) this.xmlToObj(in, Service.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Service}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Service}
	 * @return Object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Service}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Service readService(byte[] in) throws DeserializerException {
		return (Service) this.xmlToObj(in, Service.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Service}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Service}
	 * @return Object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Service}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Service readServiceFromString(String in) throws DeserializerException {
		return (Service) this.xmlToObj(in.getBytes(), Service.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: PartInfo
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartInfo}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartInfo}
	 * @return Object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartInfo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PartInfo readPartInfo(String fileName) throws DeserializerException {
		return (PartInfo) this.xmlToObj(fileName, PartInfo.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartInfo}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartInfo}
	 * @return Object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartInfo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PartInfo readPartInfo(File file) throws DeserializerException {
		return (PartInfo) this.xmlToObj(file, PartInfo.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartInfo}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartInfo}
	 * @return Object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartInfo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PartInfo readPartInfo(InputStream in) throws DeserializerException {
		return (PartInfo) this.xmlToObj(in, PartInfo.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartInfo}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartInfo}
	 * @return Object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartInfo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PartInfo readPartInfo(byte[] in) throws DeserializerException {
		return (PartInfo) this.xmlToObj(in, PartInfo.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartInfo}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartInfo}
	 * @return Object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartInfo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PartInfo readPartInfoFromString(String in) throws DeserializerException {
		return (PartInfo) this.xmlToObj(in.getBytes(), PartInfo.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: From
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.From}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.From}
	 * @return Object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.From}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public From readFrom(String fileName) throws DeserializerException {
		return (From) this.xmlToObj(fileName, From.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.From}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.From}
	 * @return Object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.From}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public From readFrom(File file) throws DeserializerException {
		return (From) this.xmlToObj(file, From.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.From}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.From}
	 * @return Object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.From}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public From readFrom(InputStream in) throws DeserializerException {
		return (From) this.xmlToObj(in, From.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.From}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.From}
	 * @return Object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.From}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public From readFrom(byte[] in) throws DeserializerException {
		return (From) this.xmlToObj(in, From.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.From}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.From}
	 * @return Object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.From}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public From readFromFromString(String in) throws DeserializerException {
		return (From) this.xmlToObj(in.getBytes(), From.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: Schema
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Schema}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Schema}
	 * @return Object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Schema}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Schema readSchema(String fileName) throws DeserializerException {
		return (Schema) this.xmlToObj(fileName, Schema.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Schema}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Schema}
	 * @return Object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Schema}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Schema readSchema(File file) throws DeserializerException {
		return (Schema) this.xmlToObj(file, Schema.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Schema}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Schema}
	 * @return Object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Schema}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Schema readSchema(InputStream in) throws DeserializerException {
		return (Schema) this.xmlToObj(in, Schema.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Schema}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Schema}
	 * @return Object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Schema}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Schema readSchema(byte[] in) throws DeserializerException {
		return (Schema) this.xmlToObj(in, Schema.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Schema}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Schema}
	 * @return Object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Schema}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Schema readSchemaFromString(String in) throws DeserializerException {
		return (Schema) this.xmlToObj(in.getBytes(), Schema.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: Error
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Error}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Error}
	 * @return Object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Error}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Error readError(String fileName) throws DeserializerException {
		return (Error) this.xmlToObj(fileName, Error.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Error}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Error}
	 * @return Object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Error}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Error readError(File file) throws DeserializerException {
		return (Error) this.xmlToObj(file, Error.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Error}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Error}
	 * @return Object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Error}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Error readError(InputStream in) throws DeserializerException {
		return (Error) this.xmlToObj(in, Error.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Error}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Error}
	 * @return Object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Error}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Error readError(byte[] in) throws DeserializerException {
		return (Error) this.xmlToObj(in, Error.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Error}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Error}
	 * @return Object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Error}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Error readErrorFromString(String in) throws DeserializerException {
		return (Error) this.xmlToObj(in.getBytes(), Error.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: Description
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Description}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Description}
	 * @return Object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Description}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Description readDescription(String fileName) throws DeserializerException {
		return (Description) this.xmlToObj(fileName, Description.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Description}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Description}
	 * @return Object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Description}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Description readDescription(File file) throws DeserializerException {
		return (Description) this.xmlToObj(file, Description.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Description}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Description}
	 * @return Object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Description}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Description readDescription(InputStream in) throws DeserializerException {
		return (Description) this.xmlToObj(in, Description.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Description}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Description}
	 * @return Object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Description}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Description readDescription(byte[] in) throws DeserializerException {
		return (Description) this.xmlToObj(in, Description.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Description}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Description}
	 * @return Object type {@link org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Description}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Description readDescriptionFromString(String in) throws DeserializerException {
		return (Description) this.xmlToObj(in.getBytes(), Description.class);
	}	
	
	
	

}
