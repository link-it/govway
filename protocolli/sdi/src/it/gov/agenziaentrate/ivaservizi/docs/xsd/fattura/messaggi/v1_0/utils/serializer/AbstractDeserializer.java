/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it).
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
package it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.utils.serializer;

import org.openspcoop2.generic_project.exception.DeserializerException;

import it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RicevutaConsegnaType;
import it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.DestinatarioType;
import it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RiferimentoArchivioType;
import it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.ErroreType;
import it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.ListaErroriType;
import it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.FileMetadatiType;
import it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RiferimentoFatturaType;
import it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RicevutaScartoType;
import it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RicevutaImpossibilitaRecapitoType;

import java.io.InputStream;
import java.io.File;

/**     
 * XML Deserializer of beans
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public abstract class AbstractDeserializer extends org.openspcoop2.generic_project.serializer.AbstractDeserializerBase {



	/*
	 =================================================================================
	 Object: RicevutaConsegna_Type
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RicevutaConsegnaType}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RicevutaConsegnaType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RicevutaConsegnaType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RicevutaConsegnaType readRicevutaConsegnaType(String fileName) throws DeserializerException {
		return (RicevutaConsegnaType) this.xmlToObj(fileName, RicevutaConsegnaType.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RicevutaConsegnaType}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RicevutaConsegnaType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RicevutaConsegnaType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RicevutaConsegnaType readRicevutaConsegnaType(File file) throws DeserializerException {
		return (RicevutaConsegnaType) this.xmlToObj(file, RicevutaConsegnaType.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RicevutaConsegnaType}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RicevutaConsegnaType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RicevutaConsegnaType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RicevutaConsegnaType readRicevutaConsegnaType(InputStream in) throws DeserializerException {
		return (RicevutaConsegnaType) this.xmlToObj(in, RicevutaConsegnaType.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RicevutaConsegnaType}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RicevutaConsegnaType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RicevutaConsegnaType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RicevutaConsegnaType readRicevutaConsegnaType(byte[] in) throws DeserializerException {
		return (RicevutaConsegnaType) this.xmlToObj(in, RicevutaConsegnaType.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RicevutaConsegnaType}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RicevutaConsegnaType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RicevutaConsegnaType}
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
	 * Transform the xml in <var>fileName</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.DestinatarioType}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.DestinatarioType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.DestinatarioType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DestinatarioType readDestinatarioType(String fileName) throws DeserializerException {
		return (DestinatarioType) this.xmlToObj(fileName, DestinatarioType.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.DestinatarioType}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.DestinatarioType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.DestinatarioType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DestinatarioType readDestinatarioType(File file) throws DeserializerException {
		return (DestinatarioType) this.xmlToObj(file, DestinatarioType.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.DestinatarioType}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.DestinatarioType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.DestinatarioType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DestinatarioType readDestinatarioType(InputStream in) throws DeserializerException {
		return (DestinatarioType) this.xmlToObj(in, DestinatarioType.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.DestinatarioType}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.DestinatarioType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.DestinatarioType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DestinatarioType readDestinatarioType(byte[] in) throws DeserializerException {
		return (DestinatarioType) this.xmlToObj(in, DestinatarioType.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.DestinatarioType}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.DestinatarioType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.DestinatarioType}
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
	 * Transform the xml in <var>fileName</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RiferimentoArchivioType}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RiferimentoArchivioType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RiferimentoArchivioType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RiferimentoArchivioType readRiferimentoArchivioType(String fileName) throws DeserializerException {
		return (RiferimentoArchivioType) this.xmlToObj(fileName, RiferimentoArchivioType.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RiferimentoArchivioType}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RiferimentoArchivioType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RiferimentoArchivioType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RiferimentoArchivioType readRiferimentoArchivioType(File file) throws DeserializerException {
		return (RiferimentoArchivioType) this.xmlToObj(file, RiferimentoArchivioType.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RiferimentoArchivioType}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RiferimentoArchivioType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RiferimentoArchivioType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RiferimentoArchivioType readRiferimentoArchivioType(InputStream in) throws DeserializerException {
		return (RiferimentoArchivioType) this.xmlToObj(in, RiferimentoArchivioType.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RiferimentoArchivioType}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RiferimentoArchivioType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RiferimentoArchivioType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RiferimentoArchivioType readRiferimentoArchivioType(byte[] in) throws DeserializerException {
		return (RiferimentoArchivioType) this.xmlToObj(in, RiferimentoArchivioType.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RiferimentoArchivioType}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RiferimentoArchivioType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RiferimentoArchivioType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RiferimentoArchivioType readRiferimentoArchivioTypeFromString(String in) throws DeserializerException {
		return (RiferimentoArchivioType) this.xmlToObj(in.getBytes(), RiferimentoArchivioType.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: Errore_Type
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.ErroreType}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.ErroreType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.ErroreType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ErroreType readErroreType(String fileName) throws DeserializerException {
		return (ErroreType) this.xmlToObj(fileName, ErroreType.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.ErroreType}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.ErroreType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.ErroreType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ErroreType readErroreType(File file) throws DeserializerException {
		return (ErroreType) this.xmlToObj(file, ErroreType.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.ErroreType}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.ErroreType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.ErroreType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ErroreType readErroreType(InputStream in) throws DeserializerException {
		return (ErroreType) this.xmlToObj(in, ErroreType.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.ErroreType}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.ErroreType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.ErroreType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ErroreType readErroreType(byte[] in) throws DeserializerException {
		return (ErroreType) this.xmlToObj(in, ErroreType.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.ErroreType}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.ErroreType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.ErroreType}
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
	 * Transform the xml in <var>fileName</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.ListaErroriType}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.ListaErroriType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.ListaErroriType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ListaErroriType readListaErroriType(String fileName) throws DeserializerException {
		return (ListaErroriType) this.xmlToObj(fileName, ListaErroriType.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.ListaErroriType}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.ListaErroriType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.ListaErroriType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ListaErroriType readListaErroriType(File file) throws DeserializerException {
		return (ListaErroriType) this.xmlToObj(file, ListaErroriType.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.ListaErroriType}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.ListaErroriType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.ListaErroriType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ListaErroriType readListaErroriType(InputStream in) throws DeserializerException {
		return (ListaErroriType) this.xmlToObj(in, ListaErroriType.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.ListaErroriType}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.ListaErroriType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.ListaErroriType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ListaErroriType readListaErroriType(byte[] in) throws DeserializerException {
		return (ListaErroriType) this.xmlToObj(in, ListaErroriType.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.ListaErroriType}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.ListaErroriType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.ListaErroriType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ListaErroriType readListaErroriTypeFromString(String in) throws DeserializerException {
		return (ListaErroriType) this.xmlToObj(in.getBytes(), ListaErroriType.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: FileMetadati_Type
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.FileMetadatiType}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.FileMetadatiType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.FileMetadatiType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public FileMetadatiType readFileMetadatiType(String fileName) throws DeserializerException {
		return (FileMetadatiType) this.xmlToObj(fileName, FileMetadatiType.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.FileMetadatiType}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.FileMetadatiType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.FileMetadatiType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public FileMetadatiType readFileMetadatiType(File file) throws DeserializerException {
		return (FileMetadatiType) this.xmlToObj(file, FileMetadatiType.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.FileMetadatiType}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.FileMetadatiType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.FileMetadatiType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public FileMetadatiType readFileMetadatiType(InputStream in) throws DeserializerException {
		return (FileMetadatiType) this.xmlToObj(in, FileMetadatiType.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.FileMetadatiType}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.FileMetadatiType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.FileMetadatiType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public FileMetadatiType readFileMetadatiType(byte[] in) throws DeserializerException {
		return (FileMetadatiType) this.xmlToObj(in, FileMetadatiType.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.FileMetadatiType}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.FileMetadatiType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.FileMetadatiType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public FileMetadatiType readFileMetadatiTypeFromString(String in) throws DeserializerException {
		return (FileMetadatiType) this.xmlToObj(in.getBytes(), FileMetadatiType.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: RiferimentoFattura_Type
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RiferimentoFatturaType}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RiferimentoFatturaType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RiferimentoFatturaType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RiferimentoFatturaType readRiferimentoFatturaType(String fileName) throws DeserializerException {
		return (RiferimentoFatturaType) this.xmlToObj(fileName, RiferimentoFatturaType.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RiferimentoFatturaType}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RiferimentoFatturaType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RiferimentoFatturaType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RiferimentoFatturaType readRiferimentoFatturaType(File file) throws DeserializerException {
		return (RiferimentoFatturaType) this.xmlToObj(file, RiferimentoFatturaType.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RiferimentoFatturaType}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RiferimentoFatturaType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RiferimentoFatturaType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RiferimentoFatturaType readRiferimentoFatturaType(InputStream in) throws DeserializerException {
		return (RiferimentoFatturaType) this.xmlToObj(in, RiferimentoFatturaType.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RiferimentoFatturaType}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RiferimentoFatturaType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RiferimentoFatturaType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RiferimentoFatturaType readRiferimentoFatturaType(byte[] in) throws DeserializerException {
		return (RiferimentoFatturaType) this.xmlToObj(in, RiferimentoFatturaType.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RiferimentoFatturaType}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RiferimentoFatturaType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RiferimentoFatturaType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RiferimentoFatturaType readRiferimentoFatturaTypeFromString(String in) throws DeserializerException {
		return (RiferimentoFatturaType) this.xmlToObj(in.getBytes(), RiferimentoFatturaType.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: RicevutaScarto_Type
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RicevutaScartoType}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RicevutaScartoType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RicevutaScartoType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RicevutaScartoType readRicevutaScartoType(String fileName) throws DeserializerException {
		return (RicevutaScartoType) this.xmlToObj(fileName, RicevutaScartoType.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RicevutaScartoType}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RicevutaScartoType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RicevutaScartoType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RicevutaScartoType readRicevutaScartoType(File file) throws DeserializerException {
		return (RicevutaScartoType) this.xmlToObj(file, RicevutaScartoType.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RicevutaScartoType}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RicevutaScartoType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RicevutaScartoType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RicevutaScartoType readRicevutaScartoType(InputStream in) throws DeserializerException {
		return (RicevutaScartoType) this.xmlToObj(in, RicevutaScartoType.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RicevutaScartoType}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RicevutaScartoType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RicevutaScartoType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RicevutaScartoType readRicevutaScartoType(byte[] in) throws DeserializerException {
		return (RicevutaScartoType) this.xmlToObj(in, RicevutaScartoType.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RicevutaScartoType}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RicevutaScartoType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RicevutaScartoType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RicevutaScartoType readRicevutaScartoTypeFromString(String in) throws DeserializerException {
		return (RicevutaScartoType) this.xmlToObj(in.getBytes(), RicevutaScartoType.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: RicevutaImpossibilitaRecapito_Type
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RicevutaImpossibilitaRecapitoType}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RicevutaImpossibilitaRecapitoType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RicevutaImpossibilitaRecapitoType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RicevutaImpossibilitaRecapitoType readRicevutaImpossibilitaRecapitoType(String fileName) throws DeserializerException {
		return (RicevutaImpossibilitaRecapitoType) this.xmlToObj(fileName, RicevutaImpossibilitaRecapitoType.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RicevutaImpossibilitaRecapitoType}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RicevutaImpossibilitaRecapitoType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RicevutaImpossibilitaRecapitoType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RicevutaImpossibilitaRecapitoType readRicevutaImpossibilitaRecapitoType(File file) throws DeserializerException {
		return (RicevutaImpossibilitaRecapitoType) this.xmlToObj(file, RicevutaImpossibilitaRecapitoType.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RicevutaImpossibilitaRecapitoType}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RicevutaImpossibilitaRecapitoType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RicevutaImpossibilitaRecapitoType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RicevutaImpossibilitaRecapitoType readRicevutaImpossibilitaRecapitoType(InputStream in) throws DeserializerException {
		return (RicevutaImpossibilitaRecapitoType) this.xmlToObj(in, RicevutaImpossibilitaRecapitoType.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RicevutaImpossibilitaRecapitoType}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RicevutaImpossibilitaRecapitoType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RicevutaImpossibilitaRecapitoType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RicevutaImpossibilitaRecapitoType readRicevutaImpossibilitaRecapitoType(byte[] in) throws DeserializerException {
		return (RicevutaImpossibilitaRecapitoType) this.xmlToObj(in, RicevutaImpossibilitaRecapitoType.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RicevutaImpossibilitaRecapitoType}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RicevutaImpossibilitaRecapitoType}
	 * @return Object type {@link it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RicevutaImpossibilitaRecapitoType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RicevutaImpossibilitaRecapitoType readRicevutaImpossibilitaRecapitoTypeFromString(String in) throws DeserializerException {
		return (RicevutaImpossibilitaRecapitoType) this.xmlToObj(in.getBytes(), RicevutaImpossibilitaRecapitoType.class);
	}	
	
	
	

}
