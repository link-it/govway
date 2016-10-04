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
package org.openspcoop2.core.api.utils.serializer;

import org.openspcoop2.generic_project.exception.SerializerException;
import org.openspcoop2.utils.beans.WriteToSerializerType;
import org.openspcoop2.utils.xml.JaxbUtils;

import org.openspcoop2.core.api.UrlParameter;
import org.openspcoop2.core.api.Resource;
import org.openspcoop2.core.api.Invocation;
import org.openspcoop2.core.api.UrlParameters;
import org.openspcoop2.core.api.HeaderParameters;
import org.openspcoop2.core.api.HeaderParameter;

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
	 Object: url-parameter
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>urlParameter</var> of type {@link org.openspcoop2.core.api.UrlParameter}
	 * 
	 * @param fileName Xml file to serialize the object <var>urlParameter</var>
	 * @param urlParameter Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,UrlParameter urlParameter) throws SerializerException {
		this.objToXml(fileName, UrlParameter.class, urlParameter, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>urlParameter</var> of type {@link org.openspcoop2.core.api.UrlParameter}
	 * 
	 * @param fileName Xml file to serialize the object <var>urlParameter</var>
	 * @param urlParameter Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,UrlParameter urlParameter,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, UrlParameter.class, urlParameter, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>urlParameter</var> of type {@link org.openspcoop2.core.api.UrlParameter}
	 * 
	 * @param file Xml file to serialize the object <var>urlParameter</var>
	 * @param urlParameter Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,UrlParameter urlParameter) throws SerializerException {
		this.objToXml(file, UrlParameter.class, urlParameter, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>urlParameter</var> of type {@link org.openspcoop2.core.api.UrlParameter}
	 * 
	 * @param file Xml file to serialize the object <var>urlParameter</var>
	 * @param urlParameter Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,UrlParameter urlParameter,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, UrlParameter.class, urlParameter, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>urlParameter</var> of type {@link org.openspcoop2.core.api.UrlParameter}
	 * 
	 * @param out OutputStream to serialize the object <var>urlParameter</var>
	 * @param urlParameter Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,UrlParameter urlParameter) throws SerializerException {
		this.objToXml(out, UrlParameter.class, urlParameter, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>urlParameter</var> of type {@link org.openspcoop2.core.api.UrlParameter}
	 * 
	 * @param out OutputStream to serialize the object <var>urlParameter</var>
	 * @param urlParameter Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,UrlParameter urlParameter,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, UrlParameter.class, urlParameter, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>urlParameter</var> of type {@link org.openspcoop2.core.api.UrlParameter}
	 * 
	 * @param urlParameter Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(UrlParameter urlParameter) throws SerializerException {
		return this.objToXml(UrlParameter.class, urlParameter, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>urlParameter</var> of type {@link org.openspcoop2.core.api.UrlParameter}
	 * 
	 * @param urlParameter Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(UrlParameter urlParameter,boolean prettyPrint) throws SerializerException {
		return this.objToXml(UrlParameter.class, urlParameter, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>urlParameter</var> of type {@link org.openspcoop2.core.api.UrlParameter}
	 * 
	 * @param urlParameter Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(UrlParameter urlParameter) throws SerializerException {
		return this.objToXml(UrlParameter.class, urlParameter, false).toString();
	}
	/**
	 * Serialize to String the object <var>urlParameter</var> of type {@link org.openspcoop2.core.api.UrlParameter}
	 * 
	 * @param urlParameter Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(UrlParameter urlParameter,boolean prettyPrint) throws SerializerException {
		return this.objToXml(UrlParameter.class, urlParameter, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: resource
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>resource</var> of type {@link org.openspcoop2.core.api.Resource}
	 * 
	 * @param fileName Xml file to serialize the object <var>resource</var>
	 * @param resource Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Resource resource) throws SerializerException {
		this.objToXml(fileName, Resource.class, resource, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>resource</var> of type {@link org.openspcoop2.core.api.Resource}
	 * 
	 * @param fileName Xml file to serialize the object <var>resource</var>
	 * @param resource Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Resource resource,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Resource.class, resource, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>resource</var> of type {@link org.openspcoop2.core.api.Resource}
	 * 
	 * @param file Xml file to serialize the object <var>resource</var>
	 * @param resource Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Resource resource) throws SerializerException {
		this.objToXml(file, Resource.class, resource, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>resource</var> of type {@link org.openspcoop2.core.api.Resource}
	 * 
	 * @param file Xml file to serialize the object <var>resource</var>
	 * @param resource Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Resource resource,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Resource.class, resource, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>resource</var> of type {@link org.openspcoop2.core.api.Resource}
	 * 
	 * @param out OutputStream to serialize the object <var>resource</var>
	 * @param resource Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Resource resource) throws SerializerException {
		this.objToXml(out, Resource.class, resource, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>resource</var> of type {@link org.openspcoop2.core.api.Resource}
	 * 
	 * @param out OutputStream to serialize the object <var>resource</var>
	 * @param resource Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Resource resource,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Resource.class, resource, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>resource</var> of type {@link org.openspcoop2.core.api.Resource}
	 * 
	 * @param resource Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Resource resource) throws SerializerException {
		return this.objToXml(Resource.class, resource, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>resource</var> of type {@link org.openspcoop2.core.api.Resource}
	 * 
	 * @param resource Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Resource resource,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Resource.class, resource, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>resource</var> of type {@link org.openspcoop2.core.api.Resource}
	 * 
	 * @param resource Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Resource resource) throws SerializerException {
		return this.objToXml(Resource.class, resource, false).toString();
	}
	/**
	 * Serialize to String the object <var>resource</var> of type {@link org.openspcoop2.core.api.Resource}
	 * 
	 * @param resource Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Resource resource,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Resource.class, resource, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: invocation
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>invocation</var> of type {@link org.openspcoop2.core.api.Invocation}
	 * 
	 * @param fileName Xml file to serialize the object <var>invocation</var>
	 * @param invocation Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Invocation invocation) throws SerializerException {
		this.objToXml(fileName, Invocation.class, invocation, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>invocation</var> of type {@link org.openspcoop2.core.api.Invocation}
	 * 
	 * @param fileName Xml file to serialize the object <var>invocation</var>
	 * @param invocation Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Invocation invocation,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Invocation.class, invocation, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>invocation</var> of type {@link org.openspcoop2.core.api.Invocation}
	 * 
	 * @param file Xml file to serialize the object <var>invocation</var>
	 * @param invocation Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Invocation invocation) throws SerializerException {
		this.objToXml(file, Invocation.class, invocation, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>invocation</var> of type {@link org.openspcoop2.core.api.Invocation}
	 * 
	 * @param file Xml file to serialize the object <var>invocation</var>
	 * @param invocation Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Invocation invocation,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Invocation.class, invocation, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>invocation</var> of type {@link org.openspcoop2.core.api.Invocation}
	 * 
	 * @param out OutputStream to serialize the object <var>invocation</var>
	 * @param invocation Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Invocation invocation) throws SerializerException {
		this.objToXml(out, Invocation.class, invocation, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>invocation</var> of type {@link org.openspcoop2.core.api.Invocation}
	 * 
	 * @param out OutputStream to serialize the object <var>invocation</var>
	 * @param invocation Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Invocation invocation,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Invocation.class, invocation, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>invocation</var> of type {@link org.openspcoop2.core.api.Invocation}
	 * 
	 * @param invocation Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Invocation invocation) throws SerializerException {
		return this.objToXml(Invocation.class, invocation, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>invocation</var> of type {@link org.openspcoop2.core.api.Invocation}
	 * 
	 * @param invocation Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Invocation invocation,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Invocation.class, invocation, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>invocation</var> of type {@link org.openspcoop2.core.api.Invocation}
	 * 
	 * @param invocation Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Invocation invocation) throws SerializerException {
		return this.objToXml(Invocation.class, invocation, false).toString();
	}
	/**
	 * Serialize to String the object <var>invocation</var> of type {@link org.openspcoop2.core.api.Invocation}
	 * 
	 * @param invocation Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Invocation invocation,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Invocation.class, invocation, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: url-parameters
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>urlParameters</var> of type {@link org.openspcoop2.core.api.UrlParameters}
	 * 
	 * @param fileName Xml file to serialize the object <var>urlParameters</var>
	 * @param urlParameters Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,UrlParameters urlParameters) throws SerializerException {
		this.objToXml(fileName, UrlParameters.class, urlParameters, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>urlParameters</var> of type {@link org.openspcoop2.core.api.UrlParameters}
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
	 * Serialize to file system in <var>file</var> the object <var>urlParameters</var> of type {@link org.openspcoop2.core.api.UrlParameters}
	 * 
	 * @param file Xml file to serialize the object <var>urlParameters</var>
	 * @param urlParameters Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,UrlParameters urlParameters) throws SerializerException {
		this.objToXml(file, UrlParameters.class, urlParameters, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>urlParameters</var> of type {@link org.openspcoop2.core.api.UrlParameters}
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
	 * Serialize to output stream <var>out</var> the object <var>urlParameters</var> of type {@link org.openspcoop2.core.api.UrlParameters}
	 * 
	 * @param out OutputStream to serialize the object <var>urlParameters</var>
	 * @param urlParameters Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,UrlParameters urlParameters) throws SerializerException {
		this.objToXml(out, UrlParameters.class, urlParameters, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>urlParameters</var> of type {@link org.openspcoop2.core.api.UrlParameters}
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
	 * Serialize to byte array the object <var>urlParameters</var> of type {@link org.openspcoop2.core.api.UrlParameters}
	 * 
	 * @param urlParameters Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(UrlParameters urlParameters) throws SerializerException {
		return this.objToXml(UrlParameters.class, urlParameters, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>urlParameters</var> of type {@link org.openspcoop2.core.api.UrlParameters}
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
	 * Serialize to String the object <var>urlParameters</var> of type {@link org.openspcoop2.core.api.UrlParameters}
	 * 
	 * @param urlParameters Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(UrlParameters urlParameters) throws SerializerException {
		return this.objToXml(UrlParameters.class, urlParameters, false).toString();
	}
	/**
	 * Serialize to String the object <var>urlParameters</var> of type {@link org.openspcoop2.core.api.UrlParameters}
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
	 Object: header-parameters
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>headerParameters</var> of type {@link org.openspcoop2.core.api.HeaderParameters}
	 * 
	 * @param fileName Xml file to serialize the object <var>headerParameters</var>
	 * @param headerParameters Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,HeaderParameters headerParameters) throws SerializerException {
		this.objToXml(fileName, HeaderParameters.class, headerParameters, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>headerParameters</var> of type {@link org.openspcoop2.core.api.HeaderParameters}
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
	 * Serialize to file system in <var>file</var> the object <var>headerParameters</var> of type {@link org.openspcoop2.core.api.HeaderParameters}
	 * 
	 * @param file Xml file to serialize the object <var>headerParameters</var>
	 * @param headerParameters Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,HeaderParameters headerParameters) throws SerializerException {
		this.objToXml(file, HeaderParameters.class, headerParameters, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>headerParameters</var> of type {@link org.openspcoop2.core.api.HeaderParameters}
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
	 * Serialize to output stream <var>out</var> the object <var>headerParameters</var> of type {@link org.openspcoop2.core.api.HeaderParameters}
	 * 
	 * @param out OutputStream to serialize the object <var>headerParameters</var>
	 * @param headerParameters Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,HeaderParameters headerParameters) throws SerializerException {
		this.objToXml(out, HeaderParameters.class, headerParameters, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>headerParameters</var> of type {@link org.openspcoop2.core.api.HeaderParameters}
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
	 * Serialize to byte array the object <var>headerParameters</var> of type {@link org.openspcoop2.core.api.HeaderParameters}
	 * 
	 * @param headerParameters Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(HeaderParameters headerParameters) throws SerializerException {
		return this.objToXml(HeaderParameters.class, headerParameters, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>headerParameters</var> of type {@link org.openspcoop2.core.api.HeaderParameters}
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
	 * Serialize to String the object <var>headerParameters</var> of type {@link org.openspcoop2.core.api.HeaderParameters}
	 * 
	 * @param headerParameters Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(HeaderParameters headerParameters) throws SerializerException {
		return this.objToXml(HeaderParameters.class, headerParameters, false).toString();
	}
	/**
	 * Serialize to String the object <var>headerParameters</var> of type {@link org.openspcoop2.core.api.HeaderParameters}
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
	 Object: header-parameter
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>headerParameter</var> of type {@link org.openspcoop2.core.api.HeaderParameter}
	 * 
	 * @param fileName Xml file to serialize the object <var>headerParameter</var>
	 * @param headerParameter Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,HeaderParameter headerParameter) throws SerializerException {
		this.objToXml(fileName, HeaderParameter.class, headerParameter, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>headerParameter</var> of type {@link org.openspcoop2.core.api.HeaderParameter}
	 * 
	 * @param fileName Xml file to serialize the object <var>headerParameter</var>
	 * @param headerParameter Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,HeaderParameter headerParameter,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, HeaderParameter.class, headerParameter, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>headerParameter</var> of type {@link org.openspcoop2.core.api.HeaderParameter}
	 * 
	 * @param file Xml file to serialize the object <var>headerParameter</var>
	 * @param headerParameter Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,HeaderParameter headerParameter) throws SerializerException {
		this.objToXml(file, HeaderParameter.class, headerParameter, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>headerParameter</var> of type {@link org.openspcoop2.core.api.HeaderParameter}
	 * 
	 * @param file Xml file to serialize the object <var>headerParameter</var>
	 * @param headerParameter Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,HeaderParameter headerParameter,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, HeaderParameter.class, headerParameter, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>headerParameter</var> of type {@link org.openspcoop2.core.api.HeaderParameter}
	 * 
	 * @param out OutputStream to serialize the object <var>headerParameter</var>
	 * @param headerParameter Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,HeaderParameter headerParameter) throws SerializerException {
		this.objToXml(out, HeaderParameter.class, headerParameter, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>headerParameter</var> of type {@link org.openspcoop2.core.api.HeaderParameter}
	 * 
	 * @param out OutputStream to serialize the object <var>headerParameter</var>
	 * @param headerParameter Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,HeaderParameter headerParameter,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, HeaderParameter.class, headerParameter, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>headerParameter</var> of type {@link org.openspcoop2.core.api.HeaderParameter}
	 * 
	 * @param headerParameter Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(HeaderParameter headerParameter) throws SerializerException {
		return this.objToXml(HeaderParameter.class, headerParameter, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>headerParameter</var> of type {@link org.openspcoop2.core.api.HeaderParameter}
	 * 
	 * @param headerParameter Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(HeaderParameter headerParameter,boolean prettyPrint) throws SerializerException {
		return this.objToXml(HeaderParameter.class, headerParameter, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>headerParameter</var> of type {@link org.openspcoop2.core.api.HeaderParameter}
	 * 
	 * @param headerParameter Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(HeaderParameter headerParameter) throws SerializerException {
		return this.objToXml(HeaderParameter.class, headerParameter, false).toString();
	}
	/**
	 * Serialize to String the object <var>headerParameter</var> of type {@link org.openspcoop2.core.api.HeaderParameter}
	 * 
	 * @param headerParameter Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(HeaderParameter headerParameter,boolean prettyPrint) throws SerializerException {
		return this.objToXml(HeaderParameter.class, headerParameter, prettyPrint).toString();
	}
	
	
	

}
