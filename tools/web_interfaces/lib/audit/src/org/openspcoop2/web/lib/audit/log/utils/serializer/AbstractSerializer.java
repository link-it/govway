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
package org.openspcoop2.web.lib.audit.log.utils.serializer;

import org.openspcoop2.generic_project.exception.SerializerException;
import org.openspcoop2.utils.beans.WriteToSerializerType;
import org.openspcoop2.utils.xml.JaxbUtils;

import org.openspcoop2.web.lib.audit.log.Operation;
import org.openspcoop2.web.lib.audit.log.Binary;

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
	 Object: operation
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>operation</var> of type {@link org.openspcoop2.web.lib.audit.log.Operation}
	 * 
	 * @param fileName Xml file to serialize the object <var>operation</var>
	 * @param operation Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Operation operation) throws SerializerException {
		this.objToXml(fileName, Operation.class, operation, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>operation</var> of type {@link org.openspcoop2.web.lib.audit.log.Operation}
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
	 * Serialize to file system in <var>file</var> the object <var>operation</var> of type {@link org.openspcoop2.web.lib.audit.log.Operation}
	 * 
	 * @param file Xml file to serialize the object <var>operation</var>
	 * @param operation Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Operation operation) throws SerializerException {
		this.objToXml(file, Operation.class, operation, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>operation</var> of type {@link org.openspcoop2.web.lib.audit.log.Operation}
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
	 * Serialize to output stream <var>out</var> the object <var>operation</var> of type {@link org.openspcoop2.web.lib.audit.log.Operation}
	 * 
	 * @param out OutputStream to serialize the object <var>operation</var>
	 * @param operation Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Operation operation) throws SerializerException {
		this.objToXml(out, Operation.class, operation, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>operation</var> of type {@link org.openspcoop2.web.lib.audit.log.Operation}
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
	 * Serialize to byte array the object <var>operation</var> of type {@link org.openspcoop2.web.lib.audit.log.Operation}
	 * 
	 * @param operation Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Operation operation) throws SerializerException {
		return this.objToXml(Operation.class, operation, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>operation</var> of type {@link org.openspcoop2.web.lib.audit.log.Operation}
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
	 * Serialize to String the object <var>operation</var> of type {@link org.openspcoop2.web.lib.audit.log.Operation}
	 * 
	 * @param operation Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Operation operation) throws SerializerException {
		return this.objToXml(Operation.class, operation, false).toString();
	}
	/**
	 * Serialize to String the object <var>operation</var> of type {@link org.openspcoop2.web.lib.audit.log.Operation}
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
	 Object: binary
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>binary</var> of type {@link org.openspcoop2.web.lib.audit.log.Binary}
	 * 
	 * @param fileName Xml file to serialize the object <var>binary</var>
	 * @param binary Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Binary binary) throws SerializerException {
		this.objToXml(fileName, Binary.class, binary, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>binary</var> of type {@link org.openspcoop2.web.lib.audit.log.Binary}
	 * 
	 * @param fileName Xml file to serialize the object <var>binary</var>
	 * @param binary Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Binary binary,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Binary.class, binary, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>binary</var> of type {@link org.openspcoop2.web.lib.audit.log.Binary}
	 * 
	 * @param file Xml file to serialize the object <var>binary</var>
	 * @param binary Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Binary binary) throws SerializerException {
		this.objToXml(file, Binary.class, binary, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>binary</var> of type {@link org.openspcoop2.web.lib.audit.log.Binary}
	 * 
	 * @param file Xml file to serialize the object <var>binary</var>
	 * @param binary Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Binary binary,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Binary.class, binary, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>binary</var> of type {@link org.openspcoop2.web.lib.audit.log.Binary}
	 * 
	 * @param out OutputStream to serialize the object <var>binary</var>
	 * @param binary Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Binary binary) throws SerializerException {
		this.objToXml(out, Binary.class, binary, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>binary</var> of type {@link org.openspcoop2.web.lib.audit.log.Binary}
	 * 
	 * @param out OutputStream to serialize the object <var>binary</var>
	 * @param binary Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Binary binary,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Binary.class, binary, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>binary</var> of type {@link org.openspcoop2.web.lib.audit.log.Binary}
	 * 
	 * @param binary Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Binary binary) throws SerializerException {
		return this.objToXml(Binary.class, binary, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>binary</var> of type {@link org.openspcoop2.web.lib.audit.log.Binary}
	 * 
	 * @param binary Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Binary binary,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Binary.class, binary, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>binary</var> of type {@link org.openspcoop2.web.lib.audit.log.Binary}
	 * 
	 * @param binary Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Binary binary) throws SerializerException {
		return this.objToXml(Binary.class, binary, false).toString();
	}
	/**
	 * Serialize to String the object <var>binary</var> of type {@link org.openspcoop2.web.lib.audit.log.Binary}
	 * 
	 * @param binary Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Binary binary,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Binary.class, binary, prettyPrint).toString();
	}
	
	
	

}
