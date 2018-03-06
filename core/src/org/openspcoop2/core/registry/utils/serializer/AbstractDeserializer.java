/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
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
package org.openspcoop2.core.registry.utils.serializer;

import org.openspcoop2.generic_project.exception.DeserializerException;

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
import org.openspcoop2.core.registry.Documento;
import org.openspcoop2.core.registry.IdPortaDominio;
import org.openspcoop2.core.registry.IdSoggetto;
import org.openspcoop2.core.registry.IdAccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.ConfigurazioneServizio;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.core.registry.Property;
import org.openspcoop2.core.registry.Ruolo;
import org.openspcoop2.core.registry.AccordoCooperazionePartecipanti;
import org.openspcoop2.core.registry.Azione;
import org.openspcoop2.core.registry.Connettore;
import org.openspcoop2.core.registry.ConfigurazioneServizioAzione;
import org.openspcoop2.core.registry.RuoloSoggetto;
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
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.RuoliSoggetto;
import org.openspcoop2.core.registry.IdAccordoServizioParteComune;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.File;

/**     
 * XML Deserializer of beans
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public abstract class AbstractDeserializer {



	protected abstract Object _xmlToObj(InputStream is, Class<?> c) throws Exception;
	
	private Object xmlToObj(InputStream is,Class<?> c) throws DeserializerException{
		try{
			return this._xmlToObj(is, c);
		}catch(Exception e){
			throw new DeserializerException(e.getMessage(), e);
		}
	}
	private Object xmlToObj(String fileName,Class<?> c) throws DeserializerException{
		try{
			return this.xmlToObj(new File(fileName), c);
		}catch(Exception e){
			throw new DeserializerException(e.getMessage(), e);
		}
	}
	private Object xmlToObj(File file,Class<?> c) throws DeserializerException{
		FileInputStream fin = null;
		try{
			fin = new FileInputStream(file);
			return this._xmlToObj(fin, c);
		}catch(Exception e){
			throw new DeserializerException(e.getMessage(),e);
		}finally{
			try{
				fin.close();
			}catch(Exception e){}
		}
	}
	private Object xmlToObj(byte[] file,Class<?> c) throws DeserializerException{
		ByteArrayInputStream fin = null;
		try{
			fin = new ByteArrayInputStream(file);
			return this._xmlToObj(fin, c);
		}catch(Exception e){
			throw new DeserializerException(e.getMessage(),e);
		}finally{
			try{
				fin.close();
			}catch(Exception e){}
		}
	}






	/*
	 =================================================================================
	 Object: message
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.registry.Message}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.Message}
	 * @return Object type {@link org.openspcoop2.core.registry.Message}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Message readMessage(String fileName) throws DeserializerException {
		return (Message) this.xmlToObj(fileName, Message.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.registry.Message}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.Message}
	 * @return Object type {@link org.openspcoop2.core.registry.Message}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Message readMessage(File file) throws DeserializerException {
		return (Message) this.xmlToObj(file, Message.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.registry.Message}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.Message}
	 * @return Object type {@link org.openspcoop2.core.registry.Message}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Message readMessage(InputStream in) throws DeserializerException {
		return (Message) this.xmlToObj(in, Message.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.registry.Message}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.Message}
	 * @return Object type {@link org.openspcoop2.core.registry.Message}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Message readMessage(byte[] in) throws DeserializerException {
		return (Message) this.xmlToObj(in, Message.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.registry.Message}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.Message}
	 * @return Object type {@link org.openspcoop2.core.registry.Message}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Message readMessageFromString(String in) throws DeserializerException {
		return (Message) this.xmlToObj(in.getBytes(), Message.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: operation
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.registry.Operation}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.Operation}
	 * @return Object type {@link org.openspcoop2.core.registry.Operation}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Operation readOperation(String fileName) throws DeserializerException {
		return (Operation) this.xmlToObj(fileName, Operation.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.registry.Operation}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.Operation}
	 * @return Object type {@link org.openspcoop2.core.registry.Operation}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Operation readOperation(File file) throws DeserializerException {
		return (Operation) this.xmlToObj(file, Operation.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.registry.Operation}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.Operation}
	 * @return Object type {@link org.openspcoop2.core.registry.Operation}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Operation readOperation(InputStream in) throws DeserializerException {
		return (Operation) this.xmlToObj(in, Operation.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.registry.Operation}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.Operation}
	 * @return Object type {@link org.openspcoop2.core.registry.Operation}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Operation readOperation(byte[] in) throws DeserializerException {
		return (Operation) this.xmlToObj(in, Operation.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.registry.Operation}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.Operation}
	 * @return Object type {@link org.openspcoop2.core.registry.Operation}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Operation readOperationFromString(String in) throws DeserializerException {
		return (Operation) this.xmlToObj(in.getBytes(), Operation.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: protocol-property
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.registry.ProtocolProperty}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.ProtocolProperty}
	 * @return Object type {@link org.openspcoop2.core.registry.ProtocolProperty}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ProtocolProperty readProtocolProperty(String fileName) throws DeserializerException {
		return (ProtocolProperty) this.xmlToObj(fileName, ProtocolProperty.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.registry.ProtocolProperty}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.ProtocolProperty}
	 * @return Object type {@link org.openspcoop2.core.registry.ProtocolProperty}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ProtocolProperty readProtocolProperty(File file) throws DeserializerException {
		return (ProtocolProperty) this.xmlToObj(file, ProtocolProperty.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.registry.ProtocolProperty}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.ProtocolProperty}
	 * @return Object type {@link org.openspcoop2.core.registry.ProtocolProperty}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ProtocolProperty readProtocolProperty(InputStream in) throws DeserializerException {
		return (ProtocolProperty) this.xmlToObj(in, ProtocolProperty.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.registry.ProtocolProperty}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.ProtocolProperty}
	 * @return Object type {@link org.openspcoop2.core.registry.ProtocolProperty}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ProtocolProperty readProtocolProperty(byte[] in) throws DeserializerException {
		return (ProtocolProperty) this.xmlToObj(in, ProtocolProperty.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.registry.ProtocolProperty}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.ProtocolProperty}
	 * @return Object type {@link org.openspcoop2.core.registry.ProtocolProperty}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ProtocolProperty readProtocolPropertyFromString(String in) throws DeserializerException {
		return (ProtocolProperty) this.xmlToObj(in.getBytes(), ProtocolProperty.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: resource-parameter
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.registry.ResourceParameter}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.ResourceParameter}
	 * @return Object type {@link org.openspcoop2.core.registry.ResourceParameter}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ResourceParameter readResourceParameter(String fileName) throws DeserializerException {
		return (ResourceParameter) this.xmlToObj(fileName, ResourceParameter.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.registry.ResourceParameter}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.ResourceParameter}
	 * @return Object type {@link org.openspcoop2.core.registry.ResourceParameter}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ResourceParameter readResourceParameter(File file) throws DeserializerException {
		return (ResourceParameter) this.xmlToObj(file, ResourceParameter.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.registry.ResourceParameter}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.ResourceParameter}
	 * @return Object type {@link org.openspcoop2.core.registry.ResourceParameter}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ResourceParameter readResourceParameter(InputStream in) throws DeserializerException {
		return (ResourceParameter) this.xmlToObj(in, ResourceParameter.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.registry.ResourceParameter}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.ResourceParameter}
	 * @return Object type {@link org.openspcoop2.core.registry.ResourceParameter}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ResourceParameter readResourceParameter(byte[] in) throws DeserializerException {
		return (ResourceParameter) this.xmlToObj(in, ResourceParameter.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.registry.ResourceParameter}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.ResourceParameter}
	 * @return Object type {@link org.openspcoop2.core.registry.ResourceParameter}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ResourceParameter readResourceParameterFromString(String in) throws DeserializerException {
		return (ResourceParameter) this.xmlToObj(in.getBytes(), ResourceParameter.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: resource-request
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.registry.ResourceRequest}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.ResourceRequest}
	 * @return Object type {@link org.openspcoop2.core.registry.ResourceRequest}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ResourceRequest readResourceRequest(String fileName) throws DeserializerException {
		return (ResourceRequest) this.xmlToObj(fileName, ResourceRequest.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.registry.ResourceRequest}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.ResourceRequest}
	 * @return Object type {@link org.openspcoop2.core.registry.ResourceRequest}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ResourceRequest readResourceRequest(File file) throws DeserializerException {
		return (ResourceRequest) this.xmlToObj(file, ResourceRequest.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.registry.ResourceRequest}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.ResourceRequest}
	 * @return Object type {@link org.openspcoop2.core.registry.ResourceRequest}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ResourceRequest readResourceRequest(InputStream in) throws DeserializerException {
		return (ResourceRequest) this.xmlToObj(in, ResourceRequest.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.registry.ResourceRequest}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.ResourceRequest}
	 * @return Object type {@link org.openspcoop2.core.registry.ResourceRequest}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ResourceRequest readResourceRequest(byte[] in) throws DeserializerException {
		return (ResourceRequest) this.xmlToObj(in, ResourceRequest.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.registry.ResourceRequest}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.ResourceRequest}
	 * @return Object type {@link org.openspcoop2.core.registry.ResourceRequest}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ResourceRequest readResourceRequestFromString(String in) throws DeserializerException {
		return (ResourceRequest) this.xmlToObj(in.getBytes(), ResourceRequest.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: resource-representation
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.registry.ResourceRepresentation}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.ResourceRepresentation}
	 * @return Object type {@link org.openspcoop2.core.registry.ResourceRepresentation}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ResourceRepresentation readResourceRepresentation(String fileName) throws DeserializerException {
		return (ResourceRepresentation) this.xmlToObj(fileName, ResourceRepresentation.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.registry.ResourceRepresentation}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.ResourceRepresentation}
	 * @return Object type {@link org.openspcoop2.core.registry.ResourceRepresentation}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ResourceRepresentation readResourceRepresentation(File file) throws DeserializerException {
		return (ResourceRepresentation) this.xmlToObj(file, ResourceRepresentation.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.registry.ResourceRepresentation}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.ResourceRepresentation}
	 * @return Object type {@link org.openspcoop2.core.registry.ResourceRepresentation}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ResourceRepresentation readResourceRepresentation(InputStream in) throws DeserializerException {
		return (ResourceRepresentation) this.xmlToObj(in, ResourceRepresentation.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.registry.ResourceRepresentation}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.ResourceRepresentation}
	 * @return Object type {@link org.openspcoop2.core.registry.ResourceRepresentation}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ResourceRepresentation readResourceRepresentation(byte[] in) throws DeserializerException {
		return (ResourceRepresentation) this.xmlToObj(in, ResourceRepresentation.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.registry.ResourceRepresentation}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.ResourceRepresentation}
	 * @return Object type {@link org.openspcoop2.core.registry.ResourceRepresentation}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ResourceRepresentation readResourceRepresentationFromString(String in) throws DeserializerException {
		return (ResourceRepresentation) this.xmlToObj(in.getBytes(), ResourceRepresentation.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: resource
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.registry.Resource}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.Resource}
	 * @return Object type {@link org.openspcoop2.core.registry.Resource}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Resource readResource(String fileName) throws DeserializerException {
		return (Resource) this.xmlToObj(fileName, Resource.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.registry.Resource}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.Resource}
	 * @return Object type {@link org.openspcoop2.core.registry.Resource}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Resource readResource(File file) throws DeserializerException {
		return (Resource) this.xmlToObj(file, Resource.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.registry.Resource}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.Resource}
	 * @return Object type {@link org.openspcoop2.core.registry.Resource}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Resource readResource(InputStream in) throws DeserializerException {
		return (Resource) this.xmlToObj(in, Resource.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.registry.Resource}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.Resource}
	 * @return Object type {@link org.openspcoop2.core.registry.Resource}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Resource readResource(byte[] in) throws DeserializerException {
		return (Resource) this.xmlToObj(in, Resource.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.registry.Resource}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.Resource}
	 * @return Object type {@link org.openspcoop2.core.registry.Resource}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Resource readResourceFromString(String in) throws DeserializerException {
		return (Resource) this.xmlToObj(in.getBytes(), Resource.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: resource-response
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.registry.ResourceResponse}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.ResourceResponse}
	 * @return Object type {@link org.openspcoop2.core.registry.ResourceResponse}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ResourceResponse readResourceResponse(String fileName) throws DeserializerException {
		return (ResourceResponse) this.xmlToObj(fileName, ResourceResponse.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.registry.ResourceResponse}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.ResourceResponse}
	 * @return Object type {@link org.openspcoop2.core.registry.ResourceResponse}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ResourceResponse readResourceResponse(File file) throws DeserializerException {
		return (ResourceResponse) this.xmlToObj(file, ResourceResponse.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.registry.ResourceResponse}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.ResourceResponse}
	 * @return Object type {@link org.openspcoop2.core.registry.ResourceResponse}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ResourceResponse readResourceResponse(InputStream in) throws DeserializerException {
		return (ResourceResponse) this.xmlToObj(in, ResourceResponse.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.registry.ResourceResponse}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.ResourceResponse}
	 * @return Object type {@link org.openspcoop2.core.registry.ResourceResponse}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ResourceResponse readResourceResponse(byte[] in) throws DeserializerException {
		return (ResourceResponse) this.xmlToObj(in, ResourceResponse.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.registry.ResourceResponse}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.ResourceResponse}
	 * @return Object type {@link org.openspcoop2.core.registry.ResourceResponse}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ResourceResponse readResourceResponseFromString(String in) throws DeserializerException {
		return (ResourceResponse) this.xmlToObj(in.getBytes(), ResourceResponse.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: id-ruolo
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.registry.IdRuolo}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.IdRuolo}
	 * @return Object type {@link org.openspcoop2.core.registry.IdRuolo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdRuolo readIdRuolo(String fileName) throws DeserializerException {
		return (IdRuolo) this.xmlToObj(fileName, IdRuolo.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.registry.IdRuolo}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.IdRuolo}
	 * @return Object type {@link org.openspcoop2.core.registry.IdRuolo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdRuolo readIdRuolo(File file) throws DeserializerException {
		return (IdRuolo) this.xmlToObj(file, IdRuolo.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.registry.IdRuolo}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.IdRuolo}
	 * @return Object type {@link org.openspcoop2.core.registry.IdRuolo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdRuolo readIdRuolo(InputStream in) throws DeserializerException {
		return (IdRuolo) this.xmlToObj(in, IdRuolo.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.registry.IdRuolo}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.IdRuolo}
	 * @return Object type {@link org.openspcoop2.core.registry.IdRuolo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdRuolo readIdRuolo(byte[] in) throws DeserializerException {
		return (IdRuolo) this.xmlToObj(in, IdRuolo.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.registry.IdRuolo}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.IdRuolo}
	 * @return Object type {@link org.openspcoop2.core.registry.IdRuolo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdRuolo readIdRuoloFromString(String in) throws DeserializerException {
		return (IdRuolo) this.xmlToObj(in.getBytes(), IdRuolo.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: resource-representation-json
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.registry.ResourceRepresentationJson}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.ResourceRepresentationJson}
	 * @return Object type {@link org.openspcoop2.core.registry.ResourceRepresentationJson}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ResourceRepresentationJson readResourceRepresentationJson(String fileName) throws DeserializerException {
		return (ResourceRepresentationJson) this.xmlToObj(fileName, ResourceRepresentationJson.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.registry.ResourceRepresentationJson}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.ResourceRepresentationJson}
	 * @return Object type {@link org.openspcoop2.core.registry.ResourceRepresentationJson}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ResourceRepresentationJson readResourceRepresentationJson(File file) throws DeserializerException {
		return (ResourceRepresentationJson) this.xmlToObj(file, ResourceRepresentationJson.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.registry.ResourceRepresentationJson}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.ResourceRepresentationJson}
	 * @return Object type {@link org.openspcoop2.core.registry.ResourceRepresentationJson}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ResourceRepresentationJson readResourceRepresentationJson(InputStream in) throws DeserializerException {
		return (ResourceRepresentationJson) this.xmlToObj(in, ResourceRepresentationJson.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.registry.ResourceRepresentationJson}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.ResourceRepresentationJson}
	 * @return Object type {@link org.openspcoop2.core.registry.ResourceRepresentationJson}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ResourceRepresentationJson readResourceRepresentationJson(byte[] in) throws DeserializerException {
		return (ResourceRepresentationJson) this.xmlToObj(in, ResourceRepresentationJson.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.registry.ResourceRepresentationJson}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.ResourceRepresentationJson}
	 * @return Object type {@link org.openspcoop2.core.registry.ResourceRepresentationJson}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ResourceRepresentationJson readResourceRepresentationJsonFromString(String in) throws DeserializerException {
		return (ResourceRepresentationJson) this.xmlToObj(in.getBytes(), ResourceRepresentationJson.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: message-part
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.registry.MessagePart}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.MessagePart}
	 * @return Object type {@link org.openspcoop2.core.registry.MessagePart}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public MessagePart readMessagePart(String fileName) throws DeserializerException {
		return (MessagePart) this.xmlToObj(fileName, MessagePart.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.registry.MessagePart}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.MessagePart}
	 * @return Object type {@link org.openspcoop2.core.registry.MessagePart}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public MessagePart readMessagePart(File file) throws DeserializerException {
		return (MessagePart) this.xmlToObj(file, MessagePart.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.registry.MessagePart}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.MessagePart}
	 * @return Object type {@link org.openspcoop2.core.registry.MessagePart}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public MessagePart readMessagePart(InputStream in) throws DeserializerException {
		return (MessagePart) this.xmlToObj(in, MessagePart.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.registry.MessagePart}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.MessagePart}
	 * @return Object type {@link org.openspcoop2.core.registry.MessagePart}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public MessagePart readMessagePart(byte[] in) throws DeserializerException {
		return (MessagePart) this.xmlToObj(in, MessagePart.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.registry.MessagePart}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.MessagePart}
	 * @return Object type {@link org.openspcoop2.core.registry.MessagePart}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public MessagePart readMessagePartFromString(String in) throws DeserializerException {
		return (MessagePart) this.xmlToObj(in.getBytes(), MessagePart.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: documento
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.registry.Documento}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.Documento}
	 * @return Object type {@link org.openspcoop2.core.registry.Documento}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Documento readDocumento(String fileName) throws DeserializerException {
		return (Documento) this.xmlToObj(fileName, Documento.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.registry.Documento}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.Documento}
	 * @return Object type {@link org.openspcoop2.core.registry.Documento}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Documento readDocumento(File file) throws DeserializerException {
		return (Documento) this.xmlToObj(file, Documento.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.registry.Documento}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.Documento}
	 * @return Object type {@link org.openspcoop2.core.registry.Documento}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Documento readDocumento(InputStream in) throws DeserializerException {
		return (Documento) this.xmlToObj(in, Documento.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.registry.Documento}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.Documento}
	 * @return Object type {@link org.openspcoop2.core.registry.Documento}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Documento readDocumento(byte[] in) throws DeserializerException {
		return (Documento) this.xmlToObj(in, Documento.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.registry.Documento}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.Documento}
	 * @return Object type {@link org.openspcoop2.core.registry.Documento}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Documento readDocumentoFromString(String in) throws DeserializerException {
		return (Documento) this.xmlToObj(in.getBytes(), Documento.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: id-porta-dominio
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.registry.IdPortaDominio}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.IdPortaDominio}
	 * @return Object type {@link org.openspcoop2.core.registry.IdPortaDominio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdPortaDominio readIdPortaDominio(String fileName) throws DeserializerException {
		return (IdPortaDominio) this.xmlToObj(fileName, IdPortaDominio.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.registry.IdPortaDominio}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.IdPortaDominio}
	 * @return Object type {@link org.openspcoop2.core.registry.IdPortaDominio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdPortaDominio readIdPortaDominio(File file) throws DeserializerException {
		return (IdPortaDominio) this.xmlToObj(file, IdPortaDominio.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.registry.IdPortaDominio}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.IdPortaDominio}
	 * @return Object type {@link org.openspcoop2.core.registry.IdPortaDominio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdPortaDominio readIdPortaDominio(InputStream in) throws DeserializerException {
		return (IdPortaDominio) this.xmlToObj(in, IdPortaDominio.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.registry.IdPortaDominio}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.IdPortaDominio}
	 * @return Object type {@link org.openspcoop2.core.registry.IdPortaDominio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdPortaDominio readIdPortaDominio(byte[] in) throws DeserializerException {
		return (IdPortaDominio) this.xmlToObj(in, IdPortaDominio.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.registry.IdPortaDominio}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.IdPortaDominio}
	 * @return Object type {@link org.openspcoop2.core.registry.IdPortaDominio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdPortaDominio readIdPortaDominioFromString(String in) throws DeserializerException {
		return (IdPortaDominio) this.xmlToObj(in.getBytes(), IdPortaDominio.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: id-soggetto
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.registry.IdSoggetto}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.IdSoggetto}
	 * @return Object type {@link org.openspcoop2.core.registry.IdSoggetto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdSoggetto readIdSoggetto(String fileName) throws DeserializerException {
		return (IdSoggetto) this.xmlToObj(fileName, IdSoggetto.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.registry.IdSoggetto}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.IdSoggetto}
	 * @return Object type {@link org.openspcoop2.core.registry.IdSoggetto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdSoggetto readIdSoggetto(File file) throws DeserializerException {
		return (IdSoggetto) this.xmlToObj(file, IdSoggetto.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.registry.IdSoggetto}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.IdSoggetto}
	 * @return Object type {@link org.openspcoop2.core.registry.IdSoggetto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdSoggetto readIdSoggetto(InputStream in) throws DeserializerException {
		return (IdSoggetto) this.xmlToObj(in, IdSoggetto.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.registry.IdSoggetto}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.IdSoggetto}
	 * @return Object type {@link org.openspcoop2.core.registry.IdSoggetto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdSoggetto readIdSoggetto(byte[] in) throws DeserializerException {
		return (IdSoggetto) this.xmlToObj(in, IdSoggetto.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.registry.IdSoggetto}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.IdSoggetto}
	 * @return Object type {@link org.openspcoop2.core.registry.IdSoggetto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdSoggetto readIdSoggettoFromString(String in) throws DeserializerException {
		return (IdSoggetto) this.xmlToObj(in.getBytes(), IdSoggetto.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: id-accordo-servizio-parte-specifica
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.registry.IdAccordoServizioParteSpecifica}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.IdAccordoServizioParteSpecifica}
	 * @return Object type {@link org.openspcoop2.core.registry.IdAccordoServizioParteSpecifica}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdAccordoServizioParteSpecifica readIdAccordoServizioParteSpecifica(String fileName) throws DeserializerException {
		return (IdAccordoServizioParteSpecifica) this.xmlToObj(fileName, IdAccordoServizioParteSpecifica.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.registry.IdAccordoServizioParteSpecifica}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.IdAccordoServizioParteSpecifica}
	 * @return Object type {@link org.openspcoop2.core.registry.IdAccordoServizioParteSpecifica}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdAccordoServizioParteSpecifica readIdAccordoServizioParteSpecifica(File file) throws DeserializerException {
		return (IdAccordoServizioParteSpecifica) this.xmlToObj(file, IdAccordoServizioParteSpecifica.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.registry.IdAccordoServizioParteSpecifica}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.IdAccordoServizioParteSpecifica}
	 * @return Object type {@link org.openspcoop2.core.registry.IdAccordoServizioParteSpecifica}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdAccordoServizioParteSpecifica readIdAccordoServizioParteSpecifica(InputStream in) throws DeserializerException {
		return (IdAccordoServizioParteSpecifica) this.xmlToObj(in, IdAccordoServizioParteSpecifica.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.registry.IdAccordoServizioParteSpecifica}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.IdAccordoServizioParteSpecifica}
	 * @return Object type {@link org.openspcoop2.core.registry.IdAccordoServizioParteSpecifica}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdAccordoServizioParteSpecifica readIdAccordoServizioParteSpecifica(byte[] in) throws DeserializerException {
		return (IdAccordoServizioParteSpecifica) this.xmlToObj(in, IdAccordoServizioParteSpecifica.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.registry.IdAccordoServizioParteSpecifica}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.IdAccordoServizioParteSpecifica}
	 * @return Object type {@link org.openspcoop2.core.registry.IdAccordoServizioParteSpecifica}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdAccordoServizioParteSpecifica readIdAccordoServizioParteSpecificaFromString(String in) throws DeserializerException {
		return (IdAccordoServizioParteSpecifica) this.xmlToObj(in.getBytes(), IdAccordoServizioParteSpecifica.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: configurazione-servizio
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.registry.ConfigurazioneServizio}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.ConfigurazioneServizio}
	 * @return Object type {@link org.openspcoop2.core.registry.ConfigurazioneServizio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConfigurazioneServizio readConfigurazioneServizio(String fileName) throws DeserializerException {
		return (ConfigurazioneServizio) this.xmlToObj(fileName, ConfigurazioneServizio.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.registry.ConfigurazioneServizio}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.ConfigurazioneServizio}
	 * @return Object type {@link org.openspcoop2.core.registry.ConfigurazioneServizio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConfigurazioneServizio readConfigurazioneServizio(File file) throws DeserializerException {
		return (ConfigurazioneServizio) this.xmlToObj(file, ConfigurazioneServizio.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.registry.ConfigurazioneServizio}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.ConfigurazioneServizio}
	 * @return Object type {@link org.openspcoop2.core.registry.ConfigurazioneServizio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConfigurazioneServizio readConfigurazioneServizio(InputStream in) throws DeserializerException {
		return (ConfigurazioneServizio) this.xmlToObj(in, ConfigurazioneServizio.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.registry.ConfigurazioneServizio}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.ConfigurazioneServizio}
	 * @return Object type {@link org.openspcoop2.core.registry.ConfigurazioneServizio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConfigurazioneServizio readConfigurazioneServizio(byte[] in) throws DeserializerException {
		return (ConfigurazioneServizio) this.xmlToObj(in, ConfigurazioneServizio.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.registry.ConfigurazioneServizio}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.ConfigurazioneServizio}
	 * @return Object type {@link org.openspcoop2.core.registry.ConfigurazioneServizio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConfigurazioneServizio readConfigurazioneServizioFromString(String in) throws DeserializerException {
		return (ConfigurazioneServizio) this.xmlToObj(in.getBytes(), ConfigurazioneServizio.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: accordo-servizio-parte-specifica
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.registry.AccordoServizioParteSpecifica}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.AccordoServizioParteSpecifica}
	 * @return Object type {@link org.openspcoop2.core.registry.AccordoServizioParteSpecifica}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccordoServizioParteSpecifica readAccordoServizioParteSpecifica(String fileName) throws DeserializerException {
		return (AccordoServizioParteSpecifica) this.xmlToObj(fileName, AccordoServizioParteSpecifica.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.registry.AccordoServizioParteSpecifica}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.AccordoServizioParteSpecifica}
	 * @return Object type {@link org.openspcoop2.core.registry.AccordoServizioParteSpecifica}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccordoServizioParteSpecifica readAccordoServizioParteSpecifica(File file) throws DeserializerException {
		return (AccordoServizioParteSpecifica) this.xmlToObj(file, AccordoServizioParteSpecifica.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.registry.AccordoServizioParteSpecifica}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.AccordoServizioParteSpecifica}
	 * @return Object type {@link org.openspcoop2.core.registry.AccordoServizioParteSpecifica}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccordoServizioParteSpecifica readAccordoServizioParteSpecifica(InputStream in) throws DeserializerException {
		return (AccordoServizioParteSpecifica) this.xmlToObj(in, AccordoServizioParteSpecifica.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.registry.AccordoServizioParteSpecifica}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.AccordoServizioParteSpecifica}
	 * @return Object type {@link org.openspcoop2.core.registry.AccordoServizioParteSpecifica}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccordoServizioParteSpecifica readAccordoServizioParteSpecifica(byte[] in) throws DeserializerException {
		return (AccordoServizioParteSpecifica) this.xmlToObj(in, AccordoServizioParteSpecifica.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.registry.AccordoServizioParteSpecifica}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.AccordoServizioParteSpecifica}
	 * @return Object type {@link org.openspcoop2.core.registry.AccordoServizioParteSpecifica}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccordoServizioParteSpecifica readAccordoServizioParteSpecificaFromString(String in) throws DeserializerException {
		return (AccordoServizioParteSpecifica) this.xmlToObj(in.getBytes(), AccordoServizioParteSpecifica.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: fruitore
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.registry.Fruitore}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.Fruitore}
	 * @return Object type {@link org.openspcoop2.core.registry.Fruitore}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Fruitore readFruitore(String fileName) throws DeserializerException {
		return (Fruitore) this.xmlToObj(fileName, Fruitore.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.registry.Fruitore}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.Fruitore}
	 * @return Object type {@link org.openspcoop2.core.registry.Fruitore}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Fruitore readFruitore(File file) throws DeserializerException {
		return (Fruitore) this.xmlToObj(file, Fruitore.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.registry.Fruitore}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.Fruitore}
	 * @return Object type {@link org.openspcoop2.core.registry.Fruitore}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Fruitore readFruitore(InputStream in) throws DeserializerException {
		return (Fruitore) this.xmlToObj(in, Fruitore.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.registry.Fruitore}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.Fruitore}
	 * @return Object type {@link org.openspcoop2.core.registry.Fruitore}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Fruitore readFruitore(byte[] in) throws DeserializerException {
		return (Fruitore) this.xmlToObj(in, Fruitore.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.registry.Fruitore}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.Fruitore}
	 * @return Object type {@link org.openspcoop2.core.registry.Fruitore}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Fruitore readFruitoreFromString(String in) throws DeserializerException {
		return (Fruitore) this.xmlToObj(in.getBytes(), Fruitore.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: Property
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.registry.Property}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.Property}
	 * @return Object type {@link org.openspcoop2.core.registry.Property}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Property readProperty(String fileName) throws DeserializerException {
		return (Property) this.xmlToObj(fileName, Property.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.registry.Property}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.Property}
	 * @return Object type {@link org.openspcoop2.core.registry.Property}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Property readProperty(File file) throws DeserializerException {
		return (Property) this.xmlToObj(file, Property.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.registry.Property}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.Property}
	 * @return Object type {@link org.openspcoop2.core.registry.Property}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Property readProperty(InputStream in) throws DeserializerException {
		return (Property) this.xmlToObj(in, Property.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.registry.Property}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.Property}
	 * @return Object type {@link org.openspcoop2.core.registry.Property}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Property readProperty(byte[] in) throws DeserializerException {
		return (Property) this.xmlToObj(in, Property.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.registry.Property}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.Property}
	 * @return Object type {@link org.openspcoop2.core.registry.Property}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Property readPropertyFromString(String in) throws DeserializerException {
		return (Property) this.xmlToObj(in.getBytes(), Property.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: ruolo
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.registry.Ruolo}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.Ruolo}
	 * @return Object type {@link org.openspcoop2.core.registry.Ruolo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Ruolo readRuolo(String fileName) throws DeserializerException {
		return (Ruolo) this.xmlToObj(fileName, Ruolo.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.registry.Ruolo}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.Ruolo}
	 * @return Object type {@link org.openspcoop2.core.registry.Ruolo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Ruolo readRuolo(File file) throws DeserializerException {
		return (Ruolo) this.xmlToObj(file, Ruolo.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.registry.Ruolo}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.Ruolo}
	 * @return Object type {@link org.openspcoop2.core.registry.Ruolo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Ruolo readRuolo(InputStream in) throws DeserializerException {
		return (Ruolo) this.xmlToObj(in, Ruolo.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.registry.Ruolo}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.Ruolo}
	 * @return Object type {@link org.openspcoop2.core.registry.Ruolo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Ruolo readRuolo(byte[] in) throws DeserializerException {
		return (Ruolo) this.xmlToObj(in, Ruolo.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.registry.Ruolo}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.Ruolo}
	 * @return Object type {@link org.openspcoop2.core.registry.Ruolo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Ruolo readRuoloFromString(String in) throws DeserializerException {
		return (Ruolo) this.xmlToObj(in.getBytes(), Ruolo.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: accordo-cooperazione-partecipanti
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.registry.AccordoCooperazionePartecipanti}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.AccordoCooperazionePartecipanti}
	 * @return Object type {@link org.openspcoop2.core.registry.AccordoCooperazionePartecipanti}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccordoCooperazionePartecipanti readAccordoCooperazionePartecipanti(String fileName) throws DeserializerException {
		return (AccordoCooperazionePartecipanti) this.xmlToObj(fileName, AccordoCooperazionePartecipanti.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.registry.AccordoCooperazionePartecipanti}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.AccordoCooperazionePartecipanti}
	 * @return Object type {@link org.openspcoop2.core.registry.AccordoCooperazionePartecipanti}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccordoCooperazionePartecipanti readAccordoCooperazionePartecipanti(File file) throws DeserializerException {
		return (AccordoCooperazionePartecipanti) this.xmlToObj(file, AccordoCooperazionePartecipanti.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.registry.AccordoCooperazionePartecipanti}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.AccordoCooperazionePartecipanti}
	 * @return Object type {@link org.openspcoop2.core.registry.AccordoCooperazionePartecipanti}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccordoCooperazionePartecipanti readAccordoCooperazionePartecipanti(InputStream in) throws DeserializerException {
		return (AccordoCooperazionePartecipanti) this.xmlToObj(in, AccordoCooperazionePartecipanti.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.registry.AccordoCooperazionePartecipanti}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.AccordoCooperazionePartecipanti}
	 * @return Object type {@link org.openspcoop2.core.registry.AccordoCooperazionePartecipanti}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccordoCooperazionePartecipanti readAccordoCooperazionePartecipanti(byte[] in) throws DeserializerException {
		return (AccordoCooperazionePartecipanti) this.xmlToObj(in, AccordoCooperazionePartecipanti.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.registry.AccordoCooperazionePartecipanti}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.AccordoCooperazionePartecipanti}
	 * @return Object type {@link org.openspcoop2.core.registry.AccordoCooperazionePartecipanti}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccordoCooperazionePartecipanti readAccordoCooperazionePartecipantiFromString(String in) throws DeserializerException {
		return (AccordoCooperazionePartecipanti) this.xmlToObj(in.getBytes(), AccordoCooperazionePartecipanti.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: azione
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.registry.Azione}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.Azione}
	 * @return Object type {@link org.openspcoop2.core.registry.Azione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Azione readAzione(String fileName) throws DeserializerException {
		return (Azione) this.xmlToObj(fileName, Azione.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.registry.Azione}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.Azione}
	 * @return Object type {@link org.openspcoop2.core.registry.Azione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Azione readAzione(File file) throws DeserializerException {
		return (Azione) this.xmlToObj(file, Azione.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.registry.Azione}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.Azione}
	 * @return Object type {@link org.openspcoop2.core.registry.Azione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Azione readAzione(InputStream in) throws DeserializerException {
		return (Azione) this.xmlToObj(in, Azione.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.registry.Azione}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.Azione}
	 * @return Object type {@link org.openspcoop2.core.registry.Azione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Azione readAzione(byte[] in) throws DeserializerException {
		return (Azione) this.xmlToObj(in, Azione.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.registry.Azione}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.Azione}
	 * @return Object type {@link org.openspcoop2.core.registry.Azione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Azione readAzioneFromString(String in) throws DeserializerException {
		return (Azione) this.xmlToObj(in.getBytes(), Azione.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: connettore
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.registry.Connettore}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.Connettore}
	 * @return Object type {@link org.openspcoop2.core.registry.Connettore}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Connettore readConnettore(String fileName) throws DeserializerException {
		return (Connettore) this.xmlToObj(fileName, Connettore.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.registry.Connettore}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.Connettore}
	 * @return Object type {@link org.openspcoop2.core.registry.Connettore}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Connettore readConnettore(File file) throws DeserializerException {
		return (Connettore) this.xmlToObj(file, Connettore.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.registry.Connettore}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.Connettore}
	 * @return Object type {@link org.openspcoop2.core.registry.Connettore}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Connettore readConnettore(InputStream in) throws DeserializerException {
		return (Connettore) this.xmlToObj(in, Connettore.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.registry.Connettore}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.Connettore}
	 * @return Object type {@link org.openspcoop2.core.registry.Connettore}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Connettore readConnettore(byte[] in) throws DeserializerException {
		return (Connettore) this.xmlToObj(in, Connettore.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.registry.Connettore}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.Connettore}
	 * @return Object type {@link org.openspcoop2.core.registry.Connettore}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Connettore readConnettoreFromString(String in) throws DeserializerException {
		return (Connettore) this.xmlToObj(in.getBytes(), Connettore.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: configurazione-servizio-azione
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.registry.ConfigurazioneServizioAzione}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.ConfigurazioneServizioAzione}
	 * @return Object type {@link org.openspcoop2.core.registry.ConfigurazioneServizioAzione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConfigurazioneServizioAzione readConfigurazioneServizioAzione(String fileName) throws DeserializerException {
		return (ConfigurazioneServizioAzione) this.xmlToObj(fileName, ConfigurazioneServizioAzione.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.registry.ConfigurazioneServizioAzione}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.ConfigurazioneServizioAzione}
	 * @return Object type {@link org.openspcoop2.core.registry.ConfigurazioneServizioAzione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConfigurazioneServizioAzione readConfigurazioneServizioAzione(File file) throws DeserializerException {
		return (ConfigurazioneServizioAzione) this.xmlToObj(file, ConfigurazioneServizioAzione.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.registry.ConfigurazioneServizioAzione}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.ConfigurazioneServizioAzione}
	 * @return Object type {@link org.openspcoop2.core.registry.ConfigurazioneServizioAzione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConfigurazioneServizioAzione readConfigurazioneServizioAzione(InputStream in) throws DeserializerException {
		return (ConfigurazioneServizioAzione) this.xmlToObj(in, ConfigurazioneServizioAzione.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.registry.ConfigurazioneServizioAzione}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.ConfigurazioneServizioAzione}
	 * @return Object type {@link org.openspcoop2.core.registry.ConfigurazioneServizioAzione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConfigurazioneServizioAzione readConfigurazioneServizioAzione(byte[] in) throws DeserializerException {
		return (ConfigurazioneServizioAzione) this.xmlToObj(in, ConfigurazioneServizioAzione.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.registry.ConfigurazioneServizioAzione}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.ConfigurazioneServizioAzione}
	 * @return Object type {@link org.openspcoop2.core.registry.ConfigurazioneServizioAzione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConfigurazioneServizioAzione readConfigurazioneServizioAzioneFromString(String in) throws DeserializerException {
		return (ConfigurazioneServizioAzione) this.xmlToObj(in.getBytes(), ConfigurazioneServizioAzione.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: ruolo-soggetto
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.registry.RuoloSoggetto}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.RuoloSoggetto}
	 * @return Object type {@link org.openspcoop2.core.registry.RuoloSoggetto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RuoloSoggetto readRuoloSoggetto(String fileName) throws DeserializerException {
		return (RuoloSoggetto) this.xmlToObj(fileName, RuoloSoggetto.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.registry.RuoloSoggetto}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.RuoloSoggetto}
	 * @return Object type {@link org.openspcoop2.core.registry.RuoloSoggetto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RuoloSoggetto readRuoloSoggetto(File file) throws DeserializerException {
		return (RuoloSoggetto) this.xmlToObj(file, RuoloSoggetto.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.registry.RuoloSoggetto}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.RuoloSoggetto}
	 * @return Object type {@link org.openspcoop2.core.registry.RuoloSoggetto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RuoloSoggetto readRuoloSoggetto(InputStream in) throws DeserializerException {
		return (RuoloSoggetto) this.xmlToObj(in, RuoloSoggetto.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.registry.RuoloSoggetto}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.RuoloSoggetto}
	 * @return Object type {@link org.openspcoop2.core.registry.RuoloSoggetto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RuoloSoggetto readRuoloSoggetto(byte[] in) throws DeserializerException {
		return (RuoloSoggetto) this.xmlToObj(in, RuoloSoggetto.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.registry.RuoloSoggetto}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.RuoloSoggetto}
	 * @return Object type {@link org.openspcoop2.core.registry.RuoloSoggetto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RuoloSoggetto readRuoloSoggettoFromString(String in) throws DeserializerException {
		return (RuoloSoggetto) this.xmlToObj(in.getBytes(), RuoloSoggetto.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: resource-representation-xml
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.registry.ResourceRepresentationXml}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.ResourceRepresentationXml}
	 * @return Object type {@link org.openspcoop2.core.registry.ResourceRepresentationXml}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ResourceRepresentationXml readResourceRepresentationXml(String fileName) throws DeserializerException {
		return (ResourceRepresentationXml) this.xmlToObj(fileName, ResourceRepresentationXml.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.registry.ResourceRepresentationXml}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.ResourceRepresentationXml}
	 * @return Object type {@link org.openspcoop2.core.registry.ResourceRepresentationXml}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ResourceRepresentationXml readResourceRepresentationXml(File file) throws DeserializerException {
		return (ResourceRepresentationXml) this.xmlToObj(file, ResourceRepresentationXml.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.registry.ResourceRepresentationXml}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.ResourceRepresentationXml}
	 * @return Object type {@link org.openspcoop2.core.registry.ResourceRepresentationXml}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ResourceRepresentationXml readResourceRepresentationXml(InputStream in) throws DeserializerException {
		return (ResourceRepresentationXml) this.xmlToObj(in, ResourceRepresentationXml.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.registry.ResourceRepresentationXml}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.ResourceRepresentationXml}
	 * @return Object type {@link org.openspcoop2.core.registry.ResourceRepresentationXml}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ResourceRepresentationXml readResourceRepresentationXml(byte[] in) throws DeserializerException {
		return (ResourceRepresentationXml) this.xmlToObj(in, ResourceRepresentationXml.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.registry.ResourceRepresentationXml}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.ResourceRepresentationXml}
	 * @return Object type {@link org.openspcoop2.core.registry.ResourceRepresentationXml}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ResourceRepresentationXml readResourceRepresentationXmlFromString(String in) throws DeserializerException {
		return (ResourceRepresentationXml) this.xmlToObj(in.getBytes(), ResourceRepresentationXml.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: accordo-servizio-parte-comune-servizio-composto-servizio-componente
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.registry.AccordoServizioParteComuneServizioCompostoServizioComponente}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.AccordoServizioParteComuneServizioCompostoServizioComponente}
	 * @return Object type {@link org.openspcoop2.core.registry.AccordoServizioParteComuneServizioCompostoServizioComponente}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccordoServizioParteComuneServizioCompostoServizioComponente readAccordoServizioParteComuneServizioCompostoServizioComponente(String fileName) throws DeserializerException {
		return (AccordoServizioParteComuneServizioCompostoServizioComponente) this.xmlToObj(fileName, AccordoServizioParteComuneServizioCompostoServizioComponente.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.registry.AccordoServizioParteComuneServizioCompostoServizioComponente}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.AccordoServizioParteComuneServizioCompostoServizioComponente}
	 * @return Object type {@link org.openspcoop2.core.registry.AccordoServizioParteComuneServizioCompostoServizioComponente}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccordoServizioParteComuneServizioCompostoServizioComponente readAccordoServizioParteComuneServizioCompostoServizioComponente(File file) throws DeserializerException {
		return (AccordoServizioParteComuneServizioCompostoServizioComponente) this.xmlToObj(file, AccordoServizioParteComuneServizioCompostoServizioComponente.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.registry.AccordoServizioParteComuneServizioCompostoServizioComponente}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.AccordoServizioParteComuneServizioCompostoServizioComponente}
	 * @return Object type {@link org.openspcoop2.core.registry.AccordoServizioParteComuneServizioCompostoServizioComponente}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccordoServizioParteComuneServizioCompostoServizioComponente readAccordoServizioParteComuneServizioCompostoServizioComponente(InputStream in) throws DeserializerException {
		return (AccordoServizioParteComuneServizioCompostoServizioComponente) this.xmlToObj(in, AccordoServizioParteComuneServizioCompostoServizioComponente.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.registry.AccordoServizioParteComuneServizioCompostoServizioComponente}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.AccordoServizioParteComuneServizioCompostoServizioComponente}
	 * @return Object type {@link org.openspcoop2.core.registry.AccordoServizioParteComuneServizioCompostoServizioComponente}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccordoServizioParteComuneServizioCompostoServizioComponente readAccordoServizioParteComuneServizioCompostoServizioComponente(byte[] in) throws DeserializerException {
		return (AccordoServizioParteComuneServizioCompostoServizioComponente) this.xmlToObj(in, AccordoServizioParteComuneServizioCompostoServizioComponente.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.registry.AccordoServizioParteComuneServizioCompostoServizioComponente}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.AccordoServizioParteComuneServizioCompostoServizioComponente}
	 * @return Object type {@link org.openspcoop2.core.registry.AccordoServizioParteComuneServizioCompostoServizioComponente}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccordoServizioParteComuneServizioCompostoServizioComponente readAccordoServizioParteComuneServizioCompostoServizioComponenteFromString(String in) throws DeserializerException {
		return (AccordoServizioParteComuneServizioCompostoServizioComponente) this.xmlToObj(in.getBytes(), AccordoServizioParteComuneServizioCompostoServizioComponente.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: accordo-servizio-parte-comune-servizio-composto
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.registry.AccordoServizioParteComuneServizioComposto}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.AccordoServizioParteComuneServizioComposto}
	 * @return Object type {@link org.openspcoop2.core.registry.AccordoServizioParteComuneServizioComposto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccordoServizioParteComuneServizioComposto readAccordoServizioParteComuneServizioComposto(String fileName) throws DeserializerException {
		return (AccordoServizioParteComuneServizioComposto) this.xmlToObj(fileName, AccordoServizioParteComuneServizioComposto.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.registry.AccordoServizioParteComuneServizioComposto}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.AccordoServizioParteComuneServizioComposto}
	 * @return Object type {@link org.openspcoop2.core.registry.AccordoServizioParteComuneServizioComposto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccordoServizioParteComuneServizioComposto readAccordoServizioParteComuneServizioComposto(File file) throws DeserializerException {
		return (AccordoServizioParteComuneServizioComposto) this.xmlToObj(file, AccordoServizioParteComuneServizioComposto.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.registry.AccordoServizioParteComuneServizioComposto}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.AccordoServizioParteComuneServizioComposto}
	 * @return Object type {@link org.openspcoop2.core.registry.AccordoServizioParteComuneServizioComposto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccordoServizioParteComuneServizioComposto readAccordoServizioParteComuneServizioComposto(InputStream in) throws DeserializerException {
		return (AccordoServizioParteComuneServizioComposto) this.xmlToObj(in, AccordoServizioParteComuneServizioComposto.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.registry.AccordoServizioParteComuneServizioComposto}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.AccordoServizioParteComuneServizioComposto}
	 * @return Object type {@link org.openspcoop2.core.registry.AccordoServizioParteComuneServizioComposto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccordoServizioParteComuneServizioComposto readAccordoServizioParteComuneServizioComposto(byte[] in) throws DeserializerException {
		return (AccordoServizioParteComuneServizioComposto) this.xmlToObj(in, AccordoServizioParteComuneServizioComposto.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.registry.AccordoServizioParteComuneServizioComposto}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.AccordoServizioParteComuneServizioComposto}
	 * @return Object type {@link org.openspcoop2.core.registry.AccordoServizioParteComuneServizioComposto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccordoServizioParteComuneServizioComposto readAccordoServizioParteComuneServizioCompostoFromString(String in) throws DeserializerException {
		return (AccordoServizioParteComuneServizioComposto) this.xmlToObj(in.getBytes(), AccordoServizioParteComuneServizioComposto.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: port-type
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.registry.PortType}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.PortType}
	 * @return Object type {@link org.openspcoop2.core.registry.PortType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PortType readPortType(String fileName) throws DeserializerException {
		return (PortType) this.xmlToObj(fileName, PortType.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.registry.PortType}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.PortType}
	 * @return Object type {@link org.openspcoop2.core.registry.PortType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PortType readPortType(File file) throws DeserializerException {
		return (PortType) this.xmlToObj(file, PortType.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.registry.PortType}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.PortType}
	 * @return Object type {@link org.openspcoop2.core.registry.PortType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PortType readPortType(InputStream in) throws DeserializerException {
		return (PortType) this.xmlToObj(in, PortType.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.registry.PortType}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.PortType}
	 * @return Object type {@link org.openspcoop2.core.registry.PortType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PortType readPortType(byte[] in) throws DeserializerException {
		return (PortType) this.xmlToObj(in, PortType.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.registry.PortType}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.PortType}
	 * @return Object type {@link org.openspcoop2.core.registry.PortType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PortType readPortTypeFromString(String in) throws DeserializerException {
		return (PortType) this.xmlToObj(in.getBytes(), PortType.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: id-accordo-cooperazione
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.registry.IdAccordoCooperazione}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.IdAccordoCooperazione}
	 * @return Object type {@link org.openspcoop2.core.registry.IdAccordoCooperazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdAccordoCooperazione readIdAccordoCooperazione(String fileName) throws DeserializerException {
		return (IdAccordoCooperazione) this.xmlToObj(fileName, IdAccordoCooperazione.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.registry.IdAccordoCooperazione}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.IdAccordoCooperazione}
	 * @return Object type {@link org.openspcoop2.core.registry.IdAccordoCooperazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdAccordoCooperazione readIdAccordoCooperazione(File file) throws DeserializerException {
		return (IdAccordoCooperazione) this.xmlToObj(file, IdAccordoCooperazione.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.registry.IdAccordoCooperazione}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.IdAccordoCooperazione}
	 * @return Object type {@link org.openspcoop2.core.registry.IdAccordoCooperazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdAccordoCooperazione readIdAccordoCooperazione(InputStream in) throws DeserializerException {
		return (IdAccordoCooperazione) this.xmlToObj(in, IdAccordoCooperazione.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.registry.IdAccordoCooperazione}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.IdAccordoCooperazione}
	 * @return Object type {@link org.openspcoop2.core.registry.IdAccordoCooperazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdAccordoCooperazione readIdAccordoCooperazione(byte[] in) throws DeserializerException {
		return (IdAccordoCooperazione) this.xmlToObj(in, IdAccordoCooperazione.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.registry.IdAccordoCooperazione}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.IdAccordoCooperazione}
	 * @return Object type {@link org.openspcoop2.core.registry.IdAccordoCooperazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdAccordoCooperazione readIdAccordoCooperazioneFromString(String in) throws DeserializerException {
		return (IdAccordoCooperazione) this.xmlToObj(in.getBytes(), IdAccordoCooperazione.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: accordo-cooperazione
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.registry.AccordoCooperazione}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.AccordoCooperazione}
	 * @return Object type {@link org.openspcoop2.core.registry.AccordoCooperazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccordoCooperazione readAccordoCooperazione(String fileName) throws DeserializerException {
		return (AccordoCooperazione) this.xmlToObj(fileName, AccordoCooperazione.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.registry.AccordoCooperazione}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.AccordoCooperazione}
	 * @return Object type {@link org.openspcoop2.core.registry.AccordoCooperazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccordoCooperazione readAccordoCooperazione(File file) throws DeserializerException {
		return (AccordoCooperazione) this.xmlToObj(file, AccordoCooperazione.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.registry.AccordoCooperazione}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.AccordoCooperazione}
	 * @return Object type {@link org.openspcoop2.core.registry.AccordoCooperazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccordoCooperazione readAccordoCooperazione(InputStream in) throws DeserializerException {
		return (AccordoCooperazione) this.xmlToObj(in, AccordoCooperazione.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.registry.AccordoCooperazione}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.AccordoCooperazione}
	 * @return Object type {@link org.openspcoop2.core.registry.AccordoCooperazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccordoCooperazione readAccordoCooperazione(byte[] in) throws DeserializerException {
		return (AccordoCooperazione) this.xmlToObj(in, AccordoCooperazione.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.registry.AccordoCooperazione}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.AccordoCooperazione}
	 * @return Object type {@link org.openspcoop2.core.registry.AccordoCooperazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccordoCooperazione readAccordoCooperazioneFromString(String in) throws DeserializerException {
		return (AccordoCooperazione) this.xmlToObj(in.getBytes(), AccordoCooperazione.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: credenziali-soggetto
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.registry.CredenzialiSoggetto}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.CredenzialiSoggetto}
	 * @return Object type {@link org.openspcoop2.core.registry.CredenzialiSoggetto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public CredenzialiSoggetto readCredenzialiSoggetto(String fileName) throws DeserializerException {
		return (CredenzialiSoggetto) this.xmlToObj(fileName, CredenzialiSoggetto.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.registry.CredenzialiSoggetto}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.CredenzialiSoggetto}
	 * @return Object type {@link org.openspcoop2.core.registry.CredenzialiSoggetto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public CredenzialiSoggetto readCredenzialiSoggetto(File file) throws DeserializerException {
		return (CredenzialiSoggetto) this.xmlToObj(file, CredenzialiSoggetto.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.registry.CredenzialiSoggetto}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.CredenzialiSoggetto}
	 * @return Object type {@link org.openspcoop2.core.registry.CredenzialiSoggetto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public CredenzialiSoggetto readCredenzialiSoggetto(InputStream in) throws DeserializerException {
		return (CredenzialiSoggetto) this.xmlToObj(in, CredenzialiSoggetto.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.registry.CredenzialiSoggetto}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.CredenzialiSoggetto}
	 * @return Object type {@link org.openspcoop2.core.registry.CredenzialiSoggetto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public CredenzialiSoggetto readCredenzialiSoggetto(byte[] in) throws DeserializerException {
		return (CredenzialiSoggetto) this.xmlToObj(in, CredenzialiSoggetto.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.registry.CredenzialiSoggetto}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.CredenzialiSoggetto}
	 * @return Object type {@link org.openspcoop2.core.registry.CredenzialiSoggetto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public CredenzialiSoggetto readCredenzialiSoggettoFromString(String in) throws DeserializerException {
		return (CredenzialiSoggetto) this.xmlToObj(in.getBytes(), CredenzialiSoggetto.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: registro-servizi
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.registry.RegistroServizi}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.RegistroServizi}
	 * @return Object type {@link org.openspcoop2.core.registry.RegistroServizi}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RegistroServizi readRegistroServizi(String fileName) throws DeserializerException {
		return (RegistroServizi) this.xmlToObj(fileName, RegistroServizi.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.registry.RegistroServizi}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.RegistroServizi}
	 * @return Object type {@link org.openspcoop2.core.registry.RegistroServizi}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RegistroServizi readRegistroServizi(File file) throws DeserializerException {
		return (RegistroServizi) this.xmlToObj(file, RegistroServizi.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.registry.RegistroServizi}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.RegistroServizi}
	 * @return Object type {@link org.openspcoop2.core.registry.RegistroServizi}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RegistroServizi readRegistroServizi(InputStream in) throws DeserializerException {
		return (RegistroServizi) this.xmlToObj(in, RegistroServizi.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.registry.RegistroServizi}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.RegistroServizi}
	 * @return Object type {@link org.openspcoop2.core.registry.RegistroServizi}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RegistroServizi readRegistroServizi(byte[] in) throws DeserializerException {
		return (RegistroServizi) this.xmlToObj(in, RegistroServizi.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.registry.RegistroServizi}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.RegistroServizi}
	 * @return Object type {@link org.openspcoop2.core.registry.RegistroServizi}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RegistroServizi readRegistroServiziFromString(String in) throws DeserializerException {
		return (RegistroServizi) this.xmlToObj(in.getBytes(), RegistroServizi.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: accordo-servizio-parte-comune
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.registry.AccordoServizioParteComune}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.AccordoServizioParteComune}
	 * @return Object type {@link org.openspcoop2.core.registry.AccordoServizioParteComune}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccordoServizioParteComune readAccordoServizioParteComune(String fileName) throws DeserializerException {
		return (AccordoServizioParteComune) this.xmlToObj(fileName, AccordoServizioParteComune.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.registry.AccordoServizioParteComune}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.AccordoServizioParteComune}
	 * @return Object type {@link org.openspcoop2.core.registry.AccordoServizioParteComune}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccordoServizioParteComune readAccordoServizioParteComune(File file) throws DeserializerException {
		return (AccordoServizioParteComune) this.xmlToObj(file, AccordoServizioParteComune.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.registry.AccordoServizioParteComune}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.AccordoServizioParteComune}
	 * @return Object type {@link org.openspcoop2.core.registry.AccordoServizioParteComune}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccordoServizioParteComune readAccordoServizioParteComune(InputStream in) throws DeserializerException {
		return (AccordoServizioParteComune) this.xmlToObj(in, AccordoServizioParteComune.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.registry.AccordoServizioParteComune}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.AccordoServizioParteComune}
	 * @return Object type {@link org.openspcoop2.core.registry.AccordoServizioParteComune}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccordoServizioParteComune readAccordoServizioParteComune(byte[] in) throws DeserializerException {
		return (AccordoServizioParteComune) this.xmlToObj(in, AccordoServizioParteComune.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.registry.AccordoServizioParteComune}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.AccordoServizioParteComune}
	 * @return Object type {@link org.openspcoop2.core.registry.AccordoServizioParteComune}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccordoServizioParteComune readAccordoServizioParteComuneFromString(String in) throws DeserializerException {
		return (AccordoServizioParteComune) this.xmlToObj(in.getBytes(), AccordoServizioParteComune.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: porta-dominio
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.registry.PortaDominio}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.PortaDominio}
	 * @return Object type {@link org.openspcoop2.core.registry.PortaDominio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PortaDominio readPortaDominio(String fileName) throws DeserializerException {
		return (PortaDominio) this.xmlToObj(fileName, PortaDominio.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.registry.PortaDominio}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.PortaDominio}
	 * @return Object type {@link org.openspcoop2.core.registry.PortaDominio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PortaDominio readPortaDominio(File file) throws DeserializerException {
		return (PortaDominio) this.xmlToObj(file, PortaDominio.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.registry.PortaDominio}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.PortaDominio}
	 * @return Object type {@link org.openspcoop2.core.registry.PortaDominio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PortaDominio readPortaDominio(InputStream in) throws DeserializerException {
		return (PortaDominio) this.xmlToObj(in, PortaDominio.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.registry.PortaDominio}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.PortaDominio}
	 * @return Object type {@link org.openspcoop2.core.registry.PortaDominio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PortaDominio readPortaDominio(byte[] in) throws DeserializerException {
		return (PortaDominio) this.xmlToObj(in, PortaDominio.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.registry.PortaDominio}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.PortaDominio}
	 * @return Object type {@link org.openspcoop2.core.registry.PortaDominio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PortaDominio readPortaDominioFromString(String in) throws DeserializerException {
		return (PortaDominio) this.xmlToObj(in.getBytes(), PortaDominio.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: soggetto
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.registry.Soggetto}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.Soggetto}
	 * @return Object type {@link org.openspcoop2.core.registry.Soggetto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Soggetto readSoggetto(String fileName) throws DeserializerException {
		return (Soggetto) this.xmlToObj(fileName, Soggetto.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.registry.Soggetto}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.Soggetto}
	 * @return Object type {@link org.openspcoop2.core.registry.Soggetto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Soggetto readSoggetto(File file) throws DeserializerException {
		return (Soggetto) this.xmlToObj(file, Soggetto.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.registry.Soggetto}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.Soggetto}
	 * @return Object type {@link org.openspcoop2.core.registry.Soggetto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Soggetto readSoggetto(InputStream in) throws DeserializerException {
		return (Soggetto) this.xmlToObj(in, Soggetto.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.registry.Soggetto}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.Soggetto}
	 * @return Object type {@link org.openspcoop2.core.registry.Soggetto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Soggetto readSoggetto(byte[] in) throws DeserializerException {
		return (Soggetto) this.xmlToObj(in, Soggetto.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.registry.Soggetto}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.Soggetto}
	 * @return Object type {@link org.openspcoop2.core.registry.Soggetto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Soggetto readSoggettoFromString(String in) throws DeserializerException {
		return (Soggetto) this.xmlToObj(in.getBytes(), Soggetto.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: ruoli-soggetto
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.registry.RuoliSoggetto}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.RuoliSoggetto}
	 * @return Object type {@link org.openspcoop2.core.registry.RuoliSoggetto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RuoliSoggetto readRuoliSoggetto(String fileName) throws DeserializerException {
		return (RuoliSoggetto) this.xmlToObj(fileName, RuoliSoggetto.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.registry.RuoliSoggetto}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.RuoliSoggetto}
	 * @return Object type {@link org.openspcoop2.core.registry.RuoliSoggetto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RuoliSoggetto readRuoliSoggetto(File file) throws DeserializerException {
		return (RuoliSoggetto) this.xmlToObj(file, RuoliSoggetto.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.registry.RuoliSoggetto}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.RuoliSoggetto}
	 * @return Object type {@link org.openspcoop2.core.registry.RuoliSoggetto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RuoliSoggetto readRuoliSoggetto(InputStream in) throws DeserializerException {
		return (RuoliSoggetto) this.xmlToObj(in, RuoliSoggetto.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.registry.RuoliSoggetto}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.RuoliSoggetto}
	 * @return Object type {@link org.openspcoop2.core.registry.RuoliSoggetto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RuoliSoggetto readRuoliSoggetto(byte[] in) throws DeserializerException {
		return (RuoliSoggetto) this.xmlToObj(in, RuoliSoggetto.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.registry.RuoliSoggetto}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.RuoliSoggetto}
	 * @return Object type {@link org.openspcoop2.core.registry.RuoliSoggetto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RuoliSoggetto readRuoliSoggettoFromString(String in) throws DeserializerException {
		return (RuoliSoggetto) this.xmlToObj(in.getBytes(), RuoliSoggetto.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: id-accordo-servizio-parte-comune
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.registry.IdAccordoServizioParteComune}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.IdAccordoServizioParteComune}
	 * @return Object type {@link org.openspcoop2.core.registry.IdAccordoServizioParteComune}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdAccordoServizioParteComune readIdAccordoServizioParteComune(String fileName) throws DeserializerException {
		return (IdAccordoServizioParteComune) this.xmlToObj(fileName, IdAccordoServizioParteComune.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.registry.IdAccordoServizioParteComune}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.IdAccordoServizioParteComune}
	 * @return Object type {@link org.openspcoop2.core.registry.IdAccordoServizioParteComune}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdAccordoServizioParteComune readIdAccordoServizioParteComune(File file) throws DeserializerException {
		return (IdAccordoServizioParteComune) this.xmlToObj(file, IdAccordoServizioParteComune.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.registry.IdAccordoServizioParteComune}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.IdAccordoServizioParteComune}
	 * @return Object type {@link org.openspcoop2.core.registry.IdAccordoServizioParteComune}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdAccordoServizioParteComune readIdAccordoServizioParteComune(InputStream in) throws DeserializerException {
		return (IdAccordoServizioParteComune) this.xmlToObj(in, IdAccordoServizioParteComune.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.registry.IdAccordoServizioParteComune}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.IdAccordoServizioParteComune}
	 * @return Object type {@link org.openspcoop2.core.registry.IdAccordoServizioParteComune}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdAccordoServizioParteComune readIdAccordoServizioParteComune(byte[] in) throws DeserializerException {
		return (IdAccordoServizioParteComune) this.xmlToObj(in, IdAccordoServizioParteComune.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.registry.IdAccordoServizioParteComune}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.IdAccordoServizioParteComune}
	 * @return Object type {@link org.openspcoop2.core.registry.IdAccordoServizioParteComune}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdAccordoServizioParteComune readIdAccordoServizioParteComuneFromString(String in) throws DeserializerException {
		return (IdAccordoServizioParteComune) this.xmlToObj(in.getBytes(), IdAccordoServizioParteComune.class);
	}	
	
	
	

}
