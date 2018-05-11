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
package org.openspcoop2.core.eccezione.errore_applicativo.utils.serializer;

import org.openspcoop2.generic_project.exception.DeserializerException;

import org.openspcoop2.core.eccezione.errore_applicativo.CodiceEccezione;
import org.openspcoop2.core.eccezione.errore_applicativo.Eccezione;
import org.openspcoop2.core.eccezione.errore_applicativo.Servizio;
import org.openspcoop2.core.eccezione.errore_applicativo.Dominio;
import org.openspcoop2.core.eccezione.errore_applicativo.ErroreApplicativo;
import org.openspcoop2.core.eccezione.errore_applicativo.DatiCooperazione;
import org.openspcoop2.core.eccezione.errore_applicativo.DominioSoggetto;
import org.openspcoop2.core.eccezione.errore_applicativo.SoggettoIdentificativo;
import org.openspcoop2.core.eccezione.errore_applicativo.Soggetto;

import java.io.InputStream;
import java.io.File;

/**     
 * XML Deserializer of beans
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public abstract class AbstractDeserializer extends org.openspcoop2.generic_project.serializer.AbstractDeserializer {



	/*
	 =================================================================================
	 Object: CodiceEccezione
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.eccezione.errore_applicativo.CodiceEccezione}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.eccezione.errore_applicativo.CodiceEccezione}
	 * @return Object type {@link org.openspcoop2.core.eccezione.errore_applicativo.CodiceEccezione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public CodiceEccezione readCodiceEccezione(String fileName) throws DeserializerException {
		return (CodiceEccezione) this.xmlToObj(fileName, CodiceEccezione.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.eccezione.errore_applicativo.CodiceEccezione}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.eccezione.errore_applicativo.CodiceEccezione}
	 * @return Object type {@link org.openspcoop2.core.eccezione.errore_applicativo.CodiceEccezione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public CodiceEccezione readCodiceEccezione(File file) throws DeserializerException {
		return (CodiceEccezione) this.xmlToObj(file, CodiceEccezione.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.eccezione.errore_applicativo.CodiceEccezione}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.eccezione.errore_applicativo.CodiceEccezione}
	 * @return Object type {@link org.openspcoop2.core.eccezione.errore_applicativo.CodiceEccezione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public CodiceEccezione readCodiceEccezione(InputStream in) throws DeserializerException {
		return (CodiceEccezione) this.xmlToObj(in, CodiceEccezione.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.eccezione.errore_applicativo.CodiceEccezione}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.eccezione.errore_applicativo.CodiceEccezione}
	 * @return Object type {@link org.openspcoop2.core.eccezione.errore_applicativo.CodiceEccezione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public CodiceEccezione readCodiceEccezione(byte[] in) throws DeserializerException {
		return (CodiceEccezione) this.xmlToObj(in, CodiceEccezione.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.eccezione.errore_applicativo.CodiceEccezione}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.eccezione.errore_applicativo.CodiceEccezione}
	 * @return Object type {@link org.openspcoop2.core.eccezione.errore_applicativo.CodiceEccezione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public CodiceEccezione readCodiceEccezioneFromString(String in) throws DeserializerException {
		return (CodiceEccezione) this.xmlToObj(in.getBytes(), CodiceEccezione.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: eccezione
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.eccezione.errore_applicativo.Eccezione}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.eccezione.errore_applicativo.Eccezione}
	 * @return Object type {@link org.openspcoop2.core.eccezione.errore_applicativo.Eccezione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Eccezione readEccezione(String fileName) throws DeserializerException {
		return (Eccezione) this.xmlToObj(fileName, Eccezione.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.eccezione.errore_applicativo.Eccezione}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.eccezione.errore_applicativo.Eccezione}
	 * @return Object type {@link org.openspcoop2.core.eccezione.errore_applicativo.Eccezione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Eccezione readEccezione(File file) throws DeserializerException {
		return (Eccezione) this.xmlToObj(file, Eccezione.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.eccezione.errore_applicativo.Eccezione}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.eccezione.errore_applicativo.Eccezione}
	 * @return Object type {@link org.openspcoop2.core.eccezione.errore_applicativo.Eccezione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Eccezione readEccezione(InputStream in) throws DeserializerException {
		return (Eccezione) this.xmlToObj(in, Eccezione.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.eccezione.errore_applicativo.Eccezione}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.eccezione.errore_applicativo.Eccezione}
	 * @return Object type {@link org.openspcoop2.core.eccezione.errore_applicativo.Eccezione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Eccezione readEccezione(byte[] in) throws DeserializerException {
		return (Eccezione) this.xmlToObj(in, Eccezione.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.eccezione.errore_applicativo.Eccezione}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.eccezione.errore_applicativo.Eccezione}
	 * @return Object type {@link org.openspcoop2.core.eccezione.errore_applicativo.Eccezione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Eccezione readEccezioneFromString(String in) throws DeserializerException {
		return (Eccezione) this.xmlToObj(in.getBytes(), Eccezione.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: servizio
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.eccezione.errore_applicativo.Servizio}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.eccezione.errore_applicativo.Servizio}
	 * @return Object type {@link org.openspcoop2.core.eccezione.errore_applicativo.Servizio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Servizio readServizio(String fileName) throws DeserializerException {
		return (Servizio) this.xmlToObj(fileName, Servizio.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.eccezione.errore_applicativo.Servizio}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.eccezione.errore_applicativo.Servizio}
	 * @return Object type {@link org.openspcoop2.core.eccezione.errore_applicativo.Servizio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Servizio readServizio(File file) throws DeserializerException {
		return (Servizio) this.xmlToObj(file, Servizio.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.eccezione.errore_applicativo.Servizio}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.eccezione.errore_applicativo.Servizio}
	 * @return Object type {@link org.openspcoop2.core.eccezione.errore_applicativo.Servizio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Servizio readServizio(InputStream in) throws DeserializerException {
		return (Servizio) this.xmlToObj(in, Servizio.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.eccezione.errore_applicativo.Servizio}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.eccezione.errore_applicativo.Servizio}
	 * @return Object type {@link org.openspcoop2.core.eccezione.errore_applicativo.Servizio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Servizio readServizio(byte[] in) throws DeserializerException {
		return (Servizio) this.xmlToObj(in, Servizio.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.eccezione.errore_applicativo.Servizio}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.eccezione.errore_applicativo.Servizio}
	 * @return Object type {@link org.openspcoop2.core.eccezione.errore_applicativo.Servizio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Servizio readServizioFromString(String in) throws DeserializerException {
		return (Servizio) this.xmlToObj(in.getBytes(), Servizio.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: dominio
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.eccezione.errore_applicativo.Dominio}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.eccezione.errore_applicativo.Dominio}
	 * @return Object type {@link org.openspcoop2.core.eccezione.errore_applicativo.Dominio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Dominio readDominio(String fileName) throws DeserializerException {
		return (Dominio) this.xmlToObj(fileName, Dominio.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.eccezione.errore_applicativo.Dominio}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.eccezione.errore_applicativo.Dominio}
	 * @return Object type {@link org.openspcoop2.core.eccezione.errore_applicativo.Dominio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Dominio readDominio(File file) throws DeserializerException {
		return (Dominio) this.xmlToObj(file, Dominio.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.eccezione.errore_applicativo.Dominio}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.eccezione.errore_applicativo.Dominio}
	 * @return Object type {@link org.openspcoop2.core.eccezione.errore_applicativo.Dominio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Dominio readDominio(InputStream in) throws DeserializerException {
		return (Dominio) this.xmlToObj(in, Dominio.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.eccezione.errore_applicativo.Dominio}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.eccezione.errore_applicativo.Dominio}
	 * @return Object type {@link org.openspcoop2.core.eccezione.errore_applicativo.Dominio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Dominio readDominio(byte[] in) throws DeserializerException {
		return (Dominio) this.xmlToObj(in, Dominio.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.eccezione.errore_applicativo.Dominio}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.eccezione.errore_applicativo.Dominio}
	 * @return Object type {@link org.openspcoop2.core.eccezione.errore_applicativo.Dominio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Dominio readDominioFromString(String in) throws DeserializerException {
		return (Dominio) this.xmlToObj(in.getBytes(), Dominio.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: errore-applicativo
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.eccezione.errore_applicativo.ErroreApplicativo}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.eccezione.errore_applicativo.ErroreApplicativo}
	 * @return Object type {@link org.openspcoop2.core.eccezione.errore_applicativo.ErroreApplicativo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ErroreApplicativo readErroreApplicativo(String fileName) throws DeserializerException {
		return (ErroreApplicativo) this.xmlToObj(fileName, ErroreApplicativo.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.eccezione.errore_applicativo.ErroreApplicativo}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.eccezione.errore_applicativo.ErroreApplicativo}
	 * @return Object type {@link org.openspcoop2.core.eccezione.errore_applicativo.ErroreApplicativo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ErroreApplicativo readErroreApplicativo(File file) throws DeserializerException {
		return (ErroreApplicativo) this.xmlToObj(file, ErroreApplicativo.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.eccezione.errore_applicativo.ErroreApplicativo}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.eccezione.errore_applicativo.ErroreApplicativo}
	 * @return Object type {@link org.openspcoop2.core.eccezione.errore_applicativo.ErroreApplicativo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ErroreApplicativo readErroreApplicativo(InputStream in) throws DeserializerException {
		return (ErroreApplicativo) this.xmlToObj(in, ErroreApplicativo.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.eccezione.errore_applicativo.ErroreApplicativo}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.eccezione.errore_applicativo.ErroreApplicativo}
	 * @return Object type {@link org.openspcoop2.core.eccezione.errore_applicativo.ErroreApplicativo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ErroreApplicativo readErroreApplicativo(byte[] in) throws DeserializerException {
		return (ErroreApplicativo) this.xmlToObj(in, ErroreApplicativo.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.eccezione.errore_applicativo.ErroreApplicativo}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.eccezione.errore_applicativo.ErroreApplicativo}
	 * @return Object type {@link org.openspcoop2.core.eccezione.errore_applicativo.ErroreApplicativo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ErroreApplicativo readErroreApplicativoFromString(String in) throws DeserializerException {
		return (ErroreApplicativo) this.xmlToObj(in.getBytes(), ErroreApplicativo.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: dati-cooperazione
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.eccezione.errore_applicativo.DatiCooperazione}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.eccezione.errore_applicativo.DatiCooperazione}
	 * @return Object type {@link org.openspcoop2.core.eccezione.errore_applicativo.DatiCooperazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiCooperazione readDatiCooperazione(String fileName) throws DeserializerException {
		return (DatiCooperazione) this.xmlToObj(fileName, DatiCooperazione.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.eccezione.errore_applicativo.DatiCooperazione}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.eccezione.errore_applicativo.DatiCooperazione}
	 * @return Object type {@link org.openspcoop2.core.eccezione.errore_applicativo.DatiCooperazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiCooperazione readDatiCooperazione(File file) throws DeserializerException {
		return (DatiCooperazione) this.xmlToObj(file, DatiCooperazione.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.eccezione.errore_applicativo.DatiCooperazione}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.eccezione.errore_applicativo.DatiCooperazione}
	 * @return Object type {@link org.openspcoop2.core.eccezione.errore_applicativo.DatiCooperazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiCooperazione readDatiCooperazione(InputStream in) throws DeserializerException {
		return (DatiCooperazione) this.xmlToObj(in, DatiCooperazione.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.eccezione.errore_applicativo.DatiCooperazione}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.eccezione.errore_applicativo.DatiCooperazione}
	 * @return Object type {@link org.openspcoop2.core.eccezione.errore_applicativo.DatiCooperazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiCooperazione readDatiCooperazione(byte[] in) throws DeserializerException {
		return (DatiCooperazione) this.xmlToObj(in, DatiCooperazione.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.eccezione.errore_applicativo.DatiCooperazione}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.eccezione.errore_applicativo.DatiCooperazione}
	 * @return Object type {@link org.openspcoop2.core.eccezione.errore_applicativo.DatiCooperazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiCooperazione readDatiCooperazioneFromString(String in) throws DeserializerException {
		return (DatiCooperazione) this.xmlToObj(in.getBytes(), DatiCooperazione.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: dominio-soggetto
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.eccezione.errore_applicativo.DominioSoggetto}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.eccezione.errore_applicativo.DominioSoggetto}
	 * @return Object type {@link org.openspcoop2.core.eccezione.errore_applicativo.DominioSoggetto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DominioSoggetto readDominioSoggetto(String fileName) throws DeserializerException {
		return (DominioSoggetto) this.xmlToObj(fileName, DominioSoggetto.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.eccezione.errore_applicativo.DominioSoggetto}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.eccezione.errore_applicativo.DominioSoggetto}
	 * @return Object type {@link org.openspcoop2.core.eccezione.errore_applicativo.DominioSoggetto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DominioSoggetto readDominioSoggetto(File file) throws DeserializerException {
		return (DominioSoggetto) this.xmlToObj(file, DominioSoggetto.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.eccezione.errore_applicativo.DominioSoggetto}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.eccezione.errore_applicativo.DominioSoggetto}
	 * @return Object type {@link org.openspcoop2.core.eccezione.errore_applicativo.DominioSoggetto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DominioSoggetto readDominioSoggetto(InputStream in) throws DeserializerException {
		return (DominioSoggetto) this.xmlToObj(in, DominioSoggetto.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.eccezione.errore_applicativo.DominioSoggetto}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.eccezione.errore_applicativo.DominioSoggetto}
	 * @return Object type {@link org.openspcoop2.core.eccezione.errore_applicativo.DominioSoggetto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DominioSoggetto readDominioSoggetto(byte[] in) throws DeserializerException {
		return (DominioSoggetto) this.xmlToObj(in, DominioSoggetto.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.eccezione.errore_applicativo.DominioSoggetto}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.eccezione.errore_applicativo.DominioSoggetto}
	 * @return Object type {@link org.openspcoop2.core.eccezione.errore_applicativo.DominioSoggetto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DominioSoggetto readDominioSoggettoFromString(String in) throws DeserializerException {
		return (DominioSoggetto) this.xmlToObj(in.getBytes(), DominioSoggetto.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: soggetto-identificativo
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.eccezione.errore_applicativo.SoggettoIdentificativo}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.eccezione.errore_applicativo.SoggettoIdentificativo}
	 * @return Object type {@link org.openspcoop2.core.eccezione.errore_applicativo.SoggettoIdentificativo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SoggettoIdentificativo readSoggettoIdentificativo(String fileName) throws DeserializerException {
		return (SoggettoIdentificativo) this.xmlToObj(fileName, SoggettoIdentificativo.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.eccezione.errore_applicativo.SoggettoIdentificativo}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.eccezione.errore_applicativo.SoggettoIdentificativo}
	 * @return Object type {@link org.openspcoop2.core.eccezione.errore_applicativo.SoggettoIdentificativo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SoggettoIdentificativo readSoggettoIdentificativo(File file) throws DeserializerException {
		return (SoggettoIdentificativo) this.xmlToObj(file, SoggettoIdentificativo.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.eccezione.errore_applicativo.SoggettoIdentificativo}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.eccezione.errore_applicativo.SoggettoIdentificativo}
	 * @return Object type {@link org.openspcoop2.core.eccezione.errore_applicativo.SoggettoIdentificativo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SoggettoIdentificativo readSoggettoIdentificativo(InputStream in) throws DeserializerException {
		return (SoggettoIdentificativo) this.xmlToObj(in, SoggettoIdentificativo.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.eccezione.errore_applicativo.SoggettoIdentificativo}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.eccezione.errore_applicativo.SoggettoIdentificativo}
	 * @return Object type {@link org.openspcoop2.core.eccezione.errore_applicativo.SoggettoIdentificativo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SoggettoIdentificativo readSoggettoIdentificativo(byte[] in) throws DeserializerException {
		return (SoggettoIdentificativo) this.xmlToObj(in, SoggettoIdentificativo.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.eccezione.errore_applicativo.SoggettoIdentificativo}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.eccezione.errore_applicativo.SoggettoIdentificativo}
	 * @return Object type {@link org.openspcoop2.core.eccezione.errore_applicativo.SoggettoIdentificativo}
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
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.eccezione.errore_applicativo.Soggetto}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.eccezione.errore_applicativo.Soggetto}
	 * @return Object type {@link org.openspcoop2.core.eccezione.errore_applicativo.Soggetto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Soggetto readSoggetto(String fileName) throws DeserializerException {
		return (Soggetto) this.xmlToObj(fileName, Soggetto.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.eccezione.errore_applicativo.Soggetto}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.eccezione.errore_applicativo.Soggetto}
	 * @return Object type {@link org.openspcoop2.core.eccezione.errore_applicativo.Soggetto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Soggetto readSoggetto(File file) throws DeserializerException {
		return (Soggetto) this.xmlToObj(file, Soggetto.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.eccezione.errore_applicativo.Soggetto}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.eccezione.errore_applicativo.Soggetto}
	 * @return Object type {@link org.openspcoop2.core.eccezione.errore_applicativo.Soggetto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Soggetto readSoggetto(InputStream in) throws DeserializerException {
		return (Soggetto) this.xmlToObj(in, Soggetto.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.eccezione.errore_applicativo.Soggetto}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.eccezione.errore_applicativo.Soggetto}
	 * @return Object type {@link org.openspcoop2.core.eccezione.errore_applicativo.Soggetto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Soggetto readSoggetto(byte[] in) throws DeserializerException {
		return (Soggetto) this.xmlToObj(in, Soggetto.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.eccezione.errore_applicativo.Soggetto}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.eccezione.errore_applicativo.Soggetto}
	 * @return Object type {@link org.openspcoop2.core.eccezione.errore_applicativo.Soggetto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Soggetto readSoggettoFromString(String in) throws DeserializerException {
		return (Soggetto) this.xmlToObj(in.getBytes(), Soggetto.class);
	}	
	
	
	

}
