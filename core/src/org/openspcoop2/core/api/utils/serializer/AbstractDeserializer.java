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

import org.openspcoop2.generic_project.exception.DeserializerException;

import org.openspcoop2.core.api.UrlParameter;
import org.openspcoop2.core.api.Resource;
import org.openspcoop2.core.api.Invocation;
import org.openspcoop2.core.api.UrlParameters;
import org.openspcoop2.core.api.HeaderParameters;
import org.openspcoop2.core.api.HeaderParameter;

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
	 Object: url-parameter
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.api.UrlParameter}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.api.UrlParameter}
	 * @return Object type {@link org.openspcoop2.core.api.UrlParameter}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public UrlParameter readUrlParameter(String fileName) throws DeserializerException {
		return (UrlParameter) this.xmlToObj(fileName, UrlParameter.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.api.UrlParameter}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.api.UrlParameter}
	 * @return Object type {@link org.openspcoop2.core.api.UrlParameter}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public UrlParameter readUrlParameter(File file) throws DeserializerException {
		return (UrlParameter) this.xmlToObj(file, UrlParameter.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.api.UrlParameter}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.api.UrlParameter}
	 * @return Object type {@link org.openspcoop2.core.api.UrlParameter}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public UrlParameter readUrlParameter(InputStream in) throws DeserializerException {
		return (UrlParameter) this.xmlToObj(in, UrlParameter.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.api.UrlParameter}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.api.UrlParameter}
	 * @return Object type {@link org.openspcoop2.core.api.UrlParameter}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public UrlParameter readUrlParameter(byte[] in) throws DeserializerException {
		return (UrlParameter) this.xmlToObj(in, UrlParameter.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.api.UrlParameter}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.api.UrlParameter}
	 * @return Object type {@link org.openspcoop2.core.api.UrlParameter}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public UrlParameter readUrlParameterFromString(String in) throws DeserializerException {
		return (UrlParameter) this.xmlToObj(in.getBytes(), UrlParameter.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: resource
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.api.Resource}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.api.Resource}
	 * @return Object type {@link org.openspcoop2.core.api.Resource}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Resource readResource(String fileName) throws DeserializerException {
		return (Resource) this.xmlToObj(fileName, Resource.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.api.Resource}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.api.Resource}
	 * @return Object type {@link org.openspcoop2.core.api.Resource}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Resource readResource(File file) throws DeserializerException {
		return (Resource) this.xmlToObj(file, Resource.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.api.Resource}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.api.Resource}
	 * @return Object type {@link org.openspcoop2.core.api.Resource}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Resource readResource(InputStream in) throws DeserializerException {
		return (Resource) this.xmlToObj(in, Resource.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.api.Resource}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.api.Resource}
	 * @return Object type {@link org.openspcoop2.core.api.Resource}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Resource readResource(byte[] in) throws DeserializerException {
		return (Resource) this.xmlToObj(in, Resource.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.api.Resource}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.api.Resource}
	 * @return Object type {@link org.openspcoop2.core.api.Resource}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Resource readResourceFromString(String in) throws DeserializerException {
		return (Resource) this.xmlToObj(in.getBytes(), Resource.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: invocation
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.api.Invocation}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.api.Invocation}
	 * @return Object type {@link org.openspcoop2.core.api.Invocation}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Invocation readInvocation(String fileName) throws DeserializerException {
		return (Invocation) this.xmlToObj(fileName, Invocation.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.api.Invocation}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.api.Invocation}
	 * @return Object type {@link org.openspcoop2.core.api.Invocation}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Invocation readInvocation(File file) throws DeserializerException {
		return (Invocation) this.xmlToObj(file, Invocation.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.api.Invocation}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.api.Invocation}
	 * @return Object type {@link org.openspcoop2.core.api.Invocation}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Invocation readInvocation(InputStream in) throws DeserializerException {
		return (Invocation) this.xmlToObj(in, Invocation.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.api.Invocation}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.api.Invocation}
	 * @return Object type {@link org.openspcoop2.core.api.Invocation}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Invocation readInvocation(byte[] in) throws DeserializerException {
		return (Invocation) this.xmlToObj(in, Invocation.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.api.Invocation}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.api.Invocation}
	 * @return Object type {@link org.openspcoop2.core.api.Invocation}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Invocation readInvocationFromString(String in) throws DeserializerException {
		return (Invocation) this.xmlToObj(in.getBytes(), Invocation.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: url-parameters
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.api.UrlParameters}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.api.UrlParameters}
	 * @return Object type {@link org.openspcoop2.core.api.UrlParameters}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public UrlParameters readUrlParameters(String fileName) throws DeserializerException {
		return (UrlParameters) this.xmlToObj(fileName, UrlParameters.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.api.UrlParameters}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.api.UrlParameters}
	 * @return Object type {@link org.openspcoop2.core.api.UrlParameters}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public UrlParameters readUrlParameters(File file) throws DeserializerException {
		return (UrlParameters) this.xmlToObj(file, UrlParameters.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.api.UrlParameters}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.api.UrlParameters}
	 * @return Object type {@link org.openspcoop2.core.api.UrlParameters}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public UrlParameters readUrlParameters(InputStream in) throws DeserializerException {
		return (UrlParameters) this.xmlToObj(in, UrlParameters.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.api.UrlParameters}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.api.UrlParameters}
	 * @return Object type {@link org.openspcoop2.core.api.UrlParameters}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public UrlParameters readUrlParameters(byte[] in) throws DeserializerException {
		return (UrlParameters) this.xmlToObj(in, UrlParameters.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.api.UrlParameters}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.api.UrlParameters}
	 * @return Object type {@link org.openspcoop2.core.api.UrlParameters}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public UrlParameters readUrlParametersFromString(String in) throws DeserializerException {
		return (UrlParameters) this.xmlToObj(in.getBytes(), UrlParameters.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: header-parameters
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.api.HeaderParameters}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.api.HeaderParameters}
	 * @return Object type {@link org.openspcoop2.core.api.HeaderParameters}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public HeaderParameters readHeaderParameters(String fileName) throws DeserializerException {
		return (HeaderParameters) this.xmlToObj(fileName, HeaderParameters.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.api.HeaderParameters}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.api.HeaderParameters}
	 * @return Object type {@link org.openspcoop2.core.api.HeaderParameters}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public HeaderParameters readHeaderParameters(File file) throws DeserializerException {
		return (HeaderParameters) this.xmlToObj(file, HeaderParameters.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.api.HeaderParameters}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.api.HeaderParameters}
	 * @return Object type {@link org.openspcoop2.core.api.HeaderParameters}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public HeaderParameters readHeaderParameters(InputStream in) throws DeserializerException {
		return (HeaderParameters) this.xmlToObj(in, HeaderParameters.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.api.HeaderParameters}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.api.HeaderParameters}
	 * @return Object type {@link org.openspcoop2.core.api.HeaderParameters}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public HeaderParameters readHeaderParameters(byte[] in) throws DeserializerException {
		return (HeaderParameters) this.xmlToObj(in, HeaderParameters.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.api.HeaderParameters}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.api.HeaderParameters}
	 * @return Object type {@link org.openspcoop2.core.api.HeaderParameters}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public HeaderParameters readHeaderParametersFromString(String in) throws DeserializerException {
		return (HeaderParameters) this.xmlToObj(in.getBytes(), HeaderParameters.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: header-parameter
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.api.HeaderParameter}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.api.HeaderParameter}
	 * @return Object type {@link org.openspcoop2.core.api.HeaderParameter}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public HeaderParameter readHeaderParameter(String fileName) throws DeserializerException {
		return (HeaderParameter) this.xmlToObj(fileName, HeaderParameter.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.api.HeaderParameter}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.api.HeaderParameter}
	 * @return Object type {@link org.openspcoop2.core.api.HeaderParameter}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public HeaderParameter readHeaderParameter(File file) throws DeserializerException {
		return (HeaderParameter) this.xmlToObj(file, HeaderParameter.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.api.HeaderParameter}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.api.HeaderParameter}
	 * @return Object type {@link org.openspcoop2.core.api.HeaderParameter}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public HeaderParameter readHeaderParameter(InputStream in) throws DeserializerException {
		return (HeaderParameter) this.xmlToObj(in, HeaderParameter.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.api.HeaderParameter}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.api.HeaderParameter}
	 * @return Object type {@link org.openspcoop2.core.api.HeaderParameter}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public HeaderParameter readHeaderParameter(byte[] in) throws DeserializerException {
		return (HeaderParameter) this.xmlToObj(in, HeaderParameter.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.api.HeaderParameter}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.api.HeaderParameter}
	 * @return Object type {@link org.openspcoop2.core.api.HeaderParameter}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public HeaderParameter readHeaderParameterFromString(String in) throws DeserializerException {
		return (HeaderParameter) this.xmlToObj(in.getBytes(), HeaderParameter.class);
	}	
	
	
	

}
