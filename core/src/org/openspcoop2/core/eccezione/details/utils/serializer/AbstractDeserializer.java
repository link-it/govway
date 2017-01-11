/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2017 Link.it srl (http://link.it).
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
package org.openspcoop2.core.eccezione.details.utils.serializer;

import org.openspcoop2.generic_project.exception.DeserializerException;

import org.openspcoop2.core.eccezione.details.Eccezione;
import org.openspcoop2.core.eccezione.details.DominioSoggetto;
import org.openspcoop2.core.eccezione.details.Dettaglio;
import org.openspcoop2.core.eccezione.details.Dominio;
import org.openspcoop2.core.eccezione.details.DettaglioEccezione;
import org.openspcoop2.core.eccezione.details.Eccezioni;
import org.openspcoop2.core.eccezione.details.Dettagli;

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
	 Object: eccezione
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.eccezione.details.Eccezione}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.eccezione.details.Eccezione}
	 * @return Object type {@link org.openspcoop2.core.eccezione.details.Eccezione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Eccezione readEccezione(String fileName) throws DeserializerException {
		return (Eccezione) this.xmlToObj(fileName, Eccezione.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.eccezione.details.Eccezione}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.eccezione.details.Eccezione}
	 * @return Object type {@link org.openspcoop2.core.eccezione.details.Eccezione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Eccezione readEccezione(File file) throws DeserializerException {
		return (Eccezione) this.xmlToObj(file, Eccezione.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.eccezione.details.Eccezione}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.eccezione.details.Eccezione}
	 * @return Object type {@link org.openspcoop2.core.eccezione.details.Eccezione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Eccezione readEccezione(InputStream in) throws DeserializerException {
		return (Eccezione) this.xmlToObj(in, Eccezione.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.eccezione.details.Eccezione}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.eccezione.details.Eccezione}
	 * @return Object type {@link org.openspcoop2.core.eccezione.details.Eccezione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Eccezione readEccezione(byte[] in) throws DeserializerException {
		return (Eccezione) this.xmlToObj(in, Eccezione.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.eccezione.details.Eccezione}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.eccezione.details.Eccezione}
	 * @return Object type {@link org.openspcoop2.core.eccezione.details.Eccezione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Eccezione readEccezioneFromString(String in) throws DeserializerException {
		return (Eccezione) this.xmlToObj(in.getBytes(), Eccezione.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: dominio-soggetto
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.eccezione.details.DominioSoggetto}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.eccezione.details.DominioSoggetto}
	 * @return Object type {@link org.openspcoop2.core.eccezione.details.DominioSoggetto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DominioSoggetto readDominioSoggetto(String fileName) throws DeserializerException {
		return (DominioSoggetto) this.xmlToObj(fileName, DominioSoggetto.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.eccezione.details.DominioSoggetto}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.eccezione.details.DominioSoggetto}
	 * @return Object type {@link org.openspcoop2.core.eccezione.details.DominioSoggetto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DominioSoggetto readDominioSoggetto(File file) throws DeserializerException {
		return (DominioSoggetto) this.xmlToObj(file, DominioSoggetto.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.eccezione.details.DominioSoggetto}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.eccezione.details.DominioSoggetto}
	 * @return Object type {@link org.openspcoop2.core.eccezione.details.DominioSoggetto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DominioSoggetto readDominioSoggetto(InputStream in) throws DeserializerException {
		return (DominioSoggetto) this.xmlToObj(in, DominioSoggetto.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.eccezione.details.DominioSoggetto}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.eccezione.details.DominioSoggetto}
	 * @return Object type {@link org.openspcoop2.core.eccezione.details.DominioSoggetto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DominioSoggetto readDominioSoggetto(byte[] in) throws DeserializerException {
		return (DominioSoggetto) this.xmlToObj(in, DominioSoggetto.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.eccezione.details.DominioSoggetto}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.eccezione.details.DominioSoggetto}
	 * @return Object type {@link org.openspcoop2.core.eccezione.details.DominioSoggetto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DominioSoggetto readDominioSoggettoFromString(String in) throws DeserializerException {
		return (DominioSoggetto) this.xmlToObj(in.getBytes(), DominioSoggetto.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: dettaglio
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.eccezione.details.Dettaglio}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.eccezione.details.Dettaglio}
	 * @return Object type {@link org.openspcoop2.core.eccezione.details.Dettaglio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Dettaglio readDettaglio(String fileName) throws DeserializerException {
		return (Dettaglio) this.xmlToObj(fileName, Dettaglio.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.eccezione.details.Dettaglio}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.eccezione.details.Dettaglio}
	 * @return Object type {@link org.openspcoop2.core.eccezione.details.Dettaglio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Dettaglio readDettaglio(File file) throws DeserializerException {
		return (Dettaglio) this.xmlToObj(file, Dettaglio.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.eccezione.details.Dettaglio}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.eccezione.details.Dettaglio}
	 * @return Object type {@link org.openspcoop2.core.eccezione.details.Dettaglio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Dettaglio readDettaglio(InputStream in) throws DeserializerException {
		return (Dettaglio) this.xmlToObj(in, Dettaglio.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.eccezione.details.Dettaglio}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.eccezione.details.Dettaglio}
	 * @return Object type {@link org.openspcoop2.core.eccezione.details.Dettaglio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Dettaglio readDettaglio(byte[] in) throws DeserializerException {
		return (Dettaglio) this.xmlToObj(in, Dettaglio.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.eccezione.details.Dettaglio}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.eccezione.details.Dettaglio}
	 * @return Object type {@link org.openspcoop2.core.eccezione.details.Dettaglio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Dettaglio readDettaglioFromString(String in) throws DeserializerException {
		return (Dettaglio) this.xmlToObj(in.getBytes(), Dettaglio.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: dominio
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.eccezione.details.Dominio}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.eccezione.details.Dominio}
	 * @return Object type {@link org.openspcoop2.core.eccezione.details.Dominio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Dominio readDominio(String fileName) throws DeserializerException {
		return (Dominio) this.xmlToObj(fileName, Dominio.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.eccezione.details.Dominio}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.eccezione.details.Dominio}
	 * @return Object type {@link org.openspcoop2.core.eccezione.details.Dominio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Dominio readDominio(File file) throws DeserializerException {
		return (Dominio) this.xmlToObj(file, Dominio.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.eccezione.details.Dominio}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.eccezione.details.Dominio}
	 * @return Object type {@link org.openspcoop2.core.eccezione.details.Dominio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Dominio readDominio(InputStream in) throws DeserializerException {
		return (Dominio) this.xmlToObj(in, Dominio.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.eccezione.details.Dominio}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.eccezione.details.Dominio}
	 * @return Object type {@link org.openspcoop2.core.eccezione.details.Dominio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Dominio readDominio(byte[] in) throws DeserializerException {
		return (Dominio) this.xmlToObj(in, Dominio.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.eccezione.details.Dominio}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.eccezione.details.Dominio}
	 * @return Object type {@link org.openspcoop2.core.eccezione.details.Dominio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Dominio readDominioFromString(String in) throws DeserializerException {
		return (Dominio) this.xmlToObj(in.getBytes(), Dominio.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: dettaglio-eccezione
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.eccezione.details.DettaglioEccezione}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.eccezione.details.DettaglioEccezione}
	 * @return Object type {@link org.openspcoop2.core.eccezione.details.DettaglioEccezione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DettaglioEccezione readDettaglioEccezione(String fileName) throws DeserializerException {
		return (DettaglioEccezione) this.xmlToObj(fileName, DettaglioEccezione.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.eccezione.details.DettaglioEccezione}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.eccezione.details.DettaglioEccezione}
	 * @return Object type {@link org.openspcoop2.core.eccezione.details.DettaglioEccezione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DettaglioEccezione readDettaglioEccezione(File file) throws DeserializerException {
		return (DettaglioEccezione) this.xmlToObj(file, DettaglioEccezione.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.eccezione.details.DettaglioEccezione}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.eccezione.details.DettaglioEccezione}
	 * @return Object type {@link org.openspcoop2.core.eccezione.details.DettaglioEccezione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DettaglioEccezione readDettaglioEccezione(InputStream in) throws DeserializerException {
		return (DettaglioEccezione) this.xmlToObj(in, DettaglioEccezione.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.eccezione.details.DettaglioEccezione}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.eccezione.details.DettaglioEccezione}
	 * @return Object type {@link org.openspcoop2.core.eccezione.details.DettaglioEccezione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DettaglioEccezione readDettaglioEccezione(byte[] in) throws DeserializerException {
		return (DettaglioEccezione) this.xmlToObj(in, DettaglioEccezione.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.eccezione.details.DettaglioEccezione}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.eccezione.details.DettaglioEccezione}
	 * @return Object type {@link org.openspcoop2.core.eccezione.details.DettaglioEccezione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DettaglioEccezione readDettaglioEccezioneFromString(String in) throws DeserializerException {
		return (DettaglioEccezione) this.xmlToObj(in.getBytes(), DettaglioEccezione.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: eccezioni
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.eccezione.details.Eccezioni}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.eccezione.details.Eccezioni}
	 * @return Object type {@link org.openspcoop2.core.eccezione.details.Eccezioni}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Eccezioni readEccezioni(String fileName) throws DeserializerException {
		return (Eccezioni) this.xmlToObj(fileName, Eccezioni.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.eccezione.details.Eccezioni}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.eccezione.details.Eccezioni}
	 * @return Object type {@link org.openspcoop2.core.eccezione.details.Eccezioni}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Eccezioni readEccezioni(File file) throws DeserializerException {
		return (Eccezioni) this.xmlToObj(file, Eccezioni.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.eccezione.details.Eccezioni}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.eccezione.details.Eccezioni}
	 * @return Object type {@link org.openspcoop2.core.eccezione.details.Eccezioni}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Eccezioni readEccezioni(InputStream in) throws DeserializerException {
		return (Eccezioni) this.xmlToObj(in, Eccezioni.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.eccezione.details.Eccezioni}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.eccezione.details.Eccezioni}
	 * @return Object type {@link org.openspcoop2.core.eccezione.details.Eccezioni}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Eccezioni readEccezioni(byte[] in) throws DeserializerException {
		return (Eccezioni) this.xmlToObj(in, Eccezioni.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.eccezione.details.Eccezioni}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.eccezione.details.Eccezioni}
	 * @return Object type {@link org.openspcoop2.core.eccezione.details.Eccezioni}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Eccezioni readEccezioniFromString(String in) throws DeserializerException {
		return (Eccezioni) this.xmlToObj(in.getBytes(), Eccezioni.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: dettagli
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.eccezione.details.Dettagli}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.eccezione.details.Dettagli}
	 * @return Object type {@link org.openspcoop2.core.eccezione.details.Dettagli}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Dettagli readDettagli(String fileName) throws DeserializerException {
		return (Dettagli) this.xmlToObj(fileName, Dettagli.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.eccezione.details.Dettagli}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.eccezione.details.Dettagli}
	 * @return Object type {@link org.openspcoop2.core.eccezione.details.Dettagli}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Dettagli readDettagli(File file) throws DeserializerException {
		return (Dettagli) this.xmlToObj(file, Dettagli.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.eccezione.details.Dettagli}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.eccezione.details.Dettagli}
	 * @return Object type {@link org.openspcoop2.core.eccezione.details.Dettagli}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Dettagli readDettagli(InputStream in) throws DeserializerException {
		return (Dettagli) this.xmlToObj(in, Dettagli.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.eccezione.details.Dettagli}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.eccezione.details.Dettagli}
	 * @return Object type {@link org.openspcoop2.core.eccezione.details.Dettagli}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Dettagli readDettagli(byte[] in) throws DeserializerException {
		return (Dettagli) this.xmlToObj(in, Dettagli.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.eccezione.details.Dettagli}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.eccezione.details.Dettagli}
	 * @return Object type {@link org.openspcoop2.core.eccezione.details.Dettagli}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Dettagli readDettagliFromString(String in) throws DeserializerException {
		return (Dettagli) this.xmlToObj(in.getBytes(), Dettagli.class);
	}	
	
	
	

}
