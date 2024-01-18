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
package it.gov.fatturapa.sdi.ws.trasmissione.v1_0.types.utils.serializer;

import org.openspcoop2.generic_project.exception.SerializerException;
import org.openspcoop2.utils.beans.WriteToSerializerType;
import org.openspcoop2.utils.xml.JaxbUtils;

import it.gov.fatturapa.sdi.ws.trasmissione.v1_0.types.FileSdIBaseType;
import it.gov.fatturapa.sdi.ws.trasmissione.v1_0.types.FileSdIType;
import it.gov.fatturapa.sdi.ws.trasmissione.v1_0.types.RispostaSdIRiceviFileType;

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
				if(fout!=null){
					fout.flush();
				}
			}catch(Exception e){
				// ignore
			}
			try{
				if(fout!=null){
					fout.close();
				}
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
				if(bout!=null){
					bout.flush();
				}
			}catch(Exception e){
				// ignore
			}
			try{
				if(bout!=null){
					bout.close();
				}
			}catch(Exception e){
				// ignore
			}
		}
		return bout;
	}




	/*
	 =================================================================================
	 Object: fileSdIBase_Type
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>fileSdIBaseType</var> of type {@link it.gov.fatturapa.sdi.ws.trasmissione.v1_0.types.FileSdIBaseType}
	 * 
	 * @param fileName Xml file to serialize the object <var>fileSdIBaseType</var>
	 * @param fileSdIBaseType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,FileSdIBaseType fileSdIBaseType) throws SerializerException {
		this.objToXml(fileName, FileSdIBaseType.class, fileSdIBaseType, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>fileSdIBaseType</var> of type {@link it.gov.fatturapa.sdi.ws.trasmissione.v1_0.types.FileSdIBaseType}
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
	 * Serialize to file system in <var>file</var> the object <var>fileSdIBaseType</var> of type {@link it.gov.fatturapa.sdi.ws.trasmissione.v1_0.types.FileSdIBaseType}
	 * 
	 * @param file Xml file to serialize the object <var>fileSdIBaseType</var>
	 * @param fileSdIBaseType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,FileSdIBaseType fileSdIBaseType) throws SerializerException {
		this.objToXml(file, FileSdIBaseType.class, fileSdIBaseType, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>fileSdIBaseType</var> of type {@link it.gov.fatturapa.sdi.ws.trasmissione.v1_0.types.FileSdIBaseType}
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
	 * Serialize to output stream <var>out</var> the object <var>fileSdIBaseType</var> of type {@link it.gov.fatturapa.sdi.ws.trasmissione.v1_0.types.FileSdIBaseType}
	 * 
	 * @param out OutputStream to serialize the object <var>fileSdIBaseType</var>
	 * @param fileSdIBaseType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,FileSdIBaseType fileSdIBaseType) throws SerializerException {
		this.objToXml(out, FileSdIBaseType.class, fileSdIBaseType, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>fileSdIBaseType</var> of type {@link it.gov.fatturapa.sdi.ws.trasmissione.v1_0.types.FileSdIBaseType}
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
	 * Serialize to byte array the object <var>fileSdIBaseType</var> of type {@link it.gov.fatturapa.sdi.ws.trasmissione.v1_0.types.FileSdIBaseType}
	 * 
	 * @param fileSdIBaseType Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(FileSdIBaseType fileSdIBaseType) throws SerializerException {
		return this.objToXml(FileSdIBaseType.class, fileSdIBaseType, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>fileSdIBaseType</var> of type {@link it.gov.fatturapa.sdi.ws.trasmissione.v1_0.types.FileSdIBaseType}
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
	 * Serialize to String the object <var>fileSdIBaseType</var> of type {@link it.gov.fatturapa.sdi.ws.trasmissione.v1_0.types.FileSdIBaseType}
	 * 
	 * @param fileSdIBaseType Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(FileSdIBaseType fileSdIBaseType) throws SerializerException {
		return this.objToXml(FileSdIBaseType.class, fileSdIBaseType, false).toString();
	}
	/**
	 * Serialize to String the object <var>fileSdIBaseType</var> of type {@link it.gov.fatturapa.sdi.ws.trasmissione.v1_0.types.FileSdIBaseType}
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
	 * Serialize to file system in <var>fileName</var> the object <var>fileSdIType</var> of type {@link it.gov.fatturapa.sdi.ws.trasmissione.v1_0.types.FileSdIType}
	 * 
	 * @param fileName Xml file to serialize the object <var>fileSdIType</var>
	 * @param fileSdIType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,FileSdIType fileSdIType) throws SerializerException {
		this.objToXml(fileName, FileSdIType.class, fileSdIType, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>fileSdIType</var> of type {@link it.gov.fatturapa.sdi.ws.trasmissione.v1_0.types.FileSdIType}
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
	 * Serialize to file system in <var>file</var> the object <var>fileSdIType</var> of type {@link it.gov.fatturapa.sdi.ws.trasmissione.v1_0.types.FileSdIType}
	 * 
	 * @param file Xml file to serialize the object <var>fileSdIType</var>
	 * @param fileSdIType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,FileSdIType fileSdIType) throws SerializerException {
		this.objToXml(file, FileSdIType.class, fileSdIType, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>fileSdIType</var> of type {@link it.gov.fatturapa.sdi.ws.trasmissione.v1_0.types.FileSdIType}
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
	 * Serialize to output stream <var>out</var> the object <var>fileSdIType</var> of type {@link it.gov.fatturapa.sdi.ws.trasmissione.v1_0.types.FileSdIType}
	 * 
	 * @param out OutputStream to serialize the object <var>fileSdIType</var>
	 * @param fileSdIType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,FileSdIType fileSdIType) throws SerializerException {
		this.objToXml(out, FileSdIType.class, fileSdIType, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>fileSdIType</var> of type {@link it.gov.fatturapa.sdi.ws.trasmissione.v1_0.types.FileSdIType}
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
	 * Serialize to byte array the object <var>fileSdIType</var> of type {@link it.gov.fatturapa.sdi.ws.trasmissione.v1_0.types.FileSdIType}
	 * 
	 * @param fileSdIType Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(FileSdIType fileSdIType) throws SerializerException {
		return this.objToXml(FileSdIType.class, fileSdIType, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>fileSdIType</var> of type {@link it.gov.fatturapa.sdi.ws.trasmissione.v1_0.types.FileSdIType}
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
	 * Serialize to String the object <var>fileSdIType</var> of type {@link it.gov.fatturapa.sdi.ws.trasmissione.v1_0.types.FileSdIType}
	 * 
	 * @param fileSdIType Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(FileSdIType fileSdIType) throws SerializerException {
		return this.objToXml(FileSdIType.class, fileSdIType, false).toString();
	}
	/**
	 * Serialize to String the object <var>fileSdIType</var> of type {@link it.gov.fatturapa.sdi.ws.trasmissione.v1_0.types.FileSdIType}
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
	 Object: rispostaSdIRiceviFile_Type
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>rispostaSdIRiceviFileType</var> of type {@link it.gov.fatturapa.sdi.ws.trasmissione.v1_0.types.RispostaSdIRiceviFileType}
	 * 
	 * @param fileName Xml file to serialize the object <var>rispostaSdIRiceviFileType</var>
	 * @param rispostaSdIRiceviFileType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,RispostaSdIRiceviFileType rispostaSdIRiceviFileType) throws SerializerException {
		this.objToXml(fileName, RispostaSdIRiceviFileType.class, rispostaSdIRiceviFileType, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>rispostaSdIRiceviFileType</var> of type {@link it.gov.fatturapa.sdi.ws.trasmissione.v1_0.types.RispostaSdIRiceviFileType}
	 * 
	 * @param fileName Xml file to serialize the object <var>rispostaSdIRiceviFileType</var>
	 * @param rispostaSdIRiceviFileType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,RispostaSdIRiceviFileType rispostaSdIRiceviFileType,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, RispostaSdIRiceviFileType.class, rispostaSdIRiceviFileType, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>rispostaSdIRiceviFileType</var> of type {@link it.gov.fatturapa.sdi.ws.trasmissione.v1_0.types.RispostaSdIRiceviFileType}
	 * 
	 * @param file Xml file to serialize the object <var>rispostaSdIRiceviFileType</var>
	 * @param rispostaSdIRiceviFileType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,RispostaSdIRiceviFileType rispostaSdIRiceviFileType) throws SerializerException {
		this.objToXml(file, RispostaSdIRiceviFileType.class, rispostaSdIRiceviFileType, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>rispostaSdIRiceviFileType</var> of type {@link it.gov.fatturapa.sdi.ws.trasmissione.v1_0.types.RispostaSdIRiceviFileType}
	 * 
	 * @param file Xml file to serialize the object <var>rispostaSdIRiceviFileType</var>
	 * @param rispostaSdIRiceviFileType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,RispostaSdIRiceviFileType rispostaSdIRiceviFileType,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, RispostaSdIRiceviFileType.class, rispostaSdIRiceviFileType, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>rispostaSdIRiceviFileType</var> of type {@link it.gov.fatturapa.sdi.ws.trasmissione.v1_0.types.RispostaSdIRiceviFileType}
	 * 
	 * @param out OutputStream to serialize the object <var>rispostaSdIRiceviFileType</var>
	 * @param rispostaSdIRiceviFileType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,RispostaSdIRiceviFileType rispostaSdIRiceviFileType) throws SerializerException {
		this.objToXml(out, RispostaSdIRiceviFileType.class, rispostaSdIRiceviFileType, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>rispostaSdIRiceviFileType</var> of type {@link it.gov.fatturapa.sdi.ws.trasmissione.v1_0.types.RispostaSdIRiceviFileType}
	 * 
	 * @param out OutputStream to serialize the object <var>rispostaSdIRiceviFileType</var>
	 * @param rispostaSdIRiceviFileType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,RispostaSdIRiceviFileType rispostaSdIRiceviFileType,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, RispostaSdIRiceviFileType.class, rispostaSdIRiceviFileType, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>rispostaSdIRiceviFileType</var> of type {@link it.gov.fatturapa.sdi.ws.trasmissione.v1_0.types.RispostaSdIRiceviFileType}
	 * 
	 * @param rispostaSdIRiceviFileType Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(RispostaSdIRiceviFileType rispostaSdIRiceviFileType) throws SerializerException {
		return this.objToXml(RispostaSdIRiceviFileType.class, rispostaSdIRiceviFileType, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>rispostaSdIRiceviFileType</var> of type {@link it.gov.fatturapa.sdi.ws.trasmissione.v1_0.types.RispostaSdIRiceviFileType}
	 * 
	 * @param rispostaSdIRiceviFileType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(RispostaSdIRiceviFileType rispostaSdIRiceviFileType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(RispostaSdIRiceviFileType.class, rispostaSdIRiceviFileType, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>rispostaSdIRiceviFileType</var> of type {@link it.gov.fatturapa.sdi.ws.trasmissione.v1_0.types.RispostaSdIRiceviFileType}
	 * 
	 * @param rispostaSdIRiceviFileType Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(RispostaSdIRiceviFileType rispostaSdIRiceviFileType) throws SerializerException {
		return this.objToXml(RispostaSdIRiceviFileType.class, rispostaSdIRiceviFileType, false).toString();
	}
	/**
	 * Serialize to String the object <var>rispostaSdIRiceviFileType</var> of type {@link it.gov.fatturapa.sdi.ws.trasmissione.v1_0.types.RispostaSdIRiceviFileType}
	 * 
	 * @param rispostaSdIRiceviFileType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(RispostaSdIRiceviFileType rispostaSdIRiceviFileType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(RispostaSdIRiceviFileType.class, rispostaSdIRiceviFileType, prettyPrint).toString();
	}
	
	
	

}
