/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it).
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
package org.openspcoop2.core.transazioni.utils.serializer;

import org.openspcoop2.generic_project.exception.DeserializerException;

import org.openspcoop2.core.transazioni.DumpAllegato;
import org.openspcoop2.core.transazioni.DumpHeaderAllegato;
import org.openspcoop2.core.transazioni.DumpMultipartHeader;
import org.openspcoop2.core.transazioni.DumpHeaderTrasporto;
import org.openspcoop2.core.transazioni.DumpContenuto;
import org.openspcoop2.core.transazioni.Transazione;
import org.openspcoop2.core.transazioni.DumpMessaggio;
import org.openspcoop2.core.transazioni.TransazioneExtendedInfo;
import org.openspcoop2.core.transazioni.IdDumpMessaggio;

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
	 Object: dump-allegato
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.transazioni.DumpAllegato}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.transazioni.DumpAllegato}
	 * @return Object type {@link org.openspcoop2.core.transazioni.DumpAllegato}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DumpAllegato readDumpAllegato(String fileName) throws DeserializerException {
		return (DumpAllegato) this.xmlToObj(fileName, DumpAllegato.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.transazioni.DumpAllegato}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.transazioni.DumpAllegato}
	 * @return Object type {@link org.openspcoop2.core.transazioni.DumpAllegato}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DumpAllegato readDumpAllegato(File file) throws DeserializerException {
		return (DumpAllegato) this.xmlToObj(file, DumpAllegato.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.transazioni.DumpAllegato}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.transazioni.DumpAllegato}
	 * @return Object type {@link org.openspcoop2.core.transazioni.DumpAllegato}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DumpAllegato readDumpAllegato(InputStream in) throws DeserializerException {
		return (DumpAllegato) this.xmlToObj(in, DumpAllegato.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.transazioni.DumpAllegato}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.transazioni.DumpAllegato}
	 * @return Object type {@link org.openspcoop2.core.transazioni.DumpAllegato}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DumpAllegato readDumpAllegato(byte[] in) throws DeserializerException {
		return (DumpAllegato) this.xmlToObj(in, DumpAllegato.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.transazioni.DumpAllegato}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.transazioni.DumpAllegato}
	 * @return Object type {@link org.openspcoop2.core.transazioni.DumpAllegato}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DumpAllegato readDumpAllegatoFromString(String in) throws DeserializerException {
		return (DumpAllegato) this.xmlToObj(in.getBytes(), DumpAllegato.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: dump-header-allegato
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.transazioni.DumpHeaderAllegato}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.transazioni.DumpHeaderAllegato}
	 * @return Object type {@link org.openspcoop2.core.transazioni.DumpHeaderAllegato}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DumpHeaderAllegato readDumpHeaderAllegato(String fileName) throws DeserializerException {
		return (DumpHeaderAllegato) this.xmlToObj(fileName, DumpHeaderAllegato.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.transazioni.DumpHeaderAllegato}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.transazioni.DumpHeaderAllegato}
	 * @return Object type {@link org.openspcoop2.core.transazioni.DumpHeaderAllegato}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DumpHeaderAllegato readDumpHeaderAllegato(File file) throws DeserializerException {
		return (DumpHeaderAllegato) this.xmlToObj(file, DumpHeaderAllegato.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.transazioni.DumpHeaderAllegato}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.transazioni.DumpHeaderAllegato}
	 * @return Object type {@link org.openspcoop2.core.transazioni.DumpHeaderAllegato}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DumpHeaderAllegato readDumpHeaderAllegato(InputStream in) throws DeserializerException {
		return (DumpHeaderAllegato) this.xmlToObj(in, DumpHeaderAllegato.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.transazioni.DumpHeaderAllegato}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.transazioni.DumpHeaderAllegato}
	 * @return Object type {@link org.openspcoop2.core.transazioni.DumpHeaderAllegato}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DumpHeaderAllegato readDumpHeaderAllegato(byte[] in) throws DeserializerException {
		return (DumpHeaderAllegato) this.xmlToObj(in, DumpHeaderAllegato.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.transazioni.DumpHeaderAllegato}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.transazioni.DumpHeaderAllegato}
	 * @return Object type {@link org.openspcoop2.core.transazioni.DumpHeaderAllegato}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DumpHeaderAllegato readDumpHeaderAllegatoFromString(String in) throws DeserializerException {
		return (DumpHeaderAllegato) this.xmlToObj(in.getBytes(), DumpHeaderAllegato.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: dump-multipart-header
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.transazioni.DumpMultipartHeader}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.transazioni.DumpMultipartHeader}
	 * @return Object type {@link org.openspcoop2.core.transazioni.DumpMultipartHeader}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DumpMultipartHeader readDumpMultipartHeader(String fileName) throws DeserializerException {
		return (DumpMultipartHeader) this.xmlToObj(fileName, DumpMultipartHeader.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.transazioni.DumpMultipartHeader}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.transazioni.DumpMultipartHeader}
	 * @return Object type {@link org.openspcoop2.core.transazioni.DumpMultipartHeader}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DumpMultipartHeader readDumpMultipartHeader(File file) throws DeserializerException {
		return (DumpMultipartHeader) this.xmlToObj(file, DumpMultipartHeader.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.transazioni.DumpMultipartHeader}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.transazioni.DumpMultipartHeader}
	 * @return Object type {@link org.openspcoop2.core.transazioni.DumpMultipartHeader}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DumpMultipartHeader readDumpMultipartHeader(InputStream in) throws DeserializerException {
		return (DumpMultipartHeader) this.xmlToObj(in, DumpMultipartHeader.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.transazioni.DumpMultipartHeader}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.transazioni.DumpMultipartHeader}
	 * @return Object type {@link org.openspcoop2.core.transazioni.DumpMultipartHeader}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DumpMultipartHeader readDumpMultipartHeader(byte[] in) throws DeserializerException {
		return (DumpMultipartHeader) this.xmlToObj(in, DumpMultipartHeader.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.transazioni.DumpMultipartHeader}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.transazioni.DumpMultipartHeader}
	 * @return Object type {@link org.openspcoop2.core.transazioni.DumpMultipartHeader}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DumpMultipartHeader readDumpMultipartHeaderFromString(String in) throws DeserializerException {
		return (DumpMultipartHeader) this.xmlToObj(in.getBytes(), DumpMultipartHeader.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: dump-header-trasporto
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.transazioni.DumpHeaderTrasporto}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.transazioni.DumpHeaderTrasporto}
	 * @return Object type {@link org.openspcoop2.core.transazioni.DumpHeaderTrasporto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DumpHeaderTrasporto readDumpHeaderTrasporto(String fileName) throws DeserializerException {
		return (DumpHeaderTrasporto) this.xmlToObj(fileName, DumpHeaderTrasporto.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.transazioni.DumpHeaderTrasporto}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.transazioni.DumpHeaderTrasporto}
	 * @return Object type {@link org.openspcoop2.core.transazioni.DumpHeaderTrasporto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DumpHeaderTrasporto readDumpHeaderTrasporto(File file) throws DeserializerException {
		return (DumpHeaderTrasporto) this.xmlToObj(file, DumpHeaderTrasporto.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.transazioni.DumpHeaderTrasporto}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.transazioni.DumpHeaderTrasporto}
	 * @return Object type {@link org.openspcoop2.core.transazioni.DumpHeaderTrasporto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DumpHeaderTrasporto readDumpHeaderTrasporto(InputStream in) throws DeserializerException {
		return (DumpHeaderTrasporto) this.xmlToObj(in, DumpHeaderTrasporto.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.transazioni.DumpHeaderTrasporto}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.transazioni.DumpHeaderTrasporto}
	 * @return Object type {@link org.openspcoop2.core.transazioni.DumpHeaderTrasporto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DumpHeaderTrasporto readDumpHeaderTrasporto(byte[] in) throws DeserializerException {
		return (DumpHeaderTrasporto) this.xmlToObj(in, DumpHeaderTrasporto.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.transazioni.DumpHeaderTrasporto}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.transazioni.DumpHeaderTrasporto}
	 * @return Object type {@link org.openspcoop2.core.transazioni.DumpHeaderTrasporto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DumpHeaderTrasporto readDumpHeaderTrasportoFromString(String in) throws DeserializerException {
		return (DumpHeaderTrasporto) this.xmlToObj(in.getBytes(), DumpHeaderTrasporto.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: dump-contenuto
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.transazioni.DumpContenuto}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.transazioni.DumpContenuto}
	 * @return Object type {@link org.openspcoop2.core.transazioni.DumpContenuto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DumpContenuto readDumpContenuto(String fileName) throws DeserializerException {
		return (DumpContenuto) this.xmlToObj(fileName, DumpContenuto.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.transazioni.DumpContenuto}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.transazioni.DumpContenuto}
	 * @return Object type {@link org.openspcoop2.core.transazioni.DumpContenuto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DumpContenuto readDumpContenuto(File file) throws DeserializerException {
		return (DumpContenuto) this.xmlToObj(file, DumpContenuto.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.transazioni.DumpContenuto}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.transazioni.DumpContenuto}
	 * @return Object type {@link org.openspcoop2.core.transazioni.DumpContenuto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DumpContenuto readDumpContenuto(InputStream in) throws DeserializerException {
		return (DumpContenuto) this.xmlToObj(in, DumpContenuto.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.transazioni.DumpContenuto}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.transazioni.DumpContenuto}
	 * @return Object type {@link org.openspcoop2.core.transazioni.DumpContenuto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DumpContenuto readDumpContenuto(byte[] in) throws DeserializerException {
		return (DumpContenuto) this.xmlToObj(in, DumpContenuto.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.transazioni.DumpContenuto}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.transazioni.DumpContenuto}
	 * @return Object type {@link org.openspcoop2.core.transazioni.DumpContenuto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DumpContenuto readDumpContenutoFromString(String in) throws DeserializerException {
		return (DumpContenuto) this.xmlToObj(in.getBytes(), DumpContenuto.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: transazione
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.transazioni.Transazione}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.transazioni.Transazione}
	 * @return Object type {@link org.openspcoop2.core.transazioni.Transazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Transazione readTransazione(String fileName) throws DeserializerException {
		return (Transazione) this.xmlToObj(fileName, Transazione.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.transazioni.Transazione}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.transazioni.Transazione}
	 * @return Object type {@link org.openspcoop2.core.transazioni.Transazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Transazione readTransazione(File file) throws DeserializerException {
		return (Transazione) this.xmlToObj(file, Transazione.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.transazioni.Transazione}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.transazioni.Transazione}
	 * @return Object type {@link org.openspcoop2.core.transazioni.Transazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Transazione readTransazione(InputStream in) throws DeserializerException {
		return (Transazione) this.xmlToObj(in, Transazione.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.transazioni.Transazione}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.transazioni.Transazione}
	 * @return Object type {@link org.openspcoop2.core.transazioni.Transazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Transazione readTransazione(byte[] in) throws DeserializerException {
		return (Transazione) this.xmlToObj(in, Transazione.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.transazioni.Transazione}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.transazioni.Transazione}
	 * @return Object type {@link org.openspcoop2.core.transazioni.Transazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Transazione readTransazioneFromString(String in) throws DeserializerException {
		return (Transazione) this.xmlToObj(in.getBytes(), Transazione.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: dump-messaggio
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.transazioni.DumpMessaggio}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.transazioni.DumpMessaggio}
	 * @return Object type {@link org.openspcoop2.core.transazioni.DumpMessaggio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DumpMessaggio readDumpMessaggio(String fileName) throws DeserializerException {
		return (DumpMessaggio) this.xmlToObj(fileName, DumpMessaggio.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.transazioni.DumpMessaggio}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.transazioni.DumpMessaggio}
	 * @return Object type {@link org.openspcoop2.core.transazioni.DumpMessaggio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DumpMessaggio readDumpMessaggio(File file) throws DeserializerException {
		return (DumpMessaggio) this.xmlToObj(file, DumpMessaggio.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.transazioni.DumpMessaggio}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.transazioni.DumpMessaggio}
	 * @return Object type {@link org.openspcoop2.core.transazioni.DumpMessaggio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DumpMessaggio readDumpMessaggio(InputStream in) throws DeserializerException {
		return (DumpMessaggio) this.xmlToObj(in, DumpMessaggio.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.transazioni.DumpMessaggio}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.transazioni.DumpMessaggio}
	 * @return Object type {@link org.openspcoop2.core.transazioni.DumpMessaggio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DumpMessaggio readDumpMessaggio(byte[] in) throws DeserializerException {
		return (DumpMessaggio) this.xmlToObj(in, DumpMessaggio.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.transazioni.DumpMessaggio}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.transazioni.DumpMessaggio}
	 * @return Object type {@link org.openspcoop2.core.transazioni.DumpMessaggio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DumpMessaggio readDumpMessaggioFromString(String in) throws DeserializerException {
		return (DumpMessaggio) this.xmlToObj(in.getBytes(), DumpMessaggio.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: transazione-extended-info
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.transazioni.TransazioneExtendedInfo}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.transazioni.TransazioneExtendedInfo}
	 * @return Object type {@link org.openspcoop2.core.transazioni.TransazioneExtendedInfo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public TransazioneExtendedInfo readTransazioneExtendedInfo(String fileName) throws DeserializerException {
		return (TransazioneExtendedInfo) this.xmlToObj(fileName, TransazioneExtendedInfo.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.transazioni.TransazioneExtendedInfo}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.transazioni.TransazioneExtendedInfo}
	 * @return Object type {@link org.openspcoop2.core.transazioni.TransazioneExtendedInfo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public TransazioneExtendedInfo readTransazioneExtendedInfo(File file) throws DeserializerException {
		return (TransazioneExtendedInfo) this.xmlToObj(file, TransazioneExtendedInfo.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.transazioni.TransazioneExtendedInfo}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.transazioni.TransazioneExtendedInfo}
	 * @return Object type {@link org.openspcoop2.core.transazioni.TransazioneExtendedInfo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public TransazioneExtendedInfo readTransazioneExtendedInfo(InputStream in) throws DeserializerException {
		return (TransazioneExtendedInfo) this.xmlToObj(in, TransazioneExtendedInfo.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.transazioni.TransazioneExtendedInfo}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.transazioni.TransazioneExtendedInfo}
	 * @return Object type {@link org.openspcoop2.core.transazioni.TransazioneExtendedInfo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public TransazioneExtendedInfo readTransazioneExtendedInfo(byte[] in) throws DeserializerException {
		return (TransazioneExtendedInfo) this.xmlToObj(in, TransazioneExtendedInfo.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.transazioni.TransazioneExtendedInfo}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.transazioni.TransazioneExtendedInfo}
	 * @return Object type {@link org.openspcoop2.core.transazioni.TransazioneExtendedInfo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public TransazioneExtendedInfo readTransazioneExtendedInfoFromString(String in) throws DeserializerException {
		return (TransazioneExtendedInfo) this.xmlToObj(in.getBytes(), TransazioneExtendedInfo.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: id-dump-messaggio
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.transazioni.IdDumpMessaggio}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.transazioni.IdDumpMessaggio}
	 * @return Object type {@link org.openspcoop2.core.transazioni.IdDumpMessaggio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdDumpMessaggio readIdDumpMessaggio(String fileName) throws DeserializerException {
		return (IdDumpMessaggio) this.xmlToObj(fileName, IdDumpMessaggio.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.transazioni.IdDumpMessaggio}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.transazioni.IdDumpMessaggio}
	 * @return Object type {@link org.openspcoop2.core.transazioni.IdDumpMessaggio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdDumpMessaggio readIdDumpMessaggio(File file) throws DeserializerException {
		return (IdDumpMessaggio) this.xmlToObj(file, IdDumpMessaggio.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.transazioni.IdDumpMessaggio}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.transazioni.IdDumpMessaggio}
	 * @return Object type {@link org.openspcoop2.core.transazioni.IdDumpMessaggio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdDumpMessaggio readIdDumpMessaggio(InputStream in) throws DeserializerException {
		return (IdDumpMessaggio) this.xmlToObj(in, IdDumpMessaggio.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.transazioni.IdDumpMessaggio}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.transazioni.IdDumpMessaggio}
	 * @return Object type {@link org.openspcoop2.core.transazioni.IdDumpMessaggio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdDumpMessaggio readIdDumpMessaggio(byte[] in) throws DeserializerException {
		return (IdDumpMessaggio) this.xmlToObj(in, IdDumpMessaggio.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.transazioni.IdDumpMessaggio}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.transazioni.IdDumpMessaggio}
	 * @return Object type {@link org.openspcoop2.core.transazioni.IdDumpMessaggio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdDumpMessaggio readIdDumpMessaggioFromString(String in) throws DeserializerException {
		return (IdDumpMessaggio) this.xmlToObj(in.getBytes(), IdDumpMessaggio.class);
	}	
	
	
	

}
