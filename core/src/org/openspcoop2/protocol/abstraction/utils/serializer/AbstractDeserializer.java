/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it).
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
package org.openspcoop2.protocol.abstraction.utils.serializer;

import org.openspcoop2.generic_project.exception.DeserializerException;

import org.openspcoop2.protocol.abstraction.Soggetto;
import org.openspcoop2.protocol.abstraction.RiferimentoSoggetto;
import org.openspcoop2.protocol.abstraction.SoggettoNotExistsBehaviour;
import org.openspcoop2.protocol.abstraction.CredenzialiInvocazioneBasic;
import org.openspcoop2.protocol.abstraction.RiferimentoServizioApplicativoFruitore;
import org.openspcoop2.protocol.abstraction.DatiApplicativiFruizione;
import org.openspcoop2.protocol.abstraction.IdentificatoreAccordo;
import org.openspcoop2.protocol.abstraction.IdentificatoreServizio;
import org.openspcoop2.protocol.abstraction.DatiServizio;
import org.openspcoop2.protocol.abstraction.Fruitori;
import org.openspcoop2.protocol.abstraction.RiferimentoServizioApplicativoErogatore;
import org.openspcoop2.protocol.abstraction.DatiApplicativiErogazione;
import org.openspcoop2.protocol.abstraction.RiferimentoAccordoServizioParteSpecifica;
import org.openspcoop2.protocol.abstraction.DatiFruizione;
import org.openspcoop2.protocol.abstraction.RiferimentoAccordoServizioParteComune;
import org.openspcoop2.protocol.abstraction.Erogazione;
import org.openspcoop2.protocol.abstraction.Fruizione;

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
	 Object: Soggetto
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.protocol.abstraction.Soggetto}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.abstraction.Soggetto}
	 * @return Object type {@link org.openspcoop2.protocol.abstraction.Soggetto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Soggetto readSoggetto(String fileName) throws DeserializerException {
		return (Soggetto) this.xmlToObj(fileName, Soggetto.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.protocol.abstraction.Soggetto}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.abstraction.Soggetto}
	 * @return Object type {@link org.openspcoop2.protocol.abstraction.Soggetto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Soggetto readSoggetto(File file) throws DeserializerException {
		return (Soggetto) this.xmlToObj(file, Soggetto.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.protocol.abstraction.Soggetto}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.protocol.abstraction.Soggetto}
	 * @return Object type {@link org.openspcoop2.protocol.abstraction.Soggetto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Soggetto readSoggetto(InputStream in) throws DeserializerException {
		return (Soggetto) this.xmlToObj(in, Soggetto.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.protocol.abstraction.Soggetto}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.protocol.abstraction.Soggetto}
	 * @return Object type {@link org.openspcoop2.protocol.abstraction.Soggetto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Soggetto readSoggetto(byte[] in) throws DeserializerException {
		return (Soggetto) this.xmlToObj(in, Soggetto.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.protocol.abstraction.Soggetto}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.protocol.abstraction.Soggetto}
	 * @return Object type {@link org.openspcoop2.protocol.abstraction.Soggetto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Soggetto readSoggettoFromString(String in) throws DeserializerException {
		return (Soggetto) this.xmlToObj(in.getBytes(), Soggetto.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: RiferimentoSoggetto
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.protocol.abstraction.RiferimentoSoggetto}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.abstraction.RiferimentoSoggetto}
	 * @return Object type {@link org.openspcoop2.protocol.abstraction.RiferimentoSoggetto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RiferimentoSoggetto readRiferimentoSoggetto(String fileName) throws DeserializerException {
		return (RiferimentoSoggetto) this.xmlToObj(fileName, RiferimentoSoggetto.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.protocol.abstraction.RiferimentoSoggetto}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.abstraction.RiferimentoSoggetto}
	 * @return Object type {@link org.openspcoop2.protocol.abstraction.RiferimentoSoggetto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RiferimentoSoggetto readRiferimentoSoggetto(File file) throws DeserializerException {
		return (RiferimentoSoggetto) this.xmlToObj(file, RiferimentoSoggetto.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.protocol.abstraction.RiferimentoSoggetto}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.protocol.abstraction.RiferimentoSoggetto}
	 * @return Object type {@link org.openspcoop2.protocol.abstraction.RiferimentoSoggetto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RiferimentoSoggetto readRiferimentoSoggetto(InputStream in) throws DeserializerException {
		return (RiferimentoSoggetto) this.xmlToObj(in, RiferimentoSoggetto.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.protocol.abstraction.RiferimentoSoggetto}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.protocol.abstraction.RiferimentoSoggetto}
	 * @return Object type {@link org.openspcoop2.protocol.abstraction.RiferimentoSoggetto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RiferimentoSoggetto readRiferimentoSoggetto(byte[] in) throws DeserializerException {
		return (RiferimentoSoggetto) this.xmlToObj(in, RiferimentoSoggetto.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.protocol.abstraction.RiferimentoSoggetto}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.protocol.abstraction.RiferimentoSoggetto}
	 * @return Object type {@link org.openspcoop2.protocol.abstraction.RiferimentoSoggetto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RiferimentoSoggetto readRiferimentoSoggettoFromString(String in) throws DeserializerException {
		return (RiferimentoSoggetto) this.xmlToObj(in.getBytes(), RiferimentoSoggetto.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: SoggettoNotExistsBehaviour
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.protocol.abstraction.SoggettoNotExistsBehaviour}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.abstraction.SoggettoNotExistsBehaviour}
	 * @return Object type {@link org.openspcoop2.protocol.abstraction.SoggettoNotExistsBehaviour}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SoggettoNotExistsBehaviour readSoggettoNotExistsBehaviour(String fileName) throws DeserializerException {
		return (SoggettoNotExistsBehaviour) this.xmlToObj(fileName, SoggettoNotExistsBehaviour.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.protocol.abstraction.SoggettoNotExistsBehaviour}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.abstraction.SoggettoNotExistsBehaviour}
	 * @return Object type {@link org.openspcoop2.protocol.abstraction.SoggettoNotExistsBehaviour}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SoggettoNotExistsBehaviour readSoggettoNotExistsBehaviour(File file) throws DeserializerException {
		return (SoggettoNotExistsBehaviour) this.xmlToObj(file, SoggettoNotExistsBehaviour.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.protocol.abstraction.SoggettoNotExistsBehaviour}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.protocol.abstraction.SoggettoNotExistsBehaviour}
	 * @return Object type {@link org.openspcoop2.protocol.abstraction.SoggettoNotExistsBehaviour}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SoggettoNotExistsBehaviour readSoggettoNotExistsBehaviour(InputStream in) throws DeserializerException {
		return (SoggettoNotExistsBehaviour) this.xmlToObj(in, SoggettoNotExistsBehaviour.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.protocol.abstraction.SoggettoNotExistsBehaviour}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.protocol.abstraction.SoggettoNotExistsBehaviour}
	 * @return Object type {@link org.openspcoop2.protocol.abstraction.SoggettoNotExistsBehaviour}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SoggettoNotExistsBehaviour readSoggettoNotExistsBehaviour(byte[] in) throws DeserializerException {
		return (SoggettoNotExistsBehaviour) this.xmlToObj(in, SoggettoNotExistsBehaviour.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.protocol.abstraction.SoggettoNotExistsBehaviour}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.protocol.abstraction.SoggettoNotExistsBehaviour}
	 * @return Object type {@link org.openspcoop2.protocol.abstraction.SoggettoNotExistsBehaviour}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SoggettoNotExistsBehaviour readSoggettoNotExistsBehaviourFromString(String in) throws DeserializerException {
		return (SoggettoNotExistsBehaviour) this.xmlToObj(in.getBytes(), SoggettoNotExistsBehaviour.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: CredenzialiInvocazioneBasic
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.protocol.abstraction.CredenzialiInvocazioneBasic}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.abstraction.CredenzialiInvocazioneBasic}
	 * @return Object type {@link org.openspcoop2.protocol.abstraction.CredenzialiInvocazioneBasic}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public CredenzialiInvocazioneBasic readCredenzialiInvocazioneBasic(String fileName) throws DeserializerException {
		return (CredenzialiInvocazioneBasic) this.xmlToObj(fileName, CredenzialiInvocazioneBasic.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.protocol.abstraction.CredenzialiInvocazioneBasic}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.abstraction.CredenzialiInvocazioneBasic}
	 * @return Object type {@link org.openspcoop2.protocol.abstraction.CredenzialiInvocazioneBasic}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public CredenzialiInvocazioneBasic readCredenzialiInvocazioneBasic(File file) throws DeserializerException {
		return (CredenzialiInvocazioneBasic) this.xmlToObj(file, CredenzialiInvocazioneBasic.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.protocol.abstraction.CredenzialiInvocazioneBasic}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.protocol.abstraction.CredenzialiInvocazioneBasic}
	 * @return Object type {@link org.openspcoop2.protocol.abstraction.CredenzialiInvocazioneBasic}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public CredenzialiInvocazioneBasic readCredenzialiInvocazioneBasic(InputStream in) throws DeserializerException {
		return (CredenzialiInvocazioneBasic) this.xmlToObj(in, CredenzialiInvocazioneBasic.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.protocol.abstraction.CredenzialiInvocazioneBasic}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.protocol.abstraction.CredenzialiInvocazioneBasic}
	 * @return Object type {@link org.openspcoop2.protocol.abstraction.CredenzialiInvocazioneBasic}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public CredenzialiInvocazioneBasic readCredenzialiInvocazioneBasic(byte[] in) throws DeserializerException {
		return (CredenzialiInvocazioneBasic) this.xmlToObj(in, CredenzialiInvocazioneBasic.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.protocol.abstraction.CredenzialiInvocazioneBasic}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.protocol.abstraction.CredenzialiInvocazioneBasic}
	 * @return Object type {@link org.openspcoop2.protocol.abstraction.CredenzialiInvocazioneBasic}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public CredenzialiInvocazioneBasic readCredenzialiInvocazioneBasicFromString(String in) throws DeserializerException {
		return (CredenzialiInvocazioneBasic) this.xmlToObj(in.getBytes(), CredenzialiInvocazioneBasic.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: RiferimentoServizioApplicativoFruitore
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.protocol.abstraction.RiferimentoServizioApplicativoFruitore}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.abstraction.RiferimentoServizioApplicativoFruitore}
	 * @return Object type {@link org.openspcoop2.protocol.abstraction.RiferimentoServizioApplicativoFruitore}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RiferimentoServizioApplicativoFruitore readRiferimentoServizioApplicativoFruitore(String fileName) throws DeserializerException {
		return (RiferimentoServizioApplicativoFruitore) this.xmlToObj(fileName, RiferimentoServizioApplicativoFruitore.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.protocol.abstraction.RiferimentoServizioApplicativoFruitore}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.abstraction.RiferimentoServizioApplicativoFruitore}
	 * @return Object type {@link org.openspcoop2.protocol.abstraction.RiferimentoServizioApplicativoFruitore}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RiferimentoServizioApplicativoFruitore readRiferimentoServizioApplicativoFruitore(File file) throws DeserializerException {
		return (RiferimentoServizioApplicativoFruitore) this.xmlToObj(file, RiferimentoServizioApplicativoFruitore.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.protocol.abstraction.RiferimentoServizioApplicativoFruitore}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.protocol.abstraction.RiferimentoServizioApplicativoFruitore}
	 * @return Object type {@link org.openspcoop2.protocol.abstraction.RiferimentoServizioApplicativoFruitore}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RiferimentoServizioApplicativoFruitore readRiferimentoServizioApplicativoFruitore(InputStream in) throws DeserializerException {
		return (RiferimentoServizioApplicativoFruitore) this.xmlToObj(in, RiferimentoServizioApplicativoFruitore.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.protocol.abstraction.RiferimentoServizioApplicativoFruitore}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.protocol.abstraction.RiferimentoServizioApplicativoFruitore}
	 * @return Object type {@link org.openspcoop2.protocol.abstraction.RiferimentoServizioApplicativoFruitore}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RiferimentoServizioApplicativoFruitore readRiferimentoServizioApplicativoFruitore(byte[] in) throws DeserializerException {
		return (RiferimentoServizioApplicativoFruitore) this.xmlToObj(in, RiferimentoServizioApplicativoFruitore.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.protocol.abstraction.RiferimentoServizioApplicativoFruitore}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.protocol.abstraction.RiferimentoServizioApplicativoFruitore}
	 * @return Object type {@link org.openspcoop2.protocol.abstraction.RiferimentoServizioApplicativoFruitore}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RiferimentoServizioApplicativoFruitore readRiferimentoServizioApplicativoFruitoreFromString(String in) throws DeserializerException {
		return (RiferimentoServizioApplicativoFruitore) this.xmlToObj(in.getBytes(), RiferimentoServizioApplicativoFruitore.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: DatiApplicativiFruizione
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.protocol.abstraction.DatiApplicativiFruizione}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.abstraction.DatiApplicativiFruizione}
	 * @return Object type {@link org.openspcoop2.protocol.abstraction.DatiApplicativiFruizione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiApplicativiFruizione readDatiApplicativiFruizione(String fileName) throws DeserializerException {
		return (DatiApplicativiFruizione) this.xmlToObj(fileName, DatiApplicativiFruizione.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.protocol.abstraction.DatiApplicativiFruizione}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.abstraction.DatiApplicativiFruizione}
	 * @return Object type {@link org.openspcoop2.protocol.abstraction.DatiApplicativiFruizione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiApplicativiFruizione readDatiApplicativiFruizione(File file) throws DeserializerException {
		return (DatiApplicativiFruizione) this.xmlToObj(file, DatiApplicativiFruizione.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.protocol.abstraction.DatiApplicativiFruizione}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.protocol.abstraction.DatiApplicativiFruizione}
	 * @return Object type {@link org.openspcoop2.protocol.abstraction.DatiApplicativiFruizione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiApplicativiFruizione readDatiApplicativiFruizione(InputStream in) throws DeserializerException {
		return (DatiApplicativiFruizione) this.xmlToObj(in, DatiApplicativiFruizione.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.protocol.abstraction.DatiApplicativiFruizione}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.protocol.abstraction.DatiApplicativiFruizione}
	 * @return Object type {@link org.openspcoop2.protocol.abstraction.DatiApplicativiFruizione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiApplicativiFruizione readDatiApplicativiFruizione(byte[] in) throws DeserializerException {
		return (DatiApplicativiFruizione) this.xmlToObj(in, DatiApplicativiFruizione.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.protocol.abstraction.DatiApplicativiFruizione}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.protocol.abstraction.DatiApplicativiFruizione}
	 * @return Object type {@link org.openspcoop2.protocol.abstraction.DatiApplicativiFruizione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiApplicativiFruizione readDatiApplicativiFruizioneFromString(String in) throws DeserializerException {
		return (DatiApplicativiFruizione) this.xmlToObj(in.getBytes(), DatiApplicativiFruizione.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: IdentificatoreAccordo
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.protocol.abstraction.IdentificatoreAccordo}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.abstraction.IdentificatoreAccordo}
	 * @return Object type {@link org.openspcoop2.protocol.abstraction.IdentificatoreAccordo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdentificatoreAccordo readIdentificatoreAccordo(String fileName) throws DeserializerException {
		return (IdentificatoreAccordo) this.xmlToObj(fileName, IdentificatoreAccordo.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.protocol.abstraction.IdentificatoreAccordo}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.abstraction.IdentificatoreAccordo}
	 * @return Object type {@link org.openspcoop2.protocol.abstraction.IdentificatoreAccordo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdentificatoreAccordo readIdentificatoreAccordo(File file) throws DeserializerException {
		return (IdentificatoreAccordo) this.xmlToObj(file, IdentificatoreAccordo.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.protocol.abstraction.IdentificatoreAccordo}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.protocol.abstraction.IdentificatoreAccordo}
	 * @return Object type {@link org.openspcoop2.protocol.abstraction.IdentificatoreAccordo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdentificatoreAccordo readIdentificatoreAccordo(InputStream in) throws DeserializerException {
		return (IdentificatoreAccordo) this.xmlToObj(in, IdentificatoreAccordo.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.protocol.abstraction.IdentificatoreAccordo}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.protocol.abstraction.IdentificatoreAccordo}
	 * @return Object type {@link org.openspcoop2.protocol.abstraction.IdentificatoreAccordo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdentificatoreAccordo readIdentificatoreAccordo(byte[] in) throws DeserializerException {
		return (IdentificatoreAccordo) this.xmlToObj(in, IdentificatoreAccordo.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.protocol.abstraction.IdentificatoreAccordo}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.protocol.abstraction.IdentificatoreAccordo}
	 * @return Object type {@link org.openspcoop2.protocol.abstraction.IdentificatoreAccordo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdentificatoreAccordo readIdentificatoreAccordoFromString(String in) throws DeserializerException {
		return (IdentificatoreAccordo) this.xmlToObj(in.getBytes(), IdentificatoreAccordo.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: IdentificatoreServizio
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.protocol.abstraction.IdentificatoreServizio}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.abstraction.IdentificatoreServizio}
	 * @return Object type {@link org.openspcoop2.protocol.abstraction.IdentificatoreServizio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdentificatoreServizio readIdentificatoreServizio(String fileName) throws DeserializerException {
		return (IdentificatoreServizio) this.xmlToObj(fileName, IdentificatoreServizio.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.protocol.abstraction.IdentificatoreServizio}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.abstraction.IdentificatoreServizio}
	 * @return Object type {@link org.openspcoop2.protocol.abstraction.IdentificatoreServizio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdentificatoreServizio readIdentificatoreServizio(File file) throws DeserializerException {
		return (IdentificatoreServizio) this.xmlToObj(file, IdentificatoreServizio.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.protocol.abstraction.IdentificatoreServizio}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.protocol.abstraction.IdentificatoreServizio}
	 * @return Object type {@link org.openspcoop2.protocol.abstraction.IdentificatoreServizio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdentificatoreServizio readIdentificatoreServizio(InputStream in) throws DeserializerException {
		return (IdentificatoreServizio) this.xmlToObj(in, IdentificatoreServizio.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.protocol.abstraction.IdentificatoreServizio}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.protocol.abstraction.IdentificatoreServizio}
	 * @return Object type {@link org.openspcoop2.protocol.abstraction.IdentificatoreServizio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdentificatoreServizio readIdentificatoreServizio(byte[] in) throws DeserializerException {
		return (IdentificatoreServizio) this.xmlToObj(in, IdentificatoreServizio.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.protocol.abstraction.IdentificatoreServizio}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.protocol.abstraction.IdentificatoreServizio}
	 * @return Object type {@link org.openspcoop2.protocol.abstraction.IdentificatoreServizio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdentificatoreServizio readIdentificatoreServizioFromString(String in) throws DeserializerException {
		return (IdentificatoreServizio) this.xmlToObj(in.getBytes(), IdentificatoreServizio.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: DatiServizio
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.protocol.abstraction.DatiServizio}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.abstraction.DatiServizio}
	 * @return Object type {@link org.openspcoop2.protocol.abstraction.DatiServizio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiServizio readDatiServizio(String fileName) throws DeserializerException {
		return (DatiServizio) this.xmlToObj(fileName, DatiServizio.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.protocol.abstraction.DatiServizio}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.abstraction.DatiServizio}
	 * @return Object type {@link org.openspcoop2.protocol.abstraction.DatiServizio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiServizio readDatiServizio(File file) throws DeserializerException {
		return (DatiServizio) this.xmlToObj(file, DatiServizio.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.protocol.abstraction.DatiServizio}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.protocol.abstraction.DatiServizio}
	 * @return Object type {@link org.openspcoop2.protocol.abstraction.DatiServizio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiServizio readDatiServizio(InputStream in) throws DeserializerException {
		return (DatiServizio) this.xmlToObj(in, DatiServizio.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.protocol.abstraction.DatiServizio}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.protocol.abstraction.DatiServizio}
	 * @return Object type {@link org.openspcoop2.protocol.abstraction.DatiServizio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiServizio readDatiServizio(byte[] in) throws DeserializerException {
		return (DatiServizio) this.xmlToObj(in, DatiServizio.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.protocol.abstraction.DatiServizio}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.protocol.abstraction.DatiServizio}
	 * @return Object type {@link org.openspcoop2.protocol.abstraction.DatiServizio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiServizio readDatiServizioFromString(String in) throws DeserializerException {
		return (DatiServizio) this.xmlToObj(in.getBytes(), DatiServizio.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: Fruitori
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.protocol.abstraction.Fruitori}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.abstraction.Fruitori}
	 * @return Object type {@link org.openspcoop2.protocol.abstraction.Fruitori}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Fruitori readFruitori(String fileName) throws DeserializerException {
		return (Fruitori) this.xmlToObj(fileName, Fruitori.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.protocol.abstraction.Fruitori}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.abstraction.Fruitori}
	 * @return Object type {@link org.openspcoop2.protocol.abstraction.Fruitori}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Fruitori readFruitori(File file) throws DeserializerException {
		return (Fruitori) this.xmlToObj(file, Fruitori.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.protocol.abstraction.Fruitori}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.protocol.abstraction.Fruitori}
	 * @return Object type {@link org.openspcoop2.protocol.abstraction.Fruitori}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Fruitori readFruitori(InputStream in) throws DeserializerException {
		return (Fruitori) this.xmlToObj(in, Fruitori.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.protocol.abstraction.Fruitori}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.protocol.abstraction.Fruitori}
	 * @return Object type {@link org.openspcoop2.protocol.abstraction.Fruitori}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Fruitori readFruitori(byte[] in) throws DeserializerException {
		return (Fruitori) this.xmlToObj(in, Fruitori.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.protocol.abstraction.Fruitori}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.protocol.abstraction.Fruitori}
	 * @return Object type {@link org.openspcoop2.protocol.abstraction.Fruitori}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Fruitori readFruitoriFromString(String in) throws DeserializerException {
		return (Fruitori) this.xmlToObj(in.getBytes(), Fruitori.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: RiferimentoServizioApplicativoErogatore
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.protocol.abstraction.RiferimentoServizioApplicativoErogatore}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.abstraction.RiferimentoServizioApplicativoErogatore}
	 * @return Object type {@link org.openspcoop2.protocol.abstraction.RiferimentoServizioApplicativoErogatore}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RiferimentoServizioApplicativoErogatore readRiferimentoServizioApplicativoErogatore(String fileName) throws DeserializerException {
		return (RiferimentoServizioApplicativoErogatore) this.xmlToObj(fileName, RiferimentoServizioApplicativoErogatore.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.protocol.abstraction.RiferimentoServizioApplicativoErogatore}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.abstraction.RiferimentoServizioApplicativoErogatore}
	 * @return Object type {@link org.openspcoop2.protocol.abstraction.RiferimentoServizioApplicativoErogatore}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RiferimentoServizioApplicativoErogatore readRiferimentoServizioApplicativoErogatore(File file) throws DeserializerException {
		return (RiferimentoServizioApplicativoErogatore) this.xmlToObj(file, RiferimentoServizioApplicativoErogatore.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.protocol.abstraction.RiferimentoServizioApplicativoErogatore}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.protocol.abstraction.RiferimentoServizioApplicativoErogatore}
	 * @return Object type {@link org.openspcoop2.protocol.abstraction.RiferimentoServizioApplicativoErogatore}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RiferimentoServizioApplicativoErogatore readRiferimentoServizioApplicativoErogatore(InputStream in) throws DeserializerException {
		return (RiferimentoServizioApplicativoErogatore) this.xmlToObj(in, RiferimentoServizioApplicativoErogatore.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.protocol.abstraction.RiferimentoServizioApplicativoErogatore}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.protocol.abstraction.RiferimentoServizioApplicativoErogatore}
	 * @return Object type {@link org.openspcoop2.protocol.abstraction.RiferimentoServizioApplicativoErogatore}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RiferimentoServizioApplicativoErogatore readRiferimentoServizioApplicativoErogatore(byte[] in) throws DeserializerException {
		return (RiferimentoServizioApplicativoErogatore) this.xmlToObj(in, RiferimentoServizioApplicativoErogatore.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.protocol.abstraction.RiferimentoServizioApplicativoErogatore}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.protocol.abstraction.RiferimentoServizioApplicativoErogatore}
	 * @return Object type {@link org.openspcoop2.protocol.abstraction.RiferimentoServizioApplicativoErogatore}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RiferimentoServizioApplicativoErogatore readRiferimentoServizioApplicativoErogatoreFromString(String in) throws DeserializerException {
		return (RiferimentoServizioApplicativoErogatore) this.xmlToObj(in.getBytes(), RiferimentoServizioApplicativoErogatore.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: DatiApplicativiErogazione
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.protocol.abstraction.DatiApplicativiErogazione}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.abstraction.DatiApplicativiErogazione}
	 * @return Object type {@link org.openspcoop2.protocol.abstraction.DatiApplicativiErogazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiApplicativiErogazione readDatiApplicativiErogazione(String fileName) throws DeserializerException {
		return (DatiApplicativiErogazione) this.xmlToObj(fileName, DatiApplicativiErogazione.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.protocol.abstraction.DatiApplicativiErogazione}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.abstraction.DatiApplicativiErogazione}
	 * @return Object type {@link org.openspcoop2.protocol.abstraction.DatiApplicativiErogazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiApplicativiErogazione readDatiApplicativiErogazione(File file) throws DeserializerException {
		return (DatiApplicativiErogazione) this.xmlToObj(file, DatiApplicativiErogazione.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.protocol.abstraction.DatiApplicativiErogazione}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.protocol.abstraction.DatiApplicativiErogazione}
	 * @return Object type {@link org.openspcoop2.protocol.abstraction.DatiApplicativiErogazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiApplicativiErogazione readDatiApplicativiErogazione(InputStream in) throws DeserializerException {
		return (DatiApplicativiErogazione) this.xmlToObj(in, DatiApplicativiErogazione.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.protocol.abstraction.DatiApplicativiErogazione}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.protocol.abstraction.DatiApplicativiErogazione}
	 * @return Object type {@link org.openspcoop2.protocol.abstraction.DatiApplicativiErogazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiApplicativiErogazione readDatiApplicativiErogazione(byte[] in) throws DeserializerException {
		return (DatiApplicativiErogazione) this.xmlToObj(in, DatiApplicativiErogazione.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.protocol.abstraction.DatiApplicativiErogazione}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.protocol.abstraction.DatiApplicativiErogazione}
	 * @return Object type {@link org.openspcoop2.protocol.abstraction.DatiApplicativiErogazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiApplicativiErogazione readDatiApplicativiErogazioneFromString(String in) throws DeserializerException {
		return (DatiApplicativiErogazione) this.xmlToObj(in.getBytes(), DatiApplicativiErogazione.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: RiferimentoAccordoServizioParteSpecifica
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.protocol.abstraction.RiferimentoAccordoServizioParteSpecifica}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.abstraction.RiferimentoAccordoServizioParteSpecifica}
	 * @return Object type {@link org.openspcoop2.protocol.abstraction.RiferimentoAccordoServizioParteSpecifica}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RiferimentoAccordoServizioParteSpecifica readRiferimentoAccordoServizioParteSpecifica(String fileName) throws DeserializerException {
		return (RiferimentoAccordoServizioParteSpecifica) this.xmlToObj(fileName, RiferimentoAccordoServizioParteSpecifica.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.protocol.abstraction.RiferimentoAccordoServizioParteSpecifica}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.abstraction.RiferimentoAccordoServizioParteSpecifica}
	 * @return Object type {@link org.openspcoop2.protocol.abstraction.RiferimentoAccordoServizioParteSpecifica}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RiferimentoAccordoServizioParteSpecifica readRiferimentoAccordoServizioParteSpecifica(File file) throws DeserializerException {
		return (RiferimentoAccordoServizioParteSpecifica) this.xmlToObj(file, RiferimentoAccordoServizioParteSpecifica.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.protocol.abstraction.RiferimentoAccordoServizioParteSpecifica}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.protocol.abstraction.RiferimentoAccordoServizioParteSpecifica}
	 * @return Object type {@link org.openspcoop2.protocol.abstraction.RiferimentoAccordoServizioParteSpecifica}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RiferimentoAccordoServizioParteSpecifica readRiferimentoAccordoServizioParteSpecifica(InputStream in) throws DeserializerException {
		return (RiferimentoAccordoServizioParteSpecifica) this.xmlToObj(in, RiferimentoAccordoServizioParteSpecifica.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.protocol.abstraction.RiferimentoAccordoServizioParteSpecifica}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.protocol.abstraction.RiferimentoAccordoServizioParteSpecifica}
	 * @return Object type {@link org.openspcoop2.protocol.abstraction.RiferimentoAccordoServizioParteSpecifica}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RiferimentoAccordoServizioParteSpecifica readRiferimentoAccordoServizioParteSpecifica(byte[] in) throws DeserializerException {
		return (RiferimentoAccordoServizioParteSpecifica) this.xmlToObj(in, RiferimentoAccordoServizioParteSpecifica.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.protocol.abstraction.RiferimentoAccordoServizioParteSpecifica}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.protocol.abstraction.RiferimentoAccordoServizioParteSpecifica}
	 * @return Object type {@link org.openspcoop2.protocol.abstraction.RiferimentoAccordoServizioParteSpecifica}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RiferimentoAccordoServizioParteSpecifica readRiferimentoAccordoServizioParteSpecificaFromString(String in) throws DeserializerException {
		return (RiferimentoAccordoServizioParteSpecifica) this.xmlToObj(in.getBytes(), RiferimentoAccordoServizioParteSpecifica.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: DatiFruizione
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.protocol.abstraction.DatiFruizione}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.abstraction.DatiFruizione}
	 * @return Object type {@link org.openspcoop2.protocol.abstraction.DatiFruizione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiFruizione readDatiFruizione(String fileName) throws DeserializerException {
		return (DatiFruizione) this.xmlToObj(fileName, DatiFruizione.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.protocol.abstraction.DatiFruizione}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.abstraction.DatiFruizione}
	 * @return Object type {@link org.openspcoop2.protocol.abstraction.DatiFruizione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiFruizione readDatiFruizione(File file) throws DeserializerException {
		return (DatiFruizione) this.xmlToObj(file, DatiFruizione.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.protocol.abstraction.DatiFruizione}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.protocol.abstraction.DatiFruizione}
	 * @return Object type {@link org.openspcoop2.protocol.abstraction.DatiFruizione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiFruizione readDatiFruizione(InputStream in) throws DeserializerException {
		return (DatiFruizione) this.xmlToObj(in, DatiFruizione.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.protocol.abstraction.DatiFruizione}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.protocol.abstraction.DatiFruizione}
	 * @return Object type {@link org.openspcoop2.protocol.abstraction.DatiFruizione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiFruizione readDatiFruizione(byte[] in) throws DeserializerException {
		return (DatiFruizione) this.xmlToObj(in, DatiFruizione.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.protocol.abstraction.DatiFruizione}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.protocol.abstraction.DatiFruizione}
	 * @return Object type {@link org.openspcoop2.protocol.abstraction.DatiFruizione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DatiFruizione readDatiFruizioneFromString(String in) throws DeserializerException {
		return (DatiFruizione) this.xmlToObj(in.getBytes(), DatiFruizione.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: RiferimentoAccordoServizioParteComune
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.protocol.abstraction.RiferimentoAccordoServizioParteComune}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.abstraction.RiferimentoAccordoServizioParteComune}
	 * @return Object type {@link org.openspcoop2.protocol.abstraction.RiferimentoAccordoServizioParteComune}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RiferimentoAccordoServizioParteComune readRiferimentoAccordoServizioParteComune(String fileName) throws DeserializerException {
		return (RiferimentoAccordoServizioParteComune) this.xmlToObj(fileName, RiferimentoAccordoServizioParteComune.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.protocol.abstraction.RiferimentoAccordoServizioParteComune}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.abstraction.RiferimentoAccordoServizioParteComune}
	 * @return Object type {@link org.openspcoop2.protocol.abstraction.RiferimentoAccordoServizioParteComune}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RiferimentoAccordoServizioParteComune readRiferimentoAccordoServizioParteComune(File file) throws DeserializerException {
		return (RiferimentoAccordoServizioParteComune) this.xmlToObj(file, RiferimentoAccordoServizioParteComune.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.protocol.abstraction.RiferimentoAccordoServizioParteComune}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.protocol.abstraction.RiferimentoAccordoServizioParteComune}
	 * @return Object type {@link org.openspcoop2.protocol.abstraction.RiferimentoAccordoServizioParteComune}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RiferimentoAccordoServizioParteComune readRiferimentoAccordoServizioParteComune(InputStream in) throws DeserializerException {
		return (RiferimentoAccordoServizioParteComune) this.xmlToObj(in, RiferimentoAccordoServizioParteComune.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.protocol.abstraction.RiferimentoAccordoServizioParteComune}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.protocol.abstraction.RiferimentoAccordoServizioParteComune}
	 * @return Object type {@link org.openspcoop2.protocol.abstraction.RiferimentoAccordoServizioParteComune}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RiferimentoAccordoServizioParteComune readRiferimentoAccordoServizioParteComune(byte[] in) throws DeserializerException {
		return (RiferimentoAccordoServizioParteComune) this.xmlToObj(in, RiferimentoAccordoServizioParteComune.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.protocol.abstraction.RiferimentoAccordoServizioParteComune}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.protocol.abstraction.RiferimentoAccordoServizioParteComune}
	 * @return Object type {@link org.openspcoop2.protocol.abstraction.RiferimentoAccordoServizioParteComune}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RiferimentoAccordoServizioParteComune readRiferimentoAccordoServizioParteComuneFromString(String in) throws DeserializerException {
		return (RiferimentoAccordoServizioParteComune) this.xmlToObj(in.getBytes(), RiferimentoAccordoServizioParteComune.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: erogazione
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.protocol.abstraction.Erogazione}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.abstraction.Erogazione}
	 * @return Object type {@link org.openspcoop2.protocol.abstraction.Erogazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Erogazione readErogazione(String fileName) throws DeserializerException {
		return (Erogazione) this.xmlToObj(fileName, Erogazione.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.protocol.abstraction.Erogazione}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.abstraction.Erogazione}
	 * @return Object type {@link org.openspcoop2.protocol.abstraction.Erogazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Erogazione readErogazione(File file) throws DeserializerException {
		return (Erogazione) this.xmlToObj(file, Erogazione.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.protocol.abstraction.Erogazione}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.protocol.abstraction.Erogazione}
	 * @return Object type {@link org.openspcoop2.protocol.abstraction.Erogazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Erogazione readErogazione(InputStream in) throws DeserializerException {
		return (Erogazione) this.xmlToObj(in, Erogazione.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.protocol.abstraction.Erogazione}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.protocol.abstraction.Erogazione}
	 * @return Object type {@link org.openspcoop2.protocol.abstraction.Erogazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Erogazione readErogazione(byte[] in) throws DeserializerException {
		return (Erogazione) this.xmlToObj(in, Erogazione.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.protocol.abstraction.Erogazione}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.protocol.abstraction.Erogazione}
	 * @return Object type {@link org.openspcoop2.protocol.abstraction.Erogazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Erogazione readErogazioneFromString(String in) throws DeserializerException {
		return (Erogazione) this.xmlToObj(in.getBytes(), Erogazione.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: fruizione
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.protocol.abstraction.Fruizione}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.abstraction.Fruizione}
	 * @return Object type {@link org.openspcoop2.protocol.abstraction.Fruizione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Fruizione readFruizione(String fileName) throws DeserializerException {
		return (Fruizione) this.xmlToObj(fileName, Fruizione.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.protocol.abstraction.Fruizione}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.abstraction.Fruizione}
	 * @return Object type {@link org.openspcoop2.protocol.abstraction.Fruizione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Fruizione readFruizione(File file) throws DeserializerException {
		return (Fruizione) this.xmlToObj(file, Fruizione.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.protocol.abstraction.Fruizione}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.protocol.abstraction.Fruizione}
	 * @return Object type {@link org.openspcoop2.protocol.abstraction.Fruizione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Fruizione readFruizione(InputStream in) throws DeserializerException {
		return (Fruizione) this.xmlToObj(in, Fruizione.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.protocol.abstraction.Fruizione}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.protocol.abstraction.Fruizione}
	 * @return Object type {@link org.openspcoop2.protocol.abstraction.Fruizione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Fruizione readFruizione(byte[] in) throws DeserializerException {
		return (Fruizione) this.xmlToObj(in, Fruizione.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.protocol.abstraction.Fruizione}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.protocol.abstraction.Fruizione}
	 * @return Object type {@link org.openspcoop2.protocol.abstraction.Fruizione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Fruizione readFruizioneFromString(String in) throws DeserializerException {
		return (Fruizione) this.xmlToObj(in.getBytes(), Fruizione.class);
	}	
	
	
	

}
