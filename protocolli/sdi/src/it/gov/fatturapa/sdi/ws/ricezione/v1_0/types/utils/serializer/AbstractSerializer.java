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
package it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.utils.serializer;

import org.openspcoop2.generic_project.exception.SerializerException;
import org.openspcoop2.utils.beans.WriteToSerializerType;
import org.openspcoop2.utils.xml.JaxbUtils;

import it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.FileSdIConMetadatiType;
import it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.FileSdIBaseType;
import it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.FileSdIType;
import it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.RispostaSdINotificaEsitoType;

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
	 Object: fileSdIConMetadati_Type
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>fileSdIConMetadatiType</var> of type {@link it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.FileSdIConMetadatiType}
	 * 
	 * @param fileName Xml file to serialize the object <var>fileSdIConMetadatiType</var>
	 * @param fileSdIConMetadatiType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,FileSdIConMetadatiType fileSdIConMetadatiType) throws SerializerException {
		this.objToXml(fileName, FileSdIConMetadatiType.class, fileSdIConMetadatiType, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>fileSdIConMetadatiType</var> of type {@link it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.FileSdIConMetadatiType}
	 * 
	 * @param fileName Xml file to serialize the object <var>fileSdIConMetadatiType</var>
	 * @param fileSdIConMetadatiType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,FileSdIConMetadatiType fileSdIConMetadatiType,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, FileSdIConMetadatiType.class, fileSdIConMetadatiType, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>fileSdIConMetadatiType</var> of type {@link it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.FileSdIConMetadatiType}
	 * 
	 * @param file Xml file to serialize the object <var>fileSdIConMetadatiType</var>
	 * @param fileSdIConMetadatiType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,FileSdIConMetadatiType fileSdIConMetadatiType) throws SerializerException {
		this.objToXml(file, FileSdIConMetadatiType.class, fileSdIConMetadatiType, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>fileSdIConMetadatiType</var> of type {@link it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.FileSdIConMetadatiType}
	 * 
	 * @param file Xml file to serialize the object <var>fileSdIConMetadatiType</var>
	 * @param fileSdIConMetadatiType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,FileSdIConMetadatiType fileSdIConMetadatiType,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, FileSdIConMetadatiType.class, fileSdIConMetadatiType, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>fileSdIConMetadatiType</var> of type {@link it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.FileSdIConMetadatiType}
	 * 
	 * @param out OutputStream to serialize the object <var>fileSdIConMetadatiType</var>
	 * @param fileSdIConMetadatiType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,FileSdIConMetadatiType fileSdIConMetadatiType) throws SerializerException {
		this.objToXml(out, FileSdIConMetadatiType.class, fileSdIConMetadatiType, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>fileSdIConMetadatiType</var> of type {@link it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.FileSdIConMetadatiType}
	 * 
	 * @param out OutputStream to serialize the object <var>fileSdIConMetadatiType</var>
	 * @param fileSdIConMetadatiType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,FileSdIConMetadatiType fileSdIConMetadatiType,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, FileSdIConMetadatiType.class, fileSdIConMetadatiType, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>fileSdIConMetadatiType</var> of type {@link it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.FileSdIConMetadatiType}
	 * 
	 * @param fileSdIConMetadatiType Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(FileSdIConMetadatiType fileSdIConMetadatiType) throws SerializerException {
		return this.objToXml(FileSdIConMetadatiType.class, fileSdIConMetadatiType, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>fileSdIConMetadatiType</var> of type {@link it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.FileSdIConMetadatiType}
	 * 
	 * @param fileSdIConMetadatiType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(FileSdIConMetadatiType fileSdIConMetadatiType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(FileSdIConMetadatiType.class, fileSdIConMetadatiType, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>fileSdIConMetadatiType</var> of type {@link it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.FileSdIConMetadatiType}
	 * 
	 * @param fileSdIConMetadatiType Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(FileSdIConMetadatiType fileSdIConMetadatiType) throws SerializerException {
		return this.objToXml(FileSdIConMetadatiType.class, fileSdIConMetadatiType, false).toString();
	}
	/**
	 * Serialize to String the object <var>fileSdIConMetadatiType</var> of type {@link it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.FileSdIConMetadatiType}
	 * 
	 * @param fileSdIConMetadatiType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(FileSdIConMetadatiType fileSdIConMetadatiType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(FileSdIConMetadatiType.class, fileSdIConMetadatiType, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: fileSdIBase_Type
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>fileSdIBaseType</var> of type {@link it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.FileSdIBaseType}
	 * 
	 * @param fileName Xml file to serialize the object <var>fileSdIBaseType</var>
	 * @param fileSdIBaseType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,FileSdIBaseType fileSdIBaseType) throws SerializerException {
		this.objToXml(fileName, FileSdIBaseType.class, fileSdIBaseType, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>fileSdIBaseType</var> of type {@link it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.FileSdIBaseType}
	 * 
	 * @param fileName Xml file to serialize the object <var>fileSdIBaseType</var>
	 * @param fileSdIBaseType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,FileSdIBaseType fileSdIBaseType,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, FileSdIBaseType.class, fileSdIBaseType, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>fileSdIBaseType</var> of type {@link it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.FileSdIBaseType}
	 * 
	 * @param file Xml file to serialize the object <var>fileSdIBaseType</var>
	 * @param fileSdIBaseType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,FileSdIBaseType fileSdIBaseType) throws SerializerException {
		this.objToXml(file, FileSdIBaseType.class, fileSdIBaseType, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>fileSdIBaseType</var> of type {@link it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.FileSdIBaseType}
	 * 
	 * @param file Xml file to serialize the object <var>fileSdIBaseType</var>
	 * @param fileSdIBaseType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,FileSdIBaseType fileSdIBaseType,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, FileSdIBaseType.class, fileSdIBaseType, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>fileSdIBaseType</var> of type {@link it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.FileSdIBaseType}
	 * 
	 * @param out OutputStream to serialize the object <var>fileSdIBaseType</var>
	 * @param fileSdIBaseType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,FileSdIBaseType fileSdIBaseType) throws SerializerException {
		this.objToXml(out, FileSdIBaseType.class, fileSdIBaseType, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>fileSdIBaseType</var> of type {@link it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.FileSdIBaseType}
	 * 
	 * @param out OutputStream to serialize the object <var>fileSdIBaseType</var>
	 * @param fileSdIBaseType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,FileSdIBaseType fileSdIBaseType,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, FileSdIBaseType.class, fileSdIBaseType, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>fileSdIBaseType</var> of type {@link it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.FileSdIBaseType}
	 * 
	 * @param fileSdIBaseType Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(FileSdIBaseType fileSdIBaseType) throws SerializerException {
		return this.objToXml(FileSdIBaseType.class, fileSdIBaseType, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>fileSdIBaseType</var> of type {@link it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.FileSdIBaseType}
	 * 
	 * @param fileSdIBaseType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(FileSdIBaseType fileSdIBaseType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(FileSdIBaseType.class, fileSdIBaseType, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>fileSdIBaseType</var> of type {@link it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.FileSdIBaseType}
	 * 
	 * @param fileSdIBaseType Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(FileSdIBaseType fileSdIBaseType) throws SerializerException {
		return this.objToXml(FileSdIBaseType.class, fileSdIBaseType, false).toString();
	}
	/**
	 * Serialize to String the object <var>fileSdIBaseType</var> of type {@link it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.FileSdIBaseType}
	 * 
	 * @param fileSdIBaseType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(FileSdIBaseType fileSdIBaseType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(FileSdIBaseType.class, fileSdIBaseType, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: fileSdI_Type
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>fileSdIType</var> of type {@link it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.FileSdIType}
	 * 
	 * @param fileName Xml file to serialize the object <var>fileSdIType</var>
	 * @param fileSdIType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,FileSdIType fileSdIType) throws SerializerException {
		this.objToXml(fileName, FileSdIType.class, fileSdIType, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>fileSdIType</var> of type {@link it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.FileSdIType}
	 * 
	 * @param fileName Xml file to serialize the object <var>fileSdIType</var>
	 * @param fileSdIType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,FileSdIType fileSdIType,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, FileSdIType.class, fileSdIType, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>fileSdIType</var> of type {@link it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.FileSdIType}
	 * 
	 * @param file Xml file to serialize the object <var>fileSdIType</var>
	 * @param fileSdIType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,FileSdIType fileSdIType) throws SerializerException {
		this.objToXml(file, FileSdIType.class, fileSdIType, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>fileSdIType</var> of type {@link it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.FileSdIType}
	 * 
	 * @param file Xml file to serialize the object <var>fileSdIType</var>
	 * @param fileSdIType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,FileSdIType fileSdIType,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, FileSdIType.class, fileSdIType, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>fileSdIType</var> of type {@link it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.FileSdIType}
	 * 
	 * @param out OutputStream to serialize the object <var>fileSdIType</var>
	 * @param fileSdIType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,FileSdIType fileSdIType) throws SerializerException {
		this.objToXml(out, FileSdIType.class, fileSdIType, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>fileSdIType</var> of type {@link it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.FileSdIType}
	 * 
	 * @param out OutputStream to serialize the object <var>fileSdIType</var>
	 * @param fileSdIType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,FileSdIType fileSdIType,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, FileSdIType.class, fileSdIType, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>fileSdIType</var> of type {@link it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.FileSdIType}
	 * 
	 * @param fileSdIType Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(FileSdIType fileSdIType) throws SerializerException {
		return this.objToXml(FileSdIType.class, fileSdIType, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>fileSdIType</var> of type {@link it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.FileSdIType}
	 * 
	 * @param fileSdIType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(FileSdIType fileSdIType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(FileSdIType.class, fileSdIType, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>fileSdIType</var> of type {@link it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.FileSdIType}
	 * 
	 * @param fileSdIType Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(FileSdIType fileSdIType) throws SerializerException {
		return this.objToXml(FileSdIType.class, fileSdIType, false).toString();
	}
	/**
	 * Serialize to String the object <var>fileSdIType</var> of type {@link it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.FileSdIType}
	 * 
	 * @param fileSdIType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(FileSdIType fileSdIType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(FileSdIType.class, fileSdIType, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: rispostaSdINotificaEsito_Type
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>rispostaSdINotificaEsitoType</var> of type {@link it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.RispostaSdINotificaEsitoType}
	 * 
	 * @param fileName Xml file to serialize the object <var>rispostaSdINotificaEsitoType</var>
	 * @param rispostaSdINotificaEsitoType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,RispostaSdINotificaEsitoType rispostaSdINotificaEsitoType) throws SerializerException {
		this.objToXml(fileName, RispostaSdINotificaEsitoType.class, rispostaSdINotificaEsitoType, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>rispostaSdINotificaEsitoType</var> of type {@link it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.RispostaSdINotificaEsitoType}
	 * 
	 * @param fileName Xml file to serialize the object <var>rispostaSdINotificaEsitoType</var>
	 * @param rispostaSdINotificaEsitoType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,RispostaSdINotificaEsitoType rispostaSdINotificaEsitoType,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, RispostaSdINotificaEsitoType.class, rispostaSdINotificaEsitoType, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>rispostaSdINotificaEsitoType</var> of type {@link it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.RispostaSdINotificaEsitoType}
	 * 
	 * @param file Xml file to serialize the object <var>rispostaSdINotificaEsitoType</var>
	 * @param rispostaSdINotificaEsitoType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,RispostaSdINotificaEsitoType rispostaSdINotificaEsitoType) throws SerializerException {
		this.objToXml(file, RispostaSdINotificaEsitoType.class, rispostaSdINotificaEsitoType, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>rispostaSdINotificaEsitoType</var> of type {@link it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.RispostaSdINotificaEsitoType}
	 * 
	 * @param file Xml file to serialize the object <var>rispostaSdINotificaEsitoType</var>
	 * @param rispostaSdINotificaEsitoType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,RispostaSdINotificaEsitoType rispostaSdINotificaEsitoType,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, RispostaSdINotificaEsitoType.class, rispostaSdINotificaEsitoType, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>rispostaSdINotificaEsitoType</var> of type {@link it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.RispostaSdINotificaEsitoType}
	 * 
	 * @param out OutputStream to serialize the object <var>rispostaSdINotificaEsitoType</var>
	 * @param rispostaSdINotificaEsitoType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,RispostaSdINotificaEsitoType rispostaSdINotificaEsitoType) throws SerializerException {
		this.objToXml(out, RispostaSdINotificaEsitoType.class, rispostaSdINotificaEsitoType, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>rispostaSdINotificaEsitoType</var> of type {@link it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.RispostaSdINotificaEsitoType}
	 * 
	 * @param out OutputStream to serialize the object <var>rispostaSdINotificaEsitoType</var>
	 * @param rispostaSdINotificaEsitoType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,RispostaSdINotificaEsitoType rispostaSdINotificaEsitoType,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, RispostaSdINotificaEsitoType.class, rispostaSdINotificaEsitoType, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>rispostaSdINotificaEsitoType</var> of type {@link it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.RispostaSdINotificaEsitoType}
	 * 
	 * @param rispostaSdINotificaEsitoType Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(RispostaSdINotificaEsitoType rispostaSdINotificaEsitoType) throws SerializerException {
		return this.objToXml(RispostaSdINotificaEsitoType.class, rispostaSdINotificaEsitoType, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>rispostaSdINotificaEsitoType</var> of type {@link it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.RispostaSdINotificaEsitoType}
	 * 
	 * @param rispostaSdINotificaEsitoType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(RispostaSdINotificaEsitoType rispostaSdINotificaEsitoType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(RispostaSdINotificaEsitoType.class, rispostaSdINotificaEsitoType, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>rispostaSdINotificaEsitoType</var> of type {@link it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.RispostaSdINotificaEsitoType}
	 * 
	 * @param rispostaSdINotificaEsitoType Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(RispostaSdINotificaEsitoType rispostaSdINotificaEsitoType) throws SerializerException {
		return this.objToXml(RispostaSdINotificaEsitoType.class, rispostaSdINotificaEsitoType, false).toString();
	}
	/**
	 * Serialize to String the object <var>rispostaSdINotificaEsitoType</var> of type {@link it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.RispostaSdINotificaEsitoType}
	 * 
	 * @param rispostaSdINotificaEsitoType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(RispostaSdINotificaEsitoType rispostaSdINotificaEsitoType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(RispostaSdINotificaEsitoType.class, rispostaSdINotificaEsitoType, prettyPrint).toString();
	}
	
	
	

}
