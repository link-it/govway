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
package it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.utils.serializer;

import org.openspcoop2.generic_project.exception.DeserializerException;

import it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.FileSdIConMetadatiType;
import it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.FileSdIBaseType;
import it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.FileSdIType;
import it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.RispostaSdINotificaEsitoType;

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
	 Object: fileSdIConMetadati_Type
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.FileSdIConMetadatiType}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.FileSdIConMetadatiType}
	 * @return Object type {@link it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.FileSdIConMetadatiType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public FileSdIConMetadatiType readFileSdIConMetadatiType(String fileName) throws DeserializerException {
		return (FileSdIConMetadatiType) this.xmlToObj(fileName, FileSdIConMetadatiType.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.FileSdIConMetadatiType}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.FileSdIConMetadatiType}
	 * @return Object type {@link it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.FileSdIConMetadatiType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public FileSdIConMetadatiType readFileSdIConMetadatiType(File file) throws DeserializerException {
		return (FileSdIConMetadatiType) this.xmlToObj(file, FileSdIConMetadatiType.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.FileSdIConMetadatiType}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.FileSdIConMetadatiType}
	 * @return Object type {@link it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.FileSdIConMetadatiType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public FileSdIConMetadatiType readFileSdIConMetadatiType(InputStream in) throws DeserializerException {
		return (FileSdIConMetadatiType) this.xmlToObj(in, FileSdIConMetadatiType.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.FileSdIConMetadatiType}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.FileSdIConMetadatiType}
	 * @return Object type {@link it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.FileSdIConMetadatiType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public FileSdIConMetadatiType readFileSdIConMetadatiType(byte[] in) throws DeserializerException {
		return (FileSdIConMetadatiType) this.xmlToObj(in, FileSdIConMetadatiType.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.FileSdIConMetadatiType}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.FileSdIConMetadatiType}
	 * @return Object type {@link it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.FileSdIConMetadatiType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public FileSdIConMetadatiType readFileSdIConMetadatiTypeFromString(String in) throws DeserializerException {
		return (FileSdIConMetadatiType) this.xmlToObj(in.getBytes(), FileSdIConMetadatiType.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: fileSdIBase_Type
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.FileSdIBaseType}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.FileSdIBaseType}
	 * @return Object type {@link it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.FileSdIBaseType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public FileSdIBaseType readFileSdIBaseType(String fileName) throws DeserializerException {
		return (FileSdIBaseType) this.xmlToObj(fileName, FileSdIBaseType.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.FileSdIBaseType}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.FileSdIBaseType}
	 * @return Object type {@link it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.FileSdIBaseType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public FileSdIBaseType readFileSdIBaseType(File file) throws DeserializerException {
		return (FileSdIBaseType) this.xmlToObj(file, FileSdIBaseType.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.FileSdIBaseType}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.FileSdIBaseType}
	 * @return Object type {@link it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.FileSdIBaseType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public FileSdIBaseType readFileSdIBaseType(InputStream in) throws DeserializerException {
		return (FileSdIBaseType) this.xmlToObj(in, FileSdIBaseType.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.FileSdIBaseType}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.FileSdIBaseType}
	 * @return Object type {@link it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.FileSdIBaseType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public FileSdIBaseType readFileSdIBaseType(byte[] in) throws DeserializerException {
		return (FileSdIBaseType) this.xmlToObj(in, FileSdIBaseType.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.FileSdIBaseType}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.FileSdIBaseType}
	 * @return Object type {@link it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.FileSdIBaseType}
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
	 * Transform the xml in <var>fileName</var> in the object type {@link it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.FileSdIType}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.FileSdIType}
	 * @return Object type {@link it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.FileSdIType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public FileSdIType readFileSdIType(String fileName) throws DeserializerException {
		return (FileSdIType) this.xmlToObj(fileName, FileSdIType.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.FileSdIType}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.FileSdIType}
	 * @return Object type {@link it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.FileSdIType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public FileSdIType readFileSdIType(File file) throws DeserializerException {
		return (FileSdIType) this.xmlToObj(file, FileSdIType.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.FileSdIType}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.FileSdIType}
	 * @return Object type {@link it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.FileSdIType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public FileSdIType readFileSdIType(InputStream in) throws DeserializerException {
		return (FileSdIType) this.xmlToObj(in, FileSdIType.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.FileSdIType}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.FileSdIType}
	 * @return Object type {@link it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.FileSdIType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public FileSdIType readFileSdIType(byte[] in) throws DeserializerException {
		return (FileSdIType) this.xmlToObj(in, FileSdIType.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.FileSdIType}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.FileSdIType}
	 * @return Object type {@link it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.FileSdIType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public FileSdIType readFileSdITypeFromString(String in) throws DeserializerException {
		return (FileSdIType) this.xmlToObj(in.getBytes(), FileSdIType.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: rispostaSdINotificaEsito_Type
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.RispostaSdINotificaEsitoType}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.RispostaSdINotificaEsitoType}
	 * @return Object type {@link it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.RispostaSdINotificaEsitoType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RispostaSdINotificaEsitoType readRispostaSdINotificaEsitoType(String fileName) throws DeserializerException {
		return (RispostaSdINotificaEsitoType) this.xmlToObj(fileName, RispostaSdINotificaEsitoType.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.RispostaSdINotificaEsitoType}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.RispostaSdINotificaEsitoType}
	 * @return Object type {@link it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.RispostaSdINotificaEsitoType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RispostaSdINotificaEsitoType readRispostaSdINotificaEsitoType(File file) throws DeserializerException {
		return (RispostaSdINotificaEsitoType) this.xmlToObj(file, RispostaSdINotificaEsitoType.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.RispostaSdINotificaEsitoType}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.RispostaSdINotificaEsitoType}
	 * @return Object type {@link it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.RispostaSdINotificaEsitoType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RispostaSdINotificaEsitoType readRispostaSdINotificaEsitoType(InputStream in) throws DeserializerException {
		return (RispostaSdINotificaEsitoType) this.xmlToObj(in, RispostaSdINotificaEsitoType.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.RispostaSdINotificaEsitoType}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.RispostaSdINotificaEsitoType}
	 * @return Object type {@link it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.RispostaSdINotificaEsitoType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RispostaSdINotificaEsitoType readRispostaSdINotificaEsitoType(byte[] in) throws DeserializerException {
		return (RispostaSdINotificaEsitoType) this.xmlToObj(in, RispostaSdINotificaEsitoType.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.RispostaSdINotificaEsitoType}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.RispostaSdINotificaEsitoType}
	 * @return Object type {@link it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.RispostaSdINotificaEsitoType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RispostaSdINotificaEsitoType readRispostaSdINotificaEsitoTypeFromString(String in) throws DeserializerException {
		return (RispostaSdINotificaEsitoType) this.xmlToObj(in.getBytes(), RispostaSdINotificaEsitoType.class);
	}	
	
	
	

}
