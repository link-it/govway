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
package org.openspcoop2.core.registry.utils.serializer;

import org.openspcoop2.generic_project.exception.DeserializerException;

import org.openspcoop2.core.registry.MessagePart;
import org.openspcoop2.core.registry.Message;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.core.registry.Connettore;
import org.openspcoop2.core.registry.IdSoggetto;
import org.openspcoop2.core.registry.AccordoCooperazionePartecipanti;
import org.openspcoop2.core.registry.AccordoServizioParteComuneServizioCompostoServizioComponente;
import org.openspcoop2.core.registry.IdPortaDominio;
import org.openspcoop2.core.registry.Servizio;
import org.openspcoop2.core.registry.ServizioAzione;
import org.openspcoop2.core.registry.Operation;
import org.openspcoop2.core.registry.PortType;
import org.openspcoop2.core.registry.AccordoCooperazione;
import org.openspcoop2.core.registry.RegistroServizi;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.PortaDominio;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Documento;
import org.openspcoop2.core.registry.Property;
import org.openspcoop2.core.registry.AccordoServizioParteComuneServizioComposto;
import org.openspcoop2.core.registry.Azione;
import org.openspcoop2.core.registry.ServizioAzioneFruitore;
import org.openspcoop2.core.registry.IdAccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.IdAccordoCooperazione;
import org.openspcoop2.core.registry.IdAccordoServizioParteComune;

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
	 Object: message-part
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.registry.MessagePart}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.MessagePart}
	 * @return Object type {@link org.openspcoop2.core.registry.MessagePart}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public MessagePart readMessagePart(String fileName) throws DeserializerException {
		return (MessagePart) this.xmlToObj(fileName, MessagePart.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.registry.MessagePart}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.MessagePart}
	 * @return Object type {@link org.openspcoop2.core.registry.MessagePart}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public MessagePart readMessagePart(File file) throws DeserializerException {
		return (MessagePart) this.xmlToObj(file, MessagePart.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.registry.MessagePart}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.MessagePart}
	 * @return Object type {@link org.openspcoop2.core.registry.MessagePart}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public MessagePart readMessagePart(InputStream in) throws DeserializerException {
		return (MessagePart) this.xmlToObj(in, MessagePart.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.registry.MessagePart}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.MessagePart}
	 * @return Object type {@link org.openspcoop2.core.registry.MessagePart}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public MessagePart readMessagePart(byte[] in) throws DeserializerException {
		return (MessagePart) this.xmlToObj(in, MessagePart.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.registry.MessagePart}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.MessagePart}
	 * @return Object type {@link org.openspcoop2.core.registry.MessagePart}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public MessagePart readMessagePartFromString(String in) throws DeserializerException {
		return (MessagePart) this.xmlToObj(in.getBytes(), MessagePart.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: message
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.registry.Message}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.Message}
	 * @return Object type {@link org.openspcoop2.core.registry.Message}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Message readMessage(String fileName) throws DeserializerException {
		return (Message) this.xmlToObj(fileName, Message.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.registry.Message}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.Message}
	 * @return Object type {@link org.openspcoop2.core.registry.Message}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Message readMessage(File file) throws DeserializerException {
		return (Message) this.xmlToObj(file, Message.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.registry.Message}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.Message}
	 * @return Object type {@link org.openspcoop2.core.registry.Message}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Message readMessage(InputStream in) throws DeserializerException {
		return (Message) this.xmlToObj(in, Message.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.registry.Message}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.Message}
	 * @return Object type {@link org.openspcoop2.core.registry.Message}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Message readMessage(byte[] in) throws DeserializerException {
		return (Message) this.xmlToObj(in, Message.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.registry.Message}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.Message}
	 * @return Object type {@link org.openspcoop2.core.registry.Message}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Message readMessageFromString(String in) throws DeserializerException {
		return (Message) this.xmlToObj(in.getBytes(), Message.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: fruitore
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.registry.Fruitore}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.Fruitore}
	 * @return Object type {@link org.openspcoop2.core.registry.Fruitore}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Fruitore readFruitore(String fileName) throws DeserializerException {
		return (Fruitore) this.xmlToObj(fileName, Fruitore.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.registry.Fruitore}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.Fruitore}
	 * @return Object type {@link org.openspcoop2.core.registry.Fruitore}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Fruitore readFruitore(File file) throws DeserializerException {
		return (Fruitore) this.xmlToObj(file, Fruitore.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.registry.Fruitore}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.Fruitore}
	 * @return Object type {@link org.openspcoop2.core.registry.Fruitore}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Fruitore readFruitore(InputStream in) throws DeserializerException {
		return (Fruitore) this.xmlToObj(in, Fruitore.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.registry.Fruitore}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.Fruitore}
	 * @return Object type {@link org.openspcoop2.core.registry.Fruitore}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Fruitore readFruitore(byte[] in) throws DeserializerException {
		return (Fruitore) this.xmlToObj(in, Fruitore.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.registry.Fruitore}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.Fruitore}
	 * @return Object type {@link org.openspcoop2.core.registry.Fruitore}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Fruitore readFruitoreFromString(String in) throws DeserializerException {
		return (Fruitore) this.xmlToObj(in.getBytes(), Fruitore.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: connettore
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.registry.Connettore}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.Connettore}
	 * @return Object type {@link org.openspcoop2.core.registry.Connettore}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Connettore readConnettore(String fileName) throws DeserializerException {
		return (Connettore) this.xmlToObj(fileName, Connettore.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.registry.Connettore}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.Connettore}
	 * @return Object type {@link org.openspcoop2.core.registry.Connettore}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Connettore readConnettore(File file) throws DeserializerException {
		return (Connettore) this.xmlToObj(file, Connettore.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.registry.Connettore}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.Connettore}
	 * @return Object type {@link org.openspcoop2.core.registry.Connettore}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Connettore readConnettore(InputStream in) throws DeserializerException {
		return (Connettore) this.xmlToObj(in, Connettore.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.registry.Connettore}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.Connettore}
	 * @return Object type {@link org.openspcoop2.core.registry.Connettore}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Connettore readConnettore(byte[] in) throws DeserializerException {
		return (Connettore) this.xmlToObj(in, Connettore.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.registry.Connettore}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.Connettore}
	 * @return Object type {@link org.openspcoop2.core.registry.Connettore}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Connettore readConnettoreFromString(String in) throws DeserializerException {
		return (Connettore) this.xmlToObj(in.getBytes(), Connettore.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: id-soggetto
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.registry.IdSoggetto}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.IdSoggetto}
	 * @return Object type {@link org.openspcoop2.core.registry.IdSoggetto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdSoggetto readIdSoggetto(String fileName) throws DeserializerException {
		return (IdSoggetto) this.xmlToObj(fileName, IdSoggetto.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.registry.IdSoggetto}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.IdSoggetto}
	 * @return Object type {@link org.openspcoop2.core.registry.IdSoggetto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdSoggetto readIdSoggetto(File file) throws DeserializerException {
		return (IdSoggetto) this.xmlToObj(file, IdSoggetto.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.registry.IdSoggetto}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.IdSoggetto}
	 * @return Object type {@link org.openspcoop2.core.registry.IdSoggetto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdSoggetto readIdSoggetto(InputStream in) throws DeserializerException {
		return (IdSoggetto) this.xmlToObj(in, IdSoggetto.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.registry.IdSoggetto}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.IdSoggetto}
	 * @return Object type {@link org.openspcoop2.core.registry.IdSoggetto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdSoggetto readIdSoggetto(byte[] in) throws DeserializerException {
		return (IdSoggetto) this.xmlToObj(in, IdSoggetto.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.registry.IdSoggetto}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.IdSoggetto}
	 * @return Object type {@link org.openspcoop2.core.registry.IdSoggetto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdSoggetto readIdSoggettoFromString(String in) throws DeserializerException {
		return (IdSoggetto) this.xmlToObj(in.getBytes(), IdSoggetto.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: accordo-cooperazione-partecipanti
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.registry.AccordoCooperazionePartecipanti}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.AccordoCooperazionePartecipanti}
	 * @return Object type {@link org.openspcoop2.core.registry.AccordoCooperazionePartecipanti}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccordoCooperazionePartecipanti readAccordoCooperazionePartecipanti(String fileName) throws DeserializerException {
		return (AccordoCooperazionePartecipanti) this.xmlToObj(fileName, AccordoCooperazionePartecipanti.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.registry.AccordoCooperazionePartecipanti}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.AccordoCooperazionePartecipanti}
	 * @return Object type {@link org.openspcoop2.core.registry.AccordoCooperazionePartecipanti}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccordoCooperazionePartecipanti readAccordoCooperazionePartecipanti(File file) throws DeserializerException {
		return (AccordoCooperazionePartecipanti) this.xmlToObj(file, AccordoCooperazionePartecipanti.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.registry.AccordoCooperazionePartecipanti}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.AccordoCooperazionePartecipanti}
	 * @return Object type {@link org.openspcoop2.core.registry.AccordoCooperazionePartecipanti}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccordoCooperazionePartecipanti readAccordoCooperazionePartecipanti(InputStream in) throws DeserializerException {
		return (AccordoCooperazionePartecipanti) this.xmlToObj(in, AccordoCooperazionePartecipanti.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.registry.AccordoCooperazionePartecipanti}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.AccordoCooperazionePartecipanti}
	 * @return Object type {@link org.openspcoop2.core.registry.AccordoCooperazionePartecipanti}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccordoCooperazionePartecipanti readAccordoCooperazionePartecipanti(byte[] in) throws DeserializerException {
		return (AccordoCooperazionePartecipanti) this.xmlToObj(in, AccordoCooperazionePartecipanti.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.registry.AccordoCooperazionePartecipanti}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.AccordoCooperazionePartecipanti}
	 * @return Object type {@link org.openspcoop2.core.registry.AccordoCooperazionePartecipanti}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccordoCooperazionePartecipanti readAccordoCooperazionePartecipantiFromString(String in) throws DeserializerException {
		return (AccordoCooperazionePartecipanti) this.xmlToObj(in.getBytes(), AccordoCooperazionePartecipanti.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: accordo-servizio-parte-comune-servizio-composto-servizio-componente
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.registry.AccordoServizioParteComuneServizioCompostoServizioComponente}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.AccordoServizioParteComuneServizioCompostoServizioComponente}
	 * @return Object type {@link org.openspcoop2.core.registry.AccordoServizioParteComuneServizioCompostoServizioComponente}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccordoServizioParteComuneServizioCompostoServizioComponente readAccordoServizioParteComuneServizioCompostoServizioComponente(String fileName) throws DeserializerException {
		return (AccordoServizioParteComuneServizioCompostoServizioComponente) this.xmlToObj(fileName, AccordoServizioParteComuneServizioCompostoServizioComponente.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.registry.AccordoServizioParteComuneServizioCompostoServizioComponente}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.AccordoServizioParteComuneServizioCompostoServizioComponente}
	 * @return Object type {@link org.openspcoop2.core.registry.AccordoServizioParteComuneServizioCompostoServizioComponente}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccordoServizioParteComuneServizioCompostoServizioComponente readAccordoServizioParteComuneServizioCompostoServizioComponente(File file) throws DeserializerException {
		return (AccordoServizioParteComuneServizioCompostoServizioComponente) this.xmlToObj(file, AccordoServizioParteComuneServizioCompostoServizioComponente.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.registry.AccordoServizioParteComuneServizioCompostoServizioComponente}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.AccordoServizioParteComuneServizioCompostoServizioComponente}
	 * @return Object type {@link org.openspcoop2.core.registry.AccordoServizioParteComuneServizioCompostoServizioComponente}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccordoServizioParteComuneServizioCompostoServizioComponente readAccordoServizioParteComuneServizioCompostoServizioComponente(InputStream in) throws DeserializerException {
		return (AccordoServizioParteComuneServizioCompostoServizioComponente) this.xmlToObj(in, AccordoServizioParteComuneServizioCompostoServizioComponente.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.registry.AccordoServizioParteComuneServizioCompostoServizioComponente}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.AccordoServizioParteComuneServizioCompostoServizioComponente}
	 * @return Object type {@link org.openspcoop2.core.registry.AccordoServizioParteComuneServizioCompostoServizioComponente}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccordoServizioParteComuneServizioCompostoServizioComponente readAccordoServizioParteComuneServizioCompostoServizioComponente(byte[] in) throws DeserializerException {
		return (AccordoServizioParteComuneServizioCompostoServizioComponente) this.xmlToObj(in, AccordoServizioParteComuneServizioCompostoServizioComponente.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.registry.AccordoServizioParteComuneServizioCompostoServizioComponente}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.AccordoServizioParteComuneServizioCompostoServizioComponente}
	 * @return Object type {@link org.openspcoop2.core.registry.AccordoServizioParteComuneServizioCompostoServizioComponente}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccordoServizioParteComuneServizioCompostoServizioComponente readAccordoServizioParteComuneServizioCompostoServizioComponenteFromString(String in) throws DeserializerException {
		return (AccordoServizioParteComuneServizioCompostoServizioComponente) this.xmlToObj(in.getBytes(), AccordoServizioParteComuneServizioCompostoServizioComponente.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: id-porta-dominio
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.registry.IdPortaDominio}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.IdPortaDominio}
	 * @return Object type {@link org.openspcoop2.core.registry.IdPortaDominio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdPortaDominio readIdPortaDominio(String fileName) throws DeserializerException {
		return (IdPortaDominio) this.xmlToObj(fileName, IdPortaDominio.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.registry.IdPortaDominio}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.IdPortaDominio}
	 * @return Object type {@link org.openspcoop2.core.registry.IdPortaDominio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdPortaDominio readIdPortaDominio(File file) throws DeserializerException {
		return (IdPortaDominio) this.xmlToObj(file, IdPortaDominio.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.registry.IdPortaDominio}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.IdPortaDominio}
	 * @return Object type {@link org.openspcoop2.core.registry.IdPortaDominio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdPortaDominio readIdPortaDominio(InputStream in) throws DeserializerException {
		return (IdPortaDominio) this.xmlToObj(in, IdPortaDominio.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.registry.IdPortaDominio}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.IdPortaDominio}
	 * @return Object type {@link org.openspcoop2.core.registry.IdPortaDominio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdPortaDominio readIdPortaDominio(byte[] in) throws DeserializerException {
		return (IdPortaDominio) this.xmlToObj(in, IdPortaDominio.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.registry.IdPortaDominio}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.IdPortaDominio}
	 * @return Object type {@link org.openspcoop2.core.registry.IdPortaDominio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdPortaDominio readIdPortaDominioFromString(String in) throws DeserializerException {
		return (IdPortaDominio) this.xmlToObj(in.getBytes(), IdPortaDominio.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: servizio
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.registry.Servizio}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.Servizio}
	 * @return Object type {@link org.openspcoop2.core.registry.Servizio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Servizio readServizio(String fileName) throws DeserializerException {
		return (Servizio) this.xmlToObj(fileName, Servizio.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.registry.Servizio}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.Servizio}
	 * @return Object type {@link org.openspcoop2.core.registry.Servizio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Servizio readServizio(File file) throws DeserializerException {
		return (Servizio) this.xmlToObj(file, Servizio.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.registry.Servizio}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.Servizio}
	 * @return Object type {@link org.openspcoop2.core.registry.Servizio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Servizio readServizio(InputStream in) throws DeserializerException {
		return (Servizio) this.xmlToObj(in, Servizio.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.registry.Servizio}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.Servizio}
	 * @return Object type {@link org.openspcoop2.core.registry.Servizio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Servizio readServizio(byte[] in) throws DeserializerException {
		return (Servizio) this.xmlToObj(in, Servizio.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.registry.Servizio}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.Servizio}
	 * @return Object type {@link org.openspcoop2.core.registry.Servizio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Servizio readServizioFromString(String in) throws DeserializerException {
		return (Servizio) this.xmlToObj(in.getBytes(), Servizio.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: servizio-azione
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.registry.ServizioAzione}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.ServizioAzione}
	 * @return Object type {@link org.openspcoop2.core.registry.ServizioAzione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ServizioAzione readServizioAzione(String fileName) throws DeserializerException {
		return (ServizioAzione) this.xmlToObj(fileName, ServizioAzione.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.registry.ServizioAzione}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.ServizioAzione}
	 * @return Object type {@link org.openspcoop2.core.registry.ServizioAzione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ServizioAzione readServizioAzione(File file) throws DeserializerException {
		return (ServizioAzione) this.xmlToObj(file, ServizioAzione.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.registry.ServizioAzione}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.ServizioAzione}
	 * @return Object type {@link org.openspcoop2.core.registry.ServizioAzione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ServizioAzione readServizioAzione(InputStream in) throws DeserializerException {
		return (ServizioAzione) this.xmlToObj(in, ServizioAzione.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.registry.ServizioAzione}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.ServizioAzione}
	 * @return Object type {@link org.openspcoop2.core.registry.ServizioAzione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ServizioAzione readServizioAzione(byte[] in) throws DeserializerException {
		return (ServizioAzione) this.xmlToObj(in, ServizioAzione.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.registry.ServizioAzione}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.ServizioAzione}
	 * @return Object type {@link org.openspcoop2.core.registry.ServizioAzione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ServizioAzione readServizioAzioneFromString(String in) throws DeserializerException {
		return (ServizioAzione) this.xmlToObj(in.getBytes(), ServizioAzione.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: operation
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.registry.Operation}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.Operation}
	 * @return Object type {@link org.openspcoop2.core.registry.Operation}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Operation readOperation(String fileName) throws DeserializerException {
		return (Operation) this.xmlToObj(fileName, Operation.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.registry.Operation}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.Operation}
	 * @return Object type {@link org.openspcoop2.core.registry.Operation}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Operation readOperation(File file) throws DeserializerException {
		return (Operation) this.xmlToObj(file, Operation.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.registry.Operation}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.Operation}
	 * @return Object type {@link org.openspcoop2.core.registry.Operation}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Operation readOperation(InputStream in) throws DeserializerException {
		return (Operation) this.xmlToObj(in, Operation.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.registry.Operation}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.Operation}
	 * @return Object type {@link org.openspcoop2.core.registry.Operation}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Operation readOperation(byte[] in) throws DeserializerException {
		return (Operation) this.xmlToObj(in, Operation.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.registry.Operation}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.Operation}
	 * @return Object type {@link org.openspcoop2.core.registry.Operation}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Operation readOperationFromString(String in) throws DeserializerException {
		return (Operation) this.xmlToObj(in.getBytes(), Operation.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: port-type
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.registry.PortType}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.PortType}
	 * @return Object type {@link org.openspcoop2.core.registry.PortType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PortType readPortType(String fileName) throws DeserializerException {
		return (PortType) this.xmlToObj(fileName, PortType.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.registry.PortType}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.PortType}
	 * @return Object type {@link org.openspcoop2.core.registry.PortType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PortType readPortType(File file) throws DeserializerException {
		return (PortType) this.xmlToObj(file, PortType.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.registry.PortType}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.PortType}
	 * @return Object type {@link org.openspcoop2.core.registry.PortType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PortType readPortType(InputStream in) throws DeserializerException {
		return (PortType) this.xmlToObj(in, PortType.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.registry.PortType}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.PortType}
	 * @return Object type {@link org.openspcoop2.core.registry.PortType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PortType readPortType(byte[] in) throws DeserializerException {
		return (PortType) this.xmlToObj(in, PortType.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.registry.PortType}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.PortType}
	 * @return Object type {@link org.openspcoop2.core.registry.PortType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PortType readPortTypeFromString(String in) throws DeserializerException {
		return (PortType) this.xmlToObj(in.getBytes(), PortType.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: accordo-cooperazione
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.registry.AccordoCooperazione}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.AccordoCooperazione}
	 * @return Object type {@link org.openspcoop2.core.registry.AccordoCooperazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccordoCooperazione readAccordoCooperazione(String fileName) throws DeserializerException {
		return (AccordoCooperazione) this.xmlToObj(fileName, AccordoCooperazione.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.registry.AccordoCooperazione}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.AccordoCooperazione}
	 * @return Object type {@link org.openspcoop2.core.registry.AccordoCooperazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccordoCooperazione readAccordoCooperazione(File file) throws DeserializerException {
		return (AccordoCooperazione) this.xmlToObj(file, AccordoCooperazione.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.registry.AccordoCooperazione}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.AccordoCooperazione}
	 * @return Object type {@link org.openspcoop2.core.registry.AccordoCooperazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccordoCooperazione readAccordoCooperazione(InputStream in) throws DeserializerException {
		return (AccordoCooperazione) this.xmlToObj(in, AccordoCooperazione.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.registry.AccordoCooperazione}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.AccordoCooperazione}
	 * @return Object type {@link org.openspcoop2.core.registry.AccordoCooperazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccordoCooperazione readAccordoCooperazione(byte[] in) throws DeserializerException {
		return (AccordoCooperazione) this.xmlToObj(in, AccordoCooperazione.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.registry.AccordoCooperazione}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.AccordoCooperazione}
	 * @return Object type {@link org.openspcoop2.core.registry.AccordoCooperazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccordoCooperazione readAccordoCooperazioneFromString(String in) throws DeserializerException {
		return (AccordoCooperazione) this.xmlToObj(in.getBytes(), AccordoCooperazione.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: registro-servizi
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.registry.RegistroServizi}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.RegistroServizi}
	 * @return Object type {@link org.openspcoop2.core.registry.RegistroServizi}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RegistroServizi readRegistroServizi(String fileName) throws DeserializerException {
		return (RegistroServizi) this.xmlToObj(fileName, RegistroServizi.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.registry.RegistroServizi}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.RegistroServizi}
	 * @return Object type {@link org.openspcoop2.core.registry.RegistroServizi}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RegistroServizi readRegistroServizi(File file) throws DeserializerException {
		return (RegistroServizi) this.xmlToObj(file, RegistroServizi.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.registry.RegistroServizi}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.RegistroServizi}
	 * @return Object type {@link org.openspcoop2.core.registry.RegistroServizi}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RegistroServizi readRegistroServizi(InputStream in) throws DeserializerException {
		return (RegistroServizi) this.xmlToObj(in, RegistroServizi.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.registry.RegistroServizi}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.RegistroServizi}
	 * @return Object type {@link org.openspcoop2.core.registry.RegistroServizi}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RegistroServizi readRegistroServizi(byte[] in) throws DeserializerException {
		return (RegistroServizi) this.xmlToObj(in, RegistroServizi.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.registry.RegistroServizi}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.RegistroServizi}
	 * @return Object type {@link org.openspcoop2.core.registry.RegistroServizi}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RegistroServizi readRegistroServiziFromString(String in) throws DeserializerException {
		return (RegistroServizi) this.xmlToObj(in.getBytes(), RegistroServizi.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: accordo-servizio-parte-comune
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.registry.AccordoServizioParteComune}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.AccordoServizioParteComune}
	 * @return Object type {@link org.openspcoop2.core.registry.AccordoServizioParteComune}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccordoServizioParteComune readAccordoServizioParteComune(String fileName) throws DeserializerException {
		return (AccordoServizioParteComune) this.xmlToObj(fileName, AccordoServizioParteComune.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.registry.AccordoServizioParteComune}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.AccordoServizioParteComune}
	 * @return Object type {@link org.openspcoop2.core.registry.AccordoServizioParteComune}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccordoServizioParteComune readAccordoServizioParteComune(File file) throws DeserializerException {
		return (AccordoServizioParteComune) this.xmlToObj(file, AccordoServizioParteComune.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.registry.AccordoServizioParteComune}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.AccordoServizioParteComune}
	 * @return Object type {@link org.openspcoop2.core.registry.AccordoServizioParteComune}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccordoServizioParteComune readAccordoServizioParteComune(InputStream in) throws DeserializerException {
		return (AccordoServizioParteComune) this.xmlToObj(in, AccordoServizioParteComune.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.registry.AccordoServizioParteComune}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.AccordoServizioParteComune}
	 * @return Object type {@link org.openspcoop2.core.registry.AccordoServizioParteComune}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccordoServizioParteComune readAccordoServizioParteComune(byte[] in) throws DeserializerException {
		return (AccordoServizioParteComune) this.xmlToObj(in, AccordoServizioParteComune.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.registry.AccordoServizioParteComune}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.AccordoServizioParteComune}
	 * @return Object type {@link org.openspcoop2.core.registry.AccordoServizioParteComune}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccordoServizioParteComune readAccordoServizioParteComuneFromString(String in) throws DeserializerException {
		return (AccordoServizioParteComune) this.xmlToObj(in.getBytes(), AccordoServizioParteComune.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: porta-dominio
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.registry.PortaDominio}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.PortaDominio}
	 * @return Object type {@link org.openspcoop2.core.registry.PortaDominio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PortaDominio readPortaDominio(String fileName) throws DeserializerException {
		return (PortaDominio) this.xmlToObj(fileName, PortaDominio.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.registry.PortaDominio}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.PortaDominio}
	 * @return Object type {@link org.openspcoop2.core.registry.PortaDominio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PortaDominio readPortaDominio(File file) throws DeserializerException {
		return (PortaDominio) this.xmlToObj(file, PortaDominio.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.registry.PortaDominio}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.PortaDominio}
	 * @return Object type {@link org.openspcoop2.core.registry.PortaDominio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PortaDominio readPortaDominio(InputStream in) throws DeserializerException {
		return (PortaDominio) this.xmlToObj(in, PortaDominio.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.registry.PortaDominio}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.PortaDominio}
	 * @return Object type {@link org.openspcoop2.core.registry.PortaDominio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PortaDominio readPortaDominio(byte[] in) throws DeserializerException {
		return (PortaDominio) this.xmlToObj(in, PortaDominio.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.registry.PortaDominio}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.PortaDominio}
	 * @return Object type {@link org.openspcoop2.core.registry.PortaDominio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PortaDominio readPortaDominioFromString(String in) throws DeserializerException {
		return (PortaDominio) this.xmlToObj(in.getBytes(), PortaDominio.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: soggetto
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.registry.Soggetto}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.Soggetto}
	 * @return Object type {@link org.openspcoop2.core.registry.Soggetto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Soggetto readSoggetto(String fileName) throws DeserializerException {
		return (Soggetto) this.xmlToObj(fileName, Soggetto.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.registry.Soggetto}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.Soggetto}
	 * @return Object type {@link org.openspcoop2.core.registry.Soggetto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Soggetto readSoggetto(File file) throws DeserializerException {
		return (Soggetto) this.xmlToObj(file, Soggetto.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.registry.Soggetto}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.Soggetto}
	 * @return Object type {@link org.openspcoop2.core.registry.Soggetto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Soggetto readSoggetto(InputStream in) throws DeserializerException {
		return (Soggetto) this.xmlToObj(in, Soggetto.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.registry.Soggetto}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.Soggetto}
	 * @return Object type {@link org.openspcoop2.core.registry.Soggetto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Soggetto readSoggetto(byte[] in) throws DeserializerException {
		return (Soggetto) this.xmlToObj(in, Soggetto.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.registry.Soggetto}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.Soggetto}
	 * @return Object type {@link org.openspcoop2.core.registry.Soggetto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Soggetto readSoggettoFromString(String in) throws DeserializerException {
		return (Soggetto) this.xmlToObj(in.getBytes(), Soggetto.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: accordo-servizio-parte-specifica
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.registry.AccordoServizioParteSpecifica}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.AccordoServizioParteSpecifica}
	 * @return Object type {@link org.openspcoop2.core.registry.AccordoServizioParteSpecifica}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccordoServizioParteSpecifica readAccordoServizioParteSpecifica(String fileName) throws DeserializerException {
		return (AccordoServizioParteSpecifica) this.xmlToObj(fileName, AccordoServizioParteSpecifica.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.registry.AccordoServizioParteSpecifica}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.AccordoServizioParteSpecifica}
	 * @return Object type {@link org.openspcoop2.core.registry.AccordoServizioParteSpecifica}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccordoServizioParteSpecifica readAccordoServizioParteSpecifica(File file) throws DeserializerException {
		return (AccordoServizioParteSpecifica) this.xmlToObj(file, AccordoServizioParteSpecifica.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.registry.AccordoServizioParteSpecifica}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.AccordoServizioParteSpecifica}
	 * @return Object type {@link org.openspcoop2.core.registry.AccordoServizioParteSpecifica}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccordoServizioParteSpecifica readAccordoServizioParteSpecifica(InputStream in) throws DeserializerException {
		return (AccordoServizioParteSpecifica) this.xmlToObj(in, AccordoServizioParteSpecifica.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.registry.AccordoServizioParteSpecifica}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.AccordoServizioParteSpecifica}
	 * @return Object type {@link org.openspcoop2.core.registry.AccordoServizioParteSpecifica}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccordoServizioParteSpecifica readAccordoServizioParteSpecifica(byte[] in) throws DeserializerException {
		return (AccordoServizioParteSpecifica) this.xmlToObj(in, AccordoServizioParteSpecifica.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.registry.AccordoServizioParteSpecifica}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.AccordoServizioParteSpecifica}
	 * @return Object type {@link org.openspcoop2.core.registry.AccordoServizioParteSpecifica}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccordoServizioParteSpecifica readAccordoServizioParteSpecificaFromString(String in) throws DeserializerException {
		return (AccordoServizioParteSpecifica) this.xmlToObj(in.getBytes(), AccordoServizioParteSpecifica.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: documento
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.registry.Documento}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.Documento}
	 * @return Object type {@link org.openspcoop2.core.registry.Documento}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Documento readDocumento(String fileName) throws DeserializerException {
		return (Documento) this.xmlToObj(fileName, Documento.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.registry.Documento}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.Documento}
	 * @return Object type {@link org.openspcoop2.core.registry.Documento}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Documento readDocumento(File file) throws DeserializerException {
		return (Documento) this.xmlToObj(file, Documento.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.registry.Documento}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.Documento}
	 * @return Object type {@link org.openspcoop2.core.registry.Documento}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Documento readDocumento(InputStream in) throws DeserializerException {
		return (Documento) this.xmlToObj(in, Documento.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.registry.Documento}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.Documento}
	 * @return Object type {@link org.openspcoop2.core.registry.Documento}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Documento readDocumento(byte[] in) throws DeserializerException {
		return (Documento) this.xmlToObj(in, Documento.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.registry.Documento}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.Documento}
	 * @return Object type {@link org.openspcoop2.core.registry.Documento}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Documento readDocumentoFromString(String in) throws DeserializerException {
		return (Documento) this.xmlToObj(in.getBytes(), Documento.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: Property
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.registry.Property}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.Property}
	 * @return Object type {@link org.openspcoop2.core.registry.Property}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Property readProperty(String fileName) throws DeserializerException {
		return (Property) this.xmlToObj(fileName, Property.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.registry.Property}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.Property}
	 * @return Object type {@link org.openspcoop2.core.registry.Property}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Property readProperty(File file) throws DeserializerException {
		return (Property) this.xmlToObj(file, Property.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.registry.Property}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.Property}
	 * @return Object type {@link org.openspcoop2.core.registry.Property}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Property readProperty(InputStream in) throws DeserializerException {
		return (Property) this.xmlToObj(in, Property.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.registry.Property}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.Property}
	 * @return Object type {@link org.openspcoop2.core.registry.Property}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Property readProperty(byte[] in) throws DeserializerException {
		return (Property) this.xmlToObj(in, Property.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.registry.Property}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.Property}
	 * @return Object type {@link org.openspcoop2.core.registry.Property}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Property readPropertyFromString(String in) throws DeserializerException {
		return (Property) this.xmlToObj(in.getBytes(), Property.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: accordo-servizio-parte-comune-servizio-composto
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.registry.AccordoServizioParteComuneServizioComposto}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.AccordoServizioParteComuneServizioComposto}
	 * @return Object type {@link org.openspcoop2.core.registry.AccordoServizioParteComuneServizioComposto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccordoServizioParteComuneServizioComposto readAccordoServizioParteComuneServizioComposto(String fileName) throws DeserializerException {
		return (AccordoServizioParteComuneServizioComposto) this.xmlToObj(fileName, AccordoServizioParteComuneServizioComposto.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.registry.AccordoServizioParteComuneServizioComposto}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.AccordoServizioParteComuneServizioComposto}
	 * @return Object type {@link org.openspcoop2.core.registry.AccordoServizioParteComuneServizioComposto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccordoServizioParteComuneServizioComposto readAccordoServizioParteComuneServizioComposto(File file) throws DeserializerException {
		return (AccordoServizioParteComuneServizioComposto) this.xmlToObj(file, AccordoServizioParteComuneServizioComposto.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.registry.AccordoServizioParteComuneServizioComposto}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.AccordoServizioParteComuneServizioComposto}
	 * @return Object type {@link org.openspcoop2.core.registry.AccordoServizioParteComuneServizioComposto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccordoServizioParteComuneServizioComposto readAccordoServizioParteComuneServizioComposto(InputStream in) throws DeserializerException {
		return (AccordoServizioParteComuneServizioComposto) this.xmlToObj(in, AccordoServizioParteComuneServizioComposto.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.registry.AccordoServizioParteComuneServizioComposto}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.AccordoServizioParteComuneServizioComposto}
	 * @return Object type {@link org.openspcoop2.core.registry.AccordoServizioParteComuneServizioComposto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccordoServizioParteComuneServizioComposto readAccordoServizioParteComuneServizioComposto(byte[] in) throws DeserializerException {
		return (AccordoServizioParteComuneServizioComposto) this.xmlToObj(in, AccordoServizioParteComuneServizioComposto.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.registry.AccordoServizioParteComuneServizioComposto}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.AccordoServizioParteComuneServizioComposto}
	 * @return Object type {@link org.openspcoop2.core.registry.AccordoServizioParteComuneServizioComposto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccordoServizioParteComuneServizioComposto readAccordoServizioParteComuneServizioCompostoFromString(String in) throws DeserializerException {
		return (AccordoServizioParteComuneServizioComposto) this.xmlToObj(in.getBytes(), AccordoServizioParteComuneServizioComposto.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: azione
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.registry.Azione}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.Azione}
	 * @return Object type {@link org.openspcoop2.core.registry.Azione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Azione readAzione(String fileName) throws DeserializerException {
		return (Azione) this.xmlToObj(fileName, Azione.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.registry.Azione}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.Azione}
	 * @return Object type {@link org.openspcoop2.core.registry.Azione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Azione readAzione(File file) throws DeserializerException {
		return (Azione) this.xmlToObj(file, Azione.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.registry.Azione}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.Azione}
	 * @return Object type {@link org.openspcoop2.core.registry.Azione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Azione readAzione(InputStream in) throws DeserializerException {
		return (Azione) this.xmlToObj(in, Azione.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.registry.Azione}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.Azione}
	 * @return Object type {@link org.openspcoop2.core.registry.Azione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Azione readAzione(byte[] in) throws DeserializerException {
		return (Azione) this.xmlToObj(in, Azione.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.registry.Azione}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.Azione}
	 * @return Object type {@link org.openspcoop2.core.registry.Azione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Azione readAzioneFromString(String in) throws DeserializerException {
		return (Azione) this.xmlToObj(in.getBytes(), Azione.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: servizio-azione-fruitore
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.registry.ServizioAzioneFruitore}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.ServizioAzioneFruitore}
	 * @return Object type {@link org.openspcoop2.core.registry.ServizioAzioneFruitore}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ServizioAzioneFruitore readServizioAzioneFruitore(String fileName) throws DeserializerException {
		return (ServizioAzioneFruitore) this.xmlToObj(fileName, ServizioAzioneFruitore.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.registry.ServizioAzioneFruitore}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.ServizioAzioneFruitore}
	 * @return Object type {@link org.openspcoop2.core.registry.ServizioAzioneFruitore}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ServizioAzioneFruitore readServizioAzioneFruitore(File file) throws DeserializerException {
		return (ServizioAzioneFruitore) this.xmlToObj(file, ServizioAzioneFruitore.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.registry.ServizioAzioneFruitore}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.ServizioAzioneFruitore}
	 * @return Object type {@link org.openspcoop2.core.registry.ServizioAzioneFruitore}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ServizioAzioneFruitore readServizioAzioneFruitore(InputStream in) throws DeserializerException {
		return (ServizioAzioneFruitore) this.xmlToObj(in, ServizioAzioneFruitore.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.registry.ServizioAzioneFruitore}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.ServizioAzioneFruitore}
	 * @return Object type {@link org.openspcoop2.core.registry.ServizioAzioneFruitore}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ServizioAzioneFruitore readServizioAzioneFruitore(byte[] in) throws DeserializerException {
		return (ServizioAzioneFruitore) this.xmlToObj(in, ServizioAzioneFruitore.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.registry.ServizioAzioneFruitore}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.ServizioAzioneFruitore}
	 * @return Object type {@link org.openspcoop2.core.registry.ServizioAzioneFruitore}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ServizioAzioneFruitore readServizioAzioneFruitoreFromString(String in) throws DeserializerException {
		return (ServizioAzioneFruitore) this.xmlToObj(in.getBytes(), ServizioAzioneFruitore.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: id-accordo-servizio-parte-specifica
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.registry.IdAccordoServizioParteSpecifica}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.IdAccordoServizioParteSpecifica}
	 * @return Object type {@link org.openspcoop2.core.registry.IdAccordoServizioParteSpecifica}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdAccordoServizioParteSpecifica readIdAccordoServizioParteSpecifica(String fileName) throws DeserializerException {
		return (IdAccordoServizioParteSpecifica) this.xmlToObj(fileName, IdAccordoServizioParteSpecifica.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.registry.IdAccordoServizioParteSpecifica}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.IdAccordoServizioParteSpecifica}
	 * @return Object type {@link org.openspcoop2.core.registry.IdAccordoServizioParteSpecifica}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdAccordoServizioParteSpecifica readIdAccordoServizioParteSpecifica(File file) throws DeserializerException {
		return (IdAccordoServizioParteSpecifica) this.xmlToObj(file, IdAccordoServizioParteSpecifica.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.registry.IdAccordoServizioParteSpecifica}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.IdAccordoServizioParteSpecifica}
	 * @return Object type {@link org.openspcoop2.core.registry.IdAccordoServizioParteSpecifica}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdAccordoServizioParteSpecifica readIdAccordoServizioParteSpecifica(InputStream in) throws DeserializerException {
		return (IdAccordoServizioParteSpecifica) this.xmlToObj(in, IdAccordoServizioParteSpecifica.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.registry.IdAccordoServizioParteSpecifica}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.IdAccordoServizioParteSpecifica}
	 * @return Object type {@link org.openspcoop2.core.registry.IdAccordoServizioParteSpecifica}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdAccordoServizioParteSpecifica readIdAccordoServizioParteSpecifica(byte[] in) throws DeserializerException {
		return (IdAccordoServizioParteSpecifica) this.xmlToObj(in, IdAccordoServizioParteSpecifica.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.registry.IdAccordoServizioParteSpecifica}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.IdAccordoServizioParteSpecifica}
	 * @return Object type {@link org.openspcoop2.core.registry.IdAccordoServizioParteSpecifica}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdAccordoServizioParteSpecifica readIdAccordoServizioParteSpecificaFromString(String in) throws DeserializerException {
		return (IdAccordoServizioParteSpecifica) this.xmlToObj(in.getBytes(), IdAccordoServizioParteSpecifica.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: id-accordo-cooperazione
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.registry.IdAccordoCooperazione}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.IdAccordoCooperazione}
	 * @return Object type {@link org.openspcoop2.core.registry.IdAccordoCooperazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdAccordoCooperazione readIdAccordoCooperazione(String fileName) throws DeserializerException {
		return (IdAccordoCooperazione) this.xmlToObj(fileName, IdAccordoCooperazione.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.registry.IdAccordoCooperazione}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.IdAccordoCooperazione}
	 * @return Object type {@link org.openspcoop2.core.registry.IdAccordoCooperazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdAccordoCooperazione readIdAccordoCooperazione(File file) throws DeserializerException {
		return (IdAccordoCooperazione) this.xmlToObj(file, IdAccordoCooperazione.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.registry.IdAccordoCooperazione}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.IdAccordoCooperazione}
	 * @return Object type {@link org.openspcoop2.core.registry.IdAccordoCooperazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdAccordoCooperazione readIdAccordoCooperazione(InputStream in) throws DeserializerException {
		return (IdAccordoCooperazione) this.xmlToObj(in, IdAccordoCooperazione.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.registry.IdAccordoCooperazione}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.IdAccordoCooperazione}
	 * @return Object type {@link org.openspcoop2.core.registry.IdAccordoCooperazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdAccordoCooperazione readIdAccordoCooperazione(byte[] in) throws DeserializerException {
		return (IdAccordoCooperazione) this.xmlToObj(in, IdAccordoCooperazione.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.registry.IdAccordoCooperazione}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.IdAccordoCooperazione}
	 * @return Object type {@link org.openspcoop2.core.registry.IdAccordoCooperazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdAccordoCooperazione readIdAccordoCooperazioneFromString(String in) throws DeserializerException {
		return (IdAccordoCooperazione) this.xmlToObj(in.getBytes(), IdAccordoCooperazione.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: id-accordo-servizio-parte-comune
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.registry.IdAccordoServizioParteComune}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.IdAccordoServizioParteComune}
	 * @return Object type {@link org.openspcoop2.core.registry.IdAccordoServizioParteComune}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdAccordoServizioParteComune readIdAccordoServizioParteComune(String fileName) throws DeserializerException {
		return (IdAccordoServizioParteComune) this.xmlToObj(fileName, IdAccordoServizioParteComune.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.registry.IdAccordoServizioParteComune}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.IdAccordoServizioParteComune}
	 * @return Object type {@link org.openspcoop2.core.registry.IdAccordoServizioParteComune}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdAccordoServizioParteComune readIdAccordoServizioParteComune(File file) throws DeserializerException {
		return (IdAccordoServizioParteComune) this.xmlToObj(file, IdAccordoServizioParteComune.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.registry.IdAccordoServizioParteComune}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.IdAccordoServizioParteComune}
	 * @return Object type {@link org.openspcoop2.core.registry.IdAccordoServizioParteComune}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdAccordoServizioParteComune readIdAccordoServizioParteComune(InputStream in) throws DeserializerException {
		return (IdAccordoServizioParteComune) this.xmlToObj(in, IdAccordoServizioParteComune.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.registry.IdAccordoServizioParteComune}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.IdAccordoServizioParteComune}
	 * @return Object type {@link org.openspcoop2.core.registry.IdAccordoServizioParteComune}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdAccordoServizioParteComune readIdAccordoServizioParteComune(byte[] in) throws DeserializerException {
		return (IdAccordoServizioParteComune) this.xmlToObj(in, IdAccordoServizioParteComune.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.registry.IdAccordoServizioParteComune}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.registry.IdAccordoServizioParteComune}
	 * @return Object type {@link org.openspcoop2.core.registry.IdAccordoServizioParteComune}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdAccordoServizioParteComune readIdAccordoServizioParteComuneFromString(String in) throws DeserializerException {
		return (IdAccordoServizioParteComune) this.xmlToObj(in.getBytes(), IdAccordoServizioParteComune.class);
	}	
	
	
	

}
