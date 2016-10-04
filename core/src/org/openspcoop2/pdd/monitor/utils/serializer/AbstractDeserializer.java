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
package org.openspcoop2.pdd.monitor.utils.serializer;

import org.openspcoop2.generic_project.exception.DeserializerException;

import org.openspcoop2.pdd.monitor.BustaSoggetto;
import org.openspcoop2.pdd.monitor.Busta;
import org.openspcoop2.pdd.monitor.BustaServizio;
import org.openspcoop2.pdd.monitor.StatoPdd;
import org.openspcoop2.pdd.monitor.Openspcoop2;
import org.openspcoop2.pdd.monitor.Messaggio;
import org.openspcoop2.pdd.monitor.ServizioApplicativoConsegna;
import org.openspcoop2.pdd.monitor.Dettaglio;
import org.openspcoop2.pdd.monitor.Filtro;
import org.openspcoop2.pdd.monitor.Proprieta;

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
	 Object: busta-soggetto
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.pdd.monitor.BustaSoggetto}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.pdd.monitor.BustaSoggetto}
	 * @return Object type {@link org.openspcoop2.pdd.monitor.BustaSoggetto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public BustaSoggetto readBustaSoggetto(String fileName) throws DeserializerException {
		return (BustaSoggetto) this.xmlToObj(fileName, BustaSoggetto.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.pdd.monitor.BustaSoggetto}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.pdd.monitor.BustaSoggetto}
	 * @return Object type {@link org.openspcoop2.pdd.monitor.BustaSoggetto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public BustaSoggetto readBustaSoggetto(File file) throws DeserializerException {
		return (BustaSoggetto) this.xmlToObj(file, BustaSoggetto.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.pdd.monitor.BustaSoggetto}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.pdd.monitor.BustaSoggetto}
	 * @return Object type {@link org.openspcoop2.pdd.monitor.BustaSoggetto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public BustaSoggetto readBustaSoggetto(InputStream in) throws DeserializerException {
		return (BustaSoggetto) this.xmlToObj(in, BustaSoggetto.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.pdd.monitor.BustaSoggetto}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.pdd.monitor.BustaSoggetto}
	 * @return Object type {@link org.openspcoop2.pdd.monitor.BustaSoggetto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public BustaSoggetto readBustaSoggetto(byte[] in) throws DeserializerException {
		return (BustaSoggetto) this.xmlToObj(in, BustaSoggetto.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.pdd.monitor.BustaSoggetto}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.pdd.monitor.BustaSoggetto}
	 * @return Object type {@link org.openspcoop2.pdd.monitor.BustaSoggetto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public BustaSoggetto readBustaSoggettoFromString(String in) throws DeserializerException {
		return (BustaSoggetto) this.xmlToObj(in.getBytes(), BustaSoggetto.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: busta
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.pdd.monitor.Busta}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.pdd.monitor.Busta}
	 * @return Object type {@link org.openspcoop2.pdd.monitor.Busta}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Busta readBusta(String fileName) throws DeserializerException {
		return (Busta) this.xmlToObj(fileName, Busta.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.pdd.monitor.Busta}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.pdd.monitor.Busta}
	 * @return Object type {@link org.openspcoop2.pdd.monitor.Busta}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Busta readBusta(File file) throws DeserializerException {
		return (Busta) this.xmlToObj(file, Busta.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.pdd.monitor.Busta}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.pdd.monitor.Busta}
	 * @return Object type {@link org.openspcoop2.pdd.monitor.Busta}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Busta readBusta(InputStream in) throws DeserializerException {
		return (Busta) this.xmlToObj(in, Busta.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.pdd.monitor.Busta}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.pdd.monitor.Busta}
	 * @return Object type {@link org.openspcoop2.pdd.monitor.Busta}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Busta readBusta(byte[] in) throws DeserializerException {
		return (Busta) this.xmlToObj(in, Busta.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.pdd.monitor.Busta}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.pdd.monitor.Busta}
	 * @return Object type {@link org.openspcoop2.pdd.monitor.Busta}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Busta readBustaFromString(String in) throws DeserializerException {
		return (Busta) this.xmlToObj(in.getBytes(), Busta.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: busta-servizio
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.pdd.monitor.BustaServizio}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.pdd.monitor.BustaServizio}
	 * @return Object type {@link org.openspcoop2.pdd.monitor.BustaServizio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public BustaServizio readBustaServizio(String fileName) throws DeserializerException {
		return (BustaServizio) this.xmlToObj(fileName, BustaServizio.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.pdd.monitor.BustaServizio}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.pdd.monitor.BustaServizio}
	 * @return Object type {@link org.openspcoop2.pdd.monitor.BustaServizio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public BustaServizio readBustaServizio(File file) throws DeserializerException {
		return (BustaServizio) this.xmlToObj(file, BustaServizio.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.pdd.monitor.BustaServizio}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.pdd.monitor.BustaServizio}
	 * @return Object type {@link org.openspcoop2.pdd.monitor.BustaServizio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public BustaServizio readBustaServizio(InputStream in) throws DeserializerException {
		return (BustaServizio) this.xmlToObj(in, BustaServizio.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.pdd.monitor.BustaServizio}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.pdd.monitor.BustaServizio}
	 * @return Object type {@link org.openspcoop2.pdd.monitor.BustaServizio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public BustaServizio readBustaServizio(byte[] in) throws DeserializerException {
		return (BustaServizio) this.xmlToObj(in, BustaServizio.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.pdd.monitor.BustaServizio}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.pdd.monitor.BustaServizio}
	 * @return Object type {@link org.openspcoop2.pdd.monitor.BustaServizio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public BustaServizio readBustaServizioFromString(String in) throws DeserializerException {
		return (BustaServizio) this.xmlToObj(in.getBytes(), BustaServizio.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: stato-pdd
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.pdd.monitor.StatoPdd}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.pdd.monitor.StatoPdd}
	 * @return Object type {@link org.openspcoop2.pdd.monitor.StatoPdd}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public StatoPdd readStatoPdd(String fileName) throws DeserializerException {
		return (StatoPdd) this.xmlToObj(fileName, StatoPdd.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.pdd.monitor.StatoPdd}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.pdd.monitor.StatoPdd}
	 * @return Object type {@link org.openspcoop2.pdd.monitor.StatoPdd}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public StatoPdd readStatoPdd(File file) throws DeserializerException {
		return (StatoPdd) this.xmlToObj(file, StatoPdd.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.pdd.monitor.StatoPdd}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.pdd.monitor.StatoPdd}
	 * @return Object type {@link org.openspcoop2.pdd.monitor.StatoPdd}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public StatoPdd readStatoPdd(InputStream in) throws DeserializerException {
		return (StatoPdd) this.xmlToObj(in, StatoPdd.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.pdd.monitor.StatoPdd}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.pdd.monitor.StatoPdd}
	 * @return Object type {@link org.openspcoop2.pdd.monitor.StatoPdd}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public StatoPdd readStatoPdd(byte[] in) throws DeserializerException {
		return (StatoPdd) this.xmlToObj(in, StatoPdd.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.pdd.monitor.StatoPdd}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.pdd.monitor.StatoPdd}
	 * @return Object type {@link org.openspcoop2.pdd.monitor.StatoPdd}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public StatoPdd readStatoPddFromString(String in) throws DeserializerException {
		return (StatoPdd) this.xmlToObj(in.getBytes(), StatoPdd.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: openspcoop2
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.pdd.monitor.Openspcoop2}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.pdd.monitor.Openspcoop2}
	 * @return Object type {@link org.openspcoop2.pdd.monitor.Openspcoop2}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Openspcoop2 readOpenspcoop2(String fileName) throws DeserializerException {
		return (Openspcoop2) this.xmlToObj(fileName, Openspcoop2.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.pdd.monitor.Openspcoop2}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.pdd.monitor.Openspcoop2}
	 * @return Object type {@link org.openspcoop2.pdd.monitor.Openspcoop2}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Openspcoop2 readOpenspcoop2(File file) throws DeserializerException {
		return (Openspcoop2) this.xmlToObj(file, Openspcoop2.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.pdd.monitor.Openspcoop2}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.pdd.monitor.Openspcoop2}
	 * @return Object type {@link org.openspcoop2.pdd.monitor.Openspcoop2}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Openspcoop2 readOpenspcoop2(InputStream in) throws DeserializerException {
		return (Openspcoop2) this.xmlToObj(in, Openspcoop2.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.pdd.monitor.Openspcoop2}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.pdd.monitor.Openspcoop2}
	 * @return Object type {@link org.openspcoop2.pdd.monitor.Openspcoop2}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Openspcoop2 readOpenspcoop2(byte[] in) throws DeserializerException {
		return (Openspcoop2) this.xmlToObj(in, Openspcoop2.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.pdd.monitor.Openspcoop2}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.pdd.monitor.Openspcoop2}
	 * @return Object type {@link org.openspcoop2.pdd.monitor.Openspcoop2}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Openspcoop2 readOpenspcoop2FromString(String in) throws DeserializerException {
		return (Openspcoop2) this.xmlToObj(in.getBytes(), Openspcoop2.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: messaggio
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.pdd.monitor.Messaggio}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.pdd.monitor.Messaggio}
	 * @return Object type {@link org.openspcoop2.pdd.monitor.Messaggio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Messaggio readMessaggio(String fileName) throws DeserializerException {
		return (Messaggio) this.xmlToObj(fileName, Messaggio.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.pdd.monitor.Messaggio}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.pdd.monitor.Messaggio}
	 * @return Object type {@link org.openspcoop2.pdd.monitor.Messaggio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Messaggio readMessaggio(File file) throws DeserializerException {
		return (Messaggio) this.xmlToObj(file, Messaggio.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.pdd.monitor.Messaggio}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.pdd.monitor.Messaggio}
	 * @return Object type {@link org.openspcoop2.pdd.monitor.Messaggio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Messaggio readMessaggio(InputStream in) throws DeserializerException {
		return (Messaggio) this.xmlToObj(in, Messaggio.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.pdd.monitor.Messaggio}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.pdd.monitor.Messaggio}
	 * @return Object type {@link org.openspcoop2.pdd.monitor.Messaggio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Messaggio readMessaggio(byte[] in) throws DeserializerException {
		return (Messaggio) this.xmlToObj(in, Messaggio.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.pdd.monitor.Messaggio}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.pdd.monitor.Messaggio}
	 * @return Object type {@link org.openspcoop2.pdd.monitor.Messaggio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Messaggio readMessaggioFromString(String in) throws DeserializerException {
		return (Messaggio) this.xmlToObj(in.getBytes(), Messaggio.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: servizio-applicativo-consegna
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.pdd.monitor.ServizioApplicativoConsegna}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.pdd.monitor.ServizioApplicativoConsegna}
	 * @return Object type {@link org.openspcoop2.pdd.monitor.ServizioApplicativoConsegna}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ServizioApplicativoConsegna readServizioApplicativoConsegna(String fileName) throws DeserializerException {
		return (ServizioApplicativoConsegna) this.xmlToObj(fileName, ServizioApplicativoConsegna.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.pdd.monitor.ServizioApplicativoConsegna}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.pdd.monitor.ServizioApplicativoConsegna}
	 * @return Object type {@link org.openspcoop2.pdd.monitor.ServizioApplicativoConsegna}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ServizioApplicativoConsegna readServizioApplicativoConsegna(File file) throws DeserializerException {
		return (ServizioApplicativoConsegna) this.xmlToObj(file, ServizioApplicativoConsegna.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.pdd.monitor.ServizioApplicativoConsegna}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.pdd.monitor.ServizioApplicativoConsegna}
	 * @return Object type {@link org.openspcoop2.pdd.monitor.ServizioApplicativoConsegna}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ServizioApplicativoConsegna readServizioApplicativoConsegna(InputStream in) throws DeserializerException {
		return (ServizioApplicativoConsegna) this.xmlToObj(in, ServizioApplicativoConsegna.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.pdd.monitor.ServizioApplicativoConsegna}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.pdd.monitor.ServizioApplicativoConsegna}
	 * @return Object type {@link org.openspcoop2.pdd.monitor.ServizioApplicativoConsegna}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ServizioApplicativoConsegna readServizioApplicativoConsegna(byte[] in) throws DeserializerException {
		return (ServizioApplicativoConsegna) this.xmlToObj(in, ServizioApplicativoConsegna.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.pdd.monitor.ServizioApplicativoConsegna}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.pdd.monitor.ServizioApplicativoConsegna}
	 * @return Object type {@link org.openspcoop2.pdd.monitor.ServizioApplicativoConsegna}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ServizioApplicativoConsegna readServizioApplicativoConsegnaFromString(String in) throws DeserializerException {
		return (ServizioApplicativoConsegna) this.xmlToObj(in.getBytes(), ServizioApplicativoConsegna.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: dettaglio
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.pdd.monitor.Dettaglio}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.pdd.monitor.Dettaglio}
	 * @return Object type {@link org.openspcoop2.pdd.monitor.Dettaglio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Dettaglio readDettaglio(String fileName) throws DeserializerException {
		return (Dettaglio) this.xmlToObj(fileName, Dettaglio.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.pdd.monitor.Dettaglio}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.pdd.monitor.Dettaglio}
	 * @return Object type {@link org.openspcoop2.pdd.monitor.Dettaglio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Dettaglio readDettaglio(File file) throws DeserializerException {
		return (Dettaglio) this.xmlToObj(file, Dettaglio.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.pdd.monitor.Dettaglio}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.pdd.monitor.Dettaglio}
	 * @return Object type {@link org.openspcoop2.pdd.monitor.Dettaglio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Dettaglio readDettaglio(InputStream in) throws DeserializerException {
		return (Dettaglio) this.xmlToObj(in, Dettaglio.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.pdd.monitor.Dettaglio}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.pdd.monitor.Dettaglio}
	 * @return Object type {@link org.openspcoop2.pdd.monitor.Dettaglio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Dettaglio readDettaglio(byte[] in) throws DeserializerException {
		return (Dettaglio) this.xmlToObj(in, Dettaglio.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.pdd.monitor.Dettaglio}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.pdd.monitor.Dettaglio}
	 * @return Object type {@link org.openspcoop2.pdd.monitor.Dettaglio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Dettaglio readDettaglioFromString(String in) throws DeserializerException {
		return (Dettaglio) this.xmlToObj(in.getBytes(), Dettaglio.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: filtro
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.pdd.monitor.Filtro}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.pdd.monitor.Filtro}
	 * @return Object type {@link org.openspcoop2.pdd.monitor.Filtro}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Filtro readFiltro(String fileName) throws DeserializerException {
		return (Filtro) this.xmlToObj(fileName, Filtro.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.pdd.monitor.Filtro}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.pdd.monitor.Filtro}
	 * @return Object type {@link org.openspcoop2.pdd.monitor.Filtro}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Filtro readFiltro(File file) throws DeserializerException {
		return (Filtro) this.xmlToObj(file, Filtro.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.pdd.monitor.Filtro}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.pdd.monitor.Filtro}
	 * @return Object type {@link org.openspcoop2.pdd.monitor.Filtro}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Filtro readFiltro(InputStream in) throws DeserializerException {
		return (Filtro) this.xmlToObj(in, Filtro.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.pdd.monitor.Filtro}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.pdd.monitor.Filtro}
	 * @return Object type {@link org.openspcoop2.pdd.monitor.Filtro}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Filtro readFiltro(byte[] in) throws DeserializerException {
		return (Filtro) this.xmlToObj(in, Filtro.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.pdd.monitor.Filtro}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.pdd.monitor.Filtro}
	 * @return Object type {@link org.openspcoop2.pdd.monitor.Filtro}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Filtro readFiltroFromString(String in) throws DeserializerException {
		return (Filtro) this.xmlToObj(in.getBytes(), Filtro.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: proprieta
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.pdd.monitor.Proprieta}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.pdd.monitor.Proprieta}
	 * @return Object type {@link org.openspcoop2.pdd.monitor.Proprieta}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Proprieta readProprieta(String fileName) throws DeserializerException {
		return (Proprieta) this.xmlToObj(fileName, Proprieta.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.pdd.monitor.Proprieta}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.pdd.monitor.Proprieta}
	 * @return Object type {@link org.openspcoop2.pdd.monitor.Proprieta}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Proprieta readProprieta(File file) throws DeserializerException {
		return (Proprieta) this.xmlToObj(file, Proprieta.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.pdd.monitor.Proprieta}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.pdd.monitor.Proprieta}
	 * @return Object type {@link org.openspcoop2.pdd.monitor.Proprieta}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Proprieta readProprieta(InputStream in) throws DeserializerException {
		return (Proprieta) this.xmlToObj(in, Proprieta.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.pdd.monitor.Proprieta}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.pdd.monitor.Proprieta}
	 * @return Object type {@link org.openspcoop2.pdd.monitor.Proprieta}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Proprieta readProprieta(byte[] in) throws DeserializerException {
		return (Proprieta) this.xmlToObj(in, Proprieta.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.pdd.monitor.Proprieta}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.pdd.monitor.Proprieta}
	 * @return Object type {@link org.openspcoop2.pdd.monitor.Proprieta}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Proprieta readProprietaFromString(String in) throws DeserializerException {
		return (Proprieta) this.xmlToObj(in.getBytes(), Proprieta.class);
	}	
	
	
	

}
