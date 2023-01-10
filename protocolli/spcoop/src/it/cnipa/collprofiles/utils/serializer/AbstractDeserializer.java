/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it).
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

import org.openspcoop2.generic_project.exception.DeserializerException;

import it.cnipa.collprofiles.OperationType;
import it.cnipa.collprofiles.OperationListType;
import it.cnipa.collprofiles.EgovDecllElement;

import java.io.InputStream;
import java.io.File;

/**     
 * XML Deserializer of beans
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public abstract class AbstractDeserializer extends org.openspcoop2.generic_project.serializer.AbstractDeserializerBase {



	/*
	 =================================================================================
	 Object: operationType
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link it.cnipa.collprofiles.OperationType}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.cnipa.collprofiles.OperationType}
	 * @return Object type {@link it.cnipa.collprofiles.OperationType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public OperationType readOperationType(String fileName) throws DeserializerException {
		return (OperationType) this.xmlToObj(fileName, OperationType.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.cnipa.collprofiles.OperationType}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.cnipa.collprofiles.OperationType}
	 * @return Object type {@link it.cnipa.collprofiles.OperationType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public OperationType readOperationType(File file) throws DeserializerException {
		return (OperationType) this.xmlToObj(file, OperationType.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.cnipa.collprofiles.OperationType}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.cnipa.collprofiles.OperationType}
	 * @return Object type {@link it.cnipa.collprofiles.OperationType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public OperationType readOperationType(InputStream in) throws DeserializerException {
		return (OperationType) this.xmlToObj(in, OperationType.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.cnipa.collprofiles.OperationType}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.cnipa.collprofiles.OperationType}
	 * @return Object type {@link it.cnipa.collprofiles.OperationType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public OperationType readOperationType(byte[] in) throws DeserializerException {
		return (OperationType) this.xmlToObj(in, OperationType.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.cnipa.collprofiles.OperationType}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.cnipa.collprofiles.OperationType}
	 * @return Object type {@link it.cnipa.collprofiles.OperationType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public OperationType readOperationTypeFromString(String in) throws DeserializerException {
		return (OperationType) this.xmlToObj(in.getBytes(), OperationType.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: operationListType
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link it.cnipa.collprofiles.OperationListType}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.cnipa.collprofiles.OperationListType}
	 * @return Object type {@link it.cnipa.collprofiles.OperationListType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public OperationListType readOperationListType(String fileName) throws DeserializerException {
		return (OperationListType) this.xmlToObj(fileName, OperationListType.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.cnipa.collprofiles.OperationListType}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.cnipa.collprofiles.OperationListType}
	 * @return Object type {@link it.cnipa.collprofiles.OperationListType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public OperationListType readOperationListType(File file) throws DeserializerException {
		return (OperationListType) this.xmlToObj(file, OperationListType.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.cnipa.collprofiles.OperationListType}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.cnipa.collprofiles.OperationListType}
	 * @return Object type {@link it.cnipa.collprofiles.OperationListType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public OperationListType readOperationListType(InputStream in) throws DeserializerException {
		return (OperationListType) this.xmlToObj(in, OperationListType.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.cnipa.collprofiles.OperationListType}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.cnipa.collprofiles.OperationListType}
	 * @return Object type {@link it.cnipa.collprofiles.OperationListType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public OperationListType readOperationListType(byte[] in) throws DeserializerException {
		return (OperationListType) this.xmlToObj(in, OperationListType.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.cnipa.collprofiles.OperationListType}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.cnipa.collprofiles.OperationListType}
	 * @return Object type {@link it.cnipa.collprofiles.OperationListType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public OperationListType readOperationListTypeFromString(String in) throws DeserializerException {
		return (OperationListType) this.xmlToObj(in.getBytes(), OperationListType.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: egovDecllElement
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link it.cnipa.collprofiles.EgovDecllElement}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.cnipa.collprofiles.EgovDecllElement}
	 * @return Object type {@link it.cnipa.collprofiles.EgovDecllElement}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public EgovDecllElement readEgovDecllElement(String fileName) throws DeserializerException {
		return (EgovDecllElement) this.xmlToObj(fileName, EgovDecllElement.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.cnipa.collprofiles.EgovDecllElement}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.cnipa.collprofiles.EgovDecllElement}
	 * @return Object type {@link it.cnipa.collprofiles.EgovDecllElement}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public EgovDecllElement readEgovDecllElement(File file) throws DeserializerException {
		return (EgovDecllElement) this.xmlToObj(file, EgovDecllElement.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.cnipa.collprofiles.EgovDecllElement}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.cnipa.collprofiles.EgovDecllElement}
	 * @return Object type {@link it.cnipa.collprofiles.EgovDecllElement}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public EgovDecllElement readEgovDecllElement(InputStream in) throws DeserializerException {
		return (EgovDecllElement) this.xmlToObj(in, EgovDecllElement.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.cnipa.collprofiles.EgovDecllElement}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.cnipa.collprofiles.EgovDecllElement}
	 * @return Object type {@link it.cnipa.collprofiles.EgovDecllElement}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public EgovDecllElement readEgovDecllElement(byte[] in) throws DeserializerException {
		return (EgovDecllElement) this.xmlToObj(in, EgovDecllElement.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.cnipa.collprofiles.EgovDecllElement}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.cnipa.collprofiles.EgovDecllElement}
	 * @return Object type {@link it.cnipa.collprofiles.EgovDecllElement}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public EgovDecllElement readEgovDecllElementFromString(String in) throws DeserializerException {
		return (EgovDecllElement) this.xmlToObj(in.getBytes(), EgovDecllElement.class);
	}	
	
	
	

}
