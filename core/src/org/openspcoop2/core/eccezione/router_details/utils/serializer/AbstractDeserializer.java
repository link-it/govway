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
package org.openspcoop2.core.eccezione.router_details.utils.serializer;

import org.openspcoop2.generic_project.exception.DeserializerException;

import org.openspcoop2.core.eccezione.router_details.DominioSoggetto;
import org.openspcoop2.core.eccezione.router_details.Dettaglio;
import org.openspcoop2.core.eccezione.router_details.Dominio;
import org.openspcoop2.core.eccezione.router_details.DettaglioRouting;

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
	 Object: dominio-soggetto
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.eccezione.router_details.DominioSoggetto}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.eccezione.router_details.DominioSoggetto}
	 * @return Object type {@link org.openspcoop2.core.eccezione.router_details.DominioSoggetto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DominioSoggetto readDominioSoggetto(String fileName) throws DeserializerException {
		return (DominioSoggetto) this.xmlToObj(fileName, DominioSoggetto.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.eccezione.router_details.DominioSoggetto}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.eccezione.router_details.DominioSoggetto}
	 * @return Object type {@link org.openspcoop2.core.eccezione.router_details.DominioSoggetto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DominioSoggetto readDominioSoggetto(File file) throws DeserializerException {
		return (DominioSoggetto) this.xmlToObj(file, DominioSoggetto.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.eccezione.router_details.DominioSoggetto}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.eccezione.router_details.DominioSoggetto}
	 * @return Object type {@link org.openspcoop2.core.eccezione.router_details.DominioSoggetto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DominioSoggetto readDominioSoggetto(InputStream in) throws DeserializerException {
		return (DominioSoggetto) this.xmlToObj(in, DominioSoggetto.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.eccezione.router_details.DominioSoggetto}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.eccezione.router_details.DominioSoggetto}
	 * @return Object type {@link org.openspcoop2.core.eccezione.router_details.DominioSoggetto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DominioSoggetto readDominioSoggetto(byte[] in) throws DeserializerException {
		return (DominioSoggetto) this.xmlToObj(in, DominioSoggetto.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.eccezione.router_details.DominioSoggetto}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.eccezione.router_details.DominioSoggetto}
	 * @return Object type {@link org.openspcoop2.core.eccezione.router_details.DominioSoggetto}
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
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.eccezione.router_details.Dettaglio}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.eccezione.router_details.Dettaglio}
	 * @return Object type {@link org.openspcoop2.core.eccezione.router_details.Dettaglio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Dettaglio readDettaglio(String fileName) throws DeserializerException {
		return (Dettaglio) this.xmlToObj(fileName, Dettaglio.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.eccezione.router_details.Dettaglio}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.eccezione.router_details.Dettaglio}
	 * @return Object type {@link org.openspcoop2.core.eccezione.router_details.Dettaglio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Dettaglio readDettaglio(File file) throws DeserializerException {
		return (Dettaglio) this.xmlToObj(file, Dettaglio.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.eccezione.router_details.Dettaglio}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.eccezione.router_details.Dettaglio}
	 * @return Object type {@link org.openspcoop2.core.eccezione.router_details.Dettaglio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Dettaglio readDettaglio(InputStream in) throws DeserializerException {
		return (Dettaglio) this.xmlToObj(in, Dettaglio.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.eccezione.router_details.Dettaglio}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.eccezione.router_details.Dettaglio}
	 * @return Object type {@link org.openspcoop2.core.eccezione.router_details.Dettaglio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Dettaglio readDettaglio(byte[] in) throws DeserializerException {
		return (Dettaglio) this.xmlToObj(in, Dettaglio.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.eccezione.router_details.Dettaglio}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.eccezione.router_details.Dettaglio}
	 * @return Object type {@link org.openspcoop2.core.eccezione.router_details.Dettaglio}
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
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.eccezione.router_details.Dominio}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.eccezione.router_details.Dominio}
	 * @return Object type {@link org.openspcoop2.core.eccezione.router_details.Dominio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Dominio readDominio(String fileName) throws DeserializerException {
		return (Dominio) this.xmlToObj(fileName, Dominio.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.eccezione.router_details.Dominio}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.eccezione.router_details.Dominio}
	 * @return Object type {@link org.openspcoop2.core.eccezione.router_details.Dominio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Dominio readDominio(File file) throws DeserializerException {
		return (Dominio) this.xmlToObj(file, Dominio.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.eccezione.router_details.Dominio}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.eccezione.router_details.Dominio}
	 * @return Object type {@link org.openspcoop2.core.eccezione.router_details.Dominio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Dominio readDominio(InputStream in) throws DeserializerException {
		return (Dominio) this.xmlToObj(in, Dominio.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.eccezione.router_details.Dominio}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.eccezione.router_details.Dominio}
	 * @return Object type {@link org.openspcoop2.core.eccezione.router_details.Dominio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Dominio readDominio(byte[] in) throws DeserializerException {
		return (Dominio) this.xmlToObj(in, Dominio.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.eccezione.router_details.Dominio}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.eccezione.router_details.Dominio}
	 * @return Object type {@link org.openspcoop2.core.eccezione.router_details.Dominio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Dominio readDominioFromString(String in) throws DeserializerException {
		return (Dominio) this.xmlToObj(in.getBytes(), Dominio.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: dettaglio-routing
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.eccezione.router_details.DettaglioRouting}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.eccezione.router_details.DettaglioRouting}
	 * @return Object type {@link org.openspcoop2.core.eccezione.router_details.DettaglioRouting}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DettaglioRouting readDettaglioRouting(String fileName) throws DeserializerException {
		return (DettaglioRouting) this.xmlToObj(fileName, DettaglioRouting.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.eccezione.router_details.DettaglioRouting}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.eccezione.router_details.DettaglioRouting}
	 * @return Object type {@link org.openspcoop2.core.eccezione.router_details.DettaglioRouting}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DettaglioRouting readDettaglioRouting(File file) throws DeserializerException {
		return (DettaglioRouting) this.xmlToObj(file, DettaglioRouting.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.eccezione.router_details.DettaglioRouting}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.eccezione.router_details.DettaglioRouting}
	 * @return Object type {@link org.openspcoop2.core.eccezione.router_details.DettaglioRouting}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DettaglioRouting readDettaglioRouting(InputStream in) throws DeserializerException {
		return (DettaglioRouting) this.xmlToObj(in, DettaglioRouting.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.eccezione.router_details.DettaglioRouting}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.eccezione.router_details.DettaglioRouting}
	 * @return Object type {@link org.openspcoop2.core.eccezione.router_details.DettaglioRouting}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DettaglioRouting readDettaglioRouting(byte[] in) throws DeserializerException {
		return (DettaglioRouting) this.xmlToObj(in, DettaglioRouting.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.eccezione.router_details.DettaglioRouting}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.eccezione.router_details.DettaglioRouting}
	 * @return Object type {@link org.openspcoop2.core.eccezione.router_details.DettaglioRouting}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DettaglioRouting readDettaglioRoutingFromString(String in) throws DeserializerException {
		return (DettaglioRouting) this.xmlToObj(in.getBytes(), DettaglioRouting.class);
	}	
	
	
	

}
