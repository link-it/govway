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
package backend.ecodex.org._1_1.utils.serializer;

import org.openspcoop2.generic_project.exception.SerializerException;
import org.openspcoop2.utils.beans.WriteToSerializerType;
import org.openspcoop2.utils.xml.JaxbUtils;

import backend.ecodex.org._1_1.MessageErrorsRequest;
import backend.ecodex.org._1_1.GetStatusRequest;
import backend.ecodex.org._1_1.PayloadType;
import backend.ecodex.org._1_1.ErrorResultImpl;
import backend.ecodex.org._1_1.ErrorResultImplArray;
import backend.ecodex.org._1_1.FaultDetail;
import backend.ecodex.org._1_1.DownloadMessageResponse;
import backend.ecodex.org._1_1.Collection;
import backend.ecodex.org._1_1.DownloadMessageRequest;
import backend.ecodex.org._1_1.ListPendingMessagesResponse;
import backend.ecodex.org._1_1.MessageStatusRequest;
import backend.ecodex.org._1_1.SendRequest;
import backend.ecodex.org._1_1.SendRequestURL;
import backend.ecodex.org._1_1.SendResponse;
import backend.ecodex.org._1_1.GetErrorsRequest;
import backend.ecodex.org._1_1.PayloadURLType;

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
	 Object: messageErrorsRequest
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>messageErrorsRequest</var> of type {@link backend.ecodex.org._1_1.MessageErrorsRequest}
	 * 
	 * @param fileName Xml file to serialize the object <var>messageErrorsRequest</var>
	 * @param messageErrorsRequest Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,MessageErrorsRequest messageErrorsRequest) throws SerializerException {
		this.objToXml(fileName, MessageErrorsRequest.class, messageErrorsRequest, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>messageErrorsRequest</var> of type {@link backend.ecodex.org._1_1.MessageErrorsRequest}
	 * 
	 * @param fileName Xml file to serialize the object <var>messageErrorsRequest</var>
	 * @param messageErrorsRequest Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,MessageErrorsRequest messageErrorsRequest,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, MessageErrorsRequest.class, messageErrorsRequest, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>messageErrorsRequest</var> of type {@link backend.ecodex.org._1_1.MessageErrorsRequest}
	 * 
	 * @param file Xml file to serialize the object <var>messageErrorsRequest</var>
	 * @param messageErrorsRequest Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,MessageErrorsRequest messageErrorsRequest) throws SerializerException {
		this.objToXml(file, MessageErrorsRequest.class, messageErrorsRequest, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>messageErrorsRequest</var> of type {@link backend.ecodex.org._1_1.MessageErrorsRequest}
	 * 
	 * @param file Xml file to serialize the object <var>messageErrorsRequest</var>
	 * @param messageErrorsRequest Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,MessageErrorsRequest messageErrorsRequest,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, MessageErrorsRequest.class, messageErrorsRequest, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>messageErrorsRequest</var> of type {@link backend.ecodex.org._1_1.MessageErrorsRequest}
	 * 
	 * @param out OutputStream to serialize the object <var>messageErrorsRequest</var>
	 * @param messageErrorsRequest Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,MessageErrorsRequest messageErrorsRequest) throws SerializerException {
		this.objToXml(out, MessageErrorsRequest.class, messageErrorsRequest, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>messageErrorsRequest</var> of type {@link backend.ecodex.org._1_1.MessageErrorsRequest}
	 * 
	 * @param out OutputStream to serialize the object <var>messageErrorsRequest</var>
	 * @param messageErrorsRequest Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,MessageErrorsRequest messageErrorsRequest,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, MessageErrorsRequest.class, messageErrorsRequest, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>messageErrorsRequest</var> of type {@link backend.ecodex.org._1_1.MessageErrorsRequest}
	 * 
	 * @param messageErrorsRequest Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(MessageErrorsRequest messageErrorsRequest) throws SerializerException {
		return this.objToXml(MessageErrorsRequest.class, messageErrorsRequest, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>messageErrorsRequest</var> of type {@link backend.ecodex.org._1_1.MessageErrorsRequest}
	 * 
	 * @param messageErrorsRequest Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(MessageErrorsRequest messageErrorsRequest,boolean prettyPrint) throws SerializerException {
		return this.objToXml(MessageErrorsRequest.class, messageErrorsRequest, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>messageErrorsRequest</var> of type {@link backend.ecodex.org._1_1.MessageErrorsRequest}
	 * 
	 * @param messageErrorsRequest Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(MessageErrorsRequest messageErrorsRequest) throws SerializerException {
		return this.objToXml(MessageErrorsRequest.class, messageErrorsRequest, false).toString();
	}
	/**
	 * Serialize to String the object <var>messageErrorsRequest</var> of type {@link backend.ecodex.org._1_1.MessageErrorsRequest}
	 * 
	 * @param messageErrorsRequest Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(MessageErrorsRequest messageErrorsRequest,boolean prettyPrint) throws SerializerException {
		return this.objToXml(MessageErrorsRequest.class, messageErrorsRequest, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: getStatusRequest
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>getStatusRequest</var> of type {@link backend.ecodex.org._1_1.GetStatusRequest}
	 * 
	 * @param fileName Xml file to serialize the object <var>getStatusRequest</var>
	 * @param getStatusRequest Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,GetStatusRequest getStatusRequest) throws SerializerException {
		this.objToXml(fileName, GetStatusRequest.class, getStatusRequest, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>getStatusRequest</var> of type {@link backend.ecodex.org._1_1.GetStatusRequest}
	 * 
	 * @param fileName Xml file to serialize the object <var>getStatusRequest</var>
	 * @param getStatusRequest Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,GetStatusRequest getStatusRequest,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, GetStatusRequest.class, getStatusRequest, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>getStatusRequest</var> of type {@link backend.ecodex.org._1_1.GetStatusRequest}
	 * 
	 * @param file Xml file to serialize the object <var>getStatusRequest</var>
	 * @param getStatusRequest Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,GetStatusRequest getStatusRequest) throws SerializerException {
		this.objToXml(file, GetStatusRequest.class, getStatusRequest, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>getStatusRequest</var> of type {@link backend.ecodex.org._1_1.GetStatusRequest}
	 * 
	 * @param file Xml file to serialize the object <var>getStatusRequest</var>
	 * @param getStatusRequest Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,GetStatusRequest getStatusRequest,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, GetStatusRequest.class, getStatusRequest, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>getStatusRequest</var> of type {@link backend.ecodex.org._1_1.GetStatusRequest}
	 * 
	 * @param out OutputStream to serialize the object <var>getStatusRequest</var>
	 * @param getStatusRequest Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,GetStatusRequest getStatusRequest) throws SerializerException {
		this.objToXml(out, GetStatusRequest.class, getStatusRequest, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>getStatusRequest</var> of type {@link backend.ecodex.org._1_1.GetStatusRequest}
	 * 
	 * @param out OutputStream to serialize the object <var>getStatusRequest</var>
	 * @param getStatusRequest Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,GetStatusRequest getStatusRequest,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, GetStatusRequest.class, getStatusRequest, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>getStatusRequest</var> of type {@link backend.ecodex.org._1_1.GetStatusRequest}
	 * 
	 * @param getStatusRequest Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(GetStatusRequest getStatusRequest) throws SerializerException {
		return this.objToXml(GetStatusRequest.class, getStatusRequest, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>getStatusRequest</var> of type {@link backend.ecodex.org._1_1.GetStatusRequest}
	 * 
	 * @param getStatusRequest Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(GetStatusRequest getStatusRequest,boolean prettyPrint) throws SerializerException {
		return this.objToXml(GetStatusRequest.class, getStatusRequest, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>getStatusRequest</var> of type {@link backend.ecodex.org._1_1.GetStatusRequest}
	 * 
	 * @param getStatusRequest Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(GetStatusRequest getStatusRequest) throws SerializerException {
		return this.objToXml(GetStatusRequest.class, getStatusRequest, false).toString();
	}
	/**
	 * Serialize to String the object <var>getStatusRequest</var> of type {@link backend.ecodex.org._1_1.GetStatusRequest}
	 * 
	 * @param getStatusRequest Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(GetStatusRequest getStatusRequest,boolean prettyPrint) throws SerializerException {
		return this.objToXml(GetStatusRequest.class, getStatusRequest, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: PayloadType
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>payloadType</var> of type {@link backend.ecodex.org._1_1.PayloadType}
	 * 
	 * @param fileName Xml file to serialize the object <var>payloadType</var>
	 * @param payloadType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,PayloadType payloadType) throws SerializerException {
		this.objToXml(fileName, PayloadType.class, payloadType, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>payloadType</var> of type {@link backend.ecodex.org._1_1.PayloadType}
	 * 
	 * @param fileName Xml file to serialize the object <var>payloadType</var>
	 * @param payloadType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,PayloadType payloadType,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, PayloadType.class, payloadType, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>payloadType</var> of type {@link backend.ecodex.org._1_1.PayloadType}
	 * 
	 * @param file Xml file to serialize the object <var>payloadType</var>
	 * @param payloadType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,PayloadType payloadType) throws SerializerException {
		this.objToXml(file, PayloadType.class, payloadType, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>payloadType</var> of type {@link backend.ecodex.org._1_1.PayloadType}
	 * 
	 * @param file Xml file to serialize the object <var>payloadType</var>
	 * @param payloadType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,PayloadType payloadType,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, PayloadType.class, payloadType, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>payloadType</var> of type {@link backend.ecodex.org._1_1.PayloadType}
	 * 
	 * @param out OutputStream to serialize the object <var>payloadType</var>
	 * @param payloadType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,PayloadType payloadType) throws SerializerException {
		this.objToXml(out, PayloadType.class, payloadType, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>payloadType</var> of type {@link backend.ecodex.org._1_1.PayloadType}
	 * 
	 * @param out OutputStream to serialize the object <var>payloadType</var>
	 * @param payloadType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,PayloadType payloadType,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, PayloadType.class, payloadType, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>payloadType</var> of type {@link backend.ecodex.org._1_1.PayloadType}
	 * 
	 * @param payloadType Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(PayloadType payloadType) throws SerializerException {
		return this.objToXml(PayloadType.class, payloadType, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>payloadType</var> of type {@link backend.ecodex.org._1_1.PayloadType}
	 * 
	 * @param payloadType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(PayloadType payloadType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(PayloadType.class, payloadType, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>payloadType</var> of type {@link backend.ecodex.org._1_1.PayloadType}
	 * 
	 * @param payloadType Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(PayloadType payloadType) throws SerializerException {
		return this.objToXml(PayloadType.class, payloadType, false).toString();
	}
	/**
	 * Serialize to String the object <var>payloadType</var> of type {@link backend.ecodex.org._1_1.PayloadType}
	 * 
	 * @param payloadType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(PayloadType payloadType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(PayloadType.class, payloadType, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: errorResultImpl
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>errorResultImpl</var> of type {@link backend.ecodex.org._1_1.ErrorResultImpl}
	 * 
	 * @param fileName Xml file to serialize the object <var>errorResultImpl</var>
	 * @param errorResultImpl Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ErrorResultImpl errorResultImpl) throws SerializerException {
		this.objToXml(fileName, ErrorResultImpl.class, errorResultImpl, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>errorResultImpl</var> of type {@link backend.ecodex.org._1_1.ErrorResultImpl}
	 * 
	 * @param fileName Xml file to serialize the object <var>errorResultImpl</var>
	 * @param errorResultImpl Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ErrorResultImpl errorResultImpl,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, ErrorResultImpl.class, errorResultImpl, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>errorResultImpl</var> of type {@link backend.ecodex.org._1_1.ErrorResultImpl}
	 * 
	 * @param file Xml file to serialize the object <var>errorResultImpl</var>
	 * @param errorResultImpl Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ErrorResultImpl errorResultImpl) throws SerializerException {
		this.objToXml(file, ErrorResultImpl.class, errorResultImpl, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>errorResultImpl</var> of type {@link backend.ecodex.org._1_1.ErrorResultImpl}
	 * 
	 * @param file Xml file to serialize the object <var>errorResultImpl</var>
	 * @param errorResultImpl Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ErrorResultImpl errorResultImpl,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, ErrorResultImpl.class, errorResultImpl, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>errorResultImpl</var> of type {@link backend.ecodex.org._1_1.ErrorResultImpl}
	 * 
	 * @param out OutputStream to serialize the object <var>errorResultImpl</var>
	 * @param errorResultImpl Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ErrorResultImpl errorResultImpl) throws SerializerException {
		this.objToXml(out, ErrorResultImpl.class, errorResultImpl, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>errorResultImpl</var> of type {@link backend.ecodex.org._1_1.ErrorResultImpl}
	 * 
	 * @param out OutputStream to serialize the object <var>errorResultImpl</var>
	 * @param errorResultImpl Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ErrorResultImpl errorResultImpl,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, ErrorResultImpl.class, errorResultImpl, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>errorResultImpl</var> of type {@link backend.ecodex.org._1_1.ErrorResultImpl}
	 * 
	 * @param errorResultImpl Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ErrorResultImpl errorResultImpl) throws SerializerException {
		return this.objToXml(ErrorResultImpl.class, errorResultImpl, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>errorResultImpl</var> of type {@link backend.ecodex.org._1_1.ErrorResultImpl}
	 * 
	 * @param errorResultImpl Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ErrorResultImpl errorResultImpl,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ErrorResultImpl.class, errorResultImpl, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>errorResultImpl</var> of type {@link backend.ecodex.org._1_1.ErrorResultImpl}
	 * 
	 * @param errorResultImpl Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ErrorResultImpl errorResultImpl) throws SerializerException {
		return this.objToXml(ErrorResultImpl.class, errorResultImpl, false).toString();
	}
	/**
	 * Serialize to String the object <var>errorResultImpl</var> of type {@link backend.ecodex.org._1_1.ErrorResultImpl}
	 * 
	 * @param errorResultImpl Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ErrorResultImpl errorResultImpl,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ErrorResultImpl.class, errorResultImpl, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: errorResultImplArray
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>errorResultImplArray</var> of type {@link backend.ecodex.org._1_1.ErrorResultImplArray}
	 * 
	 * @param fileName Xml file to serialize the object <var>errorResultImplArray</var>
	 * @param errorResultImplArray Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ErrorResultImplArray errorResultImplArray) throws SerializerException {
		this.objToXml(fileName, ErrorResultImplArray.class, errorResultImplArray, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>errorResultImplArray</var> of type {@link backend.ecodex.org._1_1.ErrorResultImplArray}
	 * 
	 * @param fileName Xml file to serialize the object <var>errorResultImplArray</var>
	 * @param errorResultImplArray Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ErrorResultImplArray errorResultImplArray,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, ErrorResultImplArray.class, errorResultImplArray, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>errorResultImplArray</var> of type {@link backend.ecodex.org._1_1.ErrorResultImplArray}
	 * 
	 * @param file Xml file to serialize the object <var>errorResultImplArray</var>
	 * @param errorResultImplArray Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ErrorResultImplArray errorResultImplArray) throws SerializerException {
		this.objToXml(file, ErrorResultImplArray.class, errorResultImplArray, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>errorResultImplArray</var> of type {@link backend.ecodex.org._1_1.ErrorResultImplArray}
	 * 
	 * @param file Xml file to serialize the object <var>errorResultImplArray</var>
	 * @param errorResultImplArray Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ErrorResultImplArray errorResultImplArray,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, ErrorResultImplArray.class, errorResultImplArray, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>errorResultImplArray</var> of type {@link backend.ecodex.org._1_1.ErrorResultImplArray}
	 * 
	 * @param out OutputStream to serialize the object <var>errorResultImplArray</var>
	 * @param errorResultImplArray Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ErrorResultImplArray errorResultImplArray) throws SerializerException {
		this.objToXml(out, ErrorResultImplArray.class, errorResultImplArray, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>errorResultImplArray</var> of type {@link backend.ecodex.org._1_1.ErrorResultImplArray}
	 * 
	 * @param out OutputStream to serialize the object <var>errorResultImplArray</var>
	 * @param errorResultImplArray Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ErrorResultImplArray errorResultImplArray,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, ErrorResultImplArray.class, errorResultImplArray, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>errorResultImplArray</var> of type {@link backend.ecodex.org._1_1.ErrorResultImplArray}
	 * 
	 * @param errorResultImplArray Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ErrorResultImplArray errorResultImplArray) throws SerializerException {
		return this.objToXml(ErrorResultImplArray.class, errorResultImplArray, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>errorResultImplArray</var> of type {@link backend.ecodex.org._1_1.ErrorResultImplArray}
	 * 
	 * @param errorResultImplArray Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ErrorResultImplArray errorResultImplArray,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ErrorResultImplArray.class, errorResultImplArray, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>errorResultImplArray</var> of type {@link backend.ecodex.org._1_1.ErrorResultImplArray}
	 * 
	 * @param errorResultImplArray Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ErrorResultImplArray errorResultImplArray) throws SerializerException {
		return this.objToXml(ErrorResultImplArray.class, errorResultImplArray, false).toString();
	}
	/**
	 * Serialize to String the object <var>errorResultImplArray</var> of type {@link backend.ecodex.org._1_1.ErrorResultImplArray}
	 * 
	 * @param errorResultImplArray Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ErrorResultImplArray errorResultImplArray,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ErrorResultImplArray.class, errorResultImplArray, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: FaultDetail
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>faultDetail</var> of type {@link backend.ecodex.org._1_1.FaultDetail}
	 * 
	 * @param fileName Xml file to serialize the object <var>faultDetail</var>
	 * @param faultDetail Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,FaultDetail faultDetail) throws SerializerException {
		this.objToXml(fileName, FaultDetail.class, faultDetail, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>faultDetail</var> of type {@link backend.ecodex.org._1_1.FaultDetail}
	 * 
	 * @param fileName Xml file to serialize the object <var>faultDetail</var>
	 * @param faultDetail Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,FaultDetail faultDetail,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, FaultDetail.class, faultDetail, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>faultDetail</var> of type {@link backend.ecodex.org._1_1.FaultDetail}
	 * 
	 * @param file Xml file to serialize the object <var>faultDetail</var>
	 * @param faultDetail Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,FaultDetail faultDetail) throws SerializerException {
		this.objToXml(file, FaultDetail.class, faultDetail, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>faultDetail</var> of type {@link backend.ecodex.org._1_1.FaultDetail}
	 * 
	 * @param file Xml file to serialize the object <var>faultDetail</var>
	 * @param faultDetail Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,FaultDetail faultDetail,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, FaultDetail.class, faultDetail, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>faultDetail</var> of type {@link backend.ecodex.org._1_1.FaultDetail}
	 * 
	 * @param out OutputStream to serialize the object <var>faultDetail</var>
	 * @param faultDetail Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,FaultDetail faultDetail) throws SerializerException {
		this.objToXml(out, FaultDetail.class, faultDetail, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>faultDetail</var> of type {@link backend.ecodex.org._1_1.FaultDetail}
	 * 
	 * @param out OutputStream to serialize the object <var>faultDetail</var>
	 * @param faultDetail Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,FaultDetail faultDetail,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, FaultDetail.class, faultDetail, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>faultDetail</var> of type {@link backend.ecodex.org._1_1.FaultDetail}
	 * 
	 * @param faultDetail Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(FaultDetail faultDetail) throws SerializerException {
		return this.objToXml(FaultDetail.class, faultDetail, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>faultDetail</var> of type {@link backend.ecodex.org._1_1.FaultDetail}
	 * 
	 * @param faultDetail Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(FaultDetail faultDetail,boolean prettyPrint) throws SerializerException {
		return this.objToXml(FaultDetail.class, faultDetail, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>faultDetail</var> of type {@link backend.ecodex.org._1_1.FaultDetail}
	 * 
	 * @param faultDetail Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(FaultDetail faultDetail) throws SerializerException {
		return this.objToXml(FaultDetail.class, faultDetail, false).toString();
	}
	/**
	 * Serialize to String the object <var>faultDetail</var> of type {@link backend.ecodex.org._1_1.FaultDetail}
	 * 
	 * @param faultDetail Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(FaultDetail faultDetail,boolean prettyPrint) throws SerializerException {
		return this.objToXml(FaultDetail.class, faultDetail, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: downloadMessageResponse
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>downloadMessageResponse</var> of type {@link backend.ecodex.org._1_1.DownloadMessageResponse}
	 * 
	 * @param fileName Xml file to serialize the object <var>downloadMessageResponse</var>
	 * @param downloadMessageResponse Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,DownloadMessageResponse downloadMessageResponse) throws SerializerException {
		this.objToXml(fileName, DownloadMessageResponse.class, downloadMessageResponse, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>downloadMessageResponse</var> of type {@link backend.ecodex.org._1_1.DownloadMessageResponse}
	 * 
	 * @param fileName Xml file to serialize the object <var>downloadMessageResponse</var>
	 * @param downloadMessageResponse Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,DownloadMessageResponse downloadMessageResponse,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, DownloadMessageResponse.class, downloadMessageResponse, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>downloadMessageResponse</var> of type {@link backend.ecodex.org._1_1.DownloadMessageResponse}
	 * 
	 * @param file Xml file to serialize the object <var>downloadMessageResponse</var>
	 * @param downloadMessageResponse Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,DownloadMessageResponse downloadMessageResponse) throws SerializerException {
		this.objToXml(file, DownloadMessageResponse.class, downloadMessageResponse, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>downloadMessageResponse</var> of type {@link backend.ecodex.org._1_1.DownloadMessageResponse}
	 * 
	 * @param file Xml file to serialize the object <var>downloadMessageResponse</var>
	 * @param downloadMessageResponse Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,DownloadMessageResponse downloadMessageResponse,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, DownloadMessageResponse.class, downloadMessageResponse, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>downloadMessageResponse</var> of type {@link backend.ecodex.org._1_1.DownloadMessageResponse}
	 * 
	 * @param out OutputStream to serialize the object <var>downloadMessageResponse</var>
	 * @param downloadMessageResponse Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,DownloadMessageResponse downloadMessageResponse) throws SerializerException {
		this.objToXml(out, DownloadMessageResponse.class, downloadMessageResponse, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>downloadMessageResponse</var> of type {@link backend.ecodex.org._1_1.DownloadMessageResponse}
	 * 
	 * @param out OutputStream to serialize the object <var>downloadMessageResponse</var>
	 * @param downloadMessageResponse Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,DownloadMessageResponse downloadMessageResponse,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, DownloadMessageResponse.class, downloadMessageResponse, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>downloadMessageResponse</var> of type {@link backend.ecodex.org._1_1.DownloadMessageResponse}
	 * 
	 * @param downloadMessageResponse Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(DownloadMessageResponse downloadMessageResponse) throws SerializerException {
		return this.objToXml(DownloadMessageResponse.class, downloadMessageResponse, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>downloadMessageResponse</var> of type {@link backend.ecodex.org._1_1.DownloadMessageResponse}
	 * 
	 * @param downloadMessageResponse Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(DownloadMessageResponse downloadMessageResponse,boolean prettyPrint) throws SerializerException {
		return this.objToXml(DownloadMessageResponse.class, downloadMessageResponse, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>downloadMessageResponse</var> of type {@link backend.ecodex.org._1_1.DownloadMessageResponse}
	 * 
	 * @param downloadMessageResponse Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(DownloadMessageResponse downloadMessageResponse) throws SerializerException {
		return this.objToXml(DownloadMessageResponse.class, downloadMessageResponse, false).toString();
	}
	/**
	 * Serialize to String the object <var>downloadMessageResponse</var> of type {@link backend.ecodex.org._1_1.DownloadMessageResponse}
	 * 
	 * @param downloadMessageResponse Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(DownloadMessageResponse downloadMessageResponse,boolean prettyPrint) throws SerializerException {
		return this.objToXml(DownloadMessageResponse.class, downloadMessageResponse, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: collection
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>collection</var> of type {@link backend.ecodex.org._1_1.Collection}
	 * 
	 * @param fileName Xml file to serialize the object <var>collection</var>
	 * @param collection Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Collection collection) throws SerializerException {
		this.objToXml(fileName, Collection.class, collection, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>collection</var> of type {@link backend.ecodex.org._1_1.Collection}
	 * 
	 * @param fileName Xml file to serialize the object <var>collection</var>
	 * @param collection Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Collection collection,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Collection.class, collection, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>collection</var> of type {@link backend.ecodex.org._1_1.Collection}
	 * 
	 * @param file Xml file to serialize the object <var>collection</var>
	 * @param collection Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Collection collection) throws SerializerException {
		this.objToXml(file, Collection.class, collection, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>collection</var> of type {@link backend.ecodex.org._1_1.Collection}
	 * 
	 * @param file Xml file to serialize the object <var>collection</var>
	 * @param collection Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Collection collection,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Collection.class, collection, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>collection</var> of type {@link backend.ecodex.org._1_1.Collection}
	 * 
	 * @param out OutputStream to serialize the object <var>collection</var>
	 * @param collection Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Collection collection) throws SerializerException {
		this.objToXml(out, Collection.class, collection, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>collection</var> of type {@link backend.ecodex.org._1_1.Collection}
	 * 
	 * @param out OutputStream to serialize the object <var>collection</var>
	 * @param collection Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Collection collection,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Collection.class, collection, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>collection</var> of type {@link backend.ecodex.org._1_1.Collection}
	 * 
	 * @param collection Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Collection collection) throws SerializerException {
		return this.objToXml(Collection.class, collection, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>collection</var> of type {@link backend.ecodex.org._1_1.Collection}
	 * 
	 * @param collection Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Collection collection,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Collection.class, collection, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>collection</var> of type {@link backend.ecodex.org._1_1.Collection}
	 * 
	 * @param collection Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Collection collection) throws SerializerException {
		return this.objToXml(Collection.class, collection, false).toString();
	}
	/**
	 * Serialize to String the object <var>collection</var> of type {@link backend.ecodex.org._1_1.Collection}
	 * 
	 * @param collection Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Collection collection,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Collection.class, collection, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: downloadMessageRequest
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>downloadMessageRequest</var> of type {@link backend.ecodex.org._1_1.DownloadMessageRequest}
	 * 
	 * @param fileName Xml file to serialize the object <var>downloadMessageRequest</var>
	 * @param downloadMessageRequest Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,DownloadMessageRequest downloadMessageRequest) throws SerializerException {
		this.objToXml(fileName, DownloadMessageRequest.class, downloadMessageRequest, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>downloadMessageRequest</var> of type {@link backend.ecodex.org._1_1.DownloadMessageRequest}
	 * 
	 * @param fileName Xml file to serialize the object <var>downloadMessageRequest</var>
	 * @param downloadMessageRequest Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,DownloadMessageRequest downloadMessageRequest,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, DownloadMessageRequest.class, downloadMessageRequest, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>downloadMessageRequest</var> of type {@link backend.ecodex.org._1_1.DownloadMessageRequest}
	 * 
	 * @param file Xml file to serialize the object <var>downloadMessageRequest</var>
	 * @param downloadMessageRequest Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,DownloadMessageRequest downloadMessageRequest) throws SerializerException {
		this.objToXml(file, DownloadMessageRequest.class, downloadMessageRequest, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>downloadMessageRequest</var> of type {@link backend.ecodex.org._1_1.DownloadMessageRequest}
	 * 
	 * @param file Xml file to serialize the object <var>downloadMessageRequest</var>
	 * @param downloadMessageRequest Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,DownloadMessageRequest downloadMessageRequest,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, DownloadMessageRequest.class, downloadMessageRequest, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>downloadMessageRequest</var> of type {@link backend.ecodex.org._1_1.DownloadMessageRequest}
	 * 
	 * @param out OutputStream to serialize the object <var>downloadMessageRequest</var>
	 * @param downloadMessageRequest Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,DownloadMessageRequest downloadMessageRequest) throws SerializerException {
		this.objToXml(out, DownloadMessageRequest.class, downloadMessageRequest, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>downloadMessageRequest</var> of type {@link backend.ecodex.org._1_1.DownloadMessageRequest}
	 * 
	 * @param out OutputStream to serialize the object <var>downloadMessageRequest</var>
	 * @param downloadMessageRequest Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,DownloadMessageRequest downloadMessageRequest,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, DownloadMessageRequest.class, downloadMessageRequest, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>downloadMessageRequest</var> of type {@link backend.ecodex.org._1_1.DownloadMessageRequest}
	 * 
	 * @param downloadMessageRequest Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(DownloadMessageRequest downloadMessageRequest) throws SerializerException {
		return this.objToXml(DownloadMessageRequest.class, downloadMessageRequest, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>downloadMessageRequest</var> of type {@link backend.ecodex.org._1_1.DownloadMessageRequest}
	 * 
	 * @param downloadMessageRequest Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(DownloadMessageRequest downloadMessageRequest,boolean prettyPrint) throws SerializerException {
		return this.objToXml(DownloadMessageRequest.class, downloadMessageRequest, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>downloadMessageRequest</var> of type {@link backend.ecodex.org._1_1.DownloadMessageRequest}
	 * 
	 * @param downloadMessageRequest Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(DownloadMessageRequest downloadMessageRequest) throws SerializerException {
		return this.objToXml(DownloadMessageRequest.class, downloadMessageRequest, false).toString();
	}
	/**
	 * Serialize to String the object <var>downloadMessageRequest</var> of type {@link backend.ecodex.org._1_1.DownloadMessageRequest}
	 * 
	 * @param downloadMessageRequest Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(DownloadMessageRequest downloadMessageRequest,boolean prettyPrint) throws SerializerException {
		return this.objToXml(DownloadMessageRequest.class, downloadMessageRequest, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: listPendingMessagesResponse
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>listPendingMessagesResponse</var> of type {@link backend.ecodex.org._1_1.ListPendingMessagesResponse}
	 * 
	 * @param fileName Xml file to serialize the object <var>listPendingMessagesResponse</var>
	 * @param listPendingMessagesResponse Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ListPendingMessagesResponse listPendingMessagesResponse) throws SerializerException {
		this.objToXml(fileName, ListPendingMessagesResponse.class, listPendingMessagesResponse, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>listPendingMessagesResponse</var> of type {@link backend.ecodex.org._1_1.ListPendingMessagesResponse}
	 * 
	 * @param fileName Xml file to serialize the object <var>listPendingMessagesResponse</var>
	 * @param listPendingMessagesResponse Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ListPendingMessagesResponse listPendingMessagesResponse,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, ListPendingMessagesResponse.class, listPendingMessagesResponse, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>listPendingMessagesResponse</var> of type {@link backend.ecodex.org._1_1.ListPendingMessagesResponse}
	 * 
	 * @param file Xml file to serialize the object <var>listPendingMessagesResponse</var>
	 * @param listPendingMessagesResponse Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ListPendingMessagesResponse listPendingMessagesResponse) throws SerializerException {
		this.objToXml(file, ListPendingMessagesResponse.class, listPendingMessagesResponse, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>listPendingMessagesResponse</var> of type {@link backend.ecodex.org._1_1.ListPendingMessagesResponse}
	 * 
	 * @param file Xml file to serialize the object <var>listPendingMessagesResponse</var>
	 * @param listPendingMessagesResponse Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ListPendingMessagesResponse listPendingMessagesResponse,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, ListPendingMessagesResponse.class, listPendingMessagesResponse, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>listPendingMessagesResponse</var> of type {@link backend.ecodex.org._1_1.ListPendingMessagesResponse}
	 * 
	 * @param out OutputStream to serialize the object <var>listPendingMessagesResponse</var>
	 * @param listPendingMessagesResponse Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ListPendingMessagesResponse listPendingMessagesResponse) throws SerializerException {
		this.objToXml(out, ListPendingMessagesResponse.class, listPendingMessagesResponse, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>listPendingMessagesResponse</var> of type {@link backend.ecodex.org._1_1.ListPendingMessagesResponse}
	 * 
	 * @param out OutputStream to serialize the object <var>listPendingMessagesResponse</var>
	 * @param listPendingMessagesResponse Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ListPendingMessagesResponse listPendingMessagesResponse,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, ListPendingMessagesResponse.class, listPendingMessagesResponse, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>listPendingMessagesResponse</var> of type {@link backend.ecodex.org._1_1.ListPendingMessagesResponse}
	 * 
	 * @param listPendingMessagesResponse Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ListPendingMessagesResponse listPendingMessagesResponse) throws SerializerException {
		return this.objToXml(ListPendingMessagesResponse.class, listPendingMessagesResponse, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>listPendingMessagesResponse</var> of type {@link backend.ecodex.org._1_1.ListPendingMessagesResponse}
	 * 
	 * @param listPendingMessagesResponse Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ListPendingMessagesResponse listPendingMessagesResponse,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ListPendingMessagesResponse.class, listPendingMessagesResponse, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>listPendingMessagesResponse</var> of type {@link backend.ecodex.org._1_1.ListPendingMessagesResponse}
	 * 
	 * @param listPendingMessagesResponse Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ListPendingMessagesResponse listPendingMessagesResponse) throws SerializerException {
		return this.objToXml(ListPendingMessagesResponse.class, listPendingMessagesResponse, false).toString();
	}
	/**
	 * Serialize to String the object <var>listPendingMessagesResponse</var> of type {@link backend.ecodex.org._1_1.ListPendingMessagesResponse}
	 * 
	 * @param listPendingMessagesResponse Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ListPendingMessagesResponse listPendingMessagesResponse,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ListPendingMessagesResponse.class, listPendingMessagesResponse, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: messageStatusRequest
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>messageStatusRequest</var> of type {@link backend.ecodex.org._1_1.MessageStatusRequest}
	 * 
	 * @param fileName Xml file to serialize the object <var>messageStatusRequest</var>
	 * @param messageStatusRequest Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,MessageStatusRequest messageStatusRequest) throws SerializerException {
		this.objToXml(fileName, MessageStatusRequest.class, messageStatusRequest, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>messageStatusRequest</var> of type {@link backend.ecodex.org._1_1.MessageStatusRequest}
	 * 
	 * @param fileName Xml file to serialize the object <var>messageStatusRequest</var>
	 * @param messageStatusRequest Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,MessageStatusRequest messageStatusRequest,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, MessageStatusRequest.class, messageStatusRequest, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>messageStatusRequest</var> of type {@link backend.ecodex.org._1_1.MessageStatusRequest}
	 * 
	 * @param file Xml file to serialize the object <var>messageStatusRequest</var>
	 * @param messageStatusRequest Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,MessageStatusRequest messageStatusRequest) throws SerializerException {
		this.objToXml(file, MessageStatusRequest.class, messageStatusRequest, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>messageStatusRequest</var> of type {@link backend.ecodex.org._1_1.MessageStatusRequest}
	 * 
	 * @param file Xml file to serialize the object <var>messageStatusRequest</var>
	 * @param messageStatusRequest Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,MessageStatusRequest messageStatusRequest,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, MessageStatusRequest.class, messageStatusRequest, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>messageStatusRequest</var> of type {@link backend.ecodex.org._1_1.MessageStatusRequest}
	 * 
	 * @param out OutputStream to serialize the object <var>messageStatusRequest</var>
	 * @param messageStatusRequest Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,MessageStatusRequest messageStatusRequest) throws SerializerException {
		this.objToXml(out, MessageStatusRequest.class, messageStatusRequest, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>messageStatusRequest</var> of type {@link backend.ecodex.org._1_1.MessageStatusRequest}
	 * 
	 * @param out OutputStream to serialize the object <var>messageStatusRequest</var>
	 * @param messageStatusRequest Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,MessageStatusRequest messageStatusRequest,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, MessageStatusRequest.class, messageStatusRequest, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>messageStatusRequest</var> of type {@link backend.ecodex.org._1_1.MessageStatusRequest}
	 * 
	 * @param messageStatusRequest Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(MessageStatusRequest messageStatusRequest) throws SerializerException {
		return this.objToXml(MessageStatusRequest.class, messageStatusRequest, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>messageStatusRequest</var> of type {@link backend.ecodex.org._1_1.MessageStatusRequest}
	 * 
	 * @param messageStatusRequest Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(MessageStatusRequest messageStatusRequest,boolean prettyPrint) throws SerializerException {
		return this.objToXml(MessageStatusRequest.class, messageStatusRequest, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>messageStatusRequest</var> of type {@link backend.ecodex.org._1_1.MessageStatusRequest}
	 * 
	 * @param messageStatusRequest Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(MessageStatusRequest messageStatusRequest) throws SerializerException {
		return this.objToXml(MessageStatusRequest.class, messageStatusRequest, false).toString();
	}
	/**
	 * Serialize to String the object <var>messageStatusRequest</var> of type {@link backend.ecodex.org._1_1.MessageStatusRequest}
	 * 
	 * @param messageStatusRequest Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(MessageStatusRequest messageStatusRequest,boolean prettyPrint) throws SerializerException {
		return this.objToXml(MessageStatusRequest.class, messageStatusRequest, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: sendRequest
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>sendRequest</var> of type {@link backend.ecodex.org._1_1.SendRequest}
	 * 
	 * @param fileName Xml file to serialize the object <var>sendRequest</var>
	 * @param sendRequest Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,SendRequest sendRequest) throws SerializerException {
		this.objToXml(fileName, SendRequest.class, sendRequest, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>sendRequest</var> of type {@link backend.ecodex.org._1_1.SendRequest}
	 * 
	 * @param fileName Xml file to serialize the object <var>sendRequest</var>
	 * @param sendRequest Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,SendRequest sendRequest,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, SendRequest.class, sendRequest, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>sendRequest</var> of type {@link backend.ecodex.org._1_1.SendRequest}
	 * 
	 * @param file Xml file to serialize the object <var>sendRequest</var>
	 * @param sendRequest Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,SendRequest sendRequest) throws SerializerException {
		this.objToXml(file, SendRequest.class, sendRequest, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>sendRequest</var> of type {@link backend.ecodex.org._1_1.SendRequest}
	 * 
	 * @param file Xml file to serialize the object <var>sendRequest</var>
	 * @param sendRequest Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,SendRequest sendRequest,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, SendRequest.class, sendRequest, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>sendRequest</var> of type {@link backend.ecodex.org._1_1.SendRequest}
	 * 
	 * @param out OutputStream to serialize the object <var>sendRequest</var>
	 * @param sendRequest Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,SendRequest sendRequest) throws SerializerException {
		this.objToXml(out, SendRequest.class, sendRequest, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>sendRequest</var> of type {@link backend.ecodex.org._1_1.SendRequest}
	 * 
	 * @param out OutputStream to serialize the object <var>sendRequest</var>
	 * @param sendRequest Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,SendRequest sendRequest,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, SendRequest.class, sendRequest, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>sendRequest</var> of type {@link backend.ecodex.org._1_1.SendRequest}
	 * 
	 * @param sendRequest Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(SendRequest sendRequest) throws SerializerException {
		return this.objToXml(SendRequest.class, sendRequest, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>sendRequest</var> of type {@link backend.ecodex.org._1_1.SendRequest}
	 * 
	 * @param sendRequest Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(SendRequest sendRequest,boolean prettyPrint) throws SerializerException {
		return this.objToXml(SendRequest.class, sendRequest, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>sendRequest</var> of type {@link backend.ecodex.org._1_1.SendRequest}
	 * 
	 * @param sendRequest Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(SendRequest sendRequest) throws SerializerException {
		return this.objToXml(SendRequest.class, sendRequest, false).toString();
	}
	/**
	 * Serialize to String the object <var>sendRequest</var> of type {@link backend.ecodex.org._1_1.SendRequest}
	 * 
	 * @param sendRequest Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(SendRequest sendRequest,boolean prettyPrint) throws SerializerException {
		return this.objToXml(SendRequest.class, sendRequest, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: sendRequestURL
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>sendRequestURL</var> of type {@link backend.ecodex.org._1_1.SendRequestURL}
	 * 
	 * @param fileName Xml file to serialize the object <var>sendRequestURL</var>
	 * @param sendRequestURL Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,SendRequestURL sendRequestURL) throws SerializerException {
		this.objToXml(fileName, SendRequestURL.class, sendRequestURL, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>sendRequestURL</var> of type {@link backend.ecodex.org._1_1.SendRequestURL}
	 * 
	 * @param fileName Xml file to serialize the object <var>sendRequestURL</var>
	 * @param sendRequestURL Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,SendRequestURL sendRequestURL,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, SendRequestURL.class, sendRequestURL, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>sendRequestURL</var> of type {@link backend.ecodex.org._1_1.SendRequestURL}
	 * 
	 * @param file Xml file to serialize the object <var>sendRequestURL</var>
	 * @param sendRequestURL Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,SendRequestURL sendRequestURL) throws SerializerException {
		this.objToXml(file, SendRequestURL.class, sendRequestURL, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>sendRequestURL</var> of type {@link backend.ecodex.org._1_1.SendRequestURL}
	 * 
	 * @param file Xml file to serialize the object <var>sendRequestURL</var>
	 * @param sendRequestURL Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,SendRequestURL sendRequestURL,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, SendRequestURL.class, sendRequestURL, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>sendRequestURL</var> of type {@link backend.ecodex.org._1_1.SendRequestURL}
	 * 
	 * @param out OutputStream to serialize the object <var>sendRequestURL</var>
	 * @param sendRequestURL Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,SendRequestURL sendRequestURL) throws SerializerException {
		this.objToXml(out, SendRequestURL.class, sendRequestURL, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>sendRequestURL</var> of type {@link backend.ecodex.org._1_1.SendRequestURL}
	 * 
	 * @param out OutputStream to serialize the object <var>sendRequestURL</var>
	 * @param sendRequestURL Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,SendRequestURL sendRequestURL,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, SendRequestURL.class, sendRequestURL, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>sendRequestURL</var> of type {@link backend.ecodex.org._1_1.SendRequestURL}
	 * 
	 * @param sendRequestURL Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(SendRequestURL sendRequestURL) throws SerializerException {
		return this.objToXml(SendRequestURL.class, sendRequestURL, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>sendRequestURL</var> of type {@link backend.ecodex.org._1_1.SendRequestURL}
	 * 
	 * @param sendRequestURL Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(SendRequestURL sendRequestURL,boolean prettyPrint) throws SerializerException {
		return this.objToXml(SendRequestURL.class, sendRequestURL, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>sendRequestURL</var> of type {@link backend.ecodex.org._1_1.SendRequestURL}
	 * 
	 * @param sendRequestURL Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(SendRequestURL sendRequestURL) throws SerializerException {
		return this.objToXml(SendRequestURL.class, sendRequestURL, false).toString();
	}
	/**
	 * Serialize to String the object <var>sendRequestURL</var> of type {@link backend.ecodex.org._1_1.SendRequestURL}
	 * 
	 * @param sendRequestURL Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(SendRequestURL sendRequestURL,boolean prettyPrint) throws SerializerException {
		return this.objToXml(SendRequestURL.class, sendRequestURL, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: sendResponse
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>sendResponse</var> of type {@link backend.ecodex.org._1_1.SendResponse}
	 * 
	 * @param fileName Xml file to serialize the object <var>sendResponse</var>
	 * @param sendResponse Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,SendResponse sendResponse) throws SerializerException {
		this.objToXml(fileName, SendResponse.class, sendResponse, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>sendResponse</var> of type {@link backend.ecodex.org._1_1.SendResponse}
	 * 
	 * @param fileName Xml file to serialize the object <var>sendResponse</var>
	 * @param sendResponse Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,SendResponse sendResponse,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, SendResponse.class, sendResponse, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>sendResponse</var> of type {@link backend.ecodex.org._1_1.SendResponse}
	 * 
	 * @param file Xml file to serialize the object <var>sendResponse</var>
	 * @param sendResponse Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,SendResponse sendResponse) throws SerializerException {
		this.objToXml(file, SendResponse.class, sendResponse, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>sendResponse</var> of type {@link backend.ecodex.org._1_1.SendResponse}
	 * 
	 * @param file Xml file to serialize the object <var>sendResponse</var>
	 * @param sendResponse Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,SendResponse sendResponse,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, SendResponse.class, sendResponse, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>sendResponse</var> of type {@link backend.ecodex.org._1_1.SendResponse}
	 * 
	 * @param out OutputStream to serialize the object <var>sendResponse</var>
	 * @param sendResponse Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,SendResponse sendResponse) throws SerializerException {
		this.objToXml(out, SendResponse.class, sendResponse, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>sendResponse</var> of type {@link backend.ecodex.org._1_1.SendResponse}
	 * 
	 * @param out OutputStream to serialize the object <var>sendResponse</var>
	 * @param sendResponse Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,SendResponse sendResponse,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, SendResponse.class, sendResponse, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>sendResponse</var> of type {@link backend.ecodex.org._1_1.SendResponse}
	 * 
	 * @param sendResponse Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(SendResponse sendResponse) throws SerializerException {
		return this.objToXml(SendResponse.class, sendResponse, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>sendResponse</var> of type {@link backend.ecodex.org._1_1.SendResponse}
	 * 
	 * @param sendResponse Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(SendResponse sendResponse,boolean prettyPrint) throws SerializerException {
		return this.objToXml(SendResponse.class, sendResponse, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>sendResponse</var> of type {@link backend.ecodex.org._1_1.SendResponse}
	 * 
	 * @param sendResponse Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(SendResponse sendResponse) throws SerializerException {
		return this.objToXml(SendResponse.class, sendResponse, false).toString();
	}
	/**
	 * Serialize to String the object <var>sendResponse</var> of type {@link backend.ecodex.org._1_1.SendResponse}
	 * 
	 * @param sendResponse Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(SendResponse sendResponse,boolean prettyPrint) throws SerializerException {
		return this.objToXml(SendResponse.class, sendResponse, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: getErrorsRequest
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>getErrorsRequest</var> of type {@link backend.ecodex.org._1_1.GetErrorsRequest}
	 * 
	 * @param fileName Xml file to serialize the object <var>getErrorsRequest</var>
	 * @param getErrorsRequest Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,GetErrorsRequest getErrorsRequest) throws SerializerException {
		this.objToXml(fileName, GetErrorsRequest.class, getErrorsRequest, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>getErrorsRequest</var> of type {@link backend.ecodex.org._1_1.GetErrorsRequest}
	 * 
	 * @param fileName Xml file to serialize the object <var>getErrorsRequest</var>
	 * @param getErrorsRequest Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,GetErrorsRequest getErrorsRequest,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, GetErrorsRequest.class, getErrorsRequest, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>getErrorsRequest</var> of type {@link backend.ecodex.org._1_1.GetErrorsRequest}
	 * 
	 * @param file Xml file to serialize the object <var>getErrorsRequest</var>
	 * @param getErrorsRequest Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,GetErrorsRequest getErrorsRequest) throws SerializerException {
		this.objToXml(file, GetErrorsRequest.class, getErrorsRequest, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>getErrorsRequest</var> of type {@link backend.ecodex.org._1_1.GetErrorsRequest}
	 * 
	 * @param file Xml file to serialize the object <var>getErrorsRequest</var>
	 * @param getErrorsRequest Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,GetErrorsRequest getErrorsRequest,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, GetErrorsRequest.class, getErrorsRequest, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>getErrorsRequest</var> of type {@link backend.ecodex.org._1_1.GetErrorsRequest}
	 * 
	 * @param out OutputStream to serialize the object <var>getErrorsRequest</var>
	 * @param getErrorsRequest Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,GetErrorsRequest getErrorsRequest) throws SerializerException {
		this.objToXml(out, GetErrorsRequest.class, getErrorsRequest, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>getErrorsRequest</var> of type {@link backend.ecodex.org._1_1.GetErrorsRequest}
	 * 
	 * @param out OutputStream to serialize the object <var>getErrorsRequest</var>
	 * @param getErrorsRequest Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,GetErrorsRequest getErrorsRequest,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, GetErrorsRequest.class, getErrorsRequest, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>getErrorsRequest</var> of type {@link backend.ecodex.org._1_1.GetErrorsRequest}
	 * 
	 * @param getErrorsRequest Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(GetErrorsRequest getErrorsRequest) throws SerializerException {
		return this.objToXml(GetErrorsRequest.class, getErrorsRequest, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>getErrorsRequest</var> of type {@link backend.ecodex.org._1_1.GetErrorsRequest}
	 * 
	 * @param getErrorsRequest Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(GetErrorsRequest getErrorsRequest,boolean prettyPrint) throws SerializerException {
		return this.objToXml(GetErrorsRequest.class, getErrorsRequest, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>getErrorsRequest</var> of type {@link backend.ecodex.org._1_1.GetErrorsRequest}
	 * 
	 * @param getErrorsRequest Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(GetErrorsRequest getErrorsRequest) throws SerializerException {
		return this.objToXml(GetErrorsRequest.class, getErrorsRequest, false).toString();
	}
	/**
	 * Serialize to String the object <var>getErrorsRequest</var> of type {@link backend.ecodex.org._1_1.GetErrorsRequest}
	 * 
	 * @param getErrorsRequest Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(GetErrorsRequest getErrorsRequest,boolean prettyPrint) throws SerializerException {
		return this.objToXml(GetErrorsRequest.class, getErrorsRequest, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: PayloadURLType
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>payloadURLType</var> of type {@link backend.ecodex.org._1_1.PayloadURLType}
	 * 
	 * @param fileName Xml file to serialize the object <var>payloadURLType</var>
	 * @param payloadURLType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,PayloadURLType payloadURLType) throws SerializerException {
		this.objToXml(fileName, PayloadURLType.class, payloadURLType, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>payloadURLType</var> of type {@link backend.ecodex.org._1_1.PayloadURLType}
	 * 
	 * @param fileName Xml file to serialize the object <var>payloadURLType</var>
	 * @param payloadURLType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,PayloadURLType payloadURLType,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, PayloadURLType.class, payloadURLType, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>payloadURLType</var> of type {@link backend.ecodex.org._1_1.PayloadURLType}
	 * 
	 * @param file Xml file to serialize the object <var>payloadURLType</var>
	 * @param payloadURLType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,PayloadURLType payloadURLType) throws SerializerException {
		this.objToXml(file, PayloadURLType.class, payloadURLType, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>payloadURLType</var> of type {@link backend.ecodex.org._1_1.PayloadURLType}
	 * 
	 * @param file Xml file to serialize the object <var>payloadURLType</var>
	 * @param payloadURLType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,PayloadURLType payloadURLType,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, PayloadURLType.class, payloadURLType, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>payloadURLType</var> of type {@link backend.ecodex.org._1_1.PayloadURLType}
	 * 
	 * @param out OutputStream to serialize the object <var>payloadURLType</var>
	 * @param payloadURLType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,PayloadURLType payloadURLType) throws SerializerException {
		this.objToXml(out, PayloadURLType.class, payloadURLType, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>payloadURLType</var> of type {@link backend.ecodex.org._1_1.PayloadURLType}
	 * 
	 * @param out OutputStream to serialize the object <var>payloadURLType</var>
	 * @param payloadURLType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,PayloadURLType payloadURLType,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, PayloadURLType.class, payloadURLType, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>payloadURLType</var> of type {@link backend.ecodex.org._1_1.PayloadURLType}
	 * 
	 * @param payloadURLType Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(PayloadURLType payloadURLType) throws SerializerException {
		return this.objToXml(PayloadURLType.class, payloadURLType, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>payloadURLType</var> of type {@link backend.ecodex.org._1_1.PayloadURLType}
	 * 
	 * @param payloadURLType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(PayloadURLType payloadURLType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(PayloadURLType.class, payloadURLType, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>payloadURLType</var> of type {@link backend.ecodex.org._1_1.PayloadURLType}
	 * 
	 * @param payloadURLType Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(PayloadURLType payloadURLType) throws SerializerException {
		return this.objToXml(PayloadURLType.class, payloadURLType, false).toString();
	}
	/**
	 * Serialize to String the object <var>payloadURLType</var> of type {@link backend.ecodex.org._1_1.PayloadURLType}
	 * 
	 * @param payloadURLType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(PayloadURLType payloadURLType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(PayloadURLType.class, payloadURLType, prettyPrint).toString();
	}
	
	
	

}
