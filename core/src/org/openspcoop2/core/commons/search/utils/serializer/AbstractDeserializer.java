/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
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
package org.openspcoop2.core.commons.search.utils.serializer;

import org.openspcoop2.generic_project.exception.DeserializerException;

import org.openspcoop2.core.commons.search.Operation;
import org.openspcoop2.core.commons.search.IdPortType;
import org.openspcoop2.core.commons.search.PortaApplicativa;
import org.openspcoop2.core.commons.search.IdSoggetto;
import org.openspcoop2.core.commons.search.PortaApplicativaServizioApplicativo;
import org.openspcoop2.core.commons.search.PortaApplicativaAzione;
import org.openspcoop2.core.commons.search.Resource;
import org.openspcoop2.core.commons.search.IdAccordoServizioParteComune;
import org.openspcoop2.core.commons.search.AccordoServizioParteComuneAzione;
import org.openspcoop2.core.commons.search.IdRuolo;
import org.openspcoop2.core.commons.search.IdPortaDominio;
import org.openspcoop2.core.commons.search.IdAccordoServizioParteSpecifica;
import org.openspcoop2.core.commons.search.ServizioApplicativoRuolo;
import org.openspcoop2.core.commons.search.IdAccordoServizioParteComuneAzione;
import org.openspcoop2.core.commons.search.AccordoServizioParteSpecifica;
import org.openspcoop2.core.commons.search.ServizioApplicativoProprietaProtocollo;
import org.openspcoop2.core.commons.search.Ruolo;
import org.openspcoop2.core.commons.search.SoggettoRuolo;
import org.openspcoop2.core.commons.search.ServizioApplicativo;
import org.openspcoop2.core.commons.search.IdGruppo;
import org.openspcoop2.core.commons.search.AccordoServizioParteComuneGruppo;
import org.openspcoop2.core.commons.search.IdServizioApplicativo;
import org.openspcoop2.core.commons.search.PortType;
import org.openspcoop2.core.commons.search.IdPortaDelegata;
import org.openspcoop2.core.commons.search.IdAccordoServizioParteComuneGruppo;
import org.openspcoop2.core.commons.search.PortaDelegataAzione;
import org.openspcoop2.core.commons.search.IdFruitore;
import org.openspcoop2.core.commons.search.IdOperation;
import org.openspcoop2.core.commons.search.Soggetto;
import org.openspcoop2.core.commons.search.Gruppo;
import org.openspcoop2.core.commons.search.AccordoServizioParteComune;
import org.openspcoop2.core.commons.search.PortaDominio;
import org.openspcoop2.core.commons.search.IdPortaApplicativa;
import org.openspcoop2.core.commons.search.IdResource;
import org.openspcoop2.core.commons.search.PortaDelegataServizioApplicativo;
import org.openspcoop2.core.commons.search.Fruitore;
import org.openspcoop2.core.commons.search.PortaDelegata;
import org.openspcoop2.core.commons.search.IdSoggettoRuolo;

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
	 Object: operation
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.commons.search.Operation}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.Operation}
	 * @return Object type {@link org.openspcoop2.core.commons.search.Operation}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Operation readOperation(String fileName) throws DeserializerException {
		return (Operation) this.xmlToObj(fileName, Operation.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.commons.search.Operation}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.Operation}
	 * @return Object type {@link org.openspcoop2.core.commons.search.Operation}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Operation readOperation(File file) throws DeserializerException {
		return (Operation) this.xmlToObj(file, Operation.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.commons.search.Operation}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.Operation}
	 * @return Object type {@link org.openspcoop2.core.commons.search.Operation}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Operation readOperation(InputStream in) throws DeserializerException {
		return (Operation) this.xmlToObj(in, Operation.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.commons.search.Operation}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.Operation}
	 * @return Object type {@link org.openspcoop2.core.commons.search.Operation}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Operation readOperation(byte[] in) throws DeserializerException {
		return (Operation) this.xmlToObj(in, Operation.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.commons.search.Operation}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.Operation}
	 * @return Object type {@link org.openspcoop2.core.commons.search.Operation}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Operation readOperationFromString(String in) throws DeserializerException {
		return (Operation) this.xmlToObj(in.getBytes(), Operation.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: id-port-type
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.commons.search.IdPortType}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.IdPortType}
	 * @return Object type {@link org.openspcoop2.core.commons.search.IdPortType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdPortType readIdPortType(String fileName) throws DeserializerException {
		return (IdPortType) this.xmlToObj(fileName, IdPortType.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.commons.search.IdPortType}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.IdPortType}
	 * @return Object type {@link org.openspcoop2.core.commons.search.IdPortType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdPortType readIdPortType(File file) throws DeserializerException {
		return (IdPortType) this.xmlToObj(file, IdPortType.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.commons.search.IdPortType}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.IdPortType}
	 * @return Object type {@link org.openspcoop2.core.commons.search.IdPortType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdPortType readIdPortType(InputStream in) throws DeserializerException {
		return (IdPortType) this.xmlToObj(in, IdPortType.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.commons.search.IdPortType}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.IdPortType}
	 * @return Object type {@link org.openspcoop2.core.commons.search.IdPortType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdPortType readIdPortType(byte[] in) throws DeserializerException {
		return (IdPortType) this.xmlToObj(in, IdPortType.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.commons.search.IdPortType}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.IdPortType}
	 * @return Object type {@link org.openspcoop2.core.commons.search.IdPortType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdPortType readIdPortTypeFromString(String in) throws DeserializerException {
		return (IdPortType) this.xmlToObj(in.getBytes(), IdPortType.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: porta-applicativa
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.commons.search.PortaApplicativa}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.PortaApplicativa}
	 * @return Object type {@link org.openspcoop2.core.commons.search.PortaApplicativa}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PortaApplicativa readPortaApplicativa(String fileName) throws DeserializerException {
		return (PortaApplicativa) this.xmlToObj(fileName, PortaApplicativa.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.commons.search.PortaApplicativa}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.PortaApplicativa}
	 * @return Object type {@link org.openspcoop2.core.commons.search.PortaApplicativa}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PortaApplicativa readPortaApplicativa(File file) throws DeserializerException {
		return (PortaApplicativa) this.xmlToObj(file, PortaApplicativa.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.commons.search.PortaApplicativa}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.PortaApplicativa}
	 * @return Object type {@link org.openspcoop2.core.commons.search.PortaApplicativa}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PortaApplicativa readPortaApplicativa(InputStream in) throws DeserializerException {
		return (PortaApplicativa) this.xmlToObj(in, PortaApplicativa.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.commons.search.PortaApplicativa}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.PortaApplicativa}
	 * @return Object type {@link org.openspcoop2.core.commons.search.PortaApplicativa}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PortaApplicativa readPortaApplicativa(byte[] in) throws DeserializerException {
		return (PortaApplicativa) this.xmlToObj(in, PortaApplicativa.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.commons.search.PortaApplicativa}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.PortaApplicativa}
	 * @return Object type {@link org.openspcoop2.core.commons.search.PortaApplicativa}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PortaApplicativa readPortaApplicativaFromString(String in) throws DeserializerException {
		return (PortaApplicativa) this.xmlToObj(in.getBytes(), PortaApplicativa.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: id-soggetto
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.commons.search.IdSoggetto}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.IdSoggetto}
	 * @return Object type {@link org.openspcoop2.core.commons.search.IdSoggetto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdSoggetto readIdSoggetto(String fileName) throws DeserializerException {
		return (IdSoggetto) this.xmlToObj(fileName, IdSoggetto.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.commons.search.IdSoggetto}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.IdSoggetto}
	 * @return Object type {@link org.openspcoop2.core.commons.search.IdSoggetto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdSoggetto readIdSoggetto(File file) throws DeserializerException {
		return (IdSoggetto) this.xmlToObj(file, IdSoggetto.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.commons.search.IdSoggetto}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.IdSoggetto}
	 * @return Object type {@link org.openspcoop2.core.commons.search.IdSoggetto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdSoggetto readIdSoggetto(InputStream in) throws DeserializerException {
		return (IdSoggetto) this.xmlToObj(in, IdSoggetto.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.commons.search.IdSoggetto}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.IdSoggetto}
	 * @return Object type {@link org.openspcoop2.core.commons.search.IdSoggetto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdSoggetto readIdSoggetto(byte[] in) throws DeserializerException {
		return (IdSoggetto) this.xmlToObj(in, IdSoggetto.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.commons.search.IdSoggetto}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.IdSoggetto}
	 * @return Object type {@link org.openspcoop2.core.commons.search.IdSoggetto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdSoggetto readIdSoggettoFromString(String in) throws DeserializerException {
		return (IdSoggetto) this.xmlToObj(in.getBytes(), IdSoggetto.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: porta-applicativa-servizio-applicativo
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.commons.search.PortaApplicativaServizioApplicativo}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.PortaApplicativaServizioApplicativo}
	 * @return Object type {@link org.openspcoop2.core.commons.search.PortaApplicativaServizioApplicativo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PortaApplicativaServizioApplicativo readPortaApplicativaServizioApplicativo(String fileName) throws DeserializerException {
		return (PortaApplicativaServizioApplicativo) this.xmlToObj(fileName, PortaApplicativaServizioApplicativo.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.commons.search.PortaApplicativaServizioApplicativo}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.PortaApplicativaServizioApplicativo}
	 * @return Object type {@link org.openspcoop2.core.commons.search.PortaApplicativaServizioApplicativo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PortaApplicativaServizioApplicativo readPortaApplicativaServizioApplicativo(File file) throws DeserializerException {
		return (PortaApplicativaServizioApplicativo) this.xmlToObj(file, PortaApplicativaServizioApplicativo.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.commons.search.PortaApplicativaServizioApplicativo}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.PortaApplicativaServizioApplicativo}
	 * @return Object type {@link org.openspcoop2.core.commons.search.PortaApplicativaServizioApplicativo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PortaApplicativaServizioApplicativo readPortaApplicativaServizioApplicativo(InputStream in) throws DeserializerException {
		return (PortaApplicativaServizioApplicativo) this.xmlToObj(in, PortaApplicativaServizioApplicativo.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.commons.search.PortaApplicativaServizioApplicativo}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.PortaApplicativaServizioApplicativo}
	 * @return Object type {@link org.openspcoop2.core.commons.search.PortaApplicativaServizioApplicativo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PortaApplicativaServizioApplicativo readPortaApplicativaServizioApplicativo(byte[] in) throws DeserializerException {
		return (PortaApplicativaServizioApplicativo) this.xmlToObj(in, PortaApplicativaServizioApplicativo.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.commons.search.PortaApplicativaServizioApplicativo}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.PortaApplicativaServizioApplicativo}
	 * @return Object type {@link org.openspcoop2.core.commons.search.PortaApplicativaServizioApplicativo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PortaApplicativaServizioApplicativo readPortaApplicativaServizioApplicativoFromString(String in) throws DeserializerException {
		return (PortaApplicativaServizioApplicativo) this.xmlToObj(in.getBytes(), PortaApplicativaServizioApplicativo.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: porta-applicativa-azione
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.commons.search.PortaApplicativaAzione}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.PortaApplicativaAzione}
	 * @return Object type {@link org.openspcoop2.core.commons.search.PortaApplicativaAzione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PortaApplicativaAzione readPortaApplicativaAzione(String fileName) throws DeserializerException {
		return (PortaApplicativaAzione) this.xmlToObj(fileName, PortaApplicativaAzione.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.commons.search.PortaApplicativaAzione}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.PortaApplicativaAzione}
	 * @return Object type {@link org.openspcoop2.core.commons.search.PortaApplicativaAzione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PortaApplicativaAzione readPortaApplicativaAzione(File file) throws DeserializerException {
		return (PortaApplicativaAzione) this.xmlToObj(file, PortaApplicativaAzione.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.commons.search.PortaApplicativaAzione}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.PortaApplicativaAzione}
	 * @return Object type {@link org.openspcoop2.core.commons.search.PortaApplicativaAzione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PortaApplicativaAzione readPortaApplicativaAzione(InputStream in) throws DeserializerException {
		return (PortaApplicativaAzione) this.xmlToObj(in, PortaApplicativaAzione.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.commons.search.PortaApplicativaAzione}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.PortaApplicativaAzione}
	 * @return Object type {@link org.openspcoop2.core.commons.search.PortaApplicativaAzione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PortaApplicativaAzione readPortaApplicativaAzione(byte[] in) throws DeserializerException {
		return (PortaApplicativaAzione) this.xmlToObj(in, PortaApplicativaAzione.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.commons.search.PortaApplicativaAzione}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.PortaApplicativaAzione}
	 * @return Object type {@link org.openspcoop2.core.commons.search.PortaApplicativaAzione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PortaApplicativaAzione readPortaApplicativaAzioneFromString(String in) throws DeserializerException {
		return (PortaApplicativaAzione) this.xmlToObj(in.getBytes(), PortaApplicativaAzione.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: resource
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.commons.search.Resource}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.Resource}
	 * @return Object type {@link org.openspcoop2.core.commons.search.Resource}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Resource readResource(String fileName) throws DeserializerException {
		return (Resource) this.xmlToObj(fileName, Resource.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.commons.search.Resource}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.Resource}
	 * @return Object type {@link org.openspcoop2.core.commons.search.Resource}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Resource readResource(File file) throws DeserializerException {
		return (Resource) this.xmlToObj(file, Resource.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.commons.search.Resource}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.Resource}
	 * @return Object type {@link org.openspcoop2.core.commons.search.Resource}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Resource readResource(InputStream in) throws DeserializerException {
		return (Resource) this.xmlToObj(in, Resource.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.commons.search.Resource}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.Resource}
	 * @return Object type {@link org.openspcoop2.core.commons.search.Resource}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Resource readResource(byte[] in) throws DeserializerException {
		return (Resource) this.xmlToObj(in, Resource.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.commons.search.Resource}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.Resource}
	 * @return Object type {@link org.openspcoop2.core.commons.search.Resource}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Resource readResourceFromString(String in) throws DeserializerException {
		return (Resource) this.xmlToObj(in.getBytes(), Resource.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: id-accordo-servizio-parte-comune
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.commons.search.IdAccordoServizioParteComune}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.IdAccordoServizioParteComune}
	 * @return Object type {@link org.openspcoop2.core.commons.search.IdAccordoServizioParteComune}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdAccordoServizioParteComune readIdAccordoServizioParteComune(String fileName) throws DeserializerException {
		return (IdAccordoServizioParteComune) this.xmlToObj(fileName, IdAccordoServizioParteComune.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.commons.search.IdAccordoServizioParteComune}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.IdAccordoServizioParteComune}
	 * @return Object type {@link org.openspcoop2.core.commons.search.IdAccordoServizioParteComune}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdAccordoServizioParteComune readIdAccordoServizioParteComune(File file) throws DeserializerException {
		return (IdAccordoServizioParteComune) this.xmlToObj(file, IdAccordoServizioParteComune.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.commons.search.IdAccordoServizioParteComune}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.IdAccordoServizioParteComune}
	 * @return Object type {@link org.openspcoop2.core.commons.search.IdAccordoServizioParteComune}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdAccordoServizioParteComune readIdAccordoServizioParteComune(InputStream in) throws DeserializerException {
		return (IdAccordoServizioParteComune) this.xmlToObj(in, IdAccordoServizioParteComune.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.commons.search.IdAccordoServizioParteComune}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.IdAccordoServizioParteComune}
	 * @return Object type {@link org.openspcoop2.core.commons.search.IdAccordoServizioParteComune}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdAccordoServizioParteComune readIdAccordoServizioParteComune(byte[] in) throws DeserializerException {
		return (IdAccordoServizioParteComune) this.xmlToObj(in, IdAccordoServizioParteComune.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.commons.search.IdAccordoServizioParteComune}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.IdAccordoServizioParteComune}
	 * @return Object type {@link org.openspcoop2.core.commons.search.IdAccordoServizioParteComune}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdAccordoServizioParteComune readIdAccordoServizioParteComuneFromString(String in) throws DeserializerException {
		return (IdAccordoServizioParteComune) this.xmlToObj(in.getBytes(), IdAccordoServizioParteComune.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: accordo-servizio-parte-comune-azione
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.commons.search.AccordoServizioParteComuneAzione}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.AccordoServizioParteComuneAzione}
	 * @return Object type {@link org.openspcoop2.core.commons.search.AccordoServizioParteComuneAzione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccordoServizioParteComuneAzione readAccordoServizioParteComuneAzione(String fileName) throws DeserializerException {
		return (AccordoServizioParteComuneAzione) this.xmlToObj(fileName, AccordoServizioParteComuneAzione.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.commons.search.AccordoServizioParteComuneAzione}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.AccordoServizioParteComuneAzione}
	 * @return Object type {@link org.openspcoop2.core.commons.search.AccordoServizioParteComuneAzione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccordoServizioParteComuneAzione readAccordoServizioParteComuneAzione(File file) throws DeserializerException {
		return (AccordoServizioParteComuneAzione) this.xmlToObj(file, AccordoServizioParteComuneAzione.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.commons.search.AccordoServizioParteComuneAzione}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.AccordoServizioParteComuneAzione}
	 * @return Object type {@link org.openspcoop2.core.commons.search.AccordoServizioParteComuneAzione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccordoServizioParteComuneAzione readAccordoServizioParteComuneAzione(InputStream in) throws DeserializerException {
		return (AccordoServizioParteComuneAzione) this.xmlToObj(in, AccordoServizioParteComuneAzione.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.commons.search.AccordoServizioParteComuneAzione}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.AccordoServizioParteComuneAzione}
	 * @return Object type {@link org.openspcoop2.core.commons.search.AccordoServizioParteComuneAzione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccordoServizioParteComuneAzione readAccordoServizioParteComuneAzione(byte[] in) throws DeserializerException {
		return (AccordoServizioParteComuneAzione) this.xmlToObj(in, AccordoServizioParteComuneAzione.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.commons.search.AccordoServizioParteComuneAzione}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.AccordoServizioParteComuneAzione}
	 * @return Object type {@link org.openspcoop2.core.commons.search.AccordoServizioParteComuneAzione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccordoServizioParteComuneAzione readAccordoServizioParteComuneAzioneFromString(String in) throws DeserializerException {
		return (AccordoServizioParteComuneAzione) this.xmlToObj(in.getBytes(), AccordoServizioParteComuneAzione.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: id-ruolo
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.commons.search.IdRuolo}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.IdRuolo}
	 * @return Object type {@link org.openspcoop2.core.commons.search.IdRuolo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdRuolo readIdRuolo(String fileName) throws DeserializerException {
		return (IdRuolo) this.xmlToObj(fileName, IdRuolo.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.commons.search.IdRuolo}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.IdRuolo}
	 * @return Object type {@link org.openspcoop2.core.commons.search.IdRuolo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdRuolo readIdRuolo(File file) throws DeserializerException {
		return (IdRuolo) this.xmlToObj(file, IdRuolo.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.commons.search.IdRuolo}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.IdRuolo}
	 * @return Object type {@link org.openspcoop2.core.commons.search.IdRuolo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdRuolo readIdRuolo(InputStream in) throws DeserializerException {
		return (IdRuolo) this.xmlToObj(in, IdRuolo.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.commons.search.IdRuolo}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.IdRuolo}
	 * @return Object type {@link org.openspcoop2.core.commons.search.IdRuolo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdRuolo readIdRuolo(byte[] in) throws DeserializerException {
		return (IdRuolo) this.xmlToObj(in, IdRuolo.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.commons.search.IdRuolo}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.IdRuolo}
	 * @return Object type {@link org.openspcoop2.core.commons.search.IdRuolo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdRuolo readIdRuoloFromString(String in) throws DeserializerException {
		return (IdRuolo) this.xmlToObj(in.getBytes(), IdRuolo.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: id-porta-dominio
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.commons.search.IdPortaDominio}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.IdPortaDominio}
	 * @return Object type {@link org.openspcoop2.core.commons.search.IdPortaDominio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdPortaDominio readIdPortaDominio(String fileName) throws DeserializerException {
		return (IdPortaDominio) this.xmlToObj(fileName, IdPortaDominio.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.commons.search.IdPortaDominio}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.IdPortaDominio}
	 * @return Object type {@link org.openspcoop2.core.commons.search.IdPortaDominio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdPortaDominio readIdPortaDominio(File file) throws DeserializerException {
		return (IdPortaDominio) this.xmlToObj(file, IdPortaDominio.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.commons.search.IdPortaDominio}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.IdPortaDominio}
	 * @return Object type {@link org.openspcoop2.core.commons.search.IdPortaDominio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdPortaDominio readIdPortaDominio(InputStream in) throws DeserializerException {
		return (IdPortaDominio) this.xmlToObj(in, IdPortaDominio.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.commons.search.IdPortaDominio}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.IdPortaDominio}
	 * @return Object type {@link org.openspcoop2.core.commons.search.IdPortaDominio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdPortaDominio readIdPortaDominio(byte[] in) throws DeserializerException {
		return (IdPortaDominio) this.xmlToObj(in, IdPortaDominio.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.commons.search.IdPortaDominio}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.IdPortaDominio}
	 * @return Object type {@link org.openspcoop2.core.commons.search.IdPortaDominio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdPortaDominio readIdPortaDominioFromString(String in) throws DeserializerException {
		return (IdPortaDominio) this.xmlToObj(in.getBytes(), IdPortaDominio.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: id-accordo-servizio-parte-specifica
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.commons.search.IdAccordoServizioParteSpecifica}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.IdAccordoServizioParteSpecifica}
	 * @return Object type {@link org.openspcoop2.core.commons.search.IdAccordoServizioParteSpecifica}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdAccordoServizioParteSpecifica readIdAccordoServizioParteSpecifica(String fileName) throws DeserializerException {
		return (IdAccordoServizioParteSpecifica) this.xmlToObj(fileName, IdAccordoServizioParteSpecifica.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.commons.search.IdAccordoServizioParteSpecifica}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.IdAccordoServizioParteSpecifica}
	 * @return Object type {@link org.openspcoop2.core.commons.search.IdAccordoServizioParteSpecifica}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdAccordoServizioParteSpecifica readIdAccordoServizioParteSpecifica(File file) throws DeserializerException {
		return (IdAccordoServizioParteSpecifica) this.xmlToObj(file, IdAccordoServizioParteSpecifica.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.commons.search.IdAccordoServizioParteSpecifica}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.IdAccordoServizioParteSpecifica}
	 * @return Object type {@link org.openspcoop2.core.commons.search.IdAccordoServizioParteSpecifica}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdAccordoServizioParteSpecifica readIdAccordoServizioParteSpecifica(InputStream in) throws DeserializerException {
		return (IdAccordoServizioParteSpecifica) this.xmlToObj(in, IdAccordoServizioParteSpecifica.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.commons.search.IdAccordoServizioParteSpecifica}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.IdAccordoServizioParteSpecifica}
	 * @return Object type {@link org.openspcoop2.core.commons.search.IdAccordoServizioParteSpecifica}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdAccordoServizioParteSpecifica readIdAccordoServizioParteSpecifica(byte[] in) throws DeserializerException {
		return (IdAccordoServizioParteSpecifica) this.xmlToObj(in, IdAccordoServizioParteSpecifica.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.commons.search.IdAccordoServizioParteSpecifica}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.IdAccordoServizioParteSpecifica}
	 * @return Object type {@link org.openspcoop2.core.commons.search.IdAccordoServizioParteSpecifica}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdAccordoServizioParteSpecifica readIdAccordoServizioParteSpecificaFromString(String in) throws DeserializerException {
		return (IdAccordoServizioParteSpecifica) this.xmlToObj(in.getBytes(), IdAccordoServizioParteSpecifica.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: servizio-applicativo-ruolo
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.commons.search.ServizioApplicativoRuolo}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.ServizioApplicativoRuolo}
	 * @return Object type {@link org.openspcoop2.core.commons.search.ServizioApplicativoRuolo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ServizioApplicativoRuolo readServizioApplicativoRuolo(String fileName) throws DeserializerException {
		return (ServizioApplicativoRuolo) this.xmlToObj(fileName, ServizioApplicativoRuolo.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.commons.search.ServizioApplicativoRuolo}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.ServizioApplicativoRuolo}
	 * @return Object type {@link org.openspcoop2.core.commons.search.ServizioApplicativoRuolo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ServizioApplicativoRuolo readServizioApplicativoRuolo(File file) throws DeserializerException {
		return (ServizioApplicativoRuolo) this.xmlToObj(file, ServizioApplicativoRuolo.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.commons.search.ServizioApplicativoRuolo}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.ServizioApplicativoRuolo}
	 * @return Object type {@link org.openspcoop2.core.commons.search.ServizioApplicativoRuolo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ServizioApplicativoRuolo readServizioApplicativoRuolo(InputStream in) throws DeserializerException {
		return (ServizioApplicativoRuolo) this.xmlToObj(in, ServizioApplicativoRuolo.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.commons.search.ServizioApplicativoRuolo}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.ServizioApplicativoRuolo}
	 * @return Object type {@link org.openspcoop2.core.commons.search.ServizioApplicativoRuolo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ServizioApplicativoRuolo readServizioApplicativoRuolo(byte[] in) throws DeserializerException {
		return (ServizioApplicativoRuolo) this.xmlToObj(in, ServizioApplicativoRuolo.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.commons.search.ServizioApplicativoRuolo}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.ServizioApplicativoRuolo}
	 * @return Object type {@link org.openspcoop2.core.commons.search.ServizioApplicativoRuolo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ServizioApplicativoRuolo readServizioApplicativoRuoloFromString(String in) throws DeserializerException {
		return (ServizioApplicativoRuolo) this.xmlToObj(in.getBytes(), ServizioApplicativoRuolo.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: id-accordo-servizio-parte-comune-azione
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.commons.search.IdAccordoServizioParteComuneAzione}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.IdAccordoServizioParteComuneAzione}
	 * @return Object type {@link org.openspcoop2.core.commons.search.IdAccordoServizioParteComuneAzione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdAccordoServizioParteComuneAzione readIdAccordoServizioParteComuneAzione(String fileName) throws DeserializerException {
		return (IdAccordoServizioParteComuneAzione) this.xmlToObj(fileName, IdAccordoServizioParteComuneAzione.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.commons.search.IdAccordoServizioParteComuneAzione}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.IdAccordoServizioParteComuneAzione}
	 * @return Object type {@link org.openspcoop2.core.commons.search.IdAccordoServizioParteComuneAzione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdAccordoServizioParteComuneAzione readIdAccordoServizioParteComuneAzione(File file) throws DeserializerException {
		return (IdAccordoServizioParteComuneAzione) this.xmlToObj(file, IdAccordoServizioParteComuneAzione.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.commons.search.IdAccordoServizioParteComuneAzione}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.IdAccordoServizioParteComuneAzione}
	 * @return Object type {@link org.openspcoop2.core.commons.search.IdAccordoServizioParteComuneAzione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdAccordoServizioParteComuneAzione readIdAccordoServizioParteComuneAzione(InputStream in) throws DeserializerException {
		return (IdAccordoServizioParteComuneAzione) this.xmlToObj(in, IdAccordoServizioParteComuneAzione.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.commons.search.IdAccordoServizioParteComuneAzione}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.IdAccordoServizioParteComuneAzione}
	 * @return Object type {@link org.openspcoop2.core.commons.search.IdAccordoServizioParteComuneAzione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdAccordoServizioParteComuneAzione readIdAccordoServizioParteComuneAzione(byte[] in) throws DeserializerException {
		return (IdAccordoServizioParteComuneAzione) this.xmlToObj(in, IdAccordoServizioParteComuneAzione.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.commons.search.IdAccordoServizioParteComuneAzione}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.IdAccordoServizioParteComuneAzione}
	 * @return Object type {@link org.openspcoop2.core.commons.search.IdAccordoServizioParteComuneAzione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdAccordoServizioParteComuneAzione readIdAccordoServizioParteComuneAzioneFromString(String in) throws DeserializerException {
		return (IdAccordoServizioParteComuneAzione) this.xmlToObj(in.getBytes(), IdAccordoServizioParteComuneAzione.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: accordo-servizio-parte-specifica
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.commons.search.AccordoServizioParteSpecifica}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.AccordoServizioParteSpecifica}
	 * @return Object type {@link org.openspcoop2.core.commons.search.AccordoServizioParteSpecifica}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccordoServizioParteSpecifica readAccordoServizioParteSpecifica(String fileName) throws DeserializerException {
		return (AccordoServizioParteSpecifica) this.xmlToObj(fileName, AccordoServizioParteSpecifica.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.commons.search.AccordoServizioParteSpecifica}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.AccordoServizioParteSpecifica}
	 * @return Object type {@link org.openspcoop2.core.commons.search.AccordoServizioParteSpecifica}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccordoServizioParteSpecifica readAccordoServizioParteSpecifica(File file) throws DeserializerException {
		return (AccordoServizioParteSpecifica) this.xmlToObj(file, AccordoServizioParteSpecifica.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.commons.search.AccordoServizioParteSpecifica}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.AccordoServizioParteSpecifica}
	 * @return Object type {@link org.openspcoop2.core.commons.search.AccordoServizioParteSpecifica}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccordoServizioParteSpecifica readAccordoServizioParteSpecifica(InputStream in) throws DeserializerException {
		return (AccordoServizioParteSpecifica) this.xmlToObj(in, AccordoServizioParteSpecifica.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.commons.search.AccordoServizioParteSpecifica}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.AccordoServizioParteSpecifica}
	 * @return Object type {@link org.openspcoop2.core.commons.search.AccordoServizioParteSpecifica}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccordoServizioParteSpecifica readAccordoServizioParteSpecifica(byte[] in) throws DeserializerException {
		return (AccordoServizioParteSpecifica) this.xmlToObj(in, AccordoServizioParteSpecifica.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.commons.search.AccordoServizioParteSpecifica}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.AccordoServizioParteSpecifica}
	 * @return Object type {@link org.openspcoop2.core.commons.search.AccordoServizioParteSpecifica}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccordoServizioParteSpecifica readAccordoServizioParteSpecificaFromString(String in) throws DeserializerException {
		return (AccordoServizioParteSpecifica) this.xmlToObj(in.getBytes(), AccordoServizioParteSpecifica.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: servizio-applicativo-proprieta-protocollo
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.commons.search.ServizioApplicativoProprietaProtocollo}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.ServizioApplicativoProprietaProtocollo}
	 * @return Object type {@link org.openspcoop2.core.commons.search.ServizioApplicativoProprietaProtocollo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ServizioApplicativoProprietaProtocollo readServizioApplicativoProprietaProtocollo(String fileName) throws DeserializerException {
		return (ServizioApplicativoProprietaProtocollo) this.xmlToObj(fileName, ServizioApplicativoProprietaProtocollo.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.commons.search.ServizioApplicativoProprietaProtocollo}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.ServizioApplicativoProprietaProtocollo}
	 * @return Object type {@link org.openspcoop2.core.commons.search.ServizioApplicativoProprietaProtocollo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ServizioApplicativoProprietaProtocollo readServizioApplicativoProprietaProtocollo(File file) throws DeserializerException {
		return (ServizioApplicativoProprietaProtocollo) this.xmlToObj(file, ServizioApplicativoProprietaProtocollo.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.commons.search.ServizioApplicativoProprietaProtocollo}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.ServizioApplicativoProprietaProtocollo}
	 * @return Object type {@link org.openspcoop2.core.commons.search.ServizioApplicativoProprietaProtocollo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ServizioApplicativoProprietaProtocollo readServizioApplicativoProprietaProtocollo(InputStream in) throws DeserializerException {
		return (ServizioApplicativoProprietaProtocollo) this.xmlToObj(in, ServizioApplicativoProprietaProtocollo.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.commons.search.ServizioApplicativoProprietaProtocollo}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.ServizioApplicativoProprietaProtocollo}
	 * @return Object type {@link org.openspcoop2.core.commons.search.ServizioApplicativoProprietaProtocollo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ServizioApplicativoProprietaProtocollo readServizioApplicativoProprietaProtocollo(byte[] in) throws DeserializerException {
		return (ServizioApplicativoProprietaProtocollo) this.xmlToObj(in, ServizioApplicativoProprietaProtocollo.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.commons.search.ServizioApplicativoProprietaProtocollo}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.ServizioApplicativoProprietaProtocollo}
	 * @return Object type {@link org.openspcoop2.core.commons.search.ServizioApplicativoProprietaProtocollo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ServizioApplicativoProprietaProtocollo readServizioApplicativoProprietaProtocolloFromString(String in) throws DeserializerException {
		return (ServizioApplicativoProprietaProtocollo) this.xmlToObj(in.getBytes(), ServizioApplicativoProprietaProtocollo.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: ruolo
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.commons.search.Ruolo}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.Ruolo}
	 * @return Object type {@link org.openspcoop2.core.commons.search.Ruolo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Ruolo readRuolo(String fileName) throws DeserializerException {
		return (Ruolo) this.xmlToObj(fileName, Ruolo.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.commons.search.Ruolo}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.Ruolo}
	 * @return Object type {@link org.openspcoop2.core.commons.search.Ruolo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Ruolo readRuolo(File file) throws DeserializerException {
		return (Ruolo) this.xmlToObj(file, Ruolo.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.commons.search.Ruolo}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.Ruolo}
	 * @return Object type {@link org.openspcoop2.core.commons.search.Ruolo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Ruolo readRuolo(InputStream in) throws DeserializerException {
		return (Ruolo) this.xmlToObj(in, Ruolo.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.commons.search.Ruolo}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.Ruolo}
	 * @return Object type {@link org.openspcoop2.core.commons.search.Ruolo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Ruolo readRuolo(byte[] in) throws DeserializerException {
		return (Ruolo) this.xmlToObj(in, Ruolo.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.commons.search.Ruolo}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.Ruolo}
	 * @return Object type {@link org.openspcoop2.core.commons.search.Ruolo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Ruolo readRuoloFromString(String in) throws DeserializerException {
		return (Ruolo) this.xmlToObj(in.getBytes(), Ruolo.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: soggetto-ruolo
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.commons.search.SoggettoRuolo}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.SoggettoRuolo}
	 * @return Object type {@link org.openspcoop2.core.commons.search.SoggettoRuolo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SoggettoRuolo readSoggettoRuolo(String fileName) throws DeserializerException {
		return (SoggettoRuolo) this.xmlToObj(fileName, SoggettoRuolo.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.commons.search.SoggettoRuolo}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.SoggettoRuolo}
	 * @return Object type {@link org.openspcoop2.core.commons.search.SoggettoRuolo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SoggettoRuolo readSoggettoRuolo(File file) throws DeserializerException {
		return (SoggettoRuolo) this.xmlToObj(file, SoggettoRuolo.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.commons.search.SoggettoRuolo}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.SoggettoRuolo}
	 * @return Object type {@link org.openspcoop2.core.commons.search.SoggettoRuolo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SoggettoRuolo readSoggettoRuolo(InputStream in) throws DeserializerException {
		return (SoggettoRuolo) this.xmlToObj(in, SoggettoRuolo.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.commons.search.SoggettoRuolo}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.SoggettoRuolo}
	 * @return Object type {@link org.openspcoop2.core.commons.search.SoggettoRuolo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SoggettoRuolo readSoggettoRuolo(byte[] in) throws DeserializerException {
		return (SoggettoRuolo) this.xmlToObj(in, SoggettoRuolo.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.commons.search.SoggettoRuolo}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.SoggettoRuolo}
	 * @return Object type {@link org.openspcoop2.core.commons.search.SoggettoRuolo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SoggettoRuolo readSoggettoRuoloFromString(String in) throws DeserializerException {
		return (SoggettoRuolo) this.xmlToObj(in.getBytes(), SoggettoRuolo.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: servizio-applicativo
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.commons.search.ServizioApplicativo}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.ServizioApplicativo}
	 * @return Object type {@link org.openspcoop2.core.commons.search.ServizioApplicativo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ServizioApplicativo readServizioApplicativo(String fileName) throws DeserializerException {
		return (ServizioApplicativo) this.xmlToObj(fileName, ServizioApplicativo.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.commons.search.ServizioApplicativo}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.ServizioApplicativo}
	 * @return Object type {@link org.openspcoop2.core.commons.search.ServizioApplicativo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ServizioApplicativo readServizioApplicativo(File file) throws DeserializerException {
		return (ServizioApplicativo) this.xmlToObj(file, ServizioApplicativo.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.commons.search.ServizioApplicativo}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.ServizioApplicativo}
	 * @return Object type {@link org.openspcoop2.core.commons.search.ServizioApplicativo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ServizioApplicativo readServizioApplicativo(InputStream in) throws DeserializerException {
		return (ServizioApplicativo) this.xmlToObj(in, ServizioApplicativo.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.commons.search.ServizioApplicativo}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.ServizioApplicativo}
	 * @return Object type {@link org.openspcoop2.core.commons.search.ServizioApplicativo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ServizioApplicativo readServizioApplicativo(byte[] in) throws DeserializerException {
		return (ServizioApplicativo) this.xmlToObj(in, ServizioApplicativo.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.commons.search.ServizioApplicativo}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.ServizioApplicativo}
	 * @return Object type {@link org.openspcoop2.core.commons.search.ServizioApplicativo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ServizioApplicativo readServizioApplicativoFromString(String in) throws DeserializerException {
		return (ServizioApplicativo) this.xmlToObj(in.getBytes(), ServizioApplicativo.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: id-gruppo
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.commons.search.IdGruppo}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.IdGruppo}
	 * @return Object type {@link org.openspcoop2.core.commons.search.IdGruppo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdGruppo readIdGruppo(String fileName) throws DeserializerException {
		return (IdGruppo) this.xmlToObj(fileName, IdGruppo.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.commons.search.IdGruppo}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.IdGruppo}
	 * @return Object type {@link org.openspcoop2.core.commons.search.IdGruppo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdGruppo readIdGruppo(File file) throws DeserializerException {
		return (IdGruppo) this.xmlToObj(file, IdGruppo.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.commons.search.IdGruppo}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.IdGruppo}
	 * @return Object type {@link org.openspcoop2.core.commons.search.IdGruppo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdGruppo readIdGruppo(InputStream in) throws DeserializerException {
		return (IdGruppo) this.xmlToObj(in, IdGruppo.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.commons.search.IdGruppo}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.IdGruppo}
	 * @return Object type {@link org.openspcoop2.core.commons.search.IdGruppo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdGruppo readIdGruppo(byte[] in) throws DeserializerException {
		return (IdGruppo) this.xmlToObj(in, IdGruppo.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.commons.search.IdGruppo}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.IdGruppo}
	 * @return Object type {@link org.openspcoop2.core.commons.search.IdGruppo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdGruppo readIdGruppoFromString(String in) throws DeserializerException {
		return (IdGruppo) this.xmlToObj(in.getBytes(), IdGruppo.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: accordo-servizio-parte-comune-gruppo
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.commons.search.AccordoServizioParteComuneGruppo}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.AccordoServizioParteComuneGruppo}
	 * @return Object type {@link org.openspcoop2.core.commons.search.AccordoServizioParteComuneGruppo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccordoServizioParteComuneGruppo readAccordoServizioParteComuneGruppo(String fileName) throws DeserializerException {
		return (AccordoServizioParteComuneGruppo) this.xmlToObj(fileName, AccordoServizioParteComuneGruppo.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.commons.search.AccordoServizioParteComuneGruppo}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.AccordoServizioParteComuneGruppo}
	 * @return Object type {@link org.openspcoop2.core.commons.search.AccordoServizioParteComuneGruppo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccordoServizioParteComuneGruppo readAccordoServizioParteComuneGruppo(File file) throws DeserializerException {
		return (AccordoServizioParteComuneGruppo) this.xmlToObj(file, AccordoServizioParteComuneGruppo.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.commons.search.AccordoServizioParteComuneGruppo}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.AccordoServizioParteComuneGruppo}
	 * @return Object type {@link org.openspcoop2.core.commons.search.AccordoServizioParteComuneGruppo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccordoServizioParteComuneGruppo readAccordoServizioParteComuneGruppo(InputStream in) throws DeserializerException {
		return (AccordoServizioParteComuneGruppo) this.xmlToObj(in, AccordoServizioParteComuneGruppo.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.commons.search.AccordoServizioParteComuneGruppo}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.AccordoServizioParteComuneGruppo}
	 * @return Object type {@link org.openspcoop2.core.commons.search.AccordoServizioParteComuneGruppo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccordoServizioParteComuneGruppo readAccordoServizioParteComuneGruppo(byte[] in) throws DeserializerException {
		return (AccordoServizioParteComuneGruppo) this.xmlToObj(in, AccordoServizioParteComuneGruppo.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.commons.search.AccordoServizioParteComuneGruppo}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.AccordoServizioParteComuneGruppo}
	 * @return Object type {@link org.openspcoop2.core.commons.search.AccordoServizioParteComuneGruppo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccordoServizioParteComuneGruppo readAccordoServizioParteComuneGruppoFromString(String in) throws DeserializerException {
		return (AccordoServizioParteComuneGruppo) this.xmlToObj(in.getBytes(), AccordoServizioParteComuneGruppo.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: id-servizio-applicativo
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.commons.search.IdServizioApplicativo}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.IdServizioApplicativo}
	 * @return Object type {@link org.openspcoop2.core.commons.search.IdServizioApplicativo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdServizioApplicativo readIdServizioApplicativo(String fileName) throws DeserializerException {
		return (IdServizioApplicativo) this.xmlToObj(fileName, IdServizioApplicativo.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.commons.search.IdServizioApplicativo}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.IdServizioApplicativo}
	 * @return Object type {@link org.openspcoop2.core.commons.search.IdServizioApplicativo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdServizioApplicativo readIdServizioApplicativo(File file) throws DeserializerException {
		return (IdServizioApplicativo) this.xmlToObj(file, IdServizioApplicativo.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.commons.search.IdServizioApplicativo}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.IdServizioApplicativo}
	 * @return Object type {@link org.openspcoop2.core.commons.search.IdServizioApplicativo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdServizioApplicativo readIdServizioApplicativo(InputStream in) throws DeserializerException {
		return (IdServizioApplicativo) this.xmlToObj(in, IdServizioApplicativo.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.commons.search.IdServizioApplicativo}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.IdServizioApplicativo}
	 * @return Object type {@link org.openspcoop2.core.commons.search.IdServizioApplicativo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdServizioApplicativo readIdServizioApplicativo(byte[] in) throws DeserializerException {
		return (IdServizioApplicativo) this.xmlToObj(in, IdServizioApplicativo.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.commons.search.IdServizioApplicativo}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.IdServizioApplicativo}
	 * @return Object type {@link org.openspcoop2.core.commons.search.IdServizioApplicativo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdServizioApplicativo readIdServizioApplicativoFromString(String in) throws DeserializerException {
		return (IdServizioApplicativo) this.xmlToObj(in.getBytes(), IdServizioApplicativo.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: port-type
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.commons.search.PortType}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.PortType}
	 * @return Object type {@link org.openspcoop2.core.commons.search.PortType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PortType readPortType(String fileName) throws DeserializerException {
		return (PortType) this.xmlToObj(fileName, PortType.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.commons.search.PortType}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.PortType}
	 * @return Object type {@link org.openspcoop2.core.commons.search.PortType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PortType readPortType(File file) throws DeserializerException {
		return (PortType) this.xmlToObj(file, PortType.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.commons.search.PortType}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.PortType}
	 * @return Object type {@link org.openspcoop2.core.commons.search.PortType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PortType readPortType(InputStream in) throws DeserializerException {
		return (PortType) this.xmlToObj(in, PortType.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.commons.search.PortType}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.PortType}
	 * @return Object type {@link org.openspcoop2.core.commons.search.PortType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PortType readPortType(byte[] in) throws DeserializerException {
		return (PortType) this.xmlToObj(in, PortType.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.commons.search.PortType}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.PortType}
	 * @return Object type {@link org.openspcoop2.core.commons.search.PortType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PortType readPortTypeFromString(String in) throws DeserializerException {
		return (PortType) this.xmlToObj(in.getBytes(), PortType.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: id-porta-delegata
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.commons.search.IdPortaDelegata}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.IdPortaDelegata}
	 * @return Object type {@link org.openspcoop2.core.commons.search.IdPortaDelegata}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdPortaDelegata readIdPortaDelegata(String fileName) throws DeserializerException {
		return (IdPortaDelegata) this.xmlToObj(fileName, IdPortaDelegata.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.commons.search.IdPortaDelegata}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.IdPortaDelegata}
	 * @return Object type {@link org.openspcoop2.core.commons.search.IdPortaDelegata}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdPortaDelegata readIdPortaDelegata(File file) throws DeserializerException {
		return (IdPortaDelegata) this.xmlToObj(file, IdPortaDelegata.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.commons.search.IdPortaDelegata}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.IdPortaDelegata}
	 * @return Object type {@link org.openspcoop2.core.commons.search.IdPortaDelegata}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdPortaDelegata readIdPortaDelegata(InputStream in) throws DeserializerException {
		return (IdPortaDelegata) this.xmlToObj(in, IdPortaDelegata.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.commons.search.IdPortaDelegata}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.IdPortaDelegata}
	 * @return Object type {@link org.openspcoop2.core.commons.search.IdPortaDelegata}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdPortaDelegata readIdPortaDelegata(byte[] in) throws DeserializerException {
		return (IdPortaDelegata) this.xmlToObj(in, IdPortaDelegata.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.commons.search.IdPortaDelegata}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.IdPortaDelegata}
	 * @return Object type {@link org.openspcoop2.core.commons.search.IdPortaDelegata}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdPortaDelegata readIdPortaDelegataFromString(String in) throws DeserializerException {
		return (IdPortaDelegata) this.xmlToObj(in.getBytes(), IdPortaDelegata.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: id-accordo-servizio-parte-comune-gruppo
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.commons.search.IdAccordoServizioParteComuneGruppo}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.IdAccordoServizioParteComuneGruppo}
	 * @return Object type {@link org.openspcoop2.core.commons.search.IdAccordoServizioParteComuneGruppo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdAccordoServizioParteComuneGruppo readIdAccordoServizioParteComuneGruppo(String fileName) throws DeserializerException {
		return (IdAccordoServizioParteComuneGruppo) this.xmlToObj(fileName, IdAccordoServizioParteComuneGruppo.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.commons.search.IdAccordoServizioParteComuneGruppo}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.IdAccordoServizioParteComuneGruppo}
	 * @return Object type {@link org.openspcoop2.core.commons.search.IdAccordoServizioParteComuneGruppo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdAccordoServizioParteComuneGruppo readIdAccordoServizioParteComuneGruppo(File file) throws DeserializerException {
		return (IdAccordoServizioParteComuneGruppo) this.xmlToObj(file, IdAccordoServizioParteComuneGruppo.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.commons.search.IdAccordoServizioParteComuneGruppo}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.IdAccordoServizioParteComuneGruppo}
	 * @return Object type {@link org.openspcoop2.core.commons.search.IdAccordoServizioParteComuneGruppo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdAccordoServizioParteComuneGruppo readIdAccordoServizioParteComuneGruppo(InputStream in) throws DeserializerException {
		return (IdAccordoServizioParteComuneGruppo) this.xmlToObj(in, IdAccordoServizioParteComuneGruppo.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.commons.search.IdAccordoServizioParteComuneGruppo}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.IdAccordoServizioParteComuneGruppo}
	 * @return Object type {@link org.openspcoop2.core.commons.search.IdAccordoServizioParteComuneGruppo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdAccordoServizioParteComuneGruppo readIdAccordoServizioParteComuneGruppo(byte[] in) throws DeserializerException {
		return (IdAccordoServizioParteComuneGruppo) this.xmlToObj(in, IdAccordoServizioParteComuneGruppo.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.commons.search.IdAccordoServizioParteComuneGruppo}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.IdAccordoServizioParteComuneGruppo}
	 * @return Object type {@link org.openspcoop2.core.commons.search.IdAccordoServizioParteComuneGruppo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdAccordoServizioParteComuneGruppo readIdAccordoServizioParteComuneGruppoFromString(String in) throws DeserializerException {
		return (IdAccordoServizioParteComuneGruppo) this.xmlToObj(in.getBytes(), IdAccordoServizioParteComuneGruppo.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: porta-delegata-azione
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.commons.search.PortaDelegataAzione}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.PortaDelegataAzione}
	 * @return Object type {@link org.openspcoop2.core.commons.search.PortaDelegataAzione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PortaDelegataAzione readPortaDelegataAzione(String fileName) throws DeserializerException {
		return (PortaDelegataAzione) this.xmlToObj(fileName, PortaDelegataAzione.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.commons.search.PortaDelegataAzione}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.PortaDelegataAzione}
	 * @return Object type {@link org.openspcoop2.core.commons.search.PortaDelegataAzione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PortaDelegataAzione readPortaDelegataAzione(File file) throws DeserializerException {
		return (PortaDelegataAzione) this.xmlToObj(file, PortaDelegataAzione.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.commons.search.PortaDelegataAzione}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.PortaDelegataAzione}
	 * @return Object type {@link org.openspcoop2.core.commons.search.PortaDelegataAzione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PortaDelegataAzione readPortaDelegataAzione(InputStream in) throws DeserializerException {
		return (PortaDelegataAzione) this.xmlToObj(in, PortaDelegataAzione.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.commons.search.PortaDelegataAzione}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.PortaDelegataAzione}
	 * @return Object type {@link org.openspcoop2.core.commons.search.PortaDelegataAzione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PortaDelegataAzione readPortaDelegataAzione(byte[] in) throws DeserializerException {
		return (PortaDelegataAzione) this.xmlToObj(in, PortaDelegataAzione.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.commons.search.PortaDelegataAzione}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.PortaDelegataAzione}
	 * @return Object type {@link org.openspcoop2.core.commons.search.PortaDelegataAzione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PortaDelegataAzione readPortaDelegataAzioneFromString(String in) throws DeserializerException {
		return (PortaDelegataAzione) this.xmlToObj(in.getBytes(), PortaDelegataAzione.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: id-fruitore
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.commons.search.IdFruitore}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.IdFruitore}
	 * @return Object type {@link org.openspcoop2.core.commons.search.IdFruitore}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdFruitore readIdFruitore(String fileName) throws DeserializerException {
		return (IdFruitore) this.xmlToObj(fileName, IdFruitore.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.commons.search.IdFruitore}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.IdFruitore}
	 * @return Object type {@link org.openspcoop2.core.commons.search.IdFruitore}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdFruitore readIdFruitore(File file) throws DeserializerException {
		return (IdFruitore) this.xmlToObj(file, IdFruitore.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.commons.search.IdFruitore}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.IdFruitore}
	 * @return Object type {@link org.openspcoop2.core.commons.search.IdFruitore}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdFruitore readIdFruitore(InputStream in) throws DeserializerException {
		return (IdFruitore) this.xmlToObj(in, IdFruitore.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.commons.search.IdFruitore}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.IdFruitore}
	 * @return Object type {@link org.openspcoop2.core.commons.search.IdFruitore}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdFruitore readIdFruitore(byte[] in) throws DeserializerException {
		return (IdFruitore) this.xmlToObj(in, IdFruitore.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.commons.search.IdFruitore}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.IdFruitore}
	 * @return Object type {@link org.openspcoop2.core.commons.search.IdFruitore}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdFruitore readIdFruitoreFromString(String in) throws DeserializerException {
		return (IdFruitore) this.xmlToObj(in.getBytes(), IdFruitore.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: id-operation
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.commons.search.IdOperation}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.IdOperation}
	 * @return Object type {@link org.openspcoop2.core.commons.search.IdOperation}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdOperation readIdOperation(String fileName) throws DeserializerException {
		return (IdOperation) this.xmlToObj(fileName, IdOperation.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.commons.search.IdOperation}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.IdOperation}
	 * @return Object type {@link org.openspcoop2.core.commons.search.IdOperation}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdOperation readIdOperation(File file) throws DeserializerException {
		return (IdOperation) this.xmlToObj(file, IdOperation.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.commons.search.IdOperation}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.IdOperation}
	 * @return Object type {@link org.openspcoop2.core.commons.search.IdOperation}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdOperation readIdOperation(InputStream in) throws DeserializerException {
		return (IdOperation) this.xmlToObj(in, IdOperation.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.commons.search.IdOperation}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.IdOperation}
	 * @return Object type {@link org.openspcoop2.core.commons.search.IdOperation}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdOperation readIdOperation(byte[] in) throws DeserializerException {
		return (IdOperation) this.xmlToObj(in, IdOperation.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.commons.search.IdOperation}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.IdOperation}
	 * @return Object type {@link org.openspcoop2.core.commons.search.IdOperation}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdOperation readIdOperationFromString(String in) throws DeserializerException {
		return (IdOperation) this.xmlToObj(in.getBytes(), IdOperation.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: soggetto
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.commons.search.Soggetto}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.Soggetto}
	 * @return Object type {@link org.openspcoop2.core.commons.search.Soggetto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Soggetto readSoggetto(String fileName) throws DeserializerException {
		return (Soggetto) this.xmlToObj(fileName, Soggetto.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.commons.search.Soggetto}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.Soggetto}
	 * @return Object type {@link org.openspcoop2.core.commons.search.Soggetto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Soggetto readSoggetto(File file) throws DeserializerException {
		return (Soggetto) this.xmlToObj(file, Soggetto.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.commons.search.Soggetto}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.Soggetto}
	 * @return Object type {@link org.openspcoop2.core.commons.search.Soggetto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Soggetto readSoggetto(InputStream in) throws DeserializerException {
		return (Soggetto) this.xmlToObj(in, Soggetto.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.commons.search.Soggetto}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.Soggetto}
	 * @return Object type {@link org.openspcoop2.core.commons.search.Soggetto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Soggetto readSoggetto(byte[] in) throws DeserializerException {
		return (Soggetto) this.xmlToObj(in, Soggetto.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.commons.search.Soggetto}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.Soggetto}
	 * @return Object type {@link org.openspcoop2.core.commons.search.Soggetto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Soggetto readSoggettoFromString(String in) throws DeserializerException {
		return (Soggetto) this.xmlToObj(in.getBytes(), Soggetto.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: gruppo
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.commons.search.Gruppo}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.Gruppo}
	 * @return Object type {@link org.openspcoop2.core.commons.search.Gruppo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Gruppo readGruppo(String fileName) throws DeserializerException {
		return (Gruppo) this.xmlToObj(fileName, Gruppo.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.commons.search.Gruppo}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.Gruppo}
	 * @return Object type {@link org.openspcoop2.core.commons.search.Gruppo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Gruppo readGruppo(File file) throws DeserializerException {
		return (Gruppo) this.xmlToObj(file, Gruppo.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.commons.search.Gruppo}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.Gruppo}
	 * @return Object type {@link org.openspcoop2.core.commons.search.Gruppo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Gruppo readGruppo(InputStream in) throws DeserializerException {
		return (Gruppo) this.xmlToObj(in, Gruppo.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.commons.search.Gruppo}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.Gruppo}
	 * @return Object type {@link org.openspcoop2.core.commons.search.Gruppo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Gruppo readGruppo(byte[] in) throws DeserializerException {
		return (Gruppo) this.xmlToObj(in, Gruppo.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.commons.search.Gruppo}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.Gruppo}
	 * @return Object type {@link org.openspcoop2.core.commons.search.Gruppo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Gruppo readGruppoFromString(String in) throws DeserializerException {
		return (Gruppo) this.xmlToObj(in.getBytes(), Gruppo.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: accordo-servizio-parte-comune
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.commons.search.AccordoServizioParteComune}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.AccordoServizioParteComune}
	 * @return Object type {@link org.openspcoop2.core.commons.search.AccordoServizioParteComune}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccordoServizioParteComune readAccordoServizioParteComune(String fileName) throws DeserializerException {
		return (AccordoServizioParteComune) this.xmlToObj(fileName, AccordoServizioParteComune.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.commons.search.AccordoServizioParteComune}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.AccordoServizioParteComune}
	 * @return Object type {@link org.openspcoop2.core.commons.search.AccordoServizioParteComune}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccordoServizioParteComune readAccordoServizioParteComune(File file) throws DeserializerException {
		return (AccordoServizioParteComune) this.xmlToObj(file, AccordoServizioParteComune.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.commons.search.AccordoServizioParteComune}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.AccordoServizioParteComune}
	 * @return Object type {@link org.openspcoop2.core.commons.search.AccordoServizioParteComune}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccordoServizioParteComune readAccordoServizioParteComune(InputStream in) throws DeserializerException {
		return (AccordoServizioParteComune) this.xmlToObj(in, AccordoServizioParteComune.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.commons.search.AccordoServizioParteComune}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.AccordoServizioParteComune}
	 * @return Object type {@link org.openspcoop2.core.commons.search.AccordoServizioParteComune}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccordoServizioParteComune readAccordoServizioParteComune(byte[] in) throws DeserializerException {
		return (AccordoServizioParteComune) this.xmlToObj(in, AccordoServizioParteComune.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.commons.search.AccordoServizioParteComune}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.AccordoServizioParteComune}
	 * @return Object type {@link org.openspcoop2.core.commons.search.AccordoServizioParteComune}
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
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.commons.search.PortaDominio}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.PortaDominio}
	 * @return Object type {@link org.openspcoop2.core.commons.search.PortaDominio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PortaDominio readPortaDominio(String fileName) throws DeserializerException {
		return (PortaDominio) this.xmlToObj(fileName, PortaDominio.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.commons.search.PortaDominio}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.PortaDominio}
	 * @return Object type {@link org.openspcoop2.core.commons.search.PortaDominio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PortaDominio readPortaDominio(File file) throws DeserializerException {
		return (PortaDominio) this.xmlToObj(file, PortaDominio.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.commons.search.PortaDominio}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.PortaDominio}
	 * @return Object type {@link org.openspcoop2.core.commons.search.PortaDominio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PortaDominio readPortaDominio(InputStream in) throws DeserializerException {
		return (PortaDominio) this.xmlToObj(in, PortaDominio.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.commons.search.PortaDominio}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.PortaDominio}
	 * @return Object type {@link org.openspcoop2.core.commons.search.PortaDominio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PortaDominio readPortaDominio(byte[] in) throws DeserializerException {
		return (PortaDominio) this.xmlToObj(in, PortaDominio.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.commons.search.PortaDominio}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.PortaDominio}
	 * @return Object type {@link org.openspcoop2.core.commons.search.PortaDominio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PortaDominio readPortaDominioFromString(String in) throws DeserializerException {
		return (PortaDominio) this.xmlToObj(in.getBytes(), PortaDominio.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: id-porta-applicativa
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.commons.search.IdPortaApplicativa}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.IdPortaApplicativa}
	 * @return Object type {@link org.openspcoop2.core.commons.search.IdPortaApplicativa}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdPortaApplicativa readIdPortaApplicativa(String fileName) throws DeserializerException {
		return (IdPortaApplicativa) this.xmlToObj(fileName, IdPortaApplicativa.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.commons.search.IdPortaApplicativa}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.IdPortaApplicativa}
	 * @return Object type {@link org.openspcoop2.core.commons.search.IdPortaApplicativa}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdPortaApplicativa readIdPortaApplicativa(File file) throws DeserializerException {
		return (IdPortaApplicativa) this.xmlToObj(file, IdPortaApplicativa.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.commons.search.IdPortaApplicativa}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.IdPortaApplicativa}
	 * @return Object type {@link org.openspcoop2.core.commons.search.IdPortaApplicativa}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdPortaApplicativa readIdPortaApplicativa(InputStream in) throws DeserializerException {
		return (IdPortaApplicativa) this.xmlToObj(in, IdPortaApplicativa.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.commons.search.IdPortaApplicativa}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.IdPortaApplicativa}
	 * @return Object type {@link org.openspcoop2.core.commons.search.IdPortaApplicativa}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdPortaApplicativa readIdPortaApplicativa(byte[] in) throws DeserializerException {
		return (IdPortaApplicativa) this.xmlToObj(in, IdPortaApplicativa.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.commons.search.IdPortaApplicativa}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.IdPortaApplicativa}
	 * @return Object type {@link org.openspcoop2.core.commons.search.IdPortaApplicativa}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdPortaApplicativa readIdPortaApplicativaFromString(String in) throws DeserializerException {
		return (IdPortaApplicativa) this.xmlToObj(in.getBytes(), IdPortaApplicativa.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: id-resource
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.commons.search.IdResource}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.IdResource}
	 * @return Object type {@link org.openspcoop2.core.commons.search.IdResource}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdResource readIdResource(String fileName) throws DeserializerException {
		return (IdResource) this.xmlToObj(fileName, IdResource.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.commons.search.IdResource}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.IdResource}
	 * @return Object type {@link org.openspcoop2.core.commons.search.IdResource}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdResource readIdResource(File file) throws DeserializerException {
		return (IdResource) this.xmlToObj(file, IdResource.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.commons.search.IdResource}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.IdResource}
	 * @return Object type {@link org.openspcoop2.core.commons.search.IdResource}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdResource readIdResource(InputStream in) throws DeserializerException {
		return (IdResource) this.xmlToObj(in, IdResource.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.commons.search.IdResource}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.IdResource}
	 * @return Object type {@link org.openspcoop2.core.commons.search.IdResource}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdResource readIdResource(byte[] in) throws DeserializerException {
		return (IdResource) this.xmlToObj(in, IdResource.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.commons.search.IdResource}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.IdResource}
	 * @return Object type {@link org.openspcoop2.core.commons.search.IdResource}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdResource readIdResourceFromString(String in) throws DeserializerException {
		return (IdResource) this.xmlToObj(in.getBytes(), IdResource.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: porta-delegata-servizio-applicativo
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.commons.search.PortaDelegataServizioApplicativo}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.PortaDelegataServizioApplicativo}
	 * @return Object type {@link org.openspcoop2.core.commons.search.PortaDelegataServizioApplicativo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PortaDelegataServizioApplicativo readPortaDelegataServizioApplicativo(String fileName) throws DeserializerException {
		return (PortaDelegataServizioApplicativo) this.xmlToObj(fileName, PortaDelegataServizioApplicativo.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.commons.search.PortaDelegataServizioApplicativo}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.PortaDelegataServizioApplicativo}
	 * @return Object type {@link org.openspcoop2.core.commons.search.PortaDelegataServizioApplicativo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PortaDelegataServizioApplicativo readPortaDelegataServizioApplicativo(File file) throws DeserializerException {
		return (PortaDelegataServizioApplicativo) this.xmlToObj(file, PortaDelegataServizioApplicativo.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.commons.search.PortaDelegataServizioApplicativo}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.PortaDelegataServizioApplicativo}
	 * @return Object type {@link org.openspcoop2.core.commons.search.PortaDelegataServizioApplicativo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PortaDelegataServizioApplicativo readPortaDelegataServizioApplicativo(InputStream in) throws DeserializerException {
		return (PortaDelegataServizioApplicativo) this.xmlToObj(in, PortaDelegataServizioApplicativo.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.commons.search.PortaDelegataServizioApplicativo}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.PortaDelegataServizioApplicativo}
	 * @return Object type {@link org.openspcoop2.core.commons.search.PortaDelegataServizioApplicativo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PortaDelegataServizioApplicativo readPortaDelegataServizioApplicativo(byte[] in) throws DeserializerException {
		return (PortaDelegataServizioApplicativo) this.xmlToObj(in, PortaDelegataServizioApplicativo.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.commons.search.PortaDelegataServizioApplicativo}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.PortaDelegataServizioApplicativo}
	 * @return Object type {@link org.openspcoop2.core.commons.search.PortaDelegataServizioApplicativo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PortaDelegataServizioApplicativo readPortaDelegataServizioApplicativoFromString(String in) throws DeserializerException {
		return (PortaDelegataServizioApplicativo) this.xmlToObj(in.getBytes(), PortaDelegataServizioApplicativo.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: fruitore
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.commons.search.Fruitore}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.Fruitore}
	 * @return Object type {@link org.openspcoop2.core.commons.search.Fruitore}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Fruitore readFruitore(String fileName) throws DeserializerException {
		return (Fruitore) this.xmlToObj(fileName, Fruitore.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.commons.search.Fruitore}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.Fruitore}
	 * @return Object type {@link org.openspcoop2.core.commons.search.Fruitore}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Fruitore readFruitore(File file) throws DeserializerException {
		return (Fruitore) this.xmlToObj(file, Fruitore.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.commons.search.Fruitore}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.Fruitore}
	 * @return Object type {@link org.openspcoop2.core.commons.search.Fruitore}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Fruitore readFruitore(InputStream in) throws DeserializerException {
		return (Fruitore) this.xmlToObj(in, Fruitore.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.commons.search.Fruitore}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.Fruitore}
	 * @return Object type {@link org.openspcoop2.core.commons.search.Fruitore}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Fruitore readFruitore(byte[] in) throws DeserializerException {
		return (Fruitore) this.xmlToObj(in, Fruitore.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.commons.search.Fruitore}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.Fruitore}
	 * @return Object type {@link org.openspcoop2.core.commons.search.Fruitore}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Fruitore readFruitoreFromString(String in) throws DeserializerException {
		return (Fruitore) this.xmlToObj(in.getBytes(), Fruitore.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: porta-delegata
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.commons.search.PortaDelegata}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.PortaDelegata}
	 * @return Object type {@link org.openspcoop2.core.commons.search.PortaDelegata}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PortaDelegata readPortaDelegata(String fileName) throws DeserializerException {
		return (PortaDelegata) this.xmlToObj(fileName, PortaDelegata.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.commons.search.PortaDelegata}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.PortaDelegata}
	 * @return Object type {@link org.openspcoop2.core.commons.search.PortaDelegata}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PortaDelegata readPortaDelegata(File file) throws DeserializerException {
		return (PortaDelegata) this.xmlToObj(file, PortaDelegata.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.commons.search.PortaDelegata}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.PortaDelegata}
	 * @return Object type {@link org.openspcoop2.core.commons.search.PortaDelegata}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PortaDelegata readPortaDelegata(InputStream in) throws DeserializerException {
		return (PortaDelegata) this.xmlToObj(in, PortaDelegata.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.commons.search.PortaDelegata}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.PortaDelegata}
	 * @return Object type {@link org.openspcoop2.core.commons.search.PortaDelegata}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PortaDelegata readPortaDelegata(byte[] in) throws DeserializerException {
		return (PortaDelegata) this.xmlToObj(in, PortaDelegata.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.commons.search.PortaDelegata}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.PortaDelegata}
	 * @return Object type {@link org.openspcoop2.core.commons.search.PortaDelegata}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PortaDelegata readPortaDelegataFromString(String in) throws DeserializerException {
		return (PortaDelegata) this.xmlToObj(in.getBytes(), PortaDelegata.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: id-soggetto-ruolo
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.commons.search.IdSoggettoRuolo}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.IdSoggettoRuolo}
	 * @return Object type {@link org.openspcoop2.core.commons.search.IdSoggettoRuolo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdSoggettoRuolo readIdSoggettoRuolo(String fileName) throws DeserializerException {
		return (IdSoggettoRuolo) this.xmlToObj(fileName, IdSoggettoRuolo.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.commons.search.IdSoggettoRuolo}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.IdSoggettoRuolo}
	 * @return Object type {@link org.openspcoop2.core.commons.search.IdSoggettoRuolo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdSoggettoRuolo readIdSoggettoRuolo(File file) throws DeserializerException {
		return (IdSoggettoRuolo) this.xmlToObj(file, IdSoggettoRuolo.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.commons.search.IdSoggettoRuolo}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.IdSoggettoRuolo}
	 * @return Object type {@link org.openspcoop2.core.commons.search.IdSoggettoRuolo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdSoggettoRuolo readIdSoggettoRuolo(InputStream in) throws DeserializerException {
		return (IdSoggettoRuolo) this.xmlToObj(in, IdSoggettoRuolo.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.commons.search.IdSoggettoRuolo}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.IdSoggettoRuolo}
	 * @return Object type {@link org.openspcoop2.core.commons.search.IdSoggettoRuolo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdSoggettoRuolo readIdSoggettoRuolo(byte[] in) throws DeserializerException {
		return (IdSoggettoRuolo) this.xmlToObj(in, IdSoggettoRuolo.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.commons.search.IdSoggettoRuolo}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.commons.search.IdSoggettoRuolo}
	 * @return Object type {@link org.openspcoop2.core.commons.search.IdSoggettoRuolo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdSoggettoRuolo readIdSoggettoRuoloFromString(String in) throws DeserializerException {
		return (IdSoggettoRuolo) this.xmlToObj(in.getBytes(), IdSoggettoRuolo.class);
	}	
	
	
	

}
