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
package org.openspcoop2.core.plugins.utils.serializer;

import org.openspcoop2.generic_project.exception.DeserializerException;

import org.openspcoop2.core.plugins.IdConfigurazioneFiltro;
import org.openspcoop2.core.plugins.ElencoIdConfigurazioneFiltro;
import org.openspcoop2.core.plugins.Plugin;
import org.openspcoop2.core.plugins.ElencoPlugin;
import org.openspcoop2.core.plugins.ConfigurazioneServizioAzione;
import org.openspcoop2.core.plugins.IdConfigurazioneServizio;
import org.openspcoop2.core.plugins.ConfigurazioneServizio;
import org.openspcoop2.core.plugins.IdConfigurazioneServizioAzione;
import org.openspcoop2.core.plugins.ConfigurazioneFiltro;
import org.openspcoop2.core.plugins.ElencoConfigurazioneFiltro;
import org.openspcoop2.core.plugins.PluginServizioAzioneCompatibilita;
import org.openspcoop2.core.plugins.IdPlugin;
import org.openspcoop2.core.plugins.ElencoIdPlugin;
import org.openspcoop2.core.plugins.PluginServizioCompatibilita;
import org.openspcoop2.core.plugins.PluginProprietaCompatibilita;
import org.openspcoop2.core.plugins.ElencoIdConfigurazioneServizio;
import org.openspcoop2.core.plugins.ElencoConfigurazioneServizio;

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
	 Object: id-configurazione-filtro
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.plugins.IdConfigurazioneFiltro}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.plugins.IdConfigurazioneFiltro}
	 * @return Object type {@link org.openspcoop2.core.plugins.IdConfigurazioneFiltro}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdConfigurazioneFiltro readIdConfigurazioneFiltro(String fileName) throws DeserializerException {
		return (IdConfigurazioneFiltro) this.xmlToObj(fileName, IdConfigurazioneFiltro.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.plugins.IdConfigurazioneFiltro}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.plugins.IdConfigurazioneFiltro}
	 * @return Object type {@link org.openspcoop2.core.plugins.IdConfigurazioneFiltro}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdConfigurazioneFiltro readIdConfigurazioneFiltro(File file) throws DeserializerException {
		return (IdConfigurazioneFiltro) this.xmlToObj(file, IdConfigurazioneFiltro.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.plugins.IdConfigurazioneFiltro}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.plugins.IdConfigurazioneFiltro}
	 * @return Object type {@link org.openspcoop2.core.plugins.IdConfigurazioneFiltro}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdConfigurazioneFiltro readIdConfigurazioneFiltro(InputStream in) throws DeserializerException {
		return (IdConfigurazioneFiltro) this.xmlToObj(in, IdConfigurazioneFiltro.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.plugins.IdConfigurazioneFiltro}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.plugins.IdConfigurazioneFiltro}
	 * @return Object type {@link org.openspcoop2.core.plugins.IdConfigurazioneFiltro}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdConfigurazioneFiltro readIdConfigurazioneFiltro(byte[] in) throws DeserializerException {
		return (IdConfigurazioneFiltro) this.xmlToObj(in, IdConfigurazioneFiltro.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.plugins.IdConfigurazioneFiltro}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.plugins.IdConfigurazioneFiltro}
	 * @return Object type {@link org.openspcoop2.core.plugins.IdConfigurazioneFiltro}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdConfigurazioneFiltro readIdConfigurazioneFiltroFromString(String in) throws DeserializerException {
		return (IdConfigurazioneFiltro) this.xmlToObj(in.getBytes(), IdConfigurazioneFiltro.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: elenco-id-configurazione-filtro
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.plugins.ElencoIdConfigurazioneFiltro}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.plugins.ElencoIdConfigurazioneFiltro}
	 * @return Object type {@link org.openspcoop2.core.plugins.ElencoIdConfigurazioneFiltro}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ElencoIdConfigurazioneFiltro readElencoIdConfigurazioneFiltro(String fileName) throws DeserializerException {
		return (ElencoIdConfigurazioneFiltro) this.xmlToObj(fileName, ElencoIdConfigurazioneFiltro.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.plugins.ElencoIdConfigurazioneFiltro}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.plugins.ElencoIdConfigurazioneFiltro}
	 * @return Object type {@link org.openspcoop2.core.plugins.ElencoIdConfigurazioneFiltro}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ElencoIdConfigurazioneFiltro readElencoIdConfigurazioneFiltro(File file) throws DeserializerException {
		return (ElencoIdConfigurazioneFiltro) this.xmlToObj(file, ElencoIdConfigurazioneFiltro.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.plugins.ElencoIdConfigurazioneFiltro}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.plugins.ElencoIdConfigurazioneFiltro}
	 * @return Object type {@link org.openspcoop2.core.plugins.ElencoIdConfigurazioneFiltro}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ElencoIdConfigurazioneFiltro readElencoIdConfigurazioneFiltro(InputStream in) throws DeserializerException {
		return (ElencoIdConfigurazioneFiltro) this.xmlToObj(in, ElencoIdConfigurazioneFiltro.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.plugins.ElencoIdConfigurazioneFiltro}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.plugins.ElencoIdConfigurazioneFiltro}
	 * @return Object type {@link org.openspcoop2.core.plugins.ElencoIdConfigurazioneFiltro}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ElencoIdConfigurazioneFiltro readElencoIdConfigurazioneFiltro(byte[] in) throws DeserializerException {
		return (ElencoIdConfigurazioneFiltro) this.xmlToObj(in, ElencoIdConfigurazioneFiltro.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.plugins.ElencoIdConfigurazioneFiltro}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.plugins.ElencoIdConfigurazioneFiltro}
	 * @return Object type {@link org.openspcoop2.core.plugins.ElencoIdConfigurazioneFiltro}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ElencoIdConfigurazioneFiltro readElencoIdConfigurazioneFiltroFromString(String in) throws DeserializerException {
		return (ElencoIdConfigurazioneFiltro) this.xmlToObj(in.getBytes(), ElencoIdConfigurazioneFiltro.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: plugin
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.plugins.Plugin}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.plugins.Plugin}
	 * @return Object type {@link org.openspcoop2.core.plugins.Plugin}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Plugin readPlugin(String fileName) throws DeserializerException {
		return (Plugin) this.xmlToObj(fileName, Plugin.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.plugins.Plugin}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.plugins.Plugin}
	 * @return Object type {@link org.openspcoop2.core.plugins.Plugin}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Plugin readPlugin(File file) throws DeserializerException {
		return (Plugin) this.xmlToObj(file, Plugin.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.plugins.Plugin}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.plugins.Plugin}
	 * @return Object type {@link org.openspcoop2.core.plugins.Plugin}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Plugin readPlugin(InputStream in) throws DeserializerException {
		return (Plugin) this.xmlToObj(in, Plugin.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.plugins.Plugin}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.plugins.Plugin}
	 * @return Object type {@link org.openspcoop2.core.plugins.Plugin}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Plugin readPlugin(byte[] in) throws DeserializerException {
		return (Plugin) this.xmlToObj(in, Plugin.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.plugins.Plugin}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.plugins.Plugin}
	 * @return Object type {@link org.openspcoop2.core.plugins.Plugin}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Plugin readPluginFromString(String in) throws DeserializerException {
		return (Plugin) this.xmlToObj(in.getBytes(), Plugin.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: elenco-plugin
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.plugins.ElencoPlugin}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.plugins.ElencoPlugin}
	 * @return Object type {@link org.openspcoop2.core.plugins.ElencoPlugin}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ElencoPlugin readElencoPlugin(String fileName) throws DeserializerException {
		return (ElencoPlugin) this.xmlToObj(fileName, ElencoPlugin.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.plugins.ElencoPlugin}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.plugins.ElencoPlugin}
	 * @return Object type {@link org.openspcoop2.core.plugins.ElencoPlugin}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ElencoPlugin readElencoPlugin(File file) throws DeserializerException {
		return (ElencoPlugin) this.xmlToObj(file, ElencoPlugin.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.plugins.ElencoPlugin}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.plugins.ElencoPlugin}
	 * @return Object type {@link org.openspcoop2.core.plugins.ElencoPlugin}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ElencoPlugin readElencoPlugin(InputStream in) throws DeserializerException {
		return (ElencoPlugin) this.xmlToObj(in, ElencoPlugin.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.plugins.ElencoPlugin}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.plugins.ElencoPlugin}
	 * @return Object type {@link org.openspcoop2.core.plugins.ElencoPlugin}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ElencoPlugin readElencoPlugin(byte[] in) throws DeserializerException {
		return (ElencoPlugin) this.xmlToObj(in, ElencoPlugin.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.plugins.ElencoPlugin}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.plugins.ElencoPlugin}
	 * @return Object type {@link org.openspcoop2.core.plugins.ElencoPlugin}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ElencoPlugin readElencoPluginFromString(String in) throws DeserializerException {
		return (ElencoPlugin) this.xmlToObj(in.getBytes(), ElencoPlugin.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: configurazione-servizio-azione
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.plugins.ConfigurazioneServizioAzione}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.plugins.ConfigurazioneServizioAzione}
	 * @return Object type {@link org.openspcoop2.core.plugins.ConfigurazioneServizioAzione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConfigurazioneServizioAzione readConfigurazioneServizioAzione(String fileName) throws DeserializerException {
		return (ConfigurazioneServizioAzione) this.xmlToObj(fileName, ConfigurazioneServizioAzione.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.plugins.ConfigurazioneServizioAzione}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.plugins.ConfigurazioneServizioAzione}
	 * @return Object type {@link org.openspcoop2.core.plugins.ConfigurazioneServizioAzione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConfigurazioneServizioAzione readConfigurazioneServizioAzione(File file) throws DeserializerException {
		return (ConfigurazioneServizioAzione) this.xmlToObj(file, ConfigurazioneServizioAzione.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.plugins.ConfigurazioneServizioAzione}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.plugins.ConfigurazioneServizioAzione}
	 * @return Object type {@link org.openspcoop2.core.plugins.ConfigurazioneServizioAzione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConfigurazioneServizioAzione readConfigurazioneServizioAzione(InputStream in) throws DeserializerException {
		return (ConfigurazioneServizioAzione) this.xmlToObj(in, ConfigurazioneServizioAzione.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.plugins.ConfigurazioneServizioAzione}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.plugins.ConfigurazioneServizioAzione}
	 * @return Object type {@link org.openspcoop2.core.plugins.ConfigurazioneServizioAzione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConfigurazioneServizioAzione readConfigurazioneServizioAzione(byte[] in) throws DeserializerException {
		return (ConfigurazioneServizioAzione) this.xmlToObj(in, ConfigurazioneServizioAzione.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.plugins.ConfigurazioneServizioAzione}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.plugins.ConfigurazioneServizioAzione}
	 * @return Object type {@link org.openspcoop2.core.plugins.ConfigurazioneServizioAzione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConfigurazioneServizioAzione readConfigurazioneServizioAzioneFromString(String in) throws DeserializerException {
		return (ConfigurazioneServizioAzione) this.xmlToObj(in.getBytes(), ConfigurazioneServizioAzione.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: id-configurazione-servizio
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.plugins.IdConfigurazioneServizio}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.plugins.IdConfigurazioneServizio}
	 * @return Object type {@link org.openspcoop2.core.plugins.IdConfigurazioneServizio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdConfigurazioneServizio readIdConfigurazioneServizio(String fileName) throws DeserializerException {
		return (IdConfigurazioneServizio) this.xmlToObj(fileName, IdConfigurazioneServizio.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.plugins.IdConfigurazioneServizio}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.plugins.IdConfigurazioneServizio}
	 * @return Object type {@link org.openspcoop2.core.plugins.IdConfigurazioneServizio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdConfigurazioneServizio readIdConfigurazioneServizio(File file) throws DeserializerException {
		return (IdConfigurazioneServizio) this.xmlToObj(file, IdConfigurazioneServizio.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.plugins.IdConfigurazioneServizio}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.plugins.IdConfigurazioneServizio}
	 * @return Object type {@link org.openspcoop2.core.plugins.IdConfigurazioneServizio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdConfigurazioneServizio readIdConfigurazioneServizio(InputStream in) throws DeserializerException {
		return (IdConfigurazioneServizio) this.xmlToObj(in, IdConfigurazioneServizio.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.plugins.IdConfigurazioneServizio}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.plugins.IdConfigurazioneServizio}
	 * @return Object type {@link org.openspcoop2.core.plugins.IdConfigurazioneServizio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdConfigurazioneServizio readIdConfigurazioneServizio(byte[] in) throws DeserializerException {
		return (IdConfigurazioneServizio) this.xmlToObj(in, IdConfigurazioneServizio.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.plugins.IdConfigurazioneServizio}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.plugins.IdConfigurazioneServizio}
	 * @return Object type {@link org.openspcoop2.core.plugins.IdConfigurazioneServizio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdConfigurazioneServizio readIdConfigurazioneServizioFromString(String in) throws DeserializerException {
		return (IdConfigurazioneServizio) this.xmlToObj(in.getBytes(), IdConfigurazioneServizio.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: configurazione-servizio
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.plugins.ConfigurazioneServizio}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.plugins.ConfigurazioneServizio}
	 * @return Object type {@link org.openspcoop2.core.plugins.ConfigurazioneServizio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConfigurazioneServizio readConfigurazioneServizio(String fileName) throws DeserializerException {
		return (ConfigurazioneServizio) this.xmlToObj(fileName, ConfigurazioneServizio.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.plugins.ConfigurazioneServizio}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.plugins.ConfigurazioneServizio}
	 * @return Object type {@link org.openspcoop2.core.plugins.ConfigurazioneServizio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConfigurazioneServizio readConfigurazioneServizio(File file) throws DeserializerException {
		return (ConfigurazioneServizio) this.xmlToObj(file, ConfigurazioneServizio.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.plugins.ConfigurazioneServizio}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.plugins.ConfigurazioneServizio}
	 * @return Object type {@link org.openspcoop2.core.plugins.ConfigurazioneServizio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConfigurazioneServizio readConfigurazioneServizio(InputStream in) throws DeserializerException {
		return (ConfigurazioneServizio) this.xmlToObj(in, ConfigurazioneServizio.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.plugins.ConfigurazioneServizio}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.plugins.ConfigurazioneServizio}
	 * @return Object type {@link org.openspcoop2.core.plugins.ConfigurazioneServizio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConfigurazioneServizio readConfigurazioneServizio(byte[] in) throws DeserializerException {
		return (ConfigurazioneServizio) this.xmlToObj(in, ConfigurazioneServizio.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.plugins.ConfigurazioneServizio}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.plugins.ConfigurazioneServizio}
	 * @return Object type {@link org.openspcoop2.core.plugins.ConfigurazioneServizio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConfigurazioneServizio readConfigurazioneServizioFromString(String in) throws DeserializerException {
		return (ConfigurazioneServizio) this.xmlToObj(in.getBytes(), ConfigurazioneServizio.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: id-configurazione-servizio-azione
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.plugins.IdConfigurazioneServizioAzione}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.plugins.IdConfigurazioneServizioAzione}
	 * @return Object type {@link org.openspcoop2.core.plugins.IdConfigurazioneServizioAzione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdConfigurazioneServizioAzione readIdConfigurazioneServizioAzione(String fileName) throws DeserializerException {
		return (IdConfigurazioneServizioAzione) this.xmlToObj(fileName, IdConfigurazioneServizioAzione.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.plugins.IdConfigurazioneServizioAzione}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.plugins.IdConfigurazioneServizioAzione}
	 * @return Object type {@link org.openspcoop2.core.plugins.IdConfigurazioneServizioAzione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdConfigurazioneServizioAzione readIdConfigurazioneServizioAzione(File file) throws DeserializerException {
		return (IdConfigurazioneServizioAzione) this.xmlToObj(file, IdConfigurazioneServizioAzione.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.plugins.IdConfigurazioneServizioAzione}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.plugins.IdConfigurazioneServizioAzione}
	 * @return Object type {@link org.openspcoop2.core.plugins.IdConfigurazioneServizioAzione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdConfigurazioneServizioAzione readIdConfigurazioneServizioAzione(InputStream in) throws DeserializerException {
		return (IdConfigurazioneServizioAzione) this.xmlToObj(in, IdConfigurazioneServizioAzione.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.plugins.IdConfigurazioneServizioAzione}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.plugins.IdConfigurazioneServizioAzione}
	 * @return Object type {@link org.openspcoop2.core.plugins.IdConfigurazioneServizioAzione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdConfigurazioneServizioAzione readIdConfigurazioneServizioAzione(byte[] in) throws DeserializerException {
		return (IdConfigurazioneServizioAzione) this.xmlToObj(in, IdConfigurazioneServizioAzione.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.plugins.IdConfigurazioneServizioAzione}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.plugins.IdConfigurazioneServizioAzione}
	 * @return Object type {@link org.openspcoop2.core.plugins.IdConfigurazioneServizioAzione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdConfigurazioneServizioAzione readIdConfigurazioneServizioAzioneFromString(String in) throws DeserializerException {
		return (IdConfigurazioneServizioAzione) this.xmlToObj(in.getBytes(), IdConfigurazioneServizioAzione.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: configurazione-filtro
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.plugins.ConfigurazioneFiltro}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.plugins.ConfigurazioneFiltro}
	 * @return Object type {@link org.openspcoop2.core.plugins.ConfigurazioneFiltro}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConfigurazioneFiltro readConfigurazioneFiltro(String fileName) throws DeserializerException {
		return (ConfigurazioneFiltro) this.xmlToObj(fileName, ConfigurazioneFiltro.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.plugins.ConfigurazioneFiltro}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.plugins.ConfigurazioneFiltro}
	 * @return Object type {@link org.openspcoop2.core.plugins.ConfigurazioneFiltro}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConfigurazioneFiltro readConfigurazioneFiltro(File file) throws DeserializerException {
		return (ConfigurazioneFiltro) this.xmlToObj(file, ConfigurazioneFiltro.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.plugins.ConfigurazioneFiltro}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.plugins.ConfigurazioneFiltro}
	 * @return Object type {@link org.openspcoop2.core.plugins.ConfigurazioneFiltro}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConfigurazioneFiltro readConfigurazioneFiltro(InputStream in) throws DeserializerException {
		return (ConfigurazioneFiltro) this.xmlToObj(in, ConfigurazioneFiltro.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.plugins.ConfigurazioneFiltro}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.plugins.ConfigurazioneFiltro}
	 * @return Object type {@link org.openspcoop2.core.plugins.ConfigurazioneFiltro}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConfigurazioneFiltro readConfigurazioneFiltro(byte[] in) throws DeserializerException {
		return (ConfigurazioneFiltro) this.xmlToObj(in, ConfigurazioneFiltro.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.plugins.ConfigurazioneFiltro}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.plugins.ConfigurazioneFiltro}
	 * @return Object type {@link org.openspcoop2.core.plugins.ConfigurazioneFiltro}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConfigurazioneFiltro readConfigurazioneFiltroFromString(String in) throws DeserializerException {
		return (ConfigurazioneFiltro) this.xmlToObj(in.getBytes(), ConfigurazioneFiltro.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: elenco-configurazione-filtro
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.plugins.ElencoConfigurazioneFiltro}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.plugins.ElencoConfigurazioneFiltro}
	 * @return Object type {@link org.openspcoop2.core.plugins.ElencoConfigurazioneFiltro}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ElencoConfigurazioneFiltro readElencoConfigurazioneFiltro(String fileName) throws DeserializerException {
		return (ElencoConfigurazioneFiltro) this.xmlToObj(fileName, ElencoConfigurazioneFiltro.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.plugins.ElencoConfigurazioneFiltro}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.plugins.ElencoConfigurazioneFiltro}
	 * @return Object type {@link org.openspcoop2.core.plugins.ElencoConfigurazioneFiltro}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ElencoConfigurazioneFiltro readElencoConfigurazioneFiltro(File file) throws DeserializerException {
		return (ElencoConfigurazioneFiltro) this.xmlToObj(file, ElencoConfigurazioneFiltro.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.plugins.ElencoConfigurazioneFiltro}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.plugins.ElencoConfigurazioneFiltro}
	 * @return Object type {@link org.openspcoop2.core.plugins.ElencoConfigurazioneFiltro}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ElencoConfigurazioneFiltro readElencoConfigurazioneFiltro(InputStream in) throws DeserializerException {
		return (ElencoConfigurazioneFiltro) this.xmlToObj(in, ElencoConfigurazioneFiltro.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.plugins.ElencoConfigurazioneFiltro}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.plugins.ElencoConfigurazioneFiltro}
	 * @return Object type {@link org.openspcoop2.core.plugins.ElencoConfigurazioneFiltro}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ElencoConfigurazioneFiltro readElencoConfigurazioneFiltro(byte[] in) throws DeserializerException {
		return (ElencoConfigurazioneFiltro) this.xmlToObj(in, ElencoConfigurazioneFiltro.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.plugins.ElencoConfigurazioneFiltro}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.plugins.ElencoConfigurazioneFiltro}
	 * @return Object type {@link org.openspcoop2.core.plugins.ElencoConfigurazioneFiltro}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ElencoConfigurazioneFiltro readElencoConfigurazioneFiltroFromString(String in) throws DeserializerException {
		return (ElencoConfigurazioneFiltro) this.xmlToObj(in.getBytes(), ElencoConfigurazioneFiltro.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: plugin-servizio-azione-compatibilita
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.plugins.PluginServizioAzioneCompatibilita}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.plugins.PluginServizioAzioneCompatibilita}
	 * @return Object type {@link org.openspcoop2.core.plugins.PluginServizioAzioneCompatibilita}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PluginServizioAzioneCompatibilita readPluginServizioAzioneCompatibilita(String fileName) throws DeserializerException {
		return (PluginServizioAzioneCompatibilita) this.xmlToObj(fileName, PluginServizioAzioneCompatibilita.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.plugins.PluginServizioAzioneCompatibilita}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.plugins.PluginServizioAzioneCompatibilita}
	 * @return Object type {@link org.openspcoop2.core.plugins.PluginServizioAzioneCompatibilita}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PluginServizioAzioneCompatibilita readPluginServizioAzioneCompatibilita(File file) throws DeserializerException {
		return (PluginServizioAzioneCompatibilita) this.xmlToObj(file, PluginServizioAzioneCompatibilita.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.plugins.PluginServizioAzioneCompatibilita}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.plugins.PluginServizioAzioneCompatibilita}
	 * @return Object type {@link org.openspcoop2.core.plugins.PluginServizioAzioneCompatibilita}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PluginServizioAzioneCompatibilita readPluginServizioAzioneCompatibilita(InputStream in) throws DeserializerException {
		return (PluginServizioAzioneCompatibilita) this.xmlToObj(in, PluginServizioAzioneCompatibilita.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.plugins.PluginServizioAzioneCompatibilita}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.plugins.PluginServizioAzioneCompatibilita}
	 * @return Object type {@link org.openspcoop2.core.plugins.PluginServizioAzioneCompatibilita}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PluginServizioAzioneCompatibilita readPluginServizioAzioneCompatibilita(byte[] in) throws DeserializerException {
		return (PluginServizioAzioneCompatibilita) this.xmlToObj(in, PluginServizioAzioneCompatibilita.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.plugins.PluginServizioAzioneCompatibilita}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.plugins.PluginServizioAzioneCompatibilita}
	 * @return Object type {@link org.openspcoop2.core.plugins.PluginServizioAzioneCompatibilita}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PluginServizioAzioneCompatibilita readPluginServizioAzioneCompatibilitaFromString(String in) throws DeserializerException {
		return (PluginServizioAzioneCompatibilita) this.xmlToObj(in.getBytes(), PluginServizioAzioneCompatibilita.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: id-plugin
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.plugins.IdPlugin}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.plugins.IdPlugin}
	 * @return Object type {@link org.openspcoop2.core.plugins.IdPlugin}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdPlugin readIdPlugin(String fileName) throws DeserializerException {
		return (IdPlugin) this.xmlToObj(fileName, IdPlugin.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.plugins.IdPlugin}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.plugins.IdPlugin}
	 * @return Object type {@link org.openspcoop2.core.plugins.IdPlugin}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdPlugin readIdPlugin(File file) throws DeserializerException {
		return (IdPlugin) this.xmlToObj(file, IdPlugin.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.plugins.IdPlugin}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.plugins.IdPlugin}
	 * @return Object type {@link org.openspcoop2.core.plugins.IdPlugin}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdPlugin readIdPlugin(InputStream in) throws DeserializerException {
		return (IdPlugin) this.xmlToObj(in, IdPlugin.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.plugins.IdPlugin}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.plugins.IdPlugin}
	 * @return Object type {@link org.openspcoop2.core.plugins.IdPlugin}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdPlugin readIdPlugin(byte[] in) throws DeserializerException {
		return (IdPlugin) this.xmlToObj(in, IdPlugin.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.plugins.IdPlugin}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.plugins.IdPlugin}
	 * @return Object type {@link org.openspcoop2.core.plugins.IdPlugin}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdPlugin readIdPluginFromString(String in) throws DeserializerException {
		return (IdPlugin) this.xmlToObj(in.getBytes(), IdPlugin.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: elenco-id-plugin
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.plugins.ElencoIdPlugin}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.plugins.ElencoIdPlugin}
	 * @return Object type {@link org.openspcoop2.core.plugins.ElencoIdPlugin}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ElencoIdPlugin readElencoIdPlugin(String fileName) throws DeserializerException {
		return (ElencoIdPlugin) this.xmlToObj(fileName, ElencoIdPlugin.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.plugins.ElencoIdPlugin}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.plugins.ElencoIdPlugin}
	 * @return Object type {@link org.openspcoop2.core.plugins.ElencoIdPlugin}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ElencoIdPlugin readElencoIdPlugin(File file) throws DeserializerException {
		return (ElencoIdPlugin) this.xmlToObj(file, ElencoIdPlugin.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.plugins.ElencoIdPlugin}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.plugins.ElencoIdPlugin}
	 * @return Object type {@link org.openspcoop2.core.plugins.ElencoIdPlugin}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ElencoIdPlugin readElencoIdPlugin(InputStream in) throws DeserializerException {
		return (ElencoIdPlugin) this.xmlToObj(in, ElencoIdPlugin.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.plugins.ElencoIdPlugin}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.plugins.ElencoIdPlugin}
	 * @return Object type {@link org.openspcoop2.core.plugins.ElencoIdPlugin}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ElencoIdPlugin readElencoIdPlugin(byte[] in) throws DeserializerException {
		return (ElencoIdPlugin) this.xmlToObj(in, ElencoIdPlugin.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.plugins.ElencoIdPlugin}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.plugins.ElencoIdPlugin}
	 * @return Object type {@link org.openspcoop2.core.plugins.ElencoIdPlugin}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ElencoIdPlugin readElencoIdPluginFromString(String in) throws DeserializerException {
		return (ElencoIdPlugin) this.xmlToObj(in.getBytes(), ElencoIdPlugin.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: plugin-servizio-compatibilita
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.plugins.PluginServizioCompatibilita}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.plugins.PluginServizioCompatibilita}
	 * @return Object type {@link org.openspcoop2.core.plugins.PluginServizioCompatibilita}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PluginServizioCompatibilita readPluginServizioCompatibilita(String fileName) throws DeserializerException {
		return (PluginServizioCompatibilita) this.xmlToObj(fileName, PluginServizioCompatibilita.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.plugins.PluginServizioCompatibilita}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.plugins.PluginServizioCompatibilita}
	 * @return Object type {@link org.openspcoop2.core.plugins.PluginServizioCompatibilita}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PluginServizioCompatibilita readPluginServizioCompatibilita(File file) throws DeserializerException {
		return (PluginServizioCompatibilita) this.xmlToObj(file, PluginServizioCompatibilita.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.plugins.PluginServizioCompatibilita}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.plugins.PluginServizioCompatibilita}
	 * @return Object type {@link org.openspcoop2.core.plugins.PluginServizioCompatibilita}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PluginServizioCompatibilita readPluginServizioCompatibilita(InputStream in) throws DeserializerException {
		return (PluginServizioCompatibilita) this.xmlToObj(in, PluginServizioCompatibilita.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.plugins.PluginServizioCompatibilita}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.plugins.PluginServizioCompatibilita}
	 * @return Object type {@link org.openspcoop2.core.plugins.PluginServizioCompatibilita}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PluginServizioCompatibilita readPluginServizioCompatibilita(byte[] in) throws DeserializerException {
		return (PluginServizioCompatibilita) this.xmlToObj(in, PluginServizioCompatibilita.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.plugins.PluginServizioCompatibilita}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.plugins.PluginServizioCompatibilita}
	 * @return Object type {@link org.openspcoop2.core.plugins.PluginServizioCompatibilita}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PluginServizioCompatibilita readPluginServizioCompatibilitaFromString(String in) throws DeserializerException {
		return (PluginServizioCompatibilita) this.xmlToObj(in.getBytes(), PluginServizioCompatibilita.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: plugin-proprieta-compatibilita
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.plugins.PluginProprietaCompatibilita}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.plugins.PluginProprietaCompatibilita}
	 * @return Object type {@link org.openspcoop2.core.plugins.PluginProprietaCompatibilita}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PluginProprietaCompatibilita readPluginProprietaCompatibilita(String fileName) throws DeserializerException {
		return (PluginProprietaCompatibilita) this.xmlToObj(fileName, PluginProprietaCompatibilita.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.plugins.PluginProprietaCompatibilita}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.plugins.PluginProprietaCompatibilita}
	 * @return Object type {@link org.openspcoop2.core.plugins.PluginProprietaCompatibilita}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PluginProprietaCompatibilita readPluginProprietaCompatibilita(File file) throws DeserializerException {
		return (PluginProprietaCompatibilita) this.xmlToObj(file, PluginProprietaCompatibilita.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.plugins.PluginProprietaCompatibilita}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.plugins.PluginProprietaCompatibilita}
	 * @return Object type {@link org.openspcoop2.core.plugins.PluginProprietaCompatibilita}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PluginProprietaCompatibilita readPluginProprietaCompatibilita(InputStream in) throws DeserializerException {
		return (PluginProprietaCompatibilita) this.xmlToObj(in, PluginProprietaCompatibilita.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.plugins.PluginProprietaCompatibilita}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.plugins.PluginProprietaCompatibilita}
	 * @return Object type {@link org.openspcoop2.core.plugins.PluginProprietaCompatibilita}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PluginProprietaCompatibilita readPluginProprietaCompatibilita(byte[] in) throws DeserializerException {
		return (PluginProprietaCompatibilita) this.xmlToObj(in, PluginProprietaCompatibilita.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.plugins.PluginProprietaCompatibilita}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.plugins.PluginProprietaCompatibilita}
	 * @return Object type {@link org.openspcoop2.core.plugins.PluginProprietaCompatibilita}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PluginProprietaCompatibilita readPluginProprietaCompatibilitaFromString(String in) throws DeserializerException {
		return (PluginProprietaCompatibilita) this.xmlToObj(in.getBytes(), PluginProprietaCompatibilita.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: elenco-id-configurazione-servizio
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.plugins.ElencoIdConfigurazioneServizio}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.plugins.ElencoIdConfigurazioneServizio}
	 * @return Object type {@link org.openspcoop2.core.plugins.ElencoIdConfigurazioneServizio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ElencoIdConfigurazioneServizio readElencoIdConfigurazioneServizio(String fileName) throws DeserializerException {
		return (ElencoIdConfigurazioneServizio) this.xmlToObj(fileName, ElencoIdConfigurazioneServizio.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.plugins.ElencoIdConfigurazioneServizio}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.plugins.ElencoIdConfigurazioneServizio}
	 * @return Object type {@link org.openspcoop2.core.plugins.ElencoIdConfigurazioneServizio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ElencoIdConfigurazioneServizio readElencoIdConfigurazioneServizio(File file) throws DeserializerException {
		return (ElencoIdConfigurazioneServizio) this.xmlToObj(file, ElencoIdConfigurazioneServizio.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.plugins.ElencoIdConfigurazioneServizio}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.plugins.ElencoIdConfigurazioneServizio}
	 * @return Object type {@link org.openspcoop2.core.plugins.ElencoIdConfigurazioneServizio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ElencoIdConfigurazioneServizio readElencoIdConfigurazioneServizio(InputStream in) throws DeserializerException {
		return (ElencoIdConfigurazioneServizio) this.xmlToObj(in, ElencoIdConfigurazioneServizio.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.plugins.ElencoIdConfigurazioneServizio}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.plugins.ElencoIdConfigurazioneServizio}
	 * @return Object type {@link org.openspcoop2.core.plugins.ElencoIdConfigurazioneServizio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ElencoIdConfigurazioneServizio readElencoIdConfigurazioneServizio(byte[] in) throws DeserializerException {
		return (ElencoIdConfigurazioneServizio) this.xmlToObj(in, ElencoIdConfigurazioneServizio.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.plugins.ElencoIdConfigurazioneServizio}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.plugins.ElencoIdConfigurazioneServizio}
	 * @return Object type {@link org.openspcoop2.core.plugins.ElencoIdConfigurazioneServizio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ElencoIdConfigurazioneServizio readElencoIdConfigurazioneServizioFromString(String in) throws DeserializerException {
		return (ElencoIdConfigurazioneServizio) this.xmlToObj(in.getBytes(), ElencoIdConfigurazioneServizio.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: elenco-configurazione-servizio
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.core.plugins.ElencoConfigurazioneServizio}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.plugins.ElencoConfigurazioneServizio}
	 * @return Object type {@link org.openspcoop2.core.plugins.ElencoConfigurazioneServizio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ElencoConfigurazioneServizio readElencoConfigurazioneServizio(String fileName) throws DeserializerException {
		return (ElencoConfigurazioneServizio) this.xmlToObj(fileName, ElencoConfigurazioneServizio.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.core.plugins.ElencoConfigurazioneServizio}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.core.plugins.ElencoConfigurazioneServizio}
	 * @return Object type {@link org.openspcoop2.core.plugins.ElencoConfigurazioneServizio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ElencoConfigurazioneServizio readElencoConfigurazioneServizio(File file) throws DeserializerException {
		return (ElencoConfigurazioneServizio) this.xmlToObj(file, ElencoConfigurazioneServizio.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.core.plugins.ElencoConfigurazioneServizio}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.core.plugins.ElencoConfigurazioneServizio}
	 * @return Object type {@link org.openspcoop2.core.plugins.ElencoConfigurazioneServizio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ElencoConfigurazioneServizio readElencoConfigurazioneServizio(InputStream in) throws DeserializerException {
		return (ElencoConfigurazioneServizio) this.xmlToObj(in, ElencoConfigurazioneServizio.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.core.plugins.ElencoConfigurazioneServizio}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.core.plugins.ElencoConfigurazioneServizio}
	 * @return Object type {@link org.openspcoop2.core.plugins.ElencoConfigurazioneServizio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ElencoConfigurazioneServizio readElencoConfigurazioneServizio(byte[] in) throws DeserializerException {
		return (ElencoConfigurazioneServizio) this.xmlToObj(in, ElencoConfigurazioneServizio.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.core.plugins.ElencoConfigurazioneServizio}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.core.plugins.ElencoConfigurazioneServizio}
	 * @return Object type {@link org.openspcoop2.core.plugins.ElencoConfigurazioneServizio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ElencoConfigurazioneServizio readElencoConfigurazioneServizioFromString(String in) throws DeserializerException {
		return (ElencoConfigurazioneServizio) this.xmlToObj(in.getBytes(), ElencoConfigurazioneServizio.class);
	}	
	
	
	

}
