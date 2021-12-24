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
package org.openspcoop2.protocol.information_missing.utils.serializer;

import org.openspcoop2.generic_project.exception.DeserializerException;

import org.openspcoop2.protocol.information_missing.ReplaceMatchFieldType;
import org.openspcoop2.protocol.information_missing.ReplaceMatchType;
import org.openspcoop2.protocol.information_missing.ConditionsType;
import org.openspcoop2.protocol.information_missing.Fruitore;
import org.openspcoop2.protocol.information_missing.ReplaceFruitoreMatchType;
import org.openspcoop2.protocol.information_missing.PortaDelegata;
import org.openspcoop2.protocol.information_missing.ProprietaRequisitoInput;
import org.openspcoop2.protocol.information_missing.ProprietaDefault;
import org.openspcoop2.protocol.information_missing.AccordoServizioParteSpecifica;
import org.openspcoop2.protocol.information_missing.Default;
import org.openspcoop2.protocol.information_missing.DescriptionType;
import org.openspcoop2.protocol.information_missing.ConditionType;
import org.openspcoop2.protocol.information_missing.AccordoServizioParteComune;
import org.openspcoop2.protocol.information_missing.PortaApplicativa;
import org.openspcoop2.protocol.information_missing.ServizioApplicativo;
import org.openspcoop2.protocol.information_missing.RequisitoProtocollo;
import org.openspcoop2.protocol.information_missing.AccordoCooperazione;
import org.openspcoop2.protocol.information_missing.Requisiti;
import org.openspcoop2.protocol.information_missing.RequisitoInput;
import org.openspcoop2.protocol.information_missing.Openspcoop2;
import org.openspcoop2.protocol.information_missing.Operazione;
import org.openspcoop2.protocol.information_missing.Soggetto;
import org.openspcoop2.protocol.information_missing.Input;
import org.openspcoop2.protocol.information_missing.Proprieta;
import org.openspcoop2.protocol.information_missing.Wizard;
import org.openspcoop2.protocol.information_missing.Description;

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
	 Object: ReplaceMatchFieldType
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.protocol.information_missing.ReplaceMatchFieldType}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.ReplaceMatchFieldType}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.ReplaceMatchFieldType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ReplaceMatchFieldType readReplaceMatchFieldType(String fileName) throws DeserializerException {
		return (ReplaceMatchFieldType) this.xmlToObj(fileName, ReplaceMatchFieldType.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.protocol.information_missing.ReplaceMatchFieldType}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.ReplaceMatchFieldType}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.ReplaceMatchFieldType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ReplaceMatchFieldType readReplaceMatchFieldType(File file) throws DeserializerException {
		return (ReplaceMatchFieldType) this.xmlToObj(file, ReplaceMatchFieldType.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.protocol.information_missing.ReplaceMatchFieldType}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.ReplaceMatchFieldType}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.ReplaceMatchFieldType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ReplaceMatchFieldType readReplaceMatchFieldType(InputStream in) throws DeserializerException {
		return (ReplaceMatchFieldType) this.xmlToObj(in, ReplaceMatchFieldType.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.protocol.information_missing.ReplaceMatchFieldType}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.ReplaceMatchFieldType}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.ReplaceMatchFieldType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ReplaceMatchFieldType readReplaceMatchFieldType(byte[] in) throws DeserializerException {
		return (ReplaceMatchFieldType) this.xmlToObj(in, ReplaceMatchFieldType.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.protocol.information_missing.ReplaceMatchFieldType}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.ReplaceMatchFieldType}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.ReplaceMatchFieldType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ReplaceMatchFieldType readReplaceMatchFieldTypeFromString(String in) throws DeserializerException {
		return (ReplaceMatchFieldType) this.xmlToObj(in.getBytes(), ReplaceMatchFieldType.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: replaceMatchType
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.protocol.information_missing.ReplaceMatchType}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.ReplaceMatchType}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.ReplaceMatchType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ReplaceMatchType readReplaceMatchType(String fileName) throws DeserializerException {
		return (ReplaceMatchType) this.xmlToObj(fileName, ReplaceMatchType.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.protocol.information_missing.ReplaceMatchType}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.ReplaceMatchType}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.ReplaceMatchType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ReplaceMatchType readReplaceMatchType(File file) throws DeserializerException {
		return (ReplaceMatchType) this.xmlToObj(file, ReplaceMatchType.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.protocol.information_missing.ReplaceMatchType}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.ReplaceMatchType}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.ReplaceMatchType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ReplaceMatchType readReplaceMatchType(InputStream in) throws DeserializerException {
		return (ReplaceMatchType) this.xmlToObj(in, ReplaceMatchType.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.protocol.information_missing.ReplaceMatchType}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.ReplaceMatchType}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.ReplaceMatchType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ReplaceMatchType readReplaceMatchType(byte[] in) throws DeserializerException {
		return (ReplaceMatchType) this.xmlToObj(in, ReplaceMatchType.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.protocol.information_missing.ReplaceMatchType}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.ReplaceMatchType}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.ReplaceMatchType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ReplaceMatchType readReplaceMatchTypeFromString(String in) throws DeserializerException {
		return (ReplaceMatchType) this.xmlToObj(in.getBytes(), ReplaceMatchType.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: ConditionsType
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.protocol.information_missing.ConditionsType}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.ConditionsType}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.ConditionsType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConditionsType readConditionsType(String fileName) throws DeserializerException {
		return (ConditionsType) this.xmlToObj(fileName, ConditionsType.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.protocol.information_missing.ConditionsType}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.ConditionsType}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.ConditionsType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConditionsType readConditionsType(File file) throws DeserializerException {
		return (ConditionsType) this.xmlToObj(file, ConditionsType.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.protocol.information_missing.ConditionsType}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.ConditionsType}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.ConditionsType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConditionsType readConditionsType(InputStream in) throws DeserializerException {
		return (ConditionsType) this.xmlToObj(in, ConditionsType.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.protocol.information_missing.ConditionsType}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.ConditionsType}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.ConditionsType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConditionsType readConditionsType(byte[] in) throws DeserializerException {
		return (ConditionsType) this.xmlToObj(in, ConditionsType.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.protocol.information_missing.ConditionsType}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.ConditionsType}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.ConditionsType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConditionsType readConditionsTypeFromString(String in) throws DeserializerException {
		return (ConditionsType) this.xmlToObj(in.getBytes(), ConditionsType.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: Fruitore
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.protocol.information_missing.Fruitore}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.Fruitore}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.Fruitore}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Fruitore readFruitore(String fileName) throws DeserializerException {
		return (Fruitore) this.xmlToObj(fileName, Fruitore.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.protocol.information_missing.Fruitore}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.Fruitore}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.Fruitore}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Fruitore readFruitore(File file) throws DeserializerException {
		return (Fruitore) this.xmlToObj(file, Fruitore.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.protocol.information_missing.Fruitore}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.Fruitore}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.Fruitore}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Fruitore readFruitore(InputStream in) throws DeserializerException {
		return (Fruitore) this.xmlToObj(in, Fruitore.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.protocol.information_missing.Fruitore}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.Fruitore}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.Fruitore}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Fruitore readFruitore(byte[] in) throws DeserializerException {
		return (Fruitore) this.xmlToObj(in, Fruitore.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.protocol.information_missing.Fruitore}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.Fruitore}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.Fruitore}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Fruitore readFruitoreFromString(String in) throws DeserializerException {
		return (Fruitore) this.xmlToObj(in.getBytes(), Fruitore.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: replaceFruitoreMatchType
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.protocol.information_missing.ReplaceFruitoreMatchType}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.ReplaceFruitoreMatchType}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.ReplaceFruitoreMatchType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ReplaceFruitoreMatchType readReplaceFruitoreMatchType(String fileName) throws DeserializerException {
		return (ReplaceFruitoreMatchType) this.xmlToObj(fileName, ReplaceFruitoreMatchType.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.protocol.information_missing.ReplaceFruitoreMatchType}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.ReplaceFruitoreMatchType}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.ReplaceFruitoreMatchType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ReplaceFruitoreMatchType readReplaceFruitoreMatchType(File file) throws DeserializerException {
		return (ReplaceFruitoreMatchType) this.xmlToObj(file, ReplaceFruitoreMatchType.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.protocol.information_missing.ReplaceFruitoreMatchType}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.ReplaceFruitoreMatchType}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.ReplaceFruitoreMatchType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ReplaceFruitoreMatchType readReplaceFruitoreMatchType(InputStream in) throws DeserializerException {
		return (ReplaceFruitoreMatchType) this.xmlToObj(in, ReplaceFruitoreMatchType.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.protocol.information_missing.ReplaceFruitoreMatchType}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.ReplaceFruitoreMatchType}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.ReplaceFruitoreMatchType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ReplaceFruitoreMatchType readReplaceFruitoreMatchType(byte[] in) throws DeserializerException {
		return (ReplaceFruitoreMatchType) this.xmlToObj(in, ReplaceFruitoreMatchType.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.protocol.information_missing.ReplaceFruitoreMatchType}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.ReplaceFruitoreMatchType}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.ReplaceFruitoreMatchType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ReplaceFruitoreMatchType readReplaceFruitoreMatchTypeFromString(String in) throws DeserializerException {
		return (ReplaceFruitoreMatchType) this.xmlToObj(in.getBytes(), ReplaceFruitoreMatchType.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: PortaDelegata
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.protocol.information_missing.PortaDelegata}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.PortaDelegata}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.PortaDelegata}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PortaDelegata readPortaDelegata(String fileName) throws DeserializerException {
		return (PortaDelegata) this.xmlToObj(fileName, PortaDelegata.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.protocol.information_missing.PortaDelegata}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.PortaDelegata}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.PortaDelegata}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PortaDelegata readPortaDelegata(File file) throws DeserializerException {
		return (PortaDelegata) this.xmlToObj(file, PortaDelegata.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.protocol.information_missing.PortaDelegata}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.PortaDelegata}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.PortaDelegata}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PortaDelegata readPortaDelegata(InputStream in) throws DeserializerException {
		return (PortaDelegata) this.xmlToObj(in, PortaDelegata.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.protocol.information_missing.PortaDelegata}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.PortaDelegata}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.PortaDelegata}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PortaDelegata readPortaDelegata(byte[] in) throws DeserializerException {
		return (PortaDelegata) this.xmlToObj(in, PortaDelegata.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.protocol.information_missing.PortaDelegata}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.PortaDelegata}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.PortaDelegata}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PortaDelegata readPortaDelegataFromString(String in) throws DeserializerException {
		return (PortaDelegata) this.xmlToObj(in.getBytes(), PortaDelegata.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: ProprietaRequisitoInput
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.protocol.information_missing.ProprietaRequisitoInput}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.ProprietaRequisitoInput}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.ProprietaRequisitoInput}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ProprietaRequisitoInput readProprietaRequisitoInput(String fileName) throws DeserializerException {
		return (ProprietaRequisitoInput) this.xmlToObj(fileName, ProprietaRequisitoInput.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.protocol.information_missing.ProprietaRequisitoInput}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.ProprietaRequisitoInput}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.ProprietaRequisitoInput}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ProprietaRequisitoInput readProprietaRequisitoInput(File file) throws DeserializerException {
		return (ProprietaRequisitoInput) this.xmlToObj(file, ProprietaRequisitoInput.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.protocol.information_missing.ProprietaRequisitoInput}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.ProprietaRequisitoInput}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.ProprietaRequisitoInput}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ProprietaRequisitoInput readProprietaRequisitoInput(InputStream in) throws DeserializerException {
		return (ProprietaRequisitoInput) this.xmlToObj(in, ProprietaRequisitoInput.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.protocol.information_missing.ProprietaRequisitoInput}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.ProprietaRequisitoInput}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.ProprietaRequisitoInput}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ProprietaRequisitoInput readProprietaRequisitoInput(byte[] in) throws DeserializerException {
		return (ProprietaRequisitoInput) this.xmlToObj(in, ProprietaRequisitoInput.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.protocol.information_missing.ProprietaRequisitoInput}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.ProprietaRequisitoInput}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.ProprietaRequisitoInput}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ProprietaRequisitoInput readProprietaRequisitoInputFromString(String in) throws DeserializerException {
		return (ProprietaRequisitoInput) this.xmlToObj(in.getBytes(), ProprietaRequisitoInput.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: ProprietaDefault
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.protocol.information_missing.ProprietaDefault}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.ProprietaDefault}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.ProprietaDefault}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ProprietaDefault readProprietaDefault(String fileName) throws DeserializerException {
		return (ProprietaDefault) this.xmlToObj(fileName, ProprietaDefault.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.protocol.information_missing.ProprietaDefault}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.ProprietaDefault}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.ProprietaDefault}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ProprietaDefault readProprietaDefault(File file) throws DeserializerException {
		return (ProprietaDefault) this.xmlToObj(file, ProprietaDefault.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.protocol.information_missing.ProprietaDefault}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.ProprietaDefault}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.ProprietaDefault}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ProprietaDefault readProprietaDefault(InputStream in) throws DeserializerException {
		return (ProprietaDefault) this.xmlToObj(in, ProprietaDefault.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.protocol.information_missing.ProprietaDefault}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.ProprietaDefault}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.ProprietaDefault}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ProprietaDefault readProprietaDefault(byte[] in) throws DeserializerException {
		return (ProprietaDefault) this.xmlToObj(in, ProprietaDefault.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.protocol.information_missing.ProprietaDefault}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.ProprietaDefault}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.ProprietaDefault}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ProprietaDefault readProprietaDefaultFromString(String in) throws DeserializerException {
		return (ProprietaDefault) this.xmlToObj(in.getBytes(), ProprietaDefault.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: AccordoServizioParteSpecifica
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.protocol.information_missing.AccordoServizioParteSpecifica}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.AccordoServizioParteSpecifica}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.AccordoServizioParteSpecifica}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccordoServizioParteSpecifica readAccordoServizioParteSpecifica(String fileName) throws DeserializerException {
		return (AccordoServizioParteSpecifica) this.xmlToObj(fileName, AccordoServizioParteSpecifica.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.protocol.information_missing.AccordoServizioParteSpecifica}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.AccordoServizioParteSpecifica}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.AccordoServizioParteSpecifica}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccordoServizioParteSpecifica readAccordoServizioParteSpecifica(File file) throws DeserializerException {
		return (AccordoServizioParteSpecifica) this.xmlToObj(file, AccordoServizioParteSpecifica.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.protocol.information_missing.AccordoServizioParteSpecifica}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.AccordoServizioParteSpecifica}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.AccordoServizioParteSpecifica}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccordoServizioParteSpecifica readAccordoServizioParteSpecifica(InputStream in) throws DeserializerException {
		return (AccordoServizioParteSpecifica) this.xmlToObj(in, AccordoServizioParteSpecifica.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.protocol.information_missing.AccordoServizioParteSpecifica}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.AccordoServizioParteSpecifica}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.AccordoServizioParteSpecifica}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccordoServizioParteSpecifica readAccordoServizioParteSpecifica(byte[] in) throws DeserializerException {
		return (AccordoServizioParteSpecifica) this.xmlToObj(in, AccordoServizioParteSpecifica.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.protocol.information_missing.AccordoServizioParteSpecifica}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.AccordoServizioParteSpecifica}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.AccordoServizioParteSpecifica}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccordoServizioParteSpecifica readAccordoServizioParteSpecificaFromString(String in) throws DeserializerException {
		return (AccordoServizioParteSpecifica) this.xmlToObj(in.getBytes(), AccordoServizioParteSpecifica.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: Default
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.protocol.information_missing.Default}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.Default}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.Default}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Default readDefault(String fileName) throws DeserializerException {
		return (Default) this.xmlToObj(fileName, Default.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.protocol.information_missing.Default}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.Default}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.Default}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Default readDefault(File file) throws DeserializerException {
		return (Default) this.xmlToObj(file, Default.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.protocol.information_missing.Default}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.Default}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.Default}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Default readDefault(InputStream in) throws DeserializerException {
		return (Default) this.xmlToObj(in, Default.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.protocol.information_missing.Default}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.Default}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.Default}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Default readDefault(byte[] in) throws DeserializerException {
		return (Default) this.xmlToObj(in, Default.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.protocol.information_missing.Default}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.Default}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.Default}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Default readDefaultFromString(String in) throws DeserializerException {
		return (Default) this.xmlToObj(in.getBytes(), Default.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: DescriptionType
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.protocol.information_missing.DescriptionType}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.DescriptionType}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.DescriptionType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DescriptionType readDescriptionType(String fileName) throws DeserializerException {
		return (DescriptionType) this.xmlToObj(fileName, DescriptionType.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.protocol.information_missing.DescriptionType}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.DescriptionType}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.DescriptionType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DescriptionType readDescriptionType(File file) throws DeserializerException {
		return (DescriptionType) this.xmlToObj(file, DescriptionType.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.protocol.information_missing.DescriptionType}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.DescriptionType}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.DescriptionType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DescriptionType readDescriptionType(InputStream in) throws DeserializerException {
		return (DescriptionType) this.xmlToObj(in, DescriptionType.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.protocol.information_missing.DescriptionType}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.DescriptionType}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.DescriptionType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DescriptionType readDescriptionType(byte[] in) throws DeserializerException {
		return (DescriptionType) this.xmlToObj(in, DescriptionType.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.protocol.information_missing.DescriptionType}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.DescriptionType}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.DescriptionType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DescriptionType readDescriptionTypeFromString(String in) throws DeserializerException {
		return (DescriptionType) this.xmlToObj(in.getBytes(), DescriptionType.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: ConditionType
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.protocol.information_missing.ConditionType}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.ConditionType}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.ConditionType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConditionType readConditionType(String fileName) throws DeserializerException {
		return (ConditionType) this.xmlToObj(fileName, ConditionType.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.protocol.information_missing.ConditionType}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.ConditionType}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.ConditionType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConditionType readConditionType(File file) throws DeserializerException {
		return (ConditionType) this.xmlToObj(file, ConditionType.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.protocol.information_missing.ConditionType}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.ConditionType}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.ConditionType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConditionType readConditionType(InputStream in) throws DeserializerException {
		return (ConditionType) this.xmlToObj(in, ConditionType.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.protocol.information_missing.ConditionType}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.ConditionType}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.ConditionType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConditionType readConditionType(byte[] in) throws DeserializerException {
		return (ConditionType) this.xmlToObj(in, ConditionType.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.protocol.information_missing.ConditionType}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.ConditionType}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.ConditionType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConditionType readConditionTypeFromString(String in) throws DeserializerException {
		return (ConditionType) this.xmlToObj(in.getBytes(), ConditionType.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: AccordoServizioParteComune
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.protocol.information_missing.AccordoServizioParteComune}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.AccordoServizioParteComune}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.AccordoServizioParteComune}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccordoServizioParteComune readAccordoServizioParteComune(String fileName) throws DeserializerException {
		return (AccordoServizioParteComune) this.xmlToObj(fileName, AccordoServizioParteComune.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.protocol.information_missing.AccordoServizioParteComune}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.AccordoServizioParteComune}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.AccordoServizioParteComune}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccordoServizioParteComune readAccordoServizioParteComune(File file) throws DeserializerException {
		return (AccordoServizioParteComune) this.xmlToObj(file, AccordoServizioParteComune.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.protocol.information_missing.AccordoServizioParteComune}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.AccordoServizioParteComune}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.AccordoServizioParteComune}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccordoServizioParteComune readAccordoServizioParteComune(InputStream in) throws DeserializerException {
		return (AccordoServizioParteComune) this.xmlToObj(in, AccordoServizioParteComune.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.protocol.information_missing.AccordoServizioParteComune}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.AccordoServizioParteComune}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.AccordoServizioParteComune}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccordoServizioParteComune readAccordoServizioParteComune(byte[] in) throws DeserializerException {
		return (AccordoServizioParteComune) this.xmlToObj(in, AccordoServizioParteComune.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.protocol.information_missing.AccordoServizioParteComune}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.AccordoServizioParteComune}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.AccordoServizioParteComune}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccordoServizioParteComune readAccordoServizioParteComuneFromString(String in) throws DeserializerException {
		return (AccordoServizioParteComune) this.xmlToObj(in.getBytes(), AccordoServizioParteComune.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: PortaApplicativa
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.protocol.information_missing.PortaApplicativa}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.PortaApplicativa}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.PortaApplicativa}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PortaApplicativa readPortaApplicativa(String fileName) throws DeserializerException {
		return (PortaApplicativa) this.xmlToObj(fileName, PortaApplicativa.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.protocol.information_missing.PortaApplicativa}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.PortaApplicativa}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.PortaApplicativa}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PortaApplicativa readPortaApplicativa(File file) throws DeserializerException {
		return (PortaApplicativa) this.xmlToObj(file, PortaApplicativa.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.protocol.information_missing.PortaApplicativa}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.PortaApplicativa}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.PortaApplicativa}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PortaApplicativa readPortaApplicativa(InputStream in) throws DeserializerException {
		return (PortaApplicativa) this.xmlToObj(in, PortaApplicativa.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.protocol.information_missing.PortaApplicativa}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.PortaApplicativa}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.PortaApplicativa}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PortaApplicativa readPortaApplicativa(byte[] in) throws DeserializerException {
		return (PortaApplicativa) this.xmlToObj(in, PortaApplicativa.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.protocol.information_missing.PortaApplicativa}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.PortaApplicativa}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.PortaApplicativa}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PortaApplicativa readPortaApplicativaFromString(String in) throws DeserializerException {
		return (PortaApplicativa) this.xmlToObj(in.getBytes(), PortaApplicativa.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: ServizioApplicativo
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.protocol.information_missing.ServizioApplicativo}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.ServizioApplicativo}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.ServizioApplicativo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ServizioApplicativo readServizioApplicativo(String fileName) throws DeserializerException {
		return (ServizioApplicativo) this.xmlToObj(fileName, ServizioApplicativo.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.protocol.information_missing.ServizioApplicativo}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.ServizioApplicativo}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.ServizioApplicativo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ServizioApplicativo readServizioApplicativo(File file) throws DeserializerException {
		return (ServizioApplicativo) this.xmlToObj(file, ServizioApplicativo.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.protocol.information_missing.ServizioApplicativo}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.ServizioApplicativo}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.ServizioApplicativo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ServizioApplicativo readServizioApplicativo(InputStream in) throws DeserializerException {
		return (ServizioApplicativo) this.xmlToObj(in, ServizioApplicativo.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.protocol.information_missing.ServizioApplicativo}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.ServizioApplicativo}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.ServizioApplicativo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ServizioApplicativo readServizioApplicativo(byte[] in) throws DeserializerException {
		return (ServizioApplicativo) this.xmlToObj(in, ServizioApplicativo.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.protocol.information_missing.ServizioApplicativo}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.ServizioApplicativo}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.ServizioApplicativo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ServizioApplicativo readServizioApplicativoFromString(String in) throws DeserializerException {
		return (ServizioApplicativo) this.xmlToObj(in.getBytes(), ServizioApplicativo.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: RequisitoProtocollo
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.protocol.information_missing.RequisitoProtocollo}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.RequisitoProtocollo}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.RequisitoProtocollo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RequisitoProtocollo readRequisitoProtocollo(String fileName) throws DeserializerException {
		return (RequisitoProtocollo) this.xmlToObj(fileName, RequisitoProtocollo.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.protocol.information_missing.RequisitoProtocollo}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.RequisitoProtocollo}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.RequisitoProtocollo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RequisitoProtocollo readRequisitoProtocollo(File file) throws DeserializerException {
		return (RequisitoProtocollo) this.xmlToObj(file, RequisitoProtocollo.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.protocol.information_missing.RequisitoProtocollo}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.RequisitoProtocollo}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.RequisitoProtocollo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RequisitoProtocollo readRequisitoProtocollo(InputStream in) throws DeserializerException {
		return (RequisitoProtocollo) this.xmlToObj(in, RequisitoProtocollo.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.protocol.information_missing.RequisitoProtocollo}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.RequisitoProtocollo}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.RequisitoProtocollo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RequisitoProtocollo readRequisitoProtocollo(byte[] in) throws DeserializerException {
		return (RequisitoProtocollo) this.xmlToObj(in, RequisitoProtocollo.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.protocol.information_missing.RequisitoProtocollo}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.RequisitoProtocollo}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.RequisitoProtocollo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RequisitoProtocollo readRequisitoProtocolloFromString(String in) throws DeserializerException {
		return (RequisitoProtocollo) this.xmlToObj(in.getBytes(), RequisitoProtocollo.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: AccordoCooperazione
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.protocol.information_missing.AccordoCooperazione}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.AccordoCooperazione}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.AccordoCooperazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccordoCooperazione readAccordoCooperazione(String fileName) throws DeserializerException {
		return (AccordoCooperazione) this.xmlToObj(fileName, AccordoCooperazione.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.protocol.information_missing.AccordoCooperazione}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.AccordoCooperazione}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.AccordoCooperazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccordoCooperazione readAccordoCooperazione(File file) throws DeserializerException {
		return (AccordoCooperazione) this.xmlToObj(file, AccordoCooperazione.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.protocol.information_missing.AccordoCooperazione}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.AccordoCooperazione}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.AccordoCooperazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccordoCooperazione readAccordoCooperazione(InputStream in) throws DeserializerException {
		return (AccordoCooperazione) this.xmlToObj(in, AccordoCooperazione.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.protocol.information_missing.AccordoCooperazione}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.AccordoCooperazione}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.AccordoCooperazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccordoCooperazione readAccordoCooperazione(byte[] in) throws DeserializerException {
		return (AccordoCooperazione) this.xmlToObj(in, AccordoCooperazione.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.protocol.information_missing.AccordoCooperazione}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.AccordoCooperazione}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.AccordoCooperazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public AccordoCooperazione readAccordoCooperazioneFromString(String in) throws DeserializerException {
		return (AccordoCooperazione) this.xmlToObj(in.getBytes(), AccordoCooperazione.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: Requisiti
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.protocol.information_missing.Requisiti}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.Requisiti}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.Requisiti}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Requisiti readRequisiti(String fileName) throws DeserializerException {
		return (Requisiti) this.xmlToObj(fileName, Requisiti.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.protocol.information_missing.Requisiti}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.Requisiti}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.Requisiti}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Requisiti readRequisiti(File file) throws DeserializerException {
		return (Requisiti) this.xmlToObj(file, Requisiti.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.protocol.information_missing.Requisiti}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.Requisiti}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.Requisiti}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Requisiti readRequisiti(InputStream in) throws DeserializerException {
		return (Requisiti) this.xmlToObj(in, Requisiti.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.protocol.information_missing.Requisiti}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.Requisiti}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.Requisiti}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Requisiti readRequisiti(byte[] in) throws DeserializerException {
		return (Requisiti) this.xmlToObj(in, Requisiti.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.protocol.information_missing.Requisiti}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.Requisiti}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.Requisiti}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Requisiti readRequisitiFromString(String in) throws DeserializerException {
		return (Requisiti) this.xmlToObj(in.getBytes(), Requisiti.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: RequisitoInput
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.protocol.information_missing.RequisitoInput}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.RequisitoInput}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.RequisitoInput}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RequisitoInput readRequisitoInput(String fileName) throws DeserializerException {
		return (RequisitoInput) this.xmlToObj(fileName, RequisitoInput.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.protocol.information_missing.RequisitoInput}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.RequisitoInput}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.RequisitoInput}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RequisitoInput readRequisitoInput(File file) throws DeserializerException {
		return (RequisitoInput) this.xmlToObj(file, RequisitoInput.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.protocol.information_missing.RequisitoInput}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.RequisitoInput}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.RequisitoInput}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RequisitoInput readRequisitoInput(InputStream in) throws DeserializerException {
		return (RequisitoInput) this.xmlToObj(in, RequisitoInput.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.protocol.information_missing.RequisitoInput}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.RequisitoInput}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.RequisitoInput}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RequisitoInput readRequisitoInput(byte[] in) throws DeserializerException {
		return (RequisitoInput) this.xmlToObj(in, RequisitoInput.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.protocol.information_missing.RequisitoInput}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.RequisitoInput}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.RequisitoInput}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RequisitoInput readRequisitoInputFromString(String in) throws DeserializerException {
		return (RequisitoInput) this.xmlToObj(in.getBytes(), RequisitoInput.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: openspcoop2
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.protocol.information_missing.Openspcoop2}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.Openspcoop2}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.Openspcoop2}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Openspcoop2 readOpenspcoop2(String fileName) throws DeserializerException {
		return (Openspcoop2) this.xmlToObj(fileName, Openspcoop2.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.protocol.information_missing.Openspcoop2}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.Openspcoop2}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.Openspcoop2}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Openspcoop2 readOpenspcoop2(File file) throws DeserializerException {
		return (Openspcoop2) this.xmlToObj(file, Openspcoop2.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.protocol.information_missing.Openspcoop2}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.Openspcoop2}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.Openspcoop2}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Openspcoop2 readOpenspcoop2(InputStream in) throws DeserializerException {
		return (Openspcoop2) this.xmlToObj(in, Openspcoop2.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.protocol.information_missing.Openspcoop2}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.Openspcoop2}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.Openspcoop2}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Openspcoop2 readOpenspcoop2(byte[] in) throws DeserializerException {
		return (Openspcoop2) this.xmlToObj(in, Openspcoop2.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.protocol.information_missing.Openspcoop2}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.Openspcoop2}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.Openspcoop2}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Openspcoop2 readOpenspcoop2FromString(String in) throws DeserializerException {
		return (Openspcoop2) this.xmlToObj(in.getBytes(), Openspcoop2.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: operazione
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.protocol.information_missing.Operazione}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.Operazione}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.Operazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Operazione readOperazione(String fileName) throws DeserializerException {
		return (Operazione) this.xmlToObj(fileName, Operazione.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.protocol.information_missing.Operazione}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.Operazione}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.Operazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Operazione readOperazione(File file) throws DeserializerException {
		return (Operazione) this.xmlToObj(file, Operazione.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.protocol.information_missing.Operazione}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.Operazione}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.Operazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Operazione readOperazione(InputStream in) throws DeserializerException {
		return (Operazione) this.xmlToObj(in, Operazione.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.protocol.information_missing.Operazione}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.Operazione}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.Operazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Operazione readOperazione(byte[] in) throws DeserializerException {
		return (Operazione) this.xmlToObj(in, Operazione.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.protocol.information_missing.Operazione}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.Operazione}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.Operazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Operazione readOperazioneFromString(String in) throws DeserializerException {
		return (Operazione) this.xmlToObj(in.getBytes(), Operazione.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: Soggetto
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.protocol.information_missing.Soggetto}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.Soggetto}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.Soggetto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Soggetto readSoggetto(String fileName) throws DeserializerException {
		return (Soggetto) this.xmlToObj(fileName, Soggetto.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.protocol.information_missing.Soggetto}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.Soggetto}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.Soggetto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Soggetto readSoggetto(File file) throws DeserializerException {
		return (Soggetto) this.xmlToObj(file, Soggetto.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.protocol.information_missing.Soggetto}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.Soggetto}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.Soggetto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Soggetto readSoggetto(InputStream in) throws DeserializerException {
		return (Soggetto) this.xmlToObj(in, Soggetto.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.protocol.information_missing.Soggetto}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.Soggetto}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.Soggetto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Soggetto readSoggetto(byte[] in) throws DeserializerException {
		return (Soggetto) this.xmlToObj(in, Soggetto.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.protocol.information_missing.Soggetto}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.Soggetto}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.Soggetto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Soggetto readSoggettoFromString(String in) throws DeserializerException {
		return (Soggetto) this.xmlToObj(in.getBytes(), Soggetto.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: Input
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.protocol.information_missing.Input}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.Input}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.Input}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Input readInput(String fileName) throws DeserializerException {
		return (Input) this.xmlToObj(fileName, Input.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.protocol.information_missing.Input}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.Input}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.Input}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Input readInput(File file) throws DeserializerException {
		return (Input) this.xmlToObj(file, Input.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.protocol.information_missing.Input}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.Input}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.Input}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Input readInput(InputStream in) throws DeserializerException {
		return (Input) this.xmlToObj(in, Input.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.protocol.information_missing.Input}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.Input}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.Input}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Input readInput(byte[] in) throws DeserializerException {
		return (Input) this.xmlToObj(in, Input.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.protocol.information_missing.Input}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.Input}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.Input}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Input readInputFromString(String in) throws DeserializerException {
		return (Input) this.xmlToObj(in.getBytes(), Input.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: Proprieta
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.protocol.information_missing.Proprieta}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.Proprieta}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.Proprieta}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Proprieta readProprieta(String fileName) throws DeserializerException {
		return (Proprieta) this.xmlToObj(fileName, Proprieta.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.protocol.information_missing.Proprieta}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.Proprieta}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.Proprieta}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Proprieta readProprieta(File file) throws DeserializerException {
		return (Proprieta) this.xmlToObj(file, Proprieta.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.protocol.information_missing.Proprieta}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.Proprieta}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.Proprieta}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Proprieta readProprieta(InputStream in) throws DeserializerException {
		return (Proprieta) this.xmlToObj(in, Proprieta.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.protocol.information_missing.Proprieta}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.Proprieta}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.Proprieta}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Proprieta readProprieta(byte[] in) throws DeserializerException {
		return (Proprieta) this.xmlToObj(in, Proprieta.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.protocol.information_missing.Proprieta}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.Proprieta}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.Proprieta}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Proprieta readProprietaFromString(String in) throws DeserializerException {
		return (Proprieta) this.xmlToObj(in.getBytes(), Proprieta.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: Wizard
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.protocol.information_missing.Wizard}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.Wizard}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.Wizard}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Wizard readWizard(String fileName) throws DeserializerException {
		return (Wizard) this.xmlToObj(fileName, Wizard.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.protocol.information_missing.Wizard}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.Wizard}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.Wizard}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Wizard readWizard(File file) throws DeserializerException {
		return (Wizard) this.xmlToObj(file, Wizard.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.protocol.information_missing.Wizard}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.Wizard}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.Wizard}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Wizard readWizard(InputStream in) throws DeserializerException {
		return (Wizard) this.xmlToObj(in, Wizard.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.protocol.information_missing.Wizard}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.Wizard}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.Wizard}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Wizard readWizard(byte[] in) throws DeserializerException {
		return (Wizard) this.xmlToObj(in, Wizard.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.protocol.information_missing.Wizard}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.Wizard}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.Wizard}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Wizard readWizardFromString(String in) throws DeserializerException {
		return (Wizard) this.xmlToObj(in.getBytes(), Wizard.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: Description
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.protocol.information_missing.Description}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.Description}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.Description}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Description readDescription(String fileName) throws DeserializerException {
		return (Description) this.xmlToObj(fileName, Description.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.protocol.information_missing.Description}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.Description}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.Description}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Description readDescription(File file) throws DeserializerException {
		return (Description) this.xmlToObj(file, Description.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.protocol.information_missing.Description}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.Description}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.Description}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Description readDescription(InputStream in) throws DeserializerException {
		return (Description) this.xmlToObj(in, Description.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.protocol.information_missing.Description}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.Description}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.Description}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Description readDescription(byte[] in) throws DeserializerException {
		return (Description) this.xmlToObj(in, Description.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.protocol.information_missing.Description}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.protocol.information_missing.Description}
	 * @return Object type {@link org.openspcoop2.protocol.information_missing.Description}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Description readDescriptionFromString(String in) throws DeserializerException {
		return (Description) this.xmlToObj(in.getBytes(), Description.class);
	}	
	
	
	

}
