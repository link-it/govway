/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
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
package it.gov.spcoop.sica.manifest.utils.serializer;

import org.openspcoop2.generic_project.exception.DeserializerException;

import it.gov.spcoop.sica.manifest.DocumentoSicurezza;
import it.gov.spcoop.sica.manifest.ElencoServiziComponenti;
import it.gov.spcoop.sica.manifest.DocumentoSemiformale;
import it.gov.spcoop.sica.manifest.SpecificaSemiformale;
import it.gov.spcoop.sica.manifest.AccordoServizioParteSpecifica;
import it.gov.spcoop.sica.manifest.SpecificaPortiAccesso;
import it.gov.spcoop.sica.manifest.SpecificaSicurezza;
import it.gov.spcoop.sica.manifest.SpecificaLivelliServizio;
import it.gov.spcoop.sica.manifest.SpecificaInterfaccia;
import it.gov.spcoop.sica.manifest.AccordoServizioParteComune;
import it.gov.spcoop.sica.manifest.SpecificaConversazione;
import it.gov.spcoop.sica.manifest.DocumentoConversazione;
import it.gov.spcoop.sica.manifest.DocumentoInterfaccia;
import it.gov.spcoop.sica.manifest.ElencoPartecipanti;
import it.gov.spcoop.sica.manifest.ServizioComposto;
import it.gov.spcoop.sica.manifest.ElencoAllegati;
import it.gov.spcoop.sica.manifest.SpecificaCoordinamento;
import it.gov.spcoop.sica.manifest.DocumentoLivelloServizio;
import it.gov.spcoop.sica.manifest.AccordoCooperazione;
import it.gov.spcoop.sica.manifest.ElencoServiziComposti;
import it.gov.spcoop.sica.manifest.DocumentoCoordinamento;
import it.gov.spcoop.sica.manifest.AccordoServizio;

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
	 Object: DocumentoSicurezza
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link it.gov.spcoop.sica.manifest.DocumentoSicurezza}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.gov.spcoop.sica.manifest.DocumentoSicurezza}
	 * @return Object type {@link it.gov.spcoop.sica.manifest.DocumentoSicurezza}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DocumentoSicurezza readDocumentoSicurezza(String fileName) throws DeserializerException {
		return (DocumentoSicurezza) this.xmlToObj(fileName, DocumentoSicurezza.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.gov.spcoop.sica.manifest.DocumentoSicurezza}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.gov.spcoop.sica.manifest.DocumentoSicurezza}
	 * @return Object type {@link it.gov.spcoop.sica.manifest.DocumentoSicurezza}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DocumentoSicurezza readDocumentoSicurezza(File file) throws DeserializerException {
		return (DocumentoSicurezza) this.xmlToObj(file, DocumentoSicurezza.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.gov.spcoop.sica.manifest.DocumentoSicurezza}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.gov.spcoop.sica.manifest.DocumentoSicurezza}
	 * @return Object type {@link it.gov.spcoop.sica.manifest.DocumentoSicurezza}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DocumentoSicurezza readDocumentoSicurezza(InputStream in) throws DeserializerException {
		return (DocumentoSicurezza) this.xmlToObj(in, DocumentoSicurezza.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.gov.spcoop.sica.manifest.DocumentoSicurezza}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.gov.spcoop.sica.manifest.DocumentoSicurezza}
	 * @return Object type {@link it.gov.spcoop.sica.manifest.DocumentoSicurezza}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DocumentoSicurezza readDocumentoSicurezza(byte[] in) throws DeserializerException {
		return (DocumentoSicurezza) this.xmlToObj(in, DocumentoSicurezza.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.gov.spcoop.sica.manifest.DocumentoSicurezza}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.gov.spcoop.sica.manifest.DocumentoSicurezza}
	 * @return Object type {@link it.gov.spcoop.sica.manifest.DocumentoSicurezza}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DocumentoSicurezza readDocumentoSicurezzaFromString(String in) throws DeserializerException {
		return (DocumentoSicurezza) this.xmlToObj(in.getBytes(), DocumentoSicurezza.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: ElencoServiziComponenti
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link it.gov.spcoop.sica.manifest.ElencoServiziComponenti}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.gov.spcoop.sica.manifest.ElencoServiziComponenti}
	 * @return Object type {@link it.gov.spcoop.sica.manifest.ElencoServiziComponenti}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ElencoServiziComponenti readElencoServiziComponenti(String fileName) throws DeserializerException {
		return (ElencoServiziComponenti) this.xmlToObj(fileName, ElencoServiziComponenti.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.gov.spcoop.sica.manifest.ElencoServiziComponenti}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.gov.spcoop.sica.manifest.ElencoServiziComponenti}
	 * @return Object type {@link it.gov.spcoop.sica.manifest.ElencoServiziComponenti}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ElencoServiziComponenti readElencoServiziComponenti(File file) throws DeserializerException {
		return (ElencoServiziComponenti) this.xmlToObj(file, ElencoServiziComponenti.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.gov.spcoop.sica.manifest.ElencoServiziComponenti}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.gov.spcoop.sica.manifest.ElencoServiziComponenti}
	 * @return Object type {@link it.gov.spcoop.sica.manifest.ElencoServiziComponenti}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ElencoServiziComponenti readElencoServiziComponenti(InputStream in) throws DeserializerException {
		return (ElencoServiziComponenti) this.xmlToObj(in, ElencoServiziComponenti.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.gov.spcoop.sica.manifest.ElencoServiziComponenti}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.gov.spcoop.sica.manifest.ElencoServiziComponenti}
	 * @return Object type {@link it.gov.spcoop.sica.manifest.ElencoServiziComponenti}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ElencoServiziComponenti readElencoServiziComponenti(byte[] in) throws DeserializerException {
		return (ElencoServiziComponenti) this.xmlToObj(in, ElencoServiziComponenti.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.gov.spcoop.sica.manifest.ElencoServiziComponenti}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.gov.spcoop.sica.manifest.ElencoServiziComponenti}
	 * @return Object type {@link it.gov.spcoop.sica.manifest.ElencoServiziComponenti}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ElencoServiziComponenti readElencoServiziComponentiFromString(String in) throws DeserializerException {
		return (ElencoServiziComponenti) this.xmlToObj(in.getBytes(), ElencoServiziComponenti.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: DocumentoSemiformale
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link it.gov.spcoop.sica.manifest.DocumentoSemiformale}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.gov.spcoop.sica.manifest.DocumentoSemiformale}
	 * @return Object type {@link it.gov.spcoop.sica.manifest.DocumentoSemiformale}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DocumentoSemiformale readDocumentoSemiformale(String fileName) throws DeserializerException {
		return (DocumentoSemiformale) this.xmlToObj(fileName, DocumentoSemiformale.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.gov.spcoop.sica.manifest.DocumentoSemiformale}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.gov.spcoop.sica.manifest.DocumentoSemiformale}
	 * @return Object type {@link it.gov.spcoop.sica.manifest.DocumentoSemiformale}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DocumentoSemiformale readDocumentoSemiformale(File file) throws DeserializerException {
		return (DocumentoSemiformale) this.xmlToObj(file, DocumentoSemiformale.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.gov.spcoop.sica.manifest.DocumentoSemiformale}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.gov.spcoop.sica.manifest.DocumentoSemiformale}
	 * @return Object type {@link it.gov.spcoop.sica.manifest.DocumentoSemiformale}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DocumentoSemiformale readDocumentoSemiformale(InputStream in) throws DeserializerException {
		return (DocumentoSemiformale) this.xmlToObj(in, DocumentoSemiformale.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.gov.spcoop.sica.manifest.DocumentoSemiformale}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.gov.spcoop.sica.manifest.DocumentoSemiformale}
	 * @return Object type {@link it.gov.spcoop.sica.manifest.DocumentoSemiformale}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DocumentoSemiformale readDocumentoSemiformale(byte[] in) throws DeserializerException {
		return (DocumentoSemiformale) this.xmlToObj(in, DocumentoSemiformale.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.gov.spcoop.sica.manifest.DocumentoSemiformale}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.gov.spcoop.sica.manifest.DocumentoSemiformale}
	 * @return Object type {@link it.gov.spcoop.sica.manifest.DocumentoSemiformale}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DocumentoSemiformale readDocumentoSemiformaleFromString(String in) throws DeserializerException {
		return (DocumentoSemiformale) this.xmlToObj(in.getBytes(), DocumentoSemiformale.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: SpecificaSemiformale
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link it.gov.spcoop.sica.manifest.SpecificaSemiformale}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.gov.spcoop.sica.manifest.SpecificaSemiformale}
	 * @return Object type {@link it.gov.spcoop.sica.manifest.SpecificaSemiformale}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SpecificaSemiformale readSpecificaSemiformale(String fileName) throws DeserializerException {
		return (SpecificaSemiformale) this.xmlToObj(fileName, SpecificaSemiformale.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.gov.spcoop.sica.manifest.SpecificaSemiformale}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.gov.spcoop.sica.manifest.SpecificaSemiformale}
	 * @return Object type {@link it.gov.spcoop.sica.manifest.SpecificaSemiformale}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SpecificaSemiformale readSpecificaSemiformale(File file) throws DeserializerException {
		return (SpecificaSemiformale) this.xmlToObj(file, SpecificaSemiformale.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.gov.spcoop.sica.manifest.SpecificaSemiformale}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.gov.spcoop.sica.manifest.SpecificaSemiformale}
	 * @return Object type {@link it.gov.spcoop.sica.manifest.SpecificaSemiformale}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SpecificaSemiformale readSpecificaSemiformale(InputStream in) throws DeserializerException {
		return (SpecificaSemiformale) this.xmlToObj(in, SpecificaSemiformale.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.gov.spcoop.sica.manifest.SpecificaSemiformale}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.gov.spcoop.sica.manifest.SpecificaSemiformale}
	 * @return Object type {@link it.gov.spcoop.sica.manifest.SpecificaSemiformale}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SpecificaSemiformale readSpecificaSemiformale(byte[] in) throws DeserializerException {
		return (SpecificaSemiformale) this.xmlToObj(in, SpecificaSemiformale.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.gov.spcoop.sica.manifest.SpecificaSemiformale}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.gov.spcoop.sica.manifest.SpecificaSemiformale}
	 * @return Object type {@link it.gov.spcoop.sica.manifest.SpecificaSemiformale}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SpecificaSemiformale readSpecificaSemiformaleFromString(String in) throws DeserializerException {
		return (SpecificaSemiformale) this.xmlToObj(in.getBytes(), SpecificaSemiformale.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: accordoServizioParteSpecifica
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link it.gov.spcoop.sica.manifest.AccordoServizioParteSpecifica}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.gov.spcoop.sica.manifest.AccordoServizioParteSpecifica}
	 * @return Object type {@link it.gov.spcoop.sica.manifest.AccordoServizioParteSpecifica}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccordoServizioParteSpecifica readAccordoServizioParteSpecifica(String fileName) throws DeserializerException {
		return (AccordoServizioParteSpecifica) this.xmlToObj(fileName, AccordoServizioParteSpecifica.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.gov.spcoop.sica.manifest.AccordoServizioParteSpecifica}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.gov.spcoop.sica.manifest.AccordoServizioParteSpecifica}
	 * @return Object type {@link it.gov.spcoop.sica.manifest.AccordoServizioParteSpecifica}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccordoServizioParteSpecifica readAccordoServizioParteSpecifica(File file) throws DeserializerException {
		return (AccordoServizioParteSpecifica) this.xmlToObj(file, AccordoServizioParteSpecifica.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.gov.spcoop.sica.manifest.AccordoServizioParteSpecifica}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.gov.spcoop.sica.manifest.AccordoServizioParteSpecifica}
	 * @return Object type {@link it.gov.spcoop.sica.manifest.AccordoServizioParteSpecifica}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccordoServizioParteSpecifica readAccordoServizioParteSpecifica(InputStream in) throws DeserializerException {
		return (AccordoServizioParteSpecifica) this.xmlToObj(in, AccordoServizioParteSpecifica.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.gov.spcoop.sica.manifest.AccordoServizioParteSpecifica}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.gov.spcoop.sica.manifest.AccordoServizioParteSpecifica}
	 * @return Object type {@link it.gov.spcoop.sica.manifest.AccordoServizioParteSpecifica}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccordoServizioParteSpecifica readAccordoServizioParteSpecifica(byte[] in) throws DeserializerException {
		return (AccordoServizioParteSpecifica) this.xmlToObj(in, AccordoServizioParteSpecifica.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.gov.spcoop.sica.manifest.AccordoServizioParteSpecifica}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.gov.spcoop.sica.manifest.AccordoServizioParteSpecifica}
	 * @return Object type {@link it.gov.spcoop.sica.manifest.AccordoServizioParteSpecifica}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccordoServizioParteSpecifica readAccordoServizioParteSpecificaFromString(String in) throws DeserializerException {
		return (AccordoServizioParteSpecifica) this.xmlToObj(in.getBytes(), AccordoServizioParteSpecifica.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: SpecificaPortiAccesso
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link it.gov.spcoop.sica.manifest.SpecificaPortiAccesso}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.gov.spcoop.sica.manifest.SpecificaPortiAccesso}
	 * @return Object type {@link it.gov.spcoop.sica.manifest.SpecificaPortiAccesso}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SpecificaPortiAccesso readSpecificaPortiAccesso(String fileName) throws DeserializerException {
		return (SpecificaPortiAccesso) this.xmlToObj(fileName, SpecificaPortiAccesso.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.gov.spcoop.sica.manifest.SpecificaPortiAccesso}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.gov.spcoop.sica.manifest.SpecificaPortiAccesso}
	 * @return Object type {@link it.gov.spcoop.sica.manifest.SpecificaPortiAccesso}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SpecificaPortiAccesso readSpecificaPortiAccesso(File file) throws DeserializerException {
		return (SpecificaPortiAccesso) this.xmlToObj(file, SpecificaPortiAccesso.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.gov.spcoop.sica.manifest.SpecificaPortiAccesso}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.gov.spcoop.sica.manifest.SpecificaPortiAccesso}
	 * @return Object type {@link it.gov.spcoop.sica.manifest.SpecificaPortiAccesso}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SpecificaPortiAccesso readSpecificaPortiAccesso(InputStream in) throws DeserializerException {
		return (SpecificaPortiAccesso) this.xmlToObj(in, SpecificaPortiAccesso.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.gov.spcoop.sica.manifest.SpecificaPortiAccesso}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.gov.spcoop.sica.manifest.SpecificaPortiAccesso}
	 * @return Object type {@link it.gov.spcoop.sica.manifest.SpecificaPortiAccesso}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SpecificaPortiAccesso readSpecificaPortiAccesso(byte[] in) throws DeserializerException {
		return (SpecificaPortiAccesso) this.xmlToObj(in, SpecificaPortiAccesso.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.gov.spcoop.sica.manifest.SpecificaPortiAccesso}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.gov.spcoop.sica.manifest.SpecificaPortiAccesso}
	 * @return Object type {@link it.gov.spcoop.sica.manifest.SpecificaPortiAccesso}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SpecificaPortiAccesso readSpecificaPortiAccessoFromString(String in) throws DeserializerException {
		return (SpecificaPortiAccesso) this.xmlToObj(in.getBytes(), SpecificaPortiAccesso.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: SpecificaSicurezza
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link it.gov.spcoop.sica.manifest.SpecificaSicurezza}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.gov.spcoop.sica.manifest.SpecificaSicurezza}
	 * @return Object type {@link it.gov.spcoop.sica.manifest.SpecificaSicurezza}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SpecificaSicurezza readSpecificaSicurezza(String fileName) throws DeserializerException {
		return (SpecificaSicurezza) this.xmlToObj(fileName, SpecificaSicurezza.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.gov.spcoop.sica.manifest.SpecificaSicurezza}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.gov.spcoop.sica.manifest.SpecificaSicurezza}
	 * @return Object type {@link it.gov.spcoop.sica.manifest.SpecificaSicurezza}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SpecificaSicurezza readSpecificaSicurezza(File file) throws DeserializerException {
		return (SpecificaSicurezza) this.xmlToObj(file, SpecificaSicurezza.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.gov.spcoop.sica.manifest.SpecificaSicurezza}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.gov.spcoop.sica.manifest.SpecificaSicurezza}
	 * @return Object type {@link it.gov.spcoop.sica.manifest.SpecificaSicurezza}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SpecificaSicurezza readSpecificaSicurezza(InputStream in) throws DeserializerException {
		return (SpecificaSicurezza) this.xmlToObj(in, SpecificaSicurezza.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.gov.spcoop.sica.manifest.SpecificaSicurezza}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.gov.spcoop.sica.manifest.SpecificaSicurezza}
	 * @return Object type {@link it.gov.spcoop.sica.manifest.SpecificaSicurezza}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SpecificaSicurezza readSpecificaSicurezza(byte[] in) throws DeserializerException {
		return (SpecificaSicurezza) this.xmlToObj(in, SpecificaSicurezza.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.gov.spcoop.sica.manifest.SpecificaSicurezza}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.gov.spcoop.sica.manifest.SpecificaSicurezza}
	 * @return Object type {@link it.gov.spcoop.sica.manifest.SpecificaSicurezza}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SpecificaSicurezza readSpecificaSicurezzaFromString(String in) throws DeserializerException {
		return (SpecificaSicurezza) this.xmlToObj(in.getBytes(), SpecificaSicurezza.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: SpecificaLivelliServizio
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link it.gov.spcoop.sica.manifest.SpecificaLivelliServizio}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.gov.spcoop.sica.manifest.SpecificaLivelliServizio}
	 * @return Object type {@link it.gov.spcoop.sica.manifest.SpecificaLivelliServizio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SpecificaLivelliServizio readSpecificaLivelliServizio(String fileName) throws DeserializerException {
		return (SpecificaLivelliServizio) this.xmlToObj(fileName, SpecificaLivelliServizio.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.gov.spcoop.sica.manifest.SpecificaLivelliServizio}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.gov.spcoop.sica.manifest.SpecificaLivelliServizio}
	 * @return Object type {@link it.gov.spcoop.sica.manifest.SpecificaLivelliServizio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SpecificaLivelliServizio readSpecificaLivelliServizio(File file) throws DeserializerException {
		return (SpecificaLivelliServizio) this.xmlToObj(file, SpecificaLivelliServizio.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.gov.spcoop.sica.manifest.SpecificaLivelliServizio}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.gov.spcoop.sica.manifest.SpecificaLivelliServizio}
	 * @return Object type {@link it.gov.spcoop.sica.manifest.SpecificaLivelliServizio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SpecificaLivelliServizio readSpecificaLivelliServizio(InputStream in) throws DeserializerException {
		return (SpecificaLivelliServizio) this.xmlToObj(in, SpecificaLivelliServizio.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.gov.spcoop.sica.manifest.SpecificaLivelliServizio}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.gov.spcoop.sica.manifest.SpecificaLivelliServizio}
	 * @return Object type {@link it.gov.spcoop.sica.manifest.SpecificaLivelliServizio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SpecificaLivelliServizio readSpecificaLivelliServizio(byte[] in) throws DeserializerException {
		return (SpecificaLivelliServizio) this.xmlToObj(in, SpecificaLivelliServizio.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.gov.spcoop.sica.manifest.SpecificaLivelliServizio}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.gov.spcoop.sica.manifest.SpecificaLivelliServizio}
	 * @return Object type {@link it.gov.spcoop.sica.manifest.SpecificaLivelliServizio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SpecificaLivelliServizio readSpecificaLivelliServizioFromString(String in) throws DeserializerException {
		return (SpecificaLivelliServizio) this.xmlToObj(in.getBytes(), SpecificaLivelliServizio.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: SpecificaInterfaccia
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link it.gov.spcoop.sica.manifest.SpecificaInterfaccia}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.gov.spcoop.sica.manifest.SpecificaInterfaccia}
	 * @return Object type {@link it.gov.spcoop.sica.manifest.SpecificaInterfaccia}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SpecificaInterfaccia readSpecificaInterfaccia(String fileName) throws DeserializerException {
		return (SpecificaInterfaccia) this.xmlToObj(fileName, SpecificaInterfaccia.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.gov.spcoop.sica.manifest.SpecificaInterfaccia}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.gov.spcoop.sica.manifest.SpecificaInterfaccia}
	 * @return Object type {@link it.gov.spcoop.sica.manifest.SpecificaInterfaccia}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SpecificaInterfaccia readSpecificaInterfaccia(File file) throws DeserializerException {
		return (SpecificaInterfaccia) this.xmlToObj(file, SpecificaInterfaccia.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.gov.spcoop.sica.manifest.SpecificaInterfaccia}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.gov.spcoop.sica.manifest.SpecificaInterfaccia}
	 * @return Object type {@link it.gov.spcoop.sica.manifest.SpecificaInterfaccia}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SpecificaInterfaccia readSpecificaInterfaccia(InputStream in) throws DeserializerException {
		return (SpecificaInterfaccia) this.xmlToObj(in, SpecificaInterfaccia.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.gov.spcoop.sica.manifest.SpecificaInterfaccia}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.gov.spcoop.sica.manifest.SpecificaInterfaccia}
	 * @return Object type {@link it.gov.spcoop.sica.manifest.SpecificaInterfaccia}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SpecificaInterfaccia readSpecificaInterfaccia(byte[] in) throws DeserializerException {
		return (SpecificaInterfaccia) this.xmlToObj(in, SpecificaInterfaccia.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.gov.spcoop.sica.manifest.SpecificaInterfaccia}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.gov.spcoop.sica.manifest.SpecificaInterfaccia}
	 * @return Object type {@link it.gov.spcoop.sica.manifest.SpecificaInterfaccia}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SpecificaInterfaccia readSpecificaInterfacciaFromString(String in) throws DeserializerException {
		return (SpecificaInterfaccia) this.xmlToObj(in.getBytes(), SpecificaInterfaccia.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: accordoServizioParteComune
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link it.gov.spcoop.sica.manifest.AccordoServizioParteComune}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.gov.spcoop.sica.manifest.AccordoServizioParteComune}
	 * @return Object type {@link it.gov.spcoop.sica.manifest.AccordoServizioParteComune}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccordoServizioParteComune readAccordoServizioParteComune(String fileName) throws DeserializerException {
		return (AccordoServizioParteComune) this.xmlToObj(fileName, AccordoServizioParteComune.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.gov.spcoop.sica.manifest.AccordoServizioParteComune}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.gov.spcoop.sica.manifest.AccordoServizioParteComune}
	 * @return Object type {@link it.gov.spcoop.sica.manifest.AccordoServizioParteComune}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccordoServizioParteComune readAccordoServizioParteComune(File file) throws DeserializerException {
		return (AccordoServizioParteComune) this.xmlToObj(file, AccordoServizioParteComune.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.gov.spcoop.sica.manifest.AccordoServizioParteComune}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.gov.spcoop.sica.manifest.AccordoServizioParteComune}
	 * @return Object type {@link it.gov.spcoop.sica.manifest.AccordoServizioParteComune}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccordoServizioParteComune readAccordoServizioParteComune(InputStream in) throws DeserializerException {
		return (AccordoServizioParteComune) this.xmlToObj(in, AccordoServizioParteComune.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.gov.spcoop.sica.manifest.AccordoServizioParteComune}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.gov.spcoop.sica.manifest.AccordoServizioParteComune}
	 * @return Object type {@link it.gov.spcoop.sica.manifest.AccordoServizioParteComune}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccordoServizioParteComune readAccordoServizioParteComune(byte[] in) throws DeserializerException {
		return (AccordoServizioParteComune) this.xmlToObj(in, AccordoServizioParteComune.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.gov.spcoop.sica.manifest.AccordoServizioParteComune}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.gov.spcoop.sica.manifest.AccordoServizioParteComune}
	 * @return Object type {@link it.gov.spcoop.sica.manifest.AccordoServizioParteComune}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccordoServizioParteComune readAccordoServizioParteComuneFromString(String in) throws DeserializerException {
		return (AccordoServizioParteComune) this.xmlToObj(in.getBytes(), AccordoServizioParteComune.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: SpecificaConversazione
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link it.gov.spcoop.sica.manifest.SpecificaConversazione}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.gov.spcoop.sica.manifest.SpecificaConversazione}
	 * @return Object type {@link it.gov.spcoop.sica.manifest.SpecificaConversazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SpecificaConversazione readSpecificaConversazione(String fileName) throws DeserializerException {
		return (SpecificaConversazione) this.xmlToObj(fileName, SpecificaConversazione.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.gov.spcoop.sica.manifest.SpecificaConversazione}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.gov.spcoop.sica.manifest.SpecificaConversazione}
	 * @return Object type {@link it.gov.spcoop.sica.manifest.SpecificaConversazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SpecificaConversazione readSpecificaConversazione(File file) throws DeserializerException {
		return (SpecificaConversazione) this.xmlToObj(file, SpecificaConversazione.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.gov.spcoop.sica.manifest.SpecificaConversazione}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.gov.spcoop.sica.manifest.SpecificaConversazione}
	 * @return Object type {@link it.gov.spcoop.sica.manifest.SpecificaConversazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SpecificaConversazione readSpecificaConversazione(InputStream in) throws DeserializerException {
		return (SpecificaConversazione) this.xmlToObj(in, SpecificaConversazione.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.gov.spcoop.sica.manifest.SpecificaConversazione}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.gov.spcoop.sica.manifest.SpecificaConversazione}
	 * @return Object type {@link it.gov.spcoop.sica.manifest.SpecificaConversazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SpecificaConversazione readSpecificaConversazione(byte[] in) throws DeserializerException {
		return (SpecificaConversazione) this.xmlToObj(in, SpecificaConversazione.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.gov.spcoop.sica.manifest.SpecificaConversazione}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.gov.spcoop.sica.manifest.SpecificaConversazione}
	 * @return Object type {@link it.gov.spcoop.sica.manifest.SpecificaConversazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SpecificaConversazione readSpecificaConversazioneFromString(String in) throws DeserializerException {
		return (SpecificaConversazione) this.xmlToObj(in.getBytes(), SpecificaConversazione.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: DocumentoConversazione
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link it.gov.spcoop.sica.manifest.DocumentoConversazione}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.gov.spcoop.sica.manifest.DocumentoConversazione}
	 * @return Object type {@link it.gov.spcoop.sica.manifest.DocumentoConversazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DocumentoConversazione readDocumentoConversazione(String fileName) throws DeserializerException {
		return (DocumentoConversazione) this.xmlToObj(fileName, DocumentoConversazione.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.gov.spcoop.sica.manifest.DocumentoConversazione}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.gov.spcoop.sica.manifest.DocumentoConversazione}
	 * @return Object type {@link it.gov.spcoop.sica.manifest.DocumentoConversazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DocumentoConversazione readDocumentoConversazione(File file) throws DeserializerException {
		return (DocumentoConversazione) this.xmlToObj(file, DocumentoConversazione.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.gov.spcoop.sica.manifest.DocumentoConversazione}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.gov.spcoop.sica.manifest.DocumentoConversazione}
	 * @return Object type {@link it.gov.spcoop.sica.manifest.DocumentoConversazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DocumentoConversazione readDocumentoConversazione(InputStream in) throws DeserializerException {
		return (DocumentoConversazione) this.xmlToObj(in, DocumentoConversazione.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.gov.spcoop.sica.manifest.DocumentoConversazione}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.gov.spcoop.sica.manifest.DocumentoConversazione}
	 * @return Object type {@link it.gov.spcoop.sica.manifest.DocumentoConversazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DocumentoConversazione readDocumentoConversazione(byte[] in) throws DeserializerException {
		return (DocumentoConversazione) this.xmlToObj(in, DocumentoConversazione.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.gov.spcoop.sica.manifest.DocumentoConversazione}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.gov.spcoop.sica.manifest.DocumentoConversazione}
	 * @return Object type {@link it.gov.spcoop.sica.manifest.DocumentoConversazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DocumentoConversazione readDocumentoConversazioneFromString(String in) throws DeserializerException {
		return (DocumentoConversazione) this.xmlToObj(in.getBytes(), DocumentoConversazione.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: DocumentoInterfaccia
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link it.gov.spcoop.sica.manifest.DocumentoInterfaccia}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.gov.spcoop.sica.manifest.DocumentoInterfaccia}
	 * @return Object type {@link it.gov.spcoop.sica.manifest.DocumentoInterfaccia}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DocumentoInterfaccia readDocumentoInterfaccia(String fileName) throws DeserializerException {
		return (DocumentoInterfaccia) this.xmlToObj(fileName, DocumentoInterfaccia.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.gov.spcoop.sica.manifest.DocumentoInterfaccia}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.gov.spcoop.sica.manifest.DocumentoInterfaccia}
	 * @return Object type {@link it.gov.spcoop.sica.manifest.DocumentoInterfaccia}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DocumentoInterfaccia readDocumentoInterfaccia(File file) throws DeserializerException {
		return (DocumentoInterfaccia) this.xmlToObj(file, DocumentoInterfaccia.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.gov.spcoop.sica.manifest.DocumentoInterfaccia}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.gov.spcoop.sica.manifest.DocumentoInterfaccia}
	 * @return Object type {@link it.gov.spcoop.sica.manifest.DocumentoInterfaccia}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DocumentoInterfaccia readDocumentoInterfaccia(InputStream in) throws DeserializerException {
		return (DocumentoInterfaccia) this.xmlToObj(in, DocumentoInterfaccia.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.gov.spcoop.sica.manifest.DocumentoInterfaccia}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.gov.spcoop.sica.manifest.DocumentoInterfaccia}
	 * @return Object type {@link it.gov.spcoop.sica.manifest.DocumentoInterfaccia}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DocumentoInterfaccia readDocumentoInterfaccia(byte[] in) throws DeserializerException {
		return (DocumentoInterfaccia) this.xmlToObj(in, DocumentoInterfaccia.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.gov.spcoop.sica.manifest.DocumentoInterfaccia}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.gov.spcoop.sica.manifest.DocumentoInterfaccia}
	 * @return Object type {@link it.gov.spcoop.sica.manifest.DocumentoInterfaccia}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DocumentoInterfaccia readDocumentoInterfacciaFromString(String in) throws DeserializerException {
		return (DocumentoInterfaccia) this.xmlToObj(in.getBytes(), DocumentoInterfaccia.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: ElencoPartecipanti
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link it.gov.spcoop.sica.manifest.ElencoPartecipanti}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.gov.spcoop.sica.manifest.ElencoPartecipanti}
	 * @return Object type {@link it.gov.spcoop.sica.manifest.ElencoPartecipanti}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ElencoPartecipanti readElencoPartecipanti(String fileName) throws DeserializerException {
		return (ElencoPartecipanti) this.xmlToObj(fileName, ElencoPartecipanti.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.gov.spcoop.sica.manifest.ElencoPartecipanti}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.gov.spcoop.sica.manifest.ElencoPartecipanti}
	 * @return Object type {@link it.gov.spcoop.sica.manifest.ElencoPartecipanti}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ElencoPartecipanti readElencoPartecipanti(File file) throws DeserializerException {
		return (ElencoPartecipanti) this.xmlToObj(file, ElencoPartecipanti.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.gov.spcoop.sica.manifest.ElencoPartecipanti}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.gov.spcoop.sica.manifest.ElencoPartecipanti}
	 * @return Object type {@link it.gov.spcoop.sica.manifest.ElencoPartecipanti}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ElencoPartecipanti readElencoPartecipanti(InputStream in) throws DeserializerException {
		return (ElencoPartecipanti) this.xmlToObj(in, ElencoPartecipanti.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.gov.spcoop.sica.manifest.ElencoPartecipanti}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.gov.spcoop.sica.manifest.ElencoPartecipanti}
	 * @return Object type {@link it.gov.spcoop.sica.manifest.ElencoPartecipanti}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ElencoPartecipanti readElencoPartecipanti(byte[] in) throws DeserializerException {
		return (ElencoPartecipanti) this.xmlToObj(in, ElencoPartecipanti.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.gov.spcoop.sica.manifest.ElencoPartecipanti}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.gov.spcoop.sica.manifest.ElencoPartecipanti}
	 * @return Object type {@link it.gov.spcoop.sica.manifest.ElencoPartecipanti}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ElencoPartecipanti readElencoPartecipantiFromString(String in) throws DeserializerException {
		return (ElencoPartecipanti) this.xmlToObj(in.getBytes(), ElencoPartecipanti.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: servizioComposto
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link it.gov.spcoop.sica.manifest.ServizioComposto}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.gov.spcoop.sica.manifest.ServizioComposto}
	 * @return Object type {@link it.gov.spcoop.sica.manifest.ServizioComposto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ServizioComposto readServizioComposto(String fileName) throws DeserializerException {
		return (ServizioComposto) this.xmlToObj(fileName, ServizioComposto.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.gov.spcoop.sica.manifest.ServizioComposto}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.gov.spcoop.sica.manifest.ServizioComposto}
	 * @return Object type {@link it.gov.spcoop.sica.manifest.ServizioComposto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ServizioComposto readServizioComposto(File file) throws DeserializerException {
		return (ServizioComposto) this.xmlToObj(file, ServizioComposto.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.gov.spcoop.sica.manifest.ServizioComposto}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.gov.spcoop.sica.manifest.ServizioComposto}
	 * @return Object type {@link it.gov.spcoop.sica.manifest.ServizioComposto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ServizioComposto readServizioComposto(InputStream in) throws DeserializerException {
		return (ServizioComposto) this.xmlToObj(in, ServizioComposto.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.gov.spcoop.sica.manifest.ServizioComposto}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.gov.spcoop.sica.manifest.ServizioComposto}
	 * @return Object type {@link it.gov.spcoop.sica.manifest.ServizioComposto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ServizioComposto readServizioComposto(byte[] in) throws DeserializerException {
		return (ServizioComposto) this.xmlToObj(in, ServizioComposto.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.gov.spcoop.sica.manifest.ServizioComposto}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.gov.spcoop.sica.manifest.ServizioComposto}
	 * @return Object type {@link it.gov.spcoop.sica.manifest.ServizioComposto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ServizioComposto readServizioCompostoFromString(String in) throws DeserializerException {
		return (ServizioComposto) this.xmlToObj(in.getBytes(), ServizioComposto.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: ElencoAllegati
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link it.gov.spcoop.sica.manifest.ElencoAllegati}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.gov.spcoop.sica.manifest.ElencoAllegati}
	 * @return Object type {@link it.gov.spcoop.sica.manifest.ElencoAllegati}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ElencoAllegati readElencoAllegati(String fileName) throws DeserializerException {
		return (ElencoAllegati) this.xmlToObj(fileName, ElencoAllegati.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.gov.spcoop.sica.manifest.ElencoAllegati}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.gov.spcoop.sica.manifest.ElencoAllegati}
	 * @return Object type {@link it.gov.spcoop.sica.manifest.ElencoAllegati}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ElencoAllegati readElencoAllegati(File file) throws DeserializerException {
		return (ElencoAllegati) this.xmlToObj(file, ElencoAllegati.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.gov.spcoop.sica.manifest.ElencoAllegati}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.gov.spcoop.sica.manifest.ElencoAllegati}
	 * @return Object type {@link it.gov.spcoop.sica.manifest.ElencoAllegati}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ElencoAllegati readElencoAllegati(InputStream in) throws DeserializerException {
		return (ElencoAllegati) this.xmlToObj(in, ElencoAllegati.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.gov.spcoop.sica.manifest.ElencoAllegati}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.gov.spcoop.sica.manifest.ElencoAllegati}
	 * @return Object type {@link it.gov.spcoop.sica.manifest.ElencoAllegati}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ElencoAllegati readElencoAllegati(byte[] in) throws DeserializerException {
		return (ElencoAllegati) this.xmlToObj(in, ElencoAllegati.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.gov.spcoop.sica.manifest.ElencoAllegati}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.gov.spcoop.sica.manifest.ElencoAllegati}
	 * @return Object type {@link it.gov.spcoop.sica.manifest.ElencoAllegati}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ElencoAllegati readElencoAllegatiFromString(String in) throws DeserializerException {
		return (ElencoAllegati) this.xmlToObj(in.getBytes(), ElencoAllegati.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: SpecificaCoordinamento
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link it.gov.spcoop.sica.manifest.SpecificaCoordinamento}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.gov.spcoop.sica.manifest.SpecificaCoordinamento}
	 * @return Object type {@link it.gov.spcoop.sica.manifest.SpecificaCoordinamento}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SpecificaCoordinamento readSpecificaCoordinamento(String fileName) throws DeserializerException {
		return (SpecificaCoordinamento) this.xmlToObj(fileName, SpecificaCoordinamento.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.gov.spcoop.sica.manifest.SpecificaCoordinamento}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.gov.spcoop.sica.manifest.SpecificaCoordinamento}
	 * @return Object type {@link it.gov.spcoop.sica.manifest.SpecificaCoordinamento}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SpecificaCoordinamento readSpecificaCoordinamento(File file) throws DeserializerException {
		return (SpecificaCoordinamento) this.xmlToObj(file, SpecificaCoordinamento.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.gov.spcoop.sica.manifest.SpecificaCoordinamento}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.gov.spcoop.sica.manifest.SpecificaCoordinamento}
	 * @return Object type {@link it.gov.spcoop.sica.manifest.SpecificaCoordinamento}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SpecificaCoordinamento readSpecificaCoordinamento(InputStream in) throws DeserializerException {
		return (SpecificaCoordinamento) this.xmlToObj(in, SpecificaCoordinamento.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.gov.spcoop.sica.manifest.SpecificaCoordinamento}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.gov.spcoop.sica.manifest.SpecificaCoordinamento}
	 * @return Object type {@link it.gov.spcoop.sica.manifest.SpecificaCoordinamento}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SpecificaCoordinamento readSpecificaCoordinamento(byte[] in) throws DeserializerException {
		return (SpecificaCoordinamento) this.xmlToObj(in, SpecificaCoordinamento.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.gov.spcoop.sica.manifest.SpecificaCoordinamento}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.gov.spcoop.sica.manifest.SpecificaCoordinamento}
	 * @return Object type {@link it.gov.spcoop.sica.manifest.SpecificaCoordinamento}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SpecificaCoordinamento readSpecificaCoordinamentoFromString(String in) throws DeserializerException {
		return (SpecificaCoordinamento) this.xmlToObj(in.getBytes(), SpecificaCoordinamento.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: DocumentoLivelloServizio
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link it.gov.spcoop.sica.manifest.DocumentoLivelloServizio}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.gov.spcoop.sica.manifest.DocumentoLivelloServizio}
	 * @return Object type {@link it.gov.spcoop.sica.manifest.DocumentoLivelloServizio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DocumentoLivelloServizio readDocumentoLivelloServizio(String fileName) throws DeserializerException {
		return (DocumentoLivelloServizio) this.xmlToObj(fileName, DocumentoLivelloServizio.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.gov.spcoop.sica.manifest.DocumentoLivelloServizio}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.gov.spcoop.sica.manifest.DocumentoLivelloServizio}
	 * @return Object type {@link it.gov.spcoop.sica.manifest.DocumentoLivelloServizio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DocumentoLivelloServizio readDocumentoLivelloServizio(File file) throws DeserializerException {
		return (DocumentoLivelloServizio) this.xmlToObj(file, DocumentoLivelloServizio.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.gov.spcoop.sica.manifest.DocumentoLivelloServizio}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.gov.spcoop.sica.manifest.DocumentoLivelloServizio}
	 * @return Object type {@link it.gov.spcoop.sica.manifest.DocumentoLivelloServizio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DocumentoLivelloServizio readDocumentoLivelloServizio(InputStream in) throws DeserializerException {
		return (DocumentoLivelloServizio) this.xmlToObj(in, DocumentoLivelloServizio.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.gov.spcoop.sica.manifest.DocumentoLivelloServizio}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.gov.spcoop.sica.manifest.DocumentoLivelloServizio}
	 * @return Object type {@link it.gov.spcoop.sica.manifest.DocumentoLivelloServizio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DocumentoLivelloServizio readDocumentoLivelloServizio(byte[] in) throws DeserializerException {
		return (DocumentoLivelloServizio) this.xmlToObj(in, DocumentoLivelloServizio.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.gov.spcoop.sica.manifest.DocumentoLivelloServizio}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.gov.spcoop.sica.manifest.DocumentoLivelloServizio}
	 * @return Object type {@link it.gov.spcoop.sica.manifest.DocumentoLivelloServizio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DocumentoLivelloServizio readDocumentoLivelloServizioFromString(String in) throws DeserializerException {
		return (DocumentoLivelloServizio) this.xmlToObj(in.getBytes(), DocumentoLivelloServizio.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: accordoCooperazione
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link it.gov.spcoop.sica.manifest.AccordoCooperazione}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.gov.spcoop.sica.manifest.AccordoCooperazione}
	 * @return Object type {@link it.gov.spcoop.sica.manifest.AccordoCooperazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccordoCooperazione readAccordoCooperazione(String fileName) throws DeserializerException {
		return (AccordoCooperazione) this.xmlToObj(fileName, AccordoCooperazione.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.gov.spcoop.sica.manifest.AccordoCooperazione}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.gov.spcoop.sica.manifest.AccordoCooperazione}
	 * @return Object type {@link it.gov.spcoop.sica.manifest.AccordoCooperazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccordoCooperazione readAccordoCooperazione(File file) throws DeserializerException {
		return (AccordoCooperazione) this.xmlToObj(file, AccordoCooperazione.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.gov.spcoop.sica.manifest.AccordoCooperazione}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.gov.spcoop.sica.manifest.AccordoCooperazione}
	 * @return Object type {@link it.gov.spcoop.sica.manifest.AccordoCooperazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccordoCooperazione readAccordoCooperazione(InputStream in) throws DeserializerException {
		return (AccordoCooperazione) this.xmlToObj(in, AccordoCooperazione.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.gov.spcoop.sica.manifest.AccordoCooperazione}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.gov.spcoop.sica.manifest.AccordoCooperazione}
	 * @return Object type {@link it.gov.spcoop.sica.manifest.AccordoCooperazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccordoCooperazione readAccordoCooperazione(byte[] in) throws DeserializerException {
		return (AccordoCooperazione) this.xmlToObj(in, AccordoCooperazione.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.gov.spcoop.sica.manifest.AccordoCooperazione}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.gov.spcoop.sica.manifest.AccordoCooperazione}
	 * @return Object type {@link it.gov.spcoop.sica.manifest.AccordoCooperazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccordoCooperazione readAccordoCooperazioneFromString(String in) throws DeserializerException {
		return (AccordoCooperazione) this.xmlToObj(in.getBytes(), AccordoCooperazione.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: ElencoServiziComposti
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link it.gov.spcoop.sica.manifest.ElencoServiziComposti}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.gov.spcoop.sica.manifest.ElencoServiziComposti}
	 * @return Object type {@link it.gov.spcoop.sica.manifest.ElencoServiziComposti}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ElencoServiziComposti readElencoServiziComposti(String fileName) throws DeserializerException {
		return (ElencoServiziComposti) this.xmlToObj(fileName, ElencoServiziComposti.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.gov.spcoop.sica.manifest.ElencoServiziComposti}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.gov.spcoop.sica.manifest.ElencoServiziComposti}
	 * @return Object type {@link it.gov.spcoop.sica.manifest.ElencoServiziComposti}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ElencoServiziComposti readElencoServiziComposti(File file) throws DeserializerException {
		return (ElencoServiziComposti) this.xmlToObj(file, ElencoServiziComposti.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.gov.spcoop.sica.manifest.ElencoServiziComposti}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.gov.spcoop.sica.manifest.ElencoServiziComposti}
	 * @return Object type {@link it.gov.spcoop.sica.manifest.ElencoServiziComposti}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ElencoServiziComposti readElencoServiziComposti(InputStream in) throws DeserializerException {
		return (ElencoServiziComposti) this.xmlToObj(in, ElencoServiziComposti.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.gov.spcoop.sica.manifest.ElencoServiziComposti}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.gov.spcoop.sica.manifest.ElencoServiziComposti}
	 * @return Object type {@link it.gov.spcoop.sica.manifest.ElencoServiziComposti}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ElencoServiziComposti readElencoServiziComposti(byte[] in) throws DeserializerException {
		return (ElencoServiziComposti) this.xmlToObj(in, ElencoServiziComposti.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.gov.spcoop.sica.manifest.ElencoServiziComposti}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.gov.spcoop.sica.manifest.ElencoServiziComposti}
	 * @return Object type {@link it.gov.spcoop.sica.manifest.ElencoServiziComposti}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ElencoServiziComposti readElencoServiziCompostiFromString(String in) throws DeserializerException {
		return (ElencoServiziComposti) this.xmlToObj(in.getBytes(), ElencoServiziComposti.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: DocumentoCoordinamento
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link it.gov.spcoop.sica.manifest.DocumentoCoordinamento}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.gov.spcoop.sica.manifest.DocumentoCoordinamento}
	 * @return Object type {@link it.gov.spcoop.sica.manifest.DocumentoCoordinamento}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DocumentoCoordinamento readDocumentoCoordinamento(String fileName) throws DeserializerException {
		return (DocumentoCoordinamento) this.xmlToObj(fileName, DocumentoCoordinamento.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.gov.spcoop.sica.manifest.DocumentoCoordinamento}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.gov.spcoop.sica.manifest.DocumentoCoordinamento}
	 * @return Object type {@link it.gov.spcoop.sica.manifest.DocumentoCoordinamento}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DocumentoCoordinamento readDocumentoCoordinamento(File file) throws DeserializerException {
		return (DocumentoCoordinamento) this.xmlToObj(file, DocumentoCoordinamento.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.gov.spcoop.sica.manifest.DocumentoCoordinamento}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.gov.spcoop.sica.manifest.DocumentoCoordinamento}
	 * @return Object type {@link it.gov.spcoop.sica.manifest.DocumentoCoordinamento}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DocumentoCoordinamento readDocumentoCoordinamento(InputStream in) throws DeserializerException {
		return (DocumentoCoordinamento) this.xmlToObj(in, DocumentoCoordinamento.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.gov.spcoop.sica.manifest.DocumentoCoordinamento}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.gov.spcoop.sica.manifest.DocumentoCoordinamento}
	 * @return Object type {@link it.gov.spcoop.sica.manifest.DocumentoCoordinamento}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DocumentoCoordinamento readDocumentoCoordinamento(byte[] in) throws DeserializerException {
		return (DocumentoCoordinamento) this.xmlToObj(in, DocumentoCoordinamento.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.gov.spcoop.sica.manifest.DocumentoCoordinamento}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.gov.spcoop.sica.manifest.DocumentoCoordinamento}
	 * @return Object type {@link it.gov.spcoop.sica.manifest.DocumentoCoordinamento}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DocumentoCoordinamento readDocumentoCoordinamentoFromString(String in) throws DeserializerException {
		return (DocumentoCoordinamento) this.xmlToObj(in.getBytes(), DocumentoCoordinamento.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: accordoServizio
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link it.gov.spcoop.sica.manifest.AccordoServizio}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link it.gov.spcoop.sica.manifest.AccordoServizio}
	 * @return Object type {@link it.gov.spcoop.sica.manifest.AccordoServizio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccordoServizio readAccordoServizio(String fileName) throws DeserializerException {
		return (AccordoServizio) this.xmlToObj(fileName, AccordoServizio.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link it.gov.spcoop.sica.manifest.AccordoServizio}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link it.gov.spcoop.sica.manifest.AccordoServizio}
	 * @return Object type {@link it.gov.spcoop.sica.manifest.AccordoServizio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccordoServizio readAccordoServizio(File file) throws DeserializerException {
		return (AccordoServizio) this.xmlToObj(file, AccordoServizio.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link it.gov.spcoop.sica.manifest.AccordoServizio}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link it.gov.spcoop.sica.manifest.AccordoServizio}
	 * @return Object type {@link it.gov.spcoop.sica.manifest.AccordoServizio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccordoServizio readAccordoServizio(InputStream in) throws DeserializerException {
		return (AccordoServizio) this.xmlToObj(in, AccordoServizio.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link it.gov.spcoop.sica.manifest.AccordoServizio}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link it.gov.spcoop.sica.manifest.AccordoServizio}
	 * @return Object type {@link it.gov.spcoop.sica.manifest.AccordoServizio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccordoServizio readAccordoServizio(byte[] in) throws DeserializerException {
		return (AccordoServizio) this.xmlToObj(in, AccordoServizio.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link it.gov.spcoop.sica.manifest.AccordoServizio}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link it.gov.spcoop.sica.manifest.AccordoServizio}
	 * @return Object type {@link it.gov.spcoop.sica.manifest.AccordoServizio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccordoServizio readAccordoServizioFromString(String in) throws DeserializerException {
		return (AccordoServizio) this.xmlToObj(in.getBytes(), AccordoServizio.class);
	}	
	
	
	

}
