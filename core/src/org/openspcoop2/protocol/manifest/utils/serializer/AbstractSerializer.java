/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
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
package org.openspcoop2.protocol.manifest.utils.serializer;

import org.openspcoop2.generic_project.exception.SerializerException;
import org.openspcoop2.utils.beans.WriteToSerializerType;
import org.openspcoop2.utils.xml.JaxbUtils;

import org.openspcoop2.protocol.manifest.SoapHeaderBypassMustUnderstandHeader;
import org.openspcoop2.protocol.manifest.Tipi;
import org.openspcoop2.protocol.manifest.Servizi;
import org.openspcoop2.protocol.manifest.Profilo;
import org.openspcoop2.protocol.manifest.Funzionalita;
import org.openspcoop2.protocol.manifest.WebEmptyContext;
import org.openspcoop2.protocol.manifest.SoapHeaderBypassMustUnderstand;
import org.openspcoop2.protocol.manifest.Openspcoop2;
import org.openspcoop2.protocol.manifest.Web;
import org.openspcoop2.protocol.manifest.RegistroServizi;
import org.openspcoop2.protocol.manifest.UrlMapping;
import org.openspcoop2.protocol.manifest.Binding;
import org.openspcoop2.protocol.manifest.Versioni;
import org.openspcoop2.protocol.manifest.Soggetti;

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
	 Object: soapHeaderBypassMustUnderstandHeader
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>soapHeaderBypassMustUnderstandHeader</var> of type {@link org.openspcoop2.protocol.manifest.SoapHeaderBypassMustUnderstandHeader}
	 * 
	 * @param fileName Xml file to serialize the object <var>soapHeaderBypassMustUnderstandHeader</var>
	 * @param soapHeaderBypassMustUnderstandHeader Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,SoapHeaderBypassMustUnderstandHeader soapHeaderBypassMustUnderstandHeader) throws SerializerException {
		this.objToXml(fileName, SoapHeaderBypassMustUnderstandHeader.class, soapHeaderBypassMustUnderstandHeader, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>soapHeaderBypassMustUnderstandHeader</var> of type {@link org.openspcoop2.protocol.manifest.SoapHeaderBypassMustUnderstandHeader}
	 * 
	 * @param fileName Xml file to serialize the object <var>soapHeaderBypassMustUnderstandHeader</var>
	 * @param soapHeaderBypassMustUnderstandHeader Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,SoapHeaderBypassMustUnderstandHeader soapHeaderBypassMustUnderstandHeader,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, SoapHeaderBypassMustUnderstandHeader.class, soapHeaderBypassMustUnderstandHeader, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>soapHeaderBypassMustUnderstandHeader</var> of type {@link org.openspcoop2.protocol.manifest.SoapHeaderBypassMustUnderstandHeader}
	 * 
	 * @param file Xml file to serialize the object <var>soapHeaderBypassMustUnderstandHeader</var>
	 * @param soapHeaderBypassMustUnderstandHeader Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,SoapHeaderBypassMustUnderstandHeader soapHeaderBypassMustUnderstandHeader) throws SerializerException {
		this.objToXml(file, SoapHeaderBypassMustUnderstandHeader.class, soapHeaderBypassMustUnderstandHeader, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>soapHeaderBypassMustUnderstandHeader</var> of type {@link org.openspcoop2.protocol.manifest.SoapHeaderBypassMustUnderstandHeader}
	 * 
	 * @param file Xml file to serialize the object <var>soapHeaderBypassMustUnderstandHeader</var>
	 * @param soapHeaderBypassMustUnderstandHeader Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,SoapHeaderBypassMustUnderstandHeader soapHeaderBypassMustUnderstandHeader,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, SoapHeaderBypassMustUnderstandHeader.class, soapHeaderBypassMustUnderstandHeader, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>soapHeaderBypassMustUnderstandHeader</var> of type {@link org.openspcoop2.protocol.manifest.SoapHeaderBypassMustUnderstandHeader}
	 * 
	 * @param out OutputStream to serialize the object <var>soapHeaderBypassMustUnderstandHeader</var>
	 * @param soapHeaderBypassMustUnderstandHeader Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,SoapHeaderBypassMustUnderstandHeader soapHeaderBypassMustUnderstandHeader) throws SerializerException {
		this.objToXml(out, SoapHeaderBypassMustUnderstandHeader.class, soapHeaderBypassMustUnderstandHeader, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>soapHeaderBypassMustUnderstandHeader</var> of type {@link org.openspcoop2.protocol.manifest.SoapHeaderBypassMustUnderstandHeader}
	 * 
	 * @param out OutputStream to serialize the object <var>soapHeaderBypassMustUnderstandHeader</var>
	 * @param soapHeaderBypassMustUnderstandHeader Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,SoapHeaderBypassMustUnderstandHeader soapHeaderBypassMustUnderstandHeader,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, SoapHeaderBypassMustUnderstandHeader.class, soapHeaderBypassMustUnderstandHeader, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>soapHeaderBypassMustUnderstandHeader</var> of type {@link org.openspcoop2.protocol.manifest.SoapHeaderBypassMustUnderstandHeader}
	 * 
	 * @param soapHeaderBypassMustUnderstandHeader Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(SoapHeaderBypassMustUnderstandHeader soapHeaderBypassMustUnderstandHeader) throws SerializerException {
		return this.objToXml(SoapHeaderBypassMustUnderstandHeader.class, soapHeaderBypassMustUnderstandHeader, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>soapHeaderBypassMustUnderstandHeader</var> of type {@link org.openspcoop2.protocol.manifest.SoapHeaderBypassMustUnderstandHeader}
	 * 
	 * @param soapHeaderBypassMustUnderstandHeader Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(SoapHeaderBypassMustUnderstandHeader soapHeaderBypassMustUnderstandHeader,boolean prettyPrint) throws SerializerException {
		return this.objToXml(SoapHeaderBypassMustUnderstandHeader.class, soapHeaderBypassMustUnderstandHeader, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>soapHeaderBypassMustUnderstandHeader</var> of type {@link org.openspcoop2.protocol.manifest.SoapHeaderBypassMustUnderstandHeader}
	 * 
	 * @param soapHeaderBypassMustUnderstandHeader Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(SoapHeaderBypassMustUnderstandHeader soapHeaderBypassMustUnderstandHeader) throws SerializerException {
		return this.objToXml(SoapHeaderBypassMustUnderstandHeader.class, soapHeaderBypassMustUnderstandHeader, false).toString();
	}
	/**
	 * Serialize to String the object <var>soapHeaderBypassMustUnderstandHeader</var> of type {@link org.openspcoop2.protocol.manifest.SoapHeaderBypassMustUnderstandHeader}
	 * 
	 * @param soapHeaderBypassMustUnderstandHeader Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(SoapHeaderBypassMustUnderstandHeader soapHeaderBypassMustUnderstandHeader,boolean prettyPrint) throws SerializerException {
		return this.objToXml(SoapHeaderBypassMustUnderstandHeader.class, soapHeaderBypassMustUnderstandHeader, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: tipi
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>tipi</var> of type {@link org.openspcoop2.protocol.manifest.Tipi}
	 * 
	 * @param fileName Xml file to serialize the object <var>tipi</var>
	 * @param tipi Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Tipi tipi) throws SerializerException {
		this.objToXml(fileName, Tipi.class, tipi, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>tipi</var> of type {@link org.openspcoop2.protocol.manifest.Tipi}
	 * 
	 * @param fileName Xml file to serialize the object <var>tipi</var>
	 * @param tipi Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Tipi tipi,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Tipi.class, tipi, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>tipi</var> of type {@link org.openspcoop2.protocol.manifest.Tipi}
	 * 
	 * @param file Xml file to serialize the object <var>tipi</var>
	 * @param tipi Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Tipi tipi) throws SerializerException {
		this.objToXml(file, Tipi.class, tipi, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>tipi</var> of type {@link org.openspcoop2.protocol.manifest.Tipi}
	 * 
	 * @param file Xml file to serialize the object <var>tipi</var>
	 * @param tipi Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Tipi tipi,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Tipi.class, tipi, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>tipi</var> of type {@link org.openspcoop2.protocol.manifest.Tipi}
	 * 
	 * @param out OutputStream to serialize the object <var>tipi</var>
	 * @param tipi Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Tipi tipi) throws SerializerException {
		this.objToXml(out, Tipi.class, tipi, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>tipi</var> of type {@link org.openspcoop2.protocol.manifest.Tipi}
	 * 
	 * @param out OutputStream to serialize the object <var>tipi</var>
	 * @param tipi Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Tipi tipi,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Tipi.class, tipi, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>tipi</var> of type {@link org.openspcoop2.protocol.manifest.Tipi}
	 * 
	 * @param tipi Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Tipi tipi) throws SerializerException {
		return this.objToXml(Tipi.class, tipi, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>tipi</var> of type {@link org.openspcoop2.protocol.manifest.Tipi}
	 * 
	 * @param tipi Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Tipi tipi,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Tipi.class, tipi, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>tipi</var> of type {@link org.openspcoop2.protocol.manifest.Tipi}
	 * 
	 * @param tipi Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Tipi tipi) throws SerializerException {
		return this.objToXml(Tipi.class, tipi, false).toString();
	}
	/**
	 * Serialize to String the object <var>tipi</var> of type {@link org.openspcoop2.protocol.manifest.Tipi}
	 * 
	 * @param tipi Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Tipi tipi,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Tipi.class, tipi, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: servizi
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>servizi</var> of type {@link org.openspcoop2.protocol.manifest.Servizi}
	 * 
	 * @param fileName Xml file to serialize the object <var>servizi</var>
	 * @param servizi Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Servizi servizi) throws SerializerException {
		this.objToXml(fileName, Servizi.class, servizi, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>servizi</var> of type {@link org.openspcoop2.protocol.manifest.Servizi}
	 * 
	 * @param fileName Xml file to serialize the object <var>servizi</var>
	 * @param servizi Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Servizi servizi,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Servizi.class, servizi, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>servizi</var> of type {@link org.openspcoop2.protocol.manifest.Servizi}
	 * 
	 * @param file Xml file to serialize the object <var>servizi</var>
	 * @param servizi Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Servizi servizi) throws SerializerException {
		this.objToXml(file, Servizi.class, servizi, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>servizi</var> of type {@link org.openspcoop2.protocol.manifest.Servizi}
	 * 
	 * @param file Xml file to serialize the object <var>servizi</var>
	 * @param servizi Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Servizi servizi,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Servizi.class, servizi, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>servizi</var> of type {@link org.openspcoop2.protocol.manifest.Servizi}
	 * 
	 * @param out OutputStream to serialize the object <var>servizi</var>
	 * @param servizi Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Servizi servizi) throws SerializerException {
		this.objToXml(out, Servizi.class, servizi, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>servizi</var> of type {@link org.openspcoop2.protocol.manifest.Servizi}
	 * 
	 * @param out OutputStream to serialize the object <var>servizi</var>
	 * @param servizi Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Servizi servizi,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Servizi.class, servizi, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>servizi</var> of type {@link org.openspcoop2.protocol.manifest.Servizi}
	 * 
	 * @param servizi Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Servizi servizi) throws SerializerException {
		return this.objToXml(Servizi.class, servizi, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>servizi</var> of type {@link org.openspcoop2.protocol.manifest.Servizi}
	 * 
	 * @param servizi Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Servizi servizi,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Servizi.class, servizi, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>servizi</var> of type {@link org.openspcoop2.protocol.manifest.Servizi}
	 * 
	 * @param servizi Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Servizi servizi) throws SerializerException {
		return this.objToXml(Servizi.class, servizi, false).toString();
	}
	/**
	 * Serialize to String the object <var>servizi</var> of type {@link org.openspcoop2.protocol.manifest.Servizi}
	 * 
	 * @param servizi Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Servizi servizi,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Servizi.class, servizi, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: profilo
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>profilo</var> of type {@link org.openspcoop2.protocol.manifest.Profilo}
	 * 
	 * @param fileName Xml file to serialize the object <var>profilo</var>
	 * @param profilo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Profilo profilo) throws SerializerException {
		this.objToXml(fileName, Profilo.class, profilo, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>profilo</var> of type {@link org.openspcoop2.protocol.manifest.Profilo}
	 * 
	 * @param fileName Xml file to serialize the object <var>profilo</var>
	 * @param profilo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Profilo profilo,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Profilo.class, profilo, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>profilo</var> of type {@link org.openspcoop2.protocol.manifest.Profilo}
	 * 
	 * @param file Xml file to serialize the object <var>profilo</var>
	 * @param profilo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Profilo profilo) throws SerializerException {
		this.objToXml(file, Profilo.class, profilo, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>profilo</var> of type {@link org.openspcoop2.protocol.manifest.Profilo}
	 * 
	 * @param file Xml file to serialize the object <var>profilo</var>
	 * @param profilo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Profilo profilo,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Profilo.class, profilo, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>profilo</var> of type {@link org.openspcoop2.protocol.manifest.Profilo}
	 * 
	 * @param out OutputStream to serialize the object <var>profilo</var>
	 * @param profilo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Profilo profilo) throws SerializerException {
		this.objToXml(out, Profilo.class, profilo, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>profilo</var> of type {@link org.openspcoop2.protocol.manifest.Profilo}
	 * 
	 * @param out OutputStream to serialize the object <var>profilo</var>
	 * @param profilo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Profilo profilo,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Profilo.class, profilo, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>profilo</var> of type {@link org.openspcoop2.protocol.manifest.Profilo}
	 * 
	 * @param profilo Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Profilo profilo) throws SerializerException {
		return this.objToXml(Profilo.class, profilo, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>profilo</var> of type {@link org.openspcoop2.protocol.manifest.Profilo}
	 * 
	 * @param profilo Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Profilo profilo,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Profilo.class, profilo, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>profilo</var> of type {@link org.openspcoop2.protocol.manifest.Profilo}
	 * 
	 * @param profilo Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Profilo profilo) throws SerializerException {
		return this.objToXml(Profilo.class, profilo, false).toString();
	}
	/**
	 * Serialize to String the object <var>profilo</var> of type {@link org.openspcoop2.protocol.manifest.Profilo}
	 * 
	 * @param profilo Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Profilo profilo,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Profilo.class, profilo, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: funzionalita
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>funzionalita</var> of type {@link org.openspcoop2.protocol.manifest.Funzionalita}
	 * 
	 * @param fileName Xml file to serialize the object <var>funzionalita</var>
	 * @param funzionalita Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Funzionalita funzionalita) throws SerializerException {
		this.objToXml(fileName, Funzionalita.class, funzionalita, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>funzionalita</var> of type {@link org.openspcoop2.protocol.manifest.Funzionalita}
	 * 
	 * @param fileName Xml file to serialize the object <var>funzionalita</var>
	 * @param funzionalita Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Funzionalita funzionalita,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Funzionalita.class, funzionalita, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>funzionalita</var> of type {@link org.openspcoop2.protocol.manifest.Funzionalita}
	 * 
	 * @param file Xml file to serialize the object <var>funzionalita</var>
	 * @param funzionalita Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Funzionalita funzionalita) throws SerializerException {
		this.objToXml(file, Funzionalita.class, funzionalita, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>funzionalita</var> of type {@link org.openspcoop2.protocol.manifest.Funzionalita}
	 * 
	 * @param file Xml file to serialize the object <var>funzionalita</var>
	 * @param funzionalita Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Funzionalita funzionalita,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Funzionalita.class, funzionalita, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>funzionalita</var> of type {@link org.openspcoop2.protocol.manifest.Funzionalita}
	 * 
	 * @param out OutputStream to serialize the object <var>funzionalita</var>
	 * @param funzionalita Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Funzionalita funzionalita) throws SerializerException {
		this.objToXml(out, Funzionalita.class, funzionalita, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>funzionalita</var> of type {@link org.openspcoop2.protocol.manifest.Funzionalita}
	 * 
	 * @param out OutputStream to serialize the object <var>funzionalita</var>
	 * @param funzionalita Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Funzionalita funzionalita,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Funzionalita.class, funzionalita, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>funzionalita</var> of type {@link org.openspcoop2.protocol.manifest.Funzionalita}
	 * 
	 * @param funzionalita Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Funzionalita funzionalita) throws SerializerException {
		return this.objToXml(Funzionalita.class, funzionalita, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>funzionalita</var> of type {@link org.openspcoop2.protocol.manifest.Funzionalita}
	 * 
	 * @param funzionalita Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Funzionalita funzionalita,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Funzionalita.class, funzionalita, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>funzionalita</var> of type {@link org.openspcoop2.protocol.manifest.Funzionalita}
	 * 
	 * @param funzionalita Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Funzionalita funzionalita) throws SerializerException {
		return this.objToXml(Funzionalita.class, funzionalita, false).toString();
	}
	/**
	 * Serialize to String the object <var>funzionalita</var> of type {@link org.openspcoop2.protocol.manifest.Funzionalita}
	 * 
	 * @param funzionalita Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Funzionalita funzionalita,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Funzionalita.class, funzionalita, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: webEmptyContext
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>webEmptyContext</var> of type {@link org.openspcoop2.protocol.manifest.WebEmptyContext}
	 * 
	 * @param fileName Xml file to serialize the object <var>webEmptyContext</var>
	 * @param webEmptyContext Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,WebEmptyContext webEmptyContext) throws SerializerException {
		this.objToXml(fileName, WebEmptyContext.class, webEmptyContext, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>webEmptyContext</var> of type {@link org.openspcoop2.protocol.manifest.WebEmptyContext}
	 * 
	 * @param fileName Xml file to serialize the object <var>webEmptyContext</var>
	 * @param webEmptyContext Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,WebEmptyContext webEmptyContext,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, WebEmptyContext.class, webEmptyContext, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>webEmptyContext</var> of type {@link org.openspcoop2.protocol.manifest.WebEmptyContext}
	 * 
	 * @param file Xml file to serialize the object <var>webEmptyContext</var>
	 * @param webEmptyContext Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,WebEmptyContext webEmptyContext) throws SerializerException {
		this.objToXml(file, WebEmptyContext.class, webEmptyContext, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>webEmptyContext</var> of type {@link org.openspcoop2.protocol.manifest.WebEmptyContext}
	 * 
	 * @param file Xml file to serialize the object <var>webEmptyContext</var>
	 * @param webEmptyContext Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,WebEmptyContext webEmptyContext,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, WebEmptyContext.class, webEmptyContext, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>webEmptyContext</var> of type {@link org.openspcoop2.protocol.manifest.WebEmptyContext}
	 * 
	 * @param out OutputStream to serialize the object <var>webEmptyContext</var>
	 * @param webEmptyContext Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,WebEmptyContext webEmptyContext) throws SerializerException {
		this.objToXml(out, WebEmptyContext.class, webEmptyContext, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>webEmptyContext</var> of type {@link org.openspcoop2.protocol.manifest.WebEmptyContext}
	 * 
	 * @param out OutputStream to serialize the object <var>webEmptyContext</var>
	 * @param webEmptyContext Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,WebEmptyContext webEmptyContext,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, WebEmptyContext.class, webEmptyContext, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>webEmptyContext</var> of type {@link org.openspcoop2.protocol.manifest.WebEmptyContext}
	 * 
	 * @param webEmptyContext Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(WebEmptyContext webEmptyContext) throws SerializerException {
		return this.objToXml(WebEmptyContext.class, webEmptyContext, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>webEmptyContext</var> of type {@link org.openspcoop2.protocol.manifest.WebEmptyContext}
	 * 
	 * @param webEmptyContext Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(WebEmptyContext webEmptyContext,boolean prettyPrint) throws SerializerException {
		return this.objToXml(WebEmptyContext.class, webEmptyContext, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>webEmptyContext</var> of type {@link org.openspcoop2.protocol.manifest.WebEmptyContext}
	 * 
	 * @param webEmptyContext Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(WebEmptyContext webEmptyContext) throws SerializerException {
		return this.objToXml(WebEmptyContext.class, webEmptyContext, false).toString();
	}
	/**
	 * Serialize to String the object <var>webEmptyContext</var> of type {@link org.openspcoop2.protocol.manifest.WebEmptyContext}
	 * 
	 * @param webEmptyContext Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(WebEmptyContext webEmptyContext,boolean prettyPrint) throws SerializerException {
		return this.objToXml(WebEmptyContext.class, webEmptyContext, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: soapHeaderBypassMustUnderstand
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>soapHeaderBypassMustUnderstand</var> of type {@link org.openspcoop2.protocol.manifest.SoapHeaderBypassMustUnderstand}
	 * 
	 * @param fileName Xml file to serialize the object <var>soapHeaderBypassMustUnderstand</var>
	 * @param soapHeaderBypassMustUnderstand Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,SoapHeaderBypassMustUnderstand soapHeaderBypassMustUnderstand) throws SerializerException {
		this.objToXml(fileName, SoapHeaderBypassMustUnderstand.class, soapHeaderBypassMustUnderstand, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>soapHeaderBypassMustUnderstand</var> of type {@link org.openspcoop2.protocol.manifest.SoapHeaderBypassMustUnderstand}
	 * 
	 * @param fileName Xml file to serialize the object <var>soapHeaderBypassMustUnderstand</var>
	 * @param soapHeaderBypassMustUnderstand Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,SoapHeaderBypassMustUnderstand soapHeaderBypassMustUnderstand,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, SoapHeaderBypassMustUnderstand.class, soapHeaderBypassMustUnderstand, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>soapHeaderBypassMustUnderstand</var> of type {@link org.openspcoop2.protocol.manifest.SoapHeaderBypassMustUnderstand}
	 * 
	 * @param file Xml file to serialize the object <var>soapHeaderBypassMustUnderstand</var>
	 * @param soapHeaderBypassMustUnderstand Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,SoapHeaderBypassMustUnderstand soapHeaderBypassMustUnderstand) throws SerializerException {
		this.objToXml(file, SoapHeaderBypassMustUnderstand.class, soapHeaderBypassMustUnderstand, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>soapHeaderBypassMustUnderstand</var> of type {@link org.openspcoop2.protocol.manifest.SoapHeaderBypassMustUnderstand}
	 * 
	 * @param file Xml file to serialize the object <var>soapHeaderBypassMustUnderstand</var>
	 * @param soapHeaderBypassMustUnderstand Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,SoapHeaderBypassMustUnderstand soapHeaderBypassMustUnderstand,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, SoapHeaderBypassMustUnderstand.class, soapHeaderBypassMustUnderstand, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>soapHeaderBypassMustUnderstand</var> of type {@link org.openspcoop2.protocol.manifest.SoapHeaderBypassMustUnderstand}
	 * 
	 * @param out OutputStream to serialize the object <var>soapHeaderBypassMustUnderstand</var>
	 * @param soapHeaderBypassMustUnderstand Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,SoapHeaderBypassMustUnderstand soapHeaderBypassMustUnderstand) throws SerializerException {
		this.objToXml(out, SoapHeaderBypassMustUnderstand.class, soapHeaderBypassMustUnderstand, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>soapHeaderBypassMustUnderstand</var> of type {@link org.openspcoop2.protocol.manifest.SoapHeaderBypassMustUnderstand}
	 * 
	 * @param out OutputStream to serialize the object <var>soapHeaderBypassMustUnderstand</var>
	 * @param soapHeaderBypassMustUnderstand Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,SoapHeaderBypassMustUnderstand soapHeaderBypassMustUnderstand,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, SoapHeaderBypassMustUnderstand.class, soapHeaderBypassMustUnderstand, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>soapHeaderBypassMustUnderstand</var> of type {@link org.openspcoop2.protocol.manifest.SoapHeaderBypassMustUnderstand}
	 * 
	 * @param soapHeaderBypassMustUnderstand Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(SoapHeaderBypassMustUnderstand soapHeaderBypassMustUnderstand) throws SerializerException {
		return this.objToXml(SoapHeaderBypassMustUnderstand.class, soapHeaderBypassMustUnderstand, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>soapHeaderBypassMustUnderstand</var> of type {@link org.openspcoop2.protocol.manifest.SoapHeaderBypassMustUnderstand}
	 * 
	 * @param soapHeaderBypassMustUnderstand Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(SoapHeaderBypassMustUnderstand soapHeaderBypassMustUnderstand,boolean prettyPrint) throws SerializerException {
		return this.objToXml(SoapHeaderBypassMustUnderstand.class, soapHeaderBypassMustUnderstand, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>soapHeaderBypassMustUnderstand</var> of type {@link org.openspcoop2.protocol.manifest.SoapHeaderBypassMustUnderstand}
	 * 
	 * @param soapHeaderBypassMustUnderstand Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(SoapHeaderBypassMustUnderstand soapHeaderBypassMustUnderstand) throws SerializerException {
		return this.objToXml(SoapHeaderBypassMustUnderstand.class, soapHeaderBypassMustUnderstand, false).toString();
	}
	/**
	 * Serialize to String the object <var>soapHeaderBypassMustUnderstand</var> of type {@link org.openspcoop2.protocol.manifest.SoapHeaderBypassMustUnderstand}
	 * 
	 * @param soapHeaderBypassMustUnderstand Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(SoapHeaderBypassMustUnderstand soapHeaderBypassMustUnderstand,boolean prettyPrint) throws SerializerException {
		return this.objToXml(SoapHeaderBypassMustUnderstand.class, soapHeaderBypassMustUnderstand, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: openspcoop2
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>openspcoop2</var> of type {@link org.openspcoop2.protocol.manifest.Openspcoop2}
	 * 
	 * @param fileName Xml file to serialize the object <var>openspcoop2</var>
	 * @param openspcoop2 Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Openspcoop2 openspcoop2) throws SerializerException {
		this.objToXml(fileName, Openspcoop2.class, openspcoop2, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>openspcoop2</var> of type {@link org.openspcoop2.protocol.manifest.Openspcoop2}
	 * 
	 * @param fileName Xml file to serialize the object <var>openspcoop2</var>
	 * @param openspcoop2 Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Openspcoop2 openspcoop2,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Openspcoop2.class, openspcoop2, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>openspcoop2</var> of type {@link org.openspcoop2.protocol.manifest.Openspcoop2}
	 * 
	 * @param file Xml file to serialize the object <var>openspcoop2</var>
	 * @param openspcoop2 Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Openspcoop2 openspcoop2) throws SerializerException {
		this.objToXml(file, Openspcoop2.class, openspcoop2, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>openspcoop2</var> of type {@link org.openspcoop2.protocol.manifest.Openspcoop2}
	 * 
	 * @param file Xml file to serialize the object <var>openspcoop2</var>
	 * @param openspcoop2 Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Openspcoop2 openspcoop2,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Openspcoop2.class, openspcoop2, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>openspcoop2</var> of type {@link org.openspcoop2.protocol.manifest.Openspcoop2}
	 * 
	 * @param out OutputStream to serialize the object <var>openspcoop2</var>
	 * @param openspcoop2 Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Openspcoop2 openspcoop2) throws SerializerException {
		this.objToXml(out, Openspcoop2.class, openspcoop2, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>openspcoop2</var> of type {@link org.openspcoop2.protocol.manifest.Openspcoop2}
	 * 
	 * @param out OutputStream to serialize the object <var>openspcoop2</var>
	 * @param openspcoop2 Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Openspcoop2 openspcoop2,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Openspcoop2.class, openspcoop2, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>openspcoop2</var> of type {@link org.openspcoop2.protocol.manifest.Openspcoop2}
	 * 
	 * @param openspcoop2 Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Openspcoop2 openspcoop2) throws SerializerException {
		return this.objToXml(Openspcoop2.class, openspcoop2, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>openspcoop2</var> of type {@link org.openspcoop2.protocol.manifest.Openspcoop2}
	 * 
	 * @param openspcoop2 Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Openspcoop2 openspcoop2,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Openspcoop2.class, openspcoop2, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>openspcoop2</var> of type {@link org.openspcoop2.protocol.manifest.Openspcoop2}
	 * 
	 * @param openspcoop2 Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Openspcoop2 openspcoop2) throws SerializerException {
		return this.objToXml(Openspcoop2.class, openspcoop2, false).toString();
	}
	/**
	 * Serialize to String the object <var>openspcoop2</var> of type {@link org.openspcoop2.protocol.manifest.Openspcoop2}
	 * 
	 * @param openspcoop2 Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Openspcoop2 openspcoop2,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Openspcoop2.class, openspcoop2, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: web
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>web</var> of type {@link org.openspcoop2.protocol.manifest.Web}
	 * 
	 * @param fileName Xml file to serialize the object <var>web</var>
	 * @param web Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Web web) throws SerializerException {
		this.objToXml(fileName, Web.class, web, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>web</var> of type {@link org.openspcoop2.protocol.manifest.Web}
	 * 
	 * @param fileName Xml file to serialize the object <var>web</var>
	 * @param web Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Web web,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Web.class, web, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>web</var> of type {@link org.openspcoop2.protocol.manifest.Web}
	 * 
	 * @param file Xml file to serialize the object <var>web</var>
	 * @param web Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Web web) throws SerializerException {
		this.objToXml(file, Web.class, web, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>web</var> of type {@link org.openspcoop2.protocol.manifest.Web}
	 * 
	 * @param file Xml file to serialize the object <var>web</var>
	 * @param web Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Web web,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Web.class, web, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>web</var> of type {@link org.openspcoop2.protocol.manifest.Web}
	 * 
	 * @param out OutputStream to serialize the object <var>web</var>
	 * @param web Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Web web) throws SerializerException {
		this.objToXml(out, Web.class, web, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>web</var> of type {@link org.openspcoop2.protocol.manifest.Web}
	 * 
	 * @param out OutputStream to serialize the object <var>web</var>
	 * @param web Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Web web,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Web.class, web, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>web</var> of type {@link org.openspcoop2.protocol.manifest.Web}
	 * 
	 * @param web Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Web web) throws SerializerException {
		return this.objToXml(Web.class, web, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>web</var> of type {@link org.openspcoop2.protocol.manifest.Web}
	 * 
	 * @param web Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Web web,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Web.class, web, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>web</var> of type {@link org.openspcoop2.protocol.manifest.Web}
	 * 
	 * @param web Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Web web) throws SerializerException {
		return this.objToXml(Web.class, web, false).toString();
	}
	/**
	 * Serialize to String the object <var>web</var> of type {@link org.openspcoop2.protocol.manifest.Web}
	 * 
	 * @param web Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Web web,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Web.class, web, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: registroServizi
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>registroServizi</var> of type {@link org.openspcoop2.protocol.manifest.RegistroServizi}
	 * 
	 * @param fileName Xml file to serialize the object <var>registroServizi</var>
	 * @param registroServizi Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,RegistroServizi registroServizi) throws SerializerException {
		this.objToXml(fileName, RegistroServizi.class, registroServizi, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>registroServizi</var> of type {@link org.openspcoop2.protocol.manifest.RegistroServizi}
	 * 
	 * @param fileName Xml file to serialize the object <var>registroServizi</var>
	 * @param registroServizi Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,RegistroServizi registroServizi,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, RegistroServizi.class, registroServizi, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>registroServizi</var> of type {@link org.openspcoop2.protocol.manifest.RegistroServizi}
	 * 
	 * @param file Xml file to serialize the object <var>registroServizi</var>
	 * @param registroServizi Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,RegistroServizi registroServizi) throws SerializerException {
		this.objToXml(file, RegistroServizi.class, registroServizi, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>registroServizi</var> of type {@link org.openspcoop2.protocol.manifest.RegistroServizi}
	 * 
	 * @param file Xml file to serialize the object <var>registroServizi</var>
	 * @param registroServizi Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,RegistroServizi registroServizi,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, RegistroServizi.class, registroServizi, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>registroServizi</var> of type {@link org.openspcoop2.protocol.manifest.RegistroServizi}
	 * 
	 * @param out OutputStream to serialize the object <var>registroServizi</var>
	 * @param registroServizi Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,RegistroServizi registroServizi) throws SerializerException {
		this.objToXml(out, RegistroServizi.class, registroServizi, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>registroServizi</var> of type {@link org.openspcoop2.protocol.manifest.RegistroServizi}
	 * 
	 * @param out OutputStream to serialize the object <var>registroServizi</var>
	 * @param registroServizi Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,RegistroServizi registroServizi,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, RegistroServizi.class, registroServizi, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>registroServizi</var> of type {@link org.openspcoop2.protocol.manifest.RegistroServizi}
	 * 
	 * @param registroServizi Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(RegistroServizi registroServizi) throws SerializerException {
		return this.objToXml(RegistroServizi.class, registroServizi, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>registroServizi</var> of type {@link org.openspcoop2.protocol.manifest.RegistroServizi}
	 * 
	 * @param registroServizi Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(RegistroServizi registroServizi,boolean prettyPrint) throws SerializerException {
		return this.objToXml(RegistroServizi.class, registroServizi, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>registroServizi</var> of type {@link org.openspcoop2.protocol.manifest.RegistroServizi}
	 * 
	 * @param registroServizi Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(RegistroServizi registroServizi) throws SerializerException {
		return this.objToXml(RegistroServizi.class, registroServizi, false).toString();
	}
	/**
	 * Serialize to String the object <var>registroServizi</var> of type {@link org.openspcoop2.protocol.manifest.RegistroServizi}
	 * 
	 * @param registroServizi Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(RegistroServizi registroServizi,boolean prettyPrint) throws SerializerException {
		return this.objToXml(RegistroServizi.class, registroServizi, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: urlMapping
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>urlMapping</var> of type {@link org.openspcoop2.protocol.manifest.UrlMapping}
	 * 
	 * @param fileName Xml file to serialize the object <var>urlMapping</var>
	 * @param urlMapping Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,UrlMapping urlMapping) throws SerializerException {
		this.objToXml(fileName, UrlMapping.class, urlMapping, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>urlMapping</var> of type {@link org.openspcoop2.protocol.manifest.UrlMapping}
	 * 
	 * @param fileName Xml file to serialize the object <var>urlMapping</var>
	 * @param urlMapping Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,UrlMapping urlMapping,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, UrlMapping.class, urlMapping, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>urlMapping</var> of type {@link org.openspcoop2.protocol.manifest.UrlMapping}
	 * 
	 * @param file Xml file to serialize the object <var>urlMapping</var>
	 * @param urlMapping Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,UrlMapping urlMapping) throws SerializerException {
		this.objToXml(file, UrlMapping.class, urlMapping, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>urlMapping</var> of type {@link org.openspcoop2.protocol.manifest.UrlMapping}
	 * 
	 * @param file Xml file to serialize the object <var>urlMapping</var>
	 * @param urlMapping Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,UrlMapping urlMapping,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, UrlMapping.class, urlMapping, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>urlMapping</var> of type {@link org.openspcoop2.protocol.manifest.UrlMapping}
	 * 
	 * @param out OutputStream to serialize the object <var>urlMapping</var>
	 * @param urlMapping Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,UrlMapping urlMapping) throws SerializerException {
		this.objToXml(out, UrlMapping.class, urlMapping, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>urlMapping</var> of type {@link org.openspcoop2.protocol.manifest.UrlMapping}
	 * 
	 * @param out OutputStream to serialize the object <var>urlMapping</var>
	 * @param urlMapping Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,UrlMapping urlMapping,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, UrlMapping.class, urlMapping, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>urlMapping</var> of type {@link org.openspcoop2.protocol.manifest.UrlMapping}
	 * 
	 * @param urlMapping Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(UrlMapping urlMapping) throws SerializerException {
		return this.objToXml(UrlMapping.class, urlMapping, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>urlMapping</var> of type {@link org.openspcoop2.protocol.manifest.UrlMapping}
	 * 
	 * @param urlMapping Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(UrlMapping urlMapping,boolean prettyPrint) throws SerializerException {
		return this.objToXml(UrlMapping.class, urlMapping, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>urlMapping</var> of type {@link org.openspcoop2.protocol.manifest.UrlMapping}
	 * 
	 * @param urlMapping Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(UrlMapping urlMapping) throws SerializerException {
		return this.objToXml(UrlMapping.class, urlMapping, false).toString();
	}
	/**
	 * Serialize to String the object <var>urlMapping</var> of type {@link org.openspcoop2.protocol.manifest.UrlMapping}
	 * 
	 * @param urlMapping Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(UrlMapping urlMapping,boolean prettyPrint) throws SerializerException {
		return this.objToXml(UrlMapping.class, urlMapping, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: binding
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>binding</var> of type {@link org.openspcoop2.protocol.manifest.Binding}
	 * 
	 * @param fileName Xml file to serialize the object <var>binding</var>
	 * @param binding Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Binding binding) throws SerializerException {
		this.objToXml(fileName, Binding.class, binding, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>binding</var> of type {@link org.openspcoop2.protocol.manifest.Binding}
	 * 
	 * @param fileName Xml file to serialize the object <var>binding</var>
	 * @param binding Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Binding binding,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Binding.class, binding, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>binding</var> of type {@link org.openspcoop2.protocol.manifest.Binding}
	 * 
	 * @param file Xml file to serialize the object <var>binding</var>
	 * @param binding Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Binding binding) throws SerializerException {
		this.objToXml(file, Binding.class, binding, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>binding</var> of type {@link org.openspcoop2.protocol.manifest.Binding}
	 * 
	 * @param file Xml file to serialize the object <var>binding</var>
	 * @param binding Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Binding binding,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Binding.class, binding, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>binding</var> of type {@link org.openspcoop2.protocol.manifest.Binding}
	 * 
	 * @param out OutputStream to serialize the object <var>binding</var>
	 * @param binding Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Binding binding) throws SerializerException {
		this.objToXml(out, Binding.class, binding, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>binding</var> of type {@link org.openspcoop2.protocol.manifest.Binding}
	 * 
	 * @param out OutputStream to serialize the object <var>binding</var>
	 * @param binding Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Binding binding,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Binding.class, binding, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>binding</var> of type {@link org.openspcoop2.protocol.manifest.Binding}
	 * 
	 * @param binding Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Binding binding) throws SerializerException {
		return this.objToXml(Binding.class, binding, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>binding</var> of type {@link org.openspcoop2.protocol.manifest.Binding}
	 * 
	 * @param binding Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Binding binding,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Binding.class, binding, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>binding</var> of type {@link org.openspcoop2.protocol.manifest.Binding}
	 * 
	 * @param binding Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Binding binding) throws SerializerException {
		return this.objToXml(Binding.class, binding, false).toString();
	}
	/**
	 * Serialize to String the object <var>binding</var> of type {@link org.openspcoop2.protocol.manifest.Binding}
	 * 
	 * @param binding Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Binding binding,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Binding.class, binding, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: versioni
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>versioni</var> of type {@link org.openspcoop2.protocol.manifest.Versioni}
	 * 
	 * @param fileName Xml file to serialize the object <var>versioni</var>
	 * @param versioni Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Versioni versioni) throws SerializerException {
		this.objToXml(fileName, Versioni.class, versioni, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>versioni</var> of type {@link org.openspcoop2.protocol.manifest.Versioni}
	 * 
	 * @param fileName Xml file to serialize the object <var>versioni</var>
	 * @param versioni Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Versioni versioni,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Versioni.class, versioni, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>versioni</var> of type {@link org.openspcoop2.protocol.manifest.Versioni}
	 * 
	 * @param file Xml file to serialize the object <var>versioni</var>
	 * @param versioni Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Versioni versioni) throws SerializerException {
		this.objToXml(file, Versioni.class, versioni, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>versioni</var> of type {@link org.openspcoop2.protocol.manifest.Versioni}
	 * 
	 * @param file Xml file to serialize the object <var>versioni</var>
	 * @param versioni Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Versioni versioni,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Versioni.class, versioni, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>versioni</var> of type {@link org.openspcoop2.protocol.manifest.Versioni}
	 * 
	 * @param out OutputStream to serialize the object <var>versioni</var>
	 * @param versioni Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Versioni versioni) throws SerializerException {
		this.objToXml(out, Versioni.class, versioni, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>versioni</var> of type {@link org.openspcoop2.protocol.manifest.Versioni}
	 * 
	 * @param out OutputStream to serialize the object <var>versioni</var>
	 * @param versioni Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Versioni versioni,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Versioni.class, versioni, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>versioni</var> of type {@link org.openspcoop2.protocol.manifest.Versioni}
	 * 
	 * @param versioni Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Versioni versioni) throws SerializerException {
		return this.objToXml(Versioni.class, versioni, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>versioni</var> of type {@link org.openspcoop2.protocol.manifest.Versioni}
	 * 
	 * @param versioni Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Versioni versioni,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Versioni.class, versioni, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>versioni</var> of type {@link org.openspcoop2.protocol.manifest.Versioni}
	 * 
	 * @param versioni Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Versioni versioni) throws SerializerException {
		return this.objToXml(Versioni.class, versioni, false).toString();
	}
	/**
	 * Serialize to String the object <var>versioni</var> of type {@link org.openspcoop2.protocol.manifest.Versioni}
	 * 
	 * @param versioni Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Versioni versioni,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Versioni.class, versioni, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: soggetti
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>soggetti</var> of type {@link org.openspcoop2.protocol.manifest.Soggetti}
	 * 
	 * @param fileName Xml file to serialize the object <var>soggetti</var>
	 * @param soggetti Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Soggetti soggetti) throws SerializerException {
		this.objToXml(fileName, Soggetti.class, soggetti, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>soggetti</var> of type {@link org.openspcoop2.protocol.manifest.Soggetti}
	 * 
	 * @param fileName Xml file to serialize the object <var>soggetti</var>
	 * @param soggetti Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Soggetti soggetti,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Soggetti.class, soggetti, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>soggetti</var> of type {@link org.openspcoop2.protocol.manifest.Soggetti}
	 * 
	 * @param file Xml file to serialize the object <var>soggetti</var>
	 * @param soggetti Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Soggetti soggetti) throws SerializerException {
		this.objToXml(file, Soggetti.class, soggetti, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>soggetti</var> of type {@link org.openspcoop2.protocol.manifest.Soggetti}
	 * 
	 * @param file Xml file to serialize the object <var>soggetti</var>
	 * @param soggetti Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Soggetti soggetti,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Soggetti.class, soggetti, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>soggetti</var> of type {@link org.openspcoop2.protocol.manifest.Soggetti}
	 * 
	 * @param out OutputStream to serialize the object <var>soggetti</var>
	 * @param soggetti Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Soggetti soggetti) throws SerializerException {
		this.objToXml(out, Soggetti.class, soggetti, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>soggetti</var> of type {@link org.openspcoop2.protocol.manifest.Soggetti}
	 * 
	 * @param out OutputStream to serialize the object <var>soggetti</var>
	 * @param soggetti Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Soggetti soggetti,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Soggetti.class, soggetti, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>soggetti</var> of type {@link org.openspcoop2.protocol.manifest.Soggetti}
	 * 
	 * @param soggetti Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Soggetti soggetti) throws SerializerException {
		return this.objToXml(Soggetti.class, soggetti, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>soggetti</var> of type {@link org.openspcoop2.protocol.manifest.Soggetti}
	 * 
	 * @param soggetti Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Soggetti soggetti,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Soggetti.class, soggetti, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>soggetti</var> of type {@link org.openspcoop2.protocol.manifest.Soggetti}
	 * 
	 * @param soggetti Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Soggetti soggetti) throws SerializerException {
		return this.objToXml(Soggetti.class, soggetti, false).toString();
	}
	/**
	 * Serialize to String the object <var>soggetti</var> of type {@link org.openspcoop2.protocol.manifest.Soggetti}
	 * 
	 * @param soggetti Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Soggetti soggetti,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Soggetti.class, soggetti, prettyPrint).toString();
	}
	
	
	

}
