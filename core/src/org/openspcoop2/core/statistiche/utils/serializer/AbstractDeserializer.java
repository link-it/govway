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
package org.openspcoop2.core.statistiche.utils.serializer;

import org.openspcoop2.generic_project.exception.DeserializerException;

import org.openspcoop2.core.statistiche.Statistica;
import org.openspcoop2.core.statistiche.StatisticaMensile;
import org.openspcoop2.core.statistiche.StatisticaContenuti;
import org.openspcoop2.core.statistiche.StatisticaGiornaliera;
import org.openspcoop2.core.statistiche.StatisticaSettimanale;
import org.openspcoop2.core.statistiche.StatisticaOraria;
import org.openspcoop2.core.statistiche.StatisticaInfo;

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
	 Object: statistica
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.statistiche.Statistica}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.statistiche.Statistica}
	 * @return Object type {@link org.openspcoop2.core.statistiche.Statistica}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Statistica readStatistica(String fileName) throws DeserializerException {
		return (Statistica) this.xmlToObj(fileName, Statistica.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.statistiche.Statistica}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.statistiche.Statistica}
	 * @return Object type {@link org.openspcoop2.core.statistiche.Statistica}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Statistica readStatistica(File file) throws DeserializerException {
		return (Statistica) this.xmlToObj(file, Statistica.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.statistiche.Statistica}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.statistiche.Statistica}
	 * @return Object type {@link org.openspcoop2.core.statistiche.Statistica}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Statistica readStatistica(InputStream in) throws DeserializerException {
		return (Statistica) this.xmlToObj(in, Statistica.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.statistiche.Statistica}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.statistiche.Statistica}
	 * @return Object type {@link org.openspcoop2.core.statistiche.Statistica}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Statistica readStatistica(byte[] in) throws DeserializerException {
		return (Statistica) this.xmlToObj(in, Statistica.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.statistiche.Statistica}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.statistiche.Statistica}
	 * @return Object type {@link org.openspcoop2.core.statistiche.Statistica}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Statistica readStatisticaFromString(String in) throws DeserializerException {
		return (Statistica) this.xmlToObj(in.getBytes(), Statistica.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: statistica-mensile
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.statistiche.StatisticaMensile}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.statistiche.StatisticaMensile}
	 * @return Object type {@link org.openspcoop2.core.statistiche.StatisticaMensile}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public StatisticaMensile readStatisticaMensile(String fileName) throws DeserializerException {
		return (StatisticaMensile) this.xmlToObj(fileName, StatisticaMensile.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.statistiche.StatisticaMensile}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.statistiche.StatisticaMensile}
	 * @return Object type {@link org.openspcoop2.core.statistiche.StatisticaMensile}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public StatisticaMensile readStatisticaMensile(File file) throws DeserializerException {
		return (StatisticaMensile) this.xmlToObj(file, StatisticaMensile.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.statistiche.StatisticaMensile}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.statistiche.StatisticaMensile}
	 * @return Object type {@link org.openspcoop2.core.statistiche.StatisticaMensile}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public StatisticaMensile readStatisticaMensile(InputStream in) throws DeserializerException {
		return (StatisticaMensile) this.xmlToObj(in, StatisticaMensile.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.statistiche.StatisticaMensile}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.statistiche.StatisticaMensile}
	 * @return Object type {@link org.openspcoop2.core.statistiche.StatisticaMensile}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public StatisticaMensile readStatisticaMensile(byte[] in) throws DeserializerException {
		return (StatisticaMensile) this.xmlToObj(in, StatisticaMensile.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.statistiche.StatisticaMensile}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.statistiche.StatisticaMensile}
	 * @return Object type {@link org.openspcoop2.core.statistiche.StatisticaMensile}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public StatisticaMensile readStatisticaMensileFromString(String in) throws DeserializerException {
		return (StatisticaMensile) this.xmlToObj(in.getBytes(), StatisticaMensile.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: statistica-contenuti
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.statistiche.StatisticaContenuti}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.statistiche.StatisticaContenuti}
	 * @return Object type {@link org.openspcoop2.core.statistiche.StatisticaContenuti}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public StatisticaContenuti readStatisticaContenuti(String fileName) throws DeserializerException {
		return (StatisticaContenuti) this.xmlToObj(fileName, StatisticaContenuti.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.statistiche.StatisticaContenuti}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.statistiche.StatisticaContenuti}
	 * @return Object type {@link org.openspcoop2.core.statistiche.StatisticaContenuti}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public StatisticaContenuti readStatisticaContenuti(File file) throws DeserializerException {
		return (StatisticaContenuti) this.xmlToObj(file, StatisticaContenuti.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.statistiche.StatisticaContenuti}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.statistiche.StatisticaContenuti}
	 * @return Object type {@link org.openspcoop2.core.statistiche.StatisticaContenuti}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public StatisticaContenuti readStatisticaContenuti(InputStream in) throws DeserializerException {
		return (StatisticaContenuti) this.xmlToObj(in, StatisticaContenuti.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.statistiche.StatisticaContenuti}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.statistiche.StatisticaContenuti}
	 * @return Object type {@link org.openspcoop2.core.statistiche.StatisticaContenuti}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public StatisticaContenuti readStatisticaContenuti(byte[] in) throws DeserializerException {
		return (StatisticaContenuti) this.xmlToObj(in, StatisticaContenuti.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.statistiche.StatisticaContenuti}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.statistiche.StatisticaContenuti}
	 * @return Object type {@link org.openspcoop2.core.statistiche.StatisticaContenuti}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public StatisticaContenuti readStatisticaContenutiFromString(String in) throws DeserializerException {
		return (StatisticaContenuti) this.xmlToObj(in.getBytes(), StatisticaContenuti.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: statistica-giornaliera
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.statistiche.StatisticaGiornaliera}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.statistiche.StatisticaGiornaliera}
	 * @return Object type {@link org.openspcoop2.core.statistiche.StatisticaGiornaliera}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public StatisticaGiornaliera readStatisticaGiornaliera(String fileName) throws DeserializerException {
		return (StatisticaGiornaliera) this.xmlToObj(fileName, StatisticaGiornaliera.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.statistiche.StatisticaGiornaliera}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.statistiche.StatisticaGiornaliera}
	 * @return Object type {@link org.openspcoop2.core.statistiche.StatisticaGiornaliera}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public StatisticaGiornaliera readStatisticaGiornaliera(File file) throws DeserializerException {
		return (StatisticaGiornaliera) this.xmlToObj(file, StatisticaGiornaliera.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.statistiche.StatisticaGiornaliera}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.statistiche.StatisticaGiornaliera}
	 * @return Object type {@link org.openspcoop2.core.statistiche.StatisticaGiornaliera}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public StatisticaGiornaliera readStatisticaGiornaliera(InputStream in) throws DeserializerException {
		return (StatisticaGiornaliera) this.xmlToObj(in, StatisticaGiornaliera.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.statistiche.StatisticaGiornaliera}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.statistiche.StatisticaGiornaliera}
	 * @return Object type {@link org.openspcoop2.core.statistiche.StatisticaGiornaliera}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public StatisticaGiornaliera readStatisticaGiornaliera(byte[] in) throws DeserializerException {
		return (StatisticaGiornaliera) this.xmlToObj(in, StatisticaGiornaliera.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.statistiche.StatisticaGiornaliera}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.statistiche.StatisticaGiornaliera}
	 * @return Object type {@link org.openspcoop2.core.statistiche.StatisticaGiornaliera}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public StatisticaGiornaliera readStatisticaGiornalieraFromString(String in) throws DeserializerException {
		return (StatisticaGiornaliera) this.xmlToObj(in.getBytes(), StatisticaGiornaliera.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: statistica-settimanale
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.statistiche.StatisticaSettimanale}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.statistiche.StatisticaSettimanale}
	 * @return Object type {@link org.openspcoop2.core.statistiche.StatisticaSettimanale}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public StatisticaSettimanale readStatisticaSettimanale(String fileName) throws DeserializerException {
		return (StatisticaSettimanale) this.xmlToObj(fileName, StatisticaSettimanale.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.statistiche.StatisticaSettimanale}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.statistiche.StatisticaSettimanale}
	 * @return Object type {@link org.openspcoop2.core.statistiche.StatisticaSettimanale}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public StatisticaSettimanale readStatisticaSettimanale(File file) throws DeserializerException {
		return (StatisticaSettimanale) this.xmlToObj(file, StatisticaSettimanale.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.statistiche.StatisticaSettimanale}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.statistiche.StatisticaSettimanale}
	 * @return Object type {@link org.openspcoop2.core.statistiche.StatisticaSettimanale}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public StatisticaSettimanale readStatisticaSettimanale(InputStream in) throws DeserializerException {
		return (StatisticaSettimanale) this.xmlToObj(in, StatisticaSettimanale.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.statistiche.StatisticaSettimanale}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.statistiche.StatisticaSettimanale}
	 * @return Object type {@link org.openspcoop2.core.statistiche.StatisticaSettimanale}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public StatisticaSettimanale readStatisticaSettimanale(byte[] in) throws DeserializerException {
		return (StatisticaSettimanale) this.xmlToObj(in, StatisticaSettimanale.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.statistiche.StatisticaSettimanale}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.statistiche.StatisticaSettimanale}
	 * @return Object type {@link org.openspcoop2.core.statistiche.StatisticaSettimanale}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public StatisticaSettimanale readStatisticaSettimanaleFromString(String in) throws DeserializerException {
		return (StatisticaSettimanale) this.xmlToObj(in.getBytes(), StatisticaSettimanale.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: statistica-oraria
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.statistiche.StatisticaOraria}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.statistiche.StatisticaOraria}
	 * @return Object type {@link org.openspcoop2.core.statistiche.StatisticaOraria}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public StatisticaOraria readStatisticaOraria(String fileName) throws DeserializerException {
		return (StatisticaOraria) this.xmlToObj(fileName, StatisticaOraria.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.statistiche.StatisticaOraria}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.statistiche.StatisticaOraria}
	 * @return Object type {@link org.openspcoop2.core.statistiche.StatisticaOraria}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public StatisticaOraria readStatisticaOraria(File file) throws DeserializerException {
		return (StatisticaOraria) this.xmlToObj(file, StatisticaOraria.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.statistiche.StatisticaOraria}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.statistiche.StatisticaOraria}
	 * @return Object type {@link org.openspcoop2.core.statistiche.StatisticaOraria}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public StatisticaOraria readStatisticaOraria(InputStream in) throws DeserializerException {
		return (StatisticaOraria) this.xmlToObj(in, StatisticaOraria.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.statistiche.StatisticaOraria}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.statistiche.StatisticaOraria}
	 * @return Object type {@link org.openspcoop2.core.statistiche.StatisticaOraria}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public StatisticaOraria readStatisticaOraria(byte[] in) throws DeserializerException {
		return (StatisticaOraria) this.xmlToObj(in, StatisticaOraria.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.statistiche.StatisticaOraria}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.statistiche.StatisticaOraria}
	 * @return Object type {@link org.openspcoop2.core.statistiche.StatisticaOraria}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public StatisticaOraria readStatisticaOrariaFromString(String in) throws DeserializerException {
		return (StatisticaOraria) this.xmlToObj(in.getBytes(), StatisticaOraria.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: statistica-info
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.statistiche.StatisticaInfo}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.statistiche.StatisticaInfo}
	 * @return Object type {@link org.openspcoop2.core.statistiche.StatisticaInfo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public StatisticaInfo readStatisticaInfo(String fileName) throws DeserializerException {
		return (StatisticaInfo) this.xmlToObj(fileName, StatisticaInfo.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.statistiche.StatisticaInfo}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.statistiche.StatisticaInfo}
	 * @return Object type {@link org.openspcoop2.core.statistiche.StatisticaInfo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public StatisticaInfo readStatisticaInfo(File file) throws DeserializerException {
		return (StatisticaInfo) this.xmlToObj(file, StatisticaInfo.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.statistiche.StatisticaInfo}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.statistiche.StatisticaInfo}
	 * @return Object type {@link org.openspcoop2.core.statistiche.StatisticaInfo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public StatisticaInfo readStatisticaInfo(InputStream in) throws DeserializerException {
		return (StatisticaInfo) this.xmlToObj(in, StatisticaInfo.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.statistiche.StatisticaInfo}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.statistiche.StatisticaInfo}
	 * @return Object type {@link org.openspcoop2.core.statistiche.StatisticaInfo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public StatisticaInfo readStatisticaInfo(byte[] in) throws DeserializerException {
		return (StatisticaInfo) this.xmlToObj(in, StatisticaInfo.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.statistiche.StatisticaInfo}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.statistiche.StatisticaInfo}
	 * @return Object type {@link org.openspcoop2.core.statistiche.StatisticaInfo}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public StatisticaInfo readStatisticaInfoFromString(String in) throws DeserializerException {
		return (StatisticaInfo) this.xmlToObj(in.getBytes(), StatisticaInfo.class);
	}	
	
	
	

}
