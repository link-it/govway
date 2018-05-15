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
package org.openspcoop2.monitor.engine.config.ricerche.utils.serializer;

import org.openspcoop2.generic_project.exception.DeserializerException;

import org.openspcoop2.monitor.engine.config.ricerche.InfoPlugin;
import org.openspcoop2.monitor.engine.config.ricerche.ConfigurazioneServizioAzione;
import org.openspcoop2.monitor.engine.config.ricerche.ConfigurazioneServizio;
import org.openspcoop2.monitor.engine.config.ricerche.IdConfigurazioneServizio;
import org.openspcoop2.monitor.engine.config.ricerche.IdConfigurazioneServizioAzione;
import org.openspcoop2.monitor.engine.config.ricerche.IdPlugin;
import org.openspcoop2.monitor.engine.config.ricerche.Plugin;
import org.openspcoop2.monitor.engine.config.ricerche.ConfigurazioneRicerca;
import org.openspcoop2.monitor.engine.config.ricerche.IdConfigurazioneRicerca;

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
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.monitor.engine.config.ricerche.InfoPlugin}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.ricerche.InfoPlugin}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.ricerche.InfoPlugin}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public InfoPlugin readInfoPlugin(String fileName) throws DeserializerException {
		return (InfoPlugin) this.xmlToObj(fileName, InfoPlugin.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.monitor.engine.config.ricerche.InfoPlugin}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.ricerche.InfoPlugin}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.ricerche.InfoPlugin}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public InfoPlugin readInfoPlugin(File file) throws DeserializerException {
		return (InfoPlugin) this.xmlToObj(file, InfoPlugin.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.monitor.engine.config.ricerche.InfoPlugin}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.ricerche.InfoPlugin}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.ricerche.InfoPlugin}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public InfoPlugin readInfoPlugin(InputStream in) throws DeserializerException {
		return (InfoPlugin) this.xmlToObj(in, InfoPlugin.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.monitor.engine.config.ricerche.InfoPlugin}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.ricerche.InfoPlugin}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.ricerche.InfoPlugin}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public InfoPlugin readInfoPlugin(byte[] in) throws DeserializerException {
		return (InfoPlugin) this.xmlToObj(in, InfoPlugin.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.monitor.engine.config.ricerche.InfoPlugin}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.ricerche.InfoPlugin}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.ricerche.InfoPlugin}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public InfoPlugin readInfoPluginFromString(String in) throws DeserializerException {
		return (InfoPlugin) this.xmlToObj(in.getBytes(), InfoPlugin.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: configurazione-servizio-azione
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.monitor.engine.config.ricerche.ConfigurazioneServizioAzione}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.ricerche.ConfigurazioneServizioAzione}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.ricerche.ConfigurazioneServizioAzione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConfigurazioneServizioAzione readConfigurazioneServizioAzione(String fileName) throws DeserializerException {
		return (ConfigurazioneServizioAzione) this.xmlToObj(fileName, ConfigurazioneServizioAzione.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.monitor.engine.config.ricerche.ConfigurazioneServizioAzione}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.ricerche.ConfigurazioneServizioAzione}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.ricerche.ConfigurazioneServizioAzione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConfigurazioneServizioAzione readConfigurazioneServizioAzione(File file) throws DeserializerException {
		return (ConfigurazioneServizioAzione) this.xmlToObj(file, ConfigurazioneServizioAzione.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.monitor.engine.config.ricerche.ConfigurazioneServizioAzione}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.ricerche.ConfigurazioneServizioAzione}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.ricerche.ConfigurazioneServizioAzione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConfigurazioneServizioAzione readConfigurazioneServizioAzione(InputStream in) throws DeserializerException {
		return (ConfigurazioneServizioAzione) this.xmlToObj(in, ConfigurazioneServizioAzione.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.monitor.engine.config.ricerche.ConfigurazioneServizioAzione}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.ricerche.ConfigurazioneServizioAzione}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.ricerche.ConfigurazioneServizioAzione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConfigurazioneServizioAzione readConfigurazioneServizioAzione(byte[] in) throws DeserializerException {
		return (ConfigurazioneServizioAzione) this.xmlToObj(in, ConfigurazioneServizioAzione.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.monitor.engine.config.ricerche.ConfigurazioneServizioAzione}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.ricerche.ConfigurazioneServizioAzione}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.ricerche.ConfigurazioneServizioAzione}
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
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.monitor.engine.config.ricerche.ConfigurazioneServizio}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.ricerche.ConfigurazioneServizio}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.ricerche.ConfigurazioneServizio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConfigurazioneServizio readConfigurazioneServizio(String fileName) throws DeserializerException {
		return (ConfigurazioneServizio) this.xmlToObj(fileName, ConfigurazioneServizio.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.monitor.engine.config.ricerche.ConfigurazioneServizio}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.ricerche.ConfigurazioneServizio}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.ricerche.ConfigurazioneServizio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConfigurazioneServizio readConfigurazioneServizio(File file) throws DeserializerException {
		return (ConfigurazioneServizio) this.xmlToObj(file, ConfigurazioneServizio.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.monitor.engine.config.ricerche.ConfigurazioneServizio}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.ricerche.ConfigurazioneServizio}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.ricerche.ConfigurazioneServizio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConfigurazioneServizio readConfigurazioneServizio(InputStream in) throws DeserializerException {
		return (ConfigurazioneServizio) this.xmlToObj(in, ConfigurazioneServizio.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.monitor.engine.config.ricerche.ConfigurazioneServizio}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.ricerche.ConfigurazioneServizio}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.ricerche.ConfigurazioneServizio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConfigurazioneServizio readConfigurazioneServizio(byte[] in) throws DeserializerException {
		return (ConfigurazioneServizio) this.xmlToObj(in, ConfigurazioneServizio.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.monitor.engine.config.ricerche.ConfigurazioneServizio}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.ricerche.ConfigurazioneServizio}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.ricerche.ConfigurazioneServizio}
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
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.monitor.engine.config.ricerche.IdConfigurazioneServizio}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.ricerche.IdConfigurazioneServizio}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.ricerche.IdConfigurazioneServizio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdConfigurazioneServizio readIdConfigurazioneServizio(String fileName) throws DeserializerException {
		return (IdConfigurazioneServizio) this.xmlToObj(fileName, IdConfigurazioneServizio.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.monitor.engine.config.ricerche.IdConfigurazioneServizio}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.ricerche.IdConfigurazioneServizio}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.ricerche.IdConfigurazioneServizio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdConfigurazioneServizio readIdConfigurazioneServizio(File file) throws DeserializerException {
		return (IdConfigurazioneServizio) this.xmlToObj(file, IdConfigurazioneServizio.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.monitor.engine.config.ricerche.IdConfigurazioneServizio}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.ricerche.IdConfigurazioneServizio}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.ricerche.IdConfigurazioneServizio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdConfigurazioneServizio readIdConfigurazioneServizio(InputStream in) throws DeserializerException {
		return (IdConfigurazioneServizio) this.xmlToObj(in, IdConfigurazioneServizio.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.monitor.engine.config.ricerche.IdConfigurazioneServizio}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.ricerche.IdConfigurazioneServizio}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.ricerche.IdConfigurazioneServizio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdConfigurazioneServizio readIdConfigurazioneServizio(byte[] in) throws DeserializerException {
		return (IdConfigurazioneServizio) this.xmlToObj(in, IdConfigurazioneServizio.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.monitor.engine.config.ricerche.IdConfigurazioneServizio}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.ricerche.IdConfigurazioneServizio}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.ricerche.IdConfigurazioneServizio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdConfigurazioneServizio readIdConfigurazioneServizioFromString(String in) throws DeserializerException {
		return (IdConfigurazioneServizio) this.xmlToObj(in.getBytes(), IdConfigurazioneServizio.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: id-configurazione-servizio-azione
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.monitor.engine.config.ricerche.IdConfigurazioneServizioAzione}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.ricerche.IdConfigurazioneServizioAzione}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.ricerche.IdConfigurazioneServizioAzione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdConfigurazioneServizioAzione readIdConfigurazioneServizioAzione(String fileName) throws DeserializerException {
		return (IdConfigurazioneServizioAzione) this.xmlToObj(fileName, IdConfigurazioneServizioAzione.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.monitor.engine.config.ricerche.IdConfigurazioneServizioAzione}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.ricerche.IdConfigurazioneServizioAzione}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.ricerche.IdConfigurazioneServizioAzione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdConfigurazioneServizioAzione readIdConfigurazioneServizioAzione(File file) throws DeserializerException {
		return (IdConfigurazioneServizioAzione) this.xmlToObj(file, IdConfigurazioneServizioAzione.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.monitor.engine.config.ricerche.IdConfigurazioneServizioAzione}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.ricerche.IdConfigurazioneServizioAzione}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.ricerche.IdConfigurazioneServizioAzione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdConfigurazioneServizioAzione readIdConfigurazioneServizioAzione(InputStream in) throws DeserializerException {
		return (IdConfigurazioneServizioAzione) this.xmlToObj(in, IdConfigurazioneServizioAzione.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.monitor.engine.config.ricerche.IdConfigurazioneServizioAzione}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.ricerche.IdConfigurazioneServizioAzione}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.ricerche.IdConfigurazioneServizioAzione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdConfigurazioneServizioAzione readIdConfigurazioneServizioAzione(byte[] in) throws DeserializerException {
		return (IdConfigurazioneServizioAzione) this.xmlToObj(in, IdConfigurazioneServizioAzione.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.monitor.engine.config.ricerche.IdConfigurazioneServizioAzione}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.ricerche.IdConfigurazioneServizioAzione}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.ricerche.IdConfigurazioneServizioAzione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdConfigurazioneServizioAzione readIdConfigurazioneServizioAzioneFromString(String in) throws DeserializerException {
		return (IdConfigurazioneServizioAzione) this.xmlToObj(in.getBytes(), IdConfigurazioneServizioAzione.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: id-plugin
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.monitor.engine.config.ricerche.IdPlugin}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.ricerche.IdPlugin}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.ricerche.IdPlugin}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdPlugin readIdPlugin(String fileName) throws DeserializerException {
		return (IdPlugin) this.xmlToObj(fileName, IdPlugin.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.monitor.engine.config.ricerche.IdPlugin}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.ricerche.IdPlugin}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.ricerche.IdPlugin}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdPlugin readIdPlugin(File file) throws DeserializerException {
		return (IdPlugin) this.xmlToObj(file, IdPlugin.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.monitor.engine.config.ricerche.IdPlugin}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.ricerche.IdPlugin}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.ricerche.IdPlugin}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdPlugin readIdPlugin(InputStream in) throws DeserializerException {
		return (IdPlugin) this.xmlToObj(in, IdPlugin.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.monitor.engine.config.ricerche.IdPlugin}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.ricerche.IdPlugin}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.ricerche.IdPlugin}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdPlugin readIdPlugin(byte[] in) throws DeserializerException {
		return (IdPlugin) this.xmlToObj(in, IdPlugin.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.monitor.engine.config.ricerche.IdPlugin}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.ricerche.IdPlugin}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.ricerche.IdPlugin}
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
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.monitor.engine.config.ricerche.Plugin}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.ricerche.Plugin}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.ricerche.Plugin}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Plugin readPlugin(String fileName) throws DeserializerException {
		return (Plugin) this.xmlToObj(fileName, Plugin.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.monitor.engine.config.ricerche.Plugin}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.ricerche.Plugin}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.ricerche.Plugin}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Plugin readPlugin(File file) throws DeserializerException {
		return (Plugin) this.xmlToObj(file, Plugin.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.monitor.engine.config.ricerche.Plugin}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.ricerche.Plugin}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.ricerche.Plugin}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Plugin readPlugin(InputStream in) throws DeserializerException {
		return (Plugin) this.xmlToObj(in, Plugin.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.monitor.engine.config.ricerche.Plugin}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.ricerche.Plugin}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.ricerche.Plugin}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Plugin readPlugin(byte[] in) throws DeserializerException {
		return (Plugin) this.xmlToObj(in, Plugin.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.monitor.engine.config.ricerche.Plugin}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.ricerche.Plugin}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.ricerche.Plugin}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Plugin readPluginFromString(String in) throws DeserializerException {
		return (Plugin) this.xmlToObj(in.getBytes(), Plugin.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: configurazione-ricerca
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.monitor.engine.config.ricerche.ConfigurazioneRicerca}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.ricerche.ConfigurazioneRicerca}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.ricerche.ConfigurazioneRicerca}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConfigurazioneRicerca readConfigurazioneRicerca(String fileName) throws DeserializerException {
		return (ConfigurazioneRicerca) this.xmlToObj(fileName, ConfigurazioneRicerca.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.monitor.engine.config.ricerche.ConfigurazioneRicerca}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.ricerche.ConfigurazioneRicerca}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.ricerche.ConfigurazioneRicerca}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConfigurazioneRicerca readConfigurazioneRicerca(File file) throws DeserializerException {
		return (ConfigurazioneRicerca) this.xmlToObj(file, ConfigurazioneRicerca.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.monitor.engine.config.ricerche.ConfigurazioneRicerca}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.ricerche.ConfigurazioneRicerca}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.ricerche.ConfigurazioneRicerca}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConfigurazioneRicerca readConfigurazioneRicerca(InputStream in) throws DeserializerException {
		return (ConfigurazioneRicerca) this.xmlToObj(in, ConfigurazioneRicerca.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.monitor.engine.config.ricerche.ConfigurazioneRicerca}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.ricerche.ConfigurazioneRicerca}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.ricerche.ConfigurazioneRicerca}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConfigurazioneRicerca readConfigurazioneRicerca(byte[] in) throws DeserializerException {
		return (ConfigurazioneRicerca) this.xmlToObj(in, ConfigurazioneRicerca.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.monitor.engine.config.ricerche.ConfigurazioneRicerca}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.ricerche.ConfigurazioneRicerca}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.ricerche.ConfigurazioneRicerca}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConfigurazioneRicerca readConfigurazioneRicercaFromString(String in) throws DeserializerException {
		return (ConfigurazioneRicerca) this.xmlToObj(in.getBytes(), ConfigurazioneRicerca.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: id-configurazione-ricerca
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.monitor.engine.config.ricerche.IdConfigurazioneRicerca}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.ricerche.IdConfigurazioneRicerca}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.ricerche.IdConfigurazioneRicerca}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdConfigurazioneRicerca readIdConfigurazioneRicerca(String fileName) throws DeserializerException {
		return (IdConfigurazioneRicerca) this.xmlToObj(fileName, IdConfigurazioneRicerca.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.monitor.engine.config.ricerche.IdConfigurazioneRicerca}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.ricerche.IdConfigurazioneRicerca}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.ricerche.IdConfigurazioneRicerca}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdConfigurazioneRicerca readIdConfigurazioneRicerca(File file) throws DeserializerException {
		return (IdConfigurazioneRicerca) this.xmlToObj(file, IdConfigurazioneRicerca.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.monitor.engine.config.ricerche.IdConfigurazioneRicerca}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.ricerche.IdConfigurazioneRicerca}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.ricerche.IdConfigurazioneRicerca}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdConfigurazioneRicerca readIdConfigurazioneRicerca(InputStream in) throws DeserializerException {
		return (IdConfigurazioneRicerca) this.xmlToObj(in, IdConfigurazioneRicerca.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.monitor.engine.config.ricerche.IdConfigurazioneRicerca}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.ricerche.IdConfigurazioneRicerca}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.ricerche.IdConfigurazioneRicerca}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdConfigurazioneRicerca readIdConfigurazioneRicerca(byte[] in) throws DeserializerException {
		return (IdConfigurazioneRicerca) this.xmlToObj(in, IdConfigurazioneRicerca.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.monitor.engine.config.ricerche.IdConfigurazioneRicerca}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.ricerche.IdConfigurazioneRicerca}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.ricerche.IdConfigurazioneRicerca}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdConfigurazioneRicerca readIdConfigurazioneRicercaFromString(String in) throws DeserializerException {
		return (IdConfigurazioneRicerca) this.xmlToObj(in.getBytes(), IdConfigurazioneRicerca.class);
	}	
	
	
	

}
