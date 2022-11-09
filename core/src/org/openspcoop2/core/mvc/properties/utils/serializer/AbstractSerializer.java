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
package org.openspcoop2.core.mvc.properties.utils.serializer;

import org.openspcoop2.generic_project.exception.SerializerException;
import org.openspcoop2.utils.beans.WriteToSerializerType;
import org.openspcoop2.utils.xml.JaxbUtils;

import org.openspcoop2.core.mvc.properties.Defined;
import org.openspcoop2.core.mvc.properties.Conditions;
import org.openspcoop2.core.mvc.properties.Subsection;
import org.openspcoop2.core.mvc.properties.Item;
import org.openspcoop2.core.mvc.properties.Selected;
import org.openspcoop2.core.mvc.properties.ItemValue;
import org.openspcoop2.core.mvc.properties.ItemValues;
import org.openspcoop2.core.mvc.properties.Equals;
import org.openspcoop2.core.mvc.properties.Compatibility;
import org.openspcoop2.core.mvc.properties.Config;
import org.openspcoop2.core.mvc.properties.Properties;
import org.openspcoop2.core.mvc.properties.Section;
import org.openspcoop2.core.mvc.properties.Property;
import org.openspcoop2.core.mvc.properties.Tags;
import org.openspcoop2.core.mvc.properties.Collection;
import org.openspcoop2.core.mvc.properties.Condition;

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
				fout.flush();
			}catch(Exception e){
				// ignore
			}
			try{
				fout.close();
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
				bout.flush();
			}catch(Exception e){
				// ignore
			}
			try{
				bout.close();
			}catch(Exception e){
				// ignore
			}
		}
		return bout;
	}




	/*
	 =================================================================================
	 Object: defined
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>defined</var> of type {@link org.openspcoop2.core.mvc.properties.Defined}
	 * 
	 * @param fileName Xml file to serialize the object <var>defined</var>
	 * @param defined Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Defined defined) throws SerializerException {
		this.objToXml(fileName, Defined.class, defined, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>defined</var> of type {@link org.openspcoop2.core.mvc.properties.Defined}
	 * 
	 * @param fileName Xml file to serialize the object <var>defined</var>
	 * @param defined Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Defined defined,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Defined.class, defined, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>defined</var> of type {@link org.openspcoop2.core.mvc.properties.Defined}
	 * 
	 * @param file Xml file to serialize the object <var>defined</var>
	 * @param defined Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Defined defined) throws SerializerException {
		this.objToXml(file, Defined.class, defined, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>defined</var> of type {@link org.openspcoop2.core.mvc.properties.Defined}
	 * 
	 * @param file Xml file to serialize the object <var>defined</var>
	 * @param defined Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Defined defined,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Defined.class, defined, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>defined</var> of type {@link org.openspcoop2.core.mvc.properties.Defined}
	 * 
	 * @param out OutputStream to serialize the object <var>defined</var>
	 * @param defined Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Defined defined) throws SerializerException {
		this.objToXml(out, Defined.class, defined, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>defined</var> of type {@link org.openspcoop2.core.mvc.properties.Defined}
	 * 
	 * @param out OutputStream to serialize the object <var>defined</var>
	 * @param defined Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Defined defined,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Defined.class, defined, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>defined</var> of type {@link org.openspcoop2.core.mvc.properties.Defined}
	 * 
	 * @param defined Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Defined defined) throws SerializerException {
		return this.objToXml(Defined.class, defined, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>defined</var> of type {@link org.openspcoop2.core.mvc.properties.Defined}
	 * 
	 * @param defined Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Defined defined,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Defined.class, defined, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>defined</var> of type {@link org.openspcoop2.core.mvc.properties.Defined}
	 * 
	 * @param defined Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Defined defined) throws SerializerException {
		return this.objToXml(Defined.class, defined, false).toString();
	}
	/**
	 * Serialize to String the object <var>defined</var> of type {@link org.openspcoop2.core.mvc.properties.Defined}
	 * 
	 * @param defined Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Defined defined,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Defined.class, defined, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: conditions
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>conditions</var> of type {@link org.openspcoop2.core.mvc.properties.Conditions}
	 * 
	 * @param fileName Xml file to serialize the object <var>conditions</var>
	 * @param conditions Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Conditions conditions) throws SerializerException {
		this.objToXml(fileName, Conditions.class, conditions, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>conditions</var> of type {@link org.openspcoop2.core.mvc.properties.Conditions}
	 * 
	 * @param fileName Xml file to serialize the object <var>conditions</var>
	 * @param conditions Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Conditions conditions,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Conditions.class, conditions, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>conditions</var> of type {@link org.openspcoop2.core.mvc.properties.Conditions}
	 * 
	 * @param file Xml file to serialize the object <var>conditions</var>
	 * @param conditions Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Conditions conditions) throws SerializerException {
		this.objToXml(file, Conditions.class, conditions, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>conditions</var> of type {@link org.openspcoop2.core.mvc.properties.Conditions}
	 * 
	 * @param file Xml file to serialize the object <var>conditions</var>
	 * @param conditions Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Conditions conditions,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Conditions.class, conditions, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>conditions</var> of type {@link org.openspcoop2.core.mvc.properties.Conditions}
	 * 
	 * @param out OutputStream to serialize the object <var>conditions</var>
	 * @param conditions Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Conditions conditions) throws SerializerException {
		this.objToXml(out, Conditions.class, conditions, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>conditions</var> of type {@link org.openspcoop2.core.mvc.properties.Conditions}
	 * 
	 * @param out OutputStream to serialize the object <var>conditions</var>
	 * @param conditions Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Conditions conditions,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Conditions.class, conditions, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>conditions</var> of type {@link org.openspcoop2.core.mvc.properties.Conditions}
	 * 
	 * @param conditions Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Conditions conditions) throws SerializerException {
		return this.objToXml(Conditions.class, conditions, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>conditions</var> of type {@link org.openspcoop2.core.mvc.properties.Conditions}
	 * 
	 * @param conditions Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Conditions conditions,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Conditions.class, conditions, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>conditions</var> of type {@link org.openspcoop2.core.mvc.properties.Conditions}
	 * 
	 * @param conditions Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Conditions conditions) throws SerializerException {
		return this.objToXml(Conditions.class, conditions, false).toString();
	}
	/**
	 * Serialize to String the object <var>conditions</var> of type {@link org.openspcoop2.core.mvc.properties.Conditions}
	 * 
	 * @param conditions Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Conditions conditions,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Conditions.class, conditions, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: subsection
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>subsection</var> of type {@link org.openspcoop2.core.mvc.properties.Subsection}
	 * 
	 * @param fileName Xml file to serialize the object <var>subsection</var>
	 * @param subsection Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Subsection subsection) throws SerializerException {
		this.objToXml(fileName, Subsection.class, subsection, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>subsection</var> of type {@link org.openspcoop2.core.mvc.properties.Subsection}
	 * 
	 * @param fileName Xml file to serialize the object <var>subsection</var>
	 * @param subsection Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Subsection subsection,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Subsection.class, subsection, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>subsection</var> of type {@link org.openspcoop2.core.mvc.properties.Subsection}
	 * 
	 * @param file Xml file to serialize the object <var>subsection</var>
	 * @param subsection Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Subsection subsection) throws SerializerException {
		this.objToXml(file, Subsection.class, subsection, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>subsection</var> of type {@link org.openspcoop2.core.mvc.properties.Subsection}
	 * 
	 * @param file Xml file to serialize the object <var>subsection</var>
	 * @param subsection Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Subsection subsection,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Subsection.class, subsection, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>subsection</var> of type {@link org.openspcoop2.core.mvc.properties.Subsection}
	 * 
	 * @param out OutputStream to serialize the object <var>subsection</var>
	 * @param subsection Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Subsection subsection) throws SerializerException {
		this.objToXml(out, Subsection.class, subsection, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>subsection</var> of type {@link org.openspcoop2.core.mvc.properties.Subsection}
	 * 
	 * @param out OutputStream to serialize the object <var>subsection</var>
	 * @param subsection Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Subsection subsection,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Subsection.class, subsection, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>subsection</var> of type {@link org.openspcoop2.core.mvc.properties.Subsection}
	 * 
	 * @param subsection Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Subsection subsection) throws SerializerException {
		return this.objToXml(Subsection.class, subsection, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>subsection</var> of type {@link org.openspcoop2.core.mvc.properties.Subsection}
	 * 
	 * @param subsection Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Subsection subsection,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Subsection.class, subsection, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>subsection</var> of type {@link org.openspcoop2.core.mvc.properties.Subsection}
	 * 
	 * @param subsection Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Subsection subsection) throws SerializerException {
		return this.objToXml(Subsection.class, subsection, false).toString();
	}
	/**
	 * Serialize to String the object <var>subsection</var> of type {@link org.openspcoop2.core.mvc.properties.Subsection}
	 * 
	 * @param subsection Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Subsection subsection,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Subsection.class, subsection, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: item
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>item</var> of type {@link org.openspcoop2.core.mvc.properties.Item}
	 * 
	 * @param fileName Xml file to serialize the object <var>item</var>
	 * @param item Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Item item) throws SerializerException {
		this.objToXml(fileName, Item.class, item, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>item</var> of type {@link org.openspcoop2.core.mvc.properties.Item}
	 * 
	 * @param fileName Xml file to serialize the object <var>item</var>
	 * @param item Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Item item,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Item.class, item, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>item</var> of type {@link org.openspcoop2.core.mvc.properties.Item}
	 * 
	 * @param file Xml file to serialize the object <var>item</var>
	 * @param item Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Item item) throws SerializerException {
		this.objToXml(file, Item.class, item, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>item</var> of type {@link org.openspcoop2.core.mvc.properties.Item}
	 * 
	 * @param file Xml file to serialize the object <var>item</var>
	 * @param item Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Item item,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Item.class, item, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>item</var> of type {@link org.openspcoop2.core.mvc.properties.Item}
	 * 
	 * @param out OutputStream to serialize the object <var>item</var>
	 * @param item Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Item item) throws SerializerException {
		this.objToXml(out, Item.class, item, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>item</var> of type {@link org.openspcoop2.core.mvc.properties.Item}
	 * 
	 * @param out OutputStream to serialize the object <var>item</var>
	 * @param item Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Item item,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Item.class, item, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>item</var> of type {@link org.openspcoop2.core.mvc.properties.Item}
	 * 
	 * @param item Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Item item) throws SerializerException {
		return this.objToXml(Item.class, item, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>item</var> of type {@link org.openspcoop2.core.mvc.properties.Item}
	 * 
	 * @param item Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Item item,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Item.class, item, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>item</var> of type {@link org.openspcoop2.core.mvc.properties.Item}
	 * 
	 * @param item Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Item item) throws SerializerException {
		return this.objToXml(Item.class, item, false).toString();
	}
	/**
	 * Serialize to String the object <var>item</var> of type {@link org.openspcoop2.core.mvc.properties.Item}
	 * 
	 * @param item Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Item item,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Item.class, item, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: selected
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>selected</var> of type {@link org.openspcoop2.core.mvc.properties.Selected}
	 * 
	 * @param fileName Xml file to serialize the object <var>selected</var>
	 * @param selected Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Selected selected) throws SerializerException {
		this.objToXml(fileName, Selected.class, selected, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>selected</var> of type {@link org.openspcoop2.core.mvc.properties.Selected}
	 * 
	 * @param fileName Xml file to serialize the object <var>selected</var>
	 * @param selected Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Selected selected,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Selected.class, selected, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>selected</var> of type {@link org.openspcoop2.core.mvc.properties.Selected}
	 * 
	 * @param file Xml file to serialize the object <var>selected</var>
	 * @param selected Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Selected selected) throws SerializerException {
		this.objToXml(file, Selected.class, selected, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>selected</var> of type {@link org.openspcoop2.core.mvc.properties.Selected}
	 * 
	 * @param file Xml file to serialize the object <var>selected</var>
	 * @param selected Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Selected selected,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Selected.class, selected, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>selected</var> of type {@link org.openspcoop2.core.mvc.properties.Selected}
	 * 
	 * @param out OutputStream to serialize the object <var>selected</var>
	 * @param selected Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Selected selected) throws SerializerException {
		this.objToXml(out, Selected.class, selected, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>selected</var> of type {@link org.openspcoop2.core.mvc.properties.Selected}
	 * 
	 * @param out OutputStream to serialize the object <var>selected</var>
	 * @param selected Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Selected selected,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Selected.class, selected, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>selected</var> of type {@link org.openspcoop2.core.mvc.properties.Selected}
	 * 
	 * @param selected Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Selected selected) throws SerializerException {
		return this.objToXml(Selected.class, selected, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>selected</var> of type {@link org.openspcoop2.core.mvc.properties.Selected}
	 * 
	 * @param selected Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Selected selected,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Selected.class, selected, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>selected</var> of type {@link org.openspcoop2.core.mvc.properties.Selected}
	 * 
	 * @param selected Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Selected selected) throws SerializerException {
		return this.objToXml(Selected.class, selected, false).toString();
	}
	/**
	 * Serialize to String the object <var>selected</var> of type {@link org.openspcoop2.core.mvc.properties.Selected}
	 * 
	 * @param selected Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Selected selected,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Selected.class, selected, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: itemValue
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>itemValue</var> of type {@link org.openspcoop2.core.mvc.properties.ItemValue}
	 * 
	 * @param fileName Xml file to serialize the object <var>itemValue</var>
	 * @param itemValue Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ItemValue itemValue) throws SerializerException {
		this.objToXml(fileName, ItemValue.class, itemValue, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>itemValue</var> of type {@link org.openspcoop2.core.mvc.properties.ItemValue}
	 * 
	 * @param fileName Xml file to serialize the object <var>itemValue</var>
	 * @param itemValue Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ItemValue itemValue,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, ItemValue.class, itemValue, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>itemValue</var> of type {@link org.openspcoop2.core.mvc.properties.ItemValue}
	 * 
	 * @param file Xml file to serialize the object <var>itemValue</var>
	 * @param itemValue Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ItemValue itemValue) throws SerializerException {
		this.objToXml(file, ItemValue.class, itemValue, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>itemValue</var> of type {@link org.openspcoop2.core.mvc.properties.ItemValue}
	 * 
	 * @param file Xml file to serialize the object <var>itemValue</var>
	 * @param itemValue Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ItemValue itemValue,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, ItemValue.class, itemValue, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>itemValue</var> of type {@link org.openspcoop2.core.mvc.properties.ItemValue}
	 * 
	 * @param out OutputStream to serialize the object <var>itemValue</var>
	 * @param itemValue Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ItemValue itemValue) throws SerializerException {
		this.objToXml(out, ItemValue.class, itemValue, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>itemValue</var> of type {@link org.openspcoop2.core.mvc.properties.ItemValue}
	 * 
	 * @param out OutputStream to serialize the object <var>itemValue</var>
	 * @param itemValue Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ItemValue itemValue,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, ItemValue.class, itemValue, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>itemValue</var> of type {@link org.openspcoop2.core.mvc.properties.ItemValue}
	 * 
	 * @param itemValue Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ItemValue itemValue) throws SerializerException {
		return this.objToXml(ItemValue.class, itemValue, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>itemValue</var> of type {@link org.openspcoop2.core.mvc.properties.ItemValue}
	 * 
	 * @param itemValue Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ItemValue itemValue,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ItemValue.class, itemValue, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>itemValue</var> of type {@link org.openspcoop2.core.mvc.properties.ItemValue}
	 * 
	 * @param itemValue Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ItemValue itemValue) throws SerializerException {
		return this.objToXml(ItemValue.class, itemValue, false).toString();
	}
	/**
	 * Serialize to String the object <var>itemValue</var> of type {@link org.openspcoop2.core.mvc.properties.ItemValue}
	 * 
	 * @param itemValue Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ItemValue itemValue,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ItemValue.class, itemValue, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: itemValues
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>itemValues</var> of type {@link org.openspcoop2.core.mvc.properties.ItemValues}
	 * 
	 * @param fileName Xml file to serialize the object <var>itemValues</var>
	 * @param itemValues Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ItemValues itemValues) throws SerializerException {
		this.objToXml(fileName, ItemValues.class, itemValues, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>itemValues</var> of type {@link org.openspcoop2.core.mvc.properties.ItemValues}
	 * 
	 * @param fileName Xml file to serialize the object <var>itemValues</var>
	 * @param itemValues Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,ItemValues itemValues,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, ItemValues.class, itemValues, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>itemValues</var> of type {@link org.openspcoop2.core.mvc.properties.ItemValues}
	 * 
	 * @param file Xml file to serialize the object <var>itemValues</var>
	 * @param itemValues Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ItemValues itemValues) throws SerializerException {
		this.objToXml(file, ItemValues.class, itemValues, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>itemValues</var> of type {@link org.openspcoop2.core.mvc.properties.ItemValues}
	 * 
	 * @param file Xml file to serialize the object <var>itemValues</var>
	 * @param itemValues Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,ItemValues itemValues,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, ItemValues.class, itemValues, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>itemValues</var> of type {@link org.openspcoop2.core.mvc.properties.ItemValues}
	 * 
	 * @param out OutputStream to serialize the object <var>itemValues</var>
	 * @param itemValues Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ItemValues itemValues) throws SerializerException {
		this.objToXml(out, ItemValues.class, itemValues, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>itemValues</var> of type {@link org.openspcoop2.core.mvc.properties.ItemValues}
	 * 
	 * @param out OutputStream to serialize the object <var>itemValues</var>
	 * @param itemValues Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,ItemValues itemValues,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, ItemValues.class, itemValues, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>itemValues</var> of type {@link org.openspcoop2.core.mvc.properties.ItemValues}
	 * 
	 * @param itemValues Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ItemValues itemValues) throws SerializerException {
		return this.objToXml(ItemValues.class, itemValues, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>itemValues</var> of type {@link org.openspcoop2.core.mvc.properties.ItemValues}
	 * 
	 * @param itemValues Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(ItemValues itemValues,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ItemValues.class, itemValues, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>itemValues</var> of type {@link org.openspcoop2.core.mvc.properties.ItemValues}
	 * 
	 * @param itemValues Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ItemValues itemValues) throws SerializerException {
		return this.objToXml(ItemValues.class, itemValues, false).toString();
	}
	/**
	 * Serialize to String the object <var>itemValues</var> of type {@link org.openspcoop2.core.mvc.properties.ItemValues}
	 * 
	 * @param itemValues Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(ItemValues itemValues,boolean prettyPrint) throws SerializerException {
		return this.objToXml(ItemValues.class, itemValues, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: equals
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>equals</var> of type {@link org.openspcoop2.core.mvc.properties.Equals}
	 * 
	 * @param fileName Xml file to serialize the object <var>equals</var>
	 * @param equals Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Equals equals) throws SerializerException {
		this.objToXml(fileName, Equals.class, equals, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>equals</var> of type {@link org.openspcoop2.core.mvc.properties.Equals}
	 * 
	 * @param fileName Xml file to serialize the object <var>equals</var>
	 * @param equals Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Equals equals,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Equals.class, equals, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>equals</var> of type {@link org.openspcoop2.core.mvc.properties.Equals}
	 * 
	 * @param file Xml file to serialize the object <var>equals</var>
	 * @param equals Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Equals equals) throws SerializerException {
		this.objToXml(file, Equals.class, equals, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>equals</var> of type {@link org.openspcoop2.core.mvc.properties.Equals}
	 * 
	 * @param file Xml file to serialize the object <var>equals</var>
	 * @param equals Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Equals equals,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Equals.class, equals, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>equals</var> of type {@link org.openspcoop2.core.mvc.properties.Equals}
	 * 
	 * @param out OutputStream to serialize the object <var>equals</var>
	 * @param equals Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Equals equals) throws SerializerException {
		this.objToXml(out, Equals.class, equals, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>equals</var> of type {@link org.openspcoop2.core.mvc.properties.Equals}
	 * 
	 * @param out OutputStream to serialize the object <var>equals</var>
	 * @param equals Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Equals equals,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Equals.class, equals, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>equals</var> of type {@link org.openspcoop2.core.mvc.properties.Equals}
	 * 
	 * @param equals Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Equals equals) throws SerializerException {
		return this.objToXml(Equals.class, equals, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>equals</var> of type {@link org.openspcoop2.core.mvc.properties.Equals}
	 * 
	 * @param equals Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Equals equals,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Equals.class, equals, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>equals</var> of type {@link org.openspcoop2.core.mvc.properties.Equals}
	 * 
	 * @param equals Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Equals equals) throws SerializerException {
		return this.objToXml(Equals.class, equals, false).toString();
	}
	/**
	 * Serialize to String the object <var>equals</var> of type {@link org.openspcoop2.core.mvc.properties.Equals}
	 * 
	 * @param equals Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Equals equals,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Equals.class, equals, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: compatibility
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>compatibility</var> of type {@link org.openspcoop2.core.mvc.properties.Compatibility}
	 * 
	 * @param fileName Xml file to serialize the object <var>compatibility</var>
	 * @param compatibility Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Compatibility compatibility) throws SerializerException {
		this.objToXml(fileName, Compatibility.class, compatibility, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>compatibility</var> of type {@link org.openspcoop2.core.mvc.properties.Compatibility}
	 * 
	 * @param fileName Xml file to serialize the object <var>compatibility</var>
	 * @param compatibility Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Compatibility compatibility,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Compatibility.class, compatibility, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>compatibility</var> of type {@link org.openspcoop2.core.mvc.properties.Compatibility}
	 * 
	 * @param file Xml file to serialize the object <var>compatibility</var>
	 * @param compatibility Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Compatibility compatibility) throws SerializerException {
		this.objToXml(file, Compatibility.class, compatibility, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>compatibility</var> of type {@link org.openspcoop2.core.mvc.properties.Compatibility}
	 * 
	 * @param file Xml file to serialize the object <var>compatibility</var>
	 * @param compatibility Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Compatibility compatibility,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Compatibility.class, compatibility, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>compatibility</var> of type {@link org.openspcoop2.core.mvc.properties.Compatibility}
	 * 
	 * @param out OutputStream to serialize the object <var>compatibility</var>
	 * @param compatibility Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Compatibility compatibility) throws SerializerException {
		this.objToXml(out, Compatibility.class, compatibility, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>compatibility</var> of type {@link org.openspcoop2.core.mvc.properties.Compatibility}
	 * 
	 * @param out OutputStream to serialize the object <var>compatibility</var>
	 * @param compatibility Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Compatibility compatibility,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Compatibility.class, compatibility, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>compatibility</var> of type {@link org.openspcoop2.core.mvc.properties.Compatibility}
	 * 
	 * @param compatibility Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Compatibility compatibility) throws SerializerException {
		return this.objToXml(Compatibility.class, compatibility, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>compatibility</var> of type {@link org.openspcoop2.core.mvc.properties.Compatibility}
	 * 
	 * @param compatibility Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Compatibility compatibility,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Compatibility.class, compatibility, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>compatibility</var> of type {@link org.openspcoop2.core.mvc.properties.Compatibility}
	 * 
	 * @param compatibility Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Compatibility compatibility) throws SerializerException {
		return this.objToXml(Compatibility.class, compatibility, false).toString();
	}
	/**
	 * Serialize to String the object <var>compatibility</var> of type {@link org.openspcoop2.core.mvc.properties.Compatibility}
	 * 
	 * @param compatibility Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Compatibility compatibility,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Compatibility.class, compatibility, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: config
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>config</var> of type {@link org.openspcoop2.core.mvc.properties.Config}
	 * 
	 * @param fileName Xml file to serialize the object <var>config</var>
	 * @param config Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Config config) throws SerializerException {
		this.objToXml(fileName, Config.class, config, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>config</var> of type {@link org.openspcoop2.core.mvc.properties.Config}
	 * 
	 * @param fileName Xml file to serialize the object <var>config</var>
	 * @param config Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Config config,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Config.class, config, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>config</var> of type {@link org.openspcoop2.core.mvc.properties.Config}
	 * 
	 * @param file Xml file to serialize the object <var>config</var>
	 * @param config Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Config config) throws SerializerException {
		this.objToXml(file, Config.class, config, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>config</var> of type {@link org.openspcoop2.core.mvc.properties.Config}
	 * 
	 * @param file Xml file to serialize the object <var>config</var>
	 * @param config Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Config config,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Config.class, config, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>config</var> of type {@link org.openspcoop2.core.mvc.properties.Config}
	 * 
	 * @param out OutputStream to serialize the object <var>config</var>
	 * @param config Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Config config) throws SerializerException {
		this.objToXml(out, Config.class, config, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>config</var> of type {@link org.openspcoop2.core.mvc.properties.Config}
	 * 
	 * @param out OutputStream to serialize the object <var>config</var>
	 * @param config Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Config config,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Config.class, config, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>config</var> of type {@link org.openspcoop2.core.mvc.properties.Config}
	 * 
	 * @param config Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Config config) throws SerializerException {
		return this.objToXml(Config.class, config, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>config</var> of type {@link org.openspcoop2.core.mvc.properties.Config}
	 * 
	 * @param config Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Config config,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Config.class, config, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>config</var> of type {@link org.openspcoop2.core.mvc.properties.Config}
	 * 
	 * @param config Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Config config) throws SerializerException {
		return this.objToXml(Config.class, config, false).toString();
	}
	/**
	 * Serialize to String the object <var>config</var> of type {@link org.openspcoop2.core.mvc.properties.Config}
	 * 
	 * @param config Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Config config,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Config.class, config, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: properties
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>properties</var> of type {@link org.openspcoop2.core.mvc.properties.Properties}
	 * 
	 * @param fileName Xml file to serialize the object <var>properties</var>
	 * @param properties Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Properties properties) throws SerializerException {
		this.objToXml(fileName, Properties.class, properties, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>properties</var> of type {@link org.openspcoop2.core.mvc.properties.Properties}
	 * 
	 * @param fileName Xml file to serialize the object <var>properties</var>
	 * @param properties Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Properties properties,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Properties.class, properties, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>properties</var> of type {@link org.openspcoop2.core.mvc.properties.Properties}
	 * 
	 * @param file Xml file to serialize the object <var>properties</var>
	 * @param properties Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Properties properties) throws SerializerException {
		this.objToXml(file, Properties.class, properties, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>properties</var> of type {@link org.openspcoop2.core.mvc.properties.Properties}
	 * 
	 * @param file Xml file to serialize the object <var>properties</var>
	 * @param properties Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Properties properties,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Properties.class, properties, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>properties</var> of type {@link org.openspcoop2.core.mvc.properties.Properties}
	 * 
	 * @param out OutputStream to serialize the object <var>properties</var>
	 * @param properties Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Properties properties) throws SerializerException {
		this.objToXml(out, Properties.class, properties, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>properties</var> of type {@link org.openspcoop2.core.mvc.properties.Properties}
	 * 
	 * @param out OutputStream to serialize the object <var>properties</var>
	 * @param properties Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Properties properties,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Properties.class, properties, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>properties</var> of type {@link org.openspcoop2.core.mvc.properties.Properties}
	 * 
	 * @param properties Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Properties properties) throws SerializerException {
		return this.objToXml(Properties.class, properties, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>properties</var> of type {@link org.openspcoop2.core.mvc.properties.Properties}
	 * 
	 * @param properties Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Properties properties,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Properties.class, properties, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>properties</var> of type {@link org.openspcoop2.core.mvc.properties.Properties}
	 * 
	 * @param properties Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Properties properties) throws SerializerException {
		return this.objToXml(Properties.class, properties, false).toString();
	}
	/**
	 * Serialize to String the object <var>properties</var> of type {@link org.openspcoop2.core.mvc.properties.Properties}
	 * 
	 * @param properties Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Properties properties,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Properties.class, properties, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: section
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>section</var> of type {@link org.openspcoop2.core.mvc.properties.Section}
	 * 
	 * @param fileName Xml file to serialize the object <var>section</var>
	 * @param section Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Section section) throws SerializerException {
		this.objToXml(fileName, Section.class, section, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>section</var> of type {@link org.openspcoop2.core.mvc.properties.Section}
	 * 
	 * @param fileName Xml file to serialize the object <var>section</var>
	 * @param section Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Section section,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Section.class, section, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>section</var> of type {@link org.openspcoop2.core.mvc.properties.Section}
	 * 
	 * @param file Xml file to serialize the object <var>section</var>
	 * @param section Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Section section) throws SerializerException {
		this.objToXml(file, Section.class, section, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>section</var> of type {@link org.openspcoop2.core.mvc.properties.Section}
	 * 
	 * @param file Xml file to serialize the object <var>section</var>
	 * @param section Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Section section,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Section.class, section, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>section</var> of type {@link org.openspcoop2.core.mvc.properties.Section}
	 * 
	 * @param out OutputStream to serialize the object <var>section</var>
	 * @param section Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Section section) throws SerializerException {
		this.objToXml(out, Section.class, section, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>section</var> of type {@link org.openspcoop2.core.mvc.properties.Section}
	 * 
	 * @param out OutputStream to serialize the object <var>section</var>
	 * @param section Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Section section,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Section.class, section, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>section</var> of type {@link org.openspcoop2.core.mvc.properties.Section}
	 * 
	 * @param section Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Section section) throws SerializerException {
		return this.objToXml(Section.class, section, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>section</var> of type {@link org.openspcoop2.core.mvc.properties.Section}
	 * 
	 * @param section Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Section section,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Section.class, section, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>section</var> of type {@link org.openspcoop2.core.mvc.properties.Section}
	 * 
	 * @param section Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Section section) throws SerializerException {
		return this.objToXml(Section.class, section, false).toString();
	}
	/**
	 * Serialize to String the object <var>section</var> of type {@link org.openspcoop2.core.mvc.properties.Section}
	 * 
	 * @param section Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Section section,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Section.class, section, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: property
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>property</var> of type {@link org.openspcoop2.core.mvc.properties.Property}
	 * 
	 * @param fileName Xml file to serialize the object <var>property</var>
	 * @param property Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Property property) throws SerializerException {
		this.objToXml(fileName, Property.class, property, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>property</var> of type {@link org.openspcoop2.core.mvc.properties.Property}
	 * 
	 * @param fileName Xml file to serialize the object <var>property</var>
	 * @param property Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Property property,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Property.class, property, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>property</var> of type {@link org.openspcoop2.core.mvc.properties.Property}
	 * 
	 * @param file Xml file to serialize the object <var>property</var>
	 * @param property Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Property property) throws SerializerException {
		this.objToXml(file, Property.class, property, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>property</var> of type {@link org.openspcoop2.core.mvc.properties.Property}
	 * 
	 * @param file Xml file to serialize the object <var>property</var>
	 * @param property Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Property property,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Property.class, property, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>property</var> of type {@link org.openspcoop2.core.mvc.properties.Property}
	 * 
	 * @param out OutputStream to serialize the object <var>property</var>
	 * @param property Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Property property) throws SerializerException {
		this.objToXml(out, Property.class, property, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>property</var> of type {@link org.openspcoop2.core.mvc.properties.Property}
	 * 
	 * @param out OutputStream to serialize the object <var>property</var>
	 * @param property Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Property property,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Property.class, property, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>property</var> of type {@link org.openspcoop2.core.mvc.properties.Property}
	 * 
	 * @param property Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Property property) throws SerializerException {
		return this.objToXml(Property.class, property, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>property</var> of type {@link org.openspcoop2.core.mvc.properties.Property}
	 * 
	 * @param property Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Property property,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Property.class, property, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>property</var> of type {@link org.openspcoop2.core.mvc.properties.Property}
	 * 
	 * @param property Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Property property) throws SerializerException {
		return this.objToXml(Property.class, property, false).toString();
	}
	/**
	 * Serialize to String the object <var>property</var> of type {@link org.openspcoop2.core.mvc.properties.Property}
	 * 
	 * @param property Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Property property,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Property.class, property, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: tags
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>tags</var> of type {@link org.openspcoop2.core.mvc.properties.Tags}
	 * 
	 * @param fileName Xml file to serialize the object <var>tags</var>
	 * @param tags Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Tags tags) throws SerializerException {
		this.objToXml(fileName, Tags.class, tags, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>tags</var> of type {@link org.openspcoop2.core.mvc.properties.Tags}
	 * 
	 * @param fileName Xml file to serialize the object <var>tags</var>
	 * @param tags Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Tags tags,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Tags.class, tags, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>tags</var> of type {@link org.openspcoop2.core.mvc.properties.Tags}
	 * 
	 * @param file Xml file to serialize the object <var>tags</var>
	 * @param tags Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Tags tags) throws SerializerException {
		this.objToXml(file, Tags.class, tags, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>tags</var> of type {@link org.openspcoop2.core.mvc.properties.Tags}
	 * 
	 * @param file Xml file to serialize the object <var>tags</var>
	 * @param tags Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Tags tags,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Tags.class, tags, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>tags</var> of type {@link org.openspcoop2.core.mvc.properties.Tags}
	 * 
	 * @param out OutputStream to serialize the object <var>tags</var>
	 * @param tags Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Tags tags) throws SerializerException {
		this.objToXml(out, Tags.class, tags, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>tags</var> of type {@link org.openspcoop2.core.mvc.properties.Tags}
	 * 
	 * @param out OutputStream to serialize the object <var>tags</var>
	 * @param tags Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Tags tags,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Tags.class, tags, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>tags</var> of type {@link org.openspcoop2.core.mvc.properties.Tags}
	 * 
	 * @param tags Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Tags tags) throws SerializerException {
		return this.objToXml(Tags.class, tags, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>tags</var> of type {@link org.openspcoop2.core.mvc.properties.Tags}
	 * 
	 * @param tags Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Tags tags,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Tags.class, tags, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>tags</var> of type {@link org.openspcoop2.core.mvc.properties.Tags}
	 * 
	 * @param tags Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Tags tags) throws SerializerException {
		return this.objToXml(Tags.class, tags, false).toString();
	}
	/**
	 * Serialize to String the object <var>tags</var> of type {@link org.openspcoop2.core.mvc.properties.Tags}
	 * 
	 * @param tags Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Tags tags,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Tags.class, tags, prettyPrint).toString();
	}
	
	
	
	/*
	 =================================================================================
	 Object: collection
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>collection</var> of type {@link org.openspcoop2.core.mvc.properties.Collection}
	 * 
	 * @param fileName Xml file to serialize the object <var>collection</var>
	 * @param collection Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Collection collection) throws SerializerException {
		this.objToXml(fileName, Collection.class, collection, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>collection</var> of type {@link org.openspcoop2.core.mvc.properties.Collection}
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
	 * Serialize to file system in <var>file</var> the object <var>collection</var> of type {@link org.openspcoop2.core.mvc.properties.Collection}
	 * 
	 * @param file Xml file to serialize the object <var>collection</var>
	 * @param collection Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Collection collection) throws SerializerException {
		this.objToXml(file, Collection.class, collection, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>collection</var> of type {@link org.openspcoop2.core.mvc.properties.Collection}
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
	 * Serialize to output stream <var>out</var> the object <var>collection</var> of type {@link org.openspcoop2.core.mvc.properties.Collection}
	 * 
	 * @param out OutputStream to serialize the object <var>collection</var>
	 * @param collection Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Collection collection) throws SerializerException {
		this.objToXml(out, Collection.class, collection, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>collection</var> of type {@link org.openspcoop2.core.mvc.properties.Collection}
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
	 * Serialize to byte array the object <var>collection</var> of type {@link org.openspcoop2.core.mvc.properties.Collection}
	 * 
	 * @param collection Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Collection collection) throws SerializerException {
		return this.objToXml(Collection.class, collection, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>collection</var> of type {@link org.openspcoop2.core.mvc.properties.Collection}
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
	 * Serialize to String the object <var>collection</var> of type {@link org.openspcoop2.core.mvc.properties.Collection}
	 * 
	 * @param collection Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Collection collection) throws SerializerException {
		return this.objToXml(Collection.class, collection, false).toString();
	}
	/**
	 * Serialize to String the object <var>collection</var> of type {@link org.openspcoop2.core.mvc.properties.Collection}
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
	 Object: condition
	 =================================================================================
	*/
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>condition</var> of type {@link org.openspcoop2.core.mvc.properties.Condition}
	 * 
	 * @param fileName Xml file to serialize the object <var>condition</var>
	 * @param condition Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Condition condition) throws SerializerException {
		this.objToXml(fileName, Condition.class, condition, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>condition</var> of type {@link org.openspcoop2.core.mvc.properties.Condition}
	 * 
	 * @param fileName Xml file to serialize the object <var>condition</var>
	 * @param condition Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(String fileName,Condition condition,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, Condition.class, condition, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>condition</var> of type {@link org.openspcoop2.core.mvc.properties.Condition}
	 * 
	 * @param file Xml file to serialize the object <var>condition</var>
	 * @param condition Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Condition condition) throws SerializerException {
		this.objToXml(file, Condition.class, condition, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>condition</var> of type {@link org.openspcoop2.core.mvc.properties.Condition}
	 * 
	 * @param file Xml file to serialize the object <var>condition</var>
	 * @param condition Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(File file,Condition condition,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, Condition.class, condition, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>condition</var> of type {@link org.openspcoop2.core.mvc.properties.Condition}
	 * 
	 * @param out OutputStream to serialize the object <var>condition</var>
	 * @param condition Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Condition condition) throws SerializerException {
		this.objToXml(out, Condition.class, condition, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>condition</var> of type {@link org.openspcoop2.core.mvc.properties.Condition}
	 * 
	 * @param out OutputStream to serialize the object <var>condition</var>
	 * @param condition Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public void write(OutputStream out,Condition condition,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, Condition.class, condition, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>condition</var> of type {@link org.openspcoop2.core.mvc.properties.Condition}
	 * 
	 * @param condition Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Condition condition) throws SerializerException {
		return this.objToXml(Condition.class, condition, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>condition</var> of type {@link org.openspcoop2.core.mvc.properties.Condition}
	 * 
	 * @param condition Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public byte[] toByteArray(Condition condition,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Condition.class, condition, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>condition</var> of type {@link org.openspcoop2.core.mvc.properties.Condition}
	 * 
	 * @param condition Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Condition condition) throws SerializerException {
		return this.objToXml(Condition.class, condition, false).toString();
	}
	/**
	 * Serialize to String the object <var>condition</var> of type {@link org.openspcoop2.core.mvc.properties.Condition}
	 * 
	 * @param condition Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public String toString(Condition condition,boolean prettyPrint) throws SerializerException {
		return this.objToXml(Condition.class, condition, prettyPrint).toString();
	}
	
	
	

}
