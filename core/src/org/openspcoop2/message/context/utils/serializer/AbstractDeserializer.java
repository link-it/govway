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
package org.openspcoop2.message.context.utils.serializer;

import org.openspcoop2.generic_project.exception.DeserializerException;

import org.openspcoop2.message.context.StringParameter;
import org.openspcoop2.message.context.UrlParameters;
import org.openspcoop2.message.context.ContentLength;
import org.openspcoop2.message.context.TransportRequestContext;
import org.openspcoop2.message.context.HeaderParameters;
import org.openspcoop2.message.context.Credentials;
import org.openspcoop2.message.context.SerializedParameter;
import org.openspcoop2.message.context.SerializedContext;
import org.openspcoop2.message.context.ForcedResponse;
import org.openspcoop2.message.context.ForcedResponseMessage;
import org.openspcoop2.message.context.TransportResponseContext;
import org.openspcoop2.message.context.Soap;
import org.openspcoop2.message.context.MessageContext;
import org.openspcoop2.message.context.ContentTypeParameters;

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
	 Object: string-parameter
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.message.context.StringParameter}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.message.context.StringParameter}
	 * @return Object type {@link org.openspcoop2.message.context.StringParameter}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public StringParameter readStringParameter(String fileName) throws DeserializerException {
		return (StringParameter) this.xmlToObj(fileName, StringParameter.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.message.context.StringParameter}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.message.context.StringParameter}
	 * @return Object type {@link org.openspcoop2.message.context.StringParameter}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public StringParameter readStringParameter(File file) throws DeserializerException {
		return (StringParameter) this.xmlToObj(file, StringParameter.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.message.context.StringParameter}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.message.context.StringParameter}
	 * @return Object type {@link org.openspcoop2.message.context.StringParameter}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public StringParameter readStringParameter(InputStream in) throws DeserializerException {
		return (StringParameter) this.xmlToObj(in, StringParameter.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.message.context.StringParameter}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.message.context.StringParameter}
	 * @return Object type {@link org.openspcoop2.message.context.StringParameter}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public StringParameter readStringParameter(byte[] in) throws DeserializerException {
		return (StringParameter) this.xmlToObj(in, StringParameter.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.message.context.StringParameter}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.message.context.StringParameter}
	 * @return Object type {@link org.openspcoop2.message.context.StringParameter}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public StringParameter readStringParameterFromString(String in) throws DeserializerException {
		return (StringParameter) this.xmlToObj(in.getBytes(), StringParameter.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: url-parameters
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.message.context.UrlParameters}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.message.context.UrlParameters}
	 * @return Object type {@link org.openspcoop2.message.context.UrlParameters}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public UrlParameters readUrlParameters(String fileName) throws DeserializerException {
		return (UrlParameters) this.xmlToObj(fileName, UrlParameters.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.message.context.UrlParameters}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.message.context.UrlParameters}
	 * @return Object type {@link org.openspcoop2.message.context.UrlParameters}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public UrlParameters readUrlParameters(File file) throws DeserializerException {
		return (UrlParameters) this.xmlToObj(file, UrlParameters.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.message.context.UrlParameters}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.message.context.UrlParameters}
	 * @return Object type {@link org.openspcoop2.message.context.UrlParameters}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public UrlParameters readUrlParameters(InputStream in) throws DeserializerException {
		return (UrlParameters) this.xmlToObj(in, UrlParameters.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.message.context.UrlParameters}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.message.context.UrlParameters}
	 * @return Object type {@link org.openspcoop2.message.context.UrlParameters}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public UrlParameters readUrlParameters(byte[] in) throws DeserializerException {
		return (UrlParameters) this.xmlToObj(in, UrlParameters.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.message.context.UrlParameters}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.message.context.UrlParameters}
	 * @return Object type {@link org.openspcoop2.message.context.UrlParameters}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public UrlParameters readUrlParametersFromString(String in) throws DeserializerException {
		return (UrlParameters) this.xmlToObj(in.getBytes(), UrlParameters.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: content-length
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.message.context.ContentLength}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.message.context.ContentLength}
	 * @return Object type {@link org.openspcoop2.message.context.ContentLength}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ContentLength readContentLength(String fileName) throws DeserializerException {
		return (ContentLength) this.xmlToObj(fileName, ContentLength.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.message.context.ContentLength}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.message.context.ContentLength}
	 * @return Object type {@link org.openspcoop2.message.context.ContentLength}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ContentLength readContentLength(File file) throws DeserializerException {
		return (ContentLength) this.xmlToObj(file, ContentLength.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.message.context.ContentLength}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.message.context.ContentLength}
	 * @return Object type {@link org.openspcoop2.message.context.ContentLength}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ContentLength readContentLength(InputStream in) throws DeserializerException {
		return (ContentLength) this.xmlToObj(in, ContentLength.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.message.context.ContentLength}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.message.context.ContentLength}
	 * @return Object type {@link org.openspcoop2.message.context.ContentLength}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ContentLength readContentLength(byte[] in) throws DeserializerException {
		return (ContentLength) this.xmlToObj(in, ContentLength.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.message.context.ContentLength}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.message.context.ContentLength}
	 * @return Object type {@link org.openspcoop2.message.context.ContentLength}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ContentLength readContentLengthFromString(String in) throws DeserializerException {
		return (ContentLength) this.xmlToObj(in.getBytes(), ContentLength.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: transport-request-context
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.message.context.TransportRequestContext}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.message.context.TransportRequestContext}
	 * @return Object type {@link org.openspcoop2.message.context.TransportRequestContext}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public TransportRequestContext readTransportRequestContext(String fileName) throws DeserializerException {
		return (TransportRequestContext) this.xmlToObj(fileName, TransportRequestContext.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.message.context.TransportRequestContext}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.message.context.TransportRequestContext}
	 * @return Object type {@link org.openspcoop2.message.context.TransportRequestContext}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public TransportRequestContext readTransportRequestContext(File file) throws DeserializerException {
		return (TransportRequestContext) this.xmlToObj(file, TransportRequestContext.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.message.context.TransportRequestContext}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.message.context.TransportRequestContext}
	 * @return Object type {@link org.openspcoop2.message.context.TransportRequestContext}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public TransportRequestContext readTransportRequestContext(InputStream in) throws DeserializerException {
		return (TransportRequestContext) this.xmlToObj(in, TransportRequestContext.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.message.context.TransportRequestContext}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.message.context.TransportRequestContext}
	 * @return Object type {@link org.openspcoop2.message.context.TransportRequestContext}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public TransportRequestContext readTransportRequestContext(byte[] in) throws DeserializerException {
		return (TransportRequestContext) this.xmlToObj(in, TransportRequestContext.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.message.context.TransportRequestContext}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.message.context.TransportRequestContext}
	 * @return Object type {@link org.openspcoop2.message.context.TransportRequestContext}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public TransportRequestContext readTransportRequestContextFromString(String in) throws DeserializerException {
		return (TransportRequestContext) this.xmlToObj(in.getBytes(), TransportRequestContext.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: header-parameters
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.message.context.HeaderParameters}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.message.context.HeaderParameters}
	 * @return Object type {@link org.openspcoop2.message.context.HeaderParameters}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public HeaderParameters readHeaderParameters(String fileName) throws DeserializerException {
		return (HeaderParameters) this.xmlToObj(fileName, HeaderParameters.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.message.context.HeaderParameters}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.message.context.HeaderParameters}
	 * @return Object type {@link org.openspcoop2.message.context.HeaderParameters}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public HeaderParameters readHeaderParameters(File file) throws DeserializerException {
		return (HeaderParameters) this.xmlToObj(file, HeaderParameters.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.message.context.HeaderParameters}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.message.context.HeaderParameters}
	 * @return Object type {@link org.openspcoop2.message.context.HeaderParameters}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public HeaderParameters readHeaderParameters(InputStream in) throws DeserializerException {
		return (HeaderParameters) this.xmlToObj(in, HeaderParameters.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.message.context.HeaderParameters}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.message.context.HeaderParameters}
	 * @return Object type {@link org.openspcoop2.message.context.HeaderParameters}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public HeaderParameters readHeaderParameters(byte[] in) throws DeserializerException {
		return (HeaderParameters) this.xmlToObj(in, HeaderParameters.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.message.context.HeaderParameters}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.message.context.HeaderParameters}
	 * @return Object type {@link org.openspcoop2.message.context.HeaderParameters}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public HeaderParameters readHeaderParametersFromString(String in) throws DeserializerException {
		return (HeaderParameters) this.xmlToObj(in.getBytes(), HeaderParameters.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: credentials
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.message.context.Credentials}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.message.context.Credentials}
	 * @return Object type {@link org.openspcoop2.message.context.Credentials}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Credentials readCredentials(String fileName) throws DeserializerException {
		return (Credentials) this.xmlToObj(fileName, Credentials.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.message.context.Credentials}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.message.context.Credentials}
	 * @return Object type {@link org.openspcoop2.message.context.Credentials}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Credentials readCredentials(File file) throws DeserializerException {
		return (Credentials) this.xmlToObj(file, Credentials.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.message.context.Credentials}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.message.context.Credentials}
	 * @return Object type {@link org.openspcoop2.message.context.Credentials}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Credentials readCredentials(InputStream in) throws DeserializerException {
		return (Credentials) this.xmlToObj(in, Credentials.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.message.context.Credentials}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.message.context.Credentials}
	 * @return Object type {@link org.openspcoop2.message.context.Credentials}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Credentials readCredentials(byte[] in) throws DeserializerException {
		return (Credentials) this.xmlToObj(in, Credentials.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.message.context.Credentials}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.message.context.Credentials}
	 * @return Object type {@link org.openspcoop2.message.context.Credentials}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Credentials readCredentialsFromString(String in) throws DeserializerException {
		return (Credentials) this.xmlToObj(in.getBytes(), Credentials.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: serialized-parameter
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.message.context.SerializedParameter}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.message.context.SerializedParameter}
	 * @return Object type {@link org.openspcoop2.message.context.SerializedParameter}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SerializedParameter readSerializedParameter(String fileName) throws DeserializerException {
		return (SerializedParameter) this.xmlToObj(fileName, SerializedParameter.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.message.context.SerializedParameter}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.message.context.SerializedParameter}
	 * @return Object type {@link org.openspcoop2.message.context.SerializedParameter}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SerializedParameter readSerializedParameter(File file) throws DeserializerException {
		return (SerializedParameter) this.xmlToObj(file, SerializedParameter.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.message.context.SerializedParameter}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.message.context.SerializedParameter}
	 * @return Object type {@link org.openspcoop2.message.context.SerializedParameter}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SerializedParameter readSerializedParameter(InputStream in) throws DeserializerException {
		return (SerializedParameter) this.xmlToObj(in, SerializedParameter.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.message.context.SerializedParameter}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.message.context.SerializedParameter}
	 * @return Object type {@link org.openspcoop2.message.context.SerializedParameter}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SerializedParameter readSerializedParameter(byte[] in) throws DeserializerException {
		return (SerializedParameter) this.xmlToObj(in, SerializedParameter.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.message.context.SerializedParameter}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.message.context.SerializedParameter}
	 * @return Object type {@link org.openspcoop2.message.context.SerializedParameter}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SerializedParameter readSerializedParameterFromString(String in) throws DeserializerException {
		return (SerializedParameter) this.xmlToObj(in.getBytes(), SerializedParameter.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: serialized-context
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.message.context.SerializedContext}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.message.context.SerializedContext}
	 * @return Object type {@link org.openspcoop2.message.context.SerializedContext}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SerializedContext readSerializedContext(String fileName) throws DeserializerException {
		return (SerializedContext) this.xmlToObj(fileName, SerializedContext.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.message.context.SerializedContext}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.message.context.SerializedContext}
	 * @return Object type {@link org.openspcoop2.message.context.SerializedContext}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SerializedContext readSerializedContext(File file) throws DeserializerException {
		return (SerializedContext) this.xmlToObj(file, SerializedContext.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.message.context.SerializedContext}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.message.context.SerializedContext}
	 * @return Object type {@link org.openspcoop2.message.context.SerializedContext}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SerializedContext readSerializedContext(InputStream in) throws DeserializerException {
		return (SerializedContext) this.xmlToObj(in, SerializedContext.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.message.context.SerializedContext}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.message.context.SerializedContext}
	 * @return Object type {@link org.openspcoop2.message.context.SerializedContext}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SerializedContext readSerializedContext(byte[] in) throws DeserializerException {
		return (SerializedContext) this.xmlToObj(in, SerializedContext.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.message.context.SerializedContext}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.message.context.SerializedContext}
	 * @return Object type {@link org.openspcoop2.message.context.SerializedContext}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SerializedContext readSerializedContextFromString(String in) throws DeserializerException {
		return (SerializedContext) this.xmlToObj(in.getBytes(), SerializedContext.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: forced-response
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.message.context.ForcedResponse}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.message.context.ForcedResponse}
	 * @return Object type {@link org.openspcoop2.message.context.ForcedResponse}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ForcedResponse readForcedResponse(String fileName) throws DeserializerException {
		return (ForcedResponse) this.xmlToObj(fileName, ForcedResponse.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.message.context.ForcedResponse}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.message.context.ForcedResponse}
	 * @return Object type {@link org.openspcoop2.message.context.ForcedResponse}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ForcedResponse readForcedResponse(File file) throws DeserializerException {
		return (ForcedResponse) this.xmlToObj(file, ForcedResponse.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.message.context.ForcedResponse}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.message.context.ForcedResponse}
	 * @return Object type {@link org.openspcoop2.message.context.ForcedResponse}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ForcedResponse readForcedResponse(InputStream in) throws DeserializerException {
		return (ForcedResponse) this.xmlToObj(in, ForcedResponse.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.message.context.ForcedResponse}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.message.context.ForcedResponse}
	 * @return Object type {@link org.openspcoop2.message.context.ForcedResponse}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ForcedResponse readForcedResponse(byte[] in) throws DeserializerException {
		return (ForcedResponse) this.xmlToObj(in, ForcedResponse.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.message.context.ForcedResponse}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.message.context.ForcedResponse}
	 * @return Object type {@link org.openspcoop2.message.context.ForcedResponse}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ForcedResponse readForcedResponseFromString(String in) throws DeserializerException {
		return (ForcedResponse) this.xmlToObj(in.getBytes(), ForcedResponse.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: forced-response-message
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.message.context.ForcedResponseMessage}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.message.context.ForcedResponseMessage}
	 * @return Object type {@link org.openspcoop2.message.context.ForcedResponseMessage}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ForcedResponseMessage readForcedResponseMessage(String fileName) throws DeserializerException {
		return (ForcedResponseMessage) this.xmlToObj(fileName, ForcedResponseMessage.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.message.context.ForcedResponseMessage}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.message.context.ForcedResponseMessage}
	 * @return Object type {@link org.openspcoop2.message.context.ForcedResponseMessage}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ForcedResponseMessage readForcedResponseMessage(File file) throws DeserializerException {
		return (ForcedResponseMessage) this.xmlToObj(file, ForcedResponseMessage.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.message.context.ForcedResponseMessage}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.message.context.ForcedResponseMessage}
	 * @return Object type {@link org.openspcoop2.message.context.ForcedResponseMessage}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ForcedResponseMessage readForcedResponseMessage(InputStream in) throws DeserializerException {
		return (ForcedResponseMessage) this.xmlToObj(in, ForcedResponseMessage.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.message.context.ForcedResponseMessage}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.message.context.ForcedResponseMessage}
	 * @return Object type {@link org.openspcoop2.message.context.ForcedResponseMessage}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ForcedResponseMessage readForcedResponseMessage(byte[] in) throws DeserializerException {
		return (ForcedResponseMessage) this.xmlToObj(in, ForcedResponseMessage.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.message.context.ForcedResponseMessage}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.message.context.ForcedResponseMessage}
	 * @return Object type {@link org.openspcoop2.message.context.ForcedResponseMessage}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ForcedResponseMessage readForcedResponseMessageFromString(String in) throws DeserializerException {
		return (ForcedResponseMessage) this.xmlToObj(in.getBytes(), ForcedResponseMessage.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: transport-response-context
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.message.context.TransportResponseContext}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.message.context.TransportResponseContext}
	 * @return Object type {@link org.openspcoop2.message.context.TransportResponseContext}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public TransportResponseContext readTransportResponseContext(String fileName) throws DeserializerException {
		return (TransportResponseContext) this.xmlToObj(fileName, TransportResponseContext.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.message.context.TransportResponseContext}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.message.context.TransportResponseContext}
	 * @return Object type {@link org.openspcoop2.message.context.TransportResponseContext}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public TransportResponseContext readTransportResponseContext(File file) throws DeserializerException {
		return (TransportResponseContext) this.xmlToObj(file, TransportResponseContext.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.message.context.TransportResponseContext}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.message.context.TransportResponseContext}
	 * @return Object type {@link org.openspcoop2.message.context.TransportResponseContext}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public TransportResponseContext readTransportResponseContext(InputStream in) throws DeserializerException {
		return (TransportResponseContext) this.xmlToObj(in, TransportResponseContext.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.message.context.TransportResponseContext}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.message.context.TransportResponseContext}
	 * @return Object type {@link org.openspcoop2.message.context.TransportResponseContext}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public TransportResponseContext readTransportResponseContext(byte[] in) throws DeserializerException {
		return (TransportResponseContext) this.xmlToObj(in, TransportResponseContext.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.message.context.TransportResponseContext}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.message.context.TransportResponseContext}
	 * @return Object type {@link org.openspcoop2.message.context.TransportResponseContext}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public TransportResponseContext readTransportResponseContextFromString(String in) throws DeserializerException {
		return (TransportResponseContext) this.xmlToObj(in.getBytes(), TransportResponseContext.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: soap
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.message.context.Soap}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.message.context.Soap}
	 * @return Object type {@link org.openspcoop2.message.context.Soap}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Soap readSoap(String fileName) throws DeserializerException {
		return (Soap) this.xmlToObj(fileName, Soap.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.message.context.Soap}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.message.context.Soap}
	 * @return Object type {@link org.openspcoop2.message.context.Soap}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Soap readSoap(File file) throws DeserializerException {
		return (Soap) this.xmlToObj(file, Soap.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.message.context.Soap}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.message.context.Soap}
	 * @return Object type {@link org.openspcoop2.message.context.Soap}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Soap readSoap(InputStream in) throws DeserializerException {
		return (Soap) this.xmlToObj(in, Soap.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.message.context.Soap}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.message.context.Soap}
	 * @return Object type {@link org.openspcoop2.message.context.Soap}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Soap readSoap(byte[] in) throws DeserializerException {
		return (Soap) this.xmlToObj(in, Soap.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.message.context.Soap}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.message.context.Soap}
	 * @return Object type {@link org.openspcoop2.message.context.Soap}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Soap readSoapFromString(String in) throws DeserializerException {
		return (Soap) this.xmlToObj(in.getBytes(), Soap.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: message-context
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.message.context.MessageContext}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.message.context.MessageContext}
	 * @return Object type {@link org.openspcoop2.message.context.MessageContext}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public MessageContext readMessageContext(String fileName) throws DeserializerException {
		return (MessageContext) this.xmlToObj(fileName, MessageContext.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.message.context.MessageContext}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.message.context.MessageContext}
	 * @return Object type {@link org.openspcoop2.message.context.MessageContext}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public MessageContext readMessageContext(File file) throws DeserializerException {
		return (MessageContext) this.xmlToObj(file, MessageContext.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.message.context.MessageContext}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.message.context.MessageContext}
	 * @return Object type {@link org.openspcoop2.message.context.MessageContext}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public MessageContext readMessageContext(InputStream in) throws DeserializerException {
		return (MessageContext) this.xmlToObj(in, MessageContext.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.message.context.MessageContext}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.message.context.MessageContext}
	 * @return Object type {@link org.openspcoop2.message.context.MessageContext}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public MessageContext readMessageContext(byte[] in) throws DeserializerException {
		return (MessageContext) this.xmlToObj(in, MessageContext.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.message.context.MessageContext}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.message.context.MessageContext}
	 * @return Object type {@link org.openspcoop2.message.context.MessageContext}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public MessageContext readMessageContextFromString(String in) throws DeserializerException {
		return (MessageContext) this.xmlToObj(in.getBytes(), MessageContext.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: content-type-parameters
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.message.context.ContentTypeParameters}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.message.context.ContentTypeParameters}
	 * @return Object type {@link org.openspcoop2.message.context.ContentTypeParameters}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ContentTypeParameters readContentTypeParameters(String fileName) throws DeserializerException {
		return (ContentTypeParameters) this.xmlToObj(fileName, ContentTypeParameters.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.message.context.ContentTypeParameters}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.message.context.ContentTypeParameters}
	 * @return Object type {@link org.openspcoop2.message.context.ContentTypeParameters}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ContentTypeParameters readContentTypeParameters(File file) throws DeserializerException {
		return (ContentTypeParameters) this.xmlToObj(file, ContentTypeParameters.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.message.context.ContentTypeParameters}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.message.context.ContentTypeParameters}
	 * @return Object type {@link org.openspcoop2.message.context.ContentTypeParameters}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ContentTypeParameters readContentTypeParameters(InputStream in) throws DeserializerException {
		return (ContentTypeParameters) this.xmlToObj(in, ContentTypeParameters.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.message.context.ContentTypeParameters}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.message.context.ContentTypeParameters}
	 * @return Object type {@link org.openspcoop2.message.context.ContentTypeParameters}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ContentTypeParameters readContentTypeParameters(byte[] in) throws DeserializerException {
		return (ContentTypeParameters) this.xmlToObj(in, ContentTypeParameters.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.message.context.ContentTypeParameters}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.message.context.ContentTypeParameters}
	 * @return Object type {@link org.openspcoop2.message.context.ContentTypeParameters}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ContentTypeParameters readContentTypeParametersFromString(String in) throws DeserializerException {
		return (ContentTypeParameters) this.xmlToObj(in.getBytes(), ContentTypeParameters.class);
	}	
	
	
	

}
