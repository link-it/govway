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
package org.openspcoop2.monitor.engine.config.transazioni.utils.serializer;

import org.openspcoop2.generic_project.exception.DeserializerException;

import org.openspcoop2.monitor.engine.config.transazioni.IdConfigurazioneServizioAzione;
import org.openspcoop2.monitor.engine.config.transazioni.IdConfigurazioneTransazione;
import org.openspcoop2.monitor.engine.config.transazioni.InfoPlugin;
import org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneServizioAzione;
import org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneServizio;
import org.openspcoop2.monitor.engine.config.transazioni.IdConfigurazioneServizio;
import org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazioneRisorsaContenuto;
import org.openspcoop2.monitor.engine.config.transazioni.IdConfigurazioneTransazioneStato;
import org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazione;
import org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazionePlugin;
import org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazioneStato;
import org.openspcoop2.monitor.engine.config.transazioni.IdPlugin;
import org.openspcoop2.monitor.engine.config.transazioni.Plugin;

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
	 Object: id-configurazione-servizio-azione
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.monitor.engine.config.transazioni.IdConfigurazioneServizioAzione}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.transazioni.IdConfigurazioneServizioAzione}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.transazioni.IdConfigurazioneServizioAzione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdConfigurazioneServizioAzione readIdConfigurazioneServizioAzione(String fileName) throws DeserializerException {
		return (IdConfigurazioneServizioAzione) this.xmlToObj(fileName, IdConfigurazioneServizioAzione.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.monitor.engine.config.transazioni.IdConfigurazioneServizioAzione}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.transazioni.IdConfigurazioneServizioAzione}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.transazioni.IdConfigurazioneServizioAzione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdConfigurazioneServizioAzione readIdConfigurazioneServizioAzione(File file) throws DeserializerException {
		return (IdConfigurazioneServizioAzione) this.xmlToObj(file, IdConfigurazioneServizioAzione.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.monitor.engine.config.transazioni.IdConfigurazioneServizioAzione}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.transazioni.IdConfigurazioneServizioAzione}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.transazioni.IdConfigurazioneServizioAzione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdConfigurazioneServizioAzione readIdConfigurazioneServizioAzione(InputStream in) throws DeserializerException {
		return (IdConfigurazioneServizioAzione) this.xmlToObj(in, IdConfigurazioneServizioAzione.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.monitor.engine.config.transazioni.IdConfigurazioneServizioAzione}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.transazioni.IdConfigurazioneServizioAzione}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.transazioni.IdConfigurazioneServizioAzione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdConfigurazioneServizioAzione readIdConfigurazioneServizioAzione(byte[] in) throws DeserializerException {
		return (IdConfigurazioneServizioAzione) this.xmlToObj(in, IdConfigurazioneServizioAzione.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.monitor.engine.config.transazioni.IdConfigurazioneServizioAzione}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.transazioni.IdConfigurazioneServizioAzione}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.transazioni.IdConfigurazioneServizioAzione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdConfigurazioneServizioAzione readIdConfigurazioneServizioAzioneFromString(String in) throws DeserializerException {
		return (IdConfigurazioneServizioAzione) this.xmlToObj(in.getBytes(), IdConfigurazioneServizioAzione.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: id-configurazione-transazione
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.monitor.engine.config.transazioni.IdConfigurazioneTransazione}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.transazioni.IdConfigurazioneTransazione}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.transazioni.IdConfigurazioneTransazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdConfigurazioneTransazione readIdConfigurazioneTransazione(String fileName) throws DeserializerException {
		return (IdConfigurazioneTransazione) this.xmlToObj(fileName, IdConfigurazioneTransazione.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.monitor.engine.config.transazioni.IdConfigurazioneTransazione}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.transazioni.IdConfigurazioneTransazione}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.transazioni.IdConfigurazioneTransazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdConfigurazioneTransazione readIdConfigurazioneTransazione(File file) throws DeserializerException {
		return (IdConfigurazioneTransazione) this.xmlToObj(file, IdConfigurazioneTransazione.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.monitor.engine.config.transazioni.IdConfigurazioneTransazione}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.transazioni.IdConfigurazioneTransazione}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.transazioni.IdConfigurazioneTransazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdConfigurazioneTransazione readIdConfigurazioneTransazione(InputStream in) throws DeserializerException {
		return (IdConfigurazioneTransazione) this.xmlToObj(in, IdConfigurazioneTransazione.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.monitor.engine.config.transazioni.IdConfigurazioneTransazione}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.transazioni.IdConfigurazioneTransazione}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.transazioni.IdConfigurazioneTransazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdConfigurazioneTransazione readIdConfigurazioneTransazione(byte[] in) throws DeserializerException {
		return (IdConfigurazioneTransazione) this.xmlToObj(in, IdConfigurazioneTransazione.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.monitor.engine.config.transazioni.IdConfigurazioneTransazione}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.transazioni.IdConfigurazioneTransazione}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.transazioni.IdConfigurazioneTransazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdConfigurazioneTransazione readIdConfigurazioneTransazioneFromString(String in) throws DeserializerException {
		return (IdConfigurazioneTransazione) this.xmlToObj(in.getBytes(), IdConfigurazioneTransazione.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: info-plugin
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.monitor.engine.config.transazioni.InfoPlugin}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.transazioni.InfoPlugin}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.transazioni.InfoPlugin}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public InfoPlugin readInfoPlugin(String fileName) throws DeserializerException {
		return (InfoPlugin) this.xmlToObj(fileName, InfoPlugin.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.monitor.engine.config.transazioni.InfoPlugin}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.transazioni.InfoPlugin}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.transazioni.InfoPlugin}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public InfoPlugin readInfoPlugin(File file) throws DeserializerException {
		return (InfoPlugin) this.xmlToObj(file, InfoPlugin.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.monitor.engine.config.transazioni.InfoPlugin}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.transazioni.InfoPlugin}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.transazioni.InfoPlugin}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public InfoPlugin readInfoPlugin(InputStream in) throws DeserializerException {
		return (InfoPlugin) this.xmlToObj(in, InfoPlugin.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.monitor.engine.config.transazioni.InfoPlugin}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.transazioni.InfoPlugin}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.transazioni.InfoPlugin}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public InfoPlugin readInfoPlugin(byte[] in) throws DeserializerException {
		return (InfoPlugin) this.xmlToObj(in, InfoPlugin.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.monitor.engine.config.transazioni.InfoPlugin}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.transazioni.InfoPlugin}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.transazioni.InfoPlugin}
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
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneServizioAzione}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneServizioAzione}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneServizioAzione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConfigurazioneServizioAzione readConfigurazioneServizioAzione(String fileName) throws DeserializerException {
		return (ConfigurazioneServizioAzione) this.xmlToObj(fileName, ConfigurazioneServizioAzione.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneServizioAzione}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneServizioAzione}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneServizioAzione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConfigurazioneServizioAzione readConfigurazioneServizioAzione(File file) throws DeserializerException {
		return (ConfigurazioneServizioAzione) this.xmlToObj(file, ConfigurazioneServizioAzione.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneServizioAzione}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneServizioAzione}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneServizioAzione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConfigurazioneServizioAzione readConfigurazioneServizioAzione(InputStream in) throws DeserializerException {
		return (ConfigurazioneServizioAzione) this.xmlToObj(in, ConfigurazioneServizioAzione.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneServizioAzione}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneServizioAzione}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneServizioAzione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConfigurazioneServizioAzione readConfigurazioneServizioAzione(byte[] in) throws DeserializerException {
		return (ConfigurazioneServizioAzione) this.xmlToObj(in, ConfigurazioneServizioAzione.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneServizioAzione}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneServizioAzione}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneServizioAzione}
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
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneServizio}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneServizio}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneServizio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConfigurazioneServizio readConfigurazioneServizio(String fileName) throws DeserializerException {
		return (ConfigurazioneServizio) this.xmlToObj(fileName, ConfigurazioneServizio.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneServizio}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneServizio}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneServizio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConfigurazioneServizio readConfigurazioneServizio(File file) throws DeserializerException {
		return (ConfigurazioneServizio) this.xmlToObj(file, ConfigurazioneServizio.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneServizio}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneServizio}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneServizio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConfigurazioneServizio readConfigurazioneServizio(InputStream in) throws DeserializerException {
		return (ConfigurazioneServizio) this.xmlToObj(in, ConfigurazioneServizio.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneServizio}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneServizio}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneServizio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConfigurazioneServizio readConfigurazioneServizio(byte[] in) throws DeserializerException {
		return (ConfigurazioneServizio) this.xmlToObj(in, ConfigurazioneServizio.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneServizio}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneServizio}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneServizio}
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
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.monitor.engine.config.transazioni.IdConfigurazioneServizio}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.transazioni.IdConfigurazioneServizio}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.transazioni.IdConfigurazioneServizio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdConfigurazioneServizio readIdConfigurazioneServizio(String fileName) throws DeserializerException {
		return (IdConfigurazioneServizio) this.xmlToObj(fileName, IdConfigurazioneServizio.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.monitor.engine.config.transazioni.IdConfigurazioneServizio}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.transazioni.IdConfigurazioneServizio}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.transazioni.IdConfigurazioneServizio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdConfigurazioneServizio readIdConfigurazioneServizio(File file) throws DeserializerException {
		return (IdConfigurazioneServizio) this.xmlToObj(file, IdConfigurazioneServizio.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.monitor.engine.config.transazioni.IdConfigurazioneServizio}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.transazioni.IdConfigurazioneServizio}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.transazioni.IdConfigurazioneServizio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdConfigurazioneServizio readIdConfigurazioneServizio(InputStream in) throws DeserializerException {
		return (IdConfigurazioneServizio) this.xmlToObj(in, IdConfigurazioneServizio.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.monitor.engine.config.transazioni.IdConfigurazioneServizio}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.transazioni.IdConfigurazioneServizio}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.transazioni.IdConfigurazioneServizio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdConfigurazioneServizio readIdConfigurazioneServizio(byte[] in) throws DeserializerException {
		return (IdConfigurazioneServizio) this.xmlToObj(in, IdConfigurazioneServizio.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.monitor.engine.config.transazioni.IdConfigurazioneServizio}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.transazioni.IdConfigurazioneServizio}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.transazioni.IdConfigurazioneServizio}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdConfigurazioneServizio readIdConfigurazioneServizioFromString(String in) throws DeserializerException {
		return (IdConfigurazioneServizio) this.xmlToObj(in.getBytes(), IdConfigurazioneServizio.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: configurazione-transazione-risorsa-contenuto
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazioneRisorsaContenuto}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazioneRisorsaContenuto}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazioneRisorsaContenuto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConfigurazioneTransazioneRisorsaContenuto readConfigurazioneTransazioneRisorsaContenuto(String fileName) throws DeserializerException {
		return (ConfigurazioneTransazioneRisorsaContenuto) this.xmlToObj(fileName, ConfigurazioneTransazioneRisorsaContenuto.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazioneRisorsaContenuto}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazioneRisorsaContenuto}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazioneRisorsaContenuto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConfigurazioneTransazioneRisorsaContenuto readConfigurazioneTransazioneRisorsaContenuto(File file) throws DeserializerException {
		return (ConfigurazioneTransazioneRisorsaContenuto) this.xmlToObj(file, ConfigurazioneTransazioneRisorsaContenuto.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazioneRisorsaContenuto}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazioneRisorsaContenuto}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazioneRisorsaContenuto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConfigurazioneTransazioneRisorsaContenuto readConfigurazioneTransazioneRisorsaContenuto(InputStream in) throws DeserializerException {
		return (ConfigurazioneTransazioneRisorsaContenuto) this.xmlToObj(in, ConfigurazioneTransazioneRisorsaContenuto.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazioneRisorsaContenuto}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazioneRisorsaContenuto}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazioneRisorsaContenuto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConfigurazioneTransazioneRisorsaContenuto readConfigurazioneTransazioneRisorsaContenuto(byte[] in) throws DeserializerException {
		return (ConfigurazioneTransazioneRisorsaContenuto) this.xmlToObj(in, ConfigurazioneTransazioneRisorsaContenuto.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazioneRisorsaContenuto}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazioneRisorsaContenuto}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazioneRisorsaContenuto}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConfigurazioneTransazioneRisorsaContenuto readConfigurazioneTransazioneRisorsaContenutoFromString(String in) throws DeserializerException {
		return (ConfigurazioneTransazioneRisorsaContenuto) this.xmlToObj(in.getBytes(), ConfigurazioneTransazioneRisorsaContenuto.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: id-configurazione-transazione-stato
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.monitor.engine.config.transazioni.IdConfigurazioneTransazioneStato}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.transazioni.IdConfigurazioneTransazioneStato}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.transazioni.IdConfigurazioneTransazioneStato}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdConfigurazioneTransazioneStato readIdConfigurazioneTransazioneStato(String fileName) throws DeserializerException {
		return (IdConfigurazioneTransazioneStato) this.xmlToObj(fileName, IdConfigurazioneTransazioneStato.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.monitor.engine.config.transazioni.IdConfigurazioneTransazioneStato}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.transazioni.IdConfigurazioneTransazioneStato}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.transazioni.IdConfigurazioneTransazioneStato}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdConfigurazioneTransazioneStato readIdConfigurazioneTransazioneStato(File file) throws DeserializerException {
		return (IdConfigurazioneTransazioneStato) this.xmlToObj(file, IdConfigurazioneTransazioneStato.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.monitor.engine.config.transazioni.IdConfigurazioneTransazioneStato}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.transazioni.IdConfigurazioneTransazioneStato}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.transazioni.IdConfigurazioneTransazioneStato}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdConfigurazioneTransazioneStato readIdConfigurazioneTransazioneStato(InputStream in) throws DeserializerException {
		return (IdConfigurazioneTransazioneStato) this.xmlToObj(in, IdConfigurazioneTransazioneStato.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.monitor.engine.config.transazioni.IdConfigurazioneTransazioneStato}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.transazioni.IdConfigurazioneTransazioneStato}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.transazioni.IdConfigurazioneTransazioneStato}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdConfigurazioneTransazioneStato readIdConfigurazioneTransazioneStato(byte[] in) throws DeserializerException {
		return (IdConfigurazioneTransazioneStato) this.xmlToObj(in, IdConfigurazioneTransazioneStato.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.monitor.engine.config.transazioni.IdConfigurazioneTransazioneStato}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.transazioni.IdConfigurazioneTransazioneStato}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.transazioni.IdConfigurazioneTransazioneStato}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdConfigurazioneTransazioneStato readIdConfigurazioneTransazioneStatoFromString(String in) throws DeserializerException {
		return (IdConfigurazioneTransazioneStato) this.xmlToObj(in.getBytes(), IdConfigurazioneTransazioneStato.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: configurazione-transazione
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazione}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazione}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConfigurazioneTransazione readConfigurazioneTransazione(String fileName) throws DeserializerException {
		return (ConfigurazioneTransazione) this.xmlToObj(fileName, ConfigurazioneTransazione.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazione}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazione}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConfigurazioneTransazione readConfigurazioneTransazione(File file) throws DeserializerException {
		return (ConfigurazioneTransazione) this.xmlToObj(file, ConfigurazioneTransazione.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazione}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazione}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConfigurazioneTransazione readConfigurazioneTransazione(InputStream in) throws DeserializerException {
		return (ConfigurazioneTransazione) this.xmlToObj(in, ConfigurazioneTransazione.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazione}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazione}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConfigurazioneTransazione readConfigurazioneTransazione(byte[] in) throws DeserializerException {
		return (ConfigurazioneTransazione) this.xmlToObj(in, ConfigurazioneTransazione.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazione}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazione}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazione}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConfigurazioneTransazione readConfigurazioneTransazioneFromString(String in) throws DeserializerException {
		return (ConfigurazioneTransazione) this.xmlToObj(in.getBytes(), ConfigurazioneTransazione.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: configurazione-transazione-plugin
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazionePlugin}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazionePlugin}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazionePlugin}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConfigurazioneTransazionePlugin readConfigurazioneTransazionePlugin(String fileName) throws DeserializerException {
		return (ConfigurazioneTransazionePlugin) this.xmlToObj(fileName, ConfigurazioneTransazionePlugin.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazionePlugin}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazionePlugin}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazionePlugin}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConfigurazioneTransazionePlugin readConfigurazioneTransazionePlugin(File file) throws DeserializerException {
		return (ConfigurazioneTransazionePlugin) this.xmlToObj(file, ConfigurazioneTransazionePlugin.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazionePlugin}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazionePlugin}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazionePlugin}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConfigurazioneTransazionePlugin readConfigurazioneTransazionePlugin(InputStream in) throws DeserializerException {
		return (ConfigurazioneTransazionePlugin) this.xmlToObj(in, ConfigurazioneTransazionePlugin.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazionePlugin}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazionePlugin}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazionePlugin}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConfigurazioneTransazionePlugin readConfigurazioneTransazionePlugin(byte[] in) throws DeserializerException {
		return (ConfigurazioneTransazionePlugin) this.xmlToObj(in, ConfigurazioneTransazionePlugin.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazionePlugin}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazionePlugin}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazionePlugin}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConfigurazioneTransazionePlugin readConfigurazioneTransazionePluginFromString(String in) throws DeserializerException {
		return (ConfigurazioneTransazionePlugin) this.xmlToObj(in.getBytes(), ConfigurazioneTransazionePlugin.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: configurazione-transazione-stato
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazioneStato}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazioneStato}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazioneStato}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConfigurazioneTransazioneStato readConfigurazioneTransazioneStato(String fileName) throws DeserializerException {
		return (ConfigurazioneTransazioneStato) this.xmlToObj(fileName, ConfigurazioneTransazioneStato.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazioneStato}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazioneStato}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazioneStato}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConfigurazioneTransazioneStato readConfigurazioneTransazioneStato(File file) throws DeserializerException {
		return (ConfigurazioneTransazioneStato) this.xmlToObj(file, ConfigurazioneTransazioneStato.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazioneStato}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazioneStato}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazioneStato}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConfigurazioneTransazioneStato readConfigurazioneTransazioneStato(InputStream in) throws DeserializerException {
		return (ConfigurazioneTransazioneStato) this.xmlToObj(in, ConfigurazioneTransazioneStato.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazioneStato}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazioneStato}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazioneStato}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConfigurazioneTransazioneStato readConfigurazioneTransazioneStato(byte[] in) throws DeserializerException {
		return (ConfigurazioneTransazioneStato) this.xmlToObj(in, ConfigurazioneTransazioneStato.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazioneStato}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazioneStato}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazioneStato}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ConfigurazioneTransazioneStato readConfigurazioneTransazioneStatoFromString(String in) throws DeserializerException {
		return (ConfigurazioneTransazioneStato) this.xmlToObj(in.getBytes(), ConfigurazioneTransazioneStato.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: id-plugin
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.monitor.engine.config.transazioni.IdPlugin}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.transazioni.IdPlugin}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.transazioni.IdPlugin}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdPlugin readIdPlugin(String fileName) throws DeserializerException {
		return (IdPlugin) this.xmlToObj(fileName, IdPlugin.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.monitor.engine.config.transazioni.IdPlugin}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.transazioni.IdPlugin}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.transazioni.IdPlugin}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdPlugin readIdPlugin(File file) throws DeserializerException {
		return (IdPlugin) this.xmlToObj(file, IdPlugin.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.monitor.engine.config.transazioni.IdPlugin}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.transazioni.IdPlugin}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.transazioni.IdPlugin}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdPlugin readIdPlugin(InputStream in) throws DeserializerException {
		return (IdPlugin) this.xmlToObj(in, IdPlugin.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.monitor.engine.config.transazioni.IdPlugin}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.transazioni.IdPlugin}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.transazioni.IdPlugin}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public IdPlugin readIdPlugin(byte[] in) throws DeserializerException {
		return (IdPlugin) this.xmlToObj(in, IdPlugin.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.monitor.engine.config.transazioni.IdPlugin}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.transazioni.IdPlugin}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.transazioni.IdPlugin}
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
	 * Transform the xml in <var>fileName</var> in the object type {@link org.openspcoop2.monitor.engine.config.transazioni.Plugin}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.transazioni.Plugin}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.transazioni.Plugin}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Plugin readPlugin(String fileName) throws DeserializerException {
		return (Plugin) this.xmlToObj(fileName, Plugin.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link org.openspcoop2.monitor.engine.config.transazioni.Plugin}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.transazioni.Plugin}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.transazioni.Plugin}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Plugin readPlugin(File file) throws DeserializerException {
		return (Plugin) this.xmlToObj(file, Plugin.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link org.openspcoop2.monitor.engine.config.transazioni.Plugin}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.transazioni.Plugin}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.transazioni.Plugin}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Plugin readPlugin(InputStream in) throws DeserializerException {
		return (Plugin) this.xmlToObj(in, Plugin.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link org.openspcoop2.monitor.engine.config.transazioni.Plugin}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.transazioni.Plugin}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.transazioni.Plugin}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Plugin readPlugin(byte[] in) throws DeserializerException {
		return (Plugin) this.xmlToObj(in, Plugin.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link org.openspcoop2.monitor.engine.config.transazioni.Plugin}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link org.openspcoop2.monitor.engine.config.transazioni.Plugin}
	 * @return Object type {@link org.openspcoop2.monitor.engine.config.transazioni.Plugin}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Plugin readPluginFromString(String in) throws DeserializerException {
		return (Plugin) this.xmlToObj(in.getBytes(), Plugin.class);
	}	
	
	
	

}
