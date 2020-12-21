/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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
package org.openspcoop2.core.allarmi.utils.serializer;

import java.io.File;
import java.io.OutputStream;

import org.openspcoop2.utils.beans.WriteToSerializerType;
import org.openspcoop2.generic_project.exception.SerializerException;

import javax.xml.bind.JAXBElement;

/**     
 * XML Serializer of beans with jaxb
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JaxbSerializer extends AbstractSerializer {

	@Override
	protected WriteToSerializerType getType(){
		return WriteToSerializerType.XML_JAXB;
	}
	
	
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>jaxbElement</var> of type {@link javax.xml.bind.JAXBElement}
	 * 
	 * @param fileName Xml file to serialize the object <var>jaxbElement</var>
	 * @param jaxbElement Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public <T> void write(String fileName,JAXBElement<T> jaxbElement) throws SerializerException {
		this.objToXml(fileName, jaxbElement.getValue().getClass(), jaxbElement, false);
	}
	/**
	 * Serialize to file system in <var>fileName</var> the object <var>jaxbElement</var> of type {@link javax.xml.bind.JAXBElement}
	 * 
	 * @param fileName Xml file to serialize the object <var>jaxbElement</var>
	 * @param jaxbElement Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public <T> void write(String fileName,JAXBElement<T> jaxbElement,boolean prettyPrint) throws SerializerException {
		this.objToXml(fileName, jaxbElement.getValue().getClass(), jaxbElement, prettyPrint);
	}
	
	/**
	 * Serialize to file system in <var>file</var> the object <var>jaxbElement</var> of type {@link javax.xml.bind.JAXBElement}
	 * 
	 * @param file Xml file to serialize the object <var>jaxbElement</var>
	 * @param jaxbElement Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public <T> void write(File file,JAXBElement<T> jaxbElement) throws SerializerException {
		this.objToXml(file, jaxbElement.getValue().getClass(), jaxbElement, false);
	}
	/**
	 * Serialize to file system in <var>file</var> the object <var>jaxbElement</var> of type {@link javax.xml.bind.JAXBElement}
	 * 
	 * @param file Xml file to serialize the object <var>jaxbElement</var>
	 * @param jaxbElement Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public <T> void write(File file,JAXBElement<T> jaxbElement,boolean prettyPrint) throws SerializerException {
		this.objToXml(file, jaxbElement.getValue().getClass(), jaxbElement, prettyPrint);
	}
	
	/**
	 * Serialize to output stream <var>out</var> the object <var>jaxbElement</var> of type {@link javax.xml.bind.JAXBElement}
	 * 
	 * @param out OutputStream to serialize the object <var>jaxbElement</var>
	 * @param jaxbElement Object to be serialized in xml file <var>fileName</var>
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public <T> void write(OutputStream out,JAXBElement<T> jaxbElement) throws SerializerException {
		this.objToXml(out, jaxbElement.getValue().getClass(), jaxbElement, false);
	}
	/**
	 * Serialize to output stream <var>out</var> the object <var>jaxbElement</var> of type {@link javax.xml.bind.JAXBElement}
	 * 
	 * @param out OutputStream to serialize the object <var>jaxbElement</var>
	 * @param jaxbElement Object to be serialized in xml file <var>fileName</var>
	 * @param prettyPrint if true output the XML with indenting
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public <T> void write(OutputStream out,JAXBElement<T> jaxbElement,boolean prettyPrint) throws SerializerException {
		this.objToXml(out, jaxbElement.getValue().getClass(), jaxbElement, prettyPrint);
	}
			
	/**
	 * Serialize to byte array the object <var>jaxbElement</var> of type {@link javax.xml.bind.JAXBElement}
	 * 
	 * @param jaxbElement Object to be serialized
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public <T> byte[] toByteArray(JAXBElement<T> jaxbElement) throws SerializerException {
		return this.objToXml(jaxbElement.getValue().getClass(), jaxbElement, false).toByteArray();
	}
	/**
	 * Serialize to byte array the object <var>jaxbElement</var> of type {@link javax.xml.bind.JAXBElement}
	 * 
	 * @param jaxbElement Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized in byte array
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public <T> byte[] toByteArray(JAXBElement<T> jaxbElement,boolean prettyPrint) throws SerializerException {
		return this.objToXml(jaxbElement.getValue().getClass(), jaxbElement, prettyPrint).toByteArray();
	}
	
	/**
	 * Serialize to String the object <var>jaxbElement</var> of type {@link javax.xml.bind.JAXBElement}
	 * 
	 * @param jaxbElement Object to be serialized
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public <T> String toString(JAXBElement<T> jaxbElement) throws SerializerException {
		return this.objToXml(jaxbElement.getValue().getClass(), jaxbElement, false).toString();
	}
	/**
	 * Serialize to String the object <var>jaxbElement</var> of type {@link javax.xml.bind.JAXBElement}
	 * 
	 * @param jaxbElement Object to be serialized
	 * @param prettyPrint if true output the XML with indenting
	 * @return Object to be serialized as String
	 * @throws SerializerException The exception that is thrown when an error occurs during serialization
	 */
	public <T> String toString(JAXBElement<T> jaxbElement,boolean prettyPrint) throws SerializerException {
		return this.objToXml(jaxbElement.getValue().getClass(), jaxbElement, prettyPrint).toString();
	}	
}