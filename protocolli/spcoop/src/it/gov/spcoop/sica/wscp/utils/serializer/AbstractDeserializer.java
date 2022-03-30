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
package it.gov.spcoop.sica.wscp.utils.serializer;

import org.openspcoop2.generic_project.exception.DeserializerException;

import it.gov.spcoop.sica.wscp.OperationType;
import it.gov.spcoop.sica.wscp.OperationListType;
import it.gov.spcoop.sica.wscp.ProfiloCollaborazioneEGOV;

import java.io.InputStream;
import java.io.File;

/**     
 * XML Deserializer of beans
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public abstract class AbstractDeserializer extends org.openspcoop2.generic_project.serializer.AbstractDeserializer {



	/*
	 =================================================================================
	 Object: operationType
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link it.gov.spcoop.sica.wscp.OperationType}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.gov.spcoop.sica.wscp.OperationType}
	 * @return Object type {@link it.gov.spcoop.sica.wscp.OperationType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public OperationType readOperationType(String fileName) throws DeserializerException {
		return (OperationType) this.xmlToObj(fileName, OperationType.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.gov.spcoop.sica.wscp.OperationType}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.gov.spcoop.sica.wscp.OperationType}
	 * @return Object type {@link it.gov.spcoop.sica.wscp.OperationType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public OperationType readOperationType(File file) throws DeserializerException {
		return (OperationType) this.xmlToObj(file, OperationType.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.gov.spcoop.sica.wscp.OperationType}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.gov.spcoop.sica.wscp.OperationType}
	 * @return Object type {@link it.gov.spcoop.sica.wscp.OperationType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public OperationType readOperationType(InputStream in) throws DeserializerException {
		return (OperationType) this.xmlToObj(in, OperationType.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.gov.spcoop.sica.wscp.OperationType}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.gov.spcoop.sica.wscp.OperationType}
	 * @return Object type {@link it.gov.spcoop.sica.wscp.OperationType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public OperationType readOperationType(byte[] in) throws DeserializerException {
		return (OperationType) this.xmlToObj(in, OperationType.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.gov.spcoop.sica.wscp.OperationType}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.gov.spcoop.sica.wscp.OperationType}
	 * @return Object type {@link it.gov.spcoop.sica.wscp.OperationType}
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
	 * Transform the xml in <var>fileName</var> in the object type {@link it.gov.spcoop.sica.wscp.OperationListType}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.gov.spcoop.sica.wscp.OperationListType}
	 * @return Object type {@link it.gov.spcoop.sica.wscp.OperationListType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public OperationListType readOperationListType(String fileName) throws DeserializerException {
		return (OperationListType) this.xmlToObj(fileName, OperationListType.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.gov.spcoop.sica.wscp.OperationListType}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.gov.spcoop.sica.wscp.OperationListType}
	 * @return Object type {@link it.gov.spcoop.sica.wscp.OperationListType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public OperationListType readOperationListType(File file) throws DeserializerException {
		return (OperationListType) this.xmlToObj(file, OperationListType.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.gov.spcoop.sica.wscp.OperationListType}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.gov.spcoop.sica.wscp.OperationListType}
	 * @return Object type {@link it.gov.spcoop.sica.wscp.OperationListType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public OperationListType readOperationListType(InputStream in) throws DeserializerException {
		return (OperationListType) this.xmlToObj(in, OperationListType.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.gov.spcoop.sica.wscp.OperationListType}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.gov.spcoop.sica.wscp.OperationListType}
	 * @return Object type {@link it.gov.spcoop.sica.wscp.OperationListType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public OperationListType readOperationListType(byte[] in) throws DeserializerException {
		return (OperationListType) this.xmlToObj(in, OperationListType.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.gov.spcoop.sica.wscp.OperationListType}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.gov.spcoop.sica.wscp.OperationListType}
	 * @return Object type {@link it.gov.spcoop.sica.wscp.OperationListType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public OperationListType readOperationListTypeFromString(String in) throws DeserializerException {
		return (OperationListType) this.xmlToObj(in.getBytes(), OperationListType.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: profiloCollaborazioneEGOV
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link it.gov.spcoop.sica.wscp.ProfiloCollaborazioneEGOV}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.gov.spcoop.sica.wscp.ProfiloCollaborazioneEGOV}
	 * @return Object type {@link it.gov.spcoop.sica.wscp.ProfiloCollaborazioneEGOV}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ProfiloCollaborazioneEGOV readProfiloCollaborazioneEGOV(String fileName) throws DeserializerException {
		return (ProfiloCollaborazioneEGOV) this.xmlToObj(fileName, ProfiloCollaborazioneEGOV.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.gov.spcoop.sica.wscp.ProfiloCollaborazioneEGOV}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.gov.spcoop.sica.wscp.ProfiloCollaborazioneEGOV}
	 * @return Object type {@link it.gov.spcoop.sica.wscp.ProfiloCollaborazioneEGOV}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ProfiloCollaborazioneEGOV readProfiloCollaborazioneEGOV(File file) throws DeserializerException {
		return (ProfiloCollaborazioneEGOV) this.xmlToObj(file, ProfiloCollaborazioneEGOV.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.gov.spcoop.sica.wscp.ProfiloCollaborazioneEGOV}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.gov.spcoop.sica.wscp.ProfiloCollaborazioneEGOV}
	 * @return Object type {@link it.gov.spcoop.sica.wscp.ProfiloCollaborazioneEGOV}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ProfiloCollaborazioneEGOV readProfiloCollaborazioneEGOV(InputStream in) throws DeserializerException {
		return (ProfiloCollaborazioneEGOV) this.xmlToObj(in, ProfiloCollaborazioneEGOV.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.gov.spcoop.sica.wscp.ProfiloCollaborazioneEGOV}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.gov.spcoop.sica.wscp.ProfiloCollaborazioneEGOV}
	 * @return Object type {@link it.gov.spcoop.sica.wscp.ProfiloCollaborazioneEGOV}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ProfiloCollaborazioneEGOV readProfiloCollaborazioneEGOV(byte[] in) throws DeserializerException {
		return (ProfiloCollaborazioneEGOV) this.xmlToObj(in, ProfiloCollaborazioneEGOV.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.gov.spcoop.sica.wscp.ProfiloCollaborazioneEGOV}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.gov.spcoop.sica.wscp.ProfiloCollaborazioneEGOV}
	 * @return Object type {@link it.gov.spcoop.sica.wscp.ProfiloCollaborazioneEGOV}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ProfiloCollaborazioneEGOV readProfiloCollaborazioneEGOVFromString(String in) throws DeserializerException {
		return (ProfiloCollaborazioneEGOV) this.xmlToObj(in.getBytes(), ProfiloCollaborazioneEGOV.class);
	}	
	
	
	

}
