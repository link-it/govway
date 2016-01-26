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
package it.gov.fatturapa.sdi.messaggi.v1_0.utils.serializer;

import org.openspcoop2.generic_project.exception.DeserializerException;

import it.gov.fatturapa.sdi.messaggi.v1_0.NotificaEsitoCommittenteType;
import it.gov.fatturapa.sdi.messaggi.v1_0.RiferimentoFatturaType;
import it.gov.fatturapa.sdi.messaggi.v1_0.RicevutaConsegnaType;
import it.gov.fatturapa.sdi.messaggi.v1_0.DestinatarioType;
import it.gov.fatturapa.sdi.messaggi.v1_0.RiferimentoArchivioType;
import it.gov.fatturapa.sdi.messaggi.v1_0.MetadatiInvioFileType;
import it.gov.fatturapa.sdi.messaggi.v1_0.NotificaDecorrenzaTerminiType;
import it.gov.fatturapa.sdi.messaggi.v1_0.ScartoEsitoCommittenteType;
import it.gov.fatturapa.sdi.messaggi.v1_0.ErroreType;
import it.gov.fatturapa.sdi.messaggi.v1_0.ListaErroriType;
import it.gov.fatturapa.sdi.messaggi.v1_0.NotificaEsitoType;
import it.gov.fatturapa.sdi.messaggi.v1_0.NotificaScartoType;
import it.gov.fatturapa.sdi.messaggi.v1_0.NotificaMancataConsegnaType;
import it.gov.fatturapa.sdi.messaggi.v1_0.AttestazioneTrasmissioneFatturaType;

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
	 Object: NotificaEsitoCommittente_Type
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.NotificaEsitoCommittenteType}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.NotificaEsitoCommittenteType}
	 * @return Object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.NotificaEsitoCommittenteType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public NotificaEsitoCommittenteType readNotificaEsitoCommittenteType(String fileName) throws DeserializerException {
		return (NotificaEsitoCommittenteType) this.xmlToObj(fileName, NotificaEsitoCommittenteType.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.NotificaEsitoCommittenteType}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.NotificaEsitoCommittenteType}
	 * @return Object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.NotificaEsitoCommittenteType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public NotificaEsitoCommittenteType readNotificaEsitoCommittenteType(File file) throws DeserializerException {
		return (NotificaEsitoCommittenteType) this.xmlToObj(file, NotificaEsitoCommittenteType.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.NotificaEsitoCommittenteType}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.NotificaEsitoCommittenteType}
	 * @return Object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.NotificaEsitoCommittenteType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public NotificaEsitoCommittenteType readNotificaEsitoCommittenteType(InputStream in) throws DeserializerException {
		return (NotificaEsitoCommittenteType) this.xmlToObj(in, NotificaEsitoCommittenteType.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.NotificaEsitoCommittenteType}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.NotificaEsitoCommittenteType}
	 * @return Object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.NotificaEsitoCommittenteType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public NotificaEsitoCommittenteType readNotificaEsitoCommittenteType(byte[] in) throws DeserializerException {
		return (NotificaEsitoCommittenteType) this.xmlToObj(in, NotificaEsitoCommittenteType.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.NotificaEsitoCommittenteType}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.NotificaEsitoCommittenteType}
	 * @return Object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.NotificaEsitoCommittenteType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public NotificaEsitoCommittenteType readNotificaEsitoCommittenteTypeFromString(String in) throws DeserializerException {
		return (NotificaEsitoCommittenteType) this.xmlToObj(in.getBytes(), NotificaEsitoCommittenteType.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: RiferimentoFattura_Type
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.RiferimentoFatturaType}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.RiferimentoFatturaType}
	 * @return Object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.RiferimentoFatturaType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RiferimentoFatturaType readRiferimentoFatturaType(String fileName) throws DeserializerException {
		return (RiferimentoFatturaType) this.xmlToObj(fileName, RiferimentoFatturaType.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.RiferimentoFatturaType}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.RiferimentoFatturaType}
	 * @return Object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.RiferimentoFatturaType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RiferimentoFatturaType readRiferimentoFatturaType(File file) throws DeserializerException {
		return (RiferimentoFatturaType) this.xmlToObj(file, RiferimentoFatturaType.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.RiferimentoFatturaType}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.RiferimentoFatturaType}
	 * @return Object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.RiferimentoFatturaType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RiferimentoFatturaType readRiferimentoFatturaType(InputStream in) throws DeserializerException {
		return (RiferimentoFatturaType) this.xmlToObj(in, RiferimentoFatturaType.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.RiferimentoFatturaType}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.RiferimentoFatturaType}
	 * @return Object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.RiferimentoFatturaType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RiferimentoFatturaType readRiferimentoFatturaType(byte[] in) throws DeserializerException {
		return (RiferimentoFatturaType) this.xmlToObj(in, RiferimentoFatturaType.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.RiferimentoFatturaType}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.RiferimentoFatturaType}
	 * @return Object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.RiferimentoFatturaType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RiferimentoFatturaType readRiferimentoFatturaTypeFromString(String in) throws DeserializerException {
		return (RiferimentoFatturaType) this.xmlToObj(in.getBytes(), RiferimentoFatturaType.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: RicevutaConsegna_Type
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.RicevutaConsegnaType}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.RicevutaConsegnaType}
	 * @return Object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.RicevutaConsegnaType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RicevutaConsegnaType readRicevutaConsegnaType(String fileName) throws DeserializerException {
		return (RicevutaConsegnaType) this.xmlToObj(fileName, RicevutaConsegnaType.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.RicevutaConsegnaType}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.RicevutaConsegnaType}
	 * @return Object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.RicevutaConsegnaType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RicevutaConsegnaType readRicevutaConsegnaType(File file) throws DeserializerException {
		return (RicevutaConsegnaType) this.xmlToObj(file, RicevutaConsegnaType.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.RicevutaConsegnaType}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.RicevutaConsegnaType}
	 * @return Object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.RicevutaConsegnaType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RicevutaConsegnaType readRicevutaConsegnaType(InputStream in) throws DeserializerException {
		return (RicevutaConsegnaType) this.xmlToObj(in, RicevutaConsegnaType.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.RicevutaConsegnaType}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.RicevutaConsegnaType}
	 * @return Object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.RicevutaConsegnaType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RicevutaConsegnaType readRicevutaConsegnaType(byte[] in) throws DeserializerException {
		return (RicevutaConsegnaType) this.xmlToObj(in, RicevutaConsegnaType.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.RicevutaConsegnaType}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.RicevutaConsegnaType}
	 * @return Object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.RicevutaConsegnaType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RicevutaConsegnaType readRicevutaConsegnaTypeFromString(String in) throws DeserializerException {
		return (RicevutaConsegnaType) this.xmlToObj(in.getBytes(), RicevutaConsegnaType.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: Destinatario_Type
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.DestinatarioType}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.DestinatarioType}
	 * @return Object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.DestinatarioType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DestinatarioType readDestinatarioType(String fileName) throws DeserializerException {
		return (DestinatarioType) this.xmlToObj(fileName, DestinatarioType.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.DestinatarioType}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.DestinatarioType}
	 * @return Object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.DestinatarioType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DestinatarioType readDestinatarioType(File file) throws DeserializerException {
		return (DestinatarioType) this.xmlToObj(file, DestinatarioType.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.DestinatarioType}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.DestinatarioType}
	 * @return Object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.DestinatarioType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DestinatarioType readDestinatarioType(InputStream in) throws DeserializerException {
		return (DestinatarioType) this.xmlToObj(in, DestinatarioType.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.DestinatarioType}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.DestinatarioType}
	 * @return Object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.DestinatarioType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DestinatarioType readDestinatarioType(byte[] in) throws DeserializerException {
		return (DestinatarioType) this.xmlToObj(in, DestinatarioType.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.DestinatarioType}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.DestinatarioType}
	 * @return Object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.DestinatarioType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DestinatarioType readDestinatarioTypeFromString(String in) throws DeserializerException {
		return (DestinatarioType) this.xmlToObj(in.getBytes(), DestinatarioType.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: RiferimentoArchivio_Type
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.RiferimentoArchivioType}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.RiferimentoArchivioType}
	 * @return Object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.RiferimentoArchivioType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RiferimentoArchivioType readRiferimentoArchivioType(String fileName) throws DeserializerException {
		return (RiferimentoArchivioType) this.xmlToObj(fileName, RiferimentoArchivioType.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.RiferimentoArchivioType}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.RiferimentoArchivioType}
	 * @return Object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.RiferimentoArchivioType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RiferimentoArchivioType readRiferimentoArchivioType(File file) throws DeserializerException {
		return (RiferimentoArchivioType) this.xmlToObj(file, RiferimentoArchivioType.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.RiferimentoArchivioType}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.RiferimentoArchivioType}
	 * @return Object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.RiferimentoArchivioType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RiferimentoArchivioType readRiferimentoArchivioType(InputStream in) throws DeserializerException {
		return (RiferimentoArchivioType) this.xmlToObj(in, RiferimentoArchivioType.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.RiferimentoArchivioType}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.RiferimentoArchivioType}
	 * @return Object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.RiferimentoArchivioType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RiferimentoArchivioType readRiferimentoArchivioType(byte[] in) throws DeserializerException {
		return (RiferimentoArchivioType) this.xmlToObj(in, RiferimentoArchivioType.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.RiferimentoArchivioType}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.RiferimentoArchivioType}
	 * @return Object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.RiferimentoArchivioType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RiferimentoArchivioType readRiferimentoArchivioTypeFromString(String in) throws DeserializerException {
		return (RiferimentoArchivioType) this.xmlToObj(in.getBytes(), RiferimentoArchivioType.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: MetadatiInvioFile_Type
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.MetadatiInvioFileType}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.MetadatiInvioFileType}
	 * @return Object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.MetadatiInvioFileType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public MetadatiInvioFileType readMetadatiInvioFileType(String fileName) throws DeserializerException {
		return (MetadatiInvioFileType) this.xmlToObj(fileName, MetadatiInvioFileType.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.MetadatiInvioFileType}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.MetadatiInvioFileType}
	 * @return Object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.MetadatiInvioFileType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public MetadatiInvioFileType readMetadatiInvioFileType(File file) throws DeserializerException {
		return (MetadatiInvioFileType) this.xmlToObj(file, MetadatiInvioFileType.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.MetadatiInvioFileType}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.MetadatiInvioFileType}
	 * @return Object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.MetadatiInvioFileType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public MetadatiInvioFileType readMetadatiInvioFileType(InputStream in) throws DeserializerException {
		return (MetadatiInvioFileType) this.xmlToObj(in, MetadatiInvioFileType.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.MetadatiInvioFileType}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.MetadatiInvioFileType}
	 * @return Object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.MetadatiInvioFileType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public MetadatiInvioFileType readMetadatiInvioFileType(byte[] in) throws DeserializerException {
		return (MetadatiInvioFileType) this.xmlToObj(in, MetadatiInvioFileType.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.MetadatiInvioFileType}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.MetadatiInvioFileType}
	 * @return Object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.MetadatiInvioFileType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public MetadatiInvioFileType readMetadatiInvioFileTypeFromString(String in) throws DeserializerException {
		return (MetadatiInvioFileType) this.xmlToObj(in.getBytes(), MetadatiInvioFileType.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: NotificaDecorrenzaTermini_Type
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.NotificaDecorrenzaTerminiType}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.NotificaDecorrenzaTerminiType}
	 * @return Object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.NotificaDecorrenzaTerminiType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public NotificaDecorrenzaTerminiType readNotificaDecorrenzaTerminiType(String fileName) throws DeserializerException {
		return (NotificaDecorrenzaTerminiType) this.xmlToObj(fileName, NotificaDecorrenzaTerminiType.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.NotificaDecorrenzaTerminiType}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.NotificaDecorrenzaTerminiType}
	 * @return Object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.NotificaDecorrenzaTerminiType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public NotificaDecorrenzaTerminiType readNotificaDecorrenzaTerminiType(File file) throws DeserializerException {
		return (NotificaDecorrenzaTerminiType) this.xmlToObj(file, NotificaDecorrenzaTerminiType.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.NotificaDecorrenzaTerminiType}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.NotificaDecorrenzaTerminiType}
	 * @return Object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.NotificaDecorrenzaTerminiType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public NotificaDecorrenzaTerminiType readNotificaDecorrenzaTerminiType(InputStream in) throws DeserializerException {
		return (NotificaDecorrenzaTerminiType) this.xmlToObj(in, NotificaDecorrenzaTerminiType.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.NotificaDecorrenzaTerminiType}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.NotificaDecorrenzaTerminiType}
	 * @return Object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.NotificaDecorrenzaTerminiType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public NotificaDecorrenzaTerminiType readNotificaDecorrenzaTerminiType(byte[] in) throws DeserializerException {
		return (NotificaDecorrenzaTerminiType) this.xmlToObj(in, NotificaDecorrenzaTerminiType.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.NotificaDecorrenzaTerminiType}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.NotificaDecorrenzaTerminiType}
	 * @return Object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.NotificaDecorrenzaTerminiType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public NotificaDecorrenzaTerminiType readNotificaDecorrenzaTerminiTypeFromString(String in) throws DeserializerException {
		return (NotificaDecorrenzaTerminiType) this.xmlToObj(in.getBytes(), NotificaDecorrenzaTerminiType.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: ScartoEsitoCommittente_Type
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.ScartoEsitoCommittenteType}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.ScartoEsitoCommittenteType}
	 * @return Object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.ScartoEsitoCommittenteType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ScartoEsitoCommittenteType readScartoEsitoCommittenteType(String fileName) throws DeserializerException {
		return (ScartoEsitoCommittenteType) this.xmlToObj(fileName, ScartoEsitoCommittenteType.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.ScartoEsitoCommittenteType}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.ScartoEsitoCommittenteType}
	 * @return Object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.ScartoEsitoCommittenteType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ScartoEsitoCommittenteType readScartoEsitoCommittenteType(File file) throws DeserializerException {
		return (ScartoEsitoCommittenteType) this.xmlToObj(file, ScartoEsitoCommittenteType.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.ScartoEsitoCommittenteType}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.ScartoEsitoCommittenteType}
	 * @return Object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.ScartoEsitoCommittenteType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ScartoEsitoCommittenteType readScartoEsitoCommittenteType(InputStream in) throws DeserializerException {
		return (ScartoEsitoCommittenteType) this.xmlToObj(in, ScartoEsitoCommittenteType.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.ScartoEsitoCommittenteType}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.ScartoEsitoCommittenteType}
	 * @return Object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.ScartoEsitoCommittenteType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ScartoEsitoCommittenteType readScartoEsitoCommittenteType(byte[] in) throws DeserializerException {
		return (ScartoEsitoCommittenteType) this.xmlToObj(in, ScartoEsitoCommittenteType.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.ScartoEsitoCommittenteType}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.ScartoEsitoCommittenteType}
	 * @return Object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.ScartoEsitoCommittenteType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ScartoEsitoCommittenteType readScartoEsitoCommittenteTypeFromString(String in) throws DeserializerException {
		return (ScartoEsitoCommittenteType) this.xmlToObj(in.getBytes(), ScartoEsitoCommittenteType.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: Errore_Type
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.ErroreType}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.ErroreType}
	 * @return Object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.ErroreType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ErroreType readErroreType(String fileName) throws DeserializerException {
		return (ErroreType) this.xmlToObj(fileName, ErroreType.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.ErroreType}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.ErroreType}
	 * @return Object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.ErroreType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ErroreType readErroreType(File file) throws DeserializerException {
		return (ErroreType) this.xmlToObj(file, ErroreType.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.ErroreType}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.ErroreType}
	 * @return Object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.ErroreType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ErroreType readErroreType(InputStream in) throws DeserializerException {
		return (ErroreType) this.xmlToObj(in, ErroreType.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.ErroreType}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.ErroreType}
	 * @return Object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.ErroreType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ErroreType readErroreType(byte[] in) throws DeserializerException {
		return (ErroreType) this.xmlToObj(in, ErroreType.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.ErroreType}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.ErroreType}
	 * @return Object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.ErroreType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ErroreType readErroreTypeFromString(String in) throws DeserializerException {
		return (ErroreType) this.xmlToObj(in.getBytes(), ErroreType.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: ListaErrori_Type
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.ListaErroriType}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.ListaErroriType}
	 * @return Object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.ListaErroriType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ListaErroriType readListaErroriType(String fileName) throws DeserializerException {
		return (ListaErroriType) this.xmlToObj(fileName, ListaErroriType.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.ListaErroriType}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.ListaErroriType}
	 * @return Object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.ListaErroriType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ListaErroriType readListaErroriType(File file) throws DeserializerException {
		return (ListaErroriType) this.xmlToObj(file, ListaErroriType.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.ListaErroriType}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.ListaErroriType}
	 * @return Object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.ListaErroriType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ListaErroriType readListaErroriType(InputStream in) throws DeserializerException {
		return (ListaErroriType) this.xmlToObj(in, ListaErroriType.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.ListaErroriType}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.ListaErroriType}
	 * @return Object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.ListaErroriType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ListaErroriType readListaErroriType(byte[] in) throws DeserializerException {
		return (ListaErroriType) this.xmlToObj(in, ListaErroriType.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.ListaErroriType}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.ListaErroriType}
	 * @return Object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.ListaErroriType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ListaErroriType readListaErroriTypeFromString(String in) throws DeserializerException {
		return (ListaErroriType) this.xmlToObj(in.getBytes(), ListaErroriType.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: NotificaEsito_Type
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.NotificaEsitoType}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.NotificaEsitoType}
	 * @return Object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.NotificaEsitoType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public NotificaEsitoType readNotificaEsitoType(String fileName) throws DeserializerException {
		return (NotificaEsitoType) this.xmlToObj(fileName, NotificaEsitoType.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.NotificaEsitoType}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.NotificaEsitoType}
	 * @return Object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.NotificaEsitoType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public NotificaEsitoType readNotificaEsitoType(File file) throws DeserializerException {
		return (NotificaEsitoType) this.xmlToObj(file, NotificaEsitoType.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.NotificaEsitoType}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.NotificaEsitoType}
	 * @return Object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.NotificaEsitoType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public NotificaEsitoType readNotificaEsitoType(InputStream in) throws DeserializerException {
		return (NotificaEsitoType) this.xmlToObj(in, NotificaEsitoType.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.NotificaEsitoType}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.NotificaEsitoType}
	 * @return Object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.NotificaEsitoType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public NotificaEsitoType readNotificaEsitoType(byte[] in) throws DeserializerException {
		return (NotificaEsitoType) this.xmlToObj(in, NotificaEsitoType.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.NotificaEsitoType}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.NotificaEsitoType}
	 * @return Object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.NotificaEsitoType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public NotificaEsitoType readNotificaEsitoTypeFromString(String in) throws DeserializerException {
		return (NotificaEsitoType) this.xmlToObj(in.getBytes(), NotificaEsitoType.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: NotificaScarto_Type
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.NotificaScartoType}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.NotificaScartoType}
	 * @return Object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.NotificaScartoType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public NotificaScartoType readNotificaScartoType(String fileName) throws DeserializerException {
		return (NotificaScartoType) this.xmlToObj(fileName, NotificaScartoType.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.NotificaScartoType}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.NotificaScartoType}
	 * @return Object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.NotificaScartoType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public NotificaScartoType readNotificaScartoType(File file) throws DeserializerException {
		return (NotificaScartoType) this.xmlToObj(file, NotificaScartoType.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.NotificaScartoType}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.NotificaScartoType}
	 * @return Object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.NotificaScartoType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public NotificaScartoType readNotificaScartoType(InputStream in) throws DeserializerException {
		return (NotificaScartoType) this.xmlToObj(in, NotificaScartoType.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.NotificaScartoType}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.NotificaScartoType}
	 * @return Object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.NotificaScartoType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public NotificaScartoType readNotificaScartoType(byte[] in) throws DeserializerException {
		return (NotificaScartoType) this.xmlToObj(in, NotificaScartoType.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.NotificaScartoType}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.NotificaScartoType}
	 * @return Object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.NotificaScartoType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public NotificaScartoType readNotificaScartoTypeFromString(String in) throws DeserializerException {
		return (NotificaScartoType) this.xmlToObj(in.getBytes(), NotificaScartoType.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: NotificaMancataConsegna_Type
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.NotificaMancataConsegnaType}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.NotificaMancataConsegnaType}
	 * @return Object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.NotificaMancataConsegnaType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public NotificaMancataConsegnaType readNotificaMancataConsegnaType(String fileName) throws DeserializerException {
		return (NotificaMancataConsegnaType) this.xmlToObj(fileName, NotificaMancataConsegnaType.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.NotificaMancataConsegnaType}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.NotificaMancataConsegnaType}
	 * @return Object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.NotificaMancataConsegnaType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public NotificaMancataConsegnaType readNotificaMancataConsegnaType(File file) throws DeserializerException {
		return (NotificaMancataConsegnaType) this.xmlToObj(file, NotificaMancataConsegnaType.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.NotificaMancataConsegnaType}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.NotificaMancataConsegnaType}
	 * @return Object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.NotificaMancataConsegnaType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public NotificaMancataConsegnaType readNotificaMancataConsegnaType(InputStream in) throws DeserializerException {
		return (NotificaMancataConsegnaType) this.xmlToObj(in, NotificaMancataConsegnaType.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.NotificaMancataConsegnaType}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.NotificaMancataConsegnaType}
	 * @return Object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.NotificaMancataConsegnaType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public NotificaMancataConsegnaType readNotificaMancataConsegnaType(byte[] in) throws DeserializerException {
		return (NotificaMancataConsegnaType) this.xmlToObj(in, NotificaMancataConsegnaType.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.NotificaMancataConsegnaType}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.NotificaMancataConsegnaType}
	 * @return Object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.NotificaMancataConsegnaType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public NotificaMancataConsegnaType readNotificaMancataConsegnaTypeFromString(String in) throws DeserializerException {
		return (NotificaMancataConsegnaType) this.xmlToObj(in.getBytes(), NotificaMancataConsegnaType.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: AttestazioneTrasmissioneFattura_Type
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.AttestazioneTrasmissioneFatturaType}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.AttestazioneTrasmissioneFatturaType}
	 * @return Object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.AttestazioneTrasmissioneFatturaType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AttestazioneTrasmissioneFatturaType readAttestazioneTrasmissioneFatturaType(String fileName) throws DeserializerException {
		return (AttestazioneTrasmissioneFatturaType) this.xmlToObj(fileName, AttestazioneTrasmissioneFatturaType.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.AttestazioneTrasmissioneFatturaType}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.AttestazioneTrasmissioneFatturaType}
	 * @return Object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.AttestazioneTrasmissioneFatturaType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AttestazioneTrasmissioneFatturaType readAttestazioneTrasmissioneFatturaType(File file) throws DeserializerException {
		return (AttestazioneTrasmissioneFatturaType) this.xmlToObj(file, AttestazioneTrasmissioneFatturaType.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.AttestazioneTrasmissioneFatturaType}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.AttestazioneTrasmissioneFatturaType}
	 * @return Object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.AttestazioneTrasmissioneFatturaType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AttestazioneTrasmissioneFatturaType readAttestazioneTrasmissioneFatturaType(InputStream in) throws DeserializerException {
		return (AttestazioneTrasmissioneFatturaType) this.xmlToObj(in, AttestazioneTrasmissioneFatturaType.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.AttestazioneTrasmissioneFatturaType}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.AttestazioneTrasmissioneFatturaType}
	 * @return Object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.AttestazioneTrasmissioneFatturaType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AttestazioneTrasmissioneFatturaType readAttestazioneTrasmissioneFatturaType(byte[] in) throws DeserializerException {
		return (AttestazioneTrasmissioneFatturaType) this.xmlToObj(in, AttestazioneTrasmissioneFatturaType.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.AttestazioneTrasmissioneFatturaType}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.AttestazioneTrasmissioneFatturaType}
	 * @return Object type {@link it.gov.fatturapa.sdi.messaggi.v1_0.AttestazioneTrasmissioneFatturaType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AttestazioneTrasmissioneFatturaType readAttestazioneTrasmissioneFatturaTypeFromString(String in) throws DeserializerException {
		return (AttestazioneTrasmissioneFatturaType) this.xmlToObj(in.getBytes(), AttestazioneTrasmissioneFatturaType.class);
	}	
	
	
	

}
