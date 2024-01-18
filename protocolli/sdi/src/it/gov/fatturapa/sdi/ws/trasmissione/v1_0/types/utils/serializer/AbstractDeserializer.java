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

import org.openspcoop2.generic_project.exception.DeserializerException;

import it.gov.fatturapa.sdi.ws.trasmissione.v1_0.types.FileSdIBaseType;
import it.gov.fatturapa.sdi.ws.trasmissione.v1_0.types.FileSdIType;
import it.gov.fatturapa.sdi.ws.trasmissione.v1_0.types.RispostaSdIRiceviFileType;

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
	 Object: fileSdIBase_Type
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link it.gov.fatturapa.sdi.ws.trasmissione.v1_0.types.FileSdIBaseType}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.ws.trasmissione.v1_0.types.FileSdIBaseType}
	 * @return Object type {@link it.gov.fatturapa.sdi.ws.trasmissione.v1_0.types.FileSdIBaseType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public FileSdIBaseType readFileSdIBaseType(String fileName) throws DeserializerException {
		return (FileSdIBaseType) this.xmlToObj(fileName, FileSdIBaseType.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.gov.fatturapa.sdi.ws.trasmissione.v1_0.types.FileSdIBaseType}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.ws.trasmissione.v1_0.types.FileSdIBaseType}
	 * @return Object type {@link it.gov.fatturapa.sdi.ws.trasmissione.v1_0.types.FileSdIBaseType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public FileSdIBaseType readFileSdIBaseType(File file) throws DeserializerException {
		return (FileSdIBaseType) this.xmlToObj(file, FileSdIBaseType.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.gov.fatturapa.sdi.ws.trasmissione.v1_0.types.FileSdIBaseType}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.ws.trasmissione.v1_0.types.FileSdIBaseType}
	 * @return Object type {@link it.gov.fatturapa.sdi.ws.trasmissione.v1_0.types.FileSdIBaseType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public FileSdIBaseType readFileSdIBaseType(InputStream in) throws DeserializerException {
		return (FileSdIBaseType) this.xmlToObj(in, FileSdIBaseType.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.gov.fatturapa.sdi.ws.trasmissione.v1_0.types.FileSdIBaseType}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.ws.trasmissione.v1_0.types.FileSdIBaseType}
	 * @return Object type {@link it.gov.fatturapa.sdi.ws.trasmissione.v1_0.types.FileSdIBaseType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public FileSdIBaseType readFileSdIBaseType(byte[] in) throws DeserializerException {
		return (FileSdIBaseType) this.xmlToObj(in, FileSdIBaseType.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.gov.fatturapa.sdi.ws.trasmissione.v1_0.types.FileSdIBaseType}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.ws.trasmissione.v1_0.types.FileSdIBaseType}
	 * @return Object type {@link it.gov.fatturapa.sdi.ws.trasmissione.v1_0.types.FileSdIBaseType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public FileSdIBaseType readFileSdIBaseTypeFromString(String in) throws DeserializerException {
		return (FileSdIBaseType) this.xmlToObj(in.getBytes(), FileSdIBaseType.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: fileSdI_Type
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link it.gov.fatturapa.sdi.ws.trasmissione.v1_0.types.FileSdIType}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.ws.trasmissione.v1_0.types.FileSdIType}
	 * @return Object type {@link it.gov.fatturapa.sdi.ws.trasmissione.v1_0.types.FileSdIType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public FileSdIType readFileSdIType(String fileName) throws DeserializerException {
		return (FileSdIType) this.xmlToObj(fileName, FileSdIType.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.gov.fatturapa.sdi.ws.trasmissione.v1_0.types.FileSdIType}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.ws.trasmissione.v1_0.types.FileSdIType}
	 * @return Object type {@link it.gov.fatturapa.sdi.ws.trasmissione.v1_0.types.FileSdIType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public FileSdIType readFileSdIType(File file) throws DeserializerException {
		return (FileSdIType) this.xmlToObj(file, FileSdIType.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.gov.fatturapa.sdi.ws.trasmissione.v1_0.types.FileSdIType}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.ws.trasmissione.v1_0.types.FileSdIType}
	 * @return Object type {@link it.gov.fatturapa.sdi.ws.trasmissione.v1_0.types.FileSdIType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public FileSdIType readFileSdIType(InputStream in) throws DeserializerException {
		return (FileSdIType) this.xmlToObj(in, FileSdIType.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.gov.fatturapa.sdi.ws.trasmissione.v1_0.types.FileSdIType}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.ws.trasmissione.v1_0.types.FileSdIType}
	 * @return Object type {@link it.gov.fatturapa.sdi.ws.trasmissione.v1_0.types.FileSdIType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public FileSdIType readFileSdIType(byte[] in) throws DeserializerException {
		return (FileSdIType) this.xmlToObj(in, FileSdIType.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.gov.fatturapa.sdi.ws.trasmissione.v1_0.types.FileSdIType}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.ws.trasmissione.v1_0.types.FileSdIType}
	 * @return Object type {@link it.gov.fatturapa.sdi.ws.trasmissione.v1_0.types.FileSdIType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public FileSdIType readFileSdITypeFromString(String in) throws DeserializerException {
		return (FileSdIType) this.xmlToObj(in.getBytes(), FileSdIType.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: rispostaSdIRiceviFile_Type
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link it.gov.fatturapa.sdi.ws.trasmissione.v1_0.types.RispostaSdIRiceviFileType}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.ws.trasmissione.v1_0.types.RispostaSdIRiceviFileType}
	 * @return Object type {@link it.gov.fatturapa.sdi.ws.trasmissione.v1_0.types.RispostaSdIRiceviFileType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RispostaSdIRiceviFileType readRispostaSdIRiceviFileType(String fileName) throws DeserializerException {
		return (RispostaSdIRiceviFileType) this.xmlToObj(fileName, RispostaSdIRiceviFileType.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.gov.fatturapa.sdi.ws.trasmissione.v1_0.types.RispostaSdIRiceviFileType}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.ws.trasmissione.v1_0.types.RispostaSdIRiceviFileType}
	 * @return Object type {@link it.gov.fatturapa.sdi.ws.trasmissione.v1_0.types.RispostaSdIRiceviFileType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RispostaSdIRiceviFileType readRispostaSdIRiceviFileType(File file) throws DeserializerException {
		return (RispostaSdIRiceviFileType) this.xmlToObj(file, RispostaSdIRiceviFileType.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.gov.fatturapa.sdi.ws.trasmissione.v1_0.types.RispostaSdIRiceviFileType}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.ws.trasmissione.v1_0.types.RispostaSdIRiceviFileType}
	 * @return Object type {@link it.gov.fatturapa.sdi.ws.trasmissione.v1_0.types.RispostaSdIRiceviFileType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RispostaSdIRiceviFileType readRispostaSdIRiceviFileType(InputStream in) throws DeserializerException {
		return (RispostaSdIRiceviFileType) this.xmlToObj(in, RispostaSdIRiceviFileType.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.gov.fatturapa.sdi.ws.trasmissione.v1_0.types.RispostaSdIRiceviFileType}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.ws.trasmissione.v1_0.types.RispostaSdIRiceviFileType}
	 * @return Object type {@link it.gov.fatturapa.sdi.ws.trasmissione.v1_0.types.RispostaSdIRiceviFileType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RispostaSdIRiceviFileType readRispostaSdIRiceviFileType(byte[] in) throws DeserializerException {
		return (RispostaSdIRiceviFileType) this.xmlToObj(in, RispostaSdIRiceviFileType.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.gov.fatturapa.sdi.ws.trasmissione.v1_0.types.RispostaSdIRiceviFileType}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.ws.trasmissione.v1_0.types.RispostaSdIRiceviFileType}
	 * @return Object type {@link it.gov.fatturapa.sdi.ws.trasmissione.v1_0.types.RispostaSdIRiceviFileType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RispostaSdIRiceviFileType readRispostaSdIRiceviFileTypeFromString(String in) throws DeserializerException {
		return (RispostaSdIRiceviFileType) this.xmlToObj(in.getBytes(), RispostaSdIRiceviFileType.class);
	}	
	
	
	

}
