/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it).
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
package org.openspcoop2.monitor.engine.config.statistiche.utils.serializer;

import org.openspcoop2.generic_project.exception.DeserializerException;

import org.openspcoop2.monitor.engine.config.statistiche.InfoPlugin;
import org.openspcoop2.monitor.engine.config.statistiche.ConfigurazioneStatistica;
import org.openspcoop2.monitor.engine.config.statistiche.IdConfigurazioneServizioAzione;
import org.openspcoop2.monitor.engine.config.statistiche.ConfigurazioneServizioAzione;
import org.openspcoop2.monitor.engine.config.statistiche.ConfigurazioneServizio;
import org.openspcoop2.monitor.engine.config.statistiche.IdConfigurazioneServizio;
import org.openspcoop2.monitor.engine.config.statistiche.IdConfigurazioneStatistica;
import org.openspcoop2.monitor.engine.config.statistiche.IdPlugin;
import org.openspcoop2.monitor.engine.config.statistiche.Plugin;

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
	 Object: info-plugin
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.monitor.engine.config.statistiche.InfoPlugin}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.statistiche.InfoPlugin}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.statistiche.InfoPlugin}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public InfoPlugin readInfoPlugin(String fileName) throws DeserializerException {
		return (InfoPlugin) this.xmlToObj(fileName, InfoPlugin.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.monitor.engine.config.statistiche.InfoPlugin}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.statistiche.InfoPlugin}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.statistiche.InfoPlugin}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public InfoPlugin readInfoPlugin(File file) throws DeserializerException {
		return (InfoPlugin) this.xmlToObj(file, InfoPlugin.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.monitor.engine.config.statistiche.InfoPlugin}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.statistiche.InfoPlugin}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.statistiche.InfoPlugin}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public InfoPlugin readInfoPlugin(InputStream in) throws DeserializerException {
		return (InfoPlugin) this.xmlToObj(in, InfoPlugin.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.monitor.engine.config.statistiche.InfoPlugin}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.statistiche.InfoPlugin}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.statistiche.InfoPlugin}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public InfoPlugin readInfoPlugin(byte[] in) throws DeserializerException {
		return (InfoPlugin) this.xmlToObj(in, InfoPlugin.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.monitor.engine.config.statistiche.InfoPlugin}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.statistiche.InfoPlugin}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.statistiche.InfoPlugin}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public InfoPlugin readInfoPluginFromString(String in) throws DeserializerException {
		return (InfoPlugin) this.xmlToObj(in.getBytes(), InfoPlugin.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: configurazione-statistica
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.monitor.engine.config.statistiche.ConfigurazioneStatistica}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.statistiche.ConfigurazioneStatistica}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.statistiche.ConfigurazioneStatistica}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConfigurazioneStatistica readConfigurazioneStatistica(String fileName) throws DeserializerException {
		return (ConfigurazioneStatistica) this.xmlToObj(fileName, ConfigurazioneStatistica.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.monitor.engine.config.statistiche.ConfigurazioneStatistica}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.statistiche.ConfigurazioneStatistica}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.statistiche.ConfigurazioneStatistica}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConfigurazioneStatistica readConfigurazioneStatistica(File file) throws DeserializerException {
		return (ConfigurazioneStatistica) this.xmlToObj(file, ConfigurazioneStatistica.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.monitor.engine.config.statistiche.ConfigurazioneStatistica}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.statistiche.ConfigurazioneStatistica}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.statistiche.ConfigurazioneStatistica}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConfigurazioneStatistica readConfigurazioneStatistica(InputStream in) throws DeserializerException {
		return (ConfigurazioneStatistica) this.xmlToObj(in, ConfigurazioneStatistica.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.monitor.engine.config.statistiche.ConfigurazioneStatistica}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.statistiche.ConfigurazioneStatistica}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.statistiche.ConfigurazioneStatistica}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConfigurazioneStatistica readConfigurazioneStatistica(byte[] in) throws DeserializerException {
		return (ConfigurazioneStatistica) this.xmlToObj(in, ConfigurazioneStatistica.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.monitor.engine.config.statistiche.ConfigurazioneStatistica}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.statistiche.ConfigurazioneStatistica}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.statistiche.ConfigurazioneStatistica}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConfigurazioneStatistica readConfigurazioneStatisticaFromString(String in) throws DeserializerException {
		return (ConfigurazioneStatistica) this.xmlToObj(in.getBytes(), ConfigurazioneStatistica.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: id-configurazione-servizio-azione
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.monitor.engine.config.statistiche.IdConfigurazioneServizioAzione}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.statistiche.IdConfigurazioneServizioAzione}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.statistiche.IdConfigurazioneServizioAzione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdConfigurazioneServizioAzione readIdConfigurazioneServizioAzione(String fileName) throws DeserializerException {
		return (IdConfigurazioneServizioAzione) this.xmlToObj(fileName, IdConfigurazioneServizioAzione.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.monitor.engine.config.statistiche.IdConfigurazioneServizioAzione}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.statistiche.IdConfigurazioneServizioAzione}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.statistiche.IdConfigurazioneServizioAzione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdConfigurazioneServizioAzione readIdConfigurazioneServizioAzione(File file) throws DeserializerException {
		return (IdConfigurazioneServizioAzione) this.xmlToObj(file, IdConfigurazioneServizioAzione.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.monitor.engine.config.statistiche.IdConfigurazioneServizioAzione}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.statistiche.IdConfigurazioneServizioAzione}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.statistiche.IdConfigurazioneServizioAzione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdConfigurazioneServizioAzione readIdConfigurazioneServizioAzione(InputStream in) throws DeserializerException {
		return (IdConfigurazioneServizioAzione) this.xmlToObj(in, IdConfigurazioneServizioAzione.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.monitor.engine.config.statistiche.IdConfigurazioneServizioAzione}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.statistiche.IdConfigurazioneServizioAzione}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.statistiche.IdConfigurazioneServizioAzione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdConfigurazioneServizioAzione readIdConfigurazioneServizioAzione(byte[] in) throws DeserializerException {
		return (IdConfigurazioneServizioAzione) this.xmlToObj(in, IdConfigurazioneServizioAzione.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.monitor.engine.config.statistiche.IdConfigurazioneServizioAzione}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.statistiche.IdConfigurazioneServizioAzione}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.statistiche.IdConfigurazioneServizioAzione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdConfigurazioneServizioAzione readIdConfigurazioneServizioAzioneFromString(String in) throws DeserializerException {
		return (IdConfigurazioneServizioAzione) this.xmlToObj(in.getBytes(), IdConfigurazioneServizioAzione.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: configurazione-servizio-azione
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.monitor.engine.config.statistiche.ConfigurazioneServizioAzione}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.statistiche.ConfigurazioneServizioAzione}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.statistiche.ConfigurazioneServizioAzione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConfigurazioneServizioAzione readConfigurazioneServizioAzione(String fileName) throws DeserializerException {
		return (ConfigurazioneServizioAzione) this.xmlToObj(fileName, ConfigurazioneServizioAzione.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.monitor.engine.config.statistiche.ConfigurazioneServizioAzione}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.statistiche.ConfigurazioneServizioAzione}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.statistiche.ConfigurazioneServizioAzione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConfigurazioneServizioAzione readConfigurazioneServizioAzione(File file) throws DeserializerException {
		return (ConfigurazioneServizioAzione) this.xmlToObj(file, ConfigurazioneServizioAzione.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.monitor.engine.config.statistiche.ConfigurazioneServizioAzione}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.statistiche.ConfigurazioneServizioAzione}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.statistiche.ConfigurazioneServizioAzione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConfigurazioneServizioAzione readConfigurazioneServizioAzione(InputStream in) throws DeserializerException {
		return (ConfigurazioneServizioAzione) this.xmlToObj(in, ConfigurazioneServizioAzione.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.monitor.engine.config.statistiche.ConfigurazioneServizioAzione}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.statistiche.ConfigurazioneServizioAzione}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.statistiche.ConfigurazioneServizioAzione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConfigurazioneServizioAzione readConfigurazioneServizioAzione(byte[] in) throws DeserializerException {
		return (ConfigurazioneServizioAzione) this.xmlToObj(in, ConfigurazioneServizioAzione.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.monitor.engine.config.statistiche.ConfigurazioneServizioAzione}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.statistiche.ConfigurazioneServizioAzione}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.statistiche.ConfigurazioneServizioAzione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConfigurazioneServizioAzione readConfigurazioneServizioAzioneFromString(String in) throws DeserializerException {
		return (ConfigurazioneServizioAzione) this.xmlToObj(in.getBytes(), ConfigurazioneServizioAzione.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: configurazione-servizio
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.monitor.engine.config.statistiche.ConfigurazioneServizio}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.statistiche.ConfigurazioneServizio}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.statistiche.ConfigurazioneServizio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConfigurazioneServizio readConfigurazioneServizio(String fileName) throws DeserializerException {
		return (ConfigurazioneServizio) this.xmlToObj(fileName, ConfigurazioneServizio.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.monitor.engine.config.statistiche.ConfigurazioneServizio}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.statistiche.ConfigurazioneServizio}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.statistiche.ConfigurazioneServizio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConfigurazioneServizio readConfigurazioneServizio(File file) throws DeserializerException {
		return (ConfigurazioneServizio) this.xmlToObj(file, ConfigurazioneServizio.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.monitor.engine.config.statistiche.ConfigurazioneServizio}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.statistiche.ConfigurazioneServizio}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.statistiche.ConfigurazioneServizio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConfigurazioneServizio readConfigurazioneServizio(InputStream in) throws DeserializerException {
		return (ConfigurazioneServizio) this.xmlToObj(in, ConfigurazioneServizio.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.monitor.engine.config.statistiche.ConfigurazioneServizio}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.statistiche.ConfigurazioneServizio}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.statistiche.ConfigurazioneServizio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConfigurazioneServizio readConfigurazioneServizio(byte[] in) throws DeserializerException {
		return (ConfigurazioneServizio) this.xmlToObj(in, ConfigurazioneServizio.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.monitor.engine.config.statistiche.ConfigurazioneServizio}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.statistiche.ConfigurazioneServizio}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.statistiche.ConfigurazioneServizio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConfigurazioneServizio readConfigurazioneServizioFromString(String in) throws DeserializerException {
		return (ConfigurazioneServizio) this.xmlToObj(in.getBytes(), ConfigurazioneServizio.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: id-configurazione-servizio
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.monitor.engine.config.statistiche.IdConfigurazioneServizio}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.statistiche.IdConfigurazioneServizio}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.statistiche.IdConfigurazioneServizio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdConfigurazioneServizio readIdConfigurazioneServizio(String fileName) throws DeserializerException {
		return (IdConfigurazioneServizio) this.xmlToObj(fileName, IdConfigurazioneServizio.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.monitor.engine.config.statistiche.IdConfigurazioneServizio}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.statistiche.IdConfigurazioneServizio}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.statistiche.IdConfigurazioneServizio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdConfigurazioneServizio readIdConfigurazioneServizio(File file) throws DeserializerException {
		return (IdConfigurazioneServizio) this.xmlToObj(file, IdConfigurazioneServizio.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.monitor.engine.config.statistiche.IdConfigurazioneServizio}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.statistiche.IdConfigurazioneServizio}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.statistiche.IdConfigurazioneServizio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdConfigurazioneServizio readIdConfigurazioneServizio(InputStream in) throws DeserializerException {
		return (IdConfigurazioneServizio) this.xmlToObj(in, IdConfigurazioneServizio.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.monitor.engine.config.statistiche.IdConfigurazioneServizio}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.statistiche.IdConfigurazioneServizio}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.statistiche.IdConfigurazioneServizio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdConfigurazioneServizio readIdConfigurazioneServizio(byte[] in) throws DeserializerException {
		return (IdConfigurazioneServizio) this.xmlToObj(in, IdConfigurazioneServizio.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.monitor.engine.config.statistiche.IdConfigurazioneServizio}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.statistiche.IdConfigurazioneServizio}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.statistiche.IdConfigurazioneServizio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdConfigurazioneServizio readIdConfigurazioneServizioFromString(String in) throws DeserializerException {
		return (IdConfigurazioneServizio) this.xmlToObj(in.getBytes(), IdConfigurazioneServizio.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: id-configurazione-statistica
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.monitor.engine.config.statistiche.IdConfigurazioneStatistica}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.statistiche.IdConfigurazioneStatistica}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.statistiche.IdConfigurazioneStatistica}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdConfigurazioneStatistica readIdConfigurazioneStatistica(String fileName) throws DeserializerException {
		return (IdConfigurazioneStatistica) this.xmlToObj(fileName, IdConfigurazioneStatistica.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.monitor.engine.config.statistiche.IdConfigurazioneStatistica}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.statistiche.IdConfigurazioneStatistica}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.statistiche.IdConfigurazioneStatistica}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdConfigurazioneStatistica readIdConfigurazioneStatistica(File file) throws DeserializerException {
		return (IdConfigurazioneStatistica) this.xmlToObj(file, IdConfigurazioneStatistica.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.monitor.engine.config.statistiche.IdConfigurazioneStatistica}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.statistiche.IdConfigurazioneStatistica}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.statistiche.IdConfigurazioneStatistica}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdConfigurazioneStatistica readIdConfigurazioneStatistica(InputStream in) throws DeserializerException {
		return (IdConfigurazioneStatistica) this.xmlToObj(in, IdConfigurazioneStatistica.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.monitor.engine.config.statistiche.IdConfigurazioneStatistica}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.statistiche.IdConfigurazioneStatistica}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.statistiche.IdConfigurazioneStatistica}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdConfigurazioneStatistica readIdConfigurazioneStatistica(byte[] in) throws DeserializerException {
		return (IdConfigurazioneStatistica) this.xmlToObj(in, IdConfigurazioneStatistica.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.monitor.engine.config.statistiche.IdConfigurazioneStatistica}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.statistiche.IdConfigurazioneStatistica}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.statistiche.IdConfigurazioneStatistica}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdConfigurazioneStatistica readIdConfigurazioneStatisticaFromString(String in) throws DeserializerException {
		return (IdConfigurazioneStatistica) this.xmlToObj(in.getBytes(), IdConfigurazioneStatistica.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: id-plugin
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.monitor.engine.config.statistiche.IdPlugin}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.statistiche.IdPlugin}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.statistiche.IdPlugin}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdPlugin readIdPlugin(String fileName) throws DeserializerException {
		return (IdPlugin) this.xmlToObj(fileName, IdPlugin.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.monitor.engine.config.statistiche.IdPlugin}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.statistiche.IdPlugin}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.statistiche.IdPlugin}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdPlugin readIdPlugin(File file) throws DeserializerException {
		return (IdPlugin) this.xmlToObj(file, IdPlugin.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.monitor.engine.config.statistiche.IdPlugin}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.statistiche.IdPlugin}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.statistiche.IdPlugin}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdPlugin readIdPlugin(InputStream in) throws DeserializerException {
		return (IdPlugin) this.xmlToObj(in, IdPlugin.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.monitor.engine.config.statistiche.IdPlugin}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.statistiche.IdPlugin}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.statistiche.IdPlugin}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdPlugin readIdPlugin(byte[] in) throws DeserializerException {
		return (IdPlugin) this.xmlToObj(in, IdPlugin.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.monitor.engine.config.statistiche.IdPlugin}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.statistiche.IdPlugin}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.statistiche.IdPlugin}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdPlugin readIdPluginFromString(String in) throws DeserializerException {
		return (IdPlugin) this.xmlToObj(in.getBytes(), IdPlugin.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: plugin
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.monitor.engine.config.statistiche.Plugin}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.statistiche.Plugin}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.statistiche.Plugin}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Plugin readPlugin(String fileName) throws DeserializerException {
		return (Plugin) this.xmlToObj(fileName, Plugin.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.monitor.engine.config.statistiche.Plugin}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.statistiche.Plugin}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.statistiche.Plugin}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Plugin readPlugin(File file) throws DeserializerException {
		return (Plugin) this.xmlToObj(file, Plugin.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.monitor.engine.config.statistiche.Plugin}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.statistiche.Plugin}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.statistiche.Plugin}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Plugin readPlugin(InputStream in) throws DeserializerException {
		return (Plugin) this.xmlToObj(in, Plugin.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.monitor.engine.config.statistiche.Plugin}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.statistiche.Plugin}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.statistiche.Plugin}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Plugin readPlugin(byte[] in) throws DeserializerException {
		return (Plugin) this.xmlToObj(in, Plugin.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.monitor.engine.config.statistiche.Plugin}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.statistiche.Plugin}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.statistiche.Plugin}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Plugin readPluginFromString(String in) throws DeserializerException {
		return (Plugin) this.xmlToObj(in.getBytes(), Plugin.class);
	}	
	
	
	

}
