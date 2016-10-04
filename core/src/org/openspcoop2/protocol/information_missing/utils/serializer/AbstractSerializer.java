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
package org.openspcoop2.protocol.information_missing.utils.serializer;

import org.openspcoop2.generic_project.exception.SerializerException;
import org.openspcoop2.utils.beans.WriteToSerializerType;
import org.openspcoop2.utils.xml.JaxbUtils;

import org.openspcoop2.protocol.information_missing.ReplaceMatchType;
import org.openspcoop2.protocol.information_missing.ServizioApplicativo;
import org.openspcoop2.protocol.information_missing.Openspcoop2;
import org.openspcoop2.protocol.information_missing.Soggetto;
import org.openspcoop2.protocol.information_missing.Input;
import org.openspcoop2.protocol.information_missing.AccordoCooperazione;
import org.openspcoop2.protocol.information_missing.AccordoServizioParteComune;
import org.openspcoop2.protocol.information_missing.AccordoServizioParteSpecifica;
import org.openspcoop2.protocol.information_missing.Fruitore;
import org.openspcoop2.protocol.information_missing.RequisitoProtocollo;
import org.openspcoop2.protocol.information_missing.Requisiti;
import org.openspcoop2.protocol.information_missing.Proprieta;
import org.openspcoop2.protocol.information_missing.ReplaceMatchFieldType;
import org.openspcoop2.protocol.information_missing.Wizard;

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
	 Object: replaceMatchType
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>replaceMatchType</var> of type {@link org.openspcoop2.protocol.information_missing.ReplaceMatchType}
	 * 
	 * @param fileName Xml file to serialize the object <var>replaceMatchType</var>
	 * @param replaceMatchType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ReplaceMatchType replaceMatchType) throws SerializerException {
		this.objToXml(fileName, ReplaceMatchType.class, replaceMatchType, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>replaceMatchType</var> of type {@link org.openspcoop2.protocol.information_missing.ReplaceMatchType}
	 * 
	 * @param fileName Xml file to serialize the object <var>replaceMatchType</var>
	 * @param replaceMatchType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ReplaceMatchType replaceMatchType,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, ReplaceMatchType.class, replaceMatchType, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>replaceMatchType</var> of type {@link org.openspcoop2.protocol.information_missing.ReplaceMatchType}
	 * 
	 * @param file Xml file to serialize the object <var>replaceMatchType</var>
	 * @param replaceMatchType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ReplaceMatchType replaceMatchType) throws SerializerException {
		this.objToXml(file, ReplaceMatchType.class, replaceMatchType, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>replaceMatchType</var> of type {@link org.openspcoop2.protocol.information_missing.ReplaceMatchType}
	 * 
	 * @param file Xml file to serialize the object <var>replaceMatchType</var>
	 * @param replaceMatchType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ReplaceMatchType replaceMatchType,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, ReplaceMatchType.class, replaceMatchType, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>replaceMatchType</var> of type {@link org.openspcoop2.protocol.information_missing.ReplaceMatchType}
	 * 
	 * @param out OutputStream to serialize the object <var>replaceMatchType</var>
	 * @param replaceMatchType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ReplaceMatchType replaceMatchType) throws SerializerException {
		this.objToXml(out, ReplaceMatchType.class, replaceMatchType, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>replaceMatchType</var> of type {@link org.openspcoop2.protocol.information_missing.ReplaceMatchType}
	 * 
	 * @param out OutputStream to serialize the object <var>replaceMatchType</var>
	 * @param replaceMatchType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ReplaceMatchType replaceMatchType,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, ReplaceMatchType.class, replaceMatchType, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>replaceMatchType</var> of type {@link org.openspcoop2.protocol.information_missing.ReplaceMatchType}
	 * 
	 * @param replaceMatchType Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ReplaceMatchType replaceMatchType) throws SerializerException {
		return this.objToXml(ReplaceMatchType.class, replaceMatchType, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>replaceMatchType</var> of type {@link org.openspcoop2.protocol.information_missing.ReplaceMatchType}
	 * 
	 * @param replaceMatchType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ReplaceMatchType replaceMatchType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ReplaceMatchType.class, replaceMatchType, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>replaceMatchType</var> of type {@link org.openspcoop2.protocol.information_missing.ReplaceMatchType}
	 * 
	 * @param replaceMatchType Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ReplaceMatchType replaceMatchType) throws SerializerException {
		return this.objToXml(ReplaceMatchType.class, replaceMatchType, false).toString();
	}
	/**
	 * Serialize to String the object <var>replaceMatchType</var> of type {@link org.openspcoop2.protocol.information_missing.ReplaceMatchType}
	 * 
	 * @param replaceMatchType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ReplaceMatchType replaceMatchType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ReplaceMatchType.class, replaceMatchType, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: ServizioApplicativo
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>servizioApplicativo</var> of type {@link org.openspcoop2.protocol.information_missing.ServizioApplicativo}
	 * 
	 * @param fileName Xml file to serialize the object <var>servizioApplicativo</var>
	 * @param servizioApplicativo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ServizioApplicativo servizioApplicativo) throws SerializerException {
		this.objToXml(fileName, ServizioApplicativo.class, servizioApplicativo, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>servizioApplicativo</var> of type {@link org.openspcoop2.protocol.information_missing.ServizioApplicativo}
	 * 
	 * @param fileName Xml file to serialize the object <var>servizioApplicativo</var>
	 * @param servizioApplicativo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ServizioApplicativo servizioApplicativo,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, ServizioApplicativo.class, servizioApplicativo, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>servizioApplicativo</var> of type {@link org.openspcoop2.protocol.information_missing.ServizioApplicativo}
	 * 
	 * @param file Xml file to serialize the object <var>servizioApplicativo</var>
	 * @param servizioApplicativo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ServizioApplicativo servizioApplicativo) throws SerializerException {
		this.objToXml(file, ServizioApplicativo.class, servizioApplicativo, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>servizioApplicativo</var> of type {@link org.openspcoop2.protocol.information_missing.ServizioApplicativo}
	 * 
	 * @param file Xml file to serialize the object <var>servizioApplicativo</var>
	 * @param servizioApplicativo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ServizioApplicativo servizioApplicativo,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, ServizioApplicativo.class, servizioApplicativo, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>servizioApplicativo</var> of type {@link org.openspcoop2.protocol.information_missing.ServizioApplicativo}
	 * 
	 * @param out OutputStream to serialize the object <var>servizioApplicativo</var>
	 * @param servizioApplicativo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ServizioApplicativo servizioApplicativo) throws SerializerException {
		this.objToXml(out, ServizioApplicativo.class, servizioApplicativo, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>servizioApplicativo</var> of type {@link org.openspcoop2.protocol.information_missing.ServizioApplicativo}
	 * 
	 * @param out OutputStream to serialize the object <var>servizioApplicativo</var>
	 * @param servizioApplicativo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ServizioApplicativo servizioApplicativo,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, ServizioApplicativo.class, servizioApplicativo, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>servizioApplicativo</var> of type {@link org.openspcoop2.protocol.information_missing.ServizioApplicativo}
	 * 
	 * @param servizioApplicativo Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ServizioApplicativo servizioApplicativo) throws SerializerException {
		return this.objToXml(ServizioApplicativo.class, servizioApplicativo, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>servizioApplicativo</var> of type {@link org.openspcoop2.protocol.information_missing.ServizioApplicativo}
	 * 
	 * @param servizioApplicativo Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ServizioApplicativo servizioApplicativo,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ServizioApplicativo.class, servizioApplicativo, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>servizioApplicativo</var> of type {@link org.openspcoop2.protocol.information_missing.ServizioApplicativo}
	 * 
	 * @param servizioApplicativo Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ServizioApplicativo servizioApplicativo) throws SerializerException {
		return this.objToXml(ServizioApplicativo.class, servizioApplicativo, false).toString();
	}
	/**
	 * Serialize to String the object <var>servizioApplicativo</var> of type {@link org.openspcoop2.protocol.information_missing.ServizioApplicativo}
	 * 
	 * @param servizioApplicativo Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ServizioApplicativo servizioApplicativo,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ServizioApplicativo.class, servizioApplicativo, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: openspcoop2
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>openspcoop2</var> of type {@link org.openspcoop2.protocol.information_missing.Openspcoop2}
	 * 
	 * @param fileName Xml file to serialize the object <var>openspcoop2</var>
	 * @param openspcoop2 Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Openspcoop2 openspcoop2) throws SerializerException {
		this.objToXml(fileName, Openspcoop2.class, openspcoop2, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>openspcoop2</var> of type {@link org.openspcoop2.protocol.information_missing.Openspcoop2}
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
	 * Serialize to file system in <var>file</var> the object <var>openspcoop2</var> of type {@link org.openspcoop2.protocol.information_missing.Openspcoop2}
	 * 
	 * @param file Xml file to serialize the object <var>openspcoop2</var>
	 * @param openspcoop2 Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Openspcoop2 openspcoop2) throws SerializerException {
		this.objToXml(file, Openspcoop2.class, openspcoop2, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>openspcoop2</var> of type {@link org.openspcoop2.protocol.information_missing.Openspcoop2}
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
	 * Serialize to output stream <var>out</var> the object <var>openspcoop2</var> of type {@link org.openspcoop2.protocol.information_missing.Openspcoop2}
	 * 
	 * @param out OutputStream to serialize the object <var>openspcoop2</var>
	 * @param openspcoop2 Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Openspcoop2 openspcoop2) throws SerializerException {
		this.objToXml(out, Openspcoop2.class, openspcoop2, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>openspcoop2</var> of type {@link org.openspcoop2.protocol.information_missing.Openspcoop2}
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
	 * Serialize to byte array the object <var>openspcoop2</var> of type {@link org.openspcoop2.protocol.information_missing.Openspcoop2}
	 * 
	 * @param openspcoop2 Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Openspcoop2 openspcoop2) throws SerializerException {
		return this.objToXml(Openspcoop2.class, openspcoop2, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>openspcoop2</var> of type {@link org.openspcoop2.protocol.information_missing.Openspcoop2}
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
	 * Serialize to String the object <var>openspcoop2</var> of type {@link org.openspcoop2.protocol.information_missing.Openspcoop2}
	 * 
	 * @param openspcoop2 Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Openspcoop2 openspcoop2) throws SerializerException {
		return this.objToXml(Openspcoop2.class, openspcoop2, false).toString();
	}
	/**
	 * Serialize to String the object <var>openspcoop2</var> of type {@link org.openspcoop2.protocol.information_missing.Openspcoop2}
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
	 Object: Soggetto
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>soggetto</var> of type {@link org.openspcoop2.protocol.information_missing.Soggetto}
	 * 
	 * @param fileName Xml file to serialize the object <var>soggetto</var>
	 * @param soggetto Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Soggetto soggetto) throws SerializerException {
		this.objToXml(fileName, Soggetto.class, soggetto, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>soggetto</var> of type {@link org.openspcoop2.protocol.information_missing.Soggetto}
	 * 
	 * @param fileName Xml file to serialize the object <var>soggetto</var>
	 * @param soggetto Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Soggetto soggetto,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Soggetto.class, soggetto, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>soggetto</var> of type {@link org.openspcoop2.protocol.information_missing.Soggetto}
	 * 
	 * @param file Xml file to serialize the object <var>soggetto</var>
	 * @param soggetto Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Soggetto soggetto) throws SerializerException {
		this.objToXml(file, Soggetto.class, soggetto, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>soggetto</var> of type {@link org.openspcoop2.protocol.information_missing.Soggetto}
	 * 
	 * @param file Xml file to serialize the object <var>soggetto</var>
	 * @param soggetto Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Soggetto soggetto,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Soggetto.class, soggetto, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>soggetto</var> of type {@link org.openspcoop2.protocol.information_missing.Soggetto}
	 * 
	 * @param out OutputStream to serialize the object <var>soggetto</var>
	 * @param soggetto Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Soggetto soggetto) throws SerializerException {
		this.objToXml(out, Soggetto.class, soggetto, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>soggetto</var> of type {@link org.openspcoop2.protocol.information_missing.Soggetto}
	 * 
	 * @param out OutputStream to serialize the object <var>soggetto</var>
	 * @param soggetto Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Soggetto soggetto,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Soggetto.class, soggetto, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>soggetto</var> of type {@link org.openspcoop2.protocol.information_missing.Soggetto}
	 * 
	 * @param soggetto Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Soggetto soggetto) throws SerializerException {
		return this.objToXml(Soggetto.class, soggetto, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>soggetto</var> of type {@link org.openspcoop2.protocol.information_missing.Soggetto}
	 * 
	 * @param soggetto Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Soggetto soggetto,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Soggetto.class, soggetto, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>soggetto</var> of type {@link org.openspcoop2.protocol.information_missing.Soggetto}
	 * 
	 * @param soggetto Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Soggetto soggetto) throws SerializerException {
		return this.objToXml(Soggetto.class, soggetto, false).toString();
	}
	/**
	 * Serialize to String the object <var>soggetto</var> of type {@link org.openspcoop2.protocol.information_missing.Soggetto}
	 * 
	 * @param soggetto Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Soggetto soggetto,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Soggetto.class, soggetto, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: Input
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>input</var> of type {@link org.openspcoop2.protocol.information_missing.Input}
	 * 
	 * @param fileName Xml file to serialize the object <var>input</var>
	 * @param input Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Input input) throws SerializerException {
		this.objToXml(fileName, Input.class, input, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>input</var> of type {@link org.openspcoop2.protocol.information_missing.Input}
	 * 
	 * @param fileName Xml file to serialize the object <var>input</var>
	 * @param input Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Input input,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Input.class, input, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>input</var> of type {@link org.openspcoop2.protocol.information_missing.Input}
	 * 
	 * @param file Xml file to serialize the object <var>input</var>
	 * @param input Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Input input) throws SerializerException {
		this.objToXml(file, Input.class, input, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>input</var> of type {@link org.openspcoop2.protocol.information_missing.Input}
	 * 
	 * @param file Xml file to serialize the object <var>input</var>
	 * @param input Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Input input,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Input.class, input, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>input</var> of type {@link org.openspcoop2.protocol.information_missing.Input}
	 * 
	 * @param out OutputStream to serialize the object <var>input</var>
	 * @param input Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Input input) throws SerializerException {
		this.objToXml(out, Input.class, input, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>input</var> of type {@link org.openspcoop2.protocol.information_missing.Input}
	 * 
	 * @param out OutputStream to serialize the object <var>input</var>
	 * @param input Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Input input,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Input.class, input, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>input</var> of type {@link org.openspcoop2.protocol.information_missing.Input}
	 * 
	 * @param input Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Input input) throws SerializerException {
		return this.objToXml(Input.class, input, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>input</var> of type {@link org.openspcoop2.protocol.information_missing.Input}
	 * 
	 * @param input Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Input input,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Input.class, input, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>input</var> of type {@link org.openspcoop2.protocol.information_missing.Input}
	 * 
	 * @param input Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Input input) throws SerializerException {
		return this.objToXml(Input.class, input, false).toString();
	}
	/**
	 * Serialize to String the object <var>input</var> of type {@link org.openspcoop2.protocol.information_missing.Input}
	 * 
	 * @param input Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Input input,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Input.class, input, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: AccordoCooperazione
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>accordoCooperazione</var> of type {@link org.openspcoop2.protocol.information_missing.AccordoCooperazione}
	 * 
	 * @param fileName Xml file to serialize the object <var>accordoCooperazione</var>
	 * @param accordoCooperazione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,AccordoCooperazione accordoCooperazione) throws SerializerException {
		this.objToXml(fileName, AccordoCooperazione.class, accordoCooperazione, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>accordoCooperazione</var> of type {@link org.openspcoop2.protocol.information_missing.AccordoCooperazione}
	 * 
	 * @param fileName Xml file to serialize the object <var>accordoCooperazione</var>
	 * @param accordoCooperazione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,AccordoCooperazione accordoCooperazione,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, AccordoCooperazione.class, accordoCooperazione, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>accordoCooperazione</var> of type {@link org.openspcoop2.protocol.information_missing.AccordoCooperazione}
	 * 
	 * @param file Xml file to serialize the object <var>accordoCooperazione</var>
	 * @param accordoCooperazione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,AccordoCooperazione accordoCooperazione) throws SerializerException {
		this.objToXml(file, AccordoCooperazione.class, accordoCooperazione, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>accordoCooperazione</var> of type {@link org.openspcoop2.protocol.information_missing.AccordoCooperazione}
	 * 
	 * @param file Xml file to serialize the object <var>accordoCooperazione</var>
	 * @param accordoCooperazione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,AccordoCooperazione accordoCooperazione,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, AccordoCooperazione.class, accordoCooperazione, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>accordoCooperazione</var> of type {@link org.openspcoop2.protocol.information_missing.AccordoCooperazione}
	 * 
	 * @param out OutputStream to serialize the object <var>accordoCooperazione</var>
	 * @param accordoCooperazione Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,AccordoCooperazione accordoCooperazione) throws SerializerException {
		this.objToXml(out, AccordoCooperazione.class, accordoCooperazione, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>accordoCooperazione</var> of type {@link org.openspcoop2.protocol.information_missing.AccordoCooperazione}
	 * 
	 * @param out OutputStream to serialize the object <var>accordoCooperazione</var>
	 * @param accordoCooperazione Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,AccordoCooperazione accordoCooperazione,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, AccordoCooperazione.class, accordoCooperazione, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>accordoCooperazione</var> of type {@link org.openspcoop2.protocol.information_missing.AccordoCooperazione}
	 * 
	 * @param accordoCooperazione Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(AccordoCooperazione accordoCooperazione) throws SerializerException {
		return this.objToXml(AccordoCooperazione.class, accordoCooperazione, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>accordoCooperazione</var> of type {@link org.openspcoop2.protocol.information_missing.AccordoCooperazione}
	 * 
	 * @param accordoCooperazione Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(AccordoCooperazione accordoCooperazione,boolean prettyPrint) throws SerializerException {
		return this.objToXml(AccordoCooperazione.class, accordoCooperazione, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>accordoCooperazione</var> of type {@link org.openspcoop2.protocol.information_missing.AccordoCooperazione}
	 * 
	 * @param accordoCooperazione Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(AccordoCooperazione accordoCooperazione) throws SerializerException {
		return this.objToXml(AccordoCooperazione.class, accordoCooperazione, false).toString();
	}
	/**
	 * Serialize to String the object <var>accordoCooperazione</var> of type {@link org.openspcoop2.protocol.information_missing.AccordoCooperazione}
	 * 
	 * @param accordoCooperazione Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(AccordoCooperazione accordoCooperazione,boolean prettyPrint) throws SerializerException {
		return this.objToXml(AccordoCooperazione.class, accordoCooperazione, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: AccordoServizioParteComune
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>accordoServizioParteComune</var> of type {@link org.openspcoop2.protocol.information_missing.AccordoServizioParteComune}
	 * 
	 * @param fileName Xml file to serialize the object <var>accordoServizioParteComune</var>
	 * @param accordoServizioParteComune Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,AccordoServizioParteComune accordoServizioParteComune) throws SerializerException {
		this.objToXml(fileName, AccordoServizioParteComune.class, accordoServizioParteComune, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>accordoServizioParteComune</var> of type {@link org.openspcoop2.protocol.information_missing.AccordoServizioParteComune}
	 * 
	 * @param fileName Xml file to serialize the object <var>accordoServizioParteComune</var>
	 * @param accordoServizioParteComune Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,AccordoServizioParteComune accordoServizioParteComune,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, AccordoServizioParteComune.class, accordoServizioParteComune, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>accordoServizioParteComune</var> of type {@link org.openspcoop2.protocol.information_missing.AccordoServizioParteComune}
	 * 
	 * @param file Xml file to serialize the object <var>accordoServizioParteComune</var>
	 * @param accordoServizioParteComune Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,AccordoServizioParteComune accordoServizioParteComune) throws SerializerException {
		this.objToXml(file, AccordoServizioParteComune.class, accordoServizioParteComune, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>accordoServizioParteComune</var> of type {@link org.openspcoop2.protocol.information_missing.AccordoServizioParteComune}
	 * 
	 * @param file Xml file to serialize the object <var>accordoServizioParteComune</var>
	 * @param accordoServizioParteComune Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,AccordoServizioParteComune accordoServizioParteComune,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, AccordoServizioParteComune.class, accordoServizioParteComune, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>accordoServizioParteComune</var> of type {@link org.openspcoop2.protocol.information_missing.AccordoServizioParteComune}
	 * 
	 * @param out OutputStream to serialize the object <var>accordoServizioParteComune</var>
	 * @param accordoServizioParteComune Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,AccordoServizioParteComune accordoServizioParteComune) throws SerializerException {
		this.objToXml(out, AccordoServizioParteComune.class, accordoServizioParteComune, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>accordoServizioParteComune</var> of type {@link org.openspcoop2.protocol.information_missing.AccordoServizioParteComune}
	 * 
	 * @param out OutputStream to serialize the object <var>accordoServizioParteComune</var>
	 * @param accordoServizioParteComune Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,AccordoServizioParteComune accordoServizioParteComune,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, AccordoServizioParteComune.class, accordoServizioParteComune, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>accordoServizioParteComune</var> of type {@link org.openspcoop2.protocol.information_missing.AccordoServizioParteComune}
	 * 
	 * @param accordoServizioParteComune Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(AccordoServizioParteComune accordoServizioParteComune) throws SerializerException {
		return this.objToXml(AccordoServizioParteComune.class, accordoServizioParteComune, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>accordoServizioParteComune</var> of type {@link org.openspcoop2.protocol.information_missing.AccordoServizioParteComune}
	 * 
	 * @param accordoServizioParteComune Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(AccordoServizioParteComune accordoServizioParteComune,boolean prettyPrint) throws SerializerException {
		return this.objToXml(AccordoServizioParteComune.class, accordoServizioParteComune, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>accordoServizioParteComune</var> of type {@link org.openspcoop2.protocol.information_missing.AccordoServizioParteComune}
	 * 
	 * @param accordoServizioParteComune Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(AccordoServizioParteComune accordoServizioParteComune) throws SerializerException {
		return this.objToXml(AccordoServizioParteComune.class, accordoServizioParteComune, false).toString();
	}
	/**
	 * Serialize to String the object <var>accordoServizioParteComune</var> of type {@link org.openspcoop2.protocol.information_missing.AccordoServizioParteComune}
	 * 
	 * @param accordoServizioParteComune Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(AccordoServizioParteComune accordoServizioParteComune,boolean prettyPrint) throws SerializerException {
		return this.objToXml(AccordoServizioParteComune.class, accordoServizioParteComune, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: AccordoServizioParteSpecifica
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>accordoServizioParteSpecifica</var> of type {@link org.openspcoop2.protocol.information_missing.AccordoServizioParteSpecifica}
	 * 
	 * @param fileName Xml file to serialize the object <var>accordoServizioParteSpecifica</var>
	 * @param accordoServizioParteSpecifica Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,AccordoServizioParteSpecifica accordoServizioParteSpecifica) throws SerializerException {
		this.objToXml(fileName, AccordoServizioParteSpecifica.class, accordoServizioParteSpecifica, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>accordoServizioParteSpecifica</var> of type {@link org.openspcoop2.protocol.information_missing.AccordoServizioParteSpecifica}
	 * 
	 * @param fileName Xml file to serialize the object <var>accordoServizioParteSpecifica</var>
	 * @param accordoServizioParteSpecifica Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,AccordoServizioParteSpecifica accordoServizioParteSpecifica,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, AccordoServizioParteSpecifica.class, accordoServizioParteSpecifica, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>accordoServizioParteSpecifica</var> of type {@link org.openspcoop2.protocol.information_missing.AccordoServizioParteSpecifica}
	 * 
	 * @param file Xml file to serialize the object <var>accordoServizioParteSpecifica</var>
	 * @param accordoServizioParteSpecifica Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,AccordoServizioParteSpecifica accordoServizioParteSpecifica) throws SerializerException {
		this.objToXml(file, AccordoServizioParteSpecifica.class, accordoServizioParteSpecifica, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>accordoServizioParteSpecifica</var> of type {@link org.openspcoop2.protocol.information_missing.AccordoServizioParteSpecifica}
	 * 
	 * @param file Xml file to serialize the object <var>accordoServizioParteSpecifica</var>
	 * @param accordoServizioParteSpecifica Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,AccordoServizioParteSpecifica accordoServizioParteSpecifica,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, AccordoServizioParteSpecifica.class, accordoServizioParteSpecifica, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>accordoServizioParteSpecifica</var> of type {@link org.openspcoop2.protocol.information_missing.AccordoServizioParteSpecifica}
	 * 
	 * @param out OutputStream to serialize the object <var>accordoServizioParteSpecifica</var>
	 * @param accordoServizioParteSpecifica Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,AccordoServizioParteSpecifica accordoServizioParteSpecifica) throws SerializerException {
		this.objToXml(out, AccordoServizioParteSpecifica.class, accordoServizioParteSpecifica, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>accordoServizioParteSpecifica</var> of type {@link org.openspcoop2.protocol.information_missing.AccordoServizioParteSpecifica}
	 * 
	 * @param out OutputStream to serialize the object <var>accordoServizioParteSpecifica</var>
	 * @param accordoServizioParteSpecifica Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,AccordoServizioParteSpecifica accordoServizioParteSpecifica,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, AccordoServizioParteSpecifica.class, accordoServizioParteSpecifica, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>accordoServizioParteSpecifica</var> of type {@link org.openspcoop2.protocol.information_missing.AccordoServizioParteSpecifica}
	 * 
	 * @param accordoServizioParteSpecifica Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(AccordoServizioParteSpecifica accordoServizioParteSpecifica) throws SerializerException {
		return this.objToXml(AccordoServizioParteSpecifica.class, accordoServizioParteSpecifica, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>accordoServizioParteSpecifica</var> of type {@link org.openspcoop2.protocol.information_missing.AccordoServizioParteSpecifica}
	 * 
	 * @param accordoServizioParteSpecifica Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(AccordoServizioParteSpecifica accordoServizioParteSpecifica,boolean prettyPrint) throws SerializerException {
		return this.objToXml(AccordoServizioParteSpecifica.class, accordoServizioParteSpecifica, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>accordoServizioParteSpecifica</var> of type {@link org.openspcoop2.protocol.information_missing.AccordoServizioParteSpecifica}
	 * 
	 * @param accordoServizioParteSpecifica Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(AccordoServizioParteSpecifica accordoServizioParteSpecifica) throws SerializerException {
		return this.objToXml(AccordoServizioParteSpecifica.class, accordoServizioParteSpecifica, false).toString();
	}
	/**
	 * Serialize to String the object <var>accordoServizioParteSpecifica</var> of type {@link org.openspcoop2.protocol.information_missing.AccordoServizioParteSpecifica}
	 * 
	 * @param accordoServizioParteSpecifica Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(AccordoServizioParteSpecifica accordoServizioParteSpecifica,boolean prettyPrint) throws SerializerException {
		return this.objToXml(AccordoServizioParteSpecifica.class, accordoServizioParteSpecifica, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: Fruitore
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>fruitore</var> of type {@link org.openspcoop2.protocol.information_missing.Fruitore}
	 * 
	 * @param fileName Xml file to serialize the object <var>fruitore</var>
	 * @param fruitore Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Fruitore fruitore) throws SerializerException {
		this.objToXml(fileName, Fruitore.class, fruitore, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>fruitore</var> of type {@link org.openspcoop2.protocol.information_missing.Fruitore}
	 * 
	 * @param fileName Xml file to serialize the object <var>fruitore</var>
	 * @param fruitore Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Fruitore fruitore,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Fruitore.class, fruitore, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>fruitore</var> of type {@link org.openspcoop2.protocol.information_missing.Fruitore}
	 * 
	 * @param file Xml file to serialize the object <var>fruitore</var>
	 * @param fruitore Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Fruitore fruitore) throws SerializerException {
		this.objToXml(file, Fruitore.class, fruitore, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>fruitore</var> of type {@link org.openspcoop2.protocol.information_missing.Fruitore}
	 * 
	 * @param file Xml file to serialize the object <var>fruitore</var>
	 * @param fruitore Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Fruitore fruitore,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Fruitore.class, fruitore, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>fruitore</var> of type {@link org.openspcoop2.protocol.information_missing.Fruitore}
	 * 
	 * @param out OutputStream to serialize the object <var>fruitore</var>
	 * @param fruitore Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Fruitore fruitore) throws SerializerException {
		this.objToXml(out, Fruitore.class, fruitore, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>fruitore</var> of type {@link org.openspcoop2.protocol.information_missing.Fruitore}
	 * 
	 * @param out OutputStream to serialize the object <var>fruitore</var>
	 * @param fruitore Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Fruitore fruitore,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Fruitore.class, fruitore, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>fruitore</var> of type {@link org.openspcoop2.protocol.information_missing.Fruitore}
	 * 
	 * @param fruitore Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Fruitore fruitore) throws SerializerException {
		return this.objToXml(Fruitore.class, fruitore, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>fruitore</var> of type {@link org.openspcoop2.protocol.information_missing.Fruitore}
	 * 
	 * @param fruitore Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Fruitore fruitore,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Fruitore.class, fruitore, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>fruitore</var> of type {@link org.openspcoop2.protocol.information_missing.Fruitore}
	 * 
	 * @param fruitore Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Fruitore fruitore) throws SerializerException {
		return this.objToXml(Fruitore.class, fruitore, false).toString();
	}
	/**
	 * Serialize to String the object <var>fruitore</var> of type {@link org.openspcoop2.protocol.information_missing.Fruitore}
	 * 
	 * @param fruitore Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Fruitore fruitore,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Fruitore.class, fruitore, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: RequisitoProtocollo
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>requisitoProtocollo</var> of type {@link org.openspcoop2.protocol.information_missing.RequisitoProtocollo}
	 * 
	 * @param fileName Xml file to serialize the object <var>requisitoProtocollo</var>
	 * @param requisitoProtocollo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,RequisitoProtocollo requisitoProtocollo) throws SerializerException {
		this.objToXml(fileName, RequisitoProtocollo.class, requisitoProtocollo, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>requisitoProtocollo</var> of type {@link org.openspcoop2.protocol.information_missing.RequisitoProtocollo}
	 * 
	 * @param fileName Xml file to serialize the object <var>requisitoProtocollo</var>
	 * @param requisitoProtocollo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,RequisitoProtocollo requisitoProtocollo,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, RequisitoProtocollo.class, requisitoProtocollo, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>requisitoProtocollo</var> of type {@link org.openspcoop2.protocol.information_missing.RequisitoProtocollo}
	 * 
	 * @param file Xml file to serialize the object <var>requisitoProtocollo</var>
	 * @param requisitoProtocollo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,RequisitoProtocollo requisitoProtocollo) throws SerializerException {
		this.objToXml(file, RequisitoProtocollo.class, requisitoProtocollo, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>requisitoProtocollo</var> of type {@link org.openspcoop2.protocol.information_missing.RequisitoProtocollo}
	 * 
	 * @param file Xml file to serialize the object <var>requisitoProtocollo</var>
	 * @param requisitoProtocollo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,RequisitoProtocollo requisitoProtocollo,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, RequisitoProtocollo.class, requisitoProtocollo, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>requisitoProtocollo</var> of type {@link org.openspcoop2.protocol.information_missing.RequisitoProtocollo}
	 * 
	 * @param out OutputStream to serialize the object <var>requisitoProtocollo</var>
	 * @param requisitoProtocollo Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,RequisitoProtocollo requisitoProtocollo) throws SerializerException {
		this.objToXml(out, RequisitoProtocollo.class, requisitoProtocollo, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>requisitoProtocollo</var> of type {@link org.openspcoop2.protocol.information_missing.RequisitoProtocollo}
	 * 
	 * @param out OutputStream to serialize the object <var>requisitoProtocollo</var>
	 * @param requisitoProtocollo Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,RequisitoProtocollo requisitoProtocollo,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, RequisitoProtocollo.class, requisitoProtocollo, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>requisitoProtocollo</var> of type {@link org.openspcoop2.protocol.information_missing.RequisitoProtocollo}
	 * 
	 * @param requisitoProtocollo Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(RequisitoProtocollo requisitoProtocollo) throws SerializerException {
		return this.objToXml(RequisitoProtocollo.class, requisitoProtocollo, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>requisitoProtocollo</var> of type {@link org.openspcoop2.protocol.information_missing.RequisitoProtocollo}
	 * 
	 * @param requisitoProtocollo Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(RequisitoProtocollo requisitoProtocollo,boolean prettyPrint) throws SerializerException {
		return this.objToXml(RequisitoProtocollo.class, requisitoProtocollo, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>requisitoProtocollo</var> of type {@link org.openspcoop2.protocol.information_missing.RequisitoProtocollo}
	 * 
	 * @param requisitoProtocollo Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(RequisitoProtocollo requisitoProtocollo) throws SerializerException {
		return this.objToXml(RequisitoProtocollo.class, requisitoProtocollo, false).toString();
	}
	/**
	 * Serialize to String the object <var>requisitoProtocollo</var> of type {@link org.openspcoop2.protocol.information_missing.RequisitoProtocollo}
	 * 
	 * @param requisitoProtocollo Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(RequisitoProtocollo requisitoProtocollo,boolean prettyPrint) throws SerializerException {
		return this.objToXml(RequisitoProtocollo.class, requisitoProtocollo, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: Requisiti
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>requisiti</var> of type {@link org.openspcoop2.protocol.information_missing.Requisiti}
	 * 
	 * @param fileName Xml file to serialize the object <var>requisiti</var>
	 * @param requisiti Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Requisiti requisiti) throws SerializerException {
		this.objToXml(fileName, Requisiti.class, requisiti, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>requisiti</var> of type {@link org.openspcoop2.protocol.information_missing.Requisiti}
	 * 
	 * @param fileName Xml file to serialize the object <var>requisiti</var>
	 * @param requisiti Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Requisiti requisiti,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Requisiti.class, requisiti, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>requisiti</var> of type {@link org.openspcoop2.protocol.information_missing.Requisiti}
	 * 
	 * @param file Xml file to serialize the object <var>requisiti</var>
	 * @param requisiti Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Requisiti requisiti) throws SerializerException {
		this.objToXml(file, Requisiti.class, requisiti, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>requisiti</var> of type {@link org.openspcoop2.protocol.information_missing.Requisiti}
	 * 
	 * @param file Xml file to serialize the object <var>requisiti</var>
	 * @param requisiti Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Requisiti requisiti,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Requisiti.class, requisiti, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>requisiti</var> of type {@link org.openspcoop2.protocol.information_missing.Requisiti}
	 * 
	 * @param out OutputStream to serialize the object <var>requisiti</var>
	 * @param requisiti Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Requisiti requisiti) throws SerializerException {
		this.objToXml(out, Requisiti.class, requisiti, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>requisiti</var> of type {@link org.openspcoop2.protocol.information_missing.Requisiti}
	 * 
	 * @param out OutputStream to serialize the object <var>requisiti</var>
	 * @param requisiti Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Requisiti requisiti,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Requisiti.class, requisiti, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>requisiti</var> of type {@link org.openspcoop2.protocol.information_missing.Requisiti}
	 * 
	 * @param requisiti Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Requisiti requisiti) throws SerializerException {
		return this.objToXml(Requisiti.class, requisiti, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>requisiti</var> of type {@link org.openspcoop2.protocol.information_missing.Requisiti}
	 * 
	 * @param requisiti Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Requisiti requisiti,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Requisiti.class, requisiti, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>requisiti</var> of type {@link org.openspcoop2.protocol.information_missing.Requisiti}
	 * 
	 * @param requisiti Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Requisiti requisiti) throws SerializerException {
		return this.objToXml(Requisiti.class, requisiti, false).toString();
	}
	/**
	 * Serialize to String the object <var>requisiti</var> of type {@link org.openspcoop2.protocol.information_missing.Requisiti}
	 * 
	 * @param requisiti Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Requisiti requisiti,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Requisiti.class, requisiti, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: Proprieta
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>proprieta</var> of type {@link org.openspcoop2.protocol.information_missing.Proprieta}
	 * 
	 * @param fileName Xml file to serialize the object <var>proprieta</var>
	 * @param proprieta Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Proprieta proprieta) throws SerializerException {
		this.objToXml(fileName, Proprieta.class, proprieta, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>proprieta</var> of type {@link org.openspcoop2.protocol.information_missing.Proprieta}
	 * 
	 * @param fileName Xml file to serialize the object <var>proprieta</var>
	 * @param proprieta Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Proprieta proprieta,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Proprieta.class, proprieta, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>proprieta</var> of type {@link org.openspcoop2.protocol.information_missing.Proprieta}
	 * 
	 * @param file Xml file to serialize the object <var>proprieta</var>
	 * @param proprieta Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Proprieta proprieta) throws SerializerException {
		this.objToXml(file, Proprieta.class, proprieta, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>proprieta</var> of type {@link org.openspcoop2.protocol.information_missing.Proprieta}
	 * 
	 * @param file Xml file to serialize the object <var>proprieta</var>
	 * @param proprieta Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Proprieta proprieta,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Proprieta.class, proprieta, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>proprieta</var> of type {@link org.openspcoop2.protocol.information_missing.Proprieta}
	 * 
	 * @param out OutputStream to serialize the object <var>proprieta</var>
	 * @param proprieta Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Proprieta proprieta) throws SerializerException {
		this.objToXml(out, Proprieta.class, proprieta, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>proprieta</var> of type {@link org.openspcoop2.protocol.information_missing.Proprieta}
	 * 
	 * @param out OutputStream to serialize the object <var>proprieta</var>
	 * @param proprieta Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Proprieta proprieta,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Proprieta.class, proprieta, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>proprieta</var> of type {@link org.openspcoop2.protocol.information_missing.Proprieta}
	 * 
	 * @param proprieta Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Proprieta proprieta) throws SerializerException {
		return this.objToXml(Proprieta.class, proprieta, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>proprieta</var> of type {@link org.openspcoop2.protocol.information_missing.Proprieta}
	 * 
	 * @param proprieta Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Proprieta proprieta,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Proprieta.class, proprieta, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>proprieta</var> of type {@link org.openspcoop2.protocol.information_missing.Proprieta}
	 * 
	 * @param proprieta Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Proprieta proprieta) throws SerializerException {
		return this.objToXml(Proprieta.class, proprieta, false).toString();
	}
	/**
	 * Serialize to String the object <var>proprieta</var> of type {@link org.openspcoop2.protocol.information_missing.Proprieta}
	 * 
	 * @param proprieta Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Proprieta proprieta,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Proprieta.class, proprieta, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: ReplaceMatchFieldType
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>replaceMatchFieldType</var> of type {@link org.openspcoop2.protocol.information_missing.ReplaceMatchFieldType}
	 * 
	 * @param fileName Xml file to serialize the object <var>replaceMatchFieldType</var>
	 * @param replaceMatchFieldType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ReplaceMatchFieldType replaceMatchFieldType) throws SerializerException {
		this.objToXml(fileName, ReplaceMatchFieldType.class, replaceMatchFieldType, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>replaceMatchFieldType</var> of type {@link org.openspcoop2.protocol.information_missing.ReplaceMatchFieldType}
	 * 
	 * @param fileName Xml file to serialize the object <var>replaceMatchFieldType</var>
	 * @param replaceMatchFieldType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ReplaceMatchFieldType replaceMatchFieldType,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, ReplaceMatchFieldType.class, replaceMatchFieldType, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>replaceMatchFieldType</var> of type {@link org.openspcoop2.protocol.information_missing.ReplaceMatchFieldType}
	 * 
	 * @param file Xml file to serialize the object <var>replaceMatchFieldType</var>
	 * @param replaceMatchFieldType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ReplaceMatchFieldType replaceMatchFieldType) throws SerializerException {
		this.objToXml(file, ReplaceMatchFieldType.class, replaceMatchFieldType, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>replaceMatchFieldType</var> of type {@link org.openspcoop2.protocol.information_missing.ReplaceMatchFieldType}
	 * 
	 * @param file Xml file to serialize the object <var>replaceMatchFieldType</var>
	 * @param replaceMatchFieldType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ReplaceMatchFieldType replaceMatchFieldType,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, ReplaceMatchFieldType.class, replaceMatchFieldType, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>replaceMatchFieldType</var> of type {@link org.openspcoop2.protocol.information_missing.ReplaceMatchFieldType}
	 * 
	 * @param out OutputStream to serialize the object <var>replaceMatchFieldType</var>
	 * @param replaceMatchFieldType Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ReplaceMatchFieldType replaceMatchFieldType) throws SerializerException {
		this.objToXml(out, ReplaceMatchFieldType.class, replaceMatchFieldType, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>replaceMatchFieldType</var> of type {@link org.openspcoop2.protocol.information_missing.ReplaceMatchFieldType}
	 * 
	 * @param out OutputStream to serialize the object <var>replaceMatchFieldType</var>
	 * @param replaceMatchFieldType Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ReplaceMatchFieldType replaceMatchFieldType,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, ReplaceMatchFieldType.class, replaceMatchFieldType, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>replaceMatchFieldType</var> of type {@link org.openspcoop2.protocol.information_missing.ReplaceMatchFieldType}
	 * 
	 * @param replaceMatchFieldType Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ReplaceMatchFieldType replaceMatchFieldType) throws SerializerException {
		return this.objToXml(ReplaceMatchFieldType.class, replaceMatchFieldType, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>replaceMatchFieldType</var> of type {@link org.openspcoop2.protocol.information_missing.ReplaceMatchFieldType}
	 * 
	 * @param replaceMatchFieldType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ReplaceMatchFieldType replaceMatchFieldType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ReplaceMatchFieldType.class, replaceMatchFieldType, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>replaceMatchFieldType</var> of type {@link org.openspcoop2.protocol.information_missing.ReplaceMatchFieldType}
	 * 
	 * @param replaceMatchFieldType Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ReplaceMatchFieldType replaceMatchFieldType) throws SerializerException {
		return this.objToXml(ReplaceMatchFieldType.class, replaceMatchFieldType, false).toString();
	}
	/**
	 * Serialize to String the object <var>replaceMatchFieldType</var> of type {@link org.openspcoop2.protocol.information_missing.ReplaceMatchFieldType}
	 * 
	 * @param replaceMatchFieldType Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ReplaceMatchFieldType replaceMatchFieldType,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ReplaceMatchFieldType.class, replaceMatchFieldType, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: Wizard
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>wizard</var> of type {@link org.openspcoop2.protocol.information_missing.Wizard}
	 * 
	 * @param fileName Xml file to serialize the object <var>wizard</var>
	 * @param wizard Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Wizard wizard) throws SerializerException {
		this.objToXml(fileName, Wizard.class, wizard, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>wizard</var> of type {@link org.openspcoop2.protocol.information_missing.Wizard}
	 * 
	 * @param fileName Xml file to serialize the object <var>wizard</var>
	 * @param wizard Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Wizard wizard,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Wizard.class, wizard, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>wizard</var> of type {@link org.openspcoop2.protocol.information_missing.Wizard}
	 * 
	 * @param file Xml file to serialize the object <var>wizard</var>
	 * @param wizard Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Wizard wizard) throws SerializerException {
		this.objToXml(file, Wizard.class, wizard, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>wizard</var> of type {@link org.openspcoop2.protocol.information_missing.Wizard}
	 * 
	 * @param file Xml file to serialize the object <var>wizard</var>
	 * @param wizard Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Wizard wizard,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Wizard.class, wizard, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>wizard</var> of type {@link org.openspcoop2.protocol.information_missing.Wizard}
	 * 
	 * @param out OutputStream to serialize the object <var>wizard</var>
	 * @param wizard Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Wizard wizard) throws SerializerException {
		this.objToXml(out, Wizard.class, wizard, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>wizard</var> of type {@link org.openspcoop2.protocol.information_missing.Wizard}
	 * 
	 * @param out OutputStream to serialize the object <var>wizard</var>
	 * @param wizard Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Wizard wizard,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Wizard.class, wizard, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>wizard</var> of type {@link org.openspcoop2.protocol.information_missing.Wizard}
	 * 
	 * @param wizard Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Wizard wizard) throws SerializerException {
		return this.objToXml(Wizard.class, wizard, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>wizard</var> of type {@link org.openspcoop2.protocol.information_missing.Wizard}
	 * 
	 * @param wizard Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Wizard wizard,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Wizard.class, wizard, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>wizard</var> of type {@link org.openspcoop2.protocol.information_missing.Wizard}
	 * 
	 * @param wizard Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Wizard wizard) throws SerializerException {
		return this.objToXml(Wizard.class, wizard, false).toString();
	}
	/**
	 * Serialize to String the object <var>wizard</var> of type {@link org.openspcoop2.protocol.information_missing.Wizard}
	 * 
	 * @param wizard Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Wizard wizard,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Wizard.class, wizard, prettyPrint).toString();
	}
	
	
	

}
