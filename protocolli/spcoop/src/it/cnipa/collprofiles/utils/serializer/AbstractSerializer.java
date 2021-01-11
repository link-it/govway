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
package it.cnipa.collprofiles.utils.serializer;

import org.openspcoop2.generic_project.exception.SerializerException;
import org.openspcoop2.utils.beans.WriteToSerializerType;
import org.openspcoop2.utils.xml.JaxbUtils;

import it.cnipa.collprofiles.OperationType;
import it.cnipa.collprofiles.OperationListType;
import it.cnipa.collprofiles.EgovDecllElement;

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
	 Object: operationType
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>operationType</var> of type {@link it.cnipa.collprofiles.OperationType}
	 * 
	 * @param fileName Xml file to serialize the object <var>operationType</var>
	 * @param operationType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,OperationType operationType) throws SerializerException {
		this.objToXml(fileName, OperationType.class, operationType, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>operationType</var> of type {@link it.cnipa.collprofiles.OperationType}
	 * 
	 * @param fileName Xml file to serialize the object <var>operationType</var>
	 * @param operationType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,OperationType operationType,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, OperationType.class, operationType, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>operationType</var> of type {@link it.cnipa.collprofiles.OperationType}
	 * 
	 * @param file Xml file to serialize the object <var>operationType</var>
	 * @param operationType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,OperationType operationType) throws SerializerException {
		this.objToXml(file, OperationType.class, operationType, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>operationType</var> of type {@link it.cnipa.collprofiles.OperationType}
	 * 
	 * @param file Xml file to serialize the object <var>operationType</var>
	 * @param operationType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,OperationType operationType,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, OperationType.class, operationType, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>operationType</var> of type {@link it.cnipa.collprofiles.OperationType}
	 * 
	 * @param out OutputStream to serialize the object <var>operationType</var>
	 * @param operationType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,OperationType operationType) throws SerializerException {
		this.objToXml(out, OperationType.class, operationType, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>operationType</var> of type {@link it.cnipa.collprofiles.OperationType}
	 * 
	 * @param out OutputStream to serialize the object <var>operationType</var>
	 * @param operationType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,OperationType operationType,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, OperationType.class, operationType, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>operationType</var> of type {@link it.cnipa.collprofiles.OperationType}
	 * 
	 * @param operationType Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(OperationType operationType) throws SerializerException {
		return this.objToXml(OperationType.class, operationType, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>operationType</var> of type {@link it.cnipa.collprofiles.OperationType}
	 * 
	 * @param operationType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(OperationType operationType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(OperationType.class, operationType, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>operationType</var> of type {@link it.cnipa.collprofiles.OperationType}
	 * 
	 * @param operationType Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(OperationType operationType) throws SerializerException {
		return this.objToXml(OperationType.class, operationType, false).toString();
	}
	/**
	 * Serialize to String the object <var>operationType</var> of type {@link it.cnipa.collprofiles.OperationType}
	 * 
	 * @param operationType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(OperationType operationType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(OperationType.class, operationType, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: operationListType
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>operationListType</var> of type {@link it.cnipa.collprofiles.OperationListType}
	 * 
	 * @param fileName Xml file to serialize the object <var>operationListType</var>
	 * @param operationListType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,OperationListType operationListType) throws SerializerException {
		this.objToXml(fileName, OperationListType.class, operationListType, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>operationListType</var> of type {@link it.cnipa.collprofiles.OperationListType}
	 * 
	 * @param fileName Xml file to serialize the object <var>operationListType</var>
	 * @param operationListType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,OperationListType operationListType,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, OperationListType.class, operationListType, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>operationListType</var> of type {@link it.cnipa.collprofiles.OperationListType}
	 * 
	 * @param file Xml file to serialize the object <var>operationListType</var>
	 * @param operationListType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,OperationListType operationListType) throws SerializerException {
		this.objToXml(file, OperationListType.class, operationListType, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>operationListType</var> of type {@link it.cnipa.collprofiles.OperationListType}
	 * 
	 * @param file Xml file to serialize the object <var>operationListType</var>
	 * @param operationListType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,OperationListType operationListType,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, OperationListType.class, operationListType, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>operationListType</var> of type {@link it.cnipa.collprofiles.OperationListType}
	 * 
	 * @param out OutputStream to serialize the object <var>operationListType</var>
	 * @param operationListType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,OperationListType operationListType) throws SerializerException {
		this.objToXml(out, OperationListType.class, operationListType, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>operationListType</var> of type {@link it.cnipa.collprofiles.OperationListType}
	 * 
	 * @param out OutputStream to serialize the object <var>operationListType</var>
	 * @param operationListType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,OperationListType operationListType,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, OperationListType.class, operationListType, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>operationListType</var> of type {@link it.cnipa.collprofiles.OperationListType}
	 * 
	 * @param operationListType Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(OperationListType operationListType) throws SerializerException {
		return this.objToXml(OperationListType.class, operationListType, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>operationListType</var> of type {@link it.cnipa.collprofiles.OperationListType}
	 * 
	 * @param operationListType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(OperationListType operationListType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(OperationListType.class, operationListType, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>operationListType</var> of type {@link it.cnipa.collprofiles.OperationListType}
	 * 
	 * @param operationListType Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(OperationListType operationListType) throws SerializerException {
		return this.objToXml(OperationListType.class, operationListType, false).toString();
	}
	/**
	 * Serialize to String the object <var>operationListType</var> of type {@link it.cnipa.collprofiles.OperationListType}
	 * 
	 * @param operationListType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(OperationListType operationListType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(OperationListType.class, operationListType, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: egovDecllElement
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>egovDecllElement</var> of type {@link it.cnipa.collprofiles.EgovDecllElement}
	 * 
	 * @param fileName Xml file to serialize the object <var>egovDecllElement</var>
	 * @param egovDecllElement Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,EgovDecllElement egovDecllElement) throws SerializerException {
		this.objToXml(fileName, EgovDecllElement.class, egovDecllElement, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>egovDecllElement</var> of type {@link it.cnipa.collprofiles.EgovDecllElement}
	 * 
	 * @param fileName Xml file to serialize the object <var>egovDecllElement</var>
	 * @param egovDecllElement Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,EgovDecllElement egovDecllElement,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, EgovDecllElement.class, egovDecllElement, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>egovDecllElement</var> of type {@link it.cnipa.collprofiles.EgovDecllElement}
	 * 
	 * @param file Xml file to serialize the object <var>egovDecllElement</var>
	 * @param egovDecllElement Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,EgovDecllElement egovDecllElement) throws SerializerException {
		this.objToXml(file, EgovDecllElement.class, egovDecllElement, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>egovDecllElement</var> of type {@link it.cnipa.collprofiles.EgovDecllElement}
	 * 
	 * @param file Xml file to serialize the object <var>egovDecllElement</var>
	 * @param egovDecllElement Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,EgovDecllElement egovDecllElement,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, EgovDecllElement.class, egovDecllElement, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>egovDecllElement</var> of type {@link it.cnipa.collprofiles.EgovDecllElement}
	 * 
	 * @param out OutputStream to serialize the object <var>egovDecllElement</var>
	 * @param egovDecllElement Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,EgovDecllElement egovDecllElement) throws SerializerException {
		this.objToXml(out, EgovDecllElement.class, egovDecllElement, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>egovDecllElement</var> of type {@link it.cnipa.collprofiles.EgovDecllElement}
	 * 
	 * @param out OutputStream to serialize the object <var>egovDecllElement</var>
	 * @param egovDecllElement Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,EgovDecllElement egovDecllElement,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, EgovDecllElement.class, egovDecllElement, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>egovDecllElement</var> of type {@link it.cnipa.collprofiles.EgovDecllElement}
	 * 
	 * @param egovDecllElement Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(EgovDecllElement egovDecllElement) throws SerializerException {
		return this.objToXml(EgovDecllElement.class, egovDecllElement, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>egovDecllElement</var> of type {@link it.cnipa.collprofiles.EgovDecllElement}
	 * 
	 * @param egovDecllElement Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(EgovDecllElement egovDecllElement,boolean prettyPrint) throws SerializerException {
		return this.objToXml(EgovDecllElement.class, egovDecllElement, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>egovDecllElement</var> of type {@link it.cnipa.collprofiles.EgovDecllElement}
	 * 
	 * @param egovDecllElement Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(EgovDecllElement egovDecllElement) throws SerializerException {
		return this.objToXml(EgovDecllElement.class, egovDecllElement, false).toString();
	}
	/**
	 * Serialize to String the object <var>egovDecllElement</var> of type {@link it.cnipa.collprofiles.EgovDecllElement}
	 * 
	 * @param egovDecllElement Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(EgovDecllElement egovDecllElement,boolean prettyPrint) throws SerializerException {
		return this.objToXml(EgovDecllElement.class, egovDecllElement, prettyPrint).toString();
	}
	
	
	

}
