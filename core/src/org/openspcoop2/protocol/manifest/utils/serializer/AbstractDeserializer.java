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
package org.openspcoop2.protocol.manifest.utils.serializer;

import org.openspcoop2.generic_project.exception.DeserializerException;

import org.openspcoop2.protocol.manifest.SoapHeaderBypassMustUnderstandHeader;
import org.openspcoop2.protocol.manifest.Tipi;
import org.openspcoop2.protocol.manifest.Servizi;
import org.openspcoop2.protocol.manifest.Profilo;
import org.openspcoop2.protocol.manifest.Funzionalita;
import org.openspcoop2.protocol.manifest.WebEmptyContext;
import org.openspcoop2.protocol.manifest.SoapHeaderBypassMustUnderstand;
import org.openspcoop2.protocol.manifest.Openspcoop2;
import org.openspcoop2.protocol.manifest.Web;
import org.openspcoop2.protocol.manifest.RegistroServizi;
import org.openspcoop2.protocol.manifest.UrlMapping;
import org.openspcoop2.protocol.manifest.Binding;
import org.openspcoop2.protocol.manifest.Versioni;
import org.openspcoop2.protocol.manifest.Soggetti;

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
	 Object: soapHeaderBypassMustUnderstandHeader
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.protocol.manifest.SoapHeaderBypassMustUnderstandHeader}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.SoapHeaderBypassMustUnderstandHeader}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.SoapHeaderBypassMustUnderstandHeader}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SoapHeaderBypassMustUnderstandHeader readSoapHeaderBypassMustUnderstandHeader(String fileName) throws DeserializerException {
		return (SoapHeaderBypassMustUnderstandHeader) this.xmlToObj(fileName, SoapHeaderBypassMustUnderstandHeader.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.protocol.manifest.SoapHeaderBypassMustUnderstandHeader}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.SoapHeaderBypassMustUnderstandHeader}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.SoapHeaderBypassMustUnderstandHeader}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SoapHeaderBypassMustUnderstandHeader readSoapHeaderBypassMustUnderstandHeader(File file) throws DeserializerException {
		return (SoapHeaderBypassMustUnderstandHeader) this.xmlToObj(file, SoapHeaderBypassMustUnderstandHeader.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.SoapHeaderBypassMustUnderstandHeader}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.SoapHeaderBypassMustUnderstandHeader}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.SoapHeaderBypassMustUnderstandHeader}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SoapHeaderBypassMustUnderstandHeader readSoapHeaderBypassMustUnderstandHeader(InputStream in) throws DeserializerException {
		return (SoapHeaderBypassMustUnderstandHeader) this.xmlToObj(in, SoapHeaderBypassMustUnderstandHeader.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.SoapHeaderBypassMustUnderstandHeader}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.SoapHeaderBypassMustUnderstandHeader}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.SoapHeaderBypassMustUnderstandHeader}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SoapHeaderBypassMustUnderstandHeader readSoapHeaderBypassMustUnderstandHeader(byte[] in) throws DeserializerException {
		return (SoapHeaderBypassMustUnderstandHeader) this.xmlToObj(in, SoapHeaderBypassMustUnderstandHeader.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.SoapHeaderBypassMustUnderstandHeader}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.SoapHeaderBypassMustUnderstandHeader}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.SoapHeaderBypassMustUnderstandHeader}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SoapHeaderBypassMustUnderstandHeader readSoapHeaderBypassMustUnderstandHeaderFromString(String in) throws DeserializerException {
		return (SoapHeaderBypassMustUnderstandHeader) this.xmlToObj(in.getBytes(), SoapHeaderBypassMustUnderstandHeader.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: tipi
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.protocol.manifest.Tipi}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.Tipi}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.Tipi}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Tipi readTipi(String fileName) throws DeserializerException {
		return (Tipi) this.xmlToObj(fileName, Tipi.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.protocol.manifest.Tipi}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.Tipi}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.Tipi}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Tipi readTipi(File file) throws DeserializerException {
		return (Tipi) this.xmlToObj(file, Tipi.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.Tipi}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.Tipi}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.Tipi}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Tipi readTipi(InputStream in) throws DeserializerException {
		return (Tipi) this.xmlToObj(in, Tipi.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.Tipi}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.Tipi}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.Tipi}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Tipi readTipi(byte[] in) throws DeserializerException {
		return (Tipi) this.xmlToObj(in, Tipi.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.Tipi}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.Tipi}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.Tipi}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Tipi readTipiFromString(String in) throws DeserializerException {
		return (Tipi) this.xmlToObj(in.getBytes(), Tipi.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: servizi
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.protocol.manifest.Servizi}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.Servizi}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.Servizi}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Servizi readServizi(String fileName) throws DeserializerException {
		return (Servizi) this.xmlToObj(fileName, Servizi.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.protocol.manifest.Servizi}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.Servizi}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.Servizi}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Servizi readServizi(File file) throws DeserializerException {
		return (Servizi) this.xmlToObj(file, Servizi.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.Servizi}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.Servizi}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.Servizi}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Servizi readServizi(InputStream in) throws DeserializerException {
		return (Servizi) this.xmlToObj(in, Servizi.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.Servizi}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.Servizi}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.Servizi}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Servizi readServizi(byte[] in) throws DeserializerException {
		return (Servizi) this.xmlToObj(in, Servizi.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.Servizi}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.Servizi}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.Servizi}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Servizi readServiziFromString(String in) throws DeserializerException {
		return (Servizi) this.xmlToObj(in.getBytes(), Servizi.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: profilo
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.protocol.manifest.Profilo}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.Profilo}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.Profilo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Profilo readProfilo(String fileName) throws DeserializerException {
		return (Profilo) this.xmlToObj(fileName, Profilo.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.protocol.manifest.Profilo}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.Profilo}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.Profilo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Profilo readProfilo(File file) throws DeserializerException {
		return (Profilo) this.xmlToObj(file, Profilo.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.Profilo}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.Profilo}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.Profilo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Profilo readProfilo(InputStream in) throws DeserializerException {
		return (Profilo) this.xmlToObj(in, Profilo.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.Profilo}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.Profilo}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.Profilo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Profilo readProfilo(byte[] in) throws DeserializerException {
		return (Profilo) this.xmlToObj(in, Profilo.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.Profilo}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.Profilo}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.Profilo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Profilo readProfiloFromString(String in) throws DeserializerException {
		return (Profilo) this.xmlToObj(in.getBytes(), Profilo.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: funzionalita
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.protocol.manifest.Funzionalita}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.Funzionalita}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.Funzionalita}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Funzionalita readFunzionalita(String fileName) throws DeserializerException {
		return (Funzionalita) this.xmlToObj(fileName, Funzionalita.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.protocol.manifest.Funzionalita}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.Funzionalita}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.Funzionalita}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Funzionalita readFunzionalita(File file) throws DeserializerException {
		return (Funzionalita) this.xmlToObj(file, Funzionalita.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.Funzionalita}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.Funzionalita}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.Funzionalita}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Funzionalita readFunzionalita(InputStream in) throws DeserializerException {
		return (Funzionalita) this.xmlToObj(in, Funzionalita.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.Funzionalita}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.Funzionalita}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.Funzionalita}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Funzionalita readFunzionalita(byte[] in) throws DeserializerException {
		return (Funzionalita) this.xmlToObj(in, Funzionalita.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.Funzionalita}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.Funzionalita}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.Funzionalita}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Funzionalita readFunzionalitaFromString(String in) throws DeserializerException {
		return (Funzionalita) this.xmlToObj(in.getBytes(), Funzionalita.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: webEmptyContext
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.protocol.manifest.WebEmptyContext}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.WebEmptyContext}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.WebEmptyContext}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public WebEmptyContext readWebEmptyContext(String fileName) throws DeserializerException {
		return (WebEmptyContext) this.xmlToObj(fileName, WebEmptyContext.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.protocol.manifest.WebEmptyContext}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.WebEmptyContext}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.WebEmptyContext}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public WebEmptyContext readWebEmptyContext(File file) throws DeserializerException {
		return (WebEmptyContext) this.xmlToObj(file, WebEmptyContext.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.WebEmptyContext}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.WebEmptyContext}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.WebEmptyContext}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public WebEmptyContext readWebEmptyContext(InputStream in) throws DeserializerException {
		return (WebEmptyContext) this.xmlToObj(in, WebEmptyContext.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.WebEmptyContext}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.WebEmptyContext}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.WebEmptyContext}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public WebEmptyContext readWebEmptyContext(byte[] in) throws DeserializerException {
		return (WebEmptyContext) this.xmlToObj(in, WebEmptyContext.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.WebEmptyContext}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.WebEmptyContext}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.WebEmptyContext}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public WebEmptyContext readWebEmptyContextFromString(String in) throws DeserializerException {
		return (WebEmptyContext) this.xmlToObj(in.getBytes(), WebEmptyContext.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: soapHeaderBypassMustUnderstand
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.protocol.manifest.SoapHeaderBypassMustUnderstand}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.SoapHeaderBypassMustUnderstand}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.SoapHeaderBypassMustUnderstand}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SoapHeaderBypassMustUnderstand readSoapHeaderBypassMustUnderstand(String fileName) throws DeserializerException {
		return (SoapHeaderBypassMustUnderstand) this.xmlToObj(fileName, SoapHeaderBypassMustUnderstand.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.protocol.manifest.SoapHeaderBypassMustUnderstand}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.SoapHeaderBypassMustUnderstand}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.SoapHeaderBypassMustUnderstand}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SoapHeaderBypassMustUnderstand readSoapHeaderBypassMustUnderstand(File file) throws DeserializerException {
		return (SoapHeaderBypassMustUnderstand) this.xmlToObj(file, SoapHeaderBypassMustUnderstand.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.SoapHeaderBypassMustUnderstand}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.SoapHeaderBypassMustUnderstand}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.SoapHeaderBypassMustUnderstand}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SoapHeaderBypassMustUnderstand readSoapHeaderBypassMustUnderstand(InputStream in) throws DeserializerException {
		return (SoapHeaderBypassMustUnderstand) this.xmlToObj(in, SoapHeaderBypassMustUnderstand.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.SoapHeaderBypassMustUnderstand}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.SoapHeaderBypassMustUnderstand}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.SoapHeaderBypassMustUnderstand}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SoapHeaderBypassMustUnderstand readSoapHeaderBypassMustUnderstand(byte[] in) throws DeserializerException {
		return (SoapHeaderBypassMustUnderstand) this.xmlToObj(in, SoapHeaderBypassMustUnderstand.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.SoapHeaderBypassMustUnderstand}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.SoapHeaderBypassMustUnderstand}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.SoapHeaderBypassMustUnderstand}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SoapHeaderBypassMustUnderstand readSoapHeaderBypassMustUnderstandFromString(String in) throws DeserializerException {
		return (SoapHeaderBypassMustUnderstand) this.xmlToObj(in.getBytes(), SoapHeaderBypassMustUnderstand.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: openspcoop2
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.protocol.manifest.Openspcoop2}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.Openspcoop2}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.Openspcoop2}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Openspcoop2 readOpenspcoop2(String fileName) throws DeserializerException {
		return (Openspcoop2) this.xmlToObj(fileName, Openspcoop2.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.protocol.manifest.Openspcoop2}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.Openspcoop2}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.Openspcoop2}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Openspcoop2 readOpenspcoop2(File file) throws DeserializerException {
		return (Openspcoop2) this.xmlToObj(file, Openspcoop2.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.Openspcoop2}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.Openspcoop2}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.Openspcoop2}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Openspcoop2 readOpenspcoop2(InputStream in) throws DeserializerException {
		return (Openspcoop2) this.xmlToObj(in, Openspcoop2.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.Openspcoop2}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.Openspcoop2}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.Openspcoop2}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Openspcoop2 readOpenspcoop2(byte[] in) throws DeserializerException {
		return (Openspcoop2) this.xmlToObj(in, Openspcoop2.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.Openspcoop2}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.Openspcoop2}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.Openspcoop2}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Openspcoop2 readOpenspcoop2FromString(String in) throws DeserializerException {
		return (Openspcoop2) this.xmlToObj(in.getBytes(), Openspcoop2.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: web
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.protocol.manifest.Web}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.Web}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.Web}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Web readWeb(String fileName) throws DeserializerException {
		return (Web) this.xmlToObj(fileName, Web.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.protocol.manifest.Web}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.Web}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.Web}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Web readWeb(File file) throws DeserializerException {
		return (Web) this.xmlToObj(file, Web.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.Web}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.Web}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.Web}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Web readWeb(InputStream in) throws DeserializerException {
		return (Web) this.xmlToObj(in, Web.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.Web}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.Web}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.Web}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Web readWeb(byte[] in) throws DeserializerException {
		return (Web) this.xmlToObj(in, Web.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.Web}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.Web}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.Web}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Web readWebFromString(String in) throws DeserializerException {
		return (Web) this.xmlToObj(in.getBytes(), Web.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: registroServizi
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.protocol.manifest.RegistroServizi}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.RegistroServizi}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.RegistroServizi}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RegistroServizi readRegistroServizi(String fileName) throws DeserializerException {
		return (RegistroServizi) this.xmlToObj(fileName, RegistroServizi.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.protocol.manifest.RegistroServizi}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.RegistroServizi}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.RegistroServizi}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RegistroServizi readRegistroServizi(File file) throws DeserializerException {
		return (RegistroServizi) this.xmlToObj(file, RegistroServizi.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.RegistroServizi}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.RegistroServizi}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.RegistroServizi}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RegistroServizi readRegistroServizi(InputStream in) throws DeserializerException {
		return (RegistroServizi) this.xmlToObj(in, RegistroServizi.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.RegistroServizi}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.RegistroServizi}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.RegistroServizi}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RegistroServizi readRegistroServizi(byte[] in) throws DeserializerException {
		return (RegistroServizi) this.xmlToObj(in, RegistroServizi.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.RegistroServizi}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.RegistroServizi}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.RegistroServizi}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RegistroServizi readRegistroServiziFromString(String in) throws DeserializerException {
		return (RegistroServizi) this.xmlToObj(in.getBytes(), RegistroServizi.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: urlMapping
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.protocol.manifest.UrlMapping}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.UrlMapping}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.UrlMapping}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public UrlMapping readUrlMapping(String fileName) throws DeserializerException {
		return (UrlMapping) this.xmlToObj(fileName, UrlMapping.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.protocol.manifest.UrlMapping}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.UrlMapping}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.UrlMapping}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public UrlMapping readUrlMapping(File file) throws DeserializerException {
		return (UrlMapping) this.xmlToObj(file, UrlMapping.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.UrlMapping}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.UrlMapping}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.UrlMapping}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public UrlMapping readUrlMapping(InputStream in) throws DeserializerException {
		return (UrlMapping) this.xmlToObj(in, UrlMapping.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.UrlMapping}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.UrlMapping}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.UrlMapping}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public UrlMapping readUrlMapping(byte[] in) throws DeserializerException {
		return (UrlMapping) this.xmlToObj(in, UrlMapping.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.UrlMapping}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.UrlMapping}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.UrlMapping}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public UrlMapping readUrlMappingFromString(String in) throws DeserializerException {
		return (UrlMapping) this.xmlToObj(in.getBytes(), UrlMapping.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: binding
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.protocol.manifest.Binding}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.Binding}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.Binding}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Binding readBinding(String fileName) throws DeserializerException {
		return (Binding) this.xmlToObj(fileName, Binding.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.protocol.manifest.Binding}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.Binding}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.Binding}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Binding readBinding(File file) throws DeserializerException {
		return (Binding) this.xmlToObj(file, Binding.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.Binding}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.Binding}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.Binding}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Binding readBinding(InputStream in) throws DeserializerException {
		return (Binding) this.xmlToObj(in, Binding.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.Binding}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.Binding}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.Binding}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Binding readBinding(byte[] in) throws DeserializerException {
		return (Binding) this.xmlToObj(in, Binding.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.Binding}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.Binding}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.Binding}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Binding readBindingFromString(String in) throws DeserializerException {
		return (Binding) this.xmlToObj(in.getBytes(), Binding.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: versioni
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.protocol.manifest.Versioni}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.Versioni}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.Versioni}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Versioni readVersioni(String fileName) throws DeserializerException {
		return (Versioni) this.xmlToObj(fileName, Versioni.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.protocol.manifest.Versioni}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.Versioni}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.Versioni}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Versioni readVersioni(File file) throws DeserializerException {
		return (Versioni) this.xmlToObj(file, Versioni.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.Versioni}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.Versioni}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.Versioni}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Versioni readVersioni(InputStream in) throws DeserializerException {
		return (Versioni) this.xmlToObj(in, Versioni.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.Versioni}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.Versioni}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.Versioni}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Versioni readVersioni(byte[] in) throws DeserializerException {
		return (Versioni) this.xmlToObj(in, Versioni.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.Versioni}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.Versioni}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.Versioni}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Versioni readVersioniFromString(String in) throws DeserializerException {
		return (Versioni) this.xmlToObj(in.getBytes(), Versioni.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: soggetti
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.protocol.manifest.Soggetti}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.Soggetti}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.Soggetti}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Soggetti readSoggetti(String fileName) throws DeserializerException {
		return (Soggetti) this.xmlToObj(fileName, Soggetti.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.protocol.manifest.Soggetti}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.Soggetti}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.Soggetti}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Soggetti readSoggetti(File file) throws DeserializerException {
		return (Soggetti) this.xmlToObj(file, Soggetti.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.Soggetti}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.Soggetti}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.Soggetti}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Soggetti readSoggetti(InputStream in) throws DeserializerException {
		return (Soggetti) this.xmlToObj(in, Soggetti.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.Soggetti}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.Soggetti}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.Soggetti}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Soggetti readSoggetti(byte[] in) throws DeserializerException {
		return (Soggetti) this.xmlToObj(in, Soggetti.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.protocol.manifest.Soggetti}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.protocol.manifest.Soggetti}
	 * @return Object type {@link org.openspcoop2.protocol.manifest.Soggetti}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Soggetti readSoggettiFromString(String in) throws DeserializerException {
		return (Soggetti) this.xmlToObj(in.getBytes(), Soggetti.class);
	}	
	
	
	

}
