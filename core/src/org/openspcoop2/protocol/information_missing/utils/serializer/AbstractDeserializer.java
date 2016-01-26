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
package org.openspcoop2.protocol.information_missing.utils.serializer;

import org.openspcoop2.generic_project.exception.DeserializerException;

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
	 Object: replaceMatchType
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.protocol.information_missing.ReplaceMatchType}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.ReplaceMatchType}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.ReplaceMatchType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ReplaceMatchType readReplaceMatchType(String fileName) throws DeserializerException {
		return (ReplaceMatchType) this.xmlToObj(fileName, ReplaceMatchType.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.protocol.information_missing.ReplaceMatchType}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.ReplaceMatchType}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.ReplaceMatchType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ReplaceMatchType readReplaceMatchType(File file) throws DeserializerException {
		return (ReplaceMatchType) this.xmlToObj(file, ReplaceMatchType.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.protocol.information_missing.ReplaceMatchType}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.ReplaceMatchType}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.ReplaceMatchType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ReplaceMatchType readReplaceMatchType(InputStream in) throws DeserializerException {
		return (ReplaceMatchType) this.xmlToObj(in, ReplaceMatchType.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.protocol.information_missing.ReplaceMatchType}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.ReplaceMatchType}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.ReplaceMatchType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ReplaceMatchType readReplaceMatchType(byte[] in) throws DeserializerException {
		return (ReplaceMatchType) this.xmlToObj(in, ReplaceMatchType.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.protocol.information_missing.ReplaceMatchType}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.ReplaceMatchType}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.ReplaceMatchType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ReplaceMatchType readReplaceMatchTypeFromString(String in) throws DeserializerException {
		return (ReplaceMatchType) this.xmlToObj(in.getBytes(), ReplaceMatchType.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: ServizioApplicativo
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.protocol.information_missing.ServizioApplicativo}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.ServizioApplicativo}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.ServizioApplicativo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ServizioApplicativo readServizioApplicativo(String fileName) throws DeserializerException {
		return (ServizioApplicativo) this.xmlToObj(fileName, ServizioApplicativo.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.protocol.information_missing.ServizioApplicativo}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.ServizioApplicativo}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.ServizioApplicativo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ServizioApplicativo readServizioApplicativo(File file) throws DeserializerException {
		return (ServizioApplicativo) this.xmlToObj(file, ServizioApplicativo.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.protocol.information_missing.ServizioApplicativo}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.ServizioApplicativo}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.ServizioApplicativo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ServizioApplicativo readServizioApplicativo(InputStream in) throws DeserializerException {
		return (ServizioApplicativo) this.xmlToObj(in, ServizioApplicativo.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.protocol.information_missing.ServizioApplicativo}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.ServizioApplicativo}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.ServizioApplicativo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ServizioApplicativo readServizioApplicativo(byte[] in) throws DeserializerException {
		return (ServizioApplicativo) this.xmlToObj(in, ServizioApplicativo.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.protocol.information_missing.ServizioApplicativo}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.ServizioApplicativo}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.ServizioApplicativo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ServizioApplicativo readServizioApplicativoFromString(String in) throws DeserializerException {
		return (ServizioApplicativo) this.xmlToObj(in.getBytes(), ServizioApplicativo.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: openspcoop2
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.protocol.information_missing.Openspcoop2}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.Openspcoop2}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.Openspcoop2}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Openspcoop2 readOpenspcoop2(String fileName) throws DeserializerException {
		return (Openspcoop2) this.xmlToObj(fileName, Openspcoop2.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.protocol.information_missing.Openspcoop2}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.Openspcoop2}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.Openspcoop2}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Openspcoop2 readOpenspcoop2(File file) throws DeserializerException {
		return (Openspcoop2) this.xmlToObj(file, Openspcoop2.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.protocol.information_missing.Openspcoop2}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.Openspcoop2}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.Openspcoop2}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Openspcoop2 readOpenspcoop2(InputStream in) throws DeserializerException {
		return (Openspcoop2) this.xmlToObj(in, Openspcoop2.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.protocol.information_missing.Openspcoop2}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.Openspcoop2}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.Openspcoop2}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Openspcoop2 readOpenspcoop2(byte[] in) throws DeserializerException {
		return (Openspcoop2) this.xmlToObj(in, Openspcoop2.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.protocol.information_missing.Openspcoop2}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.Openspcoop2}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.Openspcoop2}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Openspcoop2 readOpenspcoop2FromString(String in) throws DeserializerException {
		return (Openspcoop2) this.xmlToObj(in.getBytes(), Openspcoop2.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: Soggetto
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.protocol.information_missing.Soggetto}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.Soggetto}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.Soggetto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Soggetto readSoggetto(String fileName) throws DeserializerException {
		return (Soggetto) this.xmlToObj(fileName, Soggetto.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.protocol.information_missing.Soggetto}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.Soggetto}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.Soggetto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Soggetto readSoggetto(File file) throws DeserializerException {
		return (Soggetto) this.xmlToObj(file, Soggetto.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.protocol.information_missing.Soggetto}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.Soggetto}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.Soggetto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Soggetto readSoggetto(InputStream in) throws DeserializerException {
		return (Soggetto) this.xmlToObj(in, Soggetto.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.protocol.information_missing.Soggetto}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.Soggetto}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.Soggetto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Soggetto readSoggetto(byte[] in) throws DeserializerException {
		return (Soggetto) this.xmlToObj(in, Soggetto.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.protocol.information_missing.Soggetto}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.Soggetto}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.Soggetto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Soggetto readSoggettoFromString(String in) throws DeserializerException {
		return (Soggetto) this.xmlToObj(in.getBytes(), Soggetto.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: Input
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.protocol.information_missing.Input}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.Input}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.Input}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Input readInput(String fileName) throws DeserializerException {
		return (Input) this.xmlToObj(fileName, Input.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.protocol.information_missing.Input}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.Input}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.Input}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Input readInput(File file) throws DeserializerException {
		return (Input) this.xmlToObj(file, Input.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.protocol.information_missing.Input}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.Input}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.Input}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Input readInput(InputStream in) throws DeserializerException {
		return (Input) this.xmlToObj(in, Input.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.protocol.information_missing.Input}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.Input}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.Input}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Input readInput(byte[] in) throws DeserializerException {
		return (Input) this.xmlToObj(in, Input.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.protocol.information_missing.Input}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.Input}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.Input}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Input readInputFromString(String in) throws DeserializerException {
		return (Input) this.xmlToObj(in.getBytes(), Input.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: AccordoCooperazione
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.protocol.information_missing.AccordoCooperazione}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.AccordoCooperazione}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.AccordoCooperazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccordoCooperazione readAccordoCooperazione(String fileName) throws DeserializerException {
		return (AccordoCooperazione) this.xmlToObj(fileName, AccordoCooperazione.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.protocol.information_missing.AccordoCooperazione}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.AccordoCooperazione}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.AccordoCooperazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccordoCooperazione readAccordoCooperazione(File file) throws DeserializerException {
		return (AccordoCooperazione) this.xmlToObj(file, AccordoCooperazione.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.protocol.information_missing.AccordoCooperazione}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.AccordoCooperazione}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.AccordoCooperazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccordoCooperazione readAccordoCooperazione(InputStream in) throws DeserializerException {
		return (AccordoCooperazione) this.xmlToObj(in, AccordoCooperazione.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.protocol.information_missing.AccordoCooperazione}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.AccordoCooperazione}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.AccordoCooperazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccordoCooperazione readAccordoCooperazione(byte[] in) throws DeserializerException {
		return (AccordoCooperazione) this.xmlToObj(in, AccordoCooperazione.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.protocol.information_missing.AccordoCooperazione}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.AccordoCooperazione}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.AccordoCooperazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccordoCooperazione readAccordoCooperazioneFromString(String in) throws DeserializerException {
		return (AccordoCooperazione) this.xmlToObj(in.getBytes(), AccordoCooperazione.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: AccordoServizioParteComune
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.protocol.information_missing.AccordoServizioParteComune}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.AccordoServizioParteComune}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.AccordoServizioParteComune}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccordoServizioParteComune readAccordoServizioParteComune(String fileName) throws DeserializerException {
		return (AccordoServizioParteComune) this.xmlToObj(fileName, AccordoServizioParteComune.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.protocol.information_missing.AccordoServizioParteComune}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.AccordoServizioParteComune}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.AccordoServizioParteComune}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccordoServizioParteComune readAccordoServizioParteComune(File file) throws DeserializerException {
		return (AccordoServizioParteComune) this.xmlToObj(file, AccordoServizioParteComune.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.protocol.information_missing.AccordoServizioParteComune}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.AccordoServizioParteComune}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.AccordoServizioParteComune}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccordoServizioParteComune readAccordoServizioParteComune(InputStream in) throws DeserializerException {
		return (AccordoServizioParteComune) this.xmlToObj(in, AccordoServizioParteComune.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.protocol.information_missing.AccordoServizioParteComune}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.AccordoServizioParteComune}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.AccordoServizioParteComune}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccordoServizioParteComune readAccordoServizioParteComune(byte[] in) throws DeserializerException {
		return (AccordoServizioParteComune) this.xmlToObj(in, AccordoServizioParteComune.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.protocol.information_missing.AccordoServizioParteComune}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.AccordoServizioParteComune}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.AccordoServizioParteComune}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccordoServizioParteComune readAccordoServizioParteComuneFromString(String in) throws DeserializerException {
		return (AccordoServizioParteComune) this.xmlToObj(in.getBytes(), AccordoServizioParteComune.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: AccordoServizioParteSpecifica
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.protocol.information_missing.AccordoServizioParteSpecifica}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.AccordoServizioParteSpecifica}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.AccordoServizioParteSpecifica}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccordoServizioParteSpecifica readAccordoServizioParteSpecifica(String fileName) throws DeserializerException {
		return (AccordoServizioParteSpecifica) this.xmlToObj(fileName, AccordoServizioParteSpecifica.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.protocol.information_missing.AccordoServizioParteSpecifica}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.AccordoServizioParteSpecifica}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.AccordoServizioParteSpecifica}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccordoServizioParteSpecifica readAccordoServizioParteSpecifica(File file) throws DeserializerException {
		return (AccordoServizioParteSpecifica) this.xmlToObj(file, AccordoServizioParteSpecifica.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.protocol.information_missing.AccordoServizioParteSpecifica}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.AccordoServizioParteSpecifica}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.AccordoServizioParteSpecifica}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccordoServizioParteSpecifica readAccordoServizioParteSpecifica(InputStream in) throws DeserializerException {
		return (AccordoServizioParteSpecifica) this.xmlToObj(in, AccordoServizioParteSpecifica.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.protocol.information_missing.AccordoServizioParteSpecifica}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.AccordoServizioParteSpecifica}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.AccordoServizioParteSpecifica}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccordoServizioParteSpecifica readAccordoServizioParteSpecifica(byte[] in) throws DeserializerException {
		return (AccordoServizioParteSpecifica) this.xmlToObj(in, AccordoServizioParteSpecifica.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.protocol.information_missing.AccordoServizioParteSpecifica}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.AccordoServizioParteSpecifica}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.AccordoServizioParteSpecifica}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccordoServizioParteSpecifica readAccordoServizioParteSpecificaFromString(String in) throws DeserializerException {
		return (AccordoServizioParteSpecifica) this.xmlToObj(in.getBytes(), AccordoServizioParteSpecifica.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: Fruitore
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.protocol.information_missing.Fruitore}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.Fruitore}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.Fruitore}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Fruitore readFruitore(String fileName) throws DeserializerException {
		return (Fruitore) this.xmlToObj(fileName, Fruitore.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.protocol.information_missing.Fruitore}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.Fruitore}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.Fruitore}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Fruitore readFruitore(File file) throws DeserializerException {
		return (Fruitore) this.xmlToObj(file, Fruitore.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.protocol.information_missing.Fruitore}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.Fruitore}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.Fruitore}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Fruitore readFruitore(InputStream in) throws DeserializerException {
		return (Fruitore) this.xmlToObj(in, Fruitore.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.protocol.information_missing.Fruitore}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.Fruitore}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.Fruitore}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Fruitore readFruitore(byte[] in) throws DeserializerException {
		return (Fruitore) this.xmlToObj(in, Fruitore.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.protocol.information_missing.Fruitore}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.Fruitore}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.Fruitore}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Fruitore readFruitoreFromString(String in) throws DeserializerException {
		return (Fruitore) this.xmlToObj(in.getBytes(), Fruitore.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: RequisitoProtocollo
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.protocol.information_missing.RequisitoProtocollo}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.RequisitoProtocollo}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.RequisitoProtocollo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RequisitoProtocollo readRequisitoProtocollo(String fileName) throws DeserializerException {
		return (RequisitoProtocollo) this.xmlToObj(fileName, RequisitoProtocollo.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.protocol.information_missing.RequisitoProtocollo}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.RequisitoProtocollo}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.RequisitoProtocollo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RequisitoProtocollo readRequisitoProtocollo(File file) throws DeserializerException {
		return (RequisitoProtocollo) this.xmlToObj(file, RequisitoProtocollo.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.protocol.information_missing.RequisitoProtocollo}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.RequisitoProtocollo}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.RequisitoProtocollo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RequisitoProtocollo readRequisitoProtocollo(InputStream in) throws DeserializerException {
		return (RequisitoProtocollo) this.xmlToObj(in, RequisitoProtocollo.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.protocol.information_missing.RequisitoProtocollo}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.RequisitoProtocollo}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.RequisitoProtocollo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RequisitoProtocollo readRequisitoProtocollo(byte[] in) throws DeserializerException {
		return (RequisitoProtocollo) this.xmlToObj(in, RequisitoProtocollo.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.protocol.information_missing.RequisitoProtocollo}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.RequisitoProtocollo}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.RequisitoProtocollo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RequisitoProtocollo readRequisitoProtocolloFromString(String in) throws DeserializerException {
		return (RequisitoProtocollo) this.xmlToObj(in.getBytes(), RequisitoProtocollo.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: Requisiti
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.protocol.information_missing.Requisiti}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.Requisiti}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.Requisiti}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Requisiti readRequisiti(String fileName) throws DeserializerException {
		return (Requisiti) this.xmlToObj(fileName, Requisiti.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.protocol.information_missing.Requisiti}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.Requisiti}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.Requisiti}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Requisiti readRequisiti(File file) throws DeserializerException {
		return (Requisiti) this.xmlToObj(file, Requisiti.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.protocol.information_missing.Requisiti}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.Requisiti}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.Requisiti}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Requisiti readRequisiti(InputStream in) throws DeserializerException {
		return (Requisiti) this.xmlToObj(in, Requisiti.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.protocol.information_missing.Requisiti}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.Requisiti}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.Requisiti}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Requisiti readRequisiti(byte[] in) throws DeserializerException {
		return (Requisiti) this.xmlToObj(in, Requisiti.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.protocol.information_missing.Requisiti}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.Requisiti}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.Requisiti}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Requisiti readRequisitiFromString(String in) throws DeserializerException {
		return (Requisiti) this.xmlToObj(in.getBytes(), Requisiti.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: Proprieta
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.protocol.information_missing.Proprieta}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.Proprieta}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.Proprieta}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Proprieta readProprieta(String fileName) throws DeserializerException {
		return (Proprieta) this.xmlToObj(fileName, Proprieta.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.protocol.information_missing.Proprieta}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.Proprieta}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.Proprieta}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Proprieta readProprieta(File file) throws DeserializerException {
		return (Proprieta) this.xmlToObj(file, Proprieta.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.protocol.information_missing.Proprieta}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.Proprieta}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.Proprieta}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Proprieta readProprieta(InputStream in) throws DeserializerException {
		return (Proprieta) this.xmlToObj(in, Proprieta.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.protocol.information_missing.Proprieta}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.Proprieta}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.Proprieta}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Proprieta readProprieta(byte[] in) throws DeserializerException {
		return (Proprieta) this.xmlToObj(in, Proprieta.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.protocol.information_missing.Proprieta}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.Proprieta}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.Proprieta}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Proprieta readProprietaFromString(String in) throws DeserializerException {
		return (Proprieta) this.xmlToObj(in.getBytes(), Proprieta.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: ReplaceMatchFieldType
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.protocol.information_missing.ReplaceMatchFieldType}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.ReplaceMatchFieldType}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.ReplaceMatchFieldType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ReplaceMatchFieldType readReplaceMatchFieldType(String fileName) throws DeserializerException {
		return (ReplaceMatchFieldType) this.xmlToObj(fileName, ReplaceMatchFieldType.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.protocol.information_missing.ReplaceMatchFieldType}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.ReplaceMatchFieldType}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.ReplaceMatchFieldType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ReplaceMatchFieldType readReplaceMatchFieldType(File file) throws DeserializerException {
		return (ReplaceMatchFieldType) this.xmlToObj(file, ReplaceMatchFieldType.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.protocol.information_missing.ReplaceMatchFieldType}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.ReplaceMatchFieldType}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.ReplaceMatchFieldType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ReplaceMatchFieldType readReplaceMatchFieldType(InputStream in) throws DeserializerException {
		return (ReplaceMatchFieldType) this.xmlToObj(in, ReplaceMatchFieldType.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.protocol.information_missing.ReplaceMatchFieldType}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.ReplaceMatchFieldType}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.ReplaceMatchFieldType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ReplaceMatchFieldType readReplaceMatchFieldType(byte[] in) throws DeserializerException {
		return (ReplaceMatchFieldType) this.xmlToObj(in, ReplaceMatchFieldType.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.protocol.information_missing.ReplaceMatchFieldType}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.ReplaceMatchFieldType}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.ReplaceMatchFieldType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ReplaceMatchFieldType readReplaceMatchFieldTypeFromString(String in) throws DeserializerException {
		return (ReplaceMatchFieldType) this.xmlToObj(in.getBytes(), ReplaceMatchFieldType.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: Wizard
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.protocol.information_missing.Wizard}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.Wizard}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.Wizard}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Wizard readWizard(String fileName) throws DeserializerException {
		return (Wizard) this.xmlToObj(fileName, Wizard.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.protocol.information_missing.Wizard}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.Wizard}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.Wizard}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Wizard readWizard(File file) throws DeserializerException {
		return (Wizard) this.xmlToObj(file, Wizard.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.protocol.information_missing.Wizard}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.Wizard}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.Wizard}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Wizard readWizard(InputStream in) throws DeserializerException {
		return (Wizard) this.xmlToObj(in, Wizard.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.protocol.information_missing.Wizard}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.Wizard}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.Wizard}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Wizard readWizard(byte[] in) throws DeserializerException {
		return (Wizard) this.xmlToObj(in, Wizard.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.protocol.information_missing.Wizard}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.Wizard}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.Wizard}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Wizard readWizardFromString(String in) throws DeserializerException {
		return (Wizard) this.xmlToObj(in.getBytes(), Wizard.class);
	}	
	
	
	

}
