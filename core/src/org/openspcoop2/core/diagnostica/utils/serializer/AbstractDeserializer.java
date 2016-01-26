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
package org.openspcoop2.core.diagnostica.utils.serializer;

import org.openspcoop2.generic_project.exception.DeserializerException;

import org.openspcoop2.core.diagnostica.DominioTransazione;
import org.openspcoop2.core.diagnostica.DominioSoggetto;
import org.openspcoop2.core.diagnostica.IdInformazioniProtocolloTransazione;
import org.openspcoop2.core.diagnostica.DominioDiagnostico;
import org.openspcoop2.core.diagnostica.MessaggioDiagnostico;
import org.openspcoop2.core.diagnostica.Protocollo;
import org.openspcoop2.core.diagnostica.FiltroInformazioneProtocollo;
import org.openspcoop2.core.diagnostica.Servizio;
import org.openspcoop2.core.diagnostica.FiltroInformazioniDiagnostici;
import org.openspcoop2.core.diagnostica.SoggettoIdentificativo;
import org.openspcoop2.core.diagnostica.Soggetto;
import org.openspcoop2.core.diagnostica.Proprieta;
import org.openspcoop2.core.diagnostica.InformazioniProtocolloTransazione;

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
	 Object: dominio-transazione
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.diagnostica.DominioTransazione}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.diagnostica.DominioTransazione}
	 * @return Object type {@link org.openspcoop2.core.diagnostica.DominioTransazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DominioTransazione readDominioTransazione(String fileName) throws DeserializerException {
		return (DominioTransazione) this.xmlToObj(fileName, DominioTransazione.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.diagnostica.DominioTransazione}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.diagnostica.DominioTransazione}
	 * @return Object type {@link org.openspcoop2.core.diagnostica.DominioTransazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DominioTransazione readDominioTransazione(File file) throws DeserializerException {
		return (DominioTransazione) this.xmlToObj(file, DominioTransazione.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.diagnostica.DominioTransazione}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.diagnostica.DominioTransazione}
	 * @return Object type {@link org.openspcoop2.core.diagnostica.DominioTransazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DominioTransazione readDominioTransazione(InputStream in) throws DeserializerException {
		return (DominioTransazione) this.xmlToObj(in, DominioTransazione.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.diagnostica.DominioTransazione}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.diagnostica.DominioTransazione}
	 * @return Object type {@link org.openspcoop2.core.diagnostica.DominioTransazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DominioTransazione readDominioTransazione(byte[] in) throws DeserializerException {
		return (DominioTransazione) this.xmlToObj(in, DominioTransazione.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.diagnostica.DominioTransazione}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.diagnostica.DominioTransazione}
	 * @return Object type {@link org.openspcoop2.core.diagnostica.DominioTransazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DominioTransazione readDominioTransazioneFromString(String in) throws DeserializerException {
		return (DominioTransazione) this.xmlToObj(in.getBytes(), DominioTransazione.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: dominio-soggetto
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.diagnostica.DominioSoggetto}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.diagnostica.DominioSoggetto}
	 * @return Object type {@link org.openspcoop2.core.diagnostica.DominioSoggetto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DominioSoggetto readDominioSoggetto(String fileName) throws DeserializerException {
		return (DominioSoggetto) this.xmlToObj(fileName, DominioSoggetto.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.diagnostica.DominioSoggetto}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.diagnostica.DominioSoggetto}
	 * @return Object type {@link org.openspcoop2.core.diagnostica.DominioSoggetto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DominioSoggetto readDominioSoggetto(File file) throws DeserializerException {
		return (DominioSoggetto) this.xmlToObj(file, DominioSoggetto.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.diagnostica.DominioSoggetto}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.diagnostica.DominioSoggetto}
	 * @return Object type {@link org.openspcoop2.core.diagnostica.DominioSoggetto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DominioSoggetto readDominioSoggetto(InputStream in) throws DeserializerException {
		return (DominioSoggetto) this.xmlToObj(in, DominioSoggetto.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.diagnostica.DominioSoggetto}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.diagnostica.DominioSoggetto}
	 * @return Object type {@link org.openspcoop2.core.diagnostica.DominioSoggetto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DominioSoggetto readDominioSoggetto(byte[] in) throws DeserializerException {
		return (DominioSoggetto) this.xmlToObj(in, DominioSoggetto.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.diagnostica.DominioSoggetto}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.diagnostica.DominioSoggetto}
	 * @return Object type {@link org.openspcoop2.core.diagnostica.DominioSoggetto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DominioSoggetto readDominioSoggettoFromString(String in) throws DeserializerException {
		return (DominioSoggetto) this.xmlToObj(in.getBytes(), DominioSoggetto.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: id-informazioni-protocollo-transazione
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.diagnostica.IdInformazioniProtocolloTransazione}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.diagnostica.IdInformazioniProtocolloTransazione}
	 * @return Object type {@link org.openspcoop2.core.diagnostica.IdInformazioniProtocolloTransazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdInformazioniProtocolloTransazione readIdInformazioniProtocolloTransazione(String fileName) throws DeserializerException {
		return (IdInformazioniProtocolloTransazione) this.xmlToObj(fileName, IdInformazioniProtocolloTransazione.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.diagnostica.IdInformazioniProtocolloTransazione}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.diagnostica.IdInformazioniProtocolloTransazione}
	 * @return Object type {@link org.openspcoop2.core.diagnostica.IdInformazioniProtocolloTransazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdInformazioniProtocolloTransazione readIdInformazioniProtocolloTransazione(File file) throws DeserializerException {
		return (IdInformazioniProtocolloTransazione) this.xmlToObj(file, IdInformazioniProtocolloTransazione.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.diagnostica.IdInformazioniProtocolloTransazione}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.diagnostica.IdInformazioniProtocolloTransazione}
	 * @return Object type {@link org.openspcoop2.core.diagnostica.IdInformazioniProtocolloTransazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdInformazioniProtocolloTransazione readIdInformazioniProtocolloTransazione(InputStream in) throws DeserializerException {
		return (IdInformazioniProtocolloTransazione) this.xmlToObj(in, IdInformazioniProtocolloTransazione.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.diagnostica.IdInformazioniProtocolloTransazione}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.diagnostica.IdInformazioniProtocolloTransazione}
	 * @return Object type {@link org.openspcoop2.core.diagnostica.IdInformazioniProtocolloTransazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdInformazioniProtocolloTransazione readIdInformazioniProtocolloTransazione(byte[] in) throws DeserializerException {
		return (IdInformazioniProtocolloTransazione) this.xmlToObj(in, IdInformazioniProtocolloTransazione.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.diagnostica.IdInformazioniProtocolloTransazione}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.diagnostica.IdInformazioniProtocolloTransazione}
	 * @return Object type {@link org.openspcoop2.core.diagnostica.IdInformazioniProtocolloTransazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdInformazioniProtocolloTransazione readIdInformazioniProtocolloTransazioneFromString(String in) throws DeserializerException {
		return (IdInformazioniProtocolloTransazione) this.xmlToObj(in.getBytes(), IdInformazioniProtocolloTransazione.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: dominio-diagnostico
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.diagnostica.DominioDiagnostico}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.diagnostica.DominioDiagnostico}
	 * @return Object type {@link org.openspcoop2.core.diagnostica.DominioDiagnostico}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DominioDiagnostico readDominioDiagnostico(String fileName) throws DeserializerException {
		return (DominioDiagnostico) this.xmlToObj(fileName, DominioDiagnostico.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.diagnostica.DominioDiagnostico}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.diagnostica.DominioDiagnostico}
	 * @return Object type {@link org.openspcoop2.core.diagnostica.DominioDiagnostico}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DominioDiagnostico readDominioDiagnostico(File file) throws DeserializerException {
		return (DominioDiagnostico) this.xmlToObj(file, DominioDiagnostico.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.diagnostica.DominioDiagnostico}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.diagnostica.DominioDiagnostico}
	 * @return Object type {@link org.openspcoop2.core.diagnostica.DominioDiagnostico}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DominioDiagnostico readDominioDiagnostico(InputStream in) throws DeserializerException {
		return (DominioDiagnostico) this.xmlToObj(in, DominioDiagnostico.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.diagnostica.DominioDiagnostico}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.diagnostica.DominioDiagnostico}
	 * @return Object type {@link org.openspcoop2.core.diagnostica.DominioDiagnostico}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DominioDiagnostico readDominioDiagnostico(byte[] in) throws DeserializerException {
		return (DominioDiagnostico) this.xmlToObj(in, DominioDiagnostico.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.diagnostica.DominioDiagnostico}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.diagnostica.DominioDiagnostico}
	 * @return Object type {@link org.openspcoop2.core.diagnostica.DominioDiagnostico}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DominioDiagnostico readDominioDiagnosticoFromString(String in) throws DeserializerException {
		return (DominioDiagnostico) this.xmlToObj(in.getBytes(), DominioDiagnostico.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: messaggio-diagnostico
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.diagnostica.MessaggioDiagnostico}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.diagnostica.MessaggioDiagnostico}
	 * @return Object type {@link org.openspcoop2.core.diagnostica.MessaggioDiagnostico}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public MessaggioDiagnostico readMessaggioDiagnostico(String fileName) throws DeserializerException {
		return (MessaggioDiagnostico) this.xmlToObj(fileName, MessaggioDiagnostico.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.diagnostica.MessaggioDiagnostico}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.diagnostica.MessaggioDiagnostico}
	 * @return Object type {@link org.openspcoop2.core.diagnostica.MessaggioDiagnostico}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public MessaggioDiagnostico readMessaggioDiagnostico(File file) throws DeserializerException {
		return (MessaggioDiagnostico) this.xmlToObj(file, MessaggioDiagnostico.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.diagnostica.MessaggioDiagnostico}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.diagnostica.MessaggioDiagnostico}
	 * @return Object type {@link org.openspcoop2.core.diagnostica.MessaggioDiagnostico}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public MessaggioDiagnostico readMessaggioDiagnostico(InputStream in) throws DeserializerException {
		return (MessaggioDiagnostico) this.xmlToObj(in, MessaggioDiagnostico.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.diagnostica.MessaggioDiagnostico}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.diagnostica.MessaggioDiagnostico}
	 * @return Object type {@link org.openspcoop2.core.diagnostica.MessaggioDiagnostico}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public MessaggioDiagnostico readMessaggioDiagnostico(byte[] in) throws DeserializerException {
		return (MessaggioDiagnostico) this.xmlToObj(in, MessaggioDiagnostico.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.diagnostica.MessaggioDiagnostico}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.diagnostica.MessaggioDiagnostico}
	 * @return Object type {@link org.openspcoop2.core.diagnostica.MessaggioDiagnostico}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public MessaggioDiagnostico readMessaggioDiagnosticoFromString(String in) throws DeserializerException {
		return (MessaggioDiagnostico) this.xmlToObj(in.getBytes(), MessaggioDiagnostico.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: protocollo
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.diagnostica.Protocollo}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.diagnostica.Protocollo}
	 * @return Object type {@link org.openspcoop2.core.diagnostica.Protocollo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Protocollo readProtocollo(String fileName) throws DeserializerException {
		return (Protocollo) this.xmlToObj(fileName, Protocollo.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.diagnostica.Protocollo}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.diagnostica.Protocollo}
	 * @return Object type {@link org.openspcoop2.core.diagnostica.Protocollo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Protocollo readProtocollo(File file) throws DeserializerException {
		return (Protocollo) this.xmlToObj(file, Protocollo.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.diagnostica.Protocollo}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.diagnostica.Protocollo}
	 * @return Object type {@link org.openspcoop2.core.diagnostica.Protocollo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Protocollo readProtocollo(InputStream in) throws DeserializerException {
		return (Protocollo) this.xmlToObj(in, Protocollo.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.diagnostica.Protocollo}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.diagnostica.Protocollo}
	 * @return Object type {@link org.openspcoop2.core.diagnostica.Protocollo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Protocollo readProtocollo(byte[] in) throws DeserializerException {
		return (Protocollo) this.xmlToObj(in, Protocollo.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.diagnostica.Protocollo}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.diagnostica.Protocollo}
	 * @return Object type {@link org.openspcoop2.core.diagnostica.Protocollo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Protocollo readProtocolloFromString(String in) throws DeserializerException {
		return (Protocollo) this.xmlToObj(in.getBytes(), Protocollo.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: filtro-informazione-protocollo
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.diagnostica.FiltroInformazioneProtocollo}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.diagnostica.FiltroInformazioneProtocollo}
	 * @return Object type {@link org.openspcoop2.core.diagnostica.FiltroInformazioneProtocollo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public FiltroInformazioneProtocollo readFiltroInformazioneProtocollo(String fileName) throws DeserializerException {
		return (FiltroInformazioneProtocollo) this.xmlToObj(fileName, FiltroInformazioneProtocollo.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.diagnostica.FiltroInformazioneProtocollo}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.diagnostica.FiltroInformazioneProtocollo}
	 * @return Object type {@link org.openspcoop2.core.diagnostica.FiltroInformazioneProtocollo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public FiltroInformazioneProtocollo readFiltroInformazioneProtocollo(File file) throws DeserializerException {
		return (FiltroInformazioneProtocollo) this.xmlToObj(file, FiltroInformazioneProtocollo.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.diagnostica.FiltroInformazioneProtocollo}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.diagnostica.FiltroInformazioneProtocollo}
	 * @return Object type {@link org.openspcoop2.core.diagnostica.FiltroInformazioneProtocollo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public FiltroInformazioneProtocollo readFiltroInformazioneProtocollo(InputStream in) throws DeserializerException {
		return (FiltroInformazioneProtocollo) this.xmlToObj(in, FiltroInformazioneProtocollo.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.diagnostica.FiltroInformazioneProtocollo}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.diagnostica.FiltroInformazioneProtocollo}
	 * @return Object type {@link org.openspcoop2.core.diagnostica.FiltroInformazioneProtocollo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public FiltroInformazioneProtocollo readFiltroInformazioneProtocollo(byte[] in) throws DeserializerException {
		return (FiltroInformazioneProtocollo) this.xmlToObj(in, FiltroInformazioneProtocollo.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.diagnostica.FiltroInformazioneProtocollo}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.diagnostica.FiltroInformazioneProtocollo}
	 * @return Object type {@link org.openspcoop2.core.diagnostica.FiltroInformazioneProtocollo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public FiltroInformazioneProtocollo readFiltroInformazioneProtocolloFromString(String in) throws DeserializerException {
		return (FiltroInformazioneProtocollo) this.xmlToObj(in.getBytes(), FiltroInformazioneProtocollo.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: servizio
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.diagnostica.Servizio}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.diagnostica.Servizio}
	 * @return Object type {@link org.openspcoop2.core.diagnostica.Servizio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Servizio readServizio(String fileName) throws DeserializerException {
		return (Servizio) this.xmlToObj(fileName, Servizio.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.diagnostica.Servizio}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.diagnostica.Servizio}
	 * @return Object type {@link org.openspcoop2.core.diagnostica.Servizio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Servizio readServizio(File file) throws DeserializerException {
		return (Servizio) this.xmlToObj(file, Servizio.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.diagnostica.Servizio}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.diagnostica.Servizio}
	 * @return Object type {@link org.openspcoop2.core.diagnostica.Servizio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Servizio readServizio(InputStream in) throws DeserializerException {
		return (Servizio) this.xmlToObj(in, Servizio.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.diagnostica.Servizio}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.diagnostica.Servizio}
	 * @return Object type {@link org.openspcoop2.core.diagnostica.Servizio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Servizio readServizio(byte[] in) throws DeserializerException {
		return (Servizio) this.xmlToObj(in, Servizio.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.diagnostica.Servizio}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.diagnostica.Servizio}
	 * @return Object type {@link org.openspcoop2.core.diagnostica.Servizio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Servizio readServizioFromString(String in) throws DeserializerException {
		return (Servizio) this.xmlToObj(in.getBytes(), Servizio.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: filtro-informazioni-diagnostici
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.diagnostica.FiltroInformazioniDiagnostici}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.diagnostica.FiltroInformazioniDiagnostici}
	 * @return Object type {@link org.openspcoop2.core.diagnostica.FiltroInformazioniDiagnostici}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public FiltroInformazioniDiagnostici readFiltroInformazioniDiagnostici(String fileName) throws DeserializerException {
		return (FiltroInformazioniDiagnostici) this.xmlToObj(fileName, FiltroInformazioniDiagnostici.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.diagnostica.FiltroInformazioniDiagnostici}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.diagnostica.FiltroInformazioniDiagnostici}
	 * @return Object type {@link org.openspcoop2.core.diagnostica.FiltroInformazioniDiagnostici}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public FiltroInformazioniDiagnostici readFiltroInformazioniDiagnostici(File file) throws DeserializerException {
		return (FiltroInformazioniDiagnostici) this.xmlToObj(file, FiltroInformazioniDiagnostici.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.diagnostica.FiltroInformazioniDiagnostici}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.diagnostica.FiltroInformazioniDiagnostici}
	 * @return Object type {@link org.openspcoop2.core.diagnostica.FiltroInformazioniDiagnostici}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public FiltroInformazioniDiagnostici readFiltroInformazioniDiagnostici(InputStream in) throws DeserializerException {
		return (FiltroInformazioniDiagnostici) this.xmlToObj(in, FiltroInformazioniDiagnostici.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.diagnostica.FiltroInformazioniDiagnostici}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.diagnostica.FiltroInformazioniDiagnostici}
	 * @return Object type {@link org.openspcoop2.core.diagnostica.FiltroInformazioniDiagnostici}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public FiltroInformazioniDiagnostici readFiltroInformazioniDiagnostici(byte[] in) throws DeserializerException {
		return (FiltroInformazioniDiagnostici) this.xmlToObj(in, FiltroInformazioniDiagnostici.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.diagnostica.FiltroInformazioniDiagnostici}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.diagnostica.FiltroInformazioniDiagnostici}
	 * @return Object type {@link org.openspcoop2.core.diagnostica.FiltroInformazioniDiagnostici}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public FiltroInformazioniDiagnostici readFiltroInformazioniDiagnosticiFromString(String in) throws DeserializerException {
		return (FiltroInformazioniDiagnostici) this.xmlToObj(in.getBytes(), FiltroInformazioniDiagnostici.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: soggetto-identificativo
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.diagnostica.SoggettoIdentificativo}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.diagnostica.SoggettoIdentificativo}
	 * @return Object type {@link org.openspcoop2.core.diagnostica.SoggettoIdentificativo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SoggettoIdentificativo readSoggettoIdentificativo(String fileName) throws DeserializerException {
		return (SoggettoIdentificativo) this.xmlToObj(fileName, SoggettoIdentificativo.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.diagnostica.SoggettoIdentificativo}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.diagnostica.SoggettoIdentificativo}
	 * @return Object type {@link org.openspcoop2.core.diagnostica.SoggettoIdentificativo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SoggettoIdentificativo readSoggettoIdentificativo(File file) throws DeserializerException {
		return (SoggettoIdentificativo) this.xmlToObj(file, SoggettoIdentificativo.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.diagnostica.SoggettoIdentificativo}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.diagnostica.SoggettoIdentificativo}
	 * @return Object type {@link org.openspcoop2.core.diagnostica.SoggettoIdentificativo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SoggettoIdentificativo readSoggettoIdentificativo(InputStream in) throws DeserializerException {
		return (SoggettoIdentificativo) this.xmlToObj(in, SoggettoIdentificativo.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.diagnostica.SoggettoIdentificativo}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.diagnostica.SoggettoIdentificativo}
	 * @return Object type {@link org.openspcoop2.core.diagnostica.SoggettoIdentificativo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SoggettoIdentificativo readSoggettoIdentificativo(byte[] in) throws DeserializerException {
		return (SoggettoIdentificativo) this.xmlToObj(in, SoggettoIdentificativo.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.diagnostica.SoggettoIdentificativo}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.diagnostica.SoggettoIdentificativo}
	 * @return Object type {@link org.openspcoop2.core.diagnostica.SoggettoIdentificativo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SoggettoIdentificativo readSoggettoIdentificativoFromString(String in) throws DeserializerException {
		return (SoggettoIdentificativo) this.xmlToObj(in.getBytes(), SoggettoIdentificativo.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: soggetto
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.diagnostica.Soggetto}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.diagnostica.Soggetto}
	 * @return Object type {@link org.openspcoop2.core.diagnostica.Soggetto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Soggetto readSoggetto(String fileName) throws DeserializerException {
		return (Soggetto) this.xmlToObj(fileName, Soggetto.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.diagnostica.Soggetto}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.diagnostica.Soggetto}
	 * @return Object type {@link org.openspcoop2.core.diagnostica.Soggetto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Soggetto readSoggetto(File file) throws DeserializerException {
		return (Soggetto) this.xmlToObj(file, Soggetto.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.diagnostica.Soggetto}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.diagnostica.Soggetto}
	 * @return Object type {@link org.openspcoop2.core.diagnostica.Soggetto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Soggetto readSoggetto(InputStream in) throws DeserializerException {
		return (Soggetto) this.xmlToObj(in, Soggetto.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.diagnostica.Soggetto}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.diagnostica.Soggetto}
	 * @return Object type {@link org.openspcoop2.core.diagnostica.Soggetto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Soggetto readSoggetto(byte[] in) throws DeserializerException {
		return (Soggetto) this.xmlToObj(in, Soggetto.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.diagnostica.Soggetto}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.diagnostica.Soggetto}
	 * @return Object type {@link org.openspcoop2.core.diagnostica.Soggetto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Soggetto readSoggettoFromString(String in) throws DeserializerException {
		return (Soggetto) this.xmlToObj(in.getBytes(), Soggetto.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: proprieta
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.diagnostica.Proprieta}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.diagnostica.Proprieta}
	 * @return Object type {@link org.openspcoop2.core.diagnostica.Proprieta}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Proprieta readProprieta(String fileName) throws DeserializerException {
		return (Proprieta) this.xmlToObj(fileName, Proprieta.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.diagnostica.Proprieta}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.diagnostica.Proprieta}
	 * @return Object type {@link org.openspcoop2.core.diagnostica.Proprieta}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Proprieta readProprieta(File file) throws DeserializerException {
		return (Proprieta) this.xmlToObj(file, Proprieta.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.diagnostica.Proprieta}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.diagnostica.Proprieta}
	 * @return Object type {@link org.openspcoop2.core.diagnostica.Proprieta}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Proprieta readProprieta(InputStream in) throws DeserializerException {
		return (Proprieta) this.xmlToObj(in, Proprieta.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.diagnostica.Proprieta}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.diagnostica.Proprieta}
	 * @return Object type {@link org.openspcoop2.core.diagnostica.Proprieta}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Proprieta readProprieta(byte[] in) throws DeserializerException {
		return (Proprieta) this.xmlToObj(in, Proprieta.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.diagnostica.Proprieta}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.diagnostica.Proprieta}
	 * @return Object type {@link org.openspcoop2.core.diagnostica.Proprieta}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Proprieta readProprietaFromString(String in) throws DeserializerException {
		return (Proprieta) this.xmlToObj(in.getBytes(), Proprieta.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: informazioni-protocollo-transazione
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.diagnostica.InformazioniProtocolloTransazione}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.diagnostica.InformazioniProtocolloTransazione}
	 * @return Object type {@link org.openspcoop2.core.diagnostica.InformazioniProtocolloTransazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public InformazioniProtocolloTransazione readInformazioniProtocolloTransazione(String fileName) throws DeserializerException {
		return (InformazioniProtocolloTransazione) this.xmlToObj(fileName, InformazioniProtocolloTransazione.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.diagnostica.InformazioniProtocolloTransazione}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.diagnostica.InformazioniProtocolloTransazione}
	 * @return Object type {@link org.openspcoop2.core.diagnostica.InformazioniProtocolloTransazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public InformazioniProtocolloTransazione readInformazioniProtocolloTransazione(File file) throws DeserializerException {
		return (InformazioniProtocolloTransazione) this.xmlToObj(file, InformazioniProtocolloTransazione.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.diagnostica.InformazioniProtocolloTransazione}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.diagnostica.InformazioniProtocolloTransazione}
	 * @return Object type {@link org.openspcoop2.core.diagnostica.InformazioniProtocolloTransazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public InformazioniProtocolloTransazione readInformazioniProtocolloTransazione(InputStream in) throws DeserializerException {
		return (InformazioniProtocolloTransazione) this.xmlToObj(in, InformazioniProtocolloTransazione.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.diagnostica.InformazioniProtocolloTransazione}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.diagnostica.InformazioniProtocolloTransazione}
	 * @return Object type {@link org.openspcoop2.core.diagnostica.InformazioniProtocolloTransazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public InformazioniProtocolloTransazione readInformazioniProtocolloTransazione(byte[] in) throws DeserializerException {
		return (InformazioniProtocolloTransazione) this.xmlToObj(in, InformazioniProtocolloTransazione.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.diagnostica.InformazioniProtocolloTransazione}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.diagnostica.InformazioniProtocolloTransazione}
	 * @return Object type {@link org.openspcoop2.core.diagnostica.InformazioniProtocolloTransazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public InformazioniProtocolloTransazione readInformazioniProtocolloTransazioneFromString(String in) throws DeserializerException {
		return (InformazioniProtocolloTransazione) this.xmlToObj(in.getBytes(), InformazioniProtocolloTransazione.class);
	}	
	
	
	

}
