/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2017 Link.it srl (http://link.it).
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

import org.openspcoop2.generic_project.exception.SerializerException;
import org.openspcoop2.utils.beans.WriteToSerializerType;
import org.openspcoop2.utils.xml.JaxbUtils;

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
	 Object: string-parameter
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>stringParameter</var> of type {@link org.openspcoop2.message.context.StringParameter}
	 * 
	 * @param fileName Xml file to serialize the object <var>stringParameter</var>
	 * @param stringParameter Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,StringParameter stringParameter) throws SerializerException {
		this.objToXml(fileName, StringParameter.class, stringParameter, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>stringParameter</var> of type {@link org.openspcoop2.message.context.StringParameter}
	 * 
	 * @param fileName Xml file to serialize the object <var>stringParameter</var>
	 * @param stringParameter Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,StringParameter stringParameter,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, StringParameter.class, stringParameter, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>stringParameter</var> of type {@link org.openspcoop2.message.context.StringParameter}
	 * 
	 * @param file Xml file to serialize the object <var>stringParameter</var>
	 * @param stringParameter Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,StringParameter stringParameter) throws SerializerException {
		this.objToXml(file, StringParameter.class, stringParameter, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>stringParameter</var> of type {@link org.openspcoop2.message.context.StringParameter}
	 * 
	 * @param file Xml file to serialize the object <var>stringParameter</var>
	 * @param stringParameter Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,StringParameter stringParameter,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, StringParameter.class, stringParameter, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>stringParameter</var> of type {@link org.openspcoop2.message.context.StringParameter}
	 * 
	 * @param out OutputStream to serialize the object <var>stringParameter</var>
	 * @param stringParameter Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,StringParameter stringParameter) throws SerializerException {
		this.objToXml(out, StringParameter.class, stringParameter, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>stringParameter</var> of type {@link org.openspcoop2.message.context.StringParameter}
	 * 
	 * @param out OutputStream to serialize the object <var>stringParameter</var>
	 * @param stringParameter Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,StringParameter stringParameter,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, StringParameter.class, stringParameter, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>stringParameter</var> of type {@link org.openspcoop2.message.context.StringParameter}
	 * 
	 * @param stringParameter Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(StringParameter stringParameter) throws SerializerException {
		return this.objToXml(StringParameter.class, stringParameter, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>stringParameter</var> of type {@link org.openspcoop2.message.context.StringParameter}
	 * 
	 * @param stringParameter Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(StringParameter stringParameter,boolean prettyPrint) throws SerializerException {
		return this.objToXml(StringParameter.class, stringParameter, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>stringParameter</var> of type {@link org.openspcoop2.message.context.StringParameter}
	 * 
	 * @param stringParameter Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(StringParameter stringParameter) throws SerializerException {
		return this.objToXml(StringParameter.class, stringParameter, false).toString();
	}
	/**
	 * Serialize to String the object <var>stringParameter</var> of type {@link org.openspcoop2.message.context.StringParameter}
	 * 
	 * @param stringParameter Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(StringParameter stringParameter,boolean prettyPrint) throws SerializerException {
		return this.objToXml(StringParameter.class, stringParameter, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: url-parameters
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>urlParameters</var> of type {@link org.openspcoop2.message.context.UrlParameters}
	 * 
	 * @param fileName Xml file to serialize the object <var>urlParameters</var>
	 * @param urlParameters Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,UrlParameters urlParameters) throws SerializerException {
		this.objToXml(fileName, UrlParameters.class, urlParameters, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>urlParameters</var> of type {@link org.openspcoop2.message.context.UrlParameters}
	 * 
	 * @param fileName Xml file to serialize the object <var>urlParameters</var>
	 * @param urlParameters Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,UrlParameters urlParameters,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, UrlParameters.class, urlParameters, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>urlParameters</var> of type {@link org.openspcoop2.message.context.UrlParameters}
	 * 
	 * @param file Xml file to serialize the object <var>urlParameters</var>
	 * @param urlParameters Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,UrlParameters urlParameters) throws SerializerException {
		this.objToXml(file, UrlParameters.class, urlParameters, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>urlParameters</var> of type {@link org.openspcoop2.message.context.UrlParameters}
	 * 
	 * @param file Xml file to serialize the object <var>urlParameters</var>
	 * @param urlParameters Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,UrlParameters urlParameters,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, UrlParameters.class, urlParameters, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>urlParameters</var> of type {@link org.openspcoop2.message.context.UrlParameters}
	 * 
	 * @param out OutputStream to serialize the object <var>urlParameters</var>
	 * @param urlParameters Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,UrlParameters urlParameters) throws SerializerException {
		this.objToXml(out, UrlParameters.class, urlParameters, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>urlParameters</var> of type {@link org.openspcoop2.message.context.UrlParameters}
	 * 
	 * @param out OutputStream to serialize the object <var>urlParameters</var>
	 * @param urlParameters Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,UrlParameters urlParameters,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, UrlParameters.class, urlParameters, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>urlParameters</var> of type {@link org.openspcoop2.message.context.UrlParameters}
	 * 
	 * @param urlParameters Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(UrlParameters urlParameters) throws SerializerException {
		return this.objToXml(UrlParameters.class, urlParameters, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>urlParameters</var> of type {@link org.openspcoop2.message.context.UrlParameters}
	 * 
	 * @param urlParameters Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(UrlParameters urlParameters,boolean prettyPrint) throws SerializerException {
		return this.objToXml(UrlParameters.class, urlParameters, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>urlParameters</var> of type {@link org.openspcoop2.message.context.UrlParameters}
	 * 
	 * @param urlParameters Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(UrlParameters urlParameters) throws SerializerException {
		return this.objToXml(UrlParameters.class, urlParameters, false).toString();
	}
	/**
	 * Serialize to String the object <var>urlParameters</var> of type {@link org.openspcoop2.message.context.UrlParameters}
	 * 
	 * @param urlParameters Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(UrlParameters urlParameters,boolean prettyPrint) throws SerializerException {
		return this.objToXml(UrlParameters.class, urlParameters, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: content-length
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>contentLength</var> of type {@link org.openspcoop2.message.context.ContentLength}
	 * 
	 * @param fileName Xml file to serialize the object <var>contentLength</var>
	 * @param contentLength Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ContentLength contentLength) throws SerializerException {
		this.objToXml(fileName, ContentLength.class, contentLength, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>contentLength</var> of type {@link org.openspcoop2.message.context.ContentLength}
	 * 
	 * @param fileName Xml file to serialize the object <var>contentLength</var>
	 * @param contentLength Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ContentLength contentLength,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, ContentLength.class, contentLength, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>contentLength</var> of type {@link org.openspcoop2.message.context.ContentLength}
	 * 
	 * @param file Xml file to serialize the object <var>contentLength</var>
	 * @param contentLength Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ContentLength contentLength) throws SerializerException {
		this.objToXml(file, ContentLength.class, contentLength, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>contentLength</var> of type {@link org.openspcoop2.message.context.ContentLength}
	 * 
	 * @param file Xml file to serialize the object <var>contentLength</var>
	 * @param contentLength Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ContentLength contentLength,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, ContentLength.class, contentLength, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>contentLength</var> of type {@link org.openspcoop2.message.context.ContentLength}
	 * 
	 * @param out OutputStream to serialize the object <var>contentLength</var>
	 * @param contentLength Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ContentLength contentLength) throws SerializerException {
		this.objToXml(out, ContentLength.class, contentLength, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>contentLength</var> of type {@link org.openspcoop2.message.context.ContentLength}
	 * 
	 * @param out OutputStream to serialize the object <var>contentLength</var>
	 * @param contentLength Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ContentLength contentLength,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, ContentLength.class, contentLength, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>contentLength</var> of type {@link org.openspcoop2.message.context.ContentLength}
	 * 
	 * @param contentLength Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ContentLength contentLength) throws SerializerException {
		return this.objToXml(ContentLength.class, contentLength, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>contentLength</var> of type {@link org.openspcoop2.message.context.ContentLength}
	 * 
	 * @param contentLength Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ContentLength contentLength,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ContentLength.class, contentLength, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>contentLength</var> of type {@link org.openspcoop2.message.context.ContentLength}
	 * 
	 * @param contentLength Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ContentLength contentLength) throws SerializerException {
		return this.objToXml(ContentLength.class, contentLength, false).toString();
	}
	/**
	 * Serialize to String the object <var>contentLength</var> of type {@link org.openspcoop2.message.context.ContentLength}
	 * 
	 * @param contentLength Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ContentLength contentLength,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ContentLength.class, contentLength, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: transport-request-context
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>transportRequestContext</var> of type {@link org.openspcoop2.message.context.TransportRequestContext}
	 * 
	 * @param fileName Xml file to serialize the object <var>transportRequestContext</var>
	 * @param transportRequestContext Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,TransportRequestContext transportRequestContext) throws SerializerException {
		this.objToXml(fileName, TransportRequestContext.class, transportRequestContext, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>transportRequestContext</var> of type {@link org.openspcoop2.message.context.TransportRequestContext}
	 * 
	 * @param fileName Xml file to serialize the object <var>transportRequestContext</var>
	 * @param transportRequestContext Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,TransportRequestContext transportRequestContext,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, TransportRequestContext.class, transportRequestContext, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>transportRequestContext</var> of type {@link org.openspcoop2.message.context.TransportRequestContext}
	 * 
	 * @param file Xml file to serialize the object <var>transportRequestContext</var>
	 * @param transportRequestContext Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,TransportRequestContext transportRequestContext) throws SerializerException {
		this.objToXml(file, TransportRequestContext.class, transportRequestContext, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>transportRequestContext</var> of type {@link org.openspcoop2.message.context.TransportRequestContext}
	 * 
	 * @param file Xml file to serialize the object <var>transportRequestContext</var>
	 * @param transportRequestContext Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,TransportRequestContext transportRequestContext,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, TransportRequestContext.class, transportRequestContext, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>transportRequestContext</var> of type {@link org.openspcoop2.message.context.TransportRequestContext}
	 * 
	 * @param out OutputStream to serialize the object <var>transportRequestContext</var>
	 * @param transportRequestContext Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,TransportRequestContext transportRequestContext) throws SerializerException {
		this.objToXml(out, TransportRequestContext.class, transportRequestContext, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>transportRequestContext</var> of type {@link org.openspcoop2.message.context.TransportRequestContext}
	 * 
	 * @param out OutputStream to serialize the object <var>transportRequestContext</var>
	 * @param transportRequestContext Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,TransportRequestContext transportRequestContext,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, TransportRequestContext.class, transportRequestContext, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>transportRequestContext</var> of type {@link org.openspcoop2.message.context.TransportRequestContext}
	 * 
	 * @param transportRequestContext Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(TransportRequestContext transportRequestContext) throws SerializerException {
		return this.objToXml(TransportRequestContext.class, transportRequestContext, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>transportRequestContext</var> of type {@link org.openspcoop2.message.context.TransportRequestContext}
	 * 
	 * @param transportRequestContext Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(TransportRequestContext transportRequestContext,boolean prettyPrint) throws SerializerException {
		return this.objToXml(TransportRequestContext.class, transportRequestContext, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>transportRequestContext</var> of type {@link org.openspcoop2.message.context.TransportRequestContext}
	 * 
	 * @param transportRequestContext Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(TransportRequestContext transportRequestContext) throws SerializerException {
		return this.objToXml(TransportRequestContext.class, transportRequestContext, false).toString();
	}
	/**
	 * Serialize to String the object <var>transportRequestContext</var> of type {@link org.openspcoop2.message.context.TransportRequestContext}
	 * 
	 * @param transportRequestContext Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(TransportRequestContext transportRequestContext,boolean prettyPrint) throws SerializerException {
		return this.objToXml(TransportRequestContext.class, transportRequestContext, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: header-parameters
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>headerParameters</var> of type {@link org.openspcoop2.message.context.HeaderParameters}
	 * 
	 * @param fileName Xml file to serialize the object <var>headerParameters</var>
	 * @param headerParameters Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,HeaderParameters headerParameters) throws SerializerException {
		this.objToXml(fileName, HeaderParameters.class, headerParameters, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>headerParameters</var> of type {@link org.openspcoop2.message.context.HeaderParameters}
	 * 
	 * @param fileName Xml file to serialize the object <var>headerParameters</var>
	 * @param headerParameters Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,HeaderParameters headerParameters,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, HeaderParameters.class, headerParameters, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>headerParameters</var> of type {@link org.openspcoop2.message.context.HeaderParameters}
	 * 
	 * @param file Xml file to serialize the object <var>headerParameters</var>
	 * @param headerParameters Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,HeaderParameters headerParameters) throws SerializerException {
		this.objToXml(file, HeaderParameters.class, headerParameters, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>headerParameters</var> of type {@link org.openspcoop2.message.context.HeaderParameters}
	 * 
	 * @param file Xml file to serialize the object <var>headerParameters</var>
	 * @param headerParameters Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,HeaderParameters headerParameters,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, HeaderParameters.class, headerParameters, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>headerParameters</var> of type {@link org.openspcoop2.message.context.HeaderParameters}
	 * 
	 * @param out OutputStream to serialize the object <var>headerParameters</var>
	 * @param headerParameters Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,HeaderParameters headerParameters) throws SerializerException {
		this.objToXml(out, HeaderParameters.class, headerParameters, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>headerParameters</var> of type {@link org.openspcoop2.message.context.HeaderParameters}
	 * 
	 * @param out OutputStream to serialize the object <var>headerParameters</var>
	 * @param headerParameters Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,HeaderParameters headerParameters,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, HeaderParameters.class, headerParameters, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>headerParameters</var> of type {@link org.openspcoop2.message.context.HeaderParameters}
	 * 
	 * @param headerParameters Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(HeaderParameters headerParameters) throws SerializerException {
		return this.objToXml(HeaderParameters.class, headerParameters, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>headerParameters</var> of type {@link org.openspcoop2.message.context.HeaderParameters}
	 * 
	 * @param headerParameters Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(HeaderParameters headerParameters,boolean prettyPrint) throws SerializerException {
		return this.objToXml(HeaderParameters.class, headerParameters, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>headerParameters</var> of type {@link org.openspcoop2.message.context.HeaderParameters}
	 * 
	 * @param headerParameters Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(HeaderParameters headerParameters) throws SerializerException {
		return this.objToXml(HeaderParameters.class, headerParameters, false).toString();
	}
	/**
	 * Serialize to String the object <var>headerParameters</var> of type {@link org.openspcoop2.message.context.HeaderParameters}
	 * 
	 * @param headerParameters Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(HeaderParameters headerParameters,boolean prettyPrint) throws SerializerException {
		return this.objToXml(HeaderParameters.class, headerParameters, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: credentials
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>credentials</var> of type {@link org.openspcoop2.message.context.Credentials}
	 * 
	 * @param fileName Xml file to serialize the object <var>credentials</var>
	 * @param credentials Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Credentials credentials) throws SerializerException {
		this.objToXml(fileName, Credentials.class, credentials, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>credentials</var> of type {@link org.openspcoop2.message.context.Credentials}
	 * 
	 * @param fileName Xml file to serialize the object <var>credentials</var>
	 * @param credentials Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Credentials credentials,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Credentials.class, credentials, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>credentials</var> of type {@link org.openspcoop2.message.context.Credentials}
	 * 
	 * @param file Xml file to serialize the object <var>credentials</var>
	 * @param credentials Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Credentials credentials) throws SerializerException {
		this.objToXml(file, Credentials.class, credentials, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>credentials</var> of type {@link org.openspcoop2.message.context.Credentials}
	 * 
	 * @param file Xml file to serialize the object <var>credentials</var>
	 * @param credentials Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Credentials credentials,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Credentials.class, credentials, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>credentials</var> of type {@link org.openspcoop2.message.context.Credentials}
	 * 
	 * @param out OutputStream to serialize the object <var>credentials</var>
	 * @param credentials Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Credentials credentials) throws SerializerException {
		this.objToXml(out, Credentials.class, credentials, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>credentials</var> of type {@link org.openspcoop2.message.context.Credentials}
	 * 
	 * @param out OutputStream to serialize the object <var>credentials</var>
	 * @param credentials Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Credentials credentials,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Credentials.class, credentials, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>credentials</var> of type {@link org.openspcoop2.message.context.Credentials}
	 * 
	 * @param credentials Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Credentials credentials) throws SerializerException {
		return this.objToXml(Credentials.class, credentials, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>credentials</var> of type {@link org.openspcoop2.message.context.Credentials}
	 * 
	 * @param credentials Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Credentials credentials,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Credentials.class, credentials, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>credentials</var> of type {@link org.openspcoop2.message.context.Credentials}
	 * 
	 * @param credentials Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Credentials credentials) throws SerializerException {
		return this.objToXml(Credentials.class, credentials, false).toString();
	}
	/**
	 * Serialize to String the object <var>credentials</var> of type {@link org.openspcoop2.message.context.Credentials}
	 * 
	 * @param credentials Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Credentials credentials,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Credentials.class, credentials, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: serialized-parameter
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>serializedParameter</var> of type {@link org.openspcoop2.message.context.SerializedParameter}
	 * 
	 * @param fileName Xml file to serialize the object <var>serializedParameter</var>
	 * @param serializedParameter Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,SerializedParameter serializedParameter) throws SerializerException {
		this.objToXml(fileName, SerializedParameter.class, serializedParameter, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>serializedParameter</var> of type {@link org.openspcoop2.message.context.SerializedParameter}
	 * 
	 * @param fileName Xml file to serialize the object <var>serializedParameter</var>
	 * @param serializedParameter Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,SerializedParameter serializedParameter,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, SerializedParameter.class, serializedParameter, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>serializedParameter</var> of type {@link org.openspcoop2.message.context.SerializedParameter}
	 * 
	 * @param file Xml file to serialize the object <var>serializedParameter</var>
	 * @param serializedParameter Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,SerializedParameter serializedParameter) throws SerializerException {
		this.objToXml(file, SerializedParameter.class, serializedParameter, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>serializedParameter</var> of type {@link org.openspcoop2.message.context.SerializedParameter}
	 * 
	 * @param file Xml file to serialize the object <var>serializedParameter</var>
	 * @param serializedParameter Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,SerializedParameter serializedParameter,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, SerializedParameter.class, serializedParameter, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>serializedParameter</var> of type {@link org.openspcoop2.message.context.SerializedParameter}
	 * 
	 * @param out OutputStream to serialize the object <var>serializedParameter</var>
	 * @param serializedParameter Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,SerializedParameter serializedParameter) throws SerializerException {
		this.objToXml(out, SerializedParameter.class, serializedParameter, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>serializedParameter</var> of type {@link org.openspcoop2.message.context.SerializedParameter}
	 * 
	 * @param out OutputStream to serialize the object <var>serializedParameter</var>
	 * @param serializedParameter Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,SerializedParameter serializedParameter,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, SerializedParameter.class, serializedParameter, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>serializedParameter</var> of type {@link org.openspcoop2.message.context.SerializedParameter}
	 * 
	 * @param serializedParameter Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(SerializedParameter serializedParameter) throws SerializerException {
		return this.objToXml(SerializedParameter.class, serializedParameter, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>serializedParameter</var> of type {@link org.openspcoop2.message.context.SerializedParameter}
	 * 
	 * @param serializedParameter Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(SerializedParameter serializedParameter,boolean prettyPrint) throws SerializerException {
		return this.objToXml(SerializedParameter.class, serializedParameter, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>serializedParameter</var> of type {@link org.openspcoop2.message.context.SerializedParameter}
	 * 
	 * @param serializedParameter Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(SerializedParameter serializedParameter) throws SerializerException {
		return this.objToXml(SerializedParameter.class, serializedParameter, false).toString();
	}
	/**
	 * Serialize to String the object <var>serializedParameter</var> of type {@link org.openspcoop2.message.context.SerializedParameter}
	 * 
	 * @param serializedParameter Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(SerializedParameter serializedParameter,boolean prettyPrint) throws SerializerException {
		return this.objToXml(SerializedParameter.class, serializedParameter, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: serialized-context
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>serializedContext</var> of type {@link org.openspcoop2.message.context.SerializedContext}
	 * 
	 * @param fileName Xml file to serialize the object <var>serializedContext</var>
	 * @param serializedContext Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,SerializedContext serializedContext) throws SerializerException {
		this.objToXml(fileName, SerializedContext.class, serializedContext, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>serializedContext</var> of type {@link org.openspcoop2.message.context.SerializedContext}
	 * 
	 * @param fileName Xml file to serialize the object <var>serializedContext</var>
	 * @param serializedContext Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,SerializedContext serializedContext,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, SerializedContext.class, serializedContext, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>serializedContext</var> of type {@link org.openspcoop2.message.context.SerializedContext}
	 * 
	 * @param file Xml file to serialize the object <var>serializedContext</var>
	 * @param serializedContext Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,SerializedContext serializedContext) throws SerializerException {
		this.objToXml(file, SerializedContext.class, serializedContext, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>serializedContext</var> of type {@link org.openspcoop2.message.context.SerializedContext}
	 * 
	 * @param file Xml file to serialize the object <var>serializedContext</var>
	 * @param serializedContext Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,SerializedContext serializedContext,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, SerializedContext.class, serializedContext, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>serializedContext</var> of type {@link org.openspcoop2.message.context.SerializedContext}
	 * 
	 * @param out OutputStream to serialize the object <var>serializedContext</var>
	 * @param serializedContext Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,SerializedContext serializedContext) throws SerializerException {
		this.objToXml(out, SerializedContext.class, serializedContext, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>serializedContext</var> of type {@link org.openspcoop2.message.context.SerializedContext}
	 * 
	 * @param out OutputStream to serialize the object <var>serializedContext</var>
	 * @param serializedContext Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,SerializedContext serializedContext,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, SerializedContext.class, serializedContext, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>serializedContext</var> of type {@link org.openspcoop2.message.context.SerializedContext}
	 * 
	 * @param serializedContext Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(SerializedContext serializedContext) throws SerializerException {
		return this.objToXml(SerializedContext.class, serializedContext, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>serializedContext</var> of type {@link org.openspcoop2.message.context.SerializedContext}
	 * 
	 * @param serializedContext Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(SerializedContext serializedContext,boolean prettyPrint) throws SerializerException {
		return this.objToXml(SerializedContext.class, serializedContext, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>serializedContext</var> of type {@link org.openspcoop2.message.context.SerializedContext}
	 * 
	 * @param serializedContext Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(SerializedContext serializedContext) throws SerializerException {
		return this.objToXml(SerializedContext.class, serializedContext, false).toString();
	}
	/**
	 * Serialize to String the object <var>serializedContext</var> of type {@link org.openspcoop2.message.context.SerializedContext}
	 * 
	 * @param serializedContext Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(SerializedContext serializedContext,boolean prettyPrint) throws SerializerException {
		return this.objToXml(SerializedContext.class, serializedContext, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: forced-response
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>forcedResponse</var> of type {@link org.openspcoop2.message.context.ForcedResponse}
	 * 
	 * @param fileName Xml file to serialize the object <var>forcedResponse</var>
	 * @param forcedResponse Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ForcedResponse forcedResponse) throws SerializerException {
		this.objToXml(fileName, ForcedResponse.class, forcedResponse, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>forcedResponse</var> of type {@link org.openspcoop2.message.context.ForcedResponse}
	 * 
	 * @param fileName Xml file to serialize the object <var>forcedResponse</var>
	 * @param forcedResponse Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ForcedResponse forcedResponse,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, ForcedResponse.class, forcedResponse, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>forcedResponse</var> of type {@link org.openspcoop2.message.context.ForcedResponse}
	 * 
	 * @param file Xml file to serialize the object <var>forcedResponse</var>
	 * @param forcedResponse Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ForcedResponse forcedResponse) throws SerializerException {
		this.objToXml(file, ForcedResponse.class, forcedResponse, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>forcedResponse</var> of type {@link org.openspcoop2.message.context.ForcedResponse}
	 * 
	 * @param file Xml file to serialize the object <var>forcedResponse</var>
	 * @param forcedResponse Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ForcedResponse forcedResponse,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, ForcedResponse.class, forcedResponse, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>forcedResponse</var> of type {@link org.openspcoop2.message.context.ForcedResponse}
	 * 
	 * @param out OutputStream to serialize the object <var>forcedResponse</var>
	 * @param forcedResponse Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ForcedResponse forcedResponse) throws SerializerException {
		this.objToXml(out, ForcedResponse.class, forcedResponse, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>forcedResponse</var> of type {@link org.openspcoop2.message.context.ForcedResponse}
	 * 
	 * @param out OutputStream to serialize the object <var>forcedResponse</var>
	 * @param forcedResponse Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ForcedResponse forcedResponse,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, ForcedResponse.class, forcedResponse, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>forcedResponse</var> of type {@link org.openspcoop2.message.context.ForcedResponse}
	 * 
	 * @param forcedResponse Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ForcedResponse forcedResponse) throws SerializerException {
		return this.objToXml(ForcedResponse.class, forcedResponse, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>forcedResponse</var> of type {@link org.openspcoop2.message.context.ForcedResponse}
	 * 
	 * @param forcedResponse Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ForcedResponse forcedResponse,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ForcedResponse.class, forcedResponse, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>forcedResponse</var> of type {@link org.openspcoop2.message.context.ForcedResponse}
	 * 
	 * @param forcedResponse Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ForcedResponse forcedResponse) throws SerializerException {
		return this.objToXml(ForcedResponse.class, forcedResponse, false).toString();
	}
	/**
	 * Serialize to String the object <var>forcedResponse</var> of type {@link org.openspcoop2.message.context.ForcedResponse}
	 * 
	 * @param forcedResponse Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ForcedResponse forcedResponse,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ForcedResponse.class, forcedResponse, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: forced-response-message
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>forcedResponseMessage</var> of type {@link org.openspcoop2.message.context.ForcedResponseMessage}
	 * 
	 * @param fileName Xml file to serialize the object <var>forcedResponseMessage</var>
	 * @param forcedResponseMessage Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ForcedResponseMessage forcedResponseMessage) throws SerializerException {
		this.objToXml(fileName, ForcedResponseMessage.class, forcedResponseMessage, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>forcedResponseMessage</var> of type {@link org.openspcoop2.message.context.ForcedResponseMessage}
	 * 
	 * @param fileName Xml file to serialize the object <var>forcedResponseMessage</var>
	 * @param forcedResponseMessage Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ForcedResponseMessage forcedResponseMessage,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, ForcedResponseMessage.class, forcedResponseMessage, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>forcedResponseMessage</var> of type {@link org.openspcoop2.message.context.ForcedResponseMessage}
	 * 
	 * @param file Xml file to serialize the object <var>forcedResponseMessage</var>
	 * @param forcedResponseMessage Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ForcedResponseMessage forcedResponseMessage) throws SerializerException {
		this.objToXml(file, ForcedResponseMessage.class, forcedResponseMessage, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>forcedResponseMessage</var> of type {@link org.openspcoop2.message.context.ForcedResponseMessage}
	 * 
	 * @param file Xml file to serialize the object <var>forcedResponseMessage</var>
	 * @param forcedResponseMessage Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ForcedResponseMessage forcedResponseMessage,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, ForcedResponseMessage.class, forcedResponseMessage, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>forcedResponseMessage</var> of type {@link org.openspcoop2.message.context.ForcedResponseMessage}
	 * 
	 * @param out OutputStream to serialize the object <var>forcedResponseMessage</var>
	 * @param forcedResponseMessage Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ForcedResponseMessage forcedResponseMessage) throws SerializerException {
		this.objToXml(out, ForcedResponseMessage.class, forcedResponseMessage, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>forcedResponseMessage</var> of type {@link org.openspcoop2.message.context.ForcedResponseMessage}
	 * 
	 * @param out OutputStream to serialize the object <var>forcedResponseMessage</var>
	 * @param forcedResponseMessage Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ForcedResponseMessage forcedResponseMessage,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, ForcedResponseMessage.class, forcedResponseMessage, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>forcedResponseMessage</var> of type {@link org.openspcoop2.message.context.ForcedResponseMessage}
	 * 
	 * @param forcedResponseMessage Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ForcedResponseMessage forcedResponseMessage) throws SerializerException {
		return this.objToXml(ForcedResponseMessage.class, forcedResponseMessage, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>forcedResponseMessage</var> of type {@link org.openspcoop2.message.context.ForcedResponseMessage}
	 * 
	 * @param forcedResponseMessage Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ForcedResponseMessage forcedResponseMessage,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ForcedResponseMessage.class, forcedResponseMessage, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>forcedResponseMessage</var> of type {@link org.openspcoop2.message.context.ForcedResponseMessage}
	 * 
	 * @param forcedResponseMessage Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ForcedResponseMessage forcedResponseMessage) throws SerializerException {
		return this.objToXml(ForcedResponseMessage.class, forcedResponseMessage, false).toString();
	}
	/**
	 * Serialize to String the object <var>forcedResponseMessage</var> of type {@link org.openspcoop2.message.context.ForcedResponseMessage}
	 * 
	 * @param forcedResponseMessage Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ForcedResponseMessage forcedResponseMessage,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ForcedResponseMessage.class, forcedResponseMessage, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: transport-response-context
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>transportResponseContext</var> of type {@link org.openspcoop2.message.context.TransportResponseContext}
	 * 
	 * @param fileName Xml file to serialize the object <var>transportResponseContext</var>
	 * @param transportResponseContext Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,TransportResponseContext transportResponseContext) throws SerializerException {
		this.objToXml(fileName, TransportResponseContext.class, transportResponseContext, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>transportResponseContext</var> of type {@link org.openspcoop2.message.context.TransportResponseContext}
	 * 
	 * @param fileName Xml file to serialize the object <var>transportResponseContext</var>
	 * @param transportResponseContext Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,TransportResponseContext transportResponseContext,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, TransportResponseContext.class, transportResponseContext, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>transportResponseContext</var> of type {@link org.openspcoop2.message.context.TransportResponseContext}
	 * 
	 * @param file Xml file to serialize the object <var>transportResponseContext</var>
	 * @param transportResponseContext Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,TransportResponseContext transportResponseContext) throws SerializerException {
		this.objToXml(file, TransportResponseContext.class, transportResponseContext, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>transportResponseContext</var> of type {@link org.openspcoop2.message.context.TransportResponseContext}
	 * 
	 * @param file Xml file to serialize the object <var>transportResponseContext</var>
	 * @param transportResponseContext Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,TransportResponseContext transportResponseContext,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, TransportResponseContext.class, transportResponseContext, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>transportResponseContext</var> of type {@link org.openspcoop2.message.context.TransportResponseContext}
	 * 
	 * @param out OutputStream to serialize the object <var>transportResponseContext</var>
	 * @param transportResponseContext Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,TransportResponseContext transportResponseContext) throws SerializerException {
		this.objToXml(out, TransportResponseContext.class, transportResponseContext, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>transportResponseContext</var> of type {@link org.openspcoop2.message.context.TransportResponseContext}
	 * 
	 * @param out OutputStream to serialize the object <var>transportResponseContext</var>
	 * @param transportResponseContext Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,TransportResponseContext transportResponseContext,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, TransportResponseContext.class, transportResponseContext, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>transportResponseContext</var> of type {@link org.openspcoop2.message.context.TransportResponseContext}
	 * 
	 * @param transportResponseContext Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(TransportResponseContext transportResponseContext) throws SerializerException {
		return this.objToXml(TransportResponseContext.class, transportResponseContext, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>transportResponseContext</var> of type {@link org.openspcoop2.message.context.TransportResponseContext}
	 * 
	 * @param transportResponseContext Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(TransportResponseContext transportResponseContext,boolean prettyPrint) throws SerializerException {
		return this.objToXml(TransportResponseContext.class, transportResponseContext, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>transportResponseContext</var> of type {@link org.openspcoop2.message.context.TransportResponseContext}
	 * 
	 * @param transportResponseContext Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(TransportResponseContext transportResponseContext) throws SerializerException {
		return this.objToXml(TransportResponseContext.class, transportResponseContext, false).toString();
	}
	/**
	 * Serialize to String the object <var>transportResponseContext</var> of type {@link org.openspcoop2.message.context.TransportResponseContext}
	 * 
	 * @param transportResponseContext Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(TransportResponseContext transportResponseContext,boolean prettyPrint) throws SerializerException {
		return this.objToXml(TransportResponseContext.class, transportResponseContext, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: soap
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>soap</var> of type {@link org.openspcoop2.message.context.Soap}
	 * 
	 * @param fileName Xml file to serialize the object <var>soap</var>
	 * @param soap Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Soap soap) throws SerializerException {
		this.objToXml(fileName, Soap.class, soap, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>soap</var> of type {@link org.openspcoop2.message.context.Soap}
	 * 
	 * @param fileName Xml file to serialize the object <var>soap</var>
	 * @param soap Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Soap soap,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Soap.class, soap, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>soap</var> of type {@link org.openspcoop2.message.context.Soap}
	 * 
	 * @param file Xml file to serialize the object <var>soap</var>
	 * @param soap Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Soap soap) throws SerializerException {
		this.objToXml(file, Soap.class, soap, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>soap</var> of type {@link org.openspcoop2.message.context.Soap}
	 * 
	 * @param file Xml file to serialize the object <var>soap</var>
	 * @param soap Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Soap soap,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Soap.class, soap, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>soap</var> of type {@link org.openspcoop2.message.context.Soap}
	 * 
	 * @param out OutputStream to serialize the object <var>soap</var>
	 * @param soap Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Soap soap) throws SerializerException {
		this.objToXml(out, Soap.class, soap, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>soap</var> of type {@link org.openspcoop2.message.context.Soap}
	 * 
	 * @param out OutputStream to serialize the object <var>soap</var>
	 * @param soap Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Soap soap,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Soap.class, soap, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>soap</var> of type {@link org.openspcoop2.message.context.Soap}
	 * 
	 * @param soap Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Soap soap) throws SerializerException {
		return this.objToXml(Soap.class, soap, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>soap</var> of type {@link org.openspcoop2.message.context.Soap}
	 * 
	 * @param soap Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Soap soap,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Soap.class, soap, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>soap</var> of type {@link org.openspcoop2.message.context.Soap}
	 * 
	 * @param soap Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Soap soap) throws SerializerException {
		return this.objToXml(Soap.class, soap, false).toString();
	}
	/**
	 * Serialize to String the object <var>soap</var> of type {@link org.openspcoop2.message.context.Soap}
	 * 
	 * @param soap Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Soap soap,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Soap.class, soap, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: message-context
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>messageContext</var> of type {@link org.openspcoop2.message.context.MessageContext}
	 * 
	 * @param fileName Xml file to serialize the object <var>messageContext</var>
	 * @param messageContext Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,MessageContext messageContext) throws SerializerException {
		this.objToXml(fileName, MessageContext.class, messageContext, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>messageContext</var> of type {@link org.openspcoop2.message.context.MessageContext}
	 * 
	 * @param fileName Xml file to serialize the object <var>messageContext</var>
	 * @param messageContext Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,MessageContext messageContext,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, MessageContext.class, messageContext, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>messageContext</var> of type {@link org.openspcoop2.message.context.MessageContext}
	 * 
	 * @param file Xml file to serialize the object <var>messageContext</var>
	 * @param messageContext Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,MessageContext messageContext) throws SerializerException {
		this.objToXml(file, MessageContext.class, messageContext, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>messageContext</var> of type {@link org.openspcoop2.message.context.MessageContext}
	 * 
	 * @param file Xml file to serialize the object <var>messageContext</var>
	 * @param messageContext Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,MessageContext messageContext,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, MessageContext.class, messageContext, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>messageContext</var> of type {@link org.openspcoop2.message.context.MessageContext}
	 * 
	 * @param out OutputStream to serialize the object <var>messageContext</var>
	 * @param messageContext Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,MessageContext messageContext) throws SerializerException {
		this.objToXml(out, MessageContext.class, messageContext, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>messageContext</var> of type {@link org.openspcoop2.message.context.MessageContext}
	 * 
	 * @param out OutputStream to serialize the object <var>messageContext</var>
	 * @param messageContext Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,MessageContext messageContext,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, MessageContext.class, messageContext, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>messageContext</var> of type {@link org.openspcoop2.message.context.MessageContext}
	 * 
	 * @param messageContext Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(MessageContext messageContext) throws SerializerException {
		return this.objToXml(MessageContext.class, messageContext, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>messageContext</var> of type {@link org.openspcoop2.message.context.MessageContext}
	 * 
	 * @param messageContext Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(MessageContext messageContext,boolean prettyPrint) throws SerializerException {
		return this.objToXml(MessageContext.class, messageContext, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>messageContext</var> of type {@link org.openspcoop2.message.context.MessageContext}
	 * 
	 * @param messageContext Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(MessageContext messageContext) throws SerializerException {
		return this.objToXml(MessageContext.class, messageContext, false).toString();
	}
	/**
	 * Serialize to String the object <var>messageContext</var> of type {@link org.openspcoop2.message.context.MessageContext}
	 * 
	 * @param messageContext Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(MessageContext messageContext,boolean prettyPrint) throws SerializerException {
		return this.objToXml(MessageContext.class, messageContext, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: content-type-parameters
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>contentTypeParameters</var> of type {@link org.openspcoop2.message.context.ContentTypeParameters}
	 * 
	 * @param fileName Xml file to serialize the object <var>contentTypeParameters</var>
	 * @param contentTypeParameters Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ContentTypeParameters contentTypeParameters) throws SerializerException {
		this.objToXml(fileName, ContentTypeParameters.class, contentTypeParameters, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>contentTypeParameters</var> of type {@link org.openspcoop2.message.context.ContentTypeParameters}
	 * 
	 * @param fileName Xml file to serialize the object <var>contentTypeParameters</var>
	 * @param contentTypeParameters Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ContentTypeParameters contentTypeParameters,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, ContentTypeParameters.class, contentTypeParameters, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>contentTypeParameters</var> of type {@link org.openspcoop2.message.context.ContentTypeParameters}
	 * 
	 * @param file Xml file to serialize the object <var>contentTypeParameters</var>
	 * @param contentTypeParameters Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ContentTypeParameters contentTypeParameters) throws SerializerException {
		this.objToXml(file, ContentTypeParameters.class, contentTypeParameters, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>contentTypeParameters</var> of type {@link org.openspcoop2.message.context.ContentTypeParameters}
	 * 
	 * @param file Xml file to serialize the object <var>contentTypeParameters</var>
	 * @param contentTypeParameters Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ContentTypeParameters contentTypeParameters,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, ContentTypeParameters.class, contentTypeParameters, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>contentTypeParameters</var> of type {@link org.openspcoop2.message.context.ContentTypeParameters}
	 * 
	 * @param out OutputStream to serialize the object <var>contentTypeParameters</var>
	 * @param contentTypeParameters Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ContentTypeParameters contentTypeParameters) throws SerializerException {
		this.objToXml(out, ContentTypeParameters.class, contentTypeParameters, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>contentTypeParameters</var> of type {@link org.openspcoop2.message.context.ContentTypeParameters}
	 * 
	 * @param out OutputStream to serialize the object <var>contentTypeParameters</var>
	 * @param contentTypeParameters Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ContentTypeParameters contentTypeParameters,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, ContentTypeParameters.class, contentTypeParameters, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>contentTypeParameters</var> of type {@link org.openspcoop2.message.context.ContentTypeParameters}
	 * 
	 * @param contentTypeParameters Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ContentTypeParameters contentTypeParameters) throws SerializerException {
		return this.objToXml(ContentTypeParameters.class, contentTypeParameters, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>contentTypeParameters</var> of type {@link org.openspcoop2.message.context.ContentTypeParameters}
	 * 
	 * @param contentTypeParameters Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ContentTypeParameters contentTypeParameters,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ContentTypeParameters.class, contentTypeParameters, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>contentTypeParameters</var> of type {@link org.openspcoop2.message.context.ContentTypeParameters}
	 * 
	 * @param contentTypeParameters Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ContentTypeParameters contentTypeParameters) throws SerializerException {
		return this.objToXml(ContentTypeParameters.class, contentTypeParameters, false).toString();
	}
	/**
	 * Serialize to String the object <var>contentTypeParameters</var> of type {@link org.openspcoop2.message.context.ContentTypeParameters}
	 * 
	 * @param contentTypeParameters Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ContentTypeParameters contentTypeParameters,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ContentTypeParameters.class, contentTypeParameters, prettyPrint).toString();
	}
	
	
	

}
